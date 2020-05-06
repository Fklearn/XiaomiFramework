package com.miui.securityscan.f;

import android.os.Build;
import android.view.Window;
import java.lang.reflect.Method;

public class c {

    /* renamed from: a  reason: collision with root package name */
    protected static Method f7701a;

    static {
        Class<Window> cls = Window.class;
        try {
            f7701a = cls.getDeclaredMethod("setExtraFlags", new Class[]{Integer.TYPE, Integer.TYPE});
        } catch (Exception e) {
            f7701a = null;
            e.printStackTrace();
        }
    }

    public static void a(Window window) {
        if (Build.VERSION.SDK_INT >= 23) {
            window.addFlags(Integer.MIN_VALUE);
            window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility() | 8192);
            return;
        }
        Method method = f7701a;
        if (method != null) {
            try {
                method.invoke(window, new Object[]{17, 17});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void b(Window window) {
        if (Build.VERSION.SDK_INT >= 23) {
            window.addFlags(Integer.MIN_VALUE);
            window.getDecorView().setSystemUiVisibility(window.getDecorView().getSystemUiVisibility() & -8193);
            return;
        }
        Method method = f7701a;
        if (method != null) {
            try {
                method.invoke(window, new Object[]{1, 17});
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
