package com.miui.applicationlock;

import android.os.CountDownTimer;
import com.miui.securitycenter.R;

class T extends CountDownTimer {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ConfirmAccessControl f3216a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    T(ConfirmAccessControl confirmAccessControl, long j, long j2) {
        super(j, j2);
        this.f3216a = confirmAccessControl;
    }

    public void onFinish() {
        this.f3216a.q();
    }

    public void onTick(long j) {
        boolean unused = this.f3216a.z = true;
        this.f3216a.f3136b.b();
        if (!this.f3216a.R) {
            int i = (int) (j / 1000);
            this.f3216a.f3138d.setVisibility(0);
            this.f3216a.f3138d.setText(this.f3216a.getResources().getQuantityString(R.plurals.lockpattern_too_many_failed_confirmation_attempts_footer, i, new Object[]{Integer.valueOf(i)}));
        }
    }
}
