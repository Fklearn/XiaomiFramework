package com.miui.antispam.ui.activity;

import android.content.DialogInterface;
import android.content.Intent;
import com.miui.antispam.ui.activity.MarkNumberBlockActivity;

class P implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ MarkNumberBlockActivity.a f2568a;

    P(MarkNumberBlockActivity.a aVar) {
        this.f2568a = aVar;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f2568a.e.startActivity(new Intent("miui.intent.action.TURN_ON_SMART_ANTISPAM"));
    }
}
