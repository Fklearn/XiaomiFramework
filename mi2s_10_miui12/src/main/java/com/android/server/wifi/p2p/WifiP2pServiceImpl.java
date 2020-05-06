package com.android.server.wifi.p2p;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.DhcpResults;
import android.net.InterfaceConfiguration;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.NetworkUtils;
import android.net.ip.IIpClient;
import android.net.ip.IpClientCallbacks;
import android.net.ip.IpClientUtil;
import android.net.shared.ProvisioningConfiguration;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.IWifiP2pManager;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pGroupList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pProvDiscEvent;
import android.net.wifi.p2p.WifiP2pWfdInfo;
import android.net.wifi.p2p.nsd.WifiP2pServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pServiceRequest;
import android.net.wifi.p2p.nsd.WifiP2pServiceResponse;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MiuiWindowManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.AsyncChannel;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import com.android.server.wifi.FrameworkFacade;
import com.android.server.wifi.HalDeviceManager;
import com.android.server.wifi.WifiInjector;
import com.android.server.wifi.WifiLog;
import com.android.server.wifi.WifiNative;
import com.android.server.wifi.p2p.WifiP2pServiceImpl;
import com.android.server.wifi.rtt.RttServiceImpl;
import com.android.server.wifi.util.WifiAsyncChannel;
import com.android.server.wifi.util.WifiHandler;
import com.android.server.wifi.util.WifiPermissionsUtil;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.telephony.phonenumber.Prefix;

public class WifiP2pServiceImpl extends IWifiP2pManager.Stub {
    private static final String ANONYMIZED_DEVICE_ADDRESS = "02:00:00:00:00:00";
    private static final int BASE = 143360;
    public static final int BLOCK_DISCOVERY = 143375;
    public static final int DISABLED = 0;
    public static final int DISABLE_P2P = 143377;
    public static final int DISABLE_P2P_TIMED_OUT = 143366;
    private static final int DISABLE_P2P_WAIT_TIME_MS = 5000;
    public static final int DISCONNECT_WIFI_REQUEST = 143372;
    public static final int DISCONNECT_WIFI_RESPONSE = 143373;
    private static final int DISCOVER_TIMEOUT_S = 120;
    private static final int DROP_WIFI_USER_ACCEPT = 143364;
    private static final int DROP_WIFI_USER_REJECT = 143365;
    private static final String EMPTY_DEVICE_ADDRESS = "00:00:00:00:00:00";
    public static final int ENABLED = 1;
    public static final int ENABLE_P2P = 143376;
    /* access modifiers changed from: private */
    public static final Boolean FORM_GROUP = false;
    public static final int GROUP_CREATING_TIMED_OUT = 143361;
    private static final int GROUP_CREATING_WAIT_TIME_MS = 120000;
    private static final int GROUP_IDLE_TIME_S = 10;
    private static final int IPC_DHCP_RESULTS = 143392;
    private static final int IPC_POST_DHCP_ACTION = 143391;
    private static final int IPC_PRE_DHCP_ACTION = 143390;
    private static final int IPC_PROVISIONING_FAILURE = 143394;
    private static final int IPC_PROVISIONING_SUCCESS = 143393;
    private static final Boolean JOIN_GROUP = true;
    private static final String NETWORKTYPE = "WIFI_P2P";
    private static final Boolean NO_RELOAD = false;
    static final int P2P_BLUETOOTH_COEXISTENCE_MODE_DISABLED = 1;
    static final int P2P_BLUETOOTH_COEXISTENCE_MODE_SENSE = 2;
    public static final int P2P_CONNECTION_CHANGED = 143371;
    private static final int PEER_CONNECTION_USER_ACCEPT = 143362;
    public static final int PEER_CONNECTION_USER_CONFIRM = 143367;
    private static final int PEER_CONNECTION_USER_REJECT = 143363;
    /* access modifiers changed from: private */
    public static final String[] RECEIVER_PERMISSIONS_FOR_BROADCAST = {"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_WIFI_STATE"};
    /* access modifiers changed from: private */
    public static final Boolean RELOAD = true;
    public static final int REMOVE_CLIENT_INFO = 143378;
    private static final String SERVER_ADDRESS = "192.168.49.1";
    public static final int SET_MIRACAST_MODE = 143374;
    private static final String TAG = "WifiP2pService";
    /* access modifiers changed from: private */
    public static int sDisableP2pTimeoutIndex = 0;
    /* access modifiers changed from: private */
    public static int sGroupCreatingTimeoutIndex = 0;
    /* access modifiers changed from: private */
    public boolean mAutonomousGroup;
    /* access modifiers changed from: private */
    public Map<IBinder, Messenger> mClientChannelList = new HashMap();
    private ClientHandler mClientHandler;
    /* access modifiers changed from: private */
    public HashMap<Messenger, ClientInfo> mClientInfoList = new HashMap<>();
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public final Map<IBinder, DeathHandlerData> mDeathDataByBinder = new HashMap();
    /* access modifiers changed from: private */
    public DhcpResults mDhcpResults;
    /* access modifiers changed from: private */
    public boolean mDiscoveryBlocked;
    /* access modifiers changed from: private */
    public boolean mDiscoveryPostponed = false;
    /* access modifiers changed from: private */
    public boolean mDiscoveryStarted;
    /* access modifiers changed from: private */
    public FrameworkFacade mFrameworkFacade;
    /* access modifiers changed from: private */
    public IIpClient mIpClient;
    /* access modifiers changed from: private */
    public int mIpClientStartIndex = 0;
    /* access modifiers changed from: private */
    public boolean mJoinExistingGroup;
    private LocationManager mLocationManager;
    private Object mLock = new Object();
    /* access modifiers changed from: private */
    public NetworkInfo mNetworkInfo;
    INetworkManagementService mNwService;
    /* access modifiers changed from: private */
    public P2pStateMachine mP2pStateMachine;
    private final boolean mP2pSupported;
    /* access modifiers changed from: private */
    public AsyncChannel mReplyChannel = new WifiAsyncChannel(TAG);
    /* access modifiers changed from: private */
    public String mServiceDiscReqId;
    /* access modifiers changed from: private */
    public byte mServiceTransactionId = 0;
    /* access modifiers changed from: private */
    public boolean mTemporarilyDisconnectedWifi = false;
    /* access modifiers changed from: private */
    public WifiP2pDevice mThisDevice = new WifiP2pDevice();
    /* access modifiers changed from: private */
    public boolean mVerboseLoggingEnabled = false;
    /* access modifiers changed from: private */
    public AsyncChannel mWifiChannel;
    /* access modifiers changed from: private */
    public WifiInjector mWifiInjector;
    /* access modifiers changed from: private */
    public WifiP2pMetrics mWifiP2pMetrics;
    /* access modifiers changed from: private */
    public WifiPermissionsUtil mWifiPermissionsUtil;

    static /* synthetic */ byte access$11104(WifiP2pServiceImpl x0) {
        byte b = (byte) (x0.mServiceTransactionId + 1);
        x0.mServiceTransactionId = b;
        return b;
    }

    static /* synthetic */ int access$4304() {
        int i = sDisableP2pTimeoutIndex + 1;
        sDisableP2pTimeoutIndex = i;
        return i;
    }

    static /* synthetic */ int access$8204() {
        int i = sGroupCreatingTimeoutIndex + 1;
        sGroupCreatingTimeoutIndex = i;
        return i;
    }

    public enum P2pStatus {
        SUCCESS,
        INFORMATION_IS_CURRENTLY_UNAVAILABLE,
        INCOMPATIBLE_PARAMETERS,
        LIMIT_REACHED,
        INVALID_PARAMETER,
        UNABLE_TO_ACCOMMODATE_REQUEST,
        PREVIOUS_PROTOCOL_ERROR,
        NO_COMMON_CHANNEL,
        UNKNOWN_P2P_GROUP,
        BOTH_GO_INTENT_15,
        INCOMPATIBLE_PROVISIONING_METHOD,
        REJECTED_BY_USER,
        UNKNOWN;

        public static P2pStatus valueOf(int error) {
            switch (error) {
                case 0:
                    return SUCCESS;
                case 1:
                    return INFORMATION_IS_CURRENTLY_UNAVAILABLE;
                case 2:
                    return INCOMPATIBLE_PARAMETERS;
                case 3:
                    return LIMIT_REACHED;
                case 4:
                    return INVALID_PARAMETER;
                case 5:
                    return UNABLE_TO_ACCOMMODATE_REQUEST;
                case 6:
                    return PREVIOUS_PROTOCOL_ERROR;
                case 7:
                    return NO_COMMON_CHANNEL;
                case 8:
                    return UNKNOWN_P2P_GROUP;
                case 9:
                    return BOTH_GO_INTENT_15;
                case 10:
                    return INCOMPATIBLE_PROVISIONING_METHOD;
                case 11:
                    return REJECTED_BY_USER;
                default:
                    return UNKNOWN;
            }
        }
    }

    private class ClientHandler extends WifiHandler {
        ClientHandler(String tag, Looper looper) {
            super(tag, looper);
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 139265:
                case 139268:
                case 139271:
                case 139274:
                case 139277:
                case 139280:
                case 139283:
                case 139285:
                case 139287:
                case 139292:
                case 139295:
                case 139298:
                case 139301:
                case 139304:
                case 139307:
                case 139310:
                case 139315:
                case 139318:
                case 139321:
                case 139323:
                case 139326:
                case 139329:
                case 139332:
                case 139335:
                case 139346:
                case 139349:
                case 139351:
                case 139354:
                case 139357:
                case 139359:
                case 139361:
                case 139363:
                case 139364:
                    WifiP2pServiceImpl.this.mP2pStateMachine.sendMessage(Message.obtain(msg));
                    return;
                default:
                    Slog.d(WifiP2pServiceImpl.TAG, "ClientHandler.handleMessage ignoring msg=" + msg);
                    return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setWifiHandlerLogForTest(WifiLog log) {
        this.mClientHandler.setWifiLog(log);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setWifiLogForReplyChannel(WifiLog log) {
        this.mReplyChannel.setWifiLog(log);
    }

    private class DeathHandlerData {
        IBinder.DeathRecipient mDeathRecipient;
        Messenger mMessenger;

        DeathHandlerData(IBinder.DeathRecipient dr, Messenger m) {
            this.mDeathRecipient = dr;
            this.mMessenger = m;
        }

        public String toString() {
            return "deathRecipient=" + this.mDeathRecipient + ", messenger=" + this.mMessenger;
        }
    }

    public WifiP2pServiceImpl(Context context, WifiInjector wifiInjector) {
        this.mContext = context;
        this.mWifiInjector = wifiInjector;
        this.mWifiPermissionsUtil = this.mWifiInjector.getWifiPermissionsUtil();
        this.mFrameworkFacade = this.mWifiInjector.getFrameworkFacade();
        this.mWifiP2pMetrics = this.mWifiInjector.getWifiP2pMetrics();
        this.mNetworkInfo = new NetworkInfo(13, 0, NETWORKTYPE, Prefix.EMPTY);
        this.mP2pSupported = this.mContext.getPackageManager().hasSystemFeature("android.hardware.wifi.direct");
        this.mThisDevice.primaryDeviceType = this.mContext.getResources().getString(17039802);
        HandlerThread wifiP2pThread = this.mWifiInjector.getWifiP2pServiceHandlerThread();
        this.mClientHandler = new ClientHandler(TAG, wifiP2pThread.getLooper());
        this.mP2pStateMachine = new P2pStateMachine(TAG, wifiP2pThread.getLooper(), this.mP2pSupported);
        this.mP2pStateMachine.start();
    }

    public void enableVerboseLogging(int verbose) {
        this.mVerboseLoggingEnabled = verbose > 0;
    }

    public void connectivityServiceReady() {
        this.mNwService = INetworkManagementService.Stub.asInterface(ServiceManager.getService("network_management"));
    }

    private void enforceAccessPermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_WIFI_STATE", TAG);
    }

    private void enforceChangePermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CHANGE_WIFI_STATE", TAG);
    }

    private void enforceConnectivityInternalPermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", TAG);
    }

    private int checkConnectivityInternalPermission() {
        return this.mContext.checkCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL");
    }

    private int checkLocationHardwarePermission() {
        return this.mContext.checkCallingOrSelfPermission("android.permission.LOCATION_HARDWARE");
    }

    private void enforceConnectivityInternalOrLocationHardwarePermission() {
        if (checkConnectivityInternalPermission() != 0 && checkLocationHardwarePermission() != 0) {
            enforceConnectivityInternalPermission();
        }
    }

    /* access modifiers changed from: private */
    public void stopIpClient() {
        this.mIpClientStartIndex++;
        IIpClient iIpClient = this.mIpClient;
        if (iIpClient != null) {
            try {
                iIpClient.stop();
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
            }
            this.mIpClient = null;
        }
        this.mDhcpResults = null;
    }

    /* access modifiers changed from: private */
    public void startIpClient(String ifname, Handler smHandler) {
        stopIpClient();
        this.mIpClientStartIndex++;
        IpClientUtil.makeIpClient(this.mContext, ifname, new IpClientCallbacksImpl(this.mIpClientStartIndex, smHandler));
    }

    private class IpClientCallbacksImpl extends IpClientCallbacks {
        private final Handler mHandler;
        private final int mStartIndex;

        private IpClientCallbacksImpl(int startIndex, Handler handler) {
            this.mStartIndex = startIndex;
            this.mHandler = handler;
        }

        public void onIpClientCreated(IIpClient ipClient) {
            this.mHandler.post(new Runnable(ipClient) {
                private final /* synthetic */ IIpClient f$1;

                {
                    this.f$1 = r2;
                }

                public final void run() {
                    WifiP2pServiceImpl.IpClientCallbacksImpl.this.lambda$onIpClientCreated$0$WifiP2pServiceImpl$IpClientCallbacksImpl(this.f$1);
                }
            });
        }

        public /* synthetic */ void lambda$onIpClientCreated$0$WifiP2pServiceImpl$IpClientCallbacksImpl(IIpClient ipClient) {
            if (WifiP2pServiceImpl.this.mIpClientStartIndex == this.mStartIndex) {
                IIpClient unused = WifiP2pServiceImpl.this.mIpClient = ipClient;
                try {
                    WifiP2pServiceImpl.this.mIpClient.startProvisioning(new ProvisioningConfiguration.Builder().withoutIpReachabilityMonitor().withPreDhcpAction(30000).withProvisioningTimeoutMs(36000).build().toStableParcelable());
                } catch (RemoteException e) {
                    e.rethrowFromSystemServer();
                }
            }
        }

        public void onPreDhcpAction() {
            WifiP2pServiceImpl.this.mP2pStateMachine.sendMessage(WifiP2pServiceImpl.IPC_PRE_DHCP_ACTION);
        }

        public void onPostDhcpAction() {
            WifiP2pServiceImpl.this.mP2pStateMachine.sendMessage(WifiP2pServiceImpl.IPC_POST_DHCP_ACTION);
        }

        public void onNewDhcpResults(DhcpResults dhcpResults) {
            WifiP2pServiceImpl.this.mP2pStateMachine.sendMessage(WifiP2pServiceImpl.IPC_DHCP_RESULTS, dhcpResults);
        }

        public void onProvisioningSuccess(LinkProperties newLp) {
            WifiP2pServiceImpl.this.mP2pStateMachine.sendMessage(WifiP2pServiceImpl.IPC_PROVISIONING_SUCCESS);
        }

        public void onProvisioningFailure(LinkProperties newLp) {
            WifiP2pServiceImpl.this.mP2pStateMachine.sendMessage(WifiP2pServiceImpl.IPC_PROVISIONING_FAILURE);
        }
    }

    public Messenger getMessenger(IBinder binder) {
        Messenger messenger;
        enforceAccessPermission();
        enforceChangePermission();
        synchronized (this.mLock) {
            messenger = new Messenger(this.mClientHandler);
            if (this.mVerboseLoggingEnabled) {
                Log.d(TAG, "getMessenger: uid=" + getCallingUid() + ", binder=" + binder + ", messenger=" + messenger);
            }
            IBinder.DeathRecipient dr = new IBinder.DeathRecipient(binder) {
                private final /* synthetic */ IBinder f$1;

                {
                    this.f$1 = r2;
                }

                public final void binderDied() {
                    WifiP2pServiceImpl.this.lambda$getMessenger$0$WifiP2pServiceImpl(this.f$1);
                }
            };
            try {
                binder.linkToDeath(dr, 0);
                this.mDeathDataByBinder.put(binder, new DeathHandlerData(dr, messenger));
            } catch (RemoteException e) {
                Log.e(TAG, "Error on linkToDeath: e=" + e);
            }
            this.mP2pStateMachine.sendMessage(ENABLE_P2P);
        }
        return messenger;
    }

    public /* synthetic */ void lambda$getMessenger$0$WifiP2pServiceImpl(IBinder binder) {
        if (this.mVerboseLoggingEnabled) {
            Log.d(TAG, "binderDied: binder=" + binder);
        }
        close(binder);
    }

    public Messenger getP2pStateMachineMessenger() {
        enforceConnectivityInternalOrLocationHardwarePermission();
        enforceAccessPermission();
        enforceChangePermission();
        return new Messenger(this.mP2pStateMachine.getHandler());
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0078, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void close(android.os.IBinder r7) {
        /*
            r6 = this;
            r6.enforceAccessPermission()
            r6.enforceChangePermission()
            java.lang.Object r0 = r6.mLock
            monitor-enter(r0)
            java.util.Map<android.os.IBinder, com.android.server.wifi.p2p.WifiP2pServiceImpl$DeathHandlerData> r1 = r6.mDeathDataByBinder     // Catch:{ all -> 0x0079 }
            java.lang.Object r1 = r1.get(r7)     // Catch:{ all -> 0x0079 }
            com.android.server.wifi.p2p.WifiP2pServiceImpl$DeathHandlerData r1 = (com.android.server.wifi.p2p.WifiP2pServiceImpl.DeathHandlerData) r1     // Catch:{ all -> 0x0079 }
            if (r1 != 0) goto L_0x001c
            java.lang.String r2 = "WifiP2pService"
            java.lang.String r3 = "close(): no death recipient for binder"
            android.util.Log.w(r2, r3)     // Catch:{ all -> 0x0079 }
            monitor-exit(r0)     // Catch:{ all -> 0x0079 }
            return
        L_0x001c:
            com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r2 = r6.mP2pStateMachine     // Catch:{ all -> 0x0079 }
            r3 = 143378(0x23012, float:2.00915E-40)
            r4 = 0
            r2.sendMessage(r3, r4, r4, r7)     // Catch:{ all -> 0x0079 }
            android.os.IBinder$DeathRecipient r2 = r1.mDeathRecipient     // Catch:{ all -> 0x0079 }
            r7.unlinkToDeath(r2, r4)     // Catch:{ all -> 0x0079 }
            java.util.Map<android.os.IBinder, com.android.server.wifi.p2p.WifiP2pServiceImpl$DeathHandlerData> r2 = r6.mDeathDataByBinder     // Catch:{ all -> 0x0079 }
            r2.remove(r7)     // Catch:{ all -> 0x0079 }
            android.os.Messenger r2 = r1.mMessenger     // Catch:{ all -> 0x0079 }
            if (r2 == 0) goto L_0x0077
            java.util.Map<android.os.IBinder, com.android.server.wifi.p2p.WifiP2pServiceImpl$DeathHandlerData> r2 = r6.mDeathDataByBinder     // Catch:{ all -> 0x0079 }
            boolean r2 = r2.isEmpty()     // Catch:{ all -> 0x0079 }
            if (r2 == 0) goto L_0x0077
            android.os.Messenger r2 = r1.mMessenger     // Catch:{ RemoteException -> 0x0058 }
            com.android.server.wifi.p2p.WifiP2pServiceImpl$ClientHandler r3 = r6.mClientHandler     // Catch:{ RemoteException -> 0x0058 }
            r4 = 139268(0x22004, float:1.95156E-40)
            android.os.Message r3 = r3.obtainMessage(r4)     // Catch:{ RemoteException -> 0x0058 }
            r2.send(r3)     // Catch:{ RemoteException -> 0x0058 }
            android.os.Messenger r2 = r1.mMessenger     // Catch:{ RemoteException -> 0x0058 }
            com.android.server.wifi.p2p.WifiP2pServiceImpl$ClientHandler r3 = r6.mClientHandler     // Catch:{ RemoteException -> 0x0058 }
            r4 = 139280(0x22010, float:1.95173E-40)
            android.os.Message r3 = r3.obtainMessage(r4)     // Catch:{ RemoteException -> 0x0058 }
            r2.send(r3)     // Catch:{ RemoteException -> 0x0058 }
            goto L_0x006f
        L_0x0058:
            r2 = move-exception
            java.lang.String r3 = "WifiP2pService"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0079 }
            r4.<init>()     // Catch:{ all -> 0x0079 }
            java.lang.String r5 = "close: Failed sending clean-up commands: e="
            r4.append(r5)     // Catch:{ all -> 0x0079 }
            r4.append(r2)     // Catch:{ all -> 0x0079 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0079 }
            android.util.Log.e(r3, r4)     // Catch:{ all -> 0x0079 }
        L_0x006f:
            com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r2 = r6.mP2pStateMachine     // Catch:{ all -> 0x0079 }
            r3 = 143377(0x23011, float:2.00914E-40)
            r2.sendMessage(r3)     // Catch:{ all -> 0x0079 }
        L_0x0077:
            monitor-exit(r0)     // Catch:{ all -> 0x0079 }
            return
        L_0x0079:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0079 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.p2p.WifiP2pServiceImpl.close(android.os.IBinder):void");
    }

    public void setMiracastMode(int mode) {
        enforceConnectivityInternalPermission();
        checkConfigureWifiDisplayPermission();
        this.mP2pStateMachine.sendMessage(SET_MIRACAST_MODE, mode);
        AsyncChannel asyncChannel = this.mWifiChannel;
        if (asyncChannel != null) {
            asyncChannel.sendMessage(SET_MIRACAST_MODE, mode);
        } else {
            Log.e(TAG, "setMiracastMode(): WifiChannel is null");
        }
    }

    public void checkConfigureWifiDisplayPermission() {
        if (!getWfdPermission(Binder.getCallingUid())) {
            throw new SecurityException("Wifi Display Permission denied for uid = " + Binder.getCallingUid());
        }
    }

    /* access modifiers changed from: private */
    public boolean getWfdPermission(int uid) {
        return this.mWifiInjector.getWifiPermissionsWrapper().getUidPermission("android.permission.CONFIGURE_WIFI_DISPLAY", uid) != -1;
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0) {
            pw.println("Permission Denial: can't dump WifiP2pService from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        this.mP2pStateMachine.dump(fd, pw, args);
        pw.println("mAutonomousGroup " + this.mAutonomousGroup);
        pw.println("mJoinExistingGroup " + this.mJoinExistingGroup);
        pw.println("mDiscoveryStarted " + this.mDiscoveryStarted);
        pw.println("mNetworkInfo " + this.mNetworkInfo);
        pw.println("mTemporarilyDisconnectedWifi " + this.mTemporarilyDisconnectedWifi);
        pw.println("mServiceDiscReqId " + this.mServiceDiscReqId);
        pw.println("mDeathDataByBinder " + this.mDeathDataByBinder);
        pw.println("mClientInfoList " + this.mClientInfoList.size());
        pw.println();
        IIpClient ipClient = this.mIpClient;
        if (ipClient != null) {
            pw.println("mIpClient:");
            IpClientUtil.dumpIpClient(ipClient, fd, pw, args);
        }
    }

    private class P2pStateMachine extends StateMachine {
        private static final int P2P_SETUP_FAILURE_COUNT_THRESHOLD = 10;
        private DefaultState mDefaultState = new DefaultState();
        /* access modifiers changed from: private */
        public FrequencyConflictState mFrequencyConflictState = new FrequencyConflictState();
        /* access modifiers changed from: private */
        public WifiP2pGroup mGroup;
        /* access modifiers changed from: private */
        public GroupCreatedState mGroupCreatedState = new GroupCreatedState();
        /* access modifiers changed from: private */
        public GroupCreatingState mGroupCreatingState = new GroupCreatingState();
        /* access modifiers changed from: private */
        public GroupNegotiationState mGroupNegotiationState = new GroupNegotiationState();
        /* access modifiers changed from: private */
        public final WifiP2pGroupList mGroups = new WifiP2pGroupList((WifiP2pGroupList) null, new WifiP2pGroupList.GroupDeleteListener() {
            public void onDeleteGroup(int netId) {
                if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                    P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                    p2pStateMachine.logd("called onDeleteGroup() netId=" + netId);
                }
                P2pStateMachine.this.mWifiNative.removeP2pNetwork(netId);
                P2pStateMachine.this.mWifiNative.saveConfig();
                P2pStateMachine.this.sendP2pPersistentGroupsChangedBroadcast();
            }
        });
        /* access modifiers changed from: private */
        public InactiveState mInactiveState = new InactiveState();
        /* access modifiers changed from: private */
        public String mInterfaceName;
        /* access modifiers changed from: private */
        public boolean mIsBTCoexDisabled = false;
        /* access modifiers changed from: private */
        public boolean mIsHalInterfaceAvailable = false;
        /* access modifiers changed from: private */
        public boolean mIsWifiEnabled = false;
        /* access modifiers changed from: private */
        public OngoingGroupRemovalState mOngoingGroupRemovalState = new OngoingGroupRemovalState();
        /* access modifiers changed from: private */
        public P2pDisabledState mP2pDisabledState = new P2pDisabledState();
        /* access modifiers changed from: private */
        public P2pDisablingState mP2pDisablingState = new P2pDisablingState();
        private P2pEnabledState mP2pEnabledState = new P2pEnabledState();
        private P2pNotSupportedState mP2pNotSupportedState = new P2pNotSupportedState();
        /* access modifiers changed from: private */
        public final WifiP2pDeviceList mPeers = new WifiP2pDeviceList();
        /* access modifiers changed from: private */
        public final WifiP2pDeviceList mPeersLostDuringConnection = new WifiP2pDeviceList();
        /* access modifiers changed from: private */
        public ProvisionDiscoveryState mProvisionDiscoveryState = new ProvisionDiscoveryState();
        /* access modifiers changed from: private */
        public WifiP2pConfig mSavedPeerConfig = new WifiP2pConfig();
        /* access modifiers changed from: private */
        public int mSetupFailureCount = 0;
        /* access modifiers changed from: private */
        public UserAuthorizingInviteRequestState mUserAuthorizingInviteRequestState = new UserAuthorizingInviteRequestState();
        /* access modifiers changed from: private */
        public UserAuthorizingJoinState mUserAuthorizingJoinState = new UserAuthorizingJoinState();
        /* access modifiers changed from: private */
        public UserAuthorizingNegotiationRequestState mUserAuthorizingNegotiationRequestState = new UserAuthorizingNegotiationRequestState();
        /* access modifiers changed from: private */
        public WifiNative mWifNative = WifiP2pServiceImpl.this.mWifiInjector.getWifiNative();
        /* access modifiers changed from: private */
        public WifiP2pMonitor mWifiMonitor = WifiP2pServiceImpl.this.mWifiInjector.getWifiP2pMonitor();
        /* access modifiers changed from: private */
        public WifiP2pNative mWifiNative = WifiP2pServiceImpl.this.mWifiInjector.getWifiP2pNative();
        /* access modifiers changed from: private */
        public final WifiP2pInfo mWifiP2pInfo = new WifiP2pInfo();

        static /* synthetic */ int access$1008(P2pStateMachine x0) {
            int i = x0.mSetupFailureCount;
            x0.mSetupFailureCount = i + 1;
            return i;
        }

        /* access modifiers changed from: private */
        public void logStateAndMessage(Message message, State state) {
            StringBuilder b = new StringBuilder();
            if (message != null) {
                b.append("{ what=");
                b.append(message.what);
                if (message.arg1 != 0) {
                    b.append(" arg1=");
                    b.append(message.arg1);
                }
                if (message.arg2 != 0) {
                    b.append(" arg2=");
                    b.append(message.arg2);
                }
                if (message.obj != null) {
                    b.append(" obj=");
                    b.append(message.obj.getClass().getSimpleName());
                }
                b.append(" }");
            }
            logd(" " + state.getClass().getSimpleName() + " " + b.toString());
        }

        P2pStateMachine(String name, Looper looper, boolean p2pSupported) {
            super(name, looper);
            addState(this.mDefaultState);
            addState(this.mP2pNotSupportedState, this.mDefaultState);
            addState(this.mP2pDisablingState, this.mDefaultState);
            addState(this.mP2pDisabledState, this.mDefaultState);
            addState(this.mP2pEnabledState, this.mDefaultState);
            addState(this.mInactiveState, this.mP2pEnabledState);
            addState(this.mGroupCreatingState, this.mP2pEnabledState);
            addState(this.mUserAuthorizingInviteRequestState, this.mGroupCreatingState);
            addState(this.mUserAuthorizingNegotiationRequestState, this.mGroupCreatingState);
            addState(this.mProvisionDiscoveryState, this.mGroupCreatingState);
            addState(this.mGroupNegotiationState, this.mGroupCreatingState);
            addState(this.mFrequencyConflictState, this.mGroupCreatingState);
            addState(this.mGroupCreatedState, this.mP2pEnabledState);
            addState(this.mUserAuthorizingJoinState, this.mGroupCreatedState);
            addState(this.mOngoingGroupRemovalState, this.mGroupCreatedState);
            if (p2pSupported) {
                setInitialState(this.mP2pDisabledState);
            } else {
                setInitialState(this.mP2pNotSupportedState);
            }
            setLogRecSize(50);
            setLogOnlyTransitions(true);
            if (p2pSupported) {
                WifiP2pServiceImpl.this.mContext.registerReceiver(new BroadcastReceiver(WifiP2pServiceImpl.this) {
                    public void onReceive(Context context, Intent intent) {
                        if (intent.getIntExtra("wifi_state", 4) == 3) {
                            boolean unused = P2pStateMachine.this.mIsWifiEnabled = true;
                            P2pStateMachine.this.checkAndReEnableP2p();
                        } else {
                            boolean unused2 = P2pStateMachine.this.mIsWifiEnabled = false;
                            int unused3 = P2pStateMachine.this.mSetupFailureCount = 0;
                            P2pStateMachine.this.sendMessage(WifiP2pServiceImpl.DISABLE_P2P);
                        }
                        P2pStateMachine.this.checkAndSendP2pStateChangedBroadcast();
                    }
                }, new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED"));
                WifiP2pServiceImpl.this.mContext.registerReceiver(new BroadcastReceiver(WifiP2pServiceImpl.this) {
                    public void onReceive(Context context, Intent intent) {
                        if (!WifiP2pServiceImpl.this.mWifiPermissionsUtil.isLocationModeEnabled()) {
                            P2pStateMachine.this.sendMessage(139268);
                        }
                    }
                }, new IntentFilter("android.location.MODE_CHANGED"));
                this.mWifiNative.registerInterfaceAvailableListener(new HalDeviceManager.InterfaceAvailableForRequestListener() {
                    public final void onAvailabilityChanged(boolean z) {
                        WifiP2pServiceImpl.P2pStateMachine.this.lambda$new$0$WifiP2pServiceImpl$P2pStateMachine(z);
                    }
                }, getHandler());
                WifiP2pServiceImpl.this.mFrameworkFacade.registerContentObserver(WifiP2pServiceImpl.this.mContext, Settings.Global.getUriFor("wifi_verbose_logging_enabled"), true, new ContentObserver(new Handler(looper), WifiP2pServiceImpl.this) {
                    public void onChange(boolean selfChange) {
                        P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                        p2pStateMachine.enableVerboseLogging(WifiP2pServiceImpl.this.mFrameworkFacade.getIntegerSetting(WifiP2pServiceImpl.this.mContext, "wifi_verbose_logging_enabled", 0));
                    }
                });
            }
        }

        public /* synthetic */ void lambda$new$0$WifiP2pServiceImpl$P2pStateMachine(boolean isAvailable) {
            this.mIsHalInterfaceAvailable = isAvailable;
            if (this.mSetupFailureCount < 10) {
                if (isAvailable) {
                    checkAndReEnableP2p();
                }
                checkAndSendP2pStateChangedBroadcast();
                return;
            }
            Log.i(WifiP2pServiceImpl.TAG, "Ignore InterfaceAvailable for continuous failures. count=" + this.mSetupFailureCount);
        }

        /* access modifiers changed from: private */
        public void enableVerboseLogging(int verbose) {
            boolean unused = WifiP2pServiceImpl.this.mVerboseLoggingEnabled = verbose > 0;
            this.mWifiNative.enableVerboseLogging(verbose);
            this.mWifiMonitor.enableVerboseLogging(verbose);
        }

        public void registerForWifiMonitorEvents() {
            this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiP2pMonitor.AP_STA_CONNECTED_EVENT, getHandler());
            this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiP2pMonitor.AP_STA_DISCONNECTED_EVENT, getHandler());
            this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiP2pMonitor.P2P_DEVICE_FOUND_EVENT, getHandler());
            this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiP2pMonitor.P2P_DEVICE_LOST_EVENT, getHandler());
            this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiP2pMonitor.P2P_FIND_STOPPED_EVENT, getHandler());
            this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiP2pMonitor.P2P_GO_NEGOTIATION_FAILURE_EVENT, getHandler());
            this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiP2pMonitor.P2P_GO_NEGOTIATION_REQUEST_EVENT, getHandler());
            this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiP2pMonitor.P2P_GO_NEGOTIATION_SUCCESS_EVENT, getHandler());
            this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiP2pMonitor.P2P_GROUP_FORMATION_FAILURE_EVENT, getHandler());
            this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiP2pMonitor.P2P_GROUP_FORMATION_SUCCESS_EVENT, getHandler());
            this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiP2pMonitor.P2P_GROUP_REMOVED_EVENT, getHandler());
            this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiP2pMonitor.P2P_GROUP_STARTED_EVENT, getHandler());
            this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiP2pMonitor.P2P_INVITATION_RECEIVED_EVENT, getHandler());
            this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiP2pMonitor.P2P_INVITATION_RESULT_EVENT, getHandler());
            this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiP2pMonitor.P2P_PROV_DISC_ENTER_PIN_EVENT, getHandler());
            this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiP2pMonitor.P2P_PROV_DISC_FAILURE_EVENT, getHandler());
            this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiP2pMonitor.P2P_PROV_DISC_PBC_REQ_EVENT, getHandler());
            this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiP2pMonitor.P2P_PROV_DISC_PBC_RSP_EVENT, getHandler());
            this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiP2pMonitor.P2P_PROV_DISC_SHOW_PIN_EVENT, getHandler());
            this.mWifiMonitor.registerHandler(this.mInterfaceName, WifiP2pMonitor.P2P_SERV_DISC_RESP_EVENT, getHandler());
            this.mWifiMonitor.registerHandler(this.mInterfaceName, 147457, getHandler());
            this.mWifiMonitor.registerHandler(this.mInterfaceName, 147458, getHandler());
            this.mWifiMonitor.startMonitoring(this.mInterfaceName);
        }

        class DefaultState extends State {
            DefaultState() {
            }

            public boolean processMessage(Message message) {
                if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                    P2pStateMachine.this.logd(getName() + message.toString());
                }
                int i = 2;
                switch (message.what) {
                    case 69632:
                        if (message.arg1 != 0) {
                            P2pStateMachine.this.loge("Full connection failure, error = " + message.arg1);
                            AsyncChannel unused = WifiP2pServiceImpl.this.mWifiChannel = null;
                            P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                            p2pStateMachine.transitionTo(p2pStateMachine.mP2pDisabledState);
                            break;
                        } else {
                            if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                P2pStateMachine.this.logd("Full connection with ClientModeImpl established");
                            }
                            AsyncChannel unused2 = WifiP2pServiceImpl.this.mWifiChannel = (AsyncChannel) message.obj;
                            break;
                        }
                    case 69633:
                        new WifiAsyncChannel(WifiP2pServiceImpl.TAG).connect(WifiP2pServiceImpl.this.mContext, P2pStateMachine.this.getHandler(), message.replyTo);
                        break;
                    case 69636:
                        if (message.arg1 == 2) {
                            P2pStateMachine.this.loge("Send failed, client connection lost");
                        } else {
                            P2pStateMachine.this.loge("Client connection lost with reason: " + message.arg1);
                        }
                        AsyncChannel unused3 = WifiP2pServiceImpl.this.mWifiChannel = null;
                        P2pStateMachine p2pStateMachine2 = P2pStateMachine.this;
                        p2pStateMachine2.transitionTo(p2pStateMachine2.mP2pDisabledState);
                        break;
                    case 139265:
                        P2pStateMachine.this.replyToMessage(message, 139266, 2);
                        break;
                    case 139268:
                        P2pStateMachine.this.replyToMessage(message, 139269, 2);
                        break;
                    case 139271:
                        P2pStateMachine.this.replyToMessage(message, 139272, 2);
                        break;
                    case 139274:
                        P2pStateMachine.this.replyToMessage(message, 139275, 2);
                        break;
                    case 139277:
                        P2pStateMachine.this.replyToMessage(message, 139278, 2);
                        break;
                    case 139280:
                        P2pStateMachine.this.replyToMessage(message, 139281, 2);
                        break;
                    case 139283:
                        P2pStateMachine p2pStateMachine3 = P2pStateMachine.this;
                        p2pStateMachine3.replyToMessage(message, 139284, (Object) p2pStateMachine3.getPeers(p2pStateMachine3.getCallingPkgName(message.sendingUid, message.replyTo), message.sendingUid));
                        break;
                    case 139285:
                        P2pStateMachine p2pStateMachine4 = P2pStateMachine.this;
                        p2pStateMachine4.replyToMessage(message, 139286, (Object) new WifiP2pInfo(p2pStateMachine4.mWifiP2pInfo));
                        break;
                    case 139287:
                        if (WifiP2pServiceImpl.this.mWifiPermissionsUtil.checkCanAccessWifiDirect(P2pStateMachine.this.getCallingPkgName(message.sendingUid, message.replyTo), message.sendingUid, false)) {
                            P2pStateMachine p2pStateMachine5 = P2pStateMachine.this;
                            p2pStateMachine5.replyToMessage(message, 139288, (Object) p2pStateMachine5.maybeEraseOwnDeviceAddress(p2pStateMachine5.mGroup, message.sendingUid));
                            break;
                        } else {
                            P2pStateMachine.this.replyToMessage(message, 139288, (Object) null);
                            break;
                        }
                    case 139292:
                        P2pStateMachine.this.replyToMessage(message, 139293, 2);
                        break;
                    case 139295:
                        P2pStateMachine.this.replyToMessage(message, 139296, 2);
                        break;
                    case 139298:
                        P2pStateMachine.this.replyToMessage(message, 139299, 2);
                        break;
                    case 139301:
                        P2pStateMachine.this.replyToMessage(message, 139302, 2);
                        break;
                    case 139304:
                        P2pStateMachine.this.replyToMessage(message, 139305, 2);
                        break;
                    case 139307:
                        P2pStateMachine.this.replyToMessage(message, 139308, 2);
                        break;
                    case 139310:
                        P2pStateMachine.this.replyToMessage(message, 139311, 2);
                        break;
                    case 139315:
                        P2pStateMachine.this.replyToMessage(message, 139316, 2);
                        break;
                    case 139318:
                        P2pStateMachine.this.replyToMessage(message, 139319, 2);
                        break;
                    case 139321:
                        P2pStateMachine p2pStateMachine6 = P2pStateMachine.this;
                        p2pStateMachine6.replyToMessage(message, 139322, (Object) new WifiP2pGroupList(p2pStateMachine6.maybeEraseOwnDeviceAddress(p2pStateMachine6.mGroups, message.sendingUid), (WifiP2pGroupList.GroupDeleteListener) null));
                        break;
                    case 139323:
                        if (WifiP2pServiceImpl.this.getWfdPermission(message.sendingUid)) {
                            P2pStateMachine.this.replyToMessage(message, 139324, 2);
                            break;
                        } else {
                            P2pStateMachine.this.replyToMessage(message, 139324, 0);
                            break;
                        }
                    case 139326:
                        P2pStateMachine.this.replyToMessage(message, 139327, 2);
                        break;
                    case 139329:
                    case 139332:
                    case 139335:
                    case WifiP2pServiceImpl.GROUP_CREATING_TIMED_OUT /*143361*/:
                    case WifiP2pServiceImpl.PEER_CONNECTION_USER_ACCEPT /*143362*/:
                    case WifiP2pServiceImpl.PEER_CONNECTION_USER_REJECT /*143363*/:
                    case WifiP2pServiceImpl.DROP_WIFI_USER_ACCEPT /*143364*/:
                    case WifiP2pServiceImpl.DROP_WIFI_USER_REJECT /*143365*/:
                    case WifiP2pServiceImpl.DISABLE_P2P_TIMED_OUT /*143366*/:
                    case WifiP2pServiceImpl.DISCONNECT_WIFI_RESPONSE /*143373*/:
                    case WifiP2pServiceImpl.SET_MIRACAST_MODE /*143374*/:
                    case WifiP2pServiceImpl.ENABLE_P2P /*143376*/:
                    case WifiP2pServiceImpl.DISABLE_P2P /*143377*/:
                    case WifiP2pServiceImpl.IPC_PRE_DHCP_ACTION /*143390*/:
                    case WifiP2pServiceImpl.IPC_POST_DHCP_ACTION /*143391*/:
                    case WifiP2pServiceImpl.IPC_DHCP_RESULTS /*143392*/:
                    case WifiP2pServiceImpl.IPC_PROVISIONING_SUCCESS /*143393*/:
                    case WifiP2pServiceImpl.IPC_PROVISIONING_FAILURE /*143394*/:
                    case 147457:
                    case 147458:
                    case WifiP2pMonitor.P2P_DEVICE_FOUND_EVENT:
                    case WifiP2pMonitor.P2P_DEVICE_LOST_EVENT:
                    case WifiP2pMonitor.P2P_GROUP_FORMATION_FAILURE_EVENT:
                    case WifiP2pMonitor.P2P_GROUP_REMOVED_EVENT:
                    case WifiP2pMonitor.P2P_INVITATION_RESULT_EVENT:
                    case WifiP2pMonitor.P2P_FIND_STOPPED_EVENT:
                    case WifiP2pMonitor.P2P_SERV_DISC_RESP_EVENT:
                    case WifiP2pMonitor.P2P_PROV_DISC_FAILURE_EVENT:
                        break;
                    case 139339:
                    case 139340:
                        P2pStateMachine.this.replyToMessage(message, 139341, (Object) null);
                        break;
                    case 139342:
                    case 139343:
                        P2pStateMachine.this.replyToMessage(message, 139345, 2);
                        break;
                    case 139346:
                        if (!P2pStateMachine.this.factoryReset(message.sendingUid)) {
                            P2pStateMachine.this.replyToMessage(message, 139347, 0);
                            break;
                        } else {
                            P2pStateMachine.this.replyToMessage(message, 139348);
                            break;
                        }
                    case 139349:
                        if (!WifiP2pServiceImpl.this.mWifiPermissionsUtil.checkNetworkStackPermission(message.sendingUid)) {
                            P2pStateMachine.this.loge("Permission violation - no NETWORK_STACK permission, uid = " + message.sendingUid);
                            P2pStateMachine.this.replyToMessage(message, 139350, (Object) null);
                            break;
                        } else {
                            P2pStateMachine p2pStateMachine7 = P2pStateMachine.this;
                            p2pStateMachine7.replyToMessage(message, 139350, (Object) p2pStateMachine7.mSavedPeerConfig);
                            break;
                        }
                    case 139351:
                        if (!WifiP2pServiceImpl.this.mWifiPermissionsUtil.checkNetworkStackPermission(message.sendingUid)) {
                            P2pStateMachine.this.loge("Permission violation - no NETWORK_STACK permission, uid = " + message.sendingUid);
                            P2pStateMachine.this.replyToMessage(message, 139352);
                            break;
                        } else {
                            WifiP2pConfig peerConfig = (WifiP2pConfig) message.obj;
                            if (!P2pStateMachine.this.isConfigInvalid(peerConfig)) {
                                P2pStateMachine.this.logd("setSavedPeerConfig to " + peerConfig);
                                WifiP2pConfig unused4 = P2pStateMachine.this.mSavedPeerConfig = peerConfig;
                                P2pStateMachine.this.replyToMessage(message, 139353);
                                break;
                            } else {
                                P2pStateMachine.this.loge("Dropping set mSavedPeerConfig requeset" + peerConfig);
                                P2pStateMachine.this.replyToMessage(message, 139352);
                                break;
                            }
                        }
                    case 139354:
                        if (WifiP2pServiceImpl.this.getWfdPermission(message.sendingUid)) {
                            P2pStateMachine.this.replyToMessage(message, 139355, 2);
                            break;
                        } else {
                            P2pStateMachine.this.replyToMessage(message, 139355, 0);
                            break;
                        }
                    case 139357:
                        P2pStateMachine p2pStateMachine8 = P2pStateMachine.this;
                        if (!p2pStateMachine8.mIsWifiEnabled || !P2pStateMachine.this.isHalInterfaceAvailable()) {
                            i = 1;
                        }
                        p2pStateMachine8.replyToMessage(message, 139358, i);
                        break;
                    case 139359:
                        P2pStateMachine p2pStateMachine9 = P2pStateMachine.this;
                        if (!WifiP2pServiceImpl.this.mDiscoveryStarted) {
                            i = 1;
                        }
                        p2pStateMachine9.replyToMessage(message, 139360, i);
                        break;
                    case 139361:
                        P2pStateMachine p2pStateMachine10 = P2pStateMachine.this;
                        p2pStateMachine10.replyToMessage(message, 139362, (Object) WifiP2pServiceImpl.this.mNetworkInfo);
                        break;
                    case 139363:
                        if (message.obj instanceof Bundle) {
                            Bundle bundle = (Bundle) message.obj;
                            String pkgName = bundle.getString("android.net.wifi.p2p.CALLING_PACKAGE");
                            IBinder binder = bundle.getBinder("android.net.wifi.p2p.CALLING_BINDER");
                            try {
                                WifiP2pServiceImpl.this.mWifiPermissionsUtil.checkPackage(message.sendingUid, pkgName);
                                if (!(binder == null || message.replyTo == null)) {
                                    WifiP2pServiceImpl.this.mClientChannelList.put(binder, message.replyTo);
                                    String unused5 = P2pStateMachine.this.getClientInfo(message.replyTo, true).mPackageName = pkgName;
                                    break;
                                }
                            } catch (SecurityException se) {
                                P2pStateMachine.this.loge("Unable to update calling package, " + se);
                                break;
                            }
                        }
                        break;
                    case 139364:
                        if (WifiP2pServiceImpl.this.mWifiPermissionsUtil.checkCanAccessWifiDirect(P2pStateMachine.this.getCallingPkgName(message.sendingUid, message.replyTo), message.sendingUid, false)) {
                            P2pStateMachine p2pStateMachine11 = P2pStateMachine.this;
                            p2pStateMachine11.replyToMessage(message, 139365, (Object) p2pStateMachine11.maybeEraseOwnDeviceAddress(WifiP2pServiceImpl.this.mThisDevice, message.sendingUid));
                            break;
                        } else {
                            P2pStateMachine.this.replyToMessage(message, 139365, (Object) null);
                            break;
                        }
                    case WifiP2pServiceImpl.BLOCK_DISCOVERY /*143375*/:
                        boolean unused6 = WifiP2pServiceImpl.this.mDiscoveryBlocked = message.arg1 == 1;
                        boolean unused7 = WifiP2pServiceImpl.this.mDiscoveryPostponed = false;
                        if (WifiP2pServiceImpl.this.mDiscoveryBlocked) {
                            if (message.obj != null) {
                                try {
                                    ((StateMachine) message.obj).sendMessage(message.arg2);
                                    break;
                                } catch (Exception e) {
                                    P2pStateMachine.this.loge("unable to send BLOCK_DISCOVERY response: " + e);
                                    break;
                                }
                            } else {
                                Log.e(WifiP2pServiceImpl.TAG, "Illegal argument(s)");
                                break;
                            }
                        }
                        break;
                    case WifiP2pMonitor.P2P_GROUP_STARTED_EVENT:
                        if (message.obj != null) {
                            WifiP2pGroup unused8 = P2pStateMachine.this.mGroup = (WifiP2pGroup) message.obj;
                            P2pStateMachine.this.loge("Unexpected group creation, remove " + P2pStateMachine.this.mGroup);
                            P2pStateMachine.this.mWifiNative.p2pGroupRemove(P2pStateMachine.this.mGroup.getInterface());
                            break;
                        } else {
                            Log.e(WifiP2pServiceImpl.TAG, "Illegal arguments");
                            break;
                        }
                    default:
                        P2pStateMachine.this.loge("Unhandled message " + message);
                        return false;
                }
                return true;
            }
        }

        class P2pNotSupportedState extends State {
            P2pNotSupportedState() {
            }

            public boolean processMessage(Message message) {
                if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                    P2pStateMachine.this.logStateAndMessage(message, this);
                }
                switch (message.what) {
                    case 139265:
                        P2pStateMachine.this.replyToMessage(message, 139266, 1);
                        break;
                    case 139268:
                        P2pStateMachine.this.replyToMessage(message, 139269, 1);
                        break;
                    case 139271:
                        P2pStateMachine.this.replyToMessage(message, 139272, 1);
                        break;
                    case 139274:
                        P2pStateMachine.this.replyToMessage(message, 139275, 1);
                        break;
                    case 139277:
                        P2pStateMachine.this.replyToMessage(message, 139278, 1);
                        break;
                    case 139280:
                        P2pStateMachine.this.replyToMessage(message, 139281, 1);
                        break;
                    case 139292:
                        P2pStateMachine.this.replyToMessage(message, 139293, 1);
                        break;
                    case 139295:
                        P2pStateMachine.this.replyToMessage(message, 139296, 1);
                        break;
                    case 139298:
                        P2pStateMachine.this.replyToMessage(message, 139299, 1);
                        break;
                    case 139301:
                        P2pStateMachine.this.replyToMessage(message, 139302, 1);
                        break;
                    case 139304:
                        P2pStateMachine.this.replyToMessage(message, 139305, 1);
                        break;
                    case 139307:
                        P2pStateMachine.this.replyToMessage(message, 139308, 1);
                        break;
                    case 139310:
                        P2pStateMachine.this.replyToMessage(message, 139311, 1);
                        break;
                    case 139315:
                        P2pStateMachine.this.replyToMessage(message, 139316, 1);
                        break;
                    case 139318:
                        P2pStateMachine.this.replyToMessage(message, 139319, 1);
                        break;
                    case 139323:
                        if (WifiP2pServiceImpl.this.getWfdPermission(message.sendingUid)) {
                            P2pStateMachine.this.replyToMessage(message, 139324, 1);
                            break;
                        } else {
                            P2pStateMachine.this.replyToMessage(message, 139324, 0);
                            break;
                        }
                    case 139326:
                        P2pStateMachine.this.replyToMessage(message, 139327, 1);
                        break;
                    case 139329:
                        P2pStateMachine.this.replyToMessage(message, 139330, 1);
                        break;
                    case 139332:
                        P2pStateMachine.this.replyToMessage(message, 139333, 1);
                        break;
                    case 139346:
                        P2pStateMachine.this.replyToMessage(message, 139347, 1);
                        break;
                    case 139354:
                        if (WifiP2pServiceImpl.this.getWfdPermission(message.sendingUid)) {
                            P2pStateMachine.this.replyToMessage(message, 139355, 1);
                            break;
                        } else {
                            P2pStateMachine.this.replyToMessage(message, 139355, 0);
                            break;
                        }
                    default:
                        return false;
                }
                return true;
            }
        }

        class P2pDisablingState extends State {
            P2pDisablingState() {
            }

            public void enter() {
                if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                    P2pStateMachine.this.logd(getName());
                }
                P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                p2pStateMachine.sendMessageDelayed(p2pStateMachine.obtainMessage(WifiP2pServiceImpl.DISABLE_P2P_TIMED_OUT, WifiP2pServiceImpl.access$4304(), 0), RttServiceImpl.HAL_RANGING_TIMEOUT_MS);
            }

            public boolean processMessage(Message message) {
                if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                    P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                    p2pStateMachine.logd(getName() + message.toString());
                }
                int i = message.what;
                if (i != 143366) {
                    if (i != 147458) {
                        switch (i) {
                            case WifiP2pServiceImpl.ENABLE_P2P /*143376*/:
                            case WifiP2pServiceImpl.DISABLE_P2P /*143377*/:
                            case WifiP2pServiceImpl.REMOVE_CLIENT_INFO /*143378*/:
                                P2pStateMachine.this.deferMessage(message);
                                return true;
                            default:
                                return false;
                        }
                    } else {
                        if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                            P2pStateMachine.this.logd("p2p socket connection lost");
                        }
                        P2pStateMachine p2pStateMachine2 = P2pStateMachine.this;
                        p2pStateMachine2.transitionTo(p2pStateMachine2.mP2pDisabledState);
                        return true;
                    }
                } else if (WifiP2pServiceImpl.sDisableP2pTimeoutIndex != message.arg1) {
                    return true;
                } else {
                    P2pStateMachine.this.loge("P2p disable timed out");
                    P2pStateMachine p2pStateMachine3 = P2pStateMachine.this;
                    p2pStateMachine3.transitionTo(p2pStateMachine3.mP2pDisabledState);
                    return true;
                }
            }
        }

        class P2pDisabledState extends State {
            P2pDisabledState() {
            }

            public void enter() {
                if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                    P2pStateMachine.this.logd(getName());
                }
            }

            private void setupInterfaceFeatures(String interfaceName) {
                if (WifiP2pServiceImpl.this.mContext.getResources().getBoolean(17891599)) {
                    Log.i(WifiP2pServiceImpl.TAG, "Supported feature: P2P MAC randomization");
                    P2pStateMachine.this.mWifiNative.setMacRandomization(true);
                    return;
                }
                P2pStateMachine.this.mWifiNative.setMacRandomization(false);
            }

            public boolean processMessage(Message message) {
                if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                    P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                    p2pStateMachine.logd(getName() + message.toString());
                }
                int i = message.what;
                if (i != 143376) {
                    if (i != 143378) {
                        return false;
                    }
                    if (!(message.obj instanceof IBinder)) {
                        P2pStateMachine.this.loge("Invalid obj when REMOVE_CLIENT_INFO");
                        return true;
                    }
                    Map access$3700 = WifiP2pServiceImpl.this.mClientChannelList;
                    ClientInfo clientInfo = (ClientInfo) WifiP2pServiceImpl.this.mClientInfoList.remove((Messenger) access$3700.remove((IBinder) message.obj));
                    if (clientInfo == null) {
                        return true;
                    }
                    P2pStateMachine p2pStateMachine2 = P2pStateMachine.this;
                    p2pStateMachine2.logd("Remove client - " + clientInfo.mPackageName);
                    return true;
                } else if (!P2pStateMachine.this.mIsWifiEnabled) {
                    Log.e(WifiP2pServiceImpl.TAG, "Ignore P2P enable since wifi is " + P2pStateMachine.this.mIsWifiEnabled);
                    return true;
                } else {
                    P2pStateMachine p2pStateMachine3 = P2pStateMachine.this;
                    String unused = p2pStateMachine3.mInterfaceName = p2pStateMachine3.mWifiNative.setupInterface(
                    /*  JADX ERROR: Method code generation error
                        jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x00be: INVOKE  
                          (r1v6 'p2pStateMachine3' com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine)
                          (wrap: java.lang.String : 0x00ba: INVOKE  (r4v1 java.lang.String) = 
                          (wrap: com.android.server.wifi.p2p.WifiP2pNative : 0x00ab: INVOKE  (r4v0 com.android.server.wifi.p2p.WifiP2pNative) = 
                          (r1v6 'p2pStateMachine3' com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine)
                         com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.access$600(com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine):com.android.server.wifi.p2p.WifiP2pNative type: STATIC)
                          (wrap: com.android.server.wifi.p2p.-$$Lambda$WifiP2pServiceImpl$P2pStateMachine$P2pDisabledState$13XANUNRJEt7WjtJr5tKTd2g-PM : 0x00b1: CONSTRUCTOR  (r5v0 com.android.server.wifi.p2p.-$$Lambda$WifiP2pServiceImpl$P2pStateMachine$P2pDisabledState$13XANUNRJEt7WjtJr5tKTd2g-PM) = 
                          (r7v0 'this' com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine$P2pDisabledState A[THIS])
                         call: com.android.server.wifi.p2p.-$$Lambda$WifiP2pServiceImpl$P2pStateMachine$P2pDisabledState$13XANUNRJEt7WjtJr5tKTd2g-PM.<init>(com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine$P2pDisabledState):void type: CONSTRUCTOR)
                          (wrap: android.os.Handler : 0x00b6: INVOKE  (r6v1 android.os.Handler) = 
                          (wrap: com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine : 0x00b4: IGET  (r6v0 com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine) = 
                          (r7v0 'this' com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine$P2pDisabledState A[THIS])
                         com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.P2pDisabledState.this$1 com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine)
                         com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.getHandler():android.os.Handler type: VIRTUAL)
                         com.android.server.wifi.p2p.WifiP2pNative.setupInterface(com.android.server.wifi.HalDeviceManager$InterfaceDestroyedListener, android.os.Handler):java.lang.String type: VIRTUAL)
                         com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.access$4402(com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine, java.lang.String):java.lang.String type: STATIC in method: com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.P2pDisabledState.processMessage(android.os.Message):boolean, dex: classes.dex
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:221)
                        	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.RegionGen.makeRegionIndent(RegionGen.java:98)
                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:156)
                        	at jadx.core.codegen.RegionGen.connectElseIf(RegionGen.java:175)
                        	at jadx.core.codegen.RegionGen.makeIf(RegionGen.java:152)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:62)
                        	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
                        	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
                        	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:211)
                        	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:204)
                        	at jadx.core.codegen.ClassGen.addMethodCode(ClassGen.java:318)
                        	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:271)
                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:240)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:184)
                        	at java.util.ArrayList.forEach(ArrayList.java:1257)
                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:390)
                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)
                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:471)
                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:151)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:174)
                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:418)
                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                        	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:184)
                        	at java.util.ArrayList.forEach(ArrayList.java:1257)
                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:390)
                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)
                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:471)
                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:151)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:174)
                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:418)
                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                        	at jadx.core.codegen.ClassGen.addInnerClass(ClassGen.java:249)
                        	at jadx.core.codegen.ClassGen.lambda$addInnerClsAndMethods$2(ClassGen.java:238)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.accept(ForEachOps.java:184)
                        	at java.util.ArrayList.forEach(ArrayList.java:1257)
                        	at java.util.stream.SortedOps$RefSortingSink.end(SortedOps.java:390)
                        	at java.util.stream.Sink$ChainedReference.end(Sink.java:258)
                        	at java.util.stream.AbstractPipeline.copyInto(AbstractPipeline.java:482)
                        	at java.util.stream.AbstractPipeline.wrapAndCopyInto(AbstractPipeline.java:471)
                        	at java.util.stream.ForEachOps$ForEachOp.evaluateSequential(ForEachOps.java:151)
                        	at java.util.stream.ForEachOps$ForEachOp$OfRef.evaluateSequential(ForEachOps.java:174)
                        	at java.util.stream.AbstractPipeline.evaluate(AbstractPipeline.java:234)
                        	at java.util.stream.ReferencePipeline.forEach(ReferencePipeline.java:418)
                        	at jadx.core.codegen.ClassGen.addInnerClsAndMethods(ClassGen.java:236)
                        	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:227)
                        	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:112)
                        	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:78)
                        	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
                        	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:33)
                        	at jadx.core.codegen.CodeGen.generate(CodeGen.java:21)
                        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:61)
                        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:273)
                        Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0000: IPUT  
                          (wrap: java.lang.String : 0x00ba: INVOKE  (r4v1 java.lang.String) = 
                          (wrap: com.android.server.wifi.p2p.WifiP2pNative : 0x00ab: INVOKE  (r4v0 com.android.server.wifi.p2p.WifiP2pNative) = 
                          (r1v6 'p2pStateMachine3' com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine)
                         com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.access$600(com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine):com.android.server.wifi.p2p.WifiP2pNative type: STATIC)
                          (wrap: com.android.server.wifi.p2p.-$$Lambda$WifiP2pServiceImpl$P2pStateMachine$P2pDisabledState$13XANUNRJEt7WjtJr5tKTd2g-PM : 0x00b1: CONSTRUCTOR  (r5v0 com.android.server.wifi.p2p.-$$Lambda$WifiP2pServiceImpl$P2pStateMachine$P2pDisabledState$13XANUNRJEt7WjtJr5tKTd2g-PM) = 
                          (r7v0 'this' com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine$P2pDisabledState A[THIS])
                         call: com.android.server.wifi.p2p.-$$Lambda$WifiP2pServiceImpl$P2pStateMachine$P2pDisabledState$13XANUNRJEt7WjtJr5tKTd2g-PM.<init>(com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine$P2pDisabledState):void type: CONSTRUCTOR)
                          (wrap: android.os.Handler : 0x00b6: INVOKE  (r6v1 android.os.Handler) = 
                          (wrap: com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine : 0x00b4: IGET  (r6v0 com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine) = 
                          (r7v0 'this' com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine$P2pDisabledState A[THIS])
                         com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.P2pDisabledState.this$1 com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine)
                         com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.getHandler():android.os.Handler type: VIRTUAL)
                         com.android.server.wifi.p2p.WifiP2pNative.setupInterface(com.android.server.wifi.HalDeviceManager$InterfaceDestroyedListener, android.os.Handler):java.lang.String type: VIRTUAL)
                          (r1v6 'p2pStateMachine3' com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine)
                         com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.mInterfaceName java.lang.String in method: com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.P2pDisabledState.processMessage(android.os.Message):boolean, dex: classes.dex
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                        	at jadx.core.codegen.InsnGen.inlineMethod(InsnGen.java:924)
                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:684)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:250)
                        	... 66 more
                        Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x00ba: INVOKE  (r4v1 java.lang.String) = 
                          (wrap: com.android.server.wifi.p2p.WifiP2pNative : 0x00ab: INVOKE  (r4v0 com.android.server.wifi.p2p.WifiP2pNative) = 
                          (r1v6 'p2pStateMachine3' com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine)
                         com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.access$600(com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine):com.android.server.wifi.p2p.WifiP2pNative type: STATIC)
                          (wrap: com.android.server.wifi.p2p.-$$Lambda$WifiP2pServiceImpl$P2pStateMachine$P2pDisabledState$13XANUNRJEt7WjtJr5tKTd2g-PM : 0x00b1: CONSTRUCTOR  (r5v0 com.android.server.wifi.p2p.-$$Lambda$WifiP2pServiceImpl$P2pStateMachine$P2pDisabledState$13XANUNRJEt7WjtJr5tKTd2g-PM) = 
                          (r7v0 'this' com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine$P2pDisabledState A[THIS])
                         call: com.android.server.wifi.p2p.-$$Lambda$WifiP2pServiceImpl$P2pStateMachine$P2pDisabledState$13XANUNRJEt7WjtJr5tKTd2g-PM.<init>(com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine$P2pDisabledState):void type: CONSTRUCTOR)
                          (wrap: android.os.Handler : 0x00b6: INVOKE  (r6v1 android.os.Handler) = 
                          (wrap: com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine : 0x00b4: IGET  (r6v0 com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine) = 
                          (r7v0 'this' com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine$P2pDisabledState A[THIS])
                         com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.P2pDisabledState.this$1 com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine)
                         com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.getHandler():android.os.Handler type: VIRTUAL)
                         com.android.server.wifi.p2p.WifiP2pNative.setupInterface(com.android.server.wifi.HalDeviceManager$InterfaceDestroyedListener, android.os.Handler):java.lang.String type: VIRTUAL in method: com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.P2pDisabledState.processMessage(android.os.Message):boolean, dex: classes.dex
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:429)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                        	... 70 more
                        Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x00b1: CONSTRUCTOR  (r5v0 com.android.server.wifi.p2p.-$$Lambda$WifiP2pServiceImpl$P2pStateMachine$P2pDisabledState$13XANUNRJEt7WjtJr5tKTd2g-PM) = 
                          (r7v0 'this' com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine$P2pDisabledState A[THIS])
                         call: com.android.server.wifi.p2p.-$$Lambda$WifiP2pServiceImpl$P2pStateMachine$P2pDisabledState$13XANUNRJEt7WjtJr5tKTd2g-PM.<init>(com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine$P2pDisabledState):void type: CONSTRUCTOR in method: com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.P2pDisabledState.processMessage(android.os.Message):boolean, dex: classes.dex
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:256)
                        	at jadx.core.codegen.InsnGen.addWrappedArg(InsnGen.java:123)
                        	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:107)
                        	at jadx.core.codegen.InsnGen.generateMethodArguments(InsnGen.java:787)
                        	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:728)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:368)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                        	... 74 more
                        Caused by: jadx.core.utils.exceptions.JadxRuntimeException: Expected class to be processed at this point, class: com.android.server.wifi.p2p.-$$Lambda$WifiP2pServiceImpl$P2pStateMachine$P2pDisabledState$13XANUNRJEt7WjtJr5tKTd2g-PM, state: NOT_LOADED
                        	at jadx.core.dex.nodes.ClassNode.ensureProcessed(ClassNode.java:260)
                        	at jadx.core.codegen.InsnGen.makeConstructor(InsnGen.java:606)
                        	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:364)
                        	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:231)
                        	... 80 more
                        */
                    /*
                        this = this;
                        java.lang.String r0 = "Unable to change interface settings: "
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        com.android.server.wifi.p2p.WifiP2pServiceImpl r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.this
                        boolean r1 = r1.mVerboseLoggingEnabled
                        if (r1 == 0) goto L_0x0028
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        java.lang.StringBuilder r2 = new java.lang.StringBuilder
                        r2.<init>()
                        java.lang.String r3 = r7.getName()
                        r2.append(r3)
                        java.lang.String r3 = r8.toString()
                        r2.append(r3)
                        java.lang.String r2 = r2.toString()
                        r1.logd(r2)
                    L_0x0028:
                        int r1 = r8.what
                        r2 = 143376(0x23010, float:2.00913E-40)
                        r3 = 0
                        if (r1 == r2) goto L_0x0083
                        r0 = 143378(0x23012, float:2.00915E-40)
                        if (r1 == r0) goto L_0x0036
                        return r3
                    L_0x0036:
                        java.lang.Object r0 = r8.obj
                        boolean r0 = r0 instanceof android.os.IBinder
                        if (r0 != 0) goto L_0x0045
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        java.lang.String r1 = "Invalid obj when REMOVE_CLIENT_INFO"
                        r0.loge(r1)
                        goto L_0x012d
                    L_0x0045:
                        java.lang.Object r0 = r8.obj
                        android.os.IBinder r0 = (android.os.IBinder) r0
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        com.android.server.wifi.p2p.WifiP2pServiceImpl r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.this
                        java.util.Map r1 = r1.mClientChannelList
                        java.lang.Object r1 = r1.remove(r0)
                        android.os.Messenger r1 = (android.os.Messenger) r1
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r2 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        com.android.server.wifi.p2p.WifiP2pServiceImpl r2 = com.android.server.wifi.p2p.WifiP2pServiceImpl.this
                        java.util.HashMap r2 = r2.mClientInfoList
                        java.lang.Object r2 = r2.remove(r1)
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$ClientInfo r2 = (com.android.server.wifi.p2p.WifiP2pServiceImpl.ClientInfo) r2
                        if (r2 == 0) goto L_0x012d
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r3 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        java.lang.StringBuilder r4 = new java.lang.StringBuilder
                        r4.<init>()
                        java.lang.String r5 = "Remove client - "
                        r4.append(r5)
                        java.lang.String r5 = r2.mPackageName
                        r4.append(r5)
                        java.lang.String r4 = r4.toString()
                        r3.logd(r4)
                        goto L_0x012d
                    L_0x0083:
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        boolean r1 = r1.mIsWifiEnabled
                        java.lang.String r2 = "WifiP2pService"
                        if (r1 != 0) goto L_0x00a9
                        java.lang.StringBuilder r0 = new java.lang.StringBuilder
                        r0.<init>()
                        java.lang.String r1 = "Ignore P2P enable since wifi is "
                        r0.append(r1)
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        boolean r1 = r1.mIsWifiEnabled
                        r0.append(r1)
                        java.lang.String r0 = r0.toString()
                        android.util.Log.e(r2, r0)
                        goto L_0x012d
                    L_0x00a9:
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        com.android.server.wifi.p2p.WifiP2pNative r4 = r1.mWifiNative
                        com.android.server.wifi.p2p.-$$Lambda$WifiP2pServiceImpl$P2pStateMachine$P2pDisabledState$13XANUNRJEt7WjtJr5tKTd2g-PM r5 = new com.android.server.wifi.p2p.-$$Lambda$WifiP2pServiceImpl$P2pStateMachine$P2pDisabledState$13XANUNRJEt7WjtJr5tKTd2g-PM
                        r5.<init>(r7)
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r6 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        android.os.Handler r6 = r6.getHandler()
                        java.lang.String r4 = r4.setupInterface(r5, r6)
                        java.lang.String unused = r1.mInterfaceName = r4
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        java.lang.String r1 = r1.mInterfaceName
                        if (r1 != 0) goto L_0x00d4
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.access$1008(r0)
                        java.lang.String r0 = "Failed to setup interface for P2P"
                        android.util.Log.e(r2, r0)
                        goto L_0x012d
                    L_0x00d4:
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        int unused = r1.mSetupFailureCount = r3
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        java.lang.String r1 = r1.mInterfaceName
                        r7.setupInterfaceFeatures(r1)
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this     // Catch:{ RemoteException -> 0x0108, IllegalStateException -> 0x00f2 }
                        com.android.server.wifi.p2p.WifiP2pServiceImpl r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.this     // Catch:{ RemoteException -> 0x0108, IllegalStateException -> 0x00f2 }
                        android.os.INetworkManagementService r1 = r1.mNwService     // Catch:{ RemoteException -> 0x0108, IllegalStateException -> 0x00f2 }
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r2 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this     // Catch:{ RemoteException -> 0x0108, IllegalStateException -> 0x00f2 }
                        java.lang.String r2 = r2.mInterfaceName     // Catch:{ RemoteException -> 0x0108, IllegalStateException -> 0x00f2 }
                        r1.setInterfaceUp(r2)     // Catch:{ RemoteException -> 0x0108, IllegalStateException -> 0x00f2 }
                        goto L_0x011d
                    L_0x00f2:
                        r1 = move-exception
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r2 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        java.lang.StringBuilder r3 = new java.lang.StringBuilder
                        r3.<init>()
                        r3.append(r0)
                        r3.append(r1)
                        java.lang.String r0 = r3.toString()
                        r2.loge(r0)
                        goto L_0x011e
                    L_0x0108:
                        r1 = move-exception
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r2 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        java.lang.StringBuilder r3 = new java.lang.StringBuilder
                        r3.<init>()
                        r3.append(r0)
                        r3.append(r1)
                        java.lang.String r0 = r3.toString()
                        r2.loge(r0)
                    L_0x011d:
                    L_0x011e:
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        r0.registerForWifiMonitorEvents()
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine$InactiveState r1 = r0.mInactiveState
                        r0.transitionTo(r1)
                    L_0x012d:
                        r0 = 1
                        return r0
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.P2pDisabledState.processMessage(android.os.Message):boolean");
                }

                public /* synthetic */ void lambda$processMessage$0$WifiP2pServiceImpl$P2pStateMachine$P2pDisabledState(String ifaceName) {
                    boolean unused = P2pStateMachine.this.mIsHalInterfaceAvailable = false;
                    P2pStateMachine.this.sendMessage(WifiP2pServiceImpl.DISABLE_P2P);
                    P2pStateMachine.this.checkAndSendP2pStateChangedBroadcast();
                }
            }

            class P2pEnabledState extends State {
                P2pEnabledState() {
                }

                public void enter() {
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        P2pStateMachine.this.logd(getName());
                    }
                    WifiP2pServiceImpl.this.mNetworkInfo.setIsAvailable(true);
                    if (P2pStateMachine.this.isPendingFactoryReset()) {
                        boolean unused = P2pStateMachine.this.factoryReset(1000);
                    }
                    P2pStateMachine.this.sendP2pConnectionChangedBroadcast();
                    P2pStateMachine.this.initializeP2pSettings();
                }

                public boolean processMessage(Message message) {
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                        p2pStateMachine.logd(getName() + message.toString());
                    }
                    switch (message.what) {
                        case 139265:
                            if (WifiP2pServiceImpl.this.mWifiPermissionsUtil.checkCanAccessWifiDirect(P2pStateMachine.this.getCallingPkgName(message.sendingUid, message.replyTo), message.sendingUid, true)) {
                                if (!WifiP2pServiceImpl.this.mDiscoveryBlocked) {
                                    P2pStateMachine.this.clearSupplicantServiceRequest();
                                    if (!P2pStateMachine.this.mWifiNative.p2pFind(120)) {
                                        P2pStateMachine.this.replyToMessage(message, 139266, 0);
                                        break;
                                    } else {
                                        WifiP2pServiceImpl.this.mWifiP2pMetrics.incrementPeerScans();
                                        P2pStateMachine.this.replyToMessage(message, 139267);
                                        P2pStateMachine.this.sendP2pDiscoveryChangedBroadcast(true);
                                        break;
                                    }
                                } else {
                                    P2pStateMachine.this.replyToMessage(message, 139266, 2);
                                    break;
                                }
                            } else {
                                P2pStateMachine.this.replyToMessage(message, 139266, 0);
                                break;
                            }
                        case 139268:
                            if (!P2pStateMachine.this.mWifiNative.p2pStopFind()) {
                                P2pStateMachine.this.replyToMessage(message, 139269, 0);
                                break;
                            } else {
                                P2pStateMachine.this.replyToMessage(message, 139270);
                                break;
                            }
                        case 139292:
                            if (WifiP2pServiceImpl.this.mWifiPermissionsUtil.checkCanAccessWifiDirect(P2pStateMachine.this.getCallingPkgName(message.sendingUid, message.replyTo), message.sendingUid, false)) {
                                if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                    P2pStateMachine p2pStateMachine2 = P2pStateMachine.this;
                                    p2pStateMachine2.logd(getName() + " add service");
                                }
                                if (!P2pStateMachine.this.addLocalService(message.replyTo, (WifiP2pServiceInfo) message.obj)) {
                                    P2pStateMachine.this.replyToMessage(message, 139293);
                                    break;
                                } else {
                                    P2pStateMachine.this.replyToMessage(message, 139294);
                                    break;
                                }
                            } else {
                                P2pStateMachine.this.replyToMessage(message, 139293);
                                break;
                            }
                        case 139295:
                            if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                P2pStateMachine p2pStateMachine3 = P2pStateMachine.this;
                                p2pStateMachine3.logd(getName() + " remove service");
                            }
                            P2pStateMachine.this.removeLocalService(message.replyTo, (WifiP2pServiceInfo) message.obj);
                            P2pStateMachine.this.replyToMessage(message, 139297);
                            break;
                        case 139298:
                            if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                P2pStateMachine p2pStateMachine4 = P2pStateMachine.this;
                                p2pStateMachine4.logd(getName() + " clear service");
                            }
                            P2pStateMachine.this.clearLocalServices(message.replyTo);
                            P2pStateMachine.this.replyToMessage(message, 139300);
                            break;
                        case 139301:
                            if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                P2pStateMachine p2pStateMachine5 = P2pStateMachine.this;
                                p2pStateMachine5.logd(getName() + " add service request");
                            }
                            if (P2pStateMachine.this.addServiceRequest(message.replyTo, (WifiP2pServiceRequest) message.obj)) {
                                P2pStateMachine.this.replyToMessage(message, 139303);
                                break;
                            } else {
                                P2pStateMachine.this.replyToMessage(message, 139302);
                                break;
                            }
                        case 139304:
                            if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                P2pStateMachine p2pStateMachine6 = P2pStateMachine.this;
                                p2pStateMachine6.logd(getName() + " remove service request");
                            }
                            P2pStateMachine.this.removeServiceRequest(message.replyTo, (WifiP2pServiceRequest) message.obj);
                            P2pStateMachine.this.replyToMessage(message, 139306);
                            break;
                        case 139307:
                            if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                P2pStateMachine p2pStateMachine7 = P2pStateMachine.this;
                                p2pStateMachine7.logd(getName() + " clear service request");
                            }
                            P2pStateMachine.this.clearServiceRequests(message.replyTo);
                            P2pStateMachine.this.replyToMessage(message, 139309);
                            break;
                        case 139310:
                            if (WifiP2pServiceImpl.this.mWifiPermissionsUtil.checkCanAccessWifiDirect(P2pStateMachine.this.getCallingPkgName(message.sendingUid, message.replyTo), message.sendingUid, true)) {
                                if (!WifiP2pServiceImpl.this.mDiscoveryBlocked) {
                                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                        P2pStateMachine p2pStateMachine8 = P2pStateMachine.this;
                                        p2pStateMachine8.logd(getName() + " discover services");
                                    }
                                    if (P2pStateMachine.this.updateSupplicantServiceRequest()) {
                                        if (!P2pStateMachine.this.mWifiNative.p2pFind(120)) {
                                            P2pStateMachine.this.replyToMessage(message, 139311, 0);
                                            break;
                                        } else {
                                            WifiP2pServiceImpl.this.mWifiP2pMetrics.incrementServiceScans();
                                            P2pStateMachine.this.replyToMessage(message, 139312);
                                            break;
                                        }
                                    } else {
                                        P2pStateMachine.this.replyToMessage(message, 139311, 3);
                                        break;
                                    }
                                } else {
                                    P2pStateMachine.this.replyToMessage(message, 139311, 2);
                                    break;
                                }
                            } else {
                                P2pStateMachine.this.replyToMessage(message, 139311, 0);
                                break;
                            }
                        case 139315:
                            WifiP2pDevice d = (WifiP2pDevice) message.obj;
                            if (d != null && P2pStateMachine.this.setAndPersistDeviceName(d.deviceName)) {
                                if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                    P2pStateMachine p2pStateMachine9 = P2pStateMachine.this;
                                    p2pStateMachine9.logd("set device name " + d.deviceName);
                                }
                                P2pStateMachine.this.replyToMessage(message, 139317);
                                break;
                            } else {
                                P2pStateMachine.this.replyToMessage(message, 139316, 0);
                                break;
                            }
                        case 139318:
                            if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                P2pStateMachine p2pStateMachine10 = P2pStateMachine.this;
                                p2pStateMachine10.logd(getName() + " delete persistent group");
                            }
                            P2pStateMachine.this.mGroups.remove(message.arg1);
                            WifiP2pServiceImpl.this.mWifiP2pMetrics.updatePersistentGroup(P2pStateMachine.this.mGroups);
                            P2pStateMachine.this.replyToMessage(message, 139320);
                            break;
                        case 139323:
                            WifiP2pWfdInfo d2 = (WifiP2pWfdInfo) message.obj;
                            if (WifiP2pServiceImpl.this.getWfdPermission(message.sendingUid)) {
                                if (d2 != null && P2pStateMachine.this.setWfdInfo(d2)) {
                                    P2pStateMachine.this.replyToMessage(message, 139325);
                                    break;
                                } else {
                                    P2pStateMachine.this.replyToMessage(message, 139324, 0);
                                    break;
                                }
                            } else {
                                P2pStateMachine.this.replyToMessage(message, 139324, 0);
                                break;
                            }
                            break;
                        case 139329:
                            if (WifiP2pServiceImpl.this.mWifiPermissionsUtil.checkNetworkSettingsPermission(message.sendingUid)) {
                                if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                    P2pStateMachine p2pStateMachine11 = P2pStateMachine.this;
                                    p2pStateMachine11.logd(getName() + " start listen mode");
                                }
                                P2pStateMachine.this.mWifiNative.p2pFlush();
                                if (!P2pStateMachine.this.mWifiNative.p2pExtListen(true, 500, 500)) {
                                    P2pStateMachine.this.replyToMessage(message, 139330);
                                    break;
                                } else {
                                    P2pStateMachine.this.replyToMessage(message, 139331);
                                    break;
                                }
                            } else {
                                P2pStateMachine p2pStateMachine12 = P2pStateMachine.this;
                                p2pStateMachine12.loge("Permission violation - no NETWORK_SETTING permission, uid = " + message.sendingUid);
                                P2pStateMachine.this.replyToMessage(message, 139330);
                                break;
                            }
                        case 139332:
                            if (WifiP2pServiceImpl.this.mWifiPermissionsUtil.checkNetworkSettingsPermission(message.sendingUid)) {
                                if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                    P2pStateMachine p2pStateMachine13 = P2pStateMachine.this;
                                    p2pStateMachine13.logd(getName() + " stop listen mode");
                                }
                                if (P2pStateMachine.this.mWifiNative.p2pExtListen(false, 0, 0)) {
                                    P2pStateMachine.this.replyToMessage(message, 139334);
                                } else {
                                    P2pStateMachine.this.replyToMessage(message, 139333);
                                }
                                P2pStateMachine.this.mWifiNative.p2pFlush();
                                break;
                            } else {
                                P2pStateMachine p2pStateMachine14 = P2pStateMachine.this;
                                p2pStateMachine14.loge("Permission violation - no NETWORK_SETTING permission, uid = " + message.sendingUid);
                                P2pStateMachine.this.replyToMessage(message, 139333);
                                break;
                            }
                        case 139335:
                            Bundle p2pChannels = (Bundle) message.obj;
                            int lc = p2pChannels.getInt("lc", 0);
                            int oc = p2pChannels.getInt("oc", 0);
                            if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                P2pStateMachine p2pStateMachine15 = P2pStateMachine.this;
                                p2pStateMachine15.logd(getName() + " set listen and operating channel");
                            }
                            if (!P2pStateMachine.this.mWifiNative.p2pSetChannel(lc, oc)) {
                                P2pStateMachine.this.replyToMessage(message, 139336);
                                break;
                            } else {
                                P2pStateMachine.this.replyToMessage(message, 139337);
                                break;
                            }
                        case 139339:
                            Bundle requestBundle = new Bundle();
                            requestBundle.putString("android.net.wifi.p2p.EXTRA_HANDOVER_MESSAGE", P2pStateMachine.this.mWifiNative.getNfcHandoverRequest());
                            P2pStateMachine.this.replyToMessage(message, 139341, (Object) requestBundle);
                            break;
                        case 139340:
                            Bundle selectBundle = new Bundle();
                            selectBundle.putString("android.net.wifi.p2p.EXTRA_HANDOVER_MESSAGE", P2pStateMachine.this.mWifiNative.getNfcHandoverSelect());
                            P2pStateMachine.this.replyToMessage(message, 139341, (Object) selectBundle);
                            break;
                        case 139354:
                            WifiP2pWfdInfo d3 = (WifiP2pWfdInfo) message.obj;
                            if (WifiP2pServiceImpl.this.getWfdPermission(message.sendingUid)) {
                                if (d3 != null && P2pStateMachine.this.setWfdR2Info(d3)) {
                                    P2pStateMachine.this.replyToMessage(message, 139356);
                                    break;
                                } else {
                                    P2pStateMachine.this.replyToMessage(message, 139355, 0);
                                    break;
                                }
                            } else {
                                P2pStateMachine.this.replyToMessage(message, 139355, 0);
                                break;
                            }
                            break;
                        case WifiP2pServiceImpl.SET_MIRACAST_MODE /*143374*/:
                            P2pStateMachine.this.mWifiNative.setMiracastMode(message.arg1);
                            break;
                        case WifiP2pServiceImpl.BLOCK_DISCOVERY /*143375*/:
                            boolean blocked = message.arg1 == 1;
                            if (WifiP2pServiceImpl.this.mDiscoveryBlocked != blocked) {
                                boolean unused = WifiP2pServiceImpl.this.mDiscoveryBlocked = blocked;
                                if (blocked && WifiP2pServiceImpl.this.mDiscoveryStarted) {
                                    P2pStateMachine.this.mWifiNative.p2pStopFind();
                                    boolean unused2 = WifiP2pServiceImpl.this.mDiscoveryPostponed = true;
                                }
                                if (!blocked && WifiP2pServiceImpl.this.mDiscoveryPostponed) {
                                    boolean unused3 = WifiP2pServiceImpl.this.mDiscoveryPostponed = false;
                                    P2pStateMachine.this.mWifiNative.p2pFind(120);
                                }
                                if (blocked) {
                                    if (message.obj != null) {
                                        try {
                                            ((StateMachine) message.obj).sendMessage(message.arg2);
                                            break;
                                        } catch (Exception e) {
                                            P2pStateMachine p2pStateMachine16 = P2pStateMachine.this;
                                            p2pStateMachine16.loge("unable to send BLOCK_DISCOVERY response: " + e);
                                            break;
                                        }
                                    } else {
                                        Log.e(WifiP2pServiceImpl.TAG, "Illegal argument(s)");
                                        break;
                                    }
                                }
                            }
                            break;
                        case WifiP2pServiceImpl.ENABLE_P2P /*143376*/:
                            break;
                        case WifiP2pServiceImpl.DISABLE_P2P /*143377*/:
                            if (P2pStateMachine.this.mPeers.clear()) {
                                P2pStateMachine.this.sendPeersChangedBroadcast();
                            }
                            if (P2pStateMachine.this.mGroups.clear()) {
                                P2pStateMachine.this.sendP2pPersistentGroupsChangedBroadcast();
                            }
                            P2pStateMachine.this.clearServicesForAllClients();
                            P2pStateMachine.this.mWifiMonitor.stopMonitoring(P2pStateMachine.this.mInterfaceName);
                            P2pStateMachine.this.mWifiNative.teardownInterface();
                            P2pStateMachine p2pStateMachine17 = P2pStateMachine.this;
                            p2pStateMachine17.transitionTo(p2pStateMachine17.mP2pDisablingState);
                            break;
                        case WifiP2pServiceImpl.REMOVE_CLIENT_INFO /*143378*/:
                            if (message.obj instanceof IBinder) {
                                IBinder b = (IBinder) message.obj;
                                P2pStateMachine p2pStateMachine18 = P2pStateMachine.this;
                                p2pStateMachine18.clearClientInfo((Messenger) WifiP2pServiceImpl.this.mClientChannelList.get(b));
                                WifiP2pServiceImpl.this.mClientChannelList.remove(b);
                                break;
                            }
                            break;
                        case 147458:
                            P2pStateMachine.this.loge("Unexpected loss of p2p socket connection");
                            P2pStateMachine p2pStateMachine19 = P2pStateMachine.this;
                            p2pStateMachine19.transitionTo(p2pStateMachine19.mP2pDisabledState);
                            break;
                        case WifiP2pMonitor.P2P_DEVICE_FOUND_EVENT:
                            if (message.obj != null) {
                                WifiP2pDevice device = (WifiP2pDevice) message.obj;
                                if (!WifiP2pServiceImpl.this.mThisDevice.deviceAddress.equals(device.deviceAddress)) {
                                    P2pStateMachine.this.mPeers.updateSupplicantDetails(device);
                                    P2pStateMachine.this.sendPeersChangedBroadcast();
                                    break;
                                }
                            } else {
                                Log.e(WifiP2pServiceImpl.TAG, "Illegal argument(s)");
                                break;
                            }
                            break;
                        case WifiP2pMonitor.P2P_DEVICE_LOST_EVENT:
                            if (message.obj != null) {
                                if (P2pStateMachine.this.mPeers.remove(((WifiP2pDevice) message.obj).deviceAddress) != null) {
                                    P2pStateMachine.this.sendPeersChangedBroadcast();
                                    break;
                                }
                            } else {
                                Log.e(WifiP2pServiceImpl.TAG, "Illegal argument(s)");
                                break;
                            }
                            break;
                        case WifiP2pMonitor.P2P_FIND_STOPPED_EVENT:
                            P2pStateMachine.this.sendP2pDiscoveryChangedBroadcast(false);
                            break;
                        case WifiP2pMonitor.P2P_SERV_DISC_RESP_EVENT:
                            if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                P2pStateMachine p2pStateMachine20 = P2pStateMachine.this;
                                p2pStateMachine20.logd(getName() + " receive service response");
                            }
                            if (message.obj != null) {
                                for (WifiP2pServiceResponse resp : (List) message.obj) {
                                    resp.setSrcDevice(P2pStateMachine.this.mPeers.get(resp.getSrcDevice().deviceAddress));
                                    P2pStateMachine.this.sendServiceResponse(resp);
                                }
                                break;
                            } else {
                                Log.e(WifiP2pServiceImpl.TAG, "Illegal argument(s)");
                                break;
                            }
                        default:
                            return false;
                    }
                    return true;
                }

                public void exit() {
                    P2pStateMachine.this.sendP2pDiscoveryChangedBroadcast(false);
                    WifiP2pServiceImpl.this.mNetworkInfo.setIsAvailable(false);
                }
            }

            class InactiveState extends State {
                InactiveState() {
                }

                public void enter() {
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        P2pStateMachine.this.logd(getName());
                    }
                    P2pStateMachine.this.mSavedPeerConfig.invalidate();
                }

                public boolean processMessage(Message message) {
                    boolean ret;
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                        p2pStateMachine.logd(getName() + message.toString());
                    }
                    switch (message.what) {
                        case 139268:
                            if (!P2pStateMachine.this.mWifiNative.p2pStopFind()) {
                                P2pStateMachine.this.replyToMessage(message, 139269, 0);
                                break;
                            } else {
                                P2pStateMachine.this.mWifiNative.p2pFlush();
                                String unused = WifiP2pServiceImpl.this.mServiceDiscReqId = null;
                                P2pStateMachine.this.replyToMessage(message, 139270);
                                break;
                            }
                        case 139271:
                            if (WifiP2pServiceImpl.this.mWifiPermissionsUtil.checkCanAccessWifiDirect(P2pStateMachine.this.getCallingPkgName(message.sendingUid, message.replyTo), message.sendingUid, false)) {
                                if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                    P2pStateMachine p2pStateMachine2 = P2pStateMachine.this;
                                    p2pStateMachine2.logd(getName() + " sending connect");
                                }
                                WifiP2pConfig config = (WifiP2pConfig) message.obj;
                                boolean isConnectFailed = false;
                                if (P2pStateMachine.this.isConfigValidAsGroup(config)) {
                                    boolean unused2 = WifiP2pServiceImpl.this.mAutonomousGroup = false;
                                    P2pStateMachine.this.mWifiNative.p2pStopFind();
                                    if (P2pStateMachine.this.mWifiNative.p2pGroupAdd(config, true)) {
                                        WifiP2pServiceImpl.this.mWifiP2pMetrics.startConnectionEvent(3, config);
                                        P2pStateMachine p2pStateMachine3 = P2pStateMachine.this;
                                        p2pStateMachine3.transitionTo(p2pStateMachine3.mGroupNegotiationState);
                                    } else {
                                        P2pStateMachine.this.loge("Cannot join a group with config.");
                                        isConnectFailed = true;
                                        P2pStateMachine.this.replyToMessage(message, 139272);
                                    }
                                } else if (P2pStateMachine.this.isConfigInvalid(config)) {
                                    P2pStateMachine p2pStateMachine4 = P2pStateMachine.this;
                                    p2pStateMachine4.loge("Dropping connect request " + config);
                                    isConnectFailed = true;
                                    P2pStateMachine.this.replyToMessage(message, 139272);
                                } else {
                                    boolean unused3 = WifiP2pServiceImpl.this.mAutonomousGroup = false;
                                    P2pStateMachine.this.mWifiNative.p2pStopFind();
                                    if (P2pStateMachine.this.reinvokePersistentGroup(config)) {
                                        WifiP2pServiceImpl.this.mWifiP2pMetrics.startConnectionEvent(1, config);
                                        P2pStateMachine p2pStateMachine5 = P2pStateMachine.this;
                                        p2pStateMachine5.transitionTo(p2pStateMachine5.mGroupNegotiationState);
                                    } else {
                                        WifiP2pServiceImpl.this.mWifiP2pMetrics.startConnectionEvent(0, config);
                                        P2pStateMachine p2pStateMachine6 = P2pStateMachine.this;
                                        p2pStateMachine6.transitionTo(p2pStateMachine6.mProvisionDiscoveryState);
                                    }
                                }
                                if (!isConnectFailed) {
                                    WifiP2pConfig unused4 = P2pStateMachine.this.mSavedPeerConfig = config;
                                    P2pStateMachine.this.mPeers.updateStatus(P2pStateMachine.this.mSavedPeerConfig.deviceAddress, 1);
                                    P2pStateMachine.this.sendPeersChangedBroadcast();
                                    P2pStateMachine.this.replyToMessage(message, 139273);
                                    break;
                                }
                            } else {
                                P2pStateMachine.this.replyToMessage(message, 139272);
                                break;
                            }
                            break;
                        case 139277:
                            if (WifiP2pServiceImpl.this.mWifiPermissionsUtil.checkCanAccessWifiDirect(P2pStateMachine.this.getCallingPkgName(message.sendingUid, message.replyTo), message.sendingUid, false)) {
                                boolean unused5 = WifiP2pServiceImpl.this.mAutonomousGroup = true;
                                int netId = message.arg1;
                                WifiP2pConfig config2 = (WifiP2pConfig) message.obj;
                                if (config2 != null) {
                                    if (P2pStateMachine.this.isConfigValidAsGroup(config2)) {
                                        WifiP2pServiceImpl.this.mWifiP2pMetrics.startConnectionEvent(3, config2);
                                        ret = P2pStateMachine.this.mWifiNative.p2pGroupAdd(config2, false);
                                    } else {
                                        ret = false;
                                    }
                                } else if (netId == -2) {
                                    int netId2 = P2pStateMachine.this.mGroups.getNetworkId(WifiP2pServiceImpl.this.mThisDevice.deviceAddress);
                                    if (netId2 != -1) {
                                        WifiP2pServiceImpl.this.mWifiP2pMetrics.startConnectionEvent(1, (WifiP2pConfig) null);
                                        ret = P2pStateMachine.this.mWifiNative.p2pGroupAdd(netId2);
                                    } else {
                                        WifiP2pServiceImpl.this.mWifiP2pMetrics.startConnectionEvent(2, (WifiP2pConfig) null);
                                        ret = P2pStateMachine.this.mWifiNative.p2pGroupAdd(true);
                                    }
                                } else {
                                    WifiP2pServiceImpl.this.mWifiP2pMetrics.startConnectionEvent(2, (WifiP2pConfig) null);
                                    ret = P2pStateMachine.this.mWifiNative.p2pGroupAdd(false);
                                }
                                if (!ret) {
                                    P2pStateMachine.this.replyToMessage(message, 139278, 0);
                                    break;
                                } else {
                                    P2pStateMachine.this.replyToMessage(message, 139279);
                                    P2pStateMachine p2pStateMachine7 = P2pStateMachine.this;
                                    p2pStateMachine7.transitionTo(p2pStateMachine7.mGroupNegotiationState);
                                    break;
                                }
                            } else {
                                P2pStateMachine.this.replyToMessage(message, 139278, 0);
                                break;
                            }
                        case 139329:
                            if (WifiP2pServiceImpl.this.mWifiPermissionsUtil.checkNetworkSettingsPermission(message.sendingUid)) {
                                if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                    P2pStateMachine p2pStateMachine8 = P2pStateMachine.this;
                                    p2pStateMachine8.logd(getName() + " start listen mode");
                                }
                                P2pStateMachine.this.mWifiNative.p2pFlush();
                                if (!P2pStateMachine.this.mWifiNative.p2pExtListen(true, 500, 500)) {
                                    P2pStateMachine.this.replyToMessage(message, 139330);
                                    break;
                                } else {
                                    P2pStateMachine.this.replyToMessage(message, 139331);
                                    break;
                                }
                            } else {
                                P2pStateMachine p2pStateMachine9 = P2pStateMachine.this;
                                p2pStateMachine9.loge("Permission violation - no NETWORK_SETTING permission, uid = " + message.sendingUid);
                                P2pStateMachine.this.replyToMessage(message, 139330);
                                break;
                            }
                        case 139332:
                            if (WifiP2pServiceImpl.this.mWifiPermissionsUtil.checkNetworkSettingsPermission(message.sendingUid)) {
                                if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                    P2pStateMachine p2pStateMachine10 = P2pStateMachine.this;
                                    p2pStateMachine10.logd(getName() + " stop listen mode");
                                }
                                if (P2pStateMachine.this.mWifiNative.p2pExtListen(false, 0, 0)) {
                                    P2pStateMachine.this.replyToMessage(message, 139334);
                                } else {
                                    P2pStateMachine.this.replyToMessage(message, 139333);
                                }
                                P2pStateMachine.this.mWifiNative.p2pFlush();
                                break;
                            } else {
                                P2pStateMachine p2pStateMachine11 = P2pStateMachine.this;
                                p2pStateMachine11.loge("Permission violation - no NETWORK_SETTING permission, uid = " + message.sendingUid);
                                P2pStateMachine.this.replyToMessage(message, 139333);
                                break;
                            }
                        case 139335:
                            if (message.obj != null) {
                                Bundle p2pChannels = (Bundle) message.obj;
                                int lc = p2pChannels.getInt("lc", 0);
                                int oc = p2pChannels.getInt("oc", 0);
                                if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                    P2pStateMachine p2pStateMachine12 = P2pStateMachine.this;
                                    p2pStateMachine12.logd(getName() + " set listen and operating channel");
                                }
                                if (!P2pStateMachine.this.mWifiNative.p2pSetChannel(lc, oc)) {
                                    P2pStateMachine.this.replyToMessage(message, 139336);
                                    break;
                                } else {
                                    P2pStateMachine.this.replyToMessage(message, 139337);
                                    break;
                                }
                            } else {
                                Log.e(WifiP2pServiceImpl.TAG, "Illegal arguments(s)");
                                break;
                            }
                        case 139342:
                            String handoverSelect = null;
                            if (message.obj != null) {
                                handoverSelect = ((Bundle) message.obj).getString("android.net.wifi.p2p.EXTRA_HANDOVER_MESSAGE");
                            }
                            if (handoverSelect != null && P2pStateMachine.this.mWifiNative.initiatorReportNfcHandover(handoverSelect)) {
                                P2pStateMachine.this.replyToMessage(message, 139344);
                                P2pStateMachine p2pStateMachine13 = P2pStateMachine.this;
                                p2pStateMachine13.transitionTo(p2pStateMachine13.mGroupCreatingState);
                                break;
                            } else {
                                P2pStateMachine.this.replyToMessage(message, 139345);
                                break;
                            }
                            break;
                        case 139343:
                            String handoverRequest = null;
                            if (message.obj != null) {
                                handoverRequest = ((Bundle) message.obj).getString("android.net.wifi.p2p.EXTRA_HANDOVER_MESSAGE");
                            }
                            if (handoverRequest != null && P2pStateMachine.this.mWifiNative.responderReportNfcHandover(handoverRequest)) {
                                P2pStateMachine.this.replyToMessage(message, 139344);
                                P2pStateMachine p2pStateMachine14 = P2pStateMachine.this;
                                p2pStateMachine14.transitionTo(p2pStateMachine14.mGroupCreatingState);
                                break;
                            } else {
                                P2pStateMachine.this.replyToMessage(message, 139345);
                                break;
                            }
                            break;
                        case WifiP2pMonitor.P2P_GO_NEGOTIATION_REQUEST_EVENT:
                            WifiP2pConfig config3 = (WifiP2pConfig) message.obj;
                            if (!P2pStateMachine.this.isConfigInvalid(config3)) {
                                WifiP2pConfig unused6 = P2pStateMachine.this.mSavedPeerConfig = config3;
                                boolean unused7 = WifiP2pServiceImpl.this.mAutonomousGroup = false;
                                boolean unused8 = WifiP2pServiceImpl.this.mJoinExistingGroup = false;
                                WifiP2pServiceImpl.this.mWifiP2pMetrics.startConnectionEvent(0, config3);
                                P2pStateMachine p2pStateMachine15 = P2pStateMachine.this;
                                p2pStateMachine15.transitionTo(p2pStateMachine15.mUserAuthorizingNegotiationRequestState);
                                break;
                            } else {
                                P2pStateMachine p2pStateMachine16 = P2pStateMachine.this;
                                p2pStateMachine16.loge("Dropping GO neg request " + config3);
                                break;
                            }
                        case WifiP2pMonitor.P2P_GROUP_STARTED_EVENT:
                            if (message.obj != null) {
                                WifiP2pGroup unused9 = P2pStateMachine.this.mGroup = (WifiP2pGroup) message.obj;
                                if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                    P2pStateMachine p2pStateMachine17 = P2pStateMachine.this;
                                    p2pStateMachine17.logd(getName() + " group started");
                                }
                                if (P2pStateMachine.this.mGroup.isGroupOwner() && WifiP2pServiceImpl.EMPTY_DEVICE_ADDRESS.equals(P2pStateMachine.this.mGroup.getOwner().deviceAddress)) {
                                    P2pStateMachine.this.mGroup.getOwner().deviceAddress = WifiP2pServiceImpl.this.mThisDevice.deviceAddress;
                                }
                                if (P2pStateMachine.this.mGroup.getNetworkId() != -2) {
                                    P2pStateMachine p2pStateMachine18 = P2pStateMachine.this;
                                    p2pStateMachine18.loge("Unexpected group creation, remove " + P2pStateMachine.this.mGroup);
                                    P2pStateMachine.this.mWifiNative.p2pGroupRemove(P2pStateMachine.this.mGroup.getInterface());
                                    break;
                                } else {
                                    boolean unused10 = WifiP2pServiceImpl.this.mAutonomousGroup = false;
                                    P2pStateMachine.this.deferMessage(message);
                                    P2pStateMachine p2pStateMachine19 = P2pStateMachine.this;
                                    p2pStateMachine19.transitionTo(p2pStateMachine19.mGroupNegotiationState);
                                    break;
                                }
                            } else {
                                Log.e(WifiP2pServiceImpl.TAG, "Invalid argument(s)");
                                break;
                            }
                        case WifiP2pMonitor.P2P_INVITATION_RECEIVED_EVENT:
                            if (message.obj != null) {
                                WifiP2pGroup group = (WifiP2pGroup) message.obj;
                                WifiP2pDevice owner = group.getOwner();
                                if (owner == null) {
                                    int id = group.getNetworkId();
                                    if (id >= 0) {
                                        String addr = P2pStateMachine.this.mGroups.getOwnerAddr(id);
                                        if (addr == null) {
                                            P2pStateMachine.this.loge("Ignored invitation from null owner");
                                            break;
                                        } else {
                                            group.setOwner(new WifiP2pDevice(addr));
                                            owner = group.getOwner();
                                        }
                                    } else {
                                        P2pStateMachine.this.loge("Ignored invitation from null owner");
                                        break;
                                    }
                                }
                                WifiP2pConfig config4 = new WifiP2pConfig();
                                config4.deviceAddress = group.getOwner().deviceAddress;
                                if (!P2pStateMachine.this.isConfigInvalid(config4)) {
                                    WifiP2pConfig unused11 = P2pStateMachine.this.mSavedPeerConfig = config4;
                                    if (owner != null) {
                                        WifiP2pDevice wifiP2pDevice = P2pStateMachine.this.mPeers.get(owner.deviceAddress);
                                        WifiP2pDevice owner2 = wifiP2pDevice;
                                        if (wifiP2pDevice != null) {
                                            if (owner2.wpsPbcSupported()) {
                                                P2pStateMachine.this.mSavedPeerConfig.wps.setup = 0;
                                            } else if (owner2.wpsKeypadSupported()) {
                                                P2pStateMachine.this.mSavedPeerConfig.wps.setup = 2;
                                            } else if (owner2.wpsDisplaySupported()) {
                                                P2pStateMachine.this.mSavedPeerConfig.wps.setup = 1;
                                            }
                                        }
                                    }
                                    boolean unused12 = WifiP2pServiceImpl.this.mAutonomousGroup = false;
                                    boolean unused13 = WifiP2pServiceImpl.this.mJoinExistingGroup = true;
                                    WifiP2pServiceImpl.this.mWifiP2pMetrics.startConnectionEvent(0, config4);
                                    P2pStateMachine p2pStateMachine20 = P2pStateMachine.this;
                                    p2pStateMachine20.transitionTo(p2pStateMachine20.mUserAuthorizingInviteRequestState);
                                    break;
                                } else {
                                    P2pStateMachine p2pStateMachine21 = P2pStateMachine.this;
                                    p2pStateMachine21.loge("Dropping invitation request " + config4);
                                    break;
                                }
                            } else {
                                Log.e(WifiP2pServiceImpl.TAG, "Invalid argument(s)");
                                break;
                            }
                        case WifiP2pMonitor.P2P_PROV_DISC_PBC_REQ_EVENT:
                        case WifiP2pMonitor.P2P_PROV_DISC_ENTER_PIN_EVENT:
                            break;
                        case WifiP2pMonitor.P2P_PROV_DISC_SHOW_PIN_EVENT:
                            if (message.obj != null) {
                                WifiP2pProvDiscEvent provDisc = (WifiP2pProvDiscEvent) message.obj;
                                WifiP2pDevice device = provDisc.device;
                                if (device != null) {
                                    WifiP2pConfig unused14 = P2pStateMachine.this.mSavedPeerConfig = new WifiP2pConfig();
                                    P2pStateMachine.this.mSavedPeerConfig.wps.setup = 2;
                                    P2pStateMachine.this.mSavedPeerConfig.deviceAddress = device.deviceAddress;
                                    P2pStateMachine.this.mSavedPeerConfig.wps.pin = provDisc.pin;
                                    P2pStateMachine.this.notifyP2pProvDiscShowPinRequest(provDisc.pin, device.deviceAddress);
                                    P2pStateMachine.this.mPeers.updateStatus(device.deviceAddress, 1);
                                    P2pStateMachine.this.sendPeersChangedBroadcast();
                                    P2pStateMachine p2pStateMachine22 = P2pStateMachine.this;
                                    p2pStateMachine22.transitionTo(p2pStateMachine22.mUserAuthorizingNegotiationRequestState);
                                    break;
                                } else {
                                    Slog.d(WifiP2pServiceImpl.TAG, "Device entry is null");
                                    break;
                                }
                            } else {
                                Log.e(WifiP2pServiceImpl.TAG, "Illegal argument(s)");
                                break;
                            }
                        default:
                            return false;
                    }
                    return true;
                }
            }

            class GroupCreatingState extends State {
                GroupCreatingState() {
                }

                public void enter() {
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        P2pStateMachine.this.logd(getName());
                    }
                    P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                    p2pStateMachine.sendMessageDelayed(p2pStateMachine.obtainMessage(WifiP2pServiceImpl.GROUP_CREATING_TIMED_OUT, WifiP2pServiceImpl.access$8204(), 0), 120000);
                }

                public boolean processMessage(Message message) {
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                        p2pStateMachine.logd(getName() + message.toString());
                    }
                    switch (message.what) {
                        case 139265:
                            P2pStateMachine.this.replyToMessage(message, 139266, 2);
                            return true;
                        case 139274:
                            P2pStateMachine.this.mWifiNative.p2pCancelConnect();
                            WifiP2pServiceImpl.this.mWifiP2pMetrics.endConnectionEvent(3);
                            P2pStateMachine.this.handleGroupCreationFailure();
                            P2pStateMachine p2pStateMachine2 = P2pStateMachine.this;
                            p2pStateMachine2.transitionTo(p2pStateMachine2.mInactiveState);
                            P2pStateMachine.this.replyToMessage(message, 139276);
                            return true;
                        case WifiP2pServiceImpl.GROUP_CREATING_TIMED_OUT /*143361*/:
                            if (WifiP2pServiceImpl.sGroupCreatingTimeoutIndex != message.arg1) {
                                return true;
                            }
                            if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                P2pStateMachine.this.logd("Group negotiation timed out");
                            }
                            WifiP2pServiceImpl.this.mWifiP2pMetrics.endConnectionEvent(2);
                            P2pStateMachine.this.handleGroupCreationFailure();
                            P2pStateMachine p2pStateMachine3 = P2pStateMachine.this;
                            p2pStateMachine3.transitionTo(p2pStateMachine3.mInactiveState);
                            return true;
                        case WifiP2pMonitor.P2P_DEVICE_LOST_EVENT:
                            if (message.obj == null) {
                                Log.e(WifiP2pServiceImpl.TAG, "Illegal argument(s)");
                                return true;
                            }
                            WifiP2pDevice device = (WifiP2pDevice) message.obj;
                            if (!P2pStateMachine.this.mSavedPeerConfig.deviceAddress.equals(device.deviceAddress)) {
                                if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                    P2pStateMachine p2pStateMachine4 = P2pStateMachine.this;
                                    p2pStateMachine4.logd("mSavedPeerConfig " + P2pStateMachine.this.mSavedPeerConfig.deviceAddress + "device " + device.deviceAddress);
                                }
                                return false;
                            }
                            if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                P2pStateMachine p2pStateMachine5 = P2pStateMachine.this;
                                p2pStateMachine5.logd("Add device to lost list " + device);
                            }
                            P2pStateMachine.this.mPeersLostDuringConnection.updateSupplicantDetails(device);
                            return true;
                        case WifiP2pMonitor.P2P_GO_NEGOTIATION_SUCCESS_EVENT:
                            boolean unused = WifiP2pServiceImpl.this.mAutonomousGroup = false;
                            P2pStateMachine p2pStateMachine6 = P2pStateMachine.this;
                            p2pStateMachine6.transitionTo(p2pStateMachine6.mGroupNegotiationState);
                            return true;
                        default:
                            return false;
                    }
                }
            }

            class UserAuthorizingNegotiationRequestState extends State {
                UserAuthorizingNegotiationRequestState() {
                }

                public void enter() {
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        P2pStateMachine.this.logd(getName());
                    }
                    if (P2pStateMachine.this.mSavedPeerConfig.wps.setup == 0 || TextUtils.isEmpty(P2pStateMachine.this.mSavedPeerConfig.wps.pin)) {
                        P2pStateMachine.this.notifyInvitationReceived();
                    }
                }

                public boolean processMessage(Message message) {
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                        p2pStateMachine.logd(getName() + message.toString());
                    }
                    switch (message.what) {
                        case WifiP2pServiceImpl.PEER_CONNECTION_USER_ACCEPT /*143362*/:
                            P2pStateMachine.this.mWifiNative.p2pStopFind();
                            P2pStateMachine p2pStateMachine2 = P2pStateMachine.this;
                            p2pStateMachine2.p2pConnectWithPinDisplay(p2pStateMachine2.mSavedPeerConfig);
                            P2pStateMachine.this.mPeers.updateStatus(P2pStateMachine.this.mSavedPeerConfig.deviceAddress, 1);
                            P2pStateMachine.this.sendPeersChangedBroadcast();
                            P2pStateMachine p2pStateMachine3 = P2pStateMachine.this;
                            p2pStateMachine3.transitionTo(p2pStateMachine3.mGroupNegotiationState);
                            break;
                        case WifiP2pServiceImpl.PEER_CONNECTION_USER_REJECT /*143363*/:
                            if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                P2pStateMachine p2pStateMachine4 = P2pStateMachine.this;
                                p2pStateMachine4.logd("User rejected negotiation " + P2pStateMachine.this.mSavedPeerConfig);
                            }
                            P2pStateMachine p2pStateMachine5 = P2pStateMachine.this;
                            p2pStateMachine5.transitionTo(p2pStateMachine5.mInactiveState);
                            break;
                        case WifiP2pServiceImpl.PEER_CONNECTION_USER_CONFIRM /*143367*/:
                            P2pStateMachine.this.mSavedPeerConfig.wps.setup = 1;
                            P2pStateMachine.this.mWifiNative.p2pConnect(P2pStateMachine.this.mSavedPeerConfig, WifiP2pServiceImpl.FORM_GROUP.booleanValue());
                            P2pStateMachine p2pStateMachine6 = P2pStateMachine.this;
                            p2pStateMachine6.transitionTo(p2pStateMachine6.mGroupNegotiationState);
                            break;
                        default:
                            return false;
                    }
                    return true;
                }

                public void exit() {
                }
            }

            class UserAuthorizingInviteRequestState extends State {
                UserAuthorizingInviteRequestState() {
                }

                public void enter() {
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        P2pStateMachine.this.logd(getName());
                    }
                    P2pStateMachine.this.notifyInvitationReceived();
                }

                public boolean processMessage(Message message) {
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                        p2pStateMachine.logd(getName() + message.toString());
                    }
                    switch (message.what) {
                        case WifiP2pServiceImpl.PEER_CONNECTION_USER_ACCEPT /*143362*/:
                            P2pStateMachine.this.mWifiNative.p2pStopFind();
                            P2pStateMachine p2pStateMachine2 = P2pStateMachine.this;
                            if (!p2pStateMachine2.reinvokePersistentGroup(p2pStateMachine2.mSavedPeerConfig)) {
                                P2pStateMachine p2pStateMachine3 = P2pStateMachine.this;
                                p2pStateMachine3.p2pConnectWithPinDisplay(p2pStateMachine3.mSavedPeerConfig);
                            }
                            P2pStateMachine.this.mPeers.updateStatus(P2pStateMachine.this.mSavedPeerConfig.deviceAddress, 1);
                            P2pStateMachine.this.sendPeersChangedBroadcast();
                            P2pStateMachine p2pStateMachine4 = P2pStateMachine.this;
                            p2pStateMachine4.transitionTo(p2pStateMachine4.mGroupNegotiationState);
                            break;
                        case WifiP2pServiceImpl.PEER_CONNECTION_USER_REJECT /*143363*/:
                            if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                P2pStateMachine p2pStateMachine5 = P2pStateMachine.this;
                                p2pStateMachine5.logd("User rejected invitation " + P2pStateMachine.this.mSavedPeerConfig);
                            }
                            P2pStateMachine p2pStateMachine6 = P2pStateMachine.this;
                            p2pStateMachine6.transitionTo(p2pStateMachine6.mInactiveState);
                            break;
                        default:
                            return false;
                    }
                    return true;
                }

                public void exit() {
                }
            }

            class ProvisionDiscoveryState extends State {
                ProvisionDiscoveryState() {
                }

                public void enter() {
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        P2pStateMachine.this.logd(getName());
                    }
                    P2pStateMachine.this.mWifiNative.p2pProvisionDiscovery(P2pStateMachine.this.mSavedPeerConfig);
                }

                public boolean processMessage(Message message) {
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                        p2pStateMachine.logd(getName() + message.toString());
                    }
                    switch (message.what) {
                        case WifiP2pMonitor.P2P_PROV_DISC_PBC_RSP_EVENT:
                            if (message.obj != null) {
                                WifiP2pDevice device = ((WifiP2pProvDiscEvent) message.obj).device;
                                if ((device == null || device.deviceAddress.equals(P2pStateMachine.this.mSavedPeerConfig.deviceAddress)) && P2pStateMachine.this.mSavedPeerConfig.wps.setup == 0) {
                                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                        P2pStateMachine p2pStateMachine2 = P2pStateMachine.this;
                                        p2pStateMachine2.logd("Found a match " + P2pStateMachine.this.mSavedPeerConfig);
                                    }
                                    P2pStateMachine p2pStateMachine3 = P2pStateMachine.this;
                                    p2pStateMachine3.p2pConnectWithPinDisplay(p2pStateMachine3.mSavedPeerConfig);
                                    P2pStateMachine p2pStateMachine4 = P2pStateMachine.this;
                                    p2pStateMachine4.transitionTo(p2pStateMachine4.mGroupNegotiationState);
                                    break;
                                }
                            } else {
                                Log.e(WifiP2pServiceImpl.TAG, "Invalid argument(s)");
                                break;
                            }
                        case WifiP2pMonitor.P2P_PROV_DISC_ENTER_PIN_EVENT:
                            if (message.obj != null) {
                                WifiP2pDevice device2 = ((WifiP2pProvDiscEvent) message.obj).device;
                                if ((device2 == null || device2.deviceAddress.equals(P2pStateMachine.this.mSavedPeerConfig.deviceAddress)) && P2pStateMachine.this.mSavedPeerConfig.wps.setup == 2) {
                                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                        P2pStateMachine p2pStateMachine5 = P2pStateMachine.this;
                                        p2pStateMachine5.logd("Found a match " + P2pStateMachine.this.mSavedPeerConfig);
                                    }
                                    if (TextUtils.isEmpty(P2pStateMachine.this.mSavedPeerConfig.wps.pin)) {
                                        boolean unused = WifiP2pServiceImpl.this.mJoinExistingGroup = false;
                                        P2pStateMachine p2pStateMachine6 = P2pStateMachine.this;
                                        p2pStateMachine6.transitionTo(p2pStateMachine6.mUserAuthorizingNegotiationRequestState);
                                        break;
                                    } else {
                                        P2pStateMachine p2pStateMachine7 = P2pStateMachine.this;
                                        p2pStateMachine7.p2pConnectWithPinDisplay(p2pStateMachine7.mSavedPeerConfig);
                                        P2pStateMachine p2pStateMachine8 = P2pStateMachine.this;
                                        p2pStateMachine8.transitionTo(p2pStateMachine8.mGroupNegotiationState);
                                        break;
                                    }
                                }
                            } else {
                                Log.e(WifiP2pServiceImpl.TAG, "Illegal argument(s)");
                                break;
                            }
                        case WifiP2pMonitor.P2P_PROV_DISC_SHOW_PIN_EVENT:
                            if (message.obj != null) {
                                WifiP2pProvDiscEvent provDisc = (WifiP2pProvDiscEvent) message.obj;
                                WifiP2pDevice device3 = provDisc.device;
                                if (device3 != null) {
                                    if (device3.deviceAddress.equals(P2pStateMachine.this.mSavedPeerConfig.deviceAddress) && P2pStateMachine.this.mSavedPeerConfig.wps.setup == 1) {
                                        if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                            P2pStateMachine p2pStateMachine9 = P2pStateMachine.this;
                                            p2pStateMachine9.logd("Found a match " + P2pStateMachine.this.mSavedPeerConfig);
                                        }
                                        P2pStateMachine.this.mSavedPeerConfig.wps.pin = provDisc.pin;
                                        P2pStateMachine p2pStateMachine10 = P2pStateMachine.this;
                                        p2pStateMachine10.p2pConnectWithPinDisplay(p2pStateMachine10.mSavedPeerConfig);
                                        P2pStateMachine.this.notifyInvitationSent(provDisc.pin, device3.deviceAddress);
                                        P2pStateMachine p2pStateMachine11 = P2pStateMachine.this;
                                        p2pStateMachine11.transitionTo(p2pStateMachine11.mGroupNegotiationState);
                                        break;
                                    }
                                } else {
                                    Log.e(WifiP2pServiceImpl.TAG, "Invalid device");
                                    break;
                                }
                            } else {
                                Log.e(WifiP2pServiceImpl.TAG, "Illegal argument(s)");
                                break;
                            }
                        case WifiP2pMonitor.P2P_PROV_DISC_FAILURE_EVENT:
                            P2pStateMachine.this.loge("provision discovery failed");
                            WifiP2pServiceImpl.this.mWifiP2pMetrics.endConnectionEvent(4);
                            P2pStateMachine.this.handleGroupCreationFailure();
                            P2pStateMachine p2pStateMachine12 = P2pStateMachine.this;
                            p2pStateMachine12.transitionTo(p2pStateMachine12.mInactiveState);
                            break;
                        default:
                            return false;
                    }
                    return true;
                }
            }

            class GroupNegotiationState extends State {
                GroupNegotiationState() {
                }

                public void enter() {
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        P2pStateMachine.this.logd(getName());
                    }
                }

                /* JADX WARNING: Can't fix incorrect switch cases order */
                /* Code decompiled incorrectly, please refer to instructions dump. */
                public boolean processMessage(android.os.Message r7) {
                    /*
                        r6 = this;
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        com.android.server.wifi.p2p.WifiP2pServiceImpl r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.this
                        boolean r0 = r0.mVerboseLoggingEnabled
                        if (r0 == 0) goto L_0x0026
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        java.lang.StringBuilder r1 = new java.lang.StringBuilder
                        r1.<init>()
                        java.lang.String r2 = r6.getName()
                        r1.append(r2)
                        java.lang.String r2 = r7.toString()
                        r1.append(r2)
                        java.lang.String r1 = r1.toString()
                        r0.logd(r1)
                    L_0x0026:
                        int r0 = r7.what
                        r1 = -2
                        r2 = 0
                        r3 = 1
                        switch(r0) {
                            case 147481: goto L_0x02a7;
                            case 147482: goto L_0x0257;
                            case 147483: goto L_0x02a7;
                            case 147484: goto L_0x0245;
                            case 147485: goto L_0x00ce;
                            case 147486: goto L_0x0269;
                            case 147487: goto L_0x002e;
                            case 147488: goto L_0x002f;
                            default: goto L_0x002e;
                        }
                    L_0x002e:
                        return r2
                    L_0x002f:
                        java.lang.Object r0 = r7.obj
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStatus r0 = (com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStatus) r0
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStatus r2 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStatus.SUCCESS
                        if (r0 != r2) goto L_0x0039
                        goto L_0x02cb
                    L_0x0039:
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r2 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        java.lang.StringBuilder r4 = new java.lang.StringBuilder
                        r4.<init>()
                        java.lang.String r5 = "Invitation result "
                        r4.append(r5)
                        r4.append(r0)
                        java.lang.String r4 = r4.toString()
                        r2.loge(r4)
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStatus r2 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStatus.UNKNOWN_P2P_GROUP
                        if (r0 != r2) goto L_0x008c
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r2 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        android.net.wifi.p2p.WifiP2pConfig r2 = r2.mSavedPeerConfig
                        int r2 = r2.netId
                        if (r2 < 0) goto L_0x0079
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r4 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        com.android.server.wifi.p2p.WifiP2pServiceImpl r4 = com.android.server.wifi.p2p.WifiP2pServiceImpl.this
                        boolean r4 = r4.mVerboseLoggingEnabled
                        if (r4 == 0) goto L_0x006e
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r4 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        java.lang.String r5 = "Remove unknown client from the list"
                        r4.logd(r5)
                    L_0x006e:
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r4 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        android.net.wifi.p2p.WifiP2pConfig r5 = r4.mSavedPeerConfig
                        java.lang.String r5 = r5.deviceAddress
                        boolean unused = r4.removeClientFromList(r2, r5, r3)
                    L_0x0079:
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r4 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        android.net.wifi.p2p.WifiP2pConfig r4 = r4.mSavedPeerConfig
                        r4.netId = r1
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        android.net.wifi.p2p.WifiP2pConfig r4 = r1.mSavedPeerConfig
                        r1.p2pConnectWithPinDisplay(r4)
                        goto L_0x02cb
                    L_0x008c:
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStatus r2 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStatus.INFORMATION_IS_CURRENTLY_UNAVAILABLE
                        if (r0 != r2) goto L_0x00a3
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r2 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        android.net.wifi.p2p.WifiP2pConfig r2 = r2.mSavedPeerConfig
                        r2.netId = r1
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        android.net.wifi.p2p.WifiP2pConfig r2 = r1.mSavedPeerConfig
                        r1.p2pConnectWithPinDisplay(r2)
                        goto L_0x02cb
                    L_0x00a3:
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStatus r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStatus.NO_COMMON_CHANNEL
                        if (r0 != r1) goto L_0x00b2
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine$FrequencyConflictState r2 = r1.mFrequencyConflictState
                        r1.transitionTo(r2)
                        goto L_0x02cb
                    L_0x00b2:
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        com.android.server.wifi.p2p.WifiP2pServiceImpl r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.this
                        com.android.server.wifi.p2p.WifiP2pMetrics r1 = r1.mWifiP2pMetrics
                        r2 = 5
                        r1.endConnectionEvent(r2)
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        r1.handleGroupCreationFailure()
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine$InactiveState r2 = r1.mInactiveState
                        r1.transitionTo(r2)
                        goto L_0x02cb
                    L_0x00ce:
                        java.lang.Object r0 = r7.obj
                        if (r0 != 0) goto L_0x00db
                        java.lang.String r0 = "WifiP2pService"
                        java.lang.String r1 = "Illegal argument(s)"
                        android.util.Log.e(r0, r1)
                        goto L_0x02cb
                    L_0x00db:
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        java.lang.Object r4 = r7.obj
                        android.net.wifi.p2p.WifiP2pGroup r4 = (android.net.wifi.p2p.WifiP2pGroup) r4
                        android.net.wifi.p2p.WifiP2pGroup unused = r0.mGroup = r4
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        com.android.server.wifi.p2p.WifiP2pServiceImpl r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.this
                        boolean r0 = r0.mVerboseLoggingEnabled
                        if (r0 == 0) goto L_0x0108
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        java.lang.StringBuilder r4 = new java.lang.StringBuilder
                        r4.<init>()
                        java.lang.String r5 = r6.getName()
                        r4.append(r5)
                        java.lang.String r5 = " group started"
                        r4.append(r5)
                        java.lang.String r4 = r4.toString()
                        r0.logd(r4)
                    L_0x0108:
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        android.net.wifi.p2p.WifiP2pGroup r0 = r0.mGroup
                        boolean r0 = r0.isGroupOwner()
                        if (r0 == 0) goto L_0x013e
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        android.net.wifi.p2p.WifiP2pGroup r0 = r0.mGroup
                        android.net.wifi.p2p.WifiP2pDevice r0 = r0.getOwner()
                        java.lang.String r0 = r0.deviceAddress
                        java.lang.String r4 = "00:00:00:00:00:00"
                        boolean r0 = r4.equals(r0)
                        if (r0 == 0) goto L_0x013e
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        android.net.wifi.p2p.WifiP2pGroup r0 = r0.mGroup
                        android.net.wifi.p2p.WifiP2pDevice r0 = r0.getOwner()
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r4 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        com.android.server.wifi.p2p.WifiP2pServiceImpl r4 = com.android.server.wifi.p2p.WifiP2pServiceImpl.this
                        android.net.wifi.p2p.WifiP2pDevice r4 = r4.mThisDevice
                        java.lang.String r4 = r4.deviceAddress
                        r0.deviceAddress = r4
                    L_0x013e:
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        android.net.wifi.p2p.WifiP2pGroup r0 = r0.mGroup
                        int r0 = r0.getNetworkId()
                        if (r0 != r1) goto L_0x0180
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        java.lang.Boolean r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.RELOAD
                        boolean r1 = r1.booleanValue()
                        r0.updatePersistentNetworks(r1)
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        android.net.wifi.p2p.WifiP2pGroup r0 = r0.mGroup
                        android.net.wifi.p2p.WifiP2pDevice r0 = r0.getOwner()
                        java.lang.String r0 = r0.deviceAddress
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        android.net.wifi.p2p.WifiP2pGroup r1 = r1.mGroup
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r4 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        android.net.wifi.p2p.WifiP2pGroupList r4 = r4.mGroups
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r5 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        android.net.wifi.p2p.WifiP2pGroup r5 = r5.mGroup
                        java.lang.String r5 = r5.getNetworkName()
                        int r4 = r4.getNetworkId(r0, r5)
                        r1.setNetworkId(r4)
                    L_0x0180:
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        android.net.wifi.p2p.WifiP2pGroup r0 = r0.mGroup
                        boolean r0 = r0.isGroupOwner()
                        r1 = 10
                        if (r0 == 0) goto L_0x01ba
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        com.android.server.wifi.p2p.WifiP2pServiceImpl r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.this
                        boolean r0 = r0.mAutonomousGroup
                        if (r0 != 0) goto L_0x01ab
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        com.android.server.wifi.p2p.WifiP2pNative r0 = r0.mWifiNative
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r2 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        android.net.wifi.p2p.WifiP2pGroup r2 = r2.mGroup
                        java.lang.String r2 = r2.getInterface()
                        r0.setP2pGroupIdle(r2, r1)
                    L_0x01ab:
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        android.net.wifi.p2p.WifiP2pGroup r1 = r0.mGroup
                        java.lang.String r1 = r1.getInterface()
                        r0.startDhcpServer(r1)
                        goto L_0x023a
                    L_0x01ba:
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        com.android.server.wifi.p2p.WifiP2pNative r0 = r0.mWifiNative
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r4 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        android.net.wifi.p2p.WifiP2pGroup r4 = r4.mGroup
                        java.lang.String r4 = r4.getInterface()
                        r0.setP2pGroupIdle(r4, r1)
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        com.android.server.wifi.p2p.WifiP2pServiceImpl r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.this
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        android.net.wifi.p2p.WifiP2pGroup r1 = r1.mGroup
                        java.lang.String r1 = r1.getInterface()
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r4 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        android.os.Handler r4 = r4.getHandler()
                        r0.startIpClient(r1, r4)
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        com.android.server.wifi.WifiNative r0 = r0.mWifNative
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        java.lang.String r1 = r1.mInterfaceName
                        r0.setBluetoothCoexistenceMode(r1, r3)
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        boolean unused = r0.mIsBTCoexDisabled = r3
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        android.net.wifi.p2p.WifiP2pGroup r0 = r0.mGroup
                        android.net.wifi.p2p.WifiP2pDevice r0 = r0.getOwner()
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        android.net.wifi.p2p.WifiP2pDeviceList r1 = r1.mPeers
                        java.lang.String r4 = r0.deviceAddress
                        android.net.wifi.p2p.WifiP2pDevice r1 = r1.get(r4)
                        if (r1 == 0) goto L_0x0224
                        r0.updateSupplicantDetails(r1)
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r4 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        android.net.wifi.p2p.WifiP2pDeviceList r4 = r4.mPeers
                        java.lang.String r5 = r0.deviceAddress
                        r4.updateStatus(r5, r2)
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r2 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        r2.sendPeersChangedBroadcast()
                        goto L_0x023a
                    L_0x0224:
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r2 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        java.lang.StringBuilder r4 = new java.lang.StringBuilder
                        r4.<init>()
                        java.lang.String r5 = "Unknown group owner "
                        r4.append(r5)
                        r4.append(r0)
                        java.lang.String r4 = r4.toString()
                        r2.logw(r4)
                    L_0x023a:
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine$GroupCreatedState r1 = r0.mGroupCreatedState
                        r0.transitionTo(r1)
                        goto L_0x02cb
                    L_0x0245:
                        java.lang.Object r0 = r7.obj
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStatus r0 = (com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStatus) r0
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStatus r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStatus.NO_COMMON_CHANNEL
                        if (r0 != r1) goto L_0x02cb
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine$FrequencyConflictState r2 = r1.mFrequencyConflictState
                        r1.transitionTo(r2)
                        goto L_0x02cb
                    L_0x0257:
                        java.lang.Object r0 = r7.obj
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStatus r0 = (com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStatus) r0
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStatus r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStatus.NO_COMMON_CHANNEL
                        if (r0 != r1) goto L_0x0269
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r1 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine$FrequencyConflictState r2 = r1.mFrequencyConflictState
                        r1.transitionTo(r2)
                        goto L_0x02cb
                    L_0x0269:
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        com.android.server.wifi.p2p.WifiP2pServiceImpl r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.this
                        boolean r0 = r0.mVerboseLoggingEnabled
                        if (r0 == 0) goto L_0x028d
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        java.lang.StringBuilder r1 = new java.lang.StringBuilder
                        r1.<init>()
                        java.lang.String r4 = r6.getName()
                        r1.append(r4)
                        java.lang.String r4 = " go failure"
                        r1.append(r4)
                        java.lang.String r1 = r1.toString()
                        r0.logd(r1)
                    L_0x028d:
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        com.android.server.wifi.p2p.WifiP2pServiceImpl r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.this
                        com.android.server.wifi.p2p.WifiP2pMetrics r0 = r0.mWifiP2pMetrics
                        r0.endConnectionEvent(r2)
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        r0.handleGroupCreationFailure()
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine$InactiveState r1 = r0.mInactiveState
                        r0.transitionTo(r1)
                        goto L_0x02cb
                    L_0x02a7:
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        com.android.server.wifi.p2p.WifiP2pServiceImpl r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.this
                        boolean r0 = r0.mVerboseLoggingEnabled
                        if (r0 == 0) goto L_0x02cb
                        com.android.server.wifi.p2p.WifiP2pServiceImpl$P2pStateMachine r0 = com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.this
                        java.lang.StringBuilder r1 = new java.lang.StringBuilder
                        r1.<init>()
                        java.lang.String r2 = r6.getName()
                        r1.append(r2)
                        java.lang.String r2 = " go success"
                        r1.append(r2)
                        java.lang.String r1 = r1.toString()
                        r0.logd(r1)
                    L_0x02cb:
                        return r3
                    */
                    throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.p2p.WifiP2pServiceImpl.P2pStateMachine.GroupNegotiationState.processMessage(android.os.Message):boolean");
                }
            }

            class FrequencyConflictState extends State {
                private AlertDialog mFrequencyConflictDialog;

                FrequencyConflictState() {
                }

                public void enter() {
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        P2pStateMachine.this.logd(getName());
                    }
                    notifyFrequencyConflict();
                }

                private void notifyFrequencyConflict() {
                    P2pStateMachine.this.logd("Notify frequency conflict");
                    Resources r = Resources.getSystem();
                    AlertDialog.Builder builder = new AlertDialog.Builder(WifiP2pServiceImpl.this.mContext);
                    P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                    AlertDialog dialog = builder.setMessage(r.getString(17041410, new Object[]{p2pStateMachine.getDeviceName(p2pStateMachine.mSavedPeerConfig.deviceAddress)})).setPositiveButton(r.getString(17039910), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            P2pStateMachine.this.sendMessage(WifiP2pServiceImpl.DROP_WIFI_USER_ACCEPT);
                        }
                    }).setNegativeButton(r.getString(17039869), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            P2pStateMachine.this.sendMessage(WifiP2pServiceImpl.DROP_WIFI_USER_REJECT);
                        }
                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                        public void onCancel(DialogInterface arg0) {
                            P2pStateMachine.this.sendMessage(WifiP2pServiceImpl.DROP_WIFI_USER_REJECT);
                        }
                    }).create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.getWindow().setType(2003);
                    WindowManager.LayoutParams attrs = dialog.getWindow().getAttributes();
                    attrs.privateFlags = 16;
                    dialog.getWindow().setAttributes(attrs);
                    dialog.show();
                    this.mFrequencyConflictDialog = dialog;
                }

                public boolean processMessage(Message message) {
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                        p2pStateMachine.logd(getName() + message.toString());
                    }
                    int i = message.what;
                    if (i != 143373) {
                        switch (i) {
                            case WifiP2pServiceImpl.DROP_WIFI_USER_ACCEPT /*143364*/:
                                if (WifiP2pServiceImpl.this.mWifiChannel != null) {
                                    WifiP2pServiceImpl.this.mWifiChannel.sendMessage(WifiP2pServiceImpl.DISCONNECT_WIFI_REQUEST, 1);
                                } else {
                                    P2pStateMachine.this.loge("DROP_WIFI_USER_ACCEPT message received when WifiChannel is null");
                                }
                                boolean unused = WifiP2pServiceImpl.this.mTemporarilyDisconnectedWifi = true;
                                break;
                            case WifiP2pServiceImpl.DROP_WIFI_USER_REJECT /*143365*/:
                                WifiP2pServiceImpl.this.mWifiP2pMetrics.endConnectionEvent(6);
                                P2pStateMachine.this.handleGroupCreationFailure();
                                P2pStateMachine p2pStateMachine2 = P2pStateMachine.this;
                                p2pStateMachine2.transitionTo(p2pStateMachine2.mInactiveState);
                                break;
                            default:
                                switch (i) {
                                    case WifiP2pMonitor.P2P_GO_NEGOTIATION_SUCCESS_EVENT:
                                    case WifiP2pMonitor.P2P_GROUP_FORMATION_SUCCESS_EVENT:
                                        P2pStateMachine p2pStateMachine3 = P2pStateMachine.this;
                                        p2pStateMachine3.loge(getName() + "group sucess during freq conflict!");
                                        break;
                                    case WifiP2pMonitor.P2P_GO_NEGOTIATION_FAILURE_EVENT:
                                    case WifiP2pMonitor.P2P_GROUP_FORMATION_FAILURE_EVENT:
                                    case WifiP2pMonitor.P2P_GROUP_REMOVED_EVENT:
                                        break;
                                    case WifiP2pMonitor.P2P_GROUP_STARTED_EVENT:
                                        P2pStateMachine p2pStateMachine4 = P2pStateMachine.this;
                                        p2pStateMachine4.loge(getName() + "group started after freq conflict, handle anyway");
                                        P2pStateMachine.this.deferMessage(message);
                                        P2pStateMachine p2pStateMachine5 = P2pStateMachine.this;
                                        p2pStateMachine5.transitionTo(p2pStateMachine5.mGroupNegotiationState);
                                        break;
                                    default:
                                        return false;
                                }
                        }
                    } else {
                        if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                            P2pStateMachine p2pStateMachine6 = P2pStateMachine.this;
                            p2pStateMachine6.logd(getName() + "Wifi disconnected, retry p2p");
                        }
                        P2pStateMachine p2pStateMachine7 = P2pStateMachine.this;
                        p2pStateMachine7.transitionTo(p2pStateMachine7.mInactiveState);
                        P2pStateMachine p2pStateMachine8 = P2pStateMachine.this;
                        p2pStateMachine8.sendMessage(139271, p2pStateMachine8.mSavedPeerConfig);
                    }
                    return true;
                }

                public void exit() {
                    AlertDialog alertDialog = this.mFrequencyConflictDialog;
                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }
                }
            }

            class GroupCreatedState extends State {
                GroupCreatedState() {
                }

                public void enter() {
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        P2pStateMachine.this.logd(getName());
                    }
                    P2pStateMachine.this.mSavedPeerConfig.invalidate();
                    WifiP2pServiceImpl.this.mNetworkInfo.setDetailedState(NetworkInfo.DetailedState.CONNECTED, (String) null, (String) null);
                    P2pStateMachine.this.updateThisDevice(0);
                    if (P2pStateMachine.this.mGroup.isGroupOwner()) {
                        P2pStateMachine.this.setWifiP2pInfoOnGroupFormation(NetworkUtils.numericToInetAddress(WifiP2pServiceImpl.SERVER_ADDRESS));
                    }
                    if (WifiP2pServiceImpl.this.mAutonomousGroup) {
                        P2pStateMachine.this.sendP2pConnectionChangedBroadcast();
                    }
                    WifiP2pServiceImpl.this.mWifiP2pMetrics.endConnectionEvent(1);
                    WifiP2pServiceImpl.this.mWifiP2pMetrics.startGroupEvent(P2pStateMachine.this.mGroup);
                }

                public boolean processMessage(Message message) {
                    int netId;
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                        p2pStateMachine.logd(getName() + message.toString());
                    }
                    switch (message.what) {
                        case 139271:
                            if (WifiP2pServiceImpl.this.mWifiPermissionsUtil.checkCanAccessWifiDirect(P2pStateMachine.this.getCallingPkgName(message.sendingUid, message.replyTo), message.sendingUid, false)) {
                                WifiP2pConfig config = (WifiP2pConfig) message.obj;
                                if (!P2pStateMachine.this.isConfigInvalid(config)) {
                                    P2pStateMachine p2pStateMachine2 = P2pStateMachine.this;
                                    p2pStateMachine2.logd("Inviting device : " + config.deviceAddress);
                                    WifiP2pConfig unused = P2pStateMachine.this.mSavedPeerConfig = config;
                                    if (!P2pStateMachine.this.mWifiNative.p2pInvite(P2pStateMachine.this.mGroup, config.deviceAddress)) {
                                        P2pStateMachine.this.replyToMessage(message, 139272, 0);
                                        break;
                                    } else {
                                        P2pStateMachine.this.mPeers.updateStatus(config.deviceAddress, 1);
                                        P2pStateMachine.this.sendPeersChangedBroadcast();
                                        P2pStateMachine.this.replyToMessage(message, 139273);
                                        break;
                                    }
                                } else {
                                    P2pStateMachine p2pStateMachine3 = P2pStateMachine.this;
                                    p2pStateMachine3.loge("Dropping connect request " + config);
                                    P2pStateMachine.this.replyToMessage(message, 139272);
                                    break;
                                }
                            } else {
                                P2pStateMachine.this.replyToMessage(message, 139272);
                                break;
                            }
                        case 139280:
                            if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                P2pStateMachine p2pStateMachine4 = P2pStateMachine.this;
                                p2pStateMachine4.logd(getName() + " remove group");
                            }
                            P2pStateMachine.this.enableBTCoex();
                            if (!P2pStateMachine.this.mWifiNative.p2pGroupRemove(P2pStateMachine.this.mGroup.getInterface())) {
                                P2pStateMachine.this.handleGroupRemoved();
                                P2pStateMachine p2pStateMachine5 = P2pStateMachine.this;
                                p2pStateMachine5.transitionTo(p2pStateMachine5.mInactiveState);
                                P2pStateMachine.this.replyToMessage(message, 139281, 0);
                                break;
                            } else {
                                P2pStateMachine p2pStateMachine6 = P2pStateMachine.this;
                                p2pStateMachine6.transitionTo(p2pStateMachine6.mOngoingGroupRemovalState);
                                P2pStateMachine.this.replyToMessage(message, 139282);
                                break;
                            }
                        case 139326:
                            WpsInfo wps = (WpsInfo) message.obj;
                            int i = 139327;
                            if (wps != null) {
                                boolean ret = true;
                                if (wps.setup == 0) {
                                    ret = P2pStateMachine.this.mWifiNative.startWpsPbc(P2pStateMachine.this.mGroup.getInterface(), (String) null);
                                } else if (wps.pin == null) {
                                    String pin = P2pStateMachine.this.mWifiNative.startWpsPinDisplay(P2pStateMachine.this.mGroup.getInterface(), (String) null);
                                    try {
                                        Integer.parseInt(pin);
                                        P2pStateMachine.this.notifyInvitationSent(pin, "any");
                                    } catch (NumberFormatException e) {
                                        ret = false;
                                    }
                                } else {
                                    ret = P2pStateMachine.this.mWifiNative.startWpsPinKeypad(P2pStateMachine.this.mGroup.getInterface(), wps.pin);
                                }
                                P2pStateMachine p2pStateMachine7 = P2pStateMachine.this;
                                if (ret) {
                                    i = 139328;
                                }
                                p2pStateMachine7.replyToMessage(message, i);
                                break;
                            } else {
                                P2pStateMachine.this.replyToMessage(message, 139327);
                                break;
                            }
                        case WifiP2pServiceImpl.DISABLE_P2P /*143377*/:
                            P2pStateMachine.this.sendMessage(139280);
                            P2pStateMachine.this.deferMessage(message);
                            break;
                        case WifiP2pServiceImpl.IPC_PRE_DHCP_ACTION /*143390*/:
                            P2pStateMachine.this.mWifiNative.setP2pPowerSave(P2pStateMachine.this.mGroup.getInterface(), false);
                            try {
                                WifiP2pServiceImpl.this.mIpClient.completedPreDhcpAction();
                                break;
                            } catch (RemoteException e2) {
                                e2.rethrowFromSystemServer();
                                break;
                            }
                        case WifiP2pServiceImpl.IPC_POST_DHCP_ACTION /*143391*/:
                            P2pStateMachine.this.mWifiNative.setP2pPowerSave(P2pStateMachine.this.mGroup.getInterface(), true);
                            P2pStateMachine.this.enableBTCoex();
                            break;
                        case WifiP2pServiceImpl.IPC_DHCP_RESULTS /*143392*/:
                            DhcpResults unused2 = WifiP2pServiceImpl.this.mDhcpResults = (DhcpResults) message.obj;
                            break;
                        case WifiP2pServiceImpl.IPC_PROVISIONING_SUCCESS /*143393*/:
                            if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                P2pStateMachine p2pStateMachine8 = P2pStateMachine.this;
                                p2pStateMachine8.logd("mDhcpResults: " + WifiP2pServiceImpl.this.mDhcpResults);
                            }
                            if (WifiP2pServiceImpl.this.mDhcpResults != null) {
                                P2pStateMachine p2pStateMachine9 = P2pStateMachine.this;
                                p2pStateMachine9.setWifiP2pInfoOnGroupFormation(WifiP2pServiceImpl.this.mDhcpResults.serverAddress);
                            }
                            P2pStateMachine.this.sendP2pConnectionChangedBroadcast();
                            try {
                                String ifname = P2pStateMachine.this.mGroup.getInterface();
                                if (WifiP2pServiceImpl.this.mDhcpResults != null) {
                                    WifiP2pServiceImpl.this.mNwService.addInterfaceToLocalNetwork(ifname, WifiP2pServiceImpl.this.mDhcpResults.getRoutes(ifname));
                                    break;
                                }
                            } catch (Exception e3) {
                                P2pStateMachine p2pStateMachine10 = P2pStateMachine.this;
                                p2pStateMachine10.loge("Failed to add iface to local network " + e3);
                                break;
                            }
                            break;
                        case WifiP2pServiceImpl.IPC_PROVISIONING_FAILURE /*143394*/:
                            P2pStateMachine.this.loge("IP provisioning failed");
                            P2pStateMachine.this.mWifiNative.p2pGroupRemove(P2pStateMachine.this.mGroup.getInterface());
                            break;
                        case WifiP2pMonitor.P2P_DEVICE_LOST_EVENT:
                            if (message.obj == null) {
                                Log.e(WifiP2pServiceImpl.TAG, "Illegal argument(s)");
                                return false;
                            }
                            WifiP2pDevice device = (WifiP2pDevice) message.obj;
                            if (!P2pStateMachine.this.mGroup.contains(device)) {
                                return false;
                            }
                            if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                P2pStateMachine p2pStateMachine11 = P2pStateMachine.this;
                                p2pStateMachine11.logd("Add device to lost list " + device);
                            }
                            P2pStateMachine.this.mPeersLostDuringConnection.updateSupplicantDetails(device);
                            return true;
                        case WifiP2pMonitor.P2P_GROUP_STARTED_EVENT:
                            P2pStateMachine.this.loge("Duplicate group creation event notice, ignore");
                            break;
                        case WifiP2pMonitor.P2P_GROUP_REMOVED_EVENT:
                            if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                P2pStateMachine p2pStateMachine12 = P2pStateMachine.this;
                                p2pStateMachine12.logd(getName() + " group removed");
                            }
                            P2pStateMachine.this.enableBTCoex();
                            P2pStateMachine.this.handleGroupRemoved();
                            P2pStateMachine p2pStateMachine13 = P2pStateMachine.this;
                            p2pStateMachine13.transitionTo(p2pStateMachine13.mInactiveState);
                            break;
                        case WifiP2pMonitor.P2P_INVITATION_RESULT_EVENT:
                            P2pStatus status = (P2pStatus) message.obj;
                            if (status != P2pStatus.SUCCESS) {
                                P2pStateMachine p2pStateMachine14 = P2pStateMachine.this;
                                p2pStateMachine14.loge("Invitation result " + status);
                                if (status == P2pStatus.UNKNOWN_P2P_GROUP && (netId = P2pStateMachine.this.mGroup.getNetworkId()) >= 0) {
                                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                        P2pStateMachine.this.logd("Remove unknown client from the list");
                                    }
                                    P2pStateMachine p2pStateMachine15 = P2pStateMachine.this;
                                    boolean unused3 = p2pStateMachine15.removeClientFromList(netId, p2pStateMachine15.mSavedPeerConfig.deviceAddress, false);
                                    P2pStateMachine p2pStateMachine16 = P2pStateMachine.this;
                                    p2pStateMachine16.sendMessage(139271, p2pStateMachine16.mSavedPeerConfig);
                                    break;
                                }
                            }
                            break;
                        case WifiP2pMonitor.P2P_PROV_DISC_PBC_REQ_EVENT:
                        case WifiP2pMonitor.P2P_PROV_DISC_ENTER_PIN_EVENT:
                        case WifiP2pMonitor.P2P_PROV_DISC_SHOW_PIN_EVENT:
                            WifiP2pProvDiscEvent provDisc = (WifiP2pProvDiscEvent) message.obj;
                            WifiP2pConfig unused4 = P2pStateMachine.this.mSavedPeerConfig = new WifiP2pConfig();
                            if (!(provDisc == null || provDisc.device == null)) {
                                P2pStateMachine.this.mSavedPeerConfig.deviceAddress = provDisc.device.deviceAddress;
                            }
                            if (message.what == 147491) {
                                P2pStateMachine.this.mSavedPeerConfig.wps.setup = 2;
                            } else if (message.what == 147492) {
                                P2pStateMachine.this.mSavedPeerConfig.wps.setup = 1;
                                P2pStateMachine.this.mSavedPeerConfig.wps.pin = provDisc.pin;
                            } else {
                                P2pStateMachine.this.mSavedPeerConfig.wps.setup = 0;
                            }
                            if (!P2pStateMachine.this.mGroup.isGroupOwner()) {
                                if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                    P2pStateMachine.this.logd("Ignore provision discovery for GC");
                                    break;
                                }
                            } else {
                                P2pStateMachine p2pStateMachine17 = P2pStateMachine.this;
                                p2pStateMachine17.transitionTo(p2pStateMachine17.mUserAuthorizingJoinState);
                                break;
                            }
                            break;
                        case WifiP2pMonitor.AP_STA_DISCONNECTED_EVENT:
                            if (message.obj != null) {
                                WifiP2pDevice device2 = (WifiP2pDevice) message.obj;
                                String deviceAddress = device2.deviceAddress;
                                if (deviceAddress == null) {
                                    P2pStateMachine p2pStateMachine18 = P2pStateMachine.this;
                                    p2pStateMachine18.loge("Disconnect on unknown device: " + device2);
                                    break;
                                } else {
                                    P2pStateMachine.this.mPeers.updateStatus(deviceAddress, 3);
                                    WifiP2pDevice devDisconnected = P2pStateMachine.this.mPeers.remove(deviceAddress);
                                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                        P2pStateMachine p2pStateMachine19 = P2pStateMachine.this;
                                        p2pStateMachine19.logd("Removed device from mPeers: " + devDisconnected);
                                    }
                                    if (P2pStateMachine.this.mGroup.removeClient(deviceAddress)) {
                                        if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                            P2pStateMachine p2pStateMachine20 = P2pStateMachine.this;
                                            p2pStateMachine20.logd("Removed client " + deviceAddress);
                                        }
                                        if (WifiP2pServiceImpl.this.mAutonomousGroup || !P2pStateMachine.this.mGroup.isClientListEmpty()) {
                                            P2pStateMachine.this.sendP2pConnectionChangedBroadcast();
                                        } else {
                                            P2pStateMachine.this.logd("Client list empty, remove non-persistent p2p group");
                                            P2pStateMachine.this.mWifiNative.p2pGroupRemove(P2pStateMachine.this.mGroup.getInterface());
                                        }
                                        WifiP2pServiceImpl.this.mWifiP2pMetrics.updateGroupEvent(P2pStateMachine.this.mGroup);
                                    } else {
                                        if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                            P2pStateMachine p2pStateMachine21 = P2pStateMachine.this;
                                            p2pStateMachine21.logd("Failed to remove client " + deviceAddress);
                                        }
                                        for (WifiP2pDevice c : P2pStateMachine.this.mGroup.getClientList()) {
                                            if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                                P2pStateMachine p2pStateMachine22 = P2pStateMachine.this;
                                                p2pStateMachine22.logd("client " + c.deviceAddress);
                                            }
                                        }
                                    }
                                    P2pStateMachine.this.sendPeersChangedBroadcast();
                                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                        P2pStateMachine p2pStateMachine23 = P2pStateMachine.this;
                                        p2pStateMachine23.logd(getName() + " ap sta disconnected");
                                        break;
                                    }
                                }
                            } else {
                                Log.e(WifiP2pServiceImpl.TAG, "Illegal argument(s)");
                                break;
                            }
                            break;
                        case WifiP2pMonitor.AP_STA_CONNECTED_EVENT:
                            if (message.obj != null) {
                                String deviceAddress2 = ((WifiP2pDevice) message.obj).deviceAddress;
                                P2pStateMachine.this.mWifiNative.setP2pGroupIdle(P2pStateMachine.this.mGroup.getInterface(), 0);
                                if (deviceAddress2 != null) {
                                    if (P2pStateMachine.this.mPeers.get(deviceAddress2) != null) {
                                        P2pStateMachine.this.mGroup.addClient(P2pStateMachine.this.mPeers.get(deviceAddress2));
                                    } else {
                                        P2pStateMachine.this.mGroup.addClient(deviceAddress2);
                                    }
                                    P2pStateMachine.this.mPeers.updateStatus(deviceAddress2, 0);
                                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                        P2pStateMachine p2pStateMachine24 = P2pStateMachine.this;
                                        p2pStateMachine24.logd(getName() + " ap sta connected");
                                    }
                                    P2pStateMachine.this.sendPeersChangedBroadcast();
                                    WifiP2pServiceImpl.this.mWifiP2pMetrics.updateGroupEvent(P2pStateMachine.this.mGroup);
                                } else {
                                    P2pStateMachine.this.loge("Connect on null device address, ignore");
                                }
                                P2pStateMachine.this.sendP2pConnectionChangedBroadcast();
                                break;
                            } else {
                                Log.e(WifiP2pServiceImpl.TAG, "Illegal argument(s)");
                                break;
                            }
                        default:
                            return false;
                    }
                    return true;
                }

                public void exit() {
                    WifiP2pServiceImpl.this.mWifiP2pMetrics.endGroupEvent();
                    P2pStateMachine.this.updateThisDevice(3);
                    P2pStateMachine.this.resetWifiP2pInfo();
                    WifiP2pServiceImpl.this.mNetworkInfo.setDetailedState(NetworkInfo.DetailedState.DISCONNECTED, (String) null, (String) null);
                    P2pStateMachine.this.sendP2pConnectionChangedBroadcast();
                }
            }

            class UserAuthorizingJoinState extends State {
                UserAuthorizingJoinState() {
                }

                public void enter() {
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        P2pStateMachine.this.logd(getName());
                    }
                    P2pStateMachine.this.notifyInvitationReceived();
                }

                public boolean processMessage(Message message) {
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                        p2pStateMachine.logd(getName() + message.toString());
                    }
                    switch (message.what) {
                        case WifiP2pServiceImpl.PEER_CONNECTION_USER_ACCEPT /*143362*/:
                            P2pStateMachine.this.mWifiNative.p2pStopFind();
                            if (P2pStateMachine.this.mSavedPeerConfig.wps.setup == 0) {
                                P2pStateMachine.this.mWifiNative.startWpsPbc(P2pStateMachine.this.mGroup.getInterface(), (String) null);
                            } else {
                                P2pStateMachine.this.mWifiNative.startWpsPinKeypad(P2pStateMachine.this.mGroup.getInterface(), P2pStateMachine.this.mSavedPeerConfig.wps.pin);
                            }
                            P2pStateMachine p2pStateMachine2 = P2pStateMachine.this;
                            p2pStateMachine2.transitionTo(p2pStateMachine2.mGroupCreatedState);
                            return true;
                        case WifiP2pServiceImpl.PEER_CONNECTION_USER_REJECT /*143363*/:
                            if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                P2pStateMachine.this.logd("User rejected incoming request");
                            }
                            P2pStateMachine p2pStateMachine3 = P2pStateMachine.this;
                            p2pStateMachine3.transitionTo(p2pStateMachine3.mGroupCreatedState);
                            return true;
                        case WifiP2pMonitor.P2P_PROV_DISC_PBC_REQ_EVENT:
                        case WifiP2pMonitor.P2P_PROV_DISC_ENTER_PIN_EVENT:
                        case WifiP2pMonitor.P2P_PROV_DISC_SHOW_PIN_EVENT:
                            return true;
                        default:
                            return false;
                    }
                }

                public void exit() {
                }
            }

            class OngoingGroupRemovalState extends State {
                OngoingGroupRemovalState() {
                }

                public void enter() {
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        P2pStateMachine.this.logd(getName());
                    }
                }

                public boolean processMessage(Message message) {
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                        p2pStateMachine.logd(getName() + message.toString());
                    }
                    if (message.what != 139280) {
                        return false;
                    }
                    P2pStateMachine.this.replyToMessage(message, 139282);
                    return true;
                }
            }

            public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
                WifiP2pServiceImpl.super.dump(fd, pw, args);
                pw.println("mWifiP2pInfo " + this.mWifiP2pInfo);
                pw.println("mGroup " + this.mGroup);
                pw.println("mSavedPeerConfig " + this.mSavedPeerConfig);
                pw.println("mGroups" + this.mGroups);
                pw.println();
            }

            /* access modifiers changed from: private */
            public void checkAndReEnableP2p() {
                boolean isHalInterfaceAvailable = isHalInterfaceAvailable();
                Log.d(WifiP2pServiceImpl.TAG, "Wifi enabled=" + this.mIsWifiEnabled + ", P2P Interface availability=" + isHalInterfaceAvailable + ", Number of clients=" + WifiP2pServiceImpl.this.mDeathDataByBinder.size());
                if (this.mIsWifiEnabled && isHalInterfaceAvailable && !WifiP2pServiceImpl.this.mDeathDataByBinder.isEmpty()) {
                    sendMessage(WifiP2pServiceImpl.ENABLE_P2P);
                }
            }

            /* access modifiers changed from: private */
            public boolean isHalInterfaceAvailable() {
                if (this.mWifiNative.isHalInterfaceSupported()) {
                    return this.mIsHalInterfaceAvailable;
                }
                return true;
            }

            /* access modifiers changed from: private */
            public void checkAndSendP2pStateChangedBroadcast() {
                boolean isHalInterfaceAvailable = isHalInterfaceAvailable();
                Log.d(WifiP2pServiceImpl.TAG, "Wifi enabled=" + this.mIsWifiEnabled + ", P2P Interface availability=" + isHalInterfaceAvailable);
                sendP2pStateChangedBroadcast(this.mIsWifiEnabled && isHalInterfaceAvailable);
            }

            private void sendP2pStateChangedBroadcast(boolean enabled) {
                Intent intent = new Intent("android.net.wifi.p2p.STATE_CHANGED");
                intent.addFlags(MiuiWindowManager.LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
                if (enabled) {
                    intent.putExtra("wifi_p2p_state", 2);
                } else {
                    intent.putExtra("wifi_p2p_state", 1);
                }
                WifiP2pServiceImpl.this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
            }

            /* access modifiers changed from: private */
            public void sendP2pDiscoveryChangedBroadcast(boolean started) {
                int i;
                if (WifiP2pServiceImpl.this.mDiscoveryStarted != started) {
                    boolean unused = WifiP2pServiceImpl.this.mDiscoveryStarted = started;
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        logd("discovery change broadcast " + started);
                    }
                    Intent intent = new Intent("android.net.wifi.p2p.DISCOVERY_STATE_CHANGE");
                    intent.addFlags(MiuiWindowManager.LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
                    if (started) {
                        i = 2;
                    } else {
                        i = 1;
                    }
                    intent.putExtra("discoveryState", i);
                    WifiP2pServiceImpl.this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
                }
            }

            private void sendThisDeviceChangedBroadcast() {
                Intent intent = new Intent("android.net.wifi.p2p.THIS_DEVICE_CHANGED");
                intent.addFlags(MiuiWindowManager.LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
                intent.putExtra("wifiP2pDevice", eraseOwnDeviceAddress(WifiP2pServiceImpl.this.mThisDevice));
                WifiP2pServiceImpl.this.mContext.sendBroadcastAsUserMultiplePermissions(intent, UserHandle.ALL, WifiP2pServiceImpl.RECEIVER_PERMISSIONS_FOR_BROADCAST);
            }

            /* access modifiers changed from: private */
            public void sendPeersChangedBroadcast() {
                Intent intent = new Intent("android.net.wifi.p2p.PEERS_CHANGED");
                intent.putExtra("wifiP2pDeviceList", new WifiP2pDeviceList(this.mPeers));
                intent.addFlags(MiuiWindowManager.LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
                WifiP2pServiceImpl.this.mContext.sendBroadcastAsUserMultiplePermissions(intent, UserHandle.ALL, WifiP2pServiceImpl.RECEIVER_PERMISSIONS_FOR_BROADCAST);
            }

            /* access modifiers changed from: private */
            public void sendP2pConnectionChangedBroadcast() {
                if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                    logd("sending p2p connection changed broadcast");
                }
                Intent intent = new Intent("android.net.wifi.p2p.CONNECTION_STATE_CHANGE");
                intent.addFlags(603979776);
                intent.putExtra("wifiP2pInfo", new WifiP2pInfo(this.mWifiP2pInfo));
                intent.putExtra("networkInfo", new NetworkInfo(WifiP2pServiceImpl.this.mNetworkInfo));
                intent.putExtra("p2pGroupInfo", eraseOwnDeviceAddress(this.mGroup));
                WifiP2pServiceImpl.this.mContext.sendBroadcastAsUserMultiplePermissions(intent, UserHandle.ALL, WifiP2pServiceImpl.RECEIVER_PERMISSIONS_FOR_BROADCAST);
                if (WifiP2pServiceImpl.this.mWifiChannel != null) {
                    WifiP2pServiceImpl.this.mWifiChannel.sendMessage(WifiP2pServiceImpl.P2P_CONNECTION_CHANGED, new NetworkInfo(WifiP2pServiceImpl.this.mNetworkInfo));
                } else {
                    loge("sendP2pConnectionChangedBroadcast(): WifiChannel is null");
                }
            }

            /* access modifiers changed from: private */
            public void sendP2pPersistentGroupsChangedBroadcast() {
                if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                    logd("sending p2p persistent groups changed broadcast");
                }
                Intent intent = new Intent("android.net.wifi.p2p.PERSISTENT_GROUPS_CHANGED");
                intent.addFlags(MiuiWindowManager.LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
                WifiP2pServiceImpl.this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
            }

            /* access modifiers changed from: private */
            public void startDhcpServer(String intf) {
                try {
                    InterfaceConfiguration ifcg = WifiP2pServiceImpl.this.mNwService.getInterfaceConfig(intf);
                    ifcg.setLinkAddress(new LinkAddress(NetworkUtils.numericToInetAddress(WifiP2pServiceImpl.SERVER_ADDRESS), 24));
                    ifcg.setInterfaceUp();
                    WifiP2pServiceImpl.this.mNwService.setInterfaceConfig(intf, ifcg);
                    String[] tetheringDhcpRanges = ((ConnectivityManager) WifiP2pServiceImpl.this.mContext.getSystemService("connectivity")).getTetheredDhcpRanges();
                    if (WifiP2pServiceImpl.this.mNwService.isTetheringStarted()) {
                        if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                            logd("Stop existing tethering and restart it");
                        }
                        WifiP2pServiceImpl.this.mNwService.stopTethering();
                    }
                    WifiP2pServiceImpl.this.mNwService.tetherInterface(intf);
                    WifiP2pServiceImpl.this.mNwService.startTethering(tetheringDhcpRanges);
                    logd("Started Dhcp server on " + intf);
                } catch (Exception e) {
                    loge("Error configuring interface " + intf + ", :" + e);
                }
            }

            private void stopDhcpServer(String intf) {
                String str;
                str = "Stopped Dhcp server";
                try {
                    WifiP2pServiceImpl.this.mNwService.untetherInterface(intf);
                    for (String temp : WifiP2pServiceImpl.this.mNwService.listTetheredInterfaces()) {
                        logd("List all interfaces " + temp);
                        if (temp.compareTo(intf) != 0) {
                            str = "Found other tethering interfaces, so keep tethering alive";
                            logd(str);
                            return;
                        }
                    }
                    WifiP2pServiceImpl.this.mNwService.stopTethering();
                    logd(str);
                } catch (Exception e) {
                    loge("Error stopping Dhcp server" + e);
                } finally {
                    logd(str);
                }
            }

            private void notifyP2pEnableFailure() {
                Resources r = Resources.getSystem();
                AlertDialog dialog = new AlertDialog.Builder(WifiP2pServiceImpl.this.mContext).setTitle(r.getString(17041405)).setMessage(r.getString(17041409)).setPositiveButton(r.getString(17039370), (DialogInterface.OnClickListener) null).create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setType(2003);
                WindowManager.LayoutParams attrs = dialog.getWindow().getAttributes();
                attrs.privateFlags = 16;
                dialog.getWindow().setAttributes(attrs);
                dialog.show();
            }

            private void addRowToDialog(ViewGroup group, int stringId, String value) {
                Resources r = Resources.getSystem();
                View row = LayoutInflater.from(WifiP2pServiceImpl.this.mContext).inflate(17367364, group, false);
                ((TextView) row.findViewById(16909159)).setText(r.getString(stringId));
                ((TextView) row.findViewById(16909583)).setText(value);
                group.addView(row);
            }

            /* access modifiers changed from: private */
            public void notifyInvitationSent(String pin, String peerAddress) {
                Resources r = Resources.getSystem();
                View textEntryView = LayoutInflater.from(WifiP2pServiceImpl.this.mContext).inflate(17367363, (ViewGroup) null);
                ViewGroup group = (ViewGroup) textEntryView.findViewById(16909033);
                addRowToDialog(group, 17041415, getDeviceName(peerAddress));
                addRowToDialog(group, 17041414, pin);
                AlertDialog dialog = new AlertDialog.Builder(WifiP2pServiceImpl.this.mContext).setTitle(r.getString(17041412)).setView(textEntryView).setPositiveButton(r.getString(17039370), (DialogInterface.OnClickListener) null).create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setType(2003);
                WindowManager.LayoutParams attrs = dialog.getWindow().getAttributes();
                attrs.privateFlags = 16;
                dialog.getWindow().setAttributes(attrs);
                dialog.show();
            }

            /* access modifiers changed from: private */
            public void notifyP2pProvDiscShowPinRequest(String pin, String peerAddress) {
                Resources r = Resources.getSystem();
                View textEntryView = LayoutInflater.from(WifiP2pServiceImpl.this.mContext).inflate(17367363, (ViewGroup) null);
                ViewGroup group = (ViewGroup) textEntryView.findViewById(16909033);
                addRowToDialog(group, 17041415, getDeviceName(peerAddress));
                addRowToDialog(group, 17041414, pin);
                AlertDialog dialog = new AlertDialog.Builder(WifiP2pServiceImpl.this.mContext).setTitle(r.getString(17041412)).setView(textEntryView).setPositiveButton(r.getString(17039441), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        P2pStateMachine.this.sendMessage(WifiP2pServiceImpl.PEER_CONNECTION_USER_CONFIRM);
                    }
                }).setCancelable(false).create();
                dialog.setCanceledOnTouchOutside(false);
                dialog.getWindow().setType(2003);
                WindowManager.LayoutParams attrs = dialog.getWindow().getAttributes();
                attrs.privateFlags = 16;
                dialog.getWindow().setAttributes(attrs);
                dialog.show();
            }

            /* access modifiers changed from: private */
            public void notifyInvitationReceived() {
                String acceptMac = Settings.System.getString(WifiP2pServiceImpl.this.mContext.getContentResolver(), "wifi_p2p_accept_mac");
                if (TextUtils.isEmpty(this.mSavedPeerConfig.deviceAddress) || !this.mSavedPeerConfig.deviceAddress.equals(acceptMac)) {
                    if (acceptMac != null) {
                        Log.e(WifiP2pServiceImpl.TAG, "notifyInvitationReceived: deviceAddress: " + this.mSavedPeerConfig.deviceAddress + "acceptP2pMac: " + acceptMac);
                    }
                    Resources r = Resources.getSystem();
                    final WpsInfo wps = this.mSavedPeerConfig.wps;
                    View textEntryView = LayoutInflater.from(WifiP2pServiceImpl.this.mContext).inflate(17367363, (ViewGroup) null);
                    ViewGroup group = (ViewGroup) textEntryView.findViewById(16909033);
                    addRowToDialog(group, 17041411, getDeviceName(this.mSavedPeerConfig.deviceAddress));
                    final EditText pin = (EditText) textEntryView.findViewById(16909604);
                    AlertDialog dialog = new AlertDialog.Builder(WifiP2pServiceImpl.this.mContext).setTitle(r.getString(17041413)).setView(textEntryView).setPositiveButton(r.getString(17039441), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (wps.setup == 2) {
                                P2pStateMachine.this.mSavedPeerConfig.wps.pin = pin.getText().toString();
                            }
                            if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                                p2pStateMachine.logd(P2pStateMachine.this.getName() + " accept invitation " + P2pStateMachine.this.mSavedPeerConfig);
                            }
                            P2pStateMachine.this.sendMessage(WifiP2pServiceImpl.PEER_CONNECTION_USER_ACCEPT);
                        }
                    }).setNegativeButton(r.getString(17039869), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                                p2pStateMachine.logd(P2pStateMachine.this.getName() + " ignore connect");
                            }
                            P2pStateMachine.this.sendMessage(WifiP2pServiceImpl.PEER_CONNECTION_USER_REJECT);
                        }
                    }).setOnCancelListener(new DialogInterface.OnCancelListener() {
                        public void onCancel(DialogInterface arg0) {
                            if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                P2pStateMachine p2pStateMachine = P2pStateMachine.this;
                                p2pStateMachine.logd(P2pStateMachine.this.getName() + " ignore connect");
                            }
                            P2pStateMachine.this.sendMessage(WifiP2pServiceImpl.PEER_CONNECTION_USER_REJECT);
                        }
                    }).create();
                    dialog.setCanceledOnTouchOutside(false);
                    int i = wps.setup;
                    if (i == 1) {
                        if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                            logd("Shown pin section visible");
                        }
                        addRowToDialog(group, 17041414, wps.pin);
                    } else if (i == 2) {
                        if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                            logd("Enter pin section visible");
                        }
                        textEntryView.findViewById(16908905).setVisibility(0);
                    }
                    if ((r.getConfiguration().uiMode & 5) == 5) {
                        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                if (keyCode != 164) {
                                    return false;
                                }
                                P2pStateMachine.this.sendMessage(WifiP2pServiceImpl.PEER_CONNECTION_USER_ACCEPT);
                                dialog.dismiss();
                                return true;
                            }
                        });
                    }
                    dialog.getWindow().setType(2003);
                    WindowManager.LayoutParams attrs = dialog.getWindow().getAttributes();
                    attrs.privateFlags = 16;
                    dialog.getWindow().setAttributes(attrs);
                    dialog.show();
                    return;
                }
                sendMessage(WifiP2pServiceImpl.PEER_CONNECTION_USER_ACCEPT);
            }

            /* access modifiers changed from: private */
            public void updatePersistentNetworks(boolean reload) {
                if (reload) {
                    this.mGroups.clear();
                }
                if (this.mWifiNative.p2pListNetworks(this.mGroups) || reload) {
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        logd("list p2p networks success");
                    }
                    for (WifiP2pGroup group : this.mGroups.getGroupList()) {
                        if (group.getOwner() != null && WifiP2pServiceImpl.this.mThisDevice.deviceAddress.equals(group.getOwner().deviceAddress)) {
                            if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                logd("start to set owner");
                            }
                            group.setOwner(WifiP2pServiceImpl.this.mThisDevice);
                        }
                    }
                    this.mWifiNative.saveConfig();
                    WifiP2pServiceImpl.this.mWifiP2pMetrics.updatePersistentGroup(this.mGroups);
                    sendP2pPersistentGroupsChangedBroadcast();
                }
            }

            /* access modifiers changed from: private */
            public boolean isConfigInvalid(WifiP2pConfig config) {
                if (config == null || TextUtils.isEmpty(config.deviceAddress) || this.mPeers.get(config.deviceAddress) == null) {
                    return true;
                }
                return false;
            }

            /* access modifiers changed from: private */
            public boolean isConfigValidAsGroup(WifiP2pConfig config) {
                if (config != null && !TextUtils.isEmpty(config.deviceAddress) && !TextUtils.isEmpty(config.networkName) && !TextUtils.isEmpty(config.passphrase)) {
                    return true;
                }
                return false;
            }

            private WifiP2pDevice fetchCurrentDeviceDetails(WifiP2pConfig config) {
                if (config == null) {
                    return null;
                }
                this.mPeers.updateGroupCapability(config.deviceAddress, this.mWifiNative.getGroupCapability(config.deviceAddress));
                return this.mPeers.get(config.deviceAddress);
            }

            private WifiP2pDevice eraseOwnDeviceAddress(WifiP2pDevice device) {
                if (device == null) {
                    return null;
                }
                WifiP2pDevice result = new WifiP2pDevice(device);
                if (device.deviceAddress != null && WifiP2pServiceImpl.this.mThisDevice.deviceAddress != null && device.deviceAddress.length() > 0 && WifiP2pServiceImpl.this.mThisDevice.deviceAddress.equals(device.deviceAddress)) {
                    result.deviceAddress = WifiP2pServiceImpl.ANONYMIZED_DEVICE_ADDRESS;
                }
                return result;
            }

            private WifiP2pGroup eraseOwnDeviceAddress(WifiP2pGroup group) {
                if (group == null) {
                    return null;
                }
                WifiP2pGroup result = new WifiP2pGroup(group);
                for (WifiP2pDevice originalDevice : group.getClientList()) {
                    result.removeClient(originalDevice);
                    result.addClient(eraseOwnDeviceAddress(originalDevice));
                }
                result.setOwner(eraseOwnDeviceAddress(group.getOwner()));
                return result;
            }

            /* access modifiers changed from: private */
            public WifiP2pDevice maybeEraseOwnDeviceAddress(WifiP2pDevice device, int uid) {
                if (device == null) {
                    return null;
                }
                if (WifiP2pServiceImpl.this.mWifiPermissionsUtil.checkLocalMacAddressPermission(uid)) {
                    return new WifiP2pDevice(device);
                }
                return eraseOwnDeviceAddress(device);
            }

            /* access modifiers changed from: private */
            public WifiP2pGroup maybeEraseOwnDeviceAddress(WifiP2pGroup group, int uid) {
                if (group == null) {
                    return null;
                }
                if (WifiP2pServiceImpl.this.mWifiPermissionsUtil.checkLocalMacAddressPermission(uid)) {
                    return new WifiP2pGroup(group);
                }
                return eraseOwnDeviceAddress(group);
            }

            /* access modifiers changed from: private */
            public WifiP2pGroupList maybeEraseOwnDeviceAddress(WifiP2pGroupList groupList, int uid) {
                if (groupList == null) {
                    return null;
                }
                WifiP2pGroupList result = new WifiP2pGroupList();
                for (WifiP2pGroup group : groupList.getGroupList()) {
                    result.add(maybeEraseOwnDeviceAddress(group, uid));
                }
                return result;
            }

            /* access modifiers changed from: private */
            public void p2pConnectWithPinDisplay(WifiP2pConfig config) {
                if (config == null) {
                    Log.e(WifiP2pServiceImpl.TAG, "Illegal argument(s)");
                    return;
                }
                WifiP2pDevice dev = fetchCurrentDeviceDetails(config);
                if (dev == null) {
                    Log.e(WifiP2pServiceImpl.TAG, "Invalid device");
                    return;
                }
                String pin = this.mWifiNative.p2pConnect(config, dev.isGroupOwner());
                try {
                    Integer.parseInt(pin);
                    notifyInvitationSent(pin, config.deviceAddress);
                } catch (NumberFormatException e) {
                }
            }

            /* access modifiers changed from: private */
            public boolean reinvokePersistentGroup(WifiP2pConfig config) {
                int netId;
                if (config == null) {
                    Log.e(WifiP2pServiceImpl.TAG, "Illegal argument(s)");
                    return false;
                }
                WifiP2pDevice dev = fetchCurrentDeviceDetails(config);
                if (dev == null) {
                    Log.e(WifiP2pServiceImpl.TAG, "Invalid device");
                    return false;
                }
                boolean join = dev.isGroupOwner();
                String ssid = this.mWifiNative.p2pGetSsid(dev.deviceAddress);
                if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                    logd("target ssid is " + ssid + " join:" + join);
                }
                if (join && dev.isGroupLimit()) {
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        logd("target device reaches group limit.");
                    }
                    join = false;
                } else if (join && (netId = this.mGroups.getNetworkId(dev.deviceAddress, ssid)) >= 0) {
                    if (!this.mWifiNative.p2pGroupAdd(netId)) {
                        return false;
                    }
                    return true;
                }
                if (join || !dev.isDeviceLimit()) {
                    if (!join && dev.isInvitationCapable()) {
                        int netId2 = -2;
                        if (config.netId < 0) {
                            netId2 = this.mGroups.getNetworkId(dev.deviceAddress);
                        } else if (config.deviceAddress.equals(this.mGroups.getOwnerAddr(config.netId))) {
                            netId2 = config.netId;
                        }
                        if (netId2 < 0) {
                            netId2 = getNetworkIdFromClientList(dev.deviceAddress);
                        }
                        if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                            logd("netId related with " + dev.deviceAddress + " = " + netId2);
                        }
                        if (netId2 >= 0) {
                            if (this.mWifiNative.p2pReinvoke(netId2, dev.deviceAddress)) {
                                config.netId = netId2;
                                return true;
                            }
                            loge("p2pReinvoke() failed, update networks");
                            updatePersistentNetworks(WifiP2pServiceImpl.RELOAD.booleanValue());
                            return false;
                        }
                    }
                    return false;
                }
                loge("target device reaches the device limit.");
                return false;
            }

            private int getNetworkIdFromClientList(String deviceAddress) {
                if (deviceAddress == null) {
                    return -1;
                }
                for (WifiP2pGroup group : this.mGroups.getGroupList()) {
                    int netId = group.getNetworkId();
                    String[] p2pClientList = getClientList(netId);
                    if (p2pClientList != null) {
                        for (String client : p2pClientList) {
                            if (deviceAddress.equalsIgnoreCase(client)) {
                                return netId;
                            }
                        }
                        continue;
                    }
                }
                return -1;
            }

            private String[] getClientList(int netId) {
                String p2pClients = this.mWifiNative.getP2pClientList(netId);
                if (p2pClients == null) {
                    return null;
                }
                return p2pClients.split(" ");
            }

            /* access modifiers changed from: private */
            public boolean removeClientFromList(int netId, String addr, boolean isRemovable) {
                StringBuilder modifiedClientList = new StringBuilder();
                String[] currentClientList = getClientList(netId);
                boolean isClientRemoved = false;
                if (currentClientList != null) {
                    boolean isClientRemoved2 = false;
                    for (String client : currentClientList) {
                        if (!client.equalsIgnoreCase(addr)) {
                            modifiedClientList.append(" ");
                            modifiedClientList.append(client);
                        } else {
                            isClientRemoved2 = true;
                        }
                    }
                    isClientRemoved = isClientRemoved2;
                }
                if (modifiedClientList.length() == 0 && isRemovable) {
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        logd("Remove unknown network");
                    }
                    this.mGroups.remove(netId);
                    WifiP2pServiceImpl.this.mWifiP2pMetrics.updatePersistentGroup(this.mGroups);
                    return true;
                } else if (!isClientRemoved) {
                    return false;
                } else {
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        logd("Modified client list: " + modifiedClientList);
                    }
                    if (modifiedClientList.length() == 0) {
                        modifiedClientList.append("\"\"");
                    }
                    this.mWifiNative.setP2pClientList(netId, modifiedClientList.toString());
                    this.mWifiNative.saveConfig();
                    return true;
                }
            }

            /* access modifiers changed from: private */
            public void setWifiP2pInfoOnGroupFormation(InetAddress serverInetAddress) {
                WifiP2pInfo wifiP2pInfo = this.mWifiP2pInfo;
                wifiP2pInfo.groupFormed = true;
                wifiP2pInfo.isGroupOwner = this.mGroup.isGroupOwner();
                this.mWifiP2pInfo.groupOwnerAddress = serverInetAddress;
            }

            /* access modifiers changed from: private */
            public void resetWifiP2pInfo() {
                WifiP2pInfo wifiP2pInfo = this.mWifiP2pInfo;
                wifiP2pInfo.groupFormed = false;
                wifiP2pInfo.isGroupOwner = false;
                wifiP2pInfo.groupOwnerAddress = null;
            }

            /* access modifiers changed from: private */
            public String getDeviceName(String deviceAddress) {
                WifiP2pDevice d = this.mPeers.get(deviceAddress);
                if (d != null) {
                    return d.deviceName;
                }
                return deviceAddress;
            }

            private String getPersistedDeviceName() {
                String deviceName = WifiP2pServiceImpl.this.mFrameworkFacade.getStringSetting(WifiP2pServiceImpl.this.mContext, "wifi_p2p_device_name");
                if (deviceName != null) {
                    return deviceName;
                }
                String id = WifiP2pServiceImpl.this.mFrameworkFacade.getSecureStringSetting(WifiP2pServiceImpl.this.mContext, "android_id");
                return "Android_" + id.substring(0, 4);
            }

            /* access modifiers changed from: private */
            public boolean setAndPersistDeviceName(String devName) {
                if (devName == null) {
                    return false;
                }
                if (!this.mWifiNative.setDeviceName(devName)) {
                    loge("Failed to set device name " + devName);
                    return false;
                }
                WifiP2pServiceImpl.this.mThisDevice.deviceName = devName;
                WifiP2pNative wifiP2pNative = this.mWifiNative;
                wifiP2pNative.setP2pSsidPostfix("-" + WifiP2pServiceImpl.this.mThisDevice.deviceName);
                WifiP2pServiceImpl.this.mFrameworkFacade.setStringSetting(WifiP2pServiceImpl.this.mContext, "wifi_p2p_device_name", devName);
                sendThisDeviceChangedBroadcast();
                return true;
            }

            /* access modifiers changed from: private */
            public boolean setWfdInfo(WifiP2pWfdInfo wfdInfo) {
                boolean success;
                if (!wfdInfo.isWfdEnabled()) {
                    success = this.mWifiNative.setWfdEnable(false);
                } else {
                    success = this.mWifiNative.setWfdEnable(true) && this.mWifiNative.setWfdDeviceInfo(wfdInfo.getDeviceInfoHex());
                }
                if (!success) {
                    loge("Failed to set wfd properties");
                    return false;
                }
                WifiP2pServiceImpl.this.mThisDevice.wfdInfo = wfdInfo;
                sendThisDeviceChangedBroadcast();
                return true;
            }

            /* access modifiers changed from: private */
            public boolean setWfdR2Info(WifiP2pWfdInfo wfdInfo) {
                boolean success;
                if (!wfdInfo.isWfdEnabled()) {
                    success = this.mWifiNative.setWfdEnable(false);
                } else {
                    success = this.mWifiNative.setWfdEnable(true) && this.mWifiNative.setWfdR2DeviceInfo(wfdInfo.getR2DeviceInfoHex());
                }
                if (!success) {
                    loge("Failed to set wfd properties");
                    return false;
                }
                WifiP2pServiceImpl.this.mThisDevice.wfdInfo = wfdInfo;
                sendThisDeviceChangedBroadcast();
                return true;
            }

            /* access modifiers changed from: private */
            public void initializeP2pSettings() {
                WifiP2pServiceImpl.this.mThisDevice.deviceName = getPersistedDeviceName();
                this.mWifiNative.setP2pDeviceName(WifiP2pServiceImpl.this.mThisDevice.deviceName);
                WifiP2pNative wifiP2pNative = this.mWifiNative;
                wifiP2pNative.setP2pSsidPostfix("-" + WifiP2pServiceImpl.this.mThisDevice.deviceName);
                this.mWifiNative.setP2pDeviceType(WifiP2pServiceImpl.this.mThisDevice.primaryDeviceType);
                this.mWifiNative.setConfigMethods("virtual_push_button physical_display keypad");
                WifiP2pServiceImpl.this.mThisDevice.deviceAddress = this.mWifiNative.p2pGetDeviceAddress();
                updateThisDevice(3);
                if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                    logd("DeviceAddress: " + WifiP2pServiceImpl.this.mThisDevice.deviceAddress);
                }
                this.mWifiNative.p2pFlush();
                this.mWifiNative.p2pServiceFlush();
                byte unused = WifiP2pServiceImpl.this.mServiceTransactionId = (byte) 0;
                String unused2 = WifiP2pServiceImpl.this.mServiceDiscReqId = null;
                updatePersistentNetworks(WifiP2pServiceImpl.RELOAD.booleanValue());
                enableVerboseLogging(WifiP2pServiceImpl.this.mFrameworkFacade.getIntegerSetting(WifiP2pServiceImpl.this.mContext, "wifi_verbose_logging_enabled", 0));
            }

            /* access modifiers changed from: private */
            public void updateThisDevice(int status) {
                WifiP2pServiceImpl.this.mThisDevice.status = status;
                sendThisDeviceChangedBroadcast();
            }

            /* access modifiers changed from: private */
            public void handleGroupCreationFailure() {
                resetWifiP2pInfo();
                WifiP2pServiceImpl.this.mNetworkInfo.setDetailedState(NetworkInfo.DetailedState.FAILED, (String) null, (String) null);
                sendP2pConnectionChangedBroadcast();
                boolean peersChanged = this.mPeers.remove(this.mPeersLostDuringConnection);
                if (!TextUtils.isEmpty(this.mSavedPeerConfig.deviceAddress) && this.mPeers.remove(this.mSavedPeerConfig.deviceAddress) != null) {
                    peersChanged = true;
                }
                if (peersChanged) {
                    sendPeersChangedBroadcast();
                }
                this.mPeersLostDuringConnection.clear();
                String unused = WifiP2pServiceImpl.this.mServiceDiscReqId = null;
                sendMessage(139265);
            }

            /* access modifiers changed from: private */
            public void handleGroupRemoved() {
                if (this.mGroup.isGroupOwner()) {
                    stopDhcpServer(this.mGroup.getInterface());
                } else {
                    if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                        logd("stop IpClient");
                    }
                    WifiP2pServiceImpl.this.stopIpClient();
                    try {
                        WifiP2pServiceImpl.this.mNwService.removeInterfaceFromLocalNetwork(this.mGroup.getInterface());
                    } catch (RemoteException e) {
                        loge("Failed to remove iface from local network " + e);
                    }
                }
                try {
                    WifiP2pServiceImpl.this.mNwService.clearInterfaceAddresses(this.mGroup.getInterface());
                } catch (Exception e2) {
                    loge("Failed to clear addresses " + e2);
                }
                this.mWifiNative.setP2pGroupIdle(this.mGroup.getInterface(), 0);
                boolean peersChanged = false;
                for (WifiP2pDevice d : this.mGroup.getClientList()) {
                    if (this.mPeers.remove(d)) {
                        peersChanged = true;
                    }
                }
                if (this.mPeers.remove(this.mGroup.getOwner())) {
                    peersChanged = true;
                }
                if (this.mPeers.remove(this.mPeersLostDuringConnection)) {
                    peersChanged = true;
                }
                if (peersChanged) {
                    sendPeersChangedBroadcast();
                }
                this.mGroup = null;
                this.mPeersLostDuringConnection.clear();
                String unused = WifiP2pServiceImpl.this.mServiceDiscReqId = null;
                if (WifiP2pServiceImpl.this.mTemporarilyDisconnectedWifi) {
                    if (WifiP2pServiceImpl.this.mWifiChannel != null) {
                        WifiP2pServiceImpl.this.mWifiChannel.sendMessage(WifiP2pServiceImpl.DISCONNECT_WIFI_REQUEST, 0);
                    } else {
                        loge("handleGroupRemoved(): WifiChannel is null");
                    }
                    boolean unused2 = WifiP2pServiceImpl.this.mTemporarilyDisconnectedWifi = false;
                }
            }

            /* access modifiers changed from: private */
            public void replyToMessage(Message msg, int what) {
                if (msg.replyTo != null) {
                    Message dstMsg = obtainMessage(msg);
                    dstMsg.what = what;
                    WifiP2pServiceImpl.this.mReplyChannel.replyToMessage(msg, dstMsg);
                }
            }

            /* access modifiers changed from: private */
            public void replyToMessage(Message msg, int what, int arg1) {
                if (msg.replyTo != null) {
                    Message dstMsg = obtainMessage(msg);
                    dstMsg.what = what;
                    dstMsg.arg1 = arg1;
                    WifiP2pServiceImpl.this.mReplyChannel.replyToMessage(msg, dstMsg);
                }
            }

            /* access modifiers changed from: private */
            public void replyToMessage(Message msg, int what, Object obj) {
                if (msg.replyTo != null) {
                    Message dstMsg = obtainMessage(msg);
                    dstMsg.what = what;
                    dstMsg.obj = obj;
                    WifiP2pServiceImpl.this.mReplyChannel.replyToMessage(msg, dstMsg);
                }
            }

            private Message obtainMessage(Message srcMsg) {
                Message msg = Message.obtain();
                msg.arg2 = srcMsg.arg2;
                return msg;
            }

            /* access modifiers changed from: protected */
            public void logd(String s) {
                Slog.d(WifiP2pServiceImpl.TAG, s);
            }

            /* access modifiers changed from: protected */
            public void loge(String s) {
                Slog.e(WifiP2pServiceImpl.TAG, s);
            }

            /* access modifiers changed from: private */
            public boolean updateSupplicantServiceRequest() {
                clearSupplicantServiceRequest();
                StringBuffer sb = new StringBuffer();
                for (ClientInfo c : WifiP2pServiceImpl.this.mClientInfoList.values()) {
                    for (int i = 0; i < c.mReqList.size(); i++) {
                        WifiP2pServiceRequest req = (WifiP2pServiceRequest) c.mReqList.valueAt(i);
                        if (req != null) {
                            sb.append(req.getSupplicantQuery());
                        }
                    }
                }
                if (sb.length() == 0) {
                    return false;
                }
                String unused = WifiP2pServiceImpl.this.mServiceDiscReqId = this.mWifiNative.p2pServDiscReq(WifiP2pServiceImpl.EMPTY_DEVICE_ADDRESS, sb.toString());
                if (WifiP2pServiceImpl.this.mServiceDiscReqId == null) {
                    return false;
                }
                return true;
            }

            /* access modifiers changed from: private */
            public void clearSupplicantServiceRequest() {
                if (WifiP2pServiceImpl.this.mServiceDiscReqId != null) {
                    this.mWifiNative.p2pServDiscCancelReq(WifiP2pServiceImpl.this.mServiceDiscReqId);
                    String unused = WifiP2pServiceImpl.this.mServiceDiscReqId = null;
                }
            }

            /* access modifiers changed from: private */
            public boolean addServiceRequest(Messenger m, WifiP2pServiceRequest req) {
                if (m == null || req == null) {
                    Log.e(WifiP2pServiceImpl.TAG, "Illegal argument(s)");
                    return false;
                }
                clearClientDeadChannels();
                ClientInfo clientInfo = getClientInfo(m, false);
                if (clientInfo == null) {
                    return false;
                }
                WifiP2pServiceImpl.access$11104(WifiP2pServiceImpl.this);
                if (WifiP2pServiceImpl.this.mServiceTransactionId == 0) {
                    WifiP2pServiceImpl.access$11104(WifiP2pServiceImpl.this);
                }
                req.setTransactionId(WifiP2pServiceImpl.this.mServiceTransactionId);
                clientInfo.mReqList.put(WifiP2pServiceImpl.this.mServiceTransactionId, req);
                if (WifiP2pServiceImpl.this.mServiceDiscReqId == null) {
                    return true;
                }
                return updateSupplicantServiceRequest();
            }

            /* access modifiers changed from: private */
            public void removeServiceRequest(Messenger m, WifiP2pServiceRequest req) {
                if (m == null || req == null) {
                    Log.e(WifiP2pServiceImpl.TAG, "Illegal argument(s)");
                }
                ClientInfo clientInfo = getClientInfo(m, false);
                if (clientInfo != null) {
                    boolean removed = false;
                    int i = 0;
                    while (true) {
                        if (i >= clientInfo.mReqList.size()) {
                            break;
                        } else if (req.equals(clientInfo.mReqList.valueAt(i))) {
                            removed = true;
                            clientInfo.mReqList.removeAt(i);
                            break;
                        } else {
                            i++;
                        }
                    }
                    if (removed && WifiP2pServiceImpl.this.mServiceDiscReqId != null) {
                        updateSupplicantServiceRequest();
                    }
                }
            }

            /* access modifiers changed from: private */
            public void clearServiceRequests(Messenger m) {
                if (m == null) {
                    Log.e(WifiP2pServiceImpl.TAG, "Illegal argument(s)");
                    return;
                }
                ClientInfo clientInfo = getClientInfo(m, false);
                if (clientInfo != null && clientInfo.mReqList.size() != 0) {
                    clientInfo.mReqList.clear();
                    if (WifiP2pServiceImpl.this.mServiceDiscReqId != null) {
                        updateSupplicantServiceRequest();
                    }
                }
            }

            /* access modifiers changed from: private */
            public boolean addLocalService(Messenger m, WifiP2pServiceInfo servInfo) {
                if (m == null || servInfo == null) {
                    Log.e(WifiP2pServiceImpl.TAG, "Illegal arguments");
                    return false;
                }
                clearClientDeadChannels();
                ClientInfo clientInfo = getClientInfo(m, false);
                if (clientInfo == null || !clientInfo.mServList.add(servInfo)) {
                    return false;
                }
                if (this.mWifiNative.p2pServiceAdd(servInfo)) {
                    return true;
                }
                clientInfo.mServList.remove(servInfo);
                return false;
            }

            /* access modifiers changed from: private */
            public void removeLocalService(Messenger m, WifiP2pServiceInfo servInfo) {
                if (m == null || servInfo == null) {
                    Log.e(WifiP2pServiceImpl.TAG, "Illegal arguments");
                    return;
                }
                ClientInfo clientInfo = getClientInfo(m, false);
                if (clientInfo != null) {
                    this.mWifiNative.p2pServiceDel(servInfo);
                    clientInfo.mServList.remove(servInfo);
                }
            }

            /* access modifiers changed from: private */
            public void clearLocalServices(Messenger m) {
                if (m == null) {
                    Log.e(WifiP2pServiceImpl.TAG, "Illegal argument(s)");
                    return;
                }
                ClientInfo clientInfo = getClientInfo(m, false);
                if (clientInfo != null) {
                    for (WifiP2pServiceInfo servInfo : clientInfo.mServList) {
                        this.mWifiNative.p2pServiceDel(servInfo);
                    }
                    clientInfo.mServList.clear();
                }
            }

            /* access modifiers changed from: private */
            public void clearClientInfo(Messenger m) {
                clearLocalServices(m);
                clearServiceRequests(m);
                ClientInfo clientInfo = (ClientInfo) WifiP2pServiceImpl.this.mClientInfoList.remove(m);
                if (clientInfo != null) {
                    logd("Client:" + clientInfo.mPackageName + " is removed");
                }
            }

            /* access modifiers changed from: private */
            public void sendServiceResponse(WifiP2pServiceResponse resp) {
                if (resp == null) {
                    Log.e(WifiP2pServiceImpl.TAG, "sendServiceResponse with null response");
                    return;
                }
                for (ClientInfo c : WifiP2pServiceImpl.this.mClientInfoList.values()) {
                    if (((WifiP2pServiceRequest) c.mReqList.get(resp.getTransactionId())) != null) {
                        Message msg = Message.obtain();
                        msg.what = 139314;
                        msg.arg1 = 0;
                        msg.arg2 = 0;
                        msg.obj = resp;
                        if (c.mMessenger != null) {
                            try {
                                c.mMessenger.send(msg);
                            } catch (RemoteException e) {
                                if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                    logd("detect dead channel");
                                }
                                clearClientInfo(c.mMessenger);
                                return;
                            }
                        }
                    }
                }
            }

            private void clearClientDeadChannels() {
                ArrayList<Messenger> deadClients = new ArrayList<>();
                for (ClientInfo c : WifiP2pServiceImpl.this.mClientInfoList.values()) {
                    Message msg = Message.obtain();
                    msg.what = 139313;
                    msg.arg1 = 0;
                    msg.arg2 = 0;
                    msg.obj = null;
                    if (c.mMessenger != null) {
                        try {
                            c.mMessenger.send(msg);
                        } catch (RemoteException e) {
                            if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                                logd("detect dead channel");
                            }
                            deadClients.add(c.mMessenger);
                        }
                    }
                }
                Iterator<Messenger> it = deadClients.iterator();
                while (it.hasNext()) {
                    clearClientInfo(it.next());
                }
            }

            /* access modifiers changed from: private */
            public ClientInfo getClientInfo(Messenger m, boolean createIfNotExist) {
                ClientInfo clientInfo = (ClientInfo) WifiP2pServiceImpl.this.mClientInfoList.get(m);
                if (clientInfo != null || !createIfNotExist) {
                    return clientInfo;
                }
                if (WifiP2pServiceImpl.this.mVerboseLoggingEnabled) {
                    logd("add a new client");
                }
                ClientInfo clientInfo2 = new ClientInfo(m);
                WifiP2pServiceImpl.this.mClientInfoList.put(m, clientInfo2);
                return clientInfo2;
            }

            /* access modifiers changed from: private */
            public WifiP2pDeviceList getPeers(String pkgName, int uid) {
                if (WifiP2pServiceImpl.this.mWifiPermissionsUtil.checkCanAccessWifiDirect(pkgName, uid, true)) {
                    return new WifiP2pDeviceList(this.mPeers);
                }
                return new WifiP2pDeviceList();
            }

            /* access modifiers changed from: private */
            public void enableBTCoex() {
                if (this.mIsBTCoexDisabled) {
                    this.mWifNative.setBluetoothCoexistenceMode(this.mInterfaceName, 2);
                    this.mIsBTCoexDisabled = false;
                }
            }

            private void setPendingFactoryReset(boolean pending) {
                WifiP2pServiceImpl.this.mFrameworkFacade.setIntegerSetting(WifiP2pServiceImpl.this.mContext, "wifi_p2p_pending_factory_reset", pending);
            }

            /* access modifiers changed from: private */
            public boolean isPendingFactoryReset() {
                if (WifiP2pServiceImpl.this.mFrameworkFacade.getIntegerSetting(WifiP2pServiceImpl.this.mContext, "wifi_p2p_pending_factory_reset", 0) != 0) {
                    return true;
                }
                return false;
            }

            /* access modifiers changed from: private */
            public boolean factoryReset(int uid) {
                String pkgName = WifiP2pServiceImpl.this.mContext.getPackageManager().getNameForUid(uid);
                UserManager userManager = WifiP2pServiceImpl.this.mWifiInjector.getUserManager();
                if (!WifiP2pServiceImpl.this.mWifiPermissionsUtil.checkNetworkSettingsPermission(uid) || userManager.hasUserRestriction("no_network_reset") || userManager.hasUserRestriction("no_config_wifi")) {
                    return false;
                }
                Log.i(WifiP2pServiceImpl.TAG, "factoryReset uid=" + uid + " pkg=" + pkgName);
                if (WifiP2pServiceImpl.this.mNetworkInfo.isAvailable()) {
                    if (this.mWifiNative.p2pListNetworks(this.mGroups)) {
                        for (WifiP2pGroup group : this.mGroups.getGroupList()) {
                            this.mWifiNative.removeP2pNetwork(group.getNetworkId());
                        }
                    }
                    updatePersistentNetworks(true);
                    setPendingFactoryReset(false);
                } else {
                    setPendingFactoryReset(true);
                }
                return true;
            }

            /* access modifiers changed from: private */
            public String getCallingPkgName(int uid, Messenger replyMessenger) {
                ClientInfo clientInfo = (ClientInfo) WifiP2pServiceImpl.this.mClientInfoList.get(replyMessenger);
                if (clientInfo != null) {
                    return clientInfo.mPackageName;
                }
                if (uid == 1000) {
                    return WifiP2pServiceImpl.this.mContext.getOpPackageName();
                }
                return null;
            }

            /* access modifiers changed from: private */
            public void clearServicesForAllClients() {
                for (ClientInfo c : WifiP2pServiceImpl.this.mClientInfoList.values()) {
                    clearLocalServices(c.mMessenger);
                    clearServiceRequests(c.mMessenger);
                }
            }
        }

        private class ClientInfo {
            /* access modifiers changed from: private */
            public Messenger mMessenger;
            /* access modifiers changed from: private */
            public String mPackageName;
            /* access modifiers changed from: private */
            public SparseArray<WifiP2pServiceRequest> mReqList;
            /* access modifiers changed from: private */
            public List<WifiP2pServiceInfo> mServList;

            private ClientInfo(Messenger m) {
                this.mMessenger = m;
                this.mPackageName = null;
                this.mReqList = new SparseArray<>();
                this.mServList = new ArrayList();
            }
        }
    }
