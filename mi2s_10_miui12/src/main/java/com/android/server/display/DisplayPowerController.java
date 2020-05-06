package com.android.server.display;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ParceledListSlice;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.display.AmbientBrightnessDayStats;
import android.hardware.display.BrightnessChangeEvent;
import android.hardware.display.BrightnessConfiguration;
import android.hardware.display.DisplayManagerInternal;
import android.metrics.LogMaker;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.MathUtils;
import android.util.Slog;
import android.util.TimeUtils;
import android.view.Display;
import com.android.internal.app.IBatteryStats;
import com.android.internal.logging.MetricsLogger;
import com.android.server.LocalServices;
import com.android.server.ScreenOnMonitor;
import com.android.server.am.BatteryStatsService;
import com.android.server.am.ProcessPolicy;
import com.android.server.display.AutomaticBrightnessController;
import com.android.server.display.BrightnessTracker;
import com.android.server.display.DisplayPowerControllerInjector;
import com.android.server.display.RampAnimator;
import com.android.server.display.whitebalance.DisplayWhiteBalanceController;
import com.android.server.display.whitebalance.DisplayWhiteBalanceFactory;
import com.android.server.display.whitebalance.DisplayWhiteBalanceSettings;
import com.android.server.policy.WindowManagerPolicy;
import java.io.PrintWriter;
import java.util.List;

final class DisplayPowerController implements AutomaticBrightnessController.Callbacks, DisplayWhiteBalanceController.Callbacks {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    static final int COLOR_FADE_OFF_ANIMATION_DURATION_MILLIS = 300;
    private static final int COLOR_FADE_ON_ANIMATION_DURATION_MILLIS = 250;
    private static boolean DEBUG = false;
    private static final boolean DEBUG_PRETEND_PROXIMITY_SENSOR_ABSENT = false;
    private static final int MSG_CONFIGURE_BRIGHTNESS = 5;
    private static final int MSG_ON_USER_SWITCH = 8;
    private static final int MSG_PROXIMITY_SENSOR_DEBOUNCED = 2;
    private static final int MSG_RESET_SHORT_MODEL = 255;
    private static final int MSG_SCREEN_OFF_UNBLOCKED = 4;
    private static final int MSG_SCREEN_ON_UNBLOCKED = 3;
    private static final int MSG_SET_TEMPORARY_AUTO_BRIGHTNESS_ADJUSTMENT = 7;
    private static final int MSG_SET_TEMPORARY_BRIGHTNESS = 6;
    private static final int MSG_UPDATE_POWER_STATE = 1;
    private static final int PROXIMITY_NEGATIVE = 0;
    private static final int PROXIMITY_POSITIVE = 1;
    private static final int PROXIMITY_SENSOR_NEGATIVE_DEBOUNCE_DELAY = 0;
    private static final int PROXIMITY_SENSOR_POSITIVE_DEBOUNCE_DELAY = 0;
    private static final int PROXIMITY_UNKNOWN = -1;
    private static final int RAMP_STATE_SKIP_AUTOBRIGHT = 2;
    private static final int RAMP_STATE_SKIP_INITIAL = 1;
    private static final int RAMP_STATE_SKIP_NONE = 0;
    private static final int REPORTED_TO_POLICY_SCREEN_OFF = 0;
    private static final int REPORTED_TO_POLICY_SCREEN_ON = 2;
    private static final int REPORTED_TO_POLICY_SCREEN_TURNING_OFF = 3;
    private static final int REPORTED_TO_POLICY_SCREEN_TURNING_ON = 1;
    private static final int SCREEN_DIM_MINIMUM_REDUCTION = 10;
    private static final String SCREEN_OFF_BLOCKED_TRACE_NAME = "Screen off blocked";
    private static final String SCREEN_ON_BLOCKED_TRACE_NAME = "Screen on blocked";
    private static final String TAG = "DisplayPowerController";
    private static final float TYPICAL_PROXIMITY_THRESHOLD = 5.0f;
    private static final boolean USE_COLOR_FADE_ON_ANIMATION = false;
    private final boolean mAllowAutoBrightnessWhileDozingConfig;
    private final Animator.AnimatorListener mAnimatorListener = new Animator.AnimatorListener() {
        public void onAnimationStart(Animator animation) {
        }

        public void onAnimationEnd(Animator animation) {
            DisplayPowerController.this.sendUpdatePowerState();
        }

        public void onAnimationRepeat(Animator animation) {
        }

        public void onAnimationCancel(Animator animation) {
        }
    };
    private boolean mAppliedAutoBrightness;
    private boolean mAppliedBrightnessBoost;
    private boolean mAppliedDimming;
    private boolean mAppliedLowPower;
    private boolean mAppliedScreenBrightnessOverride;
    private boolean mAppliedTemporaryAutoBrightnessAdjustment;
    private boolean mAppliedTemporaryBrightness;
    private float mAutoBrightnessAdjustment;
    /* access modifiers changed from: private */
    public AutomaticBrightnessController mAutomaticBrightnessController;
    private final IBatteryStats mBatteryStats;
    private final DisplayBlanker mBlanker;
    private boolean mBrightnessBucketsInDozeConfig;
    /* access modifiers changed from: private */
    public BrightnessConfiguration mBrightnessConfiguration;
    private BrightnessMappingStrategy mBrightnessMapper;
    private final int mBrightnessRampRateFast;
    private int mBrightnessRampRateSlow;
    private BrightnessReason mBrightnessReason = new BrightnessReason();
    private BrightnessReason mBrightnessReasonTemp = new BrightnessReason();
    /* access modifiers changed from: private */
    public final BrightnessTracker mBrightnessTracker;
    /* access modifiers changed from: private */
    public final DisplayManagerInternal.DisplayPowerCallbacks mCallbacks;
    private final Runnable mCleanListener = new Runnable() {
        public void run() {
            DisplayPowerController.this.sendUpdatePowerState();
        }
    };
    private final boolean mColorFadeEnabled;
    private boolean mColorFadeFadesConfig;
    ObjectAnimator mColorFadeOffAnimator;
    private ObjectAnimator mColorFadeOnAnimator;
    private final Context mContext;
    private int mCurrentScreenBrightnessSetting;
    private boolean mDisplayBlanksAfterDozeConfig;
    private boolean mDisplayReadyLocked;
    private final DisplayWhiteBalanceController mDisplayWhiteBalanceController;
    private final DisplayWhiteBalanceSettings mDisplayWhiteBalanceSettings;
    private boolean mDozing;
    /* access modifiers changed from: private */
    public final DisplayControllerHandler mHandler;
    private int mInitialAutoBrightness;
    /* access modifiers changed from: private */
    public DisplayPowerControllerInjector mInjector;
    private int mLastUserSetScreenBrightness;
    private final Object mLock = new Object();
    private final Runnable mOnProximityNegativeRunnable = new Runnable() {
        public void run() {
            DisplayPowerController.this.mCallbacks.onProximityNegative();
            DisplayPowerController.this.mCallbacks.releaseSuspendBlocker();
        }
    };
    private final Runnable mOnProximityPositiveRunnable = new Runnable() {
        public void run() {
            DisplayPowerController.this.mCallbacks.onProximityPositive();
            DisplayPowerController.this.mCallbacks.releaseSuspendBlocker();
        }
    };
    private final Runnable mOnStateChangedRunnable = new Runnable() {
        public void run() {
            DisplayPowerController.this.mCallbacks.onStateChanged();
            DisplayPowerController.this.mCallbacks.releaseSuspendBlocker();
        }
    };
    private float mPendingAutoBrightnessAdjustment;
    private int mPendingProximity = -1;
    private long mPendingProximityDebounceTime = -1;
    private boolean mPendingRequestChangedLocked;
    private DisplayManagerInternal.DisplayPowerRequest mPendingRequestLocked;
    private int mPendingScreenBrightnessSetting;
    private boolean mPendingScreenOff;
    /* access modifiers changed from: private */
    public ScreenOffUnblocker mPendingScreenOffUnblocker;
    /* access modifiers changed from: private */
    public ScreenOnUnblocker mPendingScreenOnUnblocker;
    private boolean mPendingUpdatePowerStateLocked;
    private boolean mPendingWaitForNegativeProximityLocked;
    private DisplayManagerInternal.DisplayPowerRequest mPowerRequest;
    private DisplayPowerState mPowerState;
    private int mProximity = -1;
    private Sensor mProximitySensor;
    /* access modifiers changed from: private */
    public boolean mProximitySensorEnabled;
    private final SensorEventListener mProximitySensorListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            if (DisplayPowerController.this.mProximitySensorEnabled) {
                long time = SystemClock.uptimeMillis();
                boolean positive = false;
                float distance = event.values[0];
                if (distance >= 0.0f && distance < DisplayPowerController.this.mProximityThreshold) {
                    positive = true;
                }
                Slog.d(DisplayPowerController.TAG, "onSensorChanged: time=" + time + ",distance=" + distance + ",positive ? " + positive);
                DisplayPowerController.this.handleProximitySensorEvent(time, positive);
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    /* access modifiers changed from: private */
    public float mProximityThreshold;
    private final RampAnimator.Listener mRampAnimatorListener = new RampAnimator.Listener() {
        public void onAnimationEnd() {
            DisplayPowerController.this.sendUpdatePowerState();
        }
    };
    private int mReportedScreenStateToPolicy;
    private final int mScreenBrightnessDefault;
    private final int mScreenBrightnessDimConfig;
    private final int mScreenBrightnessDozeConfig;
    private int mScreenBrightnessForVr;
    private final int mScreenBrightnessForVrDefault;
    private final int mScreenBrightnessForVrRangeMaximum;
    private final int mScreenBrightnessForVrRangeMinimum;
    private RampAnimator<DisplayPowerState> mScreenBrightnessRampAnimator;
    private final int mScreenBrightnessRangeMaximum;
    private final int mScreenBrightnessRangeMinimum;
    private boolean mScreenOffBecauseOfProximity;
    private long mScreenOffBlockStartRealTime;
    private long mScreenOnBlockStartRealTime;
    private final SensorManager mSensorManager;
    private final SettingsObserver mSettingsObserver;
    private int mSkipRampState = 0;
    private final boolean mSkipScreenOnBrightnessRamp;
    private DisplayPowerControllerInjector.SunlightStateChangedListener mSunlightListener = new DisplayPowerControllerInjector.SunlightStateChangedListener() {
        public void onSunlightStateChange() {
            DisplayPowerController.this.sendUpdatePowerState();
        }

        public void updateScreenBrightnessSettingDueToSunlight(int brightness) {
            DisplayPowerController.this.putScreenBrightnessSetting(brightness);
        }
    };
    /* access modifiers changed from: private */
    public float mTemporaryAutoBrightnessAdjRatio;
    private float mTemporaryAutoBrightnessAdjustment;
    /* access modifiers changed from: private */
    public int mTemporaryScreenBrightness;
    private boolean mUnfinishedBusiness;
    private boolean mUseSoftwareAutoBrightnessConfig;
    private boolean mWaitingForNegativeProximity;
    private final WindowManagerPolicy mWindowManagerPolicy;

    public DisplayPowerController(Context context, DisplayManagerInternal.DisplayPowerCallbacks callbacks, Handler handler, SensorManager sensorManager, DisplayBlanker blanker) {
        String str;
        Resources resources;
        DisplayPowerController displayPowerController;
        int initialLightSensorRate;
        Context context2 = context;
        this.mHandler = new DisplayControllerHandler(handler.getLooper());
        this.mBrightnessTracker = new BrightnessTracker(context2, (BrightnessTracker.Injector) null);
        this.mSettingsObserver = new SettingsObserver(this.mHandler);
        this.mCallbacks = callbacks;
        this.mBatteryStats = BatteryStatsService.getService();
        this.mSensorManager = sensorManager;
        this.mWindowManagerPolicy = (WindowManagerPolicy) LocalServices.getService(WindowManagerPolicy.class);
        this.mBlanker = blanker;
        this.mContext = context2;
        Resources resources2 = context.getResources();
        int screenBrightnessSettingMinimum = clampAbsoluteBrightness(resources2.getInteger(17694891));
        this.mScreenBrightnessDozeConfig = clampAbsoluteBrightness(resources2.getInteger(17694885));
        this.mScreenBrightnessDimConfig = clampAbsoluteBrightness(resources2.getInteger(17694884));
        this.mScreenBrightnessRangeMinimum = Math.min(screenBrightnessSettingMinimum, this.mScreenBrightnessDimConfig);
        this.mScreenBrightnessRangeMaximum = clampAbsoluteBrightness(resources2.getInteger(17694890));
        this.mScreenBrightnessDefault = clampAbsoluteBrightness(resources2.getInteger(17694889));
        this.mScreenBrightnessForVrRangeMinimum = clampAbsoluteBrightness(resources2.getInteger(17694888));
        this.mScreenBrightnessForVrRangeMaximum = clampAbsoluteBrightness(resources2.getInteger(17694887));
        this.mScreenBrightnessForVrDefault = clampAbsoluteBrightness(resources2.getInteger(17694886));
        this.mUseSoftwareAutoBrightnessConfig = resources2.getBoolean(17891367);
        this.mAllowAutoBrightnessWhileDozingConfig = resources2.getBoolean(17891342);
        this.mBrightnessRampRateFast = resources2.getInteger(17694751);
        this.mBrightnessRampRateSlow = resources2.getInteger(17694752);
        this.mSkipScreenOnBrightnessRamp = resources2.getBoolean(17891523);
        if (this.mUseSoftwareAutoBrightnessConfig) {
            float dozeScaleFactor = resources2.getFraction(18022406, 1, 1);
            int[] ambientBrighteningThresholds = resources2.getIntArray(17235980);
            int[] ambientDarkeningThresholds = resources2.getIntArray(17235981);
            int[] ambientThresholdLevels = resources2.getIntArray(17235982);
            HysteresisLevels ambientBrightnessThresholds = new HysteresisLevels(ambientBrighteningThresholds, ambientDarkeningThresholds, ambientThresholdLevels);
            int[] screenBrighteningThresholds = resources2.getIntArray(17236059);
            int[] screenDarkeningThresholds = resources2.getIntArray(17236062);
            int[] screenThresholdLevels = resources2.getIntArray(17236063);
            HysteresisLevels screenBrightnessThresholds = new HysteresisLevels(screenBrighteningThresholds, screenDarkeningThresholds, screenThresholdLevels);
            long brighteningLightDebounce = (long) resources2.getInteger(17694736);
            long darkeningLightDebounce = (long) resources2.getInteger(17694737);
            boolean autoBrightnessResetAmbientLuxAfterWarmUp = resources2.getBoolean(17891362);
            int lightSensorWarmUpTimeConfig = resources2.getInteger(17694820);
            int lightSensorRate = resources2.getInteger(17694739);
            int[] iArr = ambientBrighteningThresholds;
            int initialLightSensorRate2 = resources2.getInteger(17694738);
            int[] screenDarkeningThresholds2 = screenDarkeningThresholds;
            if (initialLightSensorRate2 == -1) {
                initialLightSensorRate = lightSensorRate;
                int[] iArr2 = screenThresholdLevels;
            } else {
                if (initialLightSensorRate2 > lightSensorRate) {
                    StringBuilder sb = new StringBuilder();
                    int[] iArr3 = screenThresholdLevels;
                    sb.append("Expected config_autoBrightnessInitialLightSensorRate (");
                    sb.append(initialLightSensorRate2);
                    sb.append(") to be less than or equal to config_autoBrightnessLightSensorRate (");
                    sb.append(lightSensorRate);
                    sb.append(").");
                    Slog.w(TAG, sb.toString());
                }
                initialLightSensorRate = initialLightSensorRate2;
            }
            int shortTermModelTimeout = resources2.getInteger(17694740);
            String lightSensorType = resources2.getString(17039744);
            Sensor lightSensor = findDisplayLightSensor(lightSensorType);
            this.mBrightnessMapper = BrightnessMappingStrategy.create(resources2);
            if (this.mBrightnessMapper != null) {
                long darkeningLightDebounce2 = darkeningLightDebounce;
                AutomaticBrightnessController automaticBrightnessController = r1;
                int[] iArr4 = screenDarkeningThresholds2;
                String str2 = lightSensorType;
                int[] iArr5 = screenBrighteningThresholds;
                BrightnessMappingStrategy brightnessMappingStrategy = this.mBrightnessMapper;
                int[] iArr6 = ambientThresholdLevels;
                int i = this.mScreenBrightnessRangeMinimum;
                int[] iArr7 = ambientDarkeningThresholds;
                int i2 = this.mScreenBrightnessRangeMaximum;
                PackageManager packageManager = context.getPackageManager();
                int i3 = shortTermModelTimeout;
                int i4 = screenBrightnessSettingMinimum;
                resources = resources2;
                str = TAG;
                AutomaticBrightnessController automaticBrightnessController2 = new AutomaticBrightnessController(this, handler.getLooper(), sensorManager, lightSensor, brightnessMappingStrategy, lightSensorWarmUpTimeConfig, i, i2, dozeScaleFactor, lightSensorRate, initialLightSensorRate, brighteningLightDebounce, darkeningLightDebounce2, autoBrightnessResetAmbientLuxAfterWarmUp, ambientBrightnessThresholds, screenBrightnessThresholds, (long) shortTermModelTimeout, packageManager);
                displayPowerController = this;
                displayPowerController.mAutomaticBrightnessController = automaticBrightnessController;
            } else {
                String str3 = lightSensorType;
                int i5 = shortTermModelTimeout;
                str = TAG;
                int[] iArr8 = screenBrighteningThresholds;
                int i6 = screenBrightnessSettingMinimum;
                int[] iArr9 = ambientThresholdLevels;
                int[] iArr10 = ambientDarkeningThresholds;
                resources = resources2;
                displayPowerController = this;
                int[] iArr11 = screenDarkeningThresholds2;
                long j = darkeningLightDebounce;
                long darkeningLightDebounce3 = brighteningLightDebounce;
                displayPowerController.mUseSoftwareAutoBrightnessConfig = false;
            }
        } else {
            str = TAG;
            int i7 = screenBrightnessSettingMinimum;
            resources = resources2;
            displayPowerController = this;
        }
        displayPowerController.mColorFadeEnabled = !ActivityManager.isLowRamDeviceStatic();
        Resources resources3 = resources;
        displayPowerController.mColorFadeFadesConfig = resources3.getBoolean(17891359);
        displayPowerController.mDisplayBlanksAfterDozeConfig = resources3.getBoolean(17891411);
        displayPowerController.mBrightnessBucketsInDozeConfig = resources3.getBoolean(17891412);
        displayPowerController.mProximitySensor = displayPowerController.mSensorManager.getDefaultSensor(8);
        Sensor sensor = displayPowerController.mProximitySensor;
        if (sensor != null) {
            displayPowerController.mProximityThreshold = Math.min(sensor.getMaximumRange(), TYPICAL_PROXIMITY_THRESHOLD);
        }
        displayPowerController.mCurrentScreenBrightnessSetting = getScreenBrightnessSetting();
        displayPowerController.mScreenBrightnessForVr = getScreenBrightnessForVrSetting();
        displayPowerController.mAutoBrightnessAdjustment = getAutoBrightnessAdjustmentSetting();
        displayPowerController.mTemporaryScreenBrightness = -1;
        displayPowerController.mPendingScreenBrightnessSetting = -1;
        displayPowerController.mTemporaryAutoBrightnessAdjustment = Float.NaN;
        displayPowerController.mTemporaryAutoBrightnessAdjRatio = 0.0f;
        displayPowerController.mPendingAutoBrightnessAdjustment = Float.NaN;
        DisplayWhiteBalanceSettings displayWhiteBalanceSettings = null;
        DisplayWhiteBalanceController displayWhiteBalanceController = null;
        try {
            displayWhiteBalanceSettings = new DisplayWhiteBalanceSettings(displayPowerController.mContext, displayPowerController.mHandler);
            displayWhiteBalanceController = DisplayWhiteBalanceFactory.create(displayPowerController.mHandler, displayPowerController.mSensorManager, resources3);
            displayWhiteBalanceSettings.setCallbacks(displayPowerController);
            displayWhiteBalanceController.setCallbacks(displayPowerController);
        } catch (Exception e) {
            Slog.e(str, "failed to set up display white-balance: " + e);
        }
        displayPowerController.mDisplayWhiteBalanceSettings = displayWhiteBalanceSettings;
        displayPowerController.mDisplayWhiteBalanceController = displayWhiteBalanceController;
        ScreenEffectService.initDisplayPowerController(displayPowerController, displayPowerController.mHandler.getLooper());
        displayPowerController.mInjector = new DisplayPowerControllerInjector(displayPowerController.mContext, displayPowerController.mHandler.getLooper());
        displayPowerController.mInjector.setSunlightListener(displayPowerController.mSunlightListener);
    }

    private Sensor findDisplayLightSensor(String sensorType) {
        if (!TextUtils.isEmpty(sensorType)) {
            List<Sensor> sensors = this.mSensorManager.getSensorList(-1);
            for (int i = 0; i < sensors.size(); i++) {
                Sensor sensor = sensors.get(i);
                if (sensorType.equals(sensor.getStringType())) {
                    return sensor;
                }
            }
        }
        return this.mSensorManager.getDefaultSensor(5);
    }

    public boolean isProximitySensorAvailable() {
        return this.mProximitySensor != null;
    }

    public ParceledListSlice<BrightnessChangeEvent> getBrightnessEvents(int userId, boolean includePackage) {
        return this.mBrightnessTracker.getEvents(userId, includePackage);
    }

    public void onSwitchUser(int newUserId) {
        this.mHandler.obtainMessage(8, newUserId, 0).sendToTarget();
    }

    public ParceledListSlice<AmbientBrightnessDayStats> getAmbientBrightnessStats(int userId) {
        return this.mBrightnessTracker.getAmbientBrightnessStats(userId);
    }

    public void persistBrightnessTrackerState() {
        this.mBrightnessTracker.persistBrightnessTrackerState();
    }

    public boolean requestPowerState(DisplayManagerInternal.DisplayPowerRequest request, boolean waitForNegativeProximity) {
        boolean z;
        if (DEBUG) {
            Slog.d(TAG, "requestPowerState: " + request + ", waitForNegativeProximity=" + waitForNegativeProximity);
        }
        synchronized (this.mLock) {
            boolean changed = false;
            if (waitForNegativeProximity) {
                if (!this.mPendingWaitForNegativeProximityLocked) {
                    this.mPendingWaitForNegativeProximityLocked = true;
                    changed = true;
                }
            }
            if (this.mPendingRequestLocked == null) {
                this.mPendingRequestLocked = new DisplayManagerInternal.DisplayPowerRequest(request);
                changed = true;
            } else if (!this.mPendingRequestLocked.equals(request)) {
                this.mPendingRequestLocked.copyFrom(request);
                changed = true;
            }
            if (changed) {
                this.mDisplayReadyLocked = false;
            }
            if (changed && !this.mPendingRequestChangedLocked) {
                this.mPendingRequestChangedLocked = true;
                sendUpdatePowerStateLocked();
            }
            z = this.mDisplayReadyLocked;
        }
        return z;
    }

    public BrightnessConfiguration getDefaultBrightnessConfiguration() {
        AutomaticBrightnessController automaticBrightnessController = this.mAutomaticBrightnessController;
        if (automaticBrightnessController == null) {
            return null;
        }
        return automaticBrightnessController.getDefaultConfig();
    }

    /* access modifiers changed from: private */
    public void sendUpdatePowerState() {
        synchronized (this.mLock) {
            sendUpdatePowerStateLocked();
        }
    }

    private void sendUpdatePowerStateLocked() {
        if (!this.mPendingUpdatePowerStateLocked) {
            this.mPendingUpdatePowerStateLocked = true;
            this.mHandler.sendMessage(this.mHandler.obtainMessage(1));
        }
    }

    private void initialize() {
        this.mPowerState = new DisplayPowerState(this.mBlanker, this.mColorFadeEnabled ? new ColorFade(0) : null);
        if (this.mColorFadeEnabled) {
            this.mColorFadeOnAnimator = ObjectAnimator.ofFloat(this.mPowerState, DisplayPowerState.COLOR_FADE_LEVEL, new float[]{0.0f, 1.0f});
            this.mColorFadeOnAnimator.setDuration(250);
            this.mColorFadeOnAnimator.addListener(this.mAnimatorListener);
            this.mColorFadeOffAnimator = ObjectAnimator.ofFloat(this.mPowerState, DisplayPowerState.COLOR_FADE_LEVEL, new float[]{1.0f, 0.0f});
            this.mColorFadeOffAnimator.setDuration(300);
            this.mColorFadeOffAnimator.addListener(this.mAnimatorListener);
        }
        this.mScreenBrightnessRampAnimator = new RampAnimator<>(this.mPowerState, DisplayPowerState.SCREEN_BRIGHTNESS);
        this.mScreenBrightnessRampAnimator.setListener(this.mRampAnimatorListener);
        try {
            this.mBatteryStats.noteScreenState(this.mPowerState.getScreenState());
            this.mBatteryStats.noteScreenBrightness(this.mPowerState.getScreenBrightness());
        } catch (RemoteException e) {
        }
        float brightness = convertToNits(this.mPowerState.getScreenBrightness());
        if (brightness >= 0.0f) {
            this.mBrightnessTracker.start(brightness);
        }
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_brightness"), false, this.mSettingsObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_brightness_for_vr"), false, this.mSettingsObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_auto_brightness_adj"), false, this.mSettingsObserver, -1);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:100:0x015b, code lost:
        if (java.lang.Float.isNaN(r1.mTemporaryAutoBrightnessAdjustment) != false) goto L_0x0163;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:101:0x015d, code lost:
        r14 = r1.mTemporaryAutoBrightnessAdjustment;
        r3 = 1;
        r1.mAppliedTemporaryAutoBrightnessAdjustment = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:102:0x0163, code lost:
        r14 = r1.mAutoBrightnessAdjustment;
        r3 = 2;
        r1.mAppliedTemporaryAutoBrightnessAdjustment = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:104:0x016c, code lost:
        if (r1.mPowerRequest.boostScreenBrightness == false) goto L_0x017c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:105:0x016e, code lost:
        if (r5 == 0) goto L_0x017c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:106:0x0170, code lost:
        r5 = android.os.PowerManager.BRIGHTNESS_ON;
        r1.mBrightnessReasonTemp.setReason(9);
        r1.mAppliedBrightnessBoost = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:107:0x017c, code lost:
        r1.mAppliedBrightnessBoost = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:108:0x017f, code lost:
        if (r5 >= 0) goto L_0x0187;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:109:0x0181, code lost:
        if (r12 != false) goto L_0x0185;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:110:0x0183, code lost:
        if (r25 == false) goto L_0x0187;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:111:0x0185, code lost:
        r0 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:112:0x0187, code lost:
        r0 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:113:0x0188, code lost:
        r11 = false;
        r8 = r1.mAutomaticBrightnessController;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:114:0x018b, code lost:
        if (r8 == null) goto L_0x01b9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:115:0x018d, code lost:
        r11 = r8.hasUserDataPoints();
        r26 = r2;
        r27 = r3;
        r28 = r4;
        r1.mAutomaticBrightnessController.configure(r15, r1.mBrightnessConfiguration, ((float) r1.mLastUserSetScreenBrightness) / ((float) android.os.PowerManager.BRIGHTNESS_ON), r25, r14, r12, r1.mPowerRequest.policy);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:116:0x01b9, code lost:
        r26 = r2;
        r27 = r3;
        r28 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:118:0x01c2, code lost:
        if (r5 >= 0) goto L_0x022c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:119:0x01c4, code lost:
        r4 = r14;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:120:0x01c5, code lost:
        if (r15 == false) goto L_0x01dc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:121:0x01c7, code lost:
        r17 = false;
        r5 = (int) (((float) r1.mAutomaticBrightnessController.getAutomaticScreenBrightness()) * (r1.mTemporaryAutoBrightnessAdjRatio + 1.0f));
        r4 = r1.mAutomaticBrightnessController.getAutomaticScreenBrightnessAdjustment();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:122:0x01dc, code lost:
        r17 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:123:0x01de, code lost:
        if (r5 < 0) goto L_0x0217;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:124:0x01e0, code lost:
        r2 = clampScreenBrightness(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:125:0x01e6, code lost:
        if (r1.mAppliedAutoBrightness == 0) goto L_0x0207;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:126:0x01e8, code lost:
        if (r12 != false) goto L_0x0207;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:128:0x01ee, code lost:
        if (com.android.server.display.AutomaticBrightnessControllerInjector.isAmbientLuxFirstEvent() != false) goto L_0x0207;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:129:0x01f0, code lost:
        r17 = true;
        r1.mBrightnessRampRateSlow = com.android.server.display.AutomaticBrightnessControllerInjector.getScreenDarkenRate(r1.mPowerState.getScreenBrightness(), r2, r1.mBrightnessRampRateSlow, r1.mPowerState.getColorFadeLevel());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:130:0x0207, code lost:
        putScreenBrightnessSetting(r2);
        r1.mAppliedAutoBrightness = true;
        r1.mBrightnessReasonTemp.setReason(4);
        r5 = r2;
        r2 = r17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:131:0x0217, code lost:
        r1.mAppliedAutoBrightness = false;
        r2 = r17;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:133:0x021e, code lost:
        if (r14 == r4) goto L_0x0224;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:134:0x0220, code lost:
        putAutoBrightnessAdjustmentSetting(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:135:0x0224, code lost:
        r27 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:136:0x0227, code lost:
        r17 = r2;
        r2 = r27;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:137:0x022c, code lost:
        r17 = false;
        r1.mAppliedAutoBrightness = false;
        r2 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:138:0x0235, code lost:
        if (r5 >= 0) goto L_0x0249;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:140:0x023b, code lost:
        if (android.view.Display.isDozeState(r10) == false) goto L_0x0249;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:141:0x023d, code lost:
        r5 = clampScreenBrightness(r1.mCurrentScreenBrightnessSetting);
        r1.mBrightnessReasonTemp.setReason(3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:142:0x0249, code lost:
        r3 = r1.mInjector.canApplyingSunlightBrightness(r1.mPowerRequest.useAutoBrightness, r10, r1.mCurrentScreenBrightnessSetting, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:143:0x0255, code lost:
        if (r3 >= 0) goto L_0x0275;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x0257, code lost:
        if (r15 == false) goto L_0x0262;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:146:0x025e, code lost:
        if (r1.mPowerRequest.policy == 1) goto L_0x0262;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:147:0x0260, code lost:
        r4 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:148:0x0262, code lost:
        r4 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:150:0x0267, code lost:
        if (com.android.server.display.AutomaticBrightnessControllerInjector.waitForAutoBrightness(r4) != false) goto L_0x0275;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:151:0x0269, code lost:
        r3 = clampScreenBrightness(r1.mCurrentScreenBrightnessSetting);
        r1.mBrightnessReasonTemp.setReason(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:153:0x027a, code lost:
        if (r1.mPowerRequest.policy != 2) goto L_0x029e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:155:0x027e, code lost:
        if (r3 <= r1.mScreenBrightnessRangeMinimum) goto L_0x0294;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:0x0280, code lost:
        r3 = java.lang.Math.max(java.lang.Math.min(r3 - 10, r1.mScreenBrightnessDimConfig), r1.mScreenBrightnessRangeMinimum);
        r1.mBrightnessReasonTemp.addModifier(1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:158:0x0296, code lost:
        if (r1.mAppliedDimming != false) goto L_0x029a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:159:0x0298, code lost:
        r17 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:160:0x029a, code lost:
        r1.mAppliedDimming = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:162:0x02a0, code lost:
        if (r1.mAppliedDimming == false) goto L_0x02a7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:163:0x02a2, code lost:
        r17 = false;
        r1.mAppliedDimming = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:165:0x02ab, code lost:
        if (r1.mPowerRequest.lowPowerMode == false) goto L_0x02d8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:167:0x02af, code lost:
        if (r3 <= r1.mScreenBrightnessRangeMinimum) goto L_0x02ce;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:168:0x02b1, code lost:
        r3 = java.lang.Math.max((int) (((float) r3) * java.lang.Math.min(r1.mPowerRequest.screenLowPowerBrightnessFactor, 1.0f)), r1.mScreenBrightnessRangeMinimum);
        r1.mBrightnessReasonTemp.addModifier(2);
        r3 = r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:170:0x02d0, code lost:
        if (r1.mAppliedLowPower != false) goto L_0x02d4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:171:0x02d2, code lost:
        r17 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:172:0x02d4, code lost:
        r1.mAppliedLowPower = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:174:0x02da, code lost:
        if (r1.mAppliedLowPower == false) goto L_0x02e1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:175:0x02dc, code lost:
        r17 = false;
        r1.mAppliedLowPower = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:177:0x02e3, code lost:
        if (r1.mPendingScreenOff != false) goto L_0x03ec;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:179:0x02e7, code lost:
        if (r1.mSkipScreenOnBrightnessRamp == false) goto L_0x0319;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:181:0x02ea, code lost:
        if (r10 != 2) goto L_0x0316;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:183:0x02ee, code lost:
        if (r1.mSkipRampState != 0) goto L_0x02fa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:185:0x02f2, code lost:
        if (r1.mDozing == false) goto L_0x02fa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:186:0x02f4, code lost:
        r1.mInitialAutoBrightness = r3;
        r1.mSkipRampState = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:188:0x02fd, code lost:
        if (r1.mSkipRampState != 1) goto L_0x030b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:190:0x0301, code lost:
        if (r1.mUseSoftwareAutoBrightnessConfig == false) goto L_0x030b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:192:0x0305, code lost:
        if (r3 == r1.mInitialAutoBrightness) goto L_0x030b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x0307, code lost:
        r1.mSkipRampState = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:195:0x030e, code lost:
        if (r1.mSkipRampState != 2) goto L_0x0314;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:196:0x0310, code lost:
        r1.mSkipRampState = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:198:0x0316, code lost:
        r1.mSkipRampState = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:199:0x0319, code lost:
        r4 = r1.mScreenBrightnessRangeMinimum;
        r5 = r1.mScreenBrightnessRangeMaximum;
        r8 = r1.mPowerRequest.useAutoBrightness;
        r19 = r9;
        r9 = r1.mAutomaticBrightnessController;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:200:0x0325, code lost:
        if (r9 == null) goto L_0x032c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:201:0x0327, code lost:
        r9 = r9.getAmbientLux();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:202:0x032c, code lost:
        r9 = -1.0f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:203:0x032e, code lost:
        r33 = r9;
        r9 = r1.mPowerState;
        r20 = r12;
        r12 = r1.mBrightnessMapper;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:204:0x0336, code lost:
        if (r12 == null) goto L_0x033f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:205:0x0338, code lost:
        r35 = r12.getNitToBrightnessSpline();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:206:0x033f, code lost:
        r35 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:207:0x0341, code lost:
        r3 = com.android.server.display.BackLightController.adjustBrightness(r3, r4, r5, r8, r33, r9, r35);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:208:0x0350, code lost:
        if (r10 == 5) goto L_0x0357;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:209:0x0352, code lost:
        if (r13 != 5) goto L_0x0355;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:210:0x0355, code lost:
        r4 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:211:0x0357, code lost:
        r4 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:213:0x0359, code lost:
        if (r10 != 2) goto L_0x0361;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:215:0x035d, code lost:
        if (r1.mSkipRampState == 0) goto L_0x0361;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:216:0x035f, code lost:
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:217:0x0361, code lost:
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:219:0x0367, code lost:
        if (android.view.Display.isDozeState(r10) == false) goto L_0x036f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0058, code lost:
        if (r2 == false) goto L_0x005d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:221:0x036b, code lost:
        if (r1.mBrightnessBucketsInDozeConfig == false) goto L_0x036f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:222:0x036d, code lost:
        r8 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:223:0x036f, code lost:
        r8 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:225:0x0372, code lost:
        if (r1.mColorFadeEnabled == false) goto L_0x0382;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:227:0x037e, code lost:
        if (r1.mPowerState.getColorFadeLevel() != 1.0f) goto L_0x0382;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:228:0x0380, code lost:
        r9 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:229:0x0382, code lost:
        r9 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x005a, code lost:
        initialize();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:231:0x0385, code lost:
        if (r1.mAppliedTemporaryBrightness != false) goto L_0x038e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:233:0x0389, code lost:
        if (r1.mAppliedTemporaryAutoBrightnessAdjustment == false) goto L_0x038c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:234:0x038c, code lost:
        r12 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:235:0x038e, code lost:
        r12 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:236:0x038f, code lost:
        if (r5 != false) goto L_0x03ce;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:237:0x0391, code lost:
        if (r8 != false) goto L_0x03ce;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:238:0x0393, code lost:
        r16 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:239:0x0397, code lost:
        if (r1.mDozing == false) goto L_0x03a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x005d, code lost:
        r5 = -1;
        r9 = false;
        r10 = r1.mPowerRequest.policy;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:241:0x039a, code lost:
        if (r10 == 2) goto L_0x039d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:242:0x039d, code lost:
        r18 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:243:0x03a0, code lost:
        if (r4 != false) goto L_0x03cb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:244:0x03a2, code lost:
        if (r9 == false) goto L_0x03cb;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:245:0x03a4, code lost:
        if (r12 == false) goto L_0x03a7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:246:0x03a7, code lost:
        if (r17 == false) goto L_0x03b3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:248:0x03ab, code lost:
        if (r1.mAppliedDimming != false) goto L_0x03b3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0065, code lost:
        if (r10 == 0) goto L_0x0089;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:250:0x03af, code lost:
        if (r1.mAppliedLowPower != false) goto L_0x03b3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:251:0x03b1, code lost:
        r5 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:252:0x03b3, code lost:
        r5 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:253:0x03b4, code lost:
        r18 = r4;
        com.android.server.display.AutomaticBrightnessControllerInjector.updateSlowChangeStatus(r5, r1.mPowerState.getScreenBrightness());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:254:0x03c0, code lost:
        if (r17 == false) goto L_0x03c5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:255:0x03c2, code lost:
        r4 = r1.mBrightnessRampRateSlow;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:256:0x03c5, code lost:
        r4 = r1.mBrightnessRampRateFast;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x03c7, code lost:
        animateScreenBrightness(r3, r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:258:0x03cb, code lost:
        r18 = r4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:259:0x03ce, code lost:
        r18 = r4;
        r16 = r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0067, code lost:
        if (r10 == 1) goto L_0x006f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:260:0x03d2, code lost:
        animateScreenBrightness(r3, 0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:261:0x03d6, code lost:
        if (r12 != false) goto L_0x03ea;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:262:0x03d8, code lost:
        if (r0 == false) goto L_0x03e5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:263:0x03da, code lost:
        r4 = r1.mAutomaticBrightnessController;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:264:0x03dc, code lost:
        if (r4 == null) goto L_0x03e4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:266:0x03e2, code lost:
        if (r4.hasValidAmbientLux() != false) goto L_0x03e5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:267:0x03e4, code lost:
        r0 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:268:0x03e5, code lost:
        notifyBrightnessChanged(r3, r0, r11);
        r4 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:269:0x03ea, code lost:
        r4 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0069, code lost:
        if (r10 == 4) goto L_0x006d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:270:0x03ec, code lost:
        r19 = r9;
        r20 = r12;
        r4 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:272:0x03f9, code lost:
        if (r1.mBrightnessReasonTemp.equals(r1.mBrightnessReason) == false) goto L_0x03fd;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:273:0x03fb, code lost:
        if (r2 == 0) goto L_0x0437;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:274:0x03fd, code lost:
        android.util.Slog.v(TAG, "Brightness [" + r3 + "] reason changing to: '" + r1.mBrightnessReasonTemp.toString(r2) + "', previous reason: '" + r1.mBrightnessReason + "'.");
        r1.mBrightnessReason.set(r1.mBrightnessReasonTemp);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:276:0x0439, code lost:
        if (r1.mDisplayWhiteBalanceController == null) goto L_0x0458;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:278:0x043c, code lost:
        if (r10 != 2) goto L_0x0452;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x006b, code lost:
        r10 = 2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:280:0x0444, code lost:
        if (r1.mDisplayWhiteBalanceSettings.isEnabled() == false) goto L_0x0452;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:281:0x0446, code lost:
        r1.mDisplayWhiteBalanceController.setEnabled(true);
        r1.mDisplayWhiteBalanceController.updateDisplayColorTemperature();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:282:0x0452, code lost:
        r1.mDisplayWhiteBalanceController.setEnabled(false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:284:0x045a, code lost:
        if (r1.mPendingScreenOnUnblocker != null) goto L_0x047c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:286:0x045e, code lost:
        if (r1.mColorFadeEnabled == false) goto L_0x0470;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:288:0x0466, code lost:
        if (r1.mColorFadeOnAnimator.isStarted() != false) goto L_0x047c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x006d, code lost:
        r10 = 5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:290:0x046e, code lost:
        if (r1.mColorFadeOffAnimator.isStarted() != false) goto L_0x047c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:292:0x0478, code lost:
        if (r1.mPowerState.waitUntilClean(r1.mCleanListener) == false) goto L_0x047c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:293:0x047a, code lost:
        r0 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:294:0x047c, code lost:
        r0 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:295:0x047d, code lost:
        r5 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:296:0x047e, code lost:
        if (r5 == false) goto L_0x048a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:298:0x0486, code lost:
        if (r1.mScreenBrightnessRampAnimator.isAnimating() != false) goto L_0x048a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:299:0x0488, code lost:
        r0 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:300:0x048a, code lost:
        r0 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:301:0x048b, code lost:
        r8 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:302:0x048c, code lost:
        if (r5 == false) goto L_0x049e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:304:0x048f, code lost:
        if (r10 == 1) goto L_0x049e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:306:0x0493, code lost:
        if (r1.mReportedScreenStateToPolicy != 1) goto L_0x049e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:307:0x0495, code lost:
        setReportedScreenState(2);
        r1.mWindowManagerPolicy.screenTurnedOn();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:308:0x049e, code lost:
        if (r8 != false) goto L_0x04b7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0073, code lost:
        if (r1.mPowerRequest.dozeScreenState == 0) goto L_0x007a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:310:0x04a2, code lost:
        if (r1.mUnfinishedBusiness != false) goto L_0x04b7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:312:0x04a6, code lost:
        if (DEBUG == false) goto L_0x04af;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:313:0x04a8, code lost:
        android.util.Slog.d(TAG, "Unfinished business...");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:314:0x04af, code lost:
        r1.mCallbacks.acquireSuspendBlocker();
        r1.mUnfinishedBusiness = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:315:0x04b7, code lost:
        if (r5 == false) goto L_0x04d8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:316:0x04b9, code lost:
        if (r7 == false) goto L_0x04d8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:317:0x04bb, code lost:
        r9 = r1.mLock;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:318:0x04bd, code lost:
        monitor-enter(r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0075, code lost:
        r10 = r1.mPowerRequest.dozeScreenState;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:321:0x04c0, code lost:
        if (r1.mPendingRequestChangedLocked != false) goto L_0x04d0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:322:0x04c2, code lost:
        r1.mDisplayReadyLocked = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:323:0x04c7, code lost:
        if (DEBUG == false) goto L_0x04d0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:324:0x04c9, code lost:
        android.util.Slog.d(TAG, "Display ready!");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:325:0x04d0, code lost:
        monitor-exit(r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:326:0x04d1, code lost:
        sendOnStateChangedWithWakelock();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x007a, code lost:
        r10 = 3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:331:0x04d8, code lost:
        if (r8 == false) goto L_0x04f2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:333:0x04dc, code lost:
        if (r1.mUnfinishedBusiness == false) goto L_0x04f2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:335:0x04e0, code lost:
        if (DEBUG == false) goto L_0x04e9;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:336:0x04e2, code lost:
        android.util.Slog.d(TAG, "Finished business...");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:337:0x04e9, code lost:
        r0 = false;
        r1.mUnfinishedBusiness = false;
        r1.mCallbacks.releaseSuspendBlocker();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:338:0x04f2, code lost:
        r0 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:340:0x04f4, code lost:
        if (r10 == 2) goto L_0x04f7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:341:0x04f6, code lost:
        r0 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:342:0x04f7, code lost:
        r1.mDozing = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:343:0x04fd, code lost:
        if (r6 == r1.mPowerRequest.policy) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:344:0x04ff, code lost:
        logDisplayPolicyChanged(r1.mPowerRequest.policy);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x007d, code lost:
        if (r1.mAllowAutoBrightnessWhileDozingConfig != false) goto L_0x008c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:354:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:355:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x007f, code lost:
        r5 = r1.mPowerRequest.dozeScreenBrightness;
        r1.mBrightnessReasonTemp.setReason(2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x0089, code lost:
        r10 = 1;
        r9 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x008f, code lost:
        if (r1.mProximitySensor == null) goto L_0x00cf;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0095, code lost:
        if (r1.mPowerRequest.useProximitySensor == false) goto L_0x00aa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x0097, code lost:
        if (r10 == 1) goto L_0x00aa;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0099, code lost:
        setProximitySensorEnabled(true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x009e, code lost:
        if (r1.mScreenOffBecauseOfProximity != false) goto L_0x00c1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00a2, code lost:
        if (r1.mProximity != 1) goto L_0x00c1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00a4, code lost:
        r1.mScreenOffBecauseOfProximity = true;
        sendOnProximityPositiveWithWakelock();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00ac, code lost:
        if (r1.mWaitingForNegativeProximity == false) goto L_0x00bc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00b0, code lost:
        if (r1.mScreenOffBecauseOfProximity == false) goto L_0x00bc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x00b4, code lost:
        if (r1.mProximity != 1) goto L_0x00bc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00b6, code lost:
        if (r10 == 1) goto L_0x00bc;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00b8, code lost:
        setProximitySensorEnabled(true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00bc, code lost:
        setProximitySensorEnabled(false);
        r1.mWaitingForNegativeProximity = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x00c3, code lost:
        if (r1.mScreenOffBecauseOfProximity == false) goto L_0x00d1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x00c7, code lost:
        if (r1.mProximity == 1) goto L_0x00d1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x00c9, code lost:
        r1.mScreenOffBecauseOfProximity = false;
        sendOnProximityNegativeWithWakelock();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x00cf, code lost:
        r1.mWaitingForNegativeProximity = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:0x00d3, code lost:
        if (r1.mScreenOffBecauseOfProximity == false) goto L_0x00d6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x00d5, code lost:
        r10 = 1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x00d6, code lost:
        r13 = r1.mPowerState.getScreenState();
        animateScreenStateChange(r10, r9);
        r10 = r1.mPowerState.getScreenState();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x00e6, code lost:
        if (r10 == 1) goto L_0x00ee;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x00ec, code lost:
        if (r1.mPowerRequest.policy != 5) goto L_0x00f4;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x00ee, code lost:
        r5 = 0;
        r1.mBrightnessReasonTemp.setReason(5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x00f4, code lost:
        if (r10 != 5) goto L_0x00fe;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x00f6, code lost:
        r5 = r1.mScreenBrightnessForVr;
        r1.mBrightnessReasonTemp.setReason(6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x00fe, code lost:
        if (r5 >= 0) goto L_0x0113;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x0104, code lost:
        if (r1.mPowerRequest.screenBrightnessOverride <= 0) goto L_0x0113;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x0106, code lost:
        r5 = r1.mPowerRequest.screenBrightnessOverride;
        r1.mBrightnessReasonTemp.setReason(7);
        r1.mAppliedScreenBrightnessOverride = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x0113, code lost:
        r1.mAppliedScreenBrightnessOverride = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:78:0x0117, code lost:
        if (r1.mAllowAutoBrightnessWhileDozingConfig == false) goto L_0x0121;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x011d, code lost:
        if (android.view.Display.isDozeState(r10) == false) goto L_0x0121;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:81:0x011f, code lost:
        r4 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x0121, code lost:
        r4 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:84:0x0126, code lost:
        if (r1.mPowerRequest.useAutoBrightness == false) goto L_0x0134;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:85:0x0128, code lost:
        if (r10 == 2) goto L_0x012c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:0x012a, code lost:
        if (r4 == false) goto L_0x0134;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x012c, code lost:
        if (r5 >= 0) goto L_0x0134;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:89:0x0130, code lost:
        if (r1.mAutomaticBrightnessController == null) goto L_0x0134;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:90:0x0132, code lost:
        r15 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:91:0x0134, code lost:
        r15 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:92:0x0135, code lost:
        r25 = updateUserSetScreenBrightness();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:93:0x013b, code lost:
        if (r1.mTemporaryScreenBrightness <= 0) goto L_0x0149;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:94:0x013d, code lost:
        r5 = r1.mTemporaryScreenBrightness;
        r1.mAppliedTemporaryBrightness = true;
        r1.mBrightnessReasonTemp.setReason(8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:95:0x0149, code lost:
        r1.mAppliedTemporaryBrightness = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:96:0x014b, code lost:
        r12 = updateAutoBrightnessAdjustment();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:97:0x014f, code lost:
        if (r12 == false) goto L_0x0155;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:98:0x0151, code lost:
        r1.mTemporaryAutoBrightnessAdjustment = Float.NaN;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updatePowerState() {
        /*
            r36 = this;
            r1 = r36
            r2 = 0
            r3 = 0
            com.android.server.display.DisplayPowerController$BrightnessReason r0 = r1.mBrightnessReasonTemp
            r4 = 0
            r0.set(r4)
            java.lang.Object r5 = r1.mLock
            monitor-enter(r5)
            r0 = 0
            r1.mPendingUpdatePowerStateLocked = r0     // Catch:{ all -> 0x050b }
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r6 = r1.mPendingRequestLocked     // Catch:{ all -> 0x050b }
            if (r6 != 0) goto L_0x0016
            monitor-exit(r5)     // Catch:{ all -> 0x050b }
            return
        L_0x0016:
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r6 = r1.mPowerRequest     // Catch:{ all -> 0x050b }
            if (r6 != 0) goto L_0x002e
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r6 = new android.hardware.display.DisplayManagerInternal$DisplayPowerRequest     // Catch:{ all -> 0x050b }
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r7 = r1.mPendingRequestLocked     // Catch:{ all -> 0x050b }
            r6.<init>(r7)     // Catch:{ all -> 0x050b }
            r1.mPowerRequest = r6     // Catch:{ all -> 0x050b }
            boolean r6 = r1.mPendingWaitForNegativeProximityLocked     // Catch:{ all -> 0x050b }
            r1.mWaitingForNegativeProximity = r6     // Catch:{ all -> 0x050b }
            r1.mPendingWaitForNegativeProximityLocked = r0     // Catch:{ all -> 0x050b }
            r1.mPendingRequestChangedLocked = r0     // Catch:{ all -> 0x050b }
            r2 = 1
            r6 = 3
            goto L_0x004f
        L_0x002e:
            boolean r6 = r1.mPendingRequestChangedLocked     // Catch:{ all -> 0x050b }
            if (r6 == 0) goto L_0x004b
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r6 = r1.mPowerRequest     // Catch:{ all -> 0x050b }
            int r6 = r6.policy     // Catch:{ all -> 0x050b }
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r7 = r1.mPowerRequest     // Catch:{ all -> 0x050b }
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r8 = r1.mPendingRequestLocked     // Catch:{ all -> 0x050b }
            r7.copyFrom(r8)     // Catch:{ all -> 0x050b }
            boolean r7 = r1.mWaitingForNegativeProximity     // Catch:{ all -> 0x050b }
            boolean r8 = r1.mPendingWaitForNegativeProximityLocked     // Catch:{ all -> 0x050b }
            r7 = r7 | r8
            r1.mWaitingForNegativeProximity = r7     // Catch:{ all -> 0x050b }
            r1.mPendingWaitForNegativeProximityLocked = r0     // Catch:{ all -> 0x050b }
            r1.mPendingRequestChangedLocked = r0     // Catch:{ all -> 0x050b }
            r1.mDisplayReadyLocked = r0     // Catch:{ all -> 0x050b }
            goto L_0x004f
        L_0x004b:
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r6 = r1.mPowerRequest     // Catch:{ all -> 0x050b }
            int r6 = r6.policy     // Catch:{ all -> 0x050b }
        L_0x004f:
            boolean r7 = r1.mDisplayReadyLocked     // Catch:{ all -> 0x0507 }
            r8 = 1
            if (r7 != 0) goto L_0x0056
            r7 = r8
            goto L_0x0057
        L_0x0056:
            r7 = r0
        L_0x0057:
            monitor-exit(r5)     // Catch:{ all -> 0x0507 }
            if (r2 == 0) goto L_0x005d
            r36.initialize()
        L_0x005d:
            r5 = -1
            r9 = 0
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r10 = r1.mPowerRequest
            int r10 = r10.policy
            r11 = 4
            r12 = 2
            if (r10 == 0) goto L_0x0089
            if (r10 == r8) goto L_0x006f
            if (r10 == r11) goto L_0x006d
            r10 = 2
            goto L_0x008c
        L_0x006d:
            r10 = 5
            goto L_0x008c
        L_0x006f:
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r10 = r1.mPowerRequest
            int r10 = r10.dozeScreenState
            if (r10 == 0) goto L_0x007a
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r10 = r1.mPowerRequest
            int r10 = r10.dozeScreenState
            goto L_0x007b
        L_0x007a:
            r10 = 3
        L_0x007b:
            boolean r13 = r1.mAllowAutoBrightnessWhileDozingConfig
            if (r13 != 0) goto L_0x008c
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r13 = r1.mPowerRequest
            int r5 = r13.dozeScreenBrightness
            com.android.server.display.DisplayPowerController$BrightnessReason r13 = r1.mBrightnessReasonTemp
            r13.setReason(r12)
            goto L_0x008c
        L_0x0089:
            r10 = 1
            r9 = 1
        L_0x008c:
            android.hardware.Sensor r13 = r1.mProximitySensor
            if (r13 == 0) goto L_0x00cf
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r13 = r1.mPowerRequest
            boolean r13 = r13.useProximitySensor
            if (r13 == 0) goto L_0x00aa
            if (r10 == r8) goto L_0x00aa
            r1.setProximitySensorEnabled(r8)
            boolean r13 = r1.mScreenOffBecauseOfProximity
            if (r13 != 0) goto L_0x00c1
            int r13 = r1.mProximity
            if (r13 != r8) goto L_0x00c1
            r1.mScreenOffBecauseOfProximity = r8
            r36.sendOnProximityPositiveWithWakelock()
            goto L_0x00c1
        L_0x00aa:
            boolean r13 = r1.mWaitingForNegativeProximity
            if (r13 == 0) goto L_0x00bc
            boolean r13 = r1.mScreenOffBecauseOfProximity
            if (r13 == 0) goto L_0x00bc
            int r13 = r1.mProximity
            if (r13 != r8) goto L_0x00bc
            if (r10 == r8) goto L_0x00bc
            r1.setProximitySensorEnabled(r8)
            goto L_0x00c1
        L_0x00bc:
            r1.setProximitySensorEnabled(r0)
            r1.mWaitingForNegativeProximity = r0
        L_0x00c1:
            boolean r13 = r1.mScreenOffBecauseOfProximity
            if (r13 == 0) goto L_0x00d1
            int r13 = r1.mProximity
            if (r13 == r8) goto L_0x00d1
            r1.mScreenOffBecauseOfProximity = r0
            r36.sendOnProximityNegativeWithWakelock()
            goto L_0x00d1
        L_0x00cf:
            r1.mWaitingForNegativeProximity = r0
        L_0x00d1:
            boolean r13 = r1.mScreenOffBecauseOfProximity
            if (r13 == 0) goto L_0x00d6
            r10 = 1
        L_0x00d6:
            com.android.server.display.DisplayPowerState r13 = r1.mPowerState
            int r13 = r13.getScreenState()
            r1.animateScreenStateChange(r10, r9)
            com.android.server.display.DisplayPowerState r14 = r1.mPowerState
            int r10 = r14.getScreenState()
            r14 = 5
            if (r10 == r8) goto L_0x00ee
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r15 = r1.mPowerRequest
            int r15 = r15.policy
            if (r15 != r14) goto L_0x00f4
        L_0x00ee:
            r5 = 0
            com.android.server.display.DisplayPowerController$BrightnessReason r15 = r1.mBrightnessReasonTemp
            r15.setReason(r14)
        L_0x00f4:
            if (r10 != r14) goto L_0x00fe
            int r5 = r1.mScreenBrightnessForVr
            com.android.server.display.DisplayPowerController$BrightnessReason r15 = r1.mBrightnessReasonTemp
            r4 = 6
            r15.setReason(r4)
        L_0x00fe:
            if (r5 >= 0) goto L_0x0113
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r4 = r1.mPowerRequest
            int r4 = r4.screenBrightnessOverride
            if (r4 <= 0) goto L_0x0113
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r4 = r1.mPowerRequest
            int r5 = r4.screenBrightnessOverride
            com.android.server.display.DisplayPowerController$BrightnessReason r4 = r1.mBrightnessReasonTemp
            r15 = 7
            r4.setReason(r15)
            r1.mAppliedScreenBrightnessOverride = r8
            goto L_0x0115
        L_0x0113:
            r1.mAppliedScreenBrightnessOverride = r0
        L_0x0115:
            boolean r4 = r1.mAllowAutoBrightnessWhileDozingConfig
            if (r4 == 0) goto L_0x0121
            boolean r4 = android.view.Display.isDozeState(r10)
            if (r4 == 0) goto L_0x0121
            r4 = r8
            goto L_0x0122
        L_0x0121:
            r4 = r0
        L_0x0122:
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r15 = r1.mPowerRequest
            boolean r15 = r15.useAutoBrightness
            if (r15 == 0) goto L_0x0134
            if (r10 == r12) goto L_0x012c
            if (r4 == 0) goto L_0x0134
        L_0x012c:
            if (r5 >= 0) goto L_0x0134
            com.android.server.display.AutomaticBrightnessController r15 = r1.mAutomaticBrightnessController
            if (r15 == 0) goto L_0x0134
            r15 = r8
            goto L_0x0135
        L_0x0134:
            r15 = r0
        L_0x0135:
            boolean r25 = r36.updateUserSetScreenBrightness()
            int r14 = r1.mTemporaryScreenBrightness
            if (r14 <= 0) goto L_0x0149
            int r5 = r1.mTemporaryScreenBrightness
            r1.mAppliedTemporaryBrightness = r8
            com.android.server.display.DisplayPowerController$BrightnessReason r14 = r1.mBrightnessReasonTemp
            r12 = 8
            r14.setReason(r12)
            goto L_0x014b
        L_0x0149:
            r1.mAppliedTemporaryBrightness = r0
        L_0x014b:
            boolean r12 = r36.updateAutoBrightnessAdjustment()
            if (r12 == 0) goto L_0x0155
            r14 = 2143289344(0x7fc00000, float:NaN)
            r1.mTemporaryAutoBrightnessAdjustment = r14
        L_0x0155:
            float r14 = r1.mTemporaryAutoBrightnessAdjustment
            boolean r14 = java.lang.Float.isNaN(r14)
            if (r14 != 0) goto L_0x0163
            float r14 = r1.mTemporaryAutoBrightnessAdjustment
            r3 = 1
            r1.mAppliedTemporaryAutoBrightnessAdjustment = r8
            goto L_0x0168
        L_0x0163:
            float r14 = r1.mAutoBrightnessAdjustment
            r3 = 2
            r1.mAppliedTemporaryAutoBrightnessAdjustment = r0
        L_0x0168:
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r11 = r1.mPowerRequest
            boolean r11 = r11.boostScreenBrightness
            if (r11 == 0) goto L_0x017c
            if (r5 == 0) goto L_0x017c
            int r5 = android.os.PowerManager.BRIGHTNESS_ON
            com.android.server.display.DisplayPowerController$BrightnessReason r11 = r1.mBrightnessReasonTemp
            r0 = 9
            r11.setReason(r0)
            r1.mAppliedBrightnessBoost = r8
            goto L_0x017f
        L_0x017c:
            r0 = 0
            r1.mAppliedBrightnessBoost = r0
        L_0x017f:
            if (r5 >= 0) goto L_0x0187
            if (r12 != 0) goto L_0x0185
            if (r25 == 0) goto L_0x0187
        L_0x0185:
            r0 = r8
            goto L_0x0188
        L_0x0187:
            r0 = 0
        L_0x0188:
            r11 = 0
            com.android.server.display.AutomaticBrightnessController r8 = r1.mAutomaticBrightnessController
            if (r8 == 0) goto L_0x01b9
            boolean r11 = r8.hasUserDataPoints()
            com.android.server.display.AutomaticBrightnessController r8 = r1.mAutomaticBrightnessController
            r26 = r2
            android.hardware.display.BrightnessConfiguration r2 = r1.mBrightnessConfiguration
            r27 = r3
            int r3 = r1.mLastUserSetScreenBrightness
            float r3 = (float) r3
            r28 = r4
            int r4 = android.os.PowerManager.BRIGHTNESS_ON
            float r4 = (float) r4
            float r20 = r3 / r4
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r3 = r1.mPowerRequest
            int r3 = r3.policy
            r17 = r8
            r18 = r15
            r19 = r2
            r21 = r25
            r22 = r14
            r23 = r12
            r24 = r3
            r17.configure(r18, r19, r20, r21, r22, r23, r24)
            goto L_0x01bf
        L_0x01b9:
            r26 = r2
            r27 = r3
            r28 = r4
        L_0x01bf:
            r2 = 0
            r3 = 1065353216(0x3f800000, float:1.0)
            if (r5 >= 0) goto L_0x022c
            r4 = r14
            if (r15 == 0) goto L_0x01dc
            com.android.server.display.AutomaticBrightnessController r8 = r1.mAutomaticBrightnessController
            int r8 = r8.getAutomaticScreenBrightness()
            float r8 = (float) r8
            r17 = r2
            float r2 = r1.mTemporaryAutoBrightnessAdjRatio
            float r2 = r2 + r3
            float r8 = r8 * r2
            int r5 = (int) r8
            com.android.server.display.AutomaticBrightnessController r2 = r1.mAutomaticBrightnessController
            float r4 = r2.getAutomaticScreenBrightnessAdjustment()
            goto L_0x01de
        L_0x01dc:
            r17 = r2
        L_0x01de:
            if (r5 < 0) goto L_0x0217
            int r2 = r1.clampScreenBrightness(r5)
            boolean r5 = r1.mAppliedAutoBrightness
            if (r5 == 0) goto L_0x0207
            if (r12 != 0) goto L_0x0207
            boolean r5 = com.android.server.display.AutomaticBrightnessControllerInjector.isAmbientLuxFirstEvent()
            if (r5 != 0) goto L_0x0207
            r5 = 1
            com.android.server.display.DisplayPowerState r8 = r1.mPowerState
            int r8 = r8.getScreenBrightness()
            int r3 = r1.mBrightnessRampRateSlow
            r17 = r5
            com.android.server.display.DisplayPowerState r5 = r1.mPowerState
            float r5 = r5.getColorFadeLevel()
            int r3 = com.android.server.display.AutomaticBrightnessControllerInjector.getScreenDarkenRate(r8, r2, r3, r5)
            r1.mBrightnessRampRateSlow = r3
        L_0x0207:
            r1.putScreenBrightnessSetting(r2)
            r3 = 1
            r1.mAppliedAutoBrightness = r3
            com.android.server.display.DisplayPowerController$BrightnessReason r3 = r1.mBrightnessReasonTemp
            r5 = 4
            r3.setReason(r5)
            r5 = r2
            r2 = r17
            goto L_0x021c
        L_0x0217:
            r2 = 0
            r1.mAppliedAutoBrightness = r2
            r2 = r17
        L_0x021c:
            int r3 = (r14 > r4 ? 1 : (r14 == r4 ? 0 : -1))
            if (r3 == 0) goto L_0x0224
            r1.putAutoBrightnessAdjustmentSetting(r4)
            goto L_0x0227
        L_0x0224:
            r3 = 0
            r27 = r3
        L_0x0227:
            r17 = r2
            r2 = r27
            goto L_0x0235
        L_0x022c:
            r17 = r2
            r2 = 0
            r1.mAppliedAutoBrightness = r2
            r27 = 0
            r2 = r27
        L_0x0235:
            if (r5 >= 0) goto L_0x0249
            boolean r3 = android.view.Display.isDozeState(r10)
            if (r3 == 0) goto L_0x0249
            int r3 = r1.mCurrentScreenBrightnessSetting
            int r5 = r1.clampScreenBrightness(r3)
            com.android.server.display.DisplayPowerController$BrightnessReason r3 = r1.mBrightnessReasonTemp
            r4 = 3
            r3.setReason(r4)
        L_0x0249:
            com.android.server.display.DisplayPowerControllerInjector r3 = r1.mInjector
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r4 = r1.mPowerRequest
            boolean r4 = r4.useAutoBrightness
            int r8 = r1.mCurrentScreenBrightnessSetting
            int r3 = r3.canApplyingSunlightBrightness(r4, r10, r8, r5)
            if (r3 >= 0) goto L_0x0275
            if (r15 == 0) goto L_0x0262
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r4 = r1.mPowerRequest
            int r4 = r4.policy
            r5 = 1
            if (r4 == r5) goto L_0x0262
            r4 = 1
            goto L_0x0263
        L_0x0262:
            r4 = 0
        L_0x0263:
            boolean r4 = com.android.server.display.AutomaticBrightnessControllerInjector.waitForAutoBrightness(r4)
            if (r4 != 0) goto L_0x0275
            int r4 = r1.mCurrentScreenBrightnessSetting
            int r3 = r1.clampScreenBrightness(r4)
            com.android.server.display.DisplayPowerController$BrightnessReason r4 = r1.mBrightnessReasonTemp
            r5 = 1
            r4.setReason(r5)
        L_0x0275:
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r4 = r1.mPowerRequest
            int r4 = r4.policy
            r5 = 2
            if (r4 != r5) goto L_0x029e
            int r4 = r1.mScreenBrightnessRangeMinimum
            if (r3 <= r4) goto L_0x0294
            int r4 = r3 + -10
            int r5 = r1.mScreenBrightnessDimConfig
            int r4 = java.lang.Math.min(r4, r5)
            int r5 = r1.mScreenBrightnessRangeMinimum
            int r3 = java.lang.Math.max(r4, r5)
            com.android.server.display.DisplayPowerController$BrightnessReason r4 = r1.mBrightnessReasonTemp
            r5 = 1
            r4.addModifier(r5)
        L_0x0294:
            boolean r4 = r1.mAppliedDimming
            if (r4 != 0) goto L_0x029a
            r17 = 0
        L_0x029a:
            r4 = 1
            r1.mAppliedDimming = r4
            goto L_0x02a7
        L_0x029e:
            boolean r4 = r1.mAppliedDimming
            if (r4 == 0) goto L_0x02a7
            r17 = 0
            r4 = 0
            r1.mAppliedDimming = r4
        L_0x02a7:
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r4 = r1.mPowerRequest
            boolean r4 = r4.lowPowerMode
            if (r4 == 0) goto L_0x02d8
            int r4 = r1.mScreenBrightnessRangeMinimum
            if (r3 <= r4) goto L_0x02ce
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r4 = r1.mPowerRequest
            float r4 = r4.screenLowPowerBrightnessFactor
            r5 = 1065353216(0x3f800000, float:1.0)
            float r4 = java.lang.Math.min(r4, r5)
            float r5 = (float) r3
            float r5 = r5 * r4
            int r5 = (int) r5
            int r8 = r1.mScreenBrightnessRangeMinimum
            int r3 = java.lang.Math.max(r5, r8)
            com.android.server.display.DisplayPowerController$BrightnessReason r8 = r1.mBrightnessReasonTemp
            r19 = r3
            r3 = 2
            r8.addModifier(r3)
            r3 = r19
        L_0x02ce:
            boolean r4 = r1.mAppliedLowPower
            if (r4 != 0) goto L_0x02d4
            r17 = 0
        L_0x02d4:
            r4 = 1
            r1.mAppliedLowPower = r4
            goto L_0x02e1
        L_0x02d8:
            boolean r4 = r1.mAppliedLowPower
            if (r4 == 0) goto L_0x02e1
            r17 = 0
            r4 = 0
            r1.mAppliedLowPower = r4
        L_0x02e1:
            boolean r4 = r1.mPendingScreenOff
            if (r4 != 0) goto L_0x03ec
            boolean r4 = r1.mSkipScreenOnBrightnessRamp
            if (r4 == 0) goto L_0x0319
            r4 = 2
            if (r10 != r4) goto L_0x0316
            int r4 = r1.mSkipRampState
            if (r4 != 0) goto L_0x02fa
            boolean r4 = r1.mDozing
            if (r4 == 0) goto L_0x02fa
            r1.mInitialAutoBrightness = r3
            r4 = 1
            r1.mSkipRampState = r4
            goto L_0x0319
        L_0x02fa:
            r4 = 1
            int r5 = r1.mSkipRampState
            if (r5 != r4) goto L_0x030b
            boolean r4 = r1.mUseSoftwareAutoBrightnessConfig
            if (r4 == 0) goto L_0x030b
            int r4 = r1.mInitialAutoBrightness
            if (r3 == r4) goto L_0x030b
            r4 = 2
            r1.mSkipRampState = r4
            goto L_0x0319
        L_0x030b:
            r4 = 2
            int r5 = r1.mSkipRampState
            if (r5 != r4) goto L_0x0314
            r4 = 0
            r1.mSkipRampState = r4
            goto L_0x0319
        L_0x0314:
            r4 = 0
            goto L_0x0319
        L_0x0316:
            r4 = 0
            r1.mSkipRampState = r4
        L_0x0319:
            int r4 = r1.mScreenBrightnessRangeMinimum
            int r5 = r1.mScreenBrightnessRangeMaximum
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r8 = r1.mPowerRequest
            boolean r8 = r8.useAutoBrightness
            r19 = r9
            com.android.server.display.AutomaticBrightnessController r9 = r1.mAutomaticBrightnessController
            if (r9 == 0) goto L_0x032c
            float r9 = r9.getAmbientLux()
            goto L_0x032e
        L_0x032c:
            r9 = -1082130432(0xffffffffbf800000, float:-1.0)
        L_0x032e:
            r33 = r9
            com.android.server.display.DisplayPowerState r9 = r1.mPowerState
            r20 = r12
            com.android.server.display.BrightnessMappingStrategy r12 = r1.mBrightnessMapper
            if (r12 == 0) goto L_0x033f
            android.util.Spline r12 = r12.getNitToBrightnessSpline()
            r35 = r12
            goto L_0x0341
        L_0x033f:
            r35 = 0
        L_0x0341:
            r29 = r3
            r30 = r4
            r31 = r5
            r32 = r8
            r34 = r9
            int r3 = com.android.server.display.BackLightController.adjustBrightness(r29, r30, r31, r32, r33, r34, r35)
            r4 = 5
            if (r10 == r4) goto L_0x0357
            if (r13 != r4) goto L_0x0355
            goto L_0x0357
        L_0x0355:
            r4 = 0
            goto L_0x0358
        L_0x0357:
            r4 = 1
        L_0x0358:
            r5 = 2
            if (r10 != r5) goto L_0x0361
            int r5 = r1.mSkipRampState
            if (r5 == 0) goto L_0x0361
            r5 = 1
            goto L_0x0362
        L_0x0361:
            r5 = 0
        L_0x0362:
            boolean r8 = android.view.Display.isDozeState(r10)
            if (r8 == 0) goto L_0x036f
            boolean r8 = r1.mBrightnessBucketsInDozeConfig
            if (r8 == 0) goto L_0x036f
            r8 = 1
            goto L_0x0370
        L_0x036f:
            r8 = 0
        L_0x0370:
            boolean r9 = r1.mColorFadeEnabled
            if (r9 == 0) goto L_0x0382
            com.android.server.display.DisplayPowerState r9 = r1.mPowerState
            float r9 = r9.getColorFadeLevel()
            r12 = 1065353216(0x3f800000, float:1.0)
            int r9 = (r9 > r12 ? 1 : (r9 == r12 ? 0 : -1))
            if (r9 != 0) goto L_0x0382
            r9 = 1
            goto L_0x0383
        L_0x0382:
            r9 = 0
        L_0x0383:
            boolean r12 = r1.mAppliedTemporaryBrightness
            if (r12 != 0) goto L_0x038e
            boolean r12 = r1.mAppliedTemporaryAutoBrightnessAdjustment
            if (r12 == 0) goto L_0x038c
            goto L_0x038e
        L_0x038c:
            r12 = 0
            goto L_0x038f
        L_0x038e:
            r12 = 1
        L_0x038f:
            if (r5 != 0) goto L_0x03ce
            if (r8 != 0) goto L_0x03ce
            r16 = r5
            boolean r5 = r1.mDozing
            if (r5 == 0) goto L_0x03a0
            r5 = 2
            if (r10 == r5) goto L_0x039d
            goto L_0x03a0
        L_0x039d:
            r18 = r4
            goto L_0x03d2
        L_0x03a0:
            if (r4 != 0) goto L_0x03cb
            if (r9 == 0) goto L_0x03cb
            if (r12 == 0) goto L_0x03a7
            goto L_0x039d
        L_0x03a7:
            if (r17 == 0) goto L_0x03b3
            boolean r5 = r1.mAppliedDimming
            if (r5 != 0) goto L_0x03b3
            boolean r5 = r1.mAppliedLowPower
            if (r5 != 0) goto L_0x03b3
            r5 = 1
            goto L_0x03b4
        L_0x03b3:
            r5 = 0
        L_0x03b4:
            r18 = r4
            com.android.server.display.DisplayPowerState r4 = r1.mPowerState
            int r4 = r4.getScreenBrightness()
            com.android.server.display.AutomaticBrightnessControllerInjector.updateSlowChangeStatus(r5, r4)
            if (r17 == 0) goto L_0x03c5
            int r4 = r1.mBrightnessRampRateSlow
            goto L_0x03c7
        L_0x03c5:
            int r4 = r1.mBrightnessRampRateFast
        L_0x03c7:
            r1.animateScreenBrightness(r3, r4)
            goto L_0x03d6
        L_0x03cb:
            r18 = r4
            goto L_0x03d2
        L_0x03ce:
            r18 = r4
            r16 = r5
        L_0x03d2:
            r4 = 0
            r1.animateScreenBrightness(r3, r4)
        L_0x03d6:
            if (r12 != 0) goto L_0x03ea
            if (r0 == 0) goto L_0x03e5
            com.android.server.display.AutomaticBrightnessController r4 = r1.mAutomaticBrightnessController
            if (r4 == 0) goto L_0x03e4
            boolean r4 = r4.hasValidAmbientLux()
            if (r4 != 0) goto L_0x03e5
        L_0x03e4:
            r0 = 0
        L_0x03e5:
            r1.notifyBrightnessChanged(r3, r0, r11)
            r4 = r0
            goto L_0x03f1
        L_0x03ea:
            r4 = r0
            goto L_0x03f1
        L_0x03ec:
            r19 = r9
            r20 = r12
            r4 = r0
        L_0x03f1:
            com.android.server.display.DisplayPowerController$BrightnessReason r0 = r1.mBrightnessReasonTemp
            com.android.server.display.DisplayPowerController$BrightnessReason r5 = r1.mBrightnessReason
            boolean r0 = r0.equals(r5)
            if (r0 == 0) goto L_0x03fd
            if (r2 == 0) goto L_0x0437
        L_0x03fd:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r5 = "Brightness ["
            r0.append(r5)
            r0.append(r3)
            java.lang.String r5 = "] reason changing to: '"
            r0.append(r5)
            com.android.server.display.DisplayPowerController$BrightnessReason r5 = r1.mBrightnessReasonTemp
            java.lang.String r5 = r5.toString(r2)
            r0.append(r5)
            java.lang.String r5 = "', previous reason: '"
            r0.append(r5)
            com.android.server.display.DisplayPowerController$BrightnessReason r5 = r1.mBrightnessReason
            r0.append(r5)
            java.lang.String r5 = "'."
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            java.lang.String r5 = "DisplayPowerController"
            android.util.Slog.v(r5, r0)
            com.android.server.display.DisplayPowerController$BrightnessReason r0 = r1.mBrightnessReason
            com.android.server.display.DisplayPowerController$BrightnessReason r5 = r1.mBrightnessReasonTemp
            r0.set(r5)
        L_0x0437:
            com.android.server.display.whitebalance.DisplayWhiteBalanceController r0 = r1.mDisplayWhiteBalanceController
            if (r0 == 0) goto L_0x0458
            r0 = 2
            if (r10 != r0) goto L_0x0452
            com.android.server.display.whitebalance.DisplayWhiteBalanceSettings r0 = r1.mDisplayWhiteBalanceSettings
            boolean r0 = r0.isEnabled()
            if (r0 == 0) goto L_0x0452
            com.android.server.display.whitebalance.DisplayWhiteBalanceController r0 = r1.mDisplayWhiteBalanceController
            r5 = 1
            r0.setEnabled(r5)
            com.android.server.display.whitebalance.DisplayWhiteBalanceController r0 = r1.mDisplayWhiteBalanceController
            r0.updateDisplayColorTemperature()
            goto L_0x0458
        L_0x0452:
            com.android.server.display.whitebalance.DisplayWhiteBalanceController r0 = r1.mDisplayWhiteBalanceController
            r5 = 0
            r0.setEnabled(r5)
        L_0x0458:
            com.android.server.display.DisplayPowerController$ScreenOnUnblocker r0 = r1.mPendingScreenOnUnblocker
            if (r0 != 0) goto L_0x047c
            boolean r0 = r1.mColorFadeEnabled
            if (r0 == 0) goto L_0x0470
            android.animation.ObjectAnimator r0 = r1.mColorFadeOnAnimator
            boolean r0 = r0.isStarted()
            if (r0 != 0) goto L_0x047c
            android.animation.ObjectAnimator r0 = r1.mColorFadeOffAnimator
            boolean r0 = r0.isStarted()
            if (r0 != 0) goto L_0x047c
        L_0x0470:
            com.android.server.display.DisplayPowerState r0 = r1.mPowerState
            java.lang.Runnable r5 = r1.mCleanListener
            boolean r0 = r0.waitUntilClean(r5)
            if (r0 == 0) goto L_0x047c
            r0 = 1
            goto L_0x047d
        L_0x047c:
            r0 = 0
        L_0x047d:
            r5 = r0
            if (r5 == 0) goto L_0x048a
            com.android.server.display.RampAnimator<com.android.server.display.DisplayPowerState> r0 = r1.mScreenBrightnessRampAnimator
            boolean r0 = r0.isAnimating()
            if (r0 != 0) goto L_0x048a
            r0 = 1
            goto L_0x048b
        L_0x048a:
            r0 = 0
        L_0x048b:
            r8 = r0
            if (r5 == 0) goto L_0x049e
            r0 = 1
            if (r10 == r0) goto L_0x049e
            int r9 = r1.mReportedScreenStateToPolicy
            if (r9 != r0) goto L_0x049e
            r0 = 2
            r1.setReportedScreenState(r0)
            com.android.server.policy.WindowManagerPolicy r0 = r1.mWindowManagerPolicy
            r0.screenTurnedOn()
        L_0x049e:
            if (r8 != 0) goto L_0x04b7
            boolean r0 = r1.mUnfinishedBusiness
            if (r0 != 0) goto L_0x04b7
            boolean r0 = DEBUG
            if (r0 == 0) goto L_0x04af
            java.lang.String r0 = "DisplayPowerController"
            java.lang.String r9 = "Unfinished business..."
            android.util.Slog.d(r0, r9)
        L_0x04af:
            android.hardware.display.DisplayManagerInternal$DisplayPowerCallbacks r0 = r1.mCallbacks
            r0.acquireSuspendBlocker()
            r0 = 1
            r1.mUnfinishedBusiness = r0
        L_0x04b7:
            if (r5 == 0) goto L_0x04d8
            if (r7 == 0) goto L_0x04d8
            java.lang.Object r9 = r1.mLock
            monitor-enter(r9)
            boolean r0 = r1.mPendingRequestChangedLocked     // Catch:{ all -> 0x04d5 }
            if (r0 != 0) goto L_0x04d0
            r0 = 1
            r1.mDisplayReadyLocked = r0     // Catch:{ all -> 0x04d5 }
            boolean r12 = DEBUG     // Catch:{ all -> 0x04d5 }
            if (r12 == 0) goto L_0x04d0
            java.lang.String r12 = "DisplayPowerController"
            java.lang.String r0 = "Display ready!"
            android.util.Slog.d(r12, r0)     // Catch:{ all -> 0x04d5 }
        L_0x04d0:
            monitor-exit(r9)     // Catch:{ all -> 0x04d5 }
            r36.sendOnStateChangedWithWakelock()
            goto L_0x04d8
        L_0x04d5:
            r0 = move-exception
            monitor-exit(r9)     // Catch:{ all -> 0x04d5 }
            throw r0
        L_0x04d8:
            if (r8 == 0) goto L_0x04f2
            boolean r0 = r1.mUnfinishedBusiness
            if (r0 == 0) goto L_0x04f2
            boolean r0 = DEBUG
            if (r0 == 0) goto L_0x04e9
            java.lang.String r0 = "DisplayPowerController"
            java.lang.String r9 = "Finished business..."
            android.util.Slog.d(r0, r9)
        L_0x04e9:
            r0 = 0
            r1.mUnfinishedBusiness = r0
            android.hardware.display.DisplayManagerInternal$DisplayPowerCallbacks r9 = r1.mCallbacks
            r9.releaseSuspendBlocker()
            goto L_0x04f3
        L_0x04f2:
            r0 = 0
        L_0x04f3:
            r9 = 2
            if (r10 == r9) goto L_0x04f7
            r0 = 1
        L_0x04f7:
            r1.mDozing = r0
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r0 = r1.mPowerRequest
            int r0 = r0.policy
            if (r6 == r0) goto L_0x0506
            android.hardware.display.DisplayManagerInternal$DisplayPowerRequest r0 = r1.mPowerRequest
            int r0 = r0.policy
            r1.logDisplayPolicyChanged(r0)
        L_0x0506:
            return
        L_0x0507:
            r0 = move-exception
            r26 = r2
            goto L_0x050c
        L_0x050b:
            r0 = move-exception
        L_0x050c:
            monitor-exit(r5)     // Catch:{ all -> 0x050b }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.DisplayPowerController.updatePowerState():void");
    }

    public void updateBrightness() {
        sendUpdatePowerState();
    }

    public void setBrightnessConfiguration(BrightnessConfiguration c) {
        this.mHandler.obtainMessage(5, c).sendToTarget();
    }

    public void setTemporaryBrightness(int brightness) {
        this.mHandler.obtainMessage(6, brightness, 0).sendToTarget();
    }

    public void setTemporaryAutoBrightnessAdjustment(float adjustment) {
        this.mHandler.obtainMessage(7, Float.floatToIntBits(adjustment), 0).sendToTarget();
    }

    private void blockScreenOn() {
        if (this.mPendingScreenOnUnblocker == null) {
            ScreenOnMonitor.getInstance().recordTime(2);
            Trace.asyncTraceBegin(131072, SCREEN_ON_BLOCKED_TRACE_NAME, 0);
            this.mPendingScreenOnUnblocker = new ScreenOnUnblocker();
            this.mScreenOnBlockStartRealTime = SystemClock.elapsedRealtime();
            Slog.i(TAG, "Blocking screen on until initial contents have been drawn.");
        }
    }

    /* access modifiers changed from: private */
    public void unblockScreenOn() {
        if (this.mPendingScreenOnUnblocker != null) {
            this.mPendingScreenOnUnblocker = null;
            long delay = SystemClock.elapsedRealtime() - this.mScreenOnBlockStartRealTime;
            Slog.i(TAG, "Unblocked screen on after " + delay + " ms");
            Trace.asyncTraceEnd(131072, SCREEN_ON_BLOCKED_TRACE_NAME, 0);
            ScreenOnMonitor.getInstance().recordTime(3);
        }
    }

    private void blockScreenOff() {
        if (this.mPendingScreenOffUnblocker == null) {
            Trace.asyncTraceBegin(131072, SCREEN_OFF_BLOCKED_TRACE_NAME, 0);
            this.mPendingScreenOffUnblocker = new ScreenOffUnblocker();
            this.mScreenOffBlockStartRealTime = SystemClock.elapsedRealtime();
            Slog.i(TAG, "Blocking screen off");
        }
    }

    /* access modifiers changed from: private */
    public void unblockScreenOff() {
        if (this.mPendingScreenOffUnblocker != null) {
            this.mPendingScreenOffUnblocker = null;
            long delay = SystemClock.elapsedRealtime() - this.mScreenOffBlockStartRealTime;
            Slog.i(TAG, "Unblocked screen off after " + delay + " ms");
            Trace.asyncTraceEnd(131072, SCREEN_OFF_BLOCKED_TRACE_NAME, 0);
        }
    }

    private boolean setScreenState(int state) {
        return setScreenState(state, false);
    }

    private boolean setScreenState(int state, boolean reportOnly) {
        boolean isOff = state == 1;
        if (this.mPowerState.getScreenState() != state) {
            if (isOff && !this.mScreenOffBecauseOfProximity) {
                if (this.mReportedScreenStateToPolicy == 2) {
                    setReportedScreenState(3);
                    blockScreenOff();
                    this.mWindowManagerPolicy.screenTurningOff(this.mPendingScreenOffUnblocker);
                    unblockScreenOff();
                } else if (this.mPendingScreenOffUnblocker != null) {
                    return false;
                }
            }
            if (!reportOnly) {
                Trace.traceCounter(131072, "ScreenState", state);
                this.mPowerState.setScreenState(state);
                try {
                    this.mBatteryStats.noteScreenState(state);
                } catch (RemoteException e) {
                }
            }
        }
        if (isOff && this.mReportedScreenStateToPolicy != 0 && !this.mScreenOffBecauseOfProximity) {
            setReportedScreenState(0);
            unblockScreenOn();
            this.mWindowManagerPolicy.screenTurnedOff();
        } else if (!isOff && this.mReportedScreenStateToPolicy == 3) {
            unblockScreenOff();
            this.mWindowManagerPolicy.screenTurnedOff();
            setReportedScreenState(0);
        }
        if (!isOff && this.mReportedScreenStateToPolicy == 0) {
            setReportedScreenState(1);
            if (this.mPowerState.getColorFadeLevel() == 0.0f) {
                blockScreenOn();
            } else {
                unblockScreenOn();
            }
            this.mWindowManagerPolicy.screenTurningOn(this.mPendingScreenOnUnblocker);
        }
        if (this.mPendingScreenOnUnblocker == null) {
            return true;
        }
        return false;
    }

    private void setReportedScreenState(int state) {
        Trace.traceCounter(131072, "ReportedScreenStateToPolicy", state);
        this.mReportedScreenStateToPolicy = state;
    }

    private int clampScreenBrightnessForVr(int value) {
        return MathUtils.constrain(value, this.mScreenBrightnessForVrRangeMinimum, this.mScreenBrightnessForVrRangeMaximum);
    }

    private int clampScreenBrightness(int value) {
        return MathUtils.constrain(value, this.mScreenBrightnessRangeMinimum, this.mScreenBrightnessRangeMaximum);
    }

    private void animateScreenBrightness(int target, int rate) {
        if (DEBUG) {
            Slog.d(TAG, "Animating brightness: target=" + target + ", rate=" + rate);
        }
        if (target >= 0) {
            if (this.mScreenBrightnessRampAnimator.animateTo(target, BackLightController.adjustBackLightRate(rate))) {
                Trace.traceCounter(131072, "TargetScreenBrightness", target);
                try {
                    this.mBatteryStats.noteScreenBrightness(target);
                } catch (RemoteException e) {
                }
            }
        }
    }

    private void animateScreenStateChange(int target, boolean performScreenOffTransition) {
        int i = 2;
        if (this.mColorFadeEnabled && (this.mColorFadeOnAnimator.isStarted() || this.mColorFadeOffAnimator.isStarted())) {
            if (target == 2) {
                this.mPendingScreenOff = false;
            } else {
                return;
            }
        }
        if (this.mDisplayBlanksAfterDozeConfig && Display.isDozeState(this.mPowerState.getScreenState()) && !Display.isDozeState(target)) {
            this.mPowerState.prepareColorFade(this.mContext, this.mColorFadeFadesConfig ? 2 : 0);
            ObjectAnimator objectAnimator = this.mColorFadeOffAnimator;
            if (objectAnimator != null) {
                objectAnimator.end();
            }
            setScreenState(1, target != 1);
        }
        if (this.mPendingScreenOff && target != 1) {
            setScreenState(1);
            this.mPendingScreenOff = false;
            this.mPowerState.dismissColorFadeResources();
        }
        if (target == 2) {
            if (setScreenState(2)) {
                this.mPowerState.setColorFadeLevel(1.0f);
                this.mPowerState.dismissColorFade();
            }
        } else if (target == 5) {
            if ((!this.mScreenBrightnessRampAnimator.isAnimating() || this.mPowerState.getScreenState() != 2) && setScreenState(5)) {
                this.mPowerState.setColorFadeLevel(1.0f);
                this.mPowerState.dismissColorFade();
            }
        } else if (target == 3) {
            if ((!this.mScreenBrightnessRampAnimator.isAnimating() || this.mPowerState.getScreenState() != 2) && setScreenState(3)) {
                this.mPowerState.setColorFadeLevel(1.0f);
                this.mPowerState.dismissColorFade();
            }
        } else if (target == 4) {
            if (!this.mScreenBrightnessRampAnimator.isAnimating() || this.mPowerState.getScreenState() == 4) {
                if (this.mPowerState.getScreenState() != 4) {
                    if (setScreenState(3)) {
                        setScreenState(4);
                    } else {
                        return;
                    }
                }
                this.mPowerState.setColorFadeLevel(1.0f);
                this.mPowerState.dismissColorFade();
            }
        } else if (target != 6) {
            this.mPendingScreenOff = true;
            if (!this.mColorFadeEnabled) {
                this.mPowerState.setColorFadeLevel(0.0f);
            }
            if (this.mPowerState.getColorFadeLevel() == 0.0f) {
                setScreenState(1);
                this.mPendingScreenOff = false;
                this.mPowerState.dismissColorFadeResources();
                return;
            }
            if (performScreenOffTransition) {
                DisplayPowerState displayPowerState = this.mPowerState;
                Context context = this.mContext;
                if (!this.mColorFadeFadesConfig) {
                    i = 1;
                }
                if (displayPowerState.prepareColorFade(context, i) && this.mPowerState.getScreenState() != 1) {
                    this.mColorFadeOffAnimator.start();
                    return;
                }
            }
            this.mColorFadeOffAnimator.end();
        } else if (!this.mScreenBrightnessRampAnimator.isAnimating() || this.mPowerState.getScreenState() == 6) {
            if (this.mPowerState.getScreenState() != 6) {
                if (setScreenState(2)) {
                    setScreenState(6);
                } else {
                    return;
                }
            }
            this.mPowerState.setColorFadeLevel(1.0f);
            this.mPowerState.dismissColorFade();
        }
    }

    private void setProximitySensorEnabled(boolean enable) {
        if (enable) {
            if (!this.mProximitySensorEnabled) {
                this.mProximitySensorEnabled = true;
                this.mSensorManager.registerListener(this.mProximitySensorListener, this.mProximitySensor, 3, this.mHandler);
            }
        } else if (this.mProximitySensorEnabled) {
            this.mProximitySensorEnabled = false;
            this.mProximity = -1;
            this.mPendingProximity = -1;
            this.mHandler.removeMessages(2);
            this.mSensorManager.unregisterListener(this.mProximitySensorListener);
            clearPendingProximityDebounceTime();
        }
    }

    /* access modifiers changed from: private */
    public void handleProximitySensorEvent(long time, boolean positive) {
        if (!this.mProximitySensorEnabled) {
            return;
        }
        if (this.mPendingProximity == 0 && !positive) {
            return;
        }
        if (this.mPendingProximity != 1 || !positive) {
            this.mHandler.removeMessages(2);
            if (positive) {
                this.mPendingProximity = 1;
                setPendingProximityDebounceTime(0 + time);
            } else {
                this.mPendingProximity = 0;
                setPendingProximityDebounceTime(0 + time);
            }
            debounceProximitySensor();
        }
    }

    /* access modifiers changed from: private */
    public void debounceProximitySensor() {
        if (this.mProximitySensorEnabled && this.mPendingProximity != -1 && this.mPendingProximityDebounceTime >= 0) {
            if (this.mPendingProximityDebounceTime <= SystemClock.uptimeMillis()) {
                this.mProximity = this.mPendingProximity;
                updatePowerState();
                BrightnessTracker brightnessTracker = this.mBrightnessTracker;
                if (brightnessTracker != null) {
                    boolean z = true;
                    if (this.mProximity != 1) {
                        z = false;
                    }
                    brightnessTracker.adjustSensorListenerWithProximity(z);
                }
                clearPendingProximityDebounceTime();
                return;
            }
            this.mHandler.sendMessageAtTime(this.mHandler.obtainMessage(2), this.mPendingProximityDebounceTime);
        }
    }

    private void clearPendingProximityDebounceTime() {
        if (this.mPendingProximityDebounceTime >= 0) {
            this.mPendingProximityDebounceTime = -1;
            this.mCallbacks.releaseSuspendBlocker();
        }
    }

    private void setPendingProximityDebounceTime(long debounceTime) {
        if (this.mPendingProximityDebounceTime < 0) {
            this.mCallbacks.acquireSuspendBlocker();
        }
        this.mPendingProximityDebounceTime = debounceTime;
    }

    private void sendOnStateChangedWithWakelock() {
        this.mCallbacks.acquireSuspendBlocker();
        this.mHandler.post(this.mOnStateChangedRunnable);
    }

    private void logDisplayPolicyChanged(int newPolicy) {
        LogMaker log = new LogMaker(1696);
        log.setType(6);
        log.setSubtype(newPolicy);
        MetricsLogger.action(log);
    }

    /* access modifiers changed from: private */
    public void handleSettingsChange(boolean userSwitch) {
        this.mPendingScreenBrightnessSetting = getScreenBrightnessSetting();
        this.mPendingAutoBrightnessAdjustment = getAutoBrightnessAdjustmentSetting();
        if (userSwitch) {
            this.mCurrentScreenBrightnessSetting = this.mPendingScreenBrightnessSetting;
            this.mAutoBrightnessAdjustment = this.mPendingAutoBrightnessAdjustment;
            AutomaticBrightnessController automaticBrightnessController = this.mAutomaticBrightnessController;
            if (automaticBrightnessController != null) {
                automaticBrightnessController.resetShortTermModel();
            }
        }
        this.mScreenBrightnessForVr = getScreenBrightnessForVrSetting();
    }

    private float getAutoBrightnessAdjustmentSetting() {
        float adj = Settings.System.getFloatForUser(this.mContext.getContentResolver(), "screen_auto_brightness_adj", 0.0f, -2);
        if (Float.isNaN(adj)) {
            return 0.0f;
        }
        return clampAutoBrightnessAdjustment(adj);
    }

    private int getScreenBrightnessSetting() {
        return clampAbsoluteBrightness(Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_brightness", this.mScreenBrightnessDefault, -2));
    }

    private int getScreenBrightnessForVrSetting() {
        return clampScreenBrightnessForVr(Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_brightness_for_vr", this.mScreenBrightnessForVrDefault, -2));
    }

    /* access modifiers changed from: private */
    public void putScreenBrightnessSetting(int brightness) {
        this.mCurrentScreenBrightnessSetting = brightness;
        Settings.System.putIntForUser(this.mContext.getContentResolver(), "screen_brightness", brightness, -2);
    }

    private void putAutoBrightnessAdjustmentSetting(float adjustment) {
        this.mAutoBrightnessAdjustment = adjustment;
        Settings.System.putFloatForUser(this.mContext.getContentResolver(), "screen_auto_brightness_adj", adjustment, -2);
    }

    private boolean updateAutoBrightnessAdjustment() {
        if (Float.isNaN(this.mPendingAutoBrightnessAdjustment)) {
            return false;
        }
        float f = this.mAutoBrightnessAdjustment;
        float f2 = this.mPendingAutoBrightnessAdjustment;
        if (f == f2) {
            this.mPendingAutoBrightnessAdjustment = Float.NaN;
            return false;
        }
        this.mAutoBrightnessAdjustment = f2;
        this.mPendingAutoBrightnessAdjustment = Float.NaN;
        return true;
    }

    private boolean updateUserSetScreenBrightness() {
        int i = this.mPendingScreenBrightnessSetting;
        if (i < 0) {
            return false;
        }
        if (this.mCurrentScreenBrightnessSetting == i) {
            this.mPendingScreenBrightnessSetting = -1;
            this.mTemporaryScreenBrightness = -1;
            return false;
        }
        this.mCurrentScreenBrightnessSetting = i;
        this.mLastUserSetScreenBrightness = i;
        this.mPendingScreenBrightnessSetting = -1;
        this.mTemporaryScreenBrightness = -1;
        return true;
    }

    private void notifyBrightnessChanged(int brightness, boolean userInitiated, boolean hadUserDataPoint) {
        float powerFactor;
        float brightnessInNits = convertToNits(brightness);
        if (this.mPowerRequest.useAutoBrightness && brightnessInNits >= 0.0f && this.mAutomaticBrightnessController != null) {
            if (this.mPowerRequest.lowPowerMode) {
                powerFactor = this.mPowerRequest.screenLowPowerBrightnessFactor;
            } else {
                powerFactor = 1.0f;
            }
            this.mBrightnessTracker.notifyBrightnessChanged(brightnessInNits, userInitiated, powerFactor, hadUserDataPoint, this.mAutomaticBrightnessController.isDefaultConfig());
        }
    }

    private float convertToNits(int backlight) {
        BrightnessMappingStrategy brightnessMappingStrategy = this.mBrightnessMapper;
        if (brightnessMappingStrategy != null) {
            return brightnessMappingStrategy.convertToNits(backlight);
        }
        return -1.0f;
    }

    private void sendOnProximityPositiveWithWakelock() {
        this.mCallbacks.acquireSuspendBlocker();
        this.mHandler.post(this.mOnProximityPositiveRunnable);
    }

    private void sendOnProximityNegativeWithWakelock() {
        this.mCallbacks.acquireSuspendBlocker();
        this.mHandler.post(this.mOnProximityNegativeRunnable);
    }

    public void dump(final PrintWriter pw) {
        DEBUG = AutomaticBrightnessControllerInjector.isDebuggable();
        synchronized (this.mLock) {
            pw.println();
            pw.println("Display Power Controller Locked State:");
            pw.println("  mDisplayReadyLocked=" + this.mDisplayReadyLocked);
            pw.println("  mPendingRequestLocked=" + this.mPendingRequestLocked);
            pw.println("  mPendingRequestChangedLocked=" + this.mPendingRequestChangedLocked);
            pw.println("  mPendingWaitForNegativeProximityLocked=" + this.mPendingWaitForNegativeProximityLocked);
            pw.println("  mPendingUpdatePowerStateLocked=" + this.mPendingUpdatePowerStateLocked);
        }
        pw.println();
        pw.println("Display Power Controller Configuration:");
        pw.println("  mScreenBrightnessDozeConfig=" + this.mScreenBrightnessDozeConfig);
        pw.println("  mScreenBrightnessDimConfig=" + this.mScreenBrightnessDimConfig);
        pw.println("  mScreenBrightnessRangeMinimum=" + this.mScreenBrightnessRangeMinimum);
        pw.println("  mScreenBrightnessRangeMaximum=" + this.mScreenBrightnessRangeMaximum);
        pw.println("  mScreenBrightnessDefault=" + this.mScreenBrightnessDefault);
        pw.println("  mScreenBrightnessForVrRangeMinimum=" + this.mScreenBrightnessForVrRangeMinimum);
        pw.println("  mScreenBrightnessForVrRangeMaximum=" + this.mScreenBrightnessForVrRangeMaximum);
        pw.println("  mScreenBrightnessForVrDefault=" + this.mScreenBrightnessForVrDefault);
        pw.println("  mUseSoftwareAutoBrightnessConfig=" + this.mUseSoftwareAutoBrightnessConfig);
        pw.println("  mAllowAutoBrightnessWhileDozingConfig=" + this.mAllowAutoBrightnessWhileDozingConfig);
        pw.println("  mBrightnessRampRateFast=" + this.mBrightnessRampRateFast);
        pw.println("  mBrightnessRampRateSlow=" + this.mBrightnessRampRateSlow);
        pw.println("  mSkipScreenOnBrightnessRamp=" + this.mSkipScreenOnBrightnessRamp);
        pw.println("  mColorFadeFadesConfig=" + this.mColorFadeFadesConfig);
        pw.println("  mColorFadeEnabled=" + this.mColorFadeEnabled);
        pw.println("  mDisplayBlanksAfterDozeConfig=" + this.mDisplayBlanksAfterDozeConfig);
        pw.println("  mBrightnessBucketsInDozeConfig=" + this.mBrightnessBucketsInDozeConfig);
        this.mHandler.runWithScissors(new Runnable() {
            public void run() {
                DisplayPowerController.this.dumpLocal(pw);
            }
        }, 1000);
    }

    /* access modifiers changed from: private */
    public void dumpLocal(PrintWriter pw) {
        pw.println();
        pw.println("Display Power Controller Thread State:");
        pw.println("  mPowerRequest=" + this.mPowerRequest);
        pw.println("  mUnfinishedBusiness=" + this.mUnfinishedBusiness);
        pw.println("  mWaitingForNegativeProximity=" + this.mWaitingForNegativeProximity);
        pw.println("  mProximitySensor=" + this.mProximitySensor);
        pw.println("  mProximitySensorEnabled=" + this.mProximitySensorEnabled);
        pw.println("  mProximityThreshold=" + this.mProximityThreshold);
        pw.println("  mProximity=" + proximityToString(this.mProximity));
        pw.println("  mPendingProximity=" + proximityToString(this.mPendingProximity));
        pw.println("  mPendingProximityDebounceTime=" + TimeUtils.formatUptime(this.mPendingProximityDebounceTime));
        pw.println("  mScreenOffBecauseOfProximity=" + this.mScreenOffBecauseOfProximity);
        pw.println("  mLastUserSetScreenBrightness=" + this.mLastUserSetScreenBrightness);
        pw.println("  mCurrentScreenBrightnessSetting=" + this.mCurrentScreenBrightnessSetting);
        pw.println("  mPendingScreenBrightnessSetting=" + this.mPendingScreenBrightnessSetting);
        pw.println("  mTemporaryScreenBrightness=" + this.mTemporaryScreenBrightness);
        pw.println("  mAutoBrightnessAdjustment=" + this.mAutoBrightnessAdjustment);
        pw.println("  mBrightnessReason=" + this.mBrightnessReason);
        pw.println("  mTemporaryAutoBrightnessAdjustment=" + this.mTemporaryAutoBrightnessAdjustment);
        pw.println("  mTemporaryAutoBrightnessAdjRatio=" + this.mTemporaryAutoBrightnessAdjRatio);
        pw.println("  mPendingAutoBrightnessAdjustment=" + this.mPendingAutoBrightnessAdjustment);
        pw.println("  mScreenBrightnessForVr=" + this.mScreenBrightnessForVr);
        pw.println("  mAppliedAutoBrightness=" + this.mAppliedAutoBrightness);
        pw.println("  mAppliedDimming=" + this.mAppliedDimming);
        pw.println("  mAppliedLowPower=" + this.mAppliedLowPower);
        pw.println("  mAppliedScreenBrightnessOverride=" + this.mAppliedScreenBrightnessOverride);
        pw.println("  mAppliedTemporaryBrightness=" + this.mAppliedTemporaryBrightness);
        pw.println("  mDozing=" + this.mDozing);
        pw.println("  mSkipRampState=" + skipRampStateToString(this.mSkipRampState));
        pw.println("  mInitialAutoBrightness=" + this.mInitialAutoBrightness);
        pw.println("  mScreenOnBlockStartRealTime=" + this.mScreenOnBlockStartRealTime);
        pw.println("  mScreenOffBlockStartRealTime=" + this.mScreenOffBlockStartRealTime);
        pw.println("  mPendingScreenOnUnblocker=" + this.mPendingScreenOnUnblocker);
        pw.println("  mPendingScreenOffUnblocker=" + this.mPendingScreenOffUnblocker);
        pw.println("  mPendingScreenOff=" + this.mPendingScreenOff);
        pw.println("  mReportedToPolicy=" + reportedToPolicyToString(this.mReportedScreenStateToPolicy));
        if (this.mScreenBrightnessRampAnimator != null) {
            pw.println("  mScreenBrightnessRampAnimator.isAnimating()=" + this.mScreenBrightnessRampAnimator.isAnimating());
        }
        if (this.mColorFadeOnAnimator != null) {
            pw.println("  mColorFadeOnAnimator.isStarted()=" + this.mColorFadeOnAnimator.isStarted());
        }
        if (this.mColorFadeOffAnimator != null) {
            pw.println("  mColorFadeOffAnimator.isStarted()=" + this.mColorFadeOffAnimator.isStarted());
        }
        DisplayPowerState displayPowerState = this.mPowerState;
        if (displayPowerState != null) {
            displayPowerState.dump(pw);
        }
        AutomaticBrightnessController automaticBrightnessController = this.mAutomaticBrightnessController;
        if (automaticBrightnessController != null) {
            automaticBrightnessController.dump(pw);
        }
        if (this.mBrightnessTracker != null) {
            pw.println();
            this.mBrightnessTracker.dump(pw);
        }
        pw.println();
        DisplayWhiteBalanceController displayWhiteBalanceController = this.mDisplayWhiteBalanceController;
        if (displayWhiteBalanceController != null) {
            displayWhiteBalanceController.dump(pw);
            this.mDisplayWhiteBalanceSettings.dump(pw);
        }
        this.mInjector.dump(pw);
    }

    private static String proximityToString(int state) {
        if (state == -1) {
            return ProcessPolicy.REASON_UNKNOWN;
        }
        if (state == 0) {
            return "Negative";
        }
        if (state != 1) {
            return Integer.toString(state);
        }
        return "Positive";
    }

    private static String reportedToPolicyToString(int state) {
        if (state == 0) {
            return "REPORTED_TO_POLICY_SCREEN_OFF";
        }
        if (state == 1) {
            return "REPORTED_TO_POLICY_SCREEN_TURNING_ON";
        }
        if (state != 2) {
            return Integer.toString(state);
        }
        return "REPORTED_TO_POLICY_SCREEN_ON";
    }

    private static String skipRampStateToString(int state) {
        if (state == 0) {
            return "RAMP_STATE_SKIP_NONE";
        }
        if (state == 1) {
            return "RAMP_STATE_SKIP_INITIAL";
        }
        if (state != 2) {
            return Integer.toString(state);
        }
        return "RAMP_STATE_SKIP_AUTOBRIGHT";
    }

    private static int clampAbsoluteBrightness(int value) {
        return MathUtils.constrain(value, 0, PowerManager.BRIGHTNESS_ON);
    }

    private static float clampAutoBrightnessAdjustment(float value) {
        return MathUtils.constrain(value, -1.0f, 1.0f);
    }

    private final class DisplayControllerHandler extends Handler {
        public DisplayControllerHandler(Looper looper) {
            super(looper, (Handler.Callback) null, true);
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i != 255) {
                switch (i) {
                    case 1:
                        DisplayPowerController.this.updatePowerState();
                        return;
                    case 2:
                        DisplayPowerController.this.debounceProximitySensor();
                        return;
                    case 3:
                        if (DisplayPowerController.this.mPendingScreenOnUnblocker == msg.obj) {
                            DisplayPowerController.this.unblockScreenOn();
                            DisplayPowerController.this.updatePowerState();
                            return;
                        }
                        return;
                    case 4:
                        if (DisplayPowerController.this.mPendingScreenOffUnblocker == msg.obj) {
                            DisplayPowerController.this.unblockScreenOff();
                            DisplayPowerController.this.updatePowerState();
                            return;
                        }
                        return;
                    case 5:
                        BrightnessConfiguration unused = DisplayPowerController.this.mBrightnessConfiguration = (BrightnessConfiguration) msg.obj;
                        DisplayPowerController.this.updatePowerState();
                        return;
                    case 6:
                        int unused2 = DisplayPowerController.this.mTemporaryScreenBrightness = msg.arg1;
                        DisplayPowerController.this.mInjector.mayBeReportUserDisableSunlightTemporary(DisplayPowerController.this.mTemporaryScreenBrightness);
                        DisplayPowerController.this.updatePowerState();
                        return;
                    case 7:
                        float unused3 = DisplayPowerController.this.mTemporaryAutoBrightnessAdjRatio = Float.intBitsToFloat(msg.arg1);
                        DisplayPowerController.this.updatePowerState();
                        return;
                    case 8:
                        DisplayPowerController.this.handleSettingsChange(true);
                        DisplayPowerController.this.mBrightnessTracker.onSwitchUser(msg.arg1);
                        DisplayPowerController.this.updatePowerState();
                        return;
                    default:
                        return;
                }
            } else {
                if (DisplayPowerController.this.mAutomaticBrightnessController != null) {
                    DisplayPowerController.this.mAutomaticBrightnessController.resetShortTermModel();
                }
                float unused4 = DisplayPowerController.this.mTemporaryAutoBrightnessAdjRatio = 0.0f;
                DisplayPowerController.this.updatePowerState();
            }
        }
    }

    private final class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        public void onChange(boolean selfChange, Uri uri) {
            DisplayPowerController.this.handleSettingsChange(false);
            DisplayPowerController.this.updatePowerState();
        }
    }

    private final class ScreenOnUnblocker implements WindowManagerPolicy.ScreenOnListener {
        private ScreenOnUnblocker() {
        }

        public void onScreenOn(long delayMillis) {
            DisplayPowerController.this.mHandler.sendMessageDelayed(DisplayPowerController.this.mHandler.obtainMessage(3, this), delayMillis);
        }
    }

    private final class ScreenOffUnblocker implements WindowManagerPolicy.ScreenOffListener {
        private ScreenOffUnblocker() {
        }

        public void onScreenOff() {
            DisplayPowerController.this.mHandler.sendMessage(DisplayPowerController.this.mHandler.obtainMessage(4, this));
        }
    }

    /* access modifiers changed from: package-private */
    public void setAutoBrightnessLoggingEnabled(boolean enabled) {
        AutomaticBrightnessController automaticBrightnessController = this.mAutomaticBrightnessController;
        if (automaticBrightnessController != null) {
            automaticBrightnessController.setLoggingEnabled(enabled);
        }
    }

    public void updateWhiteBalance() {
        sendUpdatePowerState();
    }

    /* access modifiers changed from: package-private */
    public void setDisplayWhiteBalanceLoggingEnabled(boolean enabled) {
        DisplayWhiteBalanceController displayWhiteBalanceController = this.mDisplayWhiteBalanceController;
        if (displayWhiteBalanceController != null) {
            displayWhiteBalanceController.setLoggingEnabled(enabled);
            this.mDisplayWhiteBalanceSettings.setLoggingEnabled(enabled);
        }
    }

    /* access modifiers changed from: package-private */
    public void setAmbientColorTemperatureOverride(float cct) {
        DisplayWhiteBalanceController displayWhiteBalanceController = this.mDisplayWhiteBalanceController;
        if (displayWhiteBalanceController != null) {
            displayWhiteBalanceController.setAmbientColorTemperatureOverride(cct);
            sendUpdatePowerState();
        }
    }

    private final class BrightnessReason {
        static final int ADJUSTMENT_AUTO = 2;
        static final int ADJUSTMENT_AUTO_TEMP = 1;
        static final int MODIFIER_DIMMED = 1;
        static final int MODIFIER_LOW_POWER = 2;
        static final int MODIFIER_MASK = 3;
        static final int REASON_AUTOMATIC = 4;
        static final int REASON_BOOST = 9;
        static final int REASON_DOZE = 2;
        static final int REASON_DOZE_DEFAULT = 3;
        static final int REASON_MANUAL = 1;
        static final int REASON_MAX = 10;
        static final int REASON_OVERRIDE = 7;
        static final int REASON_SCREEN_OFF = 5;
        static final int REASON_SUNLIGHT = 10;
        static final int REASON_TEMPORARY = 8;
        static final int REASON_UNKNOWN = 0;
        static final int REASON_VR = 6;
        public int modifier;
        public int reason;

        private BrightnessReason() {
        }

        public void set(BrightnessReason other) {
            int i = 0;
            setReason(other == null ? 0 : other.reason);
            if (other != null) {
                i = other.modifier;
            }
            setModifier(i);
        }

        public void setReason(int reason2) {
            if (reason2 < 0 || reason2 > 10) {
                Slog.w(DisplayPowerController.TAG, "brightness reason out of bounds: " + reason2);
                return;
            }
            this.reason = reason2;
        }

        public void setModifier(int modifier2) {
            if ((modifier2 & -4) != 0) {
                Slog.w(DisplayPowerController.TAG, "brightness modifier out of bounds: 0x" + Integer.toHexString(modifier2));
                return;
            }
            this.modifier = modifier2;
        }

        public void addModifier(int modifier2) {
            setModifier(this.modifier | modifier2);
        }

        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof BrightnessReason)) {
                return false;
            }
            BrightnessReason other = (BrightnessReason) obj;
            if (other.reason == this.reason && other.modifier == this.modifier) {
                return true;
            }
            return false;
        }

        public String toString() {
            return toString(0);
        }

        public String toString(int adjustments) {
            StringBuilder sb = new StringBuilder();
            sb.append(reasonToString(this.reason));
            sb.append(" [");
            if ((adjustments & 1) != 0) {
                sb.append(" temp_adj");
            }
            if ((adjustments & 2) != 0) {
                sb.append(" auto_adj");
            }
            if ((this.modifier & 2) != 0) {
                sb.append(" low_pwr");
            }
            if ((this.modifier & 1) != 0) {
                sb.append(" dim");
            }
            int strlen = sb.length();
            if (sb.charAt(strlen - 1) == '[') {
                sb.setLength(strlen - 2);
            } else {
                sb.append(" ]");
            }
            return sb.toString();
        }

        private String reasonToString(int reason2) {
            switch (reason2) {
                case 1:
                    return "manual";
                case 2:
                    return "doze";
                case 3:
                    return "doze_default";
                case 4:
                    return "automatic";
                case 5:
                    return "screen_off";
                case 6:
                    return "vr";
                case 7:
                    return "override";
                case 8:
                    return "temporary";
                case 9:
                    return "boost";
                case 10:
                    return "sunlight";
                default:
                    return Integer.toString(reason2);
            }
        }
    }

    public Handler getDisplayControllerHandler() {
        return this.mHandler;
    }

    public DisplayPowerState getDisplayPowerState() {
        return this.mPowerState;
    }
}
