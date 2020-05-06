package com.miui.networkassistant.zman;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import com.miui.luckymoney.config.Constants;
import com.miui.networkassistant.utils.DeviceUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ZmanResult {
    private static final String TAG = "zman_share";
    private int mSecurityShareCloudDisable = -1;
    private int mSecurityShareTailDisable = -1;

    public ZmanResult(String str) {
        parseJson(str);
    }

    private void parseJson(String str) {
        String str2;
        try {
            JSONObject jSONObject = new JSONObject(str);
            JSONArray jSONArray = jSONObject.getJSONArray(DataSchemeDataSource.SCHEME_DATA);
            int optInt = jSONObject.optInt("total");
            for (int i = 0; i < optInt; i++) {
                if (jSONArray.getJSONObject(i).optInt("status") == 0) {
                    str2 = "status: 0";
                } else {
                    String optString = jSONArray.getJSONObject(i).optString(Constants.JSON_KEY_VERSION_TYPE, "");
                    if (!TextUtils.equals(optString, "stable")) {
                        if (!(DeviceUtil.IS_STABLE_VERSION ? "stable" : "development").equals(optString)) {
                            Log.d(TAG, "DeviceUtil.IS_STABLE_VERSION: " + DeviceUtil.IS_STABLE_VERSION);
                            str2 = "DeviceUtil.versionType:  " + optString;
                        }
                    }
                    parseProductData(jSONArray.getJSONObject(i).getJSONObject("productData"));
                    return;
                }
                Log.d(TAG, str2);
            }
        } catch (JSONException e) {
            Log.w(TAG, "parse json failed :", e);
        }
    }

    private void parseProductData(JSONObject jSONObject) {
        Log.d(TAG, "json: " + jSONObject.toString());
        try {
            this.mSecurityShareCloudDisable = jSONObject.getInt("security_share_disable");
            this.mSecurityShareTailDisable = jSONObject.getInt("share_tail_disable");
        } catch (JSONException e) {
            Log.w(TAG, "parseProductData failed :", e);
        }
    }

    public boolean isSecurityShareCloudDisable(Context context) {
        if (this.mSecurityShareCloudDisable == -1) {
            return ZmanHelper.getSecurityShareCloudDisable(context);
        }
        Log.d(TAG, "result mSecurityShareCloudDisable:" + this.mSecurityShareCloudDisable);
        return this.mSecurityShareCloudDisable == 1;
    }

    public boolean isShareTailDisable(Context context) {
        if (this.mSecurityShareTailDisable == -1) {
            return !ZmanHelper.getShareTailCloudDisable(context);
        }
        Log.d(TAG, "result mSecurityShareTailDisable:" + this.mSecurityShareTailDisable);
        return this.mSecurityShareTailDisable == 1;
    }

    public boolean isSuccess() {
        return (this.mSecurityShareCloudDisable == -1 || this.mSecurityShareTailDisable == -1) ? false : true;
    }
}
