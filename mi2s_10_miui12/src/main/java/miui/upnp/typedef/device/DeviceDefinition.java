package miui.upnp.typedef.device;

import miui.upnp.typedef.datatype.DataType;
import miui.upnp.typedef.field.FieldDefinition;

public class DeviceDefinition {
    private static final String ADDRESS = "address";
    public static FieldDefinition Address = new FieldDefinition("address", DataType.STRING);
    private static final String DLNA_CAP = "dlna:X_DLNACAP";
    private static final String DLNA_DOC = "dlna:X_DLNADOC";
    public static FieldDefinition DeviceId = new FieldDefinition(UDN, DataType.STRING);
    public static FieldDefinition DlnaCap = new FieldDefinition(DLNA_CAP, DataType.STRING);
    public static FieldDefinition DlnaDoc = new FieldDefinition(DLNA_DOC, DataType.STRING);
    private static final String FRIENDLY_NAME = "friendlyName";
    public static FieldDefinition FriendlyName = new FieldDefinition(FRIENDLY_NAME, DataType.STRING);
    private static final String HOST_PORT = "hostPort";
    public static FieldDefinition HostPort = new FieldDefinition(HOST_PORT, DataType.INT);
    private static final String LOCATION = "location";
    public static FieldDefinition Location = new FieldDefinition("location", DataType.STRING);
    private static final String MANUFACTURER = "manufacturer";
    private static final String MANUFACTURER_URL = "manufacturerURL";
    private static final String MODEL_DESCRIPTION = "modelDescription";
    private static final String MODEL_NAME = "modelName";
    private static final String MODEL_NUMBER = "modelNumber";
    private static final String MODEL_URL = "modelURL";
    public static FieldDefinition Manufacturer = new FieldDefinition(MANUFACTURER, DataType.STRING);
    public static FieldDefinition ManufacturerUrl = new FieldDefinition(MANUFACTURER_URL, DataType.STRING);
    public static FieldDefinition ModelDescription = new FieldDefinition(MODEL_DESCRIPTION, DataType.STRING);
    public static FieldDefinition ModelName = new FieldDefinition(MODEL_NAME, DataType.STRING);
    public static FieldDefinition ModelNumber = new FieldDefinition(MODEL_NUMBER, DataType.STRING);
    public static FieldDefinition ModelUrl = new FieldDefinition(MODEL_URL, DataType.STRING);
    private static final String PRESENTATION_URL = "presentationURL";
    public static FieldDefinition PresentationUrl = new FieldDefinition(PRESENTATION_URL, DataType.STRING);
    private static final String QPLAY_CAPABILITY = "qq:X_QPlay_SoftwareCapability";
    public static FieldDefinition QplayCapability = new FieldDefinition(QPLAY_CAPABILITY, DataType.STRING);
    private static final String SERIAL_NUMBER = "serialNumber";
    public static FieldDefinition SerialNumber = new FieldDefinition(SERIAL_NUMBER, DataType.STRING);
    private static final String UDN = "UDN";
    private static final String UPC = "UPC";
    private static final String URLBASE = "URLBase";
    public static FieldDefinition Upc = new FieldDefinition(UPC, DataType.STRING);
    public static FieldDefinition UrlBase = new FieldDefinition(URLBASE, DataType.STRING);
}
