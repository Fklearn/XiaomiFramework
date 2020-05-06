package b.d.e.b;

import android.util.Log;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class e {

    /* renamed from: a  reason: collision with root package name */
    private static final String f2182a = "b.d.e.b.e";

    /* renamed from: b  reason: collision with root package name */
    protected String[] f2183b;

    /* renamed from: c  reason: collision with root package name */
    private Pattern[] f2184c;

    /* renamed from: d  reason: collision with root package name */
    protected String[] f2185d;
    private Pattern[] e;
    protected String[] f;
    private Pattern[] g;

    e(JSONObject jSONObject) {
        try {
            if (jSONObject.has("pattern")) {
                JSONArray jSONArray = jSONObject.getJSONArray("pattern");
                int length = jSONArray.length();
                this.f2183b = new String[length];
                this.f2184c = new Pattern[length];
                for (int i = 0; i < length; i++) {
                    this.f2183b[i] = jSONArray.getString(i);
                    this.f2184c[i] = Pattern.compile(this.f2183b[i], 2);
                }
            }
            if (jSONObject.has("neg_pattern")) {
                JSONArray jSONArray2 = jSONObject.getJSONArray("neg_pattern");
                int length2 = jSONArray2.length();
                this.f2185d = new String[length2];
                this.e = new Pattern[length2];
                for (int i2 = 0; i2 < length2; i2++) {
                    this.f2185d[i2] = jSONArray2.getString(i2);
                    this.e[i2] = Pattern.compile(this.f2185d[i2], 2);
                }
            }
            if (jSONObject.has("or_pattern")) {
                JSONArray jSONArray3 = jSONObject.getJSONArray("or_pattern");
                int length3 = jSONArray3.length();
                this.f = new String[length3];
                this.g = new Pattern[length3];
                for (int i3 = 0; i3 < length3; i3++) {
                    this.f[i3] = jSONArray3.getString(i3);
                    this.g[i3] = Pattern.compile(this.f[i3], 2);
                }
            }
        } catch (JSONException unused) {
            Log.e(f2182a, "JSONException when decode KEY_PATTERN features.");
        }
    }

    /* access modifiers changed from: protected */
    public boolean a(String str) {
        Pattern[] patternArr = this.f2184c;
        if (patternArr != null && patternArr.length > 0) {
            for (Pattern matcher : patternArr) {
                if (!matcher.matcher(str).find()) {
                    return false;
                }
            }
        }
        Pattern[] patternArr2 = this.e;
        if (patternArr2 != null && patternArr2.length > 0) {
            for (Pattern matcher2 : patternArr2) {
                if (matcher2.matcher(str).find()) {
                    return false;
                }
            }
        }
        Pattern[] patternArr3 = this.g;
        if (patternArr3 == null || patternArr3.length <= 0) {
            return true;
        }
        for (Pattern matcher3 : patternArr3) {
            if (matcher3.matcher(str).find()) {
                return true;
            }
        }
        return false;
    }
}
