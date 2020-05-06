package com.miui.networkassistant.service.tm;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.j.B;
import b.b.o.b.a.a;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.service.IAppMonitorBinder;
import com.miui.networkassistant.service.IAppMonitorBinderListener;
import com.miui.networkassistant.traffic.statistic.PreSetGroup;
import com.miui.networkassistant.utils.PackageUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import miui.security.SecurityManager;

class AppMonitor {
    /* access modifiers changed from: private */
    public static final String TAG = "AppMonitor";
    /* access modifiers changed from: private */
    public Object mAppMapLock = new Object();
    private AppMonitorBinder mAppMonitorBinder = new AppMonitorBinder();
    private Context mContext;
    private int mCurrentUserId = UserHandle.USER_NULL;
    /* access modifiers changed from: private */
    public Object mFillAppLock = new Object();
    /* access modifiers changed from: private */
    public HashMap<String, AppInfo> mFilteredAppInfosMap;
    /* access modifiers changed from: private */
    public boolean mIsInited = false;
    private boolean mIsOldDevice = false;
    /* access modifiers changed from: private */
    public RemoteCallbackList<IAppMonitorBinderListener> mListeners = new RemoteCallbackList<>();
    /* access modifiers changed from: private */
    public HashMap<String, AppInfo> mNetworkAccessedAppsMap;
    /* access modifiers changed from: private */
    public HashMap<String, AppInfo> mNonSystemAppMap;
    private PackageManager mPkgManager;
    private SecurityManager mSecurityManager;
    private SocketTaggerManager mSocketTaggerManager;
    /* access modifiers changed from: private */
    public HashMap<String, AppInfo> mSystemAppMap;

    private class AppMonitorBinder extends IAppMonitorBinder.Stub {
        private AppMonitorBinder() {
        }

        public AppInfo getAppInfoByPackageName(String str) {
            AppInfo appInfo;
            synchronized (AppMonitor.this.mAppMapLock) {
                appInfo = (AppInfo) AppMonitor.this.mNetworkAccessedAppsMap.get(str);
            }
            return appInfo;
        }

        public List getFilteredAppInfosList() {
            ArrayList arrayList;
            synchronized (AppMonitor.this.mAppMapLock) {
                arrayList = new ArrayList(AppMonitor.this.mFilteredAppInfosMap.values());
            }
            return arrayList;
        }

        public List getNetworkAccessedAppList() {
            synchronized (AppMonitor.this.mAppMapLock) {
                if (AppMonitor.this.mNetworkAccessedAppsMap == null) {
                    return null;
                }
                ArrayList arrayList = new ArrayList(AppMonitor.this.mNetworkAccessedAppsMap.values());
                return arrayList;
            }
        }

        public Map getNetworkAccessedAppsMap() {
            synchronized (AppMonitor.this.mAppMapLock) {
                if (AppMonitor.this.mNetworkAccessedAppsMap == null) {
                    return null;
                }
                HashMap hashMap = (HashMap) AppMonitor.this.mNetworkAccessedAppsMap.clone();
                return hashMap;
            }
        }

        public List getNonSystemAppList() {
            synchronized (AppMonitor.this.mAppMapLock) {
                if (AppMonitor.this.mNonSystemAppMap == null) {
                    return null;
                }
                ArrayList arrayList = new ArrayList(AppMonitor.this.mNonSystemAppMap.values());
                return arrayList;
            }
        }

        public List getSystemAppList() {
            synchronized (AppMonitor.this.mAppMapLock) {
                if (AppMonitor.this.mSystemAppMap == null) {
                    return null;
                }
                ArrayList arrayList = new ArrayList(AppMonitor.this.mSystemAppMap.values());
                return arrayList;
            }
        }

        public void registerLisener(IAppMonitorBinderListener iAppMonitorBinderListener) {
            synchronized (AppMonitor.this.mListeners) {
                AppMonitor.this.mListeners.register(iAppMonitorBinderListener);
                if (AppMonitor.this.mIsInited) {
                    try {
                        iAppMonitorBinderListener.onAppListUpdated();
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void unRegisterLisener(IAppMonitorBinderListener iAppMonitorBinderListener) {
            synchronized (AppMonitor.this.mListeners) {
                AppMonitor.this.mListeners.unregister(iAppMonitorBinderListener);
            }
        }
    }

    AppMonitor(Context context) {
        Log.i(TAG, "mina MonitorCenter created");
        this.mContext = context.getApplicationContext();
        this.mPkgManager = this.mContext.getPackageManager();
        this.mSecurityManager = (SecurityManager) this.mContext.getSystemService("security");
    }

    private void addMiuiFirewallSharedUid(SecurityManager securityManager, int i) {
        String str;
        String str2;
        if (!this.mIsOldDevice) {
            Class<?> cls = securityManager.getClass();
            try {
                cls.getMethod("addMiuiFirewallSharedUid", new Class[]{Integer.TYPE}).invoke(securityManager, new Object[]{Integer.valueOf(i)});
                return;
            } catch (NoSuchMethodException unused) {
                this.mIsOldDevice = true;
                str2 = TAG;
                str = "addMiuiFirewallSharedUid NoSuchMethodException";
            } catch (Exception unused2) {
                str2 = TAG;
                str = "addMiuiFirewallSharedUid Exception";
            }
        } else {
            return;
        }
        Log.e(str2, str);
    }

    /* access modifiers changed from: private */
    public void broadcastAppListUpdated() {
        synchronized (this.mListeners) {
            int beginBroadcast = this.mListeners.beginBroadcast();
            while (beginBroadcast > 0) {
                beginBroadcast--;
                try {
                    this.mListeners.getBroadcastItem(beginBroadcast).onAppListUpdated();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            this.mListeners.finishBroadcast();
        }
    }

    private AppInfo buildAppInfo(String str, int i, boolean z) {
        AppInfo appInfo = new AppInfo();
        appInfo.uid = i;
        appInfo.packageName = PackageUtil.getPackageNameFormat(str, i);
        appInfo.isSystemApp = z;
        return appInfo;
    }

    /* access modifiers changed from: private */
    public void fillNetworkAccessedApps() {
        List<ApplicationInfo> a2;
        HashMap<String, AppInfo> hashMap = new HashMap<>();
        HashMap<String, AppInfo> hashMap2 = new HashMap<>();
        HashMap<String, AppInfo> hashMap3 = new HashMap<>();
        HashMap<String, AppInfo> hashMap4 = new HashMap<>();
        HashMap hashMap5 = new HashMap();
        HashSet<Integer> hashSet = new HashSet<>();
        List<ApplicationInfo> a3 = a.a(0, this.mCurrentUserId);
        if (a3 != null) {
            if (this.mCurrentUserId == 0 && (a2 = a.a(0, 999)) != null) {
                a3.addAll(a2);
            }
            for (ApplicationInfo next : a3) {
                if (b.b.o.c.a.a(this.mContext, next.packageName, B.c(next.uid))) {
                    Log.d("Enterprise", "Net config is restricted for package" + next.packageName);
                } else if (PackageUtil.getAppEnable(next.packageName, this.mCurrentUserId) && PackageUtil.hasInternetPermission(this.mPkgManager, next.packageName)) {
                    AppInfo buildAppInfo = buildAppInfo(next.packageName, next.uid, PackageUtil.isSystemApp(next));
                    if (buildAppInfo.isSystemApp) {
                        if (hashMap5.containsKey(Integer.valueOf(next.uid))) {
                            hashSet.add(Integer.valueOf(next.uid));
                            if (PreSetGroup.isGroupHead(next.packageName)) {
                                AppInfo appInfo = (AppInfo) hashMap5.get(Integer.valueOf(next.uid));
                                hashMap2.remove(appInfo.packageName);
                                hashMap4.remove(appInfo.packageName);
                            }
                        }
                        hashMap2.put(buildAppInfo.packageName.toString(), buildAppInfo);
                        hashMap4.put(buildAppInfo.packageName.toString(), buildAppInfo);
                        hashMap5.put(Integer.valueOf(next.uid), buildAppInfo);
                    } else {
                        if (!hashMap5.containsKey(Integer.valueOf(next.uid))) {
                            hashMap4.put(buildAppInfo.packageName.toString(), buildAppInfo);
                            hashMap5.put(Integer.valueOf(next.uid), buildAppInfo);
                        } else {
                            hashSet.add(Integer.valueOf(next.uid));
                        }
                        hashMap3.put(buildAppInfo.packageName.toString(), buildAppInfo);
                    }
                    hashMap.put(buildAppInfo.packageName.toString(), buildAppInfo);
                }
            }
            hashMap5.clear();
            for (Integer intValue : hashSet) {
                addMiuiFirewallSharedUid(this.mSecurityManager, intValue.intValue());
            }
            hashSet.clear();
            synchronized (this.mAppMapLock) {
                this.mNetworkAccessedAppsMap = hashMap;
                this.mSystemAppMap = hashMap2;
                this.mNonSystemAppMap = hashMap3;
                this.mFilteredAppInfosMap = hashMap4;
                this.mIsInited = true;
            }
        }
    }

    private boolean isShouldRefreshAppList(int i) {
        return this.mCurrentUserId != i;
    }

    /* access modifiers changed from: package-private */
    public IAppMonitorBinder getBinder() {
        return this.mAppMonitorBinder;
    }

    /* access modifiers changed from: package-private */
    public void initData(int i) {
        if (isShouldRefreshAppList(i)) {
            Log.i(TAG, "init app list");
            this.mCurrentUserId = i;
            b.b.c.c.a.a.a(new Runnable() {
                public void run() {
                    synchronized (AppMonitor.this.mFillAppLock) {
                        AppMonitor.this.fillNetworkAccessedApps();
                        int i = 0;
                        while (!AppMonitor.this.mIsInited) {
                            int i2 = i + 1;
                            if (i >= 5) {
                                break;
                            }
                            String access$400 = AppMonitor.TAG;
                            Log.i(access$400, "fillNetworkAccessedApps failed, retryCount:" + i2);
                            AppMonitor.this.fillNetworkAccessedApps();
                            i = i2;
                        }
                        if (AppMonitor.this.mIsInited) {
                            AppMonitor.this.broadcastAppListUpdated();
                        }
                    }
                }
            });
        }
    }

    /* access modifiers changed from: package-private */
    public void onPackageChanged(Intent intent) {
        HashMap<String, AppInfo> hashMap;
        String charSequence;
        if (intent != null && this.mIsInited) {
            Uri data = intent.getData();
            String str = null;
            if (data != null) {
                str = data.getEncodedSchemeSpecificPart();
            }
            int intExtra = intent.getIntExtra("android.intent.extra.UID", 0);
            if (!TextUtils.isEmpty(str) && intExtra != 0) {
                String action = intent.getAction();
                if (TextUtils.equals(action, Constants.System.ACTION_PACKAGE_ADDED)) {
                    boolean isSystemApp = PackageUtil.isSystemApp(this.mContext, str);
                    if (isSystemApp && PreSetGroup.isGroupUid(intExtra) && !PreSetGroup.isGroupHead(str)) {
                        return;
                    }
                    if (PackageUtil.hasInternetPermission(this.mPkgManager, str)) {
                        AppInfo buildAppInfo = buildAppInfo(str, intExtra, isSystemApp);
                        synchronized (this.mAppMapLock) {
                            this.mNetworkAccessedAppsMap.put(buildAppInfo.packageName.toString(), buildAppInfo);
                            if (buildAppInfo.isSystemApp) {
                                hashMap = this.mSystemAppMap;
                                charSequence = buildAppInfo.packageName.toString();
                            } else {
                                hashMap = this.mNonSystemAppMap;
                                charSequence = buildAppInfo.packageName.toString();
                            }
                            hashMap.put(charSequence, buildAppInfo);
                            this.mFilteredAppInfosMap.put(buildAppInfo.packageName.toString(), buildAppInfo);
                        }
                    }
                } else if (TextUtils.equals(action, Constants.System.ACTION_PACKAGE_REMOVED)) {
                    synchronized (this.mAppMapLock) {
                        String packageNameFormat = PackageUtil.getPackageNameFormat(str, intExtra);
                        this.mNetworkAccessedAppsMap.remove(packageNameFormat);
                        this.mSystemAppMap.remove(packageNameFormat);
                        this.mNonSystemAppMap.remove(packageNameFormat);
                        this.mFilteredAppInfosMap.remove(packageNameFormat);
                    }
                }
                broadcastAppListUpdated();
            }
        }
    }
}
