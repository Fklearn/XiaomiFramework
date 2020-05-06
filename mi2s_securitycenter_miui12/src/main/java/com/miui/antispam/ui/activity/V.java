package com.miui.antispam.ui.activity;

import android.content.DialogInterface;

class V implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ RemoveBlacklistActivity f2576a;

    V(RemoveBlacklistActivity removeBlacklistActivity) {
        this.f2576a = removeBlacklistActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f2576a.c();
    }
}
