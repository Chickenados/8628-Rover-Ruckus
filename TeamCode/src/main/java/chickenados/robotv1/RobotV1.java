package chickenados.robotv1;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import chickenlib.CknDriveBase;
import chickenlib.inputstreams.CknEncoderInputStream;
import chickenlib.inputstreams.CknLocationInputStream;
import chickenlib.location.CknLocationTracker;
import chickenlib.CknPIDController;
import chickenlib.CknPIDDrive;
import chickenlib.opmode.CknRobot;
import chickenlib.display.CknSmartDashboard;
import chickenlib.sensor.CknAccelerometer;
import chickenlib.sensor.CknBNO055IMU;

public class RobotV1 extends CknRobot {

    HardwareMap hwMap;

    public CknDriveBase driveBase;
    public CknPIDDrive pidDrive;
    public CknLocationTracker locationTracker;

    public CknPIDController yPid;
    public CknPIDController turnPid;
    CknPIDController liftPid;

    CknBNO055IMU imu;

    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor rearLeft;
    DcMotor rearRight;

    public DcMotor liftMotor;
    public DcMotor pivotMotor;
    public DcMotor reachMotor;


    Servo dropperServo;
    CRServo collectorServo;

    public CknSmartDashboard dashboard;

    //Vuforia Variables
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";
    private boolean useVuforia;
    private boolean useTfod;
    private VuforiaLocalizer vuforia;
    public TFObjectDetector tfod;

    //Subsystems
    public RobotV1Lift lift;
    public RobotV1Grabber grabber;
    public RobotV1Dropper dropper;
    public RobotV1Collector collector;
    public RobotV1VisionAnalyzer analyzer = new RobotV1VisionAnalyzer(LABEL_GOLD_MINERAL);

    public RobotV1(HardwareMap hwMap, Telemetry telemetry){
        this(hwMap, telemetry, false, false);
    }

    public RobotV1(HardwareMap hwMap, Telemetry telemetry, boolean useVuforia){
        this(hwMap, telemetry, useVuforia, false);
    }

    /**
     *
     * @param hwMap         The HardwareMap from the opMode.
     * @param telemetry     Telemetry from the opMode.
     * @param useVuforia    Whether to use Vuforia Trackables Detection.
     * @param useTfod       Whether to use TFOD Object detection.
     */
    public RobotV1(HardwareMap hwMap, Telemetry telemetry, boolean useVuforia, boolean useTfod){

        this.useVuforia = useVuforia;
        this.useTfod = useTfod;
        this.hwMap = hwMap;

        //
        // If specified, Init vuforia/tfod.
        //
        if(useVuforia || useTfod){
            initVuforia();
        }
        if(useTfod){
            if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
                initTfod();
            } else {
                telemetry.addData("Sorry!", "This device is not compatible with TFOD");
            }
        }

        //
        // Initialize sensors
        //

        // Acclerometer Parameters
        CknAccelerometer.Parameters aParameters = new CknAccelerometer.Parameters();
        aParameters.doIntegration = true;

        imu = new CknBNO055IMU(hwMap,"imu", aParameters);

        //
        // Initialize Drive Train system
        //

        frontLeft = hwMap.dcMotor.get(RobotV1Info.FRONT_LEFT_NAME);
        frontRight = hwMap.dcMotor.get(RobotV1Info.FRONT_RIGHT_NAME);
        rearLeft = hwMap.dcMotor.get(RobotV1Info.REAR_LEFT_NAME);
        rearRight = hwMap.dcMotor.get(RobotV1Info.REAR_RIGHT_NAME);

        // Initialize any other motor
        liftMotor = hwMap.dcMotor.get(RobotV1Info.LIFT_MOTOR_NAME);
        liftMotor.setDirection(DcMotorSimple.Direction.REVERSE);
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Reverse Motors
        frontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        rearRight.setDirection(DcMotorSimple.Direction.REVERSE);

        //Set motors to braking
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rearLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rearRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        CknDriveBase.Parameters params = new CknDriveBase.Parameters();
        params.driveTypes.add(CknDriveBase.DriveType.TANK);
        params.driveTypes.add(CknDriveBase.DriveType.ARCADE);
        params.ticksPerRev = RobotV1Info.ENCODER_TICKS_PER_REV;
        params.gearRatio = RobotV1Info.GEAR_RATIO;
        params.wheelDiameter = RobotV1Info.WHEEL_DIAMETER_INCHES;

        driveBase = new CknDriveBase(frontLeft, frontRight, rearLeft, rearRight, params);
        driveBase.setMode(CknDriveBase.DriveType.TANK);

        //
        // Location Tracking subsystem
        //

        CknLocationTracker.Parameters LTParams = new CknLocationTracker.Parameters();
        LTParams.useEncoders = true;
        LTParams.useGyro = true;

        locationTracker = new CknLocationTracker(driveBase, imu.gyro, imu.accelerometer, LTParams);
        locationTracker.resetLocation();
        locationTracker.setTaskEnabled(true);

        //
        // Initialize SmartDashboard system
        //
        CknSmartDashboard.Parameters dashParams = new CknSmartDashboard.Parameters();
        dashParams.displayWidth = 400;
        dashParams.numLines = 32;
        dashboard = CknSmartDashboard.createInstance(telemetry, dashParams);

        //
        // PID Drive systems
        //

        CknPIDController.Parameters yPidParams = new CknPIDController.Parameters();
        yPidParams.allowOscillation = false;
        yPidParams.useWraparound = false;

        yPid = new CknPIDController(new CknPIDController.PIDCoefficients(RobotV1Info.Y_ENCODER_PID_P,
                RobotV1Info.Y_ENCODER_PID_I, RobotV1Info.Y_ENCODER_PID_D),
                new CknLocationInputStream(locationTracker, CknLocationInputStream.InputType.Y_POSITION),
                40);

        CknPIDController.Parameters turnPidParams = new CknPIDController.Parameters();
        turnPidParams.useWraparound = true;
        turnPidParams.maxTarget = 180;
        turnPidParams.minTarget = -179;
        turnPidParams.allowOscillation = true;
        turnPidParams.settlingTimeThreshold = 1;

        turnPid = new CknPIDController(new CknPIDController.PIDCoefficients(RobotV1Info.TURN_PID_P,
                RobotV1Info.TURN_PID_I, RobotV1Info.TURN_PID_D),
                new CknLocationInputStream(locationTracker, CknLocationInputStream.InputType.HEADING),
                turnPidParams);

        pidDrive = new CknPIDDrive(driveBase, yPid, turnPid);

        //
        // Lift Subsystems
        //

        CknPIDController.Parameters liftPidParams = new CknPIDController.Parameters();
        liftPidParams.useWraparound = false;
        liftPidParams.allowOscillation = false;

        liftPid = new CknPIDController(new CknPIDController.PIDCoefficients(RobotV1Info.LIFT_PID_P,
                RobotV1Info.LIFT_PID_I, RobotV1Info.LIFT_PID_D),
                new CknEncoderInputStream(liftMotor), 20, 0);
        lift = new RobotV1Lift(liftMotor, liftPid);

        //
        // Grabber subsystem
        //

        grabber = new RobotV1Grabber(hwMap.get(CRServo.class, "grabber"));

        //
        // Dropper Subsystem
        //
        dropperServo = hwMap.get(Servo.class, RobotV1Info.DROPPER_NAME);
        dropper = new RobotV1Dropper(dropperServo);

        //
        // Collector Subsystem
        //
        collectorServo = hwMap.get(CRServo.class, RobotV1Info.COLLECTOR_NAME);
        collector = new RobotV1Collector(collectorServo);

        //
        // Ball Scorer Subsystem
        //
        pivotMotor = hwMap.dcMotor.get(RobotV1Info.PIVOT_MOTOR_NAME);
        reachMotor = hwMap.dcMotor.get(RobotV1Info.REACH_MOTOR_NAME);
    }

    private void initVuforia(){
            // Init Vuforia
            VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

            parameters.vuforiaLicenseKey = RobotV1Info.VUFORIA_KEY;
            parameters.cameraName = hwMap.get(WebcamName.class, RobotV1Info.WEBCAME_NAME);

            //  Instantiate the Vuforia engine
            vuforia = ClassFactory.getInstance().createVuforia(parameters);
        if(useVuforia){
            //TODO: Add vufuria trackables init
        }
    }

    private void initTfod(){
        int tfodMonitorViewId = hwMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hwMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }

    public void activateVision(){
        if(useVuforia){
            //TODO: finish vuforia trackables support.
        }
        if(useTfod && tfod != null){
            tfod.activate();
        }
    }

    public void deactivateVision(){
        if(useTfod && tfod != null){
            tfod.deactivate();
        }
    }

    public void shutdownVision(){
        if(useTfod && tfod != null){
            tfod.shutdown();
        }
    }

}
