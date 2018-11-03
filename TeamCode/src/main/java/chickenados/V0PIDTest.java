package chickenados;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import chickenlib.CknEvent;
import chickenlib.CknStateMachine;

@Autonomous(name = "V0 PID Test")
public class V0PIDTest extends LinearOpMode{


    RobotV0 robot;
    CknStateMachine<State> sm = new CknStateMachine<>();
    CknEvent event = new CknEvent();
    State currState;

    enum State {
        DRIVE_FORWARD,
        DRIVE_BACKWARD;
    }

    @Override
    public void runOpMode(){

        robot = new RobotV0(hardwareMap, telemetry);

        waitForStart();

        sm.start(State.DRIVE_FORWARD);

        while(opModeIsActive()){

            robot.preContinuous();

            robot.dashboard.setLine(1, "Y: " + robot.locationTracker.getYPosition());


            if(sm.isReady()){

                currState = sm.getState();
                robot.dashboard.setLine(0, "State: " + currState);

                switch(currState){
                    case DRIVE_FORWARD:
                        event.reset();

                        //robot.cknPIDDrive.driveStraightTank(12, 0, 10, event);
                        robot.cknPIDDrive.driveDistanceTank(12, 45, 100, event);

                        sm.waitForEvent(event, State.DRIVE_BACKWARD);

                        break;
                    case DRIVE_BACKWARD:
                        robot.cknPIDDrive.stop();

                        sm.stop();

                        break;
                }

            }

        }


    }
}
