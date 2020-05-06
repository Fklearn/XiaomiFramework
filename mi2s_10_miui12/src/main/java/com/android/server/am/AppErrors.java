package com.android.server.am;

import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.ApplicationErrorReport;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.server.am.SplitScreenReporter;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.EventLog;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import com.android.internal.app.ProcessMap;
import com.android.internal.logging.MetricsLogger;
import com.android.server.PackageWatchdog;
import com.android.server.RescueParty;
import com.android.server.am.AppErrorDialog;
import com.android.server.wm.WindowProcessController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Collections;

class AppErrors {
    private static final String TAG = "ActivityManager";
    private ArraySet<String> mAppsNotReportingCrashes;
    private final ProcessMap<BadProcessInfo> mBadProcesses = new ProcessMap<>();
    private final Context mContext;
    private final PackageWatchdog mPackageWatchdog;
    private final ProcessMap<Long> mProcessCrashTimes = new ProcessMap<>();
    private final ProcessMap<Long> mProcessCrashTimesPersistent = new ProcessMap<>();
    private final ActivityManagerService mService;

    AppErrors(Context context, ActivityManagerService service, PackageWatchdog watchdog) {
        context.assertRuntimeOverlayThemable();
        this.mService = service;
        this.mContext = context;
        this.mPackageWatchdog = watchdog;
    }

    /* access modifiers changed from: package-private */
    public void writeToProto(ProtoOutputStream proto, long fieldId, String dumpPackage) {
        long token;
        SparseArray<BadProcessInfo> uids;
        String pname;
        ArrayMap<String, SparseArray<BadProcessInfo>> pmap;
        long token2;
        String pname2;
        int procCount;
        int uidCount;
        ArrayMap<String, SparseArray<Long>> pmap2;
        ProtoOutputStream protoOutputStream = proto;
        String str = dumpPackage;
        if (!this.mProcessCrashTimes.getMap().isEmpty() || !this.mBadProcesses.getMap().isEmpty()) {
            long token3 = proto.start(fieldId);
            long now = SystemClock.uptimeMillis();
            protoOutputStream.write(1112396529665L, now);
            long j = 1138166333441L;
            long j2 = 2246267895810L;
            if (!this.mProcessCrashTimes.getMap().isEmpty()) {
                ArrayMap<String, SparseArray<Long>> pmap3 = this.mProcessCrashTimes.getMap();
                int procCount2 = pmap3.size();
                int ip = 0;
                while (ip < procCount2) {
                    long ctoken = protoOutputStream.start(j2);
                    String pname3 = pmap3.keyAt(ip);
                    SparseArray<Long> uids2 = pmap3.valueAt(ip);
                    long now2 = now;
                    int uidCount2 = uids2.size();
                    protoOutputStream.write(j, pname3);
                    int i = 0;
                    while (i < uidCount2) {
                        int puid = uids2.keyAt(i);
                        ProcessRecord r = (ProcessRecord) this.mService.getProcessNames().get(pname3, puid);
                        if (str != null) {
                            if (r != null) {
                                uidCount = uidCount2;
                                if (!r.pkgList.containsKey(str)) {
                                    token2 = token3;
                                    pmap2 = pmap3;
                                    procCount = procCount2;
                                    pname2 = pname3;
                                }
                            } else {
                                uidCount = uidCount2;
                                token2 = token3;
                                pmap2 = pmap3;
                                procCount = procCount2;
                                pname2 = pname3;
                            }
                            i++;
                            pmap3 = pmap2;
                            uidCount2 = uidCount;
                            procCount2 = procCount;
                            pname3 = pname2;
                            token3 = token2;
                        } else {
                            uidCount = uidCount2;
                        }
                        pmap2 = pmap3;
                        procCount = procCount2;
                        ProcessRecord processRecord = r;
                        pname2 = pname3;
                        long etoken = protoOutputStream.start(2246267895810L);
                        protoOutputStream.write(1120986464257L, puid);
                        token2 = token3;
                        protoOutputStream.write(1112396529666L, uids2.valueAt(i).longValue());
                        protoOutputStream.end(etoken);
                        i++;
                        pmap3 = pmap2;
                        uidCount2 = uidCount;
                        procCount2 = procCount;
                        pname3 = pname2;
                        token3 = token2;
                    }
                    int i2 = uidCount2;
                    ArrayMap<String, SparseArray<Long>> arrayMap = pmap3;
                    int i3 = procCount2;
                    String str2 = pname3;
                    protoOutputStream.end(ctoken);
                    ip++;
                    now = now2;
                    j = 1138166333441L;
                    j2 = 2246267895810L;
                }
                token = token3;
                long j3 = now;
                ArrayMap<String, SparseArray<Long>> arrayMap2 = pmap3;
                int i4 = procCount2;
            } else {
                token = token3;
                long j4 = now;
            }
            if (!this.mBadProcesses.getMap().isEmpty()) {
                ArrayMap<String, SparseArray<BadProcessInfo>> pmap4 = this.mBadProcesses.getMap();
                int processCount = pmap4.size();
                int ip2 = 0;
                while (ip2 < processCount) {
                    long btoken = protoOutputStream.start(2246267895811L);
                    String pname4 = pmap4.keyAt(ip2);
                    SparseArray<BadProcessInfo> uids3 = pmap4.valueAt(ip2);
                    int uidCount3 = uids3.size();
                    protoOutputStream.write(1138166333441L, pname4);
                    int i5 = 0;
                    while (i5 < uidCount3) {
                        int puid2 = uids3.keyAt(i5);
                        ProcessRecord r2 = (ProcessRecord) this.mService.getProcessNames().get(pname4, puid2);
                        if (str != null) {
                            if (r2 == null) {
                                pmap = pmap4;
                                pname = pname4;
                                uids = uids3;
                            } else if (!r2.pkgList.containsKey(str)) {
                                pmap = pmap4;
                                pname = pname4;
                                uids = uids3;
                            }
                            i5++;
                            str = dumpPackage;
                            pmap4 = pmap;
                            pname4 = pname;
                            uids3 = uids;
                        }
                        BadProcessInfo info = uids3.valueAt(i5);
                        pmap = pmap4;
                        pname = pname4;
                        uids = uids3;
                        long etoken2 = protoOutputStream.start(2246267895810L);
                        protoOutputStream.write(1120986464257L, puid2);
                        int i6 = puid2;
                        ProcessRecord processRecord2 = r2;
                        protoOutputStream.write(1112396529666L, info.time);
                        protoOutputStream.write(1138166333443L, info.shortMsg);
                        protoOutputStream.write(1138166333444L, info.longMsg);
                        protoOutputStream.write(1138166333445L, info.stack);
                        protoOutputStream.end(etoken2);
                        i5++;
                        str = dumpPackage;
                        pmap4 = pmap;
                        pname4 = pname;
                        uids3 = uids;
                    }
                    String str3 = pname4;
                    SparseArray<BadProcessInfo> sparseArray = uids3;
                    protoOutputStream.end(btoken);
                    ip2++;
                    str = dumpPackage;
                }
            }
            protoOutputStream.end(token);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean dumpLocked(FileDescriptor fd, PrintWriter pw, boolean needSep, String dumpPackage) {
        boolean needSep2;
        ArrayMap<String, SparseArray<BadProcessInfo>> pmap;
        String str;
        int processCount;
        AppErrors appErrors = this;
        PrintWriter printWriter = pw;
        String str2 = dumpPackage;
        String str3 = " uid ";
        if (!appErrors.mProcessCrashTimes.getMap().isEmpty()) {
            boolean printed = false;
            long now = SystemClock.uptimeMillis();
            ArrayMap<String, SparseArray<Long>> pmap2 = appErrors.mProcessCrashTimes.getMap();
            int processCount2 = pmap2.size();
            needSep2 = needSep;
            for (int ip = 0; ip < processCount2; ip++) {
                String pname = pmap2.keyAt(ip);
                SparseArray<Long> uids = pmap2.valueAt(ip);
                int uidCount = uids.size();
                int i = 0;
                while (i < uidCount) {
                    int puid = uids.keyAt(i);
                    ArrayMap<String, SparseArray<Long>> pmap3 = pmap2;
                    ProcessRecord r = (ProcessRecord) appErrors.mService.getProcessNames().get(pname, puid);
                    if (str2 != null) {
                        if (r != null) {
                            processCount = processCount2;
                            if (!r.pkgList.containsKey(str2)) {
                            }
                        } else {
                            processCount = processCount2;
                        }
                        i++;
                        pmap2 = pmap3;
                        processCount2 = processCount;
                    } else {
                        processCount = processCount2;
                    }
                    if (!printed) {
                        if (needSep2) {
                            pw.println();
                        }
                        needSep2 = true;
                        printWriter.println("  Time since processes crashed:");
                        printed = true;
                    }
                    printWriter.print("    Process ");
                    printWriter.print(pname);
                    printWriter.print(str3);
                    printWriter.print(puid);
                    printWriter.print(": last crashed ");
                    ProcessRecord processRecord = r;
                    TimeUtils.formatDuration(now - uids.valueAt(i).longValue(), printWriter);
                    printWriter.println(" ago");
                    i++;
                    pmap2 = pmap3;
                    processCount2 = processCount;
                }
                int i2 = processCount2;
            }
            int i3 = processCount2;
        } else {
            needSep2 = needSep;
        }
        if (!appErrors.mBadProcesses.getMap().isEmpty()) {
            boolean printed2 = false;
            ArrayMap<String, SparseArray<BadProcessInfo>> pmap4 = appErrors.mBadProcesses.getMap();
            int processCount3 = pmap4.size();
            int ip2 = 0;
            while (ip2 < processCount3) {
                String pname2 = pmap4.keyAt(ip2);
                SparseArray<BadProcessInfo> uids2 = pmap4.valueAt(ip2);
                int uidCount2 = uids2.size();
                int i4 = 0;
                while (i4 < uidCount2) {
                    int puid2 = uids2.keyAt(i4);
                    ProcessRecord r2 = (ProcessRecord) appErrors.mService.getProcessNames().get(pname2, puid2);
                    if (str2 == null || (r2 != null && r2.pkgList.containsKey(str2))) {
                        if (!printed2) {
                            if (needSep2) {
                                pw.println();
                            }
                            needSep2 = true;
                            printWriter.println("  Bad processes:");
                            printed2 = true;
                        }
                        BadProcessInfo info = uids2.valueAt(i4);
                        printWriter.print("    Bad process ");
                        printWriter.print(pname2);
                        printWriter.print(str3);
                        printWriter.print(puid2);
                        printWriter.print(": crashed at time ");
                        boolean printed3 = printed2;
                        printWriter.println(info.time);
                        if (info.shortMsg != null) {
                            printWriter.print("      Short msg: ");
                            printWriter.println(info.shortMsg);
                        }
                        if (info.longMsg != null) {
                            printWriter.print("      Long msg: ");
                            printWriter.println(info.longMsg);
                        }
                        if (info.stack != null) {
                            printWriter.println("      Stack:");
                            int lastPos = 0;
                            int pos = 0;
                            while (true) {
                                str = str3;
                                if (pos >= info.stack.length()) {
                                    break;
                                }
                                ArrayMap<String, SparseArray<BadProcessInfo>> pmap5 = pmap4;
                                if (info.stack.charAt(pos) == 10) {
                                    printWriter.print("        ");
                                    printWriter.write(info.stack, lastPos, pos - lastPos);
                                    pw.println();
                                    lastPos = pos + 1;
                                }
                                pos++;
                                str3 = str;
                                pmap4 = pmap5;
                            }
                            pmap = pmap4;
                            if (lastPos < info.stack.length()) {
                                printWriter.print("        ");
                                printWriter.write(info.stack, lastPos, info.stack.length() - lastPos);
                                pw.println();
                            }
                        } else {
                            str = str3;
                            pmap = pmap4;
                        }
                        printed2 = printed3;
                    } else {
                        str = str3;
                        pmap = pmap4;
                    }
                    i4++;
                    appErrors = this;
                    str2 = dumpPackage;
                    str3 = str;
                    pmap4 = pmap;
                }
                String str4 = str3;
                ArrayMap<String, SparseArray<BadProcessInfo>> arrayMap = pmap4;
                ip2++;
                appErrors = this;
                str2 = dumpPackage;
            }
        }
        return needSep2;
    }

    /* access modifiers changed from: package-private */
    public boolean isBadProcessLocked(ApplicationInfo info) {
        return this.mBadProcesses.get(info.processName, info.uid) != null;
    }

    /* access modifiers changed from: package-private */
    public void clearBadProcessLocked(ApplicationInfo info) {
        this.mBadProcesses.remove(info.processName, info.uid);
    }

    /* access modifiers changed from: package-private */
    public void resetProcessCrashTimeLocked(ApplicationInfo info) {
        this.mProcessCrashTimes.remove(info.processName, info.uid);
    }

    /* access modifiers changed from: package-private */
    public void resetProcessCrashTimeLocked(boolean resetEntireUser, int appId, int userId) {
        ArrayMap<String, SparseArray<Long>> pmap = this.mProcessCrashTimes.getMap();
        for (int ip = pmap.size() - 1; ip >= 0; ip--) {
            SparseArray<Long> ba = pmap.valueAt(ip);
            for (int i = ba.size() - 1; i >= 0; i--) {
                boolean remove = false;
                int entUid = ba.keyAt(i);
                if (!resetEntireUser) {
                    if (userId == -1) {
                        if (UserHandle.getAppId(entUid) == appId) {
                            remove = true;
                        }
                    } else if (entUid == UserHandle.getUid(userId, appId)) {
                        remove = true;
                    }
                } else if (UserHandle.getUserId(entUid) == userId) {
                    remove = true;
                }
                if (remove) {
                    ba.removeAt(i);
                }
            }
            if (ba.size() == 0) {
                pmap.removeAt(ip);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void loadAppsNotReportingCrashesFromConfigLocked(String appsNotReportingCrashesConfig) {
        if (appsNotReportingCrashesConfig != null) {
            String[] split = appsNotReportingCrashesConfig.split(",");
            if (split.length > 0) {
                this.mAppsNotReportingCrashes = new ArraySet<>();
                Collections.addAll(this.mAppsNotReportingCrashes, split);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void killAppAtUserRequestLocked(ProcessRecord app, Dialog fromDialog) {
        app.setCrashing(false);
        app.crashingReport = null;
        app.setNotResponding(false);
        app.notRespondingReport = null;
        if (app.anrDialog == fromDialog) {
            app.anrDialog = null;
        }
        if (app.waitDialog == fromDialog) {
            app.waitDialog = null;
        }
        if (app.pid > 0 && app.pid != ActivityManagerService.MY_PID) {
            handleAppCrashLocked(app, "user-terminated", (String) null, (String) null, (String) null, (AppErrorDialog.Data) null);
            app.kill("user request after error", true);
        }
    }

    /* access modifiers changed from: package-private */
    public void scheduleAppCrashLocked(int uid, int initialPid, String packageName, int userId, String message) {
        ProcessRecord proc = null;
        synchronized (this.mService.mPidsSelfLocked) {
            int i = 0;
            while (true) {
                if (i >= this.mService.mPidsSelfLocked.size()) {
                    break;
                }
                ProcessRecord p = this.mService.mPidsSelfLocked.valueAt(i);
                if (uid < 0 || p.uid == uid) {
                    if (p.pid == initialPid) {
                        proc = p;
                        break;
                    } else if (p.pkgList.containsKey(packageName) && (userId < 0 || p.userId == userId)) {
                        proc = p;
                    }
                }
                i++;
            }
        }
        if (proc == null) {
            Slog.w(TAG, "crashApplication: nothing for uid=" + uid + " initialPid=" + initialPid + " packageName=" + packageName + " userId=" + userId);
            return;
        }
        proc.scheduleCrash(message);
    }

    /* access modifiers changed from: package-private */
    public void crashApplication(ProcessRecord r, ApplicationErrorReport.CrashInfo crashInfo) {
        int callingPid = Binder.getCallingPid();
        int callingUid = Binder.getCallingUid();
        long origId = Binder.clearCallingIdentity();
        try {
            crashApplicationInner(r, crashInfo, callingPid, callingUid);
        } finally {
            Binder.restoreCallingIdentity(origId);
        }
    }

    /* access modifiers changed from: package-private */
    public void crashApplicationInner(ProcessRecord r, ApplicationErrorReport.CrashInfo crashInfo, int callingPid, int callingUid) {
        String longMsg;
        ActivityManagerService activityManagerService;
        int res;
        ProcessRecord processRecord = r;
        ApplicationErrorReport.CrashInfo crashInfo2 = crashInfo;
        long timeMillis = System.currentTimeMillis();
        String shortMsg = crashInfo2.exceptionClassName;
        String longMsg2 = crashInfo2.exceptionMessage;
        String stackTrace = crashInfo2.stackTrace;
        if (shortMsg != null && longMsg2 != null) {
            longMsg = shortMsg + ": " + longMsg2;
        } else if (shortMsg != null) {
            longMsg = shortMsg;
        } else {
            longMsg = longMsg2;
        }
        if (processRecord != null) {
            boolean isApexModule = false;
            try {
                String[] packageList = r.getPackageList();
                int length = packageList.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    if (this.mContext.getPackageManager().getModuleInfo(packageList[i], 0) != null) {
                        isApexModule = true;
                        break;
                    }
                    i++;
                }
            } catch (PackageManager.NameNotFoundException | IllegalStateException e) {
            }
            if (r.isPersistent() || isApexModule) {
                RescueParty.noteAppCrash(this.mContext, processRecord.uid);
            }
            this.mPackageWatchdog.onPackageFailure(r.getPackageListWithVersionCode(), 3);
        }
        int relaunchReason = processRecord != null ? r.getWindowProcessController().computeRelaunchReason() : 0;
        AppErrorResult result = new AppErrorResult();
        ActivityManagerService activityManagerService2 = this.mService;
        synchronized (activityManagerService2) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                activityManagerService = activityManagerService2;
                AppErrorResult result2 = result;
                int relaunchReason2 = relaunchReason;
                String stackTrace2 = stackTrace;
                String shortMsg2 = shortMsg;
                try {
                    if (handleAppCrashInActivityController(r, crashInfo, shortMsg, longMsg, stackTrace, timeMillis, callingPid, callingUid)) {
                        try {
                            ActivityManagerService.resetPriorityAfterLockedSection();
                        } catch (Throwable th) {
                            th = th;
                            ApplicationErrorReport.CrashInfo crashInfo3 = crashInfo;
                            AppErrorResult appErrorResult = result2;
                            ActivityManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    } else if (relaunchReason2 == 2) {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                    } else {
                        if (processRecord != null) {
                            if (r.getActiveInstrumentation() != null) {
                                ActivityManagerService.resetPriorityAfterLockedSection();
                                return;
                            }
                        }
                        if (processRecord != null) {
                            this.mService.mBatteryStatsService.noteProcessCrash(processRecord.processName, processRecord.uid);
                        }
                        AppErrorDialog.Data data = new AppErrorDialog.Data();
                        AppErrorResult result3 = result2;
                        try {
                            data.result = result3;
                            data.proc = processRecord;
                            AppErrorResult result4 = result3;
                            data.crash = crashInfo;
                            if (processRecord != null) {
                                if (makeAppCrashingLocked(r, shortMsg2, longMsg, stackTrace2, data)) {
                                    Message msg = Message.obtain();
                                    msg.what = 1;
                                    int taskId = data.taskId;
                                    msg.obj = data;
                                    this.mService.mUiHandler.sendMessage(msg);
                                    ActivityManagerService.resetPriorityAfterLockedSection();
                                    int res2 = result4.get();
                                    Intent appErrorIntent = null;
                                    MetricsLogger.action(this.mContext, 316, res2);
                                    if (res2 == 6 || res2 == 7) {
                                        res = 1;
                                    } else {
                                        res = res2;
                                    }
                                    synchronized (this.mService) {
                                        try {
                                            ActivityManagerService.boostPriorityForLockedSection();
                                            if (res == 5) {
                                                stopReportingCrashesLocked(r);
                                            }
                                            if (res == 3) {
                                                this.mService.mProcessList.removeProcessLocked(processRecord, false, true, ProcessPolicy.REASON_CRASH);
                                                if (taskId != -1) {
                                                    this.mService.startActivityFromRecents(taskId, ActivityOptions.makeBasic().toBundle());
                                                }
                                            }
                                        } catch (IllegalArgumentException e2) {
                                            Slog.e(TAG, "Could not restart taskId=" + taskId, e2);
                                        } catch (Throwable th2) {
                                            while (true) {
                                                ActivityManagerService.resetPriorityAfterLockedSection();
                                                throw th2;
                                            }
                                        }
                                        if (res == 1) {
                                            this.mService.mAtmInternal.onHandleAppCrash(r.getWindowProcessController());
                                            if (!r.isPersistent()) {
                                                this.mService.mProcessList.removeProcessLocked(processRecord, false, false, ProcessPolicy.REASON_CRASH);
                                                this.mService.mAtmInternal.resumeTopActivities(false);
                                            }
                                        }
                                        if (res == 8) {
                                            appErrorIntent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
                                            appErrorIntent.setData(Uri.parse("package:" + processRecord.info.packageName));
                                            appErrorIntent.addFlags(268435456);
                                        }
                                        if (res == 2) {
                                            appErrorIntent = null;
                                        }
                                        if (!processRecord.isolated && res != 3) {
                                            this.mProcessCrashTimes.put(processRecord.info.processName, processRecord.uid, Long.valueOf(SystemClock.uptimeMillis()));
                                        }
                                    }
                                    ActivityManagerService.resetPriorityAfterLockedSection();
                                    if (appErrorIntent != null) {
                                        try {
                                            this.mContext.startActivityAsUser(appErrorIntent, new UserHandle(processRecord.userId));
                                            return;
                                        } catch (ActivityNotFoundException e3) {
                                            Slog.w(TAG, "bug report receiver dissappeared", e3);
                                            return;
                                        }
                                    } else {
                                        return;
                                    }
                                }
                            }
                            try {
                                ActivityManagerService.resetPriorityAfterLockedSection();
                            } catch (Throwable th3) {
                                th = th3;
                                ActivityManagerService.resetPriorityAfterLockedSection();
                                throw th;
                            }
                        } catch (Throwable th4) {
                            th = th4;
                            ApplicationErrorReport.CrashInfo crashInfo4 = crashInfo;
                            AppErrorResult appErrorResult2 = result3;
                            ActivityManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                } catch (Throwable th5) {
                    th = th5;
                    ApplicationErrorReport.CrashInfo crashInfo5 = crashInfo;
                    AppErrorResult appErrorResult3 = result2;
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            } catch (Throwable th6) {
                th = th6;
                activityManagerService = activityManagerService2;
                String str = stackTrace;
                String str2 = shortMsg;
                ApplicationErrorReport.CrashInfo crashInfo6 = crashInfo2;
                AppErrorResult appErrorResult4 = result;
                int i2 = relaunchReason;
                ActivityManagerService.resetPriorityAfterLockedSection();
                throw th;
            }
        }
    }

    private boolean handleAppCrashInActivityController(ProcessRecord r, ApplicationErrorReport.CrashInfo crashInfo, String shortMsg, String longMsg, String stackTrace, long timeMillis, int callingPid, int callingUid) {
        ProcessRecord processRecord = r;
        String name = processRecord != null ? processRecord.processName : null;
        int pid = processRecord != null ? processRecord.pid : callingPid;
        return this.mService.mAtmInternal.handleAppCrashInActivityController(name, pid, shortMsg, longMsg, timeMillis, crashInfo.stackTrace, new Runnable(crashInfo, name, pid, r, shortMsg, longMsg, stackTrace, processRecord != null ? processRecord.info.uid : callingUid) {
            private final /* synthetic */ ApplicationErrorReport.CrashInfo f$1;
            private final /* synthetic */ String f$2;
            private final /* synthetic */ int f$3;
            private final /* synthetic */ ProcessRecord f$4;
            private final /* synthetic */ String f$5;
            private final /* synthetic */ String f$6;
            private final /* synthetic */ String f$7;
            private final /* synthetic */ int f$8;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
                this.f$4 = r5;
                this.f$5 = r6;
                this.f$6 = r7;
                this.f$7 = r8;
                this.f$8 = r9;
            }

            public final void run() {
                AppErrors.this.lambda$handleAppCrashInActivityController$0$AppErrors(this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8);
            }
        });
    }

    public /* synthetic */ void lambda$handleAppCrashInActivityController$0$AppErrors(ApplicationErrorReport.CrashInfo crashInfo, String name, int pid, ProcessRecord r, String shortMsg, String longMsg, String stackTrace, int uid) {
        String str = name;
        int i = pid;
        ProcessRecord processRecord = r;
        if (!SplitScreenReporter.ACTION_ENTER_SPLIT.equals(SystemProperties.get("ro.debuggable", "0"))) {
            ApplicationErrorReport.CrashInfo crashInfo2 = crashInfo;
        } else if ("Native crash".equals(crashInfo.exceptionClassName)) {
            Slog.w(TAG, "Skip killing native crashed app " + name + "(" + pid + ") during testing");
            int i2 = uid;
            return;
        }
        Slog.w(TAG, "Force-killing crashed app " + name + " at watcher's request");
        if (processRecord == null) {
            Process.killProcess(pid);
            ProcessList.killProcessGroup(uid, pid);
        } else if (!makeAppCrashingLocked(r, shortMsg, longMsg, stackTrace, (AppErrorDialog.Data) null)) {
            r.kill(ProcessPolicy.REASON_CRASH, true);
            int i3 = uid;
        } else {
            int i4 = uid;
        }
    }

    private boolean makeAppCrashingLocked(ProcessRecord app, String shortMsg, String longMsg, String stackTrace, AppErrorDialog.Data data) {
        app.setCrashing(true);
        ProcessRecord processRecord = app;
        app.crashingReport = generateProcessError(processRecord, 1, (String) null, shortMsg, longMsg, stackTrace);
        app.startAppProblemLocked();
        app.getWindowProcessController().stopFreezingActivities();
        return handleAppCrashLocked(processRecord, "force-crash", shortMsg, longMsg, stackTrace, data);
    }

    /* access modifiers changed from: package-private */
    public ActivityManager.ProcessErrorStateInfo generateProcessError(ProcessRecord app, int condition, String activity, String shortMsg, String longMsg, String stackTrace) {
        ActivityManager.ProcessErrorStateInfo report = new ActivityManager.ProcessErrorStateInfo();
        report.condition = condition;
        report.processName = app.processName;
        report.pid = app.pid;
        report.uid = app.info.uid;
        report.tag = activity;
        report.shortMsg = shortMsg;
        report.longMsg = longMsg;
        report.stackTrace = stackTrace;
        return report;
    }

    /* access modifiers changed from: package-private */
    public Intent createAppErrorIntentLocked(ProcessRecord r, long timeMillis, ApplicationErrorReport.CrashInfo crashInfo) {
        ApplicationErrorReport report = createAppErrorReportLocked(r, timeMillis, crashInfo);
        if (report == null) {
            return null;
        }
        Intent result = new Intent("android.intent.action.APP_ERROR");
        result.setComponent(r.errorReportReceiver);
        result.putExtra("android.intent.extra.BUG_REPORT", report);
        result.addFlags(268435456);
        return result;
    }

    private ApplicationErrorReport createAppErrorReportLocked(ProcessRecord r, long timeMillis, ApplicationErrorReport.CrashInfo crashInfo) {
        if (r.errorReportReceiver == null) {
            return null;
        }
        if (!r.isCrashing() && !r.isNotResponding() && !r.forceCrashReport) {
            return null;
        }
        ApplicationErrorReport report = new ApplicationErrorReport();
        report.packageName = r.info.packageName;
        report.installerPackageName = r.errorReportReceiver.getPackageName();
        report.processName = r.processName;
        report.time = timeMillis;
        report.systemApp = (r.info.flags & 1) != 0;
        if (r.isCrashing() || r.forceCrashReport) {
            report.type = 1;
            report.crashInfo = crashInfo;
        } else if (r.isNotResponding()) {
            report.type = 2;
            report.anrInfo = new ApplicationErrorReport.AnrInfo();
            report.anrInfo.activity = r.notRespondingReport.tag;
            report.anrInfo.cause = r.notRespondingReport.shortMsg;
            report.anrInfo.info = r.notRespondingReport.longMsg;
        }
        return report;
    }

    /* access modifiers changed from: package-private */
    public boolean handleAppCrashLocked(ProcessRecord app, String reason, String shortMsg, String longMsg, String stackTrace, AppErrorDialog.Data data) {
        Long crashTimePersistent;
        Long crashTime;
        long now;
        boolean tryAgain;
        boolean z;
        ProcessRecord processRecord = app;
        AppErrorDialog.Data data2 = data;
        long now2 = SystemClock.uptimeMillis();
        boolean showBackground = Settings.Secure.getInt(this.mContext.getContentResolver(), "anr_show_background", 0) != 0;
        boolean procIsBoundForeground = app.getCurProcState() == 6;
        if (!processRecord.isolated) {
            crashTime = (Long) this.mProcessCrashTimes.get(processRecord.info.processName, processRecord.uid);
            crashTimePersistent = (Long) this.mProcessCrashTimesPersistent.get(processRecord.info.processName, processRecord.uid);
        } else {
            crashTime = null;
            crashTimePersistent = null;
        }
        boolean tryAgain2 = false;
        for (int i = processRecord.f3services.size() - 1; i >= 0; i--) {
            ServiceRecord sr = processRecord.f3services.valueAt(i);
            if (now2 > sr.restartTime + 60000) {
                sr.crashCount = 1;
            } else {
                sr.crashCount++;
            }
            if (((long) sr.crashCount) < this.mService.mConstants.BOUND_SERVICE_MAX_CRASH_RETRY && (sr.isForeground || procIsBoundForeground)) {
                tryAgain2 = true;
            }
        }
        if (crashTime == null || now2 >= crashTime.longValue() + 60000) {
            now = now2;
            boolean z2 = procIsBoundForeground;
            Long l = crashTime;
            tryAgain = tryAgain2;
            int affectedTaskId = this.mService.mAtmInternal.finishTopCrashedActivities(app.getWindowProcessController(), reason);
            if (data2 != null) {
                data2.taskId = affectedTaskId;
            }
            if (!(data2 == null || crashTimePersistent == null || now >= crashTimePersistent.longValue() + 60000)) {
                data2.repeating = true;
            }
        } else {
            Slog.w(TAG, "Process " + processRecord.info.processName + " has crashed too many times: killing!");
            EventLog.writeEvent(EventLogTags.AM_PROCESS_CRASHED_TOO_MUCH, new Object[]{Integer.valueOf(processRecord.userId), processRecord.info.processName, Integer.valueOf(processRecord.uid)});
            this.mService.mAtmInternal.onHandleAppCrash(app.getWindowProcessController());
            if (!app.isPersistent()) {
                EventLog.writeEvent(EventLogTags.AM_PROC_BAD, new Object[]{Integer.valueOf(processRecord.userId), Integer.valueOf(processRecord.uid), processRecord.info.processName});
                if (!processRecord.isolated) {
                    ProcessMap<BadProcessInfo> processMap = this.mBadProcesses;
                    String str = processRecord.info.processName;
                    boolean z3 = procIsBoundForeground;
                    BadProcessInfo badProcessInfo = r3;
                    Long l2 = crashTime;
                    int i2 = processRecord.uid;
                    now = now2;
                    tryAgain = tryAgain2;
                    BadProcessInfo badProcessInfo2 = new BadProcessInfo(now2, shortMsg, longMsg, stackTrace);
                    processMap.put(str, i2, badProcessInfo);
                    this.mProcessCrashTimes.remove(processRecord.info.processName, processRecord.uid);
                } else {
                    now = now2;
                    boolean z4 = procIsBoundForeground;
                    Long l3 = crashTime;
                    tryAgain = tryAgain2;
                }
                processRecord.bad = true;
                processRecord.removed = true;
                z = false;
                this.mService.mProcessList.removeProcessLocked(processRecord, false, tryAgain, ProcessPolicy.REASON_CRASH);
                this.mService.mAtmInternal.resumeTopActivities(false);
                if (!showBackground) {
                    return false;
                }
            } else {
                now = now2;
                boolean z5 = procIsBoundForeground;
                Long l4 = crashTime;
                z = false;
                tryAgain = tryAgain2;
            }
            this.mService.mAtmInternal.resumeTopActivities(z);
            String str2 = reason;
        }
        if (data2 != null && tryAgain) {
            data2.isRestartableForService = true;
        }
        WindowProcessController proc = app.getWindowProcessController();
        WindowProcessController homeProc = this.mService.mAtmInternal.getHomeProcess();
        if (proc == homeProc && proc.hasActivities() && (((ProcessRecord) homeProc.mOwner).info.flags & 1) == 0) {
            proc.clearPackagePreferredForHomeActivities();
        }
        if (!processRecord.isolated) {
            this.mProcessCrashTimes.put(processRecord.info.processName, processRecord.uid, Long.valueOf(now));
            this.mProcessCrashTimesPersistent.put(processRecord.info.processName, processRecord.uid, Long.valueOf(now));
        }
        if (processRecord.crashHandler == null) {
            return true;
        }
        this.mService.mHandler.post(processRecord.crashHandler);
        return true;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x005d, code lost:
        com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0060, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00ba, code lost:
        com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00bd, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x012c, code lost:
        com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:72:0x012f, code lost:
        if (r7 == null) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:0x0131, code lost:
        r7.show();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:86:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleShowAppErrorUi(android.os.Message r19) {
        /*
            r18 = this;
            r1 = r18
            r2 = r19
            java.lang.Object r0 = r2.obj
            r3 = r0
            com.android.server.am.AppErrorDialog$Data r3 = (com.android.server.am.AppErrorDialog.Data) r3
            android.content.Context r0 = r1.mContext
            android.content.ContentResolver r0 = r0.getContentResolver()
            r4 = 0
            java.lang.String r5 = "anr_show_background"
            int r0 = android.provider.Settings.Secure.getInt(r0, r5, r4)
            if (r0 == 0) goto L_0x001a
            r0 = 1
            goto L_0x001b
        L_0x001a:
            r0 = r4
        L_0x001b:
            r6 = r0
            r7 = 0
            com.android.server.am.ActivityManagerService r8 = r1.mService
            monitor-enter(r8)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0135 }
            com.android.server.am.ProcessRecord r0 = r3.proc     // Catch:{ all -> 0x0135 }
            com.android.server.am.AppErrorResult r9 = r3.result     // Catch:{ all -> 0x0135 }
            if (r0 != 0) goto L_0x0035
            java.lang.String r4 = "ActivityManager"
            java.lang.String r5 = "handleShowAppErrorUi: proc is null"
            android.util.Slog.e(r4, r5)     // Catch:{ all -> 0x0135 }
            monitor-exit(r8)     // Catch:{ all -> 0x0135 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0035:
            android.content.pm.ApplicationInfo r10 = r0.info     // Catch:{ all -> 0x0135 }
            java.lang.String r10 = r10.packageName     // Catch:{ all -> 0x0135 }
            int r11 = r0.userId     // Catch:{ all -> 0x0135 }
            android.app.Dialog r12 = r0.crashDialog     // Catch:{ all -> 0x0135 }
            if (r12 == 0) goto L_0x0061
            java.lang.String r4 = "ActivityManager"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0135 }
            r5.<init>()     // Catch:{ all -> 0x0135 }
            java.lang.String r12 = "App already has crash dialog: "
            r5.append(r12)     // Catch:{ all -> 0x0135 }
            r5.append(r0)     // Catch:{ all -> 0x0135 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0135 }
            android.util.Slog.e(r4, r5)     // Catch:{ all -> 0x0135 }
            if (r9 == 0) goto L_0x005c
            int r4 = com.android.server.am.AppErrorDialog.ALREADY_SHOWING     // Catch:{ all -> 0x0135 }
            r9.set(r4)     // Catch:{ all -> 0x0135 }
        L_0x005c:
            monitor-exit(r8)     // Catch:{ all -> 0x0135 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0061:
            int r12 = r0.uid     // Catch:{ all -> 0x0135 }
            int r12 = android.os.UserHandle.getAppId(r12)     // Catch:{ all -> 0x0135 }
            r13 = 10000(0x2710, float:1.4013E-41)
            if (r12 < r13) goto L_0x0073
            int r12 = r0.pid     // Catch:{ all -> 0x0135 }
            int r13 = com.android.server.am.ActivityManagerService.MY_PID     // Catch:{ all -> 0x0135 }
            if (r12 == r13) goto L_0x0073
            r12 = 1
            goto L_0x0074
        L_0x0073:
            r12 = r4
        L_0x0074:
            com.android.server.am.ActivityManagerService r13 = r1.mService     // Catch:{ all -> 0x0135 }
            com.android.server.am.UserController r13 = r13.mUserController     // Catch:{ all -> 0x0135 }
            int[] r13 = r13.getCurrentProfileIds()     // Catch:{ all -> 0x0135 }
            int r14 = r13.length     // Catch:{ all -> 0x0135 }
            r15 = r12
            r12 = r4
        L_0x007f:
            if (r12 >= r14) goto L_0x0093
            r16 = r13[r12]     // Catch:{ all -> 0x0135 }
            r17 = r16
            r5 = r17
            if (r11 == r5) goto L_0x008c
            r17 = 1
            goto L_0x008e
        L_0x008c:
            r17 = r4
        L_0x008e:
            r15 = r15 & r17
            int r12 = r12 + 1
            goto L_0x007f
        L_0x0093:
            if (r15 == 0) goto L_0x00be
            if (r6 != 0) goto L_0x00be
            java.lang.String r4 = "ActivityManager"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0135 }
            r5.<init>()     // Catch:{ all -> 0x0135 }
            java.lang.String r12 = "Skipping crash dialog of "
            r5.append(r12)     // Catch:{ all -> 0x0135 }
            r5.append(r0)     // Catch:{ all -> 0x0135 }
            java.lang.String r12 = ": background"
            r5.append(r12)     // Catch:{ all -> 0x0135 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0135 }
            android.util.Slog.w(r4, r5)     // Catch:{ all -> 0x0135 }
            if (r9 == 0) goto L_0x00b9
            int r4 = com.android.server.am.AppErrorDialog.BACKGROUND_USER     // Catch:{ all -> 0x0135 }
            r9.set(r4)     // Catch:{ all -> 0x0135 }
        L_0x00b9:
            monitor-exit(r8)     // Catch:{ all -> 0x0135 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return
        L_0x00be:
            android.content.Context r5 = r1.mContext     // Catch:{ all -> 0x0135 }
            android.content.ContentResolver r5 = r5.getContentResolver()     // Catch:{ all -> 0x0135 }
            java.lang.String r12 = "show_first_crash_dialog"
            int r5 = android.provider.Settings.Global.getInt(r5, r12, r4)     // Catch:{ all -> 0x0135 }
            if (r5 == 0) goto L_0x00cf
            r5 = 1
            goto L_0x00d0
        L_0x00cf:
            r5 = r4
        L_0x00d0:
            android.content.Context r12 = r1.mContext     // Catch:{ all -> 0x0135 }
            android.content.ContentResolver r12 = r12.getContentResolver()     // Catch:{ all -> 0x0135 }
            java.lang.String r13 = "show_first_crash_dialog_dev_option"
            com.android.server.am.ActivityManagerService r14 = r1.mService     // Catch:{ all -> 0x0135 }
            com.android.server.am.UserController r14 = r14.mUserController     // Catch:{ all -> 0x0135 }
            int r14 = r14.getCurrentUserId()     // Catch:{ all -> 0x0135 }
            int r12 = android.provider.Settings.Secure.getIntForUser(r12, r13, r4, r14)     // Catch:{ all -> 0x0135 }
            if (r12 == 0) goto L_0x00e9
            r12 = 1
            goto L_0x00ea
        L_0x00e9:
            r12 = r4
        L_0x00ea:
            android.util.ArraySet<java.lang.String> r13 = r1.mAppsNotReportingCrashes     // Catch:{ all -> 0x0135 }
            if (r13 == 0) goto L_0x00fd
            android.util.ArraySet<java.lang.String> r13 = r1.mAppsNotReportingCrashes     // Catch:{ all -> 0x0135 }
            android.content.pm.ApplicationInfo r14 = r0.info     // Catch:{ all -> 0x0135 }
            java.lang.String r14 = r14.packageName     // Catch:{ all -> 0x0135 }
            boolean r13 = r13.contains(r14)     // Catch:{ all -> 0x0135 }
            if (r13 == 0) goto L_0x00fd
            r16 = 1
            goto L_0x00ff
        L_0x00fd:
            r16 = r4
        L_0x00ff:
            r4 = r16
            com.android.server.am.ActivityManagerService r13 = r1.mService     // Catch:{ all -> 0x0135 }
            com.android.server.wm.ActivityTaskManagerInternal r13 = r13.mAtmInternal     // Catch:{ all -> 0x0135 }
            boolean r13 = r13.canShowErrorDialogs()     // Catch:{ all -> 0x0135 }
            if (r13 != 0) goto L_0x010d
            if (r6 == 0) goto L_0x0124
        L_0x010d:
            if (r4 != 0) goto L_0x0124
            if (r5 != 0) goto L_0x0117
            if (r12 != 0) goto L_0x0117
            boolean r13 = r3.repeating     // Catch:{ all -> 0x0135 }
            if (r13 == 0) goto L_0x0124
        L_0x0117:
            com.android.server.am.ActivityManagerService r13 = r1.mService     // Catch:{ all -> 0x0135 }
            android.content.Context r14 = r1.mContext     // Catch:{ all -> 0x0135 }
            com.android.server.am.ActivityManagerServiceInjector.showMiuiAppCrashDialog(r13, r3, r14, r0, r9)     // Catch:{ all -> 0x0135 }
            android.app.Dialog r13 = r0.crashDialog     // Catch:{ all -> 0x0135 }
            com.android.server.am.AppErrorDialog r13 = (com.android.server.am.AppErrorDialog) r13     // Catch:{ all -> 0x0135 }
            r7 = r13
            goto L_0x012b
        L_0x0124:
            if (r9 == 0) goto L_0x012b
            int r13 = com.android.server.am.AppErrorDialog.CANT_SHOW     // Catch:{ all -> 0x0135 }
            r9.set(r13)     // Catch:{ all -> 0x0135 }
        L_0x012b:
            monitor-exit(r8)     // Catch:{ all -> 0x0135 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            if (r7 == 0) goto L_0x0134
            r7.show()
        L_0x0134:
            return
        L_0x0135:
            r0 = move-exception
            monitor-exit(r8)     // Catch:{ all -> 0x0135 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.AppErrors.handleShowAppErrorUi(android.os.Message):void");
    }

    private void stopReportingCrashesLocked(ProcessRecord proc) {
        if (this.mAppsNotReportingCrashes == null) {
            this.mAppsNotReportingCrashes = new ArraySet<>();
        }
        this.mAppsNotReportingCrashes.add(proc.info.packageName);
    }

    /* JADX WARNING: type inference failed for: r6v1, types: [com.android.server.am.AppNotRespondingDialog] */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0086, code lost:
        com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0089, code lost:
        if (r0 == null) goto L_0x008e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x008b, code lost:
        r0.show();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x008e, code lost:
        if (r1 == null) goto L_?;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x0090, code lost:
        r9.mPackageWatchdog.onPackageFailure(r1, 4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:?, code lost:
        return;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleShowAnrUi(android.os.Message r10) {
        /*
            r9 = this;
            r0 = 0
            r1 = 0
            com.android.server.am.ActivityManagerService r2 = r9.mService
            monitor-enter(r2)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0097 }
            java.lang.Object r3 = r10.obj     // Catch:{ all -> 0x0097 }
            com.android.server.am.AppNotRespondingDialog$Data r3 = (com.android.server.am.AppNotRespondingDialog.Data) r3     // Catch:{ all -> 0x0097 }
            com.android.server.am.ProcessRecord r4 = r3.proc     // Catch:{ all -> 0x0097 }
            if (r4 != 0) goto L_0x001c
            java.lang.String r5 = "ActivityManager"
            java.lang.String r6 = "handleShowAnrUi: proc is null"
            android.util.Slog.e(r5, r6)     // Catch:{ all -> 0x0097 }
            monitor-exit(r2)     // Catch:{ all -> 0x0097 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return
        L_0x001c:
            boolean r5 = r4.isPersistent()     // Catch:{ all -> 0x0097 }
            if (r5 != 0) goto L_0x0027
            java.util.List r5 = r4.getPackageListWithVersionCode()     // Catch:{ all -> 0x0097 }
            r1 = r5
        L_0x0027:
            android.app.Dialog r5 = r4.anrDialog     // Catch:{ all -> 0x0097 }
            r6 = 317(0x13d, float:4.44E-43)
            if (r5 == 0) goto L_0x004e
            java.lang.String r5 = "ActivityManager"
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ all -> 0x0097 }
            r7.<init>()     // Catch:{ all -> 0x0097 }
            java.lang.String r8 = "App already has anr dialog: "
            r7.append(r8)     // Catch:{ all -> 0x0097 }
            r7.append(r4)     // Catch:{ all -> 0x0097 }
            java.lang.String r7 = r7.toString()     // Catch:{ all -> 0x0097 }
            android.util.Slog.e(r5, r7)     // Catch:{ all -> 0x0097 }
            android.content.Context r5 = r9.mContext     // Catch:{ all -> 0x0097 }
            r7 = -2
            com.android.internal.logging.MetricsLogger.action(r5, r6, r7)     // Catch:{ all -> 0x0097 }
            monitor-exit(r2)     // Catch:{ all -> 0x0097 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return
        L_0x004e:
            android.content.Context r5 = r9.mContext     // Catch:{ all -> 0x0097 }
            android.content.ContentResolver r5 = r5.getContentResolver()     // Catch:{ all -> 0x0097 }
            java.lang.String r7 = "anr_show_background"
            r8 = 0
            int r5 = android.provider.Settings.Secure.getInt(r5, r7, r8)     // Catch:{ all -> 0x0097 }
            if (r5 == 0) goto L_0x005e
            r8 = 1
        L_0x005e:
            r5 = r8
            com.android.server.am.ActivityManagerService r7 = r9.mService     // Catch:{ all -> 0x0097 }
            com.android.server.wm.ActivityTaskManagerInternal r7 = r7.mAtmInternal     // Catch:{ all -> 0x0097 }
            boolean r7 = r7.canShowErrorDialogs()     // Catch:{ all -> 0x0097 }
            if (r7 != 0) goto L_0x0079
            if (r5 == 0) goto L_0x006c
            goto L_0x0079
        L_0x006c:
            android.content.Context r7 = r9.mContext     // Catch:{ all -> 0x0097 }
            r8 = -1
            com.android.internal.logging.MetricsLogger.action(r7, r6, r8)     // Catch:{ all -> 0x0097 }
            com.android.server.am.ActivityManagerService r6 = r9.mService     // Catch:{ all -> 0x0097 }
            r7 = 0
            r6.killAppAtUsersRequest(r4, r7)     // Catch:{ all -> 0x0097 }
            goto L_0x0085
        L_0x0079:
            com.android.server.am.AppNotRespondingDialog r6 = new com.android.server.am.AppNotRespondingDialog     // Catch:{ all -> 0x0097 }
            com.android.server.am.ActivityManagerService r7 = r9.mService     // Catch:{ all -> 0x0097 }
            android.content.Context r8 = r9.mContext     // Catch:{ all -> 0x0097 }
            r6.<init>(r7, r8, r3)     // Catch:{ all -> 0x0097 }
            r0 = r6
            r4.anrDialog = r0     // Catch:{ all -> 0x0097 }
        L_0x0085:
            monitor-exit(r2)     // Catch:{ all -> 0x0097 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            if (r0 == 0) goto L_0x008e
            r0.show()
        L_0x008e:
            if (r1 == 0) goto L_0x0096
            com.android.server.PackageWatchdog r2 = r9.mPackageWatchdog
            r3 = 4
            r2.onPackageFailure(r1, r3)
        L_0x0096:
            return
        L_0x0097:
            r3 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0097 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.AppErrors.handleShowAnrUi(android.os.Message):void");
    }

    static final class BadProcessInfo {
        final String longMsg;
        final String shortMsg;
        final String stack;
        final long time;

        BadProcessInfo(long time2, String shortMsg2, String longMsg2, String stack2) {
            this.time = time2;
            this.shortMsg = shortMsg2;
            this.longMsg = longMsg2;
            this.stack = stack2;
        }
    }
}
