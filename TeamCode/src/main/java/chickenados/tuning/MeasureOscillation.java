package chickenados.tuning;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import java.util.ArrayList;
import java.util.List;

import chickenados.robotv1.RobotV1;
import chickenlib.util.CknData;
import chickenlib.CknPIDController;
import chickenlib.util.CknUtil;

@TeleOp(name = "Measure Oscillation", group = "PID")
public class MeasureOscillation extends LinearOpMode {

    RobotV1 robot;

    CknPIDController pidToMeasure;

    boolean runCollection = true;

    List<CknData> data = new ArrayList<>();
    CknData[] crossesZero = new CknData[10];
    int crossCount = 0;

    @Override
    public void runOpMode(){
        // Initialize Robot Hardware
        robot = new RobotV1(hardwareMap, telemetry);
        // Change this to the PID that is being measured
        pidToMeasure = robot.turnPid;

        robot.dashboard.setLine(0, " -- Oscillation Measures -- ");


        waitForStart();

        robot.pidDrive.driveDistanceTank(0, 90, 1000, null);

        CknData lastData = null;
        CknData currentData;

        // Run PIDS and collect data until X button is pressed.
        while(opModeIsActive() && runCollection){
            if(lastData == null){
                lastData = new CknData(pidToMeasure.getError(), CknUtil.getCurrentTime());
            }

            currentData = new CknData(pidToMeasure.getError(), CknUtil.getCurrentTime());

            data.add(currentData);

            // Detect change of sign
            if((double) lastData.value * (double) currentData.value < 0){
                crossesZero[crossCount] = currentData;
                crossCount++;
                robot.dashboard.setLine(1, "Cross Count: " + crossCount);
                robot.dashboard.setLine(crossCount + 2,
                        "" + currentData.value + " Time:" + currentData.timestamp);
            }

            lastData = currentData;

            if(crossesZero[crossesZero.length - 1] != null) runCollection = false;
            if(gamepad1.x) runCollection = false;
        }

        robot.pidDrive.stop();

        // Make calculations to find needed information.
        // Iterate through all data points that were near y=0 and average the period
        double totalTime = 0.0;
        double avgPeriod;
        CknData previousData = null;
        for(CknData data : crossesZero){
            if(previousData != null){
                totalTime += (data.timestamp - previousData.timestamp);
            }
            previousData = data;
        }
        avgPeriod = (totalTime / (crossCount-1)) * 2;

        // Display information collected.
        robot.dashboard.setLine(crossCount + 2 + 1, "Period:" + avgPeriod);
        robot.dashboard.setLine(crossCount + 2 + 2, "kI:" + (avgPeriod/2));
        robot.dashboard.setLine(crossCount + 2 + 3, "kD:" + (avgPeriod/8));



        // Keep op mode running until Y button is pressed.
        while(opModeIsActive() && !gamepad1.y){

        }

        robot.dashboard.clearDisplay();

    }
}
