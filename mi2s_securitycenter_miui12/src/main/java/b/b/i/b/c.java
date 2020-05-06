package b.b.i.b;

import android.text.TextUtils;
import miui.os.SystemProperties;

public class c {
    public static int a() {
        String str = SystemProperties.get("ro.miui.ui.version.code");
        if (!TextUtils.isEmpty(str)) {
            try {
                return Integer.parseInt(str);
            } catch (Exception unused) {
            }
        }
        return 0;
    }

    public static boolean b() {
        return a() >= 10;
    }
}
