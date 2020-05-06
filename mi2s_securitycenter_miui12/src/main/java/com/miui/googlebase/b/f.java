package com.miui.googlebase.b;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class f {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static AtomicInteger f5448a = new AtomicInteger();

    public static void a(Context context, String str) {
        try {
            if (Build.VERSION.SDK_INT < 28) {
                Class<?> cls = context.getPackageManager().getClass();
                Class[] clsArr = new Class[4];
                clsArr[0] = Uri.class;
                clsArr[1] = Class.forName("android.content.pm.IPackageInstallObserver");
                clsArr[2] = Integer.TYPE;
                clsArr[3] = String.class;
                cls.getMethod("installPackage", clsArr).invoke(context.getPackageManager(), new Object[]{Uri.fromFile(new File(str)), null, context.getPackageManager().getClass().getField("INSTALL_REPLACE_EXISTING").get((Object) null), null});
                Log.d("Utils", "start install apk " + str);
                return;
            }
            new Thread(new e(context, str)).start();
        } catch (Exception e) {
            Log.d("Utils", "installApkSilently.", e);
        }
    }

    public static boolean a(Context context, int i) {
        return true;
    }
}
