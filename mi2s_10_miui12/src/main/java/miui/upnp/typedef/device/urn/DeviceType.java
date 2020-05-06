package miui.upnp.typedef.device.urn;

import android.os.Parcel;
import android.os.Parcelable;
import miui.upnp.typedef.device.urn.Urn;
import miui.upnp.typedef.device.urn.schemas.Schemas;

public class DeviceType extends Urn implements Parcelable {
    public static final Parcelable.Creator<DeviceType> CREATOR = new Parcelable.Creator<DeviceType>() {
        public DeviceType createFromParcel(Parcel in) {
            return new DeviceType(in);
        }

        public DeviceType[] newArray(int size) {
            return new DeviceType[size];
        }
    };

    public DeviceType(String subType, String version) {
        super.setType(Urn.Type.DEVICE);
        super.setDomain(Schemas.UPNP);
        super.setSubType(subType);
        super.setVersion(version);
    }

    public static DeviceType create(String string) {
        DeviceType thiz = new DeviceType();
        if (!thiz.parse(string)) {
            return null;
        }
        return thiz;
    }

    public boolean parse(String string) {
        boolean ret = super.parse(string);
        if (ret && getType() == Urn.Type.DEVICE) {
            return true;
        }
        return ret;
    }

    public String getName() {
        return getSubType();
    }

    public DeviceType() {
    }

    public DeviceType(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        parse(in.readString());
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(toString());
    }
}
