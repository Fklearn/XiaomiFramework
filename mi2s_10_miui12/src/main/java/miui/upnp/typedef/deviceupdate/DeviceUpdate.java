package miui.upnp.typedef.deviceupdate;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;
import miui.upnp.typedef.device.DiscoveryType;
import miui.upnp.typedef.field.FieldList;

public class DeviceUpdate implements Parcelable {
    public static final Parcelable.Creator<DeviceUpdate> CREATOR = new Parcelable.Creator<DeviceUpdate>() {
        public DeviceUpdate createFromParcel(Parcel source) {
            return new DeviceUpdate(source);
        }

        public DeviceUpdate[] newArray(int size) {
            return new DeviceUpdate[size];
        }
    };
    private FieldList fields = new FieldList();
    private List<DeviceUpdateType> types = new ArrayList();

    public DeviceUpdate() {
        initialize();
    }

    public List<DeviceUpdateType> getTypes() {
        return this.types;
    }

    public void addType(DeviceUpdateType type) {
        if (!this.types.contains(type)) {
            this.types.add(type);
        }
    }

    public String getDeviceId() {
        return (String) this.fields.getValue(DeviceUpdateDefinition.DeviceId);
    }

    public boolean setDeviceId(String deviceId) {
        return this.fields.setValue(DeviceUpdateDefinition.DeviceId, (Object) deviceId);
    }

    public boolean setDiscoveryType(DiscoveryType discoveryType) {
        return this.fields.setValue(DeviceUpdateDefinition.DiscoveryType, (Object) discoveryType.toString());
    }

    public DiscoveryType getDiscoveryType() {
        return DiscoveryType.retrieveType((String) this.fields.getValue(DeviceUpdateDefinition.DiscoveryType));
    }

    private void initialize() {
        this.fields.initField(DeviceUpdateDefinition.DiscoveryType, (Object) null);
    }

    public DeviceUpdate(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        int n = in.readInt();
        for (int i = 0; i < n; i++) {
            this.types.add(DeviceUpdateType.retrieveType(in.readString()));
        }
        this.fields = (FieldList) in.readParcelable(FieldList.class.getClassLoader());
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.types.size());
        for (DeviceUpdateType type : this.types) {
            out.writeString(type.toString());
        }
        out.writeParcelable(this.fields, flags);
    }

    public int describeContents() {
        return 0;
    }
}
