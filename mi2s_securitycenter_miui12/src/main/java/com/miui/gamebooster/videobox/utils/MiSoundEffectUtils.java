package com.miui.gamebooster.videobox.utils;

import android.util.Log;
import b.b.c.j.y;
import b.b.o.g.c;
import b.b.o.g.e;
import com.miui.securitycenter.Application;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class MiSoundEffectUtils {

    /* renamed from: a  reason: collision with root package name */
    private static Object f5196a;

    /* renamed from: b  reason: collision with root package name */
    private static volatile boolean f5197b;

    /* renamed from: c  reason: collision with root package name */
    private static final Object f5198c = new Object();

    @Retention(RetentionPolicy.SOURCE)
    public @interface MovieSurroundLevel {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface MovieVocalLevel {
    }

    public static void a(int i) {
        Log.i("MiSoundEffectUtils", "setMovieSurroundLevel: " + i);
        try {
            if (b()) {
                d();
                if (f5196a != null) {
                    e.b(f5196a, "setMovieSurroundLevel", new Class[]{Integer.TYPE}, Integer.valueOf(i));
                }
            }
        } catch (Exception e) {
            Log.e("MiSoundEffectUtils", "setMovieSurroundLevel: " + e.toString());
        }
    }

    public static void a(boolean z) {
        Log.i("MiSoundEffectUtils", "setMovieModeEnable: " + z);
        try {
            if (c() || b()) {
                d();
                if (f5196a != null) {
                    Object obj = f5196a;
                    int i = 1;
                    Class[] clsArr = {Integer.TYPE};
                    Object[] objArr = new Object[1];
                    if (!z) {
                        i = 0;
                    }
                    objArr[0] = Integer.valueOf(i);
                    e.b(obj, "setMovieModeEnable", clsArr, objArr);
                }
            }
        } catch (Exception e) {
            Log.e("MiSoundEffectUtils", "setMovieModeEnable: " + e.toString());
        }
    }

    public static boolean a() {
        return y.a("ro.vendor.audio.spk.stereo", false);
    }

    public static void b(int i) {
        Log.i("MiSoundEffectUtils", "setMovieVocalLevel: " + i);
        try {
            if (c()) {
                d();
                if (f5196a != null) {
                    e.b(f5196a, "setMovieVocalLevel", new Class[]{Integer.TYPE}, Integer.valueOf(i));
                }
            }
        } catch (Exception e) {
            Log.e("MiSoundEffectUtils", "setMovieVocalLevel: " + e.toString());
        }
    }

    public static boolean b() {
        if (y.a("ro.vendor.audio.surround.support", false)) {
            return a() || d.a() || d.a(Application.d());
        }
        return false;
    }

    public static boolean c() {
        return y.a("ro.vendor.audio.vocal.support", false);
    }

    private static synchronized void d() {
        synchronized (MiSoundEffectUtils.class) {
            Log.i("MiSoundEffectUtils", "ensureInit: " + f5196a);
            if (!f5197b) {
                synchronized (f5198c) {
                    try {
                        f5196a = c.a(Class.forName("android.media.audiofx.MiSound"), (Class<?>[]) new Class[]{Integer.TYPE, Integer.TYPE}, 0, 0);
                    } catch (Exception e) {
                        Log.e("MiSoundEffectUtils", "ensureInit: " + e.toString());
                    }
                    f5197b = true;
                }
            }
        }
    }
}
