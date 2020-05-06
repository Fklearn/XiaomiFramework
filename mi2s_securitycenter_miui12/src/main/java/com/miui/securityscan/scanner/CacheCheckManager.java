package com.miui.securityscan.scanner;

import android.content.Context;
import android.content.pm.PackageManager;
import b.b.c.f.a;
import com.miui.optimizecenter.garbagecheck.IGarbageScanCallback;
import com.miui.securityscan.scanner.O;
import miui.util.Log;

class CacheCheckManager {

    /* renamed from: a  reason: collision with root package name */
    private static CacheCheckManager f7822a;

    /* renamed from: b  reason: collision with root package name */
    private Context f7823b;

    /* renamed from: c  reason: collision with root package name */
    private PackageManager f7824c;

    /* renamed from: d  reason: collision with root package name */
    private a f7825d;

    public static class CacheScanCallbackAdapter extends IGarbageScanCallback.Stub {
        public boolean a(String str, String str2, String str3, long j, boolean z) {
            return false;
        }

        public void b() {
        }

        public boolean b(String str) {
            return false;
        }

        public void c() {
        }
    }

    private CacheCheckManager(Context context) {
        this.f7823b = context;
        this.f7824c = context.getPackageManager();
        this.f7825d = a.a(context);
    }

    public static synchronized CacheCheckManager a(Context context) {
        CacheCheckManager cacheCheckManager;
        synchronized (CacheCheckManager.class) {
            if (f7822a == null) {
                f7822a = new CacheCheckManager(context.getApplicationContext());
            }
            cacheCheckManager = f7822a;
        }
        return cacheCheckManager;
    }

    public void a(O.e eVar, IGarbageScanCallback iGarbageScanCallback) {
        Log.d("CacheCheckManager", "startScan");
        this.f7825d.a("com.miui.cleanmaster.action.CHECK_GARBAGE_CHECK", "com.miui.cleanmaster", (a.C0027a) new C0556c(this, iGarbageScanCallback, eVar));
    }
}
