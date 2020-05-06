package com.android.server.wifi;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MiuiWindowManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.IState;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import com.android.internal.util.WakeupMessage;
import com.android.server.wifi.WifiNative;
import com.android.server.wifi.util.ApConfigUtil;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import miui.telephony.phonenumber.Prefix;

public class SoftApManager implements ActiveModeManager {
    private static final int MIN_SOFT_AP_TIMEOUT_DELAY_MS = 600000;
    @VisibleForTesting
    public static final String SOFT_AP_SEND_MESSAGE_TIMEOUT_TAG = "SoftApManager Soft AP Send Message Timeout";
    private static final String TAG = "SoftApManager";
    /* access modifiers changed from: private */
    public boolean expectedStop = false;
    /* access modifiers changed from: private */
    public WifiConfiguration mApConfig;
    /* access modifiers changed from: private */
    public String mApInterfaceName;
    /* access modifiers changed from: private */
    public final WifiManager.SoftApCallback mCallback;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final String mCountryCode;
    /* access modifiers changed from: private */
    public String mDataInterfaceName;
    /* access modifiers changed from: private */
    public final FrameworkFacade mFrameworkFacade;
    /* access modifiers changed from: private */
    public boolean mIfaceIsDestroyed;
    /* access modifiers changed from: private */
    public boolean mIfaceIsUp;
    /* access modifiers changed from: private */
    public final int mMode;
    /* access modifiers changed from: private */
    public int mNumAssociatedStations = 0;
    /* access modifiers changed from: private */
    public int mQCNumAssociatedStations = 0;
    /* access modifiers changed from: private */
    public int mReportedBandwidth = -1;
    /* access modifiers changed from: private */
    public int mReportedFrequency = -1;
    /* access modifiers changed from: private */
    public final SarManager mSarManager;
    private final WifiNative.SoftApListener mSoftApListener = new WifiNative.SoftApListener() {
        public void onFailure() {
            SoftApManager.this.mStateMachine.sendMessage(2);
        }

        public void onNumAssociatedStationsChanged(int numStations) {
            SoftApManager.this.mStateMachine.sendMessage(4, numStations);
        }

        public void onSoftApChannelSwitched(int frequency, int bandwidth) {
            SoftApManager.this.mStateMachine.sendMessage(9, frequency, bandwidth);
        }

        public void onStaConnected(String Macaddr) {
            SoftApManager.this.mStateMachine.sendMessage(10, Macaddr);
        }

        public void onStaDisconnected(String Macaddr) {
            SoftApManager.this.mStateMachine.sendMessage(11, Macaddr);
        }
    };
    private long mStartTimestamp = -1;
    /* access modifiers changed from: private */
    public final SoftApStateMachine mStateMachine;
    /* access modifiers changed from: private */
    public boolean mTimeoutEnabled = false;
    private final WifiApConfigStore mWifiApConfigStore;
    /* access modifiers changed from: private */
    public final WifiMetrics mWifiMetrics;
    /* access modifiers changed from: private */
    public final WifiNative mWifiNative;
    /* access modifiers changed from: private */
    public String[] mdualApInterfaces;

    static /* synthetic */ int access$2408(SoftApManager x0) {
        int i = x0.mQCNumAssociatedStations;
        x0.mQCNumAssociatedStations = i + 1;
        return i;
    }

    static /* synthetic */ int access$2410(SoftApManager x0) {
        int i = x0.mQCNumAssociatedStations;
        x0.mQCNumAssociatedStations = i - 1;
        return i;
    }

    public SoftApManager(Context context, Looper looper, FrameworkFacade framework, WifiNative wifiNative, String countryCode, WifiManager.SoftApCallback callback, WifiApConfigStore wifiApConfigStore, SoftApModeConfiguration apConfig, WifiMetrics wifiMetrics, SarManager sarManager) {
        this.mContext = context;
        this.mFrameworkFacade = framework;
        this.mWifiNative = wifiNative;
        this.mCountryCode = countryCode;
        this.mCallback = callback;
        this.mWifiApConfigStore = wifiApConfigStore;
        this.mMode = apConfig.getTargetMode();
        WifiConfiguration config = apConfig.getWifiConfiguration();
        if (config == null) {
            this.mApConfig = this.mWifiApConfigStore.getApConfiguration();
        } else {
            this.mApConfig = config;
        }
        this.mWifiMetrics = wifiMetrics;
        this.mSarManager = sarManager;
        this.mdualApInterfaces = new String[2];
        this.mStateMachine = new SoftApStateMachine(looper);
    }

    public void start() {
        this.mStateMachine.sendMessage(0, this.mApConfig);
    }

    public void stop() {
        Log.d(TAG, " currentstate: " + getCurrentStateName());
        this.expectedStop = true;
        if (this.mApInterfaceName != null) {
            if (this.mIfaceIsUp) {
                updateApState(10, 13, 0);
            } else {
                updateApState(10, 12, 0);
            }
        }
        this.mStateMachine.quitNow();
    }

    public int getScanMode() {
        return 0;
    }

    public int getIpMode() {
        return this.mMode;
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.println("--Dump of SoftApManager--");
        pw.println("current StateMachine mode: " + getCurrentStateName());
        pw.println("mApInterfaceName: " + this.mApInterfaceName);
        pw.println("mIfaceIsUp: " + this.mIfaceIsUp);
        pw.println("mMode: " + this.mMode);
        pw.println("mCountryCode: " + this.mCountryCode);
        if (this.mApConfig != null) {
            pw.println("mApConfig.SSID: " + this.mApConfig.SSID);
            pw.println("mApConfig.apBand: " + this.mApConfig.apBand);
            pw.println("mApConfig.hiddenSSID: " + this.mApConfig.hiddenSSID);
        } else {
            pw.println("mApConfig: null");
        }
        pw.println("mNumAssociatedStations: " + this.mNumAssociatedStations);
        pw.println("mTimeoutEnabled: " + this.mTimeoutEnabled);
        pw.println("mReportedFrequency: " + this.mReportedFrequency);
        pw.println("mReportedBandwidth: " + this.mReportedBandwidth);
        pw.println("mStartTimestamp: " + this.mStartTimestamp);
    }

    private String getCurrentStateName() {
        IState currentState = this.mStateMachine.getCurrentState();
        if (currentState != null) {
            return currentState.getName();
        }
        return "StateMachine not active";
    }

    /* access modifiers changed from: private */
    public void updateApState(int newState, int currentState, int reason) {
        this.mCallback.onStateChanged(newState, reason);
        Intent intent = new Intent("android.net.wifi.WIFI_AP_STATE_CHANGED");
        intent.addFlags(MiuiWindowManager.LayoutParams.EXTRA_FLAG_FULLSCREEN_BLURSURFACE);
        intent.putExtra("wifi_state", newState);
        intent.putExtra("previous_wifi_state", currentState);
        if (newState == 14) {
            intent.putExtra("wifi_ap_error_code", reason);
        }
        intent.putExtra("wifi_ap_interface_name", this.mDataInterfaceName);
        intent.putExtra("wifi_ap_mode", this.mMode);
        this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
    }

    /* access modifiers changed from: private */
    public int startSoftAp(WifiConfiguration config) {
        if (config == null || config.SSID == null) {
            Log.e(TAG, "Unable to start soft AP without valid configuration");
            return 2;
        }
        if (TextUtils.isEmpty(this.mCountryCode)) {
            if (config.apBand == 1) {
                Log.e(TAG, "Invalid country code, required for setting up soft ap in 5GHz");
                return 2;
            }
        } else if (!this.mWifiNative.setCountryCodeHal(this.mApInterfaceName, this.mCountryCode.toUpperCase(Locale.ROOT)) && config.apBand == 1) {
            Log.e(TAG, "Failed to set country code, required for setting up soft ap in 5GHz");
            return 2;
        }
        if (!"UTF-8".equals(WifiGbk.getEncoding(config.SSID))) {
            try {
                config.SSID = new String(config.SSID.getBytes(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.e(TAG, "Unable to convert to UTF-8");
            }
        }
        try {
            if (config.SSID == null) {
                Log.e(TAG, "Unable to convert to valid configuration");
                return 2;
            }
            if (config.SSID.getBytes("UTF-8").length > 32) {
                config.SSID = WifiGbk.subStringByU8(config.SSID, 32);
                Log.e(TAG, "Soft AP SSID sub: " + config.SSID);
            }
            WifiConfiguration localConfig = new WifiConfiguration(config);
            int result = ApConfigUtil.updateApChannelConfig(this.mWifiNative, this.mCountryCode, this.mWifiApConfigStore.getAllowed2GChannel(), localConfig);
            if (result != 0) {
                Log.e(TAG, "Failed to update AP band and channel");
                return result;
            }
            if (localConfig.hiddenSSID) {
                Log.d(TAG, "SoftAP is a hidden network");
            }
            if (!this.mWifiNative.startSoftAp(this.mApInterfaceName, localConfig, this.mSoftApListener)) {
                Log.e(TAG, "Soft AP start failed");
                return 2;
            }
            this.mStartTimestamp = SystemClock.elapsedRealtime();
            Log.d(TAG, "Soft AP is started");
            return 0;
        } catch (IOException e2) {
            e2.printStackTrace();
            Log.e(TAG, "Soft AP UnsupportedEncodingException");
        }
    }

    /* access modifiers changed from: private */
    public void stopSoftAp() {
        if (this.mWifiApConfigStore.getDualSapStatus()) {
            this.mWifiNative.teardownInterface(this.mdualApInterfaces[0]);
            this.mWifiNative.teardownInterface(this.mdualApInterfaces[1]);
        }
        this.mWifiNative.teardownInterface(this.mApInterfaceName);
        Log.d(TAG, "Soft AP is stopped");
    }

    private class SoftApStateMachine extends StateMachine {
        public static final int CMD_CONNECTED_STATIONS = 10;
        public static final int CMD_DISCONNECTED_STATIONS = 11;
        public static final int CMD_FAILURE = 2;
        public static final int CMD_INTERFACE_DESTROYED = 7;
        public static final int CMD_INTERFACE_DOWN = 8;
        public static final int CMD_INTERFACE_STATUS_CHANGED = 3;
        public static final int CMD_NO_ASSOCIATED_STATIONS_TIMEOUT = 5;
        public static final int CMD_NUM_ASSOCIATED_STATIONS_CHANGED = 4;
        public static final int CMD_SOFT_AP_CHANNEL_SWITCHED = 9;
        public static final int CMD_START = 0;
        public static final int CMD_TIMEOUT_TOGGLE_CHANGED = 6;
        /* access modifiers changed from: private */
        public final State mIdleState = new IdleState();
        /* access modifiers changed from: private */
        public final State mStartedState = new StartedState();
        private final WifiNative.InterfaceCallback mWifiNativeDualIfaceCallback = new WifiNative.InterfaceCallback() {
            public void onDestroyed(String ifaceName) {
                if (!SoftApManager.this.expectedStop) {
                    Log.e(SoftApManager.TAG, "One of Dual interface (" + ifaceName + ") destroyed. trigger cleanup");
                    if (ifaceName.equals(SoftApManager.this.mdualApInterfaces[0])) {
                        SoftApManager.this.mdualApInterfaces[0] = Prefix.EMPTY;
                    } else if (ifaceName.equals(SoftApManager.this.mdualApInterfaces[1])) {
                        SoftApManager.this.mdualApInterfaces[1] = Prefix.EMPTY;
                    }
                    SoftApManager.this.stop();
                }
            }

            public void onUp(String ifaceName) {
            }

            public void onDown(String ifaceName) {
            }
        };
        /* access modifiers changed from: private */
        public final WifiNative.InterfaceCallback mWifiNativeInterfaceCallback = new WifiNative.InterfaceCallback() {
            public void onDestroyed(String ifaceName) {
                if (SoftApManager.this.mDataInterfaceName != null && SoftApManager.this.mDataInterfaceName.equals(ifaceName)) {
                    SoftApStateMachine.this.sendMessage(7);
                }
            }

            public void onUp(String ifaceName) {
                if (SoftApManager.this.mDataInterfaceName != null && SoftApManager.this.mDataInterfaceName.equals(ifaceName)) {
                    SoftApStateMachine.this.sendMessage(3, 1);
                }
            }

            public void onDown(String ifaceName) {
                if (SoftApManager.this.mDataInterfaceName != null && SoftApManager.this.mDataInterfaceName.equals(ifaceName)) {
                    SoftApStateMachine.this.sendMessage(3, 0);
                }
            }
        };

        private boolean validateDualSapSetupResult(int result) {
            if (result != 0) {
                int failureReason = 0;
                if (result == 1) {
                    failureReason = 1;
                }
                SoftApManager.this.updateApState(14, 12, failureReason);
                SoftApManager.this.stopSoftAp();
                SoftApManager.this.mWifiMetrics.incrementSoftApStartResult(false, failureReason);
                return false;
            }
            WifiNative access$1000 = SoftApManager.this.mWifiNative;
            if (access$1000.setHostapdParams("softap bridge up " + SoftApManager.this.mApInterfaceName)) {
                return true;
            }
            Log.e(SoftApManager.TAG, "Failed to set interface up " + SoftApManager.this.mApInterfaceName);
            return false;
        }

        private boolean setupInterfacesForDualSoftApMode() {
            SoftApManager.this.mdualApInterfaces[0] = SoftApManager.this.mWifiNative.setupInterfaceForSoftApMode(this.mWifiNativeDualIfaceCallback);
            SoftApManager.this.mdualApInterfaces[1] = SoftApManager.this.mWifiNative.setupInterfaceForSoftApMode(this.mWifiNativeDualIfaceCallback);
            String unused = SoftApManager.this.mApInterfaceName = SoftApManager.this.mWifiNative.setupInterfaceForBridgeMode(this.mWifiNativeInterfaceCallback);
            if (TextUtils.isEmpty(SoftApManager.this.mdualApInterfaces[0]) || TextUtils.isEmpty(SoftApManager.this.mdualApInterfaces[1]) || TextUtils.isEmpty(SoftApManager.this.mApInterfaceName)) {
                Log.e(SoftApManager.TAG, "setup failure when creating dual ap interface(s).");
                SoftApManager.this.stopSoftAp();
                SoftApManager.this.updateApState(14, 11, 0);
                SoftApManager.this.mWifiMetrics.incrementSoftApStartResult(false, 0);
                return false;
            }
            SoftApManager softApManager = SoftApManager.this;
            String unused2 = softApManager.mDataInterfaceName = softApManager.mWifiNative.getFstDataInterfaceName();
            if (TextUtils.isEmpty(SoftApManager.this.mDataInterfaceName)) {
                SoftApManager softApManager2 = SoftApManager.this;
                String unused3 = softApManager2.mDataInterfaceName = softApManager2.mApInterfaceName;
            }
            SoftApManager.this.updateApState(12, 11, 0);
            return true;
        }

        /* access modifiers changed from: private */
        public boolean setupForDualBandSoftApMode(WifiConfiguration config) {
            if (!setupInterfacesForDualSoftApMode()) {
                return false;
            }
            WifiConfiguration localConfig = new WifiConfiguration(config);
            String bridgeIfacename = SoftApManager.this.mApInterfaceName;
            SoftApManager softApManager = SoftApManager.this;
            String unused = softApManager.mApInterfaceName = softApManager.mdualApInterfaces[0];
            localConfig.apBand = 0;
            int result = SoftApManager.this.startSoftAp(localConfig);
            if (result == 0) {
                localConfig.apBand = 1;
                SoftApManager softApManager2 = SoftApManager.this;
                String unused2 = softApManager2.mApInterfaceName = softApManager2.mdualApInterfaces[1];
                result = SoftApManager.this.startSoftAp(localConfig);
            }
            String unused3 = SoftApManager.this.mApInterfaceName = bridgeIfacename;
            return validateDualSapSetupResult(result);
        }

        /* access modifiers changed from: private */
        public boolean setupForOweTransitionSoftApMode(WifiConfiguration config) {
            if (!setupInterfacesForDualSoftApMode()) {
                return false;
            }
            WifiConfiguration oweConfig = new WifiConfiguration(config);
            WifiConfiguration openConfig = new WifiConfiguration(config);
            String bridgeIfacename = SoftApManager.this.mApInterfaceName;
            SoftApManager softApManager = SoftApManager.this;
            String unused = softApManager.mApInterfaceName = softApManager.mdualApInterfaces[0];
            oweConfig.oweTransIfaceName = SoftApManager.this.mdualApInterfaces[1];
            oweConfig.SSID = "OWE_" + oweConfig.SSID;
            oweConfig.SSID = oweConfig.SSID.length() > 32 ? oweConfig.SSID.substring(0, 32) : oweConfig.SSID;
            oweConfig.hiddenSSID = true;
            int result = SoftApManager.this.startSoftAp(oweConfig);
            if (result == 0) {
                SoftApManager softApManager2 = SoftApManager.this;
                String unused2 = softApManager2.mApInterfaceName = softApManager2.mdualApInterfaces[1];
                openConfig.oweTransIfaceName = SoftApManager.this.mdualApInterfaces[0];
                openConfig.allowedKeyManagement.clear();
                openConfig.allowedKeyManagement.set(0);
                result = SoftApManager.this.startSoftAp(openConfig);
            }
            String unused3 = SoftApManager.this.mApInterfaceName = bridgeIfacename;
            return validateDualSapSetupResult(result);
        }

        SoftApStateMachine(Looper looper) {
            super(SoftApManager.TAG, looper);
            addState(this.mIdleState);
            addState(this.mStartedState);
            setInitialState(this.mIdleState);
            start();
        }

        private class IdleState extends State {
            private IdleState() {
            }

            public void enter() {
                String unused = SoftApManager.this.mApInterfaceName = null;
                String unused2 = SoftApManager.this.mDataInterfaceName = null;
                boolean unused3 = SoftApManager.this.mIfaceIsUp = false;
                boolean unused4 = SoftApManager.this.mIfaceIsDestroyed = false;
            }

            public boolean processMessage(Message message) {
                if (message.what == 0) {
                    WifiConfiguration config = (WifiConfiguration) message.obj;
                    if (config == null || config.apBand != 2) {
                        if (config == null || config.getAuthType() != 9) {
                            String unused = SoftApManager.this.mApInterfaceName = SoftApManager.this.mWifiNative.setupInterfaceForSoftApMode(SoftApStateMachine.this.mWifiNativeInterfaceCallback);
                            if (TextUtils.isEmpty(SoftApManager.this.mApInterfaceName)) {
                                Log.e(SoftApManager.TAG, "setup failure when creating ap interface.");
                                SoftApManager.this.updateApState(14, 11, 0);
                                SoftApManager.this.mWifiMetrics.incrementSoftApStartResult(false, 0);
                            } else {
                                String unused2 = SoftApManager.this.mDataInterfaceName = SoftApManager.this.mWifiNative.getFstDataInterfaceName();
                                if (TextUtils.isEmpty(SoftApManager.this.mDataInterfaceName)) {
                                    String unused3 = SoftApManager.this.mDataInterfaceName = SoftApManager.this.mApInterfaceName;
                                }
                                SoftApManager.this.updateApState(12, 11, 0);
                                int result = SoftApManager.this.startSoftAp((WifiConfiguration) message.obj);
                                if (result != 0) {
                                    int failureReason = 0;
                                    if (result == 1) {
                                        failureReason = 1;
                                    }
                                    SoftApManager.this.updateApState(14, 12, failureReason);
                                    SoftApManager.this.stopSoftAp();
                                    SoftApManager.this.mWifiMetrics.incrementSoftApStartResult(false, failureReason);
                                } else {
                                    SoftApStateMachine softApStateMachine = SoftApStateMachine.this;
                                    softApStateMachine.transitionTo(softApStateMachine.mStartedState);
                                }
                            }
                        } else if (!SoftApStateMachine.this.setupForOweTransitionSoftApMode(config)) {
                            Log.d(SoftApManager.TAG, "OWE transition sap start failed");
                        } else {
                            SoftApStateMachine softApStateMachine2 = SoftApStateMachine.this;
                            softApStateMachine2.transitionTo(softApStateMachine2.mStartedState);
                        }
                    } else if (!SoftApStateMachine.this.setupForDualBandSoftApMode(config)) {
                        Log.d(SoftApManager.TAG, "Dual band sap start failed");
                    } else {
                        SoftApStateMachine softApStateMachine3 = SoftApStateMachine.this;
                        softApStateMachine3.transitionTo(softApStateMachine3.mStartedState);
                    }
                }
                return true;
            }
        }

        private class StartedState extends State {
            private SoftApTimeoutEnabledSettingObserver mSettingObserver;
            private WakeupMessage mSoftApTimeoutMessage;
            private int mTimeoutDelay;

            private StartedState() {
            }

            private class SoftApTimeoutEnabledSettingObserver extends ContentObserver {
                SoftApTimeoutEnabledSettingObserver(Handler handler) {
                    super(handler);
                }

                public void register() {
                    SoftApManager.this.mFrameworkFacade.registerContentObserver(SoftApManager.this.mContext, Settings.Global.getUriFor("soft_ap_timeout_enabled"), true, this);
                    boolean unused = SoftApManager.this.mTimeoutEnabled = getValue();
                }

                public void unregister() {
                    SoftApManager.this.mFrameworkFacade.unregisterContentObserver(SoftApManager.this.mContext, this);
                }

                public void onChange(boolean selfChange) {
                    super.onChange(selfChange);
                    SoftApManager.this.mStateMachine.sendMessage(6, getValue() ? 1 : 0);
                }

                private boolean getValue() {
                    boolean enabled = true;
                    if (SoftApManager.this.mFrameworkFacade.getIntegerSetting(SoftApManager.this.mContext, "soft_ap_timeout_enabled", 1) != 1) {
                        enabled = false;
                    }
                    return enabled;
                }
            }

            private int getConfigSoftApTimeoutDelay() {
                int delay = SoftApManager.this.mContext.getResources().getInteger(17694947);
                if (delay < SoftApManager.MIN_SOFT_AP_TIMEOUT_DELAY_MS) {
                    delay = SoftApManager.MIN_SOFT_AP_TIMEOUT_DELAY_MS;
                    Log.w(SoftApManager.TAG, "Overriding timeout delay with minimum limit value");
                }
                Log.d(SoftApManager.TAG, "Timeout delay: " + delay);
                return delay;
            }

            private void scheduleTimeoutMessage() {
                if (SoftApManager.this.mTimeoutEnabled) {
                    this.mSoftApTimeoutMessage.schedule(SystemClock.elapsedRealtime() + ((long) this.mTimeoutDelay));
                    Log.d(SoftApManager.TAG, "Timeout message scheduled");
                }
            }

            private void cancelTimeoutMessage() {
                this.mSoftApTimeoutMessage.cancel();
                Log.d(SoftApManager.TAG, "Timeout message canceled");
            }

            private void setNumAssociatedStations(int numStations) {
                if (SoftApManager.this.mNumAssociatedStations != numStations) {
                    int unused = SoftApManager.this.mNumAssociatedStations = numStations;
                    Log.d(SoftApManager.TAG, "Number of associated stations changed: " + SoftApManager.this.mNumAssociatedStations);
                    if (SoftApManager.this.mCallback != null) {
                        SoftApManager.this.mCallback.onNumClientsChanged(SoftApManager.this.mNumAssociatedStations);
                    } else {
                        Log.e(SoftApManager.TAG, "SoftApCallback is null. Dropping NumClientsChanged event.");
                    }
                    SoftApManager.this.mWifiMetrics.addSoftApNumAssociatedStationsChangedEvent(SoftApManager.this.mNumAssociatedStations, SoftApManager.this.mMode);
                }
            }

            private void setConnectedStations(String Macaddr) {
                SoftApManager.access$2408(SoftApManager.this);
                if (SoftApManager.this.mCallback != null) {
                    SoftApManager.this.mCallback.onStaConnected(Macaddr, SoftApManager.this.mQCNumAssociatedStations);
                } else {
                    Log.e(SoftApManager.TAG, "SoftApCallback is null. Dropping onStaConnected event.");
                }
                if (SoftApManager.this.mQCNumAssociatedStations > 0) {
                    cancelTimeoutMessage();
                }
            }

            private void setDisConnectedStations(String Macaddr) {
                if (SoftApManager.this.mQCNumAssociatedStations > 0) {
                    SoftApManager.access$2410(SoftApManager.this);
                }
                if (SoftApManager.this.mCallback != null) {
                    SoftApManager.this.mCallback.onStaDisconnected(Macaddr, SoftApManager.this.mQCNumAssociatedStations);
                } else {
                    Log.e(SoftApManager.TAG, "SoftApCallback is null. Dropping onStaDisconnected event.");
                }
                if (SoftApManager.this.mQCNumAssociatedStations == 0) {
                    scheduleTimeoutMessage();
                }
            }

            private void onUpChanged(boolean isUp) {
                if (isUp != SoftApManager.this.mIfaceIsUp) {
                    boolean unused = SoftApManager.this.mIfaceIsUp = isUp;
                    if (isUp) {
                        Log.d(SoftApManager.TAG, "SoftAp is ready for use");
                        SoftApManager.this.updateApState(13, 12, 0);
                        SoftApManager.this.mWifiMetrics.incrementSoftApStartResult(true, 0);
                        if (SoftApManager.this.mCallback != null) {
                            SoftApManager.this.mCallback.onNumClientsChanged(SoftApManager.this.mNumAssociatedStations);
                            SoftApManager.this.mCallback.onStaConnected(Prefix.EMPTY, SoftApManager.this.mQCNumAssociatedStations);
                        }
                    } else {
                        SoftApStateMachine.this.sendMessage(8);
                    }
                    SoftApManager.this.mWifiMetrics.addSoftApUpChangedEvent(isUp, SoftApManager.this.mMode);
                }
            }

            public void enter() {
                boolean unused = SoftApManager.this.mIfaceIsUp = false;
                boolean unused2 = SoftApManager.this.mIfaceIsDestroyed = false;
                onUpChanged(SoftApManager.this.mWifiNative.isInterfaceUp(SoftApManager.this.mApInterfaceName));
                onUpChanged(SoftApManager.this.mWifiNative.isInterfaceUp(SoftApManager.this.mDataInterfaceName));
                this.mTimeoutDelay = getConfigSoftApTimeoutDelay();
                Handler handler = SoftApManager.this.mStateMachine.getHandler();
                this.mSoftApTimeoutMessage = new WakeupMessage(SoftApManager.this.mContext, handler, SoftApManager.SOFT_AP_SEND_MESSAGE_TIMEOUT_TAG, 5);
                this.mSettingObserver = new SoftApTimeoutEnabledSettingObserver(handler);
                SoftApTimeoutEnabledSettingObserver softApTimeoutEnabledSettingObserver = this.mSettingObserver;
                if (softApTimeoutEnabledSettingObserver != null) {
                    softApTimeoutEnabledSettingObserver.register();
                }
                SoftApManager.this.mSarManager.setSapWifiState(13);
                Log.d(SoftApManager.TAG, "Resetting num stations on start");
                int unused3 = SoftApManager.this.mNumAssociatedStations = 0;
                int unused4 = SoftApManager.this.mQCNumAssociatedStations = 0;
                scheduleTimeoutMessage();
            }

            public void exit() {
                if (!SoftApManager.this.mIfaceIsDestroyed) {
                    SoftApManager.this.stopSoftAp();
                }
                SoftApTimeoutEnabledSettingObserver softApTimeoutEnabledSettingObserver = this.mSettingObserver;
                if (softApTimeoutEnabledSettingObserver != null) {
                    softApTimeoutEnabledSettingObserver.unregister();
                }
                Log.d(SoftApManager.TAG, "Resetting num stations on stop");
                int unused = SoftApManager.this.mQCNumAssociatedStations = 0;
                setNumAssociatedStations(0);
                cancelTimeoutMessage();
                SoftApManager.this.mWifiMetrics.addSoftApUpChangedEvent(false, SoftApManager.this.mMode);
                SoftApManager.this.updateApState(11, 10, 0);
                int unused2 = SoftApManager.this.mReportedFrequency = -1;
                Settings.System.putInt(SoftApManager.this.mContext.getContentResolver(), "softap_reported_frequency", SoftApManager.this.mReportedFrequency);
                SoftApManager.this.mSarManager.setSapWifiState(11);
                String unused3 = SoftApManager.this.mApInterfaceName = null;
                String unused4 = SoftApManager.this.mDataInterfaceName = null;
                boolean unused5 = SoftApManager.this.mIfaceIsUp = false;
                boolean unused6 = SoftApManager.this.mIfaceIsDestroyed = false;
                SoftApManager.this.mStateMachine.quitNow();
            }

            private void updateUserBandPreferenceViolationMetricsIfNeeded() {
                boolean bandPreferenceViolated = false;
                if (SoftApManager.this.mApConfig.apBand == 0 && ScanResult.is5GHz(SoftApManager.this.mReportedFrequency)) {
                    bandPreferenceViolated = true;
                } else if (SoftApManager.this.mApConfig.apBand == 1 && ScanResult.is24GHz(SoftApManager.this.mReportedFrequency)) {
                    bandPreferenceViolated = true;
                }
                if (bandPreferenceViolated) {
                    Log.e(SoftApManager.TAG, "Channel does not satisfy user band preference: " + SoftApManager.this.mReportedFrequency);
                    SoftApManager.this.mWifiMetrics.incrementNumSoftApUserBandPreferenceUnsatisfied();
                }
            }

            public boolean processMessage(Message message) {
                int i = message.what;
                if (i != 0) {
                    boolean isUp = false;
                    switch (i) {
                        case 2:
                            Log.w(SoftApManager.TAG, "hostapd failure, stop and report failure");
                            break;
                        case 3:
                            if (message.arg1 == 1) {
                                isUp = true;
                            }
                            onUpChanged(isUp);
                            break;
                        case 4:
                            if (message.arg1 >= 0) {
                                Log.d(SoftApManager.TAG, "Setting num stations on CMD_NUM_ASSOCIATED_STATIONS_CHANGED");
                                setNumAssociatedStations(message.arg1);
                                break;
                            } else {
                                Log.e(SoftApManager.TAG, "Invalid number of associated stations: " + message.arg1);
                                break;
                            }
                        case 5:
                            if (SoftApManager.this.mTimeoutEnabled) {
                                if (SoftApManager.this.mNumAssociatedStations == 0) {
                                    Log.i(SoftApManager.TAG, "Timeout message received. Stopping soft AP.");
                                    SoftApManager.this.updateApState(10, 13, 0);
                                    SoftApStateMachine softApStateMachine = SoftApStateMachine.this;
                                    softApStateMachine.transitionTo(softApStateMachine.mIdleState);
                                    break;
                                } else {
                                    Log.wtf(SoftApManager.TAG, "Timeout message received but has clients. Dropping.");
                                    break;
                                }
                            } else {
                                Log.wtf(SoftApManager.TAG, "Timeout message received while timeout is disabled. Dropping.");
                                break;
                            }
                        case 6:
                            if (message.arg1 == 1) {
                                isUp = true;
                            }
                            boolean isEnabled = isUp;
                            if (SoftApManager.this.mTimeoutEnabled != isEnabled) {
                                boolean unused = SoftApManager.this.mTimeoutEnabled = isEnabled;
                                if (!SoftApManager.this.mTimeoutEnabled) {
                                    cancelTimeoutMessage();
                                }
                                if (SoftApManager.this.mTimeoutEnabled && SoftApManager.this.mQCNumAssociatedStations == 0) {
                                    scheduleTimeoutMessage();
                                    break;
                                }
                            }
                            break;
                        case 7:
                            Log.d(SoftApManager.TAG, "Interface was cleanly destroyed.");
                            SoftApManager.this.updateApState(10, 13, 0);
                            boolean unused2 = SoftApManager.this.mIfaceIsDestroyed = true;
                            SoftApStateMachine softApStateMachine2 = SoftApStateMachine.this;
                            softApStateMachine2.transitionTo(softApStateMachine2.mIdleState);
                            break;
                        case 8:
                            break;
                        case 9:
                            int unused3 = SoftApManager.this.mReportedFrequency = message.arg1;
                            int unused4 = SoftApManager.this.mReportedBandwidth = message.arg2;
                            Log.d(SoftApManager.TAG, "Channel switched. Frequency: " + SoftApManager.this.mReportedFrequency + " Bandwidth: " + SoftApManager.this.mReportedBandwidth);
                            Settings.System.putInt(SoftApManager.this.mContext.getContentResolver(), "softap_reported_frequency", SoftApManager.this.mReportedFrequency);
                            SoftApManager.this.mWifiMetrics.addSoftApChannelSwitchedEvent(SoftApManager.this.mReportedFrequency, SoftApManager.this.mReportedBandwidth, SoftApManager.this.mMode);
                            updateUserBandPreferenceViolationMetricsIfNeeded();
                            break;
                        case 10:
                            if (message.obj != null) {
                                Log.d(SoftApManager.TAG, "Setting Macaddr of stations on CMD_CONNECTED_STATIONS");
                                setConnectedStations((String) message.obj);
                                break;
                            } else {
                                Log.e(SoftApManager.TAG, "Invalid Macaddr of connected station: " + message.obj);
                                break;
                            }
                        case 11:
                            if (message.obj != null) {
                                Log.d(SoftApManager.TAG, "Setting Macaddr of stations on CMD_DISCONNECTED_STATIONS");
                                setDisConnectedStations((String) message.obj);
                                break;
                            } else {
                                Log.e(SoftApManager.TAG, "Invalid Macaddr of disconnected station: " + message.obj);
                                break;
                            }
                        default:
                            return false;
                    }
                    Log.w(SoftApManager.TAG, "interface error, stop and report failure");
                    SoftApManager.this.updateApState(14, 13, 0);
                    SoftApManager.this.updateApState(10, 14, 0);
                    SoftApStateMachine softApStateMachine3 = SoftApStateMachine.this;
                    softApStateMachine3.transitionTo(softApStateMachine3.mIdleState);
                }
                return true;
            }
        }
    }
}
