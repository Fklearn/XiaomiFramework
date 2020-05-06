package b.b.j.d;

import android.content.Context;
import android.provider.Settings;

public class b {
    public static long a(Context context) {
        return Settings.Secure.getLong(context.getContentResolver(), "key_garbage_qq_size", 0);
    }

    public static void a(boolean z) {
        com.miui.common.persistence.b.b("pm_qq_subscript_visible", z);
    }

    public static boolean a() {
        return com.miui.common.persistence.b.a("pm_qq_subscript_visible", true);
    }

    public static long b(Context context) {
        return Settings.Secure.getLong(context.getContentResolver(), "key_garbage_wechat_size", 0);
    }

    public static void b(boolean z) {
        com.miui.common.persistence.b.b("pm_wechat_subscript_visible", z);
    }

    public static boolean b() {
        return com.miui.common.persistence.b.a("pm_wechat_subscript_visible", true);
    }
}
