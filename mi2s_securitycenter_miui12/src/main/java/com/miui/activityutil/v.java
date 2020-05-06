package com.miui.activityutil;

public final class v {
    private static int a(String str, int i) {
        try {
            Class[] clsArr = {String.class, Integer.TYPE};
            Class<?> cls = Class.forName("android.os.SystemProperties");
            Class cls2 = Integer.TYPE;
            return ((Integer) q.a((Class) cls, "getInt", clsArr, str, Integer.valueOf(i))).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return i;
        }
    }

    private static Class a() {
        return Class.forName("android.os.SystemProperties");
    }

    public static String a(String str) {
        try {
            Class[] clsArr = {String.class};
            Class<String> cls = String.class;
            return (String) q.a((Class) Class.forName("android.os.SystemProperties"), "get", clsArr, str);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String a(String str, String str2) {
        try {
            Class[] clsArr = {String.class, String.class};
            Class<String> cls = String.class;
            return (String) q.a((Class) Class.forName("android.os.SystemProperties"), "get", clsArr, str, str2);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static boolean a(String str, boolean z) {
        try {
            Class[] clsArr = {String.class, Boolean.TYPE};
            Class<?> cls = Class.forName("android.os.SystemProperties");
            Class cls2 = Boolean.TYPE;
            return ((Boolean) q.a((Class) cls, "getBoolean", clsArr, str, Boolean.valueOf(z))).booleanValue();
        } catch (Exception e) {
            e.printStackTrace();
            return z;
        }
    }

    public static long b(String str) {
        try {
            Class[] clsArr = {String.class, Long.TYPE};
            Class<?> cls = Class.forName("android.os.SystemProperties");
            Class cls2 = Long.TYPE;
            return ((Long) q.a((Class) cls, "getLong", clsArr, str, 0L)).longValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private static void b(String str, String str2) {
        try {
            Class[] clsArr = {String.class, String.class};
            Class<String> cls = String.class;
            q.a((Class) Class.forName("android.os.SystemProperties"), "set", clsArr, str, str2);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
