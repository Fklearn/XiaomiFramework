package com.miui.networkassistant.service.tm;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import b.b.c.c.a.a;
import b.b.c.h.f;
import b.d.c.a.a.a.b.a.c;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.networkassistant.config.CommonConfig;
import com.miui.networkassistant.dual.SimCardHelper;
import com.miui.networkassistant.model.AppDataUsage;
import com.miui.networkassistant.model.AppInfo;
import com.miui.networkassistant.service.wrapper.AppMonitorWrapper;
import com.miui.networkassistant.traffic.statistic.StatisticAppTraffic;
import com.miui.networkassistant.utils.DataUsageReportUtil;
import com.miui.networkassistant.utils.DateUtil;
import com.xiaomi.stat.d;
import java.util.ArrayList;
import java.util.Iterator;
import miui.security.DigestUtils;
import miui.text.ExtraTextUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DataUsageReportManager {
    private static final String TAG = "DataUsageReportManager";
    private long connectedTime = 0;
    private long disconnectedTime = 0;
    /* access modifiers changed from: private */
    public CommonConfig mConfig;
    /* access modifiers changed from: private */
    public Context mContext;
    private f.a mLastState = f.a.Inited;
    private AppMonitorWrapper mMonitorCenter;
    /* access modifiers changed from: private */
    public SimCardHelper mSimCardHelper;
    /* access modifiers changed from: private */
    public TrafficSimManager[] mTrafficManagers;

    public DataUsageReportManager(Context context) {
        this.mContext = context.getApplicationContext();
        this.mConfig = CommonConfig.getInstance(this.mContext);
        this.mSimCardHelper = SimCardHelper.getInstance(this.mContext);
    }

    private void checkAndGetMonitorCenter() {
        if (this.mMonitorCenter == null) {
            this.mMonitorCenter = AppMonitorWrapper.getInstance(this.mContext);
        }
    }

    /* access modifiers changed from: private */
    public void checkWifiAndMobileConnectedTime() {
        long currentTimeMillis = System.currentTimeMillis();
        f.a aVar = this.mLastState;
        if (aVar == f.a.WifiConnected || aVar == f.a.MobileConnected) {
            handleDisconnected(currentTimeMillis, DateUtil.getYesterdayTimeMillis());
            this.connectedTime = currentTimeMillis;
        }
        String str = TAG;
        Log.d(str, "wifiAndMobileTurnOnTime:" + this.mConfig.getWifiDailyConnectedTime() + "," + this.mConfig.getMobileDailyConnectedTime());
    }

    private JSONArray getAllAppsJsonString(String str) {
        checkAndGetMonitorCenter();
        ArrayList<AppInfo> filteredAppInfosList = this.mMonitorCenter.getFilteredAppInfosList();
        AppDataUsage appDataUsage = null;
        if (filteredAppInfosList == null) {
            return null;
        }
        StatisticAppTraffic statisticAppTraffic = new StatisticAppTraffic(this.mContext, str);
        SparseArray<AppDataUsage> yesterdayMobileTraffic = statisticAppTraffic.getYesterdayMobileTraffic();
        SparseArray<AppDataUsage> yesterdayWifiTraffic = statisticAppTraffic.getYesterdayWifiTraffic();
        statisticAppTraffic.closeSession();
        if (yesterdayMobileTraffic == null && yesterdayWifiTraffic == null) {
            return null;
        }
        JSONArray jSONArray = new JSONArray();
        Iterator<AppInfo> it = filteredAppInfosList.iterator();
        AppDataUsage appDataUsage2 = null;
        while (it.hasNext()) {
            AppInfo next = it.next();
            int i = next.uid;
            if (yesterdayMobileTraffic != null) {
                appDataUsage = yesterdayMobileTraffic.get(i);
            }
            if (yesterdayWifiTraffic != null) {
                appDataUsage2 = yesterdayWifiTraffic.get(i);
            }
            if (appDataUsage != null || appDataUsage2 != null) {
                JSONObject jSONObject = new JSONObject();
                try {
                    jSONObject.put("pkgName", next.packageName.toString());
                    long j = 0;
                    jSONObject.put("usedTraffic", appDataUsage != null ? appDataUsage.getTotal() : 0);
                    if (appDataUsage2 != null) {
                        j = appDataUsage2.getTotal();
                    }
                    jSONObject.put("usedWifi", j);
                    jSONArray.put(jSONObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return jSONArray;
    }

    /* access modifiers changed from: private */
    public JSONObject getAppDetailDataUsage(TrafficSimManager trafficSimManager) {
        JSONObject jSONObject = new JSONObject();
        try {
            checkAndGetMonitorCenter();
            ArrayList<AppInfo> filteredAppInfosList = this.mMonitorCenter.getFilteredAppInfosList();
            if (filteredAppInfosList == null) {
                return jSONObject;
            }
            StatisticAppTraffic statisticAppTraffic = new StatisticAppTraffic(this.mContext, trafficSimManager.mSimUser.getImsi());
            Iterator<AppInfo> it = filteredAppInfosList.iterator();
            while (it.hasNext()) {
                AppInfo next = it.next();
                SparseArray<AppDataUsage[]> appYesterdayPerHourTraffic = statisticAppTraffic.getAppYesterdayPerHourTraffic(next.uid);
                JSONObject jSONObject2 = new JSONObject();
                if (appYesterdayPerHourTraffic != null) {
                    for (int i = 0; i < 24; i++) {
                        AppDataUsage[] appDataUsageArr = appYesterdayPerHourTraffic.get(i);
                        JSONObject jSONObject3 = new JSONObject();
                        if (appDataUsageArr != null) {
                            long total = appDataUsageArr[0].getTotal();
                            long total2 = appDataUsageArr[1].getTotal();
                            if (total > 0 || total2 > 0) {
                                jSONObject3.put(d.V, total);
                                jSONObject3.put(AnimatedProperty.PROPERTY_NAME_W, total2);
                                jSONObject2.put(String.valueOf(i), jSONObject3);
                            }
                        }
                    }
                }
                if (jSONObject2.length() > 0) {
                    jSONObject.put(next.packageName.toString(), jSONObject2);
                }
            }
            return jSONObject;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getSha1String(String str) {
        return TextUtils.isEmpty(str) ? "" : ExtraTextUtils.toHexReadable(DigestUtils.get(String.valueOf(str), "SHA-1"));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x0096 A[Catch:{ Exception -> 0x0171 }] */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x00b8  */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x011d A[Catch:{ Exception -> 0x016f }] */
    /* JADX WARNING: Removed duplicated region for block: B:32:0x0168 A[Catch:{ Exception -> 0x016f }] */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0169 A[Catch:{ Exception -> 0x016f }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public org.json.JSONObject getUploadDataUsage(com.miui.networkassistant.service.tm.TrafficSimManager r33, int r34) {
        /*
            r32 = this;
            r1 = r32
            r0 = r33
            com.miui.networkassistant.config.SimUserInfo r2 = r0.mSimUser
            org.json.JSONObject r3 = new org.json.JSONObject
            r3.<init>()
            java.lang.String r4 = r2.getImsi()     // Catch:{ Exception -> 0x0171 }
            boolean r5 = com.miui.networkassistant.utils.DeviceUtil.IS_INTERNATIONAL_BUILD     // Catch:{ Exception -> 0x0171 }
            java.lang.String r6 = ""
            if (r5 != 0) goto L_0x0045
            android.content.Context r5 = r1.mContext     // Catch:{ Exception -> 0x0171 }
            int r7 = r2.getSlotNum()     // Catch:{ Exception -> 0x0171 }
            java.lang.String r5 = com.miui.networkassistant.utils.TelephonyUtil.getPhoneNumber(r5, r7)     // Catch:{ Exception -> 0x0171 }
            java.lang.String r5 = r1.getSha1String(r5)     // Catch:{ Exception -> 0x0171 }
            java.lang.String r7 = "phoneNum"
            r3.put(r7, r5)     // Catch:{ Exception -> 0x0171 }
            java.lang.String r5 = r1.getSha1String(r4)     // Catch:{ Exception -> 0x0171 }
            java.lang.String r7 = "imsi"
            r3.put(r7, r5)     // Catch:{ Exception -> 0x0171 }
            java.lang.String r5 = "imsi_p"
            boolean r7 = android.text.TextUtils.isEmpty(r4)     // Catch:{ Exception -> 0x0171 }
            if (r7 == 0) goto L_0x003b
            r7 = r6
            goto L_0x0042
        L_0x003b:
            r7 = 0
            r8 = 8
            java.lang.String r7 = r4.substring(r7, r8)     // Catch:{ Exception -> 0x0171 }
        L_0x0042:
            r3.put(r5, r7)     // Catch:{ Exception -> 0x0171 }
        L_0x0045:
            r5 = r34
            java.lang.String r5 = com.miui.networkassistant.utils.TelephonyUtil.getOperatorStr(r4, r5)     // Catch:{ Exception -> 0x0171 }
            java.lang.String r7 = "operator"
            r3.put(r7, r5)     // Catch:{ Exception -> 0x0171 }
            long r7 = r33.getCurrentMonthTotalPackage()     // Catch:{ Exception -> 0x0171 }
            java.lang.String r5 = "settingPkg"
            r3.put(r5, r7)     // Catch:{ Exception -> 0x0171 }
            com.miui.networkassistant.config.CommonConfig r5 = r1.mConfig     // Catch:{ Exception -> 0x0171 }
            long r7 = r5.getUploadMonthReportUpdateTime()     // Catch:{ Exception -> 0x0171 }
            long r9 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0171 }
            boolean r5 = com.miui.networkassistant.utils.DateUtil.isTheSameMonth(r7, r9)     // Catch:{ Exception -> 0x0171 }
            r7 = 1
            r8 = 0
            if (r5 != 0) goto L_0x0080
            com.miui.networkassistant.config.CommonConfig r5 = r1.mConfig     // Catch:{ Exception -> 0x0171 }
            long r10 = r5.getUploadMonthReportUpdateTime()     // Catch:{ Exception -> 0x0171 }
            int r5 = (r10 > r8 ? 1 : (r10 == r8 ? 0 : -1))
            if (r5 != 0) goto L_0x0077
            goto L_0x0080
        L_0x0077:
            long r10 = com.miui.networkassistant.utils.DateUtil.getPreMonthTimeMillis()     // Catch:{ Exception -> 0x0171 }
            long r12 = com.miui.networkassistant.utils.DateUtil.getThisMonthEndTimeMillis(r7)     // Catch:{ Exception -> 0x0171 }
            goto L_0x0088
        L_0x0080:
            long r10 = com.miui.networkassistant.utils.DateUtil.getThisMonthBeginTimeMillis(r7)     // Catch:{ Exception -> 0x0171 }
            long r12 = com.miui.networkassistant.utils.DateUtil.getTodayTimeMillis()     // Catch:{ Exception -> 0x0171 }
        L_0x0088:
            long r14 = com.miui.networkassistant.utils.DateUtil.getYesterdayTimeMillis()     // Catch:{ Exception -> 0x0171 }
            long r8 = com.miui.networkassistant.utils.DateUtil.getTodayTimeMillis()     // Catch:{ Exception -> 0x0171 }
            boolean r5 = r2.isLeisureDataUsageEffective()     // Catch:{ Exception -> 0x0171 }
            if (r5 == 0) goto L_0x00b8
            long r16 = r2.getLeisureDataUsageTotal()     // Catch:{ Exception -> 0x0171 }
            long r18 = r0.getInternalLeisureUsed(r14, r8)     // Catch:{ Exception -> 0x0171 }
            long r20 = r0.getInternalLeisureUsed(r10, r12)     // Catch:{ Exception -> 0x0171 }
            long r22 = r2.getLeisureDataUsageFromTime()     // Catch:{ Exception -> 0x0171 }
            long r24 = r2.getLeisureDataUsageToTime()     // Catch:{ Exception -> 0x0171 }
            r7 = r3
            r34 = r6
            r26 = r16
            r5 = r18
            r2 = r20
            r28 = r22
            r30 = r24
            goto L_0x00c5
        L_0x00b8:
            r7 = r3
            r34 = r6
            r2 = 0
            r5 = 0
            r26 = 0
            r28 = 0
            r30 = 0
        L_0x00c5:
            long r16 = r0.getDataUsageByFromTo(r14, r8)     // Catch:{ Exception -> 0x016f }
            r18 = r8
            long r8 = r16 - r5
            long r16 = r0.getDataUsageByFromTo(r10, r12)     // Catch:{ Exception -> 0x016f }
            r20 = r10
            long r10 = r16 - r2
            r16 = r12
            java.lang.String r12 = "yesterdayTrafficUsed"
            long r8 = r1.secDiffPrivLaplace(r8)     // Catch:{ Exception -> 0x016f }
            r7.put(r12, r8)     // Catch:{ Exception -> 0x016f }
            java.lang.String r8 = "monthTrafficUsed"
            long r9 = r1.secDiffPrivLaplace(r10)     // Catch:{ Exception -> 0x016f }
            r7.put(r8, r9)     // Catch:{ Exception -> 0x016f }
            java.lang.String r8 = "leisurePkg"
            r9 = r26
            r7.put(r8, r9)     // Catch:{ Exception -> 0x016f }
            java.lang.String r8 = "yesterdayLeisureUsed"
            long r5 = r1.secDiffPrivLaplace(r5)     // Catch:{ Exception -> 0x016f }
            r7.put(r8, r5)     // Catch:{ Exception -> 0x016f }
            java.lang.String r5 = "monthLeisureUsed"
            long r2 = r1.secDiffPrivLaplace(r2)     // Catch:{ Exception -> 0x016f }
            r7.put(r5, r2)     // Catch:{ Exception -> 0x016f }
            java.lang.String r2 = "leisureFrom"
            r5 = r28
            r7.put(r2, r5)     // Catch:{ Exception -> 0x016f }
            java.lang.String r2 = "leisureTo"
            r5 = r30
            r7.put(r2, r5)     // Catch:{ Exception -> 0x016f }
            com.miui.networkassistant.config.DataUsageIgnoreAppListConfig r0 = r0.mIgnoreAppListConfig     // Catch:{ Exception -> 0x016f }
            java.util.ArrayList r0 = r0.getIgnoreList()     // Catch:{ Exception -> 0x016f }
            org.json.JSONArray r2 = new org.json.JSONArray     // Catch:{ Exception -> 0x016f }
            r2.<init>()     // Catch:{ Exception -> 0x016f }
            if (r0 == 0) goto L_0x0131
            java.util.Iterator r0 = r0.iterator()     // Catch:{ Exception -> 0x016f }
        L_0x0121:
            boolean r3 = r0.hasNext()     // Catch:{ Exception -> 0x016f }
            if (r3 == 0) goto L_0x0131
            java.lang.Object r3 = r0.next()     // Catch:{ Exception -> 0x016f }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ Exception -> 0x016f }
            r2.put(r3)     // Catch:{ Exception -> 0x016f }
            goto L_0x0121
        L_0x0131:
            java.lang.String r0 = "ignore"
            r7.put(r0, r2)     // Catch:{ Exception -> 0x016f }
            com.miui.networkassistant.traffic.statistic.StatisticAppTraffic r0 = new com.miui.networkassistant.traffic.statistic.StatisticAppTraffic     // Catch:{ Exception -> 0x016f }
            android.content.Context r2 = r1.mContext     // Catch:{ Exception -> 0x016f }
            r0.<init>(r2, r4)     // Catch:{ Exception -> 0x016f }
            r2 = r18
            long r2 = r0.getNetworkWifiTotalBytes(r14, r2)     // Catch:{ Exception -> 0x016f }
            java.lang.String r5 = "yesterdayWifiUsed"
            long r2 = r1.secDiffPrivLaplace(r2)     // Catch:{ Exception -> 0x016f }
            r7.put(r5, r2)     // Catch:{ Exception -> 0x016f }
            r12 = r16
            r10 = r20
            long r2 = r0.getNetworkWifiTotalBytes(r10, r12)     // Catch:{ Exception -> 0x016f }
            java.lang.String r5 = "monthWifiUsed"
            long r2 = r1.secDiffPrivLaplace(r2)     // Catch:{ Exception -> 0x016f }
            r7.put(r5, r2)     // Catch:{ Exception -> 0x016f }
            r0.closeSession()     // Catch:{ Exception -> 0x016f }
            org.json.JSONArray r6 = r1.getAllAppsJsonString(r4)     // Catch:{ Exception -> 0x016f }
            java.lang.String r0 = "appRank"
            if (r6 == 0) goto L_0x0169
            goto L_0x016b
        L_0x0169:
            r6 = r34
        L_0x016b:
            r7.put(r0, r6)     // Catch:{ Exception -> 0x016f }
            goto L_0x0176
        L_0x016f:
            r0 = move-exception
            goto L_0x0173
        L_0x0171:
            r0 = move-exception
            r7 = r3
        L_0x0173:
            r0.printStackTrace()
        L_0x0176:
            return r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.service.tm.DataUsageReportManager.getUploadDataUsage(com.miui.networkassistant.service.tm.TrafficSimManager, int):org.json.JSONObject");
    }

    private void handleDisconnected(long j, long j2) {
        this.disconnectedTime = j;
        long j3 = this.connectedTime;
        long j4 = j3 < j2 ? this.disconnectedTime - j2 : this.disconnectedTime - j3;
        long j5 = 0;
        f.a aVar = this.mLastState;
        if (aVar == f.a.MobileConnected) {
            j5 = this.mConfig.getMobileDailyConnectedTime();
            this.mConfig.setMobileDailyConnectedTime(j5 + j4);
        } else if (aVar == f.a.WifiConnected) {
            j5 = this.mConfig.getWifiDailyConnectedTime();
            this.mConfig.setWifiDailyConnectedTime(j5 + j4);
        }
        String str = TAG;
        Log.d(str, "handleDisconnected: " + j4 + ", " + j5);
    }

    private boolean isUploaded() {
        long uploadMonthReportUpdateTime = this.mConfig.getUploadMonthReportUpdateTime();
        long currentTimeMillis = System.currentTimeMillis();
        return uploadMonthReportUpdateTime > currentTimeMillis && uploadMonthReportUpdateTime - currentTimeMillis < 86400000;
    }

    private long secDiffPrivLaplace(long j) {
        c cVar = new c();
        cVar.b(Float.valueOf(1.0f));
        cVar.a(Float.valueOf(0.9f));
        cVar.a(Float.valueOf(0.0f), Float.valueOf(Float.MAX_VALUE));
        return (long) Math.round(((Float) cVar.b(Float.valueOf(Float.parseFloat(String.valueOf(j))))).floatValue());
    }

    public void trackNetworkStateAnalytics(f.a aVar) {
        long currentTimeMillis = System.currentTimeMillis();
        if (aVar != this.mLastState) {
            if (aVar == f.a.MobileConnected || aVar == f.a.WifiConnected) {
                f.a aVar2 = this.mLastState;
                if (aVar2 == f.a.MobileConnected || aVar2 == f.a.WifiConnected) {
                    handleDisconnected(currentTimeMillis, DateUtil.getTodayTimeMillis());
                }
                this.connectedTime = currentTimeMillis;
            } else if (aVar == f.a.Diconnected) {
                handleDisconnected(currentTimeMillis, DateUtil.getTodayTimeMillis());
            }
            this.mLastState = aVar;
        }
    }

    public void uploadTrafficDataDaily(TrafficSimManager[] trafficSimManagerArr) {
        if (!isUploaded()) {
            this.mTrafficManagers = trafficSimManagerArr;
            a.a(new Runnable() {
                public void run() {
                    try {
                        JSONObject jSONObject = new JSONObject();
                        JSONObject jSONObject2 = new JSONObject();
                        if (DataUsageReportManager.this.mSimCardHelper.isSim1Inserted()) {
                            jSONObject2.put("sim1", DataUsageReportManager.this.getUploadDataUsage(DataUsageReportManager.this.mTrafficManagers[0], 0));
                            jSONObject.put("sim1", DataUsageReportManager.this.getAppDetailDataUsage(DataUsageReportManager.this.mTrafficManagers[0]));
                        }
                        if (DataUsageReportManager.this.mSimCardHelper.isSim2Inserted()) {
                            jSONObject2.put("sim2", DataUsageReportManager.this.getUploadDataUsage(DataUsageReportManager.this.mTrafficManagers[1], 1));
                            jSONObject.put("sim2", DataUsageReportManager.this.getAppDetailDataUsage(DataUsageReportManager.this.mTrafficManagers[1]));
                        }
                        DataUsageReportManager.this.checkWifiAndMobileConnectedTime();
                        long wifiDailyConnectedTime = DataUsageReportManager.this.mConfig.getWifiDailyConnectedTime();
                        long mobileDailyConnectedTime = DataUsageReportManager.this.mConfig.getMobileDailyConnectedTime();
                        jSONObject2.put("wifiTime", wifiDailyConnectedTime);
                        jSONObject2.put("mobileTime", mobileDailyConnectedTime);
                        DataUsageReportManager.this.mConfig.setWifiDailyConnectedTime(0);
                        DataUsageReportManager.this.mConfig.setMobileDailyConnectedTime(0);
                        DataUsageReportManager.this.mConfig.setUploadMonthReportUpdateTime((DateUtil.getTodayTimeMillis() + 86400000) - 3000);
                        if (jSONObject2.length() > 0) {
                            DataUsageReportUtil.uploadDataUsageDaily(DataUsageReportManager.this.mContext, jSONObject2.toString());
                        }
                        if (jSONObject.length() > 0) {
                            DataUsageReportUtil.uploadDataUsageDetailDaily(DataUsageReportManager.this.mContext, jSONObject.toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }
}
