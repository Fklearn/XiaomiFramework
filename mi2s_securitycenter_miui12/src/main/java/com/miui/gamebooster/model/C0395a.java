package com.miui.gamebooster.model;

import b.b.c.h.j;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.gamebooster.d.a;
import com.miui.securityscan.i.k;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;

/* renamed from: com.miui.gamebooster.model.a  reason: case insensitive filesystem */
public class C0395a implements Serializable {

    /* renamed from: a  reason: collision with root package name */
    private List<C0396b> f4548a = new ArrayList();

    public static C0395a a(JSONObject jSONObject) {
        C0395a aVar = new C0395a();
        JSONObject optJSONObject = jSONObject.optJSONObject(DataSchemeDataSource.SCHEME_DATA);
        if (optJSONObject == null || optJSONObject.toString().length() < 10) {
            return null;
        }
        a(optJSONObject, aVar);
        return aVar;
    }

    public static String a(Map<String, String> map) {
        return k.a(map, a.e, k.a.POST, "2dcd9s0c-ad3f-2fas-0l3a-abzo301jd0s9", new j("gamebooster_appinfodatamodel_post"));
    }

    private void a(C0396b bVar) {
        if (bVar != null) {
            this.f4548a.add(bVar);
        }
    }

    private static void a(JSONObject jSONObject, C0395a aVar) {
        C0396b a2 = C0396b.a(jSONObject);
        if (a2 != null) {
            aVar.a(a2);
        }
    }

    public List<C0396b> a() {
        return this.f4548a;
    }
}
