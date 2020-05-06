package com.miui.gamebooster.mutiwindow;

import android.database.ContentObserver;
import android.os.Handler;

class b extends ContentObserver {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ d f4624a;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    b(d dVar, Handler handler) {
        super(handler);
        this.f4624a = dVar;
    }

    public void onChange(boolean z) {
        d dVar = this.f4624a;
        boolean unused = dVar.f4626a = f.c(dVar.e);
        synchronized (this.f4624a.f4629d) {
            if (!this.f4624a.f4626a && this.f4624a.f4627b) {
                this.f4624a.b(false);
            }
        }
    }
}
