package miui.telephony;

import android.text.TextUtils;
import java.util.Locale;
import miui.telephony.CloudTelephonyManager;

public class IccidUtils {
    private IccidUtils() {
    }

    public static String cloudIdToLowerCase(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        CloudTelephonyManager.TypedSimId parse = CloudTelephonyManager.TypedSimId.parse(str);
        if (parse.type != 1) {
            return str;
        }
        return new CloudTelephonyManager.TypedSimId(parse.type, parse.value.toLowerCase(Locale.ENGLISH)).toPlain();
    }

    public static String iccidToLowerCase(String str) {
        return !TextUtils.isEmpty(str) ? str.toLowerCase(Locale.ENGLISH) : str;
    }
}
