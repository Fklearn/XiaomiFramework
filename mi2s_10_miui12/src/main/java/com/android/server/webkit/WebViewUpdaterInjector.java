package com.android.server.webkit;

import android.webkit.UserPackage;
import miui.securityspace.XSpaceUserHandle;

public class WebViewUpdaterInjector {
    public static boolean isInstalledAndEnabledForUser(UserPackage userPackage) {
        if (XSpaceUserHandle.isXSpaceUser(userPackage.getUserInfo()) || userPackage.getUserInfo().id == 99) {
            return true;
        }
        return false;
    }
}
