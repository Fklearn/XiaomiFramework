package miui.cloud.util;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import com.miui.activityutil.o;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import miui.cloud.os.SystemProperties;

public class AnonymousDeviceIdUtil {
    private static final String TAG = "AnonymousDeviceIdUtil";
    private static Method sGetAAID;
    private static Method sGetOAID;
    private static Method sGetUDID;
    private static Method sGetVAID;
    private static Object sIdProivderImpl;

    static {
        try {
            Class<?> cls = Class.forName("com.android.id.impl.IdProviderImpl");
            sIdProivderImpl = cls.newInstance();
            try {
                sGetUDID = cls.getMethod("getUDID", new Class[]{Context.class});
            } catch (NoSuchMethodException e) {
                Log.e(TAG, "getUDID not avaliable", e);
            }
            try {
                sGetOAID = cls.getMethod("getOAID", new Class[]{Context.class});
            } catch (NoSuchMethodException e2) {
                Log.e(TAG, "getOAID not avaliable", e2);
            }
            try {
                sGetVAID = cls.getMethod("getVAID", new Class[]{Context.class});
            } catch (NoSuchMethodException e3) {
                Log.e(TAG, "getVAID not avaliable", e3);
            }
            try {
                sGetAAID = cls.getMethod("getAAID", new Class[]{Context.class});
            } catch (NoSuchMethodException e4) {
                Log.e(TAG, "getAAID not avaliable", e4);
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e5) {
            Log.e(TAG, "provider not avaliable", e5);
        }
    }

    private AnonymousDeviceIdUtil() {
    }

    public static String getAAID(Context context) {
        return getId(context, sGetAAID);
    }

    public static String getAndroidId(Context context) {
        Log.i(TAG, "android id");
        return Settings.Secure.getString(context.getContentResolver(), "android_id");
    }

    private static String getId(Context context, Method method) {
        StringBuilder sb;
        Object obj = sIdProivderImpl;
        if (obj == null || method == null) {
            return null;
        }
        try {
            return (String) method.invoke(obj, new Object[]{context});
        } catch (IllegalAccessException e) {
            e = e;
            sb = new StringBuilder();
            sb.append("exception invoking ");
            sb.append(method);
            Log.e(TAG, sb.toString(), e);
            return null;
        } catch (InvocationTargetException e2) {
            e = e2;
            sb = new StringBuilder();
            sb.append("exception invoking ");
            sb.append(method);
            Log.e(TAG, sb.toString(), e);
            return null;
        }
    }

    public static String getOAID(Context context) {
        return getId(context, sGetOAID);
    }

    public static String getUDID(Context context) {
        return getId(context, sGetUDID);
    }

    public static String getVAID(Context context) {
        return getId(context, sGetVAID);
    }

    public static boolean isEnforced(Context context) {
        if (Build.VERSION.SDK_INT < 29) {
            Log.i(TAG, "not enforced");
            return false;
        } else if (o.f2310b.equals(SystemProperties.get("ro.miui.restrict_imei"))) {
            Log.i(TAG, "enforced");
            return true;
        } else {
            Log.i(TAG, "not enforced");
            return false;
        }
    }

    public static boolean isSupported(Context context) {
        return sIdProivderImpl != null;
    }
}
