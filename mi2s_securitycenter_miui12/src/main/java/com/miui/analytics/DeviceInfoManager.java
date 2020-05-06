package com.miui.analytics;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

public class DeviceInfoManager {
    private static final String TAG = "DeviceInfoManager";
    private static Handler mMainHandler = new Handler(Looper.getMainLooper());

    public static void handleTask(final Context context, boolean z) {
        Log.w(TAG, "handleTask:");
        if (!TextUtils.isEmpty(Settings.Secure.getString(context.getContentResolver(), "key_latest_gps_info"))) {
            Settings.Secure.putString(context.getContentResolver(), "key_latest_gps_info", (String) null);
        }
        if (z) {
            final HandlerThread handlerThread = new HandlerThread("device_info_thread");
            handlerThread.start();
            new Handler(handlerThread.getLooper()).post(new Runnable() {
                public void run() {
                    DeviceInfoUtils.saveGyroscopeInfo(context);
                    DeviceInfoUtils.saveWifiSSIDAndBSSID(context);
                    DeviceInfoUtils.saveBatteryStatus(context);
                    DeviceInfoUtils.saveAcceleratorInfo(context);
                    DeviceInfoUtils.saveMagneticInfo(context);
                    handlerThread.quitSafely();
                }
            });
        }
        mMainHandler.postDelayed(new Runnable() {
            public void run() {
                DeviceInfoManager.handleTask(context, true);
            }
        }, 28800000);
    }
}
