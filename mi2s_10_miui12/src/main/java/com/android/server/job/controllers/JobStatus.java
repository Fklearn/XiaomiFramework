package com.android.server.job.controllers;

import android.app.IActivityManager;
import android.app.job.JobInfo;
import android.app.job.JobWorkItem;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.pm.PackageManagerInternal;
import android.net.Network;
import android.net.Uri;
import android.os.UserHandle;
import android.text.format.Time;
import android.util.ArraySet;
import android.util.Pair;
import android.util.Slog;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import com.android.server.LocalServices;
import com.android.server.job.GrantedUriPermissions;
import com.android.server.job.JobSchedulerInternal;
import com.android.server.job.JobSchedulerService;
import com.android.server.job.controllers.ContentObserverController;
import com.android.server.slice.SliceClientPermissions;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

public final class JobStatus {
    static final int CONSTRAINTS_OF_INTEREST = -1811939313;
    static final int CONSTRAINT_BACKGROUND_NOT_RESTRICTED = 4194304;
    static final int CONSTRAINT_BATTERY_NOT_LOW = 2;
    static final int CONSTRAINT_CHARGING = 1;
    static final int CONSTRAINT_CONNECTIVITY = 268435456;
    static final int CONSTRAINT_CONTENT_TRIGGER = 67108864;
    static final int CONSTRAINT_DEADLINE = 1073741824;
    static final int CONSTRAINT_DEVICE_NOT_DOZING = 33554432;
    static final int CONSTRAINT_IDLE = 4;
    static final int CONSTRAINT_STORAGE_NOT_LOW = 8;
    static final int CONSTRAINT_TIMING_DELAY = Integer.MIN_VALUE;
    static final int CONSTRAINT_WITHIN_QUOTA = 16777216;
    static final boolean DEBUG = JobSchedulerService.DEBUG;
    static final boolean DEBUG_PREPARE = true;
    public static final long DEFAULT_TRIGGER_MAX_DELAY = 120000;
    public static final long DEFAULT_TRIGGER_UPDATE_DELAY = 10000;
    public static final int INTERNAL_FLAG_HAS_FOREGROUND_EXEMPTION = 1;
    public static final long MIN_TRIGGER_MAX_DELAY = 1000;
    public static final long MIN_TRIGGER_UPDATE_DELAY = 500;
    public static final long NO_EARLIEST_RUNTIME = 0;
    public static final long NO_LATEST_RUNTIME = Long.MAX_VALUE;
    public static final int OVERRIDE_FULL = 2;
    public static final int OVERRIDE_SOFT = 1;
    static final int SOFT_OVERRIDE_CONSTRAINTS = -2147483633;
    private static final int STATSD_CONSTRAINTS_TO_LOG = -989855732;
    private static final boolean STATS_LOG_ENABLED = false;
    static final String TAG = "JobSchedulerService";
    public static final int TRACKING_BATTERY = 1;
    public static final int TRACKING_CONNECTIVITY = 2;
    public static final int TRACKING_CONTENT = 4;
    public static final int TRACKING_IDLE = 8;
    public static final int TRACKING_QUOTA = 64;
    public static final int TRACKING_STORAGE = 16;
    public static final int TRACKING_TIME = 32;
    private final long baseHeartbeat;
    final String batteryName;
    final int callingUid;
    public ArraySet<String> changedAuthorities;
    public ArraySet<Uri> changedUris;
    ContentObserverController.JobInstance contentObserverJobInstance;
    public boolean dozeWhitelisted;
    private final long earliestRunTimeElapsedMillis;
    public long enqueueTime;
    public ArrayList<JobWorkItem> executingWork;
    final JobInfo job;
    public int lastEvaluatedPriority;
    private final long latestRunTimeElapsedMillis;
    private int mInternalFlags;
    private long mLastFailedRunTime;
    private long mLastSuccessfulRunTime;
    private long mOriginalLatestRunTimeElapsedMillis;
    private Pair<Long, Long> mPersistedUtcTimes;
    private boolean mReadyDeadlineSatisfied;
    private boolean mReadyNotDozing;
    private boolean mReadyNotRestrictedInBg;
    private boolean mReadyWithinQuota;
    private final int mRequiredConstraintsOfInterest;
    private int mSatisfiedConstraintsOfInterest;
    public long madeActive;
    public long madePending;
    public Network network;
    public int nextPendingWorkId;
    private final int numFailures;
    public int overrideState;
    public ArrayList<JobWorkItem> pendingWork;
    private boolean prepared;
    final int requiredConstraints;
    int satisfiedConstraints;
    final String sourcePackageName;
    final String sourceTag;
    final int sourceUid;
    final int sourceUserId;
    private int standbyBucket;
    final String tag;
    final int targetSdkVersion;
    private long totalNetworkBytes;
    private int trackingControllers;
    public boolean uidActive;
    private Throwable unpreparedPoint;
    private GrantedUriPermissions uriPerms;
    private long whenStandbyDeferred;

    public int getServiceToken() {
        return this.callingUid;
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x0060  */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x006e  */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x008d  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x00c0  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x00ca  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x00d7  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x00e1  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x00fa  */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x00fd  */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0115  */
    /* JADX WARNING: Removed duplicated region for block: B:35:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:9:0x0049  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private JobStatus(android.app.job.JobInfo r21, int r22, int r23, java.lang.String r24, int r25, int r26, long r27, java.lang.String r29, int r30, long r31, long r33, long r35, long r37, int r39) {
        /*
            r20 = this;
            r1 = r20
            r2 = r22
            r3 = r24
            r4 = r25
            r5 = r31
            r7 = r33
            r20.<init>()
            r9 = 0
            r1.unpreparedPoint = r9
            r10 = 0
            r1.satisfiedConstraints = r10
            r1.mSatisfiedConstraintsOfInterest = r10
            r11 = 1
            r1.nextPendingWorkId = r11
            r1.overrideState = r10
            r12 = -1
            r1.totalNetworkBytes = r12
            r12 = r21
            r1.job = r12
            r1.callingUid = r2
            r13 = r23
            r1.targetSdkVersion = r13
            r14 = r26
            r1.standbyBucket = r14
            r11 = r27
            r1.baseHeartbeat = r11
            r16 = -1
            r15 = -1
            if (r4 == r15) goto L_0x0045
            if (r3 == 0) goto L_0x0045
            android.content.pm.IPackageManager r0 = android.app.AppGlobals.getPackageManager()     // Catch:{ RemoteException -> 0x0044 }
            int r0 = r0.getPackageUid(r3, r10, r4)     // Catch:{ RemoteException -> 0x0044 }
            r16 = r0
            goto L_0x0047
        L_0x0044:
            r0 = move-exception
        L_0x0045:
            r0 = r16
        L_0x0047:
            if (r0 != r15) goto L_0x0060
            r1.sourceUid = r2
            int r15 = android.os.UserHandle.getUserId(r22)
            r1.sourceUserId = r15
            android.content.ComponentName r15 = r21.getService()
            java.lang.String r15 = r15.getPackageName()
            r1.sourcePackageName = r15
            r1.sourceTag = r9
            r9 = r29
            goto L_0x006a
        L_0x0060:
            r1.sourceUid = r0
            r1.sourceUserId = r4
            r1.sourcePackageName = r3
            r9 = r29
            r1.sourceTag = r9
        L_0x006a:
            java.lang.String r15 = r1.sourceTag
            if (r15 == 0) goto L_0x008d
            java.lang.StringBuilder r15 = new java.lang.StringBuilder
            r15.<init>()
            java.lang.String r10 = r1.sourceTag
            r15.append(r10)
            java.lang.String r10 = ":"
            r15.append(r10)
            android.content.ComponentName r10 = r21.getService()
            java.lang.String r10 = r10.getPackageName()
            r15.append(r10)
            java.lang.String r10 = r15.toString()
            goto L_0x0095
        L_0x008d:
            android.content.ComponentName r10 = r21.getService()
            java.lang.String r10 = r10.flattenToShortString()
        L_0x0095:
            r1.batteryName = r10
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r15 = "*job*/"
            r10.append(r15)
            java.lang.String r15 = r1.batteryName
            r10.append(r15)
            java.lang.String r10 = r10.toString()
            r1.tag = r10
            r1.earliestRunTimeElapsedMillis = r5
            r1.latestRunTimeElapsedMillis = r7
            r1.mOriginalLatestRunTimeElapsedMillis = r7
            r10 = r30
            r1.numFailures = r10
            int r15 = r21.getConstraintFlags()
            android.net.NetworkRequest r18 = r21.getRequiredNetwork()
            if (r18 == 0) goto L_0x00c4
            r18 = 268435456(0x10000000, float:2.5243549E-29)
            r15 = r15 | r18
        L_0x00c4:
            r18 = 0
            int r18 = (r5 > r18 ? 1 : (r5 == r18 ? 0 : -1))
            if (r18 == 0) goto L_0x00ce
            r18 = -2147483648(0xffffffff80000000, float:-0.0)
            r15 = r15 | r18
        L_0x00ce:
            r18 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            int r18 = (r7 > r18 ? 1 : (r7 == r18 ? 0 : -1))
            if (r18 == 0) goto L_0x00db
            r18 = 1073741824(0x40000000, float:2.0)
            r15 = r15 | r18
        L_0x00db:
            android.app.job.JobInfo$TriggerContentUri[] r18 = r21.getTriggerContentUris()
            if (r18 == 0) goto L_0x00e5
            r18 = 67108864(0x4000000, float:1.5046328E-36)
            r15 = r15 | r18
        L_0x00e5:
            r1.requiredConstraints = r15
            r18 = -1811939313(0xffffffff9400000f, float:-6.46236E-27)
            r19 = r0
            r0 = r15 & r18
            r1.mRequiredConstraintsOfInterest = r0
            int r0 = r21.getFlags()
            r17 = 1
            r0 = r0 & 1
            if (r0 == 0) goto L_0x00fd
            r0 = r17
            goto L_0x00fe
        L_0x00fd:
            r0 = 0
        L_0x00fe:
            r1.mReadyNotDozing = r0
            r2 = r35
            r1.mLastSuccessfulRunTime = r2
            r2 = r37
            r1.mLastFailedRunTime = r2
            r2 = r39
            r1.mInternalFlags = r2
            r20.updateEstimatedNetworkBytesLocked()
            android.net.NetworkRequest r0 = r21.getRequiredNetwork()
            if (r0 == 0) goto L_0x0120
            android.net.NetworkRequest r0 = r21.getRequiredNetwork()
            android.net.NetworkCapabilities r0 = r0.networkCapabilities
            int r3 = r1.sourceUid
            r0.setSingleUid(r3)
        L_0x0120:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.job.controllers.JobStatus.<init>(android.app.job.JobInfo, int, int, java.lang.String, int, int, long, java.lang.String, int, long, long, long, long, int):void");
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public JobStatus(com.android.server.job.controllers.JobStatus r22) {
        /*
            r21 = this;
            r0 = r22
            r1 = r21
            android.app.job.JobInfo r2 = r22.getJob()
            int r3 = r22.getUid()
            int r4 = r0.targetSdkVersion
            java.lang.String r5 = r22.getSourcePackageName()
            int r6 = r22.getSourceUserId()
            int r7 = r22.getStandbyBucket()
            long r8 = r22.getBaseHeartbeat()
            java.lang.String r10 = r22.getSourceTag()
            int r11 = r22.getNumFailures()
            long r12 = r22.getEarliestRunTime()
            long r14 = r22.getLatestRunTimeElapsed()
            long r16 = r22.getLastSuccessfulRunTime()
            long r18 = r22.getLastFailedRunTime()
            int r20 = r22.getInternalFlags()
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r10, r11, r12, r14, r16, r18, r20)
            android.util.Pair<java.lang.Long, java.lang.Long> r1 = r0.mPersistedUtcTimes
            r2 = r21
            r2.mPersistedUtcTimes = r1
            android.util.Pair<java.lang.Long, java.lang.Long> r1 = r0.mPersistedUtcTimes
            if (r1 == 0) goto L_0x005a
            boolean r1 = DEBUG
            if (r1 == 0) goto L_0x005a
            java.lang.RuntimeException r1 = new java.lang.RuntimeException
            java.lang.String r3 = "here"
            r1.<init>(r3)
            java.lang.String r3 = "JobSchedulerService"
            java.lang.String r4 = "Cloning job with persisted run times"
            android.util.Slog.i(r3, r4, r1)
        L_0x005a:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.job.controllers.JobStatus.<init>(com.android.server.job.controllers.JobStatus):void");
    }

    /* JADX INFO: this call moved to the top of the method (can break code semantics) */
    public JobStatus(JobInfo job2, int callingUid2, String sourcePkgName, int sourceUserId2, int standbyBucket2, long baseHeartbeat2, String sourceTag2, long earliestRunTimeElapsedMillis2, long latestRunTimeElapsedMillis2, long lastSuccessfulRunTime, long lastFailedRunTime, Pair<Long, Long> persistedExecutionTimesUTC, int innerFlags) {
        this(job2, callingUid2, resolveTargetSdkVersion(job2), sourcePkgName, sourceUserId2, standbyBucket2, baseHeartbeat2, sourceTag2, 0, earliestRunTimeElapsedMillis2, latestRunTimeElapsedMillis2, lastSuccessfulRunTime, lastFailedRunTime, innerFlags);
        Pair<Long, Long> pair = persistedExecutionTimesUTC;
        this.mPersistedUtcTimes = pair;
        if (pair != null && DEBUG) {
            Slog.i(TAG, "+ restored job with RTC times because of bad boot clock");
        }
    }

    /* JADX WARNING: Illegal instructions before constructor call */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public JobStatus(com.android.server.job.controllers.JobStatus r22, long r23, long r25, long r27, int r29, long r30, long r32) {
        /*
            r21 = this;
            r0 = r22
            r1 = r21
            r8 = r23
            r12 = r25
            r14 = r27
            r11 = r29
            r16 = r30
            r18 = r32
            android.app.job.JobInfo r2 = r0.job
            int r3 = r22.getUid()
            android.app.job.JobInfo r4 = r0.job
            int r4 = resolveTargetSdkVersion(r4)
            java.lang.String r5 = r22.getSourcePackageName()
            int r6 = r22.getSourceUserId()
            int r7 = r22.getStandbyBucket()
            java.lang.String r10 = r22.getSourceTag()
            int r20 = r22.getInternalFlags()
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r10, r11, r12, r14, r16, r18, r20)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.job.controllers.JobStatus.<init>(com.android.server.job.controllers.JobStatus, long, long, long, int, long, long):void");
    }

    public static JobStatus createFromJobInfo(JobInfo job2, int callingUid2, String sourcePkg, int sourceUserId2, String tag2) {
        long latestRunTimeElapsedMillis2;
        long earliestRunTimeElapsedMillis2;
        long currentHeartbeat;
        int i = sourceUserId2;
        long elapsedNow = JobSchedulerService.sElapsedRealtimeClock.millis();
        if (job2.isPeriodic()) {
            long period = Math.max(JobInfo.getMinPeriodMillis(), Math.min(31536000000L, job2.getIntervalMillis()));
            long latestRunTimeElapsedMillis3 = elapsedNow + period;
            earliestRunTimeElapsedMillis2 = latestRunTimeElapsedMillis3 - Math.max(JobInfo.getMinFlexMillis(), Math.min(period, job2.getFlexMillis()));
            latestRunTimeElapsedMillis2 = latestRunTimeElapsedMillis3;
        } else {
            earliestRunTimeElapsedMillis2 = job2.hasEarlyConstraint() ? job2.getMinLatencyMillis() + elapsedNow : 0;
            latestRunTimeElapsedMillis2 = job2.hasLateConstraint() ? job2.getMaxExecutionDelayMillis() + elapsedNow : NO_LATEST_RUNTIME;
        }
        String jobPackage = sourcePkg != null ? sourcePkg : job2.getService().getPackageName();
        int standbyBucket2 = JobSchedulerService.standbyBucketForPackage(jobPackage, i, elapsedNow);
        JobSchedulerInternal js = (JobSchedulerInternal) LocalServices.getService(JobSchedulerInternal.class);
        if (js != null) {
            currentHeartbeat = js.baseHeartbeatForApp(jobPackage, i, standbyBucket2);
        } else {
            currentHeartbeat = 0;
        }
        JobSchedulerInternal jobSchedulerInternal = js;
        int i2 = standbyBucket2;
        long j = elapsedNow;
        String str = jobPackage;
        return new JobStatus(job2, callingUid2, resolveTargetSdkVersion(job2), sourcePkg, sourceUserId2, standbyBucket2, currentHeartbeat, tag2, 0, earliestRunTimeElapsedMillis2, latestRunTimeElapsedMillis2, 0, 0, 0);
    }

    public void enqueueWorkLocked(IActivityManager am, JobWorkItem work) {
        if (this.pendingWork == null) {
            this.pendingWork = new ArrayList<>();
        }
        work.setWorkId(this.nextPendingWorkId);
        this.nextPendingWorkId++;
        if (work.getIntent() != null && GrantedUriPermissions.checkGrantFlags(work.getIntent().getFlags())) {
            work.setGrants(GrantedUriPermissions.createFromIntent(am, work.getIntent(), this.sourceUid, this.sourcePackageName, this.sourceUserId, toShortString()));
        }
        this.pendingWork.add(work);
        updateEstimatedNetworkBytesLocked();
    }

    public JobWorkItem dequeueWorkLocked() {
        ArrayList<JobWorkItem> arrayList = this.pendingWork;
        if (arrayList == null || arrayList.size() <= 0) {
            return null;
        }
        JobWorkItem work = this.pendingWork.remove(0);
        if (work != null) {
            if (this.executingWork == null) {
                this.executingWork = new ArrayList<>();
            }
            this.executingWork.add(work);
            work.bumpDeliveryCount();
        }
        updateEstimatedNetworkBytesLocked();
        return work;
    }

    public boolean hasWorkLocked() {
        ArrayList<JobWorkItem> arrayList = this.pendingWork;
        return (arrayList != null && arrayList.size() > 0) || hasExecutingWorkLocked();
    }

    public boolean hasExecutingWorkLocked() {
        ArrayList<JobWorkItem> arrayList = this.executingWork;
        return arrayList != null && arrayList.size() > 0;
    }

    private static void ungrantWorkItem(IActivityManager am, JobWorkItem work) {
        if (work.getGrants() != null) {
            ((GrantedUriPermissions) work.getGrants()).revoke(am);
        }
    }

    public boolean completeWorkLocked(IActivityManager am, int workId) {
        ArrayList<JobWorkItem> arrayList = this.executingWork;
        if (arrayList == null) {
            return false;
        }
        int N = arrayList.size();
        for (int i = 0; i < N; i++) {
            JobWorkItem work = this.executingWork.get(i);
            if (work.getWorkId() == workId) {
                this.executingWork.remove(i);
                ungrantWorkItem(am, work);
                return true;
            }
        }
        return false;
    }

    private static void ungrantWorkList(IActivityManager am, ArrayList<JobWorkItem> list) {
        if (list != null) {
            int N = list.size();
            for (int i = 0; i < N; i++) {
                ungrantWorkItem(am, list.get(i));
            }
        }
    }

    public void stopTrackingJobLocked(IActivityManager am, JobStatus incomingJob) {
        if (incomingJob != null) {
            ArrayList<JobWorkItem> arrayList = this.executingWork;
            if (arrayList != null && arrayList.size() > 0) {
                incomingJob.pendingWork = this.executingWork;
            }
            if (incomingJob.pendingWork == null) {
                incomingJob.pendingWork = this.pendingWork;
            } else {
                ArrayList<JobWorkItem> arrayList2 = this.pendingWork;
                if (arrayList2 != null && arrayList2.size() > 0) {
                    incomingJob.pendingWork.addAll(this.pendingWork);
                }
            }
            this.pendingWork = null;
            this.executingWork = null;
            incomingJob.nextPendingWorkId = this.nextPendingWorkId;
            incomingJob.updateEstimatedNetworkBytesLocked();
        } else {
            ungrantWorkList(am, this.pendingWork);
            this.pendingWork = null;
            ungrantWorkList(am, this.executingWork);
            this.executingWork = null;
        }
        updateEstimatedNetworkBytesLocked();
    }

    public void prepareLocked(IActivityManager am) {
        if (this.prepared) {
            Slog.wtf(TAG, "Already prepared: " + this);
            return;
        }
        this.prepared = true;
        this.unpreparedPoint = null;
        ClipData clip = this.job.getClipData();
        if (clip != null) {
            this.uriPerms = GrantedUriPermissions.createFromClip(am, clip, this.sourceUid, this.sourcePackageName, this.sourceUserId, this.job.getClipGrantFlags(), toShortString());
        }
    }

    public void unprepareLocked(IActivityManager am) {
        if (!this.prepared) {
            Slog.wtf(TAG, "Hasn't been prepared: " + this);
            Throwable th = this.unpreparedPoint;
            if (th != null) {
                Slog.e(TAG, "Was already unprepared at ", th);
                return;
            }
            return;
        }
        this.prepared = false;
        this.unpreparedPoint = new Throwable().fillInStackTrace();
        GrantedUriPermissions grantedUriPermissions = this.uriPerms;
        if (grantedUriPermissions != null) {
            grantedUriPermissions.revoke(am);
            this.uriPerms = null;
        }
    }

    public boolean isPreparedLocked() {
        return this.prepared;
    }

    public JobInfo getJob() {
        return this.job;
    }

    public int getJobId() {
        return this.job.getId();
    }

    public int getTargetSdkVersion() {
        return this.targetSdkVersion;
    }

    public void printUniqueId(PrintWriter pw) {
        UserHandle.formatUid(pw, this.callingUid);
        pw.print(SliceClientPermissions.SliceAuthority.DELIMITER);
        pw.print(this.job.getId());
    }

    public int getNumFailures() {
        return this.numFailures;
    }

    public ComponentName getServiceComponent() {
        return this.job.getService();
    }

    public String getSourcePackageName() {
        return this.sourcePackageName;
    }

    public int getSourceUid() {
        return this.sourceUid;
    }

    public int getSourceUserId() {
        return this.sourceUserId;
    }

    public int getUserId() {
        return UserHandle.getUserId(this.callingUid);
    }

    public int getStandbyBucket() {
        return this.standbyBucket;
    }

    public long getBaseHeartbeat() {
        return this.baseHeartbeat;
    }

    public void setStandbyBucket(int newBucket) {
        this.standbyBucket = newBucket;
    }

    public long getWhenStandbyDeferred() {
        return this.whenStandbyDeferred;
    }

    public void setWhenStandbyDeferred(long now) {
        this.whenStandbyDeferred = now;
    }

    public String getSourceTag() {
        return this.sourceTag;
    }

    public int getUid() {
        return this.callingUid;
    }

    public String getBatteryName() {
        return this.batteryName;
    }

    public String getTag() {
        return this.tag;
    }

    public int getPriority() {
        return this.job.getPriority();
    }

    public int getFlags() {
        return this.job.getFlags();
    }

    public int getInternalFlags() {
        return this.mInternalFlags;
    }

    public void addInternalFlags(int flags) {
        this.mInternalFlags |= flags;
    }

    public int getSatisfiedConstraintFlags() {
        return this.satisfiedConstraints;
    }

    public void maybeAddForegroundExemption(Predicate<Integer> uidForegroundChecker) {
        if (!this.job.hasEarlyConstraint() && !this.job.hasLateConstraint() && (this.mInternalFlags & 1) == 0 && uidForegroundChecker.test(Integer.valueOf(getSourceUid()))) {
            addInternalFlags(1);
        }
    }

    private void updateEstimatedNetworkBytesLocked() {
        this.totalNetworkBytes = computeEstimatedNetworkBytesLocked();
    }

    private long computeEstimatedNetworkBytesLocked() {
        long networkBytes = this.job.getEstimatedNetworkBytes();
        if (networkBytes == -1) {
            return -1;
        }
        long totalNetworkBytes2 = 0 + networkBytes;
        if (this.pendingWork != null) {
            for (int i = 0; i < this.pendingWork.size(); i++) {
                long networkBytes2 = this.pendingWork.get(i).getEstimatedNetworkBytes();
                if (networkBytes2 == -1) {
                    return -1;
                }
                totalNetworkBytes2 += networkBytes2;
            }
        }
        return totalNetworkBytes2;
    }

    public long getEstimatedNetworkBytes() {
        return this.totalNetworkBytes;
    }

    public boolean hasConnectivityConstraint() {
        return (this.requiredConstraints & CONSTRAINT_CONNECTIVITY) != 0;
    }

    public boolean hasChargingConstraint() {
        return (this.requiredConstraints & 1) != 0;
    }

    public boolean hasBatteryNotLowConstraint() {
        return (this.requiredConstraints & 2) != 0;
    }

    public boolean hasPowerConstraint() {
        return (this.requiredConstraints & 3) != 0;
    }

    public boolean hasStorageNotLowConstraint() {
        return (this.requiredConstraints & 8) != 0;
    }

    public boolean hasTimingDelayConstraint() {
        return (this.requiredConstraints & Integer.MIN_VALUE) != 0;
    }

    public boolean hasDeadlineConstraint() {
        return (this.requiredConstraints & CONSTRAINT_DEADLINE) != 0;
    }

    public boolean hasIdleConstraint() {
        return (this.requiredConstraints & 4) != 0;
    }

    public boolean hasContentTriggerConstraint() {
        return (this.requiredConstraints & 67108864) != 0;
    }

    public long getTriggerContentUpdateDelay() {
        long time = this.job.getTriggerContentUpdateDelay();
        if (time < 0) {
            return DEFAULT_TRIGGER_UPDATE_DELAY;
        }
        return Math.max(time, 500);
    }

    public long getTriggerContentMaxDelay() {
        long time = this.job.getTriggerContentMaxDelay();
        if (time < 0) {
            return DEFAULT_TRIGGER_MAX_DELAY;
        }
        return Math.max(time, 1000);
    }

    public boolean isPersisted() {
        return this.job.isPersisted();
    }

    public long getEarliestRunTime() {
        return this.earliestRunTimeElapsedMillis;
    }

    public long getLatestRunTimeElapsed() {
        return this.latestRunTimeElapsedMillis;
    }

    public long getOriginalLatestRunTimeElapsed() {
        return this.mOriginalLatestRunTimeElapsedMillis;
    }

    public void setOriginalLatestRunTimeElapsed(long latestRunTimeElapsed) {
        this.mOriginalLatestRunTimeElapsedMillis = latestRunTimeElapsed;
    }

    public float getFractionRunTime() {
        long now = JobSchedulerService.sElapsedRealtimeClock.millis();
        if (this.earliestRunTimeElapsedMillis == 0 && this.latestRunTimeElapsedMillis == NO_LATEST_RUNTIME) {
            return 1.0f;
        }
        long j = this.earliestRunTimeElapsedMillis;
        if (j != 0) {
            long j2 = this.latestRunTimeElapsedMillis;
            if (j2 == NO_LATEST_RUNTIME) {
                if (now >= j) {
                    return 1.0f;
                }
                return 0.0f;
            } else if (now <= j) {
                return 0.0f;
            } else {
                if (now >= j2) {
                    return 1.0f;
                }
                return ((float) (now - j)) / ((float) (j2 - j));
            }
        } else if (now >= this.latestRunTimeElapsedMillis) {
            return 1.0f;
        } else {
            return 0.0f;
        }
    }

    public Pair<Long, Long> getPersistedUtcTimes() {
        return this.mPersistedUtcTimes;
    }

    public void clearPersistedUtcTimes() {
        this.mPersistedUtcTimes = null;
    }

    /* access modifiers changed from: package-private */
    public boolean setChargingConstraintSatisfied(boolean state) {
        return setConstraintSatisfied(1, state);
    }

    /* access modifiers changed from: package-private */
    public boolean setBatteryNotLowConstraintSatisfied(boolean state) {
        return setConstraintSatisfied(2, state);
    }

    /* access modifiers changed from: package-private */
    public boolean setStorageNotLowConstraintSatisfied(boolean state) {
        return setConstraintSatisfied(8, state);
    }

    /* access modifiers changed from: package-private */
    public boolean setTimingDelayConstraintSatisfied(boolean state) {
        return setConstraintSatisfied(Integer.MIN_VALUE, state);
    }

    /* access modifiers changed from: package-private */
    public boolean setDeadlineConstraintSatisfied(boolean state) {
        boolean z = false;
        if (!setConstraintSatisfied(CONSTRAINT_DEADLINE, state)) {
            return false;
        }
        if (!this.job.isPeriodic() && hasDeadlineConstraint() && state) {
            z = true;
        }
        this.mReadyDeadlineSatisfied = z;
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean setIdleConstraintSatisfied(boolean state) {
        return setConstraintSatisfied(4, state);
    }

    /* access modifiers changed from: package-private */
    public boolean setConnectivityConstraintSatisfied(boolean state) {
        return setConstraintSatisfied(CONSTRAINT_CONNECTIVITY, state);
    }

    /* access modifiers changed from: package-private */
    public boolean setContentTriggerConstraintSatisfied(boolean state) {
        return setConstraintSatisfied(67108864, state);
    }

    /* access modifiers changed from: package-private */
    public boolean setDeviceNotDozingConstraintSatisfied(boolean state, boolean whitelisted) {
        this.dozeWhitelisted = whitelisted;
        boolean z = false;
        if (!setConstraintSatisfied(33554432, state)) {
            return false;
        }
        if (state || (this.job.getFlags() & 1) != 0) {
            z = true;
        }
        this.mReadyNotDozing = z;
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean setBackgroundNotRestrictedConstraintSatisfied(boolean state) {
        if (!setConstraintSatisfied(4194304, state)) {
            return false;
        }
        this.mReadyNotRestrictedInBg = state;
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean setQuotaConstraintSatisfied(boolean state) {
        if (!setConstraintSatisfied(16777216, state)) {
            return false;
        }
        this.mReadyWithinQuota = state;
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean setUidActive(boolean newActiveState) {
        if (newActiveState == this.uidActive) {
            return false;
        }
        this.uidActive = newActiveState;
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean setConstraintSatisfied(int constraint, boolean state) {
        int i = 0;
        if (((this.satisfiedConstraints & constraint) != 0) == state) {
            return false;
        }
        int i2 = this.satisfiedConstraints & (~constraint);
        if (state) {
            i = constraint;
        }
        this.satisfiedConstraints = i | i2;
        this.mSatisfiedConstraintsOfInterest = this.satisfiedConstraints & CONSTRAINTS_OF_INTEREST;
        return true;
    }

    /* access modifiers changed from: package-private */
    public boolean isConstraintSatisfied(int constraint) {
        return (this.satisfiedConstraints & constraint) != 0;
    }

    /* access modifiers changed from: package-private */
    public boolean clearTrackingController(int which) {
        int i = this.trackingControllers;
        if ((i & which) == 0) {
            return false;
        }
        this.trackingControllers = i & (~which);
        return true;
    }

    /* access modifiers changed from: package-private */
    public void setTrackingController(int which) {
        this.trackingControllers |= which;
    }

    public long getLastSuccessfulRunTime() {
        return this.mLastSuccessfulRunTime;
    }

    public long getLastFailedRunTime() {
        return this.mLastFailedRunTime;
    }

    public boolean isReady() {
        return isReady(this.mSatisfiedConstraintsOfInterest);
    }

    /* access modifiers changed from: package-private */
    public boolean wouldBeReadyWithConstraint(int constraint) {
        boolean oldValue = false;
        int satisfied = this.mSatisfiedConstraintsOfInterest;
        if (constraint == 4194304) {
            oldValue = this.mReadyNotRestrictedInBg;
            this.mReadyNotRestrictedInBg = true;
        } else if (constraint == 16777216) {
            oldValue = this.mReadyWithinQuota;
            this.mReadyWithinQuota = true;
        } else if (constraint == 33554432) {
            oldValue = this.mReadyNotDozing;
            this.mReadyNotDozing = true;
        } else if (constraint != CONSTRAINT_DEADLINE) {
            satisfied |= constraint;
        } else {
            oldValue = this.mReadyDeadlineSatisfied;
            this.mReadyDeadlineSatisfied = true;
        }
        boolean toReturn = isReady(satisfied);
        if (constraint == 4194304) {
            this.mReadyNotRestrictedInBg = oldValue;
        } else if (constraint == 16777216) {
            this.mReadyWithinQuota = oldValue;
        } else if (constraint == 33554432) {
            this.mReadyNotDozing = oldValue;
        } else if (constraint == CONSTRAINT_DEADLINE) {
            this.mReadyDeadlineSatisfied = oldValue;
        }
        return toReturn;
    }

    private boolean isReady(int satisfiedConstraints2) {
        if (!this.mReadyWithinQuota || !this.mReadyNotDozing || !this.mReadyNotRestrictedInBg) {
            return false;
        }
        if (this.mReadyDeadlineSatisfied || isConstraintsSatisfied(satisfiedConstraints2)) {
            return true;
        }
        return false;
    }

    public boolean isConstraintsSatisfied() {
        return isConstraintsSatisfied(this.mSatisfiedConstraintsOfInterest);
    }

    private boolean isConstraintsSatisfied(int satisfiedConstraints2) {
        int i = this.overrideState;
        if (i == 2) {
            return true;
        }
        int sat = satisfiedConstraints2;
        if (i == 1) {
            sat |= this.requiredConstraints & SOFT_OVERRIDE_CONSTRAINTS;
        }
        int i2 = this.mRequiredConstraintsOfInterest;
        if ((sat & i2) == i2) {
            return true;
        }
        return false;
    }

    public boolean matches(int uid, int jobId) {
        return this.job.getId() == jobId && this.callingUid == uid;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("JobStatus{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" #");
        UserHandle.formatUid(sb, this.callingUid);
        sb.append(SliceClientPermissions.SliceAuthority.DELIMITER);
        sb.append(this.job.getId());
        sb.append(' ');
        sb.append(this.batteryName);
        sb.append(" u=");
        sb.append(getUserId());
        sb.append(" s=");
        sb.append(getSourceUid());
        if (!(this.earliestRunTimeElapsedMillis == 0 && this.latestRunTimeElapsedMillis == NO_LATEST_RUNTIME)) {
            long now = JobSchedulerService.sElapsedRealtimeClock.millis();
            sb.append(" TIME=");
            StringBuilder sb2 = sb;
            long j = now;
            formatRunTime(sb2, this.earliestRunTimeElapsedMillis, 0, j);
            sb.append(":");
            formatRunTime(sb2, this.latestRunTimeElapsedMillis, (long) NO_LATEST_RUNTIME, j);
        }
        if (this.job.getRequiredNetwork() != null) {
            sb.append(" NET");
        }
        if (this.job.isRequireCharging()) {
            sb.append(" CHARGING");
        }
        if (this.job.isRequireBatteryNotLow()) {
            sb.append(" BATNOTLOW");
        }
        if (this.job.isRequireStorageNotLow()) {
            sb.append(" STORENOTLOW");
        }
        if (this.job.isRequireDeviceIdle()) {
            sb.append(" IDLE");
        }
        if (this.job.isPeriodic()) {
            sb.append(" PERIODIC");
        }
        if (this.job.isPersisted()) {
            sb.append(" PERSISTED");
        }
        if ((this.satisfiedConstraints & 33554432) == 0) {
            sb.append(" WAIT:DEV_NOT_DOZING");
        }
        if (this.job.getTriggerContentUris() != null) {
            sb.append(" URIS=");
            sb.append(Arrays.toString(this.job.getTriggerContentUris()));
        }
        if (this.numFailures != 0) {
            sb.append(" failures=");
            sb.append(this.numFailures);
        }
        if (isReady()) {
            sb.append(" READY");
        }
        sb.append("}");
        return sb.toString();
    }

    private void formatRunTime(PrintWriter pw, long runtime, long defaultValue, long now) {
        if (runtime == defaultValue) {
            pw.print("none");
        } else {
            TimeUtils.formatDuration(runtime - now, pw);
        }
    }

    private void formatRunTime(StringBuilder sb, long runtime, long defaultValue, long now) {
        if (runtime == defaultValue) {
            sb.append("none");
        } else {
            TimeUtils.formatDuration(runtime - now, sb);
        }
    }

    public String toShortString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" #");
        UserHandle.formatUid(sb, this.callingUid);
        sb.append(SliceClientPermissions.SliceAuthority.DELIMITER);
        sb.append(this.job.getId());
        sb.append(' ');
        sb.append(this.batteryName);
        return sb.toString();
    }

    public String toShortStringExceptUniqueId() {
        return Integer.toHexString(System.identityHashCode(this)) + ' ' + this.batteryName;
    }

    public void writeToShortProto(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        proto.write(1120986464257L, this.callingUid);
        proto.write(1120986464258L, this.job.getId());
        proto.write(1138166333443L, this.batteryName);
        proto.end(token);
    }

    /* access modifiers changed from: package-private */
    public void dumpConstraints(PrintWriter pw, int constraints) {
        if ((constraints & 1) != 0) {
            pw.print(" CHARGING");
        }
        if ((constraints & 2) != 0) {
            pw.print(" BATTERY_NOT_LOW");
        }
        if ((constraints & 8) != 0) {
            pw.print(" STORAGE_NOT_LOW");
        }
        if ((Integer.MIN_VALUE & constraints) != 0) {
            pw.print(" TIMING_DELAY");
        }
        if ((CONSTRAINT_DEADLINE & constraints) != 0) {
            pw.print(" DEADLINE");
        }
        if ((constraints & 4) != 0) {
            pw.print(" IDLE");
        }
        if ((CONSTRAINT_CONNECTIVITY & constraints) != 0) {
            pw.print(" CONNECTIVITY");
        }
        if ((67108864 & constraints) != 0) {
            pw.print(" CONTENT_TRIGGER");
        }
        if ((33554432 & constraints) != 0) {
            pw.print(" DEVICE_NOT_DOZING");
        }
        if ((4194304 & constraints) != 0) {
            pw.print(" BACKGROUND_NOT_RESTRICTED");
        }
        if ((16777216 & constraints) != 0) {
            pw.print(" WITHIN_QUOTA");
        }
        if (constraints != 0) {
            pw.print(" [0x");
            pw.print(Integer.toHexString(constraints));
            pw.print("]");
        }
    }

    private int getProtoConstraint(int constraint) {
        if (constraint == Integer.MIN_VALUE) {
            return 4;
        }
        if (constraint == 4) {
            return 6;
        }
        if (constraint == 8) {
            return 3;
        }
        if (constraint == 4194304) {
            return 11;
        }
        if (constraint == 16777216) {
            return 10;
        }
        if (constraint == 33554432) {
            return 9;
        }
        if (constraint == 67108864) {
            return 8;
        }
        if (constraint == CONSTRAINT_CONNECTIVITY) {
            return 7;
        }
        if (constraint == CONSTRAINT_DEADLINE) {
            return 5;
        }
        if (constraint != 1) {
            return constraint != 2 ? 0 : 2;
        }
        return 1;
    }

    /* access modifiers changed from: package-private */
    public void dumpConstraints(ProtoOutputStream proto, long fieldId, int constraints) {
        if ((constraints & 1) != 0) {
            proto.write(fieldId, 1);
        }
        if ((constraints & 2) != 0) {
            proto.write(fieldId, 2);
        }
        if ((constraints & 8) != 0) {
            proto.write(fieldId, 3);
        }
        if ((Integer.MIN_VALUE & constraints) != 0) {
            proto.write(fieldId, 4);
        }
        if ((CONSTRAINT_DEADLINE & constraints) != 0) {
            proto.write(fieldId, 5);
        }
        if ((constraints & 4) != 0) {
            proto.write(fieldId, 6);
        }
        if ((CONSTRAINT_CONNECTIVITY & constraints) != 0) {
            proto.write(fieldId, 7);
        }
        if ((67108864 & constraints) != 0) {
            proto.write(fieldId, 8);
        }
        if ((33554432 & constraints) != 0) {
            proto.write(fieldId, 9);
        }
        if ((16777216 & constraints) != 0) {
            proto.write(fieldId, 10);
        }
        if ((4194304 & constraints) != 0) {
            proto.write(fieldId, 11);
        }
    }

    private void dumpJobWorkItem(PrintWriter pw, String prefix, JobWorkItem work, int index) {
        pw.print(prefix);
        pw.print("  #");
        pw.print(index);
        pw.print(": #");
        pw.print(work.getWorkId());
        pw.print(" ");
        pw.print(work.getDeliveryCount());
        pw.print("x ");
        pw.println(work.getIntent());
        if (work.getGrants() != null) {
            pw.print(prefix);
            pw.println("  URI grants:");
            ((GrantedUriPermissions) work.getGrants()).dump(pw, prefix + "    ");
        }
    }

    private void dumpJobWorkItem(ProtoOutputStream proto, long fieldId, JobWorkItem work) {
        long token = proto.start(fieldId);
        proto.write(1120986464257L, work.getWorkId());
        proto.write(1120986464258L, work.getDeliveryCount());
        if (work.getIntent() != null) {
            work.getIntent().writeToProto(proto, 1146756268035L);
        }
        Object grants = work.getGrants();
        if (grants != null) {
            ((GrantedUriPermissions) grants).dump(proto, 1146756268036L);
        }
        proto.end(token);
    }

    /* access modifiers changed from: package-private */
    public String getBucketName() {
        return bucketName(this.standbyBucket);
    }

    static String bucketName(int standbyBucket2) {
        if (standbyBucket2 == 0) {
            return "ACTIVE";
        }
        if (standbyBucket2 == 1) {
            return "WORKING_SET";
        }
        if (standbyBucket2 == 2) {
            return "FREQUENT";
        }
        if (standbyBucket2 == 3) {
            return "RARE";
        }
        if (standbyBucket2 == 4) {
            return "NEVER";
        }
        return "Unknown: " + standbyBucket2;
    }

    private static int resolveTargetSdkVersion(JobInfo job2) {
        return ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).getPackageTargetSdkVersion(job2.getService().getPackageName());
    }

    public void dump(PrintWriter pw, String prefix, boolean full, long elapsedRealtimeMillis) {
        PrintWriter printWriter = pw;
        String str = prefix;
        long j = elapsedRealtimeMillis;
        pw.print(prefix);
        UserHandle.formatUid(printWriter, this.callingUid);
        printWriter.print(" tag=");
        printWriter.println(this.tag);
        pw.print(prefix);
        printWriter.print("Source: uid=");
        UserHandle.formatUid(printWriter, getSourceUid());
        printWriter.print(" user=");
        printWriter.print(getSourceUserId());
        printWriter.print(" pkg=");
        printWriter.println(getSourcePackageName());
        if (full) {
            pw.print(prefix);
            printWriter.println("JobInfo:");
            pw.print(prefix);
            printWriter.print("  Service: ");
            printWriter.println(this.job.getService().flattenToShortString());
            if (this.job.isPeriodic()) {
                pw.print(prefix);
                printWriter.print("  PERIODIC: interval=");
                TimeUtils.formatDuration(this.job.getIntervalMillis(), printWriter);
                printWriter.print(" flex=");
                TimeUtils.formatDuration(this.job.getFlexMillis(), printWriter);
                pw.println();
            }
            if (this.job.isPersisted()) {
                pw.print(prefix);
                printWriter.println("  PERSISTED");
            }
            if (this.job.getPriority() != 0) {
                pw.print(prefix);
                printWriter.print("  Priority: ");
                printWriter.println(JobInfo.getPriorityString(this.job.getPriority()));
            }
            if (this.job.getFlags() != 0) {
                pw.print(prefix);
                printWriter.print("  Flags: ");
                printWriter.println(Integer.toHexString(this.job.getFlags()));
            }
            if (getInternalFlags() != 0) {
                pw.print(prefix);
                printWriter.print("  Internal flags: ");
                printWriter.print(Integer.toHexString(getInternalFlags()));
                if ((getInternalFlags() & 1) != 0) {
                    printWriter.print(" HAS_FOREGROUND_EXEMPTION");
                }
                pw.println();
            }
            pw.print(prefix);
            printWriter.print("  Requires: charging=");
            printWriter.print(this.job.isRequireCharging());
            printWriter.print(" batteryNotLow=");
            printWriter.print(this.job.isRequireBatteryNotLow());
            printWriter.print(" deviceIdle=");
            printWriter.println(this.job.isRequireDeviceIdle());
            if (this.job.getTriggerContentUris() != null) {
                pw.print(prefix);
                printWriter.println("  Trigger content URIs:");
                for (JobInfo.TriggerContentUri trig : this.job.getTriggerContentUris()) {
                    pw.print(prefix);
                    printWriter.print("    ");
                    printWriter.print(Integer.toHexString(trig.getFlags()));
                    printWriter.print(' ');
                    printWriter.println(trig.getUri());
                }
                if (this.job.getTriggerContentUpdateDelay() >= 0) {
                    pw.print(prefix);
                    printWriter.print("  Trigger update delay: ");
                    TimeUtils.formatDuration(this.job.getTriggerContentUpdateDelay(), printWriter);
                    pw.println();
                }
                if (this.job.getTriggerContentMaxDelay() >= 0) {
                    pw.print(prefix);
                    printWriter.print("  Trigger max delay: ");
                    TimeUtils.formatDuration(this.job.getTriggerContentMaxDelay(), printWriter);
                    pw.println();
                }
            }
            if (this.job.getExtras() != null && !this.job.getExtras().maybeIsEmpty()) {
                pw.print(prefix);
                printWriter.print("  Extras: ");
                printWriter.println(this.job.getExtras().toShortString());
            }
            if (this.job.getTransientExtras() != null && !this.job.getTransientExtras().maybeIsEmpty()) {
                pw.print(prefix);
                printWriter.print("  Transient extras: ");
                printWriter.println(this.job.getTransientExtras().toShortString());
            }
            if (this.job.getClipData() != null) {
                pw.print(prefix);
                printWriter.print("  Clip data: ");
                StringBuilder b = new StringBuilder(128);
                this.job.getClipData().toShortString(b);
                printWriter.println(b);
            }
            if (this.uriPerms != null) {
                pw.print(prefix);
                printWriter.println("  Granted URI permissions:");
                this.uriPerms.dump(printWriter, str + "  ");
            }
            if (this.job.getRequiredNetwork() != null) {
                pw.print(prefix);
                printWriter.print("  Network type: ");
                printWriter.println(this.job.getRequiredNetwork());
            }
            if (this.totalNetworkBytes != -1) {
                pw.print(prefix);
                printWriter.print("  Network bytes: ");
                printWriter.println(this.totalNetworkBytes);
            }
            if (this.job.getMinLatencyMillis() != 0) {
                pw.print(prefix);
                printWriter.print("  Minimum latency: ");
                TimeUtils.formatDuration(this.job.getMinLatencyMillis(), printWriter);
                pw.println();
            }
            if (this.job.getMaxExecutionDelayMillis() != 0) {
                pw.print(prefix);
                printWriter.print("  Max execution delay: ");
                TimeUtils.formatDuration(this.job.getMaxExecutionDelayMillis(), printWriter);
                pw.println();
            }
            pw.print(prefix);
            printWriter.print("  Backoff: policy=");
            printWriter.print(this.job.getBackoffPolicy());
            printWriter.print(" initial=");
            TimeUtils.formatDuration(this.job.getInitialBackoffMillis(), printWriter);
            pw.println();
            if (this.job.hasEarlyConstraint()) {
                pw.print(prefix);
                printWriter.println("  Has early constraint");
            }
            if (this.job.hasLateConstraint()) {
                pw.print(prefix);
                printWriter.println("  Has late constraint");
            }
        }
        pw.print(prefix);
        printWriter.print("Required constraints:");
        dumpConstraints(printWriter, this.requiredConstraints);
        pw.println();
        if (full) {
            pw.print(prefix);
            printWriter.print("Satisfied constraints:");
            dumpConstraints(printWriter, this.satisfiedConstraints);
            pw.println();
            pw.print(prefix);
            printWriter.print("Unsatisfied constraints:");
            dumpConstraints(printWriter, (this.requiredConstraints | 16777216) & (~this.satisfiedConstraints));
            pw.println();
            if (this.dozeWhitelisted) {
                pw.print(prefix);
                printWriter.println("Doze whitelisted: true");
            }
            if (this.uidActive) {
                pw.print(prefix);
                printWriter.println("Uid: active");
            }
            if (this.job.isExemptedFromAppStandby()) {
                pw.print(prefix);
                printWriter.println("Is exempted from app standby");
            }
        }
        if (this.trackingControllers != 0) {
            pw.print(prefix);
            printWriter.print("Tracking:");
            if ((this.trackingControllers & 1) != 0) {
                printWriter.print(" BATTERY");
            }
            if ((this.trackingControllers & 2) != 0) {
                printWriter.print(" CONNECTIVITY");
            }
            if ((this.trackingControllers & 4) != 0) {
                printWriter.print(" CONTENT");
            }
            if ((this.trackingControllers & 8) != 0) {
                printWriter.print(" IDLE");
            }
            if ((this.trackingControllers & 16) != 0) {
                printWriter.print(" STORAGE");
            }
            if ((32 & this.trackingControllers) != 0) {
                printWriter.print(" TIME");
            }
            if ((this.trackingControllers & 64) != 0) {
                printWriter.print(" QUOTA");
            }
            pw.println();
        }
        pw.print(prefix);
        printWriter.println("Implicit constraints:");
        pw.print(prefix);
        printWriter.print("  readyNotDozing: ");
        printWriter.println(this.mReadyNotDozing);
        pw.print(prefix);
        printWriter.print("  readyNotRestrictedInBg: ");
        printWriter.println(this.mReadyNotRestrictedInBg);
        if (!this.job.isPeriodic() && hasDeadlineConstraint()) {
            pw.print(prefix);
            printWriter.print("  readyDeadlineSatisfied: ");
            printWriter.println(this.mReadyDeadlineSatisfied);
        }
        if (this.changedAuthorities != null) {
            pw.print(prefix);
            printWriter.println("Changed authorities:");
            for (int i = 0; i < this.changedAuthorities.size(); i++) {
                pw.print(prefix);
                printWriter.print("  ");
                printWriter.println(this.changedAuthorities.valueAt(i));
            }
            if (this.changedUris != null) {
                pw.print(prefix);
                printWriter.println("Changed URIs:");
                for (int i2 = 0; i2 < this.changedUris.size(); i2++) {
                    pw.print(prefix);
                    printWriter.print("  ");
                    printWriter.println(this.changedUris.valueAt(i2));
                }
            }
        }
        if (this.network != null) {
            pw.print(prefix);
            printWriter.print("Network: ");
            printWriter.println(this.network);
        }
        ArrayList<JobWorkItem> arrayList = this.pendingWork;
        if (arrayList != null && arrayList.size() > 0) {
            pw.print(prefix);
            printWriter.println("Pending work:");
            for (int i3 = 0; i3 < this.pendingWork.size(); i3++) {
                dumpJobWorkItem(printWriter, str, this.pendingWork.get(i3), i3);
            }
        }
        ArrayList<JobWorkItem> arrayList2 = this.executingWork;
        if (arrayList2 != null && arrayList2.size() > 0) {
            pw.print(prefix);
            printWriter.println("Executing work:");
            for (int i4 = 0; i4 < this.executingWork.size(); i4++) {
                dumpJobWorkItem(printWriter, str, this.executingWork.get(i4), i4);
            }
        }
        pw.print(prefix);
        printWriter.print("Standby bucket: ");
        printWriter.println(getBucketName());
        if (this.standbyBucket > 0) {
            pw.print(prefix);
            printWriter.print("Base heartbeat: ");
            printWriter.println(this.baseHeartbeat);
        }
        if (this.whenStandbyDeferred != 0) {
            pw.print(prefix);
            printWriter.print("  Deferred since: ");
            TimeUtils.formatDuration(this.whenStandbyDeferred, j, printWriter);
            pw.println();
        }
        pw.print(prefix);
        printWriter.print("Enqueue time: ");
        TimeUtils.formatDuration(this.enqueueTime, j, printWriter);
        pw.println();
        pw.print(prefix);
        printWriter.print("Run time: earliest=");
        PrintWriter printWriter2 = pw;
        long j2 = elapsedRealtimeMillis;
        formatRunTime(printWriter2, this.earliestRunTimeElapsedMillis, 0, j2);
        printWriter.print(", latest=");
        formatRunTime(printWriter2, this.latestRunTimeElapsedMillis, (long) NO_LATEST_RUNTIME, j2);
        printWriter.print(", original latest=");
        formatRunTime(printWriter2, this.mOriginalLatestRunTimeElapsedMillis, (long) NO_LATEST_RUNTIME, j2);
        pw.println();
        if (this.numFailures != 0) {
            pw.print(prefix);
            printWriter.print("Num failures: ");
            printWriter.println(this.numFailures);
        }
        Time t = new Time();
        if (this.mLastSuccessfulRunTime != 0) {
            pw.print(prefix);
            printWriter.print("Last successful run: ");
            t.set(this.mLastSuccessfulRunTime);
            printWriter.println(t.format("%Y-%m-%d %H:%M:%S"));
        }
        if (this.mLastFailedRunTime != 0) {
            pw.print(prefix);
            printWriter.print("Last failed run: ");
            t.set(this.mLastFailedRunTime);
            printWriter.println(t.format("%Y-%m-%d %H:%M:%S"));
        }
    }

    public void dump(ProtoOutputStream proto, long fieldId, boolean full, long elapsedRealtimeMillis) {
        ProtoOutputStream protoOutputStream = proto;
        long token = proto.start(fieldId);
        long j = 1120986464257L;
        protoOutputStream.write(1120986464257L, this.callingUid);
        protoOutputStream.write(1138166333442L, this.tag);
        protoOutputStream.write(1120986464259L, getSourceUid());
        protoOutputStream.write(1120986464260L, getSourceUserId());
        protoOutputStream.write(1138166333445L, getSourcePackageName());
        protoOutputStream.write(1112396529688L, getInternalFlags());
        if (full) {
            long jiToken = protoOutputStream.start(1146756268038L);
            this.job.getService().writeToProto(protoOutputStream, 1146756268033L);
            protoOutputStream.write(1133871366146L, this.job.isPeriodic());
            protoOutputStream.write(1112396529667L, this.job.getIntervalMillis());
            protoOutputStream.write(1112396529668L, this.job.getFlexMillis());
            protoOutputStream.write(1133871366149L, this.job.isPersisted());
            protoOutputStream.write(1172526071814L, this.job.getPriority());
            protoOutputStream.write(1120986464263L, this.job.getFlags());
            protoOutputStream.write(1133871366152L, this.job.isRequireCharging());
            protoOutputStream.write(1133871366153L, this.job.isRequireBatteryNotLow());
            protoOutputStream.write(1133871366154L, this.job.isRequireDeviceIdle());
            if (this.job.getTriggerContentUris() != null) {
                int i = 0;
                while (i < this.job.getTriggerContentUris().length) {
                    long tcuToken = protoOutputStream.start(2246267895819L);
                    JobInfo.TriggerContentUri trig = this.job.getTriggerContentUris()[i];
                    protoOutputStream.write(j, trig.getFlags());
                    Uri u = trig.getUri();
                    if (u != null) {
                        protoOutputStream.write(1138166333442L, u.toString());
                    }
                    protoOutputStream.end(tcuToken);
                    i++;
                    j = 1120986464257L;
                }
                if (this.job.getTriggerContentUpdateDelay() >= 0) {
                    protoOutputStream.write(1112396529676L, this.job.getTriggerContentUpdateDelay());
                }
                if (this.job.getTriggerContentMaxDelay() >= 0) {
                    protoOutputStream.write(1112396529677L, this.job.getTriggerContentMaxDelay());
                }
            }
            if (this.job.getExtras() != null && !this.job.getExtras().maybeIsEmpty()) {
                this.job.getExtras().writeToProto(protoOutputStream, 1146756268046L);
            }
            if (this.job.getTransientExtras() != null && !this.job.getTransientExtras().maybeIsEmpty()) {
                this.job.getTransientExtras().writeToProto(protoOutputStream, 1146756268047L);
            }
            if (this.job.getClipData() != null) {
                this.job.getClipData().writeToProto(protoOutputStream, 1146756268048L);
            }
            GrantedUriPermissions grantedUriPermissions = this.uriPerms;
            if (grantedUriPermissions != null) {
                grantedUriPermissions.dump(protoOutputStream, 1146756268049L);
            }
            if (this.job.getRequiredNetwork() != null) {
                this.job.getRequiredNetwork().writeToProto(protoOutputStream, 1146756268050L);
            }
            long j2 = this.totalNetworkBytes;
            if (j2 != -1) {
                protoOutputStream.write(1112396529683L, j2);
            }
            protoOutputStream.write(1112396529684L, this.job.getMinLatencyMillis());
            protoOutputStream.write(1112396529685L, this.job.getMaxExecutionDelayMillis());
            long bpToken = protoOutputStream.start(1146756268054L);
            protoOutputStream.write(1159641169921L, this.job.getBackoffPolicy());
            protoOutputStream.write(1112396529666L, this.job.getInitialBackoffMillis());
            protoOutputStream.end(bpToken);
            protoOutputStream.write(1133871366167L, this.job.hasEarlyConstraint());
            protoOutputStream.write(1133871366168L, this.job.hasLateConstraint());
            protoOutputStream.end(jiToken);
        }
        dumpConstraints(protoOutputStream, 2259152797703L, this.requiredConstraints);
        if (full) {
            dumpConstraints(protoOutputStream, 2259152797704L, this.satisfiedConstraints);
            dumpConstraints(protoOutputStream, 2259152797705L, (this.requiredConstraints | 16777216) & (~this.satisfiedConstraints));
            protoOutputStream.write(1133871366154L, this.dozeWhitelisted);
            protoOutputStream.write(1133871366170L, this.uidActive);
            protoOutputStream.write(1133871366171L, this.job.isExemptedFromAppStandby());
        }
        if ((this.trackingControllers & 1) != 0) {
            protoOutputStream.write(2259152797707L, 0);
        }
        if ((this.trackingControllers & 2) != 0) {
            protoOutputStream.write(2259152797707L, 1);
        }
        if ((this.trackingControllers & 4) != 0) {
            protoOutputStream.write(2259152797707L, 2);
        }
        if ((this.trackingControllers & 8) != 0) {
            protoOutputStream.write(2259152797707L, 3);
        }
        if ((this.trackingControllers & 16) != 0) {
            protoOutputStream.write(2259152797707L, 4);
        }
        if ((this.trackingControllers & 32) != 0) {
            protoOutputStream.write(2259152797707L, 5);
        }
        if ((this.trackingControllers & 64) != 0) {
            protoOutputStream.write(2259152797707L, 6);
        }
        long icToken = protoOutputStream.start(1146756268057L);
        protoOutputStream.write(1133871366145L, this.mReadyNotDozing);
        protoOutputStream.write(1133871366146L, this.mReadyNotRestrictedInBg);
        protoOutputStream.end(icToken);
        if (this.changedAuthorities != null) {
            for (int k = 0; k < this.changedAuthorities.size(); k++) {
                protoOutputStream.write(2237677961228L, this.changedAuthorities.valueAt(k));
            }
        }
        if (this.changedUris != null) {
            for (int i2 = 0; i2 < this.changedUris.size(); i2++) {
                protoOutputStream.write(2237677961229L, this.changedUris.valueAt(i2).toString());
            }
        }
        Network network2 = this.network;
        if (network2 != null) {
            network2.writeToProto(protoOutputStream, 1146756268046L);
        }
        ArrayList<JobWorkItem> arrayList = this.pendingWork;
        if (arrayList != null && arrayList.size() > 0) {
            for (int i3 = 0; i3 < this.pendingWork.size(); i3++) {
                dumpJobWorkItem(protoOutputStream, 2246267895823L, this.pendingWork.get(i3));
            }
        }
        ArrayList<JobWorkItem> arrayList2 = this.executingWork;
        if (arrayList2 != null && arrayList2.size() > 0) {
            for (int i4 = 0; i4 < this.executingWork.size(); i4++) {
                dumpJobWorkItem(protoOutputStream, 2246267895824L, this.executingWork.get(i4));
            }
        }
        protoOutputStream.write(1159641169937L, this.standbyBucket);
        protoOutputStream.write(1112396529682L, elapsedRealtimeMillis - this.enqueueTime);
        long j3 = this.earliestRunTimeElapsedMillis;
        if (j3 == 0) {
            protoOutputStream.write(1176821039123L, 0);
        } else {
            protoOutputStream.write(1176821039123L, j3 - elapsedRealtimeMillis);
        }
        long j4 = this.latestRunTimeElapsedMillis;
        if (j4 == NO_LATEST_RUNTIME) {
            protoOutputStream.write(1176821039124L, 0);
        } else {
            protoOutputStream.write(1176821039124L, j4 - elapsedRealtimeMillis);
        }
        protoOutputStream.write(1120986464277L, this.numFailures);
        protoOutputStream.write(1112396529686L, this.mLastSuccessfulRunTime);
        protoOutputStream.write(1112396529687L, this.mLastFailedRunTime);
        protoOutputStream.end(token);
    }
}
