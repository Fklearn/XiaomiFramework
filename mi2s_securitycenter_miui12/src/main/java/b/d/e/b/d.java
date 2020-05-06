package b.d.e.b;

import android.util.Log;
import b.d.e.b;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class d {

    /* renamed from: a  reason: collision with root package name */
    private static final String f2178a = "b.d.e.b.d";

    /* renamed from: b  reason: collision with root package name */
    private Map<String, c> f2179b;

    /* renamed from: c  reason: collision with root package name */
    private List<c> f2180c;

    /* renamed from: d  reason: collision with root package name */
    private boolean f2181d = false;

    public static int a(int i) {
        int i2 = (i & 65535) >> 4;
        int i3 = 0;
        int i4 = 12;
        while (i2 != 0) {
            if ((i2 & 1) != 0) {
                i3 += i4;
            }
            i2 >>= 1;
            i4--;
        }
        return i3;
    }

    public static int b(int i) {
        int i2 = (i & 268369920) >> 16;
        int i3 = 1;
        int i4 = 0;
        while (i2 != 0) {
            if ((i2 & 1) != 0) {
                i4 += i3;
            }
            i2 >>= 1;
            i3++;
        }
        return i4;
    }

    public int a(b bVar) {
        int i = 0;
        for (c next : this.f2180c) {
            if (!next.c() && next.a(bVar)) {
                i |= next.b();
            }
        }
        return i;
    }

    public void a(String str) {
        try {
            JSONObject jSONObject = new JSONObject(new JSONTokener(str));
            String string = jSONObject.getString("version");
            Log.d(f2178a, "Version:" + string);
            JSONArray jSONArray = jSONObject.getJSONArray("features");
            int length = jSONArray.length();
            Log.d(f2178a, "feature count:" + length);
            this.f2179b = new HashMap();
            this.f2180c = new ArrayList();
            for (int i = 0; i < length; i++) {
                c cVar = new c(this.f2179b);
                if (cVar.a(jSONArray.getJSONObject(i))) {
                    this.f2179b.put(cVar.a(), cVar);
                    this.f2180c.add(cVar);
                }
            }
            this.f2181d = true;
        } catch (JSONException e) {
            this.f2181d = false;
            Log.e(f2178a, "JSONException when load features from pattern files");
            e.printStackTrace();
        }
    }

    public boolean a() {
        return this.f2181d;
    }
}
