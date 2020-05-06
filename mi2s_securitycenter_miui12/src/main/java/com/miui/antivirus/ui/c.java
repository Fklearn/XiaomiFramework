package com.miui.antivirus.ui;

import android.content.Context;
import android.content.DialogInterface;
import com.miui.antivirus.model.a;

class c implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ a f2955a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Context f2956b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ e f2957c;

    c(e eVar, a aVar, Context context) {
        this.f2957c = eVar;
        this.f2955a = aVar;
        this.f2956b = context;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        if (i == 0) {
            dialogInterface.dismiss();
            this.f2957c.b(this.f2955a, this.f2956b);
        }
    }
}
