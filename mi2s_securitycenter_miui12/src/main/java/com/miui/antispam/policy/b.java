package com.miui.antispam.policy;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import b.b.a.e.c;
import b.b.a.e.p;
import b.b.c.h.j;
import com.google.android.exoplayer2.C;
import com.miui.antispam.db.d;
import com.miui.antispam.policy.a.e;
import com.miui.antispam.policy.a.i;
import com.miui.securityscan.i.k;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import miui.os.Build;
import org.json.JSONException;
import org.json.JSONObject;

public class b {

    /* renamed from: a  reason: collision with root package name */
    private Context f2385a;

    public class a {

        /* renamed from: a  reason: collision with root package name */
        public boolean f2386a;

        /* renamed from: b  reason: collision with root package name */
        public int f2387b;

        public a(boolean z, int i) {
            this.f2386a = z;
            this.f2387b = i;
        }
    }

    public b(Context context) {
        this.f2385a = context;
    }

    private int a(int i) {
        return i | 128;
    }

    private int a(String str) {
        String str2;
        try {
            str2 = Base64.encodeToString(str.getBytes(C.UTF8_NAME), 2);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            str2 = null;
        }
        if (TextUtils.isEmpty(str2)) {
            return 0;
        }
        try {
            int i = new JSONObject(k.a("https://security.browser.miui.com/phish?q=" + str2 + "&version=1", (Map<String, String>) null, new j("antispam_urlfilterpolicy"))).getInt("phish");
            if (-1 == i) {
                return 0;
            }
            if (i == 0) {
                return 2;
            }
            return (1 == i || 2 == i) ? 1 : 0;
        } catch (JSONException e2) {
            Log.e("URLFilterPolicy", "JSONException when resolve result string :", e2);
            return 0;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x0051  */
    /* JADX WARNING: Removed duplicated region for block: B:27:0x005b  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String a(java.lang.String r8, b.b.c.h.j r9) {
        /*
            r7 = this;
            r0 = 0
            r1 = 0
            r2 = -1
            java.net.URL r3 = new java.net.URL     // Catch:{ Exception -> 0x0041, all -> 0x003e }
            r3.<init>(r8)     // Catch:{ Exception -> 0x0041, all -> 0x003e }
            java.net.URLConnection r3 = r3.openConnection()     // Catch:{ Exception -> 0x0041, all -> 0x003e }
            java.net.HttpURLConnection r3 = (java.net.HttpURLConnection) r3     // Catch:{ Exception -> 0x0041, all -> 0x003e }
            java.lang.String r1 = "GET"
            r3.setRequestMethod(r1)     // Catch:{ Exception -> 0x003c }
            r1 = 5000(0x1388, float:7.006E-42)
            r3.setConnectTimeout(r1)     // Catch:{ Exception -> 0x003c }
            r3.setReadTimeout(r1)     // Catch:{ Exception -> 0x003c }
            r1 = 1
            r3.setInstanceFollowRedirects(r1)     // Catch:{ Exception -> 0x003c }
            int r2 = r3.getResponseCode()     // Catch:{ Exception -> 0x003c }
            java.lang.String r1 = "Location"
            java.lang.String r1 = r3.getHeaderField(r1)     // Catch:{ Exception -> 0x003c }
            if (r1 != 0) goto L_0x0033
            java.net.URL r1 = r3.getURL()     // Catch:{ Exception -> 0x003c }
            java.lang.String r1 = r1.toString()     // Catch:{ Exception -> 0x003c }
        L_0x0033:
            b.b.c.h.i.a(r9, r2, r0)
            if (r3 == 0) goto L_0x003b
            r3.disconnect()
        L_0x003b:
            return r1
        L_0x003c:
            r1 = move-exception
            goto L_0x0045
        L_0x003e:
            r8 = move-exception
            r3 = r1
            goto L_0x0056
        L_0x0041:
            r3 = move-exception
            r6 = r3
            r3 = r1
            r1 = r6
        L_0x0045:
            java.lang.String r4 = "URLFilterPolicy"
            java.lang.String r5 = "Exception when get redirect url :"
            android.util.Log.e(r4, r5, r1)     // Catch:{ all -> 0x0055 }
            b.b.c.h.i.a(r9, r2, r0)
            if (r3 == 0) goto L_0x0054
            r3.disconnect()
        L_0x0054:
            return r8
        L_0x0055:
            r8 = move-exception
        L_0x0056:
            b.b.c.h.i.a(r9, r2, r0)
            if (r3 == 0) goto L_0x005e
            r3.disconnect()
        L_0x005e:
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.antispam.policy.b.a(java.lang.String, b.b.c.h.j):java.lang.String");
    }

    public int a(String str, String str2) {
        try {
            b.b.a.a.a.e();
            boolean a2 = p.a(this.f2385a, str);
            String a3 = a(str2, new j("antispam_urlfilterpolicy"));
            int a4 = new i(this.f2385a).a(str2);
            if (a4 != 1) {
                Log.e("URLFilterPolicy", "check by browser!");
                a4 = a(a3);
            }
            Log.i("URLFilterPolicy", "AVL black url check done : url = " + a3 + "; result = " + a4);
            if (!a2) {
                if (a4 == 1) {
                    return 2;
                }
                return a4 == 2 ? 0 : -1;
            } else if (a4 == 1) {
                return 2;
            } else {
                String a5 = p.a(str2);
                ArrayList<String> a6 = p.a(this.f2385a);
                Log.i("URLFilterPolicy", "URL WhiteList Check done ");
                if (a6.contains(a5)) {
                    return 0;
                }
                if (!d.d()) {
                    Log.i("URLFilterPolicy", "SecurityCenter is not allowed to access internet, check failed!");
                    return -1;
                }
                String a7 = p.a(a3);
                if (a5 != null && !a5.equals(a7)) {
                    b.b.a.a.a.d();
                }
                return a6.contains(a7) ? 0 : 1;
            }
        } catch (Exception e) {
            Log.e("URLFilterPolicy", "exception when get URL Scan Result : ", e);
            return -1;
        }
    }

    public a a(int i, e eVar) {
        String next;
        if (Build.IS_INTERNATIONAL_BUILD || !c.b(this.f2385a, eVar.f2370c) || i == 8) {
            return null;
        }
        try {
            ArrayList<String> b2 = p.b(eVar.e);
            if (b2.isEmpty()) {
                return null;
            }
            if (!p.a(this.f2385a, eVar.f2369b)) {
                return new a(true, a(i));
            }
            ArrayList arrayList = new ArrayList();
            Iterator<String> it = b2.iterator();
            while (it.hasNext()) {
                arrayList.add(p.a(it.next()));
            }
            Log.i("URLFilterPolicy", "urls : " + b2.toString());
            Log.i("URLFilterPolicy", "mainUrls : " + arrayList.toString());
            i iVar = new i(this.f2385a);
            Iterator<String> it2 = b2.iterator();
            do {
                if (it2.hasNext()) {
                    next = it2.next();
                } else if (p.c(eVar.e)) {
                    b.b.a.a.a.e("number");
                    Log.i("URLFilterPolicy", "url marked by phoneNumber in text");
                    return new a(true, a(i));
                } else {
                    ArrayList<String> a2 = p.a(this.f2385a);
                    Iterator it3 = arrayList.iterator();
                    while (it3.hasNext()) {
                        String str = (String) it3.next();
                        if (!a2.contains(str)) {
                            b.b.a.a.a.e(b2.size() == 1 ? "non_white_black_single_url" : "non_white_black_multi_url");
                            b.b.a.a.a.f(eVar.f2369b + "; " + str);
                            Log.i("URLFilterPolicy", "url marked by risky url in text");
                            return new a(true, a(i));
                        }
                    }
                    b.b.a.a.a.e("white");
                    return null;
                }
            } while (iVar.a(next) != 1);
            b.b.a.a.a.e("black");
            b.b.a.a.a.d(eVar.f2369b + "; " + next);
            if (i < 3) {
                return new a(true, 8);
            }
            Log.i("URLFilterPolicy", "url marked by AVL");
            return new a(true, a(i));
        } catch (Exception e) {
            Log.e("URLFilterPolicy", "Exception when check message urls ! ", e);
        }
    }
}
