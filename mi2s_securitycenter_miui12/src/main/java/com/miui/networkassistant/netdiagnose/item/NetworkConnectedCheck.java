package com.miui.networkassistant.netdiagnose.item;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import b.b.c.h.f;
import com.miui.networkassistant.netdiagnose.AbstractNetworkDiagoneItem;
import com.miui.networkassistant.netdiagnose.NetworkDiagnosticsUtils;
import com.miui.networkassistant.utils.AnalyticsHelper;
import com.miui.securitycenter.R;
import java.util.HashMap;

public class NetworkConnectedCheck extends AbstractNetworkDiagoneItem {
    private static final String TAG = "NetworkDiagnostics_NetworkConnectedCheck";
    String mBssid;
    private NetworkDiagnosticsUtils.NetworkState mCurNetworkState = NetworkDiagnosticsUtils.NetworkState.UNKNOWN;
    String mSolution;
    String mSsid;

    public NetworkConnectedCheck(Context context) {
        super(context);
    }

    private void openWifiLogin() {
        Uri parse = Uri.parse(NetworkDiagnosticsUtils.getDefaultCaptivePortalServer());
        WifiManager wifiManager = (WifiManager) this.mContext.getSystemService("wifi");
        WifiInfo connectionInfo = wifiManager != null ? wifiManager.getConnectionInfo() : null;
        if (connectionInfo != null && TextUtils.equals(this.mBssid, connectionInfo.getBSSID())) {
            Intent intent = new Intent();
            intent.addFlags(276824064);
            intent.setAction("com.miui.action.OPEN_WIFI_LOGIN");
            intent.putExtra("miui.intent.extra.OPEN_WIFI_SSID", this.mSsid);
            intent.putExtra("miui.intent.extra.BSSID", this.mBssid);
            intent.setData(parse);
            this.mContext.startActivity(intent);
        }
    }

    public void check() {
        this.mIsStatusNormal = true;
        this.mCurNetworkState = this.mDiagnosticsManager.checkNetworkState();
        this.mDiagnosticsManager.setCurNetworkState(this.mCurNetworkState);
        if (f.l(this.mContext) && this.mCurNetworkState == NetworkDiagnosticsUtils.NetworkState.CAPTIVEPORTAL) {
            WifiManager wifiManager = (WifiManager) this.mContext.getSystemService("wifi");
            WifiInfo wifiInfo = null;
            if (wifiManager != null) {
                wifiInfo = wifiManager.getConnectionInfo();
            }
            if (wifiInfo != null) {
                this.mSsid = wifiInfo.getSSID();
                this.mBssid = wifiInfo.getBSSID();
            } else {
                return;
            }
        }
        NetworkDiagnosticsUtils.NetworkState networkState = this.mCurNetworkState;
        if (networkState == NetworkDiagnosticsUtils.NetworkState.CONNECTED) {
            this.mIsStatusNormal = false;
        } else if (networkState == NetworkDiagnosticsUtils.NetworkState.CAPTIVEPORTAL && f.l(this.mContext)) {
            this.mIsStatusNormal = false;
            if (!networkChanged()) {
                HashMap hashMap = new HashMap(1);
                hashMap.put("wifi", "captivePortal");
                AnalyticsHelper.trackNetworkDiagnosticsStep(hashMap);
            }
        }
    }

    public AbstractNetworkDiagoneItem.FixedResult fix() {
        openWifiLogin();
        return AbstractNetworkDiagoneItem.FixedResult.SUCCESS;
    }

    public boolean getIsContinueDiagnose() {
        if (this.mCurNetworkState == NetworkDiagnosticsUtils.NetworkState.CONNECTED) {
            return true;
        }
        return this.mIsStatusNormal;
    }

    public String getItemName() {
        return this.mContext.getResources().getString(R.string.network_connected_exception_title);
    }

    public String getItemSolution() {
        return this.mSolution;
    }

    public String getItemSummary() {
        this.mSolution = "";
        NetworkDiagnosticsUtils.NetworkState networkState = this.mCurNetworkState;
        if (networkState == NetworkDiagnosticsUtils.NetworkState.CONNECTED) {
            return this.mContext.getResources().getString(R.string.current_network_state_not_need_diagnose);
        }
        if (networkState != NetworkDiagnosticsUtils.NetworkState.CAPTIVEPORTAL || !f.l(this.mContext)) {
            return "";
        }
        String string = this.mContext.getResources().getString(R.string.network_blocked_connected_open_wifi);
        this.mSolution = this.mContext.getResources().getString(R.string.see_detail);
        return string;
    }
}
