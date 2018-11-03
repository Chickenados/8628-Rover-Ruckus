package chickenados;

public class RobotV0Info {

    //
    // Drive Train Motor Names
    //
    public static final String REAR_LEFT_NAME = "RearLeft";
    public static final String REAR_RIGHT_NAME = "RearRight";

    public static final double WHEEL_DIAMETER_INCHES = 3;
    public static final int ENCODER_TICKS_PER_REV = 1120;

    //PID Coefficients
    public static final double Y_ENCODER_PID_P = 0.0004;
    public static final double Y_ENCODER_PID_I = 0.0;
    public static final double Y_ENCODER_PID_D = 0.0;

    public static final double TURN_PID_P = 0.007;
    public static final double TURN_PID_I = 0.0000002;
    public static final double TURN_PID_D = 0.0;
}
