package miui.upnp.manager.handler;

import miui.upnp.typedef.error.UpnpError;

public interface UpnpCompletionHandler {
    void onFailed(UpnpError upnpError);

    void onSucceed();
}
