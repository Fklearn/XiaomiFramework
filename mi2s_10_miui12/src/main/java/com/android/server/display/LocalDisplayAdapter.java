package com.android.server.display;

import android.app.ActivityThread;
import android.content.Context;
import android.content.res.Resources;
import android.hardware.sidekick.SidekickInternal;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemProperties;
import android.os.Trace;
import android.util.LongSparseArray;
import android.util.Slog;
import android.util.SparseArray;
import android.view.Display;
import android.view.DisplayAddress;
import android.view.DisplayCutout;
import android.view.DisplayEventReceiver;
import android.view.SurfaceControl;
import com.android.server.ScreenOnMonitor;
import com.android.server.display.DisplayAdapter;
import com.android.server.display.DisplayManagerService;
import com.android.server.lights.Light;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

final class LocalDisplayAdapter extends DisplayAdapter {
    private static final boolean DEBUG = false;
    private static final String PROPERTY_EMULATOR_CIRCULAR = "ro.emulator.circular";
    private static final String TAG = "LocalDisplayAdapter";
    private static final String UNIQUE_ID_PREFIX = "local:";
    /* access modifiers changed from: private */
    public final LongSparseArray<LocalDisplayDevice> mDevices = new LongSparseArray<>();
    private PhysicalDisplayEventReceiver mPhysicalDisplayEventReceiver;

    public LocalDisplayAdapter(DisplayManagerService.SyncRoot syncRoot, Context context, Handler handler, DisplayAdapter.Listener listener) {
        super(syncRoot, context, handler, listener, TAG);
    }

    public void registerLocked() {
        super.registerLocked();
        this.mPhysicalDisplayEventReceiver = new PhysicalDisplayEventReceiver(getHandler().getLooper());
        for (long physicalDisplayId : SurfaceControl.getPhysicalDisplayIds()) {
            tryConnectDisplayLocked(physicalDisplayId);
        }
    }

    /* access modifiers changed from: private */
    public void tryConnectDisplayLocked(long physicalDisplayId) {
        int activeColorMode;
        long j = physicalDisplayId;
        IBinder displayToken = SurfaceControl.getPhysicalDisplayToken(physicalDisplayId);
        if (displayToken != null) {
            SurfaceControl.PhysicalDisplayInfo[] configs = SurfaceControl.getDisplayConfigs(displayToken);
            if (configs == null) {
                Slog.w(TAG, "No valid configs found for display device " + j);
                return;
            }
            int activeConfig = SurfaceControl.getActiveConfig(displayToken);
            if (activeConfig < 0) {
                Slog.w(TAG, "No active config found for display device " + j);
                return;
            }
            int activeColorMode2 = SurfaceControl.getActiveColorMode(displayToken);
            if (activeColorMode2 < 0) {
                Slog.w(TAG, "Unable to get active color mode for display device " + j);
                activeColorMode = -1;
            } else {
                activeColorMode = activeColorMode2;
            }
            int[] colorModes = SurfaceControl.getDisplayColorModes(displayToken);
            int[] allowedConfigs = SurfaceControl.getAllowedDisplayConfigs(displayToken);
            LocalDisplayDevice device = this.mDevices.get(j);
            if (device == null) {
                IBinder iBinder = displayToken;
                LocalDisplayDevice localDisplayDevice = device;
                LocalDisplayDevice device2 = new LocalDisplayDevice(this, displayToken, physicalDisplayId, configs, activeConfig, allowedConfigs, colorModes, activeColorMode, this.mDevices.size() == 0);
                this.mDevices.put(j, device2);
                sendDisplayDeviceEventLocked(device2, 1);
                return;
            }
            LocalDisplayDevice device3 = device;
            IBinder iBinder2 = displayToken;
            if (device3.updatePhysicalDisplayInfoLocked(configs, activeConfig, allowedConfigs, colorModes, activeColorMode)) {
                sendDisplayDeviceEventLocked(device3, 2);
            }
        }
    }

    /* access modifiers changed from: private */
    public void tryDisconnectDisplayLocked(long physicalDisplayId) {
        LocalDisplayDevice device = this.mDevices.get(physicalDisplayId);
        if (device != null) {
            this.mDevices.remove(physicalDisplayId);
            sendDisplayDeviceEventLocked(device, 3);
        }
    }

    static int getPowerModeForState(int state) {
        if (state == 1) {
            return 0;
        }
        if (state == 6) {
            return 4;
        }
        if (state == 3) {
            return 1;
        }
        if (state != 4) {
            return 2;
        }
        return 3;
    }

    private final class LocalDisplayDevice extends DisplayDevice {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        private int mActiveColorMode;
        private boolean mActiveColorModeInvalid;
        private int mActiveModeId;
        private boolean mActiveModeInvalid;
        private int mActivePhysIndex;
        private int[] mAllowedModeIds;
        private boolean mAllowedModeIdsInvalid;
        private int[] mAllowedPhysIndexes;
        /* access modifiers changed from: private */
        public final Light mBacklight;
        private int mBrightness = -1;
        private int mDefaultModeId;
        private SurfaceControl.PhysicalDisplayInfo[] mDisplayInfos;
        private boolean mHavePendingChanges;
        private Display.HdrCapabilities mHdrCapabilities;
        private DisplayDeviceInfo mInfo;
        private final boolean mIsInternal;
        private final long mPhysicalDisplayId;
        /* access modifiers changed from: private */
        public boolean mSidekickActive;
        /* access modifiers changed from: private */
        public SidekickInternal mSidekickInternal;
        private int mState = 0;
        private final ArrayList<Integer> mSupportedColorModes = new ArrayList<>();
        private final SparseArray<DisplayModeRecord> mSupportedModes = new SparseArray<>();
        final /* synthetic */ LocalDisplayAdapter this$0;

        static {
            Class<LocalDisplayAdapter> cls = LocalDisplayAdapter.class;
        }

        /* JADX WARNING: Illegal instructions before constructor call */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        LocalDisplayDevice(com.android.server.display.LocalDisplayAdapter r13, android.os.IBinder r14, long r15, android.view.SurfaceControl.PhysicalDisplayInfo[] r17, int r18, int[] r19, int[] r20, int r21, boolean r22) {
            /*
                r12 = this;
                r6 = r12
                r0 = r13
                r7 = r15
                r6.this$0 = r0
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "local:"
                r1.append(r2)
                r1.append(r7)
                java.lang.String r1 = r1.toString()
                r9 = r14
                r12.<init>(r13, r14, r1)
                android.util.SparseArray r0 = new android.util.SparseArray
                r0.<init>()
                r6.mSupportedModes = r0
                java.util.ArrayList r0 = new java.util.ArrayList
                r0.<init>()
                r6.mSupportedColorModes = r0
                r10 = 0
                r6.mState = r10
                r0 = -1
                r6.mBrightness = r0
                r6.mPhysicalDisplayId = r7
                r11 = r22
                r6.mIsInternal = r11
                r0 = r12
                r1 = r17
                r2 = r18
                r3 = r19
                r4 = r20
                r5 = r21
                r0.updatePhysicalDisplayInfoLocked(r1, r2, r3, r4, r5)
                r0 = r20
                r1 = r21
                r12.updateColorModesLocked(r0, r1)
                java.lang.Class<android.hardware.sidekick.SidekickInternal> r2 = android.hardware.sidekick.SidekickInternal.class
                java.lang.Object r2 = com.android.server.LocalServices.getService(r2)
                android.hardware.sidekick.SidekickInternal r2 = (android.hardware.sidekick.SidekickInternal) r2
                r6.mSidekickInternal = r2
                boolean r2 = r6.mIsInternal
                if (r2 == 0) goto L_0x0067
                java.lang.Class<com.android.server.lights.LightsManager> r2 = com.android.server.lights.LightsManager.class
                java.lang.Object r2 = com.android.server.LocalServices.getService(r2)
                com.android.server.lights.LightsManager r2 = (com.android.server.lights.LightsManager) r2
                com.android.server.lights.Light r3 = r2.getLight(r10)
                r6.mBacklight = r3
                goto L_0x006a
            L_0x0067:
                r2 = 0
                r6.mBacklight = r2
            L_0x006a:
                android.view.Display$HdrCapabilities r2 = android.view.SurfaceControl.getHdrCapabilities(r14)
                r6.mHdrCapabilities = r2
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.LocalDisplayAdapter.LocalDisplayDevice.<init>(com.android.server.display.LocalDisplayAdapter, android.os.IBinder, long, android.view.SurfaceControl$PhysicalDisplayInfo[], int, int[], int[], int, boolean):void");
        }

        public boolean hasStableUniqueId() {
            return true;
        }

        public boolean updatePhysicalDisplayInfoLocked(SurfaceControl.PhysicalDisplayInfo[] physicalDisplayInfos, int activeDisplayInfo, int[] allowedDisplayInfos, int[] colorModes, int activeColorMode) {
            SurfaceControl.PhysicalDisplayInfo[] physicalDisplayInfoArr = physicalDisplayInfos;
            int i = activeDisplayInfo;
            int[] iArr = allowedDisplayInfos;
            this.mDisplayInfos = (SurfaceControl.PhysicalDisplayInfo[]) Arrays.copyOf(physicalDisplayInfoArr, physicalDisplayInfoArr.length);
            this.mActivePhysIndex = i;
            this.mAllowedPhysIndexes = Arrays.copyOf(iArr, iArr.length);
            ArrayList<DisplayModeRecord> records = new ArrayList<>();
            boolean modesAdded = false;
            for (SurfaceControl.PhysicalDisplayInfo info : physicalDisplayInfoArr) {
                boolean existingMode = false;
                int j = 0;
                while (true) {
                    if (j >= records.size()) {
                        break;
                    } else if (records.get(j).hasMatchingMode(info)) {
                        existingMode = true;
                        break;
                    } else {
                        j++;
                    }
                }
                if (!existingMode) {
                    DisplayModeRecord record = findDisplayModeRecord(info);
                    if (record == null) {
                        record = new DisplayModeRecord(info);
                        modesAdded = true;
                    }
                    records.add(record);
                }
            }
            DisplayModeRecord activeRecord = null;
            int i2 = 0;
            while (true) {
                if (i2 >= records.size()) {
                    break;
                }
                DisplayModeRecord record2 = records.get(i2);
                if (record2.hasMatchingMode(physicalDisplayInfoArr[i])) {
                    activeRecord = record2;
                    break;
                }
                i2++;
            }
            int i3 = this.mActiveModeId;
            if (!(i3 == 0 || i3 == activeRecord.mMode.getModeId())) {
                this.mActiveModeInvalid = true;
                this.this$0.sendTraversalRequestLocked();
            }
            if (!(records.size() != this.mSupportedModes.size() || modesAdded)) {
                return false;
            }
            this.mHavePendingChanges = true;
            this.mSupportedModes.clear();
            Iterator<DisplayModeRecord> it = records.iterator();
            while (it.hasNext()) {
                DisplayModeRecord record3 = it.next();
                this.mSupportedModes.put(record3.mMode.getModeId(), record3);
            }
            if (findDisplayInfoIndexLocked(this.mDefaultModeId) < 0) {
                if (this.mDefaultModeId != 0) {
                    Slog.w(LocalDisplayAdapter.TAG, "Default display mode no longer available, using currently active mode as default.");
                }
                this.mDefaultModeId = activeRecord.mMode.getModeId();
            }
            if (this.mSupportedModes.indexOfKey(this.mActiveModeId) < 0) {
                if (this.mActiveModeId != 0) {
                    Slog.w(LocalDisplayAdapter.TAG, "Active display mode no longer available, reverting to default mode.");
                }
                this.mActiveModeId = this.mDefaultModeId;
                this.mActiveModeInvalid = true;
            }
            this.mAllowedModeIds = new int[]{this.mActiveModeId};
            int[] iArr2 = this.mAllowedPhysIndexes;
            int[] allowedModeIds = new int[iArr2.length];
            int size = 0;
            for (int physIndex : iArr2) {
                int modeId = findMatchingModeIdLocked(physIndex);
                if (modeId > 0) {
                    allowedModeIds[size] = modeId;
                    size++;
                }
            }
            this.mAllowedModeIdsInvalid = !Arrays.equals(allowedModeIds, this.mAllowedModeIds);
            this.this$0.sendTraversalRequestLocked();
            return true;
        }

        private boolean updateColorModesLocked(int[] colorModes, int activeColorMode) {
            List<Integer> pendingColorModes = new ArrayList<>();
            if (colorModes == null) {
                return false;
            }
            boolean colorModesAdded = false;
            for (int colorMode : colorModes) {
                if (!this.mSupportedColorModes.contains(Integer.valueOf(colorMode))) {
                    colorModesAdded = true;
                }
                pendingColorModes.add(Integer.valueOf(colorMode));
            }
            if (!(pendingColorModes.size() != this.mSupportedColorModes.size() || colorModesAdded)) {
                return false;
            }
            this.mHavePendingChanges = true;
            this.mSupportedColorModes.clear();
            this.mSupportedColorModes.addAll(pendingColorModes);
            Collections.sort(this.mSupportedColorModes);
            if (!this.mSupportedColorModes.contains(Integer.valueOf(this.mActiveColorMode))) {
                if (this.mActiveColorMode != 0) {
                    Slog.w(LocalDisplayAdapter.TAG, "Active color mode no longer available, reverting to default mode.");
                    this.mActiveColorMode = 0;
                    this.mActiveColorModeInvalid = true;
                } else if (!this.mSupportedColorModes.isEmpty()) {
                    Slog.e(LocalDisplayAdapter.TAG, "Default and active color mode is no longer available! Reverting to first available mode.");
                    this.mActiveColorMode = this.mSupportedColorModes.get(0).intValue();
                    this.mActiveColorModeInvalid = true;
                } else {
                    Slog.e(LocalDisplayAdapter.TAG, "No color modes available!");
                }
            }
            return true;
        }

        private DisplayModeRecord findDisplayModeRecord(SurfaceControl.PhysicalDisplayInfo info) {
            for (int i = 0; i < this.mSupportedModes.size(); i++) {
                DisplayModeRecord record = this.mSupportedModes.valueAt(i);
                if (record.hasMatchingMode(info)) {
                    return record;
                }
            }
            return null;
        }

        public void applyPendingDisplayDeviceInfoChangesLocked() {
            if (this.mHavePendingChanges) {
                this.mInfo = null;
                this.mHavePendingChanges = false;
            }
        }

        public DisplayDeviceInfo getDisplayDeviceInfoLocked() {
            if (this.mInfo == null) {
                SurfaceControl.PhysicalDisplayInfo phys = this.mDisplayInfos[this.mActivePhysIndex];
                this.mInfo = new DisplayDeviceInfo();
                this.mInfo.width = phys.width;
                this.mInfo.height = phys.height;
                DisplayDeviceInfo displayDeviceInfo = this.mInfo;
                displayDeviceInfo.modeId = this.mActiveModeId;
                displayDeviceInfo.defaultModeId = this.mDefaultModeId;
                displayDeviceInfo.supportedModes = getDisplayModes(this.mSupportedModes);
                DisplayDeviceInfo displayDeviceInfo2 = this.mInfo;
                displayDeviceInfo2.colorMode = this.mActiveColorMode;
                displayDeviceInfo2.supportedColorModes = new int[this.mSupportedColorModes.size()];
                for (int i = 0; i < this.mSupportedColorModes.size(); i++) {
                    this.mInfo.supportedColorModes[i] = this.mSupportedColorModes.get(i).intValue();
                }
                DisplayDeviceInfo displayDeviceInfo3 = this.mInfo;
                displayDeviceInfo3.hdrCapabilities = this.mHdrCapabilities;
                displayDeviceInfo3.appVsyncOffsetNanos = phys.appVsyncOffsetNanos;
                this.mInfo.presentationDeadlineNanos = phys.presentationDeadlineNanos;
                DisplayDeviceInfo displayDeviceInfo4 = this.mInfo;
                displayDeviceInfo4.state = this.mState;
                displayDeviceInfo4.uniqueId = getUniqueId();
                DisplayAddress.Physical physicalAddress = DisplayAddress.fromPhysicalDisplayId(this.mPhysicalDisplayId);
                this.mInfo.address = physicalAddress;
                if (phys.secure) {
                    this.mInfo.flags = 12;
                }
                Resources res = this.this$0.getOverlayContext().getResources();
                boolean isBuiltIn = this.mInfo.address != null && this.mInfo.address.getPort() < 0;
                if (this.mIsInternal) {
                    this.mInfo.name = res.getString(17039905);
                    DisplayDeviceInfo displayDeviceInfo5 = this.mInfo;
                    displayDeviceInfo5.flags = 3 | displayDeviceInfo5.flags;
                    if (res.getBoolean(17891480) || (Build.IS_EMULATOR && SystemProperties.getBoolean(LocalDisplayAdapter.PROPERTY_EMULATOR_CIRCULAR, false))) {
                        this.mInfo.flags |= 256;
                    }
                    if (res.getBoolean(17891481)) {
                        this.mInfo.flags |= 2048;
                    }
                    DisplayDeviceInfo displayDeviceInfo6 = this.mInfo;
                    displayDeviceInfo6.displayCutout = DisplayCutout.fromResourcesRectApproximation(res, displayDeviceInfo6.width, this.mInfo.height);
                    DisplayDeviceInfo displayDeviceInfo7 = this.mInfo;
                    displayDeviceInfo7.type = 1;
                    displayDeviceInfo7.densityDpi = (int) ((phys.density * 160.0f) + 0.5f);
                    this.mInfo.xDpi = phys.xDpi;
                    this.mInfo.yDpi = phys.yDpi;
                    this.mInfo.touch = 1;
                } else if (isBuiltIn) {
                    DisplayDeviceInfo displayDeviceInfo8 = this.mInfo;
                    displayDeviceInfo8.type = 1;
                    displayDeviceInfo8.touch = 1;
                    displayDeviceInfo8.name = this.this$0.getContext().getResources().getString(17039905);
                    DisplayDeviceInfo displayDeviceInfo9 = this.mInfo;
                    displayDeviceInfo9.flags = 2 | displayDeviceInfo9.flags;
                    if (SystemProperties.getBoolean("vendor.display.builtin_presentation", false)) {
                        this.mInfo.flags |= 64;
                    } else {
                        this.mInfo.flags |= 16;
                    }
                    if (!SystemProperties.getBoolean("vendor.display.builtin_mirroring", false)) {
                        this.mInfo.flags |= 128;
                    }
                    this.mInfo.setAssumedDensityForExternalDisplay(phys.width, phys.height);
                } else {
                    DisplayDeviceInfo displayDeviceInfo10 = this.mInfo;
                    displayDeviceInfo10.displayCutout = null;
                    displayDeviceInfo10.type = 2;
                    displayDeviceInfo10.flags |= 64;
                    this.mInfo.name = this.this$0.getContext().getResources().getString(17039906);
                    DisplayDeviceInfo displayDeviceInfo11 = this.mInfo;
                    displayDeviceInfo11.touch = 2;
                    displayDeviceInfo11.setAssumedDensityForExternalDisplay(phys.width, phys.height);
                    if ("portrait".equals(SystemProperties.get("persist.demo.hdmirotation"))) {
                        this.mInfo.rotation = 3;
                    }
                    if (SystemProperties.getBoolean("persist.demo.hdmirotates", false)) {
                        this.mInfo.flags |= 2;
                    }
                    if (!res.getBoolean(17891476)) {
                        this.mInfo.flags |= 128;
                    }
                    if (isDisplayPrivate(physicalAddress)) {
                        this.mInfo.flags |= 16;
                    }
                }
            }
            return this.mInfo;
        }

        public Runnable requestDisplayStateLocked(int state, int brightness) {
            int i = state;
            int i2 = brightness;
            boolean z = true;
            boolean stateChanged = this.mState != i;
            if (this.mBrightness == i2 || this.mBacklight == null) {
                z = false;
            }
            boolean brightnessChanged = z;
            if (!stateChanged && !brightnessChanged) {
                return null;
            }
            long physicalDisplayId = this.mPhysicalDisplayId;
            IBinder token = getDisplayTokenLocked();
            int oldState = this.mState;
            if (stateChanged) {
                this.mState = i;
                updateDeviceInfoLocked();
            }
            if (brightnessChanged) {
                this.mBrightness = i2;
            }
            final int i3 = oldState;
            final int i4 = state;
            final boolean z2 = brightnessChanged;
            final int i5 = brightness;
            final long j = physicalDisplayId;
            int i6 = oldState;
            final IBinder iBinder = token;
            return new Runnable() {
                public void run() {
                    int i;
                    int i2;
                    int currentState = i3;
                    boolean z = true;
                    if (Display.isSuspendedState(i3) || i3 == 0) {
                        if (!Display.isSuspendedState(i4)) {
                            ScreenOnMonitor.getInstance().recordTime(0);
                            if (z2 && i3 == 4 && i4 == 2) {
                                setDisplayState(3);
                                setDisplayBrightness(0);
                            }
                            setDisplayState(i4);
                            ScreenOnMonitor.getInstance().recordTime(1);
                            currentState = i4;
                        } else {
                            int i3 = i4;
                            if (i3 == 4 || (i2 = i3) == 4) {
                                setDisplayState(3);
                                currentState = 3;
                            } else if (i3 == 6 || i2 == 6) {
                                setDisplayState(2);
                                currentState = 2;
                            } else {
                                return;
                            }
                        }
                    }
                    ScreenEffectService.updateScreenEffect(i4);
                    boolean vrModeChange = false;
                    if ((i4 == 5 || currentState == 5) && currentState != (i = i4)) {
                        if (i != 5) {
                            z = false;
                        }
                        setVrMode(z);
                        vrModeChange = true;
                    }
                    if (z2 || vrModeChange) {
                        setDisplayBrightness(i5);
                    }
                    ScreenOnMonitor.getInstance().stopMonitor(i5, i4);
                    int i4 = i4;
                    if (i4 != currentState) {
                        setDisplayState(i4);
                    }
                }

                private void setVrMode(boolean isVrEnabled) {
                    LocalDisplayDevice.this.mBacklight.setVrMode(isVrEnabled);
                }

                /* JADX INFO: finally extract failed */
                private void setDisplayState(int state) {
                    Slog.d(LocalDisplayAdapter.TAG, "setDisplayState(id=" + j + ", state=" + Display.stateToString(state) + ")");
                    if (LocalDisplayDevice.this.mSidekickActive) {
                        Trace.traceBegin(131072, "SidekickInternal#endDisplayControl");
                        try {
                            LocalDisplayDevice.this.mSidekickInternal.endDisplayControl();
                            Trace.traceEnd(131072);
                            boolean unused = LocalDisplayDevice.this.mSidekickActive = false;
                        } catch (Throwable th) {
                            Trace.traceEnd(131072);
                            throw th;
                        }
                    }
                    int mode = LocalDisplayAdapter.getPowerModeForState(state);
                    Trace.traceBegin(131072, "setDisplayState(id=" + j + ", state=" + Display.stateToString(state) + ")");
                    try {
                        SurfaceControl.setDisplayPowerMode(iBinder, mode);
                        Trace.traceCounter(131072, "DisplayPowerMode", mode);
                        Trace.traceEnd(131072);
                        if (Display.isSuspendedState(state) && state != 1 && LocalDisplayDevice.this.mSidekickInternal != null && !LocalDisplayDevice.this.mSidekickActive) {
                            Trace.traceBegin(131072, "SidekickInternal#startDisplayControl");
                            try {
                                boolean unused2 = LocalDisplayDevice.this.mSidekickActive = LocalDisplayDevice.this.mSidekickInternal.startDisplayControl(state);
                            } finally {
                                Trace.traceEnd(131072);
                            }
                        }
                    } catch (Throwable th2) {
                        Trace.traceEnd(131072);
                        throw th2;
                    }
                }

                private void setDisplayBrightness(int brightness) {
                    Trace.traceBegin(131072, "setDisplayBrightness(id=" + j + ", brightness=" + brightness + ")");
                    try {
                        LocalDisplayDevice.this.mBacklight.setBrightness(brightness);
                        Trace.traceCounter(131072, "ScreenBrightness", brightness);
                    } finally {
                        Trace.traceEnd(131072);
                    }
                }
            };
        }

        public void setRequestedColorModeLocked(int colorMode) {
            if (requestColorModeLocked(colorMode)) {
                updateDeviceInfoLocked();
            }
        }

        public void setAllowedDisplayModesLocked(int[] modes) {
            updateAllowedModesLocked(modes);
        }

        public void onOverlayChangedLocked() {
            updateDeviceInfoLocked();
        }

        public void onActivePhysicalDisplayModeChangedLocked(int physIndex) {
            if (updateActiveModeLocked(physIndex)) {
                updateDeviceInfoLocked();
            }
        }

        public boolean updateActiveModeLocked(int activePhysIndex) {
            boolean z = false;
            if (this.mActivePhysIndex == activePhysIndex) {
                return false;
            }
            this.mActivePhysIndex = activePhysIndex;
            this.mActiveModeId = findMatchingModeIdLocked(activePhysIndex);
            if (this.mActiveModeId == 0) {
                z = true;
            }
            this.mActiveModeInvalid = z;
            if (this.mActiveModeInvalid) {
                Slog.w(LocalDisplayAdapter.TAG, "In unknown mode after setting allowed configs: allowedPhysIndexes=" + this.mAllowedPhysIndexes + ", activePhysIndex=" + this.mActivePhysIndex);
            }
            return true;
        }

        public void updateAllowedModesLocked(int[] allowedModes) {
            if ((!Arrays.equals(allowedModes, this.mAllowedModeIds) || this.mAllowedModeIdsInvalid) && updateAllowedModesInternalLocked(allowedModes)) {
                updateDeviceInfoLocked();
            }
        }

        public boolean updateAllowedModesInternalLocked(int[] allowedModes) {
            int[] allowedPhysIndexes = new int[allowedModes.length];
            int size = 0;
            for (int modeId : allowedModes) {
                int physIndex = findDisplayInfoIndexLocked(modeId);
                if (physIndex < 0) {
                    Slog.w(LocalDisplayAdapter.TAG, "Requested mode ID " + modeId + " not available, dropping from allowed set.");
                } else {
                    allowedPhysIndexes[size] = physIndex;
                    size++;
                }
            }
            if (size != allowedModes.length) {
                allowedPhysIndexes = Arrays.copyOf(allowedPhysIndexes, size);
            }
            if (size == 0) {
                int i = this.mDefaultModeId;
                allowedModes = new int[]{i};
                allowedPhysIndexes = new int[]{findDisplayInfoIndexLocked(i)};
            }
            this.mAllowedModeIds = allowedModes;
            this.mAllowedModeIdsInvalid = false;
            if (Arrays.equals(this.mAllowedPhysIndexes, allowedPhysIndexes)) {
                return false;
            }
            this.mAllowedPhysIndexes = allowedPhysIndexes;
            SurfaceControl.setAllowedDisplayConfigs(getDisplayTokenLocked(), allowedPhysIndexes);
            return updateActiveModeLocked(SurfaceControl.getActiveConfig(getDisplayTokenLocked()));
        }

        public boolean requestColorModeLocked(int colorMode) {
            if (this.mActiveColorMode == colorMode) {
                return false;
            }
            if (!this.mSupportedColorModes.contains(Integer.valueOf(colorMode))) {
                Slog.w(LocalDisplayAdapter.TAG, "Unable to find color mode " + colorMode + ", ignoring request.");
                return false;
            }
            SurfaceControl.setActiveColorMode(getDisplayTokenLocked(), colorMode);
            this.mActiveColorMode = colorMode;
            this.mActiveColorModeInvalid = false;
            return true;
        }

        public void dumpLocked(PrintWriter pw) {
            super.dumpLocked(pw);
            pw.println("mPhysicalDisplayId=" + this.mPhysicalDisplayId);
            pw.println("mAllowedPhysIndexes=" + Arrays.toString(this.mAllowedPhysIndexes));
            pw.println("mAllowedModeIds=" + Arrays.toString(this.mAllowedModeIds));
            pw.println("mAllowedModeIdsInvalid=" + this.mAllowedModeIdsInvalid);
            pw.println("mActivePhysIndex=" + this.mActivePhysIndex);
            pw.println("mActiveModeId=" + this.mActiveModeId);
            pw.println("mActiveColorMode=" + this.mActiveColorMode);
            pw.println("mDefaultModeId=" + this.mDefaultModeId);
            pw.println("mState=" + Display.stateToString(this.mState));
            pw.println("mBrightness=" + this.mBrightness);
            pw.println("mBacklight=" + this.mBacklight);
            pw.println("mDisplayInfos=");
            for (int i = 0; i < this.mDisplayInfos.length; i++) {
                pw.println("  " + this.mDisplayInfos[i]);
            }
            pw.println("mSupportedModes=");
            for (int i2 = 0; i2 < this.mSupportedModes.size(); i2++) {
                pw.println("  " + this.mSupportedModes.valueAt(i2));
            }
            pw.print("mSupportedColorModes=[");
            for (int i3 = 0; i3 < this.mSupportedColorModes.size(); i3++) {
                if (i3 != 0) {
                    pw.print(", ");
                }
                pw.print(this.mSupportedColorModes.get(i3));
            }
            pw.println("]");
        }

        private int findDisplayInfoIndexLocked(int modeId) {
            DisplayModeRecord record = this.mSupportedModes.get(modeId);
            if (record == null) {
                return -1;
            }
            int i = 0;
            while (true) {
                SurfaceControl.PhysicalDisplayInfo[] physicalDisplayInfoArr = this.mDisplayInfos;
                if (i >= physicalDisplayInfoArr.length) {
                    return -1;
                }
                if (record.hasMatchingMode(physicalDisplayInfoArr[i])) {
                    return i;
                }
                i++;
            }
        }

        private int findMatchingModeIdLocked(int physIndex) {
            SurfaceControl.PhysicalDisplayInfo info = this.mDisplayInfos[physIndex];
            for (int i = 0; i < this.mSupportedModes.size(); i++) {
                DisplayModeRecord record = this.mSupportedModes.valueAt(i);
                if (record.hasMatchingMode(info)) {
                    return record.mMode.getModeId();
                }
            }
            return 0;
        }

        private void updateDeviceInfoLocked() {
            this.mInfo = null;
            this.this$0.sendDisplayDeviceEventLocked(this, 2);
        }

        private Display.Mode[] getDisplayModes(SparseArray<DisplayModeRecord> records) {
            int size = records.size();
            Display.Mode[] modes = new Display.Mode[size];
            for (int i = 0; i < size; i++) {
                modes[i] = records.valueAt(i).mMode;
            }
            return modes;
        }

        private boolean isDisplayPrivate(DisplayAddress.Physical physicalAddress) {
            int[] ports;
            if (!(physicalAddress == null || (ports = this.this$0.getOverlayContext().getResources().getIntArray(17236034)) == null)) {
                int port = physicalAddress.getPort();
                for (int p : ports) {
                    if (p == port) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    public Context getOverlayContext() {
        return ActivityThread.currentActivityThread().getSystemUiContext();
    }

    private static final class DisplayModeRecord {
        public final Display.Mode mMode;

        public DisplayModeRecord(SurfaceControl.PhysicalDisplayInfo phys) {
            this.mMode = DisplayAdapter.createMode(phys.width, phys.height, phys.refreshRate);
        }

        public boolean hasMatchingMode(SurfaceControl.PhysicalDisplayInfo info) {
            return this.mMode.getPhysicalWidth() == info.width && this.mMode.getPhysicalHeight() == info.height && Float.floatToIntBits(this.mMode.getRefreshRate()) == Float.floatToIntBits(info.refreshRate);
        }

        public String toString() {
            return "DisplayModeRecord{mMode=" + this.mMode + "}";
        }
    }

    private final class PhysicalDisplayEventReceiver extends DisplayEventReceiver {
        PhysicalDisplayEventReceiver(Looper looper) {
            super(looper, 0);
        }

        public void onHotplug(long timestampNanos, long physicalDisplayId, boolean connected) {
            synchronized (LocalDisplayAdapter.this.getSyncRoot()) {
                if (connected) {
                    LocalDisplayAdapter.this.tryConnectDisplayLocked(physicalDisplayId);
                } else {
                    LocalDisplayAdapter.this.tryDisconnectDisplayLocked(physicalDisplayId);
                }
            }
        }

        public void onConfigChanged(long timestampNanos, long physicalDisplayId, int physIndex) {
            synchronized (LocalDisplayAdapter.this.getSyncRoot()) {
                LocalDisplayDevice device = (LocalDisplayDevice) LocalDisplayAdapter.this.mDevices.get(physicalDisplayId);
                if (device != null) {
                    device.onActivePhysicalDisplayModeChangedLocked(physIndex);
                }
            }
        }
    }
}
