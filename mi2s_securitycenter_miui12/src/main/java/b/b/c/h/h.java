package b.b.c.h;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import b.b.c.g.c;
import b.b.c.j.i;
import b.b.c.j.x;
import b.b.c.j.y;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.miui.luckymoney.config.Constants;
import com.miui.permcenter.permissions.C0466c;
import com.miui.securityscan.c.a;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import miui.os.Build;
import miui.security.DigestUtils;
import miui.text.ExtraTextUtils;
import org.json.JSONObject;

class h {

    /* renamed from: a  reason: collision with root package name */
    private static final boolean f1733a = a.f7625a;

    /* renamed from: b  reason: collision with root package name */
    private static final String f1734b = y.a("ro.product.device", com.miui.activityutil.h.f2289a);

    /* renamed from: c  reason: collision with root package name */
    private static final String f1735c = y.a("ro.carrier.name", com.miui.activityutil.h.f2289a);

    /* renamed from: d  reason: collision with root package name */
    private static final String f1736d = Build.getRegion();
    private static final String e = ("MIUI-" + Build.VERSION.INCREMENTAL);

    public static String a(Context context, String str, JSONObject jSONObject, String str2, j jVar) {
        if (TextUtils.isEmpty(str) || !com.miui.securitycenter.h.i()) {
            return null;
        }
        List<c> a2 = a(context, str2);
        if (jSONObject != null) {
            Iterator<String> keys = jSONObject.keys();
            while (keys.hasNext()) {
                String next = keys.next();
                a2.add(new c(next, jSONObject.getString(next)));
            }
        }
        a2.add(new c("sign", a(a2, str2)));
        return c.a(str, a2, jVar);
    }

    public static String a(Context context, String str, JSONObject jSONObject, String str2, String str3, j jVar) {
        DataOutputStream dataOutputStream;
        int i;
        ByteArrayOutputStream byteArrayOutputStream;
        int i2;
        ByteArrayOutputStream byteArrayOutputStream2;
        ByteArrayOutputStream byteArrayOutputStream3;
        InputStream inputStream;
        InputStream inputStream2 = null;
        if (TextUtils.isEmpty(str) || !com.miui.securitycenter.h.i()) {
            return null;
        }
        List<c> a2 = a(context, str3);
        if (jSONObject != null) {
            Iterator<String> keys = jSONObject.keys();
            while (keys.hasNext()) {
                String next = keys.next();
                a2.add(new c(next, jSONObject.getString(next)));
            }
        }
        a2.add(new c("sign", a(a2, str3)));
        String a3 = a(str, a2);
        byte[] c2 = c(str2);
        try {
            HttpURLConnection a4 = c.a(new URL(a3));
            a4.setDoOutput(true);
            a4.addRequestProperty("Content-Type", "binary/octet-stream");
            dataOutputStream = new DataOutputStream(a4.getOutputStream());
            try {
                dataOutputStream.write(c2);
                i = a4.getResponseCode();
                if (i == 200) {
                    try {
                        inputStream = a4.getInputStream();
                    } catch (Exception e2) {
                        e = e2;
                        byteArrayOutputStream = null;
                        try {
                            Log.e("NewNetworkApiHelper", "doPostWithGzip exception", e);
                            i.a(jVar, i, 0);
                            a((Closeable) inputStream2);
                            a((Closeable) byteArrayOutputStream);
                            a((Closeable) dataOutputStream);
                            return "";
                        } catch (Throwable th) {
                            th = th;
                            i.a(jVar, i, 0);
                            a((Closeable) inputStream2);
                            a((Closeable) byteArrayOutputStream);
                            a((Closeable) dataOutputStream);
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        byteArrayOutputStream = null;
                        i.a(jVar, i, 0);
                        a((Closeable) inputStream2);
                        a((Closeable) byteArrayOutputStream);
                        a((Closeable) dataOutputStream);
                        throw th;
                    }
                    try {
                        byteArrayOutputStream = new ByteArrayOutputStream();
                        try {
                            byte[] bArr = new byte[MpegAudioHeader.MAX_FRAME_SIZE_BYTES];
                            while (true) {
                                int read = inputStream.read(bArr);
                                if (read == -1) {
                                    break;
                                }
                                byteArrayOutputStream.write(bArr, 0, read);
                            }
                            String byteArrayOutputStream4 = byteArrayOutputStream.toString();
                            if (f1733a) {
                                Log.d("NewNetworkApiHelper", "request result  : " + byteArrayOutputStream4);
                            }
                            i.a(jVar, i, 0);
                            a((Closeable) inputStream);
                            a((Closeable) byteArrayOutputStream);
                            a((Closeable) dataOutputStream);
                            return byteArrayOutputStream4;
                        } catch (Exception e3) {
                            e = e3;
                            inputStream2 = inputStream;
                            Log.e("NewNetworkApiHelper", "doPostWithGzip exception", e);
                            i.a(jVar, i, 0);
                            a((Closeable) inputStream2);
                            a((Closeable) byteArrayOutputStream);
                            a((Closeable) dataOutputStream);
                            return "";
                        } catch (Throwable th3) {
                            th = th3;
                            inputStream2 = inputStream;
                            i.a(jVar, i, 0);
                            a((Closeable) inputStream2);
                            a((Closeable) byteArrayOutputStream);
                            a((Closeable) dataOutputStream);
                            throw th;
                        }
                    } catch (Exception e4) {
                        e = e4;
                        byteArrayOutputStream = null;
                        inputStream2 = inputStream;
                        Log.e("NewNetworkApiHelper", "doPostWithGzip exception", e);
                        i.a(jVar, i, 0);
                        a((Closeable) inputStream2);
                        a((Closeable) byteArrayOutputStream);
                        a((Closeable) dataOutputStream);
                        return "";
                    } catch (Throwable th4) {
                        th = th4;
                        byteArrayOutputStream = null;
                        inputStream2 = inputStream;
                        i.a(jVar, i, 0);
                        a((Closeable) inputStream2);
                        a((Closeable) byteArrayOutputStream);
                        a((Closeable) dataOutputStream);
                        throw th;
                    }
                } else {
                    i.a(jVar, i, 0);
                    a((Closeable) null);
                    a((Closeable) null);
                    a((Closeable) dataOutputStream);
                    return "";
                }
            } catch (Exception e5) {
                e = e5;
                i2 = -1;
                byteArrayOutputStream2 = null;
                e = e;
                Log.e("NewNetworkApiHelper", "doPostWithGzip exception", e);
                i.a(jVar, i, 0);
                a((Closeable) inputStream2);
                a((Closeable) byteArrayOutputStream);
                a((Closeable) dataOutputStream);
                return "";
            } catch (Throwable th5) {
                th = th5;
                i = -1;
                byteArrayOutputStream3 = null;
                th = th;
                i.a(jVar, i, 0);
                a((Closeable) inputStream2);
                a((Closeable) byteArrayOutputStream);
                a((Closeable) dataOutputStream);
                throw th;
            }
        } catch (Exception e6) {
            e = e6;
            i2 = -1;
            dataOutputStream = null;
            byteArrayOutputStream2 = null;
            e = e;
            Log.e("NewNetworkApiHelper", "doPostWithGzip exception", e);
            i.a(jVar, i, 0);
            a((Closeable) inputStream2);
            a((Closeable) byteArrayOutputStream);
            a((Closeable) dataOutputStream);
            return "";
        } catch (Throwable th6) {
            th = th6;
            i = -1;
            dataOutputStream = null;
            byteArrayOutputStream3 = null;
            th = th;
            i.a(jVar, i, 0);
            a((Closeable) inputStream2);
            a((Closeable) byteArrayOutputStream);
            a((Closeable) dataOutputStream);
            throw th;
        }
    }

    private static String a(String str, List<c> list) {
        try {
            for (c next : list) {
                String a2 = next.a();
                String b2 = next.b();
                if (!str.contains("?")) {
                    str = str + "?";
                }
                if (!str.endsWith("?")) {
                    str = str + "&";
                }
                str = str + a2 + "=" + b2;
            }
            return str;
        } catch (Exception e2) {
            e2.printStackTrace();
            return null;
        }
    }

    private static String a(List<c> list, String str) {
        Collections.sort(list, new g());
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        for (c next : list) {
            if (!z) {
                sb.append("&");
            }
            sb.append(next.a());
            sb.append("=");
            sb.append(next.b());
            z = false;
        }
        sb.append("&");
        sb.append(str);
        return b(new String(Base64.encodeToString(a(sb.toString()), 2)));
    }

    private static List<c> a(Context context, String str) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new c("d", f1734b));
        arrayList.add(new c(C0466c.f6254a, f1735c));
        arrayList.add(new c("r", f1736d));
        arrayList.add(new c("v", e));
        arrayList.add(new c(Constants.JSON_KEY_T, i.b()));
        arrayList.add(new c("e", x.a(context)));
        arrayList.add(new c("l", Locale.getDefault().toString()));
        arrayList.add(new c("a", ExtraTextUtils.toHexReadable(DigestUtils.get(i.b(context) + str, "MD5"))));
        arrayList.add(new c("o", i.d(context)));
        return arrayList;
    }

    private static void a(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException unused) {
            }
        }
    }

    private static byte[] a(String str) {
        try {
            return str.getBytes(C.UTF8_NAME);
        } catch (UnsupportedEncodingException unused) {
            return str.getBytes();
        }
    }

    private static String b(String str) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(a(str));
            return String.format("%1$032X", new Object[]{new BigInteger(1, instance.digest())});
        } catch (NoSuchAlgorithmException e2) {
            throw new RuntimeException(e2);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:16:0x0027 A[SYNTHETIC, Splitter:B:16:0x0027] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static byte[] c(java.lang.String r3) {
        /*
            java.io.ByteArrayOutputStream r0 = new java.io.ByteArrayOutputStream
            r0.<init>()
            r1 = 0
            java.util.zip.GZIPOutputStream r2 = new java.util.zip.GZIPOutputStream     // Catch:{ all -> 0x0023 }
            r2.<init>(r0)     // Catch:{ all -> 0x0023 }
            java.lang.String r1 = "UTF-8"
            byte[] r3 = r3.getBytes(r1)     // Catch:{ all -> 0x0021 }
            r2.write(r3)     // Catch:{ all -> 0x0021 }
            r2.close()     // Catch:{ IOException -> 0x0018 }
            goto L_0x001c
        L_0x0018:
            r3 = move-exception
            r3.printStackTrace()
        L_0x001c:
            byte[] r3 = r0.toByteArray()
            return r3
        L_0x0021:
            r3 = move-exception
            goto L_0x0025
        L_0x0023:
            r3 = move-exception
            r2 = r1
        L_0x0025:
            if (r2 == 0) goto L_0x002f
            r2.close()     // Catch:{ IOException -> 0x002b }
            goto L_0x002f
        L_0x002b:
            r0 = move-exception
            r0.printStackTrace()
        L_0x002f:
            throw r3
        */
        throw new UnsupportedOperationException("Method not decompiled: b.b.c.h.h.c(java.lang.String):byte[]");
    }
}
