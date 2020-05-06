package com.miui.powercenter.utils;

import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import com.miui.powercenter.bootshutdown.c;
import java.util.Calendar;
import miui.provider.ExtraCalendarContracts;

public class h {
    public static void a(Context context, int i, Calendar calendar) {
        if (i != 0) {
            int i2 = 0;
            if (i != 31) {
                if (i != 127) {
                    if (i != 128) {
                        int[] iArr = new int[7];
                        c cVar = new c(i);
                        while (i2 < 7) {
                            iArr[i2] = 1;
                            i2++;
                        }
                        cVar.a(iArr);
                        Calendar instance = Calendar.getInstance();
                        int i3 = iArr[instance.get(7) == 1 ? 6 : instance.get(7) - 2];
                        if (calendar.getTimeInMillis() <= System.currentTimeMillis() || !cVar.c()) {
                            calendar.add(7, i3);
                            return;
                        }
                        return;
                    } else if (calendar.getTimeInMillis() <= System.currentTimeMillis() || a(context, calendar)) {
                        while (i2 < 10) {
                            calendar.add(7, 1);
                            if (a(context, calendar)) {
                                i2++;
                            } else {
                                return;
                            }
                        }
                        return;
                    } else {
                        return;
                    }
                } else if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
                    return;
                }
            } else if (calendar.getTimeInMillis() <= System.currentTimeMillis() || a(calendar)) {
                while (i2 < 7) {
                    calendar.add(7, 1);
                    if (a(calendar)) {
                        i2++;
                    } else {
                        return;
                    }
                }
                return;
            } else {
                return;
            }
        } else if (calendar.getTimeInMillis() > System.currentTimeMillis()) {
            return;
        }
        calendar.add(7, 1);
    }

    public static boolean a(Context context) {
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(ContentUris.withAppendedId(ExtraCalendarContracts.HolidayContracts.HOLIDAY_CONTENT_URI, Calendar.getInstance().getTimeInMillis()), (String[]) null, (String) null, (String[]) null, (String) null);
            if (cursor == null || !cursor.moveToFirst() || cursor.getInt(2) != 3) {
                if (cursor == null) {
                    return false;
                }
                cursor.close();
                return false;
            }
            if (cursor != null) {
                cursor.close();
            }
            return true;
        } catch (Exception unused) {
            Log.e("HolidayHelper", "is Holiday Data Invalid error");
            if (cursor == null) {
                return false;
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
            throw th;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0054, code lost:
        if (r1 != null) goto L_0x0060;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x005e, code lost:
        if (r1 == null) goto L_0x0063;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0060, code lost:
        r1.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x0067, code lost:
        return a(r9);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean a(android.content.Context r8, java.util.Calendar r9) {
        /*
            java.lang.String r0 = "HolidayHelper"
            r1 = 0
            android.content.ContentResolver r2 = r8.getContentResolver()     // Catch:{ Exception -> 0x0059 }
            android.net.Uri r8 = b(r8)     // Catch:{ Exception -> 0x0059 }
            long r3 = r9.getTimeInMillis()     // Catch:{ Exception -> 0x0059 }
            android.net.Uri r3 = android.content.ContentUris.withAppendedId(r8, r3)     // Catch:{ Exception -> 0x0059 }
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            android.database.Cursor r1 = r2.query(r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x0059 }
            if (r1 == 0) goto L_0x004f
            boolean r8 = r1.moveToFirst()     // Catch:{ Exception -> 0x0059 }
            if (r8 == 0) goto L_0x004f
            r8 = 2
            int r2 = r1.getInt(r8)     // Catch:{ Exception -> 0x0059 }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0059 }
            r3.<init>()     // Catch:{ Exception -> 0x0059 }
            java.lang.String r4 = "use holiday info "
            r3.append(r4)     // Catch:{ Exception -> 0x0059 }
            r3.append(r2)     // Catch:{ Exception -> 0x0059 }
            java.lang.String r3 = r3.toString()     // Catch:{ Exception -> 0x0059 }
            android.util.Log.i(r0, r3)     // Catch:{ Exception -> 0x0059 }
            r3 = 1
            if (r3 == r2) goto L_0x0049
            if (r8 == r2) goto L_0x0048
            boolean r8 = a((java.util.Calendar) r9)     // Catch:{ Exception -> 0x0059 }
            if (r8 == 0) goto L_0x0048
            goto L_0x0049
        L_0x0048:
            r3 = 0
        L_0x0049:
            if (r1 == 0) goto L_0x004e
            r1.close()
        L_0x004e:
            return r3
        L_0x004f:
            java.lang.String r8 = "use weekend"
            android.util.Log.i(r0, r8)     // Catch:{ Exception -> 0x0059 }
            if (r1 == 0) goto L_0x0063
            goto L_0x0060
        L_0x0057:
            r8 = move-exception
            goto L_0x0068
        L_0x0059:
            java.lang.String r8 = "is Holiday error"
            android.util.Log.e(r0, r8)     // Catch:{ all -> 0x0057 }
            if (r1 == 0) goto L_0x0063
        L_0x0060:
            r1.close()
        L_0x0063:
            boolean r8 = a((java.util.Calendar) r9)
            return r8
        L_0x0068:
            if (r1 == 0) goto L_0x006d
            r1.close()
        L_0x006d:
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.utils.h.a(android.content.Context, java.util.Calendar):boolean");
    }

    public static boolean a(Calendar calendar) {
        return calendar.get(7) == 7 || calendar.get(7) == 1;
    }

    private static Uri b(Context context) {
        try {
            context.getPackageManager().getPackageInfo("com.xiaomi.calendar", 0);
            return Uri.parse("content://com.xiaomi.calendar/daysoff");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return Uri.parse("content://com.miui.calendar/daysoff");
        }
    }
}
