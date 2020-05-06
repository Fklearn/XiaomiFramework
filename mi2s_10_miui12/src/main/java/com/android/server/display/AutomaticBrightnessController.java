package com.android.server.display;

import android.app.ActivityManager;
import android.app.ActivityTaskManager;
import android.app.IActivityTaskManager;
import android.app.TaskStackListener;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.display.BrightnessConfiguration;
import android.hardware.display.DisplayManagerInternal;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.Trace;
import android.util.EventLog;
import android.util.MathUtils;
import android.util.Slog;
import android.util.TimeUtils;
import android.view.IRotationWatcher;
import android.view.IWindowManager;
import com.android.internal.os.BackgroundThread;
import com.android.server.EventLogTags;
import com.android.server.job.controllers.JobStatus;
import com.android.server.pm.DumpState;
import java.io.PrintWriter;
import miui.process.ForegroundInfo;
import miui.process.IForegroundInfoListener;
import miui.process.ProcessManager;

class AutomaticBrightnessController {
    private static final int AMBIENT_LIGHT_LONG_HORIZON_MILLIS = 10000;
    private static final long AMBIENT_LIGHT_PREDICTION_TIME_MILLIS = 100;
    private static final int AMBIENT_LIGHT_SHORT_HORIZON_MILLIS = 2000;
    private static final int BRIGHTNESS_ADJUSTMENT_SAMPLE_DEBOUNCE_MILLIS = 10000;
    private static boolean DEBUG = false;
    private static final boolean DEBUG_PRETEND_LIGHT_SENSOR_ABSENT = false;
    private static final int MSG_BRIGHTNESS_ADJUSTMENT_SAMPLE = 2;
    public static final int MSG_FOREGROUND_INFO_CHANGED = 1000;
    private static final int MSG_INVALIDATE_SHORT_TERM_MODEL = 3;
    public static final int MSG_ROTATION_CHANGE = 1001;
    public static final int MSG_ROTATION_CHANGE_DURATION_MILLIS = 30;
    private static final int MSG_UPDATE_AMBIENT_LUX = 1;
    private static final int MSG_UPDATE_FOREGROUND_APP = 4;
    private static final int MSG_UPDATE_FOREGROUND_APP_SYNC = 5;
    private static final String TAG = "AutomaticBrightnessController";
    private static final boolean USE_SCREEN_AUTO_BRIGHTNESS_ADJUSTMENT = true;
    private float SHORT_TERM_MODEL_THRESHOLD_RATIO = 0.6f;
    /* access modifiers changed from: private */
    public IActivityTaskManager mActivityTaskManager;
    private float mAmbientBrighteningThreshold;
    private final HysteresisLevels mAmbientBrightnessThresholds;
    private float mAmbientDarkeningThreshold;
    private final int mAmbientLightHorizon;
    private AmbientLightRingBuffer mAmbientLightRingBuffer;
    private float mAmbientLux;
    /* access modifiers changed from: private */
    public boolean mAmbientLuxValid;
    private final long mBrighteningLightDebounceConfig;
    private int mBrightnessAdjustmentSampleOldBrightness;
    private float mBrightnessAdjustmentSampleOldLux;
    private boolean mBrightnessAdjustmentSamplePending;
    /* access modifiers changed from: private */
    public final BrightnessMappingStrategy mBrightnessMapper;
    private final Callbacks mCallbacks;
    private int mCurrentLightSensorRate;
    private long mDarkeningLightDebounceConfig;
    private int mDisplayPolicy = 0;
    private final float mDozeScaleFactor;
    private int mForegroundAppCategory;
    /* access modifiers changed from: private */
    public String mForegroundAppPackageName;
    public String mForegroundApplicationPackageName;
    private IForegroundInfoListener.Stub mForegroundInfoListener = new IForegroundInfoListener.Stub() {
        public void onForegroundInfoChanged(ForegroundInfo foregroundInfo) throws RemoteException {
            AutomaticBrightnessController.this.mForegroundApplicationPackageName = foregroundInfo.mForegroundPackageName;
            if (BrightnessMappingStrategy.sGameWhiteList.contains(AutomaticBrightnessController.this.mForegroundApplicationPackageName)) {
                Slog.d(AutomaticBrightnessController.TAG, "curpackage = " + foregroundInfo.mForegroundPackageName);
                Message.obtain(AutomaticBrightnessController.this.mHandler, 1000).sendToTarget();
            }
        }
    };
    /* access modifiers changed from: private */
    public AutomaticBrightnessHandler mHandler;
    private final int mInitialLightSensorRate;
    private float mLastObservedLux;
    private long mLastObservedLuxTime;
    private final Sensor mLightSensor;
    private long mLightSensorEnableTime;
    /* access modifiers changed from: private */
    public boolean mLightSensorEnabled;
    private final SensorEventListener mLightSensorListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            if (AutomaticBrightnessController.this.mLightSensorEnabled) {
                long time = SystemClock.uptimeMillis();
                float lux = event.values[0];
                AutomaticBrightnessControllerInjector.checkProximityStatus(lux, AutomaticBrightnessController.this.mAmbientLuxValid);
                AutomaticBrightnessController.this.handleLightSensorEvent(time, lux);
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    private int mLightSensorWarmUpTimeConfig;
    private boolean mLoggingEnabled;
    private final int mNormalLightSensorRate;
    /* access modifiers changed from: private */
    public PackageManager mPackageManager;
    /* access modifiers changed from: private */
    public int mPendingForegroundAppCategory;
    /* access modifiers changed from: private */
    public String mPendingForegroundAppPackageName;
    private int mRecentLightSamples;
    private final boolean mResetAmbientLuxAfterWarmUpConfig;
    private RotationWatcher mRotationWatcher;
    private int mScreenAutoBrightness = -1;
    private float mScreenBrighteningThreshold;
    private final int mScreenBrightnessRangeMaximum;
    private final int mScreenBrightnessRangeMinimum;
    private final HysteresisLevels mScreenBrightnessThresholds;
    private float mScreenDarkeningThreshold;
    private final SensorManager mSensorManager;
    private float mShortTermModelAnchor;
    private long mShortTermModelTimeout;
    private boolean mShortTermModelValid;
    private TaskStackListenerImpl mTaskStackListener;
    private final int mWeightingIntercept;
    private IWindowManager mWindowManager;

    interface Callbacks {
        void updateBrightness();
    }

    /* access modifiers changed from: package-private */
    public float getAmbientLux() {
        return this.mAmbientLux;
    }

    public AutomaticBrightnessController(Callbacks callbacks, Looper looper, SensorManager sensorManager, Sensor lightSensor, BrightnessMappingStrategy mapper, int lightSensorWarmUpTime, int brightnessMin, int brightnessMax, float dozeScaleFactor, int lightSensorRate, int initialLightSensorRate, long brighteningLightDebounceConfig, long darkeningLightDebounceConfig, boolean resetAmbientLuxAfterWarmUpConfig, HysteresisLevels ambientBrightnessThresholds, HysteresisLevels screenBrightnessThresholds, long shortTermModelTimeout, PackageManager packageManager) {
        this.mCallbacks = callbacks;
        this.mSensorManager = sensorManager;
        this.mBrightnessMapper = mapper;
        this.mScreenBrightnessRangeMinimum = brightnessMin;
        this.mScreenBrightnessRangeMaximum = brightnessMax;
        this.mLightSensorWarmUpTimeConfig = lightSensorWarmUpTime;
        this.mDozeScaleFactor = dozeScaleFactor;
        this.mNormalLightSensorRate = lightSensorRate;
        this.mInitialLightSensorRate = initialLightSensorRate;
        this.mCurrentLightSensorRate = -1;
        this.mBrighteningLightDebounceConfig = brighteningLightDebounceConfig;
        this.mDarkeningLightDebounceConfig = darkeningLightDebounceConfig;
        this.mResetAmbientLuxAfterWarmUpConfig = resetAmbientLuxAfterWarmUpConfig;
        this.mAmbientLightHorizon = 10000;
        this.mWeightingIntercept = 10000;
        this.mAmbientBrightnessThresholds = ambientBrightnessThresholds;
        this.mScreenBrightnessThresholds = screenBrightnessThresholds;
        this.mShortTermModelTimeout = shortTermModelTimeout;
        this.mShortTermModelValid = true;
        this.mShortTermModelAnchor = -1.0f;
        this.mHandler = new AutomaticBrightnessHandler(looper);
        this.mAmbientLightRingBuffer = new AmbientLightRingBuffer((long) this.mNormalLightSensorRate, this.mAmbientLightHorizon);
        this.mLightSensor = lightSensor;
        AutomaticBrightnessControllerInjector.initialize(sensorManager);
        this.mRotationWatcher = new RotationWatcher();
        this.mWindowManager = IWindowManager.Stub.asInterface(ServiceManager.getService("window"));
        this.mActivityTaskManager = ActivityTaskManager.getService();
        this.mPackageManager = packageManager;
        this.mTaskStackListener = new TaskStackListenerImpl();
        this.mForegroundAppPackageName = null;
        this.mPendingForegroundAppPackageName = null;
        this.mForegroundAppCategory = -1;
        this.mPendingForegroundAppCategory = -1;
    }

    public boolean setLoggingEnabled(boolean loggingEnabled) {
        if (this.mLoggingEnabled == loggingEnabled) {
            return false;
        }
        this.mBrightnessMapper.setLoggingEnabled(loggingEnabled);
        this.mLoggingEnabled = loggingEnabled;
        return true;
    }

    public int getAutomaticScreenBrightness() {
        if (!this.mAmbientLuxValid) {
            return -1;
        }
        if (this.mDisplayPolicy == 1) {
            return (int) (((float) this.mScreenAutoBrightness) * this.mDozeScaleFactor);
        }
        return this.mScreenAutoBrightness;
    }

    public boolean hasValidAmbientLux() {
        return this.mAmbientLuxValid;
    }

    public float getAutomaticScreenBrightnessAdjustment() {
        return this.mBrightnessMapper.getAutoBrightnessAdjustment();
    }

    public void configure(boolean enable, BrightnessConfiguration configuration, float brightness, boolean userChangedBrightness, float adjustment, boolean userChangedAutoBrightnessAdjustment, int displayPolicy) {
        boolean z = true;
        boolean dozing = displayPolicy == 1;
        boolean changed = setBrightnessConfiguration(configuration) | setDisplayPolicy(displayPolicy);
        if (userChangedAutoBrightnessAdjustment) {
            changed |= setAutoBrightnessAdjustment(adjustment);
        }
        if (userChangedBrightness && enable) {
            changed |= setScreenBrightnessByUser(brightness);
        }
        boolean userInitiatedChange = userChangedBrightness || userChangedAutoBrightnessAdjustment;
        if (userInitiatedChange && enable && !dozing) {
            prepareBrightnessAdjustmentSample();
        }
        boolean changed2 = changed | setLightSensorEnabled(enable && !dozing);
        if (!enable || dozing) {
            z = false;
        }
        this.mScreenAutoBrightness = AutomaticBrightnessControllerInjector.configure(z, this.mScreenAutoBrightness);
        if (changed2) {
            updateAutoBrightness(false, userInitiatedChange);
        }
    }

    public boolean hasUserDataPoints() {
        return this.mBrightnessMapper.hasUserDataPoints();
    }

    public boolean isDefaultConfig() {
        return this.mBrightnessMapper.isDefaultConfig();
    }

    public BrightnessConfiguration getDefaultConfig() {
        return this.mBrightnessMapper.getDefaultConfig();
    }

    private boolean setDisplayPolicy(int policy) {
        if (this.mDisplayPolicy == policy) {
            return false;
        }
        int oldPolicy = this.mDisplayPolicy;
        this.mDisplayPolicy = policy;
        if (this.mLoggingEnabled) {
            Slog.d(TAG, "Display policy transitioning from " + oldPolicy + " to " + policy);
        }
        if (!isInteractivePolicy(policy) && isInteractivePolicy(oldPolicy)) {
            this.mHandler.sendEmptyMessageDelayed(3, this.mShortTermModelTimeout);
            return true;
        } else if (!isInteractivePolicy(policy) || isInteractivePolicy(oldPolicy)) {
            return true;
        } else {
            this.mHandler.removeMessages(3);
            return true;
        }
    }

    private static boolean isInteractivePolicy(int policy) {
        return policy == 3 || policy == 2 || policy == 4;
    }

    private boolean setScreenBrightnessByUser(float brightness) {
        if (!this.mAmbientLuxValid) {
            return false;
        }
        this.mBrightnessMapper.addUserDataPoint(this.mAmbientLux, brightness);
        this.mShortTermModelValid = true;
        this.mShortTermModelAnchor = this.mAmbientLux;
        if (this.mLoggingEnabled) {
            Slog.d(TAG, "ShortTermModel: anchor=" + this.mShortTermModelAnchor);
        }
        return true;
    }

    public void resetShortTermModel() {
        this.mBrightnessMapper.clearUserDataPoints();
        this.mShortTermModelValid = true;
        this.mShortTermModelAnchor = -1.0f;
    }

    /* access modifiers changed from: private */
    public void invalidateShortTermModel() {
        if (this.mLoggingEnabled) {
            Slog.d(TAG, "ShortTermModel: invalidate user data");
        }
        this.mShortTermModelValid = false;
    }

    public boolean setBrightnessConfiguration(BrightnessConfiguration configuration) {
        if (!this.mBrightnessMapper.setBrightnessConfiguration(configuration)) {
            return false;
        }
        resetShortTermModel();
        return true;
    }

    public void dump(PrintWriter pw) {
        pw.println();
        pw.println("Automatic Brightness Controller Configuration:");
        pw.println("  mScreenBrightnessRangeMinimum=" + this.mScreenBrightnessRangeMinimum);
        pw.println("  mScreenBrightnessRangeMaximum=" + this.mScreenBrightnessRangeMaximum);
        pw.println("  mDozeScaleFactor=" + this.mDozeScaleFactor);
        pw.println("  mInitialLightSensorRate=" + this.mInitialLightSensorRate);
        pw.println("  mNormalLightSensorRate=" + this.mNormalLightSensorRate);
        pw.println("  mLightSensorWarmUpTimeConfig=" + this.mLightSensorWarmUpTimeConfig);
        pw.println("  mBrighteningLightDebounceConfig=" + this.mBrighteningLightDebounceConfig);
        pw.println("  mDarkeningLightDebounceConfig=" + this.mDarkeningLightDebounceConfig);
        pw.println("  mResetAmbientLuxAfterWarmUpConfig=" + this.mResetAmbientLuxAfterWarmUpConfig);
        pw.println("  mAmbientLightHorizon=" + this.mAmbientLightHorizon);
        pw.println("  mWeightingIntercept=" + this.mWeightingIntercept);
        pw.println();
        pw.println("Automatic Brightness Controller State:");
        pw.println("  mLightSensor=" + this.mLightSensor);
        pw.println("  mLightSensorEnabled=" + this.mLightSensorEnabled);
        pw.println("  mLightSensorEnableTime=" + TimeUtils.formatUptime(this.mLightSensorEnableTime));
        pw.println("  mCurrentLightSensorRate=" + this.mCurrentLightSensorRate);
        pw.println("  mAmbientLux=" + this.mAmbientLux);
        pw.println("  mAmbientLuxValid=" + this.mAmbientLuxValid);
        pw.println("  mAmbientBrighteningThreshold=" + this.mAmbientBrighteningThreshold);
        pw.println("  mAmbientDarkeningThreshold=" + this.mAmbientDarkeningThreshold);
        pw.println("  mScreenBrighteningThreshold=" + this.mScreenBrighteningThreshold);
        pw.println("  mScreenDarkeningThreshold=" + this.mScreenDarkeningThreshold);
        pw.println("  mLastObservedLux=" + this.mLastObservedLux);
        pw.println("  mLastObservedLuxTime=" + TimeUtils.formatUptime(this.mLastObservedLuxTime));
        pw.println("  mRecentLightSamples=" + this.mRecentLightSamples);
        pw.println("  mAmbientLightRingBuffer=" + this.mAmbientLightRingBuffer);
        pw.println("  mScreenAutoBrightness=" + this.mScreenAutoBrightness);
        pw.println("  mDisplayPolicy=" + DisplayManagerInternal.DisplayPowerRequest.policyToString(this.mDisplayPolicy));
        pw.println("  mShortTermModelTimeout=" + this.mShortTermModelTimeout);
        pw.println("  mShortTermModelAnchor=" + this.mShortTermModelAnchor);
        pw.println("  mShortTermModelValid=" + this.mShortTermModelValid);
        pw.println("  mBrightnessAdjustmentSamplePending=" + this.mBrightnessAdjustmentSamplePending);
        pw.println("  mBrightnessAdjustmentSampleOldLux=" + this.mBrightnessAdjustmentSampleOldLux);
        pw.println("  mBrightnessAdjustmentSampleOldBrightness=" + this.mBrightnessAdjustmentSampleOldBrightness);
        pw.println("  mForegroundAppPackageName=" + this.mForegroundAppPackageName);
        pw.println("  mPendingForegroundAppPackageName=" + this.mPendingForegroundAppPackageName);
        pw.println("  mForegroundAppCategory=" + this.mForegroundAppCategory);
        pw.println("  mPendingForegroundAppCategory=" + this.mPendingForegroundAppCategory);
        pw.println();
        this.mBrightnessMapper.dump(pw);
        pw.println();
        this.mAmbientBrightnessThresholds.dump(pw);
        this.mScreenBrightnessThresholds.dump(pw);
        DEBUG = AutomaticBrightnessControllerInjector.dump(pw);
    }

    private boolean setLightSensorEnabled(boolean enable) {
        if (enable) {
            if (!this.mLightSensorEnabled) {
                this.mLightSensorEnabled = true;
                this.mLightSensorEnableTime = SystemClock.uptimeMillis();
                this.mCurrentLightSensorRate = this.mInitialLightSensorRate;
                registerForegroundAppUpdater();
                this.mSensorManager.registerListener(this.mLightSensorListener, this.mLightSensor, this.mCurrentLightSensorRate * 1000, this.mHandler);
                registerBrightnessCorrectionListener();
                float lux = AutomaticBrightnessControllerInjector.setSensorEnabled(false, this.mLightSensor);
                if (lux >= 0.0f) {
                    handleLightSensorEvent(SystemClock.uptimeMillis(), lux);
                }
                return true;
            }
        } else if (this.mLightSensorEnabled) {
            this.mLightSensorEnabled = false;
            this.mAmbientLuxValid = !this.mResetAmbientLuxAfterWarmUpConfig;
            this.mScreenAutoBrightness = -1;
            this.mRecentLightSamples = 0;
            this.mAmbientLightRingBuffer.clear();
            this.mCurrentLightSensorRate = -1;
            this.mHandler.removeMessages(1);
            unregisterForegroundAppUpdater();
            AutomaticBrightnessControllerInjector.setSensorEnabled(true ^ isInteractivePolicy(this.mDisplayPolicy), this.mLightSensor);
            this.mSensorManager.unregisterListener(this.mLightSensorListener);
            unregisterBrightnessCorrectionListener();
        }
        return false;
    }

    /* access modifiers changed from: private */
    public void handleLightSensorEvent(long time, float lux) {
        Trace.traceCounter(131072, "ALS", (int) lux);
        this.mHandler.removeMessages(1);
        if (this.mAmbientLightRingBuffer.size() == 0) {
            adjustLightSensorRate(this.mNormalLightSensorRate);
        }
        applyLightSensorMeasurement(time, lux);
        updateAmbientLux(time);
    }

    private void applyLightSensorMeasurement(long time, float lux) {
        this.mRecentLightSamples++;
        this.mAmbientLightRingBuffer.prune(time - ((long) this.mAmbientLightHorizon));
        this.mAmbientLightRingBuffer.push(time, lux);
        this.mLastObservedLux = lux;
        this.mLastObservedLuxTime = time;
    }

    private void adjustLightSensorRate(int lightSensorRate) {
        if (lightSensorRate != this.mCurrentLightSensorRate) {
            if (this.mLoggingEnabled) {
                Slog.d(TAG, "adjustLightSensorRate: previousRate=" + this.mCurrentLightSensorRate + ", currentRate=" + lightSensorRate);
            }
            this.mCurrentLightSensorRate = lightSensorRate;
            this.mSensorManager.unregisterListener(this.mLightSensorListener);
            this.mSensorManager.registerListener(this.mLightSensorListener, this.mLightSensor, lightSensorRate * 1000, this.mHandler);
        }
    }

    private boolean setAutoBrightnessAdjustment(float adjustment) {
        return this.mBrightnessMapper.setAutoBrightnessAdjustment(adjustment);
    }

    private void setAmbientLux(float lux) {
        if (this.mLoggingEnabled) {
            Slog.d(TAG, "setAmbientLux(" + lux + ")");
        }
        if (lux < 0.0f) {
            Slog.w(TAG, "Ambient lux was negative, ignoring and setting to 0");
            lux = 0.0f;
        }
        this.mAmbientLightRingBuffer.sort(2000);
        this.mAmbientLux = AutomaticBrightnessControllerInjector.getCurrentLux(lux, this.mAmbientLightRingBuffer.getMaxLux(), this.mAmbientLightRingBuffer.getMiniLux());
        this.mAmbientBrighteningThreshold = this.mAmbientBrightnessThresholds.getBrighteningThreshold(lux);
        this.mAmbientDarkeningThreshold = this.mAmbientBrightnessThresholds.getDarkeningThreshold(lux);
        this.mAmbientDarkeningThreshold = AutomaticBrightnessControllerInjector.getDarkenThreshold(this.mAmbientDarkeningThreshold, this.mAmbientBrighteningThreshold, this.mAmbientLux);
        if (!this.mShortTermModelValid) {
            float f = this.mShortTermModelAnchor;
            if (f != -1.0f) {
                float f2 = this.SHORT_TERM_MODEL_THRESHOLD_RATIO;
                float minAmbientLux = f - (f * f2);
                float maxAmbientLux = f + (f2 * f);
                float f3 = this.mAmbientLux;
                if (minAmbientLux > f3 || f3 > maxAmbientLux) {
                    Slog.d(TAG, "ShortTermModel: reset data, ambient lux is " + this.mAmbientLux + "(" + minAmbientLux + ", " + maxAmbientLux + ")");
                    resetShortTermModel();
                    return;
                }
                if (DEBUG) {
                    Slog.d(TAG, "ShortTermModel: re-validate user data, ambient lux is " + minAmbientLux + " <= " + this.mAmbientLux + " <= " + maxAmbientLux);
                }
                this.mShortTermModelValid = true;
            }
        }
    }

    private float calculateAmbientLux(long now, long horizon) {
        long j = now;
        long j2 = horizon;
        if (this.mLoggingEnabled) {
            Slog.d(TAG, "calculateAmbientLux(" + j + ", " + j2 + ")");
        }
        int N = this.mAmbientLightRingBuffer.size();
        if (N == 0) {
            Slog.e(TAG, "calculateAmbientLux: No ambient light readings available");
            return -1.0f;
        }
        int endIndex = 0;
        long horizonStartTime = j - j2;
        int i = 0;
        while (i < N - 1 && this.mAmbientLightRingBuffer.getTime(i + 1) <= horizonStartTime) {
            endIndex++;
            i++;
        }
        if (this.mLoggingEnabled != 0) {
            Slog.d(TAG, "calculateAmbientLux: selected endIndex=" + endIndex + ", point=(" + this.mAmbientLightRingBuffer.getTime(endIndex) + ", " + this.mAmbientLightRingBuffer.getLux(endIndex) + ")");
        }
        float sum = 0.0f;
        float totalWeight = 0.0f;
        long endTime = AMBIENT_LIGHT_PREDICTION_TIME_MILLIS;
        int i2 = N - 1;
        while (i2 >= endIndex) {
            long eventTime = this.mAmbientLightRingBuffer.getTime(i2);
            if (i2 == endIndex && eventTime < horizonStartTime) {
                eventTime = horizonStartTime;
            }
            long horizonStartTime2 = horizonStartTime;
            int endIndex2 = endIndex;
            long startTime = eventTime - j;
            float weight = calculateWeight(startTime, endTime);
            float lux = this.mAmbientLightRingBuffer.getLux(i2);
            long j3 = eventTime;
            if (this.mLoggingEnabled) {
                Slog.d(TAG, "calculateAmbientLux: [" + startTime + ", " + endTime + "]: lux=" + lux + ", weight=" + weight);
            }
            totalWeight += weight;
            sum += lux * weight;
            endTime = startTime;
            i2--;
            j = now;
            long j4 = horizon;
            endIndex = endIndex2;
            horizonStartTime = horizonStartTime2;
        }
        int i3 = endIndex;
        if (this.mLoggingEnabled) {
            Slog.d(TAG, "calculateAmbientLux: totalWeight=" + totalWeight + ", newAmbientLux=" + (sum / totalWeight));
        }
        return sum / totalWeight;
    }

    private float calculateWeight(long startDelta, long endDelta) {
        return weightIntegral(endDelta) - weightIntegral(startDelta);
    }

    private float weightIntegral(long x) {
        return ((float) x) * ((((float) x) * 0.5f) + ((float) this.mWeightingIntercept));
    }

    private long nextAmbientLightBrighteningTransition(long time) {
        long earliestValidTime = time;
        int i = this.mAmbientLightRingBuffer.size() - 1;
        while (i >= 0 && this.mAmbientLightRingBuffer.getLux(i) > this.mAmbientBrighteningThreshold) {
            earliestValidTime = this.mAmbientLightRingBuffer.getTime(i);
            i--;
        }
        return this.mBrighteningLightDebounceConfig + earliestValidTime;
    }

    private long nextAmbientLightDarkeningTransition(long time) {
        long earliestValidTime = time;
        int i = this.mAmbientLightRingBuffer.size() - 1;
        while (i >= 0 && this.mAmbientLightRingBuffer.getLux(i) < this.mAmbientDarkeningThreshold) {
            earliestValidTime = this.mAmbientLightRingBuffer.getTime(i);
            i--;
        }
        this.mDarkeningLightDebounceConfig = AutomaticBrightnessControllerInjector.updateDarkeningDebounce(this.mAmbientLux, this.mAmbientLightRingBuffer.getMaxLux(), this.mAmbientLightRingBuffer.getMiniLux());
        return this.mDarkeningLightDebounceConfig + earliestValidTime;
    }

    /* access modifiers changed from: private */
    public void updateAmbientLux() {
        long time = SystemClock.uptimeMillis();
        this.mAmbientLightRingBuffer.prune(time - ((long) this.mAmbientLightHorizon));
        updateAmbientLux(time);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:41:0x011e, code lost:
        if (r12 <= r1) goto L_0x0128;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0126, code lost:
        if (com.android.server.display.AutomaticBrightnessControllerInjector.checkSkipDebounceStatus(r0.mLightSensorEnableTime, r1, r5) != false) goto L_0x0128;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateAmbientLux(long r19) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            boolean r3 = r0.mAmbientLuxValid
            java.lang.String r4 = ", mAmbientLux="
            r5 = 2000(0x7d0, double:9.88E-321)
            r7 = 0
            java.lang.String r8 = "AutomaticBrightnessController"
            r9 = 1
            if (r3 != 0) goto L_0x0070
            int r3 = r0.mLightSensorWarmUpTimeConfig
            long r10 = (long) r3
            long r12 = r0.mLightSensorEnableTime
            long r10 = r10 + r12
            int r3 = (r1 > r10 ? 1 : (r1 == r10 ? 0 : -1))
            if (r3 >= 0) goto L_0x0041
            boolean r3 = r0.mLoggingEnabled
            if (r3 == 0) goto L_0x003b
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "updateAmbientLux: Sensor not ready yet: time="
            r3.append(r4)
            r3.append(r1)
            java.lang.String r4 = ", timeWhenSensorWarmedUp="
            r3.append(r4)
            r3.append(r10)
            java.lang.String r3 = r3.toString()
            android.util.Slog.d(r8, r3)
        L_0x003b:
            com.android.server.display.AutomaticBrightnessController$AutomaticBrightnessHandler r3 = r0.mHandler
            r3.sendEmptyMessageAtTime(r9, r10)
            return
        L_0x0041:
            float r3 = r0.calculateAmbientLux(r1, r5)
            r0.setAmbientLux(r3)
            r0.mAmbientLuxValid = r9
            boolean r3 = r0.mLoggingEnabled
            if (r3 == 0) goto L_0x006d
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r12 = "updateAmbientLux: Initializing: mAmbientLightRingBuffer="
            r3.append(r12)
            com.android.server.display.AutomaticBrightnessController$AmbientLightRingBuffer r12 = r0.mAmbientLightRingBuffer
            r3.append(r12)
            r3.append(r4)
            float r12 = r0.mAmbientLux
            r3.append(r12)
            java.lang.String r3 = r3.toString()
            android.util.Slog.d(r8, r3)
        L_0x006d:
            r0.updateAutoBrightness(r9, r7)
        L_0x0070:
            long r10 = r18.nextAmbientLightBrighteningTransition(r19)
            long r12 = r18.nextAmbientLightDarkeningTransition(r19)
            r14 = 10000(0x2710, double:4.9407E-320)
            float r3 = r0.calculateAmbientLux(r1, r14)
            float r5 = r0.calculateAmbientLux(r1, r5)
            boolean r6 = r0.mAmbientLuxValid
            boolean r6 = com.android.server.display.AutomaticBrightnessControllerInjector.checkProximityStatus(r5, r6)
            if (r6 == 0) goto L_0x0104
            float r6 = r0.mAmbientDarkeningThreshold
            int r14 = (r3 > r6 ? 1 : (r3 == r6 ? 0 : -1))
            if (r14 > 0) goto L_0x0104
            int r6 = (r5 > r6 ? 1 : (r5 == r6 ? 0 : -1))
            if (r6 > 0) goto L_0x0104
            int r6 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r6 > 0) goto L_0x0104
            r0.setAmbientLux(r5)
            long r10 = r18.nextAmbientLightBrighteningTransition(r19)
            long r12 = r18.nextAmbientLightDarkeningTransition(r19)
            long r14 = java.lang.Math.min(r12, r10)
            int r4 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x00af
            r16 = r10
            r9 = r14
            goto L_0x00b5
        L_0x00af:
            int r4 = r0.mNormalLightSensorRate
            r16 = r10
            long r9 = (long) r4
            long r9 = r9 + r1
        L_0x00b5:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r11 = "drop lux due to touch or prox event occuredwhen darking happened next update time: "
            r4.append(r11)
            r4.append(r9)
            java.lang.String r11 = android.util.TimeUtils.formatUptime(r9)
            r4.append(r11)
            java.lang.String r4 = r4.toString()
            android.util.Slog.d(r8, r4)
            boolean r4 = DEBUG
            if (r4 == 0) goto L_0x00fd
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r11 = "lux info: { "
            r4.append(r11)
            com.android.server.display.AutomaticBrightnessController$AmbientLightRingBuffer r11 = r0.mAmbientLightRingBuffer
            float r7 = r11.getLux(r7)
            r4.append(r7)
            java.lang.String r7 = ", "
            r4.append(r7)
            r4.append(r5)
            java.lang.String r7 = "}"
            r4.append(r7)
            java.lang.String r4 = r4.toString()
            android.util.Slog.d(r8, r4)
        L_0x00fd:
            com.android.server.display.AutomaticBrightnessController$AutomaticBrightnessHandler r4 = r0.mHandler
            r6 = 1
            r4.sendEmptyMessageAtTime(r6, r9)
            return
        L_0x0104:
            float r9 = r0.mAmbientBrighteningThreshold
            int r14 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r14 < 0) goto L_0x0112
            int r9 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1))
            if (r9 < 0) goto L_0x0112
            int r9 = (r10 > r1 ? 1 : (r10 == r1 ? 0 : -1))
            if (r9 <= 0) goto L_0x0128
        L_0x0112:
            float r9 = r0.mAmbientDarkeningThreshold
            int r14 = (r3 > r9 ? 1 : (r3 == r9 ? 0 : -1))
            if (r14 > 0) goto L_0x0120
            int r9 = (r5 > r9 ? 1 : (r5 == r9 ? 0 : -1))
            if (r9 > 0) goto L_0x0120
            int r9 = (r12 > r1 ? 1 : (r12 == r1 ? 0 : -1))
            if (r9 <= 0) goto L_0x0128
        L_0x0120:
            long r14 = r0.mLightSensorEnableTime
            boolean r9 = com.android.server.display.AutomaticBrightnessControllerInjector.checkSkipDebounceStatus(r14, r1, r5)
            if (r9 == 0) goto L_0x017e
        L_0x0128:
            float r9 = r0.mAmbientBrighteningThreshold
            float r14 = r0.mAmbientDarkeningThreshold
            com.android.server.display.AutomaticBrightnessControllerInjector.checkBrightening(r5, r9, r14)
            r0.setAmbientLux(r5)
            boolean r9 = r0.mLoggingEnabled
            if (r9 == 0) goto L_0x0172
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r14 = "updateAmbientLux: "
            r9.append(r14)
            float r14 = r0.mAmbientLux
            int r14 = (r5 > r14 ? 1 : (r5 == r14 ? 0 : -1))
            if (r14 <= 0) goto L_0x014a
            java.lang.String r14 = "Brightened"
            goto L_0x014c
        L_0x014a:
            java.lang.String r14 = "Darkened"
        L_0x014c:
            r9.append(r14)
            java.lang.String r14 = ": mAmbientBrighteningThreshold="
            r9.append(r14)
            float r14 = r0.mAmbientBrighteningThreshold
            r9.append(r14)
            java.lang.String r14 = ", mAmbientLightRingBuffer="
            r9.append(r14)
            com.android.server.display.AutomaticBrightnessController$AmbientLightRingBuffer r14 = r0.mAmbientLightRingBuffer
            r9.append(r14)
            r9.append(r4)
            float r4 = r0.mAmbientLux
            r9.append(r4)
            java.lang.String r4 = r9.toString()
            android.util.Slog.d(r8, r4)
        L_0x0172:
            r4 = 1
            r0.updateAutoBrightness(r4, r7)
            long r10 = r18.nextAmbientLightBrighteningTransition(r19)
            long r12 = r18.nextAmbientLightDarkeningTransition(r19)
        L_0x017e:
            long r14 = java.lang.Math.min(r12, r10)
            int r4 = (r14 > r1 ? 1 : (r14 == r1 ? 0 : -1))
            if (r4 <= 0) goto L_0x0188
            r6 = r14
            goto L_0x018c
        L_0x0188:
            int r4 = r0.mNormalLightSensorRate
            long r6 = (long) r4
            long r6 = r6 + r1
        L_0x018c:
            boolean r4 = r0.mLoggingEnabled
            if (r4 == 0) goto L_0x01ac
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r14 = "updateAmbientLux: Scheduling ambient lux update for "
            r4.append(r14)
            r4.append(r6)
            java.lang.String r14 = android.util.TimeUtils.formatUptime(r6)
            r4.append(r14)
            java.lang.String r4 = r4.toString()
            android.util.Slog.d(r8, r4)
        L_0x01ac:
            com.android.server.display.AutomaticBrightnessController$AutomaticBrightnessHandler r4 = r0.mHandler
            r8 = 1
            r4.sendEmptyMessageAtTime(r8, r6)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.AutomaticBrightnessController.updateAmbientLux(long):void");
    }

    /* access modifiers changed from: private */
    public void updateAutoBrightness(boolean sendUpdate, boolean isManuallySet) {
        if (this.mAmbientLuxValid) {
            int newScreenAutoBrightness = AutomaticBrightnessControllerInjector.changeBrightness(this.mAmbientLux, clampScreenBrightness(Math.round(((float) PowerManager.BRIGHTNESS_ON) * this.mBrightnessMapper.getBrightness(this.mAmbientLux, this.mForegroundApplicationPackageName))));
            if (this.mScreenAutoBrightness == -1 || isManuallySet || ((float) newScreenAutoBrightness) <= this.mScreenDarkeningThreshold || ((float) newScreenAutoBrightness) >= this.mScreenBrighteningThreshold) {
                if (this.mScreenAutoBrightness != newScreenAutoBrightness) {
                    Slog.d(TAG, "updateAutoBrightness: mScreenAutoBrightness=" + this.mScreenAutoBrightness + ", newScreenAutoBrightness=" + newScreenAutoBrightness);
                    this.mScreenAutoBrightness = newScreenAutoBrightness;
                    this.mScreenBrighteningThreshold = this.mScreenBrightnessThresholds.getBrighteningThreshold((float) newScreenAutoBrightness);
                    this.mScreenDarkeningThreshold = this.mScreenBrightnessThresholds.getDarkeningThreshold((float) newScreenAutoBrightness);
                    if (sendUpdate) {
                        this.mCallbacks.updateBrightness();
                    }
                }
            } else if (this.mLoggingEnabled) {
                Slog.d(TAG, "ignoring newScreenAutoBrightness: " + this.mScreenDarkeningThreshold + " < " + newScreenAutoBrightness + " < " + this.mScreenBrighteningThreshold);
            }
        }
    }

    private int clampScreenBrightness(int value) {
        return MathUtils.constrain(value, this.mScreenBrightnessRangeMinimum, this.mScreenBrightnessRangeMaximum);
    }

    private void prepareBrightnessAdjustmentSample() {
        if (!this.mBrightnessAdjustmentSamplePending) {
            this.mBrightnessAdjustmentSamplePending = true;
            this.mBrightnessAdjustmentSampleOldLux = this.mAmbientLuxValid ? this.mAmbientLux : -1.0f;
            this.mBrightnessAdjustmentSampleOldBrightness = this.mScreenAutoBrightness;
        } else {
            this.mHandler.removeMessages(2);
        }
        this.mHandler.sendEmptyMessageDelayed(2, JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY);
    }

    private void cancelBrightnessAdjustmentSample() {
        if (this.mBrightnessAdjustmentSamplePending) {
            this.mBrightnessAdjustmentSamplePending = false;
            this.mHandler.removeMessages(2);
        }
    }

    /* access modifiers changed from: private */
    public void collectBrightnessAdjustmentSample() {
        if (this.mBrightnessAdjustmentSamplePending) {
            this.mBrightnessAdjustmentSamplePending = false;
            if (this.mAmbientLuxValid && this.mScreenAutoBrightness >= 0) {
                if (this.mLoggingEnabled) {
                    Slog.d(TAG, "Auto-brightness adjustment changed by user: lux=" + this.mAmbientLux + ", brightness=" + this.mScreenAutoBrightness + ", ring=" + this.mAmbientLightRingBuffer);
                }
                EventLog.writeEvent(EventLogTags.AUTO_BRIGHTNESS_ADJ, new Object[]{Float.valueOf(this.mBrightnessAdjustmentSampleOldLux), Integer.valueOf(this.mBrightnessAdjustmentSampleOldBrightness), Float.valueOf(this.mAmbientLux), Integer.valueOf(this.mScreenAutoBrightness)});
            }
        }
    }

    private void registerForegroundAppUpdater() {
        try {
            this.mActivityTaskManager.registerTaskStackListener(this.mTaskStackListener);
            updateForegroundApp();
        } catch (RemoteException e) {
            if (this.mLoggingEnabled) {
                Slog.e(TAG, "Failed to register foreground app updater: " + e);
            }
        }
    }

    private void unregisterForegroundAppUpdater() {
        try {
            this.mActivityTaskManager.unregisterTaskStackListener(this.mTaskStackListener);
        } catch (RemoteException e) {
        }
        this.mForegroundAppPackageName = null;
        this.mForegroundAppCategory = -1;
    }

    /* access modifiers changed from: private */
    public void updateForegroundApp() {
        if (this.mLoggingEnabled) {
            Slog.d(TAG, "Attempting to update foreground app");
        }
        BackgroundThread.getHandler().post(new Runnable() {
            public void run() {
                try {
                    ActivityManager.StackInfo info = AutomaticBrightnessController.this.mActivityTaskManager.getFocusedStackInfo();
                    if (info == null) {
                        return;
                    }
                    if (info.topActivity != null) {
                        String packageName = info.topActivity.getPackageName();
                        if (AutomaticBrightnessController.this.mForegroundAppPackageName == null || !AutomaticBrightnessController.this.mForegroundAppPackageName.equals(packageName)) {
                            String unused = AutomaticBrightnessController.this.mPendingForegroundAppPackageName = packageName;
                            int unused2 = AutomaticBrightnessController.this.mPendingForegroundAppCategory = -1;
                            try {
                                int unused3 = AutomaticBrightnessController.this.mPendingForegroundAppCategory = AutomaticBrightnessController.this.mPackageManager.getApplicationInfo(packageName, DumpState.DUMP_CHANGES).category;
                            } catch (PackageManager.NameNotFoundException e) {
                            }
                            AutomaticBrightnessController.this.mHandler.sendEmptyMessage(5);
                        }
                    }
                } catch (RemoteException e2) {
                }
            }
        });
    }

    /* access modifiers changed from: private */
    public void updateForegroundAppSync() {
        if (this.mLoggingEnabled) {
            Slog.d(TAG, "Updating foreground app: packageName=" + this.mPendingForegroundAppPackageName + ", category=" + this.mPendingForegroundAppCategory);
        }
        this.mForegroundAppPackageName = this.mPendingForegroundAppPackageName;
        this.mPendingForegroundAppPackageName = null;
        this.mForegroundAppCategory = this.mPendingForegroundAppCategory;
        this.mPendingForegroundAppCategory = -1;
        updateAutoBrightness(true, false);
    }

    private final class AutomaticBrightnessHandler extends Handler {
        public AutomaticBrightnessHandler(Looper looper) {
            super(looper, (Handler.Callback) null, true);
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 1) {
                AutomaticBrightnessController.this.updateAmbientLux();
            } else if (i == 2) {
                AutomaticBrightnessController.this.collectBrightnessAdjustmentSample();
            } else if (i == 3) {
                AutomaticBrightnessController.this.invalidateShortTermModel();
            } else if (i == 4) {
                AutomaticBrightnessController.this.updateForegroundApp();
            } else if (i == 5) {
                AutomaticBrightnessController.this.updateForegroundAppSync();
            } else if (i == 1000) {
                AutomaticBrightnessController.this.updateAutoBrightness(true, false);
            } else if (i == 1001 && BrightnessMappingStrategy.sVideoWhiteList.contains(AutomaticBrightnessController.this.mForegroundApplicationPackageName)) {
                AutomaticBrightnessController.this.mBrightnessMapper.mRotation = ((Integer) msg.obj).intValue();
                AutomaticBrightnessController.this.updateAutoBrightness(true, false);
            }
        }
    }

    class TaskStackListenerImpl extends TaskStackListener {
        TaskStackListenerImpl() {
        }

        public void onTaskStackChanged() {
            AutomaticBrightnessController.this.mHandler.sendEmptyMessage(4);
        }
    }

    private static final class AmbientLightRingBuffer {
        private static final float BUFFER_SLACK = 1.5f;
        private int mCapacity;
        private int mCount;
        private int mEnd;
        private float mMaxLux;
        private float mMiniLux;
        private float[] mRingLux;
        private long[] mRingTime;
        private int mStart;

        public AmbientLightRingBuffer(long lightSensorRate, int ambientLightHorizon) {
            this.mCapacity = (int) Math.ceil((double) ((((float) ambientLightHorizon) * BUFFER_SLACK) / ((float) lightSensorRate)));
            int i = this.mCapacity;
            this.mRingLux = new float[i];
            this.mRingTime = new long[i];
        }

        public float getLux(int index) {
            return this.mRingLux[offsetOf(index)];
        }

        public long getTime(int index) {
            return this.mRingTime[offsetOf(index)];
        }

        public void push(long time, float lux) {
            int next = this.mEnd;
            int i = this.mCount;
            int i2 = this.mCapacity;
            if (i == i2) {
                int newSize = i2 * 2;
                float[] newRingLux = new float[newSize];
                long[] newRingTime = new long[newSize];
                int i3 = this.mStart;
                int length = i2 - i3;
                System.arraycopy(this.mRingLux, i3, newRingLux, 0, length);
                System.arraycopy(this.mRingTime, this.mStart, newRingTime, 0, length);
                int i4 = this.mStart;
                if (i4 != 0) {
                    System.arraycopy(this.mRingLux, 0, newRingLux, length, i4);
                    System.arraycopy(this.mRingTime, 0, newRingTime, length, this.mStart);
                }
                this.mRingLux = newRingLux;
                this.mRingTime = newRingTime;
                next = this.mCapacity;
                this.mCapacity = newSize;
                this.mStart = 0;
            }
            this.mRingTime[next] = time;
            this.mRingLux[next] = lux;
            this.mEnd = next + 1;
            if (this.mEnd == this.mCapacity) {
                this.mEnd = 0;
            }
            this.mCount++;
        }

        public void prune(long horizon) {
            if (this.mCount != 0) {
                while (this.mCount > 1) {
                    int next = this.mStart + 1;
                    int i = this.mCapacity;
                    if (next >= i) {
                        next -= i;
                    }
                    if (this.mRingTime[next] > horizon) {
                        break;
                    }
                    this.mStart = next;
                    this.mCount--;
                }
                long[] jArr = this.mRingTime;
                int i2 = this.mStart;
                if (jArr[i2] < horizon) {
                    jArr[i2] = horizon;
                }
            }
        }

        public int size() {
            return this.mCount;
        }

        public void clear() {
            this.mStart = 0;
            this.mEnd = 0;
            this.mCount = 0;
            this.mMaxLux = -1.0f;
            this.mMiniLux = -1.0f;
        }

        public String toString() {
            StringBuffer buf = new StringBuffer();
            buf.append('[');
            int i = 0;
            while (true) {
                int i2 = this.mCount;
                if (i < i2) {
                    long next = i + 1 < i2 ? getTime(i + 1) : SystemClock.uptimeMillis();
                    if (i != 0) {
                        buf.append(", ");
                    }
                    buf.append(getLux(i));
                    buf.append(" / ");
                    buf.append(next - getTime(i));
                    buf.append("ms");
                    i++;
                } else {
                    buf.append(']');
                    return buf.toString();
                }
            }
        }

        private int offsetOf(int index) {
            if (index >= this.mCount || index < 0) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            int index2 = index + this.mStart;
            int i = this.mCapacity;
            if (index2 >= i) {
                return index2 - i;
            }
            return index2;
        }

        public void sort(long horizon) {
            int N = this.mCount;
            if (N == 0) {
                Slog.e(AutomaticBrightnessController.TAG, "sort: No ambient light readings available");
                this.mMiniLux = -1.0f;
                this.mMaxLux = -1.0f;
                return;
            }
            int endIndex = 0;
            long horizonStartTime = SystemClock.uptimeMillis() - horizon;
            int i = 0;
            while (i < N - 1 && getTime(i + 1) <= horizonStartTime) {
                endIndex++;
                i++;
            }
            float lux = getLux(N - 1);
            this.mMiniLux = lux;
            this.mMaxLux = lux;
            for (int i2 = N - 1; i2 >= endIndex; i2--) {
                if (getLux(i2) > this.mMaxLux) {
                    this.mMaxLux = getLux(i2);
                } else if (getLux(i2) < this.mMiniLux) {
                    this.mMiniLux = getLux(i2);
                }
            }
        }

        public float getMaxLux() {
            return this.mMaxLux;
        }

        public float getMiniLux() {
            return this.mMiniLux;
        }
    }

    private void registerBrightnessCorrectionListener() {
        ProcessManager.registerForegroundInfoListener(this.mForegroundInfoListener);
        registerRotationListener();
    }

    private void unregisterBrightnessCorrectionListener() {
        ProcessManager.unregisterForegroundInfoListener(this.mForegroundInfoListener);
        unregisterRotationListener();
    }

    class RotationWatcher extends IRotationWatcher.Stub {
        RotationWatcher() {
        }

        public void onRotationChanged(int rotation) throws RemoteException {
            AutomaticBrightnessController.this.mHandler.sendMessageDelayed(AutomaticBrightnessController.this.mHandler.obtainMessage(1001, Integer.valueOf(rotation)), 30);
        }
    }

    private void registerRotationListener() {
        try {
            this.mWindowManager.watchRotation(this.mRotationWatcher, 0);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void unregisterRotationListener() {
        try {
            this.mWindowManager.removeRotationWatcher(this.mRotationWatcher);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
