package miui.util;

import miui.os.Build;
import miui.os.SystemProperties;

public class OldmanUtil {
    public static final boolean IS_ELDER_MODE;

    static {
        boolean z = false;
        if (SystemProperties.getInt(Build.USER_MODE, 0) == 1) {
            z = true;
        }
        IS_ELDER_MODE = z;
    }
}
