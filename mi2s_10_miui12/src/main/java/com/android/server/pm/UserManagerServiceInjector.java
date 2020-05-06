package com.android.server.pm;

import miui.securityspace.XSpaceUserHandle;

public class UserManagerServiceInjector {
    public static int checkAndGetNewUserId(int flags, int defUserId) {
        return XSpaceUserHandle.checkAndGetXSpaceUserId(flags, defUserId);
    }
}
