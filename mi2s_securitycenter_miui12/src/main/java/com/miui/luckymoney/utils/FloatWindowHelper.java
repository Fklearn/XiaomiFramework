package com.miui.luckymoney.utils;

import android.content.Context;
import android.content.Intent;
import b.b.c.j.B;
import com.miui.luckymoney.config.Constants;

public class FloatWindowHelper {
    public static void removeFloatWindow(Context context) {
        Intent intent = new Intent(Constants.ACTION_CONFIG_CHANGED_BROADCAST);
        intent.putExtra(Constants.KEY_CONFIG_CHANGED_FLAG, Constants.TYPE_REMOVE_FLOAT_WINDOW);
        context.sendBroadcastAsUser(intent, B.b());
    }

    public static void showFloatWindow(Context context) {
        Intent intent = new Intent(Constants.ACTION_CONFIG_CHANGED_BROADCAST);
        intent.putExtra(Constants.KEY_CONFIG_CHANGED_FLAG, Constants.TYPE_SHOW_FLOAT_WINDOW);
        context.sendBroadcastAsUser(intent, B.b());
    }
}
