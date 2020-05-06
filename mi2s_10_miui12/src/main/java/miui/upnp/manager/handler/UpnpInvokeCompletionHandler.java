package miui.upnp.manager.handler;

import miui.upnp.typedef.device.invocation.ActionInfo;
import miui.upnp.typedef.error.UpnpError;

public interface UpnpInvokeCompletionHandler {
    void onFailed(UpnpError upnpError);

    void onSucceed(ActionInfo actionInfo);
}
