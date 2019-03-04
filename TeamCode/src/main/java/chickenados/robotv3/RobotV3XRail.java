package chickenados.robotv3;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

import chickenlib.CknTaskManager;
import chickenlib.util.CknEvent;
import chickenlib.util.CknStopwatch;
import chickenlib.util.CknUtil;

public class RobotV3XRail implements CknTaskManager.Task{

    private enum XRailState{
        EXTENDING,
        RETRACTING,
        NONE
    }

    public static class Parameters {
        // The time to extend the collector box, in seconds
        public double extendTime = 4.0;

        //The time to retract the collector box, in seconds
        public double retractTime = 2.0;

    }

    XRailState state = XRailState.NONE;

    Parameters params;

    DcMotor xrail;


    private CknStopwatch stopwatch;

    public double startTime = 0;

    private CknEvent event;

    public RobotV3XRail(Parameters params, DcMotor xrail){
        this.xrail = xrail;
        this.params = params;
    }

    public void extend(CknEvent event) {
        state = XRailState.EXTENDING;
        startTime = CknUtil.getCurrentTime();
        CknTaskManager.getInstance().registerTask(this, CknTaskManager.TaskType.PRECONTINUOUS);
        this.xrail.setPower(1);
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
        state = XRailState.RETRACTING;
        startTime = CknUtil.getCurrentTime();
        CknTaskManager.getInstance().registerTask(this, CknTaskManager.TaskType.PRECONTINUOUS);
        this.xrail.setPower(-1);
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
        this.xrail.setPower(0);
        CknTaskManager.getInstance().unregisterTask(this, CknTaskManager.TaskType.PRECONTINUOUS);
    }

    @Override
    public void preContinuous(){
        if(state == XRailState.EXTENDING) {
            if (CknUtil.getCurrentTime() - startTime > params.extendTime) {
                stopServos();
                if(event != null){
                    event.set(true);
                }
            }
        } else if (state == XRailState.RETRACTING){
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
