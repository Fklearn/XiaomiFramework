package com.miui.antispam.ui.activity;

import android.content.DialogInterface;

class U implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RemoveBlacklistActivity f2575a;

    U(RemoveBlacklistActivity removeBlacklistActivity) {
        this.f2575a = removeBlacklistActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f2575a.finish();
    }
}
