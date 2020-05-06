package miui.upnp.typedef.deviceupdate;

import miui.upnp.typedef.datatype.DataType;
import miui.upnp.typedef.field.FieldDefinition;

public class DeviceUpdateDefinition {
    private static final String DISCOVERY_TYPE = "discoveryType";
    public static FieldDefinition DeviceId = new FieldDefinition(UDN, DataType.STRING);
    public static FieldDefinition DiscoveryType = new FieldDefinition(DISCOVERY_TYPE, DataType.STRING);
    private static final String UDN = "UDN";
}
