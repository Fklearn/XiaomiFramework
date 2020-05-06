package com.android.server.net;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerCompat;
import android.content.pm.UserInfo;
import android.os.Handler;
import android.os.UserHandle;
import android.os.UserManager;
import android.system.OsConstants;
import android.text.TextUtils;
import android.util.Log;
import com.android.server.MiuiNetworkManagementService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import miui.process.IActivityChangeListener;
import miui.process.ProcessManager;

public class MiuiNetworkPolicyQosUtils {
    private static final boolean DEBUG = true;
    private static final int DSCP_DEFAULT_STATE = 0;
    private static final int DSCP_INIT_STATE = 1;
    private static final int DSCP_QOS_STATE = 2;
    private static final int DSCP_TOS_EF = 184;
    private static final String EF_AN_WECHAT = "com.tencent.mm.plugin.voip.ui.VideoActivity";
    private static final String EF_PN_WECHAT = "com.tencent.mm";
    private static final int IP = OsConstants.IPPROTO_IP;
    private static final String[] LOCAL_EF_APP_LIST = {EF_PN_WECHAT, "com.tencent.tmgp.sgame", "com.tencent.tmgp.pubgmhd"};
    private static final String TAG = "MiuiNetworkPolicyQosUtils";
    /* access modifiers changed from: private */
    public static final int UDP = OsConstants.IPPROTO_UDP;
    private IActivityChangeListener.Stub mActivityChangeListener = new IActivityChangeListener.Stub() {
        public void onActivityChanged(ComponentName preName, ComponentName curName) {
            Log.i(MiuiNetworkPolicyQosUtils.TAG, "mActivityChangeListener preName:" + preName + ",curName:" + curName);
            if (preName != null && curName != null) {
                String cur = curName.toString();
                String pre = preName.toString();
                if (!TextUtils.isEmpty(cur) && cur.contains(MiuiNetworkPolicyQosUtils.EF_AN_WECHAT)) {
                    MiuiNetworkPolicyQosUtils miuiNetworkPolicyQosUtils = MiuiNetworkPolicyQosUtils.this;
                    miuiNetworkPolicyQosUtils.setQos(true, miuiNetworkPolicyQosUtils.getUidFromMap(MiuiNetworkPolicyQosUtils.EF_PN_WECHAT), MiuiNetworkPolicyQosUtils.UDP);
                } else if (!TextUtils.isEmpty(pre) && pre.contains(MiuiNetworkPolicyQosUtils.EF_AN_WECHAT)) {
                    MiuiNetworkPolicyQosUtils miuiNetworkPolicyQosUtils2 = MiuiNetworkPolicyQosUtils.this;
                    miuiNetworkPolicyQosUtils2.setQos(false, miuiNetworkPolicyQosUtils2.getUidFromMap(MiuiNetworkPolicyQosUtils.EF_PN_WECHAT), MiuiNetworkPolicyQosUtils.UDP);
                }
            }
        }
    };
    private final Context mContext;
    private int mDscpState;
    private Set<String> mEFAppsPN;
    private final Handler mHandler;
    /* access modifiers changed from: private */
    public MiuiNetworkManagementService mNetMgrService;
    private ConcurrentHashMap<String, Integer> mUidMap = new ConcurrentHashMap<>();

    public MiuiNetworkPolicyQosUtils(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
        setDscpStatus(0);
    }

    public void systemReady(MiuiNetworkManagementService networkMgr) {
        this.mNetMgrService = networkMgr;
        enableQos(false);
        registerActivityChangeListener();
        enableQos(true);
        updateQosUid();
    }

    private void registerActivityChangeListener() {
        if (this.mActivityChangeListener != null) {
            List<String> targetActivities = new ArrayList<>();
            List<String> targetPackages = new ArrayList<>();
            targetPackages.add(EF_PN_WECHAT);
            targetActivities.add(EF_AN_WECHAT);
            ProcessManager.unregisterActivityChanageListener(this.mActivityChangeListener);
            ProcessManager.registerActivityChangeListener(targetPackages, targetActivities, this.mActivityChangeListener);
        }
    }

    private static boolean isUidValidForQos(int uid) {
        return UserHandle.isApp(uid);
    }

    private synchronized void enableQos(final boolean enable) {
        if (isQosReadyForEnable(enable)) {
            if (this.mNetMgrService != null) {
                this.mHandler.post(new Runnable() {
                    public void run() {
                        boolean rst = MiuiNetworkPolicyQosUtils.this.mNetMgrService.enableQos(enable);
                        Log.i(MiuiNetworkPolicyQosUtils.TAG, "enableQos rst=" + rst);
                        if (rst) {
                            MiuiNetworkPolicyQosUtils.this.updateDscpStatus(enable);
                        }
                    }
                });
                return;
            }
        }
        Log.i(TAG, "enableQos return by invalid value!!!");
    }

    /* access modifiers changed from: private */
    public synchronized void setQos(final boolean add, final int uid, final int protocol) {
        if (isUidValidForQos(uid) && this.mNetMgrService != null) {
            if (isQosReadyForSetValue(add)) {
                this.mHandler.post(new Runnable() {
                    public void run() {
                        boolean rst = MiuiNetworkPolicyQosUtils.this.mNetMgrService.setQos(protocol, uid, MiuiNetworkPolicyQosUtils.DSCP_TOS_EF, add);
                        Log.i(MiuiNetworkPolicyQosUtils.TAG, "setQos rst=" + rst + ",add=" + add + ",uid=" + uid);
                        if (rst) {
                            MiuiNetworkPolicyQosUtils.this.updateDscpStatus(add);
                        }
                    }
                });
                return;
            }
        }
        Log.i(TAG, "setQos return by invalid value!!!");
    }

    private void setDscpStatus(int dscpStatus) {
        this.mDscpState = dscpStatus;
    }

    private boolean isQosReadyForSetValue(boolean action) {
        int i = this.mDscpState;
        if (action) {
            if (i != 1) {
                return false;
            }
        } else if (i != 2) {
            return false;
        }
        return true;
    }

    private boolean isQosReadyForEnable(boolean action) {
        int i = this.mDscpState;
        if (action) {
            if (i != 0) {
                return false;
            }
        } else if (i != 1) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void updateDscpStatus(boolean action) {
        if (action) {
            if (this.mDscpState == 0) {
                setDscpStatus(1);
            } else {
                setDscpStatus(2);
            }
        } else if (this.mDscpState == 1) {
            setDscpStatus(0);
        } else {
            setDscpStatus(1);
        }
    }

    private Set<String> getEFApps() {
        Set<String> appList = new HashSet<>();
        int i = 0;
        while (true) {
            String[] strArr = LOCAL_EF_APP_LIST;
            if (i >= strArr.length) {
                return appList;
            }
            appList.add(strArr[i]);
            i++;
        }
    }

    private void updateQosUid() {
        PackageManager pm = this.mContext.getPackageManager();
        List<UserInfo> users = ((UserManager) this.mContext.getSystemService("user")).getUsers();
        this.mEFAppsPN = getEFApps();
        if (!this.mEFAppsPN.isEmpty()) {
            removeAll();
            for (UserInfo user : users) {
                for (PackageInfo app : PackageManagerCompat.getInstalledPackagesAsUser(pm, 0, user.id)) {
                    if (!(app.packageName == null || app.applicationInfo == null || !this.mEFAppsPN.contains(app.packageName))) {
                        addUidToMap(app.packageName, UserHandle.getUid(user.id, app.applicationInfo.uid));
                    }
                }
            }
        }
    }

    private void addUidToMap(String packageName, int uid) {
        if (!this.mUidMap.containsKey(packageName)) {
            this.mUidMap.put(packageName, Integer.valueOf(uid));
        }
    }

    /* access modifiers changed from: private */
    public int getUidFromMap(String packageName) {
        if (this.mUidMap.get(packageName) == null) {
            return -1;
        }
        return this.mUidMap.get(packageName).intValue();
    }

    private boolean hasUidFromMap(int uid) {
        int i = 0;
        while (true) {
            String[] strArr = LOCAL_EF_APP_LIST;
            if (i >= strArr.length) {
                return false;
            }
            if (getUidFromMap(strArr[i]) == uid) {
                return true;
            }
            i++;
        }
    }

    private boolean isUidChangeApp(int uid) {
        for (int i = 0; i < LOCAL_EF_APP_LIST.length; i++) {
            if (getUidFromMap(EF_PN_WECHAT) == uid) {
                return false;
            }
        }
        return true;
    }

    private void removeUidFromMap(String packageName) {
        this.mUidMap.remove(packageName);
    }

    private void removeAll() {
        this.mUidMap.clear();
    }

    public void updateAppPN(String packageName, int uid, boolean action) {
        Set<String> set = this.mEFAppsPN;
        if (set != null && set.contains(packageName)) {
            if (action) {
                addUidToMap(packageName, uid);
            } else {
                removeUidFromMap(packageName);
            }
        }
    }

    public void updateQosForUidStateChange(int uid, int oldUidState, int newUidState) {
        if (isUidValidForQos(uid) && isUidChangeApp(uid) && isQosEnabledForUid(uid, oldUidState) != isQosEnabledForUid(uid, newUidState)) {
            updateQosForUidState(uid, newUidState);
        }
    }

    private boolean isQosEnabledForUid(int uid, int state) {
        return state == 2 && hasUidFromMap(uid);
    }

    private void updateQosForUidState(int uid, int state) {
        if (isQosEnabledForUid(uid, state)) {
            setQos(true, uid, IP);
        } else {
            setQos(false, uid, IP);
        }
    }
}
