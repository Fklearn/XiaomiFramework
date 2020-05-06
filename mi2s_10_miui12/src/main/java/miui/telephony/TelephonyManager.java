package miui.telephony;

import android.content.Context;
import android.os.Process;
import android.os.ResultReceiver;
import android.os.UserHandle;
import android.provider.Settings;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.text.TextUtils;
import com.miui.internal.telephony.TelephonyManagerAndroidImpl;
import java.util.ArrayList;
import java.util.List;
import miui.os.Build;
import miui.os.SystemProperties;
import miui.reflect.Method;
import miui.telephony.phonenumber.Prefix;

public abstract class TelephonyManager {
    public static final String ACTION_PHONE_STATE_CHANGED = "android.intent.action.PHONE_STATE";
    public static final String ACTION_RESPOND_VIA_MESSAGE = "android.intent.action.RESPOND_VIA_MESSAGE";
    public static final int CALL_STATE_IDLE = 0;
    public static final int CALL_STATE_OFFHOOK = 2;
    public static final int CALL_STATE_RINGING = 1;
    public static final int CF_ACTION_DISABLE = 0;
    public static final int CF_ACTION_ENABLE = 1;
    public static final int CF_ACTION_ERASURE = 4;
    public static final int CF_ACTION_REGISTRATION = 3;
    public static final int CF_REASON_ALL = 4;
    public static final int CF_REASON_ALL_CONDITIONAL = 5;
    public static final int CF_REASON_BUSY = 1;
    public static final int CF_REASON_NOT_REACHABLE = 3;
    public static final int CF_REASON_NO_REPLY = 2;
    public static final int CF_REASON_UNCONDITIONAL = 0;
    public static final int CT_VOLTE_MODE_HVOLTE = 2;
    public static final int CT_VOLTE_MODE_NOT_SUPPORT = 0;
    public static final int CT_VOLTE_MODE_VOLTE_ONLY = 1;
    public static final String CUSTOMIZED_REGION = SystemProperties.get("ro.miui.customized.region", Prefix.EMPTY);
    public static final int DATA_ACTIVITY_DORMANT = 4;
    public static final int DATA_ACTIVITY_IN = 1;
    public static final int DATA_ACTIVITY_INOUT = 3;
    public static final int DATA_ACTIVITY_NONE = 0;
    public static final int DATA_ACTIVITY_OUT = 2;
    public static final int DATA_CONNECTED = 2;
    public static final int DATA_CONNECTING = 1;
    public static final int DATA_DISCONNECTED = 0;
    public static final String DATA_DOMESTIC_ROAMING = "data_domestic_roaming";
    public static final int DATA_SUSPENDED = 3;
    public static final String EXTRA_INCOMING_NUMBER = "incoming_number";
    public static final String EXTRA_STATE = "state";
    public static final String EXTRA_STATE_IDLE = android.telephony.TelephonyManager.EXTRA_STATE_IDLE;
    public static final String EXTRA_STATE_OFFHOOK = android.telephony.TelephonyManager.EXTRA_STATE_OFFHOOK;
    public static final String EXTRA_STATE_RINGING = android.telephony.TelephonyManager.EXTRA_STATE_RINGING;
    private static final boolean IS_CUST_SINGLE_SIM;
    private static final boolean IS_GOOGLE_CSP = SystemProperties.getBoolean("ro.miui.google.csp", false);
    public static final String MCC_CHINA = "460";
    public static final int NETWORK_TYPE_1xRTT = 7;
    public static final int NETWORK_TYPE_CDMA = 4;
    public static final int NETWORK_TYPE_DC_HSPAP = 20;
    public static final int NETWORK_TYPE_EDGE = 2;
    public static final int NETWORK_TYPE_EHRPD = 14;
    public static final int NETWORK_TYPE_EVDO_0 = 5;
    public static final int NETWORK_TYPE_EVDO_A = 6;
    public static final int NETWORK_TYPE_EVDO_B = 12;
    public static final int NETWORK_TYPE_GPRS = 1;
    public static final int NETWORK_TYPE_HSDPA = 8;
    public static final int NETWORK_TYPE_HSPA = 10;
    public static final int NETWORK_TYPE_HSPAP = 15;
    public static final int NETWORK_TYPE_HSUPA = 9;
    public static final int NETWORK_TYPE_IDEN = 11;
    public static final int NETWORK_TYPE_LTE = 13;
    public static final int NETWORK_TYPE_LTE_CA = 19;
    public static final int NETWORK_TYPE_UMTS = 3;
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    public static final String OPERATOR_NUMERIC_CHINA_MOBILE = "46000";
    public static final String OPERATOR_NUMERIC_CHINA_TELECOM = "46003";
    public static final String OPERATOR_NUMERIC_CHINA_UNICOM = "46001";
    public static final int PHONE_TYPE_CDMA = 2;
    public static final int PHONE_TYPE_GSM = 1;
    public static final int PHONE_TYPE_NONE = 0;
    public static final int PHONE_TYPE_SIP = 3;
    public static final String REGION = SystemProperties.get("ro.miui.build.region", Prefix.EMPTY);
    public static final int SET_CALL_FOWARD_FAILURE = -1;
    public static final int SET_CALL_FOWARD_NOT_SUPPORT = -2;
    public static final int SET_CALL_FOWARD_SUCCESS = 0;
    public static final int SET_CALL_FOWARD_UT_DATA_DISABLED = -3;
    public static final int SIM_STATE_ABSENT = 1;
    public static final int SIM_STATE_NETWORK_LOCKED = 4;
    public static final int SIM_STATE_PIN_REQUIRED = 2;
    public static final int SIM_STATE_PUK_REQUIRED = 3;
    public static final int SIM_STATE_READY = 5;
    public static final int SIM_STATE_UNKNOWN = 0;
    protected static final String TAG = "TeleMgr";
    private static final String[] VDF_NUMERICS = {"23003", "26202", "21401", "20205", "21670", "27201", "22210", "20404", "26801", "22601", "23415"};
    private String BUILD_OPERATOR_TYPE = null;

    public abstract int getCallState();

    public abstract int getCallStateForSlot(int i);

    public abstract int getCallStateForSubscription(int i);

    public abstract CellLocation getCellLocationForSlot(int i);

    public abstract int getDataActivity();

    public abstract int getDataActivityForSlot(int i);

    public abstract int getDataActivityForSubscription(int i);

    public abstract int getDataState();

    public abstract int getDataStateForSlot(int i);

    public abstract int getDataStateForSubscription(int i);

    public abstract String getDeviceId();

    public abstract String getDeviceIdForSlot(int i);

    public abstract String getDeviceIdForSubscription(int i);

    public abstract String getDeviceSoftwareVersion();

    public abstract String getDeviceSoftwareVersionForSlot(int i);

    public abstract String getDeviceSoftwareVersionForSubscription(int i);

    public abstract String getImei();

    public abstract String getImeiForSlot(int i);

    public abstract String getImeiForSubscription(int i);

    public abstract String getLine1Number();

    public abstract String getLine1NumberForSlot(int i);

    public abstract String getLine1NumberForSubscription(int i);

    public abstract String getMeid();

    public abstract String getMeidForSlot(int i);

    public abstract String getMeidForSubscription(int i);

    public abstract String getMiuiDeviceId();

    public abstract int getMiuiLevel(SignalStrength signalStrength);

    public abstract String getNetworkCountryIso();

    public abstract String getNetworkCountryIsoForSlot(int i);

    public abstract String getNetworkCountryIsoForSubscription(int i);

    public abstract String getNetworkOperator();

    public abstract String getNetworkOperatorForSlot(int i);

    public abstract String getNetworkOperatorForSubscription(int i);

    public abstract String getNetworkOperatorName();

    public abstract String getNetworkOperatorNameForSlot(int i);

    public abstract String getNetworkOperatorNameForSubscription(int i);

    public abstract int getNetworkType();

    public abstract int getNetworkTypeForSlot(int i);

    public abstract int getNetworkTypeForSubscription(int i);

    public abstract int getPhoneCount();

    public abstract int getPhoneType();

    public abstract int getPhoneTypeForSlot(int i);

    public abstract int getPhoneTypeForSubscription(int i);

    public abstract String getSimCountryIso();

    public abstract String getSimCountryIsoForSlot(int i);

    public abstract String getSimCountryIsoForSubscription(int i);

    public abstract String getSimOperator();

    public abstract String getSimOperatorForSlot(int i);

    public abstract String getSimOperatorForSubscription(int i);

    public abstract String getSimOperatorName();

    public abstract String getSimOperatorNameForSlot(int i);

    public abstract String getSimOperatorNameForSubscription(int i);

    public abstract String getSimSerialNumber();

    public abstract String getSimSerialNumberForSlot(int i);

    public abstract String getSimSerialNumberForSubscription(int i);

    public abstract int getSimState();

    public abstract int getSimStateForSlot(int i);

    public abstract int getSimStateForSubscription(int i);

    public abstract String getSmallDeviceId();

    public abstract String getSubscriberId();

    public abstract String getSubscriberIdForSlot(int i);

    public abstract String getSubscriberIdForSubscription(int i);

    public abstract String getVoiceMailAlphaTag();

    public abstract String getVoiceMailAlphaTagForSlot(int i);

    public abstract String getVoiceMailAlphaTagForSubscription(int i);

    public abstract String getVoiceMailNumber();

    public abstract String getVoiceMailNumberForSlot(int i);

    public abstract String getVoiceMailNumberForSubscription(int i);

    public abstract boolean hasIccCard();

    public abstract boolean hasIccCard(int i);

    public abstract boolean isMultiSimEnabled();

    public abstract boolean isVoiceCapable();

    public abstract void listen(PhoneStateListener phoneStateListener, int i);

    public abstract void listenForSlot(int i, PhoneStateListener phoneStateListener, int i2);

    public abstract void listenForSubscription(int i, PhoneStateListener phoneStateListener, int i2);

    static class Holder {
        static final TelephonyManager INSTANCE;

        private Holder() {
        }

        static {
            TelephonyManager telephonyManager;
            if (Build.IS_MIUI) {
                telephonyManager = getMiuiTelephonyManager();
            } else {
                telephonyManager = TelephonyManagerAndroidImpl.getDefault();
            }
            INSTANCE = telephonyManager;
        }

        private static TelephonyManager getMiuiTelephonyManager() {
            try {
                Class cls = Class.forName("miui.telephony.TelephonyManagerEx");
                return (TelephonyManager) Method.of(cls, "getDefault", cls, new Class[0]).invokeObject(cls, (Object) null, new Object[0]);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public static TelephonyManager getDefault() {
        return Holder.INSTANCE;
    }

    static {
        boolean z = true;
        if (SystemProperties.getInt("ro.miui.singlesim", 0) != 1) {
            z = false;
        }
        IS_CUST_SINGLE_SIM = z;
    }

    public int getIccCardCount() {
        int max = getPhoneCount();
        int count = 0;
        for (int i = 0; i < max; i++) {
            if (hasIccCard(i)) {
                count++;
            }
        }
        return count;
    }

    public List<String> getDeviceIdList() {
        Rlog.i("TelephonyManager", "unexpected getDeviceIdList method call");
        return null;
    }

    public List<String> getImeiList() {
        return new ArrayList(0);
    }

    public List<String> getMeidList() {
        return new ArrayList(0);
    }

    public boolean isImsRegistered(int phoneId) {
        return false;
    }

    public boolean isVolteEnabledByUser() {
        return false;
    }

    public boolean isVolteEnabledByUser(int slotId) {
        return false;
    }

    public boolean isVtEnabledByPlatform() {
        return false;
    }

    public boolean isVtEnabledByPlatform(int slotId) {
        return false;
    }

    public boolean isVolteEnabledByPlatform() {
        return false;
    }

    public boolean isVolteEnabledByPlatform(int slotId) {
        return false;
    }

    public boolean isVideoTelephonyAvailable(int phoneId) {
        return false;
    }

    public boolean isWifiCallingAvailable(int phoneId) {
        return false;
    }

    public boolean isDualVolteSupported() {
        return false;
    }

    public int getCtVolteSupportedMode() {
        return 0;
    }

    public boolean isCmccCooperationDevice() {
        return false;
    }

    public boolean isSameOperator(String numeric, String anotherNumeric) {
        throw new UnsupportedOperationException("Only support android L and above");
    }

    public String getSpn(String numeric, int slotId, String spn, boolean longName) {
        throw new UnsupportedOperationException("Only support android L and above");
    }

    public boolean isDisableLte(boolean checkSim) {
        if (!Build.IS_GLOBAL_BUILD) {
            return false;
        }
        boolean disableDevice = "ido".equals(Build.DEVICE) || "kenzo".equals(Build.DEVICE);
        if (!disableDevice || !checkSim) {
            return disableDevice;
        }
        int phoneCount = getPhoneCount();
        for (int i = 0; i < phoneCount; i++) {
            String numeric = getSimOperatorForSlot(i);
            if (numeric != null && numeric.startsWith("510")) {
                return true;
            }
        }
        return false;
    }

    private static int getAppIdUserHandle(int callingUid) {
        Class cls = Integer.TYPE;
        Class[] clsArr = {Integer.TYPE};
        return Method.of(UserHandle.class, "getAppId", cls, clsArr).invokeInt(UserHandle.class, (Object) null, new Object[]{Integer.valueOf(callingUid)});
    }

    private static boolean isSameAppUserHandle(int uid1, int uid2) {
        Class cls = Boolean.TYPE;
        Class[] clsArr = {Integer.TYPE, Integer.TYPE};
        return Method.of(UserHandle.class, "isSameApp", cls, clsArr).invokeBoolean(UserHandle.class, (Object) null, new Object[]{Integer.valueOf(uid1), Integer.valueOf(uid2)});
    }

    public static boolean isSupportDomesticRoaming() {
        return Build.checkRegion("PL");
    }

    public static boolean isDomesticRoamingEnable(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), DATA_DOMESTIC_ROAMING, 1) != 0;
    }

    public static boolean setDomesticRoamingEnable(Context context, boolean isEnable) {
        return Settings.Global.putInt(context.getContentResolver(), DATA_DOMESTIC_ROAMING, isEnable);
    }

    public static boolean checkCallingOrSelfPermissionGranted(int callingUid) {
        if (getAppIdUserHandle(callingUid) == 1000) {
            return true;
        }
        if (Process.myUid() < 0 || !isSameAppUserHandle(callingUid, Process.myUid())) {
            return false;
        }
        return true;
    }

    public static boolean isCustSingleSimDevice() {
        return IS_CUST_SINGLE_SIM;
    }

    public void setCallForwardingOption(int phoneId, int action, int reason, String number, ResultReceiver callback) {
        throw new UnsupportedOperationException("setCallForwardingOption not supported");
    }

    public void setIccCardActivate(int slotId, boolean isActivate) {
        throw new UnsupportedOperationException("Only support android L and above");
    }

    public static boolean isGoogleCsp() {
        return IS_GOOGLE_CSP;
    }

    public boolean isChinaTelecomTest(String numeric) {
        if (!Build.IS_CT_CUSTOMIZATION_TEST || TextUtils.isEmpty(numeric)) {
            return false;
        }
        if (numeric.equals(OPERATOR_NUMERIC_CHINA_TELECOM) || numeric.equals("46011") || numeric.equals("45502") || numeric.equals("45507") || numeric.equals("00101") || numeric.equals("20404")) {
            return true;
        }
        return false;
    }

    public boolean isGwsdSupport() {
        return false;
    }

    public static boolean isCustForEsVodafone() {
        return "es_vodafone".equals(CUSTOMIZED_REGION);
    }

    public static boolean isCustForMxTelcel() {
        return "mx_telcel".equals(CUSTOMIZED_REGION);
    }

    public static boolean isCustForHkH3g() {
        return "hk_h3g".equals(CUSTOMIZED_REGION);
    }

    public static boolean isCustForFrOrange() {
        return "fr_orange".equals(CUSTOMIZED_REGION);
    }

    public static boolean isCustForTurkCell() {
        return "tr_turkcell".equals(CUSTOMIZED_REGION);
    }

    public static boolean isCustForFrSfr() {
        return "fr_sfr".equals(CUSTOMIZED_REGION);
    }

    public static boolean isCustForLmClaro() {
        return "lm_cr".equals(CUSTOMIZED_REGION);
    }

    public static boolean isCustForFrRussia() {
        return "ru".equals(CUSTOMIZED_REGION);
    }

    public static boolean isCustForClEntel() {
        return "cl_entel".equals(CUSTOMIZED_REGION) || "cl_en".equals(CUSTOMIZED_REGION);
    }

    public static boolean isForEEA() {
        return "eea".equals(REGION);
    }

    public static boolean isForEEAHasSubnetVDF() {
        if (!isForEEA()) {
            return false;
        }
        String numeric = SystemProperties.get("gsm.sim.operator.numeric", Prefix.EMPTY);
        for (String str : VDF_NUMERICS) {
            if (numeric.contains(str)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCustForKrSkt() {
        return "kr_skt".equals(CUSTOMIZED_REGION);
    }

    public static boolean isCustForKrKt() {
        return "kr_kt".equals(CUSTOMIZED_REGION);
    }

    public static boolean isCustForKrLgu() {
        return "kr_lgu".equals(CUSTOMIZED_REGION);
    }

    public static boolean isCustForKrOps() {
        return isCustForKrSkt() || isCustForKrKt() || isCustForKrLgu();
    }

    public boolean isUserFiveGEnabled() {
        return false;
    }

    public void setUserFiveGEnabled(boolean enabled) {
    }

    public boolean isFiveGCapable() {
        return false;
    }
}
