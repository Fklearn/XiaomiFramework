package com.android.server.job;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.app.job.JobInfo;
import android.content.ComponentName;
import android.content.Context;
import android.net.NetworkRequest;
import android.os.Environment;
import android.os.Handler;
import android.os.PersistableBundle;
import android.os.SystemClock;
import android.os.UserHandle;
import android.text.format.DateUtils;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.Xml;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.BitUtils;
import com.android.server.IoThread;
import com.android.server.LocalServices;
import com.android.server.content.SyncJobService;
import com.android.server.job.JobSchedulerInternal;
import com.android.server.job.JobStore;
import com.android.server.job.controllers.JobStatus;
import com.android.server.net.watchlist.WatchlistLoggingHandler;
import com.android.server.pm.PackageManagerService;
import com.android.server.pm.Settings;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public final class JobStore {
    /* access modifiers changed from: private */
    public static final boolean DEBUG = JobSchedulerService.DEBUG;
    private static final int JOBS_FILE_VERSION = 0;
    private static final long JOB_PERSIST_DELAY = 2000;
    private static final String TAG = "JobStore";
    private static final String XML_TAG_EXTRAS = "extras";
    private static final String XML_TAG_ONEOFF = "one-off";
    private static final String XML_TAG_PARAMS_CONSTRAINTS = "constraints";
    private static final String XML_TAG_PERIODIC = "periodic";
    private static JobStore sSingleton;
    private static final Object sSingletonLock = new Object();
    final Context mContext;
    private final Handler mIoHandler = IoThread.getHandler();
    final JobSet mJobSet;
    /* access modifiers changed from: private */
    public final AtomicFile mJobsFile;
    final Object mLock;
    /* access modifiers changed from: private */
    public JobSchedulerInternal.JobStorePersistStats mPersistInfo = new JobSchedulerInternal.JobStorePersistStats();
    private boolean mRtcGood;
    /* access modifiers changed from: private */
    @GuardedBy({"mWriteScheduleLock"})
    public boolean mWriteInProgress;
    private final Runnable mWriteRunnable = new Runnable() {
        public void run() {
            long startElapsed = JobSchedulerService.sElapsedRealtimeClock.millis();
            List<JobStatus> storeCopy = new ArrayList<>();
            synchronized (JobStore.this.mWriteScheduleLock) {
                boolean unused = JobStore.this.mWriteScheduled = false;
            }
            synchronized (JobStore.this.mLock) {
                JobStore.this.mJobSet.forEachJob((Predicate<JobStatus>) null, (Consumer<JobStatus>) new Consumer(storeCopy) {
                    private final /* synthetic */ List f$0;

                    {
                        this.f$0 = r1;
                    }

                    public final void accept(Object obj) {
                        JobStore.AnonymousClass1.lambda$run$0(this.f$0, (JobStatus) obj);
                    }
                });
            }
            writeJobsMapImpl(storeCopy);
            if (JobStore.DEBUG) {
                Slog.v(JobStore.TAG, "Finished writing, took " + (JobSchedulerService.sElapsedRealtimeClock.millis() - startElapsed) + "ms");
            }
            synchronized (JobStore.this.mWriteScheduleLock) {
                boolean unused2 = JobStore.this.mWriteInProgress = false;
                JobStore.this.mWriteScheduleLock.notifyAll();
            }
        }

        static /* synthetic */ void lambda$run$0(List storeCopy, JobStatus job) {
            if (job.isPersisted()) {
                storeCopy.add(new JobStatus(job));
            }
        }

        /* JADX WARNING: Removed duplicated region for block: B:28:0x00d7 A[Catch:{ IOException -> 0x00dd, XmlPullParserException -> 0x00ce, all -> 0x00ca, all -> 0x0105 }] */
        /* JADX WARNING: Removed duplicated region for block: B:33:0x00e6 A[Catch:{ IOException -> 0x00dd, XmlPullParserException -> 0x00ce, all -> 0x00ca, all -> 0x0105 }] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        private void writeJobsMapImpl(java.util.List<com.android.server.job.controllers.JobStatus> r17) {
            /*
                r16 = this;
                r1 = r16
                java.lang.String r0 = "job"
                java.lang.String r2 = "job-info"
                java.lang.String r3 = "JobStore"
                r4 = 0
                r5 = 0
                r6 = 0
                long r7 = android.os.SystemClock.uptimeMillis()     // Catch:{ IOException -> 0x00dd, XmlPullParserException -> 0x00ce, all -> 0x00ca }
                java.io.ByteArrayOutputStream r9 = new java.io.ByteArrayOutputStream     // Catch:{ IOException -> 0x00dd, XmlPullParserException -> 0x00ce, all -> 0x00ca }
                r9.<init>()     // Catch:{ IOException -> 0x00dd, XmlPullParserException -> 0x00ce, all -> 0x00ca }
                com.android.internal.util.FastXmlSerializer r10 = new com.android.internal.util.FastXmlSerializer     // Catch:{ IOException -> 0x00dd, XmlPullParserException -> 0x00ce, all -> 0x00ca }
                r10.<init>()     // Catch:{ IOException -> 0x00dd, XmlPullParserException -> 0x00ce, all -> 0x00ca }
                java.nio.charset.Charset r11 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ IOException -> 0x00dd, XmlPullParserException -> 0x00ce, all -> 0x00ca }
                java.lang.String r11 = r11.name()     // Catch:{ IOException -> 0x00dd, XmlPullParserException -> 0x00ce, all -> 0x00ca }
                r10.setOutput(r9, r11)     // Catch:{ IOException -> 0x00dd, XmlPullParserException -> 0x00ce, all -> 0x00ca }
                r11 = 1
                java.lang.Boolean r12 = java.lang.Boolean.valueOf(r11)     // Catch:{ IOException -> 0x00dd, XmlPullParserException -> 0x00ce, all -> 0x00ca }
                r13 = 0
                r10.startDocument(r13, r12)     // Catch:{ IOException -> 0x00dd, XmlPullParserException -> 0x00ce, all -> 0x00ca }
                java.lang.String r12 = "http://xmlpull.org/v1/doc/features.html#indent-output"
                r10.setFeature(r12, r11)     // Catch:{ IOException -> 0x00dd, XmlPullParserException -> 0x00ce, all -> 0x00ca }
                r10.startTag(r13, r2)     // Catch:{ IOException -> 0x00dd, XmlPullParserException -> 0x00ce, all -> 0x00ca }
                java.lang.String r11 = "version"
                r12 = 0
                java.lang.String r14 = java.lang.Integer.toString(r12)     // Catch:{ IOException -> 0x00dd, XmlPullParserException -> 0x00ce, all -> 0x00ca }
                r10.attribute(r13, r11, r14)     // Catch:{ IOException -> 0x00dd, XmlPullParserException -> 0x00ce, all -> 0x00ca }
                r11 = r12
            L_0x0042:
                int r12 = r17.size()     // Catch:{ IOException -> 0x00dd, XmlPullParserException -> 0x00ce, all -> 0x00ca }
                if (r11 >= r12) goto L_0x00a6
                r12 = r17
                java.lang.Object r14 = r12.get(r11)     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                com.android.server.job.controllers.JobStatus r14 = (com.android.server.job.controllers.JobStatus) r14     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                boolean r15 = com.android.server.job.JobStore.DEBUG     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                if (r15 == 0) goto L_0x006e
                java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                r15.<init>()     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                java.lang.String r13 = "Saving job "
                r15.append(r13)     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                int r13 = r14.getJobId()     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                r15.append(r13)     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                java.lang.String r13 = r15.toString()     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                android.util.Slog.d(r3, r13)     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
            L_0x006e:
                r13 = 0
                r10.startTag(r13, r0)     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                r1.addAttributesToJobTag(r10, r14)     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                r1.writeConstraintsToXml(r10, r14)     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                r1.writeExecutionCriteriaToXml(r10, r14)     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                android.app.job.JobInfo r13 = r14.getJob()     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                android.os.PersistableBundle r13 = r13.getExtras()     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                r1.writeBundleToXml(r13, r10)     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                r13 = 0
                r10.endTag(r13, r0)     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                int r4 = r4 + 1
                int r13 = r14.getUid()     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                r15 = 1000(0x3e8, float:1.401E-42)
                if (r13 != r15) goto L_0x009e
                int r5 = r5 + 1
                boolean r13 = com.android.server.job.JobStore.isSyncJob(r14)     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                if (r13 == 0) goto L_0x009e
                int r6 = r6 + 1
            L_0x009e:
                int r11 = r11 + 1
                r13 = 0
                goto L_0x0042
            L_0x00a2:
                r0 = move-exception
                goto L_0x00d1
            L_0x00a4:
                r0 = move-exception
                goto L_0x00e0
            L_0x00a6:
                r12 = r17
                r0 = 0
                r10.endTag(r0, r2)     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                r10.endDocument()     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                com.android.server.job.JobStore r0 = com.android.server.job.JobStore.this     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                android.util.AtomicFile r0 = r0.mJobsFile     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                java.io.FileOutputStream r0 = r0.startWrite(r7)     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                byte[] r2 = r9.toByteArray()     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                r0.write(r2)     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                com.android.server.job.JobStore r2 = com.android.server.job.JobStore.this     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                android.util.AtomicFile r2 = r2.mJobsFile     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                r2.finishWrite(r0)     // Catch:{ IOException -> 0x00a4, XmlPullParserException -> 0x00a2 }
                goto L_0x00eb
            L_0x00ca:
                r0 = move-exception
                r12 = r17
                goto L_0x0106
            L_0x00ce:
                r0 = move-exception
                r12 = r17
            L_0x00d1:
                boolean r2 = com.android.server.job.JobStore.DEBUG     // Catch:{ all -> 0x0105 }
                if (r2 == 0) goto L_0x00eb
                java.lang.String r2 = "Error persisting bundle."
                android.util.Slog.d(r3, r2, r0)     // Catch:{ all -> 0x0105 }
                goto L_0x00eb
            L_0x00dd:
                r0 = move-exception
                r12 = r17
            L_0x00e0:
                boolean r2 = com.android.server.job.JobStore.DEBUG     // Catch:{ all -> 0x0105 }
                if (r2 == 0) goto L_0x00eb
                java.lang.String r2 = "Error writing out job data."
                android.util.Slog.v(r3, r2, r0)     // Catch:{ all -> 0x0105 }
            L_0x00eb:
                com.android.server.job.JobStore r0 = com.android.server.job.JobStore.this
                com.android.server.job.JobSchedulerInternal$JobStorePersistStats r0 = r0.mPersistInfo
                r0.countAllJobsSaved = r4
                com.android.server.job.JobStore r0 = com.android.server.job.JobStore.this
                com.android.server.job.JobSchedulerInternal$JobStorePersistStats r0 = r0.mPersistInfo
                r0.countSystemServerJobsSaved = r5
                com.android.server.job.JobStore r0 = com.android.server.job.JobStore.this
                com.android.server.job.JobSchedulerInternal$JobStorePersistStats r0 = r0.mPersistInfo
                r0.countSystemSyncManagerJobsSaved = r6
                return
            L_0x0105:
                r0 = move-exception
            L_0x0106:
                com.android.server.job.JobStore r2 = com.android.server.job.JobStore.this
                com.android.server.job.JobSchedulerInternal$JobStorePersistStats r2 = r2.mPersistInfo
                r2.countAllJobsSaved = r4
                com.android.server.job.JobStore r2 = com.android.server.job.JobStore.this
                com.android.server.job.JobSchedulerInternal$JobStorePersistStats r2 = r2.mPersistInfo
                r2.countSystemServerJobsSaved = r5
                com.android.server.job.JobStore r2 = com.android.server.job.JobStore.this
                com.android.server.job.JobSchedulerInternal$JobStorePersistStats r2 = r2.mPersistInfo
                r2.countSystemSyncManagerJobsSaved = r6
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.job.JobStore.AnonymousClass1.writeJobsMapImpl(java.util.List):void");
        }

        private void addAttributesToJobTag(XmlSerializer out, JobStatus jobStatus) throws IOException {
            out.attribute((String) null, "jobid", Integer.toString(jobStatus.getJobId()));
            out.attribute((String) null, Settings.ATTR_PACKAGE, jobStatus.getServiceComponent().getPackageName());
            out.attribute((String) null, "class", jobStatus.getServiceComponent().getClassName());
            if (jobStatus.getSourcePackageName() != null) {
                out.attribute((String) null, "sourcePackageName", jobStatus.getSourcePackageName());
            }
            if (jobStatus.getSourceTag() != null) {
                out.attribute((String) null, "sourceTag", jobStatus.getSourceTag());
            }
            out.attribute((String) null, "sourceUserId", String.valueOf(jobStatus.getSourceUserId()));
            out.attribute((String) null, WatchlistLoggingHandler.WatchlistEventKeys.UID, Integer.toString(jobStatus.getUid()));
            out.attribute((String) null, "priority", String.valueOf(jobStatus.getPriority()));
            out.attribute((String) null, "flags", String.valueOf(jobStatus.getFlags()));
            if (jobStatus.getInternalFlags() != 0) {
                out.attribute((String) null, "internalFlags", String.valueOf(jobStatus.getInternalFlags()));
            }
            out.attribute((String) null, "lastSuccessfulRunTime", String.valueOf(jobStatus.getLastSuccessfulRunTime()));
            out.attribute((String) null, "lastFailedRunTime", String.valueOf(jobStatus.getLastFailedRunTime()));
        }

        private void writeBundleToXml(PersistableBundle extras, XmlSerializer out) throws IOException, XmlPullParserException {
            out.startTag((String) null, JobStore.XML_TAG_EXTRAS);
            deepCopyBundle(extras, 10).saveToXml(out);
            out.endTag((String) null, JobStore.XML_TAG_EXTRAS);
        }

        private PersistableBundle deepCopyBundle(PersistableBundle bundle, int maxDepth) {
            if (maxDepth <= 0) {
                return null;
            }
            PersistableBundle copy = (PersistableBundle) bundle.clone();
            for (String key : bundle.keySet()) {
                Object o = copy.get(key);
                if (o instanceof PersistableBundle) {
                    copy.putPersistableBundle(key, deepCopyBundle((PersistableBundle) o, maxDepth - 1));
                }
            }
            return copy;
        }

        private void writeConstraintsToXml(XmlSerializer out, JobStatus jobStatus) throws IOException {
            out.startTag((String) null, JobStore.XML_TAG_PARAMS_CONSTRAINTS);
            if (jobStatus.hasConnectivityConstraint()) {
                NetworkRequest network = jobStatus.getJob().getRequiredNetwork();
                out.attribute((String) null, "net-capabilities", Long.toString(BitUtils.packBits(network.networkCapabilities.getCapabilities())));
                out.attribute((String) null, "net-unwanted-capabilities", Long.toString(BitUtils.packBits(network.networkCapabilities.getUnwantedCapabilities())));
                out.attribute((String) null, "net-transport-types", Long.toString(BitUtils.packBits(network.networkCapabilities.getTransportTypes())));
            }
            if (jobStatus.hasIdleConstraint()) {
                out.attribute((String) null, "idle", Boolean.toString(true));
            }
            if (jobStatus.hasChargingConstraint()) {
                out.attribute((String) null, "charging", Boolean.toString(true));
            }
            if (jobStatus.hasBatteryNotLowConstraint()) {
                out.attribute((String) null, "battery-not-low", Boolean.toString(true));
            }
            if (jobStatus.hasStorageNotLowConstraint()) {
                out.attribute((String) null, "storage-not-low", Boolean.toString(true));
            }
            out.endTag((String) null, JobStore.XML_TAG_PARAMS_CONSTRAINTS);
        }

        private void writeExecutionCriteriaToXml(XmlSerializer out, JobStatus jobStatus) throws IOException {
            long delayWallclock;
            long deadlineWallclock;
            JobInfo job = jobStatus.getJob();
            if (jobStatus.getJob().isPeriodic()) {
                out.startTag((String) null, JobStore.XML_TAG_PERIODIC);
                out.attribute((String) null, "period", Long.toString(job.getIntervalMillis()));
                out.attribute((String) null, "flex", Long.toString(job.getFlexMillis()));
            } else {
                out.startTag((String) null, JobStore.XML_TAG_ONEOFF);
            }
            Pair<Long, Long> utcJobTimes = jobStatus.getPersistedUtcTimes();
            if (JobStore.DEBUG && utcJobTimes != null) {
                Slog.i(JobStore.TAG, "storing original UTC timestamps for " + jobStatus);
            }
            long nowRTC = JobSchedulerService.sSystemClock.millis();
            long nowElapsed = JobSchedulerService.sElapsedRealtimeClock.millis();
            if (jobStatus.hasDeadlineConstraint()) {
                if (utcJobTimes == null) {
                    deadlineWallclock = (jobStatus.getLatestRunTimeElapsed() - nowElapsed) + nowRTC;
                } else {
                    deadlineWallclock = ((Long) utcJobTimes.second).longValue();
                }
                out.attribute((String) null, "deadline", Long.toString(deadlineWallclock));
            }
            if (jobStatus.hasTimingDelayConstraint()) {
                if (utcJobTimes == null) {
                    delayWallclock = (jobStatus.getEarliestRunTime() - nowElapsed) + nowRTC;
                } else {
                    delayWallclock = ((Long) utcJobTimes.first).longValue();
                }
                out.attribute((String) null, "delay", Long.toString(delayWallclock));
            }
            if (!(jobStatus.getJob().getInitialBackoffMillis() == 30000 && jobStatus.getJob().getBackoffPolicy() == 1)) {
                out.attribute((String) null, "backoff-policy", Integer.toString(job.getBackoffPolicy()));
                out.attribute((String) null, "initial-backoff", Long.toString(job.getInitialBackoffMillis()));
            }
            if (job.isPeriodic()) {
                out.endTag((String) null, JobStore.XML_TAG_PERIODIC);
            } else {
                out.endTag((String) null, JobStore.XML_TAG_ONEOFF);
            }
        }
    };
    final Object mWriteScheduleLock;
    /* access modifiers changed from: private */
    @GuardedBy({"mWriteScheduleLock"})
    public boolean mWriteScheduled;
    private final long mXmlTimestamp;

    static JobStore initAndGet(JobSchedulerService jobManagerService) {
        JobStore jobStore;
        synchronized (sSingletonLock) {
            if (sSingleton == null) {
                sSingleton = new JobStore(jobManagerService.getContext(), jobManagerService.getLock(), Environment.getDataDirectory());
            }
            jobStore = sSingleton;
        }
        return jobStore;
    }

    @VisibleForTesting
    public static JobStore initAndGetForTesting(Context context, File dataDir) {
        JobStore jobStoreUnderTest = new JobStore(context, new Object(), dataDir);
        jobStoreUnderTest.clear();
        return jobStoreUnderTest;
    }

    private JobStore(Context context, Object lock, File dataDir) {
        this.mLock = lock;
        this.mWriteScheduleLock = new Object();
        this.mContext = context;
        File jobDir = new File(new File(dataDir, "system"), "job");
        jobDir.mkdirs();
        this.mJobsFile = new AtomicFile(new File(jobDir, "jobs.xml"), "jobs");
        this.mJobSet = new JobSet();
        this.mXmlTimestamp = this.mJobsFile.getLastModifiedTime();
        this.mRtcGood = JobSchedulerService.sSystemClock.millis() > this.mXmlTimestamp;
        readJobMapFromDisk(this.mJobSet, this.mRtcGood);
    }

    public boolean jobTimesInflatedValid() {
        return this.mRtcGood;
    }

    public boolean clockNowValidToInflate(long now) {
        return now >= this.mXmlTimestamp;
    }

    public void getRtcCorrectedJobsLocked(ArrayList<JobStatus> toAdd, ArrayList<JobStatus> toRemove) {
        forEachJob(new Consumer(JobSchedulerService.sElapsedRealtimeClock.millis(), ActivityManager.getService(), toAdd, toRemove) {
            private final /* synthetic */ long f$0;
            private final /* synthetic */ IActivityManager f$1;
            private final /* synthetic */ ArrayList f$2;
            private final /* synthetic */ ArrayList f$3;

            {
                this.f$0 = r1;
                this.f$1 = r3;
                this.f$2 = r4;
                this.f$3 = r5;
            }

            public final void accept(Object obj) {
                JobStore.lambda$getRtcCorrectedJobsLocked$0(this.f$0, this.f$1, this.f$2, this.f$3, (JobStatus) obj);
            }
        });
    }

    static /* synthetic */ void lambda$getRtcCorrectedJobsLocked$0(long elapsedNow, IActivityManager am, ArrayList toAdd, ArrayList toRemove, JobStatus job) {
        Pair<Long, Long> utcTimes = job.getPersistedUtcTimes();
        if (utcTimes != null) {
            Pair<Long, Long> elapsedRuntimes = convertRtcBoundsToElapsed(utcTimes, elapsedNow);
            JobStatus newJob = new JobStatus(job, job.getBaseHeartbeat(), ((Long) elapsedRuntimes.first).longValue(), ((Long) elapsedRuntimes.second).longValue(), 0, job.getLastSuccessfulRunTime(), job.getLastFailedRunTime());
            newJob.prepareLocked(am);
            toAdd.add(newJob);
            toRemove.add(job);
            return;
        }
        long j = elapsedNow;
        IActivityManager iActivityManager = am;
        ArrayList arrayList = toAdd;
    }

    public boolean add(JobStatus jobStatus) {
        boolean replaced = this.mJobSet.remove(jobStatus);
        this.mJobSet.add(jobStatus);
        if (jobStatus.isPersisted()) {
            maybeWriteStatusToDiskAsync();
        }
        if (DEBUG) {
            Slog.d(TAG, "Added job status to store: " + jobStatus);
        }
        return replaced;
    }

    /* access modifiers changed from: package-private */
    public boolean containsJob(JobStatus jobStatus) {
        return this.mJobSet.contains(jobStatus);
    }

    public int size() {
        return this.mJobSet.size();
    }

    public JobSchedulerInternal.JobStorePersistStats getPersistStats() {
        return this.mPersistInfo;
    }

    public int countJobsForUid(int uid) {
        return this.mJobSet.countJobsForUid(uid);
    }

    public boolean remove(JobStatus jobStatus, boolean writeBack) {
        boolean removed = this.mJobSet.remove(jobStatus);
        if (removed) {
            if (writeBack && jobStatus.isPersisted()) {
                maybeWriteStatusToDiskAsync();
            }
            return removed;
        } else if (!DEBUG) {
            return false;
        } else {
            Slog.d(TAG, "Couldn't remove job: didn't exist: " + jobStatus);
            return false;
        }
    }

    public void removeJobsOfNonUsers(int[] whitelist) {
        this.mJobSet.removeJobsOfNonUsers(whitelist);
    }

    @VisibleForTesting
    public void clear() {
        this.mJobSet.clear();
        maybeWriteStatusToDiskAsync();
    }

    public List<JobStatus> getJobsByUser(int userHandle) {
        return this.mJobSet.getJobsByUser(userHandle);
    }

    public List<JobStatus> getJobsByUid(int uid) {
        return this.mJobSet.getJobsByUid(uid);
    }

    public JobStatus getJobByUidAndJobId(int uid, int jobId) {
        return this.mJobSet.get(uid, jobId);
    }

    public void forEachJob(Consumer<JobStatus> functor) {
        this.mJobSet.forEachJob((Predicate<JobStatus>) null, functor);
    }

    public void forEachJob(Predicate<JobStatus> filterPredicate, Consumer<JobStatus> functor) {
        this.mJobSet.forEachJob(filterPredicate, functor);
    }

    public void forEachJob(int uid, Consumer<JobStatus> functor) {
        this.mJobSet.forEachJob(uid, functor);
    }

    public void forEachJobForSourceUid(int sourceUid, Consumer<JobStatus> functor) {
        this.mJobSet.forEachJobForSourceUid(sourceUid, functor);
    }

    private void maybeWriteStatusToDiskAsync() {
        synchronized (this.mWriteScheduleLock) {
            if (!this.mWriteScheduled) {
                if (DEBUG) {
                    Slog.v(TAG, "Scheduling persist of jobs to disk.");
                }
                this.mIoHandler.postDelayed(this.mWriteRunnable, JOB_PERSIST_DELAY);
                this.mWriteInProgress = true;
                this.mWriteScheduled = true;
            }
        }
    }

    @VisibleForTesting
    public void readJobMapFromDisk(JobSet jobSet, boolean rtcGood) {
        new ReadJobMapFromDiskRunnable(jobSet, rtcGood).run();
    }

    @VisibleForTesting
    public boolean waitForWriteToCompleteForTesting(long maxWaitMillis) {
        long start = SystemClock.uptimeMillis();
        long end = start + maxWaitMillis;
        synchronized (this.mWriteScheduleLock) {
            while (this.mWriteInProgress) {
                long now = SystemClock.uptimeMillis();
                if (now >= end) {
                    return false;
                }
                try {
                    this.mWriteScheduleLock.wait((now - start) + maxWaitMillis);
                } catch (InterruptedException e) {
                }
            }
            return true;
        }
    }

    /* access modifiers changed from: private */
    public static Pair<Long, Long> convertRtcBoundsToElapsed(Pair<Long, Long> rtcTimes, long nowElapsed) {
        long earliest;
        long nowWallclock = JobSchedulerService.sSystemClock.millis();
        if (((Long) rtcTimes.first).longValue() > 0) {
            earliest = Math.max(((Long) rtcTimes.first).longValue() - nowWallclock, 0) + nowElapsed;
        } else {
            earliest = 0;
        }
        long longValue = ((Long) rtcTimes.second).longValue();
        long latest = JobStatus.NO_LATEST_RUNTIME;
        if (longValue < JobStatus.NO_LATEST_RUNTIME) {
            latest = nowElapsed + Math.max(((Long) rtcTimes.second).longValue() - nowWallclock, 0);
        }
        return Pair.create(Long.valueOf(earliest), Long.valueOf(latest));
    }

    /* access modifiers changed from: private */
    public static boolean isSyncJob(JobStatus status) {
        return SyncJobService.class.getName().equals(status.getServiceComponent().getClassName());
    }

    private final class ReadJobMapFromDiskRunnable implements Runnable {
        private final JobSet jobSet;
        private final boolean rtcGood;

        ReadJobMapFromDiskRunnable(JobSet jobSet2, boolean rtcIsGood) {
            this.jobSet = jobSet2;
            this.rtcGood = rtcIsGood;
        }

        /* Debug info: failed to restart local var, previous not found, register: 13 */
        /* JADX WARNING: Code restructure failed: missing block: B:20:0x005e, code lost:
            if (com.android.server.job.JobStore.access$500(r13.this$0).countAllJobsLoaded < 0) goto L_0x0060;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x0060, code lost:
            com.android.server.job.JobStore.access$500(r13.this$0).countAllJobsLoaded = r0;
            com.android.server.job.JobStore.access$500(r13.this$0).countSystemServerJobsLoaded = r1;
            com.android.server.job.JobStore.access$500(r13.this$0).countSystemSyncManagerJobsLoaded = r2;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:33:0x008f, code lost:
            if (com.android.server.job.JobStore.access$500(r13.this$0).countAllJobsLoaded >= 0) goto L_0x00ab;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:40:0x00a8, code lost:
            if (com.android.server.job.JobStore.access$500(r13.this$0).countAllJobsLoaded >= 0) goto L_0x00ab;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:41:0x00ab, code lost:
            android.util.Slog.i(com.android.server.job.JobStore.TAG, "Read " + r0 + " jobs");
         */
        /* JADX WARNING: Code restructure failed: missing block: B:42:0x00c6, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
                r13 = this;
                r0 = 0
                r1 = 0
                r2 = 0
                com.android.server.job.JobStore r3 = com.android.server.job.JobStore.this     // Catch:{ FileNotFoundException -> 0x0092, IOException | XmlPullParserException -> 0x007e }
                android.util.AtomicFile r3 = r3.mJobsFile     // Catch:{ FileNotFoundException -> 0x0092, IOException | XmlPullParserException -> 0x007e }
                java.io.FileInputStream r3 = r3.openRead()     // Catch:{ FileNotFoundException -> 0x0092, IOException | XmlPullParserException -> 0x007e }
                com.android.server.job.JobStore r4 = com.android.server.job.JobStore.this     // Catch:{ FileNotFoundException -> 0x0092, IOException | XmlPullParserException -> 0x007e }
                java.lang.Object r4 = r4.mLock     // Catch:{ FileNotFoundException -> 0x0092, IOException | XmlPullParserException -> 0x007e }
                monitor-enter(r4)     // Catch:{ FileNotFoundException -> 0x0092, IOException | XmlPullParserException -> 0x007e }
                boolean r5 = r13.rtcGood     // Catch:{ all -> 0x0079 }
                java.util.List r5 = r13.readJobMapImpl(r3, r5)     // Catch:{ all -> 0x0079 }
                if (r5 == 0) goto L_0x0052
                java.time.Clock r6 = com.android.server.job.JobSchedulerService.sElapsedRealtimeClock     // Catch:{ all -> 0x0079 }
                long r6 = r6.millis()     // Catch:{ all -> 0x0079 }
                android.app.IActivityManager r8 = android.app.ActivityManager.getService()     // Catch:{ all -> 0x0079 }
                r9 = 0
            L_0x0025:
                int r10 = r5.size()     // Catch:{ all -> 0x0079 }
                if (r9 >= r10) goto L_0x0052
                java.lang.Object r10 = r5.get(r9)     // Catch:{ all -> 0x0079 }
                com.android.server.job.controllers.JobStatus r10 = (com.android.server.job.controllers.JobStatus) r10     // Catch:{ all -> 0x0079 }
                r10.prepareLocked(r8)     // Catch:{ all -> 0x0079 }
                r10.enqueueTime = r6     // Catch:{ all -> 0x0079 }
                com.android.server.job.JobStore$JobSet r11 = r13.jobSet     // Catch:{ all -> 0x0079 }
                r11.add(r10)     // Catch:{ all -> 0x0079 }
                int r0 = r0 + 1
                int r11 = r10.getUid()     // Catch:{ all -> 0x0079 }
                r12 = 1000(0x3e8, float:1.401E-42)
                if (r11 != r12) goto L_0x004f
                int r1 = r1 + 1
                boolean r11 = com.android.server.job.JobStore.isSyncJob(r10)     // Catch:{ all -> 0x0079 }
                if (r11 == 0) goto L_0x004f
                int r2 = r2 + 1
            L_0x004f:
                int r9 = r9 + 1
                goto L_0x0025
            L_0x0052:
                monitor-exit(r4)     // Catch:{ all -> 0x0079 }
                r3.close()     // Catch:{ FileNotFoundException -> 0x0092, IOException | XmlPullParserException -> 0x007e }
                com.android.server.job.JobStore r3 = com.android.server.job.JobStore.this
                com.android.server.job.JobSchedulerInternal$JobStorePersistStats r3 = r3.mPersistInfo
                int r3 = r3.countAllJobsLoaded
                if (r3 >= 0) goto L_0x00ab
            L_0x0060:
                com.android.server.job.JobStore r3 = com.android.server.job.JobStore.this
                com.android.server.job.JobSchedulerInternal$JobStorePersistStats r3 = r3.mPersistInfo
                r3.countAllJobsLoaded = r0
                com.android.server.job.JobStore r3 = com.android.server.job.JobStore.this
                com.android.server.job.JobSchedulerInternal$JobStorePersistStats r3 = r3.mPersistInfo
                r3.countSystemServerJobsLoaded = r1
                com.android.server.job.JobStore r3 = com.android.server.job.JobStore.this
                com.android.server.job.JobSchedulerInternal$JobStorePersistStats r3 = r3.mPersistInfo
                r3.countSystemSyncManagerJobsLoaded = r2
                goto L_0x00ab
            L_0x0079:
                r5 = move-exception
                monitor-exit(r4)     // Catch:{ all -> 0x0079 }
                throw r5     // Catch:{ FileNotFoundException -> 0x0092, IOException | XmlPullParserException -> 0x007e }
            L_0x007c:
                r3 = move-exception
                goto L_0x00c7
            L_0x007e:
                r3 = move-exception
                java.lang.String r4 = "JobStore"
                java.lang.String r5 = "Error jobstore xml."
                android.util.Slog.wtf(r4, r5, r3)     // Catch:{ all -> 0x007c }
                com.android.server.job.JobStore r3 = com.android.server.job.JobStore.this
                com.android.server.job.JobSchedulerInternal$JobStorePersistStats r3 = r3.mPersistInfo
                int r3 = r3.countAllJobsLoaded
                if (r3 >= 0) goto L_0x00ab
                goto L_0x0060
            L_0x0092:
                r3 = move-exception
                boolean r4 = com.android.server.job.JobStore.DEBUG     // Catch:{ all -> 0x007c }
                if (r4 == 0) goto L_0x00a0
                java.lang.String r4 = "JobStore"
                java.lang.String r5 = "Could not find jobs file, probably there was nothing to load."
                android.util.Slog.d(r4, r5)     // Catch:{ all -> 0x007c }
            L_0x00a0:
                com.android.server.job.JobStore r3 = com.android.server.job.JobStore.this
                com.android.server.job.JobSchedulerInternal$JobStorePersistStats r3 = r3.mPersistInfo
                int r3 = r3.countAllJobsLoaded
                if (r3 >= 0) goto L_0x00ab
                goto L_0x0060
            L_0x00ab:
                java.lang.StringBuilder r3 = new java.lang.StringBuilder
                r3.<init>()
                java.lang.String r4 = "Read "
                r3.append(r4)
                r3.append(r0)
                java.lang.String r4 = " jobs"
                r3.append(r4)
                java.lang.String r3 = r3.toString()
                java.lang.String r4 = "JobStore"
                android.util.Slog.i(r4, r3)
                return
            L_0x00c7:
                com.android.server.job.JobStore r4 = com.android.server.job.JobStore.this
                com.android.server.job.JobSchedulerInternal$JobStorePersistStats r4 = r4.mPersistInfo
                int r4 = r4.countAllJobsLoaded
                if (r4 >= 0) goto L_0x00e9
                com.android.server.job.JobStore r4 = com.android.server.job.JobStore.this
                com.android.server.job.JobSchedulerInternal$JobStorePersistStats r4 = r4.mPersistInfo
                r4.countAllJobsLoaded = r0
                com.android.server.job.JobStore r4 = com.android.server.job.JobStore.this
                com.android.server.job.JobSchedulerInternal$JobStorePersistStats r4 = r4.mPersistInfo
                r4.countSystemServerJobsLoaded = r1
                com.android.server.job.JobStore r4 = com.android.server.job.JobStore.this
                com.android.server.job.JobSchedulerInternal$JobStorePersistStats r4 = r4.mPersistInfo
                r4.countSystemSyncManagerJobsLoaded = r2
            L_0x00e9:
                throw r3
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.job.JobStore.ReadJobMapFromDiskRunnable.run():void");
        }

        private List<JobStatus> readJobMapImpl(FileInputStream fis, boolean rtcIsGood) throws XmlPullParserException, IOException {
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(fis, StandardCharsets.UTF_8.name());
            int eventType = parser.getEventType();
            while (eventType != 2 && eventType != 1) {
                eventType = parser.next();
                Slog.d(JobStore.TAG, "Start tag: " + parser.getName());
            }
            if (eventType == 1) {
                if (JobStore.DEBUG) {
                    Slog.d(JobStore.TAG, "No persisted jobs.");
                }
                return null;
            } else if (!"job-info".equals(parser.getName())) {
                return null;
            } else {
                List<JobStatus> jobs = new ArrayList<>();
                try {
                    if (Integer.parseInt(parser.getAttributeValue((String) null, "version")) != 0) {
                        Slog.d(JobStore.TAG, "Invalid version number, aborting jobs file read.");
                        return null;
                    }
                    int eventType2 = parser.next();
                    do {
                        if (eventType2 == 2) {
                            String tagName = parser.getName();
                            if ("job".equals(tagName)) {
                                JobStatus persistedJob = restoreJobFromXml(rtcIsGood, parser);
                                if (persistedJob != null) {
                                    if (JobStore.DEBUG) {
                                        Slog.d(JobStore.TAG, "Read out " + persistedJob);
                                    }
                                    jobs.add(persistedJob);
                                } else {
                                    Slog.d(JobStore.TAG, "Error reading job from file.");
                                }
                            }
                            String str = tagName;
                        }
                        eventType2 = parser.next();
                    } while (eventType2 != 1);
                    return jobs;
                } catch (NumberFormatException e) {
                    Slog.e(JobStore.TAG, "Invalid version number, aborting jobs file read.");
                    return null;
                }
            }
        }

        /* JADX WARNING: type inference failed for: r5v4, types: [com.android.server.job.controllers.JobStatus, java.lang.String] */
        /* JADX WARNING: type inference failed for: r5v18 */
        /* JADX WARNING: type inference failed for: r5v22 */
        private JobStatus restoreJobFromXml(boolean rtcIsGood, XmlPullParser parser) throws XmlPullParserException, IOException {
            int eventType;
            int eventType2;
            int internalFlags;
            boolean z;
            Pair<Long, Long> elapsedRuntimes;
            int eventType3;
            String sourcePackageName;
            long flexMillis;
            ReadJobMapFromDiskRunnable readJobMapFromDiskRunnable = this;
            XmlPullParser xmlPullParser = parser;
            int internalFlags2 = 0;
            JobStatus jobStatus = null;
            try {
                JobInfo.Builder jobBuilder = readJobMapFromDiskRunnable.buildBuilderFromXml(xmlPullParser);
                boolean z2 = true;
                jobBuilder.setPersisted(true);
                int uid = Integer.parseInt(xmlPullParser.getAttributeValue((String) null, WatchlistLoggingHandler.WatchlistEventKeys.UID));
                String val = xmlPullParser.getAttributeValue((String) null, "priority");
                if (val != null) {
                    jobBuilder.setPriority(Integer.parseInt(val));
                }
                String val2 = xmlPullParser.getAttributeValue((String) null, "flags");
                if (val2 != null) {
                    jobBuilder.setFlags(Integer.parseInt(val2));
                }
                String val3 = xmlPullParser.getAttributeValue((String) null, "internalFlags");
                if (val3 != null) {
                    internalFlags2 = Integer.parseInt(val3);
                }
                try {
                    String val4 = xmlPullParser.getAttributeValue((String) null, "sourceUserId");
                    int sourceUserId = val4 == null ? -1 : Integer.parseInt(val4);
                    String val5 = xmlPullParser.getAttributeValue((String) null, "lastSuccessfulRunTime");
                    long lastSuccessfulRunTime = val5 == null ? 0 : Long.parseLong(val5);
                    String val6 = xmlPullParser.getAttributeValue((String) null, "lastFailedRunTime");
                    long lastFailedRunTime = val6 == null ? 0 : Long.parseLong(val6);
                    String sourcePackageName2 = xmlPullParser.getAttributeValue((String) null, "sourcePackageName");
                    String sourceTag = xmlPullParser.getAttributeValue((String) null, "sourceTag");
                    while (true) {
                        eventType = parser.next();
                        if (eventType != 4) {
                            break;
                        }
                        int i = internalFlags2;
                        int i2 = sourceUserId;
                        readJobMapFromDiskRunnable = this;
                        jobStatus = null;
                    }
                    if (eventType != 2) {
                        JobStatus jobStatus2 = jobStatus;
                        int i3 = sourceUserId;
                        return jobStatus2;
                    } else if (!JobStore.XML_TAG_PARAMS_CONSTRAINTS.equals(parser.getName())) {
                        int i4 = internalFlags2;
                        JobStatus jobStatus3 = jobStatus;
                        int i5 = sourceUserId;
                        return jobStatus3;
                    } else {
                        try {
                            readJobMapFromDiskRunnable.buildConstraintsFromXml(jobBuilder, xmlPullParser);
                            parser.next();
                            ? r5 = jobStatus;
                            while (true) {
                                eventType2 = parser.next();
                                if (eventType2 != 4) {
                                    break;
                                }
                                int i6 = internalFlags2;
                                Object obj = r5;
                                int i7 = sourceUserId;
                                z2 = z2;
                                r5 = obj;
                                readJobMapFromDiskRunnable = this;
                            }
                            if (eventType2 != 2) {
                                return r5;
                            }
                            try {
                                Pair<Long, Long> rtcRuntimes = readJobMapFromDiskRunnable.buildRtcExecutionTimesFromXml(xmlPullParser);
                                int sourceUserId2 = sourceUserId;
                                long elapsedNow = JobSchedulerService.sElapsedRealtimeClock.millis();
                                Pair<Long, Long> elapsedRuntimes2 = JobStore.convertRtcBoundsToElapsed(rtcRuntimes, elapsedNow);
                                if (JobStore.XML_TAG_PERIODIC.equals(parser.getName())) {
                                    try {
                                        long periodMillis = Long.parseLong(xmlPullParser.getAttributeValue(r5, "period"));
                                        String val7 = xmlPullParser.getAttributeValue(r5, "flex");
                                        if (val7 != null) {
                                            try {
                                                flexMillis = Long.valueOf(val7).longValue();
                                            } catch (NumberFormatException e) {
                                                int i8 = internalFlags2;
                                                Slog.d(JobStore.TAG, "Error reading periodic execution criteria, skipping.");
                                                return null;
                                            }
                                        } else {
                                            flexMillis = periodMillis;
                                        }
                                        String str = val7;
                                        long periodMillis2 = periodMillis;
                                        internalFlags = internalFlags2;
                                        long flexMillis2 = flexMillis;
                                        try {
                                            jobBuilder.setPeriodic(periodMillis2, flexMillis2);
                                            if (((Long) elapsedRuntimes2.second).longValue() > elapsedNow + periodMillis2 + flexMillis2) {
                                                long clampedLateRuntimeElapsed = elapsedNow + flexMillis2 + periodMillis2;
                                                long clampedEarlyRuntimeElapsed = clampedLateRuntimeElapsed - flexMillis2;
                                                long j = periodMillis2;
                                                z = false;
                                                Slog.w(JobStore.TAG, String.format("Periodic job for uid='%d' persisted run-time is too big [%s, %s]. Clamping to [%s,%s]", new Object[]{Integer.valueOf(uid), DateUtils.formatElapsedTime(((Long) elapsedRuntimes2.first).longValue() / 1000), DateUtils.formatElapsedTime(((Long) elapsedRuntimes2.second).longValue() / 1000), DateUtils.formatElapsedTime(clampedEarlyRuntimeElapsed / 1000), DateUtils.formatElapsedTime(clampedLateRuntimeElapsed / 1000)}));
                                                elapsedRuntimes2 = Pair.create(Long.valueOf(clampedEarlyRuntimeElapsed), Long.valueOf(clampedLateRuntimeElapsed));
                                            } else {
                                                z = false;
                                            }
                                            elapsedRuntimes = elapsedRuntimes2;
                                        } catch (NumberFormatException e2) {
                                            Slog.d(JobStore.TAG, "Error reading periodic execution criteria, skipping.");
                                            return null;
                                        }
                                    } catch (NumberFormatException e3) {
                                        int i9 = internalFlags2;
                                        Slog.d(JobStore.TAG, "Error reading periodic execution criteria, skipping.");
                                        return null;
                                    }
                                } else {
                                    internalFlags = internalFlags2;
                                    z = false;
                                    if (JobStore.XML_TAG_ONEOFF.equals(parser.getName())) {
                                        try {
                                            if (((Long) elapsedRuntimes2.first).longValue() != 0) {
                                                try {
                                                    jobBuilder.setMinimumLatency(((Long) elapsedRuntimes2.first).longValue() - elapsedNow);
                                                } catch (NumberFormatException e4) {
                                                    Pair<Long, Long> pair = rtcRuntimes;
                                                    long j2 = elapsedNow;
                                                    int i10 = sourceUserId2;
                                                }
                                            }
                                            if (((Long) elapsedRuntimes2.second).longValue() != JobStatus.NO_LATEST_RUNTIME) {
                                                jobBuilder.setOverrideDeadline(((Long) elapsedRuntimes2.second).longValue() - elapsedNow);
                                            }
                                            elapsedRuntimes = elapsedRuntimes2;
                                        } catch (NumberFormatException e5) {
                                            Pair<Long, Long> pair2 = rtcRuntimes;
                                            long j3 = elapsedNow;
                                            int i11 = sourceUserId2;
                                            Slog.d(JobStore.TAG, "Error reading job execution criteria, skipping.");
                                            return null;
                                        }
                                    } else {
                                        long j4 = elapsedNow;
                                        int i12 = sourceUserId2;
                                        if (!JobStore.DEBUG) {
                                            return null;
                                        }
                                        Slog.d(JobStore.TAG, "Invalid parameter tag, skipping - " + parser.getName());
                                        return null;
                                    }
                                }
                                maybeBuildBackoffPolicyFromXml(jobBuilder, xmlPullParser);
                                parser.nextTag();
                                while (true) {
                                    eventType3 = parser.next();
                                    if (eventType3 != 4) {
                                        break;
                                    }
                                    int i13 = eventType3;
                                    long j5 = elapsedNow;
                                    int i14 = sourceUserId2;
                                }
                                if (eventType3 != 2) {
                                    int i15 = eventType3;
                                    long j6 = elapsedNow;
                                    int i16 = sourceUserId2;
                                } else if (!JobStore.XML_TAG_EXTRAS.equals(parser.getName())) {
                                    Pair<Long, Long> pair3 = rtcRuntimes;
                                    int i17 = eventType3;
                                    long j7 = elapsedNow;
                                    int i18 = sourceUserId2;
                                } else {
                                    PersistableBundle extras = PersistableBundle.restoreFromXml(parser);
                                    jobBuilder.setExtras(extras);
                                    parser.nextTag();
                                    try {
                                        JobInfo builtJob = jobBuilder.build();
                                        if (!PackageManagerService.PLATFORM_PACKAGE_NAME.equals(sourcePackageName2) || extras == null || !extras.getBoolean("SyncManagerJob", z)) {
                                            sourcePackageName = sourcePackageName2;
                                        } else {
                                            sourcePackageName = extras.getString("owningPackage", sourcePackageName2);
                                            if (JobStore.DEBUG) {
                                                Slog.i(JobStore.TAG, "Fixing up sync job source package name from 'android' to '" + sourcePackageName + "'");
                                            }
                                        }
                                        JobSchedulerInternal service = (JobSchedulerInternal) LocalServices.getService(JobSchedulerInternal.class);
                                        int sourceUserId3 = sourceUserId2;
                                        PersistableBundle persistableBundle = extras;
                                        JobInfo jobInfo = builtJob;
                                        Pair<Long, Long> pair4 = rtcRuntimes;
                                        int i19 = eventType3;
                                        long j8 = elapsedNow;
                                        return new JobStatus(jobBuilder.build(), uid, sourcePackageName, sourceUserId3, JobSchedulerService.standbyBucketForPackage(sourcePackageName, sourceUserId3, elapsedNow), service != null ? service.currentHeartbeat() : 0, sourceTag, ((Long) elapsedRuntimes.first).longValue(), ((Long) elapsedRuntimes.second).longValue(), lastSuccessfulRunTime, lastFailedRunTime, rtcIsGood ? null : rtcRuntimes, internalFlags);
                                    } catch (Exception e6) {
                                        Pair<Long, Long> pair5 = rtcRuntimes;
                                        int i20 = eventType3;
                                        long j9 = elapsedNow;
                                        PersistableBundle persistableBundle2 = extras;
                                        int i21 = sourceUserId2;
                                        Exception exc = e6;
                                        Slog.w(JobStore.TAG, "Unable to build job from XML, ignoring: " + jobBuilder.summarize());
                                        return null;
                                    }
                                }
                                if (!JobStore.DEBUG) {
                                    return null;
                                }
                                Slog.d(JobStore.TAG, "Error reading extras, skipping.");
                                return null;
                            } catch (NumberFormatException e7) {
                                int i22 = internalFlags2;
                                int i23 = sourceUserId;
                                NumberFormatException numberFormatException = e7;
                                if (!JobStore.DEBUG) {
                                    return null;
                                }
                                Slog.d(JobStore.TAG, "Error parsing execution time parameters, skipping.");
                                return null;
                            }
                        } catch (NumberFormatException e8) {
                            int i24 = internalFlags2;
                            JobStatus jobStatus4 = jobStatus;
                            int i25 = sourceUserId;
                            NumberFormatException numberFormatException2 = e8;
                            Slog.d(JobStore.TAG, "Error reading constraints, skipping.");
                            return jobStatus4;
                        }
                    }
                } catch (NumberFormatException e9) {
                    int i26 = internalFlags2;
                    Slog.e(JobStore.TAG, "Error parsing job's required fields, skipping");
                    return null;
                }
            } catch (NumberFormatException e10) {
                Slog.e(JobStore.TAG, "Error parsing job's required fields, skipping");
                return null;
            }
        }

        private JobInfo.Builder buildBuilderFromXml(XmlPullParser parser) throws NumberFormatException {
            return new JobInfo.Builder(Integer.parseInt(parser.getAttributeValue((String) null, "jobid")), new ComponentName(parser.getAttributeValue((String) null, Settings.ATTR_PACKAGE), parser.getAttributeValue((String) null, "class")));
        }

        private void buildConstraintsFromXml(JobInfo.Builder jobBuilder, XmlPullParser parser) {
            long unwantedCapabilities;
            String netCapabilities = parser.getAttributeValue((String) null, "net-capabilities");
            String netUnwantedCapabilities = parser.getAttributeValue((String) null, "net-unwanted-capabilities");
            String netTransportTypes = parser.getAttributeValue((String) null, "net-transport-types");
            if (netCapabilities == null || netTransportTypes == null) {
                if (parser.getAttributeValue((String) null, "connectivity") != null) {
                    jobBuilder.setRequiredNetworkType(1);
                }
                if (parser.getAttributeValue((String) null, "metered") != null) {
                    jobBuilder.setRequiredNetworkType(4);
                }
                if (parser.getAttributeValue((String) null, "unmetered") != null) {
                    jobBuilder.setRequiredNetworkType(2);
                }
                if (parser.getAttributeValue((String) null, "not-roaming") != null) {
                    jobBuilder.setRequiredNetworkType(3);
                }
            } else {
                NetworkRequest request = new NetworkRequest.Builder().build();
                if (netUnwantedCapabilities != null) {
                    unwantedCapabilities = Long.parseLong(netUnwantedCapabilities);
                } else {
                    unwantedCapabilities = BitUtils.packBits(request.networkCapabilities.getUnwantedCapabilities());
                }
                request.networkCapabilities.setCapabilities(BitUtils.unpackBits(Long.parseLong(netCapabilities)), BitUtils.unpackBits(unwantedCapabilities));
                request.networkCapabilities.setTransportTypes(BitUtils.unpackBits(Long.parseLong(netTransportTypes)));
                jobBuilder.setRequiredNetwork(request);
            }
            if (parser.getAttributeValue((String) null, "idle") != null) {
                jobBuilder.setRequiresDeviceIdle(true);
            }
            if (parser.getAttributeValue((String) null, "charging") != null) {
                jobBuilder.setRequiresCharging(true);
            }
            if (parser.getAttributeValue((String) null, "battery-not-low") != null) {
                jobBuilder.setRequiresBatteryNotLow(true);
            }
            if (parser.getAttributeValue((String) null, "storage-not-low") != null) {
                jobBuilder.setRequiresStorageNotLow(true);
            }
        }

        private void maybeBuildBackoffPolicyFromXml(JobInfo.Builder jobBuilder, XmlPullParser parser) {
            String val = parser.getAttributeValue((String) null, "initial-backoff");
            if (val != null) {
                jobBuilder.setBackoffCriteria(Long.parseLong(val), Integer.parseInt(parser.getAttributeValue((String) null, "backoff-policy")));
            }
        }

        private Pair<Long, Long> buildRtcExecutionTimesFromXml(XmlPullParser parser) throws NumberFormatException {
            long earliestRunTimeRtc;
            long latestRunTimeRtc;
            String val = parser.getAttributeValue((String) null, "delay");
            if (val != null) {
                earliestRunTimeRtc = Long.parseLong(val);
            } else {
                earliestRunTimeRtc = 0;
            }
            String val2 = parser.getAttributeValue((String) null, "deadline");
            if (val2 != null) {
                latestRunTimeRtc = Long.parseLong(val2);
            } else {
                latestRunTimeRtc = JobStatus.NO_LATEST_RUNTIME;
            }
            return Pair.create(Long.valueOf(earliestRunTimeRtc), Long.valueOf(latestRunTimeRtc));
        }
    }

    static final class JobSet {
        @VisibleForTesting
        final SparseArray<ArraySet<JobStatus>> mJobs = new SparseArray<>();
        @VisibleForTesting
        final SparseArray<ArraySet<JobStatus>> mJobsPerSourceUid = new SparseArray<>();

        public List<JobStatus> getJobsByUid(int uid) {
            ArrayList<JobStatus> matchingJobs = new ArrayList<>();
            ArraySet<JobStatus> jobs = this.mJobs.get(uid);
            if (jobs != null) {
                matchingJobs.addAll(jobs);
            }
            return matchingJobs;
        }

        public List<JobStatus> getJobsByUser(int userId) {
            ArraySet<JobStatus> jobs;
            ArrayList<JobStatus> result = new ArrayList<>();
            for (int i = this.mJobsPerSourceUid.size() - 1; i >= 0; i--) {
                if (UserHandle.getUserId(this.mJobsPerSourceUid.keyAt(i)) == userId && (jobs = this.mJobsPerSourceUid.valueAt(i)) != null) {
                    result.addAll(jobs);
                }
            }
            return result;
        }

        public boolean add(JobStatus job) {
            int uid = job.getUid();
            int sourceUid = job.getSourceUid();
            ArraySet<JobStatus> jobs = this.mJobs.get(uid);
            if (jobs == null) {
                jobs = new ArraySet<>();
                this.mJobs.put(uid, jobs);
            }
            ArraySet<JobStatus> jobsForSourceUid = this.mJobsPerSourceUid.get(sourceUid);
            if (jobsForSourceUid == null) {
                jobsForSourceUid = new ArraySet<>();
                this.mJobsPerSourceUid.put(sourceUid, jobsForSourceUid);
            }
            boolean added = jobs.add(job);
            boolean addedInSource = jobsForSourceUid.add(job);
            if (added != addedInSource) {
                Slog.wtf(JobStore.TAG, "mJobs and mJobsPerSourceUid mismatch; caller= " + added + " source= " + addedInSource);
            }
            return added || addedInSource;
        }

        public boolean remove(JobStatus job) {
            int uid = job.getUid();
            ArraySet<JobStatus> jobs = this.mJobs.get(uid);
            int sourceUid = job.getSourceUid();
            ArraySet<JobStatus> jobsForSourceUid = this.mJobsPerSourceUid.get(sourceUid);
            boolean didRemove = jobs != null && jobs.remove(job);
            boolean sourceRemove = jobsForSourceUid != null && jobsForSourceUid.remove(job);
            if (didRemove != sourceRemove) {
                Slog.wtf(JobStore.TAG, "Job presence mismatch; caller=" + didRemove + " source=" + sourceRemove);
            }
            if (!didRemove && !sourceRemove) {
                return false;
            }
            if (jobs != null && jobs.size() == 0) {
                this.mJobs.remove(uid);
            }
            if (jobsForSourceUid != null && jobsForSourceUid.size() == 0) {
                this.mJobsPerSourceUid.remove(sourceUid);
            }
            return true;
        }

        public void removeJobsOfNonUsers(int[] whitelist) {
            removeAll(new Predicate(whitelist) {
                private final /* synthetic */ int[] f$0;

                {
                    this.f$0 = r1;
                }

                public final boolean test(Object obj) {
                    return JobStore.JobSet.lambda$removeJobsOfNonUsers$0(this.f$0, (JobStatus) obj);
                }
            }.or(new Predicate(whitelist) {
                private final /* synthetic */ int[] f$0;

                {
                    this.f$0 = r1;
                }

                public final boolean test(Object obj) {
                    return JobStore.JobSet.lambda$removeJobsOfNonUsers$1(this.f$0, (JobStatus) obj);
                }
            }));
        }

        static /* synthetic */ boolean lambda$removeJobsOfNonUsers$0(int[] whitelist, JobStatus job) {
            return !ArrayUtils.contains(whitelist, job.getSourceUserId());
        }

        static /* synthetic */ boolean lambda$removeJobsOfNonUsers$1(int[] whitelist, JobStatus job) {
            return !ArrayUtils.contains(whitelist, job.getUserId());
        }

        private void removeAll(Predicate<JobStatus> predicate) {
            for (int jobSetIndex = this.mJobs.size() - 1; jobSetIndex >= 0; jobSetIndex--) {
                ArraySet<JobStatus> jobs = this.mJobs.valueAt(jobSetIndex);
                for (int jobIndex = jobs.size() - 1; jobIndex >= 0; jobIndex--) {
                    if (predicate.test(jobs.valueAt(jobIndex))) {
                        jobs.removeAt(jobIndex);
                    }
                }
                if (jobs.size() == 0) {
                    this.mJobs.removeAt(jobSetIndex);
                }
            }
            for (int jobSetIndex2 = this.mJobsPerSourceUid.size() - 1; jobSetIndex2 >= 0; jobSetIndex2--) {
                ArraySet<JobStatus> jobs2 = this.mJobsPerSourceUid.valueAt(jobSetIndex2);
                for (int jobIndex2 = jobs2.size() - 1; jobIndex2 >= 0; jobIndex2--) {
                    if (predicate.test(jobs2.valueAt(jobIndex2))) {
                        jobs2.removeAt(jobIndex2);
                    }
                }
                if (jobs2.size() == 0) {
                    this.mJobsPerSourceUid.removeAt(jobSetIndex2);
                }
            }
        }

        public boolean contains(JobStatus job) {
            ArraySet<JobStatus> jobs = this.mJobs.get(job.getUid());
            return jobs != null && jobs.contains(job);
        }

        public JobStatus get(int uid, int jobId) {
            ArraySet<JobStatus> jobs = this.mJobs.get(uid);
            if (jobs == null) {
                return null;
            }
            for (int i = jobs.size() - 1; i >= 0; i--) {
                JobStatus job = jobs.valueAt(i);
                if (job.getJobId() == jobId) {
                    return job;
                }
            }
            return null;
        }

        public List<JobStatus> getAllJobs() {
            ArrayList<JobStatus> allJobs = new ArrayList<>(size());
            for (int i = this.mJobs.size() - 1; i >= 0; i--) {
                ArraySet<JobStatus> jobs = this.mJobs.valueAt(i);
                if (jobs != null) {
                    for (int j = jobs.size() - 1; j >= 0; j--) {
                        allJobs.add(jobs.valueAt(j));
                    }
                }
            }
            return allJobs;
        }

        public void clear() {
            this.mJobs.clear();
            this.mJobsPerSourceUid.clear();
        }

        public int size() {
            int total = 0;
            for (int i = this.mJobs.size() - 1; i >= 0; i--) {
                total += this.mJobs.valueAt(i).size();
            }
            return total;
        }

        public int countJobsForUid(int uid) {
            int total = 0;
            ArraySet<JobStatus> jobs = this.mJobs.get(uid);
            if (jobs != null) {
                for (int i = jobs.size() - 1; i >= 0; i--) {
                    JobStatus job = jobs.valueAt(i);
                    if (job.getUid() == job.getSourceUid()) {
                        total++;
                    }
                }
            }
            return total;
        }

        public void forEachJob(Predicate<JobStatus> filterPredicate, Consumer<JobStatus> functor) {
            for (int uidIndex = this.mJobs.size() - 1; uidIndex >= 0; uidIndex--) {
                ArraySet<JobStatus> jobs = this.mJobs.valueAt(uidIndex);
                if (jobs != null) {
                    for (int i = jobs.size() - 1; i >= 0; i--) {
                        JobStatus jobStatus = jobs.valueAt(i);
                        if (filterPredicate == null || filterPredicate.test(jobStatus)) {
                            functor.accept(jobStatus);
                        }
                    }
                }
            }
        }

        public void forEachJob(int callingUid, Consumer<JobStatus> functor) {
            ArraySet<JobStatus> jobs = this.mJobs.get(callingUid);
            if (jobs != null) {
                for (int i = jobs.size() - 1; i >= 0; i--) {
                    functor.accept(jobs.valueAt(i));
                }
            }
        }

        public void forEachJobForSourceUid(int sourceUid, Consumer<JobStatus> functor) {
            ArraySet<JobStatus> jobs = this.mJobsPerSourceUid.get(sourceUid);
            if (jobs != null) {
                for (int i = jobs.size() - 1; i >= 0; i--) {
                    functor.accept(jobs.valueAt(i));
                }
            }
        }
    }
}
