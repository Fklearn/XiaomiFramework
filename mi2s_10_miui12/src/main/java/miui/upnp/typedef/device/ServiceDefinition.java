package miui.upnp.typedef.device;

import miui.upnp.typedef.datatype.DataType;
import miui.upnp.typedef.field.FieldDefinition;

public class ServiceDefinition {
    private static final String CONTROL_URL = "controlURL";
    public static FieldDefinition ControlUrl = new FieldDefinition(CONTROL_URL, DataType.STRING);
    private static final String EVENT_SUB_URL = "eventSubURL";
    public static FieldDefinition EventSubUrl = new FieldDefinition(EVENT_SUB_URL, DataType.STRING);
    private static final String SCPDURL = "SCPDURL";
    private static final String SERVICE_ID = "serviceId";
    private static final String SUBSCRIBED = "subscribed";
    private static final String SUBSCRIPTION_ID = "subscriptionId";
    public static FieldDefinition ScpdUrl = new FieldDefinition(SCPDURL, DataType.STRING);
    public static FieldDefinition ServiceId = new FieldDefinition(SERVICE_ID, DataType.STRING);
    public static FieldDefinition Subscribed = new FieldDefinition(SUBSCRIBED, DataType.BOOLEAN);
    public static FieldDefinition SubscriptionId = new FieldDefinition(SUBSCRIPTION_ID, DataType.STRING);
}
