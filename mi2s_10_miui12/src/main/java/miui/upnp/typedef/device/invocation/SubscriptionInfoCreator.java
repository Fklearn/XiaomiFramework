package miui.upnp.typedef.device.invocation;

import java.util.UUID;
import miui.upnp.typedef.device.Service;

public class SubscriptionInfoCreator {
    private SubscriptionInfoCreator() {
    }

    public static SubscriptionInfo create(Service service) {
        SubscriptionInfo info = new SubscriptionInfo();
        info.setHostAddress(service.getDevice().getAddress());
        info.setHostPort(service.getDevice().getHostPort());
        info.setSubscriptionId(service.getSubscriptionId());
        info.setEventSubUrl(service.getEventSubUrl());
        info.setDiscoveryTypes(service.getDevice().getDiscoveryTypes());
        info.setDeviceId(service.getDevice().getDeviceId());
        info.setServiceId(service.getServiceId());
        info.setCallbackUrl(String.format("/%s", new Object[]{UUID.nameUUIDFromBytes(service.getEventSubUrl().getBytes()).toString()}));
        return info;
    }
}
