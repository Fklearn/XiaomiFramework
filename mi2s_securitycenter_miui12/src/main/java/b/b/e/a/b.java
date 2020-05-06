package b.b.e.a;

import android.content.Context;
import android.provider.Settings;

public class b {
    public static String a(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), "key_miui_sos_emergency_contacts");
    }

    public static boolean b(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "key_miui_sos_call_log_enable", 0) == 1;
    }

    public static boolean c(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "key_miui_sos_calling_enable", 1) == 1;
    }

    public static boolean d(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "key_miui_sos_enable", 0) == 1;
    }
}
