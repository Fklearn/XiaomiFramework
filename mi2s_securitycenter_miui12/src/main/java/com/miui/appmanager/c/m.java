package com.miui.appmanager.c;

import android.content.Context;
import b.b.c.h.j;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.activityutil.o;
import com.miui.appmanager.C;
import com.miui.appmanager.i;
import com.miui.luckymoney.config.Constants;
import com.miui.securitycenter.R;
import com.miui.securityscan.c.b;
import com.miui.securityscan.i.k;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miui.cloud.CloudPushConstants;
import miui.os.Build;
import org.json.JSONArray;
import org.json.JSONObject;

public class m implements Serializable {

    /* renamed from: a  reason: collision with root package name */
    private static List<k> f3652a = new ArrayList();

    /* renamed from: b  reason: collision with root package name */
    private String f3653b;

    /* renamed from: c  reason: collision with root package name */
    private String f3654c;

    /* renamed from: d  reason: collision with root package name */
    private String f3655d;
    private String e;
    private String f;
    private boolean g;
    private int h;

    public static m a(Context context, JSONObject jSONObject, Boolean bool) {
        f3652a.clear();
        m mVar = new m();
        mVar.f3653b = jSONObject.optString("channel");
        mVar.f3654c = jSONObject.optString(Constants.JSON_KEY_DATA_VERSION);
        mVar.f3655d = jSONObject.optString("layoutId");
        mVar.e = jSONObject.optString("tn");
        mVar.h = jSONObject.optInt("status");
        mVar.g = jSONObject.optBoolean("forceRefresh");
        mVar.f = jSONObject.optString("layoutFormat", "1,0");
        JSONArray optJSONArray = jSONObject.optJSONArray(DataSchemeDataSource.SCHEME_DATA);
        if (optJSONArray != null) {
            for (int i = 0; i < optJSONArray.length(); i++) {
                a((i) null, optJSONArray.getJSONObject(i), mVar.f, bool);
            }
        }
        return mVar;
    }

    public static String a(Context context, Map<String, String> map) {
        if (map == null) {
            map = new HashMap<>();
        }
        if (Build.IS_INTERNATIONAL_BUILD) {
            map.put("channel", "02-15");
        } else {
            map.put("channel", "01-15");
            map.put("landingPageUrlType", "market");
        }
        map.put("setting", new i(context).b() ? o.f2312d : "2");
        return k.a(map, b.f7626a, new j("appmanager_amdatamodel"));
    }

    private static void a(i iVar, JSONObject jSONObject, String str, Boolean bool) {
        k oVar;
        if (CloudPushConstants.XML_ITEM.equals(jSONObject.optString("rowType"))) {
            String optString = jSONObject.optString("type");
            int optInt = jSONObject.optInt("template");
            JSONObject optJSONObject = jSONObject.optJSONObject(DataSchemeDataSource.SCHEME_DATA);
            if ("001".equals(optString)) {
                if (10014 == optInt) {
                    oVar = C.a();
                    if (oVar == null) {
                        return;
                    }
                } else {
                    c cVar = new c(R.layout.app_manager_adv_horizontal, optJSONObject, str);
                    a(cVar);
                    if (iVar != null) {
                        iVar.a((k) cVar);
                        return;
                    }
                    return;
                }
            } else if ("005".equals(optString)) {
                oVar = new o();
            } else {
                return;
            }
            a(oVar);
        } else if ("card".equals(jSONObject.optString("rowType"))) {
            JSONArray jSONArray = jSONObject.getJSONArray("list");
            i iVar2 = new i(jSONObject);
            a(iVar2);
            if (jSONArray.length() > 0) {
                for (int i = 0; i < jSONArray.length(); i++) {
                    a(iVar2, jSONArray.getJSONObject(i), str, bool);
                }
            }
        }
    }

    private static void a(k kVar) {
        if (kVar != null) {
            f3652a.add(kVar);
        }
    }

    public String a() {
        return this.f3654c;
    }

    public ArrayList<k> b() {
        return new ArrayList<>(f3652a);
    }
}
