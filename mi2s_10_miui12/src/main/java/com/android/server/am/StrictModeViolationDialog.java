package com.android.server.am;

import android.os.Handler;
import android.os.Message;

final class StrictModeViolationDialog extends BaseErrorDialog {
    static final int ACTION_OK = 0;
    static final int ACTION_OK_AND_REPORT = 1;
    static final long DISMISS_TIMEOUT = 60000;
    private static final String TAG = "StrictModeViolationDialog";
    private final Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            synchronized (StrictModeViolationDialog.this.mService) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    if (StrictModeViolationDialog.this.mProc != null && StrictModeViolationDialog.this.mProc.crashDialog == StrictModeViolationDialog.this) {
                        StrictModeViolationDialog.this.mProc.crashDialog = null;
                    }
                } catch (Throwable th) {
                    while (true) {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            StrictModeViolationDialog.this.mResult.set(msg.what);
            StrictModeViolationDialog.this.dismiss();
        }
    };
    /* access modifiers changed from: private */
    public final ProcessRecord mProc;
    /* access modifiers changed from: private */
    public final AppErrorResult mResult;
    /* access modifiers changed from: private */
    public final ActivityManagerService mService;

    /* JADX WARNING: Removed duplicated region for block: B:8:0x0071  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public StrictModeViolationDialog(android.content.Context r8, com.android.server.am.ActivityManagerService r9, com.android.server.am.AppErrorResult r10, com.android.server.am.ProcessRecord r11) {
        /*
            r7 = this;
            r7.<init>(r8)
            com.android.server.am.StrictModeViolationDialog$1 r0 = new com.android.server.am.StrictModeViolationDialog$1
            r0.<init>()
            r7.mHandler = r0
            android.content.res.Resources r0 = r8.getResources()
            r7.mService = r9
            r7.mProc = r11
            r7.mResult = r10
            com.android.server.am.ProcessRecord$PackageList r1 = r11.pkgList
            int r1 = r1.size()
            r2 = 1
            r3 = 0
            if (r1 != r2) goto L_0x0045
            android.content.pm.PackageManager r1 = r8.getPackageManager()
            android.content.pm.ApplicationInfo r4 = r11.info
            java.lang.CharSequence r1 = r1.getApplicationLabel(r4)
            r4 = r1
            if (r1 == 0) goto L_0x0045
            r1 = 17041152(0x1040700, float:2.4249593E-38)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]
            java.lang.String r6 = r4.toString()
            r5[r3] = r6
            android.content.pm.ApplicationInfo r6 = r11.info
            java.lang.String r6 = r6.processName
            r5[r2] = r6
            java.lang.String r1 = r0.getString(r1, r5)
            r7.setMessage(r1)
            goto L_0x0059
        L_0x0045:
            java.lang.String r4 = r11.processName
            r1 = 17041153(0x1040701, float:2.4249596E-38)
            java.lang.Object[] r5 = new java.lang.Object[r2]
            java.lang.String r6 = r4.toString()
            r5[r3] = r6
            java.lang.String r1 = r0.getString(r1, r5)
            r7.setMessage(r1)
        L_0x0059:
            r7.setCancelable(r3)
            r1 = -1
            r5 = 17039910(0x1040226, float:2.4246112E-38)
            java.lang.CharSequence r5 = r0.getText(r5)
            android.os.Handler r6 = r7.mHandler
            android.os.Message r6 = r6.obtainMessage(r3)
            r7.setButton(r1, r5, r6)
            android.content.ComponentName r1 = r11.errorReportReceiver
            if (r1 == 0) goto L_0x0082
            r1 = -2
            r5 = 17041016(0x1040678, float:2.4249212E-38)
            java.lang.CharSequence r5 = r0.getText(r5)
            android.os.Handler r6 = r7.mHandler
            android.os.Message r2 = r6.obtainMessage(r2)
            r7.setButton(r1, r5, r2)
        L_0x0082:
            android.view.Window r1 = r7.getWindow()
            r2 = 256(0x100, float:3.59E-43)
            r1.addPrivateFlags(r2)
            android.view.Window r1 = r7.getWindow()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r5 = "Strict Mode Violation: "
            r2.append(r5)
            android.content.pm.ApplicationInfo r5 = r11.info
            java.lang.String r5 = r5.processName
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            r1.setTitle(r2)
            android.os.Handler r1 = r7.mHandler
            android.os.Message r2 = r1.obtainMessage(r3)
            r5 = 60000(0xea60, double:2.9644E-319)
            r1.sendMessageDelayed(r2, r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.StrictModeViolationDialog.<init>(android.content.Context, com.android.server.am.ActivityManagerService, com.android.server.am.AppErrorResult, com.android.server.am.ProcessRecord):void");
    }
}
