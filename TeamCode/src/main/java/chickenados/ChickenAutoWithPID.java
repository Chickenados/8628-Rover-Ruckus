package chickenados;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Servo;

import chickenados.robotv1.RobotV1;
import chickenlib.CknEvent;


@Autonomous(name = "ChickenAutoWithPID")
public class ChickenAutoWithPID extends LinearOpMode {


    CknEvent event = new CknEvent();

    RobotV1 robot;

    CRServo grabber;
    Servo dropper;


    @Override
    public void runOpMode() throws InterruptedException {

        robot = new RobotV1(hardwareMap, telemetry);

        grabber = hardwareMap.get(CRServo.class,"grabber");
        dropper = hardwareMap.get(Servo.class, "dropper");



        waitForStart();

        //TODO: Add linearization with state machine
        //TODO: Move atonomous method into while loop
        while(opModeIsActive()){
            robot.preContinuous();


        }


        robot.pidDrive.driveDistanceTank(10.0, 0.0, 5.0, event);


    }

}
