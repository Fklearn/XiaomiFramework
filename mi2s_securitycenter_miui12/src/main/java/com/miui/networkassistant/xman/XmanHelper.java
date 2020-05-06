package com.miui.networkassistant.xman;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.text.TextUtils;
import b.b.c.g.a;
import b.b.c.g.c;
import b.b.c.h.f;
import b.b.c.h.j;
import com.miui.analytics.AnalyticsUtil;
import com.miui.luckymoney.config.Constants;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.PackageUtil;
import com.miui.securitycenter.h;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import miui.util.Log;

public class XmanHelper {
    private static final String CATEGORY_XMAN = "xman_share";
    private static final long DAY = 86400000;
    private static final String KEY_XMAN_ENABLE = "key_xman_enable";
    private static final String KEY_XMAN_SETTINGS = "key_xman_settings";
    private static final String KEY_XMAN_SETTINGS_ENABLE = "key_xman_settings_enable";
    private static final String KEY_XMAN_SHARED = "key_xman_shared";
    private static final String PARAM_ENABLE = "enable";
    private static final String PARAM_HIDE_CAMERA_ENABLE = "hide_camera_enable";
    private static final String PARAM_HIDE_LOCATION_ENABLE = "hide_location_enable";
    private static final String PARAM_IGNORE_ENABLE = "ignoreEnable";
    private static final String PARAM_PACKAGE = "package";
    private static final String PARAM_SRC_PACKAGE = "src_package";
    private static final String PARAM_TYPE = "type";
    private static final String PARAM_XMAN_SHARE_ENABLE = "xman_share_enable";
    private static final String TAG = "xman_share";
    private static final String XMAN_CLOUD_DISABLE = "xman_cloud_disable";
    private static final String XMAN_SHARE_ENABLE = "xman_share_enable";
    private static final String XMAN_SHARE_HIDE_CAMERA = "xman_share_hide_camera";
    private static final String XMAN_SHARE_HIDE_LOCATION = "xman_share_hide_location";
    private static final String XMAN_SHARE_IGNORE = "xman_share_ignore";
    private static long sLastCheckTime = 0;
    private static int sSupportSecurityShare = -1;
    private static int sSupportXman = -1;

    /* access modifiers changed from: private */
    public static String accessXmanModuleByPOST(String str) {
        List<c> baseParams = getBaseParams();
        baseParams.add(new c(Constants.JSON_KEY_MODULE, str));
        return a.a("update", Constants.apiUrl, "21da76da-224c-2313-ac60-abcd70139283", baseParams, new j("networkassistant_xmanhelper"));
    }

    public static void checkXmanCloudDataAsync(Context context) {
        Log.i("xman_share", "XmanHelper - isSupportXman： " + isSupportXman(context));
        final Context applicationContext = context.getApplicationContext();
        if (!DeviceUtil.IS_INTERNATIONAL_BUILD) {
            if (!h.i()) {
                Log.i("xman_share", "XmanHelper - CTA Restrict");
            } else if (!f.j(applicationContext)) {
                Log.i("xman_share", "XmanHelper - No Network Restrict");
            } else if (!isSupportXman(applicationContext)) {
                Log.i("xman_share", "XmanHelper - No Xman Restrict");
            } else if (System.currentTimeMillis() - sLastCheckTime < 86400000) {
                Log.i("xman_share", "XmanHelper - Time Restrict");
            } else {
                Log.i("xman_share", "XmanHelper - checkXmanCloudDataAsync");
                sLastCheckTime = System.currentTimeMillis();
                b.b.c.c.a.a.a(new Runnable() {
                    public void run() {
                        XmanResult xmanResult = new XmanResult(XmanHelper.accessXmanModuleByPOST("Xman"));
                        if (xmanResult.isSuccess()) {
                            Context context = applicationContext;
                            XmanHelper.setXmanCloudDisable(context, xmanResult.isXmanCloudDisable(context));
                            return;
                        }
                        Log.e("xman_share", "XmanHelper - xmanResult.isSuccess(): false");
                    }
                });
            }
        }
    }

    private static List<c> getBaseParams() {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new c(Constants.JSON_KEY_DEVICE, DeviceUtil.DEVICE_NAME));
        arrayList.add(new c(Constants.JSON_KEY_T, DeviceUtil.IS_STABLE_VERSION ? "stable" : "development"));
        arrayList.add(new c(Constants.JSON_KEY_IMEI, DeviceUtil.getImeiMd5()));
        arrayList.add(new c("region", DeviceUtil.getRegion()));
        arrayList.add(new c(Constants.JSON_KEY_MIUI_VERSION, DeviceUtil.MIUI_VERSION));
        arrayList.add(new c(Constants.JSON_KEY_CARRIER, DeviceUtil.CARRIER));
        arrayList.add(new c(Constants.JSON_KEY_APP_VERSION, DeviceUtil.getAppVersionCode()));
        return arrayList;
    }

    public static boolean getXmanCloudDisable(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), XMAN_CLOUD_DISABLE, 1) == 1;
    }

    private static boolean getXmanEnable(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "xman_share_enable", 0) == 1;
    }

    private static boolean getXmanIgnoreEnable(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), XMAN_SHARE_IGNORE, 0) == 1;
    }

    private static boolean isHideCameraInfoEnable(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), XMAN_SHARE_HIDE_CAMERA, 0) == 1;
    }

    private static boolean isHideLoactionInfoEnable(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), XMAN_SHARE_HIDE_LOCATION, 0) == 1;
    }

    private static boolean isSupportSecurityShare(Context context) {
        int i = sSupportSecurityShare;
        if (i != -1) {
            return i == 1;
        }
        boolean isIntentExist = PackageUtil.isIntentExist(context, new Intent("miui.intent.action.XMAN_SHARE_SETTING"));
        sSupportSecurityShare = isIntentExist ? 1 : 0;
        return isIntentExist;
    }

    private static boolean isSupportXman(Context context) {
        int i = sSupportXman;
        if (i != -1) {
            return i == 1;
        }
        boolean isIntentExist = PackageUtil.isIntentExist(context, new Intent("miui.intent.action.XMAN_MAIN"));
        sSupportXman = isIntentExist ? 1 : 0;
        return isIntentExist;
    }

    /* access modifiers changed from: private */
    public static void setXmanCloudDisable(Context context, boolean z) {
        Log.i("xman_share", "XmanHelper - setXmanCloudDisable： " + z);
        Settings.Secure.putInt(context.getContentResolver(), XMAN_CLOUD_DISABLE, z ? 1 : 0);
    }

    public static void trackXmanEnableEvent(Context context, boolean z, boolean z2) {
        if (!DeviceUtil.IS_INTERNATIONAL_BUILD) {
            if (!isSupportXman(context)) {
                Log.i("xman_share", "XmanHelper - No Xman, Restrict");
                return;
            }
            HashMap hashMap = new HashMap(1);
            hashMap.put(PARAM_ENABLE, String.valueOf(z ? 1 : 0));
            hashMap.put(PARAM_IGNORE_ENABLE, String.valueOf(z2 ? 1 : 0));
            AnalyticsUtil.recordCountEvent("xman_share", KEY_XMAN_ENABLE, hashMap);
        }
    }

    public static void trackXmanSettingsEnableEvent(Context context) {
        if (!DeviceUtil.IS_INTERNATIONAL_BUILD && isSupportSecurityShare(context)) {
            HashMap hashMap = new HashMap(1);
            hashMap.put(PARAM_HIDE_LOCATION_ENABLE, String.valueOf(isHideLoactionInfoEnable(context)));
            hashMap.put(PARAM_HIDE_CAMERA_ENABLE, String.valueOf(isHideCameraInfoEnable(context)));
            hashMap.put("xman_share_enable", String.valueOf(getXmanEnable(context)));
            AnalyticsUtil.recordCountEvent("xman_share", KEY_XMAN_SETTINGS_ENABLE, hashMap);
        }
    }

    public static void trackXmanSettingsEvent(String str) {
        if (!DeviceUtil.IS_INTERNATIONAL_BUILD && !TextUtils.isEmpty(str)) {
            Log.d("xman_share", "trackXmanSettingsEvent: packName: " + str);
            HashMap hashMap = new HashMap(1);
            hashMap.put(PARAM_SRC_PACKAGE, str);
            AnalyticsUtil.recordCountEvent("xman_share", KEY_XMAN_SETTINGS, hashMap);
        }
    }

    public static void trackXmanSharedEvent(String str, int i) {
        if (!DeviceUtil.IS_INTERNATIONAL_BUILD && !TextUtils.isEmpty(str) && i != 0) {
            Log.d("xman_share", "trackXmanSharedEvent: packName: " + str + "type :" + i);
            HashMap hashMap = new HashMap(1);
            hashMap.put(PARAM_PACKAGE, str);
            hashMap.put("type", String.valueOf(i));
            AnalyticsUtil.recordCountEvent("xman_share", KEY_XMAN_SHARED, hashMap);
        }
    }

    public static void uploadXmanData(Context context) {
        trackXmanEnableEvent(context, getXmanEnable(context), getXmanIgnoreEnable(context));
        trackXmanSettingsEnableEvent(context);
    }
}
