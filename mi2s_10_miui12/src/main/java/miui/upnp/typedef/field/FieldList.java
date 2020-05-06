package miui.upnp.typedef.field;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.HashMap;
import java.util.Map;

public class FieldList implements Parcelable {
    public static final Parcelable.Creator<FieldList> CREATOR = new Parcelable.Creator<FieldList>() {
        public FieldList createFromParcel(Parcel in) {
            return new FieldList(in);
        }

        public FieldList[] newArray(int size) {
            return new FieldList[size];
        }
    };
    private Map<String, Field> fields = new HashMap();

    private Field getField(String name) {
        return this.fields.get(name);
    }

    private Field getField(FieldDefinition definition) {
        return this.fields.get(definition.getName());
    }

    private FieldDefinition getFieldDefinition(String name) {
        Field field = getField(name);
        if (field == null) {
            return null;
        }
        return field.getDefinition();
    }

    public Object getValue(FieldDefinition definition) {
        Field field = getField(definition);
        if (field == null) {
            return null;
        }
        return field.getValue();
    }

    public Object getValue(String name) {
        Field field = getField(name);
        if (field == null) {
            return null;
        }
        return field.getValue();
    }

    public void initField(FieldDefinition definition, Object value) {
        this.fields.put(definition.getName(), new Field(definition, value));
    }

    public void initField(Field field) {
        this.fields.put(field.getDefinition().getName(), field);
    }

    public boolean setValue(FieldDefinition definition, Object value) {
        Field field = getField(definition);
        if (field == null) {
            return false;
        }
        field.setValue(value);
        return true;
    }

    public boolean setValue(String name, Object value) {
        Field field = getField(name);
        if (field == null) {
            return false;
        }
        field.setValue(value);
        return true;
    }

    public FieldList() {
    }

    public FieldList(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        int n = in.readInt();
        for (int i = 0; i < n; i++) {
            Field f = (Field) in.readParcelable(Field.class.getClassLoader());
            this.fields.put(f.getDefinition().getName(), f);
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.fields.size());
        for (Field f : this.fields.values()) {
            out.writeParcelable(f, flags);
        }
    }
}
