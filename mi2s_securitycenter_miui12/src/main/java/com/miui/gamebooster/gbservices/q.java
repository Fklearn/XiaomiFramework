package com.miui.gamebooster.gbservices;

import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import com.miui.gamebooster.c.a;
import com.miui.gamebooster.m.C0384o;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.m.Y;
import com.miui.gamebooster.service.r;
import com.miui.securitycenter.p;

public class q extends m {

    /* renamed from: a  reason: collision with root package name */
    private boolean f4373a;

    /* renamed from: b  reason: collision with root package name */
    private Context f4374b;

    /* renamed from: c  reason: collision with root package name */
    private r f4375c;

    /* renamed from: d  reason: collision with root package name */
    private String f4376d;
    private String e;
    private int f;

    public q(Context context, r rVar) {
        this.f4374b = context;
        this.f4375c = rVar;
        if (C0384o.b("android.provider.MiuiSettings$ScreenEffect", "GAME_MODE") != null) {
            this.e = (String) C0384o.b("android.provider.MiuiSettings$ScreenEffect", "GAME_MODE");
        }
        if (C0384o.b("android.provider.MiuiSettings$Key", "ENABLE_THREE_GESTURE_KEY") != null) {
            this.f4376d = (String) C0384o.b("android.provider.MiuiSettings$Key", "ENABLE_THREE_GESTURE_KEY");
        }
        if (C0384o.b("android.provider.MiuiSettings$ScreenEffect", "GAME_MODE_DISABLE_EYECARE") != null) {
            this.f = ((Integer) C0384o.b("android.provider.MiuiSettings$ScreenEffect", "GAME_MODE_DISABLE_EYECARE")).intValue();
        }
    }

    public void a() {
        if (this.f4373a) {
            if (a.r(false)) {
                Log.i("GameBoosterService", "misShieldAutoBright...stop ");
                Y.a(this.f4374b, false);
            }
            if (a.s(false) && !C0388t.h() && C0388t.e()) {
                Log.i("GameBoosterService", "misShieldEyeShield...stop ");
                Settings.System.putInt(this.f4374b.getContentResolver(), this.e, 0);
                C0384o.d(this.f4374b.getContentResolver(), this.e, 0, 0);
            }
            if (a.u(false) && this.f4376d != null && (C0388t.e() || p.a() >= 12)) {
                Log.i("GameBoosterService", "misShieldThreeFinger...stop ");
                C0384o.d(this.f4374b.getContentResolver(), this.f4376d, 1, 0);
            }
            if (a.g(false)) {
                Log.i("GameBoosterService", "misDisableVoiceTrigger...stop ");
                C0384o.b(this.f4374b.getContentResolver(), "disable_voicetrigger", 0, -2);
            }
        }
    }

    public boolean b() {
        return true;
    }

    public void c() {
        if (this.f4373a) {
            if (a.r(false)) {
                Log.i("GameBoosterService", "misShieldAutoBright...start ");
                Y.a(this.f4374b, true);
            }
            if (a.s(false) && !C0388t.h() && C0388t.e()) {
                Log.i("GameBoosterService", "misShieldEyeShield...start ");
                ContentResolver contentResolver = this.f4374b.getContentResolver();
                String str = this.e;
                int i = this.f;
                Settings.System.putInt(contentResolver, str, i | i);
                ContentResolver contentResolver2 = this.f4374b.getContentResolver();
                String str2 = this.e;
                int i2 = this.f;
                C0384o.d(contentResolver2, str2, i2 | i2, 0);
            }
            if (a.u(false) && this.f4376d != null && (C0388t.e() || p.a() >= 12)) {
                Log.i("GameBoosterService", "misShieldThreeFinger...start ");
                C0384o.d(this.f4374b.getContentResolver(), this.f4376d, 0, 0);
            }
            if (a.g(false)) {
                Log.i("GameBoosterService", "misDisableVoiceTrigger...start ");
                C0384o.b(this.f4374b.getContentResolver(), "disable_voicetrigger", 1, -2);
            }
        }
    }

    public void d() {
        this.f4373a = true;
    }

    public int e() {
        return 2;
    }
}
