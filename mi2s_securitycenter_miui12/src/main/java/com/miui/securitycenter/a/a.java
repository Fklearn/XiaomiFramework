package com.miui.securitycenter.a;

import android.content.Context;
import java.io.Serializable;

public class a implements Serializable {

    /* renamed from: a  reason: collision with root package name */
    private static volatile b f7465a;

    public static b a(Context context) {
        if (f7465a == null) {
            synchronized (a.class) {
                if (f7465a == null) {
                    f7465a = new b(context.getApplicationContext());
                }
            }
        }
        return f7465a;
    }
}
