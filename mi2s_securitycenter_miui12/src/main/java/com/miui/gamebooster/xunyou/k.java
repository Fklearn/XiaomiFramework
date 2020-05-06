package com.miui.gamebooster.xunyou;

import android.content.Context;
import android.widget.CompoundButton;
import com.miui.gamebooster.c.a;
import com.miui.gamebooster.m.C0371b;

class k implements CompoundButton.OnCheckedChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Context f5416a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ m f5417b;

    k(m mVar, Context context) {
        this.f5417b = mVar;
        this.f5416a = context;
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        a.ba(z);
        if (z) {
            C0371b.b(this.f5416a);
        } else {
            C0371b.a(this.f5416a);
        }
    }
}
