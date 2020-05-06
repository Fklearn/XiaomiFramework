package com.xiaomi.analytics.a.a;

import android.util.Log;

public class m {
    public static String a(String str, String str2) {
        try {
            return (String) Class.forName("android.os.SystemProperties").getMethod("get", new Class[]{String.class, String.class}).invoke((Object) null, new Object[]{str, str2});
        } catch (Exception e) {
            Log.e(a.a("SystemProperties"), "get e", e);
            return str2;
        }
    }
}
