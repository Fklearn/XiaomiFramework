package com.miui.permcenter;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.j.B;

public class s {

    /* renamed from: a  reason: collision with root package name */
    private static final String f6502a = "com.miui.permcenter.s";

    /* renamed from: b  reason: collision with root package name */
    private static final Uri f6503b = Uri.parse("content://com.lbe.security.miui.permmgr/query/wakepath/whitelist");

    /* renamed from: c  reason: collision with root package name */
    private static final Uri f6504c = Uri.parse("content://0@com.lbe.security.miui.permmgr/query/wakepath/whitelist");

    /* renamed from: d  reason: collision with root package name */
    private static final Uri f6505d = Uri.parse("content://com.lbe.security.miui.permmgr/update/wakepath/whitelist");
    private static final Uri e = Uri.parse("content://0@com.lbe.security.miui.permmgr/update/wakepath/whitelist");
    private static final Uri f = Uri.parse("content://com.lbe.security.miui.permmgr/StartActivityRuleList");
    private static final Uri g = Uri.parse("content://0@com.lbe.security.miui.permmgr/StartActivityRuleList");

    public static int a(Context context, String str, String str2) {
        ContentResolver contentResolver;
        Uri uri;
        if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2)) {
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put("callerPkgName", str);
                contentValues.put("calleePkgName", str2);
                contentValues.put("userSettings", 0);
                if (B.f()) {
                    contentResolver = context.getContentResolver();
                    uri = f;
                } else {
                    contentResolver = context.getContentResolver();
                    uri = g;
                }
                contentResolver.insert(uri, contentValues);
                return 1;
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return 0;
    }

    public static int a(Context context, String str, boolean z) {
        Uri uri;
        ContentResolver contentResolver;
        if (context == null || TextUtils.isEmpty(str)) {
            Log.i(f6502a, "updateWakePathWhiteList: Invalid parameter!!");
            return 0;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put("pkgName", str);
        contentValues.put("isAllowStartByWakePath", Boolean.valueOf(z));
        if (B.f()) {
            contentResolver = context.getContentResolver();
            uri = f6505d;
        } else {
            contentResolver = context.getContentResolver();
            uri = e;
        }
        int update = contentResolver.update(uri, contentValues, (String) null, (String[]) null);
        String str2 = f6502a;
        Log.i(str2, "updateWakePathWhiteList: ret =" + update);
        return update;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0055, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0056, code lost:
        r8 = r0;
        r0 = r9;
        r9 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x005a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x005b, code lost:
        r8 = r0;
        r0 = r9;
        r9 = r8;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:0x0074, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x0086, code lost:
        r0.close();
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:22:0x005a A[ExcHandler: all (r0v5 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:10:0x0030] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x0074  */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x0086  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.util.List<java.lang.String> a(android.content.Context r9) {
        /*
            r0 = 0
            if (r9 != 0) goto L_0x000b
            java.lang.String r9 = f6502a
            java.lang.String r1 = "getUserAcceptPkgs: Invalid parameter!!"
            android.util.Log.i(r9, r1)
            return r0
        L_0x000b:
            boolean r1 = b.b.c.j.B.f()     // Catch:{ Exception -> 0x006d }
            if (r1 == 0) goto L_0x0020
            android.content.ContentResolver r2 = r9.getContentResolver()     // Catch:{ Exception -> 0x006d }
            android.net.Uri r3 = f6503b     // Catch:{ Exception -> 0x006d }
            r4 = 0
            r5 = 0
            r6 = 0
            r7 = 0
            android.database.Cursor r9 = r2.query(r3, r4, r5, r6, r7)     // Catch:{ Exception -> 0x006d }
            goto L_0x002e
        L_0x0020:
            android.content.ContentResolver r1 = r9.getContentResolver()     // Catch:{ Exception -> 0x006d }
            android.net.Uri r2 = f6504c     // Catch:{ Exception -> 0x006d }
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = 0
            android.database.Cursor r9 = r1.query(r2, r3, r4, r5, r6)     // Catch:{ Exception -> 0x006d }
        L_0x002e:
            if (r9 == 0) goto L_0x0065
            java.util.ArrayList r1 = new java.util.ArrayList     // Catch:{ Exception -> 0x005f, all -> 0x005a }
            r1.<init>()     // Catch:{ Exception -> 0x005f, all -> 0x005a }
        L_0x0035:
            boolean r0 = r9.moveToNext()     // Catch:{ Exception -> 0x0055, all -> 0x005a }
            if (r0 == 0) goto L_0x004f
            java.lang.String r0 = "pkgName"
            int r0 = r9.getColumnIndex(r0)     // Catch:{ Exception -> 0x0055, all -> 0x005a }
            java.lang.String r0 = r9.getString(r0)     // Catch:{ Exception -> 0x0055, all -> 0x005a }
            boolean r2 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0055, all -> 0x005a }
            if (r2 != 0) goto L_0x0035
            r1.add(r0)     // Catch:{ Exception -> 0x0055, all -> 0x005a }
            goto L_0x0035
        L_0x004f:
            if (r9 == 0) goto L_0x0077
            r9.close()
            goto L_0x0077
        L_0x0055:
            r0 = move-exception
            r8 = r0
            r0 = r9
            r9 = r8
            goto L_0x006f
        L_0x005a:
            r0 = move-exception
            r8 = r0
            r0 = r9
            r9 = r8
            goto L_0x0084
        L_0x005f:
            r1 = move-exception
            r8 = r0
            r0 = r9
            r9 = r1
            r1 = r8
            goto L_0x006f
        L_0x0065:
            if (r9 == 0) goto L_0x006a
            r9.close()
        L_0x006a:
            return r0
        L_0x006b:
            r9 = move-exception
            goto L_0x0084
        L_0x006d:
            r9 = move-exception
            r1 = r0
        L_0x006f:
            r9.printStackTrace()     // Catch:{ all -> 0x006b }
            if (r0 == 0) goto L_0x0077
            r0.close()
        L_0x0077:
            if (r1 == 0) goto L_0x0083
            int r9 = r1.size()
            r0 = 1
            if (r9 <= r0) goto L_0x0083
            java.util.Collections.sort(r1)
        L_0x0083:
            return r1
        L_0x0084:
            if (r0 == 0) goto L_0x0089
            r0.close()
        L_0x0089:
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.permcenter.s.a(android.content.Context):java.util.List");
    }
}
