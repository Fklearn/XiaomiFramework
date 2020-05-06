package com.miui.securityscan.b;

import android.util.Log;
import com.miui.securityscan.L;
import java.lang.ref.WeakReference;

public class j implements n {

    /* renamed from: a  reason: collision with root package name */
    private final WeakReference<L> f7615a;

    public j(L l) {
        this.f7615a = new WeakReference<>(l);
    }

    public void a() {
        Log.d("ScanOptimizeSecurityCallback", "mSecurityCallback onStartScanManualItem");
        L l = (L) this.f7615a.get();
        if (l != null) {
            l.m.post(new h(this, l));
        }
    }

    public void b() {
        L l = (L) this.f7615a.get();
        if (l != null) {
            l.c();
        }
    }

    public void c() {
        Log.d("ScanOptimizeSecurityCallback", "mSecurityCallback onFinishScanManualItem");
        L l = (L) this.f7615a.get();
        if (l != null) {
            l.m.post(new i(this, l));
        }
    }
}
