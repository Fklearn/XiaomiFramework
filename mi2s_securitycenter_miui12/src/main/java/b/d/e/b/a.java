package b.d.e.b;

import android.util.Log;
import b.d.e.b;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class a extends e {
    private static final String h = "b.d.e.b.a";
    private int[] i;

    a(JSONObject jSONObject) {
        super(jSONObject);
        try {
            if (jSONObject.has("type")) {
                JSONArray jSONArray = jSONObject.getJSONArray("type");
                this.i = new int[jSONArray.length()];
                for (int i2 = 0; i2 < jSONArray.length(); i2++) {
                    this.i[i2] = jSONArray.getInt(i2);
                }
            }
        } catch (JSONException unused) {
            Log.e(h, "JSONException when decode KEY_TYPE features.");
        }
    }

    public boolean a(b bVar) {
        boolean z;
        int b2 = bVar.b();
        int[] iArr = this.i;
        if (iArr != null && iArr.length > 0) {
            int length = iArr.length;
            int i2 = 0;
            while (true) {
                if (i2 >= length) {
                    z = false;
                    break;
                } else if (iArr[i2] == b2) {
                    z = true;
                    break;
                } else {
                    i2++;
                }
            }
            if (!z) {
                return false;
            }
        }
        return super.a(bVar.a());
    }
}
