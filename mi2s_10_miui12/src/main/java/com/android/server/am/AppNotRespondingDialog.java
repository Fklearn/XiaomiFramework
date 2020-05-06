package com.android.server.am;

import android.app.ApplicationErrorReport;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Slog;
import android.view.View;
import com.android.internal.logging.MetricsLogger;

public final class AppNotRespondingDialog extends BaseErrorDialog implements View.OnClickListener {
    public static final int ALREADY_SHOWING = -2;
    public static final int CANT_SHOW = -1;
    static final int FORCE_CLOSE = 1;
    private static final String TAG = "AppNotRespondingDialog";
    static final int WAIT = 2;
    static final int WAIT_AND_REPORT = 3;
    private final Handler mHandler = new Handler() {
        /* JADX WARNING: type inference failed for: r3v2, types: [android.app.Dialog, com.android.server.am.AppNotRespondingDialog] */
        public void handleMessage(Message msg) {
            Intent appErrorIntent = null;
            MetricsLogger.action(AppNotRespondingDialog.this.getContext(), 317, msg.what);
            int i = msg.what;
            if (i == 1) {
                AppNotRespondingDialog.this.mService.killAppAtUsersRequest(AppNotRespondingDialog.this.mProc, AppNotRespondingDialog.this);
            } else if (i == 2 || i == 3) {
                synchronized (AppNotRespondingDialog.this.mService) {
                    try {
                        ActivityManagerService.boostPriorityForLockedSection();
                        ProcessRecord app = AppNotRespondingDialog.this.mProc;
                        if (msg.what == 3) {
                            appErrorIntent = AppNotRespondingDialog.this.mService.mAppErrors.createAppErrorIntentLocked(app, System.currentTimeMillis(), (ApplicationErrorReport.CrashInfo) null);
                        }
                        app.setNotResponding(false);
                        app.notRespondingReport = null;
                        if (app.anrDialog == AppNotRespondingDialog.this) {
                            app.anrDialog = null;
                        }
                        AppNotRespondingDialog.this.mService.mServices.scheduleServiceTimeoutLocked(app);
                    } catch (Throwable th) {
                        while (true) {
                            ActivityManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
            }
            if (appErrorIntent != null) {
                try {
                    AppNotRespondingDialog.this.getContext().startActivity(appErrorIntent);
                } catch (ActivityNotFoundException e) {
                    Slog.w(AppNotRespondingDialog.TAG, "bug report receiver dissappeared", e);
                }
            }
            AppNotRespondingDialog.this.dismiss();
        }
    };
    /* access modifiers changed from: private */
    public final ProcessRecord mProc;
    /* access modifiers changed from: private */
    public final ActivityManagerService mService;

    /* JADX WARNING: Removed duplicated region for block: B:16:0x006b  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0086  */
    /* JADX WARNING: Removed duplicated region for block: B:20:0x00c1  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x00d7  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public AppNotRespondingDialog(com.android.server.am.ActivityManagerService r11, android.content.Context r12, com.android.server.am.AppNotRespondingDialog.Data r13) {
        /*
            r10 = this;
            r10.<init>(r12)
            com.android.server.am.AppNotRespondingDialog$1 r0 = new com.android.server.am.AppNotRespondingDialog$1
            r0.<init>()
            r10.mHandler = r0
            r10.mService = r11
            com.android.server.am.ProcessRecord r0 = r13.proc
            r10.mProc = r0
            android.content.res.Resources r0 = r12.getResources()
            r1 = 0
            r10.setCancelable(r1)
            android.content.pm.ApplicationInfo r2 = r13.aInfo
            if (r2 == 0) goto L_0x0027
            android.content.pm.ApplicationInfo r2 = r13.aInfo
            android.content.pm.PackageManager r3 = r12.getPackageManager()
            java.lang.CharSequence r2 = r2.loadLabel(r3)
            goto L_0x0028
        L_0x0027:
            r2 = 0
        L_0x0028:
            r3 = 0
            com.android.server.am.ProcessRecord r4 = r10.mProc
            com.android.server.am.ProcessRecord$PackageList r4 = r4.pkgList
            int r4 = r4.size()
            r5 = 1
            if (r4 != r5) goto L_0x0053
            android.content.pm.PackageManager r4 = r12.getPackageManager()
            com.android.server.am.ProcessRecord r6 = r10.mProc
            android.content.pm.ApplicationInfo r6 = r6.info
            java.lang.CharSequence r4 = r4.getApplicationLabel(r6)
            r3 = r4
            if (r4 == 0) goto L_0x0053
            if (r2 == 0) goto L_0x004a
            r4 = 17039512(0x1040098, float:2.4244997E-38)
            goto L_0x0064
        L_0x004a:
            r2 = r3
            com.android.server.am.ProcessRecord r4 = r10.mProc
            java.lang.String r3 = r4.processName
            r4 = 17039514(0x104009a, float:2.4245003E-38)
            goto L_0x0064
        L_0x0053:
            if (r2 == 0) goto L_0x005d
            com.android.server.am.ProcessRecord r4 = r10.mProc
            java.lang.String r3 = r4.processName
            r4 = 17039513(0x1040099, float:2.4245E-38)
            goto L_0x0064
        L_0x005d:
            com.android.server.am.ProcessRecord r4 = r10.mProc
            java.lang.String r2 = r4.processName
            r4 = 17039515(0x104009b, float:2.4245005E-38)
        L_0x0064:
            android.text.BidiFormatter r6 = android.text.BidiFormatter.getInstance()
            r7 = 2
            if (r3 == 0) goto L_0x0086
            java.lang.Object[] r8 = new java.lang.Object[r7]
            java.lang.String r9 = r2.toString()
            java.lang.String r9 = r6.unicodeWrap(r9)
            r8[r1] = r9
            java.lang.String r1 = r3.toString()
            java.lang.String r1 = r6.unicodeWrap(r1)
            r8[r5] = r1
            java.lang.String r1 = r0.getString(r4, r8)
            goto L_0x0096
        L_0x0086:
            java.lang.Object[] r8 = new java.lang.Object[r5]
            java.lang.String r9 = r2.toString()
            java.lang.String r9 = r6.unicodeWrap(r9)
            r8[r1] = r9
            java.lang.String r1 = r0.getString(r4, r8)
        L_0x0096:
            r10.setTitle(r1)
            r1 = -1
            r8 = 286130200(0x110e0018, float:1.1201854E-28)
            java.lang.CharSequence r8 = r0.getText(r8)
            android.os.Handler r9 = r10.mHandler
            android.os.Message r5 = r9.obtainMessage(r5)
            r10.setButton(r1, r8, r5)
            r1 = -2
            r5 = 286130229(0x110e0035, float:1.1201889E-28)
            java.lang.CharSequence r5 = r0.getText(r5)
            android.os.Handler r8 = r10.mHandler
            android.os.Message r7 = r8.obtainMessage(r7)
            r10.setButton(r1, r5, r7)
            com.android.server.am.ProcessRecord r1 = r10.mProc
            android.content.ComponentName r1 = r1.errorReportReceiver
            if (r1 == 0) goto L_0x00d3
            r1 = -3
            r5 = 286130213(0x110e0025, float:1.1201869E-28)
            java.lang.CharSequence r5 = r0.getText(r5)
            android.os.Handler r7 = r10.mHandler
            r8 = 3
            android.os.Message r7 = r7.obtainMessage(r8)
            r10.setButton(r1, r5, r7)
        L_0x00d3:
            boolean r1 = r13.aboveSystem
            if (r1 == 0) goto L_0x00e0
            android.view.Window r1 = r10.getWindow()
            r5 = 2010(0x7da, float:2.817E-42)
            r1.setType(r5)
        L_0x00e0:
            android.view.Window r1 = r10.getWindow()
            android.view.WindowManager$LayoutParams r1 = r1.getAttributes()
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r7 = "Application Not Responding: "
            r5.append(r7)
            com.android.server.am.ProcessRecord r7 = r10.mProc
            android.content.pm.ApplicationInfo r7 = r7.info
            java.lang.String r7 = r7.processName
            r5.append(r7)
            java.lang.String r5 = r5.toString()
            r1.setTitle(r5)
            r5 = 272(0x110, float:3.81E-43)
            r1.privateFlags = r5
            android.view.Window r5 = r10.getWindow()
            r5.setAttributes(r1)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.AppNotRespondingDialog.<init>(com.android.server.am.ActivityManagerService, android.content.Context, com.android.server.am.AppNotRespondingDialog$Data):void");
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onClick(View v) {
        int id = v.getId();
        if (id == 16908706) {
            this.mHandler.obtainMessage(1).sendToTarget();
        } else if (id == 16908708) {
            this.mHandler.obtainMessage(3).sendToTarget();
        } else if (id == 16908710) {
            this.mHandler.obtainMessage(2).sendToTarget();
        }
    }

    public static class Data {
        final ApplicationInfo aInfo;
        final boolean aboveSystem;
        final ProcessRecord proc;

        public Data(ProcessRecord proc2, ApplicationInfo aInfo2, boolean aboveSystem2) {
            this.proc = proc2;
            this.aInfo = aInfo2;
            this.aboveSystem = aboveSystem2;
        }
    }
}
