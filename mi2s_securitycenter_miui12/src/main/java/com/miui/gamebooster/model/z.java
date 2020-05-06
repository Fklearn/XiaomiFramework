package com.miui.gamebooster.model;

import android.view.View;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.model.C;

class z implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f4614a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ t f4615b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ C.a f4616c;

    z(C.a aVar, boolean z, t tVar) {
        this.f4616c = aVar;
        this.f4614a = z;
        this.f4615b = tVar;
    }

    public void onClick(View view) {
        if (!this.f4614a) {
            C0373d.z(this.f4615b.d(), "WonderfulMomentActivity");
            this.f4616c.a(view, this.f4615b);
        }
    }
}
