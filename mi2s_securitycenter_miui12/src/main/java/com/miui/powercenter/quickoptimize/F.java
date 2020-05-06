package com.miui.powercenter.quickoptimize;

import com.miui.powercenter.abnormalscan.e;
import com.miui.securitycenter.R;

class F implements e.a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ScanResultFrame f7195a;

    F(ScanResultFrame scanResultFrame) {
        this.f7195a = scanResultFrame;
    }

    public void a(boolean z) {
        this.f7195a.i.setEnabled(z);
        this.f7195a.i.setBackgroundResource(z ? R.drawable.pc_button_selector : R.drawable.pc_button_selector_disable);
    }
}
