package chickenlib;

public class ExampleTask implements CknTaskManager.Task {

    CknSmartDashboard dashboard;

    public ExampleTask(CknSmartDashboard dash){
        this.dashboard = dash;
    }

    public void preContinuous(){
        dashboard.setLine(0, "Current Time: " + CknUtil.getCurrentTime());
    }


    public void postContinuous(){
        CknSmartDashboard.getInstance().setLine(1, "Time*10: " + (CknUtil.getCurrentTime() * 10));
    }

    public void enableTask(){
        CknTaskManager.getInstance().registerTask(this, CknTaskManager.TaskType.PRECONTINUOUS);
        CknTaskManager.getInstance().registerTask(this, CknTaskManager.TaskType.POSTCONTINUOUS);
    }

    public void disableTask(){
        CknTaskManager.getInstance().unregisterTask(this, CknTaskManager.TaskType.PRECONTINUOUS);
        CknTaskManager.getInstance().unregisterTask(this, CknTaskManager.TaskType.POSTCONTINUOUS);
    }

}
