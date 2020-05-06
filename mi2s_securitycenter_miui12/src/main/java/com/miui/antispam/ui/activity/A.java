package com.miui.antispam.ui.activity;

import android.content.DialogInterface;
import com.miui.antispam.ui.activity.CallInterceptSettingsActivity;

class A implements DialogInterface.OnDismissListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ CallInterceptSettingsActivity.a f2507a;

    A(CallInterceptSettingsActivity.a aVar) {
        this.f2507a = aVar;
    }

    public void onDismiss(DialogInterface dialogInterface) {
        this.f2507a.d();
    }
}
