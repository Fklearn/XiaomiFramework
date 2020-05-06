package com.miui.powercenter.utils;

class q implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ String f7317a;

    q(String str) {
        this.f7317a = str;
    }

    /* JADX WARNING: Removed duplicated region for block: B:30:0x0096 A[SYNTHETIC, Splitter:B:30:0x0096] */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00a0 A[SYNTHETIC, Splitter:B:35:0x00a0] */
    /* JADX WARNING: Removed duplicated region for block: B:41:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
            r10 = this;
            java.lang.String r0 = "operate file failed"
            java.lang.String r1 = "ThermalStoreUtils"
            java.io.File r2 = new java.io.File
            java.lang.String r3 = "/persist/thermal"
            r2.<init>(r3)
            boolean r3 = r2.exists()
            r4 = 509(0x1fd, float:7.13E-43)
            r5 = -1
            if (r3 != 0) goto L_0x0024
            boolean r3 = r2.isDirectory()
            if (r3 != 0) goto L_0x0024
            r2.mkdir()
            java.lang.String r2 = r2.getPath()
            com.miui.powercenter.utils.r.b(r2, r4, r5, r5)
        L_0x0024:
            java.io.File r2 = new java.io.File
            java.lang.String r3 = com.miui.powercenter.utils.r.f7318a
            r2.<init>(r3)
            r3 = 0
            boolean r6 = r2.exists()     // Catch:{ IOException -> 0x008d }
            if (r6 != 0) goto L_0x003f
            r2.createNewFile()     // Catch:{ IOException -> 0x008d }
            java.lang.String r6 = r2.getPath()     // Catch:{ IOException -> 0x008d }
        L_0x003b:
            com.miui.powercenter.utils.r.b(r6, r4, r5, r5)     // Catch:{ IOException -> 0x008d }
            goto L_0x005e
        L_0x003f:
            long r6 = r2.length()     // Catch:{ IOException -> 0x008d }
            r8 = 10240(0x2800, double:5.059E-320)
            int r6 = (r6 > r8 ? 1 : (r6 == r8 ? 0 : -1))
            if (r6 <= 0) goto L_0x005e
            java.lang.String r6 = com.miui.powercenter.utils.r.f7318a     // Catch:{ IOException -> 0x008d }
            com.miui.powercenter.utils.f.a((java.lang.String) r6)     // Catch:{ IOException -> 0x008d }
            boolean r6 = r2.exists()     // Catch:{ IOException -> 0x008d }
            if (r6 != 0) goto L_0x005e
            r2.createNewFile()     // Catch:{ IOException -> 0x008d }
            java.lang.String r6 = r2.getPath()     // Catch:{ IOException -> 0x008d }
            goto L_0x003b
        L_0x005e:
            java.io.FileWriter r4 = new java.io.FileWriter     // Catch:{ IOException -> 0x008d }
            r5 = 1
            r4.<init>(r2, r5)     // Catch:{ IOException -> 0x008d }
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0088, all -> 0x0085 }
            r2.<init>()     // Catch:{ IOException -> 0x0088, all -> 0x0085 }
            java.lang.String r3 = r10.f7317a     // Catch:{ IOException -> 0x0088, all -> 0x0085 }
            r2.append(r3)     // Catch:{ IOException -> 0x0088, all -> 0x0085 }
            java.lang.String r3 = ":"
            r2.append(r3)     // Catch:{ IOException -> 0x0088, all -> 0x0085 }
            java.lang.String r3 = com.miui.powercenter.utils.r.f()     // Catch:{ IOException -> 0x0088, all -> 0x0085 }
            r2.append(r3)     // Catch:{ IOException -> 0x0088, all -> 0x0085 }
            java.lang.String r2 = r2.toString()     // Catch:{ IOException -> 0x0088, all -> 0x0085 }
            r4.write(r2)     // Catch:{ IOException -> 0x0088, all -> 0x0085 }
            r4.close()     // Catch:{ IOException -> 0x009a }
            goto L_0x009d
        L_0x0085:
            r2 = move-exception
            r3 = r4
            goto L_0x009e
        L_0x0088:
            r2 = move-exception
            r3 = r4
            goto L_0x008e
        L_0x008b:
            r2 = move-exception
            goto L_0x009e
        L_0x008d:
            r2 = move-exception
        L_0x008e:
            android.util.Log.i(r1, r0)     // Catch:{ all -> 0x008b }
            r2.printStackTrace()     // Catch:{ all -> 0x008b }
            if (r3 == 0) goto L_0x009d
            r3.close()     // Catch:{ IOException -> 0x009a }
            goto L_0x009d
        L_0x009a:
            android.util.Log.i(r1, r0)
        L_0x009d:
            return
        L_0x009e:
            if (r3 == 0) goto L_0x00a7
            r3.close()     // Catch:{ IOException -> 0x00a4 }
            goto L_0x00a7
        L_0x00a4:
            android.util.Log.i(r1, r0)
        L_0x00a7:
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.utils.q.run():void");
    }
}
