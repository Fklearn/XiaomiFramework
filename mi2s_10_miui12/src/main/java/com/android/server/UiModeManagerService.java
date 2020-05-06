package com.android.server;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.IMiuiActivityObserver;
import android.app.IUiModeManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.StatusBarManager;
import android.app.UiModeManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManagerInternal;
import android.os.PowerSaveState;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ShellCallback;
import android.os.ShellCommand;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.Settings;
import android.service.vr.IVrManager;
import android.service.vr.IVrStateCallbacks;
import android.util.FloatProperty;
import android.util.Slog;
import android.view.IWindowAnimationFinishedCallback;
import android.view.IWindowManager;
import com.android.internal.app.DisableCarModeActivity;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.util.DumpUtils;
import com.android.server.pm.DumpState;
import com.android.server.twilight.TwilightListener;
import com.android.server.twilight.TwilightManager;
import com.android.server.twilight.TwilightState;
import com.android.server.wm.MiuiColorFade;
import com.android.server.wm.WindowManagerService;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.function.Consumer;

final class UiModeManagerService extends SystemService {
    private static final int ANIMATION_DURATION_MILLIS = 800;
    private static final FloatProperty<MiuiColorFade> COLOR_FADE_LEVEL = new FloatProperty<MiuiColorFade>("alphaLevel") {
        public void setValue(MiuiColorFade object, float value) {
            object.setColorFadeAlphaLevel(value);
        }

        public Float get(MiuiColorFade object) {
            return Float.valueOf(object.getColorFadeAlphaLevel());
        }
    };
    private static final boolean ENABLE_LAUNCH_DESK_DOCK_APP = true;
    private static final boolean LOG = false;
    private static final int NIGHT_CHANGE_ANIM_START = 1;
    private static final String SYSTEM_PROPERTY_DEVICE_THEME = "persist.sys.theme";
    /* access modifiers changed from: private */
    public static final String TAG = UiModeManager.class.getSimpleName();
    private IWindowAnimationFinishedCallback mAnimFinishedCallback = new IWindowAnimationFinishedCallback.Stub() {
        public void onWindowAnimFinished() {
            if (!UiModeManagerService.this.mShouldPendingSwitch) {
                return;
            }
            if (UiModeManagerService.this.mIsActivityResumed || UiModeManagerService.this.mIsActivityPaused) {
                UiModeManagerService uiModeManagerService = UiModeManagerService.this;
                uiModeManagerService.setNightModeLocked(uiModeManagerService.mDelayedNightMode, UiModeManagerService.this.mDelayedSetNightModeUser);
                boolean unused = UiModeManagerService.this.mIsActivityResumed = false;
                boolean unused2 = UiModeManagerService.this.mIsActivityPaused = false;
                boolean unused3 = UiModeManagerService.this.mShouldPendingSwitch = false;
            }
        }
    };
    private final Animator.AnimatorListener mAnimatorListener = new Animator.AnimatorListener() {
        public void onAnimationStart(Animator animation) {
        }

        public void onAnimationEnd(Animator animation) {
            UiModeManagerService.this.mHandler.removeMessages(1);
            UiModeManagerService.this.mMiuiColorFade.dismiss();
        }

        public void onAnimationRepeat(Animator animation) {
        }

        public void onAnimationCancel(Animator animation) {
            UiModeManagerService.this.mHandler.removeMessages(1);
            UiModeManagerService.this.mMiuiColorFade.dismiss();
        }
    };
    private final BroadcastReceiver mBatteryReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (((action.hashCode() == -1538406691 && action.equals("android.intent.action.BATTERY_CHANGED")) ? (char) 0 : 65535) == 0) {
                boolean unused = UiModeManagerService.this.mCharging = intent.getIntExtra("plugged", 0) != 0;
            }
            synchronized (UiModeManagerService.this.mLock) {
                if (UiModeManagerService.this.mSystemReady) {
                    UiModeManagerService.this.updateLocked(0, 0);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public int mBrightness;
    private int mCarModeEnableFlags;
    private boolean mCarModeEnabled = false;
    private boolean mCarModeKeepsScreenOn;
    /* access modifiers changed from: private */
    public boolean mCharging = false;
    private boolean mComputedNightMode;
    /* access modifiers changed from: private */
    public Configuration mConfiguration = new Configuration();
    int mCurUiMode = 0;
    /* access modifiers changed from: private */
    public int mDefaultUiModeType;
    /* access modifiers changed from: private */
    public volatile int mDelayedNightMode = -1;
    /* access modifiers changed from: private */
    public int mDelayedSetNightModeUser;
    private boolean mDeskModeKeepsScreenOn;
    private final BroadcastReceiver mDockModeReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            UiModeManagerService.this.updateDockState(intent.getIntExtra("android.intent.extra.DOCK_STATE", 0));
        }
    };
    private int mDockState = 0;
    private boolean mEnableCarDockLaunch = true;
    /* access modifiers changed from: private */
    public final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                UiModeManagerService.this.doNightChangeAnimation();
            }
        }
    };
    private boolean mHoldingConfiguration = false;
    private IWindowManager mIWindowManager;
    /* access modifiers changed from: private */
    public boolean mIsActivityPaused = false;
    /* access modifiers changed from: private */
    public boolean mIsActivityResumed = false;
    private int mLastBroadcastState = 0;
    /* access modifiers changed from: private */
    public ComponentName mLastResumedActivity;
    private final LocalService mLocalService = new LocalService();
    final Object mLock = new Object();
    private final IMiuiActivityObserver mMiuiActivityObserver = new IMiuiActivityObserver.Stub() {
        public void activityIdle(Intent intent) throws RemoteException {
        }

        public void activityResumed(Intent intent) throws RemoteException {
            ComponentName cn = intent.getComponent();
            if (cn != null && !cn.equals(UiModeManagerService.this.mLastResumedActivity)) {
                boolean unused = UiModeManagerService.this.mIsActivityResumed = true;
            }
        }

        public void activityPaused(Intent intent) throws RemoteException {
            ComponentName cn = intent.getComponent();
            if (cn != null && cn.equals(UiModeManagerService.this.mLastResumedActivity)) {
                boolean unused = UiModeManagerService.this.mIsActivityPaused = true;
            }
        }

        public void activityStopped(Intent intent) throws RemoteException {
        }

        public void activityDestroyed(Intent intent) throws RemoteException {
        }
    };
    /* access modifiers changed from: private */
    public MiuiColorFade mMiuiColorFade;
    private ObjectAnimator mNightColorFadeAnimator;
    /* access modifiers changed from: private */
    public int mNightMode = 1;
    /* access modifiers changed from: private */
    public boolean mNightModeLocked = false;
    private NotificationManager mNotificationManager;
    private boolean mPowerSave = false;
    private final BroadcastReceiver mResultReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (getResultCode() == -1) {
                int enableFlags = intent.getIntExtra("enableFlags", 0);
                int disableFlags = intent.getIntExtra("disableFlags", 0);
                synchronized (UiModeManagerService.this.mLock) {
                    UiModeManagerService.this.updateAfterBroadcastLocked(intent.getAction(), enableFlags, disableFlags);
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public final IUiModeManager.Stub mService = new MiuiUiModeManagerStub(this) {
        /* Debug info: failed to restart local var, previous not found, register: 5 */
        public void enableCarMode(int flags) {
            if (isUiModeLocked()) {
                Slog.e(UiModeManagerService.TAG, "enableCarMode while UI mode is locked");
                return;
            }
            long ident = Binder.clearCallingIdentity();
            try {
                synchronized (UiModeManagerService.this.mLock) {
                    UiModeManagerService.this.setCarModeLocked(true, flags);
                    if (UiModeManagerService.this.mSystemReady) {
                        UiModeManagerService.this.updateLocked(flags, 0);
                    }
                }
                Binder.restoreCallingIdentity(ident);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(ident);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 5 */
        public void disableCarMode(int flags) {
            if (isUiModeLocked()) {
                Slog.e(UiModeManagerService.TAG, "disableCarMode while UI mode is locked");
                return;
            }
            long ident = Binder.clearCallingIdentity();
            try {
                synchronized (UiModeManagerService.this.mLock) {
                    UiModeManagerService.this.setCarModeLocked(false, 0);
                    if (UiModeManagerService.this.mSystemReady) {
                        UiModeManagerService.this.updateLocked(0, flags);
                    }
                }
                Binder.restoreCallingIdentity(ident);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(ident);
                throw th;
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 4 */
        public int getCurrentModeType() {
            int i;
            long ident = Binder.clearCallingIdentity();
            try {
                synchronized (UiModeManagerService.this.mLock) {
                    i = UiModeManagerService.this.mCurUiMode & 15;
                }
                Binder.restoreCallingIdentity(ident);
                return i;
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(ident);
                throw th;
            }
        }

        public void setNightMode(int mode) {
            if (isNightModeLocked() && UiModeManagerService.this.getContext().checkCallingOrSelfPermission("android.permission.MODIFY_DAY_NIGHT_MODE") != 0) {
                Slog.e(UiModeManagerService.TAG, "Night mode locked, requires MODIFY_DAY_NIGHT_MODE permission");
            } else if (!UiModeManagerService.this.mSetupWizardComplete) {
                Slog.d(UiModeManagerService.TAG, "Night mode cannot be changed before setup wizard completes.");
            } else if (mode == 0 || mode == 1 || mode == 2) {
                int user = UserHandle.getCallingUserId();
                long ident = Binder.clearCallingIdentity();
                try {
                    if (!UiModeManagerService.this.shouldDelayNightModeChange(mode, user)) {
                        UiModeManagerService.this.setNightModeLocked(mode, user);
                    }
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            } else {
                throw new IllegalArgumentException("Unknown mode: " + mode);
            }
        }

        public int getNightMode() {
            int access$1000;
            synchronized (UiModeManagerService.this.mLock) {
                access$1000 = UiModeManagerService.this.mNightMode;
            }
            return access$1000;
        }

        public boolean isUiModeLocked() {
            boolean access$2300;
            synchronized (UiModeManagerService.this.mLock) {
                access$2300 = UiModeManagerService.this.mUiModeLocked;
            }
            return access$2300;
        }

        public boolean isNightModeLocked() {
            boolean access$2400;
            synchronized (UiModeManagerService.this.mLock) {
                access$2400 = UiModeManagerService.this.mNightModeLocked;
            }
            return access$2400;
        }

        public void onShellCommand(FileDescriptor in, FileDescriptor out, FileDescriptor err, String[] args, ShellCallback callback, ResultReceiver resultReceiver) {
            new Shell(UiModeManagerService.this.mService).exec(UiModeManagerService.this.mService, in, out, err, args, callback, resultReceiver);
        }

        /* access modifiers changed from: protected */
        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            if (DumpUtils.checkDumpPermission(UiModeManagerService.this.getContext(), UiModeManagerService.TAG, pw)) {
                UiModeManagerService.this.dumpImpl(pw);
            }
        }
    };
    private int mSetUiMode = 0;
    /* access modifiers changed from: private */
    public boolean mSetupWizardComplete;
    /* access modifiers changed from: private */
    public final ContentObserver mSetupWizardObserver = new ContentObserver(this.mHandler) {
        public void onChange(boolean selfChange, Uri uri) {
            if (UiModeManagerService.this.setupWizardCompleteForCurrentUser()) {
                boolean unused = UiModeManagerService.this.mSetupWizardComplete = true;
                UiModeManagerService.this.getContext().getContentResolver().unregisterContentObserver(UiModeManagerService.this.mSetupWizardObserver);
                Context context = UiModeManagerService.this.getContext();
                boolean unused2 = UiModeManagerService.this.updateNightModeFromSettings(context, context.getResources(), UserHandle.getCallingUserId());
                UiModeManagerService.this.updateLocked(0, 0);
            }
        }
    };
    /* access modifiers changed from: private */
    public boolean mShouldPendingSwitch = false;
    private StatusBarManager mStatusBarManager;
    boolean mSystemReady;
    private boolean mTelevision;
    private final TwilightListener mTwilightListener = new TwilightListener() {
        public void onTwilightStateChanged(TwilightState state) {
            synchronized (UiModeManagerService.this.mLock) {
                if (UiModeManagerService.this.mNightMode == 0) {
                    UiModeManagerService.this.updateComputedNightModeLocked();
                    UiModeManagerService.this.updateLocked(0, 0);
                }
            }
        }
    };
    private TwilightManager mTwilightManager;
    /* access modifiers changed from: private */
    public boolean mUiModeLocked = false;
    /* access modifiers changed from: private */
    public boolean mVrHeadset;
    private final IVrStateCallbacks mVrStateCallbacks = new IVrStateCallbacks.Stub() {
        public void onVrStateChanged(boolean enabled) {
            synchronized (UiModeManagerService.this.mLock) {
                boolean unused = UiModeManagerService.this.mVrHeadset = enabled;
                if (UiModeManagerService.this.mSystemReady) {
                    UiModeManagerService.this.updateLocked(0, 0);
                }
            }
        }
    };
    private PowerManager.WakeLock mWakeLock;
    private boolean mWatch;
    WindowManagerService mWmService;

    public UiModeManagerService(Context context) {
        super(context);
    }

    private static Intent buildHomeIntent(String category) {
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory(category);
        intent.setFlags(270532608);
        return intent;
    }

    public void onSwitchUser(int userHandle) {
        super.onSwitchUser(userHandle);
        getContext().getContentResolver().unregisterContentObserver(this.mSetupWizardObserver);
        verifySetupWizardCompleted();
    }

    public void onStart() {
        Context context = getContext();
        PowerManager powerManager = (PowerManager) context.getSystemService("power");
        this.mWakeLock = powerManager.newWakeLock(26, TAG);
        verifySetupWizardCompleted();
        context.registerReceiver(this.mDockModeReceiver, new IntentFilter("android.intent.action.DOCK_EVENT"));
        context.registerReceiver(this.mBatteryReceiver, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        PowerManagerInternal localPowerManager = (PowerManagerInternal) LocalServices.getService(PowerManagerInternal.class);
        this.mPowerSave = localPowerManager.getLowPowerState(16).batterySaverEnabled;
        localPowerManager.registerLowPowerModeObserver(16, new Consumer() {
            public final void accept(Object obj) {
                UiModeManagerService.this.lambda$onStart$0$UiModeManagerService((PowerSaveState) obj);
            }
        });
        this.mConfiguration.setToDefaults();
        Resources res = context.getResources();
        this.mDefaultUiModeType = res.getInteger(17694782);
        boolean z = true;
        this.mCarModeKeepsScreenOn = res.getInteger(17694760) == 1;
        this.mDeskModeKeepsScreenOn = res.getInteger(17694784) == 1;
        this.mEnableCarDockLaunch = res.getBoolean(17891436);
        this.mUiModeLocked = res.getBoolean(17891478);
        this.mNightModeLocked = res.getBoolean(17891477);
        PackageManager pm = context.getPackageManager();
        if (!pm.hasSystemFeature("android.hardware.type.television") && !pm.hasSystemFeature("android.software.leanback")) {
            z = false;
        }
        this.mTelevision = z;
        this.mWatch = pm.hasSystemFeature("android.hardware.type.watch");
        updateNightModeFromSettings(context, res, UserHandle.getCallingUserId());
        SystemServerInitThreadPool.get().submit(new Runnable() {
            public final void run() {
                UiModeManagerService.this.lambda$onStart$1$UiModeManagerService();
            }
        }, TAG + ".onStart");
        publishBinderService("uimode", this.mService);
        publishLocalService(UiModeManagerInternal.class, this.mLocalService);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.USER_SWITCHED");
        context.registerReceiver(new UserSwitchedReceiver(), filter, (String) null, this.mHandler);
        registUIModeScaleChangeObserver(this, context, this.mLock);
        UiModeManagerServiceInjector.init(context);
        final Context context2 = context;
        final int maximumScreenBrightnessSetting = powerManager.getMaximumScreenBrightnessSetting();
        final int minimumScreenBrightnessSetting = powerManager.getMinimumScreenBrightnessSetting();
        getContext().getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_brightness"), false, new ContentObserver(new Handler()) {
            public void onChange(boolean selfChange) {
                UiModeManagerService uiModeManagerService = UiModeManagerService.this;
                int unused = uiModeManagerService.mBrightness = Settings.System.getInt(uiModeManagerService.getContext().getContentResolver(), "screen_brightness", 0);
                UiModeManagerServiceInjector.updateAlpha(context2, UiModeManagerService.this.mBrightness, maximumScreenBrightnessSetting, minimumScreenBrightnessSetting);
            }
        });
        setForceDark(context);
        this.mIWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        this.mMiuiColorFade = new MiuiColorFade(0);
        this.mNightColorFadeAnimator = ObjectAnimator.ofFloat(this.mMiuiColorFade, COLOR_FADE_LEVEL, new float[]{1.0f, 0.0f});
        this.mNightColorFadeAnimator.setDuration(800);
        this.mNightColorFadeAnimator.addListener(this.mAnimatorListener);
        registerAnimFinishedCallback();
        registerActivityObserver();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0018, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public /* synthetic */ void lambda$onStart$0$UiModeManagerService(android.os.PowerSaveState r4) {
        /*
            r3 = this;
            java.lang.Object r0 = r3.mLock
            monitor-enter(r0)
            boolean r1 = r3.mPowerSave     // Catch:{ all -> 0x0019 }
            boolean r2 = r4.batterySaverEnabled     // Catch:{ all -> 0x0019 }
            if (r1 != r2) goto L_0x000b
            monitor-exit(r0)     // Catch:{ all -> 0x0019 }
            return
        L_0x000b:
            boolean r1 = r4.batterySaverEnabled     // Catch:{ all -> 0x0019 }
            r3.mPowerSave = r1     // Catch:{ all -> 0x0019 }
            boolean r1 = r3.mSystemReady     // Catch:{ all -> 0x0019 }
            if (r1 == 0) goto L_0x0017
            r1 = 0
            r3.updateLocked(r1, r1)     // Catch:{ all -> 0x0019 }
        L_0x0017:
            monitor-exit(r0)     // Catch:{ all -> 0x0019 }
            return
        L_0x0019:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0019 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.UiModeManagerService.lambda$onStart$0$UiModeManagerService(android.os.PowerSaveState):void");
    }

    public /* synthetic */ void lambda$onStart$1$UiModeManagerService() {
        synchronized (this.mLock) {
            updateConfigurationLocked();
            sendConfigurationLocked();
        }
    }

    /* access modifiers changed from: private */
    public void doNightChangeAnimation() {
        MiuiColorFade miuiColorFade = this.mMiuiColorFade;
        if (miuiColorFade != null && miuiColorFade.prepare(0)) {
            this.mNightColorFadeAnimator.start();
        }
    }

    private void verifySetupWizardCompleted() {
        Context context = getContext();
        int userId = UserHandle.getCallingUserId();
        if (!setupWizardCompleteForCurrentUser()) {
            this.mSetupWizardComplete = false;
            context.getContentResolver().registerContentObserver(Settings.Secure.getUriFor("user_setup_complete"), false, this.mSetupWizardObserver, userId);
            return;
        }
        this.mSetupWizardComplete = true;
    }

    /* access modifiers changed from: private */
    public boolean setupWizardCompleteForCurrentUser() {
        return Settings.Secure.getIntForUser(getContext().getContentResolver(), "user_setup_complete", 0, UserHandle.getCallingUserId()) == 1;
    }

    /* access modifiers changed from: private */
    public boolean updateNightModeFromSettings(Context context, Resources res, int userId) {
        int defaultNightMode = res.getInteger(17694775);
        int oldNightMode = this.mNightMode;
        if (this.mSetupWizardComplete) {
            this.mNightMode = Settings.Secure.getIntForUser(context.getContentResolver(), "ui_night_mode", defaultNightMode, userId);
        } else {
            this.mNightMode = defaultNightMode;
        }
        return oldNightMode != this.mNightMode;
    }

    /* access modifiers changed from: package-private */
    public void dumpImpl(PrintWriter pw) {
        synchronized (this.mLock) {
            pw.println("Current UI Mode Service state:");
            pw.print("  mDockState=");
            pw.print(this.mDockState);
            pw.print(" mLastBroadcastState=");
            pw.println(this.mLastBroadcastState);
            pw.print("  mNightMode=");
            pw.print(this.mNightMode);
            pw.print(" (");
            pw.print(Shell.nightModeToStr(this.mNightMode));
            pw.print(") ");
            pw.print(" mNightModeLocked=");
            pw.print(this.mNightModeLocked);
            pw.print(" mCarModeEnabled=");
            pw.print(this.mCarModeEnabled);
            pw.print(" mComputedNightMode=");
            pw.print(this.mComputedNightMode);
            pw.print(" mCarModeEnableFlags=");
            pw.print(this.mCarModeEnableFlags);
            pw.print(" mEnableCarDockLaunch=");
            pw.println(this.mEnableCarDockLaunch);
            pw.print("  mCurUiMode=0x");
            pw.print(Integer.toHexString(this.mCurUiMode));
            pw.print(" mUiModeLocked=");
            pw.print(this.mUiModeLocked);
            pw.print(" mSetUiMode=0x");
            pw.println(Integer.toHexString(this.mSetUiMode));
            pw.print("  mHoldingConfiguration=");
            pw.print(this.mHoldingConfiguration);
            pw.print(" mSystemReady=");
            pw.println(this.mSystemReady);
            if (this.mTwilightManager != null) {
                pw.print("  mTwilightService.getLastTwilightState()=");
                pw.println(this.mTwilightManager.getLastTwilightState());
            }
        }
    }

    public void onBootPhase(int phase) {
        if (phase == 500) {
            synchronized (this.mLock) {
                this.mTwilightManager = (TwilightManager) getLocalService(TwilightManager.class);
                boolean z = true;
                this.mSystemReady = true;
                if (this.mDockState != 2) {
                    z = false;
                }
                this.mCarModeEnabled = z;
                updateComputedNightModeLocked();
                registerVrStateListener();
                updateLocked(0, 0);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setCarModeLocked(boolean enabled, int flags) {
        if (this.mCarModeEnabled != enabled) {
            this.mCarModeEnabled = enabled;
            if (!this.mCarModeEnabled) {
                Context context = getContext();
                updateNightModeFromSettings(context, context.getResources(), UserHandle.getCallingUserId());
            }
        }
        this.mCarModeEnableFlags = flags;
    }

    /* access modifiers changed from: private */
    public void updateDockState(int newState) {
        synchronized (this.mLock) {
            if (newState != this.mDockState) {
                this.mDockState = newState;
                setCarModeLocked(this.mDockState == 2, 0);
                if (this.mSystemReady) {
                    updateLocked(1, 0);
                }
            }
        }
    }

    private static boolean isDeskDockState(int state) {
        if (state == 1 || state == 3 || state == 4) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void updateConfigurationLocked() {
        int uiMode;
        int i;
        int uiMode2 = this.mDefaultUiModeType;
        if (!this.mUiModeLocked) {
            if (this.mTelevision) {
                uiMode2 = 4;
            } else if (this.mWatch) {
                uiMode2 = 6;
            } else if (this.mCarModeEnabled) {
                uiMode2 = 3;
            } else if (isDeskDockState(this.mDockState)) {
                uiMode2 = 2;
            } else if (this.mVrHeadset) {
                uiMode2 = 7;
            }
        }
        if (this.mNightMode == 0) {
            TwilightManager twilightManager = this.mTwilightManager;
            if (twilightManager != null) {
                twilightManager.registerListener(this.mTwilightListener, this.mHandler);
            }
            updateComputedNightModeLocked();
            if (this.mComputedNightMode) {
                i = 32;
            } else {
                i = 16;
            }
            uiMode = uiMode2 | i;
        } else {
            TwilightManager twilightManager2 = this.mTwilightManager;
            if (twilightManager2 != null) {
                twilightManager2.unregisterListener(this.mTwilightListener);
            }
            uiMode = uiMode2 | (this.mNightMode << 4);
        }
        if (this.mPowerSave && !this.mCarModeEnabled) {
            uiMode = (uiMode & -17) | 32;
        }
        this.mCurUiMode = uiMode;
        if (!this.mHoldingConfiguration) {
            this.mConfiguration.uiMode = uiMode;
        }
    }

    private void sendConfigurationLocked() {
        if (this.mSetUiMode != this.mConfiguration.uiMode) {
            this.mSetUiMode = this.mConfiguration.uiMode;
            try {
                ActivityTaskManager.getService().updateConfiguration(this.mConfiguration);
            } catch (RemoteException e) {
                Slog.w(TAG, "Failure communicating with activity manager", e);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void updateLocked(int enableFlags, int disableFlags) {
        int i = enableFlags;
        int i2 = disableFlags;
        String action = null;
        String oldAction = null;
        int i3 = this.mLastBroadcastState;
        if (i3 == 2) {
            adjustStatusBarCarModeLocked();
            oldAction = UiModeManager.ACTION_EXIT_CAR_MODE;
        } else if (isDeskDockState(i3)) {
            oldAction = UiModeManager.ACTION_EXIT_DESK_MODE;
        }
        if (this.mCarModeEnabled) {
            if (this.mLastBroadcastState != 2) {
                adjustStatusBarCarModeLocked();
                if (oldAction != null) {
                    sendForegroundBroadcastToAllUsers(oldAction);
                }
                this.mLastBroadcastState = 2;
                action = UiModeManager.ACTION_ENTER_CAR_MODE;
            }
        } else if (!isDeskDockState(this.mDockState)) {
            this.mLastBroadcastState = 0;
            action = oldAction;
        } else if (!isDeskDockState(this.mLastBroadcastState)) {
            if (oldAction != null) {
                sendForegroundBroadcastToAllUsers(oldAction);
            }
            this.mLastBroadcastState = this.mDockState;
            action = UiModeManager.ACTION_ENTER_DESK_MODE;
        }
        boolean keepScreenOn = true;
        if (action != null) {
            Intent intent = new Intent(action);
            intent.putExtra("enableFlags", i);
            intent.putExtra("disableFlags", i2);
            intent.addFlags(268435456);
            getContext().sendOrderedBroadcastAsUser(intent, UserHandle.CURRENT, (String) null, this.mResultReceiver, (Handler) null, -1, (String) null, (Bundle) null);
            this.mHoldingConfiguration = true;
            updateConfigurationLocked();
        } else {
            String category = null;
            if (this.mCarModeEnabled) {
                if (this.mEnableCarDockLaunch && (i & 1) != 0) {
                    category = "android.intent.category.CAR_DOCK";
                }
            } else if (isDeskDockState(this.mDockState)) {
                if ((i & 1) != 0) {
                    category = "android.intent.category.DESK_DOCK";
                }
            } else if ((i2 & 1) != 0) {
                category = "android.intent.category.HOME";
            }
            sendConfigurationAndStartDreamOrDockAppLocked(category);
        }
        if (!this.mCharging || ((!this.mCarModeEnabled || !this.mCarModeKeepsScreenOn || (this.mCarModeEnableFlags & 2) != 0) && (this.mCurUiMode != 2 || !this.mDeskModeKeepsScreenOn))) {
            keepScreenOn = false;
        }
        if (keepScreenOn == this.mWakeLock.isHeld()) {
            return;
        }
        if (keepScreenOn) {
            this.mWakeLock.acquire();
        } else {
            this.mWakeLock.release();
        }
    }

    private void sendForegroundBroadcastToAllUsers(String action) {
        getContext().sendBroadcastAsUser(new Intent(action).addFlags(268435456), UserHandle.ALL);
    }

    /* access modifiers changed from: private */
    public void updateAfterBroadcastLocked(String action, int enableFlags, int disableFlags) {
        String category = null;
        if (UiModeManager.ACTION_ENTER_CAR_MODE.equals(action)) {
            if (this.mEnableCarDockLaunch && (enableFlags & 1) != 0) {
                category = "android.intent.category.CAR_DOCK";
            }
        } else if (UiModeManager.ACTION_ENTER_DESK_MODE.equals(action)) {
            if ((enableFlags & 1) != 0) {
                category = "android.intent.category.DESK_DOCK";
            }
        } else if ((disableFlags & 1) != 0) {
            category = "android.intent.category.HOME";
        }
        sendConfigurationAndStartDreamOrDockAppLocked(category);
    }

    /* JADX WARNING: Removed duplicated region for block: B:21:0x007a A[ADDED_TO_REGION] */
    /* JADX WARNING: Removed duplicated region for block: B:24:? A[ADDED_TO_REGION, RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void sendConfigurationAndStartDreamOrDockAppLocked(java.lang.String r17) {
        /*
            r16 = this;
            r1 = r16
            java.lang.String r2 = "Could not start dock app: "
            r0 = 0
            r1.mHoldingConfiguration = r0
            r16.updateConfigurationLocked()
            r3 = 0
            if (r17 == 0) goto L_0x0075
            android.content.Intent r15 = buildHomeIntent(r17)
            android.content.Context r0 = r16.getContext()
            boolean r0 = android.service.dreams.Sandman.shouldStartDockApp(r0, r15)
            if (r0 == 0) goto L_0x0074
            android.app.IActivityTaskManager r4 = android.app.ActivityTaskManager.getService()     // Catch:{ RemoteException -> 0x005d }
            r5 = 0
            r6 = 0
            r8 = 0
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            android.content.res.Configuration r13 = r1.mConfiguration     // Catch:{ RemoteException -> 0x005d }
            r14 = 0
            r0 = -2
            r7 = r15
            r1 = r15
            r15 = r0
            int r0 = r4.startActivityWithConfig(r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15)     // Catch:{ RemoteException -> 0x005b }
            boolean r4 = android.app.ActivityManager.isStartResultSuccessful(r0)     // Catch:{ RemoteException -> 0x005b }
            if (r4 == 0) goto L_0x003a
            r2 = 1
            r3 = r2
            goto L_0x005a
        L_0x003a:
            r4 = -91
            if (r0 == r4) goto L_0x005a
            java.lang.String r4 = TAG     // Catch:{ RemoteException -> 0x005b }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x005b }
            r5.<init>()     // Catch:{ RemoteException -> 0x005b }
            r5.append(r2)     // Catch:{ RemoteException -> 0x005b }
            r5.append(r1)     // Catch:{ RemoteException -> 0x005b }
            java.lang.String r6 = ", startActivityWithConfig result "
            r5.append(r6)     // Catch:{ RemoteException -> 0x005b }
            r5.append(r0)     // Catch:{ RemoteException -> 0x005b }
            java.lang.String r5 = r5.toString()     // Catch:{ RemoteException -> 0x005b }
            android.util.Slog.e(r4, r5)     // Catch:{ RemoteException -> 0x005b }
        L_0x005a:
            goto L_0x0075
        L_0x005b:
            r0 = move-exception
            goto L_0x005f
        L_0x005d:
            r0 = move-exception
            r1 = r15
        L_0x005f:
            java.lang.String r4 = TAG
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r2)
            r5.append(r1)
            java.lang.String r2 = r5.toString()
            android.util.Slog.e(r4, r2, r0)
            goto L_0x0075
        L_0x0074:
            r1 = r15
        L_0x0075:
            r16.sendConfigurationLocked()
            if (r17 == 0) goto L_0x0083
            if (r3 != 0) goto L_0x0083
            android.content.Context r0 = r16.getContext()
            android.service.dreams.Sandman.startDreamWhenDockedIfAppropriate(r0)
        L_0x0083:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.UiModeManagerService.sendConfigurationAndStartDreamOrDockAppLocked(java.lang.String):void");
    }

    private void adjustStatusBarCarModeLocked() {
        int i;
        Context context = getContext();
        if (this.mStatusBarManager == null) {
            this.mStatusBarManager = (StatusBarManager) context.getSystemService("statusbar");
        }
        StatusBarManager statusBarManager = this.mStatusBarManager;
        if (statusBarManager != null) {
            if (this.mCarModeEnabled) {
                i = DumpState.DUMP_FROZEN;
            } else {
                i = 0;
            }
            statusBarManager.disable(i);
        }
        if (this.mNotificationManager == null) {
            this.mNotificationManager = (NotificationManager) context.getSystemService("notification");
        }
        NotificationManager notificationManager = this.mNotificationManager;
        if (notificationManager == null) {
            return;
        }
        if (this.mCarModeEnabled) {
            Intent carModeOffIntent = new Intent(context, DisableCarModeActivity.class);
            this.mNotificationManager.notifyAsUser((String) null, 10, new Notification.Builder(context, SystemNotificationChannels.CAR_MODE).setSmallIcon(17303558).setDefaults(4).setOngoing(true).setWhen(0).setColor(context.getColor(17170460)).setContentTitle(context.getString(17039664)).setContentText(context.getString(17039663)).setContentIntent(PendingIntent.getActivityAsUser(context, 0, carModeOffIntent, 0, (Bundle) null, UserHandle.CURRENT)).build(), UserHandle.ALL);
            return;
        }
        notificationManager.cancelAsUser((String) null, 10, UserHandle.ALL);
    }

    /* access modifiers changed from: private */
    public void updateComputedNightModeLocked() {
        TwilightState state;
        TwilightManager twilightManager = this.mTwilightManager;
        if (twilightManager != null && (state = twilightManager.getLastTwilightState()) != null) {
            this.mComputedNightMode = state.isNight();
        }
    }

    private void registerVrStateListener() {
        IVrManager vrManager = IVrManager.Stub.asInterface(ServiceManager.getService("vrmanager"));
        if (vrManager != null) {
            try {
                vrManager.registerListener(this.mVrStateCallbacks);
            } catch (RemoteException e) {
                String str = TAG;
                Slog.e(str, "Failed to register VR mode state listener: " + e);
            }
        }
    }

    private static class Shell extends ShellCommand {
        public static final String NIGHT_MODE_STR_AUTO = "auto";
        public static final String NIGHT_MODE_STR_NO = "no";
        public static final String NIGHT_MODE_STR_UNKNOWN = "unknown";
        public static final String NIGHT_MODE_STR_YES = "yes";
        private final IUiModeManager mInterface;

        Shell(IUiModeManager iface) {
            this.mInterface = iface;
        }

        public void onHelp() {
            PrintWriter pw = getOutPrintWriter();
            pw.println("UiModeManager service (uimode) commands:");
            pw.println("  help");
            pw.println("    Print this help text.");
            pw.println("  night [yes|no|auto]");
            pw.println("    Set or read night mode.");
        }

        public int onCommand(String cmd) {
            if (cmd == null) {
                return handleDefaultCommands(cmd);
            }
            try {
                if ((cmd.hashCode() == 104817688 && cmd.equals("night")) ? false : true) {
                    return handleDefaultCommands(cmd);
                }
                return handleNightMode();
            } catch (RemoteException e) {
                PrintWriter err = getErrPrintWriter();
                err.println("Remote exception: " + e);
                return -1;
            }
        }

        private int handleNightMode() throws RemoteException {
            PrintWriter err = getErrPrintWriter();
            String modeStr = getNextArg();
            if (modeStr == null) {
                printCurrentNightMode();
                return 0;
            }
            int mode = strToNightMode(modeStr);
            if (mode >= 0) {
                this.mInterface.setNightMode(mode);
                printCurrentNightMode();
                return 0;
            }
            err.println("Error: mode must be 'yes', 'no', or 'auto'");
            return -1;
        }

        private void printCurrentNightMode() throws RemoteException {
            PrintWriter pw = getOutPrintWriter();
            String currModeStr = nightModeToStr(this.mInterface.getNightMode());
            pw.println("Night mode: " + currModeStr);
        }

        /* access modifiers changed from: private */
        public static String nightModeToStr(int mode) {
            if (mode == 0) {
                return NIGHT_MODE_STR_AUTO;
            }
            if (mode == 1) {
                return NIGHT_MODE_STR_NO;
            }
            if (mode != 2) {
                return NIGHT_MODE_STR_UNKNOWN;
            }
            return NIGHT_MODE_STR_YES;
        }

        /* JADX WARNING: Removed duplicated region for block: B:17:0x003a  */
        /* JADX WARNING: Removed duplicated region for block: B:22:0x0041 A[RETURN] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private static int strToNightMode(java.lang.String r6) {
            /*
                int r0 = r6.hashCode()
                r1 = 3521(0xdc1, float:4.934E-42)
                r2 = 0
                r3 = -1
                r4 = 2
                r5 = 1
                if (r0 == r1) goto L_0x002c
                r1 = 119527(0x1d2e7, float:1.67493E-40)
                if (r0 == r1) goto L_0x0021
                r1 = 3005871(0x2dddaf, float:4.212122E-39)
                if (r0 == r1) goto L_0x0017
            L_0x0016:
                goto L_0x0037
            L_0x0017:
                java.lang.String r0 = "auto"
                boolean r0 = r6.equals(r0)
                if (r0 == 0) goto L_0x0016
                r0 = r4
                goto L_0x0038
            L_0x0021:
                java.lang.String r0 = "yes"
                boolean r0 = r6.equals(r0)
                if (r0 == 0) goto L_0x0016
                r0 = r2
                goto L_0x0038
            L_0x002c:
                java.lang.String r0 = "no"
                boolean r0 = r6.equals(r0)
                if (r0 == 0) goto L_0x0016
                r0 = r5
                goto L_0x0038
            L_0x0037:
                r0 = r3
            L_0x0038:
                if (r0 == 0) goto L_0x0041
                if (r0 == r5) goto L_0x0040
                if (r0 == r4) goto L_0x003f
                return r3
            L_0x003f:
                return r2
            L_0x0040:
                return r5
            L_0x0041:
                return r4
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.UiModeManagerService.Shell.strToNightMode(java.lang.String):int");
        }
    }

    public final class LocalService extends UiModeManagerInternal {
        public LocalService() {
        }

        public boolean isNightMode() {
            boolean isIt;
            synchronized (UiModeManagerService.this.mLock) {
                isIt = (UiModeManagerService.this.mConfiguration.uiMode & 32) != 0;
            }
            return isIt;
        }
    }

    private final class UserSwitchedReceiver extends BroadcastReceiver {
        private UserSwitchedReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            synchronized (UiModeManagerService.this.mLock) {
                if (UiModeManagerService.this.updateNightModeFromSettings(context, context.getResources(), intent.getIntExtra("android.intent.extra.user_handle", 0))) {
                    UiModeManagerService.this.updateLocked(0, 0);
                }
            }
        }
    }

    private static void registUIModeScaleChangeObserver(final UiModeManagerService service, final Context context, final Object lock) {
        ContentObserver uiModeScaleChangedObserver = new ContentObserver(new Handler()) {
            public void onChange(boolean selfChange) {
                int unused = service.mDefaultUiModeType = Settings.System.getInt(context.getContentResolver(), "ui_mode_scale", 1);
                synchronized (lock) {
                    if (service.mSystemReady) {
                        service.updateConfigurationLocked();
                    }
                }
            }
        };
        context.getContentResolver().registerContentObserver(Settings.System.getUriFor("ui_mode_scale"), false, uiModeScaleChangedObserver);
        uiModeScaleChangedObserver.onChange(false);
    }

    private void setForceDark(Context context) {
        Settings.System.putIntForUser(getContext().getContentResolver(), "dark_mode_enable", this.mNightMode == 2 ? 1 : 0, UserHandle.getCallingUserId());
        String str = TAG;
        Slog.d(str, "mNightMode: " + this.mNightMode);
        if (this.mNightMode == 2) {
            SystemProperties.set("debug.hwui.force_dark", "true");
        }
    }

    private void setDarkProp(int mode, int user) {
        int darkmode = 0;
        if (mode == 2) {
            darkmode = 1;
        }
        Settings.System.putIntForUser(getContext().getContentResolver(), "dark_mode_enable", darkmode, user);
        Settings.System.putIntForUser(getContext().getContentResolver(), "smart_dark_enable", darkmode, user);
        SystemProperties.set("debug.hwui.force_dark", darkmode == 1 ? "true" : "false");
    }

    /* access modifiers changed from: private */
    public boolean shouldDelayNightModeChange(int mode, int user) {
        if ((Settings.Global.getInt(getContext().getContentResolver(), "uimode_timing", 0) != 0) && mode != 0) {
            String str = TAG;
            Slog.v(str, "pending switch night mode to " + this.mNightMode);
            Settings.Global.putInt(getContext().getContentResolver(), "uimode_timing", 0);
            Intent activity = null;
            try {
                activity = ActivityTaskManager.getService().getTopVisibleActivity();
            } catch (RemoteException e) {
                Slog.w(TAG, "Failure communicating with activity manager", e);
            }
            this.mLastResumedActivity = activity != null ? activity.getComponent() : null;
            if (this.mLastResumedActivity != null) {
                this.mDelayedNightMode = mode;
                this.mDelayedSetNightModeUser = user;
                this.mShouldPendingSwitch = true;
                return true;
            }
        }
        this.mShouldPendingSwitch = false;
        return false;
    }

    /* access modifiers changed from: private */
    public void setNightModeLocked(int mode, int user) {
        synchronized (this.mLock) {
            if (this.mNightMode != mode && mode >= 0) {
                this.mHandler.obtainMessage(1).sendToTarget();
                if (!this.mCarModeEnabled) {
                    Settings.Secure.putIntForUser(getContext().getContentResolver(), "ui_night_mode", mode, user);
                    if (UserManager.get(getContext()).isPrimaryUser()) {
                        SystemProperties.set(SYSTEM_PROPERTY_DEVICE_THEME, Integer.toString(mode));
                    }
                }
                setDarkProp(mode, user);
                this.mNightMode = mode;
                updateLocked(0, 0);
                this.mDelayedNightMode = -1;
            } else if (this.mDelayedNightMode > 0 && this.mDelayedNightMode != mode) {
                this.mDelayedNightMode = -1;
            }
        }
    }

    private void registerActivityObserver() {
        try {
            ActivityManager.getService().registerActivityObserver(this.mMiuiActivityObserver, new Intent());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void registerAnimFinishedCallback() {
        try {
            this.mIWindowManager.registerUiModeAnimFinishedCallback(this.mAnimFinishedCallback);
        } catch (RemoteException e) {
            String str = TAG;
            Slog.d(str, "registerAnimFinishedCallback error" + e);
        }
    }
}
