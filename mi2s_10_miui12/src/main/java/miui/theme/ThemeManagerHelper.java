package miui.theme;

import android.content.Context;
import android.text.TextUtils;
import miui.os.Build;
import miui.os.SystemProperties;
import miui.telephony.phonenumber.Prefix;

public class ThemeManagerHelper {
    private ThemeManagerHelper() {
    }

    public static boolean needDisableTheme(Context context) {
        if (Build.IS_TABLET || isHideTheme()) {
            return true;
        }
        if (Build.IS_INTERNATIONAL_BUILD && GlobalUtils.isEU(context)) {
            String miuiVersionCode = Build.getMiUiVersionCode();
            if (TextUtils.isEmpty(miuiVersionCode) || Integer.valueOf(miuiVersionCode).intValue() < 8) {
                return true;
            }
            return false;
        } else if (!Build.IS_INTERNATIONAL_BUILD || !GlobalUtils.isReligiousArea(context)) {
            return false;
        } else {
            return true;
        }
    }

    private static boolean isHideTheme() {
        String properties = SystemProperties.get("ro.miui.customized.region", Prefix.EMPTY);
        return "mx_telcel".equals(properties) || "lm_cr".equals(properties) || "mx_at".equals(properties);
    }
}
