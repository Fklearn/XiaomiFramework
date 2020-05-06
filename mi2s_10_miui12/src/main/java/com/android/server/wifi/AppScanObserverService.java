package com.android.server.wifi;

import android.app.AppGlobals;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManagerCompat;
import android.content.pm.UserInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import com.android.server.am.ExtraActivityManagerService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class AppScanObserverService {
    private static final boolean DEBUG = false;
    private static final int MSG_IGNORE_OBSERVED_AP = 1008;
    private static final int MSG_PACKAGE_ADDED = 1003;
    private static final int MSG_PACKAGE_REMOVED = 1004;
    private static final int MSG_SCAN_RESULT_AVAILABLE = 1007;
    private static final int MSG_SYSTEM_READY = 1002;
    private static final int MSG_USER_ADDED = 1005;
    private static final int MSG_USER_REMOVED = 1006;
    private static final int OBSERVE_AGING_TIME_MS = 1800000;
    private static final String TAG = "AppScanObserverService";
    private static final List<String> sAppObserverWhitelist = new ArrayList();
    private static AppScanObserverService sSelf;
    private Map<Integer, Map<Integer, AppScanObserver>> mAppScanObservers;
    private Context mContext;
    /* access modifiers changed from: private */
    public LocalHandler mLocalHandler;
    private BroadcastReceiver mPackageReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int userId = intent.getIntExtra("android.intent.extra.user_handle", -10000);
            int appUid = intent.getIntExtra("android.intent.extra.UID", -1);
            Uri appData = intent.getData();
            String packageName = appData != null ? appData.getSchemeSpecificPart() : null;
            boolean removedForAllUsers = intent.getBooleanExtra("android.intent.extra.REMOVED_FOR_ALL_USERS", false);
            AppScanObserverService.logd("Receive action: " + action + " | " + userId + " | " + appUid + " | " + packageName + " | " + removedForAllUsers);
            Message message = null;
            if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
                message = AppScanObserverService.this.mLocalHandler.obtainMessage(1003, appUid, userId, packageName);
            } else if ("android.intent.action.PACKAGE_REMOVED".equals(action)) {
                message = AppScanObserverService.this.mLocalHandler.obtainMessage(1004, appUid, userId, Boolean.valueOf(removedForAllUsers));
            } else if ("android.intent.action.USER_ADDED".equals(action)) {
                message = AppScanObserverService.this.mLocalHandler.obtainMessage(1005, userId, 0);
            } else if ("android.intent.action.USER_REMOVED".equals(action)) {
                message = AppScanObserverService.this.mLocalHandler.obtainMessage(1006, userId, 0);
            }
            if (message != null) {
                AppScanObserverService.this.mLocalHandler.sendMessage(message);
            }
        }
    };

    static {
        sAppObserverWhitelist.add("com.xiaomi.smarthome");
    }

    private AppScanObserverService(Context context) {
        this.mContext = context;
        this.mAppScanObservers = new HashMap();
    }

    protected static AppScanObserverService make(Context context) {
        if (sSelf == null) {
            sSelf = new AppScanObserverService(context);
        }
        return sSelf;
    }

    public static AppScanObserverService get() {
        AppScanObserverService appScanObserverService = sSelf;
        if (appScanObserverService != null) {
            return appScanObserverService;
        }
        return null;
    }

    /* access modifiers changed from: private */
    public static void logd(String message) {
    }

    /* access modifiers changed from: private */
    public static void logi(String message) {
        Log.i(TAG, message);
    }

    private static void loge(String message, Throwable e) {
        Log.e(TAG, message, e);
    }

    /* access modifiers changed from: private */
    public static void loge(String message) {
        Log.e(TAG, message);
    }

    /* access modifiers changed from: private */
    public static String maskBssid(String bssid) {
        if (TextUtils.isEmpty(bssid) || bssid.length() != 17) {
            return bssid;
        }
        return bssid.substring(0, 3) + "*" + bssid.substring(14, 17);
    }

    /* access modifiers changed from: protected */
    public void systemReady(HandlerThread thread) {
        this.mLocalHandler = new LocalHandler(thread.getLooper());
        this.mLocalHandler.sendEmptyMessage(1002);
        registerPackageReceiver();
    }

    /* access modifiers changed from: protected */
    public void onNewScanResultAvailable() {
        this.mLocalHandler.sendEmptyMessage(MSG_SCAN_RESULT_AVAILABLE);
    }

    private void registerPackageReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.USER_ADDED");
        filter.addAction("android.intent.action.USER_REMOVED");
        this.mContext.registerReceiverAsUser(this.mPackageReceiver, UserHandle.ALL, filter, (String) null, (Handler) null);
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction("android.intent.action.PACKAGE_ADDED");
        filter2.addAction("android.intent.action.PACKAGE_REMOVED");
        filter2.addDataScheme("package");
        this.mContext.registerReceiverAsUser(this.mPackageReceiver, UserHandle.ALL, filter2, (String) null, (Handler) null);
    }

    private boolean isAppObserveRestricted(int appUid, int userId, String packageName) {
        logd("isAppObserveRestricted: " + appUid + " | " + userId + " | " + packageName);
        if (appUid < 0 || userId < 0 || TextUtils.isEmpty(packageName)) {
            loge("Restricted for invalid app: " + appUid + " | " + userId + " | " + packageName);
            return true;
        } else if (sAppObserverWhitelist.contains(packageName)) {
            return false;
        } else {
            loge("App is not in observer whitelist");
            return true;
        }
    }

    private Map<Integer, AppScanObserver> parseAppScanObserver(int userId) {
        AppScanObserver observer;
        logd("parseAppScanObserver: " + userId);
        if (userId < 0) {
            loge("Try to parse observer for invalid user: " + userId);
            return null;
        }
        Map<Integer, AppScanObserver> observers = new HashMap<>();
        List<PackageInfo> packages = PackageManagerCompat.getInstalledPackagesAsUser(this.mContext.getPackageManager(), 128, userId);
        if (packages != null) {
            for (PackageInfo pi : packages) {
                int uid = pi.applicationInfo != null ? pi.applicationInfo.uid : -1;
                if (uid >= 0 && (observer = AppScanObserver.fromPackage(pi)) != null && !isAppObserveRestricted(uid, userId, pi.packageName)) {
                    observers.put(Integer.valueOf(uid), observer);
                    logd("parseAppScanObserver result: " + uid + " | " + observer);
                }
            }
        }
        return observers;
    }

    /* access modifiers changed from: private */
    public void handleSystemReady() {
        logd("handleSystemReady");
        List<UserInfo> users = UserManager.get(this.mContext).getUsers(true);
        if (users != null) {
            for (UserInfo user : users) {
                this.mAppScanObservers.put(Integer.valueOf(user.id), parseAppScanObserver(user.id));
            }
        }
    }

    /* access modifiers changed from: private */
    public void handlePackageAdded(int appUid, int userId, String packageName) {
        logd("handlePackageAdded: " + packageName + " | " + appUid + " | " + userId);
        if (TextUtils.isEmpty(packageName) || appUid < 0 || userId < 0 || this.mAppScanObservers.get(Integer.valueOf(userId)) == null) {
            loge("Try to add a invalid package: " + packageName + " | " + appUid + " | " + userId);
            return;
        }
        try {
            PackageInfo packageInfo = AppGlobals.getPackageManager().getPackageInfo(packageName, 128, userId);
            if (packageInfo != null) {
                int uid = packageInfo.applicationInfo != null ? packageInfo.applicationInfo.uid : -1;
                AppScanObserver observer = null;
                if (uid == appUid) {
                    observer = AppScanObserver.fromPackage(packageInfo);
                }
                if (observer != null && !isAppObserveRestricted(appUid, userId, packageName)) {
                    this.mAppScanObservers.get(Integer.valueOf(userId)).put(Integer.valueOf(uid), observer);
                }
            }
        } catch (RemoteException e) {
            loge("Exception while handle package added", e);
        }
    }

    /* access modifiers changed from: private */
    public void handlePackageRemoved(int appUid, int userId, boolean forAllUsers) {
        logd("handlePackageRemoved: " + appUid + " | " + userId + " | " + forAllUsers);
        if (appUid < 0 || userId < 0 || (!forAllUsers && this.mAppScanObservers.get(Integer.valueOf(userId)) == null)) {
            loge("Try to remove an invalid package: " + appUid + " | " + userId + " | " + forAllUsers);
        } else if (forAllUsers) {
            for (Map<Integer, AppScanObserver> observers : this.mAppScanObservers.values()) {
                observers.remove(Integer.valueOf(appUid));
            }
        } else if (this.mAppScanObservers.get(Integer.valueOf(userId)) != null) {
            this.mAppScanObservers.get(Integer.valueOf(userId)).remove(Integer.valueOf(appUid));
        }
    }

    /* access modifiers changed from: private */
    public void handleUserAdded(int userId) {
        logd("handleUserAdded: " + userId);
        if (userId < 0) {
            loge("Try to add an invalid user: " + userId);
            return;
        }
        this.mAppScanObservers.put(Integer.valueOf(userId), parseAppScanObserver(userId));
    }

    /* access modifiers changed from: private */
    public void handleUserRemoved(int userId) {
        logd("handleUserRemoved: " + userId);
        if (userId < 0) {
            loge("Try to remove an invalid user: " + userId);
            return;
        }
        this.mAppScanObservers.remove(Integer.valueOf(userId));
    }

    /* access modifiers changed from: private */
    public void handleScanResultAvailable() {
        logd("handleScanResultAvailable");
        List<ScanResult> scanResults = ((WifiManager) this.mContext.getSystemService("wifi")).getScanResults();
        if (scanResults != null && !scanResults.isEmpty()) {
            int currentUserId = ExtraActivityManagerService.getCurrentUserId();
            if (currentUserId < 0) {
                loge("Result available, invalid current user: " + currentUserId);
                return;
            }
            Map<Integer, AppScanObserver> observers = this.mAppScanObservers.get(Integer.valueOf(currentUserId));
            if (observers == null || observers.isEmpty()) {
                loge("Result available, null observers for user: " + currentUserId);
                return;
            }
            logi("Start to match app observer");
            for (AppScanObserver observer : observers.values()) {
                if (observer.matchScanResults(scanResults)) {
                    observer.sendIntent(this.mContext, UserHandle.CURRENT);
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleIgnoreObservedAps(int appUid, int userId, List<String> bssids, boolean ignore) {
        StringBuilder sb = new StringBuilder();
        sb.append("Ignore observed: ");
        sb.append(appUid);
        sb.append(" | ");
        sb.append(userId);
        sb.append(" | ");
        Object obj = "NULL";
        sb.append(bssids != null ? Integer.valueOf(bssids.size()) : obj);
        logi(sb.toString());
        if (appUid < 0 || userId < 0 || bssids == null || this.mAppScanObservers.get(Integer.valueOf(userId)) == null) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Ignore observed aps failed: ");
            sb2.append(appUid);
            sb2.append(" | ");
            sb2.append(userId);
            sb2.append(" | ");
            if (bssids != null) {
                obj = Integer.valueOf(bssids.size());
            }
            sb2.append(obj);
            loge(sb2.toString());
            return;
        }
        AppScanObserver observer = (AppScanObserver) this.mAppScanObservers.get(Integer.valueOf(userId)).get(Integer.valueOf(appUid));
        if (observer != null) {
            observer.addOrRemoveIgnoredAps(bssids, ignore);
        }
    }

    private class LocalHandler extends Handler {
        LocalHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1002:
                    AppScanObserverService.this.handleSystemReady();
                    return;
                case 1003:
                    AppScanObserverService.this.handlePackageAdded(msg.arg1, msg.arg2, (String) msg.obj);
                    return;
                case 1004:
                    AppScanObserverService.this.handlePackageRemoved(msg.arg1, msg.arg2, ((Boolean) msg.obj).booleanValue());
                    return;
                case 1005:
                    AppScanObserverService.this.handleUserAdded(msg.arg1);
                    return;
                case 1006:
                    AppScanObserverService.this.handleUserRemoved(msg.arg1);
                    return;
                case AppScanObserverService.MSG_SCAN_RESULT_AVAILABLE /*1007*/:
                    AppScanObserverService.this.handleScanResultAvailable();
                    return;
                case AppScanObserverService.MSG_IGNORE_OBSERVED_AP /*1008*/:
                    boolean z = true;
                    if (msg.arg1 != 1) {
                        z = false;
                    }
                    boolean ignore = z;
                    int appUid = msg.arg2;
                    AppScanObserverService.this.handleIgnoreObservedAps(appUid, UserHandle.getUserId(appUid), (List) msg.obj, ignore);
                    return;
                default:
                    return;
            }
        }
    }

    /* access modifiers changed from: protected */
    public boolean handleClientMessage(Message msg) {
        LocalHandler localHandler;
        if (msg.what != 155559) {
            return false;
        }
        if (msg.obj == null || (localHandler = this.mLocalHandler) == null) {
            return true;
        }
        localHandler.sendMessage(localHandler.obtainMessage(MSG_IGNORE_OBSERVED_AP, msg.arg1, msg.sendingUid, ((Bundle) msg.obj).getStringArrayList("bssid")));
        return true;
    }

    private static class AppScanObserver {
        static final int MAX_MATCHED = 64;
        static final String META_DATA_INTENT = "wifi_intent";
        static final String META_DATA_INTENT_ACTION = "wifi_intent_action";
        static final String META_DATA_INTENT_ON = "on";
        static final String META_DATA_INTENT_TYPE = "wifi_intent_type";
        static final String META_DATA_INTENT_TYPE_ACTIVITY = "activity";
        static final String META_DATA_INTENT_TYPE_BROADCAST = "broadcast";
        static final String META_DATA_INTENT_TYPE_SERVICE = "service";
        static final String META_DATA_REGULAR_BSSID = "wifi_regular_bssid";
        static final String META_DATA_REGULAR_IE = "wifi_regular_ie";
        static final String META_DATA_REGULAR_SSID = "wifi_regular_ssid";
        String bssidRegular;
        String ieRegular;
        Set<String> ignoredAps = new HashSet();
        String intentAction;
        String intentType;
        Map<String, Long> matchedAps = new HashMap();
        String packageName;
        String ssidRegular;
        int uid;

        AppScanObserver() {
        }

        public String toString() {
            return "uid: " + this.uid + ", intentType: " + this.intentType + ", intentAction: " + this.intentAction + ", ssidRegular: " + this.ssidRegular + ", bssidRegular: " + this.bssidRegular + ", ieRegular: " + this.ieRegular;
        }

        static AppScanObserver fromPackage(PackageInfo packageInfo) {
            Bundle metaData;
            AppScanObserver observer = null;
            if (!(packageInfo == null || packageInfo.applicationInfo == null || packageInfo.applicationInfo.uid <= 0 || (metaData = packageInfo.applicationInfo.metaData) == null || !TextUtils.equals(META_DATA_INTENT_ON, metaData.getString(META_DATA_INTENT)))) {
                observer = new AppScanObserver();
                observer.uid = packageInfo.applicationInfo.uid;
                observer.packageName = packageInfo.packageName;
                observer.intentType = metaData.getString(META_DATA_INTENT_TYPE);
                observer.intentAction = metaData.getString(META_DATA_INTENT_ACTION);
                observer.ssidRegular = metaData.getString(META_DATA_REGULAR_SSID);
                observer.bssidRegular = metaData.getString(META_DATA_REGULAR_BSSID);
                observer.ieRegular = metaData.getString(META_DATA_REGULAR_IE);
                if (TextUtils.isEmpty(observer.intentType) || TextUtils.isEmpty(observer.intentAction)) {
                    AppScanObserverService.logi("Parse observer failed for empty intent type or intent action");
                    return null;
                } else if (TextUtils.isEmpty(observer.ssidRegular) && TextUtils.isEmpty(observer.bssidRegular) && TextUtils.isEmpty(observer.ieRegular)) {
                    AppScanObserverService.logi("Parse observer failed for empty regular");
                    return null;
                }
            }
            return observer;
        }

        /* access modifiers changed from: package-private */
        public void addOrRemoveIgnoredAps(List<String> bssids, boolean ignore) {
            if (bssids == null) {
                AppScanObserverService.loge("Try to ignore null aps");
            } else if (ignore) {
                this.ignoredAps.addAll(bssids);
            } else {
                this.ignoredAps.removeAll(bssids);
            }
        }

        /* access modifiers changed from: package-private */
        public boolean isApRestrictedForMatch(String bssid) {
            long currentTime = System.currentTimeMillis();
            long lastMatchedTime = this.matchedAps.get(bssid) != null ? this.matchedAps.get(bssid).longValue() : -1;
            if (lastMatchedTime > 0) {
                if (currentTime - lastMatchedTime < 1800000) {
                    AppScanObserverService.logi("Restricted for last matched: " + AppScanObserverService.maskBssid(bssid) + " | " + ((1800000 - (currentTime - lastMatchedTime)) / 1000));
                    return false;
                }
                this.matchedAps.remove(bssid);
            }
            if (!this.ignoredAps.contains(bssid)) {
                return true;
            }
            AppScanObserverService.logi("Restricted for ignored: " + AppScanObserverService.maskBssid(bssid));
            return false;
        }

        /* access modifiers changed from: package-private */
        public boolean matchScanResults(List<ScanResult> scanResults) {
            if (scanResults == null) {
                AppScanObserverService.loge("Try to match a null scan result");
                return false;
            }
            if (this.matchedAps.size() >= 64) {
                Set<String> expiredAps = new HashSet<>();
                long currentTime = System.currentTimeMillis();
                for (String bssid : this.matchedAps.keySet()) {
                    if (currentTime - (this.matchedAps.get(bssid) != null ? this.matchedAps.get(bssid).longValue() : -1) > 1800000) {
                        expiredAps.add(bssid);
                    }
                }
                for (String bssid2 : expiredAps) {
                    this.matchedAps.remove(bssid2);
                }
            }
            boolean matched = false;
            for (ScanResult scanResult : scanResults) {
                if (matchScanResult(scanResult)) {
                    this.matchedAps.put(scanResult.BSSID, Long.valueOf(System.currentTimeMillis()));
                    AppScanObserverService.logi("Match observer success: " + this.uid + " | " + AppScanObserverService.maskBssid(scanResult.BSSID));
                    matched = true;
                }
            }
            return matched;
        }

        /* access modifiers changed from: package-private */
        public boolean matchScanResult(ScanResult scanResult) {
            if (scanResult == null) {
                AppScanObserverService.loge("Try to match a null scan result");
                return false;
            } else if (!isApRestrictedForMatch(scanResult.BSSID)) {
                return false;
            } else {
                if (!TextUtils.isEmpty(this.ssidRegular) && (TextUtils.isEmpty(scanResult.SSID) || !Pattern.matches(this.ssidRegular, scanResult.SSID))) {
                    AppScanObserverService.logd("Match failed for ssid: " + this.ssidRegular + " | " + scanResult.BSSID);
                    return false;
                } else if (!TextUtils.isEmpty(this.bssidRegular) && (TextUtils.isEmpty(scanResult.BSSID) || !Pattern.matches(this.bssidRegular, scanResult.BSSID))) {
                    AppScanObserverService.logd("Match failed for bssid: " + this.bssidRegular + " | " + scanResult.BSSID);
                    return false;
                } else if (TextUtils.isEmpty(this.ieRegular) || scanResult.informationElements == null) {
                    return true;
                } else {
                    for (ScanResult.InformationElement ie : scanResult.informationElements) {
                        String ieStr = Utils.byteToHex((byte) ie.id) + Utils.bytesToHex(ie.bytes);
                        if (!TextUtils.isEmpty(ieStr) && Pattern.matches(this.ieRegular, ieStr)) {
                            return true;
                        }
                    }
                    AppScanObserverService.logd("Match failed for ie: " + this.ieRegular);
                    return false;
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void sendIntent(Context context, UserHandle userHandle) {
            AppScanObserverService.logi("Send intent to app observer: " + this.intentAction + " | " + userHandle.getIdentifier());
            if (!TextUtils.isEmpty(this.intentAction)) {
                Intent intent = new Intent();
                intent.setPackage(this.packageName);
                intent.setAction(this.intentAction);
                if (TextUtils.equals(META_DATA_INTENT_TYPE_ACTIVITY, this.intentType)) {
                    intent.addFlags(268435456);
                    context.startActivityAsUser(intent, userHandle);
                } else if (TextUtils.equals(META_DATA_INTENT_TYPE_SERVICE, this.intentType)) {
                    AppScanObserverServiceCompat.startServiceAsUser(context, intent, userHandle);
                } else if (TextUtils.equals(META_DATA_INTENT_TYPE_BROADCAST, this.intentType)) {
                    context.sendBroadcastAsUser(intent, userHandle);
                }
            }
        }
    }
}
