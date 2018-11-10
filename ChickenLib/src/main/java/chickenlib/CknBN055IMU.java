package chickenlib;

import com.qualcomm.hardware.bosch.BNO055IMU;

public class CknBN055IMU{

    BNO055IMU imu;

    public CknBN055IMU(BNO055IMU imu, BNO055IMU.Parameters params){
        this.imu = imu;

        imu.initialize(params);

    }

    public class Gyro extends CknGyro {

        public Gyro(int numAxes){
            super(numAxes);
        }

        public CknData getData(int axis){
            if(axis == 1){
                
            } else if(axis == 2){

            } else if(axis == 3){

            }
            return null;
        }

    }

    public class Accelerometer extends CknAccelerometer {

    }

}
