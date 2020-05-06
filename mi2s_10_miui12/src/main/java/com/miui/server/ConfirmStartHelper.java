package com.miui.server;

import android.content.Intent;

public class ConfirmStartHelper {
    private static final String CONFIRM_START_ACTIVITY_ACTION = "android.app.action.CHECK_ALLOW_START_ACTIVITY";
    private static final String CONFIRM_START_ACTIVITY_NAME = "com.miui.wakepath.ui.ConfirmStartActivity";
    private static final String PACKAGE_SECURITYCENTER = "com.miui.securitycenter";

    public static boolean hasOpenConfirmStartPermission(int callerAppUid, Intent intent) {
        if (callerAppUid == 1000 || intent == null) {
            return true;
        }
        if (CONFIRM_START_ACTIVITY_ACTION.equals(intent.getAction())) {
            return false;
        }
        if (intent.getComponent() == null || !"com.miui.securitycenter".equals(intent.getComponent().getPackageName()) || !CONFIRM_START_ACTIVITY_NAME.equals(intent.getComponent().getClassName())) {
            return true;
        }
        return false;
    }
}
