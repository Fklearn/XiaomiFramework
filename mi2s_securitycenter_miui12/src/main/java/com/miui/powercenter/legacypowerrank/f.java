package com.miui.powercenter.legacypowerrank;

import android.content.pm.IPackageDeleteObserver;
import com.miui.powercenter.legacypowerrank.PowerDetailActivity;

class f extends IPackageDeleteObserver.Stub {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ PowerDetailActivity.a f7090a;

    f(PowerDetailActivity.a aVar) {
        this.f7090a = aVar;
    }

    public void packageDeleted(String str, int i) {
        if (i == 1) {
            this.f7090a.mHandler.post(new e(this));
        }
    }
}
