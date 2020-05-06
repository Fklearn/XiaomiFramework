package com.android.server.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.INetworkManagementService;
import java.io.FileDescriptor;
import java.io.PrintWriter;

public class NetworkStatsServiceInjector {
    private static BroadcastReceiver sBroadcastReceiver;
    private static INetworkManagementService sNetworkManager;
    /* access modifiers changed from: private */
    public static boolean sScreenState;

    private NetworkStatsServiceInjector() {
    }

    public static void registerReceiver(Context context, Handler handler, INetworkManagementService networkManager) {
        if (sBroadcastReceiver == null) {
            sScreenState = true;
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction("android.intent.action.SCREEN_ON");
            intentFilter.addAction("android.intent.action.SCREEN_OFF");
            sBroadcastReceiver = new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    boolean screenOn;
                    if (intent != null) {
                        if ("android.intent.action.SCREEN_ON".equals(intent.getAction())) {
                            screenOn = true;
                        } else {
                            screenOn = false;
                        }
                        if (NetworkStatsServiceInjector.sScreenState != screenOn) {
                            NetworkStatsServiceInjector.updateForScreenChanged(screenOn);
                            boolean unused = NetworkStatsServiceInjector.sScreenState = screenOn;
                        }
                    }
                }
            };
            sNetworkManager = networkManager;
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

    public static void updateForScreenChanged(boolean screenOn) {
        NetworkStatsActualCollection.updateNetworkStats(sNetworkManager, screenOn);
    }

    public static void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        writer.println("dump Uid SourceCollection:");
        NetworkStatsActualCollection.dump(fd, writer, args);
    }
}
