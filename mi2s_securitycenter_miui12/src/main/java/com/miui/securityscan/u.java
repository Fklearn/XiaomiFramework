package com.miui.securityscan;

import android.view.View;
import android.view.ViewStub;
import com.miui.securityscan.ui.main.NativeInterstitialAdLayout;

class u implements ViewStub.OnInflateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ L f7966a;

    u(L l) {
        this.f7966a = l;
    }

    public void onInflate(ViewStub viewStub, View view) {
        L l = this.f7966a;
        l.t = (NativeInterstitialAdLayout) view;
        l.t.setVisibility(8);
    }
}
