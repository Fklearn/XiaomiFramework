package miui.upnp.typedef.property;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Property implements Parcelable {
    public static final Parcelable.Creator<Property> CREATOR = new Parcelable.Creator<Property>() {
        public Property createFromParcel(Parcel in) {
            return new Property(in);
        }

        public Property[] newArray(int size) {
            return new Property[size];
        }
    };
    private static final String TAG = Property.class.getSimpleName();
    private PropertyDefinition definition;
    private volatile PropertyValue value;

    public Property() {
    }

    public Property(PropertyDefinition definition2, Object value2) {
        init(definition2, PropertyValue.create(value2 != null ? value2 : definition2.getDataType().createObjectValue()));
    }

    private void init(PropertyDefinition definition2, PropertyValue value2) {
        if (value2 == null) {
            value2 = PropertyValue.create(definition2.getDataType().createObjectValue());
        }
        this.definition = definition2;
        this.value = value2;
    }

    public PropertyDefinition getDefinition() {
        return this.definition;
    }

    public void setDefinition(PropertyDefinition definition2) {
        this.definition = definition2;
        this.value = PropertyValueUtil.createByType(definition2.getDataType().getJavaDataType());
    }

    public PropertyValue getPropertyValue() {
        return this.value;
    }

    public Object getOldValue() {
        return this.value.getOldValue();
    }

    public Object getCurrentValue() {
        return this.value.getValue();
    }

    public String getOldStringValue() {
        return this.definition.getDataType().toStringValue(this.value.getOldValue());
    }

    public String getCurrentStringValue() {
        return this.definition.getDataType().toStringValue(this.value.getValue());
    }

    private boolean isMultipleValue(Object value2) {
        if (this.definition.getAllowedValueType() != AllowedValueType.LIST || !(value2 instanceof String) || ((String) value2).split(",").length <= 1) {
            return false;
        }
        return true;
    }

    public boolean setDataValue(Object newValue) {
        if (newValue == null) {
            Log.e(TAG, "newValue is null");
            return false;
        }
        boolean multiple = false;
        if (this.definition.getAllowedValueType() == AllowedValueType.LIST && (newValue instanceof String)) {
            String string = (String) newValue;
            String[] a = string.split(",");
            if (a.length > 1) {
                multiple = true;
                String str = TAG;
                Log.d(str, "multiple value: " + string);
                int length = a.length;
                for (int i = 0; i < length; i++) {
                    String v = a[i];
                    if (!this.definition.validate(v)) {
                        Log.e(TAG, String.format("invalid value: %s, skip it!", new Object[]{v}));
                    } else {
                        this.value.addMultipleValue(v);
                    }
                }
            }
        }
        if (multiple) {
            return true;
        }
        if (!this.definition.validate(newValue)) {
            Log.e(TAG, String.format("invalid value: %s", new Object[]{newValue}));
            return false;
        }
        this.value.update(newValue);
        return true;
    }

    public boolean setDataValueByString(String string, boolean nullValueAllowed) {
        Object value2 = this.definition.getDataType().toObjectValue(string);
        if (value2 == null) {
            if (nullValueAllowed) {
                return true;
            }
            Log.d(TAG, "value is null");
            return false;
        } else if (setDataValue(value2)) {
            return true;
        } else {
            Log.e(TAG, String.format("%s setDataValue failed: %s(%s), dataType is: %s", new Object[]{this.definition.getName(), string, value2.getClass().getSimpleName(), this.definition.getDataType().getStringType()}));
            return false;
        }
    }

    public boolean isChanged() {
        return this.value.isChanged();
    }

    public void cleanState() {
        this.value.cleanState();
    }

    public Property(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.definition = (PropertyDefinition) in.readParcelable(PropertyDefinition.class.getClassLoader());
        this.value = (PropertyValue) in.readParcelable(PropertyValue.class.getClassLoader());
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(this.definition, flags);
        out.writeParcelable(this.value, flags);
    }
}
