package b.b.c.g;

import android.text.TextUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class d implements b {
    static final String JSON_KEY_RET_CODE = "code";
    static final String JSON_KEY_RET_DESC = "desc";
    static final String JSON_KEY_RET_OLDAGE = "oldage";
    static final String RETURN_CODE_OK = "0";
    protected String mCode;
    protected String mDesc;
    protected String mJsonStr;
    protected int mOldAge;
    protected boolean mResponsed;

    public d(String str) {
        parseJson(str);
    }

    /* access modifiers changed from: protected */
    public boolean doParseJson(JSONObject jSONObject) {
        return true;
    }

    public String getDesc() {
        return this.mDesc;
    }

    public int getOldAge() {
        return this.mOldAge;
    }

    public boolean isResponsed() {
        return this.mResponsed;
    }

    public boolean isSuccess() {
        return "0".equals(this.mCode);
    }

    public boolean parseJson(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        this.mJsonStr = str;
        JSONObject jSONObject = null;
        try {
            jSONObject = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (jSONObject == null) {
            return false;
        }
        this.mResponsed = true;
        this.mCode = jSONObject.optString(JSON_KEY_RET_CODE);
        this.mDesc = jSONObject.optString(JSON_KEY_RET_DESC);
        this.mOldAge = jSONObject.optInt(JSON_KEY_RET_OLDAGE, 1);
        return doParseJson(jSONObject);
    }

    public void setDesc(String str) {
        this.mDesc = str;
    }

    public void setOldAge(int i) {
        this.mOldAge = i;
    }

    public String toJson() {
        return this.mJsonStr;
    }

    public String toString() {
        if (!isSuccess()) {
            return TextUtils.isEmpty(this.mJsonStr) ? "get data failed from server" : this.mJsonStr;
        }
        return String.format("mCode:%s,mDesc:%s", new Object[]{this.mCode, this.mDesc});
    }
}
