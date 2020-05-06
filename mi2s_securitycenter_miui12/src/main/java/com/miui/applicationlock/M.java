package com.miui.applicationlock;

import android.os.CountDownTimer;
import com.miui.securitycenter.R;

class M extends CountDownTimer {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ConfirmAccessControl f3189a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    M(ConfirmAccessControl confirmAccessControl, long j, long j2) {
        super(j, j2);
        this.f3189a = confirmAccessControl;
    }

    public void onFinish() {
        this.f3189a.ma.setClickable(true);
        this.f3189a.ma.setText(this.f3189a.getResources().getString(R.string.reset_data_dialog_ok));
        this.f3189a.ma.setTextColor(-10855846);
    }

    public void onTick(long j) {
        this.f3189a.ma.setClickable(false);
        int i = (int) (j / 1000);
        this.f3189a.ma.setText(this.f3189a.getResources().getQuantityString(R.plurals.reset_data_dialog_ok_tick, i, new Object[]{Integer.valueOf(i)}));
        this.f3189a.ma.setTextColor(-5131855);
    }
}
