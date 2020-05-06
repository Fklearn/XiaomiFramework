package miui.upnp.typedef.device;

import miui.upnp.typedef.datatype.DataType;
import miui.upnp.typedef.field.FieldDefinition;

public class SubscriptionDefinition {
    private static final String CALLBACK_URL = "callbackUrl";
    public static FieldDefinition CallbackUrl = new FieldDefinition(CALLBACK_URL, DataType.STRING);
    private static final String SUBSCRIPTION_ID = "subscriptionId";
    public static FieldDefinition SubscriptionId = new FieldDefinition(SUBSCRIPTION_ID, DataType.STRING);
    private static final String TIMEOUT = "timeout";
    public static FieldDefinition Timeout = new FieldDefinition(TIMEOUT, DataType.INT);
}
