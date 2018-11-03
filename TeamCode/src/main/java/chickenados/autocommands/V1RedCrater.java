package chickenados.autocommands;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import chickenados.robotv1.RobotV1;
import chickenlib.CknEvent;
import chickenlib.CknStateMachine;

@Autonomous(name = "V1 Red Crater")
public class V1RedCrater extends LinearOpMode{

    enum State{
        SCAN_MINERALS,
        LOWER_LIFT,
        RELEASE_GRABBER,
        TURN_TO_MINERAL,
        DRIVE_TO_MINERAL,
        HIT_MINERAL,
        DRIVE_TO_WALL,
        /*DRIVE_TO_DEPOT,
        TURN_TO_DROP,
        DROP_MARKER,
        RESET_SERVO,
        LINE_UP_FOR_CRATER,
        DRIVE_TO_CRATER,*/
        END;
    }

    // AUTONOMOUS CONSTANTS

    // END AUTONOMOUS CONSTANTS

    CknStateMachine<State> sm = new CknStateMachine<>();
    CknEvent event = new CknEvent();

    RobotV1 robot;

    State currentState;

    @Override
    public void runOpMode() throws InterruptedException{
        robot = new RobotV1(hardwareMap, telemetry);

        sm.start(State.LOWER_LIFT);
        event.set(true);

        waitForStart();

        while(opModeIsActive()) {

            robot.preContinuous();

            robot.dashboard.setLine(1, "State: " + currentState);
            robot.dashboard.setLine(2, "Event: " + event.isTriggered());
            robot.yPid.printPIDValues();

            if (sm.isReady()) {

                currentState = sm.getState();

                switch (currentState) {
                    case SCAN_MINERALS:
                        event.reset();

                        break;
                    case LOWER_LIFT:
                        event.reset();

                        robot.lift.raiseLift(event,5);

                        sm.waitForEvent(event, State.RELEASE_GRABBER);
                        break;
                    case RELEASE_GRABBER:
                        event.reset();

                        robot.grabber.release(event);

                        sm.waitForEvent(event, State.DRIVE_TO_MINERAL);
                        break;
                    case TURN_TO_MINERAL:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(0,30, 3, event);

                        sm.waitForEvent(event, State.DRIVE_TO_MINERAL);
                        break;
                    case DRIVE_TO_MINERAL:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(-30,30,4,event);

                        sm.waitForEvent(event, State.HIT_MINERAL);
                        break;
                    case HIT_MINERAL:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(0,90,3, event);

                        sm.waitForEvent(event, State.DRIVE_TO_WALL);
                        break;
                    case DRIVE_TO_WALL:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(20,90, 3,event);

                        sm.waitForEvent(event, State.END);
                        break;
                    /*case DRIVE_TO_DEPOT:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(-90, 135, 4, event);

                        sm.waitForEvent(event, State.TURN_TO_DROP);
                        break;
                    case TURN_TO_DROP:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(0, -90, 4, event);

                        sm.waitForEvent(event, State.DROP_MARKER);
                        break;
                    case DROP_MARKER:
                        event.reset();

                        robot.dropper.drop(event);

                        sm.waitForEvent(event, State.RESET_SERVO);
                        break;
                    case RESET_SERVO:
                        event.reset();

                        robot.dropper.reset(event);

                        sm.waitForEvent(event, State.LINE_UP_FOR_CRATER);
                        break;
                    case LINE_UP_FOR_CRATER:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(0, 135, 4, event);

                        sm.waitForEvent(event, State.DRIVE_TO_CRATER);
                        break;
                    case DRIVE_TO_CRATER:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(90, 135, 5, event);

                        sm.waitForEvent(event, State.END);
                        break;*/
                    case END:
                        event.reset();
                        sm.stop();
                        break;
                }


            }
        }



    }
}
