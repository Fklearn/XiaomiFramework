package com.miui.networkassistant.utils;

import android.content.Context;
import android.widget.Toast;
import com.miui.securitycenter.R;

public class ToastUtil {
    private static String getToastTextBySlot(Context context, int i, String str) {
        if (!DeviceUtil.IS_DUAL_CARD) {
            return str;
        }
        int i2 = i == 0 ? R.string.dual_setting_simcard1 : R.string.dual_setting_simcard2;
        return context.getString(i2) + str;
    }

    public static void makeToastText(Context context, int i, int i2) {
        makeToastText(context, i, context.getString(i2));
    }

    public static void makeToastText(Context context, int i, String str) {
        String toastTextBySlot = getToastTextBySlot(context, i, str);
        if (PackageUtil.isRunningForeground(context, context.getPackageName())) {
            Toast.makeText(context, toastTextBySlot, 1).show();
        }
    }

    public static void showCorrectionSucceed(Context context, int i, int i2) {
        String toastTextBySlot = getToastTextBySlot(context, i, context.getString(i2));
        if (PackageUtil.isRunningForeground(context, context.getPackageName())) {
            Toast.makeText(context, toastTextBySlot, 1).show();
        } else {
            NotificationUtil.sendCorrectionAlertNotify(context, toastTextBySlot, context.getString(R.string.traffic_correction_success_notify_message), i);
        }
    }
}
