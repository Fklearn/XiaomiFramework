package b.b.l;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.gamebooster.m.E;
import com.miui.gamebooster.model.ActiveModel;
import com.miui.securitycenter.Application;
import com.miui.securitycenter.h;
import com.miui.securitycenter.n;
import java.text.SimpleDateFormat;
import java.util.Date;
import miui.os.Build;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class b {
    /* access modifiers changed from: private */

    /* renamed from: a  reason: collision with root package name */
    public static final String f1843a = "b";

    /* renamed from: b  reason: collision with root package name */
    private static b f1844b;

    /* renamed from: c  reason: collision with root package name */
    private final SimpleDateFormat f1845c = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private b() {
    }

    private ActiveModel a(JSONObject jSONObject) {
        if (jSONObject == null) {
            return null;
        }
        ActiveModel activeModel = new ActiveModel();
        activeModel.setId(jSONObject.optString("id"));
        activeModel.setGamePkgName(jSONObject.optString("gamePkgName"));
        activeModel.setGamePkgNameCn(jSONObject.optString("gamePkgNameCn"));
        activeModel.setPeriod(jSONObject.optString("period"));
        activeModel.setBeginTime(jSONObject.optString("beginTime"));
        activeModel.setExpireTime(jSONObject.optString("expireTime"));
        activeModel.setImgUrl(jSONObject.optString("imgUrl"));
        activeModel.setBrowserUrl(jSONObject.optString("browserUrl"));
        activeModel.setShowPoint(jSONObject.optInt("showPoint"));
        activeModel.setActivityText(jSONObject.optString("activityText"));
        activeModel.setPreReqeustTime(jSONObject.optLong("preReqeustTime"));
        activeModel.setHasBubbleShow(jSONObject.optBoolean("hasBubbleShow"));
        activeModel.setHasRedPointShow(jSONObject.optBoolean("hasRedPointShow"));
        return activeModel;
    }

    /* access modifiers changed from: private */
    public String a(ActiveModel activeModel) {
        if (activeModel == null) {
            return null;
        }
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("activityText", activeModel.getActivityText());
            jSONObject.put("gamePkgNameCn", activeModel.getGamePkgNameCn());
            jSONObject.put("gamePkgName", activeModel.getGamePkgName());
            jSONObject.put("id", activeModel.getId());
            jSONObject.put("period", activeModel.getPeriod());
            jSONObject.put("beginTime", activeModel.getBeginTime());
            jSONObject.put("expireTime", activeModel.getExpireTime());
            jSONObject.put("imgUrl", activeModel.getImgUrl());
            jSONObject.put("browserUrl", activeModel.getBrowserUrl());
            jSONObject.put("showPoint", activeModel.getShowPoint());
            jSONObject.put("preReqeustTime", activeModel.getPreReqeustTime());
            jSONObject.put("hasBubbleShow", activeModel.isHasBubbleShow());
            jSONObject.put("hasRedPointShow", activeModel.isHasRedPointShow());
            return jSONObject.toString();
        } catch (JSONException e) {
            Log.e(f1843a, "json to string error", e);
            return null;
        }
    }

    public static synchronized b b() {
        b bVar;
        synchronized (b.class) {
            if (f1844b == null) {
                f1844b = new b();
            }
            bVar = f1844b;
        }
        return bVar;
    }

    private void l(String str) {
        ActiveModel g = g(str);
        if (g != null) {
            if (!TextUtils.isEmpty(g.getId()) && System.currentTimeMillis() - m(g.getExpireTime()) > 0) {
                com.miui.common.persistence.b.b(str, "");
            }
        }
    }

    private long m(String str) {
        Date parse;
        try {
            if (TextUtils.isEmpty(str) || (parse = this.f1845c.parse(str)) == null) {
                return 0;
            }
            return parse.getTime();
        } catch (Exception e) {
            String str2 = f1843a;
            Log.e(str2, "format error " + e);
            return 0;
        }
    }

    private boolean n(String str) {
        ActiveModel g = g(str);
        if (g == null) {
            return true;
        }
        long currentTimeMillis = System.currentTimeMillis() - g.getPreReqeustTime();
        long period = g.getPeriod();
        if (period <= 0) {
            period = 3600000;
        }
        return currentTimeMillis > period;
    }

    /* access modifiers changed from: private */
    public ActiveModel o(String str) {
        JSONArray optJSONArray;
        JSONObject optJSONObject;
        JSONArray optJSONArray2;
        JSONObject optJSONObject2;
        JSONObject optJSONObject3;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            if (jSONObject.has(DataSchemeDataSource.SCHEME_DATA) && (optJSONArray = jSONObject.optJSONArray(DataSchemeDataSource.SCHEME_DATA)) != null && optJSONArray.length() > 0 && (optJSONObject = optJSONArray.optJSONObject(0)) != null && optJSONObject.has("list") && (optJSONArray2 = optJSONObject.optJSONArray("list")) != null && optJSONArray2.length() > 0 && (optJSONObject2 = optJSONArray2.optJSONObject(0)) != null && optJSONObject2.has(DataSchemeDataSource.SCHEME_DATA) && (optJSONObject3 = optJSONObject2.optJSONObject(DataSchemeDataSource.SCHEME_DATA)) != null) {
                return a(optJSONObject3);
            }
        } catch (Exception e) {
            Log.e(f1843a, "parse json error", e);
        }
        return null;
    }

    public void a(String str, boolean z) {
        if (b(str)) {
            ActiveModel g = g(str);
            if (g != null) {
                g.setHasBubbleShow(z);
            }
            com.miui.common.persistence.b.b(str, a(g));
        }
    }

    public boolean a(String str) {
        return !Build.IS_INTERNATIONAL_BUILD && E.a((Context) Application.d()) && k(str) && !TextUtils.isEmpty(f(str)) && !TextUtils.isEmpty(d(str));
    }

    public void b(String str, boolean z) {
        if (c(str)) {
            ActiveModel g = g(str);
            if (g != null) {
                g.setHasRedPointShow(z);
            }
            com.miui.common.persistence.b.b(str, a(g));
        }
    }

    public boolean b(String str) {
        ActiveModel g;
        if (!a(str) || (g = g(str)) == null) {
            return false;
        }
        int showPoint = g.getShowPoint();
        return 2 == showPoint || 3 == showPoint;
    }

    public boolean c(String str) {
        ActiveModel g;
        if (!a(str) || (g = g(str)) == null) {
            return false;
        }
        int showPoint = g.getShowPoint();
        return 1 == showPoint || 3 == showPoint;
    }

    public String d(String str) {
        ActiveModel g = g(str);
        if (g != null) {
            return g.getBrowserUrl();
        }
        return null;
    }

    public void e(String str) {
        if (!TextUtils.isEmpty(str)) {
            l(str);
            if (h.i() && n(str)) {
                n.a().b(new a(this, str));
            }
        }
    }

    public String f(String str) {
        ActiveModel g = g(str);
        if (g != null) {
            return g.getImgUrl();
        }
        return null;
    }

    public ActiveModel g(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        try {
            String a2 = com.miui.common.persistence.b.a(str, (String) null);
            if (!TextUtils.isEmpty(a2)) {
                return a(new JSONObject(a2));
            }
        } catch (Exception e) {
            Log.e(f1843a, "error proccess cache ", e);
        }
        return null;
    }

    public String h(String str) {
        ActiveModel g = g(str);
        if (g != null) {
            return g.getActivityText();
        }
        return null;
    }

    public boolean i(String str) {
        ActiveModel g = g(str);
        if (g != null) {
            return g.isHasBubbleShow();
        }
        return false;
    }

    public boolean j(String str) {
        ActiveModel g = g(str);
        if (g != null) {
            return g.isHasRedPointShow();
        }
        return false;
    }

    public boolean k(String str) {
        ActiveModel g = g(str);
        if (g == null) {
            return false;
        }
        long currentTimeMillis = System.currentTimeMillis();
        return currentTimeMillis >= m(g.getBeginTime()) && currentTimeMillis < m(g.getExpireTime());
    }
}
