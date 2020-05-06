package com.miui.powercenter.powerui;

import android.hardware.display.DisplayManager;

class g implements DisplayManager.DisplayListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ h f7148a;

    g(h hVar) {
        this.f7148a = hVar;
    }

    public void onDisplayAdded(int i) {
    }

    public void onDisplayChanged(int i) {
        int rotation = this.f7148a.j.getRotation();
        if ((rotation == 1 || rotation == 3) && this.f7148a.q != null && this.f7148a.q.isShowing()) {
            this.f7148a.j();
        }
    }

    public void onDisplayRemoved(int i) {
    }
}
