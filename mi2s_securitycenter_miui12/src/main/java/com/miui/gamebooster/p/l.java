package com.miui.gamebooster.p;

import android.view.View;

class l implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f4728a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ r f4729b;

    l(r rVar, boolean z) {
        this.f4729b = rVar;
        this.f4728a = z;
    }

    public void onClick(View view) {
        this.f4729b.j();
        this.f4729b.a(!this.f4728a);
    }
}
