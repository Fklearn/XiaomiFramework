package com.miui.gamebooster;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import com.miui.securitycenter.n;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private static c f4090a;
    /* access modifiers changed from: private */

    /* renamed from: b  reason: collision with root package name */
    public SharedPreferences f4091b;

    public class a {

        /* renamed from: a  reason: collision with root package name */
        SharedPreferences.Editor f4092a;

        public a() {
            this.f4092a = c.this.f4091b.edit();
        }

        public a a(List<String> list) {
            StringBuilder sb = new StringBuilder();
            for (String append : list) {
                sb.append(append);
                sb.append(",");
            }
            sb.lastIndexOf(",");
            Log.i("GBSettings", "saveRecommendAppCache: " + sb.toString());
            this.f4092a.putString("recommend_app_cache", sb.toString());
            return this;
        }

        public void a() {
            n.a().b(new b(this));
        }

        public a b(List<String> list) {
            StringBuilder sb = new StringBuilder();
            for (String append : list) {
                sb.append(append);
                sb.append("|");
            }
            sb.lastIndexOf("|");
            Log.i("GBSettings", "saveVBRecommendAppCache: " + sb.toString());
            this.f4092a.putString("recommend_app_cache_video", sb.toString());
            return this;
        }
    }

    public static class b {

        /* renamed from: a  reason: collision with root package name */
        public String f4097a;

        /* renamed from: b  reason: collision with root package name */
        public long f4098b;

        /* renamed from: c  reason: collision with root package name */
        public int f4099c;

        public b(String str, long j, int i) {
            this.f4097a = str;
            this.f4098b = j;
            this.f4099c = i;
        }

        public static b a(String str) {
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            try {
                String[] split = str.split(",");
                if (split.length < 3) {
                    return null;
                }
                return new b(split[0], Long.valueOf(split[1]).longValue(), Integer.valueOf(split[2]).intValue());
            } catch (Exception unused) {
                return null;
            }
        }

        public String toString() {
            return this.f4097a + "," + this.f4099c + "," + this.f4098b;
        }
    }

    private c(Context context) {
        this.f4091b = context.getSharedPreferences("game_box_settings", 0);
    }

    public static synchronized c a(Context context) {
        c cVar;
        synchronized (c.class) {
            if (f4090a == null) {
                f4090a = new c(context.getApplicationContext());
            }
            cVar = f4090a;
        }
        return cVar;
    }

    public a a() {
        return new a();
    }

    public List<String> b() {
        String string = this.f4091b.getString("recommend_app_cache", (String) null);
        ArrayList arrayList = new ArrayList();
        if (TextUtils.isEmpty(string)) {
            return arrayList;
        }
        Log.i("GBSettings", "getRecommendAppCache: " + string);
        arrayList.addAll(Arrays.asList(string.split(",")));
        return arrayList;
    }

    public List<b> c() {
        String string = this.f4091b.getString("recommend_app_cache_video", (String) null);
        ArrayList arrayList = new ArrayList();
        if (TextUtils.isEmpty(string)) {
            return arrayList;
        }
        Log.i("GBSettings", "getRecommendAppCache: " + string);
        String[] split = string.split("|");
        for (String a2 : split) {
            b a3 = b.a(a2);
            if (a3 != null) {
                arrayList.add(a3);
            }
        }
        return arrayList;
    }
}
