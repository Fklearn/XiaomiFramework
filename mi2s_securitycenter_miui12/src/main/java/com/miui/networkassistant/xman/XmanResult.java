package com.miui.networkassistant.xman;

import android.content.Context;
import android.util.Log;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.luckymoney.config.Constants;
import com.miui.networkassistant.utils.DeviceUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class XmanResult {
    private static final String TAG = "xman_share";
    private int xmanCloudDisable = -1;

    public XmanResult(String str) {
        parseJson(str);
    }

    private void parseJson(String str) {
        try {
            JSONObject jSONObject = new JSONObject(str);
            JSONArray jSONArray = jSONObject.getJSONArray(DataSchemeDataSource.SCHEME_DATA);
            int optInt = jSONObject.optInt("total");
            for (int i = 0; i < optInt; i++) {
                if (jSONArray.getJSONObject(i).optInt("status") == 0) {
                    Log.d(TAG, "status: 0");
                } else {
                    if ((DeviceUtil.IS_STABLE_VERSION ? "stable" : "development").equals(jSONArray.getJSONObject(i).optString(Constants.JSON_KEY_VERSION_TYPE, ""))) {
                        parseProductData(jSONArray.getJSONObject(i).getJSONObject("productData"));
                        return;
                    }
                }
            }
        } catch (JSONException e) {
            Log.d(TAG, "parse json failed :", e);
        }
    }

    public boolean isSuccess() {
        return this.xmanCloudDisable != -1;
    }

    public boolean isXmanCloudDisable(Context context) {
        int i = this.xmanCloudDisable;
        return i == -1 ? XmanHelper.getXmanCloudDisable(context) : i == 1;
    }

    /* access modifiers changed from: protected */
    public void parseProductData(JSONObject jSONObject) {
        Log.d(TAG, "json: " + jSONObject.toString());
        try {
            this.xmanCloudDisable = jSONObject.getInt("xman_cloud_disable");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
