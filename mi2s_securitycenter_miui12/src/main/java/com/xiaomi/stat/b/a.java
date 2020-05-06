package com.xiaomi.stat.b;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import com.xiaomi.stat.ab;
import com.xiaomi.stat.ak;
import com.xiaomi.stat.b;
import com.xiaomi.stat.d;
import com.xiaomi.stat.d.c;
import com.xiaomi.stat.d.k;
import com.xiaomi.stat.d.m;
import com.xiaomi.stat.d.r;
import java.util.Map;
import java.util.TreeMap;
import org.json.JSONObject;

public class a {

    /* renamed from: a  reason: collision with root package name */
    public static final int f8432a = 1;

    /* renamed from: b  reason: collision with root package name */
    public static final int f8433b = 2;

    /* renamed from: c  reason: collision with root package name */
    public static final int f8434c = 4;

    /* renamed from: d  reason: collision with root package name */
    private static final String f8435d = "ConfigManager";
    private static final String e = "-";
    private static int f = 0;
    private static int g = 1;
    private static int h = 2;
    private static final String i = "config_request_count";
    private static final String j = "config_request_time";
    private static final String k = "config_success_requested";
    private static final int l = 1;
    private static final int m = 2;
    private static final int n = 0;
    private static final int o = -1;
    private static final int p = 0;
    private static final int q = 12;
    private static final String r = "t";
    private static final int s = 0;
    private static volatile a t;
    /* access modifiers changed from: private */
    public int u = 0;
    private Context v = ak.a();
    private String w;
    /* access modifiers changed from: private */
    public BroadcastReceiver x = new b(this);

    private a() {
    }

    public static a a() {
        if (t == null) {
            synchronized (a.class) {
                if (t == null) {
                    t = new a();
                }
            }
        }
        return t;
    }

    private void a(int i2) {
        if (i2 > 0) {
            try {
                Thread.sleep((long) (i2 * 1000));
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
        }
        b();
    }

    private void a(String str) {
        try {
            k.b(f8435d, String.format("config result:%s", new Object[]{str}));
            d();
            if (!TextUtils.isEmpty(str)) {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.getInt("errorCode") == 0) {
                    long optLong = jSONObject.optLong("time", 0);
                    ab.a().b(k, optLong);
                    b.c(jSONObject.optString(b.i, b.n));
                    b.c(jSONObject.optInt(b.h, -1));
                    b.d(jSONObject.optInt(b.g, 0) / 1000);
                    b.h(jSONObject.optBoolean(b.k));
                    a(jSONObject);
                    r.a(optLong);
                    g.a().a(jSONObject);
                    if (this.u == 1) {
                        this.v.unregisterReceiver(this.x);
                    }
                    this.u = 2;
                }
            }
        } catch (Exception e2) {
            k.d(f8435d, "processResult exception", e2);
        }
    }

    private void a(JSONObject jSONObject) {
        try {
            int optInt = jSONObject.optInt(b.l);
            if (optInt > 0) {
                boolean z = false;
                b.k((optInt & 1) == 1);
                b.i((optInt & 2) == 2);
                if ((optInt & 4) == 4) {
                    z = true;
                }
                b.j(z);
            }
        } catch (Exception e2) {
            k.e(f8435d, "updateConfig: " + e2);
        }
    }

    private String b() {
        k.b(f8435d, "requestConfigInner");
        this.w = g.a().b();
        if (k.b()) {
            this.w = k.f8533a;
        }
        String str = null;
        try {
            TreeMap treeMap = new TreeMap();
            treeMap.put("t", String.valueOf(h));
            treeMap.put("ai", ak.b());
            treeMap.put("rc", m.h());
            treeMap.put(d.f8497d, m.g());
            treeMap.put(d.V, Build.MODEL);
            treeMap.put(d.f8495b, com.xiaomi.stat.a.g);
            treeMap.put(d.j, c.b());
            str = com.xiaomi.stat.c.c.a(this.w, (Map<String, String>) treeMap, false);
            a(str);
            return str;
        } catch (Exception e2) {
            k.b(f8435d, "requestConfigInner exception ", e2);
            return str;
        }
    }

    private boolean c() {
        long b2 = r.b();
        ab a2 = ab.a();
        try {
            if (!ab.a().a(j)) {
                a2.b(j, b2);
                a2.b(i, 1);
                return false;
            } else if (r.b(a2.a(j, 0))) {
                return a2.a(i, 0) >= 12;
            } else {
                a2.b(j, b2);
                a2.b(i, 0);
                return false;
            }
        } catch (Exception e2) {
            k.d(f8435d, "isRequestCountReachMax exception", e2);
            return false;
        }
    }

    private void d() {
        try {
            ab a2 = ab.a();
            a2.b(i, a2.a(i, 0) + 1);
        } catch (Exception e2) {
            k.d(f8435d, "addRequestCount exception", e2);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0097, code lost:
        r7 = java.lang.Integer.parseInt(r6[0]);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void a(boolean r6, boolean r7) {
        /*
            r5 = this;
            monitor-enter(r5)
            boolean r0 = com.xiaomi.stat.b.a()     // Catch:{ all -> 0x00bf }
            if (r0 == 0) goto L_0x00b6
            boolean r0 = com.xiaomi.stat.b.b()     // Catch:{ all -> 0x00bf }
            if (r0 != 0) goto L_0x000f
            goto L_0x00b6
        L_0x000f:
            boolean r0 = com.xiaomi.stat.d.l.a()     // Catch:{ all -> 0x00bf }
            r1 = 1
            if (r0 != 0) goto L_0x004a
            java.lang.String r6 = "ConfigManager"
            java.lang.String r7 = "network is not connected!"
            com.xiaomi.stat.d.k.b(r6, r7)     // Catch:{ all -> 0x00bf }
            android.content.IntentFilter r6 = new android.content.IntentFilter     // Catch:{ Exception -> 0x0031 }
            r6.<init>()     // Catch:{ Exception -> 0x0031 }
            java.lang.String r7 = "android.net.conn.CONNECTIVITY_CHANGE"
            r6.addAction(r7)     // Catch:{ Exception -> 0x0031 }
            android.content.Context r7 = r5.v     // Catch:{ Exception -> 0x0031 }
            android.content.BroadcastReceiver r0 = r5.x     // Catch:{ Exception -> 0x0031 }
            r7.registerReceiver(r0, r6)     // Catch:{ Exception -> 0x0031 }
            r5.u = r1     // Catch:{ Exception -> 0x0031 }
            goto L_0x0048
        L_0x0031:
            r6 = move-exception
            java.lang.String r7 = "ConfigManager"
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x00bf }
            r0.<init>()     // Catch:{ all -> 0x00bf }
            java.lang.String r1 = "updateConfig registerReceiver error:"
            r0.append(r1)     // Catch:{ all -> 0x00bf }
            r0.append(r6)     // Catch:{ all -> 0x00bf }
            java.lang.String r6 = r0.toString()     // Catch:{ all -> 0x00bf }
            com.xiaomi.stat.d.k.e(r7, r6)     // Catch:{ all -> 0x00bf }
        L_0x0048:
            monitor-exit(r5)
            return
        L_0x004a:
            java.lang.String r0 = "ConfigManager"
            java.lang.String r2 = "updateConfig"
            com.xiaomi.stat.d.k.b(r0, r2)     // Catch:{ all -> 0x00bf }
            if (r7 != 0) goto L_0x0084
            java.lang.String r0 = "MI_STAT_TEST"
            java.lang.String r2 = "updateConfig-InToday"
            com.xiaomi.stat.d.k.b(r0, r2)     // Catch:{ all -> 0x00bf }
            com.xiaomi.stat.ab r0 = com.xiaomi.stat.ab.a()     // Catch:{ all -> 0x00bf }
            java.lang.String r2 = "config_success_requested"
            r3 = 0
            long r2 = r0.a((java.lang.String) r2, (long) r3)     // Catch:{ all -> 0x00bf }
            boolean r0 = com.xiaomi.stat.d.r.b(r2)     // Catch:{ all -> 0x00bf }
            if (r0 == 0) goto L_0x0075
            java.lang.String r6 = "ConfigManager"
            java.lang.String r7 = "Today has successfully requested key."
            com.xiaomi.stat.d.k.b(r6, r7)     // Catch:{ all -> 0x00bf }
            monitor-exit(r5)
            return
        L_0x0075:
            boolean r0 = r5.c()     // Catch:{ all -> 0x00bf }
            if (r0 == 0) goto L_0x0084
            java.lang.String r6 = "ConfigManager"
            java.lang.String r7 = "config request to max count skip.."
            com.xiaomi.stat.d.k.d(r6, r7)     // Catch:{ all -> 0x00bf }
            monitor-exit(r5)
            return
        L_0x0084:
            r0 = 0
            if (r6 == 0) goto L_0x00b1
            if (r7 == 0) goto L_0x008a
            goto L_0x00b1
        L_0x008a:
            java.lang.String r6 = com.xiaomi.stat.b.k()     // Catch:{ all -> 0x00bf }
            java.lang.String r7 = "-"
            java.lang.String[] r6 = r6.split(r7)     // Catch:{ all -> 0x00bf }
            int r7 = r6.length     // Catch:{ all -> 0x00bf }
            if (r7 <= r1) goto L_0x00b1
            r7 = r6[r0]     // Catch:{ all -> 0x00bf }
            int r7 = java.lang.Integer.parseInt(r7)     // Catch:{ all -> 0x00bf }
            r6 = r6[r1]     // Catch:{ all -> 0x00bf }
            int r6 = java.lang.Integer.parseInt(r6)     // Catch:{ all -> 0x00bf }
            if (r6 <= r7) goto L_0x00b1
            java.util.Random r0 = new java.util.Random     // Catch:{ all -> 0x00bf }
            r0.<init>()     // Catch:{ all -> 0x00bf }
            int r6 = r6 - r7
            int r6 = r0.nextInt(r6)     // Catch:{ all -> 0x00bf }
            int r0 = r6 + r7
        L_0x00b1:
            r5.a((int) r0)     // Catch:{ all -> 0x00bf }
            monitor-exit(r5)
            return
        L_0x00b6:
            java.lang.String r6 = "ConfigManager"
            java.lang.String r7 = "update abort: statistic or network is not enabled"
            com.xiaomi.stat.d.k.c(r6, r7)     // Catch:{ all -> 0x00bf }
            monitor-exit(r5)
            return
        L_0x00bf:
            r6 = move-exception
            monitor-exit(r5)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.stat.b.a.a(boolean, boolean):void");
    }
}
