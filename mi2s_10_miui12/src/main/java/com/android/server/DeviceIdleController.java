package com.android.server;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationRequest;
import android.net.INetworkPolicyManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.IDeviceIdleController;
import android.os.IMaintenanceActivityListener;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManagerInternal;
import android.os.PowerSaveState;
import android.os.Process;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ShellCallback;
import android.os.ShellCommand;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.KeyValueListParser;
import android.util.MutableLong;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.TimeUtils;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IBatteryStats;
import com.android.internal.os.AtomicFile;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.FastXmlSerializer;
import com.android.server.AnyMotionDetector;
import com.android.server.UiModeManagerService;
import com.android.server.am.ActivityManagerServiceInjector;
import com.android.server.am.BatteryStatsService;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.deviceidle.ConstraintController;
import com.android.server.deviceidle.DeviceIdleConstraintTracker;
import com.android.server.deviceidle.IDeviceIdleConstraint;
import com.android.server.deviceidle.TvConstraintController;
import com.android.server.net.NetworkPolicyManagerInternal;
import com.android.server.pm.DumpState;
import com.android.server.wm.ActivityTaskManagerInternal;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class DeviceIdleController extends SystemService implements AnyMotionDetector.DeviceIdleCallback {
    private static final int ACTIVE_REASON_ALARM = 7;
    private static final int ACTIVE_REASON_CHARGING = 3;
    private static final int ACTIVE_REASON_FORCED = 6;
    private static final int ACTIVE_REASON_FROM_BINDER_CALL = 5;
    private static final int ACTIVE_REASON_MOTION = 1;
    private static final int ACTIVE_REASON_SCREEN = 2;
    private static final int ACTIVE_REASON_UNKNOWN = 0;
    private static final int ACTIVE_REASON_UNLOCKED = 4;
    private static final boolean COMPRESS_TIME = false;
    private static final boolean DEBUG = false;
    private static final int EVENT_BUFFER_SIZE = 100;
    private static final int EVENT_DEEP_IDLE = 4;
    private static final int EVENT_DEEP_MAINTENANCE = 5;
    private static final int EVENT_LIGHT_IDLE = 2;
    private static final int EVENT_LIGHT_MAINTENANCE = 3;
    private static final int EVENT_NORMAL = 1;
    private static final int EVENT_NULL = 0;
    @VisibleForTesting
    static final int LIGHT_STATE_ACTIVE = 0;
    @VisibleForTesting
    static final int LIGHT_STATE_IDLE = 4;
    @VisibleForTesting
    static final int LIGHT_STATE_IDLE_MAINTENANCE = 6;
    @VisibleForTesting
    static final int LIGHT_STATE_INACTIVE = 1;
    @VisibleForTesting
    static final int LIGHT_STATE_OVERRIDE = 7;
    @VisibleForTesting
    static final int LIGHT_STATE_PRE_IDLE = 3;
    @VisibleForTesting
    static final int LIGHT_STATE_WAITING_FOR_NETWORK = 5;
    @VisibleForTesting
    static final float MIN_PRE_IDLE_FACTOR_CHANGE = 0.05f;
    @VisibleForTesting
    static final long MIN_STATE_STEP_ALARM_CHANGE = 60000;
    private static final int MSG_FINISH_IDLE_OP = 8;
    private static final int MSG_REPORT_ACTIVE = 5;
    private static final int MSG_REPORT_IDLE_OFF = 4;
    private static final int MSG_REPORT_IDLE_ON = 2;
    private static final int MSG_REPORT_IDLE_ON_LIGHT = 3;
    private static final int MSG_REPORT_MAINTENANCE_ACTIVITY = 7;
    private static final int MSG_REPORT_TEMP_APP_WHITELIST_CHANGED = 9;
    private static final int MSG_RESET_PRE_IDLE_TIMEOUT_FACTOR = 12;
    private static final int MSG_SEND_CONSTRAINT_MONITORING = 10;
    private static final int MSG_TEMP_APP_WHITELIST_TIMEOUT = 6;
    private static final int MSG_UPDATE_PRE_IDLE_TIMEOUT_FACTOR = 11;
    private static final int MSG_WRITE_CONFIG = 1;
    @VisibleForTesting
    static final int SET_IDLE_FACTOR_RESULT_IGNORED = 0;
    @VisibleForTesting
    static final int SET_IDLE_FACTOR_RESULT_INVALID = 3;
    @VisibleForTesting
    static final int SET_IDLE_FACTOR_RESULT_NOT_SUPPORT = 2;
    @VisibleForTesting
    static final int SET_IDLE_FACTOR_RESULT_OK = 1;
    @VisibleForTesting
    static final int SET_IDLE_FACTOR_RESULT_UNINIT = -1;
    @VisibleForTesting
    static final int STATE_ACTIVE = 0;
    @VisibleForTesting
    static final int STATE_IDLE = 5;
    @VisibleForTesting
    static final int STATE_IDLE_MAINTENANCE = 6;
    @VisibleForTesting
    static final int STATE_IDLE_PENDING = 2;
    @VisibleForTesting
    static final int STATE_INACTIVE = 1;
    @VisibleForTesting
    static final int STATE_LOCATING = 4;
    @VisibleForTesting
    static final int STATE_QUICK_DOZE_DELAY = 7;
    @VisibleForTesting
    static final int STATE_SENSING = 3;
    private static final String TAG = "DeviceIdleController";
    private int mActiveIdleOpCount;
    private PowerManager.WakeLock mActiveIdleWakeLock;
    private int mActiveReason;
    private AlarmManager mAlarmManager;
    private boolean mAlarmsActive;
    private AnyMotionDetector mAnyMotionDetector;
    private final AppStateTracker mAppStateTracker;
    /* access modifiers changed from: private */
    public IBatteryStats mBatteryStats;
    BinderService mBinderService;
    private boolean mCharging;
    public final AtomicFile mConfigFile;
    /* access modifiers changed from: private */
    public Constants mConstants;
    private ConstraintController mConstraintController;
    private final ArrayMap<IDeviceIdleConstraint, DeviceIdleConstraintTracker> mConstraints;
    private long mCurIdleBudget;
    @VisibleForTesting
    final AlarmManager.OnAlarmListener mDeepAlarmListener;
    private boolean mDeepEnabled;
    private final int[] mEventCmds;
    private final String[] mEventReasons;
    private final long[] mEventTimes;
    private boolean mForceIdle;
    private final LocationListener mGenericLocationListener;
    /* access modifiers changed from: private */
    public PowerManager.WakeLock mGoingIdleWakeLock;
    private final LocationListener mGpsLocationListener;
    final MyHandler mHandler;
    private boolean mHasGps;
    private boolean mHasNetworkLocation;
    /* access modifiers changed from: private */
    public Intent mIdleIntent;
    private long mIdleStartTime;
    /* access modifiers changed from: private */
    public final BroadcastReceiver mIdleStartedDoneReceiver;
    private long mInactiveTimeout;
    private final Injector mInjector;
    private final BroadcastReceiver mInteractivityReceiver;
    private boolean mJobsActive;
    private Location mLastGenericLocation;
    private Location mLastGpsLocation;
    private float mLastPreIdleFactor;
    private final AlarmManager.OnAlarmListener mLightAlarmListener;
    private boolean mLightEnabled;
    /* access modifiers changed from: private */
    public Intent mLightIdleIntent;
    private int mLightState;
    private ActivityManagerInternal mLocalActivityManager;
    private ActivityTaskManagerInternal mLocalActivityTaskManager;
    private AlarmManagerInternal mLocalAlarmManager;
    /* access modifiers changed from: private */
    public PowerManagerInternal mLocalPowerManager;
    private boolean mLocated;
    private boolean mLocating;
    private LocationRequest mLocationRequest;
    /* access modifiers changed from: private */
    public final RemoteCallbackList<IMaintenanceActivityListener> mMaintenanceActivityListeners;
    private long mMaintenanceStartTime;
    @VisibleForTesting
    final MotionListener mMotionListener;
    /* access modifiers changed from: private */
    public Sensor mMotionSensor;
    private boolean mNetworkConnected;
    /* access modifiers changed from: private */
    public INetworkPolicyManager mNetworkPolicyManager;
    /* access modifiers changed from: private */
    public NetworkPolicyManagerInternal mNetworkPolicyManagerInternal;
    private long mNextAlarmTime;
    private long mNextIdleDelay;
    private long mNextIdlePendingDelay;
    private long mNextLightAlarmTime;
    private long mNextLightIdleDelay;
    private long mNextSensingTimeoutAlarmTime;
    private boolean mNotMoving;
    private int mNumBlockingConstraints;
    private PowerManager mPowerManager;
    private int[] mPowerSaveWhitelistAllAppIdArray;
    private final SparseBooleanArray mPowerSaveWhitelistAllAppIds;
    private final ArrayMap<String, Integer> mPowerSaveWhitelistApps;
    private final ArrayMap<String, Integer> mPowerSaveWhitelistAppsExceptIdle;
    private int[] mPowerSaveWhitelistExceptIdleAppIdArray;
    private final SparseBooleanArray mPowerSaveWhitelistExceptIdleAppIds;
    private final SparseBooleanArray mPowerSaveWhitelistSystemAppIds;
    private final SparseBooleanArray mPowerSaveWhitelistSystemAppIdsExceptIdle;
    private int[] mPowerSaveWhitelistUserAppIdArray;
    private final SparseBooleanArray mPowerSaveWhitelistUserAppIds;
    private final ArrayMap<String, Integer> mPowerSaveWhitelistUserApps;
    private final ArraySet<String> mPowerSaveWhitelistUserAppsExceptIdle;
    private float mPreIdleFactor;
    private boolean mQuickDozeActivated;
    private final BroadcastReceiver mReceiver;
    private ArrayMap<String, Integer> mRemovedFromSystemWhitelistApps;
    private boolean mReportedMaintenanceActivity;
    private boolean mScreenLocked;
    private ActivityTaskManagerInternal.ScreenObserver mScreenObserver;
    private boolean mScreenOn;
    private final AlarmManager.OnAlarmListener mSensingTimeoutAlarmListener;
    /* access modifiers changed from: private */
    public SensorManager mSensorManager;
    /* access modifiers changed from: private */
    public int mState;
    private int[] mTempWhitelistAppIdArray;
    private final SparseArray<Pair<MutableLong, String>> mTempWhitelistAppIdEndTimes;
    private final boolean mUseMotionSensor;

    @VisibleForTesting
    static String stateToString(int state) {
        switch (state) {
            case 0:
                return "ACTIVE";
            case 1:
                return "INACTIVE";
            case 2:
                return "IDLE_PENDING";
            case 3:
                return "SENSING";
            case 4:
                return "LOCATING";
            case 5:
                return "IDLE";
            case 6:
                return "IDLE_MAINTENANCE";
            case 7:
                return "QUICK_DOZE_DELAY";
            default:
                return Integer.toString(state);
        }
    }

    @VisibleForTesting
    static String lightStateToString(int state) {
        if (state == 0) {
            return "ACTIVE";
        }
        if (state == 1) {
            return "INACTIVE";
        }
        if (state == 3) {
            return "PRE_IDLE";
        }
        if (state == 4) {
            return "IDLE";
        }
        if (state == 5) {
            return "WAITING_FOR_NETWORK";
        }
        if (state == 6) {
            return "IDLE_MAINTENANCE";
        }
        if (state != 7) {
            return Integer.toString(state);
        }
        return "OVERRIDE";
    }

    private void addEvent(int cmd, String reason) {
        int[] iArr = this.mEventCmds;
        if (iArr[0] != cmd) {
            System.arraycopy(iArr, 0, iArr, 1, 99);
            long[] jArr = this.mEventTimes;
            System.arraycopy(jArr, 0, jArr, 1, 99);
            String[] strArr = this.mEventReasons;
            System.arraycopy(strArr, 0, strArr, 1, 99);
            this.mEventCmds[0] = cmd;
            this.mEventTimes[0] = SystemClock.elapsedRealtime();
            this.mEventReasons[0] = reason;
        }
    }

    @VisibleForTesting
    final class MotionListener extends TriggerEventListener implements SensorEventListener {
        boolean active = false;

        MotionListener() {
        }

        public boolean isActive() {
            return this.active;
        }

        public void onTrigger(TriggerEvent event) {
            synchronized (DeviceIdleController.this) {
                this.active = false;
                DeviceIdleController.this.motionLocked();
            }
        }

        public void onSensorChanged(SensorEvent event) {
            synchronized (DeviceIdleController.this) {
                DeviceIdleController.this.mSensorManager.unregisterListener(this, DeviceIdleController.this.mMotionSensor);
                this.active = false;
                DeviceIdleController.this.motionLocked();
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }

        public boolean registerLocked() {
            boolean success;
            if (DeviceIdleController.this.mMotionSensor.getReportingMode() == 2) {
                success = DeviceIdleController.this.mSensorManager.requestTriggerSensor(DeviceIdleController.this.mMotionListener, DeviceIdleController.this.mMotionSensor);
            } else {
                success = DeviceIdleController.this.mSensorManager.registerListener(DeviceIdleController.this.mMotionListener, DeviceIdleController.this.mMotionSensor, 3);
            }
            if (success) {
                this.active = true;
            } else {
                Slog.e(DeviceIdleController.TAG, "Unable to register for " + DeviceIdleController.this.mMotionSensor);
            }
            return success;
        }

        public void unregisterLocked() {
            if (DeviceIdleController.this.mMotionSensor.getReportingMode() == 2) {
                DeviceIdleController.this.mSensorManager.cancelTriggerSensor(DeviceIdleController.this.mMotionListener, DeviceIdleController.this.mMotionSensor);
            } else {
                DeviceIdleController.this.mSensorManager.unregisterListener(DeviceIdleController.this.mMotionListener);
            }
            this.active = false;
        }
    }

    public final class Constants extends ContentObserver {
        private static final String KEY_IDLE_AFTER_INACTIVE_TIMEOUT = "idle_after_inactive_to";
        private static final String KEY_IDLE_FACTOR = "idle_factor";
        private static final String KEY_IDLE_PENDING_FACTOR = "idle_pending_factor";
        private static final String KEY_IDLE_PENDING_TIMEOUT = "idle_pending_to";
        private static final String KEY_IDLE_TIMEOUT = "idle_to";
        private static final String KEY_INACTIVE_TIMEOUT = "inactive_to";
        private static final String KEY_LIGHT_IDLE_AFTER_INACTIVE_TIMEOUT = "light_after_inactive_to";
        private static final String KEY_LIGHT_IDLE_FACTOR = "light_idle_factor";
        private static final String KEY_LIGHT_IDLE_MAINTENANCE_MAX_BUDGET = "light_idle_maintenance_max_budget";
        private static final String KEY_LIGHT_IDLE_MAINTENANCE_MIN_BUDGET = "light_idle_maintenance_min_budget";
        private static final String KEY_LIGHT_IDLE_TIMEOUT = "light_idle_to";
        private static final String KEY_LIGHT_MAX_IDLE_TIMEOUT = "light_max_idle_to";
        private static final String KEY_LIGHT_PRE_IDLE_TIMEOUT = "light_pre_idle_to";
        private static final String KEY_LOCATING_TIMEOUT = "locating_to";
        private static final String KEY_LOCATION_ACCURACY = "location_accuracy";
        private static final String KEY_MAX_IDLE_PENDING_TIMEOUT = "max_idle_pending_to";
        private static final String KEY_MAX_IDLE_TIMEOUT = "max_idle_to";
        private static final String KEY_MAX_TEMP_APP_WHITELIST_DURATION = "max_temp_app_whitelist_duration";
        private static final String KEY_MIN_DEEP_MAINTENANCE_TIME = "min_deep_maintenance_time";
        private static final String KEY_MIN_LIGHT_MAINTENANCE_TIME = "min_light_maintenance_time";
        private static final String KEY_MIN_TIME_TO_ALARM = "min_time_to_alarm";
        private static final String KEY_MMS_TEMP_APP_WHITELIST_DURATION = "mms_temp_app_whitelist_duration";
        private static final String KEY_MOTION_INACTIVE_TIMEOUT = "motion_inactive_to";
        private static final String KEY_NOTIFICATION_WHITELIST_DURATION = "notification_whitelist_duration";
        private static final String KEY_PRE_IDLE_FACTOR_LONG = "pre_idle_factor_long";
        private static final String KEY_PRE_IDLE_FACTOR_SHORT = "pre_idle_factor_short";
        private static final String KEY_QUICK_DOZE_DELAY_TIMEOUT = "quick_doze_delay_to";
        private static final String KEY_SENSING_TIMEOUT = "sensing_to";
        private static final String KEY_SMS_TEMP_APP_WHITELIST_DURATION = "sms_temp_app_whitelist_duration";
        private static final String KEY_WAIT_FOR_UNLOCK = "wait_for_unlock";
        public long IDLE_AFTER_INACTIVE_TIMEOUT;
        public float IDLE_FACTOR;
        public float IDLE_PENDING_FACTOR;
        public long IDLE_PENDING_TIMEOUT;
        public long IDLE_TIMEOUT;
        public long INACTIVE_TIMEOUT;
        public long LIGHT_IDLE_AFTER_INACTIVE_TIMEOUT;
        public float LIGHT_IDLE_FACTOR;
        public long LIGHT_IDLE_MAINTENANCE_MAX_BUDGET;
        public long LIGHT_IDLE_MAINTENANCE_MIN_BUDGET;
        public long LIGHT_IDLE_TIMEOUT;
        public long LIGHT_MAX_IDLE_TIMEOUT;
        public long LIGHT_PRE_IDLE_TIMEOUT;
        public long LOCATING_TIMEOUT;
        public float LOCATION_ACCURACY;
        public long MAX_IDLE_PENDING_TIMEOUT;
        public long MAX_IDLE_TIMEOUT;
        public long MAX_TEMP_APP_WHITELIST_DURATION;
        public long MIN_DEEP_MAINTENANCE_TIME;
        public long MIN_LIGHT_MAINTENANCE_TIME;
        public long MIN_TIME_TO_ALARM;
        public long MMS_TEMP_APP_WHITELIST_DURATION;
        public long MOTION_INACTIVE_TIMEOUT;
        public long NOTIFICATION_WHITELIST_DURATION;
        public float PRE_IDLE_FACTOR_LONG;
        public float PRE_IDLE_FACTOR_SHORT;
        public long QUICK_DOZE_DELAY_TIMEOUT;
        public long SENSING_TIMEOUT;
        public long SMS_TEMP_APP_WHITELIST_DURATION;
        public boolean WAIT_FOR_UNLOCK;
        private final KeyValueListParser mParser = new KeyValueListParser(',');
        private final ContentResolver mResolver;
        private final boolean mSmallBatteryDevice;

        public Constants(Handler handler, ContentResolver resolver) {
            super(handler);
            this.mResolver = resolver;
            this.mSmallBatteryDevice = ActivityManager.isSmallBatteryDevice();
            this.mResolver.registerContentObserver(Settings.Global.getUriFor("device_idle_constants"), false, this);
            updateConstants();
        }

        public void onChange(boolean selfChange, Uri uri) {
            updateConstants();
        }

        private void updateConstants() {
            synchronized (DeviceIdleController.this) {
                try {
                    this.mParser.setString(Settings.Global.getString(this.mResolver, "device_idle_constants"));
                } catch (IllegalArgumentException e) {
                    Slog.e(DeviceIdleController.TAG, "Bad device idle settings", e);
                }
                this.LIGHT_IDLE_AFTER_INACTIVE_TIMEOUT = this.mParser.getDurationMillis(KEY_LIGHT_IDLE_AFTER_INACTIVE_TIMEOUT, 180000);
                this.LIGHT_PRE_IDLE_TIMEOUT = this.mParser.getDurationMillis(KEY_LIGHT_PRE_IDLE_TIMEOUT, 180000);
                this.LIGHT_IDLE_TIMEOUT = this.mParser.getDurationMillis(KEY_LIGHT_IDLE_TIMEOUT, BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS);
                this.LIGHT_IDLE_FACTOR = this.mParser.getFloat(KEY_LIGHT_IDLE_FACTOR, 2.0f);
                this.LIGHT_MAX_IDLE_TIMEOUT = this.mParser.getDurationMillis(KEY_LIGHT_MAX_IDLE_TIMEOUT, 900000);
                this.LIGHT_IDLE_MAINTENANCE_MIN_BUDGET = this.mParser.getDurationMillis(KEY_LIGHT_IDLE_MAINTENANCE_MIN_BUDGET, 60000);
                this.LIGHT_IDLE_MAINTENANCE_MAX_BUDGET = this.mParser.getDurationMillis(KEY_LIGHT_IDLE_MAINTENANCE_MAX_BUDGET, BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS);
                this.MIN_LIGHT_MAINTENANCE_TIME = this.mParser.getDurationMillis(KEY_MIN_LIGHT_MAINTENANCE_TIME, 5000);
                this.MIN_DEEP_MAINTENANCE_TIME = this.mParser.getDurationMillis(KEY_MIN_DEEP_MAINTENANCE_TIME, 30000);
                long inactiveTimeoutDefault = ((long) ((this.mSmallBatteryDevice ? 15 : 30) * 60)) * 1000;
                this.INACTIVE_TIMEOUT = this.mParser.getDurationMillis(KEY_INACTIVE_TIMEOUT, inactiveTimeoutDefault);
                this.SENSING_TIMEOUT = this.mParser.getDurationMillis(KEY_SENSING_TIMEOUT, 240000);
                this.LOCATING_TIMEOUT = this.mParser.getDurationMillis(KEY_LOCATING_TIMEOUT, 30000);
                this.LOCATION_ACCURACY = this.mParser.getFloat(KEY_LOCATION_ACCURACY, 20.0f);
                long j = inactiveTimeoutDefault;
                this.MOTION_INACTIVE_TIMEOUT = this.mParser.getDurationMillis(KEY_MOTION_INACTIVE_TIMEOUT, 600000);
                this.IDLE_AFTER_INACTIVE_TIMEOUT = this.mParser.getDurationMillis(KEY_IDLE_AFTER_INACTIVE_TIMEOUT, ((long) ((this.mSmallBatteryDevice ? 15 : 30) * 60)) * 1000);
                this.IDLE_PENDING_TIMEOUT = this.mParser.getDurationMillis(KEY_IDLE_PENDING_TIMEOUT, BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS);
                this.MAX_IDLE_PENDING_TIMEOUT = this.mParser.getDurationMillis(KEY_MAX_IDLE_PENDING_TIMEOUT, 600000);
                this.IDLE_PENDING_FACTOR = this.mParser.getFloat(KEY_IDLE_PENDING_FACTOR, 2.0f);
                this.QUICK_DOZE_DELAY_TIMEOUT = this.mParser.getDurationMillis(KEY_QUICK_DOZE_DELAY_TIMEOUT, 60000);
                this.IDLE_TIMEOUT = this.mParser.getDurationMillis(KEY_IDLE_TIMEOUT, 3600000);
                this.MAX_IDLE_TIMEOUT = this.mParser.getDurationMillis(KEY_MAX_IDLE_TIMEOUT, 21600000);
                this.IDLE_FACTOR = this.mParser.getFloat(KEY_IDLE_FACTOR, 2.0f);
                this.MIN_TIME_TO_ALARM = this.mParser.getDurationMillis(KEY_MIN_TIME_TO_ALARM, 3600000);
                this.MAX_TEMP_APP_WHITELIST_DURATION = this.mParser.getDurationMillis(KEY_MAX_TEMP_APP_WHITELIST_DURATION, BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS);
                this.MMS_TEMP_APP_WHITELIST_DURATION = this.mParser.getDurationMillis(KEY_MMS_TEMP_APP_WHITELIST_DURATION, 60000);
                this.SMS_TEMP_APP_WHITELIST_DURATION = this.mParser.getDurationMillis(KEY_SMS_TEMP_APP_WHITELIST_DURATION, ActivityManagerServiceInjector.KEEP_FOREGROUND_DURATION);
                this.NOTIFICATION_WHITELIST_DURATION = this.mParser.getDurationMillis(KEY_NOTIFICATION_WHITELIST_DURATION, 30000);
                this.WAIT_FOR_UNLOCK = this.mParser.getBoolean(KEY_WAIT_FOR_UNLOCK, true);
                this.PRE_IDLE_FACTOR_LONG = this.mParser.getFloat(KEY_PRE_IDLE_FACTOR_LONG, 1.67f);
                this.PRE_IDLE_FACTOR_SHORT = this.mParser.getFloat(KEY_PRE_IDLE_FACTOR_SHORT, 0.33f);
            }
        }

        /* access modifiers changed from: package-private */
        public void dump(PrintWriter pw) {
            pw.println("  Settings:");
            pw.print("    ");
            pw.print(KEY_LIGHT_IDLE_AFTER_INACTIVE_TIMEOUT);
            pw.print("=");
            TimeUtils.formatDuration(this.LIGHT_IDLE_AFTER_INACTIVE_TIMEOUT, pw);
            pw.println();
            pw.print("    ");
            pw.print(KEY_LIGHT_PRE_IDLE_TIMEOUT);
            pw.print("=");
            TimeUtils.formatDuration(this.LIGHT_PRE_IDLE_TIMEOUT, pw);
            pw.println();
            pw.print("    ");
            pw.print(KEY_LIGHT_IDLE_TIMEOUT);
            pw.print("=");
            TimeUtils.formatDuration(this.LIGHT_IDLE_TIMEOUT, pw);
            pw.println();
            pw.print("    ");
            pw.print(KEY_LIGHT_IDLE_FACTOR);
            pw.print("=");
            pw.print(this.LIGHT_IDLE_FACTOR);
            pw.println();
            pw.print("    ");
            pw.print(KEY_LIGHT_MAX_IDLE_TIMEOUT);
            pw.print("=");
            TimeUtils.formatDuration(this.LIGHT_MAX_IDLE_TIMEOUT, pw);
            pw.println();
            pw.print("    ");
            pw.print(KEY_LIGHT_IDLE_MAINTENANCE_MIN_BUDGET);
            pw.print("=");
            TimeUtils.formatDuration(this.LIGHT_IDLE_MAINTENANCE_MIN_BUDGET, pw);
            pw.println();
            pw.print("    ");
            pw.print(KEY_LIGHT_IDLE_MAINTENANCE_MAX_BUDGET);
            pw.print("=");
            TimeUtils.formatDuration(this.LIGHT_IDLE_MAINTENANCE_MAX_BUDGET, pw);
            pw.println();
            pw.print("    ");
            pw.print(KEY_MIN_LIGHT_MAINTENANCE_TIME);
            pw.print("=");
            TimeUtils.formatDuration(this.MIN_LIGHT_MAINTENANCE_TIME, pw);
            pw.println();
            pw.print("    ");
            pw.print(KEY_MIN_DEEP_MAINTENANCE_TIME);
            pw.print("=");
            TimeUtils.formatDuration(this.MIN_DEEP_MAINTENANCE_TIME, pw);
            pw.println();
            pw.print("    ");
            pw.print(KEY_INACTIVE_TIMEOUT);
            pw.print("=");
            TimeUtils.formatDuration(this.INACTIVE_TIMEOUT, pw);
            pw.println();
            pw.print("    ");
            pw.print(KEY_SENSING_TIMEOUT);
            pw.print("=");
            TimeUtils.formatDuration(this.SENSING_TIMEOUT, pw);
            pw.println();
            pw.print("    ");
            pw.print(KEY_LOCATING_TIMEOUT);
            pw.print("=");
            TimeUtils.formatDuration(this.LOCATING_TIMEOUT, pw);
            pw.println();
            pw.print("    ");
            pw.print(KEY_LOCATION_ACCURACY);
            pw.print("=");
            pw.print(this.LOCATION_ACCURACY);
            pw.print("m");
            pw.println();
            pw.print("    ");
            pw.print(KEY_MOTION_INACTIVE_TIMEOUT);
            pw.print("=");
            TimeUtils.formatDuration(this.MOTION_INACTIVE_TIMEOUT, pw);
            pw.println();
            pw.print("    ");
            pw.print(KEY_IDLE_AFTER_INACTIVE_TIMEOUT);
            pw.print("=");
            TimeUtils.formatDuration(this.IDLE_AFTER_INACTIVE_TIMEOUT, pw);
            pw.println();
            pw.print("    ");
            pw.print(KEY_IDLE_PENDING_TIMEOUT);
            pw.print("=");
            TimeUtils.formatDuration(this.IDLE_PENDING_TIMEOUT, pw);
            pw.println();
            pw.print("    ");
            pw.print(KEY_MAX_IDLE_PENDING_TIMEOUT);
            pw.print("=");
            TimeUtils.formatDuration(this.MAX_IDLE_PENDING_TIMEOUT, pw);
            pw.println();
            pw.print("    ");
            pw.print(KEY_IDLE_PENDING_FACTOR);
            pw.print("=");
            pw.println(this.IDLE_PENDING_FACTOR);
            pw.print("    ");
            pw.print(KEY_QUICK_DOZE_DELAY_TIMEOUT);
            pw.print("=");
            TimeUtils.formatDuration(this.QUICK_DOZE_DELAY_TIMEOUT, pw);
            pw.println();
            pw.print("    ");
            pw.print(KEY_IDLE_TIMEOUT);
            pw.print("=");
            TimeUtils.formatDuration(this.IDLE_TIMEOUT, pw);
            pw.println();
            pw.print("    ");
            pw.print(KEY_MAX_IDLE_TIMEOUT);
            pw.print("=");
            TimeUtils.formatDuration(this.MAX_IDLE_TIMEOUT, pw);
            pw.println();
            pw.print("    ");
            pw.print(KEY_IDLE_FACTOR);
            pw.print("=");
            pw.println(this.IDLE_FACTOR);
            pw.print("    ");
            pw.print(KEY_MIN_TIME_TO_ALARM);
            pw.print("=");
            TimeUtils.formatDuration(this.MIN_TIME_TO_ALARM, pw);
            pw.println();
            pw.print("    ");
            pw.print(KEY_MAX_TEMP_APP_WHITELIST_DURATION);
            pw.print("=");
            TimeUtils.formatDuration(this.MAX_TEMP_APP_WHITELIST_DURATION, pw);
            pw.println();
            pw.print("    ");
            pw.print(KEY_MMS_TEMP_APP_WHITELIST_DURATION);
            pw.print("=");
            TimeUtils.formatDuration(this.MMS_TEMP_APP_WHITELIST_DURATION, pw);
            pw.println();
            pw.print("    ");
            pw.print(KEY_SMS_TEMP_APP_WHITELIST_DURATION);
            pw.print("=");
            TimeUtils.formatDuration(this.SMS_TEMP_APP_WHITELIST_DURATION, pw);
            pw.println();
            pw.print("    ");
            pw.print(KEY_NOTIFICATION_WHITELIST_DURATION);
            pw.print("=");
            TimeUtils.formatDuration(this.NOTIFICATION_WHITELIST_DURATION, pw);
            pw.println();
            pw.print("    ");
            pw.print(KEY_WAIT_FOR_UNLOCK);
            pw.print("=");
            pw.println(this.WAIT_FOR_UNLOCK);
            pw.print("    ");
            pw.print(KEY_PRE_IDLE_FACTOR_LONG);
            pw.print("=");
            pw.println(this.PRE_IDLE_FACTOR_LONG);
            pw.print("    ");
            pw.print(KEY_PRE_IDLE_FACTOR_SHORT);
            pw.print("=");
            pw.println(this.PRE_IDLE_FACTOR_SHORT);
        }
    }

    public void onAnyMotionResult(int result) {
        if (result != -1) {
            synchronized (this) {
                cancelSensingTimeoutAlarmLocked();
            }
        }
        if (result == 1 || result == -1) {
            synchronized (this) {
                handleMotionDetectedLocked(this.mConstants.INACTIVE_TIMEOUT, "non_stationary");
            }
        } else if (result == 0) {
            int i = this.mState;
            if (i == 3) {
                synchronized (this) {
                    this.mNotMoving = true;
                    stepIdleStateLocked("s:stationary");
                }
            } else if (i == 4) {
                synchronized (this) {
                    this.mNotMoving = true;
                    if (this.mLocated) {
                        stepIdleStateLocked("s:stationary");
                    }
                }
            }
        }
    }

    final class MyHandler extends Handler {
        MyHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            boolean lightChanged;
            boolean deepChanged;
            int i = msg.what;
            String str = UiModeManagerService.Shell.NIGHT_MODE_STR_UNKNOWN;
            boolean monitoring = true;
            switch (i) {
                case 1:
                    DeviceIdleController.this.handleWriteConfigFile();
                    return;
                case 2:
                case 3:
                    EventLogTags.writeDeviceIdleOnStart();
                    int i2 = 2;
                    if (msg.what == 2) {
                        deepChanged = DeviceIdleController.this.mLocalPowerManager.setDeviceIdleMode(true);
                        lightChanged = DeviceIdleController.this.mLocalPowerManager.setLightDeviceIdleMode(false);
                    } else {
                        deepChanged = DeviceIdleController.this.mLocalPowerManager.setDeviceIdleMode(false);
                        lightChanged = DeviceIdleController.this.mLocalPowerManager.setLightDeviceIdleMode(true);
                    }
                    try {
                        DeviceIdleController.this.mNetworkPolicyManager.setDeviceIdleMode(true);
                        IBatteryStats access$600 = DeviceIdleController.this.mBatteryStats;
                        if (msg.what != 2) {
                            i2 = 1;
                        }
                        access$600.noteDeviceIdleMode(i2, (String) null, Process.myUid());
                    } catch (RemoteException e) {
                    }
                    if (deepChanged) {
                        DeviceIdleController.this.getContext().sendBroadcastAsUser(DeviceIdleController.this.mIdleIntent, UserHandle.ALL);
                    }
                    if (lightChanged) {
                        DeviceIdleController.this.getContext().sendBroadcastAsUser(DeviceIdleController.this.mLightIdleIntent, UserHandle.ALL);
                    }
                    EventLogTags.writeDeviceIdleOnComplete();
                    DeviceIdleController.this.mGoingIdleWakeLock.release();
                    return;
                case 4:
                    EventLogTags.writeDeviceIdleOffStart(str);
                    boolean deepChanged2 = DeviceIdleController.this.mLocalPowerManager.setDeviceIdleMode(false);
                    boolean lightChanged2 = DeviceIdleController.this.mLocalPowerManager.setLightDeviceIdleMode(false);
                    try {
                        DeviceIdleController.this.mNetworkPolicyManager.setDeviceIdleMode(false);
                        DeviceIdleController.this.mBatteryStats.noteDeviceIdleMode(0, (String) null, Process.myUid());
                    } catch (RemoteException e2) {
                    }
                    if (deepChanged2) {
                        DeviceIdleController.this.incActiveIdleOps();
                        DeviceIdleController.this.getContext().sendOrderedBroadcastAsUser(DeviceIdleController.this.mIdleIntent, UserHandle.ALL, (String) null, DeviceIdleController.this.mIdleStartedDoneReceiver, (Handler) null, 0, (String) null, (Bundle) null);
                    }
                    if (lightChanged2) {
                        DeviceIdleController.this.incActiveIdleOps();
                        DeviceIdleController.this.getContext().sendOrderedBroadcastAsUser(DeviceIdleController.this.mLightIdleIntent, UserHandle.ALL, (String) null, DeviceIdleController.this.mIdleStartedDoneReceiver, (Handler) null, 0, (String) null, (Bundle) null);
                    }
                    DeviceIdleController.this.decActiveIdleOps();
                    EventLogTags.writeDeviceIdleOffComplete();
                    return;
                case 5:
                    String activeReason = (String) msg.obj;
                    int activeUid = msg.arg1;
                    if (activeReason != null) {
                        str = activeReason;
                    }
                    EventLogTags.writeDeviceIdleOffStart(str);
                    boolean deepChanged3 = DeviceIdleController.this.mLocalPowerManager.setDeviceIdleMode(false);
                    boolean lightChanged3 = DeviceIdleController.this.mLocalPowerManager.setLightDeviceIdleMode(false);
                    try {
                        DeviceIdleController.this.mNetworkPolicyManager.setDeviceIdleMode(false);
                        DeviceIdleController.this.mBatteryStats.noteDeviceIdleMode(0, activeReason, activeUid);
                    } catch (RemoteException e3) {
                    }
                    if (deepChanged3) {
                        DeviceIdleController.this.getContext().sendBroadcastAsUser(DeviceIdleController.this.mIdleIntent, UserHandle.ALL);
                    }
                    if (lightChanged3) {
                        DeviceIdleController.this.getContext().sendBroadcastAsUser(DeviceIdleController.this.mLightIdleIntent, UserHandle.ALL);
                    }
                    EventLogTags.writeDeviceIdleOffComplete();
                    return;
                case 6:
                    DeviceIdleController.this.checkTempAppWhitelistTimeout(msg.arg1);
                    return;
                case 7:
                    if (msg.arg1 != 1) {
                        monitoring = false;
                    }
                    boolean active = monitoring;
                    int size = DeviceIdleController.this.mMaintenanceActivityListeners.beginBroadcast();
                    for (int i3 = 0; i3 < size; i3++) {
                        try {
                            DeviceIdleController.this.mMaintenanceActivityListeners.getBroadcastItem(i3).onMaintenanceActivityChanged(active);
                        } catch (RemoteException e4) {
                        } catch (Throwable th) {
                            DeviceIdleController.this.mMaintenanceActivityListeners.finishBroadcast();
                            throw th;
                        }
                    }
                    DeviceIdleController.this.mMaintenanceActivityListeners.finishBroadcast();
                    return;
                case 8:
                    DeviceIdleController.this.decActiveIdleOps();
                    return;
                case 9:
                    int appId = msg.arg1;
                    if (msg.arg2 != 1) {
                        monitoring = false;
                    }
                    DeviceIdleController.this.mNetworkPolicyManagerInternal.onTempPowerSaveWhitelistChange(appId, monitoring);
                    return;
                case 10:
                    IDeviceIdleConstraint constraint = (IDeviceIdleConstraint) msg.obj;
                    if (msg.arg1 != 1) {
                        monitoring = false;
                    }
                    if (monitoring) {
                        constraint.startMonitoring();
                        return;
                    } else {
                        constraint.stopMonitoring();
                        return;
                    }
                case 11:
                    DeviceIdleController.this.updatePreIdleFactor();
                    return;
                case 12:
                    DeviceIdleController.this.updatePreIdleFactor();
                    DeviceIdleController.this.maybeDoImmediateMaintenance();
                    return;
                default:
                    return;
            }
        }
    }

    private final class BinderService extends IDeviceIdleController.Stub {
        private BinderService() {
        }

        public void addPowerSaveWhitelistApp(String name) {
            DeviceIdleController.this.getContext().enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", (String) null);
            long ident = Binder.clearCallingIdentity();
            try {
                DeviceIdleController.this.addPowerSaveWhitelistAppInternal(name);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public void addPowerSaveWhitelistApps(String[] names) {
            DeviceIdleController.this.getContext().enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", (String) null);
            if (Binder.getCallingUid() == 1000) {
                long ident = Binder.clearCallingIdentity();
                try {
                    DeviceIdleController.this.addPowerSaveWhitelistAppsInternal(names);
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            } else {
                throw new SecurityException("Only the system can add whitelist apps more than one at once");
            }
        }

        public void removePowerSaveWhitelistApps(String[] names) {
            DeviceIdleController.this.getContext().enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", (String) null);
            if (Binder.getCallingUid() == 1000) {
                long ident = Binder.clearCallingIdentity();
                try {
                    DeviceIdleController.this.removePowerSaveWhitelistAppsInternal(names);
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            } else {
                throw new SecurityException("Only the system can remove whitelist apps more thanone at once");
            }
        }

        public void removePowerSaveWhitelistApp(String name) {
            DeviceIdleController.this.getContext().enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", (String) null);
            long ident = Binder.clearCallingIdentity();
            try {
                DeviceIdleController.this.removePowerSaveWhitelistAppInternal(name);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public void removeSystemPowerWhitelistApp(String name) {
            DeviceIdleController.this.getContext().enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", (String) null);
            long ident = Binder.clearCallingIdentity();
            try {
                DeviceIdleController.this.removeSystemPowerWhitelistAppInternal(name);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public void restoreSystemPowerWhitelistApp(String name) {
            DeviceIdleController.this.getContext().enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", (String) null);
            long ident = Binder.clearCallingIdentity();
            try {
                DeviceIdleController.this.restoreSystemPowerWhitelistAppInternal(name);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public String[] getRemovedSystemPowerWhitelistApps() {
            return DeviceIdleController.this.getRemovedSystemPowerWhitelistAppsInternal();
        }

        public String[] getSystemPowerWhitelistExceptIdle() {
            return DeviceIdleController.this.getSystemPowerWhitelistExceptIdleInternal();
        }

        public String[] getSystemPowerWhitelist() {
            return DeviceIdleController.this.getSystemPowerWhitelistInternal();
        }

        public String[] getUserPowerWhitelist() {
            return DeviceIdleController.this.getUserPowerWhitelistInternal();
        }

        public String[] getFullPowerWhitelistExceptIdle() {
            return DeviceIdleController.this.getFullPowerWhitelistExceptIdleInternal();
        }

        public String[] getFullPowerWhitelist() {
            return DeviceIdleController.this.getFullPowerWhitelistInternal();
        }

        public int[] getAppIdWhitelistExceptIdle() {
            return DeviceIdleController.this.getAppIdWhitelistExceptIdleInternal();
        }

        public int[] getAppIdWhitelist() {
            return DeviceIdleController.this.getAppIdWhitelistInternal();
        }

        public int[] getAppIdUserWhitelist() {
            return DeviceIdleController.this.getAppIdUserWhitelistInternal();
        }

        public int[] getAppIdTempWhitelist() {
            return DeviceIdleController.this.getAppIdTempWhitelistInternal();
        }

        public boolean isPowerSaveWhitelistExceptIdleApp(String name) {
            return DeviceIdleController.this.isPowerSaveWhitelistExceptIdleAppInternal(name);
        }

        public boolean isPowerSaveWhitelistApp(String name) {
            return DeviceIdleController.this.isPowerSaveWhitelistAppInternal(name);
        }

        public void addPowerSaveTempWhitelistApp(String packageName, long duration, int userId, String reason) throws RemoteException {
            DeviceIdleController.this.addPowerSaveTempWhitelistAppChecked(packageName, duration, userId, reason);
        }

        public long addPowerSaveTempWhitelistAppForMms(String packageName, int userId, String reason) throws RemoteException {
            long duration = DeviceIdleController.this.mConstants.MMS_TEMP_APP_WHITELIST_DURATION;
            DeviceIdleController.this.addPowerSaveTempWhitelistAppChecked(packageName, duration, userId, reason);
            return duration;
        }

        public long addPowerSaveTempWhitelistAppForSms(String packageName, int userId, String reason) throws RemoteException {
            long duration = DeviceIdleController.this.mConstants.SMS_TEMP_APP_WHITELIST_DURATION;
            DeviceIdleController.this.addPowerSaveTempWhitelistAppChecked(packageName, duration, userId, reason);
            return duration;
        }

        public void exitIdle(String reason) {
            DeviceIdleController.this.getContext().enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", (String) null);
            long ident = Binder.clearCallingIdentity();
            try {
                DeviceIdleController.this.exitIdleInternal(reason);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public boolean registerMaintenanceActivityListener(IMaintenanceActivityListener listener) {
            return DeviceIdleController.this.registerMaintenanceActivityListener(listener);
        }

        public void unregisterMaintenanceActivityListener(IMaintenanceActivityListener listener) {
            DeviceIdleController.this.unregisterMaintenanceActivityListener(listener);
        }

        public int setPreIdleTimeoutMode(int mode) {
            DeviceIdleController.this.getContext().enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", (String) null);
            long ident = Binder.clearCallingIdentity();
            try {
                return DeviceIdleController.this.setPreIdleTimeoutMode(mode);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public void resetPreIdleTimeoutMode() {
            DeviceIdleController.this.getContext().enforceCallingOrSelfPermission("android.permission.DEVICE_POWER", (String) null);
            long ident = Binder.clearCallingIdentity();
            try {
                DeviceIdleController.this.resetPreIdleTimeoutMode();
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        /* access modifiers changed from: protected */
        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            DeviceIdleController.this.dump(fd, pw, args);
        }

        /* JADX WARNING: type inference failed for: r1v1, types: [android.os.Binder] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onShellCommand(java.io.FileDescriptor r9, java.io.FileDescriptor r10, java.io.FileDescriptor r11, java.lang.String[] r12, android.os.ShellCallback r13, android.os.ResultReceiver r14) {
            /*
                r8 = this;
                com.android.server.DeviceIdleController$Shell r0 = new com.android.server.DeviceIdleController$Shell
                com.android.server.DeviceIdleController r1 = com.android.server.DeviceIdleController.this
                r0.<init>()
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.DeviceIdleController.BinderService.onShellCommand(java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):void");
        }
    }

    public class LocalService {
        public LocalService() {
        }

        public void onConstraintStateChanged(IDeviceIdleConstraint constraint, boolean active) {
            synchronized (DeviceIdleController.this) {
                DeviceIdleController.this.onConstraintStateChangedLocked(constraint, active);
            }
        }

        public void registerDeviceIdleConstraint(IDeviceIdleConstraint constraint, String name, int minState) {
            DeviceIdleController.this.registerDeviceIdleConstraintInternal(constraint, name, minState);
        }

        public void unregisterDeviceIdleConstraint(IDeviceIdleConstraint constraint) {
            DeviceIdleController.this.unregisterDeviceIdleConstraintInternal(constraint);
        }

        public void exitIdle(String reason) {
            DeviceIdleController.this.exitIdleInternal(reason);
        }

        public void addPowerSaveTempWhitelistApp(int callingUid, String packageName, long duration, int userId, boolean sync, String reason) {
            DeviceIdleController.this.addPowerSaveTempWhitelistAppInternal(callingUid, packageName, duration, userId, sync, reason);
        }

        public void addPowerSaveTempWhitelistAppDirect(int uid, long duration, boolean sync, String reason) {
            DeviceIdleController.this.addPowerSaveTempWhitelistAppDirectInternal(0, uid, duration, sync, reason);
        }

        public long getNotificationWhitelistDuration() {
            return DeviceIdleController.this.mConstants.NOTIFICATION_WHITELIST_DURATION;
        }

        public void setJobsActive(boolean active) {
            DeviceIdleController.this.setJobsActive(active);
        }

        public void setAlarmsActive(boolean active) {
            DeviceIdleController.this.setAlarmsActive(active);
        }

        public boolean isAppOnWhitelist(int appid) {
            return DeviceIdleController.this.isAppOnWhitelistInternal(appid);
        }

        public int[] getPowerSaveWhitelistUserAppIds() {
            return DeviceIdleController.this.getPowerSaveWhitelistUserAppIds();
        }

        public int[] getPowerSaveTempWhitelistAppIds() {
            return DeviceIdleController.this.getAppIdTempWhitelistInternal();
        }
    }

    static class Injector {
        private ConnectivityService mConnectivityService;
        private Constants mConstants;
        private final Context mContext;
        private LocationManager mLocationManager;

        Injector(Context ctx) {
            this.mContext = ctx;
        }

        /* access modifiers changed from: package-private */
        public AlarmManager getAlarmManager() {
            return (AlarmManager) this.mContext.getSystemService(AlarmManager.class);
        }

        /* access modifiers changed from: package-private */
        public AnyMotionDetector getAnyMotionDetector(Handler handler, SensorManager sm, AnyMotionDetector.DeviceIdleCallback callback, float angleThreshold) {
            return new AnyMotionDetector(getPowerManager(), handler, sm, callback, angleThreshold);
        }

        /* access modifiers changed from: package-private */
        public AppStateTracker getAppStateTracker(Context ctx, Looper looper) {
            return new AppStateTracker(ctx, looper);
        }

        /* access modifiers changed from: package-private */
        public ConnectivityService getConnectivityService() {
            if (this.mConnectivityService == null) {
                this.mConnectivityService = (ConnectivityService) ServiceManager.getService("connectivity");
            }
            return this.mConnectivityService;
        }

        /* access modifiers changed from: package-private */
        public Constants getConstants(DeviceIdleController controller, Handler handler, ContentResolver resolver) {
            if (this.mConstants == null) {
                Objects.requireNonNull(controller);
                this.mConstants = new Constants(handler, resolver);
            }
            return this.mConstants;
        }

        /* access modifiers changed from: package-private */
        public LocationManager getLocationManager() {
            if (this.mLocationManager == null) {
                this.mLocationManager = (LocationManager) this.mContext.getSystemService(LocationManager.class);
            }
            return this.mLocationManager;
        }

        /* access modifiers changed from: package-private */
        public MyHandler getHandler(DeviceIdleController controller) {
            Objects.requireNonNull(controller);
            return new MyHandler(BackgroundThread.getHandler().getLooper());
        }

        /* access modifiers changed from: package-private */
        public PowerManager getPowerManager() {
            return (PowerManager) this.mContext.getSystemService(PowerManager.class);
        }

        /* access modifiers changed from: package-private */
        public SensorManager getSensorManager() {
            return (SensorManager) this.mContext.getSystemService(SensorManager.class);
        }

        /* access modifiers changed from: package-private */
        public ConstraintController getConstraintController(Handler handler, LocalService localService) {
            if (this.mContext.getPackageManager().hasSystemFeature("android.software.leanback_only")) {
                return new TvConstraintController(this.mContext, handler);
            }
            return null;
        }

        /* access modifiers changed from: package-private */
        public boolean useMotionSensor() {
            return this.mContext.getResources().getBoolean(17891365);
        }
    }

    @VisibleForTesting
    DeviceIdleController(Context context, Injector injector) {
        super(context);
        this.mNumBlockingConstraints = 0;
        this.mConstraints = new ArrayMap<>();
        this.mMaintenanceActivityListeners = new RemoteCallbackList<>();
        this.mPowerSaveWhitelistAppsExceptIdle = new ArrayMap<>();
        this.mPowerSaveWhitelistUserAppsExceptIdle = new ArraySet<>();
        this.mPowerSaveWhitelistApps = new ArrayMap<>();
        this.mPowerSaveWhitelistUserApps = new ArrayMap<>();
        this.mPowerSaveWhitelistSystemAppIdsExceptIdle = new SparseBooleanArray();
        this.mPowerSaveWhitelistSystemAppIds = new SparseBooleanArray();
        this.mPowerSaveWhitelistExceptIdleAppIds = new SparseBooleanArray();
        this.mPowerSaveWhitelistExceptIdleAppIdArray = new int[0];
        this.mPowerSaveWhitelistAllAppIds = new SparseBooleanArray();
        this.mPowerSaveWhitelistAllAppIdArray = new int[0];
        this.mPowerSaveWhitelistUserAppIds = new SparseBooleanArray();
        this.mPowerSaveWhitelistUserAppIdArray = new int[0];
        this.mTempWhitelistAppIdEndTimes = new SparseArray<>();
        this.mTempWhitelistAppIdArray = new int[0];
        this.mRemovedFromSystemWhitelistApps = new ArrayMap<>();
        this.mEventCmds = new int[100];
        this.mEventTimes = new long[100];
        this.mEventReasons = new String[100];
        this.mReceiver = new BroadcastReceiver() {
            /* JADX WARNING: Removed duplicated region for block: B:17:0x003c  */
            /* JADX WARNING: Removed duplicated region for block: B:43:0x0081  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void onReceive(android.content.Context r7, android.content.Intent r8) {
                /*
                    r6 = this;
                    java.lang.String r0 = r8.getAction()
                    int r1 = r0.hashCode()
                    r2 = -1538406691(0xffffffffa44dc6dd, float:-4.4620733E-17)
                    r3 = 2
                    r4 = 0
                    r5 = 1
                    if (r1 == r2) goto L_0x002f
                    r2 = -1172645946(0xffffffffba1ad7c6, float:-5.9067865E-4)
                    if (r1 == r2) goto L_0x0025
                    r2 = 525384130(0x1f50b9c2, float:4.419937E-20)
                    if (r1 == r2) goto L_0x001b
                L_0x001a:
                    goto L_0x0039
                L_0x001b:
                    java.lang.String r1 = "android.intent.action.PACKAGE_REMOVED"
                    boolean r0 = r0.equals(r1)
                    if (r0 == 0) goto L_0x001a
                    r0 = r3
                    goto L_0x003a
                L_0x0025:
                    java.lang.String r1 = "android.net.conn.CONNECTIVITY_CHANGE"
                    boolean r0 = r0.equals(r1)
                    if (r0 == 0) goto L_0x001a
                    r0 = r4
                    goto L_0x003a
                L_0x002f:
                    java.lang.String r1 = "android.intent.action.BATTERY_CHANGED"
                    boolean r0 = r0.equals(r1)
                    if (r0 == 0) goto L_0x001a
                    r0 = r5
                    goto L_0x003a
                L_0x0039:
                    r0 = -1
                L_0x003a:
                    if (r0 == 0) goto L_0x0081
                    if (r0 == r5) goto L_0x005c
                    if (r0 == r3) goto L_0x0041
                    goto L_0x0087
                L_0x0041:
                    java.lang.String r0 = "android.intent.extra.REPLACING"
                    boolean r0 = r8.getBooleanExtra(r0, r4)
                    if (r0 != 0) goto L_0x0087
                    android.net.Uri r0 = r8.getData()
                    if (r0 == 0) goto L_0x0087
                    java.lang.String r1 = r0.getSchemeSpecificPart()
                    r2 = r1
                    if (r1 == 0) goto L_0x0087
                    com.android.server.DeviceIdleController r1 = com.android.server.DeviceIdleController.this
                    r1.removePowerSaveWhitelistAppInternal(r2)
                    goto L_0x0087
                L_0x005c:
                    java.lang.String r0 = "present"
                    boolean r0 = r8.getBooleanExtra(r0, r5)
                    java.lang.String r1 = "plugged"
                    int r1 = r8.getIntExtra(r1, r4)
                    if (r1 == 0) goto L_0x006e
                    r1 = r5
                    goto L_0x006f
                L_0x006e:
                    r1 = r4
                L_0x006f:
                    com.android.server.DeviceIdleController r2 = com.android.server.DeviceIdleController.this
                    monitor-enter(r2)
                    com.android.server.DeviceIdleController r3 = com.android.server.DeviceIdleController.this     // Catch:{ all -> 0x007e }
                    if (r0 == 0) goto L_0x0079
                    if (r1 == 0) goto L_0x0079
                    r4 = r5
                L_0x0079:
                    r3.updateChargingLocked(r4)     // Catch:{ all -> 0x007e }
                    monitor-exit(r2)     // Catch:{ all -> 0x007e }
                    goto L_0x0087
                L_0x007e:
                    r3 = move-exception
                    monitor-exit(r2)     // Catch:{ all -> 0x007e }
                    throw r3
                L_0x0081:
                    com.android.server.DeviceIdleController r0 = com.android.server.DeviceIdleController.this
                    r0.updateConnectivityState(r8)
                L_0x0087:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.server.DeviceIdleController.AnonymousClass1.onReceive(android.content.Context, android.content.Intent):void");
            }
        };
        this.mLightAlarmListener = new AlarmManager.OnAlarmListener() {
            public void onAlarm() {
                synchronized (DeviceIdleController.this) {
                    DeviceIdleController.this.stepLightIdleStateLocked("s:alarm");
                }
            }
        };
        this.mSensingTimeoutAlarmListener = new AlarmManager.OnAlarmListener() {
            public void onAlarm() {
                if (DeviceIdleController.this.mState == 3) {
                    synchronized (DeviceIdleController.this) {
                        DeviceIdleController.this.becomeInactiveIfAppropriateLocked();
                    }
                }
            }
        };
        this.mDeepAlarmListener = new AlarmManager.OnAlarmListener() {
            public void onAlarm() {
                synchronized (DeviceIdleController.this) {
                    DeviceIdleController.this.stepIdleStateLocked("s:alarm");
                }
            }
        };
        this.mIdleStartedDoneReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                if ("android.os.action.DEVICE_IDLE_MODE_CHANGED".equals(intent.getAction())) {
                    DeviceIdleController.this.mHandler.sendEmptyMessageDelayed(8, DeviceIdleController.this.mConstants.MIN_DEEP_MAINTENANCE_TIME);
                } else {
                    DeviceIdleController.this.mHandler.sendEmptyMessageDelayed(8, DeviceIdleController.this.mConstants.MIN_LIGHT_MAINTENANCE_TIME);
                }
            }
        };
        this.mInteractivityReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                synchronized (DeviceIdleController.this) {
                    DeviceIdleController.this.updateInteractivityLocked();
                }
            }
        };
        this.mMotionListener = new MotionListener();
        this.mGenericLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                synchronized (DeviceIdleController.this) {
                    DeviceIdleController.this.receivedGenericLocationLocked(location);
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        this.mGpsLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                synchronized (DeviceIdleController.this) {
                    DeviceIdleController.this.receivedGpsLocationLocked(location);
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
        this.mScreenObserver = new ActivityTaskManagerInternal.ScreenObserver() {
            public void onAwakeStateChanged(boolean isAwake) {
            }

            public void onKeyguardStateChanged(boolean isShowing) {
                synchronized (DeviceIdleController.this) {
                    DeviceIdleController.this.keyguardShowingLocked(isShowing);
                }
            }
        };
        this.mInjector = injector;
        this.mConfigFile = new AtomicFile(new File(getSystemDir(), "deviceidle.xml"));
        this.mHandler = this.mInjector.getHandler(this);
        this.mAppStateTracker = this.mInjector.getAppStateTracker(context, FgThread.get().getLooper());
        LocalServices.addService(AppStateTracker.class, this.mAppStateTracker);
        this.mUseMotionSensor = this.mInjector.useMotionSensor();
    }

    public DeviceIdleController(Context context) {
        this(context, new Injector(context));
    }

    /* access modifiers changed from: package-private */
    public boolean isAppOnWhitelistInternal(int appid) {
        boolean z;
        synchronized (this) {
            z = Arrays.binarySearch(this.mPowerSaveWhitelistAllAppIdArray, appid) >= 0;
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    public int[] getPowerSaveWhitelistUserAppIds() {
        int[] iArr;
        synchronized (this) {
            iArr = this.mPowerSaveWhitelistUserAppIdArray;
        }
        return iArr;
    }

    private static File getSystemDir() {
        return new File(Environment.getDataDirectory(), "system");
    }

    /* JADX WARNING: type inference failed for: r1v6, types: [com.android.server.DeviceIdleController$BinderService, android.os.IBinder] */
    public void onStart() {
        PackageManager pm = getContext().getPackageManager();
        synchronized (this) {
            boolean z = getContext().getResources().getBoolean(17891434);
            this.mDeepEnabled = z;
            this.mLightEnabled = z;
            SystemConfig sysConfig = SystemConfig.getInstance();
            ArraySet<String> allowPowerExceptIdle = sysConfig.getAllowInPowerSaveExceptIdle();
            for (int i = 0; i < allowPowerExceptIdle.size(); i++) {
                try {
                    ApplicationInfo ai = pm.getApplicationInfo(allowPowerExceptIdle.valueAt(i), DumpState.DUMP_DEXOPT);
                    int appid = UserHandle.getAppId(ai.uid);
                    this.mPowerSaveWhitelistAppsExceptIdle.put(ai.packageName, Integer.valueOf(appid));
                    this.mPowerSaveWhitelistSystemAppIdsExceptIdle.put(appid, true);
                } catch (PackageManager.NameNotFoundException e) {
                }
            }
            ArraySet<String> allowPower = sysConfig.getAllowInPowerSave();
            for (int i2 = 0; i2 < allowPower.size(); i2++) {
                try {
                    ApplicationInfo ai2 = pm.getApplicationInfo(allowPower.valueAt(i2), DumpState.DUMP_DEXOPT);
                    int appid2 = UserHandle.getAppId(ai2.uid);
                    this.mPowerSaveWhitelistAppsExceptIdle.put(ai2.packageName, Integer.valueOf(appid2));
                    this.mPowerSaveWhitelistSystemAppIdsExceptIdle.put(appid2, true);
                    this.mPowerSaveWhitelistApps.put(ai2.packageName, Integer.valueOf(appid2));
                    this.mPowerSaveWhitelistSystemAppIds.put(appid2, true);
                } catch (PackageManager.NameNotFoundException e2) {
                }
            }
            this.mConstants = this.mInjector.getConstants(this, this.mHandler, getContext().getContentResolver());
            readConfigFileLocked();
            updateWhitelistAppIdsLocked();
            this.mNetworkConnected = true;
            this.mScreenOn = true;
            this.mScreenLocked = false;
            this.mCharging = true;
            this.mActiveReason = 0;
            this.mState = 0;
            this.mLightState = 0;
            this.mInactiveTimeout = this.mConstants.INACTIVE_TIMEOUT;
            this.mPreIdleFactor = 1.0f;
            this.mLastPreIdleFactor = 1.0f;
        }
        this.mBinderService = new BinderService();
        publishBinderService("deviceidle", this.mBinderService);
        publishLocalService(LocalService.class, new LocalService());
    }

    public void onBootPhase(int phase) {
        if (phase == 500) {
            synchronized (this) {
                this.mAlarmManager = this.mInjector.getAlarmManager();
                this.mLocalAlarmManager = (AlarmManagerInternal) getLocalService(AlarmManagerInternal.class);
                this.mBatteryStats = BatteryStatsService.getService();
                this.mLocalActivityManager = (ActivityManagerInternal) getLocalService(ActivityManagerInternal.class);
                this.mLocalActivityTaskManager = (ActivityTaskManagerInternal) getLocalService(ActivityTaskManagerInternal.class);
                this.mLocalPowerManager = (PowerManagerInternal) getLocalService(PowerManagerInternal.class);
                this.mPowerManager = this.mInjector.getPowerManager();
                this.mActiveIdleWakeLock = this.mPowerManager.newWakeLock(1, "deviceidle_maint");
                this.mActiveIdleWakeLock.setReferenceCounted(false);
                this.mGoingIdleWakeLock = this.mPowerManager.newWakeLock(1, "deviceidle_going_idle");
                this.mGoingIdleWakeLock.setReferenceCounted(true);
                this.mNetworkPolicyManager = INetworkPolicyManager.Stub.asInterface(ServiceManager.getService("netpolicy"));
                this.mNetworkPolicyManagerInternal = (NetworkPolicyManagerInternal) getLocalService(NetworkPolicyManagerInternal.class);
                this.mSensorManager = this.mInjector.getSensorManager();
                if (this.mUseMotionSensor) {
                    int sigMotionSensorId = getContext().getResources().getInteger(17694742);
                    if (sigMotionSensorId > 0) {
                        this.mMotionSensor = this.mSensorManager.getDefaultSensor(sigMotionSensorId, true);
                    }
                    if (this.mMotionSensor == null && getContext().getResources().getBoolean(17891363)) {
                        this.mMotionSensor = this.mSensorManager.getDefaultSensor(26, true);
                    }
                    if (this.mMotionSensor == null) {
                        this.mMotionSensor = this.mSensorManager.getDefaultSensor(17, true);
                    }
                }
                if (getContext().getResources().getBoolean(17891364)) {
                    this.mLocationRequest = new LocationRequest().setQuality(100).setInterval(0).setFastestInterval(0).setNumUpdates(1);
                }
                this.mConstraintController = this.mInjector.getConstraintController(this.mHandler, (LocalService) getLocalService(LocalService.class));
                if (this.mConstraintController != null) {
                    this.mConstraintController.start();
                }
                this.mAnyMotionDetector = this.mInjector.getAnyMotionDetector(this.mHandler, this.mSensorManager, this, ((float) getContext().getResources().getInteger(17694743)) / 100.0f);
                this.mAppStateTracker.onSystemServicesReady();
                this.mIdleIntent = new Intent("android.os.action.DEVICE_IDLE_MODE_CHANGED");
                this.mIdleIntent.addFlags(1342177280);
                this.mLightIdleIntent = new Intent("android.os.action.LIGHT_DEVICE_IDLE_MODE_CHANGED");
                this.mLightIdleIntent.addFlags(1342177280);
                IntentFilter filter = new IntentFilter();
                filter.addAction("android.intent.action.BATTERY_CHANGED");
                getContext().registerReceiver(this.mReceiver, filter);
                IntentFilter filter2 = new IntentFilter();
                filter2.addAction("android.intent.action.PACKAGE_REMOVED");
                filter2.addDataScheme(com.android.server.pm.Settings.ATTR_PACKAGE);
                getContext().registerReceiver(this.mReceiver, filter2);
                IntentFilter filter3 = new IntentFilter();
                filter3.addAction("android.net.conn.CONNECTIVITY_CHANGE");
                getContext().registerReceiver(this.mReceiver, filter3);
                IntentFilter filter4 = new IntentFilter();
                filter4.addAction("android.intent.action.SCREEN_OFF");
                filter4.addAction("android.intent.action.SCREEN_ON");
                getContext().registerReceiver(this.mInteractivityReceiver, filter4);
                this.mLocalActivityManager.setDeviceIdleWhitelist(this.mPowerSaveWhitelistAllAppIdArray, this.mPowerSaveWhitelistExceptIdleAppIdArray);
                this.mLocalPowerManager.setDeviceIdleWhitelist(this.mPowerSaveWhitelistAllAppIdArray);
                this.mLocalPowerManager.registerLowPowerModeObserver(15, new Consumer() {
                    public final void accept(Object obj) {
                        DeviceIdleController.this.lambda$onBootPhase$0$DeviceIdleController((PowerSaveState) obj);
                    }
                });
                updateQuickDozeFlagLocked(this.mLocalPowerManager.getLowPowerState(15).batterySaverEnabled);
                this.mLocalActivityTaskManager.registerScreenObserver(this.mScreenObserver);
                passWhiteListsToForceAppStandbyTrackerLocked();
                updateInteractivityLocked();
            }
            updateConnectivityState((Intent) null);
        }
    }

    public /* synthetic */ void lambda$onBootPhase$0$DeviceIdleController(PowerSaveState state) {
        synchronized (this) {
            updateQuickDozeFlagLocked(state.batterySaverEnabled);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean hasMotionSensor() {
        return this.mUseMotionSensor && this.mMotionSensor != null;
    }

    /* access modifiers changed from: private */
    public void registerDeviceIdleConstraintInternal(IDeviceIdleConstraint constraint, String name, int type) {
        int minState;
        if (type == 0) {
            minState = 0;
        } else if (type != 1) {
            Slog.wtf(TAG, "Registering device-idle constraint with invalid type: " + type);
            return;
        } else {
            minState = 3;
        }
        synchronized (this) {
            if (this.mConstraints.containsKey(constraint)) {
                Slog.e(TAG, "Re-registering device-idle constraint: " + constraint + ".");
                return;
            }
            this.mConstraints.put(constraint, new DeviceIdleConstraintTracker(name, minState));
            updateActiveConstraintsLocked();
        }
    }

    /* access modifiers changed from: private */
    public void unregisterDeviceIdleConstraintInternal(IDeviceIdleConstraint constraint) {
        synchronized (this) {
            onConstraintStateChangedLocked(constraint, false);
            setConstraintMonitoringLocked(constraint, false);
            this.mConstraints.remove(constraint);
        }
    }

    /* access modifiers changed from: private */
    @GuardedBy({"this"})
    public void onConstraintStateChangedLocked(IDeviceIdleConstraint constraint, boolean active) {
        DeviceIdleConstraintTracker tracker = this.mConstraints.get(constraint);
        if (tracker == null) {
            Slog.e(TAG, "device-idle constraint " + constraint + " has not been registered.");
        } else if (active != tracker.active && tracker.monitoring) {
            tracker.active = active;
            this.mNumBlockingConstraints += tracker.active ? 1 : -1;
            if (this.mNumBlockingConstraints != 0) {
                return;
            }
            if (this.mState == 0) {
                becomeInactiveIfAppropriateLocked();
                return;
            }
            long j = this.mNextAlarmTime;
            if (j == 0 || j < SystemClock.elapsedRealtime()) {
                stepIdleStateLocked("s:" + tracker.name);
            }
        }
    }

    @GuardedBy({"this"})
    private void setConstraintMonitoringLocked(IDeviceIdleConstraint constraint, boolean monitor) {
        DeviceIdleConstraintTracker tracker = this.mConstraints.get(constraint);
        if (tracker.monitoring != monitor) {
            tracker.monitoring = monitor;
            updateActiveConstraintsLocked();
            this.mHandler.obtainMessage(10, monitor, -1, constraint).sendToTarget();
        }
    }

    @GuardedBy({"this"})
    private void updateActiveConstraintsLocked() {
        this.mNumBlockingConstraints = 0;
        for (int i = 0; i < this.mConstraints.size(); i++) {
            IDeviceIdleConstraint constraint = this.mConstraints.keyAt(i);
            DeviceIdleConstraintTracker tracker = this.mConstraints.valueAt(i);
            boolean monitoring = tracker.minState == this.mState;
            if (monitoring != tracker.monitoring) {
                setConstraintMonitoringLocked(constraint, monitoring);
                tracker.active = monitoring;
            }
            if (tracker.monitoring && tracker.active) {
                this.mNumBlockingConstraints++;
            }
        }
    }

    public boolean addPowerSaveWhitelistAppInternal(String name) {
        synchronized (this) {
            try {
                if (this.mPowerSaveWhitelistUserApps.put(name, Integer.valueOf(UserHandle.getAppId(getContext().getPackageManager().getApplicationInfo(name, DumpState.DUMP_CHANGES).uid))) == null) {
                    reportPowerSaveWhitelistChangedLocked();
                    updateWhitelistAppIdsLocked();
                    writeConfigFileLocked();
                }
            } catch (PackageManager.NameNotFoundException e) {
                return false;
            } catch (Throwable th) {
                throw th;
            }
        }
        return true;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0058, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean addPowerSaveWhitelistAppsInternal(java.lang.String[] r10) {
        /*
            r9 = this;
            monitor-enter(r9)
            r0 = 0
            if (r10 == 0) goto L_0x0057
            r1 = 0
            int r2 = r10.length     // Catch:{ all -> 0x0059 }
            r3 = r1
            r1 = r0
        L_0x0008:
            if (r1 >= r2) goto L_0x0049
            r4 = r10[r1]     // Catch:{ all -> 0x0059 }
            android.content.Context r5 = r9.getContext()     // Catch:{ NameNotFoundException -> 0x002e }
            android.content.pm.PackageManager r5 = r5.getPackageManager()     // Catch:{ NameNotFoundException -> 0x002e }
            r6 = 4194304(0x400000, float:5.877472E-39)
            android.content.pm.ApplicationInfo r5 = r5.getApplicationInfo(r4, r6)     // Catch:{ NameNotFoundException -> 0x002e }
            android.util.ArrayMap<java.lang.String, java.lang.Integer> r6 = r9.mPowerSaveWhitelistUserApps     // Catch:{ NameNotFoundException -> 0x002e }
            int r7 = r5.uid     // Catch:{ NameNotFoundException -> 0x002e }
            int r7 = android.os.UserHandle.getAppId(r7)     // Catch:{ NameNotFoundException -> 0x002e }
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)     // Catch:{ NameNotFoundException -> 0x002e }
            java.lang.Object r6 = r6.put(r4, r7)     // Catch:{ NameNotFoundException -> 0x002e }
            if (r6 != 0) goto L_0x002d
            r3 = 1
        L_0x002d:
            goto L_0x0046
        L_0x002e:
            r5 = move-exception
            java.lang.String r6 = "DeviceIdleController"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x0059 }
            r7.<init>()     // Catch:{ all -> 0x0059 }
            java.lang.String r8 = "failed to add pkg: "
            r7.append(r8)     // Catch:{ all -> 0x0059 }
            r7.append(r4)     // Catch:{ all -> 0x0059 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x0059 }
            android.util.Slog.e(r6, r7, r5)     // Catch:{ all -> 0x0059 }
        L_0x0046:
            int r1 = r1 + 1
            goto L_0x0008
        L_0x0049:
            if (r3 == 0) goto L_0x0057
            r9.reportPowerSaveWhitelistChangedLocked()     // Catch:{ all -> 0x0059 }
            r9.updateWhitelistAppIdsLocked()     // Catch:{ all -> 0x0059 }
            r9.writeConfigFileLocked()     // Catch:{ all -> 0x0059 }
            monitor-exit(r9)     // Catch:{ all -> 0x0059 }
            r0 = 1
            return r0
        L_0x0057:
            monitor-exit(r9)     // Catch:{ all -> 0x0059 }
            return r0
        L_0x0059:
            r0 = move-exception
            monitor-exit(r9)     // Catch:{ all -> 0x0059 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DeviceIdleController.addPowerSaveWhitelistAppsInternal(java.lang.String[]):boolean");
    }

    public boolean removePowerSaveWhitelistAppsInternal(String[] names) {
        synchronized (this) {
            boolean update = false;
            for (String name : names) {
                if (this.mPowerSaveWhitelistUserApps.remove(name) != null) {
                    update = true;
                }
            }
            if (!update) {
                return false;
            }
            reportPowerSaveWhitelistChangedLocked();
            updateWhitelistAppIdsLocked();
            writeConfigFileLocked();
            return true;
        }
    }

    public boolean removePowerSaveWhitelistAppInternal(String name) {
        synchronized (this) {
            if (this.mPowerSaveWhitelistUserApps.remove(name) == null) {
                return false;
            }
            reportPowerSaveWhitelistChangedLocked();
            updateWhitelistAppIdsLocked();
            writeConfigFileLocked();
            return true;
        }
    }

    public boolean getPowerSaveWhitelistAppInternal(String name) {
        boolean containsKey;
        synchronized (this) {
            containsKey = this.mPowerSaveWhitelistUserApps.containsKey(name);
        }
        return containsKey;
    }

    /* access modifiers changed from: package-private */
    public void resetSystemPowerWhitelistInternal() {
        synchronized (this) {
            this.mPowerSaveWhitelistApps.putAll(this.mRemovedFromSystemWhitelistApps);
            this.mRemovedFromSystemWhitelistApps.clear();
            reportPowerSaveWhitelistChangedLocked();
            updateWhitelistAppIdsLocked();
            writeConfigFileLocked();
        }
    }

    public boolean restoreSystemPowerWhitelistAppInternal(String name) {
        synchronized (this) {
            if (!this.mRemovedFromSystemWhitelistApps.containsKey(name)) {
                return false;
            }
            this.mPowerSaveWhitelistApps.put(name, this.mRemovedFromSystemWhitelistApps.remove(name));
            reportPowerSaveWhitelistChangedLocked();
            updateWhitelistAppIdsLocked();
            writeConfigFileLocked();
            return true;
        }
    }

    public boolean removeSystemPowerWhitelistAppInternal(String name) {
        synchronized (this) {
            if (!this.mPowerSaveWhitelistApps.containsKey(name)) {
                return false;
            }
            this.mRemovedFromSystemWhitelistApps.put(name, this.mPowerSaveWhitelistApps.remove(name));
            reportPowerSaveWhitelistChangedLocked();
            updateWhitelistAppIdsLocked();
            writeConfigFileLocked();
            return true;
        }
    }

    public boolean addPowerSaveWhitelistExceptIdleInternal(String name) {
        synchronized (this) {
            try {
                if (this.mPowerSaveWhitelistAppsExceptIdle.put(name, Integer.valueOf(UserHandle.getAppId(getContext().getPackageManager().getApplicationInfo(name, DumpState.DUMP_CHANGES).uid))) == null) {
                    this.mPowerSaveWhitelistUserAppsExceptIdle.add(name);
                    reportPowerSaveWhitelistChangedLocked();
                    this.mPowerSaveWhitelistExceptIdleAppIdArray = buildAppIdArray(this.mPowerSaveWhitelistAppsExceptIdle, this.mPowerSaveWhitelistUserApps, this.mPowerSaveWhitelistExceptIdleAppIds);
                    passWhiteListsToForceAppStandbyTrackerLocked();
                }
            } catch (PackageManager.NameNotFoundException e) {
                return false;
            } catch (Throwable th) {
                throw th;
            }
        }
        return true;
    }

    public void resetPowerSaveWhitelistExceptIdleInternal() {
        synchronized (this) {
            if (this.mPowerSaveWhitelistAppsExceptIdle.removeAll(this.mPowerSaveWhitelistUserAppsExceptIdle)) {
                reportPowerSaveWhitelistChangedLocked();
                this.mPowerSaveWhitelistExceptIdleAppIdArray = buildAppIdArray(this.mPowerSaveWhitelistAppsExceptIdle, this.mPowerSaveWhitelistUserApps, this.mPowerSaveWhitelistExceptIdleAppIds);
                this.mPowerSaveWhitelistUserAppsExceptIdle.clear();
                passWhiteListsToForceAppStandbyTrackerLocked();
            }
        }
    }

    public boolean getPowerSaveWhitelistExceptIdleInternal(String name) {
        boolean containsKey;
        synchronized (this) {
            containsKey = this.mPowerSaveWhitelistAppsExceptIdle.containsKey(name);
        }
        return containsKey;
    }

    public String[] getSystemPowerWhitelistExceptIdleInternal() {
        String[] apps;
        synchronized (this) {
            int size = this.mPowerSaveWhitelistAppsExceptIdle.size();
            apps = new String[size];
            for (int i = 0; i < size; i++) {
                apps[i] = this.mPowerSaveWhitelistAppsExceptIdle.keyAt(i);
            }
        }
        return apps;
    }

    public String[] getSystemPowerWhitelistInternal() {
        String[] apps;
        synchronized (this) {
            int size = this.mPowerSaveWhitelistApps.size();
            apps = new String[size];
            for (int i = 0; i < size; i++) {
                apps[i] = this.mPowerSaveWhitelistApps.keyAt(i);
            }
        }
        return apps;
    }

    public String[] getRemovedSystemPowerWhitelistAppsInternal() {
        String[] apps;
        synchronized (this) {
            int size = this.mRemovedFromSystemWhitelistApps.size();
            apps = new String[size];
            for (int i = 0; i < size; i++) {
                apps[i] = this.mRemovedFromSystemWhitelistApps.keyAt(i);
            }
        }
        return apps;
    }

    public String[] getUserPowerWhitelistInternal() {
        String[] apps;
        synchronized (this) {
            apps = new String[this.mPowerSaveWhitelistUserApps.size()];
            for (int i = 0; i < this.mPowerSaveWhitelistUserApps.size(); i++) {
                apps[i] = this.mPowerSaveWhitelistUserApps.keyAt(i);
            }
        }
        return apps;
    }

    public String[] getFullPowerWhitelistExceptIdleInternal() {
        String[] apps;
        synchronized (this) {
            apps = new String[(this.mPowerSaveWhitelistAppsExceptIdle.size() + this.mPowerSaveWhitelistUserApps.size())];
            int cur = 0;
            for (int i = 0; i < this.mPowerSaveWhitelistAppsExceptIdle.size(); i++) {
                apps[cur] = this.mPowerSaveWhitelistAppsExceptIdle.keyAt(i);
                cur++;
            }
            for (int i2 = 0; i2 < this.mPowerSaveWhitelistUserApps.size(); i2++) {
                apps[cur] = this.mPowerSaveWhitelistUserApps.keyAt(i2);
                cur++;
            }
        }
        return apps;
    }

    public String[] getFullPowerWhitelistInternal() {
        String[] apps;
        synchronized (this) {
            apps = new String[(this.mPowerSaveWhitelistApps.size() + this.mPowerSaveWhitelistUserApps.size())];
            int cur = 0;
            for (int i = 0; i < this.mPowerSaveWhitelistApps.size(); i++) {
                apps[cur] = this.mPowerSaveWhitelistApps.keyAt(i);
                cur++;
            }
            for (int i2 = 0; i2 < this.mPowerSaveWhitelistUserApps.size(); i2++) {
                apps[cur] = this.mPowerSaveWhitelistUserApps.keyAt(i2);
                cur++;
            }
        }
        return apps;
    }

    public boolean isPowerSaveWhitelistExceptIdleAppInternal(String packageName) {
        boolean z;
        synchronized (this) {
            if (!this.mPowerSaveWhitelistAppsExceptIdle.containsKey(packageName)) {
                if (!this.mPowerSaveWhitelistUserApps.containsKey(packageName)) {
                    z = false;
                }
            }
            z = true;
        }
        return z;
    }

    public boolean isPowerSaveWhitelistAppInternal(String packageName) {
        boolean z;
        synchronized (this) {
            if (!this.mPowerSaveWhitelistApps.containsKey(packageName)) {
                if (!this.mPowerSaveWhitelistUserApps.containsKey(packageName)) {
                    z = false;
                }
            }
            z = true;
        }
        return z;
    }

    public int[] getAppIdWhitelistExceptIdleInternal() {
        int[] iArr;
        synchronized (this) {
            iArr = this.mPowerSaveWhitelistExceptIdleAppIdArray;
        }
        return iArr;
    }

    public int[] getAppIdWhitelistInternal() {
        int[] iArr;
        synchronized (this) {
            iArr = this.mPowerSaveWhitelistAllAppIdArray;
        }
        return iArr;
    }

    public int[] getAppIdUserWhitelistInternal() {
        int[] iArr;
        synchronized (this) {
            iArr = this.mPowerSaveWhitelistUserAppIdArray;
        }
        return iArr;
    }

    public int[] getAppIdTempWhitelistInternal() {
        int[] iArr;
        synchronized (this) {
            iArr = this.mTempWhitelistAppIdArray;
        }
        return iArr;
    }

    /* access modifiers changed from: package-private */
    public void addPowerSaveTempWhitelistAppChecked(String packageName, long duration, int userId, String reason) throws RemoteException {
        getContext().enforceCallingPermission("android.permission.CHANGE_DEVICE_IDLE_TEMP_WHITELIST", "No permission to change device idle whitelist");
        int callingUid = Binder.getCallingUid();
        int userId2 = ActivityManager.getService().handleIncomingUser(Binder.getCallingPid(), callingUid, userId, false, false, "addPowerSaveTempWhitelistApp", (String) null);
        long token = Binder.clearCallingIdentity();
        try {
            addPowerSaveTempWhitelistAppInternal(callingUid, packageName, duration, userId2, true, reason);
            Binder.restoreCallingIdentity(token);
        } catch (Throwable th) {
            Throwable th2 = th;
            Binder.restoreCallingIdentity(token);
            throw th2;
        }
    }

    /* access modifiers changed from: package-private */
    public void removePowerSaveTempWhitelistAppChecked(String packageName, int userId) throws RemoteException {
        getContext().enforceCallingPermission("android.permission.CHANGE_DEVICE_IDLE_TEMP_WHITELIST", "No permission to change device idle whitelist");
        int userId2 = ActivityManager.getService().handleIncomingUser(Binder.getCallingPid(), Binder.getCallingUid(), userId, false, false, "removePowerSaveTempWhitelistApp", (String) null);
        long token = Binder.clearCallingIdentity();
        try {
            removePowerSaveTempWhitelistAppInternal(packageName, userId2);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    /* access modifiers changed from: package-private */
    public void addPowerSaveTempWhitelistAppInternal(int callingUid, String packageName, long duration, int userId, boolean sync, String reason) {
        try {
            addPowerSaveTempWhitelistAppDirectInternal(callingUid, getContext().getPackageManager().getPackageUidAsUser(packageName, userId), duration, sync, reason);
        } catch (PackageManager.NameNotFoundException e) {
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 16 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00ad, code lost:
        if (r5 == false) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00af, code lost:
        r1.mNetworkPolicyManagerInternal.onTempPowerSaveWhitelistChange(r6, true);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void addPowerSaveTempWhitelistAppDirectInternal(int r17, int r18, long r19, boolean r21, java.lang.String r22) {
        /*
            r16 = this;
            r1 = r16
            r2 = r22
            long r3 = android.os.SystemClock.elapsedRealtime()
            r5 = 0
            int r6 = android.os.UserHandle.getAppId(r18)
            monitor-enter(r16)
            int r0 = android.os.UserHandle.getAppId(r17)     // Catch:{ all -> 0x00bd }
            r7 = r0
            r0 = 10000(0x2710, float:1.4013E-41)
            if (r7 < r0) goto L_0x0047
            android.util.SparseBooleanArray r0 = r1.mPowerSaveWhitelistSystemAppIds     // Catch:{ all -> 0x0040 }
            boolean r0 = r0.get(r7)     // Catch:{ all -> 0x0040 }
            if (r0 == 0) goto L_0x0020
            goto L_0x0047
        L_0x0020:
            java.lang.SecurityException r0 = new java.lang.SecurityException     // Catch:{ all -> 0x0040 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x0040 }
            r8.<init>()     // Catch:{ all -> 0x0040 }
            java.lang.String r9 = "Calling app "
            r8.append(r9)     // Catch:{ all -> 0x0040 }
            java.lang.String r9 = android.os.UserHandle.formatUid(r17)     // Catch:{ all -> 0x0040 }
            r8.append(r9)     // Catch:{ all -> 0x0040 }
            java.lang.String r9 = " is not on whitelist"
            r8.append(r9)     // Catch:{ all -> 0x0040 }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x0040 }
            r0.<init>(r8)     // Catch:{ all -> 0x0040 }
            throw r0     // Catch:{ all -> 0x0040 }
        L_0x0040:
            r0 = move-exception
            r14 = r18
            r8 = r19
            goto L_0x00c3
        L_0x0047:
            com.android.server.DeviceIdleController$Constants r0 = r1.mConstants     // Catch:{ all -> 0x00bd }
            long r8 = r0.MAX_TEMP_APP_WHITELIST_DURATION     // Catch:{ all -> 0x00bd }
            r10 = r19
            long r8 = java.lang.Math.min(r10, r8)     // Catch:{ all -> 0x00b9 }
            android.util.SparseArray<android.util.Pair<android.util.MutableLong, java.lang.String>> r0 = r1.mTempWhitelistAppIdEndTimes     // Catch:{ all -> 0x00b5 }
            java.lang.Object r0 = r0.get(r6)     // Catch:{ all -> 0x00b5 }
            android.util.Pair r0 = (android.util.Pair) r0     // Catch:{ all -> 0x00b5 }
            r10 = 1
            if (r0 != 0) goto L_0x005e
            r11 = r10
            goto L_0x005f
        L_0x005e:
            r11 = 0
        L_0x005f:
            if (r11 == 0) goto L_0x0075
            android.util.Pair r12 = new android.util.Pair     // Catch:{ all -> 0x00b5 }
            android.util.MutableLong r13 = new android.util.MutableLong     // Catch:{ all -> 0x00b5 }
            r14 = 0
            r13.<init>(r14)     // Catch:{ all -> 0x00b5 }
            r12.<init>(r13, r2)     // Catch:{ all -> 0x00b5 }
            r0 = r12
            android.util.SparseArray<android.util.Pair<android.util.MutableLong, java.lang.String>> r12 = r1.mTempWhitelistAppIdEndTimes     // Catch:{ all -> 0x00b5 }
            r12.put(r6, r0)     // Catch:{ all -> 0x00b5 }
            r12 = r0
            goto L_0x0076
        L_0x0075:
            r12 = r0
        L_0x0076:
            java.lang.Object r0 = r12.first     // Catch:{ all -> 0x00b5 }
            android.util.MutableLong r0 = (android.util.MutableLong) r0     // Catch:{ all -> 0x00b5 }
            long r13 = r3 + r8
            r0.value = r13     // Catch:{ all -> 0x00b5 }
            if (r11 == 0) goto L_0x00aa
            com.android.internal.app.IBatteryStats r0 = r1.mBatteryStats     // Catch:{ RemoteException -> 0x008d }
            r13 = 32785(0x8011, float:4.5942E-41)
            r14 = r18
            r0.noteEvent(r13, r2, r14)     // Catch:{ RemoteException -> 0x008b }
            goto L_0x0090
        L_0x008b:
            r0 = move-exception
            goto L_0x0090
        L_0x008d:
            r0 = move-exception
            r14 = r18
        L_0x0090:
            r1.postTempActiveTimeoutMessage(r6, r8)     // Catch:{ all -> 0x00c5 }
            r1.updateTempWhitelistAppIdsLocked(r6, r10)     // Catch:{ all -> 0x00c5 }
            if (r21 == 0) goto L_0x009b
            r0 = 1
            r5 = r0
            goto L_0x00a6
        L_0x009b:
            com.android.server.DeviceIdleController$MyHandler r0 = r1.mHandler     // Catch:{ all -> 0x00c5 }
            r13 = 9
            android.os.Message r0 = r0.obtainMessage(r13, r6, r10)     // Catch:{ all -> 0x00c5 }
            r0.sendToTarget()     // Catch:{ all -> 0x00c5 }
        L_0x00a6:
            r16.reportTempWhitelistChangedLocked()     // Catch:{ all -> 0x00c5 }
            goto L_0x00ac
        L_0x00aa:
            r14 = r18
        L_0x00ac:
            monitor-exit(r16)     // Catch:{ all -> 0x00c5 }
            if (r5 == 0) goto L_0x00b4
            com.android.server.net.NetworkPolicyManagerInternal r0 = r1.mNetworkPolicyManagerInternal
            r0.onTempPowerSaveWhitelistChange(r6, r10)
        L_0x00b4:
            return
        L_0x00b5:
            r0 = move-exception
            r14 = r18
            goto L_0x00c3
        L_0x00b9:
            r0 = move-exception
            r14 = r18
            goto L_0x00c2
        L_0x00bd:
            r0 = move-exception
            r14 = r18
            r10 = r19
        L_0x00c2:
            r8 = r10
        L_0x00c3:
            monitor-exit(r16)     // Catch:{ all -> 0x00c5 }
            throw r0
        L_0x00c5:
            r0 = move-exception
            goto L_0x00c3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DeviceIdleController.addPowerSaveTempWhitelistAppDirectInternal(int, int, long, boolean, java.lang.String):void");
    }

    private void removePowerSaveTempWhitelistAppInternal(String packageName, int userId) {
        try {
            removePowerSaveTempWhitelistAppDirectInternal(UserHandle.getAppId(getContext().getPackageManager().getPackageUidAsUser(packageName, userId)));
        } catch (PackageManager.NameNotFoundException e) {
        }
    }

    private void removePowerSaveTempWhitelistAppDirectInternal(int appId) {
        synchronized (this) {
            int idx = this.mTempWhitelistAppIdEndTimes.indexOfKey(appId);
            if (idx >= 0) {
                this.mTempWhitelistAppIdEndTimes.removeAt(idx);
                onAppRemovedFromTempWhitelistLocked(appId, (String) this.mTempWhitelistAppIdEndTimes.valueAt(idx).second);
            }
        }
    }

    private void postTempActiveTimeoutMessage(int appId, long delay) {
        MyHandler myHandler = this.mHandler;
        myHandler.sendMessageDelayed(myHandler.obtainMessage(6, appId, 0), delay);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0033, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void checkTempAppWhitelistTimeout(int r6) {
        /*
            r5 = this;
            long r0 = android.os.SystemClock.elapsedRealtime()
            monitor-enter(r5)
            android.util.SparseArray<android.util.Pair<android.util.MutableLong, java.lang.String>> r2 = r5.mTempWhitelistAppIdEndTimes     // Catch:{ all -> 0x0034 }
            java.lang.Object r2 = r2.get(r6)     // Catch:{ all -> 0x0034 }
            android.util.Pair r2 = (android.util.Pair) r2     // Catch:{ all -> 0x0034 }
            if (r2 != 0) goto L_0x0011
            monitor-exit(r5)     // Catch:{ all -> 0x0034 }
            return
        L_0x0011:
            java.lang.Object r3 = r2.first     // Catch:{ all -> 0x0034 }
            android.util.MutableLong r3 = (android.util.MutableLong) r3     // Catch:{ all -> 0x0034 }
            long r3 = r3.value     // Catch:{ all -> 0x0034 }
            int r3 = (r0 > r3 ? 1 : (r0 == r3 ? 0 : -1))
            if (r3 < 0) goto L_0x0028
            android.util.SparseArray<android.util.Pair<android.util.MutableLong, java.lang.String>> r3 = r5.mTempWhitelistAppIdEndTimes     // Catch:{ all -> 0x0034 }
            r3.delete(r6)     // Catch:{ all -> 0x0034 }
            java.lang.Object r3 = r2.second     // Catch:{ all -> 0x0034 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x0034 }
            r5.onAppRemovedFromTempWhitelistLocked(r6, r3)     // Catch:{ all -> 0x0034 }
            goto L_0x0032
        L_0x0028:
            java.lang.Object r3 = r2.first     // Catch:{ all -> 0x0034 }
            android.util.MutableLong r3 = (android.util.MutableLong) r3     // Catch:{ all -> 0x0034 }
            long r3 = r3.value     // Catch:{ all -> 0x0034 }
            long r3 = r3 - r0
            r5.postTempActiveTimeoutMessage(r6, r3)     // Catch:{ all -> 0x0034 }
        L_0x0032:
            monitor-exit(r5)     // Catch:{ all -> 0x0034 }
            return
        L_0x0034:
            r2 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x0034 }
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DeviceIdleController.checkTempAppWhitelistTimeout(int):void");
    }

    @GuardedBy({"this"})
    private void onAppRemovedFromTempWhitelistLocked(int appId, String reason) {
        updateTempWhitelistAppIdsLocked(appId, false);
        this.mHandler.obtainMessage(9, appId, 0).sendToTarget();
        reportTempWhitelistChangedLocked();
        try {
            this.mBatteryStats.noteEvent(16401, reason, appId);
        } catch (RemoteException e) {
        }
    }

    public void exitIdleInternal(String reason) {
        synchronized (this) {
            this.mActiveReason = 5;
            becomeActiveLocked(reason, Binder.getCallingUid());
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isNetworkConnected() {
        boolean z;
        synchronized (this) {
            z = this.mNetworkConnected;
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x004a, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateConnectivityState(android.content.Intent r6) {
        /*
            r5 = this;
            monitor-enter(r5)
            com.android.server.DeviceIdleController$Injector r0 = r5.mInjector     // Catch:{ all -> 0x004e }
            com.android.server.ConnectivityService r0 = r0.getConnectivityService()     // Catch:{ all -> 0x004e }
            monitor-exit(r5)     // Catch:{ all -> 0x004e }
            if (r0 != 0) goto L_0x000b
            return
        L_0x000b:
            android.net.NetworkInfo r1 = r0.getActiveNetworkInfo()
            monitor-enter(r5)
            if (r1 != 0) goto L_0x0014
            r2 = 0
            goto L_0x0036
        L_0x0014:
            if (r6 != 0) goto L_0x001b
            boolean r2 = r1.isConnected()     // Catch:{ all -> 0x004b }
            goto L_0x0036
        L_0x001b:
            java.lang.String r2 = "networkType"
            r3 = -1
            int r2 = r6.getIntExtra(r2, r3)     // Catch:{ all -> 0x004b }
            int r3 = r1.getType()     // Catch:{ all -> 0x004b }
            if (r3 == r2) goto L_0x002b
            monitor-exit(r5)     // Catch:{ all -> 0x004b }
            return
        L_0x002b:
            java.lang.String r3 = "noConnectivity"
            r4 = 0
            boolean r3 = r6.getBooleanExtra(r3, r4)     // Catch:{ all -> 0x004b }
            r3 = r3 ^ 1
            r2 = r3
        L_0x0036:
            boolean r3 = r5.mNetworkConnected     // Catch:{ all -> 0x004b }
            if (r2 == r3) goto L_0x0049
            r5.mNetworkConnected = r2     // Catch:{ all -> 0x004b }
            if (r2 == 0) goto L_0x0049
            int r3 = r5.mLightState     // Catch:{ all -> 0x004b }
            r4 = 5
            if (r3 != r4) goto L_0x0049
            java.lang.String r3 = "network"
            r5.stepLightIdleStateLocked(r3)     // Catch:{ all -> 0x004b }
        L_0x0049:
            monitor-exit(r5)     // Catch:{ all -> 0x004b }
            return
        L_0x004b:
            r2 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x004b }
            throw r2
        L_0x004e:
            r0 = move-exception
            monitor-exit(r5)     // Catch:{ all -> 0x004e }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DeviceIdleController.updateConnectivityState(android.content.Intent):void");
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isScreenOn() {
        boolean z;
        synchronized (this) {
            z = this.mScreenOn;
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    public void updateInteractivityLocked() {
        boolean screenOn = this.mPowerManager.isInteractive();
        if (!screenOn && this.mScreenOn) {
            this.mScreenOn = false;
            if (!this.mForceIdle) {
                becomeInactiveIfAppropriateLocked();
            }
        } else if (screenOn) {
            this.mScreenOn = true;
            if (this.mForceIdle) {
                return;
            }
            if (!this.mScreenLocked || !this.mConstants.WAIT_FOR_UNLOCK) {
                this.mActiveReason = 2;
                becomeActiveLocked("screen", Process.myUid());
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isCharging() {
        boolean z;
        synchronized (this) {
            z = this.mCharging;
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    public void updateChargingLocked(boolean charging) {
        if (!charging && this.mCharging) {
            this.mCharging = false;
            if (!this.mForceIdle) {
                becomeInactiveIfAppropriateLocked();
            }
        } else if (charging) {
            this.mCharging = charging;
            if (!this.mForceIdle) {
                this.mActiveReason = 3;
                becomeActiveLocked("charging", Process.myUid());
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isQuickDozeEnabled() {
        boolean z;
        synchronized (this) {
            z = this.mQuickDozeActivated;
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void updateQuickDozeFlagLocked(boolean enabled) {
        this.mQuickDozeActivated = enabled;
        if (enabled) {
            becomeInactiveIfAppropriateLocked();
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isKeyguardShowing() {
        boolean z;
        synchronized (this) {
            z = this.mScreenLocked;
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void keyguardShowingLocked(boolean showing) {
        if (this.mScreenLocked != showing) {
            this.mScreenLocked = showing;
            if (this.mScreenOn && !this.mForceIdle && !this.mScreenLocked) {
                this.mActiveReason = 4;
                becomeActiveLocked("unlocked", Process.myUid());
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void scheduleReportActiveLocked(String activeReason, int activeUid) {
        this.mHandler.sendMessage(this.mHandler.obtainMessage(5, activeUid, 0, activeReason));
    }

    /* access modifiers changed from: package-private */
    public void becomeActiveLocked(String activeReason, int activeUid) {
        becomeActiveLocked(activeReason, activeUid, this.mConstants.INACTIVE_TIMEOUT, true);
    }

    private void becomeActiveLocked(String activeReason, int activeUid, long newInactiveTimeout, boolean changeLightIdle) {
        if (this.mState != 0 || this.mLightState != 0) {
            EventLogTags.writeDeviceIdle(0, activeReason);
            this.mState = 0;
            this.mInactiveTimeout = newInactiveTimeout;
            this.mCurIdleBudget = 0;
            this.mMaintenanceStartTime = 0;
            resetIdleManagementLocked();
            if (changeLightIdle) {
                EventLogTags.writeDeviceIdleLight(0, activeReason);
                this.mLightState = 0;
                resetLightIdleManagementLocked();
                scheduleReportActiveLocked(activeReason, activeUid);
                addEvent(1, activeReason);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setDeepEnabledForTest(boolean enabled) {
        synchronized (this) {
            this.mDeepEnabled = enabled;
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setLightEnabledForTest(boolean enabled) {
        synchronized (this) {
            this.mLightEnabled = enabled;
        }
    }

    private void verifyAlarmStateLocked() {
        if (this.mState == 0 && this.mNextAlarmTime != 0) {
            Slog.wtf(TAG, "mState=ACTIVE but mNextAlarmTime=" + this.mNextAlarmTime);
        }
        if (this.mState != 5 && this.mLocalAlarmManager.isIdling()) {
            Slog.wtf(TAG, "mState=" + stateToString(this.mState) + " but AlarmManager is idling");
        }
        if (this.mState == 5 && !this.mLocalAlarmManager.isIdling()) {
            Slog.wtf(TAG, "mState=IDLE but AlarmManager is not idling");
        }
        if (this.mLightState == 0 && this.mNextLightAlarmTime != 0) {
            Slog.wtf(TAG, "mLightState=ACTIVE but mNextLightAlarmTime is " + TimeUtils.formatDuration(this.mNextLightAlarmTime - SystemClock.elapsedRealtime()) + " from now");
        }
    }

    /* access modifiers changed from: package-private */
    public void becomeInactiveIfAppropriateLocked() {
        verifyAlarmStateLocked();
        boolean isScreenBlockingInactive = this.mScreenOn && (!this.mConstants.WAIT_FOR_UNLOCK || !this.mScreenLocked);
        if (this.mForceIdle || (!this.mCharging && !isScreenBlockingInactive)) {
            if (this.mDeepEnabled) {
                if (this.mQuickDozeActivated) {
                    int i = this.mState;
                    if (i != 7 && i != 5 && i != 6) {
                        this.mState = 7;
                        resetIdleManagementLocked();
                        scheduleAlarmLocked(this.mConstants.QUICK_DOZE_DELAY_TIMEOUT, false);
                        EventLogTags.writeDeviceIdle(this.mState, "no activity");
                    } else {
                        return;
                    }
                } else if (this.mState == 0) {
                    this.mState = 1;
                    resetIdleManagementLocked();
                    long delay = this.mInactiveTimeout;
                    if (shouldUseIdleTimeoutFactorLocked()) {
                        delay = (long) (this.mPreIdleFactor * ((float) delay));
                    }
                    scheduleAlarmLocked(delay, false);
                    EventLogTags.writeDeviceIdle(this.mState, "no activity");
                }
            }
            if (this.mLightState == 0 && this.mLightEnabled) {
                this.mLightState = 1;
                resetLightIdleManagementLocked();
                scheduleLightAlarmLocked(this.mConstants.LIGHT_IDLE_AFTER_INACTIVE_TIMEOUT);
                EventLogTags.writeDeviceIdleLight(this.mLightState, "no activity");
            }
        }
    }

    private void resetIdleManagementLocked() {
        this.mNextIdlePendingDelay = 0;
        this.mNextIdleDelay = 0;
        this.mNextLightIdleDelay = 0;
        this.mIdleStartTime = 0;
        cancelAlarmLocked();
        cancelSensingTimeoutAlarmLocked();
        cancelLocatingLocked();
        stopMonitoringMotionLocked();
        this.mAnyMotionDetector.stop();
        updateActiveConstraintsLocked();
    }

    private void resetLightIdleManagementLocked() {
        cancelLightAlarmLocked();
    }

    /* access modifiers changed from: package-private */
    public void exitForceIdleLocked() {
        if (this.mForceIdle) {
            this.mForceIdle = false;
            if (this.mScreenOn || this.mCharging) {
                this.mActiveReason = 6;
                becomeActiveLocked("exit-force", Process.myUid());
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setLightStateForTest(int lightState) {
        synchronized (this) {
            this.mLightState = lightState;
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public int getLightState() {
        return this.mLightState;
    }

    /* access modifiers changed from: package-private */
    public void stepLightIdleStateLocked(String reason) {
        if (this.mLightState != 7) {
            EventLogTags.writeDeviceIdleLightStep();
            int i = this.mLightState;
            if (i == 1) {
                this.mCurIdleBudget = this.mConstants.LIGHT_IDLE_MAINTENANCE_MIN_BUDGET;
                this.mNextLightIdleDelay = this.mConstants.LIGHT_IDLE_TIMEOUT;
                this.mMaintenanceStartTime = 0;
                if (!isOpsInactiveLocked()) {
                    this.mLightState = 3;
                    EventLogTags.writeDeviceIdleLight(this.mLightState, reason);
                    scheduleLightAlarmLocked(this.mConstants.LIGHT_PRE_IDLE_TIMEOUT);
                    return;
                }
            } else if (i != 3) {
                if (i == 4 || i == 5) {
                    if (this.mNetworkConnected || this.mLightState == 5) {
                        this.mActiveIdleOpCount = 1;
                        this.mActiveIdleWakeLock.acquire();
                        this.mMaintenanceStartTime = SystemClock.elapsedRealtime();
                        if (this.mCurIdleBudget < this.mConstants.LIGHT_IDLE_MAINTENANCE_MIN_BUDGET) {
                            this.mCurIdleBudget = this.mConstants.LIGHT_IDLE_MAINTENANCE_MIN_BUDGET;
                        } else if (this.mCurIdleBudget > this.mConstants.LIGHT_IDLE_MAINTENANCE_MAX_BUDGET) {
                            this.mCurIdleBudget = this.mConstants.LIGHT_IDLE_MAINTENANCE_MAX_BUDGET;
                        }
                        scheduleLightAlarmLocked(this.mCurIdleBudget);
                        this.mLightState = 6;
                        EventLogTags.writeDeviceIdleLight(this.mLightState, reason);
                        addEvent(3, (String) null);
                        this.mHandler.sendEmptyMessage(4);
                        return;
                    }
                    scheduleLightAlarmLocked(this.mNextLightIdleDelay);
                    this.mLightState = 5;
                    EventLogTags.writeDeviceIdleLight(this.mLightState, reason);
                    return;
                } else if (i != 6) {
                    return;
                }
            }
            if (this.mMaintenanceStartTime != 0) {
                long duration = SystemClock.elapsedRealtime() - this.mMaintenanceStartTime;
                if (duration < this.mConstants.LIGHT_IDLE_MAINTENANCE_MIN_BUDGET) {
                    this.mCurIdleBudget += this.mConstants.LIGHT_IDLE_MAINTENANCE_MIN_BUDGET - duration;
                } else {
                    this.mCurIdleBudget -= duration - this.mConstants.LIGHT_IDLE_MAINTENANCE_MIN_BUDGET;
                }
            }
            this.mMaintenanceStartTime = 0;
            scheduleLightAlarmLocked(this.mNextLightIdleDelay);
            this.mNextLightIdleDelay = Math.min(this.mConstants.LIGHT_MAX_IDLE_TIMEOUT, (long) (((float) this.mNextLightIdleDelay) * this.mConstants.LIGHT_IDLE_FACTOR));
            if (this.mNextLightIdleDelay < this.mConstants.LIGHT_IDLE_TIMEOUT) {
                this.mNextLightIdleDelay = this.mConstants.LIGHT_IDLE_TIMEOUT;
            }
            this.mLightState = 4;
            EventLogTags.writeDeviceIdleLight(this.mLightState, reason);
            addEvent(2, (String) null);
            this.mGoingIdleWakeLock.acquire();
            this.mHandler.sendEmptyMessage(3);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public int getState() {
        return this.mState;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x00be, code lost:
        cancelSensingTimeoutAlarmLocked();
        moveToStateLocked(4, r1);
        scheduleAlarmLocked(r0.mConstants.LOCATING_TIMEOUT, false);
        r4 = r0.mInjector.getLocationManager();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00d1, code lost:
        if (r4 == null) goto L_0x00ec;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x00da, code lost:
        if (r4.getProvider("network") == null) goto L_0x00ec;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00dc, code lost:
        r4.requestLocationUpdates(r0.mLocationRequest, r0.mGenericLocationListener, r0.mHandler.getLooper());
        r0.mLocating = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x00ec, code lost:
        r0.mHasNetworkLocation = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00ee, code lost:
        if (r4 == null) goto L_0x0111;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00f6, code lost:
        if (r4.getProvider("gps") == null) goto L_0x0111;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00f8, code lost:
        r0.mHasGps = true;
        r4.requestLocationUpdates("gps", 1000, 5.0f, r0.mGpsLocationListener, r0.mHandler.getLooper());
        r0.mLocating = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0111, code lost:
        r0.mHasGps = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x0115, code lost:
        if (r0.mLocating == false) goto L_0x0119;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0119, code lost:
        cancelAlarmLocked();
        cancelLocatingLocked();
        r0.mAnyMotionDetector.stop();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x0124, code lost:
        r0.mNextIdlePendingDelay = r0.mConstants.IDLE_PENDING_TIMEOUT;
        r0.mNextIdleDelay = r0.mConstants.IDLE_TIMEOUT;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x0130, code lost:
        scheduleAlarmLocked(r0.mNextIdleDelay, true);
        r0.mNextIdleDelay = (long) (((float) r0.mNextIdleDelay) * r0.mConstants.IDLE_FACTOR);
        r0.mIdleStartTime = android.os.SystemClock.elapsedRealtime();
        r0.mNextIdleDelay = java.lang.Math.min(r0.mNextIdleDelay, r0.mConstants.MAX_IDLE_TIMEOUT);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x015a, code lost:
        if (r0.mNextIdleDelay >= r0.mConstants.IDLE_TIMEOUT) goto L_0x0162;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x015c, code lost:
        r0.mNextIdleDelay = r0.mConstants.IDLE_TIMEOUT;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x0162, code lost:
        moveToStateLocked(5, r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x0167, code lost:
        if (r0.mLightState == 7) goto L_0x016e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x0169, code lost:
        r0.mLightState = 7;
        cancelLightAlarmLocked();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x016e, code lost:
        addEvent(4, (java.lang.String) null);
        r0.mGoingIdleWakeLock.acquire();
        r0.mHandler.sendEmptyMessage(2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:?, code lost:
        return;
     */
    @com.android.internal.annotations.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void stepIdleStateLocked(java.lang.String r20) {
        /*
            r19 = this;
            r0 = r19
            r1 = r20
            com.android.server.EventLogTags.writeDeviceIdleStep()
            long r2 = android.os.SystemClock.elapsedRealtime()
            com.android.server.DeviceIdleController$Constants r4 = r0.mConstants
            long r4 = r4.MIN_TIME_TO_ALARM
            long r4 = r4 + r2
            android.app.AlarmManager r6 = r0.mAlarmManager
            long r6 = r6.getNextWakeFromIdleTime()
            int r4 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            r5 = 7
            if (r4 <= 0) goto L_0x002e
            int r4 = r0.mState
            if (r4 == 0) goto L_0x002d
            r0.mActiveReason = r5
            int r4 = android.os.Process.myUid()
            java.lang.String r5 = "alarm"
            r0.becomeActiveLocked(r5, r4)
            r19.becomeInactiveIfAppropriateLocked()
        L_0x002d:
            return
        L_0x002e:
            int r4 = r0.mNumBlockingConstraints
            if (r4 == 0) goto L_0x0037
            boolean r4 = r0.mForceIdle
            if (r4 != 0) goto L_0x0037
            return
        L_0x0037:
            int r4 = r0.mState
            r6 = 2
            r7 = 5
            r8 = 4
            r9 = 0
            r10 = 1
            r11 = 0
            switch(r4) {
                case 1: goto L_0x017c;
                case 2: goto L_0x0087;
                case 3: goto L_0x00be;
                case 4: goto L_0x0119;
                case 5: goto L_0x0044;
                case 6: goto L_0x0130;
                case 7: goto L_0x0124;
                default: goto L_0x0042;
            }
        L_0x0042:
            goto L_0x0195
        L_0x0044:
            r0.mActiveIdleOpCount = r10
            android.os.PowerManager$WakeLock r4 = r0.mActiveIdleWakeLock
            r4.acquire()
            long r4 = r0.mNextIdlePendingDelay
            r0.scheduleAlarmLocked(r4, r11)
            long r4 = android.os.SystemClock.elapsedRealtime()
            r0.mMaintenanceStartTime = r4
            com.android.server.DeviceIdleController$Constants r4 = r0.mConstants
            long r4 = r4.MAX_IDLE_PENDING_TIMEOUT
            long r10 = r0.mNextIdlePendingDelay
            float r6 = (float) r10
            com.android.server.DeviceIdleController$Constants r10 = r0.mConstants
            float r10 = r10.IDLE_PENDING_FACTOR
            float r6 = r6 * r10
            long r10 = (long) r6
            long r4 = java.lang.Math.min(r4, r10)
            r0.mNextIdlePendingDelay = r4
            long r4 = r0.mNextIdlePendingDelay
            com.android.server.DeviceIdleController$Constants r6 = r0.mConstants
            long r10 = r6.IDLE_PENDING_TIMEOUT
            int r4 = (r4 > r10 ? 1 : (r4 == r10 ? 0 : -1))
            if (r4 >= 0) goto L_0x0079
            com.android.server.DeviceIdleController$Constants r4 = r0.mConstants
            long r4 = r4.IDLE_PENDING_TIMEOUT
            r0.mNextIdlePendingDelay = r4
        L_0x0079:
            r4 = 6
            r0.moveToStateLocked(r4, r1)
            r0.addEvent(r7, r9)
            com.android.server.DeviceIdleController$MyHandler r4 = r0.mHandler
            r4.sendEmptyMessage(r8)
            goto L_0x0195
        L_0x0087:
            r4 = 3
            r0.moveToStateLocked(r4, r1)
            r19.cancelLocatingLocked()
            r0.mLocated = r11
            r0.mLastGenericLocation = r9
            r0.mLastGpsLocation = r9
            r19.updateActiveConstraintsLocked()
            boolean r4 = r0.mUseMotionSensor
            if (r4 == 0) goto L_0x00b3
            com.android.server.AnyMotionDetector r4 = r0.mAnyMotionDetector
            boolean r4 = r4.hasSensor()
            if (r4 == 0) goto L_0x00b3
            com.android.server.DeviceIdleController$Constants r4 = r0.mConstants
            long r4 = r4.SENSING_TIMEOUT
            r0.scheduleSensingTimeoutAlarmLocked(r4)
            r0.mNotMoving = r11
            com.android.server.AnyMotionDetector r4 = r0.mAnyMotionDetector
            r4.checkForAnyMotion()
            goto L_0x0195
        L_0x00b3:
            int r4 = r0.mNumBlockingConstraints
            if (r4 == 0) goto L_0x00bc
            r19.cancelAlarmLocked()
            goto L_0x0195
        L_0x00bc:
            r0.mNotMoving = r10
        L_0x00be:
            r19.cancelSensingTimeoutAlarmLocked()
            r0.moveToStateLocked(r8, r1)
            com.android.server.DeviceIdleController$Constants r4 = r0.mConstants
            long r12 = r4.LOCATING_TIMEOUT
            r0.scheduleAlarmLocked(r12, r11)
            com.android.server.DeviceIdleController$Injector r4 = r0.mInjector
            android.location.LocationManager r4 = r4.getLocationManager()
            if (r4 == 0) goto L_0x00ec
            java.lang.String r12 = "network"
            android.location.LocationProvider r12 = r4.getProvider(r12)
            if (r12 == 0) goto L_0x00ec
            android.location.LocationRequest r12 = r0.mLocationRequest
            android.location.LocationListener r13 = r0.mGenericLocationListener
            com.android.server.DeviceIdleController$MyHandler r14 = r0.mHandler
            android.os.Looper r14 = r14.getLooper()
            r4.requestLocationUpdates(r12, r13, r14)
            r0.mLocating = r10
            goto L_0x00ee
        L_0x00ec:
            r0.mHasNetworkLocation = r11
        L_0x00ee:
            if (r4 == 0) goto L_0x0111
            java.lang.String r12 = "gps"
            android.location.LocationProvider r12 = r4.getProvider(r12)
            if (r12 == 0) goto L_0x0111
            r0.mHasGps = r10
            r14 = 1000(0x3e8, double:4.94E-321)
            r16 = 1084227584(0x40a00000, float:5.0)
            android.location.LocationListener r11 = r0.mGpsLocationListener
            com.android.server.DeviceIdleController$MyHandler r12 = r0.mHandler
            android.os.Looper r18 = r12.getLooper()
            java.lang.String r13 = "gps"
            r12 = r4
            r17 = r11
            r12.requestLocationUpdates(r13, r14, r16, r17, r18)
            r0.mLocating = r10
            goto L_0x0113
        L_0x0111:
            r0.mHasGps = r11
        L_0x0113:
            boolean r11 = r0.mLocating
            if (r11 == 0) goto L_0x0119
            goto L_0x0195
        L_0x0119:
            r19.cancelAlarmLocked()
            r19.cancelLocatingLocked()
            com.android.server.AnyMotionDetector r4 = r0.mAnyMotionDetector
            r4.stop()
        L_0x0124:
            com.android.server.DeviceIdleController$Constants r4 = r0.mConstants
            long r11 = r4.IDLE_PENDING_TIMEOUT
            r0.mNextIdlePendingDelay = r11
            com.android.server.DeviceIdleController$Constants r4 = r0.mConstants
            long r11 = r4.IDLE_TIMEOUT
            r0.mNextIdleDelay = r11
        L_0x0130:
            long r11 = r0.mNextIdleDelay
            r0.scheduleAlarmLocked(r11, r10)
            long r10 = r0.mNextIdleDelay
            float r4 = (float) r10
            com.android.server.DeviceIdleController$Constants r10 = r0.mConstants
            float r10 = r10.IDLE_FACTOR
            float r4 = r4 * r10
            long r10 = (long) r4
            r0.mNextIdleDelay = r10
            long r10 = android.os.SystemClock.elapsedRealtime()
            r0.mIdleStartTime = r10
            long r10 = r0.mNextIdleDelay
            com.android.server.DeviceIdleController$Constants r4 = r0.mConstants
            long r12 = r4.MAX_IDLE_TIMEOUT
            long r10 = java.lang.Math.min(r10, r12)
            r0.mNextIdleDelay = r10
            long r10 = r0.mNextIdleDelay
            com.android.server.DeviceIdleController$Constants r4 = r0.mConstants
            long r12 = r4.IDLE_TIMEOUT
            int r4 = (r10 > r12 ? 1 : (r10 == r12 ? 0 : -1))
            if (r4 >= 0) goto L_0x0162
            com.android.server.DeviceIdleController$Constants r4 = r0.mConstants
            long r10 = r4.IDLE_TIMEOUT
            r0.mNextIdleDelay = r10
        L_0x0162:
            r0.moveToStateLocked(r7, r1)
            int r4 = r0.mLightState
            if (r4 == r5) goto L_0x016e
            r0.mLightState = r5
            r19.cancelLightAlarmLocked()
        L_0x016e:
            r0.addEvent(r8, r9)
            android.os.PowerManager$WakeLock r4 = r0.mGoingIdleWakeLock
            r4.acquire()
            com.android.server.DeviceIdleController$MyHandler r4 = r0.mHandler
            r4.sendEmptyMessage(r6)
            goto L_0x0195
        L_0x017c:
            r19.startMonitoringMotionLocked()
            com.android.server.DeviceIdleController$Constants r4 = r0.mConstants
            long r4 = r4.IDLE_AFTER_INACTIVE_TIMEOUT
            boolean r7 = r19.shouldUseIdleTimeoutFactorLocked()
            if (r7 == 0) goto L_0x018e
            float r7 = r0.mPreIdleFactor
            float r8 = (float) r4
            float r7 = r7 * r8
            long r4 = (long) r7
        L_0x018e:
            r0.scheduleAlarmLocked(r4, r11)
            r0.moveToStateLocked(r6, r1)
        L_0x0195:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DeviceIdleController.stepIdleStateLocked(java.lang.String):void");
    }

    private void moveToStateLocked(int state, String reason) {
        int i = this.mState;
        this.mState = state;
        EventLogTags.writeDeviceIdle(this.mState, reason);
        updateActiveConstraintsLocked();
    }

    /* access modifiers changed from: package-private */
    public void incActiveIdleOps() {
        synchronized (this) {
            this.mActiveIdleOpCount++;
        }
    }

    /* access modifiers changed from: package-private */
    public void decActiveIdleOps() {
        synchronized (this) {
            this.mActiveIdleOpCount--;
            if (this.mActiveIdleOpCount <= 0) {
                exitMaintenanceEarlyIfNeededLocked();
                this.mActiveIdleWakeLock.release();
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setActiveIdleOpsForTest(int count) {
        synchronized (this) {
            this.mActiveIdleOpCount = count;
        }
    }

    /* access modifiers changed from: package-private */
    public void setJobsActive(boolean active) {
        synchronized (this) {
            this.mJobsActive = active;
            reportMaintenanceActivityIfNeededLocked();
            if (!active) {
                exitMaintenanceEarlyIfNeededLocked();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setAlarmsActive(boolean active) {
        synchronized (this) {
            this.mAlarmsActive = active;
            if (!active) {
                exitMaintenanceEarlyIfNeededLocked();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean registerMaintenanceActivityListener(IMaintenanceActivityListener listener) {
        boolean z;
        synchronized (this) {
            this.mMaintenanceActivityListeners.register(listener);
            z = this.mReportedMaintenanceActivity;
        }
        return z;
    }

    /* access modifiers changed from: package-private */
    public void unregisterMaintenanceActivityListener(IMaintenanceActivityListener listener) {
        synchronized (this) {
            this.mMaintenanceActivityListeners.unregister(listener);
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public int setPreIdleTimeoutMode(int mode) {
        return setPreIdleTimeoutFactor(getPreIdleTimeoutByMode(mode));
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public float getPreIdleTimeoutByMode(int mode) {
        if (mode == 0) {
            return 1.0f;
        }
        if (mode == 1) {
            return this.mConstants.PRE_IDLE_FACTOR_LONG;
        }
        if (mode == 2) {
            return this.mConstants.PRE_IDLE_FACTOR_SHORT;
        }
        Slog.w(TAG, "Invalid time out factor mode: " + mode);
        return 1.0f;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public float getPreIdleTimeoutFactor() {
        return this.mPreIdleFactor;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public int setPreIdleTimeoutFactor(float ratio) {
        if (!this.mDeepEnabled) {
            return 2;
        }
        if (ratio <= MIN_PRE_IDLE_FACTOR_CHANGE) {
            return 3;
        }
        if (Math.abs(ratio - this.mPreIdleFactor) < MIN_PRE_IDLE_FACTOR_CHANGE) {
            return 0;
        }
        synchronized (this) {
            this.mLastPreIdleFactor = this.mPreIdleFactor;
            this.mPreIdleFactor = ratio;
        }
        postUpdatePreIdleFactor();
        return 1;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void resetPreIdleTimeoutMode() {
        synchronized (this) {
            this.mLastPreIdleFactor = this.mPreIdleFactor;
            this.mPreIdleFactor = 1.0f;
        }
        postResetPreIdleTimeoutFactor();
    }

    private void postUpdatePreIdleFactor() {
        this.mHandler.sendEmptyMessage(11);
    }

    private void postResetPreIdleTimeoutFactor() {
        this.mHandler.sendEmptyMessage(12);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0046, code lost:
        return;
     */
    @com.android.internal.annotations.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updatePreIdleFactor() {
        /*
            r8 = this;
            monitor-enter(r8)
            boolean r0 = r8.shouldUseIdleTimeoutFactorLocked()     // Catch:{ all -> 0x0047 }
            if (r0 != 0) goto L_0x0009
            monitor-exit(r8)     // Catch:{ all -> 0x0047 }
            return
        L_0x0009:
            int r0 = r8.mState     // Catch:{ all -> 0x0047 }
            r1 = 1
            if (r0 == r1) goto L_0x0013
            int r0 = r8.mState     // Catch:{ all -> 0x0047 }
            r1 = 2
            if (r0 != r1) goto L_0x0045
        L_0x0013:
            long r0 = r8.mNextAlarmTime     // Catch:{ all -> 0x0047 }
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r0 != 0) goto L_0x001d
            monitor-exit(r8)     // Catch:{ all -> 0x0047 }
            return
        L_0x001d:
            long r0 = r8.mNextAlarmTime     // Catch:{ all -> 0x0047 }
            long r2 = android.os.SystemClock.elapsedRealtime()     // Catch:{ all -> 0x0047 }
            long r0 = r0 - r2
            r2 = 60000(0xea60, double:2.9644E-319)
            int r4 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            if (r4 >= 0) goto L_0x002d
            monitor-exit(r8)     // Catch:{ all -> 0x0047 }
            return
        L_0x002d:
            float r4 = (float) r0     // Catch:{ all -> 0x0047 }
            float r5 = r8.mLastPreIdleFactor     // Catch:{ all -> 0x0047 }
            float r4 = r4 / r5
            float r5 = r8.mPreIdleFactor     // Catch:{ all -> 0x0047 }
            float r4 = r4 * r5
            long r4 = (long) r4     // Catch:{ all -> 0x0047 }
            long r6 = r0 - r4
            long r6 = java.lang.Math.abs(r6)     // Catch:{ all -> 0x0047 }
            int r2 = (r6 > r2 ? 1 : (r6 == r2 ? 0 : -1))
            if (r2 >= 0) goto L_0x0041
            monitor-exit(r8)     // Catch:{ all -> 0x0047 }
            return
        L_0x0041:
            r2 = 0
            r8.scheduleAlarmLocked(r4, r2)     // Catch:{ all -> 0x0047 }
        L_0x0045:
            monitor-exit(r8)     // Catch:{ all -> 0x0047 }
            return
        L_0x0047:
            r0 = move-exception
            monitor-exit(r8)     // Catch:{ all -> 0x0047 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DeviceIdleController.updatePreIdleFactor():void");
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void maybeDoImmediateMaintenance() {
        synchronized (this) {
            if (this.mState == 5 && SystemClock.elapsedRealtime() - this.mIdleStartTime > this.mConstants.IDLE_TIMEOUT) {
                scheduleAlarmLocked(0, false);
            }
        }
    }

    private boolean shouldUseIdleTimeoutFactorLocked() {
        if (this.mActiveReason == 1) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void setIdleStartTimeForTest(long idleStartTime) {
        synchronized (this) {
            this.mIdleStartTime = idleStartTime;
        }
    }

    /* access modifiers changed from: package-private */
    public void reportMaintenanceActivityIfNeededLocked() {
        boolean active = this.mJobsActive;
        if (active != this.mReportedMaintenanceActivity) {
            this.mReportedMaintenanceActivity = active;
            this.mHandler.sendMessage(this.mHandler.obtainMessage(7, this.mReportedMaintenanceActivity ? 1 : 0, 0));
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public long getNextAlarmTime() {
        return this.mNextAlarmTime;
    }

    /* access modifiers changed from: package-private */
    public boolean isOpsInactiveLocked() {
        return this.mActiveIdleOpCount <= 0 && !this.mJobsActive && !this.mAlarmsActive;
    }

    /* access modifiers changed from: package-private */
    public void exitMaintenanceEarlyIfNeededLocked() {
        int i;
        if ((this.mState == 6 || (i = this.mLightState) == 6 || i == 3) && isOpsInactiveLocked()) {
            long elapsedRealtime = SystemClock.elapsedRealtime();
            if (this.mState == 6) {
                stepIdleStateLocked("s:early");
            } else if (this.mLightState == 3) {
                stepLightIdleStateLocked("s:predone");
            } else {
                stepLightIdleStateLocked("s:early");
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void motionLocked() {
        handleMotionDetectedLocked(this.mConstants.MOTION_INACTIVE_TIMEOUT, "motion");
    }

    /* access modifiers changed from: package-private */
    public void handleMotionDetectedLocked(long timeout, String type) {
        boolean becomeInactive = this.mState != 0 || this.mLightState == 7;
        becomeActiveLocked(type, Process.myUid(), timeout, this.mLightState == 7);
        if (becomeInactive) {
            becomeInactiveIfAppropriateLocked();
        }
    }

    /* access modifiers changed from: package-private */
    public void receivedGenericLocationLocked(Location location) {
        if (this.mState != 4) {
            cancelLocatingLocked();
            return;
        }
        this.mLastGenericLocation = new Location(location);
        if (location.getAccuracy() <= this.mConstants.LOCATION_ACCURACY || !this.mHasGps) {
            this.mLocated = true;
            if (this.mNotMoving) {
                stepIdleStateLocked("s:location");
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void receivedGpsLocationLocked(Location location) {
        if (this.mState != 4) {
            cancelLocatingLocked();
            return;
        }
        this.mLastGpsLocation = new Location(location);
        if (location.getAccuracy() <= this.mConstants.LOCATION_ACCURACY) {
            this.mLocated = true;
            if (this.mNotMoving) {
                stepIdleStateLocked("s:gps");
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void startMonitoringMotionLocked() {
        if (this.mMotionSensor != null && !this.mMotionListener.active) {
            this.mMotionListener.registerLocked();
        }
    }

    /* access modifiers changed from: package-private */
    public void stopMonitoringMotionLocked() {
        if (this.mMotionSensor != null && this.mMotionListener.active) {
            this.mMotionListener.unregisterLocked();
        }
    }

    /* access modifiers changed from: package-private */
    public void cancelAlarmLocked() {
        if (this.mNextAlarmTime != 0) {
            this.mNextAlarmTime = 0;
            this.mAlarmManager.cancel(this.mDeepAlarmListener);
        }
    }

    /* access modifiers changed from: package-private */
    public void cancelLightAlarmLocked() {
        if (this.mNextLightAlarmTime != 0) {
            this.mNextLightAlarmTime = 0;
            this.mAlarmManager.cancel(this.mLightAlarmListener);
        }
    }

    /* access modifiers changed from: package-private */
    public void cancelLocatingLocked() {
        if (this.mLocating) {
            LocationManager locationManager = this.mInjector.getLocationManager();
            locationManager.removeUpdates(this.mGenericLocationListener);
            locationManager.removeUpdates(this.mGpsLocationListener);
            this.mLocating = false;
        }
    }

    /* access modifiers changed from: package-private */
    public void cancelSensingTimeoutAlarmLocked() {
        if (this.mNextSensingTimeoutAlarmTime != 0) {
            this.mNextSensingTimeoutAlarmTime = 0;
            this.mAlarmManager.cancel(this.mSensingTimeoutAlarmListener);
        }
    }

    /* access modifiers changed from: package-private */
    public void scheduleAlarmLocked(long delay, boolean idleUntil) {
        int i;
        if (!this.mUseMotionSensor || this.mMotionSensor != null || (i = this.mState) == 7 || i == 5 || i == 6) {
            this.mNextAlarmTime = SystemClock.elapsedRealtime() + delay;
            if (idleUntil) {
                this.mAlarmManager.setIdleUntil(2, this.mNextAlarmTime, "DeviceIdleController.deep", this.mDeepAlarmListener, this.mHandler);
            } else {
                this.mAlarmManager.set(2, this.mNextAlarmTime, "DeviceIdleController.deep", this.mDeepAlarmListener, this.mHandler);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void scheduleLightAlarmLocked(long delay) {
        this.mNextLightAlarmTime = SystemClock.elapsedRealtime() + delay;
        this.mAlarmManager.set(2, this.mNextLightAlarmTime, "DeviceIdleController.light", this.mLightAlarmListener, this.mHandler);
    }

    /* access modifiers changed from: package-private */
    public void scheduleSensingTimeoutAlarmLocked(long delay) {
        this.mNextSensingTimeoutAlarmTime = SystemClock.elapsedRealtime() + delay;
        this.mAlarmManager.set(2, this.mNextSensingTimeoutAlarmTime, "DeviceIdleController.sensing", this.mSensingTimeoutAlarmListener, this.mHandler);
    }

    private static int[] buildAppIdArray(ArrayMap<String, Integer> systemApps, ArrayMap<String, Integer> userApps, SparseBooleanArray outAppIds) {
        outAppIds.clear();
        if (systemApps != null) {
            for (int i = 0; i < systemApps.size(); i++) {
                outAppIds.put(systemApps.valueAt(i).intValue(), true);
            }
        }
        if (userApps != null) {
            for (int i2 = 0; i2 < userApps.size(); i2++) {
                outAppIds.put(userApps.valueAt(i2).intValue(), true);
            }
        }
        int size = outAppIds.size();
        int[] appids = new int[size];
        for (int i3 = 0; i3 < size; i3++) {
            appids[i3] = outAppIds.keyAt(i3);
        }
        return appids;
    }

    private void updateWhitelistAppIdsLocked() {
        this.mPowerSaveWhitelistExceptIdleAppIdArray = buildAppIdArray(this.mPowerSaveWhitelistAppsExceptIdle, this.mPowerSaveWhitelistUserApps, this.mPowerSaveWhitelistExceptIdleAppIds);
        this.mPowerSaveWhitelistAllAppIdArray = buildAppIdArray(this.mPowerSaveWhitelistApps, this.mPowerSaveWhitelistUserApps, this.mPowerSaveWhitelistAllAppIds);
        this.mPowerSaveWhitelistUserAppIdArray = buildAppIdArray((ArrayMap<String, Integer>) null, this.mPowerSaveWhitelistUserApps, this.mPowerSaveWhitelistUserAppIds);
        ActivityManagerInternal activityManagerInternal = this.mLocalActivityManager;
        if (activityManagerInternal != null) {
            activityManagerInternal.setDeviceIdleWhitelist(this.mPowerSaveWhitelistAllAppIdArray, this.mPowerSaveWhitelistExceptIdleAppIdArray);
        }
        PowerManagerInternal powerManagerInternal = this.mLocalPowerManager;
        if (powerManagerInternal != null) {
            powerManagerInternal.setDeviceIdleWhitelist(this.mPowerSaveWhitelistAllAppIdArray);
        }
        passWhiteListsToForceAppStandbyTrackerLocked();
    }

    private void updateTempWhitelistAppIdsLocked(int appId, boolean adding) {
        int size = this.mTempWhitelistAppIdEndTimes.size();
        if (this.mTempWhitelistAppIdArray.length != size) {
            this.mTempWhitelistAppIdArray = new int[size];
        }
        for (int i = 0; i < size; i++) {
            this.mTempWhitelistAppIdArray[i] = this.mTempWhitelistAppIdEndTimes.keyAt(i);
        }
        ActivityManagerInternal activityManagerInternal = this.mLocalActivityManager;
        if (activityManagerInternal != null) {
            activityManagerInternal.updateDeviceIdleTempWhitelist(this.mTempWhitelistAppIdArray, appId, adding);
        }
        PowerManagerInternal powerManagerInternal = this.mLocalPowerManager;
        if (powerManagerInternal != null) {
            powerManagerInternal.setDeviceIdleTempWhitelist(this.mTempWhitelistAppIdArray);
        }
        passWhiteListsToForceAppStandbyTrackerLocked();
    }

    private void reportPowerSaveWhitelistChangedLocked() {
        Intent intent = new Intent("android.os.action.POWER_SAVE_WHITELIST_CHANGED");
        intent.addFlags(1073741824);
        getContext().sendBroadcastAsUser(intent, UserHandle.SYSTEM);
    }

    private void reportTempWhitelistChangedLocked() {
        Intent intent = new Intent("android.os.action.POWER_SAVE_TEMP_WHITELIST_CHANGED");
        intent.addFlags(1073741824);
        getContext().sendBroadcastAsUser(intent, UserHandle.SYSTEM);
    }

    private void passWhiteListsToForceAppStandbyTrackerLocked() {
        this.mAppStateTracker.setPowerSaveWhitelistAppIds(this.mPowerSaveWhitelistExceptIdleAppIdArray, this.mPowerSaveWhitelistUserAppIdArray, this.mTempWhitelistAppIdArray);
    }

    /* access modifiers changed from: package-private */
    public void readConfigFileLocked() {
        this.mPowerSaveWhitelistUserApps.clear();
        try {
            FileInputStream stream = this.mConfigFile.openRead();
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(stream, StandardCharsets.UTF_8.name());
                readConfigFileLocked(parser);
                try {
                    stream.close();
                } catch (IOException e) {
                }
            } catch (XmlPullParserException e2) {
                stream.close();
            } catch (Throwable th) {
                try {
                    stream.close();
                } catch (IOException e3) {
                }
                throw th;
            }
        } catch (FileNotFoundException e4) {
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 12 */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x0062  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x009a A[Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }] */
    /* JADX WARNING: Removed duplicated region for block: B:43:0x00bc A[SYNTHETIC, Splitter:B:43:0x00bc] */
    /* JADX WARNING: Removed duplicated region for block: B:7:0x001a A[Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void readConfigFileLocked(org.xmlpull.v1.XmlPullParser r13) {
        /*
            r12 = this;
            java.lang.String r0 = "Failed parsing config "
            java.lang.String r1 = "DeviceIdleController"
            android.content.Context r2 = r12.getContext()
            android.content.pm.PackageManager r2 = r2.getPackageManager()
        L_0x000c:
            int r3 = r13.next()     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
            r4 = r3
            r5 = 2
            r6 = 1
            if (r3 == r5) goto L_0x0018
            if (r4 == r6) goto L_0x0018
            goto L_0x000c
        L_0x0018:
            if (r4 != r5) goto L_0x00bc
            int r3 = r13.getDepth()     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
        L_0x001e:
            int r5 = r13.next()     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
            r4 = r5
            if (r5 == r6) goto L_0x013c
            r5 = 3
            if (r4 != r5) goto L_0x002e
            int r7 = r13.getDepth()     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
            if (r7 <= r3) goto L_0x013c
        L_0x002e:
            if (r4 == r5) goto L_0x001e
            r5 = 4
            if (r4 != r5) goto L_0x0034
            goto L_0x001e
        L_0x0034:
            java.lang.String r5 = r13.getName()     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
            r7 = -1
            int r8 = r5.hashCode()     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
            r9 = 3797(0xed5, float:5.321E-42)
            if (r8 == r9) goto L_0x0052
            r9 = 111376009(0x6a37689, float:6.1487957E-35)
            if (r8 == r9) goto L_0x0047
        L_0x0046:
            goto L_0x005c
        L_0x0047:
            java.lang.String r8 = "un-wl"
            boolean r8 = r5.equals(r8)     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
            if (r8 == 0) goto L_0x0046
            r7 = r6
            goto L_0x005c
        L_0x0052:
            java.lang.String r8 = "wl"
            boolean r8 = r5.equals(r8)     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
            if (r8 == 0) goto L_0x0046
            r7 = 0
        L_0x005c:
            java.lang.String r8 = "n"
            r9 = 0
            if (r7 == 0) goto L_0x009a
            if (r7 == r6) goto L_0x0080
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
            r7.<init>()     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
            java.lang.String r8 = "Unknown element under <config>: "
            r7.append(r8)     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
            java.lang.String r8 = r13.getName()     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
            r7.append(r8)     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
            java.lang.String r7 = r7.toString()     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
            android.util.Slog.w(r1, r7)     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
            com.android.internal.util.XmlUtils.skipCurrentTag(r13)     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
            goto L_0x00ba
        L_0x0080:
            java.lang.String r7 = r13.getAttributeValue(r9, r8)     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
            android.util.ArrayMap<java.lang.String, java.lang.Integer> r8 = r12.mPowerSaveWhitelistApps     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
            boolean r8 = r8.containsKey(r7)     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
            if (r8 == 0) goto L_0x00ba
            android.util.ArrayMap<java.lang.String, java.lang.Integer> r8 = r12.mRemovedFromSystemWhitelistApps     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
            android.util.ArrayMap<java.lang.String, java.lang.Integer> r9 = r12.mPowerSaveWhitelistApps     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
            java.lang.Object r9 = r9.remove(r7)     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
            java.lang.Integer r9 = (java.lang.Integer) r9     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
            r8.put(r7, r9)     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
            goto L_0x00ba
        L_0x009a:
            java.lang.String r7 = r13.getAttributeValue(r9, r8)     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
            if (r7 == 0) goto L_0x00ba
            r8 = 4194304(0x400000, float:5.877472E-39)
            android.content.pm.ApplicationInfo r8 = r2.getApplicationInfo(r7, r8)     // Catch:{ NameNotFoundException -> 0x00b9 }
            android.util.ArrayMap<java.lang.String, java.lang.Integer> r9 = r12.mPowerSaveWhitelistUserApps     // Catch:{ NameNotFoundException -> 0x00b9 }
            java.lang.String r10 = r8.packageName     // Catch:{ NameNotFoundException -> 0x00b9 }
            int r11 = r8.uid     // Catch:{ NameNotFoundException -> 0x00b9 }
            int r11 = android.os.UserHandle.getAppId(r11)     // Catch:{ NameNotFoundException -> 0x00b9 }
            java.lang.Integer r11 = java.lang.Integer.valueOf(r11)     // Catch:{ NameNotFoundException -> 0x00b9 }
            r9.put(r10, r11)     // Catch:{ NameNotFoundException -> 0x00b9 }
            goto L_0x00ba
        L_0x00b9:
            r8 = move-exception
        L_0x00ba:
            goto L_0x001e
        L_0x00bc:
            java.lang.IllegalStateException r3 = new java.lang.IllegalStateException     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
            java.lang.String r5 = "no start tag found"
            r3.<init>(r5)     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
            throw r3     // Catch:{ IllegalStateException -> 0x0129, NullPointerException -> 0x0115, NumberFormatException -> 0x0101, XmlPullParserException -> 0x00ed, IOException -> 0x00d9, IndexOutOfBoundsException -> 0x00c5 }
        L_0x00c5:
            r3 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r0)
            r4.append(r3)
            java.lang.String r0 = r4.toString()
            android.util.Slog.w(r1, r0)
            goto L_0x013d
        L_0x00d9:
            r3 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r0)
            r4.append(r3)
            java.lang.String r0 = r4.toString()
            android.util.Slog.w(r1, r0)
            goto L_0x013c
        L_0x00ed:
            r3 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r0)
            r4.append(r3)
            java.lang.String r0 = r4.toString()
            android.util.Slog.w(r1, r0)
            goto L_0x013c
        L_0x0101:
            r3 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r0)
            r4.append(r3)
            java.lang.String r0 = r4.toString()
            android.util.Slog.w(r1, r0)
            goto L_0x013c
        L_0x0115:
            r3 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r0)
            r4.append(r3)
            java.lang.String r0 = r4.toString()
            android.util.Slog.w(r1, r0)
            goto L_0x013c
        L_0x0129:
            r3 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            r4.append(r0)
            r4.append(r3)
            java.lang.String r0 = r4.toString()
            android.util.Slog.w(r1, r0)
        L_0x013c:
        L_0x013d:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DeviceIdleController.readConfigFileLocked(org.xmlpull.v1.XmlPullParser):void");
    }

    /* access modifiers changed from: package-private */
    public void writeConfigFileLocked() {
        this.mHandler.removeMessages(1);
        this.mHandler.sendEmptyMessageDelayed(1, 5000);
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    /* access modifiers changed from: package-private */
    public void handleWriteConfigFile() {
        ByteArrayOutputStream memStream = new ByteArrayOutputStream();
        try {
            synchronized (this) {
                XmlSerializer out = new FastXmlSerializer();
                out.setOutput(memStream, StandardCharsets.UTF_8.name());
                writeConfigFileLocked(out);
            }
        } catch (IOException e) {
        }
        synchronized (this.mConfigFile) {
            FileOutputStream stream = null;
            try {
                stream = this.mConfigFile.startWrite();
                memStream.writeTo(stream);
                stream.flush();
                FileUtils.sync(stream);
                stream.close();
                this.mConfigFile.finishWrite(stream);
            } catch (IOException e2) {
                Slog.w(TAG, "Error writing config file", e2);
                this.mConfigFile.failWrite(stream);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void writeConfigFileLocked(XmlSerializer out) throws IOException {
        out.startDocument((String) null, true);
        out.startTag((String) null, "config");
        for (int i = 0; i < this.mPowerSaveWhitelistUserApps.size(); i++) {
            out.startTag((String) null, "wl");
            out.attribute((String) null, "n", this.mPowerSaveWhitelistUserApps.keyAt(i));
            out.endTag((String) null, "wl");
        }
        for (int i2 = 0; i2 < this.mRemovedFromSystemWhitelistApps.size(); i2++) {
            out.startTag((String) null, "un-wl");
            out.attribute((String) null, "n", this.mRemovedFromSystemWhitelistApps.keyAt(i2));
            out.endTag((String) null, "un-wl");
        }
        out.endTag((String) null, "config");
        out.endDocument();
    }

    static void dumpHelp(PrintWriter pw) {
        pw.println("Device idle controller (deviceidle) commands:");
        pw.println("  help");
        pw.println("    Print this help text.");
        pw.println("  step [light|deep]");
        pw.println("    Immediately step to next state, without waiting for alarm.");
        pw.println("  force-idle [light|deep]");
        pw.println("    Force directly into idle mode, regardless of other device state.");
        pw.println("  force-inactive");
        pw.println("    Force to be inactive, ready to freely step idle states.");
        pw.println("  unforce");
        pw.println("    Resume normal functioning after force-idle or force-inactive.");
        pw.println("  get [light|deep|force|screen|charging|network]");
        pw.println("    Retrieve the current given state.");
        pw.println("  disable [light|deep|all]");
        pw.println("    Completely disable device idle mode.");
        pw.println("  enable [light|deep|all]");
        pw.println("    Re-enable device idle mode after it had previously been disabled.");
        pw.println("  enabled [light|deep|all]");
        pw.println("    Print 1 if device idle mode is currently enabled, else 0.");
        pw.println("  whitelist");
        pw.println("    Print currently whitelisted apps.");
        pw.println("  whitelist [package ...]");
        pw.println("    Add (prefix with +) or remove (prefix with -) packages.");
        pw.println("  sys-whitelist [package ...|reset]");
        pw.println("    Prefix the package with '-' to remove it from the system whitelist or '+' to put it back in the system whitelist.");
        pw.println("    Note that only packages that were earlier removed from the system whitelist can be added back.");
        pw.println("    reset will reset the whitelist to the original state");
        pw.println("    Prints the system whitelist if no arguments are specified");
        pw.println("  except-idle-whitelist [package ...|reset]");
        pw.println("    Prefix the package with '+' to add it to whitelist or '=' to check if it is already whitelisted");
        pw.println("    [reset] will reset the whitelist to it's original state");
        pw.println("    Note that unlike <whitelist> cmd, changes made using this won't be persisted across boots");
        pw.println("  tempwhitelist");
        pw.println("    Print packages that are temporarily whitelisted.");
        pw.println("  tempwhitelist [-u USER] [-d DURATION] [-r] [package]");
        pw.println("    Temporarily place package in whitelist for DURATION milliseconds.");
        pw.println("    If no DURATION is specified, 10 seconds is used");
        pw.println("    If [-r] option is used, then the package is removed from temp whitelist and any [-d] is ignored");
        pw.println("  motion");
        pw.println("    Simulate a motion event to bring the device out of deep doze");
        pw.println("  pre-idle-factor [0|1|2]");
        pw.println("    Set a new factor to idle time before step to idle(inactive_to and idle_after_inactive_to)");
        pw.println("  reset-pre-idle-factor");
        pw.println("    Reset factor to idle time to default");
    }

    class Shell extends ShellCommand {
        int userId = 0;

        Shell() {
        }

        public int onCommand(String cmd) {
            return DeviceIdleController.this.onShellCommand(this, cmd);
        }

        public void onHelp() {
            DeviceIdleController.dumpHelp(getOutPrintWriter());
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 19 */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* JADX WARNING: Code restructure failed: missing block: B:193:0x02e9, code lost:
        if ("all".equals(r3) == false) goto L_0x02f8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:237:0x038d, code lost:
        if ("all".equals(r3) == false) goto L_0x039c;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:330:?, code lost:
        r10.println("Package must be prefixed with +, -, or =: " + r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:331:0x050f, code lost:
        android.os.Binder.restoreCallingIdentity(r13);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:332:0x0513, code lost:
        return -1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:481:?, code lost:
        r10.println("Package must be prefixed with + or - " + r1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:482:0x07da, code lost:
        android.os.Binder.restoreCallingIdentity(r2);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:483:0x07de, code lost:
        return -1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int onShellCommand(com.android.server.DeviceIdleController.Shell r20, java.lang.String r21) {
        /*
            r19 = this;
            r7 = r19
            r8 = r20
            r9 = r21
            java.io.PrintWriter r10 = r20.getOutPrintWriter()
            java.lang.String r1 = "step"
            boolean r1 = r1.equals(r9)
            r2 = 0
            r11 = 0
            java.lang.Integer r3 = java.lang.Integer.valueOf(r11)
            if (r1 == 0) goto L_0x008e
            android.content.Context r1 = r19.getContext()
            java.lang.String r3 = "android.permission.DEVICE_POWER"
            r1.enforceCallingOrSelfPermission(r3, r2)
            monitor-enter(r19)
            long r1 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x008a }
            java.lang.String r3 = r20.getNextArg()     // Catch:{ all -> 0x008a }
            if (r3 == 0) goto L_0x0069
            java.lang.String r4 = "deep"
            boolean r4 = r4.equals(r3)     // Catch:{ all -> 0x0084 }
            if (r4 == 0) goto L_0x0036
            goto L_0x0069
        L_0x0036:
            java.lang.String r4 = "light"
            boolean r4 = r4.equals(r3)     // Catch:{ all -> 0x0084 }
            if (r4 == 0) goto L_0x0054
            java.lang.String r4 = "s:shell"
            r7.stepLightIdleStateLocked(r4)     // Catch:{ all -> 0x0084 }
            java.lang.String r4 = "Stepped to light: "
            r10.print(r4)     // Catch:{ all -> 0x0084 }
            int r4 = r7.mLightState     // Catch:{ all -> 0x0084 }
            java.lang.String r4 = lightStateToString(r4)     // Catch:{ all -> 0x0084 }
            r10.println(r4)     // Catch:{ all -> 0x0084 }
            goto L_0x007d
        L_0x0054:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0084 }
            r4.<init>()     // Catch:{ all -> 0x0084 }
            java.lang.String r5 = "Unknown idle mode: "
            r4.append(r5)     // Catch:{ all -> 0x0084 }
            r4.append(r3)     // Catch:{ all -> 0x0084 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0084 }
            r10.println(r4)     // Catch:{ all -> 0x0084 }
            goto L_0x007d
        L_0x0069:
            java.lang.String r4 = "s:shell"
            r7.stepIdleStateLocked(r4)     // Catch:{ all -> 0x0084 }
            java.lang.String r4 = "Stepped to deep: "
            r10.print(r4)     // Catch:{ all -> 0x0084 }
            int r4 = r7.mState     // Catch:{ all -> 0x0084 }
            java.lang.String r4 = stateToString(r4)     // Catch:{ all -> 0x0084 }
            r10.println(r4)     // Catch:{ all -> 0x0084 }
        L_0x007d:
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x008a }
            monitor-exit(r19)     // Catch:{ all -> 0x008a }
            goto L_0x0924
        L_0x0084:
            r0 = move-exception
            r4 = r0
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x008a }
            throw r4     // Catch:{ all -> 0x008a }
        L_0x008a:
            r0 = move-exception
            r1 = r0
            monitor-exit(r19)     // Catch:{ all -> 0x008a }
            throw r1
        L_0x008e:
            java.lang.String r1 = "force-idle"
            boolean r1 = r1.equals(r9)
            r4 = 4
            r5 = 5
            r12 = -1
            r6 = 1
            if (r1 == 0) goto L_0x015d
            android.content.Context r1 = r19.getContext()
            java.lang.String r3 = "android.permission.DEVICE_POWER"
            r1.enforceCallingOrSelfPermission(r3, r2)
            monitor-enter(r19)
            long r1 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x0159 }
            java.lang.String r3 = r20.getNextArg()     // Catch:{ all -> 0x0159 }
            if (r3 == 0) goto L_0x0109
            java.lang.String r13 = "deep"
            boolean r13 = r13.equals(r3)     // Catch:{ all -> 0x0153 }
            if (r13 == 0) goto L_0x00b7
            goto L_0x0109
        L_0x00b7:
            java.lang.String r5 = "light"
            boolean r5 = r5.equals(r3)     // Catch:{ all -> 0x0153 }
            if (r5 == 0) goto L_0x00f4
            r7.mForceIdle = r6     // Catch:{ all -> 0x0153 }
            r19.becomeInactiveIfAppropriateLocked()     // Catch:{ all -> 0x0153 }
            int r5 = r7.mLightState     // Catch:{ all -> 0x0153 }
        L_0x00c7:
            if (r5 == r4) goto L_0x00ee
            java.lang.String r6 = "s:shell"
            r7.stepLightIdleStateLocked(r6)     // Catch:{ all -> 0x0153 }
            int r6 = r7.mLightState     // Catch:{ all -> 0x0153 }
            if (r5 != r6) goto L_0x00ea
            java.lang.String r4 = "Unable to go light idle; stopped at "
            r10.print(r4)     // Catch:{ all -> 0x0153 }
            int r4 = r7.mLightState     // Catch:{ all -> 0x0153 }
            java.lang.String r4 = lightStateToString(r4)     // Catch:{ all -> 0x0153 }
            r10.println(r4)     // Catch:{ all -> 0x0153 }
            r19.exitForceIdleLocked()     // Catch:{ all -> 0x0153 }
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x0159 }
            monitor-exit(r19)     // Catch:{ all -> 0x0159 }
            return r12
        L_0x00ea:
            int r6 = r7.mLightState     // Catch:{ all -> 0x0153 }
            r5 = r6
            goto L_0x00c7
        L_0x00ee:
            java.lang.String r4 = "Now forced in to light idle mode"
            r10.println(r4)     // Catch:{ all -> 0x0153 }
            goto L_0x014c
        L_0x00f4:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x0153 }
            r4.<init>()     // Catch:{ all -> 0x0153 }
            java.lang.String r5 = "Unknown idle mode: "
            r4.append(r5)     // Catch:{ all -> 0x0153 }
            r4.append(r3)     // Catch:{ all -> 0x0153 }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x0153 }
            r10.println(r4)     // Catch:{ all -> 0x0153 }
            goto L_0x014c
        L_0x0109:
            boolean r4 = r7.mDeepEnabled     // Catch:{ all -> 0x0153 }
            if (r4 != 0) goto L_0x0118
            java.lang.String r4 = "Unable to go deep idle; not enabled"
            r10.println(r4)     // Catch:{ all -> 0x0153 }
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x0159 }
            monitor-exit(r19)     // Catch:{ all -> 0x0159 }
            return r12
        L_0x0118:
            r7.mForceIdle = r6     // Catch:{ all -> 0x0153 }
            r19.becomeInactiveIfAppropriateLocked()     // Catch:{ all -> 0x0153 }
            int r4 = r7.mState     // Catch:{ all -> 0x0153 }
        L_0x011f:
            if (r4 == r5) goto L_0x0146
            java.lang.String r6 = "s:shell"
            r7.stepIdleStateLocked(r6)     // Catch:{ all -> 0x0153 }
            int r6 = r7.mState     // Catch:{ all -> 0x0153 }
            if (r4 != r6) goto L_0x0142
            java.lang.String r5 = "Unable to go deep idle; stopped at "
            r10.print(r5)     // Catch:{ all -> 0x0153 }
            int r5 = r7.mState     // Catch:{ all -> 0x0153 }
            java.lang.String r5 = stateToString(r5)     // Catch:{ all -> 0x0153 }
            r10.println(r5)     // Catch:{ all -> 0x0153 }
            r19.exitForceIdleLocked()     // Catch:{ all -> 0x0153 }
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x0159 }
            monitor-exit(r19)     // Catch:{ all -> 0x0159 }
            return r12
        L_0x0142:
            int r6 = r7.mState     // Catch:{ all -> 0x0153 }
            r4 = r6
            goto L_0x011f
        L_0x0146:
            java.lang.String r5 = "Now forced in to deep idle mode"
            r10.println(r5)     // Catch:{ all -> 0x0153 }
        L_0x014c:
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x0159 }
            monitor-exit(r19)     // Catch:{ all -> 0x0159 }
            goto L_0x0924
        L_0x0153:
            r0 = move-exception
            r4 = r0
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x0159 }
            throw r4     // Catch:{ all -> 0x0159 }
        L_0x0159:
            r0 = move-exception
            r1 = r0
            monitor-exit(r19)     // Catch:{ all -> 0x0159 }
            throw r1
        L_0x015d:
            java.lang.String r1 = "force-inactive"
            boolean r1 = r1.equals(r9)
            if (r1 == 0) goto L_0x01a5
            android.content.Context r1 = r19.getContext()
            java.lang.String r3 = "android.permission.DEVICE_POWER"
            r1.enforceCallingOrSelfPermission(r3, r2)
            monitor-enter(r19)
            long r1 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x01a1 }
            r7.mForceIdle = r6     // Catch:{ all -> 0x019b }
            r19.becomeInactiveIfAppropriateLocked()     // Catch:{ all -> 0x019b }
            java.lang.String r3 = "Light state: "
            r10.print(r3)     // Catch:{ all -> 0x019b }
            int r3 = r7.mLightState     // Catch:{ all -> 0x019b }
            java.lang.String r3 = lightStateToString(r3)     // Catch:{ all -> 0x019b }
            r10.print(r3)     // Catch:{ all -> 0x019b }
            java.lang.String r3 = ", deep state: "
            r10.print(r3)     // Catch:{ all -> 0x019b }
            int r3 = r7.mState     // Catch:{ all -> 0x019b }
            java.lang.String r3 = stateToString(r3)     // Catch:{ all -> 0x019b }
            r10.println(r3)     // Catch:{ all -> 0x019b }
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x01a1 }
            monitor-exit(r19)     // Catch:{ all -> 0x01a1 }
            goto L_0x0924
        L_0x019b:
            r0 = move-exception
            r3 = r0
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x01a1 }
            throw r3     // Catch:{ all -> 0x01a1 }
        L_0x01a1:
            r0 = move-exception
            r1 = r0
            monitor-exit(r19)     // Catch:{ all -> 0x01a1 }
            throw r1
        L_0x01a5:
            java.lang.String r1 = "unforce"
            boolean r1 = r1.equals(r9)
            if (r1 == 0) goto L_0x01ec
            android.content.Context r1 = r19.getContext()
            java.lang.String r3 = "android.permission.DEVICE_POWER"
            r1.enforceCallingOrSelfPermission(r3, r2)
            monitor-enter(r19)
            long r1 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x01e8 }
            r19.exitForceIdleLocked()     // Catch:{ all -> 0x01e2 }
            java.lang.String r3 = "Light state: "
            r10.print(r3)     // Catch:{ all -> 0x01e2 }
            int r3 = r7.mLightState     // Catch:{ all -> 0x01e2 }
            java.lang.String r3 = lightStateToString(r3)     // Catch:{ all -> 0x01e2 }
            r10.print(r3)     // Catch:{ all -> 0x01e2 }
            java.lang.String r3 = ", deep state: "
            r10.print(r3)     // Catch:{ all -> 0x01e2 }
            int r3 = r7.mState     // Catch:{ all -> 0x01e2 }
            java.lang.String r3 = stateToString(r3)     // Catch:{ all -> 0x01e2 }
            r10.println(r3)     // Catch:{ all -> 0x01e2 }
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x01e8 }
            monitor-exit(r19)     // Catch:{ all -> 0x01e8 }
            goto L_0x0924
        L_0x01e2:
            r0 = move-exception
            r3 = r0
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x01e8 }
            throw r3     // Catch:{ all -> 0x01e8 }
        L_0x01e8:
            r0 = move-exception
            r1 = r0
            monitor-exit(r19)     // Catch:{ all -> 0x01e8 }
            throw r1
        L_0x01ec:
            java.lang.String r1 = "get"
            boolean r1 = r1.equals(r9)
            r13 = 6
            r14 = 2
            if (r1 == 0) goto L_0x02bd
            android.content.Context r1 = r19.getContext()
            java.lang.String r3 = "android.permission.DEVICE_POWER"
            r1.enforceCallingOrSelfPermission(r3, r2)
            monitor-enter(r19)
            java.lang.String r1 = r20.getNextArg()     // Catch:{ all -> 0x02b9 }
            if (r1 == 0) goto L_0x02b1
            long r2 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x02b9 }
            int r15 = r1.hashCode()     // Catch:{ all -> 0x02ab }
            switch(r15) {
                case -907689876: goto L_0x0251;
                case 3079404: goto L_0x0247;
                case 97618667: goto L_0x023d;
                case 102970646: goto L_0x0232;
                case 107947501: goto L_0x0227;
                case 1436115569: goto L_0x021d;
                case 1843485230: goto L_0x0212;
                default: goto L_0x0211;
            }     // Catch:{ all -> 0x02ab }
        L_0x0211:
            goto L_0x025b
        L_0x0212:
            java.lang.String r4 = "network"
            boolean r4 = r1.equals(r4)     // Catch:{ all -> 0x02ab }
            if (r4 == 0) goto L_0x0211
            r4 = r13
            goto L_0x025c
        L_0x021d:
            java.lang.String r4 = "charging"
            boolean r4 = r1.equals(r4)     // Catch:{ all -> 0x02ab }
            if (r4 == 0) goto L_0x0211
            r4 = r5
            goto L_0x025c
        L_0x0227:
            java.lang.String r4 = "quick"
            boolean r4 = r1.equals(r4)     // Catch:{ all -> 0x02ab }
            if (r4 == 0) goto L_0x0211
            r4 = 3
            goto L_0x025c
        L_0x0232:
            java.lang.String r4 = "light"
            boolean r4 = r1.equals(r4)     // Catch:{ all -> 0x02ab }
            if (r4 == 0) goto L_0x0211
            r4 = r11
            goto L_0x025c
        L_0x023d:
            java.lang.String r4 = "force"
            boolean r4 = r1.equals(r4)     // Catch:{ all -> 0x02ab }
            if (r4 == 0) goto L_0x0211
            r4 = r14
            goto L_0x025c
        L_0x0247:
            java.lang.String r4 = "deep"
            boolean r4 = r1.equals(r4)     // Catch:{ all -> 0x02ab }
            if (r4 == 0) goto L_0x0211
            r4 = r6
            goto L_0x025c
        L_0x0251:
            java.lang.String r5 = "screen"
            boolean r5 = r1.equals(r5)     // Catch:{ all -> 0x02ab }
            if (r5 == 0) goto L_0x0211
            goto L_0x025c
        L_0x025b:
            r4 = r12
        L_0x025c:
            switch(r4) {
                case 0: goto L_0x028a;
                case 1: goto L_0x0280;
                case 2: goto L_0x027a;
                case 3: goto L_0x0274;
                case 4: goto L_0x026e;
                case 5: goto L_0x0268;
                case 6: goto L_0x0262;
                default: goto L_0x025f;
            }     // Catch:{ all -> 0x02ab }
        L_0x025f:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x02ab }
            goto L_0x0294
        L_0x0262:
            boolean r4 = r7.mNetworkConnected     // Catch:{ all -> 0x02ab }
            r10.println(r4)     // Catch:{ all -> 0x02ab }
            goto L_0x02a6
        L_0x0268:
            boolean r4 = r7.mCharging     // Catch:{ all -> 0x02ab }
            r10.println(r4)     // Catch:{ all -> 0x02ab }
            goto L_0x02a6
        L_0x026e:
            boolean r4 = r7.mScreenOn     // Catch:{ all -> 0x02ab }
            r10.println(r4)     // Catch:{ all -> 0x02ab }
            goto L_0x02a6
        L_0x0274:
            boolean r4 = r7.mQuickDozeActivated     // Catch:{ all -> 0x02ab }
            r10.println(r4)     // Catch:{ all -> 0x02ab }
            goto L_0x02a6
        L_0x027a:
            boolean r4 = r7.mForceIdle     // Catch:{ all -> 0x02ab }
            r10.println(r4)     // Catch:{ all -> 0x02ab }
            goto L_0x02a6
        L_0x0280:
            int r4 = r7.mState     // Catch:{ all -> 0x02ab }
            java.lang.String r4 = stateToString(r4)     // Catch:{ all -> 0x02ab }
            r10.println(r4)     // Catch:{ all -> 0x02ab }
            goto L_0x02a6
        L_0x028a:
            int r4 = r7.mLightState     // Catch:{ all -> 0x02ab }
            java.lang.String r4 = lightStateToString(r4)     // Catch:{ all -> 0x02ab }
            r10.println(r4)     // Catch:{ all -> 0x02ab }
            goto L_0x02a6
        L_0x0294:
            r4.<init>()     // Catch:{ all -> 0x02ab }
            java.lang.String r5 = "Unknown get option: "
            r4.append(r5)     // Catch:{ all -> 0x02ab }
            r4.append(r1)     // Catch:{ all -> 0x02ab }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x02ab }
            r10.println(r4)     // Catch:{ all -> 0x02ab }
        L_0x02a6:
            android.os.Binder.restoreCallingIdentity(r2)     // Catch:{ all -> 0x02b9 }
            goto L_0x02b6
        L_0x02ab:
            r0 = move-exception
            r4 = r0
            android.os.Binder.restoreCallingIdentity(r2)     // Catch:{ all -> 0x02b9 }
            throw r4     // Catch:{ all -> 0x02b9 }
        L_0x02b1:
            java.lang.String r2 = "Argument required"
            r10.println(r2)     // Catch:{ all -> 0x02b9 }
        L_0x02b6:
            monitor-exit(r19)     // Catch:{ all -> 0x02b9 }
            goto L_0x0924
        L_0x02b9:
            r0 = move-exception
            r1 = r0
            monitor-exit(r19)     // Catch:{ all -> 0x02b9 }
            throw r1
        L_0x02bd:
            java.lang.String r1 = "disable"
            boolean r1 = r1.equals(r9)
            if (r1 == 0) goto L_0x0361
            android.content.Context r1 = r19.getContext()
            java.lang.String r3 = "android.permission.DEVICE_POWER"
            r1.enforceCallingOrSelfPermission(r3, r2)
            monitor-enter(r19)
            long r1 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x035d }
            java.lang.String r3 = r20.getNextArg()     // Catch:{ all -> 0x035d }
            r4 = 0
            r5 = 0
            if (r3 == 0) goto L_0x02eb
            java.lang.String r6 = "deep"
            boolean r6 = r6.equals(r3)     // Catch:{ all -> 0x0357 }
            if (r6 != 0) goto L_0x02eb
            java.lang.String r6 = "all"
            boolean r6 = r6.equals(r3)     // Catch:{ all -> 0x0357 }
            if (r6 == 0) goto L_0x02f8
        L_0x02eb:
            r5 = 1
            boolean r6 = r7.mDeepEnabled     // Catch:{ all -> 0x0357 }
            if (r6 == 0) goto L_0x02f8
            r7.mDeepEnabled = r11     // Catch:{ all -> 0x0357 }
            r4 = 1
            java.lang.String r6 = "Deep idle mode disabled"
            r10.println(r6)     // Catch:{ all -> 0x0357 }
        L_0x02f8:
            if (r3 == 0) goto L_0x030b
            java.lang.String r6 = "light"
            boolean r6 = r6.equals(r3)     // Catch:{ all -> 0x0357 }
            if (r6 != 0) goto L_0x030b
            java.lang.String r6 = "all"
            boolean r6 = r6.equals(r3)     // Catch:{ all -> 0x0357 }
            if (r6 == 0) goto L_0x0318
        L_0x030b:
            r5 = 1
            boolean r6 = r7.mLightEnabled     // Catch:{ all -> 0x0357 }
            if (r6 == 0) goto L_0x0318
            r7.mLightEnabled = r11     // Catch:{ all -> 0x0357 }
            r4 = 1
            java.lang.String r6 = "Light idle mode disabled"
            r10.println(r6)     // Catch:{ all -> 0x0357 }
        L_0x0318:
            if (r4 == 0) goto L_0x033a
            r7.mActiveReason = r13     // Catch:{ all -> 0x0357 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0357 }
            r6.<init>()     // Catch:{ all -> 0x0357 }
            if (r3 != 0) goto L_0x0326
            java.lang.String r12 = "all"
            goto L_0x0327
        L_0x0326:
            r12 = r3
        L_0x0327:
            r6.append(r12)     // Catch:{ all -> 0x0357 }
            java.lang.String r12 = "-disabled"
            r6.append(r12)     // Catch:{ all -> 0x0357 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0357 }
            int r12 = android.os.Process.myUid()     // Catch:{ all -> 0x0357 }
            r7.becomeActiveLocked(r6, r12)     // Catch:{ all -> 0x0357 }
        L_0x033a:
            if (r5 != 0) goto L_0x0350
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x0357 }
            r6.<init>()     // Catch:{ all -> 0x0357 }
            java.lang.String r12 = "Unknown idle mode: "
            r6.append(r12)     // Catch:{ all -> 0x0357 }
            r6.append(r3)     // Catch:{ all -> 0x0357 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x0357 }
            r10.println(r6)     // Catch:{ all -> 0x0357 }
        L_0x0350:
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x035d }
            monitor-exit(r19)     // Catch:{ all -> 0x035d }
            goto L_0x0924
        L_0x0357:
            r0 = move-exception
            r4 = r0
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x035d }
            throw r4     // Catch:{ all -> 0x035d }
        L_0x035d:
            r0 = move-exception
            r1 = r0
            monitor-exit(r19)     // Catch:{ all -> 0x035d }
            throw r1
        L_0x0361:
            java.lang.String r1 = "enable"
            boolean r1 = r1.equals(r9)
            if (r1 == 0) goto L_0x03e8
            android.content.Context r1 = r19.getContext()
            java.lang.String r3 = "android.permission.DEVICE_POWER"
            r1.enforceCallingOrSelfPermission(r3, r2)
            monitor-enter(r19)
            long r1 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x03e4 }
            java.lang.String r3 = r20.getNextArg()     // Catch:{ all -> 0x03e4 }
            r4 = 0
            r5 = 0
            if (r3 == 0) goto L_0x038f
            java.lang.String r12 = "deep"
            boolean r12 = r12.equals(r3)     // Catch:{ all -> 0x03de }
            if (r12 != 0) goto L_0x038f
            java.lang.String r12 = "all"
            boolean r12 = r12.equals(r3)     // Catch:{ all -> 0x03de }
            if (r12 == 0) goto L_0x039c
        L_0x038f:
            r5 = 1
            boolean r12 = r7.mDeepEnabled     // Catch:{ all -> 0x03de }
            if (r12 != 0) goto L_0x039c
            r7.mDeepEnabled = r6     // Catch:{ all -> 0x03de }
            r4 = 1
            java.lang.String r12 = "Deep idle mode enabled"
            r10.println(r12)     // Catch:{ all -> 0x03de }
        L_0x039c:
            if (r3 == 0) goto L_0x03af
            java.lang.String r12 = "light"
            boolean r12 = r12.equals(r3)     // Catch:{ all -> 0x03de }
            if (r12 != 0) goto L_0x03af
            java.lang.String r12 = "all"
            boolean r12 = r12.equals(r3)     // Catch:{ all -> 0x03de }
            if (r12 == 0) goto L_0x03bc
        L_0x03af:
            r5 = 1
            boolean r12 = r7.mLightEnabled     // Catch:{ all -> 0x03de }
            if (r12 != 0) goto L_0x03bc
            r7.mLightEnabled = r6     // Catch:{ all -> 0x03de }
            r4 = 1
            java.lang.String r6 = "Light idle mode enable"
            r10.println(r6)     // Catch:{ all -> 0x03de }
        L_0x03bc:
            if (r4 == 0) goto L_0x03c1
            r19.becomeInactiveIfAppropriateLocked()     // Catch:{ all -> 0x03de }
        L_0x03c1:
            if (r5 != 0) goto L_0x03d7
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x03de }
            r6.<init>()     // Catch:{ all -> 0x03de }
            java.lang.String r12 = "Unknown idle mode: "
            r6.append(r12)     // Catch:{ all -> 0x03de }
            r6.append(r3)     // Catch:{ all -> 0x03de }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x03de }
            r10.println(r6)     // Catch:{ all -> 0x03de }
        L_0x03d7:
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x03e4 }
            monitor-exit(r19)     // Catch:{ all -> 0x03e4 }
            goto L_0x0924
        L_0x03de:
            r0 = move-exception
            r4 = r0
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x03e4 }
            throw r4     // Catch:{ all -> 0x03e4 }
        L_0x03e4:
            r0 = move-exception
            r1 = r0
            monitor-exit(r19)     // Catch:{ all -> 0x03e4 }
            throw r1
        L_0x03e8:
            java.lang.String r1 = "enabled"
            boolean r1 = r1.equals(r9)
            if (r1 == 0) goto L_0x044e
            monitor-enter(r19)
            java.lang.String r1 = r20.getNextArg()     // Catch:{ all -> 0x044a }
            if (r1 == 0) goto L_0x043a
            java.lang.String r2 = "all"
            boolean r2 = r2.equals(r1)     // Catch:{ all -> 0x044a }
            if (r2 == 0) goto L_0x0400
            goto L_0x043a
        L_0x0400:
            java.lang.String r2 = "deep"
            boolean r2 = r2.equals(r1)     // Catch:{ all -> 0x044a }
            if (r2 == 0) goto L_0x0412
            boolean r2 = r7.mDeepEnabled     // Catch:{ all -> 0x044a }
            if (r2 == 0) goto L_0x040e
            java.lang.String r3 = "1"
        L_0x040e:
            r10.println(r3)     // Catch:{ all -> 0x044a }
            goto L_0x0447
        L_0x0412:
            java.lang.String r2 = "light"
            boolean r2 = r2.equals(r1)     // Catch:{ all -> 0x044a }
            if (r2 == 0) goto L_0x0425
            boolean r2 = r7.mLightEnabled     // Catch:{ all -> 0x044a }
            if (r2 == 0) goto L_0x0421
            java.lang.String r3 = "1"
        L_0x0421:
            r10.println(r3)     // Catch:{ all -> 0x044a }
            goto L_0x0447
        L_0x0425:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x044a }
            r2.<init>()     // Catch:{ all -> 0x044a }
            java.lang.String r3 = "Unknown idle mode: "
            r2.append(r3)     // Catch:{ all -> 0x044a }
            r2.append(r1)     // Catch:{ all -> 0x044a }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x044a }
            r10.println(r2)     // Catch:{ all -> 0x044a }
            goto L_0x0447
        L_0x043a:
            boolean r2 = r7.mDeepEnabled     // Catch:{ all -> 0x044a }
            if (r2 == 0) goto L_0x0444
            boolean r2 = r7.mLightEnabled     // Catch:{ all -> 0x044a }
            if (r2 == 0) goto L_0x0444
            java.lang.String r3 = "1"
        L_0x0444:
            r10.println(r3)     // Catch:{ all -> 0x044a }
        L_0x0447:
            monitor-exit(r19)     // Catch:{ all -> 0x044a }
            goto L_0x0924
        L_0x044a:
            r0 = move-exception
            r1 = r0
            monitor-exit(r19)     // Catch:{ all -> 0x044a }
            throw r1
        L_0x044e:
            java.lang.String r1 = "whitelist"
            boolean r1 = r1.equals(r9)
            r3 = 61
            r4 = 45
            r5 = 43
            if (r1 == 0) goto L_0x05a3
            java.lang.String r1 = r20.getNextArg()
            if (r1 == 0) goto L_0x051a
            android.content.Context r13 = r19.getContext()
            java.lang.String r14 = "android.permission.DEVICE_POWER"
            r13.enforceCallingOrSelfPermission(r14, r2)
            long r13 = android.os.Binder.clearCallingIdentity()
        L_0x0470:
            int r2 = r1.length()     // Catch:{ all -> 0x0514 }
            if (r2 < r6) goto L_0x04fb
            char r2 = r1.charAt(r11)     // Catch:{ all -> 0x0514 }
            if (r2 == r4) goto L_0x048a
            char r2 = r1.charAt(r11)     // Catch:{ all -> 0x0514 }
            if (r2 == r5) goto L_0x048a
            char r2 = r1.charAt(r11)     // Catch:{ all -> 0x0514 }
            if (r2 == r3) goto L_0x048a
            goto L_0x04fb
        L_0x048a:
            char r2 = r1.charAt(r11)     // Catch:{ all -> 0x0514 }
            java.lang.String r15 = r1.substring(r6)     // Catch:{ all -> 0x0514 }
            if (r2 != r5) goto L_0x04c4
            boolean r16 = r7.addPowerSaveWhitelistAppInternal(r15)     // Catch:{ all -> 0x0514 }
            if (r16 == 0) goto L_0x04af
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0514 }
            r3.<init>()     // Catch:{ all -> 0x0514 }
            java.lang.String r5 = "Added: "
            r3.append(r5)     // Catch:{ all -> 0x0514 }
            r3.append(r15)     // Catch:{ all -> 0x0514 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0514 }
            r10.println(r3)     // Catch:{ all -> 0x0514 }
            goto L_0x04e8
        L_0x04af:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0514 }
            r3.<init>()     // Catch:{ all -> 0x0514 }
            java.lang.String r5 = "Unknown package: "
            r3.append(r5)     // Catch:{ all -> 0x0514 }
            r3.append(r15)     // Catch:{ all -> 0x0514 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0514 }
            r10.println(r3)     // Catch:{ all -> 0x0514 }
            goto L_0x04e8
        L_0x04c4:
            if (r2 != r4) goto L_0x04e1
            boolean r3 = r7.removePowerSaveWhitelistAppInternal(r15)     // Catch:{ all -> 0x0514 }
            if (r3 == 0) goto L_0x04e8
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0514 }
            r3.<init>()     // Catch:{ all -> 0x0514 }
            java.lang.String r5 = "Removed: "
            r3.append(r5)     // Catch:{ all -> 0x0514 }
            r3.append(r15)     // Catch:{ all -> 0x0514 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0514 }
            r10.println(r3)     // Catch:{ all -> 0x0514 }
            goto L_0x04e8
        L_0x04e1:
            boolean r3 = r7.getPowerSaveWhitelistAppInternal(r15)     // Catch:{ all -> 0x0514 }
            r10.println(r3)     // Catch:{ all -> 0x0514 }
        L_0x04e8:
            java.lang.String r2 = r20.getNextArg()     // Catch:{ all -> 0x0514 }
            r1 = r2
            if (r2 != 0) goto L_0x04f5
            android.os.Binder.restoreCallingIdentity(r13)
            goto L_0x059d
        L_0x04f5:
            r3 = 61
            r5 = 43
            goto L_0x0470
        L_0x04fb:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0514 }
            r2.<init>()     // Catch:{ all -> 0x0514 }
            java.lang.String r3 = "Package must be prefixed with +, -, or =: "
            r2.append(r3)     // Catch:{ all -> 0x0514 }
            r2.append(r1)     // Catch:{ all -> 0x0514 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0514 }
            r10.println(r2)     // Catch:{ all -> 0x0514 }
            android.os.Binder.restoreCallingIdentity(r13)
            return r12
        L_0x0514:
            r0 = move-exception
            r2 = r0
            android.os.Binder.restoreCallingIdentity(r13)
            throw r2
        L_0x051a:
            monitor-enter(r19)
            r2 = r11
        L_0x051c:
            android.util.ArrayMap<java.lang.String, java.lang.Integer> r3 = r7.mPowerSaveWhitelistAppsExceptIdle     // Catch:{ all -> 0x059f }
            int r3 = r3.size()     // Catch:{ all -> 0x059f }
            if (r2 >= r3) goto L_0x0546
            java.lang.String r3 = "system-excidle,"
            r10.print(r3)     // Catch:{ all -> 0x059f }
            android.util.ArrayMap<java.lang.String, java.lang.Integer> r3 = r7.mPowerSaveWhitelistAppsExceptIdle     // Catch:{ all -> 0x059f }
            java.lang.Object r3 = r3.keyAt(r2)     // Catch:{ all -> 0x059f }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x059f }
            r10.print(r3)     // Catch:{ all -> 0x059f }
            java.lang.String r3 = ","
            r10.print(r3)     // Catch:{ all -> 0x059f }
            android.util.ArrayMap<java.lang.String, java.lang.Integer> r3 = r7.mPowerSaveWhitelistAppsExceptIdle     // Catch:{ all -> 0x059f }
            java.lang.Object r3 = r3.valueAt(r2)     // Catch:{ all -> 0x059f }
            r10.println(r3)     // Catch:{ all -> 0x059f }
            int r2 = r2 + 1
            goto L_0x051c
        L_0x0546:
            r2 = r11
        L_0x0547:
            android.util.ArrayMap<java.lang.String, java.lang.Integer> r3 = r7.mPowerSaveWhitelistApps     // Catch:{ all -> 0x059f }
            int r3 = r3.size()     // Catch:{ all -> 0x059f }
            if (r2 >= r3) goto L_0x0571
            java.lang.String r3 = "system,"
            r10.print(r3)     // Catch:{ all -> 0x059f }
            android.util.ArrayMap<java.lang.String, java.lang.Integer> r3 = r7.mPowerSaveWhitelistApps     // Catch:{ all -> 0x059f }
            java.lang.Object r3 = r3.keyAt(r2)     // Catch:{ all -> 0x059f }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x059f }
            r10.print(r3)     // Catch:{ all -> 0x059f }
            java.lang.String r3 = ","
            r10.print(r3)     // Catch:{ all -> 0x059f }
            android.util.ArrayMap<java.lang.String, java.lang.Integer> r3 = r7.mPowerSaveWhitelistApps     // Catch:{ all -> 0x059f }
            java.lang.Object r3 = r3.valueAt(r2)     // Catch:{ all -> 0x059f }
            r10.println(r3)     // Catch:{ all -> 0x059f }
            int r2 = r2 + 1
            goto L_0x0547
        L_0x0571:
            r2 = r11
        L_0x0572:
            android.util.ArrayMap<java.lang.String, java.lang.Integer> r3 = r7.mPowerSaveWhitelistUserApps     // Catch:{ all -> 0x059f }
            int r3 = r3.size()     // Catch:{ all -> 0x059f }
            if (r2 >= r3) goto L_0x059c
            java.lang.String r3 = "user,"
            r10.print(r3)     // Catch:{ all -> 0x059f }
            android.util.ArrayMap<java.lang.String, java.lang.Integer> r3 = r7.mPowerSaveWhitelistUserApps     // Catch:{ all -> 0x059f }
            java.lang.Object r3 = r3.keyAt(r2)     // Catch:{ all -> 0x059f }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x059f }
            r10.print(r3)     // Catch:{ all -> 0x059f }
            java.lang.String r3 = ","
            r10.print(r3)     // Catch:{ all -> 0x059f }
            android.util.ArrayMap<java.lang.String, java.lang.Integer> r3 = r7.mPowerSaveWhitelistUserApps     // Catch:{ all -> 0x059f }
            java.lang.Object r3 = r3.valueAt(r2)     // Catch:{ all -> 0x059f }
            r10.println(r3)     // Catch:{ all -> 0x059f }
            int r2 = r2 + 1
            goto L_0x0572
        L_0x059c:
            monitor-exit(r19)     // Catch:{ all -> 0x059f }
        L_0x059d:
            goto L_0x0924
        L_0x059f:
            r0 = move-exception
            r2 = r0
            monitor-exit(r19)     // Catch:{ all -> 0x059f }
            throw r2
        L_0x05a3:
            java.lang.String r1 = "tempwhitelist"
            boolean r1 = r1.equals(r9)
            if (r1 == 0) goto L_0x0655
            r1 = 10000(0x2710, double:4.9407E-320)
            r3 = 0
            r14 = r1
            r13 = r3
        L_0x05b1:
            java.lang.String r1 = r20.getNextOption()
            r6 = r1
            if (r1 == 0) goto L_0x05f6
            java.lang.String r1 = "-u"
            boolean r1 = r1.equals(r6)
            if (r1 == 0) goto L_0x05d3
            java.lang.String r1 = r20.getNextArg()
            if (r1 != 0) goto L_0x05cc
            java.lang.String r2 = "-u requires a user number"
            r10.println(r2)
            return r12
        L_0x05cc:
            int r2 = java.lang.Integer.parseInt(r1)
            r8.userId = r2
            goto L_0x05b1
        L_0x05d3:
            java.lang.String r1 = "-d"
            boolean r1 = r1.equals(r6)
            if (r1 == 0) goto L_0x05ec
            java.lang.String r1 = r20.getNextArg()
            if (r1 != 0) goto L_0x05e7
            java.lang.String r2 = "-d requires a duration"
            r10.println(r2)
            return r12
        L_0x05e7:
            long r14 = java.lang.Long.parseLong(r1)
            goto L_0x05b1
        L_0x05ec:
            java.lang.String r1 = "-r"
            boolean r1 = r1.equals(r6)
            if (r1 == 0) goto L_0x05b1
            r13 = 1
            goto L_0x05b1
        L_0x05f6:
            java.lang.String r5 = r20.getNextArg()
            if (r5 == 0) goto L_0x0644
            if (r13 == 0) goto L_0x060f
            int r1 = r8.userId     // Catch:{ Exception -> 0x0608 }
            r7.removePowerSaveTempWhitelistAppChecked(r5, r1)     // Catch:{ Exception -> 0x0608 }
            r18 = r5
            r17 = r6
            goto L_0x0625
        L_0x0608:
            r0 = move-exception
            r1 = r0
            r18 = r5
            r17 = r6
            goto L_0x062f
        L_0x060f:
            int r3 = r8.userId     // Catch:{ Exception -> 0x0629 }
            java.lang.String r16 = "shell"
            r1 = r19
            r2 = r5
            r17 = r3
            r3 = r14
            r18 = r5
            r5 = r17
            r17 = r6
            r6 = r16
            r1.addPowerSaveTempWhitelistAppChecked(r2, r3, r5, r6)     // Catch:{ Exception -> 0x0626 }
        L_0x0625:
            goto L_0x0653
        L_0x0626:
            r0 = move-exception
            r1 = r0
            goto L_0x062f
        L_0x0629:
            r0 = move-exception
            r18 = r5
            r17 = r6
            r1 = r0
        L_0x062f:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Failed: "
            r2.append(r3)
            r2.append(r1)
            java.lang.String r2 = r2.toString()
            r10.println(r2)
            return r12
        L_0x0644:
            r18 = r5
            r17 = r6
            if (r13 == 0) goto L_0x0650
            java.lang.String r1 = "[-r] requires a package name"
            r10.println(r1)
            return r12
        L_0x0650:
            r7.dumpTempWhitelistSchedule(r10, r11)
        L_0x0653:
            goto L_0x0924
        L_0x0655:
            java.lang.String r1 = "except-idle-whitelist"
            boolean r1 = r1.equals(r9)
            if (r1 == 0) goto L_0x0735
            android.content.Context r1 = r19.getContext()
            java.lang.String r3 = "android.permission.DEVICE_POWER"
            r1.enforceCallingOrSelfPermission(r3, r2)
            long r1 = android.os.Binder.clearCallingIdentity()
            java.lang.String r3 = r20.getNextArg()     // Catch:{ all -> 0x072f }
            if (r3 != 0) goto L_0x067a
            java.lang.String r4 = "No arguments given"
            r10.println(r4)     // Catch:{ all -> 0x072f }
            android.os.Binder.restoreCallingIdentity(r1)
            return r12
        L_0x067a:
            java.lang.String r5 = "reset"
            boolean r5 = r5.equals(r3)     // Catch:{ all -> 0x072f }
            if (r5 == 0) goto L_0x0688
            r19.resetPowerSaveWhitelistExceptIdleInternal()     // Catch:{ all -> 0x072f }
            goto L_0x06f7
        L_0x0688:
            int r5 = r3.length()     // Catch:{ all -> 0x072f }
            if (r5 < r6) goto L_0x0716
            char r5 = r3.charAt(r11)     // Catch:{ all -> 0x072f }
            if (r5 == r4) goto L_0x06a5
            char r5 = r3.charAt(r11)     // Catch:{ all -> 0x072f }
            r13 = 43
            if (r5 == r13) goto L_0x06a5
            char r5 = r3.charAt(r11)     // Catch:{ all -> 0x072f }
            r13 = 61
            if (r5 == r13) goto L_0x06a5
            goto L_0x0716
        L_0x06a5:
            char r5 = r3.charAt(r11)     // Catch:{ all -> 0x072f }
            java.lang.String r13 = r3.substring(r6)     // Catch:{ all -> 0x072f }
            r14 = 43
            if (r5 != r14) goto L_0x06e5
            boolean r14 = r7.addPowerSaveWhitelistExceptIdleInternal(r13)     // Catch:{ all -> 0x072f }
            if (r14 == 0) goto L_0x06ce
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ all -> 0x072f }
            r14.<init>()     // Catch:{ all -> 0x072f }
            java.lang.String r15 = "Added: "
            r14.append(r15)     // Catch:{ all -> 0x072f }
            r14.append(r13)     // Catch:{ all -> 0x072f }
            java.lang.String r14 = r14.toString()     // Catch:{ all -> 0x072f }
            r10.println(r14)     // Catch:{ all -> 0x072f }
            r14 = 61
            goto L_0x06f0
        L_0x06ce:
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ all -> 0x072f }
            r14.<init>()     // Catch:{ all -> 0x072f }
            java.lang.String r15 = "Unknown package: "
            r14.append(r15)     // Catch:{ all -> 0x072f }
            r14.append(r13)     // Catch:{ all -> 0x072f }
            java.lang.String r14 = r14.toString()     // Catch:{ all -> 0x072f }
            r10.println(r14)     // Catch:{ all -> 0x072f }
            r14 = 61
            goto L_0x06f0
        L_0x06e5:
            r14 = 61
            if (r5 != r14) goto L_0x06fd
            boolean r15 = r7.getPowerSaveWhitelistExceptIdleInternal(r13)     // Catch:{ all -> 0x072f }
            r10.println(r15)     // Catch:{ all -> 0x072f }
        L_0x06f0:
            java.lang.String r5 = r20.getNextArg()     // Catch:{ all -> 0x072f }
            r3 = r5
            if (r5 != 0) goto L_0x0688
        L_0x06f7:
            android.os.Binder.restoreCallingIdentity(r1)
            goto L_0x0924
        L_0x06fd:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x072f }
            r4.<init>()     // Catch:{ all -> 0x072f }
            java.lang.String r6 = "Unknown argument: "
            r4.append(r6)     // Catch:{ all -> 0x072f }
            r4.append(r3)     // Catch:{ all -> 0x072f }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x072f }
            r10.println(r4)     // Catch:{ all -> 0x072f }
            android.os.Binder.restoreCallingIdentity(r1)
            return r12
        L_0x0716:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x072f }
            r4.<init>()     // Catch:{ all -> 0x072f }
            java.lang.String r5 = "Package must be prefixed with +, -, or =: "
            r4.append(r5)     // Catch:{ all -> 0x072f }
            r4.append(r3)     // Catch:{ all -> 0x072f }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x072f }
            r10.println(r4)     // Catch:{ all -> 0x072f }
            android.os.Binder.restoreCallingIdentity(r1)
            return r12
        L_0x072f:
            r0 = move-exception
            r3 = r0
            android.os.Binder.restoreCallingIdentity(r1)
            throw r3
        L_0x0735:
            java.lang.String r1 = "sys-whitelist"
            boolean r1 = r1.equals(r9)
            if (r1 == 0) goto L_0x0813
            java.lang.String r1 = r20.getNextArg()
            if (r1 == 0) goto L_0x07e6
            android.content.Context r3 = r19.getContext()
            java.lang.String r5 = "android.permission.DEVICE_POWER"
            r3.enforceCallingOrSelfPermission(r5, r2)
            long r2 = android.os.Binder.clearCallingIdentity()
            java.lang.String r5 = "reset"
            boolean r5 = r5.equals(r1)     // Catch:{ all -> 0x07df }
            if (r5 == 0) goto L_0x075e
            r19.resetSystemPowerWhitelistInternal()     // Catch:{ all -> 0x07df }
            goto L_0x07be
        L_0x075e:
            int r5 = r1.length()     // Catch:{ all -> 0x07df }
            if (r5 < r6) goto L_0x07c6
            char r5 = r1.charAt(r11)     // Catch:{ all -> 0x07df }
            if (r5 == r4) goto L_0x0773
            char r5 = r1.charAt(r11)     // Catch:{ all -> 0x07df }
            r13 = 43
            if (r5 == r13) goto L_0x0773
            goto L_0x07c6
        L_0x0773:
            char r5 = r1.charAt(r11)     // Catch:{ all -> 0x07df }
            java.lang.String r13 = r1.substring(r6)     // Catch:{ all -> 0x07df }
            r14 = 43
            if (r5 == r14) goto L_0x079d
            if (r5 == r4) goto L_0x0782
            goto L_0x07b7
        L_0x0782:
            boolean r15 = r7.removeSystemPowerWhitelistAppInternal(r13)     // Catch:{ all -> 0x07df }
            if (r15 == 0) goto L_0x07b7
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ all -> 0x07df }
            r15.<init>()     // Catch:{ all -> 0x07df }
            java.lang.String r4 = "Removed "
            r15.append(r4)     // Catch:{ all -> 0x07df }
            r15.append(r13)     // Catch:{ all -> 0x07df }
            java.lang.String r4 = r15.toString()     // Catch:{ all -> 0x07df }
            r10.println(r4)     // Catch:{ all -> 0x07df }
            goto L_0x07b7
        L_0x079d:
            boolean r4 = r7.restoreSystemPowerWhitelistAppInternal(r13)     // Catch:{ all -> 0x07df }
            if (r4 == 0) goto L_0x07b7
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x07df }
            r4.<init>()     // Catch:{ all -> 0x07df }
            java.lang.String r15 = "Restored "
            r4.append(r15)     // Catch:{ all -> 0x07df }
            r4.append(r13)     // Catch:{ all -> 0x07df }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x07df }
            r10.println(r4)     // Catch:{ all -> 0x07df }
        L_0x07b7:
            java.lang.String r4 = r20.getNextArg()     // Catch:{ all -> 0x07df }
            r1 = r4
            if (r4 != 0) goto L_0x07c3
        L_0x07be:
            android.os.Binder.restoreCallingIdentity(r2)
            goto L_0x080d
        L_0x07c3:
            r4 = 45
            goto L_0x075e
        L_0x07c6:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x07df }
            r4.<init>()     // Catch:{ all -> 0x07df }
            java.lang.String r5 = "Package must be prefixed with + or - "
            r4.append(r5)     // Catch:{ all -> 0x07df }
            r4.append(r1)     // Catch:{ all -> 0x07df }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x07df }
            r10.println(r4)     // Catch:{ all -> 0x07df }
            android.os.Binder.restoreCallingIdentity(r2)
            return r12
        L_0x07df:
            r0 = move-exception
            r4 = r1
            r1 = r0
            android.os.Binder.restoreCallingIdentity(r2)
            throw r1
        L_0x07e6:
            monitor-enter(r19)
            r2 = r11
        L_0x07e8:
            android.util.ArrayMap<java.lang.String, java.lang.Integer> r3 = r7.mPowerSaveWhitelistApps     // Catch:{ all -> 0x080f }
            int r3 = r3.size()     // Catch:{ all -> 0x080f }
            if (r2 >= r3) goto L_0x080c
            android.util.ArrayMap<java.lang.String, java.lang.Integer> r3 = r7.mPowerSaveWhitelistApps     // Catch:{ all -> 0x080f }
            java.lang.Object r3 = r3.keyAt(r2)     // Catch:{ all -> 0x080f }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x080f }
            r10.print(r3)     // Catch:{ all -> 0x080f }
            java.lang.String r3 = ","
            r10.print(r3)     // Catch:{ all -> 0x080f }
            android.util.ArrayMap<java.lang.String, java.lang.Integer> r3 = r7.mPowerSaveWhitelistApps     // Catch:{ all -> 0x080f }
            java.lang.Object r3 = r3.valueAt(r2)     // Catch:{ all -> 0x080f }
            r10.println(r3)     // Catch:{ all -> 0x080f }
            int r2 = r2 + 1
            goto L_0x07e8
        L_0x080c:
            monitor-exit(r19)     // Catch:{ all -> 0x080f }
        L_0x080d:
            goto L_0x0924
        L_0x080f:
            r0 = move-exception
            r2 = r0
            monitor-exit(r19)     // Catch:{ all -> 0x080f }
            throw r2
        L_0x0813:
            java.lang.String r1 = "motion"
            boolean r1 = r1.equals(r9)
            if (r1 == 0) goto L_0x085a
            android.content.Context r1 = r19.getContext()
            java.lang.String r3 = "android.permission.DEVICE_POWER"
            r1.enforceCallingOrSelfPermission(r3, r2)
            monitor-enter(r19)
            long r1 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x0856 }
            r19.motionLocked()     // Catch:{ all -> 0x0850 }
            java.lang.String r3 = "Light state: "
            r10.print(r3)     // Catch:{ all -> 0x0850 }
            int r3 = r7.mLightState     // Catch:{ all -> 0x0850 }
            java.lang.String r3 = lightStateToString(r3)     // Catch:{ all -> 0x0850 }
            r10.print(r3)     // Catch:{ all -> 0x0850 }
            java.lang.String r3 = ", deep state: "
            r10.print(r3)     // Catch:{ all -> 0x0850 }
            int r3 = r7.mState     // Catch:{ all -> 0x0850 }
            java.lang.String r3 = stateToString(r3)     // Catch:{ all -> 0x0850 }
            r10.println(r3)     // Catch:{ all -> 0x0850 }
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x0856 }
            monitor-exit(r19)     // Catch:{ all -> 0x0856 }
            goto L_0x0924
        L_0x0850:
            r0 = move-exception
            r3 = r0
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x0856 }
            throw r3     // Catch:{ all -> 0x0856 }
        L_0x0856:
            r0 = move-exception
            r1 = r0
            monitor-exit(r19)     // Catch:{ all -> 0x0856 }
            throw r1
        L_0x085a:
            java.lang.String r1 = "pre-idle-factor"
            boolean r1 = r1.equals(r9)
            if (r1 == 0) goto L_0x0905
            android.content.Context r1 = r19.getContext()
            java.lang.String r3 = "android.permission.DEVICE_POWER"
            r1.enforceCallingOrSelfPermission(r3, r2)
            monitor-enter(r19)
            long r1 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x0901 }
            r3 = -1
            java.lang.String r4 = r20.getNextArg()     // Catch:{ NumberFormatException -> 0x08d9, all -> 0x08d5 }
            r5 = 0
            r12 = 0
            if (r4 == 0) goto L_0x08ae
            int r13 = java.lang.Integer.parseInt(r4)     // Catch:{ NumberFormatException -> 0x08d9, all -> 0x08d5 }
            r12 = r13
            int r13 = r7.setPreIdleTimeoutMode(r12)     // Catch:{ NumberFormatException -> 0x08d9, all -> 0x08d5 }
            r3 = r13
            if (r3 != r6) goto L_0x089d
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ NumberFormatException -> 0x08d9, all -> 0x08d5 }
            r6.<init>()     // Catch:{ NumberFormatException -> 0x08d9, all -> 0x08d5 }
            java.lang.String r13 = "pre-idle-factor: "
            r6.append(r13)     // Catch:{ NumberFormatException -> 0x08d9, all -> 0x08d5 }
            r6.append(r12)     // Catch:{ NumberFormatException -> 0x08d9, all -> 0x08d5 }
            java.lang.String r6 = r6.toString()     // Catch:{ NumberFormatException -> 0x08d9, all -> 0x08d5 }
            r10.println(r6)     // Catch:{ NumberFormatException -> 0x08d9, all -> 0x08d5 }
            r5 = 1
            goto L_0x08ae
        L_0x089d:
            if (r3 != r14) goto L_0x08a6
            r5 = 1
            java.lang.String r6 = "Deep idle not supported"
            r10.println(r6)     // Catch:{ NumberFormatException -> 0x08d9, all -> 0x08d5 }
            goto L_0x08ae
        L_0x08a6:
            if (r3 != 0) goto L_0x08ae
            r5 = 1
            java.lang.String r6 = "Idle timeout factor not changed"
            r10.println(r6)     // Catch:{ NumberFormatException -> 0x08d9, all -> 0x08d5 }
        L_0x08ae:
            if (r5 != 0) goto L_0x08d1
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ NumberFormatException -> 0x08d9, all -> 0x08d5 }
            r6.<init>()     // Catch:{ NumberFormatException -> 0x08d9, all -> 0x08d5 }
            java.lang.String r13 = "Unknown idle timeout factor: "
            r6.append(r13)     // Catch:{ NumberFormatException -> 0x08d9, all -> 0x08d5 }
            r6.append(r4)     // Catch:{ NumberFormatException -> 0x08d9, all -> 0x08d5 }
            java.lang.String r13 = ",(error code: "
            r6.append(r13)     // Catch:{ NumberFormatException -> 0x08d9, all -> 0x08d5 }
            r6.append(r3)     // Catch:{ NumberFormatException -> 0x08d9, all -> 0x08d5 }
            java.lang.String r13 = ")"
            r6.append(r13)     // Catch:{ NumberFormatException -> 0x08d9, all -> 0x08d5 }
            java.lang.String r6 = r6.toString()     // Catch:{ NumberFormatException -> 0x08d9, all -> 0x08d5 }
            r10.println(r6)     // Catch:{ NumberFormatException -> 0x08d9, all -> 0x08d5 }
        L_0x08d1:
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x0901 }
            goto L_0x08f9
        L_0x08d5:
            r0 = move-exception
            r4 = r3
            r3 = r0
            goto L_0x08fd
        L_0x08d9:
            r0 = move-exception
            r4 = r3
            r3 = r0
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x08fb }
            r5.<init>()     // Catch:{ all -> 0x08fb }
            java.lang.String r6 = "Unknown idle timeout factor,(error code: "
            r5.append(r6)     // Catch:{ all -> 0x08fb }
            r5.append(r4)     // Catch:{ all -> 0x08fb }
            java.lang.String r6 = ")"
            r5.append(r6)     // Catch:{ all -> 0x08fb }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x08fb }
            r10.println(r5)     // Catch:{ all -> 0x08fb }
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x0901 }
        L_0x08f9:
            monitor-exit(r19)     // Catch:{ all -> 0x0901 }
            goto L_0x0924
        L_0x08fb:
            r0 = move-exception
            r3 = r0
        L_0x08fd:
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x0901 }
            throw r3     // Catch:{ all -> 0x0901 }
        L_0x0901:
            r0 = move-exception
            r1 = r0
            monitor-exit(r19)     // Catch:{ all -> 0x0901 }
            throw r1
        L_0x0905:
            java.lang.String r1 = "reset-pre-idle-factor"
            boolean r1 = r1.equals(r9)
            if (r1 == 0) goto L_0x092f
            android.content.Context r1 = r19.getContext()
            java.lang.String r3 = "android.permission.DEVICE_POWER"
            r1.enforceCallingOrSelfPermission(r3, r2)
            monitor-enter(r19)
            long r1 = android.os.Binder.clearCallingIdentity()     // Catch:{ all -> 0x092b }
            r19.resetPreIdleTimeoutMode()     // Catch:{ all -> 0x0925 }
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x092b }
            monitor-exit(r19)     // Catch:{ all -> 0x092b }
        L_0x0924:
            return r11
        L_0x0925:
            r0 = move-exception
            r3 = r0
            android.os.Binder.restoreCallingIdentity(r1)     // Catch:{ all -> 0x092b }
            throw r3     // Catch:{ all -> 0x092b }
        L_0x092b:
            r0 = move-exception
            r1 = r0
            monitor-exit(r19)     // Catch:{ all -> 0x092b }
            throw r1
        L_0x092f:
            int r1 = r20.handleDefaultCommands(r21)
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.DeviceIdleController.onShellCommand(com.android.server.DeviceIdleController$Shell, java.lang.String):int");
    }

    /* JADX WARNING: type inference failed for: r9v11, types: [android.os.Binder, com.android.server.DeviceIdleController$BinderService] */
    /* access modifiers changed from: package-private */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        String label;
        PrintWriter printWriter = pw;
        String[] strArr = args;
        if (DumpUtils.checkDumpPermission(getContext(), TAG, printWriter)) {
            if (strArr != null) {
                int userId = 0;
                int i = 0;
                while (i < strArr.length) {
                    String arg = strArr[i];
                    if ("-h".equals(arg)) {
                        dumpHelp(pw);
                        return;
                    }
                    if ("-u".equals(arg)) {
                        i++;
                        if (i < strArr.length) {
                            userId = Integer.parseInt(strArr[i]);
                        }
                    } else if (!"-a".equals(arg)) {
                        if (arg.length() <= 0 || arg.charAt(0) != '-') {
                            Shell shell = new Shell();
                            shell.userId = userId;
                            String[] newArgs = new String[(strArr.length - i)];
                            System.arraycopy(strArr, i, newArgs, 0, strArr.length - i);
                            String[] strArr2 = newArgs;
                            shell.exec(this.mBinderService, (FileDescriptor) null, fd, (FileDescriptor) null, newArgs, (ShellCallback) null, new ResultReceiver((Handler) null));
                            return;
                        }
                        printWriter.println("Unknown option: " + arg);
                        return;
                    }
                    i++;
                }
            }
            synchronized (this) {
                this.mConstants.dump(printWriter);
                if (this.mEventCmds[0] != 0) {
                    printWriter.println("  Idling history:");
                    long now = SystemClock.elapsedRealtime();
                    for (int i2 = 99; i2 >= 0; i2--) {
                        if (this.mEventCmds[i2] != 0) {
                            int i3 = this.mEventCmds[i2];
                            if (i3 == 1) {
                                label = "     normal";
                            } else if (i3 == 2) {
                                label = " light-idle";
                            } else if (i3 == 3) {
                                label = "light-maint";
                            } else if (i3 == 4) {
                                label = "  deep-idle";
                            } else if (i3 != 5) {
                                label = "         ??";
                            } else {
                                label = " deep-maint";
                            }
                            printWriter.print("    ");
                            printWriter.print(label);
                            printWriter.print(": ");
                            TimeUtils.formatDuration(this.mEventTimes[i2], now, printWriter);
                            if (this.mEventReasons[i2] != null) {
                                printWriter.print(" (");
                                printWriter.print(this.mEventReasons[i2]);
                                printWriter.print(")");
                            }
                            pw.println();
                        }
                    }
                }
                int size = this.mPowerSaveWhitelistAppsExceptIdle.size();
                if (size > 0) {
                    printWriter.println("  Whitelist (except idle) system apps:");
                    for (int i4 = 0; i4 < size; i4++) {
                        printWriter.print("    ");
                        printWriter.println(this.mPowerSaveWhitelistAppsExceptIdle.keyAt(i4));
                    }
                }
                int size2 = this.mPowerSaveWhitelistApps.size();
                if (size2 > 0) {
                    printWriter.println("  Whitelist system apps:");
                    for (int i5 = 0; i5 < size2; i5++) {
                        printWriter.print("    ");
                        printWriter.println(this.mPowerSaveWhitelistApps.keyAt(i5));
                    }
                }
                int size3 = this.mRemovedFromSystemWhitelistApps.size();
                if (size3 > 0) {
                    printWriter.println("  Removed from whitelist system apps:");
                    for (int i6 = 0; i6 < size3; i6++) {
                        printWriter.print("    ");
                        printWriter.println(this.mRemovedFromSystemWhitelistApps.keyAt(i6));
                    }
                }
                int size4 = this.mPowerSaveWhitelistUserApps.size();
                if (size4 > 0) {
                    printWriter.println("  Whitelist user apps:");
                    for (int i7 = 0; i7 < size4; i7++) {
                        printWriter.print("    ");
                        printWriter.println(this.mPowerSaveWhitelistUserApps.keyAt(i7));
                    }
                }
                int size5 = this.mPowerSaveWhitelistExceptIdleAppIds.size();
                if (size5 > 0) {
                    printWriter.println("  Whitelist (except idle) all app ids:");
                    for (int i8 = 0; i8 < size5; i8++) {
                        printWriter.print("    ");
                        printWriter.print(this.mPowerSaveWhitelistExceptIdleAppIds.keyAt(i8));
                        pw.println();
                    }
                }
                int size6 = this.mPowerSaveWhitelistUserAppIds.size();
                if (size6 > 0) {
                    printWriter.println("  Whitelist user app ids:");
                    for (int i9 = 0; i9 < size6; i9++) {
                        printWriter.print("    ");
                        printWriter.print(this.mPowerSaveWhitelistUserAppIds.keyAt(i9));
                        pw.println();
                    }
                }
                int size7 = this.mPowerSaveWhitelistAllAppIds.size();
                if (size7 > 0) {
                    printWriter.println("  Whitelist all app ids:");
                    for (int i10 = 0; i10 < size7; i10++) {
                        printWriter.print("    ");
                        printWriter.print(this.mPowerSaveWhitelistAllAppIds.keyAt(i10));
                        pw.println();
                    }
                }
                dumpTempWhitelistSchedule(printWriter, true);
                int size8 = this.mTempWhitelistAppIdArray != null ? this.mTempWhitelistAppIdArray.length : 0;
                if (size8 > 0) {
                    printWriter.println("  Temp whitelist app ids:");
                    for (int i11 = 0; i11 < size8; i11++) {
                        printWriter.print("    ");
                        printWriter.print(this.mTempWhitelistAppIdArray[i11]);
                        pw.println();
                    }
                }
                printWriter.print("  mLightEnabled=");
                printWriter.print(this.mLightEnabled);
                printWriter.print("  mDeepEnabled=");
                printWriter.println(this.mDeepEnabled);
                printWriter.print("  mForceIdle=");
                printWriter.println(this.mForceIdle);
                printWriter.print("  mUseMotionSensor=");
                printWriter.print(this.mUseMotionSensor);
                if (this.mUseMotionSensor) {
                    printWriter.print(" mMotionSensor=");
                    printWriter.println(this.mMotionSensor);
                } else {
                    pw.println();
                }
                printWriter.print("  mScreenOn=");
                printWriter.println(this.mScreenOn);
                printWriter.print("  mScreenLocked=");
                printWriter.println(this.mScreenLocked);
                printWriter.print("  mNetworkConnected=");
                printWriter.println(this.mNetworkConnected);
                printWriter.print("  mCharging=");
                printWriter.println(this.mCharging);
                if (this.mConstraints.size() != 0) {
                    printWriter.println("  mConstraints={");
                    for (int i12 = 0; i12 < this.mConstraints.size(); i12++) {
                        DeviceIdleConstraintTracker tracker = this.mConstraints.valueAt(i12);
                        printWriter.print("    \"");
                        printWriter.print(tracker.name);
                        printWriter.print("\"=");
                        if (tracker.minState == this.mState) {
                            printWriter.println(tracker.active);
                        } else {
                            printWriter.print("ignored <mMinState=");
                            printWriter.print(stateToString(tracker.minState));
                            printWriter.println(">");
                        }
                    }
                    printWriter.println("  }");
                }
                if (this.mUseMotionSensor) {
                    printWriter.print("  mMotionActive=");
                    printWriter.println(this.mMotionListener.active);
                    printWriter.print("  mNotMoving=");
                    printWriter.println(this.mNotMoving);
                }
                printWriter.print("  mLocating=");
                printWriter.print(this.mLocating);
                printWriter.print(" mHasGps=");
                printWriter.print(this.mHasGps);
                printWriter.print(" mHasNetwork=");
                printWriter.print(this.mHasNetworkLocation);
                printWriter.print(" mLocated=");
                printWriter.println(this.mLocated);
                if (this.mLastGenericLocation != null) {
                    printWriter.print("  mLastGenericLocation=");
                    printWriter.println(this.mLastGenericLocation);
                }
                if (this.mLastGpsLocation != null) {
                    printWriter.print("  mLastGpsLocation=");
                    printWriter.println(this.mLastGpsLocation);
                }
                printWriter.print("  mState=");
                printWriter.print(stateToString(this.mState));
                printWriter.print(" mLightState=");
                printWriter.println(lightStateToString(this.mLightState));
                printWriter.print("  mInactiveTimeout=");
                TimeUtils.formatDuration(this.mInactiveTimeout, printWriter);
                pw.println();
                if (this.mActiveIdleOpCount != 0) {
                    printWriter.print("  mActiveIdleOpCount=");
                    printWriter.println(this.mActiveIdleOpCount);
                }
                if (this.mNextAlarmTime != 0) {
                    printWriter.print("  mNextAlarmTime=");
                    TimeUtils.formatDuration(this.mNextAlarmTime, SystemClock.elapsedRealtime(), printWriter);
                    pw.println();
                }
                if (this.mNextIdlePendingDelay != 0) {
                    printWriter.print("  mNextIdlePendingDelay=");
                    TimeUtils.formatDuration(this.mNextIdlePendingDelay, printWriter);
                    pw.println();
                }
                if (this.mNextIdleDelay != 0) {
                    printWriter.print("  mNextIdleDelay=");
                    TimeUtils.formatDuration(this.mNextIdleDelay, printWriter);
                    pw.println();
                }
                if (this.mNextLightIdleDelay != 0) {
                    printWriter.print("  mNextIdleDelay=");
                    TimeUtils.formatDuration(this.mNextLightIdleDelay, printWriter);
                    pw.println();
                }
                if (this.mNextLightAlarmTime != 0) {
                    printWriter.print("  mNextLightAlarmTime=");
                    TimeUtils.formatDuration(this.mNextLightAlarmTime, SystemClock.elapsedRealtime(), printWriter);
                    pw.println();
                }
                if (this.mCurIdleBudget != 0) {
                    printWriter.print("  mCurIdleBudget=");
                    TimeUtils.formatDuration(this.mCurIdleBudget, printWriter);
                    pw.println();
                }
                if (this.mMaintenanceStartTime != 0) {
                    printWriter.print("  mMaintenanceStartTime=");
                    TimeUtils.formatDuration(this.mMaintenanceStartTime, SystemClock.elapsedRealtime(), printWriter);
                    pw.println();
                }
                if (this.mJobsActive) {
                    printWriter.print("  mJobsActive=");
                    printWriter.println(this.mJobsActive);
                }
                if (this.mAlarmsActive) {
                    printWriter.print("  mAlarmsActive=");
                    printWriter.println(this.mAlarmsActive);
                }
                if (Math.abs(this.mPreIdleFactor - 1.0f) > MIN_PRE_IDLE_FACTOR_CHANGE) {
                    printWriter.print("  mPreIdleFactor=");
                    printWriter.println(this.mPreIdleFactor);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void dumpTempWhitelistSchedule(PrintWriter pw, boolean printTitle) {
        int size = this.mTempWhitelistAppIdEndTimes.size();
        if (size > 0) {
            String prefix = "";
            if (printTitle) {
                pw.println("  Temp whitelist schedule:");
                prefix = "    ";
            }
            long timeNow = SystemClock.elapsedRealtime();
            for (int i = 0; i < size; i++) {
                pw.print(prefix);
                pw.print("UID=");
                pw.print(this.mTempWhitelistAppIdEndTimes.keyAt(i));
                pw.print(": ");
                Pair<MutableLong, String> entry = this.mTempWhitelistAppIdEndTimes.valueAt(i);
                TimeUtils.formatDuration(((MutableLong) entry.first).value, timeNow, pw);
                pw.print(" - ");
                pw.println((String) entry.second);
            }
        }
    }
}
