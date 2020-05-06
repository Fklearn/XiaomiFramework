package com.xiaomi.analytics.a.b;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.util.Log;
import com.miui.analytics.ICore;
import com.xiaomi.analytics.a.a.a;
import com.xiaomi.analytics.a.a.b;
import com.xiaomi.analytics.a.a.o;
import com.xiaomi.analytics.a.m;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

public class e implements a {

    /* renamed from: a  reason: collision with root package name */
    private boolean f8305a = false;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public boolean f8306b = false;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public boolean f8307c = false;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public final Object f8308d = new Object();
    private final Object e = new Object();
    /* access modifiers changed from: private */
    public ICore f;
    private Context g;
    /* access modifiers changed from: private */
    public final Set<String> h = new ConcurrentSkipListSet();
    private ServiceConnection i = new c(this);

    public e(Context context) {
        this.g = b.a(context);
        this.f8305a = a(context);
        c();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:0x0015, code lost:
        r4 = r4.getPackageManager().queryIntentServices(r1, 0);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean a(android.content.Context r4) {
        /*
            r0 = 0
            android.content.Intent r1 = new android.content.Intent     // Catch:{ Exception -> 0x0027 }
            r1.<init>()     // Catch:{ Exception -> 0x0027 }
            java.lang.String r2 = "com.miui.analytics"
            java.lang.String r3 = "com.miui.analytics.AnalyticsService"
            r1.setClassName(r2, r3)     // Catch:{ Exception -> 0x0027 }
            if (r4 == 0) goto L_0x0033
            android.content.pm.PackageManager r2 = r4.getPackageManager()     // Catch:{ Exception -> 0x0027 }
            if (r2 == 0) goto L_0x0033
            android.content.pm.PackageManager r4 = r4.getPackageManager()     // Catch:{ Exception -> 0x0027 }
            java.util.List r4 = r4.queryIntentServices(r1, r0)     // Catch:{ Exception -> 0x0027 }
            if (r4 == 0) goto L_0x0033
            int r4 = r4.size()     // Catch:{ Exception -> 0x0027 }
            if (r4 <= 0) goto L_0x0033
            r4 = 1
            return r4
        L_0x0027:
            r4 = move-exception
            java.lang.String r1 = "SysAnalytics"
            java.lang.String r1 = com.xiaomi.analytics.a.a.a.a(r1)
            java.lang.String r2 = "isServiceBuiltIn exception:"
            android.util.Log.e(r1, r2, r4)
        L_0x0033:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.analytics.a.b.e.a(android.content.Context):boolean");
    }

    private void c() {
        if (this.f8305a) {
            try {
                Intent intent = new Intent();
                intent.setClassName("com.miui.analytics", "com.miui.analytics.AnalyticsService");
                this.g.bindService(intent, this.i, 1);
                this.f8307c = true;
                a.b("SysAnalytics", "try bind sys service");
            } catch (Exception e2) {
                Log.e(a.a("SysAnalytics"), "bind service exception:", e2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void d() {
        new Thread(new d(this)).start();
    }

    private void e() {
        synchronized (this.e) {
            if (this.f8307c || (this.f8306b && this.f != null)) {
                Object[] objArr = new Object[3];
                int i2 = 0;
                objArr[0] = Boolean.valueOf(this.f8307c);
                objArr[1] = Boolean.valueOf(this.f8306b);
                if (this.f != null) {
                    i2 = 1;
                }
                objArr[2] = Integer.valueOf(i2);
                a.b("SysAnalytics", String.format("ensureService mConnecting:%s, mConnected:%s, mAnalytics:%d", objArr));
            } else {
                this.g.unbindService(this.i);
                c();
            }
        }
    }

    private String f() {
        try {
            e();
            return this.f != null ? (String) Class.forName("com.miui.analytics.ICore").getMethod("getVersionName", new Class[0]).invoke(this.f, new Object[0]) : "0.0.0";
        } catch (Exception e2) {
            Log.e(a.a("SysAnalytics"), "getVersionName exception:", e2);
            return "0.0.0";
        }
    }

    public boolean a() {
        return this.f8305a;
    }

    public boolean a(String str) {
        try {
            e();
            if (this.f != null) {
                return ((Boolean) Class.forName("com.miui.analytics.ICore").getMethod("isPolicyReady", new Class[]{String.class, String.class}).invoke(this.f, new Object[]{this.g.getPackageName(), str})).booleanValue();
            }
        } catch (Exception e2) {
            Log.e(a.a("SysAnalytics"), "isPolicyReady exception:", e2);
        }
        return false;
    }

    public String b(String str) {
        try {
            e();
            if (this.f == null) {
                return "";
            }
            return (String) Class.forName("com.miui.analytics.ICore").getMethod("getClientExtra", new Class[]{String.class, String.class}).invoke(this.f, new Object[]{this.g.getPackageName(), str});
        } catch (Exception e2) {
            Log.e(a.a("SysAnalytics"), "getClientExtra exception:", e2);
            return "";
        }
    }

    public void b() {
        if (this.f8305a && !this.f8306b) {
            synchronized (this.f8308d) {
                try {
                    this.f8308d.wait((long) (o.f8297d * 3));
                } catch (Exception e2) {
                    Log.e(a.a("SysAnalytics"), "waitForConnected mSyncGuard.wait exception:", e2);
                }
            }
        }
    }

    public m getVersion() {
        return new m(f());
    }

    public void init() {
    }

    public void setDebugOn(boolean z) {
        try {
            e();
            if (this.f != null) {
                Class.forName("com.miui.analytics.ICore").getMethod("setDebugOn", new Class[]{Boolean.TYPE}).invoke(this.f, new Object[]{Boolean.valueOf(z)});
            }
        } catch (Exception e2) {
            Log.e(a.a("SysAnalytics"), "setDebugOn exception:", e2);
        }
    }

    public void setDefaultPolicy(String str, String str2) {
        try {
            e();
            if (this.f != null) {
                Class.forName("com.miui.analytics.ICore").getMethod("setDefaultPolicy", new Class[]{String.class, String.class}).invoke(this.f, new Object[]{str, str2});
            }
        } catch (Throwable th) {
            Log.e(a.a("SysAnalytics"), "setDefaultPolicy exception:", th);
        }
    }

    public void trackEvent(String str) {
        try {
            e();
            if (this.f == null) {
                synchronized (this.h) {
                    this.h.add(str);
                }
                a.b("SysAnalytics", "add 1 event into pending event list");
                return;
            }
            Class.forName("com.miui.analytics.ICore").getMethod("trackEvent", new Class[]{String.class}).invoke(this.f, new Object[]{str});
        } catch (Exception e2) {
            Log.e(a.a("SysAnalytics"), "trackEvent exception:", e2);
        }
    }

    public void trackEvents(String[] strArr) {
        try {
            e();
            if (this.f == null) {
                synchronized (this.h) {
                    if (strArr != null) {
                        if (strArr.length > 0) {
                            Collections.addAll(this.h, strArr);
                        }
                    }
                }
                Object[] objArr = new Object[1];
                objArr[0] = Integer.valueOf(strArr == null ? 0 : strArr.length);
                a.b("SysAnalytics", String.format("add %d events into pending event list", objArr));
                return;
            }
            Class.forName("com.miui.analytics.ICore").getMethod("trackEvents", new Class[]{String[].class}).invoke(this.f, new Object[]{strArr});
        } catch (Exception e2) {
            Log.e(a.a("SysAnalytics"), "trackEvents exception:", e2);
        }
    }
}
