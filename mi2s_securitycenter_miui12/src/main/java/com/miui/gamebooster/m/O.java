package com.miui.gamebooster.m;

import android.net.IMiuiNetworkManager;
import android.os.IBinder;
import android.util.Log;
import b.b.o.g.e;

public class O {
    public static void a(int i) {
        try {
            boolean z = true;
            IMiuiNetworkManager asInterface = IMiuiNetworkManager.Stub.asInterface((IBinder) e.a(e.a(Class.forName("android.os.INetworkManagementService$Stub"), "asInterface", (Class<?>[]) new Class[]{IBinder.class}, (IBinder) e.a(Class.forName("android.os.ServiceManager"), IBinder.class, "getService", (Class<?>[]) new Class[]{String.class}, (String) e.a(Class.forName("android.content.Context"), "NETWORKMANAGEMENT_SERVICE", String.class))), "getMiuiNetworkManager", (Class<?>[]) null, new Object[0]));
            Log.i("GameBoosterService", "setNetworkTrafficPolicy:" + String.valueOf(asInterface.setNetworkTrafficPolicy(i)));
            if (C0388t.k()) {
                StringBuilder sb = new StringBuilder();
                sb.append("setRpsStatus:");
                if (i == 0) {
                    z = false;
                }
                sb.append(String.valueOf(asInterface.setRpsStatus(z)));
                Log.i("GameBoosterService", sb.toString());
            }
        } catch (Exception e) {
            Log.i("GameBoosterReflectUtils", e.toString());
        }
    }
}
