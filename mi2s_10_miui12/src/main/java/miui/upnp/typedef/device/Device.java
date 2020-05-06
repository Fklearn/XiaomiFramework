package miui.upnp.typedef.device;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miui.upnp.typedef.device.urn.DeviceType;
import miui.upnp.typedef.field.FieldList;

public class Device implements Parcelable {
    public static final Parcelable.Creator<Device> CREATOR = new Parcelable.Creator<Device>() {
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        public Device[] newArray(int size) {
            return new Device[size];
        }
    };
    private DeviceType deviceType = new DeviceType();
    private List<DiscoveryType> discoveryTypes = new ArrayList();
    private FieldList fields = new FieldList();
    private List<Icon> icons = new ArrayList();
    private Map<String, Service> services = new HashMap();

    public int hashCode() {
        String deviceId = (String) this.fields.getValue(DeviceDefinition.DeviceId);
        return (1 * 31) + (deviceId == null ? 0 : deviceId.hashCode());
    }

    /* JADX WARNING: type inference failed for: r5v0, types: [java.lang.Object] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean equals(java.lang.Object r5) {
        /*
            r4 = this;
            java.lang.Class r0 = r4.getClass()
            java.lang.Class r1 = r5.getClass()
            if (r0 == r1) goto L_0x000c
            r0 = 0
            return r0
        L_0x000c:
            r0 = r5
            miui.upnp.typedef.device.Device r0 = (miui.upnp.typedef.device.Device) r0
            if (r4 != r0) goto L_0x0013
            r1 = 1
            return r1
        L_0x0013:
            miui.upnp.typedef.field.FieldList r1 = r4.fields
            miui.upnp.typedef.field.FieldDefinition r2 = miui.upnp.typedef.device.DeviceDefinition.DeviceId
            java.lang.Object r1 = r1.getValue((miui.upnp.typedef.field.FieldDefinition) r2)
            java.lang.String r1 = (java.lang.String) r1
            miui.upnp.typedef.field.FieldList r2 = r0.fields
            miui.upnp.typedef.field.FieldDefinition r3 = miui.upnp.typedef.device.DeviceDefinition.DeviceId
            java.lang.Object r2 = r2.getValue((miui.upnp.typedef.field.FieldDefinition) r3)
            java.lang.String r2 = (java.lang.String) r2
            boolean r3 = r1.equals(r2)
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: miui.upnp.typedef.device.Device.equals(java.lang.Object):boolean");
    }

    public DeviceType getDeviceType() {
        return this.deviceType;
    }

    public void setDeviceType(DeviceType type) {
        this.deviceType = type;
    }

    public List<DiscoveryType> getDiscoveryTypes() {
        return this.discoveryTypes;
    }

    public void addDiscoveryType(DiscoveryType discoveryType) {
        if (!this.discoveryTypes.contains(discoveryType)) {
            this.discoveryTypes.add(discoveryType);
        }
    }

    public String getLocation() {
        return (String) this.fields.getValue(DeviceDefinition.Location);
    }

    public boolean setLocation(String location) {
        return this.fields.setValue(DeviceDefinition.Location, (Object) location);
    }

    public String getAddress() {
        return (String) this.fields.getValue(DeviceDefinition.Address);
    }

    public boolean setAddress(String address) {
        return this.fields.setValue(DeviceDefinition.Address, (Object) address);
    }

    public int getHostPort() {
        return ((Integer) this.fields.getValue(DeviceDefinition.HostPort)).intValue();
    }

    public boolean setHostPort(int port) {
        return this.fields.setValue(DeviceDefinition.HostPort, (Object) Integer.valueOf(port));
    }

    public String getDeviceId() {
        return (String) this.fields.getValue(DeviceDefinition.DeviceId);
    }

    public boolean setDeviceId(String deviceId) {
        return this.fields.setValue(DeviceDefinition.DeviceId, (Object) deviceId);
    }

    public String getFriendlyName() {
        return (String) this.fields.getValue(DeviceDefinition.FriendlyName);
    }

    public boolean setFriendlyName(String name) {
        return this.fields.setValue(DeviceDefinition.FriendlyName, (Object) name);
    }

    public String getManufacturer() {
        return (String) this.fields.getValue(DeviceDefinition.Manufacturer);
    }

    public boolean setManufacturer(String manufacturer) {
        return this.fields.setValue(DeviceDefinition.Manufacturer, (Object) manufacturer);
    }

    public String getManufacturerUrl() {
        return (String) this.fields.getValue(DeviceDefinition.ManufacturerUrl);
    }

    public boolean setManufacturerUrl(String url) {
        return this.fields.setValue(DeviceDefinition.ManufacturerUrl, (Object) url);
    }

    public String getModelDescription() {
        return (String) this.fields.getValue(DeviceDefinition.ModelDescription);
    }

    public boolean setModelDescription(String description) {
        return this.fields.setValue(DeviceDefinition.ModelDescription, (Object) description);
    }

    public String getModelName() {
        return (String) this.fields.getValue(DeviceDefinition.ModelName);
    }

    public boolean setModelName(String name) {
        return this.fields.setValue(DeviceDefinition.ModelName, (Object) name);
    }

    public String getModelNumber() {
        return (String) this.fields.getValue(DeviceDefinition.ModelNumber);
    }

    public boolean setModelNumber(String number) {
        return this.fields.setValue(DeviceDefinition.ModelNumber, (Object) number);
    }

    public String getModelUrl() {
        return (String) this.fields.getValue(DeviceDefinition.ModelUrl);
    }

    public boolean setModelUrl(String url) {
        return this.fields.setValue(DeviceDefinition.ModelUrl, (Object) url);
    }

    public String getSerialNumber() {
        return (String) this.fields.getValue(DeviceDefinition.SerialNumber);
    }

    public boolean setSerialNumber(String number) {
        return this.fields.setValue(DeviceDefinition.SerialNumber, (Object) number);
    }

    public String getPresentationUrl() {
        return (String) this.fields.getValue(DeviceDefinition.PresentationUrl);
    }

    public boolean setPresentationUrl(String url) {
        return this.fields.setValue(DeviceDefinition.PresentationUrl, (Object) url);
    }

    public String getUrlBase() {
        return (String) this.fields.getValue(DeviceDefinition.UrlBase);
    }

    public boolean setUrlBase(String url) {
        return this.fields.setValue(DeviceDefinition.UrlBase, (Object) url);
    }

    public String getUpc() {
        return (String) this.fields.getValue(DeviceDefinition.Upc);
    }

    public boolean setUpc(String upc) {
        return this.fields.setValue(DeviceDefinition.Upc, (Object) upc);
    }

    public String getQplayCapability() {
        return (String) this.fields.getValue(DeviceDefinition.QplayCapability);
    }

    public boolean setQplayCapability(String capability) {
        return this.fields.setValue(DeviceDefinition.QplayCapability, (Object) capability);
    }

    public String getDlnaDoc() {
        return (String) this.fields.getValue(DeviceDefinition.DlnaDoc);
    }

    public boolean setDlnaDoc(String doc) {
        return this.fields.setValue(DeviceDefinition.DlnaDoc, (Object) doc);
    }

    public String getDlnaCap() {
        return (String) this.fields.getValue(DeviceDefinition.DlnaCap);
    }

    public boolean setDlnaCap(String cap) {
        return this.fields.setValue(DeviceDefinition.DlnaCap, (Object) cap);
    }

    public List<Icon> getIcons() {
        return this.icons;
    }

    public void addIcon(Icon icon) {
        this.icons.add(icon);
    }

    public Map<String, Service> getServices() {
        return this.services;
    }

    public void addService(Service service) {
        service.setDevice(this);
        this.services.put(service.getServiceId(), service);
    }

    public Service getService(String serviceId) {
        return this.services.get(serviceId);
    }

    public Device() {
        initialize();
    }

    public Device(DeviceType deviceType2) {
        initialize();
        setDeviceType(deviceType2);
    }

    private void initialize() {
        this.fields.initField(DeviceDefinition.Location, (Object) null);
        this.fields.initField(DeviceDefinition.Address, (Object) null);
        this.fields.initField(DeviceDefinition.HostPort, (Object) null);
        this.fields.initField(DeviceDefinition.DeviceId, (Object) null);
        this.fields.initField(DeviceDefinition.FriendlyName, (Object) null);
        this.fields.initField(DeviceDefinition.Manufacturer, (Object) null);
        this.fields.initField(DeviceDefinition.ManufacturerUrl, (Object) null);
        this.fields.initField(DeviceDefinition.ModelDescription, (Object) null);
        this.fields.initField(DeviceDefinition.ModelName, (Object) null);
        this.fields.initField(DeviceDefinition.ModelNumber, (Object) null);
        this.fields.initField(DeviceDefinition.ModelUrl, (Object) null);
        this.fields.initField(DeviceDefinition.SerialNumber, (Object) null);
        this.fields.initField(DeviceDefinition.PresentationUrl, (Object) null);
        this.fields.initField(DeviceDefinition.UrlBase, (Object) null);
        this.fields.initField(DeviceDefinition.Upc, (Object) null);
        this.fields.initField(DeviceDefinition.QplayCapability, (Object) null);
        this.fields.initField(DeviceDefinition.DlnaDoc, (Object) null);
        this.fields.initField(DeviceDefinition.DlnaCap, (Object) null);
    }

    public Device(Parcel in) {
        initialize();
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        this.deviceType = DeviceType.create(in.readString());
        this.fields = (FieldList) in.readParcelable(FieldList.class.getClassLoader());
        int n = in.readInt();
        for (int i = 0; i < n; i++) {
            this.discoveryTypes.add(DiscoveryType.retrieveType(in.readString()));
        }
        int n2 = in.readInt();
        for (int i2 = 0; i2 < n2; i2++) {
            this.icons.add((Icon) in.readParcelable(Icon.class.getClassLoader()));
        }
        int n3 = in.readInt();
        for (int i3 = 0; i3 < n3; i3++) {
            addService((Service) in.readParcelable(Service.class.getClassLoader()));
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.deviceType.toString());
        out.writeParcelable(this.fields, flags);
        out.writeInt(this.discoveryTypes.size());
        for (DiscoveryType discoveryType : this.discoveryTypes) {
            out.writeString(discoveryType.toString());
        }
        out.writeInt(this.icons.size());
        for (Icon icon : this.icons) {
            out.writeParcelable(icon, flags);
        }
        out.writeInt(this.services.size());
        for (Service service : this.services.values()) {
            out.writeParcelable(service, flags);
        }
    }
}
