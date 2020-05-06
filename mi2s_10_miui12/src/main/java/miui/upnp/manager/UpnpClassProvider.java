package miui.upnp.manager;

import java.util.HashMap;
import java.util.Map;
import miui.upnp.typedef.device.urn.DeviceType;
import miui.upnp.typedef.deviceclass.DeviceClass;

public class UpnpClassProvider {
    private Map<String, DeviceClass> classes = new HashMap();

    public synchronized void addDeviceClass(DeviceClass clazz) {
        this.classes.put(clazz.getDeviceType().toString(), clazz);
    }

    public synchronized DeviceClass getDeviceClass(DeviceType deviceType) {
        return getDeviceClass(deviceType.toString());
    }

    public synchronized void clear() {
        this.classes.clear();
    }

    public synchronized DeviceClass getDeviceClass(String deviceType) {
        return this.classes.get(deviceType);
    }
}
