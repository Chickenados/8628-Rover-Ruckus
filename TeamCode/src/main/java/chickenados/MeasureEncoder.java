package chickenados;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.Range;

import chickenlib.display.CknSmartDashboard;

@TeleOp(name = "Measure Encoder Ticks")
public class MeasureEncoder extends LinearOpMode{

    // Set this to the motor you want to measure.
    private static final String MOTOR_NAME = "lift";

    DcMotor testMotor;
    CknSmartDashboard dash;

    public void runOpMode(){

        dash = CknSmartDashboard.createInstance(telemetry);
        testMotor = hardwareMap.dcMotor.get(MOTOR_NAME);
        testMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        testMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        waitForStart();

        while(opModeIsActive()){

            double motorSpeed = gamepad1.right_stick_y;
            motorSpeed = Range.clip(motorSpeed, -1.0, 1.0);
            testMotor.setPower(motorSpeed);

            CknSmartDashboard.getInstance().setLine(3, "Position: " + testMotor.getCurrentPosition());
        }

    }
}
