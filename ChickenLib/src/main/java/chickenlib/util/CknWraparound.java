package chickenlib.util;

public class CknWraparound {

    //This class handles a measurement that has "wraparound" like angles

    public static double getTarget(double minPoint, double maxPoint, double inputValue, double target){
        double range = maxPoint - minPoint;
        double targetDistance = (target - inputValue) % range;

        if(targetDistance > range / 2){
            targetDistance = Math.signum(targetDistance) * (range - Math.abs(targetDistance));
        }

        return targetDistance;
    }
}
