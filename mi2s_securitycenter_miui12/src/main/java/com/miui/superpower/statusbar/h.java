package com.miui.superpower.statusbar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

public class h {

    /* renamed from: a  reason: collision with root package name */
    private static Context f8176a;

    private static Context a(Context context) {
        if (f8176a == null) {
            try {
                f8176a = context.createPackageContext("com.android.systemui", 3);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return f8176a;
    }

    public static Boolean a(Context context, String str, int i) {
        int identifier = a(context).getResources().getIdentifier(str, "bool", "com.android.systemui");
        return Boolean.valueOf(identifier == 0 ? context.getResources().getBoolean(i) : a(context).getResources().getBoolean(identifier));
    }

    public static Drawable b(Context context, String str, int i) {
        int identifier = a(context).getResources().getIdentifier(str, "drawable", "com.android.systemui");
        return identifier == 0 ? ContextCompat.getDrawable(context, i) : ContextCompat.getDrawable(a(context), identifier);
    }
}
