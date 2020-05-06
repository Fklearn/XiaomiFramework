package com.xiaomi.analytics.a;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import com.xiaomi.analytics.a.a.b;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private static volatile c f8309a = null;

    /* renamed from: b  reason: collision with root package name */
    public static boolean f8310b = false;

    /* renamed from: c  reason: collision with root package name */
    private Context f8311c;

    /* renamed from: d  reason: collision with root package name */
    private volatile boolean f8312d = false;
    private BroadcastReceiver e = new b(this);

    private c(Context context) {
        this.f8311c = b.a(context);
    }

    public static synchronized c a(Context context) {
        c cVar;
        synchronized (c.class) {
            if (f8309a == null) {
                f8309a = new c(context);
            }
            cVar = f8309a;
        }
        return cVar;
    }

    public void a() {
        if (!this.f8312d) {
            this.f8312d = true;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("com.xiaomi.analytics.intent.DEBUG_ON");
            intentFilter.addAction("com.xiaomi.analytics.intent.DEBUG_OFF");
            intentFilter.addAction("com.xiaomi.analytics.intent.STAGING_ON");
            intentFilter.addAction("com.xiaomi.analytics.intent.STAGING_OFF");
            this.f8311c.registerReceiver(this.e, intentFilter);
        }
    }
}
