package com.miui.applicationlock;

import android.content.DialogInterface;
import android.os.CountDownTimer;

class P implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ CountDownTimer f3201a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ ConfirmAccessControl f3202b;

    P(ConfirmAccessControl confirmAccessControl, CountDownTimer countDownTimer) {
        this.f3202b = confirmAccessControl;
        this.f3201a = countDownTimer;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f3201a.cancel();
    }
}
