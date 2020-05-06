package com.miui.gamebooster.a;

import android.view.View;

class B implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f3999a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ D f4000b;

    B(D d2, int i) {
        this.f4000b = d2;
        this.f3999a = i;
    }

    public void onClick(View view) {
        if (this.f4000b.f4005c != null) {
            this.f4000b.f4005c.onItemClick(this.f3999a);
        }
    }
}
