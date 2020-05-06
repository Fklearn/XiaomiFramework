package com.miui.securityscan;

import android.os.Handler;
import android.view.View;
import android.view.ViewStub;
import com.miui.securityscan.ui.main.OptimizingBar;

class J implements ViewStub.OnInflateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ L f7555a;

    J(L l) {
        this.f7555a = l;
    }

    public void onInflate(ViewStub viewStub, View view) {
        L l = this.f7555a;
        l.s = (OptimizingBar) view;
        l.s.a((Handler) l.m);
    }
}
