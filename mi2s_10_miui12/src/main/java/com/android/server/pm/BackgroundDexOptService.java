package com.android.server.pm;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageParser;
import android.os.Environment;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.storage.StorageManager;
import android.util.ArraySet;
import android.util.Log;
import android.util.StatsLog;
import com.android.internal.util.ArrayUtils;
import com.android.server.LocalServices;
import com.android.server.PinnerService;
import com.android.server.job.controllers.JobStatus;
import com.android.server.pm.dex.DexManager;
import com.android.server.pm.dex.DexoptOptions;
import com.android.server.usage.UnixCalendar;
import com.android.server.usb.descriptors.UsbTerminalTypes;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class BackgroundDexOptService extends JobService {
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    /* access modifiers changed from: private */
    public static boolean DEBUG_BG_DEXOPT = false;
    private static final int DEFAULT_IDLE_OPTIMIZE = 0;
    /* access modifiers changed from: private */
    public static final long IDLE_OPTIMIZATION_PERIOD;
    private static final int JOB_IDLE_OPTIMIZE = 800;
    private static final int JOB_OTA_IDLE_OPTIMIZE = 802;
    private static final int JOB_POST_BOOT_OPTWECHAT = 803;
    private static final int JOB_POST_BOOT_UPDATE = 801;
    private static final int LOW_THRESHOLD_MULTIPLIER_FOR_DOWNGRADE = 2;
    private static final int OPTIMIZE_ABORT_BY_JOB_SCHEDULER = 2;
    private static final int OPTIMIZE_ABORT_NO_SPACE_LEFT = 3;
    private static final int OPTIMIZE_CONTINUE = 1;
    private static final int OPTIMIZE_PROCESSED = 0;
    private static final int OTA_IDLE_OPTIMIZE = 3;
    private static final int POST_BOOT_OPTIMIZE = 1;
    private static final int POST_BOOT_WECHAT_OPTIMIZE = 2;
    private static final String TAG = "BackgroundDexOptService";
    private static final String WECHAT_PKG = "com.tencent.mm";
    /* access modifiers changed from: private */
    public static Context mContext;
    private static final long mDowngradeUnusedAppsThresholdInMillis = getDowngradeUnusedAppsThresholdInMillis();
    private static PackageManagerService mPms;
    private static ArraySet<String> mTop20Pkgs;
    /* access modifiers changed from: private */
    public static ArraySet<String> otaHasOptedPkgs;
    /* access modifiers changed from: private */
    public static ArraySet<String> otaToOptPkgs;
    /* access modifiers changed from: private */
    public static ComponentName sDexoptServiceName = new ComponentName(PackageManagerService.PLATFORM_PACKAGE_NAME, BackgroundDexOptService.class.getName());
    static final ArraySet<String> sFailedPackageNamesPrimary = new ArraySet<>();
    static final ArraySet<String> sFailedPackageNamesSecondary = new ArraySet<>();
    private static final ArrayList<String> sPauseBgDexoptReasons = new ArrayList<>();
    private final AtomicBoolean mAbortIdleOptimization = new AtomicBoolean(false);
    private final AtomicBoolean mAbortOtaIdleOptimization = new AtomicBoolean(false);
    private final AtomicBoolean mAbortPostBootOptWechat = new AtomicBoolean(false);
    private final AtomicBoolean mAbortPostBootUpdate = new AtomicBoolean(false);
    private final File mDataDir = Environment.getDataDirectory();
    private final AtomicBoolean mExitPostBootUpdate = new AtomicBoolean(false);

    static {
        long j;
        if (DEBUG) {
            j = TimeUnit.MINUTES.toMillis(1);
        } else {
            j = TimeUnit.DAYS.toMillis(1);
        }
        IDLE_OPTIMIZATION_PERIOD = j;
    }

    public static void schedule(Context context) {
        if (!isBackgroundDexoptDisabled()) {
            mContext = context;
            boolean is_ota_upgrade = false;
            DEBUG_BG_DEXOPT = SystemProperties.getBoolean("persist.bgdexopt.debug", false);
            if (otaToOptPkgs == null) {
                otaToOptPkgs = new ArraySet<>();
            }
            if (otaHasOptedPkgs == null) {
                otaHasOptedPkgs = new ArraySet<>();
            }
            JobScheduler js = (JobScheduler) context.getSystemService("jobscheduler");
            PackageManagerService packageManagerService = mPms;
            if (packageManagerService != null) {
                is_ota_upgrade = packageManagerService.isDeviceUpgrading();
            }
            if (is_ota_upgrade) {
                js.schedule(new JobInfo.Builder(JOB_OTA_IDLE_OPTIMIZE, sDexoptServiceName).setRequiresDeviceIdle(true).setPeriodic(IDLE_OPTIMIZATION_PERIOD).build());
                js.schedule(new JobInfo.Builder(JOB_POST_BOOT_OPTWECHAT, sDexoptServiceName).setMinimumLatency(TimeUnit.SECONDS.toMillis(10)).setOverrideDeadline(TimeUnit.SECONDS.toMillis(10)).build());
                return;
            }
            js.schedule(new JobInfo.Builder(JOB_POST_BOOT_UPDATE, sDexoptServiceName).setMinimumLatency(TimeUnit.MINUTES.toMillis(1)).setOverrideDeadline(TimeUnit.MINUTES.toMillis(1)).build());
            js.schedule(new JobInfo.Builder(800, sDexoptServiceName).setRequiresDeviceIdle(true).setRequiresCharging(true).setPeriodic(IDLE_OPTIMIZATION_PERIOD).build());
        }
    }

    public static void setPackageManagerService(PackageManagerService pms) {
        mPms = pms;
    }

    private static void updateTop20ThirdPartyPkgs() {
        mTop20Pkgs = new ArraySet<>(20);
        for (UsageStats usage : getRecentlyWeekUsageStats(mContext)) {
            if (mTop20Pkgs.size() >= 20) {
                break;
            }
            String packageName = usage.getPackageName();
            synchronized (mPms.mPackages) {
                PackageParser.Package pkg = mPms.mPackages.get(packageName);
                if (pkg != null) {
                    if (!pkg.isSystem()) {
                        mTop20Pkgs.add(packageName);
                    }
                }
            }
        }
        if (mTop20Pkgs.isEmpty() && DEBUG_BG_DEXOPT) {
            Log.w(TAG, "abnormal! getTop20ThirdPartyPkgs returns empty.");
        }
    }

    private static List<UsageStats> getRecentlyWeekUsageStats(Context context) {
        UsageStatsManager usm = (UsageStatsManager) context.getSystemService("usagestats");
        if (usm == null) {
            return Collections.emptyList();
        }
        long tillTime = System.currentTimeMillis();
        Map<String, UsageStats> statsMap = usm.queryAndAggregateUsageStats(tillTime - UnixCalendar.WEEK_IN_MILLIS, tillTime);
        if (statsMap == null || statsMap.isEmpty()) {
            return Collections.emptyList();
        }
        List<UsageStats> entryList = new ArrayList<>();
        entryList.addAll(statsMap.values());
        Collections.sort(entryList, new Comparator<UsageStats>() {
            public int compare(UsageStats left, UsageStats right) {
                return Long.signum(right.getTotalTimeInForeground() - left.getTotalTimeInForeground());
            }
        });
        return entryList;
    }

    private final ArraySet<String> getOptPkgsByBatteryLevel() {
        int N;
        int lev = getBatteryLevel();
        ArraySet<String> otaPkgs = new ArraySet<>();
        if (lev > 80) {
            N = 15;
        } else if (lev > 20) {
            N = 10;
        } else if (lev > 10) {
            N = 5;
        } else {
            N = 3;
        }
        int i = 0;
        while (i < N && i < mTop20Pkgs.size()) {
            otaPkgs.add(mTop20Pkgs.valueAt(i));
            i++;
        }
        return otaPkgs;
    }

    public static void notifyPackageChanged(String packageName) {
        synchronized (sFailedPackageNamesPrimary) {
            sFailedPackageNamesPrimary.remove(packageName);
        }
        synchronized (sFailedPackageNamesSecondary) {
            sFailedPackageNamesSecondary.remove(packageName);
        }
    }

    public static void pauseBgDexOpt(String reason) {
        synchronized (sPauseBgDexoptReasons) {
            sPauseBgDexoptReasons.add(reason);
        }
    }

    public static void resumeBgDexOpt(String reason) {
        synchronized (sPauseBgDexoptReasons) {
            if (!sPauseBgDexoptReasons.remove(reason)) {
                Log.w(TAG, new RuntimeException("unknown reason: " + reason));
            }
            if (sPauseBgDexoptReasons.isEmpty()) {
                sPauseBgDexoptReasons.notify();
            }
        }
    }

    private static void pauseBgDexOptIfNeed() {
        synchronized (sPauseBgDexoptReasons) {
            while (!sPauseBgDexoptReasons.isEmpty()) {
                Log.d(TAG, "pause dex optimization because of " + Arrays.toString(sPauseBgDexoptReasons.toArray(new String[0])));
                try {
                    sPauseBgDexoptReasons.wait(TimeUnit.SECONDS.toMillis(60));
                } catch (InterruptedException e) {
                }
            }
        }
        SystemClock.sleep(200);
    }

    private int getBatteryLevel() {
        Intent intent = registerReceiver((BroadcastReceiver) null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
        int level = intent.getIntExtra("level", -1);
        int scale = intent.getIntExtra("scale", -1);
        if (!intent.getBooleanExtra("present", true)) {
            return 100;
        }
        if (level < 0 || scale <= 0) {
            return 0;
        }
        return (level * 100) / scale;
    }

    private long getLowStorageThreshold(Context context) {
        long lowThreshold = StorageManager.from(context).getStorageLowBytes(this.mDataDir);
        if (lowThreshold == 0) {
            Log.e(TAG, "Invalid low storage threshold");
        }
        return lowThreshold;
    }

    private boolean runPostBootUpdate(JobParameters jobParams, PackageManagerService pm, ArraySet<String> pkgs) {
        if (this.mExitPostBootUpdate.get()) {
            return false;
        }
        final JobParameters jobParameters = jobParams;
        final PackageManagerService packageManagerService = pm;
        final ArraySet<String> arraySet = pkgs;
        new Thread("BackgroundDexOptService_PostBootUpdate") {
            public void run() {
                BackgroundDexOptService.this.postBootUpdate(jobParameters, packageManagerService, arraySet);
            }
        }.start();
        return true;
    }

    private boolean runPostBootOptWechat(JobParameters jobParams, PackageManagerService pm, ArraySet<String> pkgs) {
        if (this.mExitPostBootUpdate.get()) {
            return false;
        }
        final JobParameters jobParameters = jobParams;
        final PackageManagerService packageManagerService = pm;
        final ArraySet<String> arraySet = pkgs;
        new Thread("BackgroundDexOptService_PostBootWechatOpt") {
            public void run() {
                BackgroundDexOptService.this.postBootUpdate(jobParameters, packageManagerService, arraySet, 3, 2);
                ((JobScheduler) BackgroundDexOptService.mContext.getSystemService("jobscheduler")).schedule(new JobInfo.Builder(BackgroundDexOptService.JOB_POST_BOOT_UPDATE, BackgroundDexOptService.sDexoptServiceName).setMinimumLatency(TimeUnit.SECONDS.toMillis(1)).setOverrideDeadline(TimeUnit.SECONDS.toMillis(1)).build());
            }
        }.start();
        return true;
    }

    /* access modifiers changed from: private */
    public void postBootUpdate(JobParameters jobParams, PackageManagerService pm, ArraySet<String> pkgs) {
        postBootUpdate(jobParams, pm, pkgs, 1, 1);
    }

    /* access modifiers changed from: private */
    public void postBootUpdate(JobParameters jobParams, PackageManagerService pm, ArraySet<String> pkgs, int reason, int optimizeMode) {
        int i = optimizeMode;
        int lowBatteryThreshold = getResources().getInteger(17694828);
        long lowThreshold = getLowStorageThreshold(this);
        int i2 = 2;
        int i3 = 1;
        if (i == 1) {
            this.mAbortPostBootUpdate.set(false);
        } else if (i == 2) {
            this.mAbortPostBootOptWechat.set(false);
        }
        ArraySet<String> updatedPackages = new ArraySet<>();
        Iterator<String> it = pkgs.iterator();
        while (true) {
            if (!it.hasNext()) {
                PackageManagerService packageManagerService = pm;
                int i4 = reason;
                break;
            }
            String pkg = it.next();
            pauseBgDexOptIfNeed();
            if (i == i3 && this.mAbortPostBootUpdate.get()) {
                return;
            }
            if (i == i2 && this.mAbortPostBootOptWechat.get()) {
                return;
            }
            if (this.mExitPostBootUpdate.get()) {
                PackageManagerService packageManagerService2 = pm;
                int i5 = reason;
                break;
            } else if (getBatteryLevel() < lowBatteryThreshold) {
                PackageManagerService packageManagerService3 = pm;
                int i6 = reason;
                break;
            } else {
                long usableSpace = this.mDataDir.getUsableSpace();
                if (usableSpace < lowThreshold) {
                    Log.w(TAG, "Aborting background dex opt job due to low storage: " + usableSpace);
                    PackageManagerService packageManagerService4 = pm;
                    int i7 = reason;
                    break;
                }
                if (pm.performDexOptWithStatus(new DexoptOptions(pkg, reason, 4)) == i3) {
                    updatedPackages.add(pkg);
                }
                if (i == i2 && DEBUG_BG_DEXOPT) {
                    Log.w(TAG, "bg-dexopt: post boot, opt wechat package : " + pkg);
                }
                i2 = 2;
                i3 = 1;
            }
        }
        notifyPinService(updatedPackages);
        jobFinished(jobParams, false);
    }

    private boolean runOtaIdleOptimization(JobParameters jobParams, PackageManagerService pm, ArraySet<String> pkgs) {
        final PackageManagerService packageManagerService = pm;
        final ArraySet<String> arraySet = pkgs;
        final JobParameters jobParameters = jobParams;
        new Thread("BackgroundDexOptService_OtaIdleOptimization") {
            public void run() {
                BackgroundDexOptService backgroundDexOptService = BackgroundDexOptService.this;
                if (backgroundDexOptService.idleOptimization(packageManagerService, arraySet, backgroundDexOptService, 3) != 2 || BackgroundDexOptService.otaToOptPkgs.isEmpty()) {
                    BackgroundDexOptService.this.jobFinished(jobParameters, false);
                    BackgroundDexOptService.otaHasOptedPkgs.clear();
                    BackgroundDexOptService.otaToOptPkgs.clear();
                    JobScheduler js = (JobScheduler) BackgroundDexOptService.mContext.getSystemService("jobscheduler");
                    js.cancel(BackgroundDexOptService.JOB_OTA_IDLE_OPTIMIZE);
                    js.schedule(new JobInfo.Builder(800, BackgroundDexOptService.sDexoptServiceName).setRequiresDeviceIdle(true).setRequiresCharging(true).setPeriodic(BackgroundDexOptService.IDLE_OPTIMIZATION_PERIOD).build());
                    if (BackgroundDexOptService.DEBUG_BG_DEXOPT) {
                        Log.w(BackgroundDexOptService.TAG, "bg-dexopt: After ota work done, re-schedule an origin daily job.");
                    }
                }
            }
        }.start();
        return true;
    }

    private boolean runIdleOptimization(JobParameters jobParams, PackageManagerService pm, ArraySet<String> pkgs) {
        final PackageManagerService packageManagerService = pm;
        final ArraySet<String> arraySet = pkgs;
        final JobParameters jobParameters = jobParams;
        new Thread("BackgroundDexOptService_IdleOptimization") {
            public void run() {
                BackgroundDexOptService backgroundDexOptService = BackgroundDexOptService.this;
                if (backgroundDexOptService.idleOptimization(packageManagerService, arraySet, backgroundDexOptService) != 2) {
                    Log.w(BackgroundDexOptService.TAG, "Idle optimizations aborted because of space constraints.");
                    BackgroundDexOptService.this.jobFinished(jobParameters, false);
                }
            }
        }.start();
        return true;
    }

    /* access modifiers changed from: private */
    public int idleOptimization(PackageManagerService pm, ArraySet<String> pkgs, Context context) {
        Log.i(TAG, "Performing idle optimizations");
        return idleOptimization(pm, pkgs, context, 0);
    }

    /* access modifiers changed from: private */
    public int idleOptimization(PackageManagerService pm, ArraySet<String> pkgs, Context context, int optimizeMode) {
        int result;
        int result2;
        this.mExitPostBootUpdate.set(true);
        if (optimizeMode == 3) {
            this.mAbortIdleOptimization.set(true);
            this.mAbortOtaIdleOptimization.set(false);
        } else {
            this.mAbortOtaIdleOptimization.set(true);
            this.mAbortIdleOptimization.set(false);
        }
        long lowStorageThreshold = getLowStorageThreshold(context);
        if (optimizeMode == 3) {
            result = optimizePackages(pm, new ArraySet(pkgs), lowStorageThreshold, true, 3);
        } else {
            result = optimizePackages(pm, pkgs, lowStorageThreshold, true);
        }
        if (result == 2 || !supportSecondaryDex()) {
            return result;
        }
        if (optimizeMode == 3) {
            result2 = reconcileSecondaryDexFiles(pm.getDexManager(), 3);
        } else {
            result2 = reconcileSecondaryDexFiles(pm.getDexManager());
        }
        if (result2 == 2) {
            return result2;
        }
        if (optimizeMode != 3) {
            return optimizePackages(pm, pkgs, lowStorageThreshold, false);
        }
        return optimizePackages(pm, new ArraySet(pkgs), lowStorageThreshold, false, 3);
    }

    private long getDirectorySize(File f) {
        long size = 0;
        if (!f.isDirectory()) {
            return f.length();
        }
        for (File file : f.listFiles()) {
            size += getDirectorySize(file);
        }
        return size;
    }

    private long getPackageSize(PackageManagerService pm, String pkg) {
        PackageInfo info = pm.getPackageInfo(pkg, 0, 0);
        if (info == null || info.applicationInfo == null) {
            return 0;
        }
        File path = Paths.get(info.applicationInfo.sourceDir, new String[0]).toFile();
        if (path.isFile()) {
            path = path.getParentFile();
        }
        long size = 0 + getDirectorySize(path);
        if (ArrayUtils.isEmpty(info.applicationInfo.splitSourceDirs)) {
            return size;
        }
        long size2 = size;
        for (String splitSourceDir : info.applicationInfo.splitSourceDirs) {
            File path2 = Paths.get(splitSourceDir, new String[0]).toFile();
            if (path2.isFile()) {
                path2 = path2.getParentFile();
            }
            size2 += getDirectorySize(path2);
        }
        return size2;
    }

    private int optimizePackages(PackageManagerService pm, ArraySet<String> pkgs, long lowStorageThreshold, boolean isForPrimaryDex) {
        return optimizePackages(pm, pkgs, lowStorageThreshold, isForPrimaryDex, 0);
    }

    private int optimizePackages(PackageManagerService pm, ArraySet<String> pkgs, long lowStorageThreshold, boolean isForPrimaryDex, int optimizeMode) {
        int abort_code;
        boolean dex_opt_performed;
        PackageManagerService packageManagerService = pm;
        long j = lowStorageThreshold;
        boolean z = isForPrimaryDex;
        int i = optimizeMode;
        ArraySet<String> updatedPackages = new ArraySet<>();
        Set<String> unusedPackages = packageManagerService.getUnusedPackages(mDowngradeUnusedAppsThresholdInMillis);
        Log.d(TAG, "Unsused Packages " + String.join(",", unusedPackages));
        boolean shouldDowngrade = shouldDowngrade(2 * j);
        Log.d(TAG, "Should Downgrade " + shouldDowngrade);
        Iterator<String> it = pkgs.iterator();
        while (it.hasNext()) {
            String pkg = it.next();
            pauseBgDexOptIfNeed();
            if (i == 3) {
                int abort_code2 = abortOtaIdleOptimizations(j);
                otaHasOptedPkgs.add(pkg);
                abort_code = abort_code2;
            } else {
                abort_code = abortIdleOptimizations(j);
            }
            if (abort_code == 2) {
                return abort_code;
            }
            if (unusedPackages.contains(pkg) && shouldDowngrade) {
                dex_opt_performed = downgradePackage(packageManagerService, pkg, z);
            } else if (abort_code == 3) {
                j = lowStorageThreshold;
            } else {
                dex_opt_performed = optimizePackage(packageManagerService, pkg, z);
            }
            if (dex_opt_performed) {
                updatedPackages.add(pkg);
            }
            if (i == 3 && DEBUG_BG_DEXOPT) {
                Log.w(TAG, "bg-dexopt: After ota, optimize package : " + pkg);
            }
            j = lowStorageThreshold;
        }
        notifyPinService(updatedPackages);
        return 0;
    }

    private boolean downgradePackage(PackageManagerService pm, String pkg, boolean isForPrimaryDex) {
        Log.d(TAG, "Downgrading " + pkg);
        boolean dex_opt_performed = false;
        long package_size_before = getPackageSize(pm, pkg);
        if (!isForPrimaryDex) {
            dex_opt_performed = performDexOptSecondary(pm, pkg, 5, 548);
        } else if (!pm.canHaveOatDir(pkg)) {
            pm.deleteOatArtifactsOfPackage(pkg);
        } else {
            dex_opt_performed = performDexOptPrimary(pm, pkg, 5, 548);
        }
        if (dex_opt_performed) {
            StatsLog.write(128, pkg, package_size_before, getPackageSize(pm, pkg), false);
        }
        return dex_opt_performed;
    }

    private boolean supportSecondaryDex() {
        return SystemProperties.getBoolean("dalvik.vm.dexopt.secondary", false);
    }

    private int reconcileSecondaryDexFiles(DexManager dm) {
        return reconcileSecondaryDexFiles(dm, 0);
    }

    private int reconcileSecondaryDexFiles(DexManager dm, int optimizeMode) {
        for (String p : dm.getAllPackagesWithSecondaryDexFiles()) {
            if (optimizeMode == 0 && this.mAbortIdleOptimization.get()) {
                return 2;
            }
            if (optimizeMode == 3 && this.mAbortOtaIdleOptimization.get()) {
                return 2;
            }
            pauseBgDexOptIfNeed();
            dm.reconcileSecondaryDexFiles(p);
        }
        return 0;
    }

    private boolean optimizePackage(PackageManagerService pm, String pkg, boolean isForPrimaryDex) {
        if (isForPrimaryDex) {
            return performDexOptPrimary(pm, pkg, 3, UsbTerminalTypes.TERMINAL_IN_MIC_ARRAY);
        }
        return performDexOptSecondary(pm, pkg, 3, UsbTerminalTypes.TERMINAL_IN_MIC_ARRAY);
    }

    private boolean performDexOptPrimary(PackageManagerService pm, String pkg, int reason, int dexoptFlags) {
        if (trackPerformDexOpt(pkg, false, new Supplier(pkg, reason, dexoptFlags) {
            private final /* synthetic */ String f$1;
            private final /* synthetic */ int f$2;
            private final /* synthetic */ int f$3;

            {
                this.f$1 = r2;
                this.f$2 = r3;
                this.f$3 = r4;
            }

            public final Object get() {
                return Integer.valueOf(PackageManagerService.this.performDexOptWithStatus(new DexoptOptions(this.f$1, this.f$2, this.f$3)));
            }
        }) == 1) {
            return true;
        }
        return false;
    }

    private boolean performDexOptSecondary(PackageManagerService pm, String pkg, int reason, int dexoptFlags) {
        if (trackPerformDexOpt(pkg, true, new Supplier(new DexoptOptions(pkg, reason, dexoptFlags | 8)) {
            private final /* synthetic */ DexoptOptions f$1;

            {
                this.f$1 = r2;
            }

            public final Object get() {
                return BackgroundDexOptService.lambda$performDexOptSecondary$1(PackageManagerService.this, this.f$1);
            }
        }) == 1) {
            return true;
        }
        return false;
    }

    static /* synthetic */ Integer lambda$performDexOptSecondary$1(PackageManagerService pm, DexoptOptions dexoptOptions) {
        return Integer.valueOf(pm.performDexOpt(dexoptOptions) ? 1 : -1);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0015, code lost:
        r1 = r6.get().intValue();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0020, code lost:
        if (r1 == -1) goto L_0x002b;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0022, code lost:
        monitor-enter(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        r0.remove(r4);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0026, code lost:
        monitor-exit(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x002b, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private int trackPerformDexOpt(java.lang.String r4, boolean r5, java.util.function.Supplier<java.lang.Integer> r6) {
        /*
            r3 = this;
            if (r5 == 0) goto L_0x0005
            android.util.ArraySet<java.lang.String> r0 = sFailedPackageNamesPrimary
            goto L_0x0007
        L_0x0005:
            android.util.ArraySet<java.lang.String> r0 = sFailedPackageNamesSecondary
        L_0x0007:
            monitor-enter(r0)
            boolean r1 = r0.contains(r4)     // Catch:{ all -> 0x002c }
            if (r1 == 0) goto L_0x0011
            r1 = 0
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            return r1
        L_0x0011:
            r0.add(r4)     // Catch:{ all -> 0x002c }
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            java.lang.Object r1 = r6.get()
            java.lang.Integer r1 = (java.lang.Integer) r1
            int r1 = r1.intValue()
            r2 = -1
            if (r1 == r2) goto L_0x002b
            monitor-enter(r0)
            r0.remove(r4)     // Catch:{ all -> 0x0028 }
            monitor-exit(r0)     // Catch:{ all -> 0x0028 }
            goto L_0x002b
        L_0x0028:
            r2 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0028 }
            throw r2
        L_0x002b:
            return r1
        L_0x002c:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x002c }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.pm.BackgroundDexOptService.trackPerformDexOpt(java.lang.String, boolean, java.util.function.Supplier):int");
    }

    private int abortOtaIdleOptimizations(long lowStorageThreshold) {
        if (this.mAbortOtaIdleOptimization.get()) {
            return 2;
        }
        long usableSpace = this.mDataDir.getUsableSpace();
        if (usableSpace >= lowStorageThreshold) {
            return 1;
        }
        Log.w(TAG, "Aborting background dex opt job due to low storage: " + usableSpace);
        return 3;
    }

    private int abortIdleOptimizations(long lowStorageThreshold) {
        if (this.mAbortIdleOptimization.get()) {
            return 2;
        }
        long usableSpace = this.mDataDir.getUsableSpace();
        if (usableSpace >= lowStorageThreshold) {
            return 1;
        }
        Log.w(TAG, "Aborting background dex opt job due to low storage: " + usableSpace);
        return 3;
    }

    private boolean shouldDowngrade(long lowStorageThresholdForDowngrade) {
        if (this.mDataDir.getUsableSpace() < lowStorageThresholdForDowngrade) {
            return true;
        }
        return false;
    }

    public static boolean runIdleOptimizationsNow(PackageManagerService pm, Context context, List<String> packageNames) {
        ArraySet<String> packagesToOptimize;
        BackgroundDexOptService bdos = new BackgroundDexOptService();
        if (packageNames == null) {
            packagesToOptimize = pm.getOptimizablePackages();
        } else {
            packagesToOptimize = new ArraySet<>(packageNames);
        }
        return bdos.idleOptimization(pm, packagesToOptimize, context) == 0;
    }

    public boolean onStartJob(JobParameters params) {
        PackageManagerService pm = (PackageManagerService) ServiceManager.getService(Settings.ATTR_PACKAGE);
        if (pm.isStorageLow()) {
            return false;
        }
        ArraySet<String> pkgs = pm.getOptimizablePackages();
        if (pkgs.isEmpty()) {
            return false;
        }
        ArraySet<String> wechatPkg = new ArraySet<>();
        wechatPkg.add(WECHAT_PKG);
        if (params.getJobId() == JOB_POST_BOOT_UPDATE) {
            return runPostBootUpdate(params, pm, pkgs);
        }
        if (params.getJobId() == JOB_POST_BOOT_OPTWECHAT) {
            if (containsWechat(pkgs)) {
                return runPostBootOptWechat(params, pm, wechatPkg);
            }
            return false;
        } else if (params.getJobId() == 800) {
            return runIdleOptimization(params, pm, pkgs);
        } else {
            if (params.getJobId() != JOB_OTA_IDLE_OPTIMIZE) {
                return false;
            }
            updateTop20ThirdPartyPkgs();
            ArraySet<String> otaPkgs = getOptPkgsByBatteryLevel();
            if (otaPkgs.contains(WECHAT_PKG)) {
                otaPkgs.remove(WECHAT_PKG);
            }
            if (otaPkgs.isEmpty()) {
                return false;
            }
            otaToOptPkgs.clear();
            otaToOptPkgs.addAll(otaPkgs);
            Iterator<String> it = otaHasOptedPkgs.iterator();
            while (it.hasNext()) {
                String pkg = it.next();
                if (pkg != null) {
                    otaToOptPkgs.remove(pkg);
                }
            }
            return runOtaIdleOptimization(params, pm, otaToOptPkgs);
        }
    }

    public boolean containsWechat(ArraySet<String> pkgs) {
        Iterator<String> it = pkgs.iterator();
        while (it.hasNext()) {
            if (it.next().equals(WECHAT_PKG)) {
                return true;
            }
        }
        return false;
    }

    public boolean onStopJob(JobParameters params) {
        if (params.getJobId() == JOB_POST_BOOT_UPDATE) {
            this.mAbortPostBootUpdate.set(true);
            return false;
        } else if (params.getJobId() == JOB_POST_BOOT_OPTWECHAT) {
            this.mAbortPostBootOptWechat.set(true);
            return false;
        } else if (params.getJobId() == 800) {
            this.mAbortIdleOptimization.set(true);
            this.mAbortOtaIdleOptimization.set(false);
            return true;
        } else if (params.getJobId() != JOB_OTA_IDLE_OPTIMIZE) {
            return false;
        } else {
            this.mAbortOtaIdleOptimization.set(true);
            this.mAbortIdleOptimization.set(false);
            Iterator<String> it = otaHasOptedPkgs.iterator();
            while (it.hasNext()) {
                String pkg = it.next();
                if (pkg != null) {
                    otaToOptPkgs.remove(pkg);
                }
            }
            return !otaToOptPkgs.isEmpty();
        }
    }

    private void notifyPinService(ArraySet<String> updatedPackages) {
        PinnerService pinnerService = (PinnerService) LocalServices.getService(PinnerService.class);
        if (pinnerService != null) {
            Log.i(TAG, "Pinning optimized code " + updatedPackages);
            pinnerService.update(updatedPackages, false);
        }
    }

    private static long getDowngradeUnusedAppsThresholdInMillis() {
        String sysPropValue = SystemProperties.get("pm.dexopt.downgrade_after_inactive_days");
        if (sysPropValue != null && !sysPropValue.isEmpty()) {
            return TimeUnit.DAYS.toMillis(Long.parseLong(sysPropValue));
        }
        Log.w(TAG, "SysProp pm.dexopt.downgrade_after_inactive_days not set");
        return JobStatus.NO_LATEST_RUNTIME;
    }

    private static boolean isBackgroundDexoptDisabled() {
        return SystemProperties.getBoolean("pm.dexopt.disable_bg_dexopt", false);
    }
}
