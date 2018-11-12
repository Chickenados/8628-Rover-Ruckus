package chickenlib.sensor;

import chickenlib.util.CknData;

public abstract class CknGyro extends CknSensor<CknGyro.DataType>{

    public enum DataType {
        HEADING;
    }

    public CknGyro(){
        super(3);
    }

}
