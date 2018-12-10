package chickenados;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.robot.Robot;

import chickenados.robotv2.RobotV2;
import chickenlib.CknDriveBase;
import chickenlib.CknTaskManager;
import chickenlib.util.CknUtil;

@TeleOp(name = "V2 Teleop")
public class V2TeleOp extends LinearOpMode{

    CknTaskManager mgr = new CknTaskManager();
    RobotV2 robot;

    @Override
    public void runOpMode(){

        robot = new RobotV2(hardwareMap, telemetry);
        robot.driveBase.setMode(CknDriveBase.DriveType.MECANUM);
        CRServo grabber = hardwareMap.get(CRServo.class, "grabber");

        waitForStart();


        while(opModeIsActive()){
            mgr.executeTasks(CknTaskManager.TaskType.PRECONTINUOUS);

            robot.dashboard.setLine(1, "RY: " + gamepad1.right_stick_y);
            robot.dashboard.setLine(2, "RX: " + gamepad1.right_stick_x);
            robot.dashboard.setLine(3, "LX: " + gamepad1.left_stick_x);

            robot.driveBase.drive(gamepad1.left_stick_x, -gamepad1.left_stick_y,
                    gamepad1.right_stick_x, -gamepad1.right_stick_y);

            if(gamepad1.left_trigger > 0.0){
                robot.liftMotor.setPower(-1);
            } else if (gamepad1.right_trigger > 0.0){
                robot.liftMotor.setPower(1);
            } else {
                robot.liftMotor.setPower(0);
            }

            if(gamepad1.a){
                grabber.setPower(1);
            } else if(gamepad1.b){
                grabber.setPower(-1);
            } else {
                grabber.setPower(0);
            }

            mgr.executeTasks(CknTaskManager.TaskType.POSTCONTINUOUS);
        }
    }
}
