package com.xiaomi.analytics.a;

import android.content.Context;
import android.content.SharedPreferences;
import com.xiaomi.analytics.Analytics;
import com.xiaomi.analytics.a.a.b;
import com.xiaomi.analytics.a.a.h;
import com.xiaomi.analytics.a.a.n;
import com.xiaomi.analytics.a.a.o;
import com.xiaomi.analytics.a.a.p;
import java.util.Date;
import java.util.Random;

class l {

    /* renamed from: a  reason: collision with root package name */
    private static final long f8324a = ((long) o.f8294a);

    /* renamed from: b  reason: collision with root package name */
    private static volatile l f8325b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public Context f8326c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public String f8327d = "";
    /* access modifiers changed from: private */
    public String e = "";
    /* access modifiers changed from: private */
    public String f;
    /* access modifiers changed from: private */
    public int g;
    private a h;
    private Runnable i = new j(this);
    /* access modifiers changed from: private */
    public Runnable j = new k(this);

    interface a {
        void a(String str, boolean z);
    }

    private l(Context context) {
        this.f8326c = b.a(context);
    }

    public static synchronized l a(Context context) {
        l lVar;
        synchronized (l.class) {
            if (f8325b == null) {
                f8325b = new l(context);
            }
            lVar = f8325b;
        }
        return lVar;
    }

    /* access modifiers changed from: private */
    public synchronized void a(long j2) {
        SharedPreferences.Editor edit = this.f8326c.getSharedPreferences("analytics_updater", 0).edit();
        edit.putLong("updateTime", j2);
        edit.apply();
    }

    private synchronized long b() {
        return this.f8326c.getSharedPreferences("analytics_updater", 0).getLong("updateTime", 0);
    }

    /* access modifiers changed from: private */
    public long b(String str) {
        try {
            return Long.parseLong(str.split("-")[1]);
        } catch (Exception unused) {
            return System.currentTimeMillis();
        }
    }

    /* access modifiers changed from: private */
    public String c() {
        Random random = new Random(System.nanoTime());
        try {
            String packageName = this.f8326c.getPackageName();
            return p.a(packageName + ":" + random.nextLong());
        } catch (Exception unused) {
            return p.a(random.nextLong() + "");
        }
    }

    /* access modifiers changed from: private */
    public void d() {
        a aVar = this.h;
        if (aVar != null) {
            String str = this.f;
            boolean z = true;
            if (this.g != 1) {
                z = false;
            }
            aVar.a(str, z);
        }
    }

    public void a(a aVar) {
        this.h = aVar;
    }

    public void a(String str) {
        if (!h.a(this.f8326c, "UpdateManager")) {
            com.xiaomi.analytics.a.a.a.a("UpdateManager", "checkUpdate ");
            this.f = str;
            n.a(this.i);
            a(System.currentTimeMillis());
        }
    }

    public boolean a() {
        if (h.a(this.f8326c, "UpdateManager")) {
            return false;
        }
        if (!Analytics.a()) {
            com.xiaomi.analytics.a.a.a.a("UpdateManager", "Updating is disabled.");
            return false;
        }
        long b2 = b();
        com.xiaomi.analytics.a.a.a.a("UpdateManager", "last update check time is " + new Date(b2).toString());
        return System.currentTimeMillis() - b2 >= f8324a;
    }
}
