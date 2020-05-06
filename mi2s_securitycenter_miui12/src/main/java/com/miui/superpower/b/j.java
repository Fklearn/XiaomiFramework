package com.miui.superpower.b;

import android.content.Context;
import android.graphics.Typeface;

public class j {

    /* renamed from: a  reason: collision with root package name */
    private static Typeface f8089a;

    /* renamed from: b  reason: collision with root package name */
    private static Typeface f8090b;

    public static synchronized Typeface a(Context context) {
        Typeface typeface;
        synchronized (j.class) {
            if (f8090b == null) {
                f8090b = Typeface.createFromAsset(context.getAssets(), "fonts/Mitype2018-80.otf");
            }
            typeface = f8090b;
        }
        return typeface;
    }

    public static synchronized Typeface b(Context context) {
        Typeface typeface;
        synchronized (j.class) {
            if (f8089a == null) {
                f8089a = Typeface.createFromAsset(context.getAssets(), "fonts/Mitype2018-clock.otf");
            }
            typeface = f8089a;
        }
        return typeface;
    }
}
