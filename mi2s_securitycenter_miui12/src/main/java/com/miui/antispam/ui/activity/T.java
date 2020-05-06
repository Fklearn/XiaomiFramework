package com.miui.antispam.ui.activity;

import android.content.DialogInterface;

class T implements DialogInterface.OnCancelListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RemoveBlacklistActivity f2574a;

    T(RemoveBlacklistActivity removeBlacklistActivity) {
        this.f2574a = removeBlacklistActivity;
    }

    public void onCancel(DialogInterface dialogInterface) {
        this.f2574a.finish();
    }
}
