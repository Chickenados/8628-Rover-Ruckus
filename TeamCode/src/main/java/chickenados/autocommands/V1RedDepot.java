package chickenados.autocommands;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import chickenados.robotv1.RobotV1;
import chickenados.robotv1.RobotV1VisionAnalyzer;
import chickenlib.CknEvent;
import chickenlib.CknStateMachine;
import chickenlib.CknTaskManager;
import chickenlib.CknUtil;

@Autonomous(name = "V1 Red Depot")
public class V1RedDepot extends LinearOpMode{

    CknTaskManager mgr = new CknTaskManager();

    enum State{
        SCAN_MINERALS,
        LOWER_LIFT,
        RELEASE_GRABBER,
        MOVE_FROM_HOOK,
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

    private final boolean DO_SCAN_MINERALS = true;
    private final int SCAN_TIMEOUT = 5;

    private final double LEFT_MINERAL_ANGLE = 30;
    private final double RIGHT_MINERAL_ANGLE = -30;

    // END AUTONOMOUS CONSTANTS

    private double angleToMaintain;

    CknStateMachine<State> sm = new CknStateMachine<>();
    private CknEvent event = new CknEvent();

    private RobotV1 robot;

    private RobotV1VisionAnalyzer.GoldState goldState = RobotV1VisionAnalyzer.GoldState.UNKNOWN;

    private State currentState;

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
            // Execute precontinuous tasks at the beginning of the loop.
            mgr.executeTasks(CknTaskManager.TaskType.PRECONTINUOUS);

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
                            sm.waitForEvent(event, State.MOVE_FROM_HOOK);
                        }

                        break;
                    case MOVE_FROM_HOOK:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(-5, 0, 2, event);

                        sm.waitForEvent(event, State.TURN_TO_MINERAL);
                        break;
                    case TURN_TO_MINERAL:
                        event.reset();

                        // Either turn towards left or right mineral.
                        if(goldState == RobotV1VisionAnalyzer.GoldState.LEFT){
                            robot.pidDrive.driveDistanceTank(0, LEFT_MINERAL_ANGLE, 2.5, event);
                            angleToMaintain = LEFT_MINERAL_ANGLE;
                        } else {
                            robot.pidDrive.driveDistanceTank(0, RIGHT_MINERAL_ANGLE, 2.5, event);
                            angleToMaintain = RIGHT_MINERAL_ANGLE;
                        }

                        sm.waitForEvent(event, State.DRIVE_TO_MINERAL);
                        break;
                    case DRIVE_TO_MINERAL:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(-47, angleToMaintain, 4, event);

                        sm.waitForEvent(event, State.TURN_TO_DEPOT);
                        break;
                    case TURN_TO_DEPOT:
                        event.reset();

                        if(goldState == RobotV1VisionAnalyzer.GoldState.LEFT) angleToMaintain = -20;
                        if(goldState == RobotV1VisionAnalyzer.GoldState.RIGHT) angleToMaintain *= -1;

                        robot.pidDrive.driveDistanceTank(0, angleToMaintain, 2.5, event);

                        sm.waitForEvent(event, State.DRIVE_TO_DEPOT);
                        break;
                    case DRIVE_TO_DEPOT:
                        event.reset();

                        if(goldState == RobotV1VisionAnalyzer.GoldState.UNKNOWN
                                || goldState == RobotV1VisionAnalyzer.GoldState.CENTER) {
                            robot.pidDrive.driveDistanceTank(-75, 0, 4, event);
                        } else {
                            angleToMaintain = robot.locationTracker.getHeading();
                            if(goldState == RobotV1VisionAnalyzer.GoldState.LEFT){
                                robot.pidDrive.driveDistanceTank(-40, angleToMaintain, 4, event);
                            } else {
                                robot.pidDrive.driveDistanceTank(-37, angleToMaintain, 4, event);
                            }
                        }

                        sm.waitForEvent(event, State.TURN_TO_DROP);
                        break;
                    case TURN_TO_DROP:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(0, 90, 2.5, event);

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

                        // End the program if it detected left since the robot hits the mineral.
                        if(goldState != RobotV1VisionAnalyzer.GoldState.LEFT){
                            sm.waitForEvent(event, State.LINE_UP_FOR_CRATER);
                        } else {
                            sm.waitForEvent(event, State.END);
                        }
                        break;
                    case LINE_UP_FOR_CRATER:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(0, 50, 2.5, event);

                        sm.waitForEvent(event, State.DRIVE_TO_CRATER);
                        break;
                    case DRIVE_TO_CRATER:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(90, 48, 2.5,event);

                        sm.waitForEvent(event, State.END);
                        break;
                    case END:
                        event.reset();
                        sm.stop();
                        break;
                }


            }

            // Execute all postcontinuous tasks at the end of the loop.
            mgr.executeTasks(CknTaskManager.TaskType.POSTCONTINUOUS);
        }



    }
}
