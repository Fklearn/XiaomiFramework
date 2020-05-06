package com.miui.networkassistant.service.ts;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.UserHandle;
import android.util.Log;
import android.util.SparseArray;
import b.b.c.j.B;
import com.miui.net.MiuiNetworkSessionStats;
import com.miui.networkassistant.model.AppDataUsage;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.model.TrafficInfo;
import com.miui.networkassistant.service.wrapper.AppMonitorWrapper;
import com.miui.networkassistant.utils.DateUtil;
import com.miui.networkassistant.utils.DeviceUtil;
import com.miui.networkassistant.utils.HybirdServiceUtil;
import com.miui.networkassistant.utils.PackageUtil;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TrafficStatisticManager {
    private static final int INIT_CAPACITY = 256;
    private static final int MSG_MONITOR_APP_LIST_UPDATED = 2;
    private static final int MSG_UPDATE_TRAFFIC = 1;
    private static final String TAG = "TrafficStatisticManager";
    private SparseArray<AppDataUsage[]> mAllStats;
    private long[] mBeginTime;
    private Context mContext;
    private long[] mDataUsageTotal;
    private long[] mEndTime;
    private long mFirstDayOfMonth;
    private final HandlerThread mHandlerThread;
    private String mImsi;
    private boolean mIsMobileTraffic = true;
    private long mLastMonth;
    private ArrayList<TrafficStatisticListener> mListeners = new ArrayList<>();
    private Object mLock = new Object();
    private TrafficInfo mManagedProfileInfo;
    private HashSet<Integer> mManagedProfileUids = new HashSet<>();
    private int mManagedProfileUserId = -1;
    private AppMonitorWrapper mMonitorCenter;
    private AppMonitorWrapper.AppMonitorListener mMonitorCenterListener = new AppMonitorWrapper.AppMonitorListener() {
        public void onAppListUpdated() {
            TrafficStatisticManager.this.mUpdateHandler.sendEmptyMessage(2);
        }
    };
    private List<TrafficInfo> mNonSystemAppInfoList;
    private long mNow;
    private boolean mReady = false;
    private MiuiNetworkSessionStats mStatsSession;
    private List<TrafficInfo> mSystemAppInfoList;
    private TrafficInfo mSystemTrafficInfo;
    private long mThisWeek;
    private long mToday;
    private List<TrafficInfo> mTrafficInfoList;
    /* access modifiers changed from: private */
    public final UpdateAppTrafficHandler mUpdateHandler;
    private long mYesterday;

    public interface TrafficStatisticListener {
        void onAppTrafficStatisticUpdated();
    }

    private final class UpdateAppTrafficHandler extends Handler {
        public UpdateAppTrafficHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            super.handleMessage(message);
            int i = message.what;
            if (i == 1) {
                TrafficStatisticManager.this.refreshAppTrafficLocked();
                TrafficStatisticManager.this.broadcastTrafficUpdate();
            } else if (i == 2) {
                TrafficStatisticManager.this.initAppTraffic();
            }
        }
    }

    public TrafficStatisticManager(Context context, String str, int i) {
        this.mContext = context.getApplicationContext();
        this.mImsi = str;
        this.mIsMobileTraffic = getDataUsageType(i);
        this.mManagedProfileInfo = new TrafficInfo(new AppInfo());
        this.mHandlerThread = new HandlerThread(TAG);
        this.mHandlerThread.start();
        this.mUpdateHandler = new UpdateAppTrafficHandler(this.mHandlerThread.getLooper());
        startStatistic();
    }

    private void addAllAppsByUserIds() {
        ArrayList arrayList = new ArrayList();
        for (TrafficInfo next : this.mTrafficInfoList) {
            Iterator<Integer> it = this.mManagedProfileUids.iterator();
            while (it.hasNext()) {
                Integer next2 = it.next();
                if (next.mAppInfo.uid == B.a(next2.intValue())) {
                    Log.d(TAG, "add uid:" + next2);
                    AppInfo appInfo = new AppInfo();
                    appInfo.packageName = PackageUtil.getManagedProfilePackageNameFormat(this.mContext, next.mAppInfo.packageName.toString(), next2.intValue());
                    appInfo.uid = next2.intValue();
                    arrayList.add(new TrafficInfo(appInfo));
                }
            }
        }
        this.mTrafficInfoList.addAll(arrayList);
    }

    private void addEntryToAppUsageItem(Map<String, Long> map, SparseArray<AppDataUsage[]> sparseArray, int i, int i2) {
        AppDataUsage[] appDataUsageArr = sparseArray.get(i);
        if (appDataUsageArr == null) {
            appDataUsageArr = buildAppUsageItems();
            sparseArray.put(i, appDataUsageArr);
        }
        AppDataUsage appDataUsage = appDataUsageArr[i2];
        appDataUsage.addTxBytes(map.get("txBytes").longValue());
        appDataUsage.addRxBytes(map.get("rxBytes").longValue());
    }

    private void applyAllStatisticToAppList(List<TrafficInfo> list) {
        HashSet hashSet = new HashSet();
        this.mManagedProfileInfo.mAppStats = new TrafficInfo.AppStatistic();
        for (TrafficInfo next : list) {
            if (!hashSet.contains(Integer.valueOf(next.mAppInfo.uid))) {
                applyStatisticToApp(next, next.mAppStats);
                hashSet.add(Integer.valueOf(next.mAppInfo.uid));
            }
        }
    }

    private void applyDataUsagesTotal(long[] jArr, AppDataUsage[] appDataUsageArr) {
        if (jArr != null && appDataUsageArr != null && jArr.length == 4 && appDataUsageArr.length == 4) {
            for (int i = 0; i < 4; i++) {
                jArr[i] = appDataUsageArr[i].getTotal();
            }
        }
    }

    private void applyManagedProfileApps() {
        if (!this.mManagedProfileUids.isEmpty()) {
            TrafficInfo trafficInfo = this.mManagedProfileInfo;
            AppInfo appInfo = trafficInfo.mAppInfo;
            appInfo.packageName = "magaged_profile_package&@" + this.mManagedProfileUserId;
            appInfo.uid = this.mManagedProfileUserId;
            this.mTrafficInfoList.add(trafficInfo);
        }
    }

    private void applyStatisticToApp(TrafficInfo trafficInfo, TrafficInfo.AppStatistic appStatistic) {
        AppDataUsage appDataUsage;
        SparseArray<AppDataUsage[]> sparseArray = this.mAllStats;
        if (sparseArray != null) {
            AppDataUsage[] appDataUsageArr = sparseArray.get(trafficInfo.mAppInfo.uid);
            for (int i = 0; i < 4; i++) {
                if (!(appDataUsageArr == null || (appDataUsage = appDataUsageArr[i]) == null)) {
                    appStatistic.mTotalBytes[i] = appDataUsage.getTotal();
                }
                AppInfo appInfo = trafficInfo.mAppInfo;
                if (appInfo.isSystemApp && !HybirdServiceUtil.isHybirdService(appInfo.packageName)) {
                    long[] jArr = this.mSystemTrafficInfo.mAppStats.mTotalBytes;
                    jArr[i] = jArr[i] + appStatistic.mTotalBytes[i];
                }
                if (this.mManagedProfileUserId == UserHandle.getUserId(trafficInfo.mAppInfo.uid)) {
                    long[] jArr2 = this.mManagedProfileInfo.mAppStats.mTotalBytes;
                    jArr2[i] = jArr2[i] + appStatistic.mTotalBytes[i];
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public void broadcastTrafficUpdate() {
        synchronized (this.mListeners) {
            Log.i(TAG, "broadcastTrafficUpdate");
            Iterator<TrafficStatisticListener> it = this.mListeners.iterator();
            while (it.hasNext()) {
                it.next().onAppTrafficStatisticUpdated();
            }
        }
    }

    private void buildAppTotalTraffic() {
        buildDataUsage(new HashSet(INIT_CAPACITY), this.mIsMobileTraffic);
    }

    private AppDataUsage[] buildAppUsageItems() {
        AppDataUsage[] appDataUsageArr = new AppDataUsage[4];
        for (int i = 0; i < 4; i++) {
            appDataUsageArr[i] = new AppDataUsage();
        }
        return appDataUsageArr;
    }

    private void buildDataUsage(HashSet<Integer> hashSet, boolean z) {
        SparseArray<Map<String, Long>> sparseArray;
        if (this.mStatsSession != null && hashSet != null) {
            this.mAllStats = new SparseArray<>(INIT_CAPACITY);
            for (int i = 0; i < 4; i++) {
                if (z) {
                    try {
                        sparseArray = this.mStatsSession.getMobileSummaryForAllUid(this.mImsi, this.mBeginTime[i], this.mEndTime[i]);
                    } catch (NullPointerException e) {
                        Log.i(TAG, "get summary stats failed", e);
                        return;
                    }
                } else {
                    sparseArray = this.mStatsSession.getWifiSummaryForAllUid(this.mBeginTime[i], this.mEndTime[i]);
                }
                if (sparseArray != null) {
                    buildNetworkStats(sparseArray, i, hashSet);
                } else {
                    Log.i(TAG, "summaryStats null");
                }
            }
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:35:0x00a3  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x00ac  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void buildNetworkStats(android.util.SparseArray<java.util.Map<java.lang.String, java.lang.Long>> r18, int r19, java.util.HashSet<java.lang.Integer> r20) {
        /*
            r17 = this;
            r0 = r17
            r1 = r18
            r2 = r19
            if (r1 == 0) goto L_0x00e6
            int r3 = r18.size()
            if (r3 != 0) goto L_0x0010
            goto L_0x00e6
        L_0x0010:
            r3 = 0
            int r4 = r18.size()
            r5 = 0
            r8 = r5
            r10 = r8
            r5 = r3
            r3 = 0
        L_0x001b:
            java.lang.String r6 = "rxBytes"
            java.lang.String r12 = "txBytes"
            if (r3 >= r4) goto L_0x00d2
            int r5 = r1.keyAt(r3)
            int r13 = android.os.UserHandle.getUserId(r5)
            android.content.Context r14 = r0.mContext
            boolean r14 = b.b.c.j.B.a((android.content.Context) r14, (int) r13)
            if (r14 == 0) goto L_0x003b
            int r14 = b.b.c.j.B.a((int) r5)
            r15 = 10000(0x2710, float:1.4013E-41)
            if (r14 < r15) goto L_0x003b
            r14 = 1
            goto L_0x003c
        L_0x003b:
            r14 = 0
        L_0x003c:
            java.lang.Object r15 = r1.get(r5)
            java.util.Map r15 = (java.util.Map) r15
            r7 = -2
            if (r14 == 0) goto L_0x0051
            r0.mManagedProfileUserId = r13
            java.util.HashSet<java.lang.Integer> r13 = r0.mManagedProfileUids
            java.lang.Integer r14 = java.lang.Integer.valueOf(r5)
            r13.add(r14)
            goto L_0x0057
        L_0x0051:
            r13 = 2147483647(0x7fffffff, float:NaN)
            if (r5 != r13) goto L_0x0059
            r5 = -5
        L_0x0057:
            r14 = 0
            goto L_0x0098
        L_0x0059:
            r13 = 20000(0x4e20, float:2.8026E-41)
            if (r5 > r13) goto L_0x0061
            r13 = 1000(0x3e8, float:1.401E-42)
            if (r5 >= r13) goto L_0x0057
        L_0x0061:
            r13 = -4
            if (r5 == r13) goto L_0x0057
            r13 = -5
            if (r5 == r13) goto L_0x0057
            boolean r13 = b.b.c.j.B.g()
            if (r13 != 0) goto L_0x0057
            boolean r13 = b.b.c.j.C.a(r5)
            if (r13 != 0) goto L_0x0057
            r13 = 3
            java.lang.Object[] r13 = new java.lang.Object[r13]
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r14 = 0
            r13[r14] = r5
            java.lang.Object r5 = r15.get(r12)
            r16 = 1
            r13[r16] = r5
            r5 = 2
            java.lang.Object r16 = r15.get(r6)
            r13[r5] = r16
            java.lang.String r5 = "invalid uid :%d, tx:%d, rx:%d "
            java.lang.String r5 = java.lang.String.format(r5, r13)
            java.lang.String r13 = "TrafficStatisticManager"
            android.util.Log.i(r13, r5)
            r5 = r7
        L_0x0098:
            android.util.SparseArray<com.miui.networkassistant.model.AppDataUsage[]> r13 = r0.mAllStats
            r0.addEntryToAppUsageItem(r15, r13, r5, r2)
            boolean r13 = b.b.c.j.B.g()
            if (r13 == 0) goto L_0x00ac
            if (r5 == r7) goto L_0x00c4
            boolean r7 = b.b.c.j.B.d(r5)
            if (r7 != 0) goto L_0x00c4
            goto L_0x00ae
        L_0x00ac:
            if (r5 == r7) goto L_0x00c4
        L_0x00ae:
            java.lang.Object r7 = r15.get(r12)
            java.lang.Long r7 = (java.lang.Long) r7
            long r12 = r7.longValue()
            long r8 = r8 + r12
            java.lang.Object r6 = r15.get(r6)
            java.lang.Long r6 = (java.lang.Long) r6
            long r6 = r6.longValue()
            long r10 = r10 + r6
        L_0x00c4:
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r7 = r20
            r7.add(r5)
            int r3 = r3 + 1
            r5 = r15
            goto L_0x001b
        L_0x00d2:
            java.lang.Long r1 = java.lang.Long.valueOf(r8)
            r5.put(r12, r1)
            java.lang.Long r1 = java.lang.Long.valueOf(r10)
            r5.put(r6, r1)
            android.util.SparseArray<com.miui.networkassistant.model.AppDataUsage[]> r1 = r0.mAllStats
            r3 = -1
            r0.addEntryToAppUsageItem(r5, r1, r3, r2)
        L_0x00e6:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.service.ts.TrafficStatisticManager.buildNetworkStats(android.util.SparseArray, int, java.util.HashSet):void");
    }

    private void closeStatsSession() {
        MiuiNetworkSessionStats miuiNetworkSessionStats = this.mStatsSession;
        if (miuiNetworkSessionStats != null) {
            miuiNetworkSessionStats.closeSession();
            this.mStatsSession = null;
        }
    }

    private void copyLatestResult() {
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        for (TrafficInfo next : this.mTrafficInfoList) {
            AppInfo appInfo = next.mAppInfo;
            if (!appInfo.isSystemApp || HybirdServiceUtil.isHybirdService(appInfo.packageName)) {
                arrayList.add(new TrafficInfo(next));
            } else {
                arrayList2.add(new TrafficInfo(next));
            }
        }
        List<TrafficInfo> list = this.mNonSystemAppInfoList;
        if (list != null) {
            list.clear();
        }
        this.mNonSystemAppInfoList = arrayList;
        List<TrafficInfo> list2 = this.mSystemAppInfoList;
        if (list2 != null) {
            list2.clear();
        }
        this.mSystemAppInfoList = arrayList2;
        this.mDataUsageTotal = new long[4];
        SparseArray<AppDataUsage[]> sparseArray = this.mAllStats;
        if (sparseArray != null) {
            applyDataUsagesTotal(this.mDataUsageTotal, sparseArray.get(-1));
        }
    }

    private void createAllAppList() {
        ArrayList<AppInfo> filteredAppInfosList = this.mMonitorCenter.getFilteredAppInfosList();
        if (filteredAppInfosList != null) {
            filteredAppInfosList.add(0, new AppInfo("icon_personal_hotpot", -5, false));
            if (B.f()) {
                filteredAppInfosList.add(0, new AppInfo("icon_deleted_app", -4, false));
            }
            this.mTrafficInfoList = new ArrayList();
            Iterator<AppInfo> it = filteredAppInfosList.iterator();
            while (it.hasNext()) {
                this.mTrafficInfoList.add(new TrafficInfo(it.next()));
            }
            AppInfo appInfo = new AppInfo("icon_system_app", -10, false);
            filteredAppInfosList.add(0, appInfo);
            this.mSystemTrafficInfo = new TrafficInfo(appInfo);
            this.mTrafficInfoList.add(this.mSystemTrafficInfo);
        }
    }

    private void createStatsSession() {
        MiuiNetworkSessionStats miuiNetworkSessionStats = this.mStatsSession;
        if (miuiNetworkSessionStats == null) {
            this.mStatsSession = new MiuiNetworkSessionStats(this.mContext);
            miuiNetworkSessionStats = this.mStatsSession;
        }
        miuiNetworkSessionStats.openSession();
    }

    private boolean getDataUsageType(int i) {
        return i == 0 || i != 1;
    }

    /* access modifiers changed from: private */
    public void initAppTraffic() {
        synchronized (this.mLock) {
            createAllAppList();
        }
        this.mUpdateHandler.sendMessage(this.mUpdateHandler.obtainMessage(1));
    }

    /* access modifiers changed from: private */
    public void refreshAppTrafficLocked() {
        Log.i(TAG, "refreshAppTrafficLocked");
        this.mReady = false;
        this.mStatsSession.forceUpdate();
        this.mManagedProfileUids.clear();
        updateDateTime();
        buildAppTotalTraffic();
        synchronized (this.mLock) {
            addAllAppsByUserIds();
            applyAllStatisticToAppList(this.mTrafficInfoList);
            applyManagedProfileApps();
            copyLatestResult();
            this.mReady = true;
        }
    }

    private void registerMonitorCenter() {
        this.mMonitorCenter = AppMonitorWrapper.getInstance(this.mContext);
        this.mMonitorCenter.registerLisener(this.mMonitorCenterListener);
    }

    private void startStatistic() {
        createStatsSession();
        registerMonitorCenter();
    }

    private void unRegisterMonitorCenter() {
        AppMonitorWrapper appMonitorWrapper = this.mMonitorCenter;
        if (appMonitorWrapper != null) {
            appMonitorWrapper.unRegisterLisener(this.mMonitorCenterListener);
        }
    }

    private void updateDateTime() {
        long[] jArr;
        this.mNow = DateUtil.getNowTimeMillis();
        this.mToday = DateUtil.getTodayTimeMillis();
        this.mFirstDayOfMonth = DateUtil.getThisMonthBeginTimeMillis(1);
        this.mLastMonth = DateUtil.getLastMonthBeginTimeMillis(1);
        if (DeviceUtil.IS_CM_CUSTOMIZATION_TEST) {
            this.mThisWeek = DateUtil.getThisWeekBeginTimeMillis();
            long j = this.mFirstDayOfMonth;
            this.mBeginTime = new long[]{this.mThisWeek, this.mToday, this.mLastMonth, j};
            long j2 = this.mNow;
            jArr = new long[]{j2, j2, j, j2};
        } else {
            this.mYesterday = DateUtil.getYesterdayTimeMillis();
            long j3 = this.mToday;
            long j4 = this.mFirstDayOfMonth;
            this.mBeginTime = new long[]{this.mYesterday, j3, this.mLastMonth, j4};
            long j5 = this.mNow;
            jArr = new long[]{j3, j5, j4, j5};
        }
        this.mEndTime = jArr;
    }

    public long[] getAllAppDataUsageTotal() {
        long[] jArr;
        synchronized (this.mLock) {
            jArr = this.mDataUsageTotal;
        }
        return jArr;
    }

    public List<TrafficInfo> getNonSystemAppsListLocked() {
        ArrayList arrayList;
        synchronized (this.mLock) {
            arrayList = new ArrayList(this.mNonSystemAppInfoList);
        }
        return arrayList;
    }

    public long[] getSystemAppDataUsageTotal() {
        long[] jArr;
        synchronized (this.mLock) {
            jArr = this.mSystemTrafficInfo.mAppStats.mTotalBytes;
        }
        return jArr;
    }

    public List<TrafficInfo> getSystemAppListLocked() {
        ArrayList arrayList;
        synchronized (this.mLock) {
            arrayList = new ArrayList(this.mSystemAppInfoList);
        }
        return arrayList;
    }

    public void quitStatistic() {
        unRegisterMonitorCenter();
        this.mUpdateHandler.removeCallbacksAndMessages((Object) null);
        this.mUpdateHandler.getLooper().quit();
        closeStatsSession();
    }

    public void registerListener(TrafficStatisticListener trafficStatisticListener) {
        synchronized (this.mListeners) {
            if (trafficStatisticListener != null) {
                if (!this.mListeners.contains(trafficStatisticListener)) {
                    this.mListeners.add(trafficStatisticListener);
                    if (this.mReady) {
                        trafficStatisticListener.onAppTrafficStatisticUpdated();
                    }
                }
            }
        }
    }

    public void setDataUsageType(int i) {
        this.mIsMobileTraffic = getDataUsageType(i);
        this.mUpdateHandler.sendMessage(this.mUpdateHandler.obtainMessage(2));
    }

    public void unRegisterListener(TrafficStatisticListener trafficStatisticListener) {
        synchronized (this.mListeners) {
            if (trafficStatisticListener != null) {
                if (this.mListeners.contains(trafficStatisticListener)) {
                    this.mListeners.remove(trafficStatisticListener);
                }
            }
        }
    }
}
