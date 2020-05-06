package com.miui.cleanmaster;

import android.content.Context;
import android.content.Intent;
import android.content.pm.IPackageInstallObserver2;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import b.b.o.g.e;

public class InstallCallbackV28 extends IPackageInstallObserver2.Stub {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public i f3736a;

    public InstallCallbackV28(i iVar) {
        this.f3736a = iVar;
    }

    public static void a(Context context, Object obj) {
        try {
            e.a(Class.forName("miui.content.pm.PreloadedAppPolicy"), (Class) null, "installPreloadedDataApp", (Class<?>[]) new Class[]{Context.class, String.class, IPackageInstallObserver2.class, Integer.TYPE}, context.getApplicationContext(), "com.miui.cleanmaster", (IPackageInstallObserver2) obj, 1);
        } catch (Exception e) {
            Log.e("InstallCallbackV28", "installCleanMaster exception: ", e);
        }
    }

    public void onPackageInstalled(String str, int i, String str2, Bundle bundle) {
        new Handler(Looper.getMainLooper()).postDelayed(new k(this, i), 500);
    }

    public void onUserActionRequired(Intent intent) {
    }
}
