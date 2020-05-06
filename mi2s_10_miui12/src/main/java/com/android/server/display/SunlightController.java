package com.android.server.display;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.SystemSensorManager;
import android.miui.R;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.util.Slog;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.job.controllers.JobStatus;
import java.io.File;
import java.io.PrintWriter;

public class SunlightController {
    private static final boolean DEBUG = false;
    private static final String ENABLE_SENSOR_REASON_DEFAULT = "sunlight_mode";
    private static final String ENABLE_SENSOR_REASON_NOTIFICATION = "prepare_for_notifaction";
    private static final int MSG_SCREEN_HANG_UP_RECEIVE = 3;
    private static final int MSG_SCREEN_ON_OFF_RECEIVE = 2;
    private static final int MSG_UPDATE_SUNLIGHT_MODE = 1;
    private static final int RESET_USER_DISABLE_DURATION = 300000;
    private static final int SUNLIGHT_AMBIENT_LIGHT_HORIZON = 10000;
    private static final int SUNLIGHT_LIGHT_SENSOR_RATE = 250;
    private static final String TAG = "SunlightController";
    private static final int THRESHOLD_ENTER_SUNLIGHT_DURATION = 5000;
    private static final int THRESHOLD_EXIT_SUNLIGHT_DURATION = 2000;
    private static final int THRESHOLD_SUNLIGHT_BRIGHTNESS = 500;
    private static final int THRESHOLD_SUNLIGHT_LUX = 12000;
    private AmbientLightRingBuffer mAmbientLightRingBuffer;
    private boolean mAutoBrightnessSettingsEnable;
    private boolean mBelowThresholdBrightness;
    private Callback mCallback;
    /* access modifiers changed from: private */
    public Context mContext;
    private float mCurrentAmbientLux;
    private int mCurrentScreenBrightnessSettings;
    /* access modifiers changed from: private */
    public SunlightModeHandler mHandler;
    private float mLastObservedLux;
    private long mLastObservedLuxTime;
    private long mLastScreenOffTime;
    private Sensor mLightSensor;
    private final SensorEventListener mLightSensorListener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
            if (SunlightController.this.mSunlightSensorEnabled) {
                SunlightController.this.handleLightSensorEvent(SystemClock.uptimeMillis(), event.values[0]);
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
    private NotificationHelper mNotificationHelper;
    private PowerManager mPowerManager;
    private boolean mPreparedForNotification;
    private int mScreenBrightnessDefaultSettings;
    private boolean mScreenIsHangUp;
    private boolean mScreenOn = true;
    private SensorManager mSensorManager;
    private SettingsObserver mSettingsObserver;
    private boolean mSunlightModeActive;
    private boolean mSunlightModeDisabledByUser;
    private boolean mSunlightModeEnable;
    private long mSunlightSensorEnableTime;
    /* access modifiers changed from: private */
    public boolean mSunlightSensorEnabled;
    private String mSunlightSensorEnabledReason;
    private boolean mSunlightSettingsEnable;

    public interface Callback {
        void notifySunlightStateChange(boolean z);
    }

    public SunlightController(Context context, Callback callback, Looper looper) {
        this.mContext = context;
        this.mCallback = callback;
        this.mHandler = new SunlightModeHandler(looper);
        this.mNotificationHelper = new NotificationHelper();
        this.mSensorManager = new SystemSensorManager(this.mContext, this.mHandler.getLooper());
        this.mLightSensor = this.mSensorManager.getDefaultSensor(5);
        this.mAmbientLightRingBuffer = new AmbientLightRingBuffer(250, 10000);
        this.mSettingsObserver = new SettingsObserver(this.mHandler);
        this.mPowerManager = (PowerManager) this.mContext.getSystemService("power");
        this.mScreenBrightnessDefaultSettings = this.mPowerManager.getDefaultScreenBrightnessSetting();
        registerSettingsObserver();
        registerScreenOnReceiver();
        registerHangUpReceiver();
        updateSunlightModeSettings();
    }

    private void registerSettingsObserver() {
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor(ENABLE_SENSOR_REASON_DEFAULT), false, this.mSettingsObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_brightness_mode"), false, this.mSettingsObserver, -1);
        this.mContext.getContentResolver().registerContentObserver(Settings.System.getUriFor("screen_brightness"), false, this.mSettingsObserver, -1);
    }

    private void registerScreenOnReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.SCREEN_OFF");
        filter.setPriority(1000);
        this.mContext.registerReceiver(new ScreenOnReceiver(), filter);
    }

    private void registerHangUpReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("miui.intent.action.HANG_UP_CHANGED");
        this.mContext.registerReceiver(new ScreenHangUpReceiver(), filter);
    }

    /* access modifiers changed from: private */
    public void updateSunlightModeSettings() {
        boolean z = false;
        boolean sunlightSettingsChanged = Settings.System.getIntForUser(this.mContext.getContentResolver(), ENABLE_SENSOR_REASON_DEFAULT, 0, -2) != 0;
        if (Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_brightness_mode", 0, -2) != 0) {
            z = true;
        }
        this.mAutoBrightnessSettingsEnable = z;
        this.mCurrentScreenBrightnessSettings = Settings.System.getIntForUser(this.mContext.getContentResolver(), "screen_brightness", this.mScreenBrightnessDefaultSettings, -2);
        if (!sunlightSettingsChanged && this.mSunlightSettingsEnable) {
            resetUserDisableTemporaryData();
        }
        this.mSunlightSettingsEnable = sunlightSettingsChanged;
        if (!updateSunlightModeCondition()) {
            shouldPrepareToNotify();
        }
    }

    private void shouldPrepareToNotify() {
        setLightSensorEnabledForNotification(!this.mSunlightSettingsEnable && !this.mAutoBrightnessSettingsEnable && this.mBelowThresholdBrightness && !this.mPreparedForNotification && this.mScreenOn && !this.mNotificationHelper.mHasReachedLimitTimes);
    }

    private void setLightSensorEnabledForNotification(boolean enable) {
        if (enable != this.mPreparedForNotification) {
            this.mPreparedForNotification = enable;
            setLightSensorEnabled(enable, ENABLE_SENSOR_REASON_NOTIFICATION);
        }
    }

    private boolean updateSunlightModeCondition() {
        boolean z = true;
        this.mBelowThresholdBrightness = this.mCurrentScreenBrightnessSettings < 500;
        if (this.mAutoBrightnessSettingsEnable || !this.mSunlightSettingsEnable || !this.mScreenOn || this.mSunlightModeDisabledByUser || this.mScreenIsHangUp) {
            z = false;
        }
        boolean enable = z;
        if (enable != this.mSunlightModeEnable) {
            this.mSunlightModeEnable = enable;
            this.mPreparedForNotification = false;
            setLightSensorEnabled(this.mSunlightModeEnable);
        }
        return enable;
    }

    private void updateNotificationState() {
        if (this.mCurrentAmbientLux >= 12000.0f && !this.mNotificationHelper.showNotificationIfNecessary()) {
            setLightSensorEnabledForNotification(false);
        }
    }

    private boolean setLightSensorEnabled(boolean enabled) {
        return setLightSensorEnabled(enabled, ENABLE_SENSOR_REASON_DEFAULT);
    }

    private boolean setLightSensorEnabled(boolean enabled, String reason) {
        if (enabled) {
            if (reason.equals(this.mSunlightSensorEnabledReason)) {
                this.mSunlightSensorEnabledReason = reason;
            }
            if (this.mSunlightSensorEnabled) {
                return false;
            }
            this.mSunlightSensorEnabled = true;
            this.mSunlightSensorEnableTime = SystemClock.uptimeMillis();
            this.mSensorManager.registerListener(this.mLightSensorListener, this.mLightSensor, 0, this.mHandler);
            return true;
        } else if (!this.mSunlightSensorEnabled) {
            return false;
        } else {
            this.mSunlightSensorEnabled = false;
            this.mPreparedForNotification = false;
            this.mSunlightSensorEnableTime = SystemClock.uptimeMillis();
            this.mHandler.removeMessages(1);
            this.mSensorManager.unregisterListener(this.mLightSensorListener);
            setSunLightModeActive(false);
            return true;
        }
    }

    /* access modifiers changed from: private */
    public void handleLightSensorEvent(long time, float lux) {
        this.mHandler.removeMessages(1);
        applyLightSensorMeasurement(time, lux);
        updateAmbientLux(time);
    }

    /* access modifiers changed from: private */
    public void updateAmbientLux() {
        long time = SystemClock.uptimeMillis();
        this.mAmbientLightRingBuffer.prune(time - JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY);
        updateAmbientLux(time);
    }

    private void updateAmbientLux(long time) {
        this.mCurrentAmbientLux = this.mAmbientLightRingBuffer.calculateAmbientLux(time, JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY);
        long nextEnterSunlightModeTime = nextEnterSunlightModeTransition(time);
        long nextExitSunlightModeTime = nextExitSunlightModeTransition(time);
        if (!this.mPreparedForNotification || this.mSunlightModeEnable) {
            if (this.mCurrentAmbientLux >= 12000.0f && nextEnterSunlightModeTime <= time && this.mBelowThresholdBrightness && this.mSunlightModeEnable) {
                setSunLightModeActive(true);
            } else if (this.mCurrentAmbientLux < 12000.0f && nextExitSunlightModeTime <= time) {
                setSunLightModeActive(false);
            }
            long nextTransitionTime = Math.min(nextEnterSunlightModeTime, nextExitSunlightModeTime);
            this.mHandler.sendEmptyMessageAtTime(1, nextTransitionTime > time ? nextTransitionTime : 1000 + time);
            return;
        }
        updateNotificationState();
    }

    private void setSunLightModeActive(boolean active) {
        if (active != this.mSunlightModeActive) {
            Slog.d(TAG, "setSunLightModeActive: active: " + active);
            this.mSunlightModeActive = active;
            this.mCallback.notifySunlightStateChange(active);
        }
    }

    private void applyLightSensorMeasurement(long time, float lux) {
        this.mAmbientLightRingBuffer.prune(time - JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY);
        this.mAmbientLightRingBuffer.push(time, lux);
        this.mLastObservedLux = lux;
        this.mLastObservedLuxTime = time;
    }

    private long nextEnterSunlightModeTransition(long time) {
        long earliestValidTime = time;
        int i = this.mAmbientLightRingBuffer.size() - 1;
        while (i >= 0 && this.mAmbientLightRingBuffer.getLux(i) > 12000.0f) {
            earliestValidTime = this.mAmbientLightRingBuffer.getTime(i);
            i--;
        }
        return 5000 + earliestValidTime;
    }

    private long nextExitSunlightModeTransition(long time) {
        long earliestValidTime = time;
        int i = this.mAmbientLightRingBuffer.size() - 1;
        while (i >= 0 && this.mAmbientLightRingBuffer.getLux(i) < 12000.0f) {
            earliestValidTime = this.mAmbientLightRingBuffer.getTime(i);
            i--;
        }
        return 2000 + earliestValidTime;
    }

    public void setSunlightModeDisabledByUserTemporary() {
        if (!this.mSunlightModeDisabledByUser) {
            Slog.d(TAG, "Disable sunlight mode temporarily due to user slide bar.");
            this.mSunlightModeDisabledByUser = true;
            updateSunlightModeCondition();
        }
    }

    public boolean isSunlightModeDisabledByUser() {
        return this.mSunlightModeDisabledByUser;
    }

    private class SunlightModeHandler extends Handler {
        public SunlightModeHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 1) {
                SunlightController.this.updateAmbientLux();
            } else if (i == 2) {
                SunlightController.this.updateScreenState(((Boolean) msg.obj).booleanValue());
            } else if (i == 3) {
                SunlightController.this.updateHangUpState(((Boolean) msg.obj).booleanValue());
            }
        }
    }

    /* access modifiers changed from: private */
    public void updateHangUpState(boolean screenIsHangUp) {
        if (screenIsHangUp != this.mScreenIsHangUp) {
            this.mScreenIsHangUp = screenIsHangUp;
            updateSunlightModeCondition();
        }
    }

    /* access modifiers changed from: private */
    public void updateScreenState(boolean screenOn) {
        if (screenOn != this.mScreenOn) {
            this.mScreenOn = screenOn;
            long currentTime = SystemClock.elapsedRealtime();
            if (!this.mScreenOn) {
                clearAmbientLightRingBuffer();
                this.mLastScreenOffTime = currentTime;
            } else if (currentTime - this.mLastScreenOffTime >= BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS) {
                resetUserDisableTemporaryData();
            }
            updateSunlightModeCondition();
            shouldPrepareToNotify();
        }
    }

    private void resetUserDisableTemporaryData() {
        if (this.mSunlightModeDisabledByUser) {
            Slog.d(TAG, "Reset user slide operation.");
            this.mSunlightModeDisabledByUser = false;
            updateSunlightModeCondition();
        }
    }

    private void clearAmbientLightRingBuffer() {
        this.mAmbientLightRingBuffer.clear();
    }

    private class ScreenOnReceiver extends BroadcastReceiver {
        private ScreenOnReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            boolean screenOn = false;
            if (intent.getAction().equals("android.intent.action.SCREEN_ON")) {
                screenOn = true;
            }
            SunlightController.this.mHandler.obtainMessage(2, Boolean.valueOf(screenOn)).sendToTarget();
        }
    }

    private class ScreenHangUpReceiver extends BroadcastReceiver {
        private ScreenHangUpReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            SunlightController.this.mHandler.obtainMessage(3, Boolean.valueOf(intent.getBooleanExtra("hang_up_enable", false))).sendToTarget();
        }
    }

    private class SettingsObserver extends ContentObserver {
        public SettingsObserver(Handler handler) {
            super(handler);
        }

        /* JADX WARNING: Removed duplicated region for block: B:17:0x003e A[ADDED_TO_REGION] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onChange(boolean r6, android.net.Uri r7) {
            /*
                r5 = this;
                java.lang.String r0 = r7.getLastPathSegment()
                int r1 = r0.hashCode()
                r2 = -1763718536(0xffffffff96dfca78, float:-3.615537E-25)
                r3 = 2
                r4 = 1
                if (r1 == r2) goto L_0x0030
                r2 = -693072130(0xffffffffd6b08efe, float:-9.7064097E13)
                if (r1 == r2) goto L_0x0025
                r2 = 1735689732(0x67748604, float:1.1547296E24)
                if (r1 == r2) goto L_0x001a
            L_0x0019:
                goto L_0x003b
            L_0x001a:
                java.lang.String r1 = "screen_brightness"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x0019
                r1 = r3
                goto L_0x003c
            L_0x0025:
                java.lang.String r1 = "screen_brightness_mode"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x0019
                r1 = r4
                goto L_0x003c
            L_0x0030:
                java.lang.String r1 = "sunlight_mode"
                boolean r1 = r0.equals(r1)
                if (r1 == 0) goto L_0x0019
                r1 = 0
                goto L_0x003c
            L_0x003b:
                r1 = -1
            L_0x003c:
                if (r1 == 0) goto L_0x0043
                if (r1 == r4) goto L_0x0043
                if (r1 == r3) goto L_0x0043
                goto L_0x0049
            L_0x0043:
                com.android.server.display.SunlightController r1 = com.android.server.display.SunlightController.this
                r1.updateSunlightModeSettings()
            L_0x0049:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.display.SunlightController.SettingsObserver.onChange(boolean, android.net.Uri):void");
        }
    }

    private final class NotificationHelper {
        private static final String AUTO_BRIGHTNESS_ACTION = "com.android.settings/com.android.settings.display.BrightnessActivity";
        private static final int DEFAULT_NOTIFICATION_LIMIT = 2;
        private static final String KEY_NOTIFICATION_LAST_SHOW_TIME = "last_show_time";
        private static final String KEY_NOTIFICATION_LIMIT = "shown_times";
        private static final int MINI_INTERVAL_NOTIFICATION = 3600000;
        private static final int NOTIFICATION_ID = 1000;
        private static final String NOTIFICATION_TAG = "SUNLIGHT_NOTIFY";
        private static final String PREFS_SUNLIGHT_FILE = "sunlight_notification.xml";
        /* access modifiers changed from: private */
        public boolean mHasReachedLimitTimes;
        private long mLastShowNotificationTime = this.mNotificationLimitTimesPrefs.getLong(KEY_NOTIFICATION_LAST_SHOW_TIME, 0);
        private Notification mNotification;
        private SharedPreferences mNotificationLimitTimesPrefs;
        private NotificationManager mNotificationManager;

        public NotificationHelper() {
            this.mNotificationManager = (NotificationManager) SunlightController.this.mContext.getSystemService("notification");
            this.mNotificationLimitTimesPrefs = SunlightController.this.mContext.createDeviceProtectedStorageContext().getSharedPreferences(new File(getSystemDir(), PREFS_SUNLIGHT_FILE), 0);
        }

        /* access modifiers changed from: private */
        public boolean showNotificationIfNecessary() {
            if (isNotificationActive() || constrainedByInterval() || hasReachedLimitTimes()) {
                return false;
            }
            if (this.mNotification == null) {
                buildNotification();
            }
            updateLastShowTimePrefs();
            this.mNotificationManager.notify(NOTIFICATION_TAG, 1000, this.mNotification);
            return true;
        }

        private boolean isNotificationActive() {
            for (StatusBarNotification sbn : this.mNotificationManager.getActiveNotifications()) {
                if (1000 == sbn.getId() && NOTIFICATION_TAG.equals(sbn.getTag())) {
                    return true;
                }
            }
            return false;
        }

        private boolean constrainedByInterval() {
            long now = SystemClock.elapsedRealtime();
            long j = this.mLastShowNotificationTime;
            if (j != 0 && now - j <= 3600000) {
                return true;
            }
            return false;
        }

        private boolean hasReachedLimitTimes() {
            int times = this.mNotificationLimitTimesPrefs.getInt(KEY_NOTIFICATION_LIMIT, 2);
            if (times > 0) {
                this.mNotificationLimitTimesPrefs.edit().putInt(KEY_NOTIFICATION_LIMIT, times - 1).commit();
                this.mHasReachedLimitTimes = false;
                return false;
            }
            this.mHasReachedLimitTimes = true;
            return true;
        }

        private void updateLastShowTimePrefs() {
            this.mLastShowNotificationTime = SystemClock.elapsedRealtime();
            this.mNotificationLimitTimesPrefs.edit().putLong(KEY_NOTIFICATION_LAST_SHOW_TIME, this.mLastShowNotificationTime).commit();
        }

        private void buildNotification() {
            this.mNotification = new Notification.Builder(SunlightController.this.mContext, SystemNotificationChannels.ALERTS).setContentIntent(PendingIntent.getActivity(SunlightController.this.mContext, 0, getBrightnessActivityIntent(), 268435456)).setContentTitle(SunlightController.this.mContext.getString(R.string.sunlight_notification_title)).setContentText(SunlightController.this.mContext.getString(R.string.sunlight_notification_content)).setSmallIcon(17303570).setAutoCancel(true).build();
        }

        private Intent getBrightnessActivityIntent() {
            ComponentName component = ComponentName.unflattenFromString(AUTO_BRIGHTNESS_ACTION);
            if (component == null) {
                return null;
            }
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setComponent(component);
            intent.setFlags(335544320);
            return intent;
        }

        private File getSystemDir() {
            return new File(Environment.getDataDirectory(), "system");
        }

        /* access modifiers changed from: private */
        public void dump(PrintWriter pw) {
            pw.println("  Sunlight Controller Noticationcation Helper:");
            pw.println("    mHasReachedLimitTimes=" + this.mHasReachedLimitTimes);
            pw.println("    mLastShowNotificationTime=" + this.mLastShowNotificationTime);
        }
    }

    public void dump(PrintWriter pw) {
        pw.println();
        pw.println("Sunlight Controller Configuration:");
        pw.println("  mSunlightSettingsEnable=" + this.mSunlightSettingsEnable);
        pw.println("  mSunlightSensorEnableTime=" + this.mSunlightSensorEnableTime);
        pw.println("  mLastObservedLux=" + this.mLastObservedLux);
        pw.println("  mLastObservedLuxTime=" + this.mLastObservedLuxTime);
        pw.println("  mCurrentAmbientLux=" + this.mCurrentAmbientLux);
        pw.println("  mSunlightSensorEnabled=" + this.mSunlightSensorEnabled);
        if (this.mSunlightSensorEnabled) {
            pw.println("  mSunlightSensorEnabledReason=" + this.mSunlightSensorEnabledReason);
        }
        pw.println("  mBelowThresholdBrightness=" + this.mBelowThresholdBrightness);
        pw.println("  mSunlightModeActive=" + this.mSunlightModeActive);
        pw.println("  mSunlightModeDisabledByUser=" + this.mSunlightModeDisabledByUser);
        pw.println("  mAmbientLightRingBuffer=" + this.mAmbientLightRingBuffer);
        pw.println("  mScreenIsHangUp=" + this.mScreenIsHangUp);
        this.mNotificationHelper.dump(pw);
    }
}
