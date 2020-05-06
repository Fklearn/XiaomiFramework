package com.android.server.pm;

import android.app.AppGlobals;

public class PackageManagerServicePermissionProxy {
    public static void grantInstallPermission(String packageName, String name, int userId) {
        PackageManagerService pms = AppGlobals.getPackageManager();
        synchronized (pms.mPackages) {
            ((SettingBase) pms.mPackages.get(packageName).mExtras).getPermissionsState().grantInstallPermission(pms.mSettings.mPermissions.getPermission(name));
        }
    }
}
