package chickenados.robotv1;

import com.qualcomm.robotcore.hardware.CRServo;

public class RobotV1Collector {

    CRServo servo;

    public RobotV1Collector(CRServo servo){
        this.servo = servo;
    }

    public void collect(){
        servo.setPower(1);
    }

    public void eject(){
        servo.setPower(-1);
    }

    public void stop(){
        servo.setPower(0);
    }
}
