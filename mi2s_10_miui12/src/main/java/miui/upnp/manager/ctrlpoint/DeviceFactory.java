package miui.upnp.manager.ctrlpoint;

import android.annotation.TargetApi;
import android.util.Log;
import miui.upnp.manager.UpnpManager;
import miui.upnp.typedef.device.Device;
import miui.upnp.typedef.deviceclass.DeviceClass;

public class DeviceFactory {
    private static final String TAG = "DeviceFactroy";

    @TargetApi(19)
    public static AbstractDevice createDevice(Device device) {
        Log.e(TAG, "device type is: " + device.getDeviceType());
        DeviceClass deviceClazz = UpnpManager.getInstance().getClassProvider().getDeviceClass(device.getDeviceType());
        if (deviceClazz == null) {
            Log.e(TAG, "unknown device class: " + device.getDeviceType());
            deviceClazz = UpnpManager.getInstance().getClassProvider().getDeviceClass(UnknownDevice.DEVICE_TYPE);
            if (deviceClazz == null) {
                Log.e(TAG, "default device class not found");
                return null;
            }
        }
        Class<?> clazz = deviceClazz.getClazz();
        if (clazz == null) {
            Log.e(TAG, "class not found: " + deviceClazz.getDeviceType());
            return null;
        }
        try {
            return (AbstractDevice) clazz.getMethod("create", new Class[]{Device.class}).invoke((Object) null, new Object[]{device});
        } catch (ReflectiveOperationException e) {
            e.printStackTrace();
            return null;
        }
    }
}
