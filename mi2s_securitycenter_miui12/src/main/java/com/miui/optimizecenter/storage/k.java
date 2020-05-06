package com.miui.optimizecenter.storage;

import android.content.Context;
import b.b.c.j.x;
import com.miui.luckymoney.config.AppConstants;

public class k {
    public static boolean a(Context context) {
        return x.e(context, AppConstants.Package.PACKAGE_NAME_FILE) >= 4000;
    }
}
