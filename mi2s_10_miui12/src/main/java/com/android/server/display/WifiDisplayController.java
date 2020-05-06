package com.android.server.display;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.hardware.display.WifiDisplay;
import android.hardware.display.WifiDisplaySessionInfo;
import android.media.RemoteDisplay;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pWfdInfo;
import android.os.Handler;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Slog;
import android.view.Surface;
import com.android.internal.util.DumpUtils;
import com.android.server.job.controllers.JobStatus;
import java.io.PrintWriter;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Objects;

final class WifiDisplayController implements DumpUtils.Dump {
    private static final int CONNECTION_TIMEOUT_SECONDS = 30;
    private static final int CONNECT_MAX_RETRIES = 3;
    private static final int CONNECT_RETRY_DELAY_MILLIS = 500;
    /* access modifiers changed from: private */
    public static final boolean DEBUG = SystemProperties.getBoolean("persist.vendor.debug.wfdcdbg", false);
    private static final boolean DEBUGV = SystemProperties.getBoolean("persist.vendor.debug.wfdcdbgv", false);
    private static final int DEFAULT_CONTROL_PORT = 7236;
    private static final int DISCOVER_PEERS_INTERVAL_MILLIS = 10000;
    private static final int MAX_THROUGHPUT = 50;
    private static final int RTSP_TIMEOUT_SECONDS = 30;
    private static final int RTSP_TIMEOUT_SECONDS_CERT_MODE = 120;
    private static final String TAG = "WifiDisplayController";
    private WifiDisplay mAdvertisedDisplay;
    private int mAdvertisedDisplayFlags;
    private int mAdvertisedDisplayHeight;
    private Surface mAdvertisedDisplaySurface;
    private int mAdvertisedDisplayWidth;
    /* access modifiers changed from: private */
    public final ArrayList<WifiP2pDevice> mAvailableWifiDisplayPeers = new ArrayList<>();
    /* access modifiers changed from: private */
    public WifiP2pDevice mCancelingDevice;
    /* access modifiers changed from: private */
    public WifiP2pDevice mConnectedDevice;
    /* access modifiers changed from: private */
    public WifiP2pGroup mConnectedDeviceGroupInfo;
    /* access modifiers changed from: private */
    public WifiP2pDevice mConnectingDevice;
    /* access modifiers changed from: private */
    public int mConnectionRetriesLeft;
    /* access modifiers changed from: private */
    public final Runnable mConnectionTimeout = new Runnable() {
        public void run() {
            if (WifiDisplayController.this.mConnectingDevice != null && WifiDisplayController.this.mConnectingDevice == WifiDisplayController.this.mDesiredDevice) {
                Slog.i(WifiDisplayController.TAG, "Timed out waiting for Wifi display connection after 30 seconds: " + WifiDisplayController.this.mConnectingDevice.deviceName);
                WifiDisplayController.this.handleConnectionFailure(true);
            }
        }
    };
    private final Context mContext;
    /* access modifiers changed from: private */
    public WifiP2pDevice mDesiredDevice;
    /* access modifiers changed from: private */
    public WifiP2pDevice mDisconnectingDevice;
    private final Runnable mDiscoverPeers = new Runnable() {
        public void run() {
            WifiDisplayController.this.tryDiscoverPeers();
        }
    };
    /* access modifiers changed from: private */
    public boolean mDiscoverPeersInProgress;
    /* access modifiers changed from: private */
    public Object mExtRemoteDisplay;
    /* access modifiers changed from: private */
    public final Handler mHandler;
    /* access modifiers changed from: private */
    public final Listener mListener;
    /* access modifiers changed from: private */
    public NetworkInfo mNetworkInfo;
    /* access modifiers changed from: private */
    public RemoteDisplay mRemoteDisplay;
    /* access modifiers changed from: private */
    public boolean mRemoteDisplayConnected;
    private String mRemoteDisplayInterface;
    /* access modifiers changed from: private */
    public final Runnable mRtspTimeout = new Runnable() {
        public void run() {
            if (WifiDisplayController.this.mConnectedDevice == null) {
                return;
            }
            if (!(WifiDisplayController.this.mRemoteDisplay == null && WifiDisplayController.this.mExtRemoteDisplay == null) && !WifiDisplayController.this.mRemoteDisplayConnected) {
                Slog.i(WifiDisplayController.TAG, "Timed out waiting for Wifi display RTSP connection after 30 seconds: " + WifiDisplayController.this.mConnectedDevice.deviceName);
                WifiDisplayController.this.handleConnectionFailure(true);
            }
        }
    };
    private boolean mScanRequested;
    /* access modifiers changed from: private */
    public WifiP2pDevice mThisDevice;
    /* access modifiers changed from: private */
    public boolean mWfdEnabled;
    /* access modifiers changed from: private */
    public boolean mWfdEnabling;
    /* access modifiers changed from: private */
    public boolean mWifiDisplayCertMode;
    private boolean mWifiDisplayOnSetting;
    private int mWifiDisplayWpsConfig = 4;
    private WifiP2pManager.Channel mWifiP2pChannel;
    private boolean mWifiP2pEnabled;
    /* access modifiers changed from: private */
    public final WifiP2pManager mWifiP2pManager;
    private final BroadcastReceiver mWifiP2pReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("android.net.wifi.p2p.STATE_CHANGED")) {
                boolean enabled = true;
                if (intent.getIntExtra("wifi_p2p_state", 1) != 2) {
                    enabled = false;
                }
                if (WifiDisplayController.DEBUG) {
                    Slog.d(WifiDisplayController.TAG, "Received WIFI_P2P_STATE_CHANGED_ACTION: enabled=" + enabled);
                }
                WifiDisplayController.this.handleStateChanged(enabled);
            } else if (action.equals("android.net.wifi.p2p.PEERS_CHANGED")) {
                if (WifiDisplayController.DEBUG) {
                    Slog.d(WifiDisplayController.TAG, "Received WIFI_P2P_PEERS_CHANGED_ACTION.");
                }
                WifiDisplayController.this.handlePeersChanged();
            } else if (action.equals("android.net.wifi.p2p.CONNECTION_STATE_CHANGE")) {
                NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                if (WifiDisplayController.DEBUG) {
                    Slog.d(WifiDisplayController.TAG, "Received WIFI_P2P_CONNECTION_CHANGED_ACTION: networkInfo=" + networkInfo);
                }
                WifiDisplayController.this.handleConnectionChanged(networkInfo);
            } else if (action.equals("android.net.wifi.p2p.THIS_DEVICE_CHANGED")) {
                WifiP2pDevice unused = WifiDisplayController.this.mThisDevice = (WifiP2pDevice) intent.getParcelableExtra("wifiP2pDevice");
                if (WifiDisplayController.DEBUG) {
                    Slog.d(WifiDisplayController.TAG, "Received WIFI_P2P_THIS_DEVICE_CHANGED_ACTION: mThisDevice= " + WifiDisplayController.this.mThisDevice);
                }
            }
        }
    };

    public interface Listener {
        void onDisplayChanged(WifiDisplay wifiDisplay);

        void onDisplayConnected(WifiDisplay wifiDisplay, Surface surface, int i, int i2, int i3);

        void onDisplayConnecting(WifiDisplay wifiDisplay);

        void onDisplayConnectionFailed();

        void onDisplayDisconnected();

        void onDisplaySessionInfo(WifiDisplaySessionInfo wifiDisplaySessionInfo);

        void onFeatureStateChanged(int i);

        void onScanFinished();

        void onScanResults(WifiDisplay[] wifiDisplayArr);

        void onScanStarted();
    }

    static /* synthetic */ int access$3520(WifiDisplayController x0, int x1) {
        int i = x0.mConnectionRetriesLeft - x1;
        x0.mConnectionRetriesLeft = i;
        return i;
    }

    public WifiDisplayController(Context context, Handler handler, Listener listener) {
        this.mContext = context;
        this.mHandler = handler;
        this.mListener = listener;
        this.mWifiP2pManager = (WifiP2pManager) context.getSystemService("wifip2p");
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.wifi.p2p.STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.p2p.PEERS_CHANGED");
        intentFilter.addAction("android.net.wifi.p2p.CONNECTION_STATE_CHANGE");
        intentFilter.addAction("android.net.wifi.p2p.THIS_DEVICE_CHANGED");
        context.registerReceiver(this.mWifiP2pReceiver, intentFilter, (String) null, this.mHandler);
        ContentObserver settingsObserver = new ContentObserver(this.mHandler) {
            public void onChange(boolean selfChange, Uri uri) {
                WifiDisplayController.this.updateSettings();
            }
        };
        ContentResolver resolver = this.mContext.getContentResolver();
        resolver.registerContentObserver(Settings.Global.getUriFor("wifi_display_on"), false, settingsObserver);
        resolver.registerContentObserver(Settings.Global.getUriFor("wifi_display_certification_on"), false, settingsObserver);
        resolver.registerContentObserver(Settings.Global.getUriFor("wifi_display_wps_config"), false, settingsObserver);
        updateSettings();
        WifiDisplayControllerInjector.registerProjectionReceiver(this.mContext, this.mHandler, this);
    }

    /* access modifiers changed from: private */
    public WifiP2pManager.Channel getWifiP2pChannel() {
        if (this.mWifiP2pChannel == null) {
            this.mWifiP2pChannel = this.mWifiP2pManager.initialize(this.mContext, this.mHandler.getLooper(), (WifiP2pManager.ChannelListener) null);
            if (DEBUG) {
                Slog.d(TAG, "Creating WifiP2pChannel");
            }
        }
        return this.mWifiP2pChannel;
    }

    /* access modifiers changed from: private */
    public void updateSettings() {
        ContentResolver resolver = this.mContext.getContentResolver();
        boolean z = false;
        this.mWifiDisplayOnSetting = Settings.Global.getInt(resolver, "wifi_display_on", 0) != 0;
        if (Settings.Global.getInt(resolver, "wifi_display_certification_on", 0) != 0) {
            z = true;
        }
        this.mWifiDisplayCertMode = z;
        this.mWifiDisplayWpsConfig = 4;
        if (this.mWifiDisplayCertMode) {
            this.mWifiDisplayWpsConfig = Settings.Global.getInt(resolver, "wifi_display_wps_config", 4);
        }
        updateWfdEnableState();
    }

    public void dump(PrintWriter pw, String prefix) {
        pw.println("mWifiDisplayOnSetting=" + this.mWifiDisplayOnSetting);
        pw.println("mWifiP2pEnabled=" + this.mWifiP2pEnabled);
        pw.println("mWfdEnabled=" + this.mWfdEnabled);
        pw.println("mWfdEnabling=" + this.mWfdEnabling);
        pw.println("mNetworkInfo=" + this.mNetworkInfo);
        pw.println("mScanRequested=" + this.mScanRequested);
        pw.println("mDiscoverPeersInProgress=" + this.mDiscoverPeersInProgress);
        pw.println("mDesiredDevice=" + describeWifiP2pDevice(this.mDesiredDevice));
        pw.println("mConnectingDisplay=" + describeWifiP2pDevice(this.mConnectingDevice));
        pw.println("mDisconnectingDisplay=" + describeWifiP2pDevice(this.mDisconnectingDevice));
        pw.println("mCancelingDisplay=" + describeWifiP2pDevice(this.mCancelingDevice));
        pw.println("mConnectedDevice=" + describeWifiP2pDevice(this.mConnectedDevice));
        pw.println("mConnectionRetriesLeft=" + this.mConnectionRetriesLeft);
        pw.println("mRemoteDisplay=" + this.mRemoteDisplay);
        pw.println("mRemoteDisplayInterface=" + this.mRemoteDisplayInterface);
        pw.println("mRemoteDisplayConnected=" + this.mRemoteDisplayConnected);
        pw.println("mAdvertisedDisplay=" + this.mAdvertisedDisplay);
        pw.println("mAdvertisedDisplaySurface=" + this.mAdvertisedDisplaySurface);
        pw.println("mAdvertisedDisplayWidth=" + this.mAdvertisedDisplayWidth);
        pw.println("mAdvertisedDisplayHeight=" + this.mAdvertisedDisplayHeight);
        pw.println("mAdvertisedDisplayFlags=" + this.mAdvertisedDisplayFlags);
        pw.println("mAvailableWifiDisplayPeers: size=" + this.mAvailableWifiDisplayPeers.size());
        Iterator<WifiP2pDevice> it = this.mAvailableWifiDisplayPeers.iterator();
        while (it.hasNext()) {
            pw.println("  " + describeWifiP2pDevice(it.next()));
        }
    }

    private void dump() {
        Slog.d(TAG, "mWifiDisplayOnSetting=" + this.mWifiDisplayOnSetting);
        Slog.d(TAG, "mWifiP2pEnabled=" + this.mWifiP2pEnabled);
        Slog.d(TAG, "mWfdEnabled=" + this.mWfdEnabled);
        Slog.d(TAG, "mWfdEnabling=" + this.mWfdEnabling);
        Slog.d(TAG, "mNetworkInfo=" + this.mNetworkInfo);
        Slog.d(TAG, "mScanRequested=" + this.mScanRequested);
        Slog.d(TAG, "mDiscoverPeersInProgress=" + this.mDiscoverPeersInProgress);
        Slog.d(TAG, "mDesiredDevice=" + describeWifiP2pDevice(this.mDesiredDevice));
        Slog.d(TAG, "mConnectingDisplay=" + describeWifiP2pDevice(this.mConnectingDevice));
        Slog.d(TAG, "mDisconnectingDisplay=" + describeWifiP2pDevice(this.mDisconnectingDevice));
        Slog.d(TAG, "mCancelingDisplay=" + describeWifiP2pDevice(this.mCancelingDevice));
        Slog.d(TAG, "mConnectedDevice=" + describeWifiP2pDevice(this.mConnectedDevice));
        Slog.d(TAG, "mConnectionRetriesLeft=" + this.mConnectionRetriesLeft);
        Slog.d(TAG, "mRemoteDisplay=" + this.mRemoteDisplay);
        Slog.d(TAG, "mRemoteDisplayInterface=" + this.mRemoteDisplayInterface);
        Slog.d(TAG, "mRemoteDisplayConnected=" + this.mRemoteDisplayConnected);
        Slog.d(TAG, "mAdvertisedDisplay=" + this.mAdvertisedDisplay);
        Slog.d(TAG, "mAdvertisedDisplaySurface=" + this.mAdvertisedDisplaySurface);
        Slog.d(TAG, "mAdvertisedDisplayWidth=" + this.mAdvertisedDisplayWidth);
        Slog.d(TAG, "mAdvertisedDisplayHeight=" + this.mAdvertisedDisplayHeight);
        Slog.d(TAG, "mAdvertisedDisplayFlags=" + this.mAdvertisedDisplayFlags);
        Slog.d(TAG, "mAvailableWifiDisplayPeers: size=" + this.mAvailableWifiDisplayPeers.size());
        Iterator<WifiP2pDevice> it = this.mAvailableWifiDisplayPeers.iterator();
        while (it.hasNext()) {
            Slog.d(TAG, "  " + describeWifiP2pDevice(it.next()));
        }
    }

    public void requestStartScan() {
        if (!this.mScanRequested) {
            this.mScanRequested = true;
            updateScanState();
        }
    }

    public void requestStopScan() {
        if (this.mScanRequested) {
            this.mScanRequested = false;
            updateScanState();
        }
    }

    public void requestConnect(String address) {
        Iterator<WifiP2pDevice> it = this.mAvailableWifiDisplayPeers.iterator();
        while (it.hasNext()) {
            WifiP2pDevice device = it.next();
            if (device.deviceAddress.equals(address)) {
                connect(device);
            }
        }
    }

    public void requestPause() {
        RemoteDisplay remoteDisplay = this.mRemoteDisplay;
        if (remoteDisplay != null) {
            remoteDisplay.pause();
        }
    }

    public void requestResume() {
        RemoteDisplay remoteDisplay = this.mRemoteDisplay;
        if (remoteDisplay != null) {
            remoteDisplay.resume();
        }
    }

    public void requestDisconnect() {
        disconnect();
    }

    private void updateWfdEnableState() {
        if (!this.mWifiDisplayOnSetting || !this.mWifiP2pEnabled) {
            if (this.mWfdEnabled || this.mWfdEnabling) {
                WifiP2pWfdInfo wfdInfo = new WifiP2pWfdInfo();
                wfdInfo.setWfdEnabled(false);
                this.mWifiP2pManager.setWFDInfo(getWifiP2pChannel(), wfdInfo, new WifiP2pManager.ActionListener() {
                    public void onSuccess() {
                        if (WifiDisplayController.DEBUG) {
                            Slog.d(WifiDisplayController.TAG, "Successfully set WFD info.");
                        }
                    }

                    public void onFailure(int reason) {
                        if (WifiDisplayController.DEBUG) {
                            Slog.d(WifiDisplayController.TAG, "Failed to set WFD info with reason " + reason + ".");
                        }
                    }
                });
            }
            this.mWfdEnabling = false;
            this.mWfdEnabled = false;
            reportFeatureState();
            updateScanState();
            disconnect();
        } else if (!this.mWfdEnabled && !this.mWfdEnabling) {
            this.mWfdEnabling = true;
            WifiP2pWfdInfo wfdInfo2 = new WifiP2pWfdInfo();
            wfdInfo2.setWfdEnabled(true);
            wfdInfo2.setDeviceType(0);
            wfdInfo2.setSessionAvailable(true);
            wfdInfo2.setControlPort(DEFAULT_CONTROL_PORT);
            wfdInfo2.setMaxThroughput(50);
            this.mWifiP2pManager.setWFDInfo(getWifiP2pChannel(), wfdInfo2, new WifiP2pManager.ActionListener() {
                public void onSuccess() {
                    if (WifiDisplayController.DEBUG) {
                        Slog.d(WifiDisplayController.TAG, "Successfully set WFD info.");
                    }
                    if (WifiDisplayController.this.mWfdEnabling) {
                        boolean unused = WifiDisplayController.this.mWfdEnabling = false;
                        boolean unused2 = WifiDisplayController.this.mWfdEnabled = true;
                        WifiDisplayController.this.reportFeatureState();
                        WifiDisplayController.this.updateScanState();
                    }
                }

                public void onFailure(int reason) {
                    if (WifiDisplayController.DEBUG) {
                        Slog.d(WifiDisplayController.TAG, "Failed to set WFD info with reason " + reason + ".");
                    }
                    boolean unused = WifiDisplayController.this.mWfdEnabling = false;
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void reportFeatureState() {
        final int featureState = computeFeatureState();
        this.mHandler.post(new Runnable() {
            public void run() {
                WifiDisplayController.this.mListener.onFeatureStateChanged(featureState);
            }
        });
    }

    private int computeFeatureState() {
        if (!this.mWifiP2pEnabled) {
            return 1;
        }
        if (this.mWifiDisplayOnSetting) {
            return 3;
        }
        return 2;
    }

    /* access modifiers changed from: private */
    public void updateScanState() {
        if (this.mScanRequested && this.mWfdEnabled && this.mDesiredDevice == null && this.mConnectedDevice == null && this.mDisconnectingDevice == null) {
            if (!this.mDiscoverPeersInProgress) {
                Slog.i(TAG, "Starting Wifi display scan.");
                this.mDiscoverPeersInProgress = true;
                handleScanStarted();
                tryDiscoverPeers();
            }
        } else if (this.mDiscoverPeersInProgress) {
            this.mHandler.removeCallbacks(this.mDiscoverPeers);
            WifiP2pDevice wifiP2pDevice = this.mDesiredDevice;
            if (wifiP2pDevice == null || wifiP2pDevice == this.mConnectedDevice) {
                Slog.i(TAG, "Stopping Wifi display scan.");
                this.mDiscoverPeersInProgress = false;
                stopPeerDiscovery();
                handleScanFinished();
            }
        }
    }

    /* access modifiers changed from: private */
    public void tryDiscoverPeers() {
        this.mWifiP2pManager.discoverPeers(getWifiP2pChannel(), new WifiP2pManager.ActionListener() {
            public void onSuccess() {
                if (WifiDisplayController.DEBUG) {
                    Slog.d(WifiDisplayController.TAG, "Discover peers succeeded.  Requesting peers now.");
                }
                if (WifiDisplayController.this.mDiscoverPeersInProgress) {
                    WifiDisplayController.this.requestPeers();
                }
            }

            public void onFailure(int reason) {
                if (WifiDisplayController.DEBUG) {
                    Slog.d(WifiDisplayController.TAG, "Discover peers failed with reason " + reason + ".");
                }
            }
        });
        this.mHandler.postDelayed(this.mDiscoverPeers, JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY);
    }

    private void stopPeerDiscovery() {
        this.mWifiP2pManager.stopPeerDiscovery(getWifiP2pChannel(), new WifiP2pManager.ActionListener() {
            public void onSuccess() {
                if (WifiDisplayController.DEBUG) {
                    Slog.d(WifiDisplayController.TAG, "Stop peer discovery succeeded.");
                }
            }

            public void onFailure(int reason) {
                if (WifiDisplayController.DEBUG) {
                    Slog.d(WifiDisplayController.TAG, "Stop peer discovery failed with reason " + reason + ".");
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void requestPeers() {
        this.mWifiP2pManager.requestPeers(getWifiP2pChannel(), new WifiP2pManager.PeerListListener() {
            public void onPeersAvailable(WifiP2pDeviceList peers) {
                if (WifiDisplayController.DEBUG) {
                    Slog.d(WifiDisplayController.TAG, "Received list of peers.");
                }
                WifiDisplayController.this.mAvailableWifiDisplayPeers.clear();
                for (WifiP2pDevice device : peers.getDeviceList()) {
                    if (WifiDisplayController.DEBUG) {
                        Slog.d(WifiDisplayController.TAG, "  " + WifiDisplayController.describeWifiP2pDevice(device));
                    }
                    if (WifiDisplayController.isWifiDisplay(device)) {
                        WifiDisplayController.this.mAvailableWifiDisplayPeers.add(device);
                    }
                }
                if (WifiDisplayController.this.mDiscoverPeersInProgress) {
                    WifiDisplayController.this.handleScanResults();
                }
            }
        });
    }

    private void handleScanStarted() {
        this.mHandler.post(new Runnable() {
            public void run() {
                WifiDisplayController.this.mListener.onScanStarted();
            }
        });
    }

    /* access modifiers changed from: private */
    public void handleScanResults() {
        int count = this.mAvailableWifiDisplayPeers.size();
        final WifiDisplay[] displays = (WifiDisplay[]) WifiDisplay.CREATOR.newArray(count);
        for (int i = 0; i < count; i++) {
            WifiP2pDevice device = this.mAvailableWifiDisplayPeers.get(i);
            displays[i] = createWifiDisplay(device);
            updateDesiredDevice(device);
        }
        this.mHandler.post(new Runnable() {
            public void run() {
                WifiDisplayController.this.mListener.onScanResults(displays);
            }
        });
    }

    private void handleScanFinished() {
        this.mHandler.post(new Runnable() {
            public void run() {
                WifiDisplayController.this.mListener.onScanFinished();
            }
        });
    }

    private void updateDesiredDevice(WifiP2pDevice device) {
        String address = device.deviceAddress;
        WifiP2pDevice wifiP2pDevice = this.mDesiredDevice;
        if (wifiP2pDevice != null && wifiP2pDevice.deviceAddress.equals(address)) {
            if (DEBUG) {
                Slog.d(TAG, "updateDesiredDevice: new information " + describeWifiP2pDevice(device));
            }
            this.mDesiredDevice.update(device);
            WifiDisplay wifiDisplay = this.mAdvertisedDisplay;
            if (wifiDisplay != null && wifiDisplay.getDeviceAddress().equals(address)) {
                readvertiseDisplay(createWifiDisplay(this.mDesiredDevice));
            }
        }
    }

    private void connect(WifiP2pDevice device) {
        WifiP2pDevice wifiP2pDevice = this.mDesiredDevice;
        if (wifiP2pDevice == null || wifiP2pDevice.deviceAddress.equals(device.deviceAddress)) {
            WifiP2pDevice wifiP2pDevice2 = this.mConnectedDevice;
            if (wifiP2pDevice2 == null || wifiP2pDevice2.deviceAddress.equals(device.deviceAddress) || this.mDesiredDevice != null) {
                if (!this.mWfdEnabled) {
                    Slog.i(TAG, "Ignoring request to connect to Wifi display because the  feature is currently disabled: " + device.deviceName);
                } else if (handlePreExistingConnection(device)) {
                    Slog.i(TAG, "already handle the preexisting p2p connection status");
                } else {
                    this.mDesiredDevice = device;
                    this.mConnectionRetriesLeft = 3;
                    updateConnection();
                }
            } else if (DEBUG) {
                Slog.d(TAG, "connect: nothing to do, already connected to " + describeWifiP2pDevice(device) + " and not part way through connecting to a different device.");
            }
        } else if (DEBUG) {
            Slog.d(TAG, "connect: nothing to do, already connecting to " + describeWifiP2pDevice(device));
        }
    }

    /* access modifiers changed from: private */
    public void disconnect() {
        this.mDesiredDevice = null;
        updateConnection();
    }

    /* access modifiers changed from: private */
    public void retryConnection() {
        this.mDesiredDevice = new WifiP2pDevice(this.mDesiredDevice);
        updateConnection();
    }

    /* access modifiers changed from: private */
    public void updateConnection() {
        if (DEBUGV) {
            StackTraceElement[] st = Thread.currentThread().getStackTrace();
            int i = 2;
            while (i < st.length && i < 5) {
                Slog.i(TAG, st[i].toString());
                i++;
            }
            dump();
        }
        updateScanState();
        if (!((this.mRemoteDisplay == null && this.mExtRemoteDisplay == null) || this.mConnectedDevice == this.mDesiredDevice) || (this.mRemoteDisplayInterface != null && this.mConnectedDevice == null)) {
            Slog.i(TAG, "Stopped listening for RTSP connection on " + this.mRemoteDisplayInterface);
            RemoteDisplay remoteDisplay = this.mRemoteDisplay;
            if (remoteDisplay != null) {
                remoteDisplay.dispose();
            } else {
                Object obj = this.mExtRemoteDisplay;
                if (obj != null) {
                    ExtendedRemoteDisplayHelper.dispose(obj);
                }
            }
            this.mExtRemoteDisplay = null;
            this.mRemoteDisplay = null;
            this.mRemoteDisplayInterface = null;
            this.mHandler.removeCallbacks(this.mRtspTimeout);
            this.mWifiP2pManager.setMiracastMode(0);
            unadvertiseDisplay();
        }
        if (!this.mRemoteDisplayConnected && this.mDisconnectingDevice == null) {
            WifiP2pDevice wifiP2pDevice = this.mConnectedDevice;
            if (wifiP2pDevice != null && wifiP2pDevice != this.mDesiredDevice) {
                Slog.i(TAG, "Disconnecting from Wifi display: " + this.mConnectedDevice.deviceName);
                this.mDisconnectingDevice = this.mConnectedDevice;
                this.mConnectedDevice = null;
                this.mConnectedDeviceGroupInfo = null;
                unadvertiseDisplay();
                final WifiP2pDevice oldDevice = this.mDisconnectingDevice;
                this.mWifiP2pManager.removeGroup(getWifiP2pChannel(), new WifiP2pManager.ActionListener() {
                    public void onSuccess() {
                        Slog.i(WifiDisplayController.TAG, "Disconnected from Wifi display: " + oldDevice.deviceName);
                        next();
                    }

                    public void onFailure(int reason) {
                        Slog.i(WifiDisplayController.TAG, "Failed to disconnect from Wifi display: " + oldDevice.deviceName + ", reason=" + reason);
                        next();
                    }

                    private void next() {
                        if (WifiDisplayController.this.mDisconnectingDevice == oldDevice) {
                            WifiP2pDevice unused = WifiDisplayController.this.mDisconnectingDevice = null;
                            WifiDisplayController.this.updateConnection();
                        }
                    }
                });
            } else if (this.mCancelingDevice == null) {
                WifiP2pDevice wifiP2pDevice2 = this.mConnectingDevice;
                if (wifiP2pDevice2 != null && wifiP2pDevice2 != this.mDesiredDevice) {
                    Slog.i(TAG, "Canceling connection to Wifi display: " + this.mConnectingDevice.deviceName);
                    this.mCancelingDevice = this.mConnectingDevice;
                    this.mConnectingDevice = null;
                    unadvertiseDisplay();
                    this.mHandler.removeCallbacks(this.mConnectionTimeout);
                    final WifiP2pDevice oldDevice2 = this.mCancelingDevice;
                    this.mWifiP2pManager.cancelConnect(getWifiP2pChannel(), new WifiP2pManager.ActionListener() {
                        public void onSuccess() {
                            Slog.i(WifiDisplayController.TAG, "Canceled connection to Wifi display: " + oldDevice2.deviceName);
                            next();
                        }

                        public void onFailure(int reason) {
                            Slog.i(WifiDisplayController.TAG, "Failed to cancel connection to Wifi display: " + oldDevice2.deviceName + ", reason=" + reason);
                            next();
                        }

                        private void next() {
                            if (WifiDisplayController.this.mCancelingDevice == oldDevice2) {
                                WifiP2pDevice unused = WifiDisplayController.this.mCancelingDevice = null;
                                WifiDisplayController.this.updateConnection();
                            }
                        }
                    });
                } else if (this.mDesiredDevice == null) {
                    if (this.mWifiDisplayCertMode) {
                        this.mListener.onDisplaySessionInfo(getSessionInfo(this.mConnectedDeviceGroupInfo, 0));
                    }
                    unadvertiseDisplay();
                } else {
                    final WifiP2pDevice oldDevice3 = this.mDesiredDevice;
                    AnonymousClass13 r9 = new RemoteDisplay.Listener() {
                        public void onDisplayConnected(Surface surface, int width, int height, int flags, int session) {
                            if (WifiDisplayController.this.mConnectedDevice == oldDevice3 && !WifiDisplayController.this.mRemoteDisplayConnected) {
                                Slog.i(WifiDisplayController.TAG, "Opened RTSP connection with Wifi display: " + WifiDisplayController.this.mConnectedDevice.deviceName);
                                boolean unused = WifiDisplayController.this.mRemoteDisplayConnected = true;
                                WifiDisplayController.this.mHandler.removeCallbacks(WifiDisplayController.this.mRtspTimeout);
                                if (WifiDisplayController.this.mWifiDisplayCertMode) {
                                    Listener access$600 = WifiDisplayController.this.mListener;
                                    WifiDisplayController wifiDisplayController = WifiDisplayController.this;
                                    access$600.onDisplaySessionInfo(wifiDisplayController.getSessionInfo(wifiDisplayController.mConnectedDeviceGroupInfo, session));
                                }
                                WifiDisplayController.this.advertiseDisplay(WifiDisplayController.createWifiDisplay(WifiDisplayController.this.mConnectedDevice), surface, width, height, flags);
                            }
                        }

                        public void onDisplayDisconnected() {
                            if (WifiDisplayController.this.mConnectedDevice == oldDevice3) {
                                Slog.i(WifiDisplayController.TAG, "Closed RTSP connection with Wifi display: " + WifiDisplayController.this.mConnectedDevice.deviceName);
                                WifiDisplayController.this.mHandler.removeCallbacks(WifiDisplayController.this.mRtspTimeout);
                                boolean unused = WifiDisplayController.this.mRemoteDisplayConnected = false;
                                WifiDisplayController.this.disconnect();
                            }
                        }

                        public void onDisplayError(int error) {
                            if (WifiDisplayController.this.mConnectedDevice == oldDevice3) {
                                Slog.i(WifiDisplayController.TAG, "Lost RTSP connection with Wifi display due to error " + error + ": " + WifiDisplayController.this.mConnectedDevice.deviceName);
                                WifiDisplayController.this.mHandler.removeCallbacks(WifiDisplayController.this.mRtspTimeout);
                                WifiDisplayController.this.handleConnectionFailure(false);
                            }
                        }
                    };
                    int WFDR2Info = SystemProperties.getInt("persist.vendor.setWFDInfo.R2", 0);
                    Slog.i(TAG, "WFDR2info is: " + WFDR2Info);
                    if (WFDR2Info == 2 || WFDR2Info == 3) {
                        this.mWifiP2pManager.setWFDR2Info(getWifiP2pChannel(), this.mThisDevice.wfdInfo, new WifiP2pManager.ActionListener() {
                            public void onSuccess() {
                                if (WifiDisplayController.DEBUG) {
                                    Slog.i(WifiDisplayController.TAG, "Successfully set WFD R2 info.");
                                }
                            }

                            public void onFailure(int reason) {
                                if (WifiDisplayController.DEBUG) {
                                    Slog.i(WifiDisplayController.TAG, "Failed to set WFD R2 info with reason " + reason + ".");
                                }
                            }
                        });
                    }
                    if (this.mConnectedDevice == null && this.mConnectingDevice == null) {
                        Slog.i(TAG, "Connecting to Wifi display: " + this.mDesiredDevice.deviceName);
                        this.mConnectingDevice = this.mDesiredDevice;
                        WifiP2pConfig config = new WifiP2pConfig();
                        WpsInfo wps = new WpsInfo();
                        int i2 = this.mWifiDisplayWpsConfig;
                        if (i2 != 4) {
                            wps.setup = i2;
                        } else if (this.mConnectingDevice.wpsPbcSupported()) {
                            wps.setup = 0;
                        } else if (this.mConnectingDevice.wpsDisplaySupported()) {
                            wps.setup = 2;
                        } else {
                            wps.setup = 1;
                        }
                        config.wps = wps;
                        config.deviceAddress = this.mConnectingDevice.deviceAddress;
                        config.groupOwnerIntent = 15;
                        advertiseDisplay(createWifiDisplay(this.mConnectingDevice), (Surface) null, 0, 0, 0);
                        if (ExtendedRemoteDisplayHelper.isAvailable() && this.mExtRemoteDisplay == null) {
                            String iface = "255.255.255.255:" + getPortNumber(this.mDesiredDevice);
                            this.mRemoteDisplayInterface = iface;
                            Slog.i(TAG, "Listening for RTSP connection on " + iface + " from Wifi display: " + this.mDesiredDevice.deviceName);
                            this.mExtRemoteDisplay = ExtendedRemoteDisplayHelper.listen(iface, r9, this.mHandler, this.mContext);
                        }
                        final WifiP2pDevice newDevice = this.mDesiredDevice;
                        this.mWifiP2pManager.connect(getWifiP2pChannel(), config, new WifiP2pManager.ActionListener() {
                            public void onSuccess() {
                                Slog.i(WifiDisplayController.TAG, "Initiated connection to Wifi display: " + newDevice.deviceName);
                                WifiDisplayController.this.mHandler.postDelayed(WifiDisplayController.this.mConnectionTimeout, 30000);
                            }

                            public void onFailure(int reason) {
                                if (WifiDisplayController.this.mConnectingDevice == newDevice) {
                                    Slog.i(WifiDisplayController.TAG, "Failed to initiate connection to Wifi display: " + newDevice.deviceName + ", reason=" + reason);
                                    WifiP2pDevice unused = WifiDisplayController.this.mConnectingDevice = null;
                                    WifiDisplayController.this.handleConnectionFailure(false);
                                }
                            }
                        });
                    } else if (this.mConnectedDevice != null && this.mRemoteDisplay == null) {
                        Inet4Address addr = getInterfaceAddress(this.mConnectedDeviceGroupInfo);
                        if (addr == null) {
                            Slog.i(TAG, "Failed to get local interface address for communicating with Wifi display: " + this.mConnectedDevice.deviceName);
                            handleConnectionFailure(false);
                            return;
                        }
                        this.mWifiP2pManager.setMiracastMode(1);
                        String iface2 = addr.getHostAddress() + ":" + getPortNumber(this.mConnectedDevice);
                        this.mRemoteDisplayInterface = iface2;
                        if (!ExtendedRemoteDisplayHelper.isAvailable()) {
                            Slog.i(TAG, "Listening for RTSP connection on " + iface2 + " from Wifi display: " + this.mConnectedDevice.deviceName);
                            this.mRemoteDisplay = RemoteDisplay.listen(iface2, r9, this.mHandler, this.mContext.getOpPackageName());
                        }
                        this.mHandler.postDelayed(this.mRtspTimeout, (long) ((this.mWifiDisplayCertMode ? RTSP_TIMEOUT_SECONDS_CERT_MODE : 30) * 1000));
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public WifiDisplaySessionInfo getSessionInfo(WifiP2pGroup info, int session) {
        if (info == null || info.getOwner() == null) {
            return null;
        }
        Inet4Address addr = getInterfaceAddress(info);
        WifiDisplaySessionInfo sessionInfo = new WifiDisplaySessionInfo(!info.getOwner().deviceAddress.equals(this.mThisDevice.deviceAddress), session, info.getOwner().deviceAddress + " " + info.getNetworkName(), info.getPassphrase(), addr != null ? addr.getHostAddress() : "");
        if (DEBUG) {
            Slog.d(TAG, sessionInfo.toString());
        }
        return sessionInfo;
    }

    /* access modifiers changed from: private */
    public void handleStateChanged(boolean enabled) {
        this.mWifiP2pEnabled = enabled;
        updateWfdEnableState();
    }

    /* access modifiers changed from: private */
    public void handlePeersChanged() {
        requestPeers();
    }

    /* access modifiers changed from: private */
    public void handleConnectionChanged(NetworkInfo networkInfo) {
        this.mNetworkInfo = networkInfo;
        if (!this.mWfdEnabled || !networkInfo.isConnected()) {
            this.mConnectedDeviceGroupInfo = null;
            if (!(this.mConnectingDevice == null && this.mConnectedDevice == null)) {
                disconnect();
            }
            if (this.mDesiredDevice != null) {
                Slog.i(TAG, "reconnect new device: " + this.mDesiredDevice.deviceName);
                updateConnection();
            } else if (this.mWfdEnabled) {
                requestPeers();
            }
        } else if (this.mDesiredDevice != null || this.mWifiDisplayCertMode) {
            this.mWifiP2pManager.requestGroupInfo(getWifiP2pChannel(), new WifiP2pManager.GroupInfoListener() {
                public void onGroupInfoAvailable(WifiP2pGroup info) {
                    if (info != null) {
                        if (WifiDisplayController.DEBUG) {
                            Slog.d(WifiDisplayController.TAG, "Received group info: " + WifiDisplayController.describeWifiP2pGroup(info));
                        }
                        boolean z = false;
                        if (WifiDisplayController.this.mConnectingDevice != null && !info.contains(WifiDisplayController.this.mConnectingDevice)) {
                            Slog.i(WifiDisplayController.TAG, "Aborting connection to Wifi display because the current P2P group does not contain the device we expected to find: " + WifiDisplayController.this.mConnectingDevice.deviceName + ", group info was: " + WifiDisplayController.describeWifiP2pGroup(info));
                            WifiDisplayController.this.handleConnectionFailure(false);
                        } else if (WifiDisplayController.this.mDesiredDevice == null || info.contains(WifiDisplayController.this.mDesiredDevice)) {
                            if (WifiDisplayController.this.mWifiDisplayCertMode) {
                                if (info.getOwner() != null) {
                                    z = info.getOwner().deviceAddress.equals(WifiDisplayController.this.mThisDevice.deviceAddress);
                                }
                                boolean owner = z;
                                if (owner && info.getClientList().isEmpty()) {
                                    WifiDisplayController wifiDisplayController = WifiDisplayController.this;
                                    WifiP2pDevice unused = wifiDisplayController.mConnectingDevice = wifiDisplayController.mDesiredDevice = null;
                                    WifiP2pGroup unused2 = WifiDisplayController.this.mConnectedDeviceGroupInfo = info;
                                    WifiDisplayController.this.updateConnection();
                                } else if (WifiDisplayController.this.mConnectingDevice == null && WifiDisplayController.this.mDesiredDevice == null) {
                                    WifiDisplayController wifiDisplayController2 = WifiDisplayController.this;
                                    WifiP2pDevice unused3 = wifiDisplayController2.mConnectingDevice = wifiDisplayController2.mDesiredDevice = owner ? info.getClientList().iterator().next() : info.getOwner();
                                }
                            }
                            if (WifiDisplayController.this.mConnectingDevice != null && WifiDisplayController.this.mConnectingDevice == WifiDisplayController.this.mDesiredDevice) {
                                Slog.i(WifiDisplayController.TAG, "Connected to Wifi display: " + WifiDisplayController.this.mConnectingDevice.deviceName);
                                WifiDisplayController.this.mHandler.removeCallbacks(WifiDisplayController.this.mConnectionTimeout);
                                WifiP2pGroup unused4 = WifiDisplayController.this.mConnectedDeviceGroupInfo = info;
                                WifiDisplayController wifiDisplayController3 = WifiDisplayController.this;
                                WifiP2pDevice unused5 = wifiDisplayController3.mConnectedDevice = wifiDisplayController3.mConnectingDevice;
                                WifiP2pDevice unused6 = WifiDisplayController.this.mConnectingDevice = null;
                                WifiDisplayController.this.updateConnection();
                            }
                        } else {
                            WifiDisplayController.this.disconnect();
                        }
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void handleConnectionFailure(boolean timeoutOccurred) {
        Slog.i(TAG, "Wifi display connection failed!");
        if (this.mDesiredDevice == null) {
            return;
        }
        if (this.mConnectionRetriesLeft > 0) {
            final WifiP2pDevice oldDevice = this.mDesiredDevice;
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    if (WifiDisplayController.this.mDesiredDevice == oldDevice && WifiDisplayController.this.mConnectionRetriesLeft > 0) {
                        WifiDisplayController.access$3520(WifiDisplayController.this, 1);
                        Slog.i(WifiDisplayController.TAG, "Retrying Wifi display connection.  Retries left: " + WifiDisplayController.this.mConnectionRetriesLeft);
                        WifiDisplayController.this.retryConnection();
                    }
                }
            }, timeoutOccurred ? 0 : 500);
            return;
        }
        disconnect();
    }

    /* access modifiers changed from: private */
    public void advertiseDisplay(WifiDisplay display, Surface surface, int width, int height, int flags) {
        WifiDisplay wifiDisplay = display;
        Surface surface2 = surface;
        int i = width;
        int i2 = height;
        int i3 = flags;
        if (!Objects.equals(this.mAdvertisedDisplay, wifiDisplay) || this.mAdvertisedDisplaySurface != surface2 || this.mAdvertisedDisplayWidth != i || this.mAdvertisedDisplayHeight != i2 || this.mAdvertisedDisplayFlags != i3) {
            WifiDisplay oldDisplay = this.mAdvertisedDisplay;
            Surface oldSurface = this.mAdvertisedDisplaySurface;
            this.mAdvertisedDisplay = wifiDisplay;
            this.mAdvertisedDisplaySurface = surface2;
            this.mAdvertisedDisplayWidth = i;
            this.mAdvertisedDisplayHeight = i2;
            this.mAdvertisedDisplayFlags = i3;
            Handler handler = this.mHandler;
            final Surface surface3 = oldSurface;
            final Surface surface4 = surface;
            final WifiDisplay wifiDisplay2 = oldDisplay;
            final WifiDisplay wifiDisplay3 = display;
            AnonymousClass21 r9 = r0;
            final int i4 = width;
            Handler handler2 = handler;
            final int i5 = height;
            Surface surface5 = oldSurface;
            final int i6 = flags;
            AnonymousClass21 r0 = new Runnable() {
                public void run() {
                    Surface surface = surface3;
                    if (surface == null || surface4 == surface) {
                        WifiDisplay wifiDisplay = wifiDisplay2;
                        if (wifiDisplay != null && !wifiDisplay.hasSameAddress(wifiDisplay3)) {
                            WifiDisplayController.this.mListener.onDisplayConnectionFailed();
                        }
                    } else {
                        WifiDisplayController.this.mListener.onDisplayDisconnected();
                    }
                    WifiDisplay wifiDisplay2 = wifiDisplay3;
                    if (wifiDisplay2 != null) {
                        if (!wifiDisplay2.hasSameAddress(wifiDisplay2)) {
                            WifiDisplayController.this.mListener.onDisplayConnecting(wifiDisplay3);
                        } else if (!wifiDisplay3.equals(wifiDisplay2)) {
                            WifiDisplayController.this.mListener.onDisplayChanged(wifiDisplay3);
                        }
                        Surface surface2 = surface4;
                        if (surface2 != null && surface2 != surface3) {
                            WifiDisplayController.this.mListener.onDisplayConnected(wifiDisplay3, surface4, i4, i5, i6);
                        }
                    }
                }
            };
            handler2.post(r9);
        }
    }

    private void unadvertiseDisplay() {
        advertiseDisplay((WifiDisplay) null, (Surface) null, 0, 0, 0);
    }

    private void readvertiseDisplay(WifiDisplay display) {
        advertiseDisplay(display, this.mAdvertisedDisplaySurface, this.mAdvertisedDisplayWidth, this.mAdvertisedDisplayHeight, this.mAdvertisedDisplayFlags);
    }

    private boolean handlePreExistingConnection(final WifiP2pDevice device) {
        NetworkInfo networkInfo = this.mNetworkInfo;
        if (networkInfo == null || !networkInfo.isConnected() || this.mWifiDisplayCertMode) {
            return false;
        }
        Slog.i(TAG, "handle the preexisting p2p connection status");
        this.mWifiP2pManager.requestGroupInfo(getWifiP2pChannel(), new WifiP2pManager.GroupInfoListener() {
            public void onGroupInfoAvailable(WifiP2pGroup info) {
                if (info != null) {
                    if (info.contains(device)) {
                        Slog.i(WifiDisplayController.TAG, "already connected to the desired device: " + device.deviceName);
                        WifiDisplayController.this.updateConnection();
                        WifiDisplayController wifiDisplayController = WifiDisplayController.this;
                        wifiDisplayController.handleConnectionChanged(wifiDisplayController.mNetworkInfo);
                        return;
                    }
                    WifiDisplayController.this.mWifiP2pManager.removeGroup(WifiDisplayController.this.getWifiP2pChannel(), new WifiP2pManager.ActionListener() {
                        public void onSuccess() {
                            Slog.i(WifiDisplayController.TAG, "disconnect the old device");
                        }

                        public void onFailure(int reason) {
                            Slog.i(WifiDisplayController.TAG, "Failed to disconnect the old device: reason=" + reason);
                        }
                    });
                }
            }
        });
        this.mDesiredDevice = device;
        this.mConnectionRetriesLeft = 3;
        return true;
    }

    private static Inet4Address getInterfaceAddress(WifiP2pGroup info) {
        try {
            Enumeration<InetAddress> addrs = NetworkInterface.getByName(info.getInterface()).getInetAddresses();
            while (addrs.hasMoreElements()) {
                InetAddress addr = addrs.nextElement();
                if (addr instanceof Inet4Address) {
                    return (Inet4Address) addr;
                }
            }
            Slog.w(TAG, "Could not obtain address of network interface " + info.getInterface() + " because it had no IPv4 addresses.");
            return null;
        } catch (SocketException ex) {
            Slog.w(TAG, "Could not obtain address of network interface " + info.getInterface(), ex);
            return null;
        }
    }

    private static int getPortNumber(WifiP2pDevice device) {
        if (!device.deviceName.startsWith("DIRECT-") || !device.deviceName.endsWith("Broadcom")) {
            return DEFAULT_CONTROL_PORT;
        }
        return 8554;
    }

    /* access modifiers changed from: private */
    public static boolean isWifiDisplay(WifiP2pDevice device) {
        return device.wfdInfo != null && device.wfdInfo.isWfdEnabled() && isPrimarySinkDeviceType(device.wfdInfo.getDeviceType());
    }

    private static boolean isPrimarySinkDeviceType(int deviceType) {
        return deviceType == 1 || deviceType == 3;
    }

    /* access modifiers changed from: private */
    public static String describeWifiP2pDevice(WifiP2pDevice device) {
        return device != null ? device.toString().replace(10, ',') : "null";
    }

    /* access modifiers changed from: private */
    public static String describeWifiP2pGroup(WifiP2pGroup group) {
        return group != null ? group.toString().replace(10, ',') : "null";
    }

    /* access modifiers changed from: private */
    public static WifiDisplay createWifiDisplay(WifiP2pDevice device) {
        return new WifiDisplay(device.deviceAddress, device.deviceName, (String) null, true, device.wfdInfo.isSessionAvailable(), false);
    }
}
