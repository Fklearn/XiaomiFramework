package com.miui.gamebooster.m;

import android.util.Log;
import b.b.o.g.e;

/* renamed from: com.miui.gamebooster.m.a  reason: case insensitive filesystem */
public class C0370a {

    /* renamed from: a  reason: collision with root package name */
    public static int f4468a = -1;

    /* renamed from: b  reason: collision with root package name */
    public static int f4469b = -1;

    /* renamed from: c  reason: collision with root package name */
    public static int f4470c = -1;

    /* renamed from: d  reason: collision with root package name */
    public static int f4471d = -1;
    public static int e = -1;
    private static volatile C0370a f;

    public C0370a() {
        f4468a = a("TOUCH_GAME_MODE");
        f4469b = a("TOUCH_ACTIVE_MODE");
        f4470c = a("TOUCH_TOLERANCE");
        f4471d = a("TOUCH_UP_THRESHOLD");
        e = a("TOUCH_EDGE_FILTER");
        Log.i("CustomizedService", "TOUCH_GAME_MODE:" + f4468a + " TOUCH_ACTIVE_MODE:" + f4469b + " TOUCH_TOLERANCE:" + f4470c + " TOUCH_UP_THRESHOLD:" + f4471d + " TOUCH_EDGE_FILTER:" + e);
    }

    private int a(String str) {
        try {
            return ((Integer) e.a(Class.forName("miui.util.ITouchFeature"), str, Integer.TYPE)).intValue();
        } catch (Exception e2) {
            Log.i("GameBoosterReflectUtils", e2.toString());
            return -1;
        }
    }

    public static C0370a a() {
        if (f == null) {
            synchronized (C0370a.class) {
                if (f == null) {
                    f = new C0370a();
                }
            }
        }
        return f;
    }

    public int a(int i) {
        try {
            Object a2 = e.a(Class.forName("miui.util.ITouchFeature"), "getInstance", (Class<?>[]) null, new Object[0]);
            if (a2 == null) {
                return -1;
            }
            return ((Integer) e.a(a2, "getTouchModeDefValue", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(i))).intValue();
        } catch (Exception e2) {
            Log.i("GameBoosterReflectUtils", e2.toString());
            return -1;
        }
    }

    public boolean a(int i, int i2) {
        try {
            Log.i("CustomizedService", "mode:" + i + " value" + i2);
            Object a2 = e.a(Class.forName("miui.util.ITouchFeature"), "getInstance", (Class<?>[]) null, new Object[0]);
            if (a2 != null) {
                return ((Boolean) e.a(a2, "setTouchMode", (Class<?>[]) new Class[]{Integer.TYPE, Integer.TYPE}, Integer.valueOf(i), Integer.valueOf(i2))).booleanValue();
            }
        } catch (Exception e2) {
            Log.i("GameBoosterReflectUtils", e2.toString());
        }
        return false;
    }

    public int b(int i) {
        try {
            Object a2 = e.a(Class.forName("miui.util.ITouchFeature"), "getInstance", (Class<?>[]) null, new Object[0]);
            if (a2 == null) {
                return -1;
            }
            return ((Integer) e.a(a2, "getTouchModeMaxValue", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(i))).intValue();
        } catch (Exception e2) {
            Log.i("GameBoosterReflectUtils", e2.toString());
            return -1;
        }
    }

    public int c(int i) {
        try {
            Object a2 = e.a(Class.forName("miui.util.ITouchFeature"), "getInstance", (Class<?>[]) null, new Object[0]);
            if (a2 == null) {
                return -1;
            }
            return ((Integer) e.a(a2, "getTouchModeMinValue", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(i))).intValue();
        } catch (Exception e2) {
            Log.i("GameBoosterReflectUtils", e2.toString());
            return -1;
        }
    }

    public boolean d(int i) {
        try {
            Object a2 = e.a(Class.forName("miui.util.ITouchFeature"), "getInstance", (Class<?>[]) null, new Object[0]);
            if (a2 != null) {
                return ((Boolean) e.a(a2, "resetTouchMode", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(i))).booleanValue();
            }
        } catch (Exception e2) {
            Log.i("GameBoosterReflectUtils", e2.toString());
        }
        return false;
    }
}
