package chickenlib.util;

public class CknWraparound {

    //This class handles a measurement that has "wraparound" like angles

    public static double getTarget(double minPoint, double maxPoint, double inputValue, double target){
        double range = maxPoint - minPoint;
        double distance = (target - inputValue) % range;
        double absDistance = Math.abs(distance);

        return inputValue + ((absDistance > (range / 2)) ? -Math.signum(distance) * (range - absDistance) : distance);
    }
}
