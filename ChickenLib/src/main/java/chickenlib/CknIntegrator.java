package chickenlib;

import java.util.ArrayList;

public class CknIntegrator implements CknTaskManager.Task{

    private CknData<Double>[] dataList;

    public CknIntegrator(int numAxes){
        dataList = new CknData[numAxes];
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

    }

    @Override
    public void postContinuous(){

    }

}
