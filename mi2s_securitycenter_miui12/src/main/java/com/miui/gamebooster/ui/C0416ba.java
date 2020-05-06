package com.miui.gamebooster.ui;

import android.app.Activity;
import android.content.DialogInterface;
import com.miui.gamebooster.model.C0396b;

/* renamed from: com.miui.gamebooster.ui.ba  reason: case insensitive filesystem */
class C0416ba implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0396b f5048a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ Activity f5049b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ GameBoosterRealMainActivity f5050c;

    C0416ba(GameBoosterRealMainActivity gameBoosterRealMainActivity, C0396b bVar, Activity activity) {
        this.f5050c = gameBoosterRealMainActivity;
        this.f5048a = bVar;
        this.f5049b = activity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f5048a.a(this.f5049b.getApplicationContext(), "com.miui.securitycenter", true, 1, true);
    }
}
