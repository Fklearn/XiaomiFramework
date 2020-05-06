package b.b.n;

import android.content.Context;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.miui.systemAdSolution.common.AdInfo;
import com.miui.systemAdSolution.common.Material;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class g {

    /* renamed from: a  reason: collision with root package name */
    private final Map<Long, a> f1857a = new ArrayMap();

    /* renamed from: b  reason: collision with root package name */
    private int f1858b;

    /* renamed from: c  reason: collision with root package name */
    private int f1859c;

    /* renamed from: d  reason: collision with root package name */
    private int f1860d;
    private boolean e;
    private boolean f;
    private int g;
    private String h;
    private long i;
    private final String j = "ModuleResources";

    public static class a {

        /* renamed from: a  reason: collision with root package name */
        private String f1861a;

        /* renamed from: b  reason: collision with root package name */
        private boolean f1862b;

        /* renamed from: c  reason: collision with root package name */
        private boolean f1863c;

        /* renamed from: d  reason: collision with root package name */
        private String f1864d;
        private String e;
        private String f;

        private a() {
        }

        public String a() {
            return this.e;
        }

        public void a(String str) {
            this.e = str;
        }

        public void a(boolean z) {
            this.f1863c = z;
        }

        public String b() {
            return this.f1864d;
        }

        public void b(String str) {
            this.f1864d = str;
        }

        public void b(boolean z) {
            this.f1862b = z;
        }

        public String c() {
            return this.f1861a;
        }

        public void c(String str) {
            this.f1861a = str;
        }

        public String d() {
            return this.f;
        }

        public void d(String str) {
            this.f = str;
        }

        public boolean e() {
            return this.f1863c;
        }
    }

    public g(Context context, AdInfo adInfo, String str) {
        if (!TextUtils.isEmpty(str) && l.f1874c.get(str).equals(adInfo.getTagId())) {
            String extra = adInfo.getExtra();
            this.i = adInfo.getId();
            if (!TextUtils.isEmpty(extra)) {
                try {
                    JSONObject jSONObject = new JSONObject(extra);
                    this.f1858b = jSONObject.optInt("attribute");
                    this.e = jSONObject.optBoolean("isLight");
                    this.h = jSONObject.optString("securityscanText");
                    this.f1859c = jSONObject.optInt("frequency");
                    this.f1860d = jSONObject.optInt("totalDisplayTimesOneDay");
                    this.f = jSONObject.optBoolean("applyFeature");
                    this.g = jSONObject.optInt("displayRedPoint");
                } catch (JSONException e2) {
                    Log.w("ModuleResources", "ModuleResource transform exception", e2);
                }
            }
            Material a2 = l.a(adInfo);
            if (a2 != null && a2.getResources() != null) {
                for (Material.Resource next : a2.getResources()) {
                    a aVar = new a();
                    Material.Resource.Deeplink deeplink = next.getDeeplink();
                    String landingPageUrl = next.getLandingPageUrl();
                    if (deeplink != null) {
                        aVar.d(deeplink.getPackageName());
                        aVar.a(deeplink.getDeeplinkUrl());
                    }
                    aVar.c(l.a(context, str, adInfo, a2, next));
                    if ("9.png".equalsIgnoreCase(next.getExtra())) {
                        aVar.b(true);
                    } else if ("gif".equalsIgnoreCase(next.getExtra())) {
                        aVar.a(true);
                    }
                    aVar.b(landingPageUrl);
                    this.f1857a.put(Long.valueOf(next.getId()), aVar);
                }
            }
        }
    }

    public int a() {
        return this.f1858b;
    }

    public long b() {
        return this.i;
    }

    public int c() {
        return this.f1859c;
    }

    public Map<Long, a> d() {
        return this.f1857a;
    }

    public String e() {
        return this.h;
    }

    public int f() {
        return this.f1857a.size();
    }

    public int g() {
        return this.f1860d;
    }

    public boolean h() {
        return this.f;
    }

    public boolean i() {
        return this.g == 1;
    }

    public boolean j() {
        return this.e;
    }
}
