package com.miui.powercenter.quickoptimize;

import android.os.IBinder;
import android.util.Log;
import b.b.c.f.a;
import com.miui.powercenter.quickoptimize.C0530i;
import com.miui.securitycenter.memory.IMemoryCheck;

/* renamed from: com.miui.powercenter.quickoptimize.h  reason: case insensitive filesystem */
class C0529h implements a.C0027a {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ C0530i.b f7222a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ C0530i f7223b;

    C0529h(C0530i iVar, C0530i.b bVar) {
        this.f7223b = iVar;
        this.f7222a = bVar;
    }

    public boolean a(IBinder iBinder) {
        IMemoryCheck a2 = IMemoryCheck.Stub.a(iBinder);
        if (a2 != null) {
            try {
                this.f7222a.a(a2.t());
            } catch (Exception e) {
                Log.e("MemoryCheckManager", "getWhiteList", e);
            }
        }
        this.f7223b.f7226c.b("miui.intent.action.MEMORY_CHECK_SERVICE");
        return false;
    }
}
