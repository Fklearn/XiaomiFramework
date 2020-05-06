package com.miui.gamebooster.globalgame.util;

import android.util.Log;

public class b {
    public static void a(Object obj) {
        if (a(3) && obj != null) {
            Log.d("GlobalGameFeed", obj.toString());
        }
    }

    public static boolean a() {
        return a(3);
    }

    public static boolean a(int i) {
        return Log.isLoggable("GlobalGameFeed", i);
    }

    public static void b(Object obj) {
        if (a(6) && obj != null) {
            Log.e("GlobalGameFeed", obj.toString());
        }
    }

    public static void c(Object obj) {
        if (a(4) && obj != null) {
            Log.i("GlobalGameFeed", obj.toString());
        }
    }
}
