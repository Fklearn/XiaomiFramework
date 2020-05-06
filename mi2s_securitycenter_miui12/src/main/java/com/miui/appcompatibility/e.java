package com.miui.appcompatibility;

import android.content.DialogInterface;

class e implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppExcepitonTipsActivity f3080a;

    e(AppExcepitonTipsActivity appExcepitonTipsActivity) {
        this.f3080a = appExcepitonTipsActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f3080a.setResult(-1);
        a.a("module_click", "continue");
        this.f3080a.finish();
    }
}
