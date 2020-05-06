package com.android.server.am;

public class ActivityManagerServiceCompat {
    static boolean isUserRunning(ActivityManagerService ams, int userId) {
        return ams.isUserRunning(userId, 0);
    }
}
