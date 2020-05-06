package com.miui.networkassistant.monitor.impl;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import b.b.c.j.B;
import b.b.c.j.g;
import com.miui.networkassistant.config.Constants;
import com.miui.networkassistant.monitor.GolbalReceiver;
import com.miui.networkassistant.monitor.IMonitor;
import com.miui.networkassistant.service.FirewallService;
import com.miui.networkassistant.service.tm.TrafficManageService;

public class BootMonitor implements IMonitor {
    public void invoke(Context context, Intent intent) {
        Log.i("BootMonitor", String.format("invoked", new Object[]{intent.getAction()}));
        g.c(context, new Intent(context, FirewallService.class), B.k());
        g.c(context, new Intent(context, TrafficManageService.class), B.k());
    }

    public void register() {
        GolbalReceiver.addMonitor(Constants.System.ACTION_BOOT_COMPLETED, this);
    }
}
