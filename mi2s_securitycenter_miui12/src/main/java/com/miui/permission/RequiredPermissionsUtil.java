package com.miui.permission;

import android.app.AppOpsManager;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.Log;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import miui.os.Build;

public class RequiredPermissionsUtil {
    private static final Set<String> MIUI_SIGNATURES = new HashSet();
    private static final String REQUIRED_PERMISSIONS = "required_permissions";
    public static final Map<String, Long> RUNTIME_PERMISSIONS = new HashMap();
    private static final String TAG = "RequiredUtil";

    static {
        RUNTIME_PERMISSIONS.put("android.permission.READ_PHONE_STATE", 64L);
        RUNTIME_PERMISSIONS.put("android.permission.CALL_PHONE", 2L);
        RUNTIME_PERMISSIONS.put("android.permission.READ_CALL_LOG", 1073741824L);
        RUNTIME_PERMISSIONS.put("android.permission.WRITE_CALL_LOG", 16L);
        RUNTIME_PERMISSIONS.put("com.android.voicemail.permission.ADD_VOICEMAIL", Long.valueOf(PermissionManager.PERM_ID_ADD_VOICEMAIL));
        RUNTIME_PERMISSIONS.put("android.permission.USE_SIP", Long.valueOf(PermissionManager.PERM_ID_USE_SIP));
        RUNTIME_PERMISSIONS.put("android.permission.PROCESS_OUTGOING_CALLS", Long.valueOf(PermissionManager.PERM_ID_PROCESS_OUTGOING_CALLS));
        RUNTIME_PERMISSIONS.put("android.permission.READ_CONTACTS", Long.valueOf(PermissionManager.PERM_ID_READCONTACT));
        RUNTIME_PERMISSIONS.put("android.permission.WRITE_CONTACTS", 8L);
        RUNTIME_PERMISSIONS.put("android.permission.GET_ACCOUNTS", Long.valueOf(PermissionManager.PERM_ID_GET_ACCOUNTS));
        RUNTIME_PERMISSIONS.put("android.permission.ACCESS_FINE_LOCATION", 32L);
        RUNTIME_PERMISSIONS.put("android.permission.ACCESS_COARSE_LOCATION", 32L);
        Map<String, Long> map = RUNTIME_PERMISSIONS;
        Long valueOf = Long.valueOf(PermissionManager.PERM_ID_CALENDAR);
        map.put("android.permission.READ_CALENDAR", valueOf);
        RUNTIME_PERMISSIONS.put("android.permission.WRITE_CALENDAR", valueOf);
        RUNTIME_PERMISSIONS.put("android.permission.SEND_SMS", 1L);
        Map<String, Long> map2 = RUNTIME_PERMISSIONS;
        Long valueOf2 = Long.valueOf(PermissionManager.PERM_ID_READSMS);
        map2.put("android.permission.RECEIVE_SMS", valueOf2);
        RUNTIME_PERMISSIONS.put("android.permission.READ_SMS", valueOf2);
        Map<String, Long> map3 = RUNTIME_PERMISSIONS;
        Long valueOf3 = Long.valueOf(PermissionManager.PERM_ID_READMMS);
        map3.put("android.permission.RECEIVE_WAP_PUSH", valueOf3);
        RUNTIME_PERMISSIONS.put("android.permission.RECEIVE_MMS", valueOf3);
        RUNTIME_PERMISSIONS.put("android.permission.READ_CELL_BROADCASTS", valueOf2);
        RUNTIME_PERMISSIONS.put("android.permission.RECORD_AUDIO", Long.valueOf(PermissionManager.PERM_ID_AUDIO_RECORDER));
        RUNTIME_PERMISSIONS.put("android.permission.CAMERA", Long.valueOf(PermissionManager.PERM_ID_VIDEO_RECORDER));
        RUNTIME_PERMISSIONS.put("android.permission.BODY_SENSORS", Long.valueOf(PermissionManager.PERM_ID_BODY_SENSORS));
        Map<String, Long> map4 = RUNTIME_PERMISSIONS;
        Long valueOf4 = Long.valueOf(PermissionManager.PERM_ID_EXTERNAL_STORAGE);
        map4.put("android.permission.READ_EXTERNAL_STORAGE", valueOf4);
        RUNTIME_PERMISSIONS.put("android.permission.WRITE_EXTERNAL_STORAGE", valueOf4);
        RUNTIME_PERMISSIONS.put("android.permission.ACCESS_BACKGROUND_LOCATION", Long.valueOf(PermissionManager.PERM_ID_BACKGROUND_LOCATION));
        MIUI_SIGNATURES.add(ApkLoader.PLATFORM_SHA256);
        MIUI_SIGNATURES.add("C8:A2:E9:BC:CF:59:7C:2F:B6:DC:66:BE:E2:93:FC:13:F2:FC:47:EC:77:BC:6B:2B:0D:52:C1:1F:51:19:2A:B8");
        MIUI_SIGNATURES.add("D4:5F:07:6F:E2:3A:1A:5B:7F:48:6E:3F:F4:15:47:A2:02:3D:BF:E1:FE:73:35:3B:1E:48:EB:DF:ED:72:CC:6F");
        MIUI_SIGNATURES.add("A4:0D:A8:0A:59:D1:70:CA:A9:50:CF:15:C1:8C:45:4D:47:A3:9B:26:98:9D:8B:64:0E:CD:74:5B:A7:1B:F5:DC");
    }

    public static String getPackageSHA256(PackageInfo packageInfo) {
        try {
            MessageDigest instance = MessageDigest.getInstance("SHA256");
            instance.update(packageInfo.signatures[0].toByteArray());
            StringBuilder sb = new StringBuilder();
            byte[] digest = instance.digest();
            int length = digest.length;
            for (int i = 0; i < length; i++) {
                if (i > 0) {
                    sb.append(":");
                }
                sb.append(Integer.toString((digest[i] & 255) + 256, 16).substring(1));
            }
            return sb.toString().toUpperCase();
        } catch (Exception unused) {
            return "";
        }
    }

    public static boolean isAdaptedRequiredPermissions(PackageInfo packageInfo) {
        ApplicationInfo applicationInfo;
        if (Build.IS_INTERNATIONAL_BUILD || packageInfo == null || (applicationInfo = packageInfo.applicationInfo) == null || UserHandle.getAppId(applicationInfo.uid) < 10000 || packageInfo.applicationInfo.metaData == null || !isMiuiEleven()) {
            return false;
        }
        return !TextUtils.isEmpty(packageInfo.applicationInfo.metaData.getString(REQUIRED_PERMISSIONS));
    }

    public static boolean isAdaptedRequiredPermissionsOnData(PackageInfo packageInfo) {
        return MIUI_SIGNATURES.contains(getPackageSHA256(packageInfo)) && isAdaptedRequiredPermissions(packageInfo);
    }

    private static boolean isMiuiEleven() {
        try {
            return Integer.parseInt(SystemProperties.get("ro.miui.ui.version.name", "V0").substring(1)) >= 11;
        } catch (Exception e) {
            Log.e(TAG, "get miuiVersion Exception!", e);
            return false;
        }
    }

    public static List<Integer> retrieveRequiredOps(ApplicationInfo applicationInfo) {
        int permissionToOpCode;
        List<String> retrieveRequiredPermissions = retrieveRequiredPermissions(applicationInfo);
        if (retrieveRequiredPermissions == null) {
            return null;
        }
        ArrayList arrayList = new ArrayList();
        for (String next : retrieveRequiredPermissions) {
            if ("android.permission.ACCESS_BACKGROUND_LOCATION".equals(next)) {
                permissionToOpCode = 10027;
            } else {
                permissionToOpCode = AppOpsManager.permissionToOpCode(next);
                if (permissionToOpCode == -1) {
                }
            }
            arrayList.add(Integer.valueOf(permissionToOpCode));
        }
        return arrayList;
    }

    public static List<String> retrieveRequiredPermissions(ApplicationInfo applicationInfo) {
        if (applicationInfo == null || applicationInfo.metaData == null) {
            return null;
        }
        String string = applicationInfo.metaData.getString(REQUIRED_PERMISSIONS);
        if (TextUtils.isEmpty(string)) {
            return null;
        }
        return Arrays.asList(string.split(";"));
    }
}
