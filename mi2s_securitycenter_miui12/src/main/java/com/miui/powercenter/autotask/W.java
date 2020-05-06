package com.miui.powercenter.autotask;

import android.content.DialogInterface;
import com.miui.powercenter.autotask.X;

class W implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int[] f6737a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ AutoTask f6738b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ X.a f6739c;

    W(int[] iArr, AutoTask autoTask, X.a aVar) {
        this.f6737a = iArr;
        this.f6738b = autoTask;
        this.f6739c = aVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        int i2 = this.f6737a[i];
        if (i2 == -1) {
            this.f6738b.removeOperation("auto_clean_memory");
        } else {
            this.f6738b.setOperation("auto_clean_memory", Integer.valueOf(i2));
        }
        this.f6739c.a("auto_clean_memory");
        dialogInterface.dismiss();
    }
}
