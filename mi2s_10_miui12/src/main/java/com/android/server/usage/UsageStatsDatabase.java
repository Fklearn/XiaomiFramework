package com.android.server.usage;

import android.app.usage.TimeSparseArray;
import android.app.usage.UsageStats;
import android.os.Build;
import android.os.SystemProperties;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.TimeUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.IndentingPrintWriter;
import com.android.server.job.controllers.JobStatus;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import libcore.io.IoUtils;

public class UsageStatsDatabase {
    @VisibleForTesting
    public static final int BACKUP_VERSION = 4;
    private static final String BAK_SUFFIX = ".bak";
    private static final String CHECKED_IN_SUFFIX = "-c";
    private static final boolean DEBUG = false;
    private static final int DEFAULT_CURRENT_VERSION = 4;
    static final boolean KEEP_BACKUP_DIR = false;
    static final String KEY_USAGE_STATS = "usage_stats";
    @VisibleForTesting
    static final int[] MAX_FILES_PER_INTERVAL_TYPE = {100, 50, 12, 10};
    private static final String RETENTION_LEN_KEY = "ro.usagestats.chooser.retention";
    private static final int SELECTION_LOG_RETENTION_LEN = SystemProperties.getInt(RETENTION_LEN_KEY, 14);
    private static final String TAG = "UsageStatsDatabase";
    private static final int UPGRADE_FILE_TIMEOUT = 30000;
    private final File mBackupsDir;
    private final UnixCalendar mCal;
    private int mCurrentVersion;
    private boolean mFirstUpdate;
    private final File[] mIntervalDirs;
    private final Object mLock;
    private boolean mNewUpdate;
    @VisibleForTesting
    final TimeSparseArray<AtomicFile>[] mSortedStatFiles;
    private final File mUpdateBreadcrumb;
    private final File mVersionFile;

    public interface CheckinAction {
        boolean checkin(IntervalStats intervalStats);
    }

    public interface StatCombiner<T> {
        void combine(IntervalStats intervalStats, boolean z, List<T> list);
    }

    @VisibleForTesting
    public UsageStatsDatabase(File dir, int version) {
        this.mLock = new Object();
        this.mIntervalDirs = new File[]{new File(dir, "daily"), new File(dir, "weekly"), new File(dir, "monthly"), new File(dir, "yearly")};
        this.mCurrentVersion = version;
        this.mVersionFile = new File(dir, "version");
        this.mBackupsDir = new File(dir, "backups");
        this.mUpdateBreadcrumb = new File(dir, "breadcrumb");
        this.mSortedStatFiles = new TimeSparseArray[this.mIntervalDirs.length];
        this.mCal = new UnixCalendar(0);
    }

    public UsageStatsDatabase(File dir) {
        this(dir, 4);
    }

    /* Debug info: failed to restart local var, previous not found, register: 9 */
    public void init(long currentTimeMillis) {
        synchronized (this.mLock) {
            File[] fileArr = this.mIntervalDirs;
            int length = fileArr.length;
            int i = 0;
            while (i < length) {
                File f = fileArr[i];
                f.mkdirs();
                if (f.exists()) {
                    i++;
                } else {
                    throw new IllegalStateException("Failed to create directory " + f.getAbsolutePath());
                }
            }
            checkVersionAndBuildLocked();
            indexFilesLocked();
            for (TimeSparseArray<AtomicFile> files : this.mSortedStatFiles) {
                int startIndex = files.closestIndexOnOrAfter(currentTimeMillis);
                if (startIndex >= 0) {
                    int fileCount = files.size();
                    for (int i2 = startIndex; i2 < fileCount; i2++) {
                        ((AtomicFile) files.valueAt(i2)).delete();
                    }
                    for (int i3 = startIndex; i3 < fileCount; i3++) {
                        files.removeAt(i3);
                    }
                }
            }
        }
    }

    public boolean checkinDailyFiles(CheckinAction checkinAction) {
        synchronized (this.mLock) {
            TimeSparseArray<AtomicFile> files = this.mSortedStatFiles[0];
            int fileCount = files.size();
            int lastCheckin = -1;
            for (int i = 0; i < fileCount - 1; i++) {
                if (((AtomicFile) files.valueAt(i)).getBaseFile().getPath().endsWith(CHECKED_IN_SUFFIX)) {
                    lastCheckin = i;
                }
            }
            int start = lastCheckin + 1;
            if (start == fileCount - 1) {
                return true;
            }
            try {
                IntervalStats stats = new IntervalStats();
                for (int i2 = start; i2 < fileCount - 1; i2++) {
                    readLocked((AtomicFile) files.valueAt(i2), stats);
                    if (!checkinAction.checkin(stats)) {
                        return false;
                    }
                }
                for (int i3 = start; i3 < fileCount - 1; i3++) {
                    AtomicFile file = (AtomicFile) files.valueAt(i3);
                    File checkedInFile = new File(file.getBaseFile().getPath() + CHECKED_IN_SUFFIX);
                    if (!file.getBaseFile().renameTo(checkedInFile)) {
                        Slog.e(TAG, "Failed to mark file " + file.getBaseFile().getPath() + " as checked-in");
                        return true;
                    }
                    files.setValueAt(i3, new AtomicFile(checkedInFile));
                }
                return true;
            } catch (IOException e) {
                Slog.e(TAG, "Failed to check-in", e);
                return false;
            }
        }
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public void forceIndexFiles() {
        synchronized (this.mLock) {
            indexFilesLocked();
        }
    }

    private void indexFilesLocked() {
        FilenameFilter backupFileFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return !name.endsWith(UsageStatsDatabase.BAK_SUFFIX);
            }
        };
        int i = 0;
        while (true) {
            TimeSparseArray<AtomicFile>[] timeSparseArrayArr = this.mSortedStatFiles;
            if (i < timeSparseArrayArr.length) {
                if (timeSparseArrayArr[i] == null) {
                    timeSparseArrayArr[i] = new TimeSparseArray<>();
                } else {
                    timeSparseArrayArr[i].clear();
                }
                File[] files = this.mIntervalDirs[i].listFiles(backupFileFilter);
                if (files != null) {
                    for (File f : files) {
                        AtomicFile af = new AtomicFile(f);
                        try {
                            this.mSortedStatFiles[i].put(parseBeginTime(af), af);
                        } catch (IOException e) {
                            Slog.e(TAG, "failed to index file: " + f, e);
                        }
                    }
                    int toDelete = this.mSortedStatFiles[i].size() - MAX_FILES_PER_INTERVAL_TYPE[i];
                    if (toDelete > 0) {
                        for (int j = 0; j < toDelete; j++) {
                            ((AtomicFile) this.mSortedStatFiles[i].valueAt(0)).delete();
                            this.mSortedStatFiles[i].removeAt(0);
                        }
                        Slog.d(TAG, "Deleted " + toDelete + " stat files for interval " + i);
                    }
                }
                i++;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean isFirstUpdate() {
        return this.mFirstUpdate;
    }

    /* access modifiers changed from: package-private */
    public boolean isNewUpdate() {
        return this.mNewUpdate;
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0037, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:?, code lost:
        $closeResource(r4, r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x003b, code lost:
        throw r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:0x00f2, code lost:
        r3 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:?, code lost:
        $closeResource(r2, r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x00f6, code lost:
        throw r3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x0120, code lost:
        r2 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:?, code lost:
        $closeResource(r0, r3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x0124, code lost:
        throw r2;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void checkVersionAndBuildLocked() {
        /*
            r8 = this;
            java.lang.String r0 = "\n"
            java.lang.String r1 = r8.getBuildFingerprint()
            r2 = 1
            r8.mFirstUpdate = r2
            r8.mNewUpdate = r2
            r2 = 0
            java.io.BufferedReader r3 = new java.io.BufferedReader     // Catch:{ IOException | NumberFormatException -> 0x003c }
            java.io.FileReader r4 = new java.io.FileReader     // Catch:{ IOException | NumberFormatException -> 0x003c }
            java.io.File r5 = r8.mVersionFile     // Catch:{ IOException | NumberFormatException -> 0x003c }
            r4.<init>(r5)     // Catch:{ IOException | NumberFormatException -> 0x003c }
            r3.<init>(r4)     // Catch:{ IOException | NumberFormatException -> 0x003c }
            java.lang.String r4 = r3.readLine()     // Catch:{ all -> 0x0035 }
            int r4 = java.lang.Integer.parseInt(r4)     // Catch:{ all -> 0x0035 }
            java.lang.String r5 = r3.readLine()     // Catch:{ all -> 0x0035 }
            r6 = 0
            if (r5 == 0) goto L_0x0029
            r8.mFirstUpdate = r6     // Catch:{ all -> 0x0035 }
        L_0x0029:
            boolean r7 = r1.equals(r5)     // Catch:{ all -> 0x0035 }
            if (r7 == 0) goto L_0x0031
            r8.mNewUpdate = r6     // Catch:{ all -> 0x0035 }
        L_0x0031:
            $closeResource(r2, r3)     // Catch:{ IOException | NumberFormatException -> 0x003c }
            goto L_0x003e
        L_0x0035:
            r4 = move-exception
            throw r4     // Catch:{ all -> 0x0037 }
        L_0x0037:
            r5 = move-exception
            $closeResource(r4, r3)     // Catch:{ IOException | NumberFormatException -> 0x003c }
            throw r5     // Catch:{ IOException | NumberFormatException -> 0x003c }
        L_0x003c:
            r3 = move-exception
            r4 = 0
        L_0x003e:
            int r3 = r8.mCurrentVersion
            java.lang.String r5 = "UsageStatsDatabase"
            if (r4 == r3) goto L_0x0093
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r6 = "Upgrading from version "
            r3.append(r6)
            r3.append(r4)
            java.lang.String r6 = " to "
            r3.append(r6)
            int r7 = r8.mCurrentVersion
            r3.append(r7)
            java.lang.String r3 = r3.toString()
            android.util.Slog.i(r5, r3)
            java.io.File r3 = r8.mUpdateBreadcrumb
            boolean r3 = r3.exists()
            if (r3 != 0) goto L_0x008e
            r8.doUpgradeLocked(r4)     // Catch:{ Exception -> 0x006e }
            goto L_0x0093
        L_0x006e:
            r0 = move-exception
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Failed to upgrade from version "
            r2.append(r3)
            r2.append(r4)
            r2.append(r6)
            int r3 = r8.mCurrentVersion
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            android.util.Slog.e(r5, r2, r0)
            r8.mCurrentVersion = r4
            return
        L_0x008e:
            java.lang.String r3 = "Version upgrade breadcrumb found on disk! Continuing version upgrade"
            android.util.Slog.i(r5, r3)
        L_0x0093:
            int r3 = r8.mCurrentVersion
            if (r4 != r3) goto L_0x009b
            boolean r3 = r8.mNewUpdate
            if (r3 == 0) goto L_0x00c0
        L_0x009b:
            java.io.BufferedWriter r3 = new java.io.BufferedWriter     // Catch:{ IOException -> 0x0125 }
            java.io.FileWriter r6 = new java.io.FileWriter     // Catch:{ IOException -> 0x0125 }
            java.io.File r7 = r8.mVersionFile     // Catch:{ IOException -> 0x0125 }
            r6.<init>(r7)     // Catch:{ IOException -> 0x0125 }
            r3.<init>(r6)     // Catch:{ IOException -> 0x0125 }
            int r6 = r8.mCurrentVersion     // Catch:{ all -> 0x011e }
            java.lang.String r6 = java.lang.Integer.toString(r6)     // Catch:{ all -> 0x011e }
            r3.write(r6)     // Catch:{ all -> 0x011e }
            r3.write(r0)     // Catch:{ all -> 0x011e }
            r3.write(r1)     // Catch:{ all -> 0x011e }
            r3.write(r0)     // Catch:{ all -> 0x011e }
            r3.flush()     // Catch:{ all -> 0x011e }
            $closeResource(r2, r3)     // Catch:{ IOException -> 0x0125 }
        L_0x00c0:
            java.io.File r0 = r8.mUpdateBreadcrumb
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x0103
            int r0 = r8.mCurrentVersion
            if (r4 == r0) goto L_0x0103
            java.io.BufferedReader r0 = new java.io.BufferedReader     // Catch:{ IOException | NumberFormatException -> 0x00f7 }
            java.io.FileReader r3 = new java.io.FileReader     // Catch:{ IOException | NumberFormatException -> 0x00f7 }
            java.io.File r6 = r8.mUpdateBreadcrumb     // Catch:{ IOException | NumberFormatException -> 0x00f7 }
            r3.<init>(r6)     // Catch:{ IOException | NumberFormatException -> 0x00f7 }
            r0.<init>(r3)     // Catch:{ IOException | NumberFormatException -> 0x00f7 }
            java.lang.String r3 = r0.readLine()     // Catch:{ all -> 0x00f0 }
            long r6 = java.lang.Long.parseLong(r3)     // Catch:{ all -> 0x00f0 }
            java.lang.String r3 = r0.readLine()     // Catch:{ all -> 0x00f0 }
            int r3 = java.lang.Integer.parseInt(r3)     // Catch:{ all -> 0x00f0 }
            $closeResource(r2, r0)     // Catch:{ IOException | NumberFormatException -> 0x00f7 }
            r8.continueUpgradeLocked(r3, r6)
            goto L_0x0103
        L_0x00f0:
            r2 = move-exception
            throw r2     // Catch:{ all -> 0x00f2 }
        L_0x00f2:
            r3 = move-exception
            $closeResource(r2, r0)     // Catch:{ IOException | NumberFormatException -> 0x00f7 }
            throw r3     // Catch:{ IOException | NumberFormatException -> 0x00f7 }
        L_0x00f7:
            r0 = move-exception
            java.lang.String r2 = "Failed read version upgrade breadcrumb"
            android.util.Slog.e(r5, r2)
            java.lang.RuntimeException r2 = new java.lang.RuntimeException
            r2.<init>(r0)
            throw r2
        L_0x0103:
            java.io.File r0 = r8.mUpdateBreadcrumb
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x0110
            java.io.File r0 = r8.mUpdateBreadcrumb
            r0.delete()
        L_0x0110:
            java.io.File r0 = r8.mBackupsDir
            boolean r0 = r0.exists()
            if (r0 == 0) goto L_0x011d
            java.io.File r0 = r8.mBackupsDir
            deleteDirectory(r0)
        L_0x011d:
            return
        L_0x011e:
            r0 = move-exception
            throw r0     // Catch:{ all -> 0x0120 }
        L_0x0120:
            r2 = move-exception
            $closeResource(r0, r3)     // Catch:{ IOException -> 0x0125 }
            throw r2     // Catch:{ IOException -> 0x0125 }
        L_0x0125:
            r0 = move-exception
            java.lang.String r2 = "Failed to write new version"
            android.util.Slog.e(r5, r2)
            java.lang.RuntimeException r2 = new java.lang.RuntimeException
            r2.<init>(r0)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usage.UsageStatsDatabase.checkVersionAndBuildLocked():void");
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 != null) {
            try {
                x1.close();
            } catch (Throwable th) {
                x0.addSuppressed(th);
            }
        } else {
            x1.close();
        }
    }

    private String getBuildFingerprint() {
        return Build.VERSION.RELEASE + ";" + Build.VERSION.CODENAME + ";" + Build.VERSION.INCREMENTAL;
    }

    /* Debug info: failed to restart local var, previous not found, register: 18 */
    private void doUpgradeLocked(int thisVersion) {
        boolean z = false;
        if (thisVersion < 2) {
            Slog.i(TAG, "Deleting all usage stats files");
            int i = 0;
            while (true) {
                File[] fileArr = this.mIntervalDirs;
                if (i < fileArr.length) {
                    File[] files = fileArr[i].listFiles();
                    if (files != null) {
                        for (File f : files) {
                            f.delete();
                        }
                    }
                    i++;
                } else {
                    return;
                }
            }
        } else {
            long token = System.currentTimeMillis();
            File backupDir = new File(this.mBackupsDir, Long.toString(token));
            backupDir.mkdirs();
            if (backupDir.exists()) {
                try {
                    Files.copy(this.mVersionFile.toPath(), new File(backupDir, this.mVersionFile.getName()).toPath(), new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
                    int i2 = 0;
                    while (true) {
                        File[] fileArr2 = this.mIntervalDirs;
                        if (i2 < fileArr2.length) {
                            File backupIntervalDir = new File(backupDir, fileArr2[i2].getName());
                            backupIntervalDir.mkdir();
                            if (backupIntervalDir.exists()) {
                                File[] files2 = this.mIntervalDirs[i2].listFiles();
                                if (files2 != null) {
                                    int j = 0;
                                    while (j < files2.length) {
                                        try {
                                            Files.move(files2[j].toPath(), new File(backupIntervalDir, files2[j].getName()).toPath(), new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
                                            j++;
                                            z = false;
                                        } catch (IOException e) {
                                            Slog.e(TAG, "Failed to back up file : " + files2[j].toString());
                                            throw new RuntimeException(e);
                                        }
                                    }
                                }
                                i2++;
                                z = z;
                            } else {
                                throw new IllegalStateException("Failed to create interval backup directory " + backupIntervalDir.getAbsolutePath());
                            }
                        } else {
                            BufferedWriter writer = null;
                            try {
                                writer = new BufferedWriter(new FileWriter(this.mUpdateBreadcrumb));
                                writer.write(Long.toString(token));
                                writer.write("\n");
                                writer.write(Integer.toString(thisVersion));
                                writer.write("\n");
                                writer.flush();
                                IoUtils.closeQuietly(writer);
                                return;
                            } catch (IOException e2) {
                                Slog.e(TAG, "Failed to write new version upgrade breadcrumb");
                                throw new RuntimeException(e2);
                            } catch (Throwable th) {
                                IoUtils.closeQuietly(writer);
                                throw th;
                            }
                        }
                    }
                } catch (IOException e3) {
                    Slog.e(TAG, "Failed to back up version file : " + this.mVersionFile.toString());
                    throw new RuntimeException(e3);
                }
            } else {
                throw new IllegalStateException("Failed to create backup directory " + backupDir.getAbsolutePath());
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:39:0x00dc  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x00e2  */
    /* JADX WARNING: Removed duplicated region for block: B:45:0x00c1 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00c7 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void continueUpgradeLocked(int r19, long r20) {
        /*
            r18 = this;
            r1 = r18
            java.lang.String r2 = "ms ,will remove other backup files"
            java.lang.String r3 = "upgrade backup file timeout :30000ms, use time : "
            java.lang.String r4 = "UsageStatsDatabase"
            java.io.File r0 = new java.io.File
            java.io.File r5 = r1.mBackupsDir
            java.lang.String r6 = java.lang.Long.toString(r20)
            r0.<init>(r5, r6)
            r5 = r0
            long r6 = android.os.SystemClock.elapsedRealtime()
            r0 = 0
            r8 = r0
        L_0x001c:
            java.io.File[] r0 = r1.mIntervalDirs
            int r9 = r0.length
            if (r8 >= r9) goto L_0x00f4
            java.io.File r9 = new java.io.File
            r0 = r0[r8]
            java.lang.String r0 = r0.getName()
            r9.<init>(r5, r0)
            java.io.File[] r10 = r9.listFiles()
            if (r10 == 0) goto L_0x00ea
            r0 = 0
            r11 = r0
        L_0x0034:
            int r0 = r10.length
            if (r11 >= r0) goto L_0x00e3
            com.android.server.usage.IntervalStats r0 = new com.android.server.usage.IntervalStats     // Catch:{ Exception -> 0x0094, all -> 0x008c }
            r0.<init>()     // Catch:{ Exception -> 0x0094, all -> 0x008c }
            android.util.AtomicFile r14 = new android.util.AtomicFile     // Catch:{ Exception -> 0x0094, all -> 0x008c }
            r15 = r10[r11]     // Catch:{ Exception -> 0x0094, all -> 0x008c }
            r14.<init>(r15)     // Catch:{ Exception -> 0x0094, all -> 0x008c }
            r15 = r19
            readLocked((android.util.AtomicFile) r14, (com.android.server.usage.IntervalStats) r0, (int) r15)     // Catch:{ Exception -> 0x008a, all -> 0x0088 }
            android.util.AtomicFile r14 = new android.util.AtomicFile     // Catch:{ Exception -> 0x008a, all -> 0x0088 }
            java.io.File r12 = new java.io.File     // Catch:{ Exception -> 0x008a, all -> 0x0088 }
            java.io.File[] r13 = r1.mIntervalDirs     // Catch:{ Exception -> 0x008a, all -> 0x0088 }
            r13 = r13[r8]     // Catch:{ Exception -> 0x008a, all -> 0x0088 }
            r16 = r8
            r17 = r9
            long r8 = r0.beginTime     // Catch:{ Exception -> 0x0086 }
            java.lang.String r8 = java.lang.Long.toString(r8)     // Catch:{ Exception -> 0x0086 }
            r12.<init>(r13, r8)     // Catch:{ Exception -> 0x0086 }
            r14.<init>(r12)     // Catch:{ Exception -> 0x0086 }
            int r8 = r1.mCurrentVersion     // Catch:{ Exception -> 0x0086 }
            writeLocked((android.util.AtomicFile) r14, (com.android.server.usage.IntervalStats) r0, (int) r8)     // Catch:{ Exception -> 0x0086 }
            long r8 = android.os.SystemClock.elapsedRealtime()
            long r8 = r8 - r6
            r12 = 30000(0x7530, double:1.4822E-319)
            int r0 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
            if (r0 <= 0) goto L_0x00c7
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
        L_0x0075:
            r0.append(r3)
            r0.append(r8)
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            android.util.Slog.w(r4, r0)
            return
        L_0x0086:
            r0 = move-exception
            goto L_0x009b
        L_0x0088:
            r0 = move-exception
            goto L_0x008f
        L_0x008a:
            r0 = move-exception
            goto L_0x0097
        L_0x008c:
            r0 = move-exception
            r15 = r19
        L_0x008f:
            r16 = r8
            r17 = r9
            goto L_0x00d1
        L_0x0094:
            r0 = move-exception
            r15 = r19
        L_0x0097:
            r16 = r8
            r17 = r9
        L_0x009b:
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ all -> 0x00d0 }
            r8.<init>()     // Catch:{ all -> 0x00d0 }
            java.lang.String r9 = "Failed to upgrade backup file : "
            r8.append(r9)     // Catch:{ all -> 0x00d0 }
            r9 = r10[r11]     // Catch:{ all -> 0x00d0 }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x00d0 }
            r8.append(r9)     // Catch:{ all -> 0x00d0 }
            java.lang.String r8 = r8.toString()     // Catch:{ all -> 0x00d0 }
            android.util.Slog.e(r4, r8)     // Catch:{ all -> 0x00d0 }
            long r8 = android.os.SystemClock.elapsedRealtime()
            long r8 = r8 - r6
            r12 = 30000(0x7530, double:1.4822E-319)
            int r0 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
            if (r0 <= 0) goto L_0x00c7
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            goto L_0x0075
        L_0x00c7:
            int r11 = r11 + 1
            r8 = r16
            r9 = r17
            goto L_0x0034
        L_0x00d0:
            r0 = move-exception
        L_0x00d1:
            long r8 = android.os.SystemClock.elapsedRealtime()
            long r8 = r8 - r6
            r12 = 30000(0x7530, double:1.4822E-319)
            int r12 = (r8 > r12 ? 1 : (r8 == r12 ? 0 : -1))
            if (r12 <= 0) goto L_0x00e2
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            goto L_0x0075
        L_0x00e2:
            throw r0
        L_0x00e3:
            r15 = r19
            r16 = r8
            r17 = r9
            goto L_0x00f0
        L_0x00ea:
            r15 = r19
            r16 = r8
            r17 = r9
        L_0x00f0:
            int r8 = r16 + 1
            goto L_0x001c
        L_0x00f4:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usage.UsageStatsDatabase.continueUpgradeLocked(int, long):void");
    }

    public void onTimeChanged(long timeDiffMillis) {
        long j = timeDiffMillis;
        synchronized (this.mLock) {
            StringBuilder logBuilder = new StringBuilder();
            logBuilder.append("Time changed by ");
            TimeUtils.formatDuration(j, logBuilder);
            logBuilder.append(".");
            int filesDeleted = 0;
            int filesDeleted2 = 0;
            TimeSparseArray<AtomicFile>[] timeSparseArrayArr = this.mSortedStatFiles;
            int length = timeSparseArrayArr.length;
            int i = 0;
            while (i < length) {
                TimeSparseArray<AtomicFile> files = timeSparseArrayArr[i];
                int fileCount = files.size();
                int i2 = 0;
                int filesMoved = filesDeleted2;
                int filesDeleted3 = filesDeleted;
                while (i2 < fileCount) {
                    AtomicFile file = (AtomicFile) files.valueAt(i2);
                    long newTime = files.keyAt(i2) + j;
                    if (newTime < 0) {
                        filesDeleted3++;
                        file.delete();
                    } else {
                        try {
                            file.openRead().close();
                        } catch (IOException e) {
                        }
                        String newName = Long.toString(newTime);
                        if (file.getBaseFile().getName().endsWith(CHECKED_IN_SUFFIX)) {
                            newName = newName + CHECKED_IN_SUFFIX;
                        }
                        filesMoved++;
                        file.getBaseFile().renameTo(new File(file.getBaseFile().getParentFile(), newName));
                    }
                    i2++;
                    j = timeDiffMillis;
                }
                files.clear();
                i++;
                j = timeDiffMillis;
                filesDeleted = filesDeleted3;
                filesDeleted2 = filesMoved;
            }
            logBuilder.append(" files deleted: ");
            logBuilder.append(filesDeleted);
            logBuilder.append(" files moved: ");
            logBuilder.append(filesDeleted2);
            Slog.i(TAG, logBuilder.toString());
            indexFilesLocked();
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 6 */
    public IntervalStats getLatestUsageStats(int intervalType) {
        synchronized (this.mLock) {
            if (intervalType >= 0) {
                if (intervalType < this.mIntervalDirs.length) {
                    int fileCount = this.mSortedStatFiles[intervalType].size();
                    if (fileCount == 0) {
                        return null;
                    }
                    try {
                        IntervalStats stats = new IntervalStats();
                        readLocked((AtomicFile) this.mSortedStatFiles[intervalType].valueAt(fileCount - 1), stats);
                        return stats;
                    } catch (IOException e) {
                        Slog.e(TAG, "Failed to read usage stats file", e);
                        return null;
                    }
                }
            }
            throw new IllegalArgumentException("Bad interval type " + intervalType);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 16 */
    public <T> List<T> queryUsageStats(int intervalType, long beginTime, long endTime, StatCombiner<T> combiner) {
        int startIndex;
        int endIndex;
        UsageStatsDatabase usageStatsDatabase = this;
        int i = intervalType;
        long j = beginTime;
        long j2 = endTime;
        synchronized (usageStatsDatabase.mLock) {
            if (i >= 0) {
                try {
                    if (i < usageStatsDatabase.mIntervalDirs.length) {
                        TimeSparseArray<AtomicFile> intervalStats = usageStatsDatabase.mSortedStatFiles[i];
                        if (j2 <= j) {
                            return null;
                        }
                        int startIndex2 = intervalStats.closestIndexOnOrBefore(j);
                        if (startIndex2 < 0) {
                            startIndex = 0;
                        } else {
                            startIndex = startIndex2;
                        }
                        int endIndex2 = intervalStats.closestIndexOnOrBefore(j2);
                        if (endIndex2 < 0) {
                            return null;
                        }
                        if (intervalStats.keyAt(endIndex2) == j2) {
                            int endIndex3 = endIndex2 - 1;
                            if (endIndex3 < 0) {
                                return null;
                            }
                            endIndex = endIndex3;
                        } else {
                            endIndex = endIndex2;
                        }
                        IntervalStats stats = new IntervalStats();
                        ArrayList<T> results = new ArrayList<>();
                        int i2 = startIndex;
                        while (i2 <= endIndex) {
                            try {
                                usageStatsDatabase.readLocked((AtomicFile) intervalStats.valueAt(i2), stats);
                                if (j < stats.endTime) {
                                    try {
                                        combiner.combine(stats, false, results);
                                    } catch (IOException e) {
                                        e = e;
                                    }
                                } else {
                                    StatCombiner<T> statCombiner = combiner;
                                }
                            } catch (IOException e2) {
                                e = e2;
                                StatCombiner<T> statCombiner2 = combiner;
                                Slog.e(TAG, "Failed to read usage stats file", e);
                                i2++;
                                usageStatsDatabase = this;
                            } catch (Throwable th) {
                                th = th;
                                StatCombiner<T> statCombiner3 = combiner;
                                throw th;
                            }
                            i2++;
                            usageStatsDatabase = this;
                        }
                        return results;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    throw th;
                }
            }
            throw new IllegalArgumentException("Bad interval type " + i);
        }
    }

    public int findBestFitBucket(long beginTimeStamp, long endTimeStamp) {
        int bestBucket;
        synchronized (this.mLock) {
            bestBucket = -1;
            long smallestDiff = JobStatus.NO_LATEST_RUNTIME;
            for (int i = this.mSortedStatFiles.length - 1; i >= 0; i--) {
                int index = this.mSortedStatFiles[i].closestIndexOnOrBefore(beginTimeStamp);
                int size = this.mSortedStatFiles[i].size();
                if (index >= 0 && index < size) {
                    long diff = Math.abs(this.mSortedStatFiles[i].keyAt(index) - beginTimeStamp);
                    if (diff < smallestDiff) {
                        smallestDiff = diff;
                        bestBucket = i;
                    }
                }
            }
        }
        return bestBucket;
    }

    public void prune(long currentTimeMillis) {
        synchronized (this.mLock) {
            this.mCal.setTimeInMillis(currentTimeMillis);
            this.mCal.addYears(-3);
            pruneFilesOlderThan(this.mIntervalDirs[3], this.mCal.getTimeInMillis());
            this.mCal.setTimeInMillis(currentTimeMillis);
            this.mCal.addMonths(-6);
            pruneFilesOlderThan(this.mIntervalDirs[2], this.mCal.getTimeInMillis());
            this.mCal.setTimeInMillis(currentTimeMillis);
            this.mCal.addWeeks(-4);
            pruneFilesOlderThan(this.mIntervalDirs[1], this.mCal.getTimeInMillis());
            this.mCal.setTimeInMillis(currentTimeMillis);
            this.mCal.addDays(-10);
            pruneFilesOlderThan(this.mIntervalDirs[0], this.mCal.getTimeInMillis());
            this.mCal.setTimeInMillis(currentTimeMillis);
            this.mCal.addDays(-SELECTION_LOG_RETENTION_LEN);
            for (File pruneChooserCountsOlderThan : this.mIntervalDirs) {
                pruneChooserCountsOlderThan(pruneChooserCountsOlderThan, this.mCal.getTimeInMillis());
            }
            indexFilesLocked();
        }
    }

    private static void pruneFilesOlderThan(File dir, long expiryTime) {
        long beginTime;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                try {
                    beginTime = parseBeginTime(f);
                } catch (IOException e) {
                    beginTime = 0;
                }
                if (beginTime < expiryTime) {
                    new AtomicFile(f).delete();
                }
            }
        }
    }

    private void pruneChooserCountsOlderThan(File dir, long expiryTime) {
        long beginTime;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                try {
                    beginTime = parseBeginTime(f);
                } catch (IOException e) {
                    beginTime = 0;
                }
                if (beginTime < expiryTime) {
                    try {
                        AtomicFile af = new AtomicFile(f);
                        IntervalStats stats = new IntervalStats();
                        readLocked(af, stats);
                        int pkgCount = stats.packageStats.size();
                        for (int i = 0; i < pkgCount; i++) {
                            UsageStats pkgStats = stats.packageStats.valueAt(i);
                            if (pkgStats.mChooserCounts != null) {
                                pkgStats.mChooserCounts.clear();
                            }
                        }
                        writeLocked(af, stats);
                    } catch (Exception e2) {
                        Slog.e(TAG, "Failed to delete chooser counts from usage stats file", e2);
                    }
                }
            }
        }
    }

    private static long parseBeginTime(AtomicFile file) throws IOException {
        return parseBeginTime(file.getBaseFile());
    }

    private static long parseBeginTime(File file) throws IOException {
        String name = file.getName();
        int i = 0;
        while (true) {
            if (i < name.length()) {
                char c = name.charAt(i);
                if (c < '0' || c > '9') {
                    name = name.substring(0, i);
                } else {
                    i++;
                }
            }
            return Long.parseLong(name);
        }
        name = name.substring(0, i);
        try {
            return Long.parseLong(name);
        } catch (NumberFormatException e) {
            throw new IOException(e);
        }
    }

    private void writeLocked(AtomicFile file, IntervalStats stats) throws IOException {
        writeLocked(file, stats, this.mCurrentVersion);
    }

    private static void writeLocked(AtomicFile file, IntervalStats stats, int version) throws IOException {
        FileOutputStream fos = file.startWrite();
        try {
            writeLocked((OutputStream) fos, stats, version);
            file.finishWrite(fos);
            fos = null;
        } finally {
            file.failWrite(fos);
        }
    }

    private void writeLocked(OutputStream out, IntervalStats stats) throws IOException {
        writeLocked(out, stats, this.mCurrentVersion);
    }

    private static void writeLocked(OutputStream out, IntervalStats stats, int version) throws IOException {
        if (version == 1 || version == 2 || version == 3) {
            UsageStatsXml.write(out, stats);
        } else if (version == 4) {
            UsageStatsProto.write(out, stats);
        } else {
            throw new RuntimeException("Unhandled UsageStatsDatabase version: " + Integer.toString(version) + " on write.");
        }
    }

    private void readLocked(AtomicFile file, IntervalStats statsOut) throws IOException {
        readLocked(file, statsOut, this.mCurrentVersion);
    }

    private static void readLocked(AtomicFile file, IntervalStats statsOut, int version) throws IOException {
        FileInputStream in;
        try {
            in = file.openRead();
            statsOut.beginTime = parseBeginTime(file);
            readLocked((InputStream) in, statsOut, version);
            statsOut.lastTimeSaved = file.getLastModifiedTime();
            try {
                in.close();
            } catch (IOException e) {
            }
        } catch (FileNotFoundException e2) {
            Slog.e(TAG, TAG, e2);
            throw e2;
        } catch (Throwable th) {
            try {
                in.close();
            } catch (IOException e3) {
            }
            throw th;
        }
    }

    private void readLocked(InputStream in, IntervalStats statsOut) throws IOException {
        readLocked(in, statsOut, this.mCurrentVersion);
    }

    private static void readLocked(InputStream in, IntervalStats statsOut, int version) throws IOException {
        if (version == 1 || version == 2 || version == 3) {
            UsageStatsXml.read(in, statsOut);
        } else if (version == 4) {
            UsageStatsProto.read(in, statsOut);
        } else {
            throw new RuntimeException("Unhandled UsageStatsDatabase version: " + Integer.toString(version) + " on read.");
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 7 */
    public void putUsageStats(int intervalType, IntervalStats stats) throws IOException {
        if (stats != null) {
            synchronized (this.mLock) {
                if (intervalType >= 0) {
                    if (intervalType < this.mIntervalDirs.length) {
                        AtomicFile f = (AtomicFile) this.mSortedStatFiles[intervalType].get(stats.beginTime);
                        if (f == null) {
                            f = new AtomicFile(new File(this.mIntervalDirs[intervalType], Long.toString(stats.beginTime)));
                            this.mSortedStatFiles[intervalType].put(stats.beginTime, f);
                        }
                        writeLocked(f, stats);
                        stats.lastTimeSaved = f.getLastModifiedTime();
                    }
                }
                throw new IllegalArgumentException("Bad interval type " + intervalType);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public byte[] getBackupPayload(String key) {
        return getBackupPayload(key, 4);
    }

    @VisibleForTesting
    public byte[] getBackupPayload(String key, int version) {
        byte[] byteArray;
        synchronized (this.mLock) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            if (KEY_USAGE_STATS.equals(key)) {
                prune(System.currentTimeMillis());
                DataOutputStream out = new DataOutputStream(baos);
                try {
                    out.writeInt(version);
                    out.writeInt(this.mSortedStatFiles[0].size());
                    for (int i = 0; i < this.mSortedStatFiles[0].size(); i++) {
                        writeIntervalStatsToStream(out, (AtomicFile) this.mSortedStatFiles[0].valueAt(i), version);
                    }
                    out.writeInt(this.mSortedStatFiles[1].size());
                    for (int i2 = 0; i2 < this.mSortedStatFiles[1].size(); i2++) {
                        writeIntervalStatsToStream(out, (AtomicFile) this.mSortedStatFiles[1].valueAt(i2), version);
                    }
                    out.writeInt(this.mSortedStatFiles[2].size());
                    for (int i3 = 0; i3 < this.mSortedStatFiles[2].size(); i3++) {
                        writeIntervalStatsToStream(out, (AtomicFile) this.mSortedStatFiles[2].valueAt(i3), version);
                    }
                    out.writeInt(this.mSortedStatFiles[3].size());
                    for (int i4 = 0; i4 < this.mSortedStatFiles[3].size(); i4++) {
                        writeIntervalStatsToStream(out, (AtomicFile) this.mSortedStatFiles[3].valueAt(i4), version);
                    }
                } catch (IOException ioe) {
                    Slog.d(TAG, "Failed to write data to output stream", ioe);
                    baos.reset();
                }
            }
            byteArray = baos.toByteArray();
        }
        return byteArray;
    }

    /* Debug info: failed to restart local var, previous not found, register: 17 */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00da, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x00ec, code lost:
        r0 = th;
     */
    /* JADX WARNING: Exception block dominator not found, dom blocks: [B:33:0x00bc, B:44:0x00d2] */
    @com.android.internal.annotations.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void applyRestoredPayload(java.lang.String r18, byte[] r19) {
        /*
            r17 = this;
            r1 = r17
            java.lang.Object r2 = r1.mLock
            monitor-enter(r2)
            java.lang.String r0 = "usage_stats"
            r3 = r18
            boolean r0 = r0.equals(r3)     // Catch:{ all -> 0x00e3 }
            if (r0 == 0) goto L_0x00df
            r0 = 0
            com.android.server.usage.IntervalStats r4 = r1.getLatestUsageStats(r0)     // Catch:{ all -> 0x00e3 }
            r5 = 1
            com.android.server.usage.IntervalStats r6 = r1.getLatestUsageStats(r5)     // Catch:{ all -> 0x00e3 }
            r7 = 2
            com.android.server.usage.IntervalStats r8 = r1.getLatestUsageStats(r7)     // Catch:{ all -> 0x00e3 }
            r9 = 3
            com.android.server.usage.IntervalStats r10 = r1.getLatestUsageStats(r9)     // Catch:{ all -> 0x00e3 }
            java.io.DataInputStream r11 = new java.io.DataInputStream     // Catch:{ IOException -> 0x00cb, all -> 0x00c7 }
            java.io.ByteArrayInputStream r12 = new java.io.ByteArrayInputStream     // Catch:{ IOException -> 0x00cb, all -> 0x00c7 }
            r13 = r19
            r12.<init>(r13)     // Catch:{ IOException -> 0x00c5 }
            r11.<init>(r12)     // Catch:{ IOException -> 0x00c5 }
            int r12 = r11.readInt()     // Catch:{ IOException -> 0x00c5 }
            if (r12 < r5) goto L_0x00c0
            r14 = 4
            if (r12 <= r14) goto L_0x003f
            goto L_0x00c0
        L_0x003f:
            r14 = r0
        L_0x0040:
            java.io.File[] r15 = r1.mIntervalDirs     // Catch:{ IOException -> 0x00c5 }
            int r15 = r15.length     // Catch:{ IOException -> 0x00c5 }
            if (r14 >= r15) goto L_0x004f
            java.io.File[] r15 = r1.mIntervalDirs     // Catch:{ IOException -> 0x00c5 }
            r15 = r15[r14]     // Catch:{ IOException -> 0x00c5 }
            deleteDirectoryContents(r15)     // Catch:{ IOException -> 0x00c5 }
            int r14 = r14 + 1
            goto L_0x0040
        L_0x004f:
            int r14 = r11.readInt()     // Catch:{ IOException -> 0x00c5 }
            r15 = 0
        L_0x0054:
            if (r15 >= r14) goto L_0x006b
            byte[] r9 = getIntervalStatsBytes(r11)     // Catch:{ IOException -> 0x00c5 }
            com.android.server.usage.IntervalStats r9 = r1.deserializeIntervalStats(r9, r12)     // Catch:{ IOException -> 0x00c5 }
            com.android.server.usage.IntervalStats r16 = r1.mergeStats(r9, r4)     // Catch:{ IOException -> 0x00c5 }
            r9 = r16
            r1.putUsageStats(r0, r9)     // Catch:{ IOException -> 0x00c5 }
            int r15 = r15 + 1
            r9 = 3
            goto L_0x0054
        L_0x006b:
            int r0 = r11.readInt()     // Catch:{ IOException -> 0x00c5 }
            r9 = 0
        L_0x0070:
            if (r9 >= r0) goto L_0x0085
            byte[] r14 = getIntervalStatsBytes(r11)     // Catch:{ IOException -> 0x00c5 }
            com.android.server.usage.IntervalStats r14 = r1.deserializeIntervalStats(r14, r12)     // Catch:{ IOException -> 0x00c5 }
            com.android.server.usage.IntervalStats r15 = r1.mergeStats(r14, r6)     // Catch:{ IOException -> 0x00c5 }
            r14 = r15
            r1.putUsageStats(r5, r14)     // Catch:{ IOException -> 0x00c5 }
            int r9 = r9 + 1
            goto L_0x0070
        L_0x0085:
            int r5 = r11.readInt()     // Catch:{ IOException -> 0x00c5 }
            r0 = r5
            r5 = 0
        L_0x008b:
            if (r5 >= r0) goto L_0x00a0
            byte[] r9 = getIntervalStatsBytes(r11)     // Catch:{ IOException -> 0x00c5 }
            com.android.server.usage.IntervalStats r9 = r1.deserializeIntervalStats(r9, r12)     // Catch:{ IOException -> 0x00c5 }
            com.android.server.usage.IntervalStats r14 = r1.mergeStats(r9, r8)     // Catch:{ IOException -> 0x00c5 }
            r9 = r14
            r1.putUsageStats(r7, r9)     // Catch:{ IOException -> 0x00c5 }
            int r5 = r5 + 1
            goto L_0x008b
        L_0x00a0:
            int r5 = r11.readInt()     // Catch:{ IOException -> 0x00c5 }
            r0 = r5
            r5 = 0
        L_0x00a6:
            if (r5 >= r0) goto L_0x00bc
            byte[] r7 = getIntervalStatsBytes(r11)     // Catch:{ IOException -> 0x00c5 }
            com.android.server.usage.IntervalStats r7 = r1.deserializeIntervalStats(r7, r12)     // Catch:{ IOException -> 0x00c5 }
            com.android.server.usage.IntervalStats r9 = r1.mergeStats(r7, r10)     // Catch:{ IOException -> 0x00c5 }
            r7 = r9
            r9 = 3
            r1.putUsageStats(r9, r7)     // Catch:{ IOException -> 0x00c5 }
            int r5 = r5 + 1
            goto L_0x00a6
        L_0x00bc:
            r17.indexFilesLocked()     // Catch:{ all -> 0x00ec }
            goto L_0x00d9
        L_0x00c0:
            r17.indexFilesLocked()     // Catch:{ all -> 0x00ec }
            monitor-exit(r2)     // Catch:{ all -> 0x00ec }
            return
        L_0x00c5:
            r0 = move-exception
            goto L_0x00ce
        L_0x00c7:
            r0 = move-exception
            r13 = r19
            goto L_0x00db
        L_0x00cb:
            r0 = move-exception
            r13 = r19
        L_0x00ce:
            java.lang.String r5 = "UsageStatsDatabase"
            java.lang.String r7 = "Failed to read data from input stream"
            android.util.Slog.d(r5, r7, r0)     // Catch:{ all -> 0x00da }
            r17.indexFilesLocked()     // Catch:{ all -> 0x00ec }
        L_0x00d9:
            goto L_0x00e1
        L_0x00da:
            r0 = move-exception
        L_0x00db:
            r17.indexFilesLocked()     // Catch:{ all -> 0x00ec }
            throw r0     // Catch:{ all -> 0x00ec }
        L_0x00df:
            r13 = r19
        L_0x00e1:
            monitor-exit(r2)     // Catch:{ all -> 0x00ec }
            return
        L_0x00e3:
            r0 = move-exception
            goto L_0x00e8
        L_0x00e5:
            r0 = move-exception
            r3 = r18
        L_0x00e8:
            r13 = r19
        L_0x00ea:
            monitor-exit(r2)     // Catch:{ all -> 0x00ec }
            throw r0
        L_0x00ec:
            r0 = move-exception
            goto L_0x00ea
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.usage.UsageStatsDatabase.applyRestoredPayload(java.lang.String, byte[]):void");
    }

    private IntervalStats mergeStats(IntervalStats beingRestored, IntervalStats onDevice) {
        if (onDevice == null) {
            return beingRestored;
        }
        if (beingRestored == null) {
            return null;
        }
        beingRestored.activeConfiguration = onDevice.activeConfiguration;
        beingRestored.configurations.putAll(onDevice.configurations);
        beingRestored.events.clear();
        beingRestored.events.merge(onDevice.events);
        return beingRestored;
    }

    private void writeIntervalStatsToStream(DataOutputStream out, AtomicFile statsFile, int version) throws IOException {
        IntervalStats stats = new IntervalStats();
        try {
            readLocked(statsFile, stats);
            sanitizeIntervalStatsForBackup(stats);
            byte[] data = serializeIntervalStats(stats, version);
            out.writeInt(data.length);
            out.write(data);
        } catch (IOException e) {
            Slog.e(TAG, "Failed to read usage stats file", e);
            out.writeInt(0);
        }
    }

    private static byte[] getIntervalStatsBytes(DataInputStream in) throws IOException {
        int length = in.readInt();
        byte[] buffer = new byte[length];
        in.read(buffer, 0, length);
        return buffer;
    }

    private static void sanitizeIntervalStatsForBackup(IntervalStats stats) {
        if (stats != null) {
            stats.activeConfiguration = null;
            stats.configurations.clear();
            stats.events.clear();
        }
    }

    private byte[] serializeIntervalStats(IntervalStats stats, int version) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(baos);
        try {
            out.writeLong(stats.beginTime);
            writeLocked((OutputStream) out, stats, version);
        } catch (Exception ioe) {
            Slog.d(TAG, "Serializing IntervalStats Failed", ioe);
            baos.reset();
        }
        return baos.toByteArray();
    }

    private IntervalStats deserializeIntervalStats(byte[] data, int version) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(data));
        IntervalStats stats = new IntervalStats();
        try {
            stats.beginTime = in.readLong();
            readLocked((InputStream) in, stats, version);
            return stats;
        } catch (IOException ioe) {
            Slog.d(TAG, "DeSerializing IntervalStats Failed", ioe);
            return null;
        }
    }

    private static void deleteDirectoryContents(File directory) {
        for (File file : directory.listFiles()) {
            deleteDirectory(file);
        }
    }

    private static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (!file.isDirectory()) {
                    file.delete();
                } else {
                    deleteDirectory(file);
                }
            }
        }
        directory.delete();
    }

    public void dump(IndentingPrintWriter pw, boolean compact) {
        synchronized (this.mLock) {
            pw.println("UsageStatsDatabase:");
            pw.increaseIndent();
            for (int i = 0; i < this.mSortedStatFiles.length; i++) {
                TimeSparseArray<AtomicFile> files = this.mSortedStatFiles[i];
                int size = files.size();
                pw.print(UserUsageStatsService.intervalToString(i));
                pw.print(" stats files: ");
                pw.print(size);
                pw.println(", sorted list of files:");
                pw.increaseIndent();
                for (int f = 0; f < size; f++) {
                    long fileName = files.keyAt(f);
                    if (compact) {
                        pw.print(UserUsageStatsService.formatDateTime(fileName, false));
                    } else {
                        pw.printPair(Long.toString(fileName), UserUsageStatsService.formatDateTime(fileName, true));
                    }
                    pw.println();
                }
                pw.decreaseIndent();
            }
            pw.decreaseIndent();
        }
    }

    /* access modifiers changed from: package-private */
    public IntervalStats readIntervalStatsForFile(int interval, long fileName) {
        IntervalStats stats;
        synchronized (this.mLock) {
            stats = new IntervalStats();
            try {
                readLocked((AtomicFile) this.mSortedStatFiles[interval].get(fileName, (Object) null), stats);
            } catch (Exception e) {
                return null;
            }
        }
        return stats;
    }
}
