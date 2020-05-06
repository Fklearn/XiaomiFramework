package com.android.server.wifi;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiConfiguration;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.MiuiWindowManager;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.notification.SystemNotificationChannels;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import miui.app.constants.ThemeManagerConstants;
import miui.content.res.ThemeResources;

public class WifiApConfigStore {
    public static final String ACTION_HOTSPOT_CONFIG_USER_TAPPED_CONTENT = "com.android.server.wifi.WifiApConfigStoreUtil.HOTSPOT_CONFIG_USER_TAPPED_CONTENT";
    @VisibleForTesting
    static final int AP_CHANNEL_DEFAULT = 0;
    private static final int AP_CONFIG_FILE_VERSION = 3;
    private static final String DEFAULT_AP_CONFIG_FILE = (Environment.getDataDirectory() + "/misc/wifi/softap.conf");
    @VisibleForTesting
    static final int PSK_MAX_LEN = 63;
    @VisibleForTesting
    static final int PSK_MIN_LEN = 8;
    private static final int RAND_SSID_INT_MAX = 9999;
    private static final int RAND_SSID_INT_MIN = 1000;
    @VisibleForTesting
    static final int SSID_MAX_LEN = 32;
    @VisibleForTesting
    static final int SSID_MIN_LEN = 1;
    private static final String TAG = "WifiApConfigStore";
    private ArrayList<Integer> mAllowed2GChannel;
    private final String mApConfigFile;
    private final BackupManagerProxy mBackupManagerProxy;
    private String mBridgeInterfaceName;
    private final BroadcastReceiver mBroadcastReceiver;
    private final Context mContext;
    private boolean mDualSapStatus;
    private final FrameworkFacade mFrameworkFacade;
    private final Handler mHandler;
    private boolean mRequiresApBandConversion;
    private WifiConfiguration mWifiApConfig;

    WifiApConfigStore(Context context, Looper looper, BackupManagerProxy backupManagerProxy, FrameworkFacade frameworkFacade) {
        this(context, looper, backupManagerProxy, frameworkFacade, DEFAULT_AP_CONFIG_FILE);
    }

    WifiApConfigStore(Context context, Looper looper, BackupManagerProxy backupManagerProxy, FrameworkFacade frameworkFacade, String apConfigFile) {
        this.mWifiApConfig = null;
        this.mAllowed2GChannel = null;
        this.mRequiresApBandConversion = false;
        this.mBridgeInterfaceName = null;
        this.mDualSapStatus = false;
        this.mBroadcastReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (((action.hashCode() == 765958520 && action.equals(WifiApConfigStore.ACTION_HOTSPOT_CONFIG_USER_TAPPED_CONTENT)) ? (char) 0 : 65535) != 0) {
                    Log.e(WifiApConfigStore.TAG, "Unknown action " + intent.getAction());
                    return;
                }
                WifiApConfigStore.this.handleUserHotspotConfigTappedContent();
            }
        };
        this.mContext = context;
        this.mHandler = new Handler(looper);
        this.mBackupManagerProxy = backupManagerProxy;
        this.mFrameworkFacade = frameworkFacade;
        this.mApConfigFile = apConfigFile;
        String ap2GChannelListStr = this.mContext.getResources().getString(17039801);
        Log.d(TAG, "2G band allowed channels are:" + ap2GChannelListStr);
        if (ap2GChannelListStr != null) {
            this.mAllowed2GChannel = new ArrayList<>();
            for (String tmp : ap2GChannelListStr.split(",")) {
                this.mAllowed2GChannel.add(Integer.valueOf(Integer.parseInt(tmp)));
            }
        }
        this.mRequiresApBandConversion = this.mContext.getResources().getBoolean(17891583);
        this.mWifiApConfig = loadApConfiguration(this.mApConfigFile);
        if (this.mWifiApConfig == null) {
            Log.d(TAG, "Fallback to use default AP configuration");
            this.mWifiApConfig = getDefaultApConfiguration();
            writeApConfiguration(this.mApConfigFile, this.mWifiApConfig);
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_HOTSPOT_CONFIG_USER_TAPPED_CONTENT);
        this.mContext.registerReceiver(this.mBroadcastReceiver, filter, (String) null, this.mHandler);
        this.mBridgeInterfaceName = SystemProperties.get("persist.vendor.wifi.softap.bridge.interface", "wifi_br0");
    }

    public synchronized String getBridgeInterface() {
        return this.mBridgeInterfaceName;
    }

    public synchronized boolean getDualSapStatus() {
        return this.mDualSapStatus;
    }

    public synchronized void setDualSapStatus(boolean enable) {
        this.mDualSapStatus = enable;
    }

    public synchronized WifiConfiguration getApConfiguration() {
        WifiConfiguration config = apBandCheckConvert(this.mWifiApConfig);
        if (this.mWifiApConfig != config) {
            Log.d(TAG, "persisted config was converted, need to resave it");
            this.mWifiApConfig = config;
            persistConfigAndTriggerBackupManagerProxy(this.mWifiApConfig);
        }
        return this.mWifiApConfig;
    }

    public synchronized void setApConfiguration(WifiConfiguration config) {
        if (config == null) {
            this.mWifiApConfig = getDefaultApConfiguration();
        } else {
            this.mWifiApConfig = apBandCheckConvert(config);
        }
        persistConfigAndTriggerBackupManagerProxy(this.mWifiApConfig);
    }

    public ArrayList<Integer> getAllowed2GChannel() {
        return this.mAllowed2GChannel;
    }

    public void notifyUserOfApBandConversion(String packageName) {
        Log.w(TAG, "ready to post notification - triggered by " + packageName);
        ((NotificationManager) this.mContext.getSystemService(ThemeManagerConstants.COMPONENT_CODE_NOTIFICATION)).notify(50, createConversionNotification());
    }

    private Notification createConversionNotification() {
        CharSequence title = this.mContext.getResources().getText(17041417);
        CharSequence contentSummary = this.mContext.getResources().getText(17041419);
        CharSequence content = this.mContext.getResources().getText(17041418);
        return new Notification.Builder(this.mContext, SystemNotificationChannels.NETWORK_STATUS).setSmallIcon(17302874).setPriority(1).setCategory("sys").setContentTitle(title).setContentText(contentSummary).setContentIntent(getPrivateBroadcast(ACTION_HOTSPOT_CONFIG_USER_TAPPED_CONTENT)).setTicker(title).setShowWhen(false).setLocalOnly(true).setColor(this.mContext.getResources().getColor(17170460, this.mContext.getTheme())).setStyle(new Notification.BigTextStyle().bigText(content).setBigContentTitle(title).setSummaryText(contentSummary)).build();
    }

    private WifiConfiguration apBandCheckConvert(WifiConfiguration config) {
        if (this.mRequiresApBandConversion) {
            if (config.apBand == 1) {
                Log.w(TAG, "Supplied ap config band was 5GHz only, converting to ANY");
                WifiConfiguration convertedConfig = new WifiConfiguration(config);
                convertedConfig.apBand = -1;
                convertedConfig.apChannel = 0;
                return convertedConfig;
            }
        } else if (config.apBand == -1) {
            Log.w(TAG, "Supplied ap config band was ANY, converting to 5GHz");
            WifiConfiguration convertedConfig2 = new WifiConfiguration(config);
            convertedConfig2.apBand = 1;
            convertedConfig2.apChannel = 0;
            return convertedConfig2;
        }
        return config;
    }

    private void persistConfigAndTriggerBackupManagerProxy(WifiConfiguration config) {
        writeApConfiguration(this.mApConfigFile, this.mWifiApConfig);
        this.mBackupManagerProxy.notifyDataChanged();
    }

    private static WifiConfiguration loadApConfiguration(String filename) {
        WifiConfiguration config;
        StringBuilder sb;
        DataInputStream in = null;
        try {
            config = new WifiConfiguration();
            in = new DataInputStream(new BufferedInputStream(new FileInputStream(filename)));
            int version = in.readInt();
            if (version >= 1) {
                if (version <= 3) {
                    config.SSID = in.readUTF();
                    if (version >= 2) {
                        config.apBand = in.readInt();
                        config.apChannel = in.readInt();
                    }
                    if (version >= 3) {
                        config.hiddenSSID = in.readBoolean();
                    }
                    int authType = in.readInt();
                    config.allowedKeyManagement.set(authType);
                    if (authType != 0) {
                        config.preSharedKey = in.readUTF();
                    }
                    try {
                        in.close();
                    } catch (IOException e) {
                        e = e;
                        sb = new StringBuilder();
                    }
                    return config;
                }
            }
            Log.e(TAG, "Bad version on hotspot configuration file");
            try {
                in.close();
            } catch (IOException e2) {
                Log.e(TAG, "Error closing hotspot configuration during read" + e2);
            }
            return null;
            sb.append("Error closing hotspot configuration during read");
            sb.append(e);
            Log.e(TAG, sb.toString());
            return config;
        } catch (IOException e3) {
            Log.e(TAG, "Error reading hotspot configuration " + e3);
            config = null;
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e4) {
                    e = e4;
                    sb = new StringBuilder();
                }
            }
        } catch (Throwable th) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e5) {
                    Log.e(TAG, "Error closing hotspot configuration during read" + e5);
                }
            }
            throw th;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x003b, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0044, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void writeApConfiguration(java.lang.String r4, android.net.wifi.WifiConfiguration r5) {
        /*
            java.io.DataOutputStream r0 = new java.io.DataOutputStream     // Catch:{ IOException -> 0x0045 }
            java.io.BufferedOutputStream r1 = new java.io.BufferedOutputStream     // Catch:{ IOException -> 0x0045 }
            java.io.FileOutputStream r2 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x0045 }
            r2.<init>(r4)     // Catch:{ IOException -> 0x0045 }
            r1.<init>(r2)     // Catch:{ IOException -> 0x0045 }
            r0.<init>(r1)     // Catch:{ IOException -> 0x0045 }
            r1 = 3
            r0.writeInt(r1)     // Catch:{ all -> 0x0039 }
            java.lang.String r1 = r5.SSID     // Catch:{ all -> 0x0039 }
            r0.writeUTF(r1)     // Catch:{ all -> 0x0039 }
            int r1 = r5.apBand     // Catch:{ all -> 0x0039 }
            r0.writeInt(r1)     // Catch:{ all -> 0x0039 }
            int r1 = r5.apChannel     // Catch:{ all -> 0x0039 }
            r0.writeInt(r1)     // Catch:{ all -> 0x0039 }
            boolean r1 = r5.hiddenSSID     // Catch:{ all -> 0x0039 }
            r0.writeBoolean(r1)     // Catch:{ all -> 0x0039 }
            int r1 = r5.getAuthType()     // Catch:{ all -> 0x0039 }
            r0.writeInt(r1)     // Catch:{ all -> 0x0039 }
            if (r1 == 0) goto L_0x0035
            java.lang.String r2 = r5.preSharedKey     // Catch:{ all -> 0x0039 }
            r0.writeUTF(r2)     // Catch:{ all -> 0x0039 }
        L_0x0035:
            r0.close()     // Catch:{ IOException -> 0x0045 }
            goto L_0x005c
        L_0x0039:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x003b }
        L_0x003b:
            r2 = move-exception
            r0.close()     // Catch:{ all -> 0x0040 }
            goto L_0x0044
        L_0x0040:
            r3 = move-exception
            r1.addSuppressed(r3)     // Catch:{ IOException -> 0x0045 }
        L_0x0044:
            throw r2     // Catch:{ IOException -> 0x0045 }
        L_0x0045:
            r0 = move-exception
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Error writing hotspot configuration"
            r1.append(r2)
            r1.append(r0)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "WifiApConfigStore"
            android.util.Log.e(r2, r1)
        L_0x005c:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wifi.WifiApConfigStore.writeApConfiguration(java.lang.String, android.net.wifi.WifiConfiguration):void");
    }

    private WifiConfiguration getDefaultApConfiguration() {
        WifiConfiguration config = new WifiConfiguration();
        config.apBand = 0;
        config.SSID = this.mContext.getResources().getString(17041424) + "_" + getRandomIntForDefaultSsid();
        config.allowedKeyManagement.set(4);
        String randomUUID = UUID.randomUUID().toString();
        config.preSharedKey = randomUUID.substring(0, 8) + randomUUID.substring(9, 13);
        return config;
    }

    private static int getRandomIntForDefaultSsid() {
        return new Random().nextInt(9000) + 1000;
    }

    public static WifiConfiguration generateLocalOnlyHotspotConfig(Context context, int apBand) {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = context.getResources().getString(17041402) + "_" + getRandomIntForDefaultSsid();
        config.apBand = apBand;
        config.allowedKeyManagement.set(4);
        config.networkId = -2;
        String randomUUID = UUID.randomUUID().toString();
        config.preSharedKey = randomUUID.substring(0, 8) + randomUUID.substring(9, 13);
        return config;
    }

    private static boolean validateApConfigSsid(String ssid) {
        if (TextUtils.isEmpty(ssid)) {
            Log.d(TAG, "SSID for softap configuration must be set.");
            return false;
        }
        try {
            byte[] ssid_bytes = ssid.getBytes(StandardCharsets.UTF_8);
            if (ssid_bytes.length >= 1) {
                if (ssid_bytes.length <= 32) {
                    return true;
                }
            }
            Log.d(TAG, "softap SSID is defined as UTF-8 and it must be at least 1 byte and not more than 32 bytes");
            return false;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "softap config SSID verification failed: malformed string " + ssid);
            return false;
        }
    }

    private static boolean validateApConfigPreSharedKey(String preSharedKey) {
        if (preSharedKey.length() < 8 || preSharedKey.length() > 63) {
            Log.d(TAG, "softap network password string size must be at least 8 and no more than 63");
            return false;
        }
        try {
            preSharedKey.getBytes(StandardCharsets.UTF_8);
            return true;
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "softap network password verification failed: malformed string");
            return false;
        }
    }

    static boolean validateApWifiConfiguration(WifiConfiguration apConfig) {
        if (!validateApConfigSsid(apConfig.SSID)) {
            return false;
        }
        if (apConfig.allowedKeyManagement == null) {
            Log.d(TAG, "softap config key management bitset was null");
            return false;
        }
        String preSharedKey = apConfig.preSharedKey;
        boolean hasPreSharedKey = !TextUtils.isEmpty(preSharedKey);
        try {
            int authType = apConfig.getAuthType();
            if (authType == 0 || authType == 9) {
                if (hasPreSharedKey) {
                    Log.d(TAG, "open or OWE softap network should not have a password");
                    return false;
                }
            } else if (authType != 4 && authType != 8) {
                Log.d(TAG, "softap configs must either be open or WPA2 PSK or OWE or SAE networks");
                return false;
            } else if (!hasPreSharedKey) {
                Log.d(TAG, "softap network password must be set");
                return false;
            } else if (!validateApConfigPreSharedKey(preSharedKey)) {
                return false;
            }
            return true;
        } catch (IllegalStateException e) {
            Log.d(TAG, "Unable to get AuthType for softap config: " + e.getMessage());
            return false;
        }
    }

    private void startSoftApSettings() {
        this.mContext.startActivity(new Intent("com.android.settings.WIFI_TETHER_SETTINGS").addFlags(268435456));
    }

    /* access modifiers changed from: private */
    public void handleUserHotspotConfigTappedContent() {
        startSoftApSettings();
        ((NotificationManager) this.mContext.getSystemService(ThemeManagerConstants.COMPONENT_CODE_NOTIFICATION)).cancel(50);
    }

    private PendingIntent getPrivateBroadcast(String action) {
        return this.mFrameworkFacade.getBroadcast(this.mContext, 0, new Intent(action).setPackage(ThemeResources.FRAMEWORK_PACKAGE), MiuiWindowManager.LayoutParams.PRIVATE_FLAG_LOCKSCREEN_DISPALY_DESKTOP);
    }
}
