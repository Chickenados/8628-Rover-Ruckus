package chickenados;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import chickenados.robotv1.RobotV1;

@TeleOp(name = "Measurement")
public class MeasureOp extends LinearOpMode{

    RobotV1 robot;

    @Override
    public void runOpMode(){

        robot = new RobotV1(hardwareMap, telemetry);
        robot.driveBase.setSpeed(0.3);

        waitForStart();

        while(opModeIsActive()){
            robot.preContinuous();

            robot.driveBase.tankDrive(gamepad1.left_stick_y, gamepad1.right_stick_y);

            //Build Dashboard
            robot.dashboard.setLine(1, "YPos: " + robot.locationTracker.getYPosition());
            robot.dashboard.setLine(2, "Heading: " + robot.locationTracker.getHeading());


        }

    }
}
