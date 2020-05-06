package miui.upnp.typedef.device.invocation;

import miui.upnp.typedef.datatype.DataType;
import miui.upnp.typedef.field.FieldDefinition;

public class ActionInfoDefinition {
    private static final String ADDRESS = "address";
    public static FieldDefinition Address = new FieldDefinition("address", DataType.STRING);
    private static final String CONTROL_URL = "controlURL";
    public static FieldDefinition ControlUrl = new FieldDefinition(CONTROL_URL, DataType.STRING);
    private static final String DEVICE_ID = "deviceId";
    public static FieldDefinition DeviceId = new FieldDefinition(DEVICE_ID, DataType.STRING);
    private static final String HOST_PORT = "port";
    public static FieldDefinition HostPort = new FieldDefinition(HOST_PORT, DataType.INT);
    private static final String SERVICE_ID = "serviceId";
    private static final String SESSION_ID = "sessionId";
    public static FieldDefinition ServiceId = new FieldDefinition(SERVICE_ID, DataType.STRING);
    public static FieldDefinition SessionId = new FieldDefinition(SESSION_ID, DataType.STRING);
}
