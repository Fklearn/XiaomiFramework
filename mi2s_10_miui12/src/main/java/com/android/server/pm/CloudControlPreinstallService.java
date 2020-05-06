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
import android.os.SystemProperties;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Slog;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.SystemServiceManager;
import com.android.server.voiceinteraction.DatabaseHelper;
import com.android.server.wm.ActivityTaskManagerInternal;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;
import miui.os.Build;
import miui.os.MiuiInit;
import miui.telephony.TelephonyManager;
import org.json.JSONObject;

public class CloudControlPreinstallService extends SystemService {
    public static final String CLAUSE_AGREED = "clause_agreed";
    public static final String CLAUSE_AGREED_ACTION = "com.miui.clause_agreed";
    private static final String CONF_ID = "confId";
    private static final boolean DEBUG = true;
    private static final int DEFAULT_CONNECT_TIME_OUT = 2000;
    private static final int DEFAULT_READ_TIME_OUT = 2000;
    private static final String IMEI_MD5 = "imeiMd5";
    private static final String JSON_EXCEPTION = "json_exception";
    private static final int LOGTYPE_EVENT = 0;
    private static final String NETWORK_TYPE = "networkType";
    private static final String OFFLINE_COUNT = "offlineCount";
    private static final String PACKAGE_KEY = "com.xiaomi.preload";
    private static final String PACKAGE_NAME = "packageName";
    private static final String REQUEST_CONNECT_EXCEPTION = "request_connect_exception";
    private static final String SERVER_ADDRESS = "https://control.preload.xiaomi.com/offline_app_list/get?";
    private static final String TAG = "CloudControlPreinstall";
    private static final String TRACK_EVENT_KEY = "preload_event";
    private static final String TRACK_KEY = "preload_appoffine";
    private static final String UNINSTALL_FAILED = "uninstall_failed";
    private String mImeiMe5;
    private String mNetworkType;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            Slog.i(CloudControlPreinstallService.TAG, "onReceive:" + intent);
            if (intent != null) {
                String action = intent.getAction();
                if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
                    if (MiuiSettings.Global.getBoolean(CloudControlPreinstallService.this.getContext().getContentResolver(), CloudControlPreinstallService.CLAUSE_AGREED)) {
                        CloudControlPreinstallService.this.uninstallPreinstallApps();
                    }
                } else if (CloudControlPreinstallService.CLAUSE_AGREED_ACTION.equals(action)) {
                    CloudControlPreinstallService.this.uninstallPreinstallApps();
                }
            }
        }
    };

    public static class Action {
        protected static final String ACTION_KEY = "_action_";
        protected static final String CATEGORY = "_category_";
        protected static final String EVENT_ID = "_event_id_";
        protected static final String LABEL = "_label_";
        protected static final String VALUE = "_value_";
        private JSONObject mContent = new JSONObject();
        private JSONObject mExtra = new JSONObject();
        private Set<String> sKeywords = new HashSet();

        public Action() {
            this.sKeywords.add(EVENT_ID);
            this.sKeywords.add(CATEGORY);
            this.sKeywords.add(ACTION_KEY);
            this.sKeywords.add(LABEL);
            this.sKeywords.add(VALUE);
        }

        public Action addParam(String key, JSONObject value) {
            ensureKey(key);
            addContent(key, (Object) value);
            return this;
        }

        public Action addParam(String key, int value) {
            ensureKey(key);
            addContent(key, value);
            return this;
        }

        public Action addParam(String key, long value) {
            ensureKey(key);
            addContent(key, value);
            return this;
        }

        public Action addParam(String key, String value) {
            ensureKey(key);
            addContent(key, (Object) value);
            return this;
        }

        /* access modifiers changed from: package-private */
        public void addContent(String key, int value) {
            if (!TextUtils.isEmpty(key)) {
                try {
                    this.mContent.put(key, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void addContent(String key, long value) {
            if (!TextUtils.isEmpty(key)) {
                try {
                    this.mContent.put(key, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void addContent(String key, Object value) {
            if (!TextUtils.isEmpty(key)) {
                try {
                    this.mContent.put(key, value);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        private void ensureKey(String key) {
            if (!TextUtils.isEmpty(key) && this.sKeywords.contains(key)) {
                throw new IllegalArgumentException("this key " + key + " is built-in, please pick another key.");
            }
        }

        /* access modifiers changed from: package-private */
        public final JSONObject getContent() {
            return this.mContent;
        }

        /* access modifiers changed from: package-private */
        public final JSONObject getExtra() {
            return this.mExtra;
        }
    }

    public void trackEvent(String event, UninstallApp app) {
        Action action = new Action();
        action.addParam(TRACK_EVENT_KEY, event);
        action.addParam(PACKAGE_NAME, app.packageName);
        action.addParam(CONF_ID, app.confId);
        action.addParam(OFFLINE_COUNT, app.offlineCount);
        action.addParam(ConnectEntity.CHANNEL, Build.getCustVariant());
        action.addParam("isPreinstalled", String.valueOf(MiuiInit.isPreinstalledPackage(app.packageName)));
        action.addParam("miuiChannelPath", String.valueOf(MiuiInit.getMiuiChannelPath(app.packageName)));
        action.addParam(IMEI_MD5, this.mImeiMe5);
        action.addParam(NETWORK_TYPE, this.mNetworkType);
        trackSystem(getContext(), TRACK_KEY, action);
    }

    public void trackEvent(String event) {
        Action action = new Action();
        action.addParam(TRACK_EVENT_KEY, event);
        action.addParam(ConnectEntity.CHANNEL, Build.getCustVariant());
        action.addParam(IMEI_MD5, this.mImeiMe5);
        action.addParam(NETWORK_TYPE, this.mNetworkType);
        trackSystem(getContext(), TRACK_KEY, action);
    }

    public static void trackSystem(Context context, String key, Action action) {
        try {
            Intent intent = new Intent();
            intent.setClassName("com.miui.analytics", "com.miui.analytics.EventService");
            intent.putExtra("key", key != null ? key : "");
            intent.putExtra(ActivityTaskManagerInternal.ASSIST_KEY_CONTENT, action.getContent().toString());
            Slog.i(TAG, action.getContent().toString());
            intent.putExtra("extra", action.getExtra().toString());
            intent.putExtra("appid", PACKAGE_KEY);
            intent.putExtra(DatabaseHelper.SoundModelContract.KEY_TYPE, 0);
            context.startService(intent);
        } catch (Exception e) {
            Slog.e(TAG, "track system error!", e);
        }
    }

    public static class UninstallApp {
        int confId;
        String custVariant;
        int offlineCount;
        String packageName;

        public UninstallApp(String packageName2, String custVariant2, int confId2, int offlineCount2) {
            this.packageName = packageName2;
            this.custVariant = custVariant2;
            this.confId = confId2;
            this.offlineCount = offlineCount2;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof UninstallApp)) {
                return false;
            }
            UninstallApp app = (UninstallApp) obj;
            if (!TextUtils.equals(this.packageName, app.packageName) || !TextUtils.equals(this.custVariant, app.custVariant)) {
                return false;
            }
            return true;
        }
    }

    public static class ConnectEntity {
        public static final String CHANNEL = "channel";
        public static final String DEVICE = "device";
        public static final String IS_CN = "isCn";
        public static final String LANG = "lang";
        public static final String MIUI_VERSION = "miuiVersion";
        public static final String NONCE = "nonceStr";
        public static final String REGION = "region";
        public static final String SIGN = "sign";
        private boolean isCn;
        private String mChannel;
        private String mDevice;
        private String mImeiMd5;
        private String mLang;
        private String mNonceStr;
        private String mRegion;
        private String mSign;
        private String miuiVersion;

        public ConnectEntity(String imeiMd5, String device, String miuiVersion2, String channel, String lang, String nonceStr, String sign, boolean isCn2, String region) {
            this.mImeiMd5 = imeiMd5;
            this.mDevice = device;
            this.miuiVersion = miuiVersion2;
            this.mChannel = channel;
            this.mLang = lang;
            this.mNonceStr = nonceStr;
            this.mSign = sign;
            this.isCn = isCn2;
            this.mRegion = region;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("imeiMd5=" + this.mImeiMd5 + "&");
            builder.append("device=" + this.mDevice + "&");
            builder.append("miuiVersion=" + this.miuiVersion + "&");
            builder.append("channel=" + this.mChannel + "&");
            builder.append("lang=" + this.mLang + "&");
            builder.append("nonceStr=" + this.mNonceStr + "&");
            builder.append("sign=" + this.mSign + "&");
            builder.append("isCn=" + this.isCn + "&");
            StringBuilder sb = new StringBuilder();
            sb.append("region=");
            sb.append(this.mRegion);
            builder.append(sb.toString());
            return builder.toString();
        }
    }

    private boolean isNetworkConnected(Context context) {
        try {
            NetworkInfo networkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
            if (networkInfo != null) {
                this.mNetworkType = networkInfo.getTypeName();
                return networkInfo.isAvailable();
            }
            this.mNetworkType = "";
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public CloudControlPreinstallService(Context context) {
        super(context);
    }

    public static void startCloudControlService() {
        SystemServiceManager systemServiceManager = (SystemServiceManager) LocalServices.getService(SystemServiceManager.class);
        systemServiceManager.startService(CloudControlPreinstallService.class);
        systemServiceManager.startService(AdvancePreinstallService.class);
    }

    public void onStart() {
        Slog.i(TAG, "onStart");
    }

    public void onBootPhase(int phase) {
        if (phase == 500 && !isProvisioned(getContext())) {
            IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
            intentFilter.addAction(CLAUSE_AGREED_ACTION);
            getContext().registerReceiver(this.mReceiver, intentFilter);
        }
    }

    public void uninstallPreinstallApps() {
        if (!isProvisioned(getContext()) && !Build.IS_INTERNATIONAL_BUILD && isNetworkConnected(getContext())) {
            AsyncTask.execute(new Runnable() {
                public void run() {
                    CloudControlPreinstallService.this.trackEvent("cloud_uninstall_start");
                    CloudControlPreinstallService.this.uninstallAppsUpdateList(CloudControlPreinstallService.this.getUninstallApps());
                }
            });
        }
    }

    private ConnectEntity getConnectEntity() {
        ConnectEntity connectEntity;
        ConnectEntity entity = null;
        try {
            trackEvent("get_device_info");
            String imei = (String) Collections.min(TelephonyManager.getDefault().getImeiList());
            this.mImeiMe5 = !TextUtils.isEmpty(imei) ? CloudSignUtil.md5(imei) : "";
            String device = Build.DEVICE;
            String miuiVersion = Build.VERSION.INCREMENTAL;
            String channel = miui.os.Build.getCustVariant();
            String lang = Locale.getDefault().getLanguage();
            String nonceStr = CloudSignUtil.getNonceStr();
            boolean isCn = !miui.os.Build.IS_INTERNATIONAL_BUILD;
            String region = isCn ? "CN" : miui.os.Build.getCustVariant().toUpperCase();
            String sign = CloudSignUtil.getSign(this.mImeiMe5, device, miuiVersion, channel, region, isCn, lang, nonceStr);
            if (TextUtils.isEmpty(sign)) {
                connectEntity = null;
            } else {
                connectEntity = new ConnectEntity(this.mImeiMe5, device, miuiVersion, channel, lang, nonceStr, sign, isCn, region);
            }
            entity = connectEntity;
        } catch (NoSuchElementException ex) {
            ex.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (entity == null) {
            trackEvent("get_device_info_failed");
        }
        return entity;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:117:0x023f, code lost:
        if (r7 == null) goto L_0x0244;
     */
    /* JADX WARNING: Removed duplicated region for block: B:101:0x0212 A[Catch:{ ProtocolException -> 0x0234, IOException -> 0x0226, JSONException -> 0x0213, Exception -> 0x0205, all -> 0x01ff, all -> 0x0224 }] */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x0223 A[Catch:{ ProtocolException -> 0x0234, IOException -> 0x0226, JSONException -> 0x0213, Exception -> 0x0205, all -> 0x01ff, all -> 0x0224 }] */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x0233 A[Catch:{ ProtocolException -> 0x0234, IOException -> 0x0226, JSONException -> 0x0213, Exception -> 0x0205, all -> 0x01ff, all -> 0x0224 }] */
    /* JADX WARNING: Removed duplicated region for block: B:122:0x024b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.android.server.pm.CloudControlPreinstallService.UninstallApp> getUninstallApps() {
        /*
            r22 = this;
            r1 = r22
            java.lang.String r2 = "request_connect_exception"
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r3 = r0
            com.android.server.pm.CloudControlPreinstallService$ConnectEntity r4 = r22.getConnectEntity()
            if (r4 != 0) goto L_0x0012
            return r3
        L_0x0012:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r5 = "https://control.preload.xiaomi.com/offline_app_list/get?"
            r0.append(r5)
            java.lang.String r5 = r4.toString()
            r0.append(r5)
            java.lang.String r5 = r0.toString()
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r6 = "url:"
            r0.append(r6)
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            java.lang.String r6 = "CloudControlPreinstall"
            android.util.Slog.i(r6, r0)
            r7 = 0
            r8 = 0
            java.lang.String r0 = "request_connect_start"
            r1.trackEvent(r0)     // Catch:{ ProtocolException -> 0x0234, IOException -> 0x0226, JSONException -> 0x0213, Exception -> 0x0205, all -> 0x01ff }
            java.net.URL r0 = new java.net.URL     // Catch:{ ProtocolException -> 0x0234, IOException -> 0x0226, JSONException -> 0x0213, Exception -> 0x0205, all -> 0x01ff }
            r0.<init>(r5)     // Catch:{ ProtocolException -> 0x0234, IOException -> 0x0226, JSONException -> 0x0213, Exception -> 0x0205, all -> 0x01ff }
            java.net.URLConnection r0 = r0.openConnection()     // Catch:{ ProtocolException -> 0x0234, IOException -> 0x0226, JSONException -> 0x0213, Exception -> 0x0205, all -> 0x01ff }
            java.net.HttpURLConnection r0 = (java.net.HttpURLConnection) r0     // Catch:{ ProtocolException -> 0x0234, IOException -> 0x0226, JSONException -> 0x0213, Exception -> 0x0205, all -> 0x01ff }
            r7 = r0
            r0 = 2000(0x7d0, float:2.803E-42)
            r7.setConnectTimeout(r0)     // Catch:{ ProtocolException -> 0x0234, IOException -> 0x0226, JSONException -> 0x0213, Exception -> 0x0205, all -> 0x01ff }
            r7.setReadTimeout(r0)     // Catch:{ ProtocolException -> 0x0234, IOException -> 0x0226, JSONException -> 0x0213, Exception -> 0x0205, all -> 0x01ff }
            java.lang.String r0 = "GET"
            r7.setRequestMethod(r0)     // Catch:{ ProtocolException -> 0x0234, IOException -> 0x0226, JSONException -> 0x0213, Exception -> 0x0205, all -> 0x01ff }
            r7.connect()     // Catch:{ ProtocolException -> 0x0234, IOException -> 0x0226, JSONException -> 0x0213, Exception -> 0x0205, all -> 0x01ff }
            int r0 = r7.getResponseCode()     // Catch:{ ProtocolException -> 0x0234, IOException -> 0x0226, JSONException -> 0x0213, Exception -> 0x0205, all -> 0x01ff }
            r9 = 200(0xc8, float:2.8E-43)
            if (r0 != r9) goto L_0x01e4
            java.lang.String r9 = "request_connect_success"
            r1.trackEvent(r9)     // Catch:{ ProtocolException -> 0x0234, IOException -> 0x0226, JSONException -> 0x0213, Exception -> 0x0205, all -> 0x01ff }
            java.io.BufferedReader r9 = new java.io.BufferedReader     // Catch:{ ProtocolException -> 0x0234, IOException -> 0x0226, JSONException -> 0x0213, Exception -> 0x0205, all -> 0x01ff }
            java.io.InputStreamReader r10 = new java.io.InputStreamReader     // Catch:{ ProtocolException -> 0x0234, IOException -> 0x0226, JSONException -> 0x0213, Exception -> 0x0205, all -> 0x01ff }
            java.io.InputStream r11 = r7.getInputStream()     // Catch:{ ProtocolException -> 0x0234, IOException -> 0x0226, JSONException -> 0x0213, Exception -> 0x0205, all -> 0x01ff }
            r10.<init>(r11)     // Catch:{ ProtocolException -> 0x0234, IOException -> 0x0226, JSONException -> 0x0213, Exception -> 0x0205, all -> 0x01ff }
            r11 = 1024(0x400, float:1.435E-42)
            r9.<init>(r10, r11)     // Catch:{ ProtocolException -> 0x0234, IOException -> 0x0226, JSONException -> 0x0213, Exception -> 0x0205, all -> 0x01ff }
            r8 = r9
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ ProtocolException -> 0x01dc, IOException -> 0x01d4, JSONException -> 0x01cc, Exception -> 0x01c4, all -> 0x01bb }
            r9.<init>()     // Catch:{ ProtocolException -> 0x01dc, IOException -> 0x01d4, JSONException -> 0x01cc, Exception -> 0x01c4, all -> 0x01bb }
        L_0x0087:
            java.lang.String r10 = r8.readLine()     // Catch:{ ProtocolException -> 0x01dc, IOException -> 0x01d4, JSONException -> 0x01cc, Exception -> 0x01c4, all -> 0x01bb }
            r11 = r10
            if (r10 == 0) goto L_0x00b5
            r9.append(r11)     // Catch:{ ProtocolException -> 0x00ae, IOException -> 0x00a7, JSONException -> 0x00a0, Exception -> 0x0099, all -> 0x0092 }
            goto L_0x0087
        L_0x0092:
            r0 = move-exception
            r16 = r4
            r18 = r5
            goto L_0x0249
        L_0x0099:
            r0 = move-exception
            r16 = r4
            r18 = r5
            goto L_0x020a
        L_0x00a0:
            r0 = move-exception
            r16 = r4
            r18 = r5
            goto L_0x0218
        L_0x00a7:
            r0 = move-exception
            r16 = r4
            r18 = r5
            goto L_0x022b
        L_0x00ae:
            r0 = move-exception
            r16 = r4
            r18 = r5
            goto L_0x0239
        L_0x00b5:
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ ProtocolException -> 0x01dc, IOException -> 0x01d4, JSONException -> 0x01cc, Exception -> 0x01c4, all -> 0x01bb }
            r10.<init>()     // Catch:{ ProtocolException -> 0x01dc, IOException -> 0x01d4, JSONException -> 0x01cc, Exception -> 0x01c4, all -> 0x01bb }
            java.lang.String r12 = "result:"
            r10.append(r12)     // Catch:{ ProtocolException -> 0x01dc, IOException -> 0x01d4, JSONException -> 0x01cc, Exception -> 0x01c4, all -> 0x01bb }
            java.lang.String r12 = r9.toString()     // Catch:{ ProtocolException -> 0x01dc, IOException -> 0x01d4, JSONException -> 0x01cc, Exception -> 0x01c4, all -> 0x01bb }
            r10.append(r12)     // Catch:{ ProtocolException -> 0x01dc, IOException -> 0x01d4, JSONException -> 0x01cc, Exception -> 0x01c4, all -> 0x01bb }
            java.lang.String r10 = r10.toString()     // Catch:{ ProtocolException -> 0x01dc, IOException -> 0x01d4, JSONException -> 0x01cc, Exception -> 0x01c4, all -> 0x01bb }
            android.util.Slog.i(r6, r10)     // Catch:{ ProtocolException -> 0x01dc, IOException -> 0x01d4, JSONException -> 0x01cc, Exception -> 0x01c4, all -> 0x01bb }
            org.json.JSONObject r10 = new org.json.JSONObject     // Catch:{ ProtocolException -> 0x01dc, IOException -> 0x01d4, JSONException -> 0x01cc, Exception -> 0x01c4, all -> 0x01bb }
            java.lang.String r12 = r9.toString()     // Catch:{ ProtocolException -> 0x01dc, IOException -> 0x01d4, JSONException -> 0x01cc, Exception -> 0x01c4, all -> 0x01bb }
            r10.<init>(r12)     // Catch:{ ProtocolException -> 0x01dc, IOException -> 0x01d4, JSONException -> 0x01cc, Exception -> 0x01c4, all -> 0x01bb }
            java.lang.String r12 = "code"
            int r12 = r10.getInt(r12)     // Catch:{ ProtocolException -> 0x01dc, IOException -> 0x01d4, JSONException -> 0x01cc, Exception -> 0x01c4, all -> 0x01bb }
            java.lang.String r13 = "message"
            java.lang.String r13 = r10.getString(r13)     // Catch:{ ProtocolException -> 0x01dc, IOException -> 0x01d4, JSONException -> 0x01cc, Exception -> 0x01c4, all -> 0x01bb }
            if (r12 != 0) goto L_0x018b
            java.lang.String r14 = "Success"
            boolean r14 = r14.equals(r13)     // Catch:{ ProtocolException -> 0x01dc, IOException -> 0x01d4, JSONException -> 0x01cc, Exception -> 0x01c4, all -> 0x01bb }
            if (r14 == 0) goto L_0x018b
            java.lang.String r6 = "request_list_success"
            r1.trackEvent(r6)     // Catch:{ ProtocolException -> 0x01dc, IOException -> 0x01d4, JSONException -> 0x01cc, Exception -> 0x01c4, all -> 0x01bb }
            java.lang.String r6 = "data"
            org.json.JSONObject r6 = r10.getJSONObject(r6)     // Catch:{ ProtocolException -> 0x01dc, IOException -> 0x01d4, JSONException -> 0x01cc, Exception -> 0x01c4, all -> 0x01bb }
            java.lang.String r14 = "appList"
            org.json.JSONArray r14 = r6.getJSONArray(r14)     // Catch:{ ProtocolException -> 0x01dc, IOException -> 0x01d4, JSONException -> 0x01cc, Exception -> 0x01c4, all -> 0x01bb }
            java.lang.String r15 = "channel"
            java.lang.String r15 = r6.getString(r15)     // Catch:{ ProtocolException -> 0x01dc, IOException -> 0x01d4, JSONException -> 0x01cc, Exception -> 0x01c4, all -> 0x01bb }
            r16 = 0
            r17 = r0
            r0 = r16
        L_0x010c:
            r16 = r4
            int r4 = r14.length()     // Catch:{ ProtocolException -> 0x0184, IOException -> 0x017d, JSONException -> 0x0176, Exception -> 0x016f, all -> 0x0168 }
            if (r0 >= r4) goto L_0x0161
            org.json.JSONObject r4 = r14.getJSONObject(r0)     // Catch:{ ProtocolException -> 0x0184, IOException -> 0x017d, JSONException -> 0x0176, Exception -> 0x016f, all -> 0x0168 }
            r18 = r5
            java.lang.String r5 = "packageName"
            java.lang.String r5 = r4.getString(r5)     // Catch:{ ProtocolException -> 0x015c, IOException -> 0x0157, JSONException -> 0x0152, Exception -> 0x014d, all -> 0x0148 }
            r19 = r6
            java.lang.String r6 = "confId"
            int r6 = r4.getInt(r6)     // Catch:{ ProtocolException -> 0x015c, IOException -> 0x0157, JSONException -> 0x0152, Exception -> 0x014d, all -> 0x0148 }
            r20 = r8
            java.lang.String r8 = "offlineCount"
            int r8 = r4.getInt(r8)     // Catch:{ ProtocolException -> 0x01b6, IOException -> 0x01b1, JSONException -> 0x01ac, Exception -> 0x01a7, all -> 0x01a2 }
            r21 = r4
            com.android.server.pm.CloudControlPreinstallService$UninstallApp r4 = new com.android.server.pm.CloudControlPreinstallService$UninstallApp     // Catch:{ ProtocolException -> 0x01b6, IOException -> 0x01b1, JSONException -> 0x01ac, Exception -> 0x01a7, all -> 0x01a2 }
            r4.<init>(r5, r15, r6, r8)     // Catch:{ ProtocolException -> 0x01b6, IOException -> 0x01b1, JSONException -> 0x01ac, Exception -> 0x01a7, all -> 0x01a2 }
            r3.add(r4)     // Catch:{ ProtocolException -> 0x01b6, IOException -> 0x01b1, JSONException -> 0x01ac, Exception -> 0x01a7, all -> 0x01a2 }
            int r0 = r0 + 1
            r4 = r16
            r5 = r18
            r6 = r19
            r8 = r20
            goto L_0x010c
        L_0x0148:
            r0 = move-exception
            r20 = r8
            goto L_0x0249
        L_0x014d:
            r0 = move-exception
            r20 = r8
            goto L_0x020a
        L_0x0152:
            r0 = move-exception
            r20 = r8
            goto L_0x0218
        L_0x0157:
            r0 = move-exception
            r20 = r8
            goto L_0x022b
        L_0x015c:
            r0 = move-exception
            r20 = r8
            goto L_0x0239
        L_0x0161:
            r18 = r5
            r19 = r6
            r20 = r8
            goto L_0x019f
        L_0x0168:
            r0 = move-exception
            r18 = r5
            r20 = r8
            goto L_0x0249
        L_0x016f:
            r0 = move-exception
            r18 = r5
            r20 = r8
            goto L_0x020a
        L_0x0176:
            r0 = move-exception
            r18 = r5
            r20 = r8
            goto L_0x0218
        L_0x017d:
            r0 = move-exception
            r18 = r5
            r20 = r8
            goto L_0x022b
        L_0x0184:
            r0 = move-exception
            r18 = r5
            r20 = r8
            goto L_0x0239
        L_0x018b:
            r17 = r0
            r16 = r4
            r18 = r5
            r20 = r8
            java.lang.String r0 = "request_list_failed"
            r1.trackEvent(r0)     // Catch:{ ProtocolException -> 0x01b6, IOException -> 0x01b1, JSONException -> 0x01ac, Exception -> 0x01a7, all -> 0x01a2 }
            java.lang.String r0 = "request result is failed"
            android.util.Slog.e(r6, r0)     // Catch:{ ProtocolException -> 0x01b6, IOException -> 0x01b1, JSONException -> 0x01ac, Exception -> 0x01a7, all -> 0x01a2 }
        L_0x019f:
            r8 = r20
            goto L_0x01f6
        L_0x01a2:
            r0 = move-exception
            r8 = r20
            goto L_0x0249
        L_0x01a7:
            r0 = move-exception
            r8 = r20
            goto L_0x020a
        L_0x01ac:
            r0 = move-exception
            r8 = r20
            goto L_0x0218
        L_0x01b1:
            r0 = move-exception
            r8 = r20
            goto L_0x022b
        L_0x01b6:
            r0 = move-exception
            r8 = r20
            goto L_0x0239
        L_0x01bb:
            r0 = move-exception
            r16 = r4
            r18 = r5
            r20 = r8
            goto L_0x0249
        L_0x01c4:
            r0 = move-exception
            r16 = r4
            r18 = r5
            r20 = r8
            goto L_0x020a
        L_0x01cc:
            r0 = move-exception
            r16 = r4
            r18 = r5
            r20 = r8
            goto L_0x0218
        L_0x01d4:
            r0 = move-exception
            r16 = r4
            r18 = r5
            r20 = r8
            goto L_0x022b
        L_0x01dc:
            r0 = move-exception
            r16 = r4
            r18 = r5
            r20 = r8
            goto L_0x0239
        L_0x01e4:
            r17 = r0
            r16 = r4
            r18 = r5
            java.lang.String r0 = "request_connect_failed"
            r1.trackEvent(r0)     // Catch:{ ProtocolException -> 0x01fd, IOException -> 0x01fb, JSONException -> 0x01f9, Exception -> 0x01f7 }
            java.lang.String r0 = "server can not connected"
            android.util.Slog.i(r6, r0)     // Catch:{ ProtocolException -> 0x01fd, IOException -> 0x01fb, JSONException -> 0x01f9, Exception -> 0x01f7 }
        L_0x01f6:
            goto L_0x0241
        L_0x01f7:
            r0 = move-exception
            goto L_0x020a
        L_0x01f9:
            r0 = move-exception
            goto L_0x0218
        L_0x01fb:
            r0 = move-exception
            goto L_0x022b
        L_0x01fd:
            r0 = move-exception
            goto L_0x0239
        L_0x01ff:
            r0 = move-exception
            r16 = r4
            r18 = r5
            goto L_0x0249
        L_0x0205:
            r0 = move-exception
            r16 = r4
            r18 = r5
        L_0x020a:
            r1.trackEvent(r2)     // Catch:{ all -> 0x0224 }
            r0.printStackTrace()     // Catch:{ all -> 0x0224 }
            if (r7 == 0) goto L_0x0244
            goto L_0x0241
        L_0x0213:
            r0 = move-exception
            r16 = r4
            r18 = r5
        L_0x0218:
            java.lang.String r2 = "json_exception"
            r1.trackEvent(r2)     // Catch:{ all -> 0x0224 }
            r0.printStackTrace()     // Catch:{ all -> 0x0224 }
            if (r7 == 0) goto L_0x0244
            goto L_0x0241
        L_0x0224:
            r0 = move-exception
            goto L_0x0249
        L_0x0226:
            r0 = move-exception
            r16 = r4
            r18 = r5
        L_0x022b:
            r1.trackEvent(r2)     // Catch:{ all -> 0x0224 }
            r0.printStackTrace()     // Catch:{ all -> 0x0224 }
            if (r7 == 0) goto L_0x0244
            goto L_0x0241
        L_0x0234:
            r0 = move-exception
            r16 = r4
            r18 = r5
        L_0x0239:
            r1.trackEvent(r2)     // Catch:{ all -> 0x0224 }
            r0.printStackTrace()     // Catch:{ all -> 0x0224 }
            if (r7 == 0) goto L_0x0244
        L_0x0241:
            r7.disconnect()
        L_0x0244:
            r1.closeBufferedReader(r8)
            return r3
        L_0x0249:
            if (r7 == 0) goto L_0x024e
            r7.disconnect()
        L_0x024e:
            r1.closeBufferedReader(r8)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.CloudControlPreinstallService.getUninstallApps():java.util.List");
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

    /* access modifiers changed from: private */
    public void uninstallAppsUpdateList(List<UninstallApp> uninstallApps) {
        if (uninstallApps == null || uninstallApps.isEmpty()) {
            trackEvent("uninstall_list_empty");
            return;
        }
        String currentCustVariant = SystemProperties.get("ro.miui.cust_variant");
        PackageManager pm = getContext().getPackageManager();
        if (pm != null) {
            for (UninstallApp app : uninstallApps) {
                if (TextUtils.equals(currentCustVariant, app.custVariant)) {
                    trackEvent("remove_from_list_begin", app);
                    PreinstallApp.removeFromPreinstallList(app.packageName);
                    try {
                        pm.setApplicationEnabledSetting(app.packageName, 2, 0);
                    } catch (Exception e) {
                        Slog.i(TAG, "disable Package " + app.packageName + " failed:" + e.toString());
                    }
                }
            }
            for (UninstallApp app2 : uninstallApps) {
                if (TextUtils.equals(currentCustVariant, app2.custVariant)) {
                    if (!exists(pm, app2.packageName)) {
                        Slog.i(TAG, "Package " + app2.packageName + " not exist");
                        trackEvent("package_not_exist", app2);
                    } else {
                        try {
                            trackEvent("begin_uninstall", app2);
                            pm.deletePackage(app2.packageName, new PackageDeleteObserver(app2), 2);
                        } catch (Exception e2) {
                            Slog.e(TAG, "uninstall Package " + app2.packageName + " failed:" + e2.toString());
                            trackEvent(UNINSTALL_FAILED, app2);
                        }
                    }
                }
            }
        }
    }

    public static boolean exists(PackageManager pm, String packageName) {
        try {
            return pm.getApplicationInfo(packageName, 128) != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public class PackageDeleteObserver extends IPackageDeleteObserver.Stub {
        UninstallApp mApp;

        public PackageDeleteObserver(UninstallApp app) {
            this.mApp = app;
        }

        public void packageDeleted(String packageName, int returnCode) {
            if (returnCode >= 0) {
                CloudControlPreinstallService.this.trackEvent("uninstall_success", this.mApp);
                Slog.i(CloudControlPreinstallService.TAG, "Package " + packageName + " was uninstalled.");
                return;
            }
            CloudControlPreinstallService.this.trackEvent(CloudControlPreinstallService.UNINSTALL_FAILED, this.mApp);
            Slog.e(CloudControlPreinstallService.TAG, "Package uninstall failed " + packageName + ", returnCode " + returnCode);
        }
    }

    public static boolean isProvisioned(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "device_provisioned", 0) != 0;
    }
}
