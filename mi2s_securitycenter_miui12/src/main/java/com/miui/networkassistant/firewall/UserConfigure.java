package com.miui.networkassistant.firewall;

import android.net.Uri;

public class UserConfigure {
    public static final String BG_CONTROL_DEFAULT = "miuiAuto";
    public static final String BG_CONTROL_MIUI_AUTO = "miuiAuto";
    public static final String BG_CONTROL_NO_BG = "noBg";
    public static final String BG_CONTROL_NO_RESTRICT = "noRestrict";
    public static final String BG_CONTROL_RESTRICT_BG = "restrictBg";
    public static final Uri CONTENT_URI = Uri.withAppendedPath(URI, TABLE);
    public static final String METHOD_UPDATE = "userTableupdate";
    public static final String TABLE = "userTable";
    public static final Uri URI = Uri.parse("content://com.miui.powerkeeper.configure");

    public interface Columns {
        public static final String BG_CONTROL = "bgControl";
        public static final String BG_LOCATION = "bgLocation";
        public static final String ID = "_id";
        public static final String LAST_CONFIGURED = "lastConfigured";
        public static final String PACKAGE_NAME = "pkgName";
        public static final String USER_ID = "userId";
    }
}
