package miui.telephony;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import miui.os.Build;
import miui.telephony.PhoneNumberUtils;
import miui.telephony.phonenumber.CountryCode;
import miui.telephony.phonenumber.Prefix;

public class MiuiHeDuoHaoUtil {
    public static final String DIAL = "dial";
    public static final String HEDUOHAO_PREFIX_SIM1 = "125831";
    public static final String HEDUOHAO_PREFIX_SIM2 = "125832";
    public static final String HEDUOHAO_PREFIX_SIM3 = "125833";
    public static final String NAME = "name";
    public static final String NUMBER = "number";
    public static final String ORDER_ID = "order_id";
    public static final String PASS_ID = "pass_id";
    public static final String SLOT_ID = "slot_id";
    public static final String STATUS = "status";
    public static final String SUB_ID = "sub_id";
    private static final String TAG = "MiuiHeDuoHaoUtil";
    public static final String TOGGLE = "toggle";
    public static final Uri URI = Uri.parse("content://com.android.providers.telephony.heduohaoprovider/heduohao");

    public static class HeDuoHao {
        public int mDial;
        public String mName;
        public String mNumber;
        public int mOrderId;
        public String mPassId;
        public int mSlotId;
        public int mStatus;
        public int mSubId;
        public int mToggle;
    }

    private MiuiHeDuoHaoUtil() {
    }

    public static boolean isHeDuoHaoEnable() {
        return Build.IS_CM_CUSTOMIZATION || Build.IS_CM_CUSTOMIZATION_TEST;
    }

    public static int getIndexExcludeCC(String number) {
        String countryCode = PhoneNumberUtils.PhoneNumber.parse(number, "86".equals(CountryCode.getNetworkCountryCode())).getCountryCode();
        if (!TextUtils.isEmpty(countryCode)) {
            return number.indexOf(countryCode) + countryCode.length();
        }
        return 0;
    }

    public static boolean isHeDuoHao(String number) {
        if (TextUtils.isEmpty(number)) {
            return false;
        }
        String effectiveNumber = number.substring(getIndexExcludeCC(number)).replaceAll(" ", Prefix.EMPTY);
        if (effectiveNumber.startsWith("125831") || effectiveNumber.startsWith("125832") || effectiveNumber.startsWith("125833")) {
            return true;
        }
        return false;
    }

    public static String removeHeDuoHaoPrefix(String number) {
        if (TextUtils.isEmpty(number)) {
            return Prefix.EMPTY;
        }
        if (!isHeDuoHao(number)) {
            return number;
        }
        int index = getIndexExcludeCC(number);
        String effectiveNumber = number.substring(index).replaceAll(" ", Prefix.EMPTY);
        if (effectiveNumber.startsWith("125831")) {
            Log.i(TAG, "removeHeDuoHaoPrefix: 1");
            return number.substring(0, index) + effectiveNumber.replaceFirst("125831", Prefix.EMPTY);
        } else if (effectiveNumber.startsWith("125832")) {
            Log.i(TAG, "removeHeDuoHaoPrefix: 2");
            return number.substring(0, index) + effectiveNumber.replaceFirst("125832", Prefix.EMPTY);
        } else if (!effectiveNumber.startsWith("125833")) {
            return effectiveNumber;
        } else {
            Log.i(TAG, "removeHeDuoHaoPrefix: 3");
            return number.substring(0, index) + effectiveNumber.replaceFirst("125833", Prefix.EMPTY);
        }
    }

    public static String addHeDuoHaoPrefix(String number, int orderId) {
        if (TextUtils.isEmpty(number)) {
            return Prefix.EMPTY;
        }
        String effectiveNumber = number.substring(getIndexExcludeCC(number)).replaceAll(" ", Prefix.EMPTY);
        if (orderId == 1) {
            return "125831" + effectiveNumber;
        } else if (orderId == 2) {
            return "125832" + effectiveNumber;
        } else if (orderId != 3) {
            return number;
        } else {
            return "125833" + effectiveNumber;
        }
    }
}
