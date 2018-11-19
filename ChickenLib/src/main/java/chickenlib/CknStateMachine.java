package chickenlib;

import java.util.ArrayList;

import chickenlib.util.CknEvent;

public class CknStateMachine<T> {

    private ArrayList<T> stateList;
    private T currentState = null;
    public T nextState = null;
    private CknEvent event;

    private boolean active;

    public CknStateMachine(){

    }

    public void start(T state){
        currentState = state;
        nextState = state;
        active = true;
    }

    public void stop(){
        currentState = null;
        nextState = null;
        active = false;
    }

    public void setState(T state){
        this.currentState = state;
    }

    public T getState(){
        return currentState;
    }

    public void reset(){
        currentState = null;
        nextState = null;
    }

    public void waitForEvent(CknEvent event, T nextState){
        this.event = event;
        this.nextState = nextState;
    }

    public boolean isReady(){
        if(active) {
            if (event == null) {
                return true;
            }
            if (event.isTriggered()) {
                currentState = nextState;
                return true;
            }
        }

        return false;
    }

}
