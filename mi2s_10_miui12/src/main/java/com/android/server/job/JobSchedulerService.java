package com.android.server.job;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.AlarmManager;
import android.app.AppGlobals;
import android.app.IUidObserver;
import android.app.job.IJobScheduler;
import android.app.job.JobInfo;
import android.app.job.JobSnapshot;
import android.app.job.JobWorkItem;
import android.app.usage.UsageStatsManagerInternal;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.IPackageManager;
import android.content.pm.PackageManager;
import android.content.pm.PackageManagerInternal;
import android.content.pm.ParceledListSlice;
import android.content.pm.ServiceInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.BatteryStatsInternal;
import android.os.Binder;
import android.os.Handler;
import android.os.IThermalService;
import android.os.IThermalStatusListener;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManagerInternal;
import android.os.WorkSource;
import android.provider.Settings;
import android.util.KeyValueListParser;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.StatsLog;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.IBatteryStats;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.internal.util.Preconditions;
import com.android.server.AppStateTracker;
import com.android.server.DeviceIdleController;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.MiuiFgThread;
import com.android.server.SystemService;
import com.android.server.job.JobSchedulerInternal;
import com.android.server.job.JobSchedulerService;
import com.android.server.job.controllers.BackgroundJobsController;
import com.android.server.job.controllers.BatteryController;
import com.android.server.job.controllers.ConnectivityController;
import com.android.server.job.controllers.ContentObserverController;
import com.android.server.job.controllers.DeviceIdleJobsController;
import com.android.server.job.controllers.IdleController;
import com.android.server.job.controllers.JobStatus;
import com.android.server.job.controllers.QuotaController;
import com.android.server.job.controllers.StateController;
import com.android.server.job.controllers.StorageController;
import com.android.server.job.controllers.TimeController;
import com.android.server.job.controllers.UltraPowerSaverController;
import com.android.server.pm.DumpState;
import com.android.server.pm.PackageManagerService;
import com.android.server.slice.SliceClientPermissions;
import com.android.server.utils.PriorityDump;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import libcore.util.EmptyArray;

public class JobSchedulerService extends SystemService implements StateChangedListener, JobCompletedListener {
    public static final int ACTIVE_INDEX = 0;
    public static final boolean DEBUG = Log.isLoggable(TAG, 3);
    public static final boolean DEBUG_STANDBY = (DEBUG);
    private static final boolean ENFORCE_MAX_JOBS = true;
    public static final int FREQUENT_INDEX = 2;
    static final String HEARTBEAT_TAG = "*job.heartbeat*";
    public static final long MAX_ALLOWED_PERIOD_MS = 31536000000L;
    private static final int MAX_JOBS_PER_APP = 100;
    static final int MAX_JOB_CONTEXTS_COUNT = 16;
    static final int MSG_CHECK_JOB = 1;
    static final int MSG_CHECK_JOB_GREEDY = 3;
    static final int MSG_JOB_EXPIRED = 0;
    static final int MSG_STOP_JOB = 2;
    static final int MSG_UID_ACTIVE = 6;
    static final int MSG_UID_GONE = 5;
    static final int MSG_UID_IDLE = 7;
    static final int MSG_UID_STATE_CHANGED = 4;
    public static final int NEVER_INDEX = 4;
    private static final long PERIODIC_JOB_WINDOW_BUFFER = 1800000;
    public static final int RARE_INDEX = 3;
    public static final String TAG = "JobScheduler";
    public static final int WORKING_INDEX = 1;
    static final Comparator<JobStatus> mEnqueueTimeComparator = $$Lambda$JobSchedulerService$V6_ZmVmzJutg4w0s0LktDOsRAss.INSTANCE;
    @VisibleForTesting
    public static Clock sElapsedRealtimeClock = SystemClock.elapsedRealtimeClock();
    @VisibleForTesting
    public static Clock sSystemClock = Clock.systemUTC();
    @VisibleForTesting
    public static Clock sUptimeMillisClock = SystemClock.uptimeMillisClock();
    final List<JobServiceContext> mActiveServices = new ArrayList();
    ActivityManagerInternal mActivityManagerInternal = ((ActivityManagerInternal) Preconditions.checkNotNull((ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class)));
    AppStateTracker mAppStateTracker;
    final SparseIntArray mBackingUpUids = new SparseIntArray();
    private final BatteryController mBatteryController;
    IBatteryStats mBatteryStats;
    private boolean mBeingUltraPowerSavingMode;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            List<JobStatus> jobsForUid;
            String action = intent.getAction();
            if (JobSchedulerService.DEBUG) {
                Slog.d(JobSchedulerService.TAG, "Receieved: " + action);
            }
            String pkgName = JobSchedulerService.this.getPackageName(intent);
            int pkgUid = intent.getIntExtra("android.intent.extra.UID", -1);
            int c = 0;
            if ("android.intent.action.PACKAGE_CHANGED".equals(action)) {
                if (pkgName == null || pkgUid == -1) {
                    Slog.w(JobSchedulerService.TAG, "PACKAGE_CHANGED for " + pkgName + " / uid " + pkgUid);
                    return;
                }
                String[] changedComponents = intent.getStringArrayExtra("android.intent.extra.changed_component_name_list");
                if (changedComponents != null) {
                    int length = changedComponents.length;
                    while (true) {
                        if (c >= length) {
                            break;
                        } else if (changedComponents[c].equals(pkgName)) {
                            if (JobSchedulerService.DEBUG) {
                                Slog.d(JobSchedulerService.TAG, "Package state change: " + pkgName);
                            }
                            try {
                                int userId = UserHandle.getUserId(pkgUid);
                                int state = AppGlobals.getPackageManager().getApplicationEnabledSetting(pkgName, userId);
                                if (state == 2 || state == 3) {
                                    if (JobSchedulerService.DEBUG) {
                                        Slog.d(JobSchedulerService.TAG, "Removing jobs for package " + pkgName + " in user " + userId);
                                    }
                                    JobSchedulerService.this.cancelJobsForPackageAndUid(pkgName, pkgUid, "app disabled");
                                }
                            } catch (RemoteException | IllegalArgumentException e) {
                            }
                        } else {
                            c++;
                        }
                    }
                    if (JobSchedulerService.DEBUG) {
                        Slog.d(JobSchedulerService.TAG, "Something in " + pkgName + " changed. Reevaluating controller states.");
                    }
                    synchronized (JobSchedulerService.this.mLock) {
                        for (int c2 = JobSchedulerService.this.mControllers.size() - 1; c2 >= 0; c2--) {
                            JobSchedulerService.this.mControllers.get(c2).reevaluateStateLocked(pkgUid);
                        }
                    }
                }
            } else if ("android.intent.action.PACKAGE_REMOVED".equals(action)) {
                if (!intent.getBooleanExtra("android.intent.extra.REPLACING", false)) {
                    int uidRemoved = intent.getIntExtra("android.intent.extra.UID", -1);
                    if (JobSchedulerService.DEBUG) {
                        Slog.d(JobSchedulerService.TAG, "Removing jobs for uid: " + uidRemoved);
                    }
                    JobSchedulerService.this.cancelJobsForPackageAndUid(pkgName, uidRemoved, "app uninstalled");
                    synchronized (JobSchedulerService.this.mLock) {
                        while (c < JobSchedulerService.this.mControllers.size()) {
                            JobSchedulerService.this.mControllers.get(c).onAppRemovedLocked(pkgName, pkgUid);
                            c++;
                        }
                    }
                }
            } else if ("android.intent.action.USER_REMOVED".equals(action)) {
                int userId2 = intent.getIntExtra("android.intent.extra.user_handle", 0);
                if (JobSchedulerService.DEBUG) {
                    Slog.d(JobSchedulerService.TAG, "Removing jobs for user: " + userId2);
                }
                JobSchedulerService.this.cancelJobsForUser(userId2);
                synchronized (JobSchedulerService.this.mLock) {
                    for (int c3 = 0; c3 < JobSchedulerService.this.mControllers.size(); c3++) {
                        JobSchedulerService.this.mControllers.get(c3).onUserRemovedLocked(userId2);
                    }
                }
            } else if ("android.intent.action.QUERY_PACKAGE_RESTART".equals(action)) {
                if (pkgUid != -1) {
                    synchronized (JobSchedulerService.this.mLock) {
                        jobsForUid = JobSchedulerService.this.mJobs.getJobsByUid(pkgUid);
                    }
                    for (int i = jobsForUid.size() - 1; i >= 0; i--) {
                        if (jobsForUid.get(i).getSourcePackageName().equals(pkgName)) {
                            if (JobSchedulerService.DEBUG) {
                                Slog.d(JobSchedulerService.TAG, "Restart query: package " + pkgName + " at uid " + pkgUid + " has jobs");
                            }
                            setResultCode(-1);
                            return;
                        }
                    }
                }
            } else if ("android.intent.action.PACKAGE_RESTARTED".equals(action) && pkgUid != -1) {
                if (JobSchedulerService.DEBUG) {
                    Slog.d(JobSchedulerService.TAG, "Removing jobs for pkg " + pkgName + " at uid " + pkgUid);
                }
                JobSchedulerService.this.cancelJobsForPackageAndUid(pkgName, pkgUid, "app force stopped");
            }
        }
    };
    final JobConcurrencyManager mConcurrencyManager = new JobConcurrencyManager(this);
    final Constants mConstants = new Constants();
    final ConstantsObserver mConstantsObserver = new ConstantsObserver(this.mHandler);
    final List<StateController> mControllers;
    /* access modifiers changed from: private */
    public final DeviceIdleJobsController mDeviceIdleJobsController;
    final JobHandler mHandler = new JobHandler(MiuiFgThread.get().getLooper());
    long mHeartbeat = 0;
    final HeartbeatAlarmListener mHeartbeatAlarm = new HeartbeatAlarmListener();
    volatile boolean mInParole;
    private final Predicate<Integer> mIsUidActivePredicate = new Predicate() {
        public final boolean test(Object obj) {
            return JobSchedulerService.this.isUidActive(((Integer) obj).intValue());
        }
    };
    final JobPackageTracker mJobPackageTracker = new JobPackageTracker();
    final JobSchedulerStub mJobSchedulerStub = new JobSchedulerStub();
    /* access modifiers changed from: private */
    public final Runnable mJobTimeUpdater = new Runnable() {
        public final void run() {
            JobSchedulerService.this.lambda$new$1$JobSchedulerService();
        }
    };
    final JobStore mJobs;
    long mLastHeartbeatTime = sElapsedRealtimeClock.millis();
    final SparseArray<HashMap<String, Long>> mLastJobHeartbeats = new SparseArray<>();
    DeviceIdleController.LocalService mLocalDeviceIdleController;
    PackageManagerInternal mLocalPM = ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class));
    final Object mLock = new Object();
    private final MaybeReadyJobQueueFunctor mMaybeQueueFunctor = new MaybeReadyJobQueueFunctor();
    final long[] mNextBucketHeartbeat = {0, 0, 0, 0, JobStatus.NO_LATEST_RUNTIME};
    final ArrayList<JobStatus> mPendingJobs = new ArrayList<>();
    private final ReadyJobQueueFunctor mReadyQueueFunctor = new ReadyJobQueueFunctor();
    boolean mReadyToRock;
    boolean mReportedActive;
    final StandbyTracker mStandbyTracker = new StandbyTracker();
    int[] mStartedUsers = EmptyArray.INT;
    private final StorageController mStorageController;
    /* access modifiers changed from: private */
    @GuardedBy({"mLock"})
    public boolean mThermalConstraint = false;
    private IThermalService mThermalService;
    private final BroadcastReceiver mTimeSetReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.TIME_SET".equals(intent.getAction()) && JobSchedulerService.this.mJobs.clockNowValidToInflate(JobSchedulerService.sSystemClock.millis())) {
                Slog.i(JobSchedulerService.TAG, "RTC now valid; recalculating persisted job windows");
                context.unregisterReceiver(this);
                FgThread.getHandler().post(JobSchedulerService.this.mJobTimeUpdater);
            }
        }
    };
    private final IUidObserver mUidObserver = new IUidObserver.Stub() {
        public void onUidStateChanged(int uid, int procState, long procStateSeq) {
            JobSchedulerService.this.mHandler.obtainMessage(4, uid, procState).sendToTarget();
        }

        public void onUidGone(int uid, boolean disabled) {
            JobSchedulerService.this.mHandler.obtainMessage(5, uid, disabled).sendToTarget();
        }

        public void onUidActive(int uid) throws RemoteException {
            JobSchedulerService.this.mHandler.obtainMessage(6, uid, 0).sendToTarget();
        }

        public void onUidIdle(int uid, boolean disabled) {
            JobSchedulerService.this.mHandler.obtainMessage(7, uid, disabled).sendToTarget();
        }

        public void onUidCachedChanged(int uid, boolean cached) {
        }
    };
    final SparseIntArray mUidPriorityOverride = new SparseIntArray();
    private UltraPowerSaverController mUltraPowerSaverController;
    final UsageStatsManagerInternal mUsageStats = ((UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class));

    private class ConstantsObserver extends ContentObserver {
        private ContentResolver mResolver;

        public ConstantsObserver(Handler handler) {
            super(handler);
        }

        public void start(ContentResolver resolver) {
            this.mResolver = resolver;
            this.mResolver.registerContentObserver(Settings.Global.getUriFor("job_scheduler_constants"), false, this);
            updateConstants();
        }

        public void onChange(boolean selfChange, Uri uri) {
            updateConstants();
        }

        private void updateConstants() {
            synchronized (JobSchedulerService.this.mLock) {
                try {
                    JobSchedulerService.this.mConstants.updateConstantsLocked(Settings.Global.getString(this.mResolver, "job_scheduler_constants"));
                    for (int controller = 0; controller < JobSchedulerService.this.mControllers.size(); controller++) {
                        JobSchedulerService.this.mControllers.get(controller).onConstantsUpdatedLocked();
                    }
                } catch (IllegalArgumentException e) {
                    Slog.e(JobSchedulerService.TAG, "Bad jobscheduler settings", e);
                }
            }
            if (JobSchedulerService.this.mConstants.USE_HEARTBEATS) {
                JobSchedulerService.this.setNextHeartbeatAlarm();
            }
        }
    }

    private final class ThermalStatusListener extends IThermalStatusListener.Stub {
        private ThermalStatusListener() {
        }

        public void onStatusChange(int status) {
            synchronized (JobSchedulerService.this.mLock) {
                boolean unused = JobSchedulerService.this.mThermalConstraint = status >= 3;
            }
            JobSchedulerService.this.onControllerStateChanged();
        }
    }

    static class MaxJobCounts {
        private final KeyValueListParser.IntValue mMaxBg;
        private final KeyValueListParser.IntValue mMinBg;
        private final KeyValueListParser.IntValue mTotal;

        MaxJobCounts(int totalDefault, String totalKey, int maxBgDefault, String maxBgKey, int minBgDefault, String minBgKey) {
            this.mTotal = new KeyValueListParser.IntValue(totalKey, totalDefault);
            this.mMaxBg = new KeyValueListParser.IntValue(maxBgKey, maxBgDefault);
            this.mMinBg = new KeyValueListParser.IntValue(minBgKey, minBgDefault);
        }

        public void parse(KeyValueListParser parser) {
            this.mTotal.parse(parser);
            this.mMaxBg.parse(parser);
            this.mMinBg.parse(parser);
            if (this.mTotal.getValue() < 1) {
                this.mTotal.setValue(1);
            } else if (this.mTotal.getValue() > 16) {
                this.mTotal.setValue(16);
            }
            if (this.mMaxBg.getValue() < 1) {
                this.mMaxBg.setValue(1);
            } else if (this.mMaxBg.getValue() > this.mTotal.getValue()) {
                this.mMaxBg.setValue(this.mTotal.getValue());
            }
            if (this.mMinBg.getValue() < 0) {
                this.mMinBg.setValue(0);
                return;
            }
            if (this.mMinBg.getValue() > this.mMaxBg.getValue()) {
                this.mMinBg.setValue(this.mMaxBg.getValue());
            }
            if (this.mMinBg.getValue() >= this.mTotal.getValue()) {
                this.mMinBg.setValue(this.mTotal.getValue() - 1);
            }
        }

        public int getMaxTotal() {
            return this.mTotal.getValue();
        }

        public int getMaxBg() {
            return this.mMaxBg.getValue();
        }

        public int getMinBg() {
            return this.mMinBg.getValue();
        }

        public void dump(PrintWriter pw, String prefix) {
            this.mTotal.dump(pw, prefix);
            this.mMaxBg.dump(pw, prefix);
            this.mMinBg.dump(pw, prefix);
        }

        public void dumpProto(ProtoOutputStream proto, long fieldId) {
            long token = proto.start(fieldId);
            this.mTotal.dumpProto(proto, 1120986464257L);
            this.mMaxBg.dumpProto(proto, 1120986464258L);
            this.mMinBg.dumpProto(proto, 1120986464259L);
            proto.end(token);
        }
    }

    static class MaxJobCountsPerMemoryTrimLevel {
        public final MaxJobCounts critical;
        public final MaxJobCounts low;
        public final MaxJobCounts moderate;
        public final MaxJobCounts normal;

        MaxJobCountsPerMemoryTrimLevel(MaxJobCounts normal2, MaxJobCounts moderate2, MaxJobCounts low2, MaxJobCounts critical2) {
            this.normal = normal2;
            this.moderate = moderate2;
            this.low = low2;
            this.critical = critical2;
        }

        public void dumpProto(ProtoOutputStream proto, long fieldId) {
            long token = proto.start(fieldId);
            this.normal.dumpProto(proto, 1146756268033L);
            this.moderate.dumpProto(proto, 1146756268034L);
            this.low.dumpProto(proto, 1146756268035L);
            this.critical.dumpProto(proto, 1146756268036L);
            proto.end(token);
        }
    }

    public static class Constants {
        private static final float DEFAULT_CONN_CONGESTION_DELAY_FRAC = 0.5f;
        private static final float DEFAULT_CONN_PREFETCH_RELAX_FRAC = 0.5f;
        private static final float DEFAULT_HEAVY_USE_FACTOR = 0.9f;
        private static final int DEFAULT_MAX_STANDARD_RESCHEDULE_COUNT = Integer.MAX_VALUE;
        private static final int DEFAULT_MAX_WORK_RESCHEDULE_COUNT = Integer.MAX_VALUE;
        private static final int DEFAULT_MIN_BATTERY_NOT_LOW_COUNT = 1;
        private static final int DEFAULT_MIN_CHARGING_COUNT = 1;
        private static final int DEFAULT_MIN_CONNECTIVITY_COUNT = 1;
        private static final int DEFAULT_MIN_CONTENT_COUNT = 1;
        private static final long DEFAULT_MIN_EXP_BACKOFF_TIME = 10000;
        private static final int DEFAULT_MIN_IDLE_COUNT = 1;
        private static final long DEFAULT_MIN_LINEAR_BACKOFF_TIME = 10000;
        private static final int DEFAULT_MIN_READY_JOBS_COUNT = 1;
        private static final int DEFAULT_MIN_STORAGE_NOT_LOW_COUNT = 1;
        private static final float DEFAULT_MODERATE_USE_FACTOR = 0.5f;
        private static final int DEFAULT_STANDBY_FREQUENT_BEATS = 43;
        private static final long DEFAULT_STANDBY_HEARTBEAT_TIME = 660000;
        private static final int DEFAULT_STANDBY_RARE_BEATS = 130;
        private static final int DEFAULT_STANDBY_WORKING_BEATS = 11;
        private static final boolean DEFAULT_USE_HEARTBEATS = false;
        private static final String DEPRECATED_KEY_BG_CRITICAL_JOB_COUNT = "bg_critical_job_count";
        private static final String DEPRECATED_KEY_BG_LOW_JOB_COUNT = "bg_low_job_count";
        private static final String DEPRECATED_KEY_BG_MODERATE_JOB_COUNT = "bg_moderate_job_count";
        private static final String DEPRECATED_KEY_BG_NORMAL_JOB_COUNT = "bg_normal_job_count";
        private static final String DEPRECATED_KEY_FG_JOB_COUNT = "fg_job_count";
        private static final String KEY_CONN_CONGESTION_DELAY_FRAC = "conn_congestion_delay_frac";
        private static final String KEY_CONN_PREFETCH_RELAX_FRAC = "conn_prefetch_relax_frac";
        private static final String KEY_HEAVY_USE_FACTOR = "heavy_use_factor";
        private static final String KEY_MAX_STANDARD_RESCHEDULE_COUNT = "max_standard_reschedule_count";
        private static final String KEY_MAX_WORK_RESCHEDULE_COUNT = "max_work_reschedule_count";
        private static final String KEY_MIN_BATTERY_NOT_LOW_COUNT = "min_battery_not_low_count";
        private static final String KEY_MIN_CHARGING_COUNT = "min_charging_count";
        private static final String KEY_MIN_CONNECTIVITY_COUNT = "min_connectivity_count";
        private static final String KEY_MIN_CONTENT_COUNT = "min_content_count";
        private static final String KEY_MIN_EXP_BACKOFF_TIME = "min_exp_backoff_time";
        private static final String KEY_MIN_IDLE_COUNT = "min_idle_count";
        private static final String KEY_MIN_LINEAR_BACKOFF_TIME = "min_linear_backoff_time";
        private static final String KEY_MIN_READY_JOBS_COUNT = "min_ready_jobs_count";
        private static final String KEY_MIN_STORAGE_NOT_LOW_COUNT = "min_storage_not_low_count";
        private static final String KEY_MODERATE_USE_FACTOR = "moderate_use_factor";
        private static final String KEY_STANDBY_FREQUENT_BEATS = "standby_frequent_beats";
        private static final String KEY_STANDBY_HEARTBEAT_TIME = "standby_heartbeat_time";
        private static final String KEY_STANDBY_RARE_BEATS = "standby_rare_beats";
        private static final String KEY_STANDBY_WORKING_BEATS = "standby_working_beats";
        private static final String KEY_USE_HEARTBEATS = "use_heartbeats";
        public float CONN_CONGESTION_DELAY_FRAC = 0.5f;
        public float CONN_PREFETCH_RELAX_FRAC = 0.5f;
        float HEAVY_USE_FACTOR = DEFAULT_HEAVY_USE_FACTOR;
        final MaxJobCountsPerMemoryTrimLevel MAX_JOB_COUNTS_SCREEN_OFF = new MaxJobCountsPerMemoryTrimLevel(new MaxJobCounts(10, "max_job_total_off_normal", 6, "max_job_max_bg_off_normal", 2, "max_job_min_bg_off_normal"), new MaxJobCounts(10, "max_job_total_off_moderate", 4, "max_job_max_bg_off_moderate", 2, "max_job_min_bg_off_moderate"), new MaxJobCounts(5, "max_job_total_off_low", 1, "max_job_max_bg_off_low", 1, "max_job_min_bg_off_low"), new MaxJobCounts(5, "max_job_total_off_critical", 1, "max_job_max_bg_off_critical", 1, "max_job_min_bg_off_critical"));
        final MaxJobCountsPerMemoryTrimLevel MAX_JOB_COUNTS_SCREEN_ON = new MaxJobCountsPerMemoryTrimLevel(new MaxJobCounts(8, "max_job_total_on_normal", 6, "max_job_max_bg_on_normal", 2, "max_job_min_bg_on_normal"), new MaxJobCounts(8, "max_job_total_on_moderate", 4, "max_job_max_bg_on_moderate", 2, "max_job_min_bg_on_moderate"), new MaxJobCounts(5, "max_job_total_on_low", 1, "max_job_max_bg_on_low", 1, "max_job_min_bg_on_low"), new MaxJobCounts(5, "max_job_total_on_critical", 1, "max_job_max_bg_on_critical", 1, "max_job_min_bg_on_critical"));
        int MAX_STANDARD_RESCHEDULE_COUNT = Integer.MAX_VALUE;
        int MAX_WORK_RESCHEDULE_COUNT = Integer.MAX_VALUE;
        int MIN_BATTERY_NOT_LOW_COUNT = 1;
        int MIN_CHARGING_COUNT = 1;
        int MIN_CONNECTIVITY_COUNT = 1;
        int MIN_CONTENT_COUNT = 1;
        long MIN_EXP_BACKOFF_TIME = JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY;
        int MIN_IDLE_COUNT = 1;
        long MIN_LINEAR_BACKOFF_TIME = JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY;
        int MIN_READY_JOBS_COUNT = 1;
        int MIN_STORAGE_NOT_LOW_COUNT = 1;
        float MODERATE_USE_FACTOR = 0.5f;
        final KeyValueListParser.IntValue SCREEN_OFF_JOB_CONCURRENCY_INCREASE_DELAY_MS = new KeyValueListParser.IntValue("screen_off_job_concurrency_increase_delay_ms", 30000);
        final int[] STANDBY_BEATS = {0, 11, 43, DEFAULT_STANDBY_RARE_BEATS};
        long STANDBY_HEARTBEAT_TIME = DEFAULT_STANDBY_HEARTBEAT_TIME;
        public boolean USE_HEARTBEATS = false;
        private final KeyValueListParser mParser = new KeyValueListParser(',');

        public Constants() {
        }

        /* access modifiers changed from: package-private */
        public void updateConstantsLocked(String value) {
            try {
                this.mParser.setString(value);
            } catch (Exception e) {
                Slog.e(JobSchedulerService.TAG, "Bad jobscheduler settings", e);
            }
            this.MIN_IDLE_COUNT = this.mParser.getInt(KEY_MIN_IDLE_COUNT, 1);
            this.MIN_CHARGING_COUNT = this.mParser.getInt(KEY_MIN_CHARGING_COUNT, 1);
            this.MIN_BATTERY_NOT_LOW_COUNT = this.mParser.getInt(KEY_MIN_BATTERY_NOT_LOW_COUNT, 1);
            this.MIN_STORAGE_NOT_LOW_COUNT = this.mParser.getInt(KEY_MIN_STORAGE_NOT_LOW_COUNT, 1);
            this.MIN_CONNECTIVITY_COUNT = this.mParser.getInt(KEY_MIN_CONNECTIVITY_COUNT, 1);
            this.MIN_CONTENT_COUNT = this.mParser.getInt(KEY_MIN_CONTENT_COUNT, 1);
            this.MIN_READY_JOBS_COUNT = this.mParser.getInt(KEY_MIN_READY_JOBS_COUNT, 1);
            this.HEAVY_USE_FACTOR = this.mParser.getFloat(KEY_HEAVY_USE_FACTOR, DEFAULT_HEAVY_USE_FACTOR);
            this.MODERATE_USE_FACTOR = this.mParser.getFloat(KEY_MODERATE_USE_FACTOR, 0.5f);
            this.MAX_JOB_COUNTS_SCREEN_ON.normal.parse(this.mParser);
            this.MAX_JOB_COUNTS_SCREEN_ON.moderate.parse(this.mParser);
            this.MAX_JOB_COUNTS_SCREEN_ON.low.parse(this.mParser);
            this.MAX_JOB_COUNTS_SCREEN_ON.critical.parse(this.mParser);
            this.MAX_JOB_COUNTS_SCREEN_OFF.normal.parse(this.mParser);
            this.MAX_JOB_COUNTS_SCREEN_OFF.moderate.parse(this.mParser);
            this.MAX_JOB_COUNTS_SCREEN_OFF.low.parse(this.mParser);
            this.MAX_JOB_COUNTS_SCREEN_OFF.critical.parse(this.mParser);
            this.SCREEN_OFF_JOB_CONCURRENCY_INCREASE_DELAY_MS.parse(this.mParser);
            this.MAX_STANDARD_RESCHEDULE_COUNT = this.mParser.getInt(KEY_MAX_STANDARD_RESCHEDULE_COUNT, Integer.MAX_VALUE);
            this.MAX_WORK_RESCHEDULE_COUNT = this.mParser.getInt(KEY_MAX_WORK_RESCHEDULE_COUNT, Integer.MAX_VALUE);
            this.MIN_LINEAR_BACKOFF_TIME = this.mParser.getDurationMillis(KEY_MIN_LINEAR_BACKOFF_TIME, JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY);
            this.MIN_EXP_BACKOFF_TIME = this.mParser.getDurationMillis(KEY_MIN_EXP_BACKOFF_TIME, JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY);
            this.STANDBY_HEARTBEAT_TIME = this.mParser.getDurationMillis(KEY_STANDBY_HEARTBEAT_TIME, DEFAULT_STANDBY_HEARTBEAT_TIME);
            this.STANDBY_BEATS[1] = this.mParser.getInt(KEY_STANDBY_WORKING_BEATS, 11);
            this.STANDBY_BEATS[2] = this.mParser.getInt(KEY_STANDBY_FREQUENT_BEATS, 43);
            this.STANDBY_BEATS[3] = this.mParser.getInt(KEY_STANDBY_RARE_BEATS, DEFAULT_STANDBY_RARE_BEATS);
            this.CONN_CONGESTION_DELAY_FRAC = this.mParser.getFloat(KEY_CONN_CONGESTION_DELAY_FRAC, 0.5f);
            this.CONN_PREFETCH_RELAX_FRAC = this.mParser.getFloat(KEY_CONN_PREFETCH_RELAX_FRAC, 0.5f);
            this.USE_HEARTBEATS = this.mParser.getBoolean(KEY_USE_HEARTBEATS, false);
        }

        /* access modifiers changed from: package-private */
        public void dump(IndentingPrintWriter pw) {
            pw.println("Settings:");
            pw.increaseIndent();
            pw.printPair(KEY_MIN_IDLE_COUNT, Integer.valueOf(this.MIN_IDLE_COUNT)).println();
            pw.printPair(KEY_MIN_CHARGING_COUNT, Integer.valueOf(this.MIN_CHARGING_COUNT)).println();
            pw.printPair(KEY_MIN_BATTERY_NOT_LOW_COUNT, Integer.valueOf(this.MIN_BATTERY_NOT_LOW_COUNT)).println();
            pw.printPair(KEY_MIN_STORAGE_NOT_LOW_COUNT, Integer.valueOf(this.MIN_STORAGE_NOT_LOW_COUNT)).println();
            pw.printPair(KEY_MIN_CONNECTIVITY_COUNT, Integer.valueOf(this.MIN_CONNECTIVITY_COUNT)).println();
            pw.printPair(KEY_MIN_CONTENT_COUNT, Integer.valueOf(this.MIN_CONTENT_COUNT)).println();
            pw.printPair(KEY_MIN_READY_JOBS_COUNT, Integer.valueOf(this.MIN_READY_JOBS_COUNT)).println();
            pw.printPair(KEY_HEAVY_USE_FACTOR, Float.valueOf(this.HEAVY_USE_FACTOR)).println();
            pw.printPair(KEY_MODERATE_USE_FACTOR, Float.valueOf(this.MODERATE_USE_FACTOR)).println();
            this.MAX_JOB_COUNTS_SCREEN_ON.normal.dump(pw, "");
            this.MAX_JOB_COUNTS_SCREEN_ON.moderate.dump(pw, "");
            this.MAX_JOB_COUNTS_SCREEN_ON.low.dump(pw, "");
            this.MAX_JOB_COUNTS_SCREEN_ON.critical.dump(pw, "");
            this.MAX_JOB_COUNTS_SCREEN_OFF.normal.dump(pw, "");
            this.MAX_JOB_COUNTS_SCREEN_OFF.moderate.dump(pw, "");
            this.MAX_JOB_COUNTS_SCREEN_OFF.low.dump(pw, "");
            this.MAX_JOB_COUNTS_SCREEN_OFF.critical.dump(pw, "");
            this.SCREEN_OFF_JOB_CONCURRENCY_INCREASE_DELAY_MS.dump(pw, "");
            pw.printPair(KEY_MAX_STANDARD_RESCHEDULE_COUNT, Integer.valueOf(this.MAX_STANDARD_RESCHEDULE_COUNT)).println();
            pw.printPair(KEY_MAX_WORK_RESCHEDULE_COUNT, Integer.valueOf(this.MAX_WORK_RESCHEDULE_COUNT)).println();
            pw.printPair(KEY_MIN_LINEAR_BACKOFF_TIME, Long.valueOf(this.MIN_LINEAR_BACKOFF_TIME)).println();
            pw.printPair(KEY_MIN_EXP_BACKOFF_TIME, Long.valueOf(this.MIN_EXP_BACKOFF_TIME)).println();
            pw.printPair(KEY_STANDBY_HEARTBEAT_TIME, Long.valueOf(this.STANDBY_HEARTBEAT_TIME)).println();
            pw.print("standby_beats={");
            pw.print(this.STANDBY_BEATS[0]);
            for (int i = 1; i < this.STANDBY_BEATS.length; i++) {
                pw.print(", ");
                pw.print(this.STANDBY_BEATS[i]);
            }
            pw.println('}');
            pw.printPair(KEY_CONN_CONGESTION_DELAY_FRAC, Float.valueOf(this.CONN_CONGESTION_DELAY_FRAC)).println();
            pw.printPair(KEY_CONN_PREFETCH_RELAX_FRAC, Float.valueOf(this.CONN_PREFETCH_RELAX_FRAC)).println();
            pw.printPair(KEY_USE_HEARTBEATS, Boolean.valueOf(this.USE_HEARTBEATS)).println();
            pw.decreaseIndent();
        }

        /* access modifiers changed from: package-private */
        public void dump(ProtoOutputStream proto) {
            proto.write(1120986464257L, this.MIN_IDLE_COUNT);
            proto.write(1120986464258L, this.MIN_CHARGING_COUNT);
            proto.write(1120986464259L, this.MIN_BATTERY_NOT_LOW_COUNT);
            proto.write(1120986464260L, this.MIN_STORAGE_NOT_LOW_COUNT);
            proto.write(1120986464261L, this.MIN_CONNECTIVITY_COUNT);
            proto.write(1120986464262L, this.MIN_CONTENT_COUNT);
            proto.write(1120986464263L, this.MIN_READY_JOBS_COUNT);
            proto.write(1103806595080L, this.HEAVY_USE_FACTOR);
            proto.write(1103806595081L, this.MODERATE_USE_FACTOR);
            this.MAX_JOB_COUNTS_SCREEN_ON.dumpProto(proto, 1146756268058L);
            this.MAX_JOB_COUNTS_SCREEN_OFF.dumpProto(proto, 1146756268059L);
            this.SCREEN_OFF_JOB_CONCURRENCY_INCREASE_DELAY_MS.dumpProto(proto, 1120986464284L);
            proto.write(1120986464271L, this.MAX_STANDARD_RESCHEDULE_COUNT);
            proto.write(1120986464272L, this.MAX_WORK_RESCHEDULE_COUNT);
            proto.write(1112396529681L, this.MIN_LINEAR_BACKOFF_TIME);
            proto.write(1112396529682L, this.MIN_EXP_BACKOFF_TIME);
            proto.write(1112396529683L, this.STANDBY_HEARTBEAT_TIME);
            for (int period : this.STANDBY_BEATS) {
                proto.write(2220498092052L, period);
            }
            proto.write(1103806595093L, this.CONN_CONGESTION_DELAY_FRAC);
            proto.write(1103806595094L, this.CONN_PREFETCH_RELAX_FRAC);
            proto.write(1133871366167L, this.USE_HEARTBEATS);
        }
    }

    static /* synthetic */ int lambda$static$0(JobStatus o1, JobStatus o2) {
        if (o1.enqueueTime < o2.enqueueTime) {
            return -1;
        }
        return o1.enqueueTime > o2.enqueueTime ? 1 : 0;
    }

    static <T> void addOrderedItem(ArrayList<T> array, T newItem, Comparator<T> comparator) {
        int where = Collections.binarySearch(array, newItem, comparator);
        if (where < 0) {
            where = ~where;
        }
        array.add(where, newItem);
    }

    /* access modifiers changed from: private */
    public String getPackageName(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null) {
            return uri.getSchemeSpecificPart();
        }
        return null;
    }

    public Context getTestableContext() {
        return getContext();
    }

    public Object getLock() {
        return this.mLock;
    }

    public JobStore getJobStore() {
        return this.mJobs;
    }

    public Constants getConstants() {
        return this.mConstants;
    }

    public boolean isChainedAttributionEnabled() {
        return WorkSource.isChainedBatteryAttributionEnabled(getContext());
    }

    public void onStartUser(int userHandle) {
        synchronized (this.mLock) {
            this.mStartedUsers = ArrayUtils.appendInt(this.mStartedUsers, userHandle);
        }
        this.mHandler.obtainMessage(1).sendToTarget();
    }

    public void onUnlockUser(int userHandle) {
        this.mHandler.obtainMessage(1).sendToTarget();
    }

    public void onStopUser(int userHandle) {
        synchronized (this.mLock) {
            this.mStartedUsers = ArrayUtils.removeInt(this.mStartedUsers, userHandle);
        }
    }

    /* access modifiers changed from: private */
    public boolean isUidActive(int uid) {
        return this.mAppStateTracker.isUidActiveSynced(uid);
    }

    /* Debug info: failed to restart local var, previous not found, register: 19 */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x011d, code lost:
        return 1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int scheduleAsPackage(android.app.job.JobInfo r20, android.app.job.JobWorkItem r21, int r22, java.lang.String r23, int r24, java.lang.String r25) {
        /*
            r19 = this;
            r1 = r19
            r2 = r20
            r3 = r21
            r12 = r22
            r13 = r23
            android.app.IActivityManager r0 = android.app.ActivityManager.getService()     // Catch:{ RemoteException -> 0x0046 }
            android.content.ComponentName r4 = r20.getService()     // Catch:{ RemoteException -> 0x0046 }
            java.lang.String r4 = r4.getPackageName()     // Catch:{ RemoteException -> 0x0046 }
            boolean r0 = r0.isAppStartModeDisabled(r12, r4)     // Catch:{ RemoteException -> 0x0046 }
            if (r0 == 0) goto L_0x0045
            java.lang.String r0 = "JobScheduler"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x0046 }
            r4.<init>()     // Catch:{ RemoteException -> 0x0046 }
            java.lang.String r5 = "Not scheduling job "
            r4.append(r5)     // Catch:{ RemoteException -> 0x0046 }
            r4.append(r12)     // Catch:{ RemoteException -> 0x0046 }
            java.lang.String r5 = ":"
            r4.append(r5)     // Catch:{ RemoteException -> 0x0046 }
            java.lang.String r5 = r20.toString()     // Catch:{ RemoteException -> 0x0046 }
            r4.append(r5)     // Catch:{ RemoteException -> 0x0046 }
            java.lang.String r5 = " -- package not allowed to start"
            r4.append(r5)     // Catch:{ RemoteException -> 0x0046 }
            java.lang.String r4 = r4.toString()     // Catch:{ RemoteException -> 0x0046 }
            android.util.Slog.w(r0, r4)     // Catch:{ RemoteException -> 0x0046 }
            r0 = 0
            return r0
        L_0x0045:
            goto L_0x0047
        L_0x0046:
            r0 = move-exception
        L_0x0047:
            java.lang.Object r14 = r1.mLock
            monitor-enter(r14)
            com.android.server.job.JobStore r0 = r1.mJobs     // Catch:{ all -> 0x011f }
            int r4 = r20.getId()     // Catch:{ all -> 0x011f }
            com.android.server.job.controllers.JobStatus r0 = r0.getJobByUidAndJobId(r12, r4)     // Catch:{ all -> 0x011f }
            r15 = 1
            if (r3 == 0) goto L_0x0071
            if (r0 == 0) goto L_0x0071
            android.app.job.JobInfo r4 = r0.getJob()     // Catch:{ all -> 0x011f }
            boolean r4 = r4.equals(r2)     // Catch:{ all -> 0x011f }
            if (r4 == 0) goto L_0x0071
            android.app.IActivityManager r4 = android.app.ActivityManager.getService()     // Catch:{ all -> 0x011f }
            r0.enqueueWorkLocked(r4, r3)     // Catch:{ all -> 0x011f }
            java.util.function.Predicate<java.lang.Integer> r4 = r1.mIsUidActivePredicate     // Catch:{ all -> 0x011f }
            r0.maybeAddForegroundExemption(r4)     // Catch:{ all -> 0x011f }
            monitor-exit(r14)     // Catch:{ all -> 0x011f }
            return r15
        L_0x0071:
            r11 = r24
            r10 = r25
            com.android.server.job.controllers.JobStatus r4 = com.android.server.job.controllers.JobStatus.createFromJobInfo(r2, r12, r13, r11, r10)     // Catch:{ all -> 0x011f }
            r9 = r4
            java.util.function.Predicate<java.lang.Integer> r4 = r1.mIsUidActivePredicate     // Catch:{ all -> 0x011f }
            r9.maybeAddForegroundExemption(r4)     // Catch:{ all -> 0x011f }
            boolean r4 = DEBUG     // Catch:{ all -> 0x011f }
            if (r4 == 0) goto L_0x009d
            java.lang.String r4 = "JobScheduler"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x011f }
            r5.<init>()     // Catch:{ all -> 0x011f }
            java.lang.String r6 = "SCHEDULE: "
            r5.append(r6)     // Catch:{ all -> 0x011f }
            java.lang.String r6 = r9.toShortString()     // Catch:{ all -> 0x011f }
            r5.append(r6)     // Catch:{ all -> 0x011f }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x011f }
            android.util.Slog.d(r4, r5)     // Catch:{ all -> 0x011f }
        L_0x009d:
            if (r13 != 0) goto L_0x00c8
            com.android.server.job.JobStore r4 = r1.mJobs     // Catch:{ all -> 0x011f }
            int r4 = r4.countJobsForUid(r12)     // Catch:{ all -> 0x011f }
            r5 = 100
            if (r4 > r5) goto L_0x00aa
            goto L_0x00c8
        L_0x00aa:
            java.lang.String r4 = "JobScheduler"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x011f }
            r5.<init>()     // Catch:{ all -> 0x011f }
            java.lang.String r6 = "Too many jobs for uid "
            r5.append(r6)     // Catch:{ all -> 0x011f }
            r5.append(r12)     // Catch:{ all -> 0x011f }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x011f }
            android.util.Slog.w(r4, r5)     // Catch:{ all -> 0x011f }
            java.lang.IllegalStateException r4 = new java.lang.IllegalStateException     // Catch:{ all -> 0x011f }
            java.lang.String r5 = "Apps may not schedule more than 100 distinct jobs"
            r4.<init>(r5)     // Catch:{ all -> 0x011f }
            throw r4     // Catch:{ all -> 0x011f }
        L_0x00c8:
            android.app.IActivityManager r4 = android.app.ActivityManager.getService()     // Catch:{ all -> 0x011f }
            r9.prepareLocked(r4)     // Catch:{ all -> 0x011f }
            if (r3 == 0) goto L_0x00d8
            android.app.IActivityManager r4 = android.app.ActivityManager.getService()     // Catch:{ all -> 0x011f }
            r9.enqueueWorkLocked(r4, r3)     // Catch:{ all -> 0x011f }
        L_0x00d8:
            if (r0 == 0) goto L_0x00e1
            java.lang.String r4 = "job rescheduled by app"
            r1.cancelJobImplLocked(r0, r9, r4)     // Catch:{ all -> 0x011f }
            goto L_0x00e5
        L_0x00e1:
            r4 = 0
            r1.startTrackingJobLocked(r9, r4)     // Catch:{ all -> 0x011f }
        L_0x00e5:
            r4 = 8
            r6 = 0
            java.lang.String r7 = r9.getBatteryName()     // Catch:{ all -> 0x011f }
            r8 = 2
            r16 = 0
            int r17 = r9.getStandbyBucket()     // Catch:{ all -> 0x011f }
            int r18 = r9.getJobId()     // Catch:{ all -> 0x011f }
            r5 = r22
            r15 = r9
            r9 = r16
            r10 = r17
            r11 = r18
            android.util.StatsLog.write_non_chained(r4, r5, r6, r7, r8, r9, r10, r11)     // Catch:{ all -> 0x011f }
            boolean r4 = r1.isReadyToBeExecutedLocked(r15)     // Catch:{ all -> 0x011f }
            if (r4 == 0) goto L_0x0119
            com.android.server.job.JobPackageTracker r4 = r1.mJobPackageTracker     // Catch:{ all -> 0x011f }
            r4.notePending(r15)     // Catch:{ all -> 0x011f }
            java.util.ArrayList<com.android.server.job.controllers.JobStatus> r4 = r1.mPendingJobs     // Catch:{ all -> 0x011f }
            java.util.Comparator<com.android.server.job.controllers.JobStatus> r5 = mEnqueueTimeComparator     // Catch:{ all -> 0x011f }
            addOrderedItem(r4, r15, r5)     // Catch:{ all -> 0x011f }
            r19.maybeRunPendingJobsLocked()     // Catch:{ all -> 0x011f }
            goto L_0x011c
        L_0x0119:
            r1.evaluateControllerStatesLocked(r15)     // Catch:{ all -> 0x011f }
        L_0x011c:
            monitor-exit(r14)     // Catch:{ all -> 0x011f }
            r0 = 1
            return r0
        L_0x011f:
            r0 = move-exception
            monitor-exit(r14)     // Catch:{ all -> 0x011f }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.job.JobSchedulerService.scheduleAsPackage(android.app.job.JobInfo, android.app.job.JobWorkItem, int, java.lang.String, int, java.lang.String):int");
    }

    public List<JobInfo> getPendingJobs(int uid) {
        ArrayList<JobInfo> outList;
        synchronized (this.mLock) {
            List<JobStatus> jobs = this.mJobs.getJobsByUid(uid);
            outList = new ArrayList<>(jobs.size());
            for (int i = jobs.size() - 1; i >= 0; i--) {
                outList.add(jobs.get(i).getJob());
            }
        }
        return outList;
    }

    public JobInfo getPendingJob(int uid, int jobId) {
        synchronized (this.mLock) {
            List<JobStatus> jobs = this.mJobs.getJobsByUid(uid);
            for (int i = jobs.size() - 1; i >= 0; i--) {
                JobStatus job = jobs.get(i);
                if (job.getJobId() == jobId) {
                    JobInfo job2 = job.getJob();
                    return job2;
                }
            }
            return null;
        }
    }

    /* access modifiers changed from: package-private */
    public void cancelJobsForUser(int userHandle) {
        synchronized (this.mLock) {
            List<JobStatus> jobsForUser = this.mJobs.getJobsByUser(userHandle);
            for (int i = 0; i < jobsForUser.size(); i++) {
                cancelJobImplLocked(jobsForUser.get(i), (JobStatus) null, "user removed");
            }
        }
    }

    private void cancelJobsForNonExistentUsers() {
        UserManagerInternal umi = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
        synchronized (this.mLock) {
            this.mJobs.removeJobsOfNonUsers(umi.getUserIds());
        }
    }

    /* access modifiers changed from: package-private */
    public void cancelJobsForPackageAndUid(String pkgName, int uid, String reason) {
        if (PackageManagerService.PLATFORM_PACKAGE_NAME.equals(pkgName)) {
            Slog.wtfStack(TAG, "Can't cancel all jobs for system package");
            return;
        }
        synchronized (this.mLock) {
            List<JobStatus> jobsForUid = this.mJobs.getJobsByUid(uid);
            for (int i = jobsForUid.size() - 1; i >= 0; i--) {
                JobStatus job = jobsForUid.get(i);
                if (job.getSourcePackageName().equals(pkgName)) {
                    cancelJobImplLocked(job, (JobStatus) null, reason);
                }
            }
        }
    }

    public boolean cancelJobsForUid(int uid, String reason) {
        if (uid == 1000) {
            Slog.wtfStack(TAG, "Can't cancel all jobs for system uid");
            return false;
        }
        boolean jobsCanceled = false;
        synchronized (this.mLock) {
            List<JobStatus> jobsForUid = this.mJobs.getJobsByUid(uid);
            for (int i = 0; i < jobsForUid.size(); i++) {
                cancelJobImplLocked(jobsForUid.get(i), (JobStatus) null, reason);
                jobsCanceled = true;
            }
        }
        return jobsCanceled;
    }

    public boolean cancelJob(int uid, int jobId, int callingUid) {
        boolean z;
        synchronized (this.mLock) {
            JobStatus toCancel = this.mJobs.getJobByUidAndJobId(uid, jobId);
            if (toCancel != null) {
                cancelJobImplLocked(toCancel, (JobStatus) null, "cancel() called by app, callingUid=" + callingUid + " uid=" + uid + " jobId=" + jobId);
            }
            z = toCancel != null;
        }
        return z;
    }

    /* access modifiers changed from: private */
    public void cancelJobImplLocked(JobStatus cancelled, JobStatus incomingJob, String reason) {
        if (DEBUG) {
            Slog.d(TAG, "CANCEL: " + cancelled.toShortString());
        }
        cancelled.unprepareLocked(ActivityManager.getService());
        stopTrackingJobLocked(cancelled, incomingJob, true);
        if (this.mPendingJobs.remove(cancelled)) {
            this.mJobPackageTracker.noteNonpending(cancelled);
        }
        stopJobOnServiceContextLocked(cancelled, 0, reason);
        if (incomingJob != null) {
            if (DEBUG) {
                Slog.i(TAG, "Tracking replacement job " + incomingJob.toShortString());
            }
            startTrackingJobLocked(incomingJob, cancelled);
        }
        reportActiveLocked();
    }

    /* access modifiers changed from: package-private */
    public void updateUidState(int uid, int procState) {
        synchronized (this.mLock) {
            if (procState == 2) {
                try {
                    this.mUidPriorityOverride.put(uid, 40);
                } catch (Throwable th) {
                    throw th;
                }
            } else if (procState <= 5) {
                this.mUidPriorityOverride.put(uid, 35);
            } else if (procState <= 6) {
                this.mUidPriorityOverride.put(uid, 30);
            } else {
                this.mUidPriorityOverride.delete(uid);
            }
        }
    }

    public void onDeviceIdleStateChanged(boolean deviceIdle) {
        synchronized (this.mLock) {
            if (DEBUG) {
                Slog.d(TAG, "Doze state changed: " + deviceIdle);
            }
            if (deviceIdle) {
                for (int i = 0; i < this.mActiveServices.size(); i++) {
                    JobServiceContext jsc = this.mActiveServices.get(i);
                    JobStatus executing = jsc.getRunningJobLocked();
                    if (executing != null && (executing.getFlags() & 1) == 0) {
                        jsc.cancelExecutingJobLocked(4, "cancelled due to doze");
                    }
                }
            } else if (this.mReadyToRock) {
                if (this.mLocalDeviceIdleController != null && !this.mReportedActive) {
                    this.mReportedActive = true;
                    this.mLocalDeviceIdleController.setJobsActive(true);
                }
                this.mHandler.obtainMessage(1).sendToTarget();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void reportActiveLocked() {
        boolean active = this.mPendingJobs.size() > 0;
        if (this.mPendingJobs.size() <= 0) {
            int i = 0;
            while (true) {
                if (i < this.mActiveServices.size()) {
                    JobStatus job = this.mActiveServices.get(i).getRunningJobLocked();
                    if (job != null && (job.getJob().getFlags() & 1) == 0 && !job.dozeWhitelisted && !job.uidActive) {
                        active = true;
                        break;
                    }
                    i++;
                } else {
                    break;
                }
            }
        }
        if (this.mReportedActive != active) {
            this.mReportedActive = active;
            DeviceIdleController.LocalService localService = this.mLocalDeviceIdleController;
            if (localService != null) {
                localService.setJobsActive(active);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void reportAppUsage(String packageName, int userId) {
    }

    public JobSchedulerService(Context context) {
        super(context);
        this.mUsageStats.addAppIdleStateChangeListener(this.mStandbyTracker);
        publishLocalService(JobSchedulerInternal.class, new LocalService());
        this.mJobs = JobStore.initAndGet(this);
        this.mControllers = new ArrayList();
        this.mControllers.add(new ConnectivityController(this));
        this.mControllers.add(new TimeController(this));
        this.mControllers.add(new IdleController(this));
        this.mBatteryController = new BatteryController(this);
        this.mControllers.add(this.mBatteryController);
        this.mStorageController = new StorageController(this);
        this.mControllers.add(this.mStorageController);
        this.mControllers.add(new BackgroundJobsController(this));
        this.mControllers.add(new ContentObserverController(this));
        this.mDeviceIdleJobsController = new DeviceIdleJobsController(this);
        this.mControllers.add(this.mDeviceIdleJobsController);
        this.mControllers.add(new QuotaController(this));
        this.mUltraPowerSaverController = new UltraPowerSaverController(this);
        this.mControllers.add(this.mUltraPowerSaverController);
        if (!this.mJobs.jobTimesInflatedValid()) {
            Slog.w(TAG, "!!! RTC not yet good; tracking time updates for job scheduling");
            context.registerReceiver(this.mTimeSetReceiver, new IntentFilter("android.intent.action.TIME_SET"));
        }
    }

    public /* synthetic */ void lambda$new$1$JobSchedulerService() {
        ArrayList<JobStatus> toRemove = new ArrayList<>();
        ArrayList<JobStatus> toAdd = new ArrayList<>();
        synchronized (this.mLock) {
            getJobStore().getRtcCorrectedJobsLocked(toAdd, toRemove);
            int N = toAdd.size();
            for (int i = 0; i < N; i++) {
                JobStatus oldJob = toRemove.get(i);
                JobStatus newJob = toAdd.get(i);
                if (DEBUG) {
                    Slog.v(TAG, "  replacing " + oldJob + " with " + newJob);
                }
                cancelJobImplLocked(oldJob, newJob, "deferred rtc calculation");
            }
        }
    }

    /* JADX WARNING: type inference failed for: r0v0, types: [com.android.server.job.JobSchedulerService$JobSchedulerStub, android.os.IBinder] */
    public void onStart() {
        publishBinderService("jobscheduler", this.mJobSchedulerStub);
    }

    public void onBootPhase(int phase) {
        if (500 == phase) {
            this.mConstantsObserver.start(getContext().getContentResolver());
            for (StateController controller : this.mControllers) {
                controller.onSystemServicesReady();
            }
            this.mAppStateTracker = (AppStateTracker) Preconditions.checkNotNull((AppStateTracker) LocalServices.getService(AppStateTracker.class));
            if (this.mConstants.USE_HEARTBEATS) {
                setNextHeartbeatAlarm();
            }
            IntentFilter filter = new IntentFilter();
            filter.addAction("android.intent.action.PACKAGE_REMOVED");
            filter.addAction("android.intent.action.PACKAGE_CHANGED");
            filter.addAction("android.intent.action.PACKAGE_RESTARTED");
            filter.addAction("android.intent.action.QUERY_PACKAGE_RESTART");
            filter.addDataScheme(com.android.server.pm.Settings.ATTR_PACKAGE);
            getContext().registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, filter, (String) null, (Handler) null);
            getContext().registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, new IntentFilter("android.intent.action.USER_REMOVED"), (String) null, (Handler) null);
            try {
                ActivityManager.getService().registerUidObserver(this.mUidObserver, 15, -1, (String) null);
            } catch (RemoteException e) {
            }
            this.mConcurrencyManager.onSystemReady();
            cancelJobsForNonExistentUsers();
            this.mThermalService = IThermalService.Stub.asInterface(ServiceManager.getService("thermalservice"));
            IThermalService iThermalService = this.mThermalService;
            if (iThermalService != null) {
                try {
                    iThermalService.registerThermalStatusListener(new ThermalStatusListener());
                } catch (RemoteException e2) {
                    Slog.e(TAG, "Failed to register thermal callback.", e2);
                }
            }
            this.mUltraPowerSaverController.registerObserver();
            this.mUltraPowerSaverController.setCallback(new UltraPowerSaverController.Callback() {
                public final void onUltraPowerSaverChanged(boolean z) {
                    JobSchedulerService.this.lambda$onBootPhase$2$JobSchedulerService(z);
                }
            });
        } else if (phase == 600) {
            synchronized (this.mLock) {
                this.mReadyToRock = true;
                this.mBatteryStats = IBatteryStats.Stub.asInterface(ServiceManager.getService("batterystats"));
                this.mLocalDeviceIdleController = (DeviceIdleController.LocalService) LocalServices.getService(DeviceIdleController.LocalService.class);
                for (int i = 0; i < 16; i++) {
                    this.mActiveServices.add(new JobServiceContext(this, this.mBatteryStats, this.mJobPackageTracker, MiuiFgThread.get().getLooper()));
                }
                this.mJobs.forEachJob(new Consumer() {
                    public final void accept(Object obj) {
                        JobSchedulerService.this.lambda$onBootPhase$3$JobSchedulerService((JobStatus) obj);
                    }
                });
            }
        } else if (phase == 1000) {
            this.mHandler.obtainMessage(1).sendToTarget();
        }
    }

    public /* synthetic */ void lambda$onBootPhase$3$JobSchedulerService(JobStatus job) {
        for (int controller = 0; controller < this.mControllers.size(); controller++) {
            this.mControllers.get(controller).maybeStartTrackingJobLocked(job, (JobStatus) null);
        }
    }

    private void startTrackingJobLocked(JobStatus jobStatus, JobStatus lastJob) {
        if (!jobStatus.isPreparedLocked()) {
            Slog.wtf(TAG, "Not yet prepared when started tracking: " + jobStatus);
        }
        jobStatus.enqueueTime = sElapsedRealtimeClock.millis();
        boolean update = this.mJobs.add(jobStatus);
        if (this.mReadyToRock) {
            for (int i = 0; i < this.mControllers.size(); i++) {
                StateController controller = this.mControllers.get(i);
                if (update) {
                    controller.maybeStopTrackingJobLocked(jobStatus, (JobStatus) null, true);
                }
                controller.maybeStartTrackingJobLocked(jobStatus, lastJob);
            }
        }
    }

    private boolean stopTrackingJobLocked(JobStatus jobStatus, JobStatus incomingJob, boolean writeBack) {
        jobStatus.stopTrackingJobLocked(ActivityManager.getService(), incomingJob);
        boolean removed = this.mJobs.remove(jobStatus, writeBack);
        if (removed && this.mReadyToRock) {
            for (int i = 0; i < this.mControllers.size(); i++) {
                this.mControllers.get(i).maybeStopTrackingJobLocked(jobStatus, incomingJob, false);
            }
        }
        return removed;
    }

    private boolean stopJobOnServiceContextLocked(JobStatus job, int reason, String debugReason) {
        int i = 0;
        while (i < this.mActiveServices.size()) {
            JobServiceContext jsc = this.mActiveServices.get(i);
            JobStatus executing = jsc.getRunningJobLocked();
            if (executing == null || !executing.matches(job.getUid(), job.getJobId())) {
                i++;
            } else {
                jsc.cancelExecutingJobLocked(reason, debugReason);
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    public boolean isCurrentlyActiveLocked(JobStatus job) {
        for (int i = 0; i < this.mActiveServices.size(); i++) {
            JobStatus running = this.mActiveServices.get(i).getRunningJobLocked();
            if (running != null && running.matches(job.getUid(), job.getJobId())) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public void noteJobsPending(List<JobStatus> jobs) {
        for (int i = jobs.size() - 1; i >= 0; i--) {
            this.mJobPackageTracker.notePending(jobs.get(i));
        }
    }

    /* access modifiers changed from: package-private */
    public void noteJobsNonpending(List<JobStatus> jobs) {
        for (int i = jobs.size() - 1; i >= 0; i--) {
            this.mJobPackageTracker.noteNonpending(jobs.get(i));
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public JobStatus getRescheduleJobForFailureLocked(JobStatus failureToReschedule) {
        long backoff;
        JobStatus jobStatus = failureToReschedule;
        long elapsedNowMillis = sElapsedRealtimeClock.millis();
        JobInfo job = failureToReschedule.getJob();
        long initialBackoffMillis = job.getInitialBackoffMillis();
        int backoffAttempts = failureToReschedule.getNumFailures() + 1;
        if (failureToReschedule.hasWorkLocked()) {
            if (backoffAttempts > this.mConstants.MAX_WORK_RESCHEDULE_COUNT) {
                Slog.w(TAG, "Not rescheduling " + jobStatus + ": attempt #" + backoffAttempts + " > work limit " + this.mConstants.MAX_STANDARD_RESCHEDULE_COUNT);
                return null;
            }
        } else if (backoffAttempts > this.mConstants.MAX_STANDARD_RESCHEDULE_COUNT) {
            Slog.w(TAG, "Not rescheduling " + jobStatus + ": attempt #" + backoffAttempts + " > std limit " + this.mConstants.MAX_STANDARD_RESCHEDULE_COUNT);
            return null;
        }
        int backoffPolicy = job.getBackoffPolicy();
        if (backoffPolicy != 0) {
            if (backoffPolicy != 1 && DEBUG) {
                Slog.v(TAG, "Unrecognised back-off policy, defaulting to exponential.");
            }
            long backoff2 = initialBackoffMillis;
            if (backoff2 < this.mConstants.MIN_EXP_BACKOFF_TIME) {
                backoff2 = this.mConstants.MIN_EXP_BACKOFF_TIME;
            }
            backoff = (long) Math.scalb((float) backoff2, backoffAttempts - 1);
        } else {
            long backoff3 = initialBackoffMillis;
            if (backoff3 < this.mConstants.MIN_LINEAR_BACKOFF_TIME) {
                backoff3 = this.mConstants.MIN_LINEAR_BACKOFF_TIME;
            }
            backoff = backoff3 * ((long) backoffAttempts);
        }
        JobStatus jobStatus2 = failureToReschedule;
        int i = backoffAttempts;
        int i2 = backoffAttempts;
        JobStatus newJob = new JobStatus(jobStatus2, getCurrentHeartbeat(), elapsedNowMillis + Math.min(backoff, 18000000), JobStatus.NO_LATEST_RUNTIME, i, failureToReschedule.getLastSuccessfulRunTime(), sSystemClock.millis());
        if (job.isPeriodic()) {
            newJob.setOriginalLatestRunTimeElapsed(failureToReschedule.getOriginalLatestRunTimeElapsed());
        }
        for (int ic = 0; ic < this.mControllers.size(); ic++) {
            this.mControllers.get(ic).rescheduleForFailureLocked(newJob, jobStatus);
        }
        return newJob;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public JobStatus getRescheduleJobForPeriodic(JobStatus periodicToReschedule) {
        long newLatestRuntimeElapsed;
        long elapsedNow = sElapsedRealtimeClock.millis();
        long period = Math.max(JobInfo.getMinPeriodMillis(), Math.min(31536000000L, periodicToReschedule.getJob().getIntervalMillis()));
        long flex = Math.max(JobInfo.getMinFlexMillis(), Math.min(period, periodicToReschedule.getJob().getFlexMillis()));
        long olrte = periodicToReschedule.getOriginalLatestRunTimeElapsed();
        if (olrte < 0 || olrte == JobStatus.NO_LATEST_RUNTIME) {
            Slog.wtf(TAG, "Invalid periodic job original latest run time: " + olrte);
            olrte = elapsedNow;
        }
        long latestRunTimeElapsed = olrte;
        long diffMs = Math.abs(elapsedNow - latestRunTimeElapsed);
        long rescheduleBuffer = 0;
        if (elapsedNow > latestRunTimeElapsed) {
            if (DEBUG) {
                Slog.i(TAG, "Periodic job ran after its intended window.");
            }
            long numSkippedWindows = (diffMs / period) + 1;
            if (period != flex) {
                long j = olrte;
                if (diffMs > Math.min(1800000, (period - flex) / 2)) {
                    if (DEBUG) {
                        Slog.d(TAG, "Custom flex job ran too close to next window.");
                    }
                    numSkippedWindows++;
                }
            }
            newLatestRuntimeElapsed = (period * numSkippedWindows) + latestRunTimeElapsed;
        } else {
            long olrte2 = latestRunTimeElapsed + period;
            if (diffMs >= 1800000 || diffMs >= period / 6) {
                newLatestRuntimeElapsed = olrte2;
            } else {
                rescheduleBuffer = Math.min(1800000, (period / 6) - diffMs);
                newLatestRuntimeElapsed = olrte2;
            }
        }
        if (newLatestRuntimeElapsed < elapsedNow) {
            Slog.wtf(TAG, "Rescheduling calculated latest runtime in the past: " + newLatestRuntimeElapsed);
            return new JobStatus(periodicToReschedule, getCurrentHeartbeat(), (elapsedNow + period) - flex, elapsedNow + period, 0, sSystemClock.millis(), periodicToReschedule.getLastFailedRunTime());
        }
        long newEarliestRunTimeElapsed = newLatestRuntimeElapsed - Math.min(flex, period - rescheduleBuffer);
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            long j2 = elapsedNow;
            sb.append("Rescheduling executed periodic. New execution window [");
            long j3 = period;
            sb.append(newEarliestRunTimeElapsed / 1000);
            sb.append(", ");
            sb.append(newLatestRuntimeElapsed / 1000);
            sb.append("]s");
            Slog.v(TAG, sb.toString());
        } else {
            long j4 = period;
        }
        return new JobStatus(periodicToReschedule, getCurrentHeartbeat(), newEarliestRunTimeElapsed, newLatestRuntimeElapsed, 0, sSystemClock.millis(), periodicToReschedule.getLastFailedRunTime());
    }

    /* access modifiers changed from: package-private */
    public long heartbeatWhenJobsLastRun(String packageName, int userId) {
        long heartbeat = (long) (-this.mConstants.STANDBY_BEATS[3]);
        boolean cacheHit = false;
        synchronized (this.mLock) {
            HashMap<String, Long> jobPackages = this.mLastJobHeartbeats.get(userId);
            if (jobPackages != null) {
                long cachedValue = jobPackages.getOrDefault(packageName, Long.valueOf(JobStatus.NO_LATEST_RUNTIME)).longValue();
                if (cachedValue < JobStatus.NO_LATEST_RUNTIME) {
                    cacheHit = true;
                    heartbeat = cachedValue;
                }
            }
            if (!cacheHit) {
                long timeSinceJob = this.mUsageStats.getTimeSinceLastJobRun(packageName, userId);
                if (timeSinceJob < JobStatus.NO_LATEST_RUNTIME) {
                    heartbeat = this.mHeartbeat - (timeSinceJob / this.mConstants.STANDBY_HEARTBEAT_TIME);
                }
                setLastJobHeartbeatLocked(packageName, userId, heartbeat);
            }
        }
        if (DEBUG_STANDBY) {
            Slog.v(TAG, "Last job heartbeat " + heartbeat + " for " + packageName + SliceClientPermissions.SliceAuthority.DELIMITER + userId);
        }
        return heartbeat;
    }

    /* access modifiers changed from: package-private */
    public long heartbeatWhenJobsLastRun(JobStatus job) {
        return heartbeatWhenJobsLastRun(job.getSourcePackageName(), job.getSourceUserId());
    }

    /* access modifiers changed from: package-private */
    public void setLastJobHeartbeatLocked(String packageName, int userId, long heartbeat) {
        HashMap<String, Long> jobPackages = this.mLastJobHeartbeats.get(userId);
        if (jobPackages == null) {
            jobPackages = new HashMap<>();
            this.mLastJobHeartbeats.put(userId, jobPackages);
        }
        jobPackages.put(packageName, Long.valueOf(heartbeat));
    }

    public void onJobCompletedLocked(JobStatus jobStatus, boolean needsReschedule) {
        if (DEBUG) {
            Slog.d(TAG, "Completed " + jobStatus + ", reschedule=" + needsReschedule);
        }
        JobStatus rescheduledJob = needsReschedule ? getRescheduleJobForFailureLocked(jobStatus) : null;
        if (!stopTrackingJobLocked(jobStatus, rescheduledJob, !jobStatus.getJob().isPeriodic())) {
            if (DEBUG) {
                Slog.d(TAG, "Could not find job to remove. Was job removed while executing?");
            }
            this.mHandler.obtainMessage(3).sendToTarget();
            return;
        }
        if (rescheduledJob != null) {
            try {
                rescheduledJob.prepareLocked(ActivityManager.getService());
            } catch (SecurityException e) {
                Slog.w(TAG, "Unable to regrant job permissions for " + rescheduledJob);
            }
            startTrackingJobLocked(rescheduledJob, jobStatus);
        } else if (jobStatus.getJob().isPeriodic()) {
            JobStatus rescheduledPeriodic = getRescheduleJobForPeriodic(jobStatus);
            try {
                rescheduledPeriodic.prepareLocked(ActivityManager.getService());
            } catch (SecurityException e2) {
                Slog.w(TAG, "Unable to regrant job permissions for " + rescheduledPeriodic);
            }
            startTrackingJobLocked(rescheduledPeriodic, jobStatus);
        }
        jobStatus.unprepareLocked(ActivityManager.getService());
        reportActiveLocked();
        this.mHandler.obtainMessage(3).sendToTarget();
    }

    public void onControllerStateChanged() {
        this.mHandler.obtainMessage(1).sendToTarget();
    }

    public void onRunJobNow(JobStatus jobStatus) {
        this.mHandler.obtainMessage(0, jobStatus).sendToTarget();
    }

    private final class JobHandler extends Handler {
        public JobHandler(Looper looper) {
            super(looper);
        }

        /* Debug info: failed to restart local var, previous not found, register: 6 */
        public void handleMessage(Message message) {
            synchronized (JobSchedulerService.this.mLock) {
                if (JobSchedulerService.this.mReadyToRock) {
                    boolean disabled = true;
                    switch (message.what) {
                        case 0:
                            JobStatus runNow = (JobStatus) message.obj;
                            if (runNow != null && JobSchedulerService.this.isReadyToBeExecutedLocked(runNow)) {
                                JobSchedulerService.this.mJobPackageTracker.notePending(runNow);
                                JobSchedulerService.addOrderedItem(JobSchedulerService.this.mPendingJobs, runNow, JobSchedulerService.mEnqueueTimeComparator);
                                break;
                            } else {
                                JobSchedulerService.this.queueReadyJobsForExecutionLocked();
                                break;
                            }
                        case 1:
                            if (JobSchedulerService.DEBUG) {
                                Slog.d(JobSchedulerService.TAG, "MSG_CHECK_JOB");
                            }
                            removeMessages(1);
                            if (!JobSchedulerService.this.mReportedActive) {
                                JobSchedulerService.this.maybeQueueReadyJobsForExecutionLocked();
                                break;
                            } else {
                                JobSchedulerService.this.queueReadyJobsForExecutionLocked();
                                break;
                            }
                        case 2:
                            JobSchedulerService.this.cancelJobImplLocked((JobStatus) message.obj, (JobStatus) null, "app no longer allowed to run");
                            break;
                        case 3:
                            if (JobSchedulerService.DEBUG != 0) {
                                Slog.d(JobSchedulerService.TAG, "MSG_CHECK_JOB_GREEDY");
                            }
                            JobSchedulerService.this.queueReadyJobsForExecutionLocked();
                            break;
                        case 4:
                            JobSchedulerService.this.updateUidState(message.arg1, message.arg2);
                            break;
                        case 5:
                            int uid = message.arg1;
                            if (message.arg2 == 0) {
                                disabled = false;
                            }
                            JobSchedulerService.this.updateUidState(uid, 20);
                            if (disabled) {
                                JobSchedulerService.this.cancelJobsForUid(uid, "uid gone");
                            }
                            synchronized (JobSchedulerService.this.mLock) {
                                JobSchedulerService.this.mDeviceIdleJobsController.setUidActiveLocked(uid, false);
                            }
                            break;
                        case 6:
                            int uid2 = message.arg1;
                            synchronized (JobSchedulerService.this.mLock) {
                                JobSchedulerService.this.mDeviceIdleJobsController.setUidActiveLocked(uid2, true);
                            }
                            break;
                        case 7:
                            int uid3 = message.arg1;
                            if (message.arg2 == 0) {
                                disabled = false;
                            }
                            if (disabled) {
                                JobSchedulerService.this.cancelJobsForUid(uid3, "app uid idle");
                            }
                            synchronized (JobSchedulerService.this.mLock) {
                                JobSchedulerService.this.mDeviceIdleJobsController.setUidActiveLocked(uid3, false);
                            }
                            break;
                    }
                    JobSchedulerService.this.maybeRunPendingJobsLocked();
                }
            }
        }
    }

    private boolean isJobThermalConstrainedLocked(JobStatus job) {
        return this.mThermalConstraint && job.hasConnectivityConstraint() && evaluateJobPriorityLocked(job) < 30;
    }

    private void stopNonReadyActiveJobsLocked() {
        for (int i = 0; i < this.mActiveServices.size(); i++) {
            JobServiceContext serviceContext = this.mActiveServices.get(i);
            JobStatus running = serviceContext.getRunningJobLocked();
            if (running != null) {
                if (!running.isReady()) {
                    serviceContext.cancelExecutingJobLocked(1, "cancelled due to unsatisfied constraints");
                } else if (isJobThermalConstrainedLocked(running)) {
                    serviceContext.cancelExecutingJobLocked(5, "cancelled due to thermal condition");
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void queueReadyJobsForExecutionLocked() {
        if (DEBUG) {
            Slog.d(TAG, "queuing all ready jobs for execution:");
        }
        noteJobsNonpending(this.mPendingJobs);
        this.mPendingJobs.clear();
        stopNonReadyActiveJobsLocked();
        this.mJobs.forEachJob(this.mReadyQueueFunctor);
        this.mReadyQueueFunctor.postProcess();
        if (DEBUG) {
            int queuedJobs = this.mPendingJobs.size();
            if (queuedJobs == 0) {
                Slog.d(TAG, "No jobs pending.");
                return;
            }
            Slog.d(TAG, queuedJobs + " jobs queued.");
        }
    }

    final class ReadyJobQueueFunctor implements Consumer<JobStatus> {
        ArrayList<JobStatus> newReadyJobs;

        ReadyJobQueueFunctor() {
        }

        public void accept(JobStatus job) {
            if (JobSchedulerService.this.isReadyToBeExecutedLocked(job)) {
                if (JobSchedulerService.DEBUG) {
                    Slog.d(JobSchedulerService.TAG, "    queued " + job.toShortString());
                }
                if (this.newReadyJobs == null) {
                    this.newReadyJobs = new ArrayList<>();
                }
                this.newReadyJobs.add(job);
                return;
            }
            JobSchedulerService.this.evaluateControllerStatesLocked(job);
        }

        public void postProcess() {
            ArrayList<JobStatus> arrayList = this.newReadyJobs;
            if (arrayList != null) {
                JobSchedulerService.this.noteJobsPending(arrayList);
                JobSchedulerService.this.mPendingJobs.addAll(this.newReadyJobs);
                if (JobSchedulerService.this.mPendingJobs.size() > 1) {
                    JobSchedulerService.this.mPendingJobs.sort(JobSchedulerService.mEnqueueTimeComparator);
                }
            }
            this.newReadyJobs = null;
        }
    }

    final class MaybeReadyJobQueueFunctor implements Consumer<JobStatus> {
        int backoffCount;
        int batteryNotLowCount;
        int chargingCount;
        int connectivityCount;
        int contentCount;
        int idleCount;
        List<JobStatus> runnableJobs;
        int storageNotLowCount;

        public MaybeReadyJobQueueFunctor() {
            reset();
        }

        public void accept(JobStatus job) {
            if (JobSchedulerService.this.isReadyToBeExecutedLocked(job)) {
                try {
                    if (ActivityManager.getService().isAppStartModeDisabled(job.getUid(), job.getJob().getService().getPackageName())) {
                        Slog.w(JobSchedulerService.TAG, "Aborting job " + job.getUid() + ":" + job.getJob().toString() + " -- package not allowed to start");
                        JobSchedulerService.this.mHandler.obtainMessage(2, job).sendToTarget();
                        return;
                    }
                } catch (RemoteException e) {
                }
                if (job.getNumFailures() > 0) {
                    this.backoffCount++;
                }
                if (job.hasIdleConstraint()) {
                    this.idleCount++;
                }
                if (job.hasConnectivityConstraint()) {
                    this.connectivityCount++;
                }
                if (job.hasChargingConstraint()) {
                    this.chargingCount++;
                }
                if (job.hasBatteryNotLowConstraint()) {
                    this.batteryNotLowCount++;
                }
                if (job.hasStorageNotLowConstraint()) {
                    this.storageNotLowCount++;
                }
                if (job.hasContentTriggerConstraint()) {
                    this.contentCount++;
                }
                if (this.runnableJobs == null) {
                    this.runnableJobs = new ArrayList();
                }
                this.runnableJobs.add(job);
                return;
            }
            JobSchedulerService.this.evaluateControllerStatesLocked(job);
        }

        public void postProcess() {
            List<JobStatus> list;
            if (this.backoffCount > 0 || this.idleCount >= JobSchedulerService.this.mConstants.MIN_IDLE_COUNT || this.connectivityCount >= JobSchedulerService.this.mConstants.MIN_CONNECTIVITY_COUNT || this.chargingCount >= JobSchedulerService.this.mConstants.MIN_CHARGING_COUNT || this.batteryNotLowCount >= JobSchedulerService.this.mConstants.MIN_BATTERY_NOT_LOW_COUNT || this.storageNotLowCount >= JobSchedulerService.this.mConstants.MIN_STORAGE_NOT_LOW_COUNT || this.contentCount >= JobSchedulerService.this.mConstants.MIN_CONTENT_COUNT || ((list = this.runnableJobs) != null && list.size() >= JobSchedulerService.this.mConstants.MIN_READY_JOBS_COUNT)) {
                if (JobSchedulerService.DEBUG) {
                    Slog.d(JobSchedulerService.TAG, "maybeQueueReadyJobsForExecutionLocked: Running jobs.");
                }
                JobSchedulerService.this.noteJobsPending(this.runnableJobs);
                JobSchedulerService.this.mPendingJobs.addAll(this.runnableJobs);
                if (JobSchedulerService.this.mPendingJobs.size() > 1) {
                    JobSchedulerService.this.mPendingJobs.sort(JobSchedulerService.mEnqueueTimeComparator);
                }
            } else if (JobSchedulerService.DEBUG) {
                Slog.d(JobSchedulerService.TAG, "maybeQueueReadyJobsForExecutionLocked: Not running anything.");
            }
            reset();
        }

        private void reset() {
            this.chargingCount = 0;
            this.idleCount = 0;
            this.backoffCount = 0;
            this.connectivityCount = 0;
            this.batteryNotLowCount = 0;
            this.storageNotLowCount = 0;
            this.contentCount = 0;
            this.runnableJobs = null;
        }
    }

    /* access modifiers changed from: private */
    public void maybeQueueReadyJobsForExecutionLocked() {
        if (DEBUG) {
            Slog.d(TAG, "Maybe queuing ready jobs...");
        }
        noteJobsNonpending(this.mPendingJobs);
        this.mPendingJobs.clear();
        stopNonReadyActiveJobsLocked();
        this.mJobs.forEachJob(this.mMaybeQueueFunctor);
        this.mMaybeQueueFunctor.postProcess();
    }

    class HeartbeatAlarmListener implements AlarmManager.OnAlarmListener {
        HeartbeatAlarmListener() {
        }

        public void onAlarm() {
            synchronized (JobSchedulerService.this.mLock) {
                long beatsElapsed = (JobSchedulerService.sElapsedRealtimeClock.millis() - JobSchedulerService.this.mLastHeartbeatTime) / JobSchedulerService.this.mConstants.STANDBY_HEARTBEAT_TIME;
                if (beatsElapsed > 0) {
                    JobSchedulerService.this.mLastHeartbeatTime += JobSchedulerService.this.mConstants.STANDBY_HEARTBEAT_TIME * beatsElapsed;
                    JobSchedulerService.this.advanceHeartbeatLocked(beatsElapsed);
                }
            }
            JobSchedulerService.this.setNextHeartbeatAlarm();
        }
    }

    /* access modifiers changed from: package-private */
    public void advanceHeartbeatLocked(long beatsElapsed) {
        if (this.mConstants.USE_HEARTBEATS) {
            this.mHeartbeat += beatsElapsed;
            if (DEBUG_STANDBY) {
                Slog.v(TAG, "Advancing standby heartbeat by " + beatsElapsed + " to " + this.mHeartbeat);
            }
            boolean didAdvanceBucket = false;
            int i = 1;
            while (true) {
                long[] jArr = this.mNextBucketHeartbeat;
                if (i >= jArr.length - 1) {
                    break;
                }
                if (this.mHeartbeat >= jArr[i]) {
                    didAdvanceBucket = true;
                }
                while (true) {
                    long j = this.mHeartbeat;
                    long[] jArr2 = this.mNextBucketHeartbeat;
                    if (j <= jArr2[i]) {
                        break;
                    }
                    jArr2[i] = jArr2[i] + ((long) this.mConstants.STANDBY_BEATS[i]);
                }
                if (DEBUG_STANDBY) {
                    Slog.v(TAG, "   Bucket " + i + " next heartbeat " + this.mNextBucketHeartbeat[i]);
                }
                i++;
            }
            if (didAdvanceBucket) {
                if (DEBUG_STANDBY) {
                    Slog.v(TAG, "Hit bucket boundary; reevaluating job runnability");
                }
                this.mHandler.obtainMessage(1).sendToTarget();
            }
        }
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x001f, code lost:
        if (DEBUG_STANDBY == false) goto L_0x0045;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0021, code lost:
        android.util.Slog.i(TAG, "Setting heartbeat alarm for " + r14 + " = " + android.util.TimeUtils.formatDuration(r14 - r5));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0045, code lost:
        r18 = r14;
        ((android.app.AlarmManager) getContext().getSystemService("alarm")).setExact(3, r14, HEARTBEAT_TAG, r1.mHeartbeatAlarm, r1.mHandler);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0066, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0012, code lost:
        r5 = sElapsedRealtimeClock.millis();
        r14 = ((r5 + r3) / r3) * r3;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void setNextHeartbeatAlarm() {
        /*
            r20 = this;
            r1 = r20
            java.lang.Object r2 = r1.mLock
            monitor-enter(r2)
            com.android.server.job.JobSchedulerService$Constants r0 = r1.mConstants     // Catch:{ all -> 0x0067 }
            boolean r0 = r0.USE_HEARTBEATS     // Catch:{ all -> 0x0067 }
            if (r0 != 0) goto L_0x000d
            monitor-exit(r2)     // Catch:{ all -> 0x0067 }
            return
        L_0x000d:
            com.android.server.job.JobSchedulerService$Constants r0 = r1.mConstants     // Catch:{ all -> 0x0067 }
            long r3 = r0.STANDBY_HEARTBEAT_TIME     // Catch:{ all -> 0x0067 }
            monitor-exit(r2)     // Catch:{ all -> 0x0067 }
            java.time.Clock r0 = sElapsedRealtimeClock
            long r5 = r0.millis()
            long r7 = r5 + r3
            long r7 = r7 / r3
            long r14 = r7 * r3
            boolean r0 = DEBUG_STANDBY
            if (r0 == 0) goto L_0x0045
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "Setting heartbeat alarm for "
            r0.append(r2)
            r0.append(r14)
            java.lang.String r2 = " = "
            r0.append(r2)
            long r9 = r14 - r5
            java.lang.String r2 = android.util.TimeUtils.formatDuration(r9)
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            java.lang.String r2 = "JobScheduler"
            android.util.Slog.i(r2, r0)
        L_0x0045:
            android.content.Context r0 = r20.getContext()
            java.lang.String r2 = "alarm"
            java.lang.Object r0 = r0.getSystemService(r2)
            android.app.AlarmManager r0 = (android.app.AlarmManager) r0
            r10 = 3
            com.android.server.job.JobSchedulerService$HeartbeatAlarmListener r2 = r1.mHeartbeatAlarm
            com.android.server.job.JobSchedulerService$JobHandler r13 = r1.mHandler
            java.lang.String r16 = "*job.heartbeat*"
            r9 = r0
            r11 = r14
            r17 = r13
            r13 = r16
            r18 = r14
            r14 = r2
            r15 = r17
            r9.setExact(r10, r11, r13, r14, r15)
            return
        L_0x0067:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0067 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.job.JobSchedulerService.setNextHeartbeatAlarm():void");
    }

    private boolean areUsersStartedLocked(JobStatus job) {
        boolean sourceStarted = ArrayUtils.contains(this.mStartedUsers, job.getSourceUserId());
        if (job.getUserId() == job.getSourceUserId()) {
            return sourceStarted;
        }
        return sourceStarted && ArrayUtils.contains(this.mStartedUsers, job.getUserId());
    }

    /* access modifiers changed from: private */
    public boolean isReadyToBeExecutedLocked(JobStatus job) {
        if (this.mBeingUltraPowerSavingMode) {
            if (DEBUG) {
                Slog.v(TAG, "isReadyToBeExecutedLocked: in ultra power saving mode, return false.");
            }
            return false;
        }
        boolean jobReady = job.isReady();
        if (DEBUG) {
            Slog.v(TAG, "isReadyToBeExecutedLocked: " + job.toShortString() + " ready=" + jobReady);
        }
        if (!jobReady) {
            if (job.getSourcePackageName().equals("android.jobscheduler.cts.jobtestapp")) {
                Slog.v(TAG, "    NOT READY: " + job);
            }
            return false;
        }
        boolean jobExists = this.mJobs.containsJob(job);
        boolean userStarted = areUsersStartedLocked(job);
        if (DEBUG) {
            Slog.v(TAG, "isReadyToBeExecutedLocked: " + job.toShortString() + " exists=" + jobExists + " userStarted=" + userStarted);
        }
        if (!jobExists || !userStarted || isJobThermalConstrainedLocked(job)) {
            return false;
        }
        boolean jobPending = this.mPendingJobs.contains(job);
        boolean jobActive = isCurrentlyActiveLocked(job);
        if (DEBUG) {
            Slog.v(TAG, "isReadyToBeExecutedLocked: " + job.toShortString() + " pending=" + jobPending + " active=" + jobActive);
        }
        if (jobPending || jobActive) {
            return false;
        }
        if (this.mConstants.USE_HEARTBEATS) {
            if (DEBUG_STANDBY) {
                Slog.v(TAG, "isReadyToBeExecutedLocked: " + job.toShortString() + " parole=" + this.mInParole + " active=" + job.uidActive + " exempt=" + job.getJob().isExemptedFromAppStandby());
            }
            if (!this.mInParole && !job.uidActive && !job.getJob().isExemptedFromAppStandby()) {
                int bucket = job.getStandbyBucket();
                if (DEBUG_STANDBY) {
                    Slog.v(TAG, "  bucket=" + bucket + " heartbeat=" + this.mHeartbeat + " next=" + this.mNextBucketHeartbeat[bucket]);
                }
                if (this.mHeartbeat < this.mNextBucketHeartbeat[bucket]) {
                    long appLastRan = heartbeatWhenJobsLastRun(job);
                    if (bucket < this.mConstants.STANDBY_BEATS.length) {
                        long j = this.mHeartbeat;
                        if (j <= appLastRan || j >= ((long) this.mConstants.STANDBY_BEATS[bucket]) + appLastRan) {
                            if (DEBUG_STANDBY) {
                                Slog.v(TAG, "Bucket deferred job aged into runnability at " + this.mHeartbeat + " : " + job);
                            }
                        }
                    }
                    if (job.getWhenStandbyDeferred() == 0) {
                        if (DEBUG_STANDBY) {
                            Slog.v(TAG, "Bucket deferral: " + this.mHeartbeat + " < " + (((long) this.mConstants.STANDBY_BEATS[bucket]) + appLastRan) + " for " + job);
                        }
                        job.setWhenStandbyDeferred(sElapsedRealtimeClock.millis());
                    }
                    return false;
                }
            }
        }
        return isComponentUsable(job);
    }

    private boolean isComponentUsable(JobStatus job) {
        try {
            ServiceInfo service = AppGlobals.getPackageManager().getServiceInfo(job.getServiceComponent(), 268435456, job.getUserId());
            if (service != null) {
                boolean appIsBad = this.mActivityManagerInternal.isAppBad(service.applicationInfo);
                if (DEBUG && appIsBad) {
                    Slog.i(TAG, "App is bad for " + job.toShortString() + " so not runnable");
                }
                return !appIsBad;
            } else if (!DEBUG) {
                return false;
            } else {
                Slog.v(TAG, "isComponentUsable: " + job.toShortString() + " component not present");
                return false;
            }
        } catch (RemoteException e) {
            throw e.rethrowAsRuntimeException();
        }
    }

    /* access modifiers changed from: private */
    public void evaluateControllerStatesLocked(JobStatus job) {
        for (int c = this.mControllers.size() - 1; c >= 0; c--) {
            this.mControllers.get(c).evaluateStateLocked(job);
        }
    }

    public boolean areComponentsInPlaceLocked(JobStatus job) {
        boolean jobExists = this.mJobs.containsJob(job);
        boolean userStarted = areUsersStartedLocked(job);
        if (DEBUG) {
            Slog.v(TAG, "areComponentsInPlaceLocked: " + job.toShortString() + " exists=" + jobExists + " userStarted=" + userStarted);
        }
        if (!jobExists || !userStarted || isJobThermalConstrainedLocked(job)) {
            return false;
        }
        return isComponentUsable(job);
    }

    /* access modifiers changed from: package-private */
    public void maybeRunPendingJobsLocked() {
        if (DEBUG) {
            Slog.d(TAG, "pending queue: " + this.mPendingJobs.size() + " jobs.");
        }
        this.mConcurrencyManager.assignJobsToContextsLocked();
        reportActiveLocked();
    }

    private int adjustJobPriority(int curPriority, JobStatus job) {
        if (curPriority >= 40) {
            return curPriority;
        }
        float factor = this.mJobPackageTracker.getLoadFactor(job);
        if (factor >= this.mConstants.HEAVY_USE_FACTOR) {
            return curPriority - 80;
        }
        if (factor >= this.mConstants.MODERATE_USE_FACTOR) {
            return curPriority - 40;
        }
        return curPriority;
    }

    /* access modifiers changed from: package-private */
    public int evaluateJobPriorityLocked(JobStatus job) {
        int priority = job.getPriority();
        if (priority >= 30) {
            return adjustJobPriority(priority, job);
        }
        int override = this.mUidPriorityOverride.get(job.getSourceUid(), 0);
        if (override != 0) {
            return adjustJobPriority(override, job);
        }
        return adjustJobPriority(priority, job);
    }

    final class LocalService implements JobSchedulerInternal {
        LocalService() {
        }

        public long currentHeartbeat() {
            return JobSchedulerService.this.getCurrentHeartbeat();
        }

        public long nextHeartbeatForBucket(int bucket) {
            long j;
            synchronized (JobSchedulerService.this.mLock) {
                j = JobSchedulerService.this.mNextBucketHeartbeat[bucket];
            }
            return j;
        }

        public long baseHeartbeatForApp(String packageName, int userId, int appStandbyBucket) {
            if (appStandbyBucket != 0 && appStandbyBucket < JobSchedulerService.this.mConstants.STANDBY_BEATS.length) {
                long baseHeartbeat = JobSchedulerService.this.heartbeatWhenJobsLastRun(packageName, userId);
                if (JobSchedulerService.DEBUG_STANDBY) {
                    Slog.v(JobSchedulerService.TAG, "Base heartbeat " + baseHeartbeat + " for new job in " + packageName + SliceClientPermissions.SliceAuthority.DELIMITER + userId);
                }
                return baseHeartbeat;
            } else if (!JobSchedulerService.DEBUG_STANDBY) {
                return 0;
            } else {
                Slog.v(JobSchedulerService.TAG, "Base heartbeat forced ZERO for new job in " + packageName + SliceClientPermissions.SliceAuthority.DELIMITER + userId);
                return 0;
            }
        }

        public void noteJobStart(String packageName, int userId) {
            synchronized (JobSchedulerService.this.mLock) {
                JobSchedulerService.this.setLastJobHeartbeatLocked(packageName, userId, JobSchedulerService.this.mHeartbeat);
            }
        }

        public List<JobInfo> getSystemScheduledPendingJobs() {
            List<JobInfo> pendingJobs;
            synchronized (JobSchedulerService.this.mLock) {
                pendingJobs = new ArrayList<>();
                JobSchedulerService.this.mJobs.forEachJob(1000, (Consumer<JobStatus>) new Consumer(pendingJobs) {
                    private final /* synthetic */ List f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void accept(Object obj) {
                        JobSchedulerService.LocalService.this.lambda$getSystemScheduledPendingJobs$0$JobSchedulerService$LocalService(this.f$1, (JobStatus) obj);
                    }
                });
            }
            return pendingJobs;
        }

        public /* synthetic */ void lambda$getSystemScheduledPendingJobs$0$JobSchedulerService$LocalService(List pendingJobs, JobStatus job) {
            if (job.getJob().isPeriodic() || !JobSchedulerService.this.isCurrentlyActiveLocked(job)) {
                pendingJobs.add(job.getJob());
            }
        }

        public void cancelJobsForUid(int uid, String reason) {
            JobSchedulerService.this.cancelJobsForUid(uid, reason);
        }

        public void addBackingUpUid(int uid) {
            synchronized (JobSchedulerService.this.mLock) {
                JobSchedulerService.this.mBackingUpUids.put(uid, uid);
            }
        }

        public void removeBackingUpUid(int uid) {
            synchronized (JobSchedulerService.this.mLock) {
                JobSchedulerService.this.mBackingUpUids.delete(uid);
                if (JobSchedulerService.this.mJobs.countJobsForUid(uid) > 0) {
                    JobSchedulerService.this.mHandler.obtainMessage(1).sendToTarget();
                }
            }
        }

        public void clearAllBackingUpUids() {
            synchronized (JobSchedulerService.this.mLock) {
                if (JobSchedulerService.this.mBackingUpUids.size() > 0) {
                    JobSchedulerService.this.mBackingUpUids.clear();
                    JobSchedulerService.this.mHandler.obtainMessage(1).sendToTarget();
                }
            }
        }

        public void cancelJob(int uid, int jobId) {
            JobSchedulerService.this.cancelJob(uid, jobId, uid);
        }

        public void reportAppUsage(String packageName, int userId) {
            JobSchedulerService.this.reportAppUsage(packageName, userId);
        }

        public JobSchedulerInternal.JobStorePersistStats getPersistStats() {
            JobSchedulerInternal.JobStorePersistStats jobStorePersistStats;
            synchronized (JobSchedulerService.this.mLock) {
                jobStorePersistStats = new JobSchedulerInternal.JobStorePersistStats(JobSchedulerService.this.mJobs.getPersistStats());
            }
            return jobStorePersistStats;
        }
    }

    final class StandbyTracker extends UsageStatsManagerInternal.AppIdleStateChangeListener {
        StandbyTracker() {
        }

        public void onAppIdleStateChanged(String packageName, int userId, boolean idle, int bucket, int reason) {
        }

        public void onParoleStateChanged(boolean isParoleOn) {
            if (JobSchedulerService.DEBUG_STANDBY) {
                StringBuilder sb = new StringBuilder();
                sb.append("Global parole state now ");
                sb.append(isParoleOn ? "ON" : "OFF");
                Slog.i(JobSchedulerService.TAG, sb.toString());
            }
            JobSchedulerService.this.mInParole = isParoleOn;
        }

        public void onUserInteractionStarted(String packageName, int userId) {
            long sinceLast;
            int uid = JobSchedulerService.this.mLocalPM.getPackageUid(packageName, 8192, userId);
            if (uid >= 0) {
                long sinceLast2 = JobSchedulerService.this.mUsageStats.getTimeSinceLastJobRun(packageName, userId);
                if (sinceLast2 > 172800000) {
                    sinceLast = 0;
                } else {
                    sinceLast = sinceLast2;
                }
                DeferredJobCounter counter = new DeferredJobCounter();
                synchronized (JobSchedulerService.this.mLock) {
                    JobSchedulerService.this.mJobs.forEachJobForSourceUid(uid, counter);
                }
                if (counter.numDeferred() > 0 || sinceLast > 0) {
                    ((BatteryStatsInternal) LocalServices.getService(BatteryStatsInternal.class)).noteJobsDeferred(uid, counter.numDeferred(), sinceLast);
                    StatsLog.write_non_chained(85, uid, (String) null, counter.numDeferred(), sinceLast);
                }
            }
        }
    }

    static class DeferredJobCounter implements Consumer<JobStatus> {
        private int mDeferred = 0;

        DeferredJobCounter() {
        }

        public int numDeferred() {
            return this.mDeferred;
        }

        public void accept(JobStatus job) {
            if (job.getWhenStandbyDeferred() > 0) {
                this.mDeferred++;
            }
        }
    }

    public static int standbyBucketToBucketIndex(int bucket) {
        if (bucket == 50) {
            return 4;
        }
        if (bucket > 30) {
            return 3;
        }
        if (bucket > 20) {
            return 2;
        }
        if (bucket > 10) {
            return 1;
        }
        return 0;
    }

    public static int standbyBucketForPackage(String packageName, int userId, long elapsedNow) {
        int bucket;
        UsageStatsManagerInternal usageStats = (UsageStatsManagerInternal) LocalServices.getService(UsageStatsManagerInternal.class);
        if (usageStats != null) {
            bucket = usageStats.getAppStandbyBucket(packageName, userId, elapsedNow);
        } else {
            bucket = 0;
        }
        int bucket2 = standbyBucketToBucketIndex(bucket);
        if (DEBUG_STANDBY) {
            Slog.v(TAG, packageName + SliceClientPermissions.SliceAuthority.DELIMITER + userId + " standby bucket index: " + bucket2);
        }
        return bucket2;
    }

    /* access modifiers changed from: private */
    /* renamed from: updateUltraPowerSavingState */
    public void lambda$onBootPhase$2$JobSchedulerService(boolean ultraPowerSaving) {
        synchronized (this.mLock) {
            this.mBeingUltraPowerSavingMode = ultraPowerSaving;
            if (ultraPowerSaving) {
                for (int i = 0; i < this.mActiveServices.size(); i++) {
                    JobServiceContext jsc = this.mActiveServices.get(i);
                    if (jsc.getRunningJobLocked() != null) {
                        jsc.cancelExecutingJobLocked(0, "cancelled due to ultra power saver");
                    }
                }
            } else {
                this.mHandler.obtainMessage(1).sendToTarget();
            }
        }
    }

    final class JobSchedulerStub extends IJobScheduler.Stub {
        private final SparseArray<Boolean> mPersistCache = new SparseArray<>();

        JobSchedulerStub() {
        }

        /* Debug info: failed to restart local var, previous not found, register: 6 */
        private void enforceValidJobRequest(int uid, JobInfo job) {
            IPackageManager pm = AppGlobals.getPackageManager();
            ComponentName service = job.getService();
            try {
                ServiceInfo si = pm.getServiceInfo(service, 786432, UserHandle.getUserId(uid));
                if (si == null) {
                    throw new IllegalArgumentException("No such service " + service);
                } else if (si.applicationInfo.uid != uid) {
                    throw new IllegalArgumentException("uid " + uid + " cannot schedule job in " + service.getPackageName());
                } else if (!"android.permission.BIND_JOB_SERVICE".equals(si.permission)) {
                    throw new IllegalArgumentException("Scheduled service " + service + " does not require android.permission.BIND_JOB_SERVICE permission");
                }
            } catch (RemoteException e) {
            }
        }

        private boolean canPersistJobs(int pid, int uid) {
            boolean canPersist;
            synchronized (this.mPersistCache) {
                Boolean cached = this.mPersistCache.get(uid);
                if (cached != null) {
                    canPersist = cached.booleanValue();
                } else {
                    boolean canPersist2 = JobSchedulerService.this.getContext().checkPermission("android.permission.RECEIVE_BOOT_COMPLETED", pid, uid) == 0;
                    this.mPersistCache.put(uid, Boolean.valueOf(canPersist2));
                    canPersist = canPersist2;
                }
            }
            return canPersist;
        }

        private void validateJobFlags(JobInfo job, int callingUid) {
            if ((job.getFlags() & 1) != 0) {
                JobSchedulerService.this.getContext().enforceCallingOrSelfPermission("android.permission.CONNECTIVITY_INTERNAL", JobSchedulerService.TAG);
            }
            if ((job.getFlags() & 8) == 0) {
                return;
            }
            if (callingUid != 1000) {
                throw new SecurityException("Job has invalid flags");
            } else if (job.isPeriodic()) {
                Slog.wtf(JobSchedulerService.TAG, "Periodic jobs mustn't have FLAG_EXEMPT_FROM_APP_STANDBY. Job=" + job);
            }
        }

        public int schedule(JobInfo job) throws RemoteException {
            if (JobSchedulerService.DEBUG) {
                Slog.d(JobSchedulerService.TAG, "Scheduling job: " + job.toString());
            }
            int pid = Binder.getCallingPid();
            int uid = Binder.getCallingUid();
            int userId = UserHandle.getUserId(uid);
            enforceValidJobRequest(uid, job);
            if (!job.isPersisted() || canPersistJobs(pid, uid)) {
                validateJobFlags(job, uid);
                long ident = Binder.clearCallingIdentity();
                try {
                    return JobSchedulerService.this.scheduleAsPackage(job, (JobWorkItem) null, uid, (String) null, userId, (String) null);
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            } else {
                throw new IllegalArgumentException("Error: requested job be persisted without holding RECEIVE_BOOT_COMPLETED permission.");
            }
        }

        public int enqueue(JobInfo job, JobWorkItem work) throws RemoteException {
            if (JobSchedulerService.DEBUG) {
                Slog.d(JobSchedulerService.TAG, "Enqueueing job: " + job.toString() + " work: " + work);
            }
            int uid = Binder.getCallingUid();
            int userId = UserHandle.getUserId(uid);
            enforceValidJobRequest(uid, job);
            if (job.isPersisted()) {
                throw new IllegalArgumentException("Can't enqueue work for persisted jobs");
            } else if (work != null) {
                validateJobFlags(job, uid);
                long ident = Binder.clearCallingIdentity();
                try {
                    return JobSchedulerService.this.scheduleAsPackage(job, work, uid, (String) null, userId, (String) null);
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            } else {
                throw new NullPointerException("work is null");
            }
        }

        public int scheduleAsPackage(JobInfo job, String packageName, int userId, String tag) throws RemoteException {
            int callerUid = Binder.getCallingUid();
            if (JobSchedulerService.DEBUG) {
                Slog.d(JobSchedulerService.TAG, "Caller uid " + callerUid + " scheduling job: " + job.toString() + " on behalf of " + packageName + SliceClientPermissions.SliceAuthority.DELIMITER);
            }
            if (packageName == null) {
                throw new NullPointerException("Must specify a package for scheduleAsPackage()");
            } else if (JobSchedulerService.this.getContext().checkCallingOrSelfPermission("android.permission.UPDATE_DEVICE_STATS") == 0) {
                validateJobFlags(job, callerUid);
                long ident = Binder.clearCallingIdentity();
                try {
                    return JobSchedulerService.this.scheduleAsPackage(job, (JobWorkItem) null, callerUid, packageName, userId, tag);
                } finally {
                    Binder.restoreCallingIdentity(ident);
                }
            } else {
                throw new SecurityException("Caller uid " + callerUid + " not permitted to schedule jobs for other apps");
            }
        }

        public ParceledListSlice<JobInfo> getAllPendingJobs() throws RemoteException {
            int uid = Binder.getCallingUid();
            long ident = Binder.clearCallingIdentity();
            try {
                return new ParceledListSlice<>(JobSchedulerService.this.getPendingJobs(uid));
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public JobInfo getPendingJob(int jobId) throws RemoteException {
            int uid = Binder.getCallingUid();
            long ident = Binder.clearCallingIdentity();
            try {
                return JobSchedulerService.this.getPendingJob(uid, jobId);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public void cancelAll() throws RemoteException {
            int uid = Binder.getCallingUid();
            long ident = Binder.clearCallingIdentity();
            try {
                JobSchedulerService jobSchedulerService = JobSchedulerService.this;
                jobSchedulerService.cancelJobsForUid(uid, "cancelAll() called by app, callingUid=" + uid);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public void cancel(int jobId) throws RemoteException {
            int uid = Binder.getCallingUid();
            long ident = Binder.clearCallingIdentity();
            try {
                JobSchedulerService.this.cancelJob(uid, jobId, uid);
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }

        public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
            if (DumpUtils.checkDumpAndUsageStatsPermission(JobSchedulerService.this.getContext(), JobSchedulerService.TAG, pw)) {
                int filterUid = -1;
                boolean proto = false;
                if (!ArrayUtils.isEmpty(args)) {
                    int opti = 0;
                    while (true) {
                        if (opti >= args.length) {
                            break;
                        }
                        String arg = args[opti];
                        if ("-h".equals(arg)) {
                            JobSchedulerService.dumpHelp(pw);
                            return;
                        }
                        if (!"-a".equals(arg)) {
                            if (PriorityDump.PROTO_ARG.equals(arg)) {
                                proto = true;
                            } else if (arg.length() > 0 && arg.charAt(0) == '-') {
                                pw.println("Unknown option: " + arg);
                                return;
                            }
                        }
                        opti++;
                    }
                    if (opti < args.length) {
                        String pkg = args[opti];
                        try {
                            filterUid = JobSchedulerService.this.getContext().getPackageManager().getPackageUid(pkg, DumpState.DUMP_CHANGES);
                        } catch (PackageManager.NameNotFoundException e) {
                            pw.println("Invalid package: " + pkg);
                            return;
                        }
                    }
                }
                long identityToken = Binder.clearCallingIdentity();
                if (proto) {
                    try {
                        JobSchedulerService.this.dumpInternalProto(fd, filterUid);
                    } catch (Throwable th) {
                        Binder.restoreCallingIdentity(identityToken);
                        throw th;
                    }
                } else {
                    JobSchedulerService.this.dumpInternal(new IndentingPrintWriter(pw, "  "), filterUid);
                }
                Binder.restoreCallingIdentity(identityToken);
            }
        }

        /* JADX WARNING: type inference failed for: r1v1, types: [android.os.Binder] */
        /* JADX WARNING: Multi-variable type inference failed */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onShellCommand(java.io.FileDescriptor r9, java.io.FileDescriptor r10, java.io.FileDescriptor r11, java.lang.String[] r12, android.os.ShellCallback r13, android.os.ResultReceiver r14) {
            /*
                r8 = this;
                com.android.server.job.JobSchedulerShellCommand r0 = new com.android.server.job.JobSchedulerShellCommand
                com.android.server.job.JobSchedulerService r1 = com.android.server.job.JobSchedulerService.this
                r0.<init>(r1)
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
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.job.JobSchedulerService.JobSchedulerStub.onShellCommand(java.io.FileDescriptor, java.io.FileDescriptor, java.io.FileDescriptor, java.lang.String[], android.os.ShellCallback, android.os.ResultReceiver):void");
        }

        public List<JobInfo> getStartedJobs() {
            ArrayList<JobInfo> runningJobs;
            if (Binder.getCallingUid() == 1000) {
                synchronized (JobSchedulerService.this.mLock) {
                    runningJobs = new ArrayList<>(JobSchedulerService.this.mActiveServices.size());
                    for (JobServiceContext jsc : JobSchedulerService.this.mActiveServices) {
                        JobStatus job = jsc.getRunningJobLocked();
                        if (job != null) {
                            runningJobs.add(job.getJob());
                        }
                    }
                }
                return runningJobs;
            }
            throw new SecurityException("getStartedJobs() is system internal use only.");
        }

        public ParceledListSlice<JobSnapshot> getAllJobSnapshots() {
            ParceledListSlice<JobSnapshot> parceledListSlice;
            if (Binder.getCallingUid() == 1000) {
                synchronized (JobSchedulerService.this.mLock) {
                    ArrayList<JobSnapshot> snapshots = new ArrayList<>(JobSchedulerService.this.mJobs.size());
                    JobSchedulerService.this.mJobs.forEachJob(new Consumer(snapshots) {
                        private final /* synthetic */ ArrayList f$1;

                        {
                            this.f$1 = r2;
                        }

                        public final void accept(Object obj) {
                            JobSchedulerService.JobSchedulerStub.this.lambda$getAllJobSnapshots$0$JobSchedulerService$JobSchedulerStub(this.f$1, (JobStatus) obj);
                        }
                    });
                    parceledListSlice = new ParceledListSlice<>(snapshots);
                }
                return parceledListSlice;
            }
            throw new SecurityException("getAllJobSnapshots() is system internal use only.");
        }

        public /* synthetic */ void lambda$getAllJobSnapshots$0$JobSchedulerService$JobSchedulerStub(ArrayList snapshots, JobStatus job) {
            snapshots.add(new JobSnapshot(job.getJob(), job.getSatisfiedConstraintFlags(), JobSchedulerService.this.isReadyToBeExecutedLocked(job)));
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 5 */
    /* access modifiers changed from: package-private */
    public int executeRunCommand(String pkgName, int userId, int jobId, boolean force) {
        if (DEBUG) {
            Slog.v(TAG, "executeRunCommand(): " + pkgName + SliceClientPermissions.SliceAuthority.DELIMITER + userId + " " + jobId + " f=" + force);
        }
        try {
            int uid = AppGlobals.getPackageManager().getPackageUid(pkgName, 0, userId != -1 ? userId : 0);
            if (uid < 0) {
                return JobSchedulerShellCommand.CMD_ERR_NO_PACKAGE;
            }
            synchronized (this.mLock) {
                JobStatus js = this.mJobs.getJobByUidAndJobId(uid, jobId);
                if (js == null) {
                    return -1001;
                }
                js.overrideState = force ? 2 : 1;
                if (!js.isConstraintsSatisfied()) {
                    js.overrideState = 0;
                    return JobSchedulerShellCommand.CMD_ERR_CONSTRAINTS;
                }
                queueReadyJobsForExecutionLocked();
                maybeRunPendingJobsLocked();
            }
        } catch (RemoteException e) {
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int executeTimeoutCommand(PrintWriter pw, String pkgName, int userId, boolean hasJobId, int jobId) {
        PrintWriter printWriter = pw;
        if (DEBUG) {
            Slog.v(TAG, "executeTimeoutCommand(): " + pkgName + SliceClientPermissions.SliceAuthority.DELIMITER + userId + " " + jobId);
        } else {
            String str = pkgName;
            int i = userId;
            int i2 = jobId;
        }
        synchronized (this.mLock) {
            boolean foundSome = false;
            for (int i3 = 0; i3 < this.mActiveServices.size(); i3++) {
                JobServiceContext jc = this.mActiveServices.get(i3);
                JobStatus js = jc.getRunningJobLocked();
                if (jc.timeoutIfExecutingLocked(pkgName, userId, hasJobId, jobId, "shell")) {
                    printWriter.print("Timing out: ");
                    js.printUniqueId(printWriter);
                    printWriter.print(" ");
                    printWriter.println(js.getServiceComponent().flattenToShortString());
                    foundSome = true;
                }
            }
            if (!foundSome) {
                printWriter.println("No matching executing jobs found.");
            }
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int executeCancelCommand(PrintWriter pw, String pkgName, int userId, boolean hasJobId, int jobId) {
        if (DEBUG) {
            Slog.v(TAG, "executeCancelCommand(): " + pkgName + SliceClientPermissions.SliceAuthority.DELIMITER + userId + " " + jobId);
        }
        int pkgUid = -1;
        try {
            pkgUid = AppGlobals.getPackageManager().getPackageUid(pkgName, 0, userId);
        } catch (RemoteException e) {
        }
        if (pkgUid < 0) {
            pw.println("Package " + pkgName + " not found.");
            return JobSchedulerShellCommand.CMD_ERR_NO_PACKAGE;
        }
        if (!hasJobId) {
            pw.println("Canceling all jobs for " + pkgName + " in user " + userId);
            if (!cancelJobsForUid(pkgUid, "cancel shell command for package")) {
                pw.println("No matching jobs found.");
            }
        } else {
            pw.println("Canceling job " + pkgName + "/#" + jobId + " in user " + userId);
            if (!cancelJob(pkgUid, jobId, 2000)) {
                pw.println("No matching job found.");
            }
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public void setMonitorBattery(boolean enabled) {
        synchronized (this.mLock) {
            if (this.mBatteryController != null) {
                this.mBatteryController.getTracker().setMonitorBatteryLocked(enabled);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int getBatterySeq() {
        int seq;
        synchronized (this.mLock) {
            seq = this.mBatteryController != null ? this.mBatteryController.getTracker().getSeq() : -1;
        }
        return seq;
    }

    /* access modifiers changed from: package-private */
    public boolean getBatteryCharging() {
        boolean isOnStablePower;
        synchronized (this.mLock) {
            isOnStablePower = this.mBatteryController != null ? this.mBatteryController.getTracker().isOnStablePower() : false;
        }
        return isOnStablePower;
    }

    /* access modifiers changed from: package-private */
    public boolean getBatteryNotLow() {
        boolean isBatteryNotLow;
        synchronized (this.mLock) {
            isBatteryNotLow = this.mBatteryController != null ? this.mBatteryController.getTracker().isBatteryNotLow() : false;
        }
        return isBatteryNotLow;
    }

    /* access modifiers changed from: package-private */
    public int getStorageSeq() {
        int seq;
        synchronized (this.mLock) {
            seq = this.mStorageController != null ? this.mStorageController.getTracker().getSeq() : -1;
        }
        return seq;
    }

    /* access modifiers changed from: package-private */
    public boolean getStorageNotLow() {
        boolean isStorageNotLow;
        synchronized (this.mLock) {
            isStorageNotLow = this.mStorageController != null ? this.mStorageController.getTracker().isStorageNotLow() : false;
        }
        return isStorageNotLow;
    }

    /* access modifiers changed from: package-private */
    public long getCurrentHeartbeat() {
        long j;
        synchronized (this.mLock) {
            j = this.mHeartbeat;
        }
        return j;
    }

    /* Debug info: failed to restart local var, previous not found, register: 10 */
    /* access modifiers changed from: package-private */
    public int getJobState(PrintWriter pw, String pkgName, int userId, int jobId) {
        try {
            int uid = AppGlobals.getPackageManager().getPackageUid(pkgName, 0, userId != -1 ? userId : 0);
            if (uid < 0) {
                pw.print("unknown(");
                pw.print(pkgName);
                pw.println(")");
                return JobSchedulerShellCommand.CMD_ERR_NO_PACKAGE;
            }
            synchronized (this.mLock) {
                JobStatus js = this.mJobs.getJobByUidAndJobId(uid, jobId);
                if (DEBUG) {
                    Slog.d(TAG, "get-job-state " + uid + SliceClientPermissions.SliceAuthority.DELIMITER + jobId + ": " + js);
                }
                if (js == null) {
                    pw.print("unknown(");
                    UserHandle.formatUid(pw, uid);
                    pw.print("/jid");
                    pw.print(jobId);
                    pw.println(")");
                    return -1001;
                }
                boolean printed = false;
                if (this.mPendingJobs.contains(js)) {
                    pw.print("pending");
                    printed = true;
                }
                if (isCurrentlyActiveLocked(js)) {
                    if (printed) {
                        pw.print(" ");
                    }
                    printed = true;
                    pw.println("active");
                }
                if (!ArrayUtils.contains(this.mStartedUsers, js.getUserId())) {
                    if (printed) {
                        pw.print(" ");
                    }
                    printed = true;
                    pw.println("user-stopped");
                }
                if (!ArrayUtils.contains(this.mStartedUsers, js.getSourceUserId())) {
                    if (printed) {
                        pw.print(" ");
                    }
                    printed = true;
                    pw.println("source-user-stopped");
                }
                if (this.mBackingUpUids.indexOfKey(js.getSourceUid()) >= 0) {
                    if (printed) {
                        pw.print(" ");
                    }
                    printed = true;
                    pw.println("backing-up");
                }
                boolean componentPresent = false;
                try {
                    componentPresent = AppGlobals.getPackageManager().getServiceInfo(js.getServiceComponent(), 268435456, js.getUserId()) != null;
                } catch (RemoteException e) {
                }
                if (!componentPresent) {
                    if (printed) {
                        pw.print(" ");
                    }
                    printed = true;
                    pw.println("no-component");
                }
                if (js.isReady()) {
                    if (printed) {
                        pw.print(" ");
                    }
                    printed = true;
                    pw.println("ready");
                }
                if (!printed) {
                    pw.print("waiting");
                }
                pw.println();
            }
        } catch (RemoteException e2) {
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int executeHeartbeatCommand(PrintWriter pw, int numBeats) {
        if (numBeats < 1) {
            pw.println(getCurrentHeartbeat());
            return 0;
        }
        pw.print("Advancing standby heartbeat by ");
        pw.println(numBeats);
        synchronized (this.mLock) {
            advanceHeartbeatLocked((long) numBeats);
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public void triggerDockState(boolean idleState) {
        Intent dockIntent;
        if (idleState) {
            dockIntent = new Intent("android.intent.action.DOCK_IDLE");
        } else {
            dockIntent = new Intent("android.intent.action.DOCK_ACTIVE");
        }
        dockIntent.setPackage(PackageManagerService.PLATFORM_PACKAGE_NAME);
        dockIntent.addFlags(1342177280);
        getContext().sendBroadcastAsUser(dockIntent, UserHandle.ALL);
    }

    static void dumpHelp(PrintWriter pw) {
        pw.println("Job Scheduler (jobscheduler) dump options:");
        pw.println("  [-h] [package] ...");
        pw.println("    -h: print this help");
        pw.println("  [package] is an optional package name to limit the output to.");
    }

    private static void sortJobs(List<JobStatus> jobs) {
        Collections.sort(jobs, new Comparator<JobStatus>() {
            public int compare(JobStatus o1, JobStatus o2) {
                int uid1 = o1.getUid();
                int uid2 = o2.getUid();
                int id1 = o1.getJobId();
                int id2 = o2.getJobId();
                if (uid1 != uid2) {
                    if (uid1 < uid2) {
                        return -1;
                    }
                    return 1;
                } else if (id1 < id2) {
                    return -1;
                } else {
                    if (id1 > id2) {
                        return 1;
                    }
                    return 0;
                }
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void dumpInternal(IndentingPrintWriter pw, int filterUid) {
        Object obj;
        long now;
        Predicate<JobStatus> predicate;
        int filterUidFinal;
        IndentingPrintWriter indentingPrintWriter = pw;
        int filterUidFinal2 = UserHandle.getAppId(filterUid);
        long now2 = sSystemClock.millis();
        long nowElapsed = sElapsedRealtimeClock.millis();
        long nowUptime = sUptimeMillisClock.millis();
        Predicate<JobStatus> predicate2 = new Predicate(filterUidFinal2) {
            private final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return JobSchedulerService.lambda$dumpInternal$4(this.f$0, (JobStatus) obj);
            }
        };
        Object obj2 = this.mLock;
        synchronized (obj2) {
            try {
                this.mConstants.dump(indentingPrintWriter);
                for (StateController controller : this.mControllers) {
                    try {
                        pw.increaseIndent();
                        controller.dumpConstants(indentingPrintWriter);
                        pw.decreaseIndent();
                    } catch (Throwable th) {
                        th = th;
                        obj = obj2;
                        int i = filterUidFinal2;
                        long j = now2;
                        int filterUidFinal3 = filterUid;
                        $$Lambda$JobSchedulerService$VVCk0M0TpfxhVRrY28dggbYJQc r11 = predicate2;
                    }
                }
                pw.println();
                indentingPrintWriter.println("  Heartbeat:");
                indentingPrintWriter.print("    Current:    ");
                indentingPrintWriter.println(this.mHeartbeat);
                indentingPrintWriter.println("    Next");
                indentingPrintWriter.print("      ACTIVE:   ");
                indentingPrintWriter.println(this.mNextBucketHeartbeat[0]);
                indentingPrintWriter.print("      WORKING:  ");
                indentingPrintWriter.println(this.mNextBucketHeartbeat[1]);
                indentingPrintWriter.print("      FREQUENT: ");
                indentingPrintWriter.println(this.mNextBucketHeartbeat[2]);
                indentingPrintWriter.print("      RARE:     ");
                indentingPrintWriter.println(this.mNextBucketHeartbeat[3]);
                indentingPrintWriter.print("    Last heartbeat: ");
                TimeUtils.formatDuration(this.mLastHeartbeatTime, nowElapsed, indentingPrintWriter);
                pw.println();
                indentingPrintWriter.print("    Next heartbeat: ");
                TimeUtils.formatDuration(this.mLastHeartbeatTime + this.mConstants.STANDBY_HEARTBEAT_TIME, nowElapsed, indentingPrintWriter);
                pw.println();
                indentingPrintWriter.print("    In parole?: ");
                indentingPrintWriter.print(this.mInParole);
                pw.println();
                indentingPrintWriter.print("    In thermal throttling?: ");
                indentingPrintWriter.print(this.mThermalConstraint);
                pw.println();
                pw.println();
                indentingPrintWriter.println("Started users: " + Arrays.toString(this.mStartedUsers));
                indentingPrintWriter.print("Registered ");
                indentingPrintWriter.print(this.mJobs.size());
                indentingPrintWriter.println(" jobs:");
                if (this.mJobs.size() > 0) {
                    try {
                        List<JobStatus> jobs = this.mJobs.mJobSet.getAllJobs();
                        sortJobs(jobs);
                        for (JobStatus job : jobs) {
                            indentingPrintWriter.print("  JOB #");
                            job.printUniqueId(indentingPrintWriter);
                            indentingPrintWriter.print(": ");
                            indentingPrintWriter.println(job.toShortStringExceptUniqueId());
                            if (predicate2.test(job)) {
                                long now3 = now2;
                                JobStatus job2 = job;
                                Predicate<JobStatus> predicate3 = predicate2;
                                obj = obj2;
                                try {
                                    job.dump((PrintWriter) pw, "    ", true, nowElapsed);
                                    indentingPrintWriter.print("    Last run heartbeat: ");
                                    indentingPrintWriter.print(heartbeatWhenJobsLastRun(job2));
                                    pw.println();
                                    indentingPrintWriter.print("    Ready: ");
                                    indentingPrintWriter.print(isReadyToBeExecutedLocked(job2));
                                    indentingPrintWriter.print(" (job=");
                                    indentingPrintWriter.print(job2.isReady());
                                    indentingPrintWriter.print(" user=");
                                    indentingPrintWriter.print(areUsersStartedLocked(job2));
                                    indentingPrintWriter.print(" !pending=");
                                    indentingPrintWriter.print(!this.mPendingJobs.contains(job2));
                                    indentingPrintWriter.print(" !active=");
                                    indentingPrintWriter.print(!isCurrentlyActiveLocked(job2));
                                    indentingPrintWriter.print(" !backingup=");
                                    indentingPrintWriter.print(this.mBackingUpUids.indexOfKey(job2.getSourceUid()) < 0);
                                    indentingPrintWriter.print(" comp=");
                                    boolean componentPresent = false;
                                    try {
                                        componentPresent = AppGlobals.getPackageManager().getServiceInfo(job2.getServiceComponent(), 268435456, job2.getUserId()) != null;
                                    } catch (RemoteException e) {
                                    }
                                    indentingPrintWriter.print(componentPresent);
                                    indentingPrintWriter.println(")");
                                    predicate2 = predicate3;
                                    now2 = now3;
                                    obj2 = obj;
                                } catch (Throwable th2) {
                                    th = th2;
                                    int i2 = filterUidFinal2;
                                    int filterUidFinal4 = filterUid;
                                    throw th;
                                }
                            }
                        }
                        obj = obj2;
                        now = now2;
                        predicate = predicate2;
                    } catch (Throwable th3) {
                        th = th3;
                        obj = obj2;
                        long j2 = now2;
                        $$Lambda$JobSchedulerService$VVCk0M0TpfxhVRrY28dggbYJQc r112 = predicate2;
                        int i3 = filterUidFinal2;
                        int filterUidFinal5 = filterUid;
                        throw th;
                    }
                } else {
                    obj = obj2;
                    now = now2;
                    predicate = predicate2;
                    try {
                        indentingPrintWriter.println("  None.");
                    } catch (Throwable th4) {
                        th = th4;
                        int i4 = filterUidFinal2;
                        int filterUidFinal6 = filterUid;
                        throw th;
                    }
                }
                for (int i5 = 0; i5 < this.mControllers.size(); i5++) {
                    pw.println();
                    indentingPrintWriter.println(this.mControllers.get(i5).getClass().getSimpleName() + ":");
                    pw.increaseIndent();
                    this.mControllers.get(i5).dumpControllerStateLocked(indentingPrintWriter, predicate);
                    pw.decreaseIndent();
                }
                pw.println();
                indentingPrintWriter.println("Uid priority overrides:");
                for (int i6 = 0; i6 < this.mUidPriorityOverride.size(); i6++) {
                    int uid = this.mUidPriorityOverride.keyAt(i6);
                    if (filterUidFinal2 == -1 || filterUidFinal2 == UserHandle.getAppId(uid)) {
                        indentingPrintWriter.print("  ");
                        indentingPrintWriter.print(UserHandle.formatUid(uid));
                        indentingPrintWriter.print(": ");
                        indentingPrintWriter.println(this.mUidPriorityOverride.valueAt(i6));
                    }
                }
                if (this.mBackingUpUids.size() > 0) {
                    pw.println();
                    indentingPrintWriter.println("Backing up uids:");
                    boolean first = true;
                    for (int i7 = 0; i7 < this.mBackingUpUids.size(); i7++) {
                        int uid2 = this.mBackingUpUids.keyAt(i7);
                        if (filterUidFinal2 == -1 || filterUidFinal2 == UserHandle.getAppId(uid2)) {
                            if (first) {
                                indentingPrintWriter.print("  ");
                                first = false;
                            } else {
                                indentingPrintWriter.print(", ");
                            }
                            indentingPrintWriter.print(UserHandle.formatUid(uid2));
                        }
                    }
                    pw.println();
                }
                pw.println();
                this.mJobPackageTracker.dump((PrintWriter) indentingPrintWriter, "", filterUidFinal2);
                pw.println();
                if (this.mJobPackageTracker.dumpHistory((PrintWriter) indentingPrintWriter, "", filterUidFinal2)) {
                    pw.println();
                }
                indentingPrintWriter.println("Pending queue:");
                for (int i8 = 0; i8 < this.mPendingJobs.size(); i8++) {
                    JobStatus job3 = this.mPendingJobs.get(i8);
                    indentingPrintWriter.print("  Pending #");
                    indentingPrintWriter.print(i8);
                    indentingPrintWriter.print(": ");
                    indentingPrintWriter.println(job3.toShortString());
                    JobStatus job4 = job3;
                    job3.dump((PrintWriter) pw, "    ", false, nowElapsed);
                    int priority = evaluateJobPriorityLocked(job4);
                    indentingPrintWriter.print("    Evaluated priority: ");
                    indentingPrintWriter.println(JobInfo.getPriorityString(priority));
                    indentingPrintWriter.print("    Tag: ");
                    indentingPrintWriter.println(job4.getTag());
                    indentingPrintWriter.print("    Enq: ");
                    TimeUtils.formatDuration(job4.madePending - nowUptime, indentingPrintWriter);
                    pw.println();
                }
                pw.println();
                indentingPrintWriter.println("Active jobs:");
                int i9 = 0;
                while (i9 < this.mActiveServices.size()) {
                    JobServiceContext jsc = this.mActiveServices.get(i9);
                    indentingPrintWriter.print("  Slot #");
                    indentingPrintWriter.print(i9);
                    indentingPrintWriter.print(": ");
                    JobStatus job5 = jsc.getRunningJobLocked();
                    if (job5 != null) {
                        indentingPrintWriter.println(job5.toShortString());
                        indentingPrintWriter.print("    Running for: ");
                        TimeUtils.formatDuration(nowElapsed - jsc.getExecutionStartTimeElapsed(), indentingPrintWriter);
                        indentingPrintWriter.print(", timeout at: ");
                        TimeUtils.formatDuration(jsc.getTimeoutElapsed() - nowElapsed, indentingPrintWriter);
                        pw.println();
                        filterUidFinal = filterUidFinal2;
                        JobStatus job6 = job5;
                        try {
                            job5.dump((PrintWriter) pw, "    ", false, nowElapsed);
                            int priority2 = evaluateJobPriorityLocked(jsc.getRunningJobLocked());
                            indentingPrintWriter.print("    Evaluated priority: ");
                            indentingPrintWriter.println(JobInfo.getPriorityString(priority2));
                            indentingPrintWriter.print("    Active at ");
                            TimeUtils.formatDuration(job6.madeActive - nowUptime, indentingPrintWriter);
                            indentingPrintWriter.print(", pending for ");
                            TimeUtils.formatDuration(job6.madeActive - job6.madePending, indentingPrintWriter);
                            pw.println();
                        } catch (Throwable th5) {
                            th = th5;
                            int i10 = filterUid;
                        }
                    } else if (jsc.mStoppedReason != null) {
                        indentingPrintWriter.print("inactive since ");
                        TimeUtils.formatDuration(jsc.mStoppedTime, nowElapsed, indentingPrintWriter);
                        indentingPrintWriter.print(", stopped because: ");
                        indentingPrintWriter.println(jsc.mStoppedReason);
                        filterUidFinal = filterUidFinal2;
                    } else {
                        indentingPrintWriter.println("inactive");
                        filterUidFinal = filterUidFinal2;
                    }
                    i9++;
                    filterUidFinal2 = filterUidFinal;
                }
                if (filterUid == -1) {
                    try {
                        pw.println();
                        indentingPrintWriter.print("mReadyToRock=");
                        indentingPrintWriter.println(this.mReadyToRock);
                        indentingPrintWriter.print("mReportedActive=");
                        indentingPrintWriter.println(this.mReportedActive);
                    } catch (Throwable th6) {
                        th = th6;
                        throw th;
                    }
                }
                pw.println();
                this.mConcurrencyManager.dumpLocked(pw, now, nowElapsed);
                pw.println();
                indentingPrintWriter.print("PersistStats: ");
                indentingPrintWriter.println(this.mJobs.getPersistStats());
                pw.println();
            } catch (Throwable th7) {
                th = th7;
                obj = obj2;
                int i11 = filterUidFinal2;
                long j3 = now2;
                int filterUidFinal7 = filterUid;
                Predicate<JobStatus> predicate4 = predicate2;
                throw th;
            }
        }
    }

    static /* synthetic */ boolean lambda$dumpInternal$4(int filterUidFinal, JobStatus js) {
        return filterUidFinal == -1 || UserHandle.getAppId(js.getUid()) == filterUidFinal || UserHandle.getAppId(js.getSourceUid()) == filterUidFinal;
    }

    /* access modifiers changed from: package-private */
    public void dumpInternalProto(FileDescriptor fd, int filterUid) {
        Object obj;
        long now;
        long ajToken;
        int filterUidFinal;
        $$Lambda$JobSchedulerService$eQqdX2w3FwBMn_LMfN2Y0HQCDq4 r24;
        long rjToken;
        JobStatus job;
        long ijToken;
        ProtoOutputStream proto = new ProtoOutputStream(fd);
        int filterUidFinal2 = UserHandle.getAppId(filterUid);
        long now2 = sSystemClock.millis();
        long nowElapsed = sElapsedRealtimeClock.millis();
        long nowUptime = sUptimeMillisClock.millis();
        $$Lambda$JobSchedulerService$eQqdX2w3FwBMn_LMfN2Y0HQCDq4 r10 = new Predicate(filterUidFinal2) {
            private final /* synthetic */ int f$0;

            {
                this.f$0 = r1;
            }

            public final boolean test(Object obj) {
                return JobSchedulerService.lambda$dumpInternalProto$5(this.f$0, (JobStatus) obj);
            }
        };
        Object obj2 = this.mLock;
        synchronized (obj2) {
            try {
                long settingsToken = proto.start(1146756268033L);
                this.mConstants.dump(proto);
                for (StateController controller : this.mControllers) {
                    try {
                        controller.dumpConstants(proto);
                    } catch (Throwable th) {
                        th = th;
                        int i = filterUid;
                        obj = obj2;
                        int i2 = filterUidFinal2;
                        long j = now2;
                        $$Lambda$JobSchedulerService$eQqdX2w3FwBMn_LMfN2Y0HQCDq4 r12 = r10;
                    }
                }
                proto.end(settingsToken);
                long j2 = settingsToken;
                proto.write(1120986464270L, this.mHeartbeat);
                proto.write(2220498092047L, this.mNextBucketHeartbeat[0]);
                proto.write(2220498092047L, this.mNextBucketHeartbeat[1]);
                proto.write(2220498092047L, this.mNextBucketHeartbeat[2]);
                proto.write(2220498092047L, this.mNextBucketHeartbeat[3]);
                proto.write(1112396529680L, this.mLastHeartbeatTime - nowUptime);
                proto.write(1112396529681L, (this.mLastHeartbeatTime + this.mConstants.STANDBY_HEARTBEAT_TIME) - nowUptime);
                proto.write(1133871366162L, this.mInParole);
                proto.write(1133871366163L, this.mThermalConstraint);
                for (int u : this.mStartedUsers) {
                    proto.write(2220498092034L, u);
                }
                if (this.mJobs.size() > 0) {
                    try {
                        List<JobStatus> jobs = this.mJobs.mJobSet.getAllJobs();
                        sortJobs(jobs);
                        for (JobStatus job2 : jobs) {
                            long rjToken2 = proto.start(2246267895811L);
                            job2.writeToShortProto(proto, 1146756268033L);
                            if (r10.test(job2)) {
                                long now3 = now2;
                                long now4 = rjToken2;
                                obj = obj2;
                                JobStatus job3 = job2;
                                try {
                                    job2.dump(proto, 1146756268034L, true, nowElapsed);
                                    proto.write(1133871366147L, job3.isReady());
                                    JobStatus job4 = job3;
                                    proto.write(1133871366148L, areUsersStartedLocked(job4));
                                    proto.write(1133871366149L, this.mPendingJobs.contains(job4));
                                    proto.write(1133871366150L, isCurrentlyActiveLocked(job4));
                                    proto.write(1133871366151L, this.mBackingUpUids.indexOfKey(job4.getSourceUid()) >= 0);
                                    boolean componentPresent = false;
                                    try {
                                        componentPresent = AppGlobals.getPackageManager().getServiceInfo(job4.getServiceComponent(), 268435456, job4.getUserId()) != null;
                                    } catch (RemoteException e) {
                                    }
                                    proto.write(1133871366152L, componentPresent);
                                    proto.write(1112396529673L, heartbeatWhenJobsLastRun(job4));
                                    proto.end(now4);
                                    obj2 = obj;
                                    now2 = now3;
                                } catch (Throwable th2) {
                                    th = th2;
                                    int i3 = filterUid;
                                    int i4 = filterUidFinal2;
                                    $$Lambda$JobSchedulerService$eQqdX2w3FwBMn_LMfN2Y0HQCDq4 r122 = r10;
                                }
                            }
                        }
                        obj = obj2;
                        now = now2;
                    } catch (Throwable th3) {
                        th = th3;
                        obj = obj2;
                        long j3 = now2;
                        int i5 = filterUid;
                        int i6 = filterUidFinal2;
                        $$Lambda$JobSchedulerService$eQqdX2w3FwBMn_LMfN2Y0HQCDq4 r123 = r10;
                        throw th;
                    }
                } else {
                    obj = obj2;
                    now = now2;
                }
                try {
                    for (StateController controller2 : this.mControllers) {
                        controller2.dumpControllerStateLocked(proto, 2246267895812L, r10);
                    }
                    for (int i7 = 0; i7 < this.mUidPriorityOverride.size(); i7++) {
                        int uid = this.mUidPriorityOverride.keyAt(i7);
                        if (filterUidFinal2 == -1 || filterUidFinal2 == UserHandle.getAppId(uid)) {
                            long pToken = proto.start(2246267895813L);
                            proto.write(1120986464257L, uid);
                            proto.write(1172526071810L, this.mUidPriorityOverride.valueAt(i7));
                            proto.end(pToken);
                        }
                    }
                    for (int i8 = 0; i8 < this.mBackingUpUids.size(); i8++) {
                        int uid2 = this.mBackingUpUids.keyAt(i8);
                        if (filterUidFinal2 == -1 || filterUidFinal2 == UserHandle.getAppId(uid2)) {
                            proto.write(2220498092038L, uid2);
                        }
                    }
                    this.mJobPackageTracker.dump(proto, 1146756268040L, filterUidFinal2);
                    this.mJobPackageTracker.dumpHistory(proto, 1146756268039L, filterUidFinal2);
                    Iterator<JobStatus> it = this.mPendingJobs.iterator();
                    while (it.hasNext()) {
                        JobStatus job5 = it.next();
                        long pjToken = proto.start(2246267895817L);
                        job5.writeToShortProto(proto, 1146756268033L);
                        job5.dump(proto, 1146756268034L, false, nowElapsed);
                        proto.write(1172526071811L, evaluateJobPriorityLocked(job5));
                        proto.write(1112396529668L, nowUptime - job5.madePending);
                        proto.end(pjToken);
                    }
                    for (JobServiceContext jsc : this.mActiveServices) {
                        try {
                            long ajToken2 = proto.start(2246267895818L);
                            JobStatus job6 = jsc.getRunningJobLocked();
                            if (job6 == null) {
                                try {
                                    ijToken = proto.start(1146756268033L);
                                    r24 = r10;
                                } catch (Throwable th4) {
                                    th = th4;
                                    int i9 = filterUid;
                                    int i10 = filterUidFinal2;
                                    $$Lambda$JobSchedulerService$eQqdX2w3FwBMn_LMfN2Y0HQCDq4 r124 = r10;
                                    throw th;
                                }
                                try {
                                    proto.write(1112396529665L, nowElapsed - jsc.mStoppedTime);
                                    if (jsc.mStoppedReason != null) {
                                        proto.write(1138166333442L, jsc.mStoppedReason);
                                    }
                                    proto.end(ijToken);
                                    JobStatus jobStatus = job6;
                                    filterUidFinal = filterUidFinal2;
                                    ajToken = ajToken2;
                                } catch (Throwable th5) {
                                    th = th5;
                                    int i11 = filterUid;
                                    int i12 = filterUidFinal2;
                                    $$Lambda$JobSchedulerService$eQqdX2w3FwBMn_LMfN2Y0HQCDq4 r125 = r24;
                                    throw th;
                                }
                            } else {
                                r24 = r10;
                                try {
                                    long rjToken3 = proto.start(1146756268034L);
                                    job6.writeToShortProto(proto, 1146756268033L);
                                    proto.write(1112396529666L, nowElapsed - jsc.getExecutionStartTimeElapsed());
                                    proto.write(1112396529667L, jsc.getTimeoutElapsed() - nowElapsed);
                                    filterUidFinal = filterUidFinal2;
                                    ajToken = ajToken2;
                                    rjToken = rjToken3;
                                    job = job6;
                                } catch (Throwable th6) {
                                    th = th6;
                                    int i13 = filterUidFinal2;
                                    int i14 = filterUid;
                                    $$Lambda$JobSchedulerService$eQqdX2w3FwBMn_LMfN2Y0HQCDq4 r126 = r24;
                                    throw th;
                                }
                                try {
                                    job6.dump(proto, 1146756268036L, false, nowElapsed);
                                    proto.write(1172526071813L, evaluateJobPriorityLocked(jsc.getRunningJobLocked()));
                                    proto.write(1112396529670L, nowUptime - job.madeActive);
                                    proto.write(1112396529671L, job.madeActive - job.madePending);
                                    proto.end(rjToken);
                                } catch (Throwable th7) {
                                    th = th7;
                                    int i15 = filterUid;
                                    $$Lambda$JobSchedulerService$eQqdX2w3FwBMn_LMfN2Y0HQCDq4 r127 = r24;
                                    throw th;
                                }
                            }
                            proto.end(ajToken);
                            r10 = r24;
                            filterUidFinal2 = filterUidFinal;
                        } catch (Throwable th8) {
                            th = th8;
                            int i16 = filterUidFinal2;
                            int i17 = filterUid;
                            $$Lambda$JobSchedulerService$eQqdX2w3FwBMn_LMfN2Y0HQCDq4 r128 = r10;
                            throw th;
                        }
                    }
                    $$Lambda$JobSchedulerService$eQqdX2w3FwBMn_LMfN2Y0HQCDq4 r242 = r10;
                    int i18 = filterUidFinal2;
                    if (filterUid == -1) {
                        try {
                            proto.write(1133871366155L, this.mReadyToRock);
                            proto.write(1133871366156L, this.mReportedActive);
                        } catch (Throwable th9) {
                            th = th9;
                            $$Lambda$JobSchedulerService$eQqdX2w3FwBMn_LMfN2Y0HQCDq4 r129 = r242;
                        }
                    }
                    try {
                        $$Lambda$JobSchedulerService$eQqdX2w3FwBMn_LMfN2Y0HQCDq4 r1210 = r242;
                        try {
                            this.mConcurrencyManager.dumpProtoLocked(proto, 1146756268052L, now, nowElapsed);
                            proto.flush();
                        } catch (Throwable th10) {
                            th = th10;
                            throw th;
                        }
                    } catch (Throwable th11) {
                        th = th11;
                        $$Lambda$JobSchedulerService$eQqdX2w3FwBMn_LMfN2Y0HQCDq4 r1211 = r242;
                        throw th;
                    }
                } catch (Throwable th12) {
                    th = th12;
                    int i19 = filterUid;
                    int i20 = filterUidFinal2;
                    $$Lambda$JobSchedulerService$eQqdX2w3FwBMn_LMfN2Y0HQCDq4 r1212 = r10;
                    throw th;
                }
            } catch (Throwable th13) {
                th = th13;
                int i21 = filterUid;
                obj = obj2;
                int i22 = filterUidFinal2;
                long j4 = now2;
                $$Lambda$JobSchedulerService$eQqdX2w3FwBMn_LMfN2Y0HQCDq4 r1213 = r10;
                throw th;
            }
        }
    }

    static /* synthetic */ boolean lambda$dumpInternalProto$5(int filterUidFinal, JobStatus js) {
        return filterUidFinal == -1 || UserHandle.getAppId(js.getUid()) == filterUidFinal || UserHandle.getAppId(js.getSourceUid()) == filterUidFinal;
    }
}
