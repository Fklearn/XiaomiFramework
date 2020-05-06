package com.miui.permission;

import android.net.Uri;
import android.provider.BaseColumns;

public class PermissionContract {
    public static final String ACTION_USING_PERMISSION_CHANGE = "miui.intent.aciton.ACTION_USING_PERMISSION_CHANGE";
    public static final String ACTION_USING_STATUS_BAR_PERMISSION = "miui.intent.aciton.ACTION_USING_STATUS_BAR_PERMISSION";
    public static final String AUTHORITY = "com.lbe.security.miui.permmgr";
    public static final Uri CONTENT_URI = Uri.parse("content://com.lbe.security.miui.permmgr");
    public static final String DESCRIPTION = "miui_permission_description_info";
    public static final Uri DESCRIPTION_URI = Uri.parse("content://com.lbe.security.miui.permmgr/miui_permission_description_info");
    public static final String RECORD = "miui_permission_record_info";
    public static final Uri RECORD_URI = Uri.parse("content://com.lbe.security.miui.permmgr/miui_permission_record_info");

    public static class Active implements BaseColumns {
        public static final String FORCED_BITS = "forcedBits";
        public static final String INSTALL_TIME = "installTime";
        public static final String LAST_CONFIGURED = "lastConfigured";
        public static final String LAST_UPDATED = "lastUpdated";
        public static final String PACKAGE_NAME = "pkgName";
        public static final String PERMISSION_DESC = "permDesc";
        public static final String PERMISSION_MASK = "permMask";
        public static final String PRESENT = "present";
        public static final String PRUNE_AFTER_DELETE = "pruneAfterDelete";
        public static final String SUGGEST_ACCEPT = "suggestAccept";
        public static final String SUGGEST_BLOCK = "suggestBlock";
        public static final String SUGGEST_FOREGROUND = "suggestForeground";
        public static final String SUGGEST_PROMPT = "suggestPrompt";
        public static final String SUGGEST_REJECT = "suggestReject";
        public static final String TABLE_NAME = "active";
        public static final String UNINSTALL_TIME = "uninstallTime";
        public static final Uri URI = Uri.withAppendedPath(PermissionContract.CONTENT_URI, TABLE_NAME);
        public static final String USER_ACCEPT = "userAccept";
        public static final String USER_FOREGROUND = "userForeground";
        public static final String USER_PROMPT = "userPrompt";
        public static final String USER_REJECT = "userReject";
    }

    public static class Method {
        public static final int ADD_NOT_RECORD_LIST = 11;
        public static final int GET_ALL_PERMISSIONS = 4;
        public static final int GET_ALL_PERMISSION_GROUPS = 3;
        public static final int GET_EFFECTIVE_PERMISSIONS = 7;
        public static final int GET_FLAG = 1;
        public static final int GET_NOT_RECORD_LIST = 10;
        public static final int GET_PERMSSION_FOR_ID = 12;
        public static final int GET_USING_LOCATION_LIST = 13;
        public static final int SEND_WAKEPATH_PERMISSION_RECORD = 14;
        public static final int SET_APPLICATION_PERMISSION = 6;
        public static final int SET_FLAG = 2;
        public static final int SET_MODE = 9;
        public static final int UPDATE_DATA = 5;
        public static final int WRITE_WAKE_PATH_WHITE_LIST = 8;

        public static class Flag {
            public static final String EXTRA_DATA = "extra_data";
            public static int FLAG_ENABLED = 1;
        }

        public static class GetAllPermissionGroups {
            public static final String EXTRA_DATA = "extra_data";
        }

        public static class GetAllPermissions {
            public static final String EXTRA_DATA = "extra_data";
        }

        public static class GetEffectivePermissions {
            public static final String EXTRA_DATA = "extra_data";
        }

        public static class GetNotRecordList {
            public static final String EXTRA_DATA = "extra_data";
        }

        public static class GetPermissionForId {
            public static final String EXTRA_DATA = "extra_data";
        }

        public static class GetUsingPermissionList {
            public static final String EXTRA_DATA = "extra_data";
            public static final String EXTRA_GROUND_STATE = "groundState";
            public static final String EXTRA_PERMISSIONID = "extra_permissionId";
            public static final String EXTRA_TYPE = "extra_type";
        }

        public static class SavePermissionDescription {
            public static final String EXTRA_DESCRIPTION = "description";
            public static final String EXTRA_LOCALE = "locale";
            public static final String EXTRA_PACKAGE_NAME = "pkgName";
            public static final String EXTRA_PERMISSION_ID = "permissionId";
            public static final String EXTRA_PROCESS_STATE = "processState";
        }

        public static class SendPermissionRecord {
            public static final String EXTRA_CALLEE_PKG = "calleePkg";
            public static final String EXTRA_CALLER_UID = "callerUid";
            public static final String EXTRA_MODE = "mode";
            public static final String EXTRA_PACKAGE_NAME = "pkgName";
            public static final String EXTRA_TYPE = "type";
            public static final String EXTRA_USER = "user";
        }

        public static class SetApplicationPermission {
            public static final String EXTRA_ACTION = "extra_action";
            public static final String EXTRA_FLAGS = "extra_flags";
            public static final String EXTRA_PACKAGE = "extra_package";
            public static final String EXTRA_PERMISSION = "extra_permission";
        }

        public static class SetMode {
            public static final String EXTRA_CODE = "extra_code";
            public static final String EXTRA_FLAGS = "extra_flags";
            public static final String EXTRA_MODE = "extra_mode";
            public static final String EXTRA_PACKAGE_NAME = "extra_pkg";
            public static final String EXTRA_SUPPORT_RUNTIME = "extra_support_runtime";
            public static final String EXTRA_UID = "extra_uid";
        }
    }

    public static class PermissionRecord implements BaseColumns {
        public static final String ACTION_MODE = "mode";
        public static final String APP_OP = "op";
        public static final String CALLEE_PKG = "calleePkg";
        public static final String COUNT = "count";
        public static final String END_TIME = "endTime";
        public static final String PACKAGE_NAME = "pkgName";
        public static final String PERMISSION_ID = "permissionId";
        public static final String PROCESS_STATE = "processState";
        public static final String START_TIME = "startTime";
        public static final String TABLE_NAME = "miui_permission_record_info";
        public static final String USER = "user";
    }
}
