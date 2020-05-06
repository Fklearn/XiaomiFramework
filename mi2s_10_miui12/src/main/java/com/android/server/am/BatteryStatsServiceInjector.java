package com.android.server.am;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import com.android.internal.os.BatteryStatsImpl;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class BatteryStatsServiceInjector {
    private static BroadcastReceiver sBroadcastReceiver;
    /* access modifiers changed from: private */
    public static boolean sFirstRecord;
    /* access modifiers changed from: private */
    public static boolean sScreenState;

    private BatteryStatsServiceInjector() {
    }

    public static void registerReceiver(Context context, Handler handler, final BatteryStatsImpl batteryStatsImpl) {
        if (sBroadcastReceiver == null) {
            sFirstRecord = true;
            sScreenState = true;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.SCREEN_ON");
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            sBroadcastReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    boolean screenOn;
                    if (intent != null && BatteryStatsServiceInjector.sScreenState != (screenOn = "android.intent.action.SCREEN_ON".equals(intent.getAction()))) {
                        try {
                            CpuTimeCollection.updateUidCpuTime(batteryStatsImpl, screenOn, BatteryStatsServiceInjector.sFirstRecord);
                            boolean unused = BatteryStatsServiceInjector.sFirstRecord = false;
                        } catch (Exception e) {
                            CpuTimeCollection.resetCpuTimeModule();
                            boolean unused2 = BatteryStatsServiceInjector.sFirstRecord = true;
                        }
                        boolean unused3 = BatteryStatsServiceInjector.sScreenState = screenOn;
                    }
                }
            };
            context.registerReceiver(sBroadcastReceiver, intentFilter, (String) null, handler);
        }
    }

    public static void unRegisterReceiver(Context context) {
        BroadcastReceiver broadcastReceiver = sBroadcastReceiver;
        if (broadcastReceiver != null) {
            context.unregisterReceiver(broadcastReceiver);
            sBroadcastReceiver = null;
        }
    }

    public static void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        writer.println("dump Uid SourceCollection:");
        CpuTimeCollection.dump(fd, writer, args);
    }
}
