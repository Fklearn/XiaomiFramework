package miui.util;

import android.text.TextUtils;
import miui.net.ConnectivityHelper;
import miui.telephony.TelephonyManagerUtil;

public abstract class DeviceId
{
  public static String get()
  {
    String str1 = TelephonyManagerUtil.getDeviceId();
    if (!TextUtils.isEmpty(str1)) {
      return str1;
    }
    if (ConnectivityHelper.getInstance().isWifiOnly()) {
      str1 = ConnectivityHelper.getInstance().getMacAddress();
    }
    String str2 = str1;
    if (str1 == null) {
      str2 = "";
    }
    return str2;
  }
}


/* Location:              /Users/sanbo/Desktop/framework/miui/framework/classes4-dex2jar.jar!/miui/util/DeviceId.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */