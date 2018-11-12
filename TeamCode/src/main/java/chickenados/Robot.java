package chickenados;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import chickenlib.CknDriveBase;
import chickenlib.location.CknLocationTracker;
import chickenlib.CknPIDController;
import chickenlib.CknPIDDrive;
import chickenlib.util.CknSmartDashboard;

public class Robot implements CknPIDController.PIDInput{

    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor rearLeft;
    DcMotor rearRight;

    CknPIDController yPid;
    CknPIDController turnPid;

    CknDriveBase driveBase;
    CknPIDDrive cknPIDDrive;
    CknLocationTracker locationTracker;

    CknSmartDashboard dashboard;

    // Called to init robot.
    public Robot(HardwareMap hwMap, Telemetry telemetry){

        //
        // Initialize Drive Train system
        //
        //frontLeft = hwMap.dcMotor.get(RobotInfo.FRONT_LEFT_NAME);
        //frontRight = hwMap.dcMotor.get(RobotInfo.FRONT_RIGHT_NAME);
        rearLeft = hwMap.dcMotor.get(RobotInfo.REAR_LEFT_NAME);
        rearRight = hwMap.dcMotor.get(RobotInfo.REAR_RIGHT_NAME);
       // driveBase = new CknDriveBase(rearLeft, rearRight);
        //locationTracker = new CknLocationTracker(driveBase);
        //locationTracker.resetLocation();


        //
        // Initialize Drive Train PIDs
        //
        yPid = new CknPIDController(new CknPIDController.PIDCoefficients(RobotInfo.Y_ENCODER_PID_P,
                RobotInfo.Y_ENCODER_PID_I, RobotInfo.Y_ENCODER_PID_D),
                this, 40);

        cknPIDDrive = new CknPIDDrive(driveBase, yPid, null);

        //
        // Initialize SmartDashboard system
        //
        dashboard = CknSmartDashboard.createInstance(telemetry);

    }

    //
    // Vision Methods
    //

    //
    //  Implements CknPIDController.PIDInput
    //

    /**
     * Returns inputs for the PID loops
     * @param pid
     * @return
     */
    public double getInput(CknPIDController pid){
        if(pid == yPid){
            return locationTracker.getYPosition();
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
