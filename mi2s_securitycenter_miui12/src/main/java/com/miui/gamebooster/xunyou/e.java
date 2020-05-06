package com.miui.gamebooster.xunyou;

import android.util.Log;
import com.miui.gamebooster.ui.GameBoosterRealMainActivity;

class e implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ int f5405a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ GameBoosterRealMainActivity f5406b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ MainMiuiVpnManageServiceCallback f5407c;

    e(MainMiuiVpnManageServiceCallback mainMiuiVpnManageServiceCallback, int i, GameBoosterRealMainActivity gameBoosterRealMainActivity) {
        this.f5407c = mainMiuiVpnManageServiceCallback;
        this.f5405a = i;
        this.f5406b = gameBoosterRealMainActivity;
    }

    public void run() {
        try {
            if (this.f5405a == 100) {
                this.f5406b.r = this.f5406b.g.getSetting("detailUrl", (String) null);
                if (this.f5406b.p) {
                    this.f5406b.g.getCoupons();
                    this.f5406b.p = false;
                }
            }
        } catch (Exception e) {
            Log.i(MainMiuiVpnManageServiceCallback.f5393a, e.toString());
        }
    }
}
