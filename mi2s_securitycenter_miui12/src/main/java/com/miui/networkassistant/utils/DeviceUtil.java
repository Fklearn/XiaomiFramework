package com.miui.networkassistant.utils;

import android.content.Context;
import android.content.res.MiuiConfiguration;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import b.b.a.e.n;
import b.b.c.c.d;
import b.b.c.h.f;
import b.b.o.e.a;
import b.b.o.g.c;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.networkassistant.dual.Sim;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import miui.security.DigestUtils;
import miui.telephony.ServiceProviderUtils;
import miui.text.ExtraTextUtils;

public class DeviceUtil {
    public static final String CARRIER = a.a("ro.carrier", "null");
    private static final Set<String> CUSTOMIZED_VERSION_WHITE_LIST = new HashSet();
    public static final String DEVICE_NAME = Build.DEVICE;
    public static final boolean IS_ALPHA_BUILD = miui.os.Build.IS_ALPHA_BUILD;
    public static final boolean IS_CM_CUSTOMIZATION_TEST = miui.os.Build.IS_CM_CUSTOMIZATION_TEST;
    public static final boolean IS_CUSTOMIZED_VERSION = CUSTOMIZED_VERSION_WHITE_LIST.contains(a.a("ro.miui.customized.region", ""));
    public static final boolean IS_DEVELOPMENT_VERSION = miui.os.Build.IS_DEVELOPMENT_VERSION;
    public static final boolean IS_DUAL_CARD;
    public static final boolean IS_INTERNATIONAL_BUILD = miui.os.Build.IS_INTERNATIONAL_BUILD;
    public static final boolean IS_KITKAT_OR_LATER = (Build.VERSION.SDK_INT >= 19);
    public static final boolean IS_L_OR_LATER = (Build.VERSION.SDK_INT >= 21);
    public static final boolean IS_MIUI12;
    public static final boolean IS_M_OR_LATER = (Build.VERSION.SDK_INT >= 23);
    public static final boolean IS_N_OR_LATER = (Build.VERSION.SDK_INT >= 24);
    public static final boolean IS_OFFICIAL_VERSION = miui.os.Build.IS_OFFICIAL_VERSION;
    public static final boolean IS_P_OR_LATER = (Build.VERSION.SDK_INT >= 28);
    public static final boolean IS_Q_OR_LATER = (Build.VERSION.SDK_INT >= 29);
    public static final boolean IS_STABLE_VERSION = miui.os.Build.IS_STABLE_VERSION;
    public static final String MIUI_VERSION = Build.VERSION.INCREMENTAL;

    static {
        c.a a2 = c.a.a("miui.telephony.TelephonyManager");
        boolean z = false;
        a2.b("getDefault", (Class<?>[]) null, new Object[0]);
        a2.e();
        a2.a("isMultiSimEnabled", (Class<?>[]) null, new Object[0]);
        IS_DUAL_CARD = a2.a();
        if (n.a() > 9) {
            z = true;
        }
        IS_MIUI12 = z;
        CUSTOMIZED_VERSION_WHITE_LIST.add("lm_cr");
        CUSTOMIZED_VERSION_WHITE_LIST.add("it_vodafone");
        CUSTOMIZED_VERSION_WHITE_LIST.add("mx_telcel");
        CUSTOMIZED_VERSION_WHITE_LIST.add("cl_moviestar");
        CUSTOMIZED_VERSION_WHITE_LIST.add("es_vodafone");
        CUSTOMIZED_VERSION_WHITE_LIST.add("tr_turkcell");
        CUSTOMIZED_VERSION_WHITE_LIST.add("cl_wom");
        CUSTOMIZED_VERSION_WHITE_LIST.add("hk_h3g");
        CUSTOMIZED_VERSION_WHITE_LIST.add("it_tim");
        CUSTOMIZED_VERSION_WHITE_LIST.add("fr_orange");
        CUSTOMIZED_VERSION_WHITE_LIST.add("fr_sfr");
        CUSTOMIZED_VERSION_WHITE_LIST.add("ph_smart");
        CUSTOMIZED_VERSION_WHITE_LIST.add("es_telefonica");
        CUSTOMIZED_VERSION_WHITE_LIST.add("kh_seatel");
        CUSTOMIZED_VERSION_WHITE_LIST.add("cl_en");
        CUSTOMIZED_VERSION_WHITE_LIST.add("cl_entel");
    }

    private DeviceUtil() {
    }

    public static String getAndroidId(String str) {
        String string = Settings.Secure.getString(d.a().getContentResolver(), "android_id");
        return ExtraTextUtils.toHexReadable(DigestUtils.get(string + "-" + str, "MD5"));
    }

    public static String getAppVersionCode() {
        Context a2 = d.a();
        return String.valueOf(PackageUtil.getAppVersionCode(a2.getPackageManager(), a2.getPackageName()));
    }

    public static String getImeiMd5() {
        return getMd5(TelephonyUtil.getImei());
    }

    public static String getMd5(String str) {
        return TextUtils.isEmpty(str) ? "" : ExtraTextUtils.toHexReadable(DigestUtils.get(str, "MD5"));
    }

    public static String getMiuiVersionType() {
        return IS_STABLE_VERSION ? "stable" : IS_ALPHA_BUILD ? AnimatedProperty.PROPERTY_NAME_ALPHA : IS_DEVELOPMENT_VERSION ? "dev" : "stable";
    }

    public static String getRegion() {
        return miui.os.Build.getRegion();
    }

    public static boolean isCNLanguage() {
        String language = Locale.getDefault().getLanguage();
        return language != null && language.equals("zh");
    }

    public static boolean isCmccSIM(Context context) {
        return ServiceProviderUtils.isChinaMobile(TelephonyUtil.getSubscriberId(context, Sim.getCurrentActiveSlotNum()));
    }

    public static boolean isCnSIM(Context context) {
        String subscriberId = TelephonyUtil.getSubscriberId(context, Sim.getCurrentActiveSlotNum());
        return ServiceProviderUtils.isChinaMobile(subscriberId) || ServiceProviderUtils.isChinaUnicom(subscriberId) || ServiceProviderUtils.isChinaTelecom(subscriberId);
    }

    public static boolean isDarkMode(Context context) {
        return (context.getResources().getConfiguration().uiMode & 48) == 32;
    }

    public static boolean isLargeScaleMode() {
        int scaleMode = MiuiConfiguration.getScaleMode();
        return scaleMode == 13 || scaleMode == 14 || scaleMode == 15 || scaleMode == 11;
    }

    public static boolean isMiPushRestricted(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "mishare_wifi_connect_state", 0) == 1;
    }

    public static boolean isSmartDiagnostics(Context context) {
        return context != null && !IS_INTERNATIONAL_BUILD && IS_Q_OR_LATER && f.i(context.getApplicationContext()) && isCnSIM(context.getApplicationContext());
    }

    public static boolean isXiaoMiDevice(Context context) {
        return true;
    }

    public static void rebootPhone(Context context, String str) {
        ((PowerManager) context.getSystemService("power")).reboot(str);
    }
}
