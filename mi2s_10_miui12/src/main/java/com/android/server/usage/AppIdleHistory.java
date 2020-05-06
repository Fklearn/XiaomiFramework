package com.android.server.usage;

import android.app.usage.AppStandbyInfo;
import android.app.usage.UsageStatsManager;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.job.controllers.JobStatus;
import com.android.server.usb.descriptors.UsbTerminalTypes;
import com.android.server.voiceinteraction.DatabaseHelper;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;

public class AppIdleHistory {
    @VisibleForTesting
    static final String APP_IDLE_FILENAME = "app_idle_stats.xml";
    private static final String ATTR_BUCKETING_REASON = "bucketReason";
    private static final String ATTR_BUCKET_ACTIVE_TIMEOUT_TIME = "activeTimeoutTime";
    private static final String ATTR_BUCKET_WORKING_SET_TIMEOUT_TIME = "workingSetTimeoutTime";
    private static final String ATTR_CURRENT_BUCKET = "appLimitBucket";
    private static final String ATTR_ELAPSED_IDLE = "elapsedIdleTime";
    private static final String ATTR_LAST_PREDICTED_TIME = "lastPredictedTime";
    private static final String ATTR_LAST_RUN_JOB_TIME = "lastJobRunTime";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_SCREEN_IDLE = "screenIdleTime";
    private static final boolean DEBUG = false;
    private static final long ONE_MINUTE = 60000;
    private static final int STANDBY_BUCKET_UNKNOWN = -1;
    private static final String TAG = "AppIdleHistory";
    private static final String TAG_PACKAGE = "package";
    private static final String TAG_PACKAGES = "packages";
    private long mElapsedDuration;
    private long mElapsedSnapshot;
    private SparseArray<ArrayMap<String, AppUsageHistory>> mIdleHistory = new SparseArray<>();
    private boolean mScreenOn;
    private long mScreenOnDuration;
    private long mScreenOnSnapshot;
    private final File mStorageDir;

    static class AppUsageHistory {
        long bucketActiveTimeoutTime;
        long bucketWorkingSetTimeoutTime;
        int bucketingReason;
        int currentBucket;
        int lastInformedBucket;
        long lastJobRunTime;
        int lastPredictedBucket = -1;
        long lastPredictedTime;
        long lastUsedElapsedTime;
        long lastUsedScreenTime;

        AppUsageHistory() {
        }
    }

    AppIdleHistory(File storageDir, long elapsedRealtime) {
        this.mElapsedSnapshot = elapsedRealtime;
        this.mScreenOnSnapshot = elapsedRealtime;
        this.mStorageDir = storageDir;
        readScreenOnTime();
    }

    public void updateDisplay(boolean screenOn, long elapsedRealtime) {
        if (screenOn != this.mScreenOn) {
            this.mScreenOn = screenOn;
            if (this.mScreenOn) {
                this.mScreenOnSnapshot = elapsedRealtime;
                return;
            }
            this.mScreenOnDuration += elapsedRealtime - this.mScreenOnSnapshot;
            this.mElapsedDuration += elapsedRealtime - this.mElapsedSnapshot;
            this.mElapsedSnapshot = elapsedRealtime;
        }
    }

    public long getScreenOnTime(long elapsedRealtime) {
        long screenOnTime = this.mScreenOnDuration;
        if (this.mScreenOn) {
            return screenOnTime + (elapsedRealtime - this.mScreenOnSnapshot);
        }
        return screenOnTime;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public File getScreenOnTimeFile() {
        return new File(this.mStorageDir, "screen_on_time");
    }

    private void readScreenOnTime() {
        File screenOnTimeFile = getScreenOnTimeFile();
        if (screenOnTimeFile.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(screenOnTimeFile));
                this.mScreenOnDuration = Long.parseLong(reader.readLine());
                this.mElapsedDuration = Long.parseLong(reader.readLine());
                reader.close();
            } catch (IOException | NumberFormatException e) {
            }
        } else {
            writeScreenOnTime();
        }
    }

    private void writeScreenOnTime() {
        AtomicFile screenOnTimeFile = new AtomicFile(getScreenOnTimeFile());
        FileOutputStream fos = null;
        try {
            fos = screenOnTimeFile.startWrite();
            fos.write((Long.toString(this.mScreenOnDuration) + "\n" + Long.toString(this.mElapsedDuration) + "\n").getBytes());
            screenOnTimeFile.finishWrite(fos);
        } catch (IOException e) {
            screenOnTimeFile.failWrite(fos);
        }
    }

    public void writeAppIdleDurations() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        this.mElapsedDuration += elapsedRealtime - this.mElapsedSnapshot;
        this.mElapsedSnapshot = elapsedRealtime;
        writeScreenOnTime();
    }

    public AppUsageHistory reportUsage(AppUsageHistory appUsageHistory, String packageName, int newBucket, int usageReason, long elapsedRealtime, long timeout) {
        if (timeout > elapsedRealtime) {
            long timeoutTime = this.mElapsedDuration + (timeout - this.mElapsedSnapshot);
            if (newBucket == 10) {
                appUsageHistory.bucketActiveTimeoutTime = Math.max(timeoutTime, appUsageHistory.bucketActiveTimeoutTime);
            } else if (newBucket == 20) {
                appUsageHistory.bucketWorkingSetTimeoutTime = Math.max(timeoutTime, appUsageHistory.bucketWorkingSetTimeoutTime);
            } else {
                throw new IllegalArgumentException("Cannot set a timeout on bucket=" + newBucket);
            }
        }
        if (elapsedRealtime != 0) {
            appUsageHistory.lastUsedElapsedTime = this.mElapsedDuration + (elapsedRealtime - this.mElapsedSnapshot);
            appUsageHistory.lastUsedScreenTime = getScreenOnTime(elapsedRealtime);
        }
        if (appUsageHistory.currentBucket > newBucket) {
            appUsageHistory.currentBucket = newBucket;
        }
        appUsageHistory.bucketingReason = usageReason | 768;
        return appUsageHistory;
    }

    public AppUsageHistory reportUsage(String packageName, int userId, int newBucket, int usageReason, long nowElapsed, long timeout) {
        int i = userId;
        String str = packageName;
        return reportUsage(getPackageHistory(getUserHistory(userId), str, nowElapsed, true), str, newBucket, usageReason, nowElapsed, timeout);
    }

    private ArrayMap<String, AppUsageHistory> getUserHistory(int userId) {
        ArrayMap<String, AppUsageHistory> userHistory = this.mIdleHistory.get(userId);
        if (userHistory != null) {
            return userHistory;
        }
        ArrayMap<String, AppUsageHistory> userHistory2 = new ArrayMap<>();
        this.mIdleHistory.put(userId, userHistory2);
        readAppIdleTimes(userId, userHistory2);
        return userHistory2;
    }

    private AppUsageHistory getPackageHistory(ArrayMap<String, AppUsageHistory> userHistory, String packageName, long elapsedRealtime, boolean create) {
        AppUsageHistory appUsageHistory = userHistory.get(packageName);
        if (appUsageHistory != null || !create) {
            return appUsageHistory;
        }
        AppUsageHistory appUsageHistory2 = new AppUsageHistory();
        appUsageHistory2.lastUsedElapsedTime = getElapsedTime(elapsedRealtime);
        appUsageHistory2.lastUsedScreenTime = getScreenOnTime(elapsedRealtime);
        appUsageHistory2.lastPredictedTime = getElapsedTime(0);
        appUsageHistory2.currentBucket = 50;
        appUsageHistory2.bucketingReason = 256;
        appUsageHistory2.lastInformedBucket = -1;
        appUsageHistory2.lastJobRunTime = Long.MIN_VALUE;
        userHistory.put(packageName, appUsageHistory2);
        return appUsageHistory2;
    }

    public void onUserRemoved(int userId) {
        this.mIdleHistory.remove(userId);
    }

    public boolean isIdle(String packageName, int userId, long elapsedRealtime) {
        return getPackageHistory(getUserHistory(userId), packageName, elapsedRealtime, true).currentBucket >= 40;
    }

    public AppUsageHistory getAppUsageHistory(String packageName, int userId, long elapsedRealtime) {
        return getPackageHistory(getUserHistory(userId), packageName, elapsedRealtime, true);
    }

    public void setAppStandbyBucket(String packageName, int userId, long elapsedRealtime, int bucket, int reason) {
        setAppStandbyBucket(packageName, userId, elapsedRealtime, bucket, reason, false);
    }

    public void setAppStandbyBucket(String packageName, int userId, long elapsedRealtime, int bucket, int reason, boolean resetTimeout) {
        AppUsageHistory appUsageHistory = getPackageHistory(getUserHistory(userId), packageName, elapsedRealtime, true);
        appUsageHistory.currentBucket = bucket;
        appUsageHistory.bucketingReason = reason;
        long elapsed = getElapsedTime(elapsedRealtime);
        if ((65280 & reason) == 1280) {
            appUsageHistory.lastPredictedTime = elapsed;
            appUsageHistory.lastPredictedBucket = bucket;
        }
        if (resetTimeout) {
            appUsageHistory.bucketActiveTimeoutTime = elapsed;
            appUsageHistory.bucketWorkingSetTimeoutTime = elapsed;
        }
    }

    public void updateLastPrediction(AppUsageHistory app, long elapsedTimeAdjusted, int bucket) {
        app.lastPredictedTime = elapsedTimeAdjusted;
        app.lastPredictedBucket = bucket;
    }

    public void setLastJobRunTime(String packageName, int userId, long elapsedRealtime) {
        getPackageHistory(getUserHistory(userId), packageName, elapsedRealtime, true).lastJobRunTime = getElapsedTime(elapsedRealtime);
    }

    public long getTimeSinceLastJobRun(String packageName, int userId, long elapsedRealtime) {
        AppUsageHistory appUsageHistory = getPackageHistory(getUserHistory(userId), packageName, elapsedRealtime, false);
        if (appUsageHistory == null || appUsageHistory.lastJobRunTime == Long.MIN_VALUE) {
            return JobStatus.NO_LATEST_RUNTIME;
        }
        return getElapsedTime(elapsedRealtime) - appUsageHistory.lastJobRunTime;
    }

    public int getAppStandbyBucket(String packageName, int userId, long elapsedRealtime) {
        AppUsageHistory appUsageHistory = getPackageHistory(getUserHistory(userId), packageName, elapsedRealtime, false);
        if (appUsageHistory == null) {
            return 50;
        }
        return appUsageHistory.currentBucket;
    }

    public ArrayList<AppStandbyInfo> getAppStandbyBuckets(int userId, boolean appIdleEnabled) {
        ArrayMap<String, AppUsageHistory> userHistory = getUserHistory(userId);
        int size = userHistory.size();
        ArrayList<AppStandbyInfo> buckets = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            buckets.add(new AppStandbyInfo(userHistory.keyAt(i), appIdleEnabled ? userHistory.valueAt(i).currentBucket : 10));
        }
        return buckets;
    }

    public int getAppStandbyReason(String packageName, int userId, long elapsedRealtime) {
        AppUsageHistory appUsageHistory = getPackageHistory(getUserHistory(userId), packageName, elapsedRealtime, false);
        if (appUsageHistory != null) {
            return appUsageHistory.bucketingReason;
        }
        return 0;
    }

    public long getElapsedTime(long elapsedRealtime) {
        return (elapsedRealtime - this.mElapsedSnapshot) + this.mElapsedDuration;
    }

    public int setIdle(String packageName, int userId, boolean idle, long elapsedRealtime) {
        AppUsageHistory appUsageHistory = getPackageHistory(getUserHistory(userId), packageName, elapsedRealtime, true);
        if (idle) {
            appUsageHistory.currentBucket = 40;
            appUsageHistory.bucketingReason = 1024;
        } else {
            appUsageHistory.currentBucket = 10;
            appUsageHistory.bucketingReason = UsbTerminalTypes.TERMINAL_OUT_HEADMOUNTED;
        }
        return appUsageHistory.currentBucket;
    }

    public void clearUsage(String packageName, int userId) {
        getUserHistory(userId).remove(packageName);
    }

    /* access modifiers changed from: package-private */
    public boolean shouldInformListeners(String packageName, int userId, long elapsedRealtime, int bucket) {
        AppUsageHistory appUsageHistory = getPackageHistory(getUserHistory(userId), packageName, elapsedRealtime, true);
        if (appUsageHistory.lastInformedBucket == bucket) {
            return false;
        }
        appUsageHistory.lastInformedBucket = bucket;
        return true;
    }

    /* access modifiers changed from: package-private */
    public int getThresholdIndex(String packageName, int userId, long elapsedRealtime, long[] screenTimeThresholds, long[] elapsedTimeThresholds) {
        AppUsageHistory appUsageHistory = getPackageHistory(getUserHistory(userId), packageName, elapsedRealtime, false);
        if (appUsageHistory == null) {
            return screenTimeThresholds.length - 1;
        }
        long screenOnDelta = getScreenOnTime(elapsedRealtime) - appUsageHistory.lastUsedScreenTime;
        long elapsedDelta = getElapsedTime(elapsedRealtime) - appUsageHistory.lastUsedElapsedTime;
        for (int i = screenTimeThresholds.length - 1; i >= 0; i--) {
            if (screenOnDelta >= screenTimeThresholds[i] && elapsedDelta >= elapsedTimeThresholds[i]) {
                return i;
            }
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public File getUserFile(int userId) {
        return new File(new File(new File(this.mStorageDir, DatabaseHelper.SoundModelContract.KEY_USERS), Integer.toString(userId)), APP_IDLE_FILENAME);
    }

    public boolean userFileExists(int userId) {
        return getUserFile(userId).exists();
    }

    /* JADX WARNING: Code restructure failed: missing block: B:43:0x010d, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x010e, code lost:
        r9 = r20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x0112, code lost:
        r9 = r20;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:52:0x012d, code lost:
        libcore.io.IoUtils.closeQuietly(r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x0130, code lost:
        throw r0;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:11:0x004a A[SYNTHETIC, Splitter:B:11:0x004a] */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x0111 A[ExcHandler: IOException | XmlPullParserException (e java.lang.Throwable), PHI: r5 
      PHI: (r5v4 'fis' java.io.FileInputStream) = (r5v0 'fis' java.io.FileInputStream), (r5v6 'fis' java.io.FileInputStream), (r5v6 'fis' java.io.FileInputStream), (r5v6 'fis' java.io.FileInputStream), (r5v6 'fis' java.io.FileInputStream), (r5v6 'fis' java.io.FileInputStream), (r5v6 'fis' java.io.FileInputStream) binds: [B:1:0x0009, B:11:0x004a, B:16:0x005b, B:33:0x00f4, B:34:?, B:29:0x00eb, B:30:?] A[DONT_GENERATE, DONT_INLINE], Splitter:B:1:0x0009] */
    /* JADX WARNING: Removed duplicated region for block: B:8:0x0034 A[Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void readAppIdleTimes(int r19, android.util.ArrayMap<java.lang.String, com.android.server.usage.AppIdleHistory.AppUsageHistory> r20) {
        /*
            r18 = this;
            r1 = r18
            r2 = r19
            java.lang.String r3 = "Unable to read app idle file for user "
            java.lang.String r4 = "AppIdleHistory"
            r5 = 0
            android.util.AtomicFile r0 = new android.util.AtomicFile     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            java.io.File r6 = r18.getUserFile(r19)     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            r0.<init>(r6)     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            r6 = r0
            java.io.FileInputStream r0 = r6.openRead()     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            r5 = r0
            org.xmlpull.v1.XmlPullParser r0 = android.util.Xml.newPullParser()     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            r7 = r0
            java.nio.charset.Charset r0 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            java.lang.String r0 = r0.name()     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            r7.setInput(r5, r0)     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
        L_0x0026:
            int r0 = r7.next()     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            r8 = r0
            r9 = 1
            r10 = 2
            if (r0 == r10) goto L_0x0032
            if (r8 == r9) goto L_0x0032
            goto L_0x0026
        L_0x0032:
            if (r8 == r10) goto L_0x004a
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            r0.<init>()     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            r0.append(r3)     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            r0.append(r2)     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            java.lang.String r0 = r0.toString()     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            android.util.Slog.e(r4, r0)     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            libcore.io.IoUtils.closeQuietly(r5)
            return
        L_0x004a:
            java.lang.String r0 = r7.getName()     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            java.lang.String r11 = "packages"
            boolean r0 = r0.equals(r11)     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            if (r0 != 0) goto L_0x005b
            libcore.io.IoUtils.closeQuietly(r5)
            return
        L_0x005b:
            int r0 = r7.next()     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            r8 = r0
            if (r0 == r9) goto L_0x010a
            if (r8 != r10) goto L_0x0104
            java.lang.String r0 = r7.getName()     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            r11 = r0
            java.lang.String r0 = "package"
            boolean r0 = r11.equals(r0)     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            if (r0 == 0) goto L_0x0100
            java.lang.String r0 = "name"
            r12 = 0
            java.lang.String r0 = r7.getAttributeValue(r12, r0)     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            r13 = r0
            com.android.server.usage.AppIdleHistory$AppUsageHistory r0 = new com.android.server.usage.AppIdleHistory$AppUsageHistory     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            r0.<init>()     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            r14 = r0
            java.lang.String r0 = "elapsedIdleTime"
            java.lang.String r0 = r7.getAttributeValue(r12, r0)     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            long r9 = java.lang.Long.parseLong(r0)     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            r14.lastUsedElapsedTime = r9     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            java.lang.String r0 = "screenIdleTime"
            java.lang.String r0 = r7.getAttributeValue(r12, r0)     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            long r9 = java.lang.Long.parseLong(r0)     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            r14.lastUsedScreenTime = r9     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            java.lang.String r0 = "lastPredictedTime"
            r9 = 0
            r16 = r13
            long r12 = r1.getLongValue(r7, r0, r9)     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            r14.lastPredictedTime = r12     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            java.lang.String r0 = "appLimitBucket"
            r12 = 0
            java.lang.String r0 = r7.getAttributeValue(r12, r0)     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            r12 = r0
            if (r12 != 0) goto L_0x00b4
            r0 = 10
            goto L_0x00b8
        L_0x00b4:
            int r0 = java.lang.Integer.parseInt(r12)     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
        L_0x00b8:
            r14.currentBucket = r0     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            java.lang.String r0 = "bucketReason"
            r13 = 0
            java.lang.String r0 = r7.getAttributeValue(r13, r0)     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            r13 = r0
            java.lang.String r0 = "lastJobRunTime"
            r9 = -9223372036854775808
            long r9 = r1.getLongValue(r7, r0, r9)     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            r14.lastJobRunTime = r9     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            java.lang.String r0 = "activeTimeoutTime"
            r15 = r11
            r17 = r12
            r9 = 0
            long r11 = r1.getLongValue(r7, r0, r9)     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            r14.bucketActiveTimeoutTime = r11     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            java.lang.String r0 = "workingSetTimeoutTime"
            long r9 = r1.getLongValue(r7, r0, r9)     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            r14.bucketWorkingSetTimeoutTime = r9     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            r0 = 256(0x100, float:3.59E-43)
            r14.bucketingReason = r0     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            if (r13 == 0) goto L_0x00f3
            r0 = 16
            int r0 = java.lang.Integer.parseInt(r13, r0)     // Catch:{ NumberFormatException -> 0x00f2, IOException | XmlPullParserException -> 0x0111 }
            r14.bucketingReason = r0     // Catch:{ NumberFormatException -> 0x00f2, IOException | XmlPullParserException -> 0x0111 }
            goto L_0x00f3
        L_0x00f2:
            r0 = move-exception
        L_0x00f3:
            r0 = -1
            r14.lastInformedBucket = r0     // Catch:{ IOException | XmlPullParserException -> 0x0111, all -> 0x010d }
            r9 = r20
            r10 = r16
            r9.put(r10, r14)     // Catch:{ IOException | XmlPullParserException -> 0x00fe }
            goto L_0x0103
        L_0x00fe:
            r0 = move-exception
            goto L_0x0114
        L_0x0100:
            r9 = r20
            r15 = r11
        L_0x0103:
            goto L_0x0106
        L_0x0104:
            r9 = r20
        L_0x0106:
            r9 = 1
            r10 = 2
            goto L_0x005b
        L_0x010a:
            r9 = r20
            goto L_0x0127
        L_0x010d:
            r0 = move-exception
            r9 = r20
            goto L_0x012d
        L_0x0111:
            r0 = move-exception
            r9 = r20
        L_0x0114:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x012c }
            r6.<init>()     // Catch:{ all -> 0x012c }
            r6.append(r3)     // Catch:{ all -> 0x012c }
            r6.append(r2)     // Catch:{ all -> 0x012c }
            java.lang.String r3 = r6.toString()     // Catch:{ all -> 0x012c }
            android.util.Slog.e(r4, r3)     // Catch:{ all -> 0x012c }
        L_0x0127:
            libcore.io.IoUtils.closeQuietly(r5)
            return
        L_0x012c:
            r0 = move-exception
        L_0x012d:
            libcore.io.IoUtils.closeQuietly(r5)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usage.AppIdleHistory.readAppIdleTimes(int, android.util.ArrayMap):void");
    }

    private long getLongValue(XmlPullParser parser, String attrName, long defValue) {
        String value = parser.getAttributeValue((String) null, attrName);
        if (value == null) {
            return defValue;
        }
        return Long.parseLong(value);
    }

    public void writeAppIdleTimes(int userId) {
        AtomicFile appIdleFile = new AtomicFile(getUserFile(userId));
        try {
            FileOutputStream fos = appIdleFile.startWrite();
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            FastXmlSerializer xml = new FastXmlSerializer();
            xml.setOutput(bos, StandardCharsets.UTF_8.name());
            xml.startDocument((String) null, true);
            xml.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
            xml.startTag((String) null, TAG_PACKAGES);
            ArrayMap<String, AppUsageHistory> userHistory = getUserHistory(userId);
            int N = userHistory.size();
            for (int i = 0; i < N; i++) {
                AppUsageHistory history = userHistory.valueAt(i);
                xml.startTag((String) null, "package");
                xml.attribute((String) null, "name", userHistory.keyAt(i));
                xml.attribute((String) null, ATTR_ELAPSED_IDLE, Long.toString(history.lastUsedElapsedTime));
                xml.attribute((String) null, ATTR_SCREEN_IDLE, Long.toString(history.lastUsedScreenTime));
                xml.attribute((String) null, ATTR_LAST_PREDICTED_TIME, Long.toString(history.lastPredictedTime));
                xml.attribute((String) null, ATTR_CURRENT_BUCKET, Integer.toString(history.currentBucket));
                xml.attribute((String) null, ATTR_BUCKETING_REASON, Integer.toHexString(history.bucketingReason));
                if (history.bucketActiveTimeoutTime > 0) {
                    xml.attribute((String) null, ATTR_BUCKET_ACTIVE_TIMEOUT_TIME, Long.toString(history.bucketActiveTimeoutTime));
                }
                if (history.bucketWorkingSetTimeoutTime > 0) {
                    xml.attribute((String) null, ATTR_BUCKET_WORKING_SET_TIMEOUT_TIME, Long.toString(history.bucketWorkingSetTimeoutTime));
                }
                if (history.lastJobRunTime != Long.MIN_VALUE) {
                    xml.attribute((String) null, ATTR_LAST_RUN_JOB_TIME, Long.toString(history.lastJobRunTime));
                }
                xml.endTag((String) null, "package");
            }
            xml.endTag((String) null, TAG_PACKAGES);
            xml.endDocument();
            appIdleFile.finishWrite(fos);
            int i2 = userId;
        } catch (Exception e) {
            appIdleFile.failWrite((FileOutputStream) null);
            Slog.e(TAG, "Error writing app idle file for user " + userId);
        }
    }

    public void dump(IndentingPrintWriter idpw, int userId, String pkg) {
        ArrayMap<String, AppUsageHistory> userHistory;
        IndentingPrintWriter indentingPrintWriter = idpw;
        int i = userId;
        String str = pkg;
        indentingPrintWriter.println("App Standby States:");
        idpw.increaseIndent();
        ArrayMap<String, AppUsageHistory> userHistory2 = this.mIdleHistory.get(i);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long totalElapsedTime = getElapsedTime(elapsedRealtime);
        long screenOnTime = getScreenOnTime(elapsedRealtime);
        if (userHistory2 != null) {
            int P = userHistory2.size();
            int p = 0;
            while (p < P) {
                String packageName = userHistory2.keyAt(p);
                AppUsageHistory appUsageHistory = userHistory2.valueAt(p);
                if (str == null || str.equals(packageName)) {
                    indentingPrintWriter.print("package=" + packageName);
                    indentingPrintWriter.print(" u=" + i);
                    indentingPrintWriter.print(" bucket=" + appUsageHistory.currentBucket + " reason=" + UsageStatsManager.reasonToString(appUsageHistory.bucketingReason));
                    indentingPrintWriter.print(" used=");
                    userHistory = userHistory2;
                    TimeUtils.formatDuration(totalElapsedTime - appUsageHistory.lastUsedElapsedTime, indentingPrintWriter);
                    indentingPrintWriter.print(" usedScr=");
                    TimeUtils.formatDuration(screenOnTime - appUsageHistory.lastUsedScreenTime, indentingPrintWriter);
                    indentingPrintWriter.print(" lastPred=");
                    TimeUtils.formatDuration(totalElapsedTime - appUsageHistory.lastPredictedTime, indentingPrintWriter);
                    indentingPrintWriter.print(" activeLeft=");
                    TimeUtils.formatDuration(appUsageHistory.bucketActiveTimeoutTime - totalElapsedTime, indentingPrintWriter);
                    indentingPrintWriter.print(" wsLeft=");
                    TimeUtils.formatDuration(appUsageHistory.bucketWorkingSetTimeoutTime - totalElapsedTime, indentingPrintWriter);
                    indentingPrintWriter.print(" lastJob=");
                    TimeUtils.formatDuration(totalElapsedTime - appUsageHistory.lastJobRunTime, indentingPrintWriter);
                    StringBuilder sb = new StringBuilder();
                    sb.append(" idle=");
                    sb.append(isIdle(packageName, i, elapsedRealtime) ? "y" : "n");
                    indentingPrintWriter.print(sb.toString());
                    idpw.println();
                } else {
                    userHistory = userHistory2;
                }
                p++;
                str = pkg;
                userHistory2 = userHistory;
            }
            idpw.println();
            indentingPrintWriter.print("totalElapsedTime=");
            TimeUtils.formatDuration(getElapsedTime(elapsedRealtime), indentingPrintWriter);
            idpw.println();
            indentingPrintWriter.print("totalScreenOnTime=");
            TimeUtils.formatDuration(getScreenOnTime(elapsedRealtime), indentingPrintWriter);
            idpw.println();
            idpw.decreaseIndent();
        }
    }
}
