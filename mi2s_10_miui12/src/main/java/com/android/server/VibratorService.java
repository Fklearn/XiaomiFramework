package com.android.server;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.IUidObserver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.input.InputManager;
import android.icu.text.DateFormat;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.os.Binder;
import android.os.ExternalVibration;
import android.os.Handler;
import android.os.IBinder;
import android.os.IExternalVibratorService;
import android.os.IVibratorService;
import android.os.PowerManager;
import android.os.PowerManagerInternal;
import android.os.PowerSaveState;
import android.os.Process;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ShellCallback;
import android.os.ShellCommand;
import android.os.SystemClock;
import android.os.Trace;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.WorkSource;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.util.Slog;
import android.util.SparseArray;
import android.util.StatsLog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.IBatteryStats;
import com.android.internal.util.DumpUtils;
import com.android.server.job.controllers.JobStatus;
import com.android.server.notification.NotificationShellCmd;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

public class VibratorService extends IVibratorService.Stub implements InputManager.InputDeviceListener {
    private static final boolean DEBUG = false;
    private static final long[] DOUBLE_CLICK_EFFECT_FALLBACK_TIMINGS = {0, 30, 100, 30};
    private static final String EXTERNAL_VIBRATOR_SERVICE = "external_vibrator_service";
    private static final long MAX_HAPTIC_FEEDBACK_DURATION = 5000;
    private static final String RAMPING_RINGER_ENABLED = "ramping_ringer_enabled";
    private static final int SCALE_HIGH = 1;
    private static final float SCALE_HIGH_GAMMA = 0.5f;
    private static final int SCALE_LOW = -1;
    private static final float SCALE_LOW_GAMMA = 1.5f;
    private static final int SCALE_LOW_MAX_AMPLITUDE = 192;
    private static final int SCALE_MUTE = -100;
    private static final int SCALE_NONE = 0;
    private static final float SCALE_NONE_GAMMA = 1.0f;
    private static final int SCALE_VERY_HIGH = 2;
    private static final float SCALE_VERY_HIGH_GAMMA = 0.25f;
    private static final int SCALE_VERY_LOW = -2;
    private static final float SCALE_VERY_LOW_GAMMA = 2.0f;
    private static final int SCALE_VERY_LOW_MAX_AMPLITUDE = 168;
    private static final String SYSTEM_UI_PACKAGE = "com.android.systemui";
    private static final String TAG = "VibratorService";
    private final long MAX_VIBRATOR_TIMEOUT = JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY;
    private final long PERFECT_VIBRATOR_TIMEOUT = 1000;
    private final boolean mAllowPriorityVibrationsInLowPowerMode;
    private final AppOpsManager mAppOps;
    private final IBatteryStats mBatteryStatsService;
    /* access modifiers changed from: private */
    public final Context mContext;
    private int mCurVibUid = -1;
    /* access modifiers changed from: private */
    public ExternalVibration mCurrentExternalVibration;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public Vibration mCurrentVibration;
    private final int mDefaultVibrationAmplitude;
    private final SparseArray<VibrationEffect> mFallbackEffects;
    private final Handler mH = new Handler();
    /* access modifiers changed from: private */
    public int mHapticFeedbackIntensity;
    private InputManager mIm;
    private boolean mInputDeviceListenerRegistered;
    private final ArrayList<Vibrator> mInputDeviceVibrators = new ArrayList<>();
    BroadcastReceiver mIntentReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.SCREEN_OFF")) {
                synchronized (VibratorService.this.mLock) {
                    if (VibratorService.this.mCurrentVibration != null && (!VibratorService.this.mCurrentVibration.isHapticFeedback() || !VibratorService.this.mCurrentVibration.isFromSystem())) {
                        VibratorService.this.doCancelVibrateLocked();
                    }
                }
            }
        }
    };
    /* access modifiers changed from: private */
    public final Object mLock = new Object();
    private boolean mLowPowerMode;
    /* access modifiers changed from: private */
    public int mNotificationIntensity;
    private PowerManagerInternal mPowerManagerInternal;
    private final LinkedList<VibrationInfo> mPreviousAlarmVibrations;
    /* access modifiers changed from: private */
    public final LinkedList<ExternalVibration> mPreviousExternalVibrations;
    private final LinkedList<VibrationInfo> mPreviousNotificationVibrations;
    private final LinkedList<VibrationInfo> mPreviousRingVibrations;
    private final LinkedList<VibrationInfo> mPreviousVibrations;
    /* access modifiers changed from: private */
    public final int mPreviousVibrationsLimit;
    /* access modifiers changed from: private */
    public final SparseArray<Integer> mProcStatesCache = new SparseArray<>();
    /* access modifiers changed from: private */
    public int mRingIntensity;
    private final SparseArray<ScaleLevel> mScaleLevels;
    private SettingsObserver mSettingObserver;
    private final boolean mSupportsAmplitudeControl;
    /* access modifiers changed from: private */
    public final boolean mSupportsExternalControl;
    /* access modifiers changed from: private */
    public volatile VibrateThread mThread;
    /* access modifiers changed from: private */
    public final WorkSource mTmpWorkSource = new WorkSource();
    private final IUidObserver mUidObserver = new IUidObserver.Stub() {
        public void onUidStateChanged(int uid, int procState, long procStateSeq) {
            VibratorService.this.mProcStatesCache.put(uid, Integer.valueOf(procState));
        }

        public void onUidGone(int uid, boolean disabled) {
            VibratorService.this.mProcStatesCache.delete(uid);
        }

        public void onUidActive(int uid) {
        }

        public void onUidIdle(int uid, boolean disabled) {
        }

        public void onUidCachedChanged(int uid, boolean cached) {
        }
    };
    private boolean mVibrateInputDevicesSetting;
    private final Runnable mVibrationEndRunnable = new Runnable() {
        public void run() {
            VibratorService.this.onVibrationFinished();
        }
    };
    /* access modifiers changed from: private */
    public Vibrator mVibrator;
    private boolean mVibratorUnderExternalControl;
    /* access modifiers changed from: private */
    public final PowerManager.WakeLock mWakeLock;

    static native boolean vibratorExists();

    static native void vibratorInit();

    static native void vibratorOff();

    static native void vibratorOn(long j);

    static native long vibratorPerformEffect(long j, long j2);

    static native void vibratorSetAmplitude(int i);

    static native void vibratorSetExternalControl(boolean z);

    static native boolean vibratorSupportsAmplitudeControl();

    static native boolean vibratorSupportsExternalControl();

    private class Vibration implements IBinder.DeathRecipient {
        public VibrationEffect effect;
        public final String opPkg;
        public VibrationEffect originalEffect;
        public final String reason;
        public final long startTime;
        public final long startTimeDebug;
        public final IBinder token;
        public final int uid;
        public final int usageHint;

        private Vibration(IBinder token2, VibrationEffect effect2, int usageHint2, int uid2, String opPkg2, String reason2) {
            this.token = token2;
            this.effect = effect2;
            this.startTime = SystemClock.elapsedRealtime();
            this.startTimeDebug = System.currentTimeMillis();
            this.usageHint = usageHint2;
            this.uid = uid2;
            this.opPkg = opPkg2;
            this.reason = reason2;
        }

        public void binderDied() {
            synchronized (VibratorService.this.mLock) {
                if (this == VibratorService.this.mCurrentVibration) {
                    VibratorService.this.doCancelVibrateLocked();
                }
            }
        }

        public boolean hasTimeoutLongerThan(long millis) {
            long duration = this.effect.getDuration();
            return duration >= 0 && duration > millis;
        }

        public boolean isHapticFeedback() {
            VibratorService vibratorService = VibratorService.this;
            if (VibratorService.isHapticFeedback(this.usageHint)) {
                return true;
            }
            VibrationEffect.Prebaked prebaked = this.effect;
            if (prebaked instanceof VibrationEffect.Prebaked) {
                int id = prebaked.getId();
                if (id == 0 || id == 1 || id == 2 || id == 3 || id == 4 || id == 5 || id == 21) {
                    return true;
                }
                Slog.w(VibratorService.TAG, "Unknown prebaked vibration effect, assuming it isn't haptic feedback.");
                return false;
            }
            long duration = prebaked.getDuration();
            if (duration < 0 || duration >= VibratorService.MAX_HAPTIC_FEEDBACK_DURATION) {
                return false;
            }
            return true;
        }

        public boolean isNotification() {
            VibratorService vibratorService = VibratorService.this;
            return VibratorService.isNotification(this.usageHint);
        }

        public boolean isRingtone() {
            VibratorService vibratorService = VibratorService.this;
            return VibratorService.isRingtone(this.usageHint);
        }

        public boolean isAlarm() {
            VibratorService vibratorService = VibratorService.this;
            return VibratorService.isAlarm(this.usageHint);
        }

        public boolean isFromSystem() {
            int i = this.uid;
            return i == 1000 || i == 0 || "com.android.systemui".equals(this.opPkg);
        }

        public VibrationInfo toInfo() {
            return new VibrationInfo(this.startTimeDebug, this.effect, this.originalEffect, this.usageHint, this.uid, this.opPkg, this.reason);
        }
    }

    private static class VibrationInfo {
        private final VibrationEffect mEffect;
        private final String mOpPkg;
        private final VibrationEffect mOriginalEffect;
        private final String mReason;
        private final long mStartTimeDebug;
        private final int mUid;
        private final int mUsageHint;

        public VibrationInfo(long startTimeDebug, VibrationEffect effect, VibrationEffect originalEffect, int usageHint, int uid, String opPkg, String reason) {
            this.mStartTimeDebug = startTimeDebug;
            this.mEffect = effect;
            this.mOriginalEffect = originalEffect;
            this.mUsageHint = usageHint;
            this.mUid = uid;
            this.mOpPkg = opPkg;
            this.mReason = reason;
        }

        public String toString() {
            return "startTime: " + DateFormat.getDateTimeInstance().format(new Date(this.mStartTimeDebug)) + ", effect: " + this.mEffect + ", originalEffect: " + this.mOriginalEffect + ", usageHint: " + this.mUsageHint + ", uid: " + this.mUid + ", opPkg: " + this.mOpPkg + ", reason: " + this.mReason;
        }
    }

    private static final class ScaleLevel {
        public final float gamma;
        public final int maxAmplitude;

        public ScaleLevel(float gamma2) {
            this(gamma2, 255);
        }

        public ScaleLevel(float gamma2, int maxAmplitude2) {
            this.gamma = gamma2;
            this.maxAmplitude = maxAmplitude2;
        }

        public String toString() {
            return "ScaleLevel{gamma=" + this.gamma + ", maxAmplitude=" + this.maxAmplitude + "}";
        }
    }

    /* JADX WARNING: type inference failed for: r0v9, types: [com.android.server.VibratorService$ExternalVibratorService, android.os.IBinder] */
    VibratorService(Context context) {
        vibratorInit();
        vibratorOff();
        this.mSupportsAmplitudeControl = vibratorSupportsAmplitudeControl();
        this.mSupportsExternalControl = vibratorSupportsExternalControl();
        this.mContext = context;
        this.mWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(1, "*vibrator*");
        this.mWakeLock.setReferenceCounted(true);
        this.mAppOps = (AppOpsManager) this.mContext.getSystemService(AppOpsManager.class);
        this.mBatteryStatsService = IBatteryStats.Stub.asInterface(ServiceManager.getService("batterystats"));
        this.mPreviousVibrationsLimit = this.mContext.getResources().getInteger(17694877);
        this.mDefaultVibrationAmplitude = this.mContext.getResources().getInteger(17694783);
        this.mAllowPriorityVibrationsInLowPowerMode = this.mContext.getResources().getBoolean(17891345);
        this.mPreviousRingVibrations = new LinkedList<>();
        this.mPreviousNotificationVibrations = new LinkedList<>();
        this.mPreviousAlarmVibrations = new LinkedList<>();
        this.mPreviousVibrations = new LinkedList<>();
        this.mPreviousExternalVibrations = new LinkedList<>();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_OFF");
        context.registerReceiver(this.mIntentReceiver, filter);
        VibrationEffect clickEffect = createEffectFromResource(17236082);
        VibrationEffect doubleClickEffect = VibrationEffect.createWaveform(DOUBLE_CLICK_EFFECT_FALLBACK_TIMINGS, -1);
        VibrationEffect heavyClickEffect = createEffectFromResource(17236037);
        VibrationEffect tickEffect = createEffectFromResource(17236002);
        this.mFallbackEffects = new SparseArray<>();
        this.mFallbackEffects.put(0, clickEffect);
        this.mFallbackEffects.put(1, doubleClickEffect);
        this.mFallbackEffects.put(2, tickEffect);
        this.mFallbackEffects.put(5, heavyClickEffect);
        this.mFallbackEffects.put(21, VibrationEffect.get(2, false));
        this.mScaleLevels = new SparseArray<>();
        this.mScaleLevels.put(-2, new ScaleLevel(SCALE_VERY_LOW_GAMMA, SCALE_VERY_LOW_MAX_AMPLITUDE));
        this.mScaleLevels.put(-1, new ScaleLevel(SCALE_LOW_GAMMA, SCALE_LOW_MAX_AMPLITUDE));
        this.mScaleLevels.put(0, new ScaleLevel(1.0f));
        this.mScaleLevels.put(1, new ScaleLevel(0.5f));
        this.mScaleLevels.put(2, new ScaleLevel(SCALE_VERY_HIGH_GAMMA));
        ServiceManager.addService(EXTERNAL_VIBRATOR_SERVICE, new ExternalVibratorService());
    }

    private VibrationEffect createEffectFromResource(int resId) {
        return createEffectFromTimings(getLongIntArray(this.mContext.getResources(), resId));
    }

    private static VibrationEffect createEffectFromTimings(long[] timings) {
        if (timings == null || timings.length == 0) {
            return null;
        }
        if (timings.length == 1) {
            return VibrationEffect.createOneShot(timings[0], -1);
        }
        return VibrationEffect.createWaveform(timings, -1);
    }

    public void systemReady() {
        Trace.traceBegin(8388608, "VibratorService#systemReady");
        try {
            this.mIm = (InputManager) this.mContext.getSystemService(InputManager.class);
            this.mVibrator = (Vibrator) this.mContext.getSystemService(Vibrator.class);
            this.mSettingObserver = new SettingsObserver(this.mH);
            this.mPowerManagerInternal = (PowerManagerInternal) LocalServices.getService(PowerManagerInternal.class);
            this.mPowerManagerInternal.registerLowPowerModeObserver(new PowerManagerInternal.LowPowerModeListener() {
                public int getServiceType() {
                    return 2;
                }

                public void onLowPowerModeChanged(PowerSaveState result) {
                    VibratorService.this.updateVibrators();
                }
            });
            this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("vibrate_input_devices"), true, this.mSettingObserver, -1);
            this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("haptic_feedback_intensity"), true, this.mSettingObserver, -1);
            this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("notification_vibration_intensity"), true, this.mSettingObserver, -1);
            this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("ring_vibration_intensity"), true, this.mSettingObserver, -1);
            this.mContext.registerReceiver(new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    VibratorService.this.updateVibrators();
                }
            }, new IntentFilter("android.intent.action.USER_SWITCHED"), (String) null, this.mH);
            try {
                ActivityManager.getService().registerUidObserver(this.mUidObserver, 3, -1, (String) null);
            } catch (RemoteException e) {
            }
            updateVibrators();
            VibratorServiceInjector.listenForCallState(this.mContext);
            VibratorServiceInjector.init(this, this.mContext);
        } finally {
            Trace.traceEnd(8388608);
        }
    }

    private final class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean SelfChange) {
            VibratorService.this.updateVibrators();
        }
    }

    public boolean hasVibrator() {
        return doVibratorExists();
    }

    public boolean hasAmplitudeControl() {
        boolean z;
        synchronized (this.mInputDeviceVibrators) {
            z = this.mSupportsAmplitudeControl && this.mInputDeviceVibrators.isEmpty();
        }
        return z;
    }

    private void verifyIncomingUid(int uid) {
        if (uid != Binder.getCallingUid() && Binder.getCallingPid() != Process.myPid()) {
            this.mContext.enforcePermission("android.permission.UPDATE_APP_OPS_STATS", Binder.getCallingPid(), Binder.getCallingUid(), (String) null);
        }
    }

    private static boolean verifyVibrationEffect(VibrationEffect effect) {
        if (effect == null) {
            Slog.wtf(TAG, "effect must not be null");
            return false;
        }
        try {
            effect.validate();
            return true;
        } catch (Exception e) {
            Slog.wtf(TAG, "Encountered issue when verifying VibrationEffect.", e);
            return false;
        }
    }

    private static long[] getLongIntArray(Resources r, int resid) {
        int[] ar = r.getIntArray(resid);
        if (ar == null) {
            return null;
        }
        long[] out = new long[ar.length];
        for (int i = 0; i < ar.length; i++) {
            out[i] = (long) ar[i];
        }
        return out;
    }

    /* Debug info: failed to restart local var, previous not found, register: 18 */
    public void vibrate(int uid, String opPkg, VibrationEffect effect, int usageHint, String reason, IBinder token) {
        Object obj;
        long ident;
        int i = uid;
        Trace.traceBegin(8388608, "vibrate, reason = " + reason);
        try {
            if (this.mContext.checkCallingOrSelfPermission("android.permission.VIBRATE") != 0) {
                throw new SecurityException("Requires VIBRATE permission");
            } else if (token == null) {
                Slog.e(TAG, "token must not be null");
                Trace.traceEnd(8388608);
            } else {
                verifyIncomingUid(uid);
                if (!verifyVibrationEffect(effect)) {
                    Trace.traceEnd(8388608);
                    return;
                }
                VibrationEffect effect2 = VibratorServiceInjector.shouldVibrateForMiui(uid, opPkg, effect, usageHint, token, this.mContext);
                if (effect2 == null) {
                    Trace.traceEnd(8388608);
                    return;
                }
                try {
                    Object obj2 = this.mLock;
                    synchronized (obj2) {
                        if ((effect2 instanceof VibrationEffect.OneShot) && this.mCurrentVibration != null && (this.mCurrentVibration.effect instanceof VibrationEffect.OneShot)) {
                            VibrationEffect.OneShot newOneShot = (VibrationEffect.OneShot) effect2;
                            VibrationEffect.OneShot currentOneShot = this.mCurrentVibration.effect;
                            if (this.mCurrentVibration.hasTimeoutLongerThan(newOneShot.getDuration()) && newOneShot.getAmplitude() == currentOneShot.getAmplitude()) {
                                Trace.traceEnd(8388608);
                                return;
                            }
                        }
                        try {
                            if (this.mCurrentExternalVibration != null) {
                                Trace.traceEnd(8388608);
                            } else if (isRepeatingVibration(effect2) || this.mCurrentVibration == null || !isRepeatingVibration(this.mCurrentVibration.effect)) {
                                obj = obj2;
                                try {
                                    Vibration vibration = new Vibration(token, effect2, usageHint, uid, opPkg, reason);
                                    Vibration vib = vibration;
                                    if (this.mProcStatesCache.get(i, 7).intValue() <= 7 || vib.isNotification() || vib.isRingtone() || vib.isAlarm()) {
                                        linkVibration(vib);
                                        ident = Binder.clearCallingIdentity();
                                        doCancelVibrateLocked();
                                        startVibrationLocked(vib);
                                        addToPreviousVibrationsLocked(vib);
                                        Binder.restoreCallingIdentity(ident);
                                        Trace.traceEnd(8388608);
                                        return;
                                    }
                                    Slog.e(TAG, "Ignoring incoming vibration as process with uid = " + i + " is background, usage = " + AudioAttributes.usageToString(vib.usageHint));
                                    Trace.traceEnd(8388608);
                                } catch (Throwable th) {
                                    th = th;
                                    throw th;
                                }
                            } else {
                                Trace.traceEnd(8388608);
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            obj = obj2;
                            throw th;
                        }
                    }
                } catch (Throwable th3) {
                    th = th3;
                }
            }
        } catch (Throwable th4) {
            th = th4;
            VibrationEffect vibrationEffect = effect;
            Trace.traceEnd(8388608);
            throw th;
        }
    }

    private static boolean isRepeatingVibration(VibrationEffect effect) {
        return effect.getDuration() == JobStatus.NO_LATEST_RUNTIME;
    }

    private void addToPreviousVibrationsLocked(Vibration vib) {
        LinkedList<VibrationInfo> previousVibrations;
        if (vib.isRingtone()) {
            previousVibrations = this.mPreviousRingVibrations;
        } else if (vib.isNotification()) {
            previousVibrations = this.mPreviousNotificationVibrations;
        } else if (vib.isAlarm()) {
            previousVibrations = this.mPreviousAlarmVibrations;
        } else {
            previousVibrations = this.mPreviousVibrations;
        }
        if (previousVibrations.size() > this.mPreviousVibrationsLimit) {
            previousVibrations.removeFirst();
        }
        previousVibrations.addLast(vib.toInfo());
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* JADX INFO: finally extract failed */
    public void cancelVibrate(IBinder token) {
        this.mContext.enforceCallingOrSelfPermission("android.permission.VIBRATE", "cancelVibrate");
        synchronized (this.mLock) {
            if (this.mCurrentVibration != null && this.mCurrentVibration.token == token) {
                long ident = Binder.clearCallingIdentity();
                try {
                    doCancelVibrateLocked();
                    Binder.restoreCallingIdentity(ident);
                } catch (Throwable th) {
                    Binder.restoreCallingIdentity(ident);
                    throw th;
                }
            }
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public void doCancelVibrateLocked() {
        Trace.asyncTraceEnd(8388608, "vibration", 0);
        Trace.traceBegin(8388608, "doCancelVibrateLocked");
        try {
            this.mH.removeCallbacks(this.mVibrationEndRunnable);
            if (this.mThread != null) {
                this.mThread.cancel();
                this.mThread = null;
            }
            if (this.mCurrentExternalVibration != null) {
                this.mCurrentExternalVibration.mute();
                this.mCurrentExternalVibration = null;
                setVibratorUnderExternalControl(false);
            }
            doVibratorOff();
            reportFinishVibrationLocked();
        } finally {
            Trace.traceEnd(8388608);
        }
    }

    public void onVibrationFinished() {
        synchronized (this.mLock) {
            doCancelVibrateLocked();
        }
    }

    @GuardedBy({"mLock"})
    private void startVibrationLocked(Vibration vib) {
        Trace.traceBegin(8388608, "startVibrationLocked");
        try {
            if (isAllowedToVibrateLocked(vib)) {
                int intensity = getCurrentIntensityLocked(vib);
                if (intensity == 0) {
                    Trace.traceEnd(8388608);
                    return;
                }
                int mode = getAppOpMode(vib);
                if (mode != 0) {
                    if (mode == 2) {
                        Slog.w(TAG, "Would be an error: vibrate from uid " + vib.uid);
                    }
                    Trace.traceEnd(8388608);
                    return;
                }
                applyVibrationIntensityScalingLocked(vib, intensity);
                startVibrationInnerLocked(vib);
                Trace.traceEnd(8388608);
            }
        } finally {
            Trace.traceEnd(8388608);
        }
    }

    @GuardedBy({"mLock"})
    private void startVibrationInnerLocked(Vibration vib) {
        Trace.traceBegin(8388608, "startVibrationInnerLocked");
        try {
            this.mCurrentVibration = vib;
            if (vib.effect instanceof VibrationEffect.OneShot) {
                Trace.asyncTraceBegin(8388608, "vibration", 0);
                VibrationEffect.OneShot oneShot = vib.effect;
                doVibratorOn(oneShot.getDuration(), oneShot.getAmplitude(), vib.uid, vib.usageHint);
                this.mH.postDelayed(this.mVibrationEndRunnable, oneShot.getDuration());
            } else if (vib.effect instanceof VibrationEffect.Waveform) {
                Trace.asyncTraceBegin(8388608, "vibration", 0);
                this.mThread = new VibrateThread(vib.effect, vib.uid, vib.opPkg, vib.usageHint);
                this.mThread.start();
            } else if (vib.effect instanceof VibrationEffect.Prebaked) {
                Trace.asyncTraceBegin(8388608, "vibration", 0);
                long timeout = doVibratorPrebakedEffectLocked(vib);
                if (timeout > 0) {
                    this.mH.postDelayed(this.mVibrationEndRunnable, timeout);
                }
            } else {
                Slog.e(TAG, "Unknown vibration type, ignoring");
            }
        } finally {
            Trace.traceEnd(8388608);
        }
    }

    private boolean isAllowedToVibrateLocked(Vibration vib) {
        if (!this.mLowPowerMode || vib.usageHint == 6 || vib.usageHint == 4 || vib.usageHint == 11 || vib.usageHint == 7) {
            return true;
        }
        return false;
    }

    private int getCurrentIntensityLocked(Vibration vib) {
        if (vib.isRingtone()) {
            return this.mRingIntensity;
        }
        if (vib.isNotification()) {
            return this.mNotificationIntensity;
        }
        if (vib.isHapticFeedback()) {
            return this.mHapticFeedbackIntensity;
        }
        if (vib.isAlarm()) {
            return 3;
        }
        return 2;
    }

    private void applyVibrationIntensityScalingLocked(Vibration vib, int intensity) {
        int defaultIntensity;
        if (vib.effect instanceof VibrationEffect.Prebaked) {
            vib.effect.setEffectStrength(intensityToEffectStrength(intensity));
            return;
        }
        if (vib.isRingtone()) {
            defaultIntensity = this.mVibrator.getDefaultRingVibrationIntensity();
        } else if (vib.isNotification() != 0) {
            defaultIntensity = this.mVibrator.getDefaultNotificationVibrationIntensity();
        } else if (vib.isHapticFeedback() != 0) {
            defaultIntensity = this.mVibrator.getDefaultHapticFeedbackIntensity();
        } else if (vib.isAlarm() != 0) {
            defaultIntensity = 3;
        } else {
            return;
        }
        ScaleLevel scale = this.mScaleLevels.get(intensity - defaultIntensity);
        if (scale == null) {
            Slog.e(TAG, "No configured scaling level! (current=" + intensity + ", default= " + defaultIntensity + ")");
            return;
        }
        VibrationEffect scaledEffect = null;
        if (vib.effect instanceof VibrationEffect.OneShot) {
            scaledEffect = vib.effect.resolve(this.mDefaultVibrationAmplitude).scale(scale.gamma, scale.maxAmplitude);
        } else if (vib.effect instanceof VibrationEffect.Waveform) {
            scaledEffect = vib.effect.resolve(this.mDefaultVibrationAmplitude).scale(scale.gamma, scale.maxAmplitude);
        } else {
            Slog.w(TAG, "Unable to apply intensity scaling, unknown VibrationEffect type");
        }
        if (scaledEffect != null) {
            vib.originalEffect = vib.effect;
            vib.effect = scaledEffect;
        }
    }

    private boolean shouldVibrateForRingtone() {
        int ringerMode = ((AudioManager) this.mContext.getSystemService(AudioManager.class)).getRingerModeInternal();
        if (Settings.System.getInt(this.mContext.getContentResolver(), "vibrate_when_ringing", 0) != 0) {
            if (ringerMode != 0) {
                return true;
            }
            return false;
        } else if (Settings.Global.getInt(this.mContext.getContentResolver(), "apply_ramping_ringer", 0) == 0 || !DeviceConfig.getBoolean("telephony", RAMPING_RINGER_ENABLED, false)) {
            if (ringerMode == 1) {
                return true;
            }
            return false;
        } else if (ringerMode != 0) {
            return true;
        } else {
            return false;
        }
    }

    private int getAppOpMode(Vibration vib) {
        int mode = this.mAppOps.checkAudioOpNoThrow(3, vib.usageHint, vib.uid, vib.opPkg);
        if (mode == 0) {
            return this.mAppOps.startOpNoThrow(3, vib.uid, vib.opPkg);
        }
        return mode;
    }

    @GuardedBy({"mLock"})
    private void reportFinishVibrationLocked() {
        Trace.traceBegin(8388608, "reportFinishVibrationLocked");
        try {
            if (this.mCurrentVibration != null) {
                this.mAppOps.finishOp(3, this.mCurrentVibration.uid, this.mCurrentVibration.opPkg);
                unlinkVibration(this.mCurrentVibration);
                this.mCurrentVibration = null;
            }
        } finally {
            Trace.traceEnd(8388608);
        }
    }

    private void linkVibration(Vibration vib) {
        if (vib.effect instanceof VibrationEffect.Waveform) {
            try {
                vib.token.linkToDeath(vib, 0);
            } catch (RemoteException e) {
            }
        }
    }

    private void unlinkVibration(Vibration vib) {
        if (vib.effect instanceof VibrationEffect.Waveform) {
            vib.token.unlinkToDeath(vib, 0);
        }
    }

    /* access modifiers changed from: private */
    public void updateVibrators() {
        synchronized (this.mLock) {
            boolean devicesUpdated = updateInputDeviceVibratorsLocked();
            boolean lowPowerModeUpdated = updateLowPowerModeLocked();
            updateVibrationIntensityLocked();
            if (devicesUpdated || lowPowerModeUpdated) {
                doCancelVibrateLocked();
            }
        }
    }

    private boolean updateInputDeviceVibratorsLocked() {
        boolean changed = false;
        boolean vibrateInputDevices = false;
        try {
            vibrateInputDevices = Settings.System.getIntForUser(this.mContext.getContentResolver(), "vibrate_input_devices", -2) > 0;
        } catch (Settings.SettingNotFoundException e) {
        }
        if (vibrateInputDevices != this.mVibrateInputDevicesSetting) {
            changed = true;
            this.mVibrateInputDevicesSetting = vibrateInputDevices;
        }
        if (this.mVibrateInputDevicesSetting) {
            if (!this.mInputDeviceListenerRegistered) {
                this.mInputDeviceListenerRegistered = true;
                this.mIm.registerInputDeviceListener(this, this.mH);
            }
        } else if (this.mInputDeviceListenerRegistered) {
            this.mInputDeviceListenerRegistered = false;
            this.mIm.unregisterInputDeviceListener(this);
        }
        this.mInputDeviceVibrators.clear();
        if (!this.mVibrateInputDevicesSetting) {
            return changed;
        }
        int[] ids = this.mIm.getInputDeviceIds();
        for (int inputDevice : ids) {
            Vibrator vibrator = this.mIm.getInputDevice(inputDevice).getVibrator();
            if (vibrator.hasVibrator()) {
                this.mInputDeviceVibrators.add(vibrator);
            }
        }
        return true;
    }

    private boolean updateLowPowerModeLocked() {
        boolean lowPowerMode = this.mPowerManagerInternal.getLowPowerState(2).batterySaverEnabled;
        if (lowPowerMode == this.mLowPowerMode) {
            return false;
        }
        this.mLowPowerMode = lowPowerMode;
        return true;
    }

    private void updateVibrationIntensityLocked() {
        this.mHapticFeedbackIntensity = Settings.System.getIntForUser(this.mContext.getContentResolver(), "haptic_feedback_intensity", this.mVibrator.getDefaultHapticFeedbackIntensity(), -2);
        this.mNotificationIntensity = Settings.System.getIntForUser(this.mContext.getContentResolver(), "notification_vibration_intensity", this.mVibrator.getDefaultNotificationVibrationIntensity(), -2);
        this.mRingIntensity = Settings.System.getIntForUser(this.mContext.getContentResolver(), "ring_vibration_intensity", this.mVibrator.getDefaultRingVibrationIntensity(), -2);
    }

    public void onInputDeviceAdded(int deviceId) {
        updateVibrators();
    }

    public void onInputDeviceChanged(int deviceId) {
        updateVibrators();
    }

    public void onInputDeviceRemoved(int deviceId) {
        updateVibrators();
    }

    private boolean doVibratorExists() {
        return vibratorExists();
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    /* access modifiers changed from: private */
    public void doVibratorOn(long millis, int amplitude, int uid, int usageHint) {
        Trace.traceBegin(8388608, "doVibratorOn");
        if (millis > JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY) {
            millis = 1000;
        }
        try {
            synchronized (this.mInputDeviceVibrators) {
                if (amplitude == -1) {
                    amplitude = this.mDefaultVibrationAmplitude;
                }
                noteVibratorOnLocked(uid, millis);
                int vibratorCount = this.mInputDeviceVibrators.size();
                if (vibratorCount != 0) {
                    AudioAttributes attributes = new AudioAttributes.Builder().setUsage(usageHint).build();
                    for (int i = 0; i < vibratorCount; i++) {
                        this.mInputDeviceVibrators.get(i).vibrate(millis, attributes);
                    }
                } else {
                    vibratorOn(VibratorServiceInjector.weakenVibrationIfNecessary(millis, uid));
                    doVibratorSetAmplitude(amplitude);
                }
            }
            Trace.traceEnd(8388608);
        } catch (Throwable th) {
            Trace.traceEnd(8388608);
            throw th;
        }
    }

    /* access modifiers changed from: private */
    public void doVibratorSetAmplitude(int amplitude) {
        if (this.mSupportsAmplitudeControl) {
            vibratorSetAmplitude(amplitude);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    private void doVibratorOff() {
        Trace.traceBegin(8388608, "doVibratorOff");
        try {
            synchronized (this.mInputDeviceVibrators) {
                noteVibratorOffLocked();
                int vibratorCount = this.mInputDeviceVibrators.size();
                if (vibratorCount != 0) {
                    for (int i = 0; i < vibratorCount; i++) {
                        this.mInputDeviceVibrators.get(i).cancel();
                    }
                } else {
                    vibratorOff();
                }
            }
            Trace.traceEnd(8388608);
        } catch (Throwable th) {
            Trace.traceEnd(8388608);
            throw th;
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 19 */
    @GuardedBy({"mLock"})
    private long doVibratorPrebakedEffectLocked(Vibration vib) {
        boolean usingInputDeviceVibrators;
        Vibration vibration = vib;
        Trace.traceBegin(8388608, "doVibratorPrebakedEffectLocked");
        try {
            VibrationEffect.Prebaked prebaked = vibration.effect;
            synchronized (this.mInputDeviceVibrators) {
                usingInputDeviceVibrators = !this.mInputDeviceVibrators.isEmpty();
            }
            if (!usingInputDeviceVibrators) {
                long timeout = vibratorPerformEffect((long) prebaked.getId(), (long) prebaked.getEffectStrength());
                if (timeout > 0) {
                    noteVibratorOnLocked(vibration.uid, timeout);
                    Trace.traceEnd(8388608);
                    return timeout;
                }
            }
            if (!prebaked.shouldFallback()) {
                Trace.traceEnd(8388608);
                return 0;
            }
            VibrationEffect effect = getFallbackEffect(prebaked.getId());
            if (effect == null) {
                Slog.w(TAG, "Failed to play prebaked effect, no fallback");
                Trace.traceEnd(8388608);
                return 0;
            }
            IBinder iBinder = vibration.token;
            int i = vibration.usageHint;
            int i2 = vibration.uid;
            String str = vibration.opPkg;
            Vibration fallbackVib = new Vibration(iBinder, effect, i, i2, str, vibration.reason + " (fallback)");
            int intensity = getCurrentIntensityLocked(fallbackVib);
            linkVibration(fallbackVib);
            applyVibrationIntensityScalingLocked(fallbackVib, intensity);
            startVibrationInnerLocked(fallbackVib);
            Trace.traceEnd(8388608);
            return 0;
        } catch (Throwable th) {
            Trace.traceEnd(8388608);
            throw th;
        }
    }

    private VibrationEffect getFallbackEffect(int effectId) {
        return this.mFallbackEffects.get(effectId);
    }

    private static int intensityToEffectStrength(int intensity) {
        if (intensity == 1) {
            return 0;
        }
        if (intensity == 2) {
            return 1;
        }
        if (intensity == 3) {
            return 2;
        }
        Slog.w(TAG, "Got unexpected vibration intensity: " + intensity);
        return 2;
    }

    /* access modifiers changed from: private */
    public static boolean isNotification(int usageHint) {
        if (usageHint == 5 || usageHint == 7 || usageHint == 8 || usageHint == 9) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public static boolean isRingtone(int usageHint) {
        return usageHint == 6;
    }

    /* access modifiers changed from: private */
    public static boolean isHapticFeedback(int usageHint) {
        return usageHint == 13;
    }

    /* access modifiers changed from: private */
    public static boolean isAlarm(int usageHint) {
        return usageHint == 4;
    }

    private void noteVibratorOnLocked(int uid, long millis) {
        try {
            this.mBatteryStatsService.noteVibratorOn(uid, millis);
            StatsLog.write_non_chained(84, uid, (String) null, 1, millis);
            this.mCurVibUid = uid;
        } catch (RemoteException e) {
        }
    }

    private void noteVibratorOffLocked() {
        int i = this.mCurVibUid;
        if (i >= 0) {
            try {
                this.mBatteryStatsService.noteVibratorOff(i);
                StatsLog.write_non_chained(84, this.mCurVibUid, (String) null, 0, 0);
            } catch (RemoteException e) {
            }
            this.mCurVibUid = -1;
        }
    }

    /* access modifiers changed from: private */
    public void setVibratorUnderExternalControl(boolean externalControl) {
        this.mVibratorUnderExternalControl = externalControl;
        vibratorSetExternalControl(externalControl);
    }

    private class VibrateThread extends Thread {
        private boolean mForceStop;
        private final int mUid;
        private final int mUsageHint;
        private final VibrationEffect.Waveform mWaveform;

        VibrateThread(VibrationEffect.Waveform waveform, int uid, int usageHint) {
            this.mWaveform = waveform;
            this.mUid = uid;
            this.mUsageHint = usageHint;
            VibratorService.this.mTmpWorkSource.set(uid);
            VibratorService.this.mWakeLock.setWorkSource(VibratorService.this.mTmpWorkSource);
        }

        VibrateThread(VibrationEffect.Waveform waveform, int uid, String opPkg, int usageHint) {
            this.mWaveform = waveform;
            this.mUid = uid;
            this.mUsageHint = usageHint;
            VibratorService.this.mTmpWorkSource.set(uid, opPkg);
            VibratorService.this.mWakeLock.setWorkSource(VibratorService.this.mTmpWorkSource);
        }

        private long delayLocked(long duration) {
            Trace.traceBegin(8388608, "delayLocked");
            long durationRemaining = duration;
            if (duration > 0) {
                try {
                    long bedtime = SystemClock.uptimeMillis() + duration;
                    while (true) {
                        try {
                            wait(durationRemaining);
                        } catch (InterruptedException e) {
                        }
                        if (!this.mForceStop) {
                            durationRemaining = bedtime - SystemClock.uptimeMillis();
                            if (durationRemaining <= 0) {
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                    return duration - durationRemaining;
                } finally {
                    Trace.traceEnd(8388608);
                }
            } else {
                Trace.traceEnd(8388608);
                return 0;
            }
        }

        public void run() {
            Process.setThreadPriority(-8);
            VibratorService.this.mWakeLock.acquire();
            try {
                if (playWaveform()) {
                    VibratorService.this.onVibrationFinished();
                }
            } finally {
                VibratorService.this.mWakeLock.release();
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 20 */
        public boolean playWaveform() {
            boolean z;
            long duration;
            Trace.traceBegin(8388608, "playWaveform");
            try {
                synchronized (this) {
                    long[] timings = this.mWaveform.getTimings();
                    int[] amplitudes = this.mWaveform.getAmplitudes();
                    int len = timings.length;
                    int repeat = this.mWaveform.getRepeatIndex();
                    int index = 0;
                    long j = 0;
                    long onDuration = 0;
                    while (true) {
                        if (this.mForceStop) {
                            break;
                        } else if (index < len) {
                            int amplitude = amplitudes[index];
                            int index2 = index + 1;
                            long duration2 = timings[index];
                            if (duration2 <= j) {
                                index = index2;
                            } else {
                                if (amplitude == 0) {
                                    duration = duration2;
                                } else if (onDuration <= j) {
                                    duration = duration2;
                                    long onDuration2 = getTotalOnDuration(timings, amplitudes, index2 - 1, repeat);
                                    VibratorService.this.doVibratorOn(onDuration2, amplitude, this.mUid, this.mUsageHint);
                                    onDuration = onDuration2;
                                } else {
                                    duration = duration2;
                                    VibratorService.this.doVibratorSetAmplitude(amplitude);
                                }
                                long waitTime = delayLocked(duration);
                                if (amplitude != 0) {
                                    onDuration -= waitTime;
                                }
                                index = index2;
                                j = 0;
                            }
                        } else if (repeat < 0) {
                            break;
                        } else {
                            index = repeat;
                            j = 0;
                        }
                    }
                    z = !this.mForceStop;
                }
                Trace.traceEnd(8388608);
                return z;
            } catch (Throwable th) {
                Trace.traceEnd(8388608);
                throw th;
            }
        }

        public void cancel() {
            synchronized (this) {
                VibratorService.this.mThread.mForceStop = true;
                VibratorService.this.mThread.notify();
            }
        }

        private long getTotalOnDuration(long[] timings, int[] amplitudes, int startIndex, int repeatIndex) {
            int i = startIndex;
            long timing = 0;
            do {
                if (amplitudes[i] != 0) {
                    int i2 = i + 1;
                    timing += timings[i];
                    if (i2 < timings.length) {
                        i = i2;
                        continue;
                    } else if (repeatIndex >= 0) {
                        i = repeatIndex;
                        repeatIndex = -1;
                        continue;
                    }
                }
                return timing;
            } while (i != startIndex);
            return 1000;
        }
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (DumpUtils.checkDumpPermission(this.mContext, TAG, pw)) {
            pw.println("Vibrator Service:");
            synchronized (this.mLock) {
                pw.print("  mCurrentVibration=");
                if (this.mCurrentVibration != null) {
                    pw.println(this.mCurrentVibration.toInfo().toString());
                } else {
                    pw.println("null");
                }
                pw.print("  mCurrentExternalVibration=");
                if (this.mCurrentExternalVibration != null) {
                    pw.println(this.mCurrentExternalVibration.toString());
                } else {
                    pw.println("null");
                }
                pw.println("  mVibratorUnderExternalControl=" + this.mVibratorUnderExternalControl);
                pw.println("  mLowPowerMode=" + this.mLowPowerMode);
                pw.println("  mHapticFeedbackIntensity=" + this.mHapticFeedbackIntensity);
                pw.println("  mNotificationIntensity=" + this.mNotificationIntensity);
                pw.println("  mRingIntensity=" + this.mRingIntensity);
                pw.println("");
                pw.println("  Previous ring vibrations:");
                Iterator it = this.mPreviousRingVibrations.iterator();
                while (it.hasNext()) {
                    pw.print("    ");
                    pw.println(((VibrationInfo) it.next()).toString());
                }
                pw.println("  Previous notification vibrations:");
                Iterator it2 = this.mPreviousNotificationVibrations.iterator();
                while (it2.hasNext()) {
                    pw.print("    ");
                    pw.println(((VibrationInfo) it2.next()).toString());
                }
                pw.println("  Previous alarm vibrations:");
                Iterator it3 = this.mPreviousAlarmVibrations.iterator();
                while (it3.hasNext()) {
                    pw.print("    ");
                    pw.println(((VibrationInfo) it3.next()).toString());
                }
                pw.println("  Previous vibrations:");
                Iterator it4 = this.mPreviousVibrations.iterator();
                while (it4.hasNext()) {
                    pw.print("    ");
                    pw.println(((VibrationInfo) it4.next()).toString());
                }
                pw.println("  Previous external vibrations:");
                Iterator it5 = this.mPreviousExternalVibrations.iterator();
                while (it5.hasNext()) {
                    pw.print("    ");
                    pw.println(((ExternalVibration) it5.next()).toString());
                }
            }
            VibratorServiceInjector.dumpVibrations(fd, pw, args);
        }
    }

    /* JADX WARNING: type inference failed for: r8v0, types: [android.os.IBinder, com.android.server.VibratorService] */
    public void onShellCommand(FileDescriptor in, FileDescriptor out, FileDescriptor err, String[] args, ShellCallback callback, ResultReceiver resultReceiver) throws RemoteException {
        new VibratorShellCommand(this).exec(this, in, out, err, args, callback, resultReceiver);
    }

    final class ExternalVibratorService extends IExternalVibratorService.Stub {
        ExternalVibrationDeathRecipient mCurrentExternalDeathRecipient;

        ExternalVibratorService() {
        }

        public int onExternalVibrationStart(ExternalVibration vib) {
            int currentIntensity;
            int defaultIntensity;
            int currentIntensity2;
            if (!VibratorService.this.mSupportsExternalControl) {
                return -100;
            }
            if (ActivityManager.checkComponentPermission("android.permission.VIBRATE", vib.getUid(), -1, true) != 0) {
                Slog.w(VibratorService.TAG, "pkg=" + vib.getPackage() + ", uid=" + vib.getUid() + " tried to play externally controlled vibration without VIBRATE permission, ignoring.");
                return -100;
            }
            synchronized (VibratorService.this.mLock) {
                if (!vib.equals(VibratorService.this.mCurrentExternalVibration)) {
                    if (VibratorService.this.mCurrentExternalVibration == null) {
                        VibratorService.this.doCancelVibrateLocked();
                        VibratorService.this.setVibratorUnderExternalControl(true);
                    }
                    ExternalVibration unused = VibratorService.this.mCurrentExternalVibration = vib;
                    this.mCurrentExternalDeathRecipient = new ExternalVibrationDeathRecipient();
                    VibratorService.this.mCurrentExternalVibration.linkToDeath(this.mCurrentExternalDeathRecipient);
                    if (VibratorService.this.mPreviousExternalVibrations.size() > VibratorService.this.mPreviousVibrationsLimit) {
                        VibratorService.this.mPreviousExternalVibrations.removeFirst();
                    }
                    VibratorService.this.mPreviousExternalVibrations.addLast(vib);
                }
                int usage = vib.getAudioAttributes().getUsage();
                if (VibratorService.isRingtone(usage)) {
                    defaultIntensity = VibratorService.this.mVibrator.getDefaultRingVibrationIntensity();
                    currentIntensity = VibratorService.this.mRingIntensity;
                } else if (VibratorService.isNotification(usage) != 0) {
                    defaultIntensity = VibratorService.this.mVibrator.getDefaultNotificationVibrationIntensity();
                    currentIntensity = VibratorService.this.mNotificationIntensity;
                } else if (VibratorService.isHapticFeedback(usage) != 0) {
                    defaultIntensity = VibratorService.this.mVibrator.getDefaultHapticFeedbackIntensity();
                    currentIntensity = VibratorService.this.mHapticFeedbackIntensity;
                } else if (VibratorService.isAlarm(usage) != 0) {
                    defaultIntensity = 3;
                    currentIntensity = 3;
                } else {
                    defaultIntensity = 0;
                    currentIntensity = 0;
                }
                currentIntensity2 = currentIntensity - defaultIntensity;
            }
            if (currentIntensity2 >= -2 && currentIntensity2 <= 2) {
                return currentIntensity2;
            }
            Slog.w(VibratorService.TAG, "Error in scaling calculations, ended up with invalid scale level " + currentIntensity2 + " for vibration " + vib);
            return 0;
        }

        public void onExternalVibrationStop(ExternalVibration vib) {
            synchronized (VibratorService.this.mLock) {
                if (vib.equals(VibratorService.this.mCurrentExternalVibration)) {
                    VibratorService.this.mCurrentExternalVibration.unlinkToDeath(this.mCurrentExternalDeathRecipient);
                    this.mCurrentExternalDeathRecipient = null;
                    ExternalVibration unused = VibratorService.this.mCurrentExternalVibration = null;
                    VibratorService.this.setVibratorUnderExternalControl(false);
                }
            }
        }

        private class ExternalVibrationDeathRecipient implements IBinder.DeathRecipient {
            private ExternalVibrationDeathRecipient() {
            }

            public void binderDied() {
                synchronized (VibratorService.this.mLock) {
                    ExternalVibratorService.this.onExternalVibrationStop(VibratorService.this.mCurrentExternalVibration);
                }
            }
        }
    }

    private final class VibratorShellCommand extends ShellCommand {
        private final IBinder mToken;

        private final class CommonOptions {
            public boolean force;

            private CommonOptions() {
                this.force = false;
            }

            public void check(String opt) {
                if (((opt.hashCode() == 1497 && opt.equals("-f")) ? (char) 0 : 65535) == 0) {
                    this.force = true;
                }
            }
        }

        private VibratorShellCommand(IBinder token) {
            this.mToken = token;
        }

        public int onCommand(String cmd) {
            if ("vibrate".equals(cmd)) {
                return runVibrate();
            }
            if ("waveform".equals(cmd)) {
                return runWaveform();
            }
            if ("prebaked".equals(cmd)) {
                return runPrebaked();
            }
            if (!"cancel".equals(cmd)) {
                return handleDefaultCommands(cmd);
            }
            VibratorService.this.cancelVibrate(this.mToken);
            return 0;
        }

        /* Debug info: failed to restart local var, previous not found, register: 5 */
        /* JADX WARNING: Code restructure failed: missing block: B:16:0x0033, code lost:
            r3 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x0034, code lost:
            if (r1 != null) goto L_0x0036;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:?, code lost:
            $closeResource(r2, r1);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x0039, code lost:
            throw r3;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private boolean checkDoNotDisturb(com.android.server.VibratorService.VibratorShellCommand.CommonOptions r6) {
            /*
                r5 = this;
                com.android.server.VibratorService r0 = com.android.server.VibratorService.this     // Catch:{ SettingNotFoundException -> 0x003b }
                android.content.Context r0 = r0.mContext     // Catch:{ SettingNotFoundException -> 0x003b }
                android.content.ContentResolver r0 = r0.getContentResolver()     // Catch:{ SettingNotFoundException -> 0x003b }
                java.lang.String r1 = "zen_mode"
                int r0 = android.provider.Settings.Global.getInt(r0, r1)     // Catch:{ SettingNotFoundException -> 0x003b }
                if (r0 == 0) goto L_0x003a
                boolean r1 = r6.force     // Catch:{ SettingNotFoundException -> 0x003b }
                if (r1 != 0) goto L_0x003a
                java.io.PrintWriter r1 = r5.getOutPrintWriter()     // Catch:{ SettingNotFoundException -> 0x003b }
                r2 = 0
                java.lang.String r3 = "Ignoring because device is on DND mode "
                r1.print(r3)     // Catch:{ all -> 0x0031 }
                java.lang.Class<android.provider.Settings$Global> r3 = android.provider.Settings.Global.class
                java.lang.String r4 = "ZEN_MODE_"
                java.lang.String r3 = android.util.DebugUtils.flagsToString(r3, r4, r0)     // Catch:{ all -> 0x0031 }
                r1.println(r3)     // Catch:{ all -> 0x0031 }
                r3 = 1
                $closeResource(r2, r1)     // Catch:{ SettingNotFoundException -> 0x003b }
                return r3
            L_0x0031:
                r2 = move-exception
                throw r2     // Catch:{ all -> 0x0033 }
            L_0x0033:
                r3 = move-exception
                if (r1 == 0) goto L_0x0039
                $closeResource(r2, r1)     // Catch:{ SettingNotFoundException -> 0x003b }
            L_0x0039:
                throw r3     // Catch:{ SettingNotFoundException -> 0x003b }
            L_0x003a:
                goto L_0x003c
            L_0x003b:
                r0 = move-exception
            L_0x003c:
                r0 = 0
                return r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.VibratorService.VibratorShellCommand.checkDoNotDisturb(com.android.server.VibratorService$VibratorShellCommand$CommonOptions):boolean");
        }

        private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
            if (x0 != null) {
                try {
                    x1.close();
                } catch (Throwable th) {
                    x0.addSuppressed(th);
                }
            } else {
                x1.close();
            }
        }

        private int runVibrate() {
            Trace.traceBegin(8388608, "runVibrate");
            try {
                CommonOptions commonOptions = new CommonOptions();
                while (true) {
                    String nextOption = getNextOption();
                    String opt = nextOption;
                    if (nextOption == null) {
                        break;
                    }
                    commonOptions.check(opt);
                }
                if (checkDoNotDisturb(commonOptions)) {
                    return 0;
                }
                long duration = Long.parseLong(getNextArgRequired());
                String description = getNextArg();
                if (description == null) {
                    description = NotificationShellCmd.CHANNEL_NAME;
                }
                String str = description;
                VibratorService.this.vibrate(Binder.getCallingUid(), str, VibrationEffect.createOneShot(duration, -1), 0, "Shell Command", this.mToken);
                Trace.traceEnd(8388608);
                return 0;
            } finally {
                Trace.traceEnd(8388608);
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:23:0x0051 A[Catch:{ all -> 0x00fe }] */
        /* JADX WARNING: Removed duplicated region for block: B:29:0x006c A[Catch:{ all -> 0x00fe }] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private int runWaveform() {
            /*
                r21 = this;
                r1 = r21
                r2 = 8388608(0x800000, double:4.144523E-317)
                java.lang.String r0 = "runWaveform"
                android.os.Trace.traceBegin(r2, r0)
                java.lang.String r0 = "Shell command"
                r4 = -1
                r5 = 0
                com.android.server.VibratorService$VibratorShellCommand$CommonOptions r6 = new com.android.server.VibratorService$VibratorShellCommand$CommonOptions     // Catch:{ all -> 0x00fe }
                r7 = 0
                r6.<init>()     // Catch:{ all -> 0x00fe }
            L_0x0015:
                java.lang.String r7 = r21.getNextOption()     // Catch:{ all -> 0x00fe }
                r14 = r7
                r15 = 0
                if (r7 == 0) goto L_0x0073
                r7 = -1
                int r8 = r14.hashCode()     // Catch:{ all -> 0x00fe }
                r9 = 1492(0x5d4, float:2.091E-42)
                r10 = 2
                r11 = 1
                if (r8 == r9) goto L_0x0044
                r9 = 1495(0x5d7, float:2.095E-42)
                if (r8 == r9) goto L_0x003b
                r9 = 1509(0x5e5, float:2.115E-42)
                if (r8 == r9) goto L_0x0031
            L_0x0030:
                goto L_0x004e
            L_0x0031:
                java.lang.String r8 = "-r"
                boolean r8 = r14.equals(r8)     // Catch:{ all -> 0x00fe }
                if (r8 == 0) goto L_0x0030
                r15 = r11
                goto L_0x004f
            L_0x003b:
                java.lang.String r8 = "-d"
                boolean r8 = r14.equals(r8)     // Catch:{ all -> 0x00fe }
                if (r8 == 0) goto L_0x0030
                goto L_0x004f
            L_0x0044:
                java.lang.String r8 = "-a"
                boolean r8 = r14.equals(r8)     // Catch:{ all -> 0x00fe }
                if (r8 == 0) goto L_0x0030
                r15 = r10
                goto L_0x004f
            L_0x004e:
                r15 = r7
            L_0x004f:
                if (r15 == 0) goto L_0x006c
                if (r15 == r11) goto L_0x0062
                if (r15 == r10) goto L_0x0059
                r6.check(r14)     // Catch:{ all -> 0x00fe }
                goto L_0x0072
            L_0x0059:
                if (r5 != 0) goto L_0x0072
                java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ all -> 0x00fe }
                r7.<init>()     // Catch:{ all -> 0x00fe }
                r5 = r7
                goto L_0x0072
            L_0x0062:
                java.lang.String r7 = r21.getNextArgRequired()     // Catch:{ all -> 0x00fe }
                int r7 = java.lang.Integer.parseInt(r7)     // Catch:{ all -> 0x00fe }
                r4 = r7
                goto L_0x0072
            L_0x006c:
                java.lang.String r7 = r21.getNextArgRequired()     // Catch:{ all -> 0x00fe }
                r0 = r7
            L_0x0072:
                goto L_0x0015
            L_0x0073:
                boolean r7 = r1.checkDoNotDisturb(r6)     // Catch:{ all -> 0x00fe }
                if (r7 == 0) goto L_0x007e
                android.os.Trace.traceEnd(r2)
                return r15
            L_0x007e:
                java.util.ArrayList r7 = new java.util.ArrayList     // Catch:{ all -> 0x00fe }
                r7.<init>()     // Catch:{ all -> 0x00fe }
                r13 = r7
            L_0x0084:
                java.lang.String r7 = r21.getNextArg()     // Catch:{ all -> 0x00fe }
                r16 = r7
                if (r7 == 0) goto L_0x00b0
                if (r5 == 0) goto L_0x00a4
                int r7 = r5.size()     // Catch:{ all -> 0x00fe }
                int r8 = r13.size()     // Catch:{ all -> 0x00fe }
                if (r7 >= r8) goto L_0x00a4
                int r7 = java.lang.Integer.parseInt(r16)     // Catch:{ all -> 0x00fe }
                java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ all -> 0x00fe }
                r5.add(r7)     // Catch:{ all -> 0x00fe }
                goto L_0x0084
            L_0x00a4:
                long r7 = java.lang.Long.parseLong(r16)     // Catch:{ all -> 0x00fe }
                java.lang.Long r7 = java.lang.Long.valueOf(r7)     // Catch:{ all -> 0x00fe }
                r13.add(r7)     // Catch:{ all -> 0x00fe }
                goto L_0x0084
            L_0x00b0:
                java.util.stream.Stream r7 = r13.stream()     // Catch:{ all -> 0x00fe }
                com.android.server.-$$Lambda$ELHKvd8JMVRD8rbALqYPKbDX2mM r8 = com.android.server.$$Lambda$ELHKvd8JMVRD8rbALqYPKbDX2mM.INSTANCE     // Catch:{ all -> 0x00fe }
                java.util.stream.LongStream r7 = r7.mapToLong(r8)     // Catch:{ all -> 0x00fe }
                long[] r7 = r7.toArray()     // Catch:{ all -> 0x00fe }
                r12 = r7
                if (r5 != 0) goto L_0x00c8
                android.os.VibrationEffect r7 = android.os.VibrationEffect.createWaveform(r12, r4)     // Catch:{ all -> 0x00fe }
                r17 = r7
                goto L_0x00de
            L_0x00c8:
                java.util.stream.Stream r7 = r5.stream()     // Catch:{ all -> 0x00fe }
                com.android.server.-$$Lambda$UV1wDVoVlbcxpr8zevj_aMFtUGw r8 = com.android.server.$$Lambda$UV1wDVoVlbcxpr8zevj_aMFtUGw.INSTANCE     // Catch:{ all -> 0x00fe }
                java.util.stream.IntStream r7 = r7.mapToInt(r8)     // Catch:{ all -> 0x00fe }
                int[] r7 = r7.toArray()     // Catch:{ all -> 0x00fe }
                android.os.VibrationEffect r8 = android.os.VibrationEffect.createWaveform(r12, r7, r4)     // Catch:{ all -> 0x00fe }
                r7 = r8
                r17 = r7
            L_0x00de:
                com.android.server.VibratorService r7 = com.android.server.VibratorService.this     // Catch:{ all -> 0x00fe }
                int r8 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x00fe }
                r11 = 0
                java.lang.String r18 = "Shell Command"
                android.os.IBinder r10 = r1.mToken     // Catch:{ all -> 0x00fe }
                r9 = r0
                r19 = r10
                r10 = r17
                r20 = r12
                r12 = r18
                r18 = r13
                r13 = r19
                r7.vibrate(r8, r9, r10, r11, r12, r13)     // Catch:{ all -> 0x00fe }
                android.os.Trace.traceEnd(r2)
                return r15
            L_0x00fe:
                r0 = move-exception
                android.os.Trace.traceEnd(r2)
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.VibratorService.VibratorShellCommand.runWaveform():int");
        }

        /*  JADX ERROR: NullPointerException in pass: CodeShrinkVisitor
            java.lang.NullPointerException
            	at jadx.core.dex.instructions.args.InsnArg.wrapInstruction(InsnArg.java:118)
            	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.inline(CodeShrinkVisitor.java:146)
            	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkBlock(CodeShrinkVisitor.java:71)
            	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.shrinkMethod(CodeShrinkVisitor.java:43)
            	at jadx.core.dex.visitors.shrink.CodeShrinkVisitor.visit(CodeShrinkVisitor.java:35)
            */
        private int runPrebaked() {
            /*
                r14 = this;
                r0 = 8388608(0x800000, double:4.144523E-317)
                java.lang.String r2 = "runPrebaked"
                android.os.Trace.traceBegin(r0, r2)
                com.android.server.VibratorService$VibratorShellCommand$CommonOptions r2 = new com.android.server.VibratorService$VibratorShellCommand$CommonOptions     // Catch:{ all -> 0x0053 }
                r3 = 0
                r2.<init>()     // Catch:{ all -> 0x0053 }
            L_0x000f:
                java.lang.String r3 = r14.getNextOption()     // Catch:{ all -> 0x0053 }
                r4 = r3
                if (r3 == 0) goto L_0x001a
                r2.check(r4)     // Catch:{ all -> 0x0053 }
                goto L_0x000f
            L_0x001a:
                boolean r3 = r14.checkDoNotDisturb(r2)     // Catch:{ all -> 0x0053 }
                r5 = 0
                if (r3 == 0) goto L_0x0026
                android.os.Trace.traceEnd(r0)
                return r5
            L_0x0026:
                java.lang.String r3 = r14.getNextArgRequired()     // Catch:{ all -> 0x0053 }
                int r3 = java.lang.Integer.parseInt(r3)     // Catch:{ all -> 0x0053 }
                java.lang.String r6 = r14.getNextArg()     // Catch:{ all -> 0x0053 }
                if (r6 != 0) goto L_0x0039
                java.lang.String r7 = "Shell command"
                r6 = r7
                r13 = r6
                goto L_0x003a
            L_0x0039:
                r13 = r6
            L_0x003a:
                android.os.VibrationEffect r9 = android.os.VibrationEffect.get(r3, r5)     // Catch:{ all -> 0x0053 }
                com.android.server.VibratorService r6 = com.android.server.VibratorService.this     // Catch:{ all -> 0x0053 }
                int r7 = android.os.Binder.getCallingUid()     // Catch:{ all -> 0x0053 }
                r10 = 0
                java.lang.String r11 = "Shell Command"
                android.os.IBinder r12 = r14.mToken     // Catch:{ all -> 0x0053 }
                r8 = r13
                r6.vibrate(r7, r8, r9, r10, r11, r12)     // Catch:{ all -> 0x0053 }
                android.os.Trace.traceEnd(r0)
                return r5
            L_0x0053:
                r2 = move-exception
                android.os.Trace.traceEnd(r0)
                throw r2
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.VibratorService.VibratorShellCommand.runPrebaked():int");
        }

        /* Debug info: failed to restart local var, previous not found, register: 4 */
        /* JADX WARNING: Code restructure failed: missing block: B:10:0x007d, code lost:
            $closeResource(r0, r2);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0080, code lost:
            throw r1;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:8:0x007a, code lost:
            r1 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x007b, code lost:
            if (r2 != null) goto L_0x007d;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onHelp() {
            /*
                r4 = this;
                java.lang.String r0 = "    (Do Not Disturb) mode."
                java.lang.String r1 = ""
                java.io.PrintWriter r2 = r4.getOutPrintWriter()
                java.lang.String r3 = "Vibrator commands:"
                r2.println(r3)     // Catch:{ all -> 0x0078 }
                java.lang.String r3 = "  help"
                r2.println(r3)     // Catch:{ all -> 0x0078 }
                java.lang.String r3 = "    Prints this help text."
                r2.println(r3)     // Catch:{ all -> 0x0078 }
                r2.println(r1)     // Catch:{ all -> 0x0078 }
                java.lang.String r3 = "  vibrate duration [description]"
                r2.println(r3)     // Catch:{ all -> 0x0078 }
                java.lang.String r3 = "    Vibrates for duration milliseconds; ignored when device is on DND "
                r2.println(r3)     // Catch:{ all -> 0x0078 }
                r2.println(r0)     // Catch:{ all -> 0x0078 }
                java.lang.String r3 = "  waveform [-d description] [-r index] [-a] duration [amplitude] ..."
                r2.println(r3)     // Catch:{ all -> 0x0078 }
                java.lang.String r3 = "    Vibrates for durations and amplitudes in list;"
                r2.println(r3)     // Catch:{ all -> 0x0078 }
                java.lang.String r3 = "    ignored when device is on DND (Do Not Disturb) mode."
                r2.println(r3)     // Catch:{ all -> 0x0078 }
                java.lang.String r3 = "    If -r is provided, the waveform loops back to the specified"
                r2.println(r3)     // Catch:{ all -> 0x0078 }
                java.lang.String r3 = "    index (e.g. 0 loops from the beginning)"
                r2.println(r3)     // Catch:{ all -> 0x0078 }
                java.lang.String r3 = "    If -a is provided, the command accepts duration-amplitude pairs;"
                r2.println(r3)     // Catch:{ all -> 0x0078 }
                java.lang.String r3 = "    otherwise, it accepts durations only and alternates off/on"
                r2.println(r3)     // Catch:{ all -> 0x0078 }
                java.lang.String r3 = "    Duration is in milliseconds; amplitude is a scale of 1-255."
                r2.println(r3)     // Catch:{ all -> 0x0078 }
                java.lang.String r3 = "  prebaked effect-id [description]"
                r2.println(r3)     // Catch:{ all -> 0x0078 }
                java.lang.String r3 = "    Vibrates with prebaked effect; ignored when device is on DND "
                r2.println(r3)     // Catch:{ all -> 0x0078 }
                r2.println(r0)     // Catch:{ all -> 0x0078 }
                java.lang.String r0 = "  cancel"
                r2.println(r0)     // Catch:{ all -> 0x0078 }
                java.lang.String r0 = "    Cancels any active vibration"
                r2.println(r0)     // Catch:{ all -> 0x0078 }
                java.lang.String r0 = "Common Options:"
                r2.println(r0)     // Catch:{ all -> 0x0078 }
                java.lang.String r0 = "  -f - Force. Ignore Do Not Disturb setting."
                r2.println(r0)     // Catch:{ all -> 0x0078 }
                r2.println(r1)     // Catch:{ all -> 0x0078 }
                r0 = 0
                $closeResource(r0, r2)
                return
            L_0x0078:
                r0 = move-exception
                throw r0     // Catch:{ all -> 0x007a }
            L_0x007a:
                r1 = move-exception
                if (r2 == 0) goto L_0x0080
                $closeResource(r0, r2)
            L_0x0080:
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.VibratorService.VibratorShellCommand.onHelp():void");
        }
    }
}
