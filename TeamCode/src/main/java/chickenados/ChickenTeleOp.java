package chickenados;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import chickenados.robotv1.RobotV1;
import chickenados.robotv1.RobotV1Info;
import chickenlib.CknDriveBase;
import chickenlib.CknTaskManager;

import static chickenados.robotv1.RobotV1Info.LIFT_DOWN_SPEED;
import static chickenados.robotv1.RobotV1Info.LIFT_NULL_SPEED;
import static chickenados.robotv1.RobotV1Info.LIFT_UP_SPEED;


@TeleOp(name="ChickenTeleOp")
public class ChickenTeleOp extends LinearOpMode {

    // Declare OpMode members.
    private ElapsedTime runtime = new ElapsedTime();
    private CRServo grabber = null;
    private Servo dropper = null;

    RobotV1 robot;

    boolean rightBumperHeld = false;

    double speed = 1.0;

    CknTaskManager mgr = new CknTaskManager();



    @Override
    public void runOpMode() {


        robot = new RobotV1(hardwareMap, telemetry);
        robot.driveBase.setMode(CknDriveBase.DriveType.ARCADE);

        grabber = hardwareMap.get(CRServo.class, "grabber");
        dropper = hardwareMap.get(Servo.class, "dropper");

        waitForStart();
        runtime.reset();



        while (opModeIsActive()) {
            mgr.executeTasks(CknTaskManager.TaskType.PRECONTINUOUS);

            robot.driveBase.drive(gamepad2.right_stick_x, gamepad2.right_stick_y,
                    gamepad2.left_stick_x, gamepad2.left_stick_y);


            if (gamepad1.left_trigger > 0.3){
                robot.liftMotor.setPower(LIFT_UP_SPEED);
            }else if (gamepad1.right_trigger > 0.3) {
                robot.liftMotor.setPower(LIFT_DOWN_SPEED);
            } else {
                robot.liftMotor.setPower(LIFT_NULL_SPEED);
            }
            //robot.dashboard.setLine(3, "Lift power" + robot.liftMotor.getPower());

            if (gamepad2.left_trigger > 0.3) {
                robot.liftMotor.setPower(LIFT_UP_SPEED);
            } else if (gamepad2.right_trigger > 0.3) {
                robot.liftMotor.setPower(LIFT_DOWN_SPEED);
            } else {
                robot.liftMotor.setPower(LIFT_NULL_SPEED);
            }

            //robot.dashboard.setLine( 4, "Lift Power" + robot.liftMotor.getPower());



            if (gamepad2.a)
                grabber.setPower(1);
            else if (gamepad2.b)
                grabber.setPower(-1);
            else
                grabber.setPower(0);

            if (gamepad1.y)
                dropper.setPosition(70);
            else if (gamepad1.x)
                dropper.setPosition(0);

            if (gamepad2.y)
                dropper.setPosition(70);
            else if (gamepad2.x)
                dropper.setPosition(0);

            if(gamepad2.right_bumper && !rightBumperHeld){
                rightBumperHeld = true;
                robot.driveBase.setSpeed(RobotV1Info.PRECISION_SPEED);
            } else if(rightBumperHeld && !gamepad1.right_bumper){
                rightBumperHeld = false;
                robot.driveBase.setSpeed(RobotV1Info.FULL_SPEED);
            }

            if(gamepad1.right_bumper){
                robot.collector.collect();
            } else if(gamepad1.left_bumper){
                robot.collector.eject();
            } else {
                robot.collector.stop();
            }

            robot.pivotMotor.setPower(gamepad1.left_stick_x);
            robot.reachMotor.setPower(-gamepad1.right_stick_y);

            // Right Dpad makes the robot into Tank drive mode.
            // Left DPad makes the robot go into Arcade drive mode.
            if(gamepad2.dpad_right)
                robot.driveBase.setMode(CknDriveBase.DriveType.TANK);
            else if (gamepad2.dpad_left)
                robot.driveBase.setMode(CknDriveBase.DriveType.ARCADE);
            else if (gamepad2.dpad_up)
                robot.driveBase.setMode(CknDriveBase.DriveType.MECANUM);

            // Display robot info to the dashboard
            //robot.dashboard.setLine(1, "Drive Mode: " + robot.driveBase.getMode());
            //robot.dashboard.setLine(2, "Speed: " + robot.driveBase.getSpeed());

            mgr.executeTasks(CknTaskManager.TaskType.POSTCONTINUOUS);
        }
    }
}
