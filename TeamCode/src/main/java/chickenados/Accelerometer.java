package chickenados;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

import chickenlib.CknTaskManager;
import chickenlib.sensor.CknAccelerometer;
import chickenlib.sensor.CknBNO055IMU;
import chickenlib.sensor.CknGyro;
import chickenlib.display.CknSmartDashboard;
import chickenlib.util.CknUtil;

@TeleOp(name = "Accelerometer Test")
public class Accelerometer extends LinearOpMode {

    CknBNO055IMU imu;
    CknSmartDashboard dash;
    CknUtil.CknLoopCounter lc = CknUtil.CknLoopCounter.getInstance();

    CknTaskManager mgr;

    double xPos = 0.0, yPos = 0.0, zPos = 0.0;

    @Override
    public void runOpMode() throws InterruptedException{
        mgr = new CknTaskManager();
        dash = CknSmartDashboard.createInstance(telemetry, 16);

        CknAccelerometer.Parameters aParams = new CknAccelerometer.Parameters();
        aParams.doIntegration = false;

        imu = new CknBNO055IMU(hardwareMap,"imu", aParams);

        dash.setLine(0, "-- Accelerometer Test Ready ---");

        waitForStart();

        imu.imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);

        while(opModeIsActive()){
            lc.loop ++;
            mgr.executeTasks(CknTaskManager.TaskType.PRECONTINUOUS);

            dash.setLine(1, "Accelerometer X: "+ imu.accelerometer.getRawXAccel().value);
            dash.setLine(2, "Accelerometer Y: "+ imu.accelerometer.getRawYAccel().value);
            dash.setLine(3, "Accelerometer Z: "+ imu.accelerometer.getRawZAccel().value);

            dash.setLine(5, "Gyro X: " + imu.gyro.getData(0, CknGyro.DataType.HEADING).value);
            dash.setLine(6, "Gyro Y: " + imu.gyro.getData(1, CknGyro.DataType.HEADING).value);
            dash.setLine(7, "Gyro Z: " + imu.gyro.getData(2, CknGyro.DataType.HEADING).value);

            Position p = imu.imu.getPosition();

            xPos = p.x;
            yPos = p.y;
            zPos = p.z;


            dash.setLine(8, "Position X: " + xPos);
            dash.setLine(9, "Position Y: " + yPos);
            dash.setLine(10, "Position Z: " + zPos);

            mgr.executeTasks(CknTaskManager.TaskType.POSTCONTINUOUS);

            Thread.sleep(1000);
        }


    }
}
