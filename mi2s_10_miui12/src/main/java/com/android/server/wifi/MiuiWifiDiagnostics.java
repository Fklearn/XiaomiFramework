package com.android.server.wifi;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import miui.mqsas.sdk.MQSEventManagerDelegate;
import miui.telephony.phonenumber.Prefix;

class MiuiWifiDiagnostics extends WifiDiagnostics implements Handler.Callback {
    private static int CONNECT_FAILURE_EVENT = 1;
    private static int DISCONNECT_EVENT = 0;
    private static final String TAG = "MiuiWifiDiagnostics";
    private Handler mHandler;
    private String mInterfaceName = WifiInjector.getInstance().getWifiNative().getClientInterfaceName();
    private int mLastDisconnectReasonCode = -1;
    private int mLastRejectReasonCode = -1;
    private String mLastReportBSSID = Prefix.EMPTY;
    private MQSEventManagerDelegate mMqsEventManager = MQSEventManagerDelegate.getInstance();
    private WifiInjector mWifiInjector;

    MiuiWifiDiagnostics(Context context, Looper looper) {
        super(context, WifiInjector.getInstance(), WifiInjector.getInstance().getWifiNative(), new SystemBuildProperties(), new LastMileLogger(WifiInjector.getInstance()), WifiInjector.getInstance().getClock());
        this.mHandler = new Handler(looper, this);
        this.mWifiInjector = WifiInjector.getInstance();
    }

    public void start() {
        this.mWifiInjector.getWifiMonitor().registerHandler(this.mInterfaceName, WifiMonitor.ASSOCIATION_REJECTION_EVENT, this.mHandler);
        this.mWifiInjector.getWifiMonitor().registerHandler(this.mInterfaceName, WifiMonitor.AUTHENTICATION_FAILURE_EVENT, this.mHandler);
        WifiInjector.getInstance().getWifiMonitor().registerHandler(this.mInterfaceName, WifiMonitor.NETWORK_DISCONNECTION_EVENT, this.mHandler);
        WifiInjector.getInstance().getClientModeImpl().setWifiDiagnostics(this);
    }

    public void stop() {
        this.mWifiInjector.getWifiMonitor().deregisterHandler(this.mInterfaceName, WifiMonitor.ASSOCIATION_REJECTION_EVENT, this.mHandler);
        this.mWifiInjector.getWifiMonitor().deregisterHandler(this.mInterfaceName, WifiMonitor.AUTHENTICATION_FAILURE_EVENT, this.mHandler);
        this.mWifiInjector.getWifiMonitor().deregisterHandler(this.mInterfaceName, WifiMonitor.NETWORK_DISCONNECTION_EVENT, this.mHandler);
    }

    public boolean handleMessage(Message msg) {
        int i = msg.what;
        if (i == 147460) {
            String bssid = (String) msg.obj;
            int reasonCode = msg.arg2;
            if ((bssid == null || !bssid.equals(this.mLastReportBSSID) || this.mLastDisconnectReasonCode != reasonCode) && unexpectedDisconnectedReason(reasonCode) && !isLowRssi()) {
                this.mMqsEventManager.reportConnectExceptionEvent(DISCONNECT_EVENT, reasonCode, bssid);
            }
            this.mLastDisconnectReasonCode = reasonCode;
            if (bssid == null) {
                return true;
            }
            this.mLastReportBSSID = bssid;
            return true;
        } else if (i == 147463) {
            int reasonCode2 = msg.arg2;
            if (reasonCode2 == 2) {
                return true;
            }
            this.mMqsEventManager.reportConnectExceptionEvent(CONNECT_FAILURE_EVENT, reasonCode2, (String) null);
            return true;
        } else if (i != 147499) {
            return false;
        } else {
            String bssid2 = (String) msg.obj;
            int reasonCode3 = msg.arg2;
            if ((bssid2 == null || !bssid2.equals(this.mLastReportBSSID) || this.mLastRejectReasonCode != reasonCode3) && unexpectedAssociationRejectReason(reasonCode3) && isUserSelected()) {
                this.mMqsEventManager.reportConnectExceptionEvent(CONNECT_FAILURE_EVENT, reasonCode3, bssid2);
            }
            this.mLastRejectReasonCode = reasonCode3;
            if (bssid2 == null) {
                return true;
            }
            this.mLastReportBSSID = bssid2;
            return true;
        }
    }

    public void captureBugReportData(int reason) {
        super.captureBugReportData(reason);
        if (reason == 4 || reason == 3) {
            this.mMqsEventManager.reportConnectExceptionEvent(CONNECT_FAILURE_EVENT, reason, (String) null);
        }
    }

    private boolean isUserSelected() {
        boolean ret = false;
        int netId = MiuiSupplicantStateTracker.getNetId();
        if (netId != -1) {
            ret = netId != -1;
        }
        Log.d(TAG, "isUserSelected: " + ret);
        return ret;
    }

    private boolean isLowRssi() {
        WifiInfo info;
        boolean ret = false;
        ClientModeImpl cmi = this.mWifiInjector.getClientModeImpl();
        if (!(cmi == null || (info = cmi.getWifiInfo()) == null || info.getRssi() == -127)) {
            ret = info.getRssi() < -75;
        }
        Log.d(TAG, "isLowRssi: " + ret);
        return ret;
    }

    private boolean unexpectedAssociationRejectReason(int status) {
        return (status == 17 || status == 0) ? false : true;
    }

    private boolean unexpectedDisconnectedReason(int reason) {
        return reason == 0 || reason == 1 || reason == 2 || reason == 6 || reason == 7 || reason == 9 || reason == 14 || reason == 15 || reason == 16 || reason == 18 || reason == 19 || reason == 23 || reason == 34;
    }
}
