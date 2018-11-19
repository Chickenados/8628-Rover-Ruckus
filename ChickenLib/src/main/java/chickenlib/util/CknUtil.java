package chickenlib.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CknUtil {

    /**
     * Returns system time in seconds up to the nanosecond.
     * @return Time in seconds
     */
    public static double getCurrentTime(){
        return System.nanoTime()/1000000000.0; // Billionth of a second
    }

    public static double round(double value, int places){
        if(places < 0) throw new IllegalArgumentException("Attempted to round to illegal decimal places!");

        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static class CknLoopCounter{

        private static CknLoopCounter instance;

        public static CknLoopCounter getInstance() {
            if(instance == null){
                instance = new CknLoopCounter();
            }
            return instance;
        }

        public long loop;

        public long getLoopCount(){ return loop; }
    }
}
