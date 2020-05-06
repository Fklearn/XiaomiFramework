package com.xiaomi.stat.b;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import com.miui.maml.util.net.SimpleRequest;
import com.xiaomi.stat.ab;
import com.xiaomi.stat.ak;
import com.xiaomi.stat.c.c;
import com.xiaomi.stat.d.a;
import com.xiaomi.stat.d.b;
import com.xiaomi.stat.d.d;
import com.xiaomi.stat.d.g;
import com.xiaomi.stat.d.k;
import com.xiaomi.stat.d.o;
import com.xiaomi.stat.d.r;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class i {

    /* renamed from: a  reason: collision with root package name */
    private static final String f8455a = "SecretKeyManager";

    /* renamed from: b  reason: collision with root package name */
    private static final String f8456b = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCA1ynlvPE46RxIPx6qrb8f20DU\n\rkAJgwHtD3zCEkgOjcvFY2mLl0UGnK1F0Vsh4LvImSCa8o8qYYfBguROgIXRdJGZ+\n\rk9stSV7vWmcsxphMfHEE9R4q+QWqgPBSzwyWmwmAQ7PZmHifOrEYl9t/l0YtmjnW\n\r8Zs3aL7Ap9CGse2kWwIDAQAB\r";

    /* renamed from: c  reason: collision with root package name */
    private static final String f8457c = "sid";

    /* renamed from: d  reason: collision with root package name */
    private static final String f8458d = "sk";
    private static final String e = "rt";
    private static final String f = "rc";
    private static final String g = "request_history";
    private static final String h = "last_aes_content";
    private static final String i = "last_success_time";
    private static final String j = "4ef8b4ac42dbc3f95320b73ae0edbd43";
    private static final String k = "050f03d86eeafeb29cf38986462d957c";
    private static final int l = 1;
    private static final int m = 2;
    private static final String n = "1";
    private static final String o = "0";
    private static final int p = 7;
    private static final int q = 15;
    private static volatile i r;
    private Context s = ak.a();
    private byte[] t;
    private byte[] u;
    private String v;

    private i() {
        d();
    }

    public static i a() {
        if (r == null) {
            synchronized (i.class) {
                if (r == null) {
                    r = new i();
                }
            }
        }
        return r;
    }

    private boolean b(boolean z) {
        String str;
        if (Build.VERSION.SDK_INT < 18) {
            str = "under 4.3,use randomly generated key";
        } else {
            if (j()) {
                k();
            }
            JSONObject g2 = g();
            if (g2 != null) {
                String optString = g2.optString("sid");
                if (!TextUtils.isEmpty(g2.optString("sk")) && !TextUtils.isEmpty(optString) && !z) {
                    str = "key and sid already requested successfully in recent 7 days!";
                }
            }
            JSONObject h2 = h();
            long optLong = h2.optLong(e);
            int optInt = h2.optInt("rc");
            if (!r.b(optLong) || optInt < 15 || z) {
                return f();
            }
            str = "request count > max count today, skip...";
        }
        k.b(f8455a, str);
        return false;
    }

    private void d() {
        this.u = a.a();
        byte[] bArr = this.u;
        if (bArr == null || bArr.length <= 0) {
            this.u = a.a(k);
        }
        String concat = g.a(this.u, true).concat("_").concat(String.valueOf(r.b()));
        try {
            concat = g.a(concat.getBytes(SimpleRequest.UTF8), true);
        } catch (UnsupportedEncodingException e2) {
            e2.printStackTrace();
        }
        this.t = a.a(a.a(concat), j);
    }

    private String e() {
        String str;
        JSONObject g2;
        String str2 = null;
        if (Build.VERSION.SDK_INT < 18 || (g2 = g()) == null) {
            str = null;
        } else {
            str2 = g2.optString("sk");
            str = g2.optString("sid");
        }
        return (TextUtils.isEmpty(str2) || TextUtils.isEmpty(str)) ? g.a(this.u, true) : str2;
    }

    private boolean f() {
        boolean z = false;
        try {
            byte[] a2 = a.a();
            String a3 = d.a(o.a(d.a(f8456b), a2));
            i();
            HashMap hashMap = new HashMap();
            hashMap.put("skey_rsa", a3);
            String a4 = c.a(g.a().d(), (Map<String, String>) hashMap, false);
            if (!TextUtils.isEmpty(a4)) {
                k.b(f8455a, "result:" + a4);
                JSONObject jSONObject = new JSONObject(a4);
                String optString = jSONObject.optString("msg");
                int optInt = jSONObject.optInt("code");
                long optLong = jSONObject.optLong("curTime");
                JSONObject optJSONObject = jSONObject.optJSONObject("result");
                if (optInt == 1 && optJSONObject != null) {
                    try {
                        String optString2 = optJSONObject.optString("sid");
                        String a5 = a.a(optJSONObject.optString("key"), a2);
                        JSONObject jSONObject2 = new JSONObject();
                        jSONObject2.put("sk", a5);
                        jSONObject2.put("sid", optString2);
                        this.v = jSONObject2.toString();
                        ab.a().b("last_aes_content", b.a(this.s, jSONObject2.toString()));
                        ab.a().b("last_success_time", optLong);
                        r.a(optLong);
                        return false;
                    } catch (Exception e2) {
                        e = e2;
                    }
                } else if (optInt == 2) {
                    k.b(f8455a, "update secret-key failed: " + optString);
                }
            }
            return true;
        } catch (Exception e3) {
            e = e3;
            z = true;
            k.d(f8455a, "updateSecretKey e", e);
            return z;
        }
    }

    private JSONObject g() {
        String str;
        String a2 = ab.a().a("last_aes_content", "");
        try {
            if (TextUtils.isEmpty(a2)) {
                return null;
            }
            if (!TextUtils.isEmpty(this.v)) {
                str = this.v;
            } else {
                str = b.b(this.s, a2);
                this.v = str;
            }
            return new JSONObject(str);
        } catch (Exception e2) {
            k.d(f8455a, "decodeFromAndroidKeyStore e", e2);
            return null;
        }
    }

    private JSONObject h() {
        try {
            String a2 = ab.a().a("request_history", "");
            if (!TextUtils.isEmpty(a2)) {
                return new JSONObject(a2);
            }
        } catch (Exception e2) {
            k.d(f8455a, "getRequestHistory e", e2);
        }
        return new JSONObject();
    }

    private void i() {
        try {
            JSONObject h2 = h();
            long optLong = h2.optLong(e);
            int optInt = h2.optInt("rc");
            if (r.b(optLong)) {
                h2.put("rc", optInt + 1);
            } else {
                h2.put("rc", 1);
            }
            h2.put(e, r.b());
            ab.a().b("request_history", h2.toString());
        } catch (JSONException e2) {
            k.b(f8455a, "updateSecretKey e", e2);
        }
    }

    private boolean j() {
        long a2 = ab.a().a("last_success_time", 0);
        return a2 != 0 && r.a(a2, 604800000);
    }

    private void k() {
        ab a2 = ab.a();
        this.v = null;
        a2.b("last_aes_content");
        a2.b("last_success_time");
    }

    private synchronized boolean l() {
        boolean z;
        JSONObject g2 = g();
        z = true;
        if (g2 != null) {
            String optString = g2.optString("sk");
            String optString2 = g2.optString("sid");
            if (!TextUtils.isEmpty(optString) && !TextUtils.isEmpty(optString2)) {
                z = false;
            }
        }
        return z;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0036, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void a(boolean r5) {
        /*
            r4 = this;
            monitor-enter(r4)
            boolean r0 = com.xiaomi.stat.b.a()     // Catch:{ all -> 0x0040 }
            if (r0 == 0) goto L_0x0037
            boolean r0 = com.xiaomi.stat.b.b()     // Catch:{ all -> 0x0040 }
            if (r0 != 0) goto L_0x000e
            goto L_0x0037
        L_0x000e:
            boolean r0 = com.xiaomi.stat.d.l.a()     // Catch:{ all -> 0x0040 }
            if (r0 == 0) goto L_0x002e
            r0 = 3
            r1 = 1
        L_0x0016:
            if (r1 > r0) goto L_0x0035
            boolean r2 = r4.b(r5)     // Catch:{ all -> 0x0040 }
            if (r2 == 0) goto L_0x0035
            if (r1 != r0) goto L_0x0021
            goto L_0x0035
        L_0x0021:
            r2 = 10000(0x2710, double:4.9407E-320)
            java.lang.Thread.sleep(r2)     // Catch:{ InterruptedException -> 0x0027 }
            goto L_0x002b
        L_0x0027:
            r2 = move-exception
            r2.printStackTrace()     // Catch:{ all -> 0x0040 }
        L_0x002b:
            int r1 = r1 + 1
            goto L_0x0016
        L_0x002e:
            java.lang.String r5 = "SecretKeyManager"
            java.lang.String r0 = "network not connected!"
            com.xiaomi.stat.d.k.b(r5, r0)     // Catch:{ all -> 0x0040 }
        L_0x0035:
            monitor-exit(r4)
            return
        L_0x0037:
            java.lang.String r5 = "SecretKeyManager"
            java.lang.String r0 = "update abort: statistic or network is not enabled"
            com.xiaomi.stat.d.k.c(r5, r0)     // Catch:{ all -> 0x0040 }
            monitor-exit(r4)
            return
        L_0x0040:
            r5 = move-exception
            monitor-exit(r4)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.stat.b.i.a(boolean):void");
    }

    public synchronized byte[] a(byte[] bArr) {
        if (bArr == null) {
            k.b(f8455a, "encrypt content is empty");
            return null;
        }
        return a.a(bArr, e());
    }

    public synchronized String b() {
        String str;
        String str2;
        JSONObject g2;
        str = null;
        if (Build.VERSION.SDK_INT < 18 || (g2 = g()) == null) {
            str2 = null;
        } else {
            str = g2.optString("sid");
            str2 = g2.optString("sk");
        }
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            str = g.a(this.t, true);
        }
        return str;
    }

    public String c() {
        return (Build.VERSION.SDK_INT < 18 || l()) ? "1" : "0";
    }
}
