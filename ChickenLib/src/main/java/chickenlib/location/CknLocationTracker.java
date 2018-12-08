package chickenlib.location;

import com.qualcomm.hardware.bosch.BNO055IMU;

import chickenlib.CknDriveBase;
import chickenlib.CknTaskManager;
import chickenlib.sensor.CknAccelerometer;
import chickenlib.sensor.CknGyro;
import chickenlib.util.CknUnitConverter;

import static chickenlib.util.CknUnitConverter.Unit.INCHES;

/**
 * For keeping track of the robot's location on the field.
 */
public class CknLocationTracker implements CknTaskManager.Task {

    public static class Parameters {
        public CknUnitConverter.Unit positionUnit = INCHES;
        // These are the options for which inputs we can use to track location.
        // If multiple are set to true, the location will be averaged.
        public boolean useAccelerometer = false;
        public boolean useEncoders = false;
        public boolean useGyro = false;
    }

    private Parameters params;

    CknLocation location;

    //Drive base is for accessing motor encoders.
    private CknDriveBase driveBase;
    private CknAccelerometer accelerometer;
    private CknGyro gyro;

    public CknLocationTracker(CknDriveBase driveBase, CknGyro gyro, CknAccelerometer accelerometer, Parameters params){
        this.driveBase = driveBase;
        this.params = params;
        this.gyro = gyro;
        this.accelerometer = accelerometer;
        this.location = new CknLocation(0.0, 0.0, 0.0, 0.0);
    }

    public void resetLocation(){
        location.x = 0.0;
        location.y = 0.0;
        location.z = 0.0;
        location.heading = 0.0;

        if(params.useEncoders){
            driveBase.resetEncoders();
        }
    }

    //
    // Methods for retreiving information.
    //
    public CknLocation getLocation() {
        return location;
    }

    public void setTaskEnabled(boolean enabled){
        if(enabled){
            CknTaskManager.getInstance().registerTask(this, CknTaskManager.TaskType.PRECONTINUOUS);
        } else {
            CknTaskManager.getInstance().unregisterTask(this, CknTaskManager.TaskType.PRECONTINUOUS);
        }
    }

    @Override
    public void preContinuous(){
        int numMotors = driveBase.getNumMotors();

        // Calculations of position using encoders.
        if(params.useAccelerometer){
            //TODO: Accelerometer Input action.
        }
        if(params.useEncoders){

            if(!driveBase.isHolonomic()){

                if(numMotors == 2){

                    double leftEncoder = driveBase.getEncoderValue(CknDriveBase.MotorType.FRONT_LEFT);
                    double rightEncoder = driveBase.getEncoderValue(CknDriveBase.MotorType.FRONT_RIGHT);

                    //Average out the two values from both sides, this isn't a real
                    // y position, just the distance the robot travels.
                    double yPos = (leftEncoder + rightEncoder) / 2;
                    // Convert to inches
                    yPos = yPos / ((((3.1415 * driveBase.getWheelDiameter())) / driveBase.getTicksPerRev()) * driveBase.getGearRatio());
                    // Convert to other unit if needed.
                    if(params.positionUnit != INCHES){
                        yPos = CknUnitConverter.getInstance().convertValue(INCHES, params.positionUnit, yPos);
                    }
                    location.y = yPos;

                }
                else if(numMotors == 4){

                    double enc1 = driveBase.getEncoderValue(CknDriveBase.MotorType.FRONT_LEFT);
                    double enc2 = driveBase.getEncoderValue(CknDriveBase.MotorType.FRONT_RIGHT);
                    double enc3 = driveBase.getEncoderValue(CknDriveBase.MotorType.REAR_LEFT);
                    double enc4 = driveBase.getEncoderValue(CknDriveBase.MotorType.REAR_RIGHT);

                    location.y = (enc1 + enc2 + enc3 + enc4) / 4;
                }
                else
                {
                    throw new IllegalArgumentException("Location Tracking doesn't support current drive train.");
                }
            } else {

                if(numMotors == 4){
                    double enc1 = driveBase.getEncoderValue(CknDriveBase.MotorType.FRONT_LEFT);
                    double enc2 = driveBase.getEncoderValue(CknDriveBase.MotorType.FRONT_RIGHT);
                    double enc3 = driveBase.getEncoderValue(CknDriveBase.MotorType.REAR_LEFT);
                    double enc4 = driveBase.getEncoderValue(CknDriveBase.MotorType.REAR_RIGHT);

                    location.y = (enc1 + enc2 + enc3 + enc4) / 4;

                    if(driveBase.getMode() == CknDriveBase.DriveType.MECANUM) {
                        location.x = ((enc1 + enc4) - (enc2 + enc3)) / 4;
                    }
                }

            }
        }

        if(params.useGyro){
            if(gyro != null){
                location.heading = gyro.getData(0, CknGyro.DataType.HEADING).value;
            }
        }
    }

    @Override
    public void postContinuous(){

    }

}
