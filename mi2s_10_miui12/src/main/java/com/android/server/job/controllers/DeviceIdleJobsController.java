package com.android.server.job.controllers;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.Log;
import android.util.Slog;
import android.util.SparseBooleanArray;
import android.util.proto.ProtoOutputStream;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.DeviceIdleController;
import com.android.server.LocalServices;
import com.android.server.job.JobSchedulerService;
import com.android.server.pm.DumpState;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class DeviceIdleJobsController extends StateController {
    private static final long BACKGROUND_JOBS_DELAY = 3000;
    /* access modifiers changed from: private */
    public static final boolean DEBUG = (JobSchedulerService.DEBUG || Log.isLoggable(TAG, 3));
    static final int PROCESS_BACKGROUND_JOBS = 1;
    private static final String TAG = "JobScheduler.DeviceIdle";
    /* access modifiers changed from: private */
    public final ArraySet<JobStatus> mAllowInIdleJobs = new ArraySet<>();
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        /* JADX WARNING: Can't fix incorrect switch cases order */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void onReceive(android.content.Context r7, android.content.Intent r8) {
            /*
                r6 = this;
                java.lang.String r0 = r8.getAction()
                int r1 = r0.hashCode()
                r2 = 3
                r3 = 2
                r4 = 0
                r5 = 1
                switch(r1) {
                    case -712152692: goto L_0x002e;
                    case -65633567: goto L_0x0024;
                    case 498807504: goto L_0x001a;
                    case 870701415: goto L_0x0010;
                    default: goto L_0x000f;
                }
            L_0x000f:
                goto L_0x0038
            L_0x0010:
                java.lang.String r1 = "android.os.action.DEVICE_IDLE_MODE_CHANGED"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x000f
                r0 = r5
                goto L_0x0039
            L_0x001a:
                java.lang.String r1 = "android.os.action.LIGHT_DEVICE_IDLE_MODE_CHANGED"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x000f
                r0 = r4
                goto L_0x0039
            L_0x0024:
                java.lang.String r1 = "android.os.action.POWER_SAVE_WHITELIST_CHANGED"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x000f
                r0 = r3
                goto L_0x0039
            L_0x002e:
                java.lang.String r1 = "android.os.action.POWER_SAVE_TEMP_WHITELIST_CHANGED"
                boolean r0 = r0.equals(r1)
                if (r0 == 0) goto L_0x000f
                r0 = r2
                goto L_0x0039
            L_0x0038:
                r0 = -1
            L_0x0039:
                if (r0 == 0) goto L_0x00ee
                if (r0 == r5) goto L_0x00ee
                if (r0 == r3) goto L_0x00af
                if (r0 == r2) goto L_0x0043
                goto L_0x0115
            L_0x0043:
                com.android.server.job.controllers.DeviceIdleJobsController r0 = com.android.server.job.controllers.DeviceIdleJobsController.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.job.controllers.DeviceIdleJobsController r1 = com.android.server.job.controllers.DeviceIdleJobsController.this     // Catch:{ all -> 0x00ac }
                com.android.server.job.controllers.DeviceIdleJobsController r2 = com.android.server.job.controllers.DeviceIdleJobsController.this     // Catch:{ all -> 0x00ac }
                com.android.server.DeviceIdleController$LocalService r2 = r2.mLocalDeviceIdleController     // Catch:{ all -> 0x00ac }
                int[] r2 = r2.getPowerSaveTempWhitelistAppIds()     // Catch:{ all -> 0x00ac }
                int[] unused = r1.mPowerSaveTempWhitelistAppIds = r2     // Catch:{ all -> 0x00ac }
                boolean r1 = com.android.server.job.controllers.DeviceIdleJobsController.DEBUG     // Catch:{ all -> 0x00ac }
                if (r1 == 0) goto L_0x007d
                java.lang.String r1 = "JobScheduler.DeviceIdle"
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ac }
                r2.<init>()     // Catch:{ all -> 0x00ac }
                java.lang.String r3 = "Got temp whitelist "
                r2.append(r3)     // Catch:{ all -> 0x00ac }
                com.android.server.job.controllers.DeviceIdleJobsController r3 = com.android.server.job.controllers.DeviceIdleJobsController.this     // Catch:{ all -> 0x00ac }
                int[] r3 = r3.mPowerSaveTempWhitelistAppIds     // Catch:{ all -> 0x00ac }
                java.lang.String r3 = java.util.Arrays.toString(r3)     // Catch:{ all -> 0x00ac }
                r2.append(r3)     // Catch:{ all -> 0x00ac }
                java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x00ac }
                android.util.Slog.d(r1, r2)     // Catch:{ all -> 0x00ac }
            L_0x007d:
                r1 = 0
                r2 = r4
            L_0x007f:
                com.android.server.job.controllers.DeviceIdleJobsController r3 = com.android.server.job.controllers.DeviceIdleJobsController.this     // Catch:{ all -> 0x00ac }
                android.util.ArraySet r3 = r3.mAllowInIdleJobs     // Catch:{ all -> 0x00ac }
                int r3 = r3.size()     // Catch:{ all -> 0x00ac }
                if (r2 >= r3) goto L_0x00a1
                com.android.server.job.controllers.DeviceIdleJobsController r3 = com.android.server.job.controllers.DeviceIdleJobsController.this     // Catch:{ all -> 0x00ac }
                com.android.server.job.controllers.DeviceIdleJobsController r4 = com.android.server.job.controllers.DeviceIdleJobsController.this     // Catch:{ all -> 0x00ac }
                android.util.ArraySet r4 = r4.mAllowInIdleJobs     // Catch:{ all -> 0x00ac }
                java.lang.Object r4 = r4.valueAt(r2)     // Catch:{ all -> 0x00ac }
                com.android.server.job.controllers.JobStatus r4 = (com.android.server.job.controllers.JobStatus) r4     // Catch:{ all -> 0x00ac }
                boolean r3 = r3.updateTaskStateLocked(r4)     // Catch:{ all -> 0x00ac }
                r1 = r1 | r3
                int r2 = r2 + 1
                goto L_0x007f
            L_0x00a1:
                if (r1 == 0) goto L_0x00aa
                com.android.server.job.controllers.DeviceIdleJobsController r2 = com.android.server.job.controllers.DeviceIdleJobsController.this     // Catch:{ all -> 0x00ac }
                com.android.server.job.StateChangedListener r2 = r2.mStateChangedListener     // Catch:{ all -> 0x00ac }
                r2.onControllerStateChanged()     // Catch:{ all -> 0x00ac }
            L_0x00aa:
                monitor-exit(r0)     // Catch:{ all -> 0x00ac }
                goto L_0x0115
            L_0x00ac:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x00ac }
                throw r1
            L_0x00af:
                com.android.server.job.controllers.DeviceIdleJobsController r0 = com.android.server.job.controllers.DeviceIdleJobsController.this
                java.lang.Object r0 = r0.mLock
                monitor-enter(r0)
                com.android.server.job.controllers.DeviceIdleJobsController r1 = com.android.server.job.controllers.DeviceIdleJobsController.this     // Catch:{ all -> 0x00eb }
                com.android.server.job.controllers.DeviceIdleJobsController r2 = com.android.server.job.controllers.DeviceIdleJobsController.this     // Catch:{ all -> 0x00eb }
                com.android.server.DeviceIdleController$LocalService r2 = r2.mLocalDeviceIdleController     // Catch:{ all -> 0x00eb }
                int[] r2 = r2.getPowerSaveWhitelistUserAppIds()     // Catch:{ all -> 0x00eb }
                int[] unused = r1.mDeviceIdleWhitelistAppIds = r2     // Catch:{ all -> 0x00eb }
                boolean r1 = com.android.server.job.controllers.DeviceIdleJobsController.DEBUG     // Catch:{ all -> 0x00eb }
                if (r1 == 0) goto L_0x00e9
                java.lang.String r1 = "JobScheduler.DeviceIdle"
                java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x00eb }
                r2.<init>()     // Catch:{ all -> 0x00eb }
                java.lang.String r3 = "Got whitelist "
                r2.append(r3)     // Catch:{ all -> 0x00eb }
                com.android.server.job.controllers.DeviceIdleJobsController r3 = com.android.server.job.controllers.DeviceIdleJobsController.this     // Catch:{ all -> 0x00eb }
                int[] r3 = r3.mDeviceIdleWhitelistAppIds     // Catch:{ all -> 0x00eb }
                java.lang.String r3 = java.util.Arrays.toString(r3)     // Catch:{ all -> 0x00eb }
                r2.append(r3)     // Catch:{ all -> 0x00eb }
                java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x00eb }
                android.util.Slog.d(r1, r2)     // Catch:{ all -> 0x00eb }
            L_0x00e9:
                monitor-exit(r0)     // Catch:{ all -> 0x00eb }
                goto L_0x0115
            L_0x00eb:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x00eb }
                throw r1
            L_0x00ee:
                com.android.server.job.controllers.DeviceIdleJobsController r0 = com.android.server.job.controllers.DeviceIdleJobsController.this
                android.os.PowerManager r1 = r0.mPowerManager
                if (r1 == 0) goto L_0x0110
                com.android.server.job.controllers.DeviceIdleJobsController r1 = com.android.server.job.controllers.DeviceIdleJobsController.this
                android.os.PowerManager r1 = r1.mPowerManager
                boolean r1 = r1.isDeviceIdleMode()
                if (r1 != 0) goto L_0x010e
                com.android.server.job.controllers.DeviceIdleJobsController r1 = com.android.server.job.controllers.DeviceIdleJobsController.this
                android.os.PowerManager r1 = r1.mPowerManager
                boolean r1 = r1.isLightDeviceIdleMode()
                if (r1 == 0) goto L_0x0110
            L_0x010e:
                r4 = r5
                goto L_0x0111
            L_0x0110:
            L_0x0111:
                r0.updateIdleMode(r4)
            L_0x0115:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.job.controllers.DeviceIdleJobsController.AnonymousClass1.onReceive(android.content.Context, android.content.Intent):void");
        }
    };
    private boolean mDeviceIdleMode;
    /* access modifiers changed from: private */
    public final DeviceIdleUpdateFunctor mDeviceIdleUpdateFunctor = new DeviceIdleUpdateFunctor();
    /* access modifiers changed from: private */
    public int[] mDeviceIdleWhitelistAppIds = this.mLocalDeviceIdleController.getPowerSaveWhitelistUserAppIds();
    private final SparseBooleanArray mForegroundUids = new SparseBooleanArray();
    private final DeviceIdleJobsDelayHandler mHandler = new DeviceIdleJobsDelayHandler(this.mContext.getMainLooper());
    /* access modifiers changed from: private */
    public final DeviceIdleController.LocalService mLocalDeviceIdleController = ((DeviceIdleController.LocalService) LocalServices.getService(DeviceIdleController.LocalService.class));
    /* access modifiers changed from: private */
    public final PowerManager mPowerManager = ((PowerManager) this.mContext.getSystemService("power"));
    /* access modifiers changed from: private */
    public int[] mPowerSaveTempWhitelistAppIds = this.mLocalDeviceIdleController.getPowerSaveTempWhitelistAppIds();

    public DeviceIdleJobsController(JobSchedulerService service) {
        super(service);
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.os.action.DEVICE_IDLE_MODE_CHANGED");
        filter.addAction("android.os.action.LIGHT_DEVICE_IDLE_MODE_CHANGED");
        filter.addAction("android.os.action.POWER_SAVE_WHITELIST_CHANGED");
        filter.addAction("android.os.action.POWER_SAVE_TEMP_WHITELIST_CHANGED");
        this.mContext.registerReceiverAsUser(this.mBroadcastReceiver, UserHandle.ALL, filter, (String) null, (Handler) null);
    }

    /* access modifiers changed from: package-private */
    public void updateIdleMode(boolean enabled) {
        boolean changed = false;
        synchronized (this.mLock) {
            if (this.mDeviceIdleMode != enabled) {
                changed = true;
            }
            this.mDeviceIdleMode = enabled;
            if (DEBUG) {
                Slog.d(TAG, "mDeviceIdleMode=" + this.mDeviceIdleMode);
            }
            if (enabled) {
                this.mHandler.removeMessages(1);
                this.mService.getJobStore().forEachJob(this.mDeviceIdleUpdateFunctor);
            } else {
                for (int i = 0; i < this.mForegroundUids.size(); i++) {
                    if (this.mForegroundUids.valueAt(i)) {
                        this.mService.getJobStore().forEachJobForSourceUid(this.mForegroundUids.keyAt(i), this.mDeviceIdleUpdateFunctor);
                    }
                }
                this.mHandler.sendEmptyMessageDelayed(1, 3000);
            }
        }
        if (changed) {
            this.mStateChangedListener.onDeviceIdleStateChanged(enabled);
        }
    }

    public void setUidActiveLocked(int uid, boolean active) {
        if (active != this.mForegroundUids.get(uid)) {
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("uid ");
                sb.append(uid);
                sb.append(" going ");
                sb.append(active ? "active" : "inactive");
                Slog.d(TAG, sb.toString());
            }
            this.mForegroundUids.put(uid, active);
            this.mDeviceIdleUpdateFunctor.mChanged = false;
            this.mService.getJobStore().forEachJobForSourceUid(uid, this.mDeviceIdleUpdateFunctor);
            if (this.mDeviceIdleUpdateFunctor.mChanged) {
                this.mStateChangedListener.onControllerStateChanged();
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isWhitelistedLocked(JobStatus job) {
        return Arrays.binarySearch(this.mDeviceIdleWhitelistAppIds, UserHandle.getAppId(job.getSourceUid())) >= 0;
    }

    /* access modifiers changed from: package-private */
    public boolean isTempWhitelistedLocked(JobStatus job) {
        return ArrayUtils.contains(this.mPowerSaveTempWhitelistAppIds, UserHandle.getAppId(job.getSourceUid()));
    }

    /* access modifiers changed from: private */
    public boolean updateTaskStateLocked(JobStatus task) {
        boolean enableTask = true;
        boolean allowInIdle = (task.getFlags() & 2) != 0 && (this.mForegroundUids.get(task.getSourceUid()) || isTempWhitelistedLocked(task));
        boolean whitelisted = isWhitelistedLocked(task);
        if (this.mDeviceIdleMode && !whitelisted && !allowInIdle) {
            enableTask = false;
        }
        return task.setDeviceNotDozingConstraintSatisfied(enableTask, whitelisted);
    }

    public void maybeStartTrackingJobLocked(JobStatus jobStatus, JobStatus lastJob) {
        if ((jobStatus.getFlags() & 2) != 0) {
            this.mAllowInIdleJobs.add(jobStatus);
        }
        updateTaskStateLocked(jobStatus);
    }

    public void maybeStopTrackingJobLocked(JobStatus jobStatus, JobStatus incomingJob, boolean forUpdate) {
        if ((jobStatus.getFlags() & 2) != 0) {
            this.mAllowInIdleJobs.remove(jobStatus);
        }
    }

    public void dumpControllerStateLocked(IndentingPrintWriter pw, Predicate<JobStatus> predicate) {
        pw.println("Idle mode: " + this.mDeviceIdleMode);
        pw.println();
        this.mService.getJobStore().forEachJob(predicate, (Consumer<JobStatus>) new Consumer(pw) {
            private final /* synthetic */ IndentingPrintWriter f$1;

            {
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                DeviceIdleJobsController.this.lambda$dumpControllerStateLocked$0$DeviceIdleJobsController(this.f$1, (JobStatus) obj);
            }
        });
    }

    public /* synthetic */ void lambda$dumpControllerStateLocked$0$DeviceIdleJobsController(IndentingPrintWriter pw, JobStatus jobStatus) {
        pw.print("#");
        jobStatus.printUniqueId(pw);
        pw.print(" from ");
        UserHandle.formatUid(pw, jobStatus.getSourceUid());
        pw.print(": ");
        pw.print(jobStatus.getSourcePackageName());
        pw.print((jobStatus.satisfiedConstraints & DumpState.DUMP_APEX) != 0 ? " RUNNABLE" : " WAITING");
        if (jobStatus.dozeWhitelisted) {
            pw.print(" WHITELISTED");
        }
        if (this.mAllowInIdleJobs.contains(jobStatus)) {
            pw.print(" ALLOWED_IN_DOZE");
        }
        pw.println();
    }

    public void dumpControllerStateLocked(ProtoOutputStream proto, long fieldId, Predicate<JobStatus> predicate) {
        long token = proto.start(fieldId);
        long mToken = proto.start(1146756268037L);
        proto.write(1133871366145L, this.mDeviceIdleMode);
        this.mService.getJobStore().forEachJob(predicate, (Consumer<JobStatus>) new Consumer(proto) {
            private final /* synthetic */ ProtoOutputStream f$1;

            {
                this.f$1 = r2;
            }

            public final void accept(Object obj) {
                DeviceIdleJobsController.this.lambda$dumpControllerStateLocked$1$DeviceIdleJobsController(this.f$1, (JobStatus) obj);
            }
        });
        proto.end(mToken);
        proto.end(token);
    }

    public /* synthetic */ void lambda$dumpControllerStateLocked$1$DeviceIdleJobsController(ProtoOutputStream proto, JobStatus jobStatus) {
        long jsToken = proto.start(2246267895810L);
        jobStatus.writeToShortProto(proto, 1146756268033L);
        proto.write(1120986464258L, jobStatus.getSourceUid());
        proto.write(1138166333443L, jobStatus.getSourcePackageName());
        proto.write(1133871366148L, (jobStatus.satisfiedConstraints & DumpState.DUMP_APEX) != 0);
        proto.write(1133871366149L, jobStatus.dozeWhitelisted);
        proto.write(1133871366150L, this.mAllowInIdleJobs.contains(jobStatus));
        proto.end(jsToken);
    }

    final class DeviceIdleUpdateFunctor implements Consumer<JobStatus> {
        boolean mChanged;

        DeviceIdleUpdateFunctor() {
        }

        public void accept(JobStatus jobStatus) {
            this.mChanged |= DeviceIdleJobsController.this.updateTaskStateLocked(jobStatus);
        }
    }

    final class DeviceIdleJobsDelayHandler extends Handler {
        public DeviceIdleJobsDelayHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                synchronized (DeviceIdleJobsController.this.mLock) {
                    DeviceIdleJobsController.this.mDeviceIdleUpdateFunctor.mChanged = false;
                    DeviceIdleJobsController.this.mService.getJobStore().forEachJob(DeviceIdleJobsController.this.mDeviceIdleUpdateFunctor);
                    if (DeviceIdleJobsController.this.mDeviceIdleUpdateFunctor.mChanged) {
                        DeviceIdleJobsController.this.mStateChangedListener.onControllerStateChanged();
                    }
                }
            }
        }
    }
}
