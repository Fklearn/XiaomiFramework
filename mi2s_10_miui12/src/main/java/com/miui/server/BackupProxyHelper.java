package com.miui.server;

import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.ServiceInfo;
import android.os.RemoteException;
import android.text.TextUtils;

public class BackupProxyHelper {
    public static boolean isMiPushRequired(IPackageManager pm, String pkgName) {
        String[] permissions;
        ServiceInfo[] services2;
        try {
            PackageInfo pkgInfo = pm.getPackageInfo(pkgName, 4100, 0);
            if (pkgInfo == null || (permissions = pkgInfo.requestedPermissions) == null) {
                return false;
            }
            String needPermission = pkgName + ".permission.MIPUSH_RECEIVE";
            int permissionIndex = 0;
            while (true) {
                if (permissionIndex >= permissions.length) {
                    break;
                } else if (TextUtils.equals(needPermission, permissions[permissionIndex])) {
                    break;
                } else {
                    permissionIndex++;
                }
            }
            if (permissionIndex >= permissions.length || (services2 = pkgInfo.services) == null) {
                return false;
            }
            int serviceIndex = 0;
            while (true) {
                if (serviceIndex >= services2.length) {
                    break;
                } else if (TextUtils.equals(services2[serviceIndex].name, "com.xiaomi.mipush.sdk.PushMessageHandler")) {
                    break;
                } else {
                    serviceIndex++;
                }
            }
            if (serviceIndex >= services2.length) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isAppInXSpace(IPackageManager pm, String pkgName) {
        try {
            if (pm.getPackageInfo(pkgName, 0, 999) != null) {
                return true;
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }
}
