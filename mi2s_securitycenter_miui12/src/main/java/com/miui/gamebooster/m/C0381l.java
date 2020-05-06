package com.miui.gamebooster.m;

import android.content.Context;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.j.x;

/* renamed from: com.miui.gamebooster.m.l  reason: case insensitive filesystem */
public class C0381l {
    public static boolean a(Context context) {
        return x.e(context, "com.miui.cleanmaster") >= 150;
    }

    public static boolean b(Context context) {
        return true;
    }

    public static boolean c(Context context) {
        try {
            if (UserHandle.myUserId() == 0) {
                return false;
            }
            String str = (String) C0384o.b("android.provider.MiuiSettings$Secure", "KID_USER_ID");
            if (TextUtils.isEmpty(str)) {
                Log.i("FeatureUtil", "no kid space");
                return false;
            }
            int a2 = C0384o.a(context.getContentResolver(), str, (int) UserHandle.USER_NULL, 0);
            return a2 != -10000 && a2 == UserHandle.myUserId();
        } catch (Exception e) {
            Log.e("FeatureUtil", "isInKidSpace: ", e);
            return false;
        }
    }
}
