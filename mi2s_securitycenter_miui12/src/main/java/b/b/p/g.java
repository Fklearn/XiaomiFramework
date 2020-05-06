package b.b.p;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;

public class g {

    /* renamed from: a  reason: collision with root package name */
    private static Context f1907a;

    /* renamed from: b  reason: collision with root package name */
    private static h f1908b = new h();

    public static void a() {
        h hVar;
        Context context = f1907a;
        if (!(context == null || (hVar = f1908b) == null)) {
            context.unregisterReceiver(hVar);
        }
        Context context2 = f1907a;
        if (context2 != null) {
            f.a(context2).b();
        }
        f1908b = null;
        f1907a = null;
    }

    public static void a(Context context) {
        if (context != null) {
            f1907a = context.getApplicationContext();
            if (f1907a == null) {
                f1907a = context;
            }
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("miui.intent.action.ad.UNIFIED_AD_UPDATING");
            f1907a.registerReceiver(f1908b, intentFilter, "miui.permission.AD_COMMON_PERMISSION_SYSTEM_OR_SIGNATURE", (Handler) null);
            return;
        }
        throw new IllegalArgumentException("the context must not be null when initing the UnifiedAdManager!");
    }
}
