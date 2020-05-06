package miui.upnp.typedef.device;

import android.os.Parcel;
import android.os.Parcelable;

public class PropertyChanged implements Parcelable {
    public static final Parcelable.Creator<PropertyChanged> CREATOR = new Parcelable.Creator<PropertyChanged>() {
        public PropertyChanged createFromParcel(Parcel source) {
            return new PropertyChanged(source);
        }

        public PropertyChanged[] newArray(int size) {
            return new PropertyChanged[size];
        }
    };
    private String name;
    private String value;

    public PropertyChanged(String name2, String value2) {
        this.name = name2;
        this.value = value2;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name2) {
        this.name = name2;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value2) {
        this.value = value2;
    }

    public PropertyChanged(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.name = in.readString();
        this.value = in.readString();
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.name);
        out.writeString(this.value);
    }

    public int describeContents() {
        return 0;
    }
}
