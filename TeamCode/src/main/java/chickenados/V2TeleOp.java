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


        waitForStart();


        while(opModeIsActive()){
            mgr.executeTasks(CknTaskManager.TaskType.PRECONTINUOUS);

            robot.dashboard.setLine(1, "RY: " + gamepad1.right_stick_y);
            robot.dashboard.setLine(2, "RX: " + gamepad1.right_stick_x);
            robot.dashboard.setLine(3, "LX: " + gamepad1.left_stick_x);

            //
            // Gamepad 1
            //

            robot.driveBase.drive(gamepad1.right_stick_x, -gamepad1.left_stick_y,
                    gamepad1.left_stick_x, -gamepad1.left_stick_y);

            if(gamepad1.left_trigger > 0.0){

            }

            if (gamepad1.right_trigger > 0.0){

            }

            if(gamepad1.right_bumper){

            }

            if(gamepad1.left_bumper){

            }


            if(gamepad1.a){

            }

            if(gamepad1.b){

            }

            if (gamepad1.y) {

            }

            if (gamepad1.x) {

            }

            //
            // Gamepad 2
            //

            if(gamepad2.left_trigger > 0.0){

            }

            if (gamepad2.right_trigger > 0.0){

            }

            if(gamepad2.right_bumper){

            }

            if(gamepad2.left_bumper){

            }

            if(gamepad2.a){

            }

            if(gamepad2.b){

            }

            if (gamepad2.y) {

            }

            if (gamepad2.x) {

            }


            mgr.executeTasks(CknTaskManager.TaskType.POSTCONTINUOUS);
        }
    }
}
