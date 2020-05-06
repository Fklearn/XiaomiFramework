package com.android.server.wm;

import android.content.Context;
import android.content.pm.PackageInfo;
import java.util.ArrayList;
import miui.os.SystemProperties;

public class AppWindowContainerControllerInjector {
    private static boolean sEnableSW = SystemProperties.getBoolean("persist.startingwindow.enable", false);

    private AppWindowContainerControllerInjector() {
    }

    static boolean whiteListContains(String pkg) {
        ArrayList<String> whiteList = new ArrayList<>();
        whiteList.add("com.tencent.mm");
        whiteList.add("com.tencent.mobileqq");
        if (whiteList.contains(pkg)) {
            return true;
        }
        return false;
    }

    static boolean isSystemApp(Context context, String pkg) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(pkg, 0);
            if (packageInfo == null || packageInfo.applicationInfo == null || !packageInfo.applicationInfo.isSystemApp()) {
                return false;
            }
            return true;
        } catch (Exception e) {
            return true;
        }
    }

    static boolean isAllowedForceStartingWindow(Context context, String pkg, boolean windowIsTranslucent, boolean windowDisableStarting) {
        return !sEnableSW ? !windowIsTranslucent && !windowDisableStarting : (!windowIsTranslucent && !windowDisableStarting) || (!whiteListContains(pkg) && !isSystemApp(context, pkg));
    }
}
