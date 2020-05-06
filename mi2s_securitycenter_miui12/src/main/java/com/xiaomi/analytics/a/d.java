package com.xiaomi.analytics.a;

import android.util.Log;
import com.xiaomi.analytics.a.a.a;
import java.io.File;

class d implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ i f8313a;

    d(i iVar) {
        this.f8313a = iVar;
    }

    public void run() {
        try {
            if (this.f8313a.f == null || l.a(this.f8313a.e).a()) {
                l.a(this.f8313a.e).a(new File(this.f8313a.m()).getAbsolutePath());
            }
        } catch (Exception e) {
            Log.w(a.a("SdkManager"), "mUpdateChecker exception", e);
        }
    }
}
