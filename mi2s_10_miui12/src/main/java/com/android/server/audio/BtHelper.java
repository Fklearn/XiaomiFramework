package com.android.server.audio;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothCodecConfig;
import android.bluetooth.BluetoothCodecStatus;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothHearingAid;
import android.bluetooth.BluetoothProfile;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.AudioSystem;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import com.android.internal.annotations.GuardedBy;
import com.android.server.am.BroadcastQueueInjector;
import com.android.server.audio.AudioEventLogger;
import com.android.server.audio.AudioServiceEvents;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

public class BtHelper {
    private static final int BT_HEARING_AID_GAIN_MIN = -128;
    static final int EVENT_ACTIVE_DEVICE_CHANGE = 1;
    static final int EVENT_DEVICE_CONFIG_CHANGE = 0;
    private static final int SCO_MODE_MAX = 2;
    private static final int SCO_MODE_RAW = 1;
    static final int SCO_MODE_UNDEFINED = -1;
    static final int SCO_MODE_VIRTUAL_CALL = 0;
    private static final int SCO_MODE_VR = 2;
    private static final int SCO_STATE_ACTIVATE_REQ = 1;
    private static final int SCO_STATE_ACTIVE_EXTERNAL = 2;
    private static final int SCO_STATE_ACTIVE_INTERNAL = 3;
    private static final int SCO_STATE_DEACTIVATE_REQ = 4;
    private static final int SCO_STATE_DEACTIVATING = 5;
    private static final int SCO_STATE_INACTIVE = 0;
    private static final String TAG = "AS.BtHelper";
    private BluetoothA2dp mA2dp;
    private boolean mAvrcpAbsVolSupported = false;
    /* access modifiers changed from: private */
    public BluetoothHeadset mBluetoothHeadset;
    /* access modifiers changed from: private */
    public BluetoothDevice mBluetoothHeadsetDevice;
    private BluetoothProfile.ServiceListener mBluetoothProfileServiceListener = new BluetoothProfile.ServiceListener() {
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            Log.i(BtHelper.TAG, "In onServiceConnected(), profile: " + profile + ", proxy: " + proxy);
            if (profile == 1) {
                AudioService.sDeviceLogger.log(new AudioEventLogger.StringEvent("BT profile service: connecting HEADSET profile"));
                BtHelper.this.mDeviceBroker.postBtHeasetProfileConnected((BluetoothHeadset) proxy);
            } else if (profile == 2) {
                AudioService.sDeviceLogger.log(new AudioEventLogger.StringEvent("BT profile service: connecting A2DP profile"));
                BtHelper.this.mDeviceBroker.postBtA2dpProfileConnected((BluetoothA2dp) proxy);
            } else if (profile == 11) {
                AudioService.sDeviceLogger.log(new AudioEventLogger.StringEvent("BT profile service: connecting A2DP_SINK profile"));
                BtHelper.this.mDeviceBroker.postBtA2dpSinkProfileConnected(proxy);
            } else if (profile == 21) {
                AudioService.sDeviceLogger.log(new AudioEventLogger.StringEvent("BT profile service: connecting HEARING_AID profile"));
                BtHelper.this.mDeviceBroker.postBtHearingAidProfileConnected((BluetoothHearingAid) proxy);
            }
        }

        public void onServiceDisconnected(int profile) {
            if (profile == 1) {
                BtHelper.this.mDeviceBroker.postDisconnectHeadset();
            } else if (profile == 2) {
                BtHelper.this.mDeviceBroker.postDisconnectA2dp();
            } else if (profile == 11) {
                BtHelper.this.mDeviceBroker.postDisconnectA2dpSink();
            } else if (profile == 21) {
                BtHelper.this.mDeviceBroker.postDisconnectHearingAid();
            }
        }
    };
    /* access modifiers changed from: private */
    public final AudioDeviceBroker mDeviceBroker;
    private BluetoothHearingAid mHearingAid;
    /* access modifiers changed from: private */
    public int mScoAudioMode;
    /* access modifiers changed from: private */
    public int mScoAudioState;
    private HashMap<BluetoothDevice, Integer> mScoClientDevices = new HashMap<>();
    /* access modifiers changed from: private */
    public final ArrayList<ScoClient> mScoClients = new ArrayList<>();
    private int mScoConnectionState;

    BtHelper(AudioDeviceBroker broker) {
        this.mDeviceBroker = broker;
    }

    static class BluetoothA2dpDeviceInfo {
        private final BluetoothDevice mBtDevice;
        private final int mCodec;
        private final int mVolume;

        BluetoothA2dpDeviceInfo(BluetoothDevice btDevice) {
            this(btDevice, -1, 0);
        }

        BluetoothA2dpDeviceInfo(BluetoothDevice btDevice, int volume, int codec) {
            this.mBtDevice = btDevice;
            this.mVolume = volume;
            this.mCodec = codec;
        }

        public BluetoothDevice getBtDevice() {
            return this.mBtDevice;
        }

        public int getVolume() {
            return this.mVolume;
        }

        public int getCodec() {
            return this.mCodec;
        }
    }

    static String a2dpDeviceEventToString(int event) {
        if (event == 0) {
            return "DEVICE_CONFIG_CHANGE";
        }
        if (event == 1) {
            return "ACTIVE_DEVICE_CHANGE";
        }
        return new String("invalid event:" + event);
    }

    static String getName(BluetoothDevice device) {
        BluetoothAdapter adapter;
        String address = device.getAddress();
        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            address = "";
        }
        if (address.endsWith(".lhdc_a2dp") && (adapter = BluetoothAdapter.getDefaultAdapter()) != null) {
            device = adapter.getRemoteDevice(address.replace(".lhdc_a2dp", ""));
        }
        String addressNormal = device.getName();
        if (addressNormal == null) {
            return "";
        }
        return addressNormal;
    }

    static boolean isTwsPlusSwitch(BluetoothDevice device, String address) {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (device == null || adapter.getRemoteDevice(address) == null || device.getTwsPlusPeerAddress() == null || !device.isTwsPlusDevice() || !adapter.getRemoteDevice(address).isTwsPlusDevice() || !device.getTwsPlusPeerAddress().equals(address)) {
            return false;
        }
        Log.i(TAG, "isTwsPlusSwitch true");
        return true;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"AudioDeviceBroker.mDeviceStateLock"})
    public synchronized void onSystemReady() {
        this.mScoConnectionState = -1;
        Log.i(TAG, "In onSystemReady(), calling resetBluetoothSco()");
        resetBluetoothSco();
        getBluetoothHeadset();
        Intent newIntent = new Intent("android.media.SCO_AUDIO_STATE_CHANGED");
        newIntent.putExtra("android.media.extra.SCO_AUDIO_STATE", 0);
        sendStickyBroadcastToAll(newIntent);
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            adapter.getProfileProxy(this.mDeviceBroker.getContext(), this.mBluetoothProfileServiceListener, 2);
            adapter.getProfileProxy(this.mDeviceBroker.getContext(), this.mBluetoothProfileServiceListener, 21);
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized void onAudioServerDiedRestoreA2dp() {
        this.mDeviceBroker.setForceUse_Async(1, this.mDeviceBroker.getBluetoothA2dpEnabled() ? 0 : 10, "onAudioServerDied()");
    }

    /* access modifiers changed from: package-private */
    public synchronized boolean isAvrcpAbsoluteVolumeSupported() {
        return this.mA2dp != null && this.mAvrcpAbsVolSupported;
    }

    /* access modifiers changed from: package-private */
    public synchronized void setAvrcpAbsoluteVolumeSupported(boolean supported) {
        this.mAvrcpAbsVolSupported = supported;
        Log.i(TAG, "setAvrcpAbsoluteVolumeSupported supported=" + supported);
    }

    /* access modifiers changed from: package-private */
    public synchronized void setAvrcpAbsoluteVolumeIndex(int index) {
        if (this.mA2dp == null) {
            AudioService.sVolumeLogger.log(new AudioEventLogger.StringEvent("setAvrcpAbsoluteVolumeIndex: bailing due to null mA2dp").printLog(TAG));
        } else if (!this.mAvrcpAbsVolSupported) {
            AudioService.sVolumeLogger.log(new AudioEventLogger.StringEvent("setAvrcpAbsoluteVolumeIndex: abs vol not supported ").printLog(TAG));
        } else {
            Log.i(TAG, "setAvrcpAbsoluteVolumeIndex index=" + index);
            AudioService.sVolumeLogger.log(new AudioServiceEvents.VolumeEvent(4, index));
            this.mA2dp.setAvrcpAbsoluteVolume(index);
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized int getA2dpCodec(BluetoothDevice device) {
        if (this.mA2dp == null) {
            return 0;
        }
        BluetoothCodecStatus btCodecStatus = this.mA2dp.getCodecStatus(device);
        if (btCodecStatus == null) {
            return 0;
        }
        BluetoothCodecConfig btCodecConfig = btCodecStatus.getCodecConfig();
        if (btCodecConfig == null) {
            return 0;
        }
        return mapBluetoothCodecToAudioFormat(btCodecConfig.getCodecType());
    }

    private void updateTwsPlusScoState(BluetoothDevice device, Integer state) {
        if (this.mScoClientDevices.containsKey(device)) {
            Integer prevState = this.mScoClientDevices.get(device);
            Log.i(TAG, "updateTwsPlusScoState: prevState: " + prevState + "state: " + state);
            if (state != prevState) {
                this.mScoClientDevices.remove(device);
                this.mScoClientDevices.put(device, state);
                return;
            }
            return;
        }
        this.mScoClientDevices.put(device, state);
    }

    private boolean isAudioPathUp() {
        boolean ret = false;
        Iterator<Map.Entry<BluetoothDevice, Integer>> it = this.mScoClientDevices.entrySet().iterator();
        Iterator<Integer> it2 = this.mScoClientDevices.values().iterator();
        while (true) {
            if (it2.hasNext()) {
                if (it2.next().intValue() == 12) {
                    ret = true;
                    break;
                }
            } else {
                break;
            }
        }
        Log.d(TAG, "isAudioPathUp returns" + ret);
        return ret;
    }

    private boolean checkAndUpdatTwsPlusScoState(Intent intent, Integer state) {
        boolean ret = true;
        BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
        Log.i(TAG, "device:" + device);
        if (device == null) {
            Log.e(TAG, "checkAndUpdatTwsPlusScoState: device is null");
            return true;
        }
        if (device.isTwsPlusDevice()) {
            if (state.intValue() == 12) {
                if (isAudioPathUp()) {
                    Log.i(TAG, "No need to bringup audio-path");
                    ret = false;
                }
                updateTwsPlusScoState(device, state);
            } else {
                updateTwsPlusScoState(device, state);
                if (isAudioPathUp()) {
                    Log.i(TAG, "not good to tear down audio-path");
                    ret = false;
                }
            }
        }
        Log.i(TAG, "checkAndUpdatTwsPlusScoState returns " + ret);
        return ret;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"AudioDeviceBroker.mDeviceStateLock"})
    public synchronized void receiveBtEvent(Intent intent) {
        String action = intent.getAction();
        if (action.equals("android.bluetooth.headset.profile.action.ACTIVE_DEVICE_CHANGED")) {
            setBtScoActiveDevice((BluetoothDevice) intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE"));
        } else if (action.equals("android.bluetooth.headset.profile.action.AUDIO_STATE_CHANGED")) {
            boolean broadcast = false;
            int scoAudioState = -1;
            int btState = intent.getIntExtra("android.bluetooth.profile.extra.STATE", -1);
            boolean z = true;
            if (!this.mScoClients.isEmpty() && (this.mScoAudioState == 3 || this.mScoAudioState == 1 || this.mScoAudioState == 4 || this.mScoAudioState == 5)) {
                broadcast = true;
            }
            switch (btState) {
                case 10:
                    if (checkAndUpdatTwsPlusScoState(intent, 10)) {
                        this.mDeviceBroker.setBluetoothScoOn(false, "BtHelper.receiveBtEvent");
                        scoAudioState = 0;
                        if (this.mScoAudioState == 1 && this.mBluetoothHeadset != null && this.mBluetoothHeadsetDevice != null && connectBluetoothScoAudioHelper(this.mBluetoothHeadset, this.mBluetoothHeadsetDevice, this.mScoAudioMode)) {
                            this.mScoAudioState = 3;
                            broadcast = false;
                            break;
                        } else {
                            if (this.mScoAudioState != 3) {
                                z = false;
                            }
                            clearAllScoClients(0, z);
                            this.mScoAudioState = 0;
                            Log.i(TAG, "Audio-path brought-down");
                            break;
                        }
                    }
                    break;
                case 11:
                    if (!(this.mScoAudioState == 3 || this.mScoAudioState == 4)) {
                        this.mScoAudioState = 2;
                        break;
                    }
                case 12:
                    if (checkAndUpdatTwsPlusScoState(intent, 12)) {
                        if (!(this.mScoAudioState == 3 || this.mScoAudioState == 4)) {
                            this.mScoAudioState = 2;
                        }
                        this.mDeviceBroker.setBluetoothScoOn(true, "BtHelper.receiveBtEvent");
                        Log.i(TAG, "Audio-path brought-up");
                    }
                    scoAudioState = 1;
                    if (!(this.mScoAudioState == 3 || this.mScoAudioState == 4)) {
                        this.mScoAudioState = 2;
                        break;
                    }
                default:
                    broadcast = false;
                    break;
            }
            if (broadcast) {
                broadcastScoConnectionState(scoAudioState);
                Intent newIntent = new Intent("android.media.SCO_AUDIO_STATE_CHANGED");
                newIntent.putExtra("android.media.extra.SCO_AUDIO_STATE", scoAudioState);
                sendStickyBroadcastToAll(newIntent);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isBluetoothAudioNotConnectedToEarbud() {
        String pDevAddr;
        boolean ret = true;
        BluetoothDevice bluetoothDevice = this.mBluetoothHeadsetDevice;
        if (!(bluetoothDevice == null || !bluetoothDevice.isTwsPlusDevice() || (pDevAddr = this.mBluetoothHeadsetDevice.getTwsPlusPeerAddress()) == null)) {
            BluetoothDevice peerDev = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(pDevAddr);
            Log.d(TAG, "peer device audio State: " + this.mBluetoothHeadset.getAudioState(peerDev));
            if (this.mBluetoothHeadset.getAudioState(peerDev) == 12 || this.mBluetoothHeadset.getAudioState(this.mBluetoothHeadsetDevice) == 12) {
                Log.w(TAG, "TwsPLus Case: one of eb SCO is connected");
                ret = false;
            }
        }
        Log.d(TAG, "isBluetoothAudioConnectedToEarbud returns: " + ret);
        return ret;
    }

    /* access modifiers changed from: package-private */
    public synchronized boolean isBluetoothScoOn() {
        if (this.mBluetoothHeadset == null || this.mBluetoothHeadset.getAudioState(this.mBluetoothHeadsetDevice) == 12) {
            return true;
        }
        Log.w(TAG, "isBluetoothScoOn(true) returning false because " + this.mBluetoothHeadsetDevice + " is not in audio connected mode");
        return false;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"AudioDeviceBroker.mDeviceStateLock"})
    public synchronized void disconnectBluetoothSco(int exceptPid) {
        Log.i(TAG, "In disconnectBluetoothSco(), exceptPid: " + exceptPid);
        checkScoAudioState();
        if (this.mScoAudioState != 2) {
            clearAllScoClients(exceptPid, true);
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"AudioDeviceBroker.mDeviceStateLock"})
    public synchronized void startBluetoothScoForClient(IBinder cb, int scoAudioMode, String eventSource) {
        ScoClient client = getScoClient(cb, true);
        long ident = Binder.clearCallingIdentity();
        try {
            AudioService.sDeviceLogger.log(new AudioEventLogger.StringEvent(eventSource + " client count before=" + client.getCount()));
            client.incCount(scoAudioMode);
        } catch (NullPointerException e) {
            Log.e(TAG, "Null ScoClient", e);
        }
        Binder.restoreCallingIdentity(ident);
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"AudioDeviceBroker.mDeviceStateLock"})
    public synchronized void stopBluetoothScoForClient(IBinder cb, String eventSource) {
        ScoClient client = getScoClient(cb, false);
        long ident = Binder.clearCallingIdentity();
        if (client != null) {
            AudioService.sDeviceLogger.log(new AudioEventLogger.StringEvent(eventSource + " client count before=" + client.getCount()));
            client.decCount();
        }
        Binder.restoreCallingIdentity(ident);
    }

    /* access modifiers changed from: package-private */
    public synchronized void setHearingAidVolume(int index, int streamType) {
        if (this.mHearingAid == null) {
            Log.i(TAG, "setHearingAidVolume: null mHearingAid");
            return;
        }
        int gainDB = (int) AudioSystem.getStreamVolumeDB(streamType, index / 10, 134217728);
        if (gainDB < -128) {
            gainDB = -128;
        }
        Log.i(TAG, "setHearingAidVolume: calling mHearingAid.setVolume idx=" + index + " gain=" + gainDB);
        AudioService.sVolumeLogger.log(new AudioServiceEvents.VolumeEvent(3, index, gainDB));
        this.mHearingAid.setVolume(gainDB);
    }

    /* access modifiers changed from: package-private */
    public synchronized void onBroadcastScoConnectionState(int state) {
        if (state != this.mScoConnectionState) {
            Intent newIntent = new Intent("android.media.ACTION_SCO_AUDIO_STATE_UPDATED");
            newIntent.putExtra("android.media.extra.SCO_AUDIO_STATE", state);
            newIntent.putExtra("android.media.extra.SCO_AUDIO_PREVIOUS_STATE", this.mScoConnectionState);
            sendStickyBroadcastToAll(newIntent);
            this.mScoConnectionState = state;
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized void disconnectAllBluetoothProfiles() {
        this.mDeviceBroker.postDisconnectA2dp();
        this.mDeviceBroker.postDisconnectA2dpSink();
        this.mDeviceBroker.postDisconnectHeadset();
        this.mDeviceBroker.postDisconnectHearingAid();
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"AudioDeviceBroker.mDeviceStateLock"})
    public synchronized void resetBluetoothSco() {
        Log.i(TAG, "In resetBluetoothSco(), calling clearAllScoClients()");
        clearAllScoClients(0, false);
        this.mScoAudioState = 0;
        broadcastScoConnectionState(0);
        this.mScoClientDevices.clear();
        AudioSystem.setParameters("A2dpSuspended=false");
        this.mDeviceBroker.setBluetoothScoOn(false, "resetBluetoothSco");
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"AudioDeviceBroker.mDeviceStateLock"})
    public synchronized void disconnectHeadset() {
        setBtScoActiveDevice((BluetoothDevice) null);
        this.mBluetoothHeadset = null;
    }

    /* access modifiers changed from: package-private */
    public synchronized void onA2dpProfileConnected(BluetoothA2dp a2dp) {
        this.mA2dp = a2dp;
        List<BluetoothDevice> deviceList = this.mA2dp.getConnectedDevices();
        if (!deviceList.isEmpty()) {
            BluetoothDevice btDevice = deviceList.get(0);
            this.mDeviceBroker.handleSetA2dpSinkConnectionState(this.mA2dp.getConnectionState(btDevice), new BluetoothA2dpDeviceInfo(btDevice));
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized void onA2dpSinkProfileConnected(BluetoothProfile profile) {
        List<BluetoothDevice> deviceList = profile.getConnectedDevices();
        if (!deviceList.isEmpty()) {
            BluetoothDevice btDevice = deviceList.get(0);
            this.mDeviceBroker.postSetA2dpSourceConnectionState(profile.getConnectionState(btDevice), new BluetoothA2dpDeviceInfo(btDevice));
        }
    }

    /* access modifiers changed from: package-private */
    public synchronized void onHearingAidProfileConnected(BluetoothHearingAid hearingAid) {
        this.mHearingAid = hearingAid;
        List<BluetoothDevice> deviceList = this.mHearingAid.getConnectedDevices();
        if (!deviceList.isEmpty()) {
            BluetoothDevice btDevice = deviceList.get(0);
            this.mDeviceBroker.postBluetoothHearingAidDeviceConnectionState(btDevice, this.mHearingAid.getConnectionState(btDevice), false, 0, "mBluetoothProfileServiceListener");
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0056, code lost:
        return;
     */
    @com.android.internal.annotations.GuardedBy({"AudioDeviceBroker.mDeviceStateLock"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized void onHeadsetProfileConnected(android.bluetooth.BluetoothHeadset r5) {
        /*
            r4 = this;
            monitor-enter(r4)
            com.android.server.audio.AudioDeviceBroker r0 = r4.mDeviceBroker     // Catch:{ all -> 0x0057 }
            r0.handleCancelFailureToConnectToBtHeadsetService()     // Catch:{ all -> 0x0057 }
            r4.mBluetoothHeadset = r5     // Catch:{ all -> 0x0057 }
            android.bluetooth.BluetoothHeadset r0 = r4.mBluetoothHeadset     // Catch:{ all -> 0x0057 }
            android.bluetooth.BluetoothDevice r0 = r0.getActiveDevice()     // Catch:{ all -> 0x0057 }
            r4.setBtScoActiveDevice(r0)     // Catch:{ all -> 0x0057 }
            r4.checkScoAudioState()     // Catch:{ all -> 0x0057 }
            int r0 = r4.mScoAudioState     // Catch:{ all -> 0x0057 }
            r1 = 4
            r2 = 1
            if (r0 == r2) goto L_0x0020
            int r0 = r4.mScoAudioState     // Catch:{ all -> 0x0057 }
            if (r0 == r1) goto L_0x0020
            monitor-exit(r4)
            return
        L_0x0020:
            r0 = 0
            android.bluetooth.BluetoothDevice r3 = r4.mBluetoothHeadsetDevice     // Catch:{ all -> 0x0057 }
            if (r3 == 0) goto L_0x004d
            int r3 = r4.mScoAudioState     // Catch:{ all -> 0x0057 }
            if (r3 == r2) goto L_0x003d
            if (r3 == r1) goto L_0x002c
            goto L_0x004d
        L_0x002c:
            android.bluetooth.BluetoothHeadset r1 = r4.mBluetoothHeadset     // Catch:{ all -> 0x0057 }
            android.bluetooth.BluetoothDevice r2 = r4.mBluetoothHeadsetDevice     // Catch:{ all -> 0x0057 }
            int r3 = r4.mScoAudioMode     // Catch:{ all -> 0x0057 }
            boolean r1 = disconnectBluetoothScoAudioHelper(r1, r2, r3)     // Catch:{ all -> 0x0057 }
            r0 = r1
            if (r0 == 0) goto L_0x004d
            r1 = 5
            r4.mScoAudioState = r1     // Catch:{ all -> 0x0057 }
            goto L_0x004d
        L_0x003d:
            android.bluetooth.BluetoothHeadset r1 = r4.mBluetoothHeadset     // Catch:{ all -> 0x0057 }
            android.bluetooth.BluetoothDevice r2 = r4.mBluetoothHeadsetDevice     // Catch:{ all -> 0x0057 }
            int r3 = r4.mScoAudioMode     // Catch:{ all -> 0x0057 }
            boolean r1 = connectBluetoothScoAudioHelper(r1, r2, r3)     // Catch:{ all -> 0x0057 }
            r0 = r1
            if (r0 == 0) goto L_0x004d
            r1 = 3
            r4.mScoAudioState = r1     // Catch:{ all -> 0x0057 }
        L_0x004d:
            if (r0 != 0) goto L_0x0055
            r1 = 0
            r4.mScoAudioState = r1     // Catch:{ all -> 0x0057 }
            r4.broadcastScoConnectionState(r1)     // Catch:{ all -> 0x0057 }
        L_0x0055:
            monitor-exit(r4)
            return
        L_0x0057:
            r5 = move-exception
            monitor-exit(r4)
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.audio.BtHelper.onHeadsetProfileConnected(android.bluetooth.BluetoothHeadset):void");
    }

    /* access modifiers changed from: private */
    public void broadcastScoConnectionState(int state) {
        this.mDeviceBroker.postBroadcastScoConnectionState(state);
    }

    private boolean handleBtScoActiveDeviceChange(BluetoothDevice btDevice, boolean isActive) {
        boolean result;
        if (btDevice == null) {
            return true;
        }
        String address = btDevice.getAddress();
        BluetoothClass btClass = btDevice.getBluetoothClass();
        int[] outDeviceTypes = {16, 32, 64};
        if (btClass != null) {
            int deviceClass = btClass.getDeviceClass();
            if (deviceClass == 1028 || deviceClass == 1032) {
                outDeviceTypes = new int[]{32};
            } else if (deviceClass == 1056) {
                outDeviceTypes = new int[]{64};
            }
        }
        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            address = "";
        }
        String btDeviceName = getName(btDevice);
        if (btDeviceName == null) {
            Log.i(TAG, "handleBtScoActiveDeviceChange: btDeviceName is null, sending empty string");
            btDeviceName = "";
        }
        if (isActive) {
            result = false | this.mDeviceBroker.handleDeviceConnection(isActive, outDeviceTypes[0], address, btDeviceName);
        } else {
            boolean result2 = false;
            for (int outDeviceType : outDeviceTypes) {
                result2 |= this.mDeviceBroker.handleDeviceConnection(isActive, outDeviceType, address, btDeviceName);
            }
            result = result2;
        }
        if (!this.mDeviceBroker.handleDeviceConnection(isActive, -2147483640, address, btDeviceName) || !result) {
            return false;
        }
        return true;
    }

    @GuardedBy({"BtHelper.this"})
    private void setBtScoActiveDevice(BluetoothDevice btDevice) {
        Log.i(TAG, "setBtScoActiveDevice: " + this.mBluetoothHeadsetDevice + " -> " + btDevice);
        BluetoothDevice previousActiveDevice = this.mBluetoothHeadsetDevice;
        BluetoothDevice bluetoothDevice = this.mBluetoothHeadsetDevice;
        if (bluetoothDevice != null && bluetoothDevice.isTwsPlusDevice() && btDevice != null && Objects.equals(this.mBluetoothHeadsetDevice.getTwsPlusPeerAddress(), btDevice.getAddress())) {
            Log.i(TAG, "setBtScoActiveDevice: Active device switch between twsplus devices");
        } else if (!Objects.equals(btDevice, previousActiveDevice)) {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (adapter == null) {
                Log.i(TAG, "adapter is null, returning from setBtScoActiveDevice");
                return;
            }
            BluetoothDevice dummyActiveDevice = adapter.getRemoteDevice("00:00:00:00:00:00");
            if (this.mBluetoothHeadsetDevice == null && btDevice != null && !handleBtScoActiveDeviceChange(dummyActiveDevice, true)) {
                Log.e(TAG, "setBtScoActiveDevice() failed to add new device " + btDevice);
                btDevice = null;
            }
            if (this.mBluetoothHeadsetDevice != null && btDevice == null && !handleBtScoActiveDeviceChange(dummyActiveDevice, false)) {
                Log.w(TAG, "setBtScoActiveDevice() failed to remove previous device " + previousActiveDevice);
            }
            this.mBluetoothHeadsetDevice = btDevice;
            if (this.mBluetoothHeadsetDevice == null) {
                Log.i(TAG, "In setBtScoActiveDevice(), calling resetBluetoothSco()");
                resetBluetoothSco();
            }
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"AudioDeviceBroker.mDeviceStateLock"})
    public synchronized void scoClientDied(Object obj) {
        ScoClient client = (ScoClient) obj;
        Log.w(TAG, "SCO client died");
        if (this.mScoClients.indexOf(client) < 0) {
            Log.w(TAG, "unregistered SCO client died");
        } else {
            client.clearCount(true);
            this.mScoClients.remove(client);
        }
    }

    private class ScoClient implements IBinder.DeathRecipient {
        private IBinder mCb;
        private int mCreatorPid = Binder.getCallingPid();
        private int mStartcount = 0;

        ScoClient(IBinder cb) {
            this.mCb = cb;
        }

        public void binderDied() {
            BtHelper.this.mDeviceBroker.postScoClientDied(this);
        }

        /* access modifiers changed from: package-private */
        @GuardedBy({"BtHelper.this"})
        public void incCount(int scoAudioMode) {
            Log.i(BtHelper.TAG, "In incCount(), mStartcount = " + this.mStartcount);
            if (!requestScoState(12, scoAudioMode)) {
                Log.e(BtHelper.TAG, "Request sco connected with scoAudioMode(" + scoAudioMode + ") failed");
                return;
            }
            if (this.mStartcount == 0) {
                try {
                    this.mCb.linkToDeath(this, 0);
                } catch (RemoteException e) {
                    Log.w(BtHelper.TAG, "ScoClient incCount() could not link to " + this.mCb + " binder death");
                }
            }
            int i = this.mStartcount;
            if (i == 1) {
                Log.i(BtHelper.TAG, "mStartcount is 1, calling setBluetoothScoOn(true)in system context");
                BtHelper.this.mDeviceBroker.setBluetoothScoOn(true, "BtHelper.incCount");
            } else if (i == 0) {
                this.mStartcount = i + 1;
                Log.i(BtHelper.TAG, "mStartcount is 0, incrementing by 1");
            }
        }

        /* access modifiers changed from: package-private */
        @GuardedBy({"BtHelper.this"})
        public void decCount() {
            Log.i(BtHelper.TAG, "In decCount(), mStartcount: " + this.mStartcount);
            int i = this.mStartcount;
            if (i == 0) {
                Log.w(BtHelper.TAG, "ScoClient.decCount() already 0");
                return;
            }
            this.mStartcount = i - 1;
            if (this.mStartcount == 0) {
                try {
                    this.mCb.unlinkToDeath(this, 0);
                } catch (NoSuchElementException e) {
                    Log.w(BtHelper.TAG, "decCount() going to 0 but not registered to binder");
                }
            }
            if (!requestScoState(10, 0)) {
                Log.w(BtHelper.TAG, "Request sco disconnected with scoAudioMode(0) failed");
            }
        }

        /* access modifiers changed from: package-private */
        @GuardedBy({"BtHelper.this"})
        public void clearCount(boolean stopSco) {
            Log.i(BtHelper.TAG, "In clearCount(), stopSco: " + stopSco + ", mStartcount: " + this.mStartcount);
            if (this.mStartcount != 0) {
                try {
                    this.mCb.unlinkToDeath(this, 0);
                } catch (NoSuchElementException e) {
                    Log.w(BtHelper.TAG, "clearCount() mStartcount: " + this.mStartcount + " != 0 but not registered to binder");
                }
            }
            this.mStartcount = 0;
            if (stopSco) {
                requestScoState(10, 0);
            }
        }

        /* access modifiers changed from: package-private */
        public int getCount() {
            return this.mStartcount;
        }

        /* access modifiers changed from: package-private */
        public IBinder getBinder() {
            return this.mCb;
        }

        /* access modifiers changed from: package-private */
        public int getPid() {
            return this.mCreatorPid;
        }

        private int totalCount() {
            int count = 0;
            Iterator it = BtHelper.this.mScoClients.iterator();
            while (it.hasNext()) {
                count += ((ScoClient) it.next()).getCount();
            }
            Log.i(BtHelper.TAG, "In totalCount(), count: " + count);
            return count;
        }

        @GuardedBy({"BtHelper.this"})
        private boolean requestScoState(int state, int scoAudioMode) {
            Log.i(BtHelper.TAG, "In requestScoState(), state: " + state + ", scoAudioMode: " + scoAudioMode);
            BtHelper.this.checkScoAudioState();
            int clientCount = totalCount();
            if (clientCount != 0) {
                Log.i(BtHelper.TAG, "requestScoState: state=" + state + ", scoAudioMode=" + scoAudioMode + ", clientCount=" + clientCount);
                return true;
            }
            if (state == 12) {
                BtHelper.this.broadcastScoConnectionState(2);
                int modeOwnerPid = BtHelper.this.mDeviceBroker.getModeOwnerPid();
                if (modeOwnerPid == 0 || modeOwnerPid == this.mCreatorPid) {
                    int access$400 = BtHelper.this.mScoAudioState;
                    if (access$400 == 0) {
                        int unused = BtHelper.this.mScoAudioMode = scoAudioMode;
                        if (scoAudioMode == -1) {
                            int unused2 = BtHelper.this.mScoAudioMode = 0;
                            if (BtHelper.this.mBluetoothHeadsetDevice != null) {
                                BtHelper btHelper = BtHelper.this;
                                ContentResolver contentResolver = btHelper.mDeviceBroker.getContentResolver();
                                int unused3 = btHelper.mScoAudioMode = Settings.Global.getInt(contentResolver, "bluetooth_sco_channel_" + BtHelper.this.mBluetoothHeadsetDevice.getAddress(), 0);
                                if (BtHelper.this.mScoAudioMode > 2 || BtHelper.this.mScoAudioMode < 0) {
                                    int unused4 = BtHelper.this.mScoAudioMode = 0;
                                }
                            }
                        }
                        if (BtHelper.this.mBluetoothHeadset == null) {
                            if (BtHelper.this.getBluetoothHeadset()) {
                                int unused5 = BtHelper.this.mScoAudioState = 1;
                            } else {
                                Log.w(BtHelper.TAG, "requestScoState: getBluetoothHeadset failed during connection, mScoAudioMode=" + BtHelper.this.mScoAudioMode);
                                BtHelper.this.broadcastScoConnectionState(0);
                                return false;
                            }
                        } else if (BtHelper.this.mBluetoothHeadsetDevice == null) {
                            Log.w(BtHelper.TAG, "requestScoState: no active device while connecting, mScoAudioMode=" + BtHelper.this.mScoAudioMode);
                            BtHelper.this.broadcastScoConnectionState(0);
                            return false;
                        } else if (BtHelper.connectBluetoothScoAudioHelper(BtHelper.this.mBluetoothHeadset, BtHelper.this.mBluetoothHeadsetDevice, BtHelper.this.mScoAudioMode)) {
                            int unused6 = BtHelper.this.mScoAudioState = 3;
                        } else {
                            Log.w(BtHelper.TAG, "requestScoState: connect to " + BtHelper.this.mBluetoothHeadsetDevice + " failed, mScoAudioMode=" + BtHelper.this.mScoAudioMode);
                            BtHelper.this.broadcastScoConnectionState(0);
                            return false;
                        }
                    } else if (access$400 == 4) {
                        int unused7 = BtHelper.this.mScoAudioState = 3;
                        BtHelper.this.broadcastScoConnectionState(1);
                        return false;
                    } else if (access$400 != 5) {
                        Log.w(BtHelper.TAG, "requestScoState: failed to connect in state " + BtHelper.this.mScoAudioState + ", scoAudioMode=" + scoAudioMode);
                        BtHelper.this.broadcastScoConnectionState(0);
                        return false;
                    } else {
                        int unused8 = BtHelper.this.mScoAudioState = 1;
                    }
                } else {
                    Log.w(BtHelper.TAG, "requestScoState: audio mode is not NORMAL and modeOwnerPid " + modeOwnerPid + " != creatorPid " + this.mCreatorPid);
                    BtHelper.this.broadcastScoConnectionState(0);
                    return false;
                }
            } else if (state == 10) {
                int access$4002 = BtHelper.this.mScoAudioState;
                if (access$4002 == 1) {
                    int unused9 = BtHelper.this.mScoAudioState = 0;
                    BtHelper.this.broadcastScoConnectionState(0);
                } else if (access$4002 != 3) {
                    Log.w(BtHelper.TAG, "requestScoState: failed to disconnect in state " + BtHelper.this.mScoAudioState + ", scoAudioMode=" + scoAudioMode);
                    BtHelper.this.broadcastScoConnectionState(0);
                    return false;
                } else if (BtHelper.this.mBluetoothHeadset == null) {
                    if (BtHelper.this.getBluetoothHeadset()) {
                        int unused10 = BtHelper.this.mScoAudioState = 4;
                    } else {
                        Log.w(BtHelper.TAG, "requestScoState: getBluetoothHeadset failed during disconnection, mScoAudioMode=" + BtHelper.this.mScoAudioMode);
                        int unused11 = BtHelper.this.mScoAudioState = 0;
                        BtHelper.this.broadcastScoConnectionState(0);
                        return false;
                    }
                } else if (BtHelper.this.mBluetoothHeadsetDevice == null) {
                    int unused12 = BtHelper.this.mScoAudioState = 0;
                    BtHelper.this.broadcastScoConnectionState(0);
                } else if (BtHelper.disconnectBluetoothScoAudioHelper(BtHelper.this.mBluetoothHeadset, BtHelper.this.mBluetoothHeadsetDevice, BtHelper.this.mScoAudioMode)) {
                    int unused13 = BtHelper.this.mScoAudioState = 5;
                } else {
                    int unused14 = BtHelper.this.mScoAudioState = 0;
                    BtHelper.this.broadcastScoConnectionState(0);
                }
            }
            return true;
        }
    }

    private void sendStickyBroadcastToAll(Intent intent) {
        intent.addFlags(268435456);
        long ident = Binder.clearCallingIdentity();
        try {
            this.mDeviceBroker.getContext().sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        } finally {
            Binder.restoreCallingIdentity(ident);
        }
    }

    /* access modifiers changed from: private */
    public static boolean disconnectBluetoothScoAudioHelper(BluetoothHeadset bluetoothHeadset, BluetoothDevice device, int scoAudioMode) {
        Log.i(TAG, "In disconnectBluetoothScoAudioHelper(), scoAudioMode: " + scoAudioMode + ", bluetoothHeadset: " + bluetoothHeadset + ", BluetoothDevice: " + device);
        if (scoAudioMode == 0) {
            Log.i(TAG, "In disconnectBluetoothScoAudioHelper(), calling stopScoUsingVirtualVoiceCall()");
            return bluetoothHeadset.stopScoUsingVirtualVoiceCall();
        } else if (scoAudioMode == 1) {
            Log.i(TAG, "In disconnectBluetoothScoAudioHelper(), calling disconnectAudio()");
            return bluetoothHeadset.disconnectAudio();
        } else if (scoAudioMode != 2) {
            return false;
        } else {
            Log.i(TAG, "In disconnectBluetoothScoAudioHelper(), calling stopVoiceRecognition()");
            return bluetoothHeadset.stopVoiceRecognition(device);
        }
    }

    /* access modifiers changed from: private */
    public static boolean connectBluetoothScoAudioHelper(BluetoothHeadset bluetoothHeadset, BluetoothDevice device, int scoAudioMode) {
        Log.i(TAG, "In connectBluetoothScoAudioHelper(), scoAudioMode: " + scoAudioMode + ", bluetoothHeadset: " + bluetoothHeadset + ", BluetoothDevice: " + device);
        if (scoAudioMode == 0) {
            Log.i(TAG, "In connectBluetoothScoAudioHelper(), calling startScoUsingVirtualVoiceCall()");
            return bluetoothHeadset.startScoUsingVirtualVoiceCall();
        } else if (scoAudioMode == 1) {
            Log.i(TAG, "In connectBluetoothScoAudioHelper(), calling connectAudio()");
            return bluetoothHeadset.connectAudio();
        } else if (scoAudioMode != 2) {
            return false;
        } else {
            Log.i(TAG, "In connectBluetoothScoAudioHelper(), calling startVoiceRecognition()");
            return bluetoothHeadset.startVoiceRecognition(device);
        }
    }

    /* access modifiers changed from: private */
    public void checkScoAudioState() {
        BluetoothDevice bluetoothDevice;
        BluetoothHeadset bluetoothHeadset = this.mBluetoothHeadset;
        if (!(bluetoothHeadset == null || (bluetoothDevice = this.mBluetoothHeadsetDevice) == null || this.mScoAudioState != 0 || bluetoothHeadset.getAudioState(bluetoothDevice) == 10)) {
            this.mScoAudioState = 2;
        }
        Log.i(TAG, "In checkScoAudioState(), mScoAudioState: " + this.mScoAudioState);
    }

    private ScoClient getScoClient(IBinder cb, boolean create) {
        Iterator<ScoClient> it = this.mScoClients.iterator();
        while (it.hasNext()) {
            ScoClient existingClient = it.next();
            if (existingClient.getBinder() == cb) {
                return existingClient;
            }
        }
        if (!create) {
            return null;
        }
        ScoClient newClient = new ScoClient(cb);
        this.mScoClients.add(newClient);
        return newClient;
    }

    @GuardedBy({"BtHelper.this"})
    private void clearAllScoClients(int exceptPid, boolean stopSco) {
        ScoClient savedClient = null;
        Iterator<ScoClient> it = this.mScoClients.iterator();
        while (it.hasNext()) {
            ScoClient cl = it.next();
            if (cl.getPid() != exceptPid) {
                cl.clearCount(stopSco);
            } else {
                savedClient = cl;
            }
        }
        this.mScoClients.clear();
        if (savedClient != null) {
            this.mScoClients.add(savedClient);
        }
    }

    /* access modifiers changed from: private */
    public boolean getBluetoothHeadset() {
        boolean result = false;
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            result = adapter.getProfileProxy(this.mDeviceBroker.getContext(), this.mBluetoothProfileServiceListener, 1);
        }
        this.mDeviceBroker.handleFailureToConnectToBtHeadsetService(result ? 3000 : 0);
        return result;
    }

    private int mapBluetoothCodecToAudioFormat(int btCodecType) {
        switch (btCodecType) {
            case 0:
                return 520093696;
            case 1:
                return BroadcastQueueInjector.FLAG_IMMUTABLE;
            case 2:
                return 536870912;
            case 3:
                return 553648128;
            case 4:
                return 654311424;
            case 5:
                return 587202560;
            case 6:
                return 704643072;
            case 8:
                return 637534208;
            default:
                return 0;
        }
    }
}
