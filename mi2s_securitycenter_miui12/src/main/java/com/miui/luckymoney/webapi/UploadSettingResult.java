package com.miui.luckymoney.webapi;

import android.text.TextUtils;
import android.util.Log;
import b.b.c.c.d;
import b.b.c.g.b;
import com.miui.luckymoney.config.CommonConfig;
import org.json.JSONException;
import org.json.JSONObject;

public class UploadSettingResult implements b {
    private static String TAG = "UploadSettingResult";
    private boolean isSuccess;
    private String mJsonStr;
    private int statusCode = 401;

    public UploadSettingResult(String str) {
        this.mJsonStr = str;
        parseJson(str);
    }

    private void doParseJson(JSONObject jSONObject) {
        this.statusCode = jSONObject.optInt("code", 401);
        if (isSuccess() && this.statusCode == 200) {
            CommonConfig.getInstance(d.a()).setConfigChanged(false);
        }
    }

    public String getJson() {
        return this.mJsonStr;
    }

    public boolean isSuccess() {
        return this.isSuccess;
    }

    public boolean parseJson(String str) {
        if (TextUtils.isEmpty(str)) {
            this.isSuccess = false;
            return false;
        }
        this.mJsonStr = str;
        JSONObject jSONObject = null;
        try {
            jSONObject = new JSONObject(str);
        } catch (JSONException e) {
            this.isSuccess = false;
            Log.d(TAG, "parse json failed :", e);
        }
        if (jSONObject == null) {
            this.isSuccess = false;
            return false;
        }
        this.isSuccess = true;
        doParseJson(jSONObject);
        return true;
    }

    public String toJson() {
        return null;
    }
}
