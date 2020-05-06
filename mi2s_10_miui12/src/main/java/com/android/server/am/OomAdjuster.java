package com.android.server.am;

import android.os.Handler;
import android.os.Message;
import android.os.PowerManagerInternal;
import android.os.Process;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.util.ArraySet;
import android.util.BoostFramework;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.LocalServices;
import com.android.server.ServiceThread;
import com.android.server.wm.WindowProcessController;
import java.io.PrintWriter;

public final class OomAdjuster {
    static final String OOM_ADJ_REASON_ACTIVITY = "updateOomAdj_activityChange";
    static final String OOM_ADJ_REASON_BIND_SERVICE = "updateOomAdj_bindService";
    static final String OOM_ADJ_REASON_FINISH_RECEIVER = "updateOomAdj_finishReceiver";
    static final String OOM_ADJ_REASON_GET_PROVIDER = "updateOomAdj_getProvider";
    static final String OOM_ADJ_REASON_METHOD = "updateOomAdj";
    static final String OOM_ADJ_REASON_NONE = "updateOomAdj_meh";
    static final String OOM_ADJ_REASON_PROCESS_BEGIN = "updateOomAdj_processBegin";
    static final String OOM_ADJ_REASON_PROCESS_END = "updateOomAdj_processEnd";
    static final String OOM_ADJ_REASON_REMOVE_PROVIDER = "updateOomAdj_removeProvider";
    static final String OOM_ADJ_REASON_START_RECEIVER = "updateOomAdj_startReceiver";
    static final String OOM_ADJ_REASON_START_SERVICE = "updateOomAdj_startService";
    static final String OOM_ADJ_REASON_UI_VISIBILITY = "updateOomAdj_uiVisibility";
    static final String OOM_ADJ_REASON_UNBIND_SERVICE = "updateOomAdj_unbindService";
    static final String OOM_ADJ_REASON_WHITELIST = "updateOomAdj_whitelistChange";
    private static final String TAG = "OomAdjuster";
    public static BoostFramework mPerf = new BoostFramework();
    ActiveUids mActiveUids;
    int mAdjSeq = 0;
    AppCompactor mAppCompact;
    int mBServiceAppThreshold = 5;
    ActivityManagerConstants mConstants;
    boolean mEnableBServicePropagation = false;
    boolean mEnableProcessGroupCgroupFollow = false;
    PowerManagerInternal mLocalPowerManager;
    int mMinBServiceAgingTime = 5000;
    int mNewNumAServiceProcs = 0;
    int mNewNumServiceProcs = 0;
    int mNumCachedHiddenProcs = 0;
    int mNumNonCachedProcs = 0;
    int mNumServiceProcs = 0;
    boolean mProcessGroupCgroupFollowDex2oatOnly = false;
    private final Handler mProcessGroupHandler;
    private final ProcessList mProcessList;
    private final ActivityManagerService mService;
    private final ArraySet<BroadcastQueue> mTmpBroadcastQueue = new ArraySet<>();
    private final ComputeOomAdjWindowCallback mTmpComputeOomAdjWindowCallback = new ComputeOomAdjWindowCallback();
    final long[] mTmpLong = new long[3];

    OomAdjuster(ActivityManagerService service, ProcessList processList, ActiveUids activeUids) {
        this.mService = service;
        this.mProcessList = processList;
        this.mActiveUids = activeUids;
        this.mLocalPowerManager = (PowerManagerInternal) LocalServices.getService(PowerManagerInternal.class);
        this.mConstants = this.mService.mConstants;
        this.mAppCompact = new AppCompactor(this.mService);
        BoostFramework boostFramework = mPerf;
        if (boostFramework != null) {
            this.mMinBServiceAgingTime = Integer.valueOf(boostFramework.perfGetProp("ro.vendor.qti.sys.fw.bservice_age", "5000")).intValue();
            this.mBServiceAppThreshold = Integer.valueOf(mPerf.perfGetProp("ro.vendor.qti.sys.fw.bservice_limit", "5")).intValue();
            this.mEnableBServicePropagation = Boolean.parseBoolean(mPerf.perfGetProp("ro.vendor.qti.sys.fw.bservice_enable", "false"));
            this.mEnableProcessGroupCgroupFollow = Boolean.parseBoolean(mPerf.perfGetProp("ro.vendor.qti.cgroup_follow.enable", "false"));
            this.mProcessGroupCgroupFollowDex2oatOnly = Boolean.parseBoolean(mPerf.perfGetProp("ro.vendor.qti.cgroup_follow.dex2oat_only", "false"));
        }
        ServiceThread adjusterThread = new ServiceThread(TAG, -10, false);
        adjusterThread.start();
        Process.setThreadGroupAndCpuset(adjusterThread.getThreadId(), 5);
        this.mProcessGroupHandler = new Handler(adjusterThread.getLooper(), new Handler.Callback() {
            public final boolean handleMessage(Message message) {
                return OomAdjuster.this.lambda$new$0$OomAdjuster(message);
            }
        });
    }

    public /* synthetic */ boolean lambda$new$0$OomAdjuster(Message msg) {
        Trace.traceBegin(64, "setProcessGroup");
        int pid = msg.arg1;
        int group = msg.arg2;
        try {
            if (this.mEnableProcessGroupCgroupFollow) {
                Process.setCgroupProcsProcessGroup(((Integer) msg.obj).intValue(), pid, group, this.mProcessGroupCgroupFollowDex2oatOnly);
            } else {
                Process.setProcessGroup(pid, group);
            }
        } catch (Exception e) {
        } catch (Throwable th) {
            Trace.traceEnd(64);
            throw th;
        }
        Trace.traceEnd(64);
        return true;
    }

    /* access modifiers changed from: package-private */
    public void initSettings() {
        this.mAppCompact.init();
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public boolean updateOomAdjLocked(ProcessRecord app, boolean oomAdjAll, String oomAdjReason) {
        ProcessRecord TOP_APP = this.mService.getTopAppLocked();
        boolean wasCached = app.cached;
        this.mAdjSeq++;
        boolean success = updateOomAdjLocked(app, app.getCurRawAdj() >= 900 ? app.getCurRawAdj() : 1001, TOP_APP, false, SystemClock.uptimeMillis());
        if (oomAdjAll && (wasCached != app.cached || app.getCurRawAdj() == 1001)) {
            updateOomAdjLocked(oomAdjReason);
        }
        return success;
    }

    @GuardedBy({"mService"})
    private final boolean updateOomAdjLocked(ProcessRecord app, int cachedAdj, ProcessRecord TOP_APP, boolean doingAll, long now) {
        if (app.thread == null) {
            return false;
        }
        computeOomAdjLocked(app, cachedAdj, TOP_APP, doingAll, now, false);
        return applyOomAdjLocked(app, doingAll, now, SystemClock.elapsedRealtime());
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v26, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v65, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v69, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v27, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v71, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v72, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v33, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r5v45, resolved type: boolean} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r1v86, resolved type: boolean} */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Multi-variable type inference failed */
    @com.android.internal.annotations.GuardedBy({"mService"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void updateOomAdjLocked(java.lang.String r64) {
        /*
            r63 = this;
            r8 = r63
            r9 = 64
            r11 = r64
            android.os.Trace.traceBegin(r9, r11)
            com.android.server.am.ActivityManagerService r0 = r8.mService
            com.android.server.am.OomAdjProfiler r0 = r0.mOomAdjProfiler
            r0.oomAdjStarted()
            com.android.server.am.ActivityManagerService r0 = r8.mService
            com.android.server.am.ProcessRecord r12 = r0.getTopAppLocked()
            long r13 = android.os.SystemClock.uptimeMillis()
            long r5 = android.os.SystemClock.elapsedRealtime()
            r15 = 1800000(0x1b7740, double:8.89318E-318)
            long r17 = r13 - r15
            com.android.server.am.ProcessList r0 = r8.mProcessList
            int r7 = r0.getLruSizeLocked()
            com.android.server.am.ActiveUids r0 = r8.mActiveUids
            int r0 = r0.size()
            r4 = 1
            int r0 = r0 - r4
        L_0x0031:
            if (r0 < 0) goto L_0x003f
            com.android.server.am.ActiveUids r1 = r8.mActiveUids
            com.android.server.am.UidRecord r1 = r1.valueAt(r0)
            r1.reset()
            int r0 = r0 + -1
            goto L_0x0031
        L_0x003f:
            com.android.server.am.ActivityManagerService r0 = r8.mService
            com.android.server.wm.ActivityTaskManagerInternal r0 = r0.mAtmInternal
            if (r0 == 0) goto L_0x004c
            com.android.server.am.ActivityManagerService r0 = r8.mService
            com.android.server.wm.ActivityTaskManagerInternal r0 = r0.mAtmInternal
            r0.rankTaskLayersIfNeeded()
        L_0x004c:
            int r0 = r8.mAdjSeq
            int r0 = r0 + r4
            r8.mAdjSeq = r0
            r3 = 0
            r8.mNewNumServiceProcs = r3
            r8.mNewNumAServiceProcs = r3
            com.android.server.am.ActivityManagerConstants r0 = r8.mConstants
            int r2 = r0.CUR_MAX_EMPTY_PROCESSES
            com.android.server.am.ActivityManagerConstants r0 = r8.mConstants
            int r0 = r0.CUR_MAX_CACHED_PROCESSES
            int r1 = r0 - r2
            r19 = 10
            int r0 = r8.mNumNonCachedProcs
            int r0 = r7 - r0
            int r9 = r8.mNumCachedHiddenProcs
            int r0 = r0 - r9
            if (r0 <= r1) goto L_0x006e
            r0 = r1
            r9 = r0
            goto L_0x006f
        L_0x006e:
            r9 = r0
        L_0x006f:
            int r0 = r9 + 10
            int r0 = r0 - r4
            r10 = 10
            int r0 = r0 / r10
            if (r0 >= r4) goto L_0x0078
            r0 = 1
        L_0x0078:
            int r15 = r8.mNumCachedHiddenProcs
            if (r15 <= 0) goto L_0x007f
            int r15 = r15 + r10
            int r15 = r15 - r4
            goto L_0x0080
        L_0x007f:
            r15 = r4
        L_0x0080:
            int r15 = r15 / r10
            if (r15 >= r4) goto L_0x0084
            r15 = 1
        L_0x0084:
            r16 = -1
            r22 = -1
            r23 = 0
            r24 = 0
            r25 = 0
            r26 = 0
            r27 = 0
            r28 = 0
            r29 = 0
            r8.mNumNonCachedProcs = r3
            r8.mNumCachedHiddenProcs = r3
            r30 = 701(0x2bd, float:9.82E-43)
            r31 = 900(0x384, float:1.261E-42)
            int r32 = r31 + 10
            r33 = 0
            r34 = 905(0x389, float:1.268E-42)
            int r35 = r34 + 10
            r36 = 0
            r37 = 0
            r39 = 0
            r40 = 0
            int r41 = r7 + -1
            r10 = r41
        L_0x00b2:
            r41 = r2
            r4 = 20
            if (r10 < 0) goto L_0x00d2
            com.android.server.am.ProcessList r2 = r8.mProcessList
            java.util.ArrayList<com.android.server.am.ProcessRecord> r2 = r2.mLruProcesses
            java.lang.Object r2 = r2.get(r10)
            com.android.server.am.ProcessRecord r2 = (com.android.server.am.ProcessRecord) r2
            r2.containsCycle = r3
            r2.setCurRawProcState(r4)
            r4 = 1001(0x3e9, float:1.403E-42)
            r2.setCurRawAdj(r4)
            int r10 = r10 + -1
            r2 = r41
            r4 = 1
            goto L_0x00b2
        L_0x00d2:
            r2 = 1001(0x3e9, float:1.403E-42)
            int r10 = r7 + -1
            r47 = r27
            r50 = r28
            r48 = r29
            r44 = r30
            r51 = r31
            r52 = r32
            r45 = r34
            r46 = r35
            r49 = r36
            r27 = r16
            r16 = r7
            r7 = r39
        L_0x00ee:
            r28 = r5
            if (r10 < 0) goto L_0x0307
            com.android.server.am.ProcessList r2 = r8.mProcessList
            java.util.ArrayList<com.android.server.am.ProcessRecord> r2 = r2.mLruProcesses
            java.lang.Object r2 = r2.get(r10)
            com.android.server.am.ProcessRecord r2 = (com.android.server.am.ProcessRecord) r2
            boolean r6 = r8.mEnableBServicePropagation
            if (r6 == 0) goto L_0x0152
            boolean r6 = r2.serviceb
            if (r6 == 0) goto L_0x0152
            int r6 = r2.curAdj
            r3 = 800(0x320, float:1.121E-42)
            if (r6 != r3) goto L_0x0152
            int r7 = r7 + 1
            android.util.ArraySet<com.android.server.am.ServiceRecord> r3 = r2.f3services
            int r3 = r3.size()
            r6 = 1
            int r3 = r3 - r6
        L_0x0114:
            if (r3 < 0) goto L_0x014f
            android.util.ArraySet<com.android.server.am.ServiceRecord> r6 = r2.f3services
            java.lang.Object r6 = r6.valueAt(r3)
            com.android.server.am.ServiceRecord r6 = (com.android.server.am.ServiceRecord) r6
            long r34 = android.os.SystemClock.uptimeMillis()
            long r4 = r6.lastActivity
            long r34 = r34 - r4
            int r4 = r8.mMinBServiceAgingTime
            long r4 = (long) r4
            int r4 = (r34 > r4 ? 1 : (r34 == r4 ? 0 : -1))
            if (r4 >= 0) goto L_0x012e
            goto L_0x014b
        L_0x012e:
            r4 = 0
            int r34 = (r37 > r4 ? 1 : (r37 == r4 ? 0 : -1))
            if (r34 != 0) goto L_0x013d
            long r4 = r6.lastActivity
            r34 = r2
            r37 = r4
            r49 = r34
            goto L_0x014b
        L_0x013d:
            long r4 = r6.lastActivity
            int r4 = (r4 > r37 ? 1 : (r4 == r37 ? 0 : -1))
            if (r4 >= 0) goto L_0x014b
            long r4 = r6.lastActivity
            r34 = r2
            r37 = r4
            r49 = r34
        L_0x014b:
            int r3 = r3 + -1
            r6 = 1
            goto L_0x0114
        L_0x014f:
            r34 = r7
            goto L_0x0154
        L_0x0152:
            r34 = r7
        L_0x0154:
            boolean r3 = r2.killedByAm
            if (r3 != 0) goto L_0x02c0
            android.app.IApplicationThread r3 = r2.thread
            if (r3 == 0) goto L_0x02c0
            r3 = 0
            r2.procStateChanged = r3
            r4 = 1001(0x3e9, float:1.403E-42)
            r5 = 1
            r7 = 0
            r6 = r0
            r0 = r63
            r53 = r1
            r1 = r2
            r55 = r2
            r54 = r41
            r2 = r4
            r4 = r3
            r3 = r12
            r32 = r4
            r4 = r5
            r56 = r6
            r30 = r28
            r5 = r13
            r28 = r9
            r9 = r16
            r0.computeOomAdjLocked(r1, r2, r3, r4, r5, r7)
            r0 = r55
            boolean r1 = r0.containsCycle
            r1 = r40 | r1
            int r2 = r0.curAdj
            r3 = 1001(0x3e9, float:1.403E-42)
            if (r2 < r3) goto L_0x02ab
            int r2 = r0.getCurProcState()
            switch(r2) {
                case 17: goto L_0x01be;
                case 18: goto L_0x01be;
                case 19: goto L_0x01be;
                default: goto L_0x0192;
            }
        L_0x0192:
            r5 = r44
            r16 = r47
            r6 = r48
            r35 = r50
            r7 = r51
            r4 = r52
            r11 = r45
            r3 = r46
            if (r11 == r3) goto L_0x028b
            int r2 = r22 + 1
            r29 = r7
            r7 = r56
            if (r2 < r7) goto L_0x0288
            r22 = 0
            r45 = r3
            int r2 = r3 + 10
            r3 = 999(0x3e7, float:1.4E-42)
            if (r2 <= r3) goto L_0x0284
            r46 = 999(0x3e7, float:1.4E-42)
            r11 = r45
            r3 = r46
            goto L_0x028f
        L_0x01be:
            r5 = r44
            boolean r2 = com.android.server.am.ProcessRecordInjector.isPreviousApp(r0, r5, r13)
            if (r2 == 0) goto L_0x01dc
            r0.setCurRawAdj(r5)
            int r2 = r0.modifyRawOomAdj(r5)
            r0.curAdj = r2
            java.lang.String r2 = "previous-act"
            r0.adjType = r2
            int r44 = r5 + 1
            r40 = r1
            r7 = r56
            goto L_0x02ee
        L_0x01dc:
            r2 = 0
            int r4 = r0.connectionGroup
            if (r4 == 0) goto L_0x023b
            int r4 = r0.uid
            r6 = r48
            if (r6 != r4) goto L_0x0226
            int r4 = r0.connectionGroup
            r7 = r47
            if (r7 != r4) goto L_0x021b
            int r4 = r0.connectionImportance
            r16 = r7
            r7 = r50
            if (r4 <= r7) goto L_0x020d
            int r4 = r0.connectionImportance
            r29 = r4
            r7 = r51
            r4 = r52
            if (r7 >= r4) goto L_0x0208
            r11 = 999(0x3e7, float:1.4E-42)
            if (r7 >= r11) goto L_0x020a
            int r33 = r33 + 1
            r50 = r29
            goto L_0x0217
        L_0x0208:
            r11 = 999(0x3e7, float:1.4E-42)
        L_0x020a:
            r50 = r29
            goto L_0x0217
        L_0x020d:
            r35 = r7
            r7 = r51
            r4 = r52
            r11 = 999(0x3e7, float:1.4E-42)
            r50 = r35
        L_0x0217:
            r2 = 1
            r35 = r50
            goto L_0x0245
        L_0x021b:
            r16 = r7
            r35 = r50
            r7 = r51
            r4 = r52
            r11 = 999(0x3e7, float:1.4E-42)
            goto L_0x0230
        L_0x0226:
            r16 = r47
            r35 = r50
            r7 = r51
            r4 = r52
            r11 = 999(0x3e7, float:1.4E-42)
        L_0x0230:
            int r6 = r0.uid
            int r3 = r0.connectionGroup
            int r11 = r0.connectionImportance
            r16 = r3
            r35 = r11
            goto L_0x0245
        L_0x023b:
            r16 = r47
            r6 = r48
            r35 = r50
            r7 = r51
            r4 = r52
        L_0x0245:
            if (r2 != 0) goto L_0x0265
            if (r7 == r4) goto L_0x0265
            int r3 = r27 + 1
            r33 = 0
            if (r3 < r15) goto L_0x0260
            r27 = 0
            r51 = r4
            int r3 = r4 + 10
            r4 = 999(0x3e7, float:1.4E-42)
            if (r3 <= r4) goto L_0x025e
            r52 = 999(0x3e7, float:1.4E-42)
            r4 = r52
            goto L_0x0267
        L_0x025e:
            r4 = r3
            goto L_0x0267
        L_0x0260:
            r27 = r3
            r51 = r7
            goto L_0x0267
        L_0x0265:
            r51 = r7
        L_0x0267:
            int r3 = r51 + r33
            r0.setCurRawAdj(r3)
            int r3 = r51 + r33
            int r3 = r0.modifyRawOomAdj(r3)
            r0.curAdj = r3
            r40 = r1
            r52 = r4
            r44 = r5
            r48 = r6
            r47 = r16
            r50 = r35
            r7 = r56
            goto L_0x02ee
        L_0x0284:
            r3 = r2
            r11 = r45
            goto L_0x028f
        L_0x0288:
            r22 = r2
            goto L_0x028f
        L_0x028b:
            r29 = r7
            r7 = r56
        L_0x028f:
            r0.setCurRawAdj(r11)
            int r2 = r0.modifyRawOomAdj(r11)
            r0.curAdj = r2
            r40 = r1
            r46 = r3
            r52 = r4
            r44 = r5
            r48 = r6
            r45 = r11
            r47 = r16
            r51 = r29
            r50 = r35
            goto L_0x02ee
        L_0x02ab:
            r5 = r44
            r11 = r45
            r3 = r46
            r16 = r47
            r6 = r48
            r35 = r50
            r29 = r51
            r4 = r52
            r7 = r56
            r40 = r1
            goto L_0x02ee
        L_0x02c0:
            r7 = r0
            r53 = r1
            r0 = r2
            r30 = r28
            r54 = r41
            r5 = r44
            r11 = r45
            r3 = r46
            r6 = r48
            r35 = r50
            r29 = r51
            r4 = r52
            r32 = 0
            r28 = r9
            r9 = r16
            r16 = r47
            r46 = r3
            r52 = r4
            r44 = r5
            r48 = r6
            r45 = r11
            r47 = r16
            r51 = r29
            r50 = r35
        L_0x02ee:
            int r10 = r10 + -1
            r11 = r64
            r0 = r7
            r16 = r9
            r9 = r28
            r5 = r30
            r3 = r32
            r7 = r34
            r1 = r53
            r41 = r54
            r2 = 1001(0x3e9, float:1.403E-42)
            r4 = 20
            goto L_0x00ee
        L_0x0307:
            r53 = r1
            r32 = r3
            r34 = r7
            r30 = r28
            r54 = r41
            r5 = r44
            r11 = r45
            r3 = r46
            r6 = r48
            r35 = r50
            r29 = r51
            r4 = r52
            r7 = r0
            r28 = r9
            r9 = r16
            r0 = 0
            r10 = r0
        L_0x0326:
            if (r40 == 0) goto L_0x0415
            r2 = 10
            if (r10 >= r2) goto L_0x0415
            int r10 = r10 + 1
            r0 = 0
            r1 = 0
        L_0x0330:
            if (r1 >= r9) goto L_0x0364
            com.android.server.am.ProcessList r2 = r8.mProcessList
            java.util.ArrayList<com.android.server.am.ProcessRecord> r2 = r2.mLruProcesses
            java.lang.Object r2 = r2.get(r1)
            com.android.server.am.ProcessRecord r2 = (com.android.server.am.ProcessRecord) r2
            r16 = r0
            boolean r0 = r2.killedByAm
            if (r0 != 0) goto L_0x0358
            android.app.IApplicationThread r0 = r2.thread
            if (r0 == 0) goto L_0x0358
            boolean r0 = r2.containsCycle
            r56 = r7
            r7 = 1
            if (r0 != r7) goto L_0x035b
            int r0 = r2.adjSeq
            int r0 = r0 - r7
            r2.adjSeq = r0
            int r0 = r2.completedAdjSeq
            int r0 = r0 - r7
            r2.completedAdjSeq = r0
            goto L_0x035b
        L_0x0358:
            r56 = r7
            r7 = 1
        L_0x035b:
            int r1 = r1 + 1
            r0 = r16
            r7 = r56
            r2 = 10
            goto L_0x0330
        L_0x0364:
            r16 = r0
            r56 = r7
            r7 = 1
            r0 = 0
            r2 = r0
            r40 = r16
        L_0x036d:
            if (r2 >= r9) goto L_0x03f0
            com.android.server.am.ProcessList r0 = r8.mProcessList
            java.util.ArrayList<com.android.server.am.ProcessRecord> r0 = r0.mLruProcesses
            java.lang.Object r0 = r0.get(r2)
            r1 = r0
            com.android.server.am.ProcessRecord r1 = (com.android.server.am.ProcessRecord) r1
            boolean r0 = r1.killedByAm
            if (r0 != 0) goto L_0x03bc
            android.app.IApplicationThread r0 = r1.thread
            if (r0 == 0) goto L_0x03bc
            boolean r0 = r1.containsCycle
            if (r0 != r7) goto L_0x03bc
            int r16 = r1.getCurRawAdj()
            r36 = 1
            r39 = 1
            r0 = r63
            r41 = r1
            r42 = r2
            r43 = 10
            r2 = r16
            r46 = r3
            r3 = r12
            r52 = r4
            r4 = r36
            r44 = r5
            r48 = r6
            r5 = r13
            r16 = r10
            r51 = r29
            r10 = r34
            r50 = r35
            r29 = r56
            r34 = r11
            r11 = r7
            r7 = r39
            boolean r0 = r0.computeOomAdjLocked(r1, r2, r3, r4, r5, r7)
            if (r0 == 0) goto L_0x03d7
            r40 = 1
            goto L_0x03d7
        L_0x03bc:
            r41 = r1
            r42 = r2
            r46 = r3
            r52 = r4
            r44 = r5
            r48 = r6
            r16 = r10
            r51 = r29
            r10 = r34
            r50 = r35
            r29 = r56
            r43 = 10
            r34 = r11
            r11 = r7
        L_0x03d7:
            int r2 = r42 + 1
            r7 = r11
            r56 = r29
            r11 = r34
            r5 = r44
            r3 = r46
            r6 = r48
            r35 = r50
            r29 = r51
            r4 = r52
            r34 = r10
            r10 = r16
            goto L_0x036d
        L_0x03f0:
            r42 = r2
            r46 = r3
            r52 = r4
            r44 = r5
            r48 = r6
            r16 = r10
            r51 = r29
            r10 = r34
            r50 = r35
            r29 = r56
            r43 = 10
            r34 = r11
            r11 = r7
            r7 = r29
            r11 = r34
            r29 = r51
            r34 = r10
            r10 = r16
            goto L_0x0326
        L_0x0415:
            r46 = r3
            r52 = r4
            r44 = r5
            r48 = r6
            r16 = r10
            r51 = r29
            r10 = r34
            r50 = r35
            r29 = r7
            r34 = r11
            r11 = 1
            r0 = r32
            r1 = r32
            int r7 = r9 + -1
            r2 = r0
            r4 = r1
            r3 = r23
            r5 = r25
            r6 = r26
        L_0x0438:
            if (r7 < 0) goto L_0x05bb
            com.android.server.am.ProcessList r0 = r8.mProcessList
            java.util.ArrayList<com.android.server.am.ProcessRecord> r0 = r0.mLruProcesses
            java.lang.Object r0 = r0.get(r7)
            r1 = r0
            com.android.server.am.ProcessRecord r1 = (com.android.server.am.ProcessRecord) r1
            boolean r0 = r1.killedByAm
            if (r0 != 0) goto L_0x059a
            android.app.IApplicationThread r0 = r1.thread
            if (r0 == 0) goto L_0x059a
            r23 = 1
            r0 = r63
            r25 = r1
            r57 = r2
            r2 = r23
            r58 = r3
            r59 = r4
            r3 = r13
            r60 = r5
            r61 = r6
            r5 = r30
            r0.applyOomAdjLocked(r1, r2, r3, r5)
            com.android.server.am.ProcessPolicyManager.promoteImportantProcState(r25)
            int r0 = r25.getCurProcState()
            r1 = 17
            if (r0 == r1) goto L_0x04df
            r1 = 18
            if (r0 == r1) goto L_0x04df
            r1 = 20
            if (r0 == r1) goto L_0x0482
            int r0 = r8.mNumNonCachedProcs
            int r0 = r0 + r11
            r8.mNumNonCachedProcs = r0
            r0 = r25
            r2 = r60
            goto L_0x04b9
        L_0x0482:
            com.android.server.am.ActivityManagerConstants r0 = r8.mConstants
            int r0 = r0.CUR_TRIM_EMPTY_PROCESSES
            r2 = r60
            if (r2 <= r0) goto L_0x04bf
            r0 = r25
            long r3 = r0.lastActivityTime
            int r3 = (r3 > r17 ? 1 : (r3 == r17 ? 0 : -1))
            if (r3 >= 0) goto L_0x04c1
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "empty for "
            r3.append(r4)
            r4 = 1800000(0x1b7740, double:8.89318E-318)
            long r20 = r17 + r4
            long r4 = r0.lastActivityTime
            long r20 = r20 - r4
            r4 = 1000(0x3e8, double:4.94E-321)
            long r4 = r20 / r4
            r3.append(r4)
            java.lang.String r4 = "s"
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            r0.kill(r3, r11)
        L_0x04b9:
            r11 = r53
            r3 = r54
            goto L_0x053d
        L_0x04bf:
            r0 = r25
        L_0x04c1:
            int r5 = r2 + 1
            r3 = r54
            if (r5 <= r3) goto L_0x04db
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "empty #"
            r2.append(r4)
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            r0.kill(r2, r11)
        L_0x04db:
            r2 = r5
            r11 = r53
            goto L_0x053d
        L_0x04df:
            r0 = r25
            r3 = r54
            r2 = r60
            r1 = 20
            int r4 = r8.mNumCachedHiddenProcs
            int r4 = r4 + r11
            r8.mNumCachedHiddenProcs = r4
            r4 = r58
            int r4 = r4 + 1
            int r5 = r0.connectionGroup
            if (r5 == 0) goto L_0x050f
            android.content.pm.ApplicationInfo r5 = r0.info
            int r5 = r5.uid
            r6 = r57
            if (r6 != r5) goto L_0x0506
            int r5 = r0.connectionGroup
            r1 = r59
            if (r1 != r5) goto L_0x0508
            int r24 = r24 + 1
            r5 = r6
            goto L_0x0517
        L_0x0506:
            r1 = r59
        L_0x0508:
            android.content.pm.ApplicationInfo r5 = r0.info
            int r5 = r5.uid
            int r1 = r0.connectionGroup
            goto L_0x0517
        L_0x050f:
            r6 = r57
            r1 = r59
            r1 = r32
            r5 = r32
        L_0x0517:
            int r6 = r4 - r24
            r11 = r53
            if (r6 <= r11) goto L_0x0535
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r20 = r1
            java.lang.String r1 = "cached #"
            r6.append(r1)
            r6.append(r4)
            java.lang.String r1 = r6.toString()
            r6 = 1
            r0.kill(r1, r6)
            goto L_0x0537
        L_0x0535:
            r20 = r1
        L_0x0537:
            r58 = r4
            r57 = r5
            r59 = r20
        L_0x053d:
            boolean r1 = r0.isolated
            if (r1 == 0) goto L_0x0555
            android.util.ArraySet<com.android.server.am.ServiceRecord> r1 = r0.f3services
            int r1 = r1.size()
            if (r1 > 0) goto L_0x0555
            java.lang.String r1 = r0.isolatedEntryPoint
            if (r1 != 0) goto L_0x0555
            java.lang.String r1 = "isolated not needed"
            r4 = 1
            r0.kill(r1, r4)
            goto L_0x057b
        L_0x0555:
            com.android.server.am.UidRecord r1 = r0.uidRecord
            if (r1 == 0) goto L_0x057b
            android.content.pm.ApplicationInfo r4 = r0.info
            boolean r4 = r4.isInstantApp()
            r1.ephemeral = r4
            int r4 = r1.getCurProcState()
            int r5 = r0.getCurProcState()
            if (r4 <= r5) goto L_0x0572
            int r4 = r0.getCurProcState()
            r1.setCurProcState(r4)
        L_0x0572:
            boolean r4 = r0.hasForegroundServices()
            if (r4 == 0) goto L_0x057b
            r4 = 1
            r1.foregroundServices = r4
        L_0x057b:
            int r1 = r0.getCurProcState()
            r4 = 15
            if (r1 < r4) goto L_0x0591
            boolean r1 = r0.killedByAm
            if (r1 != 0) goto L_0x0591
            r5 = r61
            int r6 = r5 + 1
            r5 = r2
            r2 = r57
            r4 = r59
            goto L_0x05b0
        L_0x0591:
            r5 = r61
            r6 = r5
            r4 = r59
            r5 = r2
            r2 = r57
            goto L_0x05b0
        L_0x059a:
            r0 = r1
            r1 = r4
            r11 = r53
            r4 = r3
            r3 = r54
            r62 = r6
            r6 = r2
            r2 = r5
            r5 = r62
            r58 = r4
            r4 = r1
            r62 = r5
            r5 = r2
            r2 = r6
            r6 = r62
        L_0x05b0:
            int r7 = r7 + -1
            r54 = r3
            r53 = r11
            r3 = r58
            r11 = 1
            goto L_0x0438
        L_0x05bb:
            r1 = r4
            r11 = r53
            r4 = r3
            r3 = r54
            r62 = r6
            r6 = r2
            r2 = r5
            r5 = r62
            int r0 = r8.mBServiceAppThreshold
            if (r10 <= r0) goto L_0x05ef
            com.android.server.am.ActivityManagerService r0 = r8.mService
            boolean r0 = r0.mAllowLowerMemLevel
            r7 = 1
            if (r7 != r0) goto L_0x05ef
            r0 = r49
            if (r0 == 0) goto L_0x05ea
            int r7 = r0.pid
            r59 = r1
            android.content.pm.ApplicationInfo r1 = r0.info
            int r1 = r1.uid
            r54 = r3
            r3 = 999(0x3e7, float:1.4E-42)
            com.android.server.am.ProcessList.setOomAdj(r7, r1, r3)
            int r1 = r0.curAdj
            r0.setAdj = r1
            goto L_0x05f5
        L_0x05ea:
            r59 = r1
            r54 = r3
            goto L_0x05f5
        L_0x05ef:
            r59 = r1
            r54 = r3
            r0 = r49
        L_0x05f5:
            com.android.server.am.ActivityManagerService r1 = r8.mService
            r1.incrementProcStateSeqAndNotifyAppsLocked()
            int r1 = r8.mNewNumServiceProcs
            r8.mNumServiceProcs = r1
            com.android.server.am.ActivityManagerService r1 = r8.mService
            boolean r1 = r1.updateLowMemStateLocked(r4, r2, r5)
            com.android.server.am.ActivityManagerService r3 = r8.mService
            boolean r3 = r3.mAlwaysFinishActivities
            if (r3 == 0) goto L_0x0613
            com.android.server.am.ActivityManagerService r3 = r8.mService
            com.android.server.wm.ActivityTaskManagerInternal r3 = r3.mAtmInternal
            java.lang.String r7 = "always-finish"
            r3.scheduleDestroyAllActivities(r7)
        L_0x0613:
            if (r1 == 0) goto L_0x0625
            com.android.server.am.ActivityManagerService r3 = r8.mService
            com.android.server.am.ProcessStatsService r7 = r3.mProcessStats
            boolean r7 = r7.isMemFactorLowered()
            r20 = r0
            r0 = r32
            r3.requestPssAllProcsLocked(r13, r0, r7)
            goto L_0x0629
        L_0x0625:
            r20 = r0
            r0 = r32
        L_0x0629:
            r3 = 0
            android.os.PowerManagerInternal r7 = r8.mLocalPowerManager
            if (r7 == 0) goto L_0x0631
            r7.startUidChanges()
        L_0x0631:
            com.android.server.am.ActiveUids r7 = r8.mActiveUids
            int r7 = r7.size()
            r21 = 1
            int r7 = r7 + -1
        L_0x063b:
            if (r7 < 0) goto L_0x0754
            com.android.server.am.ActiveUids r0 = r8.mActiveUids
            com.android.server.am.UidRecord r0 = r0.valueAt(r7)
            r21 = 0
            r23 = r1
            int r1 = r0.getCurProcState()
            r60 = r2
            r2 = 21
            if (r1 == r2) goto L_0x0743
            int r1 = r0.setProcState
            int r2 = r0.getCurProcState()
            if (r1 != r2) goto L_0x0666
            boolean r1 = r0.setWhitelist
            boolean r2 = r0.curWhitelist
            if (r1 == r2) goto L_0x0660
            goto L_0x0666
        L_0x0660:
            r58 = r4
            r61 = r5
            goto L_0x0747
        L_0x0666:
            int r1 = r0.getCurProcState()
            boolean r1 = android.app.ActivityManager.isProcStateBackground(r1)
            if (r1 == 0) goto L_0x06c8
            boolean r1 = r0.curWhitelist
            if (r1 != 0) goto L_0x06c8
            int r1 = r0.setProcState
            boolean r1 = android.app.ActivityManager.isProcStateBackground(r1)
            if (r1 == 0) goto L_0x0686
            boolean r1 = r0.setWhitelist
            if (r1 == 0) goto L_0x0681
            goto L_0x0686
        L_0x0681:
            r58 = r4
            r61 = r5
            goto L_0x06ab
        L_0x0686:
            r1 = r30
            r0.lastBackgroundTime = r1
            com.android.server.am.ActivityManagerService r1 = r8.mService
            com.android.server.am.ActivityManagerService$MainHandler r1 = r1.mHandler
            r2 = 58
            boolean r1 = r1.hasMessages(r2)
            if (r1 != 0) goto L_0x06a7
            com.android.server.am.ActivityManagerService r1 = r8.mService
            com.android.server.am.ActivityManagerService$MainHandler r1 = r1.mHandler
            r58 = r4
            com.android.server.am.ActivityManagerConstants r4 = r8.mConstants
            r61 = r5
            long r4 = r4.BACKGROUND_SETTLE_TIME
            r1.sendEmptyMessageDelayed(r2, r4)
            goto L_0x06ab
        L_0x06a7:
            r58 = r4
            r61 = r5
        L_0x06ab:
            boolean r1 = r0.idle
            if (r1 == 0) goto L_0x06c4
            boolean r1 = r0.setIdle
            if (r1 != 0) goto L_0x06c4
            r21 = 2
            if (r3 != 0) goto L_0x06bd
            java.util.ArrayList r1 = new java.util.ArrayList
            r1.<init>()
            r3 = r1
        L_0x06bd:
            r3.add(r0)
            r1 = 0
            r4 = 0
            goto L_0x06e0
        L_0x06c4:
            r1 = 0
            r4 = 0
            goto L_0x06e0
        L_0x06c8:
            r58 = r4
            r61 = r5
            boolean r1 = r0.idle
            if (r1 == 0) goto L_0x06db
            r21 = 4
            int r1 = r0.uid
            com.android.server.am.EventLogTags.writeAmUidActive(r1)
            r1 = 0
            r0.idle = r1
            goto L_0x06dc
        L_0x06db:
            r1 = 0
        L_0x06dc:
            r4 = 0
            r0.lastBackgroundTime = r4
        L_0x06e0:
            int r2 = r0.setProcState
            r1 = 12
            if (r2 <= r1) goto L_0x06e8
            r2 = 1
            goto L_0x06e9
        L_0x06e8:
            r2 = 0
        L_0x06e9:
            int r4 = r0.getCurProcState()
            if (r4 <= r1) goto L_0x06f1
            r1 = 1
            goto L_0x06f2
        L_0x06f1:
            r1 = 0
        L_0x06f2:
            if (r2 != r1) goto L_0x06fe
            int r4 = r0.setProcState
            r5 = 21
            if (r4 != r5) goto L_0x06fb
            goto L_0x06fe
        L_0x06fb:
            r4 = r21
            goto L_0x0709
        L_0x06fe:
            if (r1 == 0) goto L_0x0703
            r4 = 8
            goto L_0x0705
        L_0x0703:
            r4 = 16
        L_0x0705:
            r21 = r21 | r4
            r4 = r21
        L_0x0709:
            int r5 = r0.getCurProcState()
            r0.setProcState = r5
            boolean r5 = r0.curWhitelist
            r0.setWhitelist = r5
            boolean r5 = r0.idle
            r0.setIdle = r5
            com.android.server.am.ActivityManagerService r5 = r8.mService
            com.android.server.wm.ActivityTaskManagerInternal r5 = r5.mAtmInternal
            r21 = r1
            int r1 = r0.uid
            r25 = r2
            int r2 = r0.setProcState
            r5.onUidProcStateChanged(r1, r2)
            com.android.server.am.ActivityManagerService r1 = r8.mService
            r2 = -1
            r1.enqueueUidChangeLocked(r0, r2, r4)
            com.android.server.am.ActivityManagerService r1 = r8.mService
            int r2 = r0.uid
            int r5 = r0.getCurProcState()
            r1.noteUidProcessState(r2, r5)
            boolean r1 = r0.foregroundServices
            if (r1 == 0) goto L_0x0747
            com.android.server.am.ActivityManagerService r1 = r8.mService
            com.android.server.am.ActiveServices r1 = r1.mServices
            r1.foregroundServiceProcStateChangedLocked(r0)
            goto L_0x0747
        L_0x0743:
            r58 = r4
            r61 = r5
        L_0x0747:
            int r7 = r7 + -1
            r1 = r23
            r4 = r58
            r2 = r60
            r5 = r61
            r0 = 0
            goto L_0x063b
        L_0x0754:
            r23 = r1
            r60 = r2
            r58 = r4
            r61 = r5
            android.os.PowerManagerInternal r0 = r8.mLocalPowerManager
            if (r0 == 0) goto L_0x0763
            r0.finishUidChanges()
        L_0x0763:
            if (r3 == 0) goto L_0x077f
            int r0 = r3.size()
            r1 = 1
            int r0 = r0 - r1
        L_0x076b:
            if (r0 < 0) goto L_0x077f
            com.android.server.am.ActivityManagerService r1 = r8.mService
            com.android.server.am.ActiveServices r1 = r1.mServices
            java.lang.Object r2 = r3.get(r0)
            com.android.server.am.UidRecord r2 = (com.android.server.am.UidRecord) r2
            int r2 = r2.uid
            r1.stopInBackgroundLocked(r2)
            int r0 = r0 + -1
            goto L_0x076b
        L_0x077f:
            com.android.server.am.ActivityManagerService r0 = r8.mService
            com.android.server.am.ProcessStatsService r0 = r0.mProcessStats
            boolean r0 = r0.shouldWriteNowLocked(r13)
            if (r0 == 0) goto L_0x0799
            com.android.server.am.ActivityManagerService r0 = r8.mService
            com.android.server.am.ActivityManagerService$MainHandler r0 = r0.mHandler
            com.android.server.am.ActivityManagerService$ProcStatsRunnable r1 = new com.android.server.am.ActivityManagerService$ProcStatsRunnable
            com.android.server.am.ActivityManagerService r2 = r8.mService
            com.android.server.am.ProcessStatsService r4 = r2.mProcessStats
            r1.<init>(r2, r4)
            r0.post(r1)
        L_0x0799:
            com.android.server.am.ActivityManagerService r0 = r8.mService
            com.android.server.am.ProcessStatsService r0 = r0.mProcessStats
            int r1 = r8.mAdjSeq
            r0.updateTrackingAssociationsLocked(r1, r13)
            com.android.server.am.ActivityManagerService r0 = r8.mService
            com.android.server.am.OomAdjProfiler r0 = r0.mOomAdjProfiler
            r0.oomAdjEnded()
            r0 = 64
            android.os.Trace.traceEnd(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.OomAdjuster.updateOomAdjLocked(java.lang.String):void");
    }

    private final class ComputeOomAdjWindowCallback implements WindowProcessController.ComputeOomAdjCallback {
        int adj;
        ProcessRecord app;
        int appUid;
        boolean foregroundActivities;
        int logUid;
        int procState;
        int processStateCurTop;
        int schedGroup;

        private ComputeOomAdjWindowCallback() {
        }

        /* access modifiers changed from: package-private */
        public void initialize(ProcessRecord app2, int adj2, boolean foregroundActivities2, int procState2, int schedGroup2, int appUid2, int logUid2, int processStateCurTop2) {
            this.app = app2;
            this.adj = adj2;
            this.foregroundActivities = foregroundActivities2;
            this.procState = procState2;
            this.schedGroup = schedGroup2;
            this.appUid = appUid2;
            this.logUid = logUid2;
            this.processStateCurTop = processStateCurTop2;
        }

        public void onVisibleActivity() {
            if (this.adj > 100) {
                this.adj = 100;
                this.app.adjType = "vis-activity";
                if (this.logUid == this.appUid) {
                    OomAdjuster oomAdjuster = OomAdjuster.this;
                    oomAdjuster.reportOomAdjMessageLocked("ActivityManager", "Raise adj to vis-activity: " + this.app);
                }
            }
            int i = this.procState;
            int i2 = this.processStateCurTop;
            if (i > i2) {
                this.procState = i2;
                this.app.adjType = "vis-activity";
                if (this.logUid == this.appUid) {
                    OomAdjuster oomAdjuster2 = OomAdjuster.this;
                    oomAdjuster2.reportOomAdjMessageLocked("ActivityManager", "Raise procstate to vis-activity (top): " + this.app);
                }
            }
            if (this.schedGroup < 2) {
                this.schedGroup = 2;
            }
            ProcessRecord processRecord = this.app;
            processRecord.cached = false;
            processRecord.empty = false;
            this.foregroundActivities = true;
        }

        public void onPausedActivity() {
            if (this.adj > 200) {
                this.adj = 200;
                this.app.adjType = "pause-activity";
                if (this.logUid == this.appUid) {
                    OomAdjuster oomAdjuster = OomAdjuster.this;
                    oomAdjuster.reportOomAdjMessageLocked("ActivityManager", "Raise adj to pause-activity: " + this.app);
                }
            }
            int i = this.procState;
            int i2 = this.processStateCurTop;
            if (i > i2) {
                this.procState = i2;
                this.app.adjType = "pause-activity";
                if (this.logUid == this.appUid) {
                    OomAdjuster oomAdjuster2 = OomAdjuster.this;
                    oomAdjuster2.reportOomAdjMessageLocked("ActivityManager", "Raise procstate to pause-activity (top): " + this.app);
                }
            }
            if (this.schedGroup < 2) {
                this.schedGroup = 2;
            }
            ProcessRecord processRecord = this.app;
            processRecord.cached = false;
            processRecord.empty = false;
            this.foregroundActivities = true;
        }

        public void onStoppingActivity(boolean finishing) {
            if (this.adj > 200) {
                this.adj = 200;
                this.app.adjType = "stop-activity";
                if (this.logUid == this.appUid) {
                    OomAdjuster oomAdjuster = OomAdjuster.this;
                    oomAdjuster.reportOomAdjMessageLocked("ActivityManager", "Raise adj to stop-activity: " + this.app);
                }
            }
            if (!finishing && this.procState > 16) {
                this.procState = 16;
                this.app.adjType = "stop-activity";
                if (this.logUid == this.appUid) {
                    OomAdjuster oomAdjuster2 = OomAdjuster.this;
                    oomAdjuster2.reportOomAdjMessageLocked("ActivityManager", "Raise procstate to stop-activity: " + this.app);
                }
            }
            ProcessRecord processRecord = this.app;
            processRecord.cached = false;
            processRecord.empty = false;
            this.foregroundActivities = true;
        }

        public void onOtherActivity() {
            if (this.procState > 17) {
                this.procState = 17;
                this.app.adjType = "cch-act";
                if (this.logUid == this.appUid) {
                    OomAdjuster oomAdjuster = OomAdjuster.this;
                    oomAdjuster.reportOomAdjMessageLocked("ActivityManager", "Raise procstate to cached activity: " + this.app);
                }
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:111:0x02df, code lost:
        if (r6 > 3) goto L_0x02e3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:134:0x0365, code lost:
        if (r7.setProcState <= 2) goto L_0x0369;
     */
    /* JADX WARNING: Removed duplicated region for block: B:343:0x07dc  */
    /* JADX WARNING: Removed duplicated region for block: B:376:0x0841  */
    /* JADX WARNING: Removed duplicated region for block: B:388:0x0864  */
    /* JADX WARNING: Removed duplicated region for block: B:389:0x0868  */
    /* JADX WARNING: Removed duplicated region for block: B:391:0x086c  */
    /* JADX WARNING: Removed duplicated region for block: B:393:0x0873  */
    /* JADX WARNING: Removed duplicated region for block: B:402:0x088d  */
    /* JADX WARNING: Removed duplicated region for block: B:406:0x08e8  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final boolean computeOomAdjLocked(com.android.server.am.ProcessRecord r42, int r43, com.android.server.am.ProcessRecord r44, boolean r45, long r46, boolean r48) {
        /*
            r41 = this;
            r8 = r41
            r7 = r42
            r5 = r44
            r3 = r46
            int r0 = r8.mAdjSeq
            int r1 = r7.adjSeq
            r6 = 1
            r2 = 0
            if (r0 != r1) goto L_0x001a
            int r0 = r7.adjSeq
            int r1 = r7.completedAdjSeq
            if (r0 != r1) goto L_0x0017
            return r2
        L_0x0017:
            r7.containsCycle = r6
            return r2
        L_0x001a:
            android.app.IApplicationThread r0 = r7.thread
            if (r0 != 0) goto L_0x0036
            int r0 = r8.mAdjSeq
            r7.adjSeq = r0
            r7.setCurrentSchedulingGroup(r2)
            r0 = 20
            r7.setCurProcState(r0)
            r0 = 999(0x3e7, float:1.4E-42)
            r7.curAdj = r0
            r7.setCurRawAdj(r0)
            int r0 = r7.adjSeq
            r7.completedAdjSeq = r0
            return r2
        L_0x0036:
            r7.adjTypeCode = r2
            r0 = 0
            r7.adjSource = r0
            r7.adjTarget = r0
            r7.empty = r2
            r7.cached = r2
            com.android.server.wm.WindowProcessController r1 = r42.getWindowProcessController()
            android.content.pm.ApplicationInfo r0 = r7.info
            int r0 = r0.uid
            com.android.server.am.ActivityManagerService r9 = r8.mService
            int r15 = r9.mCurOomAdjUid
            int r14 = r7.curAdj
            int r13 = r42.getCurProcState()
            int r9 = r7.maxAdj
            r10 = 2
            java.lang.String r12 = "ActivityManager"
            if (r9 > 0) goto L_0x00f7
            if (r15 != r0) goto L_0x0072
            com.android.server.am.ActivityManagerService r9 = r8.mService
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r6 = "Making fixed: "
            r11.append(r6)
            r11.append(r7)
            java.lang.String r6 = r11.toString()
            r9.reportOomAdjMessageLocked(r12, r6)
        L_0x0072:
            java.lang.String r6 = "fixed"
            r7.adjType = r6
            int r6 = r8.mAdjSeq
            r7.adjSeq = r6
            int r6 = r7.maxAdj
            r7.setCurRawAdj(r6)
            r7.setHasForegroundActivities(r2)
            r7.setCurrentSchedulingGroup(r10)
            r7.setCurProcState(r2)
            r6 = 1
            r7.systemNoUi = r6
            if (r7 != r5) goto L_0x0099
            r7.systemNoUi = r2
            r6 = 3
            r7.setCurrentSchedulingGroup(r6)
            java.lang.String r6 = "pers-top-activity"
            r7.adjType = r6
            goto L_0x00af
        L_0x0099:
            boolean r6 = r42.hasTopUi()
            if (r6 == 0) goto L_0x00a7
            r7.systemNoUi = r2
            java.lang.String r6 = "pers-top-ui"
            r7.adjType = r6
            goto L_0x00af
        L_0x00a7:
            boolean r6 = r1.hasVisibleActivities()
            if (r6 == 0) goto L_0x00af
            r7.systemNoUi = r2
        L_0x00af:
            int r6 = r7.pid
            int r9 = com.android.server.am.ActivityManagerService.MY_PID
            if (r6 != r9) goto L_0x00bd
            boolean r6 = com.android.server.am.ActivityManagerServiceInjector.enableTaskIsolation
            if (r6 == 0) goto L_0x00bd
            r6 = 3
            r7.setCurrentSchedulingGroup(r6)
        L_0x00bd:
            boolean r6 = r7.systemNoUi
            if (r6 != 0) goto L_0x00d7
            com.android.server.am.ActivityManagerService r6 = r8.mService
            int r6 = r6.mWakefulness
            r9 = 1
            if (r6 != r9) goto L_0x00d0
            r7.setCurProcState(r9)
            r6 = 3
            r7.setCurrentSchedulingGroup(r6)
            goto L_0x00d7
        L_0x00d0:
            r11 = 6
            r7.setCurProcState(r11)
            r7.setCurrentSchedulingGroup(r9)
        L_0x00d7:
            int r6 = r42.getCurProcState()
            r7.setCurRawProcState(r6)
            int r6 = r7.maxAdj
            r7.curAdj = r6
            int r6 = r7.adjSeq
            r7.completedAdjSeq = r6
            int r6 = r7.curAdj
            if (r6 < r14) goto L_0x00f4
            int r6 = r42.getCurProcState()
            if (r6 >= r13) goto L_0x00f1
            goto L_0x00f4
        L_0x00f1:
            r18 = r2
            goto L_0x00f6
        L_0x00f4:
            r18 = 1
        L_0x00f6:
            return r18
        L_0x00f7:
            r6 = 3
            r11 = 6
            r7.systemNoUi = r2
            com.android.server.am.ActivityManagerService r9 = r8.mService
            com.android.server.wm.ActivityTaskManagerInternal r9 = r9.mAtmInternal
            int r9 = r9.getTopProcessState()
            r16 = 0
            android.util.ArraySet<com.android.server.am.BroadcastQueue> r6 = r8.mTmpBroadcastQueue
            r6.clear()
            if (r9 != r10) goto L_0x0138
            if (r7 != r5) goto L_0x0138
            r6 = 0
            r19 = 3
            java.lang.String r10 = "top-activity"
            r7.adjType = r10
            r10 = 1
            r16 = r9
            if (r15 != r0) goto L_0x012f
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r2 = "Making top: "
            r11.append(r2)
            r11.append(r7)
            java.lang.String r2 = r11.toString()
            r8.reportOomAdjMessageLocked(r12, r2)
        L_0x012f:
            r2 = r10
            r5 = r19
            r19 = r6
            r6 = r16
            goto L_0x0253
        L_0x0138:
            boolean r2 = r7.runningRemoteAnimation
            if (r2 == 0) goto L_0x0166
            r2 = 100
            r6 = 3
            java.lang.String r10 = "running-remote-anim"
            r7.adjType = r10
            r10 = r9
            if (r15 != r0) goto L_0x015e
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r19 = r2
            java.lang.String r2 = "Making running remote anim: "
            r11.append(r2)
            r11.append(r7)
            java.lang.String r2 = r11.toString()
            r8.reportOomAdjMessageLocked(r12, r2)
            goto L_0x0160
        L_0x015e:
            r19 = r2
        L_0x0160:
            r5 = r6
            r6 = r10
            r2 = r16
            goto L_0x0253
        L_0x0166:
            com.android.server.am.ActiveInstrumentation r2 = r42.getActiveInstrumentation()
            if (r2 == 0) goto L_0x0190
            r2 = 0
            r6 = 2
            java.lang.String r10 = "instrumentation"
            r7.adjType = r10
            r10 = 5
            if (r15 != r0) goto L_0x018d
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r19 = r2
            java.lang.String r2 = "Making instrumentation: "
            r11.append(r2)
            r11.append(r7)
            java.lang.String r2 = r11.toString()
            r8.reportOomAdjMessageLocked(r12, r2)
            goto L_0x0160
        L_0x018d:
            r19 = r2
            goto L_0x0160
        L_0x0190:
            com.android.server.am.ActivityManagerService r2 = r8.mService
            android.util.ArraySet<com.android.server.am.BroadcastQueue> r6 = r8.mTmpBroadcastQueue
            boolean r2 = r2.isReceivingBroadcastLocked(r7, r6)
            if (r2 == 0) goto L_0x01cc
            r2 = 0
            android.util.ArraySet<com.android.server.am.BroadcastQueue> r6 = r8.mTmpBroadcastQueue
            com.android.server.am.ActivityManagerService r10 = r8.mService
            com.android.server.am.BroadcastQueue r10 = r10.mFgBroadcastQueue
            boolean r6 = r6.contains(r10)
            if (r6 == 0) goto L_0x01a9
            r6 = 2
            goto L_0x01aa
        L_0x01a9:
            r6 = 0
        L_0x01aa:
            java.lang.String r10 = "broadcast"
            r7.adjType = r10
            r10 = 12
            if (r15 != r0) goto L_0x01c9
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r19 = r2
            java.lang.String r2 = "Making broadcast: "
            r11.append(r2)
            r11.append(r7)
            java.lang.String r2 = r11.toString()
            r8.reportOomAdjMessageLocked(r12, r2)
            goto L_0x0160
        L_0x01c9:
            r19 = r2
            goto L_0x0160
        L_0x01cc:
            android.util.ArraySet<com.android.server.am.ServiceRecord> r2 = r7.executingServices
            int r2 = r2.size()
            if (r2 <= 0) goto L_0x0200
            r2 = 0
            boolean r6 = r7.execServicesFg
            if (r6 == 0) goto L_0x01db
            r6 = 2
            goto L_0x01dc
        L_0x01db:
            r6 = 0
        L_0x01dc:
            java.lang.String r10 = "exec-service"
            r7.adjType = r10
            r10 = 11
            if (r15 != r0) goto L_0x01fc
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r19 = r2
            java.lang.String r2 = "Making exec-service: "
            r11.append(r2)
            r11.append(r7)
            java.lang.String r2 = r11.toString()
            r8.reportOomAdjMessageLocked(r12, r2)
            goto L_0x0160
        L_0x01fc:
            r19 = r2
            goto L_0x0160
        L_0x0200:
            if (r7 != r5) goto L_0x022a
            r2 = 0
            r6 = 0
            java.lang.String r10 = "top-sleeping"
            r7.adjType = r10
            r10 = 1
            r11 = r9
            if (r15 != r0) goto L_0x0224
            r19 = r2
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r5 = "Making top (sleeping): "
            r2.append(r5)
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            r8.reportOomAdjMessageLocked(r12, r2)
            goto L_0x0226
        L_0x0224:
            r19 = r2
        L_0x0226:
            r5 = r6
            r2 = r10
            r6 = r11
            goto L_0x0253
        L_0x022a:
            r2 = 0
            r5 = r43
            r6 = 20
            r10 = 1
            r7.cached = r10
            r7.empty = r10
            java.lang.String r10 = "cch-empty"
            r7.adjType = r10
            if (r15 != r0) goto L_0x024e
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "Making empty: "
            r10.append(r11)
            r10.append(r7)
            java.lang.String r10 = r10.toString()
            r8.reportOomAdjMessageLocked(r12, r10)
        L_0x024e:
            r19 = r5
            r5 = r2
            r2 = r16
        L_0x0253:
            r11 = 100
            if (r2 != 0) goto L_0x029e
            boolean r10 = r1.hasActivities()
            if (r10 == 0) goto L_0x029e
            com.android.server.am.OomAdjuster$ComputeOomAdjWindowCallback r10 = r8.mTmpComputeOomAdjWindowCallback
            r23 = r9
            r9 = r10
            r10 = r42
            r3 = r11
            r4 = 3
            r16 = 6
            r11 = r19
            r4 = r12
            r12 = r2
            r24 = r13
            r13 = r6
            r25 = r14
            r14 = r5
            r21 = r15
            r15 = r0
            r16 = r21
            r17 = r23
            r9.initialize(r10, r11, r12, r13, r14, r15, r16, r17)
            r9 = 99
            com.android.server.am.OomAdjuster$ComputeOomAdjWindowCallback r10 = r8.mTmpComputeOomAdjWindowCallback
            int r9 = r1.computeOomAdjFromActivities(r9, r10)
            com.android.server.am.OomAdjuster$ComputeOomAdjWindowCallback r10 = r8.mTmpComputeOomAdjWindowCallback
            int r10 = r10.adj
            com.android.server.am.OomAdjuster$ComputeOomAdjWindowCallback r11 = r8.mTmpComputeOomAdjWindowCallback
            boolean r2 = r11.foregroundActivities
            com.android.server.am.OomAdjuster$ComputeOomAdjWindowCallback r11 = r8.mTmpComputeOomAdjWindowCallback
            int r6 = r11.procState
            com.android.server.am.OomAdjuster$ComputeOomAdjWindowCallback r11 = r8.mTmpComputeOomAdjWindowCallback
            int r5 = r11.schedGroup
            if (r10 != r3) goto L_0x029c
            int r19 = r10 + r9
            r9 = r2
            r10 = r19
            goto L_0x02ab
        L_0x029c:
            r9 = r2
            goto L_0x02ab
        L_0x029e:
            r23 = r9
            r3 = r11
            r4 = r12
            r24 = r13
            r25 = r14
            r21 = r15
            r9 = r2
            r10 = r19
        L_0x02ab:
            r2 = 19
            if (r6 <= r2) goto L_0x02d4
            boolean r2 = r42.hasRecentTasks()
            if (r2 == 0) goto L_0x02d4
            r6 = 19
            java.lang.String r2 = "cch-rec"
            r7.adjType = r2
            r11 = r21
            if (r11 != r0) goto L_0x02d6
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r12 = "Raise procstate to cached recent: "
            r2.append(r12)
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            r8.reportOomAdjMessageLocked(r4, r2)
            goto L_0x02d6
        L_0x02d4:
            r11 = r21
        L_0x02d6:
            java.lang.String r12 = ": "
            java.lang.String r13 = "Raise to "
            r14 = 200(0xc8, float:2.8E-43)
            if (r10 > r14) goto L_0x02e2
            r15 = 3
            if (r6 <= r15) goto L_0x034b
            goto L_0x02e3
        L_0x02e2:
            r15 = 3
        L_0x02e3:
            boolean r2 = r42.hasForegroundServices()
            if (r2 == 0) goto L_0x0324
            r10 = 200(0xc8, float:2.8E-43)
            boolean r2 = r42.hasLocationForegroundServices()
            if (r2 == 0) goto L_0x02f8
            r2 = 3
            java.lang.String r6 = "fg-service-location"
            r7.adjType = r6
            r6 = r2
            goto L_0x02fe
        L_0x02f8:
            r2 = 5
            java.lang.String r6 = "fg-service"
            r7.adjType = r6
            r6 = r2
        L_0x02fe:
            r2 = 0
            r7.cached = r2
            r5 = 2
            if (r11 != r0) goto L_0x034b
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r13)
            java.lang.String r3 = r7.adjType
            r2.append(r3)
            r2.append(r12)
            r2.append(r7)
            java.lang.String r3 = " "
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r8.reportOomAdjMessageLocked(r4, r2)
            goto L_0x034b
        L_0x0324:
            boolean r2 = r42.hasOverlayUi()
            if (r2 == 0) goto L_0x034b
            r10 = 200(0xc8, float:2.8E-43)
            r6 = 7
            r2 = 0
            r7.cached = r2
            java.lang.String r2 = "has-overlay-ui"
            r7.adjType = r2
            r5 = 2
            if (r11 != r0) goto L_0x034b
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Raise to overlay ui: "
            r2.append(r3)
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            r8.reportOomAdjMessageLocked(r4, r2)
        L_0x034b:
            boolean r2 = r42.hasForegroundServices()
            if (r2 == 0) goto L_0x0386
            r2 = 50
            if (r10 <= r2) goto L_0x0386
            long r2 = r7.lastTopTime
            com.android.server.am.ActivityManagerConstants r15 = r8.mConstants
            long r14 = r15.TOP_TO_FGS_GRACE_DURATION
            long r2 = r2 + r14
            r14 = 100
            int r2 = (r2 > r46 ? 1 : (r2 == r46 ? 0 : -1))
            if (r2 > 0) goto L_0x0368
            int r2 = r7.setProcState
            r15 = 2
            if (r2 > r15) goto L_0x0389
            goto L_0x0369
        L_0x0368:
            r15 = 2
        L_0x0369:
            r10 = 50
            java.lang.String r2 = "fg-service-act"
            r7.adjType = r2
            if (r11 != r0) goto L_0x0389
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Raise to recent fg: "
            r2.append(r3)
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            r8.reportOomAdjMessageLocked(r4, r2)
            goto L_0x0389
        L_0x0386:
            r14 = 100
            r15 = 2
        L_0x0389:
            r3 = 9
            r2 = 200(0xc8, float:2.8E-43)
            if (r10 > r2) goto L_0x0391
            if (r6 <= r3) goto L_0x03bb
        L_0x0391:
            java.lang.Object r2 = r7.forcingToImportant
            if (r2 == 0) goto L_0x03bb
            r10 = 200(0xc8, float:2.8E-43)
            r6 = 9
            r2 = 0
            r7.cached = r2
            java.lang.String r2 = "force-imp"
            r7.adjType = r2
            java.lang.Object r2 = r7.forcingToImportant
            r7.adjSource = r2
            r5 = 2
            if (r11 != r0) goto L_0x03bb
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r14 = "Raise to force imp: "
            r2.append(r14)
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            r8.reportOomAdjMessageLocked(r4, r2)
        L_0x03bb:
            com.android.server.am.ActivityManagerService r2 = r8.mService
            com.android.server.wm.ActivityTaskManagerInternal r2 = r2.mAtmInternal
            com.android.server.wm.WindowProcessController r14 = r42.getWindowProcessController()
            boolean r2 = r2.isHeavyWeightProcess(r14)
            if (r2 == 0) goto L_0x040f
            r2 = 400(0x190, float:5.6E-43)
            if (r10 <= r2) goto L_0x03ee
            r10 = 400(0x190, float:5.6E-43)
            r5 = 0
            r2 = 0
            r7.cached = r2
            java.lang.String r2 = "heavy"
            r7.adjType = r2
            if (r11 != r0) goto L_0x03ee
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r14 = "Raise adj to heavy: "
            r2.append(r14)
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            r8.reportOomAdjMessageLocked(r4, r2)
        L_0x03ee:
            r2 = 14
            if (r6 <= r2) goto L_0x040f
            r6 = 14
            java.lang.String r2 = "heavy"
            r7.adjType = r2
            if (r11 != r0) goto L_0x040f
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r14 = "Raise procstate to heavy: "
            r2.append(r14)
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            r8.reportOomAdjMessageLocked(r4, r2)
        L_0x040f:
            boolean r2 = r1.isHomeProcess()
            if (r2 == 0) goto L_0x045b
            r2 = 600(0x258, float:8.41E-43)
            if (r10 <= r2) goto L_0x043a
            r10 = 600(0x258, float:8.41E-43)
            r5 = 0
            r2 = 0
            r7.cached = r2
            java.lang.String r2 = "home"
            r7.adjType = r2
            if (r11 != r0) goto L_0x043a
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r14 = "Raise adj to home: "
            r2.append(r14)
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            r8.reportOomAdjMessageLocked(r4, r2)
        L_0x043a:
            r2 = 15
            if (r6 <= r2) goto L_0x045b
            r6 = 15
            java.lang.String r2 = "home"
            r7.adjType = r2
            if (r11 != r0) goto L_0x045b
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r14 = "Raise procstate to home: "
            r2.append(r14)
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            r8.reportOomAdjMessageLocked(r4, r2)
        L_0x045b:
            boolean r2 = r1.isPreviousProcess()
            r14 = 16
            if (r2 == 0) goto L_0x04ad
            boolean r2 = r42.hasActivities()
            if (r2 == 0) goto L_0x04ad
            r2 = 700(0x2bc, float:9.81E-43)
            if (r10 <= r2) goto L_0x048e
            r10 = 700(0x2bc, float:9.81E-43)
            r5 = 0
            r2 = 0
            r7.cached = r2
            java.lang.String r2 = "previous"
            r7.adjType = r2
            if (r11 != r0) goto L_0x048e
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r15 = "Raise adj to prev: "
            r2.append(r15)
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            r8.reportOomAdjMessageLocked(r4, r2)
        L_0x048e:
            if (r6 <= r14) goto L_0x04ad
            r6 = 16
            java.lang.String r2 = "previous"
            r7.adjType = r2
            if (r11 != r0) goto L_0x04ad
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r15 = "Raise procstate to prev: "
            r2.append(r15)
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            r8.reportOomAdjMessageLocked(r4, r2)
        L_0x04ad:
            if (r48 != 0) goto L_0x04b1
            r2 = r10
            goto L_0x04b9
        L_0x04b1:
            int r2 = r42.getCurRawAdj()
            int r2 = java.lang.Math.min(r10, r2)
        L_0x04b9:
            r7.setCurRawAdj(r2)
            if (r48 != 0) goto L_0x04c0
            r2 = r6
            goto L_0x04c8
        L_0x04c0:
            int r2 = r42.getCurRawProcState()
            int r2 = java.lang.Math.min(r6, r2)
        L_0x04c8:
            r7.setCurRawProcState(r2)
            r2 = 0
            r7.hasStartedServices = r2
            int r2 = r8.mAdjSeq
            r7.adjSeq = r2
            com.android.server.am.ActivityManagerService r2 = r8.mService
            android.util.SparseArray<com.android.server.am.BackupRecord> r2 = r2.mBackupTargets
            int r15 = r7.userId
            java.lang.Object r2 = r2.get(r15)
            r15 = r2
            com.android.server.am.BackupRecord r15 = (com.android.server.am.BackupRecord) r15
            if (r15 == 0) goto L_0x052d
            com.android.server.am.ProcessRecord r2 = r15.app
            if (r7 != r2) goto L_0x052d
            r2 = 300(0x12c, float:4.2E-43)
            if (r10 <= r2) goto L_0x050d
            r10 = 300(0x12c, float:4.2E-43)
            if (r6 <= r3) goto L_0x04f0
            r2 = 9
            r6 = r2
        L_0x04f0:
            java.lang.String r2 = "backup"
            r7.adjType = r2
            if (r11 != r0) goto L_0x050a
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Raise adj to backup: "
            r2.append(r3)
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            r8.reportOomAdjMessageLocked(r4, r2)
        L_0x050a:
            r2 = 0
            r7.cached = r2
        L_0x050d:
            r2 = 10
            if (r6 <= r2) goto L_0x052d
            r6 = 10
            java.lang.String r2 = "backup"
            r7.adjType = r2
            if (r11 != r0) goto L_0x052d
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Raise procstate to backup: "
            r2.append(r3)
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            r8.reportOomAdjMessageLocked(r4, r2)
        L_0x052d:
            android.util.ArraySet<com.android.server.am.ServiceRecord> r2 = r7.f3services
            int r2 = r2.size()
            r3 = 1
            int r2 = r2 - r3
            r39 = r10
            r10 = r2
            r2 = r39
        L_0x053a:
            if (r10 < 0) goto L_0x09cd
            if (r2 > 0) goto L_0x0559
            if (r5 == 0) goto L_0x0559
            r14 = 2
            if (r6 <= r14) goto L_0x0544
            goto L_0x0559
        L_0x0544:
            r28 = r1
            r10 = r4
            r27 = r5
            r29 = r9
            r34 = r15
            r3 = 500(0x1f4, float:7.0E-43)
            r4 = r0
            r9 = r7
            r7 = 3
            r39 = r13
            r13 = r12
            r12 = r39
            goto L_0x09e2
        L_0x0559:
            android.util.ArraySet<com.android.server.am.ServiceRecord> r14 = r7.f3services
            java.lang.Object r14 = r14.valueAt(r10)
            com.android.server.am.ServiceRecord r14 = (com.android.server.am.ServiceRecord) r14
            boolean r3 = r14.startRequested
            if (r3 == 0) goto L_0x05f0
            r3 = 1
            r7.hasStartedServices = r3
            r3 = 11
            if (r6 <= r3) goto L_0x058f
            r6 = 11
            java.lang.String r3 = "started-services"
            r7.adjType = r3
            if (r11 != r0) goto L_0x058c
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            r27 = r5
            java.lang.String r5 = "Raise procstate to started service: "
            r3.append(r5)
            r3.append(r7)
            java.lang.String r3 = r3.toString()
            r8.reportOomAdjMessageLocked(r4, r3)
            goto L_0x0591
        L_0x058c:
            r27 = r5
            goto L_0x0591
        L_0x058f:
            r27 = r5
        L_0x0591:
            boolean r3 = r7.hasShownUi
            if (r3 == 0) goto L_0x05ab
            boolean r3 = r1.isHomeProcess()
            if (r3 != 0) goto L_0x05ab
            r3 = 500(0x1f4, float:7.0E-43)
            if (r2 <= r3) goto L_0x05a3
            java.lang.String r3 = "cch-started-ui-services"
            r7.adjType = r3
        L_0x05a3:
            r28 = r1
            r29 = r9
            r30 = r10
            r5 = 0
            goto L_0x05f9
        L_0x05ab:
            r3 = r6
            long r5 = r14.lastActivity
            r28 = r1
            com.android.server.am.ActivityManagerConstants r1 = r8.mConstants
            r29 = r9
            r30 = r10
            long r9 = r1.MAX_SERVICE_INACTIVITY
            long r5 = r5 + r9
            int r1 = (r46 > r5 ? 1 : (r46 == r5 ? 0 : -1))
            if (r1 >= 0) goto L_0x05e5
            r1 = 500(0x1f4, float:7.0E-43)
            if (r2 <= r1) goto L_0x05e3
            r1 = 500(0x1f4, float:7.0E-43)
            java.lang.String r2 = "started-services"
            r7.adjType = r2
            if (r11 != r0) goto L_0x05de
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r5 = "Raise adj to started service: "
            r2.append(r5)
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            r8.reportOomAdjMessageLocked(r4, r2)
        L_0x05de:
            r5 = 0
            r7.cached = r5
            r2 = r1
            goto L_0x05e6
        L_0x05e3:
            r5 = 0
            goto L_0x05e6
        L_0x05e5:
            r5 = 0
        L_0x05e6:
            r9 = 500(0x1f4, float:7.0E-43)
            if (r2 <= r9) goto L_0x05ee
            java.lang.String r1 = "cch-started-services"
            r7.adjType = r1
        L_0x05ee:
            r6 = r3
            goto L_0x05f9
        L_0x05f0:
            r28 = r1
            r27 = r5
            r29 = r9
            r30 = r10
            r5 = 0
        L_0x05f9:
            android.util.ArrayMap r9 = r14.getConnections()
            int r1 = r9.size()
            r10 = 1
            int r1 = r1 - r10
            r39 = r6
            r6 = r1
            r1 = r39
        L_0x0608:
            if (r6 < 0) goto L_0x09a3
            if (r2 > 0) goto L_0x061f
            if (r27 == 0) goto L_0x061f
            r3 = 2
            if (r1 <= r3) goto L_0x0612
            goto L_0x061f
        L_0x0612:
            r10 = r4
            r9 = r7
            r34 = r15
            r7 = 3
            r4 = r0
            r39 = r13
            r13 = r12
            r12 = r39
            goto L_0x09b2
        L_0x061f:
            java.lang.Object r3 = r9.valueAt(r6)
            java.util.ArrayList r3 = (java.util.ArrayList) r3
            r18 = 0
            r10 = r1
            r1 = r2
            r2 = r18
        L_0x062b:
            int r5 = r3.size()
            if (r2 >= r5) goto L_0x097b
            if (r1 > 0) goto L_0x064b
            if (r27 == 0) goto L_0x064b
            r5 = 2
            if (r10 <= r5) goto L_0x0639
            goto L_0x064b
        L_0x0639:
            r3 = r1
            r2 = r4
            r17 = r6
            r32 = r9
            r34 = r15
            r4 = r0
            r9 = r7
            r7 = 3
            r39 = r13
            r13 = r12
            r12 = r39
            goto L_0x098f
        L_0x064b:
            java.lang.Object r5 = r3.get(r2)
            com.android.server.am.ConnectionRecord r5 = (com.android.server.am.ConnectionRecord) r5
            r26 = r0
            com.android.server.am.AppBindRecord r0 = r5.binding
            com.android.server.am.ProcessRecord r0 = r0.client
            if (r0 != r7) goto L_0x0672
            r33 = r1
            r22 = r2
            r20 = r3
            r37 = r4
            r17 = r6
            r32 = r9
            r35 = r12
            r36 = r13
            r34 = r15
            r38 = r26
            r12 = r46
            r9 = r7
            goto L_0x06e4
        L_0x0672:
            r31 = 0
            int r0 = r5.flags
            r0 = r0 & 32
            if (r0 != 0) goto L_0x08f2
            com.android.server.am.AppBindRecord r0 = r5.binding
            com.android.server.am.ProcessRecord r0 = r0.client
            r32 = r1
            java.lang.Object r1 = r0.adjSource
            boolean r1 = r1 instanceof com.android.server.am.ProcessRecord
            if (r1 == 0) goto L_0x06a4
            java.lang.Object r1 = r0.adjSource
            com.android.server.am.ProcessRecord r1 = (com.android.server.am.ProcessRecord) r1
            if (r1 != r7) goto L_0x06a4
            r22 = r2
            r20 = r3
            r37 = r4
            r17 = r6
            r35 = r12
            r36 = r13
            r34 = r15
            r38 = r26
            r33 = r32
            r12 = r46
            r32 = r9
            r9 = r7
            goto L_0x06e4
        L_0x06a4:
            r1 = r26
            r26 = r0
            r0 = r41
            r33 = r32
            r32 = r9
            r9 = r1
            r1 = r26
            r22 = r2
            r34 = r15
            r15 = 0
            r2 = r43
            r20 = r3
            r35 = r12
            r36 = r13
            r12 = r46
            r3 = r44
            r37 = r4
            r4 = r45
            r15 = r5
            r17 = r6
            r38 = r9
            r9 = 1
            r5 = r46
            r9 = r7
            r7 = r48
            r0.computeOomAdjLocked(r1, r2, r3, r4, r5, r7)
            r1 = r42
            r2 = r26
            r3 = r10
            r4 = r33
            r5 = r48
            boolean r0 = r0.shouldSkipDueToCycle(r1, r2, r3, r4, r5)
            if (r0 == 0) goto L_0x06f1
        L_0x06e4:
            r1 = r33
            r13 = r35
            r12 = r36
            r2 = r37
            r4 = r38
            r7 = 3
            goto L_0x0962
        L_0x06f1:
            int r0 = r26.getCurRawAdj()
            int r1 = r26.getCurRawProcState()
            r2 = 17
            if (r1 < r2) goto L_0x06ff
            r1 = 20
        L_0x06ff:
            r2 = 0
            int r3 = r15.flags
            r4 = 16
            r3 = r3 & r4
            if (r3 == 0) goto L_0x0730
            boolean r3 = r9.hasShownUi
            if (r3 == 0) goto L_0x071d
            boolean r3 = r28.isHomeProcess()
            if (r3 != 0) goto L_0x071d
            r3 = r33
            if (r3 <= r0) goto L_0x0717
            java.lang.String r2 = "cch-bound-ui-services"
        L_0x0717:
            r4 = 0
            r9.cached = r4
            r0 = r3
            r1 = r10
            goto L_0x0732
        L_0x071d:
            r3 = r33
            long r4 = r14.lastActivity
            com.android.server.am.ActivityManagerConstants r6 = r8.mConstants
            long r6 = r6.MAX_SERVICE_INACTIVITY
            long r4 = r4 + r6
            int r4 = (r12 > r4 ? 1 : (r12 == r4 ? 0 : -1))
            if (r4 < 0) goto L_0x0732
            if (r3 <= r0) goto L_0x072e
            java.lang.String r2 = "cch-bound-services"
        L_0x072e:
            r0 = r3
            goto L_0x0732
        L_0x0730:
            r3 = r33
        L_0x0732:
            if (r3 <= r0) goto L_0x07cb
            boolean r4 = r9.hasShownUi
            if (r4 == 0) goto L_0x0758
            boolean r4 = r28.isHomeProcess()
            if (r4 != 0) goto L_0x0758
            r4 = 200(0xc8, float:2.8E-43)
            if (r0 <= r4) goto L_0x0758
            r4 = 900(0x384, float:1.261E-42)
            if (r3 < r4) goto L_0x0752
            java.lang.String r2 = "cch-bound-ui-services"
            r5 = r3
            r6 = r26
            r4 = 100
            r3 = r2
            r2 = r27
            goto L_0x07d3
        L_0x0752:
            r6 = r26
            r4 = 100
            goto L_0x07cf
        L_0x0758:
            int r4 = r15.flags
            r4 = r4 & 72
            if (r4 == 0) goto L_0x0777
            r4 = -700(0xfffffffffffffd44, float:NaN)
            if (r0 < r4) goto L_0x0767
            r4 = r0
            r5 = r4
            r4 = 100
            goto L_0x07b0
        L_0x0767:
            r4 = -700(0xfffffffffffffd44, float:NaN)
            r27 = 2
            r10 = 0
            int r5 = r8.mAdjSeq
            r15.trackProcState(r10, r5, r12)
            r31 = 1
            r5 = r4
            r4 = 100
            goto L_0x07b0
        L_0x0777:
            int r4 = r15.flags
            r4 = r4 & 256(0x100, float:3.59E-43)
            if (r4 == 0) goto L_0x078b
            r4 = 200(0xc8, float:2.8E-43)
            if (r0 >= r4) goto L_0x078b
            r4 = 250(0xfa, float:3.5E-43)
            if (r3 <= r4) goto L_0x078b
            r4 = 250(0xfa, float:3.5E-43)
            r5 = r4
            r4 = 100
            goto L_0x07b0
        L_0x078b:
            int r4 = r15.flags
            r5 = 1073741824(0x40000000, float:2.0)
            r4 = r4 & r5
            if (r4 == 0) goto L_0x079d
            r4 = 200(0xc8, float:2.8E-43)
            if (r0 >= r4) goto L_0x079f
            if (r3 <= r4) goto L_0x079f
            r5 = 200(0xc8, float:2.8E-43)
            r4 = 100
            goto L_0x07b0
        L_0x079d:
            r4 = 200(0xc8, float:2.8E-43)
        L_0x079f:
            if (r0 < r4) goto L_0x07a6
            r4 = r0
            r5 = r4
            r4 = 100
            goto L_0x07b0
        L_0x07a6:
            r4 = 100
            if (r3 <= r4) goto L_0x07af
            int r5 = java.lang.Math.max(r0, r4)
            goto L_0x07b0
        L_0x07af:
            r5 = r3
        L_0x07b0:
            r6 = r26
            boolean r7 = r6.cached
            if (r7 != 0) goto L_0x07b9
            r7 = 0
            r9.cached = r7
        L_0x07b9:
            if (r3 <= r5) goto L_0x07c6
            r3 = r5
            r9.setCurRawAdj(r3)
            java.lang.String r2 = "service"
            r3 = r2
            r2 = r27
            goto L_0x07d3
        L_0x07c6:
            r5 = r3
            r3 = r2
            r2 = r27
            goto L_0x07d3
        L_0x07cb:
            r6 = r26
            r4 = 100
        L_0x07cf:
            r5 = r3
            r3 = r2
            r2 = r27
        L_0x07d3:
            int r7 = r15.flags
            r16 = 8388612(0x800004, float:1.1754949E-38)
            r7 = r7 & r16
            if (r7 != 0) goto L_0x0841
            int r7 = r6.getCurrentSchedulingGroup()
            if (r7 <= r2) goto L_0x07eb
            int r4 = r15.flags
            r4 = r4 & 64
            if (r4 == 0) goto L_0x07ea
            r2 = r7
            goto L_0x07eb
        L_0x07ea:
            r2 = 2
        L_0x07eb:
            r4 = 2
            if (r1 >= r4) goto L_0x081d
            r4 = 4096(0x1000, float:5.74E-42)
            boolean r4 = r15.hasFlag(r4)
            if (r4 == 0) goto L_0x07f8
            r4 = 3
            goto L_0x07f9
        L_0x07f8:
            r4 = 6
        L_0x07f9:
            r33 = r0
            int r0 = r15.flags
            r26 = 67108864(0x4000000, float:1.5046328E-36)
            r0 = r0 & r26
            if (r0 == 0) goto L_0x0808
            r0 = r4
            r27 = r2
            goto L_0x081b
        L_0x0808:
            com.android.server.am.ActivityManagerService r0 = r8.mService
            int r0 = r0.mWakefulness
            r27 = r2
            r2 = 1
            if (r0 != r2) goto L_0x081a
            int r0 = r15.flags
            r2 = 33554432(0x2000000, float:9.403955E-38)
            r0 = r0 & r2
            if (r0 == 0) goto L_0x081a
            r0 = r4
            goto L_0x081b
        L_0x081a:
            r0 = 7
        L_0x081b:
            r1 = r0
            goto L_0x083c
        L_0x081d:
            r33 = r0
            r27 = r2
            r0 = 2
            if (r1 != r0) goto L_0x082f
            r0 = 4096(0x1000, float:5.74E-42)
            boolean r0 = r15.notHasFlag(r0)
            if (r0 == 0) goto L_0x083c
            r0 = 4
            r1 = r0
            goto L_0x083c
        L_0x082f:
            r0 = 4096(0x1000, float:5.74E-42)
            r2 = 5
            if (r1 > r2) goto L_0x083c
            boolean r0 = r15.notHasFlag(r0)
            if (r0 == 0) goto L_0x083c
            r0 = 5
            r1 = r0
        L_0x083c:
            r2 = r27
            r0 = 9
            goto L_0x0859
        L_0x0841:
            r33 = r0
            int r0 = r15.flags
            r4 = 8388608(0x800000, float:1.17549435E-38)
            r0 = r0 & r4
            if (r0 != 0) goto L_0x0851
            r0 = 9
            if (r1 >= r0) goto L_0x0859
            r1 = 9
            goto L_0x0859
        L_0x0851:
            r0 = 9
            r4 = 8
            if (r1 >= r4) goto L_0x0859
            r1 = 8
        L_0x0859:
            r7 = 3
            if (r2 >= r7) goto L_0x0868
            int r4 = r15.flags
            r26 = 524288(0x80000, float:7.34684E-40)
            r4 = r4 & r26
            if (r4 == 0) goto L_0x0868
            r2 = 3
            r27 = r2
            goto L_0x086a
        L_0x0868:
            r27 = r2
        L_0x086a:
            if (r31 != 0) goto L_0x0871
            int r2 = r8.mAdjSeq
            r15.trackProcState(r1, r2, r12)
        L_0x0871:
            if (r10 <= r1) goto L_0x087c
            r10 = r1
            r9.setCurRawProcState(r10)
            if (r3 != 0) goto L_0x087c
            java.lang.String r3 = "service"
        L_0x087c:
            r2 = 8
            if (r10 >= r2) goto L_0x088b
            int r2 = r15.flags
            r4 = 536870912(0x20000000, float:1.0842022E-19)
            r2 = r2 & r4
            if (r2 == 0) goto L_0x088b
            r2 = 1
            r9.setPendingUiClean(r2)
        L_0x088b:
            if (r3 == 0) goto L_0x08e8
            r9.adjType = r3
            r2 = 2
            r9.adjTypeCode = r2
            com.android.server.am.AppBindRecord r2 = r15.binding
            com.android.server.am.ProcessRecord r2 = r2.client
            r9.adjSource = r2
            r9.adjSourceProcState = r1
            android.content.ComponentName r2 = r14.instanceName
            r9.adjTarget = r2
            r4 = r38
            if (r11 != r4) goto L_0x08e1
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r12 = r36
            r2.append(r12)
            r2.append(r3)
            r13 = r35
            r2.append(r13)
            r2.append(r9)
            java.lang.String r0 = ", due to "
            r2.append(r0)
            com.android.server.am.AppBindRecord r0 = r15.binding
            com.android.server.am.ProcessRecord r0 = r0.client
            r2.append(r0)
            java.lang.String r0 = " adj="
            r2.append(r0)
            r2.append(r5)
            java.lang.String r0 = " procState="
            r2.append(r0)
            java.lang.String r0 = com.android.server.am.ProcessList.makeProcStateString(r10)
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r2 = r37
            r8.reportOomAdjMessageLocked(r2, r0)
            goto L_0x08f0
        L_0x08e1:
            r13 = r35
            r12 = r36
            r2 = r37
            goto L_0x08f0
        L_0x08e8:
            r13 = r35
            r12 = r36
            r2 = r37
            r4 = r38
        L_0x08f0:
            r1 = r5
            goto L_0x0908
        L_0x08f2:
            r22 = r2
            r20 = r3
            r2 = r4
            r17 = r6
            r32 = r9
            r34 = r15
            r4 = r26
            r3 = r1
            r15 = r5
            r9 = r7
            r7 = 3
            r39 = r13
            r13 = r12
            r12 = r39
        L_0x0908:
            int r0 = r15.flags
            r3 = 134217728(0x8000000, float:3.85186E-34)
            r0 = r0 & r3
            if (r0 == 0) goto L_0x0912
            r0 = 1
            r9.treatLikeActivity = r0
        L_0x0912:
            com.android.server.wm.ActivityServiceConnectionsHolder<com.android.server.am.ConnectionRecord> r0 = r15.activity
            int r3 = r15.flags
            r3 = r3 & 128(0x80, float:1.794E-43)
            if (r3 == 0) goto L_0x0962
            if (r0 == 0) goto L_0x0962
            if (r1 <= 0) goto L_0x0962
            boolean r3 = r0.isActivityVisible()
            if (r3 == 0) goto L_0x0962
            r1 = 0
            r9.setCurRawAdj(r1)
            int r3 = r15.flags
            r3 = r3 & 4
            if (r3 != 0) goto L_0x0939
            int r3 = r15.flags
            r3 = r3 & 64
            if (r3 == 0) goto L_0x0937
            r27 = 4
            goto L_0x0939
        L_0x0937:
            r27 = 2
        L_0x0939:
            r3 = 0
            r9.cached = r3
            java.lang.String r3 = "service"
            r9.adjType = r3
            r3 = 2
            r9.adjTypeCode = r3
            r9.adjSource = r0
            r9.adjSourceProcState = r10
            android.content.ComponentName r3 = r14.instanceName
            r9.adjTarget = r3
            if (r11 != r4) goto L_0x0962
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "Raise to service w/activity: "
            r3.append(r5)
            r3.append(r9)
            java.lang.String r3 = r3.toString()
            r8.reportOomAdjMessageLocked(r2, r3)
        L_0x0962:
            int r0 = r22 + 1
            r7 = r9
            r6 = r17
            r3 = r20
            r9 = r32
            r15 = r34
            r5 = 0
            r39 = r2
            r2 = r0
            r0 = r4
            r4 = r39
            r40 = r13
            r13 = r12
            r12 = r40
            goto L_0x062b
        L_0x097b:
            r22 = r2
            r20 = r3
            r2 = r4
            r17 = r6
            r32 = r9
            r34 = r15
            r4 = r0
            r3 = r1
            r9 = r7
            r7 = 3
            r39 = r13
            r13 = r12
            r12 = r39
        L_0x098f:
            int r6 = r17 + -1
            r0 = r4
            r7 = r9
            r1 = r10
            r9 = r32
            r15 = r34
            r5 = 0
            r10 = 1
            r4 = r2
            r2 = r3
            r39 = r13
            r13 = r12
            r12 = r39
            goto L_0x0608
        L_0x09a3:
            r10 = r4
            r17 = r6
            r32 = r9
            r34 = r15
            r4 = r0
            r9 = r7
            r7 = 3
            r39 = r13
            r13 = r12
            r12 = r39
        L_0x09b2:
            int r0 = r30 + -1
            r6 = r1
            r7 = r9
            r5 = r27
            r1 = r28
            r9 = r29
            r15 = r34
            r14 = 16
            r39 = r10
            r10 = r0
            r0 = r4
            r4 = r39
            r40 = r13
            r13 = r12
            r12 = r40
            goto L_0x053a
        L_0x09cd:
            r28 = r1
            r27 = r5
            r29 = r9
            r30 = r10
            r34 = r15
            r3 = 500(0x1f4, float:7.0E-43)
            r10 = r4
            r9 = r7
            r7 = 3
            r4 = r0
            r39 = r13
            r13 = r12
            r12 = r39
        L_0x09e2:
            android.util.ArrayMap<java.lang.String, com.android.server.am.ContentProviderRecord> r0 = r9.pubProviders
            int r0 = r0.size()
            r1 = 1
            int r0 = r0 - r1
            r14 = r0
        L_0x09eb:
            if (r14 < 0) goto L_0x0bd6
            if (r2 > 0) goto L_0x09fd
            if (r27 == 0) goto L_0x09fd
            r0 = 2
            if (r6 <= r0) goto L_0x09f5
            goto L_0x09fd
        L_0x09f5:
            r13 = r4
            r26 = r7
            r0 = r10
            r4 = r46
            goto L_0x0bde
        L_0x09fd:
            android.util.ArrayMap<java.lang.String, com.android.server.am.ContentProviderRecord> r0 = r9.pubProviders
            java.lang.Object r0 = r0.valueAt(r14)
            r15 = r0
            com.android.server.am.ContentProviderRecord r15 = (com.android.server.am.ContentProviderRecord) r15
            java.util.ArrayList<com.android.server.am.ContentProviderConnection> r0 = r15.connections
            int r0 = r0.size()
            r1 = 1
            int r0 = r0 - r1
            r5 = r0
            r1 = r6
            r6 = r2
            r2 = r27
        L_0x0a13:
            if (r5 < 0) goto L_0x0b5e
            if (r6 > 0) goto L_0x0a2f
            if (r2 == 0) goto L_0x0a2f
            r0 = 2
            if (r1 <= r0) goto L_0x0a1d
            goto L_0x0a2f
        L_0x0a1d:
            r3 = r6
            r26 = r7
            r7 = r12
            r35 = r13
            r16 = r14
            r13 = r4
            r4 = r46
            r39 = r10
            r10 = r2
            r2 = r39
            goto L_0x0b70
        L_0x0a2f:
            java.util.ArrayList<com.android.server.am.ContentProviderConnection> r0 = r15.connections
            java.lang.Object r0 = r0.get(r5)
            com.android.server.am.ContentProviderConnection r0 = (com.android.server.am.ContentProviderConnection) r0
            r36 = r12
            com.android.server.am.ProcessRecord r12 = r0.client
            if (r12 != r9) goto L_0x0a4e
            r17 = r1
            r20 = r5
            r22 = r6
            r26 = r7
            r37 = r10
            r35 = r13
            r16 = r14
            r10 = r2
            r13 = r4
            goto L_0x0a7f
        L_0x0a4e:
            r16 = r14
            r14 = r0
            r0 = r41
            r17 = r1
            r1 = r12
            r37 = r10
            r10 = r2
            r2 = r43
            r3 = r44
            r35 = r13
            r13 = r4
            r4 = r45
            r20 = r5
            r22 = r6
            r5 = r46
            r26 = r7
            r7 = r48
            r0.computeOomAdjLocked(r1, r2, r3, r4, r5, r7)
            r1 = r42
            r2 = r12
            r3 = r17
            r4 = r22
            r5 = r48
            boolean r0 = r0.shouldSkipDueToCycle(r1, r2, r3, r4, r5)
            if (r0 == 0) goto L_0x0a8b
        L_0x0a7f:
            r4 = r46
            r1 = r17
            r6 = r22
            r7 = r36
            r2 = r37
            goto L_0x0b4a
        L_0x0a8b:
            int r2 = r12.getCurRawAdj()
            int r0 = r12.getCurRawProcState()
            r1 = 17
            if (r0 < r1) goto L_0x0a99
            r0 = 20
        L_0x0a99:
            r1 = 0
            r3 = r22
            if (r3 <= r2) goto L_0x0ac6
            boolean r4 = r9.hasShownUi
            if (r4 == 0) goto L_0x0ab0
            boolean r4 = r28.isHomeProcess()
            if (r4 != 0) goto L_0x0ab0
            r4 = 200(0xc8, float:2.8E-43)
            if (r2 <= r4) goto L_0x0ab2
            java.lang.String r1 = "cch-ui-provider"
            r6 = r3
            goto L_0x0abe
        L_0x0ab0:
            r4 = 200(0xc8, float:2.8E-43)
        L_0x0ab2:
            if (r2 <= 0) goto L_0x0ab6
            r5 = r2
            goto L_0x0ab7
        L_0x0ab6:
            r5 = 0
        L_0x0ab7:
            r6 = r5
            r9.setCurRawAdj(r6)
            java.lang.String r1 = "provider"
        L_0x0abe:
            boolean r3 = r9.cached
            boolean r5 = r12.cached
            r3 = r3 & r5
            r9.cached = r3
            goto L_0x0ac9
        L_0x0ac6:
            r4 = 200(0xc8, float:2.8E-43)
            r6 = r3
        L_0x0ac9:
            r3 = 5
            if (r0 > r3) goto L_0x0ad8
            if (r1 != 0) goto L_0x0ad1
            java.lang.String r1 = "provider"
        L_0x0ad1:
            r5 = 2
            if (r0 != r5) goto L_0x0ad6
            r0 = 4
            goto L_0x0ad9
        L_0x0ad6:
            r0 = 6
            goto L_0x0ad9
        L_0x0ad8:
            r5 = 2
        L_0x0ad9:
            int r3 = r8.mAdjSeq
            r4 = r46
            r7 = r36
            r14.trackProcState(r0, r3, r4)
            r3 = r17
            if (r3 <= r0) goto L_0x0aea
            r3 = r0
            r9.setCurRawProcState(r3)
        L_0x0aea:
            r17 = r2
            int r2 = r12.getCurrentSchedulingGroup()
            if (r2 <= r10) goto L_0x0af4
            r2 = 2
            r10 = r2
        L_0x0af4:
            if (r1 == 0) goto L_0x0b45
            r9.adjType = r1
            r2 = 1
            r9.adjTypeCode = r2
            r9.adjSource = r12
            r9.adjSourceProcState = r0
            android.content.ComponentName r2 = r15.name
            r9.adjTarget = r2
            if (r11 != r13) goto L_0x0b40
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r2.append(r7)
            r2.append(r1)
            r22 = r0
            r0 = r35
            r2.append(r0)
            r2.append(r9)
            java.lang.String r0 = ", due to "
            r2.append(r0)
            r2.append(r12)
            java.lang.String r0 = " adj="
            r2.append(r0)
            r2.append(r6)
            java.lang.String r0 = " procState="
            r2.append(r0)
            java.lang.String r0 = com.android.server.am.ProcessList.makeProcStateString(r3)
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            r2 = r37
            r8.reportOomAdjMessageLocked(r2, r0)
            goto L_0x0b49
        L_0x0b40:
            r22 = r0
            r2 = r37
            goto L_0x0b49
        L_0x0b45:
            r22 = r0
            r2 = r37
        L_0x0b49:
            r1 = r3
        L_0x0b4a:
            int r0 = r20 + -1
            r5 = r0
            r12 = r7
            r4 = r13
            r14 = r16
            r7 = r26
            r13 = r35
            r3 = 500(0x1f4, float:7.0E-43)
            r39 = r10
            r10 = r2
            r2 = r39
            goto L_0x0a13
        L_0x0b5e:
            r20 = r5
            r3 = r6
            r26 = r7
            r7 = r12
            r35 = r13
            r16 = r14
            r13 = r4
            r4 = r46
            r39 = r10
            r10 = r2
            r2 = r39
        L_0x0b70:
            boolean r0 = r15.hasExternalProcessHandles()
            if (r0 == 0) goto L_0x0bc5
            if (r3 <= 0) goto L_0x0ba0
            r6 = 0
            r9.setCurRawAdj(r6)
            r0 = 2
            r3 = 0
            r9.cached = r3
            java.lang.String r3 = "ext-provider"
            r9.adjType = r3
            android.content.ComponentName r3 = r15.name
            r9.adjTarget = r3
            if (r11 != r13) goto L_0x0b9e
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r10 = "Raise adj to external provider: "
            r3.append(r10)
            r3.append(r9)
            java.lang.String r3 = r3.toString()
            r8.reportOomAdjMessageLocked(r2, r3)
        L_0x0b9e:
            r10 = r0
            r3 = r6
        L_0x0ba0:
            r0 = 7
            if (r1 <= r0) goto L_0x0bc1
            r0 = 7
            r9.setCurRawProcState(r0)
            if (r11 != r13) goto L_0x0bbd
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r6 = "Raise procstate to external provider: "
            r1.append(r6)
            r1.append(r9)
            java.lang.String r1 = r1.toString()
            r8.reportOomAdjMessageLocked(r2, r1)
        L_0x0bbd:
            r6 = r0
            r27 = r10
            goto L_0x0bc8
        L_0x0bc1:
            r6 = r1
            r27 = r10
            goto L_0x0bc8
        L_0x0bc5:
            r6 = r1
            r27 = r10
        L_0x0bc8:
            int r14 = r16 + -1
            r10 = r2
            r2 = r3
            r12 = r7
            r4 = r13
            r7 = r26
            r13 = r35
            r3 = 500(0x1f4, float:7.0E-43)
            goto L_0x09eb
        L_0x0bd6:
            r13 = r4
            r26 = r7
            r0 = r10
            r16 = r14
            r4 = r46
        L_0x0bde:
            long r14 = r9.lastProviderTime
            r16 = 0
            int r1 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r1 <= 0) goto L_0x0c3b
            long r14 = r9.lastProviderTime
            com.android.server.am.ActivityManagerConstants r1 = r8.mConstants
            r3 = r6
            long r6 = r1.CONTENT_PROVIDER_RETAIN_TIME
            long r14 = r14 + r6
            int r1 = (r14 > r4 ? 1 : (r14 == r4 ? 0 : -1))
            if (r1 <= 0) goto L_0x0c3c
            r1 = 700(0x2bc, float:9.81E-43)
            if (r2 <= r1) goto L_0x0c18
            r2 = 700(0x2bc, float:9.81E-43)
            r27 = 0
            r1 = 0
            r9.cached = r1
            java.lang.String r1 = "recent-provider"
            r9.adjType = r1
            if (r11 != r13) goto L_0x0c18
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r6 = "Raise adj to recent provider: "
            r1.append(r6)
            r1.append(r9)
            java.lang.String r1 = r1.toString()
            r8.reportOomAdjMessageLocked(r0, r1)
        L_0x0c18:
            r1 = 16
            if (r3 <= r1) goto L_0x0c3c
            r6 = 16
            java.lang.String r1 = "recent-provider"
            r9.adjType = r1
            if (r11 != r13) goto L_0x0c39
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r3 = "Raise procstate to recent provider: "
            r1.append(r3)
            r1.append(r9)
            java.lang.String r1 = r1.toString()
            r8.reportOomAdjMessageLocked(r0, r1)
        L_0x0c39:
            r3 = r6
            goto L_0x0c3c
        L_0x0c3b:
            r3 = r6
        L_0x0c3c:
            r0 = 20
            if (r3 < r0) goto L_0x0c57
            boolean r0 = r42.hasClientActivities()
            if (r0 == 0) goto L_0x0c4d
            r3 = 18
            java.lang.String r0 = "cch-client-act"
            r9.adjType = r0
            goto L_0x0c57
        L_0x0c4d:
            boolean r0 = r9.treatLikeActivity
            if (r0 == 0) goto L_0x0c57
            r3 = 17
            java.lang.String r0 = "cch-as-act"
            r9.adjType = r0
        L_0x0c57:
            r0 = 500(0x1f4, float:7.0E-43)
            if (r2 != r0) goto L_0x0ca3
            if (r45 == 0) goto L_0x0c9b
            int r0 = r8.mNewNumAServiceProcs
            int r1 = r8.mNumServiceProcs
            int r1 = r1 / 3
            if (r0 <= r1) goto L_0x0c67
            r0 = 1
            goto L_0x0c68
        L_0x0c67:
            r0 = 0
        L_0x0c68:
            r9.serviceb = r0
            int r0 = r8.mNewNumServiceProcs
            r1 = 1
            int r0 = r0 + r1
            r8.mNewNumServiceProcs = r0
            boolean r0 = r9.serviceb
            if (r0 != 0) goto L_0x0c97
            com.android.server.am.ActivityManagerService r0 = r8.mService
            int r0 = r0.mLastMemoryLevel
            if (r0 <= 0) goto L_0x0c8f
            long r0 = r9.lastPss
            com.android.server.am.ProcessList r6 = r8.mProcessList
            long r6 = r6.getCachedRestoreThresholdKb()
            int r0 = (r0 > r6 ? 1 : (r0 == r6 ? 0 : -1))
            if (r0 < 0) goto L_0x0c8d
            r0 = 1
            r9.serviceHighRam = r0
            r9.serviceb = r0
            r0 = 0
            goto L_0x0c9c
        L_0x0c8d:
            r0 = 1
            goto L_0x0c90
        L_0x0c8f:
            r0 = 1
        L_0x0c90:
            int r1 = r8.mNewNumAServiceProcs
            int r1 = r1 + r0
            r8.mNewNumAServiceProcs = r1
            r0 = 0
            goto L_0x0c9c
        L_0x0c97:
            r0 = 0
            r9.serviceHighRam = r0
            goto L_0x0c9c
        L_0x0c9b:
            r0 = 0
        L_0x0c9c:
            boolean r1 = r9.serviceb
            if (r1 == 0) goto L_0x0ca4
            r2 = 800(0x320, float:1.121E-42)
            goto L_0x0ca4
        L_0x0ca3:
            r0 = 0
        L_0x0ca4:
            r9.setCurRawAdj(r2)
            int r1 = r9.maxAdj
            if (r2 <= r1) goto L_0x0cbb
            int r2 = r9.maxAdj
            int r1 = r9.maxAdj
            r6 = 250(0xfa, float:3.5E-43)
            if (r1 > r6) goto L_0x0cb8
            r27 = 2
            r1 = r27
            goto L_0x0cbd
        L_0x0cb8:
            r1 = r27
            goto L_0x0cbd
        L_0x0cbb:
            r1 = r27
        L_0x0cbd:
            r6 = 6
            if (r3 < r6) goto L_0x0ccb
            com.android.server.am.ActivityManagerService r6 = r8.mService
            int r6 = r6.mWakefulness
            r7 = 1
            if (r6 == r7) goto L_0x0ccc
            if (r1 <= r7) goto L_0x0ccc
            r1 = 1
            goto L_0x0ccc
        L_0x0ccb:
            r7 = 1
        L_0x0ccc:
            int r6 = r9.maxProcState
            if (r3 <= r6) goto L_0x0cd2
            int r3 = r9.maxProcState
        L_0x0cd2:
            int r6 = r9.modifyRawOomAdj(r2)
            r9.curAdj = r6
            r9.setCurrentSchedulingGroup(r1)
            boolean r6 = r9.enableBoost
            if (r6 == 0) goto L_0x0ce7
            long r14 = r9.boostBeginTime
            boolean r6 = com.android.server.am.ActivityManagerServiceInjector.doBoostEx(r9, r14)
            r9.enableBoost = r6
        L_0x0ce7:
            r9.setCurProcState(r3)
            r9.setCurRawProcState(r3)
            r6 = r29
            r9.setHasForegroundActivities(r6)
            int r10 = r8.mAdjSeq
            r9.completedAdjSeq = r10
            int r10 = r9.curAdj
            r12 = r25
            if (r10 < r12) goto L_0x0d05
            int r10 = r42.getCurProcState()
            r14 = r24
            if (r10 >= r14) goto L_0x0d08
            goto L_0x0d07
        L_0x0d05:
            r14 = r24
        L_0x0d07:
            r0 = r7
        L_0x0d08:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.OomAdjuster.computeOomAdjLocked(com.android.server.am.ProcessRecord, int, com.android.server.am.ProcessRecord, boolean, long, boolean):boolean");
    }

    private boolean shouldSkipDueToCycle(ProcessRecord app, ProcessRecord client, int procState, int adj, boolean cycleReEval) {
        if (!client.containsCycle) {
            return false;
        }
        app.containsCycle = true;
        if (client.completedAdjSeq >= this.mAdjSeq) {
            return false;
        }
        if (!cycleReEval) {
            return true;
        }
        if (client.getCurRawProcState() < procState || client.getCurRawAdj() < adj) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public void reportOomAdjMessageLocked(String tag, String msg) {
        Slog.d(tag, msg);
        if (this.mService.mCurOomAdjObserver != null) {
            this.mService.mUiHandler.obtainMessage(70, msg).sendToTarget();
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:157:0x031a  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x039f  */
    /* JADX WARNING: Removed duplicated region for block: B:188:0x03cc  */
    /* JADX WARNING: Removed duplicated region for block: B:189:0x03e1  */
    @com.android.internal.annotations.GuardedBy({"mService"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private final boolean applyOomAdjLocked(com.android.server.am.ProcessRecord r21, boolean r22, long r23, long r25) {
        /*
            r20 = this;
            r1 = r20
            r2 = r21
            r9 = r23
            r11 = r25
            java.lang.String r3 = "OomAdjuster"
            r4 = 1
            int r0 = r21.getCurRawAdj()
            int r5 = r2.setRawAdj
            if (r0 == r5) goto L_0x0019
            int r0 = r21.getCurRawAdj()
            r2.setRawAdj = r0
        L_0x0019:
            r5 = 0
            com.android.server.am.AppCompactor r0 = r1.mAppCompact
            boolean r0 = r0.useCompaction()
            r13 = 1
            if (r0 == 0) goto L_0x0093
            com.android.server.am.ActivityManagerService r0 = r1.mService
            boolean r0 = r0.mBooted
            if (r0 == 0) goto L_0x0093
            int r0 = r2.curAdj
            int r6 = r2.setAdj
            if (r0 == r6) goto L_0x0061
            int r0 = r2.setAdj
            r6 = 200(0xc8, float:2.8E-43)
            if (r0 > r6) goto L_0x0047
            int r0 = r2.curAdj
            r6 = 700(0x2bc, float:9.81E-43)
            if (r0 == r6) goto L_0x0041
            int r0 = r2.curAdj
            r6 = 600(0x258, float:8.41E-43)
            if (r0 != r6) goto L_0x0047
        L_0x0041:
            com.android.server.am.AppCompactor r0 = r1.mAppCompact
            r0.compactAppSome(r2)
            goto L_0x0093
        L_0x0047:
            int r0 = r2.setAdj
            r6 = 999(0x3e7, float:1.4E-42)
            r7 = 900(0x384, float:1.261E-42)
            if (r0 < r7) goto L_0x0053
            int r0 = r2.setAdj
            if (r0 <= r6) goto L_0x0093
        L_0x0053:
            int r0 = r2.curAdj
            if (r0 < r7) goto L_0x0093
            int r0 = r2.curAdj
            if (r0 > r6) goto L_0x0093
            com.android.server.am.AppCompactor r0 = r1.mAppCompact
            r0.compactAppFull(r2)
            goto L_0x0093
        L_0x0061:
            com.android.server.am.ActivityManagerService r0 = r1.mService
            int r0 = r0.mWakefulness
            if (r0 == r13) goto L_0x0079
            int r0 = r2.setAdj
            if (r0 >= 0) goto L_0x0079
            com.android.server.am.AppCompactor r0 = r1.mAppCompact
            boolean r0 = r0.shouldCompactPersistent(r2, r9)
            if (r0 == 0) goto L_0x0079
            com.android.server.am.AppCompactor r0 = r1.mAppCompact
            r0.compactAppPersistent(r2)
            goto L_0x0093
        L_0x0079:
            com.android.server.am.ActivityManagerService r0 = r1.mService
            int r0 = r0.mWakefulness
            if (r0 == r13) goto L_0x0093
            int r0 = r21.getCurProcState()
            r6 = 6
            if (r0 != r6) goto L_0x0093
            com.android.server.am.AppCompactor r0 = r1.mAppCompact
            boolean r0 = r0.shouldCompactBFGS(r2, r9)
            if (r0 == 0) goto L_0x0093
            com.android.server.am.AppCompactor r0 = r1.mAppCompact
            r0.compactAppBfgs(r2)
        L_0x0093:
            int r0 = r2.curAdj
            int r6 = r2.setAdj
            java.lang.String r7 = ": "
            java.lang.String r14 = "ActivityManager"
            if (r0 == r6) goto L_0x00ea
            int r0 = r2.pid
            int r6 = r2.uid
            int r8 = r2.curAdj
            com.android.server.am.ProcessList.setOomAdj(r0, r6, r8)
            com.android.server.am.ActivityManagerService r0 = r1.mService
            int r0 = r0.mCurOomAdjUid
            android.content.pm.ApplicationInfo r6 = r2.info
            int r6 = r6.uid
            if (r0 != r6) goto L_0x00e2
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r6 = "Set "
            r0.append(r6)
            int r6 = r2.pid
            r0.append(r6)
            java.lang.String r6 = " "
            r0.append(r6)
            java.lang.String r6 = r2.processName
            r0.append(r6)
            java.lang.String r6 = " adj "
            r0.append(r6)
            int r6 = r2.curAdj
            r0.append(r6)
            r0.append(r7)
            java.lang.String r6 = r2.adjType
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            r1.reportOomAdjMessageLocked(r14, r0)
        L_0x00e2:
            int r0 = r2.curAdj
            r2.setAdj = r0
            r0 = -10000(0xffffffffffffd8f0, float:NaN)
            r2.verifiedAdj = r0
        L_0x00ea:
            int r15 = r21.getCurrentSchedulingGroup()
            int r0 = r2.setSchedGroup
            java.lang.String r8 = " to "
            if (r0 == r15) goto L_0x0236
            int r6 = r2.setSchedGroup
            r2.setSchedGroup = r15
            com.android.server.am.ActivityManagerService r0 = r1.mService
            int r0 = r0.mCurOomAdjUid
            int r13 = r2.uid
            if (r0 != r13) goto L_0x0124
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r13 = "Setting sched group of "
            r0.append(r13)
            java.lang.String r13 = r2.processName
            r0.append(r13)
            r0.append(r8)
            r0.append(r15)
            r0.append(r7)
            java.lang.String r7 = r2.adjType
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            r1.reportOomAdjMessageLocked(r14, r0)
        L_0x0124:
            java.lang.String r0 = r2.waitingToKill
            if (r0 == 0) goto L_0x0142
            android.util.ArraySet<com.android.server.am.BroadcastRecord> r0 = r2.curReceivers
            boolean r0 = r0.isEmpty()
            if (r0 == 0) goto L_0x0142
            int r0 = r2.setSchedGroup
            if (r0 != 0) goto L_0x0142
            java.lang.String r0 = r2.waitingToKill
            r3 = 1
            r2.kill(r0, r3)
            r4 = 0
            r18 = r4
            r19 = r8
            r8 = 0
            goto L_0x023b
        L_0x0142:
            r0 = 3
            if (r15 == 0) goto L_0x0153
            r7 = 1
            if (r15 == r7) goto L_0x0151
            if (r15 == r0) goto L_0x014f
            r7 = 4
            if (r15 == r7) goto L_0x014f
            r7 = -1
            goto L_0x0155
        L_0x014f:
            r7 = 5
            goto L_0x0155
        L_0x0151:
            r7 = 7
            goto L_0x0155
        L_0x0153:
            r7 = 0
        L_0x0155:
            android.os.Handler r13 = r1.mProcessGroupHandler
            int r0 = r2.pid
            r18 = r4
            android.content.pm.ApplicationInfo r4 = r2.info
            int r4 = r4.uid
            java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
            r19 = r8
            r8 = 0
            android.os.Message r0 = r13.obtainMessage(r8, r0, r7, r4)
            r13.sendMessage(r0)
            r0 = 3
            if (r15 != r0) goto L_0x01c0
            if (r6 == r0) goto L_0x01bd
            com.android.server.wm.WindowProcessController r0 = r21.getWindowProcessController()     // Catch:{ Exception -> 0x01b9 }
            r0.onTopProcChanged()     // Catch:{ Exception -> 0x01b9 }
            com.android.server.am.ActivityManagerService r0 = r1.mService     // Catch:{ Exception -> 0x01b9 }
            boolean r0 = r0.mUseFifoUiScheduling     // Catch:{ Exception -> 0x01b9 }
            if (r0 == 0) goto L_0x01a1
            int r0 = r2.pid     // Catch:{ Exception -> 0x01b9 }
            int r0 = android.os.Process.getThreadPriority(r0)     // Catch:{ Exception -> 0x01b9 }
            r2.savedPriority = r0     // Catch:{ Exception -> 0x01b9 }
            com.android.server.am.ActivityManagerService r0 = r1.mService     // Catch:{ Exception -> 0x01b9 }
            int r0 = r2.pid     // Catch:{ Exception -> 0x01b9 }
            r3 = 1
            com.android.server.am.ActivityManagerService.scheduleAsFifoPriority(r0, r3)     // Catch:{ Exception -> 0x01b9 }
            int r0 = r2.renderThreadTid     // Catch:{ Exception -> 0x01b9 }
            if (r0 == 0) goto L_0x019e
            com.android.server.am.ActivityManagerService r0 = r1.mService     // Catch:{ Exception -> 0x01b9 }
            int r0 = r2.renderThreadTid     // Catch:{ Exception -> 0x01b9 }
            r3 = 1
            com.android.server.am.ActivityManagerService.scheduleAsFifoPriority(r0, r3)     // Catch:{ Exception -> 0x01b9 }
            r8 = 0
            goto L_0x0235
        L_0x019e:
            r8 = 0
            goto L_0x0235
        L_0x01a1:
            int r0 = r2.pid     // Catch:{ Exception -> 0x01b9 }
            r3 = -10
            android.os.Process.setThreadPriority(r0, r3)     // Catch:{ Exception -> 0x01b9 }
            int r0 = r2.renderThreadTid     // Catch:{ Exception -> 0x01b9 }
            if (r0 == 0) goto L_0x01b6
            int r0 = r2.renderThreadTid     // Catch:{ IllegalArgumentException -> 0x01b2 }
            android.os.Process.setThreadPriority(r0, r3)     // Catch:{ IllegalArgumentException -> 0x01b2 }
            goto L_0x01b3
        L_0x01b2:
            r0 = move-exception
        L_0x01b3:
            r8 = 0
            goto L_0x0235
        L_0x01b6:
            r8 = 0
            goto L_0x0235
        L_0x01b9:
            r0 = move-exception
            r8 = 0
            goto L_0x023b
        L_0x01bd:
            r8 = 0
            goto L_0x0235
        L_0x01c0:
            r0 = 3
            if (r6 != r0) goto L_0x0234
            if (r15 == r0) goto L_0x0234
            com.android.server.wm.WindowProcessController r0 = r21.getWindowProcessController()     // Catch:{ Exception -> 0x01b9 }
            r0.onTopProcChanged()     // Catch:{ Exception -> 0x01b9 }
            com.android.server.am.ActivityManagerService r0 = r1.mService     // Catch:{ Exception -> 0x01b9 }
            boolean r0 = r0.mUseFifoUiScheduling     // Catch:{ Exception -> 0x01b9 }
            if (r0 == 0) goto L_0x0222
            int r0 = r2.pid     // Catch:{ IllegalArgumentException -> 0x020b, SecurityException -> 0x01f4 }
            r4 = 0
            android.os.Process.setThreadScheduler(r0, r4, r4)     // Catch:{ Exception -> 0x01f1 }
            int r0 = r2.pid     // Catch:{ IllegalArgumentException -> 0x020b, SecurityException -> 0x01f4 }
            int r4 = r2.savedPriority     // Catch:{ IllegalArgumentException -> 0x020b, SecurityException -> 0x01f4 }
            android.os.Process.setThreadPriority(r0, r4)     // Catch:{ IllegalArgumentException -> 0x020b, SecurityException -> 0x01f4 }
            int r0 = r2.renderThreadTid     // Catch:{ IllegalArgumentException -> 0x020b, SecurityException -> 0x01f4 }
            if (r0 == 0) goto L_0x01ef
            int r0 = r2.renderThreadTid     // Catch:{ IllegalArgumentException -> 0x020b, SecurityException -> 0x01f4 }
            r4 = 0
            android.os.Process.setThreadScheduler(r0, r4, r4)     // Catch:{ Exception -> 0x01f1 }
            int r0 = r2.renderThreadTid     // Catch:{ IllegalArgumentException -> 0x020b, SecurityException -> 0x01f4 }
            r4 = -4
            android.os.Process.setThreadPriority(r0, r4)     // Catch:{ IllegalArgumentException -> 0x020b, SecurityException -> 0x01f4 }
        L_0x01ef:
            r8 = 0
            goto L_0x0235
        L_0x01f1:
            r0 = move-exception
            r8 = r4
            goto L_0x023b
        L_0x01f4:
            r0 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01b9 }
            r4.<init>()     // Catch:{ Exception -> 0x01b9 }
            java.lang.String r8 = "Failed to set scheduling policy, not allowed:\n"
            r4.append(r8)     // Catch:{ Exception -> 0x01b9 }
            r4.append(r0)     // Catch:{ Exception -> 0x01b9 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x01b9 }
            android.util.Slog.w(r3, r4)     // Catch:{ Exception -> 0x01b9 }
            r8 = 0
            goto L_0x0235
        L_0x020b:
            r0 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x01b9 }
            r4.<init>()     // Catch:{ Exception -> 0x01b9 }
            java.lang.String r8 = "Failed to set scheduling policy, thread does not exist:\n"
            r4.append(r8)     // Catch:{ Exception -> 0x01b9 }
            r4.append(r0)     // Catch:{ Exception -> 0x01b9 }
            java.lang.String r4 = r4.toString()     // Catch:{ Exception -> 0x01b9 }
            android.util.Slog.w(r3, r4)     // Catch:{ Exception -> 0x01b9 }
            r8 = 0
            goto L_0x0235
        L_0x0222:
            int r0 = r2.pid     // Catch:{ Exception -> 0x01b9 }
            r8 = 0
            android.os.Process.setThreadPriority(r0, r8)     // Catch:{ Exception -> 0x0232 }
            int r0 = r2.renderThreadTid     // Catch:{ Exception -> 0x0232 }
            if (r0 == 0) goto L_0x0235
            int r0 = r2.renderThreadTid     // Catch:{ Exception -> 0x0232 }
            android.os.Process.setThreadPriority(r0, r8)     // Catch:{ Exception -> 0x0232 }
            goto L_0x0235
        L_0x0232:
            r0 = move-exception
            goto L_0x023b
        L_0x0234:
            r8 = 0
        L_0x0235:
            goto L_0x023b
        L_0x0236:
            r18 = r4
            r19 = r8
            r8 = 0
        L_0x023b:
            boolean r0 = r2.repForegroundActivities
            boolean r3 = r21.hasForegroundActivities()
            if (r0 == r3) goto L_0x024d
            boolean r0 = r21.hasForegroundActivities()
            r2.repForegroundActivities = r0
            r5 = r5 | 1
            r13 = r5
            goto L_0x024e
        L_0x024d:
            r13 = r5
        L_0x024e:
            int r0 = r21.getReportedProcState()
            int r3 = r21.getCurProcState()
            if (r0 == r3) goto L_0x026e
            int r0 = r21.getCurProcState()
            r2.setReportedProcState(r0)
            android.app.IApplicationThread r0 = r2.thread
            if (r0 == 0) goto L_0x026e
            android.app.IApplicationThread r0 = r2.thread     // Catch:{ RemoteException -> 0x026d }
            int r3 = r21.getReportedProcState()     // Catch:{ RemoteException -> 0x026d }
            r0.setProcessState(r3)     // Catch:{ RemoteException -> 0x026d }
            goto L_0x026e
        L_0x026d:
            r0 = move-exception
        L_0x026e:
            int r0 = r2.setProcState
            r3 = 21
            if (r0 == r3) goto L_0x02ef
            int r0 = r21.getCurProcState()
            int r3 = r2.setProcState
            boolean r0 = com.android.server.am.ProcessList.procStatesDifferForMem(r0, r3)
            if (r0 == 0) goto L_0x0289
            r17 = r13
            r16 = r15
            r15 = r19
            r13 = r8
            goto L_0x02f6
        L_0x0289:
            long r3 = r2.nextPssTime
            int r0 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r0 > 0) goto L_0x02ba
            long r3 = r2.lastPssTime
            r5 = 3600000(0x36ee80, double:1.7786363E-317)
            long r3 = r3 + r5
            int r0 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r0 <= 0) goto L_0x02b2
            long r3 = r2.lastStateTime
            com.android.server.am.ActivityManagerService r0 = r1.mService
            boolean r0 = r0.mTestPssMode
            long r5 = com.android.server.am.ProcessList.minTimeFromStateChange(r0)
            long r3 = r3 + r5
            int r0 = (r9 > r3 ? 1 : (r9 == r3 ? 0 : -1))
            if (r0 <= 0) goto L_0x02a9
            goto L_0x02ba
        L_0x02a9:
            r17 = r13
            r16 = r15
            r15 = r19
            r13 = r8
            goto L_0x0312
        L_0x02b2:
            r17 = r13
            r16 = r15
            r15 = r19
            r13 = r8
            goto L_0x0312
        L_0x02ba:
            com.android.server.am.ActivityManagerService r0 = r1.mService
            int r3 = r2.setProcState
            boolean r0 = r0.requestPssLocked(r2, r3)
            if (r0 == 0) goto L_0x02e7
            int r3 = r21.getCurProcState()
            com.android.server.am.ProcessList$ProcStateMemTracker r4 = r2.procStateMemTracker
            com.android.server.am.ActivityManagerService r0 = r1.mService
            boolean r5 = r0.mTestPssMode
            com.android.server.am.ActivityManagerService r0 = r1.mService
            com.android.server.wm.ActivityTaskManagerInternal r0 = r0.mAtmInternal
            boolean r6 = r0.isSleeping()
            r7 = r8
            r17 = r13
            r16 = r15
            r15 = r19
            r13 = r7
            r7 = r23
            long r3 = com.android.server.am.ProcessList.computeNextPssTime(r3, r4, r5, r6, r7)
            r2.nextPssTime = r3
            goto L_0x0312
        L_0x02e7:
            r17 = r13
            r16 = r15
            r15 = r19
            r13 = r8
            goto L_0x0312
        L_0x02ef:
            r17 = r13
            r16 = r15
            r15 = r19
            r13 = r8
        L_0x02f6:
            r2.lastStateTime = r9
            int r3 = r21.getCurProcState()
            com.android.server.am.ProcessList$ProcStateMemTracker r4 = r2.procStateMemTracker
            com.android.server.am.ActivityManagerService r0 = r1.mService
            boolean r5 = r0.mTestPssMode
            com.android.server.am.ActivityManagerService r0 = r1.mService
            com.android.server.wm.ActivityTaskManagerInternal r0 = r0.mAtmInternal
            boolean r6 = r0.isSleeping()
            r7 = r23
            long r3 = com.android.server.am.ProcessList.computeNextPssTime(r3, r4, r5, r6, r7)
            r2.nextPssTime = r3
        L_0x0312:
            int r0 = r2.setProcState
            int r3 = r21.getCurProcState()
            if (r0 == r3) goto L_0x039f
            com.android.server.am.ActivityManagerService r0 = r1.mService
            int r0 = r0.mCurOomAdjUid
            int r3 = r2.uid
            if (r0 != r3) goto L_0x035c
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "Proc state change of "
            r0.append(r3)
            java.lang.String r3 = r2.processName
            r0.append(r3)
            r0.append(r15)
            int r3 = r21.getCurProcState()
            java.lang.String r3 = com.android.server.am.ProcessList.makeProcStateString(r3)
            r0.append(r3)
            java.lang.String r3 = " ("
            r0.append(r3)
            int r3 = r21.getCurProcState()
            r0.append(r3)
            java.lang.String r3 = "): "
            r0.append(r3)
            java.lang.String r3 = r2.adjType
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            r1.reportOomAdjMessageLocked(r14, r0)
        L_0x035c:
            int r0 = r2.setProcState
            r3 = 11
            if (r0 >= r3) goto L_0x0364
            r0 = 1
            goto L_0x0365
        L_0x0364:
            r0 = r13
        L_0x0365:
            int r4 = r21.getCurProcState()
            if (r4 >= r3) goto L_0x036d
            r3 = 1
            goto L_0x036e
        L_0x036d:
            r3 = r13
        L_0x036e:
            if (r0 == 0) goto L_0x0379
            if (r3 != 0) goto L_0x0379
            r2.setWhenUnimportant(r9)
            r4 = 0
            r2.lastCpuTime = r4
        L_0x0379:
            r1.maybeUpdateUsageStatsLocked(r2, r11)
            r1.maybeUpdateLastTopTime(r2, r9)
            int r4 = r21.getCurProcState()
            r2.setProcState = r4
            int r4 = r2.setProcState
            r5 = 15
            if (r4 < r5) goto L_0x038d
            r2.notCachedSinceIdle = r13
        L_0x038d:
            if (r22 != 0) goto L_0x039b
            com.android.server.am.ActivityManagerService r4 = r1.mService
            com.android.server.am.ProcessStatsService r5 = r4.mProcessStats
            int r5 = r5.getMemFactorLocked()
            r4.setProcessTrackerStateLocked(r2, r5, r9)
            goto L_0x039e
        L_0x039b:
            r4 = 1
            r2.procStateChanged = r4
        L_0x039e:
            goto L_0x03ca
        L_0x039f:
            boolean r0 = r2.reportedInteraction
            if (r0 == 0) goto L_0x03b5
            long r3 = r21.getInteractionEventTime()
            long r3 = r11 - r3
            com.android.server.am.ActivityManagerConstants r0 = r1.mConstants
            long r5 = r0.USAGE_STATS_INTERACTION_INTERVAL
            int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r0 <= 0) goto L_0x03b5
            r1.maybeUpdateUsageStatsLocked(r2, r11)
            goto L_0x03ca
        L_0x03b5:
            boolean r0 = r2.reportedInteraction
            if (r0 != 0) goto L_0x03ca
            long r3 = r21.getFgInteractionTime()
            long r3 = r11 - r3
            com.android.server.am.ActivityManagerConstants r0 = r1.mConstants
            long r5 = r0.SERVICE_USAGE_INTERACTION_TIME
            int r0 = (r3 > r5 ? 1 : (r3 == r5 ? 0 : -1))
            if (r0 <= 0) goto L_0x03ca
            r1.maybeUpdateUsageStatsLocked(r2, r11)
        L_0x03ca:
            if (r17 == 0) goto L_0x03e1
            com.android.server.am.ActivityManagerService r0 = r1.mService
            int r3 = r2.pid
            android.content.pm.ApplicationInfo r4 = r2.info
            int r4 = r4.uid
            com.android.server.am.ActivityManagerService$ProcessChangeItem r0 = r0.enqueueProcessChangeItemLocked(r3, r4)
            r5 = r17
            r0.changes = r5
            boolean r3 = r2.repForegroundActivities
            r0.foregroundActivities = r3
            goto L_0x03e3
        L_0x03e1:
            r5 = r17
        L_0x03e3:
            return r18
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.OomAdjuster.applyOomAdjLocked(com.android.server.am.ProcessRecord, boolean, long, long):boolean");
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void maybeUpdateUsageStats(ProcessRecord app, long nowElapsed) {
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                maybeUpdateUsageStatsLocked(app, nowElapsed);
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    @GuardedBy({"mService"})
    private void maybeUpdateUsageStatsLocked(ProcessRecord app, long nowElapsed) {
        boolean isInteraction;
        if (this.mService.mUsageStatsService != null) {
            if (app.getCurProcState() <= 2 || app.getCurProcState() == 4) {
                isInteraction = true;
                app.setFgInteractionTime(0);
            } else {
                boolean z = false;
                if (app.getCurProcState() > 5) {
                    if (app.getCurProcState() <= 7) {
                        z = true;
                    }
                    isInteraction = z;
                    app.setFgInteractionTime(0);
                } else if (app.getFgInteractionTime() == 0) {
                    app.setFgInteractionTime(nowElapsed);
                    isInteraction = false;
                } else {
                    if (nowElapsed > app.getFgInteractionTime() + this.mConstants.SERVICE_USAGE_INTERACTION_TIME) {
                        z = true;
                    }
                    isInteraction = z;
                }
            }
            if (isInteraction && (!app.reportedInteraction || nowElapsed - app.getInteractionEventTime() > this.mConstants.USAGE_STATS_INTERACTION_INTERVAL)) {
                app.setInteractionEventTime(nowElapsed);
                String[] packages = app.getPackageList();
                if (packages != null) {
                    for (String reportEvent : packages) {
                        this.mService.mUsageStatsService.reportEvent(reportEvent, app.userId, 6);
                    }
                }
            }
            app.reportedInteraction = isInteraction;
            if (!isInteraction) {
                app.setInteractionEventTime(0);
            }
        }
    }

    private void maybeUpdateLastTopTime(ProcessRecord app, long nowUptime) {
        if (app.setProcState <= 2 && app.getCurProcState() > 2) {
            app.lastTopTime = nowUptime;
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public void idleUidsLocked() {
        int N = this.mActiveUids.size();
        if (N > 0) {
            long nowElapsed = SystemClock.elapsedRealtime();
            long maxBgTime = nowElapsed - this.mConstants.BACKGROUND_SETTLE_TIME;
            long nextTime = 0;
            PowerManagerInternal powerManagerInternal = this.mLocalPowerManager;
            if (powerManagerInternal != null) {
                powerManagerInternal.startUidChanges();
            }
            for (int i = N - 1; i >= 0; i--) {
                UidRecord uidRec = this.mActiveUids.valueAt(i);
                long bgTime = uidRec.lastBackgroundTime;
                if (bgTime > 0 && !uidRec.idle) {
                    if (bgTime <= maxBgTime) {
                        EventLogTags.writeAmUidIdle(uidRec.uid);
                        uidRec.idle = true;
                        uidRec.setIdle = true;
                        this.mService.doStopUidLocked(uidRec.uid, uidRec);
                    } else if (nextTime == 0 || nextTime > bgTime) {
                        nextTime = bgTime;
                    }
                }
            }
            PowerManagerInternal powerManagerInternal2 = this.mLocalPowerManager;
            if (powerManagerInternal2 != null) {
                powerManagerInternal2.finishUidChanges();
            }
            if (nextTime > 0) {
                this.mService.mHandler.removeMessages(58);
                this.mService.mHandler.sendEmptyMessageDelayed(58, (this.mConstants.BACKGROUND_SETTLE_TIME + nextTime) - nowElapsed);
            }
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public final void setAppIdTempWhitelistStateLocked(int appId, boolean onWhitelist) {
        boolean changed = false;
        for (int i = this.mActiveUids.size() - 1; i >= 0; i--) {
            UidRecord uidRec = this.mActiveUids.valueAt(i);
            if (UserHandle.getAppId(uidRec.uid) == appId && uidRec.curWhitelist != onWhitelist) {
                uidRec.curWhitelist = onWhitelist;
                changed = true;
            }
        }
        if (changed) {
            updateOomAdjLocked(OOM_ADJ_REASON_WHITELIST);
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public final void setUidTempWhitelistStateLocked(int uid, boolean onWhitelist) {
        UidRecord uidRec = this.mActiveUids.get(uid);
        if (uidRec != null && uidRec.curWhitelist != onWhitelist) {
            uidRec.curWhitelist = onWhitelist;
            updateOomAdjLocked(OOM_ADJ_REASON_WHITELIST);
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public void dumpProcessListVariablesLocked(ProtoOutputStream proto) {
        proto.write(1120986464305L, this.mAdjSeq);
        proto.write(1120986464306L, this.mProcessList.mLruSeq);
        proto.write(1120986464307L, this.mNumNonCachedProcs);
        proto.write(1120986464309L, this.mNumServiceProcs);
        proto.write(1120986464310L, this.mNewNumServiceProcs);
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public void dumpSequenceNumbersLocked(PrintWriter pw) {
        pw.println("  mAdjSeq=" + this.mAdjSeq + " mLruSeq=" + this.mProcessList.mLruSeq);
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public void dumpProcCountsLocked(PrintWriter pw) {
        pw.println("  mNumNonCachedProcs=" + this.mNumNonCachedProcs + " (" + this.mProcessList.getLruSizeLocked() + " total) mNumCachedHiddenProcs=" + this.mNumCachedHiddenProcs + " mNumServiceProcs=" + this.mNumServiceProcs + " mNewNumServiceProcs=" + this.mNewNumServiceProcs);
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public void dumpAppCompactorSettings(PrintWriter pw) {
        this.mAppCompact.dump(pw);
    }
}
