package com.miui.networkassistant.utils;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import miui.security.DigestUtils;
import miui.text.ExtraTextUtils;

public class DataUsageReportUtil {
    private static final int DAILY_TRAFFIC_DETAIL_EVENT_TYPE = 106;
    private static final int DAILY_TRAFFIC_DETAIL_EVENT_VERSION = 1;
    private static final int DAILY_TRAFFIC_EVENT_TYPE = 105;
    private static final int DAILY_TRAFFIC_EVENT_VERSION = 4;
    private static final String URL = "content://com.miui.monthreport/report_json";

    private static void insertDataUsageData(Context context, int i, String str, long j, int i2) {
        ContentValues contentValues = new ContentValues();
        String hexReadable = ExtraTextUtils.toHexReadable(DigestUtils.get(String.valueOf(i) + String.valueOf(j), "SHA-1"));
        contentValues.put("pkgName", context.getPackageName());
        contentValues.put("eventId", hexReadable);
        contentValues.put("eventType", Integer.valueOf(i));
        contentValues.put("eventTime", Long.valueOf(j));
        contentValues.put("version", Integer.valueOf(i2));
        contentValues.put(DataSchemeDataSource.SCHEME_DATA, str);
        try {
            context.getContentResolver().insert(Uri.parse(URL), contentValues);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void uploadDataUsageDaily(Context context, String str) {
        insertDataUsageData(context, 105, str, DateUtil.getYesterdayTimeMillis(), 4);
    }

    public static void uploadDataUsageDetailDaily(Context context, String str) {
        insertDataUsageData(context, 106, str, DateUtil.getYesterdayTimeMillis(), 1);
    }
}
