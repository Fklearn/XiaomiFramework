package com.miui.antivirus.ui;

import android.view.View;
import b.b.b.o;
import b.b.c.i.b;
import com.miui.antivirus.model.e;

class r implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ o f2990a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ e f2991b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ b f2992c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ s f2993d;

    r(s sVar, o oVar, e eVar, b bVar) {
        this.f2993d = sVar;
        this.f2990a = oVar;
        this.f2991b = eVar;
        this.f2992c = bVar;
    }

    public void onClick(View view) {
        this.f2990a.c(this.f2991b);
        this.f2990a.e(this.f2991b);
        this.f2992c.a(1012, this.f2991b);
        this.f2993d.dismiss();
    }
}
