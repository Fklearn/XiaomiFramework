package b.b.c.j;

import b.b.o.g.e;
import miui.util.Log;

public class y {
    public static int a(String str, int i) {
        try {
            return ((Integer) e.a(Class.forName("android.os.SystemProperties"), Integer.TYPE, "getInt", (Class<?>[]) new Class[]{String.class, Integer.TYPE}, str, Integer.valueOf(i))).intValue();
        } catch (Exception e) {
            Log.e("SystemPropertiesUtils", "SystemPropertiesUtils getInt: ", e);
            return 0;
        }
    }

    public static String a(String str, String str2) {
        try {
            return (String) e.a(Class.forName("android.os.SystemProperties"), String.class, "get", (Class<?>[]) new Class[]{String.class, String.class}, str, str2);
        } catch (Exception e) {
            Log.e("SystemPropertiesUtils", "SystemPropertiesUtils getString: ", e);
            return "";
        }
    }

    public static boolean a(String str, boolean z) {
        try {
            return ((Boolean) e.a(Class.forName("android.os.SystemProperties"), Boolean.TYPE, "getBoolean", (Class<?>[]) new Class[]{String.class, Boolean.TYPE}, str, Boolean.valueOf(z))).booleanValue();
        } catch (Exception e) {
            Log.e("SystemPropertiesUtils", "SystemPropertiesUtils getInt: ", e);
            return false;
        }
    }

    public static void b(String str, String str2) {
        try {
            e.a(Class.forName("android.os.SystemProperties"), (Class) null, "set", (Class<?>[]) new Class[]{String.class, String.class}, str, str2);
        } catch (Exception e) {
            Log.e("SystemPropertiesUtils", "SystemPropertiesUtils getInt: ", e);
        }
    }
}
