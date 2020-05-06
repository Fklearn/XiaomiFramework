package com.android.server.wifi;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.app.IBatteryStats;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import com.android.server.wifi.ActiveModeWarden;
import com.android.server.wifi.ClientModeManager;
import com.android.server.wifi.ScanOnlyModeManager;
import com.android.server.wifi.WifiNative;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;

public class ActiveModeWarden {
    static final int BASE = 131072;
    static final int CMD_AP_STOPPED = 131096;
    static final int CMD_CLIENT_MODE_FAILED = 131376;
    static final int CMD_CLIENT_MODE_STOPPED = 131375;
    static final int CMD_SCAN_ONLY_MODE_FAILED = 131276;
    static final int CMD_SCAN_ONLY_MODE_STOPPED = 131275;
    static final int CMD_START_AP = 131093;
    static final int CMD_START_AP_FAILURE = 131094;
    static final int CMD_START_CLIENT_MODE = 131372;
    static final int CMD_START_CLIENT_MODE_FAILURE = 131373;
    static final int CMD_START_SCAN_ONLY_MODE = 131272;
    static final int CMD_START_SCAN_ONLY_MODE_FAILURE = 131273;
    static final int CMD_STOP_AP = 131095;
    static final int CMD_STOP_CLIENT_MODE = 131374;
    static final int CMD_STOP_SCAN_ONLY_MODE = 131274;
    private static final String TAG = "WifiActiveModeWarden";
    /* access modifiers changed from: private */
    public final CopyOnWriteArraySet<ActiveModeManager> mActiveModeManagers = new CopyOnWriteArraySet<>();
    private final IBatteryStats mBatteryStats;
    /* access modifiers changed from: private */
    public ClientModeManager.Listener mClientModeCallback;
    private final Context mContext;
    private DefaultModeManager mDefaultModeManager;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    /* access modifiers changed from: private */
    public final Looper mLooper;
    /* access modifiers changed from: private */
    public ModeStateMachine mModeStateMachine;
    /* access modifiers changed from: private */
    public ScanOnlyModeManager.Listener mScanOnlyCallback;
    /* access modifiers changed from: private */
    public final ScanRequestProxy mScanRequestProxy;
    private final SelfRecovery mSelfRecovery;
    /* access modifiers changed from: private */
    public WifiManager.SoftApCallback mSoftApCallback;
    /* access modifiers changed from: private */
    public BaseWifiDiagnostics mWifiDiagnostics;
    /* access modifiers changed from: private */
    public final WifiInjector mWifiInjector;
    private final WifiNative mWifiNative;
    private WifiNative.StatusListener mWifiNativeStatusListener;

    public void registerSoftApCallback(WifiManager.SoftApCallback callback) {
        this.mSoftApCallback = callback;
    }

    public void registerScanOnlyCallback(ScanOnlyModeManager.Listener callback) {
        this.mScanOnlyCallback = callback;
    }

    public void registerClientModeCallback(ClientModeManager.Listener callback) {
        this.mClientModeCallback = callback;
    }

    ActiveModeWarden(WifiInjector wifiInjector, Context context, Looper looper, WifiNative wifiNative, DefaultModeManager defaultModeManager, IBatteryStats batteryStats) {
        this.mWifiInjector = wifiInjector;
        this.mContext = context;
        this.mLooper = looper;
        this.mHandler = new Handler(looper);
        this.mWifiNative = wifiNative;
        this.mDefaultModeManager = defaultModeManager;
        this.mBatteryStats = batteryStats;
        this.mSelfRecovery = this.mWifiInjector.getSelfRecovery();
        this.mWifiDiagnostics = this.mWifiInjector.getWifiDiagnostics();
        this.mScanRequestProxy = this.mWifiInjector.getScanRequestProxy();
        this.mModeStateMachine = new ModeStateMachine();
        this.mWifiNativeStatusListener = new WifiNativeStatusListener();
        this.mWifiNative.registerStatusListener(this.mWifiNativeStatusListener);
    }

    public void enterClientMode() {
        changeMode(0);
    }

    public void enterScanOnlyMode() {
        changeMode(1);
    }

    public void enterSoftAPMode(SoftApModeConfiguration wifiConfig) {
        this.mHandler.post(new Runnable(wifiConfig) {
            private final /* synthetic */ SoftApModeConfiguration f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ActiveModeWarden.this.lambda$enterSoftAPMode$0$ActiveModeWarden(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$enterSoftAPMode$0$ActiveModeWarden(SoftApModeConfiguration wifiConfig) {
        Iterator<ActiveModeManager> it = this.mActiveModeManagers.iterator();
        while (it.hasNext()) {
            if (it.next() instanceof SoftApManager) {
                Log.d(TAG, "Dropping duplicate start softap requests!");
                return;
            }
        }
        startSoftAp(wifiConfig);
    }

    public void stopSoftAPMode(int mode) {
        this.mHandler.post(new Runnable(mode) {
            private final /* synthetic */ int f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ActiveModeWarden.this.lambda$stopSoftAPMode$1$ActiveModeWarden(this.f$1);
            }
        });
    }

    public /* synthetic */ void lambda$stopSoftAPMode$1$ActiveModeWarden(int mode) {
        ArraySet<ActiveModeManager> removeList = new ArraySet<>();
        Iterator<ActiveModeManager> it = this.mActiveModeManagers.iterator();
        while (it.hasNext()) {
            ActiveModeManager manager = it.next();
            if (manager instanceof SoftApManager) {
                SoftApManager softApManager = (SoftApManager) manager;
                if (mode == -1 || mode == softApManager.getIpMode()) {
                    softApManager.stop();
                    removeList.add(manager);
                }
            }
        }
        this.mActiveModeManagers.removeAll(removeList);
        updateBatteryStatsWifiState(false);
    }

    public void disableWifi() {
        changeMode(3);
    }

    public void shutdownWifi() {
        this.mHandler.post(new Runnable() {
            public final void run() {
                ActiveModeWarden.this.lambda$shutdownWifi$2$ActiveModeWarden();
            }
        });
    }

    public /* synthetic */ void lambda$shutdownWifi$2$ActiveModeWarden() {
        Iterator<ActiveModeManager> it = this.mActiveModeManagers.iterator();
        while (it.hasNext()) {
            it.next().stop();
        }
        updateBatteryStatsWifiState(false);
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("Dump of WifiActiveModeWarden");
        pw.println("Current wifi mode: " + getCurrentMode());
        pw.println("NumActiveModeManagers: " + this.mActiveModeManagers.size());
        Iterator<ActiveModeManager> it = this.mActiveModeManagers.iterator();
        while (it.hasNext()) {
            it.next().dump(fd, pw, args);
        }
    }

    /* access modifiers changed from: protected */
    public String getCurrentMode() {
        return this.mModeStateMachine.getCurrentMode();
    }

    private void changeMode(int newMode) {
        this.mModeStateMachine.sendMessage(newMode);
    }

    private class ModeCallback {
        ActiveModeManager mActiveManager;

        private ModeCallback() {
        }

        /* access modifiers changed from: package-private */
        public void setActiveModeManager(ActiveModeManager manager) {
            this.mActiveManager = manager;
        }

        /* access modifiers changed from: package-private */
        public ActiveModeManager getActiveModeManager() {
            return this.mActiveManager;
        }
    }

    private class ModeStateMachine extends StateMachine {
        public static final int CMD_DISABLE_WIFI = 3;
        public static final int CMD_START_CLIENT_MODE = 0;
        public static final int CMD_START_SCAN_ONLY_MODE = 1;
        private final State mClientModeActiveState = new ClientModeActiveState();
        private final State mScanOnlyModeActiveState = new ScanOnlyModeActiveState();
        /* access modifiers changed from: private */
        public final State mWifiDisabledState = new WifiDisabledState();

        ModeStateMachine() {
            super(ActiveModeWarden.TAG, ActiveModeWarden.this.mLooper);
            addState(this.mClientModeActiveState);
            addState(this.mScanOnlyModeActiveState);
            addState(this.mWifiDisabledState);
            Log.d(ActiveModeWarden.TAG, "Starting Wifi in WifiDisabledState");
            setInitialState(this.mWifiDisabledState);
            start();
        }

        /* access modifiers changed from: private */
        public String getCurrentMode() {
            return getCurrentState().getName();
        }

        /* access modifiers changed from: private */
        public boolean checkForAndHandleModeChange(Message message) {
            int i = message.what;
            if (i == 0) {
                Log.d(ActiveModeWarden.TAG, "Switching from " + getCurrentMode() + " to ClientMode");
                ActiveModeWarden.this.mModeStateMachine.transitionTo(this.mClientModeActiveState);
            } else if (i == 1) {
                Log.d(ActiveModeWarden.TAG, "Switching from " + getCurrentMode() + " to ScanOnlyMode");
                ActiveModeWarden.this.mModeStateMachine.transitionTo(this.mScanOnlyModeActiveState);
            } else if (i != 3) {
                return false;
            } else {
                Log.d(ActiveModeWarden.TAG, "Switching from " + getCurrentMode() + " to WifiDisabled");
                ActiveModeWarden.this.mModeStateMachine.transitionTo(this.mWifiDisabledState);
            }
            return true;
        }

        class ModeActiveState extends State {
            ActiveModeManager mManager;

            ModeActiveState() {
            }

            public boolean processMessage(Message message) {
                return false;
            }

            public void exit() {
                ActiveModeManager activeModeManager = this.mManager;
                if (activeModeManager != null) {
                    activeModeManager.stop();
                    ActiveModeWarden.this.mActiveModeManagers.remove(this.mManager);
                    updateScanMode();
                }
                ActiveModeWarden.this.updateBatteryStatsWifiState(false);
            }

            public void onModeActivationComplete() {
                updateScanMode();
            }

            private void updateScanMode() {
                boolean scanEnabled = false;
                boolean scanningForHiddenNetworksEnabled = false;
                Iterator it = ActiveModeWarden.this.mActiveModeManagers.iterator();
                while (it.hasNext()) {
                    int scanState = ((ActiveModeManager) it.next()).getScanMode();
                    if (scanState != 0) {
                        if (scanState == 1) {
                            scanEnabled = true;
                        } else if (scanState == 2) {
                            scanEnabled = true;
                            scanningForHiddenNetworksEnabled = true;
                        }
                    }
                }
                ActiveModeWarden.this.mScanRequestProxy.enableScanning(scanEnabled, scanningForHiddenNetworksEnabled);
            }
        }

        class WifiDisabledState extends ModeActiveState {
            WifiDisabledState() {
                super();
            }

            public void enter() {
                Log.d(ActiveModeWarden.TAG, "Entering WifiDisabledState");
            }

            public boolean processMessage(Message message) {
                Log.d(ActiveModeWarden.TAG, "received a message in WifiDisabledState: " + message);
                if (ModeStateMachine.this.checkForAndHandleModeChange(message)) {
                    return true;
                }
                return false;
            }

            public void exit() {
            }
        }

        class ClientModeActiveState extends ModeActiveState {
            ClientListener mListener;

            ClientModeActiveState() {
                super();
            }

            private class ClientListener implements ClientModeManager.Listener {
                private ClientListener() {
                }

                public void onStateChanged(int state) {
                    if (this != ClientModeActiveState.this.mListener) {
                        Log.d(ActiveModeWarden.TAG, "Client mode state change from previous manager");
                        return;
                    }
                    Log.d(ActiveModeWarden.TAG, "State changed from client mode. state = " + state);
                    if (state == 4) {
                        ActiveModeWarden.this.mModeStateMachine.sendMessage(131376, this);
                    } else if (state == 1) {
                        ActiveModeWarden.this.mModeStateMachine.sendMessage(131375, this);
                    } else if (state == 3) {
                        Log.d(ActiveModeWarden.TAG, "client mode active");
                        ClientModeActiveState.this.onModeActivationComplete();
                    }
                }
            }

            public void enter() {
                Log.d(ActiveModeWarden.TAG, "Entering ClientModeActiveState");
                this.mListener = new ClientListener();
                this.mManager = ActiveModeWarden.this.mWifiInjector.makeClientModeManager(this.mListener);
                this.mManager.start();
                ActiveModeWarden.this.mActiveModeManagers.add(this.mManager);
                ActiveModeWarden.this.updateBatteryStatsWifiState(true);
            }

            public void exit() {
                super.exit();
                this.mListener = null;
            }

            public boolean processMessage(Message message) {
                if (ModeStateMachine.this.checkForAndHandleModeChange(message)) {
                    return true;
                }
                int i = message.what;
                if (i != 0) {
                    switch (i) {
                        case 131375:
                            if (this.mListener == message.obj) {
                                Log.d(ActiveModeWarden.TAG, "ClientMode stopped, return to WifiDisabledState.");
                                ActiveModeWarden.this.mClientModeCallback.onStateChanged(1);
                                ActiveModeWarden.this.mModeStateMachine.transitionTo(ModeStateMachine.this.mWifiDisabledState);
                                break;
                            } else {
                                Log.d(ActiveModeWarden.TAG, "Client mode state change from previous manager");
                                return true;
                            }
                        case 131376:
                            if (this.mListener == message.obj) {
                                Log.d(ActiveModeWarden.TAG, "ClientMode failed, return to WifiDisabledState.");
                                ActiveModeWarden.this.mClientModeCallback.onStateChanged(4);
                                ActiveModeWarden.this.mModeStateMachine.transitionTo(ModeStateMachine.this.mWifiDisabledState);
                                break;
                            } else {
                                Log.d(ActiveModeWarden.TAG, "Client mode state change from previous manager");
                                return true;
                            }
                        default:
                            return false;
                    }
                } else {
                    Log.d(ActiveModeWarden.TAG, "Received CMD_START_CLIENT_MODE when active - drop");
                }
                return false;
            }
        }

        class ScanOnlyModeActiveState extends ModeActiveState {
            ScanOnlyListener mListener;

            ScanOnlyModeActiveState() {
                super();
            }

            private class ScanOnlyListener implements ScanOnlyModeManager.Listener {
                private ScanOnlyListener() {
                }

                public void onStateChanged(int state) {
                    if (this != ScanOnlyModeActiveState.this.mListener) {
                        Log.d(ActiveModeWarden.TAG, "ScanOnly mode state change from previous manager");
                    } else if (state == 4) {
                        Log.d(ActiveModeWarden.TAG, "ScanOnlyMode mode failed");
                        ActiveModeWarden.this.mModeStateMachine.sendMessage(ActiveModeWarden.CMD_SCAN_ONLY_MODE_FAILED, this);
                    } else if (state == 1) {
                        Log.d(ActiveModeWarden.TAG, "ScanOnlyMode stopped");
                        ActiveModeWarden.this.mModeStateMachine.sendMessage(ActiveModeWarden.CMD_SCAN_ONLY_MODE_STOPPED, this);
                    } else if (state == 3) {
                        Log.d(ActiveModeWarden.TAG, "scan mode active");
                        ScanOnlyModeActiveState.this.onModeActivationComplete();
                    } else {
                        Log.d(ActiveModeWarden.TAG, "unexpected state update: " + state);
                    }
                }
            }

            public void enter() {
                Log.d(ActiveModeWarden.TAG, "Entering ScanOnlyModeActiveState");
                this.mListener = new ScanOnlyListener();
                this.mManager = ActiveModeWarden.this.mWifiInjector.makeScanOnlyModeManager(this.mListener);
                this.mManager.start();
                ActiveModeWarden.this.mActiveModeManagers.add(this.mManager);
                ActiveModeWarden.this.updateBatteryStatsWifiState(true);
                ActiveModeWarden.this.updateBatteryStatsScanModeActive();
            }

            public void exit() {
                super.exit();
                this.mListener = null;
            }

            public boolean processMessage(Message message) {
                if (ModeStateMachine.this.checkForAndHandleModeChange(message)) {
                    return true;
                }
                int i = message.what;
                if (i != 1) {
                    switch (i) {
                        case ActiveModeWarden.CMD_SCAN_ONLY_MODE_STOPPED /*131275*/:
                            if (this.mListener == message.obj) {
                                Log.d(ActiveModeWarden.TAG, "ScanOnlyMode stopped, return to WifiDisabledState.");
                                ActiveModeWarden.this.mScanOnlyCallback.onStateChanged(1);
                                ActiveModeWarden.this.mModeStateMachine.transitionTo(ModeStateMachine.this.mWifiDisabledState);
                                break;
                            } else {
                                Log.d(ActiveModeWarden.TAG, "ScanOnly mode state change from previous manager");
                                return true;
                            }
                        case ActiveModeWarden.CMD_SCAN_ONLY_MODE_FAILED /*131276*/:
                            if (this.mListener == message.obj) {
                                Log.d(ActiveModeWarden.TAG, "ScanOnlyMode failed, return to WifiDisabledState.");
                                ActiveModeWarden.this.mScanOnlyCallback.onStateChanged(4);
                                ActiveModeWarden.this.mModeStateMachine.transitionTo(ModeStateMachine.this.mWifiDisabledState);
                                break;
                            } else {
                                Log.d(ActiveModeWarden.TAG, "ScanOnly mode state change from previous manager");
                                return true;
                            }
                        default:
                            return false;
                    }
                } else {
                    Log.d(ActiveModeWarden.TAG, "Received CMD_START_SCAN_ONLY_MODE when active - drop");
                }
                return true;
            }
        }
    }

    private class SoftApCallbackImpl extends ModeCallback implements WifiManager.SoftApCallback {
        private int mMode;

        private SoftApCallbackImpl(int mode) {
            super();
            this.mMode = mode;
        }

        public void onStateChanged(int state, int reason) {
            if (state == 11) {
                ActiveModeWarden.this.mActiveModeManagers.remove(getActiveModeManager());
                ActiveModeWarden.this.updateBatteryStatsWifiState(false);
            } else if (state == 14) {
                ActiveModeWarden.this.mActiveModeManagers.remove(getActiveModeManager());
                ActiveModeWarden.this.updateBatteryStatsWifiState(false);
            }
            if (ActiveModeWarden.this.mSoftApCallback != null && this.mMode == 1) {
                ActiveModeWarden.this.mSoftApCallback.onStateChanged(state, reason);
            }
        }

        public void onNumClientsChanged(int numClients) {
            if (ActiveModeWarden.this.mSoftApCallback == null) {
                Log.d(ActiveModeWarden.TAG, "SoftApCallback is null. Dropping NumClientsChanged event.");
            } else if (this.mMode == 1) {
                ActiveModeWarden.this.mSoftApCallback.onNumClientsChanged(numClients);
            }
        }

        public void onStaConnected(String Macaddr, int numClients) {
            if (ActiveModeWarden.this.mSoftApCallback != null) {
                ActiveModeWarden.this.mSoftApCallback.onStaConnected(Macaddr, numClients);
            } else {
                Log.d(ActiveModeWarden.TAG, "SoftApCallback is null. Dropping onStaConnected event.");
            }
        }

        public void onStaDisconnected(String Macaddr, int numClients) {
            if (ActiveModeWarden.this.mSoftApCallback != null) {
                ActiveModeWarden.this.mSoftApCallback.onStaDisconnected(Macaddr, numClients);
            } else {
                Log.d(ActiveModeWarden.TAG, "SoftApCallback is null. Dropping onStaDisconnected event.");
            }
        }
    }

    private void startSoftAp(SoftApModeConfiguration softapConfig) {
        Log.d(TAG, "Starting SoftApModeManager");
        WifiConfiguration config = softapConfig.getWifiConfiguration();
        if (config != null && config.SSID != null) {
            Log.d(TAG, "Passing config to SoftApManager! " + config);
        }
        SoftApCallbackImpl callback = new SoftApCallbackImpl(softapConfig.getTargetMode());
        ActiveModeManager manager = this.mWifiInjector.makeSoftApManager(callback, softapConfig);
        callback.setActiveModeManager(manager);
        manager.start();
        this.mActiveModeManagers.add(manager);
        updateBatteryStatsWifiState(true);
    }

    /* access modifiers changed from: private */
    public void updateBatteryStatsWifiState(boolean enabled) {
        if (enabled) {
            try {
                if (this.mActiveModeManagers.size() == 1) {
                    this.mBatteryStats.noteWifiOn();
                }
            } catch (RemoteException e) {
                Log.e(TAG, "Failed to note battery stats in wifi");
            }
        } else if (this.mActiveModeManagers.size() == 0) {
            this.mBatteryStats.noteWifiOff();
        }
    }

    /* access modifiers changed from: private */
    public void updateBatteryStatsScanModeActive() {
        try {
            this.mBatteryStats.noteWifiState(1, (String) null);
        } catch (RemoteException e) {
            Log.e(TAG, "Failed to note battery stats in wifi");
        }
    }

    private final class WifiNativeStatusListener implements WifiNative.StatusListener {
        private WifiNativeStatusListener() {
        }

        public void onStatusChanged(boolean isReady) {
            if (!isReady) {
                ActiveModeWarden.this.mHandler.post(new Runnable() {
                    public final void run() {
                        ActiveModeWarden.WifiNativeStatusListener.this.lambda$onStatusChanged$0$ActiveModeWarden$WifiNativeStatusListener();
                    }
                });
            }
        }

        public /* synthetic */ void lambda$onStatusChanged$0$ActiveModeWarden$WifiNativeStatusListener() {
            Log.e(ActiveModeWarden.TAG, "One of the native daemons died. Triggering recovery");
            ActiveModeWarden.this.mWifiDiagnostics.captureBugReportData(8);
            ActiveModeWarden.this.mWifiInjector.getSelfRecovery().trigger(1);
        }
    }
}
