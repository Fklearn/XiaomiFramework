package com.miui.gamebooster.n.d;

import android.util.Log;
import android.view.View;
import com.miui.gamebooster.videobox.settings.f;
import com.miui.gamebooster.videobox.utils.MiSoundEffectUtils;

public class d extends b {

    /* renamed from: c  reason: collision with root package name */
    private int f4691c;

    /* renamed from: d  reason: collision with root package name */
    private int f4692d;
    private int e;

    public d(int i, int i2, int i3) {
        super(i);
        this.e = i3;
        this.f4691c = i2;
    }

    public void a(int i) {
        this.f4692d = i;
    }

    public void a(View view) {
        Log.i("SrsSettingsModel", "onClick: funcId=" + this.e + "\tlevel=" + this.f4692d);
        int i = this.e;
        if (i == 6) {
            MiSoundEffectUtils.b(this.f4692d);
            f.c(this.f4692d);
        } else if (i == 7) {
            MiSoundEffectUtils.a(this.f4692d);
            f.b(this.f4692d);
        }
    }

    public boolean b() {
        int i = this.e;
        if (i == 6) {
            return MiSoundEffectUtils.c();
        }
        if (i != 7) {
            return false;
        }
        return MiSoundEffectUtils.b();
    }

    public int c() {
        return this.e;
    }

    public int d() {
        return this.f4691c;
    }

    public int e() {
        return this.f4692d;
    }

    public int f() {
        return this.e == 7 ? f.c() : f.d();
    }
}
