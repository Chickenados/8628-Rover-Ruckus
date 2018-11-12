package chickenlib.util;

public class CknEvent {

    private boolean isTriggered;

    public CknEvent(){
        this.isTriggered = false;
    }

    public void set(boolean trigger){
        this.isTriggered = trigger;
    }

    public boolean isTriggered(){
        return this.isTriggered;
    }

    public void reset(){
        this.isTriggered = false;
    }
}
