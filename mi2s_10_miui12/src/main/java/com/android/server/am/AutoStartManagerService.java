package com.android.server.am;

import android.app.AppGlobals;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.Process;
import android.util.EventLog;
import android.util.Slog;
import com.android.server.AppOpsServiceState;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import miui.io.IoUtils;
import miui.os.SystemProperties;

public class AutoStartManagerService {
    private static final String ACCT_CGROUP_PATH = "/acct";
    public static final boolean CONFIG_LOW_RAM = SystemProperties.getBoolean("ro.config.low_ram", false);
    public static final boolean CONFIG_PER_APP_MEMCG = SystemProperties.getBoolean("ro.config.per_app_memcg", CONFIG_LOW_RAM);
    private static final boolean ENABLE_SIGSTOP_KILL = SystemProperties.getBoolean("persist.proc.enable_sigstop", false);
    private static final String MEM_CGROUP_PATH = "/dev/memcg/apps";
    private static final String MEM_CGROUP_TASKS = "/dev/memcg/apps/tasks";
    private static final String TAG = "AutoStartManagerService";
    private static String sCgroupRootPath = null;

    public static boolean isAllowStartService(Context context, Intent service, int userId) {
        try {
            ApplicationInfo applicationInfo = AppGlobals.getPackageManager().getApplicationInfo(service.getComponent().getPackageName(), 0, userId);
            if (applicationInfo == null) {
                return true;
            }
            return isAllowStartService(context, service, userId, applicationInfo.uid);
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:31:?, code lost:
        com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isAllowStartService(android.content.Context r11, android.content.Intent r12, int r13, int r14) {
        /*
            r0 = 1
            java.lang.String r1 = "appops"
            java.lang.Object r1 = r11.getSystemService(r1)     // Catch:{ Exception -> 0x00a6 }
            android.app.AppOpsManager r1 = (android.app.AppOpsManager) r1     // Catch:{ Exception -> 0x00a6 }
            if (r1 != 0) goto L_0x000c
            return r0
        L_0x000c:
            android.content.ComponentName r2 = r12.getComponent()     // Catch:{ Exception -> 0x00a6 }
            java.lang.String r2 = r2.getPackageName()     // Catch:{ Exception -> 0x00a6 }
            r3 = 10008(0x2718, float:1.4024E-41)
            int r2 = r1.checkOpNoThrow(r3, r14, r2)     // Catch:{ Exception -> 0x00a6 }
            if (r2 != 0) goto L_0x0028
            android.content.ComponentName r4 = r12.getComponent()     // Catch:{ Exception -> 0x00a6 }
            java.lang.String r4 = r4.getPackageName()     // Catch:{ Exception -> 0x00a6 }
            r1.noteOpNoThrow(r3, r14, r4)     // Catch:{ Exception -> 0x00a6 }
            return r0
        L_0x0028:
            android.app.IActivityManager r4 = android.app.ActivityManagerNative.getDefault()     // Catch:{ Exception -> 0x00a6 }
            com.android.server.am.ActivityManagerService r4 = (com.android.server.am.ActivityManagerService) r4     // Catch:{ Exception -> 0x00a6 }
            android.content.pm.IPackageManager r5 = android.app.AppGlobals.getPackageManager()     // Catch:{ Exception -> 0x00a6 }
            r6 = 1024(0x400, float:1.435E-42)
            r7 = 0
            android.content.pm.ResolveInfo r5 = r5.resolveService(r12, r7, r6, r13)     // Catch:{ Exception -> 0x00a6 }
            if (r5 == 0) goto L_0x003d
            android.content.pm.ServiceInfo r7 = r5.serviceInfo     // Catch:{ Exception -> 0x00a6 }
        L_0x003d:
            r6 = r7
            if (r6 != 0) goto L_0x0041
            return r0
        L_0x0041:
            android.content.pm.ServiceInfo r7 = r5.serviceInfo     // Catch:{ Exception -> 0x00a6 }
            android.content.pm.ApplicationInfo r7 = r7.applicationInfo     // Catch:{ Exception -> 0x00a6 }
            int r7 = r7.flags     // Catch:{ Exception -> 0x00a6 }
            r7 = r7 & r0
            if (r7 != 0) goto L_0x00a5
            android.content.pm.ServiceInfo r7 = r5.serviceInfo     // Catch:{ Exception -> 0x00a6 }
            android.content.pm.ApplicationInfo r7 = r7.applicationInfo     // Catch:{ Exception -> 0x00a6 }
            java.lang.String r7 = r7.packageName     // Catch:{ Exception -> 0x00a6 }
            r8 = 0
            boolean r7 = miui.content.pm.PreloadedAppPolicy.isProtectedDataApp(r11, r7, r8)     // Catch:{ Exception -> 0x00a6 }
            if (r7 == 0) goto L_0x0058
            goto L_0x00a5
        L_0x0058:
            monitor-enter(r4)     // Catch:{ Exception -> 0x00a6 }
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x009f }
            java.lang.String r7 = r6.processName     // Catch:{ all -> 0x009f }
            com.android.server.am.ProcessRecord r7 = r4.getProcessRecordLocked(r7, r14, r8)     // Catch:{ all -> 0x009f }
            if (r7 != 0) goto L_0x009a
            android.content.ComponentName r9 = r12.getComponent()     // Catch:{ all -> 0x009f }
            java.lang.String r9 = r9.getPackageName()     // Catch:{ all -> 0x009f }
            r1.noteOpNoThrow(r3, r14, r9)     // Catch:{ all -> 0x009f }
            java.lang.String r3 = "AutoStartManagerService"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x009f }
            r9.<init>()     // Catch:{ all -> 0x009f }
            java.lang.String r10 = "MIUILOG- Reject service :"
            r9.append(r10)     // Catch:{ all -> 0x009f }
            r9.append(r12)     // Catch:{ all -> 0x009f }
            java.lang.String r10 = " userId : "
            r9.append(r10)     // Catch:{ all -> 0x009f }
            r9.append(r13)     // Catch:{ all -> 0x009f }
            java.lang.String r10 = " uid : "
            r9.append(r10)     // Catch:{ all -> 0x009f }
            r9.append(r14)     // Catch:{ all -> 0x009f }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x009f }
            android.util.Slog.i(r3, r9)     // Catch:{ all -> 0x009f }
            monitor-exit(r4)     // Catch:{ all -> 0x009f }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return r8
        L_0x009a:
            monitor-exit(r4)     // Catch:{ all -> 0x009f }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()     // Catch:{ Exception -> 0x00a6 }
            goto L_0x00aa
        L_0x009f:
            r3 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x009f }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()     // Catch:{ Exception -> 0x00a6 }
            throw r3     // Catch:{ Exception -> 0x00a6 }
        L_0x00a5:
            return r0
        L_0x00a6:
            r1 = move-exception
            r1.printStackTrace()
        L_0x00aa:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.AutoStartManagerService.isAllowStartService(android.content.Context, android.content.Intent, int, int):boolean");
    }

    public static boolean canRestartServiceLocked(String packageName, int uid, ActivityManagerService service) {
        if (AppOpsServiceState.isCtsIgnore(packageName) || service.mAppOpsService.noteOperation(10008, uid, packageName) == 0) {
            return true;
        }
        Slog.i(TAG, "MIUILOG- Reject RestartService packageName :" + packageName + " uid : " + uid);
        return false;
    }

    public static void signalStopProcessesLocked(ArrayList<ProcessRecord> procs, boolean allowRestart, String packageName, int uid, final ActivityManagerService ams) {
        if (!ENABLE_SIGSTOP_KILL) {
            return;
        }
        if (!allowRestart || !canRestartServiceLocked(packageName, uid, ams)) {
            int N = procs.size();
            for (int i = 0; i < N; i++) {
                ProcessRecord proc = procs.get(i);
                sendSignalToProcessLocked(proc.uid, proc.pid, proc.processName, proc.setAdj, 19, false);
            }
            final ArrayList<ProcessRecord> tmpProcs = (ArrayList) procs.clone();
            ProcessList.sKillHandler.postDelayed(new Runnable() {
                public void run() {
                    synchronized (ActivityManagerService.this) {
                        try {
                            ActivityManagerService.boostPriorityForLockedSection();
                            int size = tmpProcs.size();
                            for (int i = 0; i < size; i++) {
                                ProcessRecord tmpProc = (ProcessRecord) tmpProcs.get(i);
                                AutoStartManagerService.sendSignalToProcessLocked(tmpProc.uid, tmpProc.pid, tmpProc.processName, tmpProc.setAdj, 18, true);
                            }
                        } catch (Throwable th) {
                            while (true) {
                                ActivityManagerService.resetPriorityAfterLockedSection();
                                throw th;
                            }
                        }
                    }
                    ActivityManagerService.resetPriorityAfterLockedSection();
                }
            }, 500);
        }
    }

    static void sendSignalToProcessLocked(int uid, int pid, String processName, int setAdj, int signal, boolean needKill) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(getCgroupRootPath() + "/uid_" + uid + "/pid_" + pid + "/cgroup.procs")));
            while (true) {
                String readLine = reader.readLine();
                String line = readLine;
                if (readLine == null) {
                    break;
                }
                int processId = Integer.parseInt(line);
                if (processId > 0) {
                    Slog.d(TAG, "prepare force stop p:" + processId + " s: " + signal);
                    Process.sendSignal(processId, signal);
                    if (needKill) {
                        EventLog.writeEvent(EventLogTags.AM_KILL, new Object[]{Integer.valueOf(uid), Integer.valueOf(pid), processName, Integer.valueOf(setAdj), "Kill Again"});
                        Process.killProcessQuiet(pid);
                    }
                }
            }
        } catch (IOException e) {
        } catch (Exception e2) {
            e2.printStackTrace();
        } catch (Throwable th) {
            IoUtils.closeQuietly((AutoCloseable) null);
            throw th;
        }
        IoUtils.closeQuietly(reader);
    }

    private static String getCgroupRootPath() {
        String str = sCgroupRootPath;
        if (str != null) {
            return str;
        }
        if (Build.VERSION.SDK_INT <= 26 || !CONFIG_PER_APP_MEMCG || !isMemCgroupAvailable()) {
            sCgroupRootPath = ACCT_CGROUP_PATH;
        } else {
            sCgroupRootPath = MEM_CGROUP_PATH;
        }
        return sCgroupRootPath;
    }

    private static boolean isMemCgroupAvailable() {
        try {
            if (!new File(MEM_CGROUP_TASKS).exists() && !new File(MEM_CGROUP_PATH).canWrite()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
