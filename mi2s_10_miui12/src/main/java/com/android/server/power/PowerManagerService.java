package com.android.server.power;

import android.app.ActivityManager;
import android.app.SynchronousUserSwitchObserver;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.SensorManager;
import android.hardware.SystemSensorManager;
import android.hardware.display.AmbientDisplayConfiguration;
import android.hardware.display.DisplayManagerInternal;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.BatteryManagerInternal;
import android.os.BatterySaverPolicyConfig;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.IPowerManager;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManagerInternal;
import android.os.PowerSaveState;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.os.UserManager;
import android.os.WorkSource;
import android.provider.Settings;
import android.server.am.SplitScreenReporter;
import android.service.dreams.DreamManagerInternal;
import android.service.vr.IVrManager;
import android.service.vr.IVrStateCallbacks;
import android.util.KeyValueListParser;
import android.util.PrintWriterPrinter;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import android.view.Display;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IAppOpsService;
import com.android.internal.app.IBatteryStats;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.DumpUtils;
import com.android.server.EventLogTags;
import com.android.server.LockGuard;
import com.android.server.ServiceThread;
import com.android.server.SystemService;
import com.android.server.Watchdog;
import com.android.server.am.ActivityManagerServiceInjector;
import com.android.server.am.BatteryStatsService;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.display.ScreenEffectService;
import com.android.server.job.controllers.JobStatus;
import com.android.server.lights.Light;
import com.android.server.lights.LightsManager;
import com.android.server.policy.PhoneWindowManager;
import com.android.server.policy.WindowManagerPolicy;
import com.android.server.power.batterysaver.BatterySaverController;
import com.android.server.power.batterysaver.BatterySaverPolicy;
import com.android.server.power.batterysaver.BatterySaverStateMachine;
import com.android.server.power.batterysaver.BatterySavingStats;
import com.android.server.utils.PriorityDump;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.Vector;

public final class PowerManagerService extends SystemService implements Watchdog.Monitor {
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_SPEW = false;
    private static final int DEFAULT_DOUBLE_TAP_TO_WAKE = 0;
    private static final int DEFAULT_SCREEN_OFF_TIMEOUT = 15000;
    private static final int DEFAULT_SLEEP_TIMEOUT = -1;
    private static final int DIRTY_ACTUAL_DISPLAY_POWER_STATE_UPDATED = 8;
    private static final int DIRTY_BATTERY_STATE = 256;
    private static final int DIRTY_BOOT_COMPLETED = 16;
    private static final int DIRTY_DOCK_STATE = 1024;
    private static final int DIRTY_IS_POWERED = 64;
    private static final int DIRTY_PROXIMITY_POSITIVE = 512;
    private static final int DIRTY_QUIESCENT = 4096;
    private static final int DIRTY_SCREEN_BRIGHTNESS_BOOST = 2048;
    private static final int DIRTY_SETTINGS = 32;
    private static final int DIRTY_STAY_ON = 128;
    private static final int DIRTY_USER_ACTIVITY = 4;
    private static final int DIRTY_VR_MODE_CHANGED = 8192;
    private static final int DIRTY_WAKEFULNESS = 2;
    private static final int DIRTY_WAKE_LOCKS = 1;
    private static final int HALT_MODE_REBOOT = 1;
    private static final int HALT_MODE_REBOOT_SAFE_MODE = 2;
    private static final int HALT_MODE_SHUTDOWN = 0;
    static final long MIN_LONG_WAKE_CHECK_INTERVAL = 60000;
    private static final int MSG_CHECK_FOR_LONG_WAKELOCKS = 4;
    private static final int MSG_SANDMAN = 2;
    private static final int MSG_SCREEN_BRIGHTNESS_BOOST_TIMEOUT = 3;
    private static final int MSG_USER_ACTIVITY_TIMEOUT = 1;
    private static final int POWER_FEATURE_DOUBLE_TAP_TO_WAKE = 1;
    private static final String REASON_BATTERY_THERMAL_STATE = "shutdown,thermal,battery";
    private static final String REASON_LOW_BATTERY = "shutdown,battery";
    private static final String REASON_REBOOT = "reboot";
    private static final String REASON_SHUTDOWN = "shutdown";
    private static final String REASON_THERMAL_SHUTDOWN = "shutdown,thermal";
    private static final String REASON_USERREQUESTED = "shutdown,userrequested";
    private static final String REBOOT_PROPERTY = "sys.boot.reason";
    private static final int SCREEN_BRIGHTNESS_BOOST_TIMEOUT = 5000;
    private static final int SCREEN_ON_LATENCY_WARNING_MS = 200;
    private static final String SYSTEM_PROPERTY_QUIESCENT = "ro.boot.quiescent";
    private static final String SYSTEM_PROPERTY_RETAIL_DEMO_ENABLED = "sys.retaildemo.enabled";
    private static final String TAG = "PowerManagerService";
    private static final String TRACE_SCREEN_ON = "Screen turning on";
    private static final int USER_ACTIVITY_SCREEN_BRIGHT = 1;
    private static final int USER_ACTIVITY_SCREEN_DIM = 2;
    private static final int USER_ACTIVITY_SCREEN_DREAM = 4;
    private static final int WAKE_LOCK_BUTTON_BRIGHT = 8;
    private static final int WAKE_LOCK_CPU = 1;
    private static final int WAKE_LOCK_DOZE = 64;
    private static final int WAKE_LOCK_DRAW = 128;
    private static final int WAKE_LOCK_PROXIMITY_SCREEN_OFF = 16;
    private static final int WAKE_LOCK_SCREEN_BRIGHT = 2;
    private static final int WAKE_LOCK_SCREEN_DIM = 4;
    private static final int WAKE_LOCK_STAY_AWAKE = 32;
    private static boolean sQuiescent;
    private boolean mActivityManagerReady;
    private boolean mAlwaysOnEnabled;
    private final AmbientDisplayConfiguration mAmbientDisplayConfiguration;
    private IAppOpsService mAppOps;
    private final AttentionDetector mAttentionDetector;
    private Light mAttentionLight;
    private int mBatteryLevel;
    private boolean mBatteryLevelLow;
    private int mBatteryLevelWhenDreamStarted;
    private BatteryManagerInternal mBatteryManagerInternal;
    /* access modifiers changed from: private */
    public final BatterySaverController mBatterySaverController;
    /* access modifiers changed from: private */
    public final BatterySaverPolicy mBatterySaverPolicy;
    /* access modifiers changed from: private */
    public final BatterySaverStateMachine mBatterySaverStateMachine;
    private final BatterySavingStats mBatterySavingStats;
    private IBatteryStats mBatteryStats;
    private final BinderService mBinderService;
    private boolean mBootCompleted;
    final Constants mConstants;
    /* access modifiers changed from: private */
    public final Context mContext;
    /* access modifiers changed from: private */
    public boolean mDecoupleHalAutoSuspendModeFromDisplayConfig;
    /* access modifiers changed from: private */
    public boolean mDecoupleHalInteractiveModeFromDisplayConfig;
    private boolean mDeviceIdleMode;
    int[] mDeviceIdleTempWhitelist;
    int[] mDeviceIdleWhitelist;
    private int mDirty;
    /* access modifiers changed from: private */
    public DisplayManagerInternal mDisplayManagerInternal;
    private final DisplayManagerInternal.DisplayPowerCallbacks mDisplayPowerCallbacks;
    private final DisplayManagerInternal.DisplayPowerRequest mDisplayPowerRequest;
    private boolean mDisplayReady;
    /* access modifiers changed from: private */
    public final SuspendBlocker mDisplaySuspendBlocker;
    /* access modifiers changed from: private */
    public int mDockState;
    private boolean mDoubleTapWakeEnabled;
    private boolean mDozeAfterScreenOff;
    private int mDozeScreenBrightnessOverrideFromDreamManager;
    private int mDozeScreenStateOverrideFromDreamManager;
    private boolean mDrawWakeLockOverrideFromSidekick;
    private DreamManagerInternal mDreamManager;
    private boolean mDreamsActivateOnDockSetting;
    private boolean mDreamsActivateOnSleepSetting;
    private boolean mDreamsActivatedOnDockByDefaultConfig;
    private boolean mDreamsActivatedOnSleepByDefaultConfig;
    private int mDreamsBatteryLevelDrainCutoffConfig;
    private int mDreamsBatteryLevelMinimumWhenNotPoweredConfig;
    private int mDreamsBatteryLevelMinimumWhenPoweredConfig;
    private boolean mDreamsEnabledByDefaultConfig;
    private boolean mDreamsEnabledOnBatteryConfig;
    private boolean mDreamsEnabledSetting;
    private boolean mDreamsSupportedConfig;
    private boolean mForceSuspendActive;
    /* access modifiers changed from: private */
    public int mForegroundProfile;
    private boolean mHalAutoSuspendModeEnabled;
    private boolean mHalInteractiveModeEnabled;
    private final PowerManagerHandler mHandler;
    private final ServiceThread mHandlerThread;
    /* access modifiers changed from: private */
    public boolean mHangUpEnabled;
    private boolean mHoldingDisplaySuspendBlocker;
    private boolean mHoldingWakeLockSuspendBlocker;
    private final Injector mInjector;
    private boolean mIsPowered;
    private final boolean mIsUltrasonicProximity;
    /* access modifiers changed from: private */
    public boolean mIsVrModeEnabled;
    private long mLastInteractivePowerHintTime;
    private long mLastScreenBrightnessBoostTime;
    private int mLastSleepReason;
    private long mLastSleepTime;
    private long mLastUserActivityTime;
    private long mLastUserActivityTimeNoChangeLights;
    private int mLastWakeReason;
    private long mLastWakeTime;
    /* access modifiers changed from: private */
    public long mLastWarningAboutUserActivityPermission;
    private boolean mLightDeviceIdleMode;
    private LightsManager mLightsManager;
    private final LocalService mLocalService;
    /* access modifiers changed from: private */
    public final Object mLock;
    private long mMaximumScreenDimDurationConfig;
    private float mMaximumScreenDimRatioConfig;
    private long mMaximumScreenOffTimeoutFromDeviceAdmin;
    private long mMinimumScreenOffTimeoutConfig;
    /* access modifiers changed from: private */
    public final NativeWrapper mNativeWrapper;
    private Notifier mNotifier;
    private long mNotifyLongDispatched;
    private long mNotifyLongNextCheck;
    private long mNotifyLongScheduled;
    private long mOverriddenTimeout;
    private int mPlugType;
    private WindowManagerPolicy mPolicy;
    private int mPreWakefulness;
    private final SparseArray<ProfilePowerState> mProfilePowerState;
    /* access modifiers changed from: private */
    public boolean mProximityPositive;
    private boolean mRequestWaitForNegativeProximity;
    private boolean mSandmanScheduled;
    /* access modifiers changed from: private */
    public boolean mSandmanSummoned;
    private boolean mScreenBrightnessBoostInProgress;
    private int mScreenBrightnessModeSetting;
    private int mScreenBrightnessOverrideFromWindowManager;
    private int mScreenBrightnessSetting;
    private int mScreenBrightnessSettingDefault;
    private int mScreenBrightnessSettingMaximum;
    private int mScreenBrightnessSettingMinimum;
    private long mScreenOffTimeoutSetting;
    private boolean mScreenProjectionEnabled;
    private SettingsObserver mSettingsObserver;
    private long mSleepTimeoutSetting;
    private boolean mStayOn;
    private int mStayOnWhilePluggedInSetting;
    private boolean mSupportsDoubleTapWakeConfig;
    /* access modifiers changed from: private */
    public final ArrayList<SuspendBlocker> mSuspendBlockers;
    private boolean mSuspendWhenScreenOffDueToProximityConfig;
    /* access modifiers changed from: private */
    public boolean mSystemReady;
    private boolean mTheaterModeEnabled;
    private final SparseArray<UidState> mUidState;
    private boolean mUidsChanged;
    private boolean mUidsChanging;
    private int mUserActivitySummary;
    private long mUserActivityTimeoutOverrideFromWindowManager;
    private boolean mUserInactiveOverrideFromWindowManager;
    /* access modifiers changed from: private */
    public final Vector<Integer> mVisibleWindowUids;
    private final IVrStateCallbacks mVrStateCallbacks;
    private int mWakeLockSummary;
    private final SuspendBlocker mWakeLockSuspendBlocker;
    private final ArrayList<WakeLock> mWakeLocks;
    private boolean mWakeUpWhenPluggedOrUnpluggedConfig;
    private boolean mWakeUpWhenPluggedOrUnpluggedInTheaterModeConfig;
    private int mWakefulness;
    private boolean mWakefulnessChanging;
    private WirelessChargerDetector mWirelessChargerDetector;

    @Retention(RetentionPolicy.SOURCE)
    public @interface HaltMode {
    }

    /* access modifiers changed from: private */
    public static native void nativeAcquireSuspendBlocker(String str);

    /* access modifiers changed from: private */
    public static native boolean nativeForceSuspend();

    /* access modifiers changed from: private */
    public native void nativeInit();

    /* access modifiers changed from: private */
    public static native void nativeReleaseSuspendBlocker(String str);

    /* access modifiers changed from: private */
    public static native void nativeSendPowerHint(int i, int i2);

    /* access modifiers changed from: private */
    public static native void nativeSetAutoSuspend(boolean z);

    /* access modifiers changed from: private */
    public static native void nativeSetFeature(int i, int i2);

    /* access modifiers changed from: private */
    public static native void nativeSetInteractive(boolean z);

    static /* synthetic */ int access$1676(PowerManagerService x0, int x1) {
        int i = x0.mDirty | x1;
        x0.mDirty = i;
        return i;
    }

    private final class ForegroundProfileObserver extends SynchronousUserSwitchObserver {
        private ForegroundProfileObserver() {
        }

        public void onUserSwitching(int newUserId) throws RemoteException {
        }

        public void onForegroundProfileSwitch(int newProfileId) throws RemoteException {
            long now = SystemClock.uptimeMillis();
            synchronized (PowerManagerService.this.mLock) {
                int unused = PowerManagerService.this.mForegroundProfile = newProfileId;
                PowerManagerService.this.maybeUpdateForegroundProfileLastActivityLocked(now);
            }
        }
    }

    private static final class ProfilePowerState {
        long mLastUserActivityTime = SystemClock.uptimeMillis();
        boolean mLockingNotified;
        long mScreenOffTimeout;
        final int mUserId;
        int mWakeLockSummary;

        public ProfilePowerState(int userId, long screenOffTimeout) {
            this.mUserId = userId;
            this.mScreenOffTimeout = screenOffTimeout;
        }
    }

    private final class Constants extends ContentObserver {
        private static final boolean DEFAULT_NO_CACHED_WAKE_LOCKS = true;
        private static final String KEY_NO_CACHED_WAKE_LOCKS = "no_cached_wake_locks";
        public boolean NO_CACHED_WAKE_LOCKS = true;
        private final KeyValueListParser mParser = new KeyValueListParser(',');
        private ContentResolver mResolver;

        public Constants(Handler handler) {
            super(handler);
        }

        public void start(ContentResolver resolver) {
            this.mResolver = resolver;
            this.mResolver.registerContentObserver(Settings.Global.getUriFor("power_manager_constants"), false, this);
            updateConstants();
        }

        public void onChange(boolean selfChange, Uri uri) {
            updateConstants();
        }

        private void updateConstants() {
            synchronized (PowerManagerService.this.mLock) {
                try {
                    this.mParser.setString(Settings.Global.getString(this.mResolver, "power_manager_constants"));
                } catch (IllegalArgumentException e) {
                    Slog.e(PowerManagerService.TAG, "Bad alarm manager settings", e);
                }
                this.NO_CACHED_WAKE_LOCKS = this.mParser.getBoolean(KEY_NO_CACHED_WAKE_LOCKS, true);
            }
        }

        /* access modifiers changed from: package-private */
        public void dump(PrintWriter pw) {
            pw.println("  Settings power_manager_constants:");
            pw.print("    ");
            pw.print(KEY_NO_CACHED_WAKE_LOCKS);
            pw.print("=");
            pw.println(this.NO_CACHED_WAKE_LOCKS);
        }

        /* access modifiers changed from: package-private */
        public void dumpProto(ProtoOutputStream proto) {
            long constantsToken = proto.start(1146756268033L);
            proto.write(1133871366145L, this.NO_CACHED_WAKE_LOCKS);
            proto.end(constantsToken);
        }
    }

    @VisibleForTesting
    public static class NativeWrapper {
        public void nativeInit(PowerManagerService service) {
            service.nativeInit();
        }

        public void nativeAcquireSuspendBlocker(String name) {
            PowerManagerService.nativeAcquireSuspendBlocker(name);
        }

        public void nativeReleaseSuspendBlocker(String name) {
            PowerManagerService.nativeReleaseSuspendBlocker(name);
        }

        public void nativeSetInteractive(boolean enable) {
            PowerManagerService.nativeSetInteractive(enable);
        }

        public void nativeSetAutoSuspend(boolean enable) {
            PowerManagerService.nativeSetAutoSuspend(enable);
        }

        public void nativeSendPowerHint(int hintId, int data) {
            PowerManagerService.nativeSendPowerHint(hintId, data);
        }

        public void nativeSetFeature(int featureId, int data) {
            PowerManagerService.nativeSetFeature(featureId, data);
        }

        public boolean nativeForceSuspend() {
            return PowerManagerService.nativeForceSuspend();
        }
    }

    @VisibleForTesting
    static class Injector {
        Injector() {
        }

        /* access modifiers changed from: package-private */
        public Notifier createNotifier(Looper looper, Context context, IBatteryStats batteryStats, SuspendBlocker suspendBlocker, WindowManagerPolicy policy) {
            return new Notifier(looper, context, batteryStats, suspendBlocker, policy);
        }

        /* access modifiers changed from: package-private */
        public SuspendBlocker createSuspendBlocker(PowerManagerService service, String name) {
            Objects.requireNonNull(service);
            SuspendBlocker suspendBlocker = new SuspendBlockerImpl(name);
            service.mSuspendBlockers.add(suspendBlocker);
            return suspendBlocker;
        }

        /* access modifiers changed from: package-private */
        public BatterySaverPolicy createBatterySaverPolicy(Object lock, Context context, BatterySavingStats batterySavingStats) {
            return new BatterySaverPolicy(lock, context, batterySavingStats);
        }

        /* access modifiers changed from: package-private */
        public NativeWrapper createNativeWrapper() {
            return new NativeWrapper();
        }

        /* access modifiers changed from: package-private */
        public WirelessChargerDetector createWirelessChargerDetector(SensorManager sensorManager, SuspendBlocker suspendBlocker, Handler handler) {
            return new WirelessChargerDetector(sensorManager, suspendBlocker, handler);
        }

        /* access modifiers changed from: package-private */
        public AmbientDisplayConfiguration createAmbientDisplayConfiguration(Context context) {
            return new AmbientDisplayConfiguration(context);
        }
    }

    public PowerManagerService(Context context) {
        this(context, new Injector());
    }

    @VisibleForTesting
    PowerManagerService(Context context, Injector injector) {
        super(context);
        this.mLock = LockGuard.installNewLock(1);
        this.mIsUltrasonicProximity = SystemProperties.getBoolean("ro.vendor.audio.us.proximity", false);
        this.mSuspendBlockers = new ArrayList<>();
        this.mWakeLocks = new ArrayList<>();
        this.mDisplayPowerRequest = new DisplayManagerInternal.DisplayPowerRequest();
        this.mDockState = 0;
        this.mMaximumScreenOffTimeoutFromDeviceAdmin = JobStatus.NO_LATEST_RUNTIME;
        this.mScreenBrightnessOverrideFromWindowManager = -1;
        this.mOverriddenTimeout = -1;
        this.mUserActivityTimeoutOverrideFromWindowManager = -1;
        this.mDozeScreenStateOverrideFromDreamManager = 0;
        this.mDozeScreenBrightnessOverrideFromDreamManager = -1;
        this.mLastWarningAboutUserActivityPermission = Long.MIN_VALUE;
        this.mDeviceIdleWhitelist = new int[0];
        this.mDeviceIdleTempWhitelist = new int[0];
        this.mUidState = new SparseArray<>();
        this.mProfilePowerState = new SparseArray<>();
        this.mDisplayPowerCallbacks = new DisplayManagerInternal.DisplayPowerCallbacks() {
            private int mDisplayState = 0;

            public void onStateChanged() {
                synchronized (PowerManagerService.this.mLock) {
                    PowerManagerService.access$1676(PowerManagerService.this, 8);
                    PowerManagerService.this.updatePowerStateLocked();
                }
            }

            public void onProximityPositive() {
                synchronized (PowerManagerService.this.mLock) {
                    boolean unused = PowerManagerService.this.mProximityPositive = true;
                    PowerManagerService.access$1676(PowerManagerService.this, 512);
                    PowerManagerService.this.updatePowerStateLocked();
                }
            }

            public void onProximityNegative() {
                synchronized (PowerManagerService.this.mLock) {
                    boolean unused = PowerManagerService.this.mProximityPositive = false;
                    PowerManagerService.access$1676(PowerManagerService.this, 512);
                    boolean unused2 = PowerManagerService.this.userActivityNoUpdateLocked(SystemClock.uptimeMillis(), 0, 0, 1000);
                    PowerManagerService.this.updatePowerStateLocked();
                }
            }

            public void onDisplayStateChange(int state) {
                synchronized (PowerManagerService.this.mLock) {
                    if (this.mDisplayState != state) {
                        this.mDisplayState = state;
                        if (state == 1) {
                            if (!PowerManagerService.this.mDecoupleHalInteractiveModeFromDisplayConfig) {
                                PowerManagerService.this.setHalInteractiveModeLocked(false);
                            }
                            if (!PowerManagerService.this.mDecoupleHalAutoSuspendModeFromDisplayConfig) {
                                PowerManagerService.this.setHalAutoSuspendModeLocked(true);
                            }
                        } else {
                            if (!PowerManagerService.this.mDecoupleHalAutoSuspendModeFromDisplayConfig) {
                                PowerManagerService.this.setHalAutoSuspendModeLocked(false);
                            }
                            if (!PowerManagerService.this.mDecoupleHalInteractiveModeFromDisplayConfig) {
                                PowerManagerService.this.setHalInteractiveModeLocked(true);
                            }
                        }
                    }
                }
            }

            public void acquireSuspendBlocker() {
                PowerManagerService.this.mDisplaySuspendBlocker.acquire();
            }

            public void releaseSuspendBlocker() {
                PowerManagerService.this.mDisplaySuspendBlocker.release();
            }

            public String toString() {
                String str;
                synchronized (this) {
                    str = "state=" + Display.stateToString(this.mDisplayState);
                }
                return str;
            }
        };
        this.mVrStateCallbacks = new IVrStateCallbacks.Stub() {
            public void onVrStateChanged(boolean enabled) {
                PowerManagerService.this.powerHintInternal(7, enabled);
                synchronized (PowerManagerService.this.mLock) {
                    if (PowerManagerService.this.mIsVrModeEnabled != enabled) {
                        PowerManagerService.this.setVrModeEnabled(enabled);
                        PowerManagerService.access$1676(PowerManagerService.this, 8192);
                        PowerManagerService.this.updatePowerStateLocked();
                    }
                }
            }
        };
        this.mVisibleWindowUids = new Vector<>();
        this.mContext = context;
        this.mBinderService = new BinderService();
        this.mLocalService = new LocalService();
        this.mNativeWrapper = injector.createNativeWrapper();
        this.mInjector = injector;
        this.mHandlerThread = new ServiceThread(TAG, -4, false);
        this.mHandlerThread.start();
        this.mHandler = new PowerManagerHandler(this.mHandlerThread.getLooper());
        this.mConstants = new Constants(this.mHandler);
        this.mAmbientDisplayConfiguration = this.mInjector.createAmbientDisplayConfiguration(context);
        this.mAttentionDetector = new AttentionDetector(new Runnable() {
            public final void run() {
                PowerManagerService.this.onUserAttention();
            }
        }, this.mLock);
        this.mBatterySavingStats = new BatterySavingStats(this.mLock);
        this.mBatterySaverPolicy = this.mInjector.createBatterySaverPolicy(this.mLock, this.mContext, this.mBatterySavingStats);
        this.mBatterySaverController = new BatterySaverController(this.mLock, this.mContext, BackgroundThread.get().getLooper(), this.mBatterySaverPolicy, this.mBatterySavingStats);
        this.mBatterySaverStateMachine = new BatterySaverStateMachine(this.mLock, this.mContext, this.mBatterySaverController);
        synchronized (this.mLock) {
            this.mWakeLockSuspendBlocker = this.mInjector.createSuspendBlocker(this, "PowerManagerService.WakeLocks");
            this.mDisplaySuspendBlocker = this.mInjector.createSuspendBlocker(this, "PowerManagerService.Display");
            if (this.mDisplaySuspendBlocker != null) {
                this.mDisplaySuspendBlocker.acquire();
                this.mHoldingDisplaySuspendBlocker = true;
            }
            this.mHalAutoSuspendModeEnabled = false;
            this.mHalInteractiveModeEnabled = true;
            this.mWakefulness = 1;
            sQuiescent = SystemProperties.get(SYSTEM_PROPERTY_QUIESCENT, "0").equals(SplitScreenReporter.ACTION_ENTER_SPLIT);
            this.mNativeWrapper.nativeInit(this);
            this.mNativeWrapper.nativeSetAutoSuspend(false);
            this.mNativeWrapper.nativeSetInteractive(true);
            this.mNativeWrapper.nativeSetFeature(1, 0);
        }
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [com.android.server.power.PowerManagerService$BinderService, android.os.IBinder] */
    public void onStart() {
        publishBinderService("power", this.mBinderService);
        publishLocalService(PowerManagerInternal.class, this.mLocalService);
        Watchdog.getInstance().addMonitor(this);
        Watchdog.getInstance().addThread(this.mHandler);
    }

    public void onBootPhase(int phase) {
        synchronized (this.mLock) {
            if (phase == 600) {
                try {
                    incrementBootCount();
                } catch (Throwable th) {
                    throw th;
                }
            } else if (phase == 1000) {
                long now = SystemClock.uptimeMillis();
                this.mBootCompleted = true;
                this.mDirty |= 16;
                this.mBatterySaverStateMachine.onBootCompleted();
                userActivityNoUpdateLocked(now, 0, 0, 1000);
                updatePowerStateLocked();
            }
            if (phase == 550) {
                this.mActivityManagerReady = true;
            }
        }
    }

    public void systemReady(IAppOpsService appOps) {
        synchronized (this.mLock) {
            this.mSystemReady = true;
            this.mAppOps = appOps;
            this.mDreamManager = (DreamManagerInternal) getLocalService(DreamManagerInternal.class);
            this.mDisplayManagerInternal = (DisplayManagerInternal) getLocalService(DisplayManagerInternal.class);
            this.mPolicy = (WindowManagerPolicy) getLocalService(WindowManagerPolicy.class);
            this.mBatteryManagerInternal = (BatteryManagerInternal) getLocalService(BatteryManagerInternal.class);
            this.mAttentionDetector.systemReady(this.mContext);
            PowerManager pm = (PowerManager) this.mContext.getSystemService("power");
            this.mScreenBrightnessSettingMinimum = pm.getMinimumScreenBrightnessSetting();
            this.mScreenBrightnessSettingMaximum = pm.getMaximumScreenBrightnessSetting();
            this.mScreenBrightnessSettingDefault = pm.getDefaultScreenBrightnessSetting();
            SensorManager sensorManager = new SystemSensorManager(this.mContext, this.mHandler.getLooper());
            this.mBatteryStats = BatteryStatsService.getService();
            this.mNotifier = this.mInjector.createNotifier(Looper.getMainLooper(), this.mContext, this.mBatteryStats, this.mInjector.createSuspendBlocker(this, "PowerManagerService.Broadcasts"), this.mPolicy);
            this.mWirelessChargerDetector = this.mInjector.createWirelessChargerDetector(sensorManager, this.mInjector.createSuspendBlocker(this, "PowerManagerService.WirelessChargerDetector"), this.mHandler);
            this.mSettingsObserver = new SettingsObserver(this.mHandler);
            this.mLightsManager = (LightsManager) getLocalService(LightsManager.class);
            this.mAttentionLight = this.mLightsManager.getLight(5);
            this.mDisplayManagerInternal.initPowerManagement(this.mDisplayPowerCallbacks, this.mHandler, sensorManager);
            PowerManagerServiceInjector.init(this, this.mWakeLocks, this.mLock);
            IntentFirewallInjector.init(this.mContext, this);
            try {
                ActivityManager.getService().registerUserSwitchObserver(new ForegroundProfileObserver(), TAG);
            } catch (RemoteException e) {
            }
            readConfigurationLocked();
            updateSettingsLocked();
            resetScreenProjectionSettings();
            this.mDirty |= 256;
            updatePowerStateLocked();
        }
        ContentResolver resolver = this.mContext.getContentResolver();
        this.mConstants.start(resolver);
        this.mBatterySaverController.systemReady();
        this.mBatterySaverPolicy.systemReady();
        resolver.registerContentObserver(Settings.Secure.getUriFor("screensaver_enabled"), false, this.mSettingsObserver, -1);
        resolver.registerContentObserver(Settings.Secure.getUriFor("screensaver_activate_on_sleep"), false, this.mSettingsObserver, -1);
        resolver.registerContentObserver(Settings.Secure.getUriFor("screensaver_activate_on_dock"), false, this.mSettingsObserver, -1);
        resolver.registerContentObserver(Settings.System.getUriFor("screen_off_timeout"), false, this.mSettingsObserver, -1);
        resolver.registerContentObserver(Settings.Secure.getUriFor("sleep_timeout"), false, this.mSettingsObserver, -1);
        resolver.registerContentObserver(Settings.Global.getUriFor("stay_on_while_plugged_in"), false, this.mSettingsObserver, -1);
        resolver.registerContentObserver(Settings.System.getUriFor("screen_brightness_mode"), false, this.mSettingsObserver, -1);
        resolver.registerContentObserver(Settings.System.getUriFor("screen_auto_brightness_adj"), false, this.mSettingsObserver, -1);
        resolver.registerContentObserver(Settings.Global.getUriFor("theater_mode_on"), false, this.mSettingsObserver, -1);
        resolver.registerContentObserver(Settings.Secure.getUriFor("doze_always_on"), false, this.mSettingsObserver, -1);
        resolver.registerContentObserver(Settings.Secure.getUriFor("double_tap_to_wake"), false, this.mSettingsObserver, -1);
        resolver.registerContentObserver(Settings.Global.getUriFor("device_demo_mode"), false, this.mSettingsObserver, 0);
        resolver.registerContentObserver(Settings.Secure.getUriFor("screen_project_in_screening"), false, this.mSettingsObserver, -1);
        resolver.registerContentObserver(Settings.Secure.getUriFor("screen_project_hang_up_on"), false, this.mSettingsObserver, -1);
        IVrManager vrManager = IVrManager.Stub.asInterface(getBinderService("vrmanager"));
        if (vrManager != null) {
            try {
                vrManager.registerListener(this.mVrStateCallbacks);
            } catch (RemoteException e2) {
                Slog.e(TAG, "Failed to register VR mode state listener: " + e2);
            }
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.BATTERY_CHANGED");
        filter.setPriority(1000);
        this.mContext.registerReceiver(new BatteryReceiver(), filter, (String) null, this.mHandler);
        IntentFilter filter2 = new IntentFilter();
        filter2.addAction("android.intent.action.DREAMING_STARTED");
        filter2.addAction("android.intent.action.DREAMING_STOPPED");
        this.mContext.registerReceiver(new DreamReceiver(), filter2, (String) null, this.mHandler);
        IntentFilter filter3 = new IntentFilter();
        filter3.addAction("android.intent.action.USER_SWITCHED");
        this.mContext.registerReceiver(new UserSwitchedReceiver(), filter3, (String) null, this.mHandler);
        IntentFilter filter4 = new IntentFilter();
        filter4.addAction("android.intent.action.DOCK_EVENT");
        this.mContext.registerReceiver(new DockReceiver(), filter4, (String) null, this.mHandler);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void readConfigurationLocked() {
        Resources resources = this.mContext.getResources();
        this.mDecoupleHalAutoSuspendModeFromDisplayConfig = resources.getBoolean(17891498);
        this.mDecoupleHalInteractiveModeFromDisplayConfig = resources.getBoolean(17891499);
        this.mWakeUpWhenPluggedOrUnpluggedConfig = resources.getBoolean(17891559);
        this.mWakeUpWhenPluggedOrUnpluggedInTheaterModeConfig = resources.getBoolean(17891356);
        this.mSuspendWhenScreenOffDueToProximityConfig = resources.getBoolean(17891549);
        this.mDreamsSupportedConfig = resources.getBoolean(17891427);
        this.mDreamsEnabledByDefaultConfig = resources.getBoolean(17891425);
        this.mDreamsActivatedOnSleepByDefaultConfig = resources.getBoolean(17891424);
        this.mDreamsActivatedOnDockByDefaultConfig = resources.getBoolean(17891423);
        this.mDreamsEnabledOnBatteryConfig = resources.getBoolean(17891426);
        this.mDreamsBatteryLevelMinimumWhenPoweredConfig = resources.getInteger(17694804);
        this.mDreamsBatteryLevelMinimumWhenNotPoweredConfig = resources.getInteger(17694803);
        this.mDreamsBatteryLevelDrainCutoffConfig = resources.getInteger(17694802);
        this.mDozeAfterScreenOff = resources.getBoolean(17891417);
        this.mMinimumScreenOffTimeoutConfig = (long) resources.getInteger(17694844);
        this.mMaximumScreenDimDurationConfig = (long) resources.getInteger(17694839);
        this.mMaximumScreenDimRatioConfig = resources.getFraction(18022402, 1, 1);
        this.mSupportsDoubleTapWakeConfig = resources.getBoolean(17891537);
    }

    private void updateSettingsLocked() {
        ContentResolver resolver = this.mContext.getContentResolver();
        this.mDreamsEnabledSetting = Settings.Secure.getIntForUser(resolver, "screensaver_enabled", this.mDreamsEnabledByDefaultConfig ? 1 : 0, -2) != 0;
        this.mDreamsActivateOnSleepSetting = Settings.Secure.getIntForUser(resolver, "screensaver_activate_on_sleep", this.mDreamsActivatedOnSleepByDefaultConfig ? 1 : 0, -2) != 0;
        this.mDreamsActivateOnDockSetting = Settings.Secure.getIntForUser(resolver, "screensaver_activate_on_dock", this.mDreamsActivatedOnDockByDefaultConfig ? 1 : 0, -2) != 0;
        this.mScreenOffTimeoutSetting = (long) Settings.System.getIntForUser(resolver, "screen_off_timeout", 15000, -2);
        this.mSleepTimeoutSetting = (long) Settings.Secure.getIntForUser(resolver, "sleep_timeout", -1, -2);
        this.mStayOnWhilePluggedInSetting = Settings.Global.getInt(resolver, "stay_on_while_plugged_in", 1);
        this.mTheaterModeEnabled = Settings.Global.getInt(this.mContext.getContentResolver(), "theater_mode_on", 0) == 1;
        this.mAlwaysOnEnabled = this.mAmbientDisplayConfiguration.alwaysOnEnabled(-2);
        if (this.mSupportsDoubleTapWakeConfig) {
            boolean doubleTapWakeEnabled = Settings.Secure.getIntForUser(resolver, "double_tap_to_wake", 0, -2) != 0;
            if (doubleTapWakeEnabled != this.mDoubleTapWakeEnabled) {
                this.mDoubleTapWakeEnabled = doubleTapWakeEnabled;
                this.mNativeWrapper.nativeSetFeature(1, this.mDoubleTapWakeEnabled ? 1 : 0);
            }
        }
        String retailDemoValue = UserManager.isDeviceInDemoMode(this.mContext) ? SplitScreenReporter.ACTION_ENTER_SPLIT : "0";
        if (!retailDemoValue.equals(SystemProperties.get(SYSTEM_PROPERTY_RETAIL_DEMO_ENABLED))) {
            SystemProperties.set(SYSTEM_PROPERTY_RETAIL_DEMO_ENABLED, retailDemoValue);
        }
        this.mScreenBrightnessModeSetting = Settings.System.getIntForUser(resolver, "screen_brightness_mode", 0, -2);
        this.mDirty |= 32;
    }

    /* access modifiers changed from: private */
    public void handleSettingsChangedLocked() {
        updateSettingsLocked();
        updatePowerStateLocked();
    }

    /* Debug info: failed to restart local var, previous not found, register: 19 */
    /* access modifiers changed from: private */
    public void acquireWakeLockInternal(IBinder lock, int flags, String tag, String packageName, WorkSource ws, String historyTag, int uid, int pid) {
        Object obj;
        int index;
        WakeLock wakeLock;
        boolean notifyAcquire;
        UidState state;
        WakeLock wakeLock2;
        int index2;
        int i = uid;
        Object obj2 = this.mLock;
        synchronized (obj2) {
            try {
                int index3 = findWakeLockIndexLocked(lock);
                if (index3 >= 0) {
                    wakeLock = this.mWakeLocks.get(index3);
                    if (!wakeLock.hasSameProperties(flags, tag, ws, uid, pid)) {
                        index2 = index3;
                        notifyWakeLockChangingLocked(wakeLock, flags, tag, packageName, uid, pid, ws, historyTag);
                        wakeLock.updateProperties(flags, tag, packageName, ws, historyTag, uid, pid);
                    } else {
                        index2 = index3;
                    }
                    notifyAcquire = false;
                    IBinder iBinder = lock;
                    obj = obj2;
                    int i2 = index2;
                    index = i;
                } else {
                    int index4 = index3;
                    UidState state2 = this.mUidState.get(i);
                    if (state2 == null) {
                        UidState state3 = new UidState(i);
                        state3.mProcState = 21;
                        this.mUidState.put(i, state3);
                        state = state3;
                    } else {
                        state = state2;
                    }
                    state.mNumWakeLocks++;
                    obj = obj2;
                    int i3 = index4;
                    index = i;
                    try {
                        WakeLock wakeLock3 = new WakeLock(lock, flags, tag, packageName, ws, historyTag, uid, pid, state);
                        wakeLock2 = wakeLock3;
                    } catch (Throwable th) {
                        ex = th;
                        IBinder iBinder2 = lock;
                        throw ex;
                    }
                    try {
                        lock.linkToDeath(wakeLock2, 0);
                        this.mWakeLocks.add(wakeLock2);
                        setWakeLockDisabledStateLocked(wakeLock2);
                        PowerManagerServiceInjector.updateWakeLockDisabledStateLocked(wakeLock2, false);
                        notifyAcquire = true;
                        wakeLock = wakeLock2;
                    } catch (RemoteException e) {
                        RemoteException remoteException = e;
                        throw new IllegalArgumentException("Wake lock is already dead.");
                    } catch (Throwable th2) {
                        ex = th2;
                        throw ex;
                    }
                }
                applyWakeLockFlagsOnAcquireLocked(wakeLock, index);
                this.mDirty |= 1;
                updatePowerStateLocked();
                if (notifyAcquire) {
                    notifyWakeLockAcquiredLocked(wakeLock);
                }
            } catch (Throwable th3) {
                ex = th3;
                IBinder iBinder3 = lock;
                obj = obj2;
                int i4 = i;
                throw ex;
            }
        }
    }

    private static boolean isScreenLock(WakeLock wakeLock) {
        int i = wakeLock.mFlags & 65535;
        if (i == 6 || i == 10 || i == 26) {
            return true;
        }
        return false;
    }

    private static WorkSource.WorkChain getFirstNonEmptyWorkChain(WorkSource workSource) {
        if (workSource.getWorkChains() == null) {
            return null;
        }
        Iterator it = workSource.getWorkChains().iterator();
        while (it.hasNext()) {
            WorkSource.WorkChain workChain = (WorkSource.WorkChain) it.next();
            if (workChain.getSize() > 0) {
                return workChain;
            }
        }
        return null;
    }

    private void applyWakeLockFlagsOnAcquireLocked(WakeLock wakeLock, int uid) {
        String opPackageName;
        int opUid;
        if ((wakeLock.mFlags & 268435456) != 0 && isScreenLock(wakeLock)) {
            if (wakeLock.mWorkSource == null || wakeLock.mWorkSource.isEmpty()) {
                opPackageName = wakeLock.mPackageName;
                opUid = wakeLock.mOwnerUid;
            } else {
                WorkSource workSource = wakeLock.mWorkSource;
                WorkSource.WorkChain workChain = getFirstNonEmptyWorkChain(workSource);
                if (workChain != null) {
                    opPackageName = workChain.getAttributionTag();
                    opUid = workChain.getAttributionUid();
                } else {
                    String opPackageName2 = workSource.getName(0) != null ? workSource.getName(0) : wakeLock.mPackageName;
                    opUid = workSource.get(0);
                    opPackageName = opPackageName2;
                }
            }
            wakeUpNoUpdateLocked(SystemClock.uptimeMillis(), 2, wakeLock.mTag, opUid, opPackageName, opUid);
        }
    }

    /* access modifiers changed from: private */
    public void releaseWakeLockInternal(IBinder lock, int flags) {
        synchronized (this.mLock) {
            int index = findWakeLockIndexLocked(lock);
            if (index >= 0) {
                WakeLock wakeLock = this.mWakeLocks.get(index);
                if ((flags & 1) != 0 && !this.mIsUltrasonicProximity) {
                    this.mRequestWaitForNegativeProximity = true;
                }
                wakeLock.mLock.unlinkToDeath(wakeLock, 0);
                removeWakeLockLocked(wakeLock, index);
            }
        }
    }

    /* access modifiers changed from: private */
    public void handleWakeLockDeath(WakeLock wakeLock) {
        synchronized (this.mLock) {
            int index = this.mWakeLocks.indexOf(wakeLock);
            if (index >= 0) {
                removeWakeLockLocked(wakeLock, index);
            }
        }
    }

    private void removeWakeLockLocked(WakeLock wakeLock, int index) {
        this.mWakeLocks.remove(index);
        UidState state = wakeLock.mUidState;
        state.mNumWakeLocks--;
        if (state.mNumWakeLocks <= 0 && state.mProcState == 21) {
            this.mUidState.remove(state.mUid);
        }
        notifyWakeLockReleasedLocked(wakeLock);
        applyWakeLockFlagsOnReleaseLocked(wakeLock);
        this.mDirty |= 1;
        updatePowerStateLocked();
    }

    private void applyWakeLockFlagsOnReleaseLocked(WakeLock wakeLock) {
        if ((wakeLock.mFlags & 536870912) != 0 && isScreenLock(wakeLock)) {
            userActivityNoUpdateLocked(SystemClock.uptimeMillis(), 0, 1, wakeLock.mOwnerUid);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 14 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003d, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateWakeLockWorkSourceInternal(android.os.IBinder r15, android.os.WorkSource r16, java.lang.String r17, int r18) {
        /*
            r14 = this;
            r10 = r14
            r11 = r16
            java.lang.Object r12 = r10.mLock
            monitor-enter(r12)
            int r0 = r14.findWakeLockIndexLocked(r15)     // Catch:{ all -> 0x006b }
            if (r0 < 0) goto L_0x0043
            java.util.ArrayList<com.android.server.power.PowerManagerService$WakeLock> r1 = r10.mWakeLocks     // Catch:{ all -> 0x003e }
            java.lang.Object r1 = r1.get(r0)     // Catch:{ all -> 0x003e }
            com.android.server.power.PowerManagerService$WakeLock r1 = (com.android.server.power.PowerManagerService.WakeLock) r1     // Catch:{ all -> 0x003e }
            r13 = r1
            boolean r1 = r13.hasSameWorkSource(r11)     // Catch:{ all -> 0x003e }
            if (r1 != 0) goto L_0x003a
            int r3 = r13.mFlags     // Catch:{ all -> 0x003e }
            java.lang.String r4 = r13.mTag     // Catch:{ all -> 0x003e }
            java.lang.String r5 = r13.mPackageName     // Catch:{ all -> 0x003e }
            int r6 = r13.mOwnerUid     // Catch:{ all -> 0x003e }
            int r7 = r13.mOwnerPid     // Catch:{ all -> 0x003e }
            r1 = r14
            r2 = r13
            r8 = r16
            r9 = r17
            r1.notifyWakeLockChangingLocked(r2, r3, r4, r5, r6, r7, r8, r9)     // Catch:{ all -> 0x003e }
            r1 = r17
            r13.mHistoryTag = r1     // Catch:{ all -> 0x0069 }
            r13.updateWorkSource(r11)     // Catch:{ all -> 0x0069 }
            r2 = 1
            com.android.server.power.PowerManagerServiceInjector.updateWakeLockDisabledStateLocked(r13, r2)     // Catch:{ all -> 0x0069 }
            goto L_0x003c
        L_0x003a:
            r1 = r17
        L_0x003c:
            monitor-exit(r12)     // Catch:{ all -> 0x0069 }
            return
        L_0x003e:
            r0 = move-exception
            r1 = r17
        L_0x0041:
            r4 = r15
            goto L_0x006f
        L_0x0043:
            r1 = r17
            java.lang.IllegalArgumentException r2 = new java.lang.IllegalArgumentException     // Catch:{ all -> 0x0069 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0069 }
            r3.<init>()     // Catch:{ all -> 0x0069 }
            java.lang.String r4 = "Wake lock not active: "
            r3.append(r4)     // Catch:{ all -> 0x0069 }
            r4 = r15
            r3.append(r15)     // Catch:{ all -> 0x0067 }
            java.lang.String r5 = " from uid "
            r3.append(r5)     // Catch:{ all -> 0x0067 }
            r5 = r18
            r3.append(r5)     // Catch:{ all -> 0x0073 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0073 }
            r2.<init>(r3)     // Catch:{ all -> 0x0073 }
            throw r2     // Catch:{ all -> 0x0073 }
        L_0x0067:
            r0 = move-exception
            goto L_0x006f
        L_0x0069:
            r0 = move-exception
            goto L_0x0041
        L_0x006b:
            r0 = move-exception
            r4 = r15
            r1 = r17
        L_0x006f:
            r5 = r18
        L_0x0071:
            monitor-exit(r12)     // Catch:{ all -> 0x0073 }
            throw r0
        L_0x0073:
            r0 = move-exception
            goto L_0x0071
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.updateWakeLockWorkSourceInternal(android.os.IBinder, android.os.WorkSource, java.lang.String, int):void");
    }

    private int findWakeLockIndexLocked(IBinder lock) {
        int count = this.mWakeLocks.size();
        for (int i = 0; i < count; i++) {
            if (this.mWakeLocks.get(i).mLock == lock) {
                return i;
            }
        }
        return -1;
    }

    /* access modifiers changed from: package-private */
    public void notifyWakeLockAcquiredLocked(WakeLock wakeLock) {
        if (this.mSystemReady && !wakeLock.mDisabled) {
            wakeLock.mNotifiedAcquired = true;
            this.mNotifier.onWakeLockAcquired(wakeLock.mFlags, wakeLock.mTag, wakeLock.mPackageName, wakeLock.mOwnerUid, wakeLock.mOwnerPid, wakeLock.mWorkSource, wakeLock.mHistoryTag);
            restartNofifyLongTimerLocked(wakeLock);
        }
    }

    private void enqueueNotifyLongMsgLocked(long time) {
        this.mNotifyLongScheduled = time;
        Message msg = this.mHandler.obtainMessage(4);
        msg.setAsynchronous(true);
        this.mHandler.sendMessageAtTime(msg, time);
    }

    private void restartNofifyLongTimerLocked(WakeLock wakeLock) {
        wakeLock.mAcquireTime = SystemClock.uptimeMillis();
        if ((wakeLock.mFlags & 65535) == 1 && this.mNotifyLongScheduled == 0) {
            enqueueNotifyLongMsgLocked(wakeLock.mAcquireTime + 60000);
        }
    }

    private void notifyWakeLockLongStartedLocked(WakeLock wakeLock) {
        if (this.mSystemReady && !wakeLock.mDisabled) {
            wakeLock.mNotifiedLong = true;
            this.mNotifier.onLongPartialWakeLockStart(wakeLock.mTag, wakeLock.mOwnerUid, wakeLock.mWorkSource, wakeLock.mHistoryTag);
        }
    }

    private void notifyWakeLockLongFinishedLocked(WakeLock wakeLock) {
        if (wakeLock.mNotifiedLong) {
            wakeLock.mNotifiedLong = false;
            this.mNotifier.onLongPartialWakeLockFinish(wakeLock.mTag, wakeLock.mOwnerUid, wakeLock.mWorkSource, wakeLock.mHistoryTag);
        }
    }

    private void notifyWakeLockChangingLocked(WakeLock wakeLock, int flags, String tag, String packageName, int uid, int pid, WorkSource ws, String historyTag) {
        WakeLock wakeLock2 = wakeLock;
        if (this.mSystemReady && wakeLock2.mNotifiedAcquired) {
            this.mNotifier.onWakeLockChanging(wakeLock2.mFlags, wakeLock2.mTag, wakeLock2.mPackageName, wakeLock2.mOwnerUid, wakeLock2.mOwnerPid, wakeLock2.mWorkSource, wakeLock2.mHistoryTag, flags, tag, packageName, uid, pid, ws, historyTag);
            notifyWakeLockLongFinishedLocked(wakeLock);
            restartNofifyLongTimerLocked(wakeLock);
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyWakeLockReleasedLocked(WakeLock wakeLock) {
        if (this.mSystemReady && wakeLock.mNotifiedAcquired) {
            wakeLock.mNotifiedAcquired = false;
            wakeLock.mAcquireTime = 0;
            this.mNotifier.onWakeLockReleased(wakeLock.mFlags, wakeLock.mTag, wakeLock.mPackageName, wakeLock.mOwnerUid, wakeLock.mOwnerPid, wakeLock.mWorkSource, wakeLock.mHistoryTag);
            notifyWakeLockLongFinishedLocked(wakeLock);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0031, code lost:
        return r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0033, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean isWakeLockLevelSupportedInternal(int r5) {
        /*
            r4 = this;
            java.lang.Object r0 = r4.mLock
            monitor-enter(r0)
            r1 = 1
            if (r5 == r1) goto L_0x0032
            r2 = 6
            if (r5 == r2) goto L_0x0032
            r2 = 10
            if (r5 == r2) goto L_0x0032
            r2 = 26
            if (r5 == r2) goto L_0x0032
            r2 = 32
            r3 = 0
            if (r5 == r2) goto L_0x0022
            r2 = 64
            if (r5 == r2) goto L_0x0032
            r2 = 128(0x80, float:1.794E-43)
            if (r5 == r2) goto L_0x0032
            monitor-exit(r0)     // Catch:{ all -> 0x0020 }
            return r3
        L_0x0020:
            r1 = move-exception
            goto L_0x0034
        L_0x0022:
            boolean r2 = r4.mSystemReady     // Catch:{ all -> 0x0020 }
            if (r2 == 0) goto L_0x002f
            android.hardware.display.DisplayManagerInternal r2 = r4.mDisplayManagerInternal     // Catch:{ all -> 0x0020 }
            boolean r2 = r2.isProximitySensorAvailable()     // Catch:{ all -> 0x0020 }
            if (r2 == 0) goto L_0x002f
            goto L_0x0030
        L_0x002f:
            r1 = r3
        L_0x0030:
            monitor-exit(r0)     // Catch:{ all -> 0x0020 }
            return r1
        L_0x0032:
            monitor-exit(r0)     // Catch:{ all -> 0x0020 }
            return r1
        L_0x0034:
            monitor-exit(r0)     // Catch:{ all -> 0x0020 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.isWakeLockLevelSupportedInternal(int):boolean");
    }

    private void userActivityFromNative(long eventTime, int event, int flags) {
        userActivityInternal(eventTime, event, flags, 1000);
    }

    /* access modifiers changed from: private */
    public void userActivityInternal(long eventTime, int event, int flags, int uid) {
        synchronized (this.mLock) {
            if (userActivityNoUpdateLocked(eventTime, event, flags, uid)) {
                updatePowerStateLocked();
            }
        }
    }

    /* access modifiers changed from: private */
    public void onUserAttention() {
        synchronized (this.mLock) {
            if (userActivityNoUpdateLocked(SystemClock.uptimeMillis(), 4, 0, 1000)) {
                updatePowerStateLocked();
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean userActivityNoUpdateLocked(long eventTime, int event, int flags, int uid) {
        if (eventTime < this.mLastSleepTime || eventTime < this.mLastWakeTime || !this.mBootCompleted || !this.mSystemReady) {
            return false;
        }
        Trace.traceBegin(131072, "userActivity");
        try {
            if (eventTime > this.mLastInteractivePowerHintTime) {
                powerHintInternal(2, 0);
                this.mLastInteractivePowerHintTime = eventTime;
            }
            this.mNotifier.onUserActivity(event, uid);
            this.mAttentionDetector.onUserActivity(eventTime, event);
            if (this.mUserInactiveOverrideFromWindowManager) {
                this.mUserInactiveOverrideFromWindowManager = false;
                this.mOverriddenTimeout = -1;
            }
            if (!(this.mWakefulness == 0 || this.mWakefulness == 3)) {
                if ((flags & 2) == 0) {
                    maybeUpdateForegroundProfileLastActivityLocked(eventTime);
                    if ((flags & 1) != 0) {
                        if (eventTime > this.mLastUserActivityTimeNoChangeLights && eventTime > this.mLastUserActivityTime) {
                            this.mLastUserActivityTimeNoChangeLights = eventTime;
                            this.mDirty |= 4;
                            if (event == 1) {
                                this.mDirty |= 4096;
                            }
                            Trace.traceEnd(131072);
                            return true;
                        }
                    } else if (eventTime > this.mLastUserActivityTime) {
                        this.mLastUserActivityTime = eventTime;
                        this.mDirty |= 4;
                        if (event == 1) {
                            this.mDirty |= 4096;
                        }
                        Trace.traceEnd(131072);
                        return true;
                    }
                    Trace.traceEnd(131072);
                    return false;
                }
            }
            return false;
        } finally {
            Trace.traceEnd(131072);
        }
    }

    /* access modifiers changed from: private */
    public void maybeUpdateForegroundProfileLastActivityLocked(long eventTime) {
        ProfilePowerState profile = this.mProfilePowerState.get(this.mForegroundProfile);
        if (profile != null && eventTime > profile.mLastUserActivityTime) {
            profile.mLastUserActivityTime = eventTime;
        }
    }

    /* access modifiers changed from: private */
    public void wakeUpInternal(long eventTime, int reason, String details, int uid, String opPackageName, int opUid) {
        synchronized (this.mLock) {
            if (wakeUpNoUpdateLocked(eventTime, reason, details, uid, opPackageName, opUid)) {
                updatePowerStateLocked();
            }
        }
    }

    private boolean wakeUpNoUpdateLocked(long eventTime, int reason, String details, int reasonUid, String opPackageName, int opUid) {
        long j = eventTime;
        int i = reason;
        String str = details;
        if (j < this.mLastSleepTime || this.mWakefulness == 1 || !this.mBootCompleted || !this.mSystemReady) {
            int i2 = reasonUid;
        } else if (this.mForceSuspendActive) {
            int i3 = reasonUid;
        } else {
            Trace.asyncTraceBegin(131072, TRACE_SCREEN_ON, 0);
            Trace.traceBegin(131072, "wakeUp");
            WindowManagerPolicy windowManagerPolicy = this.mPolicy;
            if (windowManagerPolicy instanceof PhoneWindowManager) {
                ((PhoneWindowManager) windowManagerPolicy).wakingUp(str);
            }
            try {
                StringBuilder sb = new StringBuilder();
                sb.append("Waking up from ");
                sb.append(PowerManagerInternal.wakefulnessToString(this.mWakefulness));
                sb.append(" (uid=");
                try {
                    sb.append(reasonUid);
                    sb.append(", reason=");
                    sb.append(PowerManager.wakeReasonToString(reason));
                    sb.append(", details=");
                    sb.append(str);
                    sb.append(")...");
                    Slog.i(TAG, sb.toString());
                    this.mLastWakeTime = j;
                    this.mLastWakeReason = i;
                    setWakefulnessLocked(1, i, j);
                    this.mNotifier.onWakeUp(reason, details, reasonUid, opPackageName, opUid);
                    userActivityNoUpdateLocked(eventTime, 0, 0, reasonUid);
                    Trace.traceEnd(131072);
                    return true;
                } catch (Throwable th) {
                    th = th;
                    Trace.traceEnd(131072);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                int i4 = reasonUid;
                Trace.traceEnd(131072);
                throw th;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void goToSleepInternal(long eventTime, int reason, int flags, int uid) {
        synchronized (this.mLock) {
            if (goToSleepNoUpdateLocked(eventTime, reason, flags, uid)) {
                updatePowerStateLocked();
            }
        }
    }

    private boolean goToSleepNoUpdateLocked(long eventTime, int reason, int flags, int uid) {
        int i;
        if (eventTime < this.mLastWakeTime || (i = this.mWakefulness) == 0 || i == 3 || !this.mBootCompleted || !this.mSystemReady) {
            return false;
        }
        Trace.traceBegin(131072, "goToSleep");
        try {
            int reason2 = Math.min(8, Math.max(reason, 0));
            Slog.i(TAG, "Going to sleep due to " + PowerManager.sleepReasonToString(reason2) + " (uid " + uid + ")...");
            this.mLastSleepTime = eventTime;
            this.mLastSleepReason = reason2;
            this.mSandmanSummoned = true;
            setWakefulnessLocked(3, reason2, eventTime);
            int numWakeLocksCleared = 0;
            int numWakeLocks = this.mWakeLocks.size();
            for (int i2 = 0; i2 < numWakeLocks; i2++) {
                int i3 = this.mWakeLocks.get(i2).mFlags & 65535;
                if (i3 == 6 || i3 == 10 || i3 == 26) {
                    numWakeLocksCleared++;
                }
            }
            EventLogTags.writePowerSleepRequested(numWakeLocksCleared);
            if ((flags & 1) != 0) {
                reallyGoToSleepNoUpdateLocked(eventTime, uid);
            }
            return true;
        } finally {
            Trace.traceEnd(131072);
        }
    }

    /* access modifiers changed from: private */
    public void napInternal(long eventTime, int uid) {
        synchronized (this.mLock) {
            if (napNoUpdateLocked(eventTime, uid)) {
                updatePowerStateLocked();
            }
        }
    }

    private boolean napNoUpdateLocked(long eventTime, int uid) {
        if (eventTime < this.mLastWakeTime || this.mWakefulness != 1 || !this.mBootCompleted || !this.mSystemReady) {
            return false;
        }
        Trace.traceBegin(131072, "nap");
        try {
            Slog.i(TAG, "Nap time (uid " + uid + ")...");
            this.mSandmanSummoned = true;
            setWakefulnessLocked(2, 0, eventTime);
            return true;
        } finally {
            Trace.traceEnd(131072);
        }
    }

    /* JADX INFO: finally extract failed */
    private boolean reallyGoToSleepNoUpdateLocked(long eventTime, int uid) {
        if (eventTime < this.mLastWakeTime || this.mWakefulness == 0 || !this.mBootCompleted || !this.mSystemReady) {
            return false;
        }
        Trace.traceBegin(131072, "reallyGoToSleep");
        try {
            Slog.i(TAG, "Sleeping (uid " + uid + ")...");
            setWakefulnessLocked(0, 2, eventTime);
            Trace.traceEnd(131072);
            return true;
        } catch (Throwable th) {
            Trace.traceEnd(131072);
            throw th;
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setWakefulnessLocked(int wakefulness, int reason, long eventTime) {
        int i = this.mWakefulness;
        if (i != wakefulness) {
            this.mPreWakefulness = i;
            this.mWakefulness = wakefulness;
            this.mWakefulnessChanging = true;
            this.mDirty |= 2;
            Notifier notifier = this.mNotifier;
            if (notifier != null) {
                notifier.onWakefulnessChangeStarted(wakefulness, reason, eventTime);
            }
            this.mAttentionDetector.onWakefulnessChangeStarted(wakefulness);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public int getWakefulness() {
        return this.mWakefulness;
    }

    private void logSleepTimeoutRecapturedLocked() {
        long savedWakeTimeMs = this.mOverriddenTimeout - SystemClock.uptimeMillis();
        if (savedWakeTimeMs >= 0) {
            EventLogTags.writePowerSoftSleepRequested(savedWakeTimeMs);
            this.mOverriddenTimeout = -1;
        }
    }

    private void finishWakefulnessChangeIfNeededLocked() {
        if (this.mWakefulnessChanging && this.mDisplayReady) {
            if (this.mWakefulness != 3 || (this.mWakeLockSummary & 64) != 0) {
                int i = this.mWakefulness;
                if (i == 3 || i == 0) {
                    logSleepTimeoutRecapturedLocked();
                }
                if (this.mWakefulness == 1 && this.mPreWakefulness != 4) {
                    Trace.asyncTraceEnd(131072, TRACE_SCREEN_ON, 0);
                    int latencyMs = (int) (SystemClock.uptimeMillis() - this.mLastWakeTime);
                    if (latencyMs >= 200) {
                        Slog.w(TAG, "Screen on took " + latencyMs + " ms");
                    }
                }
                this.mWakefulnessChanging = false;
                if (!this.mScreenProjectionEnabled || this.mWakefulness != 4) {
                    this.mNotifier.onWakefulnessInHangUp(false);
                }
                this.mNotifier.onWakefulnessChangeFinished();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void updatePowerStateLocked() {
        int dirtyPhase1;
        if (this.mSystemReady && this.mDirty != 0) {
            if (!Thread.holdsLock(this.mLock)) {
                Slog.wtf(TAG, "Power manager lock was not held when calling updatePowerStateLocked");
            }
            Trace.traceBegin(131072, "updatePowerState");
            try {
                updateIsPoweredLocked(this.mDirty);
                updateStayOnLocked(this.mDirty);
                updateScreenBrightnessBoostLocked(this.mDirty);
                long now = SystemClock.uptimeMillis();
                int dirtyPhase2 = 0;
                do {
                    dirtyPhase1 = this.mDirty;
                    dirtyPhase2 |= dirtyPhase1;
                    this.mDirty = 0;
                    updateWakeLockSummaryLocked(dirtyPhase1);
                    updateUserActivitySummaryLocked(now, dirtyPhase1);
                } while (updateWakefulnessLocked(dirtyPhase1));
                updateProfilesLocked(now);
                updateDreamLocked(dirtyPhase2, updateDisplayPowerStateLocked(dirtyPhase2));
                finishWakefulnessChangeIfNeededLocked();
                updateSuspendBlockerLocked();
            } finally {
                Trace.traceEnd(131072);
            }
        }
    }

    private void updateProfilesLocked(long now) {
        int numProfiles = this.mProfilePowerState.size();
        for (int i = 0; i < numProfiles; i++) {
            ProfilePowerState profile = this.mProfilePowerState.valueAt(i);
            if (isProfileBeingKeptAwakeLocked(profile, now)) {
                profile.mLockingNotified = false;
            } else if (!profile.mLockingNotified) {
                profile.mLockingNotified = true;
                this.mNotifier.onProfileTimeout(profile.mUserId);
            }
        }
    }

    private boolean isProfileBeingKeptAwakeLocked(ProfilePowerState profile, long now) {
        return profile.mLastUserActivityTime + profile.mScreenOffTimeout > now || (profile.mWakeLockSummary & 32) != 0 || (this.mProximityPositive && (profile.mWakeLockSummary & 16) != 0);
    }

    private void updateIsPoweredLocked(int dirty) {
        if ((dirty & 256) != 0) {
            boolean wasPowered = this.mIsPowered;
            int oldPlugType = this.mPlugType;
            boolean z = this.mBatteryLevelLow;
            this.mIsPowered = this.mBatteryManagerInternal.isPowered(7);
            this.mPlugType = this.mBatteryManagerInternal.getPlugType();
            this.mBatteryLevel = this.mBatteryManagerInternal.getBatteryLevel();
            this.mBatteryLevelLow = this.mBatteryManagerInternal.getBatteryLevelLow();
            if (!(wasPowered == this.mIsPowered && oldPlugType == this.mPlugType)) {
                this.mDirty |= 64;
                boolean dockedOnWirelessCharger = this.mWirelessChargerDetector.update(this.mIsPowered, this.mPlugType);
                long now = SystemClock.uptimeMillis();
                if (shouldWakeUpWhenPluggedOrUnpluggedLocked(wasPowered, oldPlugType, dockedOnWirelessCharger)) {
                    wakeUpNoUpdateLocked(now, 3, "android.server.power:PLUGGED:" + this.mIsPowered, 1000, this.mContext.getOpPackageName(), 1000);
                }
                userActivityNoUpdateLocked(now, 0, 0, 1000);
                if (this.mBootCompleted) {
                    if (this.mIsPowered && !BatteryManager.isPlugWired(oldPlugType) && BatteryManager.isPlugWired(this.mPlugType)) {
                        this.mNotifier.onWiredChargingStarted(this.mForegroundProfile);
                    } else if (dockedOnWirelessCharger) {
                        this.mNotifier.onWirelessChargingStarted(this.mBatteryLevel, this.mForegroundProfile);
                    }
                }
            }
            this.mBatterySaverStateMachine.setBatteryStatus(this.mIsPowered, this.mBatteryLevel, this.mBatteryLevelLow);
        }
    }

    private boolean shouldWakeUpWhenPluggedOrUnpluggedLocked(boolean wasPowered, int oldPlugType, boolean dockedOnWirelessCharger) {
        if (!this.mWakeUpWhenPluggedOrUnpluggedConfig) {
            return false;
        }
        if (this.mIsPowered && this.mWakefulness == 2) {
            return false;
        }
        if (!this.mTheaterModeEnabled || this.mWakeUpWhenPluggedOrUnpluggedInTheaterModeConfig) {
            return true;
        }
        return false;
    }

    private void updateStayOnLocked(int dirty) {
        if ((dirty & 288) != 0) {
            boolean wasStayOn = this.mStayOn;
            if (this.mStayOnWhilePluggedInSetting == 0 || isMaximumScreenOffTimeoutFromDeviceAdminEnforcedLocked()) {
                this.mStayOn = false;
            } else {
                this.mStayOn = this.mBatteryManagerInternal.isPowered(this.mStayOnWhilePluggedInSetting);
            }
            if (this.mStayOn != wasStayOn) {
                this.mDirty |= 128;
            }
        }
    }

    private void updateWakeLockSummaryLocked(int dirty) {
        if ((dirty & 3) != 0) {
            this.mWakeLockSummary = 0;
            int numProfiles = this.mProfilePowerState.size();
            for (int i = 0; i < numProfiles; i++) {
                this.mProfilePowerState.valueAt(i).mWakeLockSummary = 0;
            }
            int numWakeLocks = this.mWakeLocks.size();
            for (int i2 = 0; i2 < numWakeLocks; i2++) {
                WakeLock wakeLock = this.mWakeLocks.get(i2);
                int wakeLockFlags = getWakeLockSummaryFlags(wakeLock);
                this.mWakeLockSummary |= wakeLockFlags;
                for (int j = 0; j < numProfiles; j++) {
                    ProfilePowerState profile = this.mProfilePowerState.valueAt(j);
                    if (wakeLockAffectsUser(wakeLock, profile.mUserId)) {
                        profile.mWakeLockSummary |= wakeLockFlags;
                    }
                }
            }
            this.mWakeLockSummary = adjustWakeLockSummaryLocked(this.mWakeLockSummary);
            for (int i3 = 0; i3 < numProfiles; i3++) {
                ProfilePowerState profile2 = this.mProfilePowerState.valueAt(i3);
                profile2.mWakeLockSummary = adjustWakeLockSummaryLocked(profile2.mWakeLockSummary);
            }
        }
    }

    private int adjustWakeLockSummaryLocked(int wakeLockSummary) {
        if (this.mWakefulness != 3) {
            wakeLockSummary &= -193;
        }
        if (this.mWakefulness == 0 || (wakeLockSummary & 64) != 0) {
            wakeLockSummary &= -15;
            if (this.mWakefulness == 0) {
                wakeLockSummary &= -17;
            }
        }
        if ((wakeLockSummary & 6) != 0) {
            int i = this.mWakefulness;
            if (i == 1) {
                wakeLockSummary |= 33;
            } else if (i == 2) {
                wakeLockSummary |= 1;
            }
        }
        if ((wakeLockSummary & 38) != 0 && this.mScreenProjectionEnabled) {
            wakeLockSummary &= -39;
        }
        if ((wakeLockSummary & 128) != 0) {
            return wakeLockSummary | 1;
        }
        return wakeLockSummary;
    }

    private int getWakeLockSummaryFlags(WakeLock wakeLock) {
        int i = wakeLock.mFlags & 65535;
        if (i != 1) {
            if (i != 6) {
                if (i != 10) {
                    if (i != 26) {
                        if (i == 32) {
                            return 16;
                        }
                        if (i == 64) {
                            return 64;
                        }
                        if (i != 128) {
                            return 0;
                        }
                        return 128;
                    } else if (wakeLock.mDisabled) {
                        return 0;
                    } else {
                        return 10;
                    }
                } else if (wakeLock.mDisabled) {
                    return 0;
                } else {
                    return 2;
                }
            } else if (wakeLock.mDisabled) {
                return 0;
            } else {
                return 4;
            }
        } else if (!wakeLock.mDisabled) {
            return 1;
        } else {
            return 0;
        }
    }

    private boolean wakeLockAffectsUser(WakeLock wakeLock, int userId) {
        if (wakeLock.mWorkSource != null) {
            for (int k = 0; k < wakeLock.mWorkSource.size(); k++) {
                if (userId == UserHandle.getUserId(wakeLock.mWorkSource.get(k))) {
                    return true;
                }
            }
            ArrayList<WorkSource.WorkChain> workChains = wakeLock.mWorkSource.getWorkChains();
            if (workChains != null) {
                for (int k2 = 0; k2 < workChains.size(); k2++) {
                    if (userId == UserHandle.getUserId(workChains.get(k2).getAttributionUid())) {
                        return true;
                    }
                }
            }
        }
        if (userId == UserHandle.getUserId(wakeLock.mOwnerUid)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void checkForLongWakeLocks() {
        synchronized (this.mLock) {
            long now = SystemClock.uptimeMillis();
            this.mNotifyLongDispatched = now;
            long when = now - 60000;
            long nextCheckTime = JobStatus.NO_LATEST_RUNTIME;
            int numWakeLocks = this.mWakeLocks.size();
            for (int i = 0; i < numWakeLocks; i++) {
                WakeLock wakeLock = this.mWakeLocks.get(i);
                if ((wakeLock.mFlags & 65535) == 1 && wakeLock.mNotifiedAcquired && !wakeLock.mNotifiedLong) {
                    if (wakeLock.mAcquireTime < when) {
                        notifyWakeLockLongStartedLocked(wakeLock);
                    } else {
                        long checkTime = wakeLock.mAcquireTime + 60000;
                        if (checkTime < nextCheckTime) {
                            nextCheckTime = checkTime;
                        }
                    }
                }
            }
            this.mNotifyLongScheduled = 0;
            this.mHandler.removeMessages(4);
            if (nextCheckTime != JobStatus.NO_LATEST_RUNTIME) {
                this.mNotifyLongNextCheck = nextCheckTime;
                enqueueNotifyLongMsgLocked(nextCheckTime);
            } else {
                this.mNotifyLongNextCheck = 0;
            }
        }
    }

    private void updateUserActivitySummaryLocked(long now, int dirty) {
        long nextTimeout;
        long nextTimeout2;
        int i;
        long anyUserActivity;
        if ((dirty & 39) != 0) {
            this.mHandler.removeMessages(1);
            int i2 = this.mWakefulness;
            if (i2 == 1 || i2 == 2 || i2 == 3) {
                long sleepTimeout = getSleepTimeoutLocked();
                long screenOffTimeout = getScreenOffTimeoutLocked(sleepTimeout);
                long screenDimDuration = getScreenDimDurationLocked(screenOffTimeout);
                boolean userInactiveOverride = this.mUserInactiveOverrideFromWindowManager;
                long nextProfileTimeout = getNextProfileTimeoutLocked(now);
                this.mUserActivitySummary = 0;
                long j = this.mLastUserActivityTime;
                if (j >= this.mLastWakeTime) {
                    nextTimeout = (j + screenOffTimeout) - screenDimDuration;
                    if (now < nextTimeout) {
                        this.mUserActivitySummary = 1;
                    } else {
                        nextTimeout = j + screenOffTimeout + ScreenEffectService.getDimDurationExtraTime(this.mUserActivityTimeoutOverrideFromWindowManager - screenOffTimeout);
                        if (now < nextTimeout) {
                            this.mUserActivitySummary = 2;
                        }
                    }
                } else {
                    nextTimeout = 0;
                }
                if (this.mUserActivitySummary == 0) {
                    long j2 = this.mLastUserActivityTimeNoChangeLights;
                    nextTimeout2 = nextTimeout;
                    if (j2 >= this.mLastWakeTime) {
                        long nextTimeout3 = j2 + screenOffTimeout;
                        if (now < nextTimeout3) {
                            if (this.mDisplayPowerRequest.policy == 3 || this.mDisplayPowerRequest.policy == 4) {
                                this.mUserActivitySummary = 1;
                            } else if (this.mDisplayPowerRequest.policy == 2) {
                                this.mUserActivitySummary = 2;
                            }
                        }
                        nextTimeout2 = nextTimeout3;
                    }
                } else {
                    nextTimeout2 = nextTimeout;
                }
                if (this.mUserActivitySummary != 0) {
                    i = 4;
                    anyUserActivity = nextTimeout2;
                } else if (sleepTimeout >= 0) {
                    long anyUserActivity2 = Math.max(this.mLastUserActivityTime, this.mLastUserActivityTimeNoChangeLights);
                    if (anyUserActivity2 >= this.mLastWakeTime) {
                        long nextTimeout4 = anyUserActivity2 + sleepTimeout;
                        if (now < nextTimeout4) {
                            i = 4;
                            this.mUserActivitySummary = 4;
                        } else {
                            i = 4;
                        }
                        nextTimeout2 = nextTimeout4;
                    } else {
                        i = 4;
                    }
                    anyUserActivity = nextTimeout2;
                } else {
                    i = 4;
                    this.mUserActivitySummary = 4;
                    anyUserActivity = -1;
                }
                int i3 = this.mUserActivitySummary;
                if (i3 != i && userInactiveOverride) {
                    if ((3 & i3) != 0 && anyUserActivity >= now && this.mOverriddenTimeout == -1) {
                        this.mOverriddenTimeout = anyUserActivity;
                    }
                    this.mUserActivitySummary = 4;
                    anyUserActivity = -1;
                }
                if ((this.mUserActivitySummary & 1) != 0 && (this.mWakeLockSummary & 32) == 0) {
                    anyUserActivity = this.mAttentionDetector.updateUserActivity(anyUserActivity);
                }
                if (nextProfileTimeout > 0) {
                    anyUserActivity = Math.min(anyUserActivity, nextProfileTimeout);
                }
                if (this.mUserActivitySummary != 0 && anyUserActivity >= 0) {
                    scheduleUserInactivityTimeout(anyUserActivity);
                    return;
                }
                return;
            }
            this.mUserActivitySummary = 0;
        }
    }

    private void scheduleUserInactivityTimeout(long timeMs) {
        Message msg = this.mHandler.obtainMessage(1);
        msg.setAsynchronous(true);
        this.mHandler.sendMessageAtTime(msg, timeMs);
    }

    private long getNextProfileTimeoutLocked(long now) {
        long nextTimeout = -1;
        int numProfiles = this.mProfilePowerState.size();
        for (int i = 0; i < numProfiles; i++) {
            ProfilePowerState profile = this.mProfilePowerState.valueAt(i);
            long timeout = profile.mLastUserActivityTime + profile.mScreenOffTimeout;
            if (timeout > now && (nextTimeout == -1 || timeout < nextTimeout)) {
                nextTimeout = timeout;
            }
        }
        return nextTimeout;
    }

    /* access modifiers changed from: private */
    public void handleUserActivityTimeout() {
        synchronized (this.mLock) {
            this.mDirty |= 4;
            checkScreenWakeLockDisabledStateLocked();
            updatePowerStateLocked();
        }
    }

    private long getSleepTimeoutLocked() {
        long timeout = this.mSleepTimeoutSetting;
        if (timeout <= 0) {
            return -1;
        }
        return Math.max(timeout, this.mMinimumScreenOffTimeoutConfig);
    }

    private long getScreenOffTimeoutLocked(long sleepTimeout) {
        long timeout = this.mScreenOffTimeoutSetting;
        if (isMaximumScreenOffTimeoutFromDeviceAdminEnforcedLocked()) {
            timeout = Math.min(timeout, this.mMaximumScreenOffTimeoutFromDeviceAdmin);
        }
        long j = this.mUserActivityTimeoutOverrideFromWindowManager;
        if (j >= 0) {
            timeout = Math.min(timeout, j);
        }
        if (sleepTimeout >= 0) {
            timeout = Math.min(timeout, sleepTimeout);
        }
        return Math.max(timeout, this.mMinimumScreenOffTimeoutConfig);
    }

    private long getScreenDimDurationLocked(long screenOffTimeout) {
        return Math.min(this.mMaximumScreenDimDurationConfig, (long) (((float) screenOffTimeout) * this.mMaximumScreenDimRatioConfig));
    }

    private boolean updateWakefulnessLocked(int dirty) {
        if ((dirty & 1687) == 0 || this.mWakefulness != 1 || !isItBedTimeYetLocked()) {
            return false;
        }
        long time = SystemClock.uptimeMillis();
        if (this.mScreenProjectionEnabled) {
            return hangUpNoUpdateLocked(true);
        }
        if (shouldNapAtBedTimeLocked()) {
            return napNoUpdateLocked(time, 1000);
        }
        return goToSleepNoUpdateLocked(time, 2, 0, 1000);
    }

    private boolean shouldNapAtBedTimeLocked() {
        return this.mDreamsActivateOnSleepSetting || (this.mDreamsActivateOnDockSetting && this.mDockState != 0);
    }

    private boolean isItBedTimeYetLocked() {
        return this.mBootCompleted && !isBeingKeptAwakeLocked();
    }

    private boolean isBeingKeptAwakeLocked() {
        return this.mStayOn || this.mProximityPositive || (this.mWakeLockSummary & 32) != 0 || (this.mUserActivitySummary & 3) != 0 || this.mScreenBrightnessBoostInProgress;
    }

    private void updateDreamLocked(int dirty, boolean displayBecameReady) {
        if (((dirty & 1015) != 0 || displayBecameReady) && this.mDisplayReady) {
            scheduleSandmanLocked();
        }
    }

    /* access modifiers changed from: private */
    public void scheduleSandmanLocked() {
        if (!this.mSandmanScheduled) {
            this.mSandmanScheduled = true;
            Message msg = this.mHandler.obtainMessage(2);
            msg.setAsynchronous(true);
            this.mHandler.sendMessage(msg);
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00b0, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x00f3, code lost:
        if (r4 == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x00f5, code lost:
        r15.mDreamManager.stopDream(false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleSandman() {
        /*
            r15 = this;
            java.lang.Object r0 = r15.mLock
            monitor-enter(r0)
            r1 = 0
            r15.mSandmanScheduled = r1     // Catch:{ all -> 0x0100 }
            int r2 = r15.mWakefulness     // Catch:{ all -> 0x0100 }
            boolean r3 = r15.mSandmanSummoned     // Catch:{ all -> 0x0100 }
            r4 = 1
            if (r3 == 0) goto L_0x0024
            boolean r3 = r15.mDisplayReady     // Catch:{ all -> 0x0100 }
            if (r3 == 0) goto L_0x0024
            boolean r3 = r15.canDreamLocked()     // Catch:{ all -> 0x0100 }
            if (r3 != 0) goto L_0x0020
            boolean r3 = r15.canDozeLocked()     // Catch:{ all -> 0x0100 }
            if (r3 == 0) goto L_0x001e
            goto L_0x0020
        L_0x001e:
            r3 = r1
            goto L_0x0021
        L_0x0020:
            r3 = r4
        L_0x0021:
            r15.mSandmanSummoned = r1     // Catch:{ all -> 0x0100 }
            goto L_0x0025
        L_0x0024:
            r3 = r1
        L_0x0025:
            monitor-exit(r0)     // Catch:{ all -> 0x0100 }
            android.service.dreams.DreamManagerInternal r0 = r15.mDreamManager
            r5 = 3
            if (r0 == 0) goto L_0x0041
            if (r3 == 0) goto L_0x0039
            r0.stopDream(r4)
            android.service.dreams.DreamManagerInternal r0 = r15.mDreamManager
            if (r2 != r5) goto L_0x0035
            goto L_0x0036
        L_0x0035:
            r4 = r1
        L_0x0036:
            r0.startDream(r4)
        L_0x0039:
            android.service.dreams.DreamManagerInternal r0 = r15.mDreamManager
            boolean r0 = r0.isDreaming()
            r4 = r0
            goto L_0x0043
        L_0x0041:
            r0 = 0
            r4 = r0
        L_0x0043:
            java.lang.Object r6 = r15.mLock
            monitor-enter(r6)
            if (r3 == 0) goto L_0x005f
            if (r4 == 0) goto L_0x005f
            int r0 = r15.mBatteryLevel     // Catch:{ all -> 0x00fd }
            r15.mBatteryLevelWhenDreamStarted = r0     // Catch:{ all -> 0x00fd }
            if (r2 != r5) goto L_0x0058
            java.lang.String r0 = "PowerManagerService"
            java.lang.String r7 = "Dozing..."
            android.util.Slog.i(r0, r7)     // Catch:{ all -> 0x00fd }
            goto L_0x005f
        L_0x0058:
            java.lang.String r0 = "PowerManagerService"
            java.lang.String r7 = "Dreaming..."
            android.util.Slog.i(r0, r7)     // Catch:{ all -> 0x00fd }
        L_0x005f:
            boolean r0 = r15.mSandmanSummoned     // Catch:{ all -> 0x00fd }
            if (r0 != 0) goto L_0x00fb
            int r0 = r15.mWakefulness     // Catch:{ all -> 0x00fd }
            if (r0 == r2) goto L_0x0069
            goto L_0x00fb
        L_0x0069:
            r0 = 2
            if (r2 != r0) goto L_0x00e0
            if (r4 == 0) goto L_0x00b1
            boolean r0 = r15.canDreamLocked()     // Catch:{ all -> 0x00fd }
            if (r0 == 0) goto L_0x00b1
            int r0 = r15.mDreamsBatteryLevelDrainCutoffConfig     // Catch:{ all -> 0x00fd }
            if (r0 < 0) goto L_0x00af
            int r0 = r15.mBatteryLevel     // Catch:{ all -> 0x00fd }
            int r5 = r15.mBatteryLevelWhenDreamStarted     // Catch:{ all -> 0x00fd }
            int r7 = r15.mDreamsBatteryLevelDrainCutoffConfig     // Catch:{ all -> 0x00fd }
            int r5 = r5 - r7
            if (r0 >= r5) goto L_0x00af
            boolean r0 = r15.isBeingKeptAwakeLocked()     // Catch:{ all -> 0x00fd }
            if (r0 != 0) goto L_0x00af
            java.lang.String r0 = "PowerManagerService"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x00fd }
            r5.<init>()     // Catch:{ all -> 0x00fd }
            java.lang.String r7 = "Stopping dream because the battery appears to be draining faster than it is charging.  Battery level when dream started: "
            r5.append(r7)     // Catch:{ all -> 0x00fd }
            int r7 = r15.mBatteryLevelWhenDreamStarted     // Catch:{ all -> 0x00fd }
            r5.append(r7)     // Catch:{ all -> 0x00fd }
            java.lang.String r7 = "%.  Battery level now: "
            r5.append(r7)     // Catch:{ all -> 0x00fd }
            int r7 = r15.mBatteryLevel     // Catch:{ all -> 0x00fd }
            r5.append(r7)     // Catch:{ all -> 0x00fd }
            java.lang.String r7 = "%."
            r5.append(r7)     // Catch:{ all -> 0x00fd }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x00fd }
            android.util.Slog.i(r0, r5)     // Catch:{ all -> 0x00fd }
            goto L_0x00b1
        L_0x00af:
            monitor-exit(r6)     // Catch:{ all -> 0x00fd }
            return
        L_0x00b1:
            boolean r0 = r15.isItBedTimeYetLocked()     // Catch:{ all -> 0x00fd }
            if (r0 == 0) goto L_0x00c7
            long r8 = android.os.SystemClock.uptimeMillis()     // Catch:{ all -> 0x00fd }
            r10 = 2
            r11 = 0
            r12 = 1000(0x3e8, float:1.401E-42)
            r7 = r15
            r7.goToSleepNoUpdateLocked(r8, r10, r11, r12)     // Catch:{ all -> 0x00fd }
            r15.updatePowerStateLocked()     // Catch:{ all -> 0x00fd }
            goto L_0x00f2
        L_0x00c7:
            long r8 = android.os.SystemClock.uptimeMillis()     // Catch:{ all -> 0x00fd }
            r10 = 0
            java.lang.String r11 = "android.server.power:DREAM_FINISHED"
            r12 = 1000(0x3e8, float:1.401E-42)
            android.content.Context r0 = r15.mContext     // Catch:{ all -> 0x00fd }
            java.lang.String r13 = r0.getOpPackageName()     // Catch:{ all -> 0x00fd }
            r14 = 1000(0x3e8, float:1.401E-42)
            r7 = r15
            r7.wakeUpNoUpdateLocked(r8, r10, r11, r12, r13, r14)     // Catch:{ all -> 0x00fd }
            r15.updatePowerStateLocked()     // Catch:{ all -> 0x00fd }
            goto L_0x00f2
        L_0x00e0:
            if (r2 != r5) goto L_0x00f2
            if (r4 == 0) goto L_0x00e6
            monitor-exit(r6)     // Catch:{ all -> 0x00fd }
            return
        L_0x00e6:
            long r7 = android.os.SystemClock.uptimeMillis()     // Catch:{ all -> 0x00fd }
            r0 = 1000(0x3e8, float:1.401E-42)
            r15.reallyGoToSleepNoUpdateLocked(r7, r0)     // Catch:{ all -> 0x00fd }
            r15.updatePowerStateLocked()     // Catch:{ all -> 0x00fd }
        L_0x00f2:
            monitor-exit(r6)     // Catch:{ all -> 0x00fd }
            if (r4 == 0) goto L_0x00fa
            android.service.dreams.DreamManagerInternal r0 = r15.mDreamManager
            r0.stopDream(r1)
        L_0x00fa:
            return
        L_0x00fb:
            monitor-exit(r6)     // Catch:{ all -> 0x00fd }
            return
        L_0x00fd:
            r0 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x00fd }
            throw r0
        L_0x0100:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0100 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.handleSandman():void");
    }

    private boolean canDreamLocked() {
        int i;
        int i2;
        if (this.mWakefulness != 2 || !this.mDreamsSupportedConfig || !this.mDreamsEnabledSetting || !this.mDisplayPowerRequest.isBrightOrDim() || this.mDisplayPowerRequest.isVr() || (this.mUserActivitySummary & 7) == 0 || !this.mBootCompleted) {
            return false;
        }
        if (isBeingKeptAwakeLocked()) {
            return true;
        }
        if (!this.mIsPowered && !this.mDreamsEnabledOnBatteryConfig) {
            return false;
        }
        if (!this.mIsPowered && (i2 = this.mDreamsBatteryLevelMinimumWhenNotPoweredConfig) >= 0 && this.mBatteryLevel < i2) {
            return false;
        }
        if (!this.mIsPowered || (i = this.mDreamsBatteryLevelMinimumWhenPoweredConfig) < 0 || this.mBatteryLevel >= i) {
            return true;
        }
        return false;
    }

    private boolean canDozeLocked() {
        return this.mWakefulness == 3;
    }

    /* access modifiers changed from: package-private */
    public void setWakeLockDirtyLocked() {
        this.mDirty |= 1;
    }

    private boolean updateDisplayPowerStateLocked(int dirty) {
        int screenBrightnessOverride;
        boolean autoBrightness;
        boolean oldDisplayReady = this.mDisplayReady;
        if ((dirty & 14399) != 0) {
            this.mDisplayPowerRequest.policy = getDesiredScreenPolicyLocked();
            if (!this.mActivityManagerReady) {
                autoBrightness = false;
                screenBrightnessOverride = this.mScreenBrightnessSettingDefault;
            } else if (isValidBrightness(this.mScreenBrightnessOverrideFromWindowManager)) {
                autoBrightness = false;
                screenBrightnessOverride = this.mScreenBrightnessOverrideFromWindowManager;
            } else {
                autoBrightness = this.mScreenBrightnessModeSetting == 1;
                screenBrightnessOverride = -1;
            }
            DisplayManagerInternal.DisplayPowerRequest displayPowerRequest = this.mDisplayPowerRequest;
            displayPowerRequest.screenBrightnessOverride = screenBrightnessOverride;
            displayPowerRequest.useAutoBrightness = autoBrightness;
            displayPowerRequest.useProximitySensor = shouldUseProximitySensorLocked();
            this.mDisplayPowerRequest.boostScreenBrightness = shouldBoostScreenBrightness();
            updatePowerRequestFromBatterySaverPolicy(this.mDisplayPowerRequest);
            if (this.mDisplayPowerRequest.policy == 1) {
                DisplayManagerInternal.DisplayPowerRequest displayPowerRequest2 = this.mDisplayPowerRequest;
                displayPowerRequest2.dozeScreenState = this.mDozeScreenStateOverrideFromDreamManager;
                if ((this.mWakeLockSummary & 128) != 0 && !this.mDrawWakeLockOverrideFromSidekick) {
                    if (displayPowerRequest2.dozeScreenState == 4) {
                        this.mDisplayPowerRequest.dozeScreenState = 3;
                    }
                    if (this.mDisplayPowerRequest.dozeScreenState == 6) {
                        this.mDisplayPowerRequest.dozeScreenState = 2;
                    }
                }
                this.mDisplayPowerRequest.dozeScreenBrightness = this.mDozeScreenBrightnessOverrideFromDreamManager;
            } else {
                DisplayManagerInternal.DisplayPowerRequest displayPowerRequest3 = this.mDisplayPowerRequest;
                displayPowerRequest3.dozeScreenState = 0;
                displayPowerRequest3.dozeScreenBrightness = -1;
            }
            this.mDisplayReady = this.mDisplayManagerInternal.requestPowerState(this.mDisplayPowerRequest, this.mRequestWaitForNegativeProximity);
            this.mRequestWaitForNegativeProximity = false;
            if ((dirty & 4096) != 0) {
                sQuiescent = false;
            }
        }
        if (!this.mDisplayReady || oldDisplayReady) {
            return false;
        }
        return true;
    }

    private void updateScreenBrightnessBoostLocked(int dirty) {
        if ((dirty & 2048) != 0 && this.mScreenBrightnessBoostInProgress) {
            long now = SystemClock.uptimeMillis();
            this.mHandler.removeMessages(3);
            long j = this.mLastScreenBrightnessBoostTime;
            if (j > this.mLastSleepTime) {
                long boostTimeout = j + 5000;
                if (boostTimeout > now) {
                    Message msg = this.mHandler.obtainMessage(3);
                    msg.setAsynchronous(true);
                    this.mHandler.sendMessageAtTime(msg, boostTimeout);
                    return;
                }
            }
            this.mScreenBrightnessBoostInProgress = false;
            this.mNotifier.onScreenBrightnessBoostChanged();
            userActivityNoUpdateLocked(now, 0, 0, 1000);
        }
    }

    private boolean shouldBoostScreenBrightness() {
        return !this.mIsVrModeEnabled && this.mScreenBrightnessBoostInProgress;
    }

    private static boolean isValidBrightness(int value) {
        return value >= 0 && value <= PowerManager.BRIGHTNESS_ON;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public int getDesiredScreenPolicyLocked() {
        int i = this.mWakefulness;
        if (i == 0 || sQuiescent) {
            return 0;
        }
        if (i == 3) {
            if ((this.mWakeLockSummary & 64) != 0) {
                return 1;
            }
            if (this.mDozeAfterScreenOff) {
                return 0;
            }
        }
        if (this.mIsVrModeEnabled) {
            return 4;
        }
        if (this.mScreenProjectionEnabled && this.mWakefulness == 4) {
            return 5;
        }
        if ((this.mWakeLockSummary & 2) != 0 || (this.mUserActivitySummary & 1) != 0 || !this.mBootCompleted || this.mScreenBrightnessBoostInProgress) {
            return 3;
        }
        return 2;
    }

    private boolean shouldUseProximitySensorLocked() {
        return !this.mIsVrModeEnabled && (this.mWakeLockSummary & 16) != 0;
    }

    private void updateSuspendBlockerLocked() {
        boolean needWakeLockSuspendBlocker = (this.mWakeLockSummary & 1) != 0;
        boolean needDisplaySuspendBlocker = needDisplaySuspendBlockerLocked();
        boolean autoSuspend = !needDisplaySuspendBlocker;
        boolean interactive = this.mDisplayPowerRequest.isBrightOrDim();
        if (!autoSuspend && this.mDecoupleHalAutoSuspendModeFromDisplayConfig) {
            setHalAutoSuspendModeLocked(false);
        }
        if (needWakeLockSuspendBlocker && !this.mHoldingWakeLockSuspendBlocker) {
            this.mWakeLockSuspendBlocker.acquire();
            this.mHoldingWakeLockSuspendBlocker = true;
        }
        if (needDisplaySuspendBlocker && !this.mHoldingDisplaySuspendBlocker) {
            this.mDisplaySuspendBlocker.acquire();
            this.mHoldingDisplaySuspendBlocker = true;
        }
        if (this.mDecoupleHalInteractiveModeFromDisplayConfig && (interactive || this.mDisplayReady)) {
            setHalInteractiveModeLocked(interactive);
        }
        if (!needWakeLockSuspendBlocker && this.mHoldingWakeLockSuspendBlocker) {
            this.mWakeLockSuspendBlocker.release();
            this.mHoldingWakeLockSuspendBlocker = false;
        }
        if (!needDisplaySuspendBlocker && this.mHoldingDisplaySuspendBlocker) {
            this.mDisplaySuspendBlocker.release();
            this.mHoldingDisplaySuspendBlocker = false;
        }
        if (autoSuspend && this.mDecoupleHalAutoSuspendModeFromDisplayConfig) {
            setHalAutoSuspendModeLocked(true);
        }
    }

    private boolean needDisplaySuspendBlockerLocked() {
        if (!this.mDisplayReady) {
            return true;
        }
        if ((!this.mDisplayPowerRequest.isBrightOrDim() || (this.mDisplayPowerRequest.useProximitySensor && this.mProximityPositive && this.mSuspendWhenScreenOffDueToProximityConfig)) && !this.mScreenBrightnessBoostInProgress) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public void setHalAutoSuspendModeLocked(boolean enable) {
        if (enable != this.mHalAutoSuspendModeEnabled) {
            this.mHalAutoSuspendModeEnabled = enable;
            Trace.traceBegin(131072, "setHalAutoSuspend(" + enable + ")");
            try {
                this.mNativeWrapper.nativeSetAutoSuspend(enable);
            } finally {
                Trace.traceEnd(131072);
            }
        }
    }

    /* access modifiers changed from: private */
    public void setHalInteractiveModeLocked(boolean enable) {
        if (enable != this.mHalInteractiveModeEnabled) {
            this.mHalInteractiveModeEnabled = enable;
            Trace.traceBegin(131072, "setHalInteractive(" + enable + ")");
            try {
                this.mNativeWrapper.nativeSetInteractive(enable);
            } finally {
                Trace.traceEnd(131072);
            }
        }
    }

    /* access modifiers changed from: private */
    public boolean isInteractiveInternal() {
        boolean isInteractive;
        synchronized (this.mLock) {
            isInteractive = PowerManagerInternal.isInteractive(this.mWakefulness);
        }
        return isInteractive;
    }

    /* access modifiers changed from: private */
    public boolean setLowPowerModeInternal(boolean enabled) {
        synchronized (this.mLock) {
            if (this.mIsPowered) {
                return false;
            }
            this.mBatterySaverStateMachine.setBatterySaverEnabledManually(enabled);
            return true;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isDeviceIdleModeInternal() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mDeviceIdleMode;
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    public boolean isLightDeviceIdleModeInternal() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mLightDeviceIdleMode;
        }
        return z;
    }

    /* access modifiers changed from: private */
    public void handleBatteryStateChangedLocked() {
        this.mDirty |= 256;
        updatePowerStateLocked();
    }

    /* access modifiers changed from: private */
    /*  JADX ERROR: JadxRuntimeException in pass: BlockFinish
        jadx.core.utils.exceptions.JadxRuntimeException: Dominance frontier not set for block: B:19:0x003e
        	at jadx.core.dex.nodes.BlockNode.lock(BlockNode.java:75)
        	at jadx.core.utils.ImmutableList.forEach(ImmutableList.java:108)
        	at jadx.core.dex.nodes.MethodNode.finishBasicBlocks(MethodNode.java:472)
        	at jadx.core.dex.visitors.blocksmaker.BlockFinish.visit(BlockFinish.java:27)
        */
    public void shutdownOrRebootInternal(int r4, boolean r5, java.lang.String r6, boolean r7) {
        /*
            r3 = this;
            com.android.server.power.PowerManagerService$PowerManagerHandler r0 = r3.mHandler
            if (r0 == 0) goto L_0x0008
            boolean r0 = r3.mSystemReady
            if (r0 != 0) goto L_0x0011
        L_0x0008:
            boolean r0 = com.android.server.RescueParty.isAttemptingFactoryReset()
            if (r0 == 0) goto L_0x0044
            lowLevelReboot(r6)
        L_0x0011:
            android.content.Context r0 = r3.mContext
            r1 = 1
            if (r4 != 0) goto L_0x0018
            r2 = r1
            goto L_0x0019
        L_0x0018:
            r2 = 0
        L_0x0019:
            boolean r0 = com.android.server.power.PowerManagerServiceInjector.isShutdownOrRebootPermitted(r0, r2, r5, r6, r7)
            if (r0 != 0) goto L_0x0020
            return
        L_0x0020:
            com.android.server.power.PowerManagerService$2 r0 = new com.android.server.power.PowerManagerService$2
            r0.<init>(r4, r5, r6)
            android.os.Handler r2 = com.android.server.UiThread.getHandler()
            android.os.Message r2 = android.os.Message.obtain(r2, r0)
            r2.setAsynchronous(r1)
            android.os.Handler r1 = com.android.server.UiThread.getHandler()
            r1.sendMessage(r2)
            if (r7 == 0) goto L_0x0043
            monitor-enter(r0)
        L_0x003a:
            r0.wait()     // Catch:{ InterruptedException -> 0x0041 }
        L_0x003d:
            goto L_0x003a
        L_0x003e:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003e }
            throw r1
        L_0x0041:
            r1 = move-exception
            goto L_0x003d
        L_0x0043:
            return
        L_0x0044:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException
            java.lang.String r1 = "Too early to call shutdown() or reboot()"
            r0.<init>(r1)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.shutdownOrRebootInternal(int, boolean, java.lang.String, boolean):void");
    }

    /* access modifiers changed from: private */
    public void crashInternal(final String message) {
        Thread t = new Thread("PowerManagerService.crash()") {
            public void run() {
                throw new RuntimeException(message);
            }
        };
        try {
            t.start();
            t.join();
        } catch (InterruptedException e) {
            Slog.wtf(TAG, e);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void updatePowerRequestFromBatterySaverPolicy(DisplayManagerInternal.DisplayPowerRequest displayPowerRequest) {
        PowerSaveState state = this.mBatterySaverPolicy.getBatterySaverPolicy(7);
        displayPowerRequest.lowPowerMode = state.batterySaverEnabled;
        displayPowerRequest.screenLowPowerBrightnessFactor = state.brightnessFactor;
    }

    /* access modifiers changed from: package-private */
    public void setStayOnSettingInternal(int val) {
        Settings.Global.putInt(this.mContext.getContentResolver(), "stay_on_while_plugged_in", val);
    }

    /* access modifiers changed from: package-private */
    public void setMaximumScreenOffTimeoutFromDeviceAdminInternal(int userId, long timeMs) {
        if (userId < 0) {
            Slog.wtf(TAG, "Attempt to set screen off timeout for invalid user: " + userId);
            return;
        }
        synchronized (this.mLock) {
            if (userId == 0) {
                try {
                    this.mMaximumScreenOffTimeoutFromDeviceAdmin = timeMs;
                } catch (Throwable th) {
                    throw th;
                }
            } else {
                if (timeMs != JobStatus.NO_LATEST_RUNTIME) {
                    if (timeMs != 0) {
                        ProfilePowerState profile = this.mProfilePowerState.get(userId);
                        if (profile != null) {
                            profile.mScreenOffTimeout = timeMs;
                        } else {
                            this.mProfilePowerState.put(userId, new ProfilePowerState(userId, timeMs));
                            this.mDirty |= 1;
                        }
                    }
                }
                this.mProfilePowerState.delete(userId);
            }
            this.mDirty |= 32;
            updatePowerStateLocked();
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0010, code lost:
        if (r3 == false) goto L_0x0019;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0012, code lost:
        com.android.server.EventLogTags.writeDeviceIdleOnPhase("power");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0019, code lost:
        com.android.server.EventLogTags.writeDeviceIdleOffPhase("power");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:?, code lost:
        return true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean setDeviceIdleModeInternal(boolean r3) {
        /*
            r2 = this;
            java.lang.Object r0 = r2.mLock
            monitor-enter(r0)
            boolean r1 = r2.mDeviceIdleMode     // Catch:{ all -> 0x0021 }
            if (r1 != r3) goto L_0x000a
            r1 = 0
            monitor-exit(r0)     // Catch:{ all -> 0x0021 }
            return r1
        L_0x000a:
            r2.mDeviceIdleMode = r3     // Catch:{ all -> 0x0021 }
            r2.updateWakeLockDisabledStatesLocked()     // Catch:{ all -> 0x0021 }
            monitor-exit(r0)     // Catch:{ all -> 0x0021 }
            if (r3 == 0) goto L_0x0019
            java.lang.String r0 = "power"
            com.android.server.EventLogTags.writeDeviceIdleOnPhase(r0)
            goto L_0x001f
        L_0x0019:
            java.lang.String r0 = "power"
            com.android.server.EventLogTags.writeDeviceIdleOffPhase(r0)
        L_0x001f:
            r0 = 1
            return r0
        L_0x0021:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0021 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.setDeviceIdleModeInternal(boolean):boolean");
    }

    /* access modifiers changed from: package-private */
    public boolean setLightDeviceIdleModeInternal(boolean enabled) {
        synchronized (this.mLock) {
            if (this.mLightDeviceIdleMode == enabled) {
                return false;
            }
            this.mLightDeviceIdleMode = enabled;
            return true;
        }
    }

    /* access modifiers changed from: package-private */
    public void setDeviceIdleWhitelistInternal(int[] appids) {
        synchronized (this.mLock) {
            this.mDeviceIdleWhitelist = appids;
            if (this.mDeviceIdleMode) {
                updateWakeLockDisabledStatesLocked();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setDeviceIdleTempWhitelistInternal(int[] appids) {
        synchronized (this.mLock) {
            this.mDeviceIdleTempWhitelist = appids;
            if (this.mDeviceIdleMode) {
                updateWakeLockDisabledStatesLocked();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void startUidChangesInternal() {
        synchronized (this.mLock) {
            this.mUidsChanging = true;
        }
    }

    /* access modifiers changed from: package-private */
    public void finishUidChangesInternal() {
        synchronized (this.mLock) {
            this.mUidsChanging = false;
            if (this.mUidsChanged) {
                updateWakeLockDisabledStatesLocked();
                this.mUidsChanged = false;
            }
        }
    }

    private void handleUidStateChangeLocked() {
        if (this.mUidsChanging) {
            this.mUidsChanged = true;
        } else {
            updateWakeLockDisabledStatesLocked();
        }
    }

    /* access modifiers changed from: package-private */
    public void updateUidProcStateInternal(int uid, int procState) {
        synchronized (this.mLock) {
            UidState state = this.mUidState.get(uid);
            if (state == null) {
                state = new UidState(uid);
                this.mUidState.put(uid, state);
            }
            boolean z = true;
            boolean oldShouldAllow = state.mProcState <= 12;
            state.mProcState = procState;
            if (state.mNumWakeLocks > 0) {
                if (this.mDeviceIdleMode) {
                    handleUidStateChangeLocked();
                } else if (!state.mActive) {
                    if (procState > 12) {
                        z = false;
                    }
                    if (oldShouldAllow != z) {
                        handleUidStateChangeLocked();
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void uidGoneInternal(int uid) {
        synchronized (this.mLock) {
            int index = this.mUidState.indexOfKey(uid);
            if (index >= 0) {
                UidState state = this.mUidState.valueAt(index);
                state.mProcState = 21;
                state.mActive = false;
                this.mUidState.removeAt(index);
                if (this.mDeviceIdleMode && state.mNumWakeLocks > 0) {
                    handleUidStateChangeLocked();
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void uidActiveInternal(int uid) {
        synchronized (this.mLock) {
            UidState state = this.mUidState.get(uid);
            if (state == null) {
                state = new UidState(uid);
                state.mProcState = 20;
                this.mUidState.put(uid, state);
            }
            state.mActive = true;
            if (state.mNumWakeLocks > 0) {
                handleUidStateChangeLocked();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void uidIdleInternal(int uid) {
        synchronized (this.mLock) {
            UidState state = this.mUidState.get(uid);
            if (state != null) {
                state.mActive = false;
                if (state.mNumWakeLocks > 0) {
                    handleUidStateChangeLocked();
                }
            }
        }
    }

    private void updateWakeLockDisabledStatesLocked() {
        boolean changed = false;
        int numWakeLocks = this.mWakeLocks.size();
        for (int i = 0; i < numWakeLocks; i++) {
            WakeLock wakeLock = this.mWakeLocks.get(i);
            if ((wakeLock.mFlags & 65535) == 1 && setWakeLockDisabledStateLocked(wakeLock)) {
                changed = true;
                if (wakeLock.mDisabled) {
                    notifyWakeLockReleasedLocked(wakeLock);
                } else {
                    notifyWakeLockAcquiredLocked(wakeLock);
                }
            }
        }
        if (changed) {
            this.mDirty |= 1;
            updatePowerStateLocked();
        }
    }

    private boolean setWakeLockDisabledStateLocked(WakeLock wakeLock) {
        if ((wakeLock.mFlags & 65535) == 1) {
            boolean disabled = false;
            int appid = UserHandle.getAppId(wakeLock.mOwnerUid);
            if (appid >= 10000) {
                if (this.mConstants.NO_CACHED_WAKE_LOCKS) {
                    disabled = this.mForceSuspendActive || (!wakeLock.mUidState.mActive && wakeLock.mUidState.mProcState != 21 && wakeLock.mUidState.mProcState > 12);
                }
                if (this.mDeviceIdleMode) {
                    UidState state = wakeLock.mUidState;
                    if (Arrays.binarySearch(this.mDeviceIdleWhitelist, appid) < 0 && Arrays.binarySearch(this.mDeviceIdleTempWhitelist, appid) < 0 && state.mProcState != 21 && state.mProcState > 6) {
                        disabled = true;
                    }
                }
            }
            if (wakeLock.mDisabled && !disabled) {
                disabled = PowerManagerServiceInjector.isWakelockDisabledByPolicy(wakeLock);
            }
            if (wakeLock.mDisabled != disabled) {
                wakeLock.mDisabled = disabled;
                return true;
            }
        }
        return false;
    }

    private boolean isMaximumScreenOffTimeoutFromDeviceAdminEnforcedLocked() {
        long j = this.mMaximumScreenOffTimeoutFromDeviceAdmin;
        return j >= 0 && j < JobStatus.NO_LATEST_RUNTIME;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x000e, code lost:
        if (r5 == false) goto L_0x0012;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0010, code lost:
        r3 = 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0012, code lost:
        r3 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0013, code lost:
        r1.setFlashing(r6, 2, r3, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0016, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setAttentionLightInternal(boolean r5, int r6) {
        /*
            r4 = this;
            java.lang.Object r0 = r4.mLock
            monitor-enter(r0)
            boolean r1 = r4.mSystemReady     // Catch:{ all -> 0x0017 }
            if (r1 != 0) goto L_0x0009
            monitor-exit(r0)     // Catch:{ all -> 0x0017 }
            return
        L_0x0009:
            com.android.server.lights.Light r1 = r4.mAttentionLight     // Catch:{ all -> 0x0017 }
            monitor-exit(r0)     // Catch:{ all -> 0x0017 }
            r0 = 2
            r2 = 0
            if (r5 == 0) goto L_0x0012
            r3 = 3
            goto L_0x0013
        L_0x0012:
            r3 = r2
        L_0x0013:
            r1.setFlashing(r6, r0, r3, r2)
            return
        L_0x0017:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0017 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.setAttentionLightInternal(boolean, int):void");
    }

    /* access modifiers changed from: private */
    public void setDozeAfterScreenOffInternal(boolean on) {
        synchronized (this.mLock) {
            this.mDozeAfterScreenOff = on;
        }
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x004f, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void boostScreenBrightnessInternal(long r9, int r11) {
        /*
            r8 = this;
            java.lang.Object r0 = r8.mLock
            monitor-enter(r0)
            boolean r1 = r8.mSystemReady     // Catch:{ all -> 0x0050 }
            if (r1 == 0) goto L_0x004e
            int r1 = r8.mWakefulness     // Catch:{ all -> 0x0050 }
            if (r1 == 0) goto L_0x004e
            long r1 = r8.mLastScreenBrightnessBoostTime     // Catch:{ all -> 0x0050 }
            int r1 = (r9 > r1 ? 1 : (r9 == r1 ? 0 : -1))
            if (r1 >= 0) goto L_0x0012
            goto L_0x004e
        L_0x0012:
            java.lang.String r1 = "PowerManagerService"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0050 }
            r2.<init>()     // Catch:{ all -> 0x0050 }
            java.lang.String r3 = "Brightness boost activated (uid "
            r2.append(r3)     // Catch:{ all -> 0x0050 }
            r2.append(r11)     // Catch:{ all -> 0x0050 }
            java.lang.String r3 = ")..."
            r2.append(r3)     // Catch:{ all -> 0x0050 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0050 }
            android.util.Slog.i(r1, r2)     // Catch:{ all -> 0x0050 }
            r8.mLastScreenBrightnessBoostTime = r9     // Catch:{ all -> 0x0050 }
            boolean r1 = r8.mScreenBrightnessBoostInProgress     // Catch:{ all -> 0x0050 }
            if (r1 != 0) goto L_0x003b
            r1 = 1
            r8.mScreenBrightnessBoostInProgress = r1     // Catch:{ all -> 0x0050 }
            com.android.server.power.Notifier r1 = r8.mNotifier     // Catch:{ all -> 0x0050 }
            r1.onScreenBrightnessBoostChanged()     // Catch:{ all -> 0x0050 }
        L_0x003b:
            int r1 = r8.mDirty     // Catch:{ all -> 0x0050 }
            r1 = r1 | 2048(0x800, float:2.87E-42)
            r8.mDirty = r1     // Catch:{ all -> 0x0050 }
            r5 = 0
            r6 = 0
            r2 = r8
            r3 = r9
            r7 = r11
            r2.userActivityNoUpdateLocked(r3, r5, r6, r7)     // Catch:{ all -> 0x0050 }
            r8.updatePowerStateLocked()     // Catch:{ all -> 0x0050 }
            monitor-exit(r0)     // Catch:{ all -> 0x0050 }
            return
        L_0x004e:
            monitor-exit(r0)     // Catch:{ all -> 0x0050 }
            return
        L_0x0050:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0050 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.boostScreenBrightnessInternal(long, int):void");
    }

    /* access modifiers changed from: private */
    public boolean isScreenBrightnessBoostedInternal() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mScreenBrightnessBoostInProgress;
        }
        return z;
    }

    /* access modifiers changed from: private */
    public void handleScreenBrightnessBoostTimeout() {
        synchronized (this.mLock) {
            this.mDirty |= 2048;
            updatePowerStateLocked();
        }
    }

    /* access modifiers changed from: private */
    public void setScreenBrightnessOverrideFromWindowManagerInternal(int brightness) {
        synchronized (this.mLock) {
            if (this.mScreenBrightnessOverrideFromWindowManager != brightness) {
                this.mScreenBrightnessOverrideFromWindowManager = brightness;
                this.mDirty |= 32;
                updatePowerStateLocked();
            }
        }
    }

    /* access modifiers changed from: private */
    public void setUserInactiveOverrideFromWindowManagerInternal() {
        synchronized (this.mLock) {
            this.mUserInactiveOverrideFromWindowManager = true;
            this.mDirty |= 4;
            updatePowerStateLocked();
        }
    }

    /* access modifiers changed from: private */
    public void setUserActivityTimeoutOverrideFromWindowManagerInternal(long timeoutMillis) {
        synchronized (this.mLock) {
            if (this.mUserActivityTimeoutOverrideFromWindowManager != timeoutMillis) {
                this.mUserActivityTimeoutOverrideFromWindowManager = timeoutMillis;
                EventLogTags.writeUserActivityTimeoutOverride(timeoutMillis);
                this.mDirty |= 32;
                updatePowerStateLocked();
            }
        }
    }

    /* access modifiers changed from: private */
    public void setDozeOverrideFromDreamManagerInternal(int screenState, int screenBrightness) {
        synchronized (this.mLock) {
            if (!(this.mDozeScreenStateOverrideFromDreamManager == screenState && this.mDozeScreenBrightnessOverrideFromDreamManager == screenBrightness)) {
                this.mDozeScreenStateOverrideFromDreamManager = screenState;
                this.mDozeScreenBrightnessOverrideFromDreamManager = screenBrightness;
                this.mDirty |= 32;
                updatePowerStateLocked();
            }
        }
    }

    /* access modifiers changed from: private */
    public void setDrawWakeLockOverrideFromSidekickInternal(boolean keepState) {
        synchronized (this.mLock) {
            if (this.mDrawWakeLockOverrideFromSidekick != keepState) {
                this.mDrawWakeLockOverrideFromSidekick = keepState;
                this.mDirty |= 32;
                updatePowerStateLocked();
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setVrModeEnabled(boolean enabled) {
        this.mIsVrModeEnabled = enabled;
    }

    /* access modifiers changed from: private */
    public void powerHintInternal(int hintId, int data) {
        if (hintId != 8 || data != 1 || !this.mBatterySaverController.isLaunchBoostDisabled()) {
            this.mNativeWrapper.nativeSendPowerHint(hintId, data);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean wasDeviceIdleForInternal(long ms) {
        boolean z;
        synchronized (this.mLock) {
            z = this.mLastUserActivityTime + ms < SystemClock.uptimeMillis();
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void onUserActivity() {
        synchronized (this.mLock) {
            this.mLastUserActivityTime = SystemClock.uptimeMillis();
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    /* access modifiers changed from: private */
    public boolean forceSuspendInternal(int uid) {
        try {
            synchronized (this.mLock) {
                this.mForceSuspendActive = true;
                goToSleepInternal(SystemClock.uptimeMillis(), 8, 1, uid);
                updateWakeLockDisabledStatesLocked();
            }
            Slog.i(TAG, "Force-Suspending (uid " + uid + ")...");
            boolean success = this.mNativeWrapper.nativeForceSuspend();
            if (!success) {
                Slog.i(TAG, "Force-Suspending failed in native.");
            }
            synchronized (this.mLock) {
                this.mForceSuspendActive = false;
                updateWakeLockDisabledStatesLocked();
            }
            return success;
        } catch (Throwable th) {
            synchronized (this.mLock) {
                this.mForceSuspendActive = false;
                updateWakeLockDisabledStatesLocked();
                throw th;
            }
        }
    }

    public static void lowLevelShutdown(String reason) {
        if (reason == null) {
            reason = "";
        }
        SystemProperties.set("sys.powerctl", "shutdown," + reason);
    }

    public static void lowLevelReboot(String reason) {
        if (reason == null) {
            reason = "";
        }
        if (reason.equals("quiescent")) {
            sQuiescent = true;
            reason = "";
        } else if (reason.endsWith(",quiescent")) {
            sQuiescent = true;
            reason = reason.substring(0, (reason.length() - "quiescent".length()) - 1);
        }
        if (reason.equals("recovery") || reason.equals("recovery-update")) {
            PowerManagerServiceInjector.recordShutDownTime();
            reason = "recovery";
        }
        if (sQuiescent) {
            reason = reason + ",quiescent";
        }
        SystemProperties.set("sys.powerctl", "reboot," + reason);
        try {
            Thread.sleep(ActivityManagerServiceInjector.KEEP_FOREGROUND_DURATION);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        Slog.wtf(TAG, "Unexpected return from lowLevelReboot!");
    }

    public void monitor() {
        synchronized (this.mLock) {
        }
    }

    /* access modifiers changed from: private */
    public void dumpInternal(PrintWriter pw) {
        WirelessChargerDetector wcd;
        pw.println("POWER MANAGER (dumpsys power)\n");
        synchronized (this.mLock) {
            pw.println("Power Manager State:");
            this.mConstants.dump(pw);
            pw.println("  mDirty=0x" + Integer.toHexString(this.mDirty));
            pw.println("  mWakefulness=" + PowerManagerInternal.wakefulnessToString(this.mWakefulness));
            pw.println("  mWakefulnessChanging=" + this.mWakefulnessChanging);
            pw.println("  mIsPowered=" + this.mIsPowered);
            pw.println("  mPlugType=" + this.mPlugType);
            pw.println("  mBatteryLevel=" + this.mBatteryLevel);
            pw.println("  mBatteryLevelWhenDreamStarted=" + this.mBatteryLevelWhenDreamStarted);
            pw.println("  mDockState=" + this.mDockState);
            pw.println("  mStayOn=" + this.mStayOn);
            pw.println("  mProximityPositive=" + this.mProximityPositive);
            pw.println("  mBootCompleted=" + this.mBootCompleted);
            pw.println("  mSystemReady=" + this.mSystemReady);
            pw.println("  mHalAutoSuspendModeEnabled=" + this.mHalAutoSuspendModeEnabled);
            pw.println("  mHalInteractiveModeEnabled=" + this.mHalInteractiveModeEnabled);
            pw.println("  mWakeLockSummary=0x" + Integer.toHexString(this.mWakeLockSummary));
            pw.print("  mNotifyLongScheduled=");
            if (this.mNotifyLongScheduled == 0) {
                pw.print("(none)");
            } else {
                TimeUtils.formatDuration(this.mNotifyLongScheduled, SystemClock.uptimeMillis(), pw);
            }
            pw.println();
            pw.print("  mNotifyLongDispatched=");
            if (this.mNotifyLongDispatched == 0) {
                pw.print("(none)");
            } else {
                TimeUtils.formatDuration(this.mNotifyLongDispatched, SystemClock.uptimeMillis(), pw);
            }
            pw.println();
            pw.print("  mNotifyLongNextCheck=");
            if (this.mNotifyLongNextCheck == 0) {
                pw.print("(none)");
            } else {
                TimeUtils.formatDuration(this.mNotifyLongNextCheck, SystemClock.uptimeMillis(), pw);
            }
            pw.println();
            pw.println("  mUserActivitySummary=0x" + Integer.toHexString(this.mUserActivitySummary));
            pw.println("  mRequestWaitForNegativeProximity=" + this.mRequestWaitForNegativeProximity);
            pw.println("  mSandmanScheduled=" + this.mSandmanScheduled);
            pw.println("  mSandmanSummoned=" + this.mSandmanSummoned);
            pw.println("  mBatteryLevelLow=" + this.mBatteryLevelLow);
            pw.println("  mLightDeviceIdleMode=" + this.mLightDeviceIdleMode);
            pw.println("  mDeviceIdleMode=" + this.mDeviceIdleMode);
            pw.println("  mDeviceIdleWhitelist=" + Arrays.toString(this.mDeviceIdleWhitelist));
            pw.println("  mDeviceIdleTempWhitelist=" + Arrays.toString(this.mDeviceIdleTempWhitelist));
            pw.println("  mLastWakeTime=" + TimeUtils.formatUptime(this.mLastWakeTime));
            pw.println("  mLastSleepTime=" + TimeUtils.formatUptime(this.mLastSleepTime));
            pw.println("  mLastSleepReason=" + PowerManager.sleepReasonToString(this.mLastSleepReason));
            pw.println("  mLastUserActivityTime=" + TimeUtils.formatUptime(this.mLastUserActivityTime));
            pw.println("  mLastUserActivityTimeNoChangeLights=" + TimeUtils.formatUptime(this.mLastUserActivityTimeNoChangeLights));
            pw.println("  mLastInteractivePowerHintTime=" + TimeUtils.formatUptime(this.mLastInteractivePowerHintTime));
            pw.println("  mLastScreenBrightnessBoostTime=" + TimeUtils.formatUptime(this.mLastScreenBrightnessBoostTime));
            pw.println("  mScreenBrightnessBoostInProgress=" + this.mScreenBrightnessBoostInProgress);
            pw.println("  mDisplayReady=" + this.mDisplayReady);
            pw.println("  mHoldingWakeLockSuspendBlocker=" + this.mHoldingWakeLockSuspendBlocker);
            pw.println("  mHoldingDisplaySuspendBlocker=" + this.mHoldingDisplaySuspendBlocker);
            pw.println();
            pw.println("Settings and Configuration:");
            pw.println("  mDecoupleHalAutoSuspendModeFromDisplayConfig=" + this.mDecoupleHalAutoSuspendModeFromDisplayConfig);
            pw.println("  mDecoupleHalInteractiveModeFromDisplayConfig=" + this.mDecoupleHalInteractiveModeFromDisplayConfig);
            pw.println("  mWakeUpWhenPluggedOrUnpluggedConfig=" + this.mWakeUpWhenPluggedOrUnpluggedConfig);
            pw.println("  mWakeUpWhenPluggedOrUnpluggedInTheaterModeConfig=" + this.mWakeUpWhenPluggedOrUnpluggedInTheaterModeConfig);
            pw.println("  mTheaterModeEnabled=" + this.mTheaterModeEnabled);
            pw.println("  mSuspendWhenScreenOffDueToProximityConfig=" + this.mSuspendWhenScreenOffDueToProximityConfig);
            pw.println("  mDreamsSupportedConfig=" + this.mDreamsSupportedConfig);
            pw.println("  mDreamsEnabledByDefaultConfig=" + this.mDreamsEnabledByDefaultConfig);
            pw.println("  mDreamsActivatedOnSleepByDefaultConfig=" + this.mDreamsActivatedOnSleepByDefaultConfig);
            pw.println("  mDreamsActivatedOnDockByDefaultConfig=" + this.mDreamsActivatedOnDockByDefaultConfig);
            pw.println("  mDreamsEnabledOnBatteryConfig=" + this.mDreamsEnabledOnBatteryConfig);
            pw.println("  mDreamsBatteryLevelMinimumWhenPoweredConfig=" + this.mDreamsBatteryLevelMinimumWhenPoweredConfig);
            pw.println("  mDreamsBatteryLevelMinimumWhenNotPoweredConfig=" + this.mDreamsBatteryLevelMinimumWhenNotPoweredConfig);
            pw.println("  mDreamsBatteryLevelDrainCutoffConfig=" + this.mDreamsBatteryLevelDrainCutoffConfig);
            pw.println("  mDreamsEnabledSetting=" + this.mDreamsEnabledSetting);
            pw.println("  mDreamsActivateOnSleepSetting=" + this.mDreamsActivateOnSleepSetting);
            pw.println("  mDreamsActivateOnDockSetting=" + this.mDreamsActivateOnDockSetting);
            pw.println("  mDozeAfterScreenOff=" + this.mDozeAfterScreenOff);
            pw.println("  mMinimumScreenOffTimeoutConfig=" + this.mMinimumScreenOffTimeoutConfig);
            pw.println("  mMaximumScreenDimDurationConfig=" + this.mMaximumScreenDimDurationConfig);
            pw.println("  mMaximumScreenDimRatioConfig=" + this.mMaximumScreenDimRatioConfig);
            pw.println("  mScreenOffTimeoutSetting=" + this.mScreenOffTimeoutSetting);
            pw.println("  mSleepTimeoutSetting=" + this.mSleepTimeoutSetting);
            pw.println("  mMaximumScreenOffTimeoutFromDeviceAdmin=" + this.mMaximumScreenOffTimeoutFromDeviceAdmin + " (enforced=" + isMaximumScreenOffTimeoutFromDeviceAdminEnforcedLocked() + ")");
            StringBuilder sb = new StringBuilder();
            sb.append("  mStayOnWhilePluggedInSetting=");
            sb.append(this.mStayOnWhilePluggedInSetting);
            pw.println(sb.toString());
            pw.println("  mScreenBrightnessSetting=" + this.mScreenBrightnessSetting);
            pw.println("  mScreenBrightnessModeSetting=" + this.mScreenBrightnessModeSetting);
            pw.println("  mScreenBrightnessOverrideFromWindowManager=" + this.mScreenBrightnessOverrideFromWindowManager);
            pw.println("  mUserActivityTimeoutOverrideFromWindowManager=" + this.mUserActivityTimeoutOverrideFromWindowManager);
            pw.println("  mUserInactiveOverrideFromWindowManager=" + this.mUserInactiveOverrideFromWindowManager);
            pw.println("  mDozeScreenStateOverrideFromDreamManager=" + this.mDozeScreenStateOverrideFromDreamManager);
            pw.println("  mDrawWakeLockOverrideFromSidekick=" + this.mDrawWakeLockOverrideFromSidekick);
            pw.println("  mDozeScreenBrightnessOverrideFromDreamManager=" + this.mDozeScreenBrightnessOverrideFromDreamManager);
            pw.println("  mScreenBrightnessSettingMinimum=" + this.mScreenBrightnessSettingMinimum);
            pw.println("  mScreenBrightnessSettingMaximum=" + this.mScreenBrightnessSettingMaximum);
            pw.println("  mScreenBrightnessSettingDefault=" + this.mScreenBrightnessSettingDefault);
            pw.println("  mDoubleTapWakeEnabled=" + this.mDoubleTapWakeEnabled);
            pw.println("  mIsVrModeEnabled=" + this.mIsVrModeEnabled);
            pw.println("  mForegroundProfile=" + this.mForegroundProfile);
            long sleepTimeout = getSleepTimeoutLocked();
            long screenOffTimeout = getScreenOffTimeoutLocked(sleepTimeout);
            long screenDimDuration = getScreenDimDurationLocked(screenOffTimeout);
            pw.println();
            pw.println("Sleep timeout: " + sleepTimeout + " ms");
            pw.println("Screen off timeout: " + screenOffTimeout + " ms");
            pw.println("Screen dim duration: " + screenDimDuration + " ms");
            pw.println();
            pw.print("UID states (changing=");
            pw.print(this.mUidsChanging);
            pw.print(" changed=");
            pw.print(this.mUidsChanged);
            pw.println("):");
            for (int i = 0; i < this.mUidState.size(); i++) {
                UidState state = this.mUidState.valueAt(i);
                pw.print("  UID ");
                UserHandle.formatUid(pw, this.mUidState.keyAt(i));
                pw.print(": ");
                if (state.mActive) {
                    pw.print("  ACTIVE ");
                } else {
                    pw.print("INACTIVE ");
                }
                pw.print(" count=");
                pw.print(state.mNumWakeLocks);
                pw.print(" state=");
                pw.println(state.mProcState);
            }
            pw.println();
            pw.println("Looper state:");
            this.mHandler.getLooper().dump(new PrintWriterPrinter(pw), "  ");
            pw.println();
            pw.println("Wake Locks: size=" + this.mWakeLocks.size());
            Iterator<WakeLock> it = this.mWakeLocks.iterator();
            while (it.hasNext()) {
                pw.println("  " + it.next());
            }
            pw.println();
            pw.println("Suspend Blockers: size=" + this.mSuspendBlockers.size());
            Iterator<SuspendBlocker> it2 = this.mSuspendBlockers.iterator();
            while (it2.hasNext()) {
                pw.println("  " + it2.next());
            }
            pw.println();
            pw.println("Display Power: " + this.mDisplayPowerCallbacks);
            this.mBatterySaverPolicy.dump(pw);
            this.mBatterySaverStateMachine.dump(pw);
            this.mAttentionDetector.dump(pw);
            pw.println();
            int numProfiles = this.mProfilePowerState.size();
            pw.println("Profile power states: size=" + numProfiles);
            for (int i2 = 0; i2 < numProfiles; i2++) {
                ProfilePowerState profile = this.mProfilePowerState.valueAt(i2);
                pw.print("  mUserId=");
                pw.print(profile.mUserId);
                pw.print(" mScreenOffTimeout=");
                pw.print(profile.mScreenOffTimeout);
                pw.print(" mWakeLockSummary=");
                pw.print(profile.mWakeLockSummary);
                pw.print(" mLastUserActivityTime=");
                pw.print(profile.mLastUserActivityTime);
                pw.print(" mLockingNotified=");
                pw.println(profile.mLockingNotified);
            }
            wcd = this.mWirelessChargerDetector;
        }
        if (wcd != null) {
            wcd.dump(pw);
        }
    }

    /* access modifiers changed from: private */
    public void dumpProto(FileDescriptor fd) {
        WirelessChargerDetector wcd;
        ProtoOutputStream proto = new ProtoOutputStream(fd);
        synchronized (this.mLock) {
            this.mConstants.dumpProto(proto);
            proto.write(1120986464258L, this.mDirty);
            proto.write(1159641169923L, this.mWakefulness);
            proto.write(1133871366148L, this.mWakefulnessChanging);
            proto.write(1133871366149L, this.mIsPowered);
            proto.write(1159641169926L, this.mPlugType);
            proto.write(1120986464263L, this.mBatteryLevel);
            proto.write(1120986464264L, this.mBatteryLevelWhenDreamStarted);
            proto.write(1159641169929L, this.mDockState);
            proto.write(1133871366154L, this.mStayOn);
            proto.write(1133871366155L, this.mProximityPositive);
            proto.write(1133871366156L, this.mBootCompleted);
            proto.write(1133871366157L, this.mSystemReady);
            proto.write(1133871366158L, this.mHalAutoSuspendModeEnabled);
            proto.write(1133871366159L, this.mHalInteractiveModeEnabled);
            long activeWakeLocksToken = proto.start(1146756268048L);
            proto.write(1133871366145L, (this.mWakeLockSummary & 1) != 0);
            proto.write(1133871366146L, (this.mWakeLockSummary & 2) != 0);
            proto.write(1133871366147L, (this.mWakeLockSummary & 4) != 0);
            proto.write(1133871366148L, (this.mWakeLockSummary & 8) != 0);
            proto.write(1133871366149L, (this.mWakeLockSummary & 16) != 0);
            proto.write(1133871366150L, (this.mWakeLockSummary & 32) != 0);
            proto.write(1133871366151L, (this.mWakeLockSummary & 64) != 0);
            proto.write(1133871366152L, (this.mWakeLockSummary & 128) != 0);
            proto.end(activeWakeLocksToken);
            proto.write(1112396529681L, this.mNotifyLongScheduled);
            proto.write(1112396529682L, this.mNotifyLongDispatched);
            proto.write(1112396529683L, this.mNotifyLongNextCheck);
            long userActivityToken = proto.start(1146756268052L);
            proto.write(1133871366145L, (this.mUserActivitySummary & 1) != 0);
            proto.write(1133871366146L, (this.mUserActivitySummary & 2) != 0);
            proto.write(1133871366147L, (this.mUserActivitySummary & 4) != 0);
            proto.end(userActivityToken);
            proto.write(1133871366165L, this.mRequestWaitForNegativeProximity);
            proto.write(1133871366166L, this.mSandmanScheduled);
            proto.write(1133871366167L, this.mSandmanSummoned);
            proto.write(1133871366168L, this.mBatteryLevelLow);
            proto.write(1133871366169L, this.mLightDeviceIdleMode);
            proto.write(1133871366170L, this.mDeviceIdleMode);
            for (int id : this.mDeviceIdleWhitelist) {
                proto.write(2220498092059L, id);
            }
            for (int id2 : this.mDeviceIdleTempWhitelist) {
                proto.write(2220498092060L, id2);
            }
            proto.write(1112396529693L, this.mLastWakeTime);
            proto.write(1112396529694L, this.mLastSleepTime);
            proto.write(1112396529695L, this.mLastUserActivityTime);
            proto.write(1112396529696L, this.mLastUserActivityTimeNoChangeLights);
            proto.write(1112396529697L, this.mLastInteractivePowerHintTime);
            proto.write(1112396529698L, this.mLastScreenBrightnessBoostTime);
            proto.write(1133871366179L, this.mScreenBrightnessBoostInProgress);
            proto.write(1133871366180L, this.mDisplayReady);
            proto.write(1133871366181L, this.mHoldingWakeLockSuspendBlocker);
            proto.write(1133871366182L, this.mHoldingDisplaySuspendBlocker);
            long settingsAndConfigurationToken = proto.start(1146756268071L);
            proto.write(1133871366145L, this.mDecoupleHalAutoSuspendModeFromDisplayConfig);
            proto.write(1133871366146L, this.mDecoupleHalInteractiveModeFromDisplayConfig);
            proto.write(1133871366147L, this.mWakeUpWhenPluggedOrUnpluggedConfig);
            proto.write(1133871366148L, this.mWakeUpWhenPluggedOrUnpluggedInTheaterModeConfig);
            proto.write(1133871366149L, this.mTheaterModeEnabled);
            proto.write(1133871366150L, this.mSuspendWhenScreenOffDueToProximityConfig);
            proto.write(1133871366151L, this.mDreamsSupportedConfig);
            proto.write(1133871366152L, this.mDreamsEnabledByDefaultConfig);
            proto.write(1133871366153L, this.mDreamsActivatedOnSleepByDefaultConfig);
            proto.write(1133871366154L, this.mDreamsActivatedOnDockByDefaultConfig);
            proto.write(1133871366155L, this.mDreamsEnabledOnBatteryConfig);
            proto.write(1172526071820L, this.mDreamsBatteryLevelMinimumWhenPoweredConfig);
            proto.write(1172526071821L, this.mDreamsBatteryLevelMinimumWhenNotPoweredConfig);
            proto.write(1172526071822L, this.mDreamsBatteryLevelDrainCutoffConfig);
            proto.write(1133871366159L, this.mDreamsEnabledSetting);
            proto.write(1133871366160L, this.mDreamsActivateOnSleepSetting);
            proto.write(1133871366161L, this.mDreamsActivateOnDockSetting);
            proto.write(1133871366162L, this.mDozeAfterScreenOff);
            proto.write(1120986464275L, this.mMinimumScreenOffTimeoutConfig);
            proto.write(1120986464276L, this.mMaximumScreenDimDurationConfig);
            proto.write(1108101562389L, this.mMaximumScreenDimRatioConfig);
            proto.write(1120986464278L, this.mScreenOffTimeoutSetting);
            proto.write(1172526071831L, this.mSleepTimeoutSetting);
            proto.write(1120986464280L, Math.min(this.mMaximumScreenOffTimeoutFromDeviceAdmin, 2147483647L));
            proto.write(1133871366169L, isMaximumScreenOffTimeoutFromDeviceAdminEnforcedLocked());
            long stayOnWhilePluggedInToken = proto.start(1146756268058L);
            proto.write(1133871366145L, (this.mStayOnWhilePluggedInSetting & 1) != 0);
            proto.write(1133871366146L, (this.mStayOnWhilePluggedInSetting & 2) != 0);
            proto.write(1133871366147L, (this.mStayOnWhilePluggedInSetting & 4) != 0);
            proto.end(stayOnWhilePluggedInToken);
            proto.write(1159641169947L, this.mScreenBrightnessModeSetting);
            proto.write(1172526071836L, this.mScreenBrightnessOverrideFromWindowManager);
            long j = activeWakeLocksToken;
            proto.write(1176821039133L, this.mUserActivityTimeoutOverrideFromWindowManager);
            proto.write(1133871366174L, this.mUserInactiveOverrideFromWindowManager);
            proto.write(1159641169951L, this.mDozeScreenStateOverrideFromDreamManager);
            proto.write(1133871366180L, this.mDrawWakeLockOverrideFromSidekick);
            proto.write(1108101562400L, this.mDozeScreenBrightnessOverrideFromDreamManager);
            long screenBrightnessSettingLimitsToken = proto.start(1146756268065L);
            proto.write(1120986464257L, this.mScreenBrightnessSettingMinimum);
            proto.write(1120986464258L, this.mScreenBrightnessSettingMaximum);
            proto.write(1120986464259L, this.mScreenBrightnessSettingDefault);
            proto.end(screenBrightnessSettingLimitsToken);
            proto.write(1133871366178L, this.mDoubleTapWakeEnabled);
            proto.write(1133871366179L, this.mIsVrModeEnabled);
            proto.end(settingsAndConfigurationToken);
            long sleepTimeout = getSleepTimeoutLocked();
            long j2 = screenBrightnessSettingLimitsToken;
            long screenBrightnessSettingLimitsToken2 = getScreenOffTimeoutLocked(sleepTimeout);
            long screenDimDuration = getScreenDimDurationLocked(screenBrightnessSettingLimitsToken2);
            long j3 = userActivityToken;
            proto.write(1172526071848L, sleepTimeout);
            proto.write(1120986464297L, screenBrightnessSettingLimitsToken2);
            long j4 = screenBrightnessSettingLimitsToken2;
            long screenDimDuration2 = screenDimDuration;
            proto.write(1120986464298L, screenDimDuration2);
            proto.write(1133871366187L, this.mUidsChanging);
            proto.write(1133871366188L, this.mUidsChanged);
            int i = 0;
            while (i < this.mUidState.size()) {
                UidState state = this.mUidState.valueAt(i);
                long screenDimDuration3 = screenDimDuration2;
                long uIDToken = proto.start(2246267895853L);
                int uid = this.mUidState.keyAt(i);
                proto.write(1120986464257L, uid);
                proto.write(1138166333442L, UserHandle.formatUid(uid));
                proto.write(1133871366147L, state.mActive);
                proto.write(1120986464260L, state.mNumWakeLocks);
                proto.write(1159641169925L, ActivityManager.processStateAmToProto(state.mProcState));
                proto.end(uIDToken);
                i++;
                screenDimDuration2 = screenDimDuration3;
                settingsAndConfigurationToken = settingsAndConfigurationToken;
                stayOnWhilePluggedInToken = stayOnWhilePluggedInToken;
            }
            long j5 = settingsAndConfigurationToken;
            long j6 = stayOnWhilePluggedInToken;
            this.mBatterySaverStateMachine.dumpProto(proto, 1146756268082L);
            this.mHandler.getLooper().writeToProto(proto, 1146756268078L);
            Iterator<WakeLock> it = this.mWakeLocks.iterator();
            while (it.hasNext()) {
                it.next().writeToProto(proto, 2246267895855L);
            }
            Iterator<SuspendBlocker> it2 = this.mSuspendBlockers.iterator();
            while (it2.hasNext()) {
                it2.next().writeToProto(proto, 2246267895856L);
            }
            wcd = this.mWirelessChargerDetector;
        }
        if (wcd != null) {
            wcd.writeToProto(proto, 1146756268081L);
        }
        proto.flush();
    }

    private void incrementBootCount() {
        int count;
        synchronized (this.mLock) {
            try {
                count = Settings.Global.getInt(getContext().getContentResolver(), "boot_count");
            } catch (Settings.SettingNotFoundException e) {
                count = 0;
            }
            Settings.Global.putInt(getContext().getContentResolver(), "boot_count", count + 1);
        }
    }

    /* access modifiers changed from: private */
    public static WorkSource copyWorkSource(WorkSource workSource) {
        if (workSource != null) {
            return new WorkSource(workSource);
        }
        return null;
    }

    @VisibleForTesting
    final class BatteryReceiver extends BroadcastReceiver {
        BatteryReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            synchronized (PowerManagerService.this.mLock) {
                PowerManagerService.this.handleBatteryStateChangedLocked();
            }
        }
    }

    private final class DreamReceiver extends BroadcastReceiver {
        private DreamReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            synchronized (PowerManagerService.this.mLock) {
                PowerManagerService.this.scheduleSandmanLocked();
            }
        }
    }

    public void onSwitchUser(int newUserId) {
        Slog.d(TAG, "onSwitchUser");
        synchronized (this.mLock) {
            this.mScreenBrightnessModeSetting = Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_brightness_mode", 0, -2);
        }
    }

    @VisibleForTesting
    final class UserSwitchedReceiver extends BroadcastReceiver {
        UserSwitchedReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            synchronized (PowerManagerService.this.mLock) {
                PowerManagerService.this.handleSettingsChangedLocked();
                PowerManagerService.this.mDisplayManagerInternal.switchUser(intent.getIntExtra("android.intent.extra.user_handle", 0));
            }
        }
    }

    private final class DockReceiver extends BroadcastReceiver {
        private DockReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            synchronized (PowerManagerService.this.mLock) {
                int dockState = intent.getIntExtra("android.intent.extra.DOCK_STATE", 0);
                if (PowerManagerService.this.mDockState != dockState) {
                    int unused = PowerManagerService.this.mDockState = dockState;
                    PowerManagerService.access$1676(PowerManagerService.this, 1024);
                    PowerManagerService.this.updatePowerStateLocked();
                }
            }
        }
    }

    private final class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:9:0x001d, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onChange(boolean r3, android.net.Uri r4) {
            /*
                r2 = this;
                com.android.server.power.PowerManagerService r0 = com.android.server.power.PowerManagerService.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.power.PowerManagerService r1 = com.android.server.power.PowerManagerService.this     // Catch:{ all -> 0x0025 }
                boolean r1 = r1.updateScreenProjectionLocked()     // Catch:{ all -> 0x0025 }
                if (r1 == 0) goto L_0x001e
                com.android.server.power.PowerManagerService r1 = com.android.server.power.PowerManagerService.this     // Catch:{ all -> 0x0025 }
                boolean r1 = r1.mHangUpEnabled     // Catch:{ all -> 0x0025 }
                if (r1 == 0) goto L_0x001c
                com.android.server.power.PowerManagerService r1 = com.android.server.power.PowerManagerService.this     // Catch:{ all -> 0x0025 }
                r1.updatePowerStateLocked()     // Catch:{ all -> 0x0025 }
            L_0x001c:
                monitor-exit(r0)     // Catch:{ all -> 0x0025 }
                return
            L_0x001e:
                com.android.server.power.PowerManagerService r1 = com.android.server.power.PowerManagerService.this     // Catch:{ all -> 0x0025 }
                r1.handleSettingsChangedLocked()     // Catch:{ all -> 0x0025 }
                monitor-exit(r0)     // Catch:{ all -> 0x0025 }
                return
            L_0x0025:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x0025 }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.SettingsObserver.onChange(boolean, android.net.Uri):void");
        }
    }

    private final class PowerManagerHandler extends Handler {
        public PowerManagerHandler(Looper looper) {
            super(looper, (Handler.Callback) null, true);
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 1) {
                PowerManagerService.this.handleUserActivityTimeout();
            } else if (i == 2) {
                PowerManagerService.this.handleSandman();
            } else if (i == 3) {
                PowerManagerService.this.handleScreenBrightnessBoostTimeout();
            } else if (i == 4) {
                PowerManagerService.this.checkForLongWakeLocks();
            }
        }
    }

    final class WakeLock implements IBinder.DeathRecipient {
        public long mAcquireTime;
        public boolean mDisabled;
        public int mFlags;
        public String mHistoryTag;
        public final IBinder mLock;
        public boolean mNotifiedAcquired;
        public boolean mNotifiedLong;
        public final int mOwnerPid;
        public final int mOwnerUid;
        public final String mPackageName;
        public String mTag;
        public final UidState mUidState;
        public WorkSource mWorkSource;

        public WakeLock(IBinder lock, int flags, String tag, String packageName, WorkSource workSource, String historyTag, int ownerUid, int ownerPid, UidState uidState) {
            this.mLock = lock;
            this.mFlags = flags;
            this.mTag = tag;
            this.mPackageName = packageName;
            this.mWorkSource = PowerManagerService.copyWorkSource(workSource);
            this.mHistoryTag = historyTag;
            this.mOwnerUid = ownerUid;
            this.mOwnerPid = ownerPid;
            this.mUidState = uidState;
        }

        public void binderDied() {
            PowerManagerService.this.handleWakeLockDeath(this);
        }

        public boolean hasSameProperties(int flags, String tag, WorkSource workSource, int ownerUid, int ownerPid) {
            return this.mFlags == flags && this.mTag.equals(tag) && hasSameWorkSource(workSource) && this.mOwnerUid == ownerUid && this.mOwnerPid == ownerPid;
        }

        public void updateProperties(int flags, String tag, String packageName, WorkSource workSource, String historyTag, int ownerUid, int ownerPid) {
            if (!this.mPackageName.equals(packageName)) {
                throw new IllegalStateException("Existing wake lock package name changed: " + this.mPackageName + " to " + packageName);
            } else if (this.mOwnerUid != ownerUid) {
                throw new IllegalStateException("Existing wake lock uid changed: " + this.mOwnerUid + " to " + ownerUid);
            } else if (this.mOwnerPid == ownerPid) {
                this.mFlags = flags;
                this.mTag = tag;
                updateWorkSource(workSource);
                this.mHistoryTag = historyTag;
            } else {
                throw new IllegalStateException("Existing wake lock pid changed: " + this.mOwnerPid + " to " + ownerPid);
            }
        }

        public boolean hasSameWorkSource(WorkSource workSource) {
            return Objects.equals(this.mWorkSource, workSource);
        }

        public void updateWorkSource(WorkSource workSource) {
            this.mWorkSource = PowerManagerService.copyWorkSource(workSource);
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(getLockLevelString());
            sb.append(" '");
            sb.append(this.mTag);
            sb.append("'");
            sb.append(getLockFlagsString());
            if (this.mDisabled) {
                sb.append(" DISABLED");
            }
            if (this.mNotifiedAcquired) {
                sb.append(" ACQ=");
                TimeUtils.formatDuration(this.mAcquireTime - SystemClock.uptimeMillis(), sb);
            }
            if (this.mNotifiedLong) {
                sb.append(" LONG");
            }
            sb.append(" (uid=");
            sb.append(this.mOwnerUid);
            if (this.mOwnerPid != 0) {
                sb.append(" pid=");
                sb.append(this.mOwnerPid);
            }
            if (this.mWorkSource != null) {
                sb.append(" ws=");
                sb.append(this.mWorkSource);
            }
            sb.append(")");
            return sb.toString();
        }

        public void writeToProto(ProtoOutputStream proto, long fieldId) {
            long wakeLockToken = proto.start(fieldId);
            proto.write(1159641169921L, this.mFlags & 65535);
            proto.write(1138166333442L, this.mTag);
            long wakeLockFlagsToken = proto.start(1146756268035L);
            boolean z = true;
            proto.write(1133871366145L, (this.mFlags & 268435456) != 0);
            if ((this.mFlags & 536870912) == 0) {
                z = false;
            }
            proto.write(1133871366146L, z);
            proto.end(wakeLockFlagsToken);
            proto.write(1133871366148L, this.mDisabled);
            if (this.mNotifiedAcquired) {
                proto.write(1112396529669L, this.mAcquireTime);
            }
            proto.write(1133871366150L, this.mNotifiedLong);
            proto.write(1120986464263L, this.mOwnerUid);
            proto.write(1120986464264L, this.mOwnerPid);
            WorkSource workSource = this.mWorkSource;
            if (workSource != null) {
                workSource.writeToProto(proto, 1146756268041L);
            }
            proto.end(wakeLockToken);
        }

        private String getLockLevelString() {
            int i = this.mFlags & 65535;
            if (i == 1) {
                return "PARTIAL_WAKE_LOCK             ";
            }
            if (i == 6) {
                return "SCREEN_DIM_WAKE_LOCK          ";
            }
            if (i == 10) {
                return "SCREEN_BRIGHT_WAKE_LOCK       ";
            }
            if (i == 26) {
                return "FULL_WAKE_LOCK                ";
            }
            if (i == 32) {
                return "PROXIMITY_SCREEN_OFF_WAKE_LOCK";
            }
            if (i == 64) {
                return "DOZE_WAKE_LOCK                ";
            }
            if (i != 128) {
                return "???                           ";
            }
            return "DRAW_WAKE_LOCK                ";
        }

        private String getLockFlagsString() {
            String result = "";
            if ((this.mFlags & 268435456) != 0) {
                result = result + " ACQUIRE_CAUSES_WAKEUP";
            }
            if ((this.mFlags & 536870912) == 0) {
                return result;
            }
            return result + " ON_AFTER_RELEASE";
        }
    }

    private final class SuspendBlockerImpl implements SuspendBlocker {
        private final String mName;
        private int mReferenceCount;
        private final String mTraceName;

        public SuspendBlockerImpl(String name) {
            this.mName = name;
            this.mTraceName = "SuspendBlocker (" + name + ")";
        }

        /* access modifiers changed from: protected */
        public void finalize() throws Throwable {
            try {
                if (this.mReferenceCount != 0) {
                    Slog.wtf(PowerManagerService.TAG, "Suspend blocker \"" + this.mName + "\" was finalized without being released!");
                    this.mReferenceCount = 0;
                    PowerManagerService.this.mNativeWrapper.nativeReleaseSuspendBlocker(this.mName);
                    Trace.asyncTraceEnd(131072, this.mTraceName, 0);
                }
            } finally {
                super.finalize();
            }
        }

        public void acquire() {
            synchronized (this) {
                this.mReferenceCount++;
                if (this.mReferenceCount == 1) {
                    Trace.asyncTraceBegin(131072, this.mTraceName, 0);
                    PowerManagerService.this.mNativeWrapper.nativeAcquireSuspendBlocker(this.mName);
                }
            }
        }

        public void release() {
            synchronized (this) {
                this.mReferenceCount--;
                if (this.mReferenceCount == 0) {
                    PowerManagerService.this.mNativeWrapper.nativeReleaseSuspendBlocker(this.mName);
                    Trace.asyncTraceEnd(131072, this.mTraceName, 0);
                } else if (this.mReferenceCount < 0) {
                    Slog.wtf(PowerManagerService.TAG, "Suspend blocker \"" + this.mName + "\" was released without being acquired!", new Throwable());
                    this.mReferenceCount = 0;
                }
            }
        }

        public String toString() {
            String str;
            synchronized (this) {
                str = this.mName + ": ref count=" + this.mReferenceCount;
            }
            return str;
        }

        public void writeToProto(ProtoOutputStream proto, long fieldId) {
            long sbToken = proto.start(fieldId);
            synchronized (this) {
                proto.write(1138166333441L, this.mName);
                proto.write(1120986464258L, this.mReferenceCount);
            }
            proto.end(sbToken);
        }
    }

    static final class UidState {
        boolean mActive;
        int mNumWakeLocks;
        int mProcState;
        final int mUid;

        UidState(int uid) {
            this.mUid = uid;
        }
    }

    @VisibleForTesting
    final class BinderService extends IPowerManager.Stub {
        BinderService() {
        }

        /* JADX WARNING: type inference failed for: r1v0, types: [android.os.Binder] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onShellCommand(java.io.FileDescriptor r9, java.io.FileDescriptor r10, java.io.FileDescriptor r11, java.lang.String[] r12, android.os.ShellCallback r13, android.os.ResultReceiver r14) {
            /*
                r8 = this;
                com.android.server.power.PowerManagerShellCommand r0 = new com.android.server.power.PowerManagerShellCommand
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.PowerManagerService.BinderService.onShellCommand(java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):void");
        }

        public void acquireWakeLockWithUid(IBinder lock, int flags, String tag, String packageName, int uid) {
            if (uid < 0) {
                uid = Binder.getCallingUid();
            }
            acquireWakeLock(lock, flags, tag, packageName, new WorkSource(uid), (String) null);
        }

        public void powerHint(int hintId, int data) {
            if (PowerManagerService.this.mSystemReady) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", (String) null);
                PowerManagerService.this.powerHintInternal(hintId, data);
            }
        }

        public void acquireWakeLock(IBinder lock, int flags, String tag, String packageName, WorkSource ws, String historyTag) {
            WorkSource ws2;
            if (lock == null) {
                throw new IllegalArgumentException("lock must not be null");
            } else if (packageName != null) {
                PowerManager.validateWakeLockParameters(flags, tag);
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.WAKE_LOCK", (String) null);
                if ((flags & 64) != 0) {
                    PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", (String) null);
                }
                if (ws == null || ws.isEmpty()) {
                    ws2 = null;
                } else {
                    PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.UPDATE_DEVICE_STATS", (String) null);
                    ws2 = ws;
                }
                int uid = Binder.getCallingUid();
                int pid = Binder.getCallingPid();
                long ident = Binder.clearCallingIdentity();
                try {
                    PowerManagerService.this.acquireWakeLockInternal(lock, flags, tag, packageName, ws2, historyTag, uid, pid);
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            } else {
                throw new IllegalArgumentException("packageName must not be null");
            }
        }

        public void releaseWakeLock(IBinder lock, int flags) {
            if (lock != null) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.WAKE_LOCK", (String) null);
                long ident = Binder.clearCallingIdentity();
                try {
                    PowerManagerService.this.releaseWakeLockInternal(lock, flags);
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            } else {
                throw new IllegalArgumentException("lock must not be null");
            }
        }

        public void updateWakeLockUids(IBinder lock, int[] uids) {
            WorkSource ws = null;
            if (uids != null) {
                ws = new WorkSource();
                for (int add : uids) {
                    ws.add(add);
                }
            }
            updateWakeLockWorkSource(lock, ws, (String) null);
        }

        public void updateWakeLockWorkSource(IBinder lock, WorkSource ws, String historyTag) {
            if (lock != null) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.WAKE_LOCK", (String) null);
                if (ws == null || ws.isEmpty()) {
                    ws = null;
                } else {
                    PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.UPDATE_DEVICE_STATS", (String) null);
                }
                int callingUid = Binder.getCallingUid();
                long ident = Binder.clearCallingIdentity();
                try {
                    PowerManagerService.this.updateWakeLockWorkSourceInternal(lock, ws, historyTag, callingUid);
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            } else {
                throw new IllegalArgumentException("lock must not be null");
            }
        }

        public boolean isWakeLockLevelSupported(int level) {
            long ident = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.isWakeLockLevelSupportedInternal(level);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public void userActivity(long eventTime, int event, int flags) {
            long now = SystemClock.uptimeMillis();
            if (PowerManagerService.this.mContext.checkCallingOrSelfPermission("android.permission.DEVICE_POWER") != 0 && PowerManagerService.this.mContext.checkCallingOrSelfPermission("android.permission.USER_ACTIVITY") != 0) {
                synchronized (PowerManagerService.this.mLock) {
                    if (now >= PowerManagerService.this.mLastWarningAboutUserActivityPermission + BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS) {
                        long unused = PowerManagerService.this.mLastWarningAboutUserActivityPermission = now;
                        Slog.w(PowerManagerService.TAG, "Ignoring call to PowerManager.userActivity() because the caller does not have DEVICE_POWER or USER_ACTIVITY permission.  Please fix your app!   pid=" + Binder.getCallingPid() + " uid=" + Binder.getCallingUid());
                    }
                }
            } else if (eventTime <= now) {
                int uid = Binder.getCallingUid();
                long ident = Binder.clearCallingIdentity();
                try {
                    PowerManagerService.this.userActivityInternal(eventTime, event, flags, uid);
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            } else {
                throw new IllegalArgumentException("event time must not be in the future");
            }
        }

        public void wakeUp(long eventTime, int reason, String details, String opPackageName) {
            if (eventTime <= SystemClock.uptimeMillis()) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", (String) null);
                int uid = Binder.getCallingUid();
                long ident = Binder.clearCallingIdentity();
                try {
                    PowerManagerService.this.wakeUpInternal(eventTime, reason, details, uid, opPackageName, uid);
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            } else {
                throw new IllegalArgumentException("event time must not be in the future");
            }
        }

        public void goToSleep(long eventTime, int reason, int flags) {
            if (eventTime <= SystemClock.uptimeMillis()) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", (String) null);
                int uid = Binder.getCallingUid();
                long ident = Binder.clearCallingIdentity();
                try {
                    PowerManagerService.this.goToSleepInternal(eventTime, reason, flags, uid);
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            } else {
                throw new IllegalArgumentException("event time must not be in the future");
            }
        }

        public void nap(long eventTime) {
            if (eventTime <= SystemClock.uptimeMillis()) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", (String) null);
                int uid = Binder.getCallingUid();
                long ident = Binder.clearCallingIdentity();
                try {
                    PowerManagerService.this.napInternal(eventTime, uid);
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            } else {
                throw new IllegalArgumentException("event time must not be in the future");
            }
        }

        public boolean isInteractive() {
            long ident = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.isInteractiveInternal();
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public boolean isPowerSaveMode() {
            long ident = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.mBatterySaverController.isEnabled();
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public PowerSaveState getPowerSaveState(int serviceType) {
            long ident = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.mBatterySaverPolicy.getBatterySaverPolicy(serviceType);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public boolean setPowerSaveModeEnabled(boolean enabled) {
            if (PowerManagerService.this.mContext.checkCallingOrSelfPermission("android.permission.POWER_SAVER") != 0) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", (String) null);
            }
            long ident = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.setLowPowerModeInternal(enabled);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public boolean setDynamicPowerSaveHint(boolean powerSaveHint, int disableThreshold) {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.POWER_SAVER", "updateDynamicPowerSavings");
            long ident = Binder.clearCallingIdentity();
            try {
                ContentResolver resolver = PowerManagerService.this.mContext.getContentResolver();
                boolean success = Settings.Global.putInt(resolver, "dynamic_power_savings_disable_threshold", disableThreshold);
                if (success) {
                    success &= Settings.Global.putInt(resolver, "dynamic_power_savings_enabled", powerSaveHint ? 1 : 0);
                }
                return success;
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public boolean setAdaptivePowerSavePolicy(BatterySaverPolicyConfig config) {
            if (PowerManagerService.this.mContext.checkCallingOrSelfPermission("android.permission.POWER_SAVER") != 0) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", "setAdaptivePowerSavePolicy");
            }
            long ident = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.mBatterySaverStateMachine.setAdaptiveBatterySaverPolicy(config);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public boolean setAdaptivePowerSaveEnabled(boolean enabled) {
            if (PowerManagerService.this.mContext.checkCallingOrSelfPermission("android.permission.POWER_SAVER") != 0) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", "setAdaptivePowerSaveEnabled");
            }
            long ident = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.mBatterySaverStateMachine.setAdaptiveBatterySaverEnabled(enabled);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public int getPowerSaveModeTrigger() {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.POWER_SAVER", (String) null);
            long ident = Binder.clearCallingIdentity();
            try {
                return Settings.Global.getInt(PowerManagerService.this.mContext.getContentResolver(), "automatic_power_save_mode", 0);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public boolean isDeviceIdleMode() {
            long ident = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.isDeviceIdleModeInternal();
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public boolean isLightDeviceIdleMode() {
            long ident = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.isLightDeviceIdleModeInternal();
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public int getLastShutdownReason() {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", (String) null);
            long ident = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.getLastShutdownReasonInternal(PowerManagerService.REBOOT_PROPERTY);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public int getLastSleepReason() {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", (String) null);
            long ident = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.getLastSleepReasonInternal();
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public void reboot(boolean confirm, String reason, boolean wait) {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.REBOOT", (String) null);
            if ("recovery".equals(reason) || "recovery-update".equals(reason)) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.RECOVERY", (String) null);
            }
            long ident = Binder.clearCallingIdentity();
            try {
                PowerManagerService.this.shutdownOrRebootInternal(1, confirm, reason, wait);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public void rebootSafeMode(boolean confirm, boolean wait) {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.REBOOT", (String) null);
            long ident = Binder.clearCallingIdentity();
            try {
                PowerManagerService.this.shutdownOrRebootInternal(2, confirm, "safemode", wait);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public void shutdown(boolean confirm, String reason, boolean wait) {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.REBOOT", (String) null);
            long ident = Binder.clearCallingIdentity();
            try {
                PowerManagerService.this.shutdownOrRebootInternal(0, confirm, reason, wait);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public void crash(String message) {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.REBOOT", (String) null);
            long ident = Binder.clearCallingIdentity();
            try {
                PowerManagerService.this.crashInternal(message);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public void updateWakeLockSources(IBinder lock, int[] uids, String[] pkgNames) {
            WorkSource ws = null;
            Slog.d(PowerManagerService.TAG, "updateWakeLockSources");
            if (uids != null) {
                ws = new WorkSource();
                for (int i = 0; i < uids.length; i++) {
                    ws.add(uids[i], pkgNames[i]);
                    Slog.d(PowerManagerService.TAG, "updateWakeLockSources uid=" + uids[i] + " name=" + pkgNames[i]);
                }
            }
            updateWakeLockWorkSource(lock, ws, (String) null);
        }

        public void setStayOnSetting(int val) {
            int uid = Binder.getCallingUid();
            if (uid == 0 || Settings.checkAndNoteWriteSettingsOperation(PowerManagerService.this.mContext, uid, Settings.getPackageNameForUid(PowerManagerService.this.mContext, uid), true)) {
                long ident = Binder.clearCallingIdentity();
                try {
                    PowerManagerService.this.setStayOnSettingInternal(val);
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            }
        }

        public void setAttentionLight(boolean on, int color) {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", (String) null);
            long ident = Binder.clearCallingIdentity();
            try {
                PowerManagerService.this.setAttentionLightInternal(on, color);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public void setDozeAfterScreenOff(boolean on) {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", (String) null);
            long ident = Binder.clearCallingIdentity();
            try {
                PowerManagerService.this.setDozeAfterScreenOffInternal(on);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public void boostScreenBrightness(long eventTime) {
            if (eventTime <= SystemClock.uptimeMillis()) {
                PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", (String) null);
                int uid = Binder.getCallingUid();
                long ident = Binder.clearCallingIdentity();
                try {
                    PowerManagerService.this.boostScreenBrightnessInternal(eventTime, uid);
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            } else {
                throw new IllegalArgumentException("event time must not be in the future");
            }
        }

        public boolean isScreenBrightnessBoosted() {
            long ident = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.isScreenBrightnessBoostedInternal();
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public boolean forceSuspend() {
            PowerManagerService.this.mContext.enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", (String) null);
            int uid = Binder.getCallingUid();
            long ident = Binder.clearCallingIdentity();
            try {
                return PowerManagerService.this.forceSuspendInternal(uid);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        /* access modifiers changed from: protected */
        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            if (DumpUtils.checkDumpPermission(PowerManagerService.this.mContext, PowerManagerService.TAG, pw)) {
                long ident = Binder.clearCallingIdentity();
                boolean isDumpProto = false;
                for (String arg : args) {
                    if (arg.equals(PriorityDump.PROTO_ARG)) {
                        isDumpProto = true;
                    }
                }
                if (isDumpProto) {
                    try {
                        PowerManagerService.this.dumpProto(fd);
                    } catch (Throwable th) {
                        Binder.restoreCallingIdentity(ident);
                        throw th;
                    }
                } else {
                    PowerManagerService.this.dumpInternal(pw);
                }
                Binder.restoreCallingIdentity(ident);
            }
        }
    }

    private boolean hangUpNoUpdateLocked(boolean hangUp) {
        if (this.mBootCompleted && this.mSystemReady) {
            int i = 4;
            if ((!hangUp || this.mWakefulness != 4) && (hangUp || this.mWakefulness == 4)) {
                StringBuilder sb = new StringBuilder();
                sb.append("hangUpNoUpdateLocked: ");
                sb.append(hangUp ? "enter" : "disable");
                sb.append(" hang up mode...");
                Slog.d(TAG, sb.toString());
                long time = SystemClock.uptimeMillis();
                if (!hangUp) {
                    userActivityNoUpdateLocked(time, 0, 0, 1000);
                }
                if (!hangUp) {
                    i = 1;
                }
                setWakefulnessLocked(i, 0, time);
                this.mNotifier.onWakefulnessInHangUp(hangUp);
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public boolean updateScreenProjectionLocked() {
        ContentResolver resolver = this.mContext.getContentResolver();
        boolean screenProjectingEnabled = Settings.Secure.getInt(resolver, "screen_project_in_screening", 0) == 1;
        boolean hangUpEnabled = Settings.Secure.getInt(resolver, "screen_project_hang_up_on", 0) == 1;
        if (screenProjectingEnabled == this.mScreenProjectionEnabled && hangUpEnabled == this.mHangUpEnabled) {
            return false;
        }
        if (hangUpEnabled || !this.mHangUpEnabled) {
            this.mScreenProjectionEnabled = screenProjectingEnabled;
            this.mHangUpEnabled = hangUpEnabled;
            if (screenProjectingEnabled && hangUpEnabled) {
                hangUpNoUpdateLocked(true);
            } else if (!screenProjectingEnabled) {
                hangUpNoUpdateLocked(false);
            }
            if (this.mHangUpEnabled) {
                Settings.Secure.putInt(this.mContext.getContentResolver(), "screen_project_hang_up_on", 0);
            }
            this.mDirty |= 32;
            return true;
        }
        this.mHangUpEnabled = false;
        return true;
    }

    private void resetScreenProjectionSettings() {
        Settings.Secure.putInt(this.mContext.getContentResolver(), "screen_project_hang_up_on", 0);
        Settings.Secure.putInt(this.mContext.getContentResolver(), "screen_project_in_screening", 0);
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public BinderService getBinderServiceInstance() {
        return this.mBinderService;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public LocalService getLocalServiceInstance() {
        return this.mLocalService;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public int getLastShutdownReasonInternal(String lastRebootReasonProperty) {
        String line = SystemProperties.get(lastRebootReasonProperty);
        if (line == null) {
            return 0;
        }
        char c = 65535;
        switch (line.hashCode()) {
            case -2117951935:
                if (line.equals(REASON_THERMAL_SHUTDOWN)) {
                    c = 3;
                    break;
                }
                break;
            case -1099647817:
                if (line.equals(REASON_LOW_BATTERY)) {
                    c = 4;
                    break;
                }
                break;
            case -934938715:
                if (line.equals(REASON_REBOOT)) {
                    c = 1;
                    break;
                }
                break;
            case -852189395:
                if (line.equals(REASON_USERREQUESTED)) {
                    c = 2;
                    break;
                }
                break;
            case -169343402:
                if (line.equals(REASON_SHUTDOWN)) {
                    c = 0;
                    break;
                }
                break;
            case 1218064802:
                if (line.equals(REASON_BATTERY_THERMAL_STATE)) {
                    c = 5;
                    break;
                }
                break;
        }
        if (c == 0) {
            return 1;
        }
        if (c == 1) {
            return 2;
        }
        if (c == 2) {
            return 3;
        }
        if (c == 3) {
            return 4;
        }
        if (c == 4) {
            return 5;
        }
        if (c != 5) {
            return 0;
        }
        return 6;
    }

    /* access modifiers changed from: private */
    public int getLastSleepReasonInternal() {
        int i;
        synchronized (this.mLock) {
            i = this.mLastSleepReason;
        }
        return i;
    }

    /* access modifiers changed from: private */
    public PowerManager.WakeData getLastWakeupInternal() {
        PowerManager.WakeData wakeData;
        synchronized (this.mLock) {
            wakeData = new PowerManager.WakeData(this.mLastWakeTime, this.mLastWakeReason);
        }
        return wakeData;
    }

    private final class LocalService extends PowerManagerInternal {
        private LocalService() {
        }

        public void setScreenBrightnessOverrideFromWindowManager(int screenBrightness) {
            if (screenBrightness < -1 || screenBrightness > PowerManager.BRIGHTNESS_ON) {
                screenBrightness = -1;
            }
            PowerManagerService.this.setScreenBrightnessOverrideFromWindowManagerInternal(screenBrightness);
        }

        public void setDozeOverrideFromDreamManager(int screenState, int screenBrightness) {
            switch (screenState) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    break;
                default:
                    screenState = 0;
                    break;
            }
            if (screenBrightness < -1 || screenBrightness > PowerManager.BRIGHTNESS_ON) {
                screenBrightness = -1;
            }
            PowerManagerService.this.setDozeOverrideFromDreamManagerInternal(screenState, screenBrightness);
        }

        public void setUserInactiveOverrideFromWindowManager() {
            PowerManagerService.this.setUserInactiveOverrideFromWindowManagerInternal();
        }

        public void setUserActivityTimeoutOverrideFromWindowManager(long timeoutMillis) {
            PowerManagerService.this.setUserActivityTimeoutOverrideFromWindowManagerInternal(timeoutMillis);
        }

        public void setDrawWakeLockOverrideFromSidekick(boolean keepState) {
            PowerManagerService.this.setDrawWakeLockOverrideFromSidekickInternal(keepState);
        }

        public void setMaximumScreenOffTimeoutFromDeviceAdmin(int userId, long timeMs) {
            PowerManagerService.this.setMaximumScreenOffTimeoutFromDeviceAdminInternal(userId, timeMs);
        }

        public PowerSaveState getLowPowerState(int serviceType) {
            return PowerManagerService.this.mBatterySaverPolicy.getBatterySaverPolicy(serviceType);
        }

        public void registerLowPowerModeObserver(PowerManagerInternal.LowPowerModeListener listener) {
            PowerManagerService.this.mBatterySaverController.addListener(listener);
        }

        public boolean setDeviceIdleMode(boolean enabled) {
            return PowerManagerService.this.setDeviceIdleModeInternal(enabled);
        }

        public boolean setLightDeviceIdleMode(boolean enabled) {
            return PowerManagerService.this.setLightDeviceIdleModeInternal(enabled);
        }

        public void setDeviceIdleWhitelist(int[] appids) {
            PowerManagerService.this.setDeviceIdleWhitelistInternal(appids);
        }

        public void setDeviceIdleTempWhitelist(int[] appids) {
            PowerManagerService.this.setDeviceIdleTempWhitelistInternal(appids);
        }

        public void startUidChanges() {
            PowerManagerService.this.startUidChangesInternal();
        }

        public void finishUidChanges() {
            PowerManagerService.this.finishUidChangesInternal();
        }

        public void updateUidProcState(int uid, int procState) {
            PowerManagerService.this.updateUidProcStateInternal(uid, procState);
        }

        public void uidGone(int uid) {
            PowerManagerService.this.uidGoneInternal(uid);
        }

        public void uidActive(int uid) {
            PowerManagerService.this.uidActiveInternal(uid);
        }

        public void uidIdle(int uid) {
            PowerManagerService.this.uidIdleInternal(uid);
        }

        public void powerHint(int hintId, int data) {
            PowerManagerService.this.powerHintInternal(hintId, data);
        }

        public boolean wasDeviceIdleFor(long ms) {
            return PowerManagerService.this.wasDeviceIdleForInternal(ms);
        }

        public PowerManager.WakeData getLastWakeup() {
            return PowerManagerService.this.getLastWakeupInternal();
        }

        public void addVisibleWindowUids(int uid) {
            if (!PowerManagerService.this.mVisibleWindowUids.contains(Integer.valueOf(uid))) {
                PowerManagerService.this.mVisibleWindowUids.add(Integer.valueOf(uid));
            }
        }

        public void clearVisibleWindowUids() {
            if (!PowerManagerService.this.mVisibleWindowUids.isEmpty()) {
                PowerManagerService.this.mVisibleWindowUids.clear();
            }
        }

        public void summonSandman() {
            synchronized (PowerManagerService.this.mLock) {
                boolean unused = PowerManagerService.this.mSandmanSummoned = true;
            }
        }
    }

    private void checkScreenWakeLockDisabledStateLocked() {
        boolean state;
        boolean changed = false;
        Iterator<WakeLock> it = this.mWakeLocks.iterator();
        while (it.hasNext()) {
            WakeLock wakeLock = it.next();
            int flag = wakeLock.mFlags & 65535;
            if ((flag == 26 || flag == 10 || flag == 6) && UserHandle.isApp(wakeLock.mOwnerUid) && (state = this.mVisibleWindowUids.contains(Integer.valueOf(wakeLock.mOwnerUid))) == wakeLock.mDisabled) {
                changed = true;
                wakeLock.mDisabled = !state;
                if (wakeLock.mDisabled) {
                    notifyWakeLockReleasedLocked(wakeLock);
                    Slog.d(TAG, "screen wakeLock:[" + wakeLock.toString() + "] disabled");
                } else {
                    notifyWakeLockAcquiredLocked(wakeLock);
                    Slog.d(TAG, "screen wakeLock:[" + wakeLock.toString() + "] enabled");
                }
            }
        }
        if (changed) {
            this.mDirty |= 1;
        }
    }
}
