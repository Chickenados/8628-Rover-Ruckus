package chickenados.robotv1;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import chickenlib.util.CknSmartDashboard;

@Autonomous(name = "RobotV1 Full Auto")
@Disabled
public class RobotV1Auto extends LinearOpMode{

    enum Auto{
        RED_CRATER,
        RED_DEPOT,
        BLUE_CRATER,
        BLUE_DEPOT;
    }

    RobotV1 robot;
    CknSmartDashboard dash;

    @Override
    public void runOpMode(){
        robot = new RobotV1(hardwareMap, telemetry);
        dash = robot.dashboard;

        boolean confirmed = false;
        Auto currentAuto = Auto.RED_DEPOT;

        dash.setLine(0, "-- Chickenbot V1 Autonomous --");

        while(!confirmed){
            if(gamepad1.a){
                currentAuto = Auto.RED_CRATER;
            }
            if(gamepad1.b){
                currentAuto = Auto.RED_DEPOT;
            }
            if(gamepad1.x){
                currentAuto = Auto.BLUE_DEPOT;
            }
            if(gamepad1.y){
                currentAuto = Auto.BLUE_CRATER;
            }
            if(gamepad1.left_stick_button && gamepad1.right_stick_button){
                confirmed = true;
            }

            dash.setLine(1, "Selected Auto: " + currentAuto);

        }

        dash.setLine(1, "");

        //V1RedDepot auto = new V1RedDepot(robot, false);

        switch (currentAuto){
            case RED_DEPOT:
                    //auto = new V1RedDepot(robot, false);
                break;
            case BLUE_DEPOT:

                break;

            case RED_CRATER:

                break;

            case BLUE_CRATER:
        }

        waitForStart();

        while(opModeIsActive()){

            switch (currentAuto){
                case RED_DEPOT:
                        //auto.autoCommand();
                    break;
                case BLUE_DEPOT:

                    break;

                case RED_CRATER:

                    break;

                case BLUE_CRATER:
            }
        }

    }
}
