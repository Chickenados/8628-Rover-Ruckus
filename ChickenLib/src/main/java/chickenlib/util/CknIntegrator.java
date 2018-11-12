package chickenlib.util;

import chickenlib.CknTaskManager;
import chickenlib.sensor.CknSensor;
import chickenlib.util.CknData;

public class CknIntegrator<D> implements CknTaskManager.Task {

    private CknSensor sensor;
    private D dataType;

    private CknData<Double>[] integratedData;
    private CknData<Double>[] inputData;
    private double[] prevTime;


    public CknIntegrator(int numAxes, CknSensor sensor, D dataType){
        integratedData = new CknData[numAxes];
        prevTime = new double[numAxes];
        this.sensor = sensor;
        this.dataType = dataType;
    }

    public void integrateData(CknData<Double> data, int axis){

        double deltaTime = prevTime[axis] - data.timestamp;
        integratedData[axis].timestamp = data.timestamp;
        integratedData[axis].value = integratedData[axis].value + (data.value*deltaTime);

        // Record this time as the previous time for next iteration.
        prevTime[axis] = data.timestamp;
    }

    public CknData getIntegratedData(int axis){
        return integratedData[axis];
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
        for(int i = 1; i <= sensor.getNumAxes(); i++){
            integrateData(sensor.getData(i, dataType), i);
        }
    }

    @Override
    public void postContinuous(){

    }

}
