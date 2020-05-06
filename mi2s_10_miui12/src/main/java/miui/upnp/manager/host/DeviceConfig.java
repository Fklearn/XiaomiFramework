package miui.upnp.manager.host;

import android.content.Context;
import android.net.wifi.WifiManager;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import miui.upnp.typedef.device.Device;
import miui.upnp.typedef.device.DiscoveryType;
import miui.upnp.typedef.device.urn.DeviceType;
import miui.upnp.typedef.exception.UpnpException;

public class DeviceConfig {
    private String deviceName;
    private List<DiscoveryType> discoveryTypes = new ArrayList();
    private String manufacturer;
    private String manufacturerUrl;
    private String modelDescription;
    private String modelName;
    private String modelNumber;
    private String modelUrl;

    public void addDiscoveryType(DiscoveryType discoveryType) {
        this.discoveryTypes.add(discoveryType);
    }

    public void deviceName(String deviceName2) {
        this.deviceName = deviceName2;
    }

    public void modelNumber(String modelNumber2) {
        this.modelNumber = modelNumber2;
    }

    public void modelName(String modelName2) {
        this.modelName = modelName2;
    }

    public void modelDescription(String modelDescription2) {
        this.modelDescription = modelDescription2;
    }

    public void modelUrl(String modelUrl2) {
        this.modelUrl = modelUrl2;
    }

    public void manufacturer(String manufacturer2) {
        this.manufacturer = manufacturer2;
    }

    public void manufacturerUrl(String manufacturerUrl2) {
        this.manufacturerUrl = manufacturerUrl2;
    }

    public Device build(Context context, DeviceType deviceType) throws UpnpException {
        String deviceId = genDeviceId(context);
        Device device = new Device(deviceType);
        for (DiscoveryType type : this.discoveryTypes) {
            device.addDiscoveryType(type);
        }
        device.setDeviceId(deviceId);
        device.setFriendlyName(this.deviceName);
        device.setLocation("/upnp/" + deviceId + "/description.xml");
        device.setModelNumber(this.modelNumber);
        device.setModelName(this.modelName);
        device.setModelDescription(this.modelDescription);
        device.setModelUrl(this.modelUrl);
        device.setManufacturer(this.manufacturer);
        device.setManufacturerUrl(this.manufacturerUrl);
        return device;
    }

    private String genDeviceId(Context context) {
        String address = ((WifiManager) context.getSystemService("wifi")).getConnectionInfo().getMacAddress();
        return "uuid:" + UUID.nameUUIDFromBytes(address.getBytes()).toString();
    }
}
