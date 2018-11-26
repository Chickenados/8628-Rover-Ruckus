package chickenados.robotv1;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import chickenlib.util.CknEvent;
import chickenlib.CknPIDController;
import chickenlib.CknTaskManager;
import chickenlib.util.CknUtil;

public class RobotV1Lift implements CknTaskManager.Task{

    //TODO: Incorporate Timeouts.

    enum LiftState {
        RAISED,
        LOWERED,
        UNKNOWN;
    }

    double liftPosition;

    LiftState currentState = LiftState.UNKNOWN;
    LiftState targetState = LiftState.UNKNOWN;
    public DcMotor liftMotor;
    CknPIDController liftPid;
    double startTime;
    double timeout;

    CknEvent event;

    public RobotV1Lift(DcMotor liftMotor, CknPIDController liftPid){
        this.liftMotor = liftMotor;
        this.liftPid = liftPid;
        this.liftPosition = liftMotor.getCurrentPosition();
    }

    /**
     * Sets the Lift PID to move it upwards.
     * @param event
     */
    public void raiseLift(CknEvent event, double timeout){
        this.timeout = timeout;
        startTime = CknUtil.getCurrentTime();
        liftPid.setSetPoint(RobotV1Info.RAISED_ENCODER_COUNT, false);
        currentState = LiftState.UNKNOWN;
        targetState = LiftState.RAISED;
        this.event = event;
        setTaskEnabled(true);
    }

    public void raiseLift(double timeout){
        raiseLift(null, timeout);
    }

    /**
     * Sets the Lift PID to move it downwards.
     * @param event
     */
    public void lowerLift(CknEvent event, double timeout){
        startTime = CknUtil.getCurrentTime();
        this.timeout = timeout;
        liftPid.setSetPoint(RobotV1Info.LOWERED_ENCODER_COUNT, false);
        currentState = LiftState.UNKNOWN;
        targetState = LiftState.LOWERED;
        this.event = event;
        setTaskEnabled(true);
    }

    public void lowerLift(double timeout){
        lowerLift(null, timeout);
    }

    /**
     * Returns the current state that the lift is in
     * @return
     */
    public LiftState getLiftState(){
        return currentState;
    }

    public double getLiftProgress(){
        if(targetState == LiftState.LOWERED){
            return 1-(liftPosition/RobotV1Info.RAISED_ENCODER_COUNT);
        } else if (targetState == LiftState.RAISED){
            return liftPosition/RobotV1Info.RAISED_ENCODER_COUNT;
        } else {
            return 0.0;
        }
    }

    public void stop(){
        setTaskEnabled(false);
        liftMotor.setPower(0);
        liftPid.reset();
        currentState = targetState;

        if(event != null){
            event.set(true);
        }
    }

    public void setTaskEnabled(boolean enabled){
        if(enabled){
            CknTaskManager.getInstance().registerTask(this, CknTaskManager.TaskType.POSTCONTINUOUS);
        } else {
            CknTaskManager.getInstance().unregisterTask(this, CknTaskManager.TaskType.POSTCONTINUOUS);
        }
    }

    @Override
    public void preContinuous(){

    }

    // Call this method every loop possible
    @Override
    public void postContinuous(){
            liftPosition = liftMotor.getCurrentPosition();
            
            double motorPower = liftPid.getOutput();

            liftMotor.setPower(Range.clip(motorPower, -1.0, 1.0));
            if(liftPid.onTarget() || CknUtil.getCurrentTime() > startTime + timeout){
                stop();
            }

    }
}
