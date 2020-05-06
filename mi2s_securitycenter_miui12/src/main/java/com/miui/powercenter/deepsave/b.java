package com.miui.powercenter.deepsave;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.exoplayer2.C;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private String f7046a = "";

    /* renamed from: b  reason: collision with root package name */
    private Context f7047b;

    public b(Context context) {
        this.f7047b = context;
        this.f7046a = Environment.getExternalStorageDirectory() + "/Android/data/" + context.getPackageName() + "/pc/";
        File file = new File(this.f7046a);
        if (!file.mkdirs() && !file.isDirectory()) {
            file.delete();
            if (!file.mkdirs()) {
                Log.e("IdeaCacheManager", "Create dir failed " + this.f7046a);
            }
        }
    }

    private void a(long j) {
        com.miui.common.persistence.b.b("key_pref_battery_save_last_cache_time", j);
    }

    private String b() {
        return com.miui.common.persistence.b.a("key_pref_installed_app_digest", "");
    }

    /* JADX WARNING: Removed duplicated region for block: B:18:0x0028 A[SYNTHETIC, Splitter:B:18:0x0028] */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x002f A[SYNTHETIC, Splitter:B:24:0x002f] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean b(java.lang.String r4, java.lang.String r5) {
        /*
            r3 = this;
            r0 = 0
            java.io.BufferedWriter r1 = new java.io.BufferedWriter     // Catch:{ IOException -> 0x001e }
            java.io.FileWriter r2 = new java.io.FileWriter     // Catch:{ IOException -> 0x001e }
            r2.<init>(r4)     // Catch:{ IOException -> 0x001e }
            r1.<init>(r2)     // Catch:{ IOException -> 0x001e }
            r1.write(r5)     // Catch:{ IOException -> 0x0019, all -> 0x0016 }
            r1.flush()     // Catch:{ IOException -> 0x0019, all -> 0x0016 }
            r4 = 1
            r1.close()     // Catch:{ IOException -> 0x002c }
            goto L_0x002c
        L_0x0016:
            r4 = move-exception
            r0 = r1
            goto L_0x002d
        L_0x0019:
            r4 = move-exception
            r0 = r1
            goto L_0x001f
        L_0x001c:
            r4 = move-exception
            goto L_0x002d
        L_0x001e:
            r4 = move-exception
        L_0x001f:
            java.lang.String r5 = "IdeaCacheManager"
            java.lang.String r1 = ""
            android.util.Log.e(r5, r1, r4)     // Catch:{ all -> 0x001c }
            if (r0 == 0) goto L_0x002b
            r0.close()     // Catch:{ IOException -> 0x002b }
        L_0x002b:
            r4 = 0
        L_0x002c:
            return r4
        L_0x002d:
            if (r0 == 0) goto L_0x0032
            r0.close()     // Catch:{ IOException -> 0x0032 }
        L_0x0032:
            throw r4
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.deepsave.b.b(java.lang.String, java.lang.String):boolean");
    }

    private byte[] b(String str) {
        try {
            return str.getBytes(C.UTF8_NAME);
        } catch (UnsupportedEncodingException unused) {
            return str.getBytes();
        }
    }

    private long c() {
        return com.miui.common.persistence.b.a("key_pref_battery_save_last_cache_time", 0);
    }

    private String c(String str) {
        try {
            MessageDigest instance = MessageDigest.getInstance("MD5");
            instance.update(b(str));
            return String.format("%1$032X", new Object[]{new BigInteger(1, instance.digest())});
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String d() {
        return d(this.f7046a + "idea");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0031, code lost:
        if (r3 != null) goto L_0x0033;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:?, code lost:
        r3.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x003b, code lost:
        if (r3 != null) goto L_0x0033;
     */
    /* JADX WARNING: Removed duplicated region for block: B:30:0x0045 A[SYNTHETIC, Splitter:B:30:0x0045] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:23:0x0038=Splitter:B:23:0x0038, B:17:0x002e=Splitter:B:17:0x002e} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String d(java.lang.String r7) {
        /*
            r6 = this;
            java.lang.String r0 = ""
            java.lang.String r1 = "IdeaCacheManager"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r3 = 0
            java.io.BufferedReader r4 = new java.io.BufferedReader     // Catch:{ FileNotFoundException -> 0x0037, IOException -> 0x002d }
            java.io.FileReader r5 = new java.io.FileReader     // Catch:{ FileNotFoundException -> 0x0037, IOException -> 0x002d }
            r5.<init>(r7)     // Catch:{ FileNotFoundException -> 0x0037, IOException -> 0x002d }
            r4.<init>(r5)     // Catch:{ FileNotFoundException -> 0x0037, IOException -> 0x002d }
        L_0x0014:
            java.lang.String r7 = r4.readLine()     // Catch:{ FileNotFoundException -> 0x0028, IOException -> 0x0025, all -> 0x0022 }
            if (r7 == 0) goto L_0x001e
            r2.append(r7)     // Catch:{ FileNotFoundException -> 0x0028, IOException -> 0x0025, all -> 0x0022 }
            goto L_0x0014
        L_0x001e:
            r4.close()     // Catch:{ IOException -> 0x003e }
            goto L_0x003e
        L_0x0022:
            r7 = move-exception
            r3 = r4
            goto L_0x0043
        L_0x0025:
            r7 = move-exception
            r3 = r4
            goto L_0x002e
        L_0x0028:
            r7 = move-exception
            r3 = r4
            goto L_0x0038
        L_0x002b:
            r7 = move-exception
            goto L_0x0043
        L_0x002d:
            r7 = move-exception
        L_0x002e:
            android.util.Log.e(r1, r0, r7)     // Catch:{ all -> 0x002b }
            if (r3 == 0) goto L_0x003e
        L_0x0033:
            r3.close()     // Catch:{ IOException -> 0x003e }
            goto L_0x003e
        L_0x0037:
            r7 = move-exception
        L_0x0038:
            android.util.Log.e(r1, r0, r7)     // Catch:{ all -> 0x002b }
            if (r3 == 0) goto L_0x003e
            goto L_0x0033
        L_0x003e:
            java.lang.String r7 = r2.toString()
            return r7
        L_0x0043:
            if (r3 == 0) goto L_0x0048
            r3.close()     // Catch:{ IOException -> 0x0048 }
        L_0x0048:
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.powercenter.deepsave.b.d(java.lang.String):java.lang.String");
    }

    private void e(String str) {
        com.miui.common.persistence.b.b("key_pref_installed_app_digest", str);
    }

    public String a() {
        List<PackageInfo> a2 = b.b.c.b.b.a(this.f7047b).a();
        ArrayList arrayList = new ArrayList();
        for (PackageInfo packageInfo : a2) {
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            if ((applicationInfo.flags & 1) == 0) {
                arrayList.add(applicationInfo.packageName);
            }
        }
        Collections.sort(arrayList);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arrayList.size(); i++) {
            sb.append((String) arrayList.get(i));
            if (i != arrayList.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public String a(String str) {
        String b2 = b();
        if (TextUtils.isEmpty(b2)) {
            return "";
        }
        return (!b2.equals(c(str)) || System.currentTimeMillis() - c() >= 43200000) ? "" : d();
    }

    public boolean a(String str, String str2) {
        String str3 = this.f7046a + "idea";
        new File(str3).delete();
        if (!b(str3, str2)) {
            return true;
        }
        e(c(str));
        a(System.currentTimeMillis());
        return true;
    }
}
