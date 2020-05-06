package com.miui.securitycenter.dynamic;

import android.os.SystemClock;
import com.miui.analytics.AnalyticsUtil;
import java.util.Map;
import java.util.concurrent.TimeUnit;

class EventTrack {
    private static final String CATEGORY = "dynamic";
    private static final String KEY_VERSION = "version";
    private static long sLastTrackTime;

    EventTrack() {
    }

    public static void recordCountEvent(String str) {
        AnalyticsUtil.recordCountEvent(CATEGORY, str);
    }

    public static void recordCountEvent(String str, Map<String, String> map) {
        AnalyticsUtil.recordCountEvent(CATEGORY, str, map);
    }

    public static void recordStringPropertyEvent(String str, String str2) {
        AnalyticsUtil.recordStringPropertyEvent(CATEGORY, str, str2);
    }

    public static void track(Throwable th) {
        if (sLastTrackTime == 0 || SystemClock.uptimeMillis() - sLastTrackTime >= TimeUnit.MINUTES.toMillis(5)) {
            sLastTrackTime = SystemClock.uptimeMillis();
            AnalyticsUtil.trackException(th);
        }
    }

    public static void trackVersion(int i) {
        AnalyticsUtil.recordNumericEvent(CATEGORY, KEY_VERSION, (long) i);
    }
}
