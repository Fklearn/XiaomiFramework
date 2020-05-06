package com.android.server.am;

import android.content.Context;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.spc.MemoryCleanInfo;
import android.os.spc.PressureStateSettings;
import android.util.ArrayMap;
import android.util.EventLog;
import android.util.Log;
import com.android.internal.app.ProcessMap;
import com.android.server.am.ProcessPolicy;
import com.miui.server.SystemPressureController;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import miui.process.ProcessConfig;

public class ProcessMemoryCleaner {
    public static final boolean DEBUG = PressureStateSettings.DEBUG_ALL;
    public static final int KEYGUARD_SHOWN = 17;
    public static final long[] MEM_LVL_PSS_LIMIT_KB = new long[2];
    public static final int MOVE_TO_FOREGROUND = 1;
    public static final int PROC_MEMORY_LEVEL_1 = 0;
    public static final int PROC_MEMORY_LEVEL_2 = 1;
    public static final int PROC_MEMORY_LEVEL_COUNT = 2;
    public static final int PROC_MEMORY_LEVEL_MIN = -1;
    public static final String TAG = "ProcessMemoryCleaner";
    private static final Comparator<AppInfo> USAGE_ADJ_DESC_COMPARATOR = new Comparator<AppInfo>() {
        public int compare(AppInfo t1, AppInfo t2) {
            int adjRes = t2.usageAdj - t1.usageAdj;
            if (adjRes != 0) {
                return adjRes;
            }
            long pssRes = t2.pss - t1.pss;
            if (pssRes < 0) {
                return -1;
            }
            if (pssRes > 0) {
                return 1;
            }
            return 0;
        }
    };
    private ActivityManagerService mAMS;
    private ProcessMap<Long> mAppTotalPss = new ProcessMap<>();
    private Context mContext;
    private ArrayList<ProcessRecord>[] mMemLvlProcessList = new ArrayList[2];
    private ProcessManagerService mPMS;
    private ProcessList mProcList;
    private AppUsageStatsManager mUsageStatsManager = AppUsageStatsManager.getInstance();

    static {
        MEM_LVL_PSS_LIMIT_KB[0] = PressureStateSettings.PROC_MEM_LVL1_PSS_LIMIT_KB;
        MEM_LVL_PSS_LIMIT_KB[1] = PressureStateSettings.PROC_MEM_LVL2_PSS_LIMIT_KB;
    }

    public ProcessMemoryCleaner(ActivityManagerService ams) {
        this.mAMS = ams;
        this.mProcList = ams.mProcessList;
        this.mMemLvlProcessList[0] = new ArrayList<>();
        this.mMemLvlProcessList[1] = new ArrayList<>();
    }

    public void start(Context context) {
        this.mContext = context;
        this.mPMS = (ProcessManagerService) ServiceManager.getService("ProcessManager");
        AppUsageStatsManager appUsageStatsManager = this.mUsageStatsManager;
        if (appUsageStatsManager != null) {
            appUsageStatsManager.setEnabledSpeedTestProtect(PressureStateSettings.getProcessCleanerSetting(PressureStateSettings.ProcCleanerSettingFlags.ENABLED_SPEED_TEST_PROTECT) > 0);
        }
    }

    /* JADX INFO: finally extract failed */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x01bd, code lost:
        r0 = th;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void scanProcessAndCleanUpMemory(long r20, android.os.spc.MemoryCleanInfo r22, int r23) {
        /*
            r19 = this;
            r7 = r19
            r8 = r23
            com.android.server.am.ProcessManagerService r0 = r7.mPMS
            if (r0 == 0) goto L_0x01bf
            android.content.Context r1 = r7.mContext
            if (r1 != 0) goto L_0x0010
            r2 = r22
            goto L_0x01c1
        L_0x0010:
            com.android.server.am.ProcessPolicy r0 = r0.getProcessPolicy()
            android.content.Context r1 = r7.mContext
            int r2 = android.os.UserHandle.getCallingUserId()
            java.util.HashMap r9 = r0.updateDynamicWhiteList(r1, r2)
            com.android.server.am.ProcessManagerService r0 = r7.mPMS
            com.android.server.am.ProcessPolicy r0 = r0.getProcessPolicy()
            r1 = 3
            java.util.List r10 = r0.getActiveUidRecordList(r1)
            java.util.HashSet r0 = new java.util.HashSet
            r0.<init>()
            r11 = r0
            if (r9 == 0) goto L_0x0038
            java.util.Set r0 = r9.keySet()
            r11.addAll(r0)
        L_0x0038:
            com.android.server.am.ActivityManagerService r1 = r7.mAMS
            monitor-enter(r1)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x01b5 }
            long r2 = android.os.SystemClock.uptimeMillis()     // Catch:{ all -> 0x01b5 }
            com.android.server.am.ProcessList r0 = r7.mProcList     // Catch:{ all -> 0x01b5 }
            java.util.ArrayList<com.android.server.am.ProcessRecord> r0 = r0.mLruProcesses     // Catch:{ all -> 0x01b5 }
            int r0 = r0.size()     // Catch:{ all -> 0x01b5 }
            r4 = 0
        L_0x004b:
            r12 = 2
            if (r4 >= r0) goto L_0x00a2
            com.android.server.am.ProcessList r5 = r7.mProcList     // Catch:{ all -> 0x01b5 }
            java.util.ArrayList<com.android.server.am.ProcessRecord> r5 = r5.mLruProcesses     // Catch:{ all -> 0x01b5 }
            java.lang.Object r5 = r5.get(r4)     // Catch:{ all -> 0x01b5 }
            com.android.server.am.ProcessRecord r5 = (com.android.server.am.ProcessRecord) r5     // Catch:{ all -> 0x01b5 }
            r7.updateAppTotalPss(r5)     // Catch:{ all -> 0x01b5 }
            int r6 = r5.setAdj     // Catch:{ all -> 0x01b5 }
            if (r6 != 0) goto L_0x0067
            android.content.pm.ApplicationInfo r6 = r5.info     // Catch:{ all -> 0x01b5 }
            java.lang.String r6 = r6.packageName     // Catch:{ all -> 0x01b5 }
            r11.add(r6)     // Catch:{ all -> 0x01b5 }
            goto L_0x009f
        L_0x0067:
            int r6 = r5.setAdj     // Catch:{ all -> 0x01b5 }
            int r6 = oomAdjToMemLvl(r6)     // Catch:{ all -> 0x01b5 }
            if (r6 < 0) goto L_0x009f
            if (r6 >= r12) goto L_0x009f
            java.util.ArrayList<com.android.server.am.ProcessRecord>[] r12 = r7.mMemLvlProcessList     // Catch:{ all -> 0x01b5 }
            r12 = r12[r6]     // Catch:{ all -> 0x01b5 }
            r12.add(r5)     // Catch:{ all -> 0x01b5 }
            boolean r12 = r5.hasForegroundActivities()     // Catch:{ all -> 0x01b5 }
            if (r12 != 0) goto L_0x0085
            boolean r12 = r5.hasForegroundServices()     // Catch:{ all -> 0x01b5 }
            if (r12 == 0) goto L_0x008c
        L_0x0085:
            android.content.pm.ApplicationInfo r12 = r5.info     // Catch:{ all -> 0x01b5 }
            java.lang.String r12 = r12.packageName     // Catch:{ all -> 0x01b5 }
            r11.add(r12)     // Catch:{ all -> 0x01b5 }
        L_0x008c:
            int r12 = r5.uid     // Catch:{ all -> 0x01b5 }
            com.android.server.am.ProcessPolicy$ActiveUidRecord r12 = findProcActiveInfo(r12, r10)     // Catch:{ all -> 0x01b5 }
            if (r12 == 0) goto L_0x009f
            int r13 = r12.flag     // Catch:{ all -> 0x01b5 }
            if (r13 == 0) goto L_0x009f
            android.content.pm.ApplicationInfo r13 = r5.info     // Catch:{ all -> 0x01b5 }
            java.lang.String r13 = r13.packageName     // Catch:{ all -> 0x01b5 }
            r11.add(r13)     // Catch:{ all -> 0x01b5 }
        L_0x009f:
            int r4 = r4 + 1
            goto L_0x004b
        L_0x00a2:
            boolean r4 = DEBUG     // Catch:{ all -> 0x01b5 }
            r13 = 0
            r14 = 1
            if (r4 == 0) goto L_0x013b
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x01b5 }
            r4.<init>()     // Catch:{ all -> 0x01b5 }
            java.util.Set r5 = r9.keySet()     // Catch:{ all -> 0x01b5 }
            java.util.Iterator r5 = r5.iterator()     // Catch:{ all -> 0x01b5 }
        L_0x00b5:
            boolean r6 = r5.hasNext()     // Catch:{ all -> 0x01b5 }
            if (r6 == 0) goto L_0x00cb
            java.lang.Object r6 = r5.next()     // Catch:{ all -> 0x01b5 }
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ all -> 0x01b5 }
            r4.append(r6)     // Catch:{ all -> 0x01b5 }
            java.lang.String r15 = ","
            r4.append(r15)     // Catch:{ all -> 0x01b5 }
            goto L_0x00b5
        L_0x00cb:
            java.lang.String r5 = "ProcessMemoryCleaner"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x01b5 }
            r6.<init>()     // Catch:{ all -> 0x01b5 }
            java.lang.String r15 = "proc whitelist: "
            r6.append(r15)     // Catch:{ all -> 0x01b5 }
            java.lang.String r15 = r4.toString()     // Catch:{ all -> 0x01b5 }
            r6.append(r15)     // Catch:{ all -> 0x01b5 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x01b5 }
            android.util.Log.d(r5, r6)     // Catch:{ all -> 0x01b5 }
            int r5 = r4.length()     // Catch:{ all -> 0x01b5 }
            r4.delete(r13, r5)     // Catch:{ all -> 0x01b5 }
            java.util.Iterator r5 = r11.iterator()     // Catch:{ all -> 0x01b5 }
        L_0x00f1:
            boolean r6 = r5.hasNext()     // Catch:{ all -> 0x01b5 }
            if (r6 == 0) goto L_0x0107
            java.lang.Object r6 = r5.next()     // Catch:{ all -> 0x01b5 }
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ all -> 0x01b5 }
            r4.append(r6)     // Catch:{ all -> 0x01b5 }
            java.lang.String r15 = ","
            r4.append(r15)     // Catch:{ all -> 0x01b5 }
            goto L_0x00f1
        L_0x0107:
            java.lang.String r5 = "ProcessMemoryCleaner"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x01b5 }
            r6.<init>()     // Catch:{ all -> 0x01b5 }
            java.lang.String r15 = "active packages: "
            r6.append(r15)     // Catch:{ all -> 0x01b5 }
            java.lang.String r15 = r4.toString()     // Catch:{ all -> 0x01b5 }
            r6.append(r15)     // Catch:{ all -> 0x01b5 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x01b5 }
            android.util.Log.d(r5, r6)     // Catch:{ all -> 0x01b5 }
            java.lang.String r5 = "ProcessMemoryCleaner"
            java.lang.String r6 = "scanProcessAndCleanUpMemory used time: %s"
            java.lang.Object[] r15 = new java.lang.Object[r14]     // Catch:{ all -> 0x01b5 }
            long r16 = android.os.SystemClock.uptimeMillis()     // Catch:{ all -> 0x01b5 }
            long r16 = r16 - r2
            java.lang.Long r16 = java.lang.Long.valueOf(r16)     // Catch:{ all -> 0x01b5 }
            r15[r13] = r16     // Catch:{ all -> 0x01b5 }
            java.lang.String r6 = java.lang.String.format(r6, r15)     // Catch:{ all -> 0x01b5 }
            android.util.Log.d(r5, r6)     // Catch:{ all -> 0x01b5 }
        L_0x013b:
            com.android.server.am.ActivityManagerService r4 = r7.mAMS     // Catch:{ all -> 0x01b5 }
            java.lang.String r5 = "scanProcessAndCleanUpMemory"
            r4.checkTime(r2, r5)     // Catch:{ all -> 0x01b5 }
            monitor-exit(r1)     // Catch:{ all -> 0x01b5 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            r0 = 0
            if (r8 > r14) goto L_0x0173
            r5 = r20
            java.util.List r0 = r7.updateKillingTargetAppsInMemLvl(r14, r5, r11)
            if (r0 == 0) goto L_0x0170
            int r1 = r0.size()
            if (r1 == 0) goto L_0x0170
            boolean r1 = DEBUG
            if (r1 == 0) goto L_0x0161
            java.lang.String r1 = "cache"
            r7.printAppInfoList(r1, r0)
        L_0x0161:
            r15 = 0
            r1 = r19
            r2 = r0
            r3 = r20
            r5 = r15
            r6 = r22
            long r1 = r1.cleanUpApps(r2, r3, r5, r6)
            r5 = r1
            goto L_0x0175
        L_0x0170:
            r5 = r20
            goto L_0x0175
        L_0x0173:
            r5 = r20
        L_0x0175:
            r1 = 0
            if (r8 > 0) goto L_0x01a0
            java.util.List r15 = r7.updateKillingTargetAppsInMemLvl(r13, r5, r11)
            if (r15 == 0) goto L_0x019d
            int r1 = r15.size()
            if (r1 == 0) goto L_0x019d
            boolean r1 = DEBUG
            if (r1 == 0) goto L_0x018d
            java.lang.String r1 = "available"
            r7.printAppInfoList(r1, r15)
        L_0x018d:
            r16 = 0
            r1 = r19
            r2 = r15
            r3 = r5
            r17 = r5
            r5 = r16
            r6 = r22
            r1.cleanUpApps(r2, r3, r5, r6)
            goto L_0x01a3
        L_0x019d:
            r17 = r5
            goto L_0x01a3
        L_0x01a0:
            r17 = r5
            r15 = r1
        L_0x01a3:
            java.util.List[] r1 = new java.util.List[r12]
            r1[r13] = r0
            r1[r14] = r15
            java.lang.String r1 = r7.toBriefStringAppKillingTargets(r1)
            r2 = r22
            r2.targetListData = r1
            r19.clearProcListInMemLvl()
            return
        L_0x01b5:
            r0 = move-exception
            r2 = r22
        L_0x01b8:
            monitor-exit(r1)     // Catch:{ all -> 0x01bd }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x01bd:
            r0 = move-exception
            goto L_0x01b8
        L_0x01bf:
            r2 = r22
        L_0x01c1:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ProcessMemoryCleaner.scanProcessAndCleanUpMemory(long, android.os.spc.MemoryCleanInfo, int):void");
    }

    private class AppInfo {
        public String packageName;
        public long pss;
        public int usageAdj;

        private AppInfo() {
        }
    }

    private List<AppInfo> updateKillingTargetAppsInMemLvl(int memLvl, long needMemory, Set<String> activePackages) {
        Set<String> set = activePackages;
        List<ProcessRecord> procList = this.mMemLvlProcessList[memLvl];
        long totalPssInMemLvl = calcTotalPSSInMemLvl(procList);
        long pssLimit = MEM_LVL_PSS_LIMIT_KB[memLvl];
        if (needMemory <= 5000 || pssLimit == 0 || totalPssInMemLvl < pssLimit) {
            return null;
        }
        Set<String> KillingTargetApps = new HashSet<>();
        List<AppInfo> killingTargetAppsList = new LinkedList<>();
        for (ProcessRecord proc : procList) {
            if (proc.pid != 0 && proc.lastPss >= PressureStateSettings.INTERESTED_PROC_MIN_PSS_KB) {
                String packageName = proc.info.packageName;
                if ((set == null || !set.contains(packageName)) && this.mUsageStatsManager.canForceStopPackage(packageName) && !KillingTargetApps.contains(packageName)) {
                    AppInfo item = new AppInfo();
                    item.pss = queryAppTotalPss(proc);
                    item.packageName = packageName;
                    item.usageAdj = this.mUsageStatsManager.computeUsageAdj(proc.info.packageName);
                    KillingTargetApps.add(packageName);
                    killingTargetAppsList.add(item);
                }
            }
        }
        killingTargetAppsList.sort(USAGE_ADJ_DESC_COMPARATOR);
        return killingTargetAppsList;
    }

    private long cleanUpApps(List<AppInfo> targetPackages, long neededMemSizeKB, int cleanAdj, MemoryCleanInfo cInfo) {
        long neededMemSizeKB2;
        ArrayMap<Integer, List<String>> killConfig = new ArrayMap<>(1);
        LinkedList linkedList = new LinkedList();
        killConfig.put(100, linkedList);
        try {
            if (this.mPMS != null) {
                neededMemSizeKB2 = neededMemSizeKB;
                for (AppInfo app : targetPackages) {
                    try {
                        if (app.usageAdj >= cleanAdj) {
                            try {
                                linkedList.add(app.packageName);
                                if (this.mPMS.kill(new ProcessConfig(10, 0, killConfig, "memory pressure process cleaner"))) {
                                    neededMemSizeKB2 -= app.pss;
                                    try {
                                        cInfo.killedApps.addAll(linkedList);
                                        this.mUsageStatsManager.reportAppKilledByProcessCleaner(app.packageName, SystemClock.elapsedRealtime());
                                    } catch (Exception e) {
                                        e = e;
                                        e.printStackTrace();
                                        return neededMemSizeKB2;
                                    }
                                } else {
                                    MemoryCleanInfo memoryCleanInfo = cInfo;
                                }
                                EventLog.writeEvent(SystemPressureController.PROC_CLEAN_CODE, new Object[]{app.packageName, Long.valueOf(app.pss), Integer.valueOf(app.usageAdj)});
                                if (neededMemSizeKB2 < 0) {
                                    return neededMemSizeKB2;
                                }
                                linkedList.clear();
                            } catch (Exception e2) {
                                e = e2;
                                MemoryCleanInfo memoryCleanInfo2 = cInfo;
                                e.printStackTrace();
                                return neededMemSizeKB2;
                            }
                        }
                    } catch (Exception e3) {
                        e = e3;
                        int i = cleanAdj;
                        MemoryCleanInfo memoryCleanInfo22 = cInfo;
                        e.printStackTrace();
                        return neededMemSizeKB2;
                    }
                }
                int i2 = cleanAdj;
                MemoryCleanInfo memoryCleanInfo3 = cInfo;
                return neededMemSizeKB2;
            }
            int i3 = cleanAdj;
            MemoryCleanInfo memoryCleanInfo4 = cInfo;
            return neededMemSizeKB;
        } catch (Exception e4) {
            e = e4;
            int i4 = cleanAdj;
            MemoryCleanInfo memoryCleanInfo5 = cInfo;
            neededMemSizeKB2 = neededMemSizeKB;
            e.printStackTrace();
            return neededMemSizeKB2;
        }
    }

    private static int oomAdjToMemLvl(int adj) {
        if (adj >= 100 && adj <= 500) {
            return 0;
        }
        if (adj < 800 || adj > 999) {
            return -1;
        }
        return 1;
    }

    private static ProcessPolicy.ActiveUidRecord findProcActiveInfo(int uid, List<ProcessPolicy.ActiveUidRecord> activeUids) {
        if (activeUids == null) {
            return null;
        }
        for (ProcessPolicy.ActiveUidRecord r : activeUids) {
            if (r.uid == uid) {
                return r;
            }
        }
        return null;
    }

    private void updateAppTotalPss(ProcessRecord proc) {
        if (proc != null) {
            Long pss = (Long) this.mAppTotalPss.get(proc.info.packageName, proc.info.uid);
            this.mAppTotalPss.put(proc.info.packageName, proc.info.uid, Long.valueOf(Long.valueOf(pss != null ? pss.longValue() : 0).longValue() + proc.lastPss));
        }
    }

    private long queryAppTotalPss(ProcessRecord proc) {
        Long pss;
        if (proc == null || (pss = (Long) this.mAppTotalPss.get(proc.info.packageName, proc.info.uid)) == null) {
            return 0;
        }
        return pss.longValue();
    }

    private long calcTotalPSSInMemLvl(List<ProcessRecord> procList) {
        long totalPss = 0;
        for (ProcessRecord proc : procList) {
            if (proc.pid > 0) {
                totalPss += proc.lastPss;
            }
        }
        return totalPss;
    }

    private void clearProcListInMemLvl() {
        for (List<ProcessRecord> procList : this.mMemLvlProcessList) {
            procList.clear();
        }
        this.mAppTotalPss.getMap().clear();
    }

    private String toBriefStringAppKillingTargets(List<AppInfo>... targets) {
        if (targets == null || targets.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (List<AppInfo> list : targets) {
            if (list != null) {
                for (AppInfo app : list) {
                    sb.append(app.packageName);
                    sb.append(",");
                    sb.append(app.pss);
                    sb.append(",");
                    sb.append(app.usageAdj);
                    sb.append("|");
                }
            }
        }
        return sb.toString();
    }

    private void printAppInfoList(String name, List<AppInfo> appList) {
        Log.d(TAG, String.format("----------start of %s------------", new Object[]{name}));
        for (AppInfo info : appList) {
            Log.d(TAG, String.format("App (pkg: %s, pss: %s, usageAdj: %s)", new Object[]{info.packageName, Long.valueOf(info.pss), Integer.valueOf(info.usageAdj)}));
        }
    }
}
