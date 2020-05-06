package com.android.server.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import com.android.server.wifi.WifiNative;

class MiuiWifiStateMachine extends StateMachine {
    private static final int EVENT_WIFI_DISABLED = 1;
    private static final int EVENT_WIFI_ENABLED = 0;
    private static final String TAG = "MiuiWifiStateMachine";
    private ClientModeImpl mClientModeImpl;
    /* access modifiers changed from: private */
    public Context mContext;
    private State mDefaultState = new DefaultState();
    /* access modifiers changed from: private */
    public State mEnabledState = new EnabledState();
    /* access modifiers changed from: private */
    public State mInitialState = new InitialState();
    /* access modifiers changed from: private */
    public String mInterface;
    /* access modifiers changed from: private */
    public State mL2ConnectedState = new L2ConnectedState();
    /* access modifiers changed from: private */
    public MiuiWifiDiagnostics mMiuiWifiDiagnostics;
    private SupplicantStaIfaceHal mSupplicantStaIfaceHal;
    private WifiInjector mWifiInjector;
    private WifiMonitor mWifiMonitor;
    /* access modifiers changed from: private */
    public WifiNative mWifiNative;

    MiuiWifiStateMachine(Context context, Looper looper) {
        super(TAG, looper);
        addState(this.mDefaultState);
        addState(this.mInitialState, this.mDefaultState);
        addState(this.mEnabledState, this.mInitialState);
        addState(this.mL2ConnectedState, this.mEnabledState);
        setInitialState(this.mInitialState);
        this.mContext = context;
        this.mWifiInjector = WifiInjector.getInstance();
        this.mClientModeImpl = this.mWifiInjector.getClientModeImpl();
        this.mSupplicantStaIfaceHal = this.mWifiInjector.getSupplicantStaIfaceHal();
        this.mWifiNative = this.mWifiInjector.getWifiNative();
        this.mWifiMonitor = this.mWifiInjector.getWifiMonitor();
        this.mContext.registerReceiver(new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                int state = intent.getIntExtra("wifi_state", 4);
                if (state == 3) {
                    MiuiWifiStateMachine.this.sendMessage(0);
                } else if (state == 1) {
                    MiuiWifiStateMachine.this.sendMessage(1);
                }
            }
        }, new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED"));
        start();
    }

    class DefaultState extends State {
        DefaultState() {
        }

        public boolean processMessage(Message message) {
            switch (message.what) {
            }
            return true;
        }
    }

    class InitialState extends State {
        InitialState() {
        }

        public void enter() {
            Log.d(MiuiWifiStateMachine.TAG, "enter " + getClass().getSimpleName());
        }

        public boolean processMessage(Message message) {
            if (message.what != 0) {
                return false;
            }
            MiuiWifiStateMachine miuiWifiStateMachine = MiuiWifiStateMachine.this;
            miuiWifiStateMachine.transitionTo(miuiWifiStateMachine.mEnabledState);
            return true;
        }
    }

    class EnabledState extends State {
        EnabledState() {
        }

        public void enter() {
            Log.d(MiuiWifiStateMachine.TAG, "enter " + getClass().getSimpleName());
            MiuiWifiStateMachine miuiWifiStateMachine = MiuiWifiStateMachine.this;
            String unused = miuiWifiStateMachine.mInterface = miuiWifiStateMachine.mWifiNative.getClientInterfaceName();
            MiuiWifiStateMachine.this.registerMonitorEvent();
            MiuiWifiStateMachine miuiWifiStateMachine2 = MiuiWifiStateMachine.this;
            MiuiWifiDiagnostics unused2 = miuiWifiStateMachine2.mMiuiWifiDiagnostics = new MiuiWifiDiagnostics(miuiWifiStateMachine2.mContext, MiuiWifiStateMachine.this.getHandler().getLooper());
            MiuiWifiStateMachine.this.mMiuiWifiDiagnostics.start();
        }

        public void exit() {
            Log.d(MiuiWifiStateMachine.TAG, "exit " + getClass().getSimpleName());
            MiuiWifiStateMachine.this.mMiuiWifiDiagnostics.stop();
            MiuiWifiDiagnostics unused = MiuiWifiStateMachine.this.mMiuiWifiDiagnostics = null;
            MiuiWifiStateMachine.this.deregisterMonitorEvent();
        }

        public boolean processMessage(Message message) {
            int i = message.what;
            if (i == 1) {
                MiuiWifiStateMachine miuiWifiStateMachine = MiuiWifiStateMachine.this;
                miuiWifiStateMachine.transitionTo(miuiWifiStateMachine.mInitialState);
            } else if (i != 147459) {
                return false;
            } else {
                MiuiWifiStateMachine miuiWifiStateMachine2 = MiuiWifiStateMachine.this;
                miuiWifiStateMachine2.transitionTo(miuiWifiStateMachine2.mL2ConnectedState);
            }
            return true;
        }
    }

    class L2ConnectedState extends State {
        L2ConnectedState() {
        }

        public void enter() {
            Log.d(MiuiWifiStateMachine.TAG, "enter " + getClass().getSimpleName());
            MiuiWifiStateMachine.this.setP2pPreferredChannel();
        }

        public boolean processMessage(Message message) {
            if (message.what != 147460) {
                return false;
            }
            MiuiWifiStateMachine miuiWifiStateMachine = MiuiWifiStateMachine.this;
            miuiWifiStateMachine.transitionTo(miuiWifiStateMachine.mEnabledState);
            return true;
        }
    }

    /* access modifiers changed from: private */
    public void registerMonitorEvent() {
        this.mWifiMonitor.registerHandler(this.mInterface, WifiMonitor.NETWORK_CONNECTION_EVENT, getHandler());
        this.mWifiMonitor.registerHandler(this.mInterface, WifiMonitor.NETWORK_DISCONNECTION_EVENT, getHandler());
    }

    /* access modifiers changed from: private */
    public void deregisterMonitorEvent() {
        this.mWifiMonitor.deregisterHandler(this.mInterface, WifiMonitor.NETWORK_CONNECTION_EVENT, getHandler());
        this.mWifiMonitor.deregisterHandler(this.mInterface, WifiMonitor.NETWORK_DISCONNECTION_EVENT, getHandler());
    }

    /* access modifiers changed from: private */
    public void setP2pPreferredChannel() {
        int freq = getAssociationFrequency();
        if (freq > 0) {
            int channel = convertFrequencyToChannelNumber(freq);
            int opClass = WifiStateMachineInjector.convetFrequencyToOperatingChannel(freq, 0, 0);
            if (channel > 0 && opClass > 0) {
                Pair<Boolean, String> result = this.mSupplicantStaIfaceHal.doSupplicantCommand("SET p2p_pref_chan " + String.valueOf(opClass) + ":" + String.valueOf(channel));
                if (result == null || !((Boolean) result.first).booleanValue()) {
                    Log.e(TAG, "Failed to set p2p channel");
                }
            }
        }
    }

    private int getAssociationFrequency() {
        WifiNative.SignalPollResult pollResult = this.mWifiNative.signalPoll(this.mInterface);
        if (pollResult != null) {
            return pollResult.associationFrequency;
        }
        return 0;
    }

    private int convertFrequencyToChannelNumber(int frequency) {
        if (frequency >= 2412 && frequency <= 2484) {
            return ((frequency - 2412) / 5) + 1;
        }
        if (frequency < 5170 || frequency > 5825) {
            return 0;
        }
        return ((frequency - 5170) / 5) + 34;
    }
}
