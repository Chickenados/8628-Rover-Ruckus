package chickenados.autocommands;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import chickenados.robotv1.RobotV1;
import chickenados.robotv1.RobotV1VisionAnalyzer;
import chickenados.robotv2.RobotV2;
import chickenlib.util.CknEvent;
import chickenlib.CknStateMachine;
import chickenlib.CknTaskManager;
import chickenlib.util.CknUtil;

@Autonomous(name = "Turn Testing")
public class TurnTesting extends LinearOpMode{

    CknTaskManager mgr = new CknTaskManager();
    enum State{
        SCAN_MINERALS,
        LOWER_LIFT,
        RELEASE_GRABBER,
        FIRST_TURN,
        SECOND_TURN,
        END;
    }

    // AUTONOMOUS CONSTANTS

    private final boolean DO_SCAN_MINERALS = true;
    private final int SCAN_TIMEOUT = 5;

    private final double LEFT_MINERAL_ANGLE = 330;
    private final double RIGHT_MINERAL_ANGLE = -30;

    // END AUTONOMOUS CONSTANTS

    private double angleToMaintain;

    CknStateMachine<State> sm = new CknStateMachine<>();
    private CknEvent event = new CknEvent();

    private RobotV2 robot;

    private RobotV1VisionAnalyzer.GoldState goldState = RobotV1VisionAnalyzer.GoldState.UNKNOWN;

    private State currentState;

    @Override
    public void runOpMode() throws InterruptedException{
        CknUtil.CknLoopCounter.getInstance().loop++;
        robot = new RobotV2(hardwareMap, telemetry, false, true);

        if(DO_SCAN_MINERALS){
            robot.activateVision();
            sm.start(State.SCAN_MINERALS);
        } else {
            sm.start(State.LOWER_LIFT);
        }
        event.set(true);

        waitForStart();

        while(opModeIsActive()) {

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

                            sm.waitForEvent(event, State.FIRST_TURN);
                        } else {
                            sm.waitForEvent(event, State.FIRST_TURN);
                        }

                        break;
                    case  FIRST_TURN:
                        event.reset();

                        robot.pidDrive.driveDistanceTank(0,-20,5,event);



                        sm.waitForEvent(event, State.SECOND_TURN);

                        break;



                    case END:

                        event.reset();

                        sm.stop();

                        break;

                }





            }



            mgr.executeTasks(CknTaskManager.TaskType.POSTCONTINUOUS);

        }







    }

}
