package com.miui.powercenter.quickoptimize;

import android.widget.CheckBox;
import android.widget.CompoundButton;

class p implements CompoundButton.OnCheckedChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ r f7241a;

    p(r rVar) {
        this.f7241a = rVar;
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        this.f7241a.a((CheckBox) compoundButton);
        this.f7241a.e.sendEmptyMessage(1);
    }
}
