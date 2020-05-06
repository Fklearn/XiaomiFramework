package miui.upnp.manager.host;

import miui.upnp.typedef.device.invocation.ActionInfo;
import miui.upnp.typedef.device.urn.ServiceType;
import miui.upnp.typedef.error.UpnpError;

public abstract class ServiceHandler {
    public abstract UpnpError onAction(ActionInfo actionInfo);

    public String toScpdUrl(String uuid, ServiceType serviceType) {
        return String.format("/upnp/%s/%s.xml", new Object[]{uuid, serviceType.getName()});
    }

    public String toCtrlUrl(String uuid, ServiceType serviceType) {
        return String.format("/upnp/%s/ctrl/%s", new Object[]{uuid, serviceType.getName()});
    }

    public String toEventUrl(String uuid, ServiceType serviceType) {
        return String.format("/upnp/%s/event/%s", new Object[]{uuid, serviceType.getName()});
    }

    public String toServiceId(ServiceType serviceType) {
        return String.format("urn:upnp-org:serviceId:%s", new Object[]{serviceType.getName()});
    }
}
