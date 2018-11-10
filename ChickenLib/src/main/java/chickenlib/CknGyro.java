package chickenlib;

public abstract class CknGyro extends CknSensor{

    public CknGyro(int numAxes){
        super(numAxes);
    }

    public abstract CknData getData(int axis);
}
