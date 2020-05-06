package com.miui.hybrid.accessory.sdk.icondialog;

public class c {
    /* JADX WARNING: Can't wrap try/catch for region: R(6:6|7|8|(4:16|17|(2:21|(3:35|25|26))|32)(3:34|14|15)|4|3) */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x0081, code lost:
        r8 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:30:0x0082, code lost:
        r7.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x0085, code lost:
        throw r8;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x001c */
    /* JADX WARNING: Removed duplicated region for block: B:3:0x001c A[LOOP:0: B:3:0x001c->B:32:0x001c, LOOP_START, SYNTHETIC, Splitter:B:3:0x001c] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static boolean a(android.content.Context r7, java.lang.String r8) {
        /*
            java.lang.String r0 = "content://com.miui.home.launcher.settings/favorites"
            android.net.Uri r2 = android.net.Uri.parse(r0)
            java.lang.String r0 = "intent, itemType"
            java.lang.String[] r3 = new java.lang.String[]{r0}
            android.content.ContentResolver r1 = r7.getContentResolver()
            java.lang.String r4 = "itemType=1 OR itemType=14"
            r5 = 0
            r6 = 0
            android.database.Cursor r7 = r1.query(r2, r3, r4, r5, r6)
            r0 = 0
            if (r7 != 0) goto L_0x001c
            return r0
        L_0x001c:
            boolean r1 = r7.moveToNext()     // Catch:{ all -> 0x0081 }
            if (r1 == 0) goto L_0x007d
            java.lang.String r1 = r7.getString(r0)     // Catch:{ all -> 0x0081 }
            r2 = 1
            java.lang.String r3 = r7.getString(r2)     // Catch:{ all -> 0x0081 }
            android.content.Intent r1 = android.content.Intent.parseUri(r1, r0)     // Catch:{ URISyntaxException -> 0x001c }
            java.lang.String r4 = "1"
            boolean r4 = r4.equals(r3)     // Catch:{ URISyntaxException -> 0x001c }
            if (r4 == 0) goto L_0x0053
            java.lang.String r4 = "com.miui.hybrid.action.LAUNCH"
            java.lang.String r5 = r1.getAction()     // Catch:{ URISyntaxException -> 0x001c }
            boolean r4 = r4.equals(r5)     // Catch:{ URISyntaxException -> 0x001c }
            if (r4 == 0) goto L_0x0053
            java.lang.String r4 = "EXTRA_APP"
            java.lang.String r4 = r1.getStringExtra(r4)     // Catch:{ URISyntaxException -> 0x001c }
            boolean r4 = android.text.TextUtils.equals(r4, r8)     // Catch:{ URISyntaxException -> 0x001c }
            if (r4 == 0) goto L_0x0053
            r7.close()
            return r2
        L_0x0053:
            int r4 = android.os.Build.VERSION.SDK_INT     // Catch:{ URISyntaxException -> 0x001c }
            r5 = 26
            if (r4 < r5) goto L_0x001c
            java.lang.String r4 = "14"
            boolean r3 = r4.equals(r3)     // Catch:{ URISyntaxException -> 0x001c }
            if (r3 == 0) goto L_0x001c
            java.lang.String r3 = r1.getPackage()     // Catch:{ URISyntaxException -> 0x001c }
            java.lang.String r4 = "shortcut_id"
            java.lang.String r1 = r1.getStringExtra(r4)     // Catch:{ URISyntaxException -> 0x001c }
            boolean r1 = android.text.TextUtils.equals(r1, r8)     // Catch:{ URISyntaxException -> 0x001c }
            if (r1 == 0) goto L_0x001c
            java.lang.String r1 = "com.miui.hybrid"
            boolean r1 = r1.equals(r3)     // Catch:{ URISyntaxException -> 0x001c }
            if (r1 == 0) goto L_0x001c
            r7.close()
            return r2
        L_0x007d:
            r7.close()
            return r0
        L_0x0081:
            r8 = move-exception
            r7.close()
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.hybrid.accessory.sdk.icondialog.c.a(android.content.Context, java.lang.String):boolean");
    }
}
