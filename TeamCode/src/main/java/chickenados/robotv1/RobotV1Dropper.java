package chickenados.robotv1;

import com.qualcomm.robotcore.hardware.Servo;

import chickenlib.util.CknEvent;
import chickenlib.util.CknStopwatch;

public class RobotV1Dropper{

    Servo dropperServo;
    private CknStopwatch stopwatch;

    public RobotV1Dropper(Servo servo){
        this.dropperServo = servo;
    }

    public void drop(CknEvent event) {
        dropperServo.setPosition(RobotV1Info.DROP_POSITION);
        stopwatch = new CknStopwatch(event);
        stopwatch.setTimer(1);
    }

    public void drop() {
        drop(null);
    }

    public void reset(CknEvent event){
        dropperServo.setPosition(RobotV1Info.DROP_RESET_POSITION);
        stopwatch = new CknStopwatch(event);
        stopwatch.setTimer(1);
    }

    public void reset(){
        reset(null);
    }

}
