package miui.upnp.typedef.device.invocation;

import miui.upnp.typedef.datatype.DataType;
import miui.upnp.typedef.field.FieldDefinition;

public class EventInfoDefinition {
    private static final String DEVICE_ID = "deviceId";
    public static FieldDefinition DeviceId = new FieldDefinition(DEVICE_ID, DataType.STRING);
    private static final String SERVICE_ID = "serviceId";
    private static final String SESSION_ID = "sessionId";
    public static FieldDefinition ServiceId = new FieldDefinition(SERVICE_ID, DataType.STRING);
    public static FieldDefinition SessionId = new FieldDefinition(SESSION_ID, DataType.STRING);
}
