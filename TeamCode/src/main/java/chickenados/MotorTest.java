package chickenados;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name = "Motor Test")
public class MotorTest extends LinearOpMode{

    DcMotor motor1;
    DcMotor motor2;
    DcMotor motor3;
    DcMotor motor4;

    @Override
    public void runOpMode(){

        motor1 = hardwareMap.dcMotor.get("motor1");
        motor2 = hardwareMap.dcMotor.get("motor2");
        motor3 = hardwareMap.dcMotor.get("motor3");
        motor4 = hardwareMap.dcMotor.get("motor4");

        waitForStart();

        while(opModeIsActive()){

            motor1.setPower(gamepad1.right_stick_y);
            motor2.setPower(gamepad1.right_stick_x);
            motor3.setPower(gamepad1.left_stick_y);
            motor4.setPower(gamepad1.left_stick_x);

        }

    }
}
