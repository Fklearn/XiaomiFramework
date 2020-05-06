package com.miui.gamebooster.m;

import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import com.miui.securitycenter.Application;
import java.io.File;

public class I {
    public static void a(String str) {
        if (!TextUtils.isEmpty(str)) {
            Application.d().getApplicationContext().sendBroadcast(new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE", Uri.fromFile(new File(str))));
        }
    }
}
