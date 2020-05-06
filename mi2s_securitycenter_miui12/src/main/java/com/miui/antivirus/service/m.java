package com.miui.antivirus.service;

import android.util.Log;
import b.b.b.i;
import com.miui.antivirus.service.VirusAutoUpdateJobService;
import com.miui.guardprovider.VirusObserver;
import com.miui.guardprovider.aidl.UpdateInfo;
import com.miui.guardprovider.b;

class m extends VirusObserver {

    /* renamed from: c  reason: collision with root package name */
    final /* synthetic */ i f2906c;

    /* renamed from: d  reason: collision with root package name */
    final /* synthetic */ b f2907d;
    final /* synthetic */ VirusAutoUpdateJobService.a e;

    m(VirusAutoUpdateJobService.a aVar, i iVar, b bVar) {
        this.e = aVar;
        this.f2906c = iVar;
        this.f2907d = bVar;
    }

    public void a(UpdateInfo updateInfo) {
        if (!"MiEngine".equals(updateInfo.engineName)) {
            VirusAutoUpdateJobService.this.f2879a.post(new l(this, updateInfo));
        }
    }

    public void p(int i) {
        Log.i("VirusAutoUpdateJobService", "onUpdateFinished : " + i);
        this.e.f2880a.a();
        this.f2907d.a();
    }
}
