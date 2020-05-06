package com.miui.securityscan;

import android.app.Activity;
import android.content.DialogInterface;
import com.miui.securityscan.a.G;

/* renamed from: com.miui.securityscan.h  reason: case insensitive filesystem */
class C0548h implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f7713a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Activity f7714b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ L f7715c;

    C0548h(L l, boolean z, Activity activity) {
        this.f7715c = l;
        this.f7713a = z;
        this.f7714b = activity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        G.a(2);
        if (this.f7713a) {
            this.f7715c.b();
        }
        this.f7715c.m.removeCallbacksAndMessages((Object) null);
        this.f7714b.finish();
    }
}
