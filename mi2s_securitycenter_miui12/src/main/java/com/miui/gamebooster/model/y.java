package com.miui.gamebooster.model;

import android.view.View;
import com.miui.gamebooster.m.C0373d;
import com.miui.gamebooster.model.C;
import com.miui.securityscan.i.i;

class y implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f4611a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ t f4612b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ C.a f4613c;

    y(C.a aVar, boolean z, t tVar) {
        this.f4613c = aVar;
        this.f4611a = z;
        this.f4612b = tVar;
    }

    public void onClick(View view) {
        if (!this.f4611a) {
            C0373d.t(this.f4612b.d(), "WonderfulMomentActivity");
            i.a(this.f4613c.f4539a, this.f4613c.a(this.f4612b), "video/*", true);
        }
    }
}
