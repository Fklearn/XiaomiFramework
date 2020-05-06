package com.miui.gamebooster.n.d;

import android.util.Log;
import android.view.View;
import com.miui.gamebooster.videobox.utils.f;
import com.miui.securitycenter.R;

public class a extends b {

    /* renamed from: c  reason: collision with root package name */
    private int f4685c;

    /* renamed from: d  reason: collision with root package name */
    private int f4686d;
    private boolean e;
    private int f;

    public a(int i, int i2, int i3, int i4) {
        super(i);
        this.f = i4;
        this.f4685c = i2;
        this.f4686d = i3;
    }

    public void a(View view) {
        if (this.f == 9) {
            f.b(this.e);
            com.miui.gamebooster.videobox.settings.f.g(this.e);
        }
    }

    public void a(boolean z) {
        this.e = z;
    }

    public boolean b() {
        Log.i("AdvancedSettingsModel", "isSupport: func=" + this.f + "\tvpp=" + f.a());
        return this.f == 9 && f.a() && f.a(com.miui.gamebooster.videobox.settings.f.a());
    }

    public int c() {
        return this.f;
    }

    public int d() {
        return R.drawable.vtb_video_effect_vpp_after_cn;
    }

    public int e() {
        return R.drawable.vtb_video_effect_vpp_before_cn;
    }

    public boolean f() {
        return this.e;
    }

    public boolean g() {
        return com.miui.gamebooster.videobox.settings.f.l();
    }
}
