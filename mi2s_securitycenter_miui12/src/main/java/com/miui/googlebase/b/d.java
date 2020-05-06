package com.miui.googlebase.b;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import com.miui.googlebase.GoogleBaseAppInstallService;
import com.miui.googlebase.a;
import java.io.File;
import java.util.List;

public class d {
    public static File a(List<GoogleBaseAppInstallService.a> list, String str) {
        for (GoogleBaseAppInstallService.a next : list) {
            if (next != null && str.contains(next.c())) {
                File file = new File(a.f5435a + "/" + next.a() + ".apk");
                StringBuilder sb = new StringBuilder();
                sb.append("apk filepath");
                sb.append(file);
                Log.i("GoogleBaseApp", sb.toString());
                if (file.exists()) {
                    return file;
                }
                Log.d("GoogleBaseApp", "file not found!");
            }
        }
        return null;
    }

    public static boolean a(Context context, String str) {
        try {
            return context.getPackageManager().getApplicationInfo(str, 0) != null;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }
}
