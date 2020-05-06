package com.miui.cleanmaster;

import android.content.Context;
import android.content.IntentFilter;
import com.miui.networkassistant.config.Constants;

public class h {

    /* renamed from: a  reason: collision with root package name */
    private static h f3751a;

    /* renamed from: b  reason: collision with root package name */
    private boolean f3752b;

    private h() {
    }

    public static h a() {
        if (f3751a == null) {
            f3751a = new h();
        }
        return f3751a;
    }

    public void a(Context context) {
        if (e.a(context)) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Constants.System.ACTION_USER_PRESENT);
            context.registerReceiver(new c(), intentFilter);
            b.a().a(context);
            this.f3752b = true;
        }
    }

    public boolean b() {
        return this.f3752b;
    }
}
