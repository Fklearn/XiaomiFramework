package com.miui.permission;

import java.util.HashMap;
import java.util.Map;

public class AnalyticNameUtil {
    public static final Map<Long, String> PERMISSION_ID_NAMES = new HashMap();

    static {
        PERMISSION_ID_NAMES.put(1L, "PERM_ID_SENDSMS");
        PERMISSION_ID_NAMES.put(2L, "PERM_ID_CALLPHONE");
        PERMISSION_ID_NAMES.put(4L, "PERM_ID_SMSDB");
        PERMISSION_ID_NAMES.put(8L, "PERM_ID_CONTACT");
        PERMISSION_ID_NAMES.put(16L, "PERM_ID_CALLLOG");
        PERMISSION_ID_NAMES.put(32L, "PERM_ID_LOCATION");
        PERMISSION_ID_NAMES.put(64L, "PERM_ID_PHONEINFO");
        PERMISSION_ID_NAMES.put(1024L, "PERM_ID_CALLSTATE");
        PERMISSION_ID_NAMES.put(Long.valueOf(PermissionManager.PERM_ID_CALLMONITOR), "PERM_ID_CALLMONITOR");
        PERMISSION_ID_NAMES.put(Long.valueOf(PermissionManager.PERM_ID_VIDEO_RECORDER), "PERM_ID_VIDEO_RECORDER");
        PERMISSION_ID_NAMES.put(Long.valueOf(PermissionManager.PERM_ID_SETTINGS), "PERM_ID_SETTINGS");
        PERMISSION_ID_NAMES.put(Long.valueOf(PermissionManager.PERM_ID_AUTOSTART), "PERM_ID_AUTOSTART");
        PERMISSION_ID_NAMES.put(Long.valueOf(PermissionManager.PERM_ID_AUDIO_RECORDER), "PERM_ID_AUDIO_RECORDER");
        PERMISSION_ID_NAMES.put(Long.valueOf(PermissionManager.PERM_ID_MMSDB), "PERM_ID_MMSDB");
        PERMISSION_ID_NAMES.put(Long.valueOf(PermissionManager.PERM_ID_SENDMMS), "PERM_ID_SENDMMS");
        PERMISSION_ID_NAMES.put(Long.valueOf(PermissionManager.PERM_ID_CALENDAR), "PERM_ID_CALENDAR");
        PERMISSION_ID_NAMES.put(Long.valueOf(PermissionManager.PERM_ID_READMMS), "PERM_ID_READ_MMS");
        PERMISSION_ID_NAMES.put(1073741824L, "PERM_ID_READ_CALLLOG");
        PERMISSION_ID_NAMES.put(Long.valueOf(PermissionManager.PERM_ID_READCONTACT), "PERM_ID_READ_CONTACT");
        PERMISSION_ID_NAMES.put(Long.valueOf(PermissionManager.PERM_ID_EXTERNAL_STORAGE), "PERM_ID_EXTERNAL_STORAGE");
        PERMISSION_ID_NAMES.put(Long.valueOf(PermissionManager.PERM_ID_BODY_SENSORS), "PERM_ID_BODY_SENSORS");
        PERMISSION_ID_NAMES.put(Long.valueOf(PermissionManager.PERM_ID_GET_ACCOUNTS), "PERM_ID_GET_ACCOUNTS");
        PERMISSION_ID_NAMES.put(Long.valueOf(PermissionManager.PERM_ID_ADD_VOICEMAIL), "PERM_ID_ADD_VOICEMAIL");
        PERMISSION_ID_NAMES.put(Long.valueOf(PermissionManager.PERM_ID_USE_SIP), "PERM_ID_USE_SIP");
        PERMISSION_ID_NAMES.put(Long.valueOf(PermissionManager.PERM_ID_PROCESS_OUTGOING_CALLS), "PERM_ID_PROCESS_OUTGOING_CALLS");
        PERMISSION_ID_NAMES.put(Long.valueOf(PermissionManager.PERM_ID_CLIPBOARD), "PERM_ID_CLIPBOARD");
        PERMISSION_ID_NAMES.put(Long.valueOf(PermissionManager.PERM_ID_REAL_READ_SMS), "PERM_ID_VIRTUAL_SMS");
        PERMISSION_ID_NAMES.put(Long.valueOf(PermissionManager.PERM_ID_REAL_READ_CONTACTS), "PERM_ID_VIRTUAL_CONTACTS");
        PERMISSION_ID_NAMES.put(Long.valueOf(PermissionManager.PERM_ID_REAL_READ_CALENDAR), "PERM_ID_VIRTUAL_CALENDAR");
        PERMISSION_ID_NAMES.put(Long.valueOf(PermissionManager.PERM_ID_REAL_READ_CALL_LOG), "PERM_ID_VIRTUAL_CALL_LOG");
        PERMISSION_ID_NAMES.put(Long.valueOf(PermissionManager.PERM_ID_REAL_READ_PHONE_STATE), "PERM_ID_VIRTUAL_READ_PHONE_STATE");
        PERMISSION_ID_NAMES.put(0L, "PERM_ID_WAKEPATH");
    }
}
