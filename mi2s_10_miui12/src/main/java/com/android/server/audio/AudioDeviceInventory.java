package com.android.server.audio;

import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.media.AudioDevicePort;
import android.media.AudioFormat;
import android.media.AudioPort;
import android.media.AudioRoutesInfo;
import android.media.AudioSystem;
import android.media.IAudioRoutesObserver;
import android.os.Binder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.server.am.BroadcastQueueInjector;
import com.android.server.audio.AudioDeviceInventory;
import com.android.server.audio.AudioEventLogger;
import com.android.server.audio.AudioServiceEvents;
import com.android.server.audio.BtHelper;
import com.android.server.pm.PackageManagerService;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class AudioDeviceInventory {
    private static final String CONNECT_INTENT_KEY_ADDRESS = "address";
    private static final String CONNECT_INTENT_KEY_DEVICE_CLASS = "class";
    private static final String CONNECT_INTENT_KEY_HAS_CAPTURE = "hasCapture";
    private static final String CONNECT_INTENT_KEY_HAS_MIDI = "hasMIDI";
    private static final String CONNECT_INTENT_KEY_HAS_PLAYBACK = "hasPlayback";
    private static final String CONNECT_INTENT_KEY_PORT_NAME = "portName";
    private static final String CONNECT_INTENT_KEY_STATE = "state";
    private static final int DEVICE_OVERRIDE_A2DP_ROUTE_ON_PLUG = 67264524;
    private static final String TAG = "AS.AudioDeviceInventory";
    private int mBecomingNoisyIntentDevices = 201490316;
    private final ArrayMap<String, DeviceInfo> mConnectedDevices = new ArrayMap<>();
    final AudioRoutesInfo mCurAudioRoutes = new AudioRoutesInfo();
    private final AudioDeviceBroker mDeviceBroker;
    private String mDockAddress;
    final RemoteCallbackList<IAudioRoutesObserver> mRoutesObservers = new RemoteCallbackList<>();

    AudioDeviceInventory(AudioDeviceBroker broker) {
        this.mDeviceBroker = broker;
    }

    private static class DeviceInfo {
        final String mDeviceAddress;
        int mDeviceCodecFormat;
        final String mDeviceName;
        final int mDeviceType;

        DeviceInfo(int deviceType, String deviceName, String deviceAddress, int deviceCodecFormat) {
            this.mDeviceType = deviceType;
            this.mDeviceName = deviceName;
            this.mDeviceAddress = deviceAddress;
            this.mDeviceCodecFormat = deviceCodecFormat;
        }

        public String toString() {
            return "[DeviceInfo: type:0x" + Integer.toHexString(this.mDeviceType) + " name:" + this.mDeviceName + " addr:" + this.mDeviceAddress + " codec: " + Integer.toHexString(this.mDeviceCodecFormat) + "]";
        }

        /* access modifiers changed from: private */
        public static String makeDeviceListKey(int device, String deviceAddress) {
            return "0x" + Integer.toHexString(device) + ":" + deviceAddress;
        }
    }

    class WiredDeviceConnectionState {
        public final String mAddress;
        public final String mCaller;
        public final String mName;
        public final int mState;
        public final int mType;

        WiredDeviceConnectionState(int type, int state, String address, String name, String caller) {
            this.mType = type;
            this.mState = state;
            this.mAddress = address;
            this.mName = name;
            this.mCaller = caller;
        }
    }

    /* access modifiers changed from: package-private */
    public void onRestoreDevices() {
        synchronized (this.mConnectedDevices) {
            for (int i = 0; i < this.mConnectedDevices.size(); i++) {
                DeviceInfo di = this.mConnectedDevices.valueAt(i);
                AudioSystem.setDeviceConnectionState(di.mDeviceType, 1, di.mDeviceAddress, di.mDeviceName, di.mDeviceCodecFormat);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"AudioDeviceBroker.mDeviceStateLock"})
    public void onSetA2dpSinkConnectionState(BtHelper.BluetoothA2dpDeviceInfo btInfo, int state) {
        BluetoothDevice btDevice = btInfo.getBtDevice();
        int a2dpVolume = btInfo.getVolume();
        Log.d(TAG, "onSetA2dpSinkConnectionState btDevice=" + btDevice + " state=" + state + " is dock=" + btDevice.isBluetoothDock() + " vol=" + a2dpVolume);
        String address = btDevice.getAddress();
        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            address = "";
        }
        AudioEventLogger audioEventLogger = AudioService.sDeviceLogger;
        audioEventLogger.log(new AudioEventLogger.StringEvent("A2DP sink connected: device addr=" + address + " state=" + state + " vol=" + a2dpVolume));
        int a2dpCodec = btInfo.getCodec();
        synchronized (this.mConnectedDevices) {
            DeviceInfo di = this.mConnectedDevices.get(DeviceInfo.makeDeviceListKey(128, btDevice.getAddress()));
            boolean isConnected = di != null;
            if (!isConnected || state == 2) {
                if (!isConnected && state == 2) {
                    if (btDevice.isBluetoothDock()) {
                        this.mDeviceBroker.cancelA2dpDockTimeout();
                        this.mDockAddress = address;
                    } else if (this.mDeviceBroker.hasScheduledA2dpDockTimeout() && this.mDockAddress != null) {
                        this.mDeviceBroker.cancelA2dpDockTimeout();
                        makeA2dpDeviceUnavailableNow(this.mDockAddress, 0);
                    }
                    if (a2dpVolume != -1) {
                        this.mDeviceBroker.postSetVolumeIndexOnDevice(3, a2dpVolume * 10, 128, "onSetA2dpSinkConnectionState");
                    }
                    makeA2dpDeviceAvailable(address, BtHelper.getName(btDevice), "onSetA2dpSinkConnectionState", a2dpCodec);
                }
            } else if (!btDevice.isBluetoothDock()) {
                makeA2dpDeviceUnavailableNow(address, di.mDeviceCodecFormat);
            } else if (state == 0) {
                lambda$disconnectA2dp$1$AudioDeviceInventory(address, 8000);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onSetA2dpSourceConnectionState(BtHelper.BluetoothA2dpDeviceInfo btInfo, int state) {
        BluetoothDevice btDevice = btInfo.getBtDevice();
        Log.d(TAG, "onSetA2dpSourceConnectionState btDevice=" + btDevice + " state=" + state);
        String address = btDevice.getAddress();
        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            address = "";
        }
        synchronized (this.mConnectedDevices) {
            boolean isConnected = this.mConnectedDevices.get(DeviceInfo.makeDeviceListKey(-2147352576, address)) != null;
            if (isConnected && state != 2) {
                lambda$disconnectA2dpSink$3$AudioDeviceInventory(address);
            } else if (!isConnected && state == 2) {
                makeA2dpSrcAvailable(address);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onSetHearingAidConnectionState(BluetoothDevice btDevice, int state, int streamType) {
        String address = btDevice.getAddress();
        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            address = "";
        }
        AudioEventLogger audioEventLogger = AudioService.sDeviceLogger;
        audioEventLogger.log(new AudioEventLogger.StringEvent("onSetHearingAidConnectionState addr=" + address));
        synchronized (this.mConnectedDevices) {
            boolean isConnected = this.mConnectedDevices.get(DeviceInfo.makeDeviceListKey(134217728, btDevice.getAddress())) != null;
            if (isConnected && state != 2) {
                lambda$disconnectHearingAid$5$AudioDeviceInventory(address);
            } else if (!isConnected && state == 2) {
                makeHearingAidDeviceAvailable(address, BtHelper.getName(btDevice), streamType, "onSetHearingAidConnectionState");
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00e3, code lost:
        return;
     */
    @com.android.internal.annotations.GuardedBy({"AudioDeviceBroker.mDeviceStateLock"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onBluetoothA2dpActiveDeviceChange(com.android.server.audio.BtHelper.BluetoothA2dpDeviceInfo r18, int r19) {
        /*
            r17 = this;
            r8 = r17
            r9 = r19
            android.bluetooth.BluetoothDevice r10 = r18.getBtDevice()
            if (r10 != 0) goto L_0x000b
            return
        L_0x000b:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "onBluetoothA2dpActiveDeviceChange btDevice="
            r0.append(r1)
            r0.append(r10)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "AS.AudioDeviceInventory"
            android.util.Log.d(r1, r0)
            int r11 = r18.getVolume()
            int r12 = r18.getCodec()
            java.lang.String r0 = r10.getAddress()
            boolean r1 = android.bluetooth.BluetoothAdapter.checkBluetoothAddress(r0)
            if (r1 != 0) goto L_0x0038
            java.lang.String r0 = ""
            r13 = r0
            goto L_0x0039
        L_0x0038:
            r13 = r0
        L_0x0039:
            com.android.server.audio.AudioEventLogger r0 = com.android.server.audio.AudioService.sDeviceLogger
            com.android.server.audio.AudioEventLogger$StringEvent r1 = new com.android.server.audio.AudioEventLogger$StringEvent
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "onBluetoothA2dpActiveDeviceChange addr="
            r2.append(r3)
            r2.append(r13)
            java.lang.String r3 = " event="
            r2.append(r3)
            java.lang.String r3 = com.android.server.audio.BtHelper.a2dpDeviceEventToString(r19)
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r1.<init>(r2)
            r0.log(r1)
            android.util.ArrayMap<java.lang.String, com.android.server.audio.AudioDeviceInventory$DeviceInfo> r14 = r8.mConnectedDevices
            monitor-enter(r14)
            com.android.server.audio.AudioDeviceBroker r0 = r8.mDeviceBroker     // Catch:{ all -> 0x00e4 }
            boolean r0 = r0.hasScheduledA2dpSinkConnectionState(r10)     // Catch:{ all -> 0x00e4 }
            if (r0 == 0) goto L_0x007a
            com.android.server.audio.AudioEventLogger r0 = com.android.server.audio.AudioService.sDeviceLogger     // Catch:{ all -> 0x00e4 }
            com.android.server.audio.AudioEventLogger$StringEvent r1 = new com.android.server.audio.AudioEventLogger$StringEvent     // Catch:{ all -> 0x00e4 }
            java.lang.String r2 = "A2dp config change ignored"
            r1.<init>(r2)     // Catch:{ all -> 0x00e4 }
            r0.log(r1)     // Catch:{ all -> 0x00e4 }
            monitor-exit(r14)     // Catch:{ all -> 0x00e4 }
            return
        L_0x007a:
            r0 = 128(0x80, float:1.794E-43)
            java.lang.String r1 = com.android.server.audio.AudioDeviceInventory.DeviceInfo.makeDeviceListKey(r0, r13)     // Catch:{ all -> 0x00e4 }
            r15 = r1
            android.util.ArrayMap<java.lang.String, com.android.server.audio.AudioDeviceInventory$DeviceInfo> r1 = r8.mConnectedDevices     // Catch:{ all -> 0x00e4 }
            java.lang.Object r1 = r1.get(r15)     // Catch:{ all -> 0x00e4 }
            com.android.server.audio.AudioDeviceInventory$DeviceInfo r1 = (com.android.server.audio.AudioDeviceInventory.DeviceInfo) r1     // Catch:{ all -> 0x00e4 }
            r7 = r1
            if (r7 != 0) goto L_0x0096
            java.lang.String r0 = "AS.AudioDeviceInventory"
            java.lang.String r1 = "invalid null DeviceInfo in onBluetoothA2dpActiveDeviceChange"
            android.util.Log.e(r0, r1)     // Catch:{ all -> 0x00e4 }
            monitor-exit(r14)     // Catch:{ all -> 0x00e4 }
            return
        L_0x0096:
            r1 = 1
            r2 = 3
            if (r9 != r1) goto L_0x00b4
            r1 = -1
            if (r11 == r1) goto L_0x00c1
            com.android.server.audio.AudioDeviceBroker r1 = r8.mDeviceBroker     // Catch:{ all -> 0x00e4 }
            int r3 = r11 * 10
            java.lang.String r4 = "onBluetoothA2dpActiveDeviceChange"
            r1.postSetVolumeIndexOnDevice(r2, r3, r0, r4)     // Catch:{ all -> 0x00e4 }
            java.lang.String r1 = "AS.AudioDeviceInventory"
            java.lang.String r3 = "Unmuting the stream after setting device Volume."
            android.util.Log.i(r1, r3)     // Catch:{ all -> 0x00e4 }
            com.android.server.audio.AudioDeviceBroker r1 = r8.mDeviceBroker     // Catch:{ all -> 0x00e4 }
            r1.postAccessoryPlugMediaUnmute(r0)     // Catch:{ all -> 0x00e4 }
            goto L_0x00c1
        L_0x00b4:
            if (r9 != 0) goto L_0x00c1
            int r1 = r7.mDeviceCodecFormat     // Catch:{ all -> 0x00e4 }
            if (r1 == r12) goto L_0x00c1
            r7.mDeviceCodecFormat = r12     // Catch:{ all -> 0x00e4 }
            android.util.ArrayMap<java.lang.String, com.android.server.audio.AudioDeviceInventory$DeviceInfo> r1 = r8.mConnectedDevices     // Catch:{ all -> 0x00e4 }
            r1.replace(r15, r7)     // Catch:{ all -> 0x00e4 }
        L_0x00c1:
            java.lang.String r1 = com.android.server.audio.BtHelper.getName(r10)     // Catch:{ all -> 0x00e4 }
            int r0 = android.media.AudioSystem.handleDeviceConfigChange(r0, r13, r1, r12)     // Catch:{ all -> 0x00e4 }
            if (r0 == 0) goto L_0x00e0
            com.android.server.audio.AudioDeviceBroker r0 = r8.mDeviceBroker     // Catch:{ all -> 0x00e4 }
            int r6 = r0.getDeviceForStream(r2)     // Catch:{ all -> 0x00e4 }
            r3 = 0
            r4 = 2
            r5 = 0
            r0 = -1
            r1 = r17
            r2 = r10
            r16 = r7
            r7 = r0
            r1.setBluetoothA2dpDeviceConnectionState(r2, r3, r4, r5, r6, r7)     // Catch:{ all -> 0x00e4 }
            goto L_0x00e2
        L_0x00e0:
            r16 = r7
        L_0x00e2:
            monitor-exit(r14)     // Catch:{ all -> 0x00e4 }
            return
        L_0x00e4:
            r0 = move-exception
            monitor-exit(r14)     // Catch:{ all -> 0x00e4 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.audio.AudioDeviceInventory.onBluetoothA2dpActiveDeviceChange(com.android.server.audio.BtHelper$BluetoothA2dpDeviceInfo, int):void");
    }

    /* access modifiers changed from: package-private */
    public void onMakeA2dpDeviceUnavailableNow(String address, int a2dpCodec) {
        synchronized (this.mConnectedDevices) {
            makeA2dpDeviceUnavailableNow(address, a2dpCodec);
        }
    }

    /* access modifiers changed from: package-private */
    public void onReportNewRoutes() {
        AudioRoutesInfo routes;
        int n = this.mRoutesObservers.beginBroadcast();
        if (n > 0) {
            synchronized (this.mCurAudioRoutes) {
                routes = new AudioRoutesInfo(this.mCurAudioRoutes);
            }
            while (n > 0) {
                n--;
                try {
                    this.mRoutesObservers.getBroadcastItem(n).dispatchAudioRoutesChanged(routes);
                } catch (RemoteException e) {
                }
            }
        }
        this.mRoutesObservers.finishBroadcast();
        this.mDeviceBroker.postObserveDevicesForAllStreams();
    }

    /* access modifiers changed from: package-private */
    public void onSetWiredDeviceConnectionState(WiredDeviceConnectionState wdcs) {
        AudioService.sDeviceLogger.log(new AudioServiceEvents.WiredDevConnectEvent(wdcs));
        synchronized (this.mConnectedDevices) {
            boolean z = true;
            if (wdcs.mState == 0 && (wdcs.mType & DEVICE_OVERRIDE_A2DP_ROUTE_ON_PLUG) != 0) {
                this.mDeviceBroker.setBluetoothA2dpOnInt(true, "onSetWiredDeviceConnectionState state DISCONNECTED");
            }
            if (wdcs.mState != 1) {
                z = false;
            }
            if (handleDeviceConnection(z, wdcs.mType, wdcs.mAddress, wdcs.mName)) {
                if (wdcs.mState != 0) {
                    if ((wdcs.mType & DEVICE_OVERRIDE_A2DP_ROUTE_ON_PLUG) != 0) {
                        this.mDeviceBroker.setBluetoothA2dpOnInt(false, "onSetWiredDeviceConnectionState state not DISCONNECTED");
                    }
                    this.mDeviceBroker.checkMusicActive(wdcs.mType, wdcs.mCaller);
                }
                if (wdcs.mType == 1024) {
                    this.mDeviceBroker.checkVolumeCecOnHdmiConnection(wdcs.mState, wdcs.mCaller);
                }
                sendDeviceConnectionIntent(wdcs.mType, wdcs.mState, wdcs.mAddress, wdcs.mName);
                updateAudioRoutes(wdcs.mType, wdcs.mState);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void onToggleHdmi() {
        synchronized (this.mConnectedDevices) {
            if (this.mConnectedDevices.get(DeviceInfo.makeDeviceListKey(1024, "")) == null) {
                Log.e(TAG, "invalid null DeviceInfo in onToggleHdmi");
                return;
            }
            setWiredDeviceConnectionState(1024, 0, "", "", PackageManagerService.PLATFORM_PACKAGE_NAME);
            setWiredDeviceConnectionState(1024, 1, "", "", PackageManagerService.PLATFORM_PACKAGE_NAME);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean handleDeviceConnection(boolean connect, int device, String address, String deviceName) {
        Slog.i(TAG, "handleDeviceConnection(" + connect + " dev:" + Integer.toHexString(device) + " address:" + address + " name:" + deviceName + ")");
        synchronized (this.mConnectedDevices) {
            String deviceKey = DeviceInfo.makeDeviceListKey(device, address);
            Slog.i(TAG, "deviceKey:" + deviceKey);
            DeviceInfo di = this.mConnectedDevices.get(deviceKey);
            boolean isConnected = di != null;
            Slog.i(TAG, "deviceInfo:" + di + " is(already)Connected:" + isConnected);
            if (connect && !isConnected) {
                int res = AudioSystem.setDeviceConnectionState(device, 1, address, deviceName, 0);
                if (res != 0) {
                    Slog.e(TAG, "not connecting device 0x" + Integer.toHexString(device) + " due to command error " + res);
                    return false;
                }
                this.mConnectedDevices.put(deviceKey, new DeviceInfo(device, deviceName, address, 0));
                this.mDeviceBroker.postAccessoryPlugMediaUnmute(device);
                return true;
            } else if (connect || !isConnected) {
                Log.w(TAG, "handleDeviceConnection() failed, deviceKey=" + deviceKey + ", deviceSpec=" + di + ", connect=" + connect);
                return false;
            } else {
                AudioSystem.setDeviceConnectionState(device, 0, address, deviceName, 0);
                this.mConnectedDevices.remove(deviceKey);
                return true;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void disconnectA2dp() {
        synchronized (this.mConnectedDevices) {
            ArraySet<String> toRemove = new ArraySet<>();
            this.mConnectedDevices.values().forEach(new Consumer(toRemove) {
                private final /* synthetic */ ArraySet f$0;

                {
                    this.f$0 = r1;
                }

                public final void accept(Object obj) {
                    AudioDeviceInventory.lambda$disconnectA2dp$0(this.f$0, (AudioDeviceInventory.DeviceInfo) obj);
                }
            });
            if (toRemove.size() > 0) {
                toRemove.stream().forEach(new Consumer(checkSendBecomingNoisyIntentInt(128, 0, 0)) {
                    private final /* synthetic */ int f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void accept(Object obj) {
                        AudioDeviceInventory.this.lambda$disconnectA2dp$1$AudioDeviceInventory(this.f$1, (String) obj);
                    }
                });
            }
        }
    }

    static /* synthetic */ void lambda$disconnectA2dp$0(ArraySet toRemove, DeviceInfo deviceInfo) {
        if (deviceInfo.mDeviceType == 128) {
            toRemove.add(deviceInfo.mDeviceAddress);
        }
    }

    /* access modifiers changed from: package-private */
    public void disconnectA2dpSink() {
        synchronized (this.mConnectedDevices) {
            ArraySet<String> toRemove = new ArraySet<>();
            this.mConnectedDevices.values().forEach(new Consumer(toRemove) {
                private final /* synthetic */ ArraySet f$0;

                {
                    this.f$0 = r1;
                }

                public final void accept(Object obj) {
                    AudioDeviceInventory.lambda$disconnectA2dpSink$2(this.f$0, (AudioDeviceInventory.DeviceInfo) obj);
                }
            });
            toRemove.stream().forEach(new Consumer() {
                public final void accept(Object obj) {
                    AudioDeviceInventory.this.lambda$disconnectA2dpSink$3$AudioDeviceInventory((String) obj);
                }
            });
        }
    }

    static /* synthetic */ void lambda$disconnectA2dpSink$2(ArraySet toRemove, DeviceInfo deviceInfo) {
        if (deviceInfo.mDeviceType == -2147352576) {
            toRemove.add(deviceInfo.mDeviceAddress);
        }
    }

    /* access modifiers changed from: package-private */
    public void disconnectHearingAid() {
        synchronized (this.mConnectedDevices) {
            ArraySet<String> toRemove = new ArraySet<>();
            this.mConnectedDevices.values().forEach(new Consumer(toRemove) {
                private final /* synthetic */ ArraySet f$0;

                {
                    this.f$0 = r1;
                }

                public final void accept(Object obj) {
                    AudioDeviceInventory.lambda$disconnectHearingAid$4(this.f$0, (AudioDeviceInventory.DeviceInfo) obj);
                }
            });
            if (toRemove.size() > 0) {
                int checkSendBecomingNoisyIntentInt = checkSendBecomingNoisyIntentInt(134217728, 0, 0);
                toRemove.stream().forEach(new Consumer() {
                    public final void accept(Object obj) {
                        AudioDeviceInventory.this.lambda$disconnectHearingAid$5$AudioDeviceInventory((String) obj);
                    }
                });
            }
        }
    }

    static /* synthetic */ void lambda$disconnectHearingAid$4(ArraySet toRemove, DeviceInfo deviceInfo) {
        if (deviceInfo.mDeviceType == 134217728) {
            toRemove.add(deviceInfo.mDeviceAddress);
        }
    }

    /* access modifiers changed from: package-private */
    public int checkSendBecomingNoisyIntent(int device, int state, int musicDevice) {
        int checkSendBecomingNoisyIntentInt;
        synchronized (this.mConnectedDevices) {
            checkSendBecomingNoisyIntentInt = checkSendBecomingNoisyIntentInt(device, state, musicDevice);
        }
        return checkSendBecomingNoisyIntentInt;
    }

    /* access modifiers changed from: package-private */
    public AudioRoutesInfo startWatchingRoutes(IAudioRoutesObserver observer) {
        AudioRoutesInfo routes;
        synchronized (this.mCurAudioRoutes) {
            routes = new AudioRoutesInfo(this.mCurAudioRoutes);
            this.mRoutesObservers.register(observer);
        }
        return routes;
    }

    /* access modifiers changed from: package-private */
    public AudioRoutesInfo getCurAudioRoutes() {
        return this.mCurAudioRoutes;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"AudioDeviceBroker.mDeviceStateLock"})
    public void setBluetoothA2dpDeviceConnectionState(BluetoothDevice device, int state, int profile, boolean suppressNoisyIntent, int musicDevice, int a2dpVolume) {
        if (profile == 2 || profile == 11) {
            synchronized (this.mConnectedDevices) {
                int intState = 0;
                if (profile == 2 && !suppressNoisyIntent) {
                    if (state == 2) {
                        intState = 1;
                    }
                    intState = checkSendBecomingNoisyIntentInt(128, intState, musicDevice);
                }
                int a2dpCodec = this.mDeviceBroker.getA2dpCodec(device);
                Log.i(TAG, "setBluetoothA2dpDeviceConnectionState device: " + device + " state: " + state + " delay(ms): " + intState + "codec:" + a2dpCodec + " suppressNoisyIntent: " + suppressNoisyIntent);
                BtHelper.BluetoothA2dpDeviceInfo a2dpDeviceInfo = new BtHelper.BluetoothA2dpDeviceInfo(device, a2dpVolume, a2dpCodec);
                if (profile != 2) {
                    this.mDeviceBroker.postA2dpSourceConnection(state, a2dpDeviceInfo, intState);
                } else if (intState == 0) {
                    onSetA2dpSinkConnectionState(a2dpDeviceInfo, state);
                } else {
                    this.mDeviceBroker.postA2dpSinkConnection(state, a2dpDeviceInfo, intState);
                }
            }
            return;
        }
        throw new IllegalArgumentException("invalid profile " + profile);
    }

    /* access modifiers changed from: package-private */
    public void handleBluetoothA2dpActiveDeviceChangeExt(BluetoothDevice device, int state, int profile, boolean suppressNoisyIntent, int a2dpVolume) {
        BluetoothDevice bluetoothDevice = device;
        if (state == 0) {
            this.mDeviceBroker.postBluetoothA2dpDeviceConnectionStateSuppressNoisyIntent(device, state, profile, suppressNoisyIntent, a2dpVolume);
            return;
        }
        synchronized (this.mConnectedDevices) {
            try {
                String address = device.getAddress();
                int a2dpCodec = this.mDeviceBroker.getA2dpCodec(device);
                String deviceKey = DeviceInfo.makeDeviceListKey(128, address);
                if (this.mConnectedDevices.get(deviceKey) != null) {
                    this.mDeviceBroker.postBluetoothA2dpDeviceConfigChange(device);
                    return;
                }
                int i = 0;
                while (i < this.mConnectedDevices.size()) {
                    DeviceInfo deviceInfo = this.mConnectedDevices.valueAt(i);
                    if (deviceInfo.mDeviceType != 128) {
                        i++;
                    } else {
                        this.mConnectedDevices.remove(this.mConnectedDevices.keyAt(i));
                        this.mConnectedDevices.put(deviceKey, new DeviceInfo(128, BtHelper.getName(device), address, a2dpCodec));
                        if (BtHelper.isTwsPlusSwitch(device, deviceInfo.mDeviceAddress)) {
                            Log.d(TAG, "TWS+ device switch");
                            return;
                        }
                        try {
                            this.mDeviceBroker.postA2dpActiveDeviceChange(new BtHelper.BluetoothA2dpDeviceInfo(device, a2dpVolume, a2dpCodec));
                            return;
                        } catch (Throwable th) {
                            th = th;
                            throw th;
                        }
                    }
                }
                int i2 = a2dpVolume;
                this.mDeviceBroker.postBluetoothA2dpDeviceConnectionStateSuppressNoisyIntent(device, state, profile, suppressNoisyIntent, a2dpVolume);
            } catch (Throwable th2) {
                th = th2;
                int i3 = a2dpVolume;
                throw th;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int setWiredDeviceConnectionState(int type, int state, String address, String name, String caller) {
        int delay;
        synchronized (this.mConnectedDevices) {
            int i = type;
            delay = checkSendBecomingNoisyIntentInt(type, state, 0);
            this.mDeviceBroker.postSetWiredDeviceConnectionState(new WiredDeviceConnectionState(type, state, address, name, caller), delay);
        }
        return delay;
    }

    /* access modifiers changed from: package-private */
    public int setBluetoothHearingAidDeviceConnectionState(BluetoothDevice device, int state, boolean suppressNoisyIntent, int musicDevice) {
        int intState;
        synchronized (this.mConnectedDevices) {
            intState = 0;
            if (!suppressNoisyIntent) {
                if (state == 2) {
                    intState = 1;
                }
                intState = checkSendBecomingNoisyIntentInt(134217728, intState, musicDevice);
            }
            this.mDeviceBroker.postSetHearingAidConnectionState(state, device, intState);
        }
        return intState;
    }

    @GuardedBy({"mConnectedDevices"})
    private void makeA2dpDeviceAvailable(String address, String name, String eventSource, int a2dpCodec) {
        this.mDeviceBroker.setBluetoothA2dpOnInt(true, eventSource);
        int res = AudioSystem.setDeviceConnectionState(128, 1, address, name, a2dpCodec);
        if (res != 0) {
            Slog.e(TAG, "not connecting name " + name + " due to command error " + res);
            return;
        }
        AudioSystem.setParameters("A2dpSuspended=false");
        this.mConnectedDevices.put(DeviceInfo.makeDeviceListKey(128, address), new DeviceInfo(128, name, address, a2dpCodec));
        this.mDeviceBroker.postAccessoryPlugMediaUnmute(128);
        setCurrentAudioRouteNameIfPossible(name);
    }

    @GuardedBy({"mConnectedDevices"})
    private void makeA2dpDeviceUnavailableNow(String address, int a2dpCodec) {
        if (address != null) {
            this.mDeviceBroker.setAvrcpAbsoluteVolumeSupported(false);
            AudioSystem.setDeviceConnectionState(128, 0, address, "", a2dpCodec);
            this.mConnectedDevices.remove(DeviceInfo.makeDeviceListKey(128, address));
            setCurrentAudioRouteNameIfPossible((String) null);
            if (this.mDockAddress == address) {
                this.mDockAddress = null;
            }
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mConnectedDevices"})
    /* renamed from: makeA2dpDeviceUnavailableLater */
    public void lambda$disconnectA2dp$1$AudioDeviceInventory(String address, int delayMs) {
        int a2dpCodec;
        AudioSystem.setParameters("A2dpSuspended=true");
        String deviceKey = DeviceInfo.makeDeviceListKey(128, address);
        DeviceInfo deviceInfo = this.mConnectedDevices.get(deviceKey);
        if (deviceInfo != null) {
            a2dpCodec = deviceInfo.mDeviceCodecFormat;
        } else {
            a2dpCodec = 0;
        }
        this.mConnectedDevices.remove(deviceKey);
        this.mDeviceBroker.setA2dpDockTimeout(address, a2dpCodec, delayMs);
    }

    @GuardedBy({"mConnectedDevices"})
    private void makeA2dpSrcAvailable(String address) {
        AudioSystem.setDeviceConnectionState(-2147352576, 1, address, "", 0);
        this.mConnectedDevices.put(DeviceInfo.makeDeviceListKey(-2147352576, address), new DeviceInfo(-2147352576, "", address, 0));
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mConnectedDevices"})
    /* renamed from: makeA2dpSrcUnavailable */
    public void lambda$disconnectA2dpSink$3$AudioDeviceInventory(String address) {
        AudioSystem.setDeviceConnectionState(-2147352576, 0, address, "", 0);
        this.mConnectedDevices.remove(DeviceInfo.makeDeviceListKey(-2147352576, address));
    }

    @GuardedBy({"mConnectedDevices"})
    private void makeHearingAidDeviceAvailable(String address, String name, int streamType, String eventSource) {
        this.mDeviceBroker.postSetHearingAidVolumeIndex(this.mDeviceBroker.getVssVolumeForDevice(streamType, 134217728), streamType);
        AudioSystem.setDeviceConnectionState(134217728, 1, address, name, 0);
        this.mConnectedDevices.put(DeviceInfo.makeDeviceListKey(134217728, address), new DeviceInfo(134217728, name, address, 0));
        this.mDeviceBroker.postAccessoryPlugMediaUnmute(134217728);
        this.mDeviceBroker.postApplyVolumeOnDevice(streamType, 134217728, "makeHearingAidDeviceAvailable");
        setCurrentAudioRouteNameIfPossible(name);
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mConnectedDevices"})
    /* renamed from: makeHearingAidDeviceUnavailable */
    public void lambda$disconnectHearingAid$5$AudioDeviceInventory(String address) {
        AudioSystem.setDeviceConnectionState(134217728, 0, address, "", 0);
        this.mConnectedDevices.remove(DeviceInfo.makeDeviceListKey(134217728, address));
        setCurrentAudioRouteNameIfPossible((String) null);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0021, code lost:
        return;
     */
    @com.android.internal.annotations.GuardedBy({"mConnectedDevices"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void setCurrentAudioRouteNameIfPossible(java.lang.String r3) {
        /*
            r2 = this;
            android.media.AudioRoutesInfo r0 = r2.mCurAudioRoutes
            monitor-enter(r0)
            android.media.AudioRoutesInfo r1 = r2.mCurAudioRoutes     // Catch:{ all -> 0x0022 }
            java.lang.CharSequence r1 = r1.bluetoothName     // Catch:{ all -> 0x0022 }
            boolean r1 = android.text.TextUtils.equals(r1, r3)     // Catch:{ all -> 0x0022 }
            if (r1 == 0) goto L_0x000f
            monitor-exit(r0)     // Catch:{ all -> 0x0022 }
            return
        L_0x000f:
            if (r3 != 0) goto L_0x0017
            boolean r1 = r2.isCurrentDeviceConnected()     // Catch:{ all -> 0x0022 }
            if (r1 != 0) goto L_0x0020
        L_0x0017:
            android.media.AudioRoutesInfo r1 = r2.mCurAudioRoutes     // Catch:{ all -> 0x0022 }
            r1.bluetoothName = r3     // Catch:{ all -> 0x0022 }
            com.android.server.audio.AudioDeviceBroker r1 = r2.mDeviceBroker     // Catch:{ all -> 0x0022 }
            r1.postReportNewRoutes()     // Catch:{ all -> 0x0022 }
        L_0x0020:
            monitor-exit(r0)     // Catch:{ all -> 0x0022 }
            return
        L_0x0022:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0022 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.audio.AudioDeviceInventory.setCurrentAudioRouteNameIfPossible(java.lang.String):void");
    }

    @GuardedBy({"mConnectedDevices"})
    private boolean isCurrentDeviceConnected() {
        return this.mConnectedDevices.values().stream().anyMatch(new Predicate() {
            public final boolean test(Object obj) {
                return AudioDeviceInventory.this.lambda$isCurrentDeviceConnected$6$AudioDeviceInventory((AudioDeviceInventory.DeviceInfo) obj);
            }
        });
    }

    public /* synthetic */ boolean lambda$isCurrentDeviceConnected$6$AudioDeviceInventory(DeviceInfo deviceInfo) {
        return TextUtils.equals(deviceInfo.mDeviceName, this.mCurAudioRoutes.bluetoothName);
    }

    @GuardedBy({"mConnectedDevices"})
    private int checkSendBecomingNoisyIntentInt(int device, int state, int musicDevice) {
        if (state != 0 || (this.mBecomingNoisyIntentDevices & device) == 0) {
            return 0;
        }
        int devices = 0;
        for (int i = 0; i < this.mConnectedDevices.size(); i++) {
            int dev = this.mConnectedDevices.valueAt(i).mDeviceType;
            if ((Integer.MIN_VALUE & dev) == 0 && (this.mBecomingNoisyIntentDevices & dev) != 0) {
                devices |= dev;
            }
        }
        if (musicDevice == 0) {
            musicDevice = this.mDeviceBroker.getDeviceForStream(3);
        }
        if ((device != musicDevice && !this.mDeviceBroker.isInCommunication()) || device != devices || this.mDeviceBroker.hasMediaDynamicPolicy() || (32768 & musicDevice) != 0) {
            return 0;
        }
        if (AudioSystem.isStreamActive(3, 0) || this.mDeviceBroker.hasAudioFocusUsers()) {
            this.mDeviceBroker.postBroadcastBecomingNoisy();
            return SystemProperties.getInt("audio.sys.noisy.broadcast.delay", 700);
        }
        AudioService.sDeviceLogger.log(new AudioEventLogger.StringEvent("dropping ACTION_AUDIO_BECOMING_NOISY").printLog(TAG));
        return 0;
    }

    private void sendDeviceConnectionIntent(int device, int state, String address, String deviceName) {
        Slog.i(TAG, "sendDeviceConnectionIntent(dev:0x" + Integer.toHexString(device) + " state:0x" + Integer.toHexString(state) + " address:" + address + " name:" + deviceName + ");");
        Intent intent = new Intent();
        if (device != -2147479552) {
            int i = 1;
            if (device != -2113929216) {
                if (device != 4) {
                    if (device != 8) {
                        if (device != 1024) {
                            if (device != 131072) {
                                if (device != 262144) {
                                    if (device == 67108864) {
                                        intent.setAction("android.intent.action.HEADSET_PLUG");
                                        if (AudioSystem.getDeviceConnectionState(-2113929216, "") != 1) {
                                            i = 0;
                                        }
                                        intent.putExtra("microphone", i);
                                    }
                                }
                            }
                        }
                        configureHdmiPlugIntent(intent, state);
                    }
                    intent.setAction("android.intent.action.HEADSET_PLUG");
                    intent.putExtra("microphone", 0);
                } else {
                    intent.setAction("android.intent.action.HEADSET_PLUG");
                    intent.putExtra("microphone", 1);
                }
            } else if (AudioSystem.getDeviceConnectionState(BroadcastQueueInjector.FLAG_IMMUTABLE, "") == 1) {
                intent.setAction("android.intent.action.HEADSET_PLUG");
                intent.putExtra("microphone", 1);
            } else {
                return;
            }
        } else {
            intent.setAction("android.media.extra.AUDIO_MIC_PLUG_STATE");
        }
        if (intent.getAction() != null) {
            intent.putExtra(CONNECT_INTENT_KEY_STATE, state);
            intent.putExtra(CONNECT_INTENT_KEY_ADDRESS, address);
            intent.putExtra(CONNECT_INTENT_KEY_PORT_NAME, deviceName);
            intent.addFlags(1073741824);
            long ident = Binder.clearCallingIdentity();
            try {
                ActivityManager.broadcastStickyIntent(intent, -2);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:13:0x001a, code lost:
        if (r5 != 67108864) goto L_0x0027;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0048, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateAudioRoutes(int r5, int r6) {
        /*
            r4 = this;
            r0 = 0
            r1 = 4
            if (r5 == r1) goto L_0x0025
            r1 = 8
            if (r5 == r1) goto L_0x0023
            r1 = 1024(0x400, float:1.435E-42)
            if (r5 == r1) goto L_0x0020
            r1 = 16384(0x4000, float:2.2959E-41)
            if (r5 == r1) goto L_0x001d
            r1 = 131072(0x20000, float:1.83671E-40)
            if (r5 == r1) goto L_0x0023
            r1 = 262144(0x40000, float:3.67342E-40)
            if (r5 == r1) goto L_0x0020
            r1 = 67108864(0x4000000, float:1.5046328E-36)
            if (r5 == r1) goto L_0x001d
            goto L_0x0027
        L_0x001d:
            r0 = 16
            goto L_0x0027
        L_0x0020:
            r0 = 8
            goto L_0x0027
        L_0x0023:
            r0 = 2
            goto L_0x0027
        L_0x0025:
            r0 = 1
        L_0x0027:
            android.media.AudioRoutesInfo r1 = r4.mCurAudioRoutes
            monitor-enter(r1)
            if (r0 != 0) goto L_0x002e
            monitor-exit(r1)     // Catch:{ all -> 0x0049 }
            return
        L_0x002e:
            android.media.AudioRoutesInfo r2 = r4.mCurAudioRoutes     // Catch:{ all -> 0x0049 }
            int r2 = r2.mainType     // Catch:{ all -> 0x0049 }
            if (r6 == 0) goto L_0x0036
            r2 = r2 | r0
            goto L_0x0038
        L_0x0036:
            int r3 = ~r0     // Catch:{ all -> 0x0049 }
            r2 = r2 & r3
        L_0x0038:
            android.media.AudioRoutesInfo r3 = r4.mCurAudioRoutes     // Catch:{ all -> 0x0049 }
            int r3 = r3.mainType     // Catch:{ all -> 0x0049 }
            if (r2 == r3) goto L_0x0047
            android.media.AudioRoutesInfo r3 = r4.mCurAudioRoutes     // Catch:{ all -> 0x0049 }
            r3.mainType = r2     // Catch:{ all -> 0x0049 }
            com.android.server.audio.AudioDeviceBroker r3 = r4.mDeviceBroker     // Catch:{ all -> 0x0049 }
            r3.postReportNewRoutes()     // Catch:{ all -> 0x0049 }
        L_0x0047:
            monitor-exit(r1)     // Catch:{ all -> 0x0049 }
            return
        L_0x0049:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0049 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.audio.AudioDeviceInventory.updateAudioRoutes(int, int):void");
    }

    private void configureHdmiPlugIntent(Intent intent, int state) {
        Intent intent2 = intent;
        int i = state;
        intent2.setAction("android.media.action.HDMI_AUDIO_PLUG");
        intent2.putExtra("android.media.extra.AUDIO_PLUG_STATE", i);
        if (i == 1) {
            ArrayList<AudioPort> ports = new ArrayList<>();
            int status = AudioSystem.listAudioPorts(ports, new int[1]);
            if (status != 0) {
                Log.e(TAG, "listAudioPorts error " + status + " in configureHdmiPlugIntent");
                return;
            }
            Iterator<AudioPort> it = ports.iterator();
            while (it.hasNext()) {
                AudioDevicePort next = it.next();
                if (next instanceof AudioDevicePort) {
                    AudioDevicePort devicePort = next;
                    if (devicePort.type() == 1024 || devicePort.type() == 262144) {
                        int[] formats = AudioFormat.filterPublicFormats(devicePort.formats());
                        if (formats.length > 0) {
                            ArrayList<Integer> encodingList = new ArrayList<>(1);
                            for (int format : formats) {
                                if (format != 0) {
                                    encodingList.add(Integer.valueOf(format));
                                }
                            }
                            intent2.putExtra("android.media.extra.ENCODINGS", encodingList.stream().mapToInt($$Lambda$AudioDeviceInventory$u_r8SlQF9hKqpPB7hUtpbqyzdc.INSTANCE).toArray());
                        }
                        int maxChannels = 0;
                        for (int mask : devicePort.channelMasks()) {
                            int channelCount = AudioFormat.channelCountFromOutChannelMask(mask);
                            if (channelCount > maxChannels) {
                                maxChannels = channelCount;
                            }
                        }
                        intent2.putExtra("android.media.extra.MAX_CHANNEL_COUNT", maxChannels);
                    }
                }
            }
        }
    }
}
