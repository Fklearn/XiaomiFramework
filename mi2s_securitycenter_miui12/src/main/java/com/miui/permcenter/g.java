package com.miui.permcenter;

import android.os.Build;
import android.os.UserHandle;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import com.miui.permcenter.compact.AppOpsUtilsCompat;
import miui.os.Build;

class g implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ StatusBarNotification f6107a;

    /* renamed from: b  reason: collision with root package name */
    final /* synthetic */ h f6108b;

    g(h hVar, StatusBarNotification statusBarNotification) {
        this.f6108b = hVar;
        this.f6107a = statusBarNotification;
    }

    public void run() {
        String packageName = this.f6107a.getPackageName();
        if (!Build.IS_INTERNATIONAL_BUILD && Build.VERSION.SDK_INT >= 29 && !AppOpsUtilsCompat.isXOptMode() && "com.android.permissioncontroller".equals(packageName)) {
            Log.d(j.f6168a, "cancel permission controller location check notification!");
            j jVar = this.f6108b.f6109a;
            jVar.a(jVar.f6170c, packageName, this.f6107a.getTag(), this.f6107a.getId(), UserHandle.CURRENT.getIdentifier());
        }
        if (!this.f6108b.f6109a.a(packageName)) {
            int i = this.f6107a.getNotification().flags;
            Log.v("onNotificationPostedCallBack", "flags=" + i);
            if ((i & 2) != 0) {
                if (!this.f6108b.f6109a.a(j.b(this.f6107a), packageName)) {
                    Log.v("onNotificationPostedCallBack", "2flags=" + i);
                    j jVar2 = this.f6108b.f6109a;
                    jVar2.a(jVar2.f6170c, packageName, this.f6107a.getTag(), this.f6107a.getId(), UserHandle.CURRENT.getIdentifier());
                }
            }
        }
    }
}
