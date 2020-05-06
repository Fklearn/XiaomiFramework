package com.miui.securityscan;

import android.app.Activity;
import android.content.DialogInterface;
import com.miui.securityscan.a.G;

/* renamed from: com.miui.securityscan.l  reason: case insensitive filesystem */
class C0552l implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ boolean f7757a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Activity f7758b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ L f7759c;

    C0552l(L l, boolean z, Activity activity) {
        this.f7759c = l;
        this.f7757a = z;
        this.f7758b = activity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        G.a(8);
        if (this.f7757a) {
            this.f7759c.b();
        }
        this.f7759c.m.removeCallbacksAndMessages((Object) null);
        this.f7758b.finish();
    }
}
