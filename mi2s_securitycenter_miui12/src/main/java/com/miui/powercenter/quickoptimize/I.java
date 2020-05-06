package com.miui.powercenter.quickoptimize;

import android.view.View;
import b.b.c.j.g;
import java.util.ArrayList;
import java.util.List;

class I implements View.OnClickListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ ScanResultFrame f7198a;

    I(ScanResultFrame scanResultFrame) {
        this.f7198a = scanResultFrame;
    }

    public void onClick(View view) {
        this.f7198a.i.setEnabled(false);
        if (this.f7198a.e == null) {
            this.f7198a.g();
            return;
        }
        this.f7198a.h();
        z.a((List<String>) new ArrayList(this.f7198a.e.a()), g.a(this.f7198a.f7208a));
    }
}
