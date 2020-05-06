package com.miui.antivirus.activity;

import android.content.DialogInterface;

/* renamed from: com.miui.antivirus.activity.d  reason: case insensitive filesystem */
class C0232d implements DialogInterface.OnDismissListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MainActivity f2715a;

    C0232d(MainActivity mainActivity) {
        this.f2715a = mainActivity;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.f2715a.finish();
    }
}
