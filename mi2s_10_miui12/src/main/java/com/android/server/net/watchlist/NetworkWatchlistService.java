package com.android.server.net.watchlist;

import android.content.Context;
import android.net.IIpConnectivityMetrics;
import android.net.INetdEventCallback;
import android.os.Binder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.net.INetworkWatchlistManager;
import com.android.internal.util.DumpUtils;
import com.android.server.ServiceThread;
import com.android.server.SystemService;
import com.android.server.net.BaseNetdEventCallback;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class NetworkWatchlistService extends INetworkWatchlistManager.Stub {
    static final boolean DEBUG = false;
    private static final int MAX_NUM_OF_WATCHLIST_DIGESTS = 10000;
    /* access modifiers changed from: private */
    public static final String TAG = NetworkWatchlistService.class.getSimpleName();
    private final WatchlistConfig mConfig;
    private final Context mContext;
    private final ServiceThread mHandlerThread;
    @VisibleForTesting
    IIpConnectivityMetrics mIpConnectivityMetrics;
    /* access modifiers changed from: private */
    @GuardedBy({"mLoggingSwitchLock"})
    public volatile boolean mIsLoggingEnabled = false;
    private final Object mLoggingSwitchLock = new Object();
    private final INetdEventCallback mNetdEventCallback = new BaseNetdEventCallback() {
        public void onDnsEvent(int netId, int eventType, int returnCode, String hostname, String[] ipAddresses, int ipAddressesCount, long timestamp, int uid) {
            if (NetworkWatchlistService.this.mIsLoggingEnabled) {
                NetworkWatchlistService.this.mNetworkWatchlistHandler.asyncNetworkEvent(hostname, ipAddresses, uid);
            }
        }

        public void onConnectEvent(String ipAddr, int port, long timestamp, int uid) {
            if (NetworkWatchlistService.this.mIsLoggingEnabled) {
                NetworkWatchlistService.this.mNetworkWatchlistHandler.asyncNetworkEvent((String) null, new String[]{ipAddr}, uid);
            }
        }
    };
    @VisibleForTesting
    WatchlistLoggingHandler mNetworkWatchlistHandler;

    public static class Lifecycle extends SystemService {
        private NetworkWatchlistService mService;

        public Lifecycle(Context context) {
            super(context);
        }

        /* JADX WARNING: type inference failed for: r0v4, types: [com.android.server.net.watchlist.NetworkWatchlistService, android.os.IBinder] */
        public void onStart() {
            if (Settings.Global.getInt(getContext().getContentResolver(), "network_watchlist_enabled", 1) == 0) {
                Slog.i(NetworkWatchlistService.TAG, "Network Watchlist service is disabled");
                return;
            }
            this.mService = new NetworkWatchlistService(getContext());
            publishBinderService("network_watchlist", this.mService);
        }

        public void onBootPhase(int phase) {
            if (phase != 550) {
                return;
            }
            if (Settings.Global.getInt(getContext().getContentResolver(), "network_watchlist_enabled", 1) == 0) {
                Slog.i(NetworkWatchlistService.TAG, "Network Watchlist service is disabled");
                return;
            }
            try {
                this.mService.init();
                this.mService.initIpConnectivityMetrics();
                this.mService.startWatchlistLogging();
            } catch (RemoteException e) {
            }
            ReportWatchlistJobService.schedule(getContext());
        }
    }

    public NetworkWatchlistService(Context context) {
        this.mContext = context;
        this.mConfig = WatchlistConfig.getInstance();
        this.mHandlerThread = new ServiceThread(TAG, 10, false);
        this.mHandlerThread.start();
        this.mNetworkWatchlistHandler = new WatchlistLoggingHandler(this.mContext, this.mHandlerThread.getLooper());
        this.mNetworkWatchlistHandler.reportWatchlistIfNecessary();
    }

    @VisibleForTesting
    NetworkWatchlistService(Context context, ServiceThread handlerThread, WatchlistLoggingHandler handler, IIpConnectivityMetrics ipConnectivityMetrics) {
        this.mContext = context;
        this.mConfig = WatchlistConfig.getInstance();
        this.mHandlerThread = handlerThread;
        this.mNetworkWatchlistHandler = handler;
        this.mIpConnectivityMetrics = ipConnectivityMetrics;
    }

    /* access modifiers changed from: private */
    public void init() {
        this.mConfig.removeTestModeConfig();
    }

    /* access modifiers changed from: private */
    public void initIpConnectivityMetrics() {
        this.mIpConnectivityMetrics = IIpConnectivityMetrics.Stub.asInterface(ServiceManager.getService("connmetrics"));
    }

    private boolean isCallerShell() {
        int callingUid = Binder.getCallingUid();
        return callingUid == 2000 || callingUid == 0;
    }

    /* JADX WARNING: type inference failed for: r1v1, types: [android.os.Binder] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onShellCommand(java.io.FileDescriptor r9, java.io.FileDescriptor r10, java.io.FileDescriptor r11, java.lang.String[] r12, android.os.ShellCallback r13, android.os.ResultReceiver r14) {
        /*
            r8 = this;
            boolean r0 = r8.isCallerShell()
            if (r0 != 0) goto L_0x000e
            java.lang.String r0 = TAG
            java.lang.String r1 = "Only shell is allowed to call network watchlist shell commands"
            android.util.Slog.w(r0, r1)
            return
        L_0x000e:
            com.android.server.net.watchlist.NetworkWatchlistShellCommand r0 = new com.android.server.net.watchlist.NetworkWatchlistShellCommand
            android.content.Context r1 = r8.mContext
            r0.<init>(r8, r1)
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
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.net.watchlist.NetworkWatchlistService.onShellCommand(java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):void");
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public boolean startWatchlistLoggingImpl() throws RemoteException {
        synchronized (this.mLoggingSwitchLock) {
            if (this.mIsLoggingEnabled) {
                Slog.w(TAG, "Watchlist logging is already running");
                return true;
            }
            try {
                if (!this.mIpConnectivityMetrics.addNetdEventCallback(2, this.mNetdEventCallback)) {
                    return false;
                }
                this.mIsLoggingEnabled = true;
                return true;
            } catch (RemoteException e) {
                return false;
            }
        }
    }

    public boolean startWatchlistLogging() throws RemoteException {
        enforceWatchlistLoggingPermission();
        return startWatchlistLoggingImpl();
    }

    /* access modifiers changed from: protected */
    @VisibleForTesting
    public boolean stopWatchlistLoggingImpl() {
        synchronized (this.mLoggingSwitchLock) {
            if (!this.mIsLoggingEnabled) {
                Slog.w(TAG, "Watchlist logging is not running");
                return true;
            }
            this.mIsLoggingEnabled = false;
            try {
                boolean removeNetdEventCallback = this.mIpConnectivityMetrics.removeNetdEventCallback(2);
                return removeNetdEventCallback;
            } catch (RemoteException e) {
                return false;
            }
        }
    }

    public boolean stopWatchlistLogging() throws RemoteException {
        enforceWatchlistLoggingPermission();
        return stopWatchlistLoggingImpl();
    }

    public byte[] getWatchlistConfigHash() {
        return this.mConfig.getWatchlistConfigHash();
    }

    private void enforceWatchlistLoggingPermission() {
        int uid = Binder.getCallingUid();
        if (uid != 1000) {
            throw new SecurityException(String.format("Uid %d has no permission to change watchlist setting.", new Object[]{Integer.valueOf(uid)}));
        }
    }

    public void reloadWatchlist() throws RemoteException {
        enforceWatchlistLoggingPermission();
        Slog.i(TAG, "Reloading watchlist");
        this.mConfig.reloadConfig();
    }

    public void reportWatchlistIfNecessary() {
        this.mNetworkWatchlistHandler.reportWatchlistIfNecessary();
    }

    public boolean forceReportWatchlistForTest(long lastReportTime) {
        if (this.mConfig.isConfigSecure()) {
            return false;
        }
        this.mNetworkWatchlistHandler.forceReportWatchlistForTest(lastReportTime);
        return true;
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (DumpUtils.checkDumpPermission(this.mContext, TAG, pw)) {
            this.mConfig.dump(fd, pw, args);
        }
    }
}
