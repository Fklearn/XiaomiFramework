package com.android.server;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.os.BatteryManager;
import android.os.PowerManager;
import android.util.Slog;
import com.android.server.pm.PackageManagerService;
import java.util.Calendar;

public class MountServiceDefragIdler extends JobService {
    private static int DEFRAG_JOB_ID = 809;
    private static final int MINIMUM_BATTERY_LEVEL = 40;
    private static final String TAG = "MountServiceDefragIdler";
    private static ComponentName sIdleService = new ComponentName(PackageManagerService.PLATFORM_PACKAGE_NAME, MountServiceDefragIdler.class.getName());
    /* access modifiers changed from: private */
    public Runnable mFinishCallback = new Runnable() {
        public void run() {
            Slog.i(MountServiceDefragIdler.TAG, "Got defrag completion callback");
            synchronized (MountServiceDefragIdler.this.mFinishCallback) {
                if (MountServiceDefragIdler.this.mStarted) {
                    MountServiceDefragIdler.this.jobFinished(MountServiceDefragIdler.this.mJobParams, false);
                    boolean unused = MountServiceDefragIdler.this.mStarted = false;
                }
            }
            MountServiceDefragIdler.scheduleDefrag(MountServiceDefragIdler.this);
        }
    };
    /* access modifiers changed from: private */
    public JobParameters mJobParams;
    /* access modifiers changed from: private */
    public boolean mStarted;

    public boolean onStartJob(JobParameters params) {
        this.mJobParams = params;
        int batteryLevel = ((BatteryManager) getSystemService("batterymanager")).getIntProperty(4);
        if (((PowerManager) getSystemService("power")).isInteractive() || batteryLevel < 40) {
            Slog.i(TAG, "defrag job scheduled failed, reschedule it");
            scheduleDefrag(this);
            return false;
        }
        StorageManagerService ms = StorageManagerService.sSelf;
        if (ms != null) {
            synchronized (this.mFinishCallback) {
                this.mStarted = true;
            }
            ms.runDefrag(this.mFinishCallback);
        }
        if (ms != null) {
            return true;
        }
        return false;
    }

    public boolean onStopJob(JobParameters params) {
        synchronized (this.mFinishCallback) {
            this.mStarted = false;
        }
        return false;
    }

    public static void scheduleDefrag(Context context) {
        long timeToMidnight = tomorrowMidnight().getTimeInMillis() - System.currentTimeMillis();
        JobInfo.Builder builder = new JobInfo.Builder(DEFRAG_JOB_ID, sIdleService);
        builder.setMinimumLatency(timeToMidnight);
        ((JobScheduler) context.getSystemService("jobscheduler")).schedule(builder.build());
    }

    private static Calendar tomorrowMidnight() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(11, 2);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        calendar.add(5, 1);
        return calendar;
    }
}
