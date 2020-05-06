package com.miui.antivirus.ui;

import android.view.View;
import com.miui.antivirus.ui.p;

class o implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ p.b f2975a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ boolean f2976b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ p f2977c;

    o(p pVar, p.b bVar, boolean z) {
        this.f2977c = pVar;
        this.f2975a = bVar;
        this.f2976b = z;
    }

    public void onClick(View view) {
        this.f2977c.f2980c.onCheckedChanged(this.f2975a.f2985c, !this.f2976b);
    }
}
