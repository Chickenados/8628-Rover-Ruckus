package chickenados.robotv3;

public class RobotV3Info {

    //
    // Drive Train Motor Names
    //
    public static final String REAR_LEFT_NAME = "rearLeft";
    public static final String REAR_RIGHT_NAME = "rearRight";
    public static final String FRONT_LEFT_NAME = "frontLeft";
    public static final String FRONT_RIGHT_NAME = "frontRight";

    //Other Dc Motor names
    public static final String LIFT_MOTOR_NAME = "lift";
    public static final String SPINNER_MOTOR_NAME = "spinner";
    public static final String SLIDER_MOTOR_NAME = "slider";
    public static final String XRAIL_MOTOR_NAME = "xrail";

    // Tele Op Variables
    public static final double FULL_SPEED = 1.0;
    public static final double PRECISION_SPEED = 0.3;

    //TODO: Update these values
    public static final double WHEEL_DIAMETER_INCHES = 4;
    //public static final int ENCODER_TICKS_PER_REV = 2240;
    public static final int ENCODER_TICKS_PER_REV = 2240;
    public static final double GEAR_RATIO = 2;

    //TODO: Tune PIDS
    //PID Coefficients
    public static final double Y_ENCODER_PID_P = 0.0006;
    public static final double Y_ENCODER_PID_I = 0.0;
    public static final double Y_ENCODER_PID_D = 0.0;

    //kU value (steady oscillation)
    // public static final double TURN_PID_P = 0.2;
    public static final double TURN_PID_P = 0.015;
    public static final double TURN_PID_I = 0.0;
    public static final double TURN_PID_D = 0.0;

    public static final String IMU_NAME = "imu";


    //
    // Lift Subsystem Constants
    //

    //TODO: Find these values
    public static final int RAISED_ENCODER_COUNT = 14500;
    public static final int LOWERED_ENCODER_COUNT = 0;

    public static final double LIFT_UP_SPEED = 1;
    public static final double LIFT_DOWN_SPEED = -1;
    public static final double LIFT_NULL_SPEED = 0;

    public static final double LIFT_PID_P = 0.007;
    public static final double LIFT_PID_I = 0.0;
    public static final double LIFT_PID_D = 0.0;

    //
    // Dropper Subsystem Constants
    //

    public static final String DROPPER_NAME = "dropper";
    public static final int DROP_POSITION = 70;
    public static final int DROP_RESET_POSITION = 0;
    public static final String GRABBER_NAME = "grabber";
    public static final String COLLECTOR_NAME = "collector";

    // Collector Box Constants
    public static final double extendTime = 2.0;
    public static final double retractTime = 2.0;

    public static final double xrailTimeExtend = 2.0;
    public static final double xrailTimeRetract = 2.0;



    // Webcam
    public static final String WEBCAME_NAME = "Webcam";
    public static final String VUFORIA_KEY = "AV2hPmr/////AAABmQLD9hUunkK4tSZiwFAlrpZPoN76Ej8hCf1AdzRK5+dWdO6VF0iKY/cqgZLxkQ4RCD0KXMvXtiUx87IkUWaghhJYq446Zx2MDU12MXtsE9hq8p3alcdmCCvCun+veOD/mwKlEXDnZYl8jMzxcCOpEqr3Uc2MzsjpFbrdr+m5tYXmNAKQrN9Bq4VALSSl/pUhk1/swPiJenMa938xu0pN4C+xuOCyAmNX44yln0q8GnoGmtmdMCg3NTOiEDm6K/fFTLI1nWN2LOWzVQZ88Ul0EIjgdTfA+DYgz5O8AS/leZcUn7WTbPbhy/5NaqorhI+6u1YMYYFaPq41j3lenoUU+6DdfK133dZ8+M57EvFVXJSv";

}
