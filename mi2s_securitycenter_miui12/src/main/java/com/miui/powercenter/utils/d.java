package com.miui.powercenter.utils;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;
import com.google.android.exoplayer2.extractor.MpegAudioHeader;
import com.miui.luckymoney.config.Constants;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.h;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import miui.os.Build;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class d {

    /* renamed from: a  reason: collision with root package name */
    private Handler f7298a;

    /* renamed from: b  reason: collision with root package name */
    private SharedPreferences f7299b;

    /* renamed from: c  reason: collision with root package name */
    private b f7300c;

    /* renamed from: d  reason: collision with root package name */
    private ExecutorService f7301d = Executors.newSingleThreadExecutor();
    /* access modifiers changed from: private */
    public final Object e = new Object();

    private static final class a {
        /* access modifiers changed from: private */

        /* renamed from: a  reason: collision with root package name */
        public static final d f7302a = new d();
    }

    private class b {

        /* renamed from: a  reason: collision with root package name */
        private boolean f7303a;
        /* access modifiers changed from: private */

        /* renamed from: b  reason: collision with root package name */
        public boolean f7304b;

        /* renamed from: c  reason: collision with root package name */
        private long f7305c;

        /* renamed from: d  reason: collision with root package name */
        private Runnable f7306d;
        /* access modifiers changed from: private */
        public ChargeInfo e;

        private b() {
            this.f7303a = false;
            this.f7304b = false;
        }

        /* synthetic */ b(d dVar, c cVar) {
            this();
        }

        /* access modifiers changed from: private */
        public void a(ChargeInfo chargeInfo) {
            if (chargeInfo.charged - chargeInfo.charging >= 0 && chargeInfo.duration >= 10) {
                synchronized (d.this.e) {
                    Set stringSet = d.this.d().getStringSet("charge_report_list", (Set) null);
                    if (stringSet == null) {
                        stringSet = new HashSet();
                    }
                    JSONObject json = chargeInfo.toJson();
                    if (json != null) {
                        stringSet.add(json.toString());
                        d.this.d().edit().putStringSet("charge_report_list", stringSet).apply();
                    }
                }
            }
        }

        /* access modifiers changed from: private */
        public void a(boolean z, int i) {
            if (this.f7306d != null) {
                d.this.b().removeCallbacks(this.f7306d);
                this.f7306d = null;
            }
            if (this.e == null) {
                this.e = new ChargeInfo();
            }
            if (this.f7303a && !z) {
                ChargeInfo chargeInfo = this.e;
                chargeInfo.charged = (long) i;
                chargeInfo.duration = (SystemClock.elapsedRealtime() / 1000) - this.f7305c;
                Handler c2 = d.this.b();
                e eVar = new e(this);
                this.f7306d = eVar;
                c2.postDelayed(eVar, 30000);
            } else if (!this.f7303a) {
                this.f7303a = true;
                this.e.charging = (long) i;
                this.f7305c = SystemClock.elapsedRealtime() / 1000;
            }
        }

        /* access modifiers changed from: private */
        public boolean a() {
            return this.f7304b;
        }
    }

    public static d a() {
        return a.f7302a;
    }

    private String a(long j) {
        String str;
        String str2 = "biz_id=charge_stat&timestamp=" + j;
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(str2.getBytes());
            str = String.format("%1$032X", new Object[]{new BigInteger(1, instance.digest())});
        } catch (NoSuchAlgorithmException e2) {
            Log.w("ChargeReporter", "get sign failed. " + e2);
            str = "";
        }
        return str.toLowerCase(Locale.ENGLISH);
    }

    private HttpURLConnection a(URL url) {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setConnectTimeout(15000);
        httpURLConnection.setReadTimeout(15000);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setDoInput(true);
        httpURLConnection.setDoOutput(true);
        return httpURLConnection;
    }

    private void a(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: private */
    public void a(ArrayList<ChargeInfo> arrayList) {
        ByteArrayOutputStream byteArrayOutputStream;
        long currentTimeMillis = System.currentTimeMillis() / 1000;
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("biz_id", "charge_stat");
            jSONObject.put("timestamp", currentTimeMillis);
            jSONObject.put("sign", a(currentTimeMillis));
            jSONObject.put(Constants.JSON_KEY_DEVICE, Build.DEVICE);
            jSONObject.put("capacity", o.c(Application.d()));
            JSONArray jSONArray = new JSONArray();
            Iterator<ChargeInfo> it = arrayList.iterator();
            while (it.hasNext()) {
                jSONArray.put(it.next().toJson());
            }
            jSONObject.put("charge_data", jSONArray);
        } catch (JSONException e2) {
            Log.w("ChargeReporter", "upload failed. " + e2);
        }
        InputStream inputStream = null;
        try {
            HttpURLConnection a2 = a(new URL("https://data.sec.miui.com/data/chargeV2"));
            a2.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            a2.setRequestMethod("POST");
            DataOutputStream dataOutputStream = new DataOutputStream(a2.getOutputStream());
            dataOutputStream.writeBytes(jSONObject.toString());
            dataOutputStream.flush();
            dataOutputStream.close();
            if (a2.getResponseCode() == 200) {
                InputStream inputStream2 = a2.getInputStream();
                try {
                    byteArrayOutputStream = new ByteArrayOutputStream();
                } catch (Exception e3) {
                    e = e3;
                    byteArrayOutputStream = null;
                    inputStream = inputStream2;
                    try {
                        Log.w("ChargeReporter", "upload failed. " + e);
                        a((Closeable) inputStream);
                        a((Closeable) byteArrayOutputStream);
                    } catch (Throwable th) {
                        th = th;
                        a((Closeable) inputStream);
                        a((Closeable) byteArrayOutputStream);
                        throw th;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    byteArrayOutputStream = null;
                    inputStream = inputStream2;
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
                    JSONObject jSONObject2 = new JSONObject(byteArrayOutputStream.toString());
                    if (jSONObject2.has("status")) {
                        int i = jSONObject2.getInt("status");
                        if (jSONObject2.getInt("status") == 0) {
                            Log.d("ChargeReporter", "upload success.");
                        } else {
                            Log.w("ChargeReporter", "upload failed. status = " + i);
                        }
                    }
                    inputStream = inputStream2;
                } catch (Exception e4) {
                    e = e4;
                    inputStream = inputStream2;
                    Log.w("ChargeReporter", "upload failed. " + e);
                    a((Closeable) inputStream);
                    a((Closeable) byteArrayOutputStream);
                } catch (Throwable th3) {
                    th = th3;
                    inputStream = inputStream2;
                    a((Closeable) inputStream);
                    a((Closeable) byteArrayOutputStream);
                    throw th;
                }
            } else {
                byteArrayOutputStream = null;
            }
        } catch (Exception e5) {
            e = e5;
            byteArrayOutputStream = null;
            Log.w("ChargeReporter", "upload failed. " + e);
            a((Closeable) inputStream);
            a((Closeable) byteArrayOutputStream);
        } catch (Throwable th4) {
            th = th4;
            byteArrayOutputStream = null;
            a((Closeable) inputStream);
            a((Closeable) byteArrayOutputStream);
            throw th;
        }
        a((Closeable) inputStream);
        a((Closeable) byteArrayOutputStream);
    }

    /* access modifiers changed from: private */
    public Handler b() {
        if (this.f7298a == null) {
            HandlerThread handlerThread = new HandlerThread("ChargeReporter", 10);
            handlerThread.start();
            this.f7298a = new Handler(handlerThread.getLooper());
        }
        return this.f7298a;
    }

    private b c() {
        b bVar = this.f7300c;
        if (bVar == null || bVar.a()) {
            this.f7300c = new b(this, (c) null);
        }
        return this.f7300c;
    }

    /* access modifiers changed from: private */
    public SharedPreferences d() {
        if (this.f7299b == null) {
            this.f7299b = Application.d().getSharedPreferences("pc_charge_report", 0);
        }
        return this.f7299b;
    }

    public void a(Runnable runnable) {
        if (!Build.IS_INTERNATIONAL_BUILD && h.i()) {
            this.f7301d.submit(new c(this, runnable));
        }
    }

    public void a(boolean z, int i) {
        if (!Build.IS_INTERNATIONAL_BUILD) {
            c().a(z, i);
        }
    }
}
