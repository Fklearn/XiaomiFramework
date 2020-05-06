package com.miui.gamebooster.model;

import android.view.View;
import com.miui.gamebooster.a.I;
import com.miui.gamebooster.model.C;

class x implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f4607a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ t f4608b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ I.a f4609c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ int f4610d;
    final /* synthetic */ C.a e;

    x(C.a aVar, int i, t tVar, I.a aVar2, int i2) {
        this.e = aVar;
        this.f4607a = i;
        this.f4608b = tVar;
        this.f4609c = aVar2;
        this.f4610d = i2;
    }

    public void onClick(View view) {
        this.f4608b.a(this.e.f[this.f4607a].isChecked());
        I.a aVar = this.f4609c;
        if (aVar != null) {
            aVar.a(this.f4610d);
        }
    }
}
