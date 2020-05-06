package com.miui.networkassistant.monitor.impl;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.monitor.GolbalReceiver;
import com.miui.networkassistant.monitor.IMonitor;

public class ImsiMonitor implements IMonitor {
    private static final String TAG = "ImsiMonitor";

    public void invoke(Context context, Intent intent) {
        Log.i(TAG, String.format("invoked:%s", new Object[]{intent.getAction()}));
    }

    public void register() {
        GolbalReceiver.addMonitor(Constants.System.ACTION_SIM_STATE_CHANGED, this);
    }
}
