package com.miui.networkassistant.traffic.statistic;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import b.b.c.c.a.a;
import com.miui.networkassistant.utils.DateUtil;

public class MiServiceFrameworkHelper {
    private static final String IMSI = "imsi";
    /* access modifiers changed from: private */
    public static final Uri UPDATE_IMSI_URI = Uri.parse("content://com.xiaomi.push.providers.TrafficProvider/update_imsi");
    private static final Uri sMiServiceUri = Uri.parse("content://com.xiaomi.push.providers.TrafficProvider/traffic");
    private Context mContext;
    private long mFirstDayofMonth;
    private long mLastMonth;
    private long mNow;
    private long mThisWeek;
    private long mToday;
    private long mTotalTraffic;
    private long mYesterday;

    public MiServiceFrameworkHelper(Context context) {
        this.mContext = context;
        initDateData();
    }

    private void initDateData() {
        this.mNow = DateUtil.getNowTimeMillis();
        this.mToday = DateUtil.getTodayTimeMillis();
        this.mFirstDayofMonth = DateUtil.getThisMonthBeginTimeMillis(1);
        this.mLastMonth = DateUtil.getLastMonthBeginTimeMillis(1);
        this.mYesterday = DateUtil.getYesterdayTimeMillis();
        this.mThisWeek = DateUtil.getThisWeekBeginTimeMillis();
    }

    public static void updateSim(final Context context, final String str) {
        a.a(new Runnable() {
            public void run() {
                try {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("imsi", str);
                    context.getContentResolver().update(MiServiceFrameworkHelper.UPDATE_IMSI_URI, contentValues, (String) null, (String[]) null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public long getTotalTraffic() {
        return this.mTotalTraffic;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:29:0x00d0, code lost:
        if (r11 == null) goto L_0x00de;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00d9, code lost:
        if (r11 != null) goto L_0x00db;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00db, code lost:
        r11.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00e7, code lost:
        return new java.util.ArrayList<>(r12.values());
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.ArrayList<com.miui.networkassistant.ui.adapter.MIServiceAppDetailListAdapter.MiAppInfo> query(int r11, int r12, java.lang.String r13) {
        /*
            r10 = this;
            r0 = 2
            r1 = 3
            r2 = 0
            r3 = 1
            if (r12 != r3) goto L_0x0011
            java.lang.String[] r12 = new java.lang.String[r1]
            java.lang.String r13 = java.lang.String.valueOf(r3)
            r12[r0] = r13
            java.lang.String r13 = "message_ts > ? and message_ts < ? and network_type = ?"
            goto L_0x001e
        L_0x0011:
            r12 = 4
            java.lang.String[] r12 = new java.lang.String[r12]
            java.lang.String r4 = java.lang.String.valueOf(r2)
            r12[r0] = r4
            r12[r1] = r13
            java.lang.String r13 = "message_ts > ? and message_ts < ? and network_type = ? and imsi = ?"
        L_0x001e:
            r8 = r12
            r7 = r13
            if (r11 == 0) goto L_0x005c
            if (r11 == r3) goto L_0x004b
            if (r11 == r0) goto L_0x003a
            if (r11 == r1) goto L_0x0029
            goto L_0x0081
        L_0x0029:
            long r11 = r10.mFirstDayofMonth
            java.lang.String r11 = java.lang.String.valueOf(r11)
            r8[r2] = r11
            long r11 = r10.mNow
            java.lang.String r11 = java.lang.String.valueOf(r11)
            r8[r3] = r11
            goto L_0x0081
        L_0x003a:
            long r11 = r10.mLastMonth
            java.lang.String r11 = java.lang.String.valueOf(r11)
            r8[r2] = r11
            long r11 = r10.mFirstDayofMonth
            java.lang.String r11 = java.lang.String.valueOf(r11)
            r8[r3] = r11
            goto L_0x0081
        L_0x004b:
            long r11 = r10.mToday
            java.lang.String r11 = java.lang.String.valueOf(r11)
            r8[r2] = r11
            long r11 = r10.mNow
            java.lang.String r11 = java.lang.String.valueOf(r11)
            r8[r3] = r11
            goto L_0x0081
        L_0x005c:
            boolean r11 = com.miui.networkassistant.utils.DeviceUtil.IS_CM_CUSTOMIZATION_TEST
            if (r11 == 0) goto L_0x0071
            long r11 = r10.mThisWeek
            java.lang.String r11 = java.lang.String.valueOf(r11)
            r8[r2] = r11
            long r11 = r10.mNow
            java.lang.String r11 = java.lang.String.valueOf(r11)
            r8[r3] = r11
            goto L_0x0081
        L_0x0071:
            long r11 = r10.mYesterday
            java.lang.String r11 = java.lang.String.valueOf(r11)
            r8[r2] = r11
            long r11 = r10.mToday
            java.lang.String r11 = java.lang.String.valueOf(r11)
            r8[r3] = r11
        L_0x0081:
            r11 = 0
            r10.mTotalTraffic = r11
            android.content.Context r11 = r10.mContext
            android.content.ContentResolver r4 = r11.getContentResolver()
            android.net.Uri r5 = sMiServiceUri
            r6 = 0
            r9 = 0
            android.database.Cursor r11 = r4.query(r5, r6, r7, r8, r9)
            java.util.HashMap r12 = new java.util.HashMap
            r12.<init>()
            if (r11 == 0) goto L_0x00d9
        L_0x009a:
            boolean r13 = r11.moveToNext()     // Catch:{ Exception -> 0x00cc }
            if (r13 == 0) goto L_0x00d9
            java.lang.String r13 = r11.getString(r3)     // Catch:{ Exception -> 0x00cc }
            long r4 = r11.getLong(r1)     // Catch:{ Exception -> 0x00cc }
            long r6 = r10.mTotalTraffic     // Catch:{ Exception -> 0x00cc }
            long r6 = r6 + r4
            r10.mTotalTraffic = r6     // Catch:{ Exception -> 0x00cc }
            boolean r0 = r12.containsKey(r13)     // Catch:{ Exception -> 0x00cc }
            if (r0 == 0) goto L_0x00bd
            java.lang.Object r13 = r12.get(r13)     // Catch:{ Exception -> 0x00cc }
            com.miui.networkassistant.ui.adapter.MIServiceAppDetailListAdapter$MiAppInfo r13 = (com.miui.networkassistant.ui.adapter.MIServiceAppDetailListAdapter.MiAppInfo) r13     // Catch:{ Exception -> 0x00cc }
            r13.addTraffic(r4)     // Catch:{ Exception -> 0x00cc }
            goto L_0x009a
        L_0x00bd:
            com.miui.networkassistant.ui.adapter.MIServiceAppDetailListAdapter$MiAppInfo r0 = new com.miui.networkassistant.ui.adapter.MIServiceAppDetailListAdapter$MiAppInfo     // Catch:{ Exception -> 0x00cc }
            r0.<init>()     // Catch:{ Exception -> 0x00cc }
            r0.packageName = r13     // Catch:{ Exception -> 0x00cc }
            r0.totalTraffic = r4     // Catch:{ Exception -> 0x00cc }
            r12.put(r13, r0)     // Catch:{ Exception -> 0x00cc }
            goto L_0x009a
        L_0x00ca:
            r12 = move-exception
            goto L_0x00d3
        L_0x00cc:
            r13 = move-exception
            r13.printStackTrace()     // Catch:{ all -> 0x00ca }
            if (r11 == 0) goto L_0x00de
            goto L_0x00db
        L_0x00d3:
            if (r11 == 0) goto L_0x00d8
            r11.close()
        L_0x00d8:
            throw r12
        L_0x00d9:
            if (r11 == 0) goto L_0x00de
        L_0x00db:
            r11.close()
        L_0x00de:
            java.util.ArrayList r11 = new java.util.ArrayList
            java.util.Collection r12 = r12.values()
            r11.<init>(r12)
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.networkassistant.traffic.statistic.MiServiceFrameworkHelper.query(int, int, java.lang.String):java.util.ArrayList");
    }
}
