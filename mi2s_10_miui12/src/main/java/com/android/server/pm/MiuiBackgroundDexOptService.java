package com.android.server.pm;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.ServiceManager;
import android.util.ArraySet;
import android.util.Log;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class MiuiBackgroundDexOptService extends JobService {
    private static final int DEXOPT_LRU_THRESHOLD_DAYS = 7;
    static final int MIUI_BACKGROUND_DEXOPT_JOB = 900;
    static final long RETRY_LATENCY = 14400000;
    static final String TAG = "MiuiBackgroundDexOptService";
    private static ComponentName sDexoptServiceName = new ComponentName(PackageManagerService.PLATFORM_PACKAGE_NAME, MiuiBackgroundDexOptService.class.getName());
    static final ArraySet<String> sFailedPackageNames = new ArraySet<>();
    final AtomicBoolean mIdleTime = new AtomicBoolean(false);

    public static void schedule(Context context) {
        schedule(context, 0);
    }

    public static void schedule(Context context, long minLatency) {
        ((JobScheduler) context.getSystemService("jobscheduler")).schedule(new JobInfo.Builder(MIUI_BACKGROUND_DEXOPT_JOB, sDexoptServiceName).setRequiresDeviceIdle(true).setMinimumLatency(minLatency).setRequiresCharging(false).build());
    }

    private ArraySet<String> getMostNeededDexOptPackages(Set<String> pkgs) {
        List<String> startedPkgs = PackageDexOptimizerManager.getRecentlyUsedPackages(((PackageManagerService) ServiceManager.getService(Settings.ATTR_PACKAGE)).mContext, 7);
        ArraySet<String> mostNeededPkgs = new ArraySet<>();
        for (String p : pkgs) {
            if (startedPkgs.contains(p)) {
                mostNeededPkgs.add(p);
            }
        }
        return mostNeededPkgs;
    }

    public boolean onStartJob(JobParameters params) {
        return true;
    }

    public boolean onStopJob(JobParameters params) {
        Log.i(TAG, "onIdleStop");
        this.mIdleTime.set(false);
        return false;
    }
}
