package com.market.sdk;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.market.sdk.utils.a;
import miui.os.Build;

public class p {

    /* renamed from: a  reason: collision with root package name */
    private static volatile p f2240a;

    /* renamed from: b  reason: collision with root package name */
    public static final String f2241b = c();

    /* renamed from: c  reason: collision with root package name */
    private Context f2242c;

    /* renamed from: d  reason: collision with root package name */
    private final String f2243d = "com.xiaomi.market.ui.AppDetailActivity";
    public final String e = "com.xiaomi.market.data.MarketService";
    private final String f = "com.xiaomi.market.ui.UserAgreementActivity";

    private p(Context context) {
        this.f2242c = context.getApplicationContext();
    }

    @Deprecated
    public static p a(Context context) {
        if (context == null) {
            Log.e("MarketManager", "context is null");
            return null;
        }
        a.a(context.getApplicationContext());
        if (f2240a == null) {
            synchronized (p.class) {
                if (f2240a == null) {
                    f2240a = new p(context);
                }
            }
        }
        return f2240a;
    }

    public static p b() {
        return a(a.a());
    }

    public static String c() {
        try {
            return Build.IS_INTERNATIONAL_BUILD ? "com.xiaomi.discover" : "com.xiaomi.market";
        } catch (Throwable unused) {
            return "com.xiaomi.market";
        }
    }

    public i a() {
        return i.a((Application) this.f2242c.getApplicationContext());
    }
}
