package chickenados.autocommands;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import chickenados.robotv1.RobotV1;
import chickenados.robotv1.RobotV1VisionAnalyzer;
import chickenlib.CknEvent;
import chickenlib.CknStateMachine;
import chickenlib.CknUtil;

@Autonomous(name = "V1 Red Depot")
public class V1RedDepot extends LinearOpMode{

    enum State{
        SCAN_MINERALS,
        LOWER_LIFT,
        RELEASE_GRABBER,
        TURN_TO_MINERAL,
        DRIVE_TO_MINERAL,
        TURN_TO_DEPOT,
        DRIVE_TO_DEPOT,
        TURN_TO_DROP,
        DROP_MARKER,
        RESET_SERVO,
        LINE_UP_FOR_CRATER,
        DRIVE_TO_CRATER,
        END;
    }

    // AUTONOMOUS CONSTANTS

    public final boolean DO_SCAN_MINERALS = true;
    public final int SCAN_TIMEOUT = 5;

    private final double LEFT_MINERAL_ANGLE = 30;
    private final double RIGHT_MINERAL_ANGLE = -30;

    // END AUTONOMOUS CONSTANTS

    CknStateMachine<State> sm = new CknStateMachine<>();
    CknEvent event = new CknEvent();

    RobotV1 robot;

    RobotV1VisionAnalyzer.GoldState goldState = RobotV1VisionAnalyzer.GoldState.UNKNOWN;

    State currentState;

    @Override
    public void runOpMode() throws InterruptedException{
        robot = new RobotV1(hardwareMap, telemetry, false, true);

        if(DO_SCAN_MINERALS){
            robot.activateVision();
            sm.start(State.SCAN_MINERALS);
        } else {
            sm.start(State.LOWER_LIFT);
        }
        event.set(true);

        waitForStart();

        while(opModeIsActive()) {

            robot.preContinuous();

            robot.dashboard.setLine(1, "State: " + currentState);
            robot.dashboard.setLine(2, "Event: " + event.isTriggered());

            if (sm.isReady()) {

                currentState = sm.getState();

                switch (currentState) {
                    case SCAN_MINERALS:
                        event.reset();

                        double startTime = CknUtil.getCurrentTime();

                        while(goldState == RobotV1VisionAnalyzer.GoldState.UNKNOWN
                                && CknUtil.getCurrentTime() < startTime + SCAN_TIMEOUT){
                            goldState = robot.analyzer.analyzeTFOD(robot.tfod.getUpdatedRecognitions());
                            robot.dashboard.setLine(3, "Gold State: " + goldState);
                        }
                        event.set(true);

                        sm.waitForEvent(event, State.LOWER_LIFT);
                        break;
                    case LOWER_LIFT:
                        event.reset();

                        robot.lift.raiseLift(event,5);

                        sm.waitForEvent(event, State.RELEASE_GRABBER);
                        break;
                    case RELEASE_GRABBER:
                        event.reset();

                        robot.grabber.release(event);

                        // If we didn't pick up the gold pos, just drive through the center one.
                        if(goldState == RobotV1VisionAnalyzer.GoldState.UNKNOWN
                                || goldState == RobotV1VisionAnalyzer.GoldState.CENTER){

                            sm.waitForEvent(event, State.DRIVE_TO_DEPOT);
                        } else {
                            sm.waitForEvent(event, State.TURN_TO_MINERAL);
                        }

                        break;
                    case TURN_TO_MINERAL:
                        event.reset();

                        // Either turn towards left or right mineral.
                        if(goldState == RobotV1VisionAnalyzer.GoldState.LEFT){
                            robot.pidDrive.driveDistanceTank(0, LEFT_MINERAL_ANGLE, 4, event);
                        } else {
                            robot.pidDrive.driveDistanceTank(0, RIGHT_MINERAL_ANGLE, 4, event);
                        }

                        sm.waitForEvent(event, State.DRIVE_TO_MINERAL);
                        break;
                    case DRIVE_TO_MINERAL:
                        event.reset();

                        sm.waitForEvent(event, State.TURN_TO_DEPOT);
                        break;
                    case TURN_TO_DEPOT:
                        event.reset();

                        sm.waitForEvent(event, State.DRIVE_TO_DEPOT);
                        break;
                    case DRIVE_TO_DEPOT:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(-75, 0, 4, event);

                        sm.waitForEvent(event, State.TURN_TO_DROP);
                        break;
                    case TURN_TO_DROP:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(0, 90, 4, event);

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

                        robot.pidDrive.driveDistanceTank(0, 50, 4, event);

                        sm.waitForEvent(event, State.DRIVE_TO_CRATER);
                        break;
                    case DRIVE_TO_CRATER:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(90, 48, 5, event);

                        sm.waitForEvent(event, State.END);
                        break;
                    case END:
                        event.reset();
                        sm.stop();
                        break;
                }


            }
        }



    }
}
