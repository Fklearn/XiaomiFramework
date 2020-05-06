package b.b.i.b;

import android.content.Context;
import android.content.res.Resources;
import com.miui.networkassistant.config.Constants;

public class d {
    public static int a() {
        Resources system = Resources.getSystem();
        if (system == null) {
            return 0;
        }
        return system.getDisplayMetrics().heightPixels;
    }

    public static int a(Context context, int i) {
        int identifier = context.getResources().getIdentifier("status_bar_height", "dimen", Constants.System.ANDROID_PACKAGE_NAME);
        return identifier > 0 ? context.getResources().getDimensionPixelSize(identifier) : i;
    }

    public static boolean b() {
        return a() > 1920;
    }
}
