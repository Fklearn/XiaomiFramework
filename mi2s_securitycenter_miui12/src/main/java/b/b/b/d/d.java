package b.b.b.d;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.h.j;
import com.google.android.exoplayer2.C;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.TreeMap;
import org.json.JSONArray;
import org.json.JSONException;

public class d {

    /* renamed from: a  reason: collision with root package name */
    protected JSONArray f1509a;

    /* renamed from: b  reason: collision with root package name */
    protected URL f1510b;

    /* renamed from: c  reason: collision with root package name */
    protected C0025d f1511c;

    /* renamed from: d  reason: collision with root package name */
    protected boolean f1512d;
    private byte[] e;
    private String f;
    protected Context g;

    protected class a extends Exception {

        /* renamed from: a  reason: collision with root package name */
        protected c f1513a;
    }

    protected class b extends e {
        public b(ByteArrayOutputStream byteArrayOutputStream) {
            super(byteArrayOutputStream);
        }

        public void a() {
            ((ByteArrayOutputStream) this.f1522a).reset();
        }
    }

    public enum c {
        OK,
        URL_ERROR,
        NETWORK_ERROR,
        AUTH_ERROR,
        CLIENT_ERROR,
        SERVER_ERROR,
        RESULT_ERROR,
        UNKNOWN_ERROR
    }

    /* renamed from: b.b.b.d.d$d  reason: collision with other inner class name */
    public class C0025d {

        /* renamed from: a  reason: collision with root package name */
        private TreeMap<String, String> f1519a;

        /* renamed from: b  reason: collision with root package name */
        private boolean f1520b;

        public C0025d(d dVar) {
            this(true);
        }

        public C0025d(boolean z) {
            this.f1520b = false;
            this.f1519a = new TreeMap<>();
            this.f1520b = false;
            if (z) {
                d.this.f1511c = this;
            }
        }

        public C0025d a(String str, String str2) {
            if (TextUtils.isEmpty(str2)) {
                if (this.f1520b) {
                    return this;
                }
                str2 = "";
            }
            this.f1519a.put(str, str2);
            return this;
        }

        public String a(char c2) {
            if (this.f1519a.isEmpty()) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            for (String next : this.f1519a.keySet()) {
                StringBuilder unused = d.b(sb, next, this.f1519a.get(next), c2);
            }
            return sb.toString();
        }

        public String a(String str) {
            if (this.f1519a.isEmpty()) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            for (String next : this.f1519a.keySet()) {
                StringBuilder unused = d.b(sb, next, this.f1519a.get(next), str);
            }
            return sb.toString();
        }

        public boolean a() {
            return this.f1519a.isEmpty();
        }

        public String b() {
            return a(C.UTF8_NAME);
        }

        public String toString() {
            return a('&');
        }
    }

    protected abstract class e extends OutputStream {

        /* renamed from: a  reason: collision with root package name */
        protected OutputStream f1522a;

        public e(OutputStream outputStream) {
            if (outputStream != null) {
                this.f1522a = outputStream;
                return;
            }
            throw new IllegalArgumentException("outputstream is null");
        }

        public abstract void a();

        public void close() {
            this.f1522a.close();
        }

        public void flush() {
            this.f1522a.flush();
        }

        public void write(int i) {
            this.f1522a.write(i);
        }

        public void write(byte[] bArr) {
            this.f1522a.write(bArr);
        }

        public void write(byte[] bArr, int i, int i2) {
            this.f1522a.write(bArr, i, i2);
        }
    }

    public d(String str, Context context) {
        URL url;
        this.g = context;
        try {
            url = new URL(str);
        } catch (MalformedURLException e2) {
            Log.e("PaySafetyConnection", "URL error: " + e2);
            url = null;
        }
        b(url);
    }

    private c a(int i) {
        if (i == 200) {
            return c.OK;
        }
        Log.e("PaySafetyConnection", "Network Error : " + i);
        return c.SERVER_ERROR;
    }

    /* JADX WARNING: type inference failed for: r0v14, types: [java.net.URLConnection] */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:105:0x019f  */
    /* JADX WARNING: Removed duplicated region for block: B:114:0x011c A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:119:0x01b8 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:61:0x0109 A[SYNTHETIC, Splitter:B:61:0x0109] */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x0111  */
    /* JADX WARNING: Removed duplicated region for block: B:68:0x0119 A[SYNTHETIC, Splitter:B:68:0x0119] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private b.b.b.d.d.c a(java.lang.String r17, byte[] r18, boolean r19, b.b.b.d.d.e r20, b.b.c.h.j r21) {
        /*
            r16 = this;
            r1 = r16
            r2 = r18
            r3 = r20
            r4 = r21
            java.lang.String r5 = "Connection Exception for "
            r6 = 1
            java.lang.String[] r0 = new java.lang.String[r6]
            r7 = 0
            r0[r7] = r17
            java.util.ArrayList r0 = com.miui.applicationlock.c.H.a(r0)
            java.util.Iterator r8 = r0.iterator()
        L_0x0018:
            boolean r0 = r8.hasNext()
            if (r0 == 0) goto L_0x01bd
            java.lang.Object r0 = r8.next()
            r9 = r0
            java.lang.String r9 = (java.lang.String) r9
            boolean r0 = b.b.b.d.e.f1524a
            java.lang.String r10 = "PaySafetyConnection"
            if (r0 == 0) goto L_0x003f
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r11 = "hosted connection url: "
            r0.append(r11)
            r0.append(r9)
            java.lang.String r0 = r0.toString()
            android.util.Log.d(r10, r0)
        L_0x003f:
            r11 = -1
            java.net.URL r12 = new java.net.URL     // Catch:{ MalformedURLException -> 0x01a3 }
            r12.<init>(r9)     // Catch:{ MalformedURLException -> 0x01a3 }
            java.net.URLConnection r0 = r12.openConnection()     // Catch:{ Exception -> 0x014f, all -> 0x014c }
            r14 = r0
            java.net.HttpURLConnection r14 = (java.net.HttpURLConnection) r14     // Catch:{ Exception -> 0x014f, all -> 0x014c }
            r0 = 10000(0x2710, float:1.4013E-41)
            r14.setConnectTimeout(r0)     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            android.content.Context r15 = r1.g     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            boolean r15 = b.b.b.d.k.b(r15)     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            if (r15 == 0) goto L_0x005d
            r14.setReadTimeout(r0)     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            goto L_0x0062
        L_0x005d:
            r0 = 30000(0x7530, float:4.2039E-41)
            r14.setReadTimeout(r0)     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
        L_0x0062:
            if (r19 == 0) goto L_0x006d
            java.lang.String r0 = "GET"
            r14.setRequestMethod(r0)     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            r14.setDoOutput(r7)     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            goto L_0x0096
        L_0x006d:
            java.lang.String r0 = "POST"
            r14.setRequestMethod(r0)     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            r14.setDoOutput(r6)     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            r14.setUseCaches(r7)     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            if (r2 == 0) goto L_0x0087
            int r0 = r2.length     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            if (r0 <= 0) goto L_0x0087
            java.lang.String r0 = "Content-Length"
            int r15 = r2.length     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            java.lang.String r15 = java.lang.Integer.toString(r15)     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            r14.setRequestProperty(r0, r15)     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
        L_0x0087:
            java.lang.String r0 = r1.f     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            boolean r0 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            if (r0 != 0) goto L_0x0096
            java.lang.String r0 = "Content-Type"
            java.lang.String r15 = r1.f     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            r14.setRequestProperty(r0, r15)     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
        L_0x0096:
            r1.a((java.net.HttpURLConnection) r14)     // Catch:{ a -> 0x013a }
            r14.connect()     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            if (r19 != 0) goto L_0x00ad
            if (r2 == 0) goto L_0x00ad
            int r0 = r2.length     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            if (r0 <= 0) goto L_0x00ad
            java.io.OutputStream r0 = r14.getOutputStream()     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            r0.write(r2)     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            r0.close()     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
        L_0x00ad:
            int r11 = r14.getResponseCode()     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            b.b.b.d.d$c r0 = r1.a((int) r11)     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            b.b.b.d.d$c r15 = b.b.b.d.d.c.OK     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            if (r0 != r15) goto L_0x011d
            if (r3 == 0) goto L_0x0131
            java.io.BufferedInputStream r15 = new java.io.BufferedInputStream     // Catch:{ Exception -> 0x00e4, all -> 0x00e1 }
            java.io.InputStream r6 = r14.getInputStream()     // Catch:{ Exception -> 0x00e4, all -> 0x00e1 }
            r13 = 8192(0x2000, float:1.14794E-41)
            r15.<init>(r6, r13)     // Catch:{ Exception -> 0x00e4, all -> 0x00e1 }
            r6 = 1024(0x400, float:1.435E-42)
            byte[] r13 = new byte[r6]     // Catch:{ Exception -> 0x00de, all -> 0x00db }
        L_0x00ca:
            int r2 = r15.read(r13, r7, r6)     // Catch:{ Exception -> 0x00de, all -> 0x00db }
            if (r2 <= 0) goto L_0x00d4
            r3.write(r13, r7, r2)     // Catch:{ Exception -> 0x00de, all -> 0x00db }
            goto L_0x00ca
        L_0x00d4:
            r20.flush()     // Catch:{ Exception -> 0x00de, all -> 0x00db }
            r15.close()     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            goto L_0x0131
        L_0x00db:
            r0 = move-exception
            r13 = r15
            goto L_0x0117
        L_0x00de:
            r0 = move-exception
            r13 = r15
            goto L_0x00e6
        L_0x00e1:
            r0 = move-exception
            r13 = 0
            goto L_0x0117
        L_0x00e4:
            r0 = move-exception
            r13 = 0
        L_0x00e6:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0116 }
            r2.<init>()     // Catch:{ all -> 0x0116 }
            r2.append(r5)     // Catch:{ all -> 0x0116 }
            java.lang.String r6 = r12.getHost()     // Catch:{ all -> 0x0116 }
            r2.append(r6)     // Catch:{ all -> 0x0116 }
            java.lang.String r6 = " : read file stream error "
            r2.append(r6)     // Catch:{ all -> 0x0116 }
            r2.append(r0)     // Catch:{ all -> 0x0116 }
            java.lang.String r0 = r2.toString()     // Catch:{ all -> 0x0116 }
            android.util.Log.e(r10, r0)     // Catch:{ all -> 0x0116 }
            r20.a()     // Catch:{ all -> 0x0116 }
            if (r13 == 0) goto L_0x010c
            r13.close()     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
        L_0x010c:
            b.b.c.h.i.a(r4, r11, r7)
            if (r14 == 0) goto L_0x01b8
            r14.disconnect()
            goto L_0x01b8
        L_0x0116:
            r0 = move-exception
        L_0x0117:
            if (r13 == 0) goto L_0x011c
            r13.close()     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
        L_0x011c:
            throw r0     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
        L_0x011d:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            r2.<init>()     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            r2.append(r11)     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            java.lang.String r6 = ""
            r2.append(r6)     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            java.lang.String r2 = r2.toString()     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            r1.a((java.lang.String) r9, (java.lang.String) r2)     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
        L_0x0131:
            b.b.c.h.i.a(r4, r11, r7)
            if (r14 == 0) goto L_0x0139
            r14.disconnect()
        L_0x0139:
            return r0
        L_0x013a:
            r0 = move-exception
            r2 = r0
            b.b.b.d.d$c r0 = r2.f1513a     // Catch:{ Exception -> 0x0149, all -> 0x0147 }
            b.b.c.h.i.a(r4, r11, r7)
            if (r14 == 0) goto L_0x0146
            r14.disconnect()
        L_0x0146:
            return r0
        L_0x0147:
            r0 = move-exception
            goto L_0x019a
        L_0x0149:
            r0 = move-exception
            r13 = r14
            goto L_0x0151
        L_0x014c:
            r0 = move-exception
            r14 = 0
            goto L_0x019a
        L_0x014f:
            r0 = move-exception
            r13 = 0
        L_0x0151:
            boolean r2 = b.b.b.d.e.f1524a     // Catch:{ all -> 0x0198 }
            if (r2 == 0) goto L_0x0174
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0198 }
            r2.<init>()     // Catch:{ all -> 0x0198 }
            r2.append(r5)     // Catch:{ all -> 0x0198 }
            java.lang.String r6 = r12.getHost()     // Catch:{ all -> 0x0198 }
            r2.append(r6)     // Catch:{ all -> 0x0198 }
            java.lang.String r6 = " :"
            r2.append(r6)     // Catch:{ all -> 0x0198 }
            r2.append(r0)     // Catch:{ all -> 0x0198 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0198 }
        L_0x0170:
            android.util.Log.e(r10, r2)     // Catch:{ all -> 0x0198 }
            goto L_0x0188
        L_0x0174:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x0198 }
            r2.<init>()     // Catch:{ all -> 0x0198 }
            r2.append(r5)     // Catch:{ all -> 0x0198 }
            java.lang.String r6 = r12.getHost()     // Catch:{ all -> 0x0198 }
            r2.append(r6)     // Catch:{ all -> 0x0198 }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x0198 }
            goto L_0x0170
        L_0x0188:
            java.lang.String r0 = r0.toString()     // Catch:{ all -> 0x0198 }
            r1.a((java.lang.String) r9, (java.lang.String) r0)     // Catch:{ all -> 0x0198 }
            b.b.c.h.i.a(r4, r11, r7)
            if (r13 == 0) goto L_0x01b8
            r13.disconnect()
            goto L_0x01b8
        L_0x0198:
            r0 = move-exception
            r14 = r13
        L_0x019a:
            b.b.c.h.i.a(r4, r11, r7)
            if (r14 == 0) goto L_0x01a2
            r14.disconnect()
        L_0x01a2:
            throw r0
        L_0x01a3:
            r0 = move-exception
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r6 = " URL error :"
            r2.append(r6)
            r2.append(r0)
            java.lang.String r0 = r2.toString()
            android.util.Log.e(r10, r0)
        L_0x01b8:
            r2 = r18
            r6 = 1
            goto L_0x0018
        L_0x01bd:
            b.b.b.d.d$c r0 = b.b.b.d.d.c.NETWORK_ERROR
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.b.d.d.a(java.lang.String, byte[], boolean, b.b.b.d.d$e, b.b.c.h.j):b.b.b.d.d$c");
    }

    /* access modifiers changed from: private */
    public static StringBuilder b(StringBuilder sb, String str, String str2, char c2) {
        if (sb.length() > 0) {
            sb.append(c2);
        }
        sb.append(str);
        sb.append("=");
        sb.append(str2);
        return sb;
    }

    /* access modifiers changed from: private */
    public static StringBuilder b(StringBuilder sb, String str, String str2, String str3) {
        if (sb.length() > 0) {
            sb.append("&");
        }
        sb.append(str);
        sb.append("=");
        try {
            sb.append(URLEncoder.encode(str2, str3));
        } catch (UnsupportedEncodingException unused) {
        }
        return sb;
    }

    private void b(URL url) {
        this.f1512d = false;
        if (a(url)) {
            this.f1510b = url;
        }
    }

    /* access modifiers changed from: protected */
    public c a(e eVar) {
        String str;
        StringBuilder sb;
        if (this.f1510b == null) {
            return c.URL_ERROR;
        }
        if (!k.a(this.g)) {
            return c.NETWORK_ERROR;
        }
        if (this.f1511c == null) {
            this.f1511c = new C0025d(this);
        }
        try {
            C0025d dVar = this.f1511c;
            a(dVar);
            String url = this.f1510b.toString();
            if (this.f1512d && !dVar.a()) {
                String query = this.f1510b.getQuery();
                String url2 = this.f1510b.toString();
                if (TextUtils.isEmpty(query)) {
                    sb = new StringBuilder();
                    sb.append(url2);
                    str = "?";
                } else {
                    sb = new StringBuilder();
                    sb.append(url2);
                    str = "&";
                }
                sb.append(str);
                sb.append(dVar.b());
                url = sb.toString();
            }
            try {
                a(url, dVar);
                if (e.f1524a) {
                    Log.d("PaySafetyConnection", "connection url: " + url);
                }
                if (!this.f1512d) {
                    byte[] bArr = this.e;
                    if (bArr != null && bArr.length > 0) {
                        this.f = "application/octet-stream";
                    } else if (!dVar.a()) {
                        this.e = dVar.b().getBytes();
                        if (e.f1524a) {
                            Log.d("PaySafetyConnection", "[post]" + dVar);
                        }
                    }
                    byte[] bArr2 = this.e;
                    if (bArr2 == null || bArr2.length == 0) {
                        return c.CLIENT_ERROR;
                    }
                }
                long currentTimeMillis = System.currentTimeMillis();
                c a2 = a(url, this.e, this.f1512d, eVar, new j("antivirus_connection_request"));
                if (e.f1524a) {
                    long currentTimeMillis2 = System.currentTimeMillis();
                    Log.d("PaySafetyConnection", "Time(ms) spent in request: " + (currentTimeMillis2 - currentTimeMillis) + ", " + url);
                }
                return a2;
            } catch (a e2) {
                return e2.f1513a;
            }
        } catch (a e3) {
            return e3.f1513a;
        }
    }

    /* access modifiers changed from: protected */
    public C0025d a(C0025d dVar) {
        return dVar;
    }

    /* access modifiers changed from: protected */
    public String a(String str, C0025d dVar) {
        return str;
    }

    /* access modifiers changed from: protected */
    public HttpURLConnection a(HttpURLConnection httpURLConnection) {
        return httpURLConnection;
    }

    public JSONArray a() {
        return this.f1509a;
    }

    /* access modifiers changed from: protected */
    public void a(String str, String str2) {
    }

    /* access modifiers changed from: protected */
    public boolean a(URL url) {
        if (url == null) {
            return false;
        }
        return TextUtils.equals(url.getProtocol(), "http") || TextUtils.equals(url.getProtocol(), "https");
    }

    public c b() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        c a2 = a((e) new b(byteArrayOutputStream));
        try {
            if (a2 == c.OK) {
                this.f1509a = new JSONArray(byteArrayOutputStream.toString());
            } else {
                Log.e("PaySafetyConnection", "Connection failed : " + a2);
            }
            try {
                byteArrayOutputStream.close();
            } catch (IOException unused) {
            }
            return a2;
        } catch (JSONException e2) {
            Log.e("PaySafetyConnection", "JSON error: " + e2);
            c cVar = c.RESULT_ERROR;
            try {
                byteArrayOutputStream.close();
            } catch (IOException unused2) {
            }
            return cVar;
        } catch (Throwable th) {
            try {
                byteArrayOutputStream.close();
            } catch (IOException unused3) {
            }
            throw th;
        }
    }
}
