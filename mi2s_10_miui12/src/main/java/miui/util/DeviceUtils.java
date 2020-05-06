package miui.util;

import android.content.Context;
import android.telephony.TelephonyManager;

@Deprecated
public class DeviceUtils {
    @Deprecated
    public static String getDeviceId(Context context) {
        return ((TelephonyManager) context.getSystemService("phone")).getDeviceId();
    }
}
