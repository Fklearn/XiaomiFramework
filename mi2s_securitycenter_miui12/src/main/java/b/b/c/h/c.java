package b.b.c.h;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import b.b.c.j.i;
import b.b.c.j.x;
import b.b.c.j.y;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.miui.activityutil.h;
import com.miui.luckymoney.config.Constants;
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
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import miui.os.Build;
import miui.security.DigestUtils;
import miui.text.ExtraTextUtils;
import org.json.JSONException;
import org.json.JSONObject;

class c {

    /* renamed from: a  reason: collision with root package name */
    private static final boolean f1724a = a.f7625a;

    /* renamed from: b  reason: collision with root package name */
    private static final String f1725b = y.a("ro.product.device", h.f2289a);

    /* renamed from: c  reason: collision with root package name */
    private static final String f1726c = y.a("ro.carrier.name", h.f2289a);

    /* renamed from: d  reason: collision with root package name */
    private static final String f1727d = Build.getRegion();
    private static final String e = ("MIUI-" + Build.VERSION.INCREMENTAL);

    public static String a(Context context, String str, JSONObject jSONObject, String str2, j jVar) {
        if (TextUtils.isEmpty(str) || !com.miui.securitycenter.h.i()) {
            return null;
        }
        List<b.b.c.g.c> a2 = a(context, str2);
        if (jSONObject != null) {
            Iterator<String> keys = jSONObject.keys();
            while (keys.hasNext()) {
                String next = keys.next();
                try {
                    a2.add(new b.b.c.g.c(next, jSONObject.getString(next)));
                } catch (JSONException e2) {
                    e2.printStackTrace();
                }
            }
        }
        a2.add(new b.b.c.g.c("sign", a(a2, str2)));
        return a(str, a2, jVar);
    }

    public static String a(String str, List<b.b.c.g.c> list, j jVar) {
        int i;
        ByteArrayOutputStream byteArrayOutputStream;
        int i2 = -1;
        InputStream inputStream = null;
        try {
            HttpURLConnection a2 = a(new URL(str));
            a2.setRequestMethod("POST");
            a(a2, list);
            i = a2.getResponseCode();
            try {
                if (f1724a) {
                    Log.d("NetworkApiHelper", " responseCode :  " + i);
                }
                if (i == 200) {
                    InputStream inputStream2 = a2.getInputStream();
                    try {
                        byteArrayOutputStream = new ByteArrayOutputStream();
                    } catch (Exception e2) {
                        e = e2;
                        byteArrayOutputStream = null;
                        inputStream = inputStream2;
                        e = e;
                        i2 = i;
                        try {
                            Log.e("NetworkApiHelper", "requestCore exception", e);
                            i.a(jVar, i2, 0);
                            a((Closeable) inputStream);
                            a((Closeable) byteArrayOutputStream);
                            return "";
                        } catch (Throwable th) {
                            th = th;
                            i = i2;
                            i.a(jVar, i, 0);
                            a((Closeable) inputStream);
                            a((Closeable) byteArrayOutputStream);
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        byteArrayOutputStream = null;
                        inputStream = inputStream2;
                        th = th;
                        i.a(jVar, i, 0);
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
                        if (f1724a) {
                            Log.d("NetworkApiHelper", "request result  : " + byteArrayOutputStream2);
                        }
                        i.a(jVar, i, 0);
                        a((Closeable) inputStream2);
                        a((Closeable) byteArrayOutputStream);
                        return byteArrayOutputStream2;
                    } catch (Exception e3) {
                        e = e3;
                        inputStream = inputStream2;
                        e = e;
                        i2 = i;
                        Log.e("NetworkApiHelper", "requestCore exception", e);
                        i.a(jVar, i2, 0);
                        a((Closeable) inputStream);
                        a((Closeable) byteArrayOutputStream);
                        return "";
                    } catch (Throwable th3) {
                        th = th3;
                        inputStream = inputStream2;
                        th = th;
                        i.a(jVar, i, 0);
                        a((Closeable) inputStream);
                        a((Closeable) byteArrayOutputStream);
                        throw th;
                    }
                } else {
                    i.a(jVar, i, 0);
                    a((Closeable) null);
                    a((Closeable) null);
                    return "";
                }
            } catch (Exception e4) {
                e = e4;
                i2 = i;
                byteArrayOutputStream = null;
                Log.e("NetworkApiHelper", "requestCore exception", e);
                i.a(jVar, i2, 0);
                a((Closeable) inputStream);
                a((Closeable) byteArrayOutputStream);
                return "";
            } catch (Throwable th4) {
                th = th4;
                byteArrayOutputStream = null;
                i.a(jVar, i, 0);
                a((Closeable) inputStream);
                a((Closeable) byteArrayOutputStream);
                throw th;
            }
        } catch (Exception e5) {
            e = e5;
            byteArrayOutputStream = null;
            Log.e("NetworkApiHelper", "requestCore exception", e);
            i.a(jVar, i2, 0);
            a((Closeable) inputStream);
            a((Closeable) byteArrayOutputStream);
            return "";
        } catch (Throwable th5) {
            th = th5;
            i = -1;
            byteArrayOutputStream = null;
            i.a(jVar, i, 0);
            a((Closeable) inputStream);
            a((Closeable) byteArrayOutputStream);
            throw th;
        }
    }

    private static String a(List<b.b.c.g.c> list) {
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        try {
            for (b.b.c.g.c next : list) {
                if (!z) {
                    sb.append('&');
                }
                sb.append(URLEncoder.encode(next.a(), C.UTF8_NAME));
                sb.append('=');
                sb.append(URLEncoder.encode(next.b(), C.UTF8_NAME));
                z = false;
            }
            return sb.toString();
        } catch (UnsupportedEncodingException e2) {
            throw new RuntimeException("Encoding not supported: " + sb, e2);
        }
    }

    private static String a(List<b.b.c.g.c> list, String str) {
        Collections.sort(list, new b());
        StringBuilder sb = new StringBuilder();
        boolean z = true;
        for (b.b.c.g.c next : list) {
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

    public static HttpURLConnection a(URL url) {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setConnectTimeout(15000);
        httpURLConnection.setReadTimeout(15000);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setDoInput(true);
        return httpURLConnection;
    }

    private static List<b.b.c.g.c> a(Context context, String str) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new b.b.c.g.c(Constants.JSON_KEY_DEVICE, f1725b));
        arrayList.add(new b.b.c.g.c(Constants.JSON_KEY_CARRIER, f1726c));
        arrayList.add(new b.b.c.g.c("region", f1727d));
        arrayList.add(new b.b.c.g.c(Constants.JSON_KEY_MIUI_VERSION, e));
        arrayList.add(new b.b.c.g.c(Constants.JSON_KEY_VERSION_TYPE, i.b()));
        arrayList.add(new b.b.c.g.c(Constants.JSON_KEY_APP_VERSION, x.a(context)));
        arrayList.add(new b.b.c.g.c("mi", ExtraTextUtils.toHexReadable(DigestUtils.get(i.b(context) + "-" + str, "MD5"))));
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

    private static void a(HttpURLConnection httpURLConnection, List<b.b.c.g.c> list) {
        byte[] bArr;
        if (list == null || list.size() <= 0) {
            bArr = null;
        } else {
            String a2 = a(list);
            if (f1724a) {
                Log.d("NetworkApiHelper", " post body : " + a2);
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
}
