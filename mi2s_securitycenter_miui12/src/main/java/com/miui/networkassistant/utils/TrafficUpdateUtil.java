package com.miui.networkassistant.utils;

import android.content.Context;
import android.content.Intent;
import b.b.c.j.B;
import b.b.c.j.g;
import com.miui.networkassistant.config.Constants;

public class TrafficUpdateUtil {
    public static void broadCastTrafficUpdated(Context context) {
        g.a(context, new Intent(Constants.App.ACTION_NETWORK_POLICY_UPDATE), B.e(B.c()), Constants.App.PERMISSION_EXTRA_NETWORK);
    }
}
