package com.miui.gamebooster.gbservices;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import b.b.o.g.e;
import com.miui.common.persistence.b;
import com.miui.gamebooster.c.a;
import com.miui.gamebooster.m.C0388t;
import com.miui.gamebooster.service.r;
import com.miui.luckymoney.utils.SettingsUtil;
import miui.os.Build;

/* renamed from: com.miui.gamebooster.gbservices.l  reason: case insensitive filesystem */
public class C0369l extends m {

    /* renamed from: a  reason: collision with root package name */
    private static final boolean f4360a = Build.IS_INTERNATIONAL_BUILD;

    /* renamed from: b  reason: collision with root package name */
    private boolean f4361b;

    /* renamed from: c  reason: collision with root package name */
    private boolean f4362c;

    /* renamed from: d  reason: collision with root package name */
    private Context f4363d;
    private Object e;
    private r f;
    private boolean g = false;
    private int h;
    private int i;
    private int j;

    public C0369l(Context context, r rVar) {
        this.f4363d = context;
        this.f = rVar;
        this.e = context.getSystemService("statusbar");
        try {
            this.j = ((Integer) e.a(Class.forName("android.app.MiuiStatusBarManager"), "DISABLE_FLAG_FLOAT_NOTIFICATION")).intValue();
            this.h = ((Integer) e.a(Class.forName("android.app.StatusBarManager"), "DISABLE_NONE")).intValue();
            this.i = ((Integer) e.a(Class.forName("android.app.StatusBarManager"), "DISABLE_EXPAND")).intValue();
        } catch (Exception e2) {
            Log.i("GameBoosterReflectUtils", e2.toString());
        }
    }

    private void a(int i2) {
        try {
            e.a(this.e, "disable", (Class<?>[]) new Class[]{Integer.TYPE}, Integer.valueOf(i2));
        } catch (Exception e2) {
            Log.i("GameBoosterReflectUtils", e2.toString());
        }
    }

    public void a() {
        this.g = false;
        if (this.f4361b) {
            Log.i("GameBoosterService", "mIsAntiMsg...stop");
        }
        if (this.f4362c) {
            Log.i("GameBoosterService", "misShieldPullNotificationBar...stop");
        }
        if (this.f4361b || this.f4362c) {
            a(this.h);
        }
        if (!f4360a) {
            this.f.b(false);
        }
        Settings.Secure.putInt(this.f4363d.getContentResolver(), "gb_notification", 0);
        b.b("game_IsAntiMsg", false);
        if (!f4360a) {
            this.f.b(false);
        }
        SettingsUtil.closeAccessibility(this.f4363d, AntiMsgAccessibilityService.class);
    }

    public boolean b() {
        return true;
    }

    public void c() {
        this.g = true;
        int i2 = this.h;
        if (this.f4361b) {
            i2 |= this.j;
            Log.i("GameBoosterService", "mIsAntiMsg...start ");
        }
        if (this.f4362c) {
            i2 |= this.i;
            Log.i("GameBoosterService", "misShieldPullNotificationBar...start ");
        }
        if (this.f4361b || this.f4362c) {
            a(i2);
        } else {
            Settings.Secure.putInt(this.f4363d.getContentResolver(), "gb_notification", 1);
        }
        b.b("game_IsAntiMsg", true);
        if (!f4360a) {
            this.f.b(true);
        }
        SettingsUtil.enableAccessibility(this.f4363d, AntiMsgAccessibilityService.class);
    }

    public void d() {
        boolean z;
        if (C0388t.m()) {
            a.a(this.f4363d);
            z = a.b(false);
        } else {
            a.a(this.f4363d);
            z = a.c(false);
        }
        this.f4361b = z;
        this.f4362c = a.t(false);
    }

    public int e() {
        return 1;
    }

    public boolean f() {
        return this.g;
    }
}
