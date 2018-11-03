package chickenlib;

public class CknStopwatch {

    private CknEvent event;
    private boolean active;
    private double startTime;
    private double targetTime;

    public CknStopwatch(CknEvent event){
        this.event = event;
    }

    public void setTimer(double time){
        active = true;
        startTime = CknUtil.getCurrentTime();
        targetTime = time;
    }

    public void handleStopwatch(){
        if(active){
            if(CknUtil.getCurrentTime() > startTime + targetTime){
                event.set(true);
            }
        }
    }
}
