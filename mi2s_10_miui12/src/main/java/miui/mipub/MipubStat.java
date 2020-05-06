package miui.mipub;

import android.net.Uri;
import android.provider.BaseColumns;

@Deprecated
public class MipubStat implements BaseColumns {
    public static final String AUTHORITY = "com.miui.mipub.MipubMsgProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://com.miui.mipub.MipubMsgProvider/");
    public static final int STAT_CALL_OFF = 2;
    public static final Uri STAT_CONTENT_URI = Uri.withAppendedPath(CONTENT_URI, "stat");
    public static final String STAT_CREATE_TIME = "create_time";
    public static final int STAT_DEFAULT_RETRY_TIMES = 9;
    public static final long STAT_EXPIRY_DATA = 604800000;
    public static final String STAT_MODIFY_TIME = "modify_time";
    public static final int STAT_MSG_MIPUB = 0;
    public static final int STAT_MSG_MIXIN = 1;
    public static final String STAT_MSG_TYPE = "message_type";
    public static final String STAT_PACKAGE_ID = "package_id";
    public static final String STAT_PACKAGE_XML = "package_xml";
    public static final String STAT_RETRY_TIMES = "retry_times";
}
