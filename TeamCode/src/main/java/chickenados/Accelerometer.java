package chickenados;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.navigation.Position;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

import chickenlib.CknSmartDashboard;

@TeleOp(name = "Accelerometer Test")
public class Accelerometer extends LinearOpMode {

    BNO055IMU imu;
    CknSmartDashboard dash;

    double xPos = 0.0, yPos = 0.0, zPos = 0.0;

    @Override
    public void runOpMode() throws InterruptedException{

        dash = CknSmartDashboard.createInstance(telemetry);

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        //parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        imu = hardwareMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);

        imu.startAccelerationIntegration(new Position(), new Velocity(), 1000);

        dash.setLine(0, "-- Accelerometer Test Ready ---");

        waitForStart();

        while(opModeIsActive()){

            dash.setLine(1, "Accelerometer X; "+ imu.getLinearAcceleration().xAccel);
            dash.setLine(2, "Accelerometer Y; "+ imu.getLinearAcceleration().yAccel);
            dash.setLine(3, "Accelerometer z; "+ imu.getLinearAcceleration().zAccel);

            xPos = imu.getPosition().x;
            yPos = imu.getPosition().y;
            zPos = imu.getPosition().z;

            dash.setLine(5, "Position X: " + xPos);
            dash.setLine(6, "Position Y: " + yPos);
        }


    }
}
