package com.miui.securityscan.i;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Base64;
import android.util.Log;
import b.b.c.h.i;
import b.b.c.h.j;
import b.b.c.j.y;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.miui.activityutil.h;
import com.miui.activityutil.o;
import com.miui.luckymoney.config.Constants;
import com.miui.luckymoney.model.message.Impl.QQMessage;
import com.miui.maml.folme.AnimatedProperty;
import com.miui.permcenter.permissions.C0466c;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.g;
import com.xiaomi.stat.d;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import miui.cloud.CloudPushConstants;
import miui.os.Build;
import miui.security.DigestUtils;
import miui.text.ExtraTextUtils;

public class k {

    /* renamed from: a  reason: collision with root package name */
    private static final boolean f7727a = com.miui.securityscan.c.a.f7625a;

    /* renamed from: b  reason: collision with root package name */
    private static final String f7728b = y.a("ro.product.device", h.f2289a);

    /* renamed from: c  reason: collision with root package name */
    private static final String f7729c = y.a("ro.carrier.name", h.f2289a);

    /* renamed from: d  reason: collision with root package name */
    private static final String f7730d = Build.getRegion();
    private static final String e = Build.VERSION.INCREMENTAL;
    private static final String f = y.a("ro.miui.ui.version.name", h.f2289a);
    private static final String g = Build.VERSION.RELEASE;
    private static final String h = android.os.Build.MODEL;
    private static final HashMap<String, String> i = new HashMap<>();

    public enum a {
        GET,
        POST,
        PUT,
        DELETE
    }

    private static class b implements Comparable<b> {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public String f7735a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public String f7736b;

        public b(String str, String str2) {
            this.f7735a = str;
            this.f7736b = str2;
        }

        /* renamed from: c */
        public int compareTo(b bVar) {
            return this.f7735a.compareTo(bVar.f7735a);
        }
    }

    static {
        i.put("d", f7728b);
        i.put(C0466c.f6254a, f7729c);
        i.put("r", f7730d);
        i.put("v", e);
        i.put("vn", f);
        i.put(d.j, g);
        i.put(Constants.JSON_KEY_T, c.a());
        i.put("si", String.valueOf(Build.VERSION.SDK_INT));
        i.put("mo", h);
        Application d2 = Application.d();
        if (!miui.os.Build.IS_INTERNATIONAL_BUILD) {
            boolean a2 = a();
            i.put(d.V, !a2 ? ExtraTextUtils.toHexReadable(DigestUtils.get(c.b(d2), "MD5")) : "");
            i.put("ri", String.valueOf(a2));
        }
        i.put("o", c.d(d2));
        try {
            i.put("e", String.valueOf(d2.getPackageManager().getPackageInfo(d2.getPackageName(), 0).versionCode));
        } catch (PackageManager.NameNotFoundException e2) {
            e2.printStackTrace();
        }
    }

    public static String a(String str) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(b(str));
            return String.format("%1$032X", new Object[]{new BigInteger(1, instance.digest())});
        } catch (NoSuchAlgorithmException e2) {
            throw new RuntimeException(e2);
        }
    }

    public static String a(String str, j jVar) {
        return a((Map<String, String>) null, str, jVar);
    }

    public static String a(String str, a aVar, String str2, j jVar) {
        ByteArrayOutputStream byteArrayOutputStream;
        if (!com.miui.securitycenter.h.i()) {
            return "";
        }
        int i2 = -1;
        InputStream inputStream = null;
        try {
            HashMap hashMap = new HashMap();
            hashMap.put("u", UUID.randomUUID().toString().replace("-", ""));
            hashMap.put("sign", b((Map<String, String>) hashMap, str2));
            if (aVar == a.GET) {
                String a2 = a((Map<String, String>) hashMap);
                if (!str.contains("?")) {
                    str = str.concat("?");
                }
                str = str.concat(a2);
            }
            if (f7727a) {
                Log.d("NetUtil", "request start : " + str);
            }
            HttpURLConnection a3 = a(new URL(str));
            a(a3, aVar, (Map<String, String>) hashMap);
            int responseCode = a3.getResponseCode();
            try {
                if (f7727a) {
                    Log.d("NetUtil", " responseCode :  " + responseCode);
                }
                if (responseCode == 200) {
                    InputStream inputStream2 = a3.getInputStream();
                    try {
                        byteArrayOutputStream = new ByteArrayOutputStream();
                    } catch (Exception e2) {
                        i2 = responseCode;
                        inputStream = inputStream2;
                        e = e2;
                        byteArrayOutputStream = null;
                        try {
                            e.printStackTrace();
                            i.a(jVar, i2, 0);
                            a((Closeable) inputStream);
                            a((Closeable) byteArrayOutputStream);
                            return "";
                        } catch (Throwable th) {
                            th = th;
                            i.a(jVar, i2, 0);
                            a((Closeable) inputStream);
                            a((Closeable) byteArrayOutputStream);
                            throw th;
                        }
                    } catch (Throwable th2) {
                        i2 = responseCode;
                        inputStream = inputStream2;
                        th = th2;
                        byteArrayOutputStream = null;
                        i.a(jVar, i2, 0);
                        a((Closeable) inputStream);
                        a((Closeable) byteArrayOutputStream);
                        throw th;
                    }
                    try {
                        byte[] bArr = new byte[MpegAudioHeader.MAX_FRAME_SIZE_BYTES];
                        while (true) {
                            int read = inputStream2.read(bArr);
                            if (read == -1) {
                                break;
                            }
                            byteArrayOutputStream.write(bArr, 0, read);
                        }
                        String byteArrayOutputStream2 = byteArrayOutputStream.toString();
                        if (f7727a) {
                            Log.d("NetUtil", "request result  : " + byteArrayOutputStream2);
                        }
                        i.a(jVar, responseCode, 0);
                        a((Closeable) inputStream2);
                        a((Closeable) byteArrayOutputStream);
                        return byteArrayOutputStream2;
                    } catch (Exception e3) {
                        inputStream = inputStream2;
                        i2 = responseCode;
                        e = e3;
                        e.printStackTrace();
                        i.a(jVar, i2, 0);
                        a((Closeable) inputStream);
                        a((Closeable) byteArrayOutputStream);
                        return "";
                    } catch (Throwable th3) {
                        inputStream = inputStream2;
                        i2 = responseCode;
                        th = th3;
                        i.a(jVar, i2, 0);
                        a((Closeable) inputStream);
                        a((Closeable) byteArrayOutputStream);
                        throw th;
                    }
                } else {
                    i.a(jVar, responseCode, 0);
                    a((Closeable) null);
                    a((Closeable) null);
                    return "";
                }
            } catch (Exception e4) {
                e = e4;
                i2 = responseCode;
                byteArrayOutputStream = null;
                e.printStackTrace();
                i.a(jVar, i2, 0);
                a((Closeable) inputStream);
                a((Closeable) byteArrayOutputStream);
                return "";
            } catch (Throwable th4) {
                th = th4;
                i2 = responseCode;
                byteArrayOutputStream = null;
                i.a(jVar, i2, 0);
                a((Closeable) inputStream);
                a((Closeable) byteArrayOutputStream);
                throw th;
            }
        } catch (Exception e5) {
            e = e5;
            byteArrayOutputStream = null;
            e.printStackTrace();
            i.a(jVar, i2, 0);
            a((Closeable) inputStream);
            a((Closeable) byteArrayOutputStream);
            return "";
        } catch (Throwable th5) {
            th = th5;
            byteArrayOutputStream = null;
            i.a(jVar, i2, 0);
            a((Closeable) inputStream);
            a((Closeable) byteArrayOutputStream);
            throw th;
        }
    }

    public static String a(String str, String str2) {
        ByteArrayOutputStream byteArrayOutputStream;
        InputStream inputStream;
        ByteArrayOutputStream byteArrayOutputStream2;
        if (!com.miui.securitycenter.h.i()) {
            return "";
        }
        InputStream inputStream2 = null;
        try {
            if (f7727a) {
                Log.d("NetUtil", "request post json start : " + str2);
            }
            HttpURLConnection a2 = a(new URL(str2));
            a2.setConnectTimeout(QQMessage.TYPE_DISCUSS_GROUP);
            a2.setRequestMethod("POST");
            a2.addRequestProperty("Content-Type", "application/json");
            a2.setDoOutput(true);
            DataOutputStream dataOutputStream = new DataOutputStream(a2.getOutputStream());
            dataOutputStream.write(str.getBytes());
            dataOutputStream.close();
            int responseCode = a2.getResponseCode();
            if (f7727a) {
                Log.d("NetUtil", " responseCode :  " + responseCode);
            }
            if (responseCode == 200) {
                InputStream inputStream3 = a2.getInputStream();
                try {
                    byteArrayOutputStream = new ByteArrayOutputStream();
                } catch (Exception e2) {
                    byteArrayOutputStream2 = null;
                    Exception exc = e2;
                    inputStream = inputStream3;
                    e = exc;
                    try {
                        e.printStackTrace();
                        a((Closeable) inputStream);
                        a((Closeable) byteArrayOutputStream2);
                        return null;
                    } catch (Throwable th) {
                        th = th;
                        inputStream2 = inputStream;
                        byteArrayOutputStream = byteArrayOutputStream2;
                        a((Closeable) inputStream2);
                        a((Closeable) byteArrayOutputStream);
                        throw th;
                    }
                } catch (Throwable th2) {
                    inputStream2 = inputStream3;
                    th = th2;
                    byteArrayOutputStream = null;
                    a((Closeable) inputStream2);
                    a((Closeable) byteArrayOutputStream);
                    throw th;
                }
                try {
                    byte[] bArr = new byte[MpegAudioHeader.MAX_FRAME_SIZE_BYTES];
                    while (true) {
                        int read = inputStream3.read(bArr);
                        if (read == -1) {
                            break;
                        }
                        byteArrayOutputStream.write(bArr, 0, read);
                    }
                    String byteArrayOutputStream3 = byteArrayOutputStream.toString();
                    if (f7727a) {
                        Log.d("NetUtil", "request post json result : " + byteArrayOutputStream3);
                    }
                    a((Closeable) inputStream3);
                    a((Closeable) byteArrayOutputStream);
                    return byteArrayOutputStream3;
                } catch (Exception e3) {
                    ByteArrayOutputStream byteArrayOutputStream4 = byteArrayOutputStream;
                    inputStream = inputStream3;
                    e = e3;
                    byteArrayOutputStream2 = byteArrayOutputStream4;
                    e.printStackTrace();
                    a((Closeable) inputStream);
                    a((Closeable) byteArrayOutputStream2);
                    return null;
                } catch (Throwable th3) {
                    Throwable th4 = th3;
                    inputStream2 = inputStream3;
                    th = th4;
                    a((Closeable) inputStream2);
                    a((Closeable) byteArrayOutputStream);
                    throw th;
                }
            } else {
                a((Closeable) null);
                a((Closeable) null);
                return null;
            }
        } catch (Exception e4) {
            e = e4;
            inputStream = null;
            byteArrayOutputStream2 = null;
            e.printStackTrace();
            a((Closeable) inputStream);
            a((Closeable) byteArrayOutputStream2);
            return null;
        } catch (Throwable th5) {
            th = th5;
            byteArrayOutputStream = null;
            a((Closeable) inputStream2);
            a((Closeable) byteArrayOutputStream);
            throw th;
        }
    }

    public static String a(String str, Map<String, String> map, j jVar) {
        return a(str, map, a.GET, jVar);
    }

    private static String a(String str, Map<String, String> map, a aVar, j jVar) {
        ByteArrayOutputStream byteArrayOutputStream;
        int i2;
        if (!com.miui.securitycenter.h.i()) {
            return "";
        }
        int i3 = -1;
        InputStream inputStream = null;
        try {
            if (aVar == a.GET && map != null) {
                String a2 = a(map);
                if (!str.contains("?")) {
                    str = str.concat("?");
                }
                str = str.concat(a2);
            }
            if (f7727a) {
                Log.d("NetUtil", "request start : " + str);
            }
            HttpURLConnection a3 = a(new URL(str));
            a(a3, aVar, map);
            i2 = a3.getResponseCode();
            try {
                if (f7727a) {
                    Log.d("NetUtil", " responseCode :  " + i2);
                }
                if (i2 == 200) {
                    InputStream inputStream2 = a3.getInputStream();
                    try {
                        byteArrayOutputStream = new ByteArrayOutputStream();
                    } catch (Exception e2) {
                        i3 = i2;
                        inputStream = inputStream2;
                        e = e2;
                        byteArrayOutputStream = null;
                        try {
                            e.printStackTrace();
                            i.a(jVar, i3, 0);
                            a((Closeable) inputStream);
                            a((Closeable) byteArrayOutputStream);
                            return "";
                        } catch (Throwable th) {
                            th = th;
                            i2 = i3;
                            i.a(jVar, i2, 0);
                            a((Closeable) inputStream);
                            a((Closeable) byteArrayOutputStream);
                            throw th;
                        }
                    } catch (Throwable th2) {
                        inputStream = inputStream2;
                        th = th2;
                        byteArrayOutputStream = null;
                        i.a(jVar, i2, 0);
                        a((Closeable) inputStream);
                        a((Closeable) byteArrayOutputStream);
                        throw th;
                    }
                    try {
                        byte[] bArr = new byte[MpegAudioHeader.MAX_FRAME_SIZE_BYTES];
                        while (true) {
                            int read = inputStream2.read(bArr);
                            if (read == -1) {
                                break;
                            }
                            byteArrayOutputStream.write(bArr, 0, read);
                        }
                        String byteArrayOutputStream2 = byteArrayOutputStream.toString();
                        if (f7727a) {
                            Log.d("NetUtil", "request result  : " + byteArrayOutputStream2);
                        }
                        i.a(jVar, i2, 0);
                        a((Closeable) inputStream2);
                        a((Closeable) byteArrayOutputStream);
                        return byteArrayOutputStream2;
                    } catch (Exception e3) {
                        inputStream = inputStream2;
                        i3 = i2;
                        e = e3;
                        e.printStackTrace();
                        i.a(jVar, i3, 0);
                        a((Closeable) inputStream);
                        a((Closeable) byteArrayOutputStream);
                        return "";
                    } catch (Throwable th3) {
                        inputStream = inputStream2;
                        th = th3;
                        i.a(jVar, i2, 0);
                        a((Closeable) inputStream);
                        a((Closeable) byteArrayOutputStream);
                        throw th;
                    }
                } else {
                    i.a(jVar, i2, 0);
                    a((Closeable) null);
                    a((Closeable) null);
                    return "";
                }
            } catch (Exception e4) {
                e = e4;
                i3 = i2;
                byteArrayOutputStream = null;
                e.printStackTrace();
                i.a(jVar, i3, 0);
                a((Closeable) inputStream);
                a((Closeable) byteArrayOutputStream);
                return "";
            } catch (Throwable th4) {
                th = th4;
                byteArrayOutputStream = null;
                i.a(jVar, i2, 0);
                a((Closeable) inputStream);
                a((Closeable) byteArrayOutputStream);
                throw th;
            }
        } catch (Exception e5) {
            e = e5;
            byteArrayOutputStream = null;
            e.printStackTrace();
            i.a(jVar, i3, 0);
            a((Closeable) inputStream);
            a((Closeable) byteArrayOutputStream);
            return "";
        } catch (Throwable th5) {
            th = th5;
            i2 = -1;
            byteArrayOutputStream = null;
            i.a(jVar, i2, 0);
            a((Closeable) inputStream);
            a((Closeable) byteArrayOutputStream);
            throw th;
        }
    }

    private static String a(Map<String, String> map) {
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        try {
            for (Map.Entry next : map.entrySet()) {
                if (!z) {
                    sb.append('&');
                }
                sb.append(URLEncoder.encode((String) next.getKey(), C.UTF8_NAME));
                sb.append('=');
                sb.append(URLEncoder.encode((String) next.getValue(), C.UTF8_NAME));
                z = false;
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e2) {
            throw new RuntimeException("Encoding not supported: " + sb, e2);
        }
    }

    public static String a(Map<String, String> map, String str, j jVar) {
        return a(map, str, a.POST, "2dcd9s0c-ad3f-2fas-0l3a-abzo301jd0s9", false, jVar);
    }

    public static String a(Map<String, String> map, String str, a aVar, String str2, j jVar) {
        return a(map, str, aVar, str2, false, jVar);
    }

    private static String a(Map<String, String> map, String str, a aVar, String str2, boolean z, j jVar) {
        ByteArrayOutputStream byteArrayOutputStream;
        if (!com.miui.securitycenter.h.i() && !z) {
            return "";
        }
        int i2 = -1;
        InputStream inputStream = null;
        try {
            Map<String, String> a2 = a(map, str2);
            if (aVar == a.GET && a2 != null) {
                String a3 = a(a2);
                if (!str.contains("?")) {
                    str = str.concat("?");
                }
                str = str.concat(a3);
            }
            if (f7727a) {
                Log.d("NetUtil", "request start : " + str);
            }
            HttpURLConnection a4 = a(new URL(str));
            a(a4, aVar, a2);
            int responseCode = a4.getResponseCode();
            try {
                if (f7727a) {
                    Log.d("NetUtil", " responseCode :  " + responseCode);
                }
                if (responseCode == 200) {
                    InputStream inputStream2 = a4.getInputStream();
                    try {
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        try {
                            byte[] bArr = new byte[MpegAudioHeader.MAX_FRAME_SIZE_BYTES];
                            while (true) {
                                int read = inputStream2.read(bArr);
                                if (read == -1) {
                                    break;
                                }
                                byteArrayOutputStream.write(bArr, 0, read);
                            }
                            String byteArrayOutputStream2 = byteArrayOutputStream.toString();
                            if (f7727a) {
                                Log.d("NetUtil", "request result  : " + byteArrayOutputStream2);
                            }
                            i.a(jVar, responseCode, 0);
                            a((Closeable) inputStream2);
                            a((Closeable) byteArrayOutputStream);
                            return byteArrayOutputStream2;
                        } catch (Exception e2) {
                            i2 = responseCode;
                            inputStream = inputStream2;
                            e = e2;
                            try {
                                e.printStackTrace();
                                i.a(jVar, i2, 0);
                                a((Closeable) inputStream);
                                a((Closeable) byteArrayOutputStream);
                                return "";
                            } catch (Throwable th) {
                                th = th;
                                i.a(jVar, i2, 0);
                                a((Closeable) inputStream);
                                a((Closeable) byteArrayOutputStream);
                                throw th;
                            }
                        } catch (Throwable th2) {
                            i2 = responseCode;
                            inputStream = inputStream2;
                            th = th2;
                            i.a(jVar, i2, 0);
                            a((Closeable) inputStream);
                            a((Closeable) byteArrayOutputStream);
                            throw th;
                        }
                    } catch (Exception e3) {
                        i2 = responseCode;
                        e = e3;
                        byteArrayOutputStream = null;
                        inputStream = inputStream2;
                        e.printStackTrace();
                        i.a(jVar, i2, 0);
                        a((Closeable) inputStream);
                        a((Closeable) byteArrayOutputStream);
                        return "";
                    } catch (Throwable th3) {
                        i2 = responseCode;
                        th = th3;
                        byteArrayOutputStream = null;
                        inputStream = inputStream2;
                        i.a(jVar, i2, 0);
                        a((Closeable) inputStream);
                        a((Closeable) byteArrayOutputStream);
                        throw th;
                    }
                } else {
                    i.a(jVar, responseCode, 0);
                    a((Closeable) null);
                    a((Closeable) null);
                    return "";
                }
            } catch (Exception e4) {
                i2 = responseCode;
                e = e4;
                byteArrayOutputStream = null;
                e.printStackTrace();
                i.a(jVar, i2, 0);
                a((Closeable) inputStream);
                a((Closeable) byteArrayOutputStream);
                return "";
            } catch (Throwable th4) {
                i2 = responseCode;
                th = th4;
                byteArrayOutputStream = null;
                i.a(jVar, i2, 0);
                a((Closeable) inputStream);
                a((Closeable) byteArrayOutputStream);
                throw th;
            }
        } catch (Exception e5) {
            e = e5;
            byteArrayOutputStream = null;
            e.printStackTrace();
            i.a(jVar, i2, 0);
            a((Closeable) inputStream);
            a((Closeable) byteArrayOutputStream);
            return "";
        } catch (Throwable th5) {
            th = th5;
            byteArrayOutputStream = null;
            i.a(jVar, i2, 0);
            a((Closeable) inputStream);
            a((Closeable) byteArrayOutputStream);
            throw th;
        }
    }

    public static String a(Map<String, String> map, String str, String str2, j jVar) {
        return a(map, str, a.POST, str2, false, jVar);
    }

    public static String a(Map<String, String> map, String str, String str2, boolean z, j jVar) {
        return a(map, str, a.POST, str2, z, jVar);
    }

    private static HttpURLConnection a(URL url) {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setConnectTimeout(15000);
        httpURLConnection.setReadTimeout(15000);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setDoInput(true);
        return httpURLConnection;
    }

    public static Map<String, String> a(Map<String, String> map, String str) {
        if (map == null) {
            map = new HashMap<>();
        }
        map.putAll(i);
        Application d2 = Application.d();
        map.put("l", Locale.getDefault().toString());
        map.put("n", c.c(d2));
        if (miui.os.Build.IS_INTERNATIONAL_BUILD) {
            try {
                String a2 = b.a(d2);
                if (a2 != null) {
                    map.put(CloudPushConstants.WATERMARK_TYPE.GLOBAL, a2);
                }
            } catch (Exception e2) {
                Log.e("NetUtil", "getAdvertisingId error", e2);
            }
        } else {
            map.put(AnimatedProperty.PROPERTY_NAME_X, c.a((Context) d2));
            map.put("oa", g.a(d2));
        }
        map.put("sign", b(map, str));
        return map;
    }

    private static void a(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException unused) {
            }
        }
    }

    private static void a(HttpURLConnection httpURLConnection, a aVar, Map<String, String> map) {
        String str;
        int i2 = j.f7726a[aVar.ordinal()];
        if (i2 == 1) {
            str = "GET";
        } else if (i2 == 2) {
            str = "DELETE";
        } else if (i2 == 3) {
            httpURLConnection.setRequestMethod("POST");
            a(httpURLConnection, map);
            return;
        } else if (i2 == 4) {
            str = "PUT";
        } else {
            throw new IllegalStateException("Unknown method type.");
        }
        httpURLConnection.setRequestMethod(str);
    }

    private static void a(HttpURLConnection httpURLConnection, Map<String, String> map) {
        byte[] bArr;
        if (map == null || map.size() <= 0) {
            bArr = null;
        } else {
            String a2 = a(map);
            if (f7727a) {
                Log.d("NetUtil", " post body : " + a2);
            }
            bArr = a2.getBytes();
        }
        if (bArr != null) {
            httpURLConnection.setDoOutput(true);
            httpURLConnection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
            dataOutputStream.write(bArr);
            dataOutputStream.close();
        }
    }

    public static boolean a() {
        return o.f2310b.equals(y.a("ro.miui.restrict_imei", "")) || o.f2310b.equals(y.a("ro.miui.restrict_imei_p", ""));
    }

    public static String b(String str, j jVar) {
        return a(str, a.POST, "2dcd9s0c-ad3f-2fas-0l3a-abzo301jd0s9", jVar);
    }

    public static String b(Map<String, String> map, String str) {
        StringBuilder sb = new StringBuilder();
        ArrayList arrayList = new ArrayList();
        for (Map.Entry next : map.entrySet()) {
            arrayList.add(new b((String) next.getKey(), (String) next.getValue()));
        }
        Collections.sort(arrayList);
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            if (i2 > 0) {
                sb.append("&");
            }
            b bVar = (b) arrayList.get(i2);
            sb.append(bVar.f7735a);
            sb.append("=");
            sb.append(bVar.f7736b);
        }
        sb.append("&");
        sb.append(str);
        return a(new String(Base64.encodeToString(b(sb.toString()), 2)));
    }

    private static byte[] b(String str) {
        try {
            return str.getBytes(C.UTF8_NAME);
        } catch (UnsupportedEncodingException unused) {
            return str.getBytes();
        }
    }
}
