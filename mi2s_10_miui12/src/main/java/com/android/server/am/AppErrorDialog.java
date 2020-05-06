package com.android.server.am;

import android.app.ApplicationErrorReport;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

final class AppErrorDialog extends BaseErrorDialog implements View.OnClickListener {
    static int ALREADY_SHOWING = -3;
    static final int APP_INFO = 8;
    static int BACKGROUND_USER = -2;
    static final int CANCEL = 7;
    static int CANT_SHOW = -1;
    static final long DISMISS_TIMEOUT = 300000;
    static final int FORCE_QUIT = 1;
    static final int FORCE_QUIT_AND_REPORT = 2;
    static final int MUTE = 5;
    static final int RESTART = 3;
    static final int TIMEOUT = 6;
    ApplicationErrorReport.CrashInfo mCrashInfo;
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            AppErrorDialog.this.setResult(msg.what);
            if (msg.what == 2) {
                MiuiErrorReport.startKloReportService(AppErrorDialog.this.getContext());
            }
            AppErrorDialog.this.dismiss();
        }
    };
    private final boolean mIsRestartable;
    private CharSequence mName;
    /* access modifiers changed from: private */
    public final ProcessRecord mProc;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(intent.getAction())) {
                AppErrorDialog.this.cancel();
            }
        }
    };
    private final boolean mRepeating;
    /* access modifiers changed from: private */
    public final AppErrorResult mResult;
    /* access modifiers changed from: private */
    public final ActivityManagerService mService;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x0102  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public AppErrorDialog(android.content.Context r19, com.android.server.am.ActivityManagerService r20, com.android.server.am.AppErrorDialog.Data r21) {
        /*
            r18 = this;
            r0 = r18
            r1 = r21
            r18.<init>(r19)
            com.android.server.am.AppErrorDialog$2 r2 = new com.android.server.am.AppErrorDialog$2
            r2.<init>()
            r0.mHandler = r2
            com.android.server.am.AppErrorDialog$3 r2 = new com.android.server.am.AppErrorDialog$3
            r2.<init>()
            r0.mReceiver = r2
            android.content.res.Resources r2 = r19.getResources()
            r3 = r20
            r0.mService = r3
            com.android.server.am.ProcessRecord r4 = r1.proc
            r0.mProc = r4
            com.android.server.am.AppErrorResult r4 = r1.result
            r0.mResult = r4
            boolean r4 = r1.repeating
            r0.mRepeating = r4
            int r4 = r1.taskId
            r5 = -1
            r6 = 0
            r7 = 1
            if (r4 != r5) goto L_0x0034
            boolean r4 = r1.isRestartableForService
            if (r4 == 0) goto L_0x0043
        L_0x0034:
            android.content.ContentResolver r4 = r19.getContentResolver()
            java.lang.String r8 = "show_restart_in_crash_dialog"
            int r4 = android.provider.Settings.Global.getInt(r4, r8, r6)
            if (r4 == 0) goto L_0x0043
            r4 = r7
            goto L_0x0044
        L_0x0043:
            r4 = r6
        L_0x0044:
            r0.mIsRestartable = r4
            android.text.BidiFormatter r4 = android.text.BidiFormatter.getInstance()
            android.app.ApplicationErrorReport$CrashInfo r8 = r1.crash
            r0.mCrashInfo = r8
            com.android.server.am.ProcessRecord r8 = r0.mProc
            com.android.server.am.ProcessRecord$PackageList r8 = r8.pkgList
            int r8 = r8.size()
            r9 = 2
            if (r8 != r7) goto L_0x0097
            android.content.pm.PackageManager r8 = r19.getPackageManager()
            com.android.server.am.ProcessRecord r10 = r0.mProc
            android.content.pm.ApplicationInfo r10 = r10.info
            java.lang.CharSequence r8 = r8.getApplicationLabel(r10)
            r0.mName = r8
            if (r8 == 0) goto L_0x0097
            boolean r8 = r0.mRepeating
            if (r8 == 0) goto L_0x0072
            r8 = 17039483(0x104007b, float:2.4244916E-38)
            goto L_0x0075
        L_0x0072:
            r8 = 17039482(0x104007a, float:2.4244913E-38)
        L_0x0075:
            java.lang.Object[] r10 = new java.lang.Object[r9]
            java.lang.CharSequence r11 = r0.mName
            java.lang.String r11 = r11.toString()
            java.lang.String r11 = r4.unicodeWrap(r11)
            r10[r6] = r11
            com.android.server.am.ProcessRecord r11 = r0.mProc
            android.content.pm.ApplicationInfo r11 = r11.info
            java.lang.String r11 = r11.processName
            java.lang.String r11 = r4.unicodeWrap(r11)
            r10[r7] = r11
            java.lang.String r8 = r2.getString(r8, r10)
            r0.setTitle(r8)
            goto L_0x00be
        L_0x0097:
            com.android.server.am.ProcessRecord r8 = r0.mProc
            java.lang.String r8 = r8.processName
            r0.mName = r8
            boolean r8 = r0.mRepeating
            if (r8 == 0) goto L_0x00a6
            r8 = 17039488(0x1040080, float:2.424493E-38)
            goto L_0x00a9
        L_0x00a6:
            r8 = 17039487(0x104007f, float:2.4244927E-38)
        L_0x00a9:
            java.lang.Object[] r10 = new java.lang.Object[r7]
            java.lang.CharSequence r11 = r0.mName
            java.lang.String r11 = r11.toString()
            java.lang.String r11 = r4.unicodeWrap(r11)
            r10[r6] = r11
            java.lang.String r8 = r2.getString(r8, r10)
            r0.setTitle(r8)
        L_0x00be:
            r0.setCancelable(r7)
            android.os.Handler r8 = r0.mHandler
            r10 = 7
            android.os.Message r8 = r8.obtainMessage(r10)
            r0.setCancelMessage(r8)
            android.view.Window r8 = r18.getWindow()
            android.view.WindowManager$LayoutParams r8 = r8.getAttributes()
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            java.lang.String r11 = "Application Error: "
            r10.append(r11)
            com.android.server.am.ProcessRecord r11 = r0.mProc
            android.content.pm.ApplicationInfo r11 = r11.info
            java.lang.String r11 = r11.processName
            r10.append(r11)
            java.lang.String r10 = r10.toString()
            r8.setTitle(r10)
            int r10 = r8.privateFlags
            r10 = r10 | 272(0x110, float:3.81E-43)
            r8.privateFlags = r10
            android.view.Window r10 = r18.getWindow()
            r10.setAttributes(r8)
            com.android.server.am.ProcessRecord r10 = r0.mProc
            boolean r10 = r10.isPersistent()
            if (r10 == 0) goto L_0x010b
            android.view.Window r10 = r18.getWindow()
            r11 = 2010(0x7da, float:2.817E-42)
            r10.setType(r11)
        L_0x010b:
            android.os.Handler r10 = r0.mHandler
            r11 = 6
            android.os.Message r11 = r10.obtainMessage(r11)
            r12 = 300000(0x493e0, double:1.482197E-318)
            r10.sendMessageDelayed(r11, r12)
            r10 = r19
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r12 = 286130301(0x110e007d, float:1.1201975E-28)
            java.lang.String r12 = r2.getString(r12)
            r11.append(r12)
            java.lang.String r12 = "\n\n"
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            r12 = 286130302(0x110e007e, float:1.1201977E-28)
            java.lang.String r12 = r2.getString(r12)
            android.text.SpannableStringBuilder r13 = new android.text.SpannableStringBuilder
            java.lang.StringBuilder r14 = new java.lang.StringBuilder
            r14.<init>()
            r14.append(r11)
            r14.append(r12)
            java.lang.String r14 = r14.toString()
            r13.<init>(r14)
            com.android.server.am.AppErrorDialog$1 r14 = new com.android.server.am.AppErrorDialog$1
            r14.<init>(r10)
            int r15 = r11.length()
            int r16 = r11.length()
            int r17 = r12.length()
            int r7 = r16 + r17
            r5 = 33
            r13.setSpan(r14, r15, r7, r5)
            android.widget.TextView r5 = new android.widget.TextView
            r5.<init>(r10)
            r5.setText(r13)
            android.content.res.Resources r7 = r19.getResources()
            r14 = 285605925(0x11060025, float:1.0570781E-28)
            float r7 = r7.getDimension(r14)
            r5.setTextSize(r7)
            android.content.res.Resources r7 = r19.getResources()
            r14 = 285540378(0x1105001a, float:1.0491881E-28)
            int r7 = r7.getColor(r14)
            r5.setTextColor(r7)
            android.text.method.MovementMethod r7 = android.text.method.LinkMovementMethod.getInstance()
            r5.setMovementMethod(r7)
            r0.setView(r5)
            r0.setCancelable(r6)
            android.content.res.Resources r6 = r19.getResources()
            r7 = 286130213(0x110e0025, float:1.1201869E-28)
            java.lang.CharSequence r6 = r6.getText(r7)
            android.os.Handler r7 = r0.mHandler
            android.os.Message r7 = r7.obtainMessage(r9)
            r9 = -1
            r0.setButton(r9, r6, r7)
            r6 = -2
            android.content.res.Resources r7 = r19.getResources()
            r9 = 17039360(0x1040000, float:2.424457E-38)
            java.lang.CharSequence r7 = r7.getText(r9)
            android.os.Handler r9 = r0.mHandler
            r14 = 1
            android.os.Message r9 = r9.obtainMessage(r14)
            r0.setButton(r6, r7, r9)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.AppErrorDialog.<init>(android.content.Context, com.android.server.am.ActivityManagerService, com.android.server.am.AppErrorDialog$Data):void");
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onStart() {
        super.onStart();
        getContext().registerReceiver(this.mReceiver, new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        super.onStop();
        getContext().unregisterReceiver(this.mReceiver);
    }

    public void dismiss() {
        if (!this.mResult.mHasResult) {
            setResult(1);
        }
        super.dismiss();
    }

    /* access modifiers changed from: private */
    public void setResult(int result) {
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                if (this.mProc != null && this.mProc.crashDialog == this) {
                    this.mProc.crashDialog = null;
                }
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        this.mResult.set(result);
        this.mHandler.removeMessages(6);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case 16908706:
                this.mHandler.obtainMessage(1).sendToTarget();
                return;
            case 16908707:
                this.mHandler.obtainMessage(5).sendToTarget();
                return;
            case 16908708:
                this.mHandler.obtainMessage(2).sendToTarget();
                return;
            case 16908709:
                this.mHandler.obtainMessage(3).sendToTarget();
                return;
            default:
                return;
        }
    }

    static class Data {
        ApplicationErrorReport.CrashInfo crash;
        boolean isRestartableForService;
        ProcessRecord proc;
        boolean repeating;
        AppErrorResult result;
        int taskId;

        Data() {
        }
    }
}
