package com.miui.powercenter.legacypowerrank;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.util.Log;
import com.miui.securitycenter.R;

public class b {
    public static int a(BatteryData batteryData) {
        int i = batteryData.drainType;
        if (i == 0) {
            return R.drawable.power_consume_idle;
        }
        if (i == 1) {
            return R.drawable.power_consume_signal;
        }
        if (i == 2) {
            return R.drawable.power_consume_call;
        }
        if (i == 3) {
            return R.drawable.power_consume_wifi;
        }
        if (i == 4) {
            return R.drawable.power_consume_bluetooth;
        }
        if (i == 5) {
            return R.drawable.power_consume_screen;
        }
        if (i == 10) {
            return R.drawable.power_consume_other;
        }
        if (i == 11) {
            return R.drawable.power_consume_ambient_display;
        }
        if (batteryData.getUid() == 0) {
            return R.drawable.ic_power_system;
        }
        return 0;
    }

    public static String a(Context context, int i) {
        int i2;
        if (i == 10) {
            i2 = R.string.battery_desc_other_apps;
        } else if (i != 11) {
            switch (i) {
                case 0:
                    i2 = R.string.battery_desc_standby;
                    break;
                case 1:
                    i2 = R.string.battery_desc_radio;
                    break;
                case 2:
                    i2 = R.string.battery_desc_voice;
                    break;
                case 3:
                    i2 = R.string.battery_desc_wifi;
                    break;
                case 4:
                    i2 = R.string.battery_desc_bluetooth;
                    break;
                case 5:
                    i2 = R.string.battery_desc_display;
                    break;
                case 6:
                    i2 = R.string.battery_desc_apps;
                    break;
                default:
                    return "";
            }
        } else {
            i2 = R.string.battery_desc_ambient_display;
        }
        return context.getString(i2);
    }

    public static String a(Context context, BatteryData batteryData) {
        int i;
        int i2 = batteryData.drainType;
        if (i2 == 10) {
            i = R.string.power_consume_other;
        } else if (i2 != 11) {
            switch (i2) {
                case 0:
                    i = R.string.power_idle;
                    break;
                case 1:
                    i = R.string.power_cell;
                    break;
                case 2:
                    i = R.string.power_phone;
                    break;
                case 3:
                    i = R.string.power_wifi;
                    break;
                case 4:
                    i = R.string.power_bluetooth;
                    break;
                case 5:
                    i = R.string.power_screen;
                    break;
                case 6:
                    return b(context, batteryData);
                default:
                    return "Unknown";
            }
        } else {
            i = R.string.power_ambient_display;
        }
        return context.getString(i);
    }

    private static String b(Context context, BatteryData batteryData) {
        Resources resources;
        int i;
        if (batteryData.getUid() == 0) {
            return "";
        }
        if (batteryData.getUid() == 1013) {
            resources = context.getResources();
            i = R.string.process_mediaserver_label;
        } else if (batteryData.getUid() == 1041) {
            return "";
        } else {
            if ("com.miui.vpnsdkmanager".equals(batteryData.defaultPackageName)) {
                resources = context.getResources();
                i = R.string.process_vpnsdkmanager_label;
            } else if (!TextUtils.isEmpty(batteryData.name)) {
                return b(batteryData) ? "" : batteryData.name;
            } else {
                Log.i("BatterySipperResourceH", "uid " + batteryData.uid + "default package name " + batteryData.defaultPackageName);
                return "";
            }
        }
        return resources.getString(i);
    }

    private static boolean b(BatteryData batteryData) {
        if (batteryData.getUid() != 1000) {
            return false;
        }
        String str = batteryData.name;
        return "system_server".equalsIgnoreCase(str) || "dex2oat".equalsIgnoreCase(str) || "surfaceflinger".equalsIgnoreCase(str) || str.startsWith("android.hardware") || "audioserver".equalsIgnoreCase(str) || str.startsWith("vendor.xiaomi") || str.startsWith("vendor.qti") || ".dataservices".equalsIgnoreCase(str) || str.startsWith("com.qualcomm") || str.startsWith("com.samsung") || "servicemanager".equalsIgnoreCase(str) || "hwservicemanager".equalsIgnoreCase(str);
    }
}
