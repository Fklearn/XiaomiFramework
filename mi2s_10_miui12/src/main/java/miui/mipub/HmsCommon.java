package miui.mipub;

import android.net.Uri;
import android.provider.BaseColumns;

@Deprecated
public final class HmsCommon implements BaseColumns {
    public static final Uri CONTENT_URI = Uri.parse("content://hmscommon/");
    public static final Uri CONTENT_URI_MIPUBINFO = Uri.withAppendedPath(CONTENT_URI, "mipub_info");
    public static final Uri CONTENT_URI_MIPUBINFO_LOCAL = Uri.withAppendedPath(CONTENT_URI, "mipub_info/local");
    public static final Uri CONTENT_URI_MIPUBINFO_NET = Uri.withAppendedPath(CONTENT_URI, "mipub_info/net");
    public static final String FOLLOW = "follow";
    public static final String FOLLOW_TIME = "follow_time";
    public static final String MIPUB_DESC = "desc";
    public static final String MIPUB_ICON_URL = "iconUrl";
    public static final String MIPUB_ID = "mipub_id";
    public static final String MIPUB_NAME = "name";
}
