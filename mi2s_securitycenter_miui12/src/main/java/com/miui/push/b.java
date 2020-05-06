package com.miui.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.text.TextUtils;
import android.util.Log;
import com.xiaomi.mipush.sdk.MiPushClient;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private static final String f7438a = "b";

    /* renamed from: b  reason: collision with root package name */
    private static b f7439b;

    /* renamed from: c  reason: collision with root package name */
    private static String f7440c;

    /* renamed from: d  reason: collision with root package name */
    private static int f7441d;
    private Context e;
    private BroadcastReceiver f = new a(this);

    private b(Context context) {
        this.e = context;
    }

    public static b a(Context context) {
        if (f7439b == null) {
            f7439b = new b(context.getApplicationContext());
        }
        return f7439b;
    }

    public static void b(Context context) {
        int i = f7441d;
        if (i <= 3) {
            f7441d = i + 1;
            if (TextUtils.isEmpty(f7440c)) {
                MiPushClient.registerPush(context, "2882303761517330652", "5691733067652");
                f7440c = MiPushClient.getRegId(context);
                if (TextUtils.isEmpty(f7440c)) {
                    Log.w(f7438a, "initMiPushClient.register failed");
                    return;
                }
            }
            Log.i(f7438a, "initMiPushClient.register success");
        }
    }

    public void a() {
        f7440c = MiPushClient.getRegId(this.e);
        b(this.e);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action_update_sc_network_allow");
        this.e.registerReceiver(this.f, intentFilter);
    }

    public void a(Context context, String str, String str2) {
        MiPushClient.subscribe(context, str, str2);
    }

    public void b(Context context, String str, String str2) {
        MiPushClient.setUserAccount(context, str, str2);
    }

    public void c(Context context, String str, String str2) {
        MiPushClient.unsubscribe(context, str, str2);
    }

    public void d(Context context, String str, String str2) {
        MiPushClient.unsetUserAccount(context, str, str2);
    }
}
