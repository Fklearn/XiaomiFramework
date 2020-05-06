package com.xiaomi.analytics.a.a;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class c {
    private static int a(List<String> list, String str) {
        int i = 0;
        while (list != null && i < list.size()) {
            if (!TextUtils.isEmpty(str) && str.equalsIgnoreCase(list.get(i))) {
                return i;
            }
            i++;
        }
        return -1;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:1:0x0002, code lost:
        r2 = r2.split("/");
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String a(java.lang.String r2) {
        /*
            if (r2 == 0) goto L_0x0014
            java.lang.String r0 = "/"
            java.lang.String[] r2 = r2.split(r0)
            if (r2 == 0) goto L_0x0014
            int r0 = r2.length
            r1 = 1
            if (r0 <= r1) goto L_0x0014
            int r0 = r2.length
            int r0 = r0 + -2
            r2 = r2[r0]
            return r2
        L_0x0014:
            java.lang.String r2 = "armeabi"
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.analytics.a.a.c.a(java.lang.String):java.lang.String");
    }

    private static List<String> a(Context context) {
        ArrayList arrayList = new ArrayList();
        String b2 = b(context);
        if (!TextUtils.isEmpty(b2)) {
            arrayList.add(b2);
        }
        String a2 = m.a("ro.product.cpu.abi", "");
        if (!TextUtils.isEmpty(a2)) {
            arrayList.add(a2);
        }
        String a3 = m.a("ro.product.cpu.abi2", "");
        if (!TextUtils.isEmpty(a3)) {
            arrayList.add(a3);
        }
        String a4 = m.a("ro.product.cpu.abilist", "");
        if (!TextUtils.isEmpty(a4)) {
            String[] split = a4.split(",");
            int i = 0;
            while (split != null && i < split.length) {
                if (!TextUtils.isEmpty(split[i])) {
                    arrayList.add(split[i]);
                }
                i++;
            }
        }
        arrayList.add("armeabi");
        return arrayList;
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v6, resolved type: java.util.zip.ZipFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v7, resolved type: java.util.zip.ZipFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v8, resolved type: java.util.zip.ZipFile} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v9, resolved type: java.io.BufferedInputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r9v10, resolved type: java.util.zip.ZipFile} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:70:0x0145 A[SYNTHETIC, Splitter:B:70:0x0145] */
    /* JADX WARNING: Removed duplicated region for block: B:75:0x0154 A[SYNTHETIC, Splitter:B:75:0x0154] */
    /* JADX WARNING: Removed duplicated region for block: B:87:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void a(android.content.Context r12, java.lang.String r13, java.lang.String r14) {
        /*
            java.lang.String r0 = "extractSo finally close file e"
            r1 = 0
            java.lang.String r2 = "ApkTools"
            if (r13 == 0) goto L_0x0028
            java.lang.String r3 = java.io.File.separator     // Catch:{ Exception -> 0x0025 }
            boolean r3 = r13.endsWith(r3)     // Catch:{ Exception -> 0x0025 }
            if (r3 != 0) goto L_0x0028
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0025 }
            r3.<init>()     // Catch:{ Exception -> 0x0025 }
            r3.append(r13)     // Catch:{ Exception -> 0x0025 }
            java.lang.String r13 = java.io.File.separator     // Catch:{ Exception -> 0x0025 }
            r3.append(r13)     // Catch:{ Exception -> 0x0025 }
            java.lang.String r13 = r3.toString()     // Catch:{ Exception -> 0x0025 }
            goto L_0x0028
        L_0x0021:
            r12 = move-exception
            r4 = r1
            goto L_0x0152
        L_0x0025:
            r12 = move-exception
            goto L_0x013a
        L_0x0028:
            if (r14 == 0) goto L_0x0043
            java.lang.String r3 = java.io.File.separator     // Catch:{ Exception -> 0x0025 }
            boolean r3 = r14.endsWith(r3)     // Catch:{ Exception -> 0x0025 }
            if (r3 != 0) goto L_0x0043
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0025 }
            r3.<init>()     // Catch:{ Exception -> 0x0025 }
            r3.append(r14)     // Catch:{ Exception -> 0x0025 }
            java.lang.String r14 = java.io.File.separator     // Catch:{ Exception -> 0x0025 }
            r3.append(r14)     // Catch:{ Exception -> 0x0025 }
            java.lang.String r14 = r3.toString()     // Catch:{ Exception -> 0x0025 }
        L_0x0043:
            java.util.HashMap r3 = new java.util.HashMap     // Catch:{ Exception -> 0x0025 }
            r3.<init>()     // Catch:{ Exception -> 0x0025 }
            java.util.List r12 = a((android.content.Context) r12)     // Catch:{ Exception -> 0x0025 }
            java.util.zip.ZipFile r4 = new java.util.zip.ZipFile     // Catch:{ Exception -> 0x0025 }
            r4.<init>(r13)     // Catch:{ Exception -> 0x0025 }
            java.util.Enumeration r13 = r4.entries()     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            r5 = 1024(0x400, float:1.435E-42)
            byte[] r6 = new byte[r5]     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
        L_0x0059:
            boolean r7 = r13.hasMoreElements()     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            if (r7 == 0) goto L_0x0132
            java.lang.Object r7 = r13.nextElement()     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            java.util.zip.ZipEntry r7 = (java.util.zip.ZipEntry) r7     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            r8.<init>()     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            java.lang.String r9 = "ze.getName() = "
            r8.append(r9)     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            java.lang.String r9 = r7.getName()     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            r8.append(r9)     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            com.xiaomi.analytics.a.a.a.a(r2, r8)     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            java.lang.String r8 = r7.getName()     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            java.lang.String r9 = "lib/"
            boolean r8 = r8.startsWith(r9)     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            if (r8 != 0) goto L_0x008a
            goto L_0x0059
        L_0x008a:
            boolean r8 = r7.isDirectory()     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            if (r8 != 0) goto L_0x0059
            java.lang.String r8 = r7.getName()     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            java.lang.String r8 = b((java.lang.String) r8)     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            java.lang.String r9 = r7.getName()     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            java.lang.String r9 = a((java.lang.String) r9)     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            java.lang.Object r10 = r3.get(r8)     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            java.lang.String r10 = (java.lang.String) r10     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            boolean r11 = android.text.TextUtils.isEmpty(r10)     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            if (r11 != 0) goto L_0x00b9
            int r11 = a(r12, r9)     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            if (r11 < 0) goto L_0x0059
            int r10 = a(r12, r10)     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            if (r11 < r10) goto L_0x00b9
            goto L_0x0059
        L_0x00b9:
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            r10.<init>()     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            java.lang.String r11 = "use abi "
            r10.append(r11)     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            r10.append(r9)     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            java.lang.String r10 = r10.toString()     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            com.xiaomi.analytics.a.a.a.a(r2, r10)     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            r3.put(r8, r9)     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            java.io.File r9 = new java.io.File     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            r10.<init>()     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            r10.append(r14)     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            r10.append(r8)     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            java.lang.String r8 = r10.toString()     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            r9.<init>(r8)     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            boolean r8 = r9.exists()     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            if (r8 == 0) goto L_0x00ed
            r9.delete()     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
        L_0x00ed:
            java.io.FileOutputStream r8 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0119, all -> 0x0116 }
            r8.<init>(r9)     // Catch:{ Exception -> 0x0119, all -> 0x0116 }
            java.io.BufferedInputStream r9 = new java.io.BufferedInputStream     // Catch:{ Exception -> 0x0113, all -> 0x0111 }
            java.io.InputStream r7 = r4.getInputStream(r7)     // Catch:{ Exception -> 0x0113, all -> 0x0111 }
            r9.<init>(r7)     // Catch:{ Exception -> 0x0113, all -> 0x0111 }
        L_0x00fb:
            r7 = 0
            int r10 = r9.read(r6, r7, r5)     // Catch:{ Exception -> 0x010f }
            r11 = -1
            if (r10 == r11) goto L_0x0107
            r8.write(r6, r7, r10)     // Catch:{ Exception -> 0x010f }
            goto L_0x00fb
        L_0x0107:
            com.xiaomi.analytics.a.a.g.a((java.io.Closeable) r9)     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
        L_0x010a:
            com.xiaomi.analytics.a.a.g.a((java.io.Closeable) r8)     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            goto L_0x0059
        L_0x010f:
            r7 = move-exception
            goto L_0x011c
        L_0x0111:
            r12 = move-exception
            goto L_0x012b
        L_0x0113:
            r7 = move-exception
            r9 = r1
            goto L_0x011c
        L_0x0116:
            r12 = move-exception
            r8 = r1
            goto L_0x012b
        L_0x0119:
            r7 = move-exception
            r8 = r1
            r9 = r8
        L_0x011c:
            java.lang.String r10 = com.xiaomi.analytics.a.a.a.a(r2)     // Catch:{ all -> 0x0129 }
            java.lang.String r11 = "extractSo while e"
            android.util.Log.e(r10, r11, r7)     // Catch:{ all -> 0x0129 }
            com.xiaomi.analytics.a.a.g.a((java.io.Closeable) r9)     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            goto L_0x010a
        L_0x0129:
            r12 = move-exception
            r1 = r9
        L_0x012b:
            com.xiaomi.analytics.a.a.g.a((java.io.Closeable) r1)     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            com.xiaomi.analytics.a.a.g.a((java.io.Closeable) r8)     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
            throw r12     // Catch:{ Exception -> 0x0138, all -> 0x0136 }
        L_0x0132:
            r4.close()     // Catch:{ Exception -> 0x0149 }
            goto L_0x0151
        L_0x0136:
            r12 = move-exception
            goto L_0x0152
        L_0x0138:
            r12 = move-exception
            r1 = r4
        L_0x013a:
            java.lang.String r13 = com.xiaomi.analytics.a.a.a.a(r2)     // Catch:{ all -> 0x0021 }
            java.lang.String r14 = "extractSo e"
            android.util.Log.e(r13, r14, r12)     // Catch:{ all -> 0x0021 }
            if (r1 == 0) goto L_0x0151
            r1.close()     // Catch:{ Exception -> 0x0149 }
            goto L_0x0151
        L_0x0149:
            r12 = move-exception
            java.lang.String r13 = com.xiaomi.analytics.a.a.a.a(r2)
            android.util.Log.e(r13, r0, r12)
        L_0x0151:
            return
        L_0x0152:
            if (r4 == 0) goto L_0x0160
            r4.close()     // Catch:{ Exception -> 0x0158 }
            goto L_0x0160
        L_0x0158:
            r13 = move-exception
            java.lang.String r14 = com.xiaomi.analytics.a.a.a.a(r2)
            android.util.Log.e(r14, r0, r13)
        L_0x0160:
            throw r12
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.analytics.a.a.c.a(android.content.Context, java.lang.String, java.lang.String):void");
    }

    private static String b(Context context) {
        try {
            ApplicationInfo applicationInfo = context.getApplicationInfo();
            Field declaredField = Class.forName("android.content.pm.ApplicationInfo").getDeclaredField("primaryCpuAbi");
            declaredField.setAccessible(true);
            return (String) declaredField.get(applicationInfo);
        } catch (Throwable unused) {
            return null;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:1:0x0002, code lost:
        r0 = r2.split("/");
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String b(java.lang.String r2) {
        /*
            if (r2 == 0) goto L_0x0012
            java.lang.String r0 = "/"
            java.lang.String[] r0 = r2.split(r0)
            if (r0 == 0) goto L_0x0012
            int r1 = r0.length
            if (r1 <= 0) goto L_0x0012
            int r2 = r0.length
            int r2 = r2 + -1
            r2 = r0[r2]
        L_0x0012:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.analytics.a.a.c.b(java.lang.String):java.lang.String");
    }
}
