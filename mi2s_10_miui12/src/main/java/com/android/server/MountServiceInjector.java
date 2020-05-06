package com.android.server;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Slog;
import miui.securityspace.XSpaceUserHandle;

public class MountServiceInjector {
    private static final String TAG = "MountService";

    static boolean checkExternalStorageForXSpace(Context context, int uid, String pkgName) {
        if (!XSpaceUserHandle.isUidBelongtoXSpace(uid)) {
            return false;
        }
        try {
            if ((context.getPackageManager().getApplicationInfo(pkgName, 0).flags & 1) > 0) {
                return true;
            }
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            Slog.e(TAG, "Failed to get package info for " + pkgName, e);
            return false;
        }
    }
}
