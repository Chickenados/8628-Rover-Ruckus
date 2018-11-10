package chickenados.robotv1;

import com.qualcomm.robotcore.hardware.Servo;

import chickenlib.CknEvent;
import chickenlib.CknStopwatch;
import chickenlib.CknTaskManager;

public class RobotV1Dropper{

    Servo dropperServo;
    private CknStopwatch stopwatch;

    public RobotV1Dropper(Servo servo){
        this.dropperServo = servo;
    }

    public void drop(CknEvent event) throws InterruptedException{
        dropperServo.setPosition(RobotV1Info.DROP_POSITION);
        stopwatch = new CknStopwatch(event);
        stopwatch.setTimer(1);
    }

    public void drop() throws InterruptedException{
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
