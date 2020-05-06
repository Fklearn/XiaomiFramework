package miui.upnp.typedef.device;

import miui.upnp.typedef.datatype.DataType;
import miui.upnp.typedef.field.FieldDefinition;

public class ArgumentDefinition {
    private static final String DIRECTION = "direction";
    public static FieldDefinition Direction = new FieldDefinition("direction", DataType.STRING);
    private static final String NAME = "name";
    public static FieldDefinition Name = new FieldDefinition("name", DataType.STRING);
    private static final String RELATED_PROPERTY = "relatedStateVariable";
    public static FieldDefinition RelatedProperty = new FieldDefinition(RELATED_PROPERTY, DataType.STRING);
}
