package com.android.server.connectivity;

import android.content.Context;

public class VpnInjector {
    static boolean isSpecialUser(Context context, int parentUserId, int userId) {
        if (parentUserId < 0 || userId < 0 || parentUserId != 0 || userId != 999) {
            return false;
        }
        return true;
    }
}
