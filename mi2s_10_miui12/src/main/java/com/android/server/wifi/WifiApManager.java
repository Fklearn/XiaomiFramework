package com.android.server.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.wifi.IApInterface;
import android.net.wifi.IWificond;
import android.net.wifi.MiuiTetherDevice;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import miui.telephony.phonenumber.Prefix;
import miui.util.FeatureParser;

class WifiApManager {
    private static final String ACTION_SAP_CONNECTED_DEV_INFO = "com.miui.action.ACTION_SAP_CONNECTED_DEV_INFO";
    private static final long CALL_BACK_DELAYED_TIME_MS = 100;
    private static final boolean DBG = false;
    private static final int DHCP_REQUEST_POLLING_INTERVAL = 100;
    private static final int DHCP_REQUEST_POLLING_MAX_TIMES = 5;
    private static final String HOTSPOT_DEVICE_ADDRESS = "sta_address";
    private static final String HOTSPOT_DEVICE_NAME = "sta_name";
    private static final String TAG = "WifiApManager";
    /* access modifiers changed from: private */
    public static HashMap<String, MiuiTetherDevice> mConnectedDeviceMap = new HashMap<>();
    private static String mIfaceName;
    /* access modifiers changed from: private */
    public static int mIpMode;
    /* access modifiers changed from: private */
    public static boolean mIs80211axSupported;
    /* access modifiers changed from: private */
    public static int mMaxStationNum;
    /* access modifiers changed from: private */
    public static String mOldConnectedStations;
    /* access modifiers changed from: private */
    public static String mVendorSpecific;
    /* access modifiers changed from: private */
    public String deviceAddress;
    /* access modifiers changed from: private */
    public String deviceName;
    private IApInterface mApInterface;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                String action = intent.getAction();
                if (WifiApManager.ACTION_SAP_CONNECTED_DEV_INFO.equals(action)) {
                    String unused = WifiApManager.this.deviceName = intent.getStringExtra(WifiApManager.HOTSPOT_DEVICE_NAME);
                    String unused2 = WifiApManager.this.deviceAddress = intent.getStringExtra(WifiApManager.HOTSPOT_DEVICE_ADDRESS);
                } else if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action)) {
                    int unused3 = WifiApManager.mIpMode = intent.getIntExtra("wifi_ap_mode", -1);
                    if (intent.getIntExtra("wifi_state", 0) == 13 && WifiApManager.this.mWifiManager == null) {
                        WifiApManager wifiApManager = WifiApManager.this;
                        WifiManager unused4 = wifiApManager.mWifiManager = (WifiManager) wifiApManager.mContext.getSystemService("wifi");
                        if (WifiApManager.this.mWifiManager != null) {
                            WifiApManager.this.mWifiManager.registerSoftApCallback(WifiApManager.this.mSoftApCallback, (Handler) null);
                        }
                    }
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mConnected;
    /* access modifiers changed from: private */
    public final Context mContext;
    private final Handler mHandler;
    /* access modifiers changed from: private */
    public Set<String> mHotSpotBlackSet;
    /* access modifiers changed from: private */
    public final WifiApNotificationManager mNotifManager;
    /* access modifiers changed from: private */
    public int mNumClients;
    /* access modifiers changed from: private */
    public Set<String> mOldConnectedStationsMac = new HashSet();
    /* access modifiers changed from: private */
    public WifiManager.SoftApCallback mSoftApCallback = new WifiManager.SoftApCallback() {
        public void onStateChanged(int state, int failureReason) {
            if (state == 13) {
                if (TextUtils.equals("qcom", FeatureParser.getString("vendor"))) {
                    SystemProperties.set("persist.vendor.fst.wifi.sap.interface", WifiApManager.this.mWifiNative.getSoftApInterfaceName());
                }
                WifiApManager.this.mNotifManager.showSoftApClientsNotification(WifiApManager.this.mNumClients);
                if (MiuiWifiNative.getInstance().connectToSap()) {
                    boolean unused = WifiApManager.this.mConnected = true;
                    WifiApManager.this.setHotSpotDenyMac();
                }
            } else if (state == 11) {
                WifiApManager.this.mNotifManager.clearSoftApClientsNotification();
                WifiApManager.this.mOldConnectedStationsMac.clear();
                String unused2 = WifiApManager.mOldConnectedStations = Prefix.EMPTY;
                WifiApManager.mConnectedDeviceMap.clear();
                int unused3 = WifiApManager.this.mNumClients = 0;
                if (WifiApManager.this.mConnected) {
                    MiuiWifiNative.getInstance().closeSapConnection();
                    boolean unused4 = WifiApManager.this.mConnected = false;
                }
            }
        }

        public void onStaConnected(String Macaddr, final int numClients) {
            WifiApManager.this.updateConnectedStations(Macaddr, true);
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    if (!WifiApManager.this.isLocalOnlyHotspot(WifiApManager.mIpMode) && TextUtils.equals("qcom", FeatureParser.getString("vendor"))) {
                        WifiApManager.this.updateNotification(numClients);
                    }
                }
            }, WifiApManager.CALL_BACK_DELAYED_TIME_MS);
        }

        public void onStaDisconnected(String Macaddr, int numClients) {
            WifiApManager.this.updateConnectedStations(Macaddr, false);
            if (!WifiApManager.this.isLocalOnlyHotspot(WifiApManager.mIpMode) && TextUtils.equals("qcom", FeatureParser.getString("vendor"))) {
                WifiApManager.this.updateNotification(numClients);
            }
        }

        public void onNumClientsChanged(int numClients) {
            if (!WifiApManager.this.isLocalOnlyHotspot(WifiApManager.mIpMode)) {
                if (WifiApManager.this.mNumClients == 0 && numClients == 1) {
                    WifiApManager.this.setHotSpotDenyMac();
                }
                if (!TextUtils.equals("qcom", FeatureParser.getString("vendor"))) {
                    int unused = WifiApManager.this.mNumClients = numClients;
                    WifiApManager.this.mNotifManager.showSoftApClientsNotification(WifiApManager.this.mNumClients);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public WifiManager mWifiManager;
    /* access modifiers changed from: private */
    public WifiNative mWifiNative;

    public WifiApManager(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
        this.mWifiNative = WifiInjector.getInstance().getWifiNative();
        registerHotSpotMaxNumChangedObserver();
        registerHotSpotBlackSetChangedObserver();
        registerHotSpotVendorSpecificChangedObserver();
        registerHotSpotAXSupportedChangedObserver();
        registerWifiApInfoChangedReceiver();
        this.mNotifManager = new WifiApNotificationManager(context, handler);
    }

    private void registerWifiApInfoChangedReceiver() {
        IntentFilter intentFilter = new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED");
        intentFilter.addAction(ACTION_SAP_CONNECTED_DEV_INFO);
        this.mContext.registerReceiver(this.mBroadcastReceiver, intentFilter, (String) null, this.mHandler);
    }

    private void registerHotSpotMaxNumChangedObserver() {
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("hotspot_max_station_num"), false, new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                int unused = WifiApManager.mMaxStationNum = MiuiSettings.System.getHotSpotMaxStationNum(WifiApManager.this.mContext);
            }
        }, -1);
        mMaxStationNum = MiuiSettings.System.getHotSpotMaxStationNum(this.mContext);
    }

    private void registerHotSpotAXSupportedChangedObserver() {
        ContentObserver observer = new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                boolean unused = WifiApManager.mIs80211axSupported = MiuiSettings.System.getBooleanForUser(WifiApManager.this.mContext.getContentResolver(), "hotspot_80211ax_support", false, -2);
            }
        };
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("hotspot_80211ax_support"), false, observer, -1);
        observer.onChange(false);
    }

    private void registerHotSpotBlackSetChangedObserver() {
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("hotspot_mac_black_set"), false, new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                WifiApManager wifiApManager = WifiApManager.this;
                Set unused = wifiApManager.mHotSpotBlackSet = MiuiSettings.System.getHotSpotMacBlackSet(wifiApManager.mContext);
                WifiApManager.this.setHotSpotDenyMac();
            }
        }, -1);
        this.mHotSpotBlackSet = MiuiSettings.System.getHotSpotMacBlackSet(this.mContext);
    }

    private void registerHotSpotVendorSpecificChangedObserver() {
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("hotspot_vendor_specific"), false, new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange) {
                String unused = WifiApManager.mVendorSpecific = MiuiSettings.System.getHotSpotVendorSpecific(WifiApManager.this.mContext);
            }
        }, -1);
        mVendorSpecific = MiuiSettings.System.getHotSpotVendorSpecific(this.mContext);
    }

    /* access modifiers changed from: private */
    public boolean isLocalOnlyHotspot(int ipMode) {
        if (ipMode == 2) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void updateNotification(int numClients) {
        int i = mMaxStationNum;
        if (i <= 0 || numClients <= i) {
            this.mNumClients = numClients;
            this.mNotifManager.showSoftApClientsNotification(this.mNumClients);
        }
    }

    /* access modifiers changed from: private */
    public void updateConnectedStations(String address, boolean isAdded) {
        updateConnectedStationsOld(address, isAdded);
        updateConnectedStationsNew(address, isAdded);
    }

    private void updateConnectedStationsOld(String mac, boolean isConnected) {
        if (mac != null && mac.length() > 0) {
            if (isConnected) {
                this.mOldConnectedStationsMac.add(mac);
            } else {
                this.mOldConnectedStationsMac.remove(mac);
            }
            StringBuilder macString = new StringBuilder();
            for (String string : this.mOldConnectedStationsMac) {
                macString.append(string);
                macString.append(";");
            }
            mOldConnectedStations = macString.toString();
        }
    }

    private void updateConnectedStationsNew(String address, boolean isConnected) {
        if (address != null && address.length() > 0) {
            MiuiTetherDevice device = new MiuiTetherDevice(address, isConnected);
            if (device.deviceState == 1) {
                if (readStaDeviceInfoFromBroadcast(device)) {
                    mConnectedDeviceMap.put(device.deviceAddress, device);
                } else {
                    new DhcpRequestThread(this, device, 100, 5).start();
                }
            } else if (device.deviceState == 0) {
                mConnectedDeviceMap.remove(device.deviceAddress);
            }
        }
    }

    /* access modifiers changed from: private */
    public void setHotSpotDenyMac() {
        if (this.mConnected && this.mHotSpotBlackSet != null) {
            MiuiWifiNative.getInstance().setHotSpotDenyMac(this.mHotSpotBlackSet);
        }
    }

    public static void updateHostapdConfig(WifiNative wifiNative) {
        if (mMaxStationNum > 0) {
            wifiNative.setHostapdParams("softap qccmd set max_num_sta=" + mMaxStationNum);
        }
        String str = mVendorSpecific;
        if (str == null) {
            mVendorSpecific = "DD0A0017F206010103010000";
        } else if (str.length() == 0) {
            mVendorSpecific = "dd0411223301";
        }
        wifiNative.setHostapdParams("softap qccmd set vendor_elements=" + mVendorSpecific);
        StringBuilder sb = new StringBuilder();
        sb.append("softap qccmd set ieee80211ax=");
        sb.append(mIs80211axSupported ? "1" : "0");
        wifiNative.setHostapdParams(sb.toString());
    }

    private static class DhcpRequestThread extends Thread {
        private MiuiTetherDevice mDevice;
        private int mInterval;
        private int mMaxTimes;
        private final WifiApManager mWifiApManager;

        public DhcpRequestThread(WifiApManager softap, MiuiTetherDevice device, int interval, int maxTimes) {
            super("SoftAp");
            this.mWifiApManager = softap;
            this.mInterval = interval;
            this.mMaxTimes = maxTimes;
            this.mDevice = device;
        }

        public void run() {
            while (true) {
                try {
                    if (this.mMaxTimes <= 0) {
                        break;
                    } else if (this.mWifiApManager.readStaDeviceInfoFromBroadcast(this.mDevice)) {
                        break;
                    } else {
                        this.mMaxTimes--;
                        Thread.sleep((long) this.mInterval);
                    }
                } catch (Exception ex) {
                    Log.e(WifiApManager.TAG, "Polling " + this.mDevice.deviceAddress + "error" + ex);
                }
            }
            WifiApManager wifiApManager = this.mWifiApManager;
            WifiApManager.mConnectedDeviceMap.put(this.mDevice.deviceAddress, this.mDevice);
        }
    }

    /* access modifiers changed from: private */
    public boolean readStaDeviceInfoFromBroadcast(MiuiTetherDevice device) {
        String str = this.deviceAddress;
        if (str == null || !str.equals(device.deviceAddress)) {
            return false;
        }
        String str2 = this.deviceName;
        if (str2 != null && !str2.equals("*")) {
            device.deviceName = this.deviceName;
        }
        return true;
    }

    public static List<MiuiTetherDevice> getConnectedStationsNew() {
        List<MiuiTetherDevice> connectedStationsList = new ArrayList<>();
        for (String key : mConnectedDeviceMap.keySet()) {
            connectedStationsList.add(mConnectedDeviceMap.get(key));
        }
        return connectedStationsList;
    }

    public static String getConnectedStations() {
        return mOldConnectedStations;
    }

    public static String buildSoftapdIfaceAndConf(String iface) {
        if (TextUtils.equals("qcom", FeatureParser.getString("vendor"))) {
            SystemProperties.set("persist.vendor.fst.wifi.sap.interface", iface);
        }
        StringBuilder ifaceAndConf = new StringBuilder();
        mIfaceName = iface;
        ifaceAndConf.append(iface);
        if (mMaxStationNum > 0) {
            ifaceAndConf.append("\nmax_num_sta=" + mMaxStationNum);
        }
        if (mVendorSpecific == null) {
            mVendorSpecific = "DD0A0017F206010103010000";
        }
        if (mVendorSpecific.length() > 0) {
            ifaceAndConf.append("\nvendor_elements=" + mVendorSpecific);
        }
        return ifaceAndConf.toString();
    }

    private IApInterface getApInterface() {
        IWificond wificond = WifiInjector.getInstance().makeWificond();
        List<IBinder> apInterfaces = null;
        if (wificond != null) {
            try {
                apInterfaces = wificond.GetApInterfaces();
            } catch (RemoteException e) {
                Log.e(TAG, "getApInterfaces " + e);
            }
        }
        IApInterface apInterface = null;
        if (apInterfaces != null) {
            for (IBinder binder : apInterfaces) {
                try {
                    apInterface = IApInterface.Stub.asInterface(binder);
                    if (TextUtils.equals(this.mWifiNative.getSoftApInterfaceName(), apInterface.getInterfaceName())) {
                        return apInterface;
                    }
                } catch (RemoteException e2) {
                    Log.e(TAG, "getInterfaceName " + e2);
                }
            }
        }
        return apInterface;
    }
}
