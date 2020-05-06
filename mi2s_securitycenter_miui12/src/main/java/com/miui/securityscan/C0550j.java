package com.miui.securityscan;

import android.app.Activity;
import android.content.DialogInterface;
import com.miui.securityscan.a.G;

/* renamed from: com.miui.securityscan.j  reason: case insensitive filesystem */
class C0550j implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f7752a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Activity f7753b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ L f7754c;

    C0550j(L l, boolean z, Activity activity) {
        this.f7754c = l;
        this.f7752a = z;
        this.f7753b = activity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        G.a(5);
        if (this.f7752a) {
            this.f7754c.b();
        }
        this.f7754c.m.removeCallbacksAndMessages((Object) null);
        this.f7753b.finish();
    }
}
