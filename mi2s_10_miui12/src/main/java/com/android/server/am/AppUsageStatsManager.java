package com.android.server.am;

import android.app.usage.UsageEvents;
import android.os.SystemClock;
import android.os.spc.PressureStateSettings;
import android.text.TextUtils;
import android.util.Log;
import com.miui.server.AccessController;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class AppUsageStatsManager {
    public static final int ADJ_DISTANT = 1;
    public static final int ADJ_FOREGROUND = -900;
    public static final int ADJ_KILL_PROTECTED = -2;
    public static final int ADJ_NORMAL = 0;
    public static final int ADJ_RECENT_STARTED = -1;
    public static final int ADJ_UNKNOWN = -999;
    private static final boolean DEBUG = PressureStateSettings.DEBUG_ALL;
    public static final long FAST_RESTART_TIME_MS = 600000;
    public static final long MIN_APP_FOREGROUND_EVENT_DURATION = 30000;
    public static final long PROTECTED_TIME_MS = 7200000;
    public static final String TAG = "AppUsageStatsManager";
    private static AppUsageStatsManager mAppUsageManager;
    private static ArrayList<String> mForceStopWhitelist = new ArrayList<>();
    private ConcurrentHashMap<String, PackageRecord> mAppUsageRecords = new ConcurrentHashMap<>();
    private boolean mIsEnabledSpeedTestProtect;
    private long mLastPkgResumeTimeMillis;
    private String mLastResumedPkg;

    static {
        mForceStopWhitelist.add("com.mi.android.globallauncher");
        mForceStopWhitelist.add("com.miui.home");
        mForceStopWhitelist.add(AccessController.PACKAGE_SYSTEMUI);
    }

    private class PackageRecord {
        public volatile long lastFgTime;
        public volatile long lastKilledTime;
        public volatile long lastStartTime;
        public final String packageName;
        public volatile long protectStartTime;

        public PackageRecord(String packageName2) {
            this.packageName = packageName2;
        }
    }

    private AppUsageStatsManager() {
    }

    public static AppUsageStatsManager getInstance() {
        if (mAppUsageManager == null) {
            mAppUsageManager = new AppUsageStatsManager();
        }
        return mAppUsageManager;
    }

    public void setEnabledSpeedTestProtect(boolean isEnabled) {
        this.mIsEnabledSpeedTestProtect = isEnabled;
    }

    public void reportEvent(UsageEvents.Event ev) {
        if (ev != null) {
            long curTime = SystemClock.elapsedRealtime();
            if (ev.getEventType() == 1) {
                reportAppStarted(ev.getPackageName(), curTime);
                String str = this.mLastResumedPkg;
                if (str == null) {
                    this.mLastResumedPkg = ev.getPackageName();
                    this.mLastPkgResumeTimeMillis = curTime;
                } else if (!str.equals(ev.getPackageName())) {
                    reportAppPaused(this.mLastResumedPkg, curTime - this.mLastPkgResumeTimeMillis, curTime);
                    this.mLastResumedPkg = ev.getPackageName();
                    this.mLastPkgResumeTimeMillis = curTime;
                }
            }
        }
    }

    private void reportAppStarted(String packageName, long time) {
        PackageRecord r = getOrCreatePackageUsageRecord(packageName);
        if (r != null) {
            r.lastStartTime = Math.max(r.lastStartTime, time);
        }
    }

    private void reportAppPaused(String packageName, long foregroundDuration, long endTime) {
        if (foregroundDuration >= 30000) {
            if (DEBUG) {
                Log.d(TAG, String.format("Application switch to bg: pkg:%s, duration:%s", new Object[]{packageName, Long.valueOf(foregroundDuration)}));
            }
            PackageRecord r = getOrCreatePackageUsageRecord(packageName);
            if (r != null) {
                r.lastFgTime = Math.max(r.lastFgTime, endTime);
                if (r.lastKilledTime != 0) {
                    long t = (r.lastFgTime - foregroundDuration) - r.lastKilledTime;
                    if (t > 0 && t <= 600000) {
                        Log.w(TAG, String.format("App started by user and it has been killed recently. %s, %s", new Object[]{Long.valueOf(r.lastKilledTime), Long.valueOf(r.lastFgTime)}));
                        r.protectStartTime = r.lastFgTime;
                        return;
                    }
                    return;
                }
                return;
            }
            long j = endTime;
        }
    }

    public void reportAppKilledByProcessCleaner(String packageName, long timeMs) {
        PackageRecord r = getOrCreatePackageUsageRecord(packageName);
        if (r != null) {
            r.lastKilledTime = Math.max(r.lastKilledTime, timeMs);
        }
    }

    public boolean canForceStopPackage(String packageName) {
        if (mForceStopWhitelist.contains(packageName)) {
            return false;
        }
        return true;
    }

    public int computeUsageAdj(String packageName) {
        PackageRecord r = getOrCreatePackageUsageRecord(packageName);
        if (r == null) {
            return ADJ_UNKNOWN;
        }
        if (packageName.equals(this.mLastResumedPkg)) {
            return ADJ_FOREGROUND;
        }
        long curTime = SystemClock.elapsedRealtime();
        if (r.protectStartTime != 0) {
            long t = curTime - r.protectStartTime;
            if (t > 0 && t <= 7200000) {
                return -2;
            }
        }
        if (this.mIsEnabledSpeedTestProtect && r.lastStartTime != 0 && curTime > r.lastStartTime && curTime - r.lastStartTime <= 600000) {
            return -1;
        }
        if (r.lastFgTime != 0 && curTime > r.lastFgTime && curTime - r.lastFgTime <= PressureStateSettings.RECENT_FOREGROUND_APP_TIME_MILLIS) {
            return -1;
        }
        if (r.lastFgTime == 0 || curTime - r.lastFgTime < PressureStateSettings.DISTANT_APP_TIME_TIME_MILLIS) {
            return 0;
        }
        return 1;
    }

    private PackageRecord getOrCreatePackageUsageRecord(String packageName) {
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }
        PackageRecord r = this.mAppUsageRecords.get(packageName);
        if (r != null) {
            return r;
        }
        PackageRecord r2 = new PackageRecord(packageName);
        this.mAppUsageRecords.put(packageName, r2);
        return r2;
    }

    public void dumpAppUsage(PrintWriter pw) {
        long curTime = SystemClock.elapsedRealtime();
        List<PackageRecord> records = new ArrayList<>(this.mAppUsageRecords.values());
        records.sort(new Comparator<PackageRecord>() {
            public int compare(PackageRecord t1, PackageRecord t2) {
                return AppUsageStatsManager.this.computeUsageAdj(t2.packageName) - AppUsageStatsManager.this.computeUsageAdj(t1.packageName);
            }
        });
        for (PackageRecord r : records) {
            pw.println("package: " + r.packageName);
            pw.println(" lastStartTime: " + r.lastStartTime + " lastFgTime: " + r.lastFgTime + " lastKilledTime: " + r.lastKilledTime);
            StringBuilder sb = new StringBuilder();
            sb.append(" usageAdj: ");
            sb.append(computeUsageAdj(r.packageName));
            pw.println(sb.toString());
            if (r.protectStartTime != 0) {
                pw.println(" protectDuration: " + Math.max(0, 7200000 - (curTime - r.protectStartTime)));
            }
        }
    }
}
