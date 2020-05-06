package com.miui.gamebooster.p;

import android.view.View;

class m implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f4730a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ r f4731b;

    m(r rVar, boolean z) {
        this.f4731b = rVar;
        this.f4730a = z;
    }

    public void onClick(View view) {
        this.f4731b.j();
        this.f4731b.a(!this.f4730a);
    }
}
