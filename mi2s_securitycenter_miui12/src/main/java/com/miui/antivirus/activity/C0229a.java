package com.miui.antivirus.activity;

import android.content.DialogInterface;
import b.b.b.a.b;
import com.miui.antivirus.model.e;

/* renamed from: com.miui.antivirus.activity.a  reason: case insensitive filesystem */
class C0229a implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ e f2711a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ MainActivity f2712b;

    C0229a(MainActivity mainActivity, e eVar) {
        this.f2712b = mainActivity;
        this.f2711a = eVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f2712b.r.d(this.f2711a);
        this.f2712b.r.a(this.f2711a.m());
        this.f2712b.z();
        b.C0023b.h();
    }
}
