package com.android.server.wifi.cmcc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.MiuiIntent;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;
import android.view.MiuiWindowManager;
import com.android.server.wifi.WifiInjector;

public class CmccManager {
    private static final int LTE_CONNECTED = 2;
    private static final int NONE_CONNECTED = 0;
    private static final String TAG = "CmccManager";
    public static final int TYPE_WIFI_CMCC_CONNECTED_TIP = 1;
    public static final int TYPE_WIFI_OFF_AIRPLANE_ON_TIP = 0;
    private static final int WIFI_CONNECTED = 1;
    private Context mContext;
    private int mNetworkState = 0;

    public CmccManager(Context context) {
        this.mContext = context;
        registerConnectivityChangedReceiver();
    }

    private void registerConnectivityChangedReceiver() {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                CmccManager.this.handleConnectivityChanged(intent);
            }
        };
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("android.net.wifi.SCAN_RESULTS");
        filter.addAction("android.net.wifi.STATE_CHANGE");
        this.mContext.registerReceiver(receiver, filter);
    }

    private void updateNetworkState(NetworkInfo info) {
        int type = info.getType();
        if (!info.isConnected()) {
            this.mNetworkState = 0;
        } else if (type == 0) {
            this.mNetworkState = 2;
        } else if (type == 1) {
            this.mNetworkState = 1;
        }
        Log.d(TAG, "updateNetworkState[" + convertState(this.mNetworkState) + "]");
    }

    private int getNetworkstate() {
        return this.mNetworkState;
    }

    /* access modifiers changed from: private */
    public void handleConnectivityChanged(Intent intent) {
        NetworkInfo networkInfo;
        String action = intent.getAction();
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
            NetworkInfo info = (NetworkInfo) intent.getParcelableExtra("networkInfo");
            updateNetworkState(info);
            if (CmccUtils.isWifiEnabled(this.mContext) && CmccUtils.isWifiSsidAutoSelect(this.mContext)) {
                CmccUtils.enableBestNetwork(this.mContext);
            }
            notifyConnectivityStateChanged(this.mContext, info);
        } else if ("android.net.wifi.SCAN_RESULTS".equals(action)) {
            showAskDialog();
        } else if ("android.net.wifi.STATE_CHANGE".equals(action) && (networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo")) != null && networkInfo.getType() == 1) {
            NetworkInfo.State state = networkInfo.getState();
            if (state == NetworkInfo.State.DISCONNECTED && !CmccUtils.isWifiEnabled(this.mContext) && WifiInjector.getInstance().getWifiSettingsStore().isAirplaneModeOn()) {
                showWifiTipDialog(this.mContext, 0);
            } else if (state == NetworkInfo.State.CONNECTED && CmccUtils.isWifiEnabled(this.mContext) && !TextUtils.isEmpty(networkInfo.getExtraInfo()) && "\"CMCC\"".equals(networkInfo.getExtraInfo())) {
                showWifiTipDialog(this.mContext, 1);
            }
        }
    }

    private void notifyConnectivityStateChanged(Context context, NetworkInfo info) {
        Intent intent = new Intent(MiuiIntent.ACTION_CONNECTIVITY_CHANGED);
        intent.putExtra("networkInfo", info);
        intent.setPackage("com.android.settings");
        context.sendBroadcast(intent);
    }

    private void showAskDialog() {
        int state = getNetworkstate();
        Intent intent = new Intent();
        intent.addFlags(MiuiWindowManager.LayoutParams.EXTRA_FLAG_IS_SCREEN_PROJECTION);
        if (state == 2 && !CmccUtils.isWifiAutoConnect(this.mContext)) {
            intent.setAction(MiuiIntent.ACTION_SWITCH_TO_WIFI);
        } else if (state == 0 && !CmccUtils.isWifiSsidAutoSelect(this.mContext)) {
            intent.setAction(MiuiIntent.ACTION_SLECT_WIFI_AP);
        }
        this.mContext.sendBroadcast(intent);
    }

    private static String convertState(int state) {
        if (state == 0) {
            return "NONE_CONNECTED";
        }
        if (state == 1) {
            return "WIFI_CONNECTED";
        }
        if (state != 2) {
            return null;
        }
        return "LTE_CONNECTED";
    }

    private void showWifiTipDialog(Context context, int dialogType) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.wifi.WifiTipActivity");
        intent.putExtra("extra_dialog_type", dialogType);
        intent.setFlags(268435456);
        context.startActivity(intent);
    }
}
