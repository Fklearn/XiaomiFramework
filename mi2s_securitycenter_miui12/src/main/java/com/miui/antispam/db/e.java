package com.miui.antispam.db;

import android.content.Context;
import java.io.Serializable;

public class e implements Serializable {

    /* renamed from: a  reason: collision with root package name */
    private static volatile c f2350a;

    public static c a(Context context) {
        if (f2350a == null) {
            synchronized (e.class) {
                if (f2350a == null) {
                    f2350a = new c(context.getApplicationContext());
                }
            }
        }
        return f2350a;
    }
}
