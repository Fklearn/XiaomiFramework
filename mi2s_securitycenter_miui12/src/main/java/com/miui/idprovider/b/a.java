package com.miui.idprovider.b;

import android.content.Context;
import java.io.Serializable;

public class a implements Serializable {

    /* renamed from: a  reason: collision with root package name */
    private static volatile b f5609a;

    public static b a(Context context) {
        if (f5609a == null) {
            synchronized (a.class) {
                if (f5609a == null) {
                    f5609a = new b(context.getApplicationContext());
                }
            }
        }
        return f5609a;
    }
}
