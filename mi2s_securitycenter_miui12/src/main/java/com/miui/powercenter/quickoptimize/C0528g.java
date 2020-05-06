package com.miui.powercenter.quickoptimize;

import android.os.IBinder;
import android.util.Log;
import b.b.c.f.a;
import com.miui.powercenter.quickoptimize.C0530i;
import com.miui.securitycenter.memory.IMemoryCheck;
import com.miui.securitycenter.memory.MemoryModel;
import java.util.List;

/* renamed from: com.miui.powercenter.quickoptimize.g  reason: case insensitive filesystem */
class C0528g implements a.C0027a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0530i.a f7220a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ C0530i f7221b;

    C0528g(C0530i iVar, C0530i.a aVar) {
        this.f7221b = iVar;
        this.f7220a = aVar;
    }

    public boolean a(IBinder iBinder) {
        IMemoryCheck a2 = IMemoryCheck.Stub.a(iBinder);
        if (a2 != null) {
            new C0527f(this, a2).execute(new Void[0]);
        } else {
            Log.e("MemoryCheckManager", "memoryCheck == null");
            this.f7220a.a((List<MemoryModel>) null);
        }
        return false;
    }
}
