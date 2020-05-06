package com.miui.antispam.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;

class O implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MarkNumGuideActivity f2567a;

    O(MarkNumGuideActivity markNumGuideActivity) {
        this.f2567a = markNumGuideActivity;
    }

    /* JADX WARNING: type inference failed for: r3v1, types: [android.content.Context, com.miui.antispam.ui.activity.MarkNumGuideActivity] */
    public void onClick(DialogInterface dialogInterface, int i) {
        this.f2567a.startActivity(new Intent(this.f2567a, MarkNumberBlockActivity.class));
    }
}
