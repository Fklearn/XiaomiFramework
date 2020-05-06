package com.miui.networkassistant.traffic.statistic;

import android.content.Context;
import android.os.UserHandle;
import android.util.Log;
import android.util.SparseArray;
import b.b.c.j.B;
import com.miui.net.MiuiNetworkSessionStats;
import com.miui.networkassistant.model.AppDataUsage;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.service.wrapper.AppMonitorWrapper;
import com.miui.networkassistant.utils.DateUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class StatisticAppTraffic {
    private static final int INIT_CAPACITY = 256;
    private static final String TAG = "StatisticAppTraffic";
    private long[] mBeginTime;
    private Context mContext;
    private long[] mEndTime;
    private long mFirstDayofMonth;
    private String mImsi;
    private long mLastMonth;
    private long mNow;
    private MiuiNetworkSessionStats mStatsSession;
    private long mToday;
    private long mYesterday;

    public StatisticAppTraffic(Context context, String str) {
        this.mContext = context;
        initStatsSession();
        this.mImsi = str;
        initData();
    }

    private void addEntryToAppUsageItem(SparseArray<AppDataUsage[]> sparseArray, Map<String, Long> map, int i) {
        AppDataUsage[] appDataUsageArr = sparseArray.get(i);
        AppDataUsage appDataUsage = appDataUsageArr[0];
        appDataUsage.addTxBytes(map.get("txBytes").longValue());
        appDataUsage.addRxBytes(map.get("rxBytes").longValue());
        AppDataUsage appDataUsage2 = appDataUsageArr[1];
        appDataUsage2.addTxBytes(map.get("txForegroundBytes").longValue());
        appDataUsage2.addRxBytes(map.get("rxForegroundBytes").longValue());
        AppDataUsage appDataUsage3 = appDataUsageArr[2];
        appDataUsage3.addTxBytes(map.get("txBytes").longValue() - map.get("txForegroundBytes").longValue());
        appDataUsage3.addRxBytes(map.get("rxBytes").longValue() - map.get("rxForegroundBytes").longValue());
    }

    private void buildAppUsageArray(SparseArray<AppDataUsage[]> sparseArray) {
        for (int i = 0; i < 4; i++) {
            if (sparseArray.get(i) == null) {
                sparseArray.put(i, buildAppUsageItems());
            }
        }
    }

    private AppDataUsage[] buildAppUsageItems() {
        AppDataUsage[] appDataUsageArr = new AppDataUsage[3];
        for (int i = 0; i < 3; i++) {
            appDataUsageArr[i] = new AppDataUsage();
        }
        return appDataUsageArr;
    }

    private void buildNetworkHistory(SparseArray<AppDataUsage[]> sparseArray, Integer num, long[] jArr, int i) {
        AppDataUsage appDataUsage;
        AppDataUsage[] appDataUsageArr = sparseArray.get(num.intValue());
        if (appDataUsageArr == null) {
            AppDataUsage[] appDataUsageArr2 = new AppDataUsage[2];
            for (int i2 = 0; i2 < 2; i2++) {
                appDataUsageArr2[i2] = new AppDataUsage();
            }
            sparseArray.put(num.intValue(), appDataUsageArr2);
            appDataUsageArr = appDataUsageArr2;
        }
        if (jArr != null && (appDataUsage = appDataUsageArr[i]) != null) {
            appDataUsage.addRxBytes(jArr[0]);
            appDataUsage.addTxBytes(jArr[1]);
        }
    }

    private void buildNetworkStats(SparseArray<AppDataUsage[]> sparseArray, SparseArray<Map<String, Long>> sparseArray2, int i, int i2, boolean z) {
        if (sparseArray2.size() != 0) {
            int size = sparseArray2.size();
            for (int i3 = 0; i3 < size; i3++) {
                int keyAt = sparseArray2.keyAt(i3);
                boolean z2 = true;
                if (!z ? keyAt != i2 : UserHandle.getUserId(keyAt) != i2) {
                    z2 = false;
                }
                if (z2) {
                    Map map = sparseArray2.get(keyAt);
                    if (map != null) {
                        addEntryToAppUsageItem(sparseArray, map, i);
                        if (!z) {
                            return;
                        }
                    } else {
                        return;
                    }
                }
            }
        }
    }

    private SparseArray<AppDataUsage> buildPeriodTimeMobileWorkStats(long j, long j2) {
        MiuiNetworkSessionStats miuiNetworkSessionStats = this.mStatsSession;
        if (miuiNetworkSessionStats == null) {
            return null;
        }
        return buildWorkStats(miuiNetworkSessionStats.getMobileSummaryForAllUid(this.mImsi, j, j2));
    }

    private SparseArray<AppDataUsage> buildPeriodTimeWifiWorkStats(long j, long j2) {
        MiuiNetworkSessionStats miuiNetworkSessionStats = this.mStatsSession;
        if (miuiNetworkSessionStats == null) {
            return null;
        }
        return buildWorkStats(miuiNetworkSessionStats.getWifiSummaryForAllUid(j, j2));
    }

    private SparseArray<AppDataUsage[]> buildTrafficListForUid(int i, int i2, long j, long j2, long j3) {
        SparseArray<AppDataUsage[]> sparseArray = new SparseArray<>(i2);
        long j4 = j + j3;
        int i3 = 0;
        long j5 = j;
        while (j5 < j2) {
            buildNetworkHistory(sparseArray, Integer.valueOf(i3), this.mStatsSession.getMobileHistoryForUid(this.mImsi, i, j5, j4), 0);
            buildNetworkHistory(sparseArray, Integer.valueOf(i3), this.mStatsSession.getWifiHistoryForUid(i, j5, j4), 1);
            j5 += j3;
            j4 += j3;
            i3++;
        }
        return sparseArray;
    }

    private SparseArray<AppDataUsage> buildWorkStats(SparseArray<Map<String, Long>> sparseArray) {
        SparseArray<AppDataUsage> sparseArray2 = new SparseArray<>(INIT_CAPACITY);
        if (sparseArray == null) {
            return null;
        }
        int size = sparseArray.size();
        for (int i = 0; i < size; i++) {
            int keyAt = sparseArray.keyAt(i);
            Map map = sparseArray.get(keyAt);
            if (!B.g()) {
                keyAt = B.a(keyAt);
            }
            if (keyAt == Integer.MAX_VALUE) {
                keyAt = -5;
            } else if (!((keyAt <= 20000 && keyAt >= 1000) || keyAt == -4 || keyAt == -5)) {
                Log.i(TAG, String.format("invalid uid :%d, tx:%d, rx:%d ", new Object[]{Integer.valueOf(keyAt), map.get("txBytes"), map.get("rxBytes")}));
                keyAt = -2;
            }
            AppDataUsage appDataUsage = new AppDataUsage();
            sparseArray2.put(keyAt, appDataUsage);
            appDataUsage.addTxBytes(((Long) map.get("txBytes")).longValue());
            appDataUsage.addRxBytes(((Long) map.get("rxBytes")).longValue());
        }
        return sparseArray2;
    }

    private void initData() {
        this.mNow = DateUtil.getNowTimeMillis();
        this.mToday = DateUtil.getTodayTimeMillis();
        this.mYesterday = DateUtil.getYesterdayTimeMillis();
        this.mFirstDayofMonth = DateUtil.getThisMonthBeginTimeMillis(1);
        this.mLastMonth = DateUtil.getLastMonthBeginTimeMillis(1);
        long j = this.mToday;
        long j2 = this.mFirstDayofMonth;
        this.mBeginTime = new long[]{this.mYesterday, j, this.mLastMonth, j2};
        long j3 = this.mNow;
        this.mEndTime = new long[]{j, j3, j2, j3};
    }

    private void initStatsSession() {
        MiuiNetworkSessionStats miuiNetworkSessionStats = this.mStatsSession;
        if (miuiNetworkSessionStats == null) {
            this.mStatsSession = new MiuiNetworkSessionStats(this.mContext);
            miuiNetworkSessionStats = this.mStatsSession;
        }
        miuiNetworkSessionStats.openSession();
    }

    public SparseArray<AppDataUsage[]> buildMobileDataUsage(int i, boolean z) {
        SparseArray<AppDataUsage[]> sparseArray = new SparseArray<>(3);
        buildAppUsageArray(sparseArray);
        for (int i2 = 0; i2 < 4; i2++) {
            SparseArray<Map<String, Long>> mobileSummaryForAllUid = this.mStatsSession.getMobileSummaryForAllUid(this.mImsi, this.mBeginTime[i2], this.mEndTime[i2]);
            if (mobileSummaryForAllUid != null) {
                buildNetworkStats(sparseArray, mobileSummaryForAllUid, i2, i, z);
            } else {
                Log.i(TAG, "mobile summaryStats null");
            }
        }
        return sparseArray;
    }

    public SparseArray<AppDataUsage[]> buildWifiDataUsage(int i, boolean z) {
        SparseArray<AppDataUsage[]> sparseArray = new SparseArray<>(3);
        buildAppUsageArray(sparseArray);
        for (int i2 = 0; i2 < 4; i2++) {
            SparseArray<Map<String, Long>> wifiSummaryForAllUid = this.mStatsSession.getWifiSummaryForAllUid(this.mBeginTime[i2], this.mEndTime[i2]);
            if (wifiSummaryForAllUid != null) {
                buildNetworkStats(sparseArray, wifiSummaryForAllUid, i2, i, z);
            } else {
                Log.i(TAG, "wifi summaryStats null");
            }
        }
        return sparseArray;
    }

    public void closeSession() {
        MiuiNetworkSessionStats miuiNetworkSessionStats = this.mStatsSession;
        if (miuiNetworkSessionStats != null) {
            miuiNetworkSessionStats.closeSession();
            this.mStatsSession = null;
        }
    }

    public SparseArray<AppDataUsage[]> getAppLastMonthPerDayTraffic(int i) {
        return buildTrafficListForUid(i, 31, this.mLastMonth, this.mFirstDayofMonth, 86400000);
    }

    public SparseArray<AppDataUsage[]> getAppThisMonthPerDayTraffic(int i) {
        return buildTrafficListForUid(i, 31, this.mFirstDayofMonth, this.mNow, 86400000);
    }

    public SparseArray<AppDataUsage[]> getAppTodayPerHourTraffic(int i) {
        return buildTrafficListForUid(i, 24, this.mToday, this.mNow, 3600000);
    }

    public SparseArray<AppDataUsage[]> getAppYesterdayPerHourTraffic(int i) {
        return buildTrafficListForUid(i, 24, this.mYesterday, this.mToday, 3600000);
    }

    public long getNetworkWifiTotalBytes(long j, long j2) {
        MiuiNetworkSessionStats miuiNetworkSessionStats = this.mStatsSession;
        if (miuiNetworkSessionStats != null) {
            return miuiNetworkSessionStats.getNetworkWifiTotalBytes(j, j2);
        }
        return 0;
    }

    public Map<Long, String> getTodayDataUsageAppMapByDec(Context context) {
        TreeMap treeMap = new TreeMap(new Comparator<Long>() {
            public int compare(Long l, Long l2) {
                int i = ((l2.longValue() - l.longValue()) > 0 ? 1 : ((l2.longValue() - l.longValue()) == 0 ? 0 : -1));
                if (i > 0) {
                    return 1;
                }
                return i == 0 ? 0 : -1;
            }
        });
        ArrayList<AppInfo> filteredAppInfosList = AppMonitorWrapper.getInstance(context).getFilteredAppInfosList();
        SparseArray<AppDataUsage> todayMobileTraffic = getTodayMobileTraffic();
        if (!(filteredAppInfosList == null || todayMobileTraffic == null)) {
            Iterator<AppInfo> it = filteredAppInfosList.iterator();
            while (it.hasNext()) {
                AppInfo next = it.next();
                AppDataUsage appDataUsage = todayMobileTraffic.get(next.uid);
                if (appDataUsage != null) {
                    long total = appDataUsage.getTotal();
                    treeMap.put(Long.valueOf(total), next.packageName.toString());
                }
            }
        }
        return treeMap;
    }

    public SparseArray<AppDataUsage> getTodayMobileTraffic() {
        return buildPeriodTimeMobileWorkStats(this.mToday, this.mNow);
    }

    public SparseArray<AppDataUsage> getYesterdayMobileTraffic() {
        return buildPeriodTimeMobileWorkStats(this.mYesterday, this.mToday);
    }

    public SparseArray<AppDataUsage> getYesterdayWifiTraffic() {
        return buildPeriodTimeWifiWorkStats(this.mYesterday, this.mToday);
    }
}
