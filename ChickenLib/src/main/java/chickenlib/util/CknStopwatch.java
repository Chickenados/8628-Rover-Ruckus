package chickenlib.util;

import chickenlib.CknTaskManager;

public class CknStopwatch implements CknTaskManager.Task {

    private CknEvent event;
    private boolean active;
    private double startTime;
    private double targetTime;

    public CknStopwatch(CknEvent event){
        this.event = event;
    }

    public void setTimer(double time){
        startTime = CknUtil.getCurrentTime();
        targetTime = time;
        setTaskEnabled(true);
    }

    public void setTaskEnabled(boolean enabled){
        if(enabled){
            CknTaskManager.getInstance().registerTask(this, CknTaskManager.TaskType.PRECONTINUOUS);
        } else {
            CknTaskManager.getInstance().unregisterTask(this, CknTaskManager.TaskType.PRECONTINUOUS);
        }
    }

    @Override
    public void preContinuous(){
        if(CknUtil.getCurrentTime() > startTime + targetTime){
            event.set(true);
            setTaskEnabled(false);
        }
    }

    @Override
    public void postContinuous(){

    }
}
