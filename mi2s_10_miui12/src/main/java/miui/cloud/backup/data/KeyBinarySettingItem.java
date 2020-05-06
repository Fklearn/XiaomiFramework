package miui.cloud.backup.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;
import org.json.JSONObject;

public class KeyBinarySettingItem extends SettingItem<byte[]> {
    public static final Parcelable.Creator<KeyBinarySettingItem> CREATOR = new Parcelable.Creator<KeyBinarySettingItem>() {
        public KeyBinarySettingItem[] newArray(int size) {
            return new KeyBinarySettingItem[size];
        }

        public KeyBinarySettingItem createFromParcel(Parcel source) {
            KeyBinarySettingItem obj = new KeyBinarySettingItem();
            obj.fillFromParcel(source);
            return obj;
        }
    };
    public static final String TYPE = "binary";

    /* access modifiers changed from: protected */
    public byte[] stringToValue(String rawValue) {
        return Base64.decode(rawValue, 2);
    }

    /* access modifiers changed from: protected */
    public String valueToString(byte[] actualValue) {
        return Base64.encodeToString(actualValue, 2);
    }

    /* access modifiers changed from: protected */
    public Object getJsonValue() {
        return valueToString((byte[]) getValue());
    }

    /* access modifiers changed from: protected */
    public void setValueFromJson(JSONObject json) {
        setValue(stringToValue(json.optString("value")));
    }

    /* access modifiers changed from: protected */
    public String getType() {
        return TYPE;
    }
}
