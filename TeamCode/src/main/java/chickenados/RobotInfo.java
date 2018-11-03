package chickenados;

/**
 * This class is for constants that we use in the Robot class.
 * I.E. PID tuning, sensor names, etc.
 */
public class RobotInfo {

    //
    // Drive Train Motor Names
    //
    public static final String FRONT_LEFT_NAME = "frontLeftWheel";
    public static final String FRONT_RIGHT_NAME = "frontRightWheel";
    public static final String REAR_LEFT_NAME = "RearLeft";
    public static final String REAR_RIGHT_NAME = "RearRight";

    public static final double WHEEL_DIAMETER_INCHES = 4;
    public static final int ENCODER_TICKS_PER_REV = 1120;

    //PID Coefficients
    public static final double Y_ENCODER_PID_P = 0.02;
    public static final double Y_ENCODER_PID_I = 0.0;
    public static final double Y_ENCODER_PID_D = 0.002;

    //Vision subsystem
    public static final boolean USE_OPENCV = true;
    public static final double[] CUBE_THRESHOLD_HUE = new double[]{16, 80};
    public static final double[] CUBE_THRESHOLD_SATURATION = new double[]{201, 255.0};
    public static final double[] CUBE_THRESHOLD_VALUE = new double[]{105, 255.0};
}
