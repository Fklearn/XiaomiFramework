package miui.telephony;

public class TelephonyManagerUtil {
    public static String getDeviceId() {
        return TelephonyManager.getDefault().getMiuiDeviceId();
    }
}
