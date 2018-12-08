package chickenados;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
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

            robot.driveBase.drive(gamepad1.right_stick_x, gamepad1.right_stick_y,
                    gamepad1.left_stick_x, gamepad1.left_stick_y);

            mgr.executeTasks(CknTaskManager.TaskType.POSTCONTINUOUS);
        }
    }
}
