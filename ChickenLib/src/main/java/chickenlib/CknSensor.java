package chickenlib;

public abstract class CknSensor {

    private int numAxes;

    public CknSensor(int numAxes){
        this.numAxes = numAxes;
    }

    public int getNumAxes(){
        return numAxes;
    }

    public abstract CknData getData(int axis);
}
