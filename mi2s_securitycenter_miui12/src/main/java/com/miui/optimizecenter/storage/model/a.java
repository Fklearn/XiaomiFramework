package com.miui.optimizecenter.storage.model;

import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageStats;

class a extends IPackageStatsObserver.Stub {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ b f5758a;

    a(b bVar) {
        this.f5758a = bVar;
    }

    public void onGetStatsCompleted(PackageStats packageStats, boolean z) {
        if (packageStats != null) {
            b bVar = this.f5758a;
            bVar.l = packageStats.dataSize + packageStats.externalDataSize + packageStats.externalMediaSize;
            bVar.m = packageStats.externalCodeSize + packageStats.externalObbSize + packageStats.codeSize;
            bVar.p = packageStats.externalCacheSize + packageStats.cacheSize;
            bVar.n = bVar.l + bVar.m;
            bVar.k = bVar.n;
        }
    }
}
