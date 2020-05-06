package com.android.server.am;

import android.app.AppGlobals;
import android.content.pm.ApplicationInfo;
import android.os.Binder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Slog;
import android.util.SparseArray;
import com.android.server.UiModeManagerService;
import java.util.ArrayList;
import java.util.List;
import miui.process.ProcessManager;

public class ProcessStarter {
    static final int APP_PROTECTION_TIMEOUT = 1800000;
    static final int MAX_PROTECT_APP = 5;
    private static final int START_SUCCESS = 0;
    private static final String TAG = "ProcessStarter";
    private ActivityManagerService mActivityManagerService;
    private SparseArray<List<ProcessPriorityInfo>> mLastProcessesInfo = new SparseArray<>();
    private ProcessManagerService mProcessManagerService;

    public ProcessStarter(ProcessManagerService pms, ActivityManagerService ams) {
        this.mProcessManagerService = pms;
        this.mActivityManagerService = ams;
    }

    /* access modifiers changed from: package-private */
    public ProcessRecord startProcessLocked(String packageName, String processName, int userId, String hostingType) {
        String processName2;
        int callingPid = Binder.getCallingPid();
        if (TextUtils.isEmpty(processName)) {
            processName2 = packageName;
        } else {
            processName2 = processName;
        }
        try {
            ApplicationInfo info = AppGlobals.getPackageManager().getApplicationInfo(packageName, 1024, userId);
            if (info == null) {
                return null;
            }
            ProcessRecord app = (ProcessRecord) this.mActivityManagerService.mProcessList.mProcessNames.get(processName2, info.uid);
            if (app == null) {
                ProcessRecord newApp = this.mActivityManagerService.startProcessLocked(processName2, info, false, 0, new HostingRecord(hostingType, processName2), false, false, false, ExtraActivityManagerService.getProcessNameByPid(callingPid));
                if (newApp == null) {
                    Slog.w(TAG, "startProcess :" + processName2 + " failed!");
                    return null;
                }
                this.mActivityManagerService.updateLruProcessLocked(newApp, false, (ProcessRecord) null);
                return newApp;
            } else if (app.isPersistent()) {
                Slog.w(TAG, "process: " + processName2 + " is persistent, skip!");
                return null;
            } else {
                Slog.i(TAG, "process: " + processName2 + " already exits, just protect");
                return app;
            }
        } catch (RemoteException e) {
            Slog.w(TAG, "error in getApplicationInfo!", e);
            return null;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00e8, code lost:
        r13 = r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x00ea, code lost:
        com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x00ed, code lost:
        return r5;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int startProcesses(java.util.List<miui.process.PreloadProcessData> r20, int r21, boolean r22, int r23, int r24) {
        /*
            r19 = this;
            r1 = r19
            r2 = r21
            r3 = r24
            com.android.server.am.ActivityManagerService r4 = r1.mActivityManagerService
            monitor-enter(r4)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x00ee }
            r0 = 0
            r5 = 0
            r1.restoreLastProcessesInfoLocked(r3)     // Catch:{ all -> 0x00ee }
            com.android.server.am.ProcessManagerService r6 = r1.mProcessManagerService     // Catch:{ all -> 0x00ee }
            com.android.server.am.ProcessManagerService$MainHandler r6 = r6.mHandler     // Catch:{ all -> 0x00ee }
            r6.removeMessages(r3)     // Catch:{ all -> 0x00ee }
            r6 = 0
            r7 = r6
            r18 = r5
            r5 = r0
            r0 = r18
        L_0x001f:
            int r8 = r20.size()     // Catch:{ all -> 0x00ee }
            if (r7 >= r8) goto L_0x00e4
            if (r5 < r2) goto L_0x002d
            r8 = r20
            r12 = r23
            goto L_0x00e8
        L_0x002d:
            r8 = r20
            java.lang.Object r9 = r8.get(r7)     // Catch:{ all -> 0x00e2 }
            miui.process.PreloadProcessData r9 = (miui.process.PreloadProcessData) r9     // Catch:{ all -> 0x00e2 }
            if (r9 == 0) goto L_0x00d8
            java.lang.String r10 = r9.getPackageName()     // Catch:{ all -> 0x00e2 }
            boolean r10 = android.text.TextUtils.isEmpty(r10)     // Catch:{ all -> 0x00e2 }
            if (r10 != 0) goto L_0x00d8
            java.lang.String r10 = r9.getPackageName()     // Catch:{ all -> 0x00e2 }
            java.lang.String r11 = makeHostingTypeFromFlag(r24)     // Catch:{ all -> 0x00e2 }
            r12 = r23
            com.android.server.am.ProcessRecord r11 = r1.startProcessLocked(r10, r10, r12, r11)     // Catch:{ all -> 0x00f8 }
            if (r11 == 0) goto L_0x00d5
            r1.saveProcessInfoLocked(r11, r3)     // Catch:{ all -> 0x00f8 }
            r1.addProtectionLocked(r11, r3)     // Catch:{ all -> 0x00f8 }
            int r13 = r0 + 1
            r0 = 5
            if (r13 < r0) goto L_0x0076
            java.lang.String r0 = "ProcessStarter"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x00f8 }
            r6.<init>()     // Catch:{ all -> 0x00f8 }
            java.lang.String r14 = "preload and protect processes max limit is: 5, while now count is: "
            r6.append(r14)     // Catch:{ all -> 0x00f8 }
            r6.append(r2)     // Catch:{ all -> 0x00f8 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x00f8 }
            android.util.Slog.w(r0, r6)     // Catch:{ all -> 0x00f8 }
            goto L_0x00e9
        L_0x0076:
            com.android.server.am.ProcessManagerService r0 = r1.mProcessManagerService     // Catch:{ all -> 0x00f8 }
            com.android.server.am.ProcessManagerService$MainHandler r0 = r0.mHandler     // Catch:{ all -> 0x00f8 }
            r14 = 1800000(0x1b7740, double:8.89318E-318)
            r0.sendEmptyMessageDelayed(r3, r14)     // Catch:{ all -> 0x00f8 }
            boolean r0 = r9.startActivity()     // Catch:{ all -> 0x00f8 }
            if (r0 == 0) goto L_0x00d1
            android.content.Intent r0 = r9.getIntent()     // Catch:{ all -> 0x00f8 }
            if (r0 == 0) goto L_0x00d1
            android.content.Intent r0 = r9.getIntent()     // Catch:{ all -> 0x00f8 }
            r14 = 4
            r0.addMiuiFlags(r14)     // Catch:{ all -> 0x00f8 }
            java.lang.Class<com.android.server.am.ActivityManagerService> r0 = com.android.server.am.ActivityManagerService.class
            java.lang.String r14 = "startActivityInMiuiAiMode"
            r15 = 1
            java.lang.Object[] r6 = new java.lang.Object[r15]     // Catch:{ Exception -> 0x00c6 }
            android.content.Intent r17 = r9.getIntent()     // Catch:{ Exception -> 0x00c6 }
            r16 = 0
            r6[r16] = r17     // Catch:{ Exception -> 0x00c4 }
            java.lang.reflect.Method r0 = miui.util.ReflectionUtils.findMethodBestMatch(r0, r14, r6)     // Catch:{ Exception -> 0x00c6 }
            com.android.server.am.ActivityManagerService r6 = r1.mActivityManagerService     // Catch:{ Exception -> 0x00c6 }
            java.lang.Object[] r14 = new java.lang.Object[r15]     // Catch:{ Exception -> 0x00c6 }
            android.content.Intent r15 = r9.getIntent()     // Catch:{ Exception -> 0x00c6 }
            r16 = 0
            r14[r16] = r15     // Catch:{ Exception -> 0x00c4 }
            java.lang.Object r6 = r0.invoke(r6, r14)     // Catch:{ Exception -> 0x00c4 }
            java.lang.Integer r6 = (java.lang.Integer) r6     // Catch:{ Exception -> 0x00c4 }
            int r6 = r6.intValue()     // Catch:{ Exception -> 0x00c4 }
            if (r6 != 0) goto L_0x00c2
            int r5 = r5 + 1
        L_0x00c2:
            r0 = r13
            goto L_0x00dc
        L_0x00c4:
            r0 = move-exception
            goto L_0x00cd
        L_0x00c6:
            r0 = move-exception
            r16 = 0
            goto L_0x00cd
        L_0x00ca:
            r0 = move-exception
            r16 = r6
        L_0x00cd:
            r0.printStackTrace()     // Catch:{ all -> 0x00f8 }
            goto L_0x00d3
        L_0x00d1:
            r16 = r6
        L_0x00d3:
            r0 = r13
            goto L_0x00dc
        L_0x00d5:
            r16 = r6
            goto L_0x00dc
        L_0x00d8:
            r12 = r23
            r16 = r6
        L_0x00dc:
            int r7 = r7 + 1
            r6 = r16
            goto L_0x001f
        L_0x00e2:
            r0 = move-exception
            goto L_0x00f1
        L_0x00e4:
            r8 = r20
            r12 = r23
        L_0x00e8:
            r13 = r0
        L_0x00e9:
            monitor-exit(r4)     // Catch:{ all -> 0x00f8 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return r5
        L_0x00ee:
            r0 = move-exception
            r8 = r20
        L_0x00f1:
            r12 = r23
        L_0x00f3:
            monitor-exit(r4)     // Catch:{ all -> 0x00f8 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x00f8:
            r0 = move-exception
            goto L_0x00f3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ProcessStarter.startProcesses(java.util.List, int, boolean, int, int):int");
    }

    /* access modifiers changed from: package-private */
    public void saveProcessInfoLocked(ProcessRecord app, int flag) {
        ProcessPriorityInfo lastProcess = new ProcessPriorityInfo();
        lastProcess.app = app;
        lastProcess.maxAdj = app.maxAdj;
        lastProcess.maxProcState = app.maxProcState;
        List<ProcessPriorityInfo> lastProcessList = this.mLastProcessesInfo.get(flag);
        if (lastProcessList == null) {
            lastProcessList = new ArrayList<>();
            this.mLastProcessesInfo.put(flag, lastProcessList);
        }
        lastProcessList.add(lastProcess);
    }

    /* access modifiers changed from: package-private */
    public void addProtectionLocked(ProcessRecord app, int flag) {
        if (flag == 1) {
            app.maxAdj = ProcessManager.AI_MAX_ADJ;
            app.maxProcState = 14;
        } else if (flag == 2) {
            app.maxAdj = ProcessManager.FAST_RESTART_MAX_ADJ;
            app.maxProcState = 16;
        }
    }

    /* access modifiers changed from: package-private */
    public void restoreLastProcessesInfoLocked(int flag) {
        List<ProcessPriorityInfo> lastProcessInfoList = this.mLastProcessesInfo.get(flag);
        if (lastProcessInfoList != null && !lastProcessInfoList.isEmpty()) {
            for (int i = 0; i < lastProcessInfoList.size(); i++) {
                ProcessPriorityInfo process = lastProcessInfoList.get(i);
                if (this.mProcessManagerService.getProcessPolicy().isLockedApplication(process.app.processName, process.app.userId)) {
                    Slog.i(TAG, "user: " + process.app.userId + ", packageName: " + process.app.processName + " was Locked.");
                    process.app.maxAdj = ProcessManager.LOCKED_MAX_ADJ;
                    process.app.maxProcState = 14;
                } else {
                    process.app.maxAdj = process.maxAdj;
                    process.app.maxProcState = process.maxProcState;
                }
            }
            lastProcessInfoList.clear();
        }
    }

    static class ProcessPriorityInfo {
        ProcessRecord app = null;
        int maxAdj = ProcessManager.DEFAULT_MAX_ADJ;
        int maxProcState = 21;

        ProcessPriorityInfo() {
        }
    }

    public static String makeHostingTypeFromFlag(int flag) {
        if (flag == 1) {
            return "AI";
        }
        if (flag != 2) {
            return UiModeManagerService.Shell.NIGHT_MODE_STR_UNKNOWN;
        }
        return "FastRestart";
    }
}
