package com.miui.powercenter.batteryhistory.b;

import android.text.TextUtils;
import b.b.o.g.e;
import com.miui.powercenter.batteryhistory.BatteryHistogramItem;
import com.miui.powercenter.batteryhistory.aa;
import com.miui.powercenter.legacypowerrank.BatteryData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miui.securitycenter.powercenter.BatteryHistoryHelper;
import miui.securitycenter.powercenter.HistoryItemWrapper;
import org.json.JSONException;
import org.json.JSONObject;

public class a {
    public static long a() {
        try {
            BatteryHistoryHelper batteryHistoryHelper = new BatteryHistoryHelper();
            batteryHistoryHelper.refreshHistory();
            Object a2 = e.a((Object) batteryHistoryHelper, "mStats");
            if (a2 != null) {
                return ((Long) e.a(a2, "mHistoryBaseTime", Long.TYPE)).longValue();
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static BatteryHistogramItem a(long j, long j2, String str, List<BatteryData> list) {
        BatteryHistogramItem batteryHistogramItem = new BatteryHistogramItem();
        batteryHistogramItem.type = 0;
        batteryHistogramItem.startTime = j;
        batteryHistogramItem.endTime = j2;
        batteryHistogramItem.histogramDataStr = a(j, j2, list);
        batteryHistogramItem.batteryDataStr = str;
        return batteryHistogramItem;
    }

    private static String a(long j, long j2, List<BatteryData> list) {
        long j3 = 0;
        double d2 = 0.0d;
        long j4 = 0;
        for (BatteryData next : list) {
            d2 += next.value;
            int i = next.drainType;
            if (i == 5) {
                j3 = next.usageTime;
            } else if (i == 0) {
                j4 = next.usageTime;
            }
        }
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("start_time", j);
            jSONObject.put("end_time", j2);
            jSONObject.put("total_consume", d2);
            jSONObject.put("screen_usagetime", j3);
            jSONObject.put("idle_usagetime", j4);
            return jSONObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String a(BatteryData batteryData) {
        return batteryData.uid + ":" + batteryData.drainType + ":" + batteryData.name + ":" + batteryData.defaultPackageName;
    }

    public static List<BatteryData> a(List<BatteryData> list, List<BatteryData> list2) {
        if (list2 == null || list2.size() == 0) {
            return list;
        }
        ArrayList arrayList = new ArrayList(list.size());
        HashMap hashMap = new HashMap();
        for (BatteryData next : list2) {
            hashMap.put(a(next), next);
        }
        for (BatteryData next2 : list) {
            String a2 = a(next2);
            BatteryData batteryData = new BatteryData(next2);
            BatteryData batteryData2 = (BatteryData) hashMap.get(a2);
            if (batteryData2 != null) {
                batteryData.value -= batteryData2.value;
                batteryData.usageTime -= batteryData2.usageTime;
                batteryData.cpuTime -= batteryData2.cpuTime;
                batteryData.gpsTime -= batteryData2.gpsTime;
                batteryData.wifiRunningTime -= batteryData2.wifiRunningTime;
                batteryData.cpuFgTime -= batteryData2.cpuFgTime;
                batteryData.wakeLockTime -= batteryData2.wakeLockTime;
                batteryData.mobileRxBytes -= batteryData2.mobileRxBytes;
                batteryData.mobileTxBytes -= batteryData2.mobileTxBytes;
                batteryData.noCoveragePercent -= batteryData2.noCoveragePercent;
            }
            arrayList.add(batteryData);
        }
        return arrayList;
    }

    public static void a(BatteryHistogramItem batteryHistogramItem) {
        if (batteryHistogramItem.type == 0 && !TextUtils.isEmpty(batteryHistogramItem.histogramDataStr)) {
            try {
                JSONObject jSONObject = new JSONObject(batteryHistogramItem.histogramDataStr);
                batteryHistogramItem.startTime = jSONObject.getLong("start_time");
                batteryHistogramItem.endTime = jSONObject.getLong("end_time");
                batteryHistogramItem.totalConsume = jSONObject.getDouble("total_consume");
                batteryHistogramItem.screenUsageTime = jSONObject.getLong("screen_usagetime");
                batteryHistogramItem.idleUsageTime = jSONObject.getLong("idle_usagetime");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean a(List<BatteryHistogramItem> list) {
        if (!(list == null || list == null || list.size() <= 0)) {
            for (BatteryHistogramItem batteryHistogramItem : list) {
                if (batteryHistogramItem.totalConsume < 0.0d) {
                    return false;
                }
            }
        }
        return true;
    }

    public static List<BatteryData> b(List<BatteryData> list, List<BatteryData> list2) {
        if (list2 == null || list2.size() == 0) {
            return list;
        }
        if (list == null || list.size() == 0) {
            return list2;
        }
        ArrayList arrayList = new ArrayList(list.size());
        HashMap hashMap = new HashMap();
        for (BatteryData next : list2) {
            hashMap.put(a(next), next);
        }
        for (BatteryData next2 : list) {
            String a2 = a(next2);
            BatteryData batteryData = new BatteryData(next2);
            BatteryData batteryData2 = (BatteryData) hashMap.get(a2);
            if (batteryData2 != null) {
                batteryData.value += batteryData2.value;
                batteryData.usageTime += batteryData2.usageTime;
                batteryData.cpuTime += batteryData2.cpuTime;
                batteryData.gpsTime += batteryData2.gpsTime;
                batteryData.wifiRunningTime += batteryData2.wifiRunningTime;
                batteryData.cpuFgTime += batteryData2.cpuFgTime;
                batteryData.wakeLockTime += batteryData2.wakeLockTime;
                batteryData.mobileRxBytes += batteryData2.mobileRxBytes;
                batteryData.mobileTxBytes += batteryData2.mobileTxBytes;
                batteryData.noCoveragePercent += batteryData2.noCoveragePercent;
            }
            arrayList.add(batteryData);
        }
        return arrayList;
    }

    public static Map<Long, aa> b() {
        HashMap hashMap = new HashMap();
        aa aaVar = null;
        long j = 0;
        try {
            BatteryHistoryHelper batteryHistoryHelper = new BatteryHistoryHelper();
            batteryHistoryHelper.refreshHistory();
            HistoryItemWrapper historyItemWrapper = new HistoryItemWrapper();
            if (batteryHistoryHelper.startIterate()) {
                if (batteryHistoryHelper.getNextHistoryItem(historyItemWrapper)) {
                    aaVar = new aa(historyItemWrapper);
                }
                batteryHistoryHelper.finishIterate();
            }
            Object a2 = e.a((Object) batteryHistoryHelper, "mStats");
            if (a2 != null) {
                j = ((Long) e.a(a2, "mHistoryBaseTime", Long.TYPE)).longValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        hashMap.put(Long.valueOf(j), aaVar);
        return hashMap;
    }
}
