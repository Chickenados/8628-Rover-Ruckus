package chickenlib;

public class CknUtil {

    /**
     * Returns system time in seconds up to the nanosecond.
     * @return Time in seconds
     */
    public static double getCurrentTime(){
        return System.nanoTime()/1000000000.0; // Billionth of a second
    }
}
