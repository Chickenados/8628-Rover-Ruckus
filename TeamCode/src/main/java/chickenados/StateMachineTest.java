package chickenados;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import chickenlib.util.CknEvent;
import chickenlib.util.CknSmartDashboard;
import chickenlib.CknStateMachine;

/**
 * This class is a demonstration of the State Machine class. It allows for the linearization
 * in a class with a while loop which can be very useful for implementations like Autonomous.
 * In this class, the state machine progresses through 3 different 'states' every 3 seconds.
 */
@TeleOp(name = "State Machine Test")
@Disabled
public class StateMachineTest extends LinearOpMode{

    enum State{
        STEP_1,
        STEP_2,
        STEP_3;
    }

    CknSmartDashboard dashboard;


    CknStateMachine<State> sm = new CknStateMachine<>();
    CknEvent event = new CknEvent();
    State currState;

    @Override
    public void runOpMode(){

        sm.start(State.STEP_1);
        dashboard = CknSmartDashboard.createInstance(telemetry);


        waitForStart();

        while(opModeIsActive()){


            // Check for state and switch
            if(sm.isReady()){

                currState = sm.getState();

                switch(currState){
                    case STEP_1:
                        event.reset();

                        sm.waitForEvent(event, State.STEP_2);
                        break;
                    case STEP_2:
                        event.reset();
                        sm.waitForEvent(event, State.STEP_3);
                        break;
                    case STEP_3:
                        event.reset();

                        sm.stop();
                        break;
                }

            }
        }
    }


}
