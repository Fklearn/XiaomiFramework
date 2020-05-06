package com.miui.cleanmaster;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

public class f {
    public static void a(Context context, Object obj) {
        Log.i("CMInstallHelper", "start install CleanMaster");
        if (g.a()) {
            InstallCallbackV28.a(context, obj);
        } else {
            InstallCallBack.a(context, obj);
        }
    }

    public static boolean a(Context context) {
        try {
            return context.getPackageManager().getPackageInfo("com.miui.cleanmaster", 0) != null;
        } catch (PackageManager.NameNotFoundException unused) {
        }
    }
}
