package com.miui.antispam.ui.activity;

import android.content.DialogInterface;

class N implements DialogInterface.OnDismissListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MarkNumGuideActivity f2566a;

    N(MarkNumGuideActivity markNumGuideActivity) {
        this.f2566a = markNumGuideActivity;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.f2566a.finish();
    }
}
