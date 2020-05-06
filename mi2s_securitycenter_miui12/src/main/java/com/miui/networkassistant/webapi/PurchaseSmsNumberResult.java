package com.miui.networkassistant.webapi;

import android.text.TextUtils;
import b.b.c.g.d;
import com.google.android.exoplayer2.upstream.DataSchemeDataSource;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PurchaseSmsNumberResult extends d {
    private ArrayList<String> mSmsNumberWhiteList;
    private String mSmsPhoneJson;

    public PurchaseSmsNumberResult(String str) {
        super(str);
    }

    /* access modifiers changed from: protected */
    public boolean doParseJson(JSONObject jSONObject) {
        super.doParseJson(jSONObject);
        if (!isSuccess()) {
            return true;
        }
        this.mSmsPhoneJson = jSONObject.toString();
        try {
            JSONArray optJSONArray = jSONObject.optJSONArray("items");
            this.mSmsNumberWhiteList = new ArrayList<>();
            if (optJSONArray == null) {
                return true;
            }
            for (int i = 0; i < optJSONArray.length(); i++) {
                String string = optJSONArray.getJSONObject(i).getString(DataSchemeDataSource.SCHEME_DATA);
                if (!TextUtils.isEmpty(string) && !this.mSmsNumberWhiteList.contains(string)) {
                    this.mSmsNumberWhiteList.add(string);
                }
            }
            return true;
        } catch (JSONException e) {
            e.printStackTrace();
            return true;
        }
    }

    public String getSmsNumberJson() {
        return this.mSmsPhoneJson;
    }

    public ArrayList<String> getSmsNumberList() {
        return this.mSmsNumberWhiteList;
    }
}
