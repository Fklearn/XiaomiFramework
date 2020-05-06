package com.android.server.backup;

import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.RemoteException;
import android.util.SparseBooleanArray;
import android.util.SparseLongArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.pm.PackageManagerService;

public class KeyValueBackupJob extends JobService {
    private static final long MAX_DEFERRAL = 86400000;
    @VisibleForTesting
    public static final int MAX_JOB_ID = 52418896;
    @VisibleForTesting
    public static final int MIN_JOB_ID = 52417896;
    private static final String TAG = "KeyValueBackupJob";
    private static final String USER_ID_EXTRA_KEY = "userId";
    private static ComponentName sKeyValueJobService = new ComponentName(PackageManagerService.PLATFORM_PACKAGE_NAME, KeyValueBackupJob.class.getName());
    @GuardedBy({"KeyValueBackupJob.class"})
    private static final SparseLongArray sNextScheduledForUserId = new SparseLongArray();
    @GuardedBy({"KeyValueBackupJob.class"})
    private static final SparseBooleanArray sScheduledForUserId = new SparseBooleanArray();

    public static void schedule(int userId, Context ctx, BackupManagerConstants constants) {
        schedule(userId, ctx, 0, constants);
    }

    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:657)
        	at java.util.ArrayList.get(ArrayList.java:433)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processIf(RegionMaker.java:698)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:123)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:598)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:49)
        */
    public static void schedule(int r19, android.content.Context r20, long r21, com.android.server.backup.BackupManagerConstants r23) {
        /*
            r1 = r19
            java.lang.Class<com.android.server.backup.KeyValueBackupJob> r2 = com.android.server.backup.KeyValueBackupJob.class
            monitor-enter(r2)
            android.util.SparseBooleanArray r0 = sScheduledForUserId     // Catch:{ all -> 0x00b8 }
            boolean r0 = r0.get(r1)     // Catch:{ all -> 0x00b8 }
            if (r0 == 0) goto L_0x000f
            monitor-exit(r2)     // Catch:{ all -> 0x00b8 }
            return
        L_0x000f:
            monitor-enter(r23)     // Catch:{ all -> 0x00b8 }
            long r3 = r23.getKeyValueBackupIntervalMilliseconds()     // Catch:{ all -> 0x00af }
            long r5 = r23.getKeyValueBackupFuzzMilliseconds()     // Catch:{ all -> 0x00af }
            int r0 = r23.getKeyValueBackupRequiredNetworkType()     // Catch:{ all -> 0x00af }
            boolean r7 = r23.getKeyValueBackupRequireCharging()     // Catch:{ all -> 0x00af }
            monitor-exit(r23)     // Catch:{ all -> 0x00af }
            r8 = 0
            int r8 = (r21 > r8 ? 1 : (r21 == r8 ? 0 : -1))
            if (r8 > 0) goto L_0x0034
            java.util.Random r8 = new java.util.Random     // Catch:{ all -> 0x00b8 }
            r8.<init>()     // Catch:{ all -> 0x00b8 }
            int r9 = (int) r5     // Catch:{ all -> 0x00b8 }
            int r8 = r8.nextInt(r9)     // Catch:{ all -> 0x00b8 }
            long r8 = (long) r8
            long r8 = r8 + r3
            goto L_0x0036
        L_0x0034:
            r8 = r21
        L_0x0036:
            java.lang.String r10 = "KeyValueBackupJob"
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ab }
            r11.<init>()     // Catch:{ all -> 0x00ab }
            java.lang.String r12 = "Scheduling k/v pass in "
            r11.append(r12)     // Catch:{ all -> 0x00ab }
            r12 = 1000(0x3e8, double:4.94E-321)
            long r12 = r8 / r12
            r14 = 60
            long r12 = r12 / r14
            r11.append(r12)     // Catch:{ all -> 0x00ab }
            java.lang.String r12 = " minutes"
            r11.append(r12)     // Catch:{ all -> 0x00ab }
            java.lang.String r11 = r11.toString()     // Catch:{ all -> 0x00ab }
            android.util.Slog.v(r10, r11)     // Catch:{ all -> 0x00ab }
            android.app.job.JobInfo$Builder r10 = new android.app.job.JobInfo$Builder     // Catch:{ all -> 0x00ab }
            int r11 = getJobIdForUserId(r19)     // Catch:{ all -> 0x00ab }
            android.content.ComponentName r12 = sKeyValueJobService     // Catch:{ all -> 0x00ab }
            r10.<init>(r11, r12)     // Catch:{ all -> 0x00ab }
            android.app.job.JobInfo$Builder r10 = r10.setMinimumLatency(r8)     // Catch:{ all -> 0x00ab }
            android.app.job.JobInfo$Builder r10 = r10.setRequiredNetworkType(r0)     // Catch:{ all -> 0x00ab }
            android.app.job.JobInfo$Builder r10 = r10.setRequiresCharging(r7)     // Catch:{ all -> 0x00ab }
            r11 = 86400000(0x5265c00, double:4.2687272E-316)
            android.app.job.JobInfo$Builder r10 = r10.setOverrideDeadline(r11)     // Catch:{ all -> 0x00ab }
            android.os.Bundle r11 = new android.os.Bundle     // Catch:{ all -> 0x00ab }
            r11.<init>()     // Catch:{ all -> 0x00ab }
            java.lang.String r12 = "userId"
            r11.putInt(r12, r1)     // Catch:{ all -> 0x00ab }
            r10.setTransientExtras(r11)     // Catch:{ all -> 0x00ab }
            java.lang.String r12 = "jobscheduler"
            r13 = r20
            java.lang.Object r12 = r13.getSystemService(r12)     // Catch:{ all -> 0x00bf }
            android.app.job.JobScheduler r12 = (android.app.job.JobScheduler) r12     // Catch:{ all -> 0x00bf }
            android.app.job.JobInfo r14 = r10.build()     // Catch:{ all -> 0x00bf }
            r12.schedule(r14)     // Catch:{ all -> 0x00bf }
            android.util.SparseBooleanArray r14 = sScheduledForUserId     // Catch:{ all -> 0x00bf }
            r15 = 1
            r14.put(r1, r15)     // Catch:{ all -> 0x00bf }
            android.util.SparseLongArray r14 = sNextScheduledForUserId     // Catch:{ all -> 0x00bf }
            long r15 = java.lang.System.currentTimeMillis()     // Catch:{ all -> 0x00bf }
            r17 = r3
            long r3 = r15 + r8
            r14.put(r1, r3)     // Catch:{ all -> 0x00bf }
            monitor-exit(r2)     // Catch:{ all -> 0x00bf }
            return
        L_0x00ab:
            r0 = move-exception
            r13 = r20
            goto L_0x00bd
        L_0x00af:
            r0 = move-exception
            r13 = r20
        L_0x00b2:
            monitor-exit(r23)     // Catch:{ all -> 0x00b6 }
            throw r0     // Catch:{ all -> 0x00b4 }
        L_0x00b4:
            r0 = move-exception
            goto L_0x00bb
        L_0x00b6:
            r0 = move-exception
            goto L_0x00b2
        L_0x00b8:
            r0 = move-exception
            r13 = r20
        L_0x00bb:
            r8 = r21
        L_0x00bd:
            monitor-exit(r2)     // Catch:{ all -> 0x00bf }
            throw r0
        L_0x00bf:
            r0 = move-exception
            goto L_0x00bd
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.KeyValueBackupJob.schedule(int, android.content.Context, long, com.android.server.backup.BackupManagerConstants):void");
    }

    public static void cancel(int userId, Context ctx) {
        synchronized (KeyValueBackupJob.class) {
            ((JobScheduler) ctx.getSystemService("jobscheduler")).cancel(getJobIdForUserId(userId));
            clearScheduledForUserId(userId);
        }
    }

    public static long nextScheduled(int userId) {
        long j;
        synchronized (KeyValueBackupJob.class) {
            j = sNextScheduledForUserId.get(userId);
        }
        return j;
    }

    @VisibleForTesting
    public static boolean isScheduled(int userId) {
        boolean z;
        synchronized (KeyValueBackupJob.class) {
            z = sScheduledForUserId.get(userId);
        }
        return z;
    }

    public boolean onStartJob(JobParameters params) {
        int userId = params.getTransientExtras().getInt(USER_ID_EXTRA_KEY);
        synchronized (KeyValueBackupJob.class) {
            clearScheduledForUserId(userId);
        }
        try {
            BackupManagerService.getInstance().backupNowForUser(userId);
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean onStopJob(JobParameters params) {
        return false;
    }

    @GuardedBy({"KeyValueBackupJob.class"})
    private static void clearScheduledForUserId(int userId) {
        sScheduledForUserId.delete(userId);
        sNextScheduledForUserId.delete(userId);
    }

    private static int getJobIdForUserId(int userId) {
        return JobIdManager.getJobIdForUserId(MIN_JOB_ID, 52418896, userId);
    }
}
