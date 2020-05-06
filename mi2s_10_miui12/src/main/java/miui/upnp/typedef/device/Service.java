package miui.upnp.typedef.device;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import miui.upnp.typedef.device.urn.ServiceType;
import miui.upnp.typedef.field.FieldList;
import miui.upnp.typedef.property.Property;
import miui.upnp.typedef.property.PropertyDefinition;

public class Service implements Parcelable {
    public static final Parcelable.Creator<Service> CREATOR = new Parcelable.Creator<Service>() {
        public Service createFromParcel(Parcel source) {
            return new Service(source);
        }

        public Service[] newArray(int size) {
            return new Service[size];
        }
    };
    private static final String TAG = Service.class.getSimpleName();
    private Map<String, Action> actions = new HashMap();
    private Device device;
    private FieldList fields = new FieldList();
    private volatile Map<String, Property> properties = new HashMap();
    private Map<String, Subscription> subscribers = new HashMap();
    private ServiceType type = new ServiceType();

    public Device getDevice() {
        return this.device;
    }

    public void setDevice(Device device2) {
        this.device = device2;
    }

    public ServiceType getType() {
        return this.type;
    }

    public void setType(ServiceType type2) {
        this.type = type2;
    }

    public String getServiceId() {
        return (String) this.fields.getValue(ServiceDefinition.ServiceId);
    }

    public boolean setServiceId(String id) {
        return this.fields.setValue(ServiceDefinition.ServiceId, (Object) id);
    }

    public String getControlUrl() {
        return (String) this.fields.getValue(ServiceDefinition.ControlUrl);
    }

    public boolean setControlUrl(String url) {
        return this.fields.setValue(ServiceDefinition.ControlUrl, (Object) url);
    }

    public String getEventSubUrl() {
        return (String) this.fields.getValue(ServiceDefinition.EventSubUrl);
    }

    public boolean setEventSubUrl(String url) {
        return this.fields.setValue(ServiceDefinition.EventSubUrl, (Object) url);
    }

    public String getScpdUrl() {
        return (String) this.fields.getValue(ServiceDefinition.ScpdUrl);
    }

    public boolean setScpdUrl(String url) {
        return this.fields.setValue(ServiceDefinition.ScpdUrl, (Object) url);
    }

    public boolean isSubscribed() {
        return ((Boolean) this.fields.getValue(ServiceDefinition.Subscribed)).booleanValue();
    }

    public boolean setSubscribed(boolean subscribed) {
        return this.fields.setValue(ServiceDefinition.Subscribed, (Object) Boolean.valueOf(subscribed));
    }

    public String getSubscriptionId() {
        return (String) this.fields.getValue(ServiceDefinition.SubscriptionId);
    }

    public boolean setSubscriptionId(String id) {
        return this.fields.setValue(ServiceDefinition.SubscriptionId, (Object) id);
    }

    public Collection<Property> getProperties() {
        return this.properties.values();
    }

    public Property getProperty(String name) {
        return this.properties.get(name);
    }

    public Object getPropertyValue(String name) {
        Object value = null;
        Property property = this.properties.get(name);
        if (property != null) {
            value = property.getCurrentValue();
        }
        String str = TAG;
        Log.d(str, "getPropertyValue name:" + name + " value:" + value);
        return value;
    }

    public boolean setPropertyValue(String name, Object value) {
        boolean ret = false;
        Property property = this.properties.get(name);
        if (property != null) {
            ret = property.setDataValue(value);
        }
        String str = TAG;
        Log.d(str, "setPropertyValue name:" + name + " value:" + value + " ret:" + ret);
        return ret;
    }

    public PropertyDefinition getPropertyDefinition(String name) {
        Property p = this.properties.get(name);
        if (p == null) {
            return null;
        }
        return p.getDefinition();
    }

    public void addProperty(PropertyDefinition def) {
        this.properties.put(def.getName(), new Property(def, (Object) null));
    }

    public void addProperty(Property property) {
        this.properties.put(property.getDefinition().getName(), property);
    }

    public Map<String, Action> getActions() {
        return this.actions;
    }

    public void addAction(Action action) {
        action.setService(this);
        this.actions.put(action.getName(), action);
    }

    public Map<String, Subscription> getSubscribers() {
        return this.subscribers;
    }

    public void addSubscription(Subscription subscriber) {
        subscriber.setService(this);
        this.subscribers.put(subscriber.getCallbackUrl(), subscriber);
    }

    public void removeSubscription(String callback) {
        this.subscribers.remove(callback);
    }

    public boolean isPropertyChanged() {
        for (Property p : this.properties.values()) {
            if (p.getDefinition().isSendEvents() && p.isChanged()) {
                return true;
            }
        }
        return false;
    }

    public void cleanPropertyState() {
        for (Property p : this.properties.values()) {
            if (p.getDefinition().isSendEvents()) {
                p.cleanState();
            }
        }
    }

    public Service() {
        initialize();
    }

    public Service(ServiceType serviceType) {
        initialize();
        setType(serviceType);
    }

    private void initialize() {
        this.fields.initField(ServiceDefinition.ServiceId, (Object) null);
        this.fields.initField(ServiceDefinition.ControlUrl, (Object) null);
        this.fields.initField(ServiceDefinition.EventSubUrl, (Object) null);
        this.fields.initField(ServiceDefinition.ScpdUrl, (Object) null);
        this.fields.initField(ServiceDefinition.Subscribed, (Object) null);
        this.fields.initField(ServiceDefinition.SubscriptionId, (Object) null);
    }

    public Service(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.type = ServiceType.create(in.readString());
        this.fields = (FieldList) in.readParcelable(FieldList.class.getClassLoader());
        int n = in.readInt();
        for (int i = 0; i < n; i++) {
            addAction((Action) in.readParcelable(Action.class.getClassLoader()));
        }
        int n2 = in.readInt();
        for (int i2 = 0; i2 < n2; i2++) {
            addProperty((Property) in.readParcelable(Property.class.getClassLoader()));
        }
        int n3 = in.readInt();
        for (int i3 = 0; i3 < n3; i3++) {
            addSubscription((Subscription) in.readParcelable(Subscription.class.getClassLoader()));
        }
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.type.toString());
        out.writeParcelable(this.fields, flags);
        out.writeInt(this.actions.size());
        for (Action action : this.actions.values()) {
            out.writeParcelable(action, flags);
        }
        out.writeInt(this.properties.size());
        for (Property def : this.properties.values()) {
            out.writeParcelable(def, flags);
        }
        out.writeInt(this.subscribers.size());
        for (Subscription sub : this.subscribers.values()) {
            out.writeParcelable(sub, flags);
        }
    }

    public int describeContents() {
        return 0;
    }
}
