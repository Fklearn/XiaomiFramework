package b.b.b.d;

import android.content.Context;
import miui.provider.ExtraSettings;

public final class a {
    public static void a(Context context, boolean z) {
        ExtraSettings.System.putBoolean(context.getContentResolver(), "virus_scan_install", z);
    }

    public static boolean a(Context context) {
        return ExtraSettings.System.getBoolean(context.getContentResolver(), "virus_scan_install", true);
    }
}
