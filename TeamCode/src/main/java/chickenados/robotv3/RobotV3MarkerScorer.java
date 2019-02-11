package chickenados.robotv3;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

import chickenlib.CknTaskManager;
import chickenlib.util.CknEvent;
import chickenlib.util.CknStopwatch;
import chickenlib.util.CknUtil;

public class RobotV3MarkerScorer implements CknTaskManager.Task{

    private enum MarkerState{
        EXTENDING,
        RETRACTING,
        NONE
    }

    public static class Parameters {
        // The time to extend the collector box, in seconds
        public double extendTime = 1.0;

        //The time to retract the collector box, in seconds
        public double retractTime = 1.0;

    }

    MarkerState state = MarkerState.NONE;

    Parameters params;

    CRServo leftServo;
    CRServo rightServo;

    private CknStopwatch stopwatch;

    public double startTime = 0;

    private CknEvent event;

    public RobotV3MarkerScorer(Parameters params, CRServo leftServo, CRServo rightServo){
        this.leftServo = leftServo;
        this.rightServo = rightServo;
        this.params = params;
    }

    public void extend(CknEvent event) {
        state = MarkerState.EXTENDING;
        startTime = CknUtil.getCurrentTime();
        CknTaskManager.getInstance().registerTask(this, CknTaskManager.TaskType.PRECONTINUOUS);
        this.rightServo.setPower(1);
        this.leftServo.setPower(1);
        if(event != null) {
            this.event = event;
        } else {
            this.event = null;
        }
    }

    public void extend() {
        extend(null);
    }

    public void reset(CknEvent event){
        state = MarkerState.RETRACTING;
        startTime = CknUtil.getCurrentTime();
        CknTaskManager.getInstance().registerTask(this, CknTaskManager.TaskType.PRECONTINUOUS);
        this.rightServo.setPower(-1);
        this.leftServo.setPower(-1);
        if(event != null) {
            this.event = event;
        } else {
            this.event = null;
        }
    }

    public void reset(){
        reset(null);
    }

    private void stopServos(){
        this.rightServo.setPower(0);
        this.leftServo.setPower(0);
        CknTaskManager.getInstance().unregisterTask(this, CknTaskManager.TaskType.PRECONTINUOUS);
    }

    @Override
    public void preContinuous(){
        if(state == MarkerState.EXTENDING) {
            if (CknUtil.getCurrentTime() - startTime > params.extendTime) {
                stopServos();
                if(event != null){
                    event.set(true);
                }
            }
        } else if (state == MarkerState.RETRACTING){
            if (CknUtil.getCurrentTime() - startTime > params.retractTime) {
                stopServos();
                if(event != null){
                    event.set(true);
                }
            }
        }
    }

    @Override
    public void postContinuous(){

    }


}
