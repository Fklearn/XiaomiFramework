package com.android.server.am;

import android.os.BatteryStats;
import android.os.Build;
import android.text.format.DateFormat;
import android.util.LocalLog;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.os.BatteryStatsImpl;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CpuTimeCollection {
    private static boolean DEBUG = Build.IS_DEBUGGABLE;
    private static final String FORMATE_RULE = "yyyy-MM-dd-HH-mm-ss";
    private static final String TAG = "CpuTimeCollection";
    private static final int WHICH = 0;
    private static final LocalLog mCpuCollectionLog = new LocalLog(DEBUG ? 500 : 200);
    private static ConcurrentHashMap<Integer, ModuleCpuTime> mCpuTimeModules = new ConcurrentHashMap<>();
    private static long mRecordTime;

    public static void updateUidCpuTime(BatteryStatsImpl batteryStatsImpl, boolean screenOn, boolean firstRecord) {
        SparseArray<? extends BatteryStats.Uid> uidStatsArray;
        SparseArray<? extends BatteryStats.Uid> uidStats;
        boolean z;
        boolean firstRecord2;
        SparseArray<? extends BatteryStats.Uid> uidStatsArray2;
        boolean firstRecord3;
        SparseArray<? extends BatteryStats.Uid> uidStats2;
        if (batteryStatsImpl != null && (uidStatsArray = batteryStatsImpl.getUidStats()) != null && (uidStats = uidStatsArray.clone()) != null) {
            if (DEBUG) {
                StringBuilder sb = new StringBuilder();
                sb.append("firstRecord == ");
                z = firstRecord;
                sb.append(z);
                Log.d(TAG, sb.toString());
            } else {
                z = firstRecord;
            }
            if (mRecordTime <= batteryStatsImpl.getStartClockTime()) {
                resetCpuTimeModule();
                firstRecord2 = true;
            } else {
                firstRecord2 = z;
            }
            ConcurrentHashMap<Integer, ModuleCpuTime> subCpuTimeModules = new ConcurrentHashMap<>();
            int NU = uidStats.size();
            int iu = 0;
            while (iu < NU) {
                int uid = uidStats.keyAt(iu);
                BatteryStats.Uid uidStat = (BatteryStats.Uid) uidStats.valueAt(iu);
                if (uidStat == null) {
                    uidStatsArray2 = uidStatsArray;
                    uidStats2 = uidStats;
                    firstRecord3 = firstRecord2;
                } else {
                    long userCpuTimeUs = uidStat.getUserCpuTimeUs(0);
                    long systemCpuTimeUs = uidStat.getSystemCpuTimeUs(0);
                    ModuleCpuTime currentItem = mCpuTimeModules.get(Integer.valueOf(uid));
                    if (currentItem != null) {
                        uidStatsArray2 = uidStatsArray;
                        ModuleCpuTime currentItem2 = currentItem;
                        long j = userCpuTimeUs - currentItem.userCpuTimeUs;
                        uidStats2 = uidStats;
                        firstRecord3 = firstRecord2;
                        subCpuTimeModules.put(Integer.valueOf(uid), new ModuleCpuTime(uid, j, systemCpuTimeUs - currentItem.systemCpuTimeUs));
                        currentItem2.userCpuTimeUs = userCpuTimeUs;
                        currentItem2.systemCpuTimeUs = systemCpuTimeUs;
                    } else {
                        uidStatsArray2 = uidStatsArray;
                        uidStats2 = uidStats;
                        firstRecord3 = firstRecord2;
                        ModuleCpuTime moduleCpuTime = currentItem;
                        long j2 = systemCpuTimeUs;
                        ModuleCpuTime newCpuTime = new ModuleCpuTime(uid, userCpuTimeUs, systemCpuTimeUs);
                        subCpuTimeModules.put(Integer.valueOf(uid), newCpuTime);
                        mCpuTimeModules.put(Integer.valueOf(uid), newCpuTime);
                    }
                }
                iu++;
                uidStats = uidStats2;
                firstRecord2 = firstRecord3;
                uidStatsArray = uidStatsArray2;
            }
            recordUidCpuTime(screenOn, firstRecord2, subCpuTimeModules);
        }
    }

    public static void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        StringBuilder sb = new StringBuilder(2048);
        mCpuCollectionLog.dump(fd, writer, args);
        if (mCpuTimeModules.size() != 0) {
            sb.append("\nDump Uid Cpu Time:\n");
            writeString(sb, mapValueCompareSort(mCpuTimeModules));
            writer.println(sb.toString());
        }
    }

    private CpuTimeCollection() {
    }

    private static void writeString(StringBuilder stringBuilder, List<Map.Entry<Integer, ModuleCpuTime>> CpuTimeModules) {
        if (CpuTimeModules != null && CpuTimeModules.size() != 0) {
            for (Map.Entry<Integer, ModuleCpuTime> entry : CpuTimeModules.size() >= 20 ? CpuTimeModules.subList(0, 20) : CpuTimeModules) {
                ModuleCpuTime moduleStats = entry.getValue();
                if (moduleStats != null && (moduleStats.userCpuTimeUs > 0 || moduleStats.systemCpuTimeUs > 0)) {
                    stringBuilder.append(moduleStats.toString());
                    stringBuilder.append("\n");
                }
            }
        }
    }

    private static List<Map.Entry<Integer, ModuleCpuTime>> mapValueCompareSort(ConcurrentHashMap<Integer, ModuleCpuTime> hashMap) {
        List<Map.Entry<Integer, ModuleCpuTime>> CpuTimeModules = new ArrayList<>(hashMap.entrySet());
        Collections.sort(CpuTimeModules, new Comparator<Map.Entry<Integer, ModuleCpuTime>>() {
            public int compare(Map.Entry<Integer, ModuleCpuTime> cpuTimeEntry, Map.Entry<Integer, ModuleCpuTime> cpuTimeEntryNext) {
                return -Long.valueOf(cpuTimeEntry.getValue().userCpuTimeUs + cpuTimeEntry.getValue().systemCpuTimeUs).compareTo(Long.valueOf(cpuTimeEntryNext.getValue().userCpuTimeUs + cpuTimeEntryNext.getValue().systemCpuTimeUs));
            }
        });
        return CpuTimeModules;
    }

    private static void recordUidCpuTime(boolean screenOn, boolean firstRecord, ConcurrentHashMap<Integer, ModuleCpuTime> subCpuTimeModules) {
        if (subCpuTimeModules != null) {
            StringBuilder strb = new StringBuilder(2048);
            long curTime = System.currentTimeMillis();
            if (DEBUG) {
                Log.d(TAG, "Screen state " + screenOn + ", firstRecord = " + firstRecord);
            }
            int status = getStatus(screenOn, firstRecord);
            if (status == 0) {
                strb.append("[screen_on (" + DateFormat.format(FORMATE_RULE, mRecordTime).toString() + "--" + DateFormat.format(FORMATE_RULE, curTime).toString() + ")]");
            } else if (status == 1) {
                strb.append("[first record screen off (" + DateFormat.format(FORMATE_RULE, curTime).toString() + ")]");
            } else if (status == 2) {
                strb.append("[screen_off (" + DateFormat.format(FORMATE_RULE, mRecordTime).toString() + "--" + DateFormat.format(FORMATE_RULE, curTime).toString() + ")]");
            } else if (status == 3) {
                strb.append("[first record screen on (" + DateFormat.format(FORMATE_RULE, curTime).toString() + ")]");
            }
            mRecordTime = curTime;
            strb.append("\ndump subtract cpu time:\n");
            if (subCpuTimeModules.size() != 0) {
                writeString(strb, mapValueCompareSort(subCpuTimeModules));
            }
            mCpuCollectionLog.log(strb.toString());
        }
    }

    private static int getStatus(boolean screenOn, boolean firstRecord) {
        return (screenOn ? (char) 2 : 0) | firstRecord ? 1 : 0;
    }

    private static class ModuleCpuTime {
        int moduleUid;
        long systemCpuTimeUs;
        long userCpuTimeUs;

        public ModuleCpuTime(int uid, long userCpuTimeUs2, long systemCpuTimeUs2) {
            this.moduleUid = uid;
            long j = 0;
            this.userCpuTimeUs = userCpuTimeUs2 > 0 ? userCpuTimeUs2 : 0;
            this.systemCpuTimeUs = systemCpuTimeUs2 > 0 ? systemCpuTimeUs2 : j;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(128);
            if (this.userCpuTimeUs > 0 || this.systemCpuTimeUs > 0) {
                sb.append(this.moduleUid);
                sb.append(":  Total cpu time: u=");
                BatteryStats.formatTimeMs(sb, this.userCpuTimeUs / 1000);
                sb.append("s=");
                BatteryStats.formatTimeMs(sb, this.systemCpuTimeUs / 1000);
            }
            return sb.toString();
        }
    }

    public static void resetCpuTimeModule() {
        mCpuTimeModules.clear();
    }
}
