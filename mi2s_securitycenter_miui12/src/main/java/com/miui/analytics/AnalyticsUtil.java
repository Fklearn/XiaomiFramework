package com.miui.analytics;

import android.content.Context;
import android.text.TextUtils;
import com.miui.securitycenter.h;
import com.xiaomi.stat.MiStat;
import com.xiaomi.stat.MiStatParams;
import java.util.Map;
import miui.os.Build;

public class AnalyticsUtil {
    private static final String TAG = "AnalyticsUtil";

    private static String createEventName(String str, String str2) {
        return (str + "_" + str2).replace(".", "_");
    }

    public static void initMiStats(Context context) {
        MiStat.initialize(context, "2882303761517405262", "5971740546262", false, AnalyticsConstant.getChannel());
        if (Build.IS_INTERNATIONAL_BUILD) {
            MiStat.setInternationalRegion(true, Build.getRegion());
        }
        MiStat.setStatisticEnabled(h.i());
        MiStat.setExceptionCatcherEnabled(true);
        MiStat.setUploadInterval(360000);
    }

    public static void recordCalculateEvent(String str, String str2, long j) {
        trackEvent(createEventName(str, str2), str2, j);
    }

    public static void recordCalculateEvent(String str, String str2, long j, Map<String, String> map) {
        MiStatParams miStatParams = new MiStatParams();
        miStatParams.putLong(str2, j);
        if (map != null && map.size() > 0) {
            for (Map.Entry next : map.entrySet()) {
                miStatParams.putString((String) next.getKey(), (String) next.getValue());
            }
        }
        MiStat.trackEvent(createEventName(str, str2), miStatParams);
    }

    public static void recordCalculateEvent(String str, String str2, Map<String, String> map) {
        MiStatParams miStatParams = new MiStatParams();
        if (map != null && map.size() > 0) {
            for (Map.Entry next : map.entrySet()) {
                miStatParams.putString((String) next.getKey(), (String) next.getValue());
            }
        }
        MiStat.trackEvent(createEventName(str, str2), miStatParams);
    }

    public static void recordCountEvent(String str, String str2) {
        trackEvent(createEventName(str, str2));
    }

    public static void recordCountEvent(String str, String str2, Map<String, String> map) {
        trackEvent(createEventName(str, str2), map);
    }

    public static void recordNumericEvent(String str, String str2, long j) {
        trackEvent(createEventName(str, str2), str2, j);
    }

    public static void recordPageEnd(String str) {
        MiStat.trackPageEnd(str, (MiStatParams) null);
    }

    public static void recordPageStart(String str) {
        MiStat.trackPageStart(str);
    }

    public static void recordStringPropertyEvent(String str, String str2, String str3) {
        trackEvent(createEventName(str, str2), str2, str3);
    }

    public static void setDataUploadingEnabled(boolean z) {
        MiStat.setStatisticEnabled(z);
    }

    public static void trackEvent(String str) {
        MiStat.trackEvent(str);
    }

    public static void trackEvent(String str, String str2, double d2) {
        MiStatParams miStatParams = new MiStatParams();
        if (!TextUtils.isEmpty(str2)) {
            miStatParams.putDouble(str2, d2);
        }
        MiStat.trackEvent(str, miStatParams);
    }

    public static void trackEvent(String str, String str2, int i) {
        MiStatParams miStatParams = new MiStatParams();
        if (!TextUtils.isEmpty(str2)) {
            miStatParams.putInt(str2, i);
        }
        MiStat.trackEvent(str, miStatParams);
    }

    public static void trackEvent(String str, String str2, long j) {
        MiStatParams miStatParams = new MiStatParams();
        if (!TextUtils.isEmpty(str2)) {
            miStatParams.putLong(str2, j);
        }
        MiStat.trackEvent(str, miStatParams);
    }

    public static void trackEvent(String str, String str2, String str3) {
        MiStatParams miStatParams = new MiStatParams();
        if (!TextUtils.isEmpty(str2)) {
            miStatParams.putString(str2, str3);
        }
        MiStat.trackEvent(str, miStatParams);
    }

    public static void trackEvent(String str, String str2, String str3, long j) {
        MiStatParams miStatParams = new MiStatParams();
        if (!TextUtils.isEmpty(str3)) {
            miStatParams.putLong(str3, j);
        }
        MiStat.trackEvent(str, str2, miStatParams);
    }

    public static void trackEvent(String str, Map<String, String> map) {
        MiStatParams miStatParams = new MiStatParams();
        if (map != null && map.size() > 0) {
            for (Map.Entry next : map.entrySet()) {
                miStatParams.putString((String) next.getKey(), (String) next.getValue());
            }
        }
        MiStat.trackEvent(str, miStatParams);
    }

    public static void trackException(Throwable th) {
        MiStat.trackException(th);
    }

    public static void triggerUpload() {
    }
}
