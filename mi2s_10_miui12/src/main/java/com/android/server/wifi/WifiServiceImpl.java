package com.android.server.wifi;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.admin.DevicePolicyManagerInternal;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.res.MiuiConfiguration;
import android.database.ContentObserver;
import android.net.DhcpInfo;
import android.net.DhcpResults;
import android.net.Network;
import android.net.NetworkUtils;
import android.net.Uri;
import android.net.wifi.IDppCallback;
import android.net.wifi.INetworkRequestMatchCallback;
import android.net.wifi.IOnWifiUsabilityStatsListener;
import android.net.wifi.ISoftApCallback;
import android.net.wifi.ITrafficStateCallback;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiActivityEnergyInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiDppConfig;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSuggestion;
import android.net.wifi.WifiSsid;
import android.net.wifi.hotspot2.IProvisioningCallback;
import android.net.wifi.hotspot2.OsuProvider;
import android.net.wifi.hotspot2.PasspointConfiguration;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.WorkSource;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.MutableInt;
import android.util.Slog;
import android.view.MiuiWindowManager;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.PowerProfile;
import com.android.internal.util.AsyncChannel;
import com.android.server.wifi.LocalOnlyHotspotRequestInfo;
import com.android.server.wifi.WifiServiceImpl;
import com.android.server.wifi.hotspot2.PasspointProvider;
import com.android.server.wifi.util.ExternalCallbackTracker;
import com.android.server.wifi.util.GeneralUtil;
import com.android.server.wifi.util.WifiHandler;
import com.android.server.wifi.util.WifiPermissionsUtil;
import java.io.BufferedReader;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import miui.telephony.phonenumber.Prefix;

public class WifiServiceImpl extends BaseWifiService {
    private static final int BACKGROUND_IMPORTANCE_CUTOFF = 125;
    private static final long DEFAULT_SCAN_BACKGROUND_THROTTLE_INTERVAL_MS = 1800000;
    private static final int RUN_WITH_SCISSORS_TIMEOUT_MILLIS = 4000;
    private static final String TAG = "WifiService";
    private static final boolean VDBG = false;
    final ActiveModeWarden mActiveModeWarden;
    private final ActivityManager mActivityManager;
    private final AppOpsManager mAppOps;
    private AsyncChannelExternalClientHandler mAsyncChannelExternalClientHandler;
    final ClientModeImpl mClientModeImpl;
    @VisibleForTesting
    AsyncChannel mClientModeImplChannel;
    ClientModeImplHandler mClientModeImplHandler;
    private final Clock mClock;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public final WifiCountryCode mCountryCode;
    private final DppManager mDppManager;
    private final FrameworkFacade mFacade;
    /* access modifiers changed from: private */
    public final FrameworkFacade mFrameworkFacade;
    @GuardedBy({"mLocalOnlyHotspotRequests"})
    private final ConcurrentHashMap<String, Integer> mIfaceIpModes;
    boolean mInIdleMode;
    private boolean mIsControllerStarted = false;
    @GuardedBy({"mLocalOnlyHotspotRequests"})
    private WifiConfiguration mLocalOnlyHotspotConfig = null;
    @GuardedBy({"mLocalOnlyHotspotRequests"})
    private final HashMap<Integer, LocalOnlyHotspotRequestInfo> mLocalOnlyHotspotRequests;
    private WifiLog mLog;
    private final PowerManager mPowerManager;
    PowerProfile mPowerProfile;
    private int mPrevApBand = 0;
    /* access modifiers changed from: private */
    public int mQCSoftApNumClients = 0;
    private final IntentFilter mQcIntentFilter;
    private final BroadcastReceiver mQcReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.net.wifi.supplicant.STATE_CHANGE".equals(action)) {
                SupplicantState state = (SupplicantState) intent.getParcelableExtra("newState");
                if (WifiServiceImpl.this.isCurrentStaShareThisAp() && state == SupplicantState.COMPLETED) {
                    WifiServiceImpl.this.restartSoftApIfNeeded();
                } else if (WifiServiceImpl.this.mSoftApExtendingWifi && state == SupplicantState.DISCONNECTED) {
                    WifiServiceImpl.this.restartSoftApIfNeeded();
                }
            } else if ("android.net.wifi.WIFI_STATE_CHANGED".equals(action)) {
                int state2 = intent.getIntExtra("wifi_state", 4);
                if (WifiServiceImpl.this.mSoftApExtendingWifi && state2 == 1) {
                    WifiServiceImpl.this.restartSoftApIfNeeded();
                }
            }
        }
    };
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.intent.action.USER_REMOVED")) {
                WifiServiceImpl.this.mClientModeImpl.removeUserConfigs(intent.getIntExtra("android.intent.extra.user_handle", 0));
            } else if (action.equals("android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED")) {
                WifiServiceImpl.this.mClientModeImpl.sendBluetoothAdapterStateChange(intent.getIntExtra("android.bluetooth.adapter.extra.CONNECTION_STATE", 0));
            } else if (action.equals("android.intent.action.EMERGENCY_CALLBACK_MODE_CHANGED")) {
                WifiServiceImpl.this.mWifiController.sendMessage(155649, intent.getBooleanExtra("phoneinECMState", false), 0);
            } else if (action.equals("android.intent.action.EMERGENCY_CALL_STATE_CHANGED")) {
                WifiServiceImpl.this.mWifiController.sendMessage(155662, intent.getBooleanExtra("phoneInEmergencyCall", false), 0);
            } else if (action.equals("android.os.action.DEVICE_IDLE_MODE_CHANGED")) {
                WifiServiceImpl.this.handleIdleModeChanged();
            }
        }
    };
    /* access modifiers changed from: private */
    public final ExternalCallbackTracker<ISoftApCallback> mRegisteredSoftApCallbacks;
    private boolean mRestartWifiApIfRequired = false;
    boolean mScanPending;
    final ScanRequestProxy mScanRequestProxy;
    final WifiSettingsStore mSettingsStore;
    /* access modifiers changed from: private */
    public boolean mSoftApExtendingWifi = false;
    /* access modifiers changed from: private */
    public int mSoftApNumClients = 0;
    /* access modifiers changed from: private */
    public int mSoftApState = 11;
    private final UserManager mUserManager;
    private boolean mVerboseLoggingEnabled = false;
    private WifiApConfigStore mWifiApConfigStore;
    private int mWifiApState = 11;
    private final WifiBackupRestore mWifiBackupRestore;
    /* access modifiers changed from: private */
    public WifiController mWifiController;
    /* access modifiers changed from: private */
    public final WifiInjector mWifiInjector;
    private final WifiLockManager mWifiLockManager;
    private final WifiMetrics mWifiMetrics;
    private final WifiMulticastLockManager mWifiMulticastLockManager;
    /* access modifiers changed from: private */
    public final WifiNetworkSuggestionsManager mWifiNetworkSuggestionsManager;
    /* access modifiers changed from: private */
    public WifiPermissionsUtil mWifiPermissionsUtil;
    private WifiTrafficPoller mWifiTrafficPoller;
    private int scanRequestCounter = 0;

    public final class LocalOnlyRequestorCallback implements LocalOnlyHotspotRequestInfo.RequestingApplicationDeathCallback {
        public LocalOnlyRequestorCallback() {
        }

        public void onLocalOnlyHotspotRequestorDeath(LocalOnlyHotspotRequestInfo requestor) {
            WifiServiceImpl.this.unregisterCallingAppAndStopLocalOnlyHotspot(requestor);
        }
    }

    private class AsyncChannelExternalClientHandler extends WifiHandler {
        AsyncChannelExternalClientHandler(String tag, Looper looper) {
            super(tag, looper);
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 69633:
                    WifiServiceImpl.this.mFrameworkFacade.makeWifiAsyncChannel(WifiServiceImpl.TAG).connect(WifiServiceImpl.this.mContext, this, msg.replyTo);
                    return;
                case 151553:
                    if (checkPrivilegedPermissionsAndReplyIfNotAuthorized(msg, 151554)) {
                        WifiConfiguration config = (WifiConfiguration) msg.obj;
                        int networkId = msg.arg1;
                        Slog.d(WifiServiceImpl.TAG, "CONNECT  nid=" + Integer.toString(networkId) + " config=" + config + " uid=" + msg.sendingUid + " name=" + WifiServiceImpl.this.mContext.getPackageManager().getNameForUid(msg.sendingUid));
                        if (config != null) {
                            WifiServiceImpl.this.mClientModeImpl.sendMessage(Message.obtain(msg));
                            return;
                        } else if (config != null || networkId == -1) {
                            Slog.e(WifiServiceImpl.TAG, "AsyncChannelExternalClientHandler.handleMessage ignoring invalid msg=" + msg);
                            replyFailed(msg, 151554, 8);
                            return;
                        } else {
                            WifiServiceImpl.this.mClientModeImpl.sendMessage(Message.obtain(msg));
                            return;
                        }
                    } else {
                        return;
                    }
                case 151556:
                    if (checkPrivilegedPermissionsAndReplyIfNotAuthorized(msg, 151557)) {
                        WifiServiceImpl.this.mClientModeImpl.sendMessage(Message.obtain(msg));
                        return;
                    }
                    return;
                case 151559:
                    if (checkPrivilegedPermissionsAndReplyIfNotAuthorized(msg, 151560)) {
                        WifiConfiguration config2 = (WifiConfiguration) msg.obj;
                        int networkId2 = msg.arg1;
                        Slog.d(WifiServiceImpl.TAG, "SAVE nid=" + Integer.toString(networkId2) + " config=" + config2 + " uid=" + msg.sendingUid + " name=" + WifiServiceImpl.this.mContext.getPackageManager().getNameForUid(msg.sendingUid));
                        if (config2 != null) {
                            WifiServiceImpl.this.mClientModeImpl.sendMessage(Message.obtain(msg));
                            return;
                        }
                        Slog.e(WifiServiceImpl.TAG, "AsyncChannelExternalClientHandler.handleMessage ignoring invalid msg=" + msg);
                        replyFailed(msg, 151560, 8);
                        return;
                    }
                    return;
                case 151569:
                    if (checkPrivilegedPermissionsAndReplyIfNotAuthorized(msg, 151570)) {
                        WifiServiceImpl.this.mClientModeImpl.sendMessage(Message.obtain(msg));
                        return;
                    }
                    return;
                case 151572:
                    if (checkChangePermissionAndReplyIfNotAuthorized(msg, 151574)) {
                        WifiServiceImpl.this.mClientModeImpl.sendMessage(Message.obtain(msg));
                        return;
                    }
                    return;
                default:
                    if (!MiuiWifiService.handleClientMessage(msg, WifiServiceImpl.this.mClientModeImpl)) {
                        Slog.d(WifiServiceImpl.TAG, "AsyncChannelExternalClientHandler.handleMessage ignoring msg=" + msg);
                        return;
                    }
                    return;
            }
        }

        private boolean checkChangePermissionAndReplyIfNotAuthorized(Message msg, int replyWhat) {
            if (WifiServiceImpl.this.mWifiPermissionsUtil.checkChangePermission(msg.sendingUid)) {
                return true;
            }
            Slog.e(WifiServiceImpl.TAG, "AsyncChannelExternalClientHandler.handleMessage ignoring unauthorized msg=" + msg);
            replyFailed(msg, replyWhat, 9);
            return false;
        }

        private boolean checkPrivilegedPermissionsAndReplyIfNotAuthorized(Message msg, int replyWhat) {
            if (WifiServiceImpl.this.isPrivileged(-1, msg.sendingUid)) {
                return true;
            }
            Slog.e(WifiServiceImpl.TAG, "ClientHandler.handleMessage ignoring unauthorized msg=" + msg);
            replyFailed(msg, replyWhat, 9);
            return false;
        }

        private void replyFailed(Message msg, int what, int why) {
            if (msg.replyTo != null) {
                Message reply = Message.obtain();
                reply.what = what;
                reply.arg1 = why;
                try {
                    msg.replyTo.send(reply);
                } catch (RemoteException e) {
                }
            }
        }
    }

    private class ClientModeImplHandler extends WifiHandler {
        private AsyncChannel mCmiChannel;

        ClientModeImplHandler(String tag, Looper looper, AsyncChannel asyncChannel) {
            super(tag, looper);
            this.mCmiChannel = asyncChannel;
            this.mCmiChannel.connect(WifiServiceImpl.this.mContext, this, WifiServiceImpl.this.mClientModeImpl.getHandler());
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if (i != 69632) {
                if (i != 69636) {
                    Slog.d(WifiServiceImpl.TAG, "ClientModeImplHandler.handleMessage ignoring msg=" + msg);
                    return;
                }
                Slog.e(WifiServiceImpl.TAG, "ClientModeImpl channel lost, msg.arg1 =" + msg.arg1);
                WifiServiceImpl wifiServiceImpl = WifiServiceImpl.this;
                wifiServiceImpl.mClientModeImplChannel = null;
                this.mCmiChannel.connect(wifiServiceImpl.mContext, this, WifiServiceImpl.this.mClientModeImpl.getHandler());
            } else if (msg.arg1 == 0) {
                WifiServiceImpl.this.mClientModeImplChannel = this.mCmiChannel;
            } else {
                Slog.e(WifiServiceImpl.TAG, "ClientModeImpl connection failure, error=" + msg.arg1);
                WifiServiceImpl.this.mClientModeImplChannel = null;
            }
        }
    }

    /* access modifiers changed from: private */
    public void restartSoftApIfNeeded() {
        if (getWifiApEnabledState() == 11) {
            Slog.d(TAG, "Repeater mode: not restarting SoftAP as Hotspot is disabled.");
            return;
        }
        Slog.d(TAG, "Repeater mode: Stop SoftAP.");
        this.mRestartWifiApIfRequired = true;
        stopSoftAp();
    }

    public WifiServiceImpl(Context context, WifiInjector wifiInjector, AsyncChannel asyncChannel) {
        this.mContext = context;
        this.mWifiInjector = wifiInjector;
        this.mClock = wifiInjector.getClock();
        this.mFacade = this.mWifiInjector.getFrameworkFacade();
        this.mWifiMetrics = this.mWifiInjector.getWifiMetrics();
        this.mWifiTrafficPoller = this.mWifiInjector.getWifiTrafficPoller();
        this.mUserManager = this.mWifiInjector.getUserManager();
        this.mCountryCode = this.mWifiInjector.getWifiCountryCode();
        this.mClientModeImpl = this.mWifiInjector.getClientModeImpl();
        this.mActiveModeWarden = this.mWifiInjector.getActiveModeWarden();
        this.mClientModeImpl.setTrafficPoller(this.mWifiTrafficPoller);
        this.mClientModeImpl.enableRssiPolling(true);
        this.mScanRequestProxy = this.mWifiInjector.getScanRequestProxy();
        this.mSettingsStore = this.mWifiInjector.getWifiSettingsStore();
        this.mPowerManager = (PowerManager) this.mContext.getSystemService(PowerManager.class);
        this.mAppOps = (AppOpsManager) this.mContext.getSystemService("appops");
        this.mActivityManager = (ActivityManager) this.mContext.getSystemService("activity");
        this.mWifiLockManager = this.mWifiInjector.getWifiLockManager();
        this.mWifiMulticastLockManager = this.mWifiInjector.getWifiMulticastLockManager();
        HandlerThread wifiServiceHandlerThread = this.mWifiInjector.getWifiServiceHandlerThread();
        this.mAsyncChannelExternalClientHandler = new AsyncChannelExternalClientHandler(TAG, wifiServiceHandlerThread.getLooper());
        this.mClientModeImplHandler = new ClientModeImplHandler(TAG, wifiServiceHandlerThread.getLooper(), asyncChannel);
        this.mWifiController = this.mWifiInjector.getWifiController();
        this.mWifiBackupRestore = this.mWifiInjector.getWifiBackupRestore();
        this.mWifiApConfigStore = this.mWifiInjector.getWifiApConfigStore();
        this.mWifiPermissionsUtil = this.mWifiInjector.getWifiPermissionsUtil();
        this.mLog = this.mWifiInjector.makeLog(TAG);
        this.mFrameworkFacade = wifiInjector.getFrameworkFacade();
        this.mIfaceIpModes = new ConcurrentHashMap<>();
        this.mLocalOnlyHotspotRequests = new HashMap<>();
        enableVerboseLoggingInternal(getVerboseLoggingLevel());
        this.mRegisteredSoftApCallbacks = new ExternalCallbackTracker<>(this.mClientModeImplHandler);
        this.mWifiInjector.getActiveModeWarden().registerSoftApCallback(new SoftApCallbackImpl());
        this.mPowerProfile = this.mWifiInjector.getPowerProfile();
        this.mWifiNetworkSuggestionsManager = this.mWifiInjector.getWifiNetworkSuggestionsManager();
        this.mDppManager = this.mWifiInjector.getDppManager();
        this.mQcIntentFilter = new IntentFilter("android.net.wifi.supplicant.STATE_CHANGE");
        this.mQcIntentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        this.mContext.registerReceiver(this.mQcReceiver, this.mQcIntentFilter);
        MiuiWifiService.make(context);
    }

    @VisibleForTesting
    public void setWifiHandlerLogForTest(WifiLog log) {
        this.mAsyncChannelExternalClientHandler.setWifiLog(log);
    }

    public void checkAndStartWifi() {
        if (this.mFrameworkFacade.inStorageManagerCryptKeeperBounce()) {
            Log.d(TAG, "Device still encrypted. Need to restart SystemServer.  Do not start wifi.");
            return;
        }
        boolean wifiEnabled = this.mSettingsStore.isWifiToggleEnabled();
        StringBuilder sb = new StringBuilder();
        sb.append("WifiService starting up with Wi-Fi ");
        sb.append(wifiEnabled ? "enabled" : "disabled");
        Slog.i(TAG, sb.toString());
        registerForScanModeChange();
        this.mContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (WifiServiceImpl.this.mSettingsStore.handleAirplaneModeToggled()) {
                    WifiServiceImpl.this.mWifiController.sendMessage(155657);
                }
                if (WifiServiceImpl.this.mSettingsStore.isAirplaneModeOn()) {
                    Log.d(WifiServiceImpl.TAG, "resetting country code because Airplane mode is ON");
                    WifiServiceImpl.this.mCountryCode.airplaneModeEnabled();
                }
            }
        }, new IntentFilter("android.intent.action.AIRPLANE_MODE"));
        this.mContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String state = intent.getStringExtra("ss");
                if ("ABSENT".equals(state)) {
                    Log.d(WifiServiceImpl.TAG, "resetting networks because SIM was removed");
                    WifiServiceImpl.this.mClientModeImpl.resetSimAuthNetworks(false);
                } else if ("LOADED".equals(state)) {
                    Log.d(WifiServiceImpl.TAG, "resetting networks because SIM was loaded");
                    WifiServiceImpl.this.mClientModeImpl.resetSimAuthNetworks(true);
                }
            }
        }, new IntentFilter("android.intent.action.SIM_STATE_CHANGED"));
        this.mContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                WifiServiceImpl.this.handleWifiApStateChange(intent.getIntExtra("wifi_state", 11), intent.getIntExtra("previous_wifi_state", 11), intent.getIntExtra("wifi_ap_error_code", -1), intent.getStringExtra("wifi_ap_interface_name"), intent.getIntExtra("wifi_ap_mode", -1));
            }
        }, new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED"));
        registerForBroadcasts();
        this.mInIdleMode = this.mPowerManager.isDeviceIdleMode();
        Log.d(TAG, "qcdbg= mWifiStateMachine.syncInitialize()");
        if (!this.mClientModeImpl.syncInitialize(this.mClientModeImplChannel)) {
            Log.wtf(TAG, "Failed to initialize ClientModeImpl");
        }
        this.mWifiController.start();
        this.mIsControllerStarted = true;
        MiuiWifiService.get().systemReady();
        if (wifiEnabled) {
            setWifiEnabled(this.mContext.getPackageName(), wifiEnabled);
        }
    }

    public void handleBootCompleted() {
        Log.d(TAG, "Handle boot completed");
        this.mClientModeImpl.handleBootCompleted();
    }

    public void handleUserSwitch(int userId) {
        Log.d(TAG, "Handle user switch " + userId);
        this.mClientModeImpl.handleUserSwitch(userId);
    }

    public void handleUserUnlock(int userId) {
        Log.d(TAG, "Handle user unlock " + userId);
        this.mClientModeImpl.handleUserUnlock(userId);
    }

    public void handleUserStop(int userId) {
        Log.d(TAG, "Handle user stop " + userId);
        this.mClientModeImpl.handleUserStop(userId);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:?, code lost:
        r10.mWifiPermissionsUtil.enforceCanAccessScanResults(r11, r0);
        r4 = new com.android.server.wifi.util.GeneralUtil.Mutable<>();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0049, code lost:
        if (r10.mWifiInjector.getClientModeImplHandler().runWithScissors(new com.android.server.wifi.$$Lambda$WifiServiceImpl$71KWGZ9o3U1lf_2vP7tmY9cz4qQ(r10, r4, r0, r11), 4000) != false) goto L_0x005a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x004b, code lost:
        android.util.Log.e(TAG, "Failed to post runnable to start scan");
        sendFailedScanBroadcast();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0059, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0062, code lost:
        if (((java.lang.Boolean) r4.value).booleanValue() != false) goto L_0x0070;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0064, code lost:
        android.util.Log.e(TAG, "Failed to start scan");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x006b, code lost:
        android.os.Binder.restoreCallingIdentity(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x006f, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0070, code lost:
        android.os.Binder.restoreCallingIdentity(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0074, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0075, code lost:
        r1 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0077, code lost:
        r4 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:?, code lost:
        android.util.Slog.e(TAG, "Permission violation - startScan not allowed for uid=" + r0 + ", packageName=" + r11 + ", reason=" + r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00a2, code lost:
        return false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00a3, code lost:
        android.os.Binder.restoreCallingIdentity(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00a6, code lost:
        throw r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean startScan(java.lang.String r11) {
        /*
            r10 = this;
            int r0 = r10.enforceChangePermission(r11)
            r1 = 0
            if (r0 == 0) goto L_0x0008
            return r1
        L_0x0008:
            int r0 = android.os.Binder.getCallingUid()
            long r2 = android.os.Binder.clearCallingIdentity()
            com.android.server.wifi.WifiLog r4 = r10.mLog
            java.lang.String r5 = "startScan uid=%"
            com.android.server.wifi.WifiLog$LogMessage r4 = r4.info(r5)
            long r5 = (long) r0
            com.android.server.wifi.WifiLog$LogMessage r4 = r4.c((long) r5)
            r4.flush()
            monitor-enter(r10)
            boolean r4 = r10.mInIdleMode     // Catch:{ all -> 0x00a7 }
            r5 = 1
            if (r4 == 0) goto L_0x002d
            r10.sendFailedScanBroadcast()     // Catch:{ all -> 0x00a7 }
            r10.mScanPending = r5     // Catch:{ all -> 0x00a7 }
            monitor-exit(r10)     // Catch:{ all -> 0x00a7 }
            return r1
        L_0x002d:
            monitor-exit(r10)     // Catch:{ all -> 0x00a7 }
            com.android.server.wifi.util.WifiPermissionsUtil r4 = r10.mWifiPermissionsUtil     // Catch:{ SecurityException -> 0x0077 }
            r4.enforceCanAccessScanResults(r11, r0)     // Catch:{ SecurityException -> 0x0077 }
            com.android.server.wifi.util.GeneralUtil$Mutable r4 = new com.android.server.wifi.util.GeneralUtil$Mutable     // Catch:{ SecurityException -> 0x0077 }
            r4.<init>()     // Catch:{ SecurityException -> 0x0077 }
            com.android.server.wifi.WifiInjector r6 = r10.mWifiInjector     // Catch:{ SecurityException -> 0x0077 }
            android.os.Handler r6 = r6.getClientModeImplHandler()     // Catch:{ SecurityException -> 0x0077 }
            com.android.server.wifi.-$$Lambda$WifiServiceImpl$71KWGZ9o3U1lf_2vP7tmY9cz4qQ r7 = new com.android.server.wifi.-$$Lambda$WifiServiceImpl$71KWGZ9o3U1lf_2vP7tmY9cz4qQ     // Catch:{ SecurityException -> 0x0077 }
            r7.<init>(r4, r0, r11)     // Catch:{ SecurityException -> 0x0077 }
            r8 = 4000(0xfa0, double:1.9763E-320)
            boolean r6 = r6.runWithScissors(r7, r8)     // Catch:{ SecurityException -> 0x0077 }
            if (r6 != 0) goto L_0x005a
            java.lang.String r5 = "WifiService"
            java.lang.String r7 = "Failed to post runnable to start scan"
            android.util.Log.e(r5, r7)     // Catch:{ SecurityException -> 0x0077 }
            r10.sendFailedScanBroadcast()     // Catch:{ SecurityException -> 0x0077 }
            android.os.Binder.restoreCallingIdentity(r2)
            return r1
        L_0x005a:
            E r7 = r4.value     // Catch:{ SecurityException -> 0x0077 }
            java.lang.Boolean r7 = (java.lang.Boolean) r7     // Catch:{ SecurityException -> 0x0077 }
            boolean r7 = r7.booleanValue()     // Catch:{ SecurityException -> 0x0077 }
            if (r7 != 0) goto L_0x0070
            java.lang.String r5 = "WifiService"
            java.lang.String r7 = "Failed to start scan"
            android.util.Log.e(r5, r7)     // Catch:{ SecurityException -> 0x0077 }
            android.os.Binder.restoreCallingIdentity(r2)
            return r1
        L_0x0070:
            android.os.Binder.restoreCallingIdentity(r2)
            return r5
        L_0x0075:
            r1 = move-exception
            goto L_0x00a3
        L_0x0077:
            r4 = move-exception
            java.lang.String r5 = "WifiService"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0075 }
            r6.<init>()     // Catch:{ all -> 0x0075 }
            java.lang.String r7 = "Permission violation - startScan not allowed for uid="
            r6.append(r7)     // Catch:{ all -> 0x0075 }
            r6.append(r0)     // Catch:{ all -> 0x0075 }
            java.lang.String r7 = ", packageName="
            r6.append(r7)     // Catch:{ all -> 0x0075 }
            r6.append(r11)     // Catch:{ all -> 0x0075 }
            java.lang.String r7 = ", reason="
            r6.append(r7)     // Catch:{ all -> 0x0075 }
            r6.append(r4)     // Catch:{ all -> 0x0075 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0075 }
            android.util.Slog.e(r5, r6)     // Catch:{ all -> 0x0075 }
            android.os.Binder.restoreCallingIdentity(r2)
            return r1
        L_0x00a3:
            android.os.Binder.restoreCallingIdentity(r2)
            throw r1
        L_0x00a7:
            r1 = move-exception
            monitor-exit(r10)     // Catch:{ all -> 0x00a7 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiServiceImpl.startScan(java.lang.String):boolean");
    }

    public /* synthetic */ void lambda$startScan$0$WifiServiceImpl(GeneralUtil.Mutable scanSuccess, int callingUid, String packageName) {
        scanSuccess.value = Boolean.valueOf(this.mScanRequestProxy.startScan(callingUid, packageName));
    }

    private void sendFailedScanBroadcast() {
        long callingIdentity = Binder.clearCallingIdentity();
        try {
            Intent intent = new Intent("android.net.wifi.SCAN_RESULTS");
            intent.addFlags(MiuiWindowManager.LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
            intent.putExtra("resultsUpdated", false);
            this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
        } finally {
            Binder.restoreCallingIdentity(callingIdentity);
        }
    }

    public String getCurrentNetworkWpsNfcConfigurationToken() {
        enforceConnectivityInternalPermission();
        if (!this.mVerboseLoggingEnabled) {
            return null;
        }
        this.mLog.info("getCurrentNetworkWpsNfcConfigurationToken uid=%").c((long) Binder.getCallingUid()).flush();
        return null;
    }

    /* access modifiers changed from: package-private */
    public void handleIdleModeChanged() {
        boolean doScan = false;
        synchronized (this) {
            boolean idle = this.mPowerManager.isDeviceIdleMode();
            if (this.mInIdleMode != idle) {
                this.mInIdleMode = idle;
                if (!idle && this.mScanPending) {
                    this.mScanPending = false;
                    doScan = true;
                }
            }
        }
        if (doScan) {
            startScan(this.mContext.getOpPackageName());
        }
    }

    private boolean checkNetworkSettingsPermission(int pid, int uid) {
        return this.mContext.checkPermission("android.permission.NETWORK_SETTINGS", pid, uid) == 0;
    }

    private boolean checkNetworkSetupWizardPermission(int pid, int uid) {
        return this.mContext.checkPermission("android.permission.NETWORK_SETUP_WIZARD", pid, uid) == 0;
    }

    private boolean checkNetworkStackPermission(int pid, int uid) {
        return this.mContext.checkPermission("android.permission.NETWORK_STACK", pid, uid) == 0;
    }

    private boolean checkNetworkManagedProvisioningPermission(int pid, int uid) {
        return this.mContext.checkPermission("android.permission.NETWORK_MANAGED_PROVISIONING", pid, uid) == 0;
    }

    /* access modifiers changed from: private */
    public boolean isPrivileged(int pid, int uid) {
        return checkNetworkSettingsPermission(pid, uid) || checkNetworkSetupWizardPermission(pid, uid) || checkNetworkStackPermission(pid, uid) || checkNetworkManagedProvisioningPermission(pid, uid);
    }

    private boolean isSettingsOrSuw(int pid, int uid) {
        return checkNetworkSettingsPermission(pid, uid) || checkNetworkSetupWizardPermission(pid, uid);
    }

    private boolean isSystem(String packageName) {
        long ident = Binder.clearCallingIdentity();
        boolean z = false;
        try {
            ApplicationInfo info = this.mContext.getPackageManager().getApplicationInfo(packageName, 0);
            if (info.isSystemApp() || info.isUpdatedSystemApp()) {
                z = true;
            }
            return z;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    private boolean isDeviceOrProfileOwner(int uid) {
        DevicePolicyManagerInternal dpmi = this.mWifiInjector.getWifiPermissionsWrapper().getDevicePolicyManagerInternal();
        if (dpmi == null) {
            return false;
        }
        if (dpmi.isActiveAdminWithPolicy(uid, -2) || dpmi.isActiveAdminWithPolicy(uid, -1)) {
            return true;
        }
        return false;
    }

    private void enforceNetworkSettingsPermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.NETWORK_SETTINGS", TAG);
    }

    private void enforceNetworkStackPermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.NETWORK_STACK", TAG);
    }

    private void enforceAccessPermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE", TAG);
    }

    private int enforceChangePermission(String callingPackage) {
        this.mAppOps.checkPackage(Binder.getCallingUid(), callingPackage);
        if (checkNetworkSettingsPermission(Binder.getCallingPid(), Binder.getCallingUid())) {
            return 0;
        }
        this.mContext.enforceCallingOrSelfPermission("android.permission.CHANGE_WIFI_STATE", TAG);
        return this.mAppOps.noteOp("android:change_wifi_state", Binder.getCallingUid(), callingPackage);
    }

    private void enforceLocationHardwarePermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.LOCATION_HARDWARE", "LocationHardware");
    }

    private void enforceReadCredentialPermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.READ_WIFI_CREDENTIAL", TAG);
    }

    private void enforceWorkSourcePermission() {
        this.mContext.enforceCallingPermission("android.permission.UPDATE_DEVICE_STATS", TAG);
    }

    private void enforceMulticastChangePermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CHANGE_WIFI_MULTICAST_STATE", TAG);
    }

    private void enforceConnectivityInternalPermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", "ConnectivityService");
    }

    private void enforceLocationPermission(String pkgName, int uid) {
        this.mWifiPermissionsUtil.enforceLocationPermission(pkgName, uid);
    }

    private boolean isTargetSdkLessThanQOrPrivileged(String packageName, int pid, int uid) {
        return this.mWifiPermissionsUtil.isTargetSdkLessThan(packageName, 29) || isPrivileged(pid, uid) || isDeviceOrProfileOwner(uid) || isSystem(packageName) || this.mWifiPermissionsUtil.checkSystemAlertWindowPermission(uid, packageName);
    }

    /* JADX INFO: finally extract failed */
    public synchronized boolean setWifiEnabled(String packageName, boolean enable) {
        if (enforceChangePermission(packageName) != 0) {
            return false;
        }
        boolean isPrivileged = isPrivileged(Binder.getCallingPid(), Binder.getCallingUid());
        if (!isPrivileged && !this.mWifiPermissionsUtil.isTargetSdkLessThan(packageName, 29)) {
            this.mLog.info("setWifiEnabled not allowed for uid=%").c((long) Binder.getCallingUid()).flush();
            return false;
        } else if (this.mSettingsStore.isAirplaneModeOn() && !isPrivileged) {
            this.mLog.err("setWifiEnabled in Airplane mode: only Settings can toggle wifi").flush();
            return false;
        } else if (!MiuiWifiService.enforceChangePermission(this.mContext)) {
            Slog.d(TAG, "refuse setWifiEnabled: " + enable + " pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + ", package=" + packageName);
            return false;
        } else {
            if (!(this.mWifiApState == 13) || isPrivileged) {
                this.mLog.info("setWifiEnabled package=% uid=% enable=%").c(packageName).c((long) Binder.getCallingUid()).c(enable).flush();
                long ident = Binder.clearCallingIdentity();
                try {
                    if (!this.mSettingsStore.handleWifiToggled(enable)) {
                        Binder.restoreCallingIdentity(ident);
                        return true;
                    }
                    Binder.restoreCallingIdentity(ident);
                    if (!this.mIsControllerStarted) {
                        Slog.e(TAG, "WifiController is not yet started, abort setWifiEnabled");
                        return false;
                    }
                    this.mWifiMetrics.incrementNumWifiToggles(isPrivileged, enable);
                    this.mWifiController.sendMessage(155656);
                    return true;
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(ident);
                    throw th;
                }
            } else {
                this.mLog.err("setWifiEnabled SoftAp enabled: only Settings can toggle wifi").flush();
                return false;
            }
        }
    }

    public int getWifiEnabledState() {
        enforceAccessPermission();
        if (this.mVerboseLoggingEnabled) {
            this.mLog.info("getWifiEnabledState uid=%").c((long) Binder.getCallingUid()).flush();
        }
        return this.mClientModeImpl.syncGetWifiState();
    }

    public int getWifiApEnabledState() {
        enforceAccessPermission();
        if (this.mVerboseLoggingEnabled) {
            this.mLog.info("getWifiApEnabledState uid=%").c((long) Binder.getCallingUid()).flush();
        }
        MutableInt apState = new MutableInt(11);
        this.mWifiInjector.getClientModeImplHandler().runWithScissors(new Runnable(apState) {
            private final /* synthetic */ MutableInt f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                WifiServiceImpl.this.lambda$getWifiApEnabledState$1$WifiServiceImpl(this.f$1);
            }
        }, 4000);
        return apState.value;
    }

    public /* synthetic */ void lambda$getWifiApEnabledState$1$WifiServiceImpl(MutableInt apState) {
        apState.value = this.mWifiApState;
    }

    public void updateInterfaceIpState(String ifaceName, int mode) {
        enforceNetworkStackPermission();
        this.mLog.info("updateInterfaceIpState uid=%").c((long) Binder.getCallingUid()).flush();
        this.mWifiInjector.getClientModeImplHandler().post(new Runnable(ifaceName, mode) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                WifiServiceImpl.this.lambda$updateInterfaceIpState$2$WifiServiceImpl(this.f$1, this.f$2);
            }
        });
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00aa, code lost:
        return;
     */
    /* renamed from: updateInterfaceIpStateInternal */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void lambda$updateInterfaceIpState$2$WifiServiceImpl(java.lang.String r8, int r9) {
        /*
            r7 = this;
            java.util.HashMap<java.lang.Integer, com.android.server.wifi.LocalOnlyHotspotRequestInfo> r0 = r7.mLocalOnlyHotspotRequests
            monitor-enter(r0)
            r1 = -1
            java.lang.Integer r2 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x00ab }
            if (r8 == 0) goto L_0x0017
            java.util.concurrent.ConcurrentHashMap<java.lang.String, java.lang.Integer> r3 = r7.mIfaceIpModes     // Catch:{ all -> 0x00ab }
            java.lang.Integer r4 = java.lang.Integer.valueOf(r9)     // Catch:{ all -> 0x00ab }
            java.lang.Object r3 = r3.put(r8, r4)     // Catch:{ all -> 0x00ab }
            java.lang.Integer r3 = (java.lang.Integer) r3     // Catch:{ all -> 0x00ab }
            r2 = r3
        L_0x0017:
            java.lang.String r3 = "WifiService"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ab }
            r4.<init>()     // Catch:{ all -> 0x00ab }
            java.lang.String r5 = "updateInterfaceIpState: ifaceName="
            r4.append(r5)     // Catch:{ all -> 0x00ab }
            r4.append(r8)     // Catch:{ all -> 0x00ab }
            java.lang.String r5 = " mode="
            r4.append(r5)     // Catch:{ all -> 0x00ab }
            r4.append(r9)     // Catch:{ all -> 0x00ab }
            java.lang.String r5 = " previous mode= "
            r4.append(r5)     // Catch:{ all -> 0x00ab }
            r4.append(r2)     // Catch:{ all -> 0x00ab }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00ab }
            android.util.Slog.d(r3, r4)     // Catch:{ all -> 0x00ab }
            if (r9 == r1) goto L_0x00a0
            r3 = 0
            r4 = 2
            if (r9 == 0) goto L_0x0078
            r5 = 1
            if (r9 == r5) goto L_0x006d
            if (r9 == r4) goto L_0x0059
            com.android.server.wifi.WifiLog r1 = r7.mLog     // Catch:{ all -> 0x00ab }
            java.lang.String r3 = "updateInterfaceIpStateInternal: unknown mode %"
            com.android.server.wifi.WifiLog$LogMessage r1 = r1.warn(r3)     // Catch:{ all -> 0x00ab }
            long r3 = (long) r9     // Catch:{ all -> 0x00ab }
            com.android.server.wifi.WifiLog$LogMessage r1 = r1.c((long) r3)     // Catch:{ all -> 0x00ab }
            r1.flush()     // Catch:{ all -> 0x00ab }
            goto L_0x00a9
        L_0x0059:
            java.util.HashMap<java.lang.Integer, com.android.server.wifi.LocalOnlyHotspotRequestInfo> r4 = r7.mLocalOnlyHotspotRequests     // Catch:{ all -> 0x00ab }
            boolean r4 = r4.isEmpty()     // Catch:{ all -> 0x00ab }
            if (r4 == 0) goto L_0x0069
            r7.stopSoftAp()     // Catch:{ all -> 0x00ab }
            r7.lambda$updateInterfaceIpState$2$WifiServiceImpl(r3, r1)     // Catch:{ all -> 0x00ab }
            monitor-exit(r0)     // Catch:{ all -> 0x00ab }
            return
        L_0x0069:
            r7.sendHotspotStartedMessageToAllLOHSRequestInfoEntriesLocked()     // Catch:{ all -> 0x00ab }
            goto L_0x00a9
        L_0x006d:
            boolean r1 = r7.isConcurrentLohsAndTetheringSupported()     // Catch:{ all -> 0x00ab }
            if (r1 != 0) goto L_0x00a9
            r1 = 3
            r7.sendHotspotFailedMessageToAllLOHSRequestInfoEntriesLocked(r1)     // Catch:{ all -> 0x00ab }
            goto L_0x00a9
        L_0x0078:
            java.lang.String r5 = "WifiService"
            java.lang.String r6 = "IP mode config error - need to clean up"
            android.util.Slog.d(r5, r6)     // Catch:{ all -> 0x00ab }
            java.util.HashMap<java.lang.Integer, com.android.server.wifi.LocalOnlyHotspotRequestInfo> r5 = r7.mLocalOnlyHotspotRequests     // Catch:{ all -> 0x00ab }
            boolean r5 = r5.isEmpty()     // Catch:{ all -> 0x00ab }
            if (r5 == 0) goto L_0x0092
            java.lang.String r4 = "WifiService"
            java.lang.String r5 = "no LOHS requests, stop softap"
            android.util.Slog.d(r4, r5)     // Catch:{ all -> 0x00ab }
            r7.stopSoftAp()     // Catch:{ all -> 0x00ab }
            goto L_0x009c
        L_0x0092:
            java.lang.String r5 = "WifiService"
            java.lang.String r6 = "we have LOHS requests, clean them up"
            android.util.Slog.d(r5, r6)     // Catch:{ all -> 0x00ab }
            r7.sendHotspotFailedMessageToAllLOHSRequestInfoEntriesLocked(r4)     // Catch:{ all -> 0x00ab }
        L_0x009c:
            r7.lambda$updateInterfaceIpState$2$WifiServiceImpl(r3, r1)     // Catch:{ all -> 0x00ab }
            goto L_0x00a9
        L_0x00a0:
            if (r8 != 0) goto L_0x00a9
            java.util.concurrent.ConcurrentHashMap<java.lang.String, java.lang.Integer> r1 = r7.mIfaceIpModes     // Catch:{ all -> 0x00ab }
            r1.clear()     // Catch:{ all -> 0x00ab }
            monitor-exit(r0)     // Catch:{ all -> 0x00ab }
            return
        L_0x00a9:
            monitor-exit(r0)     // Catch:{ all -> 0x00ab }
            return
        L_0x00ab:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00ab }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiServiceImpl.lambda$updateInterfaceIpState$2$WifiServiceImpl(java.lang.String, int):void");
    }

    public boolean startSoftAp(WifiConfiguration wifiConfig) {
        enforceNetworkStackPermission();
        this.mLog.info("startSoftAp uid=%").c((long) Binder.getCallingUid()).flush();
        synchronized (this.mLocalOnlyHotspotRequests) {
            if (this.mIfaceIpModes.contains(1)) {
                this.mLog.err("Tethering is already active.").flush();
                return false;
            }
            if (!isConcurrentLohsAndTetheringSupported() && !this.mLocalOnlyHotspotRequests.isEmpty()) {
                stopSoftApInternal(2);
            }
            boolean startSoftApInternal = startSoftApInternal(wifiConfig, 1);
            return startSoftApInternal;
        }
    }

    private boolean startSoftApInternal(WifiConfiguration wifiConfig, int mode) {
        this.mLog.trace("startSoftApInternal uid=% mode=%").c((long) Binder.getCallingUid()).c((long) mode).flush();
        if (wifiConfig == null && this.mSettingsStore.isAirplaneModeOn()) {
            Log.d(TAG, "Starting softap in airplane mode. Fallback to 2G band");
            wifiConfig = new WifiConfiguration(this.mWifiApConfigStore.getApConfiguration());
            wifiConfig.apBand = 0;
        }
        setDualSapMode(wifiConfig);
        this.mSoftApExtendingWifi = isCurrentStaShareThisAp();
        if (this.mSoftApExtendingWifi) {
            startSoftApInRepeaterMode(mode, wifiConfig);
            return true;
        } else if (wifiConfig == null || WifiApConfigStore.validateApWifiConfiguration(wifiConfig)) {
            this.mWifiController.sendMessage(155658, 1, 0, new SoftApModeConfiguration(mode, wifiConfig));
            return true;
        } else {
            Slog.e(TAG, "Invalid WifiConfiguration");
            return false;
        }
    }

    public boolean stopSoftAp() {
        boolean stopSoftApInternal;
        enforceNetworkStackPermission();
        this.mLog.info("stopSoftAp uid=%").c((long) Binder.getCallingUid()).flush();
        synchronized (this.mLocalOnlyHotspotRequests) {
            if (!this.mLocalOnlyHotspotRequests.isEmpty()) {
                this.mLog.trace("Call to stop Tethering while LOHS is active, Registered LOHS callers will be updated when softap stopped.").flush();
            }
            stopSoftApInternal = stopSoftApInternal(1);
        }
        return stopSoftApInternal;
    }

    private boolean stopSoftApInternal(int mode) {
        this.mLog.trace("stopSoftApInternal uid=%").c((long) Binder.getCallingUid()).flush();
        this.mSoftApExtendingWifi = false;
        this.mWifiController.sendMessage(155658, 0, mode);
        return true;
    }

    private final class SoftApCallbackImpl implements WifiManager.SoftApCallback {
        private SoftApCallbackImpl() {
        }

        public void onStateChanged(int state, int failureReason) {
            int unused = WifiServiceImpl.this.mSoftApState = state;
            Iterator<ISoftApCallback> iterator = WifiServiceImpl.this.mRegisteredSoftApCallbacks.getCallbacks().iterator();
            while (iterator.hasNext()) {
                try {
                    iterator.next().onStateChanged(state, failureReason);
                } catch (RemoteException e) {
                    Log.e(WifiServiceImpl.TAG, "onStateChanged: remote exception -- " + e);
                    iterator.remove();
                }
            }
        }

        public void onNumClientsChanged(int numClients) {
            int unused = WifiServiceImpl.this.mSoftApNumClients = numClients;
            Iterator<ISoftApCallback> iterator = WifiServiceImpl.this.mRegisteredSoftApCallbacks.getCallbacks().iterator();
            while (iterator.hasNext()) {
                try {
                    iterator.next().onNumClientsChanged(numClients);
                } catch (RemoteException e) {
                    Log.e(WifiServiceImpl.TAG, "onNumClientsChanged: remote exception -- " + e);
                    iterator.remove();
                }
            }
        }

        public void onStaConnected(String Macaddr, int numClients) {
            int unused = WifiServiceImpl.this.mQCSoftApNumClients = numClients;
            Iterator<ISoftApCallback> iterator = WifiServiceImpl.this.mRegisteredSoftApCallbacks.getCallbacks().iterator();
            while (iterator.hasNext()) {
                ISoftApCallback callback = iterator.next();
                try {
                    Log.d(WifiServiceImpl.TAG, "onStaConnected Macaddr: " + Macaddr + " with num of active client:" + WifiServiceImpl.this.mQCSoftApNumClients);
                    callback.onStaConnected(Macaddr, WifiServiceImpl.this.mQCSoftApNumClients);
                } catch (RemoteException e) {
                    Log.e(WifiServiceImpl.TAG, "onStaConnected: remote exception -- " + e);
                    iterator.remove();
                }
            }
        }

        public void onStaDisconnected(String Macaddr, int numClients) {
            int unused = WifiServiceImpl.this.mQCSoftApNumClients = numClients;
            Iterator<ISoftApCallback> iterator = WifiServiceImpl.this.mRegisteredSoftApCallbacks.getCallbacks().iterator();
            while (iterator.hasNext()) {
                ISoftApCallback callback = iterator.next();
                try {
                    Log.d(WifiServiceImpl.TAG, "onStaDisconnected Macaddr: " + Macaddr + " with num of active client:" + WifiServiceImpl.this.mQCSoftApNumClients);
                    callback.onStaDisconnected(Macaddr, WifiServiceImpl.this.mQCSoftApNumClients);
                } catch (RemoteException e) {
                    Log.e(WifiServiceImpl.TAG, "onStaDisconnected: remote exception -- " + e);
                    iterator.remove();
                }
            }
        }
    }

    public void registerSoftApCallback(IBinder binder, ISoftApCallback callback, int callbackIdentifier) {
        if (binder == null) {
            throw new IllegalArgumentException("Binder must not be null");
        } else if (callback != null) {
            enforceNetworkSettingsPermission();
            if (this.mVerboseLoggingEnabled) {
                this.mLog.info("registerSoftApCallback uid=%").c((long) Binder.getCallingUid()).flush();
            }
            this.mWifiInjector.getClientModeImplHandler().post(new Runnable(binder, callback, callbackIdentifier) {
                private final /* synthetic */ IBinder f$1;
                private final /* synthetic */ ISoftApCallback f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    WifiServiceImpl.this.lambda$registerSoftApCallback$3$WifiServiceImpl(this.f$1, this.f$2, this.f$3);
                }
            });
        } else {
            throw new IllegalArgumentException("Callback must not be null");
        }
    }

    public /* synthetic */ void lambda$registerSoftApCallback$3$WifiServiceImpl(IBinder binder, ISoftApCallback callback, int callbackIdentifier) {
        if (!this.mRegisteredSoftApCallbacks.add(binder, callback, callbackIdentifier)) {
            Log.e(TAG, "registerSoftApCallback: Failed to add callback");
            return;
        }
        try {
            callback.onStateChanged(this.mSoftApState, 0);
            callback.onNumClientsChanged(this.mSoftApNumClients);
            callback.onStaConnected(Prefix.EMPTY, this.mQCSoftApNumClients);
        } catch (RemoteException e) {
            Log.e(TAG, "registerSoftApCallback: remote exception -- " + e);
        }
    }

    public void unregisterSoftApCallback(int callbackIdentifier) {
        enforceNetworkSettingsPermission();
        if (this.mVerboseLoggingEnabled) {
            this.mLog.info("unregisterSoftApCallback uid=%").c((long) Binder.getCallingUid()).flush();
        }
        this.mWifiInjector.getClientModeImplHandler().post(new Runnable(callbackIdentifier) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                WifiServiceImpl.this.lambda$unregisterSoftApCallback$4$WifiServiceImpl(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$unregisterSoftApCallback$4$WifiServiceImpl(int callbackIdentifier) {
        this.mRegisteredSoftApCallbacks.remove(callbackIdentifier);
    }

    /* access modifiers changed from: private */
    public void handleWifiApStateChange(int currentState, int previousState, int errorCode, String ifaceName, int mode) {
        Slog.d(TAG, "handleWifiApStateChange: currentState=" + currentState + " previousState=" + previousState + " errorCode= " + errorCode + " ifaceName=" + ifaceName + " mode=" + mode);
        this.mWifiApState = currentState;
        if (currentState == 14) {
            synchronized (this.mLocalOnlyHotspotRequests) {
                int errorToReport = 2;
                if (errorCode == 1) {
                    errorToReport = 1;
                }
                sendHotspotFailedMessageToAllLOHSRequestInfoEntriesLocked(errorToReport);
                lambda$updateInterfaceIpState$2$WifiServiceImpl((String) null, -1);
            }
        } else if (currentState == 10 || currentState == 11) {
            synchronized (this.mLocalOnlyHotspotRequests) {
                if (this.mIfaceIpModes.getOrDefault(ifaceName, -1).intValue() == 2) {
                    sendHotspotStoppedMessageToAllLOHSRequestInfoEntriesLocked();
                } else if (!isConcurrentLohsAndTetheringSupported()) {
                    sendHotspotFailedMessageToAllLOHSRequestInfoEntriesLocked(2);
                }
                updateInterfaceIpState((String) null, -1);
                if (currentState == 11 && this.mRestartWifiApIfRequired) {
                    this.mWifiInjector.getClientModeImplHandler().post(new Runnable() {
                        public final void run() {
                            WifiServiceImpl.this.lambda$handleWifiApStateChange$5$WifiServiceImpl();
                        }
                    });
                }
            }
        }
    }

    public /* synthetic */ void lambda$handleWifiApStateChange$5$WifiServiceImpl() {
        Slog.d(TAG, "Repeater mode: Restart SoftAP.");
        this.mRestartWifiApIfRequired = false;
        startSoftAp((WifiConfiguration) null);
    }

    @GuardedBy({"mLocalOnlyHotspotRequests"})
    private void sendHotspotFailedMessageToAllLOHSRequestInfoEntriesLocked(int arg1) {
        for (LocalOnlyHotspotRequestInfo requestor : this.mLocalOnlyHotspotRequests.values()) {
            try {
                requestor.sendHotspotFailedMessage(arg1);
                requestor.unlinkDeathRecipient();
            } catch (RemoteException e) {
            }
        }
        this.mLocalOnlyHotspotRequests.clear();
    }

    @GuardedBy({"mLocalOnlyHotspotRequests"})
    private void sendHotspotStoppedMessageToAllLOHSRequestInfoEntriesLocked() {
        for (LocalOnlyHotspotRequestInfo requestor : this.mLocalOnlyHotspotRequests.values()) {
            try {
                requestor.sendHotspotStoppedMessage();
                requestor.unlinkDeathRecipient();
            } catch (RemoteException e) {
            }
        }
        this.mLocalOnlyHotspotRequests.clear();
    }

    @GuardedBy({"mLocalOnlyHotspotRequests"})
    private void sendHotspotStartedMessageToAllLOHSRequestInfoEntriesLocked() {
        for (LocalOnlyHotspotRequestInfo requestor : this.mLocalOnlyHotspotRequests.values()) {
            try {
                requestor.sendHotspotStartedMessage(this.mLocalOnlyHotspotConfig);
            } catch (RemoteException e) {
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void registerLOHSForTest(int pid, LocalOnlyHotspotRequestInfo request) {
        this.mLocalOnlyHotspotRequests.put(Integer.valueOf(pid), request);
    }

    /* Debug info: failed to restart local var, previous not found, register: 11 */
    /* JADX INFO: finally extract failed */
    public int startLocalOnlyHotspot(Messenger messenger, IBinder binder, String packageName) {
        int uid = Binder.getCallingUid();
        int pid = Binder.getCallingPid();
        if (enforceChangePermission(packageName) != 0) {
            return 2;
        }
        enforceLocationPermission(packageName, uid);
        long ident = Binder.clearCallingIdentity();
        try {
            if (this.mWifiPermissionsUtil.isLocationModeEnabled()) {
                Binder.restoreCallingIdentity(ident);
                if (this.mUserManager.hasUserRestriction("no_config_tethering")) {
                    return 4;
                }
                if (!this.mFrameworkFacade.isAppForeground(uid)) {
                    return 3;
                }
                this.mLog.info("startLocalOnlyHotspot uid=% pid=%").c((long) uid).c((long) pid).flush();
                synchronized (this.mLocalOnlyHotspotRequests) {
                    int i = 1;
                    if (!isConcurrentLohsAndTetheringSupported() && this.mIfaceIpModes.contains(1)) {
                        this.mLog.info("Cannot start localOnlyHotspot when WiFi Tethering is active.").flush();
                        return 3;
                    } else if (this.mLocalOnlyHotspotRequests.get(Integer.valueOf(pid)) == null) {
                        LocalOnlyHotspotRequestInfo request = new LocalOnlyHotspotRequestInfo(binder, messenger, new LocalOnlyRequestorCallback());
                        if (this.mIfaceIpModes.contains(2)) {
                            try {
                                this.mLog.trace("LOHS already up, trigger onStarted callback").flush();
                                request.sendHotspotStartedMessage(this.mLocalOnlyHotspotConfig);
                            } catch (RemoteException e) {
                                return 2;
                            }
                        } else if (this.mLocalOnlyHotspotRequests.isEmpty()) {
                            boolean is5Ghz = hasAutomotiveFeature(this.mContext) && this.mContext.getResources().getBoolean(17891597) && is5GhzSupported();
                            Context context = this.mContext;
                            if (!is5Ghz) {
                                i = 0;
                            }
                            this.mLocalOnlyHotspotConfig = WifiApConfigStore.generateLocalOnlyHotspotConfig(context, i);
                            startSoftApInternal(this.mLocalOnlyHotspotConfig, 2);
                        }
                        this.mLocalOnlyHotspotRequests.put(Integer.valueOf(pid), request);
                        return 0;
                    } else {
                        this.mLog.trace("caller already has an active request").flush();
                        throw new IllegalStateException("Caller already has an active LocalOnlyHotspot request");
                    }
                }
            } else {
                throw new SecurityException("Location mode is not enabled.");
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
    }

    public void stopLocalOnlyHotspot() {
        int uid = Binder.getCallingUid();
        int pid = Binder.getCallingPid();
        this.mLog.info("stopLocalOnlyHotspot uid=% pid=%").c((long) uid).c((long) pid).flush();
        synchronized (this.mLocalOnlyHotspotRequests) {
            LocalOnlyHotspotRequestInfo requestInfo = this.mLocalOnlyHotspotRequests.get(Integer.valueOf(pid));
            if (requestInfo != null) {
                requestInfo.unlinkDeathRecipient();
                unregisterCallingAppAndStopLocalOnlyHotspot(requestInfo);
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0055, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void unregisterCallingAppAndStopLocalOnlyHotspot(com.android.server.wifi.LocalOnlyHotspotRequestInfo r5) {
        /*
            r4 = this;
            com.android.server.wifi.WifiLog r0 = r4.mLog
            java.lang.String r1 = "unregisterCallingAppAndStopLocalOnlyHotspot pid=%"
            com.android.server.wifi.WifiLog$LogMessage r0 = r0.trace(r1)
            int r1 = r5.getPid()
            long r1 = (long) r1
            com.android.server.wifi.WifiLog$LogMessage r0 = r0.c((long) r1)
            r0.flush()
            java.util.HashMap<java.lang.Integer, com.android.server.wifi.LocalOnlyHotspotRequestInfo> r0 = r4.mLocalOnlyHotspotRequests
            monitor-enter(r0)
            java.util.HashMap<java.lang.Integer, com.android.server.wifi.LocalOnlyHotspotRequestInfo> r1 = r4.mLocalOnlyHotspotRequests     // Catch:{ all -> 0x0056 }
            int r2 = r5.getPid()     // Catch:{ all -> 0x0056 }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ all -> 0x0056 }
            java.lang.Object r1 = r1.remove(r2)     // Catch:{ all -> 0x0056 }
            if (r1 != 0) goto L_0x0034
            com.android.server.wifi.WifiLog r1 = r4.mLog     // Catch:{ all -> 0x0056 }
            java.lang.String r2 = "LocalOnlyHotspotRequestInfo not found to remove"
            com.android.server.wifi.WifiLog$LogMessage r1 = r1.trace(r2)     // Catch:{ all -> 0x0056 }
            r1.flush()     // Catch:{ all -> 0x0056 }
            monitor-exit(r0)     // Catch:{ all -> 0x0056 }
            return
        L_0x0034:
            java.util.HashMap<java.lang.Integer, com.android.server.wifi.LocalOnlyHotspotRequestInfo> r1 = r4.mLocalOnlyHotspotRequests     // Catch:{ all -> 0x0056 }
            boolean r1 = r1.isEmpty()     // Catch:{ all -> 0x0056 }
            if (r1 == 0) goto L_0x0054
            r1 = 0
            r4.mLocalOnlyHotspotConfig = r1     // Catch:{ all -> 0x0056 }
            r2 = -1
            r4.lambda$updateInterfaceIpState$2$WifiServiceImpl(r1, r2)     // Catch:{ all -> 0x0056 }
            long r1 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x0056 }
            r3 = 2
            r4.stopSoftApInternal(r3)     // Catch:{ all -> 0x004f }
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x0056 }
            goto L_0x0054
        L_0x004f:
            r3 = move-exception
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x0056 }
            throw r3     // Catch:{ all -> 0x0056 }
        L_0x0054:
            monitor-exit(r0)     // Catch:{ all -> 0x0056 }
            return
        L_0x0056:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0056 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiServiceImpl.unregisterCallingAppAndStopLocalOnlyHotspot(com.android.server.wifi.LocalOnlyHotspotRequestInfo):void");
    }

    public void startWatchLocalOnlyHotspot(Messenger messenger, IBinder binder) {
        enforceNetworkSettingsPermission();
        throw new UnsupportedOperationException("LocalOnlyHotspot is still in development");
    }

    public void stopWatchLocalOnlyHotspot() {
        enforceNetworkSettingsPermission();
        throw new UnsupportedOperationException("LocalOnlyHotspot is still in development");
    }

    public WifiConfiguration getWifiApConfiguration() {
        enforceAccessPermission();
        int uid = Binder.getCallingUid();
        if (this.mWifiPermissionsUtil.checkConfigOverridePermission(uid)) {
            this.mLog.info("getWifiApConfiguration uid=%").c((long) uid).flush();
            GeneralUtil.Mutable<WifiConfiguration> config = new GeneralUtil.Mutable<>();
            if (this.mWifiInjector.getClientModeImplHandler().runWithScissors(new Runnable(config) {
                private final /* synthetic */ GeneralUtil.Mutable f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    WifiServiceImpl.this.lambda$getWifiApConfiguration$6$WifiServiceImpl(this.f$1);
                }
            }, 4000)) {
                return (WifiConfiguration) config.value;
            }
            Log.e(TAG, "Failed to post runnable to fetch ap config");
            return new WifiConfiguration();
        }
        throw new SecurityException("App not allowed to read or update stored WiFi Ap config (uid = " + uid + ")");
    }

    public /* synthetic */ void lambda$getWifiApConfiguration$6$WifiServiceImpl(GeneralUtil.Mutable config) {
        config.value = this.mWifiApConfigStore.getApConfiguration();
    }

    public boolean setWifiApConfiguration(WifiConfiguration wifiConfig, String packageName) {
        if (enforceChangePermission(packageName) != 0) {
            return false;
        }
        int uid = Binder.getCallingUid();
        if (this.mWifiPermissionsUtil.checkConfigOverridePermission(uid)) {
            this.mLog.info("setWifiApConfiguration uid=%").c((long) uid).flush();
            if (wifiConfig == null) {
                return false;
            }
            if (WifiApConfigStore.validateApWifiConfiguration(wifiConfig)) {
                this.mClientModeImplHandler.post(new Runnable(wifiConfig) {
                    private final /* synthetic */ WifiConfiguration f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        WifiServiceImpl.this.lambda$setWifiApConfiguration$7$WifiServiceImpl(this.f$1);
                    }
                });
                return true;
            }
            Slog.e(TAG, "Invalid WifiConfiguration");
            return false;
        }
        throw new SecurityException("App not allowed to read or update stored WiFi AP config (uid = " + uid + ")");
    }

    public /* synthetic */ void lambda$setWifiApConfiguration$7$WifiServiceImpl(WifiConfiguration wifiConfig) {
        this.mWifiApConfigStore.setApConfiguration(wifiConfig);
    }

    public void notifyUserOfApBandConversion(String packageName) {
        enforceNetworkSettingsPermission();
        if (this.mVerboseLoggingEnabled) {
            this.mLog.info("notifyUserOfApBandConversion uid=% packageName=%").c((long) Binder.getCallingUid()).c(packageName).flush();
        }
        this.mWifiApConfigStore.notifyUserOfApBandConversion(packageName);
    }

    public boolean isScanAlwaysAvailable() {
        enforceAccessPermission();
        if (this.mVerboseLoggingEnabled) {
            this.mLog.info("isScanAlwaysAvailable uid=%").c((long) Binder.getCallingUid()).flush();
        }
        return this.mSettingsStore.isScanAlwaysAvailable();
    }

    public boolean disconnect(String packageName) {
        if (enforceChangePermission(packageName) != 0) {
            return false;
        }
        if (!isTargetSdkLessThanQOrPrivileged(packageName, Binder.getCallingPid(), Binder.getCallingUid())) {
            this.mLog.info("disconnect not allowed for uid=%").c((long) Binder.getCallingUid()).flush();
            return false;
        } else if (!MiuiWifiService.enforceChangePermission(this.mContext)) {
            Slog.d(TAG, "refuse disconnect: uid=" + Binder.getCallingUid());
            return false;
        } else {
            this.mLog.info("disconnect uid=%").c((long) Binder.getCallingUid()).flush();
            this.mClientModeImpl.disconnectCommand();
            return true;
        }
    }

    public boolean reconnect(String packageName) {
        if (enforceChangePermission(packageName) != 0) {
            return false;
        }
        if (!isTargetSdkLessThanQOrPrivileged(packageName, Binder.getCallingPid(), Binder.getCallingUid())) {
            this.mLog.info("reconnect not allowed for uid=%").c((long) Binder.getCallingUid()).flush();
            return false;
        }
        this.mLog.info("reconnect uid=%").c((long) Binder.getCallingUid()).flush();
        this.mClientModeImpl.reconnectCommand(new WorkSource(Binder.getCallingUid()));
        return true;
    }

    public boolean reassociate(String packageName) {
        if (enforceChangePermission(packageName) != 0) {
            return false;
        }
        if (!isTargetSdkLessThanQOrPrivileged(packageName, Binder.getCallingPid(), Binder.getCallingUid())) {
            this.mLog.info("reassociate not allowed for uid=%").c((long) Binder.getCallingUid()).flush();
            return false;
        }
        this.mLog.info("reassociate uid=%").c((long) Binder.getCallingUid()).flush();
        this.mClientModeImpl.reassociateCommand();
        return true;
    }

    public long getSupportedFeatures() {
        enforceAccessPermission();
        if (this.mVerboseLoggingEnabled) {
            this.mLog.info("getSupportedFeatures uid=%").c((long) Binder.getCallingUid()).flush();
        }
        return getSupportedFeaturesInternal();
    }

    public void requestActivityInfo(ResultReceiver result) {
        Bundle bundle = new Bundle();
        if (this.mVerboseLoggingEnabled) {
            this.mLog.info("requestActivityInfo uid=%").c((long) Binder.getCallingUid()).flush();
        }
        bundle.putParcelable("controller_activity", reportActivityInfo());
        result.send(0, bundle);
    }

    public WifiActivityEnergyInfo reportActivityInfo() {
        WifiActivityEnergyInfo energyInfo;
        long[] txTimePerLevel;
        double rxIdleCurrent;
        enforceAccessPermission();
        if (this.mVerboseLoggingEnabled) {
            this.mLog.info("reportActivityInfo uid=%").c((long) Binder.getCallingUid()).flush();
        }
        if ((getSupportedFeatures() & MiuiConfiguration.THEME_FLAG_CLOCK) == 0) {
            return null;
        }
        WifiActivityEnergyInfo energyInfo2 = null;
        AsyncChannel asyncChannel = this.mClientModeImplChannel;
        if (asyncChannel != null) {
            WifiLinkLayerStats stats = this.mClientModeImpl.syncGetLinkLayerStats(asyncChannel);
            if (stats != null) {
                double rxIdleCurrent2 = this.mPowerProfile.getAveragePower("wifi.controller.idle");
                double rxCurrent = this.mPowerProfile.getAveragePower("wifi.controller.rx");
                double txCurrent = this.mPowerProfile.getAveragePower("wifi.controller.tx");
                double voltage = this.mPowerProfile.getAveragePower("wifi.controller.voltage") / 1000.0d;
                long rxIdleTime = (long) ((stats.on_time - stats.tx_time) - stats.rx_time);
                if (stats.tx_time_per_level != null) {
                    long[] txTimePerLevel2 = new long[stats.tx_time_per_level.length];
                    int i = 0;
                    while (true) {
                        WifiActivityEnergyInfo energyInfo3 = energyInfo2;
                        if (i >= txTimePerLevel2.length) {
                            break;
                        }
                        txTimePerLevel2[i] = (long) stats.tx_time_per_level[i];
                        i++;
                        energyInfo2 = energyInfo3;
                    }
                    txTimePerLevel = txTimePerLevel2;
                } else {
                    txTimePerLevel = new long[0];
                }
                double txCurrent2 = txCurrent;
                long energyUsed = (long) (((((double) stats.tx_time) * txCurrent) + (((double) stats.rx_time) * rxCurrent) + (((double) rxIdleTime) * rxIdleCurrent2)) * voltage);
                if (rxIdleTime < 0 || stats.on_time < 0 || stats.tx_time < 0 || stats.rx_time < 0 || stats.on_time_scan < 0 || energyUsed < 0) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(" rxIdleCur=" + rxIdleCurrent2);
                    sb.append(" rxCur=" + rxCurrent);
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(" txCur=");
                    double d = rxIdleCurrent2;
                    rxIdleCurrent = txCurrent2;
                    sb2.append(rxIdleCurrent);
                    sb.append(sb2.toString());
                    sb.append(" voltage=" + voltage);
                    sb.append(" on_time=" + stats.on_time);
                    sb.append(" tx_time=" + stats.tx_time);
                    sb.append(" tx_time_per_level=" + Arrays.toString(txTimePerLevel));
                    sb.append(" rx_time=" + stats.rx_time);
                    sb.append(" rxIdleTime=" + rxIdleTime);
                    sb.append(" scan_time=" + stats.on_time_scan);
                    sb.append(" energy=" + energyUsed);
                    Log.d(TAG, " reportActivityInfo: " + sb.toString());
                } else {
                    double d2 = rxIdleCurrent2;
                    rxIdleCurrent = txCurrent2;
                }
                double d3 = rxIdleCurrent;
                double d4 = rxCurrent;
                energyInfo = new WifiActivityEnergyInfo(this.mClock.getElapsedSinceBootMillis(), 3, (long) stats.tx_time, txTimePerLevel, (long) stats.rx_time, (long) stats.on_time_scan, rxIdleTime, energyUsed);
            } else {
                energyInfo = null;
            }
            if (energyInfo == null || !energyInfo.isValid()) {
                return null;
            }
            return energyInfo;
        }
        Slog.e(TAG, "mClientModeImplChannel is not initialized");
        return null;
    }

    public ParceledListSlice<WifiConfiguration> getConfiguredNetworks(String packageName) {
        enforceAccessPermission();
        int callingUid = Binder.getCallingUid();
        if (!(callingUid == 2000 || callingUid == 0)) {
            long ident = Binder.clearCallingIdentity();
            try {
                this.mWifiPermissionsUtil.enforceCanAccessScanResults(packageName, callingUid);
            } catch (SecurityException e) {
                Slog.e(TAG, "Permission violation - getConfiguredNetworks not allowed for uid=" + callingUid + ", packageName=" + packageName + ", reason=" + e);
                return new ParceledListSlice<>(new ArrayList());
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }
        boolean isTargetSdkLessThanQOrPrivileged = isTargetSdkLessThanQOrPrivileged(packageName, Binder.getCallingPid(), callingUid);
        boolean z = true;
        if (this.mWifiInjector.makeTelephonyManager().checkCarrierPrivilegesForPackageAnyPhone(packageName) != 1) {
            z = false;
        }
        boolean isCarrierApp = z;
        if (isTargetSdkLessThanQOrPrivileged || isCarrierApp) {
            if (this.mVerboseLoggingEnabled) {
                this.mLog.info("getConfiguredNetworks uid=%").c((long) callingUid).flush();
            }
            int targetConfigUid = -1;
            if (isPrivileged(getCallingPid(), callingUid) || isDeviceOrProfileOwner(callingUid)) {
                targetConfigUid = 1010;
            } else if (isCarrierApp) {
                targetConfigUid = callingUid;
            }
            AsyncChannel asyncChannel = this.mClientModeImplChannel;
            if (asyncChannel != null) {
                List<WifiConfiguration> configs = this.mClientModeImpl.syncGetConfiguredNetworks(callingUid, asyncChannel, targetConfigUid);
                if (configs == null) {
                    return null;
                }
                if (isTargetSdkLessThanQOrPrivileged) {
                    return new ParceledListSlice<>(configs);
                }
                List<WifiConfiguration> creatorConfigs = new ArrayList<>();
                for (WifiConfiguration config : configs) {
                    if (config.creatorUid == callingUid) {
                        creatorConfigs.add(config);
                    }
                }
                return new ParceledListSlice<>(creatorConfigs);
            }
            Slog.e(TAG, "mClientModeImplChannel is not initialized");
            return null;
        }
        this.mLog.info("getConfiguredNetworks not allowed for uid=%").c((long) callingUid).flush();
        return new ParceledListSlice<>(new ArrayList());
    }

    public ParceledListSlice<WifiConfiguration> getPrivilegedConfiguredNetworks(String packageName) {
        enforceReadCredentialPermission();
        enforceAccessPermission();
        int callingUid = Binder.getCallingUid();
        long ident = Binder.clearCallingIdentity();
        try {
            this.mWifiPermissionsUtil.enforceCanAccessScanResults(packageName, callingUid);
            Binder.restoreCallingIdentity(ident);
            if (this.mVerboseLoggingEnabled) {
                this.mLog.info("getPrivilegedConfiguredNetworks uid=%").c((long) callingUid).flush();
            }
            AsyncChannel asyncChannel = this.mClientModeImplChannel;
            if (asyncChannel != null) {
                List<WifiConfiguration> configs = this.mClientModeImpl.syncGetPrivilegedConfiguredNetwork(asyncChannel);
                if (configs != null) {
                    return new ParceledListSlice<>(configs);
                }
            } else {
                Slog.e(TAG, "mClientModeImplChannel is not initialized");
            }
            return null;
        } catch (SecurityException e) {
            Slog.e(TAG, "Permission violation - getPrivilegedConfiguredNetworks not allowed for uid=" + callingUid + ", packageName=" + packageName + ", reason=" + e);
            Binder.restoreCallingIdentity(ident);
            return null;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
    }

    public Map<String, Map<Integer, List<ScanResult>>> getAllMatchingFqdnsForScanResults(List<ScanResult> scanResults) {
        if (isSettingsOrSuw(Binder.getCallingPid(), Binder.getCallingUid())) {
            if (this.mVerboseLoggingEnabled) {
                this.mLog.info("getMatchingPasspointConfigurations uid=%").c((long) Binder.getCallingUid()).flush();
            }
            if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.wifi.passpoint")) {
                return new HashMap();
            }
            return this.mClientModeImpl.syncGetAllMatchingFqdnsForScanResults(scanResults, this.mClientModeImplChannel);
        }
        throw new SecurityException("WifiService: Permission denied");
    }

    public Map<OsuProvider, List<ScanResult>> getMatchingOsuProviders(List<ScanResult> scanResults) {
        if (isSettingsOrSuw(Binder.getCallingPid(), Binder.getCallingUid())) {
            if (this.mVerboseLoggingEnabled) {
                this.mLog.info("getMatchingOsuProviders uid=%").c((long) Binder.getCallingUid()).flush();
            }
            if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.wifi.passpoint")) {
                return new HashMap();
            }
            return this.mClientModeImpl.syncGetMatchingOsuProviders(scanResults, this.mClientModeImplChannel);
        }
        throw new SecurityException("WifiService: Permission denied");
    }

    public Map<OsuProvider, PasspointConfiguration> getMatchingPasspointConfigsForOsuProviders(List<OsuProvider> osuProviders) {
        if (isSettingsOrSuw(Binder.getCallingPid(), Binder.getCallingUid())) {
            if (this.mVerboseLoggingEnabled) {
                this.mLog.info("getMatchingPasspointConfigsForOsuProviders uid=%").c((long) Binder.getCallingUid()).flush();
            }
            if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.wifi.passpoint")) {
                return new HashMap();
            }
            if (osuProviders != null) {
                return this.mClientModeImpl.syncGetMatchingPasspointConfigsForOsuProviders(osuProviders, this.mClientModeImplChannel);
            }
            Log.e(TAG, "Attempt to retrieve Passpoint configuration with null osuProviders");
            return new HashMap();
        }
        throw new SecurityException("WifiService: Permission denied");
    }

    public List<WifiConfiguration> getWifiConfigsForPasspointProfiles(List<String> fqdnList) {
        if (isSettingsOrSuw(Binder.getCallingPid(), Binder.getCallingUid())) {
            if (this.mVerboseLoggingEnabled) {
                this.mLog.info("getWifiConfigsForPasspointProfiles uid=%").c((long) Binder.getCallingUid()).flush();
            }
            if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.wifi.passpoint")) {
                return new ArrayList();
            }
            if (fqdnList != null) {
                return this.mClientModeImpl.syncGetWifiConfigsForPasspointProfiles(fqdnList, this.mClientModeImplChannel);
            }
            Log.e(TAG, "Attempt to retrieve WifiConfiguration with null fqdn List");
            return new ArrayList();
        }
        throw new SecurityException("WifiService: Permission denied");
    }

    public int addOrUpdateNetwork(WifiConfiguration config, String packageName) {
        if (enforceChangePermission(packageName) != 0) {
            return -1;
        }
        if (!isTargetSdkLessThanQOrPrivileged(packageName, Binder.getCallingPid(), Binder.getCallingUid())) {
            this.mLog.info("addOrUpdateNetwork not allowed for uid=%").c((long) Binder.getCallingUid()).flush();
            return -1;
        }
        this.mLog.info("addOrUpdateNetwork uid=%").c((long) Binder.getCallingUid()).flush();
        if (config == null) {
            Slog.e(TAG, "bad network configuration");
            return -1;
        }
        this.mWifiMetrics.incrementNumAddOrUpdateNetworkCalls();
        if (config.isPasspoint()) {
            PasspointConfiguration passpointConfig = PasspointProvider.convertFromWifiConfig(config);
            if (passpointConfig.getCredential() == null) {
                Slog.e(TAG, "Missing credential for Passpoint profile");
                return -1;
            }
            X509Certificate[] x509Certificates = null;
            if (config.enterpriseConfig.getCaCertificate() != null) {
                x509Certificates = new X509Certificate[]{config.enterpriseConfig.getCaCertificate()};
            }
            passpointConfig.getCredential().setCaCertificates(x509Certificates);
            passpointConfig.getCredential().setClientCertificateChain(config.enterpriseConfig.getClientCertificateChain());
            passpointConfig.getCredential().setClientPrivateKey(config.enterpriseConfig.getClientPrivateKey());
            if (addOrUpdatePasspointConfiguration(passpointConfig, packageName)) {
                return 0;
            }
            Slog.e(TAG, "Failed to add Passpoint profile");
            return -1;
        }
        Slog.i("addOrUpdateNetwork", " uid = " + Integer.toString(Binder.getCallingUid()) + " SSID " + config.SSID + " nid=" + Integer.toString(config.networkId));
        if (config.networkId == -1) {
            config.creatorUid = Binder.getCallingUid();
        } else {
            config.lastUpdateUid = Binder.getCallingUid();
        }
        AsyncChannel asyncChannel = this.mClientModeImplChannel;
        if (asyncChannel != null) {
            return this.mClientModeImpl.syncAddOrUpdateNetwork(asyncChannel, config);
        }
        Slog.e(TAG, "mClientModeImplChannel is not initialized");
        return -1;
    }

    public static void verifyCert(X509Certificate caCert) throws GeneralSecurityException, IOException {
        CertificateFactory factory = CertificateFactory.getInstance("X.509");
        CertPathValidator validator = CertPathValidator.getInstance(CertPathValidator.getDefaultType());
        CertPath path = factory.generateCertPath(Arrays.asList(new X509Certificate[]{caCert}));
        KeyStore ks = KeyStore.getInstance("AndroidCAStore");
        ks.load((InputStream) null, (char[]) null);
        PKIXParameters params = new PKIXParameters(ks);
        params.setRevocationEnabled(false);
        validator.validate(path, params);
    }

    public boolean removeNetwork(int netId, String packageName) {
        if (enforceChangePermission(packageName) != 0) {
            return false;
        }
        if (!isTargetSdkLessThanQOrPrivileged(packageName, Binder.getCallingPid(), Binder.getCallingUid())) {
            this.mLog.info("removeNetwork not allowed for uid=%").c((long) Binder.getCallingUid()).flush();
            return false;
        }
        this.mLog.info("removeNetwork uid=%").c((long) Binder.getCallingUid()).flush();
        AsyncChannel asyncChannel = this.mClientModeImplChannel;
        if (asyncChannel != null) {
            return this.mClientModeImpl.syncRemoveNetwork(asyncChannel, netId);
        }
        Slog.e(TAG, "mClientModeImplChannel is not initialized");
        return false;
    }

    public boolean enableNetwork(int netId, boolean disableOthers, String packageName) {
        if (enforceChangePermission(packageName) != 0) {
            return false;
        }
        if (!isTargetSdkLessThanQOrPrivileged(packageName, Binder.getCallingPid(), Binder.getCallingUid())) {
            this.mLog.info("enableNetwork not allowed for uid=%").c((long) Binder.getCallingUid()).flush();
            return false;
        }
        this.mLog.info("enableNetwork uid=% disableOthers=%").c((long) Binder.getCallingUid()).c(disableOthers).flush();
        this.mWifiMetrics.incrementNumEnableNetworkCalls();
        AsyncChannel asyncChannel = this.mClientModeImplChannel;
        if (asyncChannel != null) {
            return this.mClientModeImpl.syncEnableNetwork(asyncChannel, netId, disableOthers);
        }
        Slog.e(TAG, "mClientModeImplChannel is not initialized");
        return false;
    }

    public boolean disableNetwork(int netId, String packageName) {
        if (enforceChangePermission(packageName) != 0) {
            return false;
        }
        if (!isTargetSdkLessThanQOrPrivileged(packageName, Binder.getCallingPid(), Binder.getCallingUid())) {
            this.mLog.info("disableNetwork not allowed for uid=%").c((long) Binder.getCallingUid()).flush();
            return false;
        } else if (!MiuiWifiService.enforceChangePermission(this.mContext)) {
            Slog.d(TAG, "refuse disableNetwork: netId =" + netId + ", uid=" + Binder.getCallingUid());
            return false;
        } else {
            this.mLog.info("disableNetwork uid=%").c((long) Binder.getCallingUid()).flush();
            AsyncChannel asyncChannel = this.mClientModeImplChannel;
            if (asyncChannel != null) {
                return this.mClientModeImpl.syncDisableNetwork(asyncChannel, netId);
            }
            Slog.e(TAG, "mClientModeImplChannel is not initialized");
            return false;
        }
    }

    public WifiInfo getConnectionInfo(String callingPackage) {
        WifiInfo result;
        boolean hideDefaultMacAddress;
        boolean hideBssidSsidAndNetworkId;
        enforceAccessPermission();
        int uid = Binder.getCallingUid();
        if (this.mVerboseLoggingEnabled) {
            this.mLog.info("getConnectionInfo uid=%").c((long) uid).flush();
        }
        long ident = Binder.clearCallingIdentity();
        try {
            result = this.mClientModeImpl.syncRequestConnectionInfo();
            hideDefaultMacAddress = true;
            hideBssidSsidAndNetworkId = true;
            if (this.mWifiInjector.getWifiPermissionsWrapper().getLocalMacAddressPermission(uid) == 0) {
                hideDefaultMacAddress = false;
            }
            this.mWifiPermissionsUtil.enforceCanAccessScanResults(callingPackage, uid);
            hideBssidSsidAndNetworkId = false;
        } catch (RemoteException e) {
            Log.e(TAG, "Error checking receiver permission", e);
        } catch (SecurityException e2) {
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
        if (hideDefaultMacAddress) {
            result.setMacAddress("02:00:00:00:00:00");
        }
        if (hideBssidSsidAndNetworkId) {
            result.setBSSID("02:00:00:00:00:00");
            result.setSSID(WifiSsid.createFromHex((String) null));
            result.setNetworkId(-1);
        }
        if (this.mVerboseLoggingEnabled && (hideBssidSsidAndNetworkId || hideDefaultMacAddress)) {
            WifiLog wifiLog = this.mLog;
            wifiLog.v("getConnectionInfo: hideBssidSsidAndNetworkId=" + hideBssidSsidAndNetworkId + ", hideDefaultMacAddress=" + hideDefaultMacAddress);
        }
        Binder.restoreCallingIdentity(ident);
        return result;
    }

    public List<ScanResult> getScanResults(String callingPackage) {
        enforceAccessPermission();
        int uid = Binder.getCallingUid();
        long ident = Binder.clearCallingIdentity();
        if (this.mVerboseLoggingEnabled) {
            this.mLog.info("getScanResults uid=%").c((long) uid).flush();
        }
        try {
            this.mWifiPermissionsUtil.enforceCanAccessScanResults(callingPackage, uid);
            List<ScanResult> scanResults = new ArrayList<>();
            if (!this.mWifiInjector.getClientModeImplHandler().runWithScissors(new Runnable(scanResults) {
                private final /* synthetic */ List f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    WifiServiceImpl.this.lambda$getScanResults$8$WifiServiceImpl(this.f$1);
                }
            }, 4000)) {
                Log.e(TAG, "Failed to post runnable to fetch scan results");
                return new ArrayList();
            }
            Binder.restoreCallingIdentity(ident);
            return scanResults;
        } catch (SecurityException e) {
            Slog.e(TAG, "Permission violation - getScanResults not allowed for uid=" + uid + ", packageName=" + callingPackage + ", reason=" + e);
            return new ArrayList();
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    public /* synthetic */ void lambda$getScanResults$8$WifiServiceImpl(List scanResults) {
        scanResults.addAll(this.mScanRequestProxy.getScanResults());
    }

    public boolean addOrUpdatePasspointConfiguration(PasspointConfiguration config, String packageName) {
        if (enforceChangePermission(packageName) != 0) {
            return false;
        }
        this.mLog.info("addorUpdatePasspointConfiguration uid=%").c((long) Binder.getCallingUid()).flush();
        if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.wifi.passpoint")) {
            return false;
        }
        return this.mClientModeImpl.syncAddOrUpdatePasspointConfig(this.mClientModeImplChannel, config, Binder.getCallingUid(), packageName);
    }

    public boolean removePasspointConfiguration(String fqdn, String packageName) {
        int uid = Binder.getCallingUid();
        if (this.mWifiPermissionsUtil.checkNetworkSettingsPermission(uid) || this.mWifiPermissionsUtil.checkNetworkCarrierProvisioningPermission(uid)) {
            this.mLog.info("removePasspointConfiguration uid=%").c((long) Binder.getCallingUid()).flush();
            if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.wifi.passpoint")) {
                return false;
            }
            return this.mClientModeImpl.syncRemovePasspointConfig(this.mClientModeImplChannel, fqdn);
        } else if (this.mWifiPermissionsUtil.isTargetSdkLessThan(packageName, 29)) {
            return false;
        } else {
            throw new SecurityException("WifiService: Permission denied");
        }
    }

    public List<PasspointConfiguration> getPasspointConfigurations(String packageName) {
        int uid = Binder.getCallingUid();
        this.mAppOps.checkPackage(uid, packageName);
        if (this.mWifiPermissionsUtil.checkNetworkSettingsPermission(uid) || this.mWifiPermissionsUtil.checkNetworkSetupWizardPermission(uid)) {
            if (this.mVerboseLoggingEnabled) {
                this.mLog.info("getPasspointConfigurations uid=%").c((long) Binder.getCallingUid()).flush();
            }
            if (!this.mContext.getPackageManager().hasSystemFeature("android.hardware.wifi.passpoint")) {
                return new ArrayList();
            }
            return this.mClientModeImpl.syncGetPasspointConfigs(this.mClientModeImplChannel);
        } else if (this.mWifiPermissionsUtil.isTargetSdkLessThan(packageName, 29)) {
            return new ArrayList();
        } else {
            throw new SecurityException("WifiService: Permission denied");
        }
    }

    public void queryPasspointIcon(long bssid, String fileName) {
        enforceAccessPermission();
        this.mLog.info("queryPasspointIcon uid=%").c((long) Binder.getCallingUid()).flush();
        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.wifi.passpoint")) {
            this.mClientModeImpl.syncQueryPasspointIcon(this.mClientModeImplChannel, bssid, fileName);
            return;
        }
        throw new UnsupportedOperationException("Passpoint not enabled");
    }

    public int matchProviderWithCurrentNetwork(String fqdn) {
        this.mLog.info("matchProviderWithCurrentNetwork uid=%").c((long) Binder.getCallingUid()).flush();
        return this.mClientModeImpl.matchProviderWithCurrentNetwork(this.mClientModeImplChannel, fqdn);
    }

    public void deauthenticateNetwork(long holdoff, boolean ess) {
        this.mLog.info("deauthenticateNetwork uid=%").c((long) Binder.getCallingUid()).flush();
        this.mClientModeImpl.deauthenticateNetwork(this.mClientModeImplChannel, holdoff, ess);
    }

    public String getCapabilities(String capaType) {
        return this.mClientModeImpl.getCapabilities(capaType);
    }

    public void setCountryCode(String countryCode) {
        Slog.i(TAG, "WifiService trying to set country code to " + countryCode);
        enforceConnectivityInternalPermission();
        this.mLog.info("setCountryCode uid=%").c((long) Binder.getCallingUid()).flush();
        long token = Binder.clearCallingIdentity();
        this.mCountryCode.setCountryCode(countryCode);
        Binder.restoreCallingIdentity(token);
    }

    public String getCountryCode() {
        enforceConnectivityInternalPermission();
        if (this.mVerboseLoggingEnabled) {
            this.mLog.info("getCountryCode uid=%").c((long) Binder.getCallingUid()).flush();
        }
        return this.mCountryCode.getCountryCode();
    }

    public boolean isDualBandSupported() {
        if (this.mVerboseLoggingEnabled) {
            this.mLog.info("isDualBandSupported uid=%").c((long) Binder.getCallingUid()).flush();
        }
        return this.mContext.getResources().getBoolean(17891585);
    }

    private int getMaxApInterfacesCount() {
        return this.mContext.getResources().getInteger(17694958);
    }

    private boolean isConcurrentLohsAndTetheringSupported() {
        return getMaxApInterfacesCount() >= 2;
    }

    public boolean needs5GHzToAnyApBandConversion() {
        enforceNetworkSettingsPermission();
        if (this.mVerboseLoggingEnabled) {
            this.mLog.info("needs5GHzToAnyApBandConversion uid=%").c((long) Binder.getCallingUid()).flush();
        }
        return this.mContext.getResources().getBoolean(17891583);
    }

    @Deprecated
    public DhcpInfo getDhcpInfo() {
        enforceAccessPermission();
        if (this.mVerboseLoggingEnabled) {
            this.mLog.info("getDhcpInfo uid=%").c((long) Binder.getCallingUid()).flush();
        }
        DhcpResults dhcpResults = this.mClientModeImpl.syncGetDhcpResults();
        DhcpInfo info = new DhcpInfo();
        if (dhcpResults.ipAddress != null && (dhcpResults.ipAddress.getAddress() instanceof Inet4Address)) {
            info.ipAddress = NetworkUtils.inetAddressToInt((Inet4Address) dhcpResults.ipAddress.getAddress());
            info.netmask = NetworkUtils.prefixLengthToNetmaskInt(dhcpResults.ipAddress.getNetworkPrefixLength());
        }
        if (dhcpResults.gateway != null) {
            info.gateway = NetworkUtils.inetAddressToInt((Inet4Address) dhcpResults.gateway);
        }
        int dnsFound = 0;
        Iterator it = dhcpResults.dnsServers.iterator();
        while (it.hasNext()) {
            InetAddress dns = (InetAddress) it.next();
            if (dns instanceof Inet4Address) {
                if (dnsFound == 0) {
                    info.dns1 = NetworkUtils.inetAddressToInt((Inet4Address) dns);
                } else {
                    info.dns2 = NetworkUtils.inetAddressToInt((Inet4Address) dns);
                }
                dnsFound++;
                if (dnsFound > 1) {
                    break;
                }
            }
        }
        Inet4Address serverAddress = dhcpResults.serverAddress;
        if (serverAddress != null) {
            info.serverAddress = NetworkUtils.inetAddressToInt(serverAddress);
        }
        info.leaseDuration = dhcpResults.leaseDuration;
        return info;
    }

    class TdlsTaskParams {
        public boolean enable;
        public String remoteIpAddress;

        TdlsTaskParams() {
        }
    }

    class TdlsTask extends AsyncTask<TdlsTaskParams, Integer, Integer> {
        TdlsTask() {
        }

        /* access modifiers changed from: protected */
        public Integer doInBackground(TdlsTaskParams... params) {
            TdlsTaskParams param = params[0];
            String remoteIpAddress = param.remoteIpAddress.trim();
            boolean enable = param.enable;
            String macAddress = null;
            BufferedReader reader = null;
            try {
                BufferedReader reader2 = new BufferedReader(new FileReader("/proc/net/arp"));
                String readLine = reader2.readLine();
                while (true) {
                    String readLine2 = reader2.readLine();
                    String line = readLine2;
                    if (readLine2 == null) {
                        break;
                    }
                    String[] tokens = line.split("[ ]+");
                    if (tokens.length >= 6) {
                        String ip = tokens[0];
                        String mac = tokens[3];
                        if (remoteIpAddress.equals(ip)) {
                            macAddress = mac;
                            break;
                        }
                    }
                }
                if (macAddress == null) {
                    Slog.w(WifiServiceImpl.TAG, "Did not find remoteAddress {" + remoteIpAddress + "} in /proc/net/arp");
                } else {
                    WifiServiceImpl.this.enableTdlsWithMacAddress(macAddress, enable);
                }
                try {
                    reader2.close();
                } catch (IOException e) {
                }
            } catch (FileNotFoundException e2) {
                Slog.e(WifiServiceImpl.TAG, "Could not open /proc/net/arp to lookup mac address");
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e3) {
                Slog.e(WifiServiceImpl.TAG, "Could not read /proc/net/arp to lookup mac address");
                if (reader != null) {
                    reader.close();
                }
            } catch (Throwable th) {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e4) {
                    }
                }
                throw th;
            }
            return 0;
        }
    }

    public void enableTdls(String remoteAddress, boolean enable) {
        if (remoteAddress != null) {
            this.mLog.info("enableTdls uid=% enable=%").c((long) Binder.getCallingUid()).c(enable).flush();
            TdlsTaskParams params = new TdlsTaskParams();
            params.remoteIpAddress = remoteAddress;
            params.enable = enable;
            new TdlsTask().execute(new TdlsTaskParams[]{params});
            return;
        }
        throw new IllegalArgumentException("remoteAddress cannot be null");
    }

    public void enableTdlsWithMacAddress(String remoteMacAddress, boolean enable) {
        this.mLog.info("enableTdlsWithMacAddress uid=% enable=%").c((long) Binder.getCallingUid()).c(enable).flush();
        if (remoteMacAddress != null) {
            this.mClientModeImpl.enableTdls(remoteMacAddress, enable);
            return;
        }
        throw new IllegalArgumentException("remoteMacAddress cannot be null");
    }

    public Messenger getWifiServiceMessenger(String packageName) {
        enforceAccessPermission();
        if (enforceChangePermission(packageName) == 0) {
            this.mLog.info("getWifiServiceMessenger uid=%").c((long) Binder.getCallingUid()).flush();
            return new Messenger(this.mAsyncChannelExternalClientHandler);
        }
        throw new SecurityException("Could not create wifi service messenger");
    }

    public void disableEphemeralNetwork(String SSID, String packageName) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CHANGE_WIFI_STATE", TAG);
        if (!isPrivileged(Binder.getCallingPid(), Binder.getCallingUid())) {
            this.mLog.info("disableEphemeralNetwork not allowed for uid=%").c((long) Binder.getCallingUid()).flush();
            return;
        }
        this.mLog.info("disableEphemeralNetwork uid=%").c((long) Binder.getCallingUid()).flush();
        this.mClientModeImpl.disableEphemeralNetwork(SSID);
    }

    private void registerForScanModeChange() {
        this.mFrameworkFacade.registerContentObserver(this.mContext, Settings.Global.getUriFor("wifi_scan_always_enabled"), false, new ContentObserver((Handler) null) {
            public void onChange(boolean selfChange) {
                WifiServiceImpl.this.mSettingsStore.handleWifiScanAlwaysAvailableToggled();
                WifiServiceImpl.this.mWifiController.sendMessage(155655);
            }
        });
    }

    private void registerForBroadcasts() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_PRESENT");
        intentFilter.addAction("android.intent.action.USER_REMOVED");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        intentFilter.addAction("android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED");
        intentFilter.addAction("android.intent.action.EMERGENCY_CALLBACK_MODE_CHANGED");
        intentFilter.addAction("android.os.action.DEVICE_IDLE_MODE_CHANGED");
        if (this.mContext.getResources().getBoolean(17891604)) {
            intentFilter.addAction("android.intent.action.EMERGENCY_CALL_STATE_CHANGED");
        }
        this.mContext.registerReceiver(this.mReceiver, intentFilter);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.PACKAGE_FULLY_REMOVED");
        intentFilter2.addDataScheme("package");
        this.mContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("android.intent.action.PACKAGE_FULLY_REMOVED")) {
                    int uid = intent.getIntExtra("android.intent.extra.UID", -1);
                    Uri uri = intent.getData();
                    if (uid != -1 && uri != null) {
                        String pkgName = uri.getSchemeSpecificPart();
                        WifiServiceImpl.this.mClientModeImpl.removeAppConfigs(pkgName, uid);
                        WifiServiceImpl.this.mWifiInjector.getClientModeImplHandler().post(new Runnable(pkgName, uid) {
                            private final /* synthetic */ String f$1;
                            private final /* synthetic */ int f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run() {
                                WifiServiceImpl.AnonymousClass7.this.lambda$onReceive$0$WifiServiceImpl$7(this.f$1, this.f$2);
                            }
                        });
                    }
                }
            }

            public /* synthetic */ void lambda$onReceive$0$WifiServiceImpl$7(String pkgName, int uid) {
                WifiServiceImpl.this.mScanRequestProxy.clearScanRequestTimestampsForApp(pkgName, uid);
                WifiServiceImpl.this.mWifiNetworkSuggestionsManager.removeApp(pkgName);
                WifiServiceImpl.this.mClientModeImpl.removeNetworkRequestUserApprovedAccessPointsForApp(pkgName);
                WifiServiceImpl.this.mWifiInjector.getPasspointManager().removePasspointProviderWithPackage(pkgName);
            }
        }, intentFilter2);
    }

    /* JADX WARNING: type inference failed for: r1v1, types: [android.os.Binder] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onShellCommand(java.io.FileDescriptor r9, java.io.FileDescriptor r10, java.io.FileDescriptor r11, java.lang.String[] r12, android.os.ShellCallback r13, android.os.ResultReceiver r14) {
        /*
            r8 = this;
            com.android.server.wifi.WifiShellCommand r0 = new com.android.server.wifi.WifiShellCommand
            com.android.server.wifi.WifiInjector r1 = r8.mWifiInjector
            r0.<init>(r1)
            r1 = r8
            r2 = r9
            r3 = r10
            r4 = r11
            r5 = r12
            r6 = r13
            r7 = r14
            r0.exec(r1, r2, r3, r4, r5, r6, r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiServiceImpl.onShellCommand(java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):void");
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0) {
            pw.println("Permission Denial: can't dump WifiService from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
        } else if (args != null && args.length > 0 && WifiMetrics.PROTO_DUMP_ARG.equals(args[0])) {
            this.mClientModeImpl.updateWifiMetrics();
            this.mWifiMetrics.dump(fd, pw, args);
        } else if (args != null && args.length > 0 && "ipclient".equals(args[0])) {
            String[] ipClientArgs = new String[(args.length - 1)];
            System.arraycopy(args, 1, ipClientArgs, 0, ipClientArgs.length);
            this.mClientModeImpl.dumpIpClient(fd, pw, ipClientArgs);
        } else if (args != null && args.length > 0 && WifiScoreReport.DUMP_ARG.equals(args[0])) {
            WifiScoreReport wifiScoreReport = this.mClientModeImpl.getWifiScoreReport();
            if (wifiScoreReport != null) {
                wifiScoreReport.dump(fd, pw, args);
            }
        } else if (args == null || args.length <= 0 || !WifiScoreCard.DUMP_ARG.equals(args[0])) {
            this.mClientModeImpl.updateLinkLayerStatsRssiAndScoreReport();
            pw.println("Wi-Fi is " + this.mClientModeImpl.syncGetWifiStateByName());
            StringBuilder sb = new StringBuilder();
            sb.append("Verbose logging is ");
            sb.append(this.mVerboseLoggingEnabled ? "on" : "off");
            pw.println(sb.toString());
            pw.println("Stay-awake conditions: " + this.mFacade.getIntegerSetting(this.mContext, "stay_on_while_plugged_in", 0));
            pw.println("mInIdleMode " + this.mInIdleMode);
            pw.println("mScanPending " + this.mScanPending);
            this.mWifiController.dump(fd, pw, args);
            this.mSettingsStore.dump(fd, pw, args);
            this.mWifiTrafficPoller.dump(fd, pw, args);
            pw.println();
            pw.println("Locks held:");
            this.mWifiLockManager.dump(pw);
            pw.println();
            this.mWifiMulticastLockManager.dump(pw);
            pw.println();
            this.mActiveModeWarden.dump(fd, pw, args);
            pw.println();
            this.mClientModeImpl.dump(fd, pw, args);
            pw.println();
            this.mWifiInjector.getClientModeImplHandler().runWithScissors(new Runnable(pw) {
                private final /* synthetic */ PrintWriter f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    WifiServiceImpl.this.lambda$dump$10$WifiServiceImpl(this.f$1);
                }
            }, 4000);
            this.mClientModeImpl.updateWifiMetrics();
            this.mWifiMetrics.dump(fd, pw, args);
            pw.println();
            this.mWifiInjector.getClientModeImplHandler().runWithScissors(new Runnable(fd, pw, args) {
                private final /* synthetic */ FileDescriptor f$1;
                private final /* synthetic */ PrintWriter f$2;
                private final /* synthetic */ String[] f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    WifiServiceImpl.this.lambda$dump$11$WifiServiceImpl(this.f$1, this.f$2, this.f$3);
                }
            }, 4000);
            this.mWifiBackupRestore.dump(fd, pw, args);
            pw.println();
            pw.println("ScoringParams: settings put global wifi_score_params " + this.mWifiInjector.getScoringParams());
            pw.println();
            WifiScoreReport wifiScoreReport2 = this.mClientModeImpl.getWifiScoreReport();
            if (wifiScoreReport2 != null) {
                pw.println("WifiScoreReport:");
                wifiScoreReport2.dump(fd, pw, args);
            }
            pw.println();
            SarManager sarManager = this.mWifiInjector.getSarManager();
            if (sarManager != null) {
                sarManager.dump(fd, pw, args);
            }
            pw.println();
        } else {
            this.mWifiInjector.getClientModeImplHandler().runWithScissors(new Runnable(pw) {
                private final /* synthetic */ PrintWriter f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    WifiServiceImpl.this.lambda$dump$9$WifiServiceImpl(this.f$1);
                }
            }, 4000);
        }
    }

    public /* synthetic */ void lambda$dump$9$WifiServiceImpl(PrintWriter pw) {
        WifiScoreCard wifiScoreCard = this.mWifiInjector.getWifiScoreCard();
        if (wifiScoreCard != null) {
            pw.println(wifiScoreCard.getNetworkListBase64(true));
        }
    }

    public /* synthetic */ void lambda$dump$10$WifiServiceImpl(PrintWriter pw) {
        WifiScoreCard wifiScoreCard = this.mWifiInjector.getWifiScoreCard();
        if (wifiScoreCard != null) {
            pw.println("WifiScoreCard:");
            pw.println(wifiScoreCard.getNetworkListBase64(true));
        }
    }

    public /* synthetic */ void lambda$dump$11$WifiServiceImpl(FileDescriptor fd, PrintWriter pw, String[] args) {
        this.mWifiNetworkSuggestionsManager.dump(fd, pw, args);
        pw.println();
    }

    public boolean acquireWifiLock(IBinder binder, int lockMode, String tag, WorkSource ws) {
        this.mLog.info("acquireWifiLock uid=% lockMode=%").c((long) Binder.getCallingUid()).c((long) lockMode).flush();
        this.mContext.enforceCallingOrSelfPermission("android.permission.WAKE_LOCK", (String) null);
        WorkSource updatedWs = (ws == null || ws.isEmpty()) ? new WorkSource(Binder.getCallingUid()) : ws;
        GeneralUtil.Mutable<Boolean> lockSuccess = new GeneralUtil.Mutable<>();
        if (this.mWifiInjector.getClientModeImplHandler().runWithScissors(new Runnable(lockSuccess, lockMode, tag, binder, updatedWs) {
            private final /* synthetic */ GeneralUtil.Mutable f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ String f$3;
            private final /* synthetic */ IBinder f$4;
            private final /* synthetic */ WorkSource f$5;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
            }

            public final void run() {
                WifiServiceImpl.this.lambda$acquireWifiLock$12$WifiServiceImpl(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5);
            }
        }, 4000)) {
            return ((Boolean) lockSuccess.value).booleanValue();
        }
        Log.e(TAG, "Failed to post runnable to acquireWifiLock");
        return false;
    }

    public /* synthetic */ void lambda$acquireWifiLock$12$WifiServiceImpl(GeneralUtil.Mutable lockSuccess, int lockMode, String tag, IBinder binder, WorkSource updatedWs) {
        lockSuccess.value = Boolean.valueOf(this.mWifiLockManager.acquireWifiLock(lockMode, tag, binder, updatedWs));
    }

    public void updateWifiLockWorkSource(IBinder binder, WorkSource ws) {
        this.mLog.info("updateWifiLockWorkSource uid=%").c((long) Binder.getCallingUid()).flush();
        this.mContext.enforceCallingOrSelfPermission("android.permission.UPDATE_DEVICE_STATS", (String) null);
        if (!this.mWifiInjector.getClientModeImplHandler().runWithScissors(new Runnable(binder, (ws == null || ws.isEmpty()) ? new WorkSource(Binder.getCallingUid()) : ws) {
            private final /* synthetic */ IBinder f$1;
            private final /* synthetic */ WorkSource f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                WifiServiceImpl.this.lambda$updateWifiLockWorkSource$13$WifiServiceImpl(this.f$1, this.f$2);
            }
        }, 4000)) {
            Log.e(TAG, "Failed to post runnable to updateWifiLockWorkSource");
        }
    }

    public /* synthetic */ void lambda$updateWifiLockWorkSource$13$WifiServiceImpl(IBinder binder, WorkSource updatedWs) {
        this.mWifiLockManager.updateWifiLockWorkSource(binder, updatedWs);
    }

    public boolean releaseWifiLock(IBinder binder) {
        this.mLog.info("releaseWifiLock uid=%").c((long) Binder.getCallingUid()).flush();
        this.mContext.enforceCallingOrSelfPermission("android.permission.WAKE_LOCK", (String) null);
        GeneralUtil.Mutable<Boolean> lockSuccess = new GeneralUtil.Mutable<>();
        if (this.mWifiInjector.getClientModeImplHandler().runWithScissors(new Runnable(lockSuccess, binder) {
            private final /* synthetic */ GeneralUtil.Mutable f$1;
            private final /* synthetic */ IBinder f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                WifiServiceImpl.this.lambda$releaseWifiLock$14$WifiServiceImpl(this.f$1, this.f$2);
            }
        }, 4000)) {
            return ((Boolean) lockSuccess.value).booleanValue();
        }
        Log.e(TAG, "Failed to post runnable to releaseWifiLock");
        return false;
    }

    public /* synthetic */ void lambda$releaseWifiLock$14$WifiServiceImpl(GeneralUtil.Mutable lockSuccess, IBinder binder) {
        lockSuccess.value = Boolean.valueOf(this.mWifiLockManager.releaseWifiLock(binder));
    }

    public void initializeMulticastFiltering() {
        enforceMulticastChangePermission();
        this.mLog.info("initializeMulticastFiltering uid=%").c((long) Binder.getCallingUid()).flush();
        this.mWifiMulticastLockManager.initializeFiltering();
    }

    public void acquireMulticastLock(IBinder binder, String tag) {
        enforceMulticastChangePermission();
        this.mLog.info("acquireMulticastLock uid=%").c((long) Binder.getCallingUid()).flush();
        this.mWifiMulticastLockManager.acquireLock(binder, tag);
    }

    public void releaseMulticastLock(String tag) {
        enforceMulticastChangePermission();
        this.mLog.info("releaseMulticastLock uid=%").c((long) Binder.getCallingUid()).flush();
        this.mWifiMulticastLockManager.releaseLock(tag);
    }

    public boolean isMulticastEnabled() {
        enforceAccessPermission();
        if (this.mVerboseLoggingEnabled) {
            this.mLog.info("isMulticastEnabled uid=%").c((long) Binder.getCallingUid()).flush();
        }
        return this.mWifiMulticastLockManager.isMulticastEnabled();
    }

    public void enableVerboseLogging(int verbose) {
        enforceAccessPermission();
        enforceNetworkSettingsPermission();
        this.mLog.info("enableVerboseLogging uid=% verbose=%").c((long) Binder.getCallingUid()).c((long) verbose).flush();
        this.mFacade.setIntegerSetting(this.mContext, "wifi_verbose_logging_enabled", verbose);
        enableVerboseLoggingInternal(verbose);
    }

    /* access modifiers changed from: package-private */
    public void enableVerboseLoggingInternal(int verbose) {
        this.mVerboseLoggingEnabled = verbose > 0;
        this.mClientModeImpl.enableVerboseLogging(verbose);
        this.mWifiLockManager.enableVerboseLogging(verbose);
        this.mWifiMulticastLockManager.enableVerboseLogging(verbose);
        this.mWifiInjector.enableVerboseLogging(verbose);
    }

    public int getVerboseLoggingLevel() {
        if (this.mVerboseLoggingEnabled) {
            this.mLog.info("getVerboseLoggingLevel uid=%").c((long) Binder.getCallingUid()).flush();
        }
        return this.mFacade.getIntegerSetting(this.mContext, "wifi_verbose_logging_enabled", 0);
    }

    public void factoryReset(String packageName) {
        List<PasspointConfiguration> configs;
        enforceConnectivityInternalPermission();
        if (enforceChangePermission(packageName) == 0) {
            this.mLog.info("factoryReset uid=%").c((long) Binder.getCallingUid()).flush();
            if (!this.mUserManager.hasUserRestriction("no_network_reset")) {
                if (!this.mUserManager.hasUserRestriction("no_config_tethering")) {
                    stopSoftApInternal(-1);
                }
                if (!this.mUserManager.hasUserRestriction("no_config_wifi")) {
                    if (this.mClientModeImplChannel != null) {
                        List<WifiConfiguration> networks = this.mClientModeImpl.syncGetConfiguredNetworks(Binder.getCallingUid(), this.mClientModeImplChannel, 1010);
                        if (networks != null) {
                            for (WifiConfiguration config : networks) {
                                removeNetwork(config.networkId, packageName);
                            }
                        }
                        if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.wifi.passpoint") && (configs = this.mClientModeImpl.syncGetPasspointConfigs(this.mClientModeImplChannel)) != null) {
                            for (PasspointConfiguration config2 : configs) {
                                removePasspointConfiguration(config2.getHomeSp().getFqdn(), packageName);
                            }
                        }
                    }
                    this.mWifiInjector.getClientModeImplHandler().post(new Runnable() {
                        public final void run() {
                            WifiServiceImpl.this.lambda$factoryReset$15$WifiServiceImpl();
                        }
                    });
                }
            }
        }
    }

    public /* synthetic */ void lambda$factoryReset$15$WifiServiceImpl() {
        this.mWifiInjector.getWifiConfigManager().clearDeletedEphemeralNetworks();
        this.mClientModeImpl.clearNetworkRequestUserApprovedAccessPoints();
        this.mWifiNetworkSuggestionsManager.clear();
        this.mWifiInjector.getWifiScoreCard().clear();
    }

    static boolean logAndReturnFalse(String s) {
        Log.d(TAG, s);
        return false;
    }

    public Network getCurrentNetwork() {
        enforceAccessPermission();
        if (this.mVerboseLoggingEnabled) {
            this.mLog.info("getCurrentNetwork uid=%").c((long) Binder.getCallingUid()).flush();
        }
        return this.mClientModeImpl.getCurrentNetwork();
    }

    public static String toHexString(String s) {
        if (s == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        sb.append('\'');
        sb.append(s);
        sb.append('\'');
        for (int n = 0; n < s.length(); n++) {
            sb.append(String.format(" %02x", new Object[]{Integer.valueOf(s.charAt(n) & 65535)}));
        }
        return sb.toString();
    }

    public void enableWifiConnectivityManager(boolean enabled) {
        enforceConnectivityInternalPermission();
        this.mLog.info("enableWifiConnectivityManager uid=% enabled=%").c((long) Binder.getCallingUid()).c(enabled).flush();
        this.mClientModeImpl.enableWifiConnectivityManager(enabled);
    }

    public byte[] retrieveBackupData() {
        enforceNetworkSettingsPermission();
        this.mLog.info("retrieveBackupData uid=%").c((long) Binder.getCallingUid()).flush();
        if (this.mClientModeImplChannel == null) {
            Slog.e(TAG, "mClientModeImplChannel is not initialized");
            return null;
        }
        Slog.d(TAG, "Retrieving backup data");
        byte[] backupData = this.mWifiBackupRestore.retrieveBackupDataFromConfigurations(this.mClientModeImpl.syncGetPrivilegedConfiguredNetwork(this.mClientModeImplChannel));
        Slog.d(TAG, "Retrieved backup data");
        return backupData;
    }

    private void restoreNetworks(List<WifiConfiguration> configurations) {
        if (configurations == null) {
            Slog.e(TAG, "Backup data parse failed");
            return;
        }
        for (WifiConfiguration configuration : configurations) {
            int networkId = this.mClientModeImpl.syncAddOrUpdateNetwork(this.mClientModeImplChannel, configuration);
            if (networkId == -1) {
                Slog.e(TAG, "Restore network failed: " + configuration.configKey());
            } else {
                this.mClientModeImpl.syncEnableNetwork(this.mClientModeImplChannel, networkId, false);
            }
        }
    }

    public void restoreBackupData(byte[] data) {
        enforceNetworkSettingsPermission();
        this.mLog.info("restoreBackupData uid=%").c((long) Binder.getCallingUid()).flush();
        if (this.mClientModeImplChannel == null) {
            Slog.e(TAG, "mClientModeImplChannel is not initialized");
            return;
        }
        Slog.d(TAG, "Restoring backup data");
        restoreNetworks(this.mWifiBackupRestore.retrieveConfigurationsFromBackupData(data));
        Slog.d(TAG, "Restored backup data");
    }

    public void restoreSupplicantBackupData(byte[] supplicantData, byte[] ipConfigData) {
        enforceNetworkSettingsPermission();
        this.mLog.trace("restoreSupplicantBackupData uid=%").c((long) Binder.getCallingUid()).flush();
        if (this.mClientModeImplChannel == null) {
            Slog.e(TAG, "mClientModeImplChannel is not initialized");
            return;
        }
        Slog.d(TAG, "Restoring supplicant backup data");
        restoreNetworks(this.mWifiBackupRestore.retrieveConfigurationsFromSupplicantBackupData(supplicantData, ipConfigData));
        Slog.d(TAG, "Restored supplicant backup data");
    }

    public void startSubscriptionProvisioning(OsuProvider provider, IProvisioningCallback callback) {
        if (provider == null) {
            throw new IllegalArgumentException("Provider must not be null");
        } else if (callback == null) {
            throw new IllegalArgumentException("Callback must not be null");
        } else if (!isSettingsOrSuw(Binder.getCallingPid(), Binder.getCallingUid())) {
            throw new SecurityException("WifiService: Permission denied");
        } else if (this.mContext.getPackageManager().hasSystemFeature("android.hardware.wifi.passpoint")) {
            int uid = Binder.getCallingUid();
            this.mLog.trace("startSubscriptionProvisioning uid=%").c((long) uid).flush();
            if (this.mClientModeImpl.syncStartSubscriptionProvisioning(uid, provider, callback, this.mClientModeImplChannel)) {
                this.mLog.trace("Subscription provisioning started with %").c(provider.toString()).flush();
            }
        } else {
            throw new UnsupportedOperationException("Passpoint not enabled");
        }
    }

    public void registerTrafficStateCallback(IBinder binder, ITrafficStateCallback callback, int callbackIdentifier) {
        if (binder == null) {
            throw new IllegalArgumentException("Binder must not be null");
        } else if (callback != null) {
            enforceNetworkSettingsPermission();
            if (this.mVerboseLoggingEnabled) {
                this.mLog.info("registerTrafficStateCallback uid=%").c((long) Binder.getCallingUid()).flush();
            }
            this.mWifiInjector.getClientModeImplHandler().post(new Runnable(binder, callback, callbackIdentifier) {
                private final /* synthetic */ IBinder f$1;
                private final /* synthetic */ ITrafficStateCallback f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    WifiServiceImpl.this.lambda$registerTrafficStateCallback$16$WifiServiceImpl(this.f$1, this.f$2, this.f$3);
                }
            });
        } else {
            throw new IllegalArgumentException("Callback must not be null");
        }
    }

    public /* synthetic */ void lambda$registerTrafficStateCallback$16$WifiServiceImpl(IBinder binder, ITrafficStateCallback callback, int callbackIdentifier) {
        this.mWifiTrafficPoller.addCallback(binder, callback, callbackIdentifier);
    }

    public void unregisterTrafficStateCallback(int callbackIdentifier) {
        enforceNetworkSettingsPermission();
        if (this.mVerboseLoggingEnabled) {
            this.mLog.info("unregisterTrafficStateCallback uid=%").c((long) Binder.getCallingUid()).flush();
        }
        this.mWifiInjector.getClientModeImplHandler().post(new Runnable(callbackIdentifier) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                WifiServiceImpl.this.lambda$unregisterTrafficStateCallback$17$WifiServiceImpl(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$unregisterTrafficStateCallback$17$WifiServiceImpl(int callbackIdentifier) {
        this.mWifiTrafficPoller.removeCallback(callbackIdentifier);
    }

    private boolean is5GhzSupported() {
        return (getSupportedFeaturesInternal() & 2) == 2;
    }

    private long getSupportedFeaturesInternal() {
        AsyncChannel channel = this.mClientModeImplChannel;
        if (channel != null) {
            return this.mClientModeImpl.syncGetSupportedFeatures(channel);
        }
        Slog.e(TAG, "mClientModeImplChannel is not initialized");
        return 0;
    }

    private static boolean hasAutomotiveFeature(Context context) {
        return context.getPackageManager().hasSystemFeature("android.hardware.type.automotive");
    }

    public void registerNetworkRequestMatchCallback(IBinder binder, INetworkRequestMatchCallback callback, int callbackIdentifier) {
        if (binder == null) {
            throw new IllegalArgumentException("Binder must not be null");
        } else if (callback != null) {
            enforceNetworkSettingsPermission();
            if (this.mVerboseLoggingEnabled) {
                this.mLog.info("registerNetworkRequestMatchCallback uid=%").c((long) Binder.getCallingUid()).flush();
            }
            this.mWifiInjector.getClientModeImplHandler().post(new Runnable(binder, callback, callbackIdentifier) {
                private final /* synthetic */ IBinder f$1;
                private final /* synthetic */ INetworkRequestMatchCallback f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    WifiServiceImpl.this.lambda$registerNetworkRequestMatchCallback$18$WifiServiceImpl(this.f$1, this.f$2, this.f$3);
                }
            });
        } else {
            throw new IllegalArgumentException("Callback must not be null");
        }
    }

    public /* synthetic */ void lambda$registerNetworkRequestMatchCallback$18$WifiServiceImpl(IBinder binder, INetworkRequestMatchCallback callback, int callbackIdentifier) {
        this.mClientModeImpl.addNetworkRequestMatchCallback(binder, callback, callbackIdentifier);
    }

    public void unregisterNetworkRequestMatchCallback(int callbackIdentifier) {
        enforceNetworkSettingsPermission();
        if (this.mVerboseLoggingEnabled) {
            this.mLog.info("unregisterNetworkRequestMatchCallback uid=%").c((long) Binder.getCallingUid()).flush();
        }
        this.mWifiInjector.getClientModeImplHandler().post(new Runnable(callbackIdentifier) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                WifiServiceImpl.this.lambda$unregisterNetworkRequestMatchCallback$19$WifiServiceImpl(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$unregisterNetworkRequestMatchCallback$19$WifiServiceImpl(int callbackIdentifier) {
        this.mClientModeImpl.removeNetworkRequestMatchCallback(callbackIdentifier);
    }

    public int addNetworkSuggestions(List<WifiNetworkSuggestion> networkSuggestions, String callingPackageName) {
        if (enforceChangePermission(callingPackageName) != 0) {
            return 2;
        }
        if (this.mVerboseLoggingEnabled) {
            this.mLog.info("addNetworkSuggestions uid=%").c((long) Binder.getCallingUid()).flush();
        }
        int callingUid = Binder.getCallingUid();
        GeneralUtil.Mutable<Integer> success = new GeneralUtil.Mutable<>();
        if (!this.mWifiInjector.getClientModeImplHandler().runWithScissors(new Runnable(success, networkSuggestions, callingUid, callingPackageName) {
            private final /* synthetic */ GeneralUtil.Mutable f$1;
            private final /* synthetic */ List f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ String f$4;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
            }

            public final void run() {
                WifiServiceImpl.this.lambda$addNetworkSuggestions$20$WifiServiceImpl(this.f$1, this.f$2, this.f$3, this.f$4);
            }
        }, 4000)) {
            Log.e(TAG, "Failed to post runnable to add network suggestions");
            return 1;
        }
        if (((Integer) success.value).intValue() != 0) {
            Log.e(TAG, "Failed to add network suggestions");
        }
        return ((Integer) success.value).intValue();
    }

    public /* synthetic */ void lambda$addNetworkSuggestions$20$WifiServiceImpl(GeneralUtil.Mutable success, List networkSuggestions, int callingUid, String callingPackageName) {
        success.value = Integer.valueOf(this.mWifiNetworkSuggestionsManager.add(networkSuggestions, callingUid, callingPackageName));
    }

    public int removeNetworkSuggestions(List<WifiNetworkSuggestion> networkSuggestions, String callingPackageName) {
        if (enforceChangePermission(callingPackageName) != 0) {
            return 2;
        }
        if (this.mVerboseLoggingEnabled) {
            this.mLog.info("removeNetworkSuggestions uid=%").c((long) Binder.getCallingUid()).flush();
        }
        GeneralUtil.Mutable<Integer> success = new GeneralUtil.Mutable<>();
        if (!this.mWifiInjector.getClientModeImplHandler().runWithScissors(new Runnable(success, networkSuggestions, callingPackageName) {
            private final /* synthetic */ GeneralUtil.Mutable f$1;
            private final /* synthetic */ List f$2;
            private final /* synthetic */ String f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                WifiServiceImpl.this.lambda$removeNetworkSuggestions$21$WifiServiceImpl(this.f$1, this.f$2, this.f$3);
            }
        }, 4000)) {
            Log.e(TAG, "Failed to post runnable to remove network suggestions");
            return 1;
        }
        if (((Integer) success.value).intValue() != 0) {
            Log.e(TAG, "Failed to remove network suggestions");
        }
        return ((Integer) success.value).intValue();
    }

    public /* synthetic */ void lambda$removeNetworkSuggestions$21$WifiServiceImpl(GeneralUtil.Mutable success, List networkSuggestions, String callingPackageName) {
        success.value = Integer.valueOf(this.mWifiNetworkSuggestionsManager.remove(networkSuggestions, callingPackageName));
    }

    public String[] getFactoryMacAddresses() {
        int uid = Binder.getCallingUid();
        if (this.mWifiPermissionsUtil.checkNetworkSettingsPermission(uid)) {
            List<String> result = new ArrayList<>();
            if (!this.mWifiInjector.getClientModeImplHandler().runWithScissors(new Runnable(result) {
                private final /* synthetic */ List f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    WifiServiceImpl.this.lambda$getFactoryMacAddresses$22$WifiServiceImpl(this.f$1);
                }
            }, 4000) || result.isEmpty()) {
                return null;
            }
            return (String[]) result.stream().toArray($$Lambda$WifiServiceImpl$Q4_wUCVLLj90IY9EukPP0WzhPL4.INSTANCE);
        }
        throw new SecurityException("App not allowed to get Wi-Fi factory MAC address (uid = " + uid + ")");
    }

    public /* synthetic */ void lambda$getFactoryMacAddresses$22$WifiServiceImpl(List result) {
        String mac = this.mClientModeImpl.getFactoryMacAddress();
        if (mac != null) {
            result.add(mac);
        }
    }

    static /* synthetic */ String[] lambda$getFactoryMacAddresses$23(int x$0) {
        return new String[x$0];
    }

    public void setDeviceMobilityState(int state) {
        this.mContext.enforceCallingPermission("android.permission.WIFI_SET_DEVICE_MOBILITY_STATE", TAG);
        if (this.mVerboseLoggingEnabled) {
            this.mLog.info("setDeviceMobilityState uid=% state=%").c((long) Binder.getCallingUid()).c((long) state).flush();
        }
        this.mWifiInjector.getClientModeImplHandler().post(new Runnable(state) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                WifiServiceImpl.this.lambda$setDeviceMobilityState$24$WifiServiceImpl(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$setDeviceMobilityState$24$WifiServiceImpl(int state) {
        this.mClientModeImpl.setDeviceMobilityState(state);
    }

    public int getMockableCallingUid() {
        return getCallingUid();
    }

    public void startDppAsConfiguratorInitiator(IBinder binder, String enrolleeUri, int selectedNetworkId, int netRole, IDppCallback callback) {
        if (binder == null) {
            throw new IllegalArgumentException("Binder must not be null");
        } else if (TextUtils.isEmpty(enrolleeUri)) {
            throw new IllegalArgumentException("Enrollee URI must not be null or empty");
        } else if (selectedNetworkId < 0) {
            throw new IllegalArgumentException("Selected network ID invalid");
        } else if (callback != null) {
            int uid = getMockableCallingUid();
            if (isSettingsOrSuw(Binder.getCallingPid(), Binder.getCallingUid())) {
                this.mDppManager.mHandler.post(new Runnable(uid, binder, enrolleeUri, selectedNetworkId, netRole, callback) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ IBinder f$2;
                    private final /* synthetic */ String f$3;
                    private final /* synthetic */ int f$4;
                    private final /* synthetic */ int f$5;
                    private final /* synthetic */ IDppCallback f$6;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                        this.f$5 = r6;
                        this.f$6 = r7;
                    }

                    public final void run() {
                        WifiServiceImpl.this.lambda$startDppAsConfiguratorInitiator$25$WifiServiceImpl(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
                    }
                });
                return;
            }
            throw new SecurityException("WifiService: Permission denied");
        } else {
            throw new IllegalArgumentException("Callback must not be null");
        }
    }

    public /* synthetic */ void lambda$startDppAsConfiguratorInitiator$25$WifiServiceImpl(int uid, IBinder binder, String enrolleeUri, int selectedNetworkId, int netRole, IDppCallback callback) {
        this.mDppManager.startDppAsConfiguratorInitiator(uid, binder, enrolleeUri, selectedNetworkId, netRole, callback);
    }

    public void startDppAsEnrolleeInitiator(IBinder binder, String configuratorUri, IDppCallback callback) {
        if (binder == null) {
            throw new IllegalArgumentException("Binder must not be null");
        } else if (TextUtils.isEmpty(configuratorUri)) {
            throw new IllegalArgumentException("Enrollee URI must not be null or empty");
        } else if (callback != null) {
            int uid = getMockableCallingUid();
            if (isSettingsOrSuw(Binder.getCallingPid(), Binder.getCallingUid())) {
                this.mDppManager.mHandler.post(new Runnable(uid, binder, configuratorUri, callback) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ IBinder f$2;
                    private final /* synthetic */ String f$3;
                    private final /* synthetic */ IDppCallback f$4;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                        this.f$4 = r5;
                    }

                    public final void run() {
                        WifiServiceImpl.this.lambda$startDppAsEnrolleeInitiator$26$WifiServiceImpl(this.f$1, this.f$2, this.f$3, this.f$4);
                    }
                });
                return;
            }
            throw new SecurityException("WifiService: Permission denied");
        } else {
            throw new IllegalArgumentException("Callback must not be null");
        }
    }

    public /* synthetic */ void lambda$startDppAsEnrolleeInitiator$26$WifiServiceImpl(int uid, IBinder binder, String configuratorUri, IDppCallback callback) {
        this.mDppManager.startDppAsEnrolleeInitiator(uid, binder, configuratorUri, callback);
    }

    public void stopDppSession() throws RemoteException {
        if (isSettingsOrSuw(Binder.getCallingPid(), Binder.getCallingUid())) {
            this.mDppManager.mHandler.post(new Runnable(getMockableCallingUid()) {
                private final /* synthetic */ int f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    WifiServiceImpl.this.lambda$stopDppSession$27$WifiServiceImpl(this.f$1);
                }
            });
            return;
        }
        throw new SecurityException("WifiService: Permission denied");
    }

    public /* synthetic */ void lambda$stopDppSession$27$WifiServiceImpl(int uid) {
        this.mDppManager.stopDppSession(uid);
    }

    public int dppAddBootstrapQrCode(String uri) {
        return this.mClientModeImpl.syncDppAddBootstrapQrCode(this.mClientModeImplChannel, uri);
    }

    public int dppBootstrapGenerate(WifiDppConfig config) {
        return this.mClientModeImpl.syncDppBootstrapGenerate(this.mClientModeImplChannel, config);
    }

    public String dppGetUri(int bootstrap_id) {
        return this.mClientModeImpl.syncDppGetUri(this.mClientModeImplChannel, bootstrap_id);
    }

    public int dppBootstrapRemove(int bootstrap_id) {
        return this.mClientModeImpl.syncDppBootstrapRemove(this.mClientModeImplChannel, bootstrap_id);
    }

    public int dppListen(String frequency, int dpp_role, boolean qr_mutual, boolean netrole_ap) {
        return this.mClientModeImpl.syncDppListen(this.mClientModeImplChannel, frequency, dpp_role, qr_mutual, netrole_ap);
    }

    public void dppStopListen() {
        this.mClientModeImpl.dppStopListen(this.mClientModeImplChannel);
    }

    public int dppConfiguratorAdd(String curve, String key, int expiry) {
        return this.mClientModeImpl.syncDppConfiguratorAdd(this.mClientModeImplChannel, curve, key, expiry);
    }

    public int dppConfiguratorRemove(int config_id) {
        return this.mClientModeImpl.syncDppConfiguratorRemove(this.mClientModeImplChannel, config_id);
    }

    public int dppStartAuth(WifiDppConfig config) {
        return this.mClientModeImpl.syncDppStartAuth(this.mClientModeImplChannel, config);
    }

    public String dppConfiguratorGetKey(int id) {
        return this.mClientModeImpl.syncDppConfiguratorGetKey(this.mClientModeImplChannel, id);
    }

    private void setDualSapMode(WifiConfiguration apConfig) {
        if (apConfig == null) {
            apConfig = this.mWifiApConfigStore.getApConfiguration();
        }
        if (apConfig.apBand == 2 || apConfig.allowedKeyManagement.get(9)) {
            this.mLog.trace("setDualSapMode uid=%").c((long) Binder.getCallingUid()).flush();
            this.mWifiApConfigStore.setDualSapStatus(true);
            return;
        }
        this.mWifiApConfigStore.setDualSapStatus(false);
    }

    public boolean isExtendingWifi() {
        return this.mSoftApExtendingWifi;
    }

    public boolean isCurrentStaShareThisAp() {
        WifiConfiguration currentStaConfig;
        int authType;
        if (isWifiCoverageExtendFeatureEnabled() && (currentStaConfig = this.mClientModeImpl.getCurrentWifiConfiguration()) != null && currentStaConfig.shareThisAp && ((authType = currentStaConfig.getAuthType()) == 0 || authType == 1)) {
            return true;
        }
        return false;
    }

    private void startSoftApInRepeaterMode(int mode, WifiConfiguration apConfig) {
        SoftApModeConfiguration softApConfig = new SoftApModeConfiguration(mode, this.mWifiInjector.getWifiConfigManager().getConfiguredNetworkWithPassword(this.mClientModeImpl.getWifiInfo().getNetworkId()));
        softApConfig.mConfig.SSID = WifiInfo.removeDoubleQuotes(softApConfig.mConfig.SSID);
        softApConfig.mConfig.preSharedKey = WifiInfo.removeDoubleQuotes(softApConfig.mConfig.preSharedKey);
        if (apConfig == null) {
            softApConfig.mConfig.apBand = this.mWifiApConfigStore.getApConfiguration().apBand;
        } else {
            softApConfig.mConfig.apBand = apConfig.apBand;
        }
        Slog.d(TAG, "Repeater mode config - " + softApConfig.mConfig);
        this.mWifiController.sendMessage(155658, 1, 0, softApConfig);
    }

    public boolean isWifiCoverageExtendFeatureEnabled() {
        enforceAccessPermission();
        return this.mFacade.getIntegerSetting(this.mContext, "wifi_coverage_extend_feature_enabled", 0) > 0;
    }

    public void enableWifiCoverageExtendFeature(boolean enable) {
        enforceAccessPermission();
        enforceNetworkSettingsPermission();
        this.mLog.info("enableWifiCoverageExtendFeature uid=% enable=%").c((long) Binder.getCallingUid()).c(enable).flush();
        this.mFacade.setIntegerSetting(this.mContext, "wifi_coverage_extend_feature_enabled", enable);
    }

    public void addOnWifiUsabilityStatsListener(IBinder binder, IOnWifiUsabilityStatsListener listener, int listenerIdentifier) {
        if (binder == null) {
            throw new IllegalArgumentException("Binder must not be null");
        } else if (listener != null) {
            this.mContext.enforceCallingPermission("android.permission.WIFI_UPDATE_USABILITY_STATS_SCORE", TAG);
            if (this.mVerboseLoggingEnabled) {
                this.mLog.info("addOnWifiUsabilityStatsListener uid=%").c((long) Binder.getCallingUid()).flush();
            }
            this.mWifiInjector.getClientModeImplHandler().post(new Runnable(binder, listener, listenerIdentifier) {
                private final /* synthetic */ IBinder f$1;
                private final /* synthetic */ IOnWifiUsabilityStatsListener f$2;
                private final /* synthetic */ int f$3;

                {
                    this.f$1 = r2;
                    this.f$2 = r3;
                    this.f$3 = r4;
                }

                public final void run() {
                    WifiServiceImpl.this.lambda$addOnWifiUsabilityStatsListener$28$WifiServiceImpl(this.f$1, this.f$2, this.f$3);
                }
            });
        } else {
            throw new IllegalArgumentException("Listener must not be null");
        }
    }

    public /* synthetic */ void lambda$addOnWifiUsabilityStatsListener$28$WifiServiceImpl(IBinder binder, IOnWifiUsabilityStatsListener listener, int listenerIdentifier) {
        this.mWifiMetrics.addOnWifiUsabilityListener(binder, listener, listenerIdentifier);
    }

    public void removeOnWifiUsabilityStatsListener(int listenerIdentifier) {
        this.mContext.enforceCallingPermission("android.permission.WIFI_UPDATE_USABILITY_STATS_SCORE", TAG);
        if (this.mVerboseLoggingEnabled) {
            this.mLog.info("removeOnWifiUsabilityStatsListener uid=%").c((long) Binder.getCallingUid()).flush();
        }
        this.mWifiInjector.getClientModeImplHandler().post(new Runnable(listenerIdentifier) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                WifiServiceImpl.this.lambda$removeOnWifiUsabilityStatsListener$29$WifiServiceImpl(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$removeOnWifiUsabilityStatsListener$29$WifiServiceImpl(int listenerIdentifier) {
        this.mWifiMetrics.removeOnWifiUsabilityListener(listenerIdentifier);
    }

    public void updateWifiUsabilityScore(int seqNum, int score, int predictionHorizonSec) {
        this.mContext.enforceCallingPermission("android.permission.WIFI_UPDATE_USABILITY_STATS_SCORE", TAG);
        if (this.mVerboseLoggingEnabled) {
            this.mLog.info("updateWifiUsabilityScore uid=% seqNum=% score=% predictionHorizonSec=%").c((long) Binder.getCallingUid()).c((long) seqNum).c((long) score).c((long) predictionHorizonSec).flush();
        }
        this.mWifiInjector.getClientModeImplHandler().post(new Runnable(seqNum, score, predictionHorizonSec) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final void run() {
                WifiServiceImpl.this.lambda$updateWifiUsabilityScore$30$WifiServiceImpl(this.f$1, this.f$2, this.f$3);
            }
        });
    }

    public /* synthetic */ void lambda$updateWifiUsabilityScore$30$WifiServiceImpl(int seqNum, int score, int predictionHorizonSec) {
        this.mClientModeImpl.updateWifiUsabilityScore(seqNum, score, predictionHorizonSec);
    }
}
