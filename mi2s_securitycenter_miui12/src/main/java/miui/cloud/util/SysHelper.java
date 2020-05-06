package miui.cloud.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import com.miui.activityutil.o;
import java.util.Locale;
import miui.cloud.Constants;
import miui.os.Build;
import miui.os.UserHandle;
import miui.telephony.exception.IllegalDeviceException;

public class SysHelper {
    private static final int IMEI_LENGTH = 15;
    private static final int MEID_LENGTH = 14;
    private static final int PHONE_DEVID_MIN_LENGTH = 14;
    private static final String TAG = "SysHelper";

    @Deprecated
    public static String getQuantityStringWithUnit(long j) {
        return getQuantityStringWithUnit((Context) null, j);
    }

    public static String getQuantityStringWithUnit(Context context, long j) {
        String str;
        float f = (float) j;
        String str2 = "MB";
        if (f > 1.07374184E8f) {
            str = String.format("%1$.2f", new Object[]{Float.valueOf(((f / 1024.0f) / 1024.0f) / 1024.0f)});
            str2 = "GB";
        } else {
            str = f > 104857.6f ? String.format("%1$.2f", new Object[]{Float.valueOf((f / 1024.0f) / 1024.0f)}) : f > 0.0f ? "0.1" : o.f2309a;
        }
        return String.format("%s%s", TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1 ? new Object[]{str2, str} : new Object[]{str, str2});
    }

    private static Intent getWarnIntent(String str) {
        Intent intent = new Intent(Constants.Intents.ACTION_WARN_INVALID_DEVICE_ID);
        intent.addFlags(268435456);
        intent.setPackage("com.xiaomi.xmsf");
        intent.putExtra(Constants.Intents.EXTRA_DEVICE_ID, str);
        return intent;
    }

    public static boolean hasTelephonyFeature(Context context) {
        return context.getPackageManager().hasSystemFeature("android.hardware.telephony");
    }

    private static boolean isEmptyDeviceId(String str) {
        return TextUtils.isEmpty(str) || o.f2309a.equals(str) || "null".equalsIgnoreCase(str);
    }

    public static boolean isSecondUser() {
        return UserHandle.myUserId() > 0;
    }

    public static String maskHead(CharSequence charSequence, int i, char c2) {
        int length = charSequence.length();
        int i2 = length / i;
        StringBuilder sb = new StringBuilder(length);
        for (int i3 = 0; i3 < length; i3++) {
            sb.append((length - i3) + -1 < i2 ? charSequence.charAt(i3) : c2);
        }
        return sb.toString();
    }

    public static String maskMiddle(CharSequence charSequence, int i, char c2) {
        int length = charSequence.length();
        int i2 = length / i;
        StringBuilder sb = new StringBuilder(length);
        int i3 = 0;
        while (i3 < length) {
            sb.append((i3 < i2 || (length - i3) + -1 < i2) ? charSequence.charAt(i3) : c2);
            i3++;
        }
        return sb.toString();
    }

    public static String maskTail(String str) {
        return maskTail(str, 3, 4);
    }

    public static String maskTail(String str, int i, int i2) {
        if (i2 >= 0) {
            if (i < 1) {
                i = 1;
            }
            if (str == null) {
                return "";
            }
            int length = i + (str.length() / 5);
            if (length > i2) {
                length = i2;
            }
            char[] charArray = str.toCharArray();
            int length2 = charArray.length - 1;
            while (length2 >= 0 && length2 >= charArray.length - length) {
                charArray[length2] = '?';
                length2--;
            }
            return new String(charArray);
        }
        throw new IllegalArgumentException("maxMaskLength must be a non-negative integer");
    }

    public static void showInvalidDeviceIdWarning(Context context, String str) {
        if (!Build.IS_STABLE_VERSION) {
            try {
                context.startActivity(getWarnIntent(str));
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, "show device id invalid warning failed: ", e);
            }
        }
    }

    public static void showInvalidImeiIfNeeded(Context context, String str) {
        if (!validateIMEI(str)) {
            showInvalidDeviceIdWarning(context, str);
            throw new IllegalDeviceException("device id is invalid");
        }
    }

    public static void showInvalidMacIfNeeded(Context context, String str) {
        if (!validateMAC(str)) {
            showInvalidDeviceIdWarning(context, str);
            throw new IllegalDeviceException("device id is invalid");
        }
    }

    public static boolean validateIMEI(String str) {
        return !isEmptyDeviceId(str) && str.length() >= 14;
    }

    private static boolean validateImeiChecksum(long j) {
        long j2 = j;
        int i = 0;
        for (int i2 = 15; i2 >= 1; i2--) {
            int i3 = (int) (j2 % 10);
            if (i2 % 2 == 0) {
                int i4 = i3 * 2;
                i += (i4 / 10) + (i4 % 10);
            } else {
                i += i3;
            }
            j2 /= 10;
        }
        return i % 10 == 0;
    }

    public static boolean validateMAC(String str) {
        return !isEmptyDeviceId(str);
    }
}
