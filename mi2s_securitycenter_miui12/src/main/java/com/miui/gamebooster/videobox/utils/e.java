package com.miui.gamebooster.videobox.utils;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import b.b.i.b.c;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.mutiwindow.f;
import com.miui.gamebooster.n.b.b;
import com.miui.powercenter.utils.o;
import com.miui.securitycenter.Application;

public class e {

    /* renamed from: a  reason: collision with root package name */
    private static boolean f5202a = true;

    /* renamed from: b  reason: collision with root package name */
    private static volatile boolean f5203b;

    /* renamed from: c  reason: collision with root package name */
    private static final Object f5204c = new Object();

    public static void a(Context context) {
        Log.i("VideoBoxUtils", "exitVideoBoxMode");
        if (b()) {
            MiSoundEffectUtils.a(false);
            f.b(false);
            a.a(0);
        }
        b.a();
        f5203b = false;
    }

    public static boolean a() {
        return (d() || f.d()) && !o.m(Application.d());
    }

    public static void b(Context context) {
        int b2;
        Log.i("VideoBoxUtils", "startVideoBoxMode");
        if (a.b() && (b2 = com.miui.gamebooster.videobox.settings.f.b()) != 0) {
            a.a(b2);
        }
        if (MiSoundEffectUtils.b() || MiSoundEffectUtils.c()) {
            MiSoundEffectUtils.a(true);
            MiSoundEffectUtils.a(com.miui.gamebooster.videobox.settings.f.c());
            MiSoundEffectUtils.b(com.miui.gamebooster.videobox.settings.f.d());
        }
        String a2 = com.miui.gamebooster.videobox.settings.f.a();
        if (f.a() && f.a(a2) && com.miui.gamebooster.videobox.settings.f.l()) {
            f.b(true);
        }
    }

    public static boolean b() {
        boolean z;
        if (!f5203b) {
            synchronized (f5204c) {
                if (!f.a() && !a.b() && !MiSoundEffectUtils.b()) {
                    if (!MiSoundEffectUtils.c()) {
                        z = false;
                        f5202a = z;
                    }
                }
                z = true;
                f5202a = z;
            }
        }
        return f5202a;
    }

    public static boolean c() {
        return Build.VERSION.SDK_INT >= 29 && c.b();
    }

    private static boolean d() {
        boolean z = false;
        try {
            z = C0388t.a("miui.util.FeatureParser", "is_support_video_tool_box", false);
            Log.i("VideoBoxUtils", "isSupportVideoBox: " + z);
            return z;
        } catch (Exception e) {
            Log.e("VideoBoxUtils", "isDeviceSupport Failed", e);
            return z;
        }
    }
}
