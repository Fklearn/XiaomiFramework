package com.miui.powercenter.bootshutdown;

import android.content.DialogInterface;
import com.miui.powercenter.bootshutdown.RepeatPreference;

class l implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int[] f6962a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ RepeatPreference.a f6963b;

    l(RepeatPreference.a aVar, int[] iArr) {
        this.f6963b = aVar;
        this.f6962a = iArr;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        c cVar;
        c cVar2;
        int i2 = this.f6962a[i];
        if (i2 == 0) {
            cVar = RepeatPreference.this.f6946d;
            cVar2 = new c(0);
        } else if (i2 == 1) {
            cVar = RepeatPreference.this.f6946d;
            cVar2 = new c(127);
        } else if (i2 == 2) {
            RepeatPreference.this.f6946d.a(true);
            RepeatPreference.this.f();
            dialogInterface.cancel();
        } else if (i2 != 3) {
            if (i2 == 4) {
                RepeatPreference.this.e();
            }
            dialogInterface.cancel();
        } else {
            cVar = RepeatPreference.this.f6946d;
            cVar2 = new c(31);
        }
        cVar.a(cVar2);
        RepeatPreference.this.f();
        dialogInterface.cancel();
    }
}
