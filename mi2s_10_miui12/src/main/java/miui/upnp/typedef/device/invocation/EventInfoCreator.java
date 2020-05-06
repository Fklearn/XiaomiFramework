package miui.upnp.typedef.device.invocation;

import android.util.Log;
import miui.upnp.typedef.device.Device;
import miui.upnp.typedef.device.Service;
import miui.upnp.typedef.property.Property;

public class EventInfoCreator {
    private static final String TAG = "EventInfoCreator";

    public static EventInfo create(Device device, String serviceId) {
        Service service = device.getService(serviceId);
        if (service != null) {
            return create(service);
        }
        Log.d(TAG, String.format("Service not found: %s", new Object[]{serviceId}));
        return null;
    }

    public static EventInfo create(Service service) {
        EventInfo info = new EventInfo();
        info.setDiscoveryTypes(service.getDevice().getDiscoveryTypes());
        info.setServiceType(service.getType());
        info.setServiceId(service.getServiceId());
        info.setDeviceId(service.getDevice().getDeviceId());
        for (Property p : service.getProperties()) {
            if (p.getDefinition().isSendEvents()) {
                info.getProperties().add(p);
            }
        }
        return info;
    }
}
