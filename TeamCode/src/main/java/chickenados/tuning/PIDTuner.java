package chickenados.tuning;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import chickenados.robotv1.RobotV1;
import chickenados.robotv1.RobotV1Info;
import chickenados.robotv2.RobotV2;
import chickenados.robotv2.RobotV2Info;
import chickenados.robotv3.RobotV3;
import chickenlib.CknTaskManager;
import chickenlib.util.CknEvent;
import chickenlib.CknPIDController;
import chickenlib.CknStateMachine;
import chickenlib.util.CknUtil;

@TeleOp(name = "PID Tuner")
public class PIDTuner extends LinearOpMode {

    enum PIDType{
        TURN,
        DRIVE;
    }

    private static final int ROUND_PLACES = 6;

    RobotV3 robot;
    CknStateMachine<State> sm = new CknStateMachine<>();
    CknEvent event = new CknEvent();
    State currState;
    CknTaskManager mgr = new CknTaskManager();

    PIDType currentPid = PIDType.DRIVE;

    double kP = RobotV2Info.Y_ENCODER_PID_P;
    double kI = RobotV2Info.Y_ENCODER_PID_I;
    double kD = RobotV2Info.Y_ENCODER_PID_D;

    boolean upReleased = true;
    boolean downReleased = true;
    boolean leftReleased = true;
    boolean rightReleased = true;
    boolean leftBumperReleased = true;
    boolean xReleased = true;
    boolean yReleased = true;
    boolean aReleased = true;

    // 1 = P, 2 = I, 3 = D
    int selectedCoeff = 1;
    double incrementScale = 1;

    enum State {
        DRIVE_FORWARD,
        TANK,
        IDLE;
    }

    @Override
    public void runOpMode(){

        robot = new RobotV3(hardwareMap, telemetry);

        waitForStart();

        sm.start(PIDTuner.State.IDLE);

        while(opModeIsActive()){
            CknUtil.CknLoopCounter.getInstance().loop++;
            mgr.executeTasks(CknTaskManager.TaskType.PRECONTINUOUS);

            if(currentPid == PIDType.TURN){
                robot.turnPid.printPIDValues();
            } else if(currentPid == PIDType.DRIVE){
                robot.yPid.printPIDValues();
            }

            if(gamepad1.right_bumper && currState == State.DRIVE_FORWARD) {
                robot.pidDrive.stop();
                currState = State.IDLE;
            }
            robot.dashboard.setLine(0, "State: " + currState);

            if(sm.isReady()){

                currState = sm.getState();

                switch(currState){
                    case DRIVE_FORWARD:
                        event.reset();

                        if(currentPid == PIDType.DRIVE) {
                            robot.pidDrive.driveDistanceTank(12, 0, 1000, event);
                        } else if(currentPid == PIDType.TURN){
                            robot.pidDrive.driveDistanceTank(0, 90, 1000, event);
                        }

                        sm.waitForEvent(event, State.IDLE);

                        break;
                }

            }

            if(currState == State.IDLE){

                if(sm.nextState == State.IDLE) {
                    event.reset();
                    sm.waitForEvent(event, State.DRIVE_FORWARD);
                }

                // B button applys changes to PID.
                if(gamepad1.b){
                    if(currentPid == PIDType.TURN){
                        robot.turnPid.setCoefficients(new CknPIDController.PIDCoefficients(kP, kI, kD));
                    } else if(currentPid == PIDType.DRIVE){
                        robot.yPid.setCoefficients(new CknPIDController.PIDCoefficients(kP, kI, kD));
                    }
                }

                //A button switches to DRIVE_FORWARD, performing action
                if(gamepad1.a && aReleased){
                    aReleased = false;
                    event.set(true);
                }
                if(!gamepad1.a && !aReleased){
                    aReleased = true;
                }

                //X button cycles selected coefficient to change.
                if(gamepad1.x && xReleased){
                    xReleased = false;
                    selectedCoeff += 1;
                    if(selectedCoeff > 3){
                        selectedCoeff = 1;
                    }
                }
                if(!gamepad1.x && !xReleased){
                    xReleased = true;
                }

                if(gamepad1.y && yReleased){
                    yReleased = false;
                    if(currentPid == PIDType.TURN){
                        currentPid = PIDType.DRIVE;
                        CknPIDController.PIDCoefficients coefs = robot.yPid.getCoefficients();
                        kP = coefs.kP;
                        kI = coefs.kI;
                        kD = coefs.kD;
                    } else {
                        currentPid = PIDType.TURN;
                        CknPIDController.PIDCoefficients coefs = robot.turnPid.getCoefficients();
                        kP = coefs.kP;
                        kI = coefs.kI;
                        kD = coefs.kD;
                    }
                }
                if(!gamepad1.y && !yReleased){
                    yReleased = true;
                }

                //Dpad Up increments selected coefficient.
                if(gamepad1.dpad_up && upReleased){
                    upReleased = false;
                    if(selectedCoeff == 1){
                        kP += incrementScale;
                        kP = CknUtil.round(kP, ROUND_PLACES);
                    }
                    else if(selectedCoeff == 2){
                        kI += incrementScale;
                        kI = CknUtil.round(kI, ROUND_PLACES);
                    } else {
                        kD += incrementScale;
                        kD = CknUtil.round(kD, ROUND_PLACES);
                    }
                }
                if(!gamepad1.dpad_up && !upReleased){
                    upReleased = true;
                }

                //Dpad Down decrements selected coefficient.
                if(gamepad1.dpad_down && downReleased){
                    downReleased = false;
                    if(selectedCoeff == 1){
                        kP -= incrementScale;
                        kP = CknUtil.round(kP, ROUND_PLACES);
                    }
                    else if(selectedCoeff == 2){
                        kI -= incrementScale;
                        kI = CknUtil.round(kI, ROUND_PLACES);
                    } else {
                        kD -= incrementScale;
                        kD = CknUtil.round(kD, ROUND_PLACES);
                    }
                }
                if(!gamepad1.dpad_down && !downReleased){
                    downReleased = true;
                }

                //Dpad right increments scale.
                if(gamepad1.dpad_right && rightReleased){
                    rightReleased = false;
                    incrementScale = incrementScale / 10;
                    incrementScale = CknUtil.round(incrementScale, ROUND_PLACES);
                }
                if(!gamepad1.dpad_right && !rightReleased){
                    rightReleased = true;
                }

                //Dpad left decrements scale.
                if(gamepad1.dpad_left && leftReleased){
                    leftReleased = false;
                    incrementScale = incrementScale * 10;
                    //incrementScale = CknUtil.round(incrementScale, ROUND_PLACES);
                }
                if(!gamepad1.dpad_left && !leftReleased){
                    leftReleased = true;
                }

                if(gamepad1.left_bumper && leftBumperReleased){
                    leftBumperReleased = false;
                    currState = State.TANK;
                }
                if(!gamepad1.left_bumper && !leftBumperReleased){
                    leftBumperReleased = true;
                }

                //
                // Display coefficient information
                //

                if(selectedCoeff == 1){
                    robot.dashboard.setLine(1, "Coeff: P");
                } else if (selectedCoeff == 2){
                    robot.dashboard.setLine(1, "Coeff: I");
                } else {
                    robot.dashboard.setLine(1, "Coeff: D");
                }

                robot.dashboard.setLine(2, "PID To edit: " + currentPid);
                robot.dashboard.setLine(3, "Change Scale: " + incrementScale);
                robot.dashboard.setLine(4, "P: " + kP);
                robot.dashboard.setLine(5, "I: " + kI);
                robot.dashboard.setLine(6, "D: " + kD);

            } else if(currState == State.TANK){

                robot.driveBase.tankDrive(gamepad1.left_stick_y, gamepad1.right_stick_y);

                if(gamepad1.left_bumper && leftBumperReleased){
                    leftBumperReleased = false;
                    currState = State.IDLE;
                }
                if(!gamepad1.left_bumper && !leftBumperReleased){
                    leftBumperReleased = true;
                }

            }

            mgr.executeTasks(CknTaskManager.TaskType.POSTCONTINUOUS);
        }

    }
}
