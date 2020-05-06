package com.miui.appmanager;

import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageStats;
import b.b.o.g.d;

class w extends IPackageStatsObserver.Stub {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ AppManagerMainActivity f3691a;

    w(AppManagerMainActivity appManagerMainActivity) {
        this.f3691a = appManagerMainActivity;
    }

    public void onGetStatsCompleted(PackageStats packageStats, boolean z) {
        if (packageStats != null) {
            long j = packageStats.externalCodeSize + packageStats.externalObbSize + packageStats.externalDataSize + packageStats.externalMediaSize + packageStats.externalCacheSize + packageStats.cacheSize + packageStats.dataSize + packageStats.codeSize;
            this.f3691a.a(((Integer) d.a("AppManagerMainActivity", (Object) packageStats, "userHandle")).intValue(), packageStats.packageName, Long.valueOf(j));
        }
    }
}
