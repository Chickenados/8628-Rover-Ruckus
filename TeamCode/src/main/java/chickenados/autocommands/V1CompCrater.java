package chickenados.autocommands;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import chickenados.robotv1.RobotV1;
import chickenados.robotv1.RobotV1VisionAnalyzer;
import chickenlib.CknEvent;
import chickenlib.CknStateMachine;
import chickenlib.CknUtil;

@Autonomous(name = "V1 COMPETITION Crater")
public class V1CompCrater extends LinearOpMode{

    enum State{
        SCAN_MINERALS,
        LOWER_LIFT,
        RELEASE_GRABBER,
        DRIVE_FROM_GRABBER,
        TURN_TO_MINERAL,
        DRIVE_TO_MINERAL,
        END;
    }

    // AUTONOMOUS CONSTANTS

    private final boolean DO_SCAN_MINERALS = true;
    private final int SCAN_TIMEOUT = 5;

    private final double LEFT_MINERAL_ANGLE = 30;
    private final double RIGHT_MINERAL_ANGLE = -30;

    // END AUTONOMOUS CONSTANTS

    private double angleToMaintain;

    private RobotV1VisionAnalyzer.GoldState goldState = RobotV1VisionAnalyzer.GoldState.UNKNOWN;

    CknStateMachine<State> sm = new CknStateMachine<>();
    CknEvent event = new CknEvent();

    RobotV1 robot;

    State currentState;

    @Override
    public void runOpMode() throws InterruptedException{
        robot = new RobotV1(hardwareMap, telemetry, false, true);

        if(DO_SCAN_MINERALS){
            robot.activateVision();
            sm.start(V1CompCrater.State.SCAN_MINERALS);
        } else {
            sm.start(V1CompCrater.State.LOWER_LIFT);
        }

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

                        sm.waitForEvent(event, State.DRIVE_TO_MINERAL);
                        break;
                    case DRIVE_FROM_GRABBER:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(-5, 0, 3, event);

                        if(goldState != RobotV1VisionAnalyzer.GoldState.UNKNOWN
                                && goldState != RobotV1VisionAnalyzer.GoldState.CENTER) {
                            sm.waitForEvent(event, State.TURN_TO_MINERAL);
                        } else {
                            sm.waitForEvent(event, State.DRIVE_TO_MINERAL);
                        }

                        break;
                    case TURN_TO_MINERAL:
                        event.reset();

                        // Either turn towards left or right mineral.
                        if(goldState == RobotV1VisionAnalyzer.GoldState.LEFT){
                            robot.pidDrive.driveDistanceTank(0, LEFT_MINERAL_ANGLE, 4, event);
                            angleToMaintain = LEFT_MINERAL_ANGLE;
                        } else {
                            robot.pidDrive.driveDistanceTank(0, RIGHT_MINERAL_ANGLE, 4, event);
                            angleToMaintain = RIGHT_MINERAL_ANGLE;
                        }

                        sm.waitForEvent(event, State.DRIVE_TO_MINERAL);
                        break;
                    case DRIVE_TO_MINERAL:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(-33,30,4,event);

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
