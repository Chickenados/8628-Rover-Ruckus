package chickenlib;

import com.qualcomm.hardware.bosch.BNO055IMU;

/**
 * For keeping track of the robot's location on the field.
 */
public class CknLocationTracker implements CknTaskManager.Task{

    enum Units{
        TICKS(1),
        INCHES(1),
        MILLIMETERS(1);

        int conversion;

        Units(int unitConversion){this.conversion = unitConversion;}
    }

    public static class Parameters {
        public boolean useAccelerometer = false;
        public boolean useEncoders = false;
        public boolean useGyro = false;
    }

    private Parameters params;

    //TODO: Temporary variable.
    BNO055IMU imu;

    //locations
    private double xPos = 0.0;
    private double yPos = 0.0;
    private double heading = 0.0;

    //Drive base is for accessing motor encoders.
    private CknDriveBase driveBase;

    public CknLocationTracker(CknDriveBase driveBase, Parameters params){
        this.driveBase = driveBase;
        this.params = params;
    }

    //TODO: Temporary method, this should be handled better.
    public void setBN055IMU(BNO055IMU imu){
        this.imu = imu;
    }

    public void resetLocation(){
        xPos = 0.0;
        yPos = 0.0;
        heading = 0.0;

        if(params.useEncoders){
            driveBase.resetEncoders();
        }
    }

    //TODO: Temporary method
    public void setHeading(double heading){
        this.heading = heading;
    }

    //
    // Methods for retreiving information.
    //
    public double getYPosition(){
        return yPos;
    }

    public double getXPosition(){
        return xPos;
    }

    public double getHeading(){
        return heading;
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
            //TODO: Accelerometer location support.
            //TODO: Fix units to allow for both accelerometer and encoder measurement.
        }
        if(params.useEncoders){

            if(!driveBase.isHolonomic()){

                if(numMotors == 2){

                    double leftEncoder = driveBase.getEncoderValue(CknDriveBase.MotorType.FRONT_LEFT);
                    double rightEncoder = driveBase.getEncoderValue(CknDriveBase.MotorType.FRONT_RIGHT);

                    //Average out the two values from both sides, this isn't a real
                    // y position, just the distance the robot travels.
                    yPos = (leftEncoder + rightEncoder) / 2;


                }
                else if(numMotors == 4){
                    //TODO: Real 4 motor support
                    double enc1 = driveBase.getEncoderValue(CknDriveBase.MotorType.FRONT_LEFT);
                    double enc2 = driveBase.getEncoderValue(CknDriveBase.MotorType.FRONT_RIGHT);
                    double enc3 = driveBase.getEncoderValue(CknDriveBase.MotorType.REAR_LEFT);
                    double enc4 = driveBase.getEncoderValue(CknDriveBase.MotorType.REAR_RIGHT);

                    yPos = (enc1 + enc2 + enc3 + enc4) / 4;
                }
                else
                {
                    throw new IllegalArgumentException("Location Tracking doesn't support current drive train.");
                }
            } else {
                //TODO: Holonomic Location Tracking
            }
        }

        if(params.useGyro){
            //TODO: Gyro support
            if(imu != null){
                heading = imu.getAngularOrientation().firstAngle;
            }
        }
    }

    @Override
    public void postContinuous(){

    }

}
