package miui.upnp.typedef.device;

import miui.upnp.typedef.datatype.DataType;
import miui.upnp.typedef.field.FieldDefinition;

public class IconDefinition {
    private static final String DEPTH = "depth";
    public static FieldDefinition Depth = new FieldDefinition(DEPTH, DataType.INT);
    private static final String HEIGHT = "height";
    public static FieldDefinition Height = new FieldDefinition(HEIGHT, DataType.INT);
    private static final String MIME_TYPE = "mimetype";
    public static FieldDefinition MimeType = new FieldDefinition(MIME_TYPE, DataType.STRING);
    private static final String URL = "url";
    public static FieldDefinition Url = new FieldDefinition("url", DataType.STRING);
    private static final String WIDTH = "width";
    public static FieldDefinition Width = new FieldDefinition("width", DataType.INT);
}
