package com.android.server.backup;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.IActivityManager;
import android.app.IBackupAgent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.util.Log;
import android.util.Slog;
import com.android.server.BatteryService;
import com.android.server.backup.fullbackup.FullBackupEngine;
import com.android.server.backup.fullbackup.PerformAdbBackupTask;
import com.android.server.backup.internal.Operation;
import com.android.server.backup.restore.FullRestoreEngine;
import com.android.server.backup.utils.FullBackupUtils;
import com.miui.server.BackupProxyHelper;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import libcore.io.IoBridge;
import miui.app.backup.BackupManager;
import miui.app.backup.IBackupManager;
import miui.os.Build;

public class BackupManagerServiceInjector {
    private static final int INSTALL_ALL_WHITELIST_RESTRICTED_PERMISSIONS = 4194304;
    private static final int INSTALL_FULL_APP = 16384;
    private static final int INSTALL_REASON_USER = 4;
    private static final String TAG = "Backup:BackupManagerServiceInjector";
    private static final String XMSF_PKG_NAME = "com.xiaomi.xmsf";
    private static HashMap<IBinder, DeathLinker> sBinderDeathLinker = new HashMap<>();

    public static boolean startConfirmationUi(UserBackupManagerService thiz, int token, String action, int fd) {
        IBackupManager bm = ServiceManager.getService("MiuiBackup");
        try {
            if (bm.isRunningFromMiui(fd)) {
                bm.startConfirmationUi(token, action);
                return true;
            }
            thiz.startConfirmationUi(token, action);
            return true;
        } catch (RemoteException e) {
            Slog.e(TAG, "confirmation failed", e);
            return false;
        }
    }

    public static void errorOccur(int errCode, int fd) {
        IBackupManager bm = ServiceManager.getService("MiuiBackup");
        try {
            if (bm.isRunningFromMiui(fd)) {
                bm.errorOccur(errCode);
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "errorOccur failed", e);
        }
    }

    public static void errorOccur(int errCode, InputStream inputStream) {
        if (inputStream instanceof FileInputStream) {
            try {
                errorOccur(errCode, ((FileInputStream) inputStream).getFD().getInt$());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeMiuiBackupHeader(ParcelFileDescriptor out, int fd) {
        IBackupManager bm = ServiceManager.getService("MiuiBackup");
        try {
            if (bm.isRunningFromMiui(fd)) {
                bm.writeMiuiBackupHeader(out);
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "writeMiuiBackupHeader failed", e);
        }
    }

    public static void readMiuiBackupHeader(ParcelFileDescriptor in, int fd) {
        IBackupManager bm = ServiceManager.getService("MiuiBackup");
        try {
            if (bm.isRunningFromMiui(fd)) {
                bm.readMiuiBackupHeader(in);
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "readMiuiBackupHeader failed", e);
        }
    }

    public static void addRestoredSize(long size, int fd) {
        IBackupManager bm = ServiceManager.getService("MiuiBackup");
        try {
            if (bm.isRunningFromMiui(fd)) {
                bm.addCompletedSize(size);
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "addRestoredSize failed", e);
        }
    }

    public static void onApkInstalled(int fd) {
        IBackupManager bm = ServiceManager.getService("MiuiBackup");
        try {
            if (bm.isRunningFromMiui(fd)) {
                bm.onApkInstalled();
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "onApkInstalled failed", e);
        }
    }

    private static class DeathLinker implements IBinder.DeathRecipient {
        private IBinder mAgentBinder;
        private int mCallerFd;
        private ParcelFileDescriptor mOutPipe;
        private int mToken = -1;

        public DeathLinker(IBinder agentBinder, int fd, ParcelFileDescriptor outPipe) {
            this.mAgentBinder = agentBinder;
            this.mCallerFd = fd;
            this.mOutPipe = outPipe;
        }

        public void setToken(int token) {
            this.mToken = token;
        }

        public void binderDied() {
            tearDownPipes();
            try {
                ServiceManager.getService(BatteryService.HealthServiceWrapper.INSTANCE_HEALTHD).opComplete(this.mToken, 0);
            } catch (RemoteException e) {
                Slog.e(BackupManagerServiceInjector.TAG, "binderDied failed", e);
            }
            BackupManagerServiceInjector.errorOccur(8, this.mCallerFd);
        }

        private void tearDownPipes() {
            try {
                if (ServiceManager.getService("MiuiBackup").isRunningFromMiui(this.mCallerFd) && this.mOutPipe != null) {
                    try {
                        IoBridge.closeAndSignalBlockedThreads(this.mOutPipe.getFileDescriptor());
                        this.mOutPipe = null;
                    } catch (IOException e) {
                        Slog.w(BackupManagerServiceInjector.TAG, "Couldn't close agent pipes", e);
                    }
                }
            } catch (RemoteException e2) {
                Slog.e(BackupManagerServiceInjector.TAG, "errorOccur failed", e2);
            }
        }
    }

    public static boolean needUpdateToken(IBackupAgent backupAgent, int token) {
        boolean needUpdateToken = false;
        if (backupAgent != null) {
            needUpdateToken = backupAgent.asBinder().isBinderAlive();
            DeathLinker deathLinker = sBinderDeathLinker.get(backupAgent.asBinder());
            if (deathLinker != null) {
                deathLinker.setToken(token);
            }
        }
        return needUpdateToken;
    }

    public static void linkToDeath(IBackupAgent backupAgent, int fd, ParcelFileDescriptor outPipe) {
        if (backupAgent != null) {
            IBinder agentBinder = backupAgent.asBinder();
            DeathLinker deathLinker = new DeathLinker(agentBinder, fd, outPipe);
            sBinderDeathLinker.put(agentBinder, deathLinker);
            try {
                agentBinder.linkToDeath(deathLinker, 0);
            } catch (RemoteException e) {
                Slog.e(TAG, "linkToDeath failed", e);
            }
        }
    }

    public static void unlinkToDeath(IBackupAgent backupAgent) {
        if (backupAgent != null) {
            IBinder agentBinder = backupAgent.asBinder();
            agentBinder.unlinkToDeath(sBinderDeathLinker.get(agentBinder), 0);
            sBinderDeathLinker.remove(agentBinder);
        }
    }

    public static boolean isRunningFromMiui(int fd) {
        try {
            if (ServiceManager.getService("MiuiBackup").isRunningFromMiui(fd)) {
                return true;
            }
            return false;
        } catch (RemoteException e) {
            Slog.e(TAG, "isRunningFromMiui error", e);
            return false;
        }
    }

    public static boolean isRunningFromMiui(InputStream inputStream) {
        if (!(inputStream instanceof FileInputStream)) {
            return false;
        }
        try {
            return isRunningFromMiui(((FileInputStream) inputStream).getFD().getInt$());
        } catch (IOException e) {
            Slog.e(TAG, "isRunningFromMiui error", e);
            return false;
        }
    }

    public static boolean isSysAppRunningFromMiui(PackageInfo info, int fd) {
        return BackupManager.isSysAppForBackup(info) && isRunningFromMiui(fd);
    }

    public static boolean isForceAllowBackup(PackageInfo info, int fd) {
        if (!Build.IS_INTERNATIONAL_BUILD || BackupManager.isSysAppForBackup(info)) {
            return isRunningFromMiui(fd);
        }
        return false;
    }

    public static int getAppUserId(int fd, int def) {
        IBackupManager bm = ServiceManager.getService("MiuiBackup");
        try {
            if (bm.isRunningFromMiui(fd)) {
                return bm.getAppUserId();
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "getAppUserId failed", e);
        }
        return def;
    }

    public static ApplicationInfo getApplicationInfo(Context context, String pkgName, int fd, int userIdDef) throws PackageManager.NameNotFoundException {
        IBackupManager bm = ServiceManager.getService("MiuiBackup");
        PackageManager pm = context.getPackageManager();
        try {
            if (bm.isRunningFromMiui(fd)) {
                int userId = bm.getAppUserId();
                if (BackupManager.isSysAppForBackup(context, pkgName)) {
                    return pm.getApplicationInfoAsUser(pkgName, 1024, userId);
                }
                return pm.getApplicationInfoAsUser(pkgName, 0, userId);
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "getApplicationInfo failed", e);
        }
        return pm.getApplicationInfoAsUser(pkgName, 0, userIdDef);
    }

    public static PackageInfo getPackageInfo(Context context, String pkgName, int fd) throws PackageManager.NameNotFoundException {
        IBackupManager bm = ServiceManager.getService("MiuiBackup");
        PackageManager pm = context.getPackageManager();
        try {
            if (bm.isRunningFromMiui(fd)) {
                int userId = bm.getAppUserId();
                if (BackupManager.isSysAppForBackup(context, pkgName)) {
                    return pm.getPackageInfoAsUser(pkgName, 134218752, userId);
                }
                return pm.getPackageInfoAsUser(pkgName, 134217728, userId);
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "getPackageInfo failed", e);
        }
        return pm.getPackageInfo(pkgName, 134217728);
    }

    public static boolean isNeedBeKilled(String pkg, int fd) {
        IBackupManager bm = ServiceManager.getService("MiuiBackup");
        try {
            if (bm.isRunningFromMiui(fd)) {
                return bm.isNeedBeKilled(pkg);
            }
            return true;
        } catch (RemoteException e) {
            Slog.e(TAG, "isNeedBeKilled failed", e);
            return true;
        }
    }

    public static void restoreFileEnd(UserBackupManagerService thiz, IBackupAgent agent, android.app.backup.IBackupManager backupManagerBinder, int fd, Handler backupHandler, int restoreTimeoutMsg) {
        IBackupAgent iBackupAgent = agent;
        if (iBackupAgent != null) {
            try {
                if (ServiceManager.getService("MiuiBackup").isRunningFromMiui(fd)) {
                    int token = thiz.generateRandomIntegerToken();
                    backupHandler.removeMessages(restoreTimeoutMsg);
                    prepareOperationTimeout(thiz, token, BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS, (BackupRestoreTask) null, 1, fd);
                    int token2 = token;
                    agent.doRestoreFile((ParcelFileDescriptor) null, 0, 0, BackupManager.DOMAIN_END, (String) null, 0, 0, token, backupManagerBinder);
                    if (needUpdateToken(iBackupAgent, token2)) {
                        try {
                            thiz.waitUntilOperationComplete(token2);
                        } catch (RemoteException e) {
                            e = e;
                        }
                    } else {
                        UserBackupManagerService userBackupManagerService = thiz;
                    }
                } else {
                    UserBackupManagerService userBackupManagerService2 = thiz;
                }
            } catch (RemoteException e2) {
                e = e2;
                UserBackupManagerService userBackupManagerService3 = thiz;
                Slog.e(TAG, "restoreFileEnd failed", e);
            }
        } else {
            UserBackupManagerService userBackupManagerService4 = thiz;
        }
    }

    public static void routeSocketDataToOutput(ParcelFileDescriptor inPipe, OutputStream out, int outFd) throws IOException {
        IBackupManager bm = ServiceManager.getService("MiuiBackup");
        FileInputStream raw = null;
        DataInputStream in = null;
        try {
            if (bm.isRunningFromMiui(outFd)) {
                if (!bm.isCanceling()) {
                    raw = new FileInputStream(inPipe.getFileDescriptor());
                    in = new DataInputStream(raw);
                    byte[] buffer = new byte[32768];
                    while (true) {
                        int readInt = in.readInt();
                        int chunkTotal = readInt;
                        if (readInt <= 0) {
                            break;
                        }
                        while (true) {
                            if (chunkTotal > 0) {
                                int nRead = in.read(buffer, 0, chunkTotal > buffer.length ? buffer.length : chunkTotal);
                                if (nRead >= 0) {
                                    out.write(buffer, 0, nRead);
                                    bm.addCompletedSize((long) nRead);
                                    chunkTotal -= nRead;
                                } else {
                                    Slog.e(TAG, "Unexpectedly reached end of file while reading data");
                                    throw new EOFException();
                                }
                            }
                        }
                    }
                } else {
                    if (raw != null) {
                        raw.close();
                    }
                    if (in != null) {
                        in.close();
                        return;
                    }
                    return;
                }
            } else {
                FullBackupUtils.routeSocketDataToOutput(inPipe, out);
            }
            if (raw != null) {
                raw.close();
            }
            if (in == null) {
                return;
            }
        } catch (RemoteException | InterruptedIOException e) {
            Slog.e(TAG, "routeSocketDataToOutput failed", e);
            if (raw != null) {
                raw.close();
            }
            if (in == null) {
                return;
            }
        } catch (ArrayIndexOutOfBoundsException e2) {
            Slog.e(TAG, "routeSocketDataToOutput failed", e2);
            throw new IOException();
        } catch (Throwable th) {
            if (raw != null) {
                raw.close();
            }
            if (in != null) {
                in.close();
            }
            throw th;
        }
        in.close();
    }

    public static void setInputFileDescriptor(FullRestoreEngine engine, int fd) {
        if (engine != null) {
            engine.mInputFD = fd;
        }
    }

    public static void setOutputFileDescriptor(FullBackupEngine engine, int fd) {
        if (engine != null) {
            engine.mOutputFD = fd;
        }
    }

    public static void setOutputFileDescriptor(PerformAdbBackupTask task, ParcelFileDescriptor fileDescriptor) {
        int fd;
        if (task != null) {
            try {
                fd = fileDescriptor.getFd();
            } catch (Exception e) {
                Slog.e(TAG, "setOutputFileDescriptor failed", e);
                fd = -2;
            }
            task.mOutputFD = fd;
        }
    }

    public static boolean tearDownAgentAndKill(IActivityManager activityManager, ApplicationInfo appInfo, int fd) {
        try {
            if (!ServiceManager.getService("MiuiBackup").isRunningFromMiui(fd) || isNeedBeKilled(appInfo.packageName, fd)) {
                return false;
            }
            activityManager.unbindBackupAgent(appInfo);
            return true;
        } catch (RemoteException e) {
            Slog.e(TAG, "isNeedBeKilled failed", e);
            return false;
        }
    }

    public static void prepareOperationTimeout(UserBackupManagerService thiz, int token, long interval, BackupRestoreTask callback, int operationType, int fd) {
        IBackupManager bm = ServiceManager.getService("MiuiBackup");
        int backupTimeoutScale = 1;
        try {
            if (bm.isRunningFromMiui(fd)) {
                backupTimeoutScale = bm.getBackupTimeoutScale();
            }
        } catch (RemoteException e) {
            Slog.e(TAG, "prepareOperationTimeout failed", e);
        }
        Slog.d(TAG, "prepareOperationTimeout backupTimeoutScale = " + backupTimeoutScale);
        thiz.prepareOperationTimeout(token, ((long) backupTimeoutScale) * interval, callback, operationType);
    }

    public static void doFullBackup(IBackupAgent agent, ParcelFileDescriptor pipe, long quotaBytes, int token, android.app.backup.IBackupManager backupManagerBinder, int transportFlags, int fd) throws RemoteException {
        IBackupManager bm = ServiceManager.getService("MiuiBackup");
        if (!bm.isRunningFromMiui(fd) || !bm.shouldSkipData()) {
            agent.doFullBackup(pipe, quotaBytes, token, backupManagerBinder, transportFlags);
            return;
        }
        Slog.i(TAG, "skip app data");
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(pipe.getFileDescriptor());
            out.write(new byte[4]);
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e2) {
            Slog.e(TAG, "Unable to finalize backup stream!");
            if (out != null) {
                out.close();
            }
        } catch (Throwable th) {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
            throw th;
        }
        backupManagerBinder.opComplete(token, 0);
    }

    public static void waitingBeforeGetAgent() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean isCanceling(int fd) {
        IBackupManager bm = ServiceManager.getService("MiuiBackup");
        try {
            if (bm.isRunningFromMiui(fd)) {
                return bm.isCanceling();
            }
            return false;
        } catch (RemoteException e) {
            Slog.e(TAG, "isCanceling error", e);
            return false;
        }
    }

    public static void cancelBackups(UserBackupManagerService thiz) {
        long oldToken = Binder.clearCallingIdentity();
        try {
            List<Integer> operationsToCancel = new ArrayList<>();
            synchronized (thiz.getCurrentOpLock()) {
                for (int i = 0; i < thiz.getCurrentOperations().size(); i++) {
                    Operation op = thiz.getCurrentOperations().valueAt(i);
                    int token = thiz.getCurrentOperations().keyAt(i);
                    if (op.type == 0 || op.type == 1) {
                        operationsToCancel.add(Integer.valueOf(token));
                    }
                }
            }
            for (Integer token2 : operationsToCancel) {
                thiz.handleCancel(token2.intValue(), true);
            }
            Binder.restoreCallingIdentity(oldToken);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(oldToken);
            throw th;
        }
    }

    public static boolean isXSpaceUser(int userId) {
        return userId == 999;
    }

    public static boolean isXSpaceUserRunning() {
        try {
            return ActivityManager.getService().isUserRunning(999, 0);
        } catch (RemoteException e) {
            return false;
        }
    }

    public static boolean installExistingPackageAsUser(String pkgName, int userId) {
        try {
            IPackageManager packageManager = AppGlobals.getPackageManager();
            if (!BackupProxyHelper.isAppInXSpace(packageManager, pkgName)) {
                if (BackupProxyHelper.isMiPushRequired(packageManager, pkgName) && !BackupProxyHelper.isAppInXSpace(packageManager, XMSF_PKG_NAME)) {
                    packageManager.installExistingPackageAsUser(XMSF_PKG_NAME, userId, 4210688, 4, (List) null);
                    Slog.d(TAG, "Require XMSF, auto installed! ");
                }
                if (packageManager.installExistingPackageAsUser(pkgName, userId, 4210688, 4, (List) null) == 1) {
                    return true;
                }
                return false;
            }
            Slog.d(TAG, "has been installed, pkgName:" + pkgName + ", userId:" + userId);
            return true;
        } catch (RemoteException e) {
            Slog.e(TAG, "Unable to install package: " + pkgName, e);
            return false;
        }
    }

    public static class HuanjiReceiver extends BroadcastReceiver {
        static final String ACTION_HUANJI_END_RESTORE = "com.miui.huanji.END_RESTORE";
        static final String ACTION_HUANJI_RESTORE = "com.miui.huanji.START_RESTORE";
        private static final String TAG = "HuanjiReceiver";
        private UserBackupManagerService mUserBackupManagerService;

        public HuanjiReceiver(UserBackupManagerService userBackupManagerService) {
            this.mUserBackupManagerService = userBackupManagerService;
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                Log.i(TAG, "get action: " + action);
                char c = 65535;
                int hashCode = action.hashCode();
                if (hashCode != -1157256499) {
                    if (hashCode == 2016774470 && action.equals(ACTION_HUANJI_END_RESTORE)) {
                        c = 1;
                    }
                } else if (action.equals(ACTION_HUANJI_RESTORE)) {
                    c = 0;
                }
                if (c == 0) {
                    this.mUserBackupManagerService.setSetupComplete(true);
                } else if (c == 1) {
                    this.mUserBackupManagerService.setSetupComplete(false);
                }
            }
        }

        public void register(Context context) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(ACTION_HUANJI_RESTORE);
            filter.addAction(ACTION_HUANJI_END_RESTORE);
            context.registerReceiver(this, filter, "android.permission.BACKUP", (Handler) null);
        }

        public void unregister(Context context) {
            context.unregisterReceiver(this);
        }
    }
}
