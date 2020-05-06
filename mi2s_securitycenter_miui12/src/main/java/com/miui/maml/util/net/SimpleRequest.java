package com.miui.maml.util.net;

import android.content.ContentValues;
import com.xiaomi.stat.a.l;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.json.JSONException;
import org.json.JSONObject;

public final class SimpleRequest {
    private static final String CER_12306 = "-----BEGIN CERTIFICATE-----\nMIICmjCCAgOgAwIBAgIIbyZr5/jKH6QwDQYJKoZIhvcNAQEFBQAwRzELMAkGA1UEBhMCQ04xKTAnBgNVBAoTIFNpbm9yYWlsIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MQ0wCwYDVQQDEwRTUkNBMB4XDTA5MDUyNTA2NTYwMFoXDTI5MDUyMDA2NTYwMFowRzELMAkGA1UEBhMCQ04xKTAnBgNVBAoTIFNpbm9yYWlsIENlcnRpZmljYXRpb24gQXV0aG9yaXR5MQ0wCwYDVQQDEwRTUkNBMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDMpbNeb34p0GvLkZ6t72/OOba4mX2K/eZRWFfnuk8e5jKDH+9BgCb29bSotqPqTbxXWPxIOz8EjyUO3bfR5pQ8ovNTOlks2rS5BdMhoi4sUjCKi5ELiqtyww/XgY5iFqv6D4Pw9QvOUcdRVSbPWo1DwMmH75It6pk/rARIFHEjWwIDAQABo4GOMIGLMB8GA1UdIwQYMBaAFHletne34lKDQ+3HUYhMY4UsAENYMAwGA1UdEwQFMAMBAf8wLgYDVR0fBCcwJTAjoCGgH4YdaHR0cDovLzE5Mi4xNjguOS4xNDkvY3JsMS5jcmwwCwYDVR0PBAQDAgH+MB0GA1UdDgQWBBR5XrZ3t+JSg0Ptx1GITGOFLABDWDANBgkqhkiG9w0BAQUFAAOBgQDGrAm2U/of1LbOnG2bnnQtgcVaBXiVJF8LKPaV23XQ96HU8xfgSZMJS6U00WHAI7zp0q208RSUft9wDq9ee///VOhzR6Tebg9QfyPSohkBrhXQenvQog555S+C3eJAAVeNCTeMS3N/M5hzBRJAoffn3qoYdAO1Q8bTguOi+2849A==\n-----END CERTIFICATE-----";
    private static final boolean DEBUG = false;
    private static final String HOST_12306 = "kyfw.12306.cn";
    public static final String ISO_8859_1 = "ISO-8859-1";
    public static final String LOCATION = "Location";
    private static final String NAME_VALUE_SEPARATOR = "=";
    private static final String PARAMETER_SEPARATOR = "&";
    private static final String PARAM_IGNORE_12306_CA = "ignore12306ca";
    private static final int TIMEOUT = 30000;
    public static final String UTF8 = "utf-8";
    private static final Logger log = Logger.getLogger(SimpleRequest.class.getSimpleName());
    private static String sUserAgent;

    public static class HeaderContent {
        private final Map<String, String> headers = new HashMap();

        public String getHeader(String str) {
            return this.headers.get(str);
        }

        public Map<String, String> getHeaders() {
            return this.headers;
        }

        public void putHeader(String str, String str2) {
            this.headers.put(str, str2);
        }

        public void putHeaders(Map<String, String> map) {
            this.headers.putAll(map);
        }

        public String toString() {
            return "HeaderContent{headers=" + this.headers + '}';
        }
    }

    public static class MapContent extends HeaderContent {
        private Map<String, Object> bodies;

        public MapContent(Map<String, Object> map) {
            this.bodies = map;
        }

        public Object getFromBody(String str) {
            return this.bodies.get(str);
        }

        public String toString() {
            return "MapContent{bodies=" + this.bodies + '}';
        }
    }

    public static class StreamContent extends HeaderContent {
        private InputStream stream;

        public StreamContent(InputStream inputStream) {
            this.stream = inputStream;
        }

        public void closeStream() {
            IOUtils.closeQuietly(this.stream);
        }

        public InputStream getStream() {
            return this.stream;
        }
    }

    public static class StringContent extends HeaderContent {
        private String body;

        public StringContent(String str) {
            this.body = str;
        }

        public String getBody() {
            return this.body;
        }

        public String toString() {
            return "StringContent{body='" + this.body + '\'' + '}';
        }
    }

    protected static String appendUrl(String str, ContentValues contentValues) {
        String format;
        if (str != null) {
            StringBuilder sb = new StringBuilder(str);
            if (!(contentValues == null || (format = format(contentValues, UTF8)) == null || format.length() <= 0)) {
                if (str.contains("?")) {
                    sb.append(PARAMETER_SEPARATOR);
                } else {
                    sb.append("?");
                }
                sb.append(format);
            }
            return sb.toString();
        }
        throw new NullPointerException("origin is not allowed null");
    }

    protected static MapContent convertStringToMap(StringContent stringContent) {
        JSONObject jSONObject;
        if (stringContent == null) {
            return null;
        }
        try {
            jSONObject = new JSONObject(stringContent.getBody());
        } catch (JSONException e) {
            e.printStackTrace();
            jSONObject = null;
        }
        if (jSONObject == null) {
            return null;
        }
        MapContent mapContent = new MapContent(ObjectUtils.jsonToMap(jSONObject));
        mapContent.putHeaders(stringContent.getHeaders());
        return mapContent;
    }

    private static String encode(String str, String str2) {
        if (str2 == null) {
            str2 = ISO_8859_1;
        }
        try {
            return URLEncoder.encode(str, str2);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static String format(ContentValues contentValues, String str) {
        StringBuilder sb = new StringBuilder();
        for (String next : contentValues.keySet()) {
            String encode = encode(next, str);
            String asString = contentValues.getAsString(next);
            String encode2 = asString != null ? encode(asString, str) : "";
            if (sb.length() > 0) {
                sb.append(PARAMETER_SEPARATOR);
            }
            sb.append(encode);
            sb.append(NAME_VALUE_SEPARATOR);
            sb.append(encode2);
        }
        return sb.toString();
    }

    public static MapContent getAsMap(String str, Map<String, String> map, Map<String, String> map2, boolean z) {
        return convertStringToMap(getAsString(str, map, map2, z));
    }

    public static StreamContent getAsStream(String str, Map<String, String> map, Map<String, String> map2) {
        boolean needIgnore12306CA = needIgnore12306CA(map);
        String appendUrl = appendUrl(str, ObjectUtils.mapToPairs(map));
        HttpURLConnection makeConn = makeConn(appendUrl, map2, needIgnore12306CA);
        if (makeConn != null) {
            try {
                makeConn.setDoInput(true);
                makeConn.setRequestMethod("GET");
                makeConn.setInstanceFollowRedirects(true);
                makeConn.connect();
                int responseCode = makeConn.getResponseCode();
                if (responseCode == 200) {
                    Map headerFields = makeConn.getHeaderFields();
                    CookieManager cookieManager = new CookieManager();
                    URI create = URI.create(appendUrl);
                    cookieManager.put(create, headerFields);
                    Map<String, String> parseCookies = parseCookies(cookieManager.getCookieStore().get(create));
                    parseCookies.putAll(ObjectUtils.listToMap(headerFields));
                    StreamContent streamContent = new StreamContent(makeConn.getInputStream());
                    streamContent.putHeaders(parseCookies);
                    return streamContent;
                } else if (responseCode == 403) {
                    throw new AccessDeniedException("access denied, encrypt error or user is forbidden to access the resource");
                } else if (responseCode == 401 || responseCode == 400) {
                    throw new AuthenticationFailureException("authentication failure for get, code: " + responseCode);
                } else {
                    Logger logger = log;
                    logger.info("http status error when GET: " + responseCode);
                    if (responseCode == 301) {
                        Logger logger2 = log;
                        logger2.info("unexpected redirect from " + makeConn.getURL().getHost() + " to " + makeConn.getHeaderField(LOCATION));
                    }
                    throw new IOException("unexpected http res code: " + responseCode);
                }
            } catch (ProtocolException unused) {
                throw new IOException("protocol error");
            }
        } else {
            log.severe("failed to create URLConnection");
            throw new IOException("failed to create connection");
        }
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:40:0x0113 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.miui.maml.util.net.SimpleRequest.StringContent getAsString(java.lang.String r2, java.util.Map<java.lang.String, java.lang.String> r3, java.util.Map<java.lang.String, java.lang.String> r4, boolean r5) {
        /*
            boolean r0 = needIgnore12306CA(r3)
            android.content.ContentValues r3 = com.miui.maml.util.net.ObjectUtils.mapToPairs(r3)
            java.lang.String r2 = appendUrl(r2, r3)
            java.net.HttpURLConnection r3 = makeConn(r2, r4, r0)
            if (r3 == 0) goto L_0x011f
            r4 = 1
            r3.setDoInput(r4)     // Catch:{ ProtocolException -> 0x0113 }
            java.lang.String r4 = "GET"
            r3.setRequestMethod(r4)     // Catch:{ ProtocolException -> 0x0113 }
            r3.connect()     // Catch:{ ProtocolException -> 0x0113 }
            int r4 = r3.getResponseCode()     // Catch:{ ProtocolException -> 0x0113 }
            r0 = 200(0xc8, float:2.8E-43)
            if (r4 == r0) goto L_0x00b4
            r0 = 302(0x12e, float:4.23E-43)
            if (r4 != r0) goto L_0x002c
            goto L_0x00b4
        L_0x002c:
            r2 = 403(0x193, float:5.65E-43)
            if (r4 == r2) goto L_0x00ac
            r2 = 401(0x191, float:5.62E-43)
            if (r4 == r2) goto L_0x0095
            r2 = 400(0x190, float:5.6E-43)
            if (r4 == r2) goto L_0x0095
            java.util.logging.Logger r2 = log     // Catch:{ ProtocolException -> 0x0113 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ ProtocolException -> 0x0113 }
            r5.<init>()     // Catch:{ ProtocolException -> 0x0113 }
            java.lang.String r0 = "http status error when GET: "
            r5.append(r0)     // Catch:{ ProtocolException -> 0x0113 }
            r5.append(r4)     // Catch:{ ProtocolException -> 0x0113 }
            java.lang.String r5 = r5.toString()     // Catch:{ ProtocolException -> 0x0113 }
            r2.info(r5)     // Catch:{ ProtocolException -> 0x0113 }
            r2 = 301(0x12d, float:4.22E-43)
            if (r4 != r2) goto L_0x007e
            java.util.logging.Logger r2 = log     // Catch:{ ProtocolException -> 0x0113 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ ProtocolException -> 0x0113 }
            r5.<init>()     // Catch:{ ProtocolException -> 0x0113 }
            java.lang.String r0 = "unexpected redirect from "
            r5.append(r0)     // Catch:{ ProtocolException -> 0x0113 }
            java.net.URL r0 = r3.getURL()     // Catch:{ ProtocolException -> 0x0113 }
            java.lang.String r0 = r0.getHost()     // Catch:{ ProtocolException -> 0x0113 }
            r5.append(r0)     // Catch:{ ProtocolException -> 0x0113 }
            java.lang.String r0 = " to "
            r5.append(r0)     // Catch:{ ProtocolException -> 0x0113 }
            java.lang.String r0 = "Location"
            java.lang.String r0 = r3.getHeaderField(r0)     // Catch:{ ProtocolException -> 0x0113 }
            r5.append(r0)     // Catch:{ ProtocolException -> 0x0113 }
            java.lang.String r5 = r5.toString()     // Catch:{ ProtocolException -> 0x0113 }
            r2.info(r5)     // Catch:{ ProtocolException -> 0x0113 }
        L_0x007e:
            java.io.IOException r2 = new java.io.IOException     // Catch:{ ProtocolException -> 0x0113 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ ProtocolException -> 0x0113 }
            r5.<init>()     // Catch:{ ProtocolException -> 0x0113 }
            java.lang.String r0 = "unexpected http res code: "
            r5.append(r0)     // Catch:{ ProtocolException -> 0x0113 }
            r5.append(r4)     // Catch:{ ProtocolException -> 0x0113 }
            java.lang.String r4 = r5.toString()     // Catch:{ ProtocolException -> 0x0113 }
            r2.<init>(r4)     // Catch:{ ProtocolException -> 0x0113 }
            throw r2     // Catch:{ ProtocolException -> 0x0113 }
        L_0x0095:
            com.miui.maml.util.net.AuthenticationFailureException r2 = new com.miui.maml.util.net.AuthenticationFailureException     // Catch:{ ProtocolException -> 0x0113 }
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ ProtocolException -> 0x0113 }
            r5.<init>()     // Catch:{ ProtocolException -> 0x0113 }
            java.lang.String r0 = "authentication failure for get, code: "
            r5.append(r0)     // Catch:{ ProtocolException -> 0x0113 }
            r5.append(r4)     // Catch:{ ProtocolException -> 0x0113 }
            java.lang.String r4 = r5.toString()     // Catch:{ ProtocolException -> 0x0113 }
            r2.<init>(r4)     // Catch:{ ProtocolException -> 0x0113 }
            throw r2     // Catch:{ ProtocolException -> 0x0113 }
        L_0x00ac:
            com.miui.maml.util.net.AccessDeniedException r2 = new com.miui.maml.util.net.AccessDeniedException     // Catch:{ ProtocolException -> 0x0113 }
            java.lang.String r4 = "access denied, encrypt error or user is forbidden to access the resource"
            r2.<init>(r4)     // Catch:{ ProtocolException -> 0x0113 }
            throw r2     // Catch:{ ProtocolException -> 0x0113 }
        L_0x00b4:
            java.util.Map r4 = r3.getHeaderFields()     // Catch:{ ProtocolException -> 0x0113 }
            java.net.CookieManager r0 = new java.net.CookieManager     // Catch:{ ProtocolException -> 0x0113 }
            r0.<init>()     // Catch:{ ProtocolException -> 0x0113 }
            java.net.URI r2 = java.net.URI.create(r2)     // Catch:{ ProtocolException -> 0x0113 }
            r0.put(r2, r4)     // Catch:{ ProtocolException -> 0x0113 }
            java.net.CookieStore r0 = r0.getCookieStore()     // Catch:{ ProtocolException -> 0x0113 }
            java.util.List r2 = r0.get(r2)     // Catch:{ ProtocolException -> 0x0113 }
            java.util.Map r2 = parseCookies(r2)     // Catch:{ ProtocolException -> 0x0113 }
            java.util.Map r4 = com.miui.maml.util.net.ObjectUtils.listToMap(r4)     // Catch:{ ProtocolException -> 0x0113 }
            r2.putAll(r4)     // Catch:{ ProtocolException -> 0x0113 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ ProtocolException -> 0x0113 }
            r4.<init>()     // Catch:{ ProtocolException -> 0x0113 }
            if (r5 == 0) goto L_0x0101
            java.io.BufferedReader r5 = new java.io.BufferedReader     // Catch:{ ProtocolException -> 0x0113 }
            java.io.InputStreamReader r0 = new java.io.InputStreamReader     // Catch:{ ProtocolException -> 0x0113 }
            java.io.InputStream r1 = r3.getInputStream()     // Catch:{ ProtocolException -> 0x0113 }
            r0.<init>(r1)     // Catch:{ ProtocolException -> 0x0113 }
            r1 = 1024(0x400, float:1.435E-42)
            r5.<init>(r0, r1)     // Catch:{ ProtocolException -> 0x0113 }
        L_0x00ee:
            java.lang.String r0 = r5.readLine()     // Catch:{ all -> 0x00fc }
            if (r0 == 0) goto L_0x00f8
            r4.append(r0)     // Catch:{ all -> 0x00fc }
            goto L_0x00ee
        L_0x00f8:
            com.miui.maml.util.net.IOUtils.closeQuietly((java.io.Reader) r5)     // Catch:{ ProtocolException -> 0x0113 }
            goto L_0x0101
        L_0x00fc:
            r2 = move-exception
            com.miui.maml.util.net.IOUtils.closeQuietly((java.io.Reader) r5)     // Catch:{ ProtocolException -> 0x0113 }
            throw r2     // Catch:{ ProtocolException -> 0x0113 }
        L_0x0101:
            com.miui.maml.util.net.SimpleRequest$StringContent r5 = new com.miui.maml.util.net.SimpleRequest$StringContent     // Catch:{ ProtocolException -> 0x0113 }
            java.lang.String r4 = r4.toString()     // Catch:{ ProtocolException -> 0x0113 }
            r5.<init>(r4)     // Catch:{ ProtocolException -> 0x0113 }
            r5.putHeaders(r2)     // Catch:{ ProtocolException -> 0x0113 }
            r3.disconnect()
            return r5
        L_0x0111:
            r2 = move-exception
            goto L_0x011b
        L_0x0113:
            java.io.IOException r2 = new java.io.IOException     // Catch:{ all -> 0x0111 }
            java.lang.String r4 = "protocol error"
            r2.<init>(r4)     // Catch:{ all -> 0x0111 }
            throw r2     // Catch:{ all -> 0x0111 }
        L_0x011b:
            r3.disconnect()
            throw r2
        L_0x011f:
            java.util.logging.Logger r2 = log
            java.lang.String r3 = "failed to create URLConnection"
            r2.severe(r3)
            java.io.IOException r2 = new java.io.IOException
            java.lang.String r3 = "failed to create connection"
            r2.<init>(r3)
            throw r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.util.net.SimpleRequest.getAsString(java.lang.String, java.util.Map, java.util.Map, boolean):com.miui.maml.util.net.SimpleRequest$StringContent");
    }

    protected static String joinMap(Map<String, String> map, String str) {
        if (map == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Map.Entry next : map.entrySet()) {
            if (i > 0) {
                sb.append(str);
            }
            sb.append((String) next.getKey());
            sb.append(NAME_VALUE_SEPARATOR);
            sb.append((String) next.getValue());
            i++;
        }
        return sb.toString();
    }

    protected static HttpURLConnection makeConn(String str, Map<String, String> map) {
        return makeConn(str, map, false);
    }

    protected static HttpURLConnection makeConn(String str, Map<String, String> map, boolean z) {
        URL url;
        BufferedInputStream bufferedInputStream;
        try {
            url = new URL(str);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            url = null;
        }
        if (url == null) {
            log.severe("failed to init url");
            return null;
        }
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setInstanceFollowRedirects(false);
            httpURLConnection.setConnectTimeout(TIMEOUT);
            httpURLConnection.setReadTimeout(TIMEOUT);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            if (map != null) {
                httpURLConnection.setRequestProperty("Cookie", joinMap(map, "; "));
            }
            if (HOST_12306.equals(url.getHost()) && (httpURLConnection instanceof HttpsURLConnection)) {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) httpURLConnection;
                SSLContext instance = SSLContext.getInstance("TLS");
                if (z) {
                    instance.init((KeyManager[]) null, new TrustManager[]{new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] x509CertificateArr, String str) {
                        }

                        public void checkServerTrusted(X509Certificate[] x509CertificateArr, String str) {
                        }

                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }
                    }}, (SecureRandom) null);
                } else {
                    CertificateFactory instance2 = CertificateFactory.getInstance("X.509");
                    try {
                        bufferedInputStream = new BufferedInputStream(new ByteArrayInputStream(CER_12306.getBytes()));
                        try {
                            Certificate generateCertificate = instance2.generateCertificate(bufferedInputStream);
                            KeyStore instance3 = KeyStore.getInstance(KeyStore.getDefaultType());
                            instance3.load((InputStream) null, (char[]) null);
                            instance3.setCertificateEntry(l.a.x, generateCertificate);
                            TrustManagerFactory instance4 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                            instance4.init(instance3);
                            instance.init((KeyManager[]) null, instance4.getTrustManagers(), (SecureRandom) null);
                            IOUtils.closeQuietly((InputStream) bufferedInputStream);
                        } catch (Throwable th) {
                            th = th;
                            IOUtils.closeQuietly((InputStream) bufferedInputStream);
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        bufferedInputStream = null;
                        IOUtils.closeQuietly((InputStream) bufferedInputStream);
                        throw th;
                    }
                }
                httpsURLConnection.setSSLSocketFactory(instance.getSocketFactory());
            }
            return httpURLConnection;
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    private static boolean needIgnore12306CA(Map<String, String> map) {
        if (map == null || !Boolean.TRUE.toString().equalsIgnoreCase(map.get(PARAM_IGNORE_12306_CA))) {
            return false;
        }
        map.remove(PARAM_IGNORE_12306_CA);
        return true;
    }

    protected static Map<String, String> parseCookies(List<HttpCookie> list) {
        HashMap hashMap = new HashMap();
        for (HttpCookie next : list) {
            if (!next.hasExpired()) {
                String name = next.getName();
                String value = next.getValue();
                if (name != null) {
                    hashMap.put(name, value);
                }
            }
        }
        return hashMap;
    }

    public static MapContent postAsMap(String str, Map<String, String> map, Map<String, String> map2, boolean z) {
        return convertStringToMap(postAsString(str, map, map2, z));
    }

    /* JADX WARNING: Missing exception handler attribute for start block: B:50:0x0133 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static com.miui.maml.util.net.SimpleRequest.StringContent postAsString(java.lang.String r3, java.util.Map<java.lang.String, java.lang.String> r4, java.util.Map<java.lang.String, java.lang.String> r5, boolean r6) {
        /*
            java.lang.String r0 = "utf-8"
            boolean r1 = needIgnore12306CA(r4)
            java.net.HttpURLConnection r5 = makeConn(r3, r5, r1)
            if (r5 == 0) goto L_0x013f
            r1 = 1
            r5.setDoInput(r1)     // Catch:{ ProtocolException -> 0x0133 }
            r5.setDoOutput(r1)     // Catch:{ ProtocolException -> 0x0133 }
            java.lang.String r1 = "POST"
            r5.setRequestMethod(r1)     // Catch:{ ProtocolException -> 0x0133 }
            r5.connect()     // Catch:{ ProtocolException -> 0x0133 }
            android.content.ContentValues r4 = com.miui.maml.util.net.ObjectUtils.mapToPairs(r4)     // Catch:{ ProtocolException -> 0x0133 }
            if (r4 == 0) goto L_0x003e
            java.lang.String r4 = format(r4, r0)     // Catch:{ ProtocolException -> 0x0133 }
            java.io.OutputStream r1 = r5.getOutputStream()     // Catch:{ ProtocolException -> 0x0133 }
            java.io.BufferedOutputStream r2 = new java.io.BufferedOutputStream     // Catch:{ ProtocolException -> 0x0133 }
            r2.<init>(r1)     // Catch:{ ProtocolException -> 0x0133 }
            byte[] r4 = r4.getBytes(r0)     // Catch:{ all -> 0x0039 }
            r2.write(r4)     // Catch:{ all -> 0x0039 }
            com.miui.maml.util.net.IOUtils.closeQuietly((java.io.OutputStream) r2)     // Catch:{ ProtocolException -> 0x0133 }
            goto L_0x003e
        L_0x0039:
            r3 = move-exception
            com.miui.maml.util.net.IOUtils.closeQuietly((java.io.OutputStream) r2)     // Catch:{ ProtocolException -> 0x0133 }
            throw r3     // Catch:{ ProtocolException -> 0x0133 }
        L_0x003e:
            int r4 = r5.getResponseCode()     // Catch:{ ProtocolException -> 0x0133 }
            r0 = 200(0xc8, float:2.8E-43)
            if (r4 == r0) goto L_0x00d4
            r0 = 302(0x12e, float:4.23E-43)
            if (r4 != r0) goto L_0x004c
            goto L_0x00d4
        L_0x004c:
            r3 = 403(0x193, float:5.65E-43)
            if (r4 == r3) goto L_0x00cc
            r3 = 401(0x191, float:5.62E-43)
            if (r4 == r3) goto L_0x00b5
            r3 = 400(0x190, float:5.6E-43)
            if (r4 == r3) goto L_0x00b5
            java.util.logging.Logger r3 = log     // Catch:{ ProtocolException -> 0x0133 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ ProtocolException -> 0x0133 }
            r6.<init>()     // Catch:{ ProtocolException -> 0x0133 }
            java.lang.String r0 = "http status error when POST: "
            r6.append(r0)     // Catch:{ ProtocolException -> 0x0133 }
            r6.append(r4)     // Catch:{ ProtocolException -> 0x0133 }
            java.lang.String r6 = r6.toString()     // Catch:{ ProtocolException -> 0x0133 }
            r3.info(r6)     // Catch:{ ProtocolException -> 0x0133 }
            r3 = 301(0x12d, float:4.22E-43)
            if (r4 != r3) goto L_0x009e
            java.util.logging.Logger r3 = log     // Catch:{ ProtocolException -> 0x0133 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ ProtocolException -> 0x0133 }
            r6.<init>()     // Catch:{ ProtocolException -> 0x0133 }
            java.lang.String r0 = "unexpected redirect from "
            r6.append(r0)     // Catch:{ ProtocolException -> 0x0133 }
            java.net.URL r0 = r5.getURL()     // Catch:{ ProtocolException -> 0x0133 }
            java.lang.String r0 = r0.getHost()     // Catch:{ ProtocolException -> 0x0133 }
            r6.append(r0)     // Catch:{ ProtocolException -> 0x0133 }
            java.lang.String r0 = " to "
            r6.append(r0)     // Catch:{ ProtocolException -> 0x0133 }
            java.lang.String r0 = "Location"
            java.lang.String r0 = r5.getHeaderField(r0)     // Catch:{ ProtocolException -> 0x0133 }
            r6.append(r0)     // Catch:{ ProtocolException -> 0x0133 }
            java.lang.String r6 = r6.toString()     // Catch:{ ProtocolException -> 0x0133 }
            r3.info(r6)     // Catch:{ ProtocolException -> 0x0133 }
        L_0x009e:
            java.io.IOException r3 = new java.io.IOException     // Catch:{ ProtocolException -> 0x0133 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ ProtocolException -> 0x0133 }
            r6.<init>()     // Catch:{ ProtocolException -> 0x0133 }
            java.lang.String r0 = "unexpected http res code: "
            r6.append(r0)     // Catch:{ ProtocolException -> 0x0133 }
            r6.append(r4)     // Catch:{ ProtocolException -> 0x0133 }
            java.lang.String r4 = r6.toString()     // Catch:{ ProtocolException -> 0x0133 }
            r3.<init>(r4)     // Catch:{ ProtocolException -> 0x0133 }
            throw r3     // Catch:{ ProtocolException -> 0x0133 }
        L_0x00b5:
            com.miui.maml.util.net.AuthenticationFailureException r3 = new com.miui.maml.util.net.AuthenticationFailureException     // Catch:{ ProtocolException -> 0x0133 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ ProtocolException -> 0x0133 }
            r6.<init>()     // Catch:{ ProtocolException -> 0x0133 }
            java.lang.String r0 = "authentication failure for post, code: "
            r6.append(r0)     // Catch:{ ProtocolException -> 0x0133 }
            r6.append(r4)     // Catch:{ ProtocolException -> 0x0133 }
            java.lang.String r4 = r6.toString()     // Catch:{ ProtocolException -> 0x0133 }
            r3.<init>(r4)     // Catch:{ ProtocolException -> 0x0133 }
            throw r3     // Catch:{ ProtocolException -> 0x0133 }
        L_0x00cc:
            com.miui.maml.util.net.AccessDeniedException r3 = new com.miui.maml.util.net.AccessDeniedException     // Catch:{ ProtocolException -> 0x0133 }
            java.lang.String r4 = "access denied, encrypt error or user is forbidden to access the resource"
            r3.<init>(r4)     // Catch:{ ProtocolException -> 0x0133 }
            throw r3     // Catch:{ ProtocolException -> 0x0133 }
        L_0x00d4:
            java.util.Map r4 = r5.getHeaderFields()     // Catch:{ ProtocolException -> 0x0133 }
            java.net.CookieManager r0 = new java.net.CookieManager     // Catch:{ ProtocolException -> 0x0133 }
            r0.<init>()     // Catch:{ ProtocolException -> 0x0133 }
            java.net.URI r3 = java.net.URI.create(r3)     // Catch:{ ProtocolException -> 0x0133 }
            r0.put(r3, r4)     // Catch:{ ProtocolException -> 0x0133 }
            java.net.CookieStore r0 = r0.getCookieStore()     // Catch:{ ProtocolException -> 0x0133 }
            java.util.List r3 = r0.get(r3)     // Catch:{ ProtocolException -> 0x0133 }
            java.util.Map r3 = parseCookies(r3)     // Catch:{ ProtocolException -> 0x0133 }
            java.util.Map r4 = com.miui.maml.util.net.ObjectUtils.listToMap(r4)     // Catch:{ ProtocolException -> 0x0133 }
            r3.putAll(r4)     // Catch:{ ProtocolException -> 0x0133 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ ProtocolException -> 0x0133 }
            r4.<init>()     // Catch:{ ProtocolException -> 0x0133 }
            if (r6 == 0) goto L_0x0121
            java.io.BufferedReader r6 = new java.io.BufferedReader     // Catch:{ ProtocolException -> 0x0133 }
            java.io.InputStreamReader r0 = new java.io.InputStreamReader     // Catch:{ ProtocolException -> 0x0133 }
            java.io.InputStream r1 = r5.getInputStream()     // Catch:{ ProtocolException -> 0x0133 }
            r0.<init>(r1)     // Catch:{ ProtocolException -> 0x0133 }
            r1 = 1024(0x400, float:1.435E-42)
            r6.<init>(r0, r1)     // Catch:{ ProtocolException -> 0x0133 }
        L_0x010e:
            java.lang.String r0 = r6.readLine()     // Catch:{ all -> 0x011c }
            if (r0 == 0) goto L_0x0118
            r4.append(r0)     // Catch:{ all -> 0x011c }
            goto L_0x010e
        L_0x0118:
            com.miui.maml.util.net.IOUtils.closeQuietly((java.io.Reader) r6)     // Catch:{ ProtocolException -> 0x0133 }
            goto L_0x0121
        L_0x011c:
            r3 = move-exception
            com.miui.maml.util.net.IOUtils.closeQuietly((java.io.Reader) r6)     // Catch:{ ProtocolException -> 0x0133 }
            throw r3     // Catch:{ ProtocolException -> 0x0133 }
        L_0x0121:
            com.miui.maml.util.net.SimpleRequest$StringContent r6 = new com.miui.maml.util.net.SimpleRequest$StringContent     // Catch:{ ProtocolException -> 0x0133 }
            java.lang.String r4 = r4.toString()     // Catch:{ ProtocolException -> 0x0133 }
            r6.<init>(r4)     // Catch:{ ProtocolException -> 0x0133 }
            r6.putHeaders(r3)     // Catch:{ ProtocolException -> 0x0133 }
            r5.disconnect()
            return r6
        L_0x0131:
            r3 = move-exception
            goto L_0x013b
        L_0x0133:
            java.io.IOException r3 = new java.io.IOException     // Catch:{ all -> 0x0131 }
            java.lang.String r4 = "protocol error"
            r3.<init>(r4)     // Catch:{ all -> 0x0131 }
            throw r3     // Catch:{ all -> 0x0131 }
        L_0x013b:
            r5.disconnect()
            throw r3
        L_0x013f:
            java.util.logging.Logger r3 = log
            java.lang.String r4 = "failed to create URLConnection"
            r3.severe(r4)
            java.io.IOException r3 = new java.io.IOException
            java.lang.String r4 = "failed to create connection"
            r3.<init>(r4)
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.maml.util.net.SimpleRequest.postAsString(java.lang.String, java.util.Map, java.util.Map, boolean):com.miui.maml.util.net.SimpleRequest$StringContent");
    }
}
