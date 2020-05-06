package com.miui.powercenter.abnormalscan;

import android.widget.CompoundButton;

class c implements CompoundButton.OnCheckedChangeListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ e f6652a;

    c(e eVar) {
        this.f6652a = eVar;
    }

    public void onCheckedChanged(CompoundButton compoundButton, boolean z) {
        if (this.f6652a.g) {
            AbScanModel abScanModel = (AbScanModel) compoundButton.getTag();
            if (!z) {
                this.f6652a.h.remove(abScanModel.getAbnormalPkg());
            } else {
                this.f6652a.h.add(abScanModel.getAbnormalPkg());
            }
            if (this.f6652a.i != null) {
                this.f6652a.i.a(this.f6652a.h.size() != 0);
            }
        }
    }
}
