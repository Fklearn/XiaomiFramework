package com.miui.permcenter.install;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import b.b.c.h.i;
import b.b.c.h.j;
import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.miui.activityutil.h;
import com.miui.luckymoney.config.Constants;
import com.miui.permcenter.compact.SystemPropertiesCompat;
import com.miui.securityscan.i.c;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import miui.os.Build;
import miui.security.DigestUtils;
import miui.text.ExtraTextUtils;
import miui.util.IOUtils;

public class f {

    /* renamed from: a  reason: collision with root package name */
    private static final String f6148a = SystemPropertiesCompat.getString("ro.product.device", h.f2289a);

    /* renamed from: b  reason: collision with root package name */
    private static final String f6149b = SystemPropertiesCompat.getString("ro.carrier.name", h.f2289a);

    /* renamed from: c  reason: collision with root package name */
    private static final String f6150c = Build.getRegion();

    /* renamed from: d  reason: collision with root package name */
    private static final String f6151d = Build.VERSION.INCREMENTAL;
    private static final HashMap<String, String> e = new HashMap<>();

    static {
        e.put(Constants.JSON_KEY_DEVICE, f6148a);
        e.put(Constants.JSON_KEY_CARRIER, f6149b);
        e.put("region", f6150c);
        e.put("miui_version", f6151d);
        e.put("version_type", c.a());
        e.put("miui_version_name", SystemPropertiesCompat.getString("ro.miui.ui.version.name", h.f2289a));
    }

    public static String a(String str, byte[] bArr, j jVar) {
        ByteArrayOutputStream byteArrayOutputStream;
        OutputStream outputStream;
        InputStream inputStream;
        int i = -1;
        InputStream inputStream2 = null;
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            httpURLConnection.setConnectTimeout(5000);
            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(bArr.length));
            httpURLConnection.setDoOutput(true);
            outputStream = httpURLConnection.getOutputStream();
            try {
                outputStream.write(bArr);
                int responseCode = httpURLConnection.getResponseCode();
                if (responseCode == 200) {
                    try {
                        inputStream = httpURLConnection.getInputStream();
                    } catch (Exception e2) {
                        e = e2;
                        byteArrayOutputStream = null;
                        i = responseCode;
                        try {
                            Log.e("NetConnectUtil", "post error!", e);
                            i.a(jVar, i, 0);
                            IOUtils.closeQuietly(inputStream2);
                            IOUtils.closeQuietly(outputStream);
                            IOUtils.closeQuietly(byteArrayOutputStream);
                            return "";
                        } catch (Throwable th) {
                            th = th;
                            i.a(jVar, i, 0);
                            IOUtils.closeQuietly(inputStream2);
                            IOUtils.closeQuietly(outputStream);
                            IOUtils.closeQuietly(byteArrayOutputStream);
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        byteArrayOutputStream = null;
                        i = responseCode;
                        i.a(jVar, i, 0);
                        IOUtils.closeQuietly(inputStream2);
                        IOUtils.closeQuietly(outputStream);
                        IOUtils.closeQuietly(byteArrayOutputStream);
                        throw th;
                    }
                    try {
                        byteArrayOutputStream = new ByteArrayOutputStream();
                    } catch (Exception e3) {
                        e = e3;
                        byteArrayOutputStream = null;
                        inputStream2 = inputStream;
                        e = e;
                        i = responseCode;
                        Log.e("NetConnectUtil", "post error!", e);
                        i.a(jVar, i, 0);
                        IOUtils.closeQuietly(inputStream2);
                        IOUtils.closeQuietly(outputStream);
                        IOUtils.closeQuietly(byteArrayOutputStream);
                        return "";
                    } catch (Throwable th3) {
                        th = th3;
                        byteArrayOutputStream = null;
                        inputStream2 = inputStream;
                        th = th;
                        i = responseCode;
                        i.a(jVar, i, 0);
                        IOUtils.closeQuietly(inputStream2);
                        IOUtils.closeQuietly(outputStream);
                        IOUtils.closeQuietly(byteArrayOutputStream);
                        throw th;
                    }
                    try {
                        byte[] bArr2 = new byte[MpegAudioHeader.MAX_FRAME_SIZE_BYTES];
                        while (true) {
                            int read = inputStream.read(bArr2);
                            if (read != -1) {
                                byteArrayOutputStream.write(bArr2, 0, read);
                            } else {
                                String byteArrayOutputStream2 = byteArrayOutputStream.toString();
                                i.a(jVar, responseCode, 0);
                                IOUtils.closeQuietly(inputStream);
                                IOUtils.closeQuietly(outputStream);
                                IOUtils.closeQuietly(byteArrayOutputStream);
                                return byteArrayOutputStream2;
                            }
                        }
                    } catch (Exception e4) {
                        e = e4;
                        inputStream2 = inputStream;
                        e = e;
                        i = responseCode;
                        Log.e("NetConnectUtil", "post error!", e);
                        i.a(jVar, i, 0);
                        IOUtils.closeQuietly(inputStream2);
                        IOUtils.closeQuietly(outputStream);
                        IOUtils.closeQuietly(byteArrayOutputStream);
                        return "";
                    } catch (Throwable th4) {
                        th = th4;
                        inputStream2 = inputStream;
                        th = th;
                        i = responseCode;
                        i.a(jVar, i, 0);
                        IOUtils.closeQuietly(inputStream2);
                        IOUtils.closeQuietly(outputStream);
                        IOUtils.closeQuietly(byteArrayOutputStream);
                        throw th;
                    }
                } else {
                    i.a(jVar, responseCode, 0);
                    IOUtils.closeQuietly((InputStream) null);
                    IOUtils.closeQuietly(outputStream);
                    IOUtils.closeQuietly((OutputStream) null);
                    return "";
                }
            } catch (Exception e5) {
                e = e5;
                byteArrayOutputStream = null;
                Log.e("NetConnectUtil", "post error!", e);
                i.a(jVar, i, 0);
                IOUtils.closeQuietly(inputStream2);
                IOUtils.closeQuietly(outputStream);
                IOUtils.closeQuietly(byteArrayOutputStream);
                return "";
            } catch (Throwable th5) {
                th = th5;
                byteArrayOutputStream = null;
                i.a(jVar, i, 0);
                IOUtils.closeQuietly(inputStream2);
                IOUtils.closeQuietly(outputStream);
                IOUtils.closeQuietly(byteArrayOutputStream);
                throw th;
            }
        } catch (Exception e6) {
            e = e6;
            outputStream = null;
            byteArrayOutputStream = null;
            Log.e("NetConnectUtil", "post error!", e);
            i.a(jVar, i, 0);
            IOUtils.closeQuietly(inputStream2);
            IOUtils.closeQuietly(outputStream);
            IOUtils.closeQuietly(byteArrayOutputStream);
            return "";
        } catch (Throwable th6) {
            th = th6;
            outputStream = null;
            byteArrayOutputStream = null;
            i.a(jVar, i, 0);
            IOUtils.closeQuietly(inputStream2);
            IOUtils.closeQuietly(outputStream);
            IOUtils.closeQuietly(byteArrayOutputStream);
            throw th;
        }
    }

    public static Map<String, String> a(Context context, Map<String, String> map) {
        if (map == null) {
            map = new HashMap<>();
        }
        map.putAll(e);
        map.put("language", Locale.getDefault().toString());
        map.put("network_type", c.c(context));
        String a2 = c.a(context);
        map.put("mi", ExtraTextUtils.toHexReadable(DigestUtils.get(a2 + "2dcd9s0c-ad3f-2fas-0l3a-abzo301jd0s9", "MD5")));
        map.put("android_id", a2);
        map.put(Constants.JSON_KEY_IMEI, ExtraTextUtils.toHexReadable(DigestUtils.get(c.b(context), "MD5")));
        map.put("sim", b(context));
        try {
            map.put("app_version", String.valueOf(context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode));
        } catch (PackageManager.NameNotFoundException e2) {
            Log.e("NetConnectUtil", "NameNotFoundException", e2);
        }
        return map;
    }

    public static boolean a(Context context) {
        if (context == null) {
            return false;
        }
        try {
            NetworkInfo activeNetworkInfo = ((ConnectivityManager) context.getSystemService("connectivity")).getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                return activeNetworkInfo.isAvailable();
            }
            return false;
        } catch (Exception e2) {
            Log.d("NetConnectUtil", "isNetworkConnected:" + e2.toString());
            return false;
        }
    }

    private static String b(Context context) {
        try {
            String subscriberId = ((TelephonyManager) context.getSystemService("phone")).getSubscriberId();
            return !TextUtils.isEmpty(subscriberId) ? subscriberId : "off";
        } catch (Exception e2) {
            Log.e("NetConnectUtil", "getImsi", e2);
            return "off";
        }
    }
}
