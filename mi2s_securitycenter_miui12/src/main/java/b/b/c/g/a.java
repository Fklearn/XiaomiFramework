package b.b.c.g;

import android.util.Log;
import com.google.android.exoplayer2.C;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.List;

public class a {

    /* renamed from: a  reason: collision with root package name */
    private static byte[] f1719a = new byte[1024];

    /* JADX WARNING: Removed duplicated region for block: B:69:0x00ce A[SYNTHETIC, Splitter:B:69:0x00ce] */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x00e5 A[SYNTHETIC, Splitter:B:79:0x00e5] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.io.File a(java.lang.String r8, java.lang.String r9, java.lang.String r10, b.b.c.h.j r11) {
        /*
            r0 = -1
            r1 = 0
            r2 = 0
            java.net.URL r3 = new java.net.URL     // Catch:{ IOException -> 0x00bc, all -> 0x00b7 }
            r3.<init>(r8)     // Catch:{ IOException -> 0x00bc, all -> 0x00b7 }
            java.net.URLConnection r8 = r3.openConnection()     // Catch:{ IOException -> 0x00bc, all -> 0x00b7 }
            java.net.HttpURLConnection r8 = (java.net.HttpURLConnection) r8     // Catch:{ IOException -> 0x00bc, all -> 0x00b7 }
            java.lang.String r3 = "GET"
            r8.setRequestMethod(r3)     // Catch:{ IOException -> 0x00b2, all -> 0x00ad }
            r8.setDoOutput(r2)     // Catch:{ IOException -> 0x00b2, all -> 0x00ad }
            r8.connect()     // Catch:{ IOException -> 0x00b2, all -> 0x00ad }
            int r3 = r8.getResponseCode()     // Catch:{ IOException -> 0x00b2, all -> 0x00ad }
            r4 = 200(0xc8, float:2.8E-43)
            if (r3 != r4) goto L_0x009e
            boolean r4 = android.text.TextUtils.isEmpty(r10)     // Catch:{ IOException -> 0x0098, all -> 0x0095 }
            if (r4 == 0) goto L_0x004d
            java.lang.String r10 = "Content-Disposition"
            java.lang.String r10 = r8.getHeaderField(r10)     // Catch:{ IOException -> 0x0098, all -> 0x0095 }
            if (r10 == 0) goto L_0x0037
            java.lang.String r4 = ".*filename="
            java.lang.String r5 = ""
            java.lang.String r10 = r10.replaceAll(r4, r5)     // Catch:{ IOException -> 0x0098, all -> 0x0095 }
        L_0x0037:
            if (r10 != 0) goto L_0x004d
            b.b.c.h.i.a(r11, r3, r2)
            miui.util.IOUtils.closeQuietly(r1)
            miui.util.IOUtils.closeQuietly(r1)
            if (r8 == 0) goto L_0x004c
            r8.disconnect()     // Catch:{ Exception -> 0x0048 }
            goto L_0x004c
        L_0x0048:
            r8 = move-exception
            r8.printStackTrace()
        L_0x004c:
            return r1
        L_0x004d:
            java.io.InputStream r4 = r8.getInputStream()     // Catch:{ IOException -> 0x0098, all -> 0x0095 }
            java.io.File r5 = new java.io.File     // Catch:{ IOException -> 0x0091, all -> 0x008c }
            r5.<init>(r9, r10)     // Catch:{ IOException -> 0x0091, all -> 0x008c }
            java.io.FileOutputStream r9 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x0091, all -> 0x008c }
            r9.<init>(r5)     // Catch:{ IOException -> 0x0091, all -> 0x008c }
            r10 = 1024(0x400, float:1.435E-42)
            byte[] r10 = new byte[r10]     // Catch:{ IOException -> 0x0085, all -> 0x007d }
        L_0x005f:
            int r6 = r4.read(r10)     // Catch:{ IOException -> 0x0085, all -> 0x007d }
            if (r6 == r0) goto L_0x0069
            r9.write(r10, r2, r6)     // Catch:{ IOException -> 0x0085, all -> 0x007d }
            goto L_0x005f
        L_0x0069:
            b.b.c.h.i.a(r11, r3, r2)
            miui.util.IOUtils.closeQuietly(r4)
            miui.util.IOUtils.closeQuietly(r9)
            if (r8 == 0) goto L_0x007c
            r8.disconnect()     // Catch:{ Exception -> 0x0078 }
            goto L_0x007c
        L_0x0078:
            r8 = move-exception
            r8.printStackTrace()
        L_0x007c:
            return r5
        L_0x007d:
            r10 = move-exception
            r1 = r4
            r7 = r10
            r10 = r8
            r8 = r9
            r9 = r7
            goto L_0x00da
        L_0x0085:
            r10 = move-exception
            r0 = r3
            r7 = r10
            r10 = r8
            r8 = r9
            r9 = r7
            goto L_0x00c0
        L_0x008c:
            r9 = move-exception
            r10 = r8
            r8 = r1
            goto L_0x00d9
        L_0x0091:
            r9 = move-exception
            r10 = r8
            r8 = r1
            goto L_0x009c
        L_0x0095:
            r9 = move-exception
            r10 = r8
            goto L_0x00b0
        L_0x0098:
            r9 = move-exception
            r10 = r8
            r8 = r1
            r4 = r8
        L_0x009c:
            r0 = r3
            goto L_0x00c0
        L_0x009e:
            b.b.c.h.i.a(r11, r3, r2)
            miui.util.IOUtils.closeQuietly(r1)
            miui.util.IOUtils.closeQuietly(r1)
            if (r8 == 0) goto L_0x00d6
            r8.disconnect()     // Catch:{ Exception -> 0x00d2 }
            goto L_0x00d6
        L_0x00ad:
            r9 = move-exception
            r10 = r8
            r3 = r0
        L_0x00b0:
            r8 = r1
            goto L_0x00da
        L_0x00b2:
            r9 = move-exception
            r10 = r8
            r8 = r1
            r4 = r8
            goto L_0x00c0
        L_0x00b7:
            r9 = move-exception
            r3 = r0
            r8 = r1
            r10 = r8
            goto L_0x00da
        L_0x00bc:
            r9 = move-exception
            r8 = r1
            r10 = r8
            r4 = r10
        L_0x00c0:
            r9.printStackTrace()     // Catch:{ all -> 0x00d7 }
            b.b.c.h.i.a(r11, r0, r2)
            miui.util.IOUtils.closeQuietly(r4)
            miui.util.IOUtils.closeQuietly(r8)
            if (r10 == 0) goto L_0x00d6
            r10.disconnect()     // Catch:{ Exception -> 0x00d2 }
            goto L_0x00d6
        L_0x00d2:
            r8 = move-exception
            r8.printStackTrace()
        L_0x00d6:
            return r1
        L_0x00d7:
            r9 = move-exception
            r3 = r0
        L_0x00d9:
            r1 = r4
        L_0x00da:
            b.b.c.h.i.a(r11, r3, r2)
            miui.util.IOUtils.closeQuietly(r1)
            miui.util.IOUtils.closeQuietly(r8)
            if (r10 == 0) goto L_0x00ed
            r10.disconnect()     // Catch:{ Exception -> 0x00e9 }
            goto L_0x00ed
        L_0x00e9:
            r8 = move-exception
            r8.printStackTrace()
        L_0x00ed:
            throw r9
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.c.g.a.a(java.lang.String, java.lang.String, java.lang.String, b.b.c.h.j):java.io.File");
    }

    private static String a(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while (true) {
            int read = inputStream.read(f1719a, 0, 1024);
            if (read <= 0) {
                return new String(byteArrayOutputStream.toByteArray(), C.UTF8_NAME);
            }
            byteArrayOutputStream.write(f1719a, 0, read);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x0055 A[SYNTHETIC, Splitter:B:25:0x0055] */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x006a A[SYNTHETIC, Splitter:B:33:0x006a] */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0077 A[SYNTHETIC, Splitter:B:39:0x0077] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:21:0x0046=Splitter:B:21:0x0046, B:29:0x005b=Splitter:B:29:0x005b} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String a(java.lang.String r4, b.b.c.h.j r5) {
        /*
            r0 = 0
            r1 = 0
            r2 = -1
            java.net.URL r3 = new java.net.URL     // Catch:{ MalformedURLException -> 0x0059, IOException -> 0x0044, all -> 0x0041 }
            r3.<init>(r4)     // Catch:{ MalformedURLException -> 0x0059, IOException -> 0x0044, all -> 0x0041 }
            java.net.URLConnection r4 = r3.openConnection()     // Catch:{ MalformedURLException -> 0x0059, IOException -> 0x0044, all -> 0x0041 }
            java.net.HttpURLConnection r4 = (java.net.HttpURLConnection) r4     // Catch:{ MalformedURLException -> 0x0059, IOException -> 0x0044, all -> 0x0041 }
            java.lang.String r3 = "GET"
            r4.setRequestMethod(r3)     // Catch:{ MalformedURLException -> 0x003f, IOException -> 0x003d }
            r4.setDoOutput(r1)     // Catch:{ MalformedURLException -> 0x003f, IOException -> 0x003d }
            r4.connect()     // Catch:{ MalformedURLException -> 0x003f, IOException -> 0x003d }
            int r2 = r4.getResponseCode()     // Catch:{ MalformedURLException -> 0x003f, IOException -> 0x003d }
            r3 = 200(0xc8, float:2.8E-43)
            if (r2 != r3) goto L_0x002a
            java.io.InputStream r0 = r4.getInputStream()     // Catch:{ MalformedURLException -> 0x003f, IOException -> 0x003d }
            java.lang.String r3 = a((java.io.InputStream) r0)     // Catch:{ MalformedURLException -> 0x003f, IOException -> 0x003d }
            goto L_0x002c
        L_0x002a:
            java.lang.String r3 = ""
        L_0x002c:
            b.b.c.h.i.a(r5, r2, r1)
            miui.util.IOUtils.closeQuietly(r0)
            if (r4 == 0) goto L_0x006d
            r4.disconnect()     // Catch:{ Exception -> 0x0038 }
            goto L_0x006d
        L_0x0038:
            r4 = move-exception
            r4.printStackTrace()
            goto L_0x006d
        L_0x003d:
            r3 = move-exception
            goto L_0x0046
        L_0x003f:
            r3 = move-exception
            goto L_0x005b
        L_0x0041:
            r3 = move-exception
            r4 = r0
            goto L_0x006f
        L_0x0044:
            r3 = move-exception
            r4 = r0
        L_0x0046:
            r3.printStackTrace()     // Catch:{ all -> 0x006e }
            java.lang.String r3 = r3.getMessage()     // Catch:{ all -> 0x006e }
            b.b.c.h.i.a(r5, r2, r1)
            miui.util.IOUtils.closeQuietly(r0)
            if (r4 == 0) goto L_0x006d
            r4.disconnect()     // Catch:{ Exception -> 0x0038 }
            goto L_0x006d
        L_0x0059:
            r3 = move-exception
            r4 = r0
        L_0x005b:
            r3.printStackTrace()     // Catch:{ all -> 0x006e }
            java.lang.String r3 = r3.getMessage()     // Catch:{ all -> 0x006e }
            b.b.c.h.i.a(r5, r2, r1)
            miui.util.IOUtils.closeQuietly(r0)
            if (r4 == 0) goto L_0x006d
            r4.disconnect()     // Catch:{ Exception -> 0x0038 }
        L_0x006d:
            return r3
        L_0x006e:
            r3 = move-exception
        L_0x006f:
            b.b.c.h.i.a(r5, r2, r1)
            miui.util.IOUtils.closeQuietly(r0)
            if (r4 == 0) goto L_0x007f
            r4.disconnect()     // Catch:{ Exception -> 0x007b }
            goto L_0x007f
        L_0x007b:
            r4 = move-exception
            r4.printStackTrace()
        L_0x007f:
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.c.g.a.a(java.lang.String, b.b.c.h.j):java.lang.String");
    }

    /* JADX WARNING: Removed duplicated region for block: B:39:0x00b4 A[SYNTHETIC, Splitter:B:39:0x00b4] */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00cd A[SYNTHETIC, Splitter:B:47:0x00cd] */
    /* JADX WARNING: Removed duplicated region for block: B:53:0x00dd A[SYNTHETIC, Splitter:B:53:0x00dd] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:35:0x00a2=Splitter:B:35:0x00a2, B:43:0x00bb=Splitter:B:43:0x00bb} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static java.lang.String a(java.lang.String r3, java.lang.String r4, java.lang.String r5, java.util.List<b.b.c.g.c> r6, b.b.c.h.j r7) {
        /*
            if (r6 != 0) goto L_0x0007
            java.util.LinkedList r6 = new java.util.LinkedList
            r6.<init>()
        L_0x0007:
            r0 = -1
            b.b.c.g.c r1 = new b.b.c.g.c
            byte[] r3 = r3.getBytes()
            r2 = 2
            java.lang.String r3 = android.util.Base64.encodeToString(r3, r2)
            java.lang.String r2 = "param"
            r1.<init>(r2, r3)
            r6.add(r1)
            b.b.c.g.c r3 = new b.b.c.g.c
            java.lang.String r5 = b.b.c.g.f.a(r6, r5)
            java.lang.String r1 = "sign"
            r3.<init>(r1, r5)
            r6.add(r3)
            java.lang.String r3 = a((java.util.List<b.b.c.g.c>) r6)
            r5 = 0
            r6 = 0
            java.net.URL r1 = new java.net.URL     // Catch:{ MalformedURLException -> 0x00b8, IOException -> 0x009f, all -> 0x009b }
            r1.<init>(r4)     // Catch:{ MalformedURLException -> 0x00b8, IOException -> 0x009f, all -> 0x009b }
            java.net.URLConnection r4 = r1.openConnection()     // Catch:{ MalformedURLException -> 0x00b8, IOException -> 0x009f, all -> 0x009b }
            java.net.HttpURLConnection r4 = (java.net.HttpURLConnection) r4     // Catch:{ MalformedURLException -> 0x00b8, IOException -> 0x009f, all -> 0x009b }
            java.lang.String r1 = "POST"
            r4.setRequestMethod(r1)     // Catch:{ MalformedURLException -> 0x0098, IOException -> 0x0095, all -> 0x0092 }
            r4.setUseCaches(r6)     // Catch:{ MalformedURLException -> 0x0098, IOException -> 0x0095, all -> 0x0092 }
            java.lang.String r1 = "Accept-Charset"
            java.lang.String r2 = "utf-8"
            r4.setRequestProperty(r1, r2)     // Catch:{ MalformedURLException -> 0x0098, IOException -> 0x0095, all -> 0x0092 }
            r4.connect()     // Catch:{ MalformedURLException -> 0x0098, IOException -> 0x0095, all -> 0x0092 }
            boolean r1 = android.text.TextUtils.isEmpty(r3)     // Catch:{ MalformedURLException -> 0x0098, IOException -> 0x0095, all -> 0x0092 }
            if (r1 != 0) goto L_0x0062
            byte[] r3 = r3.getBytes()     // Catch:{ MalformedURLException -> 0x0098, IOException -> 0x0095, all -> 0x0092 }
            java.io.OutputStream r1 = r4.getOutputStream()     // Catch:{ MalformedURLException -> 0x0098, IOException -> 0x0095, all -> 0x0092 }
            int r2 = r3.length     // Catch:{ MalformedURLException -> 0x0090, IOException -> 0x008e }
            r1.write(r3, r6, r2)     // Catch:{ MalformedURLException -> 0x0090, IOException -> 0x008e }
            r1.flush()     // Catch:{ MalformedURLException -> 0x0090, IOException -> 0x008e }
            goto L_0x0063
        L_0x0062:
            r1 = r5
        L_0x0063:
            int r0 = r4.getResponseCode()     // Catch:{ MalformedURLException -> 0x0090, IOException -> 0x008e }
            int r3 = r4.getResponseCode()     // Catch:{ MalformedURLException -> 0x0090, IOException -> 0x008e }
            r2 = 200(0xc8, float:2.8E-43)
            if (r3 != r2) goto L_0x0078
            java.io.InputStream r5 = r4.getInputStream()     // Catch:{ MalformedURLException -> 0x0090, IOException -> 0x008e }
            java.lang.String r3 = a((java.io.InputStream) r5)     // Catch:{ MalformedURLException -> 0x0090, IOException -> 0x008e }
            goto L_0x007a
        L_0x0078:
            java.lang.String r3 = ""
        L_0x007a:
            b.b.c.h.i.a(r7, r0, r6)
            miui.util.IOUtils.closeQuietly(r5)
            miui.util.IOUtils.closeQuietly(r1)
            if (r4 == 0) goto L_0x00d0
            r4.disconnect()     // Catch:{ Exception -> 0x0089 }
            goto L_0x00d0
        L_0x0089:
            r4 = move-exception
            r4.printStackTrace()
            goto L_0x00d0
        L_0x008e:
            r3 = move-exception
            goto L_0x00a2
        L_0x0090:
            r3 = move-exception
            goto L_0x00bb
        L_0x0092:
            r3 = move-exception
            r1 = r5
            goto L_0x00d2
        L_0x0095:
            r3 = move-exception
            r1 = r5
            goto L_0x00a2
        L_0x0098:
            r3 = move-exception
            r1 = r5
            goto L_0x00bb
        L_0x009b:
            r3 = move-exception
            r4 = r5
            r1 = r4
            goto L_0x00d2
        L_0x009f:
            r3 = move-exception
            r4 = r5
            r1 = r4
        L_0x00a2:
            r3.printStackTrace()     // Catch:{ all -> 0x00d1 }
            java.lang.String r3 = r3.getMessage()     // Catch:{ all -> 0x00d1 }
            b.b.c.h.i.a(r7, r0, r6)
            miui.util.IOUtils.closeQuietly(r5)
            miui.util.IOUtils.closeQuietly(r1)
            if (r4 == 0) goto L_0x00d0
            r4.disconnect()     // Catch:{ Exception -> 0x0089 }
            goto L_0x00d0
        L_0x00b8:
            r3 = move-exception
            r4 = r5
            r1 = r4
        L_0x00bb:
            r3.printStackTrace()     // Catch:{ all -> 0x00d1 }
            java.lang.String r3 = r3.getMessage()     // Catch:{ all -> 0x00d1 }
            b.b.c.h.i.a(r7, r0, r6)
            miui.util.IOUtils.closeQuietly(r5)
            miui.util.IOUtils.closeQuietly(r1)
            if (r4 == 0) goto L_0x00d0
            r4.disconnect()     // Catch:{ Exception -> 0x0089 }
        L_0x00d0:
            return r3
        L_0x00d1:
            r3 = move-exception
        L_0x00d2:
            b.b.c.h.i.a(r7, r0, r6)
            miui.util.IOUtils.closeQuietly(r5)
            miui.util.IOUtils.closeQuietly(r1)
            if (r4 == 0) goto L_0x00e5
            r4.disconnect()     // Catch:{ Exception -> 0x00e1 }
            goto L_0x00e5
        L_0x00e1:
            r4 = move-exception
            r4.printStackTrace()
        L_0x00e5:
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.c.g.a.a(java.lang.String, java.lang.String, java.lang.String, java.util.List, b.b.c.h.j):java.lang.String");
    }

    public static String a(List<c> list) {
        StringBuffer stringBuffer = new StringBuffer();
        if (list != null) {
            for (c next : list) {
                try {
                    if (next.b() != null) {
                        stringBuffer.append(URLEncoder.encode(next.a(), C.UTF8_NAME));
                        stringBuffer.append("=");
                        stringBuffer.append(URLEncoder.encode(next.b(), C.UTF8_NAME));
                        stringBuffer.append("&");
                    }
                } catch (Exception e) {
                    Log.i("WebApiAccessHelper", "Failed to convert from param list to string: " + e.toString());
                    Log.i("WebApiAccessHelper", "pair: " + next.toString());
                    return null;
                }
            }
        }
        if (stringBuffer.length() > 0) {
            stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        }
        return stringBuffer.toString();
    }
}
