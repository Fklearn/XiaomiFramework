package com.android.server.pm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Slog;
import com.android.id.IdentifierManager;
import com.android.server.SystemService;
import com.android.server.pm.CloudControlPreinstallService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.TreeMap;
import miui.os.Build;
import miui.os.MiuiInit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AdvancePreinstallService extends SystemService {
    public static final String ADVANCE_TAG = "miuiAdvancePreload";
    public static final String ADVERT_TAG = "miuiAdvertisement";
    private static final String AD_ONLINE_SERVICE_NAME = "com.xiaomi.preload.services.AdvancePreInstallService";
    private static final String AD_ONLINE_SERVICE_PACKAGE_NAME = "com.xiaomi.preload";
    private static final String ANDROID_VERSION = "androidVersion";
    private static final String APP_TYPE = "appType";
    private static final String CHANNEL = "channel";
    private static final String CONF_ID = "confId";
    private static final boolean DEBUG = true;
    private static final int DEFAULT_CONNECT_TIME_OUT = 2000;
    private static final int DEFAULT_READ_TIME_OUT = 2000;
    private static final String DEVICE = "device";
    private static final String IMEI_ID = "imId";
    private static final String IMEI_MD5 = "imeiMd5";
    private static final String IS_CN = "isCn";
    private static final String KEY_IS_PREINSTALLED = "isPreinstalled";
    private static final String KEY_MIUI_CHANNELPATH = "miuiChannelPath";
    private static final String KEY_TRACK = "preload_appoffine";
    private static final String KEY_TRACK_EVENT = "preload_event";
    private static final String LANG = "lang";
    private static final String MIUI_VERSION = "miuiVersion";
    private static final String MODEL = "model";
    private static final String NETWORK_TYPE = "networkType";
    private static final String NONCE = "nonceStr";
    private static final String OFFLINE_COUNT = "offlineCount";
    private static final String PACKAGE_NAME = "packageName";
    private static final String REGION = "region";
    private static final String REQUEST_TYPE = "request_type";
    private static final String SERVER_ADDRESS = "https://control.preload.xiaomi.com/preload_app_info/get?";
    private static final String SIGN = "sign";
    private static final String SIM_DETECTION_ACTION = "com.miui.action.SIM_DETECTION";
    private static final String TAG = "AdvancePreinstallService";
    /* access modifiers changed from: private */
    public boolean mHasReceived;
    private String mImeiMe5;
    private boolean mIsNetworkConnected;
    private boolean mIsWifiConnected;
    private String mNetworkTypeName;
    /* access modifiers changed from: private */
    public PackageManager mPackageManager;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Slog.i(AdvancePreinstallService.TAG, "onReceive:" + intent);
            if (intent != null && !AdvancePreinstallService.this.mHasReceived) {
                boolean unused = AdvancePreinstallService.this.mHasReceived = true;
                if (AdvancePreinstallService.SIM_DETECTION_ACTION.equals(intent.getAction())) {
                    AdvancePreinstallService.this.handleAdvancePreinstallApps();
                }
            }
        }
    };

    public AdvancePreinstallService(Context context) {
        super(context);
    }

    public void onStart() {
        Slog.i(TAG, "onStart");
    }

    public void onBootPhase(int phase) {
        if (phase == 500) {
            getContext().registerReceiver(this.mReceiver, new IntentFilter(SIM_DETECTION_ACTION));
        }
    }

    /* access modifiers changed from: private */
    public void uninstallAdvancePreinstallApps() {
        for (Map.Entry entry : PreinstallApp.sAdvanceApps.entrySet()) {
            if (entry != null) {
                doUninstallApps((String) entry.getKey(), -1, -1, isAdvanceApps((String) entry.getValue()) ? ADVANCE_TAG : ADVERT_TAG);
            }
        }
    }

    private boolean isAdvanceApps(String apkPath) {
        return apkPath != null && apkPath.contains(ADVANCE_TAG);
    }

    private boolean isAdvertApps(String apkPath) {
        return apkPath != null && apkPath.contains(ADVERT_TAG);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v4, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v2, resolved type: java.lang.String} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String getIMEIMD5() {
        /*
            r3 = this;
            miui.telephony.TelephonyManager r0 = miui.telephony.TelephonyManager.getDefault()
            java.util.List r0 = r0.getImeiList()
            java.lang.String r1 = ""
            if (r0 == 0) goto L_0x0019
            int r2 = r0.size()
            if (r2 <= 0) goto L_0x0019
            java.lang.Object r2 = java.util.Collections.min(r0)
            r1 = r2
            java.lang.String r1 = (java.lang.String) r1
        L_0x0019:
            boolean r2 = android.text.TextUtils.isEmpty(r1)
            if (r2 != 0) goto L_0x0024
            java.lang.String r2 = com.android.server.pm.CloudSignUtil.md5(r1)
            goto L_0x0026
        L_0x0024:
            java.lang.String r2 = ""
        L_0x0026:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.AdvancePreinstallService.getIMEIMD5():java.lang.String");
    }

    /* access modifiers changed from: private */
    public void handleAdvancePreinstallApps() {
        if (!isProvisioned(getContext())) {
            this.mPackageManager = getContext().getPackageManager();
            if (!Build.IS_INTERNATIONAL_BUILD) {
                this.mImeiMe5 = getIMEIMD5();
                AsyncTask.execute(new Runnable() {
                    public void run() {
                        AdvancePreinstallService.this.trackEvent("advance_uninstall_start");
                        AdvancePreinstallService advancePreinstallService = AdvancePreinstallService.this;
                        if (!advancePreinstallService.isNetworkConnected(advancePreinstallService.getContext())) {
                            AdvancePreinstallService.this.uninstallAdvancePreinstallApps();
                        } else {
                            AdvancePreinstallService.this.getPreloadAppInfo();
                        }
                    }
                });
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean isNetworkConnected(Context context) {
        try {
            NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
            if (networkInfo == null) {
                return false;
            }
            if (networkInfo.getType() == 1) {
                this.mIsWifiConnected = true;
            }
            this.mIsNetworkConnected = networkInfo.isAvailable();
            this.mNetworkTypeName = networkInfo.getTypeName();
            return this.mIsNetworkConnected;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private int getNetworkType(Context context) {
        if (!this.mIsNetworkConnected) {
            return 0;
        }
        if (this.mIsWifiConnected) {
            return -1;
        }
        TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
        if (tm != null) {
            return tm.getNetworkType();
        }
        return 0;
    }

    public boolean exists(PackageManager pm, String packageName) {
        if (pm == null || TextUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            if (pm.getApplicationInfo(packageName, 128) != null) {
                return true;
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void doUninstallApps(String packageName, int confId, int offlineCount, String appType) {
        String str = packageName;
        if (!TextUtils.isEmpty(packageName) && this.mPackageManager != null) {
            trackEvent("remove_from_list_begin", packageName, confId, offlineCount, appType);
            PreinstallApp.removeFromPreinstallList(packageName);
            if (!exists(this.mPackageManager, packageName)) {
                trackEvent("package_not_exist", packageName, confId, offlineCount, appType);
                return;
            }
            try {
                this.mPackageManager.setApplicationEnabledSetting(packageName, 2, 0);
            } catch (Exception e) {
                Slog.i(TAG, "disable Package " + packageName + " failed:" + e.toString());
                trackEvent("disable_package_failed", packageName, confId, offlineCount, appType);
            }
            try {
                trackEvent("begin_uninstall", packageName, confId, offlineCount, appType);
                this.mPackageManager.deletePackage(packageName, new PackageDeleteObserver(packageName, confId, offlineCount, appType), 2);
            } catch (Exception e2) {
                Slog.e(TAG, "uninstall Package " + packageName + " failed:" + e2.toString());
                trackEvent("uninstall_failed", packageName, confId, offlineCount, appType);
            }
        }
    }

    private void handleOfflineApps(JSONArray array, String tag) {
        if (array != null && array.length() != 0) {
            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject appInfo = array.getJSONObject(i);
                    if (appInfo != null) {
                        doUninstallApps(appInfo.getString(PACKAGE_NAME), appInfo.getInt(CONF_ID), appInfo.getInt(OFFLINE_COUNT), tag);
                    }
                } catch (JSONException e) {
                    Slog.e(TAG, e.getMessage());
                }
            }
        }
    }

    private void handleAdOnlineInfo() {
        if (isAdOnlineServiceAvailable(getContext())) {
            Intent intent = new Intent();
            intent.setClassName(AD_ONLINE_SERVICE_PACKAGE_NAME, AD_ONLINE_SERVICE_NAME);
            getContext().startForegroundService(intent);
        }
    }

    /* access modifiers changed from: private */
    public void trackAdvancePreloadSuccess(String packageName) {
        CloudControlPreinstallService.Action action = new CloudControlPreinstallService.Action();
        action.addParam(KEY_TRACK_EVENT, "install_success");
        action.addParam(REQUEST_TYPE, 1);
        action.addParam(IMEI_MD5, this.mImeiMe5);
        action.addParam(PACKAGE_NAME, packageName);
        action.addParam(NETWORK_TYPE, this.mNetworkTypeName);
        action.addParam(APP_TYPE, ADVANCE_TAG);
        action.addParam("channel", Build.getCustVariant());
        action.addParam(IMEI_ID, IdentifierManager.getOAID(getContext()));
        CloudControlPreinstallService.trackSystem(getContext(), KEY_TRACK, action);
    }

    private void handleAdvancePreloadTrack(JSONArray array) {
        for (Map.Entry entry : PreinstallApp.sAdvanceApps.entrySet()) {
            if (entry != null && isAdvanceApps((String) entry.getValue())) {
                String existPkgName = (String) entry.getKey();
                if (!isPackageToRemove(existPkgName, array) && exists(this.mPackageManager, existPkgName)) {
                    trackAdvancePreloadSuccess(existPkgName);
                }
            }
        }
    }

    private void handleAllInstallSuccess() {
        for (Map.Entry entry : PreinstallApp.sAdvanceApps.entrySet()) {
            if (entry != null && isAdvanceApps((String) entry.getValue())) {
                String existPkgName = (String) entry.getKey();
                if (exists(this.mPackageManager, existPkgName)) {
                    trackAdvancePreloadSuccess(existPkgName);
                }
            }
        }
    }

    private boolean isPackageToRemove(String packageName, JSONArray array) {
        if (array == null || array.length() == 0) {
            return false;
        }
        int deleteCount = array.length();
        for (int i = 0; i < deleteCount; i++) {
            try {
                JSONObject appInfo = array.getJSONObject(i);
                if (appInfo != null) {
                    if (TextUtils.equals(appInfo.getString(PACKAGE_NAME), packageName)) {
                        return true;
                    }
                }
            } catch (JSONException e) {
                Slog.e(TAG, e.getMessage());
            }
        }
        return false;
    }

    private boolean isAdOnlineServiceAvailable(Context context) {
        Intent intent = new Intent();
        intent.setClassName(AD_ONLINE_SERVICE_PACKAGE_NAME, AD_ONLINE_SERVICE_NAME);
        PackageManager pm = context.getPackageManager();
        if (pm != null && pm.queryIntentServices(intent, 0).size() > 0) {
            return true;
        }
        return false;
    }

    public void trackEvent(String event, String packageName, int confId, int offlineCount, String tag) {
        CloudControlPreinstallService.Action action = new CloudControlPreinstallService.Action();
        action.addParam(KEY_TRACK_EVENT, event);
        action.addParam(PACKAGE_NAME, packageName);
        action.addParam(CONF_ID, confId);
        action.addParam(OFFLINE_COUNT, offlineCount);
        action.addParam(APP_TYPE, tag);
        action.addParam(REQUEST_TYPE, 1);
        action.addParam("channel", Build.getCustVariant());
        action.addParam(KEY_IS_PREINSTALLED, String.valueOf(MiuiInit.isPreinstalledPackage(packageName)));
        action.addParam(KEY_MIUI_CHANNELPATH, String.valueOf(MiuiInit.getMiuiChannelPath(packageName)));
        action.addParam(IMEI_MD5, this.mImeiMe5);
        action.addParam(IMEI_ID, IdentifierManager.getOAID(getContext()));
        action.addParam(NETWORK_TYPE, this.mNetworkTypeName);
        CloudControlPreinstallService.trackSystem(getContext(), KEY_TRACK, action);
    }

    /* access modifiers changed from: private */
    public void trackEvent(String event) {
        CloudControlPreinstallService.Action action = new CloudControlPreinstallService.Action();
        action.addParam(KEY_TRACK_EVENT, event);
        action.addParam(REQUEST_TYPE, 1);
        action.addParam(IMEI_MD5, this.mImeiMe5);
        action.addParam(NETWORK_TYPE, this.mNetworkTypeName);
        action.addParam("channel", Build.getCustVariant());
        action.addParam(IMEI_ID, IdentifierManager.getOAID(getContext()));
        CloudControlPreinstallService.trackSystem(getContext(), KEY_TRACK, action);
    }

    /* access modifiers changed from: private */
    public void getPreloadAppInfo() {
        ConnectEntity entity = getConnectEntity();
        if (entity != null) {
            String url = SERVER_ADDRESS + entity.toString();
            HttpURLConnection conn = null;
            BufferedReader br = null;
            try {
                trackEvent("request_connect_start");
                conn = (HttpURLConnection) new URL(url).openConnection();
                conn.setConnectTimeout(2000);
                conn.setReadTimeout(2000);
                conn.setRequestMethod("GET");
                conn.connect();
                if (conn.getResponseCode() == 200) {
                    trackEvent("request_connect_success");
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()), 1024);
                    StringBuilder sb = new StringBuilder();
                    while (true) {
                        String readLine = br.readLine();
                        String line = readLine;
                        if (readLine == null) {
                            break;
                        }
                        sb.append(line);
                    }
                    Slog.i(TAG, "result:" + sb.toString());
                    JSONObject result = new JSONObject(sb.toString());
                    int code = result.getInt("code");
                    String message = result.getString("message");
                    if (code != 0 || !"Success".equals(message)) {
                        trackEvent("request_list_failed");
                        uninstallAdvancePreinstallApps();
                        Slog.e(TAG, "advance_request result is failed");
                    } else {
                        trackEvent("request_list_success");
                        JSONObject data = result.getJSONObject("data");
                        if (data == null) {
                            trackEvent("uninstall_list_empty");
                            handleAllInstallSuccess();
                            conn.disconnect();
                            closeBufferedReader(br);
                            return;
                        }
                        JSONArray previewOfflineApps = data.getJSONArray("previewOfflineApps");
                        JSONArray adOfflineApps = data.getJSONArray("adOfflineApps");
                        if (previewOfflineApps == null || previewOfflineApps.length() == 0) {
                            if (adOfflineApps != null) {
                                if (adOfflineApps.length() == 0) {
                                }
                            }
                            trackEvent("uninstall_list_empty");
                            handleAllInstallSuccess();
                            conn.disconnect();
                            closeBufferedReader(br);
                            return;
                        }
                        handleOfflineApps(previewOfflineApps, ADVANCE_TAG);
                        handleOfflineApps(adOfflineApps, ADVERT_TAG);
                        handleAdvancePreloadTrack(previewOfflineApps);
                        handleAdOnlineInfo();
                    }
                } else {
                    trackEvent("request_connect_failed");
                    uninstallAdvancePreinstallApps();
                    Slog.i(TAG, "server can not connected");
                }
                conn.disconnect();
                if (br == null) {
                    return;
                }
            } catch (ProtocolException e) {
                trackEvent("request_connect_exception");
                uninstallAdvancePreinstallApps();
                e.printStackTrace();
                if (conn != null) {
                    conn.disconnect();
                }
                if (br == null) {
                    return;
                }
            } catch (IOException e2) {
                trackEvent("request_connect_exception");
                uninstallAdvancePreinstallApps();
                e2.printStackTrace();
                if (conn != null) {
                    conn.disconnect();
                }
                if (br == null) {
                    return;
                }
            } catch (JSONException e3) {
                trackEvent("json_exception");
                uninstallAdvancePreinstallApps();
                e3.printStackTrace();
                if (conn != null) {
                    conn.disconnect();
                }
                if (br == null) {
                    return;
                }
            } catch (Exception e4) {
                trackEvent("request_connect_exception");
                uninstallAdvancePreinstallApps();
                e4.printStackTrace();
                if (conn != null) {
                    conn.disconnect();
                }
                if (br == null) {
                    return;
                }
            } catch (Throwable th) {
                if (conn != null) {
                    conn.disconnect();
                }
                if (br != null) {
                    closeBufferedReader(br);
                }
                throw th;
            }
            closeBufferedReader(br);
        }
    }

    private ConnectEntity getConnectEntity() {
        try {
            trackEvent("get_device_info");
            String device = Build.DEVICE;
            String miuiVersion = Build.VERSION.INCREMENTAL;
            String androidVersion = Build.VERSION.RELEASE;
            String model = miui.os.Build.MODEL;
            String channel = miui.os.Build.getCustVariant();
            String lang = Locale.getDefault().getLanguage();
            String nonceStr = CloudSignUtil.getNonceStr();
            boolean isCn = !miui.os.Build.IS_INTERNATIONAL_BUILD;
            String region = isCn ? "CN" : miui.os.Build.getCustVariant().toUpperCase();
            int networkType = getNetworkType(getContext());
            String imeiId = IdentifierManager.getOAID(getContext());
            String sign = CloudSignUtil.getSign(getParamsMap(this.mImeiMe5, imeiId, model, device, miuiVersion, androidVersion, channel, region, networkType, isCn, lang), nonceStr);
            if (TextUtils.isEmpty(sign)) {
                return null;
            }
            return new ConnectEntity(this.mImeiMe5, imeiId, device, miuiVersion, channel, lang, nonceStr, sign, isCn, region, androidVersion, model, networkType);
        } catch (NoSuchElementException ex) {
            ex.printStackTrace();
            trackEvent("get_device_info_failed");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            trackEvent("get_device_info_failed");
            return null;
        }
    }

    private TreeMap<String, Object> getParamsMap(String imeiMd5, String imeiId, String model, String device, String miuiVersion, String androidVersion, String channel, String region, int networkType, boolean isCn, String lang) {
        TreeMap<String, Object> params = new TreeMap<>();
        params.put(IMEI_MD5, imeiMd5);
        params.put(IMEI_ID, imeiId);
        params.put(MODEL, model);
        params.put("device", device);
        params.put("miuiVersion", miuiVersion);
        params.put(ANDROID_VERSION, androidVersion);
        params.put("channel", channel);
        params.put("region", region);
        params.put("isCn", Boolean.valueOf(isCn));
        params.put("lang", lang);
        params.put(NETWORK_TYPE, Integer.valueOf(networkType));
        return params;
    }

    private void closeBufferedReader(BufferedReader br) {
        if (br != null) {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isProvisioned(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "device_provisioned", 0) != 0;
    }

    private static class ConnectEntity {
        private boolean isCn;
        private String mAndroidVersion;
        private String mChannel;
        private String mDevice;
        private String mImeiId;
        private String mImeiMd5;
        private String mLang;
        private String mModel;
        private int mNetworkType;
        private String mNonceStr;
        private String mRegion;
        private String mSign;
        private String miuiVersion;

        ConnectEntity(String imeiMd5, String imeiId, String device, String miuiVersion2, String channel, String lang, String nonceStr, String sign, boolean isCn2, String region, String androidVersion, String model, int networkType) {
            this.mImeiMd5 = imeiMd5;
            this.mImeiId = imeiId;
            this.mDevice = device;
            this.miuiVersion = miuiVersion2;
            this.mChannel = channel;
            this.mLang = lang;
            this.mNonceStr = nonceStr;
            this.mSign = sign;
            this.isCn = isCn2;
            this.mRegion = region;
            this.mAndroidVersion = androidVersion;
            this.mModel = model;
            this.mNetworkType = networkType;
        }

        public String toString() {
            return "imeiMd5=" + this.mImeiMd5 + "&" + AdvancePreinstallService.IMEI_ID + "=" + this.mImeiId + "&" + "device" + "=" + this.mDevice + "&" + "miuiVersion" + "=" + this.miuiVersion + "&" + "channel" + "=" + this.mChannel + "&" + "lang" + "=" + this.mLang + "&" + "nonceStr" + "=" + this.mNonceStr + "&" + "sign" + "=" + this.mSign + "&" + "isCn" + "=" + this.isCn + "&" + "region" + "=" + this.mRegion + "&" + AdvancePreinstallService.NETWORK_TYPE + "=" + this.mNetworkType + "&" + AdvancePreinstallService.ANDROID_VERSION + "=" + this.mAndroidVersion + "&" + AdvancePreinstallService.MODEL + "=" + this.mModel;
        }
    }

    public class PackageDeleteObserver extends IPackageDeleteObserver.Stub {
        private String mAppType;
        private int mConfId;
        private int mOfflineCount;
        private String mPackageName;

        public PackageDeleteObserver(String packageName, int confId, int offlineCount, String appType) {
            this.mPackageName = packageName;
            this.mConfId = confId;
            this.mOfflineCount = offlineCount;
            this.mAppType = appType;
        }

        public void packageDeleted(String packageName, int returnCode) {
            if (returnCode >= 0) {
                AdvancePreinstallService.this.trackEvent("uninstall_success", this.mPackageName, this.mConfId, this.mOfflineCount, this.mAppType);
                Slog.i(AdvancePreinstallService.TAG, "Package " + packageName + " was uninstalled.");
                return;
            }
            AdvancePreinstallService.this.trackEvent("uninstall_failed", this.mPackageName, this.mConfId, this.mOfflineCount, this.mAppType);
            AdvancePreinstallService advancePreinstallService = AdvancePreinstallService.this;
            if (advancePreinstallService.exists(advancePreinstallService.mPackageManager, this.mPackageName)) {
                AdvancePreinstallService.this.trackAdvancePreloadSuccess(this.mPackageName);
            }
            Slog.e(AdvancePreinstallService.TAG, "Package uninstall failed " + packageName + ", returnCode " + returnCode);
        }
    }
}
