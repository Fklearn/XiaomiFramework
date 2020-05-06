package com.miui.securityscan.scanner;

import android.os.RemoteException;
import com.miui.optimizecenter.garbagecheck.IGarbageCheck;
import com.miui.securityscan.scanner.O;
import miui.util.Log;

/* renamed from: com.miui.securityscan.scanner.b  reason: case insensitive filesystem */
class C0555b implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ IGarbageCheck f7880a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ C0556c f7881b;

    C0555b(C0556c cVar, IGarbageCheck iGarbageCheck) {
        this.f7881b = cVar;
        this.f7880a = iGarbageCheck;
    }

    public void run() {
        if (this.f7880a != null) {
            try {
                Log.d("CacheCheckManager", "startScan: garbageCheck startScan");
                this.f7880a.a(this.f7881b.f7882a);
            } catch (RemoteException e) {
                O.e eVar = this.f7881b.f7883b;
                if (eVar != null) {
                    eVar.a();
                }
                Log.e("CacheCheckManager", "startScan: RemoteException", e);
            }
        }
    }
}
