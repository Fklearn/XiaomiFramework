package com.xiaomi.analytics;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.analytics.a.a.b;
import com.xiaomi.analytics.a.a.l;
import com.xiaomi.analytics.a.b.a;
import com.xiaomi.analytics.a.b.e;
import com.xiaomi.analytics.a.c;
import com.xiaomi.analytics.a.i;
import java.util.concurrent.Callable;

public class Analytics {

    /* renamed from: a  reason: collision with root package name */
    private static volatile boolean f8245a = true;

    /* renamed from: b  reason: collision with root package name */
    private static volatile Analytics f8246b;

    /* renamed from: c  reason: collision with root package name */
    private LoggerFactory<Tracker> f8247c = new LoggerFactory<>();
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public Context f8248d;

    /* renamed from: com.xiaomi.analytics.Analytics$1  reason: invalid class name */
    class AnonymousClass1 implements Callable<String> {

        /* renamed from: a  reason: collision with root package name */
        final /* synthetic */ int f8249a;

        /* renamed from: b  reason: collision with root package name */
        final /* synthetic */ String f8250b;

        /* renamed from: c  reason: collision with root package name */
        final /* synthetic */ Analytics f8251c;

        public String call() {
            long currentTimeMillis = System.currentTimeMillis();
            while (System.currentTimeMillis() - currentTimeMillis < ((long) this.f8249a)) {
                a d2 = i.a(this.f8251c.f8248d).d();
                if (d2 != null) {
                    String b2 = d2.b(this.f8250b);
                    if (!TextUtils.isEmpty(b2) || d2.a(this.f8250b)) {
                        return b2;
                    }
                }
                Thread.sleep(1000);
            }
            return null;
        }
    }

    private Analytics(Context context) {
        this.f8248d = b.a(context);
        BaseLogger.a(this.f8248d);
        b();
        i.a(this.f8248d);
        c.a(this.f8248d).a();
        l.a(this.f8248d);
    }

    public static synchronized Analytics a(Context context) {
        Analytics analytics;
        synchronized (Analytics.class) {
            if (f8246b == null) {
                f8246b = new Analytics(context);
            }
            analytics = f8246b;
        }
        return analytics;
    }

    public static boolean a() {
        return f8245a;
    }

    private void b() {
        new Tracker("");
    }

    public static void b(Context context) {
        if (!e.a(context)) {
            Log.e(com.xiaomi.analytics.a.a.a.a("Analytics"), "system analytics is not exist.");
            return;
        }
        Log.d(com.xiaomi.analytics.a.a.a.a("Analytics"), "use system analytics only");
        i.g();
        b(false);
    }

    public static void b(boolean z) {
        f8245a = z;
    }

    public Tracker a(String str) {
        return this.f8247c.a(Tracker.class, str);
    }

    public void a(boolean z) {
        com.xiaomi.analytics.a.a.a.f8282a = z;
        a d2 = i.a(this.f8248d).d();
        if (d2 != null) {
            d2.setDebugOn(z);
        }
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:4:0x000c */
    @android.webkit.JavascriptInterface
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void trackAdAction(java.lang.String r2, java.lang.String r3, java.lang.String r4) {
        /*
            r1 = this;
            com.xiaomi.analytics.AdAction r3 = com.xiaomi.analytics.Actions.a(r3)     // Catch:{ Exception -> 0x0014 }
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x000c }
            r0.<init>(r4)     // Catch:{ Exception -> 0x000c }
            r3.a((org.json.JSONObject) r0)     // Catch:{ Exception -> 0x000c }
        L_0x000c:
            com.xiaomi.analytics.Tracker r2 = r1.a((java.lang.String) r2)     // Catch:{ Exception -> 0x0014 }
            r2.a(r3)     // Catch:{ Exception -> 0x0014 }
            goto L_0x001c
        L_0x0014:
            r2 = move-exception
            java.lang.String r3 = "Analytics"
            java.lang.String r4 = "JavascriptInterface trackAdAction exception:"
            com.xiaomi.analytics.a.a.a.b(r3, r4, r2)
        L_0x001c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.analytics.Analytics.trackAdAction(java.lang.String, java.lang.String, java.lang.String):void");
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:4:0x000c */
    @android.webkit.JavascriptInterface
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void trackAdAction(java.lang.String r1, java.lang.String r2, java.lang.String r3, java.lang.String r4) {
        /*
            r0 = this;
            com.xiaomi.analytics.AdAction r2 = com.xiaomi.analytics.Actions.a(r2, r3)     // Catch:{ Exception -> 0x0014 }
            org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ Exception -> 0x000c }
            r3.<init>(r4)     // Catch:{ Exception -> 0x000c }
            r2.a((org.json.JSONObject) r3)     // Catch:{ Exception -> 0x000c }
        L_0x000c:
            com.xiaomi.analytics.Tracker r1 = r0.a((java.lang.String) r1)     // Catch:{ Exception -> 0x0014 }
            r1.a(r2)     // Catch:{ Exception -> 0x0014 }
            goto L_0x001c
        L_0x0014:
            r1 = move-exception
            java.lang.String r2 = "Analytics"
            java.lang.String r3 = "JavascriptInterface trackAdAction exception:"
            com.xiaomi.analytics.a.a.a.b(r2, r3, r1)
        L_0x001c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.analytics.Analytics.trackAdAction(java.lang.String, java.lang.String, java.lang.String, java.lang.String):void");
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:4:0x000c */
    @android.webkit.JavascriptInterface
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void trackCustomAction(java.lang.String r3, java.lang.String r4) {
        /*
            r2 = this;
            com.xiaomi.analytics.CustomAction r0 = com.xiaomi.analytics.Actions.a()     // Catch:{ Exception -> 0x0014 }
            org.json.JSONObject r1 = new org.json.JSONObject     // Catch:{ Exception -> 0x000c }
            r1.<init>(r4)     // Catch:{ Exception -> 0x000c }
            r0.a((org.json.JSONObject) r1)     // Catch:{ Exception -> 0x000c }
        L_0x000c:
            com.xiaomi.analytics.Tracker r3 = r2.a((java.lang.String) r3)     // Catch:{ Exception -> 0x0014 }
            r3.a(r0)     // Catch:{ Exception -> 0x0014 }
            goto L_0x001c
        L_0x0014:
            r3 = move-exception
            java.lang.String r4 = "Analytics"
            java.lang.String r0 = "JavascriptInterface trackCustomAction exception:"
            com.xiaomi.analytics.a.a.a.b(r4, r0, r3)
        L_0x001c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.analytics.Analytics.trackCustomAction(java.lang.String, java.lang.String):void");
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:4:0x000c */
    @android.webkit.JavascriptInterface
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void trackEventAction(java.lang.String r2, java.lang.String r3, java.lang.String r4) {
        /*
            r1 = this;
            com.xiaomi.analytics.EventAction r3 = com.xiaomi.analytics.Actions.b(r3)     // Catch:{ Exception -> 0x0014 }
            org.json.JSONObject r0 = new org.json.JSONObject     // Catch:{ Exception -> 0x000c }
            r0.<init>(r4)     // Catch:{ Exception -> 0x000c }
            r3.a((org.json.JSONObject) r0)     // Catch:{ Exception -> 0x000c }
        L_0x000c:
            com.xiaomi.analytics.Tracker r2 = r1.a((java.lang.String) r2)     // Catch:{ Exception -> 0x0014 }
            r2.a(r3)     // Catch:{ Exception -> 0x0014 }
            goto L_0x001c
        L_0x0014:
            r2 = move-exception
            java.lang.String r3 = "Analytics"
            java.lang.String r4 = "JavascriptInterface trackEventAction exception:"
            com.xiaomi.analytics.a.a.a.b(r3, r4, r2)
        L_0x001c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.analytics.Analytics.trackEventAction(java.lang.String, java.lang.String, java.lang.String):void");
    }

    /* JADX WARNING: Exception block dominator not found, dom blocks: [] */
    /* JADX WARNING: Missing exception handler attribute for start block: B:4:0x000c */
    @android.webkit.JavascriptInterface
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void trackEventAction(java.lang.String r1, java.lang.String r2, java.lang.String r3, java.lang.String r4) {
        /*
            r0 = this;
            com.xiaomi.analytics.EventAction r2 = com.xiaomi.analytics.Actions.b(r2, r3)     // Catch:{ Exception -> 0x0014 }
            org.json.JSONObject r3 = new org.json.JSONObject     // Catch:{ Exception -> 0x000c }
            r3.<init>(r4)     // Catch:{ Exception -> 0x000c }
            r2.a((org.json.JSONObject) r3)     // Catch:{ Exception -> 0x000c }
        L_0x000c:
            com.xiaomi.analytics.Tracker r1 = r0.a((java.lang.String) r1)     // Catch:{ Exception -> 0x0014 }
            r1.a(r2)     // Catch:{ Exception -> 0x0014 }
            goto L_0x001c
        L_0x0014:
            r1 = move-exception
            java.lang.String r2 = "Analytics"
            java.lang.String r3 = "JavascriptInterface trackEventAction exception:"
            com.xiaomi.analytics.a.a.a.b(r2, r3, r1)
        L_0x001c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.analytics.Analytics.trackEventAction(java.lang.String, java.lang.String, java.lang.String, java.lang.String):void");
    }
}
