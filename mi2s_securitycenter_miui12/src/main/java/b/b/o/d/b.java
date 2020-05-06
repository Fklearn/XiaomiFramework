package b.b.o.d;

import android.content.Context;
import b.b.c.j.B;
import com.miui.support.provider.f;
import miui.util.FeatureParser;

public class b {
    public static boolean a(Context context) {
        return FeatureParser.getBoolean("support_led_colorful", false);
    }

    public static boolean b(Context context) {
        return f.a(context.getContentResolver(), "breath_gamemode_enable", 0, B.j()) == 1;
    }

    public static boolean c(Context context) {
        return f.a(context.getContentResolver(), "light_turn_on", 1, B.j()) == 1;
    }

    public static boolean d(Context context) {
        return f.a(context.getContentResolver(), "breath_lucky_enable", 0, B.j()) == 1;
    }
}
