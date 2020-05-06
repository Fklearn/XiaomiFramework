package com.miui.antispam.ui.activity;

import android.content.DialogInterface;

/* renamed from: com.miui.antispam.ui.activity.b  reason: case insensitive filesystem */
class C0208b implements DialogInterface.OnCancelListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AddAntiSpamActivity f2584a;

    C0208b(AddAntiSpamActivity addAntiSpamActivity) {
        this.f2584a = addAntiSpamActivity;
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.f2584a.finish();
    }
}
