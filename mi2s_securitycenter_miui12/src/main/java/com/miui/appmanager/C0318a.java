package com.miui.appmanager;

import android.view.View;

/* renamed from: com.miui.appmanager.a  reason: case insensitive filesystem */
class C0318a implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f3579a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ C0319b f3580b;

    C0318a(C0319b bVar, int i) {
        this.f3580b = bVar;
        this.f3579a = i;
    }

    public void onClick(View view) {
        if (this.f3580b.f3583c != null) {
            this.f3580b.f3583c.onItemClick(this.f3579a);
        }
    }
}
