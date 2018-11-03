package chickenados;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

@Autonomous (name = "ChickenAuto")
public class ChickenAuto extends LinearOpMode {

    private ElapsedTime runtime = new ElapsedTime();

    static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // This is < 1.0 if geared UP
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     DRIVE_SPEED             = 0.6;
    static final double     TURN_SPEED              = 0.5;
    static final double     LIFT_SPEED              = 0.5;

    DcMotor rearRight;
    DcMotor rearLeft;
    CRServo grabber;
    Servo dropper;
    DcMotor lift;

    ColorSensor colorSensor;

    @Override
    public void runOpMode() throws InterruptedException{

        lift  = hardwareMap.get(DcMotor.class, "lift");
        grabber = hardwareMap.get(CRServo.class, "grabber");
        dropper = hardwareMap.get(Servo.class, "dropper");
        rearLeft = hardwareMap.get(DcMotor.class, "rearLeft");
        rearRight = hardwareMap.get(DcMotor.class, "rearRight");
        colorSensor = hardwareMap.get(ColorSensor.class, "colorSensor");


        rearLeft.setDirection(DcMotor.Direction.FORWARD);
        rearRight.setDirection(DcMotor.Direction.REVERSE);
        lift.setDirection(DcMotor.Direction.FORWARD);

        telemetry.addData("Status", "Resetting Encoders");
        telemetry.update();

        rearRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rearLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        rearRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        rearLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        telemetry.addData("Path0",  "Starting at %7d :%7d",
                rearLeft.getCurrentPosition(),
                rearRight.getCurrentPosition());
        telemetry.update();

        telemetry.addData("Path0",  "Starting at %7d",
                lift.getCurrentPosition());
        telemetry.update();

       boolean bLedOn = true;

        float hsvValues[] = {0F,0F,0F};
        final float values[] = hsvValues;

        colorSensor.enableLed(bLedOn);


        telemetry.addData("Status", "Initialized");
        telemetry.update();

        waitForStart();

        /*liftUsingEncoders(LIFT_SPEED, -5, 10);
        grabber.setPower(-.75);
        Thread.sleep(1000);
        grabber.setPower(0);*/

        driveUsingEncoders(DRIVE_SPEED,36,  36, 7.0);
        driveUsingEncoders(TURN_SPEED,-12, 12, 5.0);
        dropper.setPosition(0);
        Thread.sleep(10000);






    }

    public void driveUsingEncoders (double speed,
                                    double leftInches, double rightInches,
                                    double timeoutS) {
        int newLeftTarget;
        int newRightTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            newLeftTarget = rearLeft.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
            newRightTarget = rearRight.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
            rearLeft.setTargetPosition(newLeftTarget);
            rearRight.setTargetPosition(newRightTarget);


            rearLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rearRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            rearLeft.setPower(Math.abs(speed));
            rearRight.setPower(Math.abs(speed));


            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (rearLeft.isBusy() && rearRight.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d :%7d", newLeftTarget,  newRightTarget);
                telemetry.addData("Path2",  "Running at %7d :%7d",
                        rearLeft.getCurrentPosition(),
                        rearRight.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            rearLeft.setPower(0);
            rearRight.setPower(0);

            // Turn off RUN_TO_POSITION
            rearLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rearRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            //  sleep(250);   // optional pause after each move
        }
    }

    public void liftUsingEncoders (double speed, double liftInches, double timeoutS) {

        int liftTarget;

        // Ensure that the opmode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            liftTarget = lift.getCurrentPosition() + (int)(liftInches * COUNTS_PER_INCH);
            rearLeft.setTargetPosition(liftTarget);



            lift.setMode(DcMotor.RunMode.RUN_TO_POSITION);


            // reset the timeout time and start motion.
            runtime.reset();
            lift.setPower(Math.abs(speed));


            while (opModeIsActive() &&
                    (runtime.seconds() < timeoutS) &&
                    (lift.isBusy())) {

                // Display it for the driver.
                telemetry.addData("Path1",  "Running to %7d", liftTarget);
                telemetry.addData("Path2",  "Running at %7d",
                        lift.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion;
            lift.setPower(0);

            // Turn off RUN_TO_POSITION
            lift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


            //  sleep(250);   // optional pause after each move
        }
    }

}
