package chickenlib.sensor;

import android.provider.ContactsContract;

import chickenlib.util.CknData;
import chickenlib.util.CknIntegrator;

public abstract class CknAccelerometer extends CknSensor<CknAccelerometer.DataType>{

    public static class Parameters {
        public DataType dataType = DataType.ACCELERATION;
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

        if(params.doIntegration && params.dataType == DataType.ACCELERATION){
            integrator = new CknIntegrator<>(3, this, DataType.ACCELERATION);
        }
    }

    public abstract CknData<Double> getRawXAccel();

    public abstract CknData<Double> getRawYAccel();

    public abstract CknData<Double> getRawZAccel();

    public void setEnabled(boolean enabled){
        if(integrator != null) integrator.setTaskEnabled(enabled);
    }



}
