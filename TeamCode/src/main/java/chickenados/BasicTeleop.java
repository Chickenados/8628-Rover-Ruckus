package chickenados;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import chickenlib.CknDriveBase;

@TeleOp(name = "Basic Teleop", group = "8628")
public class BasicTeleop extends LinearOpMode {

    RobotV0 robot;

    @Override
    public void runOpMode() {

        robot = new RobotV0(hardwareMap, telemetry);

        waitForStart();

        while (opModeIsActive()) {

            robot.driveBase.tankDrive(gamepad1.left_stick_y, gamepad1.right_stick_y);
            robot.dashboard.setLine(1, "Y=" + robot.locationTracker.getYPosition());

        }

    }

}
