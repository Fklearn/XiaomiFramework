package miui.upnp.typedef.property;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import miui.upnp.typedef.datatype.DataType;

public class PropertyDefinition implements Parcelable {
    public static final Parcelable.Creator<PropertyDefinition> CREATOR = new Parcelable.Creator<PropertyDefinition>() {
        public PropertyDefinition createFromParcel(Parcel in) {
            return new PropertyDefinition(in);
        }

        public PropertyDefinition[] newArray(int size) {
            return new PropertyDefinition[size];
        }
    };
    private static final String TAG = PropertyDefinition.class.getSimpleName();
    private AllowedValueList allowedValueList;
    private AllowedValueRange allowedValueRange;
    private AllowedValueType allowedValueType = AllowedValueType.ANY;
    private DataType dataType;
    private String defaultValue;
    private String name;
    private boolean sendEvents;

    public PropertyDefinition(String n, DataType t) {
        setName(n);
        setDataType(t);
    }

    public PropertyDefinition(String n, DataType t, boolean event) {
        setName(n);
        setDataType(t);
        setSendEvents(event);
    }

    public boolean isSendEvents() {
        return this.sendEvents;
    }

    public void setSendEvents(String sendEvents2) {
        this.sendEvents = DataType.BooleanValueOf(sendEvents2).booleanValue();
    }

    public void setSendEvents(boolean sendEvents2) {
        this.sendEvents = sendEvents2;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public DataType getDataType() {
        return this.dataType;
    }

    public void setDataType(DataType dataType2) {
        this.dataType = dataType2;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String defaultValue2) {
        this.defaultValue = defaultValue2;
    }

    public AllowedValueType getAllowedValueType() {
        return this.allowedValueType;
    }

    public void setAllowedValueType(AllowedValueType allowedValueType2) {
        this.allowedValueType = allowedValueType2;
    }

    public AllowedValueList getAllowedValueList() {
        return this.allowedValueList;
    }

    public void setAllowedValueList(AllowedValueList allowedValueList2) {
        this.allowedValueType = AllowedValueType.LIST;
        this.allowedValueList = allowedValueList2;
    }

    public AllowedValueRange getAllowedValueRange() {
        return this.allowedValueRange;
    }

    public void setAllowedValueRange(AllowedValueRange allowedValueRange2) {
        this.allowedValueType = AllowedValueType.RANGE;
        this.allowedValueRange = allowedValueRange2;
    }

    public boolean validate(Object value) {
        if (value == null) {
            return false;
        }
        if (!this.dataType.getJavaDataType().equals(value.getClass())) {
            Log.e(TAG, String.format("dataType is %s, value type is %s, invalid!", new Object[]{this.dataType.getJavaDataType().getSimpleName(), value.getClass().getSimpleName()}));
            return false;
        }
        AllowedValueList allowedValueList2 = this.allowedValueList;
        if (allowedValueList2 != null) {
            return allowedValueList2.isValid(value);
        }
        AllowedValueRange allowedValueRange2 = this.allowedValueRange;
        if (allowedValueRange2 != null) {
            return allowedValueRange2.isValid(value);
        }
        return true;
    }

    public Object valueOf(String value) {
        return this.dataType.toObjectValue(value);
    }

    public int hashCode() {
        int i = 1 * 31;
        String str = this.name;
        return i + (str == null ? 0 : str.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PropertyDefinition other = (PropertyDefinition) obj;
        String str = this.name;
        if (str == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!str.equals(other.name)) {
            return false;
        }
        return true;
    }

    public PropertyDefinition() {
    }

    public PropertyDefinition(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        boolean[] a = new boolean[1];
        in.readBooleanArray(a);
        this.sendEvents = a[0];
        this.name = in.readString();
        this.dataType = DataType.valueOf(in.readString());
        this.defaultValue = in.readString();
        this.allowedValueType = AllowedValueType.retrieveType(in.readInt());
        int i = AnonymousClass2.$SwitchMap$miui$upnp$typedef$property$AllowedValueType[this.allowedValueType.ordinal()];
        if (i == 1) {
            return;
        }
        if (i == 2) {
            this.allowedValueList = (AllowedValueList) in.readParcelable(AllowedValueList.class.getClassLoader());
        } else if (i == 3) {
            this.allowedValueRange = (AllowedValueRange) in.readParcelable(AllowedValueRange.class.getClassLoader());
        }
    }

    /* renamed from: miui.upnp.typedef.property.PropertyDefinition$2  reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$miui$upnp$typedef$property$AllowedValueType = new int[AllowedValueType.values().length];

        static {
            try {
                $SwitchMap$miui$upnp$typedef$property$AllowedValueType[AllowedValueType.ANY.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$property$AllowedValueType[AllowedValueType.LIST.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$miui$upnp$typedef$property$AllowedValueType[AllowedValueType.RANGE.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeBooleanArray(new boolean[]{this.sendEvents});
        out.writeString(this.name);
        out.writeString(this.dataType.toString());
        out.writeString(this.defaultValue);
        out.writeInt(this.allowedValueType.toInt());
        int i = AnonymousClass2.$SwitchMap$miui$upnp$typedef$property$AllowedValueType[this.allowedValueType.ordinal()];
        if (i == 1) {
            return;
        }
        if (i == 2) {
            out.writeParcelable(this.allowedValueList, flags);
        } else if (i == 3) {
            out.writeParcelable(this.allowedValueRange, flags);
        }
    }
}
