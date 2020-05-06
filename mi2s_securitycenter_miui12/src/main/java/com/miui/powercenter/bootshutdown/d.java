package com.miui.powercenter.bootshutdown;

import android.view.View;
import b.b.c.j.A;
import com.miui.securitycenter.R;

class d implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PowerShutdownOnTime f6950a;

    d(PowerShutdownOnTime powerShutdownOnTime) {
        this.f6950a = powerShutdownOnTime;
    }

    public void onClick(View view) {
        if (view.equals(this.f6950a.f6937a)) {
            if (!this.f6950a.f6939c.c()) {
                A.a(this.f6950a.getBaseContext(), (int) R.string.prompt_input_time_illegal);
                return;
            } else {
                this.f6950a.f6939c.k();
                this.f6950a.f6939c.a();
            }
        } else if (!view.equals(this.f6950a.f6938b)) {
            return;
        }
        this.f6950a.finish();
    }
}
