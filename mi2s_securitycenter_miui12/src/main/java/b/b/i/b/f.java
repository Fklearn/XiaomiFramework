package b.b.i.b;

import android.content.Context;
import com.miui.securitycenter.R;

public class f {
    public static String a(Context context, long j, String str) {
        if (context == null) {
            return "";
        }
        float f = (float) j;
        int i = R.string.size_byte;
        if (((double) f) > 900.0d) {
            i = R.string.size_kilo_byte;
            f /= 1000.0f;
        }
        if (((double) f) > 900.0d) {
            i = R.string.size_mega_byte;
            f /= 1000.0f;
        }
        if (((double) f) > 900.0d) {
            i = R.string.size_giga_byte;
            f /= 1000.0f;
        }
        if (((double) f) > 900.0d) {
            i = R.string.size_tera_byte;
            f /= 1000.0f;
        }
        if (((double) f) > 900.0d) {
            i = R.string.size_peta_byte;
            f /= 1000.0f;
        }
        return context.getResources().getString(R.string.size_suffix, new Object[]{String.format(str, new Object[]{Float.valueOf(f)}), context.getString(i)});
    }
}
