package com.android.server.tv;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.hdmi.HdmiDeviceInfo;
import android.hardware.hdmi.HdmiHotplugEvent;
import android.hardware.hdmi.IHdmiControlService;
import android.hardware.hdmi.IHdmiDeviceEventListener;
import android.hardware.hdmi.IHdmiHotplugEventListener;
import android.hardware.hdmi.IHdmiSystemAudioModeChangeListener;
import android.media.AudioDevicePort;
import android.media.AudioFormat;
import android.media.AudioGain;
import android.media.AudioGainConfig;
import android.media.AudioManager;
import android.media.AudioPatch;
import android.media.AudioPort;
import android.media.AudioPortConfig;
import android.media.tv.ITvInputHardware;
import android.media.tv.ITvInputHardwareCallback;
import android.media.tv.TvInputHardwareInfo;
import android.media.tv.TvInputInfo;
import android.media.tv.TvStreamConfig;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.ArrayMap;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.tv.TvInputHal;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

class TvInputHardwareManager implements TvInputHal.Callback {
    /* access modifiers changed from: private */
    public static final String TAG = TvInputHardwareManager.class.getSimpleName();
    /* access modifiers changed from: private */
    public final AudioManager mAudioManager;
    /* access modifiers changed from: private */
    public final SparseArray<Connection> mConnections = new SparseArray<>();
    private final Context mContext;
    private int mCurrentIndex = 0;
    private int mCurrentMaxIndex = 0;
    /* access modifiers changed from: private */
    public final TvInputHal mHal = new TvInputHal(this);
    /* access modifiers changed from: private */
    public final Handler mHandler = new ListenerHandler();
    /* access modifiers changed from: private */
    public final SparseArray<String> mHardwareInputIdMap = new SparseArray<>();
    private final List<TvInputHardwareInfo> mHardwareList = new ArrayList();
    private final IHdmiDeviceEventListener mHdmiDeviceEventListener = new HdmiDeviceEventListener();
    /* access modifiers changed from: private */
    public final List<HdmiDeviceInfo> mHdmiDeviceList = new LinkedList();
    private final IHdmiHotplugEventListener mHdmiHotplugEventListener = new HdmiHotplugEventListener();
    /* access modifiers changed from: private */
    public final SparseArray<String> mHdmiInputIdMap = new SparseArray<>();
    /* access modifiers changed from: private */
    public final SparseBooleanArray mHdmiStateMap = new SparseBooleanArray();
    private final IHdmiSystemAudioModeChangeListener mHdmiSystemAudioModeChangeListener = new HdmiSystemAudioModeChangeListener();
    private final Map<String, TvInputInfo> mInputMap = new ArrayMap();
    /* access modifiers changed from: private */
    public final Listener mListener;
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    /* access modifiers changed from: private */
    public final List<Message> mPendingHdmiDeviceEvents = new LinkedList();
    private final BroadcastReceiver mVolumeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            TvInputHardwareManager.this.handleVolumeChange(context, intent);
        }
    };

    interface Listener {
        void onHardwareDeviceAdded(TvInputHardwareInfo tvInputHardwareInfo);

        void onHardwareDeviceRemoved(TvInputHardwareInfo tvInputHardwareInfo);

        void onHdmiDeviceAdded(HdmiDeviceInfo hdmiDeviceInfo);

        void onHdmiDeviceRemoved(HdmiDeviceInfo hdmiDeviceInfo);

        void onHdmiDeviceUpdated(String str, HdmiDeviceInfo hdmiDeviceInfo);

        void onStateChanged(String str, int i);
    }

    public TvInputHardwareManager(Context context, Listener listener) {
        this.mContext = context;
        this.mListener = listener;
        this.mAudioManager = (AudioManager) context.getSystemService("audio");
        this.mHal.init();
    }

    public void onBootPhase(int phase) {
        if (phase == 500) {
            IHdmiControlService hdmiControlService = IHdmiControlService.Stub.asInterface(ServiceManager.getService("hdmi_control"));
            if (hdmiControlService != null) {
                try {
                    hdmiControlService.addHotplugEventListener(this.mHdmiHotplugEventListener);
                    hdmiControlService.addDeviceEventListener(this.mHdmiDeviceEventListener);
                    hdmiControlService.addSystemAudioModeChangeListener(this.mHdmiSystemAudioModeChangeListener);
                    this.mHdmiDeviceList.addAll(hdmiControlService.getInputDevices());
                } catch (RemoteException e) {
                    Slog.w(TAG, "Error registering listeners to HdmiControlService:", e);
                }
            } else {
                Slog.w(TAG, "HdmiControlService is not available");
            }
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.media.VOLUME_CHANGED_ACTION");
            filter.addAction("android.media.STREAM_MUTE_CHANGED_ACTION");
            this.mContext.registerReceiver(this.mVolumeReceiver, filter);
            updateVolume();
        }
    }

    public void onDeviceAvailable(TvInputHardwareInfo info, TvStreamConfig[] configs) {
        synchronized (this.mLock) {
            Connection connection = new Connection(info);
            connection.updateConfigsLocked(configs);
            this.mConnections.put(info.getDeviceId(), connection);
            buildHardwareListLocked();
            this.mHandler.obtainMessage(2, 0, 0, info).sendToTarget();
            if (info.getType() == 9) {
                processPendingHdmiDeviceEventsLocked();
            }
        }
    }

    private void buildHardwareListLocked() {
        this.mHardwareList.clear();
        for (int i = 0; i < this.mConnections.size(); i++) {
            this.mHardwareList.add(this.mConnections.valueAt(i).getHardwareInfoLocked());
        }
    }

    public void onDeviceUnavailable(int deviceId) {
        synchronized (this.mLock) {
            Connection connection = this.mConnections.get(deviceId);
            if (connection == null) {
                String str = TAG;
                Slog.e(str, "onDeviceUnavailable: Cannot find a connection with " + deviceId);
                return;
            }
            connection.resetLocked((TvInputHardwareImpl) null, (ITvInputHardwareCallback) null, (TvInputInfo) null, (Integer) null, (Integer) null);
            this.mConnections.remove(deviceId);
            buildHardwareListLocked();
            TvInputHardwareInfo info = connection.getHardwareInfoLocked();
            if (info.getType() == 9) {
                Iterator<HdmiDeviceInfo> it = this.mHdmiDeviceList.iterator();
                while (it.hasNext()) {
                    HdmiDeviceInfo deviceInfo = it.next();
                    if (deviceInfo.getPortId() == info.getHdmiPortId()) {
                        this.mHandler.obtainMessage(5, 0, 0, deviceInfo).sendToTarget();
                        it.remove();
                    }
                }
            }
            this.mHandler.obtainMessage(3, 0, 0, info).sendToTarget();
        }
    }

    public void onStreamConfigurationChanged(int deviceId, TvStreamConfig[] configs) {
        synchronized (this.mLock) {
            Connection connection = this.mConnections.get(deviceId);
            if (connection == null) {
                String str = TAG;
                Slog.e(str, "StreamConfigurationChanged: Cannot find a connection with " + deviceId);
                return;
            }
            int previousConfigsLength = connection.getConfigsLengthLocked();
            connection.updateConfigsLocked(configs);
            String inputId = this.mHardwareInputIdMap.get(deviceId);
            if (inputId != null) {
                if ((previousConfigsLength == 0) != (connection.getConfigsLengthLocked() == 0)) {
                    this.mHandler.obtainMessage(1, connection.getInputStateLocked(), 0, inputId).sendToTarget();
                }
            }
            ITvInputHardwareCallback callback = connection.getCallbackLocked();
            if (callback != null) {
                try {
                    callback.onStreamConfigChanged(configs);
                } catch (RemoteException e) {
                    Slog.e(TAG, "error in onStreamConfigurationChanged", e);
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0033, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onFirstFrameCaptured(int r6, int r7) {
        /*
            r5 = this;
            java.lang.Object r0 = r5.mLock
            monitor-enter(r0)
            android.util.SparseArray<com.android.server.tv.TvInputHardwareManager$Connection> r1 = r5.mConnections     // Catch:{ all -> 0x0034 }
            java.lang.Object r1 = r1.get(r6)     // Catch:{ all -> 0x0034 }
            com.android.server.tv.TvInputHardwareManager$Connection r1 = (com.android.server.tv.TvInputHardwareManager.Connection) r1     // Catch:{ all -> 0x0034 }
            if (r1 != 0) goto L_0x0025
            java.lang.String r2 = TAG     // Catch:{ all -> 0x0034 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0034 }
            r3.<init>()     // Catch:{ all -> 0x0034 }
            java.lang.String r4 = "FirstFrameCaptured: Cannot find a connection with "
            r3.append(r4)     // Catch:{ all -> 0x0034 }
            r3.append(r6)     // Catch:{ all -> 0x0034 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0034 }
            android.util.Slog.e(r2, r3)     // Catch:{ all -> 0x0034 }
            monitor-exit(r0)     // Catch:{ all -> 0x0034 }
            return
        L_0x0025:
            java.lang.Runnable r2 = r1.getOnFirstFrameCapturedLocked()     // Catch:{ all -> 0x0034 }
            if (r2 == 0) goto L_0x0032
            r2.run()     // Catch:{ all -> 0x0034 }
            r3 = 0
            r1.setOnFirstFrameCapturedLocked(r3)     // Catch:{ all -> 0x0034 }
        L_0x0032:
            monitor-exit(r0)     // Catch:{ all -> 0x0034 }
            return
        L_0x0034:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0034 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputHardwareManager.onFirstFrameCaptured(int, int):void");
    }

    public List<TvInputHardwareInfo> getHardwareList() {
        List<TvInputHardwareInfo> unmodifiableList;
        synchronized (this.mLock) {
            unmodifiableList = Collections.unmodifiableList(this.mHardwareList);
        }
        return unmodifiableList;
    }

    public List<HdmiDeviceInfo> getHdmiDeviceList() {
        List<HdmiDeviceInfo> unmodifiableList;
        synchronized (this.mLock) {
            unmodifiableList = Collections.unmodifiableList(this.mHdmiDeviceList);
        }
        return unmodifiableList;
    }

    private boolean checkUidChangedLocked(Connection connection, int callingUid, int resolvedUserId) {
        Integer connectionCallingUid = connection.getCallingUidLocked();
        Integer connectionResolvedUserId = connection.getResolvedUserIdLocked();
        return connectionCallingUid == null || connectionResolvedUserId == null || connectionCallingUid.intValue() != callingUid || connectionResolvedUserId.intValue() != resolvedUserId;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00b9, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void addHardwareInput(int r10, android.media.tv.TvInputInfo r11) {
        /*
            r9 = this;
            java.lang.Object r0 = r9.mLock
            monitor-enter(r0)
            android.util.SparseArray<java.lang.String> r1 = r9.mHardwareInputIdMap     // Catch:{ all -> 0x00ba }
            java.lang.Object r1 = r1.get(r10)     // Catch:{ all -> 0x00ba }
            java.lang.String r1 = (java.lang.String) r1     // Catch:{ all -> 0x00ba }
            if (r1 == 0) goto L_0x0041
            java.lang.String r2 = TAG     // Catch:{ all -> 0x00ba }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ba }
            r3.<init>()     // Catch:{ all -> 0x00ba }
            java.lang.String r4 = "Trying to override previous registration: old = "
            r3.append(r4)     // Catch:{ all -> 0x00ba }
            java.util.Map<java.lang.String, android.media.tv.TvInputInfo> r4 = r9.mInputMap     // Catch:{ all -> 0x00ba }
            java.lang.Object r4 = r4.get(r1)     // Catch:{ all -> 0x00ba }
            r3.append(r4)     // Catch:{ all -> 0x00ba }
            java.lang.String r4 = ":"
            r3.append(r4)     // Catch:{ all -> 0x00ba }
            r3.append(r10)     // Catch:{ all -> 0x00ba }
            java.lang.String r4 = ", new = "
            r3.append(r4)     // Catch:{ all -> 0x00ba }
            r3.append(r11)     // Catch:{ all -> 0x00ba }
            java.lang.String r4 = ":"
            r3.append(r4)     // Catch:{ all -> 0x00ba }
            r3.append(r10)     // Catch:{ all -> 0x00ba }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x00ba }
            android.util.Slog.w(r2, r3)     // Catch:{ all -> 0x00ba }
        L_0x0041:
            android.util.SparseArray<java.lang.String> r2 = r9.mHardwareInputIdMap     // Catch:{ all -> 0x00ba }
            java.lang.String r3 = r11.getId()     // Catch:{ all -> 0x00ba }
            r2.put(r10, r3)     // Catch:{ all -> 0x00ba }
            java.util.Map<java.lang.String, android.media.tv.TvInputInfo> r2 = r9.mInputMap     // Catch:{ all -> 0x00ba }
            java.lang.String r3 = r11.getId()     // Catch:{ all -> 0x00ba }
            r2.put(r3, r11)     // Catch:{ all -> 0x00ba }
            r2 = 0
            r3 = r2
        L_0x0055:
            android.util.SparseBooleanArray r4 = r9.mHdmiStateMap     // Catch:{ all -> 0x00ba }
            int r4 = r4.size()     // Catch:{ all -> 0x00ba }
            r5 = 1
            if (r3 >= r4) goto L_0x009d
            android.util.SparseBooleanArray r4 = r9.mHdmiStateMap     // Catch:{ all -> 0x00ba }
            int r4 = r4.keyAt(r3)     // Catch:{ all -> 0x00ba }
            android.media.tv.TvInputHardwareInfo r4 = r9.findHardwareInfoForHdmiPortLocked(r4)     // Catch:{ all -> 0x00ba }
            if (r4 != 0) goto L_0x006b
            goto L_0x009a
        L_0x006b:
            android.util.SparseArray<java.lang.String> r6 = r9.mHardwareInputIdMap     // Catch:{ all -> 0x00ba }
            int r7 = r4.getDeviceId()     // Catch:{ all -> 0x00ba }
            java.lang.Object r6 = r6.get(r7)     // Catch:{ all -> 0x00ba }
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ all -> 0x00ba }
            if (r6 == 0) goto L_0x009a
            java.lang.String r7 = r11.getId()     // Catch:{ all -> 0x00ba }
            boolean r7 = r6.equals(r7)     // Catch:{ all -> 0x00ba }
            if (r7 == 0) goto L_0x009a
            android.util.SparseBooleanArray r7 = r9.mHdmiStateMap     // Catch:{ all -> 0x00ba }
            boolean r7 = r7.valueAt(r3)     // Catch:{ all -> 0x00ba }
            if (r7 == 0) goto L_0x008d
            r7 = r2
            goto L_0x008e
        L_0x008d:
            r7 = r5
        L_0x008e:
            android.os.Handler r8 = r9.mHandler     // Catch:{ all -> 0x00ba }
            android.os.Message r2 = r8.obtainMessage(r5, r7, r2, r6)     // Catch:{ all -> 0x00ba }
            r2.sendToTarget()     // Catch:{ all -> 0x00ba }
            monitor-exit(r0)     // Catch:{ all -> 0x00ba }
            return
        L_0x009a:
            int r3 = r3 + 1
            goto L_0x0055
        L_0x009d:
            android.util.SparseArray<com.android.server.tv.TvInputHardwareManager$Connection> r3 = r9.mConnections     // Catch:{ all -> 0x00ba }
            java.lang.Object r3 = r3.get(r10)     // Catch:{ all -> 0x00ba }
            com.android.server.tv.TvInputHardwareManager$Connection r3 = (com.android.server.tv.TvInputHardwareManager.Connection) r3     // Catch:{ all -> 0x00ba }
            if (r3 == 0) goto L_0x00b8
            android.os.Handler r4 = r9.mHandler     // Catch:{ all -> 0x00ba }
            int r6 = r3.getInputStateLocked()     // Catch:{ all -> 0x00ba }
            java.lang.String r7 = r11.getId()     // Catch:{ all -> 0x00ba }
            android.os.Message r2 = r4.obtainMessage(r5, r6, r2, r7)     // Catch:{ all -> 0x00ba }
            r2.sendToTarget()     // Catch:{ all -> 0x00ba }
        L_0x00b8:
            monitor-exit(r0)     // Catch:{ all -> 0x00ba }
            return
        L_0x00ba:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00ba }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputHardwareManager.addHardwareInput(int, android.media.tv.TvInputInfo):void");
    }

    private static <T> int indexOfEqualValue(SparseArray<T> map, T value) {
        for (int i = 0; i < map.size(); i++) {
            if (map.valueAt(i).equals(value)) {
                return i;
            }
        }
        return -1;
    }

    /* access modifiers changed from: private */
    public static boolean intArrayContains(int[] array, int value) {
        for (int element : array) {
            if (element == value) {
                return true;
            }
        }
        return false;
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public void addHdmiInput(int id, TvInputInfo info) {
        if (info.getType() == 1007) {
            synchronized (this.mLock) {
                if (indexOfEqualValue(this.mHardwareInputIdMap, info.getParentId()) >= 0) {
                    String oldInputId = this.mHdmiInputIdMap.get(id);
                    if (oldInputId != null) {
                        String str = TAG;
                        Slog.w(str, "Trying to override previous registration: old = " + this.mInputMap.get(oldInputId) + ":" + id + ", new = " + info + ":" + id);
                    }
                    this.mHdmiInputIdMap.put(id, info.getId());
                    this.mInputMap.put(info.getId(), info);
                } else {
                    throw new IllegalArgumentException("info (" + info + ") has invalid parentId.");
                }
            }
            return;
        }
        throw new IllegalArgumentException("info (" + info + ") has non-HDMI type.");
    }

    public void removeHardwareInput(String inputId) {
        synchronized (this.mLock) {
            this.mInputMap.remove(inputId);
            int hardwareIndex = indexOfEqualValue(this.mHardwareInputIdMap, inputId);
            if (hardwareIndex >= 0) {
                this.mHardwareInputIdMap.removeAt(hardwareIndex);
            }
            int deviceIndex = indexOfEqualValue(this.mHdmiInputIdMap, inputId);
            if (deviceIndex >= 0) {
                this.mHdmiInputIdMap.removeAt(deviceIndex);
            }
        }
    }

    public ITvInputHardware acquireHardware(int deviceId, ITvInputHardwareCallback callback, TvInputInfo info, int callingUid, int resolvedUserId) {
        if (callback != null) {
            synchronized (this.mLock) {
                Connection connection = this.mConnections.get(deviceId);
                if (connection == null) {
                    String str = TAG;
                    Slog.e(str, "Invalid deviceId : " + deviceId);
                    return null;
                }
                if (checkUidChangedLocked(connection, callingUid, resolvedUserId)) {
                    TvInputHardwareImpl hardware = new TvInputHardwareImpl(connection.getHardwareInfoLocked());
                    try {
                        callback.asBinder().linkToDeath(connection, 0);
                        connection.resetLocked(hardware, callback, info, Integer.valueOf(callingUid), Integer.valueOf(resolvedUserId));
                    } catch (RemoteException e) {
                        hardware.release();
                        return null;
                    }
                }
                ITvInputHardware hardwareLocked = connection.getHardwareLocked();
                return hardwareLocked;
            }
        }
        throw new NullPointerException();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x003e, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void releaseHardware(int r9, android.media.tv.ITvInputHardware r10, int r11, int r12) {
        /*
            r8 = this;
            java.lang.Object r0 = r8.mLock
            monitor-enter(r0)
            android.util.SparseArray<com.android.server.tv.TvInputHardwareManager$Connection> r1 = r8.mConnections     // Catch:{ all -> 0x003f }
            java.lang.Object r1 = r1.get(r9)     // Catch:{ all -> 0x003f }
            com.android.server.tv.TvInputHardwareManager$Connection r1 = (com.android.server.tv.TvInputHardwareManager.Connection) r1     // Catch:{ all -> 0x003f }
            if (r1 != 0) goto L_0x0025
            java.lang.String r2 = TAG     // Catch:{ all -> 0x003f }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x003f }
            r3.<init>()     // Catch:{ all -> 0x003f }
            java.lang.String r4 = "Invalid deviceId : "
            r3.append(r4)     // Catch:{ all -> 0x003f }
            r3.append(r9)     // Catch:{ all -> 0x003f }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x003f }
            android.util.Slog.e(r2, r3)     // Catch:{ all -> 0x003f }
            monitor-exit(r0)     // Catch:{ all -> 0x003f }
            return
        L_0x0025:
            android.media.tv.ITvInputHardware r2 = r1.getHardwareLocked()     // Catch:{ all -> 0x003f }
            if (r2 != r10) goto L_0x003d
            boolean r2 = r8.checkUidChangedLocked(r1, r11, r12)     // Catch:{ all -> 0x003f }
            if (r2 == 0) goto L_0x0032
            goto L_0x003d
        L_0x0032:
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r2 = r1
            r2.resetLocked(r3, r4, r5, r6, r7)     // Catch:{ all -> 0x003f }
            monitor-exit(r0)     // Catch:{ all -> 0x003f }
            return
        L_0x003d:
            monitor-exit(r0)     // Catch:{ all -> 0x003f }
            return
        L_0x003f:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003f }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputHardwareManager.releaseHardware(int, android.media.tv.ITvInputHardware, int, int):void");
    }

    /* access modifiers changed from: private */
    public TvInputHardwareInfo findHardwareInfoForHdmiPortLocked(int port) {
        for (TvInputHardwareInfo hardwareInfo : this.mHardwareList) {
            if (hardwareInfo.getType() == 9 && hardwareInfo.getHdmiPortId() == port) {
                return hardwareInfo;
            }
        }
        return null;
    }

    private int findDeviceIdForInputIdLocked(String inputId) {
        for (int i = 0; i < this.mConnections.size(); i++) {
            if (this.mConnections.get(i).getInfoLocked().getId().equals(inputId)) {
                return i;
            }
        }
        return -1;
    }

    public List<TvStreamConfig> getAvailableTvStreamConfigList(String inputId, int callingUid, int resolvedUserId) {
        List<TvStreamConfig> configsList = new ArrayList<>();
        synchronized (this.mLock) {
            int deviceId = findDeviceIdForInputIdLocked(inputId);
            if (deviceId < 0) {
                Slog.e(TAG, "Invalid inputId : " + inputId);
                return configsList;
            }
            for (TvStreamConfig config : this.mConnections.get(deviceId).getConfigsLocked()) {
                if (config.getType() == 2) {
                    configsList.add(config);
                }
            }
            return configsList;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x004c, code lost:
        return r5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean captureFrame(java.lang.String r8, android.view.Surface r9, final android.media.tv.TvStreamConfig r10, int r11, int r12) {
        /*
            r7 = this;
            java.lang.Object r0 = r7.mLock
            monitor-enter(r0)
            int r1 = r7.findDeviceIdForInputIdLocked(r8)     // Catch:{ all -> 0x004f }
            r2 = 0
            if (r1 >= 0) goto L_0x0022
            java.lang.String r3 = TAG     // Catch:{ all -> 0x004f }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x004f }
            r4.<init>()     // Catch:{ all -> 0x004f }
            java.lang.String r5 = "Invalid inputId : "
            r4.append(r5)     // Catch:{ all -> 0x004f }
            r4.append(r8)     // Catch:{ all -> 0x004f }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x004f }
            android.util.Slog.e(r3, r4)     // Catch:{ all -> 0x004f }
            monitor-exit(r0)     // Catch:{ all -> 0x004f }
            return r2
        L_0x0022:
            android.util.SparseArray<com.android.server.tv.TvInputHardwareManager$Connection> r3 = r7.mConnections     // Catch:{ all -> 0x004f }
            java.lang.Object r3 = r3.get(r1)     // Catch:{ all -> 0x004f }
            com.android.server.tv.TvInputHardwareManager$Connection r3 = (com.android.server.tv.TvInputHardwareManager.Connection) r3     // Catch:{ all -> 0x004f }
            com.android.server.tv.TvInputHardwareManager$TvInputHardwareImpl r4 = r3.getHardwareImplLocked()     // Catch:{ all -> 0x004f }
            if (r4 == 0) goto L_0x004d
            java.lang.Runnable r2 = r3.getOnFirstFrameCapturedLocked()     // Catch:{ all -> 0x004f }
            if (r2 == 0) goto L_0x003d
            r2.run()     // Catch:{ all -> 0x004f }
            r5 = 0
            r3.setOnFirstFrameCapturedLocked(r5)     // Catch:{ all -> 0x004f }
        L_0x003d:
            boolean r5 = r4.startCapture(r9, r10)     // Catch:{ all -> 0x004f }
            if (r5 == 0) goto L_0x004b
            com.android.server.tv.TvInputHardwareManager$2 r6 = new com.android.server.tv.TvInputHardwareManager$2     // Catch:{ all -> 0x004f }
            r6.<init>(r4, r10)     // Catch:{ all -> 0x004f }
            r3.setOnFirstFrameCapturedLocked(r6)     // Catch:{ all -> 0x004f }
        L_0x004b:
            monitor-exit(r0)     // Catch:{ all -> 0x004f }
            return r5
        L_0x004d:
            monitor-exit(r0)     // Catch:{ all -> 0x004f }
            return r2
        L_0x004f:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x004f }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputHardwareManager.captureFrame(java.lang.String, android.view.Surface, android.media.tv.TvStreamConfig, int, int):boolean");
    }

    private void processPendingHdmiDeviceEventsLocked() {
        Iterator<Message> it = this.mPendingHdmiDeviceEvents.iterator();
        while (it.hasNext()) {
            Message msg = it.next();
            if (findHardwareInfoForHdmiPortLocked(((HdmiDeviceInfo) msg.obj).getPortId()) != null) {
                msg.sendToTarget();
                it.remove();
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateVolume() {
        this.mCurrentMaxIndex = this.mAudioManager.getStreamMaxVolume(3);
        this.mCurrentIndex = this.mAudioManager.getStreamVolume(3);
    }

    /* access modifiers changed from: private */
    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:657)
        	at java.util.ArrayList.get(ArrayList.java:433)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:693)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x002e  */
    /* JADX WARNING: Removed duplicated region for block: B:19:0x0050  */
    public void handleVolumeChange(android.content.Context r7, android.content.Intent r8) {
        /*
            r6 = this;
            java.lang.String r0 = r8.getAction()
            int r1 = r0.hashCode()
            r2 = -1940635523(0xffffffff8c54407d, float:-1.6351292E-31)
            r3 = 0
            r4 = 1
            r5 = -1
            if (r1 == r2) goto L_0x0020
            r2 = 1920758225(0x727c71d1, float:5.0001804E30)
            if (r1 == r2) goto L_0x0016
        L_0x0015:
            goto L_0x002a
        L_0x0016:
            java.lang.String r1 = "android.media.STREAM_MUTE_CHANGED_ACTION"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0015
            r1 = r4
            goto L_0x002b
        L_0x0020:
            java.lang.String r1 = "android.media.VOLUME_CHANGED_ACTION"
            boolean r1 = r0.equals(r1)
            if (r1 == 0) goto L_0x0015
            r1 = r3
            goto L_0x002b
        L_0x002a:
            r1 = r5
        L_0x002b:
            r2 = 3
            if (r1 == 0) goto L_0x0050
            if (r1 == r4) goto L_0x0047
            java.lang.String r1 = TAG
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Unrecognized intent: "
            r2.append(r3)
            r2.append(r8)
            java.lang.String r2 = r2.toString()
            android.util.Slog.w(r1, r2)
            return
        L_0x0047:
            java.lang.String r1 = "android.media.EXTRA_VOLUME_STREAM_TYPE"
            int r1 = r8.getIntExtra(r1, r5)
            if (r1 == r2) goto L_0x0067
            return
        L_0x0050:
            java.lang.String r1 = "android.media.EXTRA_VOLUME_STREAM_TYPE"
            int r1 = r8.getIntExtra(r1, r5)
            if (r1 == r2) goto L_0x0059
            return
        L_0x0059:
            java.lang.String r2 = "android.media.EXTRA_VOLUME_STREAM_VALUE"
            int r2 = r8.getIntExtra(r2, r3)
            int r4 = r6.mCurrentIndex
            if (r2 != r4) goto L_0x0064
            return
        L_0x0064:
            r6.mCurrentIndex = r2
        L_0x0067:
            java.lang.Object r1 = r6.mLock
            monitor-enter(r1)
            r2 = r3
        L_0x006b:
            android.util.SparseArray<com.android.server.tv.TvInputHardwareManager$Connection> r3 = r6.mConnections     // Catch:{ all -> 0x0089 }
            int r3 = r3.size()     // Catch:{ all -> 0x0089 }
            if (r2 >= r3) goto L_0x0087
            android.util.SparseArray<com.android.server.tv.TvInputHardwareManager$Connection> r3 = r6.mConnections     // Catch:{ all -> 0x0089 }
            java.lang.Object r3 = r3.valueAt(r2)     // Catch:{ all -> 0x0089 }
            com.android.server.tv.TvInputHardwareManager$Connection r3 = (com.android.server.tv.TvInputHardwareManager.Connection) r3     // Catch:{ all -> 0x0089 }
            com.android.server.tv.TvInputHardwareManager$TvInputHardwareImpl r3 = r3.getHardwareImplLocked()     // Catch:{ all -> 0x0089 }
            if (r3 == 0) goto L_0x0084
            r3.onMediaStreamVolumeChanged()     // Catch:{ all -> 0x0089 }
        L_0x0084:
            int r2 = r2 + 1
            goto L_0x006b
        L_0x0087:
            monitor-exit(r1)     // Catch:{ all -> 0x0089 }
            return
        L_0x0089:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0089 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputHardwareManager.handleVolumeChange(android.content.Context, android.content.Intent):void");
    }

    /* access modifiers changed from: private */
    public float getMediaStreamVolume() {
        return ((float) this.mCurrentIndex) / ((float) this.mCurrentMaxIndex);
    }

    public void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        IndentingPrintWriter pw = new IndentingPrintWriter(writer, "  ");
        if (DumpUtils.checkDumpPermission(this.mContext, TAG, pw)) {
            synchronized (this.mLock) {
                pw.println("TvInputHardwareManager Info:");
                pw.increaseIndent();
                pw.println("mConnections: deviceId -> Connection");
                pw.increaseIndent();
                for (int i = 0; i < this.mConnections.size(); i++) {
                    pw.println(this.mConnections.keyAt(i) + ": " + this.mConnections.valueAt(i));
                }
                pw.decreaseIndent();
                pw.println("mHardwareList:");
                pw.increaseIndent();
                for (TvInputHardwareInfo tvInputHardwareInfo : this.mHardwareList) {
                    pw.println(tvInputHardwareInfo);
                }
                pw.decreaseIndent();
                pw.println("mHdmiDeviceList:");
                pw.increaseIndent();
                for (HdmiDeviceInfo hdmiDeviceInfo : this.mHdmiDeviceList) {
                    pw.println(hdmiDeviceInfo);
                }
                pw.decreaseIndent();
                pw.println("mHardwareInputIdMap: deviceId -> inputId");
                pw.increaseIndent();
                for (int i2 = 0; i2 < this.mHardwareInputIdMap.size(); i2++) {
                    pw.println(this.mHardwareInputIdMap.keyAt(i2) + ": " + this.mHardwareInputIdMap.valueAt(i2));
                }
                pw.decreaseIndent();
                pw.println("mHdmiInputIdMap: id -> inputId");
                pw.increaseIndent();
                for (int i3 = 0; i3 < this.mHdmiInputIdMap.size(); i3++) {
                    pw.println(this.mHdmiInputIdMap.keyAt(i3) + ": " + this.mHdmiInputIdMap.valueAt(i3));
                }
                pw.decreaseIndent();
                pw.println("mInputMap: inputId -> inputInfo");
                pw.increaseIndent();
                for (Map.Entry<String, TvInputInfo> entry : this.mInputMap.entrySet()) {
                    pw.println(entry.getKey() + ": " + entry.getValue());
                }
                pw.decreaseIndent();
                pw.decreaseIndent();
            }
        }
    }

    private class Connection implements IBinder.DeathRecipient {
        private ITvInputHardwareCallback mCallback;
        private Integer mCallingUid = null;
        private TvStreamConfig[] mConfigs = null;
        private TvInputHardwareImpl mHardware = null;
        private final TvInputHardwareInfo mHardwareInfo;
        private TvInputInfo mInfo;
        private Runnable mOnFirstFrameCaptured;
        private Integer mResolvedUserId = null;

        public Connection(TvInputHardwareInfo hardwareInfo) {
            this.mHardwareInfo = hardwareInfo;
        }

        public void resetLocked(TvInputHardwareImpl hardware, ITvInputHardwareCallback callback, TvInputInfo info, Integer callingUid, Integer resolvedUserId) {
            ITvInputHardwareCallback iTvInputHardwareCallback;
            if (this.mHardware != null) {
                try {
                    this.mCallback.onReleased();
                } catch (RemoteException e) {
                    Slog.e(TvInputHardwareManager.TAG, "error in Connection::resetLocked", e);
                }
                this.mHardware.release();
            }
            this.mHardware = hardware;
            this.mCallback = callback;
            this.mInfo = info;
            this.mCallingUid = callingUid;
            this.mResolvedUserId = resolvedUserId;
            this.mOnFirstFrameCaptured = null;
            if (this.mHardware != null && (iTvInputHardwareCallback = this.mCallback) != null) {
                try {
                    iTvInputHardwareCallback.onStreamConfigChanged(getConfigsLocked());
                } catch (RemoteException e2) {
                    Slog.e(TvInputHardwareManager.TAG, "error in Connection::resetLocked", e2);
                }
            }
        }

        public void updateConfigsLocked(TvStreamConfig[] configs) {
            this.mConfigs = configs;
        }

        public TvInputHardwareInfo getHardwareInfoLocked() {
            return this.mHardwareInfo;
        }

        public TvInputInfo getInfoLocked() {
            return this.mInfo;
        }

        public ITvInputHardware getHardwareLocked() {
            return this.mHardware;
        }

        public TvInputHardwareImpl getHardwareImplLocked() {
            return this.mHardware;
        }

        public ITvInputHardwareCallback getCallbackLocked() {
            return this.mCallback;
        }

        public TvStreamConfig[] getConfigsLocked() {
            return this.mConfigs;
        }

        public Integer getCallingUidLocked() {
            return this.mCallingUid;
        }

        public Integer getResolvedUserIdLocked() {
            return this.mResolvedUserId;
        }

        public void setOnFirstFrameCapturedLocked(Runnable runnable) {
            this.mOnFirstFrameCaptured = runnable;
        }

        public Runnable getOnFirstFrameCapturedLocked() {
            return this.mOnFirstFrameCaptured;
        }

        public void binderDied() {
            synchronized (TvInputHardwareManager.this.mLock) {
                resetLocked((TvInputHardwareImpl) null, (ITvInputHardwareCallback) null, (TvInputInfo) null, (Integer) null, (Integer) null);
            }
        }

        public String toString() {
            return "Connection{ mHardwareInfo: " + this.mHardwareInfo + ", mInfo: " + this.mInfo + ", mCallback: " + this.mCallback + ", mConfigs: " + Arrays.toString(this.mConfigs) + ", mCallingUid: " + this.mCallingUid + ", mResolvedUserId: " + this.mResolvedUserId + " }";
        }

        /* access modifiers changed from: private */
        public int getConfigsLengthLocked() {
            TvStreamConfig[] tvStreamConfigArr = this.mConfigs;
            if (tvStreamConfigArr == null) {
                return 0;
            }
            return tvStreamConfigArr.length;
        }

        /* access modifiers changed from: private */
        public int getInputStateLocked() {
            int cableConnectionStatus;
            if (getConfigsLengthLocked() <= 0 && (cableConnectionStatus = this.mHardwareInfo.getCableConnectionStatus()) != 1) {
                return cableConnectionStatus != 2 ? 1 : 2;
            }
            return 0;
        }
    }

    private class TvInputHardwareImpl extends ITvInputHardware.Stub {
        private TvStreamConfig mActiveConfig = null;
        private final AudioManager.OnAudioPortUpdateListener mAudioListener = new AudioManager.OnAudioPortUpdateListener() {
            public void onAudioPortListUpdate(AudioPort[] portList) {
                synchronized (TvInputHardwareImpl.this.mImplLock) {
                    TvInputHardwareImpl.this.updateAudioConfigLocked();
                }
            }

            public void onAudioPatchListUpdate(AudioPatch[] patchList) {
            }

            public void onServiceDied() {
                synchronized (TvInputHardwareImpl.this.mImplLock) {
                    AudioDevicePort unused = TvInputHardwareImpl.this.mAudioSource = null;
                    TvInputHardwareImpl.this.mAudioSink.clear();
                    if (TvInputHardwareImpl.this.mAudioPatch != null) {
                        AudioManager unused2 = TvInputHardwareManager.this.mAudioManager;
                        AudioManager.releaseAudioPatch(TvInputHardwareImpl.this.mAudioPatch);
                        AudioPatch unused3 = TvInputHardwareImpl.this.mAudioPatch = null;
                    }
                }
            }
        };
        /* access modifiers changed from: private */
        public AudioPatch mAudioPatch = null;
        /* access modifiers changed from: private */
        public List<AudioDevicePort> mAudioSink = new ArrayList();
        /* access modifiers changed from: private */
        public AudioDevicePort mAudioSource;
        private float mCommittedVolume = -1.0f;
        private int mDesiredChannelMask = 1;
        private int mDesiredFormat = 1;
        private int mDesiredSamplingRate = 0;
        /* access modifiers changed from: private */
        public final Object mImplLock = new Object();
        private final TvInputHardwareInfo mInfo;
        private String mOverrideAudioAddress = "";
        private int mOverrideAudioType = 0;
        private boolean mReleased = false;
        private float mSourceVolume = 0.0f;

        public TvInputHardwareImpl(TvInputHardwareInfo info) {
            this.mInfo = info;
            TvInputHardwareManager.this.mAudioManager.registerAudioPortUpdateListener(this.mAudioListener);
            if (this.mInfo.getAudioType() != 0) {
                this.mAudioSource = findAudioDevicePort(this.mInfo.getAudioType(), this.mInfo.getAudioAddress());
                findAudioSinkFromAudioPolicy(this.mAudioSink);
            }
        }

        private void findAudioSinkFromAudioPolicy(List<AudioDevicePort> sinks) {
            sinks.clear();
            ArrayList<AudioDevicePort> devicePorts = new ArrayList<>();
            AudioManager unused = TvInputHardwareManager.this.mAudioManager;
            if (AudioManager.listAudioDevicePorts(devicePorts) == 0) {
                int sinkDevice = TvInputHardwareManager.this.mAudioManager.getDevicesForStream(3);
                Iterator<AudioDevicePort> it = devicePorts.iterator();
                while (it.hasNext()) {
                    AudioDevicePort port = it.next();
                    if ((port.type() & sinkDevice) != 0 && (port.type() & Integer.MIN_VALUE) == 0) {
                        sinks.add(port);
                    }
                }
            }
        }

        private AudioDevicePort findAudioDevicePort(int type, String address) {
            if (type == 0) {
                return null;
            }
            ArrayList<AudioDevicePort> devicePorts = new ArrayList<>();
            AudioManager unused = TvInputHardwareManager.this.mAudioManager;
            if (AudioManager.listAudioDevicePorts(devicePorts) != 0) {
                return null;
            }
            Iterator<AudioDevicePort> it = devicePorts.iterator();
            while (it.hasNext()) {
                AudioDevicePort port = it.next();
                if (port.type() == type && port.address().equals(address)) {
                    return port;
                }
            }
            return null;
        }

        public void release() {
            synchronized (this.mImplLock) {
                TvInputHardwareManager.this.mAudioManager.unregisterAudioPortUpdateListener(this.mAudioListener);
                if (this.mAudioPatch != null) {
                    AudioManager unused = TvInputHardwareManager.this.mAudioManager;
                    AudioManager.releaseAudioPatch(this.mAudioPatch);
                    this.mAudioPatch = null;
                }
                this.mReleased = true;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 8 */
        /* JADX WARNING: Code restructure failed: missing block: B:31:0x006f, code lost:
            return r3;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean setSurface(android.view.Surface r9, android.media.tv.TvStreamConfig r10) throws android.os.RemoteException {
            /*
                r8 = this;
                java.lang.Object r0 = r8.mImplLock
                monitor-enter(r0)
                boolean r1 = r8.mReleased     // Catch:{ all -> 0x0078 }
                if (r1 != 0) goto L_0x0070
                r1 = 0
                r2 = 0
                r3 = 1
                r4 = 0
                if (r9 != 0) goto L_0x0029
                android.media.tv.TvStreamConfig r5 = r8.mActiveConfig     // Catch:{ all -> 0x0078 }
                if (r5 == 0) goto L_0x0027
                com.android.server.tv.TvInputHardwareManager r5 = com.android.server.tv.TvInputHardwareManager.this     // Catch:{ all -> 0x0078 }
                com.android.server.tv.TvInputHal r5 = r5.mHal     // Catch:{ all -> 0x0078 }
                android.media.tv.TvInputHardwareInfo r6 = r8.mInfo     // Catch:{ all -> 0x0078 }
                int r6 = r6.getDeviceId()     // Catch:{ all -> 0x0078 }
                android.media.tv.TvStreamConfig r7 = r8.mActiveConfig     // Catch:{ all -> 0x0078 }
                int r5 = r5.removeStream(r6, r7)     // Catch:{ all -> 0x0078 }
                r1 = r5
                r8.mActiveConfig = r2     // Catch:{ all -> 0x0078 }
                goto L_0x0067
            L_0x0027:
                monitor-exit(r0)     // Catch:{ all -> 0x0078 }
                return r3
            L_0x0029:
                if (r10 != 0) goto L_0x002d
                monitor-exit(r0)     // Catch:{ all -> 0x0078 }
                return r4
            L_0x002d:
                android.media.tv.TvStreamConfig r5 = r8.mActiveConfig     // Catch:{ all -> 0x0078 }
                if (r5 == 0) goto L_0x0050
                android.media.tv.TvStreamConfig r5 = r8.mActiveConfig     // Catch:{ all -> 0x0078 }
                boolean r5 = r10.equals(r5)     // Catch:{ all -> 0x0078 }
                if (r5 != 0) goto L_0x0050
                com.android.server.tv.TvInputHardwareManager r5 = com.android.server.tv.TvInputHardwareManager.this     // Catch:{ all -> 0x0078 }
                com.android.server.tv.TvInputHal r5 = r5.mHal     // Catch:{ all -> 0x0078 }
                android.media.tv.TvInputHardwareInfo r6 = r8.mInfo     // Catch:{ all -> 0x0078 }
                int r6 = r6.getDeviceId()     // Catch:{ all -> 0x0078 }
                android.media.tv.TvStreamConfig r7 = r8.mActiveConfig     // Catch:{ all -> 0x0078 }
                int r5 = r5.removeStream(r6, r7)     // Catch:{ all -> 0x0078 }
                r1 = r5
                if (r1 == 0) goto L_0x0050
                r8.mActiveConfig = r2     // Catch:{ all -> 0x0078 }
            L_0x0050:
                if (r1 != 0) goto L_0x0067
                com.android.server.tv.TvInputHardwareManager r2 = com.android.server.tv.TvInputHardwareManager.this     // Catch:{ all -> 0x0078 }
                com.android.server.tv.TvInputHal r2 = r2.mHal     // Catch:{ all -> 0x0078 }
                android.media.tv.TvInputHardwareInfo r5 = r8.mInfo     // Catch:{ all -> 0x0078 }
                int r5 = r5.getDeviceId()     // Catch:{ all -> 0x0078 }
                int r2 = r2.addOrUpdateStream(r5, r9, r10)     // Catch:{ all -> 0x0078 }
                r1 = r2
                if (r1 != 0) goto L_0x0067
                r8.mActiveConfig = r10     // Catch:{ all -> 0x0078 }
            L_0x0067:
                r8.updateAudioConfigLocked()     // Catch:{ all -> 0x0078 }
                if (r1 != 0) goto L_0x006d
                goto L_0x006e
            L_0x006d:
                r3 = r4
            L_0x006e:
                monitor-exit(r0)     // Catch:{ all -> 0x0078 }
                return r3
            L_0x0070:
                java.lang.IllegalStateException r1 = new java.lang.IllegalStateException     // Catch:{ all -> 0x0078 }
                java.lang.String r2 = "Device already released."
                r1.<init>(r2)     // Catch:{ all -> 0x0078 }
                throw r1     // Catch:{ all -> 0x0078 }
            L_0x0078:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0078 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputHardwareManager.TvInputHardwareImpl.setSurface(android.view.Surface, android.media.tv.TvStreamConfig):boolean");
        }

        /* access modifiers changed from: private */
        public void updateAudioConfigLocked() {
            int gainValue;
            boolean sinkUpdated = updateAudioSinkLocked();
            boolean sourceUpdated = updateAudioSourceLocked();
            if (this.mAudioSource == null || this.mAudioSink.isEmpty()) {
                boolean z = sourceUpdated;
            } else if (this.mActiveConfig == null) {
                boolean z2 = sinkUpdated;
                boolean z3 = sourceUpdated;
            } else {
                TvInputHardwareManager.this.updateVolume();
                float volume = this.mSourceVolume * TvInputHardwareManager.this.getMediaStreamVolume();
                AudioGainConfig sourceGainConfig = null;
                int i = 1;
                if (this.mAudioSource.gains().length > 0 && volume != this.mCommittedVolume) {
                    AudioGain sourceGain = null;
                    AudioGain[] gains = this.mAudioSource.gains();
                    int length = gains.length;
                    int i2 = 0;
                    while (true) {
                        if (i2 >= length) {
                            break;
                        }
                        AudioGain gain = gains[i2];
                        if ((gain.mode() & 1) != 0) {
                            sourceGain = gain;
                            break;
                        }
                        i2++;
                    }
                    if (sourceGain != null) {
                        int steps = (sourceGain.maxValue() - sourceGain.minValue()) / sourceGain.stepValue();
                        int gainValue2 = sourceGain.minValue();
                        if (volume < 1.0f) {
                            gainValue = gainValue2 + (sourceGain.stepValue() * ((int) (((double) (((float) steps) * volume)) + 0.5d)));
                        } else {
                            gainValue = sourceGain.maxValue();
                        }
                        sourceGainConfig = sourceGain.buildConfig(1, sourceGain.channelMask(), new int[]{gainValue}, 0);
                    } else {
                        Slog.w(TvInputHardwareManager.TAG, "No audio source gain with MODE_JOINT support exists.");
                    }
                }
                AudioPortConfig sourceConfig = this.mAudioSource.activeConfig();
                List<AudioPortConfig> sinkConfigs = new ArrayList<>();
                AudioPatch[] audioPatchArray = {this.mAudioPatch};
                boolean shouldRecreateAudioPatch = sourceUpdated || sinkUpdated;
                for (AudioDevicePort audioSink : this.mAudioSink) {
                    AudioPortConfig sinkConfig = audioSink.activeConfig();
                    int sinkSamplingRate = this.mDesiredSamplingRate;
                    int sinkChannelMask = this.mDesiredChannelMask;
                    int sinkFormat = this.mDesiredFormat;
                    if (sinkConfig != null) {
                        if (sinkSamplingRate == 0) {
                            sinkSamplingRate = sinkConfig.samplingRate();
                        }
                        if (sinkChannelMask == i) {
                            sinkChannelMask = sinkConfig.channelMask();
                        }
                        if (sinkFormat == i) {
                            sinkFormat = sinkConfig.format();
                        }
                    }
                    if (sinkConfig == null || sinkConfig.samplingRate() != sinkSamplingRate || sinkConfig.channelMask() != sinkChannelMask || sinkConfig.format() != sinkFormat) {
                        if (!TvInputHardwareManager.intArrayContains(audioSink.samplingRates(), sinkSamplingRate) && audioSink.samplingRates().length > 0) {
                            sinkSamplingRate = audioSink.samplingRates()[0];
                        }
                        if (!TvInputHardwareManager.intArrayContains(audioSink.channelMasks(), sinkChannelMask)) {
                            sinkChannelMask = 1;
                        }
                        if (!TvInputHardwareManager.intArrayContains(audioSink.formats(), sinkFormat)) {
                            sinkFormat = 1;
                        }
                        sinkConfig = audioSink.buildConfig(sinkSamplingRate, sinkChannelMask, sinkFormat, (AudioGainConfig) null);
                        shouldRecreateAudioPatch = true;
                    }
                    sinkConfigs.add(sinkConfig);
                    i = 1;
                }
                AudioPortConfig sinkConfig2 = sinkConfigs.get(0);
                if (sourceConfig == null || sourceGainConfig != null) {
                    int sourceSamplingRate = 0;
                    if (TvInputHardwareManager.intArrayContains(this.mAudioSource.samplingRates(), sinkConfig2.samplingRate())) {
                        sourceSamplingRate = sinkConfig2.samplingRate();
                    } else if (this.mAudioSource.samplingRates().length > 0) {
                        sourceSamplingRate = this.mAudioSource.samplingRates()[0];
                    }
                    int sourceChannelMask = 1;
                    int[] channelMasks = this.mAudioSource.channelMasks();
                    int length2 = channelMasks.length;
                    int i3 = 0;
                    while (true) {
                        if (i3 >= length2) {
                            boolean z4 = sourceUpdated;
                            break;
                        }
                        int inChannelMask = channelMasks[i3];
                        boolean sinkUpdated2 = sinkUpdated;
                        boolean sourceUpdated2 = sourceUpdated;
                        if (AudioFormat.channelCountFromOutChannelMask(sinkConfig2.channelMask()) == AudioFormat.channelCountFromInChannelMask(inChannelMask)) {
                            sourceChannelMask = inChannelMask;
                            break;
                        }
                        i3++;
                        sinkUpdated = sinkUpdated2;
                        sourceUpdated = sourceUpdated2;
                    }
                    int sourceFormat = 1;
                    if (TvInputHardwareManager.intArrayContains(this.mAudioSource.formats(), sinkConfig2.format())) {
                        sourceFormat = sinkConfig2.format();
                    }
                    sourceConfig = this.mAudioSource.buildConfig(sourceSamplingRate, sourceChannelMask, sourceFormat, sourceGainConfig);
                    shouldRecreateAudioPatch = true;
                } else {
                    boolean z5 = sinkUpdated;
                    boolean z6 = sourceUpdated;
                }
                if (shouldRecreateAudioPatch) {
                    this.mCommittedVolume = volume;
                    if (this.mAudioPatch != null) {
                        AudioManager unused = TvInputHardwareManager.this.mAudioManager;
                        AudioManager.releaseAudioPatch(this.mAudioPatch);
                    }
                    AudioManager unused2 = TvInputHardwareManager.this.mAudioManager;
                    AudioManager.createAudioPatch(audioPatchArray, new AudioPortConfig[]{sourceConfig}, (AudioPortConfig[]) sinkConfigs.toArray(new AudioPortConfig[sinkConfigs.size()]));
                    this.mAudioPatch = audioPatchArray[0];
                    if (sourceGainConfig != null) {
                        AudioManager unused3 = TvInputHardwareManager.this.mAudioManager;
                        AudioManager.setAudioPortGain(this.mAudioSource, sourceGainConfig);
                        return;
                    }
                    return;
                }
                return;
            }
            if (this.mAudioPatch != null) {
                AudioManager unused4 = TvInputHardwareManager.this.mAudioManager;
                AudioManager.releaseAudioPatch(this.mAudioPatch);
                this.mAudioPatch = null;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        public void setStreamVolume(float volume) throws RemoteException {
            synchronized (this.mImplLock) {
                if (!this.mReleased) {
                    this.mSourceVolume = volume;
                    updateAudioConfigLocked();
                } else {
                    throw new IllegalStateException("Device already released.");
                }
            }
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Code restructure failed: missing block: B:18:0x002c, code lost:
            return r2;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x002e, code lost:
            return false;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean startCapture(android.view.Surface r5, android.media.tv.TvStreamConfig r6) {
            /*
                r4 = this;
                java.lang.Object r0 = r4.mImplLock
                monitor-enter(r0)
                boolean r1 = r4.mReleased     // Catch:{ all -> 0x002f }
                r2 = 0
                if (r1 == 0) goto L_0x000a
                monitor-exit(r0)     // Catch:{ all -> 0x002f }
                return r2
            L_0x000a:
                if (r5 == 0) goto L_0x002d
                if (r6 != 0) goto L_0x000f
                goto L_0x002d
            L_0x000f:
                int r1 = r6.getType()     // Catch:{ all -> 0x002f }
                r3 = 2
                if (r1 == r3) goto L_0x0018
                monitor-exit(r0)     // Catch:{ all -> 0x002f }
                return r2
            L_0x0018:
                com.android.server.tv.TvInputHardwareManager r1 = com.android.server.tv.TvInputHardwareManager.this     // Catch:{ all -> 0x002f }
                com.android.server.tv.TvInputHal r1 = r1.mHal     // Catch:{ all -> 0x002f }
                android.media.tv.TvInputHardwareInfo r3 = r4.mInfo     // Catch:{ all -> 0x002f }
                int r3 = r3.getDeviceId()     // Catch:{ all -> 0x002f }
                int r1 = r1.addOrUpdateStream(r3, r5, r6)     // Catch:{ all -> 0x002f }
                if (r1 != 0) goto L_0x002b
                r2 = 1
            L_0x002b:
                monitor-exit(r0)     // Catch:{ all -> 0x002f }
                return r2
            L_0x002d:
                monitor-exit(r0)     // Catch:{ all -> 0x002f }
                return r2
            L_0x002f:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x002f }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputHardwareManager.TvInputHardwareImpl.startCapture(android.view.Surface, android.media.tv.TvStreamConfig):boolean");
        }

        /* access modifiers changed from: private */
        /* JADX WARNING: Code restructure failed: missing block: B:14:0x0022, code lost:
            return r2;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public boolean stopCapture(android.media.tv.TvStreamConfig r5) {
            /*
                r4 = this;
                java.lang.Object r0 = r4.mImplLock
                monitor-enter(r0)
                boolean r1 = r4.mReleased     // Catch:{ all -> 0x0023 }
                r2 = 0
                if (r1 == 0) goto L_0x000a
                monitor-exit(r0)     // Catch:{ all -> 0x0023 }
                return r2
            L_0x000a:
                if (r5 != 0) goto L_0x000e
                monitor-exit(r0)     // Catch:{ all -> 0x0023 }
                return r2
            L_0x000e:
                com.android.server.tv.TvInputHardwareManager r1 = com.android.server.tv.TvInputHardwareManager.this     // Catch:{ all -> 0x0023 }
                com.android.server.tv.TvInputHal r1 = r1.mHal     // Catch:{ all -> 0x0023 }
                android.media.tv.TvInputHardwareInfo r3 = r4.mInfo     // Catch:{ all -> 0x0023 }
                int r3 = r3.getDeviceId()     // Catch:{ all -> 0x0023 }
                int r1 = r1.removeStream(r3, r5)     // Catch:{ all -> 0x0023 }
                if (r1 != 0) goto L_0x0021
                r2 = 1
            L_0x0021:
                monitor-exit(r0)     // Catch:{ all -> 0x0023 }
                return r2
            L_0x0023:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0023 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputHardwareManager.TvInputHardwareImpl.stopCapture(android.media.tv.TvStreamConfig):boolean");
        }

        private boolean updateAudioSourceLocked() {
            if (this.mInfo.getAudioType() == 0) {
                return false;
            }
            AudioDevicePort previousSource = this.mAudioSource;
            this.mAudioSource = findAudioDevicePort(this.mInfo.getAudioType(), this.mInfo.getAudioAddress());
            AudioDevicePort audioDevicePort = this.mAudioSource;
            if (audioDevicePort == null) {
                if (previousSource != null) {
                    return true;
                }
                return false;
            } else if (!audioDevicePort.equals(previousSource)) {
                return true;
            } else {
                return false;
            }
        }

        private boolean updateAudioSinkLocked() {
            if (this.mInfo.getAudioType() == 0) {
                return false;
            }
            List<AudioDevicePort> previousSink = this.mAudioSink;
            this.mAudioSink = new ArrayList();
            int i = this.mOverrideAudioType;
            if (i == 0) {
                findAudioSinkFromAudioPolicy(this.mAudioSink);
            } else {
                AudioDevicePort audioSink = findAudioDevicePort(i, this.mOverrideAudioAddress);
                if (audioSink != null) {
                    this.mAudioSink.add(audioSink);
                }
            }
            if (this.mAudioSink.size() != previousSink.size()) {
                return true;
            }
            previousSink.removeAll(this.mAudioSink);
            return !previousSink.isEmpty();
        }

        /* access modifiers changed from: private */
        public void handleAudioSinkUpdated() {
            synchronized (this.mImplLock) {
                updateAudioConfigLocked();
            }
        }

        public void overrideAudioSink(int audioType, String audioAddress, int samplingRate, int channelMask, int format) {
            synchronized (this.mImplLock) {
                this.mOverrideAudioType = audioType;
                this.mOverrideAudioAddress = audioAddress;
                this.mDesiredSamplingRate = samplingRate;
                this.mDesiredChannelMask = channelMask;
                this.mDesiredFormat = format;
                updateAudioConfigLocked();
            }
        }

        public void onMediaStreamVolumeChanged() {
            synchronized (this.mImplLock) {
                updateAudioConfigLocked();
            }
        }
    }

    private class ListenerHandler extends Handler {
        private static final int HARDWARE_DEVICE_ADDED = 2;
        private static final int HARDWARE_DEVICE_REMOVED = 3;
        private static final int HDMI_DEVICE_ADDED = 4;
        private static final int HDMI_DEVICE_REMOVED = 5;
        private static final int HDMI_DEVICE_UPDATED = 6;
        private static final int STATE_CHANGED = 1;

        private ListenerHandler() {
        }

        public final void handleMessage(Message msg) {
            String inputId;
            switch (msg.what) {
                case 1:
                    int state = msg.arg1;
                    TvInputHardwareManager.this.mListener.onStateChanged((String) msg.obj, state);
                    return;
                case 2:
                    TvInputHardwareManager.this.mListener.onHardwareDeviceAdded((TvInputHardwareInfo) msg.obj);
                    return;
                case 3:
                    TvInputHardwareManager.this.mListener.onHardwareDeviceRemoved((TvInputHardwareInfo) msg.obj);
                    return;
                case 4:
                    TvInputHardwareManager.this.mListener.onHdmiDeviceAdded((HdmiDeviceInfo) msg.obj);
                    return;
                case 5:
                    TvInputHardwareManager.this.mListener.onHdmiDeviceRemoved((HdmiDeviceInfo) msg.obj);
                    return;
                case 6:
                    HdmiDeviceInfo info = (HdmiDeviceInfo) msg.obj;
                    synchronized (TvInputHardwareManager.this.mLock) {
                        inputId = (String) TvInputHardwareManager.this.mHdmiInputIdMap.get(info.getId());
                    }
                    if (inputId != null) {
                        TvInputHardwareManager.this.mListener.onHdmiDeviceUpdated(inputId, info);
                        return;
                    } else {
                        Slog.w(TvInputHardwareManager.TAG, "Could not resolve input ID matching the device info; ignoring.");
                        return;
                    }
                default:
                    String access$900 = TvInputHardwareManager.TAG;
                    Slog.w(access$900, "Unhandled message: " + msg);
                    return;
            }
        }
    }

    private final class HdmiHotplugEventListener extends IHdmiHotplugEventListener.Stub {
        private HdmiHotplugEventListener() {
        }

        public void onReceived(HdmiHotplugEvent event) {
            int state;
            synchronized (TvInputHardwareManager.this.mLock) {
                TvInputHardwareManager.this.mHdmiStateMap.put(event.getPort(), event.isConnected());
                TvInputHardwareInfo hardwareInfo = TvInputHardwareManager.this.findHardwareInfoForHdmiPortLocked(event.getPort());
                if (hardwareInfo != null) {
                    String inputId = (String) TvInputHardwareManager.this.mHardwareInputIdMap.get(hardwareInfo.getDeviceId());
                    if (inputId != null) {
                        if (event.isConnected()) {
                            state = 0;
                        } else {
                            state = 1;
                        }
                        TvInputHardwareManager.this.mHandler.obtainMessage(1, state, 0, inputId).sendToTarget();
                    }
                }
            }
        }
    }

    private final class HdmiDeviceEventListener extends IHdmiDeviceEventListener.Stub {
        private HdmiDeviceEventListener() {
        }

        /* JADX WARNING: Code restructure failed: missing block: B:32:0x00cb, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onStatusChanged(android.hardware.hdmi.HdmiDeviceInfo r8, int r9) {
            /*
                r7 = this;
                boolean r0 = r8.isSourceType()
                if (r0 != 0) goto L_0x0007
                return
            L_0x0007:
                com.android.server.tv.TvInputHardwareManager r0 = com.android.server.tv.TvInputHardwareManager.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                r1 = 0
                r2 = 0
                r3 = 1
                if (r9 == r3) goto L_0x0090
                r3 = 2
                if (r9 == r3) goto L_0x005a
                r3 = 3
                if (r9 == r3) goto L_0x001b
                goto L_0x00a6
            L_0x001b:
                int r3 = r8.getId()     // Catch:{ all -> 0x00cc }
                android.hardware.hdmi.HdmiDeviceInfo r3 = r7.findHdmiDeviceInfo(r3)     // Catch:{ all -> 0x00cc }
                com.android.server.tv.TvInputHardwareManager r4 = com.android.server.tv.TvInputHardwareManager.this     // Catch:{ all -> 0x00cc }
                java.util.List r4 = r4.mHdmiDeviceList     // Catch:{ all -> 0x00cc }
                boolean r4 = r4.remove(r3)     // Catch:{ all -> 0x00cc }
                if (r4 != 0) goto L_0x004e
                java.lang.String r4 = com.android.server.tv.TvInputHardwareManager.TAG     // Catch:{ all -> 0x00cc }
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x00cc }
                r5.<init>()     // Catch:{ all -> 0x00cc }
                java.lang.String r6 = "The list doesn't contain "
                r5.append(r6)     // Catch:{ all -> 0x00cc }
                r5.append(r8)     // Catch:{ all -> 0x00cc }
                java.lang.String r6 = "; ignoring."
                r5.append(r6)     // Catch:{ all -> 0x00cc }
                java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x00cc }
                android.util.Slog.w(r4, r5)     // Catch:{ all -> 0x00cc }
                monitor-exit(r0)     // Catch:{ all -> 0x00cc }
                return
            L_0x004e:
                com.android.server.tv.TvInputHardwareManager r4 = com.android.server.tv.TvInputHardwareManager.this     // Catch:{ all -> 0x00cc }
                java.util.List r4 = r4.mHdmiDeviceList     // Catch:{ all -> 0x00cc }
                r4.add(r8)     // Catch:{ all -> 0x00cc }
                r1 = 6
                r2 = r8
                goto L_0x00a6
            L_0x005a:
                int r3 = r8.getId()     // Catch:{ all -> 0x00cc }
                android.hardware.hdmi.HdmiDeviceInfo r3 = r7.findHdmiDeviceInfo(r3)     // Catch:{ all -> 0x00cc }
                com.android.server.tv.TvInputHardwareManager r4 = com.android.server.tv.TvInputHardwareManager.this     // Catch:{ all -> 0x00cc }
                java.util.List r4 = r4.mHdmiDeviceList     // Catch:{ all -> 0x00cc }
                boolean r4 = r4.remove(r3)     // Catch:{ all -> 0x00cc }
                if (r4 != 0) goto L_0x008d
                java.lang.String r4 = com.android.server.tv.TvInputHardwareManager.TAG     // Catch:{ all -> 0x00cc }
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x00cc }
                r5.<init>()     // Catch:{ all -> 0x00cc }
                java.lang.String r6 = "The list doesn't contain "
                r5.append(r6)     // Catch:{ all -> 0x00cc }
                r5.append(r8)     // Catch:{ all -> 0x00cc }
                java.lang.String r6 = "; ignoring."
                r5.append(r6)     // Catch:{ all -> 0x00cc }
                java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x00cc }
                android.util.Slog.w(r4, r5)     // Catch:{ all -> 0x00cc }
                monitor-exit(r0)     // Catch:{ all -> 0x00cc }
                return
            L_0x008d:
                r1 = 5
                r2 = r8
                goto L_0x00a6
            L_0x0090:
                int r3 = r8.getId()     // Catch:{ all -> 0x00cc }
                android.hardware.hdmi.HdmiDeviceInfo r3 = r7.findHdmiDeviceInfo(r3)     // Catch:{ all -> 0x00cc }
                if (r3 != 0) goto L_0x00ce
                com.android.server.tv.TvInputHardwareManager r3 = com.android.server.tv.TvInputHardwareManager.this     // Catch:{ all -> 0x00cc }
                java.util.List r3 = r3.mHdmiDeviceList     // Catch:{ all -> 0x00cc }
                r3.add(r8)     // Catch:{ all -> 0x00cc }
                r1 = 4
                r2 = r8
            L_0x00a6:
                com.android.server.tv.TvInputHardwareManager r3 = com.android.server.tv.TvInputHardwareManager.this     // Catch:{ all -> 0x00cc }
                android.os.Handler r3 = r3.mHandler     // Catch:{ all -> 0x00cc }
                r4 = 0
                android.os.Message r3 = r3.obtainMessage(r1, r4, r4, r2)     // Catch:{ all -> 0x00cc }
                com.android.server.tv.TvInputHardwareManager r4 = com.android.server.tv.TvInputHardwareManager.this     // Catch:{ all -> 0x00cc }
                int r5 = r8.getPortId()     // Catch:{ all -> 0x00cc }
                android.media.tv.TvInputHardwareInfo r4 = r4.findHardwareInfoForHdmiPortLocked(r5)     // Catch:{ all -> 0x00cc }
                if (r4 == 0) goto L_0x00c1
                r3.sendToTarget()     // Catch:{ all -> 0x00cc }
                goto L_0x00ca
            L_0x00c1:
                com.android.server.tv.TvInputHardwareManager r4 = com.android.server.tv.TvInputHardwareManager.this     // Catch:{ all -> 0x00cc }
                java.util.List r4 = r4.mPendingHdmiDeviceEvents     // Catch:{ all -> 0x00cc }
                r4.add(r3)     // Catch:{ all -> 0x00cc }
            L_0x00ca:
                monitor-exit(r0)     // Catch:{ all -> 0x00cc }
                return
            L_0x00cc:
                r1 = move-exception
                goto L_0x00ed
            L_0x00ce:
                java.lang.String r3 = com.android.server.tv.TvInputHardwareManager.TAG     // Catch:{ all -> 0x00cc }
                java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x00cc }
                r4.<init>()     // Catch:{ all -> 0x00cc }
                java.lang.String r5 = "The list already contains "
                r4.append(r5)     // Catch:{ all -> 0x00cc }
                r4.append(r8)     // Catch:{ all -> 0x00cc }
                java.lang.String r5 = "; ignoring."
                r4.append(r5)     // Catch:{ all -> 0x00cc }
                java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x00cc }
                android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x00cc }
                monitor-exit(r0)     // Catch:{ all -> 0x00cc }
                return
            L_0x00ed:
                monitor-exit(r0)     // Catch:{ all -> 0x00cc }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.tv.TvInputHardwareManager.HdmiDeviceEventListener.onStatusChanged(android.hardware.hdmi.HdmiDeviceInfo, int):void");
        }

        private HdmiDeviceInfo findHdmiDeviceInfo(int id) {
            for (HdmiDeviceInfo info : TvInputHardwareManager.this.mHdmiDeviceList) {
                if (info.getId() == id) {
                    return info;
                }
            }
            return null;
        }
    }

    private final class HdmiSystemAudioModeChangeListener extends IHdmiSystemAudioModeChangeListener.Stub {
        private HdmiSystemAudioModeChangeListener() {
        }

        public void onStatusChanged(boolean enabled) throws RemoteException {
            synchronized (TvInputHardwareManager.this.mLock) {
                for (int i = 0; i < TvInputHardwareManager.this.mConnections.size(); i++) {
                    TvInputHardwareImpl impl = ((Connection) TvInputHardwareManager.this.mConnections.valueAt(i)).getHardwareImplLocked();
                    if (impl != null) {
                        impl.handleAudioSinkUpdated();
                    }
                }
            }
        }
    }
}
