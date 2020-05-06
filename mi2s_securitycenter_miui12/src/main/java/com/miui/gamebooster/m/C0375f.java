package com.miui.gamebooster.m;

import android.content.Context;
import android.content.Intent;

/* renamed from: com.miui.gamebooster.m.f  reason: case insensitive filesystem */
public class C0375f {

    /* renamed from: a  reason: collision with root package name */
    private static final boolean f4484a = C0388t.a();

    public static void a(Context context) {
        if (context != null) {
            Intent b2 = b();
            if (a(context, b2)) {
                try {
                    context.startActivity(b2);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static boolean a() {
        return f4484a;
    }

    private static boolean a(Context context, Intent intent) {
        return (intent == null || context.getApplicationContext().getPackageManager().resolveActivity(intent, 0) == null) ? false : true;
    }

    private static Intent b() {
        Intent intent = new Intent("com.blackshark.action.SHARKSPACE");
        intent.putExtra("gamebooster_entrance", "securityCenter");
        intent.addFlags(268435456);
        return intent;
    }
}
