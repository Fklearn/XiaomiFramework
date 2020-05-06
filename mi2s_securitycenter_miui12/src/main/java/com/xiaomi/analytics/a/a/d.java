package com.xiaomi.analytics.a.a;

public class d {
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v0, resolved type: java.io.InputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v1, resolved type: java.io.InputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v4, resolved type: java.io.InputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v5, resolved type: java.io.InputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v6, resolved type: java.io.InputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v7, resolved type: java.io.FileOutputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v11, resolved type: java.io.InputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v12, resolved type: java.io.InputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v14, resolved type: java.io.InputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v15, resolved type: java.io.InputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r2v16, resolved type: java.io.InputStream} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void a(android.content.Context r5, java.lang.String r6, java.lang.String r7) {
        /*
            r0 = 0
            android.content.res.AssetManager r5 = r5.getAssets()     // Catch:{ Exception -> 0x0064, all -> 0x0060 }
            java.io.InputStream r5 = r5.open(r6)     // Catch:{ Exception -> 0x0064, all -> 0x0060 }
            byte[] r6 = com.xiaomi.analytics.a.a.g.a((java.io.InputStream) r5)     // Catch:{ Exception -> 0x005b, all -> 0x0058 }
            java.io.File r1 = new java.io.File     // Catch:{ Exception -> 0x005b, all -> 0x0058 }
            r1.<init>(r7)     // Catch:{ Exception -> 0x005b, all -> 0x0058 }
            boolean r7 = r1.exists()     // Catch:{ Exception -> 0x005b, all -> 0x0058 }
            if (r7 == 0) goto L_0x003f
            java.io.FileInputStream r7 = new java.io.FileInputStream     // Catch:{ Exception -> 0x005b, all -> 0x0058 }
            r7.<init>(r1)     // Catch:{ Exception -> 0x005b, all -> 0x0058 }
            byte[] r2 = com.xiaomi.analytics.a.a.g.a((java.io.InputStream) r7)     // Catch:{ Exception -> 0x0055, all -> 0x0053 }
            java.lang.String r2 = com.xiaomi.analytics.a.a.p.a((byte[]) r2)     // Catch:{ Exception -> 0x0055, all -> 0x0053 }
            java.lang.String r3 = com.xiaomi.analytics.a.a.p.a((byte[]) r6)     // Catch:{ Exception -> 0x0055, all -> 0x0053 }
            boolean r4 = android.text.TextUtils.isEmpty(r2)     // Catch:{ Exception -> 0x0055, all -> 0x0053 }
            if (r4 != 0) goto L_0x0040
            boolean r2 = r2.equals(r3)     // Catch:{ Exception -> 0x0055, all -> 0x0053 }
            if (r2 == 0) goto L_0x0040
            com.xiaomi.analytics.a.a.g.a((java.io.Closeable) r5)
            com.xiaomi.analytics.a.a.g.a((java.io.Closeable) r7)
            com.xiaomi.analytics.a.a.g.a((java.io.Closeable) r0)
            return
        L_0x003f:
            r7 = r0
        L_0x0040:
            java.io.FileOutputStream r2 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0055, all -> 0x0053 }
            r2.<init>(r1)     // Catch:{ Exception -> 0x0055, all -> 0x0053 }
            r2.write(r6)     // Catch:{ Exception -> 0x0051, all -> 0x004f }
            r2.flush()     // Catch:{ Exception -> 0x0051, all -> 0x004f }
            com.xiaomi.analytics.a.a.g.a((java.io.Closeable) r5)
            goto L_0x0075
        L_0x004f:
            r6 = move-exception
            goto L_0x007e
        L_0x0051:
            r6 = move-exception
            goto L_0x005e
        L_0x0053:
            r6 = move-exception
            goto L_0x007f
        L_0x0055:
            r6 = move-exception
            r2 = r0
            goto L_0x005e
        L_0x0058:
            r6 = move-exception
            r7 = r0
            goto L_0x007f
        L_0x005b:
            r6 = move-exception
            r7 = r0
            r2 = r7
        L_0x005e:
            r0 = r5
            goto L_0x0067
        L_0x0060:
            r6 = move-exception
            r5 = r0
            r7 = r5
            goto L_0x007f
        L_0x0064:
            r6 = move-exception
            r7 = r0
            r2 = r7
        L_0x0067:
            java.lang.String r5 = "AssetUtils"
            java.lang.String r5 = com.xiaomi.analytics.a.a.a.a(r5)     // Catch:{ all -> 0x007c }
            java.lang.String r1 = "extractAssetFile e"
            android.util.Log.e(r5, r1, r6)     // Catch:{ all -> 0x007c }
            com.xiaomi.analytics.a.a.g.a((java.io.Closeable) r0)
        L_0x0075:
            com.xiaomi.analytics.a.a.g.a((java.io.Closeable) r7)
            com.xiaomi.analytics.a.a.g.a((java.io.Closeable) r2)
            return
        L_0x007c:
            r6 = move-exception
            r5 = r0
        L_0x007e:
            r0 = r2
        L_0x007f:
            com.xiaomi.analytics.a.a.g.a((java.io.Closeable) r5)
            com.xiaomi.analytics.a.a.g.a((java.io.Closeable) r7)
            com.xiaomi.analytics.a.a.g.a((java.io.Closeable) r0)
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.analytics.a.a.d.a(android.content.Context, java.lang.String, java.lang.String):void");
    }
}
