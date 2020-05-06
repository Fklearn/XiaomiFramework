package miui.cloud.backup.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import miui.cloud.backup.SettingsBackupConsts;
import org.json.JSONException;
import org.json.JSONObject;

public class KeyJsonSettingItem extends SettingItem<JSONObject> {
    public static final Parcelable.Creator<KeyJsonSettingItem> CREATOR = new Parcelable.Creator<KeyJsonSettingItem>() {
        public KeyJsonSettingItem[] newArray(int size) {
            return new KeyJsonSettingItem[size];
        }

        public KeyJsonSettingItem createFromParcel(Parcel source) {
            KeyJsonSettingItem obj = new KeyJsonSettingItem();
            obj.fillFromParcel(source);
            return obj;
        }
    };
    public static final String TYPE = "json";

    /* access modifiers changed from: protected */
    public JSONObject stringToValue(String rawValue) {
        try {
            return new JSONObject(rawValue);
        } catch (JSONException e) {
            Log.e(SettingsBackupConsts.TAG, "JSONException occorred when stringToValue()", e);
            return null;
        }
    }

    /* access modifiers changed from: protected */
    public String valueToString(JSONObject actualValue) {
        return actualValue.toString();
    }

    /* access modifiers changed from: protected */
    public void setValueFromJson(JSONObject json) {
        setValue(json.optJSONObject("value"));
    }

    /* access modifiers changed from: protected */
    public Object getJsonValue() {
        return getValue();
    }

    /* access modifiers changed from: protected */
    public String getType() {
        return TYPE;
    }
}
