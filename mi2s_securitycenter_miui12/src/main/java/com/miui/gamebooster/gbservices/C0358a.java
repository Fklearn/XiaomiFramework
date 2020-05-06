package com.miui.gamebooster.gbservices;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import com.miui.gamebooster.c.a;
import com.miui.gamebooster.m.C0384o;
import com.miui.gamebooster.service.r;

/* renamed from: com.miui.gamebooster.gbservices.a  reason: case insensitive filesystem */
public class C0358a extends m {

    /* renamed from: a  reason: collision with root package name */
    private boolean f4344a;

    /* renamed from: b  reason: collision with root package name */
    private Context f4345b;

    /* renamed from: c  reason: collision with root package name */
    private r f4346c;

    public C0358a(Context context, r rVar) {
        this.f4345b = context;
        this.f4346c = rVar;
    }

    private void a(boolean z) {
        String str = (String) C0384o.b("android.provider.SystemSettings$Secure", "SCREEN_BUTTONS_STATE");
        if (str != null) {
            Settings.Secure.putInt(this.f4345b.getContentResolver(), str, z ? 1 : 0);
        }
    }

    public void a() {
        if (this.f4344a) {
            Log.i("GameBoosterService", "mIsAntiKeyboard...stop");
            a(false);
        }
    }

    public boolean b() {
        return true;
    }

    public void c() {
        if (this.f4344a) {
            Log.i("GameBoosterService", "mIsAntiKeyboard...start ");
            a(true);
        }
    }

    public void d() {
        a.a(this.f4345b);
        this.f4344a = a.d(true);
    }

    public int e() {
        return 0;
    }
}
