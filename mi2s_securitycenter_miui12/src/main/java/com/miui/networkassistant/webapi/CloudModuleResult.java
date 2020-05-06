package com.miui.networkassistant.webapi;

import android.util.Log;
import b.b.c.g.d;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import org.json.JSONException;
import org.json.JSONObject;

public class CloudModuleResult extends d {
    private static final String TAG = "CloudModuleResult";
    private JSONObject mContentJson;

    public CloudModuleResult(String str) {
        super(str);
    }

    /* access modifiers changed from: protected */
    public boolean doParseJson(JSONObject jSONObject) {
        try {
            this.mContentJson = jSONObject;
            this.mContentJson = jSONObject.getJSONArray(DataSchemeDataSource.SCHEME_DATA).getJSONObject(0).getJSONObject("productData");
            return true;
        } catch (JSONException e) {
            Log.d(TAG, "parse json failed :", e);
            return true;
        }
    }

    public JSONObject getContentJson() {
        return this.mContentJson;
    }
}
