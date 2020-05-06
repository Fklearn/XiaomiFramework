package com.miui.powercenter.quickoptimize;

import android.os.AsyncTask;
import android.os.RemoteException;
import android.util.Log;
import com.miui.securitycenter.memory.IMemoryCheck;
import com.miui.securitycenter.memory.MemoryModel;
import java.util.List;

/* renamed from: com.miui.powercenter.quickoptimize.f  reason: case insensitive filesystem */
class C0527f extends AsyncTask<Void, Void, Void> {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ IMemoryCheck f7218a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ C0528g f7219b;

    C0527f(C0528g gVar, IMemoryCheck iMemoryCheck) {
        this.f7219b = gVar;
        this.f7218a = iMemoryCheck;
    }

    /* access modifiers changed from: protected */
    /* renamed from: a */
    public Void doInBackground(Void... voidArr) {
        try {
            this.f7218a.a(new C0526e(this));
        } catch (RemoteException e) {
            Log.e("MemoryCheckManager", "startScan", e);
            this.f7219b.f7220a.a((List<MemoryModel>) null);
        }
        this.f7219b.f7221b.f7226c.b("miui.intent.action.MEMORY_CHECK_SERVICE");
        return null;
    }
}
