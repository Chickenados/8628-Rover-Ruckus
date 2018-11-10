package chickenados;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import chickenlib.CknDriveBase;
import chickenlib.CknEvent;
import chickenlib.CknStateMachine;

import static chickenados.PIDTest.State.RUN_PID;

@TeleOp(name = "PID Teleop", group = "8628")
public class PIDTest extends LinearOpMode {

    enum State{
        RUN_PID;
    }

    Robot robot;
    CknEvent event;
    CknStateMachine<State> sm;
    State state;

    @Override
    public void runOpMode() {

        robot = new Robot(hardwareMap, telemetry);

        waitForStart();

        boolean targetSet = false;
        robot.cknPIDDrive.setTarget(10000);

        while (opModeIsActive() && !robot.yPid.onTarget()) {
            robot.preContinuous();
            if(!targetSet){
                robot.cknPIDDrive.setTarget(10000);
                targetSet = true;
            }

            robot.driveBase.tankDrive(robot.yPid.getOutput(), robot.yPid.getOutput());
            //robot.dashboard.setLine(1, "Y= " + robot.driveBase.getYPosition());


            //robot.driveBase.tankDrive(gamepad1.left_stick_y, gamepad1.right_stick_y);

        }

    }

}
