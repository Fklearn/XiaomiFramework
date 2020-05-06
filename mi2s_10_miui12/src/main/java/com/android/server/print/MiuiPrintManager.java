package com.android.server.print;

import android.content.Intent;
import android.print.PrintJobInfo;
import android.text.TextUtils;
import android.util.Log;

public class MiuiPrintManager {
    public static final String MIUI_ACTION_PRINT_DIALOG = "miui.print.PRINT_DIALOG";
    public static final String MIUI_PREFIX = "MIUI:";
    public static final String TAG = "MiuiPrintManager";

    public static boolean ensureInjected(PrintJobInfo info) {
        String jobName = info.getLabel();
        if (TextUtils.isEmpty(jobName) || !jobName.startsWith(MIUI_PREFIX)) {
            return false;
        }
        info.setLabel(jobName.substring(MIUI_PREFIX.length()));
        return true;
    }

    public static void printInject(boolean injected, Intent intent) {
        if (injected) {
            intent.setAction(MIUI_ACTION_PRINT_DIALOG);
            Log.d(TAG, "printInject....will handle for MIUI target.");
        }
    }
}
