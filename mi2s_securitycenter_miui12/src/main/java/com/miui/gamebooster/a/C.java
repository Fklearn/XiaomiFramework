package com.miui.gamebooster.a;

import android.view.View;

class C implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f4001a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ D f4002b;

    C(D d2, int i) {
        this.f4002b = d2;
        this.f4001a = i;
    }

    public void onClick(View view) {
        if (this.f4002b.f4005c != null) {
            this.f4002b.f4005c.onItemClick(this.f4001a);
        }
    }
}
