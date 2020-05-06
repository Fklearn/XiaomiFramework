package com.miui.permcenter.compact;

import com.miui.permission.PermissionManager;

public class PermissionManagerCompat {
    public static final int FLAG_KILL_PROCESS = 2;
    public static final String TAG = "PermissionManagerCompat";

    public static void setApplicationPermission(PermissionManager permissionManager, long j, int i, int i2, String... strArr) {
        Class cls = Integer.TYPE;
        Class[] clsArr = {Long.TYPE, cls, cls, String[].class};
        try {
            ReflectUtilHelper.callObjectMethod(TAG, permissionManager, "setApplicationPermission", clsArr, Long.valueOf(j), Integer.valueOf(i), Integer.valueOf(i2), strArr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setApplicationPermissionWithVirtual(PermissionManager permissionManager, long j, int i, int i2, String... strArr) {
        boolean z = i == 7;
        setApplicationPermission(permissionManager, j, z ? 3 : i, 2, strArr);
        if (PermissionManager.virtualMap.containsKey(Long.valueOf(j))) {
            setApplicationPermission(permissionManager, PermissionManager.virtualMap.get(Long.valueOf(j)).longValue(), z ? 1 : 3, 2, strArr);
        }
    }
}
