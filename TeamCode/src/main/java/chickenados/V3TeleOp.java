package chickenados;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
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
        CRServo collector1 = hardwareMap.get(CRServo.class, "collector1");
        CRServo collector2 = hardwareMap.get(CRServo.class, "collector2");
        collector2.setDirection(DcMotorSimple.Direction.REVERSE);
        CRServo scorer = hardwareMap.get(CRServo.class, "scorer");

        double previousPosition1 = 0.0;
        double previousPosition2= 180.0;
        double collectorRot = 0.0;
        waitForStart();


        while(opModeIsActive()){
            CknUtil.CknLoopCounter.getInstance().loop++;
            mgr.executeTasks(CknTaskManager.TaskType.PRECONTINUOUS);

            robot.dashboard.setLine(1, "RY: " + gamepad1.right_stick_y);
            robot.dashboard.setLine(2, "RX: " + gamepad1.right_stick_x);
            robot.dashboard.setLine(3, "LX: " + gamepad1.left_stick_x);

            robot.driveBase.drive(gamepad2.left_stick_x, -gamepad2.left_stick_y,
                    gamepad2.right_stick_x, -gamepad2.right_stick_y);
            /*if(gamepad2.left_stick_x > 0.05) {
                collectorRot += 0.3;
                if(collectorRot > 360) collectorRot = 360;

                collector1.setPosition(collectorRot);
                collector2.setPosition(collectorRot + 180.0);
            }

            if(gamepad2.left_stick_x < -0.05) {
                collectorRot -= 0.3;
                if(collectorRot < 0) collectorRot = 0;

                collector1.setPosition(collectorRot);
                collector2.setPosition(collectorRot + 180.0);
            }*/

            collector1.setPower(gamepad1.left_stick_x);
            collector2.setPower(gamepad1.left_stick_x);


            if(gamepad2.x){
                robot.liftMotor.setPower(-1);
            } else if (gamepad2.y){
                robot.liftMotor.setPower(1);
            } else {
                robot.liftMotor.setPower(0);
            }

            if (gamepad2.right_bumper) {
                robot.spinnerMotor.setPower(-1);
            } else if (gamepad2.left_bumper) {
                robot.spinnerMotor.setPower(1);
            } else {
                robot.spinnerMotor.setPower(0);
            }

            if (gamepad2.right_trigger > 0.0) {
                robot.sliderMotor.setPower(-1);
            } else if (gamepad2.left_trigger > 0.0) {
                robot.sliderMotor.setPower(1);
            } else {
                robot.sliderMotor.setPower(0);
            }

            if(gamepad2.a){
                grabber.setPower(1);
            } else if(gamepad2.b){
                grabber.setPower(-1);
            } else {
                grabber.setPower(0);
            }
            if(gamepad1.a){
                grabber.setPower(1);
            } else if(gamepad1.b){
                grabber.setPower(-1);
            } else {
                grabber.setPower(0);
            }

            robot.sliderMotor.setPower(gamepad1.left_stick_y);


            robot.xrailMotor.setPower(-gamepad1.right_stick_y);
            if(gamepad1.dpad_up) scorer.setPower(1);
            else if(gamepad1.dpad_down) scorer.setPower(-1);
            else scorer.setPower(0);


            if (gamepad1.right_bumper) {
                robot.spinnerMotor.setPower(-1);
            } else if (gamepad1.left_bumper) {
                robot.spinnerMotor.setPower(1);
            } else {
                robot.spinnerMotor.setPower(0);
            }

            mgr.executeTasks(CknTaskManager.TaskType.POSTCONTINUOUS);
        }
    }
}
