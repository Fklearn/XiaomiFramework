package com.xiaomi.analytics.a;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;
import com.miui.networkassistant.config.Constants;
import com.xiaomi.analytics.PolicyConfiguration;
import com.xiaomi.analytics.a.a.b;
import com.xiaomi.analytics.a.a.c;
import com.xiaomi.analytics.a.a.d;
import com.xiaomi.analytics.a.a.f;
import com.xiaomi.analytics.a.a.n;
import com.xiaomi.analytics.a.a.o;
import com.xiaomi.analytics.a.b.e;
import com.xiaomi.analytics.a.l;
import java.io.File;

public class i {

    /* renamed from: a  reason: collision with root package name */
    private static final int f8318a = (o.f8297d * 30);
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public static volatile i f8319b;
    /* access modifiers changed from: private */

    /* renamed from: c  reason: collision with root package name */
    public static Object f8320c;
    /* access modifiers changed from: private */

    /* renamed from: d  reason: collision with root package name */
    public static boolean f8321d = false;
    /* access modifiers changed from: private */
    public Context e;
    /* access modifiers changed from: private */
    public com.xiaomi.analytics.a.b.a f;
    private PolicyConfiguration g = null;
    /* access modifiers changed from: private */
    public e h;
    private a i;
    private long j = 0;
    /* access modifiers changed from: private */
    public volatile boolean k = false;
    /* access modifiers changed from: private */
    public boolean l = false;
    /* access modifiers changed from: private */
    public boolean m;
    /* access modifiers changed from: private */
    public long n;
    private Handler o;
    private HandlerThread p;
    /* access modifiers changed from: private */
    public com.xiaomi.analytics.a.b.a q = null;
    private Runnable r = new d(this);
    private Runnable s = new e(this);
    private l.a t = new f(this);
    /* access modifiers changed from: private */
    public BroadcastReceiver u = new g(this);
    private Runnable v = new h(this);

    public interface a {
        void a(com.xiaomi.analytics.a.b.a aVar);
    }

    private i(Context context) {
        this.e = b.a(context);
        Context context2 = this.e;
        f8320c = "connectivity";
        this.p = new HandlerThread("api-sdkmgr", 10);
        this.p.start();
        this.o = new Handler(this.p.getLooper());
        this.h = new e(this.e);
        l.a(this.e).a(this.t);
        n.f8293c.execute(this.s);
    }

    public static synchronized i a(Context context) {
        i iVar;
        synchronized (i.class) {
            if (f8319b == null) {
                f8319b = new i(context);
            }
            iVar = f8319b;
        }
        return iVar;
    }

    /* access modifiers changed from: private */
    public void a(long j2) {
        this.o.removeCallbacks(this.v);
        this.o.postDelayed(this.v, j2);
        com.xiaomi.analytics.a.a.a.a("SdkManager", "post dex init task");
    }

    /* access modifiers changed from: private */
    public void a(com.xiaomi.analytics.a.b.a aVar) {
        this.f = aVar;
        com.xiaomi.analytics.a.b.a aVar2 = this.f;
        if (aVar2 != null) {
            if (this.i != null) {
                aVar2.setDebugOn(com.xiaomi.analytics.a.a.a.f8282a);
                com.xiaomi.analytics.a.a.a.a("SdkManager", "Analytics module loaded, version is " + this.f.getVersion());
                this.i.a(this.f);
            }
            PolicyConfiguration policyConfiguration = this.g;
            if (policyConfiguration != null) {
                policyConfiguration.a(this.f);
            }
        }
    }

    /* access modifiers changed from: private */
    public void a(boolean z) {
        try {
            this.e.getSharedPreferences("analytics_api", 0).edit().putBoolean("pld", z).apply();
        } catch (Exception e2) {
            Log.w(com.xiaomi.analytics.a.a.a.a("SdkManager"), "savePreviousLoadDex exception", e2);
        }
    }

    private boolean a(String str) {
        try {
            String str2 = this.e.getPackageManager().getPackageArchiveInfo(str, 1).versionName;
            com.xiaomi.analytics.a.a.a.a("SdkManager", "" + str + " verName: " + str2);
            return !TextUtils.isEmpty(str2) && new m(str2).compareTo(new m("2.7.3")) >= 0;
        } catch (Exception e2) {
            Log.e(com.xiaomi.analytics.a.a.a.a("SdkManager"), "isApkSuitableForAndroidPOrAbove exception: ", e2);
            return false;
        }
    }

    public static void g() {
        f8321d = true;
    }

    /* access modifiers changed from: private */
    public synchronized void h() {
        if (System.currentTimeMillis() - this.j > ((long) o.f8295b)) {
            this.j = System.currentTimeMillis();
            n.f8293c.execute(this.r);
        }
    }

    private String i() {
        return n() + "/" + "analytics_asset.apk";
    }

    private String j() {
        return n() + "/asset_lib/";
    }

    private boolean k() {
        try {
            return this.e.getSharedPreferences("analytics_api", 0).getBoolean("pld", true);
        } catch (Exception e2) {
            Log.w(com.xiaomi.analytics.a.a.a.a("SdkManager"), "getPreviousLoadDex exception", e2);
            return false;
        }
    }

    /* access modifiers changed from: private */
    public int l() {
        if (com.xiaomi.analytics.a.a.a.f8282a) {
            return 10000;
        }
        return f8318a;
    }

    /* access modifiers changed from: private */
    public String m() {
        return n() + "/" + "analytics.apk";
    }

    private String n() {
        return this.e.getDir("analytics", 0).getAbsolutePath();
    }

    private String o() {
        return n() + "/lib/";
    }

    private boolean p() {
        return Build.VERSION.SDK_INT >= 28;
    }

    /* access modifiers changed from: private */
    public boolean q() {
        return this.m && o.a(this.n, (long) l());
    }

    /* access modifiers changed from: private */
    public com.xiaomi.analytics.a.b.a r() {
        try {
            String[] list = this.e.getAssets().list("");
            if (list != null) {
                for (int i2 = 0; i2 < list.length; i2++) {
                    if (!TextUtils.isEmpty(list[i2]) && list[i2].startsWith("analytics_core")) {
                        d.a(this.e, list[i2], i());
                        File file = new File(i());
                        if (file.exists()) {
                            if (!p() || a(i())) {
                                c.a(this.e, i(), j());
                                return new com.xiaomi.analytics.a.b.b(this.e, i(), j());
                            }
                            com.xiaomi.analytics.a.a.a.a("SdkManager", "Not suitable for Android P, so delete it");
                            file.delete();
                            return null;
                        }
                    }
                }
            }
        } catch (Exception e2) {
            Log.w(com.xiaomi.analytics.a.a.a.a("SdkManager"), "loadAssetAnalytics exception", e2);
        }
        return null;
    }

    /* access modifiers changed from: private */
    public void s() {
        if (!k()) {
            this.q = null;
        } else {
            w();
        }
    }

    /* access modifiers changed from: private */
    public com.xiaomi.analytics.a.b.a t() {
        try {
            File file = new File(m());
            if (file.exists()) {
                if (!p() || a(m())) {
                    c.a(this.e, file.getAbsolutePath(), o());
                    return new com.xiaomi.analytics.a.b.b(this.e, file.getAbsolutePath(), o());
                }
                com.xiaomi.analytics.a.a.a.a("SdkManager", "Not suitable for Android P, so delete it");
                file.delete();
                return null;
            }
        } catch (Exception e2) {
            Log.w(com.xiaomi.analytics.a.a.a.a("SdkManager"), "loadLocalAnalytics exception", e2);
        }
        return null;
    }

    /* access modifiers changed from: private */
    public com.xiaomi.analytics.a.b.a u() {
        if (this.h.a()) {
            this.h.b();
        }
        return this.h;
    }

    /* access modifiers changed from: private */
    public void v() {
        File file = new File(o());
        if (!file.exists()) {
            file.mkdirs();
        } else {
            f.a(file);
        }
        File file2 = new File(j());
        if (!file2.exists()) {
            file2.mkdirs();
        } else {
            f.a(file2);
        }
    }

    private void w() {
        com.xiaomi.analytics.a.a.a.a("SdkManager", "register screen receiver");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.System.ACTION_SCREEN_ON);
        intentFilter.addAction(Constants.System.ACTION_SCREEN_OFF);
        this.e.registerReceiver(this.u, intentFilter);
    }

    public void a(a aVar) {
        this.i = aVar;
    }

    public com.xiaomi.analytics.a.b.a d() {
        return this.f;
    }

    public m e() {
        return d() != null ? d().getVersion() : new m("0.0.0");
    }

    public void f() {
        if (this.k) {
            h();
        }
    }
}
