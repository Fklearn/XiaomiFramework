package b.b.b.d;

import android.util.Log;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class f {
    public static List<b> a(JSONArray jSONArray) {
        String str;
        if (jSONArray == null) {
            str = "[AppVerifyInfo] Json : root is null";
        } else {
            ArrayList arrayList = new ArrayList();
            int i = 0;
            while (i < jSONArray.length()) {
                try {
                    b bVar = new b();
                    bVar.k = ((JSONObject) jSONArray.get(i)).optString("packageName");
                    bVar.f = ((JSONObject) jSONArray.get(i)).optLong("nonce");
                    bVar.g = ((JSONObject) jSONArray.get(i)).optLong("timeStamp");
                    bVar.f1504a = ((JSONObject) jSONArray.get(i)).optInt("status", 3);
                    bVar.j = ((JSONObject) jSONArray.get(i)).optString("appId");
                    bVar.f1505b = ((JSONObject) jSONArray.get(i)).optString("versionName");
                    bVar.f1506c = ((JSONObject) jSONArray.get(i)).optInt("versionCode");
                    bVar.e = ((JSONObject) jSONArray.get(i)).optString("updateLog");
                    bVar.f1507d = ((JSONObject) jSONArray.get(i)).optLong("updateTime");
                    arrayList.add(bVar);
                    i++;
                } catch (JSONException e) {
                    str = "[ApkVerifyInfo] parse failed : " + e.toString();
                }
            }
            return arrayList;
        }
        Log.e("PaySafety-DataParser", str);
        return null;
    }
}
