package com.miui.applicationlock;

import android.view.View;
import com.miui.applicationlock.c.C0257a;

class C implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f3110a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ C0257a f3111b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ E f3112c;

    C(E e, int i, C0257a aVar) {
        this.f3112c = e;
        this.f3110a = i;
        this.f3111b = aVar;
    }

    public void onClick(View view) {
        if (this.f3112c.i != null) {
            this.f3112c.i.a(this.f3110a, this.f3111b);
        }
    }
}
