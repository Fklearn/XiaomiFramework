package com.miui.powercenter.autotask;

import android.content.DialogInterface;
import com.miui.powercenter.autotask.X;

class V implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f6733a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ AutoTask f6734b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ String f6735c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ X.a f6736d;

    V(int i, AutoTask autoTask, String str, X.a aVar) {
        this.f6733a = i;
        this.f6734b = autoTask;
        this.f6735c = str;
        this.f6736d = aVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i != this.f6733a) {
            int i2 = i == 0 ? 1 : i == 1 ? 0 : 2;
            if (i2 == 2) {
                this.f6734b.removeOperation(this.f6735c);
            } else {
                if ("gps".equals(this.f6735c)) {
                    i2 = i2 == 1 ? 3 : AutoTask.GPS_OFF;
                }
                this.f6734b.setOperation(this.f6735c, Integer.valueOf(i2));
            }
            X.a aVar = this.f6736d;
            if (aVar != null) {
                aVar.a(this.f6735c);
            }
        }
        dialogInterface.dismiss();
    }
}
