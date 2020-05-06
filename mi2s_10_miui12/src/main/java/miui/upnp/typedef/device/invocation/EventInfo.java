package miui.upnp.typedef.device.invocation;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;
import miui.upnp.typedef.device.DiscoveryType;
import miui.upnp.typedef.device.urn.ServiceType;
import miui.upnp.typedef.field.FieldList;
import miui.upnp.typedef.property.Property;

public class EventInfo implements Parcelable {
    public static final Parcelable.Creator<EventInfo> CREATOR = new Parcelable.Creator<EventInfo>() {
        public EventInfo createFromParcel(Parcel in) {
            return new EventInfo(in);
        }

        public EventInfo[] newArray(int size) {
            return new EventInfo[size];
        }
    };
    private List<DiscoveryType> discoveryTypes = new ArrayList();
    private FieldList fields = new FieldList();
    private List<Property> properties = new ArrayList();
    private ServiceType serviceType = new ServiceType();

    public void setDiscoveryTypes(List<DiscoveryType> discoveryTypes2) {
        this.discoveryTypes = discoveryTypes2;
    }

    public List<DiscoveryType> getDiscoveryTypes() {
        return this.discoveryTypes;
    }

    public ServiceType getServiceType() {
        return this.serviceType;
    }

    public void setServiceType(ServiceType serviceType2) {
        this.serviceType = serviceType2;
    }

    public String getDeviceId() {
        return (String) this.fields.getValue(EventInfoDefinition.DeviceId);
    }

    public boolean setDeviceId(String deviceId) {
        return this.fields.setValue(EventInfoDefinition.DeviceId, (Object) deviceId);
    }

    public String getServiceId() {
        return (String) this.fields.getValue(EventInfoDefinition.ServiceId);
    }

    public boolean setServiceId(String serviceId) {
        return this.fields.setValue(EventInfoDefinition.ServiceId, (Object) serviceId);
    }

    public String getSessionId() {
        return (String) this.fields.getValue(EventInfoDefinition.SessionId);
    }

    public boolean setSessionId(String sessionId) {
        return this.fields.setValue(EventInfoDefinition.SessionId, (Object) sessionId);
    }

    public List<Property> getProperties() {
        return this.properties;
    }

    public EventInfo() {
        initialize();
    }

    public EventInfo(Parcel in) {
        initialize();
        readFromParcel(in);
    }

    private void initialize() {
        this.fields.initField(EventInfoDefinition.DeviceId, (Object) null);
        this.fields.initField(EventInfoDefinition.ServiceId, (Object) null);
        this.fields.initField(EventInfoDefinition.SessionId, (Object) null);
    }

    public void readFromParcel(Parcel in) {
        this.serviceType = ServiceType.create(in.readString());
        this.fields = (FieldList) in.readParcelable(FieldList.class.getClassLoader());
        int n = in.readInt();
        for (int i = 0; i < n; i++) {
            this.discoveryTypes.add(DiscoveryType.retrieveType(in.readString()));
        }
        int n2 = in.readInt();
        for (int i2 = 0; i2 < n2; i2++) {
            this.properties.add((Property) in.readParcelable(Property.class.getClassLoader()));
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.serviceType.toString());
        out.writeParcelable(this.fields, flags);
        out.writeInt(this.discoveryTypes.size());
        for (DiscoveryType discoveryType : this.discoveryTypes) {
            out.writeString(discoveryType.toString());
        }
        out.writeInt(this.properties.size());
        for (Property def : this.properties) {
            out.writeParcelable(def, flags);
        }
    }
}
