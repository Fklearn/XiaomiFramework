package com.miui.networkassistant.vpn.miui;

import android.app.IMiuiProcessObserver;
import android.app.IProcessObserver;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.c.a.a;
import b.b.c.h.f;
import b.b.c.j.B;
import b.b.c.j.g;
import b.b.c.j.x;
import com.android.internal.content.PackageMonitor;
import com.miui.networkassistant.config.CommonConfig;
import com.miui.networkassistant.firewall.BackgroundPolicyService;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.PackageUtil;
import com.miui.networkassistant.utils.PrivacyDeclareAndAllowNetworkUtil;
import com.miui.networkassistant.vpn.miui.IMiuiVpnManageService;
import com.miui.networkassistant.vpn.miui.IMiuiVpnManageServiceCallback;
import com.miui.networkassistant.vpn.miui.MiuiVpnUtils;
import com.miui.networkassistant.webapi.WebApiAccessHelper;
import com.miui.securitycenter.h;
import com.miui.support.provider.MiuiSettingsCompat$SettingsCloudData;
import com.miui.support.provider.c;
import com.miui.vpnsdkmanager.IMiuiVpnSdkService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.cloud.CloudPushConstants;
import miui.provider.ExtraNetwork;
import miui.securitycenter.NetworkUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class MiuiVpnManageService extends Service {
    private static final Uri CONTENT_URI_GET_SETTINGS_YUNYOU = Uri.parse("content://com.miui.vpnsdkmanager/settings/xunyou");
    private static final String MIUI_VPN_INFOS = "miui_vpn_infos";
    private static final String MIUI_VPN_NAME_XUNYOU = "xunyou";
    private static final int MIUI_VPN_TYPE_UNKNOWN = 0;
    private static final int MIUI_VPN_TYPE_XUNYOU = 4;
    private static final int MSG_RESTART_SERVICE = 262;
    protected static String TAG = "MiuiVpnManageService";
    private static final String VPN_PROC_NAME = "com.miui.vpnsdkmanager";
    private static final int VPN_STATE_CONNECTED = 1;
    private static final int VPN_STATE_DISCONNECTED = 3;
    private static final int VPN_STATE_NONE = 0;
    private static final int VPN_STATE_PAUSE = 2;
    /* access modifiers changed from: private */
    public int mAppUid = -1;
    /* access modifiers changed from: private */
    public final Handler mBackgroundHandler;
    private Handler.Callback mBackgroundHandlerCallback = new Handler.Callback() {
        public boolean handleMessage(Message message) {
            if (message.what != MiuiVpnManageService.MSG_RESTART_SERVICE) {
                return true;
            }
            MiuiVpnManageService.this.restartService();
            return true;
        }
    };
    private RemoteCallbackList<IMiuiVpnManageServiceCallback> mCallbackList = new RemoteCallbackList<>();
    private final ContentObserver mCloudDataObserver = new ContentObserver(new Handler()) {
        public void onChange(boolean z) {
            Log.i(MiuiVpnManageService.TAG, "mCloudDataObserver change");
            MiuiVpnManageService.this.updateMiuiVpnInfos(false);
        }
    };
    /* access modifiers changed from: private */
    public CommonConfig mCommonConfig;
    /* access modifiers changed from: private */
    public Context mContext;
    private final HandlerThread mHandlerThread = new HandlerThread(TAG);
    /* access modifiers changed from: private */
    public List<MiuiVpnUtils.MiuiVpnDetailInfo> mMiuiVpnDetailInfos = new ArrayList();
    /* access modifiers changed from: private */
    public Map<Integer, MiuiVpnInfo> mMiuiVpnInfos = new HashMap();
    private MiuiVpnManageServiceBinder mMiuiVpnManageServiceBinder;
    /* access modifiers changed from: private */
    public MiuiVpnManageServiceCallback mMiuiVpnManageServiceCallback = new MiuiVpnManageServiceCallback();
    /* access modifiers changed from: private */
    public IMiuiVpnSdkService mMiuiVpnSdkService;
    private ServiceConnection mMiuiVpnSdkServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            String str = MiuiVpnManageService.TAG;
            Log.i(str, "onServiceConnected. name=" + componentName + ", vpnState=" + MiuiVpnManageService.this.mVpnState);
            if (iBinder == null) {
                Log.e(MiuiVpnManageService.TAG, "onServiceConnected. service is null!");
                return;
            }
            synchronized (MiuiVpnManageService.this.mVpnSdkServiceLocker) {
                try {
                    IMiuiVpnSdkService unused = MiuiVpnManageService.this.mMiuiVpnSdkService = IMiuiVpnSdkService.Stub.a(iBinder);
                    int unused2 = MiuiVpnManageService.this.mVpnProcPid = MiuiVpnManageService.this.mMiuiVpnSdkService.z();
                    if (!(MiuiVpnManageService.this.mPendingDestAppInfo == null || MiuiVpnManageService.this.mVpnProcPid == 0 || MiuiVpnManageService.this.mVpnPkgUid == 0)) {
                        MiuiVpnUtils.keepVpnProcAlive(MiuiVpnManageService.this.mContext, MiuiVpnManageService.VPN_PROC_NAME, MiuiVpnManageService.this.mVpnPkgUid, MiuiVpnManageService.this.mVpnProcPid, MiuiVpnManageService.this.mPendingDestAppInfo.mPackageName, MiuiVpnManageService.this.mPendingDestAppInfo.mUid, MiuiVpnManageService.this.mPendingDestAppInfo.mPid);
                        MiuiVpnUtils.WatchPackageInfo unused3 = MiuiVpnManageService.this.mPendingDestAppInfo = null;
                    }
                    MiuiVpnManageService.this.mMiuiVpnSdkService.registerCallback(MiuiVpnManageService.this.mMiuiVpnManageServiceCallback);
                    int unused4 = MiuiVpnManageService.this.init(MiuiVpnManageService.this.mVpnType);
                    int access$2400 = MiuiVpnManageService.this.mVpnState;
                    if (access$2400 == 1) {
                        int unused5 = MiuiVpnManageService.this.connectVpn(MiuiVpnManageService.this.mAppUid);
                    } else if (access$2400 != 2) {
                    }
                } catch (Exception e) {
                    Log.e(MiuiVpnManageService.TAG, "onServiceConnected", e);
                }
            }
        }

        public void onServiceDisconnected(ComponentName componentName) {
            String str = MiuiVpnManageService.TAG;
            Log.i(str, "onServiceDisconnected. name=" + componentName);
            synchronized (MiuiVpnManageService.this.mVpnSdkServiceLocker) {
                IMiuiVpnSdkService unused = MiuiVpnManageService.this.mMiuiVpnSdkService = null;
            }
            MiuiVpnManageService.this.unbindVpnSdkService();
            MiuiVpnManageService.this.mBackgroundHandler.sendEmptyMessageDelayed(MiuiVpnManageService.MSG_RESTART_SERVICE, 1000);
        }
    };
    private BroadcastReceiver mNetworkConnectivityReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (TextUtils.isEmpty(MiuiVpnManageService.this.mCommonConfig.getMiuiVpnInfos())) {
                MiuiVpnManageService.this.forceUpdateCloudData();
            }
        }
    };
    private PackageMonitor mPackageMonitor = new MiuiVpnPackageMonitor();
    /* access modifiers changed from: private */
    public MiuiVpnUtils.WatchPackageInfo mPendingDestAppInfo;
    private IProcessObserver mProcessObserver = new IMiuiProcessObserver() {
        /* JADX WARNING: Code restructure failed: missing block: B:35:?, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onForegroundActivitiesChanged(int r8, int r9, boolean r10) {
            /*
                r7 = this;
                if (r10 == 0) goto L_0x0072
                com.miui.networkassistant.vpn.miui.MiuiVpnManageService r10 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.this
                java.util.Map r10 = r10.mWatchPackages
                monitor-enter(r10)
                com.miui.networkassistant.vpn.miui.MiuiVpnManageService r0 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.this     // Catch:{ all -> 0x006f }
                java.util.Map r0 = r0.mWatchPackages     // Catch:{ all -> 0x006f }
                java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ all -> 0x006f }
                java.lang.Object r9 = r0.get(r9)     // Catch:{ all -> 0x006f }
                com.miui.networkassistant.vpn.miui.MiuiVpnUtils$WatchPackageInfo r9 = (com.miui.networkassistant.vpn.miui.MiuiVpnUtils.WatchPackageInfo) r9     // Catch:{ all -> 0x006f }
                if (r9 != 0) goto L_0x001d
                monitor-exit(r10)     // Catch:{ all -> 0x006f }
                return
            L_0x001d:
                com.miui.networkassistant.vpn.miui.MiuiVpnManageService r0 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.this     // Catch:{ all -> 0x006f }
                com.miui.networkassistant.vpn.miui.MiuiVpnManageService r1 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.this     // Catch:{ all -> 0x006f }
                int r1 = r1.mVpnType     // Catch:{ all -> 0x006f }
                com.miui.networkassistant.vpn.miui.MiuiVpnManageService r2 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.this     // Catch:{ all -> 0x006f }
                int r2 = r2.mVpnState     // Catch:{ all -> 0x006f }
                r3 = 0
                r0.onVpnStateChanged(r1, r2, r3)     // Catch:{ all -> 0x006f }
                r9.mPid = r8     // Catch:{ all -> 0x006f }
                boolean r8 = r9.mIsRunning     // Catch:{ all -> 0x006f }
                if (r8 == 0) goto L_0x0037
                monitor-exit(r10)     // Catch:{ all -> 0x006f }
                return
            L_0x0037:
                r8 = 1
                r9.mIsRunning = r8     // Catch:{ all -> 0x006f }
                com.miui.networkassistant.vpn.miui.MiuiVpnManageService r8 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.this     // Catch:{ all -> 0x006f }
                int r8 = r8.mVpnProcPid     // Catch:{ all -> 0x006f }
                if (r8 == 0) goto L_0x0068
                com.miui.networkassistant.vpn.miui.MiuiVpnManageService r8 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.this     // Catch:{ all -> 0x006f }
                int r8 = r8.mVpnPkgUid     // Catch:{ all -> 0x006f }
                if (r8 == 0) goto L_0x0068
                com.miui.networkassistant.vpn.miui.MiuiVpnManageService r8 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.this     // Catch:{ all -> 0x006f }
                android.content.Context r0 = r8.mContext     // Catch:{ all -> 0x006f }
                java.lang.String r1 = "com.miui.vpnsdkmanager"
                com.miui.networkassistant.vpn.miui.MiuiVpnManageService r8 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.this     // Catch:{ all -> 0x006f }
                int r2 = r8.mVpnPkgUid     // Catch:{ all -> 0x006f }
                com.miui.networkassistant.vpn.miui.MiuiVpnManageService r8 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.this     // Catch:{ all -> 0x006f }
                int r3 = r8.mVpnProcPid     // Catch:{ all -> 0x006f }
                java.lang.String r4 = r9.mPackageName     // Catch:{ all -> 0x006f }
                int r5 = r9.mUid     // Catch:{ all -> 0x006f }
                int r6 = r9.mPid     // Catch:{ all -> 0x006f }
                com.miui.networkassistant.vpn.miui.MiuiVpnUtils.keepVpnProcAlive(r0, r1, r2, r3, r4, r5, r6)     // Catch:{ all -> 0x006f }
                goto L_0x006d
            L_0x0068:
                com.miui.networkassistant.vpn.miui.MiuiVpnManageService r8 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.this     // Catch:{ all -> 0x006f }
                com.miui.networkassistant.vpn.miui.MiuiVpnUtils.WatchPackageInfo unused = r8.mPendingDestAppInfo = r9     // Catch:{ all -> 0x006f }
            L_0x006d:
                monitor-exit(r10)     // Catch:{ all -> 0x006f }
                goto L_0x008e
            L_0x006f:
                r8 = move-exception
                monitor-exit(r10)     // Catch:{ all -> 0x006f }
                throw r8
            L_0x0072:
                com.miui.networkassistant.vpn.miui.MiuiVpnManageService r8 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.this
                java.util.Map r8 = r8.mWatchPackages
                monitor-enter(r8)
                com.miui.networkassistant.vpn.miui.MiuiVpnManageService r10 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.this     // Catch:{ all -> 0x008f }
                java.util.Map r10 = r10.mWatchPackages     // Catch:{ all -> 0x008f }
                java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ all -> 0x008f }
                java.lang.Object r9 = r10.get(r9)     // Catch:{ all -> 0x008f }
                com.miui.networkassistant.vpn.miui.MiuiVpnUtils$WatchPackageInfo r9 = (com.miui.networkassistant.vpn.miui.MiuiVpnUtils.WatchPackageInfo) r9     // Catch:{ all -> 0x008f }
                if (r9 != 0) goto L_0x008d
                monitor-exit(r8)     // Catch:{ all -> 0x008f }
                return
            L_0x008d:
                monitor-exit(r8)     // Catch:{ all -> 0x008f }
            L_0x008e:
                return
            L_0x008f:
                r9 = move-exception
                monitor-exit(r8)     // Catch:{ all -> 0x008f }
                throw r9
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.vpn.miui.MiuiVpnManageService.AnonymousClass6.onForegroundActivitiesChanged(int, int, boolean):void");
        }

        public void onImportanceChanged(int i, int i2, int i3) {
        }

        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0028, code lost:
            if (android.text.TextUtils.isEmpty(r1) != false) goto L_?;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x002a, code lost:
            r0 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.TAG;
            android.util.Log.i(r0, "onProcessDied. uid=" + r6 + " pid=" + r5);
            com.miui.networkassistant.vpn.miui.MiuiVpnManageService.access$2900(r4.this$0, r6, r1);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:?, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onProcessDied(int r5, int r6) {
            /*
                r4 = this;
                com.miui.networkassistant.vpn.miui.MiuiVpnManageService r0 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.this
                java.util.Map r0 = r0.mWatchPackages
                monitor-enter(r0)
                com.miui.networkassistant.vpn.miui.MiuiVpnManageService r1 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.this     // Catch:{ all -> 0x004e }
                java.util.Map r1 = r1.mWatchPackages     // Catch:{ all -> 0x004e }
                java.lang.Integer r2 = java.lang.Integer.valueOf(r6)     // Catch:{ all -> 0x004e }
                java.lang.Object r1 = r1.get(r2)     // Catch:{ all -> 0x004e }
                com.miui.networkassistant.vpn.miui.MiuiVpnUtils$WatchPackageInfo r1 = (com.miui.networkassistant.vpn.miui.MiuiVpnUtils.WatchPackageInfo) r1     // Catch:{ all -> 0x004e }
                if (r1 != 0) goto L_0x001b
                monitor-exit(r0)     // Catch:{ all -> 0x004e }
                return
            L_0x001b:
                boolean r2 = r1.mIsRunning     // Catch:{ all -> 0x004e }
                if (r2 != 0) goto L_0x0021
                monitor-exit(r0)     // Catch:{ all -> 0x004e }
                return
            L_0x0021:
                java.lang.String r1 = r1.mPackageName     // Catch:{ all -> 0x004e }
                monitor-exit(r0)     // Catch:{ all -> 0x004e }
                boolean r0 = android.text.TextUtils.isEmpty(r1)
                if (r0 != 0) goto L_0x004d
                java.lang.String r0 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.TAG
                java.lang.StringBuilder r2 = new java.lang.StringBuilder
                r2.<init>()
                java.lang.String r3 = "onProcessDied. uid="
                r2.append(r3)
                r2.append(r6)
                java.lang.String r3 = " pid="
                r2.append(r3)
                r2.append(r5)
                java.lang.String r5 = r2.toString()
                android.util.Log.i(r0, r5)
                com.miui.networkassistant.vpn.miui.MiuiVpnManageService r5 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.this
                r5.onWatchPackageDied(r6, r1)
            L_0x004d:
                return
            L_0x004e:
                r5 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x004e }
                throw r5
            */
            throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.vpn.miui.MiuiVpnManageService.AnonymousClass6.onProcessDied(int, int):void");
        }

        public void onProcessStateChanged(int i, int i2, int i3) {
        }
    };
    private BroadcastReceiver mScNetworkStatusReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getBooleanExtra("extra_network_status", false)) {
                MiuiVpnManageService.this.forceUpdateCloudData();
            }
        }
    };
    private boolean mSuportVpn;
    /* access modifiers changed from: private */
    public Object mVpnInfoLock = new Object();
    /* access modifiers changed from: private */
    public int mVpnPkgUid = 0;
    /* access modifiers changed from: private */
    public int mVpnProcPid = 0;
    /* access modifiers changed from: private */
    public Object mVpnSdkServiceLocker = new Object();
    /* access modifiers changed from: private */
    public int mVpnState = 0;
    /* access modifiers changed from: private */
    public int mVpnType = 0;
    /* access modifiers changed from: private */
    public Map<Integer, MiuiVpnUtils.WatchPackageInfo> mWatchPackages = new HashMap();

    private class MiuiVpnManageServiceBinder extends IMiuiVpnManageService.Stub {
        private MiuiVpnManageServiceBinder() {
        }

        public int connectVpn(String str) {
            return MiuiVpnManageService.this.connectVpn(str);
        }

        public void disConnectVpn() {
            int unused = MiuiVpnManageService.this.disconnectVpn();
        }

        public void getCoupons() {
            MiuiVpnManageService.this.getCoupons();
        }

        public String getSetting(String str, String str2) {
            return MiuiVpnManageService.this.getSetting(str, str2);
        }

        public String getSettingEx(String str, String str2, String str3) {
            return MiuiVpnManageService.this.getSettingEx(str, str2, str3);
        }

        public List<String> getSupportApps(String str) {
            Log.i(MiuiVpnManageService.TAG, "getSupportApps");
            return MiuiVpnManageService.this.getSupportApps(str);
        }

        public String getSupportVpn() {
            Log.i(MiuiVpnManageService.TAG, "getSupportVpn");
            return MiuiVpnManageService.this.getSupportVpn();
        }

        public boolean getVpnEnabled(String str, String str2) {
            Log.i(MiuiVpnManageService.TAG, "getVpnEnabled");
            return MiuiVpnManageService.this.getVpnEnabled(str, str2);
        }

        public int init(String str) {
            return MiuiVpnManageService.this.init(str);
        }

        public int refreshUserState() {
            return MiuiVpnManageService.this.refreshUserState();
        }

        public void registerCallback(IMiuiVpnManageServiceCallback iMiuiVpnManageServiceCallback) {
            MiuiVpnManageService.this.registerCallback(iMiuiVpnManageServiceCallback);
        }

        public boolean setSetting(String str, String str2) {
            return MiuiVpnManageService.this.setSetting(str, str2);
        }

        public boolean setSettingEx(String str, String str2, String str3) {
            return MiuiVpnManageService.this.setSettingEx(str, str2, str3);
        }

        public int setVpnEnabled(String str, String str2, boolean z) {
            Log.i(MiuiVpnManageService.TAG, "setVpnEnabled");
            return MiuiVpnManageService.this.setVpnEnabled(str, str2, z);
        }

        public void unregisterCallback(IMiuiVpnManageServiceCallback iMiuiVpnManageServiceCallback) {
            MiuiVpnManageService.this.unregisterCallback(iMiuiVpnManageServiceCallback);
        }
    }

    private class MiuiVpnManageServiceCallback extends IMiuiVpnManageServiceCallback.Stub {
        private MiuiVpnManageServiceCallback() {
        }

        public boolean isVpnConnected() {
            return false;
        }

        public void onQueryCouponsResult(int i, List<String> list) {
            MiuiVpnManageService.this.onQueryCouponsResult(i, list);
        }

        public void onVpnStateChanged(int i, int i2, String str) {
            MiuiVpnManageService.this.onVpnStateChanged(i, i2, str);
        }
    }

    private class MiuiVpnPackageMonitor extends PackageMonitor {
        private MiuiVpnPackageMonitor() {
        }

        public void onPackageAdded(final String str, final int i) {
            String str2 = MiuiVpnManageService.TAG;
            Log.i(str2, "PackageMonitor. onPackageAdded: packageName =" + str);
            a.a(new Runnable() {
                /* JADX WARNING: Code restructure failed: missing block: B:23:0x0067, code lost:
                    r1 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.access$3400(r9.this$1.this$0);
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:24:0x006f, code lost:
                    monitor-enter(r1);
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:27:0x0078, code lost:
                    if (com.miui.networkassistant.vpn.miui.MiuiVpnManageService.access$3500(r9.this$1.this$0) == null) goto L_0x0092;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
                    com.miui.networkassistant.vpn.miui.MiuiVpnManageService.access$3500(r9.this$1.this$0).d(r4, r5);
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:30:0x008a, code lost:
                    r0 = move-exception;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:32:?, code lost:
                    android.util.Log.e(com.miui.networkassistant.vpn.miui.MiuiVpnManageService.TAG, "PackageMonitor an exception occurred!", r0);
                 */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public void run() {
                    /*
                        r9 = this;
                        com.miui.networkassistant.vpn.miui.MiuiVpnManageService$MiuiVpnPackageMonitor r0 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.MiuiVpnPackageMonitor.this
                        com.miui.networkassistant.vpn.miui.MiuiVpnManageService r0 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.this
                        java.lang.Object r0 = r0.mVpnInfoLock
                        monitor-enter(r0)
                        com.miui.networkassistant.vpn.miui.MiuiVpnManageService$MiuiVpnPackageMonitor r1 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.MiuiVpnPackageMonitor.this     // Catch:{ all -> 0x0097 }
                        com.miui.networkassistant.vpn.miui.MiuiVpnManageService r1 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.this     // Catch:{ all -> 0x0097 }
                        java.util.List r1 = r1.mMiuiVpnDetailInfos     // Catch:{ all -> 0x0097 }
                        int r1 = r1.size()     // Catch:{ all -> 0x0097 }
                        if (r1 != 0) goto L_0x0019
                        monitor-exit(r0)     // Catch:{ all -> 0x0097 }
                        return
                    L_0x0019:
                        com.miui.networkassistant.vpn.miui.MiuiVpnManageService$MiuiVpnPackageMonitor r1 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.MiuiVpnPackageMonitor.this     // Catch:{ all -> 0x0097 }
                        com.miui.networkassistant.vpn.miui.MiuiVpnManageService r1 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.this     // Catch:{ all -> 0x0097 }
                        java.util.List r1 = r1.mMiuiVpnDetailInfos     // Catch:{ all -> 0x0097 }
                        java.util.Iterator r1 = r1.iterator()     // Catch:{ all -> 0x0097 }
                    L_0x0025:
                        boolean r2 = r1.hasNext()     // Catch:{ all -> 0x0097 }
                        if (r2 == 0) goto L_0x0066
                        java.lang.Object r2 = r1.next()     // Catch:{ all -> 0x0097 }
                        com.miui.networkassistant.vpn.miui.MiuiVpnUtils$MiuiVpnDetailInfo r2 = (com.miui.networkassistant.vpn.miui.MiuiVpnUtils.MiuiVpnDetailInfo) r2     // Catch:{ all -> 0x0097 }
                        java.util.List r2 = r2.getPackages()     // Catch:{ all -> 0x0097 }
                        java.lang.String r3 = r4     // Catch:{ all -> 0x0097 }
                        boolean r2 = r2.contains(r3)     // Catch:{ all -> 0x0097 }
                        if (r2 == 0) goto L_0x0025
                        com.miui.networkassistant.vpn.miui.MiuiVpnManageService$MiuiVpnPackageMonitor r2 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.MiuiVpnPackageMonitor.this     // Catch:{ all -> 0x0097 }
                        com.miui.networkassistant.vpn.miui.MiuiVpnManageService r2 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.this     // Catch:{ all -> 0x0097 }
                        java.util.Map r2 = r2.mWatchPackages     // Catch:{ all -> 0x0097 }
                        monitor-enter(r2)     // Catch:{ all -> 0x0097 }
                        com.miui.networkassistant.vpn.miui.MiuiVpnManageService$MiuiVpnPackageMonitor r3 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.MiuiVpnPackageMonitor.this     // Catch:{ all -> 0x0063 }
                        com.miui.networkassistant.vpn.miui.MiuiVpnManageService r3 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.this     // Catch:{ all -> 0x0063 }
                        java.util.Map r3 = r3.mWatchPackages     // Catch:{ all -> 0x0063 }
                        int r4 = r5     // Catch:{ all -> 0x0063 }
                        java.lang.Integer r4 = java.lang.Integer.valueOf(r4)     // Catch:{ all -> 0x0063 }
                        com.miui.networkassistant.vpn.miui.MiuiVpnUtils$WatchPackageInfo r5 = new com.miui.networkassistant.vpn.miui.MiuiVpnUtils$WatchPackageInfo     // Catch:{ all -> 0x0063 }
                        int r6 = r5     // Catch:{ all -> 0x0063 }
                        java.lang.String r7 = r4     // Catch:{ all -> 0x0063 }
                        r8 = 0
                        r5.<init>(r6, r7, r8, r8)     // Catch:{ all -> 0x0063 }
                        r3.put(r4, r5)     // Catch:{ all -> 0x0063 }
                        monitor-exit(r2)     // Catch:{ all -> 0x0063 }
                        goto L_0x0025
                    L_0x0063:
                        r1 = move-exception
                        monitor-exit(r2)     // Catch:{ all -> 0x0063 }
                        throw r1     // Catch:{ all -> 0x0097 }
                    L_0x0066:
                        monitor-exit(r0)     // Catch:{ all -> 0x0097 }
                        com.miui.networkassistant.vpn.miui.MiuiVpnManageService$MiuiVpnPackageMonitor r0 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.MiuiVpnPackageMonitor.this
                        com.miui.networkassistant.vpn.miui.MiuiVpnManageService r0 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.this
                        java.lang.Object r1 = r0.mVpnSdkServiceLocker
                        monitor-enter(r1)
                        com.miui.networkassistant.vpn.miui.MiuiVpnManageService$MiuiVpnPackageMonitor r0 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.MiuiVpnPackageMonitor.this     // Catch:{ all -> 0x0094 }
                        com.miui.networkassistant.vpn.miui.MiuiVpnManageService r0 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.this     // Catch:{ all -> 0x0094 }
                        com.miui.vpnsdkmanager.IMiuiVpnSdkService r0 = r0.mMiuiVpnSdkService     // Catch:{ all -> 0x0094 }
                        if (r0 == 0) goto L_0x0092
                        com.miui.networkassistant.vpn.miui.MiuiVpnManageService$MiuiVpnPackageMonitor r0 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.MiuiVpnPackageMonitor.this     // Catch:{ Exception -> 0x008a }
                        com.miui.networkassistant.vpn.miui.MiuiVpnManageService r0 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.this     // Catch:{ Exception -> 0x008a }
                        com.miui.vpnsdkmanager.IMiuiVpnSdkService r0 = r0.mMiuiVpnSdkService     // Catch:{ Exception -> 0x008a }
                        java.lang.String r2 = r4     // Catch:{ Exception -> 0x008a }
                        int r3 = r5     // Catch:{ Exception -> 0x008a }
                        r0.d(r2, r3)     // Catch:{ Exception -> 0x008a }
                        goto L_0x0092
                    L_0x008a:
                        r0 = move-exception
                        java.lang.String r2 = com.miui.networkassistant.vpn.miui.MiuiVpnManageService.TAG     // Catch:{ all -> 0x0094 }
                        java.lang.String r3 = "PackageMonitor an exception occurred!"
                        android.util.Log.e(r2, r3, r0)     // Catch:{ all -> 0x0094 }
                    L_0x0092:
                        monitor-exit(r1)     // Catch:{ all -> 0x0094 }
                        return
                    L_0x0094:
                        r0 = move-exception
                        monitor-exit(r1)     // Catch:{ all -> 0x0094 }
                        throw r0
                    L_0x0097:
                        r1 = move-exception
                        monitor-exit(r0)     // Catch:{ all -> 0x0097 }
                        throw r1
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.vpn.miui.MiuiVpnManageService.MiuiVpnPackageMonitor.AnonymousClass1.run():void");
                }
            });
        }

        public void onPackageRemovedAllUsers(final String str, final int i) {
            String str2 = MiuiVpnManageService.TAG;
            Log.i(str2, "PackageMonitor. onPackageRemovedAllUsers: packageName =" + str);
            super.onPackageRemovedAllUsers(str, i);
            a.a(new Runnable() {
                public void run() {
                    synchronized (MiuiVpnManageService.this.mVpnInfoLock) {
                        if (MiuiVpnManageService.this.mMiuiVpnDetailInfos.size() != 0) {
                            for (MiuiVpnUtils.MiuiVpnDetailInfo packages : MiuiVpnManageService.this.mMiuiVpnDetailInfos) {
                                if (packages.getPackages().contains(str)) {
                                    synchronized (MiuiVpnManageService.this.mWatchPackages) {
                                        MiuiVpnManageService.this.mWatchPackages.put(Integer.valueOf(i), new MiuiVpnUtils.WatchPackageInfo(i, str, false, 0));
                                    }
                                }
                            }
                        }
                    }
                }
            });
        }
    }

    public MiuiVpnManageService() {
        boolean z = false;
        this.mHandlerThread.start();
        this.mBackgroundHandler = new Handler(this.mHandlerThread.getLooper(), this.mBackgroundHandlerCallback);
        if (!DeviceUtil.IS_INTERNATIONAL_BUILD && DeviceUtil.IS_L_OR_LATER) {
            z = true;
        }
        this.mSuportVpn = z;
    }

    private void bindVpnSdkService() {
        if (this.mSuportVpn) {
            synchronized (this.mVpnSdkServiceLocker) {
                if (this.mMiuiVpnSdkService != null) {
                    Log.w(TAG, "Vpn sdk service already bound");
                    return;
                }
                Intent intent = new Intent("com.miui.vpnsdkmanager.SDK_SERVICE");
                intent.setPackage(VPN_PROC_NAME);
                g.a(this.mContext, intent, this.mMiuiVpnSdkServiceConnection, 1, B.k());
            }
        }
    }

    private boolean checkNetworkPolicy(int i) {
        if (!PrivacyDeclareAndAllowNetworkUtil.isAllowNetwork()) {
            Log.e(TAG, "checkNetworkPolicy. SecurityCenter is not allow connect network");
            return false;
        }
        NetworkUtils.vpnPrepareAndAuthorize(VPN_PROC_NAME);
        this.mVpnPkgUid = PackageUtil.getUidByPackageName(this.mContext, VPN_PROC_NAME);
        if (this.mVpnPkgUid == 1000) {
            return true;
        }
        BackgroundPolicyService instance = BackgroundPolicyService.getInstance(this.mContext);
        if (instance.isAppRestrictBackground(VPN_PROC_NAME, this.mVpnPkgUid)) {
            instance.setAppRestrictBackground(this.mVpnPkgUid, false);
        }
        if (!ExtraNetwork.isMobileRestrict(this.mContext, VPN_PROC_NAME)) {
            return true;
        }
        ExtraNetwork.setMobileRestrict(this.mContext, VPN_PROC_NAME, false);
        return true;
    }

    /* access modifiers changed from: private */
    public int connectVpn(int i) {
        int i2;
        String str = TAG;
        Log.i(str, "connectVpn. uid=" + i);
        this.mAppUid = i;
        this.mVpnState = 1;
        synchronized (this.mVpnSdkServiceLocker) {
            i2 = 0;
            try {
                if (this.mMiuiVpnSdkService == null) {
                    Log.e(TAG, "connectVpn. sdkService is null. please call init first.");
                } else {
                    if (this.mVpnType == 4) {
                        i2 = this.mMiuiVpnSdkService.g(i);
                        String str2 = TAG;
                        Log.i(str2, "parpareApp. ret=" + i2);
                        if (i2 != 0) {
                            return i2;
                        }
                    }
                    i2 = this.mMiuiVpnSdkService.C();
                    String str3 = TAG;
                    Log.i(str3, "connectVpn. ret=" + i2);
                }
            } catch (Exception e) {
                Log.e(TAG, "connectVpn", e);
            }
        }
        return i2;
    }

    /* access modifiers changed from: private */
    public int connectVpn(String str) {
        if (TextUtils.isEmpty(str)) {
            return -1;
        }
        int i = this.mVpnType;
        if (i != 4) {
            Log.e(TAG, "connectVpn: vpnSdkService is null. please call init first.");
            return -1;
        }
        MiuiVpnUtils.MiuiVpnDetailInfo miuiVpnDetailInfo = getMiuiVpnDetailInfo(i);
        if (miuiVpnDetailInfo == null) {
            return -1;
        }
        try {
            PackageInfo c2 = x.c(this.mContext, str);
            if (c2 == null) {
                String str2 = TAG;
                Log.e(str2, "connectVpn. " + str + " not installed.");
                return -1;
            }
            miuiVpnDetailInfo.addPackage(str);
            synchronized (this.mWatchPackages) {
                this.mWatchPackages.put(Integer.valueOf(c2.applicationInfo.uid), new MiuiVpnUtils.WatchPackageInfo(c2.applicationInfo.uid, c2.packageName, false, 0));
            }
            return connectVpn(c2.applicationInfo.uid);
        } catch (Exception e) {
            Log.e(TAG, "connectVpn. An exception occurred!", e);
            return -1;
        }
    }

    private int convertVpnNameToType(String str) {
        return TextUtils.equals(str, MIUI_VPN_NAME_XUNYOU) ? 4 : 0;
    }

    /* access modifiers changed from: private */
    public int disconnectVpn() {
        String str = TAG;
        Log.i(str, "disconnectVpn. mVpnType=" + this.mVpnType);
        this.mVpnState = 3;
        this.mVpnType = 0;
        unbindVpnSdkService();
        return 0;
    }

    /* access modifiers changed from: private */
    public void forceUpdateCloudData() {
        Log.i(TAG, "forceUpdateCloudData");
        if (h.i()) {
            a.a(new Runnable() {
                public void run() {
                    JSONObject contentJson;
                    if (f.j(MiuiVpnManageService.this) && (contentJson = WebApiAccessHelper.updateMiuiVpnInfos().getContentJson()) != null) {
                        String jSONObject = contentJson.toString();
                        String str = MiuiVpnManageService.TAG;
                        Log.i(str, "forceUpdateCloudData:" + jSONObject);
                        if (!TextUtils.isEmpty(jSONObject)) {
                            MiuiVpnManageService.this.mCommonConfig.setMiuiVpnInfos(jSONObject);
                            MiuiVpnManageService.this.parseMiuiVpnInfos(jSONObject);
                        }
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void getCoupons() {
        IMiuiVpnSdkService iMiuiVpnSdkService = this.mMiuiVpnSdkService;
        if (iMiuiVpnSdkService != null) {
            try {
                iMiuiVpnSdkService.getCoupons();
            } catch (Exception e) {
                Log.e(TAG, "getCoupons: An exception occurred!", e);
            }
        } else {
            Log.e(TAG, "getCoupons: vpnSdkService is null!");
        }
    }

    /* access modifiers changed from: private */
    public MiuiVpnUtils.MiuiVpnDetailInfo getMiuiVpnDetailInfo(int i) {
        MiuiVpnUtils.MiuiVpnDetailInfo miuiVpnDetailInfo = null;
        if (i == 0) {
            return null;
        }
        if (Build.VERSION.SDK_INT <= 19) {
            String str = TAG;
            Log.i(str, "Unsupported VPN. type=" + i + " SDK_INT=" + Build.VERSION.SDK_INT);
            return null;
        }
        synchronized (this.mVpnInfoLock) {
            int i2 = 0;
            while (true) {
                if (i2 >= this.mMiuiVpnDetailInfos.size()) {
                    break;
                }
                MiuiVpnUtils.MiuiVpnDetailInfo miuiVpnDetailInfo2 = this.mMiuiVpnDetailInfos.get(i2);
                if (miuiVpnDetailInfo2.getType() == i) {
                    miuiVpnDetailInfo = miuiVpnDetailInfo2;
                    break;
                }
                i2++;
            }
            while (true) {
            }
        }
        if (miuiVpnDetailInfo == null) {
            String str2 = TAG;
            Log.i(str2, "Unsupported VPN. type=" + i);
        }
        return miuiVpnDetailInfo;
    }

    /* access modifiers changed from: private */
    public String getSetting(String str, String str2) {
        IMiuiVpnSdkService iMiuiVpnSdkService = this.mMiuiVpnSdkService;
        if (iMiuiVpnSdkService != null) {
            try {
                return iMiuiVpnSdkService.getSetting(str, str2);
            } catch (Exception e) {
                Log.e(TAG, "getSetting: An exception occurred!", e);
            }
        } else {
            Log.e(TAG, "getSetting: vpnSdkService is null!");
            return str2;
        }
    }

    /* access modifiers changed from: private */
    public String getSettingEx(String str, String str2, String str3) {
        int convertVpnNameToType = convertVpnNameToType(str);
        if (getMiuiVpnDetailInfo(convertVpnNameToType) == null) {
            return "";
        }
        Cursor query = convertVpnNameToType != 4 ? null : this.mContext.getContentResolver().query(CONTENT_URI_GET_SETTINGS_YUNYOU, (String[]) null, str2, (String[]) null, str3);
        if (query == null) {
            Log.i(TAG, "getSettingEx return null!!");
            return str3;
        }
        if (query != null) {
            try {
                if (query.moveToFirst()) {
                    return query.getString(0);
                }
            } finally {
                query.close();
            }
        }
        query.close();
        return str3;
    }

    /* access modifiers changed from: private */
    public List<String> getSupportApps(String str) {
        int convertVpnNameToType = convertVpnNameToType(str);
        if (convertVpnNameToType != 4) {
            return null;
        }
        synchronized (this.mVpnInfoLock) {
            for (int i = 0; i < this.mMiuiVpnDetailInfos.size(); i++) {
                MiuiVpnUtils.MiuiVpnDetailInfo miuiVpnDetailInfo = this.mMiuiVpnDetailInfos.get(i);
                if (miuiVpnDetailInfo.getType() == convertVpnNameToType) {
                    List<String> packages = miuiVpnDetailInfo.getPackages();
                    return packages;
                }
            }
            return null;
        }
    }

    /* access modifiers changed from: private */
    public String getSupportVpn() {
        synchronized (this.mVpnInfoLock) {
            if (this.mMiuiVpnInfos.size() == 0) {
                return "";
            }
            String str = "";
            for (MiuiVpnInfo next : this.mMiuiVpnInfos.values()) {
                if (TextUtils.isEmpty(str)) {
                    str = next.getName();
                } else {
                    str = str + "," + next.getName();
                }
            }
            return str;
        }
    }

    /* access modifiers changed from: private */
    public boolean getVpnEnabled(String str, String str2) {
        return this.mCommonConfig.getVpnState(str, str2) != 2;
    }

    /* access modifiers changed from: private */
    public int init(int i) {
        MiuiVpnUtils.MiuiVpnDetailInfo miuiVpnDetailInfo;
        if (!checkNetworkPolicy(i) || (miuiVpnDetailInfo = getMiuiVpnDetailInfo(i)) == null) {
            return -1;
        }
        this.mVpnType = i;
        synchronized (this.mVpnSdkServiceLocker) {
            if (this.mMiuiVpnSdkService != null) {
                try {
                    int a2 = this.mMiuiVpnSdkService.a(this.mVpnType, miuiVpnDetailInfo.getPackages());
                    String str = TAG;
                    Log.i(str, "prepareVpn. ret=" + a2);
                    return a2;
                } catch (Exception e) {
                    Log.e(TAG, "init: An exception occurred!", e);
                    return 0;
                }
            } else {
                bindVpnSdkService();
            }
        }
    }

    /* access modifiers changed from: private */
    public int init(String str) {
        return init(convertVpnNameToType(str));
    }

    /* access modifiers changed from: private */
    public void onQueryCouponsResult(int i, List<String> list) {
        synchronized (this.mCallbackList) {
            int beginBroadcast = this.mCallbackList.beginBroadcast();
            while (beginBroadcast > 0) {
                beginBroadcast--;
                try {
                    this.mCallbackList.getBroadcastItem(beginBroadcast).onQueryCouponsResult(i, list);
                } catch (RemoteException e) {
                    Log.i(TAG, "onQueryCouponsResult. an exception occurred!", e);
                }
            }
            this.mCallbackList.finishBroadcast();
        }
    }

    /* access modifiers changed from: private */
    public void onVpnStateChanged(int i, int i2, String str) {
        int i3 = this.mVpnType;
        if (i3 != 0 && i3 == i) {
            this.mVpnState = i2;
        }
        synchronized (this.mCallbackList) {
            int beginBroadcast = this.mCallbackList.beginBroadcast();
            while (beginBroadcast > 0) {
                beginBroadcast--;
                try {
                    this.mCallbackList.getBroadcastItem(beginBroadcast).onVpnStateChanged(i, i2, str);
                } catch (RemoteException e) {
                    Log.i(TAG, "onVpnStateChanged. an exception occurred!", e);
                }
            }
            this.mCallbackList.finishBroadcast();
        }
    }

    /* access modifiers changed from: private */
    public void onWatchPackageDied(final int i, String str) {
        if (this.mVpnType != 0) {
            a.a(new Runnable() {
                public void run() {
                    List<String> packages;
                    MiuiVpnManageService miuiVpnManageService = MiuiVpnManageService.this;
                    MiuiVpnUtils.MiuiVpnDetailInfo access$2000 = miuiVpnManageService.getMiuiVpnDetailInfo(miuiVpnManageService.mVpnType);
                    if (access$2000 != null && (packages = access$2000.getPackages()) != null) {
                        List<String> b2 = x.b(MiuiVpnManageService.this.mContext);
                        synchronized (MiuiVpnManageService.this.mWatchPackages) {
                            MiuiVpnUtils.WatchPackageInfo watchPackageInfo = (MiuiVpnUtils.WatchPackageInfo) MiuiVpnManageService.this.mWatchPackages.get(Integer.valueOf(i));
                            if (watchPackageInfo != null && watchPackageInfo.mIsRunning && !b2.contains(watchPackageInfo.mPackageName)) {
                                String str = MiuiVpnManageService.TAG;
                                Log.i(str, watchPackageInfo.mPackageName + " is Died.");
                                watchPackageInfo.mIsRunning = false;
                            }
                        }
                        for (String contains : packages) {
                            if (b2.contains(contains)) {
                                return;
                            }
                        }
                        int unused = MiuiVpnManageService.this.disconnectVpn();
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void parseMiuiVpnInfos(String str) {
        if (!TextUtils.isEmpty(str)) {
            try {
                JSONObject jSONObject = new JSONObject(str);
                if (jSONObject.has("version")) {
                    int i = jSONObject.getInt("version");
                    if (i == 1 || (i == 2 && jSONObject.optBoolean(DeviceUtil.getMiuiVersionType()))) {
                        parseMiuiVpnInfos(jSONObject);
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "parseMiuiVpnInfos", e);
            }
        }
    }

    private void parseMiuiVpnInfos(JSONObject jSONObject) {
        JSONArray jSONArray;
        JSONArray jSONArray2;
        int i;
        PackageInfo a2;
        PackageInfo a3;
        JSONObject jSONObject2 = jSONObject;
        try {
            if (jSONObject2.has("miuiVpnInfos") && (jSONArray = jSONObject2.getJSONArray("miuiVpnInfos")) != null) {
                HashMap hashMap = new HashMap();
                HashMap hashMap2 = new HashMap();
                ArrayList arrayList = new ArrayList();
                int i2 = 0;
                for (jSONArray = jSONObject2.getJSONArray("miuiVpnInfos"); i2 < jSONArray.length(); jSONArray = jSONArray2) {
                    JSONObject jSONObject3 = jSONArray.getJSONObject(i2);
                    if (jSONObject3 != null && jSONObject3.has("id")) {
                        if (jSONObject3.has(CloudPushConstants.XML_NAME)) {
                            int i3 = jSONObject3.getInt("id");
                            String string = jSONObject3.getString(CloudPushConstants.XML_NAME);
                            if (i3 == 4) {
                                if (MIUI_VPN_NAME_XUNYOU.equals(string)) {
                                    String string2 = jSONObject3.getString("describe");
                                    String string3 = jSONObject3.getString("detailInfoUrl");
                                    int i4 = jSONObject3.getInt("operator");
                                    String string4 = jSONObject3.getString("supportPkgs");
                                    if (!TextUtils.isEmpty(string2)) {
                                        if (!TextUtils.isEmpty(string3)) {
                                            int i5 = jSONObject3.has("minAndroidSdk") ? jSONObject3.getInt("minAndroidSdk") : 0;
                                            String string5 = jSONObject3.has("purchaseNotificationTitle") ? jSONObject3.getString("purchaseNotificationTitle") : null;
                                            String string6 = jSONObject3.has("purchaseNotificationSummary") ? jSONObject3.getString("purchaseNotificationSummary") : null;
                                            boolean z = jSONObject3.has("autoStart") ? jSONObject3.getBoolean("autoStart") : true;
                                            MiuiVpnInfo miuiVpnInfo = r9;
                                            jSONArray2 = jSONArray;
                                            MiuiVpnInfo miuiVpnInfo2 = new MiuiVpnInfo(i3, string, string2, string3, 0);
                                            Log.i(TAG, "parseMiuiVpnInfos. id=" + i3 + " name=" + string);
                                            String[] split = TextUtils.split(string4, ",");
                                            i = i2;
                                            MiuiVpnUtils.MiuiVpnDetailInfo miuiVpnDetailInfo = r9;
                                            MiuiVpnUtils.MiuiVpnDetailInfo miuiVpnDetailInfo2 = new MiuiVpnUtils.MiuiVpnDetailInfo(i3, i4, Arrays.asList(split), i5, z, string5, string6);
                                            hashMap2.put(Integer.valueOf(i3), miuiVpnInfo);
                                            arrayList.add(miuiVpnDetailInfo);
                                            if (z) {
                                                int a4 = com.miui.support.provider.f.a(this.mContext.getContentResolver(), "second_user_id", (int) UserHandle.USER_NULL);
                                                boolean a5 = c.a(this.mContext.getContentResolver(), "xspace_enabled", false);
                                                for (String str : split) {
                                                    PackageInfo c2 = x.c(this.mContext, str);
                                                    if (c2 != null) {
                                                        hashMap.put(Integer.valueOf(c2.applicationInfo.uid), new MiuiVpnUtils.WatchPackageInfo(c2.applicationInfo.uid, c2.packageName, false, 0));
                                                    }
                                                    if (!(a4 == -10000 || (a3 = b.b.o.b.a.a.a(str, 0, a4)) == null)) {
                                                        hashMap.put(Integer.valueOf(a3.applicationInfo.uid), new MiuiVpnUtils.WatchPackageInfo(a3.applicationInfo.uid, a3.packageName, false, 0));
                                                    }
                                                    if (a5 && (a2 = b.b.o.b.a.a.a(str, 0, 999)) != null) {
                                                        hashMap.put(Integer.valueOf(a2.applicationInfo.uid), new MiuiVpnUtils.WatchPackageInfo(a2.applicationInfo.uid, a2.packageName, false, 0));
                                                    }
                                                }
                                            }
                                            i2 = i + 1;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    jSONArray2 = jSONArray;
                    i = i2;
                    i2 = i + 1;
                }
                synchronized (this.mVpnInfoLock) {
                    this.mMiuiVpnInfos.putAll(hashMap2);
                    this.mMiuiVpnDetailInfos.addAll(arrayList);
                }
                synchronized (this.mWatchPackages) {
                    this.mWatchPackages.putAll(hashMap);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "parseMiuiVpnInfos", e);
        }
    }

    /* access modifiers changed from: private */
    public void registerCallback(IMiuiVpnManageServiceCallback iMiuiVpnManageServiceCallback) {
        Log.i(TAG, "registerCallback");
        if (iMiuiVpnManageServiceCallback != null) {
            synchronized (this.mCallbackList) {
                this.mCallbackList.register(iMiuiVpnManageServiceCallback);
            }
        }
    }

    private void registerCloudDataObserver() {
        getContentResolver().registerContentObserver(MiuiSettingsCompat$SettingsCloudData.a(), true, this.mCloudDataObserver);
    }

    private void registerNetworkConnectivityReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(this.mNetworkConnectivityReceiver, intentFilter);
    }

    private void registerProcessObserver() {
        ActivityManagerCompat.registerProcessObserver(this.mProcessObserver);
    }

    private void registerScNetworkStatusReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("action_update_sc_network_allow");
        registerReceiver(this.mScNetworkStatusReceiver, intentFilter);
    }

    /* access modifiers changed from: private */
    public void restartService() {
        Log.i(TAG, "restartService");
        a.a(new Runnable() {
            public void run() {
                List<String> packages;
                if (MiuiVpnManageService.this.mVpnType != 0) {
                    boolean z = true;
                    if (MiuiVpnManageService.this.mVpnState == 1) {
                        MiuiVpnManageService miuiVpnManageService = MiuiVpnManageService.this;
                        MiuiVpnUtils.MiuiVpnDetailInfo access$2000 = miuiVpnManageService.getMiuiVpnDetailInfo(miuiVpnManageService.mVpnType);
                        if (access$2000 != null && (packages = access$2000.getPackages()) != null) {
                            List<String> b2 = x.b(MiuiVpnManageService.this.mContext);
                            Iterator<String> it = packages.iterator();
                            while (true) {
                                if (it.hasNext()) {
                                    if (b2.contains(it.next())) {
                                        break;
                                    }
                                } else {
                                    z = false;
                                    break;
                                }
                            }
                            MiuiVpnManageService miuiVpnManageService2 = MiuiVpnManageService.this;
                            int unused = miuiVpnManageService2.init(miuiVpnManageService2.mVpnType);
                            if (z) {
                                MiuiVpnManageService miuiVpnManageService3 = MiuiVpnManageService.this;
                                int unused2 = miuiVpnManageService3.connectVpn(miuiVpnManageService3.mVpnType);
                            }
                        }
                    }
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public boolean setSetting(String str, String str2) {
        IMiuiVpnSdkService iMiuiVpnSdkService = this.mMiuiVpnSdkService;
        if (iMiuiVpnSdkService != null) {
            try {
                return iMiuiVpnSdkService.setSetting(str, str2);
            } catch (Exception e) {
                Log.e(TAG, "setSetting: An exception occurred!", e);
                return false;
            }
        } else {
            Log.e(TAG, "setSetting: vpnSdkService is null!");
            return false;
        }
    }

    /* access modifiers changed from: private */
    public boolean setSettingEx(String str, String str2, String str3) {
        int convertVpnNameToType = convertVpnNameToType(str);
        if (getMiuiVpnDetailInfo(convertVpnNameToType) == null || convertVpnNameToType != 4) {
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(str2, str3);
        return this.mContext.getContentResolver().update(CONTENT_URI_GET_SETTINGS_YUNYOU, contentValues, (String) null, (String[]) null) == 0;
    }

    /* access modifiers changed from: private */
    public int setVpnEnabled(String str, String str2, final boolean z) {
        this.mCommonConfig.setVpnState(str, str2, z ? 1 : 2);
        final int convertVpnNameToType = convertVpnNameToType(str);
        a.a(new Runnable() {
            public void run() {
                MiuiVpnUtils.MiuiVpnDetailInfo access$2000;
                List<String> packages;
                if (!z && MiuiVpnManageService.this.mVpnType != 0) {
                    int unused = MiuiVpnManageService.this.disconnectVpn();
                } else if (z) {
                    if (!(MiuiVpnManageService.this.mVpnType == 0 || MiuiVpnManageService.this.mVpnType == convertVpnNameToType)) {
                        int unused2 = MiuiVpnManageService.this.disconnectVpn();
                    }
                    if (MiuiVpnManageService.this.mVpnType == 0 && (access$2000 = MiuiVpnManageService.this.getMiuiVpnDetailInfo(convertVpnNameToType)) != null && (packages = access$2000.getPackages()) != null) {
                        boolean z = false;
                        List<String> b2 = x.b(MiuiVpnManageService.this.mContext);
                        Iterator<String> it = packages.iterator();
                        while (true) {
                            if (it.hasNext()) {
                                if (b2.contains(it.next())) {
                                    z = true;
                                    break;
                                }
                            } else {
                                break;
                            }
                        }
                        if (z) {
                            int unused3 = MiuiVpnManageService.this.connectVpn(convertVpnNameToType);
                        }
                    }
                }
            }
        });
        return 0;
    }

    private void unRegisterNetworkConnectivityReceiver() {
        unregisterReceiver(this.mNetworkConnectivityReceiver);
    }

    private void unRegisterProcessObserver() {
        ActivityManagerCompat.unRegisterProcessObserver(this.mProcessObserver);
    }

    private void unRegisterScNetworkStatusReceiver() {
        unregisterReceiver(this.mScNetworkStatusReceiver);
    }

    /* access modifiers changed from: private */
    public void unbindVpnSdkService() {
        if (this.mSuportVpn) {
            synchronized (this.mVpnSdkServiceLocker) {
                try {
                    if (this.mMiuiVpnSdkService != null) {
                        try {
                            this.mMiuiVpnSdkService.registerCallback((IMiuiVpnManageServiceCallback) null);
                            this.mMiuiVpnSdkService.o();
                        } catch (Exception e) {
                            Log.e(TAG, "unbindVpnSdkService", e);
                        }
                        this.mContext.unbindService(this.mMiuiVpnSdkServiceConnection);
                    }
                    this.mVpnProcPid = 0;
                    this.mPendingDestAppInfo = null;
                } catch (Exception e2) {
                    try {
                        Log.e(TAG, "unbindVpnSdkService", e2);
                        this.mVpnProcPid = 0;
                        this.mPendingDestAppInfo = null;
                    } catch (Throwable th) {
                        this.mVpnProcPid = 0;
                        this.mPendingDestAppInfo = null;
                        this.mMiuiVpnSdkService = null;
                        throw th;
                    }
                }
                this.mMiuiVpnSdkService = null;
            }
        }
    }

    /* access modifiers changed from: private */
    public void unregisterCallback(IMiuiVpnManageServiceCallback iMiuiVpnManageServiceCallback) {
        if (iMiuiVpnManageServiceCallback != null) {
            synchronized (this.mCallbackList) {
                this.mCallbackList.unregister(iMiuiVpnManageServiceCallback);
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateMiuiVpnInfos(final boolean z) {
        Log.i(TAG, "updateMiuiVpnInfos");
        a.a(new Runnable() {
            public void run() {
                String str = null;
                try {
                    if (z) {
                        str = MiuiVpnManageService.this.mCommonConfig.getMiuiVpnInfos();
                    }
                    if (!z || TextUtils.isEmpty(str)) {
                        if (!z) {
                            MiuiVpnManageService.this.mCommonConfig.setMiuiVpnInfos("");
                        }
                        List<MiuiSettingsCompat$SettingsCloudData.CloudData> a2 = MiuiSettingsCompat$SettingsCloudData.a(MiuiVpnManageService.this.mContext.getContentResolver(), "miui_vpn_infos");
                        if (a2 != null) {
                            if (a2.size() != 0) {
                                Iterator<MiuiSettingsCompat$SettingsCloudData.CloudData> it = a2.iterator();
                                while (true) {
                                    if (!it.hasNext()) {
                                        break;
                                    }
                                    str = it.next().toString();
                                    if (!TextUtils.isEmpty(str)) {
                                        MiuiVpnManageService.this.mCommonConfig.setMiuiVpnInfos(str);
                                        break;
                                    }
                                }
                            }
                        }
                        Log.i(MiuiVpnManageService.TAG, "updateMiuiVpnInfos, cloudDatalist is null");
                        return;
                    }
                    synchronized (MiuiVpnManageService.this.mVpnInfoLock) {
                        MiuiVpnManageService.this.mMiuiVpnInfos.clear();
                        MiuiVpnManageService.this.mMiuiVpnDetailInfos.clear();
                    }
                    synchronized (MiuiVpnManageService.this.mWatchPackages) {
                        MiuiVpnManageService.this.mWatchPackages.clear();
                    }
                    String str2 = MiuiVpnManageService.TAG;
                    Log.i(str2, "updateMiuiVpnInfos. miuiVpnInfos=" + str);
                    MiuiVpnManageService.this.parseMiuiVpnInfos(str);
                } catch (Exception e) {
                    Log.e(MiuiVpnManageService.TAG, "updateMiuiVpnInfos", e);
                }
            }
        });
    }

    public IBinder onBind(Intent intent) {
        if (!this.mSuportVpn) {
            return null;
        }
        return this.mMiuiVpnManageServiceBinder.asBinder();
    }

    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
        this.mContext = this;
        this.mCommonConfig = CommonConfig.getInstance(this.mContext);
        this.mMiuiVpnManageServiceBinder = new MiuiVpnManageServiceBinder();
        if (this.mSuportVpn) {
            registerProcessObserver();
            this.mPackageMonitor.register(this.mContext, (Looper) null, B.d(), false);
            updateMiuiVpnInfos(true);
            registerCloudDataObserver();
        }
        registerNetworkConnectivityReceiver();
        registerScNetworkStatusReceiver();
    }

    public void onDestroy() {
        super.onDestroy();
        try {
            unbindVpnSdkService();
            unRegisterNetworkConnectivityReceiver();
            unRegisterScNetworkStatusReceiver();
            if (this.mSuportVpn) {
                unRegisterProcessObserver();
                this.mPackageMonitor.unregister();
            }
        } catch (Exception e) {
            Log.i(TAG, "destroy error", e);
        }
    }

    public int refreshUserState() {
        IMiuiVpnSdkService iMiuiVpnSdkService = this.mMiuiVpnSdkService;
        if (iMiuiVpnSdkService != null) {
            try {
                return iMiuiVpnSdkService.refreshUserState();
            } catch (Exception e) {
                Log.e(TAG, "refreshUserState: An exception occurred!", e);
                return 0;
            }
        } else {
            Log.e(TAG, "refreshUserState: vpnSdkService is null. please call init first.");
            return 0;
        }
    }
}
