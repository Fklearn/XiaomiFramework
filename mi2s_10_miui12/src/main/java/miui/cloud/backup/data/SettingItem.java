package miui.cloud.backup.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class SettingItem<T> implements Parcelable, Comparable<SettingItem<?>> {
    private static final String KEY_KEY = "key";
    private static final String KEY_TYPE = "type";
    protected static final String KEY_VALUE = "value";
    protected static final String TAG = "SettingsBackup";
    public String key;
    private T value;

    /* access modifiers changed from: protected */
    public abstract Object getJsonValue();

    /* access modifiers changed from: protected */
    public abstract String getType();

    /* access modifiers changed from: protected */
    public abstract void setValueFromJson(JSONObject jSONObject);

    /* access modifiers changed from: protected */
    public abstract T stringToValue(String str);

    /* access modifiers changed from: protected */
    public abstract String valueToString(T t);

    public T getValue() {
        return this.value;
    }

    public void setValue(T value2) {
        this.value = value2;
    }

    public static SettingItem<?> fromJson(JSONObject json) {
        if (json != null) {
            SettingItem<?> obj = createByType(json.optString("type"));
            if (obj == null) {
                return null;
            }
            obj.key = json.optString(KEY_KEY);
            obj.setValueFromJson(json);
            return obj;
        }
        throw new IllegalArgumentException("json cannot be null");
    }

    private static SettingItem<?> createByType(String type) {
        if (KeyStringSettingItem.TYPE.equals(type)) {
            return new KeyStringSettingItem();
        }
        if (KeyBinarySettingItem.TYPE.equals(type)) {
            return new KeyBinarySettingItem();
        }
        if (KeyJsonSettingItem.TYPE.equals(type)) {
            return new KeyJsonSettingItem();
        }
        Log.w("SettingsBackup", "type: " + type + " are not handled!");
        return null;
    }

    public JSONObject toJson() {
        JSONObject obj = new JSONObject();
        try {
            obj.put(KEY_KEY, this.key);
            obj.put("type", getType());
            obj.put("value", getJsonValue());
        } catch (JSONException e) {
            Log.e("SettingsBackup", "JSONException occorred when toJson()", e);
        }
        return obj;
    }

    public int describeContents() {
        return 0;
    }

    /* access modifiers changed from: protected */
    public void fillFromParcel(Parcel source) {
        String key2 = source.readString();
        String rawValue = source.readString();
        this.key = key2;
        setValue(stringToValue(rawValue));
    }

    public void writeToParcel(Parcel dest, int flags) {
        String rawValue = valueToString(getValue());
        dest.writeString(this.key);
        dest.writeString(rawValue);
    }

    public int compareTo(SettingItem<?> another) {
        if (another == null) {
            return 1;
        }
        if (this.key != null || another.key == null) {
            return this.key.compareTo(another.key);
        }
        return -1;
    }
}
