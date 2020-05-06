package com.xiaomi.analytics.a;

import android.provider.Settings;
import android.text.TextUtils;
import b.c.a.b.f;
import com.miui.warningcenter.mijia.MijiaAlertModel;
import com.xiaomi.analytics.a.a.a;
import com.xiaomi.analytics.a.a.g;
import com.xiaomi.analytics.a.a.h;
import com.xiaomi.analytics.a.a.k;
import com.xiaomi.analytics.a.a.l;
import com.xiaomi.analytics.a.a.n;
import com.xiaomi.analytics.a.a.p;
import com.xiaomi.stat.d;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

class j implements Runnable {

    /* renamed from: a  reason: collision with root package name */
    final /* synthetic */ l f8322a;

    j(l lVar) {
        this.f8322a = lVar;
    }

    public void run() {
        int i;
        j jVar;
        String str;
        String str2;
        String str3;
        String str4;
        String a2;
        StringBuilder sb;
        String str5;
        int optInt;
        j jVar2 = this;
        String str6 = "v";
        String str7 = "UpdateManager";
        m mVar = a.f8278a;
        m e = i.a(jVar2.f8322a.f8326c).e();
        long currentTimeMillis = System.currentTimeMillis();
        int i2 = 0;
        while (true) {
            int i3 = i2 + 1;
            if (i2 < 2) {
                try {
                    String a3 = l.a();
                    String c2 = l.c();
                    String b2 = l.b(jVar2.f8322a.f8326c);
                    String b3 = l.b();
                    int b4 = k.b(jVar2.f8322a.f8326c);
                    String b5 = jVar2.f8322a.c();
                    String packageName = jVar2.f8322a.f8326c.getPackageName();
                    String e2 = l.e();
                    i = i3;
                    try {
                        String d2 = l.d();
                        try {
                            String string = Settings.Secure.getString(jVar2.f8322a.f8326c.getContentResolver(), "android_id");
                            String a4 = p.a(packageName + string);
                            StringBuilder sb2 = new StringBuilder();
                            str3 = str6;
                            try {
                                sb2.append("i=");
                                sb2.append(a4);
                                sb2.append(", orig=");
                                sb2.append(b2);
                                a.a(str7, sb2.toString());
                                StringBuilder sb3 = new StringBuilder();
                                sb3.append(d.j + mVar);
                                sb3.append("cv" + e);
                                sb3.append("d" + a3);
                                sb3.append(f.f2028a + c2);
                                if (!h.c()) {
                                    sb3.append("i" + a4);
                                }
                                sb3.append(d.V + b3);
                                sb3.append("n" + b4);
                                sb3.append("nonce" + b5);
                                sb3.append("p" + packageName);
                                sb3.append("r" + e2);
                                sb3.append("ts" + currentTimeMillis);
                                StringBuilder sb4 = new StringBuilder();
                                String str8 = str3;
                                try {
                                    sb4.append(str8);
                                    str3 = str8;
                                    str4 = d2;
                                    sb4.append(str4);
                                    sb3.append(sb4.toString());
                                    sb3.append("miui_sdkconfig_jafej!@#)(*e@!#");
                                    a2 = p.a(sb3.toString());
                                    str5 = str7;
                                } catch (Exception e3) {
                                    e = e3;
                                    str2 = str7;
                                    str = str8;
                                    jVar = this;
                                    jVar.f8322a.a(0);
                                    a.b(str2, "exception ", e);
                                    str7 = str2;
                                    str6 = str;
                                    jVar2 = jVar;
                                    i2 = i;
                                }
                                try {
                                    sb = new StringBuilder(h.c() ? "https://sdkconfig.ad.intl.xiaomi.com/api/checkupdate/lastusefulversion2?" : "https://sdkconfig.ad.xiaomi.com/api/checkupdate/lastusefulversion2?");
                                    StringBuilder sb5 = new StringBuilder();
                                    String str9 = a2;
                                    sb5.append("av=");
                                    sb5.append(mVar);
                                    sb.append(sb5.toString());
                                    sb.append("&cv=" + e);
                                    sb.append("&d=" + a3);
                                    sb.append("&f=" + c2);
                                    if (!h.c()) {
                                        sb.append("&i=" + a4);
                                    }
                                    sb.append("&m=" + b3);
                                    sb.append("&n=" + b4);
                                    sb.append("&nonce=" + b5);
                                    sb.append("&p=" + packageName);
                                    sb.append("&r=" + e2);
                                    sb.append("&ts=" + currentTimeMillis);
                                    sb.append("&v=" + str4);
                                    sb.append("&sign=" + str9);
                                    str2 = str5;
                                } catch (Exception e4) {
                                    e = e4;
                                    jVar = this;
                                    str = str3;
                                    str2 = str5;
                                    jVar.f8322a.a(0);
                                    a.b(str2, "exception ", e);
                                    str7 = str2;
                                    str6 = str;
                                    jVar2 = jVar;
                                    i2 = i;
                                }
                            } catch (Exception e5) {
                                e = e5;
                                jVar = this;
                                str2 = str7;
                                str = str3;
                                jVar.f8322a.a(0);
                                a.b(str2, "exception ", e);
                                str7 = str2;
                                str6 = str;
                                jVar2 = jVar;
                                i2 = i;
                            }
                        } catch (Exception e6) {
                            e = e6;
                            jVar = this;
                            str = str6;
                            str2 = str7;
                            jVar.f8322a.a(0);
                            a.b(str2, "exception ", e);
                            str7 = str2;
                            str6 = str;
                            jVar2 = jVar;
                            i2 = i;
                        }
                    } catch (Exception e7) {
                        e = e7;
                        jVar = jVar2;
                        str = str6;
                        str2 = str7;
                        jVar.f8322a.a(0);
                        a.b(str2, "exception ", e);
                        str7 = str2;
                        str6 = str;
                        jVar2 = jVar;
                        i2 = i;
                    }
                    try {
                        a.a(str2, sb.toString());
                        HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(sb.toString()).openConnection();
                        httpURLConnection.setRequestMethod("GET");
                        httpURLConnection.setConnectTimeout(a.f8280c);
                        httpURLConnection.connect();
                        String str10 = new String(g.a(httpURLConnection.getInputStream()));
                        a.a(str2, "result " + str10);
                        JSONObject jSONObject = new JSONObject(str10);
                        String optString = jSONObject.optString(MijiaAlertModel.KEY_URL);
                        try {
                            optInt = jSONObject.optInt("code", 0);
                            str = str3;
                        } catch (Exception e8) {
                            e = e8;
                            jVar = this;
                            str = str3;
                            jVar.f8322a.a(0);
                            a.b(str2, "exception ", e);
                            str7 = str2;
                            str6 = str;
                            jVar2 = jVar;
                            i2 = i;
                        }
                        try {
                            String optString2 = jSONObject.optString(str);
                            jVar = this;
                            try {
                                int unused = jVar.f8322a.g = jSONObject.optInt("force", 0);
                                if (!TextUtils.isEmpty(optString) && !TextUtils.isEmpty(optString2)) {
                                    m mVar2 = new m(optString2);
                                    if (h.a() || mVar2.f8330c == 0) {
                                        String unused2 = jVar.f8322a.e = jSONObject.optString("md5");
                                        String unused3 = jVar.f8322a.f8327d = optString;
                                        n.a(jVar.f8322a.j);
                                        return;
                                    }
                                    return;
                                } else if (optInt == -8) {
                                    currentTimeMillis = jVar.f8322a.b(jSONObject.optString("failMsg"));
                                    str7 = str2;
                                    str6 = str;
                                    jVar2 = jVar;
                                    i2 = i;
                                } else {
                                    return;
                                }
                            } catch (Exception e9) {
                                e = e9;
                                jVar.f8322a.a(0);
                                a.b(str2, "exception ", e);
                                str7 = str2;
                                str6 = str;
                                jVar2 = jVar;
                                i2 = i;
                            }
                        } catch (Exception e10) {
                            e = e10;
                            jVar = this;
                            jVar.f8322a.a(0);
                            a.b(str2, "exception ", e);
                            str7 = str2;
                            str6 = str;
                            jVar2 = jVar;
                            i2 = i;
                        }
                    } catch (Exception e11) {
                        e = e11;
                        jVar = this;
                        str = str3;
                        jVar.f8322a.a(0);
                        a.b(str2, "exception ", e);
                        str7 = str2;
                        str6 = str;
                        jVar2 = jVar;
                        i2 = i;
                    }
                } catch (Exception e12) {
                    e = e12;
                    jVar = jVar2;
                    str2 = str7;
                    i = i3;
                    str = str6;
                    jVar.f8322a.a(0);
                    a.b(str2, "exception ", e);
                    str7 = str2;
                    str6 = str;
                    jVar2 = jVar;
                    i2 = i;
                }
            } else {
                j jVar3 = jVar2;
                return;
            }
        }
    }
}
