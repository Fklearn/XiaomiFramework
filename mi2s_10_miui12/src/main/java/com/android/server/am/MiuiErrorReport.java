package com.android.server.am;

import android.app.ApplicationErrorReport;
import android.content.Context;
import android.content.Intent;
import android.os.UserHandle;
import miui.util.ErrorReport;

public class MiuiErrorReport {
    private static final String DUMP_KLO_BUGREPORT = "miui.intent.action.DUMP_KLO_BUGREPORT";
    private static final String EXTRA_FC_PREVIEW = "extra_fc_report";
    private static final String FC_PREVIEW = "miui.intent.action.FC_PREVIEW";
    private static final String TAG = "MiuiErrorReport";

    public static void sendFcErrorReport(Context context, ProcessRecord proc, ApplicationErrorReport.CrashInfo crashInfo) {
        try {
            ErrorReport.sendExceptionReport(context, proc.info.packageName, crashInfo, 1);
        } catch (Exception e) {
        }
    }

    public static void sendAnrErrorReport(Context context, ProcessRecord proc, boolean mandatory) {
        try {
            ErrorReport.sendAnrReport(context, proc.notRespondingReport, 1);
        } catch (Exception e) {
        }
    }

    public static void startKloReportService(Context context) {
        Intent intent = new Intent();
        intent.setAction(DUMP_KLO_BUGREPORT);
        context.sendBroadcast(intent);
    }

    public static void startFcPreviewActivity(Context context, String packageName, ApplicationErrorReport.CrashInfo crashInfo) {
        Intent intent = new Intent();
        intent.setAction(FC_PREVIEW);
        intent.putExtra(EXTRA_FC_PREVIEW, ErrorReport.getExceptionData(context, packageName, crashInfo).toString());
        intent.setFlags(268435456);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivityAsUser(intent, UserHandle.CURRENT);
        }
    }
}
