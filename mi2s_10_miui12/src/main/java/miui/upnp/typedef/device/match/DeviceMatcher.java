package miui.upnp.typedef.device.match;

import java.util.List;
import miui.upnp.typedef.device.Device;
import miui.upnp.typedef.device.Service;
import miui.upnp.typedef.device.urn.Urn;

public class DeviceMatcher {
    public static boolean deviceIsMatched(Device device, List<Urn> filters) {
        if (filters == null || filters.size() == 0) {
            return true;
        }
        for (Urn t : filters) {
            if (t.getType() == Urn.Type.DEVICE) {
                if (device.getDeviceType().toString().equals(t.toString())) {
                    return true;
                }
            } else if (serviceIsMatched(device, t)) {
                return true;
            }
        }
        return false;
    }

    public static boolean serviceIsMatched(Device device, Urn serviceType) {
        for (Service s : device.getServices().values()) {
            if (s.getType().toString().equals(serviceType.toString())) {
                return true;
            }
        }
        return false;
    }
}
