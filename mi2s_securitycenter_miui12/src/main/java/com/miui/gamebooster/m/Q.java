package com.miui.gamebooster.m;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import b.b.c.j.i;
import b.b.o.g.e;
import java.lang.reflect.Method;
import java.util.List;

public class Q {
    public static Rect a(View view) {
        try {
            Method declaredMethod = Class.forName("android.view.View").getDeclaredMethod("getRootWindowInsets", new Class[0]);
            declaredMethod.setAccessible(true);
            Object invoke = declaredMethod.invoke(view, new Object[0]);
            if (invoke == null) {
                return null;
            }
            Method declaredMethod2 = Class.forName("android.view.WindowInsets").getDeclaredMethod("getDisplayCutout", new Class[0]);
            declaredMethod2.setAccessible(true);
            Object invoke2 = declaredMethod2.invoke(invoke, new Object[0]);
            if (invoke2 == null) {
                return null;
            }
            Method declaredMethod3 = Class.forName("android.view.DisplayCutout").getDeclaredMethod("getBoundingRects", new Class[0]);
            declaredMethod3.setAccessible(true);
            List list = (List) declaredMethod3.invoke(invoke2, new Object[0]);
            if (list == null || list.isEmpty()) {
                return null;
            }
            return (Rect) list.get(0);
        } catch (Exception unused) {
            return null;
        }
    }

    public static void a(@NonNull Activity activity) {
        Window window = activity.getWindow();
        if (window != null) {
            if (Build.VERSION.SDK_INT >= 28 && i.e() && !a((Context) activity)) {
                try {
                    WindowManager.LayoutParams attributes = window.getAttributes();
                    e.a((Object) attributes, "layoutInDisplayCutoutMode", (Object) 1);
                    window.setAttributes(attributes);
                } catch (Exception e) {
                    Log.i("GameBoosterReflectUtils", e.toString());
                }
            } else if (Build.VERSION.SDK_INT >= 27) {
                Class<Window> cls = Window.class;
                try {
                    cls.getMethod("addExtraFlags", new Class[]{Integer.TYPE}).invoke(window, new Object[]{1280});
                } catch (Exception unused) {
                    Log.i("NotchUtils", "addExtraFlags not found.");
                }
            }
        }
    }

    public static void a(Activity activity, int i) {
        Window window;
        if (activity != null && !activity.isDestroyed() && (window = activity.getWindow()) != null && Build.VERSION.SDK_INT >= 28 && i.e()) {
            try {
                WindowManager.LayoutParams attributes = window.getAttributes();
                e.a((Object) attributes, "layoutInDisplayCutoutMode", (Object) Integer.valueOf(i));
                window.setAttributes(attributes);
            } catch (Exception e) {
                Log.i("GameBoosterReflectUtils", e.toString());
            }
        }
    }

    public static boolean a(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "force_black", 0) == 1;
    }
}
