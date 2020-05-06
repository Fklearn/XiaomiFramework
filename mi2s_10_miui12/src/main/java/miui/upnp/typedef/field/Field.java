package miui.upnp.typedef.field;

import android.os.Parcel;
import android.os.Parcelable;

public class Field implements Parcelable {
    public static final Parcelable.Creator<Field> CREATOR = new Parcelable.Creator<Field>() {
        public Field createFromParcel(Parcel in) {
            return new Field(in);
        }

        public Field[] newArray(int size) {
            return new Field[size];
        }
    };
    private FieldDefinition definition;
    private Object value;

    public Field(FieldDefinition definition2, Object value2) {
        init(definition2, value2);
    }

    private void init(FieldDefinition definition2, Object value2) {
        if (value2 == null) {
            value2 = definition2.getDataType().createObjectValue();
        }
        this.definition = definition2;
        this.value = value2;
    }

    public FieldDefinition getDefinition() {
        return this.definition;
    }

    public void setDefinition(FieldDefinition definition2) {
        this.definition = definition2;
        this.value = definition2.getDataType().createObjectValue();
    }

    public Object getValue() {
        return this.value;
    }

    public boolean setValue(Object value2) {
        this.value = value2;
        return true;
    }

    public Field(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.definition = (FieldDefinition) in.readParcelable(FieldDefinition.class.getClassLoader());
        this.value = in.readValue(Object.class.getClassLoader());
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(this.definition, flags);
        out.writeValue(this.value);
    }
}
