package chickenlib;

import com.qualcomm.hardware.bosch.BNO055IMU;

public class CknBN055IMU{

    BNO055IMU imu;

    public CknBN055IMU(BNO055IMU imu){
        this.imu = imu;
    }

    public class Gyro extends CknGyro {

        public CknData getData(int axis){
            if(axis == 1){
                
            } else if(axis == 2){

            } else if(axis == 3){

            }
        }

    }

    public class Accelerometer extends CknAccelerometer {

    }

}
