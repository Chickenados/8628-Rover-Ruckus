package chickenados;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robot.Robot;

import chickenados.robotv3.RobotV3;
import chickenlib.CknDriveBase;
import chickenlib.CknTaskManager;
import chickenlib.util.CknUtil;

@TeleOp(name = "V3 Teleop")
public class V3TeleOp extends LinearOpMode{

    CknTaskManager mgr = new CknTaskManager();
    RobotV3 robot;

    @Override
    public void runOpMode(){

        robot = new RobotV3(hardwareMap, telemetry);
        robot.driveBase.setMode(CknDriveBase.DriveType.MECANUM);
        CRServo grabber = hardwareMap.get(CRServo.class, "grabber");
        Servo collector1 = hardwareMap.get(Servo.class, "collector1");
        Servo collector2 = hardwareMap.get(Servo.class, "collector2");
        CRServo scorer = hardwareMap.get(CRServo.class, "scorer");

        double previousPosition1 = 0.0;
        double previousPosition2= 180.0;
        waitForStart();


        while(opModeIsActive()){
            mgr.executeTasks(CknTaskManager.TaskType.PRECONTINUOUS);

            robot.dashboard.setLine(1, "RY: " + gamepad1.right_stick_y);
            robot.dashboard.setLine(2, "RX: " + gamepad1.right_stick_x);
            robot.dashboard.setLine(3, "LX: " + gamepad1.left_stick_x);

            robot.driveBase.drive(gamepad1.left_stick_x, -gamepad1.left_stick_y,
                    gamepad1.right_stick_x, -gamepad1.right_stick_y);
            while(gamepad2.left_stick_x > 0.05) {
                collector1.setPosition(previousPosition1);
                collector2.setPosition(previousPosition2);
                previousPosition1 += 0.03;
                previousPosition2 -= 0.03;
            }

            while(gamepad2.left_stick_x < -0.05) {
                collector1.setPosition(previousPosition1);
                collector2.setPosition(previousPosition2);
                previousPosition1 -= 0.03;
                previousPosition2 += 0.03;
            }


            if(gamepad1.x){
                robot.liftMotor.setPower(-1);
            } else if (gamepad1.y){
                robot.liftMotor.setPower(1);
            } else {
                robot.liftMotor.setPower(0);
            }

            if (gamepad1.right_bumper) {
                robot.spinnerMotor.setPower(-1);
            } else if (gamepad1.left_bumper) {
                robot.spinnerMotor.setPower(1);
            } else {
                robot.spinnerMotor.setPower(0);
            }

            if (gamepad1.right_trigger > 0.0) {
                robot.sliderMotor.setPower(-1);
            } else if (gamepad1.left_trigger > 0.0) {
                robot.sliderMotor.setPower(1);
            } else {
                robot.sliderMotor.setPower(0);
            }

            if(gamepad1.a){
                grabber.setPower(1);
            } else if(gamepad1.b){
                grabber.setPower(-1);
            } else {
                grabber.setPower(0);
            }
            if(gamepad2.a){
                grabber.setPower(1);
            } else if(gamepad2.b){
                grabber.setPower(-1);
            } else {
                grabber.setPower(0);
            }

            if (gamepad2.y)
                robot.dropper.drop();
            else if (gamepad2.x)
                robot.dropper.reset();

            robot.sliderMotor.setPower(gamepad2.left_stick_y);


            robot.xrailMotor.setPower(gamepad2.right_stick_y);
            scorer.setPower(gamepad2.right_stick_x);


            if (gamepad2.right_bumper) {
                robot.spinnerMotor.setPower(-1);
            } else if (gamepad2.left_bumper) {
                robot.spinnerMotor.setPower(1);
            } else {
                robot.spinnerMotor.setPower(0);
            }

            mgr.executeTasks(CknTaskManager.TaskType.POSTCONTINUOUS);
        }
    }
}
