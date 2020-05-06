package com.android.server.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import com.android.internal.app.IBatteryStats;
import com.android.internal.telephony.IccCardConstants;
import com.android.server.am.BatteryStatsService;
import com.android.server.policy.PhoneWindowManager;
import miui.telephony.SubscriptionManager;

public class DataConnectionStats extends BroadcastReceiver {
    private static final boolean DEBUG = false;
    private static final String TAG = "DataConnectionStats";
    private final IBatteryStats mBatteryStats;
    private final Context mContext;
    /* access modifiers changed from: private */
    public int mDataState = 0;
    private final PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            SignalStrength unused = DataConnectionStats.this.mSignalStrength = signalStrength;
        }

        public void onServiceStateChanged(ServiceState state) {
            ServiceState unused = DataConnectionStats.this.mServiceState = state;
            DataConnectionStats.this.notePhoneDataConnectionState();
        }

        public void onDataConnectionStateChanged(int state, int networkType) {
            int unused = DataConnectionStats.this.mDataState = state;
            DataConnectionStats.this.notePhoneDataConnectionState();
        }

        public void onDataActivity(int direction) {
            DataConnectionStats.this.notePhoneDataConnectionState();
        }
    };
    /* access modifiers changed from: private */
    public ServiceState mServiceState;
    /* access modifiers changed from: private */
    public SignalStrength mSignalStrength;
    private IccCardConstants.State mSimState = IccCardConstants.State.READY;

    public DataConnectionStats(Context context) {
        this.mContext = context;
        this.mBatteryStats = BatteryStatsService.getService();
    }

    public void startMonitoring() {
        ((TelephonyManager) this.mContext.getSystemService("phone")).listen(this.mPhoneStateListener, 449);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SIM_STATE_CHANGED");
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.conn.INET_CONDITION_ACTION");
        addActionIfNeeded(filter);
        this.mContext.registerReceiver(this, filter);
    }

    public void onReceive(Context context, Intent intent) {
        if (!handleAction(intent)) {
            String action = intent.getAction();
            if (action.equals("android.intent.action.SIM_STATE_CHANGED")) {
                updateSimState(intent);
                notePhoneDataConnectionState();
            } else if (action.equals("android.net.conn.CONNECTIVITY_CHANGE") || action.equals("android.net.conn.INET_CONDITION_ACTION")) {
                notePhoneDataConnectionState();
            }
        }
    }

    /* access modifiers changed from: private */
    public void notePhoneDataConnectionState() {
        if (this.mServiceState != null) {
            boolean z = false;
            if (((this.mSimState == IccCardConstants.State.READY || this.mSimState == IccCardConstants.State.UNKNOWN) || isCdma()) && hasService() && this.mDataState == 2) {
                z = true;
            }
            boolean visible = z;
            int networkType = this.mServiceState.getDataNetworkType();
            if (networkType == 13 && this.mServiceState.isUsingCarrierAggregation()) {
                networkType = 19;
            }
            try {
                this.mBatteryStats.notePhoneDataConnectionState(networkType, visible);
            } catch (RemoteException e) {
                Log.w(TAG, "Error noting data connection state", e);
            }
        }
    }

    private final void updateSimState(Intent intent) {
        String stateExtra = intent.getStringExtra("ss");
        if ("ABSENT".equals(stateExtra)) {
            this.mSimState = IccCardConstants.State.ABSENT;
        } else if ("READY".equals(stateExtra)) {
            this.mSimState = IccCardConstants.State.READY;
        } else if ("LOCKED".equals(stateExtra)) {
            String lockedReason = intent.getStringExtra(PhoneWindowManager.SYSTEM_DIALOG_REASON_KEY);
            if ("PIN".equals(lockedReason)) {
                this.mSimState = IccCardConstants.State.PIN_REQUIRED;
            } else if ("PUK".equals(lockedReason)) {
                this.mSimState = IccCardConstants.State.PUK_REQUIRED;
            } else {
                this.mSimState = IccCardConstants.State.NETWORK_LOCKED;
            }
        } else {
            this.mSimState = IccCardConstants.State.UNKNOWN;
        }
    }

    private boolean isCdma() {
        SignalStrength signalStrength = this.mSignalStrength;
        return signalStrength != null && !signalStrength.isGsm();
    }

    private boolean hasService() {
        ServiceState serviceState = this.mServiceState;
        if (serviceState == null || serviceState.getState() == 1 || this.mServiceState.getState() == 3) {
            return false;
        }
        return true;
    }

    private void addActionIfNeeded(IntentFilter filter) {
        if (miui.telephony.TelephonyManager.getDefault().isMultiSimEnabled()) {
            filter.addAction("android.intent.action.ACTION_SUBINFO_RECORD_UPDATED");
            filter.addAction("miui.intent.action.ACTION_DEFAULT_DATA_SLOT_CHANGED");
        }
    }

    private boolean handleAction(Intent intent) {
        String action = intent.getAction();
        if ("android.intent.action.SIM_STATE_CHANGED".equals(action)) {
            if (intent.getIntExtra("phone", -1) != SubscriptionManager.getDefault().getDefaultDataSlotId()) {
                return true;
            }
        } else if ("android.intent.action.ACTION_SUBINFO_RECORD_UPDATED".equals(action) || "miui.intent.action.ACTION_DEFAULT_DATA_SLOT_CHANGED".equals(action)) {
            int[] dataSub = android.telephony.SubscriptionManager.getSubId(SubscriptionManager.getDefault().getDefaultDataSlotId());
            int dataSubId = -1;
            if (dataSub != null && dataSub.length > 0) {
                dataSubId = dataSub[0];
            }
            if (dataSubId != this.mPhoneStateListener.getSubId()) {
                Log.d(TAG, "listen phone state subId new=" + dataSubId + " old=" + this.mPhoneStateListener.getSubId());
                this.mPhoneStateListener.setSubId(dataSubId);
                TelephonyManager.from(this.mContext).listen(this.mPhoneStateListener, 449);
            }
            return true;
        }
        return false;
    }
}
