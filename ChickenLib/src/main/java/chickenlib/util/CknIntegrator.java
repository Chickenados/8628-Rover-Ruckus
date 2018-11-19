package chickenlib.util;

import chickenlib.CknTaskManager;
import chickenlib.inputstreams.CknInputStream;
import chickenlib.logging.CknDbgLog;
import chickenlib.sensor.CknSensor;
import chickenlib.util.CknData;

public class CknIntegrator<D> implements CknTaskManager.Task {

    public static class Parameters{
        public boolean doDoubleIntegration = false;
    }

    private CknSensor<D> sensor;
    private D dataType;
    private Parameters params;

    private CknData<Double>[] integratedData;
    private CknData<Double>[] doubleIntegratedData;
    private CknData<Double>[] inputData;
    private double[] prevTime;


    public CknIntegrator(int numAxes, CknSensor sensor, D dataType, Parameters params){
        this.params = params;
        integratedData = new CknData[numAxes];
        if(params.doDoubleIntegration) doubleIntegratedData = new CknData[numAxes];
        prevTime = new double[numAxes];
        this.sensor = sensor;
        this.dataType = dataType;

        for(int i = 0; i < numAxes; i++){
            integratedData[i] = new CknData<>(0.0, 0.0);
            prevTime[i] = 0.0;
            if(params.doDoubleIntegration){
                doubleIntegratedData[i] = new CknData<>(0.0, 0.0);
            }
        }
    }

    public CknIntegrator(int numAxes, CknSensor sensor, D dataType){
        this(numAxes, sensor, dataType, new Parameters());
    }

    public void integrateData(CknData<Double> data, int axis){

        double deltaTime = prevTime[axis] - data.timestamp;
        integratedData[axis].timestamp = data.timestamp;
        integratedData[axis].value = integratedData[axis].value + (data.value*deltaTime);

        if(params.doDoubleIntegration){
            doubleIntegratedData[axis].timestamp = data.timestamp;
            doubleIntegratedData[axis].value = doubleIntegratedData[axis].value
                    + (integratedData[axis].value*deltaTime);
        }

        // Record this time as the previous time for next iteration.
        prevTime[axis] = data.timestamp;
    }

    public CknData getIntegratedData(int axis){
        if(integratedData[axis] != null && axis < integratedData.length){
            return integratedData[axis];
        }
        CknDbgLog.msg(CknDbgLog.Priority.WARN, "Attempted to retrieve invalid integration data.");
        return new CknData<>(0.0, CknUtil.getCurrentTime());
    }

    public CknData getDoubleIntegratedData(int axis){
        if(doubleIntegratedData[axis] != null && axis < doubleIntegratedData.length){
            return doubleIntegratedData[axis];
        }
        CknDbgLog.msg(CknDbgLog.Priority.WARN, "Attempted to retrieve invalid integration data.");
        return new CknData<>(0.0, CknUtil.getCurrentTime());
    }

    public void setTaskEnabled(boolean enabled){
        if(enabled) {
            CknTaskManager.getInstance().registerTask(this, CknTaskManager.TaskType.PRECONTINUOUS);
        } else {
            CknTaskManager.getInstance().unregisterTask(this, CknTaskManager.TaskType.PRECONTINUOUS);
        }
    }

    @Override
    public void preContinuous(){
        // Do the integration
        for(int i = 0; i < sensor.getNumAxes(); i++){
            integrateData(sensor.getData(i, dataType), i);
        }
    }

    @Override
    public void postContinuous(){

    }

}
