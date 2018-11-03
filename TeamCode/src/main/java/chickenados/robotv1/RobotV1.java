package chickenados.robotv1;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.hardware.bosch.JustLoggingAccelerationIntegrator;
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
import chickenlib.CknLocationTracker;
import chickenlib.CknPIDController;
import chickenlib.CknPIDDrive;
import chickenlib.CknSmartDashboard;

public class RobotV1 implements CknPIDController.PIDInput{

    HardwareMap hwMap;

    public CknDriveBase driveBase;
    public CknPIDDrive pidDrive;
    public CknLocationTracker locationTracker;

    public CknPIDController yPid;
    public CknPIDController turnPid;
    CknPIDController liftPid;

    BNO055IMU imu;

    DcMotor frontLeft;
    DcMotor frontRight;
    DcMotor rearLeft;
    DcMotor rearRight;

    public DcMotor liftMotor;

    Servo dropperServo;

    public CknSmartDashboard dashboard;

    //Vuforia Variabeles
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
        // Initialize Drive Train system
        //
        frontLeft = hwMap.dcMotor.get(RobotV1Info.FRONT_LEFT_NAME);
        frontRight = hwMap.dcMotor.get(RobotV1Info.FRONT_RIGHT_NAME);
        rearLeft = hwMap.dcMotor.get(RobotV1Info.REAR_LEFT_NAME);
        rearRight = hwMap.dcMotor.get(RobotV1Info.REAR_RIGHT_NAME);

        // Initialize any other motor
        liftMotor = hwMap.dcMotor.get(RobotV1Info.LIFT_MOTOR_NAME);
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

        driveBase = new CknDriveBase(frontLeft, frontRight, rearLeft, rearRight);
        driveBase.setWheelInfo(RobotV1Info.WHEEL_DIAMETER_INCHES, RobotV1Info.ENCODER_TICKS_PER_REV, RobotV1Info.GEAR_RATIO);
        locationTracker = new CknLocationTracker(driveBase);
        locationTracker.resetLocation();

        //
        // Initialize SmartDashboard system
        //
        dashboard = CknSmartDashboard.createInstance(telemetry, 32);

        //
        // PID Drive systems
        //

        yPid = new CknPIDController(new CknPIDController.PIDCoefficients(RobotV1Info.Y_ENCODER_PID_P,
                RobotV1Info.Y_ENCODER_PID_I, RobotV1Info.Y_ENCODER_PID_D),
                this, 40);
        turnPid = new CknPIDController(new CknPIDController.PIDCoefficients(RobotV1Info.TURN_PID_P,
                RobotV1Info.TURN_PID_I, RobotV1Info.TURN_PID_D), this, 2, 1);

        pidDrive = new CknPIDDrive(driveBase, yPid, turnPid);

        //
        // Initialize sensors
        //

        // IMU
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        imu = hwMap.get(BNO055IMU.class, "imu");
        imu.initialize(parameters);

        //
        // Lift Subsystems
        //

        liftPid = new CknPIDController(new CknPIDController.PIDCoefficients(RobotV1Info.LIFT_PID_P,
                RobotV1Info.LIFT_PID_I, RobotV1Info.LIFT_PID_D), this, 20, 0);
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

    //
    // Implements CknPIDController.PidInput
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
        else if (pid == turnPid){
            return locationTracker.getHeading();
        } else if(pid == liftPid){
            return liftMotor.getCurrentPosition();
        }
        return 0.0;
    }

    //
    // Looped Methods
    //

    // Call at the beginning of the while loop
    public void preContinuous(){
        locationTracker.trackLocation();
        locationTracker.setHeading(imu.getAngularOrientation().firstAngle);
        lift.handlePids();
        dropper.handleDropper();
        pidDrive.handlePIDs();
    }

    // Call at the end of the while loop
    public void postContinuous(){

    }

}
