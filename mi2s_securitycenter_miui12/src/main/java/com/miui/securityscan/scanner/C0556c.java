package com.miui.securityscan.scanner;

import android.os.IBinder;
import b.b.c.f.a;
import b.b.c.j.d;
import com.miui.optimizecenter.garbagecheck.IGarbageCheck;
import com.miui.optimizecenter.garbagecheck.IGarbageScanCallback;
import com.miui.securityscan.scanner.O;
import miui.util.Log;

/* renamed from: com.miui.securityscan.scanner.c  reason: case insensitive filesystem */
class C0556c implements a.C0027a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ IGarbageScanCallback f7882a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ O.e f7883b;

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ CacheCheckManager f7884c;

    C0556c(CacheCheckManager cacheCheckManager, IGarbageScanCallback iGarbageScanCallback, O.e eVar) {
        this.f7884c = cacheCheckManager;
        this.f7882a = iGarbageScanCallback;
        this.f7883b = eVar;
    }

    public boolean a(IBinder iBinder) {
        Log.d("CacheCheckManager", "startScan: onGetBinder(IBinder service) callback");
        d.a(new C0555b(this, IGarbageCheck.Stub.a(iBinder)));
        return false;
    }
}
