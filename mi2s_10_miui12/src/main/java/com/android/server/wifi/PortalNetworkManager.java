package com.android.server.wifi;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.MiuiIntent;
import android.net.NetworkInfo;
import android.os.RemoteException;

public class PortalNetworkManager {
    private Context mContext;
    /* access modifiers changed from: private */
    public boolean mIsPortalNetworkConnected;

    public PortalNetworkManager(Context context) {
        this.mContext = context;
        registerReceiver();
    }

    private void registerReceiver() {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (MiuiIntent.ACTION_OPEN_WIFI_LOGIN.equals(action)) {
                    boolean unused = PortalNetworkManager.this.mIsPortalNetworkConnected = true;
                } else if (PortalNetworkManager.this.mIsPortalNetworkConnected && "android.net.wifi.STATE_CHANGE".equals(action) && ((NetworkInfo) intent.getParcelableExtra("networkInfo")).getState() == NetworkInfo.State.DISCONNECTED) {
                    PortalNetworkManager.this.cancelNotification();
                }
            }
        };
        IntentFilter networkChangedFilter = new IntentFilter();
        networkChangedFilter.addAction("android.net.wifi.STATE_CHANGE");
        IntentFilter portalNetworkConnectFilter = new IntentFilter();
        portalNetworkConnectFilter.addAction(MiuiIntent.ACTION_OPEN_WIFI_LOGIN);
        portalNetworkConnectFilter.addDataScheme("http");
        portalNetworkConnectFilter.addDataScheme("https");
        this.mContext.registerReceiver(receiver, networkChangedFilter);
        this.mContext.registerReceiver(receiver, portalNetworkConnectFilter);
    }

    /* access modifiers changed from: private */
    public void cancelNotification() {
        this.mIsPortalNetworkConnected = false;
        try {
            NotificationManager.getService().cancelAllNotifications("com.android.htmlviewer", 0);
        } catch (RemoteException e) {
        }
    }
}
