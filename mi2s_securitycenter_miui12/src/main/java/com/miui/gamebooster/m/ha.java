package com.miui.gamebooster.m;

import android.text.TextUtils;

public class ha {

    /* renamed from: a  reason: collision with root package name */
    private static StringBuilder f4493a;

    public static String a(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        if (str.startsWith("http")) {
            return str;
        }
        StringBuilder sb = f4493a;
        if (sb == null) {
            f4493a = new StringBuilder();
        } else {
            sb.setLength(0);
        }
        StringBuilder sb2 = f4493a;
        sb2.append("http://video.kts.g.mi.com/");
        sb2.append(str);
        return sb2.toString();
    }
}
