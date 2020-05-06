package b.b.c.j;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.Log;
import miui.os.SystemProperties;

public class e {
    public static int a(Activity activity) {
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getRealSize(point);
        return point.y;
    }

    public static long a() {
        try {
            return ((Long) b.b.o.g.e.a(Class.forName("com.miui.daemon.performance.PerfShielderManager"), "getFreeMemory", (Class<?>[]) null, new Object[0])).longValue();
        } catch (Exception e) {
            Log.e("CommonUtils", "reflect error while get free memory", e);
            return 0;
        }
    }

    public static void a(String str, int i, boolean z) {
        try {
            b.b.o.g.e.a(Class.forName("miui.process.ProcessManager"), "updateApplicationLockedState", (Class<?>[]) new Class[]{String.class, Integer.TYPE, Boolean.TYPE}, str, Integer.valueOf(i), Boolean.valueOf(z));
        } catch (Exception e) {
            Log.e("CommonUtils", "reflect error while update app locked state", e);
        }
    }

    public static boolean a(Context context, String str) {
        try {
            return ((Boolean) b.b.o.g.e.a(Class.forName("com.miui.enterprise.ApplicationHelper"), "shouldKeeAlive", (Class<?>[]) new Class[]{Context.class, String.class}, context, str)).booleanValue();
        } catch (Exception e) {
            Log.e("CommonUtils", "reflect error shoueKeeAlive", e);
            return false;
        }
    }

    public static boolean a(Context context, String str, int i) {
        try {
            return ((Boolean) b.b.o.g.e.a(Class.forName("miui.content.pm.PreloadedAppPolicy"), Boolean.TYPE, "isProtectedDataApp", (Class<?>[]) new Class[]{Context.class, String.class, Integer.TYPE}, context, str, Integer.valueOf(i))).booleanValue();
        } catch (Exception e) {
            Log.e("CommonUtils", "isProtectedDataApp: ", e);
            return false;
        }
    }

    public static boolean a(String str, int i) {
        try {
            return ((Boolean) b.b.o.g.e.a(Class.forName("miui.process.ProcessManager"), "isLockedApplication", (Class<?>[]) new Class[]{String.class, Integer.TYPE}, str, Integer.valueOf(i))).booleanValue();
        } catch (Exception e) {
            Log.e("CommonUtils", "reflect error while get app is locked", e);
            return false;
        }
    }

    public static int b() {
        try {
            String str = SystemProperties.get("ro.miui.ui.version.code");
            if (!TextUtils.isEmpty(str)) {
                return Integer.parseInt(str);
            }
            return 0;
        } catch (Exception unused) {
            return 0;
        }
    }

    public static boolean b(Activity activity) {
        return a(activity) <= 1920;
    }

    public static boolean b(Context context, String str, int i) {
        try {
            return ((Boolean) b.b.o.g.e.a(Class.forName("com.miui.enterprise.ApplicationHelper"), "protectedFromDelete", (Class<?>[]) new Class[]{Context.class, String.class, Integer.TYPE}, context, str, Integer.valueOf(i))).booleanValue();
        } catch (Exception e) {
            Log.e("CommonUtils", "reflect error shoueKeeAlive", e);
            return false;
        }
    }

    public static long c() {
        try {
            return ((Long) b.b.o.g.e.a(Class.forName("miui.util.HardwareInfo"), Long.TYPE, "getTotalPhysicalMemory", (Class<?>[]) null, new Object[0])).longValue();
        } catch (Exception e) {
            Log.d("CommonUtils", "getTotalPhysicalMemory exception ", e);
            return 0;
        }
    }

    public static boolean c(Context context, String str, int i) {
        try {
            return ((Boolean) b.b.o.g.e.a(Class.forName("com.miui.enterprise.ApplicationHelper"), "shouldKeeAlive", (Class<?>[]) new Class[]{Context.class, String.class, Integer.TYPE}, context, str, Integer.valueOf(i))).booleanValue();
        } catch (Exception e) {
            Log.e("CommonUtils", "reflect error shoueKeeAlive", e);
            return false;
        }
    }
}
