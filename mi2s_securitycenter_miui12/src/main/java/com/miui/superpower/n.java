package com.miui.superpower;

import android.util.Log;
import com.miui.superpower.a.d;

class n implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ o f8119a;

    n(o oVar) {
        this.f8119a = oVar;
    }

    public void run() {
        if (this.f8119a.j.get()) {
            for (d b2 : this.f8119a.k) {
                try {
                    b2.b();
                } catch (Exception e) {
                    Log.e("SuperPowerSaveManager", "rePower excepiton : " + e);
                }
            }
        }
    }
}
