package com.android.server.wifi;

import android.content.Context;
import android.database.ContentObserver;
import android.os.Handler;
import android.provider.MiuiSettings;
import android.provider.Settings;
import com.android.server.wifi.cmcc.CmccManager;
import java.util.HashSet;
import miui.os.Build;

class WifiAutoConnController {
    private final String TAG = "WifiAutoConnController";
    /* access modifiers changed from: private */
    public Context mContext;
    /* access modifiers changed from: private */
    public HashSet<String> mDisableSsidSet;
    private Handler mHandler;
    private WifiConfigManager mWifiConfigManager;

    public WifiAutoConnController(WifiConfigManager wifiConfigManager, Context context, Handler handler) {
        this.mWifiConfigManager = wifiConfigManager;
        this.mContext = context;
        this.mHandler = handler;
        registerDisableWifiAutoConnectChangedObserver();
        registerEnableDataStallDetectChangedObserver();
        if (Build.IS_CM_CUSTOMIZATION) {
            new CmccManager(this.mContext);
        }
        new WifiApManager(context, handler);
    }

    /* access modifiers changed from: package-private */
    public void registerEnableDataStallDetectChangedObserver() {
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("cloud_data_stall_detect_enabled"), false, new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                MiuiWifiService.enableDataStallDetection(WifiAutoConnController.this.mContext);
            }
        });
    }

    /* access modifiers changed from: package-private */
    public boolean isDisableByUser(String ssid) {
        if (!this.mDisableSsidSet.contains(ssid)) {
            HashSet<String> hashSet = this.mDisableSsidSet;
            StringBuilder sb = new StringBuilder();
            sb.append("\"");
            sb.append(ssid);
            sb.append("\"");
            return hashSet.contains(sb.toString());
        }
    }

    /* access modifiers changed from: package-private */
    public void registerDisableWifiAutoConnectChangedObserver() {
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("disable_wifi_auto_connect_ssid"), false, new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                WifiAutoConnController wifiAutoConnController = WifiAutoConnController.this;
                HashSet unused = wifiAutoConnController.mDisableSsidSet = MiuiSettings.System.getDisableWifiAutoConnectSsid(wifiAutoConnController.mContext);
            }
        }, -1);
        this.mDisableSsidSet = MiuiSettings.System.getDisableWifiAutoConnectSsid(this.mContext);
    }
}
