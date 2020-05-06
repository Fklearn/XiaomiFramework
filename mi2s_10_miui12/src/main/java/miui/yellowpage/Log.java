package miui.yellowpage;

import java.util.Arrays;
import miui.os.Build;

public class Log {
    private static final boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final String TAG = "YellowPage";

    private Log() {
    }

    public static void d(String tag, String msg) {
        if (DEBUG) {
            android.util.Log.d(TAG, tag + ":" + msg);
        }
    }

    public static void d(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            android.util.Log.d(TAG, tag + ":" + msg, tr);
        }
    }

    public static void e(String tag, String msg) {
        android.util.Log.e(TAG, tag + ":" + msg);
    }

    public static void e(String tag, String msg, Throwable tr) {
        android.util.Log.e(TAG, tag + ":" + msg, tr);
    }

    public static void v(String tag, String msg) {
        android.util.Log.v(TAG, tag + ":" + msg);
    }

    public static void v(String tag, String msg, Throwable tr) {
        android.util.Log.v(TAG, tag + ":" + msg, tr);
    }

    public static void i(String tag, String msg) {
        if (DEBUG) {
            android.util.Log.i(TAG, tag + ":" + msg);
        }
    }

    public static void i(String tag, String msg, Throwable tr) {
        if (DEBUG) {
            android.util.Log.i(TAG, tag + ":" + msg, tr);
        }
    }

    public static void wtf(String tag, String msg) {
        android.util.Log.wtf(TAG, tag + ":" + msg);
    }

    public static void wtf(String tag, String msg, Throwable tr) {
        android.util.Log.wtf(TAG, tag + ":" + msg, tr);
    }

    public static String logify(String privacy) {
        if (privacy == null) {
            return null;
        }
        char[] log = new char[privacy.length()];
        Arrays.fill(log, '*');
        return new String(log);
    }
}
