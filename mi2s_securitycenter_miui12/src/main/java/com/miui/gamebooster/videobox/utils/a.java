package com.miui.gamebooster.videobox.utils;

import android.util.Log;
import b.b.c.j.y;
import b.b.o.g.e;
import com.miui.gamebooster.m.C0388t;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static Object f5199a;

    public static int a() {
        return y.a("vendor.video.mode.status", 0);
    }

    public static void a(int i) {
        try {
            c();
            Log.i("DisplayEffectUtils", "setScreenEffect: " + i);
            if (f5199a == null) {
                Log.e("DisplayEffectUtils", "setScreenEffect failed DFM=null");
                return;
            }
            if (i <= 4) {
                if (i >= 0) {
                    e.a(f5199a, "setScreenEffect", (Class<?>[]) new Class[]{Integer.TYPE, Integer.TYPE}, 27, Integer.valueOf(i));
                    return;
                }
            }
            Log.e("DisplayEffectUtils", "setScreenEffect failed invalidate value = " + i);
        } catch (Exception e) {
            Log.e("DisplayEffectUtils", "setScreenEffect: " + e.toString());
        }
    }

    public static boolean b() {
        boolean z = false;
        try {
            z = C0388t.a("miui.util.FeatureParser", "support_videobox_display_effect", false);
            Log.i("DisplayEffectUtils", "isSupportVDS: " + z);
            return z;
        } catch (Exception e) {
            Log.e("DisplayEffectUtils", "isSupportVDS Failed", e);
            return z;
        }
    }

    private static synchronized void c() {
        synchronized (a.class) {
            if (f5199a == null) {
                try {
                    f5199a = e.a(Class.forName("miui.hardware.display.DisplayFeatureManager"), "getInstance", (Class<?>[]) null, new Object[0]);
                } catch (Exception e) {
                    Log.i("DisplayEffectUtils", "ensureinit: " + e.toString());
                }
            } else {
                return;
            }
        }
        return;
    }
}
