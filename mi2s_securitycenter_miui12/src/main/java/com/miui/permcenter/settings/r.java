package com.miui.permcenter.settings;

import android.view.View;

class r implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ k f6562a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ int f6563b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ t f6564c;

    r(t tVar, k kVar, int i) {
        this.f6564c = tVar;
        this.f6562a = kVar;
        this.f6563b = i;
    }

    public void onClick(View view) {
        if (this.f6564c.f6571d != null) {
            this.f6564c.f6571d.a(this.f6562a, this.f6563b);
        }
    }
}
