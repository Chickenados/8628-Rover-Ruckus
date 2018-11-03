package chickenlib;

/**
 * For keeping track of the robot's location on the field.
 */
public class CknLocationTracker {

    enum Units{
        TICKS(1),
        INCHES(1),
        MILLIMETERS(1);

        int conversion;

        Units(int unitConversion){this.conversion = unitConversion;}
    }

    // Options for sensors that can be used to track location.
    private boolean useAccelerometer;
    private boolean useEncoders = true;
    private boolean useGyro;

    //locations
    private double xPos = 0.0;
    private double yPos = 0.0;
    private double heading = 0.0;

    //Drive base is for accessing motor encoders.
    CknDriveBase driveBase;

    public CknLocationTracker(CknDriveBase driveBase){
        this.driveBase = driveBase;
        if(driveBase != null) useEncoders = true;
    }

    public void resetLocation(){
        xPos = 0.0;
        yPos = 0.0;
        heading = 0.0;

        if(useEncoders){
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

    /**
     * Call this as often as possible to keep an accurate representation of the current location.
     */
    public void trackLocation(){
        int numMotors = driveBase.getNumMotors();

        // Calculations of position using encoders.
        if(useAccelerometer){
            //TODO: Accelerometer location support.
        }
        if(useEncoders){

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
            }
        }
        if(useGyro){
            //TODO: Gyro support
        }

        //CknSmartDashboard.getInstance().setLine(1, "Y: " + yPos + " Heading: " + heading);
    }


}
