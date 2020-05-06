package miui.provider;

import android.net.Uri;

public class ThemeRuntimeDataContract {
    public static final String CONTENT_AUTHORITY = "com.android.thememanager.provider";
    public static final Uri CONTENT_URI = Uri.parse(CONTENT_URI_STRING);
    public static final String CONTENT_URI_STRING = "content://com.android.thememanager.provider";

    public static class Projection {
        public static final String LOCAL_ID = "local_id";
        public static final String ONLINE_ID = "online_id";
        public static final String RESOURCE_HASH = "resource_hash";
        public static final String RESOURCE_NAME = "resource_name";
        public static final String RESOURCE_PATH = "resource_path";

        private Projection() {
        }
    }

    private ThemeRuntimeDataContract() {
    }
}
