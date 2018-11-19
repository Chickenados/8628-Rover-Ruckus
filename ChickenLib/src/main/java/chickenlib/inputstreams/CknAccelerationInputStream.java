package chickenlib.inputstreams;

import chickenlib.location.CknAcceleration;
import chickenlib.sensor.CknAccelerometer;

public class CknAccelerationInputStream extends CknInputStream<CknAcceleration>{

    CknAccelerometer accelerometer;

    public CknAccelerationInputStream(CknAccelerometer accel){
        this.accelerometer = accel;
    }

    @Override
    public CknAcceleration getInput() {
        return null;
    }
}
