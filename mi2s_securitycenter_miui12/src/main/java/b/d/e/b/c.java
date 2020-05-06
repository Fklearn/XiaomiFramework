package b.d.e.b;

import android.util.Log;
import b.d.e.b;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import miui.cloud.CloudPushConstants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class c {

    /* renamed from: a  reason: collision with root package name */
    private static final String f2174a = "b.d.e.b.c";

    /* renamed from: b  reason: collision with root package name */
    private a f2175b;

    /* renamed from: c  reason: collision with root package name */
    private b f2176c;

    /* renamed from: d  reason: collision with root package name */
    private int f2177d;
    private String e;
    private Map<String, c> f;
    private List<String[]> g;
    private boolean h = false;

    public c(Map<String, c> map) {
        this.f = map;
    }

    public String a() {
        return this.e;
    }

    public boolean a(b bVar) {
        boolean z;
        a aVar = this.f2175b;
        if (aVar != null && !aVar.a(bVar)) {
            return false;
        }
        b bVar2 = this.f2176c;
        if (bVar2 != null && !bVar2.a(bVar)) {
            return false;
        }
        List<String[]> list = this.g;
        if (list == null) {
            return true;
        }
        for (String[] next : list) {
            int length = next.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    z = true;
                    continue;
                    break;
                }
                if (!this.f.get(next[i]).a(bVar)) {
                    z = false;
                    continue;
                    break;
                }
                i++;
            }
            if (z) {
                return true;
            }
        }
        return false;
    }

    public boolean a(JSONObject jSONObject) {
        if (!jSONObject.has("sms_type") && !jSONObject.has("assist")) {
            return false;
        }
        try {
            if (jSONObject.has("sms_type")) {
                this.f2177d = jSONObject.getInt("sms_type");
            }
            if (jSONObject.has("assist") && jSONObject.getBoolean("assist")) {
                this.h = true;
            }
            if (jSONObject.has(CloudPushConstants.XML_NAME)) {
                this.e = jSONObject.getString(CloudPushConstants.XML_NAME);
            }
            if (jSONObject.has("address")) {
                this.f2175b = new a(jSONObject.getJSONObject("address"));
            }
            if (jSONObject.has(TtmlNode.TAG_BODY)) {
                this.f2176c = new b(jSONObject.getJSONObject(TtmlNode.TAG_BODY));
            }
            if (jSONObject.has("combi")) {
                JSONArray jSONArray = jSONObject.getJSONArray("combi");
                int length = jSONArray.length();
                this.g = new ArrayList();
                for (int i = 0; i < length; i++) {
                    this.g.add(jSONArray.getString(i).split("&"));
                }
            }
            return (this.f2175b == null && this.f2176c == null && this.g == null) ? false : true;
        } catch (JSONException unused) {
            Log.e(f2174a, "JSONException when decode features.");
            return false;
        }
    }

    public int b() {
        return this.f2177d;
    }

    public boolean c() {
        return this.h;
    }
}
