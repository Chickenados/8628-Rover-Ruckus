package chickenlib.sensor;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

import chickenlib.util.CknData;
import chickenlib.util.CknUtil;

public class CknBNO055IMU {

    public BNO055IMU imu;

    public CknGyro gyro;
    public CknAccelerometer accelerometer;

    public CknBNO055IMU(HardwareMap hwMap, String imuName, CknAccelerometer.Parameters aParams){

        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.mode                = BNO055IMU.SensorMode.IMU;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "BNO055IMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        //parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        imu = hwMap.get(BNO055IMU.class, imuName);
        imu.initialize(parameters);

        gyro = new Gyro();
        accelerometer = new Accelerometer(aParams);
    }

    private class Gyro extends CknGyro {

        private long lastLoop = -1;
        private Orientation o;

        public Gyro(){
            super();
        }

        public CknData<Double> getRawXHeading(){
            return getRawData(0, DataType.HEADING);
        }

        public CknData<Double> getRawYHeading(){
            return getRawData(1, DataType.HEADING);
        }

        public CknData<Double> getRawZHeading(){
            return getRawData(2, DataType.HEADING);
        }

        public CknData getRawData(int axis, DataType dataType){
            double value = 0.0;

            if(dataType == DataType.HEADING) {
                long currentLoop = CknUtil.CknLoopCounter.getInstance().loop;
                if(currentLoop != lastLoop) {
                    o = imu.getAngularOrientation();
                }
                if (axis == 0) {
                    value = o.firstAngle;
                } else if (axis == 1) {
                    value = o.secondAngle;
                } else if (axis == 2) {
                    value = o.thirdAngle;
                } else {
                    value = o.firstAngle;
                }
                lastLoop = currentLoop;
            }
            return new CknData(value, CknUtil.getCurrentTime());
        }

    }

    private class Accelerometer extends CknAccelerometer {

        private long lastLoop = -1;
        private Acceleration accel;

        public Accelerometer(Parameters params){
            super(params);
        }

        public CknData<Double> getRawData(int axis, DataType dataType){
            double value = 0.0;
            long loopCount = CknUtil.CknLoopCounter.getInstance().getLoopCount();

            if(dataType == DataType.ACCELERATION){
                if(loopCount != lastLoop){
                    accel = imu.getAcceleration();
                }
                switch(axis){
                    case 0:
                        value = accel.xAccel;
                        break;
                    case 1:
                        value = accel.yAccel;
                        break;
                    case 2:
                        value = accel.zAccel;
                        break;
                }
            } else if(dataType == DataType.VELOCITY){
                Velocity v = imu.getVelocity();
                switch(axis) {
                    case 0:
                        value = v.xVeloc;
                        break;
                    case 1:
                        value = v.yVeloc;
                        break;
                    case 2:
                        value = v.zVeloc;
                        break;
                }
            }
            lastLoop = loopCount;
            return new CknData(value, CknUtil.getCurrentTime());
        }

        public CknData<Double> getRawXAccel(){
            return getRawData(0, DataType.ACCELERATION);
        }

        public CknData<Double> getRawYAccel(){
            return getRawData(1, DataType.ACCELERATION);
        }

        public CknData<Double> getRawZAccel(){
            return getRawData(2, DataType.ACCELERATION);
        }
    }

}