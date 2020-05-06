package com.miui.superpower.statusbar.button;

import android.database.ContentObserver;
import android.os.Handler;

class c extends ContentObserver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ GPSButton f8164a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    c(GPSButton gPSButton, Handler handler) {
        super(handler);
        this.f8164a = gPSButton;
    }

    public void onChange(boolean z) {
        this.f8164a.b();
    }
}
