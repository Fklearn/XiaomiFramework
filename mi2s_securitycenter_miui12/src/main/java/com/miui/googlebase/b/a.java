package com.miui.googlebase.b;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static final String f5442a = "a";

    /* JADX WARNING: Code restructure failed: missing block: B:15:0x0022, code lost:
        if (r1 != null) goto L_0x0024;
     */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x002b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static final java.lang.String a(java.io.File r4) {
        /*
            r0 = 0
            java.io.FileInputStream r1 = new java.io.FileInputStream     // Catch:{ IOException -> 0x0019, all -> 0x0016 }
            r1.<init>(r4)     // Catch:{ IOException -> 0x0019, all -> 0x0016 }
            java.lang.String r4 = b.b.c.j.j.b((java.io.InputStream) r1)     // Catch:{ IOException -> 0x0014 }
            boolean r2 = android.text.TextUtils.isEmpty(r4)     // Catch:{ IOException -> 0x0014 }
            if (r2 != 0) goto L_0x0024
            miui.util.IOUtils.closeQuietly(r1)
            return r4
        L_0x0014:
            r4 = move-exception
            goto L_0x001b
        L_0x0016:
            r4 = move-exception
            r1 = r0
            goto L_0x0029
        L_0x0019:
            r4 = move-exception
            r1 = r0
        L_0x001b:
            java.lang.String r2 = f5442a     // Catch:{ all -> 0x0028 }
            java.lang.String r3 = "encodeMD5 Error"
            android.util.Log.e(r2, r3, r4)     // Catch:{ all -> 0x0028 }
            if (r1 == 0) goto L_0x0027
        L_0x0024:
            miui.util.IOUtils.closeQuietly(r1)
        L_0x0027:
            return r0
        L_0x0028:
            r4 = move-exception
        L_0x0029:
            if (r1 == 0) goto L_0x002e
            miui.util.IOUtils.closeQuietly(r1)
        L_0x002e:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.googlebase.b.a.a(java.io.File):java.lang.String");
    }
}
