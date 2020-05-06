package com.miui.applicationlock;

import android.os.Bundle;
import android.util.Log;

class V implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ Bundle f3226a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ ConfirmAccessControl f3227b;

    V(ConfirmAccessControl confirmAccessControl, Bundle bundle) {
        this.f3227b = confirmAccessControl;
        this.f3226a = bundle;
    }

    public void run() {
        try {
            this.f3227b.startActivity(this.f3227b.l, this.f3226a);
        } catch (Exception e) {
            Log.e("ConfirmAccessControl", "post delay start activity", e);
        }
    }
}
