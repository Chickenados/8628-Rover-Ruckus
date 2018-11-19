package chickenlib.location;

public class CknLocation {

    public double x, y, z, heading;

    public CknLocation(double x, double y, double z, double heading){
        this.x = x;
        this.y = y;
        this.z = z;
        this.heading = heading;
    }

    public CknLocation(){
        this(0.0, 0.0, 0.0, 0.0);
    }

}
