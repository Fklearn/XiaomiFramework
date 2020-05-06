package com.miui.cleanmaster;

import android.content.Context;
import android.content.pm.IPackageInstallObserver;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import b.b.o.g.e;

public class InstallCallBack extends IPackageInstallObserver.Stub {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public i f3735a;

    public InstallCallBack(i iVar) {
        this.f3735a = iVar;
    }

    public static void a(Context context, Object obj) {
        try {
            e.a(Class.forName("miui.content.pm.PreloadedAppPolicy"), (Class) null, "installPreloadedDataApp", (Class<?>[]) new Class[]{Context.class, String.class, IPackageInstallObserver.class, Integer.TYPE}, context.getApplicationContext(), "com.miui.cleanmaster", (IPackageInstallObserver) obj, 1);
        } catch (Exception e) {
            Log.e("InstallCallBack", "installCleanMaster exception: ", e);
        }
    }

    public void packageInstalled(String str, int i) {
        new Handler(Looper.getMainLooper()).postDelayed(new j(this, i), 500);
    }
}
