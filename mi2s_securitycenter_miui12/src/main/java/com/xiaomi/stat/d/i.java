package com.xiaomi.stat.d;

import android.text.TextUtils;
import com.xiaomi.stat.ak;
import com.xiaomi.stat.d;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class i {

    /* renamed from: a  reason: collision with root package name */
    public static final int f8527a = 10000;

    /* renamed from: b  reason: collision with root package name */
    public static final int f8528b = 15000;

    /* renamed from: c  reason: collision with root package name */
    private static final String f8529c = "GET";

    /* renamed from: d  reason: collision with root package name */
    private static final String f8530d = "POST";
    private static final String e = "&";
    private static final String f = "=";
    private static final String g = "UTF-8";

    private i() {
    }

    public static String a(String str) {
        return a(str, (Map<String, String>) null, false);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v14, resolved type: java.io.OutputStream} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v15, resolved type: java.io.OutputStream} */
    /* JADX WARNING: type inference failed for: r0v2, types: [java.io.OutputStream] */
    /* JADX WARNING: type inference failed for: r0v13 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.lang.String a(java.lang.String r11, java.lang.String r12, java.util.Map<java.lang.String, java.lang.String> r13, boolean r14) {
        /*
            java.lang.String r0 = "POST"
            java.lang.String r1 = "GET"
            r2 = 2
            r3 = 3
            r4 = 1
            r5 = 0
            r6 = 0
            if (r13 != 0) goto L_0x000d
            r13 = r6
            goto L_0x0011
        L_0x000d:
            java.lang.String r13 = a((java.util.Map<java.lang.String, java.lang.String>) r13, (boolean) r14)     // Catch:{ IOException -> 0x00b7, all -> 0x00b3 }
        L_0x0011:
            boolean r14 = r1.equals(r11)     // Catch:{ IOException -> 0x00b7, all -> 0x00b3 }
            if (r14 == 0) goto L_0x002e
            if (r13 == 0) goto L_0x002e
            java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00b7, all -> 0x00b3 }
            r14.<init>()     // Catch:{ IOException -> 0x00b7, all -> 0x00b3 }
            r14.append(r12)     // Catch:{ IOException -> 0x00b7, all -> 0x00b3 }
            java.lang.String r7 = "? "
            r14.append(r7)     // Catch:{ IOException -> 0x00b7, all -> 0x00b3 }
            r14.append(r13)     // Catch:{ IOException -> 0x00b7, all -> 0x00b3 }
            java.lang.String r14 = r14.toString()     // Catch:{ IOException -> 0x00b7, all -> 0x00b3 }
            goto L_0x002f
        L_0x002e:
            r14 = r12
        L_0x002f:
            java.net.URL r7 = new java.net.URL     // Catch:{ IOException -> 0x00b7, all -> 0x00b3 }
            r7.<init>(r14)     // Catch:{ IOException -> 0x00b7, all -> 0x00b3 }
            java.net.URLConnection r14 = r7.openConnection()     // Catch:{ IOException -> 0x00b7, all -> 0x00b3 }
            java.net.HttpURLConnection r14 = (java.net.HttpURLConnection) r14     // Catch:{ IOException -> 0x00b7, all -> 0x00b3 }
            r7 = 10000(0x2710, float:1.4013E-41)
            r14.setConnectTimeout(r7)     // Catch:{ IOException -> 0x00b0, all -> 0x00ad }
            r7 = 15000(0x3a98, float:2.102E-41)
            r14.setReadTimeout(r7)     // Catch:{ IOException -> 0x00b0, all -> 0x00ad }
            boolean r7 = r1.equals(r11)     // Catch:{ IOException -> 0x00b0, all -> 0x00ad }
            java.lang.String r8 = "UTF-8"
            if (r7 == 0) goto L_0x0050
            r14.setRequestMethod(r1)     // Catch:{ IOException -> 0x00b0, all -> 0x00ad }
            goto L_0x0075
        L_0x0050:
            boolean r1 = r0.equals(r11)     // Catch:{ IOException -> 0x00b0, all -> 0x00ad }
            if (r1 == 0) goto L_0x0075
            if (r13 == 0) goto L_0x0075
            r14.setRequestMethod(r0)     // Catch:{ IOException -> 0x00b0, all -> 0x00ad }
            java.lang.String r0 = "Content-Type"
            java.lang.String r1 = "application/x-www-form-urlencoded"
            r14.setRequestProperty(r0, r1)     // Catch:{ IOException -> 0x00b0, all -> 0x00ad }
            r14.setDoOutput(r4)     // Catch:{ IOException -> 0x00b0, all -> 0x00ad }
            byte[] r13 = r13.getBytes(r8)     // Catch:{ IOException -> 0x00b0, all -> 0x00ad }
            java.io.OutputStream r0 = r14.getOutputStream()     // Catch:{ IOException -> 0x00b0, all -> 0x00ad }
            int r1 = r13.length     // Catch:{ IOException -> 0x00aa, all -> 0x00a8 }
            r0.write(r13, r5, r1)     // Catch:{ IOException -> 0x00aa, all -> 0x00a8 }
            r0.flush()     // Catch:{ IOException -> 0x00aa, all -> 0x00a8 }
            goto L_0x0076
        L_0x0075:
            r0 = r6
        L_0x0076:
            int r13 = r14.getResponseCode()     // Catch:{ IOException -> 0x00aa, all -> 0x00a8 }
            java.io.InputStream r1 = r14.getInputStream()     // Catch:{ IOException -> 0x00aa, all -> 0x00a8 }
            byte[] r7 = com.xiaomi.stat.d.j.b(r1)     // Catch:{ IOException -> 0x00a6 }
            java.lang.String r9 = "HttpUtil %s succeed url: %s, code: %s"
            java.lang.Object[] r10 = new java.lang.Object[r3]     // Catch:{ IOException -> 0x00a6 }
            r10[r5] = r11     // Catch:{ IOException -> 0x00a6 }
            r10[r4] = r12     // Catch:{ IOException -> 0x00a6 }
            java.lang.Integer r13 = java.lang.Integer.valueOf(r13)     // Catch:{ IOException -> 0x00a6 }
            r10[r2] = r13     // Catch:{ IOException -> 0x00a6 }
            java.lang.String r13 = java.lang.String.format(r9, r10)     // Catch:{ IOException -> 0x00a6 }
            com.xiaomi.stat.d.k.b(r13)     // Catch:{ IOException -> 0x00a6 }
            java.lang.String r13 = new java.lang.String     // Catch:{ IOException -> 0x00a6 }
            r13.<init>(r7, r8)     // Catch:{ IOException -> 0x00a6 }
            com.xiaomi.stat.d.j.a((java.io.InputStream) r1)
            com.xiaomi.stat.d.j.a((java.io.OutputStream) r0)
            com.xiaomi.stat.d.j.a((java.net.HttpURLConnection) r14)
            return r13
        L_0x00a6:
            r13 = move-exception
            goto L_0x00bb
        L_0x00a8:
            r11 = move-exception
            goto L_0x00dc
        L_0x00aa:
            r13 = move-exception
            r1 = r6
            goto L_0x00bb
        L_0x00ad:
            r11 = move-exception
            r0 = r6
            goto L_0x00dc
        L_0x00b0:
            r13 = move-exception
            r0 = r6
            goto L_0x00ba
        L_0x00b3:
            r11 = move-exception
            r14 = r6
            r0 = r14
            goto L_0x00dc
        L_0x00b7:
            r13 = move-exception
            r14 = r6
            r0 = r14
        L_0x00ba:
            r1 = r0
        L_0x00bb:
            java.lang.String r7 = "HttpUtil %s failed, url: %s, error: %s"
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x00da }
            r3[r5] = r11     // Catch:{ all -> 0x00da }
            r3[r4] = r12     // Catch:{ all -> 0x00da }
            java.lang.String r11 = r13.getMessage()     // Catch:{ all -> 0x00da }
            r3[r2] = r11     // Catch:{ all -> 0x00da }
            java.lang.String r11 = java.lang.String.format(r7, r3)     // Catch:{ all -> 0x00da }
            com.xiaomi.stat.d.k.e(r11)     // Catch:{ all -> 0x00da }
            com.xiaomi.stat.d.j.a((java.io.InputStream) r1)
            com.xiaomi.stat.d.j.a((java.io.OutputStream) r0)
            com.xiaomi.stat.d.j.a((java.net.HttpURLConnection) r14)
            return r6
        L_0x00da:
            r11 = move-exception
            r6 = r1
        L_0x00dc:
            com.xiaomi.stat.d.j.a((java.io.InputStream) r6)
            com.xiaomi.stat.d.j.a((java.io.OutputStream) r0)
            com.xiaomi.stat.d.j.a((java.net.HttpURLConnection) r14)
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.stat.d.i.a(java.lang.String, java.lang.String, java.util.Map, boolean):java.lang.String");
    }

    public static String a(String str, Map<String, String> map) {
        return a(str, map, true);
    }

    public static String a(String str, Map<String, String> map, boolean z) {
        return a(f8529c, str, map, z);
    }

    public static String a(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        if (map != null) {
            ArrayList<String> arrayList = new ArrayList<>(map.keySet());
            Collections.sort(arrayList);
            for (String str : arrayList) {
                if (!TextUtils.isEmpty(str)) {
                    sb.append(str);
                    sb.append(map.get(str));
                }
            }
        }
        sb.append(ak.c());
        return g.c(sb.toString());
    }

    private static String a(Map<String, String> map, boolean z) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry next : map.entrySet()) {
            try {
                if (!TextUtils.isEmpty((CharSequence) next.getKey())) {
                    if (sb.length() > 0) {
                        sb.append(e);
                    }
                    sb.append(URLEncoder.encode((String) next.getKey(), "UTF-8"));
                    sb.append(f);
                    sb.append(URLEncoder.encode(next.getValue() == null ? "null" : (String) next.getValue(), "UTF-8"));
                }
            } catch (UnsupportedEncodingException unused) {
                k.e("format params failed");
            }
        }
        if (z) {
            String a2 = a(map);
            if (sb.length() > 0) {
                sb.append(e);
            }
            sb.append(URLEncoder.encode(d.f, "UTF-8"));
            sb.append(f);
            sb.append(URLEncoder.encode(a2, "UTF-8"));
        }
        return sb.toString();
    }

    public static String b(String str, Map<String, String> map) {
        return b(str, map, true);
    }

    public static String b(String str, Map<String, String> map, boolean z) {
        return a(f8530d, str, map, z);
    }
}
