package com.android.server.usage;

import android.app.usage.ConfigurationStats;
import android.app.usage.EventList;
import android.app.usage.EventStats;
import android.app.usage.TimeSparseArray;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.SystemClock;
import android.server.am.SplitScreenReporter;
import android.text.format.DateUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.SparseIntArray;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.am.AssistDataRequester;
import com.android.server.pm.PackageManagerService;
import com.android.server.pm.Settings;
import com.android.server.policy.PhoneWindowManager;
import com.android.server.usage.IntervalStats;
import com.android.server.usage.UsageStatsDatabase;
import com.android.server.voiceinteraction.DatabaseHelper;
import com.android.server.wm.WindowManagerService;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

class UserUsageStatsService {
    private static final boolean DEBUG = false;
    private static final long[] INTERVAL_LENGTH = {86400000, UnixCalendar.WEEK_IN_MILLIS, UnixCalendar.MONTH_IN_MILLIS, 31536000000L};
    private static final String TAG = "UsageStatsService";
    private static final UsageStatsDatabase.StatCombiner<ConfigurationStats> sConfigStatsCombiner = new UsageStatsDatabase.StatCombiner<ConfigurationStats>() {
        public void combine(IntervalStats stats, boolean mutable, List<ConfigurationStats> accResult) {
            if (!mutable) {
                accResult.addAll(stats.configurations.values());
                return;
            }
            int configCount = stats.configurations.size();
            for (int i = 0; i < configCount; i++) {
                accResult.add(new ConfigurationStats(stats.configurations.valueAt(i)));
            }
        }
    };
    private static final SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final int sDateFormatFlags = 131093;
    private static final UsageStatsDatabase.StatCombiner<EventStats> sEventStatsCombiner = new UsageStatsDatabase.StatCombiner<EventStats>() {
        public void combine(IntervalStats stats, boolean mutable, List<EventStats> accResult) {
            stats.addEventStatsTo(accResult);
        }
    };
    private static final UsageStatsDatabase.StatCombiner<UsageStats> sUsageStatsCombiner = new UsageStatsDatabase.StatCombiner<UsageStats>() {
        public void combine(IntervalStats stats, boolean mutable, List<UsageStats> accResult) {
            if (!mutable) {
                accResult.addAll(stats.packageStats.values());
                return;
            }
            int statCount = stats.packageStats.size();
            for (int i = 0; i < statCount; i++) {
                accResult.add(new UsageStats(stats.packageStats.valueAt(i)));
            }
        }
    };
    private final Context mContext;
    private final IntervalStats[] mCurrentStats;
    private final UnixCalendar mDailyExpiryDate;
    private final UsageStatsDatabase mDatabase;
    private String mLastBackgroundedPackage;
    private final StatsUpdatedListener mListener;
    private final String mLogPrefix;
    private boolean mStatsChanged = false;
    private final int mUserId;

    interface StatsUpdatedListener {
        void onNewUpdate(int i);

        void onStatsReloaded();

        void onStatsUpdated();
    }

    UserUsageStatsService(Context context, int userId, File usageStatsDir, StatsUpdatedListener listener) {
        this.mContext = context;
        this.mDailyExpiryDate = new UnixCalendar(0);
        this.mDatabase = new UsageStatsDatabase(usageStatsDir);
        this.mCurrentStats = new IntervalStats[4];
        this.mListener = listener;
        this.mLogPrefix = "User[" + Integer.toString(userId) + "] ";
        this.mUserId = userId;
    }

    /* access modifiers changed from: package-private */
    public void init(long currentTimeMillis) {
        IntervalStats[] intervalStatsArr;
        this.mDatabase.init(currentTimeMillis);
        int nullCount = 0;
        int i = 0;
        while (true) {
            intervalStatsArr = this.mCurrentStats;
            if (i >= intervalStatsArr.length) {
                break;
            }
            intervalStatsArr[i] = this.mDatabase.getLatestUsageStats(i);
            if (this.mCurrentStats[i] == null) {
                nullCount++;
            }
            i++;
        }
        if (nullCount > 0) {
            if (nullCount != intervalStatsArr.length) {
                Slog.w(TAG, this.mLogPrefix + "Some stats have no latest available");
            }
            loadActiveStats(currentTimeMillis);
        } else {
            updateRolloverDeadline();
        }
        IntervalStats currentDailyStats = this.mCurrentStats[0];
        if (currentDailyStats != null) {
            UsageEvents.Event shutdownEvent = new UsageEvents.Event(26, currentDailyStats.lastTimeSaved + 1000);
            shutdownEvent.mPackage = PackageManagerService.PLATFORM_PACKAGE_NAME;
            currentDailyStats.addEvent(shutdownEvent);
            UsageEvents.Event startupEvent = new UsageEvents.Event(27, currentTimeMillis);
            startupEvent.mPackage = PackageManagerService.PLATFORM_PACKAGE_NAME;
            currentDailyStats.addEvent(startupEvent);
        }
        if (this.mDatabase.isNewUpdate()) {
            notifyNewUpdate();
        }
    }

    /* access modifiers changed from: package-private */
    public void onTimeChanged(long oldTime, long newTime) {
        persistActiveStats();
        this.mDatabase.onTimeChanged(newTime - oldTime);
        loadActiveStats(newTime);
    }

    /* access modifiers changed from: package-private */
    public void reportEvent(UsageEvents.Event event) {
        UsageEvents.Event event2 = event;
        if (event2.mTimeStamp >= this.mDailyExpiryDate.getTimeInMillis()) {
            rolloverStats(event2.mTimeStamp);
        }
        IntervalStats currentDailyStats = this.mCurrentStats[0];
        Configuration newFullConfig = event2.mConfiguration;
        int i = 5;
        if (event2.mEventType == 5 && currentDailyStats.activeConfiguration != null) {
            event2.mConfiguration = Configuration.generateDelta(currentDailyStats.activeConfiguration, newFullConfig);
        }
        if (!(event2.mEventType == 6 || event2.mEventType == 24 || event2.mEventType == 25 || event2.mEventType == 26)) {
            currentDailyStats.addEvent(event2);
        }
        boolean incrementAppLaunch = false;
        if (event2.mEventType == 1) {
            if (event2.mPackage != null && !event2.mPackage.equals(this.mLastBackgroundedPackage)) {
                incrementAppLaunch = true;
            }
        } else if (event2.mEventType == 2 && event2.mPackage != null) {
            this.mLastBackgroundedPackage = event2.mPackage;
        }
        IntervalStats[] intervalStatsArr = this.mCurrentStats;
        int length = intervalStatsArr.length;
        int i2 = 0;
        while (i2 < length) {
            IntervalStats stats = intervalStatsArr[i2];
            int i3 = event2.mEventType;
            if (i3 == i) {
                stats.updateConfigurationStats(newFullConfig, event2.mTimeStamp);
            } else if (i3 != 9) {
                switch (i3) {
                    case 15:
                        stats.updateScreenInteractive(event2.mTimeStamp);
                        break;
                    case 16:
                        stats.updateScreenNonInteractive(event2.mTimeStamp);
                        break;
                    case 17:
                        stats.updateKeyguardShown(event2.mTimeStamp);
                        break;
                    case 18:
                        stats.updateKeyguardHidden(event2.mTimeStamp);
                        break;
                    default:
                        IntervalStats stats2 = stats;
                        stats.update(event2.mPackage, event.getClassName(), event2.mTimeStamp, event2.mEventType, event2.mInstanceId);
                        if (!incrementAppLaunch) {
                            break;
                        } else {
                            stats2.incrementAppLaunchCount(event2.mPackage);
                            break;
                        }
                }
            } else {
                IntervalStats stats3 = stats;
                stats3.updateChooserCounts(event2.mPackage, event2.mContentType, event2.mAction);
                String[] annotations = event2.mContentAnnotations;
                if (annotations != null) {
                    for (String annotation : annotations) {
                        stats3.updateChooserCounts(event2.mPackage, annotation, event2.mAction);
                    }
                }
            }
            i2++;
            i = 5;
        }
        notifyStatsChanged();
    }

    private <T> List<T> queryStats(int intervalType, long beginTime, long endTime, UsageStatsDatabase.StatCombiner<T> combiner) {
        int intervalType2;
        long j = beginTime;
        long j2 = endTime;
        int i = intervalType;
        if (i == 4) {
            int intervalType3 = this.mDatabase.findBestFitBucket(j, j2);
            if (intervalType3 < 0) {
                intervalType2 = 0;
            } else {
                intervalType2 = intervalType3;
            }
        } else {
            intervalType2 = i;
        }
        if (intervalType2 >= 0) {
            IntervalStats[] intervalStatsArr = this.mCurrentStats;
            if (intervalType2 >= intervalStatsArr.length) {
                UsageStatsDatabase.StatCombiner<T> statCombiner = combiner;
            } else {
                IntervalStats currentStats = intervalStatsArr[intervalType2];
                if (j >= currentStats.endTime) {
                    return null;
                }
                List<T> results = this.mDatabase.queryUsageStats(intervalType2, beginTime, Math.min(currentStats.beginTime, j2), combiner);
                if (j >= currentStats.endTime || j2 <= currentStats.beginTime) {
                    UsageStatsDatabase.StatCombiner<T> statCombiner2 = combiner;
                } else {
                    if (results == null) {
                        results = new ArrayList<>();
                    }
                    combiner.combine(currentStats, true, results);
                }
                return results;
            }
        } else {
            UsageStatsDatabase.StatCombiner<T> statCombiner3 = combiner;
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public List<UsageStats> queryUsageStats(int bucketType, long beginTime, long endTime) {
        return queryStats(bucketType, beginTime, endTime, sUsageStatsCombiner);
    }

    /* access modifiers changed from: package-private */
    public List<ConfigurationStats> queryConfigurationStats(int bucketType, long beginTime, long endTime) {
        return queryStats(bucketType, beginTime, endTime, sConfigStatsCombiner);
    }

    /* access modifiers changed from: package-private */
    public List<EventStats> queryEventStats(int bucketType, long beginTime, long endTime) {
        return queryStats(bucketType, beginTime, endTime, sEventStatsCombiner);
    }

    /* access modifiers changed from: package-private */
    public UsageEvents queryEvents(long beginTime, long endTime, boolean obfuscateInstantApps) {
        ArraySet<String> names = new ArraySet<>();
        final long j = beginTime;
        final long j2 = endTime;
        final boolean z = obfuscateInstantApps;
        final ArraySet<String> arraySet = names;
        List<UsageEvents.Event> results = queryStats(0, j, j2, new UsageStatsDatabase.StatCombiner<UsageEvents.Event>() {
            public void combine(IntervalStats stats, boolean mutable, List<UsageEvents.Event> accumulatedResult) {
                int startIndex = stats.events.firstIndexOnOrAfter(j);
                int size = stats.events.size();
                int i = startIndex;
                while (i < size && stats.events.get(i).mTimeStamp < j2) {
                    UsageEvents.Event event = stats.events.get(i);
                    if (z) {
                        event = event.getObfuscatedIfInstantApp();
                    }
                    if (event.mPackage != null) {
                        arraySet.add(event.mPackage);
                    }
                    if (event.mClass != null) {
                        arraySet.add(event.mClass);
                    }
                    if (event.mTaskRootPackage != null) {
                        arraySet.add(event.mTaskRootPackage);
                    }
                    if (event.mTaskRootClass != null) {
                        arraySet.add(event.mTaskRootClass);
                    }
                    accumulatedResult.add(event);
                    i++;
                }
            }
        });
        if (results == null || results.isEmpty()) {
            return null;
        }
        String[] table = (String[]) names.toArray(new String[names.size()]);
        Arrays.sort(table);
        return new UsageEvents(results, table, true);
    }

    /* access modifiers changed from: package-private */
    public UsageEvents queryEventsForPackage(long beginTime, long endTime, String packageName, boolean includeTaskRoot) {
        ArraySet<String> names = new ArraySet<>();
        names.add(packageName);
        List<UsageEvents.Event> results = queryStats(0, beginTime, endTime, new UsageStatsDatabase.StatCombiner(beginTime, endTime, packageName, names, includeTaskRoot) {
            private final /* synthetic */ long f$0;
            private final /* synthetic */ long f$1;
            private final /* synthetic */ String f$2;
            private final /* synthetic */ ArraySet f$3;
            private final /* synthetic */ boolean f$4;

            {
                this.f$0 = r1;
                this.f$1 = r3;
                this.f$2 = r5;
                this.f$3 = r6;
                this.f$4 = r7;
            }

            public final void combine(IntervalStats intervalStats, boolean z, List list) {
                UserUsageStatsService.lambda$queryEventsForPackage$0(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, intervalStats, z, list);
            }
        });
        if (results == null) {
            boolean z = includeTaskRoot;
            return null;
        } else if (results.isEmpty()) {
            boolean z2 = includeTaskRoot;
            return null;
        } else {
            String[] table = (String[]) names.toArray(new String[names.size()]);
            Arrays.sort(table);
            return new UsageEvents(results, table, includeTaskRoot);
        }
    }

    static /* synthetic */ void lambda$queryEventsForPackage$0(long beginTime, long endTime, String packageName, ArraySet names, boolean includeTaskRoot, IntervalStats stats, boolean mutable, List accumulatedResult) {
        int startIndex = stats.events.firstIndexOnOrAfter(beginTime);
        int size = stats.events.size();
        int i = startIndex;
        while (i < size && stats.events.get(i).mTimeStamp < endTime) {
            UsageEvents.Event event = stats.events.get(i);
            if (packageName.equals(event.mPackage)) {
                if (event.mClass != null) {
                    names.add(event.mClass);
                }
                if (includeTaskRoot && event.mTaskRootPackage != null) {
                    names.add(event.mTaskRootPackage);
                }
                if (includeTaskRoot && event.mTaskRootClass != null) {
                    names.add(event.mTaskRootClass);
                }
                accumulatedResult.add(event);
            }
            i++;
        }
    }

    /* access modifiers changed from: package-private */
    public void persistActiveStats() {
        if (this.mStatsChanged) {
            Slog.i(TAG, this.mLogPrefix + "Flushing usage stats to disk");
            int i = 0;
            while (i < this.mCurrentStats.length) {
                try {
                    this.mDatabase.putUsageStats(i, this.mCurrentStats[i]);
                    i++;
                } catch (IOException e) {
                    Slog.e(TAG, this.mLogPrefix + "Failed to persist active stats", e);
                    return;
                }
            }
            this.mStatsChanged = false;
        }
    }

    private void rolloverStats(long currentTimeMillis) {
        IntervalStats stat;
        int i;
        int i2;
        long beginTime;
        IntervalStats[] intervalStatsArr;
        ArrayMap<String, SparseIntArray> continueActivity;
        ArraySet<String> continuePkgs;
        int continueCount;
        IntervalStats stat2;
        int i3;
        IntervalStats[] intervalStatsArr2;
        int pkgCount;
        UsageStats pkgStats;
        long startTime = SystemClock.elapsedRealtime();
        Slog.i(TAG, this.mLogPrefix + "Rolling over usage stats");
        Configuration previousConfig = this.mCurrentStats[0].activeConfiguration;
        ArraySet<String> continuePkgs2 = new ArraySet<>();
        ArrayMap<String, SparseIntArray> continueActivity2 = new ArrayMap<>();
        ArrayMap<String, ArrayMap<String, Integer>> continueForegroundService = new ArrayMap<>();
        IntervalStats[] intervalStatsArr3 = this.mCurrentStats;
        int length = intervalStatsArr3.length;
        int i4 = 0;
        while (i4 < length) {
            IntervalStats stat3 = intervalStatsArr3[i4];
            int pkgCount2 = stat3.packageStats.size();
            int i5 = 0;
            while (i5 < pkgCount2) {
                UsageStats pkgStats2 = stat3.packageStats.valueAt(i5);
                if (pkgStats2.mActivities.size() > 0 || !pkgStats2.mForegroundServices.isEmpty()) {
                    if (pkgStats2.mActivities.size() > 0) {
                        intervalStatsArr2 = intervalStatsArr3;
                        continueActivity2.put(pkgStats2.mPackageName, pkgStats2.mActivities);
                        pkgStats = pkgStats2;
                        i3 = i5;
                        pkgCount = pkgCount2;
                        stat2 = stat3;
                        stat3.update(pkgStats2.mPackageName, (String) null, this.mDailyExpiryDate.getTimeInMillis() - 1, 3, 0);
                    } else {
                        intervalStatsArr2 = intervalStatsArr3;
                        pkgStats = pkgStats2;
                        i3 = i5;
                        pkgCount = pkgCount2;
                        stat2 = stat3;
                    }
                    UsageStats pkgStats3 = pkgStats;
                    if (!pkgStats3.mForegroundServices.isEmpty()) {
                        continueForegroundService.put(pkgStats3.mPackageName, pkgStats3.mForegroundServices);
                        stat2.update(pkgStats3.mPackageName, (String) null, this.mDailyExpiryDate.getTimeInMillis() - 1, 22, 0);
                    }
                    continuePkgs2.add(pkgStats3.mPackageName);
                    notifyStatsChanged();
                } else {
                    intervalStatsArr2 = intervalStatsArr3;
                    i3 = i5;
                    pkgCount = pkgCount2;
                    stat2 = stat3;
                }
                i5 = i3 + 1;
                pkgCount2 = pkgCount;
                intervalStatsArr3 = intervalStatsArr2;
                stat3 = stat2;
            }
            int i6 = i5;
            int i7 = pkgCount2;
            IntervalStats stat4 = stat3;
            stat4.updateConfigurationStats((Configuration) null, this.mDailyExpiryDate.getTimeInMillis() - 1);
            stat4.commitTime(this.mDailyExpiryDate.getTimeInMillis() - 1);
            i4++;
            intervalStatsArr3 = intervalStatsArr3;
        }
        persistActiveStats();
        this.mDatabase.prune(currentTimeMillis);
        loadActiveStats(currentTimeMillis);
        int continueCount2 = continuePkgs2.size();
        int i8 = 0;
        while (i8 < continueCount2) {
            String pkgName = continuePkgs2.valueAt(i8);
            long beginTime2 = this.mCurrentStats[0].beginTime;
            IntervalStats[] intervalStatsArr4 = this.mCurrentStats;
            long beginTime3 = beginTime2;
            int i9 = 0;
            for (int length2 = intervalStatsArr4.length; i9 < length2; length2 = i) {
                int i10 = length2;
                IntervalStats stat5 = intervalStatsArr4[i9];
                if (continueActivity2.containsKey(pkgName)) {
                    continueCount = continueCount2;
                    SparseIntArray eventMap = continueActivity2.get(pkgName);
                    continuePkgs = continuePkgs2;
                    int size = eventMap.size();
                    continueActivity = continueActivity2;
                    int j = 0;
                    while (j < size) {
                        long j2 = beginTime3;
                        stat5.update(pkgName, (String) null, beginTime3, eventMap.valueAt(j), eventMap.keyAt(j));
                        j++;
                        intervalStatsArr4 = intervalStatsArr4;
                        i9 = i9;
                        i10 = i10;
                        stat5 = stat5;
                    }
                    intervalStatsArr = intervalStatsArr4;
                    i2 = i9;
                    stat = stat5;
                    beginTime = beginTime3;
                    i = i10;
                } else {
                    continueCount = continueCount2;
                    continuePkgs = continuePkgs2;
                    continueActivity = continueActivity2;
                    intervalStatsArr = intervalStatsArr4;
                    i2 = i9;
                    stat = stat5;
                    beginTime = beginTime3;
                    i = i10;
                }
                if (continueForegroundService.containsKey(pkgName) != 0) {
                    ArrayMap<String, Integer> eventMap2 = continueForegroundService.get(pkgName);
                    int size2 = eventMap2.size();
                    for (int j3 = 0; j3 < size2; j3++) {
                        stat.update(pkgName, eventMap2.keyAt(j3), beginTime, eventMap2.valueAt(j3).intValue(), 0);
                    }
                }
                long beginTime4 = beginTime;
                stat.updateConfigurationStats(previousConfig, beginTime4);
                notifyStatsChanged();
                i9 = i2 + 1;
                beginTime3 = beginTime4;
                continueCount2 = continueCount;
                continuePkgs2 = continuePkgs;
                continueActivity2 = continueActivity;
                intervalStatsArr4 = intervalStatsArr;
            }
            int continueCount3 = continueCount2;
            ArrayMap<String, SparseIntArray> arrayMap = continueActivity2;
            long j4 = beginTime3;
            i8++;
            continueCount2 = continueCount3;
            continuePkgs2 = continuePkgs2;
        }
        ArrayMap<String, SparseIntArray> arrayMap2 = continueActivity2;
        persistActiveStats();
        Slog.i(TAG, this.mLogPrefix + "Rolling over usage stats complete. Took " + (SystemClock.elapsedRealtime() - startTime) + " milliseconds");
    }

    private void notifyStatsChanged() {
        if (!this.mStatsChanged) {
            this.mStatsChanged = true;
            this.mListener.onStatsUpdated();
        }
    }

    private void notifyNewUpdate() {
        this.mListener.onNewUpdate(this.mUserId);
    }

    private void loadActiveStats(long currentTimeMillis) {
        for (int intervalType = 0; intervalType < this.mCurrentStats.length; intervalType++) {
            IntervalStats stats = this.mDatabase.getLatestUsageStats(intervalType);
            if (stats == null || currentTimeMillis >= stats.beginTime + INTERVAL_LENGTH[intervalType]) {
                this.mCurrentStats[intervalType] = new IntervalStats();
                IntervalStats[] intervalStatsArr = this.mCurrentStats;
                intervalStatsArr[intervalType].beginTime = currentTimeMillis;
                intervalStatsArr[intervalType].endTime = 1 + currentTimeMillis;
            } else {
                this.mCurrentStats[intervalType] = stats;
            }
        }
        this.mStatsChanged = false;
        updateRolloverDeadline();
        this.mListener.onStatsReloaded();
    }

    private void updateRolloverDeadline() {
        this.mDailyExpiryDate.setTimeInMillis(this.mCurrentStats[0].beginTime);
        this.mDailyExpiryDate.addDays(1);
        Slog.i(TAG, this.mLogPrefix + "Rollover scheduled @ " + sDateFormat.format(Long.valueOf(this.mDailyExpiryDate.getTimeInMillis())) + "(" + this.mDailyExpiryDate.getTimeInMillis() + ")");
    }

    /* access modifiers changed from: package-private */
    public void checkin(final IndentingPrintWriter pw) {
        this.mDatabase.checkinDailyFiles(new UsageStatsDatabase.CheckinAction() {
            public boolean checkin(IntervalStats stats) {
                UserUsageStatsService.this.printIntervalStats(pw, stats, false, false, (String) null);
                return true;
            }
        });
    }

    /* access modifiers changed from: package-private */
    public void dump(IndentingPrintWriter pw, String pkg) {
        dump(pw, pkg, false);
    }

    /* access modifiers changed from: package-private */
    public void dump(IndentingPrintWriter pw, String pkg, boolean compact) {
        printLast24HrEvents(pw, !compact, pkg);
        for (int interval = 0; interval < this.mCurrentStats.length; interval++) {
            pw.print("In-memory ");
            pw.print(intervalToString(interval));
            pw.println(" stats");
            printIntervalStats(pw, this.mCurrentStats[interval], !compact, true, pkg);
        }
        this.mDatabase.dump(pw, compact);
    }

    /* access modifiers changed from: package-private */
    public void dumpDatabaseInfo(IndentingPrintWriter ipw) {
        this.mDatabase.dump(ipw, false);
    }

    /* access modifiers changed from: package-private */
    public void dumpFile(IndentingPrintWriter ipw, String[] args) {
        int interval;
        if (args == null || args.length == 0) {
            int numIntervals = this.mDatabase.mSortedStatFiles.length;
            for (int interval2 = 0; interval2 < numIntervals; interval2++) {
                ipw.println("interval=" + intervalToString(interval2));
                ipw.increaseIndent();
                dumpFileDetailsForInterval(ipw, interval2);
                ipw.decreaseIndent();
            }
            return;
        }
        try {
            int intervalValue = stringToInterval(args[0]);
            if (intervalValue == -1) {
                interval = Integer.valueOf(args[0]).intValue();
            } else {
                interval = intervalValue;
            }
            if (interval < 0 || interval >= this.mDatabase.mSortedStatFiles.length) {
                ipw.println("the specified interval does not exist.");
            } else if (args.length == 1) {
                dumpFileDetailsForInterval(ipw, interval);
            } else {
                try {
                    IntervalStats stats = this.mDatabase.readIntervalStatsForFile(interval, Long.valueOf(args[1]).longValue());
                    if (stats == null) {
                        ipw.println("the specified filename does not exist.");
                    } else {
                        dumpFileDetails(ipw, stats, Long.valueOf(args[1]).longValue());
                    }
                } catch (NumberFormatException e) {
                    ipw.println("invalid filename specified.");
                }
            }
        } catch (NumberFormatException e2) {
            ipw.println("invalid interval specified.");
        }
    }

    private void dumpFileDetailsForInterval(IndentingPrintWriter ipw, int interval) {
        TimeSparseArray<AtomicFile> files = this.mDatabase.mSortedStatFiles[interval];
        int numFiles = files.size();
        for (int i = 0; i < numFiles; i++) {
            long filename = files.keyAt(i);
            dumpFileDetails(ipw, this.mDatabase.readIntervalStatsForFile(interval, filename), filename);
            ipw.println();
        }
    }

    private void dumpFileDetails(IndentingPrintWriter ipw, IntervalStats stats, long filename) {
        ipw.println("file=" + filename);
        ipw.increaseIndent();
        printIntervalStats(ipw, stats, false, false, (String) null);
        ipw.decreaseIndent();
    }

    static String formatDateTime(long dateTime, boolean pretty) {
        if (!pretty) {
            return Long.toString(dateTime);
        }
        return "\"" + sDateFormat.format(Long.valueOf(dateTime)) + "\"";
    }

    private String formatElapsedTime(long elapsedTime, boolean pretty) {
        if (!pretty) {
            return Long.toString(elapsedTime);
        }
        return "\"" + DateUtils.formatElapsedTime(elapsedTime / 1000) + "\"";
    }

    /* access modifiers changed from: package-private */
    public void printEvent(IndentingPrintWriter pw, UsageEvents.Event event, boolean prettyDates) {
        pw.printPair(SplitScreenReporter.STR_DEAL_TIME, formatDateTime(event.mTimeStamp, prettyDates));
        pw.printPair(DatabaseHelper.SoundModelContract.KEY_TYPE, eventToString(event.mEventType));
        pw.printPair(Settings.ATTR_PACKAGE, event.mPackage);
        if (event.mClass != null) {
            pw.printPair("class", event.mClass);
        }
        if (event.mConfiguration != null) {
            pw.printPair("config", Configuration.resourceQualifierString(event.mConfiguration));
        }
        if (event.mShortcutId != null) {
            pw.printPair("shortcutId", event.mShortcutId);
        }
        if (event.mEventType == 11) {
            pw.printPair("standbyBucket", Integer.valueOf(event.getStandbyBucket()));
            pw.printPair(PhoneWindowManager.SYSTEM_DIALOG_REASON_KEY, UsageStatsManager.reasonToString(event.getStandbyReason()));
        } else if (event.mEventType == 1 || event.mEventType == 2 || event.mEventType == 23) {
            pw.printPair("instanceId", Integer.valueOf(event.getInstanceId()));
        }
        if (event.getTaskRootPackageName() != null) {
            pw.printPair("taskRootPackage", event.getTaskRootPackageName());
        }
        if (event.getTaskRootClassName() != null) {
            pw.printPair("taskRootClass", event.getTaskRootClassName());
        }
        if (event.mNotificationChannelId != null) {
            pw.printPair("channelId", event.mNotificationChannelId);
        }
        pw.printHexPair("flags", event.mFlags);
        pw.println();
    }

    /* access modifiers changed from: package-private */
    public void printLast24HrEvents(IndentingPrintWriter pw, boolean prettyDates, String pkg) {
        IndentingPrintWriter indentingPrintWriter = pw;
        boolean z = prettyDates;
        long endTime = System.currentTimeMillis();
        UnixCalendar yesterday = new UnixCalendar(endTime);
        yesterday.addDays(-1);
        long beginTime = yesterday.getTimeInMillis();
        final long j = beginTime;
        final long j2 = endTime;
        final String str = pkg;
        List<UsageEvents.Event> events = queryStats(0, j, j2, new UsageStatsDatabase.StatCombiner<UsageEvents.Event>() {
            public void combine(IntervalStats stats, boolean mutable, List<UsageEvents.Event> accumulatedResult) {
                int startIndex = stats.events.firstIndexOnOrAfter(j);
                int size = stats.events.size();
                int i = startIndex;
                while (i < size && stats.events.get(i).mTimeStamp < j2) {
                    UsageEvents.Event event = stats.events.get(i);
                    String str = str;
                    if (str == null || str.equals(event.mPackage)) {
                        accumulatedResult.add(event);
                    }
                    i++;
                }
            }
        });
        indentingPrintWriter.print("Last 24 hour events (");
        if (z) {
            StringBuilder sb = new StringBuilder();
            sb.append("\"");
            UnixCalendar unixCalendar = yesterday;
            sb.append(DateUtils.formatDateRange(this.mContext, beginTime, endTime, sDateFormatFlags));
            sb.append("\"");
            indentingPrintWriter.printPair("timeRange", sb.toString());
        } else {
            indentingPrintWriter.printPair("beginTime", Long.valueOf(beginTime));
            indentingPrintWriter.printPair("endTime", Long.valueOf(endTime));
        }
        indentingPrintWriter.println(")");
        if (events != null) {
            pw.increaseIndent();
            for (UsageEvents.Event event : events) {
                printEvent(indentingPrintWriter, event, z);
            }
            pw.decreaseIndent();
        }
    }

    /* access modifiers changed from: package-private */
    public void printEventAggregation(IndentingPrintWriter pw, String label, IntervalStats.EventTracker tracker, boolean prettyDates) {
        if (tracker.count != 0 || tracker.duration != 0) {
            pw.print(label);
            pw.print(": ");
            pw.print(tracker.count);
            pw.print("x for ");
            pw.print(formatElapsedTime(tracker.duration, prettyDates));
            if (tracker.curStartTime != 0) {
                pw.print(" (now running, started at ");
                formatDateTime(tracker.curStartTime, prettyDates);
                pw.print(")");
            }
            pw.println();
        }
    }

    /* access modifiers changed from: package-private */
    public void printIntervalStats(IndentingPrintWriter pw, IntervalStats stats, boolean prettyDates, boolean skipEvents, String pkg) {
        String str;
        Iterator<UsageStats> it;
        String str2;
        IndentingPrintWriter indentingPrintWriter = pw;
        IntervalStats intervalStats = stats;
        boolean z = prettyDates;
        String str3 = pkg;
        if (z) {
            indentingPrintWriter.printPair("timeRange", "\"" + DateUtils.formatDateRange(this.mContext, intervalStats.beginTime, intervalStats.endTime, sDateFormatFlags) + "\"");
        } else {
            indentingPrintWriter.printPair("beginTime", Long.valueOf(intervalStats.beginTime));
            indentingPrintWriter.printPair("endTime", Long.valueOf(intervalStats.endTime));
        }
        pw.println();
        pw.increaseIndent();
        indentingPrintWriter.println("packages");
        pw.increaseIndent();
        ArrayMap<String, UsageStats> pkgStats = intervalStats.packageStats;
        int pkgCount = pkgStats.size();
        int i = 0;
        while (true) {
            str = Settings.ATTR_PACKAGE;
            if (i >= pkgCount) {
                break;
            }
            UsageStats usageStats = pkgStats.valueAt(i);
            if (str3 == null || str3.equals(usageStats.mPackageName)) {
                indentingPrintWriter.printPair(str, usageStats.mPackageName);
                indentingPrintWriter.printPair("totalTimeUsed", formatElapsedTime(usageStats.mTotalTimeInForeground, z));
                indentingPrintWriter.printPair("lastTimeUsed", formatDateTime(usageStats.mLastTimeUsed, z));
                indentingPrintWriter.printPair("totalTimeVisible", formatElapsedTime(usageStats.mTotalTimeVisible, z));
                indentingPrintWriter.printPair("lastTimeVisible", formatDateTime(usageStats.mLastTimeVisible, z));
                indentingPrintWriter.printPair("totalTimeFS", formatElapsedTime(usageStats.mTotalTimeForegroundServiceUsed, z));
                indentingPrintWriter.printPair("lastTimeFS", formatDateTime(usageStats.mLastTimeForegroundServiceUsed, z));
                indentingPrintWriter.printPair("appLaunchCount", Integer.valueOf(usageStats.mAppLaunchCount));
                pw.println();
            }
            i++;
        }
        pw.decreaseIndent();
        pw.println();
        indentingPrintWriter.println("ChooserCounts");
        pw.increaseIndent();
        Iterator<UsageStats> it2 = pkgStats.values().iterator();
        while (it2.hasNext()) {
            UsageStats usageStats2 = it2.next();
            if (str3 == null || str3.equals(usageStats2.mPackageName)) {
                indentingPrintWriter.printPair(str, usageStats2.mPackageName);
                if (usageStats2.mChooserCounts != null) {
                    int chooserCountSize = usageStats2.mChooserCounts.size();
                    int i2 = 0;
                    while (i2 < chooserCountSize) {
                        String action = (String) usageStats2.mChooserCounts.keyAt(i2);
                        ArrayMap<String, Integer> counts = (ArrayMap) usageStats2.mChooserCounts.valueAt(i2);
                        int annotationSize = counts.size();
                        ArrayMap<String, UsageStats> pkgStats2 = pkgStats;
                        int j = 0;
                        while (j < annotationSize) {
                            int pkgCount2 = pkgCount;
                            String key = counts.keyAt(j);
                            int count = counts.valueAt(j).intValue();
                            if (count != 0) {
                                str2 = str;
                                StringBuilder sb = new StringBuilder();
                                sb.append(action);
                                it = it2;
                                sb.append(":");
                                sb.append(key);
                                sb.append(" is ");
                                sb.append(Integer.toString(count));
                                indentingPrintWriter.printPair("ChooserCounts", sb.toString());
                                pw.println();
                            } else {
                                str2 = str;
                                it = it2;
                            }
                            j++;
                            pkgCount = pkgCount2;
                            str = str2;
                            it2 = it;
                        }
                        String str4 = str;
                        Iterator<UsageStats> it3 = it2;
                        i2++;
                        pkgStats = pkgStats2;
                    }
                }
                pw.println();
                pkgStats = pkgStats;
                pkgCount = pkgCount;
                str = str;
                it2 = it2;
            }
        }
        int i3 = pkgCount;
        pw.decreaseIndent();
        if (str3 == null) {
            indentingPrintWriter.println("configurations");
            pw.increaseIndent();
            ArrayMap<Configuration, ConfigurationStats> configStats = intervalStats.configurations;
            int configCount = configStats.size();
            for (int i4 = 0; i4 < configCount; i4++) {
                ConfigurationStats config = configStats.valueAt(i4);
                indentingPrintWriter.printPair("config", Configuration.resourceQualifierString(config.mConfiguration));
                indentingPrintWriter.printPair("totalTime", formatElapsedTime(config.mTotalTimeActive, z));
                indentingPrintWriter.printPair("lastTime", formatDateTime(config.mLastTimeActive, z));
                indentingPrintWriter.printPair(AssistDataRequester.KEY_RECEIVER_EXTRA_COUNT, Integer.valueOf(config.mActivationCount));
                pw.println();
            }
            pw.decreaseIndent();
            indentingPrintWriter.println("event aggregations");
            pw.increaseIndent();
            printEventAggregation(indentingPrintWriter, "screen-interactive", intervalStats.interactiveTracker, z);
            printEventAggregation(indentingPrintWriter, "screen-non-interactive", intervalStats.nonInteractiveTracker, z);
            printEventAggregation(indentingPrintWriter, "keyguard-shown", intervalStats.keyguardShownTracker, z);
            printEventAggregation(indentingPrintWriter, "keyguard-hidden", intervalStats.keyguardHiddenTracker, z);
            pw.decreaseIndent();
        }
        if (!skipEvents) {
            indentingPrintWriter.println("events");
            pw.increaseIndent();
            EventList events = intervalStats.events;
            int eventCount = events != null ? events.size() : 0;
            for (int i5 = 0; i5 < eventCount; i5++) {
                UsageEvents.Event event = events.get(i5);
                if (str3 == null || str3.equals(event.mPackage)) {
                    printEvent(indentingPrintWriter, event, z);
                }
            }
            pw.decreaseIndent();
        }
        pw.decreaseIndent();
    }

    public static String intervalToString(int interval) {
        if (interval == 0) {
            return "daily";
        }
        if (interval == 1) {
            return "weekly";
        }
        if (interval == 2) {
            return "monthly";
        }
        if (interval != 3) {
            return "?";
        }
        return "yearly";
    }

    /* JADX WARNING: Can't fix incorrect switch cases order */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static int stringToInterval(java.lang.String r7) {
        /*
            java.lang.String r0 = r7.toLowerCase()
            int r1 = r0.hashCode()
            r2 = 0
            r3 = -1
            r4 = 3
            r5 = 2
            r6 = 1
            switch(r1) {
                case -791707519: goto L_0x0031;
                case -734561654: goto L_0x0026;
                case 95346201: goto L_0x001c;
                case 1236635661: goto L_0x0011;
                default: goto L_0x0010;
            }
        L_0x0010:
            goto L_0x003c
        L_0x0011:
            java.lang.String r1 = "monthly"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0010
            r0 = r5
            goto L_0x003d
        L_0x001c:
            java.lang.String r1 = "daily"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0010
            r0 = r2
            goto L_0x003d
        L_0x0026:
            java.lang.String r1 = "yearly"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0010
            r0 = r4
            goto L_0x003d
        L_0x0031:
            java.lang.String r1 = "weekly"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0010
            r0 = r6
            goto L_0x003d
        L_0x003c:
            r0 = r3
        L_0x003d:
            if (r0 == 0) goto L_0x0049
            if (r0 == r6) goto L_0x0048
            if (r0 == r5) goto L_0x0047
            if (r0 == r4) goto L_0x0046
            return r3
        L_0x0046:
            return r4
        L_0x0047:
            return r5
        L_0x0048:
            return r6
        L_0x0049:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usage.UserUsageStatsService.stringToInterval(java.lang.String):int");
    }

    private static String eventToString(int eventType) {
        switch (eventType) {
            case 0:
                return "NONE";
            case 1:
                return "ACTIVITY_RESUMED";
            case 2:
                return "ACTIVITY_PAUSED";
            case 3:
                return "END_OF_DAY";
            case 4:
                return "CONTINUE_PREVIOUS_DAY";
            case 5:
                return "CONFIGURATION_CHANGE";
            case 6:
                return "SYSTEM_INTERACTION";
            case 7:
                return "USER_INTERACTION";
            case 8:
                return "SHORTCUT_INVOCATION";
            case 9:
                return "CHOOSER_ACTION";
            case 10:
                return "NOTIFICATION_SEEN";
            case 11:
                return "STANDBY_BUCKET_CHANGED";
            case 12:
                return "NOTIFICATION_INTERRUPTION";
            case 13:
                return "SLICE_PINNED_PRIV";
            case 14:
                return "SLICE_PINNED";
            case 15:
                return "SCREEN_INTERACTIVE";
            case 16:
                return "SCREEN_NON_INTERACTIVE";
            case 17:
                return "KEYGUARD_SHOWN";
            case 18:
                return "KEYGUARD_HIDDEN";
            case 19:
                return "FOREGROUND_SERVICE_START";
            case 20:
                return "FOREGROUND_SERVICE_STOP";
            case 21:
                return "CONTINUING_FOREGROUND_SERVICE";
            case 22:
                return "ROLLOVER_FOREGROUND_SERVICE";
            case WindowManagerService.H.BOOT_TIMEOUT /*23*/:
                return "ACTIVITY_STOPPED";
            case 26:
                return "DEVICE_SHUTDOWN";
            case 27:
                return "DEVICE_STARTUP";
            default:
                return "UNKNOWN_TYPE_" + eventType;
        }
    }

    /* access modifiers changed from: package-private */
    public byte[] getBackupPayload(String key) {
        return this.mDatabase.getBackupPayload(key);
    }

    /* access modifiers changed from: package-private */
    public void applyRestoredPayload(String key, byte[] payload) {
        this.mDatabase.applyRestoredPayload(key, payload);
    }
}
