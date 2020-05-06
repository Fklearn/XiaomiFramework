package com.miui.powercenter.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import b.b.c.j.r;
import b.c.a.b.a.e;
import b.c.a.b.a.i;
import b.c.a.b.e.a;
import b.c.a.b.e.c;
import com.miui.securitycenter.R;
import miui.content.res.IconCustomizer;

public class b {
    public static Bitmap a(String str) {
        return r.a("pkg_icon://" + str, r.e);
    }

    private static e a(ImageView imageView) {
        int width = imageView.getWidth();
        if (width == 0) {
            width = imageView.getResources().getDimensionPixelSize(R.dimen.list_item_app_icon_size);
        }
        return new e(width, width);
    }

    public static String a(Context context, String str) {
        PackageManager packageManager = context.getPackageManager();
        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(str, 0);
            if (applicationInfo != null) {
                return packageManager.getApplicationLabel(applicationInfo).toString();
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("", "", e);
        }
        return str;
    }

    public static void a(ImageView imageView, int i) {
        imageView.setImageDrawable(IconCustomizer.generateIconStyleDrawable(imageView.getResources().getDrawable(i)));
    }

    public static void a(ImageView imageView, String str) {
        String str2 = "pkg_icon://" + str;
        imageView.setTag(str2);
        r.a(str2, (a) new c(a(imageView), i.CROP), r.h, (b.c.a.b.f.a) new a(imageView));
    }

    public static boolean b(Context context, String str) {
        if (TextUtils.isEmpty(str)) {
            return true;
        }
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(str, 0);
            if (applicationInfo == null) {
                return false;
            }
            if ((applicationInfo.flags & 1) != 0) {
                return true;
            }
            return applicationInfo.uid >= 0 && applicationInfo.uid < 10000;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("", "", e);
        }
    }
}
