package com.miui.applicationlock;

import android.content.DialogInterface;
import android.os.CountDownTimer;

class N implements DialogInterface.OnDismissListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ CountDownTimer f3196a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ ConfirmAccessControl f3197b;

    N(ConfirmAccessControl confirmAccessControl, CountDownTimer countDownTimer) {
        this.f3197b = confirmAccessControl;
        this.f3196a = countDownTimer;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.f3196a.cancel();
    }
}
