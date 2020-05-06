package com.android.server.power;

import android.app.Dialog;
import android.app.IActivityManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.os.Bundle;
import android.os.FileUtils;
import android.os.Handler;
import android.os.PowerManager;
import android.os.RecoverySystem;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.UserManager;
import android.server.am.SplitScreenReporter;
import android.util.ArrayMap;
import android.util.Log;
import android.util.TimingsTraceLog;
import com.android.server.LocalServices;
import com.android.server.RescueParty;
import com.android.server.content.SyncStorageEngine;
import com.android.server.job.controllers.JobStatus;
import com.android.server.pm.PackageManagerService;
import com.android.server.pm.Settings;
import com.android.server.statusbar.StatusBarManagerInternal;
import java.io.File;
import java.io.IOException;
import miui.app.AlertDialog;

public final class ShutdownThread extends Thread {
    private static final int ACTION_DONE_POLL_WAIT_MS = 500;
    private static final int ACTIVITY_MANAGER_STOP_PERCENT = 4;
    private static final int BROADCAST_STOP_PERCENT = 2;
    private static final int MAX_BROADCAST_TIME = 10000;
    private static final int MAX_RADIO_WAIT_TIME = 12000;
    private static final int MAX_SHUTDOWN_WAIT_TIME = 20000;
    private static final int MAX_UNCRYPT_WAIT_TIME = 900000;
    private static final String METRICS_FILE_BASENAME = "/data/system/shutdown-metrics";
    private static String METRIC_AM = "shutdown_activity_manager";
    private static String METRIC_PM = "shutdown_package_manager";
    /* access modifiers changed from: private */
    public static String METRIC_RADIO = "shutdown_radio";
    private static String METRIC_RADIOS = "shutdown_radios";
    private static String METRIC_SEND_BROADCAST = "shutdown_send_shutdown_broadcast";
    private static String METRIC_SHUTDOWN_TIME_START = "begin_shutdown";
    private static String METRIC_SYSTEM_SERVER = "shutdown_system_server";
    private static final int MOUNT_SERVICE_STOP_PERCENT = 20;
    private static final int PACKAGE_MANAGER_STOP_PERCENT = 6;
    private static final int RADIOS_STATE_POLL_SLEEP_MS = 100;
    private static final int RADIO_STOP_PERCENT = 18;
    public static final String REBOOT_SAFEMODE_PROPERTY = "persist.sys.safemode";
    public static final String RO_SAFEMODE_PROPERTY = "ro.sys.safemode";
    public static final String SHUTDOWN_ACTION_PROPERTY = "sys.shutdown.requested";
    private static final int SHUTDOWN_VIBRATE_MS = 0;
    private static final String TAG = "ShutdownThread";
    /* access modifiers changed from: private */
    public static final ArrayMap<String, Long> TRON_METRICS = new ArrayMap<>();
    private static final int VENDOR_SUBSYS_MAX_WAIT_MS = 10000;
    private static final int VENDOR_SUBSYS_STATE_CHECK_INTERVAL_MS = 100;
    private static final AudioAttributes VIBRATION_ATTRIBUTES = new AudioAttributes.Builder().setContentType(4).setUsage(13).build();
    private static String mReason;
    private static boolean mReboot;
    /* access modifiers changed from: private */
    public static boolean mRebootHasProgressBar;
    private static boolean mRebootSafeMode;
    private static AlertDialog sConfirmDialog;
    /* access modifiers changed from: private */
    public static final ShutdownThread sInstance = new ShutdownThread();
    private static boolean sIsStarted = false;
    private static final Object sIsStartedGuard = new Object();
    private boolean mActionDone;
    private final Object mActionDoneSync = new Object();
    /* access modifiers changed from: private */
    public Context mContext;
    private PowerManager.WakeLock mCpuWakeLock;
    private Handler mHandler;
    private PowerManager mPowerManager;
    /* access modifiers changed from: private */
    public ProgressDialog mProgressDialog;
    private PowerManager.WakeLock mScreenWakeLock;

    private ShutdownThread() {
    }

    public static void shutdown(Context context, String reason, boolean confirm) {
        mReboot = false;
        mRebootSafeMode = false;
        mReason = reason;
        shutdownInner(context, confirm);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0021, code lost:
        if (mRebootSafeMode == false) goto L_0x0027;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:11:0x0023, code lost:
        r1 = 17040987;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0028, code lost:
        if (r0 != 2) goto L_0x002e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x002a, code lost:
        r1 = 17041118;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x002e, code lost:
        r1 = 17041117;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0031, code lost:
        android.util.Log.d(TAG, "Notifying thread to start shutdown longPressBehavior=" + r0);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x0048, code lost:
        if (r7 == false) goto L_0x00a0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x004a, code lost:
        r2 = new com.android.server.power.ShutdownThread.CloseDialogReceiver(r6);
        r3 = sConfirmDialog;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0051, code lost:
        if (r3 == null) goto L_0x0056;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0053, code lost:
        r3.dismiss();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0056, code lost:
        r3 = new miui.app.AlertDialog.Builder(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x005d, code lost:
        if (mRebootSafeMode == false) goto L_0x0063;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x005f, code lost:
        r4 = 17040988;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0063, code lost:
        r4 = 17040969;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0066, code lost:
        sConfirmDialog = r3.setTitle(r4).setMessage(r1).setPositiveButton(17039379, new com.android.server.power.ShutdownThread.AnonymousClass1()).setNegativeButton(17039369, (android.content.DialogInterface.OnClickListener) null).create();
        r3 = sConfirmDialog;
        r2.dialog = r3;
        r3.setOnDismissListener(r2);
        sConfirmDialog.getWindow().setType(2009);
        sConfirmDialog.show();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x00a0, code lost:
        beginShutdownSequence(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:35:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0014, code lost:
        r0 = r6.getResources().getInteger(17694824);
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void shutdownInner(final android.content.Context r6, boolean r7) {
        /*
            r6.assertRuntimeOverlayThemable()
            java.lang.Object r0 = sIsStartedGuard
            monitor-enter(r0)
            boolean r1 = sIsStarted     // Catch:{ all -> 0x00a4 }
            if (r1 == 0) goto L_0x0013
            java.lang.String r1 = "ShutdownThread"
            java.lang.String r2 = "Request to shutdown already running, returning."
            android.util.Log.d(r1, r2)     // Catch:{ all -> 0x00a4 }
            monitor-exit(r0)     // Catch:{ all -> 0x00a4 }
            return
        L_0x0013:
            monitor-exit(r0)     // Catch:{ all -> 0x00a4 }
            android.content.res.Resources r0 = r6.getResources()
            r1 = 17694824(0x10e0068, float:2.6081572E-38)
            int r0 = r0.getInteger(r1)
            boolean r1 = mRebootSafeMode
            if (r1 == 0) goto L_0x0027
            r1 = 17040987(0x104065b, float:2.424913E-38)
            goto L_0x0031
        L_0x0027:
            r1 = 2
            if (r0 != r1) goto L_0x002e
            r1 = 17041118(0x10406de, float:2.4249498E-38)
            goto L_0x0031
        L_0x002e:
            r1 = 17041117(0x10406dd, float:2.4249495E-38)
        L_0x0031:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Notifying thread to start shutdown longPressBehavior="
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "ShutdownThread"
            android.util.Log.d(r3, r2)
            if (r7 == 0) goto L_0x00a0
            com.android.server.power.ShutdownThread$CloseDialogReceiver r2 = new com.android.server.power.ShutdownThread$CloseDialogReceiver
            r2.<init>(r6)
            miui.app.AlertDialog r3 = sConfirmDialog
            if (r3 == 0) goto L_0x0056
            r3.dismiss()
        L_0x0056:
            miui.app.AlertDialog$Builder r3 = new miui.app.AlertDialog$Builder
            r3.<init>(r6)
            boolean r4 = mRebootSafeMode
            if (r4 == 0) goto L_0x0063
            r4 = 17040988(0x104065c, float:2.4249134E-38)
            goto L_0x0066
        L_0x0063:
            r4 = 17040969(0x1040649, float:2.424908E-38)
        L_0x0066:
            miui.app.AlertDialog$Builder r3 = r3.setTitle(r4)
            miui.app.AlertDialog$Builder r3 = r3.setMessage(r1)
            r4 = 17039379(0x1040013, float:2.4244624E-38)
            com.android.server.power.ShutdownThread$1 r5 = new com.android.server.power.ShutdownThread$1
            r5.<init>(r6)
            miui.app.AlertDialog$Builder r3 = r3.setPositiveButton(r4, r5)
            r4 = 17039369(0x1040009, float:2.4244596E-38)
            r5 = 0
            miui.app.AlertDialog$Builder r3 = r3.setNegativeButton(r4, r5)
            miui.app.AlertDialog r3 = r3.create()
            sConfirmDialog = r3
            miui.app.AlertDialog r3 = sConfirmDialog
            r2.dialog = r3
            r3.setOnDismissListener(r2)
            miui.app.AlertDialog r3 = sConfirmDialog
            android.view.Window r3 = r3.getWindow()
            r4 = 2009(0x7d9, float:2.815E-42)
            r3.setType(r4)
            miui.app.AlertDialog r3 = sConfirmDialog
            r3.show()
            goto L_0x00a3
        L_0x00a0:
            beginShutdownSequence(r6)
        L_0x00a3:
            return
        L_0x00a4:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00a4 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.ShutdownThread.shutdownInner(android.content.Context, boolean):void");
    }

    private static class CloseDialogReceiver extends BroadcastReceiver implements DialogInterface.OnDismissListener {
        public Dialog dialog;
        private Context mContext;

        CloseDialogReceiver(Context context) {
            this.mContext = context;
            context.registerReceiver(this, new IntentFilter("android.intent.action.CLOSE_SYSTEM_DIALOGS"));
        }

        public void onReceive(Context context, Intent intent) {
            this.dialog.cancel();
        }

        public void onDismiss(DialogInterface unused) {
            this.mContext.unregisterReceiver(this);
        }
    }

    public static void reboot(Context context, String reason, boolean confirm) {
        mReboot = true;
        mRebootSafeMode = false;
        mRebootHasProgressBar = false;
        mReason = reason;
        shutdownInner(context, confirm);
    }

    public static void rebootSafeMode(Context context, boolean confirm) {
        if (!((UserManager) context.getSystemService("user")).hasUserRestriction("no_safe_boot")) {
            mReboot = true;
            mRebootSafeMode = true;
            mRebootHasProgressBar = false;
            mReason = null;
            shutdownInner(context, confirm);
        }
    }

    private static ProgressDialog showShutdownDialog(Context context) {
        ProgressDialog pd = new ProgressDialog(context);
        String str = mReason;
        if (str == null || !str.startsWith("recovery-update")) {
            String str2 = mReason;
            if (str2 == null || !str2.equals("recovery")) {
                if (showSysuiReboot()) {
                    return null;
                }
                pd.setTitle(context.getText(17040969));
                pd.setMessage(context.getText(17041119));
                pd.setIndeterminate(true);
            } else if (RescueParty.isAttemptingFactoryReset()) {
                pd.setTitle(context.getText(17040969));
                pd.setMessage(context.getText(17041119));
                pd.setIndeterminate(true);
            } else {
                pd.setTitle(context.getText(17040991));
                pd.setMessage(context.getText(17040990));
                pd.setIndeterminate(true);
            }
        } else {
            mRebootHasProgressBar = RecoverySystem.UNCRYPT_PACKAGE_FILE.exists() && !RecoverySystem.BLOCK_MAP_FILE.exists();
            pd.setTitle(context.getText(17040995));
            if (mRebootHasProgressBar) {
                pd.setMax(100);
                pd.setProgress(0);
                pd.setIndeterminate(false);
                pd.setProgressNumberFormat((String) null);
                pd.setProgressStyle(1);
                pd.setMessage(context.getText(17040993));
            } else if (showSysuiReboot()) {
                return null;
            } else {
                pd.setIndeterminate(true);
                pd.setMessage(context.getText(17040994));
            }
        }
        pd.setCancelable(false);
        pd.getWindow().setType(2009);
        ShutdownThreadInjector.showShutdownAnimOrDialog(context, mReboot);
        return pd;
    }

    private static boolean showSysuiReboot() {
        Log.d(TAG, "Attempting to use SysUI shutdown UI");
        try {
            if (((StatusBarManagerInternal) LocalServices.getService(StatusBarManagerInternal.class)).showShutdownUi(mReboot, mReason)) {
                Log.d(TAG, "SysUI handling shutdown UI");
                return true;
            }
        } catch (Exception e) {
        }
        Log.d(TAG, "SysUI is unavailable");
        return false;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:10:0x0014, code lost:
        sInstance.mProgressDialog = showShutdownDialog(r6);
        r0 = sInstance;
        r0.mContext = r6;
        r0.mPowerManager = (android.os.PowerManager) r6.getSystemService("power");
        r0 = sInstance;
        r0.mCpuWakeLock = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:12:?, code lost:
        r0.mCpuWakeLock = r0.mPowerManager.newWakeLock(1, "ShutdownThread-cpu");
        sInstance.mCpuWakeLock.setReferenceCounted(false);
        sInstance.mCpuWakeLock.acquire();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x004a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x004b, code lost:
        android.util.Log.w(TAG, "No permission to acquire wake lock", r0);
        sInstance.mCpuWakeLock = null;
     */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x0093  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void beginShutdownSequence(android.content.Context r6) {
        /*
            java.lang.Object r0 = sIsStartedGuard
            monitor-enter(r0)
            boolean r1 = sIsStarted     // Catch:{ all -> 0x00aa }
            if (r1 == 0) goto L_0x0010
            java.lang.String r1 = "ShutdownThread"
            java.lang.String r2 = "Shutdown sequence already running, returning."
            android.util.Log.d(r1, r2)     // Catch:{ all -> 0x00aa }
            monitor-exit(r0)     // Catch:{ all -> 0x00aa }
            return
        L_0x0010:
            r1 = 1
            sIsStarted = r1     // Catch:{ all -> 0x00aa }
            monitor-exit(r0)     // Catch:{ all -> 0x00aa }
            com.android.server.power.ShutdownThread r0 = sInstance
            android.app.ProgressDialog r2 = showShutdownDialog(r6)
            r0.mProgressDialog = r2
            com.android.server.power.ShutdownThread r0 = sInstance
            r0.mContext = r6
            java.lang.String r2 = "power"
            java.lang.Object r2 = r6.getSystemService(r2)
            android.os.PowerManager r2 = (android.os.PowerManager) r2
            r0.mPowerManager = r2
            com.android.server.power.ShutdownThread r0 = sInstance
            r2 = 0
            r0.mCpuWakeLock = r2
            r3 = 0
            android.os.PowerManager r4 = r0.mPowerManager     // Catch:{ SecurityException -> 0x004a }
            java.lang.String r5 = "ShutdownThread-cpu"
            android.os.PowerManager$WakeLock r1 = r4.newWakeLock(r1, r5)     // Catch:{ SecurityException -> 0x004a }
            r0.mCpuWakeLock = r1     // Catch:{ SecurityException -> 0x004a }
            com.android.server.power.ShutdownThread r0 = sInstance     // Catch:{ SecurityException -> 0x004a }
            android.os.PowerManager$WakeLock r0 = r0.mCpuWakeLock     // Catch:{ SecurityException -> 0x004a }
            r0.setReferenceCounted(r3)     // Catch:{ SecurityException -> 0x004a }
            com.android.server.power.ShutdownThread r0 = sInstance     // Catch:{ SecurityException -> 0x004a }
            android.os.PowerManager$WakeLock r0 = r0.mCpuWakeLock     // Catch:{ SecurityException -> 0x004a }
            r0.acquire()     // Catch:{ SecurityException -> 0x004a }
            goto L_0x0056
        L_0x004a:
            r0 = move-exception
            java.lang.String r1 = "ShutdownThread"
            java.lang.String r4 = "No permission to acquire wake lock"
            android.util.Log.w(r1, r4, r0)
            com.android.server.power.ShutdownThread r1 = sInstance
            r1.mCpuWakeLock = r2
        L_0x0056:
            com.android.server.power.ShutdownThread r0 = sInstance
            r0.mScreenWakeLock = r2
            android.os.PowerManager r0 = r0.mPowerManager
            boolean r0 = r0.isScreenOn()
            if (r0 == 0) goto L_0x008d
            com.android.server.power.ShutdownThread r0 = sInstance     // Catch:{ SecurityException -> 0x0081 }
            com.android.server.power.ShutdownThread r1 = sInstance     // Catch:{ SecurityException -> 0x0081 }
            android.os.PowerManager r1 = r1.mPowerManager     // Catch:{ SecurityException -> 0x0081 }
            r4 = 26
            java.lang.String r5 = "ShutdownThread-screen"
            android.os.PowerManager$WakeLock r1 = r1.newWakeLock(r4, r5)     // Catch:{ SecurityException -> 0x0081 }
            r0.mScreenWakeLock = r1     // Catch:{ SecurityException -> 0x0081 }
            com.android.server.power.ShutdownThread r0 = sInstance     // Catch:{ SecurityException -> 0x0081 }
            android.os.PowerManager$WakeLock r0 = r0.mScreenWakeLock     // Catch:{ SecurityException -> 0x0081 }
            r0.setReferenceCounted(r3)     // Catch:{ SecurityException -> 0x0081 }
            com.android.server.power.ShutdownThread r0 = sInstance     // Catch:{ SecurityException -> 0x0081 }
            android.os.PowerManager$WakeLock r0 = r0.mScreenWakeLock     // Catch:{ SecurityException -> 0x0081 }
            r0.acquire()     // Catch:{ SecurityException -> 0x0081 }
            goto L_0x008d
        L_0x0081:
            r0 = move-exception
            java.lang.String r1 = "ShutdownThread"
            java.lang.String r4 = "No permission to acquire wake lock"
            android.util.Log.w(r1, r4, r0)
            com.android.server.power.ShutdownThread r1 = sInstance
            r1.mScreenWakeLock = r2
        L_0x008d:
            boolean r0 = android.app.admin.SecurityLog.isLoggingEnabled()
            if (r0 == 0) goto L_0x009b
            r0 = 210010(0x3345a, float:2.94287E-40)
            java.lang.Object[] r1 = new java.lang.Object[r3]
            android.app.admin.SecurityLog.writeEvent(r0, r1)
        L_0x009b:
            com.android.server.power.ShutdownThread r0 = sInstance
            com.android.server.power.ShutdownThread$2 r1 = new com.android.server.power.ShutdownThread$2
            r1.<init>()
            r0.mHandler = r1
            com.android.server.power.ShutdownThread r0 = sInstance
            r0.start()
            return
        L_0x00aa:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x00aa }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.ShutdownThread.beginShutdownSequence(android.content.Context):void");
    }

    /* access modifiers changed from: package-private */
    public void actionDone() {
        synchronized (this.mActionDoneSync) {
            this.mActionDone = true;
            this.mActionDoneSync.notifyAll();
        }
    }

    public void run() {
        TimingsTraceLog shutdownTimingLog = newTimingsLog();
        shutdownTimingLog.traceBegin("SystemServerShutdown");
        metricShutdownStart();
        metricStarted(METRIC_SYSTEM_SERVER);
        BroadcastReceiver br = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                ShutdownThread.this.actionDone();
            }
        };
        StringBuilder sb = new StringBuilder();
        sb.append(mReboot ? SplitScreenReporter.ACTION_ENTER_SPLIT : "0");
        String str = mReason;
        if (str == null) {
            str = "";
        }
        sb.append(str);
        SystemProperties.set(SHUTDOWN_ACTION_PROPERTY, sb.toString());
        if (mRebootSafeMode) {
            SystemProperties.set(REBOOT_SAFEMODE_PROPERTY, SplitScreenReporter.ACTION_ENTER_SPLIT);
        }
        metricStarted(METRIC_SEND_BROADCAST);
        shutdownTimingLog.traceBegin("SendShutdownBroadcast");
        Log.i(TAG, "Sending shutdown broadcast...");
        this.mActionDone = false;
        Intent intent = new Intent("android.intent.action.ACTION_SHUTDOWN");
        intent.addFlags(1342177280);
        this.mContext.sendOrderedBroadcastAsUser(intent, UserHandle.ALL, (String) null, br, this.mHandler, 0, (String) null, (Bundle) null);
        long endTime = SystemClock.elapsedRealtime() + JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY;
        synchronized (this.mActionDoneSync) {
            while (true) {
                if (!this.mActionDone) {
                    long delay = endTime - SystemClock.elapsedRealtime();
                    if (delay <= 0) {
                        Log.w(TAG, "Shutdown broadcast timed out");
                        break;
                    }
                    if (mRebootHasProgressBar) {
                        sInstance.setRebootProgress((int) (((((double) (JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY - delay)) * 1.0d) * 2.0d) / 10000.0d), (CharSequence) null);
                    }
                    try {
                        this.mActionDoneSync.wait(Math.min(delay, 500));
                    } catch (InterruptedException e) {
                    }
                }
            }
        }
        if (mRebootHasProgressBar) {
            sInstance.setRebootProgress(2, (CharSequence) null);
        }
        shutdownTimingLog.traceEnd();
        metricEnded(METRIC_SEND_BROADCAST);
        Log.i(TAG, "Shutting down activity manager...");
        shutdownTimingLog.traceBegin("ShutdownActivityManager");
        metricStarted(METRIC_AM);
        IActivityManager am = IActivityManager.Stub.asInterface(ServiceManager.checkService("activity"));
        if (am != null) {
            try {
                am.shutdown(10000);
            } catch (RemoteException e2) {
            }
        }
        if (mRebootHasProgressBar) {
            sInstance.setRebootProgress(4, (CharSequence) null);
        }
        shutdownTimingLog.traceEnd();
        metricEnded(METRIC_AM);
        Log.i(TAG, "Shutting down package manager...");
        shutdownTimingLog.traceBegin("ShutdownPackageManager");
        metricStarted(METRIC_PM);
        PackageManagerService pm = (PackageManagerService) ServiceManager.getService(Settings.ATTR_PACKAGE);
        if (pm != null) {
            pm.shutdown();
        }
        if (mRebootHasProgressBar) {
            sInstance.setRebootProgress(6, (CharSequence) null);
        }
        shutdownTimingLog.traceEnd();
        metricEnded(METRIC_PM);
        shutdownTimingLog.traceBegin("ShutdownRadios");
        metricStarted(METRIC_RADIOS);
        shutdownRadios(MAX_RADIO_WAIT_TIME);
        if (mRebootHasProgressBar) {
            sInstance.setRebootProgress(18, (CharSequence) null);
        }
        shutdownTimingLog.traceEnd();
        metricEnded(METRIC_RADIOS);
        if (mRebootHasProgressBar) {
            sInstance.setRebootProgress(20, (CharSequence) null);
            if (!SystemProperties.get("vold.miui.uncrypt").equals(SyncStorageEngine.MESG_SUCCESS)) {
                uncrypt();
            }
        }
        shutdownTimingLog.traceEnd();
        metricEnded(METRIC_SYSTEM_SERVER);
        saveMetrics(mReboot, mReason);
        rebootOrShutdown(this.mContext, mReboot, mReason);
    }

    /* access modifiers changed from: private */
    public static TimingsTraceLog newTimingsLog() {
        return new TimingsTraceLog("ShutdownTiming", 524288);
    }

    /* access modifiers changed from: private */
    public static void metricStarted(String metricKey) {
        synchronized (TRON_METRICS) {
            TRON_METRICS.put(metricKey, Long.valueOf(SystemClock.elapsedRealtime() * -1));
        }
    }

    /* access modifiers changed from: private */
    public static void metricEnded(String metricKey) {
        synchronized (TRON_METRICS) {
            TRON_METRICS.put(metricKey, Long.valueOf(SystemClock.elapsedRealtime() + TRON_METRICS.get(metricKey).longValue()));
        }
    }

    private static void metricShutdownStart() {
        synchronized (TRON_METRICS) {
            TRON_METRICS.put(METRIC_SHUTDOWN_TIME_START, Long.valueOf(System.currentTimeMillis()));
        }
    }

    /* access modifiers changed from: private */
    public void setRebootProgress(final int progress, final CharSequence message) {
        this.mHandler.post(new Runnable() {
            public void run() {
                if (ShutdownThread.this.mProgressDialog != null) {
                    ShutdownThread.this.mProgressDialog.setProgress(progress);
                    if (message != null) {
                        ShutdownThread.this.mProgressDialog.setMessage(message);
                    }
                }
            }
        });
    }

    private void shutdownRadios(int timeout) {
        boolean[] done = new boolean[1];
        final long elapsedRealtime = SystemClock.elapsedRealtime() + ((long) timeout);
        final int i = timeout;
        final boolean[] zArr = done;
        AnonymousClass5 r4 = new Thread() {
            /* JADX WARNING: Removed duplicated region for block: B:11:0x0026 A[Catch:{ RemoteException -> 0x0021 }] */
            /* JADX WARNING: Removed duplicated region for block: B:16:0x004e  */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                    r14 = this;
                    android.util.TimingsTraceLog r0 = com.android.server.power.ShutdownThread.newTimingsLog()
                    java.lang.String r1 = "phone"
                    android.os.IBinder r1 = android.os.ServiceManager.checkService(r1)
                    com.android.internal.telephony.ITelephony r1 = com.android.internal.telephony.ITelephony.Stub.asInterface(r1)
                    r2 = 0
                    java.lang.String r3 = "RemoteException during radio shutdown"
                    r4 = 1
                    java.lang.String r5 = "ShutdownThread"
                    if (r1 == 0) goto L_0x0023
                    boolean r6 = r1.needMobileRadioShutdown()     // Catch:{ RemoteException -> 0x0021 }
                    if (r6 != 0) goto L_0x001f
                    goto L_0x0023
                L_0x001f:
                    r6 = r2
                    goto L_0x0024
                L_0x0021:
                    r6 = move-exception
                    goto L_0x0036
                L_0x0023:
                    r6 = r4
                L_0x0024:
                    if (r6 != 0) goto L_0x003b
                    java.lang.String r7 = "Turning off cellular radios..."
                    android.util.Log.w(r5, r7)     // Catch:{ RemoteException -> 0x0021 }
                    java.lang.String r7 = com.android.server.power.ShutdownThread.METRIC_RADIO     // Catch:{ RemoteException -> 0x0021 }
                    com.android.server.power.ShutdownThread.metricStarted(r7)     // Catch:{ RemoteException -> 0x0021 }
                    r1.shutdownMobileRadios()     // Catch:{ RemoteException -> 0x0021 }
                    goto L_0x003b
                L_0x0036:
                    android.util.Log.e(r5, r3, r6)
                    r6 = 1
                    goto L_0x003c
                L_0x003b:
                L_0x003c:
                    java.lang.String r7 = "Waiting for Radio..."
                    android.util.Log.i(r5, r7)
                    long r7 = r6
                    long r9 = android.os.SystemClock.elapsedRealtime()
                    long r7 = r7 - r9
                L_0x0048:
                    r9 = 0
                    int r9 = (r7 > r9 ? 1 : (r7 == r9 ? 0 : -1))
                    if (r9 <= 0) goto L_0x00ba
                    boolean r9 = com.android.server.power.ShutdownThread.mRebootHasProgressBar
                    if (r9 == 0) goto L_0x006c
                    int r9 = r8
                    long r10 = (long) r9
                    long r10 = r10 - r7
                    double r10 = (double) r10
                    r12 = 4607182418800017408(0x3ff0000000000000, double:1.0)
                    double r10 = r10 * r12
                    r12 = 4622945017495814144(0x4028000000000000, double:12.0)
                    double r10 = r10 * r12
                    double r12 = (double) r9
                    double r10 = r10 / r12
                    int r9 = (int) r10
                    int r9 = r9 + 6
                    com.android.server.power.ShutdownThread r10 = com.android.server.power.ShutdownThread.sInstance
                    r11 = 0
                    r10.setRebootProgress(r9, r11)
                L_0x006c:
                    if (r6 != 0) goto L_0x00a0
                    boolean r9 = r1.needMobileRadioShutdown()     // Catch:{ RemoteException -> 0x0075 }
                    r9 = r9 ^ r4
                    r6 = r9
                    goto L_0x007a
                L_0x0075:
                    r9 = move-exception
                    android.util.Log.e(r5, r3, r9)
                    r6 = 1
                L_0x007a:
                    if (r6 == 0) goto L_0x00a0
                    java.lang.String r9 = "Radio turned off."
                    android.util.Log.i(r5, r9)
                    java.lang.String r9 = com.android.server.power.ShutdownThread.METRIC_RADIO
                    com.android.server.power.ShutdownThread.metricEnded(r9)
                    android.util.ArrayMap r9 = com.android.server.power.ShutdownThread.TRON_METRICS
                    java.lang.String r10 = com.android.server.power.ShutdownThread.METRIC_RADIO
                    java.lang.Object r9 = r9.get(r10)
                    java.lang.Long r9 = (java.lang.Long) r9
                    long r9 = r9.longValue()
                    java.lang.String r11 = "ShutdownRadio"
                    r0.logDuration(r11, r9)
                L_0x00a0:
                    if (r6 == 0) goto L_0x00ac
                    java.lang.String r3 = "Radio shutdown complete."
                    android.util.Log.i(r5, r3)
                    boolean[] r3 = r9
                    r3[r2] = r4
                    goto L_0x00ba
                L_0x00ac:
                    r9 = 100
                    android.os.SystemClock.sleep(r9)
                    long r9 = r6
                    long r11 = android.os.SystemClock.elapsedRealtime()
                    long r7 = r9 - r11
                    goto L_0x0048
                L_0x00ba:
                    return
                */
                throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.ShutdownThread.AnonymousClass5.run():void");
            }
        };
        r4.start();
        try {
            r4.join((long) timeout);
        } catch (InterruptedException e) {
        }
        if (!done[0]) {
            Log.w(TAG, "Timed out waiting for Radio shutdown.");
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:11:0x0067  */
    /* JADX WARNING: Removed duplicated region for block: B:17:0x0077  */
    /* JADX WARNING: Removed duplicated region for block: B:24:0x00b8  */
    /* JADX WARNING: Removed duplicated region for block: B:6:0x0043  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static void rebootOrShutdown(android.content.Context r14, boolean r15, java.lang.String r16) {
        /*
            java.lang.String r0 = "ERROR"
            java.lang.String r1 = "vendor.peripheral.shutdown_critical_list"
            java.lang.String r1 = android.os.SystemProperties.get(r1, r0)
            boolean r2 = r1.equals(r0)
            java.lang.String r3 = "ShutdownThread"
            if (r2 != 0) goto L_0x00bd
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "Shutdown critical subsyslist is :"
            r2.append(r4)
            r2.append(r1)
            java.lang.String r4 = ": "
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            android.util.Log.i(r3, r2)
            java.lang.String r2 = "Waiting for a maximum of 10000ms"
            android.util.Log.i(r3, r2)
            java.lang.String r2 = " "
            java.lang.String[] r2 = r1.split(r2)
            r4 = 0
            r5 = 1
            int r6 = r2.length
        L_0x0038:
            r5 = 1
            r7 = 0
        L_0x003a:
            java.lang.String r8 = "ONLINE"
            java.lang.String r9 = ".state"
            java.lang.String r10 = "vendor.peripheral."
            if (r7 >= r6) goto L_0x0065
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r10)
            r10 = r2[r7]
            r11.append(r10)
            r11.append(r9)
            java.lang.String r9 = r11.toString()
            java.lang.String r9 = android.os.SystemProperties.get(r9, r0)
            boolean r8 = r9.equals(r8)
            if (r8 == 0) goto L_0x0062
            r5 = 0
        L_0x0062:
            int r7 = r7 + 1
            goto L_0x003a
        L_0x0065:
            if (r5 != 0) goto L_0x006e
            r11 = 100
            android.os.SystemClock.sleep(r11)
            int r4 = r4 + 1
        L_0x006e:
            r7 = 1
            if (r5 == r7) goto L_0x0075
            r11 = 100
            if (r4 < r11) goto L_0x0038
        L_0x0075:
            if (r5 == r7) goto L_0x00b8
            r7 = 0
        L_0x0078:
            int r11 = r2.length
            if (r7 >= r11) goto L_0x00b7
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            r11.append(r10)
            r12 = r2[r7]
            r11.append(r12)
            r11.append(r9)
            java.lang.String r11 = r11.toString()
            java.lang.String r11 = android.os.SystemProperties.get(r11, r0)
            boolean r12 = r11.equals(r8)
            if (r12 == 0) goto L_0x00b4
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r13 = "Subsystem "
            r12.append(r13)
            r13 = r2[r7]
            r12.append(r13)
            java.lang.String r13 = "did not shut down within timeout"
            r12.append(r13)
            java.lang.String r12 = r12.toString()
            android.util.Log.w(r3, r12)
        L_0x00b4:
            int r7 = r7 + 1
            goto L_0x0078
        L_0x00b7:
            goto L_0x00bd
        L_0x00b8:
            java.lang.String r0 = "Vendor subsystem(s) shutdown successful"
            android.util.Log.i(r3, r0)
        L_0x00bd:
            if (r15 == 0) goto L_0x00df
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "Rebooting, reason: "
            r0.append(r2)
            r2 = r16
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            android.util.Log.i(r3, r0)
            com.android.server.power.PowerManagerService.lowLevelReboot(r16)
            java.lang.String r0 = "Reboot failed, will attempt shutdown instead"
            android.util.Log.e(r3, r0)
            r0 = 0
            goto L_0x00e2
        L_0x00df:
            r2 = r16
            r0 = r2
        L_0x00e2:
            java.lang.String r2 = "Performing low-level shutdown..."
            android.util.Log.i(r3, r2)
            com.android.server.power.PowerManagerService.lowLevelShutdown(r0)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.ShutdownThread.rebootOrShutdown(android.content.Context, boolean, java.lang.String):void");
    }

    /* JADX WARNING: Code restructure failed: missing block: B:23:0x0094, code lost:
        r7 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:25:?, code lost:
        r5.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x009d, code lost:
        throw r7;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static void saveMetrics(boolean r9, java.lang.String r10) {
        /*
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "reboot:"
            r0.append(r1)
            if (r9 == 0) goto L_0x0011
            java.lang.String r1 = "y"
            goto L_0x0014
        L_0x0011:
            java.lang.String r1 = "n"
        L_0x0014:
            r0.append(r1)
            java.lang.String r1 = ","
            r0.append(r1)
            java.lang.String r1 = "reason:"
            r0.append(r1)
            r0.append(r10)
            android.util.ArrayMap<java.lang.String, java.lang.Long> r1 = TRON_METRICS
            int r1 = r1.size()
            r2 = 0
        L_0x002c:
            java.lang.String r3 = "ShutdownThread"
            if (r2 >= r1) goto L_0x0073
            android.util.ArrayMap<java.lang.String, java.lang.Long> r4 = TRON_METRICS
            java.lang.Object r4 = r4.keyAt(r2)
            java.lang.String r4 = (java.lang.String) r4
            android.util.ArrayMap<java.lang.String, java.lang.Long> r5 = TRON_METRICS
            java.lang.Object r5 = r5.valueAt(r2)
            java.lang.Long r5 = (java.lang.Long) r5
            long r5 = r5.longValue()
            r7 = 0
            int r7 = (r5 > r7 ? 1 : (r5 == r7 ? 0 : -1))
            if (r7 >= 0) goto L_0x0060
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "metricEnded wasn't called for "
            r7.append(r8)
            r7.append(r4)
            java.lang.String r7 = r7.toString()
            android.util.Log.e(r3, r7)
            goto L_0x0070
        L_0x0060:
            r3 = 44
            r0.append(r3)
            r0.append(r4)
            r3 = 58
            r0.append(r3)
            r0.append(r5)
        L_0x0070:
            int r2 = r2 + 1
            goto L_0x002c
        L_0x0073:
            java.io.File r2 = new java.io.File
            java.lang.String r4 = "/data/system/shutdown-metrics.tmp"
            r2.<init>(r4)
            r4 = 0
            java.io.FileOutputStream r5 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x009e }
            r5.<init>(r2)     // Catch:{ IOException -> 0x009e }
            java.lang.String r6 = r0.toString()     // Catch:{ all -> 0x0092 }
            java.nio.charset.Charset r7 = java.nio.charset.StandardCharsets.UTF_8     // Catch:{ all -> 0x0092 }
            byte[] r6 = r6.getBytes(r7)     // Catch:{ all -> 0x0092 }
            r5.write(r6)     // Catch:{ all -> 0x0092 }
            r4 = 1
            r5.close()     // Catch:{ IOException -> 0x009e }
            goto L_0x00a4
        L_0x0092:
            r6 = move-exception
            throw r6     // Catch:{ all -> 0x0094 }
        L_0x0094:
            r7 = move-exception
            r5.close()     // Catch:{ all -> 0x0099 }
            goto L_0x009d
        L_0x0099:
            r8 = move-exception
            r6.addSuppressed(r8)     // Catch:{ IOException -> 0x009e }
        L_0x009d:
            throw r7     // Catch:{ IOException -> 0x009e }
        L_0x009e:
            r5 = move-exception
            java.lang.String r6 = "Cannot save shutdown metrics"
            android.util.Log.e(r3, r6, r5)
        L_0x00a4:
            if (r4 == 0) goto L_0x00b0
            java.io.File r3 = new java.io.File
            java.lang.String r5 = "/data/system/shutdown-metrics.txt"
            r3.<init>(r5)
            r2.renameTo(r3)
        L_0x00b0:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.power.ShutdownThread.saveMetrics(boolean, java.lang.String):void");
    }

    private void uncrypt() {
        Log.i(TAG, "Calling uncrypt and monitoring the progress...");
        final RecoverySystem.ProgressListener progressListener = new RecoverySystem.ProgressListener() {
            public void onProgress(int status) {
                if (status >= 0 && status < 100) {
                    CharSequence msg = ShutdownThread.this.mContext.getText(17040992);
                    ShutdownThread.sInstance.setRebootProgress(((int) ((((double) status) * 80.0d) / 100.0d)) + 20, msg);
                } else if (status == 100) {
                    ShutdownThread.sInstance.setRebootProgress(status, ShutdownThread.this.mContext.getText(17040994));
                }
            }
        };
        final boolean[] done = {false};
        Thread t = new Thread() {
            public void run() {
                RecoverySystem recoverySystem = (RecoverySystem) ShutdownThread.this.mContext.getSystemService("recovery");
                try {
                    RecoverySystem.processPackage(ShutdownThread.this.mContext, new File(FileUtils.readTextFile(RecoverySystem.UNCRYPT_PACKAGE_FILE, 0, (String) null)), progressListener);
                } catch (IOException e) {
                    Log.e(ShutdownThread.TAG, "Error uncrypting file", e);
                }
                done[0] = true;
            }
        };
        t.start();
        try {
            t.join(900000);
        } catch (InterruptedException e) {
        }
        if (!done[0]) {
            Log.w(TAG, "Timed out waiting for uncrypt.");
            try {
                FileUtils.stringToFile(RecoverySystem.UNCRYPT_STATUS_FILE, String.format("uncrypt_time: %d\nuncrypt_error: %d\n", new Object[]{900, 100}));
            } catch (IOException e2) {
                Log.e(TAG, "Failed to write timeout message to uncrypt status", e2);
            }
        }
    }
}
