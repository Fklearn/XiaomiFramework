package com.miui.gamebooster.ui;

import android.content.DialogInterface;
import com.miui.gamebooster.ui.N;

/* renamed from: com.miui.gamebooster.ui.m  reason: case insensitive filesystem */
class C0437m implements DialogInterface.OnCancelListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ N.f f5086a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ N f5087b;

    C0437m(N n, N.f fVar) {
        this.f5087b = n;
        this.f5086a = fVar;
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.f5086a.a(true);
    }
}
