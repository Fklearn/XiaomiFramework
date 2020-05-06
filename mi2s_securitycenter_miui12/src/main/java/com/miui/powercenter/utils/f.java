package com.miui.powercenter.utils;

import android.text.TextUtils;
import android.util.Log;
import java.io.File;

public class f {
    public static boolean a(File file) {
        if (file == null || !file.exists()) {
            return false;
        }
        if (!file.isDirectory()) {
            return file.delete();
        }
        for (File a2 : file.listFiles()) {
            if (!a(a2)) {
                return false;
            }
        }
        return file.delete();
    }

    public static boolean a(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return a(new File(str));
    }

    public static boolean a(String str, String str2) {
        File file = new File(str2);
        if (!file.exists() || file.isDirectory()) {
            Log.d(str, "ConfigReady:false");
            return false;
        }
        Log.d(str, "ConfigReady:true");
        return true;
    }
}
