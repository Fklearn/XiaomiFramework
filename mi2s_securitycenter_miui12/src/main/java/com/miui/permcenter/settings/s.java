package com.miui.permcenter.settings;

import android.view.View;

class s implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ k f6565a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ int f6566b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ t f6567c;

    s(t tVar, k kVar, int i) {
        this.f6567c = tVar;
        this.f6565a = kVar;
        this.f6566b = i;
    }

    public void onClick(View view) {
        if (this.f6567c.f6571d != null) {
            this.f6567c.f6571d.b(this.f6565a, this.f6566b);
        }
    }
}
