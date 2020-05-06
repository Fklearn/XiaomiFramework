package miui.upnp.manager.handler;

import miui.upnp.typedef.device.invocation.ActionInfo;
import miui.upnp.typedef.error.UpnpError;

public interface UpnpActionHandler {
    UpnpError onAction(ActionInfo actionInfo);
}
