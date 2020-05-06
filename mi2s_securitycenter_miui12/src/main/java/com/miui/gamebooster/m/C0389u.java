package com.miui.gamebooster.m;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

/* renamed from: com.miui.gamebooster.m.u  reason: case insensitive filesystem */
public class C0389u {

    /* renamed from: a  reason: collision with root package name */
    private static final boolean f4520a = "beryllium".equals(Build.DEVICE);

    public static boolean a() {
        return f4520a && miui.os.Build.IS_INTERNATIONAL_BUILD;
    }

    public static boolean a(Context context) {
        return f4520a && miui.os.Build.IS_INTERNATIONAL_BUILD && a(context, b());
    }

    private static boolean a(Context context, Intent intent) {
        return (intent == null || context.getApplicationContext().getPackageManager().resolveActivity(intent, 0) == null) ? false : true;
    }

    private static Intent b() {
        Intent intent = new Intent("com.xiaomi.gameboosterglobal.action.ACCESS_MAIN_ACTIVITY");
        intent.putExtra("gamebooster_entrance", "securityCenter");
        return intent;
    }

    public static void b(Context context) {
        if (context != null) {
            context.startActivity(b());
        }
    }
}
