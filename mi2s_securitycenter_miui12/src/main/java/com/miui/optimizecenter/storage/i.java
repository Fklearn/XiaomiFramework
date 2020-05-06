package com.miui.optimizecenter.storage;

import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageStats;
import android.util.Log;

class i extends IPackageStatsObserver.Stub {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppSystemDataManager f5744a;

    i(AppSystemDataManager appSystemDataManager) {
        this.f5744a = appSystemDataManager;
    }

    public void onGetStatsCompleted(PackageStats packageStats, boolean z) {
        if (packageStats != null) {
            long j = u.APP_DATA.a().f5755c + packageStats.dataSize + packageStats.externalDataSize + packageStats.externalMediaSize + packageStats.externalCodeSize + packageStats.externalObbSize + packageStats.codeSize + packageStats.externalCacheSize + packageStats.cacheSize;
            Log.i("StorageScanTask", "appData space 26以下 = " + j);
            s a2 = s.a(this.f5744a.f5687c);
            a2.a(u.APP_DATA, j);
            a2.e();
            u uVar = u.OTHER;
            a2.a(uVar, uVar.a().f5755c);
        }
    }
}
