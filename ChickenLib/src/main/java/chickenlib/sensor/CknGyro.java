package chickenlib.sensor;

import chickenlib.util.CknData;

public abstract class CknGyro extends CknSensor<CknGyro.DataType>{

    public enum DataType {
        HEADING;
    }

    public CknGyro(){
        super(3);
    }

    public abstract CknData<Double> getRawXHeading();

    public abstract CknData<Double> getRawYHeading();

    public abstract CknData<Double> getRawZHeading();

    public CknData<Double> getXHeading(){
        return getData(0, DataType.HEADING);
    }

    public CknData<Double> getYHeading(){
        return getData(1, DataType.HEADING);
    }

    public CknData<Double> getZHeading(){
        return getData(2, DataType.HEADING);
    }

}
