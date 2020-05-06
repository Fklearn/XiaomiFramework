package com.miui.hybrid.accessory.a.c;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.text.TextUtils;
import com.google.android.exoplayer2.C;
import com.miui.activityutil.h;
import com.miui.hybrid.accessory.a.a.c;
import com.miui.networkassistant.utils.HybirdServiceUtil;
import java.io.ByteArrayOutputStream;
import java.io.FilterInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class b {

    /* renamed from: a  reason: collision with root package name */
    public static final Pattern f5483a = Pattern.compile("([^\\s;]+)(.*)");

    /* renamed from: b  reason: collision with root package name */
    public static final Pattern f5484b = Pattern.compile("(.*?charset\\s*=[^a-zA-Z0-9]*)([-a-zA-Z0-9]+)(.*)", 2);

    /* renamed from: c  reason: collision with root package name */
    public static final Pattern f5485c = Pattern.compile("(\\<\\?xml\\s+.*?encoding\\s*=[^a-zA-Z0-9]*)([-a-zA-Z0-9]+)(.*)", 2);

    /* renamed from: d  reason: collision with root package name */
    private static volatile Map<String, String> f5486d = null;

    public static final class a extends FilterInputStream {

        /* renamed from: a  reason: collision with root package name */
        private boolean f5487a;

        public a(InputStream inputStream) {
            super(inputStream);
        }

        public int read(byte[] bArr, int i, int i2) {
            int read;
            if (!this.f5487a && (read = super.read(bArr, i, i2)) != -1) {
                return read;
            }
            this.f5487a = true;
            return -1;
        }
    }

    /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int a(android.content.Context r2) {
        /*
            r0 = -1
            java.lang.String r1 = "connectivity"
            java.lang.Object r2 = r2.getSystemService(r1)     // Catch:{ Exception -> 0x0018 }
            android.net.ConnectivityManager r2 = (android.net.ConnectivityManager) r2     // Catch:{ Exception -> 0x0018 }
            if (r2 != 0) goto L_0x000c
            return r0
        L_0x000c:
            android.net.NetworkInfo r2 = r2.getActiveNetworkInfo()     // Catch:{  }
            if (r2 != 0) goto L_0x0013
            return r0
        L_0x0013:
            int r2 = r2.getType()
            return r2
        L_0x0018:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.hybrid.accessory.a.c.b.a(android.content.Context):int");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:35:0x00bb, code lost:
        r4 = th;
     */
    /* JADX WARNING: Missing exception handler attribute for start block: B:29:0x00a0 */
    /* JADX WARNING: Removed duplicated region for block: B:35:0x00bb A[ExcHandler: Throwable (th java.lang.Throwable), Splitter:B:22:0x0068] */
    /* JADX WARNING: Removed duplicated region for block: B:42:0x00ca A[SYNTHETIC, Splitter:B:42:0x00ca] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.miui.hybrid.accessory.a.c.a a(android.content.Context r4, java.lang.String r5, java.lang.String r6, java.util.Map<java.lang.String, java.lang.String> r7, java.lang.String r8) {
        /*
            java.lang.String r0 = "MiuiNetworkUtils"
            com.miui.hybrid.accessory.a.c.a r1 = new com.miui.hybrid.accessory.a.c.a
            r1.<init>()
            r2 = 0
            java.net.URL r5 = a((java.lang.String) r5)     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            java.net.HttpURLConnection r4 = a((android.content.Context) r4, (java.net.URL) r5)     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            r5 = 10000(0x2710, float:1.4013E-41)
            r4.setConnectTimeout(r5)     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            r5 = 15000(0x3a98, float:2.102E-41)
            r4.setReadTimeout(r5)     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            if (r6 != 0) goto L_0x001e
            java.lang.String r6 = "GET"
        L_0x001e:
            r4.setRequestMethod(r6)     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            if (r7 == 0) goto L_0x0041
            java.util.Set r5 = r7.keySet()     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            java.util.Iterator r5 = r5.iterator()     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
        L_0x002b:
            boolean r6 = r5.hasNext()     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            if (r6 == 0) goto L_0x0041
            java.lang.Object r6 = r5.next()     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            java.lang.String r6 = (java.lang.String) r6     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            java.lang.Object r3 = r7.get(r6)     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            java.lang.String r3 = (java.lang.String) r3     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            r4.setRequestProperty(r6, r3)     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            goto L_0x002b
        L_0x0041:
            boolean r5 = android.text.TextUtils.isEmpty(r8)     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            r6 = 0
            r7 = 1
            if (r5 != 0) goto L_0x0068
            r4.setDoOutput(r7)     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            byte[] r5 = r8.getBytes()     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            java.io.OutputStream r8 = r4.getOutputStream()     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            int r3 = r5.length     // Catch:{ IOException -> 0x0065, Throwable -> 0x0062, all -> 0x005f }
            r8.write(r5, r6, r3)     // Catch:{ IOException -> 0x0065, Throwable -> 0x0062, all -> 0x005f }
            r8.flush()     // Catch:{ IOException -> 0x0065, Throwable -> 0x0062, all -> 0x005f }
            r8.close()     // Catch:{ IOException -> 0x0065, Throwable -> 0x0062, all -> 0x005f }
            goto L_0x0068
        L_0x005f:
            r4 = move-exception
            r2 = r8
            goto L_0x00c8
        L_0x0062:
            r4 = move-exception
            r2 = r8
            goto L_0x00bc
        L_0x0065:
            r4 = move-exception
            r2 = r8
            goto L_0x00c7
        L_0x0068:
            int r5 = r4.getResponseCode()     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            r1.f5479a = r5     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            r5.<init>()     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            java.lang.String r8 = "Http POST Response Code: "
            r5.append(r8)     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            int r8 = r1.f5479a     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            r5.append(r8)     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            java.lang.String r5 = r5.toString()     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            com.miui.hybrid.accessory.a.b.a.b(r0, r5)     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
        L_0x0084:
            java.lang.String r5 = r4.getHeaderFieldKey(r6)     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            java.lang.String r8 = r4.getHeaderField(r6)     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            if (r5 != 0) goto L_0x00b0
            if (r8 != 0) goto L_0x00b0
            com.miui.hybrid.accessory.a.c.b$a r5 = new com.miui.hybrid.accessory.a.c.b$a     // Catch:{ IOException -> 0x00a0, Throwable -> 0x00bb }
            java.io.InputStream r6 = r4.getInputStream()     // Catch:{ IOException -> 0x00a0, Throwable -> 0x00bb }
            r5.<init>(r6)     // Catch:{ IOException -> 0x00a0, Throwable -> 0x00bb }
            byte[] r5 = a((java.io.InputStream) r5)     // Catch:{ IOException -> 0x00a0, Throwable -> 0x00bb }
            r1.f5481c = r5     // Catch:{ IOException -> 0x00a0, Throwable -> 0x00bb }
            goto L_0x00af
        L_0x00a0:
            com.miui.hybrid.accessory.a.c.b$a r5 = new com.miui.hybrid.accessory.a.c.b$a     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            java.io.InputStream r4 = r4.getErrorStream()     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            r5.<init>(r4)     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            byte[] r4 = a((java.io.InputStream) r5)     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            r1.f5481c = r4     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
        L_0x00af:
            return r1
        L_0x00b0:
            java.util.Map<java.lang.String, java.lang.String> r3 = r1.f5480b     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            r3.put(r5, r8)     // Catch:{ IOException -> 0x00c6, Throwable -> 0x00bb }
            int r6 = r6 + 1
            int r6 = r6 + r7
            goto L_0x0084
        L_0x00b9:
            r4 = move-exception
            goto L_0x00c8
        L_0x00bb:
            r4 = move-exception
        L_0x00bc:
            java.io.IOException r5 = new java.io.IOException     // Catch:{ all -> 0x00b9 }
            java.lang.String r4 = r4.getMessage()     // Catch:{ all -> 0x00b9 }
            r5.<init>(r4)     // Catch:{ all -> 0x00b9 }
            throw r5     // Catch:{ all -> 0x00b9 }
        L_0x00c6:
            r4 = move-exception
        L_0x00c7:
            throw r4     // Catch:{ all -> 0x00b9 }
        L_0x00c8:
            if (r2 == 0) goto L_0x00d4
            r2.close()     // Catch:{ IOException -> 0x00ce }
            goto L_0x00d4
        L_0x00ce:
            r5 = move-exception
            java.lang.String r6 = " error while closing strean"
            com.miui.hybrid.accessory.a.b.a.b(r0, r6, r5)
        L_0x00d4:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.hybrid.accessory.a.c.b.a(android.content.Context, java.lang.String, java.lang.String, java.util.Map, java.lang.String):com.miui.hybrid.accessory.a.c.a");
    }

    public static a a(Context context, String str, Map<String, String> map, boolean z) {
        if (z) {
            if (map == null) {
                map = new HashMap<>();
            }
            map.putAll(k(context));
        }
        return a(context, str, "POST", (Map<String, String>) null, a(map));
    }

    public static String a(URL url) {
        StringBuilder sb = new StringBuilder();
        sb.append(url.getProtocol());
        sb.append("://");
        sb.append("10.0.0.172");
        sb.append(url.getPath());
        if (!TextUtils.isEmpty(url.getQuery())) {
            sb.append("?");
            sb.append(url.getQuery());
        }
        return sb.toString();
    }

    public static String a(Map<String, String> map) {
        if (map == null || map.size() <= 0) {
            return null;
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (Map.Entry next : map.entrySet()) {
            if (!(next.getKey() == null || next.getValue() == null)) {
                try {
                    stringBuffer.append(URLEncoder.encode((String) next.getKey(), C.UTF8_NAME));
                    stringBuffer.append("=");
                    stringBuffer.append(URLEncoder.encode((String) next.getValue(), C.UTF8_NAME));
                    stringBuffer.append("&");
                } catch (UnsupportedEncodingException e) {
                    com.miui.hybrid.accessory.a.b.a.c("MiuiNetworkUtils", " Failed to convert from params map to string: " + e.toString());
                    com.miui.hybrid.accessory.a.b.a.c("MiuiNetworkUtils", " map: " + map.toString());
                    return null;
                }
            }
        }
        if (stringBuffer.length() > 0) {
            stringBuffer = stringBuffer.deleteCharAt(stringBuffer.length() - 1);
        }
        return stringBuffer.toString();
    }

    public static HttpURLConnection a(Context context, URL url) {
        URLConnection openConnection;
        if ("http".equals(url.getProtocol())) {
            if (c(context)) {
                openConnection = url.openConnection(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.0.0.200", 80)));
                return (HttpURLConnection) openConnection;
            } else if (b(context)) {
                String host = url.getHost();
                HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(a(url)).openConnection();
                httpURLConnection.addRequestProperty("X-Online-Host", host);
                return httpURLConnection;
            }
        }
        openConnection = url.openConnection();
        return (HttpURLConnection) openConnection;
    }

    private static URL a(String str) {
        return new URL(str);
    }

    public static boolean a(String str, OutputStream outputStream) {
        return a(str, outputStream, false, (Context) null);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0066, code lost:
        if (r8 == null) goto L_0x0087;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:?, code lost:
        r8.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x0084, code lost:
        if (r8 == null) goto L_0x0087;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean a(java.lang.String r7, java.io.OutputStream r8, boolean r9, android.content.Context r10) {
        /*
            java.lang.String r0 = " error while download file"
            java.lang.String r1 = "MiuiNetworkUtils"
            r2 = 0
            r3 = 0
            java.net.URL r4 = new java.net.URL     // Catch:{ IOException -> 0x006c, Throwable -> 0x004e }
            r4.<init>(r7)     // Catch:{ IOException -> 0x006c, Throwable -> 0x004e }
            java.net.URLConnection r7 = r4.openConnection()     // Catch:{ IOException -> 0x006c, Throwable -> 0x004e }
            java.net.HttpURLConnection r7 = (java.net.HttpURLConnection) r7     // Catch:{ IOException -> 0x006c, Throwable -> 0x004e }
            r4 = 10000(0x2710, float:1.4013E-41)
            r7.setConnectTimeout(r4)     // Catch:{ IOException -> 0x006c, Throwable -> 0x004e }
            r4 = 15000(0x3a98, float:2.102E-41)
            r7.setReadTimeout(r4)     // Catch:{ IOException -> 0x006c, Throwable -> 0x004e }
            r4 = 1
            java.net.HttpURLConnection.setFollowRedirects(r4)     // Catch:{ IOException -> 0x006c, Throwable -> 0x004e }
            r7.connect()     // Catch:{ IOException -> 0x006c, Throwable -> 0x004e }
            java.io.InputStream r3 = r7.getInputStream()     // Catch:{ IOException -> 0x006c, Throwable -> 0x004e }
            r7 = 1024(0x400, float:1.435E-42)
            byte[] r7 = new byte[r7]     // Catch:{ IOException -> 0x006c, Throwable -> 0x004e }
        L_0x002a:
            int r5 = r3.read(r7)     // Catch:{ IOException -> 0x006c, Throwable -> 0x004e }
            r6 = -1
            if (r5 == r6) goto L_0x003f
            r8.write(r7, r2, r5)     // Catch:{ IOException -> 0x006c, Throwable -> 0x004e }
            if (r9 == 0) goto L_0x002a
            if (r10 == 0) goto L_0x002a
            boolean r5 = f(r10)     // Catch:{ IOException -> 0x006c, Throwable -> 0x004e }
            if (r5 != 0) goto L_0x002a
            r2 = r4
        L_0x003f:
            r7 = r2 ^ 1
            if (r3 == 0) goto L_0x0046
            r3.close()     // Catch:{ IOException -> 0x0046 }
        L_0x0046:
            if (r8 == 0) goto L_0x004b
            r8.close()     // Catch:{ IOException -> 0x004b }
        L_0x004b:
            return r7
        L_0x004c:
            r7 = move-exception
            goto L_0x0088
        L_0x004e:
            r7 = move-exception
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x004c }
            r9.<init>()     // Catch:{ all -> 0x004c }
            r9.append(r0)     // Catch:{ all -> 0x004c }
            r9.append(r7)     // Catch:{ all -> 0x004c }
            java.lang.String r7 = r9.toString()     // Catch:{ all -> 0x004c }
            com.miui.hybrid.accessory.a.b.a.e(r1, r7)     // Catch:{ all -> 0x004c }
            if (r3 == 0) goto L_0x0066
            r3.close()     // Catch:{ IOException -> 0x0066 }
        L_0x0066:
            if (r8 == 0) goto L_0x0087
        L_0x0068:
            r8.close()     // Catch:{ IOException -> 0x0087 }
            goto L_0x0087
        L_0x006c:
            r7 = move-exception
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x004c }
            r9.<init>()     // Catch:{ all -> 0x004c }
            r9.append(r0)     // Catch:{ all -> 0x004c }
            r9.append(r7)     // Catch:{ all -> 0x004c }
            java.lang.String r7 = r9.toString()     // Catch:{ all -> 0x004c }
            com.miui.hybrid.accessory.a.b.a.e(r1, r7)     // Catch:{ all -> 0x004c }
            if (r3 == 0) goto L_0x0084
            r3.close()     // Catch:{ IOException -> 0x0084 }
        L_0x0084:
            if (r8 == 0) goto L_0x0087
            goto L_0x0068
        L_0x0087:
            return r2
        L_0x0088:
            if (r3 == 0) goto L_0x008d
            r3.close()     // Catch:{ IOException -> 0x008d }
        L_0x008d:
            if (r8 == 0) goto L_0x0092
            r8.close()     // Catch:{ IOException -> 0x0092 }
        L_0x0092:
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.hybrid.accessory.a.c.b.a(java.lang.String, java.io.OutputStream, boolean, android.content.Context):boolean");
    }

    private static byte[] a(InputStream inputStream) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bArr = new byte[1024];
        while (true) {
            int read = inputStream.read(bArr);
            if (read == -1) {
                return byteArrayOutputStream.toByteArray();
            }
            byteArrayOutputStream.write(bArr, 0, read);
        }
    }

    /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean b(android.content.Context r8) {
        /*
            java.lang.String r0 = "phone"
            java.lang.Object r0 = r8.getSystemService(r0)
            android.telephony.TelephonyManager r0 = (android.telephony.TelephonyManager) r0
            java.lang.String r0 = r0.getSimCountryIso()
            java.lang.String r1 = "CN"
            boolean r0 = r1.equalsIgnoreCase(r0)
            r1 = 0
            if (r0 != 0) goto L_0x0016
            return r1
        L_0x0016:
            java.lang.String r0 = "connectivity"
            java.lang.Object r8 = r8.getSystemService(r0)     // Catch:{ Exception -> 0x0053 }
            android.net.ConnectivityManager r8 = (android.net.ConnectivityManager) r8     // Catch:{ Exception -> 0x0053 }
            if (r8 != 0) goto L_0x0021
            return r1
        L_0x0021:
            android.net.NetworkInfo r8 = r8.getActiveNetworkInfo()     // Catch:{  }
            if (r8 != 0) goto L_0x0028
            return r1
        L_0x0028:
            java.lang.String r2 = r8.getExtraInfo()
            boolean r8 = android.text.TextUtils.isEmpty(r2)
            if (r8 != 0) goto L_0x0053
            int r8 = r2.length()
            r0 = 3
            if (r8 >= r0) goto L_0x003a
            goto L_0x0053
        L_0x003a:
            java.lang.String r8 = "ctwap"
            boolean r8 = r2.contains(r8)
            if (r8 == 0) goto L_0x0043
            return r1
        L_0x0043:
            r3 = 1
            int r8 = r2.length()
            int r4 = r8 + -3
            r6 = 0
            r7 = 3
            java.lang.String r5 = "wap"
            boolean r8 = r2.regionMatches(r3, r4, r5, r6, r7)
            return r8
        L_0x0053:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.hybrid.accessory.a.c.b.b(android.content.Context):boolean");
    }

    /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean c(android.content.Context r3) {
        /*
            java.lang.String r0 = "phone"
            java.lang.Object r0 = r3.getSystemService(r0)
            android.telephony.TelephonyManager r0 = (android.telephony.TelephonyManager) r0
            java.lang.String r0 = r0.getSimCountryIso()
            java.lang.String r1 = "CN"
            boolean r0 = r1.equalsIgnoreCase(r0)
            r1 = 0
            if (r0 != 0) goto L_0x0016
            return r1
        L_0x0016:
            java.lang.String r0 = "connectivity"
            java.lang.Object r3 = r3.getSystemService(r0)     // Catch:{ Exception -> 0x0044 }
            android.net.ConnectivityManager r3 = (android.net.ConnectivityManager) r3     // Catch:{ Exception -> 0x0044 }
            if (r3 != 0) goto L_0x0021
            return r1
        L_0x0021:
            android.net.NetworkInfo r3 = r3.getActiveNetworkInfo()     // Catch:{  }
            if (r3 != 0) goto L_0x0028
            return r1
        L_0x0028:
            java.lang.String r3 = r3.getExtraInfo()
            boolean r0 = android.text.TextUtils.isEmpty(r3)
            if (r0 != 0) goto L_0x0044
            int r0 = r3.length()
            r2 = 3
            if (r0 >= r2) goto L_0x003a
            goto L_0x0044
        L_0x003a:
            java.lang.String r0 = "ctwap"
            boolean r3 = r3.contains(r0)
            if (r3 == 0) goto L_0x0044
            r3 = 1
            return r3
        L_0x0044:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.hybrid.accessory.a.c.b.c(android.content.Context):boolean");
    }

    public static boolean d(Context context) {
        return a(context) >= 0;
    }

    public static String e(Context context) {
        return f(context) ? "wifi" : g(context) ? "4g" : h(context) ? "3g" : i(context) ? "2g" : h.f2289a;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:6:?, code lost:
        r2 = r2.getActiveNetworkInfo();
     */
    /* JADX WARNING: No exception handlers in catch block: Catch:{  } */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean f(android.content.Context r2) {
        /*
            r0 = 0
            java.lang.String r1 = "connectivity"
            java.lang.Object r2 = r2.getSystemService(r1)     // Catch:{ Exception -> 0x001b }
            android.net.ConnectivityManager r2 = (android.net.ConnectivityManager) r2     // Catch:{ Exception -> 0x001b }
            if (r2 != 0) goto L_0x000c
            return r0
        L_0x000c:
            android.net.NetworkInfo r2 = r2.getActiveNetworkInfo()     // Catch:{  }
            if (r2 != 0) goto L_0x0013
            return r0
        L_0x0013:
            int r2 = r2.getType()
            r1 = 1
            if (r1 != r2) goto L_0x001b
            r0 = r1
        L_0x001b:
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.hybrid.accessory.a.c.b.f(android.content.Context):boolean");
    }

    public static boolean g(Context context) {
        NetworkInfo j = j(context);
        return j != null && j.getType() == 0 && 13 == j.getSubtype();
    }

    public static boolean h(Context context) {
        NetworkInfo j = j(context);
        if (j == null || j.getType() != 0) {
            return false;
        }
        String subtypeName = j.getSubtypeName();
        if (!"TD-SCDMA".equalsIgnoreCase(subtypeName) && !"CDMA2000".equalsIgnoreCase(subtypeName) && !"WCDMA".equalsIgnoreCase(subtypeName)) {
            switch (j.getSubtype()) {
                case 3:
                case 5:
                case 6:
                case 8:
                case 9:
                case 10:
                case 12:
                case 14:
                case 15:
                    break;
                default:
                    return false;
            }
        }
        return true;
    }

    public static boolean i(Context context) {
        NetworkInfo j = j(context);
        if (j == null || j.getType() != 0) {
            return false;
        }
        int subtype = j.getSubtype();
        return subtype == 1 || subtype == 2 || subtype == 4 || subtype == 7 || subtype == 11;
    }

    public static NetworkInfo j(Context context) {
        try {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService("connectivity");
            if (connectivityManager == null) {
                return null;
            }
            return connectivityManager.getActiveNetworkInfo();
        } catch (Exception unused) {
            return null;
        }
    }

    public static Map<String, String> k(Context context) {
        if (f5486d == null) {
            HashMap hashMap = new HashMap();
            hashMap.put("android_os_version", String.valueOf(Build.VERSION.SDK_INT));
            hashMap.put("os_version_type", c.b());
            hashMap.put("device_model", Build.DEVICE);
            String a2 = com.miui.hybrid.accessory.a.a.b.a(context);
            if (!TextUtils.isEmpty(a2)) {
                hashMap.put("imei_md5", com.miui.hybrid.accessory.a.e.a.a(a2));
            } else {
                com.miui.hybrid.accessory.a.b.a.d("MiuiNetworkUtils", "NetWork will do http post request without device id. ");
            }
            hashMap.put("hybrid_version_code", String.valueOf(com.miui.hybrid.accessory.a.a.a.a(context, HybirdServiceUtil.HYBIRD_PACKAGE_NAME)));
            hashMap.put("platform_version", String.valueOf(com.miui.hybrid.accessory.a.a.a.a(context, HybirdServiceUtil.HYBIRD_PACKAGE_NAME, "platformVersion", -1)));
            f5486d = hashMap;
        }
        HashMap hashMap2 = new HashMap();
        hashMap2.putAll(f5486d);
        hashMap2.put("network_type", e(context));
        return hashMap2;
    }
}
