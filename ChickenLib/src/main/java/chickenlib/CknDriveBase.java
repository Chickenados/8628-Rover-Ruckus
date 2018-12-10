package chickenlib;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import java.util.ArrayList;

public class CknDriveBase {

    public static class Parameters {

        public ArrayList<DriveType> driveTypes = new ArrayList<>();

        public int ticksPerRev = 1440;
        public double gearRatio = 3.0;
        public double wheelDiameter = 1.0;
    }

    public enum MotorType{
        FRONT_LEFT(0),
        FRONT_RIGHT(1),
        REAR_LEFT(2),
        REAR_RIGHT(3);

        int value;

        MotorType(int id){
            this.value = id;
        }
    }

    public enum DriveType{
        TANK,
        ARCADE,
        MECANUM;
    }

    private Parameters params;
    DriveType mode = DriveType.TANK;
    private boolean reversed;

    private int numMotors = 0;

    private DcMotor frontLeft;
    private DcMotor frontRight;
    private DcMotor rearLeft;
    private DcMotor rearRight;

    //Other info
    private double speed = 1.0;

    /**
     *  Initialize the motors to be used in the drive train.
     *
     * @param frontLeft The Front-Left motor.
     * @param frontRight The Front-Right motor.
     * @param rearLeft The Rear-Left motor.
     * @param rearRight The Rear-Right motor.
     */
    public CknDriveBase(DcMotor frontLeft, DcMotor frontRight, DcMotor rearLeft, DcMotor rearRight, Parameters params){
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.rearLeft = rearLeft;
        this.rearRight = rearRight;

        if(frontLeft != null) numMotors++;
        if(frontRight != null) numMotors++;
        if(rearLeft != null) numMotors++;
        if(rearRight != null) numMotors++;

        resetEncoders();

        this.params = params;
        // Default drive mode to first option on the list.
        this.mode = params.driveTypes.get(0);
    }

    public void setMode(DriveType mode){
        this.mode = mode;
    }

    public DriveType getMode() {
        return mode;
    }

    // Allow outside access to variables in the Parameters class.

    public double getGearRatio(){
        return params.gearRatio;
    }

    public double getWheelDiameter(){ return params.wheelDiameter; }

    public double getTicksPerRev(){ return params.ticksPerRev; }

    public CknDriveBase(DcMotor frontLeft, DcMotor frontRight, Parameters params){
        this(frontLeft, frontRight, null, null, params);
    }

    /**
     * Reverses the direction of a specific motor in the drive train.
     *
     * @param motorType the motor to invert.
     */
    public void reverseMotor(MotorType motorType){
        if(motorType == MotorType.FRONT_LEFT){
            this.frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        }
        else if(motorType == MotorType.FRONT_RIGHT){
            this.frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        }
        else if(motorType == MotorType.REAR_LEFT){
            this.rearLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        }
        else if(motorType == MotorType.REAR_RIGHT){
            this.rearRight.setDirection(DcMotorSimple.Direction.REVERSE);
        }
    }

    /**
     * Returns the number of motors initialized in this drive base.
     *
     * @return
     */
    public int getNumMotors(){
        return this.numMotors;
    }

    /**
     * @return whether the robot is holonomic
     */
    public boolean isHolonomic(){
        if(params.driveTypes.contains(DriveType.MECANUM)) return true;
        return false;
    }

    /**
     *  Method for a simple tank drive. Left power moves the left motors, right moves right motors.
     *
     * @param leftPower
     * @param rightPower
     */
    public void tankDrive(double leftPower, double rightPower){

        // Create an array for each motor power and put either left or right power into it.
        double[] motorPowers = new double[4];
        if(frontLeft != null) motorPowers[MotorType.FRONT_LEFT.value] = leftPower * speed;
        if(frontRight != null) motorPowers[MotorType.FRONT_RIGHT.value] = rightPower * speed;
        if(rearLeft != null) motorPowers[MotorType.REAR_LEFT.value] = leftPower * speed;
        if(rearRight != null) motorPowers[MotorType.REAR_RIGHT.value] = rightPower * speed;

        // Normalize each motor speed so we don't exceed 1.
        motorPowers = normalizeMotorPowers(motorPowers);

        // Set the power of each motor
        if(frontLeft != null) frontLeft.setPower(motorPowers[MotorType.FRONT_LEFT.value]);
        if(frontRight != null) frontRight.setPower(motorPowers[MotorType.FRONT_RIGHT.value]);
        if(rearLeft != null) rearLeft.setPower(motorPowers[MotorType.REAR_LEFT.value]);
        if(rearRight != null) rearRight.setPower(motorPowers[MotorType.REAR_RIGHT.value]);


    }

    /**
     * Use Mecanum drive to move the robot to a specified x-y point while maintaining an angle
     *
     * @param x The distance we want to move on the x-axis
     * @param y The distance we want to move on the y-axis
     * @param rotation The amount we want to rotate
     */
    public void mecanumDrive_Cartesian(double x, double y, double rotation){
        if(numMotors != 4){
            throw new IllegalArgumentException("Mecanum drive requires 4 motors!");
        }

        // Calculate xMove and yMove
        double[] rotatedVector = RotateVector(x, y, rotation);
        double xMove = rotatedVector[0];
        double yMove = rotatedVector[1];

        // Calculate powers for each motor.
        double[] motorPowers = new double[4];
        motorPowers[MotorType.FRONT_LEFT.value] = (xMove + yMove + rotation) * speed;
        motorPowers[MotorType.FRONT_RIGHT.value] = (-xMove + yMove - rotation) * speed;
        motorPowers[MotorType.REAR_LEFT.value] = (-xMove + yMove + rotation) * speed;
        motorPowers[MotorType.REAR_RIGHT.value] = (xMove + yMove - rotation) * speed;

        // Normalize each motor speed so we don't exceed 1.
        motorPowers = normalizeMotorPowers(motorPowers);

        // Set the power of each motor
        frontLeft.setPower(motorPowers[MotorType.FRONT_LEFT.value]);
        frontRight.setPower(motorPowers[MotorType.FRONT_RIGHT.value]);
        rearLeft.setPower(motorPowers[MotorType.REAR_LEFT.value]);
        rearRight.setPower(motorPowers[MotorType.REAR_RIGHT.value]);
    }

    public void mecanumDrive(double forward, double sideways, double rotation){
        if(numMotors != 4){
            throw new IllegalArgumentException("Mecanum drive requires 4 motors!");
        }

        double[] motorPowers = new double[4];
        motorPowers[MotorType.FRONT_LEFT.value] = (forward + sideways + rotation) * speed;
        motorPowers[MotorType.FRONT_RIGHT.value] = (forward - sideways) - rotation * speed;
        motorPowers[MotorType.REAR_LEFT.value] = (forward - sideways) + rotation * speed;
        motorPowers[MotorType.REAR_RIGHT.value] = forward + (sideways - rotation) * speed;

        // Normalize each motor speed so we don't exceed 1.
        motorPowers = normalizeMotorPowers(motorPowers);

        // Set the power of each motor
        frontLeft.setPower(motorPowers[MotorType.FRONT_LEFT.value]);
        frontRight.setPower(motorPowers[MotorType.FRONT_RIGHT.value]);
        rearLeft.setPower(motorPowers[MotorType.REAR_LEFT.value]);
        rearRight.setPower(motorPowers[MotorType.REAR_RIGHT.value]);
    }

    public void arcadeDrive(double power, double turn){

        double leftpower = (power - turn) * speed;
        double rightpower = (power + turn) * speed;
        double max = Math.max(leftpower, rightpower);
        if(max > 1.0){
            leftpower /= max;
            rightpower /= max;
        }

        tankDrive(leftpower, rightpower);

    }

    public void drive(double x1, double y1, double x2, double y2){
        switch(mode){
            case TANK:
                tankDrive(y2, y1);
                break;
            case ARCADE:
                arcadeDrive(y1, x2);
                break;
            case MECANUM:
                mecanumDrive(y2, x2, x1);
                break;
        }
    }

    //Loose Motor
    //Precision Mode
    //Program drop and drive to drop marker

    public void setSpeed(double speed){
        this.speed = speed;
        if(speed < -1) speed = -1;
        if(speed > 1) speed = 1;
    }

    public double getSpeed(){
        return speed;
    }

    public void setReversed(boolean reversed){
        this.reversed = reversed;
    }


    /**
     *  Normalize an array of motor powers to make sure it doesn't exceed 1.0
     *
     * @param motorPowers
     * @return Motor Powers scaled to fit the range of -1.0, 1.0
     */
    public double[] normalizeMotorPowers(double[] motorPowers){

        double maxPower = 0.0;

        for(int i = 0; i < motorPowers.length; i++){
            // See which power is the largest.
            if(Math.abs(motorPowers[i]) > maxPower) maxPower = Math.abs(motorPowers[i]);

        }

        // If the maximum power is greater than 1.0, scale all powers down so it equals 1.0
        if(maxPower > 1.0){
            for(int i = 0; i < motorPowers.length; i++){
                motorPowers[i] = motorPowers[i] / maxPower;
            }
        }

        return motorPowers;

    }

    /**
     * Returns encoder value from a specific motor.
     * @param motor
     * @return
     */
    public double getEncoderValue(MotorType motor){
        if(motor == MotorType.FRONT_LEFT){
            if(frontLeft != null) return frontLeft.getCurrentPosition();
        }
        else if(motor == MotorType.FRONT_RIGHT){
            if(frontRight != null) return frontRight.getCurrentPosition();
        }
        else if(motor == MotorType.REAR_LEFT){
            if(rearLeft != null) return rearLeft.getCurrentPosition();
        }
        else if(motor == MotorType.REAR_RIGHT){
            if(rearRight != null) return rearRight.getCurrentPosition();
        }
        return 0;
    }

    public void resetEncoders(){
        if(frontLeft != null) frontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        if(frontRight != null) frontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        if(rearLeft != null) rearLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        if(rearRight != null) rearRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        if(frontLeft != null) frontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        if(frontRight != null) frontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        if(rearLeft != null) rearLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        if(rearRight != null) rearRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    /**
     * Rotates a vector to a desired angle.
     * @param x The X value of the vector.
     * @param y the Y value of the vector.
     * @param angle the angle to rotate the vector to.
     * @return table of X, Y values of the rotated vector.
     */
    public double[] RotateVector(double x, double y, double angle){
        double cosA = Math.cos(angle * (3.1415 / 180.0));
        double sinA = Math.sin(angle * (3.1415 / 180.0));
        double xOut = x * cosA - y * sinA;
        double yOut = x * sinA + y * cosA;
        return new double[]{xOut, yOut};
    }

    public void stopMotors(){
        double[] motorPowers = new double[]{0,0,0,0};

        if(frontLeft != null) frontLeft.setPower(motorPowers[MotorType.FRONT_LEFT.value]);
        if(frontRight != null) frontRight.setPower(motorPowers[MotorType.FRONT_RIGHT.value]);
        if(rearLeft != null) rearLeft.setPower(motorPowers[MotorType.REAR_LEFT.value]);
        if(rearRight != null) rearRight.setPower(motorPowers[MotorType.REAR_RIGHT.value]);
    }

}
