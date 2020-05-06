package com.miui.permcenter.privacymanager.b;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import com.miui.permcenter.privacymanager.behaviorrecord.o;
import miui.os.Build;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private static boolean f6349a = false;

    /* renamed from: b  reason: collision with root package name */
    private static boolean f6350b = false;

    public static boolean a(@NonNull Context context) {
        if (f6349a) {
            return f6350b;
        }
        f6350b = Build.IS_INTERNATIONAL_BUILD && b(context) >= 124 && o.a(context);
        f6349a = true;
        return f6350b;
    }

    private static int b(@NonNull Context context) {
        try {
            return context.getPackageManager().getPackageInfo("com.lbe.security.miui", 0).versionCode;
        } catch (PackageManager.NameNotFoundException unused) {
            return -1;
        }
    }
}
