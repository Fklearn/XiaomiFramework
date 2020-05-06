package com.miui.securityscan.c;

import miui.os.Build;

public class b {

    /* renamed from: a  reason: collision with root package name */
    public static final String f7626a;

    static {
        StringBuilder sb = new StringBuilder();
        sb.append(Build.IS_INTERNATIONAL_BUILD ? "https://adv.sec.intl.miui.com" : "https://adv.sec.miui.com");
        sb.append("/info/layout");
        f7626a = sb.toString();
    }
}
