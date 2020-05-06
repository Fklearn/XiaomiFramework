package com.miui.analytics;

import android.content.Context;
import android.provider.Settings;

public class DeviceInfoConfig {
    static final String PREF_KEY_LATEST_ACCELERATOR_INFO = "key_latest_accelerator_info";
    static final String PREF_KEY_LATEST_BATTERY_STATUS = "key_latest_battery_status";
    static final String PREF_KEY_LATEST_GYROSCOPE_INFO = "key_latest_gyroscope_info";
    static final String PREF_KEY_LATEST_MAGNETIC_INFO = "key_latest_magnetic_info";
    static final String PREF_KEY_LATEST_WIFI_SSID_BSSID = "key_latest_wifi_ssid_bssid";
    private static final String UNKNOWN = "null;null;null;null;null;null;null;null;null";

    public static String getAcceleratorInfo(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), PREF_KEY_LATEST_ACCELERATOR_INFO);
        return string == null ? UNKNOWN : string;
    }

    public static String getBatteryStatus(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), PREF_KEY_LATEST_BATTERY_STATUS);
        return string == null ? UNKNOWN : string;
    }

    public static String getGyroscopeInfo(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), PREF_KEY_LATEST_GYROSCOPE_INFO);
        return string == null ? UNKNOWN : string;
    }

    public static String getMagneticInfo(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), PREF_KEY_LATEST_MAGNETIC_INFO);
        return string == null ? UNKNOWN : string;
    }

    public static String getWifiSSIDAndBSSID(Context context) {
        String string = Settings.Secure.getString(context.getContentResolver(), PREF_KEY_LATEST_WIFI_SSID_BSSID);
        return string == null ? UNKNOWN : string;
    }

    public static void setAcceleratorInfo(Context context, String str) {
        Settings.Secure.putString(context.getContentResolver(), PREF_KEY_LATEST_ACCELERATOR_INFO, str);
    }

    public static void setBatteryStatus(Context context, String str) {
        Settings.Secure.putString(context.getContentResolver(), PREF_KEY_LATEST_BATTERY_STATUS, str);
    }

    public static void setGyroscopeInfo(Context context, String str) {
        Settings.Secure.putString(context.getContentResolver(), PREF_KEY_LATEST_GYROSCOPE_INFO, str);
    }

    public static void setMagneticInfo(Context context, String str) {
        Settings.Secure.putString(context.getContentResolver(), PREF_KEY_LATEST_MAGNETIC_INFO, str);
    }

    public static void setWifiSSIDAndBSSID(Context context, String str) {
        Settings.Secure.putString(context.getContentResolver(), PREF_KEY_LATEST_WIFI_SSID_BSSID, str);
    }
}
