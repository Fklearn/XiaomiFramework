package com.android.server.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.view.MiuiWindowManager;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import com.android.server.wifi.ClientModeManager;
import com.android.server.wifi.ScanOnlyModeManager;
import com.android.server.wifi.WifiController;
import com.android.server.wifi.util.WifiPermissionsUtil;
import miui.telephony.phonenumber.Prefix;

public class WifiController extends StateMachine {
    private static final int BASE = 155648;
    static final int CMD_AIRPLANE_TOGGLED = 155657;
    static final int CMD_AP_START_FAILURE = 155661;
    static final int CMD_AP_STOPPED = 155663;
    static final int CMD_DEFERRED_RECOVERY_RESTART_WIFI = 155670;
    static final int CMD_DEFERRED_TOGGLE = 155659;
    static final int CMD_DELAY_DISCONNECT = 155678;
    static final int CMD_EMERGENCY_CALL_STATE_CHANGED = 155662;
    static final int CMD_EMERGENCY_MODE_CHANGED = 155649;
    static final int CMD_RECOVERY_DISABLE_WIFI = 155667;
    static final int CMD_RECOVERY_RESTART_WIFI = 155665;
    private static final int CMD_RECOVERY_RESTART_WIFI_CONTINUE = 155666;
    static final int CMD_SCANNING_STOPPED = 155669;
    static final int CMD_SCAN_ALWAYS_MODE_CHANGED = 155655;
    static final int CMD_SET_AP = 155658;
    static final int CMD_STA_START_FAILURE = 155664;
    static final int CMD_STA_STOPPED = 155668;
    static final int CMD_WIFI_TOGGLED = 155656;
    private static final boolean DBG = false;
    private static final long DEFAULT_REENABLE_DELAY_MS = 500;
    private static final long DEFER_MARGIN_MS = 5;
    private static final int MAX_RECOVERY_TIMEOUT_DELAY_MS = 4000;
    private static final String TAG = "WifiController";
    /* access modifiers changed from: private */
    public final ActiveModeWarden mActiveModeWarden;
    private ClientModeManager.Listener mClientModeCallback = new ClientModeCallback();
    /* access modifiers changed from: private */
    public final ClientModeImpl mClientModeImpl;
    /* access modifiers changed from: private */
    public final Looper mClientModeImplLooper;
    /* access modifiers changed from: private */
    public Context mContext;
    private DefaultState mDefaultState = new DefaultState();
    /* access modifiers changed from: private */
    public EcmState mEcmState = new EcmState();
    /* access modifiers changed from: private */
    public final FrameworkFacade mFacade;
    private boolean mFirstUserSignOnSeen = false;
    /* access modifiers changed from: private */
    public QcStaDisablingState mQcStaDisablingState = new QcStaDisablingState();
    /* access modifiers changed from: private */
    public long mReEnableDelayMillis;
    /* access modifiers changed from: private */
    public int mRecoveryDelayMillis;
    private ScanOnlyModeManager.Listener mScanOnlyModeCallback = new ScanOnlyCallback();
    /* access modifiers changed from: private */
    public final WifiSettingsStore mSettingsStore;
    /* access modifiers changed from: private */
    public StaDisabledState mStaDisabledState = new StaDisabledState();
    /* access modifiers changed from: private */
    public StaDisabledWithScanState mStaDisabledWithScanState = new StaDisabledWithScanState();
    /* access modifiers changed from: private */
    public StaEnabledState mStaEnabledState = new StaEnabledState();
    /* access modifiers changed from: private */
    public final WifiApConfigStore mWifiApConfigStore;
    /* access modifiers changed from: private */
    public boolean mWifiControllerReady = false;
    private final WifiPermissionsUtil mWifiPermissionsUtil;

    WifiController(Context context, ClientModeImpl clientModeImpl, Looper clientModeImplLooper, WifiSettingsStore wss, Looper wifiServiceLooper, FrameworkFacade f, ActiveModeWarden amw, WifiPermissionsUtil wifiPermissionsUtil) {
        super(TAG, wifiServiceLooper);
        this.mFacade = f;
        this.mContext = context;
        this.mClientModeImpl = clientModeImpl;
        this.mClientModeImplLooper = clientModeImplLooper;
        this.mActiveModeWarden = amw;
        this.mSettingsStore = wss;
        this.mWifiPermissionsUtil = wifiPermissionsUtil;
        this.mWifiControllerReady = false;
        this.mWifiApConfigStore = WifiInjector.getInstance().getWifiApConfigStore();
        addState(this.mDefaultState);
        addState(this.mStaDisabledState, this.mDefaultState);
        addState(this.mStaEnabledState, this.mDefaultState);
        addState(this.mStaDisabledWithScanState, this.mDefaultState);
        addState(this.mEcmState, this.mDefaultState);
        addState(this.mQcStaDisablingState, this.mDefaultState);
        setLogRecSize(100);
        setLogOnlyTransitions(false);
        this.mActiveModeWarden.registerScanOnlyCallback(this.mScanOnlyModeCallback);
        this.mActiveModeWarden.registerClientModeCallback(this.mClientModeCallback);
        readWifiReEnableDelay();
        readWifiRecoveryDelay();
    }

    public void start() {
        boolean isAirplaneModeOn = this.mSettingsStore.isAirplaneModeOn();
        boolean isWifiEnabled = this.mSettingsStore.isWifiToggleEnabled();
        boolean isScanningAlwaysAvailable = this.mSettingsStore.isScanAlwaysAvailable();
        boolean isLocationModeActive = this.mWifiPermissionsUtil.isLocationModeEnabled();
        log("isAirplaneModeOn = " + isAirplaneModeOn + ", isWifiEnabled = " + isWifiEnabled + ", isScanningAvailable = " + isScanningAlwaysAvailable + ", isLocationModeActive = " + isLocationModeActive);
        if (checkScanOnlyModeAvailable()) {
            setInitialState(this.mStaDisabledWithScanState);
        } else {
            setInitialState(this.mStaDisabledState);
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.wifi.WIFI_AP_STATE_CHANGED");
        filter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filter.addAction("android.location.MODE_CHANGED");
        this.mContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("android.net.wifi.WIFI_AP_STATE_CHANGED")) {
                    int state = intent.getIntExtra("wifi_state", 14);
                    if (state == 14) {
                        Log.e(WifiController.TAG, "SoftAP start failed");
                        WifiController.this.sendMessage(WifiController.CMD_AP_START_FAILURE);
                    } else if (state == 11) {
                        WifiController.this.sendMessage(WifiController.CMD_AP_STOPPED);
                    }
                } else if (action.equals("android.location.MODE_CHANGED") && WifiController.this.mWifiControllerReady) {
                    WifiController.this.sendMessage(WifiController.CMD_SCAN_ALWAYS_MODE_CHANGED);
                }
            }
        }, new IntentFilter(filter));
        WifiController.super.start();
    }

    /* access modifiers changed from: private */
    public boolean checkScanOnlyModeAvailable() {
        if (!this.mWifiPermissionsUtil.isLocationModeEnabled()) {
            return false;
        }
        return this.mSettingsStore.isScanAlwaysAvailable();
    }

    private class ScanOnlyCallback implements ScanOnlyModeManager.Listener {
        private ScanOnlyCallback() {
        }

        public void onStateChanged(int state) {
            if (state == 4) {
                Log.d(WifiController.TAG, "ScanOnlyMode unexpected failure: state unknown");
            } else if (state == 1) {
                Log.d(WifiController.TAG, "ScanOnlyMode stopped");
                WifiController.this.sendMessage(WifiController.CMD_SCANNING_STOPPED);
            } else if (state == 3) {
                Log.d(WifiController.TAG, "scan mode active");
            } else {
                Log.d(WifiController.TAG, "unexpected state update: " + state);
            }
        }
    }

    private class ClientModeCallback implements ClientModeManager.Listener {
        private ClientModeCallback() {
        }

        public void onStateChanged(int state) {
            if (state == 4) {
                WifiController.this.logd("ClientMode unexpected failure: state unknown");
                WifiController.this.sendMessage(WifiController.CMD_STA_START_FAILURE);
            } else if (state == 1) {
                WifiController.this.logd("ClientMode stopped");
                WifiController.this.sendMessage(WifiController.CMD_STA_STOPPED);
            } else if (state == 3) {
                WifiController.this.logd("client mode active");
            } else {
                WifiController wifiController = WifiController.this;
                wifiController.logd("unexpected state update: " + state);
            }
        }
    }

    private void readWifiReEnableDelay() {
        this.mReEnableDelayMillis = this.mFacade.getLongSetting(this.mContext, "wifi_reenable_delay", DEFAULT_REENABLE_DELAY_MS);
    }

    private void readWifiRecoveryDelay() {
        this.mRecoveryDelayMillis = this.mContext.getResources().getInteger(17694941);
        if (this.mRecoveryDelayMillis > MAX_RECOVERY_TIMEOUT_DELAY_MS) {
            this.mRecoveryDelayMillis = MAX_RECOVERY_TIMEOUT_DELAY_MS;
            Log.w(TAG, "Overriding timeout delay with maximum limit value");
        }
    }

    class DefaultState extends State {
        DefaultState() {
        }

        public void enter() {
            boolean unused = WifiController.this.mWifiControllerReady = true;
        }

        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0071, code lost:
            com.android.server.wifi.WifiController.access$1900(r4.this$0, "SoftAp mode disabled, determine next state");
         */
        /* JADX WARNING: Code restructure failed: missing block: B:12:0x0082, code lost:
            if (com.android.server.wifi.WifiController.access$1200(r4.this$0).isWifiToggleEnabled() == false) goto L_0x008f;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0084, code lost:
            r0 = r4.this$0;
            r0.transitionTo(com.android.server.wifi.WifiController.access$1500(r0));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0095, code lost:
            if (com.android.server.wifi.WifiController.access$1600(r4.this$0) == false) goto L_0x014a;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0097, code lost:
            r0 = r4.this$0;
            r0.transitionTo(com.android.server.wifi.WifiController.access$1700(r0));
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean processMessage(android.os.Message r5) {
            /*
                r4 = this;
                int r0 = r5.what
                r1 = 155649(0x26001, float:2.18111E-40)
                r2 = 1
                if (r0 == r1) goto L_0x013d
                r1 = 155678(0x2601e, float:2.18151E-40)
                if (r0 == r1) goto L_0x013c
                switch(r0) {
                    case 155655: goto L_0x013c;
                    case 155656: goto L_0x013c;
                    case 155657: goto L_0x00e7;
                    case 155658: goto L_0x00ab;
                    case 155659: goto L_0x00a2;
                    default: goto L_0x0010;
                }
            L_0x0010:
                switch(r0) {
                    case 155661: goto L_0x0067;
                    case 155662: goto L_0x013d;
                    case 155663: goto L_0x0071;
                    case 155664: goto L_0x013c;
                    case 155665: goto L_0x0047;
                    case 155666: goto L_0x013c;
                    case 155667: goto L_0x002c;
                    case 155668: goto L_0x013c;
                    case 155669: goto L_0x013c;
                    case 155670: goto L_0x013c;
                    default: goto L_0x0013;
                }
            L_0x0013:
                java.lang.RuntimeException r0 = new java.lang.RuntimeException
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "WifiController.handleMessage "
                r1.append(r2)
                int r2 = r5.what
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                r0.<init>(r1)
                throw r0
            L_0x002c:
                com.android.server.wifi.WifiController r0 = com.android.server.wifi.WifiController.this
                java.lang.String r1 = "Recovery has been throttled, disable wifi"
                r0.log(r1)
                com.android.server.wifi.WifiController r0 = com.android.server.wifi.WifiController.this
                com.android.server.wifi.ActiveModeWarden r0 = r0.mActiveModeWarden
                r0.shutdownWifi()
                com.android.server.wifi.WifiController r0 = com.android.server.wifi.WifiController.this
                com.android.server.wifi.WifiController$StaDisabledState r1 = r0.mStaDisabledState
                r0.transitionTo(r1)
                goto L_0x014a
            L_0x0047:
                com.android.server.wifi.WifiController r0 = com.android.server.wifi.WifiController.this
                r1 = 155670(0x26016, float:2.1814E-40)
                android.os.Message r1 = r0.obtainMessage(r1)
                r0.deferMessage(r1)
                com.android.server.wifi.WifiController r0 = com.android.server.wifi.WifiController.this
                com.android.server.wifi.ActiveModeWarden r0 = r0.mActiveModeWarden
                r0.shutdownWifi()
                com.android.server.wifi.WifiController r0 = com.android.server.wifi.WifiController.this
                com.android.server.wifi.WifiController$StaDisabledState r1 = r0.mStaDisabledState
                r0.transitionTo(r1)
                goto L_0x014a
            L_0x0067:
                com.android.server.wifi.WifiController r0 = com.android.server.wifi.WifiController.this
                com.android.server.wifi.ActiveModeWarden r0 = r0.mActiveModeWarden
                r1 = -1
                r0.stopSoftAPMode(r1)
            L_0x0071:
                com.android.server.wifi.WifiController r0 = com.android.server.wifi.WifiController.this
                java.lang.String r1 = "SoftAp mode disabled, determine next state"
                r0.log(r1)
                com.android.server.wifi.WifiController r0 = com.android.server.wifi.WifiController.this
                com.android.server.wifi.WifiSettingsStore r0 = r0.mSettingsStore
                boolean r0 = r0.isWifiToggleEnabled()
                if (r0 == 0) goto L_0x008f
                com.android.server.wifi.WifiController r0 = com.android.server.wifi.WifiController.this
                com.android.server.wifi.WifiController$StaEnabledState r1 = r0.mStaEnabledState
                r0.transitionTo(r1)
                goto L_0x014a
            L_0x008f:
                com.android.server.wifi.WifiController r0 = com.android.server.wifi.WifiController.this
                boolean r0 = r0.checkScanOnlyModeAvailable()
                if (r0 == 0) goto L_0x014a
                com.android.server.wifi.WifiController r0 = com.android.server.wifi.WifiController.this
                com.android.server.wifi.WifiController$StaDisabledWithScanState r1 = r0.mStaDisabledWithScanState
                r0.transitionTo(r1)
                goto L_0x014a
            L_0x00a2:
                com.android.server.wifi.WifiController r0 = com.android.server.wifi.WifiController.this
                java.lang.String r1 = "DEFERRED_TOGGLE ignored due to state change"
                r0.log(r1)
                goto L_0x014a
            L_0x00ab:
                int r0 = r5.arg1
                if (r0 != r2) goto L_0x00c4
                com.android.server.wifi.WifiController r0 = com.android.server.wifi.WifiController.this
                com.android.server.wifi.WifiApConfigStore r0 = r0.mWifiApConfigStore
                boolean r0 = r0.getDualSapStatus()
                if (r0 == 0) goto L_0x00c4
                com.android.server.wifi.WifiController r0 = com.android.server.wifi.WifiController.this
                com.android.server.wifi.WifiController$StaDisabledState r1 = r0.mStaDisabledState
                r0.transitionTo(r1)
            L_0x00c4:
                int r0 = r5.arg1
                if (r0 != r2) goto L_0x00db
                java.lang.Object r0 = r5.obj
                com.android.server.wifi.SoftApModeConfiguration r0 = (com.android.server.wifi.SoftApModeConfiguration) r0
                com.android.server.wifi.WifiController r1 = com.android.server.wifi.WifiController.this
                com.android.server.wifi.ActiveModeWarden r1 = r1.mActiveModeWarden
                java.lang.Object r3 = r5.obj
                com.android.server.wifi.SoftApModeConfiguration r3 = (com.android.server.wifi.SoftApModeConfiguration) r3
                r1.enterSoftAPMode(r3)
                goto L_0x014a
            L_0x00db:
                com.android.server.wifi.WifiController r0 = com.android.server.wifi.WifiController.this
                com.android.server.wifi.ActiveModeWarden r0 = r0.mActiveModeWarden
                int r1 = r5.arg2
                r0.stopSoftAPMode(r1)
                goto L_0x014a
            L_0x00e7:
                com.android.server.wifi.WifiController r0 = com.android.server.wifi.WifiController.this
                com.android.server.wifi.WifiSettingsStore r0 = r0.mSettingsStore
                boolean r0 = r0.isAirplaneModeOn()
                if (r0 == 0) goto L_0x010d
                com.android.server.wifi.WifiController r0 = com.android.server.wifi.WifiController.this
                java.lang.String r1 = "Airplane mode toggled, shutdown all modes"
                r0.log(r1)
                com.android.server.wifi.WifiController r0 = com.android.server.wifi.WifiController.this
                com.android.server.wifi.ActiveModeWarden r0 = r0.mActiveModeWarden
                r0.shutdownWifi()
                com.android.server.wifi.WifiController r0 = com.android.server.wifi.WifiController.this
                com.android.server.wifi.WifiController$StaDisabledState r1 = r0.mStaDisabledState
                r0.transitionTo(r1)
                goto L_0x014a
            L_0x010d:
                com.android.server.wifi.WifiController r0 = com.android.server.wifi.WifiController.this
                java.lang.String r1 = "Airplane mode disabled, determine next state"
                r0.log(r1)
                com.android.server.wifi.WifiController r0 = com.android.server.wifi.WifiController.this
                com.android.server.wifi.WifiSettingsStore r0 = r0.mSettingsStore
                boolean r0 = r0.isWifiToggleEnabled()
                if (r0 == 0) goto L_0x012a
                com.android.server.wifi.WifiController r0 = com.android.server.wifi.WifiController.this
                com.android.server.wifi.WifiController$StaEnabledState r1 = r0.mStaEnabledState
                r0.transitionTo(r1)
                goto L_0x014a
            L_0x012a:
                com.android.server.wifi.WifiController r0 = com.android.server.wifi.WifiController.this
                boolean r0 = r0.checkScanOnlyModeAvailable()
                if (r0 == 0) goto L_0x014a
                com.android.server.wifi.WifiController r0 = com.android.server.wifi.WifiController.this
                com.android.server.wifi.WifiController$StaDisabledWithScanState r1 = r0.mStaDisabledWithScanState
                r0.transitionTo(r1)
                goto L_0x014a
            L_0x013c:
                goto L_0x014a
            L_0x013d:
                int r0 = r5.arg1
                if (r0 != r2) goto L_0x014a
                com.android.server.wifi.WifiController r0 = com.android.server.wifi.WifiController.this
                com.android.server.wifi.WifiController$EcmState r1 = r0.mEcmState
                r0.transitionTo(r1)
            L_0x014a:
                return r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiController.DefaultState.processMessage(android.os.Message):boolean");
        }
    }

    class StaDisabledState extends State {
        private int mDeferredEnableSerialNumber = 0;
        private long mDisabledTimestamp;
        private boolean mHaveDeferredEnable = false;

        StaDisabledState() {
        }

        public void enter() {
            WifiController.this.mActiveModeWarden.disableWifi();
            this.mDisabledTimestamp = SystemClock.elapsedRealtime();
            this.mDeferredEnableSerialNumber++;
            this.mHaveDeferredEnable = false;
            boolean unused = WifiController.this.mWifiControllerReady = true;
        }

        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case WifiController.CMD_SCAN_ALWAYS_MODE_CHANGED /*155655*/:
                    if (WifiController.this.checkScanOnlyModeAvailable()) {
                        WifiController wifiController = WifiController.this;
                        wifiController.transitionTo(wifiController.mStaDisabledWithScanState);
                        break;
                    }
                    break;
                case WifiController.CMD_WIFI_TOGGLED /*155656*/:
                    if (!WifiController.this.mSettingsStore.isWifiToggleEnabled()) {
                        if (WifiController.this.checkScanOnlyModeAvailable() && WifiController.this.mSettingsStore.isAirplaneModeOn()) {
                            WifiController wifiController2 = WifiController.this;
                            wifiController2.transitionTo(wifiController2.mStaDisabledWithScanState);
                            break;
                        }
                    } else if (!doDeferEnable(msg)) {
                        WifiController wifiController3 = WifiController.this;
                        wifiController3.transitionTo(wifiController3.mStaEnabledState);
                        break;
                    } else {
                        if (this.mHaveDeferredEnable) {
                            this.mDeferredEnableSerialNumber++;
                        }
                        this.mHaveDeferredEnable = !this.mHaveDeferredEnable;
                        break;
                    }
                case WifiController.CMD_SET_AP /*155658*/:
                    if (msg.arg1 == 1) {
                        WifiController.this.mSettingsStore.setWifiSavedState(0);
                    }
                    return false;
                case WifiController.CMD_DEFERRED_TOGGLE /*155659*/:
                    if (msg.arg1 == this.mDeferredEnableSerialNumber) {
                        WifiController.this.log("DEFERRED_TOGGLE handled");
                        WifiController.this.sendMessage((Message) msg.obj);
                        break;
                    } else {
                        WifiController.this.log("DEFERRED_TOGGLE ignored due to serial mismatch");
                        break;
                    }
                case WifiController.CMD_RECOVERY_RESTART_WIFI_CONTINUE /*155666*/:
                    if (!WifiController.this.mSettingsStore.isWifiToggleEnabled()) {
                        if (WifiController.this.checkScanOnlyModeAvailable()) {
                            WifiController wifiController4 = WifiController.this;
                            wifiController4.transitionTo(wifiController4.mStaDisabledWithScanState);
                            break;
                        }
                    } else {
                        WifiController wifiController5 = WifiController.this;
                        wifiController5.transitionTo(wifiController5.mStaEnabledState);
                        break;
                    }
                    break;
                case WifiController.CMD_DEFERRED_RECOVERY_RESTART_WIFI /*155670*/:
                    WifiController wifiController6 = WifiController.this;
                    wifiController6.sendMessageDelayed(WifiController.CMD_RECOVERY_RESTART_WIFI_CONTINUE, (long) wifiController6.mRecoveryDelayMillis);
                    break;
                default:
                    return false;
            }
            return true;
        }

        private boolean doDeferEnable(Message msg) {
            long delaySoFar = SystemClock.elapsedRealtime() - this.mDisabledTimestamp;
            if (delaySoFar >= WifiController.this.mReEnableDelayMillis) {
                return false;
            }
            WifiController wifiController = WifiController.this;
            wifiController.log("WifiController msg " + msg + " deferred for " + (WifiController.this.mReEnableDelayMillis - delaySoFar) + "ms");
            Message deferredMsg = WifiController.this.obtainMessage(WifiController.CMD_DEFERRED_TOGGLE);
            deferredMsg.obj = Message.obtain(msg);
            int i = this.mDeferredEnableSerialNumber + 1;
            this.mDeferredEnableSerialNumber = i;
            deferredMsg.arg1 = i;
            WifiController wifiController2 = WifiController.this;
            wifiController2.sendMessageDelayed(deferredMsg, (wifiController2.mReEnableDelayMillis - delaySoFar) + WifiController.DEFER_MARGIN_MS);
            return true;
        }
    }

    class StaEnabledState extends State {
        private final BroadcastReceiver br = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                WifiController.this.log("delayed disconnect cancelled. disconnecting...");
                if (WifiController.this.hasMessages(WifiController.CMD_DELAY_DISCONNECT)) {
                    WifiController.this.removeMessages(WifiController.CMD_DELAY_DISCONNECT);
                    WifiController.this.sendMessage(WifiController.CMD_DELAY_DISCONNECT);
                }
            }
        };

        StaEnabledState() {
        }

        private boolean checkAndHandleDelayDisconnectDuration() {
            int delay = Settings.Secure.getInt(WifiController.this.mContext.getContentResolver(), "wifi_disconnect_delay_duration", 0);
            if (delay > 0) {
                WifiController wifiController = WifiController.this;
                wifiController.log("DISCONNECT_DELAY_DURATION set. Delaying disconnection by: " + delay + " seconds");
                Intent intent = new Intent("com.qualcomm.qti.net.wifi.WIFI_DISCONNECT_IN_PROGRESS");
                intent.addFlags(MiuiWindowManager.LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
                WifiController.this.mContext.sendOrderedBroadcastAsUser(intent, UserHandle.ALL, (String) null, this.br, WifiController.this.mClientModeImpl.getHandler(), 0, (String) null, (Bundle) null);
                WifiController wifiController2 = WifiController.this;
                wifiController2.sendMessageDelayed(wifiController2.obtainMessage(WifiController.CMD_DELAY_DISCONNECT), (long) (delay * 1000));
                WifiController wifiController3 = WifiController.this;
                wifiController3.transitionTo(wifiController3.mQcStaDisablingState);
            }
            if (delay > 0) {
                return true;
            }
            return false;
        }

        public void enter() {
            WifiController.this.log("StaEnabledState.enter()");
            WifiController.this.mActiveModeWarden.enterClientMode();
        }

        public boolean processMessage(Message msg) {
            String bugTitle;
            String bugDetail;
            switch (msg.what) {
                case WifiController.CMD_WIFI_TOGGLED /*155656*/:
                    if (!WifiController.this.mSettingsStore.isWifiToggleEnabled() && !checkAndHandleDelayDisconnectDuration()) {
                        if (!WifiController.this.checkScanOnlyModeAvailable()) {
                            WifiController wifiController = WifiController.this;
                            wifiController.transitionTo(wifiController.mStaDisabledState);
                            break;
                        } else {
                            WifiController wifiController2 = WifiController.this;
                            wifiController2.transitionTo(wifiController2.mStaDisabledWithScanState);
                            break;
                        }
                    }
                case WifiController.CMD_AIRPLANE_TOGGLED /*155657*/:
                    if (!WifiController.this.mSettingsStore.isAirplaneModeOn()) {
                        WifiController.this.log("airplane mode toggled - and airplane mode is off.  return handled");
                        return true;
                    } else if (!checkAndHandleDelayDisconnectDuration()) {
                        return false;
                    } else {
                        WifiController.this.deferMessage(msg);
                        return true;
                    }
                case WifiController.CMD_SET_AP /*155658*/:
                    if (msg.arg1 == 1) {
                        WifiController.this.mSettingsStore.setWifiSavedState(1);
                    }
                    return false;
                case WifiController.CMD_AP_START_FAILURE /*155661*/:
                    WifiController.this.mActiveModeWarden.stopSoftAPMode(-1);
                    break;
                case WifiController.CMD_AP_STOPPED /*155663*/:
                    break;
                case WifiController.CMD_STA_START_FAILURE /*155664*/:
                    if (WifiController.this.checkScanOnlyModeAvailable()) {
                        WifiController wifiController3 = WifiController.this;
                        wifiController3.transitionTo(wifiController3.mStaDisabledWithScanState);
                        break;
                    } else {
                        WifiController wifiController4 = WifiController.this;
                        wifiController4.transitionTo(wifiController4.mStaDisabledState);
                        break;
                    }
                case WifiController.CMD_RECOVERY_RESTART_WIFI /*155665*/:
                    if (msg.arg1 >= SelfRecovery.REASON_STRINGS.length || msg.arg1 < 0) {
                        bugDetail = Prefix.EMPTY;
                        bugTitle = "Wi-Fi BugReport";
                    } else {
                        bugDetail = SelfRecovery.REASON_STRINGS[msg.arg1];
                        bugTitle = "Wi-Fi BugReport: " + bugDetail;
                    }
                    if (msg.arg1 != 0) {
                        new Handler(WifiController.this.mClientModeImplLooper).post(new Runnable(bugTitle, bugDetail) {
                            private final /* synthetic */ String f$1;
                            private final /* synthetic */ String f$2;

                            {
                                this.f$1 = r2;
                                this.f$2 = r3;
                            }

                            public final void run() {
                                WifiController.StaEnabledState.this.lambda$processMessage$0$WifiController$StaEnabledState(this.f$1, this.f$2);
                            }
                        });
                    }
                    return false;
                case WifiController.CMD_STA_STOPPED /*155668*/:
                    WifiController wifiController5 = WifiController.this;
                    wifiController5.transitionTo(wifiController5.mStaDisabledState);
                    break;
                default:
                    return false;
            }
            return true;
        }

        public /* synthetic */ void lambda$processMessage$0$WifiController$StaEnabledState(String bugTitle, String bugDetail) {
            WifiController.this.mClientModeImpl.takeBugReport(bugTitle, bugDetail);
        }
    }

    class StaDisabledWithScanState extends State {
        private int mDeferredEnableSerialNumber = 0;
        private long mDisabledTimestamp;
        private boolean mHaveDeferredEnable = false;

        StaDisabledWithScanState() {
        }

        public void enter() {
            WifiController.this.mActiveModeWarden.enterScanOnlyMode();
            this.mDisabledTimestamp = SystemClock.elapsedRealtime();
            this.mDeferredEnableSerialNumber++;
            this.mHaveDeferredEnable = false;
            boolean unused = WifiController.this.mWifiControllerReady = true;
        }

        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case WifiController.CMD_SCAN_ALWAYS_MODE_CHANGED /*155655*/:
                    if (!WifiController.this.checkScanOnlyModeAvailable()) {
                        WifiController.this.log("StaDisabledWithScanState: scan no longer available");
                        WifiController wifiController = WifiController.this;
                        wifiController.transitionTo(wifiController.mStaDisabledState);
                        break;
                    }
                    break;
                case WifiController.CMD_WIFI_TOGGLED /*155656*/:
                    if (WifiController.this.mSettingsStore.isWifiToggleEnabled()) {
                        if (!doDeferEnable(msg)) {
                            WifiController wifiController2 = WifiController.this;
                            wifiController2.transitionTo(wifiController2.mStaEnabledState);
                            break;
                        } else {
                            if (this.mHaveDeferredEnable) {
                                this.mDeferredEnableSerialNumber++;
                            }
                            this.mHaveDeferredEnable = !this.mHaveDeferredEnable;
                            break;
                        }
                    }
                    break;
                case WifiController.CMD_SET_AP /*155658*/:
                    if (msg.arg1 == 1) {
                        WifiController.this.mSettingsStore.setWifiSavedState(0);
                    }
                    return false;
                case WifiController.CMD_DEFERRED_TOGGLE /*155659*/:
                    if (msg.arg1 == this.mDeferredEnableSerialNumber) {
                        WifiController.this.logd("DEFERRED_TOGGLE handled");
                        WifiController.this.sendMessage((Message) msg.obj);
                        break;
                    } else {
                        WifiController.this.log("DEFERRED_TOGGLE ignored due to serial mismatch");
                        break;
                    }
                case WifiController.CMD_AP_START_FAILURE /*155661*/:
                    WifiController.this.mActiveModeWarden.stopSoftAPMode(-1);
                    break;
                case WifiController.CMD_AP_STOPPED /*155663*/:
                    break;
                case WifiController.CMD_SCANNING_STOPPED /*155669*/:
                    WifiController.this.log("WifiController: SCANNING_STOPPED when in scan mode -> StaDisabled");
                    WifiController wifiController3 = WifiController.this;
                    wifiController3.transitionTo(wifiController3.mStaDisabledState);
                    break;
                default:
                    return false;
            }
            return true;
        }

        private boolean doDeferEnable(Message msg) {
            long delaySoFar = SystemClock.elapsedRealtime() - this.mDisabledTimestamp;
            if (delaySoFar >= WifiController.this.mReEnableDelayMillis) {
                return false;
            }
            WifiController wifiController = WifiController.this;
            wifiController.log("WifiController msg " + msg + " deferred for " + (WifiController.this.mReEnableDelayMillis - delaySoFar) + "ms");
            Message deferredMsg = WifiController.this.obtainMessage(WifiController.CMD_DEFERRED_TOGGLE);
            deferredMsg.obj = Message.obtain(msg);
            int i = this.mDeferredEnableSerialNumber + 1;
            this.mDeferredEnableSerialNumber = i;
            deferredMsg.arg1 = i;
            WifiController wifiController2 = WifiController.this;
            wifiController2.sendMessageDelayed(deferredMsg, (wifiController2.mReEnableDelayMillis - delaySoFar) + WifiController.DEFER_MARGIN_MS);
            return true;
        }
    }

    private State getNextWifiState() {
        if (this.mSettingsStore.getWifiSavedState() == 1) {
            return this.mStaEnabledState;
        }
        if (checkScanOnlyModeAvailable()) {
            return this.mStaDisabledWithScanState;
        }
        return this.mStaDisabledState;
    }

    class EcmState extends State {
        private int mEcmEntryCount;

        EcmState() {
        }

        public void enter() {
            WifiController.this.mActiveModeWarden.stopSoftAPMode(-1);
            boolean configWiFiDisableInECBM = WifiController.this.mFacade.getConfigWiFiDisableInECBM(WifiController.this.mContext);
            WifiController wifiController = WifiController.this;
            wifiController.log("WifiController msg getConfigWiFiDisableInECBM " + configWiFiDisableInECBM);
            if (configWiFiDisableInECBM) {
                WifiController.this.mActiveModeWarden.shutdownWifi();
            }
            this.mEcmEntryCount = 1;
        }

        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case WifiController.CMD_EMERGENCY_MODE_CHANGED /*155649*/:
                    if (msg.arg1 == 1) {
                        this.mEcmEntryCount++;
                    } else if (msg.arg1 == 0) {
                        decrementCountAndReturnToAppropriateState();
                    }
                    return true;
                case WifiController.CMD_SET_AP /*155658*/:
                    return true;
                case WifiController.CMD_EMERGENCY_CALL_STATE_CHANGED /*155662*/:
                    if (msg.arg1 == 1) {
                        this.mEcmEntryCount++;
                    } else if (msg.arg1 == 0) {
                        decrementCountAndReturnToAppropriateState();
                    }
                    return true;
                case WifiController.CMD_AP_STOPPED /*155663*/:
                case WifiController.CMD_STA_STOPPED /*155668*/:
                case WifiController.CMD_SCANNING_STOPPED /*155669*/:
                    return true;
                case WifiController.CMD_RECOVERY_RESTART_WIFI /*155665*/:
                case WifiController.CMD_RECOVERY_DISABLE_WIFI /*155667*/:
                    return true;
                default:
                    return false;
            }
        }

        private void decrementCountAndReturnToAppropriateState() {
            boolean exitEcm = false;
            int i = this.mEcmEntryCount;
            if (i == 0) {
                WifiController.this.loge("mEcmEntryCount is 0; exiting Ecm");
                exitEcm = true;
            } else {
                int i2 = i - 1;
                this.mEcmEntryCount = i2;
                if (i2 == 0) {
                    exitEcm = true;
                }
            }
            if (!exitEcm) {
                return;
            }
            if (WifiController.this.mSettingsStore.isWifiToggleEnabled()) {
                WifiController wifiController = WifiController.this;
                wifiController.transitionTo(wifiController.mStaEnabledState);
            } else if (WifiController.this.checkScanOnlyModeAvailable()) {
                WifiController wifiController2 = WifiController.this;
                wifiController2.transitionTo(wifiController2.mStaDisabledWithScanState);
            } else {
                WifiController wifiController3 = WifiController.this;
                wifiController3.transitionTo(wifiController3.mStaDisabledState);
            }
        }
    }

    class QcStaDisablingState extends State {
        QcStaDisablingState() {
        }

        public void enter() {
            WifiController.this.log("QcStaDisablingState.enter()");
        }

        public boolean processMessage(Message msg) {
            switch (msg.what) {
                case WifiController.CMD_WIFI_TOGGLED /*155656*/:
                case WifiController.CMD_AIRPLANE_TOGGLED /*155657*/:
                    WifiController.this.log("In QcStaDisablingState, deferMessage");
                    WifiController.this.deferMessage(msg);
                    return true;
                case WifiController.CMD_DELAY_DISCONNECT /*155678*/:
                    if (WifiController.this.mSettingsStore.isWifiToggleEnabled() || !WifiController.this.checkScanOnlyModeAvailable()) {
                        WifiController wifiController = WifiController.this;
                        wifiController.transitionTo(wifiController.mStaDisabledState);
                        return true;
                    }
                    WifiController wifiController2 = WifiController.this;
                    wifiController2.transitionTo(wifiController2.mStaDisabledWithScanState);
                    return true;
                default:
                    return false;
            }
        }
    }
}
