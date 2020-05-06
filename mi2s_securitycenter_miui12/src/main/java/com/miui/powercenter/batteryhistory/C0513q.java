package com.miui.powercenter.batteryhistory;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.google.gson.Gson;
import com.miui.activityutil.o;
import com.miui.powercenter.batteryhistory.b.a;
import com.miui.powercenter.legacypowerrank.BatteryData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* renamed from: com.miui.powercenter.batteryhistory.q  reason: case insensitive filesystem */
class C0513q {

    /* renamed from: a  reason: collision with root package name */
    private static volatile C0513q f6917a;

    /* renamed from: b  reason: collision with root package name */
    private Context f6918b;

    /* renamed from: c  reason: collision with root package name */
    private r f6919c;

    /* renamed from: d  reason: collision with root package name */
    private SharedPreferences f6920d;
    private Gson e = new Gson();

    private C0513q(Context context) {
        this.f6918b = context.getApplicationContext();
        this.f6919c = new r(this.f6918b);
        this.f6920d = this.f6918b.getSharedPreferences("batteryhistory_preferences", 0);
    }

    public static C0513q a(Context context) {
        if (f6917a == null) {
            synchronized (C0513q.class) {
                if (f6917a == null) {
                    f6917a = new C0513q(context);
                }
            }
        }
        return f6917a;
    }

    private List<BatteryData> a(List<BatteryData> list, List<BatteryData> list2) {
        ArrayList arrayList = new ArrayList();
        return (list == null || list2 == null) ? arrayList : a.a(list2, list);
    }

    private void a(long j, long j2) {
        SQLiteDatabase writableDatabase = this.f6919c.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put("shutdown_time", Long.valueOf(j));
            contentValues.put("shutdown_duration", Long.valueOf(j2));
            writableDatabase.insert("shutdown", (String) null, contentValues);
            writableDatabase.setTransactionSuccessful();
        } catch (Exception e2) {
            Log.e("BatteryHistory", "insertShutdownTime exception ", e2);
        } catch (Throwable th) {
            writableDatabase.endTransaction();
            throw th;
        }
        writableDatabase.endTransaction();
    }

    private Map<Long, List<BatteryData>> p() {
        HashMap hashMap = new HashMap();
        Cursor query = this.f6919c.getReadableDatabase().query("history", (String[]) null, (String) null, (String[]) null, (String) null, (String) null, "time DESC", o.f2310b);
        if (query != null && query.moveToFirst()) {
            String string = query.getString(query.getColumnIndex(DataSchemeDataSource.SCHEME_DATA));
            long j = query.getLong(query.getColumnIndex("time"));
            query.close();
            hashMap.put(Long.valueOf(j), (List) this.e.fromJson(string, new C0512p(this).getType()));
        }
        return hashMap;
    }

    public void a() {
        SQLiteDatabase writableDatabase = this.f6919c.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete("shutdown", (String) null, (String[]) null);
            writableDatabase.delete("history", (String) null, (String[]) null);
            writableDatabase.delete("histogram", (String) null, (String[]) null);
            writableDatabase.setTransactionSuccessful();
        } catch (Exception e2) {
            Log.e("BatteryHistory", "clearHistoryData exception ", e2);
        } catch (Throwable th) {
            writableDatabase.endTransaction();
            throw th;
        }
        writableDatabase.endTransaction();
    }

    public void a(long j) {
        SQLiteDatabase writableDatabase = this.f6919c.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            writableDatabase.delete("history", "time<=?", new String[]{String.valueOf(j)});
            writableDatabase.delete("histogram", "start_time<=?", new String[]{String.valueOf(j)});
            writableDatabase.delete("shutdown", "shutdown_time<=?", new String[]{String.valueOf(j)});
            writableDatabase.setTransactionSuccessful();
        } catch (Exception e2) {
            Log.e("BatteryHistory", "clearHistoryLeqTime exception ", e2);
        } catch (Throwable th) {
            writableDatabase.endTransaction();
            throw th;
        }
        writableDatabase.endTransaction();
    }

    public void a(long j, long j2, long j3, List<BatteryData> list) {
        long j4;
        ContentValues contentValues;
        String str;
        long j5 = j3;
        List<BatteryData> list2 = list;
        SQLiteDatabase writableDatabase = this.f6919c.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            a(j2, j5);
            Map<Long, List<BatteryData>> p = p();
            long longValue = ((Long) new ArrayList(p.keySet()).get(0)).longValue();
            BatteryHistogramItem d2 = d();
            long j6 = (j - longValue) + j5;
            if (d2 != null) {
                j6 += d2.shutdownDuration;
            }
            long currentTimeMillis = System.currentTimeMillis();
            String a2 = C0519x.a(currentTimeMillis);
            long j7 = j6;
            BatteryHistogramItem batteryHistogramItem = d2;
            String str2 = "start_time";
            String str3 = "id=?";
            String str4 = "type";
            if (Math.abs(j6 - 3600000) <= 60000) {
                List<BatteryData> a3 = a(p.get(Long.valueOf(longValue)), list2);
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put("time", Long.valueOf(j));
                contentValues2.put("utc_time", Long.valueOf(currentTimeMillis));
                contentValues2.put("utc_time_display", a2);
                contentValues2.put(DataSchemeDataSource.SCHEME_DATA, this.e.toJson((Object) list2));
                writableDatabase.insert("history", (String) null, contentValues2);
                BatteryHistogramItem a4 = a.a(longValue, j, this.e.toJson((Object) a3), a3);
                ContentValues contentValues3 = new ContentValues();
                contentValues3.put(str4, Integer.valueOf(a4.type));
                contentValues3.put(str2, Long.valueOf(a4.startTime));
                contentValues3.put("end_time", Long.valueOf(a4.endTime));
                contentValues3.put("histogram_data", a4.histogramDataStr);
                contentValues3.put("battery_data", a4.batteryDataStr);
                String str5 = "histogram";
                writableDatabase.insert(str5, (String) null, contentValues3);
                if (batteryHistogramItem != null) {
                    writableDatabase.delete(str5, str3, new String[]{String.valueOf(batteryHistogramItem.id)});
                }
            } else {
                String str6 = str3;
                String str7 = "history";
                String str8 = "histogram";
                String str9 = str4;
                BatteryHistogramItem batteryHistogramItem2 = batteryHistogramItem;
                String str10 = DataSchemeDataSource.SCHEME_DATA;
                String str11 = "utc_time_display";
                if (j7 >= 3600000) {
                    String str12 = str10;
                    String str13 = "shutdown_duration";
                    if (batteryHistogramItem2 != null) {
                        j4 = currentTimeMillis;
                        j3 += batteryHistogramItem2.shutdownDuration;
                        writableDatabase.delete(str8, str6, new String[]{String.valueOf(batteryHistogramItem2.id)});
                    } else {
                        j4 = currentTimeMillis;
                    }
                    List<BatteryData> a5 = a(p.get(Long.valueOf(longValue)), list2);
                    ContentValues contentValues4 = new ContentValues();
                    contentValues4.put("time", Long.valueOf(j));
                    contentValues4.put("utc_time", Long.valueOf(j4));
                    contentValues4.put(str11, a2);
                    contentValues4.put(str12, this.e.toJson((Object) list2));
                    writableDatabase.insert(str7, (String) null, contentValues4);
                    String str14 = str2;
                    BatteryHistogramItem a6 = a.a(longValue, j, this.e.toJson((Object) a5), a5);
                    ContentValues contentValues5 = new ContentValues();
                    contentValues5.put(str9, Integer.valueOf(a6.type));
                    contentValues5.put(str14, Long.valueOf(a6.startTime));
                    contentValues5.put("end_time", Long.valueOf(a6.endTime));
                    contentValues5.put("histogram_data", a6.histogramDataStr);
                    contentValues5.put("battery_data", a6.batteryDataStr);
                    writableDatabase.insert(str8, (String) null, contentValues5);
                    long j8 = j3 - (3600000 - (a6.endTime - a6.startTime));
                    while (j8 >= 3600000) {
                        j8 -= 3600000;
                        ContentValues contentValues6 = new ContentValues();
                        contentValues6.put(str9, 1);
                        longValue++;
                        contentValues6.put(str14, Long.valueOf(longValue));
                        String str15 = str13;
                        contentValues6.put(str15, 3600000L);
                        writableDatabase.insert(str8, (String) null, contentValues6);
                        str13 = str15;
                    }
                    String str16 = str13;
                    if (j8 > 0) {
                        contentValues = new ContentValues();
                        contentValues.put(str9, 2);
                        contentValues.put(str16, Long.valueOf(j8));
                        str = null;
                    }
                } else if (batteryHistogramItem2 == null) {
                    contentValues = new ContentValues();
                    contentValues.put(str9, 2);
                    contentValues.put("shutdown_duration", Long.valueOf(j3));
                    str = null;
                } else {
                    batteryHistogramItem2.shutdownDuration += j3;
                    ContentValues contentValues7 = new ContentValues();
                    contentValues7.put("shutdown_duration", Long.valueOf(batteryHistogramItem2.shutdownDuration));
                    writableDatabase.update(str8, contentValues7, str6, new String[]{String.valueOf(batteryHistogramItem2.id)});
                }
                writableDatabase.insert(str8, str, contentValues);
            }
            writableDatabase.setTransactionSuccessful();
        } catch (Exception e2) {
            Log.e("BatteryHistory", "saveHistoryData exception ", e2);
        } catch (Throwable th) {
            writableDatabase.endTransaction();
            throw th;
        }
        writableDatabase.endTransaction();
    }

    public void a(long j, List<BatteryData> list) {
        List<BatteryData> list2 = list;
        String json = this.e.toJson((Object) list2);
        SQLiteDatabase writableDatabase = this.f6919c.getWritableDatabase();
        writableDatabase.beginTransaction();
        try {
            Cursor query = writableDatabase.query("history", C0518w.f6936a, (String) null, (String[]) null, (String) null, (String) null, "time ASC");
            int count = query != null ? query.getCount() : 0;
            long currentTimeMillis = System.currentTimeMillis();
            String a2 = C0519x.a(currentTimeMillis);
            if (count == 0) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("time", Long.valueOf(j));
                contentValues.put("utc_time", Long.valueOf(currentTimeMillis));
                contentValues.put("utc_time_display", a2);
                contentValues.put(DataSchemeDataSource.SCHEME_DATA, json);
                writableDatabase.insert("history", (String) null, contentValues);
            } else {
                Map<Long, List<BatteryData>> p = p();
                long longValue = ((Long) new ArrayList(p.keySet()).get(0)).longValue();
                List<BatteryData> a3 = a(p.get(Long.valueOf(longValue)), list2);
                ContentValues contentValues2 = new ContentValues();
                contentValues2.put("time", Long.valueOf(j));
                contentValues2.put("utc_time", Long.valueOf(currentTimeMillis));
                contentValues2.put("utc_time_display", a2);
                contentValues2.put(DataSchemeDataSource.SCHEME_DATA, json);
                writableDatabase.insert("history", (String) null, contentValues2);
                BatteryHistogramItem a4 = a.a(longValue, j, this.e.toJson((Object) a3), a3);
                ContentValues contentValues3 = new ContentValues();
                contentValues3.put("type", Integer.valueOf(a4.type));
                contentValues3.put("start_time", Long.valueOf(a4.startTime));
                contentValues3.put("end_time", Long.valueOf(a4.endTime));
                contentValues3.put("histogram_data", a4.histogramDataStr);
                contentValues3.put("battery_data", a4.batteryDataStr);
                writableDatabase.insert("histogram", (String) null, contentValues3);
                writableDatabase.delete("histogram", "type=?", new String[]{String.valueOf(2)});
            }
            writableDatabase.setTransactionSuccessful();
        } catch (Exception e2) {
            Log.e("BatteryHistory", "saveHistoryData exception ", e2);
        } catch (Throwable th) {
            writableDatabase.endTransaction();
            throw th;
        }
        writableDatabase.endTransaction();
    }

    /* JADX WARNING: Removed duplicated region for block: B:13:0x00b2 A[DONT_GENERATE] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.ArrayList<com.miui.powercenter.batteryhistory.BatteryHistogramItem> b() {
        /*
            r13 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r1 = 24
            r0.<init>(r1)
            r1 = 0
            com.miui.powercenter.batteryhistory.r r2 = r13.f6919c     // Catch:{ all -> 0x00b6 }
            android.database.sqlite.SQLiteDatabase r3 = r2.getReadableDatabase()     // Catch:{ all -> 0x00b6 }
            java.lang.String r4 = "histogram"
            r5 = 0
            java.lang.String r6 = "type!=?"
            r2 = 1
            java.lang.String[] r7 = new java.lang.String[r2]     // Catch:{ all -> 0x00b6 }
            r2 = 0
            r8 = 2
            java.lang.String r8 = java.lang.String.valueOf(r8)     // Catch:{ all -> 0x00b6 }
            r7[r2] = r8     // Catch:{ all -> 0x00b6 }
            r8 = 0
            r9 = 0
            java.lang.String r10 = "start_time ASC"
            android.database.Cursor r1 = r3.query(r4, r5, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x00b6 }
            if (r1 == 0) goto L_0x00b0
            boolean r2 = r1.moveToFirst()     // Catch:{ all -> 0x00b6 }
            if (r2 == 0) goto L_0x00b0
            java.lang.String r2 = "id"
            int r2 = r1.getColumnIndex(r2)     // Catch:{ all -> 0x00b6 }
            java.lang.String r3 = "type"
            int r3 = r1.getColumnIndex(r3)     // Catch:{ all -> 0x00b6 }
            java.lang.String r4 = "start_time"
            int r4 = r1.getColumnIndex(r4)     // Catch:{ all -> 0x00b6 }
            java.lang.String r5 = "end_time"
            int r5 = r1.getColumnIndex(r5)     // Catch:{ all -> 0x00b6 }
            java.lang.String r6 = "shutdown_duration"
            int r6 = r1.getColumnIndex(r6)     // Catch:{ all -> 0x00b6 }
            java.lang.String r7 = "histogram_data"
            int r7 = r1.getColumnIndex(r7)     // Catch:{ all -> 0x00b6 }
            java.lang.String r8 = "battery_data"
            int r8 = r1.getColumnIndex(r8)     // Catch:{ all -> 0x00b6 }
        L_0x0058:
            com.miui.powercenter.batteryhistory.BatteryHistogramItem r9 = new com.miui.powercenter.batteryhistory.BatteryHistogramItem     // Catch:{ all -> 0x00b6 }
            r9.<init>()     // Catch:{ all -> 0x00b6 }
            int r10 = r1.getInt(r2)     // Catch:{ all -> 0x00b6 }
            r9.id = r10     // Catch:{ all -> 0x00b6 }
            int r10 = r1.getInt(r3)     // Catch:{ all -> 0x00b6 }
            r9.type = r10     // Catch:{ all -> 0x00b6 }
            long r10 = r1.getLong(r4)     // Catch:{ all -> 0x00b6 }
            r9.startTime = r10     // Catch:{ all -> 0x00b6 }
            long r10 = r1.getLong(r5)     // Catch:{ all -> 0x00b6 }
            r9.endTime = r10     // Catch:{ all -> 0x00b6 }
            long r10 = r1.getLong(r6)     // Catch:{ all -> 0x00b6 }
            r9.shutdownDuration = r10     // Catch:{ all -> 0x00b6 }
            java.lang.String r10 = r1.getString(r7)     // Catch:{ all -> 0x00b6 }
            r9.histogramDataStr = r10     // Catch:{ all -> 0x00b6 }
            java.lang.String r10 = r1.getString(r8)     // Catch:{ all -> 0x00b6 }
            r9.batteryDataStr = r10     // Catch:{ all -> 0x00b6 }
            com.miui.powercenter.batteryhistory.b.a.a((com.miui.powercenter.batteryhistory.BatteryHistogramItem) r9)     // Catch:{ all -> 0x00b6 }
            java.lang.String r10 = r9.batteryDataStr     // Catch:{ all -> 0x00b6 }
            boolean r10 = android.text.TextUtils.isEmpty(r10)     // Catch:{ all -> 0x00b6 }
            if (r10 != 0) goto L_0x00a7
            com.google.gson.Gson r10 = r13.e     // Catch:{ all -> 0x00b6 }
            java.lang.String r11 = r9.batteryDataStr     // Catch:{ all -> 0x00b6 }
            com.miui.powercenter.batteryhistory.o r12 = new com.miui.powercenter.batteryhistory.o     // Catch:{ all -> 0x00b6 }
            r12.<init>(r13)     // Catch:{ all -> 0x00b6 }
            java.lang.reflect.Type r12 = r12.getType()     // Catch:{ all -> 0x00b6 }
            java.lang.Object r10 = r10.fromJson((java.lang.String) r11, (java.lang.reflect.Type) r12)     // Catch:{ all -> 0x00b6 }
            java.util.List r10 = (java.util.List) r10     // Catch:{ all -> 0x00b6 }
            r9.batteryDataList = r10     // Catch:{ all -> 0x00b6 }
        L_0x00a7:
            r0.add(r9)     // Catch:{ all -> 0x00b6 }
            boolean r9 = r1.moveToNext()     // Catch:{ all -> 0x00b6 }
            if (r9 != 0) goto L_0x0058
        L_0x00b0:
            if (r1 == 0) goto L_0x00b5
            r1.close()
        L_0x00b5:
            return r0
        L_0x00b6:
            r0 = move-exception
            if (r1 == 0) goto L_0x00bc
            r1.close()
        L_0x00bc:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.batteryhistory.C0513q.b():java.util.ArrayList");
    }

    public void b(long j) {
        this.f6920d.edit().putLong("key_resettime", j).commit();
    }

    /* JADX WARNING: Removed duplicated region for block: B:10:0x007b A[DONT_GENERATE] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<com.miui.powercenter.batteryhistory.BatteryHistogramItem> c() {
        /*
            r11 = this;
            java.util.ArrayList r0 = new java.util.ArrayList
            r1 = 24
            r0.<init>(r1)
            r1 = 0
            com.miui.powercenter.batteryhistory.r r2 = r11.f6919c     // Catch:{ all -> 0x007f }
            android.database.sqlite.SQLiteDatabase r3 = r2.getReadableDatabase()     // Catch:{ all -> 0x007f }
            java.lang.String r4 = "histogram"
            java.lang.String[] r5 = com.miui.powercenter.batteryhistory.C0507k.f6899a     // Catch:{ all -> 0x007f }
            java.lang.String r6 = "type!=?"
            r2 = 1
            java.lang.String[] r7 = new java.lang.String[r2]     // Catch:{ all -> 0x007f }
            r2 = 0
            r8 = 2
            java.lang.String r8 = java.lang.String.valueOf(r8)     // Catch:{ all -> 0x007f }
            r7[r2] = r8     // Catch:{ all -> 0x007f }
            r8 = 0
            r9 = 0
            java.lang.String r10 = "start_time ASC"
            android.database.Cursor r1 = r3.query(r4, r5, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x007f }
            if (r1 == 0) goto L_0x0079
            boolean r2 = r1.moveToFirst()     // Catch:{ all -> 0x007f }
            if (r2 == 0) goto L_0x0079
            java.lang.String r2 = "id"
            int r2 = r1.getColumnIndex(r2)     // Catch:{ all -> 0x007f }
            java.lang.String r3 = "type"
            int r3 = r1.getColumnIndex(r3)     // Catch:{ all -> 0x007f }
            java.lang.String r4 = "start_time"
            int r4 = r1.getColumnIndex(r4)     // Catch:{ all -> 0x007f }
            java.lang.String r5 = "end_time"
            int r5 = r1.getColumnIndex(r5)     // Catch:{ all -> 0x007f }
            java.lang.String r6 = "shutdown_duration"
            int r6 = r1.getColumnIndex(r6)     // Catch:{ all -> 0x007f }
        L_0x004d:
            com.miui.powercenter.batteryhistory.BatteryHistogramItem r7 = new com.miui.powercenter.batteryhistory.BatteryHistogramItem     // Catch:{ all -> 0x007f }
            r7.<init>()     // Catch:{ all -> 0x007f }
            int r8 = r1.getInt(r2)     // Catch:{ all -> 0x007f }
            r7.id = r8     // Catch:{ all -> 0x007f }
            int r8 = r1.getInt(r3)     // Catch:{ all -> 0x007f }
            r7.type = r8     // Catch:{ all -> 0x007f }
            long r8 = r1.getLong(r4)     // Catch:{ all -> 0x007f }
            r7.startTime = r8     // Catch:{ all -> 0x007f }
            long r8 = r1.getLong(r5)     // Catch:{ all -> 0x007f }
            r7.endTime = r8     // Catch:{ all -> 0x007f }
            long r8 = r1.getLong(r6)     // Catch:{ all -> 0x007f }
            r7.shutdownDuration = r8     // Catch:{ all -> 0x007f }
            r0.add(r7)     // Catch:{ all -> 0x007f }
            boolean r7 = r1.moveToNext()     // Catch:{ all -> 0x007f }
            if (r7 != 0) goto L_0x004d
        L_0x0079:
            if (r1 == 0) goto L_0x007e
            r1.close()
        L_0x007e:
            return r0
        L_0x007f:
            r0 = move-exception
            if (r1 == 0) goto L_0x0085
            r1.close()
        L_0x0085:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.batteryhistory.C0513q.c():java.util.List");
    }

    public void c(long j) {
        this.f6920d.edit().putLong("key_shutdowntime", j).commit();
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0066  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.miui.powercenter.batteryhistory.BatteryHistogramItem d() {
        /*
            r12 = this;
            r0 = 0
            com.miui.powercenter.batteryhistory.r r1 = r12.f6919c     // Catch:{ all -> 0x0060 }
            android.database.sqlite.SQLiteDatabase r2 = r1.getReadableDatabase()     // Catch:{ all -> 0x0060 }
            java.lang.String r3 = "histogram"
            java.lang.String[] r4 = com.miui.powercenter.batteryhistory.C0507k.f6899a     // Catch:{ all -> 0x0060 }
            java.lang.String r5 = "type=?"
            r1 = 1
            java.lang.String[] r6 = new java.lang.String[r1]     // Catch:{ all -> 0x0060 }
            r1 = 0
            r7 = 2
            java.lang.String r7 = java.lang.String.valueOf(r7)     // Catch:{ all -> 0x0060 }
            r6[r1] = r7     // Catch:{ all -> 0x0060 }
            r7 = 0
            r8 = 0
            r9 = 0
            java.lang.String r10 = "1"
            android.database.Cursor r1 = r2.query(r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ all -> 0x0060 }
            if (r1 == 0) goto L_0x005a
            boolean r2 = r1.moveToFirst()     // Catch:{ all -> 0x0058 }
            if (r2 == 0) goto L_0x005a
            java.lang.String r0 = "id"
            int r0 = r1.getColumnIndex(r0)     // Catch:{ all -> 0x0058 }
            java.lang.String r2 = "type"
            int r2 = r1.getColumnIndex(r2)     // Catch:{ all -> 0x0058 }
            java.lang.String r3 = "shutdown_duration"
            int r3 = r1.getColumnIndex(r3)     // Catch:{ all -> 0x0058 }
            com.miui.powercenter.batteryhistory.BatteryHistogramItem r4 = new com.miui.powercenter.batteryhistory.BatteryHistogramItem     // Catch:{ all -> 0x0058 }
            r4.<init>()     // Catch:{ all -> 0x0058 }
            int r0 = r1.getInt(r0)     // Catch:{ all -> 0x0058 }
            r4.id = r0     // Catch:{ all -> 0x0058 }
            int r0 = r1.getInt(r2)     // Catch:{ all -> 0x0058 }
            r4.type = r0     // Catch:{ all -> 0x0058 }
            long r2 = r1.getLong(r3)     // Catch:{ all -> 0x0058 }
            r4.shutdownDuration = r2     // Catch:{ all -> 0x0058 }
            if (r1 == 0) goto L_0x0057
            r1.close()
        L_0x0057:
            return r4
        L_0x0058:
            r0 = move-exception
            goto L_0x0064
        L_0x005a:
            if (r1 == 0) goto L_0x005f
            r1.close()
        L_0x005f:
            return r0
        L_0x0060:
            r1 = move-exception
            r11 = r1
            r1 = r0
            r0 = r11
        L_0x0064:
            if (r1 == 0) goto L_0x0069
            r1.close()
        L_0x0069:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.batteryhistory.C0513q.d():com.miui.powercenter.batteryhistory.BatteryHistogramItem");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003c, code lost:
        if (r1 == null) goto L_0x0041;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003e, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0041, code lost:
        return 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:8:0x002f, code lost:
        if (r1 != null) goto L_0x003e;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int e() {
        /*
            r11 = this;
            r0 = 0
            r1 = 0
            com.miui.powercenter.batteryhistory.r r2 = r11.f6919c     // Catch:{ Exception -> 0x0034 }
            android.database.sqlite.SQLiteDatabase r3 = r2.getReadableDatabase()     // Catch:{ Exception -> 0x0034 }
            java.lang.String r2 = "id"
            java.lang.String[] r5 = new java.lang.String[]{r2}     // Catch:{ Exception -> 0x0034 }
            java.lang.String r4 = "histogram"
            java.lang.String r6 = "type!=?"
            r2 = 1
            java.lang.String[] r7 = new java.lang.String[r2]     // Catch:{ Exception -> 0x0034 }
            r2 = 2
            java.lang.String r2 = java.lang.String.valueOf(r2)     // Catch:{ Exception -> 0x0034 }
            r7[r0] = r2     // Catch:{ Exception -> 0x0034 }
            r8 = 0
            r9 = 0
            r10 = 0
            android.database.Cursor r1 = r3.query(r4, r5, r6, r7, r8, r9, r10)     // Catch:{ Exception -> 0x0034 }
            if (r1 == 0) goto L_0x002f
            int r0 = r1.getCount()     // Catch:{ Exception -> 0x0034 }
            if (r1 == 0) goto L_0x002e
            r1.close()
        L_0x002e:
            return r0
        L_0x002f:
            if (r1 == 0) goto L_0x0041
            goto L_0x003e
        L_0x0032:
            r0 = move-exception
            goto L_0x0042
        L_0x0034:
            r2 = move-exception
            java.lang.String r3 = "BatteryHistory"
            java.lang.String r4 = "getHistogramSize exception "
            android.util.Log.e(r3, r4, r2)     // Catch:{ all -> 0x0032 }
            if (r1 == 0) goto L_0x0041
        L_0x003e:
            r1.close()
        L_0x0041:
            return r0
        L_0x0042:
            if (r1 == 0) goto L_0x0047
            r1.close()
        L_0x0047:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.batteryhistory.C0513q.e():int");
    }

    public long f() {
        try {
            Cursor query = this.f6919c.getReadableDatabase().query("history", (String[]) null, (String) null, (String[]) null, (String) null, (String) null, "time ASC");
            if (query == null || !query.moveToFirst()) {
                return -1;
            }
            long j = query.getLong(query.getColumnIndex("time"));
            query.close();
            return j;
        } catch (Exception e2) {
            Log.e("BatteryHistory", "getHistoryBeginTime exception ", e2);
            return -1;
        }
    }

    public long g() {
        try {
            Cursor query = this.f6919c.getReadableDatabase().query("history", (String[]) null, (String) null, (String[]) null, (String) null, (String) null, "time DESC");
            if (query == null || !query.moveToFirst()) {
                return -1;
            }
            long j = query.getLong(query.getColumnIndex("time"));
            query.close();
            return j;
        } catch (Exception e2) {
            Log.e("BatteryHistory", "getHistoryEndTime exception ", e2);
            return -1;
        }
    }

    public long[] h() {
        try {
            Cursor query = this.f6919c.getReadableDatabase().query("history", (String[]) null, (String) null, (String[]) null, (String) null, (String) null, "time DESC");
            if (query == null || !query.moveToFirst()) {
                return null;
            }
            int columnIndex = query.getColumnIndex("time");
            int columnIndex2 = query.getColumnIndex("utc_time");
            long j = query.getLong(columnIndex);
            long j2 = query.getLong(columnIndex2);
            query.close();
            return new long[]{j, j2};
        } catch (Exception e2) {
            Log.e("BatteryHistory", "getHistoryEndTime exception ", e2);
            return null;
        }
    }

    public List<BatteryData> i() {
        ArrayList arrayList = new ArrayList();
        Cursor query = this.f6919c.getReadableDatabase().query("history", (String[]) null, (String) null, (String[]) null, (String) null, (String) null, "time ASC", o.f2310b);
        if (query == null || !query.moveToFirst()) {
            return arrayList;
        }
        String string = query.getString(query.getColumnIndex(DataSchemeDataSource.SCHEME_DATA));
        query.close();
        return (List) this.e.fromJson(string, new C0509m(this).getType());
    }

    public List<BatteryData> j() {
        ArrayList arrayList = new ArrayList();
        Cursor query = this.f6919c.getReadableDatabase().query("history", (String[]) null, (String) null, (String[]) null, (String) null, (String) null, "time DESC", o.f2310b);
        if (query == null || !query.moveToFirst()) {
            return arrayList;
        }
        String string = query.getString(query.getColumnIndex(DataSchemeDataSource.SCHEME_DATA));
        query.close();
        return (List) this.e.fromJson(string, new C0510n(this).getType());
    }

    public long k() {
        return this.f6920d.getLong("key_resettime", 0);
    }

    public int l() {
        Cursor cursor = null;
        try {
            cursor = this.f6919c.getReadableDatabase().query("history", new String[]{"time"}, (String) null, (String[]) null, (String) null, (String) null, (String) null);
            if (cursor != null) {
                int count = cursor.getCount();
                if (cursor != null) {
                    cursor.close();
                }
                return count;
            }
            if (cursor == null) {
                return 0;
            }
            cursor.close();
            return 0;
        } catch (Exception e2) {
            Log.e("BatteryHistory", "getHistoryBeginTime exception ", e2);
            if (cursor == null) {
                return 0;
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    public long m() {
        return this.f6920d.getLong("key_shutdowntime", 0);
    }

    public ArrayList<BatteryShutdownItem> n() {
        ArrayList<BatteryShutdownItem> arrayList = new ArrayList<>(10);
        try {
            Cursor query = this.f6919c.getReadableDatabase().query("shutdown", (String[]) null, (String) null, (String[]) null, (String) null, (String) null, "shutdown_time ASC");
            if (query != null && query.moveToFirst()) {
                int columnIndex = query.getColumnIndex("shutdown_time");
                int columnIndex2 = query.getColumnIndex("shutdown_duration");
                do {
                    BatteryShutdownItem batteryShutdownItem = new BatteryShutdownItem();
                    batteryShutdownItem.shutDownTime = query.getLong(columnIndex);
                    batteryShutdownItem.shutDownDuration = query.getLong(columnIndex2);
                    arrayList.add(batteryShutdownItem);
                } while (query.moveToNext());
            }
        } catch (Exception e2) {
            Log.e("BatteryHistory", "getShutdownItems exception ", e2);
        }
        return arrayList;
    }

    public boolean o() {
        ArrayList<BatteryHistogramItem> b2 = b();
        if (b2 == null || b2.size() <= 0) {
            return true;
        }
        Iterator<BatteryHistogramItem> it = b2.iterator();
        while (it.hasNext()) {
            if (it.next().totalConsume < 0.0d) {
                return false;
            }
        }
        return true;
    }
}
