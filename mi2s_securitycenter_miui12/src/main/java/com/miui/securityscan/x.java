package com.miui.securityscan;

import com.miui.securityscan.ui.main.NativeInterstitialAdLayout;

class x implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ L f8042a;

    x(L l) {
        this.f8042a = l;
    }

    public void run() {
        NativeInterstitialAdLayout nativeInterstitialAdLayout = this.f8042a.t;
        if (nativeInterstitialAdLayout != null) {
            nativeInterstitialAdLayout.setVisibility(8);
        }
    }
}
