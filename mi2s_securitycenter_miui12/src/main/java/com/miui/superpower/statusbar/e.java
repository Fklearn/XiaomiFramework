package com.miui.superpower.statusbar;

import android.hardware.display.DisplayManager;

class e implements DisplayManager.DisplayListener {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ g f8167a;

    e(g gVar) {
        this.f8167a = gVar;
    }

    public void onDisplayAdded(int i) {
    }

    public void onDisplayChanged(int i) {
        this.f8167a.h();
    }

    public void onDisplayRemoved(int i) {
    }
}
