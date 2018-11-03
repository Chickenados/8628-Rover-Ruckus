package chickenados.tuning;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import chickenados.robotv1.RobotV1;
import chickenados.robotv1.RobotV1Info;
import chickenlib.CknEvent;
import chickenlib.CknPIDController;
import chickenlib.CknStateMachine;

@TeleOp(name = "PID Tuner")
public class PIDTuner extends LinearOpMode {

    RobotV1 robot;
    CknStateMachine<State> sm = new CknStateMachine<>();
    CknEvent event = new CknEvent();
    State currState;

    double kP = RobotV1Info.Y_ENCODER_PID_P;
    double kI = RobotV1Info.Y_ENCODER_PID_I;
    double kD = RobotV1Info.Y_ENCODER_PID_D;

    boolean upReleased = true;
    boolean downReleased = true;
    boolean leftReleased = true;
    boolean rightReleased = true;
    boolean xReleased = true;
    boolean aReleased = true;

    // 1 = P, 2 = I, 3 = D
    int selectedCoeff = 1;
    double incrementScale = 1;

    enum State {
        DRIVE_FORWARD,
        IDLE;
    }

    @Override
    public void runOpMode(){

        robot = new RobotV1(hardwareMap, telemetry);

        waitForStart();

        sm.start(PIDTuner.State.IDLE);

        while(opModeIsActive()){

            robot.preContinuous();
            robot.yPid.printPIDValues();

            if(sm.isReady()){

                currState = sm.getState();
                robot.dashboard.setLine(0, "State: " + currState);

                switch(currState){
                    case DRIVE_FORWARD:
                        event.reset();

                        //robot.cknPIDDrive.driveStraightTank(12, 0, 10, event);
                        robot.pidDrive.driveDistanceTank(12, 0, 1000, event);

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
                    robot.yPid.setCoefficients(new CknPIDController.PIDCoefficients(kP, kI, kD));
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

                //Dpad Up increments selected coefficient.
                if(gamepad1.dpad_up && upReleased){
                    upReleased = false;
                    if(selectedCoeff == 1){
                        kP += incrementScale;
                    }
                    else if(selectedCoeff == 2){
                        kI += incrementScale;
                    } else {
                        kD += incrementScale;
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
                    }
                    else if(selectedCoeff == 2){
                        kI -= incrementScale;
                    } else {
                        kD -= incrementScale;
                    }
                }
                if(!gamepad1.dpad_down && !downReleased){
                    downReleased = true;
                }

                //Dpad right increments scale.
                if(gamepad1.dpad_right && rightReleased){
                    rightReleased = false;
                    incrementScale = incrementScale * 10;
                }
                if(!gamepad1.dpad_right && !rightReleased){
                    rightReleased = true;
                }

                //Dpad left decrements scale.
                if(gamepad1.dpad_left && leftReleased){
                    leftReleased = false;
                    incrementScale = incrementScale / 10;
                }
                if(!gamepad1.dpad_left && !leftReleased){
                    leftReleased = true;
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

                robot.dashboard.setLine(2, "Change Scale: " + incrementScale);
                robot.dashboard.setLine(3, "P: " + kP);
                robot.dashboard.setLine(4, "I: " + kI);
                robot.dashboard.setLine(5, "D: " + kD);



            }

        }


    }
}
