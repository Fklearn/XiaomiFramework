package com.xiaomi.analytics.a;

import com.xiaomi.analytics.a.b.a;

class h implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ i f8317a;

    h(i iVar) {
        this.f8317a = iVar;
    }

    public void run() {
        String str;
        String str2;
        try {
            synchronized (i.f8319b) {
                if (!this.f8317a.q() || this.f8317a.q == null) {
                    str = "SdkManager";
                    str2 = "skip init dex";
                } else {
                    this.f8317a.q.init();
                    a unused = this.f8317a.q = null;
                    this.f8317a.e.unregisterReceiver(this.f8317a.u);
                    str = "SdkManager";
                    str2 = "pending dex init executed, unregister and clear pending";
                }
                com.xiaomi.analytics.a.a.a.a(str, str2);
            }
        } catch (Exception e) {
            com.xiaomi.analytics.a.a.a.b("SdkManager", "dexInitTask", e);
        }
    }
}
