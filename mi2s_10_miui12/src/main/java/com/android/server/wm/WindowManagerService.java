package com.android.server.wm;

import android.animation.ValueAnimator;
import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.ActivityThread;
import android.app.AppOpsManager;
import android.app.IActivityManager;
import android.app.IActivityTaskManager;
import android.app.IAssistDataReceiver;
import android.app.admin.DevicePolicyCache;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerInternal;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.graphics.Bitmap;
import android.graphics.GraphicBuffer;
import android.graphics.Insets;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.hardware.configstore.V1_0.ISurfaceFlingerConfigs;
import android.hardware.configstore.V1_0.OptionalBool;
import android.hardware.display.DisplayManager;
import android.hardware.display.DisplayManagerInternal;
import android.hardware.input.InputManager;
import android.hardware.input.InputManagerInternal;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.IRemoteCallback;
import android.os.Looper;
import android.os.Message;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.PowerManager;
import android.os.PowerManagerInternal;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.SystemService;
import android.os.Trace;
import android.os.UserHandle;
import android.os.WorkSource;
import android.provider.DeviceConfig;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.server.am.SplitScreenReporter;
import android.service.vr.IVrManager;
import android.service.vr.IVrStateCallbacks;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.BoostFramework;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.MiuiMultiWindowUtils;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.TimeUtils;
import android.util.TypedValue;
import android.util.proto.ProtoOutputStream;
import android.view.Display;
import android.view.DisplayInfo;
import android.view.IAppTransitionAnimationSpecsFuture;
import android.view.IDisplayFoldListener;
import android.view.IDockedStackListener;
import android.view.IInputFilter;
import android.view.IOnKeyguardExitResult;
import android.view.IPinnedStackListener;
import android.view.IRecentsAnimationRunner;
import android.view.IRotationWatcher;
import android.view.ISystemGestureExclusionListener;
import android.view.IWallpaperVisibilityListener;
import android.view.IWindow;
import android.view.IWindowAnimationFinishedCallback;
import android.view.IWindowId;
import android.view.IWindowManager;
import android.view.IWindowSession;
import android.view.IWindowSessionCallback;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.KeyEvent;
import android.view.MagnificationSpec;
import android.view.MotionEvent;
import android.view.RemoteAnimationAdapter;
import android.view.SurfaceControl;
import android.view.SurfaceSession;
import android.view.WindowContentFrameStats;
import android.view.WindowManager;
import android.view.WindowManagerPolicyConstants;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.BackgroundThread;
import com.android.internal.os.IResultReceiver;
import com.android.internal.policy.IKeyguardDismissCallback;
import com.android.internal.policy.IShortcutService;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FastPrintWriter;
import com.android.internal.util.LatencyTracker;
import com.android.internal.util.Preconditions;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.internal.view.WindowManagerPolicyThread;
import com.android.server.DisplayThread;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.UiThread;
import com.android.server.Watchdog;
import com.android.server.input.InputManagerService;
import com.android.server.pm.DumpState;
import com.android.server.policy.MiuiPhoneWindowManager;
import com.android.server.policy.WindowManagerPolicy;
import com.android.server.power.ShutdownThread;
import com.android.server.usb.descriptors.UsbACInterface;
import com.android.server.usb.descriptors.UsbTerminalTypes;
import com.android.server.utils.PriorityDump;
import com.android.server.wm.RecentsAnimationController;
import com.android.server.wm.WindowManagerInternal;
import com.android.server.wm.WindowManagerService;
import com.android.server.wm.WindowState;
import com.miui.enterprise.RestrictionsHelper;
import com.miui.internal.transition.IMiuiAppTransitionAnimationHelper;
import com.miui.internal.transition.IMiuiFreeFormGestureControlHelper;
import com.miui.internal.transition.IMiuiGestureControlHelper;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.net.Socket;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class WindowManagerService extends IWindowManager.Stub implements Watchdog.Monitor, WindowManagerPolicy.WindowManagerFuncs {
    private static final boolean ALWAYS_KEEP_CURRENT = true;
    private static final int ANIMATION_COMPLETED_TIMEOUT_MS = 5000;
    private static final int ANIMATION_DURATION_SCALE = 2;
    private static final int BOOT_ANIMATION_POLL_INTERVAL = 200;
    private static final String BOOT_ANIMATION_SERVICE = "bootanim";
    public static final int CAST_ROTATION_UNSPECIFIED = -1;
    static final boolean CUSTOM_SCREEN_ROTATION = true;
    static final long DEFAULT_INPUT_DISPATCHING_TIMEOUT_NANOS = 8000000000L;
    private static final String DENSITY_OVERRIDE = "ro.config.density_override";
    private static final int INPUT_DEVICES_READY_FOR_SAFE_MODE_DETECTION_TIMEOUT_MILLIS = 1000;
    static final int LAST_ANR_LIFETIME_DURATION_MSECS = 7200000;
    static final int LAYER_OFFSET_DIM = 1;
    static final int LAYER_OFFSET_THUMBNAIL = 4;
    static final int LAYOUT_REPEAT_THRESHOLD = 4;
    static final int MAX_ANIMATION_DURATION = 10000;
    private static final int MAX_SCREENSHOT_RETRIES = 3;
    private static final int MIN_GESTURE_EXCLUSION_LIMIT_DP = 200;
    static final boolean PROFILE_ORIENTATION = false;
    private static final String PROPERTY_EMULATOR_CIRCULAR = "ro.emulator.circular";
    static final int SEAMLESS_ROTATION_TIMEOUT_DURATION = 2000;
    private static final String SIZE_OVERRIDE = "ro.config.size_override";
    private static final String SYSTEM_DEBUGGABLE = "ro.debuggable";
    private static final String SYSTEM_SECURE = "ro.secure";
    private static final String TAG = "WindowManager";
    private static final int TRANSITION_ANIMATION_SCALE = 1;
    static final int TYPE_LAYER_MULTIPLIER = 10000;
    static final int TYPE_LAYER_OFFSET = 1000;
    static final int UPDATE_FOCUS_NORMAL = 0;
    static final int UPDATE_FOCUS_PLACING_SURFACES = 2;
    static final int UPDATE_FOCUS_REMOVING_FOCUS = 4;
    static final int UPDATE_FOCUS_WILL_ASSIGN_LAYERS = 1;
    static final int UPDATE_FOCUS_WILL_PLACE_SURFACES = 3;
    static final int WINDOWS_FREEZING_SCREENS_ACTIVE = 1;
    static final int WINDOWS_FREEZING_SCREENS_NONE = 0;
    static final int WINDOWS_FREEZING_SCREENS_TIMEOUT = 2;
    private static final int WINDOW_ANIMATION_SCALE = 0;
    static final int WINDOW_FREEZE_TIMEOUT_DURATION = 2000;
    static final int WINDOW_LAYER_MULTIPLIER = 5;
    static final int WINDOW_REPLACEMENT_TIMEOUT_DURATION = 2000;
    static final boolean localLOGV = false;
    static WindowState mFocusingWindow;
    /* access modifiers changed from: private */
    public static WindowManagerService sInstance;
    static WindowManagerThreadPriorityBooster sThreadPriorityBooster = new WindowManagerThreadPriorityBooster();
    boolean enableMIUIWatermark;
    boolean isFpClientOn;
    AccessibilityController mAccessibilityController;
    final IActivityManager mActivityManager;
    final WindowManagerInternal.AppTransitionListener mActivityManagerAppTransitionNotifier;
    final IActivityTaskManager mActivityTaskManager;
    final boolean mAllowAnimationsInLowPowerMode;
    final boolean mAllowBootMessages;
    boolean mAllowTheaterModeWakeFromLayout;
    final ActivityManagerInternal mAmInternal;
    final Handler mAnimationHandler;
    final ArrayMap<AnimationAdapter, SurfaceAnimator> mAnimationTransferMap;
    /* access modifiers changed from: private */
    public boolean mAnimationsDisabled;
    final WindowAnimator mAnimator;
    /* access modifiers changed from: private */
    public float mAnimatorDurationScaleSetting;
    final ArrayList<AppFreezeListener> mAppFreezeListeners;
    final AppOpsManager mAppOps;
    int mAppsFreezingScreen;
    final ActivityTaskManagerInternal mAtmInternal;
    final ActivityTaskManagerService mAtmService;
    boolean mBootAnimationStopped;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (((action.hashCode() == 988075300 && action.equals("android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED")) ? (char) 0 : 65535) == 0) {
                WindowManagerService.this.mKeyguardDisableHandler.updateKeyguardEnabled(getSendingUserId());
            }
        }
    };
    public int mCastRotation;
    private int mCastStackId;
    private WindowState mCastWindow;
    CircularDisplayMask mCircularDisplayMask;
    boolean mClientFreezingScreen;
    final Context mContext;
    int[] mCurrentProfileIds = new int[0];
    int mCurrentUserId;
    final ArrayList<WindowState> mDestroyPreservedSurface;
    final ArrayList<WindowState> mDestroySurface;
    boolean mDisableTransitionAnimation;
    boolean mDisplayEnabled;
    long mDisplayFreezeTime;
    boolean mDisplayFrozen;
    final DisplayManager mDisplayManager;
    final DisplayManagerInternal mDisplayManagerInternal;
    boolean mDisplayReady;
    final DisplayWindowSettings mDisplayWindowSettings;
    Rect mDockedStackCreateBounds;
    int mDockedStackCreateMode;
    final DragDropController mDragDropController;
    final long mDrawLockTimeoutMillis;
    AppWindowToken mDummyVisibleApp;
    boolean mDummyVisibleAppEnter;
    EmulatorDisplayOverlay mEmulatorDisplayOverlay;
    private int mEnterAnimId;
    private boolean mEventDispatchingEnabled;
    private int mExitAnimId;
    boolean mFocusMayChange;
    String mFocusingActivity;
    boolean mForceDesktopModeOnExternalDisplays;
    boolean mForceDisplayEnabled;
    final ArrayList<WindowState> mForceRemoves;
    boolean mForceResizableTasks;
    private int mFrozenDisplayId;
    final WindowManagerGlobalLock mGlobalLock;
    final H mH;
    boolean mHardKeyboardAvailable;
    WindowManagerInternal.OnHardKeyboardStatusChangeListener mHardKeyboardStatusChangeListener;
    private boolean mHasHdrSupport;
    final boolean mHasPermanentDpad;
    private boolean mHasWideColorGamutSupport;
    private ArrayList<WindowState> mHidingNonSystemOverlayWindows;
    final HighRefreshRateBlacklist mHighRefreshRateBlacklist;
    private Session mHoldingScreenOn;
    private PowerManager.WakeLock mHoldingScreenWakeLock;
    boolean mInTouchMode;
    final InputManagerService mInputManager;
    final InputManagerCallback mInputManagerCallback;
    private boolean mIsCastMode;
    /* access modifiers changed from: private */
    public boolean mIsCastModeRotationChanged;
    boolean mIsInMultiWindowMode;
    public boolean mIsInScreenProjection;
    public WindowState mIsLastFrameWin;
    boolean mIsPc;
    /* access modifiers changed from: private */
    public int mIsScreenProjectionPrivace;
    /* access modifiers changed from: private */
    public int mIsScreenProjectionSmallWindow;
    boolean mIsTouchDevice;
    /* access modifiers changed from: private */
    public final KeyguardDisableHandler mKeyguardDisableHandler;
    boolean mKeyguardGoingAway;
    boolean mKeyguardOrAodShowingOnDefaultDisplay;
    String mLastANRState;
    int mLastDisplayFreezeDuration;
    Object mLastFinishedFreezeSource;
    WindowState mLastWakeLockHoldingWindow;
    WindowState mLastWakeLockObscuringWindow;
    private final LatencyTracker mLatencyTracker;
    final boolean mLimitedAlphaCompositing;
    final boolean mLowRamTaskSnapshotsAndRecents;
    MIUIWatermark mMIUIWatermark;
    final int mMaxUiWidth;
    IMiuiAppTransitionAnimationHelper mMiuiAppTransitionAnimationHelper;
    MiuiContrastOverlay mMiuiContrastOverlay;
    MiuiFreeFormGestureController mMiuiFreeFormGestureController;
    @VisibleForTesting
    MiuiGestureController mMiuiGestureController;
    MousePositionTracker mMousePositionTracker;
    final boolean mOnlyCore;
    boolean mPendingExecuteAppTransition;
    final ArrayList<WindowState> mPendingRemove;
    WindowState[] mPendingRemoveTmp;
    @VisibleForTesting
    boolean mPerDisplayFocusEnabled;
    private BoostFramework mPerf = null;
    final PackageManagerInternal mPmInternal;
    boolean mPointerLocationEnabled;
    @VisibleForTesting
    WindowManagerPolicy mPolicy;
    PowerManager mPowerManager;
    PowerManagerInternal mPowerManagerInternal;
    private final PriorityDump.PriorityDumper mPriorityDumper = new PriorityDump.PriorityDumper() {
        public void dumpCritical(FileDescriptor fd, PrintWriter pw, String[] args, boolean asProto) {
            if (asProto && WindowManagerService.this.mWindowTracing.isEnabled()) {
                WindowManagerService.this.mWindowTracing.stopTrace((PrintWriter) null, false);
                BackgroundThread.getHandler().post(new Runnable() {
                    public final void run() {
                        WindowManagerService.AnonymousClass3.this.lambda$dumpCritical$0$WindowManagerService$3();
                    }
                });
            }
            WindowManagerService.this.doDump(fd, pw, new String[]{"-a"}, asProto);
        }

        public /* synthetic */ void lambda$dumpCritical$0$WindowManagerService$3() {
            WindowManagerService.this.mWindowTracing.writeTraceToFile();
            WindowManagerService.this.mWindowTracing.startTrace((PrintWriter) null);
        }

        public void dump(FileDescriptor fd, PrintWriter pw, String[] args, boolean asProto) {
            WindowManagerService.this.doDump(fd, pw, args, asProto);
        }
    };
    final SparseArray<Configuration> mProcessConfigurations;
    /* access modifiers changed from: private */
    public RecentsAnimationController mRecentsAnimationController;
    final ArrayList<WindowState> mResizingWindows;
    RootWindowContainer mRoot;
    private boolean mRotatingSeamlessly;
    ArrayList<RotationWatcher> mRotationWatchers;
    boolean mSafeMode;
    AppWindowToken mSaveSurfaceByKeyguardToken;
    private final PowerManager.WakeLock mScreenFrozenLock;
    /* access modifiers changed from: private */
    public int mScreenProjectionOnOrOff;
    private int mSeamlessRotationCount;
    final ArraySet<Session> mSessions;
    SettingsObserver mSettingsObserver;
    boolean mShowAlertWindowNotifications;
    boolean mShowingBootMessages;
    StrictModeFlash mStrictModeFlash;
    boolean mSupportsFreeformWindowManagement;
    boolean mSupportsPictureInPicture;
    final SurfaceAnimationRunner mSurfaceAnimationRunner;
    SurfaceBuilderFactory mSurfaceBuilderFactory;
    SurfaceFactory mSurfaceFactory;
    boolean mSwitchingUser;
    boolean mSystemBooted;
    boolean mSystemGestureExcludedByPreQStickyImmersive;
    int mSystemGestureExclusionLimitDp;
    boolean mSystemReady;
    TalkbackWatermark mTalkbackWatermark;
    final TaskPositioningController mTaskPositioningController;
    final TaskSnapshotController mTaskSnapshotController;
    final Configuration mTempConfiguration;
    private WindowContentFrameStats mTempWindowRenderStats;
    final float[] mTmpFloats;
    final Rect mTmpRect;
    final Rect mTmpRect2;
    final Rect mTmpRect3;
    final RectF mTmpRectF;
    final Matrix mTmpTransform;
    private final SurfaceControl.Transaction mTransaction;
    TransactionFactory mTransactionFactory;
    int mTransactionSequence;
    /* access modifiers changed from: private */
    public float mTransitionAnimationScaleSetting;
    IWindowAnimationFinishedCallback mUiModeAnimFinishedCallback;
    private ViewServer mViewServer;
    int mVr2dDisplayId = -1;
    boolean mVrModeEnabled = false;
    private final IVrStateCallbacks mVrStateCallbacks = new IVrStateCallbacks.Stub() {
        public void onVrStateChanged(boolean enabled) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowManagerService.this.mVrModeEnabled = enabled;
                    WindowManagerService.this.mRoot.forAllDisplayPolicies(PooledLambda.obtainConsumer($$Lambda$h9zRxk6xP2dliCTsIiNVg_lH9kA.INSTANCE, PooledLambda.__(), Boolean.valueOf(enabled)));
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }
    };
    ArrayList<WindowState> mWaitingForDrawn;
    Runnable mWaitingForDrawnCallback;
    final WallpaperVisibilityListeners mWallpaperVisibilityListeners;
    Watermark mWatermark;
    /* access modifiers changed from: private */
    public float mWindowAnimationScaleSetting;
    final ArrayList<WindowChangeListener> mWindowChangeListeners;
    final WindowHashMap mWindowMap;
    final WindowSurfacePlacer mWindowPlacerLocked;
    final ArrayList<AppWindowToken> mWindowReplacementTimeouts;
    final WindowTracing mWindowTracing;
    boolean mWindowsChanged;
    int mWindowsFreezingScreen;

    interface AppFreezeListener {
        void onAppFreezeTimeout();
    }

    @Retention(RetentionPolicy.SOURCE)
    private @interface UpdateAnimationScaleMode {
    }

    public interface WindowChangeListener {
        void focusChanged();

        void windowsChanged();
    }

    /* access modifiers changed from: package-private */
    public int getDragLayerLocked() {
        return (this.mPolicy.getWindowLayerFromTypeLw(2016) * 10000) + 1000;
    }

    class RotationWatcher {
        final IBinder.DeathRecipient mDeathRecipient;
        final int mDisplayId;
        final IRotationWatcher mWatcher;

        RotationWatcher(IRotationWatcher watcher, IBinder.DeathRecipient deathRecipient, int displayId) {
            this.mWatcher = watcher;
            this.mDeathRecipient = deathRecipient;
            this.mDisplayId = displayId;
        }
    }

    public int getScreenProjectionState() {
        return this.mScreenProjectionOnOrOff;
    }

    public int getScreenProjectionPrivaceState() {
        return this.mIsScreenProjectionPrivace;
    }

    private final class SettingsObserver extends ContentObserver {
        private final Uri mAnimationDurationScaleUri = Settings.Global.getUriFor("animator_duration_scale");
        private final Uri mContrastAlphaUri = Settings.System.getUriFor("contrast_alpha");
        private final Uri mDarkModeContrastEnable = Settings.System.getUriFor("dark_mode_contrast_enable");
        private final Uri mDarkModeEnable = Settings.System.getUriFor("dark_mode_enable");
        private final Uri mDisplayInversionEnabledUri = Settings.Secure.getUriFor("accessibility_display_inversion_enabled");
        private final Uri mImmersiveModeConfirmationsUri = Settings.Secure.getUriFor("immersive_mode_confirmations");
        private final Uri mPointerLocationUri = Settings.System.getUriFor("pointer_location");
        private final Uri mPolicyControlUri = Settings.Global.getUriFor("policy_control");
        private final Uri mScreenProjectionHangUpOnUri = Settings.Secure.getUriFor("screen_project_hang_up_on");
        private final Uri mScreenProjectionOnOffUri = Settings.Secure.getUriFor("screen_project_in_screening");
        private final Uri mScreenProjectionPrivateOnfUri = Settings.Secure.getUriFor("screen_project_private_on");
        private final Uri mScreenProjectionSamllWindowUri = Settings.Secure.getUriFor("screen_project_small_window_on");
        private final Uri mTalkbackWatermarkEnableUri = Settings.Secure.getUriFor("talkback_watermark_enable");
        private final Uri mTransitionAnimationScaleUri = Settings.Global.getUriFor("transition_animation_scale");
        private final Uri mWindowAnimationScaleUri = Settings.Global.getUriFor("window_animation_scale");

        public SettingsObserver() {
            super(new Handler());
            ContentResolver resolver = WindowManagerService.this.mContext.getContentResolver();
            resolver.registerContentObserver(this.mDisplayInversionEnabledUri, false, this, -1);
            resolver.registerContentObserver(this.mWindowAnimationScaleUri, false, this, -1);
            resolver.registerContentObserver(this.mTransitionAnimationScaleUri, false, this, -1);
            resolver.registerContentObserver(this.mAnimationDurationScaleUri, false, this, -1);
            resolver.registerContentObserver(this.mImmersiveModeConfirmationsUri, false, this, -1);
            resolver.registerContentObserver(this.mPolicyControlUri, false, this, -1);
            resolver.registerContentObserver(this.mPointerLocationUri, false, this, -1);
            resolver.registerContentObserver(this.mTalkbackWatermarkEnableUri, false, this, -1);
            resolver.registerContentObserver(this.mScreenProjectionOnOffUri, false, this, -1);
            resolver.registerContentObserver(this.mScreenProjectionHangUpOnUri, false, this, -1);
            resolver.registerContentObserver(this.mScreenProjectionSamllWindowUri, false, this, -1);
            resolver.registerContentObserver(this.mScreenProjectionPrivateOnfUri, false, this, -1);
            resolver.registerContentObserver(this.mDarkModeEnable, false, this, -1);
            resolver.registerContentObserver(this.mDarkModeContrastEnable, false, this, -1);
            resolver.registerContentObserver(this.mContrastAlphaUri, false, this, -1);
        }

        public void onChange(boolean selfChange, Uri uri) {
            int mode;
            if (uri != null) {
                if (this.mImmersiveModeConfirmationsUri.equals(uri) || this.mPolicyControlUri.equals(uri)) {
                    updateSystemUiSettings();
                } else if (this.mDisplayInversionEnabledUri.equals(uri)) {
                    WindowManagerService.this.updateCircularDisplayMaskIfNeeded();
                } else if (this.mTalkbackWatermarkEnableUri.equals(uri)) {
                    WindowManagerService.this.updateTalkbackWatermark(true);
                } else if (this.mDarkModeEnable.equals(uri) || this.mDarkModeContrastEnable.equals(uri) || this.mContrastAlphaUri.equals(uri)) {
                    Slog.d("WindowManager", "updateContrast : " + uri);
                    WindowManagerService windowManagerService = WindowManagerService.this;
                    windowManagerService.updateContrastAlpha(true ^ windowManagerService.isFpClientOn);
                } else if (this.mScreenProjectionOnOffUri.equals(uri)) {
                    WindowManagerService windowManagerService2 = WindowManagerService.this;
                    int unused = windowManagerService2.mScreenProjectionOnOrOff = Settings.Secure.getInt(windowManagerService2.mContext.getContentResolver(), "screen_project_in_screening", 0);
                    WindowManagerService windowManagerService3 = WindowManagerService.this;
                    windowManagerService3.setScreenProjectionList(windowManagerService3.mScreenProjectionOnOrOff, WindowManagerService.this.mIsScreenProjectionPrivace);
                    if (WindowManagerService.this.mScreenProjectionOnOrOff == 0) {
                        WindowManagerService.this.mAtmInternal.exitProjectionMode();
                    }
                } else if (this.mScreenProjectionSamllWindowUri.equals(uri)) {
                    WindowManagerService windowManagerService4 = WindowManagerService.this;
                    int unused2 = windowManagerService4.mIsScreenProjectionSmallWindow = Settings.Secure.getInt(windowManagerService4.mContext.getContentResolver(), "screen_project_small_window_on", 0);
                    Slog.d("WindowManager", "mIsScreenProjectionSmallWindow = " + WindowManagerService.this.mIsScreenProjectionSmallWindow + " ,mScreenProjectionOnOrOff = " + WindowManagerService.this.mScreenProjectionOnOrOff);
                    if (WindowManagerService.this.mIsScreenProjectionSmallWindow > 0 && WindowManagerService.this.mScreenProjectionOnOrOff > 0) {
                        WindowManagerService.this.mAtmInternal.moveTopActivityToCastMode();
                    } else if (WindowManagerService.this.mScreenProjectionOnOrOff == 0 || WindowManagerService.this.mIsScreenProjectionSmallWindow == 0) {
                        WindowManagerService.this.mAtmInternal.exitCastMode();
                    }
                } else if (this.mScreenProjectionPrivateOnfUri.equals(uri)) {
                    WindowManagerService windowManagerService5 = WindowManagerService.this;
                    int unused3 = windowManagerService5.mIsScreenProjectionPrivace = Settings.Secure.getInt(windowManagerService5.mContext.getContentResolver(), "screen_project_private_on", 0);
                    Slog.d("WindowManager", "mIsScreenProjectionPrivace = " + WindowManagerService.this.mIsScreenProjectionPrivace + " ,mScreenProjectionOnOrOff = " + WindowManagerService.this.mScreenProjectionOnOrOff);
                    WindowManagerService windowManagerService6 = WindowManagerService.this;
                    windowManagerService6.setScreenProjectionList(windowManagerService6.mScreenProjectionOnOrOff, WindowManagerService.this.mIsScreenProjectionPrivace);
                } else if (this.mPointerLocationUri.equals(uri)) {
                    updatePointerLocation();
                } else {
                    if (this.mWindowAnimationScaleUri.equals(uri)) {
                        mode = 0;
                    } else if (this.mTransitionAnimationScaleUri.equals(uri)) {
                        mode = 1;
                    } else if (this.mAnimationDurationScaleUri.equals(uri)) {
                        mode = 2;
                    } else {
                        return;
                    }
                    WindowManagerService.this.mH.sendMessage(WindowManagerService.this.mH.obtainMessage(51, mode, 0));
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void updateSystemUiSettings() {
            boolean changed;
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (!ImmersiveModeConfirmation.loadSetting(WindowManagerService.this.mCurrentUserId, WindowManagerService.this.mContext)) {
                        if (!PolicyControl.reloadFromSetting(WindowManagerService.this.mContext)) {
                            changed = false;
                        }
                    }
                    changed = true;
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            if (changed) {
                WindowManagerService.this.updateRotation(false, false);
            }
        }

        /* access modifiers changed from: package-private */
        public void updatePointerLocation() {
            boolean enablePointerLocation = false;
            if (Settings.System.getIntForUser(WindowManagerService.this.mContext.getContentResolver(), "pointer_location", 0, -2) != 0) {
                enablePointerLocation = true;
            }
            if (WindowManagerService.this.mPointerLocationEnabled != enablePointerLocation) {
                WindowManagerService windowManagerService = WindowManagerService.this;
                windowManagerService.mPointerLocationEnabled = enablePointerLocation;
                synchronized (windowManagerService.mGlobalLock) {
                    try {
                        WindowManagerService.boostPriorityForLockedSection();
                        WindowManagerService.this.mRoot.forAllDisplayPolicies(PooledLambda.obtainConsumer($$Lambda$1z_bkwouqOBIC89HKBNNqb1FoaY.INSTANCE, PooledLambda.__(), Boolean.valueOf(WindowManagerService.this.mPointerLocationEnabled)));
                    } catch (Throwable th) {
                        while (true) {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                }
                WindowManagerService.resetPriorityAfterLockedSection();
            }
        }
    }

    static void boostPriorityForLockedSection() {
        sThreadPriorityBooster.boost();
    }

    static void resetPriorityAfterLockedSection() {
        sThreadPriorityBooster.reset();
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: package-private */
    public void openSurfaceTransaction() {
        try {
            Trace.traceBegin(32, "openSurfaceTransaction");
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                SurfaceControl.openTransaction();
            }
            resetPriorityAfterLockedSection();
            Trace.traceEnd(32);
        } catch (Throwable th) {
            Trace.traceEnd(32);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: package-private */
    public void closeSurfaceTransaction(String where) {
        try {
            Trace.traceBegin(32, "closeSurfaceTransaction");
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                SurfaceControl.closeTransaction();
                this.mWindowTracing.logState(where);
            }
            resetPriorityAfterLockedSection();
            Trace.traceEnd(32);
        } catch (Throwable th) {
            Trace.traceEnd(32);
            throw th;
        }
    }

    static WindowManagerService getInstance() {
        return sInstance;
    }

    public static WindowManagerService main(Context context, InputManagerService im, boolean showBootMsgs, boolean onlyCore, WindowManagerPolicy policy, ActivityTaskManagerService atm) {
        return main(context, im, showBootMsgs, onlyCore, policy, atm, $$Lambda$hBnABSAsqXWvQ0zKwHWE4BZ3Mc0.INSTANCE);
    }

    @VisibleForTesting
    public static WindowManagerService main(Context context, InputManagerService im, boolean showBootMsgs, boolean onlyCore, WindowManagerPolicy policy, ActivityTaskManagerService atm, TransactionFactory transactionFactory) {
        DisplayThread.getHandler().runWithScissors(new Runnable(context, im, showBootMsgs, onlyCore, policy, atm, transactionFactory) {
            private final /* synthetic */ Context f$0;
            private final /* synthetic */ InputManagerService f$1;
            private final /* synthetic */ boolean f$2;
            private final /* synthetic */ boolean f$3;
            private final /* synthetic */ WindowManagerPolicy f$4;
            private final /* synthetic */ ActivityTaskManagerService f$5;
            private final /* synthetic */ TransactionFactory f$6;

            {
                this.f$0 = r1;
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
            }

            public final void run() {
                WindowManagerService.sInstance = new WindowManagerService(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6);
            }
        }, 0);
        return sInstance;
    }

    private void initPolicy() {
        UiThread.getHandler().runWithScissors(new Runnable() {
            public void run() {
                WindowManagerPolicyThread.set(Thread.currentThread(), Looper.myLooper());
                WindowManagerPolicy windowManagerPolicy = WindowManagerService.this.mPolicy;
                Context context = WindowManagerService.this.mContext;
                WindowManagerService windowManagerService = WindowManagerService.this;
                windowManagerPolicy.init(context, windowManagerService, windowManagerService);
            }
        }, 0);
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.os.Binder] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onShellCommand(java.io.FileDescriptor r9, java.io.FileDescriptor r10, java.io.FileDescriptor r11, java.lang.String[] r12, android.os.ShellCallback r13, android.os.ResultReceiver r14) {
        /*
            r8 = this;
            com.android.server.wm.WindowManagerShellCommand r0 = new com.android.server.wm.WindowManagerShellCommand
            r0.<init>(r8)
            r1 = r8
            r2 = r9
            r3 = r10
            r4 = r11
            r5 = r12
            r6 = r13
            r7 = r14
            r0.exec(r1, r2, r3, r4, r5, r6, r7)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.onShellCommand(java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):void");
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v73, resolved type: com.android.server.wm.WindowManagerService$7} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v0, resolved type: android.app.AppOpsManager$OnOpChangedListener} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private WindowManagerService(android.content.Context r27, com.android.server.input.InputManagerService r28, boolean r29, boolean r30, com.android.server.policy.WindowManagerPolicy r31, com.android.server.wm.ActivityTaskManagerService r32, com.android.server.wm.TransactionFactory r33) {
        /*
            r26 = this;
            r0 = r26
            r7 = r27
            r26.<init>()
            r8 = 0
            r0.mPerf = r8
            r1 = -1
            r0.mVr2dDisplayId = r1
            r9 = 0
            r0.mVrModeEnabled = r9
            com.android.server.wm.WindowManagerService$1 r2 = new com.android.server.wm.WindowManagerService$1
            r2.<init>()
            r0.mVrStateCallbacks = r2
            com.android.server.wm.WindowManagerService$2 r2 = new com.android.server.wm.WindowManagerService$2
            r2.<init>()
            r0.mBroadcastReceiver = r2
            com.android.server.wm.WindowManagerService$3 r2 = new com.android.server.wm.WindowManagerService$3
            r2.<init>()
            r0.mPriorityDumper = r2
            int[] r2 = new int[r9]
            r0.mCurrentProfileIds = r2
            r10 = 1
            r0.mShowAlertWindowNotifications = r10
            android.util.ArraySet r2 = new android.util.ArraySet
            r2.<init>()
            r0.mSessions = r2
            com.android.server.wm.WindowHashMap r2 = new com.android.server.wm.WindowHashMap
            r2.<init>()
            r0.mWindowMap = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0.mWindowReplacementTimeouts = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0.mResizingWindows = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0.mPendingRemove = r2
            r2 = 20
            com.android.server.wm.WindowState[] r2 = new com.android.server.wm.WindowState[r2]
            r0.mPendingRemoveTmp = r2
            android.util.SparseArray r2 = new android.util.SparseArray
            r2.<init>()
            r0.mProcessConfigurations = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0.mDestroySurface = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0.mDestroyPreservedSurface = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0.mForceRemoves = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0.mWaitingForDrawn = r2
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0.mHidingNonSystemOverlayWindows = r2
            r2 = 9
            float[] r2 = new float[r2]
            r0.mTmpFloats = r2
            android.graphics.Rect r2 = new android.graphics.Rect
            r2.<init>()
            r0.mTmpRect = r2
            android.graphics.Rect r2 = new android.graphics.Rect
            r2.<init>()
            r0.mTmpRect2 = r2
            android.graphics.Rect r2 = new android.graphics.Rect
            r2.<init>()
            r0.mTmpRect3 = r2
            android.graphics.RectF r2 = new android.graphics.RectF
            r2.<init>()
            r0.mTmpRectF = r2
            android.graphics.Matrix r2 = new android.graphics.Matrix
            r2.<init>()
            r0.mTmpTransform = r2
            r0.mDisplayEnabled = r9
            r0.mSystemBooted = r9
            r0.mForceDisplayEnabled = r9
            r0.mShowingBootMessages = r9
            r0.mBootAnimationStopped = r9
            r0.mSystemReady = r9
            r0.enableMIUIWatermark = r9
            r0.mLastWakeLockHoldingWindow = r8
            r0.mLastWakeLockObscuringWindow = r8
            r0.mDockedStackCreateMode = r9
            java.util.ArrayList r2 = new java.util.ArrayList
            r2.<init>()
            r0.mRotationWatchers = r2
            com.android.server.wm.WallpaperVisibilityListeners r2 = new com.android.server.wm.WallpaperVisibilityListeners
            r2.<init>()
            r0.mWallpaperVisibilityListeners = r2
            r0.mDisplayFrozen = r9
            r2 = 0
            r0.mDisplayFreezeTime = r2
            r0.mLastDisplayFreezeDuration = r9
            r0.mLastFinishedFreezeSource = r8
            r0.mSwitchingUser = r9
            r0.mWindowsFreezingScreen = r9
            r0.mClientFreezingScreen = r9
            r0.mAppsFreezingScreen = r9
            com.android.server.wm.WindowManagerService$H r2 = new com.android.server.wm.WindowManagerService$H
            r2.<init>()
            r0.mH = r2
            android.os.Handler r2 = new android.os.Handler
            android.os.Handler r3 = com.android.server.AnimationThread.getHandler()
            android.os.Looper r3 = r3.getLooper()
            r2.<init>(r3)
            r0.mAnimationHandler = r2
            r0.mSeamlessRotationCount = r9
            r0.mRotatingSeamlessly = r9
            r0.mIsCastMode = r9
            r0.mIsCastModeRotationChanged = r9
            r0.mCastStackId = r1
            r0.mCastWindow = r8
            r0.mCastRotation = r1
            r1 = 1065353216(0x3f800000, float:1.0)
            r0.mWindowAnimationScaleSetting = r1
            r0.mTransitionAnimationScaleSetting = r1
            r0.mAnimatorDurationScaleSetting = r1
            r0.mAnimationsDisabled = r9
            r0.mPointerLocationEnabled = r9
            android.util.ArrayMap r1 = new android.util.ArrayMap
            r1.<init>()
            r0.mAnimationTransferMap = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r0.mWindowChangeListeners = r1
            r0.mWindowsChanged = r9
            android.content.res.Configuration r1 = new android.content.res.Configuration
            r1.<init>()
            r0.mTempConfiguration = r1
            com.android.server.wm.HighRefreshRateBlacklist r1 = com.android.server.wm.HighRefreshRateBlacklist.create()
            r0.mHighRefreshRateBlacklist = r1
            com.android.server.wm.-$$Lambda$XZ-U3HlCFtHp_gydNmNMeRmQMCI r1 = com.android.server.wm.$$Lambda$XZU3HlCFtHp_gydNmNMeRmQMCI.INSTANCE
            r0.mSurfaceBuilderFactory = r1
            com.android.server.wm.-$$Lambda$hBnABSAsqXWvQ0zKwHWE4BZ3Mc0 r1 = com.android.server.wm.$$Lambda$hBnABSAsqXWvQ0zKwHWE4BZ3Mc0.INSTANCE
            r0.mTransactionFactory = r1
            com.android.server.wm.-$$Lambda$6DEhn1zqxqV5_Ytb_NyzMW23Ano r1 = com.android.server.wm.$$Lambda$6DEhn1zqxqV5_Ytb_NyzMW23Ano.INSTANCE
            r0.mSurfaceFactory = r1
            com.android.server.wm.WindowManagerService$4 r1 = new com.android.server.wm.WindowManagerService$4
            r1.<init>()
            r0.mActivityManagerAppTransitionNotifier = r1
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r0.mAppFreezeListeners = r1
            com.android.server.wm.InputManagerCallback r1 = new com.android.server.wm.InputManagerCallback
            r1.<init>(r0)
            r0.mInputManagerCallback = r1
            com.android.server.wm.WindowManagerService$MousePositionTracker r1 = new com.android.server.wm.WindowManagerService$MousePositionTracker
            r1.<init>()
            r0.mMousePositionTracker = r1
            r1 = 5
            com.android.server.LockGuard.installLock((java.lang.Object) r0, (int) r1)
            com.android.server.wm.WindowManagerGlobalLock r1 = r32.getGlobalLock()
            r0.mGlobalLock = r1
            r11 = r32
            r0.mAtmService = r11
            r0.mContext = r7
            r12 = r29
            r0.mAllowBootMessages = r12
            r13 = r30
            r0.mOnlyCore = r13
            android.content.res.Resources r1 = r27.getResources()
            r2 = 17891513(0x11100b9, float:2.6632812E-38)
            boolean r1 = r1.getBoolean(r2)
            r0.mLimitedAlphaCompositing = r1
            android.content.res.Resources r1 = r27.getResources()
            r2 = 17891466(0x111008a, float:2.663268E-38)
            boolean r1 = r1.getBoolean(r2)
            r0.mHasPermanentDpad = r1
            android.content.res.Resources r1 = r27.getResources()
            r2 = 17891398(0x1110046, float:2.663249E-38)
            boolean r1 = r1.getBoolean(r2)
            r0.mInTouchMode = r1
            android.content.res.Resources r1 = r27.getResources()
            r2 = 17694801(0x10e0051, float:2.6081508E-38)
            int r1 = r1.getInteger(r2)
            long r1 = (long) r1
            r0.mDrawLockTimeoutMillis = r1
            android.content.res.Resources r1 = r27.getResources()
            r2 = 17891341(0x111000d, float:2.663233E-38)
            boolean r1 = r1.getBoolean(r2)
            r0.mAllowAnimationsInLowPowerMode = r1
            android.content.res.Resources r1 = r27.getResources()
            r2 = 17694837(0x10e0075, float:2.608161E-38)
            int r1 = r1.getInteger(r2)
            r0.mMaxUiWidth = r1
            android.content.res.Resources r1 = r27.getResources()
            r2 = 17891409(0x1110051, float:2.663252E-38)
            boolean r1 = r1.getBoolean(r2)
            r0.mDisableTransitionAnimation = r1
            android.content.res.Resources r1 = r27.getResources()
            r2 = 17891332(0x1110004, float:2.6632305E-38)
            boolean r1 = r1.getBoolean(r2)
            r0.mPerDisplayFocusEnabled = r1
            android.content.res.Resources r1 = r27.getResources()
            r2 = 17891479(0x1110097, float:2.6632717E-38)
            boolean r1 = r1.getBoolean(r2)
            r0.mLowRamTaskSnapshotsAndRecents = r1
            r14 = r28
            r0.mInputManager = r14
            java.lang.Class<android.hardware.display.DisplayManagerInternal> r1 = android.hardware.display.DisplayManagerInternal.class
            java.lang.Object r1 = com.android.server.LocalServices.getService(r1)
            android.hardware.display.DisplayManagerInternal r1 = (android.hardware.display.DisplayManagerInternal) r1
            r0.mDisplayManagerInternal = r1
            com.android.server.wm.DisplayWindowSettings r1 = new com.android.server.wm.DisplayWindowSettings
            r1.<init>(r0)
            r0.mDisplayWindowSettings = r1
            r15 = r33
            r0.mTransactionFactory = r15
            com.android.server.wm.TransactionFactory r1 = r0.mTransactionFactory
            android.view.SurfaceControl$Transaction r1 = r1.make()
            r0.mTransaction = r1
            r6 = r31
            r0.mPolicy = r6
            com.android.server.wm.WindowAnimator r1 = new com.android.server.wm.WindowAnimator
            r1.<init>(r0)
            r0.mAnimator = r1
            com.android.server.wm.RootWindowContainer r1 = new com.android.server.wm.RootWindowContainer
            r1.<init>(r0)
            r0.mRoot = r1
            com.android.server.wm.WindowSurfacePlacer r1 = new com.android.server.wm.WindowSurfacePlacer
            r1.<init>(r0)
            r0.mWindowPlacerLocked = r1
            com.android.server.wm.TaskSnapshotController r1 = new com.android.server.wm.TaskSnapshotController
            r1.<init>(r0)
            r0.mTaskSnapshotController = r1
            android.view.Choreographer r1 = android.view.Choreographer.getInstance()
            com.android.server.wm.WindowTracing r1 = com.android.server.wm.WindowTracing.createDefaultAndStartLooper(r0, r1)
            r0.mWindowTracing = r1
            java.lang.Class<com.android.server.policy.WindowManagerPolicy> r1 = com.android.server.policy.WindowManagerPolicy.class
            com.android.server.policy.WindowManagerPolicy r2 = r0.mPolicy
            com.android.server.LocalServices.addService(r1, r2)
            java.lang.String r1 = "display"
            java.lang.Object r1 = r7.getSystemService(r1)
            android.hardware.display.DisplayManager r1 = (android.hardware.display.DisplayManager) r1
            r0.mDisplayManager = r1
            android.content.Context r1 = r0.mContext
            com.android.server.policy.WindowManagerPolicy r2 = r0.mPolicy
            com.android.server.wm.WindowManagerService$H r3 = r0.mH
            com.android.server.wm.KeyguardDisableHandler r1 = com.android.server.wm.KeyguardDisableHandler.create(r1, r2, r3)
            r0.mKeyguardDisableHandler = r1
            java.lang.String r1 = "power"
            java.lang.Object r1 = r7.getSystemService(r1)
            android.os.PowerManager r1 = (android.os.PowerManager) r1
            r0.mPowerManager = r1
            java.lang.Class<android.os.PowerManagerInternal> r1 = android.os.PowerManagerInternal.class
            java.lang.Object r1 = com.android.server.LocalServices.getService(r1)
            android.os.PowerManagerInternal r1 = (android.os.PowerManagerInternal) r1
            r0.mPowerManagerInternal = r1
            android.os.PowerManagerInternal r1 = r0.mPowerManagerInternal
            if (r1 == 0) goto L_0x0273
            com.android.server.wm.WindowManagerService$6 r2 = new com.android.server.wm.WindowManagerService$6
            r2.<init>()
            r1.registerLowPowerModeObserver(r2)
            android.os.PowerManagerInternal r1 = r0.mPowerManagerInternal
            r2 = 3
            android.os.PowerSaveState r1 = r1.getLowPowerState(r2)
            boolean r1 = r1.batterySaverEnabled
            r0.mAnimationsDisabled = r1
        L_0x0273:
            android.os.PowerManager r1 = r0.mPowerManager
            java.lang.String r2 = "SCREEN_FROZEN"
            android.os.PowerManager$WakeLock r1 = r1.newWakeLock(r10, r2)
            r0.mScreenFrozenLock = r1
            android.os.PowerManager$WakeLock r1 = r0.mScreenFrozenLock
            r1.setReferenceCounted(r9)
            android.app.IActivityManager r1 = android.app.ActivityManager.getService()
            r0.mActivityManager = r1
            android.app.IActivityTaskManager r1 = android.app.ActivityTaskManager.getService()
            r0.mActivityTaskManager = r1
            java.lang.Class<android.app.ActivityManagerInternal> r1 = android.app.ActivityManagerInternal.class
            java.lang.Object r1 = com.android.server.LocalServices.getService(r1)
            android.app.ActivityManagerInternal r1 = (android.app.ActivityManagerInternal) r1
            r0.mAmInternal = r1
            java.lang.Class<com.android.server.wm.ActivityTaskManagerInternal> r1 = com.android.server.wm.ActivityTaskManagerInternal.class
            java.lang.Object r1 = com.android.server.LocalServices.getService(r1)
            com.android.server.wm.ActivityTaskManagerInternal r1 = (com.android.server.wm.ActivityTaskManagerInternal) r1
            r0.mAtmInternal = r1
            java.lang.String r1 = "appops"
            java.lang.Object r1 = r7.getSystemService(r1)
            android.app.AppOpsManager r1 = (android.app.AppOpsManager) r1
            r0.mAppOps = r1
            com.android.server.wm.WindowManagerService$7 r1 = new com.android.server.wm.WindowManagerService$7
            r1.<init>()
            r5 = r1
            android.app.AppOpsManager r1 = r0.mAppOps
            r2 = 24
            r1.startWatchingMode(r2, r8, r5)
            android.app.AppOpsManager r1 = r0.mAppOps
            r2 = 45
            r1.startWatchingMode(r2, r8, r5)
            java.lang.Class<android.content.pm.PackageManagerInternal> r1 = android.content.pm.PackageManagerInternal.class
            java.lang.Object r1 = com.android.server.LocalServices.getService(r1)
            android.content.pm.PackageManagerInternal r1 = (android.content.pm.PackageManagerInternal) r1
            r0.mPmInternal = r1
            android.content.IntentFilter r1 = new android.content.IntentFilter
            r1.<init>()
            r4 = r1
            java.lang.String r1 = "android.intent.action.PACKAGES_SUSPENDED"
            r4.addAction(r1)
            java.lang.String r1 = "android.intent.action.PACKAGES_UNSUSPENDED"
            r4.addAction(r1)
            com.android.server.wm.WindowManagerService$8 r2 = new com.android.server.wm.WindowManagerService$8
            r2.<init>()
            android.os.UserHandle r3 = android.os.UserHandle.ALL
            r16 = 0
            r17 = 0
            r1 = r27
            r18 = r4
            r19 = r5
            r5 = r16
            r6 = r17
            r1.registerReceiverAsUser(r2, r3, r4, r5, r6)
            android.content.ContentResolver r1 = r27.getContentResolver()
            float r2 = r0.mWindowAnimationScaleSetting
            java.lang.String r3 = "window_animation_scale"
            float r2 = android.provider.Settings.Global.getFloat(r1, r3, r2)
            r0.mWindowAnimationScaleSetting = r2
            android.content.res.Resources r2 = r27.getResources()
            r3 = 17105049(0x1050099, float:2.442867E-38)
            float r2 = r2.getFloat(r3)
            java.lang.String r3 = "transition_animation_scale"
            float r2 = android.provider.Settings.Global.getFloat(r1, r3, r2)
            r0.mTransitionAnimationScaleSetting = r2
            float r2 = r0.mAnimatorDurationScaleSetting
            java.lang.String r3 = "animator_duration_scale"
            float r2 = android.provider.Settings.Global.getFloat(r1, r3, r2)
            r0.setAnimatorDurationScale(r2)
            java.lang.String r2 = "force_desktop_mode_on_external_displays"
            int r2 = android.provider.Settings.Global.getInt(r1, r2, r9)
            if (r2 == 0) goto L_0x0328
            goto L_0x0329
        L_0x0328:
            r10 = r9
        L_0x0329:
            r0.mForceDesktopModeOnExternalDisplays = r10
            android.content.IntentFilter r2 = new android.content.IntentFilter
            r2.<init>()
            java.lang.String r3 = "android.app.action.DEVICE_POLICY_MANAGER_STATE_CHANGED"
            r2.addAction(r3)
            android.content.Context r3 = r0.mContext
            android.content.BroadcastReceiver r4 = r0.mBroadcastReceiver
            android.os.UserHandle r22 = android.os.UserHandle.ALL
            r24 = 0
            r25 = 0
            r20 = r3
            r21 = r4
            r23 = r2
            r20.registerReceiverAsUser(r21, r22, r23, r24, r25)
            com.android.internal.util.LatencyTracker r3 = com.android.internal.util.LatencyTracker.getInstance(r27)
            r0.mLatencyTracker = r3
            com.android.server.wm.WindowManagerService$SettingsObserver r3 = new com.android.server.wm.WindowManagerService$SettingsObserver
            r3.<init>()
            r0.mSettingsObserver = r3
            android.os.PowerManager r3 = r0.mPowerManager
            r4 = 536870922(0x2000000a, float:1.0842035E-19)
            java.lang.String r5 = "WindowManager"
            android.os.PowerManager$WakeLock r3 = r3.newWakeLock(r4, r5)
            r0.mHoldingScreenWakeLock = r3
            android.os.PowerManager$WakeLock r3 = r0.mHoldingScreenWakeLock
            r3.setReferenceCounted(r9)
            com.android.server.wm.SurfaceAnimationRunner r3 = new com.android.server.wm.SurfaceAnimationRunner
            android.os.PowerManagerInternal r4 = r0.mPowerManagerInternal
            r3.<init>(r4)
            r0.mSurfaceAnimationRunner = r3
            android.content.res.Resources r3 = r27.getResources()
            r4 = 17891357(0x111001d, float:2.6632375E-38)
            boolean r3 = r3.getBoolean(r4)
            r0.mAllowTheaterModeWakeFromLayout = r3
            com.android.server.wm.TaskPositioningController r3 = new com.android.server.wm.TaskPositioningController
            com.android.server.input.InputManagerService r4 = r0.mInputManager
            android.app.IActivityTaskManager r5 = r0.mActivityTaskManager
            com.android.server.wm.WindowManagerService$H r6 = r0.mH
            android.os.Looper r6 = r6.getLooper()
            r3.<init>(r0, r4, r5, r6)
            r0.mTaskPositioningController = r3
            com.android.server.wm.DragDropController r3 = new com.android.server.wm.DragDropController
            com.android.server.wm.WindowManagerService$H r4 = r0.mH
            android.os.Looper r4 = r4.getLooper()
            r3.<init>(r0, r4)
            r0.mDragDropController = r3
            r3 = 200(0xc8, float:2.8E-43)
            java.lang.String r4 = "android:window_manager"
            java.lang.String r5 = "system_gesture_exclusion_limit_dp"
            int r5 = android.provider.DeviceConfig.getInt(r4, r5, r9)
            int r3 = java.lang.Math.max(r3, r5)
            r0.mSystemGestureExclusionLimitDp = r3
            java.lang.String r3 = "system_gestures_excluded_by_pre_q_sticky_immersive"
            boolean r3 = android.provider.DeviceConfig.getBoolean(r4, r3, r9)
            r0.mSystemGestureExcludedByPreQStickyImmersive = r3
            android.os.HandlerExecutor r3 = new android.os.HandlerExecutor
            com.android.server.wm.WindowManagerService$H r5 = r0.mH
            r3.<init>(r5)
            com.android.server.wm.-$$Lambda$WindowManagerService$vZ2iP62NKu_V2W-h0-abrxnOgoI r5 = new com.android.server.wm.-$$Lambda$WindowManagerService$vZ2iP62NKu_V2W-h0-abrxnOgoI
            r5.<init>()
            android.provider.DeviceConfig.addOnPropertiesChangedListener(r4, r3, r5)
            java.lang.Class<com.android.server.wm.WindowManagerInternal> r3 = com.android.server.wm.WindowManagerInternal.class
            com.android.server.wm.WindowManagerService$LocalService r4 = new com.android.server.wm.WindowManagerService$LocalService
            r4.<init>()
            com.android.server.LocalServices.addService(r3, r4)
            r0.mTalkbackWatermark = r8
            r26.setGlobalShadowSettings()
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.<init>(android.content.Context, com.android.server.input.InputManagerService, boolean, boolean, com.android.server.policy.WindowManagerPolicy, com.android.server.wm.ActivityTaskManagerService, com.android.server.wm.TransactionFactory):void");
    }

    public /* synthetic */ void lambda$new$1$WindowManagerService(DeviceConfig.Properties properties) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                int exclusionLimitDp = Math.max(200, properties.getInt("system_gesture_exclusion_limit_dp", 0));
                boolean excludedByPreQSticky = DeviceConfig.getBoolean("android:window_manager", "system_gestures_excluded_by_pre_q_sticky_immersive", false);
                if (!(this.mSystemGestureExcludedByPreQStickyImmersive == excludedByPreQSticky && this.mSystemGestureExclusionLimitDp == exclusionLimitDp)) {
                    this.mSystemGestureExclusionLimitDp = exclusionLimitDp;
                    this.mSystemGestureExcludedByPreQStickyImmersive = excludedByPreQSticky;
                    this.mRoot.forAllDisplays($$Lambda$JQG7CszycLV40zONwvdlvplb1TI.INSTANCE);
                }
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    private void setGlobalShadowSettings() {
        SurfaceControl.setGlobalShadowSettings(new float[]{0.0f, 0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f, 0.2f}, 0.0f, 15.0f, 500.0f);
    }

    /* JADX INFO: finally extract failed */
    public void onInitReady() {
        initPolicy();
        Watchdog.getInstance().addMonitor(this);
        openSurfaceTransaction();
        try {
            createWatermarkInTransaction();
            closeSurfaceTransaction("createWatermarkInTransaction");
            WindowManagerPolicy windowManagerPolicy = this.mPolicy;
            if (windowManagerPolicy instanceof MiuiPhoneWindowManager) {
                ((MiuiPhoneWindowManager) windowManagerPolicy).registerMIUIWatermarkCallback(new MiuiPhoneWindowManager.MIUIWatermarkCallback() {
                    public void onShowWatermark() {
                        if (WindowManagerService.this.mMIUIWatermark != null) {
                            WindowManagerService.this.mMIUIWatermark.updateText(WindowManagerService.this);
                            WindowManagerService.this.mMIUIWatermark.showWaterMarker();
                            return;
                        }
                        WindowManagerService windowManagerService = WindowManagerService.this;
                        windowManagerService.enableMIUIWatermark = true;
                        if (windowManagerService.mSystemBooted) {
                            Slog.i("WindowManager", "initwatermark!");
                            WindowManagerService windowManagerService2 = WindowManagerService.this;
                            windowManagerService2.mMIUIWatermark = MIUIWatermark.initWatermark(windowManagerService2);
                        }
                    }

                    public void onHideWatermark() {
                        if (WindowManagerService.this.mMIUIWatermark != null) {
                            WindowManagerService.this.mMIUIWatermark.hideWaterMarker();
                        }
                    }
                });
            }
            showEmulatorDisplayOverlayIfNeeded();
        } catch (Throwable th) {
            closeSurfaceTransaction("createWatermarkInTransaction");
            throw th;
        }
    }

    public InputManagerCallback getInputManagerCallback() {
        return this.mInputManagerCallback;
    }

    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        try {
            return WindowManagerService.super.onTransact(code, data, reply, flags);
        } catch (RuntimeException e) {
            if (!(e instanceof SecurityException)) {
                Slog.wtf("WindowManager", "Window Manager Crash", e);
            }
            throw e;
        }
    }

    static boolean excludeWindowTypeFromTapOutTask(int windowType) {
        if (windowType == 2000 || windowType == 2012 || windowType == 2019) {
            return true;
        }
        return false;
    }

    /* Debug info: failed to restart local var, previous not found, register: 42 */
    /* JADX WARNING: Code restructure failed: missing block: B:357:0x06cf, code lost:
        if (r0.mCurrentFocus.mOwnerUid == r11) goto L_0x06f4;
     */
    /* JADX WARNING: Removed duplicated region for block: B:339:0x06a2 A[Catch:{ all -> 0x064c }] */
    /* JADX WARNING: Removed duplicated region for block: B:342:0x06a9  */
    /* JADX WARNING: Removed duplicated region for block: B:361:0x06f0  */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x06fa A[SYNTHETIC, Splitter:B:366:0x06fa] */
    /* JADX WARNING: Removed duplicated region for block: B:371:0x0705 A[SYNTHETIC, Splitter:B:371:0x0705] */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x073d A[Catch:{ all -> 0x08e3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:377:0x073f A[Catch:{ all -> 0x08e3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:389:0x075b A[SYNTHETIC, Splitter:B:389:0x075b] */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x0762 A[Catch:{ all -> 0x06e3 }] */
    /* JADX WARNING: Removed duplicated region for block: B:413:0x07a3 A[SYNTHETIC, Splitter:B:413:0x07a3] */
    /* JADX WARNING: Removed duplicated region for block: B:420:0x07bf  */
    /* JADX WARNING: Removed duplicated region for block: B:424:0x07c5 A[SYNTHETIC, Splitter:B:424:0x07c5] */
    /* JADX WARNING: Removed duplicated region for block: B:429:0x07d4 A[SYNTHETIC, Splitter:B:429:0x07d4] */
    /* JADX WARNING: Removed duplicated region for block: B:439:0x07fc A[SYNTHETIC, Splitter:B:439:0x07fc] */
    /* JADX WARNING: Removed duplicated region for block: B:451:0x0851 A[Catch:{ all -> 0x08d7 }] */
    /* JADX WARNING: Removed duplicated region for block: B:457:0x0866 A[Catch:{ all -> 0x08d2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:465:0x0888 A[Catch:{ all -> 0x08d2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:468:0x0895 A[Catch:{ all -> 0x08d2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:470:0x0899 A[Catch:{ all -> 0x08d2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:473:0x08a6 A[Catch:{ all -> 0x08d2 }] */
    /* JADX WARNING: Removed duplicated region for block: B:482:0x08cb  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int addWindow(com.android.server.wm.Session r43, android.view.IWindow r44, int r45, android.view.WindowManager.LayoutParams r46, int r47, int r48, android.graphics.Rect r49, android.graphics.Rect r50, android.graphics.Rect r51, android.graphics.Rect r52, android.view.DisplayCutout.ParcelableWrapper r53, android.view.InputChannel r54, android.view.InsetsState r55) {
        /*
            r42 = this;
            r13 = r42
            r14 = r43
            r15 = r46
            r12 = r48
            r11 = r54
            r0 = 1
            int[] r10 = new int[r0]
            com.android.server.policy.WindowManagerPolicy r1 = r13.mPolicy
            int r16 = r1.checkAddPermission(r15, r10)
            if (r16 == 0) goto L_0x0016
            return r16
        L_0x0016:
            android.content.Context r1 = r13.mContext
            android.app.AppOpsManager r2 = r13.mAppOps
            java.lang.String r3 = r15.packageName
            int r4 = r14.mUid
            com.android.server.wm.WindowManagerServiceInjector.adjustWindowParams(r1, r2, r15, r3, r4)
            r17 = 0
            r1 = 0
            int r9 = android.os.Binder.getCallingUid()
            int r7 = r15.type
            com.android.server.wm.WindowManagerGlobalLock r6 = r13.mGlobalLock
            monitor-enter(r6)
            boostPriorityForLockedSection()     // Catch:{ all -> 0x0943 }
            boolean r2 = r13.mDisplayReady     // Catch:{ all -> 0x0943 }
            if (r2 == 0) goto L_0x092f
            android.os.IBinder r2 = r15.token     // Catch:{ all -> 0x0943 }
            com.android.server.wm.DisplayContent r2 = r13.getDisplayContentOrCreate(r12, r2)     // Catch:{ all -> 0x0943 }
            r5 = r2
            r18 = -9
            if (r5 != 0) goto L_0x006e
            java.lang.String r0 = "WindowManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x005f }
            r2.<init>()     // Catch:{ all -> 0x005f }
            java.lang.String r3 = "Attempted to add window to a display that does not exist: "
            r2.append(r3)     // Catch:{ all -> 0x005f }
            r2.append(r12)     // Catch:{ all -> 0x005f }
            java.lang.String r3 = ".  Aborting."
            r2.append(r3)     // Catch:{ all -> 0x005f }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x005f }
            android.util.Slog.w(r0, r2)     // Catch:{ all -> 0x005f }
            monitor-exit(r6)     // Catch:{ all -> 0x005f }
            resetPriorityAfterLockedSection()
            return r18
        L_0x005f:
            r0 = move-exception
            r4 = r55
            r36 = r6
            r41 = r7
            r40 = r9
            r28 = r10
            r14 = r11
            r2 = r12
            goto L_0x0950
        L_0x006e:
            int r2 = r14.mUid     // Catch:{ all -> 0x0943 }
            boolean r2 = r5.hasAccess(r2)     // Catch:{ all -> 0x0943 }
            if (r2 != 0) goto L_0x0096
            java.lang.String r0 = "WindowManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x005f }
            r2.<init>()     // Catch:{ all -> 0x005f }
            java.lang.String r3 = "Attempted to add window to a display for which the application does not have access: "
            r2.append(r3)     // Catch:{ all -> 0x005f }
            r2.append(r12)     // Catch:{ all -> 0x005f }
            java.lang.String r3 = ".  Aborting."
            r2.append(r3)     // Catch:{ all -> 0x005f }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x005f }
            android.util.Slog.w(r0, r2)     // Catch:{ all -> 0x005f }
            monitor-exit(r6)     // Catch:{ all -> 0x005f }
            resetPriorityAfterLockedSection()
            return r18
        L_0x0096:
            com.android.server.wm.WindowHashMap r2 = r13.mWindowMap     // Catch:{ all -> 0x0943 }
            android.os.IBinder r3 = r44.asBinder()     // Catch:{ all -> 0x0943 }
            boolean r2 = r2.containsKey(r3)     // Catch:{ all -> 0x0943 }
            r19 = -5
            if (r2 == 0) goto L_0x00d7
            java.lang.String r0 = "WindowManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x00c6 }
            r2.<init>()     // Catch:{ all -> 0x00c6 }
            java.lang.String r3 = "Window "
            r2.append(r3)     // Catch:{ all -> 0x00c6 }
            r4 = r44
            r2.append(r4)     // Catch:{ all -> 0x005f }
            java.lang.String r3 = " is already added"
            r2.append(r3)     // Catch:{ all -> 0x005f }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x005f }
            android.util.Slog.w(r0, r2)     // Catch:{ all -> 0x005f }
            monitor-exit(r6)     // Catch:{ all -> 0x005f }
            resetPriorityAfterLockedSection()
            return r19
        L_0x00c6:
            r0 = move-exception
            r4 = r44
            r4 = r55
            r36 = r6
            r41 = r7
            r40 = r9
            r28 = r10
            r14 = r11
            r2 = r12
            goto L_0x0950
        L_0x00d7:
            r4 = r44
            r2 = 1000(0x3e8, float:1.401E-42)
            r8 = 0
            if (r7 < r2) goto L_0x0140
            r3 = 1999(0x7cf, float:2.801E-42)
            if (r7 > r3) goto L_0x0140
            r0 = 0
            android.os.IBinder r3 = r15.token     // Catch:{ all -> 0x005f }
            com.android.server.wm.WindowState r0 = r13.windowForClientLocked((com.android.server.wm.Session) r0, (android.os.IBinder) r3, (boolean) r8)     // Catch:{ all -> 0x005f }
            r1 = r0
            if (r1 != 0) goto L_0x010f
            java.lang.String r0 = "WindowManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x005f }
            r2.<init>()     // Catch:{ all -> 0x005f }
            java.lang.String r3 = "Attempted to add window with token that is not a window: "
            r2.append(r3)     // Catch:{ all -> 0x005f }
            android.os.IBinder r3 = r15.token     // Catch:{ all -> 0x005f }
            r2.append(r3)     // Catch:{ all -> 0x005f }
            java.lang.String r3 = ".  Aborting."
            r2.append(r3)     // Catch:{ all -> 0x005f }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x005f }
            android.util.Slog.w(r0, r2)     // Catch:{ all -> 0x005f }
            r0 = -2
            monitor-exit(r6)     // Catch:{ all -> 0x005f }
            resetPriorityAfterLockedSection()
            return r0
        L_0x010f:
            android.view.WindowManager$LayoutParams r0 = r1.mAttrs     // Catch:{ all -> 0x005f }
            int r0 = r0.type     // Catch:{ all -> 0x005f }
            if (r0 < r2) goto L_0x0140
            android.view.WindowManager$LayoutParams r0 = r1.mAttrs     // Catch:{ all -> 0x005f }
            int r0 = r0.type     // Catch:{ all -> 0x005f }
            r2 = 1999(0x7cf, float:2.801E-42)
            if (r0 > r2) goto L_0x0140
            java.lang.String r0 = "WindowManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x005f }
            r2.<init>()     // Catch:{ all -> 0x005f }
            java.lang.String r3 = "Attempted to add window with token that is a sub-window: "
            r2.append(r3)     // Catch:{ all -> 0x005f }
            android.os.IBinder r3 = r15.token     // Catch:{ all -> 0x005f }
            r2.append(r3)     // Catch:{ all -> 0x005f }
            java.lang.String r3 = ".  Aborting."
            r2.append(r3)     // Catch:{ all -> 0x005f }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x005f }
            android.util.Slog.w(r0, r2)     // Catch:{ all -> 0x005f }
            r0 = -2
            monitor-exit(r6)     // Catch:{ all -> 0x005f }
            resetPriorityAfterLockedSection()
            return r0
        L_0x0140:
            r2 = r1
            r0 = 2030(0x7ee, float:2.845E-42)
            if (r7 != r0) goto L_0x0168
            boolean r0 = r5.isPrivate()     // Catch:{ all -> 0x0158 }
            if (r0 != 0) goto L_0x0168
            java.lang.String r0 = "WindowManager"
            java.lang.String r1 = "Attempted to add private presentation window to a non-private display.  Aborting."
            android.util.Slog.w(r0, r1)     // Catch:{ all -> 0x0158 }
            r0 = -8
            monitor-exit(r6)     // Catch:{ all -> 0x0158 }
            resetPriorityAfterLockedSection()
            return r0
        L_0x0158:
            r0 = move-exception
            r4 = r55
            r1 = r2
            r36 = r6
            r41 = r7
            r40 = r9
            r28 = r10
            r14 = r11
            r2 = r12
            goto L_0x0950
        L_0x0168:
            r0 = 0
            if (r2 == 0) goto L_0x016d
            r1 = 1
            goto L_0x016e
        L_0x016d:
            r1 = r8
        L_0x016e:
            r20 = r1
            if (r20 == 0) goto L_0x0178
            android.view.WindowManager$LayoutParams r1 = r2.mAttrs     // Catch:{ all -> 0x0158 }
            android.os.IBinder r1 = r1.token     // Catch:{ all -> 0x0158 }
            goto L_0x017a
        L_0x0178:
            android.os.IBinder r1 = r15.token     // Catch:{ all -> 0x091d }
        L_0x017a:
            com.android.server.wm.WindowToken r1 = r5.getWindowToken(r1)     // Catch:{ all -> 0x091d }
            if (r20 == 0) goto L_0x0185
            android.view.WindowManager$LayoutParams r3 = r2.mAttrs     // Catch:{ all -> 0x0158 }
            int r3 = r3.type     // Catch:{ all -> 0x0158 }
            goto L_0x0186
        L_0x0185:
            r3 = r7
        L_0x0186:
            r21 = 0
            r26 = r5
            r5 = 2011(0x7db, float:2.818E-42)
            r29 = -1
            if (r1 != 0) goto L_0x032f
            r8 = 1
            if (r3 < r8) goto L_0x01bb
            r8 = 99
            if (r3 > r8) goto L_0x01bb
            java.lang.String r5 = "WindowManager"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0158 }
            r8.<init>()     // Catch:{ all -> 0x0158 }
            r31 = r0
            java.lang.String r0 = "Attempted to add application window with unknown token "
            r8.append(r0)     // Catch:{ all -> 0x0158 }
            android.os.IBinder r0 = r15.token     // Catch:{ all -> 0x0158 }
            r8.append(r0)     // Catch:{ all -> 0x0158 }
            java.lang.String r0 = ".  Aborting."
            r8.append(r0)     // Catch:{ all -> 0x0158 }
            java.lang.String r0 = r8.toString()     // Catch:{ all -> 0x0158 }
            android.util.Slog.w(r5, r0)     // Catch:{ all -> 0x0158 }
            monitor-exit(r6)     // Catch:{ all -> 0x0158 }
            resetPriorityAfterLockedSection()
            return r29
        L_0x01bb:
            r31 = r0
            if (r3 != r5) goto L_0x01e1
            java.lang.String r0 = "WindowManager"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0158 }
            r5.<init>()     // Catch:{ all -> 0x0158 }
            java.lang.String r8 = "Attempted to add input method window with unknown token "
            r5.append(r8)     // Catch:{ all -> 0x0158 }
            android.os.IBinder r8 = r15.token     // Catch:{ all -> 0x0158 }
            r5.append(r8)     // Catch:{ all -> 0x0158 }
            java.lang.String r8 = ".  Aborting."
            r5.append(r8)     // Catch:{ all -> 0x0158 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0158 }
            android.util.Slog.w(r0, r5)     // Catch:{ all -> 0x0158 }
            monitor-exit(r6)     // Catch:{ all -> 0x0158 }
            resetPriorityAfterLockedSection()
            return r29
        L_0x01e1:
            r0 = 2031(0x7ef, float:2.846E-42)
            if (r3 != r0) goto L_0x0207
            java.lang.String r0 = "WindowManager"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0158 }
            r5.<init>()     // Catch:{ all -> 0x0158 }
            java.lang.String r8 = "Attempted to add voice interaction window with unknown token "
            r5.append(r8)     // Catch:{ all -> 0x0158 }
            android.os.IBinder r8 = r15.token     // Catch:{ all -> 0x0158 }
            r5.append(r8)     // Catch:{ all -> 0x0158 }
            java.lang.String r8 = ".  Aborting."
            r5.append(r8)     // Catch:{ all -> 0x0158 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0158 }
            android.util.Slog.w(r0, r5)     // Catch:{ all -> 0x0158 }
            monitor-exit(r6)     // Catch:{ all -> 0x0158 }
            resetPriorityAfterLockedSection()
            return r29
        L_0x0207:
            r0 = 2013(0x7dd, float:2.821E-42)
            if (r3 != r0) goto L_0x022d
            java.lang.String r0 = "WindowManager"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0158 }
            r5.<init>()     // Catch:{ all -> 0x0158 }
            java.lang.String r8 = "Attempted to add wallpaper window with unknown token "
            r5.append(r8)     // Catch:{ all -> 0x0158 }
            android.os.IBinder r8 = r15.token     // Catch:{ all -> 0x0158 }
            r5.append(r8)     // Catch:{ all -> 0x0158 }
            java.lang.String r8 = ".  Aborting."
            r5.append(r8)     // Catch:{ all -> 0x0158 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0158 }
            android.util.Slog.w(r0, r5)     // Catch:{ all -> 0x0158 }
            monitor-exit(r6)     // Catch:{ all -> 0x0158 }
            resetPriorityAfterLockedSection()
            return r29
        L_0x022d:
            r8 = 2023(0x7e7, float:2.835E-42)
            if (r3 != r8) goto L_0x0253
            java.lang.String r0 = "WindowManager"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0158 }
            r5.<init>()     // Catch:{ all -> 0x0158 }
            java.lang.String r8 = "Attempted to add Dream window with unknown token "
            r5.append(r8)     // Catch:{ all -> 0x0158 }
            android.os.IBinder r8 = r15.token     // Catch:{ all -> 0x0158 }
            r5.append(r8)     // Catch:{ all -> 0x0158 }
            java.lang.String r8 = ".  Aborting."
            r5.append(r8)     // Catch:{ all -> 0x0158 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0158 }
            android.util.Slog.w(r0, r5)     // Catch:{ all -> 0x0158 }
            monitor-exit(r6)     // Catch:{ all -> 0x0158 }
            resetPriorityAfterLockedSection()
            return r29
        L_0x0253:
            r8 = 2035(0x7f3, float:2.852E-42)
            if (r3 != r8) goto L_0x0279
            java.lang.String r0 = "WindowManager"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0158 }
            r5.<init>()     // Catch:{ all -> 0x0158 }
            java.lang.String r8 = "Attempted to add QS dialog window with unknown token "
            r5.append(r8)     // Catch:{ all -> 0x0158 }
            android.os.IBinder r8 = r15.token     // Catch:{ all -> 0x0158 }
            r5.append(r8)     // Catch:{ all -> 0x0158 }
            java.lang.String r8 = ".  Aborting."
            r5.append(r8)     // Catch:{ all -> 0x0158 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0158 }
            android.util.Slog.w(r0, r5)     // Catch:{ all -> 0x0158 }
            monitor-exit(r6)     // Catch:{ all -> 0x0158 }
            resetPriorityAfterLockedSection()
            return r29
        L_0x0279:
            r8 = 2032(0x7f0, float:2.847E-42)
            if (r3 != r8) goto L_0x029f
            java.lang.String r0 = "WindowManager"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0158 }
            r5.<init>()     // Catch:{ all -> 0x0158 }
            java.lang.String r8 = "Attempted to add Accessibility overlay window with unknown token "
            r5.append(r8)     // Catch:{ all -> 0x0158 }
            android.os.IBinder r8 = r15.token     // Catch:{ all -> 0x0158 }
            r5.append(r8)     // Catch:{ all -> 0x0158 }
            java.lang.String r8 = ".  Aborting."
            r5.append(r8)     // Catch:{ all -> 0x0158 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0158 }
            android.util.Slog.w(r0, r5)     // Catch:{ all -> 0x0158 }
            monitor-exit(r6)     // Catch:{ all -> 0x0158 }
            resetPriorityAfterLockedSection()
            return r29
        L_0x029f:
            r8 = 2005(0x7d5, float:2.81E-42)
            if (r7 != r8) goto L_0x02cd
            java.lang.String r0 = r15.packageName     // Catch:{ all -> 0x0158 }
            boolean r0 = r13.doesAddToastWindowRequireToken(r0, r9, r2)     // Catch:{ all -> 0x0158 }
            if (r0 == 0) goto L_0x02cd
            java.lang.String r0 = "WindowManager"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0158 }
            r5.<init>()     // Catch:{ all -> 0x0158 }
            java.lang.String r8 = "Attempted to add a toast window with unknown token "
            r5.append(r8)     // Catch:{ all -> 0x0158 }
            android.os.IBinder r8 = r15.token     // Catch:{ all -> 0x0158 }
            r5.append(r8)     // Catch:{ all -> 0x0158 }
            java.lang.String r8 = ".  Aborting."
            r5.append(r8)     // Catch:{ all -> 0x0158 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0158 }
            android.util.Slog.w(r0, r5)     // Catch:{ all -> 0x0158 }
            monitor-exit(r6)     // Catch:{ all -> 0x0158 }
            resetPriorityAfterLockedSection()
            return r29
        L_0x02cd:
            android.os.IBinder r0 = r15.token     // Catch:{ all -> 0x031b }
            if (r0 == 0) goto L_0x02d4
            android.os.IBinder r0 = r15.token     // Catch:{ all -> 0x0158 }
            goto L_0x02d8
        L_0x02d4:
            android.os.IBinder r0 = r44.asBinder()     // Catch:{ all -> 0x031b }
        L_0x02d8:
            r32 = r3
            r3 = r0
            int r0 = r15.privateFlags     // Catch:{ all -> 0x031b }
            r23 = 1048576(0x100000, float:1.469368E-39)
            r0 = r0 & r23
            if (r0 == 0) goto L_0x02e5
            r0 = 1
            goto L_0x02e6
        L_0x02e5:
            r0 = 0
        L_0x02e6:
            r22 = r8
            r8 = r0
            com.android.server.wm.WindowToken r0 = new com.android.server.wm.WindowToken     // Catch:{ all -> 0x031b }
            r23 = 0
            boolean r5 = r14.mCanAddInternalSystemWindow     // Catch:{ all -> 0x031b }
            r27 = r1
            r1 = r0
            r34 = r2
            r2 = r42
            r4 = r7
            r22 = r5
            r35 = r26
            r12 = 2011(0x7db, float:2.818E-42)
            r5 = r23
            r36 = r6
            r6 = r35
            r12 = r7
            r7 = r22
            r1.<init>(r2, r3, r4, r5, r6, r7, r8)     // Catch:{ all -> 0x03ed }
            r22 = r21
            r8 = r31
            r26 = r34
            r23 = 2013(0x7dd, float:2.821E-42)
            r25 = 2005(0x7d5, float:2.81E-42)
            r27 = 2011(0x7db, float:2.818E-42)
            r21 = r0
            r0 = r32
            goto L_0x05e8
        L_0x031b:
            r0 = move-exception
            r34 = r2
            r36 = r6
            r2 = r48
            r4 = r55
            r41 = r7
            r40 = r9
            r28 = r10
            r14 = r11
            r1 = r34
            goto L_0x0950
        L_0x032f:
            r31 = r0
            r27 = r1
            r34 = r2
            r32 = r3
            r36 = r6
            r12 = r7
            r35 = r26
            r0 = r32
            r1 = 1
            if (r0 < r1) goto L_0x03b7
            r1 = 99
            if (r0 > r1) goto L_0x03b7
            com.android.server.wm.AppWindowToken r1 = r27.asAppWindowToken()     // Catch:{ all -> 0x03ed }
            if (r1 != 0) goto L_0x036e
            java.lang.String r2 = "WindowManager"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x03ed }
            r3.<init>()     // Catch:{ all -> 0x03ed }
            java.lang.String r4 = "Attempted to add window with non-application token "
            r3.append(r4)     // Catch:{ all -> 0x03ed }
            r8 = r27
            r3.append(r8)     // Catch:{ all -> 0x03ed }
            java.lang.String r4 = ".  Aborting."
            r3.append(r4)     // Catch:{ all -> 0x03ed }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x03ed }
            android.util.Slog.w(r2, r3)     // Catch:{ all -> 0x03ed }
            r2 = -3
            monitor-exit(r36)     // Catch:{ all -> 0x03ed }
            resetPriorityAfterLockedSection()
            return r2
        L_0x036e:
            r8 = r27
            boolean r2 = r1.removed     // Catch:{ all -> 0x03ed }
            if (r2 == 0) goto L_0x0395
            java.lang.String r2 = "WindowManager"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x03ed }
            r3.<init>()     // Catch:{ all -> 0x03ed }
            java.lang.String r4 = "Attempted to add window with exiting application token "
            r3.append(r4)     // Catch:{ all -> 0x03ed }
            r3.append(r8)     // Catch:{ all -> 0x03ed }
            java.lang.String r4 = ".  Aborting."
            r3.append(r4)     // Catch:{ all -> 0x03ed }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x03ed }
            android.util.Slog.w(r2, r3)     // Catch:{ all -> 0x03ed }
            r2 = -4
            monitor-exit(r36)     // Catch:{ all -> 0x03ed }
            resetPriorityAfterLockedSection()
            return r2
        L_0x0395:
            r2 = 3
            if (r12 != r2) goto L_0x03a8
            com.android.server.wm.WindowState r2 = r1.startingWindow     // Catch:{ all -> 0x03ed }
            if (r2 == 0) goto L_0x03a8
            java.lang.String r2 = "WindowManager"
            java.lang.String r3 = "Attempted to add starting window to token with already existing starting window"
            android.util.Slog.w(r2, r3)     // Catch:{ all -> 0x03ed }
            monitor-exit(r36)     // Catch:{ all -> 0x03ed }
            resetPriorityAfterLockedSection()
            return r19
        L_0x03a8:
            r22 = r21
            r26 = r34
            r23 = 2013(0x7dd, float:2.821E-42)
            r25 = 2005(0x7d5, float:2.81E-42)
            r27 = 2011(0x7db, float:2.818E-42)
            r21 = r8
            r8 = r1
            goto L_0x05e8
        L_0x03b7:
            r8 = r27
            r7 = 2011(0x7db, float:2.818E-42)
            if (r0 != r7) goto L_0x03fd
            int r1 = r8.windowType     // Catch:{ all -> 0x03ed }
            if (r1 == r7) goto L_0x03e3
            java.lang.String r1 = "WindowManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x03ed }
            r2.<init>()     // Catch:{ all -> 0x03ed }
            java.lang.String r3 = "Attempted to add input method window with bad token "
            r2.append(r3)     // Catch:{ all -> 0x03ed }
            android.os.IBinder r3 = r15.token     // Catch:{ all -> 0x03ed }
            r2.append(r3)     // Catch:{ all -> 0x03ed }
            java.lang.String r3 = ".  Aborting."
            r2.append(r3)     // Catch:{ all -> 0x03ed }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x03ed }
            android.util.Slog.w(r1, r2)     // Catch:{ all -> 0x03ed }
            monitor-exit(r36)     // Catch:{ all -> 0x03ed }
            resetPriorityAfterLockedSection()
            return r29
        L_0x03e3:
            r27 = r7
            r26 = r34
            r23 = 2013(0x7dd, float:2.821E-42)
            r25 = 2005(0x7d5, float:2.81E-42)
            goto L_0x05e2
        L_0x03ed:
            r0 = move-exception
            r2 = r48
            r4 = r55
            r40 = r9
            r28 = r10
            r14 = r11
            r41 = r12
            r1 = r34
            goto L_0x0950
        L_0x03fd:
            r1 = 2031(0x7ef, float:2.846E-42)
            if (r0 != r1) goto L_0x0431
            int r2 = r8.windowType     // Catch:{ all -> 0x03ed }
            if (r2 == r1) goto L_0x0427
            java.lang.String r1 = "WindowManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x03ed }
            r2.<init>()     // Catch:{ all -> 0x03ed }
            java.lang.String r3 = "Attempted to add voice interaction window with bad token "
            r2.append(r3)     // Catch:{ all -> 0x03ed }
            android.os.IBinder r3 = r15.token     // Catch:{ all -> 0x03ed }
            r2.append(r3)     // Catch:{ all -> 0x03ed }
            java.lang.String r3 = ".  Aborting."
            r2.append(r3)     // Catch:{ all -> 0x03ed }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x03ed }
            android.util.Slog.w(r1, r2)     // Catch:{ all -> 0x03ed }
            monitor-exit(r36)     // Catch:{ all -> 0x03ed }
            resetPriorityAfterLockedSection()
            return r29
        L_0x0427:
            r27 = r7
            r26 = r34
            r23 = 2013(0x7dd, float:2.821E-42)
            r25 = 2005(0x7d5, float:2.81E-42)
            goto L_0x05e2
        L_0x0431:
            r6 = 2013(0x7dd, float:2.821E-42)
            if (r0 != r6) goto L_0x0465
            int r1 = r8.windowType     // Catch:{ all -> 0x03ed }
            if (r1 == r6) goto L_0x045b
            java.lang.String r1 = "WindowManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x03ed }
            r2.<init>()     // Catch:{ all -> 0x03ed }
            java.lang.String r3 = "Attempted to add wallpaper window with bad token "
            r2.append(r3)     // Catch:{ all -> 0x03ed }
            android.os.IBinder r3 = r15.token     // Catch:{ all -> 0x03ed }
            r2.append(r3)     // Catch:{ all -> 0x03ed }
            java.lang.String r3 = ".  Aborting."
            r2.append(r3)     // Catch:{ all -> 0x03ed }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x03ed }
            android.util.Slog.w(r1, r2)     // Catch:{ all -> 0x03ed }
            monitor-exit(r36)     // Catch:{ all -> 0x03ed }
            resetPriorityAfterLockedSection()
            return r29
        L_0x045b:
            r23 = r6
            r27 = r7
            r26 = r34
            r25 = 2005(0x7d5, float:2.81E-42)
            goto L_0x05e2
        L_0x0465:
            r1 = 2023(0x7e7, float:2.835E-42)
            if (r0 != r1) goto L_0x0499
            int r2 = r8.windowType     // Catch:{ all -> 0x03ed }
            if (r2 == r1) goto L_0x048f
            java.lang.String r1 = "WindowManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x03ed }
            r2.<init>()     // Catch:{ all -> 0x03ed }
            java.lang.String r3 = "Attempted to add Dream window with bad token "
            r2.append(r3)     // Catch:{ all -> 0x03ed }
            android.os.IBinder r3 = r15.token     // Catch:{ all -> 0x03ed }
            r2.append(r3)     // Catch:{ all -> 0x03ed }
            java.lang.String r3 = ".  Aborting."
            r2.append(r3)     // Catch:{ all -> 0x03ed }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x03ed }
            android.util.Slog.w(r1, r2)     // Catch:{ all -> 0x03ed }
            monitor-exit(r36)     // Catch:{ all -> 0x03ed }
            resetPriorityAfterLockedSection()
            return r29
        L_0x048f:
            r23 = r6
            r27 = r7
            r26 = r34
            r25 = 2005(0x7d5, float:2.81E-42)
            goto L_0x05e2
        L_0x0499:
            r1 = 2032(0x7f0, float:2.847E-42)
            if (r0 != r1) goto L_0x04cd
            int r2 = r8.windowType     // Catch:{ all -> 0x03ed }
            if (r2 == r1) goto L_0x04c3
            java.lang.String r1 = "WindowManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x03ed }
            r2.<init>()     // Catch:{ all -> 0x03ed }
            java.lang.String r3 = "Attempted to add Accessibility overlay window with bad token "
            r2.append(r3)     // Catch:{ all -> 0x03ed }
            android.os.IBinder r3 = r15.token     // Catch:{ all -> 0x03ed }
            r2.append(r3)     // Catch:{ all -> 0x03ed }
            java.lang.String r3 = ".  Aborting."
            r2.append(r3)     // Catch:{ all -> 0x03ed }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x03ed }
            android.util.Slog.w(r1, r2)     // Catch:{ all -> 0x03ed }
            monitor-exit(r36)     // Catch:{ all -> 0x03ed }
            resetPriorityAfterLockedSection()
            return r29
        L_0x04c3:
            r23 = r6
            r27 = r7
            r26 = r34
            r25 = 2005(0x7d5, float:2.81E-42)
            goto L_0x05e2
        L_0x04cd:
            r1 = 2005(0x7d5, float:2.81E-42)
            if (r12 != r1) goto L_0x0528
            java.lang.String r1 = r15.packageName     // Catch:{ all -> 0x0517 }
            r5 = r34
            boolean r1 = r13.doesAddToastWindowRequireToken(r1, r9, r5)     // Catch:{ all -> 0x055f }
            r21 = r1
            if (r21 == 0) goto L_0x0505
            int r1 = r8.windowType     // Catch:{ all -> 0x055f }
            r4 = 2005(0x7d5, float:2.81E-42)
            if (r1 == r4) goto L_0x0507
            java.lang.String r1 = "WindowManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x055f }
            r2.<init>()     // Catch:{ all -> 0x055f }
            java.lang.String r3 = "Attempted to add a toast window with bad token "
            r2.append(r3)     // Catch:{ all -> 0x055f }
            android.os.IBinder r3 = r15.token     // Catch:{ all -> 0x055f }
            r2.append(r3)     // Catch:{ all -> 0x055f }
            java.lang.String r3 = ".  Aborting."
            r2.append(r3)     // Catch:{ all -> 0x055f }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x055f }
            android.util.Slog.w(r1, r2)     // Catch:{ all -> 0x055f }
            monitor-exit(r36)     // Catch:{ all -> 0x055f }
            resetPriorityAfterLockedSection()
            return r29
        L_0x0505:
            r4 = 2005(0x7d5, float:2.81E-42)
        L_0x0507:
            r25 = r4
            r26 = r5
            r23 = r6
            r27 = r7
            r22 = r21
            r21 = r8
            r8 = r31
            goto L_0x05e8
        L_0x0517:
            r0 = move-exception
            r5 = r34
            r2 = r48
            r4 = r55
            r1 = r5
            r40 = r9
            r28 = r10
            r14 = r11
            r41 = r12
            goto L_0x0950
        L_0x0528:
            r4 = r1
            r5 = r34
            r1 = 2035(0x7f3, float:2.852E-42)
            if (r12 != r1) goto L_0x056e
            int r2 = r8.windowType     // Catch:{ all -> 0x055f }
            if (r2 == r1) goto L_0x0555
            java.lang.String r1 = "WindowManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x055f }
            r2.<init>()     // Catch:{ all -> 0x055f }
            java.lang.String r3 = "Attempted to add QS dialog window with bad token "
            r2.append(r3)     // Catch:{ all -> 0x055f }
            android.os.IBinder r3 = r15.token     // Catch:{ all -> 0x055f }
            r2.append(r3)     // Catch:{ all -> 0x055f }
            java.lang.String r3 = ".  Aborting."
            r2.append(r3)     // Catch:{ all -> 0x055f }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x055f }
            android.util.Slog.w(r1, r2)     // Catch:{ all -> 0x055f }
            monitor-exit(r36)     // Catch:{ all -> 0x055f }
            resetPriorityAfterLockedSection()
            return r29
        L_0x0555:
            r25 = r4
            r26 = r5
            r23 = r6
            r27 = r7
            goto L_0x05e2
        L_0x055f:
            r0 = move-exception
            r2 = r48
            r4 = r55
            r1 = r5
            r40 = r9
            r28 = r10
            r14 = r11
            r41 = r12
            goto L_0x0950
        L_0x056e:
            com.android.server.wm.AppWindowToken r1 = r8.asAppWindowToken()     // Catch:{ all -> 0x090c }
            if (r1 == 0) goto L_0x05da
            java.lang.String r1 = "WindowManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x05c8 }
            r2.<init>()     // Catch:{ all -> 0x05c8 }
            java.lang.String r3 = "Non-null appWindowToken for system window of rootType="
            r2.append(r3)     // Catch:{ all -> 0x05c8 }
            r2.append(r0)     // Catch:{ all -> 0x05c8 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x05c8 }
            android.util.Slog.w(r1, r2)     // Catch:{ all -> 0x05c8 }
            r1 = 0
            r15.token = r1     // Catch:{ all -> 0x05c8 }
            com.android.server.wm.WindowToken r22 = new com.android.server.wm.WindowToken     // Catch:{ all -> 0x05c8 }
            android.os.IBinder r3 = r44.asBinder()     // Catch:{ all -> 0x05c8 }
            r23 = 0
            boolean r2 = r14.mCanAddInternalSystemWindow     // Catch:{ all -> 0x05c8 }
            r1 = r22
            r24 = r2
            r2 = r42
            r25 = r4
            r4 = r12
            r26 = r5
            r5 = r23
            r23 = r6
            r6 = r35
            r27 = r7
            r7 = r24
            r1.<init>(r2, r3, r4, r5, r6, r7)     // Catch:{ all -> 0x05b8 }
            r1 = r22
            r22 = r21
            r8 = r31
            r21 = r1
            goto L_0x05e8
        L_0x05b8:
            r0 = move-exception
            r2 = r48
            r4 = r55
            r40 = r9
            r28 = r10
            r14 = r11
            r41 = r12
            r1 = r26
            goto L_0x0950
        L_0x05c8:
            r0 = move-exception
            r26 = r5
            r2 = r48
            r4 = r55
            r40 = r9
            r28 = r10
            r14 = r11
            r41 = r12
            r1 = r26
            goto L_0x0950
        L_0x05da:
            r25 = r4
            r26 = r5
            r23 = r6
            r27 = r7
        L_0x05e2:
            r22 = r21
            r21 = r8
            r8 = r31
        L_0x05e8:
            com.android.server.wm.WindowState r24 = new com.android.server.wm.WindowState     // Catch:{ all -> 0x08fd }
            r7 = 0
            r28 = r10[r7]     // Catch:{ all -> 0x08fd }
            int r6 = r14.mUid     // Catch:{ all -> 0x08fd }
            boolean r5 = r14.mCanAddInternalSystemWindow     // Catch:{ all -> 0x08fd }
            r1 = r24
            r2 = r42
            r3 = r43
            r4 = r44
            r29 = r5
            r5 = r21
            r30 = r6
            r6 = r26
            r33 = r7
            r7 = r28
            r32 = r0
            r0 = r8
            r8 = r45
            r14 = r9
            r9 = r46
            r28 = r10
            r10 = r47
            r31 = r14
            r14 = r11
            r11 = r30
            r23 = r0
            r38 = r12
            r0 = r25
            r12 = r29
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12)     // Catch:{ all -> 0x08f0 }
            r1 = r24
            com.android.server.wm.WindowState$DeathRecipient r2 = r1.mDeathRecipient     // Catch:{ all -> 0x08f0 }
            if (r2 != 0) goto L_0x0659
            java.lang.String r0 = "WindowManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x064c }
            r2.<init>()     // Catch:{ all -> 0x064c }
            java.lang.String r3 = "Adding window client "
            r2.append(r3)     // Catch:{ all -> 0x064c }
            android.os.IBinder r3 = r44.asBinder()     // Catch:{ all -> 0x064c }
            r2.append(r3)     // Catch:{ all -> 0x064c }
            java.lang.String r3 = " that is dead, aborting."
            r2.append(r3)     // Catch:{ all -> 0x064c }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x064c }
            android.util.Slog.w(r0, r2)     // Catch:{ all -> 0x064c }
            r0 = -4
            monitor-exit(r36)     // Catch:{ all -> 0x064c }
            resetPriorityAfterLockedSection()
            return r0
        L_0x064c:
            r0 = move-exception
            r2 = r48
            r4 = r55
            r1 = r26
            r40 = r31
            r41 = r38
            goto L_0x0950
        L_0x0659:
            com.android.server.wm.DisplayContent r2 = r1.getDisplayContent()     // Catch:{ all -> 0x08f0 }
            if (r2 != 0) goto L_0x066b
            java.lang.String r0 = "WindowManager"
            java.lang.String r2 = "Adding window to Display that has been removed."
            android.util.Slog.w(r0, r2)     // Catch:{ all -> 0x064c }
            monitor-exit(r36)     // Catch:{ all -> 0x064c }
            resetPriorityAfterLockedSection()
            return r18
        L_0x066b:
            com.android.server.wm.DisplayPolicy r2 = r35.getDisplayPolicy()     // Catch:{ all -> 0x08f0 }
            android.view.WindowManager$LayoutParams r3 = r1.mAttrs     // Catch:{ all -> 0x08f0 }
            int r4 = android.os.Binder.getCallingPid()     // Catch:{ all -> 0x08f0 }
            int r5 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x08f0 }
            r2.adjustWindowParamsLw(r1, r3, r4, r5)     // Catch:{ all -> 0x08f0 }
            com.android.server.policy.WindowManagerPolicy r3 = r13.mPolicy     // Catch:{ all -> 0x08f0 }
            boolean r3 = r3.checkShowToOwnerOnly(r15)     // Catch:{ all -> 0x08f0 }
            r1.setShowToOwnerOnlyLocked(r3)     // Catch:{ all -> 0x08f0 }
            int r3 = r2.prepareAddWindowLw(r1, r15)     // Catch:{ all -> 0x08f0 }
            r16 = r3
            if (r16 == 0) goto L_0x0692
            monitor-exit(r36)     // Catch:{ all -> 0x064c }
            resetPriorityAfterLockedSection()
            return r16
        L_0x0692:
            if (r14 == 0) goto L_0x069c
            int r3 = r15.inputFeatures     // Catch:{ all -> 0x064c }
            r3 = r3 & 2
            if (r3 != 0) goto L_0x069c
            r3 = 1
            goto L_0x069e
        L_0x069c:
            r3 = r33
        L_0x069e:
            r18 = r3
            if (r18 == 0) goto L_0x06a5
            r1.openInputChannel(r14)     // Catch:{ all -> 0x064c }
        L_0x06a5:
            r12 = r38
            if (r12 != r0) goto L_0x06f0
            r11 = r31
            r0 = r35
            boolean r3 = r0.canAddToastWindowForUid(r11)     // Catch:{ all -> 0x06e3 }
            if (r3 != 0) goto L_0x06bf
            java.lang.String r3 = "WindowManager"
            java.lang.String r4 = "Adding more than one toast window for UID at a time."
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x06e3 }
            monitor-exit(r36)     // Catch:{ all -> 0x06e3 }
            resetPriorityAfterLockedSection()
            return r19
        L_0x06bf:
            if (r22 != 0) goto L_0x06d1
            int r3 = r15.flags     // Catch:{ all -> 0x06e3 }
            r3 = r3 & 8
            if (r3 == 0) goto L_0x06d1
            com.android.server.wm.WindowState r3 = r0.mCurrentFocus     // Catch:{ all -> 0x06e3 }
            if (r3 == 0) goto L_0x06d1
            com.android.server.wm.WindowState r3 = r0.mCurrentFocus     // Catch:{ all -> 0x06e3 }
            int r3 = r3.mOwnerUid     // Catch:{ all -> 0x06e3 }
            if (r3 == r11) goto L_0x06f4
        L_0x06d1:
            com.android.server.wm.WindowManagerService$H r3 = r13.mH     // Catch:{ all -> 0x06e3 }
            com.android.server.wm.WindowManagerService$H r4 = r13.mH     // Catch:{ all -> 0x06e3 }
            r5 = 52
            android.os.Message r4 = r4.obtainMessage(r5, r1)     // Catch:{ all -> 0x06e3 }
            android.view.WindowManager$LayoutParams r5 = r1.mAttrs     // Catch:{ all -> 0x06e3 }
            long r5 = r5.hideTimeoutMilliseconds     // Catch:{ all -> 0x06e3 }
            r3.sendMessageDelayed(r4, r5)     // Catch:{ all -> 0x06e3 }
            goto L_0x06f4
        L_0x06e3:
            r0 = move-exception
            r2 = r48
            r4 = r55
            r40 = r11
            r41 = r12
            r1 = r26
            goto L_0x0950
        L_0x06f0:
            r11 = r31
            r0 = r35
        L_0x06f4:
            r16 = 0
            com.android.server.wm.WindowState r3 = r0.mCurrentFocus     // Catch:{ all -> 0x08e3 }
            if (r3 != 0) goto L_0x06ff
            java.util.ArrayList<com.android.server.wm.WindowState> r3 = r0.mWinAddedSinceNullFocus     // Catch:{ all -> 0x06e3 }
            r3.add(r1)     // Catch:{ all -> 0x06e3 }
        L_0x06ff:
            boolean r3 = excludeWindowTypeFromTapOutTask(r12)     // Catch:{ all -> 0x08e3 }
            if (r3 == 0) goto L_0x070a
            java.util.ArrayList<com.android.server.wm.WindowState> r3 = r0.mTapExcludedWindows     // Catch:{ all -> 0x06e3 }
            r3.add(r1)     // Catch:{ all -> 0x06e3 }
        L_0x070a:
            long r3 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x08e3 }
            r24 = r3
            r1.attach()     // Catch:{ all -> 0x08e3 }
            com.android.server.wm.WindowHashMap r3 = r13.mWindowMap     // Catch:{ all -> 0x08e3 }
            android.os.IBinder r4 = r44.asBinder()     // Catch:{ all -> 0x08e3 }
            r3.put(r4, r1)     // Catch:{ all -> 0x08e3 }
            r1.initAppOpsState()     // Catch:{ all -> 0x08e3 }
            android.content.pm.PackageManagerInternal r3 = r13.mPmInternal     // Catch:{ all -> 0x08e3 }
            java.lang.String r4 = r1.getOwningPackage()     // Catch:{ all -> 0x08e3 }
            int r5 = r1.getOwningUid()     // Catch:{ all -> 0x08e3 }
            int r5 = android.os.UserHandle.getUserId(r5)     // Catch:{ all -> 0x08e3 }
            boolean r3 = r3.isPackageSuspended(r4, r5)     // Catch:{ all -> 0x08e3 }
            r10 = r3
            r1.setHiddenWhileSuspended(r10)     // Catch:{ all -> 0x08e3 }
            java.util.ArrayList<com.android.server.wm.WindowState> r3 = r13.mHidingNonSystemOverlayWindows     // Catch:{ all -> 0x08e3 }
            boolean r3 = r3.isEmpty()     // Catch:{ all -> 0x08e3 }
            if (r3 != 0) goto L_0x073f
            r3 = 1
            goto L_0x0741
        L_0x073f:
            r3 = r33
        L_0x0741:
            r9 = r3
            r1.setForceHideNonSystemOverlayWindowIfNeeded(r9)     // Catch:{ all -> 0x08e3 }
            com.android.server.wm.AppWindowToken r3 = r21.asAppWindowToken()     // Catch:{ all -> 0x08e3 }
            r8 = r3
            r3 = 3
            if (r12 != r3) goto L_0x0751
            if (r8 == 0) goto L_0x0751
            r8.startingWindow = r1     // Catch:{ all -> 0x06e3 }
        L_0x0751:
            r3 = 1
            com.android.server.wm.WindowToken r4 = r1.mToken     // Catch:{ all -> 0x08e3 }
            r4.addWindow(r1)     // Catch:{ all -> 0x08e3 }
            r4 = 2011(0x7db, float:2.818E-42)
            if (r12 != r4) goto L_0x0762
            r0.setInputMethodWindowLocked(r1)     // Catch:{ all -> 0x06e3 }
            r3 = 0
            r19 = r3
            goto L_0x079c
        L_0x0762:
            r4 = 2012(0x7dc, float:2.82E-42)
            if (r12 != r4) goto L_0x076e
            r4 = 1
            r0.computeImeTarget(r4)     // Catch:{ all -> 0x06e3 }
            r3 = 0
            r19 = r3
            goto L_0x079c
        L_0x076e:
            r4 = 2013(0x7dd, float:2.821E-42)
            if (r12 != r4) goto L_0x077e
            com.android.server.wm.WallpaperController r4 = r0.mWallpaperController     // Catch:{ all -> 0x06e3 }
            r4.clearLastWallpaperTimeoutTime()     // Catch:{ all -> 0x06e3 }
            int r4 = r0.pendingLayoutChanges     // Catch:{ all -> 0x06e3 }
            r4 = r4 | 4
            r0.pendingLayoutChanges = r4     // Catch:{ all -> 0x06e3 }
            goto L_0x079a
        L_0x077e:
            int r4 = r15.flags     // Catch:{ all -> 0x08e3 }
            r5 = 1048576(0x100000, float:1.469368E-39)
            r4 = r4 & r5
            if (r4 == 0) goto L_0x078c
            int r4 = r0.pendingLayoutChanges     // Catch:{ all -> 0x06e3 }
            r4 = r4 | 4
            r0.pendingLayoutChanges = r4     // Catch:{ all -> 0x06e3 }
            goto L_0x079a
        L_0x078c:
            com.android.server.wm.WallpaperController r4 = r0.mWallpaperController     // Catch:{ all -> 0x08e3 }
            boolean r4 = r4.isBelowWallpaperTarget(r1)     // Catch:{ all -> 0x08e3 }
            if (r4 == 0) goto L_0x079a
            int r4 = r0.pendingLayoutChanges     // Catch:{ all -> 0x06e3 }
            r4 = r4 | 4
            r0.pendingLayoutChanges = r4     // Catch:{ all -> 0x06e3 }
        L_0x079a:
            r19 = r3
        L_0x079c:
            r1.applyAdjustForImeIfNeeded()     // Catch:{ all -> 0x08e3 }
            r3 = 2034(0x7f2, float:2.85E-42)
            if (r12 != r3) goto L_0x07bf
            com.android.server.wm.RootWindowContainer r3 = r13.mRoot     // Catch:{ all -> 0x06e3 }
            r7 = r48
            com.android.server.wm.DisplayContent r3 = r3.getDisplayContent(r7)     // Catch:{ all -> 0x07b3 }
            com.android.server.wm.DockedStackDividerController r3 = r3.getDockedDividerController()     // Catch:{ all -> 0x07b3 }
            r3.setWindow(r1)     // Catch:{ all -> 0x07b3 }
            goto L_0x07c1
        L_0x07b3:
            r0 = move-exception
            r4 = r55
            r2 = r7
            r40 = r11
            r41 = r12
            r1 = r26
            goto L_0x0950
        L_0x07bf:
            r7 = r48
        L_0x07c1:
            com.android.server.wm.MiuiGestureController r3 = r13.mMiuiGestureController     // Catch:{ all -> 0x08de }
            if (r3 == 0) goto L_0x07ca
            com.android.server.wm.MiuiGestureController r3 = r13.mMiuiGestureController     // Catch:{ all -> 0x07b3 }
            r3.tryToSetGestureStubWindow(r1)     // Catch:{ all -> 0x07b3 }
        L_0x07ca:
            com.android.server.wm.WindowStateAnimator r3 = r1.mWinAnimator     // Catch:{ all -> 0x08de }
            r6 = r3
            r3 = 1
            r6.mEnterAnimationPending = r3     // Catch:{ all -> 0x08de }
            r6.mEnteringAnimation = r3     // Catch:{ all -> 0x08de }
            if (r23 == 0) goto L_0x07e6
            boolean r3 = r23.isVisible()     // Catch:{ all -> 0x07b3 }
            if (r3 == 0) goto L_0x07e6
            r5 = r23
            boolean r3 = r13.prepareWindowReplacementTransition(r5)     // Catch:{ all -> 0x07b3 }
            if (r3 != 0) goto L_0x07e8
            r13.prepareNoneTransitionForRelaunching(r5)     // Catch:{ all -> 0x07b3 }
            goto L_0x07e8
        L_0x07e6:
            r5 = r23
        L_0x07e8:
            com.android.server.wm.DisplayFrames r3 = r0.mDisplayFrames     // Catch:{ all -> 0x08de }
            r4 = r3
            android.view.DisplayInfo r3 = r0.getDisplayInfo()     // Catch:{ all -> 0x08de }
            r23 = r6
            int r6 = r3.rotation     // Catch:{ all -> 0x08de }
            com.android.server.wm.utils.WmDisplayCutout r6 = r0.calculateDisplayCutoutForRotation(r6)     // Catch:{ all -> 0x08de }
            r4.onDisplayInfoUpdated(r3, r6)     // Catch:{ all -> 0x08de }
            if (r5 == 0) goto L_0x081e
            com.android.server.wm.Task r6 = r5.getTask()     // Catch:{ all -> 0x07b3 }
            if (r6 == 0) goto L_0x081e
            android.graphics.Rect r6 = r13.mTmpRect     // Catch:{ all -> 0x07b3 }
            r27 = r3
            com.android.server.wm.Task r3 = r5.getTask()     // Catch:{ all -> 0x07b3 }
            r29 = r4
            android.graphics.Rect r4 = r13.mTmpRect     // Catch:{ all -> 0x07b3 }
            r3.getBounds(r4)     // Catch:{ all -> 0x07b3 }
            com.android.server.wm.Task r3 = r5.getTask()     // Catch:{ all -> 0x07b3 }
            boolean r3 = r3.isFloating()     // Catch:{ all -> 0x07b3 }
            r31 = r3
            r30 = r6
            goto L_0x0829
        L_0x081e:
            r27 = r3
            r29 = r4
            r3 = 0
            r4 = r33
            r30 = r3
            r31 = r4
        L_0x0829:
            android.view.WindowManager$LayoutParams r4 = r1.mAttrs     // Catch:{ all -> 0x08de }
            r3 = r2
            r34 = r5
            r5 = r30
            r6 = r29
            r35 = r2
            r2 = r7
            r7 = r31
            r37 = r8
            r8 = r49
            r38 = r9
            r9 = r50
            r39 = r10
            r10 = r51
            r40 = r11
            r11 = r52
            r41 = r12
            r12 = r53
            boolean r3 = r3.getLayoutHintLw(r4, r5, r6, r7, r8, r9, r10, r11, r12)     // Catch:{ all -> 0x08d7 }
            if (r3 == 0) goto L_0x0855
            r3 = r16 | 4
            r16 = r3
        L_0x0855:
            com.android.server.wm.InsetsStateController r3 = r0.getInsetsStateController()     // Catch:{ all -> 0x08d7 }
            android.view.InsetsState r3 = r3.getInsetsForDispatch(r1)     // Catch:{ all -> 0x08d7 }
            r4 = r55
            r4.set(r3)     // Catch:{ all -> 0x08d2 }
            boolean r3 = r13.mInTouchMode     // Catch:{ all -> 0x08d2 }
            if (r3 == 0) goto L_0x086a
            r3 = r16 | 1
            r16 = r3
        L_0x086a:
            com.android.server.wm.AppWindowToken r3 = r1.mAppToken     // Catch:{ all -> 0x08d2 }
            if (r3 == 0) goto L_0x0876
            com.android.server.wm.AppWindowToken r3 = r1.mAppToken     // Catch:{ all -> 0x08d2 }
            boolean r3 = r3.isClientHidden()     // Catch:{ all -> 0x08d2 }
            if (r3 != 0) goto L_0x087a
        L_0x0876:
            r3 = r16 | 2
            r16 = r3
        L_0x087a:
            com.android.server.wm.InputMonitor r3 = r0.getInputMonitor()     // Catch:{ all -> 0x08d2 }
            r3.setUpdateInputWindowsNeededLw()     // Catch:{ all -> 0x08d2 }
            r3 = 0
            boolean r5 = r1.canReceiveKeys()     // Catch:{ all -> 0x08d2 }
            if (r5 == 0) goto L_0x0895
            r5 = r33
            r6 = 1
            boolean r7 = r13.updateFocusedWindowLocked(r6, r5)     // Catch:{ all -> 0x08d2 }
            r3 = r7
            if (r3 == 0) goto L_0x0897
            r19 = 0
            goto L_0x0897
        L_0x0895:
            r5 = r33
        L_0x0897:
            if (r19 == 0) goto L_0x089d
            r6 = 1
            r0.computeImeTarget(r6)     // Catch:{ all -> 0x08d2 }
        L_0x089d:
            com.android.server.wm.WindowContainer r6 = r1.getParent()     // Catch:{ all -> 0x08d2 }
            r6.assignChildLayers()     // Catch:{ all -> 0x08d2 }
            if (r3 == 0) goto L_0x08af
            com.android.server.wm.InputMonitor r6 = r0.getInputMonitor()     // Catch:{ all -> 0x08d2 }
            com.android.server.wm.WindowState r7 = r0.mCurrentFocus     // Catch:{ all -> 0x08d2 }
            r6.setInputFocusLw(r7, r5)     // Catch:{ all -> 0x08d2 }
        L_0x08af:
            com.android.server.wm.InputMonitor r6 = r0.getInputMonitor()     // Catch:{ all -> 0x08d2 }
            r6.updateInputWindowsLw(r5)     // Catch:{ all -> 0x08d2 }
            boolean r5 = r1.isVisibleOrAdding()     // Catch:{ all -> 0x08d2 }
            if (r5 == 0) goto L_0x08c5
            boolean r5 = r0.updateOrientationFromAppTokens()     // Catch:{ all -> 0x08d2 }
            if (r5 == 0) goto L_0x08c5
            r5 = 1
            r17 = r5
        L_0x08c5:
            monitor-exit(r36)     // Catch:{ all -> 0x08d2 }
            resetPriorityAfterLockedSection()
            if (r17 == 0) goto L_0x08ce
            r13.sendNewConfiguration(r2)
        L_0x08ce:
            android.os.Binder.restoreCallingIdentity(r24)
            return r16
        L_0x08d2:
            r0 = move-exception
            r1 = r26
            goto L_0x0950
        L_0x08d7:
            r0 = move-exception
            r4 = r55
            r1 = r26
            goto L_0x0950
        L_0x08de:
            r0 = move-exception
            r4 = r55
            r2 = r7
            goto L_0x08e8
        L_0x08e3:
            r0 = move-exception
            r2 = r48
            r4 = r55
        L_0x08e8:
            r40 = r11
            r41 = r12
            r1 = r26
            goto L_0x0950
        L_0x08f0:
            r0 = move-exception
            r2 = r48
            r4 = r55
            r40 = r31
            r41 = r38
            r1 = r26
            goto L_0x0950
        L_0x08fd:
            r0 = move-exception
            r2 = r48
            r4 = r55
            r40 = r9
            r28 = r10
            r14 = r11
            r41 = r12
            r1 = r26
            goto L_0x0950
        L_0x090c:
            r0 = move-exception
            r2 = r48
            r4 = r55
            r26 = r5
            r40 = r9
            r28 = r10
            r14 = r11
            r41 = r12
            r1 = r26
            goto L_0x0950
        L_0x091d:
            r0 = move-exception
            r4 = r55
            r26 = r2
            r36 = r6
            r41 = r7
            r40 = r9
            r28 = r10
            r14 = r11
            r2 = r12
            r1 = r26
            goto L_0x0950
        L_0x092f:
            r4 = r55
            r36 = r6
            r41 = r7
            r40 = r9
            r28 = r10
            r14 = r11
            r2 = r12
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException     // Catch:{ all -> 0x0955 }
            java.lang.String r3 = "Display has not been initialialized"
            r0.<init>(r3)     // Catch:{ all -> 0x0955 }
            throw r0     // Catch:{ all -> 0x0955 }
        L_0x0943:
            r0 = move-exception
            r4 = r55
            r36 = r6
            r41 = r7
            r40 = r9
            r28 = r10
            r14 = r11
            r2 = r12
        L_0x0950:
            monitor-exit(r36)     // Catch:{ all -> 0x0955 }
            resetPriorityAfterLockedSection()
            throw r0
        L_0x0955:
            r0 = move-exception
            goto L_0x0950
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.addWindow(com.android.server.wm.Session, android.view.IWindow, int, android.view.WindowManager$LayoutParams, int, int, android.graphics.Rect, android.graphics.Rect, android.graphics.Rect, android.graphics.Rect, android.view.DisplayCutout$ParcelableWrapper, android.view.InputChannel, android.view.InsetsState):int");
    }

    private DisplayContent getDisplayContentOrCreate(int displayId, IBinder token) {
        Display display;
        WindowToken wToken;
        if (token != null && (wToken = this.mRoot.getWindowToken(token)) != null) {
            return wToken.getDisplayContent();
        }
        DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
        if (displayContent != null || (display = this.mDisplayManager.getDisplay(displayId)) == null) {
            return displayContent;
        }
        return this.mRoot.createDisplayContent(display, (ActivityDisplay) null);
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    private boolean doesAddToastWindowRequireToken(String packageName, int callingUid, WindowState attachedWindow) {
        if (attachedWindow == null) {
            try {
                ApplicationInfo appInfo = this.mContext.getPackageManager().getApplicationInfoAsUser(packageName, 0, UserHandle.getUserId(callingUid));
                if (appInfo.uid == callingUid) {
                    return appInfo.targetSdkVersion >= 26;
                }
                throw new SecurityException("Package " + packageName + " not in UID " + callingUid);
            } catch (PackageManager.NameNotFoundException e) {
            }
        } else if (attachedWindow.mAppToken == null || attachedWindow.mAppToken.mTargetSdk < 26) {
            return false;
        } else {
            return true;
        }
    }

    private boolean prepareWindowReplacementTransition(AppWindowToken atoken) {
        atoken.clearAllDrawn();
        WindowState replacedWindow = atoken.getReplacingWindow();
        if (replacedWindow == null) {
            return false;
        }
        Rect frame = replacedWindow.getVisibleFrameLw();
        DisplayContent dc = atoken.getDisplayContent();
        dc.mOpeningApps.add(atoken);
        dc.prepareAppTransition(18, true, 0, false);
        dc.mAppTransition.overridePendingAppTransitionClipReveal(frame.left, frame.top, frame.width(), frame.height());
        dc.executeAppTransition();
        return true;
    }

    private void prepareNoneTransitionForRelaunching(AppWindowToken atoken) {
        DisplayContent dc = atoken.getDisplayContent();
        if (this.mDisplayFrozen && !dc.mOpeningApps.contains(atoken) && atoken.isRelaunching()) {
            dc.mOpeningApps.add(atoken);
            dc.prepareAppTransition(0, false, 0, false);
            dc.executeAppTransition();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isSecureLocked(WindowState w) {
        if ((w.mAttrs.flags & 8192) == 0 && !DevicePolicyCache.getInstance().getScreenCaptureDisabled(UserHandle.getUserId(w.mOwnerUid)) && !RestrictionsHelper.hasRestriction(this.mContext, "disallow_screencapture", UserHandle.getUserId(w.mOwnerUid))) {
            return false;
        }
        return true;
    }

    public void refreshScreenCaptureDisabled(int userId) {
        if (Binder.getCallingUid() == 1000) {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    this.mRoot.setSecureSurfaceState(userId, DevicePolicyCache.getInstance().getScreenCaptureDisabled(userId));
                } catch (Throwable th) {
                    while (true) {
                        resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            resetPriorityAfterLockedSection();
            return;
        }
        throw new SecurityException("Only system can call refreshScreenCaptureDisabled.");
    }

    /* access modifiers changed from: package-private */
    public void removeWindow(Session session, IWindow client) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                WindowState win = windowForClientLocked(session, client, false);
                if (win == null) {
                    resetPriorityAfterLockedSection();
                    return;
                }
                win.removeIfPossible();
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void postWindowRemoveCleanupLocked(WindowState win) {
        this.mWindowMap.remove(win.mClient.asBinder());
        markForSeamlessRotation(win, false);
        win.resetAppOpsState();
        DisplayContent dc = win.getDisplayContent();
        if (dc.mCurrentFocus == null) {
            dc.mWinRemovedSinceNullFocus.add(win);
        }
        this.mPendingRemove.remove(win);
        this.mResizingWindows.remove(win);
        updateNonSystemOverlayWindowsVisibilityIfNeeded(win, false);
        this.mWindowsChanged = true;
        DisplayContent displayContent = win.getDisplayContent();
        if (displayContent.mInputMethodWindow == win) {
            displayContent.setInputMethodWindowLocked((WindowState) null);
        }
        WindowToken token = win.mToken;
        AppWindowToken atoken = win.mAppToken;
        if (token.isEmpty()) {
            if (!token.mPersistOnEmpty) {
                token.removeImmediately();
            } else if (atoken != null) {
                atoken.firstWindowDrawn = false;
                atoken.clearAllDrawn();
                TaskStack stack = atoken.getStack();
                if (stack != null) {
                    stack.mExitingAppTokens.remove(atoken);
                }
            }
        }
        if (atoken != null) {
            atoken.postWindowRemoveStartingWindowCleanup(win);
        }
        if (win.mAttrs.type == 2013) {
            dc.mWallpaperController.clearLastWallpaperTimeoutTime();
            dc.pendingLayoutChanges |= 4;
        } else if ((win.mAttrs.flags & DumpState.DUMP_DEXOPT) != 0) {
            dc.pendingLayoutChanges |= 4;
        }
        if (!this.mWindowPlacerLocked.isInLayout()) {
            dc.assignWindowLayers(true);
            this.mWindowPlacerLocked.performSurfacePlacement();
            if (win.mAppToken != null) {
                win.mAppToken.updateReportedVisibilityLocked();
            }
        }
        dc.getInputMonitor().updateInputWindowsLw(true);
    }

    /* access modifiers changed from: private */
    public void updateHiddenWhileSuspendedState(ArraySet<String> packages, boolean suspended) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mRoot.updateHiddenWhileSuspendedState(packages, suspended);
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    /* access modifiers changed from: private */
    public void updateAppOpsState() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mRoot.updateAppOpsState();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    static void logSurface(WindowState w, String msg, boolean withStackTrace) {
        String str = "  SURFACE " + msg + ": " + w;
        if (withStackTrace) {
            logWithStack("WindowManager", str);
        } else {
            Slog.i("WindowManager", str);
        }
    }

    static void logSurface(SurfaceControl s, String title, String msg) {
        Slog.i("WindowManager", "  SURFACE " + s + ": " + msg + " / " + title);
    }

    static void logWithStack(String tag, String s) {
        Slog.i(tag, s, (Throwable) null);
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    /* access modifiers changed from: package-private */
    public void setTransparentRegionWindow(Session session, IWindow client, Region region) {
        long origId = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                WindowState w = windowForClientLocked(session, client, false);
                if (w != null && w.mHasSurface) {
                    w.mWinAnimator.setTransparentRegionHintLocked(region);
                }
            }
            resetPriorityAfterLockedSection();
            Binder.restoreCallingIdentity(origId);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(origId);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    /* access modifiers changed from: package-private */
    public void setInsetsWindow(Session session, IWindow client, int touchableInsets, Rect contentInsets, Rect visibleInsets, Region touchableRegion) {
        long origId = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                WindowState w = windowForClientLocked(session, client, false);
                if (w != null) {
                    w.mGivenInsetsPending = false;
                    w.mGivenContentInsets.set(contentInsets);
                    w.mGivenVisibleInsets.set(visibleInsets);
                    w.mGivenTouchableRegion.set(touchableRegion);
                    w.mTouchableInsets = touchableInsets;
                    if (w.mGlobalScale != 1.0f) {
                        w.mGivenContentInsets.scale(w.mGlobalScale);
                        w.mGivenVisibleInsets.scale(w.mGlobalScale);
                        w.mGivenTouchableRegion.scale(w.mGlobalScale);
                    }
                    w.setDisplayLayoutNeeded();
                    this.mWindowPlacerLocked.performSurfacePlacement();
                    if (this.mAccessibilityController != null && (w.getDisplayContent().getDisplayId() == 0 || w.getDisplayContent().getParentWindow() != null)) {
                        this.mAccessibilityController.onSomeWindowResizedOrMovedLocked();
                    }
                }
            }
            resetPriorityAfterLockedSection();
            Binder.restoreCallingIdentity(origId);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(origId);
            throw th;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0028, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x002b, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void getWindowDisplayFrame(com.android.server.wm.Session r4, android.view.IWindow r5, android.graphics.Rect r6) {
        /*
            r3 = this;
            com.android.server.wm.WindowManagerGlobalLock r0 = r3.mGlobalLock
            monitor-enter(r0)
            boostPriorityForLockedSection()     // Catch:{ all -> 0x002c }
            r1 = 0
            com.android.server.wm.WindowState r1 = r3.windowForClientLocked((com.android.server.wm.Session) r4, (android.view.IWindow) r5, (boolean) r1)     // Catch:{ all -> 0x002c }
            if (r1 != 0) goto L_0x0015
            r6.setEmpty()     // Catch:{ all -> 0x002c }
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            resetPriorityAfterLockedSection()
            return
        L_0x0015:
            android.graphics.Rect r2 = r1.getDisplayFrameLw()     // Catch:{ all -> 0x002c }
            r6.set(r2)     // Catch:{ all -> 0x002c }
            boolean r2 = r1.inSizeCompatMode()     // Catch:{ all -> 0x002c }
            if (r2 == 0) goto L_0x0027
            float r2 = r1.mInvGlobalScale     // Catch:{ all -> 0x002c }
            r6.scale(r2)     // Catch:{ all -> 0x002c }
        L_0x0027:
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            resetPriorityAfterLockedSection()
            return
        L_0x002c:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            resetPriorityAfterLockedSection()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.getWindowDisplayFrame(com.android.server.wm.Session, android.view.IWindow, android.graphics.Rect):void");
    }

    public void onRectangleOnScreenRequested(IBinder token, Rect rectangle) {
        WindowState window;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                if (!(this.mAccessibilityController == null || (window = (WindowState) this.mWindowMap.get(token)) == null)) {
                    this.mAccessibilityController.onRectangleOnScreenRequestedLocked(window.getDisplayId(), rectangle);
                }
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public IWindowId getWindowId(IBinder token) {
        WindowState.WindowId windowId;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                WindowState window = (WindowState) this.mWindowMap.get(token);
                windowId = window != null ? window.mWindowId : null;
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
        return windowId;
    }

    public void pokeDrawLock(Session session, IBinder token) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                WindowState window = windowForClientLocked(session, token, false);
                if (window != null) {
                    window.pokeDrawLockLw(this.mDrawLockTimeoutMillis);
                }
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    private boolean hasStatusBarPermission(int pid, int uid) {
        return this.mContext.checkPermission("android.permission.STATUS_BAR", pid, uid) == 0;
    }

    /* Debug info: failed to restart local var, previous not found, register: 37 */
    /* JADX WARNING: Code restructure failed: missing block: B:130:0x020c, code lost:
        if (r15.mAttrs.surfaceInsets.bottom == 0) goto L_0x021e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:321:0x04cc, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:322:0x04cf, code lost:
        if (r9 == false) goto L_0x04e1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:323:0x04d1, code lost:
        android.os.Trace.traceBegin(32, "relayoutWindow: sendNewConfiguration");
        sendNewConfiguration(r21);
        android.os.Trace.traceEnd(32);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x04e1, code lost:
        r11 = r21;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:325:0x04e3, code lost:
        android.os.Binder.restoreCallingIdentity(r33);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:326:0x04e6, code lost:
        return r7;
     */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x0257 A[SYNTHETIC, Splitter:B:164:0x0257] */
    /* JADX WARNING: Removed duplicated region for block: B:171:0x0266 A[Catch:{ all -> 0x020f }] */
    /* JADX WARNING: Removed duplicated region for block: B:172:0x0269 A[Catch:{ all -> 0x020f }] */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x0277 A[Catch:{ all -> 0x020f }] */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x0283  */
    /* JADX WARNING: Removed duplicated region for block: B:183:0x0293 A[Catch:{ all -> 0x050a }] */
    /* JADX WARNING: Removed duplicated region for block: B:184:0x0295 A[Catch:{ all -> 0x050a }] */
    /* JADX WARNING: Removed duplicated region for block: B:187:0x029a A[SYNTHETIC, Splitter:B:187:0x029a] */
    /* JADX WARNING: Removed duplicated region for block: B:201:0x02bc A[Catch:{ all -> 0x020f }] */
    /* JADX WARNING: Removed duplicated region for block: B:213:0x02dd A[Catch:{ all -> 0x020f }] */
    /* JADX WARNING: Removed duplicated region for block: B:217:0x02e5 A[Catch:{ all -> 0x020f }] */
    /* JADX WARNING: Removed duplicated region for block: B:247:0x037c  */
    /* JADX WARNING: Removed duplicated region for block: B:263:0x03d9 A[Catch:{ all -> 0x0502 }] */
    /* JADX WARNING: Removed duplicated region for block: B:264:0x03de A[Catch:{ all -> 0x0502 }] */
    /* JADX WARNING: Removed duplicated region for block: B:267:0x03e4 A[Catch:{ all -> 0x0502 }] */
    /* JADX WARNING: Removed duplicated region for block: B:268:0x03e6 A[Catch:{ all -> 0x0502 }] */
    /* JADX WARNING: Removed duplicated region for block: B:270:0x03e9 A[Catch:{ all -> 0x0502 }] */
    /* JADX WARNING: Removed duplicated region for block: B:273:0x03f4 A[Catch:{ all -> 0x0502 }] */
    /* JADX WARNING: Removed duplicated region for block: B:275:0x03f7 A[Catch:{ all -> 0x0502 }] */
    /* JADX WARNING: Removed duplicated region for block: B:278:0x0403 A[Catch:{ all -> 0x0502 }] */
    /* JADX WARNING: Removed duplicated region for block: B:281:0x0418 A[Catch:{ all -> 0x0502 }] */
    /* JADX WARNING: Removed duplicated region for block: B:290:0x0430 A[Catch:{ all -> 0x0502 }] */
    /* JADX WARNING: Removed duplicated region for block: B:291:0x0443 A[Catch:{ all -> 0x0502 }] */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x044b A[Catch:{ all -> 0x0502 }] */
    /* JADX WARNING: Removed duplicated region for block: B:297:0x0454 A[Catch:{ all -> 0x0502 }] */
    /* JADX WARNING: Removed duplicated region for block: B:300:0x0462 A[Catch:{ all -> 0x0502 }] */
    /* JADX WARNING: Removed duplicated region for block: B:303:0x046b A[Catch:{ all -> 0x0502 }] */
    /* JADX WARNING: Removed duplicated region for block: B:305:0x0470  */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x0476 A[Catch:{ all -> 0x04e9 }] */
    /* JADX WARNING: Removed duplicated region for block: B:317:0x04c1 A[Catch:{ all -> 0x04ef, all -> 0x0539 }] */
    /* JADX WARNING: Removed duplicated region for block: B:318:0x04c4 A[Catch:{ all -> 0x04ef, all -> 0x0539 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int relayoutWindow(com.android.server.wm.Session r38, android.view.IWindow r39, int r40, android.view.WindowManager.LayoutParams r41, int r42, int r43, int r44, int r45, long r46, android.graphics.Rect r48, android.graphics.Rect r49, android.graphics.Rect r50, android.graphics.Rect r51, android.graphics.Rect r52, android.graphics.Rect r53, android.graphics.Rect r54, android.view.DisplayCutout.ParcelableWrapper r55, android.util.MergedConfiguration r56, android.view.SurfaceControl r57, android.view.InsetsState r58) {
        /*
            r37 = this;
            r1 = r37
            r2 = r39
            r3 = r41
            r4 = r44
            r5 = r56
            r6 = r57
            r7 = 0
            int r8 = android.os.Binder.getCallingPid()
            int r9 = android.os.Binder.getCallingUid()
            long r10 = android.os.Binder.clearCallingIdentity()
            com.android.server.wm.WindowManagerGlobalLock r12 = r1.mGlobalLock
            monitor-enter(r12)
            boostPriorityForLockedSection()     // Catch:{ all -> 0x0528 }
            r13 = 0
            r14 = r38
            com.android.server.wm.WindowState r0 = r1.windowForClientLocked((com.android.server.wm.Session) r14, (android.view.IWindow) r2, (boolean) r13)     // Catch:{ all -> 0x0528 }
            r15 = r0
            if (r15 != 0) goto L_0x003c
            monitor-exit(r12)     // Catch:{ all -> 0x002e }
            resetPriorityAfterLockedSection()
            return r13
        L_0x002e:
            r0 = move-exception
            r13 = r48
            r15 = r55
            r4 = r5
            r23 = r8
            r27 = r9
            r33 = r10
            goto L_0x0534
        L_0x003c:
            int r0 = r15.getDisplayId()     // Catch:{ all -> 0x0528 }
            r21 = r0
            com.android.server.wm.DisplayContent r0 = r15.getDisplayContent()     // Catch:{ all -> 0x0528 }
            r22 = r0
            com.android.server.wm.DisplayPolicy r0 = r22.getDisplayPolicy()     // Catch:{ all -> 0x0528 }
            r23 = r0
            android.content.Context r0 = r1.mContext     // Catch:{ all -> 0x0528 }
            android.app.AppOpsManager r13 = r1.mAppOps     // Catch:{ all -> 0x0528 }
            java.lang.String r14 = r15.getOwningPackage()     // Catch:{ all -> 0x0528 }
            int r5 = r15.getOwningUid()     // Catch:{ all -> 0x0520 }
            com.android.server.wm.WindowManagerServiceInjector.adjustWindowParams(r0, r13, r3, r14, r5)     // Catch:{ all -> 0x0520 }
            com.android.server.wm.WindowStateAnimator r0 = r15.mWinAnimator     // Catch:{ all -> 0x0520 }
            r5 = r0
            r0 = 8
            if (r4 == r0) goto L_0x007b
            r13 = r42
            r14 = r43
            r15.setRequestedSize(r13, r14)     // Catch:{ all -> 0x006c }
            goto L_0x007f
        L_0x006c:
            r0 = move-exception
            r13 = r48
            r15 = r55
            r4 = r56
            r23 = r8
            r27 = r9
            r33 = r10
            goto L_0x0534
        L_0x007b:
            r13 = r42
            r14 = r43
        L_0x007f:
            r13 = r46
            r15.setFrameNumber(r13)     // Catch:{ all -> 0x0520 }
            com.android.server.wm.DisplayContent r16 = r15.getDisplayContent()     // Catch:{ all -> 0x0520 }
            r24 = r16
            r13 = r24
            boolean r14 = r13.mWaitingForConfig     // Catch:{ all -> 0x0520 }
            if (r14 != 0) goto L_0x0094
            r14 = 0
            r15.finishSeamlessRotation(r14)     // Catch:{ all -> 0x006c }
        L_0x0094:
            r14 = 0
            r16 = 0
            r17 = 0
            r24 = r13
            if (r3 == 0) goto L_0x01ae
            r13 = r23
            r13.adjustWindowParamsLw(r15, r3, r8, r9)     // Catch:{ all -> 0x019f }
            int r0 = r15.mSeq     // Catch:{ all -> 0x019f }
            r20 = r14
            r14 = r40
            if (r14 != r0) goto L_0x00c0
            int r0 = r3.systemUiVisibility     // Catch:{ all -> 0x006c }
            int r14 = r3.subtreeSystemUiVisibility     // Catch:{ all -> 0x006c }
            r0 = r0 | r14
            r14 = 67043328(0x3ff0000, float:1.4987553E-36)
            r14 = r14 & r0
            if (r14 == 0) goto L_0x00be
            boolean r14 = r1.hasStatusBarPermission(r8, r9)     // Catch:{ all -> 0x006c }
            if (r14 != 0) goto L_0x00be
            r14 = -67043329(0xfffffffffc00ffff, float:-2.679225E36)
            r0 = r0 & r14
        L_0x00be:
            r15.mSystemUiVisibility = r0     // Catch:{ all -> 0x006c }
        L_0x00c0:
            android.view.WindowManager$LayoutParams r0 = r15.mAttrs     // Catch:{ all -> 0x019f }
            int r0 = r0.type     // Catch:{ all -> 0x019f }
            int r14 = r3.type     // Catch:{ all -> 0x019f }
            if (r0 != r14) goto L_0x0195
            int r0 = r3.privateFlags     // Catch:{ all -> 0x019f }
            r0 = r0 & 8192(0x2000, float:1.14794E-41)
            if (r0 == 0) goto L_0x00e6
            android.view.WindowManager$LayoutParams r0 = r15.mAttrs     // Catch:{ all -> 0x006c }
            int r0 = r0.x     // Catch:{ all -> 0x006c }
            r3.x = r0     // Catch:{ all -> 0x006c }
            android.view.WindowManager$LayoutParams r0 = r15.mAttrs     // Catch:{ all -> 0x006c }
            int r0 = r0.y     // Catch:{ all -> 0x006c }
            r3.y = r0     // Catch:{ all -> 0x006c }
            android.view.WindowManager$LayoutParams r0 = r15.mAttrs     // Catch:{ all -> 0x006c }
            int r0 = r0.width     // Catch:{ all -> 0x006c }
            r3.width = r0     // Catch:{ all -> 0x006c }
            android.view.WindowManager$LayoutParams r0 = r15.mAttrs     // Catch:{ all -> 0x006c }
            int r0 = r0.height     // Catch:{ all -> 0x006c }
            r3.height = r0     // Catch:{ all -> 0x006c }
        L_0x00e6:
            android.view.WindowManager$LayoutParams r0 = r15.mAttrs     // Catch:{ all -> 0x019f }
            int r14 = r0.flags     // Catch:{ all -> 0x019f }
            r23 = r8
            int r8 = r3.flags     // Catch:{ all -> 0x01ca }
            r8 = r8 ^ r14
            r0.flags = r8     // Catch:{ all -> 0x01ca }
            r16 = r8
            android.view.WindowManager$LayoutParams r0 = r15.mAttrs     // Catch:{ all -> 0x01ca }
            int r8 = r0.privateFlags     // Catch:{ all -> 0x01ca }
            int r14 = r3.privateFlags     // Catch:{ all -> 0x01ca }
            r8 = r8 ^ r14
            r0.privateFlags = r8     // Catch:{ all -> 0x01ca }
            r17 = r8
            android.view.WindowManager$LayoutParams r0 = r15.mAttrs     // Catch:{ all -> 0x01ca }
            int r0 = r0.copyFrom(r3)     // Catch:{ all -> 0x01ca }
            r14 = r0
            r0 = r16 & 4
            if (r0 == 0) goto L_0x0110
            r15.isBlurStateChanged()     // Catch:{ all -> 0x01ca }
            r8 = 1
            r15.mBlurFlagChanged = r8     // Catch:{ all -> 0x01ca }
            goto L_0x0121
        L_0x0110:
            android.view.WindowManager$LayoutParams r0 = r15.mAttrs     // Catch:{ all -> 0x01ca }
            int r0 = r0.flags     // Catch:{ all -> 0x01ca }
            r8 = 4
            r0 = r0 & r8
            if (r0 == 0) goto L_0x0121
            boolean r0 = r15.isBlurStateChanged()     // Catch:{ all -> 0x01ca }
            if (r0 == 0) goto L_0x0121
            r8 = 1
            r15.mBlurFlagChanged = r8     // Catch:{ all -> 0x01ca }
        L_0x0121:
            r0 = r17 & 8
            if (r0 == 0) goto L_0x0129
            r8 = 1
            r15.mBlurCurrentFlagChanged = r8     // Catch:{ all -> 0x01ca }
            goto L_0x0135
        L_0x0129:
            android.view.WindowManager$LayoutParams r0 = r15.mAttrs     // Catch:{ all -> 0x01ca }
            int r0 = r0.privateFlags     // Catch:{ all -> 0x01ca }
            r8 = 8
            r0 = r0 & r8
            if (r0 == 0) goto L_0x0135
            r8 = 1
            r15.mBlurCurrentFlagChanged = r8     // Catch:{ all -> 0x01ca }
        L_0x0135:
            r0 = r14 & 16385(0x4001, float:2.296E-41)
            if (r0 == 0) goto L_0x013c
            r8 = 1
            r15.mLayoutNeeded = r8     // Catch:{ all -> 0x01ca }
        L_0x013c:
            com.android.server.wm.AppWindowToken r0 = r15.mAppToken     // Catch:{ all -> 0x01ca }
            r8 = 524288(0x80000, float:7.34684E-40)
            if (r0 == 0) goto L_0x0151
            r0 = r16 & r8
            if (r0 != 0) goto L_0x014c
            r0 = 4194304(0x400000, float:5.877472E-39)
            r0 = r16 & r0
            if (r0 == 0) goto L_0x0151
        L_0x014c:
            com.android.server.wm.AppWindowToken r0 = r15.mAppToken     // Catch:{ all -> 0x01ca }
            r0.checkKeyguardFlagsChanged()     // Catch:{ all -> 0x01ca }
        L_0x0151:
            r0 = 33554432(0x2000000, float:9.403955E-38)
            r0 = r0 & r14
            if (r0 == 0) goto L_0x016f
            com.android.server.wm.AccessibilityController r0 = r1.mAccessibilityController     // Catch:{ all -> 0x01ca }
            if (r0 == 0) goto L_0x016f
            int r0 = r15.getDisplayId()     // Catch:{ all -> 0x01ca }
            if (r0 == 0) goto L_0x016a
            com.android.server.wm.DisplayContent r0 = r15.getDisplayContent()     // Catch:{ all -> 0x01ca }
            com.android.server.wm.WindowState r0 = r0.getParentWindow()     // Catch:{ all -> 0x01ca }
            if (r0 == 0) goto L_0x016f
        L_0x016a:
            com.android.server.wm.AccessibilityController r0 = r1.mAccessibilityController     // Catch:{ all -> 0x01ca }
            r0.onSomeWindowResizedOrMovedLocked()     // Catch:{ all -> 0x01ca }
        L_0x016f:
            r0 = r16 & r8
            if (r0 == 0) goto L_0x017c
            com.android.server.wm.WindowStateAnimator r0 = r15.mWinAnimator     // Catch:{ all -> 0x01ca }
            boolean r0 = r0.getShown()     // Catch:{ all -> 0x01ca }
            r1.updateNonSystemOverlayWindowsVisibilityIfNeeded(r15, r0)     // Catch:{ all -> 0x01ca }
        L_0x017c:
            r0 = 131072(0x20000, float:1.83671E-40)
            r0 = r0 & r14
            if (r0 == 0) goto L_0x0190
            android.view.WindowManager$LayoutParams r0 = r15.mAttrs     // Catch:{ all -> 0x01ca }
            int r0 = r0.privateFlags     // Catch:{ all -> 0x01ca }
            r8 = 16777216(0x1000000, float:2.3509887E-38)
            r0 = r0 & r8
            if (r0 == 0) goto L_0x018c
            r0 = 1
            goto L_0x018d
        L_0x018c:
            r0 = 0
        L_0x018d:
            r5.setColorSpaceAgnosticLocked(r0)     // Catch:{ all -> 0x01ca }
        L_0x0190:
            r8 = r16
            r26 = r17
            goto L_0x01b8
        L_0x0195:
            r23 = r8
            java.lang.IllegalArgumentException r0 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x01ca }
            java.lang.String r8 = "Window type can not be changed after the window is added."
            r0.<init>(r8)     // Catch:{ all -> 0x01ca }
            throw r0     // Catch:{ all -> 0x01ca }
        L_0x019f:
            r0 = move-exception
            r23 = r8
            r13 = r48
            r15 = r55
            r4 = r56
            r27 = r9
            r33 = r10
            goto L_0x0534
        L_0x01ae:
            r20 = r14
            r13 = r23
            r23 = r8
            r8 = r16
            r26 = r17
        L_0x01b8:
            r0 = r45 & 2
            if (r0 == 0) goto L_0x01be
            r0 = 1
            goto L_0x01bf
        L_0x01be:
            r0 = 0
        L_0x01bf:
            r5.mSurfaceDestroyDeferred = r0     // Catch:{ all -> 0x0514 }
            r0 = r14 & 128(0x80, float:1.794E-43)
            if (r0 == 0) goto L_0x01d7
            float r0 = r3.alpha     // Catch:{ all -> 0x01ca }
            r5.mAlpha = r0     // Catch:{ all -> 0x01ca }
            goto L_0x01d7
        L_0x01ca:
            r0 = move-exception
            r13 = r48
            r15 = r55
            r4 = r56
            r27 = r9
            r33 = r10
            goto L_0x0534
        L_0x01d7:
            int r0 = r15.mRequestedWidth     // Catch:{ all -> 0x0514 }
            r27 = r9
            int r9 = r15.mRequestedHeight     // Catch:{ all -> 0x050a }
            boolean r0 = com.android.server.wm.WindowStateInjector.adjustFlagsForOnePixelWindow(r15, r0, r9, r3)     // Catch:{ all -> 0x050a }
            r9 = r0
            int r0 = r15.mRequestedWidth     // Catch:{ all -> 0x050a }
            int r3 = r15.mRequestedHeight     // Catch:{ all -> 0x050a }
            r15.setWindowScale(r0, r3)     // Catch:{ all -> 0x050a }
            android.view.WindowManager$LayoutParams r0 = r15.mAttrs     // Catch:{ all -> 0x050a }
            com.android.server.wm.WindowManagerServiceInjector.setAlertWindowTitle(r0)     // Catch:{ all -> 0x050a }
            android.view.WindowManager$LayoutParams r0 = r15.mAttrs     // Catch:{ all -> 0x050a }
            android.graphics.Rect r0 = r0.surfaceInsets     // Catch:{ all -> 0x050a }
            int r0 = r0.left     // Catch:{ all -> 0x050a }
            if (r0 != 0) goto L_0x021a
            android.view.WindowManager$LayoutParams r0 = r15.mAttrs     // Catch:{ all -> 0x020f }
            android.graphics.Rect r0 = r0.surfaceInsets     // Catch:{ all -> 0x020f }
            int r0 = r0.top     // Catch:{ all -> 0x020f }
            if (r0 != 0) goto L_0x021a
            android.view.WindowManager$LayoutParams r0 = r15.mAttrs     // Catch:{ all -> 0x020f }
            android.graphics.Rect r0 = r0.surfaceInsets     // Catch:{ all -> 0x020f }
            int r0 = r0.right     // Catch:{ all -> 0x020f }
            if (r0 != 0) goto L_0x021a
            android.view.WindowManager$LayoutParams r0 = r15.mAttrs     // Catch:{ all -> 0x020f }
            android.graphics.Rect r0 = r0.surfaceInsets     // Catch:{ all -> 0x020f }
            int r0 = r0.bottom     // Catch:{ all -> 0x020f }
            if (r0 == 0) goto L_0x021e
            goto L_0x021a
        L_0x020f:
            r0 = move-exception
            r13 = r48
            r15 = r55
            r4 = r56
            r33 = r10
            goto L_0x0534
        L_0x021a:
            r3 = 0
            r5.setOpaqueLocked(r3)     // Catch:{ all -> 0x050a }
        L_0x021e:
            int r0 = r15.mViewVisibility     // Catch:{ all -> 0x050a }
            r3 = r0
            r0 = 4
            if (r3 == r0) goto L_0x0228
            r0 = 8
            if (r3 != r0) goto L_0x022c
        L_0x0228:
            if (r4 != 0) goto L_0x022c
            r0 = 1
            goto L_0x022d
        L_0x022c:
            r0 = 0
        L_0x022d:
            r28 = r0
            r0 = 131080(0x20008, float:1.83682E-40)
            r0 = r0 & r8
            if (r0 != 0) goto L_0x023a
            if (r28 == 0) goto L_0x0238
            goto L_0x023a
        L_0x0238:
            r0 = 0
            goto L_0x023b
        L_0x023a:
            r0 = 1
        L_0x023b:
            r16 = r0
            int r0 = r15.mViewVisibility     // Catch:{ all -> 0x050a }
            if (r0 != r4) goto L_0x024e
            r0 = r8 & 8
            if (r0 != 0) goto L_0x024e
            boolean r0 = r15.mRelayoutCalled     // Catch:{ all -> 0x020f }
            if (r0 == 0) goto L_0x024e
            if (r9 == 0) goto L_0x024c
            goto L_0x024e
        L_0x024c:
            r0 = 0
            goto L_0x024f
        L_0x024e:
            r0 = 1
        L_0x024f:
            r29 = r3
            int r3 = r15.mViewVisibility     // Catch:{ all -> 0x050a }
            r17 = 1048576(0x100000, float:1.469368E-39)
            if (r3 == r4) goto L_0x0261
            android.view.WindowManager$LayoutParams r3 = r15.mAttrs     // Catch:{ all -> 0x020f }
            int r3 = r3.flags     // Catch:{ all -> 0x020f }
            r3 = r3 & r17
            if (r3 == 0) goto L_0x0261
            r3 = 1
            goto L_0x0262
        L_0x0261:
            r3 = 0
        L_0x0262:
            r17 = r8 & r17
            if (r17 == 0) goto L_0x0269
            r17 = 1
            goto L_0x026b
        L_0x0269:
            r17 = 0
        L_0x026b:
            r3 = r3 | r17
            r30 = r9
            r9 = r8 & 8192(0x2000, float:1.14794E-41)
            if (r9 == 0) goto L_0x0283
            com.android.server.wm.WindowSurfaceController r9 = r5.mSurfaceController     // Catch:{ all -> 0x020f }
            if (r9 == 0) goto L_0x0283
            com.android.server.wm.WindowSurfaceController r9 = r5.mSurfaceController     // Catch:{ all -> 0x020f }
            r31 = r8
            boolean r8 = r1.isSecureLocked(r15)     // Catch:{ all -> 0x020f }
            r9.setSecure(r8)     // Catch:{ all -> 0x020f }
            goto L_0x0285
        L_0x0283:
            r31 = r8
        L_0x0285:
            r8 = 1
            r15.mRelayoutCalled = r8     // Catch:{ all -> 0x050a }
            r15.mInRelayout = r8     // Catch:{ all -> 0x050a }
            r15.mViewVisibility = r4     // Catch:{ all -> 0x050a }
            r15.setDisplayLayoutNeeded()     // Catch:{ all -> 0x050a }
            r8 = r45 & 1
            if (r8 == 0) goto L_0x0295
            r8 = 1
            goto L_0x0296
        L_0x0295:
            r8 = 0
        L_0x0296:
            r15.mGivenInsetsPending = r8     // Catch:{ all -> 0x050a }
            if (r4 != 0) goto L_0x02af
            com.android.server.wm.AppWindowToken r8 = r15.mAppToken     // Catch:{ all -> 0x020f }
            if (r8 == 0) goto L_0x02ad
            android.view.WindowManager$LayoutParams r8 = r15.mAttrs     // Catch:{ all -> 0x020f }
            int r8 = r8.type     // Catch:{ all -> 0x020f }
            r9 = 3
            if (r8 == r9) goto L_0x02ad
            com.android.server.wm.AppWindowToken r8 = r15.mAppToken     // Catch:{ all -> 0x020f }
            boolean r8 = r8.isClientHidden()     // Catch:{ all -> 0x020f }
            if (r8 != 0) goto L_0x02af
        L_0x02ad:
            r8 = 1
            goto L_0x02b0
        L_0x02af:
            r8 = 0
        L_0x02b0:
            if (r8 != 0) goto L_0x02dd
            boolean r9 = r5.hasSurface()     // Catch:{ all -> 0x020f }
            if (r9 == 0) goto L_0x02dd
            boolean r9 = r15.mAnimatingExit     // Catch:{ all -> 0x020f }
            if (r9 != 0) goto L_0x02dd
            com.android.server.wm.AppWindowToken r9 = r1.mSaveSurfaceByKeyguardToken     // Catch:{ all -> 0x020f }
            if (r9 == 0) goto L_0x02cd
            com.android.server.wm.AppWindowToken r9 = r15.mAppToken     // Catch:{ all -> 0x020f }
            if (r9 == 0) goto L_0x02cd
            com.android.server.wm.AppWindowToken r9 = r1.mSaveSurfaceByKeyguardToken     // Catch:{ all -> 0x020f }
            r32 = r13
            com.android.server.wm.AppWindowToken r13 = r15.mAppToken     // Catch:{ all -> 0x020f }
            if (r9 == r13) goto L_0x02df
            goto L_0x02cf
        L_0x02cd:
            r32 = r13
        L_0x02cf:
            r7 = r7 | 4
            boolean r9 = r15.mWillReplaceWindow     // Catch:{ all -> 0x020f }
            if (r9 != 0) goto L_0x02db
            boolean r9 = r1.tryStartExitingAnimation(r15, r5, r0)     // Catch:{ all -> 0x020f }
            r0 = r9
            goto L_0x02e0
        L_0x02db:
            r9 = r0
            goto L_0x02e0
        L_0x02dd:
            r32 = r13
        L_0x02df:
            r9 = r0
        L_0x02e0:
            r13 = r3
            r3 = 32
            if (r8 == 0) goto L_0x037c
            java.lang.String r0 = "relayoutWindow: viewVisibility_1"
            android.os.Trace.traceBegin(r3, r0)     // Catch:{ all -> 0x020f }
            int r0 = r15.relayoutVisibleWindow(r7, r14)     // Catch:{ all -> 0x020f }
            r7 = r0
            int r0 = r1.createSurfaceControl(r6, r7, r15, r5)     // Catch:{ Exception -> 0x031f }
            r7 = r0
            r0 = r7 & 2
            if (r0 == 0) goto L_0x02fa
            r9 = 1
        L_0x02fa:
            android.view.WindowManager$LayoutParams r0 = r15.mAttrs     // Catch:{ all -> 0x020f }
            int r0 = r0.type     // Catch:{ all -> 0x020f }
            r3 = 2011(0x7db, float:2.818E-42)
            if (r0 != r3) goto L_0x030e
            r3 = r22
            com.android.server.wm.WindowState r0 = r3.mInputMethodWindow     // Catch:{ all -> 0x020f }
            if (r0 != 0) goto L_0x0310
            r3.setInputMethodWindowLocked(r15)     // Catch:{ all -> 0x020f }
            r16 = 1
            goto L_0x0310
        L_0x030e:
            r3 = r22
        L_0x0310:
            r15.adjustStartingWindowFlags()     // Catch:{ all -> 0x020f }
            r17 = 32
            android.os.Trace.traceEnd(r17)     // Catch:{ all -> 0x020f }
            r0 = r9
            r33 = r10
            r9 = 32
            goto L_0x03ca
        L_0x031f:
            r0 = move-exception
            r3 = r22
            r4 = r0
            r0 = r4
            com.android.server.wm.InputMonitor r4 = r3.getInputMonitor()     // Catch:{ all -> 0x036f }
            r17 = r7
            r7 = 1
            r4.updateInputWindowsLw(r7)     // Catch:{ all -> 0x0362 }
            java.lang.String r4 = "WindowManager"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x0362 }
            r7.<init>()     // Catch:{ all -> 0x0362 }
            r20 = r9
            java.lang.String r9 = "Exception thrown when creating surface for client "
            r7.append(r9)     // Catch:{ all -> 0x0362 }
            r7.append(r2)     // Catch:{ all -> 0x0362 }
            java.lang.String r9 = " ("
            r7.append(r9)     // Catch:{ all -> 0x0362 }
            android.view.WindowManager$LayoutParams r9 = r15.mAttrs     // Catch:{ all -> 0x0362 }
            java.lang.CharSequence r9 = r9.getTitle()     // Catch:{ all -> 0x0362 }
            r7.append(r9)     // Catch:{ all -> 0x0362 }
            java.lang.String r9 = ")"
            r7.append(r9)     // Catch:{ all -> 0x0362 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x0362 }
            android.util.Slog.w(r4, r7, r0)     // Catch:{ all -> 0x0362 }
            android.os.Binder.restoreCallingIdentity(r10)     // Catch:{ all -> 0x0362 }
            monitor-exit(r12)     // Catch:{ all -> 0x0362 }
            resetPriorityAfterLockedSection()
            r4 = 0
            return r4
        L_0x0362:
            r0 = move-exception
            r13 = r48
            r15 = r55
            r4 = r56
            r33 = r10
            r7 = r17
            goto L_0x0534
        L_0x036f:
            r0 = move-exception
            r17 = r7
            r13 = r48
            r15 = r55
            r4 = r56
            r33 = r10
            goto L_0x0534
        L_0x037c:
            r20 = r9
            r3 = r22
            java.lang.String r0 = "relayoutWindow: viewVisibility_2"
            r33 = r10
            r9 = 32
            android.os.Trace.traceBegin(r9, r0)     // Catch:{ all -> 0x0502 }
            r4 = 0
            r5.mEnterAnimationPending = r4     // Catch:{ all -> 0x0502 }
            r5.mEnteringAnimation = r4     // Catch:{ all -> 0x0502 }
            if (r44 != 0) goto L_0x03a4
            boolean r0 = r5.hasSurface()     // Catch:{ all -> 0x0502 }
            if (r0 == 0) goto L_0x03a4
            java.lang.String r0 = "relayoutWindow: getSurface"
            android.os.Trace.traceBegin(r9, r0)     // Catch:{ all -> 0x0502 }
            com.android.server.wm.WindowSurfaceController r0 = r5.mSurfaceController     // Catch:{ all -> 0x0502 }
            r0.getSurfaceControl(r6)     // Catch:{ all -> 0x0502 }
            android.os.Trace.traceEnd(r9)     // Catch:{ all -> 0x0502 }
            goto L_0x03c5
        L_0x03a4:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ all -> 0x04ef }
            r0.<init>()     // Catch:{ all -> 0x04ef }
            java.lang.String r4 = "wmReleaseOutSurface_"
            r0.append(r4)     // Catch:{ all -> 0x04ef }
            android.view.WindowManager$LayoutParams r4 = r15.mAttrs     // Catch:{ all -> 0x04ef }
            java.lang.CharSequence r4 = r4.getTitle()     // Catch:{ all -> 0x04ef }
            r0.append(r4)     // Catch:{ all -> 0x04ef }
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x04ef }
            android.os.Trace.traceBegin(r9, r0)     // Catch:{ all -> 0x04ef }
            r57.release()     // Catch:{ all -> 0x04ef }
            android.os.Trace.traceEnd(r9)     // Catch:{ all -> 0x0502 }
        L_0x03c5:
            android.os.Trace.traceEnd(r9)     // Catch:{ all -> 0x0502 }
            r0 = r20
        L_0x03ca:
            com.android.server.wm.WindowSurfacePlacer r4 = r1.mWindowPlacerLocked     // Catch:{ all -> 0x0502 }
            r11 = 1
            r4.performSurfacePlacement(r11)     // Catch:{ all -> 0x0502 }
            if (r0 == 0) goto L_0x03de
            r4 = 0
            boolean r17 = r1.updateFocusedWindowLocked(r4, r11)     // Catch:{ all -> 0x0502 }
            if (r17 == 0) goto L_0x03de
            r16 = 0
            r4 = r16
            goto L_0x03e0
        L_0x03de:
            r4 = r16
        L_0x03e0:
            r11 = r7 & 2
            if (r11 == 0) goto L_0x03e6
            r11 = 1
            goto L_0x03e7
        L_0x03e6:
            r11 = 0
        L_0x03e7:
            if (r4 == 0) goto L_0x03f4
            r9 = 1
            r3.computeImeTarget(r9)     // Catch:{ all -> 0x0502 }
            if (r11 == 0) goto L_0x03f5
            r10 = 0
            r3.assignWindowLayers(r10)     // Catch:{ all -> 0x0502 }
            goto L_0x03f5
        L_0x03f4:
            r9 = 1
        L_0x03f5:
            if (r13 == 0) goto L_0x03ff
            int r10 = r3.pendingLayoutChanges     // Catch:{ all -> 0x0502 }
            r16 = 4
            r10 = r10 | 4
            r3.pendingLayoutChanges = r10     // Catch:{ all -> 0x0502 }
        L_0x03ff:
            com.android.server.wm.AppWindowToken r10 = r15.mAppToken     // Catch:{ all -> 0x0502 }
            if (r10 == 0) goto L_0x040a
            com.android.server.wm.UnknownAppVisibilityController r10 = r3.mUnknownAppVisibilityController     // Catch:{ all -> 0x0502 }
            com.android.server.wm.AppWindowToken r9 = r15.mAppToken     // Catch:{ all -> 0x0502 }
            r10.notifyRelayouted(r9)     // Catch:{ all -> 0x0502 }
        L_0x040a:
            java.lang.String r9 = "relayoutWindow: updateOrientationFromAppTokens"
            r22 = r13
            r10 = r14
            r13 = 32
            android.os.Trace.traceBegin(r13, r9)     // Catch:{ all -> 0x0502 }
            com.android.server.wm.AppWindowToken r9 = r15.mAppToken     // Catch:{ all -> 0x0502 }
            if (r9 == 0) goto L_0x0421
            com.android.server.wm.AppWindowToken r9 = r15.mAppToken     // Catch:{ all -> 0x0502 }
            com.android.server.wm.WindowState r9 = r9.startingWindow     // Catch:{ all -> 0x0502 }
            if (r9 == r15) goto L_0x041f
            goto L_0x0421
        L_0x041f:
            r9 = 0
            goto L_0x0425
        L_0x0421:
            boolean r9 = r3.updateOrientationFromAppTokens()     // Catch:{ all -> 0x0502 }
        L_0x0425:
            r13 = 32
            android.os.Trace.traceEnd(r13)     // Catch:{ all -> 0x0502 }
            if (r11 == 0) goto L_0x0443
            boolean r13 = r15.mIsWallpaper     // Catch:{ all -> 0x0502 }
            if (r13 == 0) goto L_0x0443
            android.view.DisplayInfo r13 = r3.getDisplayInfo()     // Catch:{ all -> 0x0502 }
            com.android.server.wm.WallpaperController r14 = r3.mWallpaperController     // Catch:{ all -> 0x0502 }
            r35 = r0
            int r0 = r13.logicalWidth     // Catch:{ all -> 0x0502 }
            int r2 = r13.logicalHeight     // Catch:{ all -> 0x0502 }
            r36 = r4
            r4 = 0
            r14.updateWallpaperOffset(r15, r0, r2, r4)     // Catch:{ all -> 0x0502 }
            goto L_0x0447
        L_0x0443:
            r35 = r0
            r36 = r4
        L_0x0447:
            com.android.server.wm.AppWindowToken r0 = r15.mAppToken     // Catch:{ all -> 0x0502 }
            if (r0 == 0) goto L_0x0450
            com.android.server.wm.AppWindowToken r0 = r15.mAppToken     // Catch:{ all -> 0x0502 }
            r0.updateReportedVisibilityLocked()     // Catch:{ all -> 0x0502 }
        L_0x0450:
            boolean r0 = r5.mReportSurfaceResized     // Catch:{ all -> 0x0502 }
            if (r0 == 0) goto L_0x045a
            r2 = 0
            r5.mReportSurfaceResized = r2     // Catch:{ all -> 0x0502 }
            r0 = r7 | 32
            r7 = r0
        L_0x045a:
            r2 = r32
            boolean r0 = r2.areSystemBarsForcedShownLw(r15)     // Catch:{ all -> 0x0502 }
            if (r0 == 0) goto L_0x0465
            r0 = r7 | 64
            r7 = r0
        L_0x0465:
            boolean r0 = r15.isGoneForLayoutLw()     // Catch:{ all -> 0x0502 }
            if (r0 != 0) goto L_0x046e
            r4 = 0
            r15.mResizedWhileGone = r4     // Catch:{ all -> 0x0502 }
        L_0x046e:
            if (r8 == 0) goto L_0x0476
            r4 = r56
            r15.getMergedConfiguration(r4)     // Catch:{ all -> 0x04e9 }
            goto L_0x047b
        L_0x0476:
            r4 = r56
            r15.getLastReportedMergedConfiguration(r4)     // Catch:{ all -> 0x04e9 }
        L_0x047b:
            r15.setLastReportedMergedConfiguration(r4)     // Catch:{ all -> 0x04e9 }
            r15.updateLastInsetValues()     // Catch:{ all -> 0x04e9 }
            r13 = r48
            r15.getCompatFrame(r13)     // Catch:{ all -> 0x04e7 }
            r14 = r15
            r16 = r49
            r17 = r50
            r18 = r51
            r19 = r52
            r20 = r53
            r15.getInsetsForRelayout(r16, r17, r18, r19, r20)     // Catch:{ all -> 0x04e7 }
            com.android.server.wm.utils.WmDisplayCutout r0 = r14.getWmDisplayCutout()     // Catch:{ all -> 0x04e7 }
            android.view.DisplayCutout r0 = r0.getDisplayCutout()     // Catch:{ all -> 0x04e7 }
            r15 = r55
            r15.set(r0)     // Catch:{ all -> 0x0539 }
            android.graphics.Rect r0 = r14.getFrameLw()     // Catch:{ all -> 0x0539 }
            android.graphics.Rect r0 = r14.getBackdropFrame(r0)     // Catch:{ all -> 0x0539 }
            r32 = r2
            r2 = r54
            r2.set(r0)     // Catch:{ all -> 0x0539 }
            com.android.server.wm.InsetsStateController r0 = r3.getInsetsStateController()     // Catch:{ all -> 0x0539 }
            android.view.InsetsState r0 = r0.getInsetsForDispatch(r14)     // Catch:{ all -> 0x0539 }
            r2 = r58
            r2.set(r0)     // Catch:{ all -> 0x0539 }
            boolean r0 = r1.mInTouchMode     // Catch:{ all -> 0x0539 }
            if (r0 == 0) goto L_0x04c4
            r25 = 1
            goto L_0x04c6
        L_0x04c4:
            r25 = 0
        L_0x04c6:
            r7 = r7 | r25
            r2 = 0
            r14.mInRelayout = r2     // Catch:{ all -> 0x0539 }
            monitor-exit(r12)     // Catch:{ all -> 0x0539 }
            resetPriorityAfterLockedSection()
            if (r9 == 0) goto L_0x04e1
            java.lang.String r0 = "relayoutWindow: sendNewConfiguration"
            r2 = 32
            android.os.Trace.traceBegin(r2, r0)
            r11 = r21
            r1.sendNewConfiguration(r11)
            android.os.Trace.traceEnd(r2)
            goto L_0x04e3
        L_0x04e1:
            r11 = r21
        L_0x04e3:
            android.os.Binder.restoreCallingIdentity(r33)
            return r7
        L_0x04e7:
            r0 = move-exception
            goto L_0x04ec
        L_0x04e9:
            r0 = move-exception
            r13 = r48
        L_0x04ec:
            r15 = r55
            goto L_0x0534
        L_0x04ef:
            r0 = move-exception
            r4 = r56
            r22 = r13
            r10 = r14
            r14 = r15
            r11 = r21
            r13 = r48
            r15 = r55
            r17 = 32
            android.os.Trace.traceEnd(r17)     // Catch:{ all -> 0x0539 }
            throw r0     // Catch:{ all -> 0x0539 }
        L_0x0502:
            r0 = move-exception
            r13 = r48
            r15 = r55
            r4 = r56
            goto L_0x0534
        L_0x050a:
            r0 = move-exception
            r13 = r48
            r15 = r55
            r4 = r56
            r33 = r10
            goto L_0x0534
        L_0x0514:
            r0 = move-exception
            r13 = r48
            r15 = r55
            r4 = r56
            r27 = r9
            r33 = r10
            goto L_0x0534
        L_0x0520:
            r0 = move-exception
            r13 = r48
            r15 = r55
            r4 = r56
            goto L_0x052e
        L_0x0528:
            r0 = move-exception
            r13 = r48
            r15 = r55
            r4 = r5
        L_0x052e:
            r23 = r8
            r27 = r9
            r33 = r10
        L_0x0534:
            monitor-exit(r12)     // Catch:{ all -> 0x0539 }
            resetPriorityAfterLockedSection()
            throw r0
        L_0x0539:
            r0 = move-exception
            goto L_0x0534
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.relayoutWindow(com.android.server.wm.Session, android.view.IWindow, int, android.view.WindowManager$LayoutParams, int, int, int, int, long, android.graphics.Rect, android.graphics.Rect, android.graphics.Rect, android.graphics.Rect, android.graphics.Rect, android.graphics.Rect, android.graphics.Rect, android.view.DisplayCutout$ParcelableWrapper, android.util.MergedConfiguration, android.view.SurfaceControl, android.view.InsetsState):int");
    }

    private boolean tryStartExitingAnimation(WindowState win, WindowStateAnimator winAnimator, boolean focusMayChange) {
        int transit = 2;
        if (win.mAttrs.type == 3) {
            transit = 5;
        }
        if (win.isWinVisibleLw() && winAnimator.applyAnimationLocked(transit, false)) {
            focusMayChange = true;
            win.mAnimatingExit = true;
        } else if (win.isAnimating()) {
            win.mAnimatingExit = true;
        } else if (win.getDisplayContent().mWallpaperController.isWallpaperTarget(win)) {
            win.mAnimatingExit = true;
        } else {
            DisplayContent displayContent = win.getDisplayContent();
            if (displayContent.mInputMethodWindow == win) {
                displayContent.setInputMethodWindowLocked((WindowState) null);
            }
            boolean stopped = win.mAppToken != null ? win.mAppToken.mAppStopped : true;
            boolean hasSurfaceView = win.mHasSurfaceView;
            if (win.mAppToken == null || !this.mPolicy.isKeyguardShowingAndNotOccluded() || win.mAppToken.isActivityTypeHome() || !win.mAppToken.isOnTop() || !win.hasDrawnLw() || hasSurfaceView) {
                win.mDestroying = true;
                win.destroySurface(false, stopped);
            } else {
                win.mDestroying = false;
                this.mDestroySurface.add(win);
                this.mSaveSurfaceByKeyguardToken = win.mAppToken;
            }
        }
        AccessibilityController accessibilityController = this.mAccessibilityController;
        if (accessibilityController != null) {
            accessibilityController.onWindowTransitionLocked(win, transit);
        }
        SurfaceControl.openTransaction();
        winAnimator.detachChildren();
        SurfaceControl.closeTransaction();
        return focusMayChange;
    }

    private int createSurfaceControl(SurfaceControl outSurfaceControl, int result, WindowState win, WindowStateAnimator winAnimator) {
        DisplayContent displayContent = getDefaultDisplayContentLocked();
        if (win.mAppToken != null && win.getDisplayContent() == displayContent && displayContent.mOpeningApps.contains(win.mAppToken)) {
            this.mMiuiGestureController.cancelGoHomeAnimationIfNeeded(win.mAppToken);
        }
        if (!win.mHasSurface) {
            result |= 4;
        }
        try {
            Trace.traceBegin(32, "createSurfaceControl");
            WindowSurfaceController surfaceController = winAnimator.createSurfaceLocked(win.mAttrs.type, win.mOwnerUid);
            if (surfaceController != null) {
                surfaceController.getSurfaceControl(outSurfaceControl);
            } else {
                Slog.w("WindowManager", "Failed to create surface control for " + win);
                outSurfaceControl.release();
            }
            return result;
        } finally {
            Trace.traceEnd(32);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    public boolean outOfMemoryWindow(Session session, IWindow client) {
        long origId = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                WindowState win = windowForClientLocked(session, client, false);
                if (win == null) {
                    resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(origId);
                    return false;
                }
                boolean reclaimSomeSurfaceMemory = this.mRoot.reclaimSomeSurfaceMemory(win.mWinAnimator, "from-client", false);
                resetPriorityAfterLockedSection();
                Binder.restoreCallingIdentity(origId);
                return reclaimSomeSurfaceMemory;
            }
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(origId);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    /* access modifiers changed from: package-private */
    public void finishDrawingWindow(Session session, IWindow client) {
        long origId = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                WindowState win = windowForClientLocked(session, client, false);
                if (win != null && win.mWinAnimator.finishDrawingLocked()) {
                    if ((win.mAttrs.flags & DumpState.DUMP_DEXOPT) != 0) {
                        win.getDisplayContent().pendingLayoutChanges |= 4;
                    }
                    win.setDisplayLayoutNeeded();
                    this.mWindowPlacerLocked.requestTraversal();
                }
            }
            resetPriorityAfterLockedSection();
            Binder.restoreCallingIdentity(origId);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(origId);
            throw th;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean checkCallingPermission(String permission, String func) {
        if (Binder.getCallingPid() == Process.myPid() || this.mContext.checkCallingPermission(permission) == 0) {
            return true;
        }
        Slog.w("WindowManager", "Permission Denial: " + func + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + permission);
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0087, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x008a, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void addWindowToken(android.os.IBinder r11, int r12, int r13) {
        /*
            r10 = this;
            java.lang.String r0 = "android.permission.MANAGE_APP_TOKENS"
            java.lang.String r1 = "addWindowToken()"
            boolean r0 = r10.checkCallingPermission(r0, r1)
            if (r0 == 0) goto L_0x0091
            com.android.server.wm.WindowManagerGlobalLock r0 = r10.mGlobalLock
            monitor-enter(r0)
            boostPriorityForLockedSection()     // Catch:{ all -> 0x008b }
            r1 = 0
            com.android.server.wm.DisplayContent r1 = r10.getDisplayContentOrCreate(r13, r1)     // Catch:{ all -> 0x008b }
            if (r1 != 0) goto L_0x003a
            java.lang.String r2 = "WindowManager"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x008b }
            r3.<init>()     // Catch:{ all -> 0x008b }
            java.lang.String r4 = "addWindowToken: Attempted to add token: "
            r3.append(r4)     // Catch:{ all -> 0x008b }
            r3.append(r11)     // Catch:{ all -> 0x008b }
            java.lang.String r4 = " for non-exiting displayId="
            r3.append(r4)     // Catch:{ all -> 0x008b }
            r3.append(r13)     // Catch:{ all -> 0x008b }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x008b }
            android.util.Slog.w(r2, r3)     // Catch:{ all -> 0x008b }
            monitor-exit(r0)     // Catch:{ all -> 0x008b }
            resetPriorityAfterLockedSection()
            return
        L_0x003a:
            com.android.server.wm.WindowToken r2 = r1.getWindowToken(r11)     // Catch:{ all -> 0x008b }
            r9 = r2
            if (r9 == 0) goto L_0x006c
            java.lang.String r2 = "WindowManager"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x008b }
            r3.<init>()     // Catch:{ all -> 0x008b }
            java.lang.String r4 = "addWindowToken: Attempted to add binder token: "
            r3.append(r4)     // Catch:{ all -> 0x008b }
            r3.append(r11)     // Catch:{ all -> 0x008b }
            java.lang.String r4 = " for already created window token: "
            r3.append(r4)     // Catch:{ all -> 0x008b }
            r3.append(r9)     // Catch:{ all -> 0x008b }
            java.lang.String r4 = " displayId="
            r3.append(r4)     // Catch:{ all -> 0x008b }
            r3.append(r13)     // Catch:{ all -> 0x008b }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x008b }
            android.util.Slog.w(r2, r3)     // Catch:{ all -> 0x008b }
            monitor-exit(r0)     // Catch:{ all -> 0x008b }
            resetPriorityAfterLockedSection()
            return
        L_0x006c:
            r2 = 2013(0x7dd, float:2.821E-42)
            if (r12 != r2) goto L_0x007b
            com.android.server.wm.WallpaperWindowToken r2 = new com.android.server.wm.WallpaperWindowToken     // Catch:{ all -> 0x008b }
            r5 = 1
            r7 = 1
            r3 = r10
            r4 = r11
            r6 = r1
            r2.<init>(r3, r4, r5, r6, r7)     // Catch:{ all -> 0x008b }
            goto L_0x0086
        L_0x007b:
            com.android.server.wm.WindowToken r2 = new com.android.server.wm.WindowToken     // Catch:{ all -> 0x008b }
            r6 = 1
            r8 = 1
            r3 = r10
            r4 = r11
            r5 = r12
            r7 = r1
            r2.<init>(r3, r4, r5, r6, r7, r8)     // Catch:{ all -> 0x008b }
        L_0x0086:
            monitor-exit(r0)     // Catch:{ all -> 0x008b }
            resetPriorityAfterLockedSection()
            return
        L_0x008b:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x008b }
            resetPriorityAfterLockedSection()
            throw r1
        L_0x0091:
            java.lang.SecurityException r0 = new java.lang.SecurityException
            java.lang.String r1 = "Requires MANAGE_APP_TOKENS permission"
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.addWindowToken(android.os.IBinder, int, int):void");
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    public void removeWindowToken(IBinder binder, int displayId) {
        if (checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "removeWindowToken()")) {
            long origId = Binder.clearCallingIdentity();
            try {
                synchronized (this.mGlobalLock) {
                    boostPriorityForLockedSection();
                    DisplayContent dc = this.mRoot.getDisplayContent(displayId);
                    if (dc == null) {
                        Slog.w("WindowManager", "removeWindowToken: Attempted to remove token: " + binder + " for non-exiting displayId=" + displayId);
                        resetPriorityAfterLockedSection();
                        Binder.restoreCallingIdentity(origId);
                    } else if (dc.removeWindowToken(binder) == null) {
                        Slog.w("WindowManager", "removeWindowToken: Attempted to remove non-existing token: " + binder);
                        resetPriorityAfterLockedSection();
                        Binder.restoreCallingIdentity(origId);
                    } else {
                        dc.getInputMonitor().updateInputWindowsLw(true);
                        resetPriorityAfterLockedSection();
                        Binder.restoreCallingIdentity(origId);
                    }
                }
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(origId);
                throw th;
            }
        } else {
            throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
        }
    }

    /* access modifiers changed from: package-private */
    public void setNewDisplayOverrideConfiguration(Configuration overrideConfig, DisplayContent dc) {
        if (dc.mWaitingForConfig) {
            dc.mWaitingForConfig = false;
            this.mLastFinishedFreezeSource = "new-config";
        }
        this.mRoot.setDisplayOverrideConfigurationIfNeeded(overrideConfig, dc);
    }

    public void prepareAppTransition(int transit, boolean alwaysKeepCurrent) {
        if (checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "prepareAppTransition()")) {
            getDefaultDisplayContentLocked().prepareAppTransition(transit, alwaysKeepCurrent, 0, false);
            return;
        }
        throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
    }

    public void overridePendingAppTransitionLaunchFromHome(int startX, int startY, int startWidth, int startHeight, int displayId) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                if (displayContent == null) {
                    Slog.w("WindowManager", "Attempted to call overridePendingAppTransitionMultiThumbFuture for the display " + displayId + " that does not exist.");
                    resetPriorityAfterLockedSection();
                    return;
                }
                displayContent.mAppTransition.overridePendingAppTransitionLaunchFromHome(startX, startY, startWidth, startHeight);
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public void overridePendingActivityTransitionFromRoundedView(int startX, int startY, int startWidth, int startHeight, int radius, int foreGroundColor, GraphicBuffer buffer, IRemoteCallback startedCallback, IRemoteCallback finishedCallback, int displayId) {
        int i = displayId;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    Slog.w("WindowManager", "Attempted to call overridePendingAppTransitionMultiThumbFuture for the display " + i + " that does not exist.");
                    resetPriorityAfterLockedSection();
                    return;
                }
                displayContent.mAppTransition.overridePendingActivityTransitionFromRoundedView(startX, startY, startWidth, startHeight, radius, foreGroundColor, buffer, startedCallback, finishedCallback);
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public void overrideMiuiAnimSupportWinInset(Rect inset) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                AppTransitionInjector.setMiuiAnimSupportInset(inset);
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void cancelMiuiThumbnailAnimation(int displayId) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                if (displayContent == null) {
                    Slog.w("WindowManager", "Attempted to call cancelMiuiThumbnailAnimation for the display " + displayId + " that does not exist.");
                    resetPriorityAfterLockedSection();
                    return;
                }
                displayContent.mAppTransitionController.cancelMiuiThumbnailAnimationLocked();
                this.mMiuiGestureController.cancelGoHomeAnimationIfNeeded();
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public void overridePendingAppTransitionMultiThumbFuture(IAppTransitionAnimationSpecsFuture specsFuture, IRemoteCallback callback, boolean scaleUp, int displayId) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                if (displayContent == null) {
                    Slog.w("WindowManager", "Attempted to call overridePendingAppTransitionMultiThumbFuture for the display " + displayId + " that does not exist.");
                    resetPriorityAfterLockedSection();
                    return;
                }
                displayContent.mAppTransition.overridePendingAppTransitionMultiThumbFuture(specsFuture, callback, scaleUp);
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public void overridePendingAppTransitionRemote(RemoteAnimationAdapter remoteAnimationAdapter, int displayId) {
        if (checkCallingPermission("android.permission.CONTROL_REMOTE_APP_TRANSITION_ANIMATIONS", "overridePendingAppTransitionRemote()")) {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                    if (displayContent == null) {
                        Slog.w("WindowManager", "Attempted to call overridePendingAppTransitionRemote for the display " + displayId + " that does not exist.");
                        resetPriorityAfterLockedSection();
                        return;
                    }
                    displayContent.mAppTransition.overridePendingAppTransitionRemote(remoteAnimationAdapter);
                    resetPriorityAfterLockedSection();
                } catch (Throwable th) {
                    while (true) {
                        resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        } else {
            throw new SecurityException("Requires CONTROL_REMOTE_APP_TRANSITION_ANIMATIONS permission");
        }
    }

    public void endProlongedAnimations() {
    }

    public void executeAppTransition() {
        if (checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "executeAppTransition()")) {
            getDefaultDisplayContentLocked().executeAppTransition();
            return;
        }
        throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
    }

    public void initializeRecentsAnimation(int targetActivityType, IRecentsAnimationRunner recentsAnimationRunner, RecentsAnimationController.RecentsAnimationCallbacks callbacks, int displayId, SparseBooleanArray recentTaskIds) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mRecentsAnimationController = new RecentsAnimationController(this, recentsAnimationRunner, callbacks, displayId);
                this.mRoot.getDisplayContent(displayId).mAppTransition.updateBooster();
                this.mRecentsAnimationController.initialize(targetActivityType, recentTaskIds);
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setRecentsAnimationController(RecentsAnimationController controller) {
        this.mRecentsAnimationController = controller;
    }

    public RecentsAnimationController getRecentsAnimationController() {
        return this.mRecentsAnimationController;
    }

    public boolean canStartRecentsAnimation() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                if (getDefaultDisplayContentLocked().mAppTransition.isTransitionSet()) {
                    resetPriorityAfterLockedSection();
                    return false;
                }
                resetPriorityAfterLockedSection();
                return true;
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public void cancelRecentsAnimationSynchronously(@RecentsAnimationController.ReorderMode int reorderMode, String reason) {
        RecentsAnimationController recentsAnimationController = this.mRecentsAnimationController;
        if (recentsAnimationController != null) {
            recentsAnimationController.cancelAnimationSynchronously(reorderMode, reason);
        }
    }

    public void cleanupRecentsAnimation(@RecentsAnimationController.ReorderMode int reorderMode) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                if (this.mRecentsAnimationController != null) {
                    RecentsAnimationController controller = this.mRecentsAnimationController;
                    this.mRecentsAnimationController = null;
                    controller.cleanupAnimation(reorderMode);
                    getDefaultDisplayContentLocked().mAppTransition.updateBooster();
                }
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void setAppFullscreen(IBinder token, boolean toOpaque) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                AppWindowToken atoken = this.mRoot.getAppWindowToken(token);
                if (atoken != null) {
                    atoken.setFillsParent(toOpaque);
                    setWindowOpaqueLocked(token, toOpaque);
                    this.mWindowPlacerLocked.requestTraversal();
                }
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void setWindowOpaque(IBinder token, boolean isOpaque) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                setWindowOpaqueLocked(token, isOpaque);
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    private void setWindowOpaqueLocked(IBinder token, boolean isOpaque) {
        WindowState win;
        AppWindowToken wtoken = this.mRoot.getAppWindowToken(token);
        if (wtoken != null && (win = wtoken.findMainWindow()) != null) {
            win.mWinAnimator.setOpaqueLocked(isOpaque & (!PixelFormat.formatHasAlpha(win.getAttrs().format)));
        }
    }

    public void setDockedStackCreateState(int mode, Rect bounds) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                setDockedStackCreateStateLocked(mode, bounds);
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    /* access modifiers changed from: package-private */
    public void setDockedStackCreateStateLocked(int mode, Rect bounds) {
        this.mDockedStackCreateMode = mode;
        this.mDockedStackCreateBounds = bounds;
    }

    public void checkSplitScreenMinimizedChanged(boolean animate) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                getDefaultDisplayContentLocked().getDockedDividerController().checkMinimizeChanged(animate);
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public boolean isValidPictureInPictureAspectRatio(int displayId, float aspectRatio) {
        return this.mRoot.getDisplayContent(displayId).getPinnedStackController().isValidPictureInPictureAspectRatio(aspectRatio);
    }

    public void getStackBounds(int windowingMode, int activityType, Rect bounds) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                TaskStack stack = this.mRoot.getStack(windowingMode, activityType);
                if (stack != null) {
                    stack.getBounds(bounds);
                    resetPriorityAfterLockedSection();
                    return;
                }
                bounds.setEmpty();
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public void notifyShowingDreamChanged() {
        notifyKeyguardFlagsChanged((Runnable) null, 0);
    }

    public WindowManagerPolicy.WindowState getInputMethodWindowLw() {
        return this.mRoot.getCurrentInputMethodWindow();
    }

    public void notifyKeyguardTrustedChanged() {
        this.mAtmInternal.notifyKeyguardTrustedChanged();
    }

    public void screenTurningOff(WindowManagerPolicy.ScreenOffListener listener) {
        this.mTaskSnapshotController.screenTurningOff(listener);
    }

    public void triggerAnimationFailsafe() {
        this.mH.sendEmptyMessage(60);
    }

    public void onKeyguardShowingAndNotOccludedChanged() {
        this.mH.sendEmptyMessage(61);
    }

    public void onPowerKeyDown(boolean isScreenOn) {
        this.mRoot.forAllDisplayPolicies(PooledLambda.obtainConsumer($$Lambda$99XNq73vh8e4HVH9BuxFhbLxKVY.INSTANCE, PooledLambda.__(), Boolean.valueOf(isScreenOn)));
    }

    public void onUserSwitched() {
        this.mSettingsObserver.updateSystemUiSettings();
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mRoot.forAllDisplayPolicies($$Lambda$_jL5KNK44AQYPj1d8Hd3FYO0WM.INSTANCE);
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void moveDisplayToTop(int displayId) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                if (!(displayContent == null || this.mRoot.getTopChild() == displayContent)) {
                    this.mRoot.positionChildAt(Integer.MAX_VALUE, displayContent, true);
                }
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void onFpClientStateChanged(boolean start, boolean iskeyguard) {
        this.isFpClientOn = start;
        if (!iskeyguard) {
            updateContrastAlpha(!this.isFpClientOn);
        }
    }

    /* access modifiers changed from: package-private */
    public void deferSurfaceLayout() {
        this.mWindowPlacerLocked.deferLayout();
    }

    /* access modifiers changed from: package-private */
    public void continueSurfaceLayout() {
        this.mWindowPlacerLocked.continueLayout();
    }

    /* access modifiers changed from: package-private */
    public void notifyKeyguardFlagsChanged(Runnable callback, int displayId) {
        this.mAtmInternal.notifyKeyguardFlagsChanged(callback, displayId);
    }

    public boolean isKeyguardTrusted() {
        boolean isKeyguardTrustedLw;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                isKeyguardTrustedLw = this.mPolicy.isKeyguardTrustedLw();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
        return isKeyguardTrustedLw;
    }

    public void setKeyguardGoingAway(boolean keyguardGoingAway) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                if (!keyguardGoingAway) {
                    updateContrastAlpha(!this.isFpClientOn);
                }
                this.mKeyguardGoingAway = keyguardGoingAway;
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void setKeyguardOrAodShowingOnDefaultDisplay(boolean showing) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mKeyguardOrAodShowingOnDefaultDisplay = showing;
                getDefaultDisplayContentLocked().getDockedDividerController().checkMinimizeChanged(showing);
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public void startFreezingScreen(int exitAnim, int enterAnim) {
        long origId;
        if (checkCallingPermission("android.permission.FREEZE_SCREEN", "startFreezingScreen()")) {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    if (!this.mClientFreezingScreen) {
                        this.mClientFreezingScreen = true;
                        origId = Binder.clearCallingIdentity();
                        startFreezingDisplayLocked(exitAnim, enterAnim);
                        this.mH.removeMessages(30);
                        this.mH.sendEmptyMessageDelayed(30, 5000);
                        Binder.restoreCallingIdentity(origId);
                    }
                } catch (Throwable th) {
                    while (true) {
                        resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            resetPriorityAfterLockedSection();
            return;
        }
        throw new SecurityException("Requires FREEZE_SCREEN permission");
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public void stopFreezingScreen() {
        long origId;
        if (checkCallingPermission("android.permission.FREEZE_SCREEN", "stopFreezingScreen()")) {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    if (this.mClientFreezingScreen) {
                        this.mClientFreezingScreen = false;
                        this.mLastFinishedFreezeSource = "client";
                        origId = Binder.clearCallingIdentity();
                        stopFreezingDisplayLocked();
                        Binder.restoreCallingIdentity(origId);
                    }
                } catch (Throwable th) {
                    while (true) {
                        resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            resetPriorityAfterLockedSection();
            return;
        }
        throw new SecurityException("Requires FREEZE_SCREEN permission");
    }

    public void disableKeyguard(IBinder token, String tag, int userId) {
        int userId2 = this.mAmInternal.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId, false, 2, "disableKeyguard", (String) null);
        if (this.mContext.checkCallingOrSelfPermission("android.permission.DISABLE_KEYGUARD") != 0) {
            throw new SecurityException("Requires DISABLE_KEYGUARD permission");
        } else if (WindowManagerServiceInjector.isAllowedDisableKeyguard(this.mAppOps, Binder.getCallingUid())) {
            int callingUid = Binder.getCallingUid();
            long origIdentity = Binder.clearCallingIdentity();
            try {
                this.mKeyguardDisableHandler.disableKeyguard(token, tag, callingUid, userId2);
            } finally {
                Binder.restoreCallingIdentity(origIdentity);
            }
        }
    }

    public void reenableKeyguard(IBinder token, int userId) {
        int userId2 = this.mAmInternal.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId, false, 2, "reenableKeyguard", (String) null);
        if (this.mContext.checkCallingOrSelfPermission("android.permission.DISABLE_KEYGUARD") == 0) {
            Preconditions.checkNotNull(token, "token is null");
            int callingUid = Binder.getCallingUid();
            long origIdentity = Binder.clearCallingIdentity();
            try {
                this.mKeyguardDisableHandler.reenableKeyguard(token, callingUid, userId2);
            } finally {
                Binder.restoreCallingIdentity(origIdentity);
            }
        } else {
            throw new SecurityException("Requires DISABLE_KEYGUARD permission");
        }
    }

    public void exitKeyguardSecurely(final IOnKeyguardExitResult callback) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.DISABLE_KEYGUARD") != 0) {
            throw new SecurityException("Requires DISABLE_KEYGUARD permission");
        } else if (callback != null) {
            this.mPolicy.exitKeyguardSecurely(new WindowManagerPolicy.OnKeyguardExitResult() {
                public void onKeyguardExitResult(boolean success) {
                    try {
                        callback.onKeyguardExitResult(success);
                    } catch (RemoteException e) {
                    }
                }
            });
        } else {
            throw new IllegalArgumentException("callback == null");
        }
    }

    public boolean isKeyguardLocked() {
        return this.mPolicy.isKeyguardLocked();
    }

    public boolean isKeyguardShowingAndNotOccluded() {
        return this.mPolicy.isKeyguardShowingAndNotOccluded();
    }

    public boolean isKeyguardSecure(int userId) {
        if (userId == UserHandle.getCallingUserId() || checkCallingPermission("android.permission.INTERACT_ACROSS_USERS", "isKeyguardSecure")) {
            long origId = Binder.clearCallingIdentity();
            try {
                return this.mPolicy.isKeyguardSecure(userId);
            } finally {
                Binder.restoreCallingIdentity(origId);
            }
        } else {
            throw new SecurityException("Requires INTERACT_ACROSS_USERS permission");
        }
    }

    public boolean isShowingDream() {
        boolean isShowingDreamLw;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                isShowingDreamLw = getDefaultDisplayContentLocked().getDisplayPolicy().isShowingDreamLw();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
        return isShowingDreamLw;
    }

    public void dismissKeyguard(IKeyguardDismissCallback callback, CharSequence message) {
        if (!checkCallingPermission("android.permission.CONTROL_KEYGUARD", "dismissKeyguard")) {
            throw new SecurityException("Requires CONTROL_KEYGUARD permission");
        } else if (WindowManagerServiceInjector.isAllowedDisableKeyguard(this.mAppOps, Binder.getCallingUid())) {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    this.mPolicy.dismissKeyguardLw(callback, message);
                } catch (Throwable th) {
                    while (true) {
                        resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            resetPriorityAfterLockedSection();
        }
    }

    public void onKeyguardOccludedChanged(boolean occluded) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mPolicy.onKeyguardOccludedChangedLw(occluded);
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void setSwitchingUser(boolean switching) {
        if (checkCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL", "setSwitchingUser()")) {
            this.mPolicy.setSwitchingUser(switching);
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    this.mSwitchingUser = switching;
                } catch (Throwable th) {
                    while (true) {
                        resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            resetPriorityAfterLockedSection();
            return;
        }
        throw new SecurityException("Requires INTERACT_ACROSS_USERS_FULL permission");
    }

    /* access modifiers changed from: package-private */
    public void showGlobalActions() {
        this.mPolicy.showGlobalActions();
    }

    public void closeSystemDialogs(String reason) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mRoot.closeSystemDialogs(reason);
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    static float fixScale(float scale) {
        if (scale < 0.0f) {
            scale = 0.0f;
        } else if (scale > 20.0f) {
            scale = 20.0f;
        }
        return Math.abs(scale);
    }

    public void setAnimationScale(int which, float scale) {
        if (checkCallingPermission("android.permission.SET_ANIMATION_SCALE", "setAnimationScale()")) {
            float scale2 = fixScale(scale);
            if (which == 0) {
                this.mWindowAnimationScaleSetting = scale2;
            } else if (which == 1) {
                this.mTransitionAnimationScaleSetting = scale2;
            } else if (which == 2) {
                this.mAnimatorDurationScaleSetting = scale2;
            }
            this.mH.sendEmptyMessage(14);
            return;
        }
        throw new SecurityException("Requires SET_ANIMATION_SCALE permission");
    }

    public void setAnimationScales(float[] scales) {
        if (checkCallingPermission("android.permission.SET_ANIMATION_SCALE", "setAnimationScale()")) {
            if (scales != null) {
                if (scales.length >= 1) {
                    this.mWindowAnimationScaleSetting = fixScale(scales[0]);
                }
                if (scales.length >= 2) {
                    this.mTransitionAnimationScaleSetting = fixScale(scales[1]);
                }
                if (scales.length >= 3) {
                    this.mAnimatorDurationScaleSetting = fixScale(scales[2]);
                    dispatchNewAnimatorScaleLocked((Session) null);
                }
            }
            this.mH.sendEmptyMessage(14);
            return;
        }
        throw new SecurityException("Requires SET_ANIMATION_SCALE permission");
    }

    private void setAnimatorDurationScale(float scale) {
        this.mAnimatorDurationScaleSetting = scale;
        ValueAnimator.setDurationScale(scale);
    }

    private float animationScalesCheck(int which) {
        if (this.mAnimationsDisabled) {
            return 0.0f;
        }
        if (-1.0f != -1.0f) {
            return -1.0f;
        }
        if (which == 0) {
            return this.mWindowAnimationScaleSetting;
        }
        if (which == 1) {
            return this.mTransitionAnimationScaleSetting;
        }
        if (which != 2) {
            return -1.0f;
        }
        return this.mAnimatorDurationScaleSetting;
    }

    public float getWindowAnimationScaleLocked() {
        return animationScalesCheck(0);
    }

    public float getTransitionAnimationScaleLocked() {
        return animationScalesCheck(1);
    }

    public float getAnimationScale(int which) {
        if (which == 0) {
            return this.mWindowAnimationScaleSetting;
        }
        if (which == 1) {
            return this.mTransitionAnimationScaleSetting;
        }
        if (which != 2) {
            return 0.0f;
        }
        return this.mAnimatorDurationScaleSetting;
    }

    public float[] getAnimationScales() {
        return new float[]{this.mWindowAnimationScaleSetting, this.mTransitionAnimationScaleSetting, this.mAnimatorDurationScaleSetting};
    }

    public float getCurrentAnimatorScale() {
        float f;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                f = this.mAnimationsDisabled ? 0.0f : this.mAnimatorDurationScaleSetting;
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
        return f;
    }

    /* access modifiers changed from: package-private */
    public void dispatchNewAnimatorScaleLocked(Session session) {
        this.mH.obtainMessage(34, session).sendToTarget();
    }

    public void registerPointerEventListener(WindowManagerPolicyConstants.PointerEventListener listener, int displayId) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                if (displayContent != null) {
                    displayContent.registerPointerEventListener(listener);
                }
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void unregisterPointerEventListener(WindowManagerPolicyConstants.PointerEventListener listener, int displayId) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                if (displayContent != null) {
                    displayContent.unregisterPointerEventListener(listener);
                }
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public int getLidState() {
        int sw = this.mInputManager.getSwitchState(-1, -256, 0);
        if (sw > 0) {
            return 0;
        }
        if (sw == 0) {
            return 1;
        }
        return -1;
    }

    public void lockDeviceNow() {
        lockNow((Bundle) null);
    }

    public int getCameraLensCoverState() {
        int sw = this.mInputManager.getSwitchState(-1, -256, 9);
        if (sw > 0) {
            return 1;
        }
        if (sw == 0) {
            return 0;
        }
        return -1;
    }

    public void switchKeyboardLayout(int deviceId, int direction) {
        this.mInputManager.switchKeyboardLayout(deviceId, direction);
    }

    public void shutdown(boolean confirm) {
        ShutdownThread.shutdown(ActivityThread.currentActivityThread().getSystemUiContext(), "userrequested", confirm);
    }

    public void reboot(boolean confirm) {
        ShutdownThread.reboot(ActivityThread.currentActivityThread().getSystemUiContext(), "userrequested", confirm);
    }

    public void rebootSafeMode(boolean confirm) {
        ShutdownThread.rebootSafeMode(ActivityThread.currentActivityThread().getSystemUiContext(), confirm);
    }

    public void setCurrentProfileIds(int[] currentProfileIds) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mCurrentProfileIds = currentProfileIds;
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void setCurrentUser(int newUserId, int[] currentProfileIds) {
        int targetDensity;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mCurrentUserId = newUserId;
                this.mCurrentProfileIds = currentProfileIds;
                this.mPolicy.setCurrentUserLw(newUserId);
                this.mKeyguardDisableHandler.setCurrentUser(newUserId);
                this.mRoot.switchUser();
                this.mWindowPlacerLocked.performSurfacePlacement();
                DisplayContent displayContent = getDefaultDisplayContentLocked();
                TaskStack stack = displayContent.getSplitScreenPrimaryStackIgnoringVisibility();
                displayContent.mDividerControllerLocked.notifyDockedStackExistsChanged(stack != null && stack.hasTaskForUser(newUserId));
                this.mRoot.forAllDisplays(new Consumer(newUserId) {
                    private final /* synthetic */ int f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void accept(Object obj) {
                        ((DisplayContent) obj).mAppTransition.setCurrentUser(this.f$0);
                    }
                });
                if (this.mDisplayReady) {
                    int forcedDensity = getForcedDisplayDensityForUserLocked(newUserId);
                    if (forcedDensity != 0) {
                        targetDensity = forcedDensity;
                    } else {
                        targetDensity = displayContent.mInitialDisplayDensity;
                    }
                    displayContent.setForcedDensity(targetDensity, -2);
                }
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    /* access modifiers changed from: package-private */
    public boolean isCurrentProfileLocked(int userId) {
        if (userId == this.mCurrentUserId) {
            return true;
        }
        int i = 0;
        while (true) {
            int[] iArr = this.mCurrentProfileIds;
            if (i >= iArr.length) {
                return false;
            }
            if (iArr[i] == userId) {
                return true;
            }
            i++;
        }
    }

    public void enableScreenAfterBoot() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                if (this.mSystemBooted) {
                    resetPriorityAfterLockedSection();
                    return;
                }
                this.mSystemBooted = true;
                hideBootMessagesLocked();
                this.mH.sendEmptyMessageDelayed(23, 30000);
                resetPriorityAfterLockedSection();
                this.mPolicy.systemBooted();
                performEnableScreen();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public void enableScreenIfNeeded() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                enableScreenIfNeededLocked();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    /* access modifiers changed from: package-private */
    public void enableScreenIfNeededLocked() {
        if (!this.mDisplayEnabled) {
            if (this.mSystemBooted || this.mShowingBootMessages) {
                this.mH.sendEmptyMessage(16);
            }
        }
    }

    public void performBootTimeout() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                if (this.mDisplayEnabled) {
                    resetPriorityAfterLockedSection();
                    return;
                }
                Slog.w("WindowManager", "***** BOOT TIMEOUT: forcing display enabled");
                this.mForceDisplayEnabled = true;
                resetPriorityAfterLockedSection();
                performEnableScreen();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public void onSystemUiStarted() {
        this.mPolicy.onSystemUiStarted();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:62:0x00c3, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:?, code lost:
        r8.mActivityManager.bootAnimationComplete();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void performEnableScreen() {
        /*
            r8 = this;
            com.android.server.wm.WindowManagerGlobalLock r0 = r8.mGlobalLock
            monitor-enter(r0)
            boostPriorityForLockedSection()     // Catch:{ all -> 0x00e7 }
            boolean r1 = r8.mOnlyCore     // Catch:{ all -> 0x00e7 }
            if (r1 == 0) goto L_0x001d
            java.lang.String r1 = ""
            java.lang.String r2 = "vold.encrypt_progress"
            java.lang.String r2 = android.os.SystemProperties.get(r2)     // Catch:{ all -> 0x00e7 }
            boolean r1 = r1.equals(r2)     // Catch:{ all -> 0x00e7 }
            if (r1 != 0) goto L_0x001d
            monitor-exit(r0)     // Catch:{ all -> 0x00e7 }
            resetPriorityAfterLockedSection()
            return
        L_0x001d:
            boolean r1 = r8.mDisplayEnabled     // Catch:{ all -> 0x00e7 }
            if (r1 == 0) goto L_0x0026
            monitor-exit(r0)     // Catch:{ all -> 0x00e7 }
            resetPriorityAfterLockedSection()
            return
        L_0x0026:
            boolean r1 = r8.mSystemBooted     // Catch:{ all -> 0x00e7 }
            if (r1 != 0) goto L_0x0033
            boolean r1 = r8.mShowingBootMessages     // Catch:{ all -> 0x00e7 }
            if (r1 != 0) goto L_0x0033
            monitor-exit(r0)     // Catch:{ all -> 0x00e7 }
            resetPriorityAfterLockedSection()
            return
        L_0x0033:
            boolean r1 = r8.mShowingBootMessages     // Catch:{ all -> 0x00e7 }
            if (r1 != 0) goto L_0x0044
            com.android.server.policy.WindowManagerPolicy r1 = r8.mPolicy     // Catch:{ all -> 0x00e7 }
            boolean r1 = r1.canDismissBootAnimation()     // Catch:{ all -> 0x00e7 }
            if (r1 != 0) goto L_0x0044
            monitor-exit(r0)     // Catch:{ all -> 0x00e7 }
            resetPriorityAfterLockedSection()
            return
        L_0x0044:
            boolean r1 = r8.mForceDisplayEnabled     // Catch:{ all -> 0x00e7 }
            if (r1 != 0) goto L_0x0057
            com.android.server.wm.DisplayContent r1 = r8.getDefaultDisplayContentLocked()     // Catch:{ all -> 0x00e7 }
            boolean r1 = r1.checkWaitingForWindows()     // Catch:{ all -> 0x00e7 }
            if (r1 == 0) goto L_0x0057
            monitor-exit(r0)     // Catch:{ all -> 0x00e7 }
            resetPriorityAfterLockedSection()
            return
        L_0x0057:
            boolean r1 = r8.mBootAnimationStopped     // Catch:{ all -> 0x00e7 }
            r2 = 32
            r4 = 1
            r5 = 0
            if (r1 != 0) goto L_0x006d
            java.lang.String r1 = "Stop bootanim"
            android.os.Trace.asyncTraceBegin(r2, r1, r5)     // Catch:{ all -> 0x00e7 }
            java.lang.String r1 = "service.bootanim.exit"
            java.lang.String r6 = "1"
            android.os.SystemProperties.set(r1, r6)     // Catch:{ all -> 0x00e7 }
            r8.mBootAnimationStopped = r4     // Catch:{ all -> 0x00e7 }
        L_0x006d:
            boolean r1 = r8.mForceDisplayEnabled     // Catch:{ all -> 0x00e7 }
            if (r1 != 0) goto L_0x007c
            boolean r1 = r8.checkBootAnimationCompleteLocked()     // Catch:{ all -> 0x00e7 }
            if (r1 != 0) goto L_0x007c
            monitor-exit(r0)     // Catch:{ all -> 0x00e7 }
            resetPriorityAfterLockedSection()
            return
        L_0x007c:
            java.lang.String r1 = "SurfaceFlinger"
            android.os.IBinder r1 = android.os.ServiceManager.getService(r1)     // Catch:{ RemoteException -> 0x009c }
            if (r1 == 0) goto L_0x009b
            java.lang.String r6 = "WindowManager"
            java.lang.String r7 = "******* TELLING SURFACE FLINGER WE ARE BOOTED!"
            android.util.Slog.i(r6, r7)     // Catch:{ RemoteException -> 0x009c }
            android.os.Parcel r6 = android.os.Parcel.obtain()     // Catch:{ RemoteException -> 0x009c }
            java.lang.String r7 = "android.ui.ISurfaceComposer"
            r6.writeInterfaceToken(r7)     // Catch:{ RemoteException -> 0x009c }
            r7 = 0
            r1.transact(r4, r6, r7, r5)     // Catch:{ RemoteException -> 0x009c }
            r6.recycle()     // Catch:{ RemoteException -> 0x009c }
        L_0x009b:
            goto L_0x00a4
        L_0x009c:
            r1 = move-exception
            java.lang.String r6 = "WindowManager"
            java.lang.String r7 = "Boot completed: SurfaceFlinger is dead!"
            android.util.Slog.e(r6, r7)     // Catch:{ all -> 0x00e7 }
        L_0x00a4:
            r1 = 31007(0x791f, float:4.345E-41)
            long r6 = android.os.SystemClock.uptimeMillis()     // Catch:{ all -> 0x00e7 }
            android.util.EventLog.writeEvent(r1, r6)     // Catch:{ all -> 0x00e7 }
            java.lang.String r1 = "Stop bootanim"
            android.os.Trace.asyncTraceEnd(r2, r1, r5)     // Catch:{ all -> 0x00e7 }
            r8.mDisplayEnabled = r4     // Catch:{ all -> 0x00e7 }
            java.lang.String r1 = "WindowManager"
            java.lang.String r2 = "******************** ENABLING SCREEN!"
            android.util.Slog.i(r1, r2)     // Catch:{ all -> 0x00e7 }
            com.android.server.wm.InputManagerCallback r1 = r8.mInputManagerCallback     // Catch:{ all -> 0x00e7 }
            boolean r2 = r8.mEventDispatchingEnabled     // Catch:{ all -> 0x00e7 }
            r1.setEventDispatchingLw(r2)     // Catch:{ all -> 0x00e7 }
            monitor-exit(r0)     // Catch:{ all -> 0x00e7 }
            resetPriorityAfterLockedSection()
            android.app.IActivityManager r0 = r8.mActivityManager     // Catch:{ RemoteException -> 0x00cc }
            r0.bootAnimationComplete()     // Catch:{ RemoteException -> 0x00cc }
            goto L_0x00cd
        L_0x00cc:
            r0 = move-exception
        L_0x00cd:
            boolean r0 = r8.enableMIUIWatermark
            if (r0 == 0) goto L_0x00de
            java.lang.String r0 = "WindowManager"
            java.lang.String r1 = "initwatermark after boot"
            android.util.Slog.i(r0, r1)
            com.android.server.wm.MIUIWatermark r0 = com.android.server.wm.MIUIWatermark.initWatermark(r8)
            r8.mMIUIWatermark = r0
        L_0x00de:
            com.android.server.policy.WindowManagerPolicy r0 = r8.mPolicy
            r0.enableScreenAfterBoot()
            r8.updateRotationUnchecked(r5, r5)
            return
        L_0x00e7:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00e7 }
            resetPriorityAfterLockedSection()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.performEnableScreen():void");
    }

    /* access modifiers changed from: private */
    public boolean checkBootAnimationCompleteLocked() {
        if (!SystemService.isRunning(BOOT_ANIMATION_SERVICE)) {
            return true;
        }
        this.mH.removeMessages(37);
        this.mH.sendEmptyMessageDelayed(37, 200);
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:26:0x002e, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0031, code lost:
        if (r0 == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0033, code lost:
        performEnableScreen();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void showBootMessage(java.lang.CharSequence r4, boolean r5) {
        /*
            r3 = this;
            r0 = 0
            com.android.server.wm.WindowManagerGlobalLock r1 = r3.mGlobalLock
            monitor-enter(r1)
            boostPriorityForLockedSection()     // Catch:{ all -> 0x0037 }
            boolean r2 = r3.mAllowBootMessages     // Catch:{ all -> 0x0037 }
            if (r2 != 0) goto L_0x0010
            monitor-exit(r1)     // Catch:{ all -> 0x0037 }
            resetPriorityAfterLockedSection()
            return
        L_0x0010:
            boolean r2 = r3.mShowingBootMessages     // Catch:{ all -> 0x0037 }
            if (r2 != 0) goto L_0x001c
            if (r5 != 0) goto L_0x001b
            monitor-exit(r1)     // Catch:{ all -> 0x0037 }
            resetPriorityAfterLockedSection()
            return
        L_0x001b:
            r0 = 1
        L_0x001c:
            boolean r2 = r3.mSystemBooted     // Catch:{ all -> 0x0037 }
            if (r2 == 0) goto L_0x0025
            monitor-exit(r1)     // Catch:{ all -> 0x0037 }
            resetPriorityAfterLockedSection()
            return
        L_0x0025:
            r2 = 1
            r3.mShowingBootMessages = r2     // Catch:{ all -> 0x0037 }
            com.android.server.policy.WindowManagerPolicy r2 = r3.mPolicy     // Catch:{ all -> 0x0037 }
            r2.showBootMessage(r4, r5)     // Catch:{ all -> 0x0037 }
            monitor-exit(r1)     // Catch:{ all -> 0x0037 }
            resetPriorityAfterLockedSection()
            if (r0 == 0) goto L_0x0036
            r3.performEnableScreen()
        L_0x0036:
            return
        L_0x0037:
            r2 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0037 }
            resetPriorityAfterLockedSection()
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.showBootMessage(java.lang.CharSequence, boolean):void");
    }

    public void hideBootMessagesLocked() {
        if (this.mShowingBootMessages) {
            this.mShowingBootMessages = false;
            this.mPolicy.hideBootMessages();
        }
    }

    public void setInTouchMode(boolean mode) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mInTouchMode = mode;
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    /* access modifiers changed from: private */
    public void updateContrastAlpha(final boolean darkmode) {
        this.mH.post(new Runnable() {
            /* Debug info: failed to restart local var, previous not found, register: 8 */
            public void run() {
                String str;
                float alpha = Settings.System.getFloat(WindowManagerService.this.mContext.getContentResolver(), "contrast_alpha", 0.0f);
                if (alpha >= 0.5f) {
                    alpha = 0.0f;
                }
                boolean isContrastEnabled = WindowManagerService.this.isDarkModeContrastEnable();
                Slog.i("WindowManager", "updateContrastOverlay, darkmode: " + darkmode + " isContrastEnabled: " + isContrastEnabled + " alpha: " + alpha);
                synchronized (WindowManagerService.this.mWindowMap) {
                    WindowManagerService.this.openSurfaceTransaction();
                    try {
                        if (!darkmode || !isContrastEnabled) {
                            if (WindowManagerService.this.mMiuiContrastOverlay != null) {
                                Slog.i("WindowManager", " hideContrastOverlay ");
                                WindowManagerService.this.mMiuiContrastOverlay.hideContrastOverlay();
                            }
                            WindowManagerService.this.mMiuiContrastOverlay = null;
                        } else {
                            if (WindowManagerService.this.mMiuiContrastOverlay == null) {
                                DisplayContent displayContent = WindowManagerService.this.getDefaultDisplayContentLocked();
                                WindowManagerService.this.mMiuiContrastOverlay = new MiuiContrastOverlay(displayContent, displayContent.mRealDisplayMetrics, WindowManagerService.this.mContext);
                            }
                            WindowManagerService.this.mMiuiContrastOverlay.showContrastOverlay(alpha);
                        }
                    } finally {
                        str = "MiuiContrastOverlay";
                        WindowManagerService.this.closeSurfaceTransaction(str);
                    }
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public boolean isDarkModeContrastEnable() {
        boolean isContrastEnable = false;
        if (MiuiSettings.System.getBoolean(this.mContext.getContentResolver(), "dark_mode_enable", false) && MiuiSettings.System.getBoolean(this.mContext.getContentResolver(), "dark_mode_contrast_enable", true)) {
            isContrastEnable = true;
        }
        return isContrastEnable;
    }

    /* access modifiers changed from: private */
    public void updateTalkbackWatermark(final boolean talkbackMode) {
        this.mH.post(new Runnable() {
            /* Debug info: failed to restart local var, previous not found, register: 9 */
            public void run() {
                String str;
                boolean showSettings = MiuiSettings.Secure.getBoolean(WindowManagerService.this.mContext.getContentResolver(), "talkback_watermark_enable", true);
                synchronized (WindowManagerService.this.mWindowMap) {
                    WindowManagerService.this.openSurfaceTransaction();
                    try {
                        if (talkbackMode && showSettings) {
                            if (WindowManagerService.this.mTalkbackWatermark == null) {
                                DisplayContent displayContent = WindowManagerService.this.getDefaultDisplayContentLocked();
                                WindowManagerService.this.mTalkbackWatermark = new TalkbackWatermark(displayContent, displayContent.mRealDisplayMetrics, WindowManagerService.this.mContext);
                            }
                            WindowManagerService.this.mTalkbackWatermark.setVisibility(true);
                        } else {
                            if (WindowManagerService.this.mTalkbackWatermark != null) {
                                WindowManagerService.this.mTalkbackWatermark.setVisibility(false);
                            }
                            WindowManagerService.this.mTalkbackWatermark = null;
                        }
                    } finally {
                        str = "updateTalkbackWatermark";
                        WindowManagerService.this.closeSurfaceTransaction(str);
                    }
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void updateCircularDisplayMaskIfNeeded() {
        int currentUserId;
        if (this.mContext.getResources().getConfiguration().isScreenRound() && this.mContext.getResources().getBoolean(17891612)) {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    currentUserId = this.mCurrentUserId;
                } catch (Throwable th) {
                    while (true) {
                        resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            resetPriorityAfterLockedSection();
            int showMask = 0;
            if (Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "accessibility_display_inversion_enabled", 0, currentUserId) != 1) {
                showMask = 1;
            }
            Message m = this.mH.obtainMessage(35);
            m.arg1 = showMask;
            this.mH.sendMessage(m);
        }
    }

    public void showEmulatorDisplayOverlayIfNeeded() {
        if (this.mContext.getResources().getBoolean(17891608) && SystemProperties.getBoolean(PROPERTY_EMULATOR_CIRCULAR, false) && Build.IS_EMULATOR) {
            H h = this.mH;
            h.sendMessage(h.obtainMessage(36));
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public void showCircularMask(boolean visible) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                openSurfaceTransaction();
                if (visible) {
                    if (this.mCircularDisplayMask == null) {
                        this.mCircularDisplayMask = new CircularDisplayMask(getDefaultDisplayContentLocked(), (this.mPolicy.getWindowLayerFromTypeLw(2018) * 10000) + 10, this.mContext.getResources().getInteger(17694962), this.mContext.getResources().getDimensionPixelSize(17105047));
                    }
                    this.mCircularDisplayMask.setVisibility(true);
                } else if (this.mCircularDisplayMask != null) {
                    this.mCircularDisplayMask.setVisibility(false);
                    this.mCircularDisplayMask = null;
                }
                closeSurfaceTransaction("showCircularMask");
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public void showEmulatorDisplayOverlay() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                openSurfaceTransaction();
                if (this.mEmulatorDisplayOverlay == null) {
                    this.mEmulatorDisplayOverlay = new EmulatorDisplayOverlay(this.mContext, getDefaultDisplayContentLocked(), (this.mPolicy.getWindowLayerFromTypeLw(2018) * 10000) + 10);
                }
                this.mEmulatorDisplayOverlay.setVisibility(true);
                closeSurfaceTransaction("showEmulatorDisplayOverlay");
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void showStrictModeViolation(boolean on) {
        int pid = Binder.getCallingPid();
        if (on) {
            H h = this.mH;
            h.sendMessage(h.obtainMessage(25, 1, pid));
            H h2 = this.mH;
            h2.sendMessageDelayed(h2.obtainMessage(25, 0, pid), 1000);
            return;
        }
        H h3 = this.mH;
        h3.sendMessage(h3.obtainMessage(25, 0, pid));
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: private */
    public void showStrictModeViolation(int arg, int pid) {
        boolean on = arg != 0;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                if (!on || this.mRoot.canShowStrictModeViolation(pid)) {
                    SurfaceControl.openTransaction();
                    if (this.mStrictModeFlash == null) {
                        this.mStrictModeFlash = new StrictModeFlash(getDefaultDisplayContentLocked());
                    }
                    this.mStrictModeFlash.setVisibility(on);
                    SurfaceControl.closeTransaction();
                    resetPriorityAfterLockedSection();
                    return;
                }
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public void setStrictModeVisualIndicatorPreference(String value) {
        SystemProperties.set("persist.sys.strictmode.visual", value);
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public Bitmap screenshotWallpaper() {
        Bitmap screenshotWallpaperLocked;
        if (checkCallingPermission("android.permission.READ_FRAME_BUFFER", "screenshotWallpaper()")) {
            try {
                Trace.traceBegin(32, "screenshotWallpaper");
                synchronized (this.mGlobalLock) {
                    boostPriorityForLockedSection();
                    screenshotWallpaperLocked = this.mRoot.getDisplayContent(0).mWallpaperController.screenshotWallpaperLocked();
                }
                resetPriorityAfterLockedSection();
                Trace.traceEnd(32);
                return screenshotWallpaperLocked;
            } catch (Throwable th) {
                Trace.traceEnd(32);
                throw th;
            }
        } else {
            throw new SecurityException("Requires READ_FRAME_BUFFER permission");
        }
    }

    public boolean requestAssistScreenshot(IAssistDataReceiver receiver) {
        Bitmap bm;
        if (checkCallingPermission("android.permission.READ_FRAME_BUFFER", "requestAssistScreenshot()")) {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    DisplayContent displayContent = this.mRoot.getDisplayContent(0);
                    if (displayContent == null) {
                        bm = null;
                    } else {
                        bm = displayContent.screenshotDisplayLocked(Bitmap.Config.ARGB_8888);
                    }
                } catch (Throwable th) {
                    while (true) {
                        resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            resetPriorityAfterLockedSection();
            FgThread.getHandler().post(new Runnable(receiver, bm) {
                private final /* synthetic */ IAssistDataReceiver f$0;
                private final /* synthetic */ Bitmap f$1;

                {
                    this.f$0 = r1;
                    this.f$1 = r2;
                }

                public final void run() {
                    WindowManagerService.lambda$requestAssistScreenshot$3(this.f$0, this.f$1);
                }
            });
            return true;
        }
        throw new SecurityException("Requires READ_FRAME_BUFFER permission");
    }

    static /* synthetic */ void lambda$requestAssistScreenshot$3(IAssistDataReceiver receiver, Bitmap bm) {
        try {
            receiver.onHandleAssistScreenshot(bm);
        } catch (RemoteException e) {
        }
    }

    public ActivityManager.TaskSnapshot getTaskSnapshot(int taskId, int userId, boolean reducedResolution, boolean restoreFromDisk) {
        return this.mTaskSnapshotController.getSnapshot(taskId, userId, restoreFromDisk, reducedResolution);
    }

    public void removeObsoleteTaskFiles(ArraySet<Integer> persistentTaskIds, int[] runningUserIds) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mTaskSnapshotController.removeObsoleteTaskFiles(persistentTaskIds, runningUserIds);
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    /* access modifiers changed from: package-private */
    public void setRotateForApp(int displayId, int fixedToUserRotation) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent display = this.mRoot.getDisplayContent(displayId);
                if (display == null) {
                    Slog.w("WindowManager", "Trying to set rotate for app for a missing display.");
                    resetPriorityAfterLockedSection();
                    return;
                }
                display.getDisplayRotation().setFixedToUserRotation(fixedToUserRotation);
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public void freezeRotation(int rotation) {
        freezeDisplayRotation(0, rotation);
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public void freezeDisplayRotation(int displayId, int rotation) {
        int pid = Binder.getCallingPid();
        Slog.w("WindowManager", "freezeDisplayRotation rotation= " + rotation + " displayId=" + displayId + " by pid=" + pid);
        if (!checkCallingPermission("android.permission.SET_ORIENTATION", "freezeRotation()")) {
            throw new SecurityException("Requires SET_ORIENTATION permission");
        } else if (rotation < -1 || rotation > 3) {
            throw new IllegalArgumentException("Rotation argument must be -1 or a valid rotation constant.");
        } else {
            long origId = Binder.clearCallingIdentity();
            try {
                synchronized (this.mGlobalLock) {
                    boostPriorityForLockedSection();
                    DisplayContent display = this.mRoot.getDisplayContent(displayId);
                    if (display == null) {
                        Slog.w("WindowManager", "Trying to freeze rotation for a missing display.");
                        resetPriorityAfterLockedSection();
                        Binder.restoreCallingIdentity(origId);
                        return;
                    }
                    display.getDisplayRotation().freezeRotation(rotation);
                    resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(origId);
                    updateRotationUnchecked(false, false);
                }
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(origId);
                throw th;
            }
        }
    }

    public void thawRotation() {
        thawDisplayRotation(0);
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public void thawDisplayRotation(int displayId) {
        int pid = Binder.getCallingPid();
        Slog.w("WindowManager", "thawDisplayRotation displayId=" + displayId + " by pid=" + pid);
        if (checkCallingPermission("android.permission.SET_ORIENTATION", "thawRotation()")) {
            long origId = Binder.clearCallingIdentity();
            try {
                synchronized (this.mGlobalLock) {
                    boostPriorityForLockedSection();
                    DisplayContent display = this.mRoot.getDisplayContent(displayId);
                    if (display == null) {
                        Slog.w("WindowManager", "Trying to thaw rotation for a missing display.");
                        resetPriorityAfterLockedSection();
                        Binder.restoreCallingIdentity(origId);
                        return;
                    }
                    display.getDisplayRotation().thawRotation();
                    resetPriorityAfterLockedSection();
                    Binder.restoreCallingIdentity(origId);
                    updateRotationUnchecked(false, false);
                }
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(origId);
                throw th;
            }
        } else {
            throw new SecurityException("Requires SET_ORIENTATION permission");
        }
    }

    public boolean isRotationFrozen() {
        return isDisplayRotationFrozen(0);
    }

    public boolean isDisplayRotationFrozen(int displayId) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent display = this.mRoot.getDisplayContent(displayId);
                if (display == null) {
                    Slog.w("WindowManager", "Trying to thaw rotation for a missing display.");
                    resetPriorityAfterLockedSection();
                    return false;
                }
                boolean isRotationFrozen = display.getDisplayRotation().isRotationFrozen();
                resetPriorityAfterLockedSection();
                return isRotationFrozen;
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public void updateRotation(boolean alwaysSendConfiguration, boolean forceRelayout) {
        updateRotationUnchecked(alwaysSendConfiguration, forceRelayout);
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    private void updateRotationUnchecked(boolean alwaysSendConfiguration, boolean forceRelayout) {
        Trace.traceBegin(32, "updateRotation");
        long origId = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                boolean layoutNeeded = false;
                int displayCount = this.mRoot.mChildren.size();
                for (int i = 0; i < displayCount; i++) {
                    DisplayContent displayContent = (DisplayContent) this.mRoot.mChildren.get(i);
                    Trace.traceBegin(32, "updateRotation: display");
                    boolean rotationChanged = displayContent.updateRotationUnchecked();
                    Trace.traceEnd(32);
                    if (!rotationChanged || forceRelayout) {
                        displayContent.setLayoutNeeded();
                        layoutNeeded = true;
                    }
                    if (rotationChanged || alwaysSendConfiguration) {
                        displayContent.sendNewConfiguration();
                    }
                }
                if (layoutNeeded) {
                    Trace.traceBegin(32, "updateRotation: performSurfacePlacement");
                    this.mWindowPlacerLocked.performSurfacePlacement();
                    Trace.traceEnd(32);
                }
            }
            resetPriorityAfterLockedSection();
            Binder.restoreCallingIdentity(origId);
            Trace.traceEnd(32);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(origId);
            Trace.traceEnd(32);
            throw th;
        }
    }

    public int getDefaultDisplayRotation() {
        int rotation;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                rotation = getDefaultDisplayContentLocked().getRotation();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
        return rotation;
    }

    public int watchRotation(IRotationWatcher watcher, int displayId) {
        DisplayContent displayContent;
        int rotation;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                displayContent = this.mRoot.getDisplayContent(displayId);
            } finally {
                while (true) {
                    resetPriorityAfterLockedSection();
                }
            }
        }
        resetPriorityAfterLockedSection();
        if (displayContent != null) {
            final IBinder watcherBinder = watcher.asBinder();
            IBinder.DeathRecipient dr = new IBinder.DeathRecipient() {
                public void binderDied() {
                    synchronized (WindowManagerService.this.mGlobalLock) {
                        try {
                            WindowManagerService.boostPriorityForLockedSection();
                            int i = 0;
                            while (i < WindowManagerService.this.mRotationWatchers.size()) {
                                if (watcherBinder == WindowManagerService.this.mRotationWatchers.get(i).mWatcher.asBinder()) {
                                    IBinder binder = WindowManagerService.this.mRotationWatchers.remove(i).mWatcher.asBinder();
                                    if (binder != null) {
                                        binder.unlinkToDeath(this, 0);
                                    }
                                    i--;
                                }
                                i++;
                            }
                        } catch (Throwable th) {
                            while (true) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                throw th;
                            }
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                }
            };
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    watcher.asBinder().linkToDeath(dr, 0);
                    this.mRotationWatchers.add(new RotationWatcher(watcher, dr, displayId));
                } catch (RemoteException e) {
                }
                try {
                    rotation = displayContent.getRotation();
                } catch (Throwable th) {
                    while (true) {
                        throw th;
                    }
                }
            }
            resetPriorityAfterLockedSection();
            return rotation;
        }
        throw new IllegalArgumentException("Trying to register rotation event for invalid display: " + displayId);
    }

    public void removeRotationWatcher(IRotationWatcher watcher) {
        IBinder watcherBinder = watcher.asBinder();
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                int i = 0;
                while (i < this.mRotationWatchers.size()) {
                    if (watcherBinder == this.mRotationWatchers.get(i).mWatcher.asBinder()) {
                        RotationWatcher removed = this.mRotationWatchers.remove(i);
                        IBinder binder = removed.mWatcher.asBinder();
                        if (binder != null) {
                            binder.unlinkToDeath(removed.mDeathRecipient, 0);
                        }
                        i--;
                    }
                    i++;
                }
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public boolean registerWallpaperVisibilityListener(IWallpaperVisibilityListener listener, int displayId) {
        boolean isWallpaperVisible;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                if (displayContent != null) {
                    this.mWallpaperVisibilityListeners.registerWallpaperVisibilityListener(listener, displayId);
                    isWallpaperVisible = displayContent.mWallpaperController.isWallpaperVisible();
                } else {
                    throw new IllegalArgumentException("Trying to register visibility event for invalid display: " + displayId);
                }
            } finally {
                resetPriorityAfterLockedSection();
            }
        }
        return isWallpaperVisible;
    }

    public void unregisterWallpaperVisibilityListener(IWallpaperVisibilityListener listener, int displayId) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mWallpaperVisibilityListeners.unregisterWallpaperVisibilityListener(listener, displayId);
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void registerSystemGestureExclusionListener(ISystemGestureExclusionListener listener, int displayId) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                if (displayContent != null) {
                    displayContent.registerSystemGestureExclusionListener(listener);
                } else {
                    throw new IllegalArgumentException("Trying to register visibility event for invalid display: " + displayId);
                }
            } finally {
                resetPriorityAfterLockedSection();
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void unregisterSystemGestureExclusionListener(ISystemGestureExclusionListener listener, int displayId) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                if (displayContent != null) {
                    displayContent.unregisterSystemGestureExclusionListener(listener);
                } else {
                    throw new IllegalArgumentException("Trying to register visibility event for invalid display: " + displayId);
                }
            } finally {
                resetPriorityAfterLockedSection();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void reportSystemGestureExclusionChanged(Session session, IWindow window, List<Rect> exclusionRects) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                WindowState win = windowForClientLocked(session, window, true);
                if (win.setSystemGestureExclusion(exclusionRects)) {
                    win.getDisplayContent().updateSystemGestureExclusion();
                }
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void registerDisplayFoldListener(IDisplayFoldListener listener) {
        this.mPolicy.registerDisplayFoldListener(listener);
    }

    public void unregisterDisplayFoldListener(IDisplayFoldListener listener) {
        this.mPolicy.unregisterDisplayFoldListener(listener);
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: package-private */
    public void setOverrideFoldedArea(Rect area) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") == 0) {
            long origId = Binder.clearCallingIdentity();
            try {
                synchronized (this.mGlobalLock) {
                    boostPriorityForLockedSection();
                    this.mPolicy.setOverrideFoldedArea(area);
                }
                resetPriorityAfterLockedSection();
                Binder.restoreCallingIdentity(origId);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(origId);
                throw th;
            }
        } else {
            throw new SecurityException("Must hold permission android.permission.WRITE_SECURE_SETTINGS");
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: package-private */
    public Rect getFoldedArea() {
        Rect foldedArea;
        long origId = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                foldedArea = this.mPolicy.getFoldedArea();
            }
            resetPriorityAfterLockedSection();
            Binder.restoreCallingIdentity(origId);
            return foldedArea;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(origId);
            throw th;
        }
    }

    public int getPreferredOptionsPanelGravity(int displayId) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                if (displayContent == null) {
                    resetPriorityAfterLockedSection();
                    return 81;
                }
                int preferredOptionsPanelGravity = displayContent.getPreferredOptionsPanelGravity();
                resetPriorityAfterLockedSection();
                return preferredOptionsPanelGravity;
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public boolean startViewServer(int port) {
        if (isSystemSecure() || !checkCallingPermission("android.permission.DUMP", "startViewServer") || port < 1024) {
            return false;
        }
        ViewServer viewServer = this.mViewServer;
        if (viewServer != null) {
            if (!viewServer.isRunning()) {
                try {
                    return this.mViewServer.start();
                } catch (IOException e) {
                    Slog.w("WindowManager", "View server did not start");
                }
            }
            return false;
        }
        try {
            this.mViewServer = new ViewServer(this, port);
            return this.mViewServer.start();
        } catch (IOException e2) {
            Slog.w("WindowManager", "View server did not start");
            return false;
        }
    }

    private boolean isSystemSecure() {
        return SplitScreenReporter.ACTION_ENTER_SPLIT.equals(SystemProperties.get(SYSTEM_SECURE, SplitScreenReporter.ACTION_ENTER_SPLIT)) && "0".equals(SystemProperties.get(SYSTEM_DEBUGGABLE, "0"));
    }

    public boolean stopViewServer() {
        ViewServer viewServer;
        if (!isSystemSecure() && checkCallingPermission("android.permission.DUMP", "stopViewServer") && (viewServer = this.mViewServer) != null) {
            return viewServer.stop();
        }
        return false;
    }

    public boolean isViewServerRunning() {
        ViewServer viewServer;
        if (!isSystemSecure() && checkCallingPermission("android.permission.DUMP", "isViewServerRunning") && (viewServer = this.mViewServer) != null && viewServer.isRunning()) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public boolean viewServerListWindows(Socket client) {
        if (isSystemSecure()) {
            return false;
        }
        ArrayList<WindowState> windows = new ArrayList<>();
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mRoot.forAllWindows((Consumer<WindowState>) new Consumer(windows) {
                    private final /* synthetic */ ArrayList f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void accept(Object obj) {
                        this.f$0.add((WindowState) obj);
                    }
                }, false);
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
        BufferedWriter out = null;
        try {
            BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()), 8192);
            int count = windows.size();
            for (int i = 0; i < count; i++) {
                WindowState w = windows.get(i);
                out2.write(Integer.toHexString(System.identityHashCode(w)));
                out2.write(32);
                out2.append(w.mAttrs.getTitle());
                out2.write(10);
            }
            out2.write("DONE.\n");
            out2.flush();
            try {
                out2.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        } catch (Exception e2) {
            if (out == null) {
                return false;
            }
            out.close();
            return false;
        } catch (Throwable th2) {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e3) {
                }
            }
            throw th2;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean viewServerGetFocusedWindow(Socket client) {
        if (isSystemSecure()) {
            return false;
        }
        WindowState focusedWindow = getFocusedWindow();
        BufferedWriter out = null;
        try {
            BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()), 8192);
            if (focusedWindow != null) {
                out2.write(Integer.toHexString(System.identityHashCode(focusedWindow)));
                out2.write(32);
                out2.append(focusedWindow.mAttrs.getTitle());
            }
            out2.write(10);
            out2.flush();
            try {
                out2.close();
                return true;
            } catch (IOException e) {
                return false;
            }
        } catch (Exception e2) {
            if (out == null) {
                return false;
            }
            out.close();
            return false;
        } catch (Throwable th) {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e3) {
                }
            }
            throw th;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean viewServerWindowCommand(Socket client, String command, String parameters) {
        if (isSystemSecure()) {
            return false;
        }
        boolean success = true;
        Parcel data = null;
        Parcel reply = null;
        BufferedWriter out = null;
        try {
            int index = parameters.indexOf(32);
            if (index == -1) {
                index = parameters.length();
            }
            int hashCode = (int) Long.parseLong(parameters.substring(0, index), 16);
            if (index < parameters.length()) {
                parameters = parameters.substring(index + 1);
            } else {
                parameters = "";
            }
            WindowState window = findWindow(hashCode);
            if (window == null) {
                if (data != null) {
                    data.recycle();
                }
                if (reply != null) {
                    reply.recycle();
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
                return false;
            }
            Parcel data2 = Parcel.obtain();
            data2.writeInterfaceToken("android.view.IWindow");
            data2.writeString(command);
            data2.writeString(parameters);
            data2.writeInt(1);
            ParcelFileDescriptor.fromSocket(client).writeToParcel(data2, 0);
            Parcel reply2 = Parcel.obtain();
            window.mClient.asBinder().transact(1, data2, reply2, 0);
            reply2.readException();
            if (!client.isOutputShutdown()) {
                out = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
                out.write("DONE\n");
                out.flush();
            }
            data2.recycle();
            reply2.recycle();
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e2) {
                }
            }
            return success;
        } catch (Exception e3) {
            Slog.w("WindowManager", "Could not send command " + command + " with parameters " + parameters, e3);
            success = false;
            if (data != null) {
                data.recycle();
            }
            if (reply != null) {
                reply.recycle();
            }
            if (out != null) {
                out.close();
            }
        } catch (Throwable th) {
            if (data != null) {
                data.recycle();
            }
            if (reply != null) {
                reply.recycle();
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e4) {
                }
            }
            throw th;
        }
    }

    public void addWindowChangeListener(WindowChangeListener listener) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mWindowChangeListeners.add(listener);
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void removeWindowChangeListener(WindowChangeListener listener) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mWindowChangeListeners.remove(listener);
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0025, code lost:
        resetPriorityAfterLockedSection();
        r0 = r1.length;
        r2 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x002a, code lost:
        if (r2 >= r0) goto L_0x0034;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002c, code lost:
        r1[r2].windowsChanged();
        r2 = r2 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0034, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void notifyWindowsChanged() {
        /*
            r4 = this;
            com.android.server.wm.WindowManagerGlobalLock r0 = r4.mGlobalLock
            monitor-enter(r0)
            boostPriorityForLockedSection()     // Catch:{ all -> 0x0035 }
            java.util.ArrayList<com.android.server.wm.WindowManagerService$WindowChangeListener> r1 = r4.mWindowChangeListeners     // Catch:{ all -> 0x0035 }
            boolean r1 = r1.isEmpty()     // Catch:{ all -> 0x0035 }
            if (r1 == 0) goto L_0x0013
            monitor-exit(r0)     // Catch:{ all -> 0x0035 }
            resetPriorityAfterLockedSection()
            return
        L_0x0013:
            java.util.ArrayList<com.android.server.wm.WindowManagerService$WindowChangeListener> r1 = r4.mWindowChangeListeners     // Catch:{ all -> 0x0035 }
            int r1 = r1.size()     // Catch:{ all -> 0x0035 }
            com.android.server.wm.WindowManagerService$WindowChangeListener[] r1 = new com.android.server.wm.WindowManagerService.WindowChangeListener[r1]     // Catch:{ all -> 0x0035 }
            java.util.ArrayList<com.android.server.wm.WindowManagerService$WindowChangeListener> r2 = r4.mWindowChangeListeners     // Catch:{ all -> 0x0035 }
            java.lang.Object[] r2 = r2.toArray(r1)     // Catch:{ all -> 0x0035 }
            com.android.server.wm.WindowManagerService$WindowChangeListener[] r2 = (com.android.server.wm.WindowManagerService.WindowChangeListener[]) r2     // Catch:{ all -> 0x0035 }
            r1 = r2
            monitor-exit(r0)     // Catch:{ all -> 0x0035 }
            resetPriorityAfterLockedSection()
            int r0 = r1.length
            r2 = 0
        L_0x002a:
            if (r2 >= r0) goto L_0x0034
            r3 = r1[r2]
            r3.windowsChanged()
            int r2 = r2 + 1
            goto L_0x002a
        L_0x0034:
            return
        L_0x0035:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0035 }
            resetPriorityAfterLockedSection()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.notifyWindowsChanged():void");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0025, code lost:
        resetPriorityAfterLockedSection();
        r0 = r1.length;
        r2 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x002a, code lost:
        if (r2 >= r0) goto L_0x0034;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x002c, code lost:
        r1[r2].focusChanged();
        r2 = r2 + 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0034, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void notifyFocusChanged() {
        /*
            r4 = this;
            com.android.server.wm.WindowManagerGlobalLock r0 = r4.mGlobalLock
            monitor-enter(r0)
            boostPriorityForLockedSection()     // Catch:{ all -> 0x0035 }
            java.util.ArrayList<com.android.server.wm.WindowManagerService$WindowChangeListener> r1 = r4.mWindowChangeListeners     // Catch:{ all -> 0x0035 }
            boolean r1 = r1.isEmpty()     // Catch:{ all -> 0x0035 }
            if (r1 == 0) goto L_0x0013
            monitor-exit(r0)     // Catch:{ all -> 0x0035 }
            resetPriorityAfterLockedSection()
            return
        L_0x0013:
            java.util.ArrayList<com.android.server.wm.WindowManagerService$WindowChangeListener> r1 = r4.mWindowChangeListeners     // Catch:{ all -> 0x0035 }
            int r1 = r1.size()     // Catch:{ all -> 0x0035 }
            com.android.server.wm.WindowManagerService$WindowChangeListener[] r1 = new com.android.server.wm.WindowManagerService.WindowChangeListener[r1]     // Catch:{ all -> 0x0035 }
            java.util.ArrayList<com.android.server.wm.WindowManagerService$WindowChangeListener> r2 = r4.mWindowChangeListeners     // Catch:{ all -> 0x0035 }
            java.lang.Object[] r2 = r2.toArray(r1)     // Catch:{ all -> 0x0035 }
            com.android.server.wm.WindowManagerService$WindowChangeListener[] r2 = (com.android.server.wm.WindowManagerService.WindowChangeListener[]) r2     // Catch:{ all -> 0x0035 }
            r1 = r2
            monitor-exit(r0)     // Catch:{ all -> 0x0035 }
            resetPriorityAfterLockedSection()
            int r0 = r1.length
            r2 = 0
        L_0x002a:
            if (r2 >= r0) goto L_0x0034
            r3 = r1[r2]
            r3.focusChanged()
            int r2 = r2 + 1
            goto L_0x002a
        L_0x0034:
            return
        L_0x0035:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0035 }
            resetPriorityAfterLockedSection()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.notifyFocusChanged():void");
    }

    private WindowState findWindow(int hashCode) {
        WindowState window;
        if (hashCode == -1) {
            return getFocusedWindow();
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                window = this.mRoot.getWindow(new Predicate(hashCode) {
                    private final /* synthetic */ int f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final boolean test(Object obj) {
                        return WindowManagerService.lambda$findWindow$5(this.f$0, (WindowState) obj);
                    }
                });
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
        return window;
    }

    static /* synthetic */ boolean lambda$findWindow$5(int hashCode, WindowState w) {
        return System.identityHashCode(w) == hashCode;
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: package-private */
    public void sendNewConfiguration(int displayId) {
        try {
            if (!this.mActivityTaskManager.updateDisplayOverrideConfiguration((Configuration) null, displayId)) {
                synchronized (this.mGlobalLock) {
                    boostPriorityForLockedSection();
                    DisplayContent dc = this.mRoot.getDisplayContent(displayId);
                    if (dc != null && dc.mWaitingForConfig) {
                        dc.mWaitingForConfig = false;
                        this.mLastFinishedFreezeSource = "config-unchanged";
                        dc.setLayoutNeeded();
                        this.mWindowPlacerLocked.performSurfacePlacement();
                    }
                }
                resetPriorityAfterLockedSection();
            }
        } catch (RemoteException e) {
        } catch (Throwable th) {
            while (true) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    public Configuration computeNewConfiguration(int displayId) {
        Configuration computeNewConfigurationLocked;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                computeNewConfigurationLocked = computeNewConfigurationLocked(displayId);
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
        return computeNewConfigurationLocked;
    }

    private Configuration computeNewConfigurationLocked(int displayId) {
        if (!this.mDisplayReady) {
            return null;
        }
        Configuration config = new Configuration();
        this.mRoot.getDisplayContent(displayId).computeScreenConfiguration(config);
        return config;
    }

    /* access modifiers changed from: package-private */
    public void notifyHardKeyboardStatusChange() {
        WindowManagerInternal.OnHardKeyboardStatusChangeListener listener;
        boolean available;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                listener = this.mHardKeyboardStatusChangeListener;
                available = this.mHardKeyboardAvailable;
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
        if (listener != null) {
            listener.onHardKeyboardStatusChange(available);
        }
    }

    public void setEventDispatching(boolean enabled) {
        if (checkCallingPermission("android.permission.MANAGE_APP_TOKENS", "setEventDispatching()")) {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    this.mEventDispatchingEnabled = enabled;
                    if (this.mDisplayEnabled) {
                        this.mInputManagerCallback.setEventDispatchingLw(enabled);
                    }
                } catch (Throwable th) {
                    while (true) {
                        resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            resetPriorityAfterLockedSection();
            return;
        }
        throw new SecurityException("Requires MANAGE_APP_TOKENS permission");
    }

    private WindowState getFocusedWindow() {
        WindowState focusedWindowLocked;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                focusedWindowLocked = getFocusedWindowLocked();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
        return focusedWindowLocked;
    }

    /* access modifiers changed from: private */
    public WindowState getFocusedWindowLocked() {
        return this.mRoot.getTopFocusedDisplayContent().mCurrentFocus;
    }

    /* access modifiers changed from: package-private */
    public TaskStack getImeFocusStackLocked() {
        AppWindowToken focusedApp = this.mRoot.getTopFocusedDisplayContent().mFocusedApp;
        if (focusedApp == null || focusedApp.getTask() == null) {
            return null;
        }
        return focusedApp.getTask().mStack;
    }

    public boolean detectSafeMode() {
        if (!this.mInputManagerCallback.waitForInputDevicesReady(1000)) {
            Slog.w("WindowManager", "Devices still not ready after waiting 1000 milliseconds before attempting to detect safe mode.");
        }
        if (Settings.Global.getInt(this.mContext.getContentResolver(), "safe_boot_disallowed", 0) != 0) {
            return false;
        }
        int menuState = this.mInputManager.getKeyCodeState(-1, -256, 82);
        int sState = this.mInputManager.getKeyCodeState(-1, -256, 47);
        int dpadState = this.mInputManager.getKeyCodeState(-1, UsbTerminalTypes.TERMINAL_IN_MIC, 23);
        int trackballState = this.mInputManager.getScanCodeState(-1, 65540, 272);
        this.mSafeMode = menuState > 0 || sState > 0 || dpadState > 0 || trackballState > 0 || this.mInputManager.getKeyCodeState(-1, -256, 25) > 0;
        try {
            if (!(SystemProperties.getInt(ShutdownThread.REBOOT_SAFEMODE_PROPERTY, 0) == 0 && SystemProperties.getInt(ShutdownThread.RO_SAFEMODE_PROPERTY, 0) == 0)) {
                this.mSafeMode = true;
                SystemProperties.set(ShutdownThread.REBOOT_SAFEMODE_PROPERTY, "");
            }
        } catch (IllegalArgumentException e) {
        }
        if (this.mSafeMode) {
            Log.i("WindowManager", "SAFE MODE ENABLED (menu=" + menuState + " s=" + sState + " dpad=" + dpadState + " trackball=" + trackballState + ")");
            if (SystemProperties.getInt(ShutdownThread.RO_SAFEMODE_PROPERTY, 0) == 0) {
                SystemProperties.set(ShutdownThread.RO_SAFEMODE_PROPERTY, SplitScreenReporter.ACTION_ENTER_SPLIT);
            }
        } else {
            Log.i("WindowManager", "SAFE MODE not enabled");
        }
        this.mPolicy.setSafeMode(this.mSafeMode);
        return this.mSafeMode;
    }

    public void displayReady() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                if (this.mMaxUiWidth > 0) {
                    this.mRoot.forAllDisplays(new Consumer() {
                        public final void accept(Object obj) {
                            WindowManagerService.this.lambda$displayReady$6$WindowManagerService((DisplayContent) obj);
                        }
                    });
                }
                boolean changed = applyForcedPropertiesForDefaultDisplay();
                this.mAnimator.ready();
                this.mDisplayReady = true;
                if (changed) {
                    reconfigureDisplayLocked(getDefaultDisplayContentLocked());
                }
                this.mIsTouchDevice = this.mContext.getPackageManager().hasSystemFeature("android.hardware.touchscreen");
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
        try {
            this.mActivityTaskManager.updateConfiguration((Configuration) null);
        } catch (RemoteException e) {
        }
        updateCircularDisplayMaskIfNeeded();
    }

    public /* synthetic */ void lambda$displayReady$6$WindowManagerService(DisplayContent displayContent) {
        displayContent.setMaxUiWidth(this.mMaxUiWidth);
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public void systemReady() {
        this.mSystemReady = true;
        this.mPolicy.systemReady();
        this.mRoot.forAllDisplayPolicies($$Lambda$cJEiQ28RvThCcuht9wXeFzPgo.INSTANCE);
        this.mTaskSnapshotController.systemReady();
        this.mHasWideColorGamutSupport = queryWideColorGamutSupport();
        this.mHasHdrSupport = queryHdrSupport();
        Handler handler = UiThread.getHandler();
        SettingsObserver settingsObserver = this.mSettingsObserver;
        Objects.requireNonNull(settingsObserver);
        handler.post(new Runnable() {
            public final void run() {
                WindowManagerService.SettingsObserver.this.updateSystemUiSettings();
            }
        });
        Handler handler2 = UiThread.getHandler();
        SettingsObserver settingsObserver2 = this.mSettingsObserver;
        Objects.requireNonNull(settingsObserver2);
        handler2.post(new Runnable() {
            public final void run() {
                WindowManagerService.SettingsObserver.this.updatePointerLocation();
            }
        });
        IVrManager vrManager = IVrManager.Stub.asInterface(ServiceManager.getService("vrmanager"));
        if (vrManager != null) {
            try {
                boolean vrModeEnabled = vrManager.getVrModeState();
                synchronized (this.mGlobalLock) {
                    boostPriorityForLockedSection();
                    vrManager.registerListener(this.mVrStateCallbacks);
                    if (vrModeEnabled) {
                        this.mVrModeEnabled = vrModeEnabled;
                        this.mVrStateCallbacks.onVrStateChanged(vrModeEnabled);
                    }
                }
                resetPriorityAfterLockedSection();
            } catch (RemoteException e) {
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        initMiuiGestureController();
        initMiuiFreeFormGestureController();
    }

    private static boolean queryWideColorGamutSupport() {
        try {
            OptionalBool hasWideColor = ISurfaceFlingerConfigs.getService().hasWideColorDisplay();
            if (hasWideColor != null) {
                return hasWideColor.value;
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    private static boolean queryHdrSupport() {
        try {
            OptionalBool hasHdr = ISurfaceFlingerConfigs.getService().hasHDRDisplay();
            if (hasHdr != null) {
                return hasHdr.value;
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    final class H extends Handler {
        public static final int ALL_WINDOWS_DRAWN = 33;
        public static final int ANIMATION_FAILSAFE = 60;
        public static final int APP_FREEZE_TIMEOUT = 17;
        public static final int APP_TRANSITION_SPECS_PENDING_TIMEOUT = 102;
        public static final int BOOT_TIMEOUT = 23;
        public static final int CHECK_IF_BOOT_ANIMATION_FINISHED = 37;
        public static final int CLIENT_FREEZE_TIMEOUT = 30;
        public static final int ENABLE_SCREEN = 16;
        public static final int FORCE_GC = 15;
        public static final int NEW_ANIMATOR_SCALE = 34;
        public static final int NOTIFY_ACTIVITY_DRAWN = 32;
        public static final int NOTIFY_DOCKED_STACK_APP_TRANSITION_STARTING = 100;
        public static final int ON_POINTER_DOWN_OUTSIDE_FOCUS = 62;
        public static final int PENDING_EXECUTE_APP_TRANSITION_TIMEOUT = 103;
        public static final int PERSIST_ANIMATION_SCALE = 14;
        public static final int RECOMPUTE_FOCUS = 61;
        public static final int REPORT_FOCUS_CHANGE = 2;
        public static final int REPORT_HARD_KEYBOARD_STATUS_CHANGE = 22;
        public static final int REPORT_LOSING_FOCUS = 3;
        public static final int REPORT_WINDOWS_CHANGE = 19;
        public static final int RESET_ANR_MESSAGE = 38;
        public static final int RESTORE_POINTER_ICON = 55;
        public static final int SEAMLESS_ROTATION_TIMEOUT = 54;
        public static final int SEND_NEW_CONFIGURATION = 18;
        public static final int SET_HAS_OVERLAY_UI = 58;
        public static final int SET_RUNNING_REMOTE_ANIMATION = 59;
        public static final int SHOW_CIRCULAR_DISPLAY_MASK = 35;
        public static final int SHOW_EMULATOR_DISPLAY_OVERLAY = 36;
        public static final int SHOW_STRICT_MODE_VIOLATION = 25;
        public static final int THUMBNAIL_ANIMATION_TIMEOUT = 101;
        public static final int UNUSED = 0;
        public static final int UPDATE_ANIMATION_SCALE = 51;
        public static final int UPDATE_DOCKED_STACK_DIVIDER = 41;
        public static final int WAITING_FOR_DRAWN_TIMEOUT = 24;
        public static final int WALLPAPER_DRAW_PENDING_TIMEOUT = 39;
        public static final int WINDOW_FREEZE_TIMEOUT = 11;
        public static final int WINDOW_HIDE_TIMEOUT = 52;
        public static final int WINDOW_REPLACEMENT_TIMEOUT = 46;

        H() {
        }

        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v51, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v56, resolved type: boolean} */
        /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v58, resolved type: boolean} */
        /* JADX WARNING: type inference failed for: r2v0 */
        /* JADX WARNING: type inference failed for: r2v44, types: [int] */
        /* JADX WARNING: type inference failed for: r2v52 */
        /* JADX WARNING: type inference failed for: r2v57 */
        /* JADX WARNING: type inference failed for: r2v59 */
        /* JADX WARNING: type inference failed for: r2v62 */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void handleMessage(android.os.Message r9) {
            /*
                r8 = this;
                int r0 = r9.what
                r1 = 2
                r2 = 0
                r3 = 1
                if (r0 == r1) goto L_0x050f
                r4 = 3
                if (r0 == r4) goto L_0x04d8
                r4 = 11
                if (r0 == r4) goto L_0x04bd
                r4 = 30
                if (r0 == r4) goto L_0x0494
                r4 = 41
                if (r0 == r4) goto L_0x0470
                r4 = 46
                if (r0 == r4) goto L_0x043a
                r4 = 51
                r5 = 0
                if (r0 == r4) goto L_0x03e2
                r4 = 52
                if (r0 == r4) goto L_0x03b5
                r4 = 54
                if (r0 == r4) goto L_0x039a
                r4 = 55
                if (r0 == r4) goto L_0x0377
                switch(r0) {
                    case 14: goto L_0x033c;
                    case 15: goto L_0x02f5;
                    case 16: goto L_0x02ee;
                    case 17: goto L_0x02b4;
                    case 18: goto L_0x029a;
                    case 19: goto L_0x0277;
                    default: goto L_0x002e;
                }
            L_0x002e:
                switch(r0) {
                    case 22: goto L_0x0270;
                    case 23: goto L_0x0269;
                    case 24: goto L_0x0225;
                    case 25: goto L_0x021a;
                    default: goto L_0x0031;
                }
            L_0x0031:
                switch(r0) {
                    case 32: goto L_0x020a;
                    case 33: goto L_0x01e9;
                    case 34: goto L_0x0186;
                    case 35: goto L_0x017a;
                    case 36: goto L_0x0173;
                    case 37: goto L_0x0152;
                    case 38: goto L_0x0133;
                    case 39: goto L_0x010c;
                    default: goto L_0x0034;
                }
            L_0x0034:
                switch(r0) {
                    case 58: goto L_0x00fc;
                    case 59: goto L_0x00ec;
                    case 60: goto L_0x00c7;
                    case 61: goto L_0x00ae;
                    case 62: goto L_0x0091;
                    default: goto L_0x0037;
                }
            L_0x0037:
                switch(r0) {
                    case 101: goto L_0x0076;
                    case 102: goto L_0x0066;
                    case 103: goto L_0x003c;
                    default: goto L_0x003a;
                }
            L_0x003a:
                goto L_0x0571
            L_0x003c:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
                monitor-enter(r0)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0060 }
                java.lang.String r1 = "WindowManager"
                java.lang.String r3 = "Pending execute app transition overtime."
                android.util.Slog.d(r1, r3)     // Catch:{ all -> 0x0060 }
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x0060 }
                boolean r1 = r1.mPendingExecuteAppTransition     // Catch:{ all -> 0x0060 }
                if (r1 == 0) goto L_0x005a
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x0060 }
                r1.mPendingExecuteAppTransition = r2     // Catch:{ all -> 0x0060 }
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x0060 }
                r1.executeAppTransition()     // Catch:{ all -> 0x0060 }
            L_0x005a:
                monitor-exit(r0)     // Catch:{ all -> 0x0060 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                goto L_0x0571
            L_0x0060:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0060 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r1
            L_0x0066:
                java.lang.String r0 = "WindowManager"
                java.lang.String r1 = "Fetching specs wasn't finished in time."
                android.util.Slog.d(r0, r1)
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                int r1 = r9.arg1
                r0.finishFetchingAppTransitionSpecs(r1)
                goto L_0x0571
            L_0x0076:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
                monitor-enter(r0)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x008b }
                java.lang.String r1 = "WindowManager"
                java.lang.String r2 = "Thumbnail animation wasn't finished in time."
                android.util.Slog.d(r1, r2)     // Catch:{ all -> 0x008b }
                monitor-exit(r0)     // Catch:{ all -> 0x008b }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                goto L_0x0571
            L_0x008b:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x008b }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r1
            L_0x0091:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
                monitor-enter(r0)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x00a8 }
                java.lang.Object r1 = r9.obj     // Catch:{ all -> 0x00a8 }
                android.os.IBinder r1 = (android.os.IBinder) r1     // Catch:{ all -> 0x00a8 }
                com.android.server.wm.WindowManagerService r2 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x00a8 }
                r2.onPointerDownOutsideFocusLocked(r1)     // Catch:{ all -> 0x00a8 }
                monitor-exit(r0)     // Catch:{ all -> 0x00a8 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                goto L_0x0571
            L_0x00a8:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x00a8 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r1
            L_0x00ae:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
                monitor-enter(r0)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x00c1 }
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x00c1 }
                r1.updateFocusedWindowLocked(r2, r3)     // Catch:{ all -> 0x00c1 }
                monitor-exit(r0)     // Catch:{ all -> 0x00c1 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                goto L_0x0571
            L_0x00c1:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x00c1 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r1
            L_0x00c7:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
                monitor-enter(r0)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x00e6 }
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x00e6 }
                com.android.server.wm.RecentsAnimationController r1 = r1.mRecentsAnimationController     // Catch:{ all -> 0x00e6 }
                if (r1 == 0) goto L_0x00e0
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x00e6 }
                com.android.server.wm.RecentsAnimationController r1 = r1.mRecentsAnimationController     // Catch:{ all -> 0x00e6 }
                r1.scheduleFailsafe()     // Catch:{ all -> 0x00e6 }
            L_0x00e0:
                monitor-exit(r0)     // Catch:{ all -> 0x00e6 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                goto L_0x0571
            L_0x00e6:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x00e6 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r1
            L_0x00ec:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                android.app.ActivityManagerInternal r0 = r0.mAmInternal
                int r1 = r9.arg1
                int r4 = r9.arg2
                if (r4 != r3) goto L_0x00f7
                r2 = r3
            L_0x00f7:
                r0.setRunningRemoteAnimation(r1, r2)
                goto L_0x0571
            L_0x00fc:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                android.app.ActivityManagerInternal r0 = r0.mAmInternal
                int r1 = r9.arg1
                int r4 = r9.arg2
                if (r4 != r3) goto L_0x0107
                r2 = r3
            L_0x0107:
                r0.setHasOverlayUi(r1, r2)
                goto L_0x0571
            L_0x010c:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
                monitor-enter(r0)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x012d }
                java.lang.Object r1 = r9.obj     // Catch:{ all -> 0x012d }
                com.android.server.wm.WallpaperController r1 = (com.android.server.wm.WallpaperController) r1     // Catch:{ all -> 0x012d }
                if (r1 == 0) goto L_0x0127
                boolean r2 = r1.processWallpaperDrawPendingTimeout()     // Catch:{ all -> 0x012d }
                if (r2 == 0) goto L_0x0127
                com.android.server.wm.WindowManagerService r2 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x012d }
                com.android.server.wm.WindowSurfacePlacer r2 = r2.mWindowPlacerLocked     // Catch:{ all -> 0x012d }
                r2.performSurfacePlacement()     // Catch:{ all -> 0x012d }
            L_0x0127:
                monitor-exit(r0)     // Catch:{ all -> 0x012d }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                goto L_0x0571
            L_0x012d:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x012d }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r1
            L_0x0133:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
                monitor-enter(r0)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x014c }
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x014c }
                r1.mLastANRState = r5     // Catch:{ all -> 0x014c }
                monitor-exit(r0)     // Catch:{ all -> 0x014c }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                com.android.server.wm.ActivityTaskManagerInternal r0 = r0.mAtmInternal
                r0.clearSavedANRState()
                goto L_0x0571
            L_0x014c:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x014c }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r1
            L_0x0152:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
                monitor-enter(r0)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x016d }
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x016d }
                boolean r1 = r1.checkBootAnimationCompleteLocked()     // Catch:{ all -> 0x016d }
                monitor-exit(r0)     // Catch:{ all -> 0x016d }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                if (r1 == 0) goto L_0x0571
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                r0.performEnableScreen()
                goto L_0x0571
            L_0x016d:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x016d }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r1
            L_0x0173:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                r0.showEmulatorDisplayOverlay()
                goto L_0x0571
            L_0x017a:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                int r1 = r9.arg1
                if (r1 != r3) goto L_0x0181
                r2 = r3
            L_0x0181:
                r0.showCircularMask(r2)
                goto L_0x0571
            L_0x0186:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                float r0 = r0.getCurrentAnimatorScale()
                android.animation.ValueAnimator.setDurationScale(r0)
                java.lang.Object r1 = r9.obj
                com.android.server.wm.Session r1 = (com.android.server.wm.Session) r1
                if (r1 == 0) goto L_0x019e
                android.view.IWindowSessionCallback r2 = r1.mCallback     // Catch:{ RemoteException -> 0x019b }
                r2.onAnimatorScaleChanged(r0)     // Catch:{ RemoteException -> 0x019b }
                goto L_0x019c
            L_0x019b:
                r2 = move-exception
            L_0x019c:
                goto L_0x0571
            L_0x019e:
                java.util.ArrayList r3 = new java.util.ArrayList
                r3.<init>()
                com.android.server.wm.WindowManagerService r4 = com.android.server.wm.WindowManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r4 = r4.mGlobalLock
                monitor-enter(r4)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x01e3 }
            L_0x01ac:
                com.android.server.wm.WindowManagerService r5 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x01e3 }
                android.util.ArraySet<com.android.server.wm.Session> r5 = r5.mSessions     // Catch:{ all -> 0x01e3 }
                int r5 = r5.size()     // Catch:{ all -> 0x01e3 }
                if (r2 >= r5) goto L_0x01c8
                com.android.server.wm.WindowManagerService r5 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x01e3 }
                android.util.ArraySet<com.android.server.wm.Session> r5 = r5.mSessions     // Catch:{ all -> 0x01e3 }
                java.lang.Object r5 = r5.valueAt(r2)     // Catch:{ all -> 0x01e3 }
                com.android.server.wm.Session r5 = (com.android.server.wm.Session) r5     // Catch:{ all -> 0x01e3 }
                android.view.IWindowSessionCallback r5 = r5.mCallback     // Catch:{ all -> 0x01e3 }
                r3.add(r5)     // Catch:{ all -> 0x01e3 }
                int r2 = r2 + 1
                goto L_0x01ac
            L_0x01c8:
                monitor-exit(r4)     // Catch:{ all -> 0x01e3 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                r2 = 0
            L_0x01cd:
                int r4 = r3.size()
                if (r2 >= r4) goto L_0x01e1
                java.lang.Object r4 = r3.get(r2)     // Catch:{ RemoteException -> 0x01dd }
                android.view.IWindowSessionCallback r4 = (android.view.IWindowSessionCallback) r4     // Catch:{ RemoteException -> 0x01dd }
                r4.onAnimatorScaleChanged(r0)     // Catch:{ RemoteException -> 0x01dd }
                goto L_0x01de
            L_0x01dd:
                r4 = move-exception
            L_0x01de:
                int r2 = r2 + 1
                goto L_0x01cd
            L_0x01e1:
                goto L_0x0571
            L_0x01e3:
                r2 = move-exception
                monitor-exit(r4)     // Catch:{ all -> 0x01e3 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r2
            L_0x01e9:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
                monitor-enter(r0)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0204 }
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x0204 }
                java.lang.Runnable r1 = r1.mWaitingForDrawnCallback     // Catch:{ all -> 0x0204 }
                com.android.server.wm.WindowManagerService r2 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x0204 }
                r2.mWaitingForDrawnCallback = r5     // Catch:{ all -> 0x0204 }
                monitor-exit(r0)     // Catch:{ all -> 0x0204 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                if (r1 == 0) goto L_0x0571
                r1.run()
                goto L_0x0571
            L_0x0204:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0204 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r1
            L_0x020a:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this     // Catch:{ RemoteException -> 0x0217 }
                android.app.IActivityTaskManager r0 = r0.mActivityTaskManager     // Catch:{ RemoteException -> 0x0217 }
                java.lang.Object r1 = r9.obj     // Catch:{ RemoteException -> 0x0217 }
                android.os.IBinder r1 = (android.os.IBinder) r1     // Catch:{ RemoteException -> 0x0217 }
                r0.notifyActivityDrawn(r1)     // Catch:{ RemoteException -> 0x0217 }
                goto L_0x0571
            L_0x0217:
                r0 = move-exception
                goto L_0x0571
            L_0x021a:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                int r1 = r9.arg1
                int r2 = r9.arg2
                r0.showStrictModeViolation(r1, r2)
                goto L_0x0571
            L_0x0225:
                r0 = 0
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r1 = r1.mGlobalLock
                monitor-enter(r1)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0263 }
                java.lang.String r2 = "WindowManager"
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0263 }
                r3.<init>()     // Catch:{ all -> 0x0263 }
                java.lang.String r4 = "Timeout waiting for drawn: undrawn="
                r3.append(r4)     // Catch:{ all -> 0x0263 }
                com.android.server.wm.WindowManagerService r4 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x0263 }
                java.util.ArrayList<com.android.server.wm.WindowState> r4 = r4.mWaitingForDrawn     // Catch:{ all -> 0x0263 }
                r3.append(r4)     // Catch:{ all -> 0x0263 }
                java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0263 }
                android.util.Slog.w(r2, r3)     // Catch:{ all -> 0x0263 }
                com.android.server.wm.WindowManagerService r2 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x0263 }
                java.util.ArrayList<com.android.server.wm.WindowState> r2 = r2.mWaitingForDrawn     // Catch:{ all -> 0x0263 }
                r2.clear()     // Catch:{ all -> 0x0263 }
                com.android.server.wm.WindowManagerService r2 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x0263 }
                java.lang.Runnable r2 = r2.mWaitingForDrawnCallback     // Catch:{ all -> 0x0263 }
                r0 = r2
                com.android.server.wm.WindowManagerService r2 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x0263 }
                r2.mWaitingForDrawnCallback = r5     // Catch:{ all -> 0x0263 }
                monitor-exit(r1)     // Catch:{ all -> 0x0263 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                if (r0 == 0) goto L_0x0571
                r0.run()
                goto L_0x0571
            L_0x0263:
                r2 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x0263 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r2
            L_0x0269:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                r0.performBootTimeout()
                goto L_0x0571
            L_0x0270:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                r0.notifyHardKeyboardStatusChange()
                goto L_0x0571
            L_0x0277:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                boolean r0 = r0.mWindowsChanged
                if (r0 == 0) goto L_0x0571
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
                monitor-enter(r0)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0294 }
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x0294 }
                r1.mWindowsChanged = r2     // Catch:{ all -> 0x0294 }
                monitor-exit(r0)     // Catch:{ all -> 0x0294 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                r0.notifyWindowsChanged()
                goto L_0x0571
            L_0x0294:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0294 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r1
            L_0x029a:
                java.lang.Object r0 = r9.obj
                com.android.server.wm.DisplayContent r0 = (com.android.server.wm.DisplayContent) r0
                r1 = 18
                r8.removeMessages(r1, r0)
                boolean r1 = r0.isReady()
                if (r1 == 0) goto L_0x0571
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this
                int r2 = r0.getDisplayId()
                r1.sendNewConfiguration(r2)
                goto L_0x0571
            L_0x02b4:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
                monitor-enter(r0)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x02e8 }
                java.lang.String r2 = "WindowManager"
                java.lang.String r4 = "App freeze timeout expired."
                android.util.Slog.w(r2, r4)     // Catch:{ all -> 0x02e8 }
                com.android.server.wm.WindowManagerService r2 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x02e8 }
                r2.mWindowsFreezingScreen = r1     // Catch:{ all -> 0x02e8 }
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x02e8 }
                java.util.ArrayList<com.android.server.wm.WindowManagerService$AppFreezeListener> r1 = r1.mAppFreezeListeners     // Catch:{ all -> 0x02e8 }
                int r1 = r1.size()     // Catch:{ all -> 0x02e8 }
                int r1 = r1 - r3
            L_0x02d0:
                if (r1 < 0) goto L_0x02e2
                com.android.server.wm.WindowManagerService r2 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x02e8 }
                java.util.ArrayList<com.android.server.wm.WindowManagerService$AppFreezeListener> r2 = r2.mAppFreezeListeners     // Catch:{ all -> 0x02e8 }
                java.lang.Object r2 = r2.get(r1)     // Catch:{ all -> 0x02e8 }
                com.android.server.wm.WindowManagerService$AppFreezeListener r2 = (com.android.server.wm.WindowManagerService.AppFreezeListener) r2     // Catch:{ all -> 0x02e8 }
                r2.onAppFreezeTimeout()     // Catch:{ all -> 0x02e8 }
                int r1 = r1 + -1
                goto L_0x02d0
            L_0x02e2:
                monitor-exit(r0)     // Catch:{ all -> 0x02e8 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                goto L_0x0571
            L_0x02e8:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x02e8 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r1
            L_0x02ee:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                r0.performEnableScreen()
                goto L_0x0571
            L_0x02f5:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
                monitor-enter(r0)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0336 }
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x0336 }
                com.android.server.wm.WindowAnimator r1 = r1.mAnimator     // Catch:{ all -> 0x0336 }
                boolean r1 = r1.isAnimating()     // Catch:{ all -> 0x0336 }
                if (r1 != 0) goto L_0x032a
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x0336 }
                com.android.server.wm.WindowAnimator r1 = r1.mAnimator     // Catch:{ all -> 0x0336 }
                boolean r1 = r1.isAnimationScheduled()     // Catch:{ all -> 0x0336 }
                if (r1 == 0) goto L_0x0312
                goto L_0x032a
            L_0x0312:
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x0336 }
                boolean r1 = r1.mDisplayFrozen     // Catch:{ all -> 0x0336 }
                if (r1 == 0) goto L_0x031d
                monitor-exit(r0)     // Catch:{ all -> 0x0336 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                return
            L_0x031d:
                monitor-exit(r0)     // Catch:{ all -> 0x0336 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                java.lang.Runtime r0 = java.lang.Runtime.getRuntime()
                r0.gc()
                goto L_0x0571
            L_0x032a:
                r1 = 15
                r2 = 2000(0x7d0, double:9.88E-321)
                r8.sendEmptyMessageDelayed(r1, r2)     // Catch:{ all -> 0x0336 }
                monitor-exit(r0)     // Catch:{ all -> 0x0336 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                return
            L_0x0336:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0336 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r1
            L_0x033c:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                android.content.Context r0 = r0.mContext
                android.content.ContentResolver r0 = r0.getContentResolver()
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this
                float r1 = r1.mWindowAnimationScaleSetting
                java.lang.String r2 = "window_animation_scale"
                android.provider.Settings.Global.putFloat(r0, r2, r1)
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                android.content.Context r0 = r0.mContext
                android.content.ContentResolver r0 = r0.getContentResolver()
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this
                float r1 = r1.mTransitionAnimationScaleSetting
                java.lang.String r2 = "transition_animation_scale"
                android.provider.Settings.Global.putFloat(r0, r2, r1)
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                android.content.Context r0 = r0.mContext
                android.content.ContentResolver r0 = r0.getContentResolver()
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this
                float r1 = r1.mAnimatorDurationScaleSetting
                java.lang.String r2 = "animator_duration_scale"
                android.provider.Settings.Global.putFloat(r0, r2, r1)
                goto L_0x0571
            L_0x0377:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
                monitor-enter(r0)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0394 }
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x0394 }
                java.lang.Object r2 = r9.obj     // Catch:{ all -> 0x0394 }
                com.android.server.wm.DisplayContent r2 = (com.android.server.wm.DisplayContent) r2     // Catch:{ all -> 0x0394 }
                int r3 = r9.arg1     // Catch:{ all -> 0x0394 }
                float r3 = (float) r3     // Catch:{ all -> 0x0394 }
                int r4 = r9.arg2     // Catch:{ all -> 0x0394 }
                float r4 = (float) r4     // Catch:{ all -> 0x0394 }
                r1.restorePointerIconLocked(r2, r3, r4)     // Catch:{ all -> 0x0394 }
                monitor-exit(r0)     // Catch:{ all -> 0x0394 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                goto L_0x0571
            L_0x0394:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0394 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r1
            L_0x039a:
                java.lang.Object r0 = r9.obj
                com.android.server.wm.DisplayContent r0 = (com.android.server.wm.DisplayContent) r0
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r1 = r1.mGlobalLock
                monitor-enter(r1)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x03af }
                r0.onSeamlessRotationTimeout()     // Catch:{ all -> 0x03af }
                monitor-exit(r1)     // Catch:{ all -> 0x03af }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                goto L_0x0571
            L_0x03af:
                r2 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x03af }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r2
            L_0x03b5:
                java.lang.Object r0 = r9.obj
                com.android.server.wm.WindowState r0 = (com.android.server.wm.WindowState) r0
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r2 = r1.mGlobalLock
                monitor-enter(r2)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x03dc }
                android.view.WindowManager$LayoutParams r1 = r0.mAttrs     // Catch:{ all -> 0x03dc }
                int r3 = r1.flags     // Catch:{ all -> 0x03dc }
                r3 = r3 & -129(0xffffffffffffff7f, float:NaN)
                r1.flags = r3     // Catch:{ all -> 0x03dc }
                r0.hidePermanentlyLw()     // Catch:{ all -> 0x03dc }
                r0.setDisplayLayoutNeeded()     // Catch:{ all -> 0x03dc }
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x03dc }
                com.android.server.wm.WindowSurfacePlacer r1 = r1.mWindowPlacerLocked     // Catch:{ all -> 0x03dc }
                r1.performSurfacePlacement()     // Catch:{ all -> 0x03dc }
                monitor-exit(r2)     // Catch:{ all -> 0x03dc }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                goto L_0x0571
            L_0x03dc:
                r1 = move-exception
                monitor-exit(r2)     // Catch:{ all -> 0x03dc }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r1
            L_0x03e2:
                int r0 = r9.arg1
                if (r0 == 0) goto L_0x0420
                if (r0 == r3) goto L_0x0408
                if (r0 == r1) goto L_0x03eb
                goto L_0x0438
            L_0x03eb:
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this
                android.content.Context r2 = r1.mContext
                android.content.ContentResolver r2 = r2.getContentResolver()
                com.android.server.wm.WindowManagerService r3 = com.android.server.wm.WindowManagerService.this
                float r3 = r3.mAnimatorDurationScaleSetting
                java.lang.String r4 = "animator_duration_scale"
                float r2 = android.provider.Settings.Global.getFloat(r2, r4, r3)
                float unused = r1.mAnimatorDurationScaleSetting = r2
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this
                r1.dispatchNewAnimatorScaleLocked(r5)
                goto L_0x0438
            L_0x0408:
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this
                android.content.Context r2 = r1.mContext
                android.content.ContentResolver r2 = r2.getContentResolver()
                com.android.server.wm.WindowManagerService r3 = com.android.server.wm.WindowManagerService.this
                float r3 = r3.mTransitionAnimationScaleSetting
                java.lang.String r4 = "transition_animation_scale"
                float r2 = android.provider.Settings.Global.getFloat(r2, r4, r3)
                float unused = r1.mTransitionAnimationScaleSetting = r2
                goto L_0x0438
            L_0x0420:
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this
                android.content.Context r2 = r1.mContext
                android.content.ContentResolver r2 = r2.getContentResolver()
                com.android.server.wm.WindowManagerService r3 = com.android.server.wm.WindowManagerService.this
                float r3 = r3.mWindowAnimationScaleSetting
                java.lang.String r4 = "window_animation_scale"
                float r2 = android.provider.Settings.Global.getFloat(r2, r4, r3)
                float unused = r1.mWindowAnimationScaleSetting = r2
            L_0x0438:
                goto L_0x0571
            L_0x043a:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
                monitor-enter(r0)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x046a }
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x046a }
                java.util.ArrayList<com.android.server.wm.AppWindowToken> r1 = r1.mWindowReplacementTimeouts     // Catch:{ all -> 0x046a }
                int r1 = r1.size()     // Catch:{ all -> 0x046a }
                int r1 = r1 - r3
            L_0x044b:
                if (r1 < 0) goto L_0x045d
                com.android.server.wm.WindowManagerService r2 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x046a }
                java.util.ArrayList<com.android.server.wm.AppWindowToken> r2 = r2.mWindowReplacementTimeouts     // Catch:{ all -> 0x046a }
                java.lang.Object r2 = r2.get(r1)     // Catch:{ all -> 0x046a }
                com.android.server.wm.AppWindowToken r2 = (com.android.server.wm.AppWindowToken) r2     // Catch:{ all -> 0x046a }
                r2.onWindowReplacementTimeout()     // Catch:{ all -> 0x046a }
                int r1 = r1 + -1
                goto L_0x044b
            L_0x045d:
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x046a }
                java.util.ArrayList<com.android.server.wm.AppWindowToken> r1 = r1.mWindowReplacementTimeouts     // Catch:{ all -> 0x046a }
                r1.clear()     // Catch:{ all -> 0x046a }
                monitor-exit(r0)     // Catch:{ all -> 0x046a }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                goto L_0x0571
            L_0x046a:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x046a }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r1
            L_0x0470:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
                monitor-enter(r0)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x048e }
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x048e }
                com.android.server.wm.DisplayContent r1 = r1.getDefaultDisplayContentLocked()     // Catch:{ all -> 0x048e }
                com.android.server.wm.DockedStackDividerController r3 = r1.getDockedDividerController()     // Catch:{ all -> 0x048e }
                r3.reevaluateVisibility(r2)     // Catch:{ all -> 0x048e }
                r1.adjustForImeIfNeeded()     // Catch:{ all -> 0x048e }
                monitor-exit(r0)     // Catch:{ all -> 0x048e }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                goto L_0x0571
            L_0x048e:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x048e }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r1
            L_0x0494:
                com.android.server.wm.WindowManagerService r0 = com.android.server.wm.WindowManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
                monitor-enter(r0)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x04b7 }
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x04b7 }
                boolean r1 = r1.mClientFreezingScreen     // Catch:{ all -> 0x04b7 }
                if (r1 == 0) goto L_0x04b1
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x04b7 }
                r1.mClientFreezingScreen = r2     // Catch:{ all -> 0x04b7 }
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x04b7 }
                java.lang.String r2 = "client-timeout"
                r1.mLastFinishedFreezeSource = r2     // Catch:{ all -> 0x04b7 }
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x04b7 }
                r1.stopFreezingDisplayLocked()     // Catch:{ all -> 0x04b7 }
            L_0x04b1:
                monitor-exit(r0)     // Catch:{ all -> 0x04b7 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                goto L_0x0571
            L_0x04b7:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x04b7 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r1
            L_0x04bd:
                java.lang.Object r0 = r9.obj
                com.android.server.wm.DisplayContent r0 = (com.android.server.wm.DisplayContent) r0
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r1 = r1.mGlobalLock
                monitor-enter(r1)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x04d2 }
                r0.onWindowFreezeTimeout()     // Catch:{ all -> 0x04d2 }
                monitor-exit(r1)     // Catch:{ all -> 0x04d2 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                goto L_0x0571
            L_0x04d2:
                r2 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x04d2 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r2
            L_0x04d8:
                java.lang.Object r0 = r9.obj
                com.android.server.wm.DisplayContent r0 = (com.android.server.wm.DisplayContent) r0
                com.android.server.wm.WindowManagerService r1 = com.android.server.wm.WindowManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r1 = r1.mGlobalLock
                monitor-enter(r1)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0509 }
                java.util.ArrayList<com.android.server.wm.WindowState> r3 = r0.mLosingFocus     // Catch:{ all -> 0x0509 }
                java.util.ArrayList r4 = new java.util.ArrayList     // Catch:{ all -> 0x0509 }
                r4.<init>()     // Catch:{ all -> 0x0509 }
                r0.mLosingFocus = r4     // Catch:{ all -> 0x0509 }
                monitor-exit(r1)     // Catch:{ all -> 0x0509 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                int r1 = r3.size()
                r4 = 0
            L_0x04f6:
                if (r4 >= r1) goto L_0x0508
                java.lang.Object r5 = r3.get(r4)
                com.android.server.wm.WindowState r5 = (com.android.server.wm.WindowState) r5
                com.android.server.wm.WindowManagerService r6 = com.android.server.wm.WindowManagerService.this
                boolean r6 = r6.mInTouchMode
                r5.reportFocusChangedSerialized(r2, r6)
                int r4 = r4 + 1
                goto L_0x04f6
            L_0x0508:
                goto L_0x0571
            L_0x0509:
                r2 = move-exception
                monitor-exit(r1)     // Catch:{ all -> 0x0509 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r2
            L_0x050f:
                java.lang.Object r0 = r9.obj
                com.android.server.wm.DisplayContent r0 = (com.android.server.wm.DisplayContent) r0
                r1 = 0
                com.android.server.wm.WindowManagerService r4 = com.android.server.wm.WindowManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r4 = r4.mGlobalLock
                monitor-enter(r4)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0578 }
                com.android.server.wm.WindowManagerService r5 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x0578 }
                com.android.server.wm.AccessibilityController r5 = r5.mAccessibilityController     // Catch:{ all -> 0x0578 }
                if (r5 == 0) goto L_0x052b
                boolean r5 = r0.isDefaultDisplay     // Catch:{ all -> 0x0578 }
                if (r5 == 0) goto L_0x052b
                com.android.server.wm.WindowManagerService r5 = com.android.server.wm.WindowManagerService.this     // Catch:{ all -> 0x0578 }
                com.android.server.wm.AccessibilityController r5 = r5.mAccessibilityController     // Catch:{ all -> 0x0578 }
                r1 = r5
            L_0x052b:
                com.android.server.wm.WindowState r5 = r0.mLastFocus     // Catch:{ all -> 0x0578 }
                com.android.server.wm.WindowState r6 = r0.mCurrentFocus     // Catch:{ all -> 0x0578 }
                monitor-exit(r4)     // Catch:{ all -> 0x0578 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                if (r5 != r6) goto L_0x0536
                return
            L_0x0536:
                com.android.server.wm.WindowManagerService r4 = com.android.server.wm.WindowManagerService.this
                com.android.server.wm.WindowManagerGlobalLock r7 = r4.mGlobalLock
                monitor-enter(r7)
                com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0572 }
                r0.mLastFocus = r6     // Catch:{ all -> 0x0572 }
                if (r6 == 0) goto L_0x0551
                if (r5 == 0) goto L_0x0551
                boolean r4 = r6.isDisplayedLw()     // Catch:{ all -> 0x0572 }
                if (r4 != 0) goto L_0x0551
                java.util.ArrayList<com.android.server.wm.WindowState> r4 = r0.mLosingFocus     // Catch:{ all -> 0x0572 }
                r4.add(r5)     // Catch:{ all -> 0x0572 }
                r4 = 0
                r5 = r4
            L_0x0551:
                monitor-exit(r7)     // Catch:{ all -> 0x0572 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                if (r1 == 0) goto L_0x055a
                r1.onWindowFocusChangedNotLocked()
            L_0x055a:
                if (r6 == 0) goto L_0x0568
                com.android.server.wm.WindowManagerService r4 = com.android.server.wm.WindowManagerService.this
                boolean r4 = r4.mInTouchMode
                r6.reportFocusChangedSerialized(r3, r4)
                com.android.server.wm.WindowManagerService r3 = com.android.server.wm.WindowManagerService.this
                r3.notifyFocusChanged()
            L_0x0568:
                if (r5 == 0) goto L_0x0571
                com.android.server.wm.WindowManagerService r3 = com.android.server.wm.WindowManagerService.this
                boolean r3 = r3.mInTouchMode
                r5.reportFocusChangedSerialized(r2, r3)
            L_0x0571:
                return
            L_0x0572:
                r2 = move-exception
                monitor-exit(r7)     // Catch:{ all -> 0x0572 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r2
            L_0x0578:
                r2 = move-exception
                monitor-exit(r4)     // Catch:{ all -> 0x0578 }
                com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.H.handleMessage(android.os.Message):void");
        }

        /* access modifiers changed from: package-private */
        public void sendNewMessageDelayed(int what, Object obj, long delayMillis) {
            removeMessages(what, obj);
            sendMessageDelayed(obtainMessage(what, obj), delayMillis);
        }
    }

    /* access modifiers changed from: package-private */
    public void destroyPreservedSurfaceLocked() {
        for (int i = this.mDestroyPreservedSurface.size() - 1; i >= 0; i--) {
            this.mDestroyPreservedSurface.get(i).mWinAnimator.destroyPreservedSurfaceLocked();
        }
        this.mDestroyPreservedSurface.clear();
    }

    public IWindowSession openSession(IWindowSessionCallback callback) {
        return new Session(this, callback);
    }

    public void getInitialDisplaySize(int displayId, Point size) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                if (displayContent != null && displayContent.hasAccess(Binder.getCallingUid())) {
                    size.x = displayContent.mInitialDisplayWidth;
                    size.y = displayContent.mInitialDisplayHeight;
                }
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void getBaseDisplaySize(int displayId, Point size) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                if (displayContent != null && displayContent.hasAccess(Binder.getCallingUid())) {
                    size.x = displayContent.mBaseDisplayWidth;
                    size.y = displayContent.mBaseDisplayHeight;
                }
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public void setForcedDisplaySize(int displayId, int width, int height) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") == 0) {
            long ident = Binder.clearCallingIdentity();
            try {
                synchronized (this.mGlobalLock) {
                    boostPriorityForLockedSection();
                    DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                    if (displayContent != null) {
                        displayContent.setForcedSize(width, height);
                    }
                }
                resetPriorityAfterLockedSection();
                Binder.restoreCallingIdentity(ident);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(ident);
                throw th;
            }
        } else {
            throw new SecurityException("Must hold permission android.permission.WRITE_SECURE_SETTINGS");
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    public void setForcedDisplayScalingMode(int displayId, int mode) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") == 0) {
            long ident = Binder.clearCallingIdentity();
            try {
                synchronized (this.mGlobalLock) {
                    boostPriorityForLockedSection();
                    DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                    if (displayContent != null) {
                        displayContent.setForcedScalingMode(mode);
                    }
                }
                resetPriorityAfterLockedSection();
                Binder.restoreCallingIdentity(ident);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(ident);
                throw th;
            }
        } else {
            throw new SecurityException("Must hold permission android.permission.WRITE_SECURE_SETTINGS");
        }
    }

    private boolean applyForcedPropertiesForDefaultDisplay() {
        int pos;
        boolean changed = false;
        DisplayContent displayContent = getDefaultDisplayContentLocked();
        String sizeStr = Settings.Global.getString(this.mContext.getContentResolver(), "display_size_forced");
        if (sizeStr == null || sizeStr.length() == 0) {
            sizeStr = SystemProperties.get(SIZE_OVERRIDE, (String) null);
        }
        boolean z = false;
        if (sizeStr != null && sizeStr.length() > 0 && (pos = sizeStr.indexOf(44)) > 0 && sizeStr.lastIndexOf(44) == pos) {
            try {
                int width = Integer.parseInt(sizeStr.substring(0, pos));
                int height = Integer.parseInt(sizeStr.substring(pos + 1));
                if (!(displayContent.mBaseDisplayWidth == width && displayContent.mBaseDisplayHeight == height)) {
                    Slog.i("WindowManager", "FORCED DISPLAY SIZE: " + width + "x" + height);
                    displayContent.updateBaseDisplayMetrics(width, height, displayContent.mBaseDisplayDensity);
                    changed = true;
                }
            } catch (NumberFormatException e) {
            }
        }
        int density = getForcedDisplayDensityForUserLocked(this.mCurrentUserId);
        if (!(density == 0 || density == displayContent.mBaseDisplayDensity)) {
            displayContent.mBaseDisplayDensity = density;
            changed = true;
        }
        int mode = Settings.Global.getInt(this.mContext.getContentResolver(), "display_scaling_force", 0);
        boolean z2 = displayContent.mDisplayScalingDisabled;
        if (mode != 0) {
            z = true;
        }
        if (z2 == z) {
            return changed;
        }
        Slog.i("WindowManager", "FORCED DISPLAY SCALING DISABLED");
        displayContent.mDisplayScalingDisabled = true;
        return true;
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public void clearForcedDisplaySize(int displayId) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") == 0) {
            long ident = Binder.clearCallingIdentity();
            try {
                synchronized (this.mGlobalLock) {
                    boostPriorityForLockedSection();
                    DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                    if (displayContent != null) {
                        displayContent.setForcedSize(displayContent.mInitialDisplayWidth, displayContent.mInitialDisplayHeight);
                    }
                }
                resetPriorityAfterLockedSection();
                Binder.restoreCallingIdentity(ident);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(ident);
                throw th;
            }
        } else {
            throw new SecurityException("Must hold permission android.permission.WRITE_SECURE_SETTINGS");
        }
    }

    public int getInitialDisplayDensity(int displayId) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                if (displayContent == null || !displayContent.hasAccess(Binder.getCallingUid())) {
                    resetPriorityAfterLockedSection();
                    return -1;
                }
                int i = displayContent.mInitialDisplayDensity;
                resetPriorityAfterLockedSection();
                return i;
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public int getBaseDisplayDensity(int displayId) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                if (displayContent == null || !displayContent.hasAccess(Binder.getCallingUid())) {
                    resetPriorityAfterLockedSection();
                    return -1;
                }
                int i = displayContent.mBaseDisplayDensity;
                resetPriorityAfterLockedSection();
                return i;
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    public void setForcedDisplayDensityForUser(int displayId, int density, int userId) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") == 0) {
            int targetUserId = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId, false, true, "setForcedDisplayDensityForUser", (String) null);
            long ident = Binder.clearCallingIdentity();
            try {
                synchronized (this.mGlobalLock) {
                    boostPriorityForLockedSection();
                    DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                    if (displayContent != null) {
                        displayContent.setForcedDensity(density, targetUserId);
                    }
                }
                resetPriorityAfterLockedSection();
                Binder.restoreCallingIdentity(ident);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(ident);
                throw th;
            }
        } else {
            throw new SecurityException("Must hold permission android.permission.WRITE_SECURE_SETTINGS");
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    public void clearForcedDisplayDensityForUser(int displayId, int userId) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") == 0) {
            int callingUserId = ActivityManager.handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId, false, true, "clearForcedDisplayDensityForUser", (String) null);
            long ident = Binder.clearCallingIdentity();
            try {
                synchronized (this.mGlobalLock) {
                    boostPriorityForLockedSection();
                    DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                    if (displayContent != null) {
                        displayContent.setForcedDensity(displayContent.mInitialDisplayDensity, callingUserId);
                    }
                }
                resetPriorityAfterLockedSection();
                Binder.restoreCallingIdentity(ident);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(ident);
                throw th;
            }
        } else {
            throw new SecurityException("Must hold permission android.permission.WRITE_SECURE_SETTINGS");
        }
    }

    private int getForcedDisplayDensityForUserLocked(int userId) {
        String densityStr = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), "display_density_forced", userId);
        if (densityStr == null || densityStr.length() == 0) {
            densityStr = SystemProperties.get(DENSITY_OVERRIDE, (String) null);
        }
        if (densityStr == null || densityStr.length() <= 0) {
            return 0;
        }
        try {
            return Integer.parseInt(densityStr);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /* access modifiers changed from: package-private */
    public void reconfigureDisplayLocked(DisplayContent displayContent) {
        if (displayContent.isReady()) {
            displayContent.configureDisplayPolicy();
            displayContent.setLayoutNeeded();
            boolean configChanged = displayContent.updateOrientationFromAppTokens();
            Configuration currentDisplayConfig = displayContent.getConfiguration();
            this.mTempConfiguration.setTo(currentDisplayConfig);
            displayContent.computeScreenConfiguration(this.mTempConfiguration);
            if (configChanged || (currentDisplayConfig.diff(this.mTempConfiguration) != 0)) {
                displayContent.mWaitingForConfig = true;
                startFreezingDisplayLocked(0, 0, displayContent);
                displayContent.sendNewConfiguration();
            }
            this.mWindowPlacerLocked.performSurfacePlacement();
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    public void setOverscan(int displayId, int left, int top, int right, int bottom) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") == 0) {
            long ident = Binder.clearCallingIdentity();
            try {
                synchronized (this.mGlobalLock) {
                    boostPriorityForLockedSection();
                    DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                    if (displayContent != null) {
                        setOverscanLocked(displayContent, left, top, right, bottom);
                    }
                }
                resetPriorityAfterLockedSection();
                Binder.restoreCallingIdentity(ident);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(ident);
                throw th;
            }
        } else {
            throw new SecurityException("Must hold permission android.permission.WRITE_SECURE_SETTINGS");
        }
    }

    private void setOverscanLocked(DisplayContent displayContent, int left, int top, int right, int bottom) {
        DisplayInfo displayInfo = displayContent.getDisplayInfo();
        displayInfo.overscanLeft = left;
        displayInfo.overscanTop = top;
        displayInfo.overscanRight = right;
        displayInfo.overscanBottom = bottom;
        this.mDisplayWindowSettings.setOverscanLocked(displayInfo, left, top, right, bottom);
        reconfigureDisplayLocked(displayContent);
    }

    public void startWindowTrace() {
        this.mWindowTracing.startTrace((PrintWriter) null);
    }

    public void stopWindowTrace() {
        this.mWindowTracing.stopTrace((PrintWriter) null);
    }

    public boolean isWindowTraceEnabled() {
        return this.mWindowTracing.isEnabled();
    }

    /* access modifiers changed from: package-private */
    public final WindowState windowForClientLocked(Session session, IWindow client, boolean throwOnError) {
        return windowForClientLocked(session, client.asBinder(), throwOnError);
    }

    /* access modifiers changed from: package-private */
    public final WindowState windowForClientLocked(Session session, IBinder client, boolean throwOnError) {
        WindowState win = (WindowState) this.mWindowMap.get(client);
        if (win == null) {
            if (!throwOnError) {
                Slog.w("WindowManager", "Failed looking up window callers=" + Debug.getCallers(3));
                return null;
            }
            throw new IllegalArgumentException("Requested window " + client + " does not exist");
        } else if (session == null || win.mSession == session) {
            return win;
        } else {
            if (!throwOnError) {
                Slog.w("WindowManager", "Failed looking up window callers=" + Debug.getCallers(3));
                return null;
            }
            throw new IllegalArgumentException("Requested window " + client + " is in session " + win.mSession + ", not " + session);
        }
    }

    /* access modifiers changed from: package-private */
    public void makeWindowFreezingScreenIfNeededLocked(WindowState w) {
        if (!w.mToken.okToDisplay() && this.mWindowsFreezingScreen != 2 && w.mAppToken != null && !w.mAppToken.mIsCastMode) {
            w.setOrientationChanging(true);
            w.mLastFreezeDuration = 0;
            this.mRoot.mOrientationChangeComplete = false;
            if (this.mWindowsFreezingScreen == 0) {
                this.mWindowsFreezingScreen = 1;
                this.mH.sendNewMessageDelayed(11, w.getDisplayContent(), 2000);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void checkDrawnWindowsLocked() {
        if (!this.mWaitingForDrawn.isEmpty() && this.mWaitingForDrawnCallback != null) {
            int j = this.mWaitingForDrawn.size();
            while (true) {
                j--;
                if (j < 0) {
                    break;
                }
                WindowState win = this.mWaitingForDrawn.get(j);
                Slog.i("WindowManager", "Waiting for drawn " + win + ": removed=" + win.mRemoved + " visible=" + win.isVisibleLw() + " mHasSurface=" + win.mHasSurface + " drawState=" + win.mWinAnimator.mDrawState);
                if (win.mRemoved || !win.mHasSurface || !win.isVisibleByPolicy()) {
                    Slog.w("WindowManager", "Aborted waiting for drawn: " + win);
                    this.mWaitingForDrawn.remove(win);
                } else if (win.hasDrawnLw()) {
                    Slog.i("WindowManager", "Window drawn win=" + win);
                    this.mWaitingForDrawn.remove(win);
                }
            }
            if (this.mWaitingForDrawn.isEmpty()) {
                Slog.i("WindowManager", "All windows drawn!");
                this.mH.removeMessages(24);
                this.mH.sendEmptyMessage(33);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setHoldScreenLocked(Session newHoldScreen) {
        boolean hold = newHoldScreen != null;
        if (hold && this.mHoldingScreenOn != newHoldScreen) {
            this.mHoldingScreenWakeLock.setWorkSource(new WorkSource(newHoldScreen.mUid, newHoldScreen.mPackageName));
        }
        this.mHoldingScreenOn = newHoldScreen;
        if (hold == this.mHoldingScreenWakeLock.isHeld()) {
            return;
        }
        if (hold) {
            this.mLastWakeLockHoldingWindow = this.mRoot.mHoldScreenWindow;
            this.mLastWakeLockObscuringWindow = null;
            this.mHoldingScreenWakeLock.acquire();
            this.mPolicy.keepScreenOnStartedLw();
            return;
        }
        this.mLastWakeLockHoldingWindow = null;
        this.mLastWakeLockObscuringWindow = this.mRoot.mObscuringWindow;
        this.mPolicy.keepScreenOnStoppedLw();
        this.mHoldingScreenWakeLock.release();
    }

    /* access modifiers changed from: package-private */
    public void requestTraversal() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mWindowPlacerLocked.requestTraversal();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    /* access modifiers changed from: package-private */
    public void scheduleAnimationLocked() {
        WindowAnimator windowAnimator = this.mAnimator;
        if (windowAnimator != null) {
            windowAnimator.scheduleAnimation();
        }
    }

    /* access modifiers changed from: package-private */
    public boolean updateFocusedWindowLocked(int mode, boolean updateInputWindows) {
        Trace.traceBegin(32, "wmUpdateFocus");
        boolean changed = this.mRoot.updateFocusedWindowLocked(mode, updateInputWindows);
        Trace.traceEnd(32);
        return changed;
    }

    /* access modifiers changed from: package-private */
    public void startFreezingDisplayLocked(int exitAnim, int enterAnim) {
        startFreezingDisplayLocked(exitAnim, enterAnim, getDefaultDisplayContentLocked());
    }

    /* access modifiers changed from: package-private */
    public void startFreezingDisplayLocked(int exitAnim, int enterAnim, DisplayContent displayContent) {
        if (!this.mDisplayFrozen && !this.mRotatingSeamlessly && displayContent.isReady() && this.mPolicy.isScreenOn() && displayContent.okToAnimate()) {
            this.mScreenFrozenLock.acquire();
            this.mDisplayFrozen = true;
            this.mDisplayFreezeTime = SystemClock.elapsedRealtime();
            this.mLastFinishedFreezeSource = null;
            this.mFrozenDisplayId = displayContent.getDisplayId();
            this.mInputManagerCallback.freezeInputDispatchingLw();
            if (displayContent.mAppTransition.isTransitionSet()) {
                displayContent.mAppTransition.freeze();
            }
            this.mLatencyTracker.onActionStart(6);
            if (this.mPerf == null) {
                this.mPerf = new BoostFramework();
            }
            BoostFramework boostFramework = this.mPerf;
            if (boostFramework != null) {
                boostFramework.perfHint(4233, (String) null);
            }
            this.mExitAnimId = exitAnim;
            this.mEnterAnimId = enterAnim;
            ScreenRotationAnimation screenRotationAnimation = this.mAnimator.getScreenRotationAnimationLocked(this.mFrozenDisplayId);
            if (screenRotationAnimation != null) {
                screenRotationAnimation.kill();
            }
            boolean isSecure = displayContent.hasSecureWindowOnScreen();
            displayContent.updateDisplayInfo();
            this.mAnimator.setScreenRotationAnimationLocked(this.mFrozenDisplayId, new ScreenRotationAnimation(this.mContext, displayContent, displayContent.getDisplayRotation().isFixedToUserRotation(), isSecure, this));
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x0100  */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0103  */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x0127  */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0134  */
    /* JADX WARNING: Removed duplicated region for block: B:59:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void stopFreezingDisplayLocked() {
        /*
            r22 = this;
            r0 = r22
            boolean r1 = r0.mDisplayFrozen
            if (r1 != 0) goto L_0x0007
            return
        L_0x0007:
            com.android.server.wm.RootWindowContainer r1 = r0.mRoot
            int r2 = r0.mFrozenDisplayId
            com.android.server.wm.DisplayContent r1 = r1.getDisplayContent(r2)
            r2 = 1
            r3 = 0
            if (r1 == 0) goto L_0x0019
            boolean r4 = r1.mWaitingForConfig
            if (r4 == 0) goto L_0x0019
            r4 = r2
            goto L_0x001a
        L_0x0019:
            r4 = r3
        L_0x001a:
            if (r1 == 0) goto L_0x0023
            android.util.ArraySet<com.android.server.wm.AppWindowToken> r5 = r1.mOpeningApps
            int r5 = r5.size()
            goto L_0x0024
        L_0x0023:
            r5 = r3
        L_0x0024:
            if (r4 != 0) goto L_0x0138
            int r6 = r0.mAppsFreezingScreen
            if (r6 > 0) goto L_0x0138
            int r6 = r0.mWindowsFreezingScreen
            if (r6 == r2) goto L_0x0138
            boolean r6 = r0.mClientFreezingScreen
            if (r6 != 0) goto L_0x0138
            if (r5 <= 0) goto L_0x0036
            goto L_0x0138
        L_0x0036:
            int r6 = r0.mFrozenDisplayId
            r7 = -1
            r0.mFrozenDisplayId = r7
            r0.mDisplayFrozen = r3
            com.android.server.wm.InputManagerCallback r7 = r0.mInputManagerCallback
            r7.thawInputDispatchingLw()
            long r7 = android.os.SystemClock.elapsedRealtime()
            long r9 = r0.mDisplayFreezeTime
            long r7 = r7 - r9
            int r7 = (int) r7
            r0.mLastDisplayFreezeDuration = r7
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r8 = 128(0x80, float:1.794E-43)
            r7.<init>(r8)
            java.lang.String r8 = "Screen frozen for "
            r7.append(r8)
            int r8 = r0.mLastDisplayFreezeDuration
            long r8 = (long) r8
            android.util.TimeUtils.formatDuration(r8, r7)
            java.lang.Object r8 = r0.mLastFinishedFreezeSource
            if (r8 == 0) goto L_0x006c
            java.lang.String r8 = " due to "
            r7.append(r8)
            java.lang.Object r8 = r0.mLastFinishedFreezeSource
            r7.append(r8)
        L_0x006c:
            java.lang.String r8 = r7.toString()
            java.lang.String r9 = "WindowManager"
            android.util.Slog.i(r9, r8)
            com.android.server.wm.WindowManagerService$H r8 = r0.mH
            r9 = 17
            r8.removeMessages(r9)
            com.android.server.wm.WindowManagerService$H r8 = r0.mH
            r9 = 30
            r8.removeMessages(r9)
            r8 = 0
            com.android.server.wm.WindowAnimator r9 = r0.mAnimator
            com.android.server.wm.ScreenRotationAnimation r9 = r9.getScreenRotationAnimationLocked(r6)
            r15 = 0
            if (r9 == 0) goto L_0x00ec
            boolean r10 = r9.hasScreenshot()
            if (r10 == 0) goto L_0x00ea
            android.view.DisplayInfo r14 = r1.getDisplayInfo()
            com.android.server.wm.DisplayPolicy r10 = r1.getDisplayPolicy()
            int r11 = r0.mExitAnimId
            int r12 = r0.mEnterAnimId
            boolean r10 = r10.validateRotationAnimationLw(r11, r12, r3)
            if (r10 != 0) goto L_0x00a9
            r0.mEnterAnimId = r3
            r0.mExitAnimId = r3
        L_0x00a9:
            android.view.SurfaceControl$Transaction r11 = r0.mTransaction
            boolean r10 = r0.mIsCastMode
            if (r10 == 0) goto L_0x00b1
            r10 = 0
            goto L_0x00b5
        L_0x00b1:
            float r10 = r22.getTransitionAnimationScaleLocked()
        L_0x00b5:
            r16 = r10
            int r10 = r14.logicalWidth
            int r2 = r14.logicalHeight
            int r3 = r0.mExitAnimId
            int r12 = r0.mEnterAnimId
            r20 = r10
            r10 = r9
            r18 = r12
            r12 = 10000(0x2710, double:4.9407E-320)
            r21 = r14
            r14 = r16
            r15 = r20
            r16 = r2
            r17 = r3
            boolean r2 = r10.dismiss(r11, r12, r14, r15, r16, r17, r18)
            if (r2 == 0) goto L_0x00df
            android.view.SurfaceControl$Transaction r2 = r0.mTransaction
            r2.apply()
            r22.scheduleAnimationLocked()
            goto L_0x00e9
        L_0x00df:
            r9.kill()
            com.android.server.wm.WindowAnimator r2 = r0.mAnimator
            r3 = 0
            r2.setScreenRotationAnimationLocked(r6, r3)
            r8 = 1
        L_0x00e9:
            goto L_0x00f8
        L_0x00ea:
            r3 = r15
            goto L_0x00ed
        L_0x00ec:
            r3 = r15
        L_0x00ed:
            if (r9 == 0) goto L_0x00f7
            r9.kill()
            com.android.server.wm.WindowAnimator r2 = r0.mAnimator
            r2.setScreenRotationAnimationLocked(r6, r3)
        L_0x00f7:
            r8 = 1
        L_0x00f8:
            if (r1 == 0) goto L_0x0103
            boolean r2 = r1.updateOrientationFromAppTokens()
            if (r2 == 0) goto L_0x0103
            r19 = 1
            goto L_0x0105
        L_0x0103:
            r19 = 0
        L_0x0105:
            r2 = r19
            com.android.server.wm.WindowManagerService$H r3 = r0.mH
            r10 = 15
            r3.removeMessages(r10)
            com.android.server.wm.WindowManagerService$H r3 = r0.mH
            r11 = 2000(0x7d0, double:9.88E-321)
            r3.sendEmptyMessageDelayed(r10, r11)
            android.os.PowerManager$WakeLock r3 = r0.mScreenFrozenLock
            r3.release()
            if (r8 == 0) goto L_0x0125
            if (r1 == 0) goto L_0x0125
            if (r8 == 0) goto L_0x0125
            boolean r3 = r1.updateRotationUnchecked()
            r2 = r2 | r3
        L_0x0125:
            if (r2 == 0) goto L_0x012a
            r1.sendNewConfiguration()
        L_0x012a:
            com.android.internal.util.LatencyTracker r3 = r0.mLatencyTracker
            r10 = 6
            r3.onActionEnd(r10)
            android.util.BoostFramework r3 = r0.mPerf
            if (r3 == 0) goto L_0x0137
            r3.perfLockRelease()
        L_0x0137:
            return
        L_0x0138:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.stopFreezingDisplayLocked():void");
    }

    static int getPropertyInt(String[] tokens, int index, int defUnits, int defDps, DisplayMetrics dm) {
        String str;
        if (index < tokens.length && (str = tokens[index]) != null && str.length() > 0) {
            try {
                return Integer.parseInt(str);
            } catch (Exception e) {
            }
        }
        if (defUnits == 0) {
            return defDps;
        }
        return (int) TypedValue.applyDimension(defUnits, (float) defDps, dm);
    }

    /* access modifiers changed from: package-private */
    public void createWatermarkInTransaction() {
        String[] toks;
        if (this.mWatermark == null) {
            FileInputStream in = null;
            DataInputStream ind = null;
            try {
                DataInputStream ind2 = new DataInputStream(new FileInputStream(new File("/system/etc/setup.conf")));
                String line = ind2.readLine();
                if (!(line == null || (toks = line.split("%")) == null || toks.length <= 0)) {
                    DisplayContent displayContent = getDefaultDisplayContentLocked();
                    this.mWatermark = new Watermark(displayContent, displayContent.mRealDisplayMetrics, toks);
                }
                try {
                    ind2.close();
                } catch (IOException e) {
                }
            } catch (FileNotFoundException e2) {
                if (ind != null) {
                    ind.close();
                } else if (in != null) {
                    in.close();
                }
            } catch (IOException e3) {
                if (ind != null) {
                    ind.close();
                } else if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e4) {
                    }
                }
            } catch (Throwable th) {
                if (ind != null) {
                    try {
                        ind.close();
                    } catch (IOException e5) {
                    }
                } else if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e6) {
                    }
                }
                throw th;
            }
        }
    }

    public void setRecentsVisibility(boolean visible) {
        this.mAtmInternal.enforceCallerIsRecentsOrHasPermission("android.permission.STATUS_BAR", "setRecentsVisibility()");
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mPolicy.setRecentsVisibilityLw(visible);
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void setPipVisibility(boolean visible) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.STATUS_BAR") == 0) {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    this.mPolicy.setPipVisibilityLw(visible);
                } catch (Throwable th) {
                    while (true) {
                        resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            resetPriorityAfterLockedSection();
            return;
        }
        throw new SecurityException("Caller does not hold permission android.permission.STATUS_BAR");
    }

    public void setShelfHeight(boolean visible, int shelfHeight) {
        this.mAtmInternal.enforceCallerIsRecentsOrHasPermission("android.permission.STATUS_BAR", "setShelfHeight()");
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                getDefaultDisplayContentLocked().getPinnedStackController().setAdjustedForShelf(visible, shelfHeight);
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void statusBarVisibilityChanged(int displayId, int visibility) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.STATUS_BAR") == 0) {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                    if (displayContent != null) {
                        displayContent.statusBarVisibilityChanged(visibility);
                    } else {
                        Slog.w("WindowManager", "statusBarVisibilityChanged with invalid displayId=" + displayId);
                    }
                } catch (Throwable th) {
                    while (true) {
                        resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            resetPriorityAfterLockedSection();
            return;
        }
        throw new SecurityException("Caller does not hold permission android.permission.STATUS_BAR");
    }

    public void setForceShowSystemBars(boolean show) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.STATUS_BAR") == 0) {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    this.mRoot.forAllDisplayPolicies(PooledLambda.obtainConsumer($$Lambda$XcHmyRxMY5ULhjLiVsIKnPtvOM.INSTANCE, PooledLambda.__(), Boolean.valueOf(show)));
                } catch (Throwable th) {
                    while (true) {
                        resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            resetPriorityAfterLockedSection();
            return;
        }
        throw new SecurityException("Caller does not hold permission android.permission.STATUS_BAR");
    }

    public void setNavBarVirtualKeyHapticFeedbackEnabled(boolean enabled) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.STATUS_BAR") == 0) {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    this.mPolicy.setNavBarVirtualKeyHapticFeedbackEnabledLw(enabled);
                } catch (Throwable th) {
                    while (true) {
                        resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            resetPriorityAfterLockedSection();
            return;
        }
        throw new SecurityException("Caller does not hold permission android.permission.STATUS_BAR");
    }

    public int getNavBarPosition(int displayId) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                if (displayContent == null) {
                    Slog.w("WindowManager", "getNavBarPosition with invalid displayId=" + displayId + " callers=" + Debug.getCallers(3));
                    resetPriorityAfterLockedSection();
                    return -1;
                }
                displayContent.performLayout(false, false);
                int navBarPosition = displayContent.getDisplayPolicy().getNavBarPosition();
                resetPriorityAfterLockedSection();
                return navBarPosition;
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public WindowManagerPolicy.InputConsumer createInputConsumer(Looper looper, String name, InputEventReceiver.Factory inputEventReceiverFactory, int displayId) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                if (displayContent != null) {
                    WindowManagerPolicy.InputConsumer createInputConsumer = displayContent.getInputMonitor().createInputConsumer(looper, name, inputEventReceiverFactory);
                    resetPriorityAfterLockedSection();
                    return createInputConsumer;
                }
                resetPriorityAfterLockedSection();
                return null;
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public void createInputConsumer(IBinder token, String name, int displayId, InputChannel inputChannel) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent display = this.mRoot.getDisplayContent(displayId);
                if (display != null) {
                    display.getInputMonitor().createInputConsumer(token, name, inputChannel, Binder.getCallingPid(), Binder.getCallingUserHandle());
                }
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public boolean destroyInputConsumer(String name, int displayId) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent display = this.mRoot.getDisplayContent(displayId);
                if (display != null) {
                    boolean destroyInputConsumer = display.getInputMonitor().destroyInputConsumer(name);
                    resetPriorityAfterLockedSection();
                    return destroyInputConsumer;
                }
                resetPriorityAfterLockedSection();
                return false;
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public Region getCurrentImeTouchRegion() {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.RESTRICTED_VR_ACCESS") == 0) {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    Region r = new Region();
                    for (int i = this.mRoot.mChildren.size() - 1; i >= 0; i--) {
                        DisplayContent displayContent = (DisplayContent) this.mRoot.mChildren.get(i);
                        if (displayContent.mInputMethodWindow != null) {
                            displayContent.mInputMethodWindow.getTouchableRegion(r);
                            resetPriorityAfterLockedSection();
                            return r;
                        }
                    }
                    resetPriorityAfterLockedSection();
                    return r;
                } catch (Throwable th) {
                    while (true) {
                        resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        } else {
            throw new SecurityException("getCurrentImeTouchRegion is restricted to VR services");
        }
    }

    public boolean hasNavigationBar(int displayId) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent dc = this.mRoot.getDisplayContent(displayId);
                if (dc == null) {
                    resetPriorityAfterLockedSection();
                    return false;
                }
                boolean hasNavigationBar = dc.getDisplayPolicy().hasNavigationBar();
                resetPriorityAfterLockedSection();
                return hasNavigationBar;
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public void lockNow(Bundle options) {
        this.mPolicy.lockNow(options);
    }

    public void showRecentApps() {
        this.mPolicy.showRecentApps();
    }

    public boolean isSafeModeEnabled() {
        return this.mSafeMode;
    }

    public boolean clearWindowContentFrameStats(IBinder token) {
        if (checkCallingPermission("android.permission.FRAME_STATS", "clearWindowContentFrameStats()")) {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    WindowState windowState = (WindowState) this.mWindowMap.get(token);
                    if (windowState == null) {
                        resetPriorityAfterLockedSection();
                        return false;
                    }
                    WindowSurfaceController surfaceController = windowState.mWinAnimator.mSurfaceController;
                    if (surfaceController == null) {
                        resetPriorityAfterLockedSection();
                        return false;
                    }
                    boolean clearWindowContentFrameStats = surfaceController.clearWindowContentFrameStats();
                    resetPriorityAfterLockedSection();
                    return clearWindowContentFrameStats;
                } catch (Throwable th) {
                    while (true) {
                        resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        } else {
            throw new SecurityException("Requires FRAME_STATS permission");
        }
    }

    public WindowContentFrameStats getWindowContentFrameStats(IBinder token) {
        if (checkCallingPermission("android.permission.FRAME_STATS", "getWindowContentFrameStats()")) {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    WindowState windowState = (WindowState) this.mWindowMap.get(token);
                    if (windowState == null) {
                        resetPriorityAfterLockedSection();
                        return null;
                    }
                    WindowSurfaceController surfaceController = windowState.mWinAnimator.mSurfaceController;
                    if (surfaceController == null) {
                        resetPriorityAfterLockedSection();
                        return null;
                    }
                    if (this.mTempWindowRenderStats == null) {
                        this.mTempWindowRenderStats = new WindowContentFrameStats();
                    }
                    WindowContentFrameStats stats = this.mTempWindowRenderStats;
                    if (!surfaceController.getWindowContentFrameStats(stats)) {
                        resetPriorityAfterLockedSection();
                        return null;
                    }
                    resetPriorityAfterLockedSection();
                    return stats;
                } catch (Throwable th) {
                    while (true) {
                        resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        } else {
            throw new SecurityException("Requires FRAME_STATS permission");
        }
    }

    public void notifyAppRelaunching(IBinder token) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                AppWindowToken appWindow = this.mRoot.getAppWindowToken(token);
                if (appWindow != null) {
                    appWindow.startRelaunching();
                }
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void notifyAppRelaunchingFinished(IBinder token) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                AppWindowToken appWindow = this.mRoot.getAppWindowToken(token);
                if (appWindow != null) {
                    appWindow.finishRelaunching();
                }
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void notifyAppRelaunchesCleared(IBinder token) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                AppWindowToken appWindow = this.mRoot.getAppWindowToken(token);
                if (appWindow != null) {
                    appWindow.clearRelaunching();
                }
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void notifyAppResumedFinished(IBinder token) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mSaveSurfaceByKeyguardToken = null;
                AppWindowToken appWindow = this.mRoot.getAppWindowToken(token);
                if (appWindow != null) {
                    appWindow.getDisplayContent().mUnknownAppVisibilityController.notifyAppResumedFinished(appWindow);
                }
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void notifyTaskRemovedFromRecents(int taskId, int userId) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mTaskSnapshotController.notifyTaskRemovedFromRecents(taskId, userId);
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    private void dumpPolicyLocked(PrintWriter pw, String[] args, boolean dumpAll) {
        pw.println("WINDOW MANAGER POLICY STATE (dumpsys window policy)");
        this.mPolicy.dump("    ", pw, args);
    }

    private void dumpAnimatorLocked(PrintWriter pw, String[] args, boolean dumpAll) {
        pw.println("WINDOW MANAGER ANIMATOR STATE (dumpsys window animator)");
        this.mAnimator.dumpLocked(pw, "    ", dumpAll);
    }

    private void dumpTokensLocked(PrintWriter pw, boolean dumpAll) {
        pw.println("WINDOW MANAGER TOKENS (dumpsys window tokens)");
        this.mRoot.dumpTokens(pw, dumpAll);
    }

    private void dumpTraceStatus(PrintWriter pw) {
        pw.println("WINDOW MANAGER TRACE (dumpsys window trace)");
        pw.print(this.mWindowTracing.getStatus() + "\n");
    }

    private void dumpSessionsLocked(PrintWriter pw, boolean dumpAll) {
        pw.println("WINDOW MANAGER SESSIONS (dumpsys window sessions)");
        for (int i = 0; i < this.mSessions.size(); i++) {
            Session s = this.mSessions.valueAt(i);
            pw.print("  Session ");
            pw.print(s);
            pw.println(':');
            s.dump(pw, "    ");
        }
    }

    /* access modifiers changed from: package-private */
    public void writeToProtoLocked(ProtoOutputStream proto, int logLevel) {
        this.mPolicy.writeToProto(proto, 1146756268033L);
        this.mRoot.writeToProto(proto, 1146756268034L, logLevel);
        DisplayContent topFocusedDisplayContent = this.mRoot.getTopFocusedDisplayContent();
        if (topFocusedDisplayContent.mCurrentFocus != null) {
            topFocusedDisplayContent.mCurrentFocus.writeIdentifierToProto(proto, 1146756268035L);
        }
        if (topFocusedDisplayContent.mFocusedApp != null) {
            topFocusedDisplayContent.mFocusedApp.writeNameToProto(proto, 1138166333444L);
        }
        WindowState imeWindow = this.mRoot.getCurrentInputMethodWindow();
        if (imeWindow != null) {
            imeWindow.writeIdentifierToProto(proto, 1146756268037L);
        }
        proto.write(1133871366150L, this.mDisplayFrozen);
        DisplayContent defaultDisplayContent = getDefaultDisplayContentLocked();
        proto.write(1120986464263L, defaultDisplayContent.getRotation());
        proto.write(1120986464264L, defaultDisplayContent.getLastOrientation());
    }

    private void dumpWindowsLocked(PrintWriter pw, boolean dumpAll, ArrayList<WindowState> windows) {
        pw.println("WINDOW MANAGER WINDOWS (dumpsys window windows)");
        dumpWindowsNoHeaderLocked(pw, dumpAll, windows);
    }

    private void dumpWindowsNoHeaderLocked(PrintWriter pw, boolean dumpAll, ArrayList<WindowState> windows) {
        this.mRoot.dumpWindowsNoHeader(pw, dumpAll, windows);
        if (!this.mHidingNonSystemOverlayWindows.isEmpty()) {
            pw.println();
            pw.println("  Hiding System Alert Windows:");
            for (int i = this.mHidingNonSystemOverlayWindows.size() - 1; i >= 0; i--) {
                WindowState w = this.mHidingNonSystemOverlayWindows.get(i);
                pw.print("  #");
                pw.print(i);
                pw.print(' ');
                pw.print(w);
                if (dumpAll) {
                    pw.println(":");
                    w.dump(pw, "    ", true);
                } else {
                    pw.println();
                }
            }
        }
        if (this.mPendingRemove.size() > 0) {
            pw.println();
            pw.println("  Remove pending for:");
            for (int i2 = this.mPendingRemove.size() - 1; i2 >= 0; i2--) {
                WindowState w2 = this.mPendingRemove.get(i2);
                if (windows == null || windows.contains(w2)) {
                    pw.print("  Remove #");
                    pw.print(i2);
                    pw.print(' ');
                    pw.print(w2);
                    if (dumpAll) {
                        pw.println(":");
                        w2.dump(pw, "    ", true);
                    } else {
                        pw.println();
                    }
                }
            }
        }
        ArrayList<WindowState> arrayList = this.mForceRemoves;
        if (arrayList != null && arrayList.size() > 0) {
            pw.println();
            pw.println("  Windows force removing:");
            for (int i3 = this.mForceRemoves.size() - 1; i3 >= 0; i3--) {
                WindowState w3 = this.mForceRemoves.get(i3);
                pw.print("  Removing #");
                pw.print(i3);
                pw.print(' ');
                pw.print(w3);
                if (dumpAll) {
                    pw.println(":");
                    w3.dump(pw, "    ", true);
                } else {
                    pw.println();
                }
            }
        }
        if (this.mDestroySurface.size() > 0) {
            pw.println();
            pw.println("  Windows waiting to destroy their surface:");
            for (int i4 = this.mDestroySurface.size() - 1; i4 >= 0; i4--) {
                WindowState w4 = this.mDestroySurface.get(i4);
                if (windows == null || windows.contains(w4)) {
                    pw.print("  Destroy #");
                    pw.print(i4);
                    pw.print(' ');
                    pw.print(w4);
                    if (dumpAll) {
                        pw.println(":");
                        w4.dump(pw, "    ", true);
                    } else {
                        pw.println();
                    }
                }
            }
        }
        if (this.mResizingWindows.size() > 0) {
            pw.println();
            pw.println("  Windows waiting to resize:");
            for (int i5 = this.mResizingWindows.size() - 1; i5 >= 0; i5--) {
                WindowState w5 = this.mResizingWindows.get(i5);
                if (windows == null || windows.contains(w5)) {
                    pw.print("  Resizing #");
                    pw.print(i5);
                    pw.print(' ');
                    pw.print(w5);
                    if (dumpAll) {
                        pw.println(":");
                        w5.dump(pw, "    ", true);
                    } else {
                        pw.println();
                    }
                }
            }
        }
        if (this.mWaitingForDrawn.size() > 0) {
            pw.println();
            pw.println("  Clients waiting for these windows to be drawn:");
            for (int i6 = this.mWaitingForDrawn.size() - 1; i6 >= 0; i6--) {
                pw.print("  Waiting #");
                pw.print(i6);
                pw.print(' ');
                pw.print(this.mWaitingForDrawn.get(i6));
            }
        }
        pw.println();
        pw.print("  mGlobalConfiguration=");
        pw.println(this.mRoot.getConfiguration());
        pw.print("  mHasPermanentDpad=");
        pw.println(this.mHasPermanentDpad);
        this.mRoot.dumpTopFocusedDisplayId(pw);
        this.mRoot.forAllDisplays(new Consumer(pw) {
            private final /* synthetic */ PrintWriter f$0;

            {
                this.f$0 = r1;
            }

            public final void accept(Object obj) {
                WindowManagerService.lambda$dumpWindowsNoHeaderLocked$7(this.f$0, (DisplayContent) obj);
            }
        });
        pw.print("  mInTouchMode=");
        pw.println(this.mInTouchMode);
        pw.print("  mLastDisplayFreezeDuration=");
        TimeUtils.formatDuration((long) this.mLastDisplayFreezeDuration, pw);
        if (this.mLastFinishedFreezeSource != null) {
            pw.print(" due to ");
            pw.print(this.mLastFinishedFreezeSource);
        }
        pw.println();
        pw.print("  mLastWakeLockHoldingWindow=");
        pw.print(this.mLastWakeLockHoldingWindow);
        pw.print(" mLastWakeLockObscuringWindow=");
        pw.print(this.mLastWakeLockObscuringWindow);
        pw.println();
        this.mInputManagerCallback.dump(pw, "  ");
        this.mTaskSnapshotController.dump(pw, "  ");
        if (dumpAll) {
            WindowState imeWindow = this.mRoot.getCurrentInputMethodWindow();
            if (imeWindow != null) {
                pw.print("  mInputMethodWindow=");
                pw.println(imeWindow);
            }
            this.mWindowPlacerLocked.dump(pw, "  ");
            pw.print("  mSystemBooted=");
            pw.print(this.mSystemBooted);
            pw.print(" mDisplayEnabled=");
            pw.println(this.mDisplayEnabled);
            this.mRoot.dumpLayoutNeededDisplayIds(pw);
            pw.print("  mTransactionSequence=");
            pw.println(this.mTransactionSequence);
            pw.print("  mDisplayFrozen=");
            pw.print(this.mDisplayFrozen);
            pw.print(" windows=");
            pw.print(this.mWindowsFreezingScreen);
            pw.print(" client=");
            pw.print(this.mClientFreezingScreen);
            pw.print(" apps=");
            pw.print(this.mAppsFreezingScreen);
            DisplayContent defaultDisplayContent = getDefaultDisplayContentLocked();
            pw.print("  mRotation=");
            pw.print(defaultDisplayContent.getRotation());
            pw.print("  mLastWindowForcedOrientation=");
            pw.print(defaultDisplayContent.getLastWindowForcedOrientation());
            pw.print(" mLastOrientation=");
            pw.println(defaultDisplayContent.getLastOrientation());
            pw.print(" waitingForConfig=");
            pw.println(defaultDisplayContent.mWaitingForConfig);
            pw.print("  Animation settings: disabled=");
            pw.print(this.mAnimationsDisabled);
            pw.print(" window=");
            pw.print(this.mWindowAnimationScaleSetting);
            pw.print(" transition=");
            pw.print(this.mTransitionAnimationScaleSetting);
            pw.print(" animator=");
            pw.println(this.mAnimatorDurationScaleSetting);
            if (defaultDisplayContent.mAppTransition != null) {
                pw.print("  DefaultDisplayContent.mAppTransition:");
                defaultDisplayContent.mAppTransition.dump(pw, "    ");
            }
            if (this.mRecentsAnimationController != null) {
                pw.print("  mRecentsAnimationController=");
                pw.println(this.mRecentsAnimationController);
                this.mRecentsAnimationController.dump(pw, "    ");
            }
            PolicyControl.dump("  ", pw);
        }
    }

    static /* synthetic */ void lambda$dumpWindowsNoHeaderLocked$7(PrintWriter pw, DisplayContent dc) {
        WindowState inputMethodTarget = dc.mInputMethodTarget;
        if (inputMethodTarget != null) {
            pw.print("  mInputMethodTarget in display# ");
            pw.print(dc.getDisplayId());
            pw.print(' ');
            pw.println(inputMethodTarget);
        }
    }

    private boolean dumpWindows(PrintWriter pw, String name, String[] args, int opti, boolean dumpAll) {
        ArrayList<WindowState> windows = new ArrayList<>();
        if ("apps".equals(name) || "visible".equals(name) || "visible-apps".equals(name)) {
            boolean appsOnly = name.contains("apps");
            boolean visibleOnly = name.contains("visible");
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    if (appsOnly) {
                        this.mRoot.dumpDisplayContents(pw);
                    }
                    this.mRoot.forAllWindows((Consumer<WindowState>) new Consumer(visibleOnly, appsOnly, windows) {
                        private final /* synthetic */ boolean f$0;
                        private final /* synthetic */ boolean f$1;
                        private final /* synthetic */ ArrayList f$2;

                        {
                            this.f$0 = r1;
                            this.f$1 = r2;
                            this.f$2 = r3;
                        }

                        public final void accept(Object obj) {
                            WindowManagerService.lambda$dumpWindows$8(this.f$0, this.f$1, this.f$2, (WindowState) obj);
                        }
                    }, true);
                } catch (Throwable th) {
                    while (true) {
                        resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            resetPriorityAfterLockedSection();
        } else {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    this.mRoot.getWindowsByName(windows, name);
                } catch (Throwable th2) {
                    while (true) {
                        resetPriorityAfterLockedSection();
                        throw th2;
                    }
                }
            }
            resetPriorityAfterLockedSection();
        }
        if (windows.size() <= 0) {
            return false;
        }
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                dumpWindowsLocked(pw, dumpAll, windows);
            } catch (Throwable th3) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th3;
                }
            }
        }
        resetPriorityAfterLockedSection();
        return true;
    }

    static /* synthetic */ void lambda$dumpWindows$8(boolean visibleOnly, boolean appsOnly, ArrayList windows, WindowState w) {
        if (visibleOnly && !w.mWinAnimator.getShown()) {
            return;
        }
        if (!appsOnly || w.mAppToken != null) {
            windows.add(w);
        }
    }

    private void dumpLastANRLocked(PrintWriter pw) {
        pw.println("WINDOW MANAGER LAST ANR (dumpsys window lastanr)");
        String str = this.mLastANRState;
        if (str == null) {
            pw.println("  <no ANR has occurred since boot>");
        } else {
            pw.println(str);
        }
    }

    /* access modifiers changed from: package-private */
    public void saveANRStateLocked(AppWindowToken appWindowToken, WindowState windowState, String reason) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new FastPrintWriter(sw, false, 1024);
        pw.println("  ANR time: " + DateFormat.getDateTimeInstance().format(new Date()));
        if (appWindowToken != null) {
            pw.println("  Application at fault: " + appWindowToken.stringName);
        }
        if (windowState != null) {
            pw.println("  Window at fault: " + windowState.mAttrs.getTitle());
        }
        if (reason != null) {
            pw.println("  Reason: " + reason);
        }
        for (int i = this.mRoot.getChildCount() - 1; i >= 0; i--) {
            DisplayContent dc = (DisplayContent) this.mRoot.getChildAt(i);
            int displayId = dc.getDisplayId();
            if (!dc.mWinAddedSinceNullFocus.isEmpty()) {
                pw.println("  Windows added in display #" + displayId + " since null focus: " + dc.mWinAddedSinceNullFocus);
            }
            if (!dc.mWinRemovedSinceNullFocus.isEmpty()) {
                pw.println("  Windows removed in display #" + displayId + " since null focus: " + dc.mWinRemovedSinceNullFocus);
            }
        }
        pw.println();
        dumpWindowsNoHeaderLocked(pw, true, (ArrayList<WindowState>) null);
        pw.println();
        pw.println("Last ANR continued");
        this.mRoot.dumpDisplayContents(pw);
        pw.close();
        this.mLastANRState = sw.toString();
        this.mH.removeMessages(38);
        this.mH.sendEmptyMessageDelayed(38, 7200000);
    }

    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        PriorityDump.dump(this.mPriorityDumper, fd, pw, args);
    }

    /* access modifiers changed from: private */
    public void doDump(FileDescriptor fd, PrintWriter pw, String[] args, boolean useProto) {
        String opt;
        if (DumpUtils.checkDumpPermission(this.mContext, "WindowManager", pw)) {
            boolean dumpAll = false;
            int opti = 0;
            while (opti < args.length && (opt = args[opti]) != null && opt.length() > 0 && opt.charAt(0) == '-') {
                opti++;
                if ("-a".equals(opt)) {
                    dumpAll = true;
                } else if ("-h".equals(opt)) {
                    pw.println("Window manager dump options:");
                    pw.println("  [-a] [-h] [cmd] ...");
                    pw.println("  cmd may be one of:");
                    pw.println("    l[astanr]: last ANR information");
                    pw.println("    p[policy]: policy state");
                    pw.println("    a[animator]: animator state");
                    pw.println("    s[essions]: active sessions");
                    pw.println("    surfaces: active surfaces (debugging enabled only)");
                    pw.println("    d[isplays]: active display contents");
                    pw.println("    t[okens]: token list");
                    pw.println("    w[indows]: window list");
                    pw.println("    trace: print trace status and write Winscope trace to file");
                    pw.println("  cmd may also be a NAME to dump windows.  NAME may");
                    pw.println("    be a partial substring in a window name, a");
                    pw.println("    Window hex object identifier, or");
                    pw.println("    \"all\" for all windows, or");
                    pw.println("    \"visible\" for the visible windows.");
                    pw.println("    \"visible-apps\" for the visible app windows.");
                    pw.println("  -a: include all available server state.");
                    pw.println("  --proto: output dump in protocol buffer format.");
                    return;
                } else {
                    pw.println("Unknown argument: " + opt + "; use -h for help");
                }
            }
            if (useProto) {
                ProtoOutputStream proto = new ProtoOutputStream(fd);
                synchronized (this.mGlobalLock) {
                    try {
                        boostPriorityForLockedSection();
                        writeToProtoLocked(proto, 0);
                    } catch (Throwable th) {
                        while (true) {
                            resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                }
                resetPriorityAfterLockedSection();
                proto.flush();
            } else if (opti < args.length) {
                String cmd = args[opti];
                int opti2 = opti + 1;
                if (ActivityTaskManagerService.DUMP_LASTANR_CMD.equals(cmd) || "l".equals(cmd)) {
                    synchronized (this.mGlobalLock) {
                        try {
                            boostPriorityForLockedSection();
                            dumpLastANRLocked(pw);
                        } catch (Throwable th2) {
                            while (true) {
                                resetPriorityAfterLockedSection();
                                throw th2;
                            }
                        }
                    }
                    resetPriorityAfterLockedSection();
                } else if ("policy".equals(cmd) || "p".equals(cmd)) {
                    synchronized (this.mGlobalLock) {
                        try {
                            boostPriorityForLockedSection();
                            dumpPolicyLocked(pw, args, true);
                        } catch (Throwable th3) {
                            while (true) {
                                resetPriorityAfterLockedSection();
                                throw th3;
                            }
                        }
                    }
                    resetPriorityAfterLockedSection();
                } else if ("animator".equals(cmd) || ActivityTaskManagerService.DUMP_ACTIVITIES_SHORT_CMD.equals(cmd)) {
                    synchronized (this.mGlobalLock) {
                        try {
                            boostPriorityForLockedSection();
                            dumpAnimatorLocked(pw, args, true);
                        } catch (Throwable th4) {
                            while (true) {
                                resetPriorityAfterLockedSection();
                                throw th4;
                            }
                        }
                    }
                    resetPriorityAfterLockedSection();
                } else if ("sessions".equals(cmd) || "s".equals(cmd)) {
                    synchronized (this.mGlobalLock) {
                        try {
                            boostPriorityForLockedSection();
                            dumpSessionsLocked(pw, true);
                        } catch (Throwable th5) {
                            while (true) {
                                resetPriorityAfterLockedSection();
                                throw th5;
                            }
                        }
                    }
                    resetPriorityAfterLockedSection();
                } else if ("displays".equals(cmd) || "d".equals(cmd)) {
                    synchronized (this.mGlobalLock) {
                        try {
                            boostPriorityForLockedSection();
                            this.mRoot.dumpDisplayContents(pw);
                        } catch (Throwable th6) {
                            while (true) {
                                resetPriorityAfterLockedSection();
                                throw th6;
                            }
                        }
                    }
                    resetPriorityAfterLockedSection();
                } else if ("tokens".equals(cmd) || "t".equals(cmd)) {
                    synchronized (this.mGlobalLock) {
                        try {
                            boostPriorityForLockedSection();
                            dumpTokensLocked(pw, true);
                        } catch (Throwable th7) {
                            while (true) {
                                resetPriorityAfterLockedSection();
                                throw th7;
                            }
                        }
                    }
                    resetPriorityAfterLockedSection();
                } else if ("windows".equals(cmd) || "w".equals(cmd)) {
                    synchronized (this.mGlobalLock) {
                        try {
                            boostPriorityForLockedSection();
                            dumpWindowsLocked(pw, true, (ArrayList<WindowState>) null);
                        } catch (Throwable th8) {
                            while (true) {
                                resetPriorityAfterLockedSection();
                                throw th8;
                            }
                        }
                    }
                    resetPriorityAfterLockedSection();
                } else if ("all".equals(cmd)) {
                    synchronized (this.mGlobalLock) {
                        try {
                            boostPriorityForLockedSection();
                            dumpWindowsLocked(pw, true, (ArrayList<WindowState>) null);
                        } catch (Throwable th9) {
                            while (true) {
                                resetPriorityAfterLockedSection();
                                throw th9;
                            }
                        }
                    }
                    resetPriorityAfterLockedSection();
                } else if (ActivityTaskManagerService.DUMP_CONTAINERS_CMD.equals(cmd)) {
                    synchronized (this.mGlobalLock) {
                        try {
                            boostPriorityForLockedSection();
                            this.mRoot.dumpChildrenNames(pw, " ");
                            pw.println(" ");
                            this.mRoot.forAllWindows((Consumer<WindowState>) new Consumer(pw) {
                                private final /* synthetic */ PrintWriter f$0;

                                {
                                    this.f$0 = r1;
                                }

                                public final void accept(Object obj) {
                                    this.f$0.println((WindowState) obj);
                                }
                            }, true);
                        } catch (Throwable th10) {
                            while (true) {
                                resetPriorityAfterLockedSection();
                                throw th10;
                            }
                        }
                    }
                    resetPriorityAfterLockedSection();
                } else if ("trace".equals(cmd)) {
                    dumpTraceStatus(pw);
                } else if ("gesture".equals(cmd)) {
                    this.mMiuiGestureController.dump(pw, args);
                } else if ("freeform".equals(cmd)) {
                    this.mMiuiFreeFormGestureController.dump(pw, args);
                } else if (!dumpWindows(pw, cmd, args, opti2, dumpAll)) {
                    pw.println("Bad window command, or no windows match: " + cmd);
                    pw.println("Use -h for help.");
                }
            } else {
                synchronized (this.mGlobalLock) {
                    try {
                        boostPriorityForLockedSection();
                        pw.println();
                        if (dumpAll) {
                            pw.println("-------------------------------------------------------------------------------");
                        }
                        dumpLastANRLocked(pw);
                        pw.println();
                        if (dumpAll) {
                            pw.println("-------------------------------------------------------------------------------");
                        }
                        dumpPolicyLocked(pw, args, dumpAll);
                        pw.println();
                        if (dumpAll) {
                            pw.println("-------------------------------------------------------------------------------");
                        }
                        dumpAnimatorLocked(pw, args, dumpAll);
                        pw.println();
                        if (dumpAll) {
                            pw.println("-------------------------------------------------------------------------------");
                        }
                        dumpSessionsLocked(pw, dumpAll);
                        pw.println();
                        if (dumpAll) {
                            pw.println("-------------------------------------------------------------------------------");
                        }
                        if (dumpAll) {
                            pw.println("-------------------------------------------------------------------------------");
                        }
                        this.mRoot.dumpDisplayContents(pw);
                        pw.println();
                        if (dumpAll) {
                            pw.println("-------------------------------------------------------------------------------");
                        }
                        dumpTokensLocked(pw, dumpAll);
                        pw.println();
                        if (dumpAll) {
                            pw.println("-------------------------------------------------------------------------------");
                        }
                        dumpWindowsLocked(pw, dumpAll, (ArrayList<WindowState>) null);
                        if (dumpAll) {
                            pw.println("-------------------------------------------------------------------------------");
                        }
                        dumpTraceStatus(pw);
                        if (dumpAll) {
                            this.mMiuiGestureController.dump(pw, args);
                        }
                    } catch (Throwable th11) {
                        while (true) {
                            resetPriorityAfterLockedSection();
                            throw th11;
                        }
                    }
                }
                resetPriorityAfterLockedSection();
            }
        }
    }

    public void monitor() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    /* access modifiers changed from: package-private */
    public DisplayContent getDefaultDisplayContentLocked() {
        return this.mRoot.getDisplayContent(0);
    }

    public void onOverlayChanged() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mRoot.forAllDisplays($$Lambda$WindowManagerService$oXZopye9ykF6MR6QjHAIi3bGRc.INSTANCE);
                requestTraversal();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    static /* synthetic */ void lambda$onOverlayChanged$10(DisplayContent displayContent) {
        displayContent.getDisplayPolicy().onOverlayChangedLw();
        displayContent.updateDisplayInfo();
    }

    public void onDisplayChanged(int displayId) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                if (displayContent != null) {
                    displayContent.updateDisplayInfo();
                }
                this.mWindowPlacerLocked.requestTraversal();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public Object getWindowManagerLock() {
        return this.mGlobalLock;
    }

    public void setWillReplaceWindow(IBinder token, boolean animate) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                AppWindowToken appWindowToken = this.mRoot.getAppWindowToken(token);
                if (appWindowToken == null) {
                    Slog.w("WindowManager", "Attempted to set replacing window on non-existing app token " + token);
                    resetPriorityAfterLockedSection();
                } else if (!appWindowToken.hasContentToDisplay()) {
                    Slog.w("WindowManager", "Attempted to set replacing window on app token with no content" + token);
                    resetPriorityAfterLockedSection();
                } else {
                    appWindowToken.setWillReplaceWindows(animate);
                    resetPriorityAfterLockedSection();
                }
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setWillReplaceWindows(IBinder token, boolean childrenOnly) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                AppWindowToken appWindowToken = this.mRoot.getAppWindowToken(token);
                if (appWindowToken == null) {
                    Slog.w("WindowManager", "Attempted to set replacing window on non-existing app token " + token);
                    resetPriorityAfterLockedSection();
                } else if (!appWindowToken.hasContentToDisplay()) {
                    Slog.w("WindowManager", "Attempted to set replacing window on app token with no content" + token);
                    resetPriorityAfterLockedSection();
                } else {
                    if (childrenOnly) {
                        appWindowToken.setWillReplaceChildWindows();
                    } else {
                        appWindowToken.setWillReplaceWindows(false);
                    }
                    scheduleClearWillReplaceWindows(token, true);
                    resetPriorityAfterLockedSection();
                }
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0033, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0036, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void scheduleClearWillReplaceWindows(android.os.IBinder r6, boolean r7) {
        /*
            r5 = this;
            com.android.server.wm.WindowManagerGlobalLock r0 = r5.mGlobalLock
            monitor-enter(r0)
            boostPriorityForLockedSection()     // Catch:{ all -> 0x0037 }
            com.android.server.wm.RootWindowContainer r1 = r5.mRoot     // Catch:{ all -> 0x0037 }
            com.android.server.wm.AppWindowToken r1 = r1.getAppWindowToken(r6)     // Catch:{ all -> 0x0037 }
            if (r1 != 0) goto L_0x0029
            java.lang.String r2 = "WindowManager"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0037 }
            r3.<init>()     // Catch:{ all -> 0x0037 }
            java.lang.String r4 = "Attempted to reset replacing window on non-existing app token "
            r3.append(r4)     // Catch:{ all -> 0x0037 }
            r3.append(r6)     // Catch:{ all -> 0x0037 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0037 }
            android.util.Slog.w(r2, r3)     // Catch:{ all -> 0x0037 }
            monitor-exit(r0)     // Catch:{ all -> 0x0037 }
            resetPriorityAfterLockedSection()
            return
        L_0x0029:
            if (r7 == 0) goto L_0x002f
            r5.scheduleWindowReplacementTimeouts(r1)     // Catch:{ all -> 0x0037 }
            goto L_0x0032
        L_0x002f:
            r1.clearWillReplaceWindows()     // Catch:{ all -> 0x0037 }
        L_0x0032:
            monitor-exit(r0)     // Catch:{ all -> 0x0037 }
            resetPriorityAfterLockedSection()
            return
        L_0x0037:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0037 }
            resetPriorityAfterLockedSection()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.scheduleClearWillReplaceWindows(android.os.IBinder, boolean):void");
    }

    /* access modifiers changed from: package-private */
    public void scheduleWindowReplacementTimeouts(AppWindowToken appWindowToken) {
        if (!this.mWindowReplacementTimeouts.contains(appWindowToken)) {
            this.mWindowReplacementTimeouts.add(appWindowToken);
        }
        this.mH.removeMessages(46);
        this.mH.sendEmptyMessageDelayed(46, 2000);
    }

    public int getDockedStackSide() {
        int dockSide;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                TaskStack dockedStack = getDefaultDisplayContentLocked().getSplitScreenPrimaryStackIgnoringVisibility();
                dockSide = dockedStack == null ? -1 : dockedStack.getDockSide();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
        return dockSide;
    }

    public void setDockedStackResizing(boolean resizing) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                getDefaultDisplayContentLocked().getDockedDividerController().setResizing(resizing);
                requestTraversal();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void setDockedStackDividerTouchRegion(Rect touchRegion) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent dc = getDefaultDisplayContentLocked();
                dc.getDockedDividerController().setTouchRegion(touchRegion);
                dc.updateTouchExcludeRegion();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void setResizeDimLayer(boolean visible, int targetWindowingMode, float alpha) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                getDefaultDisplayContentLocked().getDockedDividerController().setResizeDimLayer(visible, targetWindowingMode, alpha);
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void setForceResizableTasks(boolean forceResizableTasks) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mForceResizableTasks = forceResizableTasks;
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void setSupportsPictureInPicture(boolean supportsPictureInPicture) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mSupportsPictureInPicture = supportsPictureInPicture;
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void setSupportsFreeformWindowManagement(boolean supportsFreeformWindowManagement) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mSupportsFreeformWindowManagement = supportsFreeformWindowManagement;
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    /* access modifiers changed from: package-private */
    public void setForceDesktopModeOnExternalDisplays(boolean forceDesktopModeOnExternalDisplays) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mForceDesktopModeOnExternalDisplays = forceDesktopModeOnExternalDisplays;
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void setIsPc(boolean isPc) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mIsPc = isPc;
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    static int dipToPixel(int dip, DisplayMetrics displayMetrics) {
        return (int) TypedValue.applyDimension(1, (float) dip, displayMetrics);
    }

    public void registerDockedStackListener(IDockedStackListener listener) {
        this.mAtmInternal.enforceCallerIsRecentsOrHasPermission("android.permission.REGISTER_WINDOW_MANAGER_LISTENERS", "registerDockedStackListener()");
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                getDefaultDisplayContentLocked().mDividerControllerLocked.registerDockedStackListener(listener);
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void registerPinnedStackListener(int displayId, IPinnedStackListener listener) {
        if (checkCallingPermission("android.permission.REGISTER_WINDOW_MANAGER_LISTENERS", "registerPinnedStackListener()") && this.mSupportsPictureInPicture) {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    this.mRoot.getDisplayContent(displayId).getPinnedStackController().registerPinnedStackListener(listener);
                } catch (Throwable th) {
                    while (true) {
                        resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            resetPriorityAfterLockedSection();
        }
    }

    public void requestAppKeyboardShortcuts(IResultReceiver receiver, int deviceId) {
        try {
            WindowState focusedWindow = getFocusedWindow();
            if (focusedWindow != null && focusedWindow.mClient != null) {
                getFocusedWindow().mClient.requestAppKeyboardShortcuts(receiver, deviceId);
            }
        } catch (RemoteException e) {
        }
    }

    public void getStableInsets(int displayId, Rect outInsets) throws RemoteException {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                getStableInsetsLocked(displayId, outInsets);
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    /* access modifiers changed from: package-private */
    public void getStableInsetsLocked(int displayId, Rect outInsets) {
        outInsets.setEmpty();
        DisplayContent dc = this.mRoot.getDisplayContent(displayId);
        if (dc != null) {
            DisplayInfo di = dc.getDisplayInfo();
            dc.getDisplayPolicy().getStableInsetsLw(di.rotation, di.logicalWidth, di.logicalHeight, di.displayCutout, outInsets);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public void setForwardedInsets(int displayId, Insets insets) throws RemoteException {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent dc = this.mRoot.getDisplayContent(displayId);
                if (dc != null) {
                    if (Binder.getCallingUid() == dc.getDisplay().getOwnerUid()) {
                        dc.setForwardedInsets(insets);
                        resetPriorityAfterLockedSection();
                        return;
                    }
                    throw new SecurityException("Only owner of the display can set ForwardedInsets to it.");
                }
            } finally {
                resetPriorityAfterLockedSection();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void intersectDisplayInsetBounds(Rect display, Rect insets, Rect inOutBounds) {
        this.mTmpRect3.set(display);
        this.mTmpRect3.inset(insets);
        inOutBounds.intersect(this.mTmpRect3);
    }

    private static class MousePositionTracker implements WindowManagerPolicyConstants.PointerEventListener {
        /* access modifiers changed from: private */
        public boolean mLatestEventWasMouse;
        /* access modifiers changed from: private */
        public float mLatestMouseX;
        /* access modifiers changed from: private */
        public float mLatestMouseY;

        private MousePositionTracker() {
        }

        /* access modifiers changed from: package-private */
        public void updatePosition(float x, float y) {
            synchronized (this) {
                this.mLatestEventWasMouse = true;
                this.mLatestMouseX = x;
                this.mLatestMouseY = y;
            }
        }

        public void onPointerEvent(MotionEvent motionEvent) {
            if (motionEvent.isFromSource(UsbACInterface.FORMAT_III_IEC1937_MPEG1_Layer1)) {
                updatePosition(motionEvent.getRawX(), motionEvent.getRawY());
                return;
            }
            synchronized (this) {
                this.mLatestEventWasMouse = false;
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001c, code lost:
        monitor-enter(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
        boostPriorityForLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0026, code lost:
        if (r9.mDragDropController.dragDropActiveLocked() == false) goto L_0x002d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0028, code lost:
        monitor-exit(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0029, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x002c, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
        r0 = windowForClientLocked((com.android.server.wm.Session) null, r10, false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0033, code lost:
        if (r0 != null) goto L_0x0050;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0035, code lost:
        android.util.Slog.w("WindowManager", "Bad requesting window " + r10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x004b, code lost:
        monitor-exit(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x004c, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x004f, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        r4 = r0.getDisplayContent();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0054, code lost:
        if (r4 != null) goto L_0x005c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0056, code lost:
        monitor-exit(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0057, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x005a, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:?, code lost:
        r5 = r4.getTouchableWinAtPointLocked(r1, r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x0060, code lost:
        if (r5 == r0) goto L_0x0067;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0062, code lost:
        monitor-exit(r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0063, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0066, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:?, code lost:
        r5.mClient.updatePointerIcon(r5.translateToWindowX(r1), r5.translateToWindowY(r2));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:?, code lost:
        android.util.Slog.w("WindowManager", "unable to update pointer icon");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0082, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x0084, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x0087, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x001a, code lost:
        r3 = r9.mGlobalLock;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updatePointerIcon(android.view.IWindow r10) {
        /*
            r9 = this;
            com.android.server.wm.WindowManagerService$MousePositionTracker r0 = r9.mMousePositionTracker
            monitor-enter(r0)
            com.android.server.wm.WindowManagerService$MousePositionTracker r1 = r9.mMousePositionTracker     // Catch:{ all -> 0x0088 }
            boolean r1 = r1.mLatestEventWasMouse     // Catch:{ all -> 0x0088 }
            if (r1 != 0) goto L_0x000d
            monitor-exit(r0)     // Catch:{ all -> 0x0088 }
            return
        L_0x000d:
            com.android.server.wm.WindowManagerService$MousePositionTracker r1 = r9.mMousePositionTracker     // Catch:{ all -> 0x0088 }
            float r1 = r1.mLatestMouseX     // Catch:{ all -> 0x0088 }
            com.android.server.wm.WindowManagerService$MousePositionTracker r2 = r9.mMousePositionTracker     // Catch:{ all -> 0x0088 }
            float r2 = r2.mLatestMouseY     // Catch:{ all -> 0x0088 }
            monitor-exit(r0)     // Catch:{ all -> 0x0088 }
            com.android.server.wm.WindowManagerGlobalLock r3 = r9.mGlobalLock
            monitor-enter(r3)
            boostPriorityForLockedSection()     // Catch:{ all -> 0x0082 }
            com.android.server.wm.DragDropController r0 = r9.mDragDropController     // Catch:{ all -> 0x0082 }
            boolean r0 = r0.dragDropActiveLocked()     // Catch:{ all -> 0x0082 }
            if (r0 == 0) goto L_0x002d
            monitor-exit(r3)     // Catch:{ all -> 0x0082 }
            resetPriorityAfterLockedSection()
            return
        L_0x002d:
            r0 = 0
            r4 = 0
            com.android.server.wm.WindowState r0 = r9.windowForClientLocked((com.android.server.wm.Session) r0, (android.view.IWindow) r10, (boolean) r4)     // Catch:{ all -> 0x0082 }
            if (r0 != 0) goto L_0x0050
            java.lang.String r4 = "WindowManager"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0082 }
            r5.<init>()     // Catch:{ all -> 0x0082 }
            java.lang.String r6 = "Bad requesting window "
            r5.append(r6)     // Catch:{ all -> 0x0082 }
            r5.append(r10)     // Catch:{ all -> 0x0082 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0082 }
            android.util.Slog.w(r4, r5)     // Catch:{ all -> 0x0082 }
            monitor-exit(r3)     // Catch:{ all -> 0x0082 }
            resetPriorityAfterLockedSection()
            return
        L_0x0050:
            com.android.server.wm.DisplayContent r4 = r0.getDisplayContent()     // Catch:{ all -> 0x0082 }
            if (r4 != 0) goto L_0x005b
            monitor-exit(r3)     // Catch:{ all -> 0x0082 }
            resetPriorityAfterLockedSection()
            return
        L_0x005b:
            com.android.server.wm.WindowState r5 = r4.getTouchableWinAtPointLocked(r1, r2)     // Catch:{ all -> 0x0082 }
            if (r5 == r0) goto L_0x0067
            monitor-exit(r3)     // Catch:{ all -> 0x0082 }
            resetPriorityAfterLockedSection()
            return
        L_0x0067:
            android.view.IWindow r6 = r5.mClient     // Catch:{ RemoteException -> 0x0075 }
            float r7 = r5.translateToWindowX(r1)     // Catch:{ RemoteException -> 0x0075 }
            float r8 = r5.translateToWindowY(r2)     // Catch:{ RemoteException -> 0x0075 }
            r6.updatePointerIcon(r7, r8)     // Catch:{ RemoteException -> 0x0075 }
            goto L_0x007d
        L_0x0075:
            r6 = move-exception
            java.lang.String r7 = "WindowManager"
            java.lang.String r8 = "unable to update pointer icon"
            android.util.Slog.w(r7, r8)     // Catch:{ all -> 0x0082 }
        L_0x007d:
            monitor-exit(r3)     // Catch:{ all -> 0x0082 }
            resetPriorityAfterLockedSection()
            return
        L_0x0082:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0082 }
            resetPriorityAfterLockedSection()
            throw r0
        L_0x0088:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0088 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.updatePointerIcon(android.view.IWindow):void");
    }

    /* access modifiers changed from: package-private */
    public void restorePointerIconLocked(DisplayContent displayContent, float latestX, float latestY) {
        this.mMousePositionTracker.updatePosition(latestX, latestY);
        WindowState windowUnderPointer = displayContent.getTouchableWinAtPointLocked(latestX, latestY);
        if (windowUnderPointer != null) {
            try {
                windowUnderPointer.mClient.updatePointerIcon(windowUnderPointer.translateToWindowX(latestX), windowUnderPointer.translateToWindowY(latestY));
            } catch (RemoteException e) {
                Slog.w("WindowManager", "unable to restore pointer icon");
            }
        } else {
            InputManager.getInstance().setPointerIconType(1000);
        }
    }

    private void checkCallerOwnsDisplay(int displayId) {
        Display display = this.mDisplayManager.getDisplay(displayId);
        if (display == null) {
            throw new IllegalArgumentException("Cannot find display for non-existent displayId: " + displayId);
        } else if (Binder.getCallingUid() != display.getOwnerUid()) {
            throw new SecurityException("The caller doesn't own the display.");
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* access modifiers changed from: package-private */
    public void reparentDisplayContent(IWindow client, SurfaceControl sc, int displayId) {
        long token;
        checkCallerOwnsDisplay(displayId);
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                token = Binder.clearCallingIdentity();
                WindowState win = windowForClientLocked((Session) null, client, false);
                if (win == null) {
                    Slog.w("WindowManager", "Bad requesting window " + client);
                    Binder.restoreCallingIdentity(token);
                    resetPriorityAfterLockedSection();
                    return;
                }
                getDisplayContentOrCreate(displayId, (IBinder) null).reparentDisplayContent(win, sc);
                Binder.restoreCallingIdentity(token);
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* access modifiers changed from: package-private */
    public void updateDisplayContentLocation(IWindow client, int x, int y, int displayId) {
        long token;
        checkCallerOwnsDisplay(displayId);
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                token = Binder.clearCallingIdentity();
                WindowState win = windowForClientLocked((Session) null, client, false);
                if (win == null) {
                    Slog.w("WindowManager", "Bad requesting window " + client);
                    Binder.restoreCallingIdentity(token);
                    resetPriorityAfterLockedSection();
                    return;
                }
                DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                if (displayContent != null) {
                    displayContent.updateLocation(win, x, y);
                }
                Binder.restoreCallingIdentity(token);
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void updateTapExcludeRegion(IWindow client, int regionId, Region region) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                WindowState callingWin = windowForClientLocked((Session) null, client, false);
                if (callingWin == null) {
                    Slog.w("WindowManager", "Bad requesting window " + client);
                    resetPriorityAfterLockedSection();
                    return;
                }
                callingWin.updateTapExcludeRegion(regionId, region);
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyHasSurfaceView(Session session, IBinder token, boolean hasSurfaceView) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                WindowState window = windowForClientLocked(session, token, false);
                if (window != null) {
                    window.mHasSurfaceView = hasSurfaceView;
                }
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public void dontOverrideDisplayInfo(int displayId) {
        long token = Binder.clearCallingIdentity();
        try {
            synchronized (this.mGlobalLock) {
                boostPriorityForLockedSection();
                DisplayContent dc = getDisplayContentOrCreate(displayId, (IBinder) null);
                if (dc != null) {
                    dc.mShouldOverrideDisplayConfiguration = false;
                    this.mDisplayManagerInternal.setDisplayInfoOverrideFromWindowManager(displayId, (DisplayInfo) null);
                } else {
                    throw new IllegalArgumentException("Trying to configure a non existent display.");
                }
            }
            resetPriorityAfterLockedSection();
            Binder.restoreCallingIdentity(token);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(token);
            throw th;
        }
    }

    public int getWindowingMode(int displayId) {
        if (checkCallingPermission("android.permission.INTERNAL_SYSTEM_WINDOW", "getWindowingMode()")) {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                    if (displayContent == null) {
                        Slog.w("WindowManager", "Attempted to get windowing mode of a display that does not exist: " + displayId);
                        resetPriorityAfterLockedSection();
                        return 0;
                    }
                    int windowingModeLocked = this.mDisplayWindowSettings.getWindowingModeLocked(displayContent);
                    resetPriorityAfterLockedSection();
                    return windowingModeLocked;
                } catch (Throwable th) {
                    while (true) {
                        resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        } else {
            throw new SecurityException("Requires INTERNAL_SYSTEM_WINDOW permission");
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0060, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0063, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setWindowingMode(int r7, int r8) {
        /*
            r6 = this;
            java.lang.String r0 = "android.permission.INTERNAL_SYSTEM_WINDOW"
            java.lang.String r1 = "setWindowingMode()"
            boolean r0 = r6.checkCallingPermission(r0, r1)
            if (r0 == 0) goto L_0x006a
            com.android.server.wm.WindowManagerGlobalLock r0 = r6.mGlobalLock
            monitor-enter(r0)
            boostPriorityForLockedSection()     // Catch:{ all -> 0x0064 }
            r1 = 0
            com.android.server.wm.DisplayContent r1 = r6.getDisplayContentOrCreate(r7, r1)     // Catch:{ all -> 0x0064 }
            if (r1 != 0) goto L_0x0032
            java.lang.String r2 = "WindowManager"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0064 }
            r3.<init>()     // Catch:{ all -> 0x0064 }
            java.lang.String r4 = "Attempted to set windowing mode to a display that does not exist: "
            r3.append(r4)     // Catch:{ all -> 0x0064 }
            r3.append(r7)     // Catch:{ all -> 0x0064 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0064 }
            android.util.Slog.w(r2, r3)     // Catch:{ all -> 0x0064 }
            monitor-exit(r0)     // Catch:{ all -> 0x0064 }
            resetPriorityAfterLockedSection()
            return
        L_0x0032:
            int r2 = r1.getWindowingMode()     // Catch:{ all -> 0x0064 }
            com.android.server.wm.DisplayWindowSettings r3 = r6.mDisplayWindowSettings     // Catch:{ all -> 0x0064 }
            r3.setWindowingModeLocked(r1, r8)     // Catch:{ all -> 0x0064 }
            r6.reconfigureDisplayLocked(r1)     // Catch:{ all -> 0x0064 }
            int r3 = r1.getWindowingMode()     // Catch:{ all -> 0x0064 }
            if (r2 == r3) goto L_0x005f
            com.android.server.wm.WindowManagerService$H r3 = r6.mH     // Catch:{ all -> 0x0064 }
            r4 = 18
            r3.removeMessages(r4)     // Catch:{ all -> 0x0064 }
            long r3 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x0064 }
            r6.sendNewConfiguration(r7)     // Catch:{ all -> 0x005a }
            android.os.Binder.restoreCallingIdentity(r3)     // Catch:{ all -> 0x0064 }
            r1.executeAppTransition()     // Catch:{ all -> 0x0064 }
            goto L_0x005f
        L_0x005a:
            r5 = move-exception
            android.os.Binder.restoreCallingIdentity(r3)     // Catch:{ all -> 0x0064 }
            throw r5     // Catch:{ all -> 0x0064 }
        L_0x005f:
            monitor-exit(r0)     // Catch:{ all -> 0x0064 }
            resetPriorityAfterLockedSection()
            return
        L_0x0064:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0064 }
            resetPriorityAfterLockedSection()
            throw r1
        L_0x006a:
            java.lang.SecurityException r0 = new java.lang.SecurityException
            java.lang.String r1 = "Requires INTERNAL_SYSTEM_WINDOW permission"
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.setWindowingMode(int, int):void");
    }

    @WindowManager.RemoveContentMode
    public int getRemoveContentMode(int displayId) {
        if (checkCallingPermission("android.permission.INTERNAL_SYSTEM_WINDOW", "getRemoveContentMode()")) {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                    if (displayContent == null) {
                        Slog.w("WindowManager", "Attempted to get remove mode of a display that does not exist: " + displayId);
                        resetPriorityAfterLockedSection();
                        return 0;
                    }
                    int removeContentModeLocked = this.mDisplayWindowSettings.getRemoveContentModeLocked(displayContent);
                    resetPriorityAfterLockedSection();
                    return removeContentModeLocked;
                } catch (Throwable th) {
                    while (true) {
                        resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        } else {
            throw new SecurityException("Requires INTERNAL_SYSTEM_WINDOW permission");
        }
    }

    public void setRemoveContentMode(int displayId, @WindowManager.RemoveContentMode int mode) {
        if (checkCallingPermission("android.permission.INTERNAL_SYSTEM_WINDOW", "setRemoveContentMode()")) {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    DisplayContent displayContent = getDisplayContentOrCreate(displayId, (IBinder) null);
                    if (displayContent == null) {
                        Slog.w("WindowManager", "Attempted to set remove mode to a display that does not exist: " + displayId);
                        resetPriorityAfterLockedSection();
                        return;
                    }
                    this.mDisplayWindowSettings.setRemoveContentModeLocked(displayContent, mode);
                    reconfigureDisplayLocked(displayContent);
                    resetPriorityAfterLockedSection();
                } catch (Throwable th) {
                    while (true) {
                        resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        } else {
            throw new SecurityException("Requires INTERNAL_SYSTEM_WINDOW permission");
        }
    }

    public boolean shouldShowWithInsecureKeyguard(int displayId) {
        if (checkCallingPermission("android.permission.INTERNAL_SYSTEM_WINDOW", "shouldShowWithInsecureKeyguard()")) {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                    if (displayContent == null) {
                        Slog.w("WindowManager", "Attempted to get flag of a display that does not exist: " + displayId);
                        resetPriorityAfterLockedSection();
                        return false;
                    }
                    boolean shouldShowWithInsecureKeyguardLocked = this.mDisplayWindowSettings.shouldShowWithInsecureKeyguardLocked(displayContent);
                    resetPriorityAfterLockedSection();
                    return shouldShowWithInsecureKeyguardLocked;
                } catch (Throwable th) {
                    while (true) {
                        resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        } else {
            throw new SecurityException("Requires INTERNAL_SYSTEM_WINDOW permission");
        }
    }

    public void setShouldShowWithInsecureKeyguard(int displayId, boolean shouldShow) {
        if (checkCallingPermission("android.permission.INTERNAL_SYSTEM_WINDOW", "setShouldShowWithInsecureKeyguard()")) {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    DisplayContent displayContent = getDisplayContentOrCreate(displayId, (IBinder) null);
                    if (displayContent == null) {
                        Slog.w("WindowManager", "Attempted to set flag to a display that does not exist: " + displayId);
                        resetPriorityAfterLockedSection();
                        return;
                    }
                    this.mDisplayWindowSettings.setShouldShowWithInsecureKeyguardLocked(displayContent, shouldShow);
                    reconfigureDisplayLocked(displayContent);
                    resetPriorityAfterLockedSection();
                } catch (Throwable th) {
                    while (true) {
                        resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        } else {
            throw new SecurityException("Requires INTERNAL_SYSTEM_WINDOW permission");
        }
    }

    public boolean shouldShowSystemDecors(int displayId) {
        if (checkCallingPermission("android.permission.INTERNAL_SYSTEM_WINDOW", "shouldShowSystemDecors()")) {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                    if (displayContent == null) {
                        Slog.w("WindowManager", "Attempted to get system decors flag of a display that does not exist: " + displayId);
                        resetPriorityAfterLockedSection();
                        return false;
                    } else if (displayContent.isUntrustedVirtualDisplay()) {
                        resetPriorityAfterLockedSection();
                        return false;
                    } else {
                        boolean supportsSystemDecorations = displayContent.supportsSystemDecorations();
                        resetPriorityAfterLockedSection();
                        return supportsSystemDecorations;
                    }
                } catch (Throwable th) {
                    while (true) {
                        resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        } else {
            throw new SecurityException("Requires INTERNAL_SYSTEM_WINDOW permission");
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void setShouldShowSystemDecors(int displayId, boolean shouldShow) {
        if (checkCallingPermission("android.permission.INTERNAL_SYSTEM_WINDOW", "setShouldShowSystemDecors()")) {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    DisplayContent displayContent = getDisplayContentOrCreate(displayId, (IBinder) null);
                    if (displayContent == null) {
                        Slog.w("WindowManager", "Attempted to set system decors flag to a display that does not exist: " + displayId);
                    } else if (!displayContent.isUntrustedVirtualDisplay()) {
                        this.mDisplayWindowSettings.setShouldShowSystemDecorsLocked(displayContent, shouldShow);
                        reconfigureDisplayLocked(displayContent);
                        resetPriorityAfterLockedSection();
                    } else {
                        throw new SecurityException("Attempted to set system decors flag to an untrusted virtual display: " + displayId);
                    }
                } finally {
                    resetPriorityAfterLockedSection();
                }
            }
        } else {
            throw new SecurityException("Requires INTERNAL_SYSTEM_WINDOW permission");
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:24:0x004d, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0050, code lost:
        return r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean shouldShowIme(int r7) {
        /*
            r6 = this;
            java.lang.String r0 = "android.permission.INTERNAL_SYSTEM_WINDOW"
            java.lang.String r1 = "shouldShowIme()"
            boolean r0 = r6.checkCallingPermission(r0, r1)
            if (r0 == 0) goto L_0x0057
            com.android.server.wm.WindowManagerGlobalLock r0 = r6.mGlobalLock
            monitor-enter(r0)
            boostPriorityForLockedSection()     // Catch:{ all -> 0x0051 }
            com.android.server.wm.RootWindowContainer r1 = r6.mRoot     // Catch:{ all -> 0x0051 }
            com.android.server.wm.DisplayContent r1 = r1.getDisplayContent(r7)     // Catch:{ all -> 0x0051 }
            r2 = 0
            if (r1 != 0) goto L_0x0034
            java.lang.String r3 = "WindowManager"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0051 }
            r4.<init>()     // Catch:{ all -> 0x0051 }
            java.lang.String r5 = "Attempted to get IME flag of a display that does not exist: "
            r4.append(r5)     // Catch:{ all -> 0x0051 }
            r4.append(r7)     // Catch:{ all -> 0x0051 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0051 }
            android.util.Slog.w(r3, r4)     // Catch:{ all -> 0x0051 }
            monitor-exit(r0)     // Catch:{ all -> 0x0051 }
            resetPriorityAfterLockedSection()
            return r2
        L_0x0034:
            boolean r3 = r1.isUntrustedVirtualDisplay()     // Catch:{ all -> 0x0051 }
            if (r3 == 0) goto L_0x003f
            monitor-exit(r0)     // Catch:{ all -> 0x0051 }
            resetPriorityAfterLockedSection()
            return r2
        L_0x003f:
            com.android.server.wm.DisplayWindowSettings r3 = r6.mDisplayWindowSettings     // Catch:{ all -> 0x0051 }
            boolean r3 = r3.shouldShowImeLocked(r1)     // Catch:{ all -> 0x0051 }
            if (r3 != 0) goto L_0x004b
            boolean r3 = r6.mForceDesktopModeOnExternalDisplays     // Catch:{ all -> 0x0051 }
            if (r3 == 0) goto L_0x004c
        L_0x004b:
            r2 = 1
        L_0x004c:
            monitor-exit(r0)     // Catch:{ all -> 0x0051 }
            resetPriorityAfterLockedSection()
            return r2
        L_0x0051:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0051 }
            resetPriorityAfterLockedSection()
            throw r1
        L_0x0057:
            java.lang.SecurityException r0 = new java.lang.SecurityException
            java.lang.String r1 = "Requires INTERNAL_SYSTEM_WINDOW permission"
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.shouldShowIme(int):boolean");
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    public void setShouldShowIme(int displayId, boolean shouldShow) {
        if (checkCallingPermission("android.permission.INTERNAL_SYSTEM_WINDOW", "setShouldShowIme()")) {
            synchronized (this.mGlobalLock) {
                try {
                    boostPriorityForLockedSection();
                    DisplayContent displayContent = getDisplayContentOrCreate(displayId, (IBinder) null);
                    if (displayContent == null) {
                        Slog.w("WindowManager", "Attempted to set IME flag to a display that does not exist: " + displayId);
                    } else if (!displayContent.isUntrustedVirtualDisplay()) {
                        this.mDisplayWindowSettings.setShouldShowImeLocked(displayContent, shouldShow);
                        reconfigureDisplayLocked(displayContent);
                        resetPriorityAfterLockedSection();
                    } else {
                        throw new SecurityException("Attempted to set IME flag to an untrusted virtual display: " + displayId);
                    }
                } finally {
                    resetPriorityAfterLockedSection();
                }
            }
        } else {
            throw new SecurityException("Requires INTERNAL_SYSTEM_WINDOW permission");
        }
    }

    public void registerShortcutKey(long shortcutCode, IShortcutService shortcutKeyReceiver) throws RemoteException {
        if (checkCallingPermission("android.permission.REGISTER_WINDOW_MANAGER_LISTENERS", "registerShortcutKey")) {
            this.mPolicy.registerShortcutKey(shortcutCode, shortcutKeyReceiver);
            return;
        }
        throw new SecurityException("Requires REGISTER_WINDOW_MANAGER_LISTENERS permission");
    }

    public void requestUserActivityNotification() {
        if (checkCallingPermission("android.permission.USER_ACTIVITY", "requestUserActivityNotification()")) {
            this.mPolicy.requestUserActivityNotification();
            return;
        }
        throw new SecurityException("Requires USER_ACTIVITY permission");
    }

    /* access modifiers changed from: package-private */
    public void markForSeamlessRotation(WindowState w, boolean seamlesslyRotated) {
        if (seamlesslyRotated != w.mSeamlesslyRotated && !w.mForceSeamlesslyRotate) {
            w.mSeamlesslyRotated = seamlesslyRotated;
            if (seamlesslyRotated) {
                this.mSeamlessRotationCount++;
            } else {
                this.mSeamlessRotationCount--;
            }
            if (this.mSeamlessRotationCount == 0) {
                finishSeamlessRotation();
                w.getDisplayContent().updateRotationAndSendNewConfigIfNeeded();
            }
        }
    }

    private final class LocalService extends WindowManagerInternal {
        private LocalService() {
        }

        public void setHoldOn(IBinder token, boolean holdOn) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    AppWindowToken appWindow = WindowManagerService.this.mRoot.getAppWindowToken(token);
                    if (appWindow != null) {
                        appWindow.mIsHoldOn = holdOn;
                    }
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void requestTraversalFromDisplayManager() {
            WindowManagerService.this.requestTraversal();
        }

        public boolean getCastRotationMode() {
            return WindowManagerService.this.mIsCastModeRotationChanged;
        }

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        /* JADX INFO: finally extract failed */
        public void setMagnificationSpec(int displayId, MagnificationSpec spec) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (WindowManagerService.this.mAccessibilityController != null) {
                        WindowManagerService.this.mAccessibilityController.setMagnificationSpecLocked(displayId, spec);
                    } else {
                        throw new IllegalStateException("Magnification callbacks not set!");
                    }
                } catch (Throwable th) {
                    WindowManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            if (Binder.getCallingPid() != Process.myPid()) {
                spec.recycle();
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        public void setForceShowMagnifiableBounds(int displayId, boolean show) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (WindowManagerService.this.mAccessibilityController != null) {
                        WindowManagerService.this.mAccessibilityController.setForceShowMagnifiableBoundsLocked(displayId, show);
                    } else {
                        throw new IllegalStateException("Magnification callbacks not set!");
                    }
                } finally {
                    WindowManagerService.resetPriorityAfterLockedSection();
                }
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 3 */
        public void getMagnificationRegion(int displayId, Region magnificationRegion) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (WindowManagerService.this.mAccessibilityController != null) {
                        WindowManagerService.this.mAccessibilityController.getMagnificationRegionLocked(displayId, magnificationRegion);
                    } else {
                        throw new IllegalStateException("Magnification callbacks not set!");
                    }
                } finally {
                    WindowManagerService.resetPriorityAfterLockedSection();
                }
            }
        }

        public MagnificationSpec getCompatibleMagnificationSpecForWindow(IBinder windowToken) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowState windowState = (WindowState) WindowManagerService.this.mWindowMap.get(windowToken);
                    if (windowState == null) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return null;
                    }
                    MagnificationSpec spec = null;
                    if (WindowManagerService.this.mAccessibilityController != null) {
                        spec = WindowManagerService.this.mAccessibilityController.getMagnificationSpecForWindowLocked(windowState);
                    }
                    if ((spec == null || spec.isNop()) && windowState.mGlobalScale == 1.0f) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return null;
                    }
                    MagnificationSpec spec2 = spec == null ? MagnificationSpec.obtain() : MagnificationSpec.obtain(spec);
                    spec2.scale *= windowState.mGlobalScale;
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return spec2;
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        }

        public boolean setMagnificationCallbacks(int displayId, WindowManagerInternal.MagnificationCallbacks callbacks) {
            boolean result;
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (WindowManagerService.this.mAccessibilityController == null) {
                        WindowManagerService.this.mAccessibilityController = new AccessibilityController(WindowManagerService.this);
                    }
                    result = WindowManagerService.this.mAccessibilityController.setMagnificationCallbacksLocked(displayId, callbacks);
                    if (!WindowManagerService.this.mAccessibilityController.hasCallbacksLocked()) {
                        WindowManagerService.this.mAccessibilityController = null;
                    }
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return result;
        }

        public void setWindowsForAccessibilityCallback(WindowManagerInternal.WindowsForAccessibilityCallback callback) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (WindowManagerService.this.mAccessibilityController == null) {
                        WindowManagerService.this.mAccessibilityController = new AccessibilityController(WindowManagerService.this);
                    }
                    WindowManagerService.this.mAccessibilityController.setWindowsForAccessibilityCallback(callback);
                    if (!WindowManagerService.this.mAccessibilityController.hasCallbacksLocked()) {
                        WindowManagerService.this.mAccessibilityController = null;
                    }
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void setBsInputFilter(IInputFilter filter) {
            WindowManagerService.this.mInputManager.setBsInputFilter(filter);
        }

        public void setInputFilter(IInputFilter filter) {
            WindowManagerService.this.mInputManager.setInputFilter(filter);
        }

        public IBinder getFocusedWindowToken() {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowState windowState = WindowManagerService.this.getFocusedWindowLocked();
                    if (windowState != null) {
                        IBinder asBinder = windowState.mClient.asBinder();
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return asBinder;
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return null;
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        }

        public boolean isKeyguardLocked() {
            return WindowManagerService.this.isKeyguardLocked();
        }

        public boolean isKeyguardShowingAndNotOccluded() {
            return WindowManagerService.this.isKeyguardShowingAndNotOccluded();
        }

        public void showGlobalActions() {
            WindowManagerService.this.showGlobalActions();
        }

        public void getWindowFrame(IBinder token, Rect outBounds) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowState windowState = (WindowState) WindowManagerService.this.mWindowMap.get(token);
                    if (windowState != null) {
                        outBounds.set(windowState.getFrameLw());
                    } else {
                        outBounds.setEmpty();
                    }
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void waitForAllWindowsDrawn(Runnable callback, long timeout) {
            boolean allWindowsDrawn = false;
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowManagerService.this.mWaitingForDrawnCallback = callback;
                    WindowManagerService.this.getDefaultDisplayContentLocked().waitForAllWindowsDrawn();
                    WindowManagerService.this.mWindowPlacerLocked.requestTraversal();
                    WindowManagerService.this.mH.removeMessages(24);
                    if (WindowManagerService.this.mWaitingForDrawn.isEmpty()) {
                        allWindowsDrawn = true;
                    } else {
                        WindowManagerService.this.mH.sendEmptyMessageDelayed(24, timeout);
                        WindowManagerService.this.checkDrawnWindowsLocked();
                    }
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            if (allWindowsDrawn) {
                callback.run();
            }
        }

        public void setForcedDisplaySize(int displayId, int width, int height) {
            WindowManagerService.this.setForcedDisplaySize(displayId, width, height);
        }

        public void clearForcedDisplaySize(int displayId) {
            WindowManagerService.this.clearForcedDisplaySize(displayId);
        }

        public void addWindowToken(IBinder token, int type, int displayId) {
            WindowManagerService.this.addWindowToken(token, type, displayId);
        }

        public void removeWindowToken(IBinder binder, boolean removeWindows, int displayId) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    if (removeWindows) {
                        DisplayContent dc = WindowManagerService.this.mRoot.getDisplayContent(displayId);
                        if (dc == null) {
                            Slog.w("WindowManager", "removeWindowToken: Attempted to remove token: " + binder + " for non-exiting displayId=" + displayId);
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return;
                        }
                        WindowToken token = dc.removeWindowToken(binder);
                        if (token == null) {
                            Slog.w("WindowManager", "removeWindowToken: Attempted to remove non-existing token: " + binder);
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return;
                        }
                        token.removeAllWindowsIfPossible();
                    }
                    WindowManagerService.this.removeWindowToken(binder, displayId);
                    WindowManagerService.resetPriorityAfterLockedSection();
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        }

        public void registerAppTransitionListener(WindowManagerInternal.AppTransitionListener listener) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowManagerService.this.getDefaultDisplayContentLocked().mAppTransition.registerListenerLocked(listener);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void reportPasswordChanged(int userId) {
            WindowManagerService.this.mKeyguardDisableHandler.updateKeyguardEnabled(userId);
        }

        public int getInputMethodWindowVisibleHeight(int displayId) {
            int inputMethodWindowVisibleHeight;
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    inputMethodWindowVisibleHeight = WindowManagerService.this.mRoot.getDisplayContent(displayId).mDisplayFrames.getInputMethodWindowVisibleHeight();
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return inputMethodWindowVisibleHeight;
        }

        public void updateInputMethodWindowStatus(IBinder imeToken, boolean imeWindowVisible, boolean dismissImeOnBackKeyPressed) {
            WindowManagerService.this.mPolicy.setDismissImeOnBackKeyPressed(dismissImeOnBackKeyPressed);
        }

        public void updateInputMethodTargetWindow(IBinder imeToken, IBinder imeTargetWindowToken) {
        }

        public boolean isHardKeyboardAvailable() {
            boolean z;
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    z = WindowManagerService.this.mHardKeyboardAvailable;
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return z;
        }

        public void setOnHardKeyboardStatusChangeListener(WindowManagerInternal.OnHardKeyboardStatusChangeListener listener) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowManagerService.this.mHardKeyboardStatusChangeListener = listener;
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public boolean isStackVisibleLw(int windowingMode) {
            return WindowManagerService.this.getDefaultDisplayContentLocked().isStackVisible(windowingMode);
        }

        public void setTalkbackMode(boolean enable) {
            synchronized (WindowManagerService.this.mWindowMap) {
                WindowManagerService.this.updateTalkbackWatermark(enable);
            }
        }

        public void computeWindowsForAccessibility() {
            AccessibilityController accessibilityController;
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    accessibilityController = WindowManagerService.this.mAccessibilityController;
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            if (accessibilityController != null) {
                accessibilityController.performComputeChangedWindowsNotLocked(true);
            }
        }

        public void setVr2dDisplayId(int vr2dDisplayId) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowManagerService.this.mVr2dDisplayId = vr2dDisplayId;
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void registerDragDropControllerCallback(WindowManagerInternal.IDragDropCallback callback) {
            WindowManagerService.this.mDragDropController.registerCallback(callback);
        }

        public void lockNow() {
            WindowManagerService.this.lockNow((Bundle) null);
        }

        public int getWindowOwnerUserId(IBinder token) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowState window = (WindowState) WindowManagerService.this.mWindowMap.get(token);
                    if (window != null) {
                        int userId = UserHandle.getUserId(window.mOwnerUid);
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return userId;
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        }

        public boolean isUidFocused(int uid) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    int i = WindowManagerService.this.mRoot.getChildCount() - 1;
                    while (i >= 0) {
                        DisplayContent displayContent = (DisplayContent) WindowManagerService.this.mRoot.getChildAt(i);
                        if (displayContent.mCurrentFocus == null || uid != displayContent.mCurrentFocus.getOwningUid()) {
                            i--;
                        } else {
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return true;
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        }

        public boolean isInputMethodClientFocus(int uid, int pid, int displayId) {
            if (displayId == -1) {
                return false;
            }
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    DisplayContent displayContent = WindowManagerService.this.mRoot.getTopFocusedDisplayContent();
                    if (displayContent != null && displayContent.getDisplayId() == displayId) {
                        if (displayContent.hasAccess(uid)) {
                            if (displayContent.isInputMethodClientFocus(uid, pid)) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                return true;
                            }
                            WindowState currentFocus = displayContent.mCurrentFocus;
                            if (currentFocus != null && currentFocus.mSession.mUid == uid && currentFocus.mSession.mPid == pid) {
                                WindowManagerService.resetPriorityAfterLockedSection();
                                return true;
                            }
                            WindowManagerService.resetPriorityAfterLockedSection();
                            return false;
                        }
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return false;
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        }

        public boolean isUidAllowedOnDisplay(int displayId, int uid) {
            boolean z = true;
            if (displayId == 0) {
                return true;
            }
            if (displayId == -1) {
                return false;
            }
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    DisplayContent displayContent = WindowManagerService.this.mRoot.getDisplayContent(displayId);
                    if (displayContent == null || !displayContent.hasAccess(uid)) {
                        z = false;
                    }
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return z;
        }

        public int getDisplayIdForWindow(IBinder windowToken) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowState window = (WindowState) WindowManagerService.this.mWindowMap.get(windowToken);
                    if (window != null) {
                        int displayId = window.getDisplayContent().getDisplayId();
                        WindowManagerService.resetPriorityAfterLockedSection();
                        return displayId;
                    }
                    WindowManagerService.resetPriorityAfterLockedSection();
                    return -1;
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        }

        public int getTopFocusedDisplayId() {
            int displayId;
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    displayId = WindowManagerService.this.mRoot.getTopFocusedDisplayContent().getDisplayId();
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return displayId;
        }

        public boolean shouldShowSystemDecorOnDisplay(int displayId) {
            boolean shouldShowSystemDecors;
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    shouldShowSystemDecors = WindowManagerService.this.shouldShowSystemDecors(displayId);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return shouldShowSystemDecors;
        }

        public boolean shouldShowIme(int displayId) {
            boolean shouldShowIme;
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    shouldShowIme = WindowManagerService.this.shouldShowIme(displayId);
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
            return shouldShowIme;
        }

        public void addNonHighRefreshRatePackage(String packageName) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowManagerService.this.mRoot.forAllDisplays(new Consumer(packageName) {
                        private final /* synthetic */ String f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final void accept(Object obj) {
                            ((DisplayContent) obj).getDisplayPolicy().getRefreshRatePolicy().addNonHighRefreshRatePackage(this.f$0);
                        }
                    });
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }

        public void removeNonHighRefreshRatePackage(String packageName) {
            synchronized (WindowManagerService.this.mGlobalLock) {
                try {
                    WindowManagerService.boostPriorityForLockedSection();
                    WindowManagerService.this.mRoot.forAllDisplays(new Consumer(packageName) {
                        private final /* synthetic */ String f$0;

                        {
                            this.f$0 = r1;
                        }

                        public final void accept(Object obj) {
                            ((DisplayContent) obj).getDisplayPolicy().getRefreshRatePolicy().removeNonHighRefreshRatePackage(this.f$0);
                        }
                    });
                } catch (Throwable th) {
                    while (true) {
                        WindowManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            WindowManagerService.resetPriorityAfterLockedSection();
        }
    }

    /* access modifiers changed from: package-private */
    public void registerAppFreezeListener(AppFreezeListener listener) {
        if (!this.mAppFreezeListeners.contains(listener)) {
            this.mAppFreezeListeners.add(listener);
        }
    }

    /* access modifiers changed from: package-private */
    public void unregisterAppFreezeListener(AppFreezeListener listener) {
        this.mAppFreezeListeners.remove(listener);
    }

    /* access modifiers changed from: package-private */
    public void inSurfaceTransaction(Runnable exec) {
        SurfaceControl.openTransaction();
        try {
            exec.run();
        } finally {
            SurfaceControl.closeTransaction();
        }
    }

    public void disableNonVrUi(boolean disable) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                boolean showAlertWindowNotifications = !disable;
                if (showAlertWindowNotifications == this.mShowAlertWindowNotifications) {
                    resetPriorityAfterLockedSection();
                    return;
                }
                this.mShowAlertWindowNotifications = showAlertWindowNotifications;
                for (int i = this.mSessions.size() - 1; i >= 0; i--) {
                    this.mSessions.valueAt(i).setShowingAlertWindowNotificationAllowed(this.mShowAlertWindowNotifications);
                }
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean hasWideColorGamutSupport() {
        if (!this.mHasWideColorGamutSupport || SystemProperties.getInt("persist.sys.sf.native_mode", 0) == 1) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean hasHdrSupport() {
        return this.mHasHdrSupport && hasWideColorGamutSupport();
    }

    /* access modifiers changed from: package-private */
    public void updateNonSystemOverlayWindowsVisibilityIfNeeded(WindowState win, boolean surfaceShown) {
        if (win.hideNonSystemOverlayWindowsWhenVisible() || this.mHidingNonSystemOverlayWindows.contains(win)) {
            boolean systemAlertWindowsHidden = !this.mHidingNonSystemOverlayWindows.isEmpty();
            if (!surfaceShown) {
                this.mHidingNonSystemOverlayWindows.remove(win);
            } else if (!this.mHidingNonSystemOverlayWindows.contains(win)) {
                this.mHidingNonSystemOverlayWindows.add(win);
            }
            boolean hideSystemAlertWindows = !this.mHidingNonSystemOverlayWindows.isEmpty();
            if (systemAlertWindowsHidden != hideSystemAlertWindows) {
                this.mRoot.forAllWindows((Consumer<WindowState>) new Consumer(hideSystemAlertWindows) {
                    private final /* synthetic */ boolean f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void accept(Object obj) {
                        ((WindowState) obj).setForceHideNonSystemOverlayWindowIfNeeded(this.f$0);
                    }
                }, false);
            }
        }
    }

    public void applyMagnificationSpecLocked(int displayId, MagnificationSpec spec) {
        DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
        if (displayContent != null) {
            displayContent.applyMagnificationSpec(spec);
        }
    }

    /* access modifiers changed from: package-private */
    public SurfaceControl.Builder makeSurfaceBuilder(SurfaceSession s) {
        return this.mSurfaceBuilderFactory.make(s);
    }

    /* access modifiers changed from: package-private */
    public void sendSetRunningRemoteAnimation(int pid, boolean runningRemoteAnimation) {
        this.mH.obtainMessage(59, pid, runningRemoteAnimation).sendToTarget();
    }

    /* access modifiers changed from: package-private */
    public void startSeamlessRotation() {
        this.mSeamlessRotationCount = 0;
        this.mRotatingSeamlessly = true;
    }

    /* access modifiers changed from: package-private */
    public boolean isRotatingSeamlessly() {
        return this.mRotatingSeamlessly;
    }

    /* access modifiers changed from: package-private */
    public void finishSeamlessRotation() {
        this.mRotatingSeamlessly = false;
    }

    /* access modifiers changed from: package-private */
    public void onLockTaskStateChanged(int lockTaskState) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mRoot.forAllDisplayPolicies(PooledLambda.obtainConsumer($$Lambda$5zz5Ugt4wxIXoNE3lZS6NA9z_Jk.INSTANCE, PooledLambda.__(), Integer.valueOf(lockTaskState)));
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void setAodShowing(boolean aodShowing) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                if (this.mPolicy.setAodShowing(aodShowing)) {
                    this.mWindowPlacerLocked.performSurfacePlacement();
                }
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public boolean injectInputAfterTransactionsApplied(InputEvent ev, int mode) {
        boolean isDown;
        KeyEvent keyEvent;
        boolean isMouseEvent = false;
        if (ev instanceof KeyEvent) {
            KeyEvent keyEvent2 = (KeyEvent) ev;
            isDown = keyEvent2.getAction() == 0;
            keyEvent = keyEvent2.getAction() == 1 ? 1 : null;
        } else {
            MotionEvent motionEvent = (MotionEvent) ev;
            isDown = motionEvent.getAction() == 0;
            keyEvent = motionEvent.getAction() == 1 ? 1 : null;
        }
        if (ev.getSource() == 8194) {
            isMouseEvent = true;
        }
        if (isDown || isMouseEvent) {
            syncInputTransactions();
        }
        boolean result = ((InputManagerInternal) LocalServices.getService(InputManagerInternal.class)).injectInputEvent(ev, mode);
        if (keyEvent != null) {
            syncInputTransactions();
        }
        return result;
    }

    public void syncInputTransactions() {
        waitForAnimationsToComplete();
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mWindowPlacerLocked.performSurfacePlacementIfScheduled();
                this.mRoot.forAllDisplays($$Lambda$WindowManagerService$QGTApvQkj7JVfTvOVrLJ6s24v8.INSTANCE);
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
        new SurfaceControl.Transaction().syncInputWindows().apply(true);
    }

    private void waitForAnimationsToComplete() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                long timeoutRemaining = 5000;
                while (this.mRoot.isSelfOrChildAnimating() && timeoutRemaining > 0) {
                    long startTime = System.currentTimeMillis();
                    try {
                        this.mGlobalLock.wait(timeoutRemaining);
                    } catch (InterruptedException e) {
                    }
                    timeoutRemaining -= System.currentTimeMillis() - startTime;
                }
                if (this.mRoot.isSelfOrChildAnimating()) {
                    Log.w("WindowManager", "Timed out waiting for animations to complete.");
                }
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    /* access modifiers changed from: package-private */
    public void onAnimationFinished() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mGlobalLock.notifyAll();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
        try {
            if (this.mUiModeAnimFinishedCallback != null) {
                this.mUiModeAnimFinishedCallback.onWindowAnimFinished();
            }
        } catch (RemoteException e) {
            Slog.e("WindowManager", "Call mUiModeAnimFinishedCallback.onWindowAnimFinished error " + e);
        }
    }

    /* access modifiers changed from: private */
    public void onPointerDownOutsideFocusLocked(IBinder touchedToken) {
        WindowState touchedWindow = windowForClientLocked((Session) null, touchedToken, false);
        if (touchedWindow != null && touchedWindow.canReceiveKeys()) {
            handleTaskFocusChange(touchedWindow.getTask());
            handleDisplayFocusChange(touchedWindow);
        }
    }

    private void handleTaskFocusChange(Task task) {
        if (task != null && !task.mStack.isActivityTypeHome()) {
            try {
                this.mActivityTaskManager.setFocusedTask(task.mTaskId);
            } catch (RemoteException e) {
            }
        }
    }

    private void handleDisplayFocusChange(WindowState window) {
        WindowContainer parent;
        DisplayContent displayContent = window.getDisplayContent();
        if (displayContent != null && window.canReceiveKeys() && (parent = displayContent.getParent()) != null && parent.getTopChild() != displayContent) {
            parent.positionChildAt(Integer.MAX_VALUE, displayContent, true);
            displayContent.mAcitvityDisplay.ensureActivitiesVisible((ActivityRecord) null, 0, false, true);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isAppTransitionSkipped() {
        return this.mMiuiGestureController.isAppTransitionSkipped();
    }

    public boolean isGestureOpen() {
        return this.mMiuiGestureController.isGestureOpen();
    }

    private void initMiuiFreeFormGestureController() {
        this.mMiuiFreeFormGestureController = new MiuiFreeFormGestureController(this);
    }

    private void initMiuiGestureController() {
        this.mMiuiGestureController = new MiuiGestureController(this, this.mAtmService);
        this.mAtmService.mGestureController = this.mMiuiGestureController;
    }

    public void registerMiuiGestureControlHelper(IMiuiGestureControlHelper helper) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mMiuiGestureController.registerMiuiGestureControlHelper(helper);
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void unregisterMiuiGestureControlHelper() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mMiuiGestureController.unregisterMiuiGestureControlHelper(this.mMiuiGestureController.mGestureHelperDeathRecipient);
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void registerMiuiFreeFormGestureControlHelper(IMiuiFreeFormGestureControlHelper helper) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mMiuiFreeFormGestureController.registerMiuiFreeFormGestureControlHelper(helper);
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void unregisterMiuiFreeFormGestureControlHelper() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mMiuiFreeFormGestureController.unregisterMiuiFreeFormGestureControlHelper();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public int getCurrentFreeFormWindowMode() {
        if (MiuiMultiWindowUtils.mIsMiniFreeformMode) {
            return 1;
        }
        MiuiFreeFormGestureController miuiFreeFormGestureController = this.mMiuiFreeFormGestureController;
        if (miuiFreeFormGestureController != null) {
            return miuiFreeFormGestureController.getCurrentFreeFormWindowMode();
        }
        return -1;
    }

    public void setFreeformPackageName(String packageName) {
        this.mMiuiFreeFormGestureController.setFreeformPackageName(packageName);
    }

    public boolean isFreeformCouldBeenFocusWindow() {
        return this.mMiuiFreeFormGestureController.isFreeformCouldBeenFocusWindow();
    }

    public void createFreeformSurfaceCompleted(WindowState window) {
        this.mMiuiFreeFormGestureController.createFreeformSurfaceCompleted(window);
    }

    public void removeFreeformSurface() {
        this.mMiuiFreeFormGestureController.removeFreeformSurface();
    }

    /* access modifiers changed from: package-private */
    public void launchSmallFreeFormWindow() {
        this.mMiuiFreeFormGestureController.launchSmallFreeFormWindow();
    }

    public void setLoadBackHomeAnimation(boolean loadBackHomeAnimation, int displayId) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                if (displayContent == null) {
                    Slog.w("WindowManager", "Attempted to call setLoadBackHomeAnimation for the display " + displayId + " that does not exist.");
                    resetPriorityAfterLockedSection();
                    return;
                }
                displayContent.mAppTransition.setLoadBackHomeAnimation(loadBackHomeAnimation);
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public void setLoadRoundedViewAnimation(boolean loadRoundedViewAnimation, boolean scaleBackToScreenCenter, int displayId) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                if (displayContent == null) {
                    Slog.w("WindowManager", "Attempted to call setLoadBackHomeAnimation for the display " + displayId + " that does not exist.");
                    resetPriorityAfterLockedSection();
                    return;
                }
                displayContent.mAppTransition.setLoadRoundedViewAnimation(loadRoundedViewAnimation, scaleBackToScreenCenter);
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public void overrideMiuiAnimationInfo(GraphicBuffer icon, Rect rect, int displayId) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                if (displayContent == null) {
                    Slog.w("WindowManager", "Attempted to call overrideMiuiAnimationInfo for the display " + displayId + " that does not exist.");
                    resetPriorityAfterLockedSection();
                    return;
                }
                displayContent.mAppTransition.overrideMiuiAnimationInfo(icon, rect);
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public void overrideMiuiRoundedViewAnimationInfo(GraphicBuffer icon, Rect rect, int radius, int foreGroundColor, IRemoteCallback animationReenterStartedCallback, IRemoteCallback animationReenterFinishedCallback, int displayId) {
        int i = displayId;
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(i);
                if (displayContent == null) {
                    Slog.w("WindowManager", "Attempted to call overrideMiuiRoundedViewAnimationInfo for the display " + i + " that does not exist.");
                    resetPriorityAfterLockedSection();
                    return;
                }
                displayContent.mAppTransition.overrideMiuiRoundedViewAnimationInfo(icon, rect, radius, foreGroundColor, animationReenterStartedCallback, animationReenterFinishedCallback);
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:17:0x004e, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0051, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setMiuiAppTransitionAnimationHelper(com.miui.internal.transition.IMiuiAppTransitionAnimationHelper r6, int r7) {
        /*
            r5 = this;
            com.android.server.wm.WindowManagerGlobalLock r0 = r5.mGlobalLock
            monitor-enter(r0)
            boostPriorityForLockedSection()     // Catch:{ all -> 0x0052 }
            r5.mMiuiAppTransitionAnimationHelper = r6     // Catch:{ all -> 0x0052 }
            com.android.server.wm.MiuiGestureController r1 = r5.mMiuiGestureController     // Catch:{ all -> 0x0052 }
            if (r1 == 0) goto L_0x0011
            com.android.server.wm.MiuiGestureController r1 = r5.mMiuiGestureController     // Catch:{ all -> 0x0052 }
            r1.setMiuiAppTransitionAnimationHelper(r6)     // Catch:{ all -> 0x0052 }
        L_0x0011:
            com.miui.internal.transition.IMiuiAppTransitionAnimationHelper r1 = r5.mMiuiAppTransitionAnimationHelper     // Catch:{ all -> 0x0052 }
            if (r1 != 0) goto L_0x004d
            com.android.server.wm.RootWindowContainer r1 = r5.mRoot     // Catch:{ all -> 0x0052 }
            com.android.server.wm.DisplayContent r1 = r1.getDisplayContent(r7)     // Catch:{ all -> 0x0052 }
            if (r1 != 0) goto L_0x003d
            java.lang.String r2 = "WindowManager"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0052 }
            r3.<init>()     // Catch:{ all -> 0x0052 }
            java.lang.String r4 = "Attempted to call setMiuiAppTransitionAnimationHelper for the display "
            r3.append(r4)     // Catch:{ all -> 0x0052 }
            r3.append(r7)     // Catch:{ all -> 0x0052 }
            java.lang.String r4 = " that does not exist."
            r3.append(r4)     // Catch:{ all -> 0x0052 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0052 }
            android.util.Slog.w(r2, r3)     // Catch:{ all -> 0x0052 }
            monitor-exit(r0)     // Catch:{ all -> 0x0052 }
            resetPriorityAfterLockedSection()
            return
        L_0x003d:
            com.android.server.wm.AppTransition r2 = r1.mAppTransition     // Catch:{ all -> 0x0052 }
            r3 = 0
            r2.overrideMiuiAnimationInfo(r3, r3)     // Catch:{ all -> 0x0052 }
            com.android.server.wm.AppTransition r2 = r1.mAppTransition     // Catch:{ all -> 0x0052 }
            r2.clearNextAppTransitionBackHomeType()     // Catch:{ all -> 0x0052 }
            com.android.server.wm.AppTransitionController r2 = r1.mAppTransitionController     // Catch:{ all -> 0x0052 }
            r2.cancelMiuiThumbnailAnimationLocked()     // Catch:{ all -> 0x0052 }
        L_0x004d:
            monitor-exit(r0)     // Catch:{ all -> 0x0052 }
            resetPriorityAfterLockedSection()
            return
        L_0x0052:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0052 }
            resetPriorityAfterLockedSection()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.setMiuiAppTransitionAnimationHelper(com.miui.internal.transition.IMiuiAppTransitionAnimationHelper, int):void");
    }

    public void setIsInMultiWindowMode(boolean isInMultiWindowMode) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mIsInMultiWindowMode = isInMultiWindowMode;
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public void startFetchingAppTransitionSpecs(int displayId) {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                DisplayContent displayContent = this.mRoot.getDisplayContent(displayId);
                if (displayContent == null) {
                    Slog.w("WindowManager", "Attempted to call startFetchingAppTransitionSpecs for the display " + displayId + " that does not exist.");
                    resetPriorityAfterLockedSection();
                    return;
                }
                displayContent.mAppTransition.mNextAppTransitionAnimationsSpecsPending = true;
                this.mH.removeMessages(102);
                Message message = Message.obtain();
                message.arg1 = displayId;
                message.what = 102;
                this.mH.sendMessageDelayed(message, (long) AppTransitionInjector.APP_TRANSITION_SPECS_PENDING_TIMEOUT);
                resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0044, code lost:
        resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0047, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void finishFetchingAppTransitionSpecs(int r6) {
        /*
            r5 = this;
            com.android.server.wm.WindowManagerGlobalLock r0 = r5.mGlobalLock
            monitor-enter(r0)
            boostPriorityForLockedSection()     // Catch:{ all -> 0x0048 }
            com.android.server.wm.RootWindowContainer r1 = r5.mRoot     // Catch:{ all -> 0x0048 }
            com.android.server.wm.DisplayContent r1 = r1.getDisplayContent(r6)     // Catch:{ all -> 0x0048 }
            if (r1 != 0) goto L_0x002e
            java.lang.String r2 = "WindowManager"
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0048 }
            r3.<init>()     // Catch:{ all -> 0x0048 }
            java.lang.String r4 = "Attempted to call finishFetchingAppTransitionSpecs for the display "
            r3.append(r4)     // Catch:{ all -> 0x0048 }
            r3.append(r6)     // Catch:{ all -> 0x0048 }
            java.lang.String r4 = " that does not exist."
            r3.append(r4)     // Catch:{ all -> 0x0048 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0048 }
            android.util.Slog.w(r2, r3)     // Catch:{ all -> 0x0048 }
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            resetPriorityAfterLockedSection()
            return
        L_0x002e:
            com.android.server.wm.AppTransition r2 = r1.mAppTransition     // Catch:{ all -> 0x0048 }
            boolean r2 = r2.mNextAppTransitionAnimationsSpecsPending     // Catch:{ all -> 0x0048 }
            if (r2 == 0) goto L_0x0043
            com.android.server.wm.WindowManagerService$H r2 = r5.mH     // Catch:{ all -> 0x0048 }
            r3 = 102(0x66, float:1.43E-43)
            r2.removeMessages(r3)     // Catch:{ all -> 0x0048 }
            com.android.server.wm.AppTransition r2 = r1.mAppTransition     // Catch:{ all -> 0x0048 }
            r3 = 0
            r2.mNextAppTransitionAnimationsSpecsPending = r3     // Catch:{ all -> 0x0048 }
            r5.requestTraversal()     // Catch:{ all -> 0x0048 }
        L_0x0043:
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            resetPriorityAfterLockedSection()
            return
        L_0x0048:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0048 }
            resetPriorityAfterLockedSection()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.WindowManagerService.finishFetchingAppTransitionSpecs(int):void");
    }

    public SurfaceControl getTaskStackContainersSurfaceControl() {
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                TaskStack topStack = getDefaultDisplayContentLocked().getTopStack();
                if (topStack != null) {
                    SurfaceControl parentSurfaceControl = topStack.getParentSurfaceControl();
                    resetPriorityAfterLockedSection();
                    return parentSurfaceControl;
                }
                resetPriorityAfterLockedSection();
                return null;
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public void registerUiModeAnimFinishedCallback(IWindowAnimationFinishedCallback callback) {
        this.mUiModeAnimFinishedCallback = callback;
    }

    public void setCastStackId(int stackId) {
        this.mCastStackId = stackId;
    }

    public void setCastMode(boolean enable) {
        this.mIsCastMode = enable;
        if (!this.mIsCastMode && this.mIsCastModeRotationChanged) {
            this.mIsCastModeRotationChanged = false;
            this.mCastRotation = -1;
            this.mDisplayManagerInternal.performTraversal(new SurfaceControl.Transaction());
        }
    }

    public void setRotationChangeForCastMode(int oldRotation, int newRotation) {
        if (this.mIsCastMode && this.mCastRotation == -1) {
            this.mIsCastModeRotationChanged = true;
            this.mCastRotation = oldRotation;
        } else if (!this.mIsCastMode) {
            this.mIsCastModeRotationChanged = false;
            this.mCastRotation = -1;
        }
        WindowState windowState = this.mCastWindow;
        if (windowState != null) {
            windowState.reportRotationChanged(this.mIsCastModeRotationChanged);
            try {
                this.mActivityTaskManager.castRotationChanged(this.mIsCastModeRotationChanged);
            } catch (RemoteException e) {
            }
            if (!this.mIsCastMode) {
                this.mCastWindow = null;
            }
        }
    }

    public void setCastWindow(WindowState w) {
        this.mCastWindow = w;
    }

    public int getCastStackId() {
        return this.mCastStackId;
    }

    public boolean getCastRotationMode() {
        return this.mIsCastModeRotationChanged;
    }

    public void setScreenProjectionList(int isScreenProjectionOnOrOff, int isScreenProjectionPrivace) {
        if (isScreenProjectionOnOrOff <= 0 || isScreenProjectionPrivace <= 0) {
            this.mIsInScreenProjection = false;
        } else {
            this.mIsInScreenProjection = true;
        }
        ArrayList<WindowState> windows = new ArrayList<>();
        synchronized (this.mGlobalLock) {
            try {
                boostPriorityForLockedSection();
                this.mRoot.forAllWindows((Consumer<WindowState>) new Consumer(windows) {
                    private final /* synthetic */ ArrayList f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void accept(Object obj) {
                        this.f$0.add((WindowState) obj);
                    }
                }, false);
                Iterator<WindowState> it = windows.iterator();
                while (it.hasNext()) {
                    WindowState win = it.next();
                    if (!(win == null || win.mWinAnimator == null || win.mWinAnimator.mSurfaceController == null || win.mWinAnimator.mSurfaceController.mSurfaceControl == null)) {
                        boolean tmpPrivacy = setSurfaceProjectionFlags(win.mWinAnimator.mSurfaceController.mSurfaceControl, (win.getAttrs() == null || "".equals(win.getAttrs().getTitle())) ? null : win.getAttrs().getTitle().toString());
                        if (win.mIsPrivacy != tmpPrivacy) {
                            win.mIsPrivacy = tmpPrivacy;
                            win.setDimmerPrivacyFlag(tmpPrivacy);
                        }
                    }
                }
            } catch (Throwable th) {
                while (true) {
                    resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        resetPriorityAfterLockedSection();
    }

    public boolean setSurfaceProjectionFlags(SurfaceControl surfaceControl, String name) {
        if (name == null) {
            return false;
        }
        ArrayList<String> list = WindowManagerServiceInjector.getProjectionBlackList();
        SurfaceControl.Transaction t = new SurfaceControl.Transaction();
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            String blackTitle = it.next();
            if (this.mIsInScreenProjection) {
                if (name.contains(blackTitle)) {
                    if (WindowManagerServiceInjector.getLastFrame(name)) {
                        t.setScreenProjection(surfaceControl, 16384);
                    } else {
                        t.setScreenProjection(surfaceControl, DumpState.DUMP_SERVICE_PERMISSIONS);
                    }
                    t.apply();
                    return true;
                }
            } else if (name.contains(blackTitle)) {
                t.setScreenProjection(surfaceControl, 0);
                t.apply();
                this.mIsLastFrameWin = null;
                return false;
            }
        }
        return false;
    }

    public void setLastFrame(boolean isLastFrame) {
        WindowState windowState;
        SurfaceControl.Transaction t = new SurfaceControl.Transaction();
        this.mRoot.forAllWindows((Consumer<WindowState>) new Consumer() {
            public final void accept(Object obj) {
                WindowManagerService.this.lambda$setLastFrame$14$WindowManagerService((WindowState) obj);
            }
        }, false);
        if (!this.mIsInScreenProjection || (windowState = this.mIsLastFrameWin) == null) {
            WindowState windowState2 = this.mIsLastFrameWin;
            if (windowState2 != null) {
                t.setLastFrame(windowState2.mWinAnimator.mSurfaceController.mSurfaceControl, false);
                t.apply();
                this.mIsLastFrameWin = null;
                return;
            }
            return;
        }
        t.setLastFrame(windowState.mWinAnimator.mSurfaceController.mSurfaceControl, isLastFrame);
        t.apply();
    }

    public /* synthetic */ void lambda$setLastFrame$14$WindowManagerService(WindowState w) {
        if (w.mIsWallpaper) {
            this.mIsLastFrameWin = w;
        }
    }
}
