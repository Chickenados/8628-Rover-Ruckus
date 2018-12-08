package chickenlib.util;

public class CknWraparound {

    //This class handles a measurement that has "wraparound" like angles

    private double minPoint, maxPoint, range;

    public CknWraparound(double minPoint, double maxPoint){
        this.minPoint = minPoint;
        this.maxPoint = maxPoint;
        this.range = maxPoint - minPoint;
    }

    public double getTarget(double inputValue, double target){
        double targetDistance = (target - inputValue) % range;

        if(targetDistance > range / 2){

        }

        return targetDistance;
    }
}
