package com.android.server.wifi;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.location.LocationPolicyManager;
import android.net.wifi.IWifiManager;
import android.net.wifi.MiuiTetherDevice;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.WorkSource;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import com.android.server.am.ExtraActivityManagerService;
import com.miui.enterprise.RestrictionsHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import miui.util.FeatureParser;

public class MiuiWifiService {
    private static final int MAX_SHARED_KEY_REQUEST_SIZE = 128;
    protected static final int MSG_BASE = 1;
    private static final long SHARED_KEY_REQUEST_AGING_TIME_MS = 500;
    private static final String SUPPLICANT_CONFIG_FILE = "/data/misc/wifi/wpa_supplicant.conf";
    private static final String TAG = "MiuiWifiService";
    private static final String WIFI_CONFIG_HEADER = "network={";
    private static int mCurrentLatencyLevel = 1;
    private static Set<String> sObservedAccessPoints;
    private static final Map<Integer, Long> sPreSharedKeyRequest = new HashMap();
    private static MiuiWifiService sSelf;
    private Context mContext;
    private LocalHandler mLocalHandler;
    private HandlerThread mLocalThread;
    private MiuiWifiStateMachine mMiuiWifiStateMachine;

    private MiuiWifiService(Context context) {
        this.mContext = context;
        AppScanObserverService.make(this.mContext);
    }

    static MiuiWifiService make(Context context) {
        sSelf = new MiuiWifiService(context);
        return sSelf;
    }

    public static MiuiWifiService get() {
        MiuiWifiService miuiWifiService = sSelf;
        if (miuiWifiService != null) {
            return miuiWifiService;
        }
        throw new RuntimeException("MiuiWifiService has not be initialized");
    }

    public void systemReady() {
        this.mLocalThread = new HandlerThread(TAG);
        this.mLocalThread.start();
        this.mLocalHandler = new LocalHandler(this.mLocalThread.getLooper());
        AppScanObserverService.get().systemReady(this.mLocalThread);
        if (Build.VERSION.SDK_INT >= 26) {
            this.mMiuiWifiStateMachine = new MiuiWifiStateMachine(this.mContext, this.mLocalThread.getLooper());
            new PortalNetworkManager(this.mContext);
        }
    }

    private class LocalHandler extends Handler {
        LocalHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    public static boolean CheckIfBackgroundScanAllowed(Context ctx, WorkSource workSource) {
        int realOwner = workSource != null ? workSource.get(0) : Binder.getCallingUid();
        if (!UserHandle.isApp(realOwner)) {
            return true;
        }
        try {
            ctx.enforceCallingPermission("android.permission.ACCESS_COARSE_LOCATION", (String) null);
            return LocationPolicyManager.isAllowedByLocationPolicy(ctx, realOwner, 2);
        } catch (SecurityException e) {
            return true;
        }
    }

    public static boolean enforceChangePermission(Context context) {
        if (RestrictionsHelper.hasWifiRestriction(context)) {
            return false;
        }
        int uid = Binder.getCallingUid();
        if (!UserHandle.isApp(uid)) {
            return true;
        }
        AppOpsManager appOps = (AppOpsManager) context.getSystemService("appops");
        String packageName = ExtraActivityManagerService.getPackageNameByPid(Binder.getCallingPid());
        if (appOps == null || appOps.noteOp(10001, uid, packageName) == 0) {
            return true;
        }
        return false;
    }

    public static void updateLatencyLevel() {
        MiuiWifiServiceCompat.setLatencyLevel(mCurrentLatencyLevel);
    }

    public static boolean handleClientMessage(Message msg) {
        String wifiConfig;
        int i = msg.what;
        if (i != 155560) {
            if (i != 155573) {
                boolean z = false;
                switch (i) {
                    case 155553:
                        if (UserHandle.getAppId(msg.sendingUid) != 1000) {
                            replyToMessage(msg, 2, (Object) null);
                        }
                        WifiConfiguration config = msg.obj != null ? (WifiConfiguration) msg.obj : null;
                        if (config != null && (wifiConfig = Utils.getWifiConfigStringWithPassword(config)) != null) {
                            replyToMessage(msg, 1, Bundle.forPair("config", wifiConfig));
                            break;
                        } else {
                            replyToMessage(msg, 2, (Object) null);
                            break;
                        }
                        break;
                    case 155554:
                        Bundle apInfo = (Bundle) msg.obj;
                        if (apInfo != null) {
                            sObservedAccessPoints = new HashSet(apInfo.getStringArrayList("extra_aps"));
                            break;
                        }
                        break;
                    case 155555:
                        Bundle openApInfo = new Bundle();
                        openApInfo.putStringArrayList("extra_aps", new ArrayList(sObservedAccessPoints));
                        replyToMessage(msg, 1, openApInfo);
                        break;
                    case 155556:
                        MiuiWifiService miuiWifiService = get();
                        if (msg.arg1 == 1) {
                            z = true;
                        }
                        miuiWifiService.setWirelssConnectMode(z);
                        break;
                    default:
                        switch (i) {
                            case 155562:
                                if (UserHandle.getAppId(msg.sendingUid) == 1000) {
                                    String stations = WifiApManager.getConnectedStations();
                                    if (stations == null) {
                                        replyToMessage(msg, 2, (Object) null);
                                        break;
                                    } else {
                                        replyToMessage(msg, 1, Bundle.forPair("stations", stations));
                                        break;
                                    }
                                } else {
                                    replyToMessage(msg, 2, (Object) null);
                                    break;
                                }
                            case 155563:
                                mCurrentLatencyLevel = msg.arg1;
                                MiuiWifiServiceCompat.setLatencyLevel(msg.arg1);
                                break;
                            case 155564:
                                if (UserHandle.getAppId(msg.sendingUid) == 1000) {
                                    ArrayList<MiuiTetherDevice> staDev = new ArrayList<>(WifiApManager.getConnectedStationsNew());
                                    Bundle connectedDevices = new Bundle();
                                    connectedDevices.putParcelableArrayList("stations", staDev);
                                    replyToMessage(msg, 1, connectedDevices);
                                    break;
                                } else {
                                    replyToMessage(msg, 2, (Object) null);
                                    break;
                                }
                            default:
                                if (AppScanObserverService.get() != null) {
                                    return AppScanObserverService.get().handleClientMessage(msg);
                                }
                                return false;
                        }
                }
            } else {
                int freq = msg.arg1;
                Log.e(TAG, "set a Fixed Freq: " + freq);
                MiuiWifiP2pServiceCompat.discoverPeersOnTheFixedFreq(freq);
            }
        } else if (UserHandle.getAppId(msg.sendingUid) == 1001) {
            MiuiWifiServiceCompat.setSARLimit(msg.arg1);
        }
        return true;
    }

    public static boolean handleClientMessage(Message msg, ClientModeImpl clientModeImpl) {
        int i = msg.what;
        if (i == 155557) {
            clientModeImpl.sendMessage(Message.obtain(msg));
            return true;
        } else if (i != 155561) {
            return handleClientMessage(msg);
        } else {
            MiuiWifiNative.getInstance().setPowerSaveByReason(2, msg.arg1 == 1);
            return true;
        }
    }

    private void setWirelssConnectMode(boolean enabled) {
        if (setCompatibleMode(enabled)) {
            ((WifiManager) this.mContext.getSystemService("wifi")).disconnect();
        }
    }

    private static boolean setCompatibleMode(boolean enabled) {
        return MiuiWifiNative.getInstance().setCompatibleMode(enabled, SystemProperties.get("wifi.interface", "wlan0"));
    }

    public static void initCustomWirelessConfig(Context context) {
        initWirelessConnectMode(context);
        enableDataStallDetection(context);
    }

    public static boolean enableDataStallDetection(Context context) {
        return MiuiWifiNative.getInstance().enableDataStallDetection(TextUtils.equals("on", Settings.System.getString(context.getContentResolver(), "cloud_data_stall_detect_enabled")), SystemProperties.get("wifi.interface", "wlan0"));
    }

    public static void initWirelessConnectMode(Context context) {
        boolean z = false;
        if (FeatureParser.getBoolean("support_choose_connect_mode", false)) {
            int wirelessMode = Settings.System.getInt(context.getContentResolver(), "wireless_compatible_mode", 0);
            Settings.System.putInt(context.getContentResolver(), "wireless_compatible_mode", wirelessMode);
            if (wirelessMode == 0) {
                z = true;
            }
            setCompatibleMode(z);
        }
    }

    private static void replyToMessage(Message message, int arg1, Object obj) {
        try {
            Message reply = Message.obtain();
            reply.what = message.what;
            reply.arg1 = arg1;
            reply.obj = obj;
            message.replyTo.send(reply);
        } catch (RemoteException e) {
            Log.d(TAG, "replyToMessage Failed");
        }
    }

    public static void updateScanResults(Context context) {
        try {
            if (AppScanObserverService.get() != null) {
                AppScanObserverService.get().onNewScanResultAvailable();
            }
            boolean observedApChanged = false;
            if (sObservedAccessPoints != null) {
                Iterator<ScanResult> it = IWifiManager.Stub.asInterface(ServiceManager.getService("wifi")).getScanResults(context.getOpPackageName()).iterator();
                while (true) {
                    if (!it.hasNext()) {
                        break;
                    } else if (sObservedAccessPoints.contains(configKey(it.next()))) {
                        observedApChanged = true;
                        break;
                    }
                }
                if (observedApChanged) {
                    context.sendBroadcastAsUser(new Intent("android.net.wifi.observed_accesspionts_changed"), UserHandle.ALL);
                }
            }
        } catch (RemoteException e) {
            Log.e(TAG, "get ScanResult failed due to remoteException" + e);
        }
    }

    private static String configKey(ScanResult result) {
        String key = "\"" + result.SSID + "\"";
        if (result.capabilities.contains("WEP")) {
            key = key + "-WEP";
        }
        if (result.capabilities.contains("PSK")) {
            key = key + "-" + WifiConfiguration.KeyMgmt.strings[1];
        }
        if (result.capabilities.contains("EAP") || result.capabilities.contains("IEEE8021X")) {
            key = key + "-" + WifiConfiguration.KeyMgmt.strings[2];
        }
        if (!key.equals("\"" + result.SSID + "\"")) {
            return key;
        }
        return key + "-" + WifiConfiguration.KeyMgmt.strings[0];
    }
}
