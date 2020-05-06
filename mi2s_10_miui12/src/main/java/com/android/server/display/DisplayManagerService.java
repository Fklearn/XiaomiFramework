package com.android.server.display;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.ParceledListSlice;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.ColorSpace;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.SensorManager;
import android.hardware.display.AmbientBrightnessDayStats;
import android.hardware.display.BrightnessChangeEvent;
import android.hardware.display.BrightnessConfiguration;
import android.hardware.display.Curve;
import android.hardware.display.DisplayManagerInternal;
import android.hardware.display.DisplayViewport;
import android.hardware.display.DisplayedContentSample;
import android.hardware.display.DisplayedContentSamplingAttributes;
import android.hardware.display.IDisplayManager;
import android.hardware.display.IDisplayManagerCallback;
import android.hardware.display.IVirtualDisplayCallback;
import android.hardware.display.WifiDisplayStatus;
import android.hardware.input.InputManagerInternal;
import android.media.projection.IMediaProjection;
import android.media.projection.IMediaProjectionManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.IntArray;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.Spline;
import android.view.Display;
import android.view.DisplayAddress;
import android.view.DisplayCutout;
import android.view.DisplayInfo;
import android.view.Surface;
import android.view.SurfaceControl;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.AnimationThread;
import com.android.server.DisplayThread;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.UiThread;
import com.android.server.display.DisplayAdapter;
import com.android.server.display.DisplayModeDirector;
import com.android.server.wm.SurfaceAnimationThread;
import com.android.server.wm.WindowManagerInternal;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public final class DisplayManagerService extends SystemService {
    private static final boolean DEBUG = false;
    private static final String FORCE_WIFI_DISPLAY_ENABLE = "persist.debug.wfd.enable";
    private static final int MSG_DELIVER_DISPLAY_EVENT = 3;
    private static final int MSG_LOAD_BRIGHTNESS_CONFIGURATION = 6;
    private static final int MSG_REGISTER_ADDITIONAL_DISPLAY_ADAPTERS = 2;
    private static final int MSG_REGISTER_DEFAULT_DISPLAY_ADAPTERS = 1;
    private static final int MSG_REQUEST_TRAVERSAL = 4;
    private static final int MSG_UPDATE_VIEWPORT = 5;
    private static final String PROP_DEFAULT_DISPLAY_TOP_INSET = "persist.sys.displayinset.top";
    private static final String TAG = "DisplayManagerService";
    private static final long WAIT_FOR_DEFAULT_DISPLAY_TIMEOUT = 10000;
    public final SparseArray<CallbackRecord> mCallbacks;
    /* access modifiers changed from: private */
    public final Context mContext;
    private int mCurrentUserId;
    private final int mDefaultDisplayDefaultColorMode;
    private int mDefaultDisplayTopInset;
    private final SparseArray<IntArray> mDisplayAccessUIDs;
    private final DisplayAdapterListener mDisplayAdapterListener;
    private final ArrayList<DisplayAdapter> mDisplayAdapters;
    /* access modifiers changed from: private */
    public final ArrayList<DisplayDevice> mDisplayDevices;
    private final DisplayModeDirector mDisplayModeDirector;
    /* access modifiers changed from: private */
    public DisplayPowerController mDisplayPowerController;
    private final CopyOnWriteArrayList<DisplayManagerInternal.DisplayTransactionListener> mDisplayTransactionListeners;
    private int mGlobalDisplayBrightness;
    private int mGlobalDisplayState;
    /* access modifiers changed from: private */
    public final DisplayManagerHandler mHandler;
    private final Injector mInjector;
    /* access modifiers changed from: private */
    public InputManagerInternal mInputManagerInternal;
    private final SparseArray<LogicalDisplay> mLogicalDisplays;
    private final Curve mMinimumBrightnessCurve;
    private final Spline mMinimumBrightnessSpline;
    private int mNextBuiltInDisplayId;
    private int mNextNonDefaultDisplayId;
    public boolean mOnlyCore;
    private boolean mPendingTraversal;
    /* access modifiers changed from: private */
    public final PersistentDataStore mPersistentDataStore;
    private IMediaProjectionManager mProjectionService;
    public boolean mSafeMode;
    private final boolean mSingleDisplayDemoMode;
    private Point mStableDisplaySize;
    /* access modifiers changed from: private */
    public final SyncRoot mSyncRoot;
    private boolean mSystemReady;
    private final ArrayList<CallbackRecord> mTempCallbacks;
    private final DisplayInfo mTempDisplayInfo;
    private final ArrayList<Runnable> mTempDisplayStateWorkQueue;
    /* access modifiers changed from: private */
    public final ArrayList<DisplayViewport> mTempViewports;
    private final Handler mUiHandler;
    /* access modifiers changed from: private */
    @GuardedBy({"mSyncRoot"})
    public final ArrayList<DisplayViewport> mViewports;
    private VirtualDisplayAdapter mVirtualDisplayAdapter;
    private final ColorSpace mWideColorSpace;
    private WifiDisplayAdapter mWifiDisplayAdapter;
    private int mWifiDisplayScanRequestCount;
    /* access modifiers changed from: private */
    public WindowManagerInternal mWindowManagerInternal;

    public static final class SyncRoot {
    }

    public DisplayManagerService(Context context) {
        this(context, new Injector());
    }

    @VisibleForTesting
    DisplayManagerService(Context context, Injector injector) {
        super(context);
        this.mSyncRoot = new SyncRoot();
        this.mCallbacks = new SparseArray<>();
        this.mDisplayAdapters = new ArrayList<>();
        this.mDisplayDevices = new ArrayList<>();
        this.mLogicalDisplays = new SparseArray<>();
        this.mNextNonDefaultDisplayId = 1;
        this.mNextBuiltInDisplayId = 4096;
        this.mDisplayTransactionListeners = new CopyOnWriteArrayList<>();
        this.mGlobalDisplayState = 2;
        this.mGlobalDisplayBrightness = -1;
        this.mStableDisplaySize = new Point();
        this.mViewports = new ArrayList<>();
        this.mPersistentDataStore = new PersistentDataStore();
        this.mTempCallbacks = new ArrayList<>();
        this.mTempDisplayInfo = new DisplayInfo();
        this.mTempViewports = new ArrayList<>();
        this.mTempDisplayStateWorkQueue = new ArrayList<>();
        this.mDisplayAccessUIDs = new SparseArray<>();
        this.mInjector = injector;
        this.mContext = context;
        this.mHandler = new DisplayManagerHandler(DisplayThread.get().getLooper());
        this.mUiHandler = UiThread.getHandler();
        this.mDisplayAdapterListener = new DisplayAdapterListener();
        this.mDisplayModeDirector = new DisplayModeDirector(context, this.mHandler);
        this.mSingleDisplayDemoMode = SystemProperties.getBoolean("persist.demo.singledisplay", false);
        Resources resources = this.mContext.getResources();
        this.mDefaultDisplayDefaultColorMode = this.mContext.getResources().getInteger(17694770);
        this.mDefaultDisplayTopInset = SystemProperties.getInt(PROP_DEFAULT_DISPLAY_TOP_INSET, -1);
        float[] lux = getFloatArray(resources.obtainTypedArray(17236039));
        float[] nits = getFloatArray(resources.obtainTypedArray(17236040));
        this.mMinimumBrightnessCurve = new Curve(lux, nits);
        this.mMinimumBrightnessSpline = Spline.createSpline(lux, nits);
        this.mGlobalDisplayBrightness = ((PowerManager) this.mContext.getSystemService(PowerManager.class)).getDefaultScreenBrightnessSetting();
        this.mCurrentUserId = 0;
        this.mWideColorSpace = SurfaceControl.getCompositionColorSpaces()[1];
        this.mSystemReady = false;
    }

    public void setupSchedulerPolicies() {
        Process.setThreadGroupAndCpuset(DisplayThread.get().getThreadId(), 5);
        Process.setThreadGroupAndCpuset(AnimationThread.get().getThreadId(), 5);
        Process.setThreadGroupAndCpuset(SurfaceAnimationThread.get().getThreadId(), 5);
    }

    /* JADX WARNING: type inference failed for: r0v2, types: [com.android.server.display.DisplayManagerService$BinderService, android.os.IBinder] */
    public void onStart() {
        synchronized (this.mSyncRoot) {
            this.mPersistentDataStore.loadIfNeeded();
            loadStableDisplayValuesLocked();
        }
        this.mHandler.sendEmptyMessage(1);
        publishBinderService("display", new BinderService(), true);
        publishLocalService(DisplayManagerInternal.class, new LocalService());
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    public void onBootPhase(int phase) {
        if (phase == 100) {
            synchronized (this.mSyncRoot) {
                long timeout = SystemClock.uptimeMillis() + this.mInjector.getDefaultDisplayDelayTimeout();
                while (true) {
                    if (this.mLogicalDisplays.get(0) != null) {
                        if (this.mVirtualDisplayAdapter == null) {
                        }
                    }
                    long delay = timeout - SystemClock.uptimeMillis();
                    if (delay > 0) {
                        try {
                            this.mSyncRoot.wait(delay);
                        } catch (InterruptedException e) {
                        }
                    } else {
                        throw new RuntimeException("Timeout waiting for default display to be initialized. DefaultDisplay=" + this.mLogicalDisplays.get(0) + ", mVirtualDisplayAdapter=" + this.mVirtualDisplayAdapter);
                    }
                }
            }
        }
    }

    public void switchUserInternal(int newUserId) {
        int userSerial = getUserManager().getUserSerialNumber(newUserId);
        synchronized (this.mSyncRoot) {
            if (this.mCurrentUserId != newUserId) {
                this.mCurrentUserId = newUserId;
                this.mDisplayPowerController.setBrightnessConfiguration(this.mPersistentDataStore.getBrightnessConfiguration(userSerial));
            }
            this.mDisplayPowerController.onSwitchUser(newUserId);
        }
    }

    public void windowManagerAndInputReady() {
        synchronized (this.mSyncRoot) {
            this.mWindowManagerInternal = (WindowManagerInternal) LocalServices.getService(WindowManagerInternal.class);
            this.mInputManagerInternal = (InputManagerInternal) LocalServices.getService(InputManagerInternal.class);
            scheduleTraversalLocked(false);
        }
    }

    public void systemReady(boolean safeMode, boolean onlyCore) {
        synchronized (this.mSyncRoot) {
            this.mSafeMode = safeMode;
            this.mOnlyCore = onlyCore;
            this.mSystemReady = true;
            recordTopInsetLocked(this.mLogicalDisplays.get(0));
        }
        this.mDisplayModeDirector.setListener(new AllowedDisplayModeObserver());
        this.mDisplayModeDirector.start();
        this.mHandler.sendEmptyMessage(2);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public Handler getDisplayHandler() {
        return this.mHandler;
    }

    private void loadStableDisplayValuesLocked() {
        Point size = this.mPersistentDataStore.getStableDisplaySize();
        if (size.x <= 0 || size.y <= 0) {
            Resources res = this.mContext.getResources();
            int width = res.getInteger(17694900);
            int height = res.getInteger(17694899);
            if (width > 0 && height > 0) {
                setStableDisplaySizeLocked(width, height);
                return;
            }
            return;
        }
        this.mStableDisplaySize.set(size.x, size.y);
    }

    /* access modifiers changed from: private */
    public Point getStableDisplaySizeInternal() {
        Point r = new Point();
        synchronized (this.mSyncRoot) {
            if (this.mStableDisplaySize.x > 0 && this.mStableDisplaySize.y > 0) {
                r.set(this.mStableDisplaySize.x, this.mStableDisplaySize.y);
            }
        }
        return r;
    }

    /* access modifiers changed from: private */
    public void registerDisplayTransactionListenerInternal(DisplayManagerInternal.DisplayTransactionListener listener) {
        this.mDisplayTransactionListeners.add(listener);
    }

    /* access modifiers changed from: private */
    public void unregisterDisplayTransactionListenerInternal(DisplayManagerInternal.DisplayTransactionListener listener) {
        this.mDisplayTransactionListeners.remove(listener);
    }

    /* access modifiers changed from: private */
    public void setDisplayInfoOverrideFromWindowManagerInternal(int displayId, DisplayInfo info) {
        synchronized (this.mSyncRoot) {
            LogicalDisplay display = this.mLogicalDisplays.get(displayId);
            if (display != null && display.setDisplayInfoOverrideFromWindowManagerLocked(info)) {
                handleLogicalDisplayChanged(displayId, display);
                scheduleTraversalLocked(false);
            }
        }
    }

    /* access modifiers changed from: private */
    public void getNonOverrideDisplayInfoInternal(int displayId, DisplayInfo outInfo) {
        synchronized (this.mSyncRoot) {
            LogicalDisplay display = this.mLogicalDisplays.get(displayId);
            if (display != null) {
                display.getNonOverrideDisplayInfoLocked(outInfo);
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001a, code lost:
        if (r0.hasNext() == false) goto L_0x0026;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x001c, code lost:
        r0.next().onDisplayTransaction(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0026, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0010, code lost:
        r0 = r2.mDisplayTransactionListeners.iterator();
     */
    @com.android.internal.annotations.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void performTraversalInternal(android.view.SurfaceControl.Transaction r3) {
        /*
            r2 = this;
            com.android.server.display.DisplayManagerService$SyncRoot r0 = r2.mSyncRoot
            monitor-enter(r0)
            boolean r1 = r2.mPendingTraversal     // Catch:{ all -> 0x0027 }
            if (r1 != 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x0027 }
            return
        L_0x0009:
            r1 = 0
            r2.mPendingTraversal = r1     // Catch:{ all -> 0x0027 }
            r2.performTraversalLocked(r3)     // Catch:{ all -> 0x0027 }
            monitor-exit(r0)     // Catch:{ all -> 0x0027 }
            java.util.concurrent.CopyOnWriteArrayList<android.hardware.display.DisplayManagerInternal$DisplayTransactionListener> r0 = r2.mDisplayTransactionListeners
            java.util.Iterator r0 = r0.iterator()
        L_0x0016:
            boolean r1 = r0.hasNext()
            if (r1 == 0) goto L_0x0026
            java.lang.Object r1 = r0.next()
            android.hardware.display.DisplayManagerInternal$DisplayTransactionListener r1 = (android.hardware.display.DisplayManagerInternal.DisplayTransactionListener) r1
            r1.onDisplayTransaction(r3)
            goto L_0x0016
        L_0x0026:
            return
        L_0x0027:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0027 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.DisplayManagerService.performTraversalInternal(android.view.SurfaceControl$Transaction):void");
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x005b, code lost:
        r1 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0062, code lost:
        if (r1 >= r5.mTempDisplayStateWorkQueue.size()) goto L_0x0072;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0064, code lost:
        r5.mTempDisplayStateWorkQueue.get(r1).run();
        r1 = r1 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0072, code lost:
        android.os.Trace.traceEnd(131072);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:?, code lost:
        r5.mTempDisplayStateWorkQueue.clear();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x007c, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void requestGlobalDisplayStateInternal(int r6, int r7) {
        /*
            r5 = this;
            if (r6 != 0) goto L_0x0003
            r6 = 2
        L_0x0003:
            r0 = 1
            if (r6 != r0) goto L_0x0008
            r7 = 0
            goto L_0x0012
        L_0x0008:
            if (r7 >= 0) goto L_0x000c
            r7 = -1
            goto L_0x0012
        L_0x000c:
            int r0 = android.os.PowerManager.BRIGHTNESS_ON
            if (r7 <= r0) goto L_0x0012
            int r7 = android.os.PowerManager.BRIGHTNESS_ON
        L_0x0012:
            java.util.ArrayList<java.lang.Runnable> r0 = r5.mTempDisplayStateWorkQueue
            monitor-enter(r0)
            com.android.server.display.DisplayManagerService$SyncRoot r1 = r5.mSyncRoot     // Catch:{ all -> 0x0080 }
            monitor-enter(r1)     // Catch:{ all -> 0x0080 }
            int r2 = r5.mGlobalDisplayState     // Catch:{ all -> 0x007d }
            if (r2 != r6) goto L_0x0028
            int r2 = r5.mGlobalDisplayBrightness     // Catch:{ all -> 0x007d }
            if (r2 != r7) goto L_0x0028
            monitor-exit(r1)     // Catch:{ all -> 0x007d }
            java.util.ArrayList<java.lang.Runnable> r1 = r5.mTempDisplayStateWorkQueue     // Catch:{ all -> 0x0087 }
            r1.clear()     // Catch:{ all -> 0x0087 }
            monitor-exit(r0)     // Catch:{ all -> 0x0087 }
            return
        L_0x0028:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x007d }
            r2.<init>()     // Catch:{ all -> 0x007d }
            java.lang.String r3 = "requestGlobalDisplayState("
            r2.append(r3)     // Catch:{ all -> 0x007d }
            java.lang.String r3 = android.view.Display.stateToString(r6)     // Catch:{ all -> 0x007d }
            r2.append(r3)     // Catch:{ all -> 0x007d }
            java.lang.String r3 = ", brightness="
            r2.append(r3)     // Catch:{ all -> 0x007d }
            r2.append(r7)     // Catch:{ all -> 0x007d }
            java.lang.String r3 = ")"
            r2.append(r3)     // Catch:{ all -> 0x007d }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x007d }
            r3 = 131072(0x20000, double:6.47582E-319)
            android.os.Trace.traceBegin(r3, r2)     // Catch:{ all -> 0x007d }
            r5.mGlobalDisplayState = r6     // Catch:{ all -> 0x007d }
            r5.mGlobalDisplayBrightness = r7     // Catch:{ all -> 0x007d }
            java.util.ArrayList<java.lang.Runnable> r2 = r5.mTempDisplayStateWorkQueue     // Catch:{ all -> 0x007d }
            r5.applyGlobalDisplayStateLocked(r2)     // Catch:{ all -> 0x007d }
            monitor-exit(r1)     // Catch:{ all -> 0x007d }
            r1 = 0
        L_0x005c:
            java.util.ArrayList<java.lang.Runnable> r2 = r5.mTempDisplayStateWorkQueue     // Catch:{ all -> 0x0080 }
            int r2 = r2.size()     // Catch:{ all -> 0x0080 }
            if (r1 >= r2) goto L_0x0072
            java.util.ArrayList<java.lang.Runnable> r2 = r5.mTempDisplayStateWorkQueue     // Catch:{ all -> 0x0080 }
            java.lang.Object r2 = r2.get(r1)     // Catch:{ all -> 0x0080 }
            java.lang.Runnable r2 = (java.lang.Runnable) r2     // Catch:{ all -> 0x0080 }
            r2.run()     // Catch:{ all -> 0x0080 }
            int r1 = r1 + 1
            goto L_0x005c
        L_0x0072:
            android.os.Trace.traceEnd(r3)     // Catch:{ all -> 0x0080 }
            java.util.ArrayList<java.lang.Runnable> r1 = r5.mTempDisplayStateWorkQueue     // Catch:{ all -> 0x0087 }
            r1.clear()     // Catch:{ all -> 0x0087 }
            monitor-exit(r0)     // Catch:{ all -> 0x0087 }
            return
        L_0x007d:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x007d }
            throw r2     // Catch:{ all -> 0x0080 }
        L_0x0080:
            r1 = move-exception
            java.util.ArrayList<java.lang.Runnable> r2 = r5.mTempDisplayStateWorkQueue     // Catch:{ all -> 0x0087 }
            r2.clear()     // Catch:{ all -> 0x0087 }
            throw r1     // Catch:{ all -> 0x0087 }
        L_0x0087:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0087 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.DisplayManagerService.requestGlobalDisplayStateInternal(int, int):void");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001e, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.view.DisplayInfo getDisplayInfoInternal(int r5, int r6) {
        /*
            r4 = this;
            com.android.server.display.DisplayManagerService$SyncRoot r0 = r4.mSyncRoot
            monitor-enter(r0)
            android.util.SparseArray<com.android.server.display.LogicalDisplay> r1 = r4.mLogicalDisplays     // Catch:{ all -> 0x0022 }
            java.lang.Object r1 = r1.get(r5)     // Catch:{ all -> 0x0022 }
            com.android.server.display.LogicalDisplay r1 = (com.android.server.display.LogicalDisplay) r1     // Catch:{ all -> 0x0022 }
            if (r1 == 0) goto L_0x001f
            android.view.DisplayInfo r2 = r1.getDisplayInfoLocked()     // Catch:{ all -> 0x0022 }
            boolean r3 = r2.hasAccess(r6)     // Catch:{ all -> 0x0022 }
            if (r3 != 0) goto L_0x001d
            boolean r3 = r4.isUidPresentOnDisplayInternal(r6, r5)     // Catch:{ all -> 0x0022 }
            if (r3 == 0) goto L_0x001f
        L_0x001d:
            monitor-exit(r0)     // Catch:{ all -> 0x0022 }
            return r2
        L_0x001f:
            r2 = 0
            monitor-exit(r0)     // Catch:{ all -> 0x0022 }
            return r2
        L_0x0022:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0022 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.DisplayManagerService.getDisplayInfoInternal(int, int):android.view.DisplayInfo");
    }

    /* access modifiers changed from: private */
    public int[] getDisplayIdsInternal(int callingUid) {
        int[] displayIds;
        synchronized (this.mSyncRoot) {
            int count = this.mLogicalDisplays.size();
            displayIds = new int[count];
            int n = 0;
            for (int i = 0; i < count; i++) {
                if (this.mLogicalDisplays.valueAt(i).getDisplayInfoLocked().hasAccess(callingUid)) {
                    displayIds[n] = this.mLogicalDisplays.keyAt(i);
                    n++;
                }
            }
            if (n != count) {
                displayIds = Arrays.copyOfRange(displayIds, 0, n);
            }
        }
        return displayIds;
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: private */
    public void registerCallbackInternal(IDisplayManagerCallback callback, int callingPid) {
        synchronized (this.mSyncRoot) {
            if (this.mCallbacks.get(callingPid) == null) {
                CallbackRecord record = new CallbackRecord(callingPid, callback);
                try {
                    callback.asBinder().linkToDeath(record, 0);
                    this.mCallbacks.put(callingPid, record);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                throw new SecurityException("The calling process has already registered an IDisplayManagerCallback.");
            }
        }
    }

    /* access modifiers changed from: private */
    public void onCallbackDied(CallbackRecord record) {
        synchronized (this.mSyncRoot) {
            this.mCallbacks.remove(record.mPid);
            stopWifiDisplayScanLocked(record);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: private */
    public void startWifiDisplayScanInternal(int callingPid) {
        synchronized (this.mSyncRoot) {
            CallbackRecord record = this.mCallbacks.get(callingPid);
            if (record != null) {
                startWifiDisplayScanLocked(record);
            } else {
                throw new IllegalStateException("The calling process has not registered an IDisplayManagerCallback.");
            }
        }
    }

    private void startWifiDisplayScanLocked(CallbackRecord record) {
        WifiDisplayAdapter wifiDisplayAdapter;
        if (!record.mWifiDisplayScanRequested) {
            record.mWifiDisplayScanRequested = true;
            int i = this.mWifiDisplayScanRequestCount;
            this.mWifiDisplayScanRequestCount = i + 1;
            if (i == 0 && (wifiDisplayAdapter = this.mWifiDisplayAdapter) != null) {
                wifiDisplayAdapter.requestStartScanLocked();
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: private */
    public void stopWifiDisplayScanInternal(int callingPid) {
        synchronized (this.mSyncRoot) {
            CallbackRecord record = this.mCallbacks.get(callingPid);
            if (record != null) {
                stopWifiDisplayScanLocked(record);
            } else {
                throw new IllegalStateException("The calling process has not registered an IDisplayManagerCallback.");
            }
        }
    }

    private void stopWifiDisplayScanLocked(CallbackRecord record) {
        if (record.mWifiDisplayScanRequested) {
            record.mWifiDisplayScanRequested = false;
            int i = this.mWifiDisplayScanRequestCount - 1;
            this.mWifiDisplayScanRequestCount = i;
            if (i == 0) {
                WifiDisplayAdapter wifiDisplayAdapter = this.mWifiDisplayAdapter;
                if (wifiDisplayAdapter != null) {
                    wifiDisplayAdapter.requestStopScanLocked();
                }
            } else if (this.mWifiDisplayScanRequestCount < 0) {
                Slog.wtf(TAG, "mWifiDisplayScanRequestCount became negative: " + this.mWifiDisplayScanRequestCount);
                this.mWifiDisplayScanRequestCount = 0;
            }
        }
    }

    /* access modifiers changed from: private */
    public void connectWifiDisplayInternal(String address) {
        synchronized (this.mSyncRoot) {
            if (this.mWifiDisplayAdapter != null) {
                this.mWifiDisplayAdapter.requestConnectLocked(address);
            }
        }
    }

    /* access modifiers changed from: private */
    public void pauseWifiDisplayInternal() {
        synchronized (this.mSyncRoot) {
            if (this.mWifiDisplayAdapter != null) {
                this.mWifiDisplayAdapter.requestPauseLocked();
            }
        }
    }

    /* access modifiers changed from: private */
    public void resumeWifiDisplayInternal() {
        synchronized (this.mSyncRoot) {
            if (this.mWifiDisplayAdapter != null) {
                this.mWifiDisplayAdapter.requestResumeLocked();
            }
        }
    }

    /* access modifiers changed from: private */
    public void disconnectWifiDisplayInternal() {
        synchronized (this.mSyncRoot) {
            if (this.mWifiDisplayAdapter != null) {
                this.mWifiDisplayAdapter.requestDisconnectLocked();
            }
        }
    }

    /* access modifiers changed from: private */
    public void renameWifiDisplayInternal(String address, String alias) {
        synchronized (this.mSyncRoot) {
            if (this.mWifiDisplayAdapter != null) {
                this.mWifiDisplayAdapter.requestRenameLocked(address, alias);
            }
        }
    }

    /* access modifiers changed from: private */
    public void forgetWifiDisplayInternal(String address) {
        synchronized (this.mSyncRoot) {
            if (this.mWifiDisplayAdapter != null) {
                this.mWifiDisplayAdapter.requestForgetLocked(address);
            }
        }
    }

    /* access modifiers changed from: private */
    public WifiDisplayStatus getWifiDisplayStatusInternal() {
        synchronized (this.mSyncRoot) {
            if (this.mWifiDisplayAdapter != null) {
                WifiDisplayStatus wifiDisplayStatusLocked = this.mWifiDisplayAdapter.getWifiDisplayStatusLocked();
                return wifiDisplayStatusLocked;
            }
            WifiDisplayStatus wifiDisplayStatus = new WifiDisplayStatus();
            return wifiDisplayStatus;
        }
    }

    /* access modifiers changed from: private */
    public void requestColorModeInternal(int displayId, int colorMode) {
        synchronized (this.mSyncRoot) {
            LogicalDisplay display = this.mLogicalDisplays.get(displayId);
            if (!(display == null || display.getRequestedColorModeLocked() == colorMode)) {
                display.setRequestedColorModeLocked(colorMode);
                scheduleTraversalLocked(false);
            }
        }
    }

    /* access modifiers changed from: private */
    public int createVirtualDisplayInternal(IVirtualDisplayCallback callback, IMediaProjection projection, int callingUid, String packageName, String name, int width, int height, int densityDpi, Surface surface, int flags, String uniqueId) {
        synchronized (this.mSyncRoot) {
            if (this.mVirtualDisplayAdapter == null) {
                Slog.w(TAG, "Rejecting request to create private virtual display because the virtual display adapter is not available.");
                return -1;
            }
            DisplayDevice device = this.mVirtualDisplayAdapter.createVirtualDisplayLocked(callback, projection, callingUid, packageName, name, width, height, densityDpi, surface, flags, uniqueId);
            if (device == null) {
                return -1;
            }
            handleDisplayDeviceAddedLocked(device);
            LogicalDisplay display = findLogicalDisplayForDeviceLocked(device);
            if (display != null) {
                int displayIdLocked = display.getDisplayIdLocked();
                return displayIdLocked;
            }
            Slog.w(TAG, "Rejecting request to create virtual display because the logical display was not created.");
            this.mVirtualDisplayAdapter.releaseVirtualDisplayLocked(callback.asBinder());
            handleDisplayDeviceRemovedLocked(device);
            return -1;
        }
    }

    /* access modifiers changed from: private */
    public void resizeVirtualDisplayInternal(IBinder appToken, int width, int height, int densityDpi) {
        synchronized (this.mSyncRoot) {
            if (this.mVirtualDisplayAdapter != null) {
                this.mVirtualDisplayAdapter.resizeVirtualDisplayLocked(appToken, width, height, densityDpi);
            }
        }
    }

    /* access modifiers changed from: private */
    public void setVirtualDisplaySurfaceInternal(IBinder appToken, Surface surface) {
        synchronized (this.mSyncRoot) {
            if (this.mVirtualDisplayAdapter != null) {
                this.mVirtualDisplayAdapter.setVirtualDisplaySurfaceLocked(appToken, surface);
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0015, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void releaseVirtualDisplayInternal(android.os.IBinder r3) {
        /*
            r2 = this;
            com.android.server.display.DisplayManagerService$SyncRoot r0 = r2.mSyncRoot
            monitor-enter(r0)
            com.android.server.display.VirtualDisplayAdapter r1 = r2.mVirtualDisplayAdapter     // Catch:{ all -> 0x0016 }
            if (r1 != 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x0016 }
            return
        L_0x0009:
            com.android.server.display.VirtualDisplayAdapter r1 = r2.mVirtualDisplayAdapter     // Catch:{ all -> 0x0016 }
            com.android.server.display.DisplayDevice r1 = r1.releaseVirtualDisplayLocked(r3)     // Catch:{ all -> 0x0016 }
            if (r1 == 0) goto L_0x0014
            r2.handleDisplayDeviceRemovedLocked(r1)     // Catch:{ all -> 0x0016 }
        L_0x0014:
            monitor-exit(r0)     // Catch:{ all -> 0x0016 }
            return
        L_0x0016:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0016 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.DisplayManagerService.releaseVirtualDisplayInternal(android.os.IBinder):void");
    }

    /* access modifiers changed from: private */
    public void setVirtualDisplayStateInternal(IBinder appToken, boolean isOn) {
        synchronized (this.mSyncRoot) {
            if (this.mVirtualDisplayAdapter != null) {
                this.mVirtualDisplayAdapter.setVirtualDisplayStateLocked(appToken, isOn);
            }
        }
    }

    /* access modifiers changed from: private */
    public void registerDefaultDisplayAdapters() {
        synchronized (this.mSyncRoot) {
            registerDisplayAdapterLocked(new LocalDisplayAdapter(this.mSyncRoot, this.mContext, this.mHandler, this.mDisplayAdapterListener));
            this.mVirtualDisplayAdapter = this.mInjector.getVirtualDisplayAdapter(this.mSyncRoot, this.mContext, this.mHandler, this.mDisplayAdapterListener);
            if (this.mVirtualDisplayAdapter != null) {
                registerDisplayAdapterLocked(this.mVirtualDisplayAdapter);
            }
        }
    }

    /* access modifiers changed from: private */
    public void registerAdditionalDisplayAdapters() {
        synchronized (this.mSyncRoot) {
            if (shouldRegisterNonEssentialDisplayAdaptersLocked()) {
                registerOverlayDisplayAdapterLocked();
                registerWifiDisplayAdapterLocked();
            }
        }
    }

    private void registerOverlayDisplayAdapterLocked() {
        registerDisplayAdapterLocked(new OverlayDisplayAdapter(this.mSyncRoot, this.mContext, this.mHandler, this.mDisplayAdapterListener, this.mUiHandler));
    }

    private void registerWifiDisplayAdapterLocked() {
        if (this.mContext.getResources().getBoolean(17891454) || SystemProperties.getInt(FORCE_WIFI_DISPLAY_ENABLE, -1) == 1) {
            this.mWifiDisplayAdapter = new WifiDisplayAdapter(this.mSyncRoot, this.mContext, this.mHandler, this.mDisplayAdapterListener, this.mPersistentDataStore);
            registerDisplayAdapterLocked(this.mWifiDisplayAdapter);
        }
    }

    private boolean shouldRegisterNonEssentialDisplayAdaptersLocked() {
        return !this.mSafeMode && !this.mOnlyCore;
    }

    private void registerDisplayAdapterLocked(DisplayAdapter adapter) {
        this.mDisplayAdapters.add(adapter);
        adapter.registerLocked();
    }

    /* access modifiers changed from: private */
    public void handleDisplayDeviceAdded(DisplayDevice device) {
        synchronized (this.mSyncRoot) {
            handleDisplayDeviceAddedLocked(device);
        }
    }

    private void handleDisplayDeviceAddedLocked(DisplayDevice device) {
        DisplayDeviceInfo info = device.getDisplayDeviceInfoLocked();
        if (this.mDisplayDevices.contains(device)) {
            Slog.w(TAG, "Attempted to add already added display device: " + info);
            return;
        }
        Slog.i(TAG, "Display device added: " + info);
        device.mDebugLastLoggedDeviceInfo = info;
        this.mDisplayDevices.add(device);
        LogicalDisplay addLogicalDisplayLocked = addLogicalDisplayLocked(device);
        Runnable work = updateDisplayStateLocked(device);
        if (work != null) {
            work.run();
        }
        scheduleTraversalLocked(false);
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0097, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleDisplayDeviceChanged(com.android.server.display.DisplayDevice r7) {
        /*
            r6 = this;
            com.android.server.display.DisplayManagerService$SyncRoot r0 = r6.mSyncRoot
            monitor-enter(r0)
            com.android.server.display.DisplayDeviceInfo r1 = r7.getDisplayDeviceInfoLocked()     // Catch:{ all -> 0x0098 }
            java.util.ArrayList<com.android.server.display.DisplayDevice> r2 = r6.mDisplayDevices     // Catch:{ all -> 0x0098 }
            boolean r2 = r2.contains(r7)     // Catch:{ all -> 0x0098 }
            if (r2 != 0) goto L_0x0027
            java.lang.String r2 = "DisplayManagerService"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0098 }
            r3.<init>()     // Catch:{ all -> 0x0098 }
            java.lang.String r4 = "Attempted to change non-existent display device: "
            r3.append(r4)     // Catch:{ all -> 0x0098 }
            r3.append(r1)     // Catch:{ all -> 0x0098 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0098 }
            android.util.Slog.w(r2, r3)     // Catch:{ all -> 0x0098 }
            monitor-exit(r0)     // Catch:{ all -> 0x0098 }
            return
        L_0x0027:
            com.android.server.display.DisplayDeviceInfo r2 = r7.mDebugLastLoggedDeviceInfo     // Catch:{ all -> 0x0098 }
            int r2 = r2.diff(r1)     // Catch:{ all -> 0x0098 }
            r3 = 1
            if (r2 != r3) goto L_0x0057
            java.lang.String r3 = "DisplayManagerService"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0098 }
            r4.<init>()     // Catch:{ all -> 0x0098 }
            java.lang.String r5 = "Display device changed state: \""
            r4.append(r5)     // Catch:{ all -> 0x0098 }
            java.lang.String r5 = r1.name     // Catch:{ all -> 0x0098 }
            r4.append(r5)     // Catch:{ all -> 0x0098 }
            java.lang.String r5 = "\", "
            r4.append(r5)     // Catch:{ all -> 0x0098 }
            int r5 = r1.state     // Catch:{ all -> 0x0098 }
            java.lang.String r5 = android.view.Display.stateToString(r5)     // Catch:{ all -> 0x0098 }
            r4.append(r5)     // Catch:{ all -> 0x0098 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0098 }
            android.util.Slog.i(r3, r4)     // Catch:{ all -> 0x0098 }
            goto L_0x006f
        L_0x0057:
            if (r2 == 0) goto L_0x006f
            java.lang.String r3 = "DisplayManagerService"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0098 }
            r4.<init>()     // Catch:{ all -> 0x0098 }
            java.lang.String r5 = "Display device changed: "
            r4.append(r5)     // Catch:{ all -> 0x0098 }
            r4.append(r1)     // Catch:{ all -> 0x0098 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0098 }
            android.util.Slog.i(r3, r4)     // Catch:{ all -> 0x0098 }
        L_0x006f:
            r3 = r2 & 4
            if (r3 == 0) goto L_0x0087
            com.android.server.display.PersistentDataStore r3 = r6.mPersistentDataStore     // Catch:{ all -> 0x0080 }
            int r4 = r1.colorMode     // Catch:{ all -> 0x0080 }
            r3.setColorMode(r7, r4)     // Catch:{ all -> 0x0080 }
            com.android.server.display.PersistentDataStore r3 = r6.mPersistentDataStore     // Catch:{ all -> 0x0098 }
            r3.saveIfNeeded()     // Catch:{ all -> 0x0098 }
            goto L_0x0087
        L_0x0080:
            r3 = move-exception
            com.android.server.display.PersistentDataStore r4 = r6.mPersistentDataStore     // Catch:{ all -> 0x0098 }
            r4.saveIfNeeded()     // Catch:{ all -> 0x0098 }
            throw r3     // Catch:{ all -> 0x0098 }
        L_0x0087:
            r7.mDebugLastLoggedDeviceInfo = r1     // Catch:{ all -> 0x0098 }
            r7.applyPendingDisplayDeviceInfoChangesLocked()     // Catch:{ all -> 0x0098 }
            boolean r3 = r6.updateLogicalDisplaysLocked()     // Catch:{ all -> 0x0098 }
            if (r3 == 0) goto L_0x0096
            r3 = 0
            r6.scheduleTraversalLocked(r3)     // Catch:{ all -> 0x0098 }
        L_0x0096:
            monitor-exit(r0)     // Catch:{ all -> 0x0098 }
            return
        L_0x0098:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0098 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.DisplayManagerService.handleDisplayDeviceChanged(com.android.server.display.DisplayDevice):void");
    }

    /* access modifiers changed from: private */
    public void handleDisplayDeviceRemoved(DisplayDevice device) {
        synchronized (this.mSyncRoot) {
            handleDisplayDeviceRemovedLocked(device);
        }
    }

    private void handleDisplayDeviceRemovedLocked(DisplayDevice device) {
        DisplayDeviceInfo info = device.getDisplayDeviceInfoLocked();
        if (!this.mDisplayDevices.remove(device)) {
            Slog.w(TAG, "Attempted to remove non-existent display device: " + info);
            return;
        }
        Slog.i(TAG, "Display device removed: " + info);
        device.mDebugLastLoggedDeviceInfo = info;
        updateLogicalDisplaysLocked();
        scheduleTraversalLocked(false);
    }

    private void handleLogicalDisplayChanged(int displayId, LogicalDisplay display) {
        if (displayId == 0) {
            recordTopInsetLocked(display);
        }
        sendDisplayEventLocked(displayId, 2);
    }

    private void applyGlobalDisplayStateLocked(List<Runnable> workQueue) {
        int count = this.mDisplayDevices.size();
        for (int i = 0; i < count; i++) {
            Runnable runnable = updateDisplayStateLocked(this.mDisplayDevices.get(i));
            if (runnable != null) {
                workQueue.add(runnable);
            }
        }
    }

    private Runnable updateDisplayStateLocked(DisplayDevice device) {
        if ((device.getDisplayDeviceInfoLocked().flags & 32) == 0) {
            return device.requestDisplayStateLocked(this.mGlobalDisplayState, this.mGlobalDisplayBrightness);
        }
        return null;
    }

    private LogicalDisplay addLogicalDisplayLocked(DisplayDevice device) {
        DisplayDeviceInfo deviceInfo = device.getDisplayDeviceInfoLocked();
        boolean isDefault = (deviceInfo.flags & 1) != 0;
        if (isDefault && this.mLogicalDisplays.get(0) != null) {
            Slog.w(TAG, "Ignoring attempt to add a second default display: " + deviceInfo);
            isDefault = false;
        }
        if (isDefault || !this.mSingleDisplayDemoMode) {
            int displayId = assignDisplayIdLocked(isDefault, deviceInfo.address);
            LogicalDisplay display = new LogicalDisplay(displayId, assignLayerStackLocked(displayId), device);
            display.updateLocked(this.mDisplayDevices);
            if (!display.isValidLocked()) {
                Slog.w(TAG, "Ignoring display device because the logical display created from it was not considered valid: " + deviceInfo);
                return null;
            }
            configureColorModeLocked(display, device);
            if (isDefault) {
                recordStableDisplayStatsIfNeededLocked(display);
                recordTopInsetLocked(display);
            }
            this.mLogicalDisplays.put(displayId, display);
            if (isDefault) {
                this.mSyncRoot.notifyAll();
            }
            sendDisplayEventLocked(displayId, 1);
            return display;
        }
        Slog.i(TAG, "Not creating a logical display for a secondary display  because single display demo mode is enabled: " + deviceInfo);
        return null;
    }

    private int assignDisplayIdLocked(boolean isDefault) {
        if (isDefault) {
            return 0;
        }
        int i = this.mNextNonDefaultDisplayId;
        this.mNextNonDefaultDisplayId = i + 1;
        return i;
    }

    private int assignDisplayIdLocked(boolean isDefault, DisplayAddress address) {
        boolean isDisplayBuiltIn = false;
        if (address instanceof DisplayAddress.Physical) {
            isDisplayBuiltIn = ((DisplayAddress.Physical) address).getPort() < 0;
        }
        if (isDefault || !isDisplayBuiltIn) {
            return assignDisplayIdLocked(isDefault);
        }
        int i = this.mNextBuiltInDisplayId;
        this.mNextBuiltInDisplayId = i + 1;
        return i;
    }

    private int assignLayerStackLocked(int displayId) {
        return displayId;
    }

    private void configureColorModeLocked(LogicalDisplay display, DisplayDevice device) {
        if (display.getPrimaryDisplayDeviceLocked() == device) {
            int colorMode = this.mPersistentDataStore.getColorMode(device);
            if (colorMode == -1) {
                if ((device.getDisplayDeviceInfoLocked().flags & 1) != 0) {
                    colorMode = this.mDefaultDisplayDefaultColorMode;
                } else {
                    colorMode = 0;
                }
            }
            display.setRequestedColorModeLocked(colorMode);
        }
    }

    private void recordStableDisplayStatsIfNeededLocked(LogicalDisplay d) {
        if (this.mStableDisplaySize.x <= 0 && this.mStableDisplaySize.y <= 0) {
            DisplayInfo info = d.getDisplayInfoLocked();
            setStableDisplaySizeLocked(info.getNaturalWidth(), info.getNaturalHeight());
        }
    }

    private void recordTopInsetLocked(LogicalDisplay d) {
        int topInset;
        if (this.mSystemReady && d != null && (topInset = d.getInsets().top) != this.mDefaultDisplayTopInset) {
            this.mDefaultDisplayTopInset = topInset;
            SystemProperties.set(PROP_DEFAULT_DISPLAY_TOP_INSET, Integer.toString(topInset));
        }
    }

    private void setStableDisplaySizeLocked(int width, int height) {
        this.mStableDisplaySize = new Point(width, height);
        try {
            this.mPersistentDataStore.setStableDisplaySize(this.mStableDisplaySize);
        } finally {
            this.mPersistentDataStore.saveIfNeeded();
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public Curve getMinimumBrightnessCurveInternal() {
        return this.mMinimumBrightnessCurve;
    }

    /* access modifiers changed from: package-private */
    public int getPreferredWideGamutColorSpaceIdInternal() {
        return this.mWideColorSpace.getId();
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: private */
    public void setBrightnessConfigurationForUserInternal(BrightnessConfiguration c, int userId, String packageName) {
        validateBrightnessConfiguration(c);
        int userSerial = getUserManager().getUserSerialNumber(userId);
        synchronized (this.mSyncRoot) {
            try {
                this.mPersistentDataStore.setBrightnessConfigurationForUser(c, userSerial, packageName);
                this.mPersistentDataStore.saveIfNeeded();
                if (userId == this.mCurrentUserId) {
                    this.mDisplayPowerController.setBrightnessConfiguration(c);
                }
            } catch (Throwable th) {
                this.mPersistentDataStore.saveIfNeeded();
                throw th;
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void validateBrightnessConfiguration(BrightnessConfiguration config) {
        if (config != null && isBrightnessConfigurationTooDark(config)) {
            throw new IllegalArgumentException("brightness curve is too dark");
        }
    }

    private boolean isBrightnessConfigurationTooDark(BrightnessConfiguration config) {
        Pair<float[], float[]> curve = config.getCurve();
        float[] lux = (float[]) curve.first;
        float[] nits = (float[]) curve.second;
        for (int i = 0; i < lux.length; i++) {
            if (nits[i] < this.mMinimumBrightnessSpline.interpolate(lux[i])) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void loadBrightnessConfiguration() {
        synchronized (this.mSyncRoot) {
            this.mDisplayPowerController.setBrightnessConfiguration(this.mPersistentDataStore.getBrightnessConfiguration(getUserManager().getUserSerialNumber(this.mCurrentUserId)));
        }
    }

    private boolean updateLogicalDisplaysLocked() {
        boolean changed = false;
        int displayId = this.mLogicalDisplays.size();
        while (true) {
            int i = displayId - 1;
            if (displayId <= 0) {
                return changed;
            }
            int displayId2 = this.mLogicalDisplays.keyAt(i);
            LogicalDisplay display = this.mLogicalDisplays.valueAt(i);
            display.getNonOverrideDisplayInfoLocked(this.mTempDisplayInfo);
            DisplayCutout displayCutout = this.mTempDisplayInfo.displayCutout;
            this.mTempDisplayInfo.copyFrom(display.getDisplayInfoLocked());
            display.updateLocked(this.mDisplayDevices);
            if (!display.isValidLocked()) {
                this.mLogicalDisplays.removeAt(i);
                sendDisplayEventLocked(displayId2, 3);
                changed = true;
            } else if (!this.mTempDisplayInfo.equals(display.getDisplayInfoLocked())) {
                handleLogicalDisplayChanged(displayId2, display);
                changed = true;
            } else {
                display.getNonOverrideDisplayInfoLocked(this.mTempDisplayInfo);
                if (!Objects.equals(displayCutout, this.mTempDisplayInfo.displayCutout)) {
                    sendDisplayEventLocked(displayId2, 2);
                    changed = true;
                    Slog.d(TAG, "displayCutout changed");
                }
            }
            displayId = i;
        }
    }

    private void performTraversalLocked(SurfaceControl.Transaction t) {
        clearViewportsLocked();
        int count = this.mDisplayDevices.size();
        for (int i = 0; i < count; i++) {
            DisplayDevice device = this.mDisplayDevices.get(i);
            WindowManagerInternal windowManagerInternal = this.mWindowManagerInternal;
            if (windowManagerInternal == null || !windowManagerInternal.getCastRotationMode() || device.getDisplayDeviceInfoLocked().type == 1) {
                configureDisplayLocked(t, device);
                device.performTraversalLocked(t);
            }
        }
        if (this.mInputManagerInternal != null) {
            this.mHandler.sendEmptyMessage(5);
        }
    }

    /* access modifiers changed from: private */
    public void setDisplayPropertiesInternal(int displayId, boolean hasContent, float requestedRefreshRate, int requestedModeId, boolean inTraversal) {
        synchronized (this.mSyncRoot) {
            LogicalDisplay display = this.mLogicalDisplays.get(displayId);
            if (display != null) {
                if (display.hasContentLocked() != hasContent) {
                    display.setHasContentLocked(hasContent);
                    scheduleTraversalLocked(inTraversal);
                }
                if (requestedModeId == 0 && requestedRefreshRate != 0.0f) {
                    requestedModeId = display.getDisplayInfoLocked().findDefaultModeByRefreshRate(requestedRefreshRate);
                }
                this.mDisplayModeDirector.getAppRequestObserver().setAppRequestedMode(displayId, requestedModeId);
            }
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0023, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setDisplayOffsetsInternal(int r4, int r5, int r6) {
        /*
            r3 = this;
            com.android.server.display.DisplayManagerService$SyncRoot r0 = r3.mSyncRoot
            monitor-enter(r0)
            android.util.SparseArray<com.android.server.display.LogicalDisplay> r1 = r3.mLogicalDisplays     // Catch:{ all -> 0x0024 }
            java.lang.Object r1 = r1.get(r4)     // Catch:{ all -> 0x0024 }
            com.android.server.display.LogicalDisplay r1 = (com.android.server.display.LogicalDisplay) r1     // Catch:{ all -> 0x0024 }
            if (r1 != 0) goto L_0x000f
            monitor-exit(r0)     // Catch:{ all -> 0x0024 }
            return
        L_0x000f:
            int r2 = r1.getDisplayOffsetXLocked()     // Catch:{ all -> 0x0024 }
            if (r2 != r5) goto L_0x001b
            int r2 = r1.getDisplayOffsetYLocked()     // Catch:{ all -> 0x0024 }
            if (r2 == r6) goto L_0x0022
        L_0x001b:
            r1.setDisplayOffsetsLocked(r5, r6)     // Catch:{ all -> 0x0024 }
            r2 = 0
            r3.scheduleTraversalLocked(r2)     // Catch:{ all -> 0x0024 }
        L_0x0022:
            monitor-exit(r0)     // Catch:{ all -> 0x0024 }
            return
        L_0x0024:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0024 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.DisplayManagerService.setDisplayOffsetsInternal(int, int, int):void");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001d, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setDisplayScalingDisabledInternal(int r4, boolean r5) {
        /*
            r3 = this;
            com.android.server.display.DisplayManagerService$SyncRoot r0 = r3.mSyncRoot
            monitor-enter(r0)
            android.util.SparseArray<com.android.server.display.LogicalDisplay> r1 = r3.mLogicalDisplays     // Catch:{ all -> 0x001e }
            java.lang.Object r1 = r1.get(r4)     // Catch:{ all -> 0x001e }
            com.android.server.display.LogicalDisplay r1 = (com.android.server.display.LogicalDisplay) r1     // Catch:{ all -> 0x001e }
            if (r1 != 0) goto L_0x000f
            monitor-exit(r0)     // Catch:{ all -> 0x001e }
            return
        L_0x000f:
            boolean r2 = r1.isDisplayScalingDisabled()     // Catch:{ all -> 0x001e }
            if (r2 == r5) goto L_0x001c
            r1.setDisplayScalingDisabledLocked(r5)     // Catch:{ all -> 0x001e }
            r2 = 0
            r3.scheduleTraversalLocked(r2)     // Catch:{ all -> 0x001e }
        L_0x001c:
            monitor-exit(r0)     // Catch:{ all -> 0x001e }
            return
        L_0x001e:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x001e }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.DisplayManagerService.setDisplayScalingDisabledInternal(int, boolean):void");
    }

    /* access modifiers changed from: private */
    public void setDisplayAccessUIDsInternal(SparseArray<IntArray> newDisplayAccessUIDs) {
        synchronized (this.mSyncRoot) {
            this.mDisplayAccessUIDs.clear();
            for (int i = newDisplayAccessUIDs.size() - 1; i >= 0; i--) {
                this.mDisplayAccessUIDs.append(newDisplayAccessUIDs.keyAt(i), newDisplayAccessUIDs.valueAt(i));
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean isUidPresentOnDisplayInternal(int uid, int displayId) {
        boolean z;
        synchronized (this.mSyncRoot) {
            IntArray displayUIDs = this.mDisplayAccessUIDs.get(displayId);
            z = (displayUIDs == null || displayUIDs.indexOf(uid) == -1) ? false : true;
        }
        return z;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x001a, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.os.IBinder getDisplayToken(int r5) {
        /*
            r4 = this;
            com.android.server.display.DisplayManagerService$SyncRoot r0 = r4.mSyncRoot
            monitor-enter(r0)
            android.util.SparseArray<com.android.server.display.LogicalDisplay> r1 = r4.mLogicalDisplays     // Catch:{ all -> 0x001c }
            java.lang.Object r1 = r1.get(r5)     // Catch:{ all -> 0x001c }
            com.android.server.display.LogicalDisplay r1 = (com.android.server.display.LogicalDisplay) r1     // Catch:{ all -> 0x001c }
            if (r1 == 0) goto L_0x0019
            com.android.server.display.DisplayDevice r2 = r1.getPrimaryDisplayDeviceLocked()     // Catch:{ all -> 0x001c }
            if (r2 == 0) goto L_0x0019
            android.os.IBinder r3 = r2.getDisplayTokenLocked()     // Catch:{ all -> 0x001c }
            monitor-exit(r0)     // Catch:{ all -> 0x001c }
            return r3
        L_0x0019:
            monitor-exit(r0)     // Catch:{ all -> 0x001c }
            r0 = 0
            return r0
        L_0x001c:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x001c }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.DisplayManagerService.getDisplayToken(int):android.os.IBinder");
    }

    /* access modifiers changed from: private */
    public SurfaceControl.ScreenshotGraphicBuffer screenshotInternal(int displayId) {
        IBinder token = getDisplayToken(displayId);
        if (token == null) {
            return null;
        }
        return SurfaceControl.screenshotToBufferWithSecureLayersUnsafe(token, new Rect(), 0, 0, false, 0);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public DisplayedContentSamplingAttributes getDisplayedContentSamplingAttributesInternal(int displayId) {
        IBinder token = getDisplayToken(displayId);
        if (token == null) {
            return null;
        }
        return SurfaceControl.getDisplayedContentSamplingAttributes(token);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean setDisplayedContentSamplingEnabledInternal(int displayId, boolean enable, int componentMask, int maxFrames) {
        IBinder token = getDisplayToken(displayId);
        if (token == null) {
            return false;
        }
        return SurfaceControl.setDisplayedContentSamplingEnabled(token, enable, componentMask, maxFrames);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public DisplayedContentSample getDisplayedContentSampleInternal(int displayId, long maxFrames, long timestamp) {
        IBinder token = getDisplayToken(displayId);
        if (token == null) {
            return null;
        }
        return SurfaceControl.getDisplayedContentSample(token, maxFrames, timestamp);
    }

    /* access modifiers changed from: private */
    public void onAllowedDisplayModesChangedInternal() {
        boolean changed = false;
        synchronized (this.mSyncRoot) {
            int count = this.mLogicalDisplays.size();
            for (int i = 0; i < count; i++) {
                LogicalDisplay display = this.mLogicalDisplays.valueAt(i);
                int[] allowedModes = this.mDisplayModeDirector.getAllowedModes(this.mLogicalDisplays.keyAt(i));
                if (!Arrays.equals(allowedModes, display.getAllowedDisplayModesLocked())) {
                    display.setAllowedDisplayModesLocked(allowedModes);
                    changed = true;
                }
            }
            if (changed) {
                scheduleTraversalLocked(false);
            }
        }
    }

    private void clearViewportsLocked() {
        this.mViewports.clear();
    }

    private void configureDisplayLocked(SurfaceControl.Transaction t, DisplayDevice device) {
        int viewportType;
        DisplayDeviceInfo info = device.getDisplayDeviceInfoLocked();
        boolean z = false;
        boolean ownContent = (info.flags & 128) != 0;
        LogicalDisplay display = findLogicalDisplayForDeviceLocked(device);
        if (!ownContent) {
            if (display != null && !display.hasContentLocked()) {
                display = null;
            }
            if (display == null) {
                display = this.mLogicalDisplays.get(0);
            }
        }
        if (display == null) {
            Slog.w(TAG, "Missing logical display to use for physical display device: " + device.getDisplayDeviceInfoLocked());
            return;
        }
        if (info.state == 1) {
            z = true;
        }
        display.configureDisplayLocked(t, device, z);
        if ((info.flags & 1) != 0) {
            viewportType = 1;
        } else if (info.touch == 2) {
            viewportType = 2;
        } else if (info.touch != 3 || TextUtils.isEmpty(info.uniqueId)) {
            Slog.i(TAG, "Display " + info + " does not support input device matching.");
            return;
        } else {
            viewportType = 3;
        }
        populateViewportLocked(viewportType, display.getDisplayIdLocked(), device, info.uniqueId);
    }

    private DisplayViewport getViewportLocked(int viewportType, String uniqueId) {
        if (viewportType == 1 || viewportType == 2 || viewportType == 3) {
            if (viewportType != 3) {
                uniqueId = "";
            }
            int count = this.mViewports.size();
            for (int i = 0; i < count; i++) {
                DisplayViewport viewport = this.mViewports.get(i);
                if (viewport.type == viewportType && uniqueId.equals(viewport.uniqueId)) {
                    return viewport;
                }
            }
            DisplayViewport viewport2 = new DisplayViewport();
            viewport2.type = viewportType;
            viewport2.uniqueId = uniqueId;
            this.mViewports.add(viewport2);
            return viewport2;
        }
        Slog.wtf(TAG, "Cannot call getViewportByTypeLocked for type " + DisplayViewport.typeToString(viewportType));
        return null;
    }

    private void populateViewportLocked(int viewportType, int displayId, DisplayDevice device, String uniqueId) {
        DisplayViewport viewport = getViewportLocked(viewportType, uniqueId);
        device.populateViewportLocked(viewport);
        viewport.valid = true;
        viewport.displayId = displayId;
    }

    private LogicalDisplay findLogicalDisplayForDeviceLocked(DisplayDevice device) {
        int count = this.mLogicalDisplays.size();
        for (int i = 0; i < count; i++) {
            LogicalDisplay display = this.mLogicalDisplays.valueAt(i);
            if (display.getPrimaryDisplayDeviceLocked() == device) {
                return display;
            }
        }
        return null;
    }

    private void sendDisplayEventLocked(int displayId, int event) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(3, displayId, event));
    }

    /* access modifiers changed from: private */
    public void scheduleTraversalLocked(boolean inTraversal) {
        if (!this.mPendingTraversal && this.mWindowManagerInternal != null) {
            this.mPendingTraversal = true;
            if (!inTraversal) {
                this.mHandler.sendEmptyMessage(4);
            }
        }
    }

    /* access modifiers changed from: private */
    public void deliverDisplayEvent(int displayId, int event) {
        int count;
        synchronized (this.mSyncRoot) {
            count = this.mCallbacks.size();
            this.mTempCallbacks.clear();
            for (int i = 0; i < count; i++) {
                this.mTempCallbacks.add(this.mCallbacks.valueAt(i));
            }
        }
        for (int i2 = 0; i2 < count; i2++) {
            this.mTempCallbacks.get(i2).notifyDisplayEventAsync(displayId, event);
        }
        this.mTempCallbacks.clear();
    }

    /* access modifiers changed from: private */
    public IMediaProjectionManager getProjectionService() {
        if (this.mProjectionService == null) {
            this.mProjectionService = IMediaProjectionManager.Stub.asInterface(ServiceManager.getService("media_projection"));
        }
        return this.mProjectionService;
    }

    /* access modifiers changed from: private */
    public UserManager getUserManager() {
        return (UserManager) this.mContext.getSystemService(UserManager.class);
    }

    /* access modifiers changed from: private */
    public void dumpInternal(PrintWriter pw) {
        pw.println("DISPLAY MANAGER (dumpsys display)");
        synchronized (this.mSyncRoot) {
            pw.println("  mOnlyCode=" + this.mOnlyCore);
            pw.println("  mSafeMode=" + this.mSafeMode);
            pw.println("  mPendingTraversal=" + this.mPendingTraversal);
            pw.println("  mGlobalDisplayState=" + Display.stateToString(this.mGlobalDisplayState));
            pw.println("  mNextNonDefaultDisplayId=" + this.mNextNonDefaultDisplayId);
            pw.println("  mViewports=" + this.mViewports);
            pw.println("  mDefaultDisplayDefaultColorMode=" + this.mDefaultDisplayDefaultColorMode);
            pw.println("  mSingleDisplayDemoMode=" + this.mSingleDisplayDemoMode);
            pw.println("  mWifiDisplayScanRequestCount=" + this.mWifiDisplayScanRequestCount);
            pw.println("  mStableDisplaySize=" + this.mStableDisplaySize);
            pw.println("  mMinimumBrightnessCurve=" + this.mMinimumBrightnessCurve);
            IndentingPrintWriter ipw = new IndentingPrintWriter(pw, "    ");
            ipw.increaseIndent();
            pw.println();
            pw.println("Display Adapters: size=" + this.mDisplayAdapters.size());
            Iterator<DisplayAdapter> it = this.mDisplayAdapters.iterator();
            while (it.hasNext()) {
                DisplayAdapter adapter = it.next();
                pw.println("  " + adapter.getName());
                adapter.dumpLocked(ipw);
            }
            pw.println();
            pw.println("Display Devices: size=" + this.mDisplayDevices.size());
            Iterator<DisplayDevice> it2 = this.mDisplayDevices.iterator();
            while (it2.hasNext()) {
                DisplayDevice device = it2.next();
                pw.println("  " + device.getDisplayDeviceInfoLocked());
                device.dumpLocked(ipw);
            }
            int logicalDisplayCount = this.mLogicalDisplays.size();
            pw.println();
            pw.println("Logical Displays: size=" + logicalDisplayCount);
            for (int i = 0; i < logicalDisplayCount; i++) {
                int displayId = this.mLogicalDisplays.keyAt(i);
                pw.println("  Display " + displayId + ":");
                this.mLogicalDisplays.valueAt(i).dumpLocked(ipw);
            }
            pw.println();
            this.mDisplayModeDirector.dump(pw);
            int callbackCount = this.mCallbacks.size();
            pw.println();
            pw.println("Callbacks: size=" + callbackCount);
            for (int i2 = 0; i2 < callbackCount; i2++) {
                CallbackRecord callback = this.mCallbacks.valueAt(i2);
                pw.println("  " + i2 + ": mPid=" + callback.mPid + ", mWifiDisplayScanRequested=" + callback.mWifiDisplayScanRequested);
            }
            if (this.mDisplayPowerController != null) {
                this.mDisplayPowerController.dump(pw);
            }
            pw.println();
            this.mPersistentDataStore.dump(pw);
        }
    }

    private static float[] getFloatArray(TypedArray array) {
        int length = array.length();
        float[] floatArray = new float[length];
        for (int i = 0; i < length; i++) {
            floatArray[i] = array.getFloat(i, Float.NaN);
        }
        array.recycle();
        return floatArray;
    }

    @VisibleForTesting
    static class Injector {
        Injector() {
        }

        /* access modifiers changed from: package-private */
        public VirtualDisplayAdapter getVirtualDisplayAdapter(SyncRoot syncRoot, Context context, Handler handler, DisplayAdapter.Listener displayAdapterListener) {
            return new VirtualDisplayAdapter(syncRoot, context, handler, displayAdapterListener);
        }

        /* access modifiers changed from: package-private */
        public long getDefaultDisplayDelayTimeout() {
            return 10000;
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public DisplayDeviceInfo getDisplayDeviceInfoInternal(int displayId) {
        synchronized (this.mSyncRoot) {
            LogicalDisplay display = this.mLogicalDisplays.get(displayId);
            if (display == null) {
                return null;
            }
            DisplayDeviceInfo displayDeviceInfoLocked = display.getPrimaryDisplayDeviceLocked().getDisplayDeviceInfoLocked();
            return displayDeviceInfoLocked;
        }
    }

    private final class DisplayManagerHandler extends Handler {
        public DisplayManagerHandler(Looper looper) {
            super(looper, (Handler.Callback) null, true);
        }

        public void handleMessage(Message msg) {
            boolean changed;
            switch (msg.what) {
                case 1:
                    DisplayManagerService.this.registerDefaultDisplayAdapters();
                    return;
                case 2:
                    DisplayManagerService.this.registerAdditionalDisplayAdapters();
                    return;
                case 3:
                    DisplayManagerService.this.deliverDisplayEvent(msg.arg1, msg.arg2);
                    return;
                case 4:
                    DisplayManagerService.this.mWindowManagerInternal.requestTraversalFromDisplayManager();
                    return;
                case 5:
                    synchronized (DisplayManagerService.this.mSyncRoot) {
                        changed = !DisplayManagerService.this.mTempViewports.equals(DisplayManagerService.this.mViewports);
                        if (changed) {
                            DisplayManagerService.this.mTempViewports.clear();
                            Iterator it = DisplayManagerService.this.mViewports.iterator();
                            while (it.hasNext()) {
                                DisplayManagerService.this.mTempViewports.add(((DisplayViewport) it.next()).makeCopy());
                            }
                        }
                    }
                    if (changed) {
                        DisplayManagerService.this.mInputManagerInternal.setDisplayViewports(DisplayManagerService.this.mTempViewports);
                        return;
                    }
                    return;
                case 6:
                    DisplayManagerService.this.loadBrightnessConfiguration();
                    return;
                default:
                    return;
            }
        }
    }

    private final class DisplayAdapterListener implements DisplayAdapter.Listener {
        private DisplayAdapterListener() {
        }

        public void onDisplayDeviceEvent(DisplayDevice device, int event) {
            if (event == 1) {
                DisplayManagerService.this.handleDisplayDeviceAdded(device);
            } else if (event == 2) {
                DisplayManagerService.this.handleDisplayDeviceChanged(device);
            } else if (event == 3) {
                DisplayManagerService.this.handleDisplayDeviceRemoved(device);
            }
        }

        public void onTraversalRequested() {
            synchronized (DisplayManagerService.this.mSyncRoot) {
                DisplayManagerService.this.scheduleTraversalLocked(false);
            }
        }
    }

    private final class CallbackRecord implements IBinder.DeathRecipient {
        private final IDisplayManagerCallback mCallback;
        public final int mPid;
        public boolean mWifiDisplayScanRequested;

        public CallbackRecord(int pid, IDisplayManagerCallback callback) {
            this.mPid = pid;
            this.mCallback = callback;
        }

        public void binderDied() {
            DisplayManagerService.this.onCallbackDied(this);
        }

        public void notifyDisplayEventAsync(int displayId, int event) {
            try {
                this.mCallback.onDisplayEvent(displayId, event);
            } catch (RemoteException ex) {
                Slog.w(DisplayManagerService.TAG, "Failed to notify process " + this.mPid + " that displays changed, assuming it died.", ex);
                binderDied();
            }
        }
    }

    @VisibleForTesting
    final class BinderService extends IDisplayManager.Stub {
        BinderService() {
        }

        public DisplayInfo getDisplayInfo(int displayId) {
            int callingUid = Binder.getCallingUid();
            long token = Binder.clearCallingIdentity();
            try {
                return DisplayManagerService.this.getDisplayInfoInternal(displayId, callingUid);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public int[] getDisplayIds() {
            int callingUid = Binder.getCallingUid();
            long token = Binder.clearCallingIdentity();
            try {
                return DisplayManagerService.this.getDisplayIdsInternal(callingUid);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public boolean isUidPresentOnDisplay(int uid, int displayId) {
            long token = Binder.clearCallingIdentity();
            try {
                return DisplayManagerService.this.isUidPresentOnDisplayInternal(uid, displayId);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public Point getStableDisplaySize() {
            long token = Binder.clearCallingIdentity();
            try {
                return DisplayManagerService.this.getStableDisplaySizeInternal();
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public void registerCallback(IDisplayManagerCallback callback) {
            if (callback != null) {
                int callingPid = Binder.getCallingPid();
                long token = Binder.clearCallingIdentity();
                try {
                    DisplayManagerService.this.registerCallbackInternal(callback, callingPid);
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            } else {
                throw new IllegalArgumentException("listener must not be null");
            }
        }

        public void startWifiDisplayScan() {
            DisplayManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.CONFIGURE_WIFI_DISPLAY", "Permission required to start wifi display scans");
            int callingPid = Binder.getCallingPid();
            long token = Binder.clearCallingIdentity();
            try {
                DisplayManagerService.this.startWifiDisplayScanInternal(callingPid);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public void stopWifiDisplayScan() {
            DisplayManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.CONFIGURE_WIFI_DISPLAY", "Permission required to stop wifi display scans");
            int callingPid = Binder.getCallingPid();
            long token = Binder.clearCallingIdentity();
            try {
                DisplayManagerService.this.stopWifiDisplayScanInternal(callingPid);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public void connectWifiDisplay(String address) {
            if (address != null) {
                DisplayManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.CONFIGURE_WIFI_DISPLAY", "Permission required to connect to a wifi display");
                long token = Binder.clearCallingIdentity();
                try {
                    DisplayManagerService.this.connectWifiDisplayInternal(address);
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            } else {
                throw new IllegalArgumentException("address must not be null");
            }
        }

        public void disconnectWifiDisplay() {
            long token = Binder.clearCallingIdentity();
            try {
                DisplayManagerService.this.disconnectWifiDisplayInternal();
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public void renameWifiDisplay(String address, String alias) {
            if (address != null) {
                DisplayManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.CONFIGURE_WIFI_DISPLAY", "Permission required to rename to a wifi display");
                long token = Binder.clearCallingIdentity();
                try {
                    DisplayManagerService.this.renameWifiDisplayInternal(address, alias);
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            } else {
                throw new IllegalArgumentException("address must not be null");
            }
        }

        public void forgetWifiDisplay(String address) {
            if (address != null) {
                DisplayManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.CONFIGURE_WIFI_DISPLAY", "Permission required to forget to a wifi display");
                long token = Binder.clearCallingIdentity();
                try {
                    DisplayManagerService.this.forgetWifiDisplayInternal(address);
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            } else {
                throw new IllegalArgumentException("address must not be null");
            }
        }

        public void pauseWifiDisplay() {
            DisplayManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.CONFIGURE_WIFI_DISPLAY", "Permission required to pause a wifi display session");
            long token = Binder.clearCallingIdentity();
            try {
                DisplayManagerService.this.pauseWifiDisplayInternal();
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public void resumeWifiDisplay() {
            DisplayManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.CONFIGURE_WIFI_DISPLAY", "Permission required to resume a wifi display session");
            long token = Binder.clearCallingIdentity();
            try {
                DisplayManagerService.this.resumeWifiDisplayInternal();
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public WifiDisplayStatus getWifiDisplayStatus() {
            long token = Binder.clearCallingIdentity();
            try {
                return DisplayManagerService.this.getWifiDisplayStatusInternal();
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public void requestColorMode(int displayId, int colorMode) {
            DisplayManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.CONFIGURE_DISPLAY_COLOR_MODE", "Permission required to change the display color mode");
            long token = Binder.clearCallingIdentity();
            try {
                DisplayManagerService.this.requestColorModeInternal(displayId, colorMode);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 19 */
        public int createVirtualDisplay(IVirtualDisplayCallback callback, IMediaProjection projection, String packageName, String name, int width, int height, int densityDpi, Surface surface, int flags, String uniqueId) {
            int flags2;
            int flags3;
            int flags4;
            IMediaProjection iMediaProjection = projection;
            int callingUid = Binder.getCallingUid();
            if (!validatePackageName(callingUid, packageName)) {
                throw new SecurityException("packageName must match the calling uid");
            } else if (callback == null) {
                throw new IllegalArgumentException("appToken must not be null");
            } else if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("name must be non-null and non-empty");
            } else if (width <= 0 || height <= 0 || densityDpi <= 0) {
                throw new IllegalArgumentException("width, height, and densityDpi must be greater than 0");
            } else if (surface == null || !surface.isSingleBuffered()) {
                if ((flags & 1) != 0) {
                    flags2 = flags | 16;
                    if ((flags2 & 32) != 0) {
                        throw new IllegalArgumentException("Public display must not be marked as SHOW_WHEN_LOCKED_INSECURE");
                    }
                } else {
                    flags2 = flags;
                }
                if ((flags2 & 8) != 0) {
                    flags3 = flags2 & -17;
                } else {
                    flags3 = flags2;
                }
                if (iMediaProjection != null) {
                    try {
                        if (DisplayManagerService.this.getProjectionService().isValidMediaProjection(iMediaProjection)) {
                            flags4 = iMediaProjection.applyVirtualDisplayFlags(flags3);
                        } else {
                            throw new SecurityException("Invalid media projection");
                        }
                    } catch (RemoteException e) {
                        throw new SecurityException("unable to validate media projection or flags");
                    }
                } else {
                    flags4 = flags3;
                }
                if (callingUid != 1000 && (flags4 & 16) != 0 && !canProjectVideo(iMediaProjection)) {
                    throw new SecurityException("Requires CAPTURE_VIDEO_OUTPUT or CAPTURE_SECURE_VIDEO_OUTPUT permission, or an appropriate MediaProjection token in order to create a screen sharing virtual display.");
                } else if (callingUid != 1000 && (flags4 & 4) != 0 && !canProjectSecureVideo(iMediaProjection)) {
                    throw new SecurityException("Requires CAPTURE_SECURE_VIDEO_OUTPUT or an appropriate MediaProjection token to create a secure virtual display.");
                } else if (callingUid == 1000 || (flags4 & 512) == 0 || checkCallingPermission("android.permission.INTERNAL_SYSTEM_WINDOW", "createVirtualDisplay()")) {
                    long token = Binder.clearCallingIdentity();
                    try {
                        int i = flags4;
                        try {
                            int access$3300 = DisplayManagerService.this.createVirtualDisplayInternal(callback, projection, callingUid, packageName, name, width, height, densityDpi, surface, flags4, uniqueId);
                            Binder.restoreCallingIdentity(token);
                            return access$3300;
                        } catch (Throwable th) {
                            th = th;
                            Binder.restoreCallingIdentity(token);
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        int i2 = flags4;
                        Binder.restoreCallingIdentity(token);
                        throw th;
                    }
                } else {
                    throw new SecurityException("Requires INTERNAL_SYSTEM_WINDOW permission");
                }
            } else {
                throw new IllegalArgumentException("Surface can't be single-buffered");
            }
        }

        public void resizeVirtualDisplay(IVirtualDisplayCallback callback, int width, int height, int densityDpi) {
            long token = Binder.clearCallingIdentity();
            try {
                DisplayManagerService.this.resizeVirtualDisplayInternal(callback.asBinder(), width, height, densityDpi);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public void setVirtualDisplaySurface(IVirtualDisplayCallback callback, Surface surface) {
            if (surface == null || !surface.isSingleBuffered()) {
                long token = Binder.clearCallingIdentity();
                try {
                    DisplayManagerService.this.setVirtualDisplaySurfaceInternal(callback.asBinder(), surface);
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            } else {
                throw new IllegalArgumentException("Surface can't be single-buffered");
            }
        }

        public void releaseVirtualDisplay(IVirtualDisplayCallback callback) {
            long token = Binder.clearCallingIdentity();
            try {
                DisplayManagerService.this.releaseVirtualDisplayInternal(callback.asBinder());
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public void setVirtualDisplayState(IVirtualDisplayCallback callback, boolean isOn) {
            long token = Binder.clearCallingIdentity();
            try {
                DisplayManagerService.this.setVirtualDisplayStateInternal(callback.asBinder(), isOn);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            if (DumpUtils.checkDumpPermission(DisplayManagerService.this.mContext, DisplayManagerService.TAG, pw)) {
                long token = Binder.clearCallingIdentity();
                try {
                    DisplayManagerService.this.dumpInternal(pw);
                } finally {
                    Binder.restoreCallingIdentity(token);
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 9 */
        public ParceledListSlice<BrightnessChangeEvent> getBrightnessEvents(String callingPackage) {
            ParceledListSlice<BrightnessChangeEvent> brightnessEvents;
            DisplayManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.BRIGHTNESS_SLIDER_USAGE", "Permission to read brightness events.");
            int callingUid = Binder.getCallingUid();
            int mode = ((AppOpsManager) DisplayManagerService.this.mContext.getSystemService(AppOpsManager.class)).noteOp(43, callingUid, callingPackage);
            boolean hasUsageStats = true;
            if (mode == 3) {
                if (DisplayManagerService.this.mContext.checkCallingPermission("android.permission.PACKAGE_USAGE_STATS") != 0) {
                    hasUsageStats = false;
                }
            } else if (mode != 0) {
                hasUsageStats = false;
            }
            int userId = UserHandle.getUserId(callingUid);
            long token = Binder.clearCallingIdentity();
            try {
                synchronized (DisplayManagerService.this.mSyncRoot) {
                    brightnessEvents = DisplayManagerService.this.mDisplayPowerController.getBrightnessEvents(userId, hasUsageStats);
                }
                Binder.restoreCallingIdentity(token);
                return brightnessEvents;
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(token);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 6 */
        public ParceledListSlice<AmbientBrightnessDayStats> getAmbientBrightnessStats() {
            ParceledListSlice<AmbientBrightnessDayStats> ambientBrightnessStats;
            DisplayManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.ACCESS_AMBIENT_LIGHT_STATS", "Permission required to to access ambient light stats.");
            int userId = UserHandle.getUserId(Binder.getCallingUid());
            long token = Binder.clearCallingIdentity();
            try {
                synchronized (DisplayManagerService.this.mSyncRoot) {
                    ambientBrightnessStats = DisplayManagerService.this.mDisplayPowerController.getAmbientBrightnessStats(userId);
                }
                Binder.restoreCallingIdentity(token);
                return ambientBrightnessStats;
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(token);
                throw th;
            }
        }

        public void setBrightnessConfigurationForUser(BrightnessConfiguration c, int userId, String packageName) {
            DisplayManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.CONFIGURE_DISPLAY_BRIGHTNESS", "Permission required to change the display's brightness configuration");
            if (userId != UserHandle.getCallingUserId()) {
                DisplayManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS", "Permission required to change the display brightness configuration of another user");
            }
            if (packageName != null && !validatePackageName(getCallingUid(), packageName)) {
                packageName = null;
            }
            long token = Binder.clearCallingIdentity();
            try {
                DisplayManagerService.this.setBrightnessConfigurationForUserInternal(c, userId, packageName);
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 6 */
        public BrightnessConfiguration getBrightnessConfigurationForUser(int userId) {
            BrightnessConfiguration config;
            DisplayManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.CONFIGURE_DISPLAY_BRIGHTNESS", "Permission required to read the display's brightness configuration");
            if (userId != UserHandle.getCallingUserId()) {
                DisplayManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS", "Permission required to read the display brightness configuration of another user");
            }
            long token = Binder.clearCallingIdentity();
            try {
                int userSerial = DisplayManagerService.this.getUserManager().getUserSerialNumber(userId);
                synchronized (DisplayManagerService.this.mSyncRoot) {
                    config = DisplayManagerService.this.mPersistentDataStore.getBrightnessConfiguration(userSerial);
                    if (config == null) {
                        config = DisplayManagerService.this.mDisplayPowerController.getDefaultBrightnessConfiguration();
                    }
                }
                Binder.restoreCallingIdentity(token);
                return config;
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(token);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 4 */
        public BrightnessConfiguration getDefaultBrightnessConfiguration() {
            BrightnessConfiguration defaultBrightnessConfiguration;
            DisplayManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.CONFIGURE_DISPLAY_BRIGHTNESS", "Permission required to read the display's default brightness configuration");
            long token = Binder.clearCallingIdentity();
            try {
                synchronized (DisplayManagerService.this.mSyncRoot) {
                    defaultBrightnessConfiguration = DisplayManagerService.this.mDisplayPowerController.getDefaultBrightnessConfiguration();
                }
                Binder.restoreCallingIdentity(token);
                return defaultBrightnessConfiguration;
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(token);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 4 */
        public void setTemporaryBrightness(int brightness) {
            DisplayManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.CONTROL_DISPLAY_BRIGHTNESS", "Permission required to set the display's brightness");
            long token = Binder.clearCallingIdentity();
            try {
                if (BackLightController.setNightLight(brightness)) {
                    Binder.restoreCallingIdentity(token);
                    return;
                }
                synchronized (DisplayManagerService.this.mSyncRoot) {
                    DisplayManagerService.this.mDisplayPowerController.setTemporaryBrightness(brightness);
                }
                Binder.restoreCallingIdentity(token);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(token);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 4 */
        public void setTemporaryAutoBrightnessAdjustment(float adjustment) {
            DisplayManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.CONTROL_DISPLAY_BRIGHTNESS", "Permission required to set the display's auto brightness adjustment");
            long token = Binder.clearCallingIdentity();
            try {
                synchronized (DisplayManagerService.this.mSyncRoot) {
                    DisplayManagerService.this.mDisplayPowerController.setTemporaryAutoBrightnessAdjustment(adjustment);
                }
                Binder.restoreCallingIdentity(token);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(token);
                throw th;
            }
        }

        /* JADX WARNING: type inference failed for: r4v0, types: [android.os.Binder] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onShellCommand(java.io.FileDescriptor r13, java.io.FileDescriptor r14, java.io.FileDescriptor r15, java.lang.String[] r16, android.os.ShellCallback r17, android.os.ResultReceiver r18) {
            /*
                r12 = this;
                long r1 = android.os.Binder.clearCallingIdentity()
                com.android.server.display.DisplayManagerShellCommand r3 = new com.android.server.display.DisplayManagerShellCommand     // Catch:{ all -> 0x001f }
                r11 = r12
                r3.<init>(r12)     // Catch:{ all -> 0x001d }
                r4 = r12
                r5 = r13
                r6 = r14
                r7 = r15
                r8 = r16
                r9 = r17
                r10 = r18
                r3.exec(r4, r5, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x001d }
                android.os.Binder.restoreCallingIdentity(r1)
                return
            L_0x001d:
                r0 = move-exception
                goto L_0x0021
            L_0x001f:
                r0 = move-exception
                r11 = r12
            L_0x0021:
                android.os.Binder.restoreCallingIdentity(r1)
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.DisplayManagerService.BinderService.onShellCommand(java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):void");
        }

        public Curve getMinimumBrightnessCurve() {
            long token = Binder.clearCallingIdentity();
            try {
                return DisplayManagerService.this.getMinimumBrightnessCurveInternal();
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        public int getPreferredWideGamutColorSpaceId() {
            long token = Binder.clearCallingIdentity();
            try {
                return DisplayManagerService.this.getPreferredWideGamutColorSpaceIdInternal();
            } finally {
                Binder.restoreCallingIdentity(token);
            }
        }

        /* access modifiers changed from: package-private */
        public void setBrightness(int brightness) {
            Settings.System.putIntForUser(DisplayManagerService.this.mContext.getContentResolver(), "screen_brightness", brightness, -2);
        }

        /* access modifiers changed from: package-private */
        public void resetBrightnessConfiguration() {
            DisplayManagerService displayManagerService = DisplayManagerService.this;
            displayManagerService.setBrightnessConfigurationForUserInternal((BrightnessConfiguration) null, displayManagerService.mContext.getUserId(), DisplayManagerService.this.mContext.getPackageName());
        }

        /* access modifiers changed from: package-private */
        public void setAutoBrightnessLoggingEnabled(boolean enabled) {
            if (DisplayManagerService.this.mDisplayPowerController != null) {
                synchronized (DisplayManagerService.this.mSyncRoot) {
                    DisplayManagerService.this.mDisplayPowerController.setAutoBrightnessLoggingEnabled(enabled);
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void setDisplayWhiteBalanceLoggingEnabled(boolean enabled) {
            if (DisplayManagerService.this.mDisplayPowerController != null) {
                synchronized (DisplayManagerService.this.mSyncRoot) {
                    DisplayManagerService.this.mDisplayPowerController.setDisplayWhiteBalanceLoggingEnabled(enabled);
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void setAmbientColorTemperatureOverride(float cct) {
            if (DisplayManagerService.this.mDisplayPowerController != null) {
                synchronized (DisplayManagerService.this.mSyncRoot) {
                    DisplayManagerService.this.mDisplayPowerController.setAmbientColorTemperatureOverride(cct);
                }
            }
        }

        private boolean validatePackageName(int uid, String packageName) {
            String[] packageNames;
            if (!(packageName == null || (packageNames = DisplayManagerService.this.mContext.getPackageManager().getPackagesForUid(uid)) == null)) {
                for (String n : packageNames) {
                    if (n.equals(packageName)) {
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean canProjectVideo(IMediaProjection projection) {
            if (projection != null) {
                try {
                    if (projection.canProjectVideo()) {
                        return true;
                    }
                } catch (RemoteException e) {
                    Slog.e(DisplayManagerService.TAG, "Unable to query projection service for permissions", e);
                }
            }
            if (checkCallingPermission("android.permission.CAPTURE_VIDEO_OUTPUT", "canProjectVideo()")) {
                return true;
            }
            return canProjectSecureVideo(projection);
        }

        private boolean canProjectSecureVideo(IMediaProjection projection) {
            if (projection != null) {
                try {
                    if (projection.canProjectSecureVideo()) {
                        return true;
                    }
                } catch (RemoteException e) {
                    Slog.e(DisplayManagerService.TAG, "Unable to query projection service for permissions", e);
                }
            }
            return checkCallingPermission("android.permission.CAPTURE_SECURE_VIDEO_OUTPUT", "canProjectSecureVideo()");
        }

        private boolean checkCallingPermission(String permission, String func) {
            if (DisplayManagerService.this.mContext.checkCallingPermission(permission) == 0) {
                return true;
            }
            Slog.w(DisplayManagerService.TAG, "Permission Denial: " + func + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + permission);
            return false;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            try {
                DisplayManagerService displayManagerService = DisplayManagerService.this;
                DisplayPowerController access$3900 = DisplayManagerService.this.mDisplayPowerController;
                DisplayPowerState displayPowerState = null;
                Handler displayControllerHandler = DisplayManagerService.this.mDisplayPowerController != null ? DisplayManagerService.this.mDisplayPowerController.getDisplayControllerHandler() : null;
                if (DisplayManagerService.this.mDisplayPowerController != null) {
                    displayPowerState = DisplayManagerService.this.mDisplayPowerController.getDisplayPowerState();
                }
                if (DisplayManagerServiceInjector.onTransact(displayManagerService, access$3900, displayControllerHandler, displayPowerState, code, data, reply, flags)) {
                    return true;
                }
                return DisplayManagerService.super.onTransact(code, data, reply, flags);
            } catch (RuntimeException e) {
                if (!(e instanceof SecurityException)) {
                    Slog.wtf(DisplayManagerService.TAG, "Display Manager Crash", e);
                }
                throw e;
            }
        }
    }

    private final class LocalService extends DisplayManagerInternal {
        private LocalService() {
        }

        public void initPowerManagement(final DisplayManagerInternal.DisplayPowerCallbacks callbacks, Handler handler, SensorManager sensorManager) {
            synchronized (DisplayManagerService.this.mSyncRoot) {
                DisplayPowerController unused = DisplayManagerService.this.mDisplayPowerController = new DisplayPowerController(DisplayManagerService.this.mContext, callbacks, handler, sensorManager, new DisplayBlanker() {
                    public void requestDisplayState(int state, int brightness) {
                        if (state == 1) {
                            DisplayManagerService.this.requestGlobalDisplayStateInternal(state, brightness);
                        }
                        callbacks.onDisplayStateChange(state);
                        if (state != 1) {
                            DisplayManagerService.this.requestGlobalDisplayStateInternal(state, brightness);
                        }
                    }
                });
            }
            DisplayManagerService.this.mHandler.sendEmptyMessage(6);
        }

        public boolean requestPowerState(DisplayManagerInternal.DisplayPowerRequest request, boolean waitForNegativeProximity) {
            boolean requestPowerState;
            synchronized (DisplayManagerService.this.mSyncRoot) {
                requestPowerState = DisplayManagerService.this.mDisplayPowerController.requestPowerState(request, waitForNegativeProximity);
            }
            return requestPowerState;
        }

        public void switchUser(int newUserId) {
            DisplayManagerService.this.switchUserInternal(newUserId);
        }

        public boolean isProximitySensorAvailable() {
            boolean isProximitySensorAvailable;
            synchronized (DisplayManagerService.this.mSyncRoot) {
                isProximitySensorAvailable = DisplayManagerService.this.mDisplayPowerController.isProximitySensorAvailable();
            }
            return isProximitySensorAvailable;
        }

        public void updateColorFadeOffAnimatorDuration(boolean isTouchWakeUp) {
            DisplayManagerService.this.mDisplayPowerController.mColorFadeOffAnimator.setDuration(isTouchWakeUp ? 0 : 300);
        }

        public SurfaceControl.ScreenshotGraphicBuffer screenshot(int displayId) {
            return DisplayManagerService.this.screenshotInternal(displayId);
        }

        public DisplayInfo getDisplayInfo(int displayId) {
            return DisplayManagerService.this.getDisplayInfoInternal(displayId, Process.myUid());
        }

        public void registerDisplayTransactionListener(DisplayManagerInternal.DisplayTransactionListener listener) {
            if (listener != null) {
                DisplayManagerService.this.registerDisplayTransactionListenerInternal(listener);
                return;
            }
            throw new IllegalArgumentException("listener must not be null");
        }

        public void unregisterDisplayTransactionListener(DisplayManagerInternal.DisplayTransactionListener listener) {
            if (listener != null) {
                DisplayManagerService.this.unregisterDisplayTransactionListenerInternal(listener);
                return;
            }
            throw new IllegalArgumentException("listener must not be null");
        }

        public void setDisplayInfoOverrideFromWindowManager(int displayId, DisplayInfo info) {
            DisplayManagerService.this.setDisplayInfoOverrideFromWindowManagerInternal(displayId, info);
        }

        public void getNonOverrideDisplayInfo(int displayId, DisplayInfo outInfo) {
            DisplayManagerService.this.getNonOverrideDisplayInfoInternal(displayId, outInfo);
        }

        public void performTraversal(SurfaceControl.Transaction t) {
            DisplayManagerService.this.performTraversalInternal(t);
        }

        public void setDisplayProperties(int displayId, boolean hasContent, float requestedRefreshRate, int requestedMode, boolean inTraversal) {
            DisplayManagerService.this.setDisplayPropertiesInternal(displayId, hasContent, requestedRefreshRate, requestedMode, inTraversal);
        }

        public void setDisplayOffsets(int displayId, int x, int y) {
            DisplayManagerService.this.setDisplayOffsetsInternal(displayId, x, y);
        }

        public void setDisplayScalingDisabled(int displayId, boolean disableScaling) {
            DisplayManagerService.this.setDisplayScalingDisabledInternal(displayId, disableScaling);
        }

        public void setDisplayAccessUIDs(SparseArray<IntArray> newDisplayAccessUIDs) {
            DisplayManagerService.this.setDisplayAccessUIDsInternal(newDisplayAccessUIDs);
        }

        public void persistBrightnessTrackerState() {
            synchronized (DisplayManagerService.this.mSyncRoot) {
                DisplayManagerService.this.mDisplayPowerController.persistBrightnessTrackerState();
            }
        }

        public void onOverlayChanged() {
            synchronized (DisplayManagerService.this.mSyncRoot) {
                for (int i = 0; i < DisplayManagerService.this.mDisplayDevices.size(); i++) {
                    ((DisplayDevice) DisplayManagerService.this.mDisplayDevices.get(i)).onOverlayChangedLocked();
                }
            }
        }

        public DisplayedContentSamplingAttributes getDisplayedContentSamplingAttributes(int displayId) {
            return DisplayManagerService.this.getDisplayedContentSamplingAttributesInternal(displayId);
        }

        public boolean setDisplayedContentSamplingEnabled(int displayId, boolean enable, int componentMask, int maxFrames) {
            return DisplayManagerService.this.setDisplayedContentSamplingEnabledInternal(displayId, enable, componentMask, maxFrames);
        }

        public DisplayedContentSample getDisplayedContentSample(int displayId, long maxFrames, long timestamp) {
            return DisplayManagerService.this.getDisplayedContentSampleInternal(displayId, maxFrames, timestamp);
        }
    }

    class AllowedDisplayModeObserver implements DisplayModeDirector.Listener {
        AllowedDisplayModeObserver() {
        }

        public void onAllowedDisplayModesChanged() {
            DisplayManagerService.this.onAllowedDisplayModesChangedInternal();
        }
    }
}
