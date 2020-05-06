package com.miui.permcenter.autostart;

import android.view.View;
import com.miui.permcenter.autostart.b;

class a implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f6058a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ b.C0057b f6059b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ b f6060c;

    a(b bVar, int i, b.C0057b bVar2) {
        this.f6060c = bVar;
        this.f6058a = i;
        this.f6059b = bVar2;
    }

    public void onClick(View view) {
        this.f6060c.e.a(this.f6058a, this.f6059b);
    }
}
