package com.android.server.am;

import android.os.Binder;
import android.os.Parcel;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.text.format.DateFormat;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.app.procstats.DumpUtils;
import com.android.internal.app.procstats.IProcessStats;
import com.android.internal.app.procstats.ProcessState;
import com.android.internal.app.procstats.ProcessStats;
import com.android.internal.app.procstats.ServiceState;
import com.android.internal.os.BackgroundThread;
import com.android.server.utils.PriorityDump;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public final class ProcessStatsService extends IProcessStats.Stub {
    static final boolean DEBUG = false;
    static final int MAX_HISTORIC_STATES = 8;
    static final String STATE_FILE_CHECKIN_SUFFIX = ".ci";
    static final String STATE_FILE_PREFIX = "state-";
    static final String STATE_FILE_SUFFIX = ".bin";
    static final String TAG = "ProcessStatsService";
    static long WRITE_PERIOD = 1800000;
    final ActivityManagerService mAm;
    final File mBaseDir;
    boolean mCommitPending;
    AtomicFile mFile;
    @GuardedBy({"mAm"})
    Boolean mInjectedScreenState;
    int mLastMemOnlyState = -1;
    long mLastWriteTime;
    boolean mMemFactorLowered;
    Parcel mPendingWrite;
    boolean mPendingWriteCommitted;
    AtomicFile mPendingWriteFile;
    final Object mPendingWriteLock = new Object();
    ProcessStats mProcessStats;
    boolean mShuttingDown;
    final ReentrantLock mWriteLock = new ReentrantLock();

    public ProcessStatsService(ActivityManagerService am, File file) {
        this.mAm = am;
        this.mBaseDir = file;
        this.mBaseDir.mkdirs();
        this.mProcessStats = new ProcessStats(true);
        updateFile();
        SystemProperties.addChangeCallback(new Runnable() {
            public void run() {
                synchronized (ProcessStatsService.this.mAm) {
                    try {
                        ActivityManagerService.boostPriorityForLockedSection();
                        if (ProcessStatsService.this.mProcessStats.evaluateSystemProperties(false)) {
                            ProcessStatsService.this.mProcessStats.mFlags |= 4;
                            ProcessStatsService.this.writeStateLocked(true, true);
                            ProcessStatsService.this.mProcessStats.evaluateSystemProperties(true);
                        }
                    } catch (Throwable th) {
                        while (true) {
                            ActivityManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
            }
        });
    }

    public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
        try {
            return ProcessStatsService.super.onTransact(code, data, reply, flags);
        } catch (RuntimeException e) {
            if (!(e instanceof SecurityException)) {
                Slog.wtf(TAG, "Process Stats Crash", e);
            }
            throw e;
        }
    }

    @GuardedBy({"mAm"})
    public void updateProcessStateHolderLocked(ProcessStats.ProcessStateHolder holder, String packageName, int uid, long versionCode, String processName) {
        holder.pkg = this.mProcessStats.getPackageStateLocked(packageName, uid, versionCode);
        holder.state = this.mProcessStats.getProcessStateLocked(holder.pkg, processName);
    }

    @GuardedBy({"mAm"})
    public ProcessState getProcessStateLocked(String packageName, int uid, long versionCode, String processName) {
        return this.mProcessStats.getProcessStateLocked(packageName, uid, versionCode, processName);
    }

    @GuardedBy({"mAm"})
    public ServiceState getServiceStateLocked(String packageName, int uid, long versionCode, String processName, String className) {
        return this.mProcessStats.getServiceStateLocked(packageName, uid, versionCode, processName, className);
    }

    public boolean isMemFactorLowered() {
        return this.mMemFactorLowered;
    }

    @GuardedBy({"mAm"})
    public boolean setMemFactorLocked(int memFactor, boolean screenOn, long now) {
        this.mMemFactorLowered = memFactor < this.mLastMemOnlyState;
        this.mLastMemOnlyState = memFactor;
        Boolean bool = this.mInjectedScreenState;
        if (bool != null) {
            screenOn = bool.booleanValue();
        }
        if (screenOn) {
            memFactor += 4;
        }
        if (memFactor == this.mProcessStats.mMemFactor) {
            return false;
        }
        if (this.mProcessStats.mMemFactor != -1) {
            long[] jArr = this.mProcessStats.mMemFactorDurations;
            int i = this.mProcessStats.mMemFactor;
            jArr[i] = jArr[i] + (now - this.mProcessStats.mStartTime);
        }
        ProcessStats processStats = this.mProcessStats;
        processStats.mMemFactor = memFactor;
        processStats.mStartTime = now;
        ArrayMap<String, SparseArray<LongSparseArray<ProcessStats.PackageState>>> pmap = processStats.mPackages.getMap();
        for (int ipkg = pmap.size() - 1; ipkg >= 0; ipkg--) {
            SparseArray<LongSparseArray<ProcessStats.PackageState>> uids = pmap.valueAt(ipkg);
            for (int iuid = uids.size() - 1; iuid >= 0; iuid--) {
                LongSparseArray<ProcessStats.PackageState> vers = uids.valueAt(iuid);
                for (int iver = vers.size() - 1; iver >= 0; iver--) {
                    ArrayMap<String, ServiceState> services2 = vers.valueAt(iver).mServices;
                    for (int isvc = services2.size() - 1; isvc >= 0; isvc--) {
                        services2.valueAt(isvc).setMemFactor(memFactor, now);
                    }
                }
            }
        }
        return true;
    }

    @GuardedBy({"mAm"})
    public int getMemFactorLocked() {
        if (this.mProcessStats.mMemFactor != -1) {
            return this.mProcessStats.mMemFactor;
        }
        return 0;
    }

    @GuardedBy({"mAm"})
    public void addSysMemUsageLocked(long cachedMem, long freeMem, long zramMem, long kernelMem, long nativeMem) {
        this.mProcessStats.addSysMemUsage(cachedMem, freeMem, zramMem, kernelMem, nativeMem);
    }

    @GuardedBy({"mAm"})
    public void updateTrackingAssociationsLocked(int curSeq, long now) {
        this.mProcessStats.updateTrackingAssociationsLocked(curSeq, now);
    }

    @GuardedBy({"mAm"})
    public boolean shouldWriteNowLocked(long now) {
        if (now <= this.mLastWriteTime + WRITE_PERIOD) {
            return false;
        }
        if (SystemClock.elapsedRealtime() > this.mProcessStats.mTimePeriodStartRealtime + ProcessStats.COMMIT_PERIOD && SystemClock.uptimeMillis() > this.mProcessStats.mTimePeriodStartUptime + ProcessStats.COMMIT_UPTIME_PERIOD) {
            this.mCommitPending = true;
        }
        return true;
    }

    @GuardedBy({"mAm"})
    public void shutdownLocked() {
        Slog.w(TAG, "Writing process stats before shutdown...");
        this.mProcessStats.mFlags |= 2;
        writeStateSyncLocked();
        this.mShuttingDown = true;
    }

    @GuardedBy({"mAm"})
    public void writeStateAsyncLocked() {
        writeStateLocked(false);
    }

    @GuardedBy({"mAm"})
    public void writeStateSyncLocked() {
        writeStateLocked(true);
    }

    @GuardedBy({"mAm"})
    private void writeStateLocked(boolean sync) {
        if (!this.mShuttingDown) {
            boolean commitPending = this.mCommitPending;
            this.mCommitPending = false;
            writeStateLocked(sync, commitPending);
        }
    }

    @GuardedBy({"mAm"})
    public void writeStateLocked(boolean sync, boolean commit) {
        synchronized (this.mPendingWriteLock) {
            long now = SystemClock.uptimeMillis();
            if (this.mPendingWrite == null || !this.mPendingWriteCommitted) {
                this.mPendingWrite = Parcel.obtain();
                this.mProcessStats.mTimePeriodEndRealtime = SystemClock.elapsedRealtime();
                this.mProcessStats.mTimePeriodEndUptime = now;
                if (commit) {
                    this.mProcessStats.mFlags |= 1;
                }
                this.mProcessStats.writeToParcel(this.mPendingWrite, 0);
                this.mPendingWriteFile = new AtomicFile(this.mFile.getBaseFile());
                this.mPendingWriteCommitted = commit;
            }
            if (commit) {
                this.mProcessStats.resetSafely();
                updateFile();
                this.mAm.requestPssAllProcsLocked(SystemClock.uptimeMillis(), true, false);
            }
            this.mLastWriteTime = SystemClock.uptimeMillis();
            final long totalTime = SystemClock.uptimeMillis() - now;
            if (!sync) {
                BackgroundThread.getHandler().post(new Runnable() {
                    public void run() {
                        ProcessStatsService.this.performWriteState(totalTime);
                    }
                });
            } else {
                performWriteState(totalTime);
            }
        }
    }

    private void updateFile() {
        File file = this.mBaseDir;
        this.mFile = new AtomicFile(new File(file, STATE_FILE_PREFIX + this.mProcessStats.mTimePeriodStartClockStr + STATE_FILE_SUFFIX));
        this.mLastWriteTime = SystemClock.uptimeMillis();
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r0 = r2.startWrite();
        r0.write(r1.marshall());
        r0.flush();
        r2.finishWrite(r0);
        com.android.internal.logging.EventLogTags.writeCommitSysConfigFile("procstats", (android.os.SystemClock.uptimeMillis() - r3) + r9);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x003d, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x003f, code lost:
        r5 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:?, code lost:
        android.util.Slog.w(TAG, "Error writing process statistics", r5);
        r2.failWrite(r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0057, code lost:
        r1.recycle();
        trimHistoricStatesWriteLocked();
        r8.mWriteLock.unlock();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0062, code lost:
        throw r5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0019, code lost:
        r3 = android.os.SystemClock.uptimeMillis();
        r0 = null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void performWriteState(long r9) {
        /*
            r8 = this;
            java.lang.Object r0 = r8.mPendingWriteLock
            monitor-enter(r0)
            android.os.Parcel r1 = r8.mPendingWrite     // Catch:{ all -> 0x0063 }
            android.util.AtomicFile r2 = r8.mPendingWriteFile     // Catch:{ all -> 0x0063 }
            r3 = 0
            r8.mPendingWriteCommitted = r3     // Catch:{ all -> 0x0063 }
            if (r1 != 0) goto L_0x000e
            monitor-exit(r0)     // Catch:{ all -> 0x0063 }
            return
        L_0x000e:
            r3 = 0
            r8.mPendingWrite = r3     // Catch:{ all -> 0x0063 }
            r8.mPendingWriteFile = r3     // Catch:{ all -> 0x0063 }
            java.util.concurrent.locks.ReentrantLock r3 = r8.mWriteLock     // Catch:{ all -> 0x0063 }
            r3.lock()     // Catch:{ all -> 0x0063 }
            monitor-exit(r0)     // Catch:{ all -> 0x0063 }
            long r3 = android.os.SystemClock.uptimeMillis()
            r0 = 0
            java.io.FileOutputStream r5 = r2.startWrite()     // Catch:{ IOException -> 0x003f }
            r0 = r5
            byte[] r5 = r1.marshall()     // Catch:{ IOException -> 0x003f }
            r0.write(r5)     // Catch:{ IOException -> 0x003f }
            r0.flush()     // Catch:{ IOException -> 0x003f }
            r2.finishWrite(r0)     // Catch:{ IOException -> 0x003f }
            java.lang.String r5 = "procstats"
            long r6 = android.os.SystemClock.uptimeMillis()     // Catch:{ IOException -> 0x003f }
            long r6 = r6 - r3
            long r6 = r6 + r9
            com.android.internal.logging.EventLogTags.writeCommitSysConfigFile(r5, r6)     // Catch:{ IOException -> 0x003f }
            goto L_0x004a
        L_0x003d:
            r5 = move-exception
            goto L_0x0057
        L_0x003f:
            r5 = move-exception
            java.lang.String r6 = "ProcessStatsService"
            java.lang.String r7 = "Error writing process statistics"
            android.util.Slog.w(r6, r7, r5)     // Catch:{ all -> 0x003d }
            r2.failWrite(r0)     // Catch:{ all -> 0x003d }
        L_0x004a:
            r1.recycle()
            r8.trimHistoricStatesWriteLocked()
            java.util.concurrent.locks.ReentrantLock r5 = r8.mWriteLock
            r5.unlock()
            return
        L_0x0057:
            r1.recycle()
            r8.trimHistoricStatesWriteLocked()
            java.util.concurrent.locks.ReentrantLock r6 = r8.mWriteLock
            r6.unlock()
            throw r5
        L_0x0063:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0063 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ProcessStatsService.performWriteState(long):void");
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mAm"})
    public boolean readLocked(ProcessStats stats, AtomicFile file) {
        try {
            FileInputStream stream = file.openRead();
            stats.read(stream);
            stream.close();
            if (stats.mReadError == null) {
                return true;
            }
            Slog.w(TAG, "Ignoring existing stats; " + stats.mReadError);
            return false;
        } catch (Throwable e) {
            stats.mReadError = "caught exception: " + e;
            Slog.e(TAG, "Error reading process statistics", e);
            return false;
        }
    }

    private ArrayList<String> getCommittedFiles(int minNum, boolean inclCurrent, boolean inclCheckedIn) {
        File[] files = this.mBaseDir.listFiles();
        if (files == null || files.length <= minNum) {
            return null;
        }
        ArrayList<String> filesArray = new ArrayList<>(files.length);
        String currentFile = this.mFile.getBaseFile().getPath();
        for (File file : files) {
            String fileStr = file.getPath();
            if ((inclCheckedIn || !fileStr.endsWith(STATE_FILE_CHECKIN_SUFFIX)) && (inclCurrent || !fileStr.equals(currentFile))) {
                filesArray.add(fileStr);
            }
        }
        Collections.sort(filesArray);
        return filesArray;
    }

    @GuardedBy({"mAm"})
    public void trimHistoricStatesWriteLocked() {
        ArrayList<String> filesArray = getCommittedFiles(8, false, true);
        if (filesArray != null) {
            while (filesArray.size() > 8) {
                String file = filesArray.remove(0);
                Slog.i(TAG, "Pruning old procstats: " + file);
                new File(file).delete();
            }
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mAm"})
    public boolean dumpFilteredProcessesCsvLocked(PrintWriter pw, String header, boolean sepScreenStates, int[] screenStates, boolean sepMemStates, int[] memStates, boolean sepProcStates, int[] procStates, long now, String reqPackage) {
        ArrayList<ProcessState> procs = this.mProcessStats.collectProcessesLocked(screenStates, memStates, procStates, procStates, now, reqPackage, false);
        if (procs.size() <= 0) {
            return false;
        }
        if (header != null) {
            pw.println(header);
        }
        DumpUtils.dumpProcessListCsv(pw, procs, sepScreenStates, screenStates, sepMemStates, memStates, sepProcStates, procStates, now);
        return true;
    }

    static int[] parseStateList(String[] states, int mult, String arg, boolean[] outSep, String[] outError) {
        ArrayList<Integer> res = new ArrayList<>();
        int lastPos = 0;
        int i = 0;
        while (i <= arg.length()) {
            char c = i < arg.length() ? arg.charAt(i) : 0;
            if (c == ',' || c == '+' || c == ' ' || c == 0) {
                boolean isSep = c == ',';
                if (lastPos == 0) {
                    outSep[0] = isSep;
                } else if (!(c == 0 || outSep[0] == isSep)) {
                    outError[0] = "inconsistent separators (can't mix ',' with '+')";
                    return null;
                }
                if (lastPos < i - 1) {
                    String str = arg.substring(lastPos, i);
                    int j = 0;
                    while (true) {
                        if (j >= states.length) {
                            break;
                        } else if (str.equals(states[j])) {
                            res.add(Integer.valueOf(j));
                            str = null;
                            break;
                        } else {
                            j++;
                        }
                    }
                    if (str != null) {
                        outError[0] = "invalid word \"" + str + "\"";
                        return null;
                    }
                }
                lastPos = i + 1;
            }
            i++;
        }
        int[] finalRes = new int[res.size()];
        for (int i2 = 0; i2 < res.size(); i2++) {
            finalRes[i2] = res.get(i2).intValue() * mult;
        }
        return finalRes;
    }

    static int parseSectionOptions(String optionsStr) {
        String[] sectionsStr = optionsStr.split(",");
        if (sectionsStr.length == 0) {
            return 15;
        }
        int res = 0;
        List<String> optionStrList = Arrays.asList(ProcessStats.OPTIONS_STR);
        for (String sectionStr : sectionsStr) {
            int optionIndex = optionStrList.indexOf(sectionStr);
            if (optionIndex != -1) {
                res |= ProcessStats.OPTIONS[optionIndex];
            }
        }
        return res;
    }

    public byte[] getCurrentStats(List<ParcelFileDescriptor> historic) {
        ArrayList<String> files;
        int i;
        this.mAm.mContext.enforceCallingOrSelfPermission("android.permission.PACKAGE_USAGE_STATS", (String) null);
        Parcel current = Parcel.obtain();
        synchronized (this.mAm) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                long now = SystemClock.uptimeMillis();
                this.mProcessStats.mTimePeriodEndRealtime = SystemClock.elapsedRealtime();
                this.mProcessStats.mTimePeriodEndUptime = now;
                this.mProcessStats.writeToParcel(current, now, 0);
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        this.mWriteLock.lock();
        if (historic != null) {
            try {
                files = getCommittedFiles(0, false, true);
                if (files != null) {
                    i = files.size() - 1;
                    while (i >= 0) {
                        historic.add(ParcelFileDescriptor.open(new File(files.get(i)), 268435456));
                        i--;
                    }
                }
            } catch (IOException e) {
                Slog.w(TAG, "Failure opening procstat file " + files.get(i), e);
            } catch (Throwable th2) {
                this.mWriteLock.unlock();
                throw th2;
            }
        }
        this.mWriteLock.unlock();
        return current.marshall();
    }

    public long getCommittedStats(long highWaterMarkMs, int section, boolean doAggregate, List<ParcelFileDescriptor> committedStats) {
        String highWaterMarkStr;
        ArrayList<String> files;
        String str;
        int i = section;
        List<ParcelFileDescriptor> list = committedStats;
        String str2 = STATE_FILE_PREFIX;
        this.mAm.mContext.enforceCallingOrSelfPermission("android.permission.PACKAGE_USAGE_STATS", (String) null);
        ProcessStats mergedStats = new ProcessStats(false);
        long newHighWaterMark = highWaterMarkMs;
        this.mWriteLock.lock();
        try {
            ArrayList<String> files2 = getCommittedFiles(0, false, true);
            if (files2 != null) {
                try {
                    String highWaterMarkStr2 = DateFormat.format("yyyy-MM-dd-HH-mm-ss", highWaterMarkMs).toString();
                    ProcessStats stats = new ProcessStats(false);
                    int i2 = files2.size() - 1;
                    while (i2 >= 0) {
                        String fileName = files2.get(i2);
                        try {
                            str = str2;
                            try {
                                String startTimeStr = fileName.substring(fileName.lastIndexOf(str2) + str2.length(), fileName.lastIndexOf(STATE_FILE_SUFFIX));
                                if (startTimeStr.compareToIgnoreCase(highWaterMarkStr2) > 0) {
                                    String str3 = startTimeStr;
                                    InputStream is = new ParcelFileDescriptor.AutoCloseInputStream(ParcelFileDescriptor.open(new File(fileName), 268435456));
                                    stats.reset();
                                    stats.read(is);
                                    is.close();
                                    files = files2;
                                    highWaterMarkStr = highWaterMarkStr2;
                                    try {
                                        if (stats.mTimePeriodStartClock > newHighWaterMark) {
                                            newHighWaterMark = stats.mTimePeriodStartClock;
                                        }
                                        if (doAggregate) {
                                            mergedStats.add(stats);
                                        } else {
                                            list.add(protoToParcelFileDescriptor(stats, i));
                                        }
                                        if (stats.mReadError != null) {
                                            Log.w(TAG, "Failure reading process stats: " + stats.mReadError);
                                        }
                                    } catch (IOException e) {
                                        e = e;
                                        Slog.w(TAG, "Failure opening procstat file " + fileName, e);
                                        i2--;
                                        str2 = str;
                                        files2 = files;
                                        highWaterMarkStr2 = highWaterMarkStr;
                                    } catch (IndexOutOfBoundsException e2) {
                                        e = e2;
                                        Slog.w(TAG, "Failure to read and parse commit file " + fileName, e);
                                        i2--;
                                        str2 = str;
                                        files2 = files;
                                        highWaterMarkStr2 = highWaterMarkStr;
                                    }
                                } else {
                                    String str4 = startTimeStr;
                                    files = files2;
                                    highWaterMarkStr = highWaterMarkStr2;
                                }
                            } catch (IOException e3) {
                                e = e3;
                                files = files2;
                                highWaterMarkStr = highWaterMarkStr2;
                                Slog.w(TAG, "Failure opening procstat file " + fileName, e);
                                i2--;
                                str2 = str;
                                files2 = files;
                                highWaterMarkStr2 = highWaterMarkStr;
                            } catch (IndexOutOfBoundsException e4) {
                                e = e4;
                                files = files2;
                                highWaterMarkStr = highWaterMarkStr2;
                                Slog.w(TAG, "Failure to read and parse commit file " + fileName, e);
                                i2--;
                                str2 = str;
                                files2 = files;
                                highWaterMarkStr2 = highWaterMarkStr;
                            }
                        } catch (IOException e5) {
                            e = e5;
                            str = str2;
                            files = files2;
                            highWaterMarkStr = highWaterMarkStr2;
                            Slog.w(TAG, "Failure opening procstat file " + fileName, e);
                            i2--;
                            str2 = str;
                            files2 = files;
                            highWaterMarkStr2 = highWaterMarkStr;
                        } catch (IndexOutOfBoundsException e6) {
                            e = e6;
                            str = str2;
                            files = files2;
                            highWaterMarkStr = highWaterMarkStr2;
                            Slog.w(TAG, "Failure to read and parse commit file " + fileName, e);
                            i2--;
                            str2 = str;
                            files2 = files;
                            highWaterMarkStr2 = highWaterMarkStr;
                        }
                        i2--;
                        str2 = str;
                        files2 = files;
                        highWaterMarkStr2 = highWaterMarkStr;
                    }
                    String str5 = highWaterMarkStr2;
                    if (doAggregate) {
                        list.add(protoToParcelFileDescriptor(mergedStats, i));
                    }
                    this.mWriteLock.unlock();
                    return newHighWaterMark;
                } catch (IOException e7) {
                    e = e7;
                    try {
                        Slog.w(TAG, "Failure opening procstat file", e);
                        this.mWriteLock.unlock();
                        return newHighWaterMark;
                    } catch (Throwable th) {
                        th = th;
                    }
                }
            } else {
                long j = highWaterMarkMs;
                ArrayList<String> arrayList = files2;
                this.mWriteLock.unlock();
                return newHighWaterMark;
            }
        } catch (IOException e8) {
            e = e8;
            long j2 = highWaterMarkMs;
            Slog.w(TAG, "Failure opening procstat file", e);
            this.mWriteLock.unlock();
            return newHighWaterMark;
        } catch (Throwable th2) {
            th = th2;
            long j3 = highWaterMarkMs;
            this.mWriteLock.unlock();
            throw th;
        }
    }

    private ParcelFileDescriptor protoToParcelFileDescriptor(ProcessStats stats, int section) throws IOException {
        ParcelFileDescriptor[] fds = ParcelFileDescriptor.createPipe();
        final ParcelFileDescriptor[] parcelFileDescriptorArr = fds;
        final ProcessStats processStats = stats;
        final int i = section;
        new Thread("ProcessStats pipe output") {
            public void run() {
                try {
                    FileOutputStream fout = new ParcelFileDescriptor.AutoCloseOutputStream(parcelFileDescriptorArr[1]);
                    ProtoOutputStream proto = new ProtoOutputStream(fout);
                    processStats.writeToProto(proto, processStats.mTimePeriodEndRealtime, i);
                    proto.flush();
                    fout.close();
                } catch (IOException e) {
                    Slog.w(ProcessStatsService.TAG, "Failure writing pipe", e);
                }
            }
        }.start();
        return fds[0];
    }

    public ParcelFileDescriptor getStatsOverTime(long minTime) {
        long curTime;
        this.mAm.mContext.enforceCallingOrSelfPermission("android.permission.PACKAGE_USAGE_STATS", (String) null);
        Parcel current = Parcel.obtain();
        synchronized (this.mAm) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                long now = SystemClock.uptimeMillis();
                this.mProcessStats.mTimePeriodEndRealtime = SystemClock.elapsedRealtime();
                this.mProcessStats.mTimePeriodEndUptime = now;
                this.mProcessStats.writeToParcel(current, now, 0);
                curTime = this.mProcessStats.mTimePeriodEndRealtime - this.mProcessStats.mTimePeriodStartRealtime;
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        this.mWriteLock.lock();
        if (curTime < minTime) {
            try {
                ArrayList<String> files = getCommittedFiles(0, false, true);
                if (files != null && files.size() > 0) {
                    current.setDataPosition(0);
                    ProcessStats stats = (ProcessStats) ProcessStats.CREATOR.createFromParcel(current);
                    current.recycle();
                    int i = files.size() - 1;
                    while (i >= 0 && stats.mTimePeriodEndRealtime - stats.mTimePeriodStartRealtime < minTime) {
                        AtomicFile file = new AtomicFile(new File(files.get(i)));
                        i--;
                        ProcessStats moreStats = new ProcessStats(false);
                        readLocked(moreStats, file);
                        if (moreStats.mReadError == null) {
                            stats.add(moreStats);
                            StringBuilder sb = new StringBuilder();
                            sb.append("Added stats: ");
                            sb.append(moreStats.mTimePeriodStartClockStr);
                            sb.append(", over ");
                            TimeUtils.formatDuration(moreStats.mTimePeriodEndRealtime - moreStats.mTimePeriodStartRealtime, sb);
                            Slog.i(TAG, sb.toString());
                        } else {
                            Slog.w(TAG, "Failure reading " + files.get(i + 1) + "; " + moreStats.mReadError);
                        }
                    }
                    current = Parcel.obtain();
                    stats.writeToParcel(current, 0);
                }
            } catch (IOException e) {
                Slog.w(TAG, "Failed building output pipe", e);
                this.mWriteLock.unlock();
                return null;
            } catch (Throwable th2) {
                this.mWriteLock.unlock();
                throw th2;
            }
        }
        final byte[] outData = current.marshall();
        current.recycle();
        final ParcelFileDescriptor[] fds = ParcelFileDescriptor.createPipe();
        new Thread("ProcessStats pipe output") {
            public void run() {
                FileOutputStream fout = new ParcelFileDescriptor.AutoCloseOutputStream(fds[1]);
                try {
                    fout.write(outData);
                    fout.close();
                } catch (IOException e) {
                    Slog.w(ProcessStatsService.TAG, "Failure writing pipe", e);
                }
            }
        }.start();
        ParcelFileDescriptor parcelFileDescriptor = fds[0];
        this.mWriteLock.unlock();
        return parcelFileDescriptor;
    }

    public int getCurrentMemoryState() {
        int i;
        synchronized (this.mAm) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                i = this.mLastMemOnlyState;
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        return i;
    }

    private void dumpAggregatedStats(PrintWriter pw, long aggregateHours, long now, String reqPackage, boolean isCompact, boolean dumpDetails, boolean dumpFullDetails, boolean dumpAll, boolean activeOnly, int section) {
        PrintWriter printWriter = pw;
        ParcelFileDescriptor pfd = getStatsOverTime((((aggregateHours * 60) * 60) * 1000) - (ProcessStats.COMMIT_PERIOD / 2));
        if (pfd == null) {
            printWriter.println("Unable to build stats!");
            return;
        }
        ProcessStats stats = new ProcessStats(false);
        stats.read(new ParcelFileDescriptor.AutoCloseInputStream(pfd));
        if (stats.mReadError != null) {
            printWriter.print("Failure reading: ");
            printWriter.println(stats.mReadError);
        } else if (isCompact) {
            stats.dumpCheckinLocked(printWriter, reqPackage, section);
        } else {
            String str = reqPackage;
            int i = section;
            if (dumpDetails || dumpFullDetails) {
                stats.dumpLocked(pw, reqPackage, now, !dumpFullDetails, dumpDetails, dumpAll, activeOnly, section);
                return;
            }
            stats.dumpSummaryLocked(pw, reqPackage, now, activeOnly);
        }
    }

    private static void dumpHelp(PrintWriter pw) {
        pw.println("Process stats (procstats) dump options:");
        pw.println("    [--checkin|-c|--csv] [--csv-screen] [--csv-proc] [--csv-mem]");
        pw.println("    [--details] [--full-details] [--current] [--hours N] [--last N]");
        pw.println("    [--max N] --active] [--commit] [--reset] [--clear] [--write] [-h]");
        pw.println("    [--start-testing] [--stop-testing] ");
        pw.println("    [--pretend-screen-on] [--pretend-screen-off] [--stop-pretend-screen]");
        pw.println("    [<package.name>]");
        pw.println("  --checkin: perform a checkin: print and delete old committed states.");
        pw.println("  -c: print only state in checkin format.");
        pw.println("  --csv: output data suitable for putting in a spreadsheet.");
        pw.println("  --csv-screen: on, off.");
        pw.println("  --csv-mem: norm, mod, low, crit.");
        pw.println("  --csv-proc: pers, top, fore, vis, precept, backup,");
        pw.println("    service, home, prev, cached");
        pw.println("  --details: dump per-package details, not just summary.");
        pw.println("  --full-details: dump all timing and active state details.");
        pw.println("  --current: only dump current state.");
        pw.println("  --hours: aggregate over about N last hours.");
        pw.println("  --last: only show the last committed stats at index N (starting at 1).");
        pw.println("  --max: for -a, max num of historical batches to print.");
        pw.println("  --active: only show currently active processes/services.");
        pw.println("  --commit: commit current stats to disk and reset to start new stats.");
        pw.println("  --section: proc|pkg-proc|pkg-svc|pkg-asc|pkg-all|all ");
        pw.println("    options can be combined to select desired stats");
        pw.println("  --reset: reset current stats, without committing.");
        pw.println("  --clear: clear all stats; does both --reset and deletes old stats.");
        pw.println("  --write: write current in-memory stats to disk.");
        pw.println("  --read: replace current stats with last-written stats.");
        pw.println("  --start-testing: clear all stats and starting high frequency pss sampling.");
        pw.println("  --stop-testing: stop high frequency pss sampling.");
        pw.println("  --pretend-screen-on: pretend screen is on.");
        pw.println("  --pretend-screen-off: pretend screen is off.");
        pw.println("  --stop-pretend-screen: forget \"pretend screen\" and use the real state.");
        pw.println("  -a: print everything.");
        pw.println("  -h: print this help text.");
        pw.println("  <package.name>: optional name of package to filter output by.");
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (com.android.internal.util.DumpUtils.checkDumpAndUsageStatsPermission(this.mAm.mContext, TAG, pw)) {
            long ident = Binder.clearCallingIdentity();
            try {
                if (args.length <= 0 || !PriorityDump.PROTO_ARG.equals(args[0])) {
                    dumpInner(pw, args);
                } else {
                    dumpProto(fd);
                }
            } finally {
                Binder.restoreCallingIdentity(ident);
            }
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:131:0x0335, code lost:
        com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection();
        r2 = r30;
        r3 = r32;
        r4 = r33;
        r1 = r34;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:451:0x0920, code lost:
        com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:452:0x0923, code lost:
        if (r33 != false) goto L_0x0958;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:453:0x0925, code lost:
        if (r34 == false) goto L_0x092a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:454:0x0927, code lost:
        r46.println();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:455:0x092a, code lost:
        r15.println("AGGREGATED OVER LAST 24 HOURS:");
        r1 = r45;
        r2 = r46;
        r5 = r16;
        r7 = r31;
        r8 = r30;
        r9 = r18;
        r10 = r20;
        r11 = r21;
        r12 = r24;
        r35 = r13;
        r13 = r29;
        dumpAggregatedStats(r2, 24, r5, r7, r8, r9, r10, r11, r12, r13);
        r46.println();
        r15.println("AGGREGATED OVER LAST 3 HOURS:");
        dumpAggregatedStats(r2, 3, r5, r7, r8, r9, r10, r11, r12, r13);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:456:0x0958, code lost:
        r35 = r13;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:551:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:552:?, code lost:
        return;
     */
    /* JADX WARNING: Removed duplicated region for block: B:397:0x0842 A[Catch:{ all -> 0x0867 }] */
    /* JADX WARNING: Removed duplicated region for block: B:398:0x0862  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void dumpInner(java.io.PrintWriter r46, java.lang.String[] r47) {
        /*
            r45 = this;
            r14 = r45
            r15 = r46
            r13 = r47
            long r16 = android.os.SystemClock.uptimeMillis()
            r0 = 0
            r1 = 0
            r2 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            r8 = 0
            r9 = 0
            r10 = 2
            r11 = 0
            r12 = 0
            r18 = 0
            r19 = r0
            r0 = 2
            int[] r0 = new int[r0]
            r0 = {0, 4} // fill-array
            r20 = 0
            r21 = r8
            r8 = 1
            r22 = r0
            int[] r0 = new int[r8]
            r23 = r7
            r7 = 0
            r24 = 3
            r0[r7] = r24
            r24 = 1
            int[] r25 = com.android.internal.app.procstats.ProcessStats.ALL_PROC_STATES
            r26 = 15
            if (r13 == 0) goto L_0x04fd
            r27 = 0
            r28 = r24
            r24 = r18
            r18 = r10
            r10 = r0
            r44 = r2
            r2 = r1
            r1 = r27
            r27 = r20
            r20 = r11
            r11 = r9
            r9 = r6
            r6 = r5
            r5 = r4
            r4 = r3
            r3 = r44
        L_0x0052:
            int r0 = r13.length
            if (r1 >= r0) goto L_0x04de
            r7 = r13[r1]
            java.lang.String r0 = "--checkin"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x0063
            r19 = 1
            goto L_0x04d8
        L_0x0063:
            java.lang.String r0 = "-c"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x006f
            r0 = 1
            r2 = r0
            goto L_0x04d8
        L_0x006f:
            java.lang.String r0 = "--csv"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x007a
            r3 = 1
            goto L_0x04d8
        L_0x007a:
            java.lang.String r0 = "--csv-screen"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x00d9
            int r1 = r1 + 1
            int r0 = r13.length
            if (r1 < r0) goto L_0x0090
            java.lang.String r0 = "Error: argument required for --csv-screen"
            r15.println(r0)
            dumpHelp(r46)
            return
        L_0x0090:
            boolean[] r0 = new boolean[r8]
            r30 = r2
            java.lang.String[] r2 = new java.lang.String[r8]
            java.lang.String[] r8 = com.android.internal.app.procstats.DumpUtils.ADJ_SCREEN_NAMES_CSV
            r32 = r3
            r3 = 4
            r33 = r4
            r4 = r13[r1]
            int[] r3 = parseStateList(r8, r3, r4, r0, r2)
            if (r3 != 0) goto L_0x00ca
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r8 = "Error in \""
            r4.append(r8)
            r8 = r13[r1]
            r4.append(r8)
            java.lang.String r8 = "\": "
            r4.append(r8)
            r8 = 0
            r8 = r2[r8]
            r4.append(r8)
            java.lang.String r4 = r4.toString()
            r15.println(r4)
            dumpHelp(r46)
            return
        L_0x00ca:
            r8 = 0
            boolean r0 = r0[r8]
            r24 = r0
            r22 = r3
            r2 = r30
            r3 = r32
            r4 = r33
            goto L_0x04d8
        L_0x00d9:
            r30 = r2
            r32 = r3
            r33 = r4
            java.lang.String r0 = "--csv-mem"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x0137
            int r1 = r1 + 1
            int r0 = r13.length
            if (r1 < r0) goto L_0x00f5
            java.lang.String r0 = "Error: argument required for --csv-mem"
            r15.println(r0)
            dumpHelp(r46)
            return
        L_0x00f5:
            r2 = 1
            boolean[] r0 = new boolean[r2]
            java.lang.String[] r3 = new java.lang.String[r2]
            java.lang.String[] r4 = com.android.internal.app.procstats.DumpUtils.ADJ_MEM_NAMES_CSV
            r8 = r13[r1]
            int[] r4 = parseStateList(r4, r2, r8, r0, r3)
            if (r4 != 0) goto L_0x0129
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r8 = "Error in \""
            r2.append(r8)
            r8 = r13[r1]
            r2.append(r8)
            java.lang.String r8 = "\": "
            r2.append(r8)
            r8 = 0
            r8 = r3[r8]
            r2.append(r8)
            java.lang.String r2 = r2.toString()
            r15.println(r2)
            dumpHelp(r46)
            return
        L_0x0129:
            r8 = 0
            boolean r0 = r0[r8]
            r27 = r0
            r10 = r4
            r2 = r30
            r3 = r32
            r4 = r33
            goto L_0x04d8
        L_0x0137:
            java.lang.String r0 = "--csv-proc"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x0190
            int r1 = r1 + 1
            int r0 = r13.length
            if (r1 < r0) goto L_0x014d
            java.lang.String r0 = "Error: argument required for --csv-proc"
            r15.println(r0)
            dumpHelp(r46)
            return
        L_0x014d:
            r2 = 1
            boolean[] r0 = new boolean[r2]
            java.lang.String[] r3 = new java.lang.String[r2]
            java.lang.String[] r4 = com.android.internal.app.procstats.DumpUtils.STATE_NAMES_CSV
            r8 = r13[r1]
            int[] r4 = parseStateList(r4, r2, r8, r0, r3)
            if (r4 != 0) goto L_0x0181
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r8 = "Error in \""
            r2.append(r8)
            r8 = r13[r1]
            r2.append(r8)
            java.lang.String r8 = "\": "
            r2.append(r8)
            r8 = 0
            r8 = r3[r8]
            r2.append(r8)
            java.lang.String r2 = r2.toString()
            r15.println(r2)
            dumpHelp(r46)
            return
        L_0x0181:
            r8 = 0
            boolean r0 = r0[r8]
            r28 = r0
            r25 = r4
            r2 = r30
            r3 = r32
            r4 = r33
            goto L_0x04d8
        L_0x0190:
            java.lang.String r0 = "--details"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x01a2
            r0 = 1
            r5 = r0
            r2 = r30
            r3 = r32
            r4 = r33
            goto L_0x04d8
        L_0x01a2:
            java.lang.String r0 = "--full-details"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x01b4
            r0 = 1
            r6 = r0
            r2 = r30
            r3 = r32
            r4 = r33
            goto L_0x04d8
        L_0x01b4:
            java.lang.String r0 = "--hours"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x01f5
            int r1 = r1 + 1
            int r0 = r13.length
            if (r1 < r0) goto L_0x01ca
            java.lang.String r0 = "Error: argument required for --hours"
            r15.println(r0)
            dumpHelp(r46)
            return
        L_0x01ca:
            r0 = r13[r1]     // Catch:{ NumberFormatException -> 0x01da }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x01da }
            r21 = r0
            r2 = r30
            r3 = r32
            r4 = r33
            goto L_0x04d8
        L_0x01da:
            r0 = move-exception
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Error: --hours argument not an int -- "
            r2.append(r3)
            r3 = r13[r1]
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r15.println(r2)
            dumpHelp(r46)
            return
        L_0x01f5:
            java.lang.String r0 = "--last"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x0235
            int r1 = r1 + 1
            int r0 = r13.length
            if (r1 < r0) goto L_0x020b
            java.lang.String r0 = "Error: argument required for --last"
            r15.println(r0)
            dumpHelp(r46)
            return
        L_0x020b:
            r0 = r13[r1]     // Catch:{ NumberFormatException -> 0x021a }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x021a }
            r11 = r0
            r2 = r30
            r3 = r32
            r4 = r33
            goto L_0x04d8
        L_0x021a:
            r0 = move-exception
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Error: --last argument not an int -- "
            r2.append(r3)
            r3 = r13[r1]
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r15.println(r2)
            dumpHelp(r46)
            return
        L_0x0235:
            java.lang.String r0 = "--max"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x0276
            int r1 = r1 + 1
            int r0 = r13.length
            if (r1 < r0) goto L_0x024b
            java.lang.String r0 = "Error: argument required for --max"
            r15.println(r0)
            dumpHelp(r46)
            return
        L_0x024b:
            r0 = r13[r1]     // Catch:{ NumberFormatException -> 0x025b }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x025b }
            r18 = r0
            r2 = r30
            r3 = r32
            r4 = r33
            goto L_0x04d8
        L_0x025b:
            r0 = move-exception
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Error: --max argument not an int -- "
            r2.append(r3)
            r3 = r13[r1]
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            r15.println(r2)
            dumpHelp(r46)
            return
        L_0x0276:
            java.lang.String r0 = "--active"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x0288
            r0 = 1
            r4 = 1
            r20 = r0
            r2 = r30
            r3 = r32
            goto L_0x04d8
        L_0x0288:
            java.lang.String r0 = "--current"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x0297
            r4 = 1
            r2 = r30
            r3 = r32
            goto L_0x04d8
        L_0x0297:
            java.lang.String r0 = "--commit"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x02c9
            com.android.server.am.ActivityManagerService r2 = r14.mAm
            monitor-enter(r2)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x02c3 }
            com.android.internal.app.procstats.ProcessStats r0 = r14.mProcessStats     // Catch:{ all -> 0x02c3 }
            int r3 = r0.mFlags     // Catch:{ all -> 0x02c3 }
            r4 = 1
            r3 = r3 | r4
            r0.mFlags = r3     // Catch:{ all -> 0x02c3 }
            r14.writeStateLocked(r4, r4)     // Catch:{ all -> 0x02c3 }
            java.lang.String r0 = "Process stats committed."
            r15.println(r0)     // Catch:{ all -> 0x02c3 }
            r23 = 1
            monitor-exit(r2)     // Catch:{ all -> 0x02c3 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            r2 = r30
            r3 = r32
            r4 = r33
            goto L_0x04d8
        L_0x02c3:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x02c3 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x02c9:
            java.lang.String r0 = "--section"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x02ef
            int r1 = r1 + 1
            int r0 = r13.length
            if (r1 < r0) goto L_0x02df
            java.lang.String r0 = "Error: argument required for --section"
            r15.println(r0)
            dumpHelp(r46)
            return
        L_0x02df:
            r0 = r13[r1]
            int r0 = parseSectionOptions(r0)
            r26 = r0
            r2 = r30
            r3 = r32
            r4 = r33
            goto L_0x04d8
        L_0x02ef:
            java.lang.String r0 = "--clear"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x034c
            com.android.server.am.ActivityManagerService r2 = r14.mAm
            monitor-enter(r2)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0342 }
            com.android.internal.app.procstats.ProcessStats r0 = r14.mProcessStats     // Catch:{ all -> 0x0342 }
            r0.resetSafely()     // Catch:{ all -> 0x0342 }
            com.android.server.am.ActivityManagerService r0 = r14.mAm     // Catch:{ all -> 0x0342 }
            long r3 = android.os.SystemClock.uptimeMillis()     // Catch:{ all -> 0x0342 }
            r34 = r1
            r1 = 1
            r8 = 0
            r0.requestPssAllProcsLocked(r3, r1, r8)     // Catch:{ all -> 0x034a }
            java.util.ArrayList r0 = r14.getCommittedFiles(r8, r1, r1)     // Catch:{ all -> 0x034a }
            if (r0 == 0) goto L_0x032d
            r1 = r8
        L_0x0316:
            int r3 = r0.size()     // Catch:{ all -> 0x034a }
            if (r1 >= r3) goto L_0x032d
            java.io.File r3 = new java.io.File     // Catch:{ all -> 0x034a }
            java.lang.Object r4 = r0.get(r1)     // Catch:{ all -> 0x034a }
            java.lang.String r4 = (java.lang.String) r4     // Catch:{ all -> 0x034a }
            r3.<init>(r4)     // Catch:{ all -> 0x034a }
            r3.delete()     // Catch:{ all -> 0x034a }
            int r1 = r1 + 1
            goto L_0x0316
        L_0x032d:
            java.lang.String r1 = "All process stats cleared."
            r15.println(r1)     // Catch:{ all -> 0x034a }
            r23 = 1
            monitor-exit(r2)     // Catch:{ all -> 0x034a }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            r2 = r30
            r3 = r32
            r4 = r33
            r1 = r34
            goto L_0x04d8
        L_0x0342:
            r0 = move-exception
            r34 = r1
        L_0x0345:
            monitor-exit(r2)     // Catch:{ all -> 0x034a }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x034a:
            r0 = move-exception
            goto L_0x0345
        L_0x034c:
            r34 = r1
            java.lang.String r0 = "--write"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x037a
            com.android.server.am.ActivityManagerService r1 = r14.mAm
            monitor-enter(r1)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0374 }
            r45.writeStateSyncLocked()     // Catch:{ all -> 0x0374 }
            java.lang.String r0 = "Process stats written."
            r15.println(r0)     // Catch:{ all -> 0x0374 }
            r23 = 1
            monitor-exit(r1)     // Catch:{ all -> 0x0374 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            r2 = r30
            r3 = r32
            r4 = r33
            r1 = r34
            goto L_0x04d8
        L_0x0374:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0374 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x037a:
            java.lang.String r0 = "--read"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x03aa
            com.android.server.am.ActivityManagerService r1 = r14.mAm
            monitor-enter(r1)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x03a4 }
            com.android.internal.app.procstats.ProcessStats r0 = r14.mProcessStats     // Catch:{ all -> 0x03a4 }
            android.util.AtomicFile r2 = r14.mFile     // Catch:{ all -> 0x03a4 }
            r14.readLocked(r0, r2)     // Catch:{ all -> 0x03a4 }
            java.lang.String r0 = "Process stats read."
            r15.println(r0)     // Catch:{ all -> 0x03a4 }
            r23 = 1
            monitor-exit(r1)     // Catch:{ all -> 0x03a4 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            r2 = r30
            r3 = r32
            r4 = r33
            r1 = r34
            goto L_0x04d8
        L_0x03a4:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x03a4 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x03aa:
            java.lang.String r0 = "--start-testing"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x03d9
            com.android.server.am.ActivityManagerService r1 = r14.mAm
            monitor-enter(r1)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x03d3 }
            com.android.server.am.ActivityManagerService r0 = r14.mAm     // Catch:{ all -> 0x03d3 }
            r2 = 1
            r0.setTestPssMode(r2)     // Catch:{ all -> 0x03d3 }
            java.lang.String r0 = "Started high frequency sampling."
            r15.println(r0)     // Catch:{ all -> 0x03d3 }
            r23 = 1
            monitor-exit(r1)     // Catch:{ all -> 0x03d3 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            r2 = r30
            r3 = r32
            r4 = r33
            r1 = r34
            goto L_0x04d8
        L_0x03d3:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x03d3 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x03d9:
            java.lang.String r0 = "--stop-testing"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x0408
            com.android.server.am.ActivityManagerService r1 = r14.mAm
            monitor-enter(r1)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0402 }
            com.android.server.am.ActivityManagerService r0 = r14.mAm     // Catch:{ all -> 0x0402 }
            r2 = 0
            r0.setTestPssMode(r2)     // Catch:{ all -> 0x0402 }
            java.lang.String r0 = "Stopped high frequency sampling."
            r15.println(r0)     // Catch:{ all -> 0x0402 }
            r23 = 1
            monitor-exit(r1)     // Catch:{ all -> 0x0402 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            r2 = r30
            r3 = r32
            r4 = r33
            r1 = r34
            goto L_0x04d8
        L_0x0402:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0402 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x0408:
            java.lang.String r0 = "--pretend-screen-on"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x0433
            com.android.server.am.ActivityManagerService r1 = r14.mAm
            monitor-enter(r1)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x042d }
            r2 = 1
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r2)     // Catch:{ all -> 0x042d }
            r14.mInjectedScreenState = r0     // Catch:{ all -> 0x042d }
            monitor-exit(r1)     // Catch:{ all -> 0x042d }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            r23 = 1
            r2 = r30
            r3 = r32
            r4 = r33
            r1 = r34
            goto L_0x04d8
        L_0x042d:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x042d }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x0433:
            java.lang.String r0 = "--pretend-screen-off"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x045e
            com.android.server.am.ActivityManagerService r1 = r14.mAm
            monitor-enter(r1)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0458 }
            r2 = 0
            java.lang.Boolean r0 = java.lang.Boolean.valueOf(r2)     // Catch:{ all -> 0x0458 }
            r14.mInjectedScreenState = r0     // Catch:{ all -> 0x0458 }
            monitor-exit(r1)     // Catch:{ all -> 0x0458 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            r23 = 1
            r2 = r30
            r3 = r32
            r4 = r33
            r1 = r34
            goto L_0x04d8
        L_0x0458:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0458 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x045e:
            java.lang.String r0 = "--stop-pretend-screen"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x0484
            com.android.server.am.ActivityManagerService r1 = r14.mAm
            monitor-enter(r1)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x047e }
            r0 = 0
            r14.mInjectedScreenState = r0     // Catch:{ all -> 0x047e }
            monitor-exit(r1)     // Catch:{ all -> 0x047e }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            r23 = 1
            r2 = r30
            r3 = r32
            r4 = r33
            r1 = r34
            goto L_0x04d8
        L_0x047e:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x047e }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x0484:
            java.lang.String r0 = "-h"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x0490
            dumpHelp(r46)
            return
        L_0x0490:
            java.lang.String r0 = "-a"
            boolean r0 = r0.equals(r7)
            if (r0 == 0) goto L_0x04a5
            r0 = 1
            r1 = 1
            r5 = r0
            r9 = r1
            r2 = r30
            r3 = r32
            r4 = r33
            r1 = r34
            goto L_0x04d8
        L_0x04a5:
            int r0 = r7.length()
            if (r0 <= 0) goto L_0x04cc
            r1 = 0
            char r0 = r7.charAt(r1)
            r1 = 45
            if (r0 != r1) goto L_0x04cc
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Unknown option: "
            r0.append(r1)
            r0.append(r7)
            java.lang.String r0 = r0.toString()
            r15.println(r0)
            dumpHelp(r46)
            return
        L_0x04cc:
            r0 = r7
            r1 = 1
            r12 = r0
            r5 = r1
            r2 = r30
            r3 = r32
            r4 = r33
            r1 = r34
        L_0x04d8:
            r7 = 1
            int r1 = r1 + r7
            r8 = r7
            r7 = 0
            goto L_0x0052
        L_0x04de:
            r34 = r1
            r30 = r2
            r32 = r3
            r33 = r4
            r7 = r11
            r8 = r21
            r21 = r9
            r11 = r10
            r10 = r22
            r9 = r25
            r22 = r18
            r25 = r24
            r18 = r5
            r24 = r20
            r20 = r6
            r6 = r26
            goto L_0x051f
        L_0x04fd:
            r30 = r1
            r32 = r2
            r33 = r3
            r7 = r9
            r27 = r20
            r8 = r21
            r28 = r24
            r9 = r25
            r20 = r5
            r21 = r6
            r24 = r11
            r25 = r18
            r6 = r26
            r11 = r0
            r18 = r4
            r44 = r22
            r22 = r10
            r10 = r44
        L_0x051f:
            if (r23 == 0) goto L_0x0522
            return
        L_0x0522:
            if (r32 == 0) goto L_0x05ae
            java.lang.String r0 = "Processes running summed over"
            r15.print(r0)
            if (r25 != 0) goto L_0x053c
            r0 = 0
        L_0x052c:
            int r1 = r10.length
            if (r0 >= r1) goto L_0x053c
            java.lang.String r1 = " "
            r15.print(r1)
            r1 = r10[r0]
            com.android.internal.app.procstats.DumpUtils.printScreenLabelCsv(r15, r1)
            int r0 = r0 + 1
            goto L_0x052c
        L_0x053c:
            if (r27 != 0) goto L_0x054f
            r0 = 0
        L_0x053f:
            int r1 = r11.length
            if (r0 >= r1) goto L_0x054f
            java.lang.String r1 = " "
            r15.print(r1)
            r1 = r11[r0]
            com.android.internal.app.procstats.DumpUtils.printMemLabelCsv(r15, r1)
            int r0 = r0 + 1
            goto L_0x053f
        L_0x054f:
            if (r28 != 0) goto L_0x0566
            r0 = 0
        L_0x0552:
            int r1 = r9.length
            if (r0 >= r1) goto L_0x0566
            java.lang.String r1 = " "
            r15.print(r1)
            java.lang.String[] r1 = com.android.internal.app.procstats.DumpUtils.STATE_NAMES_CSV
            r2 = r9[r0]
            r1 = r1[r2]
            r15.print(r1)
            int r0 = r0 + 1
            goto L_0x0552
        L_0x0566:
            r46.println()
            com.android.server.am.ActivityManagerService r5 = r14.mAm
            monitor-enter(r5)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0598 }
            r3 = 0
            r1 = r45
            r2 = r46
            r4 = r25
            r26 = r5
            r5 = r10
            r34 = r6
            r6 = r27
            r35 = r7
            r7 = r11
            r36 = r8
            r8 = r28
            r37 = r9
            r38 = r10
            r39 = r11
            r10 = r16
            r40 = r12
            r1.dumpFilteredProcessesCsvLocked(r2, r3, r4, r5, r6, r7, r8, r9, r10, r12)     // Catch:{ all -> 0x0596 }
            monitor-exit(r26)     // Catch:{ all -> 0x0596 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0596:
            r0 = move-exception
            goto L_0x05a9
        L_0x0598:
            r0 = move-exception
            r26 = r5
            r34 = r6
            r35 = r7
            r36 = r8
            r37 = r9
            r38 = r10
            r39 = r11
            r40 = r12
        L_0x05a9:
            monitor-exit(r26)     // Catch:{ all -> 0x0596 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x05ae:
            r34 = r6
            r35 = r7
            r36 = r8
            r37 = r9
            r38 = r10
            r39 = r11
            r40 = r12
            r12 = r36
            if (r12 == 0) goto L_0x05e8
            java.lang.String r0 = "AGGREGATED OVER LAST "
            r15.print(r0)
            r15.print(r12)
            java.lang.String r0 = " HOURS:"
            r15.println(r0)
            long r3 = (long) r12
            r1 = r45
            r2 = r46
            r5 = r16
            r7 = r40
            r8 = r30
            r9 = r18
            r10 = r20
            r11 = r21
            r26 = r12
            r12 = r24
            r13 = r34
            r1.dumpAggregatedStats(r2, r3, r5, r7, r8, r9, r10, r11, r12, r13)
            return
        L_0x05e8:
            r26 = r12
            r13 = r35
            if (r13 <= 0) goto L_0x06db
            java.lang.String r0 = "LAST STATS AT INDEX "
            r15.print(r0)
            r15.print(r13)
            java.lang.String r0 = ":"
            r15.println(r0)
            r1 = 0
            r8 = 1
            java.util.ArrayList r0 = r14.getCommittedFiles(r1, r1, r8)
            int r1 = r0.size()
            if (r13 < r1) goto L_0x0619
            java.lang.String r1 = "Only have "
            r15.print(r1)
            int r1 = r0.size()
            r15.print(r1)
            java.lang.String r1 = " data sets"
            r15.println(r1)
            return
        L_0x0619:
            android.util.AtomicFile r1 = new android.util.AtomicFile
            java.io.File r2 = new java.io.File
            java.lang.Object r3 = r0.get(r13)
            java.lang.String r3 = (java.lang.String) r3
            r2.<init>(r3)
            r1.<init>(r2)
            r11 = r1
            com.android.internal.app.procstats.ProcessStats r1 = new com.android.internal.app.procstats.ProcessStats
            r2 = 0
            r1.<init>(r2)
            r12 = r1
            r14.readLocked(r12, r11)
            java.lang.String r1 = r12.mReadError
            if (r1 == 0) goto L_0x065a
            if (r19 != 0) goto L_0x063c
            if (r30 == 0) goto L_0x0641
        L_0x063c:
            java.lang.String r1 = "err,"
            r15.print(r1)
        L_0x0641:
            java.lang.String r1 = "Failure reading "
            r15.print(r1)
            java.lang.Object r1 = r0.get(r13)
            java.lang.String r1 = (java.lang.String) r1
            r15.print(r1)
            java.lang.String r1 = "; "
            r15.print(r1)
            java.lang.String r1 = r12.mReadError
            r15.println(r1)
            return
        L_0x065a:
            java.io.File r1 = r11.getBaseFile()
            java.lang.String r10 = r1.getPath()
            java.lang.String r1 = ".ci"
            boolean r31 = r10.endsWith(r1)
            if (r19 != 0) goto L_0x06d1
            if (r30 == 0) goto L_0x0670
            r29 = r10
            goto L_0x06d3
        L_0x0670:
            java.lang.String r1 = "COMMITTED STATS FROM "
            r15.print(r1)
            java.lang.String r1 = r12.mTimePeriodStartClockStr
            r15.print(r1)
            if (r31 == 0) goto L_0x0681
            java.lang.String r1 = " (checked in)"
            r15.print(r1)
        L_0x0681:
            java.lang.String r1 = ":"
            r15.println(r1)
            if (r18 != 0) goto L_0x069e
            if (r20 == 0) goto L_0x068b
            goto L_0x069e
        L_0x068b:
            r1 = r12
            r2 = r46
            r3 = r40
            r4 = r16
            r6 = r24
            r1.dumpSummaryLocked(r2, r3, r4, r6)
            r29 = r10
            r9 = r34
            r10 = r40
            goto L_0x06da
        L_0x069e:
            if (r20 != 0) goto L_0x06a2
            r6 = r8
            goto L_0x06a3
        L_0x06a2:
            r6 = 0
        L_0x06a3:
            r1 = r12
            r2 = r46
            r3 = r40
            r4 = r16
            r7 = r18
            r8 = r21
            r9 = r24
            r29 = r10
            r10 = r34
            r1.dumpLocked(r2, r3, r4, r6, r7, r8, r9, r10)
            if (r21 == 0) goto L_0x06cc
            java.lang.String r1 = "  mFile="
            r15.print(r1)
            android.util.AtomicFile r1 = r14.mFile
            java.io.File r1 = r1.getBaseFile()
            r15.println(r1)
            r9 = r34
            r10 = r40
            goto L_0x06da
        L_0x06cc:
            r9 = r34
            r10 = r40
            goto L_0x06da
        L_0x06d1:
            r29 = r10
        L_0x06d3:
            r9 = r34
            r10 = r40
            r12.dumpCheckinLocked(r15, r10, r9)
        L_0x06da:
            return
        L_0x06db:
            r9 = r34
            r10 = r40
            r8 = 1
            r1 = 0
            if (r21 != 0) goto L_0x06ed
            if (r19 == 0) goto L_0x06e6
            goto L_0x06ed
        L_0x06e6:
            r11 = r1
            r35 = r8
            r34 = 0
            goto L_0x08aa
        L_0x06ed:
            java.util.concurrent.locks.ReentrantLock r0 = r14.mWriteLock
            r0.lock()
            if (r19 != 0) goto L_0x06f6
            r0 = r8
            goto L_0x06f7
        L_0x06f6:
            r0 = 0
        L_0x06f7:
            r2 = 0
            java.util.ArrayList r0 = r14.getCommittedFiles(r2, r2, r0)     // Catch:{ all -> 0x097c }
            r11 = r0
            if (r11 == 0) goto L_0x08a0
            if (r19 == 0) goto L_0x0703
            r7 = 0
            goto L_0x0709
        L_0x0703:
            int r0 = r11.size()     // Catch:{ all -> 0x0897 }
            int r7 = r0 - r22
        L_0x0709:
            r0 = r7
            if (r0 >= 0) goto L_0x070f
            r0 = 0
            r12 = r0
            goto L_0x0710
        L_0x070f:
            r12 = r0
        L_0x0710:
            r0 = r12
            r7 = r0
        L_0x0712:
            int r0 = r11.size()     // Catch:{ all -> 0x0897 }
            if (r7 >= r0) goto L_0x0891
            android.util.AtomicFile r0 = new android.util.AtomicFile     // Catch:{ all -> 0x0871 }
            java.io.File r2 = new java.io.File     // Catch:{ all -> 0x0871 }
            java.lang.Object r3 = r11.get(r7)     // Catch:{ all -> 0x0871 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x0871 }
            r2.<init>(r3)     // Catch:{ all -> 0x0871 }
            r0.<init>(r2)     // Catch:{ all -> 0x0871 }
            com.android.internal.app.procstats.ProcessStats r2 = new com.android.internal.app.procstats.ProcessStats     // Catch:{ all -> 0x0871 }
            r6 = 0
            r2.<init>(r6)     // Catch:{ all -> 0x0869 }
            r4 = r2
            r14.readLocked(r4, r0)     // Catch:{ all -> 0x0869 }
            java.lang.String r2 = r4.mReadError     // Catch:{ all -> 0x0869 }
            if (r2 == 0) goto L_0x0775
            if (r19 != 0) goto L_0x073a
            if (r30 == 0) goto L_0x073f
        L_0x073a:
            java.lang.String r2 = "err,"
            r15.print(r2)     // Catch:{ all -> 0x076c }
        L_0x073f:
            java.lang.String r2 = "Failure reading "
            r15.print(r2)     // Catch:{ all -> 0x076c }
            java.lang.Object r2 = r11.get(r7)     // Catch:{ all -> 0x076c }
            java.lang.String r2 = (java.lang.String) r2     // Catch:{ all -> 0x076c }
            r15.print(r2)     // Catch:{ all -> 0x076c }
            java.lang.String r2 = "; "
            r15.print(r2)     // Catch:{ all -> 0x076c }
            java.lang.String r2 = r4.mReadError     // Catch:{ all -> 0x076c }
            r15.println(r2)     // Catch:{ all -> 0x076c }
            java.io.File r2 = new java.io.File     // Catch:{ all -> 0x076c }
            java.lang.Object r3 = r11.get(r7)     // Catch:{ all -> 0x076c }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x076c }
            r2.<init>(r3)     // Catch:{ all -> 0x076c }
            r2.delete()     // Catch:{ all -> 0x076c }
            r34 = r6
            r2 = r7
            r35 = r8
            goto L_0x088b
        L_0x076c:
            r0 = move-exception
            r34 = r6
            r43 = r7
            r35 = r8
            goto L_0x0878
        L_0x0775:
            java.io.File r2 = r0.getBaseFile()     // Catch:{ all -> 0x0869 }
            java.lang.String r2 = r2.getPath()     // Catch:{ all -> 0x0869 }
            r5 = r2
            java.lang.String r2 = ".ci"
            boolean r2 = r5.endsWith(r2)     // Catch:{ all -> 0x0869 }
            r29 = r2
            if (r19 != 0) goto L_0x0829
            if (r30 == 0) goto L_0x079a
            r40 = r4
            r41 = r5
            r34 = r6
            r43 = r7
            r35 = r8
            r36 = r9
            r42 = r10
            goto L_0x0837
        L_0x079a:
            if (r1 == 0) goto L_0x07a2
            r46.println()     // Catch:{ all -> 0x076c }
            r31 = r1
            goto L_0x07a5
        L_0x07a2:
            r1 = 1
            r31 = r1
        L_0x07a5:
            java.lang.String r1 = "COMMITTED STATS FROM "
            r15.print(r1)     // Catch:{ all -> 0x081f }
            java.lang.String r1 = r4.mTimePeriodStartClockStr     // Catch:{ all -> 0x081f }
            r15.print(r1)     // Catch:{ all -> 0x081f }
            if (r29 == 0) goto L_0x07c2
            java.lang.String r1 = " (checked in)"
            r15.print(r1)     // Catch:{ all -> 0x07b7 }
            goto L_0x07c2
        L_0x07b7:
            r0 = move-exception
            r34 = r6
            r43 = r7
            r35 = r8
            r1 = r31
            goto L_0x0878
        L_0x07c2:
            java.lang.String r1 = ":"
            r15.println(r1)     // Catch:{ all -> 0x081f }
            if (r20 == 0) goto L_0x07f3
            r34 = 0
            r35 = 0
            r36 = 0
            r1 = r4
            r2 = r46
            r3 = r10
            r40 = r4
            r41 = r5
            r4 = r16
            r42 = r6
            r6 = r34
            r43 = r7
            r34 = r42
            r7 = r35
            r35 = r8
            r8 = r36
            r36 = r9
            r9 = r24
            r42 = r10
            r10 = r36
            r1.dumpLocked(r2, r3, r4, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x0817 }
            goto L_0x080e
        L_0x07f3:
            r40 = r4
            r41 = r5
            r34 = r6
            r43 = r7
            r35 = r8
            r36 = r9
            r42 = r10
            r1 = r40
            r2 = r46
            r3 = r42
            r4 = r16
            r6 = r24
            r1.dumpSummaryLocked(r2, r3, r4, r6)     // Catch:{ all -> 0x0817 }
        L_0x080e:
            r1 = r31
            r9 = r36
            r2 = r40
            r10 = r42
            goto L_0x0840
        L_0x0817:
            r0 = move-exception
            r1 = r31
            r9 = r36
            r10 = r42
            goto L_0x0878
        L_0x081f:
            r0 = move-exception
            r34 = r6
            r43 = r7
            r35 = r8
            r1 = r31
            goto L_0x0878
        L_0x0829:
            r40 = r4
            r41 = r5
            r34 = r6
            r43 = r7
            r35 = r8
            r36 = r9
            r42 = r10
        L_0x0837:
            r9 = r36
            r2 = r40
            r10 = r42
            r2.dumpCheckinLocked(r15, r10, r9)     // Catch:{ all -> 0x0867 }
        L_0x0840:
            if (r19 == 0) goto L_0x0862
            java.io.File r3 = r0.getBaseFile()     // Catch:{ all -> 0x0867 }
            java.io.File r4 = new java.io.File     // Catch:{ all -> 0x0867 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x0867 }
            r5.<init>()     // Catch:{ all -> 0x0867 }
            r6 = r41
            r5.append(r6)     // Catch:{ all -> 0x0867 }
            java.lang.String r7 = ".ci"
            r5.append(r7)     // Catch:{ all -> 0x0867 }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x0867 }
            r4.<init>(r5)     // Catch:{ all -> 0x0867 }
            r3.renameTo(r4)     // Catch:{ all -> 0x0867 }
            goto L_0x0864
        L_0x0862:
            r6 = r41
        L_0x0864:
            r2 = r43
            goto L_0x088b
        L_0x0867:
            r0 = move-exception
            goto L_0x0878
        L_0x0869:
            r0 = move-exception
            r34 = r6
            r43 = r7
            r35 = r8
            goto L_0x0878
        L_0x0871:
            r0 = move-exception
            r43 = r7
            r35 = r8
            r34 = 0
        L_0x0878:
            java.lang.String r2 = "**** FAILURE DUMPING STATE: "
            r15.print(r2)     // Catch:{ all -> 0x0897 }
            r2 = r43
            java.lang.Object r3 = r11.get(r2)     // Catch:{ all -> 0x0897 }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ all -> 0x0897 }
            r15.println(r3)     // Catch:{ all -> 0x0897 }
            r0.printStackTrace(r15)     // Catch:{ all -> 0x0897 }
        L_0x088b:
            int r7 = r2 + 1
            r8 = r35
            goto L_0x0712
        L_0x0891:
            r2 = r7
            r35 = r8
            r34 = 0
            goto L_0x08a4
        L_0x0897:
            r0 = move-exception
            r29 = r9
            r31 = r10
            r35 = r13
            goto L_0x0983
        L_0x08a0:
            r35 = r8
            r34 = 0
        L_0x08a4:
            java.util.concurrent.locks.ReentrantLock r0 = r14.mWriteLock
            r0.unlock()
            r11 = r1
        L_0x08aa:
            if (r19 != 0) goto L_0x0973
            com.android.server.am.ActivityManagerService r12 = r14.mAm
            monitor-enter(r12)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0965 }
            if (r30 == 0) goto L_0x08ca
            com.android.internal.app.procstats.ProcessStats r0 = r14.mProcessStats     // Catch:{ all -> 0x08c1 }
            r0.dumpCheckinLocked(r15, r10, r9)     // Catch:{ all -> 0x08c1 }
            r29 = r9
            r31 = r10
            r34 = r11
            goto L_0x091f
        L_0x08c1:
            r0 = move-exception
            r29 = r9
            r31 = r10
            r35 = r13
            goto L_0x096c
        L_0x08ca:
            if (r11 == 0) goto L_0x08cf
            r46.println()     // Catch:{ all -> 0x08c1 }
        L_0x08cf:
            java.lang.String r0 = "CURRENT STATS:"
            r15.println(r0)     // Catch:{ all -> 0x0965 }
            if (r18 != 0) goto L_0x08ea
            if (r20 == 0) goto L_0x08d9
            goto L_0x08ea
        L_0x08d9:
            com.android.internal.app.procstats.ProcessStats r1 = r14.mProcessStats     // Catch:{ all -> 0x08c1 }
            r2 = r46
            r3 = r10
            r4 = r16
            r6 = r24
            r1.dumpSummaryLocked(r2, r3, r4, r6)     // Catch:{ all -> 0x08c1 }
            r29 = r9
            r31 = r10
            goto L_0x091c
        L_0x08ea:
            com.android.internal.app.procstats.ProcessStats r1 = r14.mProcessStats     // Catch:{ all -> 0x0965 }
            if (r20 != 0) goto L_0x08f1
            r6 = r35
            goto L_0x08f3
        L_0x08f1:
            r6 = r34
        L_0x08f3:
            r2 = r46
            r3 = r10
            r4 = r16
            r7 = r18
            r8 = r21
            r29 = r9
            r9 = r24
            r31 = r10
            r10 = r29
            r1.dumpLocked(r2, r3, r4, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x0961 }
            if (r21 == 0) goto L_0x091c
            java.lang.String r0 = "  mFile="
            r15.print(r0)     // Catch:{ all -> 0x0918 }
            android.util.AtomicFile r0 = r14.mFile     // Catch:{ all -> 0x0918 }
            java.io.File r0 = r0.getBaseFile()     // Catch:{ all -> 0x0918 }
            r15.println(r0)     // Catch:{ all -> 0x0918 }
            goto L_0x091c
        L_0x0918:
            r0 = move-exception
            r35 = r13
            goto L_0x096c
        L_0x091c:
            r11 = 1
            r34 = r11
        L_0x091f:
            monitor-exit(r12)     // Catch:{ all -> 0x095b }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            if (r33 != 0) goto L_0x0958
            if (r34 == 0) goto L_0x092a
            r46.println()
        L_0x092a:
            java.lang.String r0 = "AGGREGATED OVER LAST 24 HOURS:"
            r15.println(r0)
            r3 = 24
            r1 = r45
            r2 = r46
            r5 = r16
            r7 = r31
            r8 = r30
            r9 = r18
            r10 = r20
            r11 = r21
            r12 = r24
            r35 = r13
            r13 = r29
            r1.dumpAggregatedStats(r2, r3, r5, r7, r8, r9, r10, r11, r12, r13)
            r46.println()
            java.lang.String r0 = "AGGREGATED OVER LAST 3 HOURS:"
            r15.println(r0)
            r3 = 3
            r1.dumpAggregatedStats(r2, r3, r5, r7, r8, r9, r10, r11, r12, r13)
            goto L_0x097b
        L_0x0958:
            r35 = r13
            goto L_0x097b
        L_0x095b:
            r0 = move-exception
            r35 = r13
            r11 = r34
            goto L_0x096c
        L_0x0961:
            r0 = move-exception
            r35 = r13
            goto L_0x096c
        L_0x0965:
            r0 = move-exception
            r29 = r9
            r31 = r10
            r35 = r13
        L_0x096c:
            monitor-exit(r12)     // Catch:{ all -> 0x0971 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x0971:
            r0 = move-exception
            goto L_0x096c
        L_0x0973:
            r29 = r9
            r31 = r10
            r35 = r13
            r34 = r11
        L_0x097b:
            return
        L_0x097c:
            r0 = move-exception
            r29 = r9
            r31 = r10
            r35 = r13
        L_0x0983:
            java.util.concurrent.locks.ReentrantLock r2 = r14.mWriteLock
            r2.unlock()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ProcessStatsService.dumpInner(java.io.PrintWriter, java.lang.String[]):void");
    }

    private void dumpAggregatedStats(ProtoOutputStream proto, long fieldId, int aggregateHours, long now) {
        ParcelFileDescriptor pfd = getStatsOverTime(((long) (((aggregateHours * 60) * 60) * 1000)) - (ProcessStats.COMMIT_PERIOD / 2));
        if (pfd != null) {
            ProcessStats stats = new ProcessStats(false);
            stats.read(new ParcelFileDescriptor.AutoCloseInputStream(pfd));
            if (stats.mReadError == null) {
                long token = proto.start(fieldId);
                stats.writeToProto(proto, now, 15);
                proto.end(token);
            }
        }
    }

    private void dumpProto(FileDescriptor fd) {
        long now;
        ProtoOutputStream proto = new ProtoOutputStream(fd);
        synchronized (this.mAm) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                now = SystemClock.uptimeMillis();
                long token = proto.start(1146756268033L);
                this.mProcessStats.writeToProto(proto, now, 15);
                proto.end(token);
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        ProtoOutputStream protoOutputStream = proto;
        long j = now;
        dumpAggregatedStats(protoOutputStream, 1146756268034L, 3, j);
        dumpAggregatedStats(protoOutputStream, 1146756268035L, 24, j);
        proto.flush();
    }
}
