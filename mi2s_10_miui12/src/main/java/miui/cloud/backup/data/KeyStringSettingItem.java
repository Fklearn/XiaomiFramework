package miui.cloud.backup.data;

import android.os.Parcel;
import android.os.Parcelable;
import org.json.JSONObject;

public class KeyStringSettingItem extends SettingItem<String> {
    public static final Parcelable.Creator<KeyStringSettingItem> CREATOR = new Parcelable.Creator<KeyStringSettingItem>() {
        public KeyStringSettingItem[] newArray(int size) {
            return new KeyStringSettingItem[size];
        }

        public KeyStringSettingItem createFromParcel(Parcel source) {
            KeyStringSettingItem obj = new KeyStringSettingItem();
            obj.fillFromParcel(source);
            return obj;
        }
    };
    public static final String TYPE = "string";

    /* access modifiers changed from: protected */
    public String stringToValue(String rawValue) {
        return rawValue;
    }

    /* access modifiers changed from: protected */
    public String valueToString(String actualValue) {
        return actualValue;
    }

    /* access modifiers changed from: protected */
    public Object getJsonValue() {
        return getValue();
    }

    /* access modifiers changed from: protected */
    public void setValueFromJson(JSONObject json) {
        setValue(json.optString("value"));
    }

    /* access modifiers changed from: protected */
    public String getType() {
        return TYPE;
    }
}
