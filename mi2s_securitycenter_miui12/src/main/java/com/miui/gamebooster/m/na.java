package com.miui.gamebooster.m;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import java.util.Locale;

public class na {
    public static int a() {
        DisplayMetrics displayMetrics;
        Resources system = Resources.getSystem();
        if (system == null || (displayMetrics = system.getDisplayMetrics()) == null) {
            return 0;
        }
        return Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    public static int a(Context context) {
        int rotation = ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getRotation();
        if (rotation == 0) {
            return 0;
        }
        if (rotation == 1) {
            return 90;
        }
        if (rotation != 2) {
            return rotation != 3 ? 0 : 270;
        }
        return 180;
    }

    public static void a(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(5894);
    }

    public static int b() {
        DisplayMetrics displayMetrics;
        Resources system = Resources.getSystem();
        if (system == null || (displayMetrics = system.getDisplayMetrics()) == null) {
            return 0;
        }
        return Math.min(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:4:0x0007, code lost:
        r4 = r4.getResources();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int b(android.content.Context r4) {
        /*
            r0 = 0
            boolean r1 = f(r4)     // Catch:{ Exception -> 0x001c }
            if (r1 != 0) goto L_0x001c
            android.content.res.Resources r4 = r4.getResources()     // Catch:{ Exception -> 0x001c }
            java.lang.String r1 = "navigation_bar_height"
            java.lang.String r2 = "dimen"
            java.lang.String r3 = "android"
            int r1 = r4.getIdentifier(r1, r2, r3)     // Catch:{ Exception -> 0x001c }
            if (r1 <= 0) goto L_0x001c
            int r4 = r4.getDimensionPixelSize(r1)     // Catch:{ Exception -> 0x001c }
            r0 = r4
        L_0x001c:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.gamebooster.m.na.b(android.content.Context):int");
    }

    public static void b(@Nullable Activity activity) {
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            WindowManager.LayoutParams attributes = activity.getWindow().getAttributes();
            attributes.flags ^= 1024;
            activity.getWindow().setAttributes(attributes);
        }
    }

    public static int c(Context context) {
        Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        int width = defaultDisplay.getWidth();
        int height = defaultDisplay.getHeight();
        return height > width ? width : height;
    }

    public static void c(@Nullable Activity activity) {
        if (activity != null && !activity.isFinishing() && !activity.isDestroyed()) {
            WindowManager.LayoutParams attributes = activity.getWindow().getAttributes();
            attributes.flags ^= 134217728;
            activity.getWindow().setAttributes(attributes);
        }
    }

    public static boolean c() {
        return TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1;
    }

    public static int d(Context context) {
        Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        int width = defaultDisplay.getWidth();
        int height = defaultDisplay.getHeight();
        return height > width ? height : width;
    }

    public static int e(Context context) {
        Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        int width = defaultDisplay.getWidth();
        int height = defaultDisplay.getHeight();
        return height > width ? height : width;
    }

    public static boolean f(Context context) {
        Boolean bool = (Boolean) C0384o.a("android.provider.MiuiSettings$Global", "getBoolean", context.getContentResolver(), (String) C0384o.b("android.provider.MiuiSettings$Global", "FORCE_FSG_NAV_BAR"));
        if (bool != null) {
            return bool.booleanValue();
        }
        return false;
    }

    public static int g(Context context) {
        Display defaultDisplay = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        return defaultDisplay.getHeight() > defaultDisplay.getWidth() ? 0 : 1;
    }
}
