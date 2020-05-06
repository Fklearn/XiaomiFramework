package com.android.server.pm;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageParser;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.ArraySet;
import android.util.Log;
import com.android.server.ServiceThread;
import com.miui.whetstone.ReflectionUtils;
import dalvik.system.VMRuntime;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.os.IMiuiDexoptObserver;
import miui.os.SystemProperties;

public class PackageDexOptimizerManager {
    private static final int DEFAULT_BOOTDEXOPT_DATA_APPS_THRESHOLD = 10;
    private static final String[] DEXOPT_WHITELIST = {"com.eg.android.AlipayGphone", "com.tencent.mm"};
    private static final int START_DEXOPT_MSG = 1;
    private static final String TAG = "PackageDexOptimizerManager";
    /* access modifiers changed from: private */
    public static Method sPerformDexOptMethod;
    private static final String sPreferredInstructionSet = VMRuntime.getInstructionSet(Build.SUPPORTED_ABIS[0]);
    private final Handler mHandler;
    private final ServiceThread mHandlerThread;
    final ArraySet<String> mPendingDexOpt;

    static {
        Class<PackageManagerService> cls = PackageManagerService.class;
        try {
            sPerformDexOptMethod = ReflectionUtils.findMethodBestMatch(cls, "performDexOpt", new Class[]{String.class, String.class, Boolean.TYPE, Integer.TYPE});
        } catch (NoSuchMethodError e) {
            Class<PackageManagerService> cls2 = PackageManagerService.class;
            try {
                sPerformDexOptMethod = ReflectionUtils.findMethodBestMatch(cls2, "performDexOpt", new Class[]{String.class, String.class, Boolean.TYPE});
            } catch (NoSuchMethodError e2) {
                sPerformDexOptMethod = null;
            }
        }
    }

    private static final class Holder {
        static PackageDexOptimizerManager INSTANCE = new PackageDexOptimizerManager();

        private Holder() {
        }
    }

    private PackageDexOptimizerManager() {
        this.mPendingDexOpt = new ArraySet<>();
        this.mHandlerThread = new ServiceThread(TAG, 10, true);
        this.mHandlerThread.start();
        this.mHandler = new DexOptHandler(this.mHandlerThread.getLooper());
    }

    public static PackageDexOptimizerManager getInstance() {
        return Holder.INSTANCE;
    }

    private static boolean isSystemPackage(PackageParser.Package pkg) {
        return (pkg.applicationInfo.flags & 1) != 0;
    }

    static List<String> getRecentlyUsedPackages(Context context, int days) {
        List<UsageStats> sortedStatsList = getSortedUsageStatsByForegroundTime(context, days);
        if (sortedStatsList == null || sortedStatsList.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> pkgs = new ArrayList<>();
        for (UsageStats usage : sortedStatsList) {
            if (usage.getTotalTimeInForeground() > 0) {
                pkgs.add(usage.getPackageName());
            }
        }
        return pkgs;
    }

    static List<UsageStats> getSortedUsageStatsByForegroundTime(Context context, int days) {
        long now = System.currentTimeMillis();
        Map<String, UsageStats> statsMap = ((UsageStatsManager) context.getSystemService("usagestats")).queryAndAggregateUsageStats(now - ((long) ((((days * 24) * 60) * 60) * 1000)), now);
        List<UsageStats> entryList = null;
        if (statsMap != null && !statsMap.isEmpty()) {
            entryList = new ArrayList<>();
            for (Map.Entry<String, UsageStats> entry : statsMap.entrySet()) {
                entryList.add(entry.getValue());
            }
            Collections.sort(entryList, new Comparator<UsageStats>() {
                public int compare(UsageStats left, UsageStats right) {
                    return Long.signum(right.getTotalTimeInForeground() - left.getTotalTimeInForeground());
                }
            });
        }
        return entryList;
    }

    static List<String> getDefaultMostUsagePackageList(PackageManagerService pms) {
        List<UsageStats> sortedStatsList = getSortedUsageStatsByForegroundTime(pms.mContext, 7);
        if (sortedStatsList == null || sortedStatsList.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> mostUsagePackages = new ArrayList<>();
        int threshold = SystemProperties.getInt("persist.sys.dexopt_threshold", 10);
        synchronized (pms.mPackages) {
            for (String packageName : DEXOPT_WHITELIST) {
                if (pms.mPackages.get(packageName) != null) {
                    Log.i(TAG, "Found default Package: " + packageName);
                    mostUsagePackages.add(packageName);
                    threshold += -1;
                }
            }
        }
        Iterator<UsageStats> it = sortedStatsList.iterator();
        while (it.hasNext() && threshold > 0) {
            String packageName2 = it.next().getPackageName();
            synchronized (pms.mPackages) {
                PackageParser.Package pkg = pms.mPackages.get(packageName2);
                if (pkg != null) {
                    if (!isSystemPackage(pkg)) {
                        if (!mostUsagePackages.contains(packageName2)) {
                            Log.i(TAG, "Found most usage Package: " + packageName2);
                            mostUsagePackages.add(packageName2);
                            threshold += -1;
                        }
                    }
                }
                it.remove();
            }
        }
        return mostUsagePackages;
    }

    private static String getPrimaryInstructionSet(ApplicationInfo info) {
        if (info.primaryCpuAbi == null) {
            return sPreferredInstructionSet;
        }
        return VMRuntime.getInstructionSet(info.primaryCpuAbi);
    }

    public boolean ensurePackageDexOpt(String packageName) {
        return ensurePackageDexOpt(packageName, (IMiuiDexoptObserver) null);
    }

    public boolean ensurePackageDexOpt(String packageName, IMiuiDexoptObserver obs) {
        if (!((PackageManagerService) ServiceManager.getService(Settings.ATTR_PACKAGE)).getOptimizablePackages().contains(packageName)) {
            return false;
        }
        synchronized (this.mPendingDexOpt) {
            if (this.mPendingDexOpt.contains(packageName)) {
                return true;
            }
            this.mPendingDexOpt.add(packageName);
            this.mHandler.sendMessageAtFrontOfQueue(this.mHandler.obtainMessage(1, new AppDexoptInfo(packageName, obs)));
            return true;
        }
    }

    class AppDexoptInfo {
        IMiuiDexoptObserver observer;
        String packageName;
        int returnCode = 0;

        AppDexoptInfo(String name, IMiuiDexoptObserver obs) {
            this.packageName = name;
            this.observer = obs;
        }
    }

    class DexOptHandler extends Handler {
        DexOptHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                AppDexoptInfo info = (AppDexoptInfo) msg.obj;
                if (info.observer != null) {
                    try {
                        info.observer.onStart(info.packageName);
                    } catch (RemoteException e) {
                    }
                }
                performDexOptInBackground(info);
                if (info.observer != null) {
                    try {
                        info.observer.onFinished(info.packageName, info.returnCode);
                    } catch (RemoteException e2) {
                    }
                }
            }
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
            	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
            	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
            	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
            	at jadx.core.dex.visitors.regions.RegionMaker.processHandlersOutBlocks(RegionMaker.java:1008)
            	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:978)
            	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:52)
            */
        private void performDexOptInBackground(com.android.server.pm.PackageDexOptimizerManager.AppDexoptInfo r15) {
            /*
                r14 = this;
                com.android.server.pm.PackageDexOptimizerManager r0 = com.android.server.pm.PackageDexOptimizerManager.this
                android.util.ArraySet<java.lang.String> r0 = r0.mPendingDexOpt
                monitor-enter(r0)
                com.android.server.pm.PackageDexOptimizerManager r1 = com.android.server.pm.PackageDexOptimizerManager.this     // Catch:{ all -> 0x00df }
                android.util.ArraySet<java.lang.String> r1 = r1.mPendingDexOpt     // Catch:{ all -> 0x00df }
                java.lang.String r2 = r15.packageName     // Catch:{ all -> 0x00df }
                boolean r1 = r1.contains(r2)     // Catch:{ all -> 0x00df }
                if (r1 != 0) goto L_0x0013
                monitor-exit(r0)     // Catch:{ all -> 0x00df }
                return
            L_0x0013:
                monitor-exit(r0)     // Catch:{ all -> 0x00df }
                java.lang.String r0 = "package"
                android.os.IBinder r0 = android.os.ServiceManager.getService(r0)
                com.android.server.pm.PackageManagerService r0 = (com.android.server.pm.PackageManagerService) r0
                java.lang.StringBuilder r1 = new java.lang.StringBuilder
                r1.<init>()
                java.lang.String r2 = "PerformDexOpt for "
                r1.append(r2)
                java.lang.String r2 = r15.packageName
                r1.append(r2)
                java.lang.String r1 = r1.toString()
                java.lang.String r2 = "PackageDexOptimizerManager"
                android.util.Log.i(r2, r1)
                long r1 = android.os.Binder.clearCallingIdentity()
                r3 = -2147483648(0xffffffff80000000, float:-0.0)
                r4 = -1
                java.lang.reflect.Method r5 = com.android.server.pm.PackageDexOptimizerManager.sPerformDexOptMethod     // Catch:{ Exception -> 0x00ad }
                if (r5 == 0) goto L_0x00a6
                r5 = 3
                r6 = 2
                r7 = 0
                r8 = 0
                r9 = 1
                java.lang.reflect.Method r10 = com.android.server.pm.PackageDexOptimizerManager.sPerformDexOptMethod     // Catch:{ IllegalArgumentException -> 0x006a }
                r11 = 4
                java.lang.Object[] r11 = new java.lang.Object[r11]     // Catch:{ IllegalArgumentException -> 0x006a }
                java.lang.String r12 = r15.packageName     // Catch:{ IllegalArgumentException -> 0x006a }
                r11[r8] = r12     // Catch:{ IllegalArgumentException -> 0x006a }
                r11[r9] = r7     // Catch:{ IllegalArgumentException -> 0x006a }
                java.lang.Boolean r12 = java.lang.Boolean.valueOf(r9)     // Catch:{ IllegalArgumentException -> 0x006a }
                r11[r6] = r12     // Catch:{ IllegalArgumentException -> 0x006a }
                r12 = -2147483648(0xffffffff80000000, float:-0.0)
                java.lang.Integer r12 = java.lang.Integer.valueOf(r12)     // Catch:{ IllegalArgumentException -> 0x006a }
                r11[r5] = r12     // Catch:{ IllegalArgumentException -> 0x006a }
                java.lang.Object r10 = r10.invoke(r0, r11)     // Catch:{ IllegalArgumentException -> 0x006a }
                java.lang.Boolean r10 = (java.lang.Boolean) r10     // Catch:{ IllegalArgumentException -> 0x006a }
                r5 = r10
                goto L_0x0099
            L_0x006a:
                r10 = move-exception
                java.lang.String r11 = "PackageDexOptimizerManager"
                java.lang.StringBuilder r12 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x00ad }
                r12.<init>()     // Catch:{ Exception -> 0x00ad }
                java.lang.String r13 = "IllegalArgumentException: "
                r12.append(r13)     // Catch:{ Exception -> 0x00ad }
                r12.append(r10)     // Catch:{ Exception -> 0x00ad }
                java.lang.String r12 = r12.toString()     // Catch:{ Exception -> 0x00ad }
                android.util.Log.d(r11, r12)     // Catch:{ Exception -> 0x00ad }
                java.lang.reflect.Method r11 = com.android.server.pm.PackageDexOptimizerManager.sPerformDexOptMethod     // Catch:{ Exception -> 0x00ad }
                java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x00ad }
                java.lang.String r12 = r15.packageName     // Catch:{ Exception -> 0x00ad }
                r5[r8] = r12     // Catch:{ Exception -> 0x00ad }
                r5[r9] = r7     // Catch:{ Exception -> 0x00ad }
                java.lang.Boolean r7 = java.lang.Boolean.valueOf(r9)     // Catch:{ Exception -> 0x00ad }
                r5[r6] = r7     // Catch:{ Exception -> 0x00ad }
                java.lang.Object r5 = r11.invoke(r0, r5)     // Catch:{ Exception -> 0x00ad }
                java.lang.Boolean r5 = (java.lang.Boolean) r5     // Catch:{ Exception -> 0x00ad }
            L_0x0099:
                if (r5 == 0) goto L_0x00a4
                boolean r6 = r5.booleanValue()     // Catch:{ Exception -> 0x00ad }
                if (r6 == 0) goto L_0x00a4
                r15.returnCode = r9     // Catch:{ Exception -> 0x00ad }
                goto L_0x00a6
            L_0x00a4:
                r15.returnCode = r4     // Catch:{ Exception -> 0x00ad }
            L_0x00a6:
            L_0x00a7:
                android.os.Binder.restoreCallingIdentity(r1)
                goto L_0x00c8
            L_0x00ab:
                r3 = move-exception
                goto L_0x00db
            L_0x00ad:
                r3 = move-exception
                r15.returnCode = r4     // Catch:{ all -> 0x00ab }
                java.lang.String r4 = "PackageDexOptimizerManager"
                java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x00ab }
                r5.<init>()     // Catch:{ all -> 0x00ab }
                java.lang.String r6 = "Exception "
                r5.append(r6)     // Catch:{ all -> 0x00ab }
                r5.append(r3)     // Catch:{ all -> 0x00ab }
                java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x00ab }
                android.util.Log.e(r4, r5)     // Catch:{ all -> 0x00ab }
                goto L_0x00a7
            L_0x00c8:
                com.android.server.pm.PackageDexOptimizerManager r3 = com.android.server.pm.PackageDexOptimizerManager.this
                android.util.ArraySet<java.lang.String> r3 = r3.mPendingDexOpt
                monitor-enter(r3)
                com.android.server.pm.PackageDexOptimizerManager r4 = com.android.server.pm.PackageDexOptimizerManager.this     // Catch:{ all -> 0x00d8 }
                android.util.ArraySet<java.lang.String> r4 = r4.mPendingDexOpt     // Catch:{ all -> 0x00d8 }
                java.lang.String r5 = r15.packageName     // Catch:{ all -> 0x00d8 }
                r4.remove(r5)     // Catch:{ all -> 0x00d8 }
                monitor-exit(r3)     // Catch:{ all -> 0x00d8 }
                return
            L_0x00d8:
                r4 = move-exception
                monitor-exit(r3)     // Catch:{ all -> 0x00d8 }
                throw r4
            L_0x00db:
                android.os.Binder.restoreCallingIdentity(r1)
                throw r3
            L_0x00df:
                r1 = move-exception
                monitor-exit(r0)     // Catch:{ all -> 0x00df }
                throw r1
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.PackageDexOptimizerManager.DexOptHandler.performDexOptInBackground(com.android.server.pm.PackageDexOptimizerManager$AppDexoptInfo):void");
        }
    }

    public static boolean isAlwaysSpeedDexOpt(PackageParser.Package pkg) {
        return false;
    }
}
