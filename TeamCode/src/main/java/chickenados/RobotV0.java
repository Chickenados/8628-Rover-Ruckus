package chickenados;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import chickenlib.CknDriveBase;
import chickenlib.CknLocationTracker;
import chickenlib.CknPIDController;
import chickenlib.CknPIDDrive;
import chickenlib.CknSmartDashboard;

public class RobotV0 implements CknPIDController.PIDInput{

    DcMotor rearLeft;
    DcMotor rearRight;

    CknPIDController yPid;
    CknPIDController turnPid;

    CknDriveBase driveBase;
    CknPIDDrive cknPIDDrive;
    CknLocationTracker locationTracker;

    BNO055IMU imu;

    CknSmartDashboard dashboard;


    public RobotV0(HardwareMap hwMap, Telemetry telemetry){

        //
        // Initialize Drive Train system
        //
        rearLeft = hwMap.dcMotor.get(RobotV0Info.REAR_LEFT_NAME);
        rearRight = hwMap.dcMotor.get(RobotV0Info.REAR_RIGHT_NAME);
        rearRight.setDirection(DcMotorSimple.Direction.REVERSE);

        rearLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rearRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        /*driveBase = new CknDriveBase(rearLeft, rearRight);
        driveBase.setWheelInfo(RobotV0Info.WHEEL_DIAMETER_INCHES, RobotV0Info.ENCODER_TICKS_PER_REV, 1);
        locationTracker = new CknLocationTracker(driveBase);
        locationTracker.resetLocation();*/

        //
        // Initialize Drive Train PIDs
        //
        yPid = new CknPIDController(new CknPIDController.PIDCoefficients(RobotV0Info.Y_ENCODER_PID_P,
                RobotV0Info.Y_ENCODER_PID_I, RobotV0Info.Y_ENCODER_PID_D),
                this, 40);
        turnPid = new CknPIDController(new CknPIDController.PIDCoefficients(RobotV0Info.TURN_PID_P,
                RobotV0Info.TURN_PID_I, RobotV0Info.TURN_PID_D), this, 2, 1);

        cknPIDDrive = new CknPIDDrive(driveBase, yPid, turnPid);

        //
        // Initialize SmartDashboard system
        //
        dashboard = CknSmartDashboard.createInstance(telemetry);

        //
        // Initialize sensors
        //

        // IMU
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        imu = hwMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);

    }


    // Implements PIDInput

    /**
     * Returns inputs for the PID loops
     * @param pid
     * @return
     */
    public double getInput(CknPIDController pid){
        if(pid == yPid){
            return locationTracker.getYPosition();
        }
        else if (pid == turnPid){
            return locationTracker.getHeading();
        }
        return 0.0;
    }

    //
    // Looped Methods
    //

    // Call at the beginnnig of the TeleOp while loop
    public void preContinuous(){

    }

}
