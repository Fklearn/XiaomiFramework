package com.miui.powercenter.utils;

import android.content.Context;
import android.util.Log;
import java.io.File;

public class p {

    /* renamed from: a  reason: collision with root package name */
    private String f7316a;

    public p(Context context, String str) {
        String str2 = context.getFilesDir().getPath() + "/pc/";
        File file = new File(str2);
        if (!file.mkdirs() && !file.isDirectory()) {
            file.delete();
            if (!file.mkdirs()) {
                Log.e("StringCache", "Create dir failed " + str2);
            }
        }
        this.f7316a = str2 + str;
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0028 A[SYNTHETIC, Splitter:B:18:0x0028] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x002f A[SYNTHETIC, Splitter:B:24:0x002f] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean a(java.lang.String r4, java.lang.String r5) {
        /*
            r3 = this;
            r0 = 0
            java.io.BufferedWriter r1 = new java.io.BufferedWriter     // Catch:{ IOException -> 0x001e }
            java.io.FileWriter r2 = new java.io.FileWriter     // Catch:{ IOException -> 0x001e }
            r2.<init>(r4)     // Catch:{ IOException -> 0x001e }
            r1.<init>(r2)     // Catch:{ IOException -> 0x001e }
            r1.write(r5)     // Catch:{ IOException -> 0x0019, all -> 0x0016 }
            r1.flush()     // Catch:{ IOException -> 0x0019, all -> 0x0016 }
            r4 = 1
            r1.close()     // Catch:{ IOException -> 0x002c }
            goto L_0x002c
        L_0x0016:
            r4 = move-exception
            r0 = r1
            goto L_0x002d
        L_0x0019:
            r4 = move-exception
            r0 = r1
            goto L_0x001f
        L_0x001c:
            r4 = move-exception
            goto L_0x002d
        L_0x001e:
            r4 = move-exception
        L_0x001f:
            java.lang.String r5 = "StringCache"
            java.lang.String r1 = ""
            android.util.Log.e(r5, r1, r4)     // Catch:{ all -> 0x001c }
            if (r0 == 0) goto L_0x002b
            r0.close()     // Catch:{ IOException -> 0x002b }
        L_0x002b:
            r4 = 0
        L_0x002c:
            return r4
        L_0x002d:
            if (r0 == 0) goto L_0x0032
            r0.close()     // Catch:{ IOException -> 0x0032 }
        L_0x0032:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.utils.p.a(java.lang.String, java.lang.String):boolean");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:22:0x003d, code lost:
        if (r3 != null) goto L_0x003f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:?, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0047, code lost:
        if (r3 != null) goto L_0x003f;
     */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0051 A[SYNTHETIC, Splitter:B:33:0x0051] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:26:0x0044=Splitter:B:26:0x0044, B:20:0x003a=Splitter:B:20:0x003a} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String b(java.lang.String r7) {
        /*
            r6 = this;
            java.lang.String r0 = "StringCache"
            java.io.File r1 = new java.io.File
            r1.<init>(r7)
            boolean r1 = r1.exists()
            java.lang.String r2 = ""
            if (r1 != 0) goto L_0x0010
            return r2
        L_0x0010:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r3 = 0
            java.io.BufferedReader r4 = new java.io.BufferedReader     // Catch:{ FileNotFoundException -> 0x0043, IOException -> 0x0039 }
            java.io.FileReader r5 = new java.io.FileReader     // Catch:{ FileNotFoundException -> 0x0043, IOException -> 0x0039 }
            r5.<init>(r7)     // Catch:{ FileNotFoundException -> 0x0043, IOException -> 0x0039 }
            r4.<init>(r5)     // Catch:{ FileNotFoundException -> 0x0043, IOException -> 0x0039 }
        L_0x0020:
            java.lang.String r7 = r4.readLine()     // Catch:{ FileNotFoundException -> 0x0034, IOException -> 0x0031, all -> 0x002e }
            if (r7 == 0) goto L_0x002a
            r1.append(r7)     // Catch:{ FileNotFoundException -> 0x0034, IOException -> 0x0031, all -> 0x002e }
            goto L_0x0020
        L_0x002a:
            r4.close()     // Catch:{ IOException -> 0x004a }
            goto L_0x004a
        L_0x002e:
            r7 = move-exception
            r3 = r4
            goto L_0x004f
        L_0x0031:
            r7 = move-exception
            r3 = r4
            goto L_0x003a
        L_0x0034:
            r7 = move-exception
            r3 = r4
            goto L_0x0044
        L_0x0037:
            r7 = move-exception
            goto L_0x004f
        L_0x0039:
            r7 = move-exception
        L_0x003a:
            android.util.Log.e(r0, r2, r7)     // Catch:{ all -> 0x0037 }
            if (r3 == 0) goto L_0x004a
        L_0x003f:
            r3.close()     // Catch:{ IOException -> 0x004a }
            goto L_0x004a
        L_0x0043:
            r7 = move-exception
        L_0x0044:
            android.util.Log.e(r0, r2, r7)     // Catch:{ all -> 0x0037 }
            if (r3 == 0) goto L_0x004a
            goto L_0x003f
        L_0x004a:
            java.lang.String r7 = r1.toString()
            return r7
        L_0x004f:
            if (r3 == 0) goto L_0x0054
            r3.close()     // Catch:{ IOException -> 0x0054 }
        L_0x0054:
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.utils.p.b(java.lang.String):java.lang.String");
    }

    public String a() {
        return b(this.f7316a);
    }

    public boolean a(String str) {
        new File(this.f7316a).delete();
        return a(this.f7316a, str);
    }
}
