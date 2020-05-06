package com.android.server.job;

import android.app.job.JobParameters;
import android.os.UserHandle;
import android.text.format.DateFormat;
import android.util.ArrayMap;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import com.android.internal.util.RingBufferIndices;
import com.android.server.job.controllers.JobStatus;
import com.android.server.slice.SliceClientPermissions;
import java.io.PrintWriter;

public final class JobPackageTracker {
    static final long BATCHING_TIME = 1800000;
    private static final int EVENT_BUFFER_SIZE = 100;
    public static final int EVENT_CMD_MASK = 255;
    public static final int EVENT_NULL = 0;
    public static final int EVENT_START_JOB = 1;
    public static final int EVENT_START_PERIODIC_JOB = 3;
    public static final int EVENT_STOP_JOB = 2;
    public static final int EVENT_STOP_PERIODIC_JOB = 4;
    public static final int EVENT_STOP_REASON_MASK = 65280;
    public static final int EVENT_STOP_REASON_SHIFT = 8;
    static final int NUM_HISTORY = 5;
    DataSet mCurDataSet = new DataSet();
    private final int[] mEventCmds = new int[100];
    private final RingBufferIndices mEventIndices = new RingBufferIndices(100);
    private final int[] mEventJobIds = new int[100];
    private final String[] mEventReasons = new String[100];
    private final String[] mEventTags = new String[100];
    private final long[] mEventTimes = new long[100];
    private final int[] mEventUids = new int[100];
    DataSet[] mLastDataSets = new DataSet[5];

    public void addEvent(int cmd, int uid, String tag, int jobId, int stopReason, String debugReason) {
        int index = this.mEventIndices.add();
        this.mEventCmds[index] = ((stopReason << 8) & EVENT_STOP_REASON_MASK) | cmd;
        this.mEventTimes[index] = JobSchedulerService.sElapsedRealtimeClock.millis();
        this.mEventUids[index] = uid;
        this.mEventTags[index] = tag;
        this.mEventJobIds[index] = jobId;
        this.mEventReasons[index] = debugReason;
    }

    static final class PackageEntry {
        int activeCount;
        int activeNesting;
        long activeStartTime;
        int activeTopCount;
        int activeTopNesting;
        long activeTopStartTime;
        boolean hadActive;
        boolean hadActiveTop;
        boolean hadPending;
        long pastActiveTime;
        long pastActiveTopTime;
        long pastPendingTime;
        int pendingCount;
        int pendingNesting;
        long pendingStartTime;
        final SparseIntArray stopReasons = new SparseIntArray();

        PackageEntry() {
        }

        public long getActiveTime(long now) {
            long time = this.pastActiveTime;
            if (this.activeNesting > 0) {
                return time + (now - this.activeStartTime);
            }
            return time;
        }

        public long getActiveTopTime(long now) {
            long time = this.pastActiveTopTime;
            if (this.activeTopNesting > 0) {
                return time + (now - this.activeTopStartTime);
            }
            return time;
        }

        public long getPendingTime(long now) {
            long time = this.pastPendingTime;
            if (this.pendingNesting > 0) {
                return time + (now - this.pendingStartTime);
            }
            return time;
        }
    }

    static final class DataSet {
        final SparseArray<ArrayMap<String, PackageEntry>> mEntries;
        int mMaxFgActive;
        int mMaxTotalActive;
        final long mStartClockTime;
        final long mStartElapsedTime;
        final long mStartUptimeTime;
        long mSummedTime;

        public DataSet(DataSet otherTimes) {
            this.mEntries = new SparseArray<>();
            this.mStartUptimeTime = otherTimes.mStartUptimeTime;
            this.mStartElapsedTime = otherTimes.mStartElapsedTime;
            this.mStartClockTime = otherTimes.mStartClockTime;
        }

        public DataSet() {
            this.mEntries = new SparseArray<>();
            this.mStartUptimeTime = JobSchedulerService.sUptimeMillisClock.millis();
            this.mStartElapsedTime = JobSchedulerService.sElapsedRealtimeClock.millis();
            this.mStartClockTime = JobSchedulerService.sSystemClock.millis();
        }

        private PackageEntry getOrCreateEntry(int uid, String pkg) {
            ArrayMap<String, PackageEntry> uidMap = this.mEntries.get(uid);
            if (uidMap == null) {
                uidMap = new ArrayMap<>();
                this.mEntries.put(uid, uidMap);
            }
            PackageEntry entry = uidMap.get(pkg);
            if (entry != null) {
                return entry;
            }
            PackageEntry entry2 = new PackageEntry();
            uidMap.put(pkg, entry2);
            return entry2;
        }

        public PackageEntry getEntry(int uid, String pkg) {
            ArrayMap<String, PackageEntry> uidMap = this.mEntries.get(uid);
            if (uidMap == null) {
                return null;
            }
            return uidMap.get(pkg);
        }

        /* access modifiers changed from: package-private */
        public long getTotalTime(long now) {
            long j = this.mSummedTime;
            if (j > 0) {
                return j;
            }
            return now - this.mStartUptimeTime;
        }

        /* access modifiers changed from: package-private */
        public void incPending(int uid, String pkg, long now) {
            PackageEntry pe = getOrCreateEntry(uid, pkg);
            if (pe.pendingNesting == 0) {
                pe.pendingStartTime = now;
                pe.pendingCount++;
            }
            pe.pendingNesting++;
        }

        /* access modifiers changed from: package-private */
        public void decPending(int uid, String pkg, long now) {
            PackageEntry pe = getOrCreateEntry(uid, pkg);
            if (pe.pendingNesting == 1) {
                pe.pastPendingTime += now - pe.pendingStartTime;
            }
            pe.pendingNesting--;
        }

        /* access modifiers changed from: package-private */
        public void incActive(int uid, String pkg, long now) {
            PackageEntry pe = getOrCreateEntry(uid, pkg);
            if (pe.activeNesting == 0) {
                pe.activeStartTime = now;
                pe.activeCount++;
            }
            pe.activeNesting++;
        }

        /* access modifiers changed from: package-private */
        public void decActive(int uid, String pkg, long now, int stopReason) {
            PackageEntry pe = getOrCreateEntry(uid, pkg);
            if (pe.activeNesting == 1) {
                pe.pastActiveTime += now - pe.activeStartTime;
            }
            pe.activeNesting--;
            pe.stopReasons.put(stopReason, pe.stopReasons.get(stopReason, 0) + 1);
        }

        /* access modifiers changed from: package-private */
        public void incActiveTop(int uid, String pkg, long now) {
            PackageEntry pe = getOrCreateEntry(uid, pkg);
            if (pe.activeTopNesting == 0) {
                pe.activeTopStartTime = now;
                pe.activeTopCount++;
            }
            pe.activeTopNesting++;
        }

        /* access modifiers changed from: package-private */
        public void decActiveTop(int uid, String pkg, long now, int stopReason) {
            PackageEntry pe = getOrCreateEntry(uid, pkg);
            if (pe.activeTopNesting == 1) {
                pe.pastActiveTopTime += now - pe.activeTopStartTime;
            }
            pe.activeTopNesting--;
            pe.stopReasons.put(stopReason, pe.stopReasons.get(stopReason, 0) + 1);
        }

        /* access modifiers changed from: package-private */
        public void finish(DataSet next, long now) {
            for (int i = this.mEntries.size() - 1; i >= 0; i--) {
                ArrayMap<String, PackageEntry> uidMap = this.mEntries.valueAt(i);
                for (int j = uidMap.size() - 1; j >= 0; j--) {
                    PackageEntry pe = uidMap.valueAt(j);
                    if (pe.activeNesting > 0 || pe.activeTopNesting > 0 || pe.pendingNesting > 0) {
                        PackageEntry nextPe = next.getOrCreateEntry(this.mEntries.keyAt(i), uidMap.keyAt(j));
                        nextPe.activeStartTime = now;
                        nextPe.activeNesting = pe.activeNesting;
                        nextPe.activeTopStartTime = now;
                        nextPe.activeTopNesting = pe.activeTopNesting;
                        nextPe.pendingStartTime = now;
                        nextPe.pendingNesting = pe.pendingNesting;
                        if (pe.activeNesting > 0) {
                            pe.pastActiveTime += now - pe.activeStartTime;
                            pe.activeNesting = 0;
                        }
                        if (pe.activeTopNesting > 0) {
                            pe.pastActiveTopTime += now - pe.activeTopStartTime;
                            pe.activeTopNesting = 0;
                        }
                        if (pe.pendingNesting > 0) {
                            pe.pastPendingTime += now - pe.pendingStartTime;
                            pe.pendingNesting = 0;
                        }
                    }
                }
            }
        }

        /* access modifiers changed from: package-private */
        public void addTo(DataSet out, long now) {
            out.mSummedTime += getTotalTime(now);
            for (int i = this.mEntries.size() - 1; i >= 0; i--) {
                ArrayMap<String, PackageEntry> uidMap = this.mEntries.valueAt(i);
                for (int j = uidMap.size() - 1; j >= 0; j--) {
                    PackageEntry pe = uidMap.valueAt(j);
                    PackageEntry outPe = out.getOrCreateEntry(this.mEntries.keyAt(i), uidMap.keyAt(j));
                    outPe.pastActiveTime += pe.pastActiveTime;
                    outPe.activeCount += pe.activeCount;
                    outPe.pastActiveTopTime += pe.pastActiveTopTime;
                    outPe.activeTopCount += pe.activeTopCount;
                    outPe.pastPendingTime += pe.pastPendingTime;
                    outPe.pendingCount += pe.pendingCount;
                    if (pe.activeNesting > 0) {
                        outPe.pastActiveTime += now - pe.activeStartTime;
                        outPe.hadActive = true;
                    }
                    if (pe.activeTopNesting > 0) {
                        outPe.pastActiveTopTime += now - pe.activeTopStartTime;
                        outPe.hadActiveTop = true;
                    }
                    if (pe.pendingNesting > 0) {
                        outPe.pastPendingTime += now - pe.pendingStartTime;
                        outPe.hadPending = true;
                    }
                    for (int k = pe.stopReasons.size() - 1; k >= 0; k--) {
                        int type = pe.stopReasons.keyAt(k);
                        outPe.stopReasons.put(type, outPe.stopReasons.get(type, 0) + pe.stopReasons.valueAt(k));
                    }
                }
            }
            int i2 = this.mMaxTotalActive;
            if (i2 > out.mMaxTotalActive) {
                out.mMaxTotalActive = i2;
            }
            int i3 = this.mMaxFgActive;
            if (i3 > out.mMaxFgActive) {
                out.mMaxFgActive = i3;
            }
        }

        /* access modifiers changed from: package-private */
        public void printDuration(PrintWriter pw, long period, long duration, int count, String suffix) {
            int percent = (int) ((100.0f * (((float) duration) / ((float) period))) + 0.5f);
            if (percent > 0) {
                pw.print(" ");
                pw.print(percent);
                pw.print("% ");
                pw.print(count);
                pw.print("x ");
                pw.print(suffix);
            } else if (count > 0) {
                pw.print(" ");
                pw.print(count);
                pw.print("x ");
                pw.print(suffix);
            }
        }

        /* access modifiers changed from: package-private */
        public void dump(PrintWriter pw, String header, String prefix, long now, long nowElapsed, int filterUid) {
            int i;
            DataSet dataSet = this;
            PrintWriter printWriter = pw;
            String str = prefix;
            long j = now;
            int i2 = filterUid;
            long period = dataSet.getTotalTime(j);
            printWriter.print(str);
            pw.print(header);
            printWriter.print(" at ");
            printWriter.print(DateFormat.format("yyyy-MM-dd-HH-mm-ss", dataSet.mStartClockTime).toString());
            printWriter.print(" (");
            TimeUtils.formatDuration(dataSet.mStartElapsedTime, nowElapsed, printWriter);
            printWriter.print(") over ");
            TimeUtils.formatDuration(period, printWriter);
            String str2 = ":";
            printWriter.println(str2);
            int NE = dataSet.mEntries.size();
            int i3 = 0;
            while (i3 < NE) {
                int uid = dataSet.mEntries.keyAt(i3);
                if (i2 == -1 || i2 == UserHandle.getAppId(uid)) {
                    ArrayMap<String, PackageEntry> uidMap = dataSet.mEntries.valueAt(i3);
                    int NP = uidMap.size();
                    int j2 = 0;
                    while (j2 < NP) {
                        PackageEntry pe = uidMap.valueAt(j2);
                        printWriter.print(str);
                        int NP2 = NP;
                        printWriter.print("  ");
                        UserHandle.formatUid(printWriter, uid);
                        printWriter.print(" / ");
                        printWriter.print(uidMap.keyAt(j2));
                        printWriter.println(str2);
                        printWriter.print(str);
                        printWriter.print("   ");
                        ArrayMap<String, PackageEntry> uidMap2 = uidMap;
                        PrintWriter printWriter2 = pw;
                        int i4 = i3;
                        int uid2 = uid;
                        long j3 = period;
                        String str3 = str2;
                        int NE2 = NE;
                        printDuration(printWriter2, j3, pe.getPendingTime(j), pe.pendingCount, "pending");
                        printDuration(printWriter2, j3, pe.getActiveTime(j), pe.activeCount, "active");
                        printDuration(printWriter2, j3, pe.getActiveTopTime(j), pe.activeTopCount, "active-top");
                        if (pe.pendingNesting > 0 || pe.hadPending) {
                            printWriter.print(" (pending)");
                        }
                        if (pe.activeNesting > 0 || pe.hadActive) {
                            printWriter.print(" (active)");
                        }
                        if (pe.activeTopNesting > 0 || pe.hadActiveTop) {
                            printWriter.print(" (active-top)");
                        }
                        pw.println();
                        if (pe.stopReasons.size() > 0) {
                            printWriter.print(str);
                            printWriter.print("    ");
                            for (int k = 0; k < pe.stopReasons.size(); k++) {
                                if (k > 0) {
                                    printWriter.print(", ");
                                }
                                printWriter.print(pe.stopReasons.valueAt(k));
                                printWriter.print("x ");
                                printWriter.print(JobParameters.getReasonName(pe.stopReasons.keyAt(k)));
                            }
                            pw.println();
                        }
                        j2++;
                        long j4 = nowElapsed;
                        NP = NP2;
                        uidMap = uidMap2;
                        i3 = i4;
                        uid = uid2;
                        NE = NE2;
                        str2 = str3;
                    }
                    ArrayMap<String, PackageEntry> arrayMap = uidMap;
                    i = i3;
                    int i5 = uid;
                } else {
                    i = i3;
                }
                i3 = i + 1;
                dataSet = this;
                long j5 = nowElapsed;
                i2 = filterUid;
                NE = NE;
                str2 = str2;
            }
            printWriter.print(str);
            printWriter.print("  Max concurrency: ");
            printWriter.print(this.mMaxTotalActive);
            printWriter.print(" total, ");
            printWriter.print(this.mMaxFgActive);
            printWriter.println(" foreground");
        }

        private void printPackageEntryState(ProtoOutputStream proto, long fieldId, long duration, int count) {
            long token = proto.start(fieldId);
            proto.write(1112396529665L, duration);
            proto.write(1120986464258L, count);
            proto.end(token);
        }

        /* access modifiers changed from: package-private */
        public void dump(ProtoOutputStream proto, long fieldId, long now, long nowElapsed, int filterUid) {
            int i;
            ProtoOutputStream protoOutputStream = proto;
            long j = now;
            int i2 = filterUid;
            long token = proto.start(fieldId);
            long period = getTotalTime(j);
            protoOutputStream.write(1112396529665L, this.mStartClockTime);
            protoOutputStream.write(1112396529666L, nowElapsed - this.mStartElapsedTime);
            protoOutputStream.write(1112396529667L, period);
            int NE = this.mEntries.size();
            int i3 = 0;
            while (i3 < NE) {
                int uid = this.mEntries.keyAt(i3);
                if (i2 == -1 || i2 == UserHandle.getAppId(uid)) {
                    ArrayMap<String, PackageEntry> uidMap = this.mEntries.valueAt(i3);
                    int NP = uidMap.size();
                    int j2 = 0;
                    while (j2 < NP) {
                        int NP2 = NP;
                        int i4 = i3;
                        long peToken = protoOutputStream.start(2246267895812L);
                        PackageEntry pe = uidMap.valueAt(j2);
                        protoOutputStream.write(1120986464257L, uid);
                        int j3 = j2;
                        protoOutputStream.write(1138166333442L, uidMap.keyAt(j2));
                        long pendingTime = pe.getPendingTime(j);
                        int j4 = pe.pendingCount;
                        PackageEntry pe2 = pe;
                        int j5 = j3;
                        long period2 = period;
                        long peToken2 = peToken;
                        int NP3 = NP2;
                        ArrayMap<String, PackageEntry> uidMap2 = uidMap;
                        int uid2 = uid;
                        long j6 = pendingTime;
                        int NE2 = NE;
                        printPackageEntryState(proto, 1146756268035L, j6, j4);
                        PackageEntry pe3 = pe2;
                        ProtoOutputStream protoOutputStream2 = proto;
                        PackageEntry pe4 = pe3;
                        printPackageEntryState(protoOutputStream2, 1146756268036L, pe3.getActiveTime(j), pe3.activeCount);
                        printPackageEntryState(protoOutputStream2, 1146756268037L, pe4.getActiveTopTime(j), pe4.activeTopCount);
                        boolean z = false;
                        protoOutputStream.write(1133871366150L, pe4.pendingNesting > 0 || pe4.hadPending);
                        protoOutputStream.write(1133871366151L, pe4.activeNesting > 0 || pe4.hadActive);
                        if (pe4.activeTopNesting > 0 || pe4.hadActiveTop) {
                            z = true;
                        }
                        protoOutputStream.write(1133871366152L, z);
                        for (int k = 0; k < pe4.stopReasons.size(); k++) {
                            long srcToken = protoOutputStream.start(2246267895817L);
                            protoOutputStream.write(1159641169921L, pe4.stopReasons.keyAt(k));
                            protoOutputStream.write(1120986464258L, pe4.stopReasons.valueAt(k));
                            protoOutputStream.end(srcToken);
                        }
                        protoOutputStream.end(peToken2);
                        j2 = j5 + 1;
                        int i5 = filterUid;
                        i3 = i4;
                        uidMap = uidMap2;
                        NP = NP3;
                        uid = uid2;
                        NE = NE2;
                        period = period2;
                    }
                    int i6 = j2;
                    ArrayMap<String, PackageEntry> arrayMap = uidMap;
                    int i7 = NP;
                    i = i3;
                    int i8 = uid;
                } else {
                    i = i3;
                }
                i3 = i + 1;
                i2 = filterUid;
                NE = NE;
                period = period;
            }
            protoOutputStream.write(1120986464261L, this.mMaxTotalActive);
            protoOutputStream.write(1120986464262L, this.mMaxFgActive);
            protoOutputStream.end(token);
        }
    }

    /* access modifiers changed from: package-private */
    public void rebatchIfNeeded(long now) {
        long totalTime = this.mCurDataSet.getTotalTime(now);
        if (totalTime > 1800000) {
            DataSet last = this.mCurDataSet;
            last.mSummedTime = totalTime;
            this.mCurDataSet = new DataSet();
            last.finish(this.mCurDataSet, now);
            DataSet[] dataSetArr = this.mLastDataSets;
            System.arraycopy(dataSetArr, 0, dataSetArr, 1, dataSetArr.length - 1);
            this.mLastDataSets[0] = last;
        }
    }

    public void notePending(JobStatus job) {
        long now = JobSchedulerService.sUptimeMillisClock.millis();
        job.madePending = now;
        rebatchIfNeeded(now);
        this.mCurDataSet.incPending(job.getSourceUid(), job.getSourcePackageName(), now);
    }

    public void noteNonpending(JobStatus job) {
        long now = JobSchedulerService.sUptimeMillisClock.millis();
        this.mCurDataSet.decPending(job.getSourceUid(), job.getSourcePackageName(), now);
        rebatchIfNeeded(now);
    }

    public void noteActive(JobStatus job) {
        long now = JobSchedulerService.sUptimeMillisClock.millis();
        job.madeActive = now;
        rebatchIfNeeded(now);
        if (job.lastEvaluatedPriority >= 40) {
            this.mCurDataSet.incActiveTop(job.getSourceUid(), job.getSourcePackageName(), now);
        } else {
            this.mCurDataSet.incActive(job.getSourceUid(), job.getSourcePackageName(), now);
        }
        addEvent(job.getJob().isPeriodic() ? 3 : 1, job.getSourceUid(), job.getBatteryName(), job.getJobId(), 0, (String) null);
    }

    public void noteInactive(JobStatus job, int stopReason, String debugReason) {
        long now = JobSchedulerService.sUptimeMillisClock.millis();
        if (job.lastEvaluatedPriority >= 40) {
            this.mCurDataSet.decActiveTop(job.getSourceUid(), job.getSourcePackageName(), now, stopReason);
        } else {
            this.mCurDataSet.decActive(job.getSourceUid(), job.getSourcePackageName(), now, stopReason);
        }
        rebatchIfNeeded(now);
        addEvent(job.getJob().isPeriodic() ? 2 : 4, job.getSourceUid(), job.getBatteryName(), job.getJobId(), stopReason, debugReason);
    }

    public void noteConcurrency(int totalActive, int fgActive) {
        if (totalActive > this.mCurDataSet.mMaxTotalActive) {
            this.mCurDataSet.mMaxTotalActive = totalActive;
        }
        if (fgActive > this.mCurDataSet.mMaxFgActive) {
            this.mCurDataSet.mMaxFgActive = fgActive;
        }
    }

    public float getLoadFactor(JobStatus job) {
        int uid = job.getSourceUid();
        String pkg = job.getSourcePackageName();
        PackageEntry cur = this.mCurDataSet.getEntry(uid, pkg);
        DataSet[] dataSetArr = this.mLastDataSets;
        PackageEntry last = dataSetArr[0] != null ? dataSetArr[0].getEntry(uid, pkg) : null;
        if (cur == null && last == null) {
            return 0.0f;
        }
        long now = JobSchedulerService.sUptimeMillisClock.millis();
        long time = 0;
        if (cur != null) {
            time = 0 + cur.getActiveTime(now) + cur.getPendingTime(now);
        }
        long period = this.mCurDataSet.getTotalTime(now);
        if (last != null) {
            time += last.getActiveTime(now) + last.getPendingTime(now);
            period += this.mLastDataSets[0].getTotalTime(now);
        }
        return ((float) time) / ((float) period);
    }

    public void dump(PrintWriter pw, String prefix, int filterUid) {
        DataSet total;
        long now = JobSchedulerService.sUptimeMillisClock.millis();
        long nowElapsed = JobSchedulerService.sElapsedRealtimeClock.millis();
        DataSet[] dataSetArr = this.mLastDataSets;
        if (dataSetArr[0] != null) {
            total = new DataSet(dataSetArr[0]);
            this.mLastDataSets[0].addTo(total, now);
        } else {
            total = new DataSet(this.mCurDataSet);
        }
        this.mCurDataSet.addTo(total, now);
        int i = 1;
        while (true) {
            DataSet[] dataSetArr2 = this.mLastDataSets;
            if (i < dataSetArr2.length) {
                if (dataSetArr2[i] != null) {
                    dataSetArr2[i].dump(pw, "Historical stats", prefix, now, nowElapsed, filterUid);
                    pw.println();
                }
                i++;
            } else {
                total.dump(pw, "Current stats", prefix, now, nowElapsed, filterUid);
                return;
            }
        }
    }

    public void dump(ProtoOutputStream proto, long fieldId, int filterUid) {
        DataSet total;
        int i;
        long token = proto.start(fieldId);
        long now = JobSchedulerService.sUptimeMillisClock.millis();
        long nowElapsed = JobSchedulerService.sElapsedRealtimeClock.millis();
        DataSet[] dataSetArr = this.mLastDataSets;
        if (dataSetArr[0] != null) {
            total = new DataSet(dataSetArr[0]);
            this.mLastDataSets[0].addTo(total, now);
        } else {
            total = new DataSet(this.mCurDataSet);
        }
        this.mCurDataSet.addTo(total, now);
        int i2 = 1;
        while (true) {
            DataSet[] dataSetArr2 = this.mLastDataSets;
            if (i2 < dataSetArr2.length) {
                if (dataSetArr2[i2] != null) {
                    i = i2;
                    dataSetArr2[i2].dump(proto, 2246267895809L, now, nowElapsed, filterUid);
                } else {
                    i = i2;
                }
                i2 = i + 1;
            } else {
                int i3 = i2;
                total.dump(proto, 1146756268034L, now, nowElapsed, filterUid);
                proto.end(token);
                return;
            }
        }
    }

    public boolean dumpHistory(PrintWriter pw, String prefix, int filterUid) {
        int cmd;
        String label;
        PrintWriter printWriter = pw;
        int i = filterUid;
        int size = this.mEventIndices.size();
        if (size <= 0) {
            return false;
        }
        printWriter.println("  Job history:");
        long now = JobSchedulerService.sElapsedRealtimeClock.millis();
        for (int i2 = 0; i2 < size; i2++) {
            int index = this.mEventIndices.indexOf(i2);
            int uid = this.mEventUids[index];
            if ((i == -1 || i == UserHandle.getAppId(uid)) && (cmd = this.mEventCmds[index] & 255) != 0) {
                if (cmd == 1) {
                    label = "  START";
                } else if (cmd == 2) {
                    label = "   STOP";
                } else if (cmd == 3) {
                    label = "START-P";
                } else if (cmd != 4) {
                    label = "     ??";
                } else {
                    label = " STOP-P";
                }
                pw.print(prefix);
                TimeUtils.formatDuration(this.mEventTimes[index] - now, printWriter, 19);
                printWriter.print(" ");
                printWriter.print(label);
                printWriter.print(": #");
                UserHandle.formatUid(printWriter, uid);
                printWriter.print(SliceClientPermissions.SliceAuthority.DELIMITER);
                printWriter.print(this.mEventJobIds[index]);
                printWriter.print(" ");
                printWriter.print(this.mEventTags[index]);
                if (cmd == 2 || cmd == 4) {
                    printWriter.print(" ");
                    String[] strArr = this.mEventReasons;
                    if (strArr[index] != null) {
                        printWriter.print(strArr[index]);
                    } else {
                        printWriter.print(JobParameters.getReasonName((this.mEventCmds[index] & EVENT_STOP_REASON_MASK) >> 8));
                    }
                }
                pw.println();
            }
        }
        return true;
    }

    public void dumpHistory(ProtoOutputStream proto, long fieldId, int filterUid) {
        int size;
        ProtoOutputStream protoOutputStream = proto;
        int i = filterUid;
        int size2 = this.mEventIndices.size();
        if (size2 != 0) {
            long token = proto.start(fieldId);
            long now = JobSchedulerService.sElapsedRealtimeClock.millis();
            int i2 = 0;
            while (i2 < size2) {
                int index = this.mEventIndices.indexOf(i2);
                int uid = this.mEventUids[index];
                if (i == -1 || i == UserHandle.getAppId(uid)) {
                    int cmd = this.mEventCmds[index] & 255;
                    if (cmd == 0) {
                        size = size2;
                    } else {
                        long heToken = protoOutputStream.start(2246267895809L);
                        protoOutputStream.write(1159641169921L, cmd);
                        size = size2;
                        protoOutputStream.write(1112396529666L, now - this.mEventTimes[index]);
                        protoOutputStream.write(1120986464259L, uid);
                        protoOutputStream.write(1120986464260L, this.mEventJobIds[index]);
                        protoOutputStream.write(1138166333445L, this.mEventTags[index]);
                        if (cmd == 2 || cmd == 4) {
                            protoOutputStream.write(1159641169926L, (this.mEventCmds[index] & EVENT_STOP_REASON_MASK) >> 8);
                        }
                        protoOutputStream.end(heToken);
                    }
                } else {
                    size = size2;
                }
                i2++;
                i = filterUid;
                size2 = size;
            }
            protoOutputStream.end(token);
        }
    }
}
