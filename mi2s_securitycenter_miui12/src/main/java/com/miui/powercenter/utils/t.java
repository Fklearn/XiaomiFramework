package com.miui.powercenter.utils;

import android.content.Context;
import android.graphics.Typeface;

public class t {

    /* renamed from: a  reason: collision with root package name */
    private static Typeface f7319a;

    /* renamed from: b  reason: collision with root package name */
    private static Typeface f7320b;

    /* renamed from: c  reason: collision with root package name */
    private static Typeface f7321c;

    public static synchronized Typeface a() {
        Typeface typeface;
        synchronized (t.class) {
            if (f7321c == null) {
                f7321c = Typeface.create("mipro", 0);
            }
            typeface = f7321c;
        }
        return typeface;
    }

    public static synchronized Typeface a(Context context) {
        Typeface typeface;
        synchronized (t.class) {
            if (f7320b == null) {
                f7320b = Typeface.createFromAsset(context.getAssets(), "fonts/Mitype-SemiBold.otf");
            }
            typeface = f7320b;
        }
        return typeface;
    }

    public static synchronized Typeface b(Context context) {
        Typeface typeface;
        synchronized (t.class) {
            if (f7319a == null) {
                f7319a = Typeface.createFromAsset(context.getAssets(), "fonts/Mitype2018-60.otf");
            }
            typeface = f7319a;
        }
        return typeface;
    }
}
