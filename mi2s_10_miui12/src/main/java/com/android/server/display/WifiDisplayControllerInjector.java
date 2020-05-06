package com.android.server.display;

import android.content.Context;
import android.content.IntentFilter;
import android.os.Handler;

class WifiDisplayControllerInjector {
    private static DisplayProjectionReceiver mReceiver;

    WifiDisplayControllerInjector() {
    }

    public static void registerProjectionReceiver(Context context, Handler handler, WifiDisplayController controller) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(DisplayProjectionReceiver.ACTION_START_PROJECTION);
        filter.addAction(DisplayProjectionReceiver.ACTION_STOP_PROJECTION);
        mReceiver = new DisplayProjectionReceiver(context, handler, controller);
        context.registerReceiver(mReceiver, filter);
    }
}
