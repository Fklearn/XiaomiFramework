package miui.upnp.typedef.device.invocation;

import android.util.Log;
import miui.upnp.typedef.device.Action;
import miui.upnp.typedef.device.Argument;
import miui.upnp.typedef.device.Device;
import miui.upnp.typedef.device.Service;

public class ActionInfoCreator {
    private static final String TAG = "ActionInfoCreator";

    public static ActionInfo create(Service service, String actionName) {
        Action action = service.getActions().get(actionName);
        if (action != null) {
            return create(action);
        }
        Log.d(TAG, String.format("Action not found: %s", new Object[]{actionName}));
        return null;
    }

    public static ActionInfo create(Device device, String serviceId, String actionName) {
        Service service = device.getService(serviceId);
        if (service != null) {
            return create(service, actionName);
        }
        Log.d(TAG, String.format("Service not found: %s", new Object[]{serviceId}));
        return null;
    }

    public static ActionInfo create(Action action) {
        ActionInfo info = null;
        if (action == null) {
            Log.d(TAG, "action is null");
        } else {
            Service service = action.getService();
            Device device = service.getDevice();
            info = new ActionInfo();
            info.setAction(action);
            info.setDiscoveryTypes(device.getDiscoveryTypes());
            info.setServiceType(service.getType());
            info.setAddress(device.getAddress());
            info.setHostPort(device.getHostPort());
            info.setControlUrl(service.getControlUrl());
            info.setDeviceId(device.getDeviceId());
            info.setServiceId(service.getServiceId());
            for (Argument arg : action.getArguments()) {
                info.getProperties().put(arg.getRelatedProperty(), service.getProperty(arg.getRelatedProperty()));
            }
        }
        return info;
    }
}
