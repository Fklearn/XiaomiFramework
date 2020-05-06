package com.android.server.am;

import android.app.ActivityManagerNative;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.util.Slog;
import com.android.server.pm.PackageManagerService;
import java.util.ArrayList;

public class ProcessKillerIdler extends JobService {
    static final long APP_MEM_THRESHOLD = 300;
    static final long BACKUP_APP_MEM_THRESHOLD = 100;
    static final long BACKUP_MEM_THRESHOLD = 1000;
    static final long CHECK_FREE_MEM_TIME = 21600000;
    private static int PROCESS_KILL_JOB_ID = 100;
    private static final String TAG = "ProcessKillerIdler";
    static final ArrayList<String> blackList = new ArrayList<>();
    private static ComponentName cm = new ComponentName(PackageManagerService.PLATFORM_PACKAGE_NAME, ProcessKillerIdler.class.getName());
    private ActivityManagerService mAm = ActivityManagerNative.getDefault();

    static {
        blackList.add("com.tencent.mm");
        blackList.add("com.tencent.mobileqq");
    }

    /* JADX INFO: finally extract failed */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00fe, code lost:
        r0 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean onStartJob(android.app.job.JobParameters r18) {
        /*
            r17 = this;
            r1 = r17
            java.lang.String r0 = "ProcessKillerIdler"
            java.lang.String r2 = "ProcessKillerIdler onStartJob"
            android.util.Slog.w(r0, r2)
            com.android.server.am.ActivityManagerService r2 = r1.mAm
            monitor-enter(r2)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x00f6 }
            com.android.server.am.ActivityManagerService r0 = r1.mAm     // Catch:{ all -> 0x00f6 }
            r3 = 300(0x12c, float:4.2E-43)
            r4 = 0
            java.util.List r0 = com.android.server.am.ProcessUtils.getProcessListByAdj(r0, r3, r4)     // Catch:{ all -> 0x00f6 }
            int r3 = r0.size()     // Catch:{ all -> 0x00f6 }
            r4 = 0
            java.util.ArrayList r6 = new java.util.ArrayList     // Catch:{ all -> 0x00f6 }
            r6.<init>()     // Catch:{ all -> 0x00f6 }
            r7 = 0
        L_0x0024:
            r8 = 1
            r9 = 1024(0x400, double:5.06E-321)
            if (r7 >= r3) goto L_0x0086
            java.lang.Object r11 = r0.get(r7)     // Catch:{ all -> 0x00f6 }
            com.android.server.am.ProcessRecord r11 = (com.android.server.am.ProcessRecord) r11     // Catch:{ all -> 0x00f6 }
            java.util.ArrayList<java.lang.String> r12 = blackList     // Catch:{ all -> 0x00f6 }
            java.lang.String r13 = r11.processName     // Catch:{ all -> 0x00f6 }
            boolean r12 = r12.contains(r13)     // Catch:{ all -> 0x00f6 }
            if (r12 == 0) goto L_0x0077
            long r12 = r11.lastPss     // Catch:{ all -> 0x00f6 }
            long r12 = r12 / r9
            r14 = 300(0x12c, double:1.48E-321)
            int r12 = (r12 > r14 ? 1 : (r12 == r14 ? 0 : -1))
            if (r12 <= 0) goto L_0x0077
            java.lang.String r12 = "ProcessKillerIdler"
            java.lang.StringBuilder r13 = new java.lang.StringBuilder     // Catch:{ all -> 0x00f6 }
            r13.<init>()     // Catch:{ all -> 0x00f6 }
            java.lang.String r14 = "killing process "
            r13.append(r14)     // Catch:{ all -> 0x00f6 }
            java.lang.String r14 = r11.processName     // Catch:{ all -> 0x00f6 }
            r13.append(r14)     // Catch:{ all -> 0x00f6 }
            java.lang.String r14 = " pid "
            r13.append(r14)     // Catch:{ all -> 0x00f6 }
            int r14 = r11.pid     // Catch:{ all -> 0x00f6 }
            r13.append(r14)     // Catch:{ all -> 0x00f6 }
            java.lang.String r14 = " size "
            r13.append(r14)     // Catch:{ all -> 0x00f6 }
            long r14 = r11.lastPss     // Catch:{ all -> 0x00f6 }
            long r14 = r14 / r9
            r13.append(r14)     // Catch:{ all -> 0x00f6 }
            java.lang.String r9 = r13.toString()     // Catch:{ all -> 0x00f6 }
            android.util.Slog.w(r12, r9)     // Catch:{ all -> 0x00f6 }
            java.lang.String r9 = "low mem kill"
            r11.kill(r9, r8)     // Catch:{ all -> 0x00f6 }
            goto L_0x0083
        L_0x0077:
            int r8 = r11.setAdj     // Catch:{ all -> 0x00f6 }
            r9 = 400(0x190, float:5.6E-43)
            if (r8 != r9) goto L_0x0083
            long r8 = r11.lastPss     // Catch:{ all -> 0x00f6 }
            long r4 = r4 + r8
            r6.add(r11)     // Catch:{ all -> 0x00f6 }
        L_0x0083:
            int r7 = r7 + 1
            goto L_0x0024
        L_0x0086:
            long r11 = r4 / r9
            r13 = 1000(0x3e8, double:4.94E-321)
            int r7 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            r11 = 0
            if (r7 < 0) goto L_0x00e9
            r7 = r11
        L_0x0090:
            int r12 = r6.size()     // Catch:{ all -> 0x00f6 }
            if (r7 >= r12) goto L_0x00e9
            java.lang.Object r12 = r6.get(r7)     // Catch:{ all -> 0x00f6 }
            com.android.server.am.ProcessRecord r12 = (com.android.server.am.ProcessRecord) r12     // Catch:{ all -> 0x00f6 }
            long r13 = r12.lastPss     // Catch:{ all -> 0x00f6 }
            long r13 = r13 / r9
            r15 = 100
            int r13 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1))
            if (r13 <= 0) goto L_0x00e2
            java.lang.String r13 = "ProcessKillerIdler"
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ all -> 0x00f6 }
            r14.<init>()     // Catch:{ all -> 0x00f6 }
            java.lang.String r15 = "killing process "
            r14.append(r15)     // Catch:{ all -> 0x00f6 }
            java.lang.String r15 = r12.processName     // Catch:{ all -> 0x00f6 }
            r14.append(r15)     // Catch:{ all -> 0x00f6 }
            java.lang.String r15 = " pid "
            r14.append(r15)     // Catch:{ all -> 0x00f6 }
            int r15 = r12.pid     // Catch:{ all -> 0x00f6 }
            r14.append(r15)     // Catch:{ all -> 0x00f6 }
            java.lang.String r15 = " size "
            r14.append(r15)     // Catch:{ all -> 0x00f6 }
            long r8 = r12.lastPss     // Catch:{ all -> 0x00f6 }
            r15 = 1024(0x400, double:5.06E-321)
            long r8 = r8 / r15
            r14.append(r8)     // Catch:{ all -> 0x00f6 }
            java.lang.String r8 = " reason: backup procs' totalMem is too big, need to kill big mem proc"
            r14.append(r8)     // Catch:{ all -> 0x00f6 }
            java.lang.String r8 = r14.toString()     // Catch:{ all -> 0x00f6 }
            android.util.Slog.w(r13, r8)     // Catch:{ all -> 0x00f6 }
            java.lang.String r8 = "low mem kill"
            r9 = 1
            r12.kill(r8, r9)     // Catch:{ all -> 0x00f6 }
            goto L_0x00e4
        L_0x00e2:
            r15 = r9
            r9 = r8
        L_0x00e4:
            int r7 = r7 + 1
            r8 = r9
            r9 = r15
            goto L_0x0090
        L_0x00e9:
            monitor-exit(r2)     // Catch:{ all -> 0x00f6 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            r3 = r18
            r1.jobFinished(r3, r11)
            schedule(r17)
            return r11
        L_0x00f6:
            r0 = move-exception
            r3 = r18
        L_0x00f9:
            monitor-exit(r2)     // Catch:{ all -> 0x00fe }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x00fe:
            r0 = move-exception
            goto L_0x00f9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ProcessKillerIdler.onStartJob(android.app.job.JobParameters):boolean");
    }

    public boolean onStopJob(JobParameters params) {
        Slog.w(TAG, "ProcessKillerIdler onStopJob");
        return false;
    }

    public static void schedule(Context context) {
        JobInfo.Builder builder = new JobInfo.Builder(PROCESS_KILL_JOB_ID, cm);
        builder.setMinimumLatency(CHECK_FREE_MEM_TIME);
        ((JobScheduler) context.getSystemService("jobscheduler")).schedule(builder.build());
    }
}
