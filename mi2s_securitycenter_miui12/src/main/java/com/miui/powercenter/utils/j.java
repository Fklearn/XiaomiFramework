package com.miui.powercenter.utils;

import android.content.ContentResolver;
import android.util.Log;
import b.b.c.j.B;
import b.b.o.g.e;
import com.miui.securitycenter.Application;
import com.miui.support.provider.f;

public class j {
    public static int a() {
        String str;
        try {
            str = (String) e.a(Class.forName("android.provider.MiuiSettings$Secure"), "SECOND_USER_ID", String.class);
        } catch (Exception e) {
            Log.e("MultiUserHelper", "getSecondUserId exception: ", e);
            str = null;
        }
        return f.a(Application.d().getContentResolver(), str, 0);
    }

    public static int a(int i) {
        return i / DefaultOggSeeker.MATCH_BYTE_RANGE;
    }

    public static boolean b() {
        return B.c() == B.j();
    }

    public static boolean c() {
        try {
            return ((Boolean) e.a(Class.forName("android.provider.MiuiSettings$Secure"), Boolean.TYPE, "isSecureSpace", (Class<?>[]) new Class[]{ContentResolver.class}, Application.d().getContentResolver())).booleanValue();
        } catch (Exception e) {
            Log.d("MultiUserHelper", "isSecureSpace exception: ", e);
            return false;
        }
    }
}
