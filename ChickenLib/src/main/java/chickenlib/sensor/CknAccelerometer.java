package chickenlib.sensor;

import android.provider.ContactsContract;

import chickenlib.logging.CknDbgLog;
import chickenlib.util.CknData;
import chickenlib.util.CknIntegrator;
import chickenlib.util.CknUtil;

public abstract class CknAccelerometer extends CknSensor<CknAccelerometer.DataType>{

    public static class Parameters {
        public boolean doIntegration = false;
    }

    public enum DataType {
        ACCELERATION,
        VELOCITY,
        POSITION;
    }

    private Parameters params;
    CknIntegrator<DataType> integrator;

    public CknAccelerometer(Parameters params){
        super(3);
        this.params = params;

        if(params.doIntegration){
            CknIntegrator.Parameters iParams = new CknIntegrator.Parameters();
            iParams.doDoubleIntegration = true;
            integrator = new CknIntegrator<>(3, this, DataType.ACCELERATION, iParams);
        }
    }

    public abstract CknData<Double> getRawXAccel();

    public abstract CknData<Double> getRawYAccel();

    public abstract CknData<Double> getRawZAccel();

    public void startIntegration(){
        if(params.doIntegration) {
            integrator.setTaskEnabled(true);
        }
    }

    public void stopIntegration(){
        if(params.doIntegration) {
            integrator.setTaskEnabled(false);
        }
    }

    public CknData<Double> getIntegratedData(int axis){
        if(params.doIntegration){
            return integrator.getIntegratedData(axis);
        } else {
            CknDbgLog.msg(CknDbgLog.Priority.WARN,
                    "Attempted to retreive integrated data when doIntegration is false!");
            return new CknData<>(0.0, CknUtil.getCurrentTime());
        }
    }

    public CknData<Double> getDoubleIntegratedData(int axis) {
        if(params.doIntegration){
            return integrator.getDoubleIntegratedData(axis);
        } else {
            CknDbgLog.msg(CknDbgLog.Priority.WARN,
                    "Attempted to retreive integrated data when doIntegration is false!");
            return new CknData<>(0.0, CknUtil.getCurrentTime());
        }
    }
}
