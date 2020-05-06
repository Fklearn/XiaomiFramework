package miui.upnp.typedef.device.invocation;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import miui.upnp.typedef.device.Action;
import miui.upnp.typedef.device.Argument;
import miui.upnp.typedef.device.DiscoveryType;
import miui.upnp.typedef.device.urn.ServiceType;
import miui.upnp.typedef.field.FieldList;
import miui.upnp.typedef.property.Property;

public class ActionInfo implements Parcelable {
    public static final Parcelable.Creator<ActionInfo> CREATOR = new Parcelable.Creator<ActionInfo>() {
        public ActionInfo createFromParcel(Parcel in) {
            return new ActionInfo(in);
        }

        public ActionInfo[] newArray(int size) {
            return new ActionInfo[size];
        }
    };
    private static final String TAG = "ActionInfo";
    private Action action;
    private List<DiscoveryType> discoveryTypes = new ArrayList();
    private FieldList fields = new FieldList();
    private Map<String, Property> properties = new HashMap();
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

    public Action getAction() {
        return this.action;
    }

    public void setAction(Action action2) {
        this.action = action2;
    }

    public String getAddress() {
        return (String) this.fields.getValue(ActionInfoDefinition.Address);
    }

    public boolean setAddress(String address) {
        return this.fields.setValue(ActionInfoDefinition.Address, (Object) address);
    }

    public int getHostPort() {
        return ((Integer) this.fields.getValue(ActionInfoDefinition.HostPort)).intValue();
    }

    public boolean setHostPort(int port) {
        return this.fields.setValue(ActionInfoDefinition.HostPort, (Object) Integer.valueOf(port));
    }

    public String getDeviceId() {
        return (String) this.fields.getValue(ActionInfoDefinition.DeviceId);
    }

    public boolean setDeviceId(String deviceId) {
        return this.fields.setValue(ActionInfoDefinition.DeviceId, (Object) deviceId);
    }

    public String getServiceId() {
        return (String) this.fields.getValue(ActionInfoDefinition.ServiceId);
    }

    public boolean setServiceId(String serviceId) {
        return this.fields.setValue(ActionInfoDefinition.ServiceId, (Object) serviceId);
    }

    public String getControlUrl() {
        return (String) this.fields.getValue(ActionInfoDefinition.ControlUrl);
    }

    public boolean setControlUrl(String id) {
        return this.fields.setValue(ActionInfoDefinition.ControlUrl, (Object) id);
    }

    public String getSessionId() {
        return (String) this.fields.getValue(ActionInfoDefinition.SessionId);
    }

    public boolean setSessionId(String sessionId) {
        return this.fields.setValue(ActionInfoDefinition.SessionId, (Object) sessionId);
    }

    public Map<String, Property> getProperties() {
        return this.properties;
    }

    public boolean setArgumentValue(String name, Object value, Argument.Direction direction) {
        Property p = getRelatedProperty(name, direction);
        if (p != null) {
            return p.setDataValue(value);
        }
        Log.d(TAG, "relatedProperty not found: " + name);
        return false;
    }

    public boolean setArgumentValueByString(String name, String value, boolean nullValueAllowed, Argument.Direction direction) {
        Property p = getRelatedProperty(name, direction);
        if (p != null) {
            return p.setDataValueByString(value, nullValueAllowed);
        }
        Log.d(TAG, "relatedProperty not found: " + name);
        return false;
    }

    public Object getArgumentValue(String name) {
        Property property = getArgument(name);
        if (property != null) {
            return property.getCurrentValue();
        }
        return null;
    }

    public Property getArgument(String name) {
        return getRelatedProperty(name, Argument.Direction.IN);
    }

    public Property getResult(String name) {
        return getRelatedProperty(name, Argument.Direction.OUT);
    }

    private Property getRelatedProperty(String name, Argument.Direction direction) {
        Property p = null;
        Argument argument = null;
        Iterator<Argument> it = this.action.getArguments().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Argument a = it.next();
            if (a.getDirection() == direction && a.getName().equals(name)) {
                argument = a;
                break;
            }
        }
        if (argument == null) {
            Log.d(TAG, "argument not found: " + name);
        } else {
            p = this.properties.get(argument.getRelatedProperty());
            if (p == null) {
                Log.d(TAG, "Property not found: " + argument.getRelatedProperty());
            }
        }
        return p;
    }

    public ActionInfo() {
        initialize();
    }

    private void initialize() {
        this.fields.initField(ActionInfoDefinition.Address, (Object) null);
        this.fields.initField(ActionInfoDefinition.HostPort, (Object) null);
        this.fields.initField(ActionInfoDefinition.ControlUrl, (Object) null);
        this.fields.initField(ActionInfoDefinition.DeviceId, (Object) null);
        this.fields.initField(ActionInfoDefinition.ServiceId, (Object) null);
        this.fields.initField(ActionInfoDefinition.SessionId, (Object) null);
    }

    public ActionInfo(Parcel in) {
        initialize();
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.action = (Action) in.readParcelable(Action.class.getClassLoader());
        this.serviceType = ServiceType.create(in.readString());
        this.fields = (FieldList) in.readParcelable(FieldList.class.getClassLoader());
        int n = in.readInt();
        for (int i = 0; i < n; i++) {
            DiscoveryType discoveryType = DiscoveryType.retrieveType(in.readString());
            if (!this.discoveryTypes.contains(discoveryType)) {
                this.discoveryTypes.add(discoveryType);
            }
        }
        int n2 = in.readInt();
        for (int i2 = 0; i2 < n2; i2++) {
            Property property = (Property) in.readParcelable(Property.class.getClassLoader());
            this.properties.put(property.getDefinition().getName(), property);
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(this.action, flags);
        out.writeString(this.serviceType.toString());
        out.writeParcelable(this.fields, flags);
        out.writeInt(this.discoveryTypes.size());
        for (DiscoveryType discoveryType : this.discoveryTypes) {
            out.writeString(discoveryType.toString());
        }
        out.writeInt(this.properties.size());
        for (Property def : this.properties.values()) {
            out.writeParcelable(def, flags);
        }
    }
}
