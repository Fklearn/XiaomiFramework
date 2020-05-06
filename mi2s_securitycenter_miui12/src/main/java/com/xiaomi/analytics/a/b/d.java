package com.xiaomi.analytics.a.b;

import android.util.Log;
import com.xiaomi.analytics.a.a.a;

class d implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ e f8304a;

    d(e eVar) {
        this.f8304a = eVar;
    }

    public void run() {
        synchronized (this.f8304a.h) {
            try {
                if (!this.f8304a.h.isEmpty()) {
                    Object obj = (String[]) this.f8304a.h.toArray(new String[this.f8304a.h.size()]);
                    Class.forName("com.miui.analytics.ICore").getMethod("trackEvents", new Class[]{String[].class}).invoke(this.f8304a.f, new Object[]{obj});
                    a.b("SysAnalytics", String.format("onServiceConnected drain %d pending events", new Object[]{Integer.valueOf(this.f8304a.h.size())}));
                    this.f8304a.h.clear();
                }
            } catch (Exception e) {
                Log.e(a.a("SysAnalytics"), "onServiceConnected drain pending events exception:", e);
            }
        }
    }
}
