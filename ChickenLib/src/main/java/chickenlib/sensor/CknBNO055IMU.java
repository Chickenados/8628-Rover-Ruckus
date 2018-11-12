package chickenlib.sensor;

import com.qualcomm.hardware.bosch.BNO055IMU;

import org.firstinspires.ftc.robotcore.external.navigation.Acceleration;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.Velocity;

import chickenlib.logging.CknDbgLog;
import chickenlib.util.CknData;
import chickenlib.util.CknUtil;

public class CknBNO055IMU {

    BNO055IMU imu;

    public CknGyro gyro;
    public CknAccelerometer accelerometer;

    public CknBNO055IMU(BNO055IMU imu, BNO055IMU.Parameters params, CknAccelerometer.Parameters aParams){
        this.imu = imu;

        imu.initialize(params);

        gyro = new Gyro();
        accelerometer = new Accelerometer(aParams);
    }

    private class Gyro extends CknGyro {

        public Gyro(){
            super();
        }

        public CknData getRawData(int axis, DataType dataType){
            double value = 0.0;

            if(dataType == DataType.HEADING) {
                Orientation o = imu.getAngularOrientation();
                if (axis == 1) {
                    value = o.firstAngle;
                } else if (axis == 2) {
                    value = o.secondAngle;
                } else if (axis == 3) {
                    value = o.thirdAngle;
                } else {
                    value = o.firstAngle;
                }
            }

            return new CknData(value, CknUtil.getCurrentTime());
        }

    }

    private class Accelerometer extends CknAccelerometer {

        public Accelerometer(Parameters params){
            super(params);
        }

        public CknData<Double> getRawData(int axis, DataType dataType){
            double value = 0.0;

            if(dataType == DataType.ACCELERATION){
                Acceleration accel = imu.getAcceleration();
                switch(axis){
                    case 1:
                        value = accel.xAccel;
                        break;
                    case 2:
                        value = accel.yAccel;
                        break;
                    case 3:
                        value = accel.zAccel;
                        break;
                }
            } else if(dataType == DataType.VELOCITY){
                Velocity v = imu.getVelocity();
                switch(axis) {
                    case 1:
                        value = v.xVeloc;
                        break;
                    case 2:
                        value = v.yVeloc;
                        break;
                    case 3:
                        value = v.zVeloc;
                        break;
                }
            }
            return new CknData(value, CknUtil.getCurrentTime());
        }

        public CknData<Double> getRawXAccel(){
            return getRawData(1, DataType.ACCELERATION);
        }

        public CknData<Double> getRawYAccel(){
            return getRawData(2, DataType.ACCELERATION);
        }

        public CknData<Double> getRawZAccel(){
            return getRawData(3, DataType.ACCELERATION);
        }
    }

}