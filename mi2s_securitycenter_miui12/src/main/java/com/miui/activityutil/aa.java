package com.miui.activityutil;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

public class aa {

    /* renamed from: a  reason: collision with root package name */
    private static final boolean f2254a = false;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public static final String f2255b = "com.miui.activityutil.aa";
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public static String[] f2256c = null;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public static String[] f2257d = null;
    /* access modifiers changed from: private */
    public static final String e = aj.a("Y3J1c2FkZQ==");
    /* access modifiers changed from: private */
    public static final String f = aj.a("c2VjX3Vfcg==");
    private static final int g = 2;
    private static final int h = 3;
    private static final int i = 4;
    private static final int j = 5;
    private static final int k = -1;
    private static final int l = 0;
    private static final int m = 1;
    private static final String n = "com.miui.activityutil_data";
    private static final String o = "last_upload_time";
    private static final Set p = new HashSet(Arrays.asList(new String[]{"AT", "BE", "BG", "CY", "CZ", "DE", "DK", "EE", "ES", "FI", "FR", "GB", "GR", "HR", "HU", "IE", "IT", "LT", "LU", "LV", "MT", "NL", "PL", "PT", "RO", "SE", "SI", "SK"}));
    private static aa q;
    /* access modifiers changed from: private */
    public Context r;
    /* access modifiers changed from: private */
    public int s;
    /* access modifiers changed from: private */
    public boolean t;
    private boolean u;
    /* access modifiers changed from: private */
    public Handler v = new Handler(Looper.getMainLooper());

    static {
        String[] strArr = new String[1];
        strArr[0] = aj.a(h.d() ? "aHR0cHM6Ly9mbGFzaC5zZWMuaW50bC5taXVpLmNvbS9kYXRhL3NlYw==" : "aHR0cHM6Ly9mbGFzaC5zZWMubWl1aS5jb20vZGF0YS9zZWM=");
        f2256c = strArr;
        String[] strArr2 = new String[1];
        strArr2[0] = aj.a(h.d() ? " aHR0cHM6Ly9mbGFzaC5zZWMuaW50bC5taXVpLmNvbS9kYXRhL3N0YXR1cw==" : "aHR0cHM6Ly9mbGFzaC5zZWMubWl1aS5jb20vZGF0YS9zdGF0dXM=");
        f2257d = strArr2;
    }

    private aa(Context context) {
        Context applicationContext = context.getApplicationContext();
        if (applicationContext != null) {
            this.r = applicationContext;
        } else {
            this.r = context;
        }
    }

    /* access modifiers changed from: private */
    public int a(byte[] bArr) {
        try {
            JSONObject jSONObject = new JSONObject(p.b(Base64.decode(bArr, 2)));
            String c2 = new h(this.r).c();
            String str = this.r.getApplicationInfo().packageName;
            if (!a(c2, jSONObject.optString("id")) || !a(str, jSONObject.optString("source"))) {
                return -1;
            }
            return jSONObject.getInt("fetched");
        } catch (Exception unused) {
            return -1;
        }
    }

    public static synchronized aa a(Context context) {
        aa aaVar;
        synchronized (aa.class) {
            if (q == null) {
                q = new aa(context);
            }
            aaVar = q;
        }
        return aaVar;
    }

    private void a(int i2) {
        aj.a((Runnable) new ab(this, i2));
    }

    private void a(ai aiVar) {
        if (this.s == 4) {
            aiVar.a(4, 0, true);
        } else {
            aj.a((Runnable) new af(this, aiVar));
        }
    }

    private static boolean a(String str, String str2) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return str.equals(str2);
    }

    private int b(String str) {
        return a(str.getBytes());
    }

    /* access modifiers changed from: private */
    public boolean b(byte[] bArr) {
        if (bArr == null) {
            return false;
        }
        try {
            String b2 = p.b(Base64.decode(bArr, 2));
            if (b2 == null) {
                return false;
            }
            JSONObject jSONObject = new JSONObject(b2);
            String c2 = new h(this.r).c();
            boolean a2 = a(h.e(), jSONObject.optString("version"));
            if (!a2) {
                h.a(h.f2291c);
            }
            return a(o.f2310b, jSONObject.optString("status")) && a(c2, jSONObject.optString("id")) && a2;
        } catch (Exception unused) {
            return false;
        }
    }

    private boolean c(String str) {
        return b(str.getBytes());
    }

    static /* synthetic */ boolean d(aa aaVar) {
        long j2 = aaVar.r.getSharedPreferences(n, 0).getLong(o, -1);
        long currentTimeMillis = System.currentTimeMillis();
        if (j2 != -1 && currentTimeMillis - j2 > TimeUnit.DAYS.toMillis(3)) {
            return true;
        }
        if (j2 > currentTimeMillis) {
            aaVar.m();
        }
        return false;
    }

    private static boolean f() {
        String a2 = h.a("ro.miui.region", h.f2289a);
        if (TextUtils.isEmpty(a2) || TextUtils.equals(a2, h.f2289a)) {
            return true;
        }
        return p.contains(a2);
    }

    private void g() {
        this.t = false;
    }

    private void h() {
        this.t = true;
        ad adVar = new ad(this);
        if (this.s == 4) {
            adVar.a(4, 0, true);
        } else {
            aj.a((Runnable) new af(this, adVar));
        }
    }

    private boolean i() {
        try {
            if (this.r.getPackageManager().getApplicationInfo("com.miui.securitycenter", 0) != null) {
                return this.u;
            }
            return true;
        } catch (PackageManager.NameNotFoundException e2) {
            Log.e(f2255b, "isAllowNetworking", e2);
            return true;
        }
    }

    private boolean j() {
        NetworkInfo activeNetworkInfo = ((ConnectivityManager) this.r.getSystemService("connectivity")).getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isAvailable();
    }

    private boolean k() {
        return b(h.a(aj.a(this.r, f)));
    }

    private boolean l() {
        long j2 = this.r.getSharedPreferences(n, 0).getLong(o, -1);
        long currentTimeMillis = System.currentTimeMillis();
        if (j2 != -1 && currentTimeMillis - j2 > TimeUnit.DAYS.toMillis(3)) {
            return true;
        }
        if (j2 > currentTimeMillis) {
            m();
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void m() {
        this.r.getSharedPreferences(n, 0).edit().putLong(o, System.currentTimeMillis()).commit();
    }

    public final void a(long j2) {
        this.v.postDelayed(new ah(this), j2);
    }

    public final void a(String str) {
        Integer num = 0;
        try {
            num = (Integer) q.b(UserHandle.class, "myUserId", (Class[]) null, new Object[0]);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        if (str != null && !this.t && i()) {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) this.r.getSystemService("connectivity")).getActiveNetworkInfo();
            if ((activeNetworkInfo != null && activeNetworkInfo.isAvailable()) && num.intValue() == 0 && Build.VERSION.SDK_INT >= 23) {
                String a2 = h.a("ro.miui.region", h.f2289a);
                if (!((TextUtils.isEmpty(a2) || TextUtils.equals(a2, h.f2289a)) ? true : p.contains(a2))) {
                    this.t = true;
                    ad adVar = new ad(this);
                    if (this.s == 4) {
                        adVar.a(4, 0, true);
                    } else {
                        aj.a((Runnable) new af(this, adVar));
                    }
                }
            }
        }
    }

    public final void a(boolean z) {
        this.u = z;
    }
}
