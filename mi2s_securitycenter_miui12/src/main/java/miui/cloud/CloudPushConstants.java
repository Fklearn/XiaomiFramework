package miui.cloud;

public class CloudPushConstants {
    public static final String ATTRIBUTES_NAME = "micloud-push";
    public static final String AUTH_TOKEN_TYPE = "micloud";
    public static final String CHANNEL_ID = "2";
    public static final String MICLOUD_PUSH_RECEIVE = "com.xiaomi.micloudPush.RECEIVE";
    public static final String MICLOUD_PUSH_REGISTRATION = "com.xiaomi.micloudPush.REGISTRATION";
    public static final String MICLOUD_PUSH_SUBSCRIBE = "com.xiaomi.micloudpush.SUBSCRIBE";
    public static final String PUSH_DATA = "pushData";
    public static final String PUSH_NAME = "pushName";
    public static final String PUSH_TYPE = "pushType";
    public static final String RECEIVER_META_DATA = "com.xiaomi.MicloudPush";
    public static final String URI_WATERMARK_LIST = "content://%s/watermark_list";
    public static final String USER_ID_SUFFIX = "@xiaomi.com";
    public static final String XML_CMD = "cmd";
    public static final String XML_ITEM = "item";
    public static final String XML_NAME = "name";
    public static final String XML_PAYLOAD = "payload";
    public static final String XML_PNS = "xm:pns";
    public static final String XML_TYPE = "type";
    public static final String XML_WATERMARK = "watermark";

    public interface MICLOUD_PUSH_ATTRI_NAME {
        public static final String CAPABILITY = "capability";
        public static final String CONTENT_AUTHORITY = "contentAuthority";
        public static final String PUSH_NAME = "pushName";
        public static final String PUSH_TYPE = "pushType";
    }

    public interface PushType {
        public static final String CMD = "cmd";
        public static final String WATERMARK = "watermark";
    }

    public interface WATERMARK_TYPE {
        public static final String GLOBAL = "g";
        public static final String PERSONAL = "p";
        public static final String SUBSCRIPTION = "s";
    }
}
