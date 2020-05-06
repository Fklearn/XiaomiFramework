package com.miui.powercenter.legacypowerrank;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.miui.networkassistant.ui.activity.NetworkDiagnosticsTipActivity;
import com.miui.securitycenter.R;
import com.miui.warningcenter.mijia.MijiaAlertModel;

public class g {
    public static void a(Context context, BatteryData batteryData, double d2) {
        double[] dArr;
        int[] iArr;
        double[] dArr2;
        Bundle bundle = new Bundle();
        bundle.putString(NetworkDiagnosticsTipActivity.TITLE_KEY_NAME, b.a(context, batteryData));
        bundle.putFloat("percent", (float) d2);
        bundle.putString("iconPackage", batteryData.getPackageName());
        bundle.putInt("iconId", b.a(batteryData));
        if (batteryData.getUid() >= 0) {
            bundle.putInt(MijiaAlertModel.KEY_UID, batteryData.getUid());
        }
        bundle.putInt("drainType", batteryData.drainType);
        int i = batteryData.drainType;
        if (i == 1) {
            iArr = new int[]{R.string.usage_type_on_time, R.string.usage_type_no_coverage};
            dArr = new double[]{(double) batteryData.usageTime, batteryData.noCoveragePercent};
        } else if (i != 6) {
            if (i == 3) {
                iArr = new int[]{R.string.usage_type_wifi_running, R.string.usage_type_cpu, R.string.usage_type_cpu_foreground, R.string.usage_type_wake_lock, R.string.usage_type_data_send, R.string.usage_type_data_recv};
                dArr2 = new double[]{(double) batteryData.usageTime, (double) batteryData.cpuTime, (double) batteryData.cpuFgTime, (double) batteryData.wakeLockTime, (double) batteryData.mobileTxBytes, (double) batteryData.mobileRxBytes};
            } else if (i != 4) {
                iArr = new int[]{R.string.usage_type_on_time};
                dArr = new double[]{(double) batteryData.usageTime};
            } else {
                iArr = new int[]{R.string.usage_type_on_time, R.string.usage_type_cpu, R.string.usage_type_cpu_foreground, R.string.usage_type_wake_lock, R.string.usage_type_data_send, R.string.usage_type_data_recv};
                dArr2 = new double[]{(double) batteryData.usageTime, (double) batteryData.cpuTime, (double) batteryData.cpuFgTime, (double) batteryData.wakeLockTime, (double) batteryData.mobileTxBytes, (double) batteryData.mobileRxBytes};
            }
            dArr = dArr2;
        } else {
            int[] iArr2 = {R.string.usage_type_cpu, R.string.usage_type_cpu_foreground, R.string.usage_type_wake_lock, R.string.usage_type_gps, R.string.usage_type_wifi_running, R.string.usage_type_data_send, R.string.usage_type_data_recv, R.string.usage_type_audio, R.string.usage_type_video};
            dArr = new double[]{(double) batteryData.cpuTime, (double) batteryData.cpuFgTime, (double) batteryData.wakeLockTime, (double) batteryData.gpsTime, (double) batteryData.wifiRunningTime, (double) batteryData.mobileTxBytes, (double) batteryData.mobileRxBytes, 0.0d, 0.0d};
            iArr = iArr2;
        }
        bundle.putIntArray("types", iArr);
        bundle.putDoubleArray("values", dArr);
        Intent intent = new Intent(context, PowerDetailActivity.class);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }
}
