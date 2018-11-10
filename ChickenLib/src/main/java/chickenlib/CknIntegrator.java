package chickenlib;

import java.util.ArrayList;

public class CknIntegrator implements CknTaskManager.Task{

    private CknData<Double>[] integratedData;
    private CknData<Double>[] inputData;
    private double[] prevTime;

    public CknIntegrator(int numAxes){
        integratedData = new CknData[numAxes];
        prevTime = new double[numAxes];
    }

    public void integrateData(CknData<Double> data, int axis){

        double deltaTime = prevTime[axis] - data.timestamp;
        integratedData[axis].value = integratedData[axis].value + (data.value*deltaTime);

        // Record this time as the previous time for next iteration.
        prevTime[axis] = data.timestamp;
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
        // TODO: Find a way to retreive information for this loop.
        CknData<Double> inputData = new CknData<>(0.0, 0.0);
        integrateData(inputData, 1);
    }

    @Override
    public void postContinuous(){

    }

}
