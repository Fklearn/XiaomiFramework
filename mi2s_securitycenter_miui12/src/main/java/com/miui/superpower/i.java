package com.miui.superpower;

import android.util.Log;
import com.miui.superpower.a.d;

class i implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ o f8106a;

    i(o oVar) {
        this.f8106a = oVar;
    }

    public void run() {
        this.f8106a.a();
        for (d dVar : this.f8106a.k) {
            try {
                if (dVar.a()) {
                    dVar.c();
                }
            } catch (Exception e) {
                Log.e("SuperPowerSaveManager", "construction restorestate excepiton : " + e);
            }
        }
    }
}
