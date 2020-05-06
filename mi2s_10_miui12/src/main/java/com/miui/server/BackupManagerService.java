package com.miui.server;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.backup.IFullBackupRestoreObserver;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.content.PackageMonitor;
import com.android.server.BatteryService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import libcore.io.IoBridge;
import miui.app.backup.BackupFileResolver;
import miui.app.backup.BackupManager;
import miui.app.backup.IBackupManager;
import miui.app.backup.IBackupServiceStateObserver;
import miui.app.backup.IPackageBackupRestoreObserver;

public class BackupManagerService extends IBackupManager.Stub {
    private static final int COMPONENT_ENABLED_STATE_NONE = -1;
    public static final int FD_CLOSE = -2;
    public static final int FD_NONE = -1;
    private static final int PID_NONE = -1;
    private static final String TAG = "Backup:BackupManagerService";
    private ActivityManager mActivityManager;
    /* access modifiers changed from: private */
    public int mAppUserId = 0;
    /* access modifiers changed from: private */
    public IPackageBackupRestoreObserver mBackupRestoreObserver;
    private volatile int mCallerFd = -1;
    /* access modifiers changed from: private */
    public Context mContext;
    private long mCurrentCompletedSize;
    /* access modifiers changed from: private */
    public long mCurrentTotalSize;
    /* access modifiers changed from: private */
    public int mCurrentWorkingFeature;
    /* access modifiers changed from: private */
    public String mCurrentWorkingPkg;
    /* access modifiers changed from: private */
    public DeathLinker mDeathLinker = new DeathLinker();
    private String mEncryptedPwd;
    private String mEncryptedPwdInBakFile;
    private Handler mHandler;
    private HandlerThread mHandlerThread;
    /* access modifiers changed from: private */
    public IBinder mICaller = null;
    /* access modifiers changed from: private */
    public boolean mIsCanceling = false;
    private int mLastError;
    private HashMap<String, Boolean> mNeedBeKilledPkgs = new HashMap<>();
    private ParcelFileDescriptor mOutputFile = null;
    /* access modifiers changed from: private */
    public int mOwnerPid = -1;
    private int mPackageLastEnableState;
    /* access modifiers changed from: private */
    public PackageManager mPackageManager;
    private IPackageManager mPackageManagerBinder;
    PackageMonitor mPackageMonitor = new PackageMonitor() {
        public boolean onPackageChanged(String packageName, int uid, String[] components) {
            if (packageName == null || !packageName.equals(BackupManagerService.this.mCurrentWorkingPkg)) {
                return true;
            }
            synchronized (BackupManagerService.this.mPkgChangingLock) {
                if (BackupManagerService.this.mPkgChangingLock.get()) {
                    BackupManagerService.this.mPkgChangingLock.set(false);
                    BackupManagerService.this.mPkgChangingLock.notify();
                }
            }
            return true;
        }
    };
    private IPackageStatsObserver mPackageStatsObserver = new IPackageStatsObserver() {
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            String pkg = pStats.packageName;
            try {
                PackageInfo pi = BackupManagerService.this.mPackageManager.getPackageInfoAsUser(pkg, 0, BackupManagerService.this.mAppUserId);
                if (BackupManager.isSysAppForBackup(BackupManagerService.this.mContext, pkg)) {
                    long unused = BackupManagerService.this.mCurrentTotalSize = pStats.dataSize;
                } else {
                    long unused2 = BackupManagerService.this.mCurrentTotalSize = new File(pi.applicationInfo.sourceDir).length() + pStats.dataSize;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        public IBinder asBinder() {
            return null;
        }
    };
    /* access modifiers changed from: private */
    public final AtomicBoolean mPkgChangingLock = new AtomicBoolean(false);
    /* access modifiers changed from: private */
    public String mPreviousWorkingPkg;
    private int mProgType;
    /* access modifiers changed from: private */
    public String mPwd;
    private boolean mShouldSkipData;
    private int mState = 0;
    private RemoteCallbackList<IBackupServiceStateObserver> mStateObservers = new RemoteCallbackList<>();
    private AtomicBoolean mTaskLatch = null;

    public BackupManagerService(Context context) {
        this.mContext = context;
        this.mPackageManager = this.mContext.getPackageManager();
        this.mPackageManagerBinder = AppGlobals.getPackageManager();
        this.mActivityManager = (ActivityManager) context.getSystemService("activity");
        this.mHandlerThread = new HandlerThread("MiuiBackup", 10);
        this.mHandlerThread.start();
        this.mHandler = new BackupHandler(this.mHandlerThread.getLooper());
        restoreLastPackageEnableState(getPackageEnableStateFile());
    }

    public void backupPackage(ParcelFileDescriptor outFileDescriptor, ParcelFileDescriptor readSide, String pkg, int feature, String pwd, String encryptedPwd, boolean includeApk, boolean forceBackup, boolean shouldSkipData, boolean isXSpace, IPackageBackupRestoreObserver observer) throws RemoteException {
        ParcelFileDescriptor parcelFileDescriptor = outFileDescriptor;
        String str = pkg;
        int i = feature;
        boolean z = includeApk;
        boolean z2 = shouldSkipData;
        boolean z3 = isXSpace;
        this.mBackupRestoreObserver = observer;
        if (Binder.getCallingPid() != this.mOwnerPid) {
            Slog.e(TAG, "You must acquire first to use the backup or restore service");
            errorOccur(9);
        } else if (this.mICaller == null) {
            Slog.e(TAG, "Caller is null You must acquire first with a binder");
            errorOccur(9);
        } else {
            if (TextUtils.isEmpty(pwd) || TextUtils.isEmpty(encryptedPwd)) {
                String str2 = pwd;
                String str3 = encryptedPwd;
            } else {
                this.mPwd = pwd;
                this.mEncryptedPwd = encryptedPwd;
            }
            String defaultIme = getDefaultIme(this.mContext);
            boolean isSystemApp = BackupManager.isSysAppForBackup(this.mContext, str);
            Slog.v(TAG, "backupPackage: pkg=" + str + " feature=" + i + " includeApk=" + z + " shouldSkipData=" + z2 + " isXSpace=" + z3 + " isSystemApp=" + isSystemApp);
            this.mAppUserId = z3 ? 999 : 0;
            this.mPackageLastEnableState = -1;
            this.mCurrentWorkingPkg = str;
            if (!isSystemApp) {
                disablePackageAndWait(str, this.mAppUserId);
            }
            this.mOutputFile = parcelFileDescriptor;
            this.mShouldSkipData = z2;
            this.mCurrentWorkingFeature = i;
            this.mLastError = 0;
            this.mProgType = 0;
            this.mState = 1;
            this.mCurrentCompletedSize = 0;
            this.mCurrentTotalSize = -1;
            BackupManagerServiceProxy.getPackageSizeInfo(this.mContext, this.mPackageManager, str, this.mAppUserId, this.mPackageStatsObserver);
            synchronized (this) {
                this.mTaskLatch = new AtomicBoolean(false);
                this.mCallerFd = outFileDescriptor.getFd();
                Slog.d(TAG, "backupPackage, MIUI FD is " + this.mCallerFd);
            }
            synchronized (this.mTaskLatch) {
                if (this.mOwnerPid != -1) {
                    BackupManagerServiceProxy.fullBackup(parcelFileDescriptor, new String[]{str}, z);
                } else {
                    errorOccur(10);
                }
                this.mTaskLatch.set(true);
                this.mTaskLatch.notifyAll();
            }
            if (!isSystemApp) {
                enablePackage(str, this.mAppUserId, defaultIme);
            }
            this.mPwd = null;
            this.mEncryptedPwd = null;
            this.mTaskLatch = null;
            this.mCallerFd = -1;
            this.mOutputFile = null;
            this.mProgType = 0;
            this.mState = 0;
            this.mPackageLastEnableState = -1;
            this.mCurrentTotalSize = -1;
            this.mCurrentCompletedSize = 0;
            this.mPreviousWorkingPkg = this.mCurrentWorkingPkg;
            this.mAppUserId = 0;
        }
    }

    @Deprecated
    public void setFutureTask(List<String> list) {
    }

    private boolean isApplicationInstalled(String packageName, int userId) {
        List<PackageInfo> installedList = this.mPackageManager.getInstalledPackagesAsUser(0, userId);
        boolean isInstalled = false;
        int i = 0;
        while (true) {
            if (i >= installedList.size()) {
                break;
            } else if (installedList.get(i).packageName.equals(packageName)) {
                isInstalled = true;
                break;
            } else {
                i++;
            }
        }
        Slog.d(TAG, "isApplicationInstalled, packageName:" + packageName + " isInstalled:" + isInstalled);
        return isInstalled;
    }

    public void startConfirmationUi(final int token, String action) throws RemoteException {
        Handler handler = this.mHandler;
        AnonymousClass2 r1 = new Runnable() {
            public void run() {
                android.app.backup.IBackupManager bm = ServiceManager.getService(BatteryService.HealthServiceWrapper.INSTANCE_HEALTHD);
                try {
                    bm.acknowledgeFullBackupOrRestore(token, true, "", BackupManagerService.this.mPwd, new FullBackupRestoreObserver());
                } catch (RemoteException e) {
                    Slog.e(BackupManagerService.TAG, "acknowledgeFullBackupOrRestore failed", e);
                }
            }
        };
        String str = this.mPreviousWorkingPkg;
        handler.postDelayed(r1, (str == null || !str.equals(this.mCurrentWorkingPkg)) ? 100 : 1500);
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0038 A[SYNTHETIC, Splitter:B:21:0x0038] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void writeMiuiBackupHeader(android.os.ParcelFileDescriptor r9) {
        /*
            r8 = this;
            r0 = 0
            java.io.FileOutputStream r1 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x002b, all -> 0x0026 }
            java.io.FileDescriptor r2 = r9.getFileDescriptor()     // Catch:{ IOException -> 0x002b, all -> 0x0026 }
            r1.<init>(r2)     // Catch:{ IOException -> 0x002b, all -> 0x0026 }
            android.content.Context r2 = r8.mContext     // Catch:{ IOException -> 0x0024 }
            java.lang.String r3 = r8.mCurrentWorkingPkg     // Catch:{ IOException -> 0x0024 }
            int r4 = r8.mCurrentWorkingFeature     // Catch:{ IOException -> 0x0024 }
            java.lang.String r5 = r8.mEncryptedPwd     // Catch:{ IOException -> 0x0024 }
            int r6 = r8.mAppUserId     // Catch:{ IOException -> 0x0024 }
            miui.app.backup.BackupFileResolver.writeMiuiHeader(r1, r2, r3, r4, r5, r6)     // Catch:{ IOException -> 0x0024 }
            r1.close()     // Catch:{ IOException -> 0x001d }
            return
        L_0x001d:
            r0 = move-exception
            java.lang.RuntimeException r2 = new java.lang.RuntimeException
            r2.<init>(r0)
            throw r2
        L_0x0024:
            r0 = move-exception
            goto L_0x002f
        L_0x0026:
            r1 = move-exception
            r7 = r1
            r1 = r0
            r0 = r7
            goto L_0x0036
        L_0x002b:
            r1 = move-exception
            r7 = r1
            r1 = r0
            r0 = r7
        L_0x002f:
            java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ all -> 0x0035 }
            r2.<init>(r0)     // Catch:{ all -> 0x0035 }
            throw r2     // Catch:{ all -> 0x0035 }
        L_0x0035:
            r0 = move-exception
        L_0x0036:
            if (r1 == 0) goto L_0x0043
            r1.close()     // Catch:{ IOException -> 0x003c }
            goto L_0x0043
        L_0x003c:
            r0 = move-exception
            java.lang.RuntimeException r2 = new java.lang.RuntimeException
            r2.<init>(r0)
            throw r2
        L_0x0043:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.server.BackupManagerService.writeMiuiBackupHeader(android.os.ParcelFileDescriptor):void");
    }

    public void readMiuiBackupHeader(ParcelFileDescriptor inFileDescriptor) {
        InputStream in = null;
        try {
            in = new FileInputStream(inFileDescriptor.getFileDescriptor());
            BackupFileResolver.BackupFileMiuiHeader header = BackupFileResolver.readMiuiHeader(in);
            if (header != null) {
                if (header.version == 2) {
                    this.mCurrentWorkingPkg = header.packageName;
                    this.mCurrentWorkingFeature = header.featureId;
                    this.mAppUserId = header.userId;
                    this.mEncryptedPwdInBakFile = header.isEncrypted ? null : header.encryptedPwd;
                    boolean isSystemApp = BackupManager.isSysAppForBackup(this.mContext, this.mCurrentWorkingPkg);
                    Slog.d(TAG, "readMiuiBackupHeader, BackupFileMiuiHeader:" + header + " isSystemApp:" + isSystemApp + " mAppUserId:" + this.mAppUserId);
                    if (!isSystemApp) {
                        disablePackageAndWait(this.mCurrentWorkingPkg, this.mAppUserId);
                    }
                    in.close();
                }
            }
            Slog.e(TAG, "readMiuiBackupHeader is error, header=" + header);
            try {
                in.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (Throwable e2) {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e3) {
                    throw new RuntimeException(e3);
                }
            }
            throw e2;
        }
    }

    public void addCompletedSize(long size) {
        IPackageBackupRestoreObserver iPackageBackupRestoreObserver;
        this.mCurrentCompletedSize += size;
        if (this.mProgType == 0 && (iPackageBackupRestoreObserver = this.mBackupRestoreObserver) != null) {
            try {
                iPackageBackupRestoreObserver.onCustomProgressChange(this.mCurrentWorkingPkg, this.mCurrentWorkingFeature, 0, this.mCurrentCompletedSize, this.mCurrentTotalSize);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void setIsNeedBeKilled(String pkg, boolean isNeedBeKilled) throws RemoteException {
        Slog.d(TAG, "setIsNeedBeKilled, pkg=" + pkg + ", isNeedBeKilled=" + isNeedBeKilled);
        this.mNeedBeKilledPkgs.put(pkg, Boolean.valueOf(isNeedBeKilled));
    }

    public boolean isNeedBeKilled(String pkg) throws RemoteException {
        Boolean isKilled = this.mNeedBeKilledPkgs.get(pkg);
        if (isKilled != null) {
            return isKilled.booleanValue();
        }
        return true;
    }

    public boolean isRunningFromMiui(int fd) throws RemoteException {
        return this.mCallerFd == fd;
    }

    public boolean isServiceIdle() {
        return this.mState == 0;
    }

    public void setCustomProgress(int progType, int prog, int total) throws RemoteException {
        this.mProgType = progType;
        IPackageBackupRestoreObserver iPackageBackupRestoreObserver = this.mBackupRestoreObserver;
        if (iPackageBackupRestoreObserver != null) {
            iPackageBackupRestoreObserver.onCustomProgressChange(this.mCurrentWorkingPkg, this.mCurrentWorkingFeature, progType, (long) prog, (long) total);
        }
    }

    private static String readHeaderLine(InputStream in) throws IOException {
        StringBuilder buffer = new StringBuilder(80);
        while (true) {
            int read = in.read();
            int c = read;
            if (read >= 0 && c != 10) {
                buffer.append((char) c);
            }
        }
        return buffer.toString();
    }

    public void restoreFile(ParcelFileDescriptor bakFd, String pwd, boolean forceBackup, IPackageBackupRestoreObserver observer) throws RemoteException {
        this.mBackupRestoreObserver = observer;
        if (getCallingPid() != this.mOwnerPid) {
            Slog.e(TAG, "You must acquire first to use the backup or restore service");
            errorOccur(9);
        } else if (this.mICaller == null) {
            Slog.e(TAG, "Caller is null You must acquire first with a binder");
            errorOccur(9);
        } else {
            String defaultIme = getDefaultIme(this.mContext);
            this.mPwd = pwd;
            this.mLastError = 0;
            this.mProgType = 0;
            this.mPackageLastEnableState = -1;
            this.mState = 2;
            this.mCurrentTotalSize = bakFd.getStatSize();
            this.mCurrentCompletedSize = 0;
            synchronized (this) {
                this.mTaskLatch = new AtomicBoolean(false);
                this.mCallerFd = bakFd.getFd();
                Slog.d(TAG, "restoreFile, MIUI FD is " + this.mCallerFd);
            }
            synchronized (this.mTaskLatch) {
                if (this.mOwnerPid != -1) {
                    BackupManagerServiceProxy.fullRestore(bakFd);
                } else {
                    errorOccur(10);
                }
                this.mTaskLatch.set(true);
                this.mTaskLatch.notifyAll();
            }
            if (!BackupManager.isSysAppForBackup(this.mContext, this.mCurrentWorkingPkg)) {
                enablePackage(this.mCurrentWorkingPkg, this.mAppUserId, defaultIme);
            }
            this.mPwd = null;
            this.mEncryptedPwd = null;
            this.mTaskLatch = null;
            this.mCallerFd = -1;
            this.mProgType = 0;
            this.mState = 0;
            this.mPackageLastEnableState = -1;
            this.mCurrentTotalSize = -1;
            this.mPreviousWorkingPkg = this.mCurrentWorkingPkg;
            this.mAppUserId = 0;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:16:0x0056, code lost:
        return false;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean acquire(miui.app.backup.IBackupServiceStateObserver r4, android.os.IBinder r5) throws android.os.RemoteException {
        /*
            r3 = this;
            r0 = 0
            if (r5 != 0) goto L_0x000b
            java.lang.String r1 = "Backup:BackupManagerService"
            java.lang.String r2 = "caller should not be null"
            android.util.Slog.w(r1, r2)
            return r0
        L_0x000b:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Client tries to acquire service. CallingPid="
            r1.append(r2)
            int r2 = android.os.Binder.getCallingPid()
            r1.append(r2)
            java.lang.String r2 = " mOwnerPid="
            r1.append(r2)
            int r2 = r3.mOwnerPid
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "Backup:BackupManagerService"
            android.util.Slog.d(r2, r1)
            monitor-enter(r3)
            java.lang.String r1 = "Backup:BackupManagerService"
            java.lang.String r2 = "Client acquire service. "
            android.util.Slog.d(r1, r2)     // Catch:{ all -> 0x0057 }
            int r1 = r3.mOwnerPid     // Catch:{ all -> 0x0057 }
            r2 = -1
            if (r1 != r2) goto L_0x004e
            int r1 = android.os.Binder.getCallingPid()     // Catch:{ all -> 0x0057 }
            r3.mOwnerPid = r1     // Catch:{ all -> 0x0057 }
            r3.mICaller = r5     // Catch:{ all -> 0x0057 }
            android.os.IBinder r1 = r3.mICaller     // Catch:{ all -> 0x0057 }
            com.miui.server.BackupManagerService$DeathLinker r2 = r3.mDeathLinker     // Catch:{ all -> 0x0057 }
            r1.linkToDeath(r2, r0)     // Catch:{ all -> 0x0057 }
            r0 = 1
            monitor-exit(r3)     // Catch:{ all -> 0x0057 }
            return r0
        L_0x004e:
            if (r4 == 0) goto L_0x0055
            android.os.RemoteCallbackList<miui.app.backup.IBackupServiceStateObserver> r1 = r3.mStateObservers     // Catch:{ all -> 0x0057 }
            r1.register(r4)     // Catch:{ all -> 0x0057 }
        L_0x0055:
            monitor-exit(r3)     // Catch:{ all -> 0x0057 }
            return r0
        L_0x0057:
            r0 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x0057 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.server.BackupManagerService.acquire(miui.app.backup.IBackupServiceStateObserver, android.os.IBinder):boolean");
    }

    public void release(IBackupServiceStateObserver stateObserver) throws RemoteException {
        Slog.d(TAG, "Client tries to release service. CallingPid=" + Binder.getCallingPid() + " mOwnerPid=" + this.mOwnerPid);
        synchronized (this) {
            Slog.d(TAG, "Client release service. Start canceling...");
            if (stateObserver != null) {
                this.mStateObservers.unregister(stateObserver);
            }
            if (Binder.getCallingPid() == this.mOwnerPid) {
                this.mIsCanceling = true;
                scheduleReleaseResource();
                waitForTheLastWorkingTask();
                this.mIsCanceling = false;
                this.mOwnerPid = -1;
                this.mICaller.unlinkToDeath(this.mDeathLinker, 0);
                this.mICaller = null;
                this.mPreviousWorkingPkg = null;
                try {
                    broadcastServiceIdle();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            Slog.d(TAG, "Client release service. Cancel completed!");
        }
    }

    /* access modifiers changed from: private */
    public void scheduleReleaseResource() {
        AtomicBoolean atomicBoolean = this.mTaskLatch;
        if (atomicBoolean != null && !atomicBoolean.get()) {
            if (Build.VERSION.SDK_INT >= 26) {
                try {
                    BackupManagerServiceProxy.fullCancel();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                closeBackupWriteStream(this.mOutputFile);
                return;
            }
            releaseBackupWriteStream(this.mOutputFile);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 3 */
    /* access modifiers changed from: private */
    public void broadcastServiceIdle() throws RemoteException {
        synchronized (this) {
            try {
                int cnt = this.mStateObservers.beginBroadcast();
                for (int i = 0; i < cnt; i++) {
                    this.mStateObservers.getBroadcastItem(i).onServiceStateIdle();
                }
                this.mStateObservers.finishBroadcast();
            } catch (Throwable th) {
                this.mStateObservers.finishBroadcast();
                throw th;
            }
        }
    }

    /* access modifiers changed from: private */
    public void waitForTheLastWorkingTask() {
        AtomicBoolean atomicBoolean = this.mTaskLatch;
        if (atomicBoolean != null) {
            synchronized (atomicBoolean) {
                while (this.mTaskLatch != null && !this.mTaskLatch.get()) {
                    try {
                        this.mTaskLatch.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private boolean checkPackageAvailable(String pkg, int userId) {
        if (!isApplicationInstalled(pkg, userId) || BackupManagerServiceProxy.isPackageStateProtected(this.mPackageManager, pkg, userId)) {
            return false;
        }
        return true;
    }

    private void enablePackage(String pkg, int userId, final String defaultIme) {
        if (checkPackageAvailable(pkg, userId)) {
            if (this.mPackageLastEnableState == -1) {
                this.mPackageLastEnableState = 0;
            }
            Slog.d(TAG, "enablePackage, pkg:" + pkg + " userId:" + userId + ", state:" + this.mPackageLastEnableState + ", defaultIme:" + defaultIme);
            if (isDefaultIme(pkg, defaultIme)) {
                setApplicationEnabledSetting(pkg, userId, this.mPackageLastEnableState, 0);
                this.mHandler.postDelayed(new Runnable() {
                    public void run() {
                        Settings.Secure.putString(BackupManagerService.this.mContext.getContentResolver(), "default_input_method", defaultIme);
                    }
                }, 2000);
                return;
            }
            setApplicationEnabledSetting(pkg, userId, this.mPackageLastEnableState, 1);
            getPackageEnableStateFile().delete();
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 8 */
    private void disablePackageAndWait(String pkg, int userId) {
        if (checkPackageAvailable(pkg, userId)) {
            int applicationSetting = getApplicationEnabledSetting(pkg, userId);
            if (this.mPackageLastEnableState == -1) {
                this.mPackageLastEnableState = applicationSetting;
            }
            Slog.d(TAG, "disablePackageAndWait, pkg:" + pkg + " userId:" + userId + ", state:" + applicationSetting + ", lastState:" + this.mPackageLastEnableState);
            saveCurrentPackageEnableState(getPackageEnableStateFile(), pkg, this.mPackageLastEnableState, userId);
            if (applicationSetting != 2) {
                try {
                    this.mPackageMonitor.register(this.mContext, this.mContext.getMainLooper(), false);
                    long waitStartTime = SystemClock.elapsedRealtime();
                    synchronized (this.mPkgChangingLock) {
                        try {
                            this.mPkgChangingLock.set(true);
                            setApplicationEnabledSetting(pkg, userId, 2, 0);
                            this.mPkgChangingLock.wait(5000);
                            this.mPkgChangingLock.set(false);
                        } catch (InterruptedException e) {
                            Slog.e(TAG, "mPkgChangingLock wait error", e);
                        }
                    }
                    Slog.i(TAG, "setApplicationEnabledSetting wait time=" + (SystemClock.elapsedRealtime() - waitStartTime) + ", pkg=" + pkg);
                    this.mPackageMonitor.unregister();
                    waitUntilAppKilled(pkg);
                } catch (Throwable th) {
                    this.mPackageMonitor.unregister();
                    throw th;
                }
            }
        }
    }

    private void waitUntilAppKilled(String pkg) {
        boolean killed;
        int round = 0;
        ActivityManager am = (ActivityManager) this.mContext.getSystemService("activity");
        while (true) {
            killed = true;
            Iterator<ActivityManager.RunningAppProcessInfo> it = am.getRunningAppProcesses().iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                ActivityManager.RunningAppProcessInfo procInfo = it.next();
                if (!procInfo.processName.equals(pkg)) {
                    String str = procInfo.processName;
                    if (!str.startsWith(pkg + ":")) {
                        continue;
                    }
                }
                if (this.mAppUserId == UserHandle.getUserId(procInfo.uid)) {
                    killed = false;
                    break;
                }
            }
            if (killed) {
                break;
            }
            try {
                Thread.sleep(500);
                int round2 = round + 1;
                if (round >= 20) {
                    int i = round2;
                    break;
                }
                round = round2;
            } catch (InterruptedException e) {
                Slog.e(TAG, "interrupted while waiting", e);
            }
        }
        if (killed) {
            Slog.i(TAG, "app: " + pkg + " is killed. continue our routine.");
            return;
        }
        Slog.w(TAG, "continue while app: " + pkg + " is still alive!");
    }

    private File getPackageEnableStateFile() {
        return new File(new File(Environment.getDataDirectory(), "system"), "backup_pkg_enable_state");
    }

    public static File getCachedInstallFile() {
        return new File(new File(Environment.getDataDirectory(), "system"), "restoring_cached_file");
    }

    private void saveCurrentPackageEnableState(File pkgStateFile, String pkg, int state, int userId) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(pkgStateFile);
            out.write((pkg + " " + state + " " + userId).getBytes());
            try {
                out.close();
            } catch (IOException e) {
                Slog.e(TAG, "IOException", e);
            }
        } catch (IOException e2) {
            Slog.e(TAG, "IOException", e2);
            if (out != null) {
                out.close();
            }
        } catch (Throwable th) {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e3) {
                    Slog.e(TAG, "IOException", e3);
                }
            }
            throw th;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:50:0x00b2 A[SYNTHETIC, Splitter:B:50:0x00b2] */
    /* JADX WARNING: Removed duplicated region for block: B:55:0x00bc  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x00cd  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void restoreLastPackageEnableState(java.io.File r18) {
        /*
            r17 = this;
            r1 = r17
            java.lang.String r2 = "IOEception"
            java.lang.String r3 = "Backup:BackupManagerService"
            java.io.File r4 = getCachedInstallFile()
            boolean r0 = r4.exists()
            if (r0 == 0) goto L_0x0013
            r4.delete()
        L_0x0013:
            boolean r0 = r18.exists()
            if (r0 == 0) goto L_0x00e0
            r5 = 0
            r6 = 0
            r7 = -2147483648(0xffffffff80000000, float:-0.0)
            r8 = 0
            java.io.FileInputStream r0 = new java.io.FileInputStream     // Catch:{ IOException -> 0x00a7, all -> 0x00a0 }
            r9 = r18
            r0.<init>(r9)     // Catch:{ IOException -> 0x009e }
            r5 = r0
            java.util.ArrayList r0 = new java.util.ArrayList     // Catch:{ IOException -> 0x009e }
            r0.<init>()     // Catch:{ IOException -> 0x009e }
            r10 = r0
        L_0x002c:
            int r0 = r5.read()     // Catch:{ IOException -> 0x009e }
            r11 = r0
            if (r0 < 0) goto L_0x003c
            byte r0 = (byte) r11     // Catch:{ IOException -> 0x009e }
            java.lang.Byte r0 = java.lang.Byte.valueOf(r0)     // Catch:{ IOException -> 0x009e }
            r10.add(r0)     // Catch:{ IOException -> 0x009e }
            goto L_0x002c
        L_0x003c:
            int r0 = r10.size()     // Catch:{ IOException -> 0x009e }
            byte[] r0 = new byte[r0]     // Catch:{ IOException -> 0x009e }
            r12 = r0
            r0 = 0
            r13 = r0
        L_0x0045:
            int r14 = r10.size()     // Catch:{ IOException -> 0x009e }
            if (r13 >= r14) goto L_0x005a
            java.lang.Object r14 = r10.get(r13)     // Catch:{ IOException -> 0x009e }
            java.lang.Byte r14 = (java.lang.Byte) r14     // Catch:{ IOException -> 0x009e }
            byte r14 = r14.byteValue()     // Catch:{ IOException -> 0x009e }
            r12[r13] = r14     // Catch:{ IOException -> 0x009e }
            int r13 = r13 + 1
            goto L_0x0045
        L_0x005a:
            java.lang.String r13 = new java.lang.String     // Catch:{ IOException -> 0x009e }
            r13.<init>(r12)     // Catch:{ IOException -> 0x009e }
            java.lang.String r14 = " "
            java.lang.String[] r13 = r13.split(r14)     // Catch:{ IOException -> 0x009e }
            int r14 = r13.length     // Catch:{ IOException -> 0x009e }
            r15 = 2
            r16 = 1
            if (r14 != r15) goto L_0x0078
            r0 = r13[r0]     // Catch:{ IOException -> 0x009e }
            r6 = r0
            r0 = r13[r16]     // Catch:{ NumberFormatException -> 0x0076 }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x0076 }
            r7 = r0
            goto L_0x0077
        L_0x0076:
            r0 = move-exception
        L_0x0077:
            goto L_0x0090
        L_0x0078:
            int r14 = r13.length     // Catch:{ IOException -> 0x009e }
            r15 = 3
            if (r14 != r15) goto L_0x0090
            r0 = r13[r0]     // Catch:{ IOException -> 0x009e }
            r6 = r0
            r0 = r13[r16]     // Catch:{ NumberFormatException -> 0x008f }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x008f }
            r7 = r0
            r0 = 2
            r0 = r13[r0]     // Catch:{ NumberFormatException -> 0x008f }
            int r0 = java.lang.Integer.parseInt(r0)     // Catch:{ NumberFormatException -> 0x008f }
            r8 = r0
            goto L_0x0090
        L_0x008f:
            r0 = move-exception
        L_0x0090:
            r5.close()     // Catch:{ IOException -> 0x0095 }
        L_0x0094:
            goto L_0x00b6
        L_0x0095:
            r0 = move-exception
            r10 = r0
            r0 = r10
            android.util.Slog.e(r3, r2, r0)
            goto L_0x0094
        L_0x009c:
            r0 = move-exception
            goto L_0x00a3
        L_0x009e:
            r0 = move-exception
            goto L_0x00aa
        L_0x00a0:
            r0 = move-exception
            r9 = r18
        L_0x00a3:
            r10 = r6
            r6 = r5
            r5 = r0
            goto L_0x00d3
        L_0x00a7:
            r0 = move-exception
            r9 = r18
        L_0x00aa:
            java.lang.String r10 = "IOException"
            android.util.Slog.e(r3, r10, r0)     // Catch:{ all -> 0x009c }
            if (r5 == 0) goto L_0x00b6
            r5.close()     // Catch:{ IOException -> 0x0095 }
            goto L_0x0094
        L_0x00b6:
            if (r6 == 0) goto L_0x00cd
            r0 = -2147483648(0xffffffff80000000, float:-0.0)
            if (r7 == r0) goto L_0x00cd
            java.lang.String r0 = "Unfinished backup package found, restore it's enable state"
            android.util.Slog.v(r3, r0)
            android.content.Context r0 = r1.mContext
            java.lang.String r0 = r1.getDefaultIme(r0)
            r1.mPackageLastEnableState = r7
            r1.enablePackage(r6, r8, r0)
            goto L_0x00e2
        L_0x00cd:
            java.lang.String r0 = "backup_pkg_enable_state file broken"
            android.util.Slog.e(r3, r0)
            goto L_0x00e2
        L_0x00d3:
            if (r6 == 0) goto L_0x00df
            r6.close()     // Catch:{ IOException -> 0x00d9 }
            goto L_0x00df
        L_0x00d9:
            r0 = move-exception
            r11 = r0
            r0 = r11
            android.util.Slog.e(r3, r2, r0)
        L_0x00df:
            throw r5
        L_0x00e0:
            r9 = r18
        L_0x00e2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.miui.server.BackupManagerService.restoreLastPackageEnableState(java.io.File):void");
    }

    public void onApkInstalled() {
        if (!BackupManager.isSysAppForBackup(this.mContext, this.mCurrentWorkingPkg)) {
            Slog.d(TAG, "onApkInstalled, mCurrentWorkingPkg:" + this.mCurrentWorkingPkg);
            disablePackageAndWait(this.mCurrentWorkingPkg, this.mAppUserId);
        }
    }

    public void errorOccur(int err) throws RemoteException {
        if (this.mLastError == 0) {
            this.mLastError = err;
            IPackageBackupRestoreObserver iPackageBackupRestoreObserver = this.mBackupRestoreObserver;
            if (iPackageBackupRestoreObserver != null) {
                iPackageBackupRestoreObserver.onError(this.mCurrentWorkingPkg, this.mCurrentWorkingFeature, err);
            }
        }
    }

    public String getCurrentRunningPackage() throws RemoteException {
        return this.mCurrentWorkingPkg;
    }

    public int getCurrentWorkingFeature() throws RemoteException {
        return this.mCurrentWorkingFeature;
    }

    public int getState() throws RemoteException {
        return this.mState;
    }

    public int getBackupTimeoutScale() throws RemoteException {
        if (BackupManager.isProgRecordApp(this.mCurrentWorkingPkg, this.mCurrentWorkingFeature)) {
            return 6;
        }
        return 1;
    }

    public boolean shouldSkipData() {
        return this.mShouldSkipData;
    }

    public int getAppUserId() {
        return this.mAppUserId;
    }

    private class FullBackupRestoreObserver extends IFullBackupRestoreObserver.Stub {
        private FullBackupRestoreObserver() {
        }

        public void onStartBackup() throws RemoteException {
        }

        public void onBackupPackage(String name) throws RemoteException {
            if (!TextUtils.isEmpty(BackupManagerService.this.mCurrentWorkingPkg)) {
                BackupManagerService.this.mCurrentWorkingPkg.equals(name);
            }
            if (BackupManagerService.this.mBackupRestoreObserver != null) {
                BackupManagerService.this.mBackupRestoreObserver.onBackupStart(BackupManagerService.this.mCurrentWorkingPkg, BackupManagerService.this.mCurrentWorkingFeature);
            }
        }

        public void onEndBackup() throws RemoteException {
            if (BackupManagerService.this.mBackupRestoreObserver != null) {
                BackupManagerService.this.mBackupRestoreObserver.onBackupEnd(BackupManagerService.this.mCurrentWorkingPkg, BackupManagerService.this.mCurrentWorkingFeature);
            }
        }

        public void onStartRestore() throws RemoteException {
        }

        public void onRestorePackage(String name) throws RemoteException {
            if (BackupManagerService.this.mBackupRestoreObserver != null) {
                BackupManagerService.this.mBackupRestoreObserver.onRestoreStart(BackupManagerService.this.mCurrentWorkingPkg, BackupManagerService.this.mCurrentWorkingFeature);
            }
        }

        public void onEndRestore() throws RemoteException {
            if (BackupManagerService.this.mBackupRestoreObserver != null) {
                BackupManagerService.this.mBackupRestoreObserver.onRestoreEnd(BackupManagerService.this.mCurrentWorkingPkg, BackupManagerService.this.mCurrentWorkingFeature);
            }
        }

        public void onTimeout() throws RemoteException {
        }
    }

    private class BackupHandler extends Handler {
        private BackupHandler(Looper looper) {
            super(looper);
        }
    }

    private class DeathLinker implements IBinder.DeathRecipient {
        private DeathLinker() {
        }

        public void binderDied() {
            Slog.d(BackupManagerService.TAG, "Client binder has died. Start canceling...");
            boolean unused = BackupManagerService.this.mIsCanceling = true;
            BackupManagerService.this.scheduleReleaseResource();
            BackupManagerService.this.waitForTheLastWorkingTask();
            boolean unused2 = BackupManagerService.this.mIsCanceling = false;
            int unused3 = BackupManagerService.this.mOwnerPid = -1;
            BackupManagerService.this.mICaller.unlinkToDeath(BackupManagerService.this.mDeathLinker, 0);
            IBinder unused4 = BackupManagerService.this.mICaller = null;
            String unused5 = BackupManagerService.this.mPreviousWorkingPkg = null;
            try {
                BackupManagerService.this.broadcastServiceIdle();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Slog.d(BackupManagerService.TAG, "Client binder has died. Cancel completed!");
        }
    }

    private String getDefaultIme(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), "default_input_method");
    }

    private boolean isDefaultIme(String pkg, String defaultIme) {
        ComponentName cn;
        if (pkg == null || defaultIme == null || (cn = ComponentName.unflattenFromString(defaultIme)) == null || !TextUtils.equals(pkg, cn.getPackageName())) {
            return false;
        }
        return true;
    }

    public void delCacheBackup() {
        int uid = Binder.getCallingUid();
        if (uid == 9802 || uid == 9800) {
            FileUtils.deleteContents(new File("/cache/backup"));
        }
    }

    public boolean isCanceling() throws RemoteException {
        return this.mIsCanceling;
    }

    private void closeBackupWriteStream(ParcelFileDescriptor outputFile) {
        if (outputFile != null) {
            try {
                IoBridge.closeAndSignalBlockedThreads(outputFile.getFileDescriptor());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void releaseBackupWriteStream(ParcelFileDescriptor outputFile) {
        if (outputFile != null) {
            this.mHandler.post(new Runnable(outputFile) {
                private final /* synthetic */ ParcelFileDescriptor f$0;

                {
                    this.f$0 = r1;
                }

                public final void run() {
                    BackupManagerService.lambda$releaseBackupWriteStream$0(this.f$0);
                }
            });
        }
    }

    static /* synthetic */ void lambda$releaseBackupWriteStream$0(ParcelFileDescriptor outputFile) {
        byte[] b = new byte[1024];
        FileInputStream fis = new FileInputStream(outputFile.getFileDescriptor());
        do {
            try {
            } catch (IOException e) {
                Slog.e(TAG, "releaseBackupReadStream", e);
                fis.close();
                return;
            } catch (Throwable th) {
                try {
                    fis.close();
                } catch (IOException e2) {
                    Slog.e(TAG, "IOException", e2);
                }
                throw th;
            }
        } while (fis.read(b) > 0);
        try {
            fis.close();
        } catch (IOException e3) {
            Slog.e(TAG, "IOException", e3);
        }
    }

    private int getApplicationEnabledSetting(String packageName, int userId) {
        try {
            return this.mPackageManagerBinder.getApplicationEnabledSetting(packageName, userId);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private void setApplicationEnabledSetting(String packageName, int userId, int newState, int flags) {
        try {
            this.mPackageManagerBinder.setApplicationEnabledSetting(packageName, newState, flags, userId, this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }
}
