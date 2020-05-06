package com.android.server.wm;

import android.app.ActivityManager;
import android.app.ActivityThread;
import android.app.LoadedApk;
import android.app.ResourcesManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Insets;
import android.graphics.Rect;
import android.graphics.Region;
import android.hardware.input.InputManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.server.am.SplitScreenReporter;
import android.util.ArraySet;
import android.util.BoostFramework;
import android.util.Pair;
import android.util.PrintWriterPrinter;
import android.util.Slog;
import android.view.DisplayCutout;
import android.view.IApplicationToken;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.policy.ScreenDecorationsUtils;
import com.android.internal.util.ScreenShapeHelper;
import com.android.internal.util.ScreenshotHelper;
import com.android.internal.util.ToBooleanFunction;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.widget.PointerLocationView;
import com.android.server.LocalServices;
import com.android.server.UiThread;
import com.android.server.am.BroadcastQueueInjector;
import com.android.server.pm.DumpState;
import com.android.server.policy.WindowManagerPolicy;
import com.android.server.policy.WindowOrientationListener;
import com.android.server.statusbar.StatusBarManagerInternal;
import com.android.server.wallpaper.WallpaperManagerInternal;
import com.android.server.wm.ActivityTaskManagerInternal;
import com.android.server.wm.BarController;
import com.android.server.wm.SystemGesturesPointerEventListener;
import com.android.server.wm.utils.InsetUtils;
import com.miui.enterprise.RestrictionsHelper;
import java.io.PrintWriter;

public class DisplayPolicy {
    private static final boolean ALTERNATE_CAR_MODE_NAV_SIZE = false;
    private static final boolean DEBUG = false;
    private static final int MSG_DISABLE_POINTER_LOCATION = 5;
    private static final int MSG_DISPOSE_INPUT_CONSUMER = 3;
    private static final int MSG_ENABLE_POINTER_LOCATION = 4;
    private static final int MSG_REQUEST_TRANSIENT_BARS = 2;
    private static final int MSG_REQUEST_TRANSIENT_BARS_ARG_NAVIGATION = 1;
    private static final int MSG_REQUEST_TRANSIENT_BARS_ARG_STATUS = 0;
    private static final int MSG_UPDATE_DREAMING_SLEEP_TOKEN = 1;
    private static final int NAV_BAR_FORCE_TRANSPARENT = 2;
    private static final int NAV_BAR_OPAQUE_WHEN_FREEFORM_OR_DOCKED = 0;
    private static final int NAV_BAR_TRANSLUCENT_WHEN_FREEFORM_OPAQUE_OTHERWISE = 1;
    private static final long PANIC_GESTURE_EXPIRATION = 30000;
    /* access modifiers changed from: private */
    public static boolean SCROLL_BOOST_SS_ENABLE = false;
    private static final int SYSTEM_UI_CHANGING_LAYOUT = -1073709042;
    public static final String TAG = "WindowManager";
    private static final Rect sTmpDisplayCutoutSafeExceptMaybeBarsRect = new Rect();
    private static final Rect sTmpDockedFrame = new Rect();
    private static final Rect sTmpLastParentFrame = new Rect();
    private static final Rect sTmpNavFrame = new Rect();
    private static final Rect sTmpRect = new Rect();
    private boolean isInSplitWindowMode = false;
    /* access modifiers changed from: private */
    public final AccessibilityManager mAccessibilityManager;
    private final Runnable mAcquireSleepTokenRunnable;
    private boolean mAllowLockscreenWhenOn;
    private volatile boolean mAllowSeamlessRotationDespiteNavBarMoving;
    private volatile boolean mAwake;
    private int mBottomGestureAdditionalInset;
    private final boolean mCarDockEnablesAccelerometer;
    /* access modifiers changed from: private */
    public final Runnable mClearHideNavigationFlag = new Runnable() {
        public void run() {
            synchronized (DisplayPolicy.this.mLock) {
                DisplayPolicy.access$1372(DisplayPolicy.this, -3);
                DisplayPolicy.this.mDisplayContent.reevaluateStatusBarVisibility();
            }
        }
    };
    protected final Context mContext;
    private Resources mCurrentUserResources;
    private final boolean mDeskDockEnablesAccelerometer;
    protected final DisplayContent mDisplayContent;
    private volatile int mDockMode = 0;
    private final Rect mDockedStackBounds = new Rect();
    private boolean mDreamingLockscreen;
    @GuardedBy({"mHandler"})
    private ActivityTaskManagerInternal.SleepToken mDreamingSleepToken;
    private boolean mDreamingSleepTokenNeeded;
    IApplicationToken mFocusedApp;
    protected WindowState mFocusedWindow;
    /* access modifiers changed from: private */
    public int mForceClearedSystemUiFlags = 0;
    protected boolean mForceShowSystemBars;
    private boolean mForceShowSystemBarsFromExternal;
    private boolean mForceStatusBar;
    protected boolean mForceStatusBarFromKeyguard;
    private boolean mForceStatusBarTransparent;
    private boolean mForcingShowNavBar;
    private int mForcingShowNavBarLayer;
    private Insets mForwardedInsets = Insets.NONE;
    protected final Handler mHandler;
    protected volatile boolean mHasNavigationBar;
    private volatile boolean mHasStatusBar;
    private volatile boolean mHdmiPlugged;
    private final Runnable mHiddenNavPanic = new Runnable() {
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0034, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r4 = this;
                com.android.server.wm.DisplayPolicy r0 = com.android.server.wm.DisplayPolicy.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.wm.DisplayPolicy r1 = com.android.server.wm.DisplayPolicy.this     // Catch:{ all -> 0x0035 }
                com.android.server.wm.WindowManagerService r1 = r1.mService     // Catch:{ all -> 0x0035 }
                com.android.server.policy.WindowManagerPolicy r1 = r1.mPolicy     // Catch:{ all -> 0x0035 }
                boolean r1 = r1.isUserSetupComplete()     // Catch:{ all -> 0x0035 }
                if (r1 != 0) goto L_0x0017
                monitor-exit(r0)     // Catch:{ all -> 0x0035 }
                return
            L_0x0017:
                com.android.server.wm.DisplayPolicy r1 = com.android.server.wm.DisplayPolicy.this     // Catch:{ all -> 0x0035 }
                long r2 = android.os.SystemClock.uptimeMillis()     // Catch:{ all -> 0x0035 }
                long unused = r1.mPendingPanicGestureUptime = r2     // Catch:{ all -> 0x0035 }
                com.android.server.wm.DisplayPolicy r1 = com.android.server.wm.DisplayPolicy.this     // Catch:{ all -> 0x0035 }
                int r1 = r1.mLastSystemUiFlags     // Catch:{ all -> 0x0035 }
                boolean r1 = com.android.server.wm.DisplayPolicy.isNavBarEmpty(r1)     // Catch:{ all -> 0x0035 }
                if (r1 != 0) goto L_0x0033
                com.android.server.wm.DisplayPolicy r1 = com.android.server.wm.DisplayPolicy.this     // Catch:{ all -> 0x0035 }
                com.android.server.wm.BarController r1 = r1.mNavigationBarController     // Catch:{ all -> 0x0035 }
                r1.showTransient()     // Catch:{ all -> 0x0035 }
            L_0x0033:
                monitor-exit(r0)     // Catch:{ all -> 0x0035 }
                return
            L_0x0035:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0035 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.DisplayPolicy.AnonymousClass4.run():void");
        }
    };
    private final ImmersiveModeConfirmation mImmersiveModeConfirmation;
    /* access modifiers changed from: private */
    public WindowManagerPolicy.InputConsumer mInputConsumer = null;
    /* access modifiers changed from: private */
    public boolean mIsPerfBoostFlingAcquired;
    private volatile boolean mKeyguardDrawComplete;
    private final Rect mLastDockedStackBounds = new Rect();
    private int mLastDockedStackSysUiFlags;
    private boolean mLastFocusNeedsMenu = false;
    private WindowState mLastFocusedWindow;
    private int mLastFullscreenStackSysUiFlags;
    private final Rect mLastNonDockedStackBounds = new Rect();
    private boolean mLastShowingDream;
    int mLastSystemUiFlags;
    private boolean mLastWindowSleepTokenNeeded;
    private volatile int mLidState = -1;
    /* access modifiers changed from: private */
    public final Object mLock;
    private int mNavBarOpacityMode = 0;
    private final BarController.OnBarVisibilityChangedListener mNavBarVisibilityListener = new BarController.OnBarVisibilityChangedListener() {
        public void onBarVisibilityChanged(boolean visible) {
            if (DisplayPolicy.this.mAccessibilityManager != null) {
                DisplayPolicy.this.mAccessibilityManager.notifyAccessibilityButtonVisibilityChanged(visible);
            }
        }
    };
    WindowState mNavigationBar = null;
    /* access modifiers changed from: private */
    public volatile boolean mNavigationBarAlwaysShowOnSideGesture;
    private volatile boolean mNavigationBarCanMove;
    /* access modifiers changed from: private */
    public final BarController mNavigationBarController;
    private int[] mNavigationBarFrameHeightForRotationDefault = new int[4];
    protected int[] mNavigationBarHeightForRotationDefault = new int[4];
    private int[] mNavigationBarHeightForRotationInCarMode = new int[4];
    private volatile boolean mNavigationBarLetsThroughTaps;
    protected int mNavigationBarPosition = 4;
    protected int[] mNavigationBarWidthForRotationDefault = new int[4];
    private int[] mNavigationBarWidthForRotationInCarMode = new int[4];
    private final Rect mNonDockedStackBounds = new Rect();
    /* access modifiers changed from: private */
    public long mPendingPanicGestureUptime;
    BoostFramework mPerf = new BoostFramework();
    BoostFramework mPerfBoostDrag = null;
    BoostFramework mPerfBoostFling = null;
    BoostFramework mPerfBoostPrefling = null;
    private volatile boolean mPersistentVrModeEnabled;
    private PointerLocationView mPointerLocationView;
    private RefreshRatePolicy mRefreshRatePolicy;
    private final Runnable mReleaseSleepTokenRunnable;
    /* access modifiers changed from: private */
    public int mResettingSystemUiFlags = 0;
    private final ArraySet<WindowState> mScreenDecorWindows = new ArraySet<>();
    private volatile boolean mScreenOnEarly;
    private volatile boolean mScreenOnFully;
    private volatile WindowManagerPolicy.ScreenOnListener mScreenOnListener;
    private final ScreenshotHelper mScreenshotHelper;
    /* access modifiers changed from: private */
    public final WindowManagerService mService;
    private final Object mServiceAcquireLock = new Object();
    private boolean mShowingDream;
    private int mSideGestureInset;
    protected WindowState mStatusBar = null;
    private final StatusBarController mStatusBarController;
    private final int[] mStatusBarHeightForRotation = new int[4];
    private StatusBarManagerInternal mStatusBarManagerInternal;
    /* access modifiers changed from: private */
    public final SystemGesturesPointerEventListener mSystemGestures;
    protected WindowState mTopDockedOpaqueOrDimmingWindowState;
    protected WindowState mTopDockedOpaqueWindowState;
    protected WindowState mTopFullscreenOpaqueOrDimmingWindowState;
    protected WindowState mTopFullscreenOpaqueWindowState;
    private boolean mTopIsFullscreen;
    private volatile boolean mWindowManagerDrawComplete;
    protected WindowManagerPolicy.WindowManagerFuncs mWindowManagerFuncs;
    private int mWindowOutsetBottom;
    @GuardedBy({"mHandler"})
    private ActivityTaskManagerInternal.SleepToken mWindowSleepToken;
    private boolean mWindowSleepTokenNeeded;

    static /* synthetic */ int access$1372(DisplayPolicy x0, int x1) {
        int i = x0.mForceClearedSystemUiFlags & x1;
        x0.mForceClearedSystemUiFlags = i;
        return i;
    }

    private StatusBarManagerInternal getStatusBarManagerInternal() {
        StatusBarManagerInternal statusBarManagerInternal;
        synchronized (this.mServiceAcquireLock) {
            if (this.mStatusBarManagerInternal == null) {
                this.mStatusBarManagerInternal = (StatusBarManagerInternal) LocalServices.getService(StatusBarManagerInternal.class);
            }
            statusBarManagerInternal = this.mStatusBarManagerInternal;
        }
        return statusBarManagerInternal;
    }

    private class PolicyHandler extends Handler {
        PolicyHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            boolean z = true;
            if (i == 1) {
                DisplayPolicy displayPolicy = DisplayPolicy.this;
                if (msg.arg1 == 0) {
                    z = false;
                }
                displayPolicy.updateDreamingSleepToken(z);
            } else if (i == 2) {
                WindowState targetBar = msg.arg1 == 0 ? DisplayPolicy.this.mStatusBar : DisplayPolicy.this.mNavigationBar;
                if (targetBar != null) {
                    DisplayPolicy.this.requestTransientBars(targetBar);
                }
            } else if (i == 3) {
                DisplayPolicy.this.disposeInputConsumer((WindowManagerPolicy.InputConsumer) msg.obj);
            } else if (i == 4) {
                DisplayPolicy.this.enablePointerLocation();
            } else if (i == 5) {
                DisplayPolicy.this.disablePointerLocation();
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean isTopAppGame() {
        boolean isGame = false;
        try {
            ApplicationInfo ai = this.mContext.getPackageManager().getApplicationInfo(((ActivityManager.RunningTaskInfo) ActivityManager.getService().getFilteredTasks(1, 3, 0).get(0)).topActivity.getPackageName(), 0);
            if (ai == null) {
                return false;
            }
            if (ai.category == 0 || (ai.flags & DumpState.DUMP_APEX) == 33554432) {
                isGame = true;
            }
            return isGame;
        } catch (Exception e) {
            return false;
        }
    }

    DisplayPolicy(WindowManagerService service, DisplayContent displayContent) {
        Context context;
        ScreenshotHelper screenshotHelper = null;
        this.mService = service;
        if (displayContent.isDefaultDisplay) {
            context = service.mContext;
        } else {
            context = service.mContext.createDisplayContext(displayContent.getDisplay());
        }
        this.mContext = context;
        this.mDisplayContent = displayContent;
        this.mLock = service.getWindowManagerLock();
        this.mWindowManagerFuncs = service;
        int displayId = displayContent.getDisplayId();
        this.mStatusBarController = new StatusBarController(displayId);
        this.mNavigationBarController = new BarController("NavigationBar", displayId, 134217728, 536870912, Integer.MIN_VALUE, 2, 134217728, 32768);
        Resources r = this.mContext.getResources();
        this.mCarDockEnablesAccelerometer = r.getBoolean(17891387);
        this.mDeskDockEnablesAccelerometer = r.getBoolean(17891402);
        this.mForceShowSystemBarsFromExternal = r.getBoolean(17891460);
        this.mAccessibilityManager = (AccessibilityManager) this.mContext.getSystemService("accessibility");
        if (!displayContent.isDefaultDisplay) {
            this.mAwake = true;
            this.mScreenOnEarly = true;
            this.mScreenOnFully = true;
        }
        BoostFramework boostFramework = this.mPerf;
        if (boostFramework != null) {
            SCROLL_BOOST_SS_ENABLE = Boolean.parseBoolean(boostFramework.perfGetProp("vendor.perf.gestureflingboost.enable", "false"));
        }
        Looper looper = UiThread.getHandler().getLooper();
        this.mHandler = new PolicyHandler(looper);
        this.mSystemGestures = new SystemGesturesPointerEventListener(this.mContext, this.mHandler, new SystemGesturesPointerEventListener.Callbacks() {
            public void onSwipeFromTop() {
                if (DisplayPolicy.this.mStatusBar != null) {
                    DisplayPolicy displayPolicy = DisplayPolicy.this;
                    displayPolicy.requestTransientBars(displayPolicy.mStatusBar);
                }
            }

            public void onSwipeFromBottom() {
                if (DisplayPolicy.this.mNavigationBar != null && DisplayPolicy.this.mNavigationBarPosition == 4) {
                    DisplayPolicy displayPolicy = DisplayPolicy.this;
                    displayPolicy.requestTransientBars(displayPolicy.mNavigationBar);
                }
            }

            public void onSwipeFromRight() {
                Region excludedRegion;
                synchronized (DisplayPolicy.this.mLock) {
                    excludedRegion = DisplayPolicy.this.mDisplayContent.calculateSystemGestureExclusion();
                }
                boolean sideAllowed = DisplayPolicy.this.mNavigationBarAlwaysShowOnSideGesture || DisplayPolicy.this.mNavigationBarPosition == 2;
                if (DisplayPolicy.this.mNavigationBar != null && sideAllowed && !DisplayPolicy.this.mSystemGestures.currentGestureStartedInRegion(excludedRegion)) {
                    DisplayPolicy displayPolicy = DisplayPolicy.this;
                    displayPolicy.requestTransientBars(displayPolicy.mNavigationBar);
                }
            }

            public void onSwipeFromLeft() {
                Region excludedRegion;
                synchronized (DisplayPolicy.this.mLock) {
                    excludedRegion = DisplayPolicy.this.mDisplayContent.calculateSystemGestureExclusion();
                }
                boolean z = true;
                if (!DisplayPolicy.this.mNavigationBarAlwaysShowOnSideGesture && DisplayPolicy.this.mNavigationBarPosition != 1) {
                    z = false;
                }
                boolean sideAllowed = z;
                if (DisplayPolicy.this.mNavigationBar != null && sideAllowed && !DisplayPolicy.this.mSystemGestures.currentGestureStartedInRegion(excludedRegion)) {
                    DisplayPolicy displayPolicy = DisplayPolicy.this;
                    displayPolicy.requestTransientBars(displayPolicy.mNavigationBar);
                }
            }

            public void onFling(int duration) {
                if (DisplayPolicy.this.mService.mPowerManagerInternal != null) {
                    DisplayPolicy.this.mService.mPowerManagerInternal.powerHint(2, duration);
                }
            }

            public void onVerticalFling(int duration) {
                String currentPackage = DisplayPolicy.this.mContext.getPackageName();
                boolean isGame = DisplayPolicy.this.isTopAppGame();
                if (DisplayPolicy.SCROLL_BOOST_SS_ENABLE && !isGame) {
                    if (DisplayPolicy.this.mPerfBoostFling == null) {
                        DisplayPolicy.this.mPerfBoostFling = new BoostFramework();
                        boolean unused = DisplayPolicy.this.mIsPerfBoostFlingAcquired = false;
                    }
                    if (DisplayPolicy.this.mPerfBoostFling == null) {
                        Slog.e(DisplayPolicy.TAG, "Error: boost object null");
                        return;
                    }
                    DisplayPolicy.this.mPerfBoostFling.perfHint(4224, currentPackage, duration + 160, 1);
                    boolean unused2 = DisplayPolicy.this.mIsPerfBoostFlingAcquired = true;
                }
            }

            public void onHorizontalFling(int duration) {
                String currentPackage = DisplayPolicy.this.mContext.getPackageName();
                boolean isGame = DisplayPolicy.this.isTopAppGame();
                if (DisplayPolicy.SCROLL_BOOST_SS_ENABLE && !isGame) {
                    if (DisplayPolicy.this.mPerfBoostFling == null) {
                        DisplayPolicy.this.mPerfBoostFling = new BoostFramework();
                        boolean unused = DisplayPolicy.this.mIsPerfBoostFlingAcquired = false;
                    }
                    if (DisplayPolicy.this.mPerfBoostFling == null) {
                        Slog.e(DisplayPolicy.TAG, "Error: boost object null");
                        return;
                    }
                    DisplayPolicy.this.mPerfBoostFling.perfHint(4224, currentPackage, duration + 160, 2);
                    boolean unused2 = DisplayPolicy.this.mIsPerfBoostFlingAcquired = true;
                }
            }

            public void onScroll(boolean started) {
                String currentPackage = DisplayPolicy.this.mContext.getPackageName();
                boolean isGame = DisplayPolicy.this.isTopAppGame();
                if (DisplayPolicy.this.mPerfBoostDrag == null) {
                    DisplayPolicy.this.mPerfBoostDrag = new BoostFramework();
                }
                if (DisplayPolicy.this.mPerfBoostDrag == null) {
                    Slog.e(DisplayPolicy.TAG, "Error: boost object null");
                    return;
                }
                if (DisplayPolicy.SCROLL_BOOST_SS_ENABLE && !isGame) {
                    if (DisplayPolicy.this.mPerfBoostPrefling == null) {
                        DisplayPolicy.this.mPerfBoostPrefling = new BoostFramework();
                    }
                    if (DisplayPolicy.this.mPerfBoostPrefling == null) {
                        Slog.e(DisplayPolicy.TAG, "Error: boost object null");
                        return;
                    }
                    DisplayPolicy.this.mPerfBoostPrefling.perfHint(4224, currentPackage, -1, 4);
                }
                if (isGame || !started) {
                    DisplayPolicy.this.mPerfBoostDrag.perfLockRelease();
                } else {
                    DisplayPolicy.this.mPerfBoostDrag.perfHint(4231, currentPackage, -1, 1);
                }
            }

            public void onDebug() {
            }

            private WindowOrientationListener getOrientationListener() {
                DisplayRotation rotation = DisplayPolicy.this.mDisplayContent.getDisplayRotation();
                if (rotation != null) {
                    return rotation.getOrientationListener();
                }
                return null;
            }

            public void onDown() {
                WindowOrientationListener listener = getOrientationListener();
                if (listener != null) {
                    listener.onTouchStart();
                }
                if (DisplayPolicy.SCROLL_BOOST_SS_ENABLE && DisplayPolicy.this.mPerfBoostFling != null && DisplayPolicy.this.mIsPerfBoostFlingAcquired) {
                    DisplayPolicy.this.mPerfBoostFling.perfLockRelease();
                    boolean unused = DisplayPolicy.this.mIsPerfBoostFlingAcquired = false;
                }
            }

            public void onUpOrCancel() {
                WindowOrientationListener listener = getOrientationListener();
                if (listener != null) {
                    listener.onTouchEnd();
                }
            }

            public void onMouseHoverAtTop() {
                DisplayPolicy.this.mHandler.removeMessages(2);
                Message msg = DisplayPolicy.this.mHandler.obtainMessage(2);
                msg.arg1 = 0;
                DisplayPolicy.this.mHandler.sendMessageDelayed(msg, 500);
            }

            public void onMouseHoverAtBottom() {
                DisplayPolicy.this.mHandler.removeMessages(2);
                Message msg = DisplayPolicy.this.mHandler.obtainMessage(2);
                msg.arg1 = 1;
                DisplayPolicy.this.mHandler.sendMessageDelayed(msg, 500);
            }

            public void onMouseLeaveFromEdge() {
                DisplayPolicy.this.mHandler.removeMessages(2);
            }
        });
        displayContent.registerPointerEventListener(this.mSystemGestures);
        displayContent.mAppTransition.registerListenerLocked(this.mStatusBarController.getAppTransitionListener());
        this.mImmersiveModeConfirmation = new ImmersiveModeConfirmation(this.mContext, looper, this.mService.mVrModeEnabled);
        this.mAcquireSleepTokenRunnable = new Runnable(service, displayId) {
            private final /* synthetic */ WindowManagerService f$1;
            private final /* synthetic */ int f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                DisplayPolicy.this.lambda$new$0$DisplayPolicy(this.f$1, this.f$2);
            }
        };
        this.mReleaseSleepTokenRunnable = new Runnable() {
            public final void run() {
                DisplayPolicy.this.lambda$new$1$DisplayPolicy();
            }
        };
        this.mScreenshotHelper = displayContent.isDefaultDisplay ? new ScreenshotHelper(this.mContext) : screenshotHelper;
        if (this.mDisplayContent.isDefaultDisplay) {
            this.mHasStatusBar = true;
            this.mHasNavigationBar = this.mContext.getResources().getBoolean(17891518);
            String navBarOverride = SystemProperties.get("qemu.hw.mainkeys");
            if (SplitScreenReporter.ACTION_ENTER_SPLIT.equals(navBarOverride)) {
                this.mHasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                this.mHasNavigationBar = true;
            }
        } else {
            this.mHasStatusBar = false;
            this.mHasNavigationBar = this.mDisplayContent.supportsSystemDecorations();
        }
        this.mRefreshRatePolicy = new RefreshRatePolicy(this.mService, this.mDisplayContent.getDisplayInfo(), this.mService.mHighRefreshRateBlacklist);
    }

    public /* synthetic */ void lambda$new$0$DisplayPolicy(WindowManagerService service, int displayId) {
        if (this.mWindowSleepToken == null) {
            ActivityTaskManagerInternal activityTaskManagerInternal = service.mAtmInternal;
            this.mWindowSleepToken = activityTaskManagerInternal.acquireSleepToken("WindowSleepTokenOnDisplay" + displayId, displayId);
        }
    }

    public /* synthetic */ void lambda$new$1$DisplayPolicy() {
        ActivityTaskManagerInternal.SleepToken sleepToken = this.mWindowSleepToken;
        if (sleepToken != null) {
            sleepToken.release();
            this.mWindowSleepToken = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void systemReady() {
        this.mSystemGestures.systemReady();
        if (this.mService.mPointerLocationEnabled) {
            setPointerLocationEnabled(true);
        }
    }

    private int getDisplayId() {
        return this.mDisplayContent.getDisplayId();
    }

    public void setHdmiPlugged(boolean plugged) {
        setHdmiPlugged(plugged, false);
    }

    public void setHdmiPlugged(boolean plugged, boolean force) {
        if (force || this.mHdmiPlugged != plugged) {
            this.mHdmiPlugged = plugged;
            this.mService.updateRotation(true, true);
            Intent intent = new Intent("android.intent.action.HDMI_PLUGGED");
            intent.addFlags(BroadcastQueueInjector.FLAG_IMMUTABLE);
            intent.putExtra("state", plugged);
            this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isHdmiPlugged() {
        return this.mHdmiPlugged;
    }

    /* access modifiers changed from: package-private */
    public boolean isCarDockEnablesAccelerometer() {
        return this.mCarDockEnablesAccelerometer;
    }

    /* access modifiers changed from: package-private */
    public boolean isDeskDockEnablesAccelerometer() {
        return this.mDeskDockEnablesAccelerometer;
    }

    public void setPersistentVrModeEnabled(boolean persistentVrModeEnabled) {
        this.mPersistentVrModeEnabled = persistentVrModeEnabled;
    }

    public boolean isPersistentVrModeEnabled() {
        return this.mPersistentVrModeEnabled;
    }

    public void setDockMode(int dockMode) {
        this.mDockMode = dockMode;
    }

    public int getDockMode() {
        return this.mDockMode;
    }

    /* access modifiers changed from: package-private */
    public void setForceShowSystemBars(boolean forceShowSystemBars) {
        this.mForceShowSystemBarsFromExternal = forceShowSystemBars;
    }

    public boolean hasNavigationBar() {
        return this.mHasNavigationBar;
    }

    public boolean hasStatusBar() {
        return this.mHasStatusBar;
    }

    public boolean navigationBarCanMove() {
        return this.mNavigationBarCanMove;
    }

    public void setLidState(int lidState) {
        this.mLidState = lidState;
    }

    public int getLidState() {
        return this.mLidState;
    }

    public void setAwake(boolean awake) {
        this.mAwake = awake;
    }

    public boolean isAwake() {
        return this.mAwake;
    }

    public boolean isScreenOnEarly() {
        return this.mScreenOnEarly;
    }

    public boolean isScreenOnFully() {
        return this.mScreenOnFully;
    }

    public boolean isKeyguardDrawComplete() {
        return this.mKeyguardDrawComplete;
    }

    public boolean isWindowManagerDrawComplete() {
        return this.mWindowManagerDrawComplete;
    }

    public WindowManagerPolicy.ScreenOnListener getScreenOnListener() {
        return this.mScreenOnListener;
    }

    public void screenTurnedOn(WindowManagerPolicy.ScreenOnListener screenOnListener) {
        synchronized (this.mLock) {
            this.mScreenOnEarly = true;
            this.mScreenOnFully = false;
            this.mKeyguardDrawComplete = false;
            this.mWindowManagerDrawComplete = false;
            this.mScreenOnListener = screenOnListener;
        }
    }

    public void screenTurnedOff() {
        synchronized (this.mLock) {
            this.mScreenOnEarly = false;
            this.mScreenOnFully = false;
            this.mKeyguardDrawComplete = false;
            this.mWindowManagerDrawComplete = false;
            this.mScreenOnListener = null;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0015, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean finishKeyguardDrawn() {
        /*
            r3 = this;
            java.lang.Object r0 = r3.mLock
            monitor-enter(r0)
            boolean r1 = r3.mScreenOnEarly     // Catch:{ all -> 0x0016 }
            r2 = 0
            if (r1 == 0) goto L_0x0014
            boolean r1 = r3.mKeyguardDrawComplete     // Catch:{ all -> 0x0016 }
            if (r1 == 0) goto L_0x000d
            goto L_0x0014
        L_0x000d:
            r1 = 1
            r3.mKeyguardDrawComplete = r1     // Catch:{ all -> 0x0016 }
            r3.mWindowManagerDrawComplete = r2     // Catch:{ all -> 0x0016 }
            monitor-exit(r0)     // Catch:{ all -> 0x0016 }
            return r1
        L_0x0014:
            monitor-exit(r0)     // Catch:{ all -> 0x0016 }
            return r2
        L_0x0016:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0016 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.DisplayPolicy.finishKeyguardDrawn():boolean");
    }

    public boolean finishWindowsDrawn() {
        synchronized (this.mLock) {
            if (this.mScreenOnEarly) {
                if (!this.mWindowManagerDrawComplete) {
                    this.mWindowManagerDrawComplete = true;
                    return true;
                }
            }
            return false;
        }
    }

    public boolean finishScreenTurningOn() {
        synchronized (this.mLock) {
            Slog.d(TAG, "finishScreenTurningOn: mAwake=" + this.mAwake + ", mScreenOnEarly=" + this.mScreenOnEarly + ", mScreenOnFully=" + this.mScreenOnFully + ", mKeyguardDrawComplete=" + this.mKeyguardDrawComplete + ", mWindowManagerDrawComplete=" + this.mWindowManagerDrawComplete);
            if (!this.mScreenOnFully && this.mScreenOnEarly && this.mWindowManagerDrawComplete) {
                if (!this.mAwake || this.mKeyguardDrawComplete) {
                    Slog.i(TAG, "Finished screen turning on...");
                    this.mScreenOnListener = null;
                    this.mScreenOnFully = true;
                    return true;
                }
            }
            return false;
        }
    }

    private boolean hasStatusBarServicePermission(int pid, int uid) {
        return this.mContext.checkPermission("android.permission.STATUS_BAR_SERVICE", pid, uid) == 0;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0044, code lost:
        if (r2 != 2006) goto L_0x00b0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void adjustWindowParamsLw(com.android.server.wm.WindowState r7, android.view.WindowManager.LayoutParams r8, int r9, int r10) {
        /*
            r6 = this;
            int r0 = r8.privateFlags
            r1 = 4194304(0x400000, float:5.877472E-39)
            r0 = r0 & r1
            r1 = 1
            if (r0 == 0) goto L_0x000a
            r0 = r1
            goto L_0x000b
        L_0x000a:
            r0 = 0
        L_0x000b:
            android.util.ArraySet<com.android.server.wm.WindowState> r2 = r6.mScreenDecorWindows
            boolean r2 = r2.contains(r7)
            if (r2 == 0) goto L_0x001b
            if (r0 != 0) goto L_0x0028
            android.util.ArraySet<com.android.server.wm.WindowState> r2 = r6.mScreenDecorWindows
            r2.remove(r7)
            goto L_0x0028
        L_0x001b:
            if (r0 == 0) goto L_0x0028
            boolean r2 = r6.hasStatusBarServicePermission(r9, r10)
            if (r2 == 0) goto L_0x0028
            android.util.ArraySet<com.android.server.wm.WindowState> r2 = r6.mScreenDecorWindows
            r2.add(r7)
        L_0x0028:
            int r2 = r8.type
            r3 = 2000(0x7d0, float:2.803E-42)
            if (r2 == r3) goto L_0x0098
            r4 = 2013(0x7dd, float:2.821E-42)
            if (r2 == r4) goto L_0x0095
            r4 = 2015(0x7df, float:2.824E-42)
            if (r2 == r4) goto L_0x0086
            r4 = 2023(0x7e7, float:2.835E-42)
            if (r2 == r4) goto L_0x0095
            r1 = 2036(0x7f4, float:2.853E-42)
            if (r2 == r1) goto L_0x007f
            r1 = 2005(0x7d5, float:2.81E-42)
            if (r2 == r1) goto L_0x0047
            r1 = 2006(0x7d6, float:2.811E-42)
            if (r2 == r1) goto L_0x0086
            goto L_0x00b0
        L_0x0047:
            long r1 = r8.hideTimeoutMilliseconds
            r4 = 0
            int r1 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            r4 = 3500(0xdac, double:1.729E-320)
            if (r1 < 0) goto L_0x0057
            long r1 = r8.hideTimeoutMilliseconds
            int r1 = (r1 > r4 ? 1 : (r1 == r4 ? 0 : -1))
            if (r1 <= 0) goto L_0x0059
        L_0x0057:
            r8.hideTimeoutMilliseconds = r4
        L_0x0059:
            android.view.accessibility.AccessibilityManager r1 = r6.mAccessibilityManager
            long r4 = r8.hideTimeoutMilliseconds
            int r2 = (int) r4
            r4 = 2
            int r1 = r1.getRecommendedTimeoutMillis(r2, r4)
            long r1 = (long) r1
            r8.hideTimeoutMilliseconds = r1
            r1 = 16973828(0x1030004, float:2.406091E-38)
            r8.windowAnimations = r1
            boolean r1 = r6.canToastShowWhenLocked(r9)
            if (r1 == 0) goto L_0x0078
            int r1 = r8.flags
            r2 = 524288(0x80000, float:7.34684E-40)
            r1 = r1 | r2
            r8.flags = r1
        L_0x0078:
            int r1 = r8.flags
            r1 = r1 | 16
            r8.flags = r1
            goto L_0x00b0
        L_0x007f:
            int r1 = r8.flags
            r1 = r1 | 8
            r8.flags = r1
            goto L_0x00b0
        L_0x0086:
            int r1 = r8.flags
            r1 = r1 | 24
            r8.flags = r1
            int r1 = r8.flags
            r2 = -262145(0xfffffffffffbffff, float:NaN)
            r1 = r1 & r2
            r8.flags = r1
            goto L_0x00b0
        L_0x0095:
            r8.layoutInDisplayCutoutMode = r1
            goto L_0x00b0
        L_0x0098:
            com.android.server.wm.WindowManagerService r1 = r6.mService
            com.android.server.policy.WindowManagerPolicy r1 = r1.mPolicy
            boolean r1 = r1.isKeyguardOccluded()
            if (r1 == 0) goto L_0x00b0
            int r1 = r8.flags
            r2 = -1048577(0xffffffffffefffff, float:NaN)
            r1 = r1 & r2
            r8.flags = r1
            int r1 = r8.privateFlags
            r1 = r1 & -1025(0xfffffffffffffbff, float:NaN)
            r8.privateFlags = r1
        L_0x00b0:
            int r1 = r8.type
            if (r1 == r3) goto L_0x00ba
            int r1 = r8.privateFlags
            r1 = r1 & -1025(0xfffffffffffffbff, float:NaN)
            r8.privateFlags = r1
        L_0x00ba:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.DisplayPolicy.adjustWindowParamsLw(com.android.server.wm.WindowState, android.view.WindowManager$LayoutParams, int, int):void");
    }

    /* access modifiers changed from: package-private */
    public boolean canToastShowWhenLocked(int callingPid) {
        return this.mDisplayContent.forAllWindows(new ToBooleanFunction(callingPid) {
            private final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            public final boolean apply(Object obj) {
                return DisplayPolicy.lambda$canToastShowWhenLocked$2(this.f$0, (WindowState) obj);
            }
        }, true);
    }

    static /* synthetic */ boolean lambda$canToastShowWhenLocked$2(int callingPid, WindowState w) {
        return callingPid == w.mSession.mPid && w.isVisible() && w.canShowWhenLocked();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002f, code lost:
        if (r0 != 2033) goto L_0x00c8;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int prepareAddWindowLw(com.android.server.wm.WindowState r7, android.view.WindowManager.LayoutParams r8) {
        /*
            r6 = this;
            int r0 = r8.privateFlags
            r1 = 4194304(0x400000, float:5.877472E-39)
            r0 = r0 & r1
            java.lang.String r1 = "DisplayPolicy"
            java.lang.String r2 = "android.permission.STATUS_BAR_SERVICE"
            if (r0 == 0) goto L_0x0015
            android.content.Context r0 = r6.mContext
            r0.enforceCallingOrSelfPermission(r2, r1)
            android.util.ArraySet<com.android.server.wm.WindowState> r0 = r6.mScreenDecorWindows
            r0.add(r7)
        L_0x0015:
            int r0 = r8.type
            r3 = 2000(0x7d0, float:2.803E-42)
            r4 = -7
            r5 = 0
            if (r0 == r3) goto L_0x008c
            r3 = 2014(0x7de, float:2.822E-42)
            if (r0 == r3) goto L_0x0086
            r3 = 2017(0x7e1, float:2.826E-42)
            if (r0 == r3) goto L_0x0086
            r3 = 2019(0x7e3, float:2.829E-42)
            if (r0 == r3) goto L_0x0033
            r3 = 2024(0x7e8, float:2.836E-42)
            if (r0 == r3) goto L_0x0086
            r3 = 2033(0x7f1, float:2.849E-42)
            if (r0 == r3) goto L_0x0086
            goto L_0x00c8
        L_0x0033:
            android.content.Context r0 = r6.mContext
            r0.enforceCallingOrSelfPermission(r2, r1)
            com.android.server.wm.WindowState r0 = r6.mNavigationBar
            if (r0 == 0) goto L_0x0043
            boolean r0 = r0.isAlive()
            if (r0 == 0) goto L_0x0043
            return r4
        L_0x0043:
            r6.mNavigationBar = r7
            com.android.server.wm.BarController r0 = r6.mNavigationBarController
            r0.setWindow(r7)
            com.android.server.wm.BarController r0 = r6.mNavigationBarController
            com.android.server.wm.BarController$OnBarVisibilityChangedListener r1 = r6.mNavBarVisibilityListener
            r2 = 1
            r0.setOnBarVisibilityChangedListener(r1, r2)
            com.android.server.wm.DisplayContent r0 = r6.mDisplayContent
            r1 = 0
            r0.setInsetProvider(r2, r7, r1)
            com.android.server.wm.DisplayContent r0 = r6.mDisplayContent
            r1 = 5
            com.android.server.wm.-$$Lambda$DisplayPolicy$52bg3qYmo5Unt8Q07j9d6hFQG2o r2 = new com.android.server.wm.-$$Lambda$DisplayPolicy$52bg3qYmo5Unt8Q07j9d6hFQG2o
            r2.<init>()
            r0.setInsetProvider(r1, r7, r2)
            com.android.server.wm.DisplayContent r0 = r6.mDisplayContent
            r1 = 6
            com.android.server.wm.-$$Lambda$DisplayPolicy$XeqRJzc7ac4NU1zAF74Hsb20Oyg r2 = new com.android.server.wm.-$$Lambda$DisplayPolicy$XeqRJzc7ac4NU1zAF74Hsb20Oyg
            r2.<init>()
            r0.setInsetProvider(r1, r7, r2)
            com.android.server.wm.DisplayContent r0 = r6.mDisplayContent
            r1 = 7
            com.android.server.wm.-$$Lambda$DisplayPolicy$2VfPB7jRHi3x9grU1pG8ihi_Ga4 r2 = new com.android.server.wm.-$$Lambda$DisplayPolicy$2VfPB7jRHi3x9grU1pG8ihi_Ga4
            r2.<init>()
            r0.setInsetProvider(r1, r7, r2)
            com.android.server.wm.DisplayContent r0 = r6.mDisplayContent
            r1 = 9
            com.android.server.wm.-$$Lambda$DisplayPolicy$LmU9vcWscAr5f4KqPLDYJTaZBVU r2 = new com.android.server.wm.-$$Lambda$DisplayPolicy$LmU9vcWscAr5f4KqPLDYJTaZBVU
            r2.<init>()
            r0.setInsetProvider(r1, r7, r2)
            goto L_0x00c8
        L_0x0086:
            android.content.Context r0 = r6.mContext
            r0.enforceCallingOrSelfPermission(r2, r1)
            goto L_0x00c8
        L_0x008c:
            android.content.Context r0 = r6.mContext
            r0.enforceCallingOrSelfPermission(r2, r1)
            com.android.server.wm.WindowState r0 = r6.mStatusBar
            if (r0 == 0) goto L_0x009c
            boolean r0 = r0.isAlive()
            if (r0 == 0) goto L_0x009c
            return r4
        L_0x009c:
            r6.mStatusBar = r7
            com.android.server.wm.StatusBarController r0 = r6.mStatusBarController
            r0.setWindow(r7)
            com.android.server.wm.DisplayContent r0 = r6.mDisplayContent
            boolean r0 = r0.isDefaultDisplay
            if (r0 == 0) goto L_0x00b0
            com.android.server.wm.WindowManagerService r0 = r6.mService
            com.android.server.policy.WindowManagerPolicy r0 = r0.mPolicy
            r0.setKeyguardCandidateLw(r7)
        L_0x00b0:
            com.android.server.wm.-$$Lambda$DisplayPolicy$sDsfACJdM5Dc_VvZ4b6PthimRJY r0 = new com.android.server.wm.-$$Lambda$DisplayPolicy$sDsfACJdM5Dc_VvZ4b6PthimRJY
            r0.<init>()
            com.android.server.wm.DisplayContent r1 = r6.mDisplayContent
            r1.setInsetProvider(r5, r7, r0)
            com.android.server.wm.DisplayContent r1 = r6.mDisplayContent
            r2 = 4
            r1.setInsetProvider(r2, r7, r0)
            com.android.server.wm.DisplayContent r1 = r6.mDisplayContent
            r2 = 8
            r1.setInsetProvider(r2, r7, r0)
        L_0x00c8:
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.DisplayPolicy.prepareAddWindowLw(com.android.server.wm.WindowState, android.view.WindowManager$LayoutParams):int");
    }

    public /* synthetic */ void lambda$prepareAddWindowLw$3$DisplayPolicy(DisplayFrames displayFrames, WindowState windowState, Rect rect) {
        rect.top = 0;
        rect.bottom = getStatusBarHeight(displayFrames);
    }

    public /* synthetic */ void lambda$prepareAddWindowLw$4$DisplayPolicy(DisplayFrames displayFrames, WindowState windowState, Rect inOutFrame) {
        inOutFrame.top -= this.mBottomGestureAdditionalInset;
    }

    public /* synthetic */ void lambda$prepareAddWindowLw$5$DisplayPolicy(DisplayFrames displayFrames, WindowState windowState, Rect inOutFrame) {
        inOutFrame.left = 0;
        inOutFrame.top = 0;
        inOutFrame.bottom = displayFrames.mDisplayHeight;
        inOutFrame.right = displayFrames.mUnrestricted.left + this.mSideGestureInset;
    }

    public /* synthetic */ void lambda$prepareAddWindowLw$6$DisplayPolicy(DisplayFrames displayFrames, WindowState windowState, Rect inOutFrame) {
        inOutFrame.left = displayFrames.mUnrestricted.right - this.mSideGestureInset;
        inOutFrame.top = 0;
        inOutFrame.bottom = displayFrames.mDisplayHeight;
        inOutFrame.right = displayFrames.mDisplayWidth;
    }

    public /* synthetic */ void lambda$prepareAddWindowLw$7$DisplayPolicy(DisplayFrames displayFrames, WindowState windowState, Rect inOutFrame) {
        if ((windowState.getAttrs().flags & 16) != 0 || this.mNavigationBarLetsThroughTaps) {
            inOutFrame.setEmpty();
        }
    }

    public void removeWindowLw(WindowState win) {
        if (this.mStatusBar == win) {
            this.mStatusBar = null;
            this.mStatusBarController.setWindow((WindowState) null);
            if (this.mDisplayContent.isDefaultDisplay) {
                this.mService.mPolicy.setKeyguardCandidateLw((WindowManagerPolicy.WindowState) null);
            }
            this.mDisplayContent.setInsetProvider(0, (WindowState) null, (TriConsumer<DisplayFrames, WindowState, Rect>) null);
        } else if (this.mNavigationBar == win) {
            this.mNavigationBar = null;
            this.mNavigationBarController.setWindow((WindowState) null);
            this.mDisplayContent.setInsetProvider(1, (WindowState) null, (TriConsumer<DisplayFrames, WindowState, Rect>) null);
        }
        if (this.mLastFocusedWindow == win) {
            this.mLastFocusedWindow = null;
        }
        this.mScreenDecorWindows.remove(win);
    }

    private int getStatusBarHeight(DisplayFrames displayFrames) {
        return Math.max(this.mStatusBarHeightForRotation[displayFrames.mRotation], displayFrames.mDisplayCutoutSafe.top);
    }

    public int selectAnimationLw(WindowState win, int transit) {
        if (win == this.mStatusBar) {
            boolean isKeyguard = (win.getAttrs().privateFlags & 1024) != 0;
            boolean expanded = win.getAttrs().height == -1 && win.getAttrs().width == -1;
            if (isKeyguard || expanded) {
                return -1;
            }
            if (transit == 2 || transit == 4) {
                return 17432757;
            }
            if (transit == 1 || transit == 3) {
                return 17432756;
            }
        } else if (win == this.mNavigationBar) {
            if (win.getAttrs().windowAnimations != 0) {
                return 0;
            }
            int i = this.mNavigationBarPosition;
            if (i == 4) {
                if (transit == 2 || transit == 4) {
                    if (this.mService.mPolicy.isKeyguardShowingAndNotOccluded()) {
                        return 17432751;
                    }
                    return 17432750;
                } else if (transit == 1 || transit == 3) {
                    return 17432749;
                }
            } else if (i == 2) {
                if (transit == 2 || transit == 4) {
                    return 17432755;
                }
                if (transit == 1 || transit == 3) {
                    return 17432754;
                }
            } else if (i == 1) {
                if (transit == 2 || transit == 4) {
                    return 17432753;
                }
                if (transit == 1 || transit == 3) {
                    return 17432752;
                }
            }
        } else if (win.getAttrs().type == 2034) {
            return selectDockedDividerAnimationLw(win, transit);
        }
        if (transit != 5) {
            return (win.getAttrs().type == 2023 && this.mDreamingLockscreen && transit == 1) ? -1 : 0;
        }
        if (win.hasAppShownWindows()) {
            return 17432730;
        }
    }

    private int selectDockedDividerAnimationLw(WindowState win, int transit) {
        int insets = this.mDisplayContent.getDockedDividerController().getContentInsets();
        Rect frame = win.getFrameLw();
        boolean behindNavBar = this.mNavigationBar != null && ((this.mNavigationBarPosition == 4 && frame.top + insets >= this.mNavigationBar.getFrameLw().top) || ((this.mNavigationBarPosition == 2 && frame.left + insets >= this.mNavigationBar.getFrameLw().left) || (this.mNavigationBarPosition == 1 && frame.right - insets <= this.mNavigationBar.getFrameLw().right)));
        boolean landscape = frame.height() > frame.width();
        boolean offscreen = (landscape && (frame.right - insets <= 0 || frame.left + insets >= win.getDisplayFrameLw().right)) || (!landscape && (frame.top - insets <= 0 || frame.bottom + insets >= win.getDisplayFrameLw().bottom));
        if (behindNavBar || offscreen) {
            return 0;
        }
        if (transit == 1 || transit == 3) {
            return 17432576;
        }
        if (transit == 2) {
            return 17432577;
        }
        return 0;
    }

    public void selectRotationAnimationLw(int[] anim) {
        if (!this.mScreenOnFully || !this.mService.mPolicy.okToAnimate()) {
            anim[0] = 17432849;
            anim[1] = 17432848;
            return;
        }
        WindowState windowState = this.mTopFullscreenOpaqueWindowState;
        if (windowState != null) {
            int animationHint = windowState.getRotationAnimationHint();
            if (animationHint < 0 && this.mTopIsFullscreen) {
                animationHint = this.mTopFullscreenOpaqueWindowState.getAttrs().rotationAnimation;
            }
            if (animationHint != 1) {
                if (animationHint == 2) {
                    anim[0] = 17432849;
                    anim[1] = 17432848;
                    return;
                } else if (animationHint != 3) {
                    anim[1] = 0;
                    anim[0] = 0;
                    return;
                }
            }
            anim[0] = 17432850;
            anim[1] = 17432848;
            return;
        }
        anim[1] = 0;
        anim[0] = 0;
    }

    public boolean validateRotationAnimationLw(int exitAnimId, int enterAnimId, boolean forceDefault) {
        switch (exitAnimId) {
            case 17432849:
            case 17432850:
                if (forceDefault) {
                    return false;
                }
                int[] anim = new int[2];
                selectRotationAnimationLw(anim);
                if (exitAnimId == anim[0] && enterAnimId == anim[1]) {
                    return true;
                }
                return false;
            default:
                return true;
        }
    }

    public int adjustSystemUiVisibilityLw(int visibility) {
        this.mStatusBarController.adjustSystemUiVisibilityLw(this.mLastSystemUiFlags, visibility);
        this.mNavigationBarController.adjustSystemUiVisibilityLw(this.mLastSystemUiFlags, visibility);
        this.mResettingSystemUiFlags &= visibility;
        return (~this.mResettingSystemUiFlags) & visibility & (~this.mForceClearedSystemUiFlags);
    }

    public boolean areSystemBarsForcedShownLw(WindowState windowState) {
        return this.mForceShowSystemBars;
    }

    public boolean getLayoutHintLw(WindowManager.LayoutParams attrs, Rect taskBounds, DisplayFrames displayFrames, boolean floatingStack, Rect outFrame, Rect outContentInsets, Rect outStableInsets, Rect outOutsets, DisplayCutout.ParcelableWrapper outDisplayCutout) {
        Rect sf;
        Rect cf;
        int outset;
        WindowManager.LayoutParams layoutParams = attrs;
        Rect rect = taskBounds;
        DisplayFrames displayFrames2 = displayFrames;
        Rect rect2 = outFrame;
        Rect rect3 = outOutsets;
        DisplayCutout.ParcelableWrapper parcelableWrapper = outDisplayCutout;
        int fl = PolicyControl.getWindowFlags((WindowState) null, layoutParams);
        int pfl = layoutParams.privateFlags;
        int requestedSysUiVis = PolicyControl.getSystemUiVisibility((WindowState) null, layoutParams);
        int sysUiVis = getImpliedSysUiFlagsForLayout(attrs) | requestedSysUiVis;
        int displayRotation = displayFrames2.mRotation;
        boolean screenDecor = true;
        if ((rect3 != null && shouldUseOutsets(layoutParams, fl)) && (outset = this.mWindowOutsetBottom) > 0) {
            if (displayRotation == 0) {
                rect3.bottom += outset;
            } else if (displayRotation == 1) {
                rect3.right += outset;
            } else if (displayRotation == 2) {
                rect3.top += outset;
            } else if (displayRotation == 3) {
                rect3.left += outset;
            }
        }
        boolean layoutInScreen = (fl & 256) != 0;
        boolean layoutInScreenAndInsetDecor = layoutInScreen && (65536 & fl) != 0;
        if ((pfl & DumpState.DUMP_CHANGES) == 0) {
            screenDecor = false;
        }
        if (!layoutInScreenAndInsetDecor || screenDecor) {
            Rect rect4 = outStableInsets;
            int i = requestedSysUiVis;
            Rect rect5 = outContentInsets;
            if (layoutInScreen) {
                rect2.set(displayFrames2.mUnrestricted);
            } else {
                rect2.set(displayFrames2.mStable);
            }
            if (rect != null) {
                rect2.intersect(rect);
            }
            outContentInsets.setEmpty();
            outStableInsets.setEmpty();
            parcelableWrapper.set(DisplayCutout.NO_CUTOUT);
            return this.mForceShowSystemBars;
        }
        if ((sysUiVis & 512) != 0) {
            rect2.set(displayFrames2.mUnrestricted);
        } else {
            rect2.set(displayFrames2.mRestricted);
        }
        if (floatingStack) {
            sf = null;
        } else {
            sf = displayFrames2.mStable;
        }
        if (floatingStack) {
            cf = null;
        } else if ((sysUiVis & 256) != 0) {
            if ((fl & 1024) != 0) {
                cf = displayFrames2.mStableFullscreen;
            } else {
                cf = displayFrames2.mStable;
            }
        } else if ((fl & 1024) == 0 && (33554432 & fl) == 0) {
            cf = displayFrames2.mCurrent;
        } else {
            cf = displayFrames2.mOverscan;
        }
        if (rect != null) {
            rect2.intersect(rect);
        }
        int i2 = requestedSysUiVis;
        InsetUtils.insetsBetweenFrames(rect2, cf, outContentInsets);
        Rect rect6 = cf;
        InsetUtils.insetsBetweenFrames(rect2, sf, outStableInsets);
        Rect rect7 = sf;
        parcelableWrapper.set(displayFrames2.mDisplayCutout.calculateRelativeTo(rect2).getDisplayCutout());
        return this.mForceShowSystemBars;
    }

    private static int getImpliedSysUiFlagsForLayout(WindowManager.LayoutParams attrs) {
        boolean forceWindowDrawsBarBackgrounds = (attrs.privateFlags & 131072) != 0 && attrs.height == -1 && attrs.width == -1;
        if ((attrs.flags & Integer.MIN_VALUE) != 0 || forceWindowDrawsBarBackgrounds) {
            return 0 | 512 | 1024;
        }
        return 0;
    }

    private static boolean shouldUseOutsets(WindowManager.LayoutParams attrs, int fl) {
        return attrs.type == 2013 || (33555456 & fl) != 0;
    }

    private final class HideNavInputEventReceiver extends InputEventReceiver {
        HideNavInputEventReceiver(InputChannel inputChannel, Looper looper) {
            super(inputChannel, looper);
        }

        /* Debug info: failed to restart local var, previous not found, register: 9 */
        public void onInputEvent(InputEvent event) {
            try {
                if ((event instanceof MotionEvent) && (event.getSource() & 2) != 0 && ((MotionEvent) event).getAction() == 0) {
                    boolean changed = false;
                    synchronized (DisplayPolicy.this.mLock) {
                        if (DisplayPolicy.this.mInputConsumer == null) {
                            finishInputEvent(event, false);
                            return;
                        }
                        int newVal = DisplayPolicy.this.mResettingSystemUiFlags | 2 | 1 | 4;
                        if (DisplayPolicy.this.mResettingSystemUiFlags != newVal) {
                            int unused = DisplayPolicy.this.mResettingSystemUiFlags = newVal;
                            changed = true;
                        }
                        int newVal2 = DisplayPolicy.this.mForceClearedSystemUiFlags | 2;
                        if (DisplayPolicy.this.mForceClearedSystemUiFlags != newVal2) {
                            int unused2 = DisplayPolicy.this.mForceClearedSystemUiFlags = newVal2;
                            changed = true;
                            DisplayPolicy.this.mHandler.postDelayed(DisplayPolicy.this.mClearHideNavigationFlag, 1000);
                        }
                        if (changed) {
                            DisplayPolicy.this.mDisplayContent.reevaluateStatusBarVisibility();
                        }
                    }
                }
                finishInputEvent(event, false);
            } catch (Throwable th) {
                finishInputEvent(event, false);
                throw th;
            }
        }
    }

    public void beginLayoutLw(DisplayFrames displayFrames, int uiMode) {
        WindowState windowState;
        DisplayFrames displayFrames2 = displayFrames;
        displayFrames.onBeginLayout();
        this.mSystemGestures.screenWidth = displayFrames2.mUnrestricted.width();
        this.mSystemGestures.screenHeight = displayFrames2.mUnrestricted.height();
        int sysui = this.mLastSystemUiFlags;
        boolean navVisible = (sysui & 2) == 0;
        boolean navTranslucent = (-2147450880 & sysui) != 0;
        boolean immersive = (sysui & 2048) != 0;
        boolean immersiveSticky = (sysui & 4096) != 0;
        boolean navAllowedHidden = immersive || immersiveSticky;
        boolean navTranslucent2 = navTranslucent & (!immersiveSticky);
        boolean isKeyguardShowing = isStatusBarKeyguard() && !this.mService.mPolicy.isKeyguardOccluded();
        boolean statusBarForcesShowingNavigation = (isKeyguardShowing || (windowState = this.mStatusBar) == null || (windowState.getAttrs().privateFlags & DumpState.DUMP_VOLUMES) == 0) ? false : true;
        if (navVisible || navAllowedHidden) {
            WindowManagerPolicy.InputConsumer inputConsumer = this.mInputConsumer;
            if (inputConsumer != null) {
                Handler handler = this.mHandler;
                handler.sendMessage(handler.obtainMessage(3, inputConsumer));
                this.mInputConsumer = null;
            }
        } else if (this.mInputConsumer == null && this.mStatusBar != null && canHideNavigationBar()) {
            this.mInputConsumer = this.mService.createInputConsumer(this.mHandler.getLooper(), "nav_input_consumer", (InputEventReceiver.Factory) new InputEventReceiver.Factory() {
                public final InputEventReceiver createInputEventReceiver(InputChannel inputChannel, Looper looper) {
                    return DisplayPolicy.this.lambda$beginLayoutLw$8$DisplayPolicy(inputChannel, looper);
                }
            }, displayFrames2.mDisplayId);
            InputManager.getInstance().setPointerIconType(0);
        }
        if (layoutNavigationBar(displayFrames, uiMode, navVisible | (!canHideNavigationBar()), navTranslucent2, navAllowedHidden, statusBarForcesShowingNavigation) || layoutStatusBar(displayFrames2, sysui, isKeyguardShowing)) {
            updateSystemUiVisibilityLw();
        }
        layoutScreenDecorWindows(displayFrames);
        if (displayFrames2.mDisplayCutoutSafe.top > displayFrames2.mUnrestricted.top) {
            displayFrames2.mDisplayCutoutSafe.top = Math.max(displayFrames2.mDisplayCutoutSafe.top, displayFrames2.mStable.top);
        }
        displayFrames2.mCurrent.inset(this.mForwardedInsets);
        displayFrames2.mContent.inset(this.mForwardedInsets);
    }

    public /* synthetic */ InputEventReceiver lambda$beginLayoutLw$8$DisplayPolicy(InputChannel x$0, Looper x$1) {
        return new HideNavInputEventReceiver(x$0, x$1);
    }

    private void layoutScreenDecorWindows(DisplayFrames displayFrames) {
        DisplayPolicy displayPolicy = this;
        DisplayFrames displayFrames2 = displayFrames;
        if (!displayPolicy.mScreenDecorWindows.isEmpty()) {
            sTmpRect.setEmpty();
            int displayId = displayFrames2.mDisplayId;
            Rect dockFrame = displayFrames2.mDock;
            int displayHeight = displayFrames2.mDisplayHeight;
            int displayWidth = displayFrames2.mDisplayWidth;
            int i = displayPolicy.mScreenDecorWindows.size() - 1;
            while (i >= 0) {
                WindowState w = displayPolicy.mScreenDecorWindows.valueAt(i);
                if (w.getDisplayId() == displayId && w.isVisibleLw()) {
                    w.getWindowFrames().setFrames(displayFrames2.mUnrestricted, displayFrames2.mUnrestricted, displayFrames2.mUnrestricted, displayFrames2.mUnrestricted, displayFrames2.mUnrestricted, sTmpRect, displayFrames2.mUnrestricted, displayFrames2.mUnrestricted);
                    w.getWindowFrames().setDisplayCutout(displayFrames2.mDisplayCutout);
                    w.computeFrameLw();
                    Rect frame = w.getFrameLw();
                    if (frame.left > 0 || frame.top > 0) {
                        if (frame.right < displayWidth || frame.bottom < displayHeight) {
                            Slog.w(TAG, "layoutScreenDecorWindows: Ignoring decor win=" + w + " not docked on one of the sides of the display. frame=" + frame + " displayWidth=" + displayWidth + " displayHeight=" + displayHeight);
                        } else if (frame.top <= 0) {
                            dockFrame.right = Math.min(frame.left, dockFrame.right);
                        } else if (frame.left <= 0) {
                            dockFrame.bottom = Math.min(frame.top, dockFrame.bottom);
                        } else {
                            Slog.w(TAG, "layoutScreenDecorWindows: Ignoring decor win=" + w + " not docked on right or bottom of display. frame=" + frame + " displayWidth=" + displayWidth + " displayHeight=" + displayHeight);
                        }
                    } else if (frame.bottom >= displayHeight) {
                        dockFrame.left = Math.max(frame.right, dockFrame.left);
                    } else if (frame.right >= displayWidth) {
                        dockFrame.top = Math.max(frame.bottom, dockFrame.top);
                    } else {
                        Slog.w(TAG, "layoutScreenDecorWindows: Ignoring decor win=" + w + " not docked on left or top of display. frame=" + frame + " displayWidth=" + displayWidth + " displayHeight=" + displayHeight);
                    }
                }
                i--;
                displayPolicy = this;
            }
            displayFrames2.mRestricted.set(dockFrame);
            displayFrames2.mCurrent.set(dockFrame);
            displayFrames2.mVoiceContent.set(dockFrame);
            displayFrames2.mSystem.set(dockFrame);
            displayFrames2.mContent.set(dockFrame);
            displayFrames2.mRestrictedOverscan.set(dockFrame);
        }
    }

    private boolean layoutStatusBar(DisplayFrames displayFrames, int sysui, boolean isKeyguardShowing) {
        boolean statusBarTranslucent = false;
        if (this.mStatusBar == null) {
            return false;
        }
        sTmpRect.setEmpty();
        WindowFrames windowFrames = this.mStatusBar.getWindowFrames();
        windowFrames.setFrames(displayFrames.mUnrestricted, displayFrames.mUnrestricted, displayFrames.mStable, displayFrames.mStable, displayFrames.mStable, sTmpRect, displayFrames.mStable, displayFrames.mStable);
        windowFrames.setDisplayCutout(displayFrames.mDisplayCutout);
        this.mStatusBar.computeFrameLw();
        displayFrames.mStable.top = displayFrames.mUnrestricted.top + this.mStatusBarHeightForRotation[displayFrames.mRotation];
        displayFrames.mStable.top = Math.max(displayFrames.mStable.top, displayFrames.mDisplayCutoutSafe.top);
        sTmpRect.set(this.mStatusBar.getContentFrameLw());
        sTmpRect.intersect(displayFrames.mDisplayCutoutSafe);
        sTmpRect.top = this.mStatusBar.getContentFrameLw().top;
        sTmpRect.bottom = displayFrames.mStable.top;
        this.mStatusBarController.setContentFrame(sTmpRect);
        boolean statusBarTransient = (67108864 & sysui) != 0;
        if ((1073741832 & sysui) != 0) {
            statusBarTranslucent = true;
        }
        if (this.mStatusBar.isVisibleLw() && !statusBarTransient) {
            Rect dockFrame = displayFrames.mDock;
            dockFrame.top = displayFrames.mStable.top;
            displayFrames.mContent.set(dockFrame);
            displayFrames.mVoiceContent.set(dockFrame);
            displayFrames.mCurrent.set(dockFrame);
            if (!statusBarTranslucent && !this.mStatusBarController.wasRecentlyTranslucent() && !this.mStatusBar.isAnimatingLw()) {
                displayFrames.mSystem.top = displayFrames.mStable.top;
            }
        }
        return this.mStatusBarController.checkHiddenLw();
    }

    private boolean layoutNavigationBar(DisplayFrames displayFrames, int uiMode, boolean navVisible, boolean navTranslucent, boolean navAllowedHidden, boolean statusBarForcesShowingNavigation) {
        DisplayFrames displayFrames2 = displayFrames;
        int i = uiMode;
        boolean z = statusBarForcesShowingNavigation;
        if (this.mNavigationBar == null) {
            this.mNavigationBarPosition = navigationBarPosition(displayFrames2.mDisplayWidth, displayFrames2.mDisplayHeight, displayFrames2.mRotation);
            return false;
        }
        Rect navigationFrame = sTmpNavFrame;
        boolean transientNavBarShowing = this.mNavigationBarController.isTransientShowing();
        int rotation = displayFrames2.mRotation;
        int displayHeight = displayFrames2.mDisplayHeight;
        int displayWidth = displayFrames2.mDisplayWidth;
        Rect dockFrame = displayFrames2.mDock;
        this.mNavigationBarPosition = navigationBarPosition(displayWidth, displayHeight, rotation);
        Rect cutoutSafeUnrestricted = sTmpRect;
        cutoutSafeUnrestricted.set(displayFrames2.mUnrestricted);
        cutoutSafeUnrestricted.intersectUnchecked(displayFrames2.mDisplayCutoutSafe);
        int left = this.mNavigationBarPosition;
        if (left == 4) {
            int top = cutoutSafeUnrestricted.bottom - getNavigationBarHeight(rotation, i);
            navigationFrame.set(0, cutoutSafeUnrestricted.bottom - getNavigationBarFrameHeight(rotation, i), displayWidth, displayFrames2.mUnrestricted.bottom);
            Rect rect = displayFrames2.mStable;
            displayFrames2.mStableFullscreen.bottom = top;
            rect.bottom = top;
            if (transientNavBarShowing) {
                this.mNavigationBarController.setBarShowingLw(true);
            } else if (navVisible) {
                this.mNavigationBarController.setBarShowingLw(true);
                Rect rect2 = displayFrames2.mRestricted;
                displayFrames2.mRestrictedOverscan.bottom = top;
                rect2.bottom = top;
                dockFrame.bottom = top;
            } else {
                this.mNavigationBarController.setBarShowingLw(z);
            }
            if (navVisible && !navTranslucent && !navAllowedHidden && !this.mNavigationBar.isAnimatingLw() && !this.mNavigationBarController.wasRecentlyTranslucent()) {
                displayFrames2.mSystem.bottom = top;
            }
        } else if (left == 2) {
            int left2 = cutoutSafeUnrestricted.right - getNavigationBarWidth(rotation, i);
            navigationFrame.set(left2, 0, displayFrames2.mUnrestricted.right, displayHeight);
            Rect rect3 = displayFrames2.mStable;
            displayFrames2.mStableFullscreen.right = left2;
            rect3.right = left2;
            if (transientNavBarShowing) {
                this.mNavigationBarController.setBarShowingLw(true);
            } else if (navVisible) {
                this.mNavigationBarController.setBarShowingLw(true);
                Rect rect4 = displayFrames2.mRestricted;
                displayFrames2.mRestrictedOverscan.right = left2;
                rect4.right = left2;
                dockFrame.right = left2;
            } else {
                this.mNavigationBarController.setBarShowingLw(z);
            }
            if (navVisible && !navTranslucent && !navAllowedHidden && !this.mNavigationBar.isAnimatingLw() && !this.mNavigationBarController.wasRecentlyTranslucent()) {
                displayFrames2.mSystem.right = left2;
            }
        } else if (left == 1) {
            int right = cutoutSafeUnrestricted.left + getNavigationBarWidth(rotation, i);
            navigationFrame.set(displayFrames2.mUnrestricted.left, 0, right, displayHeight);
            Rect rect5 = displayFrames2.mStable;
            displayFrames2.mStableFullscreen.left = right;
            rect5.left = right;
            if (transientNavBarShowing) {
                this.mNavigationBarController.setBarShowingLw(true);
            } else if (navVisible) {
                this.mNavigationBarController.setBarShowingLw(true);
                Rect rect6 = displayFrames2.mRestricted;
                displayFrames2.mRestrictedOverscan.left = right;
                rect6.left = right;
                dockFrame.left = right;
            } else {
                this.mNavigationBarController.setBarShowingLw(z);
            }
            if (navVisible && !navTranslucent && !navAllowedHidden && !this.mNavigationBar.isAnimatingLw() && !this.mNavigationBarController.wasRecentlyTranslucent()) {
                displayFrames2.mSystem.left = right;
            }
        }
        displayFrames2.mCurrent.set(dockFrame);
        displayFrames2.mVoiceContent.set(dockFrame);
        displayFrames2.mContent.set(dockFrame);
        sTmpRect.setEmpty();
        WindowFrames windowFrames = this.mNavigationBar.getWindowFrames();
        Rect rect7 = displayFrames2.mDisplayCutoutSafe;
        Rect rect8 = sTmpRect;
        Rect rect9 = cutoutSafeUnrestricted;
        Rect cutoutSafeUnrestricted2 = rect7;
        Rect rect10 = dockFrame;
        int i2 = displayWidth;
        Rect rect11 = rect8;
        int i3 = displayHeight;
        int i4 = rotation;
        windowFrames.setFrames(navigationFrame, navigationFrame, navigationFrame, cutoutSafeUnrestricted2, navigationFrame, rect11, navigationFrame, displayFrames2.mDisplayCutoutSafe);
        this.mNavigationBar.getWindowFrames().setDisplayCutout(displayFrames2.mDisplayCutout);
        this.mNavigationBar.computeFrameLw();
        this.mNavigationBarController.setContentFrame(this.mNavigationBar.getContentFrameLw());
        return this.mNavigationBarController.checkHiddenLw();
    }

    private void setAttachedWindowFrames(WindowState win, int fl, int adjust, WindowState attached, boolean insetDecors, Rect pf, Rect df, Rect of, Rect cf, Rect vf, DisplayFrames displayFrames) {
        int i = fl;
        Rect rect = df;
        Rect rect2 = of;
        Rect rect3 = cf;
        Rect rect4 = vf;
        DisplayFrames displayFrames2 = displayFrames;
        if (win.isInputMethodTarget() || !attached.isInputMethodTarget()) {
            Rect parentDisplayFrame = attached.getDisplayFrameLw();
            Rect parentOverscan = attached.getOverscanFrameLw();
            WindowManager.LayoutParams attachedAttrs = attached.mAttrs;
            if ((attachedAttrs.privateFlags & 131072) != 0 && (attachedAttrs.flags & Integer.MIN_VALUE) == 0 && (attachedAttrs.systemUiVisibility & 512) == 0) {
                parentOverscan = new Rect(parentOverscan);
                parentOverscan.intersect(displayFrames2.mRestrictedOverscan);
                parentDisplayFrame = new Rect(parentDisplayFrame);
                parentDisplayFrame.intersect(displayFrames2.mRestrictedOverscan);
            }
            if (adjust != 16) {
                rect3.set((1073741824 & i) != 0 ? attached.getContentFrameLw() : parentOverscan);
            } else {
                rect3.set(attached.getContentFrameLw());
                if (attached.isVoiceInteraction()) {
                    rect3.intersectUnchecked(displayFrames2.mVoiceContent);
                } else if (win.isInputMethodTarget() || attached.isInputMethodTarget()) {
                    rect3.intersectUnchecked(displayFrames2.mContent);
                }
            }
            rect.set(insetDecors ? parentDisplayFrame : rect3);
            rect2.set(insetDecors ? parentOverscan : rect3);
            rect4.set(attached.getVisibleFrameLw());
        } else {
            rect4.set(displayFrames2.mDock);
            rect3.set(displayFrames2.mDock);
            rect2.set(displayFrames2.mDock);
            rect.set(displayFrames2.mDock);
            int i2 = adjust;
            WindowState windowState = attached;
        }
        pf.set((i & 256) == 0 ? attached.getFrameLw() : rect);
    }

    private void applyStableConstraints(int sysui, int fl, Rect r, DisplayFrames displayFrames) {
        if ((sysui & 256) != 0) {
            if ((fl & 1024) != 0) {
                r.intersectUnchecked(displayFrames.mStableFullscreen);
            } else {
                r.intersectUnchecked(displayFrames.mStable);
            }
        }
    }

    private boolean canReceiveInput(WindowState win) {
        if (!(((win.getAttrs().flags & 8) != 0) ^ ((win.getAttrs().flags & 131072) != 0))) {
            return true;
        }
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0075, code lost:
        r7 = r12.mNavigationBar;
     */
    /* JADX WARNING: Removed duplicated region for block: B:160:0x0440  */
    /* JADX WARNING: Removed duplicated region for block: B:170:0x0469  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x0479  */
    /* JADX WARNING: Removed duplicated region for block: B:174:0x048d  */
    /* JADX WARNING: Removed duplicated region for block: B:259:0x0722  */
    /* JADX WARNING: Removed duplicated region for block: B:260:0x0728  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void layoutWindowLw(com.android.server.wm.WindowState r47, com.android.server.wm.WindowState r48, com.android.server.wm.DisplayFrames r49) {
        /*
            r46 = this;
            r12 = r46
            r13 = r47
            r14 = r49
            com.android.server.wm.WindowState r0 = r12.mStatusBar
            if (r13 != r0) goto L_0x0015
            boolean r0 = r46.canReceiveInput(r47)
            if (r0 == 0) goto L_0x0011
            goto L_0x0015
        L_0x0011:
            r3 = r12
            r0 = r13
            goto L_0x08bc
        L_0x0015:
            com.android.server.wm.WindowState r0 = r12.mNavigationBar
            if (r13 == r0) goto L_0x08ba
            android.util.ArraySet<com.android.server.wm.WindowState> r0 = r12.mScreenDecorWindows
            boolean r0 = r0.contains(r13)
            if (r0 == 0) goto L_0x0025
            r3 = r12
            r0 = r13
            goto L_0x08bc
        L_0x0025:
            android.view.WindowManager$LayoutParams r15 = r47.getAttrs()
            boolean r16 = r47.isDefaultDisplay()
            int r11 = r15.type
            int r10 = com.android.server.wm.PolicyControl.getWindowFlags(r13, r15)
            int r9 = r15.privateFlags
            int r8 = r15.softInputMode
            r0 = 0
            int r17 = com.android.server.wm.PolicyControl.getSystemUiVisibility(r0, r15)
            int r0 = getImpliedSysUiFlagsForLayout(r15)
            r7 = r17 | r0
            com.android.server.wm.WindowFrames r6 = r47.getWindowFrames()
            r5 = 0
            r6.setHasOutsets(r5)
            android.graphics.Rect r0 = sTmpLastParentFrame
            android.graphics.Rect r1 = r6.mParentFrame
            r0.set(r1)
            android.graphics.Rect r4 = r6.mParentFrame
            android.graphics.Rect r3 = r6.mDisplayFrame
            android.graphics.Rect r2 = r6.mOverscanFrame
            android.graphics.Rect r1 = r6.mContentFrame
            android.graphics.Rect r0 = r6.mVisibleFrame
            android.graphics.Rect r5 = r6.mDecorFrame
            r19 = r9
            android.graphics.Rect r9 = r6.mStableFrame
            r5.setEmpty()
            r20 = r7
            r7 = 0
            r6.setParentFrameWasClippedByDisplayCutout(r7)
            com.android.server.wm.utils.WmDisplayCutout r7 = r14.mDisplayCutout
            r6.setDisplayCutout(r7)
            boolean r7 = r46.hasNavigationBar()
            if (r7 == 0) goto L_0x0081
            com.android.server.wm.WindowState r7 = r12.mNavigationBar
            if (r7 == 0) goto L_0x0081
            boolean r7 = r7.isVisibleLw()
            if (r7 == 0) goto L_0x0081
            r7 = 1
            goto L_0x0082
        L_0x0081:
            r7 = 0
        L_0x0082:
            r21 = r7
            r7 = r8 & 240(0xf0, float:3.36E-43)
            r13 = r10 & 1024(0x400, float:1.435E-42)
            if (r13 != 0) goto L_0x0091
            r13 = r17 & 4
            if (r13 == 0) goto L_0x008f
            goto L_0x0091
        L_0x008f:
            r13 = 0
            goto L_0x0092
        L_0x0091:
            r13 = 1
        L_0x0092:
            r23 = r8
            r8 = r10 & 256(0x100, float:3.59E-43)
            r24 = r13
            r13 = 256(0x100, float:3.59E-43)
            if (r8 != r13) goto L_0x009e
            r8 = 1
            goto L_0x009f
        L_0x009e:
            r8 = 0
        L_0x009f:
            r25 = r8
            r8 = 65536(0x10000, float:9.18355E-41)
            r8 = r8 & r10
            r13 = 65536(0x10000, float:9.18355E-41)
            if (r8 != r13) goto L_0x00aa
            r8 = 1
            goto L_0x00ab
        L_0x00aa:
            r8 = 0
        L_0x00ab:
            r13 = r8
            android.graphics.Rect r8 = r14.mStable
            r9.set(r8)
            r8 = 2011(0x7db, float:2.818E-42)
            if (r11 != r8) goto L_0x0170
            android.graphics.Rect r8 = r14.mDock
            r0.set(r8)
            android.graphics.Rect r8 = r14.mDock
            r1.set(r8)
            android.graphics.Rect r8 = r14.mDock
            r2.set(r8)
            android.graphics.Rect r8 = r14.mDock
            r3.set(r8)
            android.graphics.Rect r8 = r6.mParentFrame
            r29 = r6
            android.graphics.Rect r6 = r14.mDock
            r8.set(r6)
            android.graphics.Rect r6 = r14.mUnrestricted
            int r6 = r6.bottom
            r2.bottom = r6
            r3.bottom = r6
            r4.bottom = r6
            android.graphics.Rect r6 = r14.mStable
            int r6 = r6.bottom
            r0.bottom = r6
            r1.bottom = r6
            com.android.server.wm.WindowState r6 = r12.mStatusBar
            if (r6 == 0) goto L_0x0119
            com.android.server.wm.WindowState r8 = r12.mFocusedWindow
            if (r8 != r6) goto L_0x0119
            boolean r6 = r12.canReceiveInput(r6)
            if (r6 == 0) goto L_0x0119
            int r6 = r12.mNavigationBarPosition
            r8 = 2
            if (r6 != r8) goto L_0x0107
            android.graphics.Rect r6 = r14.mStable
            int r6 = r6.right
            r0.right = r6
            r1.right = r6
            r2.right = r6
            r3.right = r6
            r4.right = r6
            r8 = 1
            goto L_0x011a
        L_0x0107:
            r8 = 1
            if (r6 != r8) goto L_0x011a
            android.graphics.Rect r6 = r14.mStable
            int r6 = r6.left
            r0.left = r6
            r1.left = r6
            r2.left = r6
            r3.left = r6
            r4.left = r6
            goto L_0x011a
        L_0x0119:
            r8 = 1
        L_0x011a:
            int r6 = r12.mNavigationBarPosition
            r8 = 4
            if (r6 != r8) goto L_0x0152
            int r6 = r14.mRotation
            com.android.server.wm.WindowManagerService r8 = r12.mService
            com.android.server.policy.WindowManagerPolicy r8 = r8.mPolicy
            int r8 = r8.getUiMode()
            int r30 = r12.getNavigationBarFrameHeight(r6, r8)
            int r31 = r12.getNavigationBarHeight(r6, r8)
            int r30 = r30 - r31
            if (r30 <= 0) goto L_0x0150
            r31 = r6
            int r6 = r1.bottom
            int r6 = r6 - r30
            r1.bottom = r6
            int r6 = r9.bottom
            int r6 = r6 - r30
            r9.bottom = r6
            int r6 = r0.bottom
            int r6 = r6 - r30
            r0.bottom = r6
            int r6 = r5.bottom
            int r6 = r6 - r30
            r5.bottom = r6
            goto L_0x0152
        L_0x0150:
            r31 = r6
        L_0x0152:
            r6 = 80
            r15.gravity = r6
            r26 = r5
            r6 = r7
            r34 = r13
            r27 = r15
            r15 = r20
            r20 = r23
            r39 = r29
            r8 = 2017(0x7e1, float:2.826E-42)
            r7 = r0
            r5 = r4
            r23 = r9
            r0 = r10
            r13 = r11
            r9 = 1
            r4 = r3
            r3 = r12
            goto L_0x072b
        L_0x0170:
            r29 = r6
            r6 = 2031(0x7ef, float:2.846E-42)
            r8 = 16
            if (r11 != r6) goto L_0x01d4
            android.graphics.Rect r6 = r14.mUnrestricted
            r2.set(r6)
            android.graphics.Rect r6 = r14.mUnrestricted
            r3.set(r6)
            android.graphics.Rect r6 = r14.mUnrestricted
            r4.set(r6)
            if (r7 == r8) goto L_0x018f
            android.graphics.Rect r6 = r14.mDock
            r1.set(r6)
            goto L_0x0194
        L_0x018f:
            android.graphics.Rect r6 = r14.mContent
            r1.set(r6)
        L_0x0194:
            r6 = 48
            if (r7 == r6) goto L_0x01b7
            android.graphics.Rect r6 = r14.mCurrent
            r0.set(r6)
            r26 = r5
            r6 = r7
            r34 = r13
            r27 = r15
            r15 = r20
            r20 = r23
            r39 = r29
            r8 = 2017(0x7e1, float:2.826E-42)
            r7 = r0
            r5 = r4
            r23 = r9
            r0 = r10
            r13 = r11
            r9 = 1
            r4 = r3
            r3 = r12
            goto L_0x072b
        L_0x01b7:
            r0.set(r1)
            r26 = r5
            r6 = r7
            r34 = r13
            r27 = r15
            r15 = r20
            r20 = r23
            r39 = r29
            r8 = 2017(0x7e1, float:2.826E-42)
            r7 = r0
            r5 = r4
            r23 = r9
            r0 = r10
            r13 = r11
            r9 = 1
            r4 = r3
            r3 = r12
            goto L_0x072b
        L_0x01d4:
            r6 = 2013(0x7dd, float:2.821E-42)
            if (r11 != r6) goto L_0x020e
            r6 = r0
            r0 = r46
            r8 = r1
            r1 = r49
            r31 = r2
            r2 = r4
            r32 = r3
            r33 = r9
            r9 = r4
            r4 = r31
            r34 = r13
            r13 = r5
            r5 = r8
            r0.layoutWallpaper(r1, r2, r3, r4, r5)
            r1 = r8
            r5 = r9
            r0 = r10
            r3 = r12
            r26 = r13
            r27 = r15
            r15 = r20
            r20 = r23
            r39 = r29
            r2 = r31
            r4 = r32
            r23 = r33
            r8 = 2017(0x7e1, float:2.826E-42)
            r9 = 1
            r13 = r11
            r45 = r7
            r7 = r6
            r6 = r45
            goto L_0x072b
        L_0x020e:
            r6 = r0
            r31 = r2
            r32 = r3
            r33 = r9
            r34 = r13
            r9 = r4
            r13 = r5
            r5 = r1
            com.android.server.wm.WindowState r0 = r12.mStatusBar
            r4 = r47
            r3 = 1
            if (r4 != r0) goto L_0x028a
            android.graphics.Rect r0 = r14.mUnrestricted
            r2 = r31
            r2.set(r0)
            android.graphics.Rect r0 = r14.mUnrestricted
            r1 = r32
            r1.set(r0)
            android.graphics.Rect r0 = r14.mUnrestricted
            r9.set(r0)
            android.graphics.Rect r0 = r14.mStable
            r5.set(r0)
            android.graphics.Rect r0 = r14.mStable
            r6.set(r0)
            if (r7 != r8) goto L_0x0262
            android.graphics.Rect r0 = r14.mContent
            int r0 = r0.bottom
            r5.bottom = r0
            r4 = r1
            r1 = r5
            r5 = r9
            r0 = r10
            r26 = r13
            r27 = r15
            r15 = r20
            r20 = r23
            r39 = r29
            r23 = r33
            r8 = 2017(0x7e1, float:2.826E-42)
            r9 = r3
            r13 = r11
            r3 = r12
            r45 = r7
            r7 = r6
            r6 = r45
            goto L_0x072b
        L_0x0262:
            android.graphics.Rect r0 = r14.mDock
            int r0 = r0.bottom
            r5.bottom = r0
            android.graphics.Rect r0 = r14.mContent
            int r0 = r0.bottom
            r6.bottom = r0
            r4 = r1
            r1 = r5
            r5 = r9
            r0 = r10
            r26 = r13
            r27 = r15
            r15 = r20
            r20 = r23
            r39 = r29
            r23 = r33
            r8 = 2017(0x7e1, float:2.826E-42)
            r9 = r3
            r13 = r11
            r3 = r12
            r45 = r7
            r7 = r6
            r6 = r45
            goto L_0x072b
        L_0x028a:
            r2 = r31
            r1 = r32
            android.graphics.Rect r0 = r14.mSystem
            r13.set(r0)
            int r0 = r15.privateFlags
            r0 = r0 & 512(0x200, float:7.175E-43)
            if (r0 == 0) goto L_0x029b
            r0 = r3
            goto L_0x029c
        L_0x029b:
            r0 = 0
        L_0x029c:
            r18 = r0
            if (r11 < r3) goto L_0x02a6
            r0 = 99
            if (r11 > r0) goto L_0x02a6
            r0 = r3
            goto L_0x02a7
        L_0x02a6:
            r0 = 0
        L_0x02a7:
            r22 = r0
            com.android.server.wm.WindowState r0 = r12.mTopFullscreenOpaqueWindowState
            if (r4 != r0) goto L_0x02b5
            boolean r0 = r47.isAnimatingLw()
            if (r0 != 0) goto L_0x02b5
            r0 = r3
            goto L_0x02b6
        L_0x02b5:
            r0 = 0
        L_0x02b6:
            r31 = r0
            if (r22 == 0) goto L_0x030b
            if (r18 != 0) goto L_0x030b
            if (r31 != 0) goto L_0x030b
            r0 = r20 & 4
            if (r0 != 0) goto L_0x02dd
            r0 = r10 & 1024(0x400, float:1.435E-42)
            if (r0 != 0) goto L_0x02dd
            r0 = 67108864(0x4000000, float:1.5046328E-36)
            r0 = r0 & r10
            if (r0 != 0) goto L_0x02dd
            r0 = -2147483648(0xffffffff80000000, float:-0.0)
            r32 = r10 & r0
            if (r32 != 0) goto L_0x02dd
            r0 = 131072(0x20000, float:1.83671E-40)
            r0 = r19 & r0
            if (r0 != 0) goto L_0x02dd
            android.graphics.Rect r0 = r14.mStable
            int r0 = r0.top
            r13.top = r0
        L_0x02dd:
            r0 = 134217728(0x8000000, float:3.85186E-34)
            r0 = r0 & r10
            if (r0 != 0) goto L_0x0308
            r0 = r20 & 2
            if (r0 != 0) goto L_0x0308
            r28 = -2147483648(0xffffffff80000000, float:-0.0)
            r0 = r10 & r28
            if (r0 != 0) goto L_0x030d
            r0 = 131072(0x20000, float:1.83671E-40)
            r0 = r19 & r0
            if (r0 != 0) goto L_0x030d
            com.android.server.wm.DisplayContent r0 = r12.mDisplayContent
            r3 = 5
            boolean r0 = r0.isStackVisible(r3)
            if (r0 != 0) goto L_0x030d
            android.graphics.Rect r0 = r14.mStable
            int r0 = r0.bottom
            r13.bottom = r0
            android.graphics.Rect r0 = r14.mStable
            int r0 = r0.right
            r13.right = r0
            goto L_0x030d
        L_0x0308:
            r28 = -2147483648(0xffffffff80000000, float:-0.0)
            goto L_0x030d
        L_0x030b:
            r28 = -2147483648(0xffffffff80000000, float:-0.0)
        L_0x030d:
            r0 = 2014(0x7de, float:2.822E-42)
            r3 = 1999(0x7cf, float:2.801E-42)
            if (r25 == 0) goto L_0x049f
            if (r34 == 0) goto L_0x049f
            if (r48 == 0) goto L_0x0363
            r8 = 1
            r0 = r46
            r3 = r1
            r1 = r47
            r35 = r2
            r2 = r10
            r36 = r3
            r3 = r7
            r4 = r48
            r32 = r5
            r5 = r8
            r8 = r29
            r29 = r6
            r6 = r9
            r38 = r7
            r37 = r20
            r7 = r36
            r39 = r8
            r20 = r23
            r8 = r35
            r40 = r9
            r23 = r33
            r9 = r32
            r41 = r10
            r10 = r29
            r26 = r13
            r13 = r11
            r11 = r49
            r0.setAttachedWindowFrames(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            r3 = r12
            r27 = r15
            r7 = r29
            r1 = r32
            r2 = r35
            r4 = r36
            r15 = r37
            r6 = r38
            r5 = r40
            r0 = r41
            r8 = 2017(0x7e1, float:2.826E-42)
            r9 = 1
            goto L_0x072b
        L_0x0363:
            r36 = r1
            r35 = r2
            r32 = r5
            r38 = r7
            r40 = r9
            r41 = r10
            r26 = r13
            r37 = r20
            r20 = r23
            r39 = r29
            r23 = r33
            r29 = r6
            r13 = r11
            if (r13 == r0) goto L_0x03f4
            r11 = 2017(0x7e1, float:2.826E-42)
            if (r13 != r11) goto L_0x038f
            r7 = r35
            r6 = r36
            r4 = r37
            r5 = r40
            r10 = r41
            r9 = 1
            goto L_0x0401
        L_0x038f:
            r0 = 33554432(0x2000000, float:9.403955E-38)
            r10 = r41
            r0 = r0 & r10
            if (r0 == 0) goto L_0x03bb
            r9 = 1
            if (r13 < r9) goto L_0x03b4
            if (r13 > r3) goto L_0x03b4
            android.graphics.Rect r0 = r14.mOverscan
            r7 = r35
            r7.set(r0)
            android.graphics.Rect r0 = r14.mOverscan
            r6 = r36
            r6.set(r0)
            android.graphics.Rect r0 = r14.mOverscan
            r5 = r40
            r5.set(r0)
            r4 = r37
            goto L_0x043c
        L_0x03b4:
            r7 = r35
            r6 = r36
            r5 = r40
            goto L_0x03c2
        L_0x03bb:
            r7 = r35
            r6 = r36
            r5 = r40
            r9 = 1
        L_0x03c2:
            r4 = r37
            r0 = r4 & 512(0x200, float:7.175E-43)
            if (r0 == 0) goto L_0x03e4
            if (r13 < r9) goto L_0x03cc
            if (r13 <= r3) goto L_0x03d4
        L_0x03cc:
            r0 = 2020(0x7e4, float:2.83E-42)
            if (r13 == r0) goto L_0x03d4
            r0 = 2009(0x7d9, float:2.815E-42)
            if (r13 != r0) goto L_0x03e4
        L_0x03d4:
            android.graphics.Rect r0 = r14.mOverscan
            r6.set(r0)
            android.graphics.Rect r0 = r14.mOverscan
            r5.set(r0)
            android.graphics.Rect r0 = r14.mUnrestricted
            r7.set(r0)
            goto L_0x043c
        L_0x03e4:
            android.graphics.Rect r0 = r14.mRestrictedOverscan
            r6.set(r0)
            android.graphics.Rect r0 = r14.mRestrictedOverscan
            r5.set(r0)
            android.graphics.Rect r0 = r14.mUnrestricted
            r7.set(r0)
            goto L_0x043c
        L_0x03f4:
            r7 = r35
            r6 = r36
            r4 = r37
            r5 = r40
            r10 = r41
            r9 = 1
            r11 = 2017(0x7e1, float:2.826E-42)
        L_0x0401:
            if (r21 == 0) goto L_0x0406
            android.graphics.Rect r0 = r14.mDock
            goto L_0x0408
        L_0x0406:
            android.graphics.Rect r0 = r14.mUnrestricted
        L_0x0408:
            int r0 = r0.left
            r7.left = r0
            r6.left = r0
            r5.left = r0
            android.graphics.Rect r0 = r14.mUnrestricted
            int r0 = r0.top
            r7.top = r0
            r6.top = r0
            r5.top = r0
            if (r21 == 0) goto L_0x0421
            android.graphics.Rect r0 = r14.mRestricted
            int r0 = r0.right
            goto L_0x0425
        L_0x0421:
            android.graphics.Rect r0 = r14.mUnrestricted
            int r0 = r0.right
        L_0x0425:
            r7.right = r0
            r6.right = r0
            r5.right = r0
            if (r21 == 0) goto L_0x0432
            android.graphics.Rect r0 = r14.mRestricted
            int r0 = r0.bottom
            goto L_0x0436
        L_0x0432:
            android.graphics.Rect r0 = r14.mUnrestricted
            int r0 = r0.bottom
        L_0x0436:
            r7.bottom = r0
            r6.bottom = r0
            r5.bottom = r0
        L_0x043c:
            r0 = r10 & 1024(0x400, float:1.435E-42)
            if (r0 != 0) goto L_0x0469
            boolean r0 = r47.isVoiceInteraction()
            if (r0 == 0) goto L_0x0450
            android.graphics.Rect r0 = r14.mVoiceContent
            r2 = r32
            r2.set(r0)
            r1 = r38
            goto L_0x0472
        L_0x0450:
            r2 = r32
            int r0 = android.view.ViewRootImpl.sNewInsetsMode
            if (r0 != 0) goto L_0x0461
            r1 = r38
            if (r1 == r8) goto L_0x045b
            goto L_0x0463
        L_0x045b:
            android.graphics.Rect r0 = r14.mContent
            r2.set(r0)
            goto L_0x0472
        L_0x0461:
            r1 = r38
        L_0x0463:
            android.graphics.Rect r0 = r14.mDock
            r2.set(r0)
            goto L_0x0472
        L_0x0469:
            r2 = r32
            r1 = r38
            android.graphics.Rect r0 = r14.mRestricted
            r2.set(r0)
        L_0x0472:
            r12.applyStableConstraints(r4, r10, r2, r14)
            r0 = 48
            if (r1 == r0) goto L_0x048d
            android.graphics.Rect r0 = r14.mCurrent
            r3 = r29
            r3.set(r0)
            r0 = r10
            r8 = r11
            r27 = r15
            r15 = r4
            r4 = r6
            r6 = r1
            r1 = r2
            r2 = r7
            r7 = r3
            r3 = r12
            goto L_0x072b
        L_0x048d:
            r3 = r29
            r3.set(r2)
            r0 = r10
            r8 = r11
            r27 = r15
            r15 = r4
            r4 = r6
            r6 = r1
            r1 = r2
            r2 = r7
            r7 = r3
            r3 = r12
            goto L_0x072b
        L_0x049f:
            r26 = r13
            r4 = r20
            r20 = r23
            r39 = r29
            r23 = r33
            r29 = r6
            r13 = r11
            r11 = 2017(0x7e1, float:2.826E-42)
            r6 = r1
            r1 = r7
            r7 = r2
            r2 = r5
            r5 = r9
            r9 = 1
            if (r25 != 0) goto L_0x05d8
            r9 = r4 & 1536(0x600, float:2.152E-42)
            if (r9 == 0) goto L_0x04c8
            r44 = r10
            r12 = r11
            r27 = r15
            r15 = r4
            r4 = r6
            r6 = r1
            r1 = r2
            r2 = r7
            r7 = r29
            goto L_0x05e4
        L_0x04c8:
            if (r48 == 0) goto L_0x0509
            r8 = 0
            r0 = r46
            r9 = r1
            r1 = r47
            r3 = r2
            r2 = r10
            r32 = r3
            r3 = r9
            r27 = r15
            r15 = r4
            r4 = r48
            r40 = r5
            r5 = r8
            r8 = r6
            r6 = r40
            r35 = r7
            r7 = r8
            r42 = r8
            r8 = r35
            r43 = r9
            r9 = r32
            r44 = r10
            r10 = r29
            r12 = r11
            r11 = r49
            r0.setAttachedWindowFrames(r1, r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            r3 = r46
            r8 = r12
            r7 = r29
            r1 = r32
            r2 = r35
            r5 = r40
            r4 = r42
            r6 = r43
            r0 = r44
            r9 = 1
            goto L_0x072b
        L_0x0509:
            r43 = r1
            r32 = r2
            r40 = r5
            r42 = r6
            r35 = r7
            r44 = r10
            r12 = r11
            r27 = r15
            r15 = r4
            if (r13 != r0) goto L_0x0543
            android.graphics.Rect r0 = r14.mRestricted
            r1 = r32
            r1.set(r0)
            android.graphics.Rect r0 = r14.mRestricted
            r2 = r35
            r2.set(r0)
            android.graphics.Rect r0 = r14.mRestricted
            r4 = r42
            r4.set(r0)
            android.graphics.Rect r0 = r14.mRestricted
            r5 = r40
            r5.set(r0)
            r3 = r46
            r8 = r12
            r7 = r29
            r6 = r43
            r0 = r44
            r9 = 1
            goto L_0x072b
        L_0x0543:
            r1 = r32
            r2 = r35
            r5 = r40
            r4 = r42
            r0 = 2005(0x7d5, float:2.81E-42)
            if (r13 == r0) goto L_0x05b8
            r0 = 2003(0x7d3, float:2.807E-42)
            if (r13 != r0) goto L_0x0558
            r7 = r29
            r6 = r43
            goto L_0x05bc
        L_0x0558:
            android.graphics.Rect r0 = r14.mContent
            r5.set(r0)
            boolean r0 = r47.isVoiceInteraction()
            if (r0 == 0) goto L_0x0575
            android.graphics.Rect r0 = r14.mVoiceContent
            r1.set(r0)
            android.graphics.Rect r0 = r14.mVoiceContent
            r2.set(r0)
            android.graphics.Rect r0 = r14.mVoiceContent
            r4.set(r0)
            r6 = r43
            goto L_0x0598
        L_0x0575:
            r6 = r43
            if (r6 == r8) goto L_0x0589
            android.graphics.Rect r0 = r14.mDock
            r1.set(r0)
            android.graphics.Rect r0 = r14.mDock
            r2.set(r0)
            android.graphics.Rect r0 = r14.mDock
            r4.set(r0)
            goto L_0x0598
        L_0x0589:
            android.graphics.Rect r0 = r14.mContent
            r1.set(r0)
            android.graphics.Rect r0 = r14.mContent
            r2.set(r0)
            android.graphics.Rect r0 = r14.mContent
            r4.set(r0)
        L_0x0598:
            r0 = 48
            if (r6 == r0) goto L_0x05ab
            android.graphics.Rect r0 = r14.mCurrent
            r7 = r29
            r7.set(r0)
            r3 = r46
            r8 = r12
            r0 = r44
            r9 = 1
            goto L_0x072b
        L_0x05ab:
            r7 = r29
            r7.set(r1)
            r3 = r46
            r8 = r12
            r0 = r44
            r9 = 1
            goto L_0x072b
        L_0x05b8:
            r7 = r29
            r6 = r43
        L_0x05bc:
            android.graphics.Rect r0 = r14.mStable
            r1.set(r0)
            android.graphics.Rect r0 = r14.mStable
            r2.set(r0)
            android.graphics.Rect r0 = r14.mStable
            r4.set(r0)
            android.graphics.Rect r0 = r14.mStable
            r5.set(r0)
            r3 = r46
            r8 = r12
            r0 = r44
            r9 = 1
            goto L_0x072b
        L_0x05d8:
            r44 = r10
            r12 = r11
            r27 = r15
            r15 = r4
            r4 = r6
            r6 = r1
            r1 = r2
            r2 = r7
            r7 = r29
        L_0x05e4:
            if (r13 == r0) goto L_0x06db
            if (r13 != r12) goto L_0x05ed
            r0 = r44
            r9 = 1
            goto L_0x06de
        L_0x05ed:
            r0 = 2019(0x7e3, float:2.829E-42)
            if (r13 == r0) goto L_0x06c8
            r0 = 2024(0x7e8, float:2.836E-42)
            if (r13 != r0) goto L_0x05fa
            r0 = r44
            r9 = 1
            goto L_0x06cb
        L_0x05fa:
            r0 = 2015(0x7df, float:2.824E-42)
            if (r13 == r0) goto L_0x0606
            r0 = 2036(0x7f4, float:2.853E-42)
            if (r13 != r0) goto L_0x0603
            goto L_0x0606
        L_0x0603:
            r0 = r44
            goto L_0x0623
        L_0x0606:
            r0 = r44
            r9 = r0 & 1024(0x400, float:1.435E-42)
            if (r9 == 0) goto L_0x0623
            android.graphics.Rect r3 = r14.mOverscan
            r1.set(r3)
            android.graphics.Rect r3 = r14.mOverscan
            r2.set(r3)
            android.graphics.Rect r3 = r14.mOverscan
            r4.set(r3)
            android.graphics.Rect r3 = r14.mOverscan
            r5.set(r3)
            r9 = 1
            goto L_0x0718
        L_0x0623:
            r9 = 2021(0x7e5, float:2.832E-42)
            if (r13 != r9) goto L_0x063e
            android.graphics.Rect r3 = r14.mOverscan
            r1.set(r3)
            android.graphics.Rect r3 = r14.mOverscan
            r2.set(r3)
            android.graphics.Rect r3 = r14.mOverscan
            r4.set(r3)
            android.graphics.Rect r3 = r14.mOverscan
            r5.set(r3)
            r9 = 1
            goto L_0x0718
        L_0x063e:
            r9 = 33554432(0x2000000, float:9.403955E-38)
            r9 = r9 & r0
            if (r9 == 0) goto L_0x065e
            r9 = 1
            if (r13 < r9) goto L_0x065f
            if (r13 > r3) goto L_0x065f
            android.graphics.Rect r3 = r14.mOverscan
            r1.set(r3)
            android.graphics.Rect r3 = r14.mOverscan
            r2.set(r3)
            android.graphics.Rect r3 = r14.mOverscan
            r4.set(r3)
            android.graphics.Rect r3 = r14.mOverscan
            r5.set(r3)
            goto L_0x0718
        L_0x065e:
            r9 = 1
        L_0x065f:
            r10 = r15 & 512(0x200, float:7.175E-43)
            if (r10 == 0) goto L_0x068d
            r10 = 2000(0x7d0, float:2.803E-42)
            if (r13 == r10) goto L_0x0677
            r10 = 2005(0x7d5, float:2.81E-42)
            if (r13 == r10) goto L_0x0677
            r10 = 2034(0x7f2, float:2.85E-42)
            if (r13 == r10) goto L_0x0677
            r10 = 2033(0x7f1, float:2.849E-42)
            if (r13 == r10) goto L_0x0677
            if (r13 < r9) goto L_0x068d
            if (r13 > r3) goto L_0x068d
        L_0x0677:
            android.graphics.Rect r3 = r14.mUnrestricted
            r1.set(r3)
            android.graphics.Rect r3 = r14.mUnrestricted
            r2.set(r3)
            android.graphics.Rect r3 = r14.mUnrestricted
            r4.set(r3)
            android.graphics.Rect r3 = r14.mUnrestricted
            r5.set(r3)
            goto L_0x0718
        L_0x068d:
            r3 = r15 & 1024(0x400, float:1.435E-42)
            if (r3 == 0) goto L_0x06b3
            android.graphics.Rect r3 = r14.mRestricted
            r2.set(r3)
            android.graphics.Rect r3 = r14.mRestricted
            r4.set(r3)
            android.graphics.Rect r3 = r14.mRestricted
            r5.set(r3)
            int r3 = android.view.ViewRootImpl.sNewInsetsMode
            if (r3 != 0) goto L_0x06ad
            if (r6 == r8) goto L_0x06a7
            goto L_0x06ad
        L_0x06a7:
            android.graphics.Rect r3 = r14.mContent
            r1.set(r3)
            goto L_0x0718
        L_0x06ad:
            android.graphics.Rect r3 = r14.mDock
            r1.set(r3)
            goto L_0x0718
        L_0x06b3:
            android.graphics.Rect r3 = r14.mRestricted
            r1.set(r3)
            android.graphics.Rect r3 = r14.mRestricted
            r2.set(r3)
            android.graphics.Rect r3 = r14.mRestricted
            r4.set(r3)
            android.graphics.Rect r3 = r14.mRestricted
            r5.set(r3)
            goto L_0x0718
        L_0x06c8:
            r0 = r44
            r9 = 1
        L_0x06cb:
            android.graphics.Rect r3 = r14.mUnrestricted
            r2.set(r3)
            android.graphics.Rect r3 = r14.mUnrestricted
            r4.set(r3)
            android.graphics.Rect r3 = r14.mUnrestricted
            r5.set(r3)
            goto L_0x0718
        L_0x06db:
            r0 = r44
            r9 = 1
        L_0x06de:
            android.graphics.Rect r3 = r14.mUnrestricted
            r1.set(r3)
            android.graphics.Rect r3 = r14.mUnrestricted
            r2.set(r3)
            android.graphics.Rect r3 = r14.mUnrestricted
            r4.set(r3)
            android.graphics.Rect r3 = r14.mUnrestricted
            r5.set(r3)
            if (r21 == 0) goto L_0x0718
            android.graphics.Rect r3 = r14.mDock
            int r3 = r3.left
            r1.left = r3
            r2.left = r3
            r4.left = r3
            r5.left = r3
            android.graphics.Rect r3 = r14.mRestricted
            int r3 = r3.right
            r1.right = r3
            r2.right = r3
            r4.right = r3
            r5.right = r3
            android.graphics.Rect r3 = r14.mRestricted
            int r3 = r3.bottom
            r1.bottom = r3
            r2.bottom = r3
            r4.bottom = r3
            r5.bottom = r3
        L_0x0718:
            r3 = r46
            r8 = r12
            r3.applyStableConstraints(r15, r0, r1, r14)
            r10 = 48
            if (r6 == r10) goto L_0x0728
            android.graphics.Rect r10 = r14.mCurrent
            r7.set(r10)
            goto L_0x072b
        L_0x0728:
            r7.set(r1)
        L_0x072b:
            r10 = r27
            int r11 = r10.layoutInDisplayCutoutMode
            if (r48 == 0) goto L_0x0735
            if (r25 != 0) goto L_0x0735
            r12 = r9
            goto L_0x0736
        L_0x0735:
            r12 = 0
        L_0x0736:
            r18 = r17 & 2
            if (r18 == 0) goto L_0x073d
            r18 = r9
            goto L_0x073f
        L_0x073d:
            r18 = 0
        L_0x073f:
            boolean r22 = r10.isFullscreen()
            if (r22 != 0) goto L_0x074c
            if (r25 == 0) goto L_0x074c
            if (r13 == r9) goto L_0x074c
            r22 = r9
            goto L_0x074e
        L_0x074c:
            r22 = 0
        L_0x074e:
            if (r11 == r9) goto L_0x07bf
            android.graphics.Rect r8 = sTmpDisplayCutoutSafeExceptMaybeBarsRect
            android.graphics.Rect r9 = r14.mDisplayCutoutSafe
            r8.set(r9)
            if (r25 == 0) goto L_0x0764
            if (r34 == 0) goto L_0x0764
            if (r24 != 0) goto L_0x0764
            if (r11 != 0) goto L_0x0764
            r9 = -2147483648(0xffffffff80000000, float:-0.0)
            r8.top = r9
            goto L_0x0766
        L_0x0764:
            r9 = -2147483648(0xffffffff80000000, float:-0.0)
        L_0x0766:
            if (r25 == 0) goto L_0x078f
            if (r34 == 0) goto L_0x078f
            if (r18 != 0) goto L_0x078f
            if (r11 != 0) goto L_0x078f
            int r9 = r3.mNavigationBarPosition
            r38 = r6
            r6 = 1
            if (r9 == r6) goto L_0x0789
            r6 = 2
            if (r9 == r6) goto L_0x0782
            r6 = 4
            if (r9 == r6) goto L_0x077c
            goto L_0x0792
        L_0x077c:
            r9 = 2147483647(0x7fffffff, float:NaN)
            r8.bottom = r9
            goto L_0x0792
        L_0x0782:
            r6 = 4
            r9 = 2147483647(0x7fffffff, float:NaN)
            r8.right = r9
            goto L_0x0792
        L_0x0789:
            r6 = 4
            r9 = -2147483648(0xffffffff80000000, float:-0.0)
            r8.left = r9
            goto L_0x0792
        L_0x078f:
            r38 = r6
            r6 = 4
        L_0x0792:
            r9 = 2011(0x7db, float:2.818E-42)
            if (r13 != r9) goto L_0x079f
            int r9 = r3.mNavigationBarPosition
            if (r9 != r6) goto L_0x079f
            r6 = 2147483647(0x7fffffff, float:NaN)
            r8.bottom = r6
        L_0x079f:
            if (r12 != 0) goto L_0x07b9
            if (r22 != 0) goto L_0x07b9
            android.graphics.Rect r6 = sTmpRect
            r6.set(r5)
            r5.intersectUnchecked(r8)
            android.graphics.Rect r6 = sTmpRect
            boolean r6 = r6.equals(r5)
            r9 = 1
            r6 = r6 ^ r9
            r9 = r39
            r9.setParentFrameWasClippedByDisplayCutout(r6)
            goto L_0x07bb
        L_0x07b9:
            r9 = r39
        L_0x07bb:
            r4.intersectUnchecked(r8)
            goto L_0x07c3
        L_0x07bf:
            r38 = r6
            r9 = r39
        L_0x07c3:
            android.graphics.Rect r6 = r14.mDisplayCutoutSafe
            r1.intersectUnchecked(r6)
            int r6 = r10.type
            r8 = 2017(0x7e1, float:2.826E-42)
            if (r6 != r8) goto L_0x07d5
            int r6 = r10.flags
            r8 = 256(0x100, float:3.59E-43)
            r6 = r6 & r8
            if (r6 != 0) goto L_0x07da
        L_0x07d5:
            int r6 = r10.type
            r8 = 3
            if (r6 != r8) goto L_0x07ea
        L_0x07da:
            r6 = r26
            r8 = 0
            r6.top = r8
            r7.top = r8
            r1.top = r8
            r2.top = r8
            r4.top = r8
            r5.top = r8
            goto L_0x07ec
        L_0x07ea:
            r6 = r26
        L_0x07ec:
            r8 = r0 & 512(0x200, float:7.175E-43)
            if (r8 == 0) goto L_0x0826
            r8 = 2010(0x7da, float:2.817E-42)
            if (r13 == r8) goto L_0x0826
            boolean r8 = r47.inMultiWindowMode()
            if (r8 != 0) goto L_0x0826
            r8 = -10000(0xffffffffffffd8f0, float:NaN)
            r4.top = r8
            r4.left = r8
            r8 = 10000(0x2710, float:1.4013E-41)
            r4.bottom = r8
            r4.right = r8
            r8 = 2013(0x7dd, float:2.821E-42)
            if (r13 == r8) goto L_0x0826
            r8 = -10000(0xffffffffffffd8f0, float:NaN)
            r7.top = r8
            r7.left = r8
            r1.top = r8
            r1.left = r8
            r2.top = r8
            r2.left = r8
            r8 = 10000(0x2710, float:1.4013E-41)
            r7.bottom = r8
            r7.right = r8
            r1.bottom = r8
            r1.right = r8
            r2.bottom = r8
            r2.right = r8
        L_0x0826:
            boolean r8 = shouldUseOutsets(r10, r0)
            if (r16 == 0) goto L_0x0872
            if (r8 == 0) goto L_0x0872
            r41 = r0
            android.graphics.Rect r0 = r9.mOutsetFrame
            r31 = r2
            int r2 = r1.left
            r32 = r4
            int r4 = r1.top
            r26 = r6
            int r6 = r1.right
            r29 = r7
            int r7 = r1.bottom
            r0.set(r2, r4, r6, r7)
            r2 = 1
            r9.setHasOutsets(r2)
            int r2 = r3.mWindowOutsetBottom
            if (r2 <= 0) goto L_0x087c
            int r4 = r14.mRotation
            if (r4 != 0) goto L_0x0857
            int r6 = r0.bottom
            int r6 = r6 + r2
            r0.bottom = r6
            goto L_0x087c
        L_0x0857:
            r6 = 1
            if (r4 != r6) goto L_0x0860
            int r6 = r0.right
            int r6 = r6 + r2
            r0.right = r6
            goto L_0x087c
        L_0x0860:
            r6 = 2
            if (r4 != r6) goto L_0x0869
            int r6 = r0.top
            int r6 = r6 - r2
            r0.top = r6
            goto L_0x087c
        L_0x0869:
            r6 = 3
            if (r4 != r6) goto L_0x087c
            int r6 = r0.left
            int r6 = r6 - r2
            r0.left = r6
            goto L_0x087c
        L_0x0872:
            r41 = r0
            r31 = r2
            r32 = r4
            r26 = r6
            r29 = r7
        L_0x087c:
            android.graphics.Rect r0 = sTmpLastParentFrame
            boolean r0 = r0.equals(r5)
            if (r0 != 0) goto L_0x0888
            r0 = 1
            r9.setContentChanged(r0)
        L_0x0888:
            r47.computeFrameLw()
            r0 = 2011(0x7db, float:2.818E-42)
            if (r13 != r0) goto L_0x08a4
            boolean r0 = r47.isVisibleLw()
            if (r0 == 0) goto L_0x08a4
            boolean r0 = r47.getGivenInsetsPendingLw()
            if (r0 != 0) goto L_0x08a1
            r0 = r47
            r3.offsetInputMethodWindowLw(r0, r14)
            goto L_0x08a6
        L_0x08a1:
            r0 = r47
            goto L_0x08a6
        L_0x08a4:
            r0 = r47
        L_0x08a6:
            r2 = 2031(0x7ef, float:2.846E-42)
            if (r13 != r2) goto L_0x08b9
            boolean r2 = r47.isVisibleLw()
            if (r2 == 0) goto L_0x08b9
            boolean r2 = r47.getGivenInsetsPendingLw()
            if (r2 != 0) goto L_0x08b9
            r3.offsetVoiceInputWindowLw(r0, r14)
        L_0x08b9:
            return
        L_0x08ba:
            r3 = r12
            r0 = r13
        L_0x08bc:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.DisplayPolicy.layoutWindowLw(com.android.server.wm.WindowState, com.android.server.wm.WindowState, com.android.server.wm.DisplayFrames):void");
    }

    private void layoutWallpaper(DisplayFrames displayFrames, Rect pf, Rect df, Rect of, Rect cf) {
        df.set(displayFrames.mOverscan);
        pf.set(displayFrames.mOverscan);
        cf.set(displayFrames.mUnrestricted);
        of.set(displayFrames.mUnrestricted);
    }

    private void offsetInputMethodWindowLw(WindowState win, DisplayFrames displayFrames) {
        int top = Math.max(win.getDisplayFrameLw().top, win.getContentFrameLw().top) + win.getGivenContentInsetsLw().top;
        displayFrames.mContent.bottom = Math.min(displayFrames.mContent.bottom, top);
        displayFrames.mVoiceContent.bottom = Math.min(displayFrames.mVoiceContent.bottom, top);
        int top2 = win.getVisibleFrameLw().top + win.getGivenVisibleInsetsLw().top;
        displayFrames.mCurrent.bottom = Math.min(displayFrames.mCurrent.bottom, top2);
    }

    private void offsetVoiceInputWindowLw(WindowState win, DisplayFrames displayFrames) {
        int top = Math.max(win.getDisplayFrameLw().top, win.getContentFrameLw().top) + win.getGivenContentInsetsLw().top;
        displayFrames.mVoiceContent.bottom = Math.min(displayFrames.mVoiceContent.bottom, top);
    }

    public void beginPostLayoutPolicyLw() {
        this.mTopFullscreenOpaqueWindowState = null;
        this.mTopFullscreenOpaqueOrDimmingWindowState = null;
        this.mTopDockedOpaqueWindowState = null;
        this.mTopDockedOpaqueOrDimmingWindowState = null;
        this.mForceStatusBar = false;
        this.mForceStatusBarFromKeyguard = false;
        this.mForceStatusBarTransparent = false;
        this.mForcingShowNavBar = false;
        this.mForcingShowNavBarLayer = -1;
        this.mAllowLockscreenWhenOn = false;
        this.mShowingDream = false;
        this.mWindowSleepTokenNeeded = false;
        this.isInSplitWindowMode = false;
    }

    public void applyPostLayoutPolicyLw(WindowState win, WindowManager.LayoutParams attrs, WindowState attached, WindowState imeTarget) {
        boolean affectsSystemUi = win.canAffectSystemUiFlags();
        this.mService.mPolicy.applyKeyguardPolicyLw(win, imeTarget);
        int fl = PolicyControl.getWindowFlags(win, attrs);
        if (this.mTopFullscreenOpaqueWindowState == null && affectsSystemUi && attrs.type == 2011) {
            this.mForcingShowNavBar = true;
            this.mForcingShowNavBarLayer = win.getSurfaceLayer();
        }
        if (attrs.type == 2000) {
            if ((attrs.privateFlags & 1024) != 0) {
                this.mForceStatusBarFromKeyguard = true;
            }
            if ((attrs.privateFlags & 4096) != 0) {
                this.mForceStatusBarTransparent = true;
            }
        }
        boolean inFullScreenOrSplitScreenSecondaryWindowingMode = false;
        boolean appWindow = attrs.type >= 1 && attrs.type < 2000;
        int windowingMode = win.getWindowingMode();
        if (windowingMode == 1 || windowingMode == 4) {
            inFullScreenOrSplitScreenSecondaryWindowingMode = true;
        }
        if (this.mTopFullscreenOpaqueWindowState == null && affectsSystemUi) {
            if ((fl & 2048) != 0) {
                this.mForceStatusBar = true;
            }
            if (attrs.type == 2023 && (!this.mDreamingLockscreen || (win.isVisibleLw() && win.hasDrawnLw()))) {
                this.mShowingDream = true;
                appWindow = true;
            }
            if (appWindow && attached == null && attrs.isFullscreen() && inFullScreenOrSplitScreenSecondaryWindowingMode) {
                this.mTopFullscreenOpaqueWindowState = win;
                if (this.mTopFullscreenOpaqueOrDimmingWindowState == null) {
                    this.mTopFullscreenOpaqueOrDimmingWindowState = win;
                }
                if ((fl & 1) != 0) {
                    this.mAllowLockscreenWhenOn = true;
                }
            }
        }
        if (affectsSystemUi && attrs.type == 2031) {
            if (this.mTopFullscreenOpaqueWindowState == null) {
                this.mTopFullscreenOpaqueWindowState = win;
                if (this.mTopFullscreenOpaqueOrDimmingWindowState == null) {
                    this.mTopFullscreenOpaqueOrDimmingWindowState = win;
                }
            }
            if (this.mTopDockedOpaqueWindowState == null) {
                this.mTopDockedOpaqueWindowState = win;
                if (this.mTopDockedOpaqueOrDimmingWindowState == null) {
                    this.mTopDockedOpaqueOrDimmingWindowState = win;
                }
            }
        }
        if (this.mTopFullscreenOpaqueOrDimmingWindowState == null && affectsSystemUi && win.isDimming() && inFullScreenOrSplitScreenSecondaryWindowingMode) {
            this.mTopFullscreenOpaqueOrDimmingWindowState = win;
        }
        if (this.mTopDockedOpaqueWindowState == null && affectsSystemUi && appWindow && attached == null && attrs.isFullscreen() && windowingMode == 3) {
            this.mTopDockedOpaqueWindowState = win;
            if (this.mTopDockedOpaqueOrDimmingWindowState == null) {
                this.mTopDockedOpaqueOrDimmingWindowState = win;
            }
            this.isInSplitWindowMode = true;
        }
        if (this.mTopDockedOpaqueOrDimmingWindowState == null && affectsSystemUi && win.isDimming() && windowingMode == 3) {
            this.mTopDockedOpaqueOrDimmingWindowState = win;
        }
        if ((attrs.extraFlags & DumpState.DUMP_CHANGES) != 0 && win.canAcquireSleepToken()) {
            this.mWindowSleepTokenNeeded = true;
        }
    }

    public int finishPostLayoutPolicyLw() {
        int changes = 0;
        boolean topIsFullscreen = false;
        boolean z = true;
        if (!this.mShowingDream) {
            this.mDreamingLockscreen = this.mService.mPolicy.isKeyguardShowingAndNotOccluded();
            if (this.mDreamingSleepTokenNeeded) {
                this.mDreamingSleepTokenNeeded = false;
                this.mHandler.obtainMessage(1, 0, 1).sendToTarget();
            }
        } else if (!this.mDreamingSleepTokenNeeded) {
            this.mDreamingSleepTokenNeeded = true;
            this.mHandler.obtainMessage(1, 1, 1).sendToTarget();
        }
        if (this.mStatusBar != null) {
            if (!(this.mForceStatusBarTransparent && !this.mForceStatusBar && !this.mForceStatusBarFromKeyguard)) {
                this.mStatusBarController.setShowTransparent(false);
            } else if (!this.mStatusBar.isVisibleLw()) {
                this.mStatusBarController.setShowTransparent(true);
            }
            boolean statusBarForcesShowingNavigation = (this.mStatusBar.getAttrs().privateFlags & DumpState.DUMP_VOLUMES) != 0;
            boolean topAppHidesStatusBar = topAppHidesStatusBar();
            if (this.mForceStatusBar || this.mForceStatusBarFromKeyguard || this.mForceStatusBarTransparent || statusBarForcesShowingNavigation) {
                if (this.mStatusBarController.setBarShowingLw(true)) {
                    changes = 0 | 1;
                }
                if (!this.mTopIsFullscreen || !this.mStatusBar.isAnimatingLw()) {
                    z = false;
                }
                topIsFullscreen = z;
                if ((this.mForceStatusBarFromKeyguard || statusBarForcesShowingNavigation) && this.mStatusBarController.isTransientShowing()) {
                    StatusBarController statusBarController = this.mStatusBarController;
                    int i = this.mLastSystemUiFlags;
                    statusBarController.updateVisibilityLw(false, i, i);
                }
            } else if (this.mTopFullscreenOpaqueWindowState != null) {
                topIsFullscreen = topAppHidesStatusBar;
                if (this.mStatusBarController.isTransientShowing()) {
                    if (this.mStatusBarController.setBarShowingLw(true)) {
                        changes = 0 | 1;
                    }
                } else if (!topIsFullscreen || this.mDisplayContent.isStackVisible(3)) {
                    if (this.mStatusBarController.setBarShowingLw(true)) {
                        changes = 0 | 1;
                    }
                    topAppHidesStatusBar = false;
                } else if (this.mStatusBarController.setBarShowingLw(false)) {
                    changes = 0 | 1;
                }
            }
            this.mStatusBarController.setTopAppHidesStatusBar(topAppHidesStatusBar);
        }
        if (this.mTopIsFullscreen != topIsFullscreen) {
            if (!topIsFullscreen) {
                changes |= 1;
            }
            this.mTopIsFullscreen = topIsFullscreen;
        }
        if ((updateSystemUiVisibilityLw() & SYSTEM_UI_CHANGING_LAYOUT) != 0) {
            changes |= 1;
        }
        boolean z2 = this.mShowingDream;
        if (z2 != this.mLastShowingDream) {
            this.mLastShowingDream = z2;
            this.mService.notifyShowingDreamChanged();
        }
        updateWindowSleepToken();
        this.mService.mPolicy.setAllowLockscreenWhenOn(getDisplayId(), this.mAllowLockscreenWhenOn);
        return changes;
    }

    private void updateWindowSleepToken() {
        if (this.mWindowSleepTokenNeeded && !this.mLastWindowSleepTokenNeeded) {
            this.mHandler.removeCallbacks(this.mReleaseSleepTokenRunnable);
            this.mHandler.post(this.mAcquireSleepTokenRunnable);
        } else if (!this.mWindowSleepTokenNeeded && this.mLastWindowSleepTokenNeeded) {
            this.mHandler.removeCallbacks(this.mAcquireSleepTokenRunnable);
            this.mHandler.post(this.mReleaseSleepTokenRunnable);
        }
        this.mLastWindowSleepTokenNeeded = this.mWindowSleepTokenNeeded;
    }

    private boolean topAppHidesStatusBar() {
        WindowState windowState = this.mTopFullscreenOpaqueWindowState;
        if (windowState == null) {
            return false;
        }
        if ((PolicyControl.getWindowFlags((WindowState) null, windowState.getAttrs()) & 1024) == 0 && (this.mLastSystemUiFlags & 4) == 0) {
            return false;
        }
        return true;
    }

    public void switchUser() {
        updateCurrentUserResources();
    }

    public void onOverlayChangedLw() {
        updateCurrentUserResources();
        onConfigurationChanged();
        this.mSystemGestures.onConfigurationChanged();
    }

    public void onConfigurationChanged() {
        DisplayRotation displayRotation = this.mDisplayContent.getDisplayRotation();
        Resources res = getCurrentUserResources();
        int portraitRotation = displayRotation.getPortraitRotation();
        int upsideDownRotation = displayRotation.getUpsideDownRotation();
        int landscapeRotation = displayRotation.getLandscapeRotation();
        int seascapeRotation = displayRotation.getSeascapeRotation();
        int uiMode = this.mService.mPolicy.getUiMode();
        if (hasStatusBar()) {
            int[] iArr = this.mStatusBarHeightForRotation;
            int dimensionPixelSize = res.getDimensionPixelSize(17105469);
            iArr[upsideDownRotation] = dimensionPixelSize;
            iArr[portraitRotation] = dimensionPixelSize;
            int[] iArr2 = this.mStatusBarHeightForRotation;
            int dimensionPixelSize2 = res.getDimensionPixelSize(17105468);
            iArr2[seascapeRotation] = dimensionPixelSize2;
            iArr2[landscapeRotation] = dimensionPixelSize2;
        } else {
            int[] iArr3 = this.mStatusBarHeightForRotation;
            iArr3[seascapeRotation] = 0;
            iArr3[landscapeRotation] = 0;
            iArr3[upsideDownRotation] = 0;
            iArr3[portraitRotation] = 0;
        }
        int[] iArr4 = this.mNavigationBarHeightForRotationDefault;
        int dimensionPixelSize3 = res.getDimensionPixelSize(17105304);
        iArr4[upsideDownRotation] = dimensionPixelSize3;
        iArr4[portraitRotation] = dimensionPixelSize3;
        int[] iArr5 = this.mNavigationBarHeightForRotationDefault;
        int dimensionPixelSize4 = res.getDimensionPixelSize(17105306);
        iArr5[seascapeRotation] = dimensionPixelSize4;
        iArr5[landscapeRotation] = dimensionPixelSize4;
        int[] iArr6 = this.mNavigationBarFrameHeightForRotationDefault;
        int dimensionPixelSize5 = res.getDimensionPixelSize(17105301);
        iArr6[upsideDownRotation] = dimensionPixelSize5;
        iArr6[portraitRotation] = dimensionPixelSize5;
        int[] iArr7 = this.mNavigationBarFrameHeightForRotationDefault;
        int dimensionPixelSize6 = res.getDimensionPixelSize(17105302);
        iArr7[seascapeRotation] = dimensionPixelSize6;
        iArr7[landscapeRotation] = dimensionPixelSize6;
        int[] iArr8 = this.mNavigationBarWidthForRotationDefault;
        int dimensionPixelSize7 = res.getDimensionPixelSize(17105309);
        iArr8[seascapeRotation] = dimensionPixelSize7;
        iArr8[landscapeRotation] = dimensionPixelSize7;
        iArr8[upsideDownRotation] = dimensionPixelSize7;
        iArr8[portraitRotation] = dimensionPixelSize7;
        this.mNavBarOpacityMode = res.getInteger(17694851);
        this.mSideGestureInset = res.getDimensionPixelSize(17105050);
        this.mNavigationBarLetsThroughTaps = res.getBoolean(17891487);
        this.mNavigationBarAlwaysShowOnSideGesture = res.getBoolean(17891484);
        this.mBottomGestureAdditionalInset = res.getDimensionPixelSize(17105303) - getNavigationBarFrameHeight(portraitRotation, uiMode);
        updateConfigurationAndScreenSizeDependentBehaviors();
        this.mWindowOutsetBottom = ScreenShapeHelper.getWindowOutsetBottomPx(this.mContext.getResources());
    }

    /* access modifiers changed from: package-private */
    public void updateConfigurationAndScreenSizeDependentBehaviors() {
        Resources res = getCurrentUserResources();
        this.mNavigationBarCanMove = this.mDisplayContent.mBaseDisplayWidth != this.mDisplayContent.mBaseDisplayHeight && res.getBoolean(17891485);
        this.mAllowSeamlessRotationDespiteNavBarMoving = res.getBoolean(17891346);
    }

    private void updateCurrentUserResources() {
        int userId = this.mService.mAmInternal.getCurrentUserId();
        Context uiContext = getSystemUiContext();
        if (userId == 0) {
            this.mCurrentUserResources = uiContext.getResources();
            return;
        }
        LoadedApk pi = ActivityThread.currentActivityThread().getPackageInfo(uiContext.getPackageName(), (CompatibilityInfo) null, 0, userId);
        this.mCurrentUserResources = ResourcesManager.getInstance().getResources((IBinder) null, pi.getResDir(), (String[]) null, pi.getOverlayDirs(), pi.getApplicationInfo().sharedLibraryFiles, this.mDisplayContent.getDisplayId(), (Configuration) null, uiContext.getResources().getCompatibilityInfo(), (ClassLoader) null);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public Resources getCurrentUserResources() {
        if (this.mCurrentUserResources == null) {
            updateCurrentUserResources();
        }
        return this.mCurrentUserResources;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public Context getContext() {
        return this.mContext;
    }

    private Context getSystemUiContext() {
        Context uiContext = ActivityThread.currentActivityThread().getSystemUiContext();
        return this.mDisplayContent.isDefaultDisplay ? uiContext : uiContext.createDisplayContext(this.mDisplayContent.getDisplay());
    }

    private int getNavigationBarWidth(int rotation, int uiMode) {
        return this.mNavigationBarWidthForRotationDefault[rotation];
    }

    /* access modifiers changed from: package-private */
    public void notifyDisplayReady() {
        this.mHandler.post(new Runnable() {
            public final void run() {
                DisplayPolicy.this.lambda$notifyDisplayReady$9$DisplayPolicy();
            }
        });
    }

    public /* synthetic */ void lambda$notifyDisplayReady$9$DisplayPolicy() {
        int displayId = getDisplayId();
        getStatusBarManagerInternal().onDisplayReady(displayId);
        ((WallpaperManagerInternal) LocalServices.getService(WallpaperManagerInternal.class)).onDisplayReady(displayId);
    }

    public int getNonDecorDisplayWidth(int fullWidth, int fullHeight, int rotation, int uiMode, DisplayCutout displayCutout) {
        int navBarPosition;
        int width = fullWidth;
        if (hasNavigationBar() && ((navBarPosition = navigationBarPosition(fullWidth, fullHeight, rotation)) == 1 || navBarPosition == 2)) {
            width -= getNavigationBarWidth(rotation, uiMode);
        }
        if (displayCutout != null) {
            return width - (displayCutout.getSafeInsetLeft() + displayCutout.getSafeInsetRight());
        }
        return width;
    }

    private int getNavigationBarHeight(int rotation, int uiMode) {
        return this.mNavigationBarHeightForRotationDefault[rotation];
    }

    private int getNavigationBarFrameHeight(int rotation, int uiMode) {
        return this.mNavigationBarFrameHeightForRotationDefault[rotation];
    }

    public int getNonDecorDisplayHeight(int fullWidth, int fullHeight, int rotation, int uiMode, DisplayCutout displayCutout) {
        int height = fullHeight;
        if (hasNavigationBar() && navigationBarPosition(fullWidth, fullHeight, rotation) == 4) {
            height -= getNavigationBarHeight(rotation, uiMode);
        }
        if (displayCutout != null) {
            return height - (displayCutout.getSafeInsetTop() + displayCutout.getSafeInsetBottom());
        }
        return height;
    }

    public int getConfigDisplayWidth(int fullWidth, int fullHeight, int rotation, int uiMode, DisplayCutout displayCutout) {
        return getNonDecorDisplayWidth(fullWidth, fullHeight, rotation, uiMode, displayCutout);
    }

    public int getConfigDisplayHeight(int fullWidth, int fullHeight, int rotation, int uiMode, DisplayCutout displayCutout) {
        int statusBarHeight = this.mStatusBarHeightForRotation[rotation];
        if (displayCutout != null) {
            statusBarHeight = Math.max(0, statusBarHeight - displayCutout.getSafeInsetTop());
        }
        return getNonDecorDisplayHeight(fullWidth, fullHeight, rotation, uiMode, displayCutout) - statusBarHeight;
    }

    /* access modifiers changed from: package-private */
    public float getWindowCornerRadius() {
        if (this.mDisplayContent.getDisplay().getType() == 1) {
            return ScreenDecorationsUtils.getWindowCornerRadius(this.mContext.getResources());
        }
        return 0.0f;
    }

    /* access modifiers changed from: package-private */
    public boolean isShowingDreamLw() {
        return this.mShowingDream;
    }

    /* access modifiers changed from: package-private */
    public void convertNonDecorInsetsToStableInsets(Rect inOutInsets, int rotation) {
        inOutInsets.top = Math.max(inOutInsets.top, this.mStatusBarHeightForRotation[rotation]);
    }

    public void getStableInsetsLw(int displayRotation, int displayWidth, int displayHeight, DisplayCutout displayCutout, Rect outInsets) {
        outInsets.setEmpty();
        getNonDecorInsetsLw(displayRotation, displayWidth, displayHeight, displayCutout, outInsets);
        convertNonDecorInsetsToStableInsets(outInsets, displayRotation);
    }

    public void getNonDecorInsetsLw(int displayRotation, int displayWidth, int displayHeight, DisplayCutout displayCutout, Rect outInsets) {
        outInsets.setEmpty();
        if (hasNavigationBar()) {
            int uiMode = this.mService.mPolicy.getUiMode();
            int position = navigationBarPosition(displayWidth, displayHeight, displayRotation);
            if (position == 4) {
                outInsets.bottom = getNavigationBarHeight(displayRotation, uiMode);
            } else if (position == 2) {
                outInsets.right = getNavigationBarWidth(displayRotation, uiMode);
            } else if (position == 1) {
                outInsets.left = getNavigationBarWidth(displayRotation, uiMode);
            }
        }
        if (displayCutout != null) {
            outInsets.left += displayCutout.getSafeInsetLeft();
            outInsets.top += displayCutout.getSafeInsetTop();
            outInsets.right += displayCutout.getSafeInsetRight();
            outInsets.bottom += displayCutout.getSafeInsetBottom();
        }
    }

    public void setForwardedInsets(Insets forwardedInsets) {
        this.mForwardedInsets = forwardedInsets;
    }

    public Insets getForwardedInsets() {
        return this.mForwardedInsets;
    }

    /* access modifiers changed from: package-private */
    public int navigationBarPosition(int displayWidth, int displayHeight, int displayRotation) {
        if (!navigationBarCanMove() || displayWidth <= displayHeight) {
            return 4;
        }
        if (displayRotation == 3) {
            return 1;
        }
        if (displayRotation == 1) {
            return 2;
        }
        return 4;
    }

    public int getNavBarPosition() {
        return this.mNavigationBarPosition;
    }

    public int focusChangedLw(WindowState lastFocus, WindowState newFocus) {
        this.mFocusedWindow = newFocus;
        this.mLastFocusedWindow = lastFocus;
        if (this.mDisplayContent.isDefaultDisplay) {
            this.mService.mPolicy.onDefaultDisplayFocusChangedLw(newFocus);
        }
        if ((updateSystemUiVisibilityLw() & SYSTEM_UI_CHANGING_LAYOUT) != 0) {
            return 1;
        }
        return 0;
    }

    public boolean allowAppAnimationsLw() {
        return !this.mShowingDream;
    }

    /* access modifiers changed from: private */
    public void updateDreamingSleepToken(boolean acquire) {
        if (acquire) {
            int displayId = getDisplayId();
            if (this.mDreamingSleepToken == null) {
                ActivityTaskManagerInternal activityTaskManagerInternal = this.mService.mAtmInternal;
                this.mDreamingSleepToken = activityTaskManagerInternal.acquireSleepToken("DreamOnDisplay" + displayId, displayId);
                return;
            }
            return;
        }
        ActivityTaskManagerInternal.SleepToken sleepToken = this.mDreamingSleepToken;
        if (sleepToken != null) {
            sleepToken.release();
            this.mDreamingSleepToken = null;
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x006b, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void requestTransientBars(com.android.server.wm.WindowState r6) {
        /*
            r5 = this;
            java.lang.Object r0 = r5.mLock
            monitor-enter(r0)
            com.android.server.wm.WindowManagerService r1 = r5.mService     // Catch:{ all -> 0x006c }
            com.android.server.policy.WindowManagerPolicy r1 = r1.mPolicy     // Catch:{ all -> 0x006c }
            boolean r1 = r1.isUserSetupComplete()     // Catch:{ all -> 0x006c }
            if (r1 != 0) goto L_0x000f
            monitor-exit(r0)     // Catch:{ all -> 0x006c }
            return
        L_0x000f:
            boolean r1 = r5.isSupportGestureLine()     // Catch:{ all -> 0x006c }
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L_0x001b
            com.android.server.wm.WindowState r1 = r5.mStatusBar     // Catch:{ all -> 0x006c }
            if (r6 != r1) goto L_0x002b
        L_0x001b:
            com.android.server.wm.StatusBarController r1 = r5.mStatusBarController     // Catch:{ all -> 0x006c }
            boolean r1 = r1.checkShowTransientBarLw()     // Catch:{ all -> 0x006c }
            if (r1 == 0) goto L_0x002b
            boolean r1 = r5.isEnterpriseHideStatusbar()     // Catch:{ all -> 0x006c }
            if (r1 != 0) goto L_0x002b
            r1 = r2
            goto L_0x002c
        L_0x002b:
            r1 = r3
        L_0x002c:
            boolean r4 = r5.isSupportGestureLine()     // Catch:{ all -> 0x006c }
            if (r4 == 0) goto L_0x0036
            com.android.server.wm.WindowState r4 = r5.mNavigationBar     // Catch:{ all -> 0x006c }
            if (r6 != r4) goto L_0x0047
        L_0x0036:
            com.android.server.wm.BarController r4 = r5.mNavigationBarController     // Catch:{ all -> 0x006c }
            boolean r4 = r4.checkShowTransientBarLw()     // Catch:{ all -> 0x006c }
            if (r4 == 0) goto L_0x0047
            int r4 = r5.mLastSystemUiFlags     // Catch:{ all -> 0x006c }
            boolean r4 = isNavBarEmpty(r4)     // Catch:{ all -> 0x006c }
            if (r4 != 0) goto L_0x0047
            goto L_0x0048
        L_0x0047:
            r2 = r3
        L_0x0048:
            if (r1 != 0) goto L_0x004c
            if (r2 == 0) goto L_0x006a
        L_0x004c:
            if (r2 != 0) goto L_0x0054
            com.android.server.wm.WindowState r3 = r5.mNavigationBar     // Catch:{ all -> 0x006c }
            if (r6 != r3) goto L_0x0054
            monitor-exit(r0)     // Catch:{ all -> 0x006c }
            return
        L_0x0054:
            if (r1 == 0) goto L_0x005b
            com.android.server.wm.StatusBarController r3 = r5.mStatusBarController     // Catch:{ all -> 0x006c }
            r3.showTransient()     // Catch:{ all -> 0x006c }
        L_0x005b:
            if (r2 == 0) goto L_0x0062
            com.android.server.wm.BarController r3 = r5.mNavigationBarController     // Catch:{ all -> 0x006c }
            r3.showTransient()     // Catch:{ all -> 0x006c }
        L_0x0062:
            com.android.server.wm.ImmersiveModeConfirmation r3 = r5.mImmersiveModeConfirmation     // Catch:{ all -> 0x006c }
            r3.confirmCurrentPrompt()     // Catch:{ all -> 0x006c }
            r5.updateSystemUiVisibilityLw()     // Catch:{ all -> 0x006c }
        L_0x006a:
            monitor-exit(r0)     // Catch:{ all -> 0x006c }
            return
        L_0x006c:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x006c }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.DisplayPolicy.requestTransientBars(com.android.server.wm.WindowState):void");
    }

    private boolean isSupportGestureLine() {
        if (!this.mNavigationBarCanMove && getCurrentUserResources().getInteger(17694850) == 2) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void disposeInputConsumer(WindowManagerPolicy.InputConsumer inputConsumer) {
        if (inputConsumer != null) {
            inputConsumer.dismiss();
        }
    }

    /* access modifiers changed from: protected */
    public boolean isStatusBarKeyguard() {
        WindowState windowState = this.mStatusBar;
        return (windowState == null || (windowState.getAttrs().privateFlags & 1024) == 0) ? false : true;
    }

    private boolean isKeyguardOccluded() {
        return this.mService.mPolicy.isKeyguardOccluded();
    }

    /* access modifiers changed from: package-private */
    public void resetSystemUiVisibilityLw() {
        this.mLastSystemUiFlags = 0;
        updateSystemUiVisibilityLw();
    }

    private int updateSystemUiVisibilityLw() {
        WindowState winCandidate;
        int tmpVisibility;
        int i;
        WindowState windowState;
        WindowState windowState2;
        WindowState winCandidate2 = this.mFocusedWindow;
        if (winCandidate2 == null) {
            winCandidate2 = this.mTopFullscreenOpaqueWindowState;
        }
        if (winCandidate2 == null) {
            return 0;
        }
        if (this.isInSplitWindowMode) {
            winCandidate2 = this.mTopDockedOpaqueWindowState;
        }
        if (winCandidate2.getAttrs().token == this.mImmersiveModeConfirmation.getWindowToken()) {
            WindowState windowState3 = this.mLastFocusedWindow;
            boolean lastFocusCanReceiveKeys = windowState3 != null && windowState3.canReceiveKeys();
            if (isStatusBarKeyguard()) {
                windowState2 = this.mStatusBar;
            } else if (lastFocusCanReceiveKeys) {
                windowState2 = this.mLastFocusedWindow;
            } else {
                windowState2 = this.mTopFullscreenOpaqueWindowState;
            }
            WindowState winCandidate3 = windowState2;
            if (winCandidate3 == null) {
                return 0;
            }
            winCandidate = winCandidate3;
        } else {
            winCandidate = winCandidate2;
        }
        WindowState win = winCandidate;
        if ((win.getAttrs().privateFlags & 1024) != 0 && isKeyguardOccluded()) {
            return 0;
        }
        this.mDisplayContent.getInsetsStateController().onBarControllingWindowChanged(this.mTopFullscreenOpaqueWindowState);
        int tmpVisibility2 = PolicyControl.getSystemUiVisibility(win, (WindowManager.LayoutParams) null) & (~this.mResettingSystemUiFlags) & (~this.mForceClearedSystemUiFlags);
        if (this.mForcingShowNavBar && win.getSurfaceLayer() < (i = this.mForcingShowNavBarLayer) && ((windowState = this.mNavigationBar) == null || i <= windowState.getSurfaceLayer())) {
            tmpVisibility2 &= ~PolicyControl.adjustClearableFlags(win, 7);
        }
        if (win.inFreeformWindowingMode()) {
            tmpVisibility = win.getConfiguration().orientation == 2 ? tmpVisibility2 | 6150 : tmpVisibility2 & (~PolicyControl.adjustClearableFlags(win, 7));
        } else {
            tmpVisibility = tmpVisibility2;
        }
        int fullscreenVisibility = updateLightStatusBarLw(0, this.mTopFullscreenOpaqueWindowState, this.mTopFullscreenOpaqueOrDimmingWindowState);
        int dockedVisibility = updateLightStatusBarLw(0, this.mTopDockedOpaqueWindowState, this.mTopDockedOpaqueOrDimmingWindowState);
        this.mService.getStackBounds(0, 2, this.mNonDockedStackBounds);
        this.mService.getStackBounds(3, 1, this.mDockedStackBounds);
        Pair<Integer, Boolean> result = updateSystemBarsLw(win, this.mLastSystemUiFlags, tmpVisibility);
        int visibility = ((Integer) result.first).intValue();
        int diff = visibility ^ this.mLastSystemUiFlags;
        int fullscreenDiff = fullscreenVisibility ^ this.mLastFullscreenStackSysUiFlags;
        int dockedDiff = dockedVisibility ^ this.mLastDockedStackSysUiFlags;
        boolean needsMenu = win.getNeedsMenuLw(this.mTopFullscreenOpaqueWindowState);
        if (diff == 0 && fullscreenDiff == 0 && dockedDiff == 0 && this.mLastFocusNeedsMenu == needsMenu && this.mFocusedApp == win.getAppToken() && this.mLastNonDockedStackBounds.equals(this.mNonDockedStackBounds) && this.mLastDockedStackBounds.equals(this.mDockedStackBounds)) {
            return 0;
        }
        this.mLastSystemUiFlags = visibility;
        this.mLastFullscreenStackSysUiFlags = fullscreenVisibility;
        this.mLastDockedStackSysUiFlags = dockedVisibility;
        this.mLastFocusNeedsMenu = needsMenu;
        this.mFocusedApp = win.getAppToken();
        this.mLastNonDockedStackBounds.set(this.mNonDockedStackBounds);
        this.mLastDockedStackBounds.set(this.mDockedStackBounds);
        Rect fullscreenStackBounds = new Rect(this.mNonDockedStackBounds);
        Rect dockedStackBounds = new Rect(this.mDockedStackBounds);
        boolean isNavbarColorManagedByIme = ((Boolean) result.second).booleanValue();
        Handler handler = this.mHandler;
        $$Lambda$DisplayPolicy$qQY9m_Itua9TDyNk3zzDxvjEwE r10 = r0;
        WindowState windowState4 = winCandidate;
        int i2 = visibility;
        Pair<Integer, Boolean> pair = result;
        $$Lambda$DisplayPolicy$qQY9m_Itua9TDyNk3zzDxvjEwE r0 = new Runnable(visibility, fullscreenVisibility, dockedVisibility, fullscreenStackBounds, dockedStackBounds, isNavbarColorManagedByIme, win, needsMenu) {
            private final /* synthetic */ int f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ Rect f$4;
            private final /* synthetic */ Rect f$5;
            private final /* synthetic */ boolean f$6;
            private final /* synthetic */ WindowState f$7;
            private final /* synthetic */ boolean f$8;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
                this.f$8 = r9;
            }

            public final void run() {
                DisplayPolicy.this.lambda$updateSystemUiVisibilityLw$10$DisplayPolicy(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        };
        handler.post(r10);
        return diff;
    }

    public /* synthetic */ void lambda$updateSystemUiVisibilityLw$10$DisplayPolicy(int visibility, int fullscreenVisibility, int dockedVisibility, Rect fullscreenStackBounds, Rect dockedStackBounds, boolean isNavbarColorManagedByIme, WindowState win, boolean needsMenu) {
        StatusBarManagerInternal statusBar = getStatusBarManagerInternal();
        if (statusBar != null) {
            int displayId = getDisplayId();
            statusBar.setSystemUiVisibility(displayId, visibility, fullscreenVisibility, dockedVisibility, -1, fullscreenStackBounds, dockedStackBounds, isNavbarColorManagedByIme, win.toString());
            statusBar.topAppWindowChanged(displayId, needsMenu);
            return;
        }
        boolean z = needsMenu;
    }

    private int updateLightStatusBarLw(int vis, WindowState opaque, WindowState opaqueOrDimming) {
        boolean onKeyguard = isStatusBarKeyguard() && !isKeyguardOccluded();
        WindowState statusColorWin = onKeyguard ? this.mStatusBar : opaqueOrDimming;
        if (statusColorWin != null && (statusColorWin == opaque || onKeyguard)) {
            return (vis & -8193) | (PolicyControl.getSystemUiVisibility(statusColorWin, (WindowManager.LayoutParams) null) & 8192);
        }
        if (statusColorWin == null || !statusColorWin.isDimming()) {
            return vis;
        }
        return vis & -8193;
    }

    @VisibleForTesting
    static WindowState chooseNavigationColorWindowLw(WindowState opaque, WindowState opaqueOrDimming, WindowState imeWindow, int navBarPosition) {
        boolean imeWindowCanNavColorWindow = imeWindow != null && imeWindow.isVisibleLw() && navBarPosition == 4 && (PolicyControl.getWindowFlags(imeWindow, (WindowManager.LayoutParams) null) & Integer.MIN_VALUE) != 0;
        if (opaque != null && opaqueOrDimming == opaque) {
            return imeWindowCanNavColorWindow ? imeWindow : opaque;
        }
        if (opaqueOrDimming == null || !opaqueOrDimming.isDimming()) {
            if (imeWindowCanNavColorWindow) {
                return imeWindow;
            }
            return null;
        } else if (imeWindowCanNavColorWindow && WindowManager.LayoutParams.mayUseInputMethod(PolicyControl.getWindowFlags(opaqueOrDimming, (WindowManager.LayoutParams) null))) {
            return imeWindow;
        } else {
            return opaqueOrDimming;
        }
    }

    @VisibleForTesting
    static int updateLightNavigationBarLw(int vis, WindowState opaque, WindowState opaqueOrDimming, WindowState imeWindow, WindowState navColorWin) {
        if (navColorWin == null) {
            return vis;
        }
        if (navColorWin == imeWindow || navColorWin == opaque) {
            return (vis & -17) | (PolicyControl.getSystemUiVisibility(navColorWin, (WindowManager.LayoutParams) null) & 16);
        }
        if (navColorWin != opaqueOrDimming || !navColorWin.isDimming()) {
            return vis;
        }
        return vis & -17;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:103:0x017e, code lost:
        if (r7.mForceShowSystemBars != false) goto L_0x0183;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private android.util.Pair<java.lang.Integer, java.lang.Boolean> updateSystemBarsLw(com.android.server.wm.WindowState r37, int r38, int r39) {
        /*
            r36 = this;
            r7 = r36
            r8 = r38
            com.android.server.wm.DisplayContent r0 = r7.mDisplayContent
            r1 = 3
            boolean r9 = r0.isStackVisible(r1)
            r10 = 0
            com.android.server.wm.DisplayContent r0 = r7.mDisplayContent
            com.android.server.wm.DockedStackDividerController r0 = r0.getDockedDividerController()
            boolean r11 = r0.isResizing()
            r13 = 0
            if (r9 != 0) goto L_0x0022
            if (r11 != 0) goto L_0x0022
            boolean r0 = r7.mForceShowSystemBarsFromExternal
            if (r0 == 0) goto L_0x0020
            goto L_0x0022
        L_0x0020:
            r0 = r13
            goto L_0x0023
        L_0x0022:
            r0 = 1
        L_0x0023:
            r7.mForceShowSystemBars = r0
            boolean r0 = r7.mForceShowSystemBars
            if (r0 == 0) goto L_0x002f
            boolean r0 = r7.mForceStatusBarFromKeyguard
            if (r0 != 0) goto L_0x002f
            r0 = 1
            goto L_0x0030
        L_0x002f:
            r0 = r13
        L_0x0030:
            r14 = r0
            boolean r0 = r36.isStatusBarKeyguard()
            if (r0 == 0) goto L_0x0040
            boolean r0 = r36.isKeyguardOccluded()
            if (r0 != 0) goto L_0x0040
            com.android.server.wm.WindowState r0 = r7.mStatusBar
            goto L_0x0042
        L_0x0040:
            com.android.server.wm.WindowState r0 = r7.mTopFullscreenOpaqueWindowState
        L_0x0042:
            r15 = r0
            com.android.server.wm.StatusBarController r0 = r7.mStatusBarController
            r1 = r39
            int r0 = r0.applyTranslucentFlagLw(r15, r1, r8)
            com.android.server.wm.BarController r1 = r7.mNavigationBarController
            int r0 = r1.applyTranslucentFlagLw(r15, r0, r8)
            com.android.server.wm.StatusBarController r1 = r7.mStatusBarController
            com.android.server.wm.WindowState r2 = r7.mTopDockedOpaqueWindowState
            int r1 = r1.applyTranslucentFlagLw(r2, r13, r13)
            com.android.server.wm.BarController r2 = r7.mNavigationBarController
            com.android.server.wm.WindowState r3 = r7.mTopDockedOpaqueWindowState
            int r6 = r2.applyTranslucentFlagLw(r3, r1, r13)
            com.android.server.wm.WindowState r1 = r7.mTopFullscreenOpaqueWindowState
            boolean r16 = r7.drawsStatusBarBackground(r0, r1)
            com.android.server.wm.WindowState r1 = r7.mTopDockedOpaqueWindowState
            boolean r17 = r7.drawsStatusBarBackground(r6, r1)
            com.android.server.wm.WindowState r1 = r7.mTopFullscreenOpaqueWindowState
            boolean r18 = r7.drawsNavigationBarBackground(r0, r1)
            com.android.server.wm.WindowState r1 = r7.mTopDockedOpaqueWindowState
            boolean r19 = r7.drawsNavigationBarBackground(r6, r1)
            android.view.WindowManager$LayoutParams r1 = r37.getAttrs()
            int r5 = r1.type
            r1 = 2000(0x7d0, float:2.803E-42)
            if (r5 != r1) goto L_0x0085
            r1 = 1
            goto L_0x0086
        L_0x0085:
            r1 = r13
        L_0x0086:
            r20 = r1
            if (r20 == 0) goto L_0x00a1
            boolean r1 = r36.isStatusBarKeyguard()
            if (r1 != 0) goto L_0x00a1
            r1 = 14342(0x3806, float:2.0097E-41)
            boolean r2 = r36.isKeyguardOccluded()
            if (r2 == 0) goto L_0x009b
            r2 = -1073741824(0xffffffffc0000000, float:-2.0)
            r1 = r1 | r2
        L_0x009b:
            int r2 = ~r1
            r2 = r2 & r0
            r3 = r8 & r1
            r0 = r2 | r3
        L_0x00a1:
            if (r16 == 0) goto L_0x00ae
            if (r17 == 0) goto L_0x00ae
            r0 = r0 | 8
            r1 = -1073741825(0xffffffffbfffffff, float:-1.9999999)
            r0 = r0 & r1
            r21 = r0
            goto L_0x00b9
        L_0x00ae:
            if (r14 == 0) goto L_0x00b7
            r1 = -1073741833(0xffffffffbffffff7, float:-1.9999989)
            r0 = r0 & r1
            r21 = r0
            goto L_0x00b9
        L_0x00b7:
            r21 = r0
        L_0x00b9:
            r3 = 0
            r0 = r36
            r1 = r21
            r2 = r9
            r4 = r11
            r22 = r5
            r5 = r18
            r23 = r6
            r6 = r19
            int r0 = r0.configureNavBarOpacity(r1, r2, r3, r4, r5, r6)
            r1 = r0 & 4096(0x1000, float:5.74E-42)
            if (r1 == 0) goto L_0x00d2
            r1 = 1
            goto L_0x00d3
        L_0x00d2:
            r1 = r13
        L_0x00d3:
            com.android.server.wm.WindowState r2 = r7.mTopFullscreenOpaqueWindowState
            if (r2 == 0) goto L_0x00e2
            r3 = 0
            int r2 = com.android.server.wm.PolicyControl.getWindowFlags(r2, r3)
            r2 = r2 & 1024(0x400, float:1.435E-42)
            if (r2 == 0) goto L_0x00e2
            r2 = 1
            goto L_0x00e3
        L_0x00e2:
            r2 = r13
        L_0x00e3:
            r3 = r0 & 4
            if (r3 == 0) goto L_0x00e9
            r3 = 1
            goto L_0x00ea
        L_0x00e9:
            r3 = r13
        L_0x00ea:
            r4 = r0 & 2
            if (r4 == 0) goto L_0x00f0
            r4 = 1
            goto L_0x00f1
        L_0x00f0:
            r4 = r13
        L_0x00f1:
            com.android.server.wm.WindowState r5 = r7.mStatusBar
            if (r5 == 0) goto L_0x0103
            if (r20 != 0) goto L_0x0101
            boolean r5 = r7.mForceShowSystemBars
            if (r5 != 0) goto L_0x0103
            if (r2 != 0) goto L_0x0101
            if (r3 == 0) goto L_0x0103
            if (r1 == 0) goto L_0x0103
        L_0x0101:
            r5 = 1
            goto L_0x0104
        L_0x0103:
            r5 = r13
        L_0x0104:
            com.android.server.wm.WindowState r6 = r7.mNavigationBar
            if (r6 == 0) goto L_0x0112
            boolean r6 = r7.mForceShowSystemBars
            if (r6 != 0) goto L_0x0112
            if (r4 == 0) goto L_0x0112
            if (r1 == 0) goto L_0x0112
            r6 = 1
            goto L_0x0113
        L_0x0112:
            r6 = r13
        L_0x0113:
            long r24 = android.os.SystemClock.uptimeMillis()
            long r12 = r7.mPendingPanicGestureUptime
            r39 = r1
            r26 = r2
            r1 = 0
            int r27 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r27 == 0) goto L_0x012d
            long r12 = r24 - r12
            r27 = 30000(0x7530, double:1.4822E-319)
            int r12 = (r12 > r27 ? 1 : (r12 == r27 ? 0 : -1))
            if (r12 > 0) goto L_0x012d
            r12 = 1
            goto L_0x012e
        L_0x012d:
            r12 = 0
        L_0x012e:
            com.android.server.wm.WindowManagerService r13 = r7.mService
            com.android.server.wm.DisplayContent r13 = r13.getDefaultDisplayContentLocked()
            com.android.server.wm.DisplayPolicy r13 = r13.getDisplayPolicy()
            if (r12 == 0) goto L_0x015a
            if (r4 == 0) goto L_0x015a
            boolean r27 = r36.isStatusBarKeyguard()
            if (r27 != 0) goto L_0x015a
            boolean r27 = r13.isKeyguardDrawComplete()
            if (r27 == 0) goto L_0x015a
            r7.mPendingPanicGestureUptime = r1
            com.android.server.wm.StatusBarController r1 = r7.mStatusBarController
            r1.showTransient()
            boolean r1 = isNavBarEmpty(r0)
            if (r1 != 0) goto L_0x015a
            com.android.server.wm.BarController r1 = r7.mNavigationBarController
            r1.showTransient()
        L_0x015a:
            com.android.server.wm.StatusBarController r1 = r7.mStatusBarController
            boolean r1 = r1.isTransientShowRequested()
            if (r1 == 0) goto L_0x0168
            if (r5 != 0) goto L_0x0168
            if (r3 == 0) goto L_0x0168
            r1 = 1
            goto L_0x0169
        L_0x0168:
            r1 = 0
        L_0x0169:
            com.android.server.wm.BarController r2 = r7.mNavigationBarController
            boolean r2 = r2.isTransientShowRequested()
            if (r2 == 0) goto L_0x0175
            if (r6 != 0) goto L_0x0175
            r2 = 1
            goto L_0x0176
        L_0x0175:
            r2 = 0
        L_0x0176:
            if (r1 != 0) goto L_0x0181
            if (r2 != 0) goto L_0x0181
            r27 = r1
            boolean r1 = r7.mForceShowSystemBars
            if (r1 == 0) goto L_0x0188
            goto L_0x0183
        L_0x0181:
            r27 = r1
        L_0x0183:
            r36.clearClearableFlagsLw()
            r0 = r0 & -8
        L_0x0188:
            r1 = r0 & 2048(0x800, float:2.87E-42)
            if (r1 == 0) goto L_0x018e
            r1 = 1
            goto L_0x018f
        L_0x018e:
            r1 = 0
        L_0x018f:
            r28 = r2
            r2 = r0 & 4096(0x1000, float:5.74E-42)
            if (r2 == 0) goto L_0x0197
            r2 = 1
            goto L_0x0198
        L_0x0197:
            r2 = 0
        L_0x0198:
            if (r1 != 0) goto L_0x01a0
            if (r2 == 0) goto L_0x019d
            goto L_0x01a0
        L_0x019d:
            r29 = 0
            goto L_0x01a2
        L_0x01a0:
            r29 = 1
        L_0x01a2:
            if (r4 == 0) goto L_0x01c5
            if (r29 != 0) goto L_0x01c5
            r39 = r1
            com.android.server.wm.WindowManagerService r1 = r7.mService
            com.android.server.policy.WindowManagerPolicy r1 = r1.mPolicy
            r30 = r2
            r2 = r37
            int r1 = r1.getWindowLayerLw(r2)
            com.android.server.wm.WindowManagerService r2 = r7.mService
            com.android.server.policy.WindowManagerPolicy r2 = r2.mPolicy
            r31 = r3
            r3 = 2022(0x7e6, float:2.833E-42)
            int r2 = r2.getWindowLayerFromTypeLw(r3)
            if (r1 <= r2) goto L_0x01cb
            r0 = r0 & -3
            goto L_0x01cb
        L_0x01c5:
            r39 = r1
            r30 = r2
            r31 = r3
        L_0x01cb:
            com.android.server.wm.StatusBarController r1 = r7.mStatusBarController
            int r0 = r1.updateVisibilityLw(r5, r8, r0)
            boolean r1 = r7.isImmersiveMode(r8)
            boolean r2 = r7.isImmersiveMode(r0)
            if (r1 == r2) goto L_0x01fb
            java.lang.String r3 = r37.getOwningPackage()
            r32 = r1
            com.android.server.wm.ImmersiveModeConfirmation r1 = r7.mImmersiveModeConfirmation
            r33 = r4
            com.android.server.wm.WindowManagerService r4 = r7.mService
            com.android.server.policy.WindowManagerPolicy r4 = r4.mPolicy
            boolean r4 = r4.isUserSetupComplete()
            int r34 = r37.getSystemUiVisibility()
            r35 = r5
            boolean r5 = isNavBarEmpty(r34)
            r1.immersiveModeChangedLw(r3, r2, r4, r5)
            goto L_0x0201
        L_0x01fb:
            r32 = r1
            r33 = r4
            r35 = r5
        L_0x0201:
            com.android.server.wm.BarController r1 = r7.mNavigationBarController
            int r0 = r1.updateVisibilityLw(r6, r8, r0)
            com.android.server.wm.WindowState r1 = r7.mTopFullscreenOpaqueWindowState
            com.android.server.wm.WindowState r3 = r7.mTopFullscreenOpaqueOrDimmingWindowState
            com.android.server.wm.DisplayContent r4 = r7.mDisplayContent
            com.android.server.wm.WindowState r4 = r4.mInputMethodWindow
            int r5 = r7.mNavigationBarPosition
            com.android.server.wm.WindowState r1 = chooseNavigationColorWindowLw(r1, r3, r4, r5)
            com.android.server.wm.WindowState r3 = r7.mTopFullscreenOpaqueWindowState
            com.android.server.wm.WindowState r4 = r7.mTopFullscreenOpaqueOrDimmingWindowState
            com.android.server.wm.DisplayContent r5 = r7.mDisplayContent
            com.android.server.wm.WindowState r5 = r5.mInputMethodWindow
            int r0 = updateLightNavigationBarLw(r0, r3, r4, r5, r1)
            if (r1 == 0) goto L_0x022c
            com.android.server.wm.DisplayContent r3 = r7.mDisplayContent
            com.android.server.wm.WindowState r3 = r3.mInputMethodWindow
            if (r1 != r3) goto L_0x022c
            r21 = 1
            goto L_0x022e
        L_0x022c:
            r21 = 0
        L_0x022e:
            r3 = r21
            com.android.server.wm.StatusBarController r4 = r7.mStatusBarController
            r7.setStatusBarController(r4)
            int r4 = r36.getExtraSystemUiVisibility(r37)
            r0 = r0 | r4
            java.lang.Integer r4 = java.lang.Integer.valueOf(r0)
            java.lang.Boolean r5 = java.lang.Boolean.valueOf(r3)
            android.util.Pair r4 = android.util.Pair.create(r4, r5)
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.DisplayPolicy.updateSystemBarsLw(com.android.server.wm.WindowState, int, int):android.util.Pair");
    }

    private boolean drawsBarBackground(int vis, WindowState win, BarController controller, int translucentFlag) {
        if (!controller.isTransparentAllowed(win)) {
            return false;
        }
        if (win == null) {
            return true;
        }
        boolean drawsSystemBars = (win.getAttrs().flags & Integer.MIN_VALUE) != 0;
        if ((win.getAttrs().privateFlags & 131072) != 0) {
            return true;
        }
        if (!drawsSystemBars || (vis & translucentFlag) != 0) {
            return false;
        }
        return true;
    }

    private boolean drawsStatusBarBackground(int vis, WindowState win) {
        return drawsBarBackground(vis, win, this.mStatusBarController, BroadcastQueueInjector.FLAG_IMMUTABLE);
    }

    private boolean drawsNavigationBarBackground(int vis, WindowState win) {
        return drawsBarBackground(vis, win, this.mNavigationBarController, 134217728);
    }

    private int configureNavBarOpacity(int visibility, boolean dockedStackVisible, boolean freeformStackVisible, boolean isDockedDividerResizing, boolean fullscreenDrawsBackground, boolean dockedDrawsNavigationBarBackground) {
        int i = this.mNavBarOpacityMode;
        if (i == 2) {
            if (fullscreenDrawsBackground && dockedDrawsNavigationBarBackground) {
                return setNavBarTransparentFlag(visibility);
            }
            if (dockedStackVisible) {
                return setNavBarOpaqueFlag(visibility);
            }
            return visibility;
        } else if (i == 0) {
            if (dockedStackVisible || freeformStackVisible || isDockedDividerResizing) {
                return setNavBarOpaqueFlag(visibility);
            }
            if (fullscreenDrawsBackground) {
                return setNavBarTransparentFlag(visibility);
            }
            return visibility;
        } else if (i != 1) {
            return visibility;
        } else {
            if (isDockedDividerResizing) {
                return setNavBarOpaqueFlag(visibility);
            }
            if (freeformStackVisible) {
                return setNavBarTranslucentFlag(visibility);
            }
            return setNavBarOpaqueFlag(visibility);
        }
    }

    private int setNavBarOpaqueFlag(int visibility) {
        return 2147450879 & visibility;
    }

    private int setNavBarTranslucentFlag(int visibility) {
        return Integer.MIN_VALUE | (visibility & -32769);
    }

    private int setNavBarTransparentFlag(int visibility) {
        return 32768 | (visibility & Integer.MAX_VALUE);
    }

    private void clearClearableFlagsLw() {
        int i = this.mResettingSystemUiFlags;
        int newVal = i | 7;
        if (newVal != i) {
            this.mResettingSystemUiFlags = newVal;
            this.mDisplayContent.reevaluateStatusBarVisibility();
        }
    }

    private boolean isImmersiveMode(int vis) {
        return (this.mNavigationBar == null || (vis & 2) == 0 || (vis & 6144) == 0 || !canHideNavigationBar()) ? false : true;
    }

    private boolean canHideNavigationBar() {
        return hasNavigationBar();
    }

    /* access modifiers changed from: private */
    public static boolean isNavBarEmpty(int systemUiFlags) {
        return (systemUiFlags & 23068672) == 23068672;
    }

    /* access modifiers changed from: package-private */
    public boolean shouldRotateSeamlessly(DisplayRotation displayRotation, int oldRotation, int newRotation) {
        WindowState w;
        if (oldRotation == displayRotation.getUpsideDownRotation() || newRotation == displayRotation.getUpsideDownRotation()) {
            return false;
        }
        if ((!navigationBarCanMove() && !this.mAllowSeamlessRotationDespiteNavBarMoving) || (w = this.mTopFullscreenOpaqueWindowState) == null || w != this.mFocusedWindow) {
            return false;
        }
        if ((w.mAppToken == null || w.mAppToken.matchParentBounds()) && !w.isAnimatingLw() && w.getAttrs().rotationAnimation == 3) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void onPowerKeyDown(boolean isScreenOn) {
        if (this.mImmersiveModeConfirmation.onPowerKeyDown(isScreenOn, SystemClock.elapsedRealtime(), isImmersiveMode(this.mLastSystemUiFlags), isNavBarEmpty(this.mLastSystemUiFlags))) {
            this.mHandler.post(this.mHiddenNavPanic);
        }
    }

    /* access modifiers changed from: package-private */
    public void onVrStateChangedLw(boolean enabled) {
        this.mImmersiveModeConfirmation.onVrStateChangedLw(enabled);
    }

    public void onLockTaskStateChangedLw(int lockTaskState) {
        this.mImmersiveModeConfirmation.onLockTaskModeChangedLw(lockTaskState);
    }

    public void takeScreenshot(int screenshotType) {
        ScreenshotHelper screenshotHelper = this.mScreenshotHelper;
        if (screenshotHelper != null) {
            WindowState windowState = this.mStatusBar;
            boolean z = true;
            boolean z2 = windowState != null && windowState.isVisibleLw();
            WindowState windowState2 = this.mNavigationBar;
            if (windowState2 == null || !windowState2.isVisibleLw()) {
                z = false;
            }
            screenshotHelper.takeScreenshot(screenshotType, z2, z, this.mHandler);
        }
    }

    /* access modifiers changed from: package-private */
    public RefreshRatePolicy getRefreshRatePolicy() {
        return this.mRefreshRatePolicy;
    }

    /* access modifiers changed from: package-private */
    public void dump(String prefix, PrintWriter pw) {
        pw.print(prefix);
        pw.print("DisplayPolicy");
        String prefix2 = prefix + "  ";
        pw.print(prefix2);
        pw.print("mCarDockEnablesAccelerometer=");
        pw.print(this.mCarDockEnablesAccelerometer);
        pw.print(" mDeskDockEnablesAccelerometer=");
        pw.println(this.mDeskDockEnablesAccelerometer);
        pw.print(prefix2);
        pw.print("mDockMode=");
        pw.print(Intent.dockStateToString(this.mDockMode));
        pw.print(" mLidState=");
        pw.println(WindowManagerPolicy.WindowManagerFuncs.lidStateToString(this.mLidState));
        pw.print(prefix2);
        pw.print("mAwake=");
        pw.print(this.mAwake);
        pw.print(" mScreenOnEarly=");
        pw.print(this.mScreenOnEarly);
        pw.print(" mScreenOnFully=");
        pw.println(this.mScreenOnFully);
        pw.print(prefix2);
        pw.print("mKeyguardDrawComplete=");
        pw.print(this.mKeyguardDrawComplete);
        pw.print(" mWindowManagerDrawComplete=");
        pw.println(this.mWindowManagerDrawComplete);
        pw.print(prefix2);
        pw.print("mHdmiPlugged=");
        pw.println(this.mHdmiPlugged);
        if (!(this.mLastSystemUiFlags == 0 && this.mResettingSystemUiFlags == 0 && this.mForceClearedSystemUiFlags == 0)) {
            pw.print(prefix2);
            pw.print("mLastSystemUiFlags=0x");
            pw.print(Integer.toHexString(this.mLastSystemUiFlags));
            pw.print(" mResettingSystemUiFlags=0x");
            pw.print(Integer.toHexString(this.mResettingSystemUiFlags));
            pw.print(" mForceClearedSystemUiFlags=0x");
            pw.println(Integer.toHexString(this.mForceClearedSystemUiFlags));
        }
        if (this.mLastFocusNeedsMenu) {
            pw.print(prefix2);
            pw.print("mLastFocusNeedsMenu=");
            pw.println(this.mLastFocusNeedsMenu);
        }
        pw.print(prefix2);
        pw.print("mShowingDream=");
        pw.print(this.mShowingDream);
        pw.print(" mDreamingLockscreen=");
        pw.print(this.mDreamingLockscreen);
        pw.print(" mDreamingSleepToken=");
        pw.println(this.mDreamingSleepToken);
        if (this.mStatusBar != null) {
            pw.print(prefix2);
            pw.print("mStatusBar=");
            pw.print(this.mStatusBar);
            pw.print(" isStatusBarKeyguard=");
            pw.println(isStatusBarKeyguard());
        }
        if (this.mNavigationBar != null) {
            pw.print(prefix2);
            pw.print("mNavigationBar=");
            pw.println(this.mNavigationBar);
            pw.print(prefix2);
            pw.print("mNavBarOpacityMode=");
            pw.println(this.mNavBarOpacityMode);
            pw.print(prefix2);
            pw.print("mNavigationBarCanMove=");
            pw.println(this.mNavigationBarCanMove);
            pw.print(prefix2);
            pw.print("mNavigationBarPosition=");
            pw.println(this.mNavigationBarPosition);
        }
        if (this.mFocusedWindow != null) {
            pw.print(prefix2);
            pw.print("mFocusedWindow=");
            pw.println(this.mFocusedWindow);
        }
        if (this.mFocusedApp != null) {
            pw.print(prefix2);
            pw.print("mFocusedApp=");
            pw.println(this.mFocusedApp);
        }
        if (this.mTopFullscreenOpaqueWindowState != null) {
            pw.print(prefix2);
            pw.print("mTopFullscreenOpaqueWindowState=");
            pw.println(this.mTopFullscreenOpaqueWindowState);
        }
        if (this.mTopFullscreenOpaqueOrDimmingWindowState != null) {
            pw.print(prefix2);
            pw.print("mTopFullscreenOpaqueOrDimmingWindowState=");
            pw.println(this.mTopFullscreenOpaqueOrDimmingWindowState);
        }
        if (this.mForcingShowNavBar) {
            pw.print(prefix2);
            pw.print("mForcingShowNavBar=");
            pw.println(this.mForcingShowNavBar);
            pw.print(prefix2);
            pw.print("mForcingShowNavBarLayer=");
            pw.println(this.mForcingShowNavBarLayer);
        }
        pw.print(prefix2);
        pw.print("mTopIsFullscreen=");
        pw.print(this.mTopIsFullscreen);
        pw.print(prefix2);
        pw.print("mForceStatusBar=");
        pw.print(this.mForceStatusBar);
        pw.print(" mForceStatusBarFromKeyguard=");
        pw.println(this.mForceStatusBarFromKeyguard);
        pw.print(" mForceShowSystemBarsFromExternal=");
        pw.println(this.mForceShowSystemBarsFromExternal);
        pw.print(prefix2);
        pw.print("mAllowLockscreenWhenOn=");
        pw.println(this.mAllowLockscreenWhenOn);
        this.mStatusBarController.dump(pw, prefix2);
        this.mNavigationBarController.dump(pw, prefix2);
        pw.print(prefix2);
        pw.println("Looper state:");
        this.mHandler.getLooper().dump(new PrintWriterPrinter(pw), prefix2 + "  ");
    }

    private boolean supportsPointerLocation() {
        return this.mDisplayContent.isDefaultDisplay || !this.mDisplayContent.isPrivate();
    }

    /* access modifiers changed from: package-private */
    public void setPointerLocationEnabled(boolean pointerLocationEnabled) {
        if (supportsPointerLocation()) {
            this.mHandler.sendEmptyMessage(pointerLocationEnabled ? 4 : 5);
        }
    }

    /* access modifiers changed from: private */
    public void enablePointerLocation() {
        if (this.mPointerLocationView == null) {
            this.mPointerLocationView = new PointerLocationView(this.mContext);
            this.mPointerLocationView.setPrintCoords(false);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams(-1, -1);
            lp.type = 2015;
            lp.flags = 1304;
            lp.layoutInDisplayCutoutMode = 1;
            if (ActivityManager.isHighEndGfx()) {
                lp.flags |= DumpState.DUMP_SERVICE_PERMISSIONS;
                lp.privateFlags |= 2;
            }
            lp.format = -3;
            lp.setTitle("PointerLocation - display " + getDisplayId());
            lp.inputFeatures = lp.inputFeatures | 2;
            ((WindowManager) this.mContext.getSystemService(WindowManager.class)).addView(this.mPointerLocationView, lp);
            this.mDisplayContent.registerPointerEventListener(this.mPointerLocationView);
        }
    }

    /* access modifiers changed from: private */
    public void disablePointerLocation() {
        PointerLocationView pointerLocationView = this.mPointerLocationView;
        if (pointerLocationView != null) {
            this.mDisplayContent.unregisterPointerEventListener(pointerLocationView);
            ((WindowManager) this.mContext.getSystemService(WindowManager.class)).removeView(this.mPointerLocationView);
            this.mPointerLocationView = null;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isWindowExcludedFromContent(WindowState w) {
        if (w != null && w.getDisplayId() != 0 && ((w.mAttrs.type == 2019 || w.mAttrs.type == 2013 || w.getActivityType() == 2) && this.mService.mForceDesktopModeOnExternalDisplays)) {
            return true;
        }
        if (w == null || this.mPointerLocationView == null) {
            return false;
        }
        if (w.mClient == this.mPointerLocationView.getWindowToken()) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void setStatusBarController(StatusBarController statusBarController) {
    }

    /* access modifiers changed from: package-private */
    public int getExtraSystemUiVisibility(WindowState win) {
        return 0;
    }

    private boolean isEnterpriseHideStatusbar() {
        return RestrictionsHelper.hasRestriction(this.mContext, "disallow_landscape_statusbar", this.mService.mAmInternal.getCurrentUserId());
    }
}
