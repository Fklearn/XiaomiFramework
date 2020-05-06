package com.miui.monthreport;

import android.content.Context;
import b.b.c.j.i;
import miui.security.DigestUtils;
import miui.text.ExtraTextUtils;

public class f {
    public static String a(Context context) {
        return ExtraTextUtils.toHexReadable(DigestUtils.get(i.b(context) + "-" + "5fdd8678-cddf-4269-bb73-48187445bba7", "MD5"));
    }

    public static String b(Context context) {
        return ExtraTextUtils.toHexReadable(DigestUtils.get(i.c(context), "MD5"));
    }
}
