package miui.upnp.typedef.device.invocation;

import miui.upnp.typedef.datatype.DataType;
import miui.upnp.typedef.field.FieldDefinition;

public class SubscriptionInfoDefinition {
    private static final String CALLBACK_URL = "callbackUrl";
    private static final String CP_ADDRESS = "cpAddress";
    private static final String CP_PORT = "cpPort";
    public static FieldDefinition CallbackUrl = new FieldDefinition(CALLBACK_URL, DataType.STRING);
    public static FieldDefinition CpAddress = new FieldDefinition(CP_ADDRESS, DataType.STRING);
    public static FieldDefinition CpPort = new FieldDefinition(CP_PORT, DataType.INT);
    private static final String DEVICE_ID = "deviceId";
    public static FieldDefinition DeviceId = new FieldDefinition(DEVICE_ID, DataType.STRING);
    private static final String EVENT_SUB_URL = "eventSubURL";
    public static FieldDefinition EventSubUrl = new FieldDefinition(EVENT_SUB_URL, DataType.STRING);
    private static final String HOST_ADDRESS = "hostAddress";
    private static final String HOST_PORT = "hostPort";
    public static FieldDefinition HostAddress = new FieldDefinition(HOST_ADDRESS, DataType.STRING);
    public static FieldDefinition HostPort = new FieldDefinition(HOST_PORT, DataType.INT);
    private static final String SERVICE_ID = "serviceId";
    private static final String SESSION_ID = "sessionId";
    private static final String SUBSCRIBED = "subscribed";
    private static final String SUBSCRIPTION_ID = "subscriptionId";
    public static FieldDefinition ServiceId = new FieldDefinition(SERVICE_ID, DataType.STRING);
    public static FieldDefinition SessionId = new FieldDefinition(SESSION_ID, DataType.STRING);
    public static FieldDefinition Subscribed = new FieldDefinition(SUBSCRIBED, DataType.BOOLEAN);
    public static FieldDefinition SubscriptionId = new FieldDefinition(SUBSCRIPTION_ID, DataType.STRING);
    private static final String TIMEOUT = "timeout";
    public static FieldDefinition Timeout = new FieldDefinition(TIMEOUT, DataType.INT);
}
