package chickenados.robotv2;

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

import chickenados.robotv1.RobotV1Collector;
import chickenados.robotv1.RobotV1Dropper;
import chickenados.robotv1.RobotV1Grabber;
import chickenados.robotv2.RobotV2Info;
import chickenados.robotv1.RobotV1Lift;
import chickenados.robotv1.RobotV1VisionAnalyzer;
import chickenlib.CknDriveBase;
import chickenlib.CknPIDController;
import chickenlib.CknPIDDrive;
import chickenlib.display.CknSmartDashboard;
import chickenlib.inputstreams.CknEncoderInputStream;
import chickenlib.inputstreams.CknLocationInputStream;
import chickenlib.location.CknLocationTracker;
import chickenlib.opmode.CknRobot;
import chickenlib.sensor.CknAccelerometer;
import chickenlib.sensor.CknBNO055IMU;

public class RobotV2 extends CknRobot {

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

    public RobotV2(HardwareMap hwMap, Telemetry telemetry){
        this(hwMap, telemetry, false, false);
    }

    public RobotV2(HardwareMap hwMap, Telemetry telemetry, boolean useVuforia){
        this(hwMap, telemetry, useVuforia, false);
    }

    /**
     *
     * @param hwMap         The HardwareMap from the opMode.
     * @param telemetry     Telemetry from the opMode.
     * @param useVuforia    Whether to use Vuforia Trackables Detection.
     * @param useTfod       Whether to use TFOD Object detection.
     */
    public RobotV2(HardwareMap hwMap, Telemetry telemetry, boolean useVuforia, boolean useTfod){

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

        frontLeft = hwMap.dcMotor.get(RobotV2Info.FRONT_LEFT_NAME);
        frontRight = hwMap.dcMotor.get(RobotV2Info.FRONT_RIGHT_NAME);
        rearLeft = hwMap.dcMotor.get(RobotV2Info.REAR_LEFT_NAME);
        rearRight = hwMap.dcMotor.get(RobotV2Info.REAR_RIGHT_NAME);

        // Initialize any other motor
        liftMotor = hwMap.dcMotor.get(RobotV2Info.LIFT_MOTOR_NAME);
        liftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Reverse Motors
        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        rearLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        //Set motors to braking
        frontLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        frontRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rearLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rearRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        CknDriveBase.Parameters params = new CknDriveBase.Parameters();
        params.driveTypes.add(CknDriveBase.DriveType.TANK);
        params.driveTypes.add(CknDriveBase.DriveType.ARCADE);
        params.driveTypes.add(CknDriveBase.DriveType.MECANUM);
        params.ticksPerRev = RobotV2Info.ENCODER_TICKS_PER_REV;
        params.gearRatio = RobotV2Info.GEAR_RATIO;
        params.wheelDiameter = RobotV2Info.WHEEL_DIAMETER_INCHES;

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

        CknPIDController.Parameters yParams = new CknPIDController.Parameters();
        yParams.allowOscillation = false;
        yParams.useWraparound = false;

        yPid = new CknPIDController(new CknPIDController.PIDCoefficients(RobotV2Info.Y_ENCODER_PID_P,
                RobotV2Info.Y_ENCODER_PID_I, RobotV2Info.Y_ENCODER_PID_D),
                new CknLocationInputStream(locationTracker, CknLocationInputStream.InputType.Y_POSITION),
                yParams);


        CknPIDController.Parameters turnParams = new CknPIDController.Parameters();
        turnParams.allowOscillation = false;
        turnParams.settlingTimeThreshold = 1;
        turnParams.useWraparound = true;
        turnParams.maxTarget = 360;
        turnParams.minTarget = 0;
        turnParams.threshold = 2.0;

        turnPid = new CknPIDController(new CknPIDController.PIDCoefficients(RobotV2Info.TURN_PID_P,
                RobotV2Info.TURN_PID_I, RobotV2Info.TURN_PID_D),
                new CknLocationInputStream(locationTracker, CknLocationInputStream.InputType.HEADING),
                turnParams);

        pidDrive = new CknPIDDrive(driveBase, yPid, turnPid);

        //
        // Lift Subsystems
        //

        CknPIDController.Parameters liftParams = new CknPIDController.Parameters();
        liftParams.allowOscillation = false;
        liftParams.useWraparound = false;

        liftPid = new CknPIDController(new CknPIDController.PIDCoefficients(RobotV2Info.LIFT_PID_P,
                RobotV2Info.LIFT_PID_I, RobotV2Info.LIFT_PID_D),
                new CknEncoderInputStream(liftMotor), liftParams);
        lift = new RobotV1Lift(liftMotor, liftPid);

        //
        // Grabber subsystem
        //

        grabber = new RobotV1Grabber(hwMap.get(CRServo.class, "grabber"));

        //
        // Dropper Subsystem
        //
        dropperServo = hwMap.get(Servo.class, RobotV2Info.DROPPER_NAME);
        dropper = new RobotV1Dropper(dropperServo);
    }

    private void initVuforia(){
        // Init Vuforia
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = RobotV2Info.VUFORIA_KEY;
        parameters.cameraName = hwMap.get(WebcamName.class, RobotV2Info.WEBCAME_NAME);

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
