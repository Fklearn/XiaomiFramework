package com.xiaomi.stat.c;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import com.xiaomi.stat.ak;
import com.xiaomi.stat.b;
import com.xiaomi.stat.d;
import com.xiaomi.stat.d.c;
import com.xiaomi.stat.d.e;
import com.xiaomi.stat.d.j;
import com.xiaomi.stat.d.k;
import com.xiaomi.stat.d.l;
import com.xiaomi.stat.d.m;
import com.xiaomi.stat.d.r;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class i {

    /* renamed from: a  reason: collision with root package name */
    private static final String f8489a = "3.0";

    /* renamed from: b  reason: collision with root package name */
    private static final String f8490b = "UploaderEngine";

    /* renamed from: c  reason: collision with root package name */
    private static final String f8491c = "code";

    /* renamed from: d  reason: collision with root package name */
    private static final String f8492d = "UTF-8";
    private static final String e = "mistat";
    private static final String f = "uploader";
    private static final String g = "3.0.16";
    private static final String h = "Android";
    private static final int i = 200;
    private static final int j = 1;
    private static final int k = -1;
    private static final int l = 3;
    private static volatile i m;
    private final byte[] n = new byte[0];
    private FileLock o;
    private FileChannel p;
    private g q;
    private a r;

    private class a extends Handler {
        public a(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what == 1) {
                i.this.g();
            }
        }
    }

    private i() {
        e();
    }

    private int a(int i2) {
        if (i2 == 1) {
            return -1;
        }
        return i2 == 3 ? 0 : 1;
    }

    public static i a() {
        if (m == null) {
            synchronized (i.class) {
                if (m == null) {
                    m = new i();
                }
            }
        }
        return m;
    }

    private String a(JSONArray jSONArray, String str) {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("id", str);
            a(str, jSONObject);
            jSONObject.put(d.I, e.d());
            jSONObject.put("rc", m.h());
            jSONObject.put(d.j, c.b());
            jSONObject.put(d.k, b.t());
            jSONObject.put(d.l, h);
            jSONObject.put(d.Z, m.a(ak.a()));
            jSONObject.put(d.m, this.q != null ? this.q.a() : 0);
            jSONObject.put(d.n, String.valueOf(r.b()));
            jSONObject.put(d.o, m.e());
            jSONObject.put(d.p, a.a(ak.b()));
            String[] o2 = b.o();
            if (o2 != null && o2.length > 0) {
                jSONObject.put(d.v, a(o2));
            }
            jSONObject.put(d.q, m.d());
            jSONObject.put("n", l.b(ak.a()));
            jSONObject.put(d.t, b.h());
            jSONObject.put(d.u, jSONArray);
        } catch (JSONException e2) {
            e2.printStackTrace();
        }
        return jSONObject.toString();
    }

    private JSONArray a(String[] strArr) {
        JSONArray jSONArray = new JSONArray();
        for (int i2 = 0; i2 < strArr.length; i2++) {
            JSONObject jSONObject = new JSONObject();
            try {
                jSONObject.put(strArr[i2], a.a(strArr[i2]));
                jSONArray.put(jSONObject);
            } catch (JSONException e2) {
                e2.printStackTrace();
            }
        }
        return jSONArray;
    }

    private void a(Message message) {
        synchronized (this.n) {
            if (this.r == null || this.q == null) {
                e();
            }
            this.r.sendMessage(message);
        }
    }

    private void a(String str, JSONObject jSONObject) {
        try {
            if (!b.e() && TextUtils.isEmpty(str)) {
                Context a2 = ak.a();
                jSONObject.put(d.C, e.b(a2));
                jSONObject.put(d.J, e.k(a2));
                jSONObject.put(d.L, e.n(a2));
                jSONObject.put(d.O, e.q(a2));
                jSONObject.put("ai", e.p(a2));
            }
        } catch (Exception unused) {
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x0080  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0088  */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x00c1  */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x00c3 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void a(com.xiaomi.stat.a.b[] r11, java.lang.String r12) {
        /*
            r10 = this;
            int r0 = r11.length
            java.lang.String r1 = "UploaderEngine"
            if (r0 != 0) goto L_0x000b
            java.lang.String r11 = "privacy policy or network state not matched"
            com.xiaomi.stat.d.k.e(r1, r11)
            return
        L_0x000b:
            com.xiaomi.stat.a.c r0 = com.xiaomi.stat.a.c.a()
            com.xiaomi.stat.a.k r0 = r0.a((com.xiaomi.stat.a.b[]) r11)
            java.util.concurrent.atomic.AtomicInteger r2 = new java.util.concurrent.atomic.AtomicInteger
            r2.<init>()
            r3 = 1
            if (r0 == 0) goto L_0x001e
            boolean r4 = r0.f8387c
            goto L_0x001f
        L_0x001e:
            r4 = r3
        L_0x001f:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            r5.append(r1)
            r5.append(r0)
            java.lang.String r5 = r5.toString()
            com.xiaomi.stat.d.k.b(r5)
            r5 = 0
            r6 = r4
            r4 = r5
        L_0x0034:
            if (r0 == 0) goto L_0x00c8
            java.util.ArrayList<java.lang.Long> r4 = r0.f8386b
            org.json.JSONArray r0 = r0.f8385a
            java.lang.String r0 = r10.a((org.json.JSONArray) r0, (java.lang.String) r12)     // Catch:{ Exception -> 0x007d }
            java.lang.String r7 = " payload:"
            com.xiaomi.stat.d.k.a((java.lang.String) r1, (java.lang.String) r7, (java.lang.String) r0)     // Catch:{ Exception -> 0x007d }
            byte[] r0 = a((java.lang.String) r0)     // Catch:{ Exception -> 0x007d }
            byte[] r0 = r10.a((byte[]) r0)     // Catch:{ Exception -> 0x007d }
            java.lang.String r0 = r10.b((byte[]) r0)     // Catch:{ Exception -> 0x007d }
            java.lang.String r7 = " encodePayload "
            com.xiaomi.stat.d.k.a((java.lang.String) r1, (java.lang.String) r7, (java.lang.String) r0)     // Catch:{ Exception -> 0x007d }
            com.xiaomi.stat.b.g r7 = com.xiaomi.stat.b.g.a()     // Catch:{ Exception -> 0x007d }
            java.lang.String r7 = r7.c()     // Catch:{ Exception -> 0x007d }
            boolean r8 = com.xiaomi.stat.d.k.b()     // Catch:{ Exception -> 0x007d }
            if (r8 == 0) goto L_0x0064
            java.lang.String r7 = "http://test.data.mistat.xiaomi.srv/mistats/v3"
        L_0x0064:
            java.util.HashMap r0 = r10.c((java.lang.String) r0)     // Catch:{ Exception -> 0x007d }
            java.lang.String r0 = com.xiaomi.stat.c.c.a((java.lang.String) r7, (java.util.Map<java.lang.String, java.lang.String>) r0, (boolean) r3)     // Catch:{ Exception -> 0x007d }
            java.lang.String r7 = " sendDataToServer response: "
            com.xiaomi.stat.d.k.a((java.lang.String) r1, (java.lang.String) r7, (java.lang.String) r0)     // Catch:{ Exception -> 0x007d }
            boolean r7 = android.text.TextUtils.isEmpty(r0)     // Catch:{ Exception -> 0x007d }
            if (r7 == 0) goto L_0x0078
            goto L_0x007d
        L_0x0078:
            boolean r0 = r10.b((java.lang.String) r0)     // Catch:{ Exception -> 0x007d }
            goto L_0x007e
        L_0x007d:
            r0 = r5
        L_0x007e:
            if (r0 == 0) goto L_0x0088
            com.xiaomi.stat.a.c r7 = com.xiaomi.stat.a.c.a()
            r7.a((java.util.ArrayList<java.lang.Long>) r4)
            goto L_0x008b
        L_0x0088:
            r2.addAndGet(r3)
        L_0x008b:
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r7 = " deleteData= "
            r4.append(r7)
            r4.append(r0)
            java.lang.String r7 = " retryCount.get()= "
            r4.append(r7)
            int r7 = r2.get()
            r4.append(r7)
            java.lang.String r4 = r4.toString()
            com.xiaomi.stat.d.k.b(r1, r4)
            if (r6 != 0) goto L_0x00c9
            if (r0 != 0) goto L_0x00b7
            int r4 = r2.get()
            r7 = 3
            if (r4 <= r7) goto L_0x00b7
            goto L_0x00c9
        L_0x00b7:
            com.xiaomi.stat.a.c r4 = com.xiaomi.stat.a.c.a()
            com.xiaomi.stat.a.k r4 = r4.a((com.xiaomi.stat.a.b[]) r11)
            if (r4 == 0) goto L_0x00c3
            boolean r6 = r4.f8387c
        L_0x00c3:
            r9 = r4
            r4 = r0
            r0 = r9
            goto L_0x0034
        L_0x00c8:
            r0 = r4
        L_0x00c9:
            com.xiaomi.stat.c.g r11 = r10.q
            if (r11 == 0) goto L_0x00d0
            r11.b(r0)
        L_0x00d0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaomi.stat.c.i.a(com.xiaomi.stat.a.b[], java.lang.String):void");
    }

    public static byte[] a(String str) {
        GZIPOutputStream gZIPOutputStream;
        ByteArrayOutputStream byteArrayOutputStream;
        byte[] bArr = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream(str.getBytes("UTF-8").length);
            try {
                gZIPOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            } catch (Exception e2) {
                e = e2;
                gZIPOutputStream = null;
                try {
                    k.e(f8490b, " zipData failed! " + e.toString());
                    j.a((OutputStream) byteArrayOutputStream);
                    j.a((OutputStream) gZIPOutputStream);
                    return bArr;
                } catch (Throwable th) {
                    th = th;
                    j.a((OutputStream) byteArrayOutputStream);
                    j.a((OutputStream) gZIPOutputStream);
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                gZIPOutputStream = null;
                j.a((OutputStream) byteArrayOutputStream);
                j.a((OutputStream) gZIPOutputStream);
                throw th;
            }
            try {
                gZIPOutputStream.write(str.getBytes("UTF-8"));
                gZIPOutputStream.finish();
                bArr = byteArrayOutputStream.toByteArray();
            } catch (Exception e3) {
                e = e3;
                k.e(f8490b, " zipData failed! " + e.toString());
                j.a((OutputStream) byteArrayOutputStream);
                j.a((OutputStream) gZIPOutputStream);
                return bArr;
            }
        } catch (Exception e4) {
            e = e4;
            byteArrayOutputStream = null;
            gZIPOutputStream = null;
            k.e(f8490b, " zipData failed! " + e.toString());
            j.a((OutputStream) byteArrayOutputStream);
            j.a((OutputStream) gZIPOutputStream);
            return bArr;
        } catch (Throwable th3) {
            th = th3;
            byteArrayOutputStream = null;
            gZIPOutputStream = null;
            j.a((OutputStream) byteArrayOutputStream);
            j.a((OutputStream) gZIPOutputStream);
            throw th;
        }
        j.a((OutputStream) byteArrayOutputStream);
        j.a((OutputStream) gZIPOutputStream);
        return bArr;
    }

    private byte[] a(byte[] bArr) {
        return com.xiaomi.stat.b.i.a().a(bArr);
    }

    private String b(byte[] bArr) {
        return com.xiaomi.stat.d.d.a(bArr);
    }

    private void b(boolean z) {
        a(c(z), com.xiaomi.stat.b.d.a().a(z));
    }

    private boolean b(String str) {
        try {
            int optInt = new JSONObject(str).optInt(f8491c);
            if (optInt != i) {
                if (!(optInt == 1002 || optInt == 1004 || optInt == 1005 || optInt == 1006 || optInt == 1007)) {
                    if (optInt != 1011) {
                        if (optInt == 2002 || optInt == 1012) {
                            com.xiaomi.stat.b.i.a().a(true);
                            com.xiaomi.stat.b.d.a().b();
                        }
                    }
                }
                com.xiaomi.stat.b.i.a().a(true);
                com.xiaomi.stat.b.d.a().b();
                return false;
            }
            return true;
        } catch (Exception e2) {
            k.d(f8490b, "parseUploadingResult exception ", e2);
            return false;
        }
    }

    private HashMap<String, String> c(String str) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("ai", ak.b());
        hashMap.put(d.f8495b, "3.0.16");
        hashMap.put(d.f8496c, f8489a);
        hashMap.put(d.f8497d, m.g());
        hashMap.put("p", str);
        hashMap.put(d.ak, com.xiaomi.stat.b.i.a().c());
        hashMap.put(d.g, com.xiaomi.stat.b.i.a().b());
        return hashMap;
    }

    private com.xiaomi.stat.a.b[] c(boolean z) {
        ArrayList<String> h2 = h();
        int size = h2.size();
        ArrayList arrayList = new ArrayList();
        for (int i2 = 0; i2 < size; i2++) {
            String str = h2.get(i2);
            int a2 = a(new f(str, z).a());
            if (a2 != -1) {
                arrayList.add(new com.xiaomi.stat.a.b(str, a2, z));
            }
        }
        com.xiaomi.stat.a.b d2 = d(z);
        if (d2 != null) {
            arrayList.add(d2);
        }
        return (com.xiaomi.stat.a.b[]) arrayList.toArray(new com.xiaomi.stat.a.b[arrayList.size()]);
    }

    private com.xiaomi.stat.a.b d(boolean z) {
        int a2 = new f(z).a();
        k.b(f8490b, " createMainAppFilter: " + a2);
        int a3 = a(a2);
        if (a3 != -1) {
            return new com.xiaomi.stat.a.b((String) null, a3, z);
        }
        return null;
    }

    private void e() {
        HandlerThread handlerThread = new HandlerThread("mi_analytics_uploader_worker");
        handlerThread.start();
        this.r = new a(handlerThread.getLooper());
        this.q = new g(handlerThread.getLooper());
    }

    private void f() {
        g gVar = this.q;
        if (gVar != null) {
            gVar.c();
        }
    }

    /* access modifiers changed from: private */
    public void g() {
        if (i()) {
            if (b.e()) {
                b(true);
                b(false);
            } else {
                a(c(false), com.xiaomi.stat.b.d.a().c());
            }
            j();
        }
    }

    private ArrayList<String> h() {
        String[] o2 = b.o();
        int length = o2 != null ? o2.length : 0;
        ArrayList<String> arrayList = new ArrayList<>();
        for (int i2 = 0; i2 < length; i2++) {
            if (!TextUtils.isEmpty(o2[i2])) {
                arrayList.add(o2[i2]);
            }
        }
        return arrayList;
    }

    private boolean i() {
        File file = new File(ak.a().getFilesDir(), e);
        if (!file.exists()) {
            file.mkdir();
        }
        try {
            this.p = new FileOutputStream(new File(file, f)).getChannel();
            try {
                this.o = this.p.tryLock();
                if (this.o != null) {
                    k.c(f8490b, " acquire lock for uploader");
                    if (this.o == null) {
                        try {
                            this.p.close();
                            this.p = null;
                        } catch (Exception unused) {
                        }
                    }
                    return true;
                }
                k.c(f8490b, " acquire lock for uploader failed");
                if (this.o == null) {
                    try {
                        this.p.close();
                        this.p = null;
                    } catch (Exception unused2) {
                    }
                }
                return false;
            } catch (Exception e2) {
                k.c(f8490b, " acquire lock for uploader failed with " + e2);
                if (this.o == null) {
                    try {
                        this.p.close();
                        this.p = null;
                    } catch (Exception unused3) {
                    }
                }
                return false;
            } catch (Throwable th) {
                if (this.o == null) {
                    try {
                        this.p.close();
                        this.p = null;
                    } catch (Exception unused4) {
                    }
                }
                throw th;
            }
        } catch (FileNotFoundException e3) {
            k.c(f8490b, " acquire lock for uploader failed with " + e3);
            return false;
        }
    }

    private void j() {
        try {
            if (this.o != null) {
                this.o.release();
                this.o = null;
            }
            if (this.p != null) {
                this.p.close();
                this.p = null;
            }
            k.c(f8490b, " releaseLock lock for uploader");
        } catch (IOException e2) {
            k.c(f8490b, " releaseLock lock for uploader failed with " + e2);
        }
    }

    public void a(boolean z) {
        g gVar = this.q;
        if (gVar != null) {
            gVar.a(z);
        }
    }

    public void b() {
        this.q.b();
        c();
    }

    public void c() {
        if (!l.a()) {
            f();
        } else if (!b.a() || !b.b()) {
            k.b(f8490b, " postToServer statistic disable or network disable access! ");
        } else if (!b.B()) {
            k.b(f8490b, " postToServer can not upload data because of configuration!");
        } else {
            Message obtain = Message.obtain();
            obtain.what = 1;
            a(obtain);
        }
    }

    public synchronized void d() {
        if (this.q != null) {
            this.q.d();
        }
    }
}
