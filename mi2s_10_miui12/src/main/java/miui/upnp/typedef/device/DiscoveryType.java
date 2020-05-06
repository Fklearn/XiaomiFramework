package miui.upnp.typedef.device;

import com.milink.api.v1.type.DeviceType;
import miui.yellowpage.YellowPageContract;

public enum DiscoveryType {
    UNDEFINED("undefined"),
    LOCAL(YellowPageContract.Search.LOCAL_SEARCH),
    LAN("lan"),
    BLUETOOTH(DeviceType.BLUETOOTH),
    BLE("ble"),
    AP("ap"),
    AIRTUNES(DeviceType.AIRTUNES);
    
    private String string;

    private DiscoveryType(String string2) {
        this.string = string2;
    }

    public String toString() {
        return this.string;
    }

    public static DiscoveryType retrieveType(String s) {
        for (DiscoveryType T : values()) {
            if (T.toString().equals(s)) {
                return T;
            }
        }
        return UNDEFINED;
    }
}
