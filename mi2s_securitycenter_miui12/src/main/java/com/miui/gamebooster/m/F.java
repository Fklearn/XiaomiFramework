package com.miui.gamebooster.m;

import android.text.TextUtils;

public class F {
    public static boolean a(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return str.contains("com.tencent.tmgp.sgame") || str.contains("com.tencent.tmgp.pubgmhd");
    }
}
