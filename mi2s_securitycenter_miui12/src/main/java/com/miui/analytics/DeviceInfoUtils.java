package com.miui.analytics;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import b.b.b.d.c;
import b.b.b.d.k;
import b.b.o.g.e;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import java.util.ArrayList;
import java.util.Arrays;

public class DeviceInfoUtils {
    private static final String TAG = "DeviceInfoUtils";
    private static Handler mMainHandler = new Handler(Looper.getMainLooper());

    /* access modifiers changed from: private */
    public static String join(Iterable<?> iterable, String str) {
        try {
            return (String) e.a(Class.forName("org.apache.miui.commons.lang3.StringUtils"), String.class, "join", (Class<?>[]) new Class[]{Iterable.class, String.class}, iterable, str);
        } catch (Exception e) {
            Log.e(TAG, "join exception: ", e);
            return "";
        }
    }

    public static void saveAcceleratorInfo(final Context context) {
        try {
            final StringBuilder sb = new StringBuilder();
            final SensorManager sensorManager = (SensorManager) context.getSystemService("sensor");
            final AnonymousClass4 r2 = new SensorEventListener() {
                public void onAccuracyChanged(Sensor sensor, int i) {
                }

                public void onSensorChanged(SensorEvent sensorEvent) {
                    if (TextUtils.isEmpty(sb) && sensorEvent.sensor.getType() == 1) {
                        float[] fArr = sensorEvent.values;
                        float f = fArr[0];
                        float f2 = fArr[1];
                        float f3 = fArr[2];
                        StringBuilder sb = sb;
                        sb.append(f + "," + f2 + "," + f3);
                        ArrayList arrayList = new ArrayList(Arrays.asList(DeviceInfoConfig.getAcceleratorInfo(context).split(";")));
                        arrayList.add(0, sb.toString());
                        if (arrayList.size() >= 9) {
                            DeviceInfoConfig.setAcceleratorInfo(context, DeviceInfoUtils.join(arrayList.subList(0, 9), ";"));
                        }
                    }
                }
            };
            sensorManager.registerListener(r2, sensorManager.getDefaultSensor(1, true), 0);
            mMainHandler.postDelayed(new Runnable() {
                public void run() {
                    sensorManager.unregisterListener(r2);
                }
            }, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        } catch (Exception e) {
            Log.e(TAG, "getAcceleratorInfo ", e);
        }
    }

    public static void saveBatteryStatus(Context context) {
        try {
            Intent registerReceiver = context.registerReceiver(new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                }
            }, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
            if (registerReceiver != null) {
                boolean z = true;
                boolean z2 = registerReceiver.getIntExtra("plugged", 0) != 0;
                int intExtra = registerReceiver.getIntExtra("status", -1);
                if ((intExtra != 5 && intExtra != 2) || !z2) {
                    z = false;
                }
                ArrayList arrayList = new ArrayList(Arrays.asList(DeviceInfoConfig.getBatteryStatus(context).split(";")));
                arrayList.add(0, String.valueOf(z));
                if (arrayList.size() >= 9) {
                    DeviceInfoConfig.setBatteryStatus(context, join(arrayList.subList(0, 9), ";"));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "saveBatteryStatus error ", e);
        }
    }

    public static void saveGyroscopeInfo(final Context context) {
        try {
            final StringBuilder sb = new StringBuilder();
            final SensorManager sensorManager = (SensorManager) context.getSystemService("sensor");
            final AnonymousClass1 r2 = new SensorEventListener() {
                public void onAccuracyChanged(Sensor sensor, int i) {
                }

                public void onSensorChanged(SensorEvent sensorEvent) {
                    if (TextUtils.isEmpty(sb) && sensorEvent.sensor.getType() == 4) {
                        float[] fArr = sensorEvent.values;
                        float f = fArr[0];
                        float f2 = fArr[1];
                        float f3 = fArr[2];
                        StringBuilder sb = sb;
                        sb.append("(" + f + "," + f2 + "," + f3 + ")");
                        ArrayList arrayList = new ArrayList(Arrays.asList(DeviceInfoConfig.getGyroscopeInfo(context).split(";")));
                        arrayList.add(0, sb.toString());
                        if (arrayList.size() >= 9) {
                            DeviceInfoConfig.setGyroscopeInfo(context, DeviceInfoUtils.join(arrayList.subList(0, 9), ";"));
                        }
                    }
                }
            };
            sensorManager.registerListener(r2, sensorManager.getDefaultSensor(4, true), 0);
            mMainHandler.postDelayed(new Runnable() {
                public void run() {
                    sensorManager.unregisterListener(r2);
                }
            }, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        } catch (Exception e) {
            Log.e(TAG, "saveGyroscopeInfo error ", e);
        }
    }

    public static void saveMagneticInfo(final Context context) {
        try {
            final StringBuilder sb = new StringBuilder();
            final SensorManager sensorManager = (SensorManager) context.getSystemService("sensor");
            final AnonymousClass6 r2 = new SensorEventListener() {
                public void onAccuracyChanged(Sensor sensor, int i) {
                }

                public void onSensorChanged(SensorEvent sensorEvent) {
                    if (TextUtils.isEmpty(sb) && sensorEvent.sensor.getType() == 2) {
                        float[] fArr = sensorEvent.values;
                        float f = fArr[0];
                        float f2 = fArr[1];
                        float f3 = fArr[2];
                        StringBuilder sb = sb;
                        sb.append(f + "," + f2 + "," + f3);
                        ArrayList arrayList = new ArrayList(Arrays.asList(DeviceInfoConfig.getMagneticInfo(context).split(";")));
                        arrayList.add(0, sb.toString());
                        if (arrayList.size() >= 9) {
                            DeviceInfoConfig.setMagneticInfo(context, DeviceInfoUtils.join(arrayList.subList(0, 9), ";"));
                        }
                    }
                }
            };
            sensorManager.registerListener(r2, sensorManager.getDefaultSensor(2, true), 0);
            mMainHandler.postDelayed(new Runnable() {
                public void run() {
                    sensorManager.unregisterListener(r2);
                }
            }, DefaultRenderersFactory.DEFAULT_ALLOWED_VIDEO_JOINING_TIME_MS);
        } catch (Exception e) {
            Log.e(TAG, "error when getMagneticInfo : ", e);
        }
    }

    public static void saveWifiSSIDAndBSSID(Context context) {
        try {
            if (k.b(context)) {
                WifiInfo connectionInfo = ((WifiManager) context.getApplicationContext().getSystemService("wifi")).getConnectionInfo();
                String bssid = connectionInfo.getBSSID();
                String ssid = connectionInfo.getSSID();
                ArrayList arrayList = new ArrayList(Arrays.asList(DeviceInfoConfig.getWifiSSIDAndBSSID(context).split(";")));
                arrayList.add(0, "(" + c.b(ssid) + "," + c.b(bssid) + ")");
                if (arrayList.size() >= 9) {
                    DeviceInfoConfig.setWifiSSIDAndBSSID(context, join(arrayList.subList(0, 9), ";"));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "saveWifiSSIDAndBSSID error ", e);
        }
    }
}
