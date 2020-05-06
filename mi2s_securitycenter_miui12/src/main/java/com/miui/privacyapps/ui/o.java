package com.miui.privacyapps.ui;

import android.view.View;
import b.b.k.c;

class o implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f7414a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ c f7415b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ p f7416c;

    o(p pVar, int i, c cVar) {
        this.f7416c = pVar;
        this.f7414a = i;
        this.f7415b = cVar;
    }

    public void onClick(View view) {
        if (this.f7416c.f7420d != null) {
            this.f7416c.f7420d.a(this.f7414a, this.f7415b);
        }
    }
}
