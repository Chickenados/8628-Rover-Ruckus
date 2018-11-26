package chickenlib.logging;

import android.util.Log;

public class CknDbgLog {

    public enum Priority {
        ASSERT(7),
        ERROR(6),
        WARN(5),
        INFO(4),
        DEBUG(3),
        VERBOSE(2);

        int value;

        Priority(int id){
            this.value = id;
        }
    }

    public static final String TAG = "CknDbg";

    public static void msg(Priority p, String message){
        switch(p) {
            case ASSERT:
                Log.wtf(TAG, message);
                break;
            case ERROR:
                Log.e(TAG, message);
                break;
            case WARN:
                Log.w(TAG, message);
                break;
            case INFO:
                Log.i(TAG, message);
                break;
            case DEBUG:
                Log.d(TAG, message);
                break;
            case VERBOSE:
                Log.v(TAG, message);
                break;
        }
    }
}
