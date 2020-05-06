package com.android.server.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerCompat;
import android.content.pm.UserInfo;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.os.Build;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Log;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MiuiNetworkPolicyAppBuckets {
    private static final String CONNECTION_EX = "enableConnectionExtension";
    private static final int CON_DISABLED = 0;
    private static final int CON_ENABLED = 1;
    private static final boolean DEBUG = true;
    private static final String LATENCY_ACTION_CHANGE_LEVEL = "com.android.phone.intent.action.CHANGE_LEVEL";
    private static final String[] LOCAL_GAME_APP_LIST = {"com.tencent.tmgp.sgame", "com.tencent.tmgp.pubgmhd"};
    private static final String[] LOCAL_HONGBAO_APP_LIST = {"com.tencent.mm"};
    private static final String[] LOCAL_TPUT_TOOL_APP_LIST = {"org.zwanoo.android.speedtest"};
    private static final String NOTIFACATION_RECEIVER_PACKAGE = "com.android.phone";
    private static final String OPTIMIZATION_ENABLED = "optimizationEnabled";
    private static final String TAG = "MiuiNetworkPolicyAppBuckets";
    private static final String TPUT_TEST_APP_OPTIMIZATION = "com.android.phone.intent.action.TPUT_OPTIMIZATION";
    private Set<Integer> mAppUid;
    private Set<String> mAppsPN;
    private ConnectivityManager mCm;
    private final Context mContext;
    private boolean mIsHongbaoAppOn = false;
    /* access modifiers changed from: private */
    public boolean mIsMobileNwOn = false;
    private boolean mIsTputTestAppOn = false;
    private boolean mLastHongbaoApp = false;
    /* access modifiers changed from: private */
    public boolean mLastMobileNw = false;
    private boolean mLastTputTestApp = false;
    final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                MiuiNetworkPolicyAppBuckets.this.log("BroadcastReceiver action is null!");
            } else if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action) && intent.getIntExtra("networkType", 0) == 0) {
                String iface = MiuiNetworkPolicyAppBuckets.this.getMobileLinkIface();
                MiuiNetworkPolicyAppBuckets miuiNetworkPolicyAppBuckets = MiuiNetworkPolicyAppBuckets.this;
                miuiNetworkPolicyAppBuckets.log("BroadcastReceiver iface=" + iface);
                boolean unused = MiuiNetworkPolicyAppBuckets.this.mIsMobileNwOn = TextUtils.isEmpty(iface) ^ true;
                if (MiuiNetworkPolicyAppBuckets.this.mLastMobileNw != MiuiNetworkPolicyAppBuckets.this.mIsMobileNwOn) {
                    MiuiNetworkPolicyAppBuckets.this.updateHongbaoModeStatus();
                    MiuiNetworkPolicyAppBuckets.this.updateTputTestAppStatus();
                    MiuiNetworkPolicyAppBuckets miuiNetworkPolicyAppBuckets2 = MiuiNetworkPolicyAppBuckets.this;
                    boolean unused2 = miuiNetworkPolicyAppBuckets2.mLastMobileNw = miuiNetworkPolicyAppBuckets2.mIsMobileNwOn;
                }
            }
        }
    };
    private ConcurrentHashMap<String, Integer> mUidMap = new ConcurrentHashMap<>();

    public MiuiNetworkPolicyAppBuckets(Context context) {
        this.mContext = context;
        this.mAppUid = new HashSet();
    }

    public void systemReady() {
        updateAppList();
        initReceiver();
    }

    private void initReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        this.mContext.registerReceiver(this.mReceiver, filter);
    }

    /* access modifiers changed from: private */
    public String getMobileLinkIface() {
        if (this.mCm == null) {
            this.mCm = (ConnectivityManager) this.mContext.getSystemService("connectivity");
        }
        LinkProperties prop = this.mCm.getLinkProperties(0);
        if (prop == null || TextUtils.isEmpty(prop.getInterfaceName())) {
            return "";
        }
        return prop.getInterfaceName();
    }

    private static boolean isUidValidForQos(int uid) {
        return UserHandle.isApp(uid);
    }

    private Set<String> getAllAppsPN() {
        Set<String> appList = new HashSet<>();
        if (isHongbaoModeAllowed()) {
            int i = 0;
            while (true) {
                String[] strArr = LOCAL_HONGBAO_APP_LIST;
                if (i >= strArr.length) {
                    break;
                }
                appList.add(strArr[i]);
                i++;
            }
        }
        if (isTputOptimizationAllowed() != 0) {
            int i2 = 0;
            while (true) {
                String[] strArr2 = LOCAL_TPUT_TOOL_APP_LIST;
                if (i2 >= strArr2.length) {
                    break;
                }
                appList.add(strArr2[i2]);
                i2++;
            }
        }
        int i3 = 0;
        while (true) {
            String[] strArr3 = LOCAL_GAME_APP_LIST;
            if (i3 >= strArr3.length) {
                return appList;
            }
            appList.add(strArr3[i3]);
            i3++;
        }
    }

    private void updateAppList() {
        PackageManager pm = this.mContext.getPackageManager();
        List<UserInfo> users = ((UserManager) this.mContext.getSystemService("user")).getUsers();
        this.mAppsPN = getAllAppsPN();
        uidRemoveAll();
        if (!this.mAppsPN.isEmpty()) {
            for (UserInfo user : users) {
                for (PackageInfo app : PackageManagerCompat.getInstalledPackagesAsUser(pm, 0, user.id)) {
                    if (!(app.packageName == null || app.applicationInfo == null || !this.mAppsPN.contains(app.packageName))) {
                        addUidToMap(app.packageName, UserHandle.getUid(user.id, app.applicationInfo.uid));
                    }
                }
            }
            updateUidFromWholeAppMap();
        }
    }

    private void addUidToMap(String packageName, int uid) {
        if (!this.mUidMap.containsKey(packageName)) {
            this.mUidMap.put(packageName, Integer.valueOf(uid));
        }
    }

    private int getUidFromMap(String packageName) {
        if (this.mUidMap.get(packageName) == null) {
            return -1;
        }
        return this.mUidMap.get(packageName).intValue();
    }

    private void removeUidFromMap(String packageName) {
        this.mUidMap.remove(packageName);
    }

    private void uidRemoveAll() {
        this.mUidMap.clear();
    }

    public void updateAppPN(String packageName, int uid, boolean installed) {
        log("updateAppPN packageName=" + packageName + ",uid=" + uid + ",installed=" + installed);
        Set<String> set = this.mAppsPN;
        if (set != null && set.contains(packageName)) {
            if (installed) {
                addUidToMap(packageName, uid);
            } else {
                removeUidFromMap(packageName);
            }
            updateUidFromWholeAppMap();
        }
    }

    public void updateAppBucketsForUidStateChange(int uid, int oldUidState, int newUidState) {
        if (isUidValidForQos(uid) && isAppBucketsEnabledForUid(uid, oldUidState) != isAppBucketsEnabledForUid(uid, newUidState)) {
            appBucketsForUidStateChanged(uid, newUidState);
        }
    }

    private boolean isAppBucketsEnabledForUid(int uid, int state) {
        return state == 2 && this.mAppUid.contains(Integer.valueOf(uid));
    }

    private void appBucketsForUidStateChanged(int uid, int state) {
        log("appBucketsForUidStateChanged uid=" + uid + ",state=" + state);
        if (isAppBucketsEnabledForUid(uid, state)) {
            processHongbaoAppIfNeed(uid, true);
            processTputTestAppIfNeed(uid, true);
            return;
        }
        processHongbaoAppIfNeed(uid, false);
        processTputTestAppIfNeed(uid, false);
    }

    /* access modifiers changed from: private */
    public synchronized void updateHongbaoModeStatus() {
        boolean isOldStatusOn = true;
        boolean isNewStatusOn = this.mIsMobileNwOn && this.mIsHongbaoAppOn;
        if (!this.mLastMobileNw || !this.mLastHongbaoApp) {
            isOldStatusOn = false;
        }
        log("updateHongbaoModeStatus isNewStatusOn=" + isNewStatusOn + ",isOldStatusOn=" + isOldStatusOn);
        if (isNewStatusOn != isOldStatusOn) {
            enableHongbaoMode(isNewStatusOn);
        }
    }

    private boolean hasUidFromHongbaoMap(int uid) {
        boolean rst = false;
        int i = 0;
        while (true) {
            String[] strArr = LOCAL_HONGBAO_APP_LIST;
            if (i >= strArr.length) {
                return rst;
            }
            if (getUidFromMap(strArr[i]) == uid) {
                rst = true;
            }
            i++;
        }
    }

    private boolean hasUidFromTputTestMap(int uid) {
        int i = 0;
        while (true) {
            String[] strArr = LOCAL_TPUT_TOOL_APP_LIST;
            if (i >= strArr.length) {
                return false;
            }
            if (getUidFromMap(strArr[i]) == uid) {
                return true;
            }
            i++;
        }
    }

    private void updateUidFromWholeAppMap() {
        this.mAppUid.clear();
        if (!this.mAppsPN.isEmpty()) {
            for (String pn : this.mAppsPN) {
                int uid = getUidFromMap(pn);
                if (uid != -1) {
                    this.mAppUid.add(Integer.valueOf(uid));
                }
            }
        }
    }

    private void processHongbaoAppIfNeed(int uid, boolean enabled) {
        if (hasUidFromHongbaoMap(uid)) {
            log("processHongbaoAppIfNeed Hongbao" + enabled);
            this.mIsHongbaoAppOn = enabled;
            updateHongbaoModeStatus();
            this.mLastHongbaoApp = this.mIsHongbaoAppOn;
        }
    }

    private void enableHongbaoMode(boolean enable) {
        log("enableHongbaoMode enable" + enable);
        Intent intent = new Intent();
        intent.setAction(LATENCY_ACTION_CHANGE_LEVEL);
        intent.setPackage(NOTIFACATION_RECEIVER_PACKAGE);
        intent.putExtra(CONNECTION_EX, enable);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);
    }

    private void processTputTestAppIfNeed(int uid, boolean enabled) {
        if (hasUidFromTputTestMap(uid)) {
            log("processTputTestAppIfNeed TputTest=" + enabled);
            this.mIsTputTestAppOn = enabled;
            updateTputTestAppStatus();
            this.mLastTputTestApp = this.mIsTputTestAppOn;
        }
    }

    /* access modifiers changed from: private */
    public synchronized void updateTputTestAppStatus() {
        boolean isOldStatusOn = true;
        boolean isNewStatusOn = this.mIsMobileNwOn && this.mIsTputTestAppOn;
        if (!this.mLastMobileNw || !this.mLastTputTestApp) {
            isOldStatusOn = false;
        }
        log("updateTputTestAppStatus isNewStatusOn=" + isNewStatusOn + ",isOldStatusOn=" + isOldStatusOn);
        if (isNewStatusOn != isOldStatusOn) {
            tputTestAppNotification(isNewStatusOn);
        }
    }

    private void tputTestAppNotification(boolean enable) {
        log("tputTestAppNotification enable=" + enable);
        Intent intent = new Intent();
        intent.setAction(TPUT_TEST_APP_OPTIMIZATION);
        intent.setPackage(NOTIFACATION_RECEIVER_PACKAGE);
        intent.putExtra(OPTIMIZATION_ENABLED, enable);
        this.mContext.sendBroadcastAsUser(intent, UserHandle.CURRENT);
    }

    private static boolean isTputOptimizationAllowed() {
        return "picasso".equals(Build.DEVICE) || "cmi".equals(Build.DEVICE) || "umi".equals(Build.DEVICE);
    }

    private static boolean isHongbaoModeAllowed() {
        return false;
    }

    /* access modifiers changed from: private */
    public void log(String s) {
        Log.d(TAG, s);
    }
}
