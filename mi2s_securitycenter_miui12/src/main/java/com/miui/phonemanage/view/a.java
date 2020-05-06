package com.miui.phonemanage.view;

import android.view.View;

class a implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Tab f6624a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Tab f6625b;

    a(Tab tab, Tab tab2) {
        this.f6625b = tab;
        this.f6624a = tab2;
    }

    public void onClick(View view) {
        if (this.f6625b.f6614d != null) {
            this.f6625b.f6614d.a(this.f6624a);
        }
    }
}
