package com.miui.networkassistant.zman;

import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import b.b.c.g.a;
import b.b.c.g.c;
import b.b.c.h.f;
import b.b.c.h.j;
import com.miui.activityutil.o;
import com.miui.analytics.AnalyticsUtil;
import com.miui.luckymoney.config.Constants;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.PackageUtil;
import com.miui.securitycenter.h;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miui.util.Log;

public class ZmanHelper {
    private static final String ACTION_ZMAN_SETTINGS = "miui.intent.action.ZMAN_SECURITY_SHARE_SETTING";
    private static final String CATEGORY_ZMAN = "security_share";
    private static final long DAY = 86400000;
    private static final String KEY_SECURITY_SHARE_ONCE_SETTINGS_CHANGE = "once_settings_change";
    private static final String KEY_SECURITY_SHARE_SHARED_IMAGE = "shared_image";
    private static final String KEY_SECURITY_SHARE_SHARED_IMAGES = "shared_images";
    private static final String KEY_SECURITY_SHARE_STATUS = "status";
    private static final String PARAM_CAMERA_ENABLE = "param_camera_enable";
    static final String PARAM_IMAGE_HAVE_CAMERA = "param_image_have_camera";
    static final String PARAM_IMAGE_HAVE_LOCATION = "param_image_have_location";
    private static final String PARAM_LOCATION_ENABLE = "param_location_enable";
    static final String PARAM_SRC_PACKAGENAME = "param_src_packagename";
    private static final String PARAM_TAIL_ENABLE = "param_tail_enable";
    private static final String TAG = "zman_share";
    private static final String ZMAN_CLOUD_DISABLE = "zman_cloud_disable";
    private static final String ZMAN_SHARE_CAMERA_ENABLE = "zman_share_hide_camera";
    private static final String ZMAN_SHARE_ENABLE = "zman_share_enable";
    private static final String ZMAN_SHARE_LOCATION_ENABLE = "zman_share_hide_location";
    private static final String ZMAN_SHARE_TAIL_CLOUD_DISABLE = "share_tail_disable";
    private static long sLastCheckTime = 0;
    private static int sSupportSecurityShare = -1;

    private ZmanHelper() {
    }

    /* access modifiers changed from: private */
    public static String accessSecurityShareModuleByPOST() {
        List<c> baseParams = getBaseParams();
        baseParams.add(new c(Constants.JSON_KEY_MODULE, "securityshare"));
        return a.a("update", Constants.apiUrl, "21da76da-224c-2313-ac60-abcd70139283", baseParams, new j("networkassistant_securityshare"));
    }

    public static void checkZmanCloudDataAsync(Context context) {
        final Context applicationContext = context.getApplicationContext();
        if (System.currentTimeMillis() - sLastCheckTime < 86400000) {
            Log.w(TAG, "Restrict - Time ");
        } else if (!isSupportSecurityShare(applicationContext)) {
            Log.w(TAG, "Restrict - No Zman ");
        } else if (!h.i()) {
            Log.w(TAG, "Restrict - CTA ");
        } else if (!f.j(applicationContext)) {
            Log.w(TAG, "Restrict - No Network ");
        } else {
            Log.d(TAG, "Start - checkZmanCloudDataAsync");
            sLastCheckTime = System.currentTimeMillis();
            b.b.c.c.a.a.a(new Runnable() {
                public void run() {
                    String access$000 = ZmanHelper.accessSecurityShareModuleByPOST();
                    ZmanResult zmanResult = new ZmanResult(access$000);
                    Log.e(ZmanHelper.TAG, "accessSecurityShareModuleByPOST - result(): " + access$000);
                    if (zmanResult.isSuccess()) {
                        Context context = applicationContext;
                        ZmanHelper.setSecurityShareCloudDisable(context, zmanResult.isSecurityShareCloudDisable(context) ? 1 : 0);
                        Context context2 = applicationContext;
                        ZmanHelper.setShareTailCloudDisable(context2, zmanResult.isShareTailDisable(context2) ? 1 : 0);
                        return;
                    }
                    Log.e(ZmanHelper.TAG, "accessSecurityShareModuleByPOST - isSuccess(): false");
                }
            });
        }
    }

    private static Map<String, String> getBaseMap(Context context, boolean z) {
        HashMap hashMap = new HashMap(1);
        hashMap.put(PARAM_LOCATION_ENABLE, String.valueOf(isHideLocationInfoEnable(context)));
        hashMap.put(PARAM_CAMERA_ENABLE, String.valueOf(isHideCameraInfoEnable(context)));
        if (z) {
            hashMap.put(PARAM_TAIL_ENABLE, String.valueOf(isSecurityShareTailEnable(context)));
        }
        return hashMap;
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

    public static boolean getSecurityShareCloudDisable(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), ZMAN_CLOUD_DISABLE, 0) == 1;
    }

    public static boolean getShareTailCloudDisable(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), ZMAN_SHARE_TAIL_CLOUD_DISABLE, 0) == 1;
    }

    private static boolean isHideCameraInfoEnable(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), ZMAN_SHARE_CAMERA_ENABLE, 1) == 1;
    }

    private static boolean isHideLocationInfoEnable(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), ZMAN_SHARE_LOCATION_ENABLE, 1) == 1;
    }

    private static boolean isSecurityShareTailEnable(Context context) {
        return !DeviceUtil.IS_INTERNATIONAL_BUILD && Settings.Secure.getInt(context.getContentResolver(), ZMAN_SHARE_ENABLE, DeviceUtil.IS_STABLE_VERSION ^ true ? 1 : 0) == 1;
    }

    private static boolean isSupportSecurityShare(Context context) {
        int i = sSupportSecurityShare;
        if (i != -1) {
            return i == 1;
        }
        boolean isIntentExist = PackageUtil.isIntentExist(context, new Intent(ACTION_ZMAN_SETTINGS));
        sSupportSecurityShare = isIntentExist ? 1 : 0;
        return isIntentExist;
    }

    public static void setSecurityShareCloudDisable(Context context, int i) {
    }

    public static void setShareTailCloudDisable(Context context, int i) {
        Settings.Secure.putInt(context.getContentResolver(), ZMAN_SHARE_TAIL_CLOUD_DISABLE, i);
    }

    private static String toStr(boolean z) {
        return z ? o.f2310b : o.f2309a;
    }

    public static void trackOnceSettingsChangeEvent(Context context, String str) {
        if (isSupportSecurityShare(context)) {
            Map<String, String> baseMap = getBaseMap(context, false);
            baseMap.put(PARAM_SRC_PACKAGENAME, str);
            AnalyticsUtil.recordCountEvent(CATEGORY_ZMAN, KEY_SECURITY_SHARE_ONCE_SETTINGS_CHANGE, baseMap);
        }
    }

    public static void trackSecurityShareStateEvent(Context context) {
        if (isSupportSecurityShare(context)) {
            AnalyticsUtil.recordCountEvent(CATEGORY_ZMAN, "status", getBaseMap(context, true));
        }
    }

    public static void trackSecuritySharedImageEvent(Context context, String str, boolean z, boolean z2) {
        if (isSupportSecurityShare(context)) {
            Map<String, String> baseMap = getBaseMap(context, true);
            baseMap.put(PARAM_SRC_PACKAGENAME, str);
            baseMap.put(PARAM_IMAGE_HAVE_LOCATION, toStr(z));
            baseMap.put(PARAM_IMAGE_HAVE_CAMERA, toStr(z2));
            AnalyticsUtil.recordCountEvent(CATEGORY_ZMAN, KEY_SECURITY_SHARE_SHARED_IMAGE, baseMap);
        }
    }

    public static void trackSecuritySharedImagesEvent(Context context, String str, boolean z, boolean z2) {
        if (isSupportSecurityShare(context)) {
            Map<String, String> baseMap = getBaseMap(context, true);
            baseMap.put(PARAM_SRC_PACKAGENAME, str);
            baseMap.put(PARAM_IMAGE_HAVE_LOCATION, toStr(z));
            baseMap.put(PARAM_IMAGE_HAVE_CAMERA, toStr(z2));
            AnalyticsUtil.recordCountEvent(CATEGORY_ZMAN, KEY_SECURITY_SHARE_SHARED_IMAGES, baseMap);
        }
    }

    public static void trackViewShowEvent(Context context, String str, String str2) {
        if (isSupportSecurityShare(context)) {
            Map<String, String> baseMap = getBaseMap(context, false);
            baseMap.put(PARAM_SRC_PACKAGENAME, str2);
            AnalyticsUtil.recordCountEvent(CATEGORY_ZMAN, str, baseMap);
        }
    }
}
