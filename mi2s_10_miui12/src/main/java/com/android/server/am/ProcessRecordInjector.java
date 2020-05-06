package com.android.server.am;

import android.app.AppGlobals;
import android.content.pm.PackageInfo;
import android.content.pm.ParceledListSlice;
import android.os.Build;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.server.am.SplitScreenReporter;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.os.BackgroundThread;
import com.android.server.LocalServices;
import com.android.server.UiModeManagerService;
import com.android.server.pm.PackageManagerService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import miui.mqsas.sdk.MQSEventManagerDelegate;
import miui.mqsas.sdk.event.ContinuousKillProcessEvent;
import miui.mqsas.sdk.event.KillProcessEvent;
import miui.process.ProcessManagerInternal;
import org.json.JSONException;
import org.json.JSONObject;

class ProcessRecordInjector {
    private static final long CONTINUOUS_KILL_EVENT_REPORT_TIME = 7200000;
    private static final long CONTINUOUS_KILL_RECORD_COUNT_LIMIT = 5;
    private static final String DEVICE = SystemProperties.get("ro.product.device", "UNKNOWN");
    private static final boolean ENABLE_LENIENT_CACHE = SystemProperties.getBoolean("persist.am.enable_lenient_cache", false);
    private static final long EXCESSIVE_CPU_RECORD_COUNT_LIMIT = 10;
    private static final long KILL_RECORD_COUNT_TIME_OUT = 300000;
    private static final int MAX_LOW_MEM_TIME = 1200000;
    private static final int MAX_PREVIOUS_APP_COUNT = (isLowMemoryDevice() ? isLowestMemoryDevice() ? 1 : 2 : 5);
    private static final int MAX_PREVIOUS_TIME = 60000;
    private static final long MEM_THRESHOLD_IN_WHITE_LIST = 71680;
    public static final String POLICY_CHANGED_PKG = "pkg changed";
    public static final String POLICY_CLEAR_DATA = "clearApplicationUserData";
    public static final String POLICY_DELETE_PACKAGE = "deletePackageX";
    public static final String POLICY_FINISH_USER = "finish user";
    public static final String POLICY_INSTALL_PACKAGE = "installPackageLI";
    public static final String POLICY_START_INSTR = "start instr";
    public static final String POLICY_UNINSTALL_PKG = "pkg removed";
    private static final int PREVIOUS_APP_MAX_ADJ = ((MAX_PREVIOUS_APP_COUNT + 700) - 1);
    public static final int PREVIOUS_APP_MIN_ADJ = 700;
    private static final int PROCESS_BUFFER_SIZE = 30;
    private static final String TAG = "ProcessRecordInjector";
    @GuardedBy({"sLock"})
    private static final SparseArray<Map<String, AppPss>> sAppPssUserMap = new SparseArray<>();
    @GuardedBy({"sLock"})
    private static final SparseArray<KillProcessEvent> sCachedProcessList = new SparseArray<>();
    /* access modifiers changed from: private */
    public static final Map<String, Integer> sContinuousKillProcessMap = new ConcurrentHashMap();
    @GuardedBy({"sLock"})
    private static final SparseArray<KillProcessEvent> sDeathProcessList = new SparseArray<>();
    @GuardedBy({"sLock"})
    private static final SparseArray<KillProcessEvent> sKillingProcessList = new SparseArray<>();
    private static final Map<String, Long> sLastContinuousKillEventMap = new ConcurrentHashMap();
    private static long sLastLowMemTime = 0;
    private static final Object sLock = new Object();
    private static List<String> sPolicyWhiteList = new ArrayList();
    private static volatile ProcessManagerInternal sProcessManagerInternal = null;
    private static final ArrayMap<String, Proc> sProcessStats = new ArrayMap<>();
    private static boolean sSystemBootCompleted;
    private static ActivityManagerService service;

    ProcessRecordInjector() {
    }

    static {
        sPolicyWhiteList.add(ProcessPolicy.REASON_ONE_KEY_CLEAN);
        sPolicyWhiteList.add(POLICY_UNINSTALL_PKG);
        sPolicyWhiteList.add(POLICY_DELETE_PACKAGE);
        sPolicyWhiteList.add(POLICY_INSTALL_PACKAGE);
        sPolicyWhiteList.add(POLICY_FINISH_USER);
        sPolicyWhiteList.add(POLICY_START_INSTR);
        sPolicyWhiteList.add(POLICY_CLEAR_DATA);
        sPolicyWhiteList.add(POLICY_CHANGED_PKG);
        sPolicyWhiteList.add(ProcessPolicy.REASON_ONE_KEY_CLEAN);
        sPolicyWhiteList.add(ProcessPolicy.REASON_FORCE_CLEAN);
        sPolicyWhiteList.add(ProcessPolicy.REASON_GARBAGE_CLEAN);
        sPolicyWhiteList.add(ProcessPolicy.REASON_GAME_CLEAN);
        sPolicyWhiteList.add(ProcessPolicy.REASON_SWIPE_UP_CLEAN);
        sPolicyWhiteList.add(ProcessPolicy.REASON_USER_DEFINED);
    }

    private static ProcessManagerInternal getProcessManagerInternal() {
        if (sProcessManagerInternal == null) {
            synchronized (ProcessRecordInjector.class) {
                if (sProcessManagerInternal == null) {
                    sProcessManagerInternal = (ProcessManagerInternal) LocalServices.getService(ProcessManagerInternal.class);
                }
            }
        }
        return sProcessManagerInternal;
    }

    public static void updateProcessForegroundLocked(ProcessRecord app) {
        getProcessManagerInternal().updateProcessForegroundLocked(app.pid);
    }

    public static boolean isPreviousApp(ProcessRecord app, int curPreviousAdj, long now) {
        return app.hasActivities() && curPreviousAdj <= PREVIOUS_APP_MAX_ADJ && now - app.lastActivityTime <= 60000;
    }

    public static boolean isLowMemoryDevice() {
        return ((int) (Process.getTotalMemory() / 1073741824)) < 3;
    }

    public static boolean isLowestMemoryDevice() {
        return ((int) (Process.getTotalMemory() / 1073741824)) < 2;
    }

    public static boolean shouldKillHighFrequencyApp(ProcessList processList, ProcessRecord app) {
        boolean z = true;
        if (ENABLE_LENIENT_CACHE && getProcessManagerInternal().isPackageFastBootEnable(app.info.packageName, app.uid, false)) {
            if (((double) app.lastCachedPss) < ((double) processList.getCachedRestoreThresholdKb()) * 1.5d) {
                z = false;
            }
            boolean kill = z;
            if (!kill) {
                Slog.d(TAG, "delay Kill high frequency app ：" + app.info.packageName);
            }
            return kill;
        } else if (app.lastCachedPss >= processList.getCachedRestoreThresholdKb()) {
            return true;
        } else {
            return false;
        }
    }

    public static void reportLowMemIfNeeded(final ActivityManagerService service2, final ProcessRecord dyingProc) {
        long now = SystemClock.uptimeMillis();
        if (now > sLastLowMemTime + 1200000) {
            BackgroundThread.getHandler().post(new Runnable() {
                public void run() {
                    ProcessRecordInjector.reportLowMemEvent(ActivityManagerService.this, dyingProc);
                }
            });
            sLastLowMemTime = now;
        }
    }

    /* JADX INFO: finally extract failed */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v15, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v3, resolved type: com.android.server.am.ProcessRecord} */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:114:0x02e0, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:87:0x025d, code lost:
        r0 = th;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void reportLowMemEvent(com.android.server.am.ActivityManagerService r23, com.android.server.am.ProcessRecord r24) {
        /*
            r1 = r23
            r2 = 0
            android.util.SparseArray r0 = new android.util.SparseArray
            r0.<init>()
            r3 = r0
            miui.mqsas.sdk.event.LowMemEvent r0 = new miui.mqsas.sdk.event.LowMemEvent
            r0.<init>()
            r4 = r0
            if (r1 == 0) goto L_0x02e2
            com.android.server.am.ProcessList r0 = r1.mProcessList
            java.util.ArrayList<com.android.server.am.ProcessRecord> r0 = r0.mLruProcesses
            int r0 = r0.size()
            if (r0 != 0) goto L_0x001f
            r10 = r24
            goto L_0x02e4
        L_0x001f:
            monitor-enter(r23)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x02d8 }
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ all -> 0x02d8 }
            com.android.server.am.ProcessList r5 = r1.mProcessList     // Catch:{ all -> 0x02d8 }
            java.util.ArrayList<com.android.server.am.ProcessRecord> r5 = r5.mLruProcesses     // Catch:{ all -> 0x02d8 }
            r0.<init>(r5)     // Catch:{ all -> 0x02d8 }
            r5 = r0
            monitor-exit(r23)     // Catch:{ all -> 0x02d8 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            int r0 = r5.size()
            int r0 = r0 + -1
            r6 = r2
            r2 = r0
        L_0x0039:
            if (r2 < 0) goto L_0x025f
            java.lang.Object r0 = r5.get(r2)
            r7 = r0
            com.android.server.am.ProcessRecord r7 = (com.android.server.am.ProcessRecord) r7
            monitor-enter(r23)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0255 }
            android.app.IApplicationThread r0 = r7.thread     // Catch:{ all -> 0x0255 }
            int r8 = r7.getSetAdjWithServices()     // Catch:{ all -> 0x0255 }
            int r9 = r7.pid     // Catch:{ all -> 0x0255 }
            monitor-exit(r23)     // Catch:{ all -> 0x0255 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            if (r0 == 0) goto L_0x024f
            r10 = r24
            if (r10 != r7) goto L_0x005a
            goto L_0x0251
        L_0x005a:
            if (r6 != 0) goto L_0x0062
            android.os.Debug$MemoryInfo r11 = new android.os.Debug$MemoryInfo
            r11.<init>()
            r6 = r11
        L_0x0062:
            android.os.Debug.getMemoryInfo(r9, r6)
            int r11 = r6.getTotalPss()
            long r13 = (long) r11
            int r11 = r6.getTotalSwappedOutPss()
            long r11 = (long) r11
            miui.mqsas.sdk.event.LowMemEvent$ProcessMemItem r18 = new miui.mqsas.sdk.event.LowMemEvent$ProcessMemItem
            java.lang.String r15 = r7.processName
            r19 = r11
            r12 = r18
            r21 = r13
            r11 = r15
            r15 = r19
            r17 = r11
            r12.<init>(r13, r15, r17)
            r11 = r18
            r3.put(r9, r11)
            r12 = -1000(0xfffffffffffffc18, float:NaN)
            r13 = -900(0xfffffffffffffc7c, float:NaN)
            if (r8 < r12) goto L_0x00a7
            if (r8 >= r13) goto L_0x00a7
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.nativeMemOom
            java.util.List r12 = r12.items
            r12.add(r11)
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.nativeMemOom
            long r13 = r12.totalPss
            long r13 = r13 + r21
            r12.totalPss = r13
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.nativeMemOom
            long r13 = r12.totalSwapPss
            long r13 = r13 + r19
            r12.totalSwapPss = r13
            goto L_0x0251
        L_0x00a7:
            r12 = -800(0xfffffffffffffce0, float:NaN)
            if (r8 < r13) goto L_0x00c6
            if (r8 >= r12) goto L_0x00c6
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.systemMemOom
            java.util.List r12 = r12.items
            r12.add(r11)
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.systemMemOom
            long r13 = r12.totalPss
            long r13 = r13 + r21
            r12.totalPss = r13
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.systemMemOom
            long r13 = r12.totalSwapPss
            long r13 = r13 + r19
            r12.totalSwapPss = r13
            goto L_0x0251
        L_0x00c6:
            r13 = -700(0xfffffffffffffd44, float:NaN)
            if (r8 < r12) goto L_0x00e5
            if (r8 >= r13) goto L_0x00e5
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.persistentMemOom
            java.util.List r12 = r12.items
            r12.add(r11)
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.persistentMemOom
            long r13 = r12.totalPss
            long r13 = r13 + r21
            r12.totalPss = r13
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.persistentMemOom
            long r13 = r12.totalSwapPss
            long r13 = r13 + r19
            r12.totalSwapPss = r13
            goto L_0x0251
        L_0x00e5:
            if (r8 < r13) goto L_0x0102
            if (r8 >= 0) goto L_0x0102
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.persistentServiceMemOom
            java.util.List r12 = r12.items
            r12.add(r11)
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.persistentServiceMemOom
            long r13 = r12.totalPss
            long r13 = r13 + r21
            r12.totalPss = r13
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.persistentServiceMemOom
            long r13 = r12.totalSwapPss
            long r13 = r13 + r19
            r12.totalSwapPss = r13
            goto L_0x0251
        L_0x0102:
            r12 = 100
            if (r8 < 0) goto L_0x0121
            if (r8 >= r12) goto L_0x0121
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.foregroundMemOom
            java.util.List r12 = r12.items
            r12.add(r11)
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.foregroundMemOom
            long r13 = r12.totalPss
            long r13 = r13 + r21
            r12.totalPss = r13
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.foregroundMemOom
            long r13 = r12.totalSwapPss
            long r13 = r13 + r19
            r12.totalSwapPss = r13
            goto L_0x0251
        L_0x0121:
            r13 = 200(0xc8, float:2.8E-43)
            if (r8 < r12) goto L_0x0140
            if (r8 >= r13) goto L_0x0140
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.visibleMemOom
            java.util.List r12 = r12.items
            r12.add(r11)
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.visibleMemOom
            long r13 = r12.totalPss
            long r13 = r13 + r21
            r12.totalPss = r13
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.visibleMemOom
            long r13 = r12.totalSwapPss
            long r13 = r13 + r19
            r12.totalSwapPss = r13
            goto L_0x0251
        L_0x0140:
            r12 = 250(0xfa, float:3.5E-43)
            if (r8 < r13) goto L_0x015f
            if (r8 >= r12) goto L_0x015f
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.perceptibleMemOom
            java.util.List r12 = r12.items
            r12.add(r11)
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.perceptibleMemOom
            long r13 = r12.totalPss
            long r13 = r13 + r21
            r12.totalPss = r13
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.perceptibleMemOom
            long r13 = r12.totalSwapPss
            long r13 = r13 + r19
            r12.totalSwapPss = r13
            goto L_0x0251
        L_0x015f:
            r13 = 300(0x12c, float:4.2E-43)
            if (r8 < r12) goto L_0x017e
            if (r8 >= r13) goto L_0x017e
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.perceptibleLowMemOom
            java.util.List r12 = r12.items
            r12.add(r11)
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.perceptibleLowMemOom
            long r13 = r12.totalPss
            long r13 = r13 + r21
            r12.totalPss = r13
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.perceptibleLowMemOom
            long r13 = r12.totalSwapPss
            long r13 = r13 + r19
            r12.totalSwapPss = r13
            goto L_0x0251
        L_0x017e:
            r12 = 400(0x190, float:5.6E-43)
            if (r8 < r13) goto L_0x019d
            if (r8 >= r12) goto L_0x019d
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.backupMemOom
            java.util.List r12 = r12.items
            r12.add(r11)
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.backupMemOom
            long r13 = r12.totalPss
            long r13 = r13 + r21
            r12.totalPss = r13
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.backupMemOom
            long r13 = r12.totalSwapPss
            long r13 = r13 + r19
            r12.totalSwapPss = r13
            goto L_0x0251
        L_0x019d:
            r13 = 500(0x1f4, float:7.0E-43)
            if (r8 < r12) goto L_0x01bc
            if (r8 >= r13) goto L_0x01bc
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.heavyWeightMemOom
            java.util.List r12 = r12.items
            r12.add(r11)
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.heavyWeightMemOom
            long r13 = r12.totalPss
            long r13 = r13 + r21
            r12.totalPss = r13
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.heavyWeightMemOom
            long r13 = r12.totalSwapPss
            long r13 = r13 + r19
            r12.totalSwapPss = r13
            goto L_0x0251
        L_0x01bc:
            r12 = 600(0x258, float:8.41E-43)
            if (r8 < r13) goto L_0x01db
            if (r8 >= r12) goto L_0x01db
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.aServicesMemOom
            java.util.List r12 = r12.items
            r12.add(r11)
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.aServicesMemOom
            long r13 = r12.totalPss
            long r13 = r13 + r21
            r12.totalPss = r13
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.aServicesMemOom
            long r13 = r12.totalSwapPss
            long r13 = r13 + r19
            r12.totalSwapPss = r13
            goto L_0x0251
        L_0x01db:
            r13 = 700(0x2bc, float:9.81E-43)
            if (r8 < r12) goto L_0x01f9
            if (r8 >= r13) goto L_0x01f9
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.homeMemOom
            java.util.List r12 = r12.items
            r12.add(r11)
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.homeMemOom
            long r13 = r12.totalPss
            long r13 = r13 + r21
            r12.totalPss = r13
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.homeMemOom
            long r13 = r12.totalSwapPss
            long r13 = r13 + r19
            r12.totalSwapPss = r13
            goto L_0x0251
        L_0x01f9:
            r12 = 800(0x320, float:1.121E-42)
            if (r8 < r13) goto L_0x0217
            if (r8 >= r12) goto L_0x0217
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.previousMemOom
            java.util.List r12 = r12.items
            r12.add(r11)
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.previousMemOom
            long r13 = r12.totalPss
            long r13 = r13 + r21
            r12.totalPss = r13
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.previousMemOom
            long r13 = r12.totalSwapPss
            long r13 = r13 + r19
            r12.totalSwapPss = r13
            goto L_0x0251
        L_0x0217:
            r13 = 900(0x384, float:1.261E-42)
            if (r8 < r12) goto L_0x0235
            if (r8 >= r13) goto L_0x0235
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.bServicesMemOom
            java.util.List r12 = r12.items
            r12.add(r11)
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.bServicesMemOom
            long r13 = r12.totalPss
            long r13 = r13 + r21
            r12.totalPss = r13
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.bServicesMemOom
            long r13 = r12.totalSwapPss
            long r13 = r13 + r19
            r12.totalSwapPss = r13
            goto L_0x0251
        L_0x0235:
            if (r8 < r13) goto L_0x0251
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.cachedMemOom
            java.util.List r12 = r12.items
            r12.add(r11)
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.cachedMemOom
            long r13 = r12.totalPss
            long r13 = r13 + r21
            r12.totalPss = r13
            miui.mqsas.sdk.event.LowMemEvent$MemOom r12 = r4.cachedMemOom
            long r13 = r12.totalSwapPss
            long r13 = r13 + r19
            r12.totalSwapPss = r13
            goto L_0x0251
        L_0x024f:
            r10 = r24
        L_0x0251:
            int r2 = r2 + -1
            goto L_0x0039
        L_0x0255:
            r0 = move-exception
            r10 = r24
        L_0x0258:
            monitor-exit(r23)     // Catch:{ all -> 0x025d }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x025d:
            r0 = move-exception
            goto L_0x0258
        L_0x025f:
            r10 = r24
            r23.updateCpuStatsNow()
            r2 = 0
            com.android.internal.os.ProcessCpuTracker r6 = r1.mProcessCpuTracker
            monitor-enter(r6)
            com.android.internal.os.ProcessCpuTracker r0 = r1.mProcessCpuTracker     // Catch:{ all -> 0x02d5 }
            int r0 = r0.countStats()     // Catch:{ all -> 0x02d5 }
            r7 = 0
        L_0x026f:
            if (r7 >= r0) goto L_0x02c9
            com.android.internal.os.ProcessCpuTracker r8 = r1.mProcessCpuTracker     // Catch:{ all -> 0x02d5 }
            com.android.internal.os.ProcessCpuTracker$Stats r8 = r8.getStats(r7)     // Catch:{ all -> 0x02d5 }
            long r11 = r8.vsize     // Catch:{ all -> 0x02d5 }
            r13 = 0
            int r9 = (r11 > r13 ? 1 : (r11 == r13 ? 0 : -1))
            if (r9 <= 0) goto L_0x02c6
            int r9 = r8.pid     // Catch:{ all -> 0x02d5 }
            int r9 = r3.indexOfKey(r9)     // Catch:{ all -> 0x02d5 }
            if (r9 >= 0) goto L_0x02c6
            if (r2 != 0) goto L_0x028f
            android.os.Debug$MemoryInfo r9 = new android.os.Debug$MemoryInfo     // Catch:{ all -> 0x02d5 }
            r9.<init>()     // Catch:{ all -> 0x02d5 }
            r2 = r9
        L_0x028f:
            int r9 = r8.pid     // Catch:{ all -> 0x02d5 }
            android.os.Debug.getMemoryInfo(r9, r2)     // Catch:{ all -> 0x02d5 }
            int r9 = r2.getTotalPss()     // Catch:{ all -> 0x02d5 }
            long r14 = (long) r9     // Catch:{ all -> 0x02d5 }
            int r9 = r2.getTotalSwappedOutPss()     // Catch:{ all -> 0x02d5 }
            long r12 = (long) r9     // Catch:{ all -> 0x02d5 }
            miui.mqsas.sdk.event.LowMemEvent$ProcessMemItem r9 = new miui.mqsas.sdk.event.LowMemEvent$ProcessMemItem     // Catch:{ all -> 0x02d5 }
            java.lang.String r11 = r8.name     // Catch:{ all -> 0x02d5 }
            r16 = r11
            r11 = r9
            r17 = r12
            r12 = r14
            r19 = r14
            r14 = r17
            r11.<init>(r12, r14, r16)     // Catch:{ all -> 0x02d5 }
            miui.mqsas.sdk.event.LowMemEvent$MemOom r11 = r4.nativeMemOom     // Catch:{ all -> 0x02d5 }
            java.util.List r11 = r11.items     // Catch:{ all -> 0x02d5 }
            r11.add(r9)     // Catch:{ all -> 0x02d5 }
            miui.mqsas.sdk.event.LowMemEvent$MemOom r11 = r4.nativeMemOom     // Catch:{ all -> 0x02d5 }
            long r12 = r11.totalPss     // Catch:{ all -> 0x02d5 }
            long r12 = r12 + r19
            r11.totalPss = r12     // Catch:{ all -> 0x02d5 }
            miui.mqsas.sdk.event.LowMemEvent$MemOom r11 = r4.nativeMemOom     // Catch:{ all -> 0x02d5 }
            long r12 = r11.totalSwapPss     // Catch:{ all -> 0x02d5 }
            long r12 = r12 + r17
            r11.totalSwapPss = r12     // Catch:{ all -> 0x02d5 }
        L_0x02c6:
            int r7 = r7 + 1
            goto L_0x026f
        L_0x02c9:
            monitor-exit(r6)     // Catch:{ all -> 0x02d5 }
            r4.sortAndSub()
            miui.mqsas.sdk.MQSEventManagerDelegate r0 = miui.mqsas.sdk.MQSEventManagerDelegate.getInstance()
            r0.reportLowMemEvent(r4)
            return
        L_0x02d5:
            r0 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x02d5 }
            throw r0
        L_0x02d8:
            r0 = move-exception
            r10 = r24
        L_0x02db:
            monitor-exit(r23)     // Catch:{ all -> 0x02e0 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x02e0:
            r0 = move-exception
            goto L_0x02db
        L_0x02e2:
            r10 = r24
        L_0x02e4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ProcessRecordInjector.reportLowMemEvent(com.android.server.am.ActivityManagerService, com.android.server.am.ProcessRecord):void");
    }

    private static boolean isInterestingToUser(ProcessRecord app) {
        return app.getWindowProcessController().isInterestingToUser();
    }

    public static void reportKillProcessEvent(ProcessRecord app, String reason) {
        if (app != null) {
            reportKillProcessEvent(app.processName, app.pid, app.setProcState, app.setAdj, reason, isInterestingToUser(app));
            reportContinuousKillEventIfNeed(app, reason);
        }
    }

    public static void reportKillProcessEvent(int killerPid, int killedPid, String reason) {
        ProcessRecord killedApp;
        String processName = "No pending application";
        int procState = -1;
        int processAdj = Integer.MAX_VALUE;
        if (TextUtils.isEmpty(reason)) {
            if (service == null) {
                service = (ActivityManagerService) ServiceManager.getService("activity");
            }
            synchronized (service.mPidsSelfLocked) {
                killedApp = service.mPidsSelfLocked.get(killedPid);
                if (killedApp != null) {
                    processName = killedApp.processName;
                    procState = killedApp.setProcState;
                    processAdj = killedApp.setAdj;
                }
                if (killerPid == killedPid) {
                    reason = "killself";
                } else {
                    ProcessRecord killerApp = service.mPidsSelfLocked.get(killerPid);
                    reason = killerApp != null ? killerApp.processName : "Killer Has Gone";
                }
            }
            if (killedApp != null) {
                reportContinuousKillEventIfNeed(killedApp, reason);
            }
        }
        reportKillProcessEvent(processName, killedPid, procState, processAdj, reason, false);
    }

    private static void reportKillProcessEvent(String processName, int killedPid, int procState, int processAdj, String reason, boolean interesting) {
        synchronized (sLock) {
            KillProcessEvent event = new KillProcessEvent();
            event.setKilledReason(reason);
            event.setKilledProc(processName);
            event.setProcState(procState);
            event.setProcAdj(processAdj);
            event.setKilledTime(SystemClock.uptimeMillis());
            event.setInterestingToUser(interesting);
            sKillingProcessList.put(killedPid, event);
        }
        killRecordCount(processName, reason);
        getProcessManagerInternal().recordKillProcessEventIfNeeded(reason, processName, killedPid);
    }

    public static void reportBinderDied(ProcessRecord app) {
        if (app != null) {
            reportBinderDied(app.processName, app.pid, app.setAdj, app.isInterestingToUserLocked());
            getProcessManagerInternal().onAppBinderDied(app);
        }
    }

    private static void reportBinderDied(String processName, int killedPid, int processAdj, boolean interesting) {
        synchronized (sLock) {
            KillProcessEvent event = sKillingProcessList.get(killedPid);
            if (event == null) {
                event = new KillProcessEvent();
                event.setPolicy("other");
                event.setKilledProc(processName);
                event.setProcAdj(processAdj);
                event.setKilledTime(SystemClock.uptimeMillis());
                event.setInterestingToUser(interesting);
            }
            sDeathProcessList.put(killedPid, event);
        }
        FindDeviceAliveChecker.postCheckFindDeviceAliveDelayed(processName);
    }

    public static void reportCleanUpAppRecord(ProcessRecord app) {
        if (app != null) {
            reportCleanUpAppRecord(app.processName, app.pid, app.setAdj, app.setProcState, app.isInterestingToUserLocked());
            getProcessManagerInternal().onCleanUpApplicationRecord(app);
        }
    }

    private static void reportCleanUpAppRecord(String processName, int killedPid, int processAdj, int procState, boolean interesting) {
        synchronized (sLock) {
            KillProcessEvent killingProcess = sKillingProcessList.get(killedPid);
            KillProcessEvent deathProcess = sDeathProcessList.get(killedPid);
            if (killingProcess != null) {
                sKillingProcessList.remove(killedPid);
                sCachedProcessList.put(killedPid, killingProcess);
            } else {
                if (deathProcess == null) {
                    deathProcess = new KillProcessEvent();
                    deathProcess.setPolicy("exception");
                    deathProcess.setKilledProc(processName);
                    deathProcess.setProcAdj(processAdj);
                    deathProcess.setProcState(procState);
                    deathProcess.setKilledTime(SystemClock.uptimeMillis());
                    deathProcess.setInterestingToUser(interesting);
                }
                sCachedProcessList.put(killedPid, deathProcess);
            }
            if (deathProcess != null) {
                sDeathProcessList.remove(killedPid);
            }
            if (sCachedProcessList.size() >= 30 && isSystemBootCompleted()) {
                Slog.d(TAG, "Begin to report kill process events...");
                List<KillProcessEvent> events = new ArrayList<>();
                for (int i = 0; i < sCachedProcessList.size(); i++) {
                    events.add(sCachedProcessList.valueAt(i));
                }
                sCachedProcessList.clear();
                reportKillProcessEvents(events);
            }
        }
    }

    private static void reportKillProcessEvents(List<KillProcessEvent> events) {
        final ParceledListSlice<KillProcessEvent> reportEvents = new ParceledListSlice<>(events);
        BackgroundThread.getHandler().post(new Runnable() {
            public void run() {
                MQSEventManagerDelegate.getInstance().reportKillProcessEvents(reportEvents);
            }
        });
    }

    public static boolean isSystemBootCompleted() {
        if (!sSystemBootCompleted) {
            sSystemBootCompleted = SplitScreenReporter.ACTION_ENTER_SPLIT.equals(SystemProperties.get("sys.boot_completed"));
        }
        return sSystemBootCompleted;
    }

    public static void checkNativeKillInList(int killedPid, String realReason) {
        synchronized (sLock) {
            KillProcessEvent event = sCachedProcessList.get(killedPid);
            if (event == null) {
                event = sDeathProcessList.get(killedPid);
            }
            if (event == null || !TextUtils.equals(event.getPolicy(), "other")) {
                Slog.w(TAG, "oops, missing real event:" + killedPid + ", " + realReason);
                return;
            }
            event.setPolicy(realReason);
            killRecordCount(event.getKilledProc(), realReason);
        }
    }

    private static void killRecordCount(String processName, String reason) {
        if (!sPolicyWhiteList.contains(reason.trim()) && !hasProcessReported(processName)) {
            Integer expCount = sContinuousKillProcessMap.get(processName);
            if (expCount == null) {
                expCount = 0;
            }
            Map<String, Integer> map = sContinuousKillProcessMap;
            Integer valueOf = Integer.valueOf(expCount.intValue() + 1);
            Integer expCount2 = valueOf;
            map.put(processName, valueOf);
            reduceRecordCountDelay(processName);
        }
    }

    private static void reduceRecordCountDelay(final String processName) {
        BackgroundThread.getHandler().postDelayed(new Runnable() {
            public void run() {
                Integer count = (Integer) ProcessRecordInjector.sContinuousKillProcessMap.get(processName);
                if (count != null && count.intValue() > 0) {
                    Integer count2 = Integer.valueOf(count.intValue() - 1);
                    if (count2.intValue() <= 0) {
                        ProcessRecordInjector.sContinuousKillProcessMap.remove(processName);
                    } else {
                        ProcessRecordInjector.sContinuousKillProcessMap.put(processName, count2);
                    }
                }
            }
        }, 300000);
    }

    private static void reportContinuousKillEventIfNeed(ProcessRecord killedApp, String reason) {
        Integer count;
        if (killedApp != null && !hasProcessReported(killedApp.processName) && (count = sContinuousKillProcessMap.get(killedApp.processName)) != null && ((long) count.intValue()) >= CONTINUOUS_KILL_RECORD_COUNT_LIMIT) {
            ContinuousKillProcessEvent event = new ContinuousKillProcessEvent();
            event.setKilledProc(killedApp.processName);
            event.setProcAdj(killedApp.setAdj);
            event.setProcState(killedApp.setProcState);
            event.setKilledReason(reason);
            StringBuilder buf = new StringBuilder();
            buf.append(killedApp.hostingRecord.getType());
            if (killedApp.hostingRecord.getName() != null) {
                buf.append(":");
                buf.append(killedApp.hostingRecord.getName());
            }
            event.setStartReason(buf.toString());
            event.setCallerPackage(killedApp.callerPackage);
            reportContinuousKillEvents(event);
            sLastContinuousKillEventMap.put(killedApp.processName, Long.valueOf(SystemClock.uptimeMillis()));
        }
    }

    private static boolean hasProcessReported(String processName) {
        Long lastReportTime = sLastContinuousKillEventMap.get(processName);
        if (lastReportTime == null) {
            return false;
        }
        if (SystemClock.uptimeMillis() <= lastReportTime.longValue() + 7200000) {
            return true;
        }
        sLastContinuousKillEventMap.remove(processName);
        return false;
    }

    private static void reportContinuousKillEvents(final ContinuousKillProcessEvent event) {
        BackgroundThread.getHandler().post(new Runnable() {
            public void run() {
                MQSEventManagerDelegate.getInstance().reportContinuousKillProcessEvent(event);
            }
        });
    }

    static final class AppPss {
        static final String MODEL = "model";
        static final String PACKAGE_NAME = "packageName";
        static final String TOTAL_PSS = "totalPss";
        static final String USER_ID = "userId";
        static final String VERSION_NAME = "versionName";
        String pkn;
        String pss;
        String user;
        String version;

        AppPss(String pkn2, long pss2, String version2, int user2) {
            this.pkn = pkn2;
            this.pss = String.valueOf(pss2);
            this.version = version2;
            this.user = String.valueOf(user2);
        }
    }

    public static void addAppPssIfNeeded(ProcessManagerService pms, ProcessRecord app) {
        PackageInfo pi;
        String pkn = app.info.packageName;
        if (!PackageManagerService.PLATFORM_PACKAGE_NAME.equals(pkn)) {
            if (service == null) {
                service = (ActivityManagerService) ServiceManager.getService("activity");
            }
            long pss = ProcessUtils.getPackageLastPss(service, pms, pkn, app.userId);
            synchronized (sLock) {
                Map<String, AppPss> appPssMap = sAppPssUserMap.get(app.userId);
                if (pss >= MEM_THRESHOLD_IN_WHITE_LIST && (appPssMap == null || appPssMap.get(pkn) == null)) {
                    try {
                        pi = AppGlobals.getPackageManager().getPackageInfo(pkn, 0, app.userId);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        pi = null;
                    }
                    AppPss appPss = new AppPss(pkn, pss, (pi == null || pi.versionName == null) ? UiModeManagerService.Shell.NIGHT_MODE_STR_UNKNOWN : pi.versionName, app.userId);
                    if (appPssMap == null) {
                        appPssMap = new HashMap<>();
                        sAppPssUserMap.put(app.userId, appPssMap);
                    }
                    appPssMap.put(pkn, appPss);
                }
            }
        }
    }

    static void reportAppPss() {
        final Map<String, AppPss> map = new HashMap<>();
        synchronized (sLock) {
            if (sAppPssUserMap.size() > 0) {
                int size = sAppPssUserMap.size();
                for (int i = 0; i < size; i++) {
                    Map<String, AppPss> appPssMap = sAppPssUserMap.valueAt(i);
                    if (appPssMap != null) {
                        map.putAll(appPssMap);
                    }
                }
                sAppPssUserMap.clear();
            }
        }
        if (map.size() != 0) {
            BackgroundThread.getHandler().post(new Runnable() {
                public void run() {
                    List<String> jsons = new ArrayList<>();
                    for (String pkn : map.keySet()) {
                        AppPss appPss = (AppPss) map.get(pkn);
                        if (appPss != null) {
                            JSONObject object = new JSONObject();
                            try {
                                object.put("packageName", appPss.pkn);
                                object.put("totalPss", appPss.pss);
                                object.put("versionName", appPss.version);
                                object.put("model", Build.MODEL);
                                object.put("userId", appPss.user);
                                jsons.add(object.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (jsons.size() > 0) {
                        MQSEventManagerDelegate.getInstance().reportEventsV2("appPss", jsons, "mqs_whiteapp_lowmem_monitor_63691000", false);
                    }
                }
            });
        }
    }

    public static void reportExcessiveCpuLocked(String proc, long unimportantTime, long overTime, long usedTime) {
        Proc p = getProcessStatsLocked(proc);
        if (p != null) {
            p.addExcessiveCpu(unimportantTime, overTime, usedTime);
        }
    }

    public static Proc getProcessStatsLocked(String name) {
        Proc ps = sProcessStats.get(name);
        if (ps != null) {
            return ps;
        }
        Proc ps2 = new Proc(name);
        sProcessStats.put(name, ps2);
        return ps2;
    }

    public static class Proc {
        static final String MODEL = "model";
        static final String PROCESS_NAME = "processName";
        ArrayList<ExcessivePower> mExcessivePower;
        final String mName;

        public static class ExcessivePower {
            static final String CPU_LIMIT = "cpuLimit";
            static final String OVER_TIME = "overTime";
            static final String UNIMPORTANT_TIME = "unimportantTime";
            static final String USED_TIME = "usedTime";
            public int cpuLimit;
            public long overTime;
            public long unimportantTime;
            public long usedTime;
        }

        public Proc(String name) {
            this.mName = name;
        }

        public void checkReport() {
            if (((long) this.mExcessivePower.size()) > ProcessRecordInjector.EXCESSIVE_CPU_RECORD_COUNT_LIMIT) {
                final ArrayList<ExcessivePower> excessivePowers = (ArrayList) this.mExcessivePower.clone();
                this.mExcessivePower.clear();
                BackgroundThread.getHandler().post(new Runnable() {
                    public void run() {
                        List<String> jsons = new ArrayList<>();
                        Iterator it = excessivePowers.iterator();
                        while (it.hasNext()) {
                            ExcessivePower info = (ExcessivePower) it.next();
                            JSONObject object = new JSONObject();
                            try {
                                object.put(Proc.PROCESS_NAME, Proc.this.mName);
                                object.put("unimportantTime", info.unimportantTime);
                                object.put("overTime", info.overTime);
                                object.put("usedTime", info.usedTime);
                                object.put("cpuLimit", info.cpuLimit);
                                object.put(Proc.MODEL, Build.MODEL);
                                jsons.add(object.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        if (jsons.size() > 0) {
                            MQSEventManagerDelegate.getInstance().reportEventsV2("excessiveCpu", jsons, "mqs_daemon_cpu_usage_30991000", false);
                        }
                    }
                });
            }
        }

        public void addExcessiveCpu(long unimportantTime, long overTime, long usedTime) {
            if (this.mExcessivePower == null) {
                this.mExcessivePower = new ArrayList<>();
            }
            ExcessivePower ew = new ExcessivePower();
            ew.unimportantTime = unimportantTime;
            ew.overTime = overTime;
            ew.usedTime = usedTime;
            ew.cpuLimit = (int) ((100 * usedTime) / overTime);
            this.mExcessivePower.add(ew);
            checkReport();
        }
    }
}
