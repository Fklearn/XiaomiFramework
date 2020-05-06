package miui.upnp.typedef.field;

import android.os.Parcel;
import android.os.Parcelable;
import miui.upnp.typedef.datatype.DataType;

public class FieldDefinition implements Parcelable {
    public static final Parcelable.Creator<FieldDefinition> CREATOR = new Parcelable.Creator<FieldDefinition>() {
        public FieldDefinition createFromParcel(Parcel in) {
            return new FieldDefinition(in);
        }

        public FieldDefinition[] newArray(int size) {
            return new FieldDefinition[size];
        }
    };
    private DataType dataType;
    private String name;

    public FieldDefinition(String name2, DataType dataType2) {
        this.name = name2;
        this.dataType = dataType2;
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
        FieldDefinition other = (FieldDefinition) obj;
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

    private FieldDefinition() {
    }

    public FieldDefinition(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.name = in.readString();
        this.dataType = DataType.valueOf(in.readString());
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.name);
        out.writeString(this.dataType.toString());
    }
}
