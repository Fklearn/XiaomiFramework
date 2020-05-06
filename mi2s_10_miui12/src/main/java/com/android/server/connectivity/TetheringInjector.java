package com.android.server.connectivity;

import android.app.Notification;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothPan;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.miui.R;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import com.android.internal.notification.SystemNotificationChannels;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicReference;

public class TetheringInjector {
    private static final String DISABLE_TETHERING_ACTION = "com.android.server.connectivity.Tethering.DisableTetheringAction";
    private static final String TAG = "Tethering";
    /* access modifiers changed from: private */
    public static AtomicReference<BluetoothPan> sBluetoothPan = new AtomicReference<>();
    private static BroadcastReceiver sDisableTetheringActionReceiver;
    private static Handler sHandler;
    private static boolean sIsRegisted;
    private static HashSet<String> sNotifyChannelSet = new HashSet<>();
    private static BluetoothProfile.ServiceListener sProfileServiceListener = new BluetoothProfile.ServiceListener() {
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            TetheringInjector.sBluetoothPan.set((BluetoothPan) proxy);
        }

        public void onServiceDisconnected(int profile) {
            if (TetheringInjector.sBluetoothPan.get() != null) {
                try {
                    BluetoothAdapter.getDefaultAdapter().closeProfileProxy(5, (BluetoothProfile) TetheringInjector.sBluetoothPan.get());
                } catch (RuntimeException e) {
                    Log.w(TetheringInjector.TAG, "Error cleaning up PAN proxy", e);
                }
            }
            TetheringInjector.sBluetoothPan.set((Object) null);
        }
    };

    public static boolean init() {
        if (sHandler != null && sDisableTetheringActionReceiver != null) {
            return true;
        }
        HandlerThread ht = new HandlerThread(TAG);
        ht.start();
        sHandler = new Handler(ht.getLooper());
        sDisableTetheringActionReceiver = new DisableTetheringActionReceiver();
        return true;
    }

    public static void setTetheredNotification(Context context, Notification.Builder notificationBuilder) {
        setTetheredNotification(context, notificationBuilder, SystemNotificationChannels.NETWORK_STATUS);
    }

    public static void setTetheredNotification(Context context, Notification.Builder notificationBuilder, String notificationChannel) {
        sNotifyChannelSet.add(notificationChannel);
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter != null) {
            adapter.getProfileProxy(context, sProfileServiceListener, 5);
        }
        if (!sIsRegisted) {
            context.registerReceiver(sDisableTetheringActionReceiver, new IntentFilter(DISABLE_TETHERING_ACTION), (String) null, sHandler);
            sIsRegisted = true;
        }
        notificationBuilder.setActions(new Notification.Action[]{new Notification.Action(R.drawable.float_notification_button_bg, context.getString(R.string.turn_off), PendingIntent.getBroadcast(context, 0, new Intent(DISABLE_TETHERING_ACTION), 0))});
        Bundle bundle = new Bundle();
        bundle.putBoolean("miui.showAction", true);
        notificationBuilder.setExtras(bundle);
        notificationBuilder.setPriority(2);
    }

    public static class DisableTetheringActionReceiver extends BroadcastReceiver {
        public void onReceive(Context context, Intent intent) {
            WifiManager wm = (WifiManager) context.getSystemService("wifi");
            if (wm != null && wm.isWifiApEnabled()) {
                wm.stopSoftAp();
            }
            if (TetheringInjector.sBluetoothPan.get() != null && ((BluetoothPan) TetheringInjector.sBluetoothPan.get()).isTetheringOn()) {
                ((BluetoothPan) TetheringInjector.sBluetoothPan.get()).setBluetoothTethering(false);
            }
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService("connectivity");
            if (cm != null) {
                cm.setUsbTethering(false);
            }
        }
    }

    public static void notificationFinished(Context context) {
        notificationFinished(context, SystemNotificationChannels.NETWORK_STATUS);
    }

    public static void notificationFinished(Context context, String notificationChannel) {
        sNotifyChannelSet.remove(notificationChannel);
        if (sIsRegisted && sNotifyChannelSet.isEmpty()) {
            context.unregisterReceiver(sDisableTetheringActionReceiver);
            sIsRegisted = false;
        }
    }
}
