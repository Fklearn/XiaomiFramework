package com.miui.permcenter.autostart;

import android.view.View;
import com.miui.permcenter.a;

class k implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f6083a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ a f6084b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ l f6085c;

    k(l lVar, int i, a aVar) {
        this.f6085c = lVar;
        this.f6083a = i;
        this.f6084b = aVar;
    }

    public void onClick(View view) {
        this.f6085c.e.a(this.f6083a, view, this.f6084b);
    }
}
