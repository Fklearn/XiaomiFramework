package miui.upnp.manager.ctrlpoint;

import android.os.Parcelable;
import miui.upnp.typedef.device.Device;

public abstract class AbstractDevice implements Parcelable {
    public Device device;

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AbstractDevice that = (AbstractDevice) o;
        Device device2 = this.device;
        if (device2 != null) {
            if (device2.equals(that.device)) {
                return true;
            }
        } else if (that.device == null) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        Device device2 = this.device;
        if (device2 != null) {
            return device2.hashCode();
        }
        return 0;
    }
}
