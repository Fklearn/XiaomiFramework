package com.miui.gamebooster.gbservices;

import android.telephony.PhoneStateListener;
import android.util.Log;

class A extends PhoneStateListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C f4310a;

    A(C c2) {
        this.f4310a = c2;
    }

    public void onCallStateChanged(int i, String str) {
        Log.d("GameBoosterService", i + " " + this.f4310a.f4318c);
        if (this.f4310a.f4318c == 2 && i == 0) {
            if (!this.f4310a.f4319d) {
                this.f4310a.a(false);
            } else {
                return;
            }
        }
        int unused = this.f4310a.f4318c = i;
        if (i != 2) {
            return;
        }
        if (this.f4310a.g()) {
            boolean unused2 = this.f4310a.f4319d = true;
            return;
        }
        boolean unused3 = this.f4310a.f4319d = false;
        this.f4310a.a(true);
    }
}
