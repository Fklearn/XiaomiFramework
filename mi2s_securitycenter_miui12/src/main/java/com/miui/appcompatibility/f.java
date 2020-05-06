package com.miui.appcompatibility;

import android.content.DialogInterface;
import com.miui.luckymoney.stats.MiStatUtil;

class f implements DialogInterface.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppExcepitonTipsActivity f3081a;

    f(AppExcepitonTipsActivity appExcepitonTipsActivity) {
        this.f3081a = appExcepitonTipsActivity;
    }

    public void onClick(DialogInterface dialogInterface, int i) {
        this.f3081a.setResult(0);
        a.a("module_click", MiStatUtil.CLOSE);
        this.f3081a.finish();
    }
}
