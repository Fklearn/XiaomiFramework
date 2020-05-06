package com.android.server.backup.fullbackup;

import android.app.IBackupAgent;
import android.app.backup.FullBackupDataOutput;
import android.app.backup.IBackupCallback;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Slog;
import com.android.internal.util.Preconditions;
import com.android.server.AppWidgetBackupBridge;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.backup.BackupManagerService;
import com.android.server.backup.BackupManagerServiceInjector;
import com.android.server.backup.BackupRestoreTask;
import com.android.server.backup.UserBackupManagerService;
import com.android.server.backup.remote.RemoteCall;
import com.android.server.backup.remote.RemoteCallable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class FullBackupEngine {
    /* access modifiers changed from: private */
    public UserBackupManagerService backupManagerService;
    private IBackupAgent mAgent;
    /* access modifiers changed from: private */
    public final BackupAgentTimeoutParameters mAgentTimeoutParameters;
    private boolean mIncludeApks;
    private final int mOpToken;
    private OutputStream mOutput;
    public int mOutputFD;
    private PackageInfo mPkg;
    private FullBackupPreflight mPreflightHook;
    /* access modifiers changed from: private */
    public final long mQuota;
    /* access modifiers changed from: private */
    public BackupRestoreTask mTimeoutMonitor;
    /* access modifiers changed from: private */
    public final int mTransportFlags;

    class FullBackupRunner implements Runnable {
        private final IBackupAgent mAgent;
        private final File mFilesDir;
        private final boolean mIncludeApks;
        private final PackageInfo mPackage;
        private final PackageManager mPackageManager;
        private final ParcelFileDescriptor mPipe;
        private final int mToken;
        private final int mUserId;

        FullBackupRunner(UserBackupManagerService userBackupManagerService, PackageInfo packageInfo, IBackupAgent agent, ParcelFileDescriptor pipe, int token, boolean includeApks) throws IOException {
            this.mUserId = BackupManagerServiceInjector.getAppUserId(FullBackupEngine.this.mOutputFD, userBackupManagerService.getUserId());
            this.mPackageManager = FullBackupEngine.this.backupManagerService.getPackageManager();
            this.mPackage = packageInfo;
            this.mAgent = agent;
            this.mPipe = ParcelFileDescriptor.dup(pipe.getFileDescriptor());
            this.mToken = token;
            this.mIncludeApks = includeApks;
            this.mFilesDir = userBackupManagerService.getDataDir();
        }

        public void run() {
            long timeout;
            try {
                AppMetadataBackupWriter appMetadataBackupWriter = new AppMetadataBackupWriter(new FullBackupDataOutput(this.mPipe, -1, FullBackupEngine.this.mTransportFlags), this.mPackageManager);
                String packageName = this.mPackage.packageName;
                boolean isSharedStorage = UserBackupManagerService.SHARED_BACKUP_AGENT_PACKAGE.equals(packageName);
                boolean writeApk = shouldWriteApk(this.mPackage.applicationInfo, this.mIncludeApks, isSharedStorage);
                if (!isSharedStorage) {
                    Slog.d(BackupManagerService.TAG, "Writing manifest for " + packageName);
                    File manifestFile = new File(this.mFilesDir, UserBackupManagerService.BACKUP_MANIFEST_FILENAME);
                    appMetadataBackupWriter.backupManifest(this.mPackage, manifestFile, this.mFilesDir, writeApk);
                    manifestFile.delete();
                    byte[] widgetData = AppWidgetBackupBridge.getWidgetState(packageName, this.mUserId);
                    if (widgetData != null && widgetData.length > 0) {
                        File metadataFile = new File(this.mFilesDir, UserBackupManagerService.BACKUP_METADATA_FILENAME);
                        appMetadataBackupWriter.backupWidget(this.mPackage, metadataFile, this.mFilesDir, widgetData);
                        metadataFile.delete();
                    }
                }
                if (writeApk) {
                    appMetadataBackupWriter.backupApk(this.mPackage);
                    appMetadataBackupWriter.backupObb(this.mUserId, this.mPackage);
                }
                Slog.d(BackupManagerService.TAG, "Calling doFullBackup() on " + packageName);
                if (isSharedStorage) {
                    timeout = FullBackupEngine.this.mAgentTimeoutParameters.getSharedBackupAgentTimeoutMillis();
                } else {
                    timeout = FullBackupEngine.this.mAgentTimeoutParameters.getFullBackupAgentTimeoutMillis();
                }
                BackupManagerServiceInjector.prepareOperationTimeout(FullBackupEngine.this.backupManagerService, this.mToken, timeout, FullBackupEngine.this.mTimeoutMonitor, 0, FullBackupEngine.this.mOutputFD);
                IBackupAgent iBackupAgent = this.mAgent;
                ParcelFileDescriptor parcelFileDescriptor = this.mPipe;
                IBackupAgent iBackupAgent2 = iBackupAgent;
                ParcelFileDescriptor parcelFileDescriptor2 = parcelFileDescriptor;
                BackupManagerServiceInjector.doFullBackup(iBackupAgent2, parcelFileDescriptor2, FullBackupEngine.this.mQuota, this.mToken, FullBackupEngine.this.backupManagerService.getBackupManagerBinder(), FullBackupEngine.this.mTransportFlags, FullBackupEngine.this.mOutputFD);
                try {
                    this.mPipe.close();
                } catch (IOException e) {
                }
            } catch (IOException e2) {
                Slog.e(BackupManagerService.TAG, "Error running full backup for " + this.mPackage.packageName, e2);
                this.mPipe.close();
            } catch (RemoteException e3) {
                Slog.e(BackupManagerService.TAG, "Remote agent vanished during full backup of " + this.mPackage.packageName, e3);
                this.mPipe.close();
            } catch (Throwable th) {
                Throwable th2 = th;
                try {
                    this.mPipe.close();
                } catch (IOException e4) {
                }
                throw th2;
            }
        }

        private boolean shouldWriteApk(ApplicationInfo applicationInfo, boolean includeApks, boolean isSharedStorage) {
            boolean isSystemApp = (applicationInfo.flags & 1) != 0;
            boolean isUpdatedSystemApp = (applicationInfo.flags & 128) != 0;
            if (!includeApks || isSharedStorage || (isSystemApp && !isUpdatedSystemApp)) {
                return false;
            }
            return true;
        }
    }

    public FullBackupEngine(UserBackupManagerService backupManagerService2, OutputStream output, FullBackupPreflight preflightHook, PackageInfo pkg, boolean alsoApks, BackupRestoreTask timeoutMonitor, long quota, int opToken, int transportFlags) {
        this.backupManagerService = backupManagerService2;
        this.mOutput = output;
        this.mPreflightHook = preflightHook;
        this.mPkg = pkg;
        this.mIncludeApks = alsoApks;
        this.mTimeoutMonitor = timeoutMonitor;
        this.mQuota = quota;
        this.mOpToken = opToken;
        this.mTransportFlags = transportFlags;
        this.mAgentTimeoutParameters = (BackupAgentTimeoutParameters) Preconditions.checkNotNull(backupManagerService2.getAgentTimeoutParameters(), "Timeout parameters cannot be null");
    }

    public int preflightCheck() throws RemoteException {
        if (this.mPreflightHook == null) {
            Slog.v(BackupManagerService.TAG, "No preflight check");
            return 0;
        } else if (initializeAgent()) {
            int result = this.mPreflightHook.preflightFullBackup(this.mPkg, this.mAgent);
            Slog.v(BackupManagerService.TAG, "preflight returned " + result);
            return result;
        } else {
            Slog.w(BackupManagerService.TAG, "Unable to bind to full agent for " + this.mPkg.packageName);
            return -1003;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:36:0x00ee A[Catch:{ IOException -> 0x0104 }] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x011a A[Catch:{ IOException -> 0x012d }] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public int backupOnePackage() throws android.os.RemoteException {
        /*
            r17 = this;
            r9 = r17
            java.lang.String r10 = "Error bringing down backup stack"
            r11 = -1003(0xfffffffffffffc15, float:NaN)
            boolean r0 = r17.initializeAgent()
            java.lang.String r12 = "BackupManagerService"
            if (r0 == 0) goto L_0x0134
            android.app.IBackupAgent r0 = r9.mAgent
            int r1 = r9.mOutputFD
            r13 = 0
            com.android.server.backup.BackupManagerServiceInjector.linkToDeath(r0, r1, r13)
            r1 = 0
            r14 = 0
            r15 = 1
            android.os.ParcelFileDescriptor[] r0 = android.os.ParcelFileDescriptor.createPipe()     // Catch:{ IOException -> 0x00c0 }
            r16 = r0
            com.android.server.backup.fullbackup.FullBackupEngine$FullBackupRunner r0 = new com.android.server.backup.fullbackup.FullBackupEngine$FullBackupRunner     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            com.android.server.backup.UserBackupManagerService r3 = r9.backupManagerService     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            android.content.pm.PackageInfo r4 = r9.mPkg     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            android.app.IBackupAgent r5 = r9.mAgent     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            r6 = r16[r15]     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            int r7 = r9.mOpToken     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            boolean r8 = r9.mIncludeApks     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            r1 = r0
            r2 = r17
            r1.<init>(r3, r4, r5, r6, r7, r8)     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            r1 = r16[r15]     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            r1.close()     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            r16[r15] = r13     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            java.lang.Thread r1 = new java.lang.Thread     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            java.lang.String r2 = "app-data-runner"
            r1.<init>(r0, r2)     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            r1.start()     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            r2 = r16[r14]     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            java.io.OutputStream r3 = r9.mOutput     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            int r4 = r9.mOutputFD     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            com.android.server.backup.BackupManagerServiceInjector.routeSocketDataToOutput(r2, r3, r4)     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            android.app.IBackupAgent r2 = r9.mAgent     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            int r3 = r9.mOpToken     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            boolean r2 = com.android.server.backup.BackupManagerServiceInjector.needUpdateToken(r2, r3)     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            if (r2 == 0) goto L_0x007a
            com.android.server.backup.UserBackupManagerService r2 = r9.backupManagerService     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            int r3 = r9.mOpToken     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            boolean r2 = r2.waitUntilOperationComplete(r3)     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            if (r2 != 0) goto L_0x007a
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            r2.<init>()     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            java.lang.String r3 = "Full backup failed on package "
            r2.append(r3)     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            android.content.pm.PackageInfo r3 = r9.mPkg     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            java.lang.String r3 = r3.packageName     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            r2.append(r3)     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            java.lang.String r2 = r2.toString()     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            android.util.Slog.e(r12, r2)     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            goto L_0x0093
        L_0x007a:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            r2.<init>()     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            java.lang.String r3 = "Full package backup success: "
            r2.append(r3)     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            android.content.pm.PackageInfo r3 = r9.mPkg     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            java.lang.String r3 = r3.packageName     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            r2.append(r3)     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            java.lang.String r2 = r2.toString()     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            android.util.Slog.d(r12, r2)     // Catch:{ IOException -> 0x00b7, all -> 0x00b4 }
            r11 = 0
        L_0x0093:
            java.io.OutputStream r0 = r9.mOutput     // Catch:{ IOException -> 0x00ac }
            r0.flush()     // Catch:{ IOException -> 0x00ac }
            r0 = r16[r14]     // Catch:{ IOException -> 0x00ac }
            if (r0 == 0) goto L_0x00a2
            r0 = r16[r14]     // Catch:{ IOException -> 0x00ac }
            r0.close()     // Catch:{ IOException -> 0x00ac }
        L_0x00a2:
            r0 = r16[r15]     // Catch:{ IOException -> 0x00ac }
            if (r0 == 0) goto L_0x00ab
            r0 = r16[r15]     // Catch:{ IOException -> 0x00ac }
            r0.close()     // Catch:{ IOException -> 0x00ac }
        L_0x00ab:
            goto L_0x010d
        L_0x00ac:
            r0 = move-exception
            android.util.Slog.w(r12, r10)
            r0 = -1000(0xfffffffffffffc18, float:NaN)
            r11 = r0
            goto L_0x010d
        L_0x00b4:
            r0 = move-exception
            r1 = r0
            goto L_0x0113
        L_0x00b7:
            r0 = move-exception
            r1 = r16
            goto L_0x00c1
        L_0x00bb:
            r0 = move-exception
            r16 = r1
            r1 = r0
            goto L_0x0113
        L_0x00c0:
            r0 = move-exception
        L_0x00c1:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ all -> 0x00bb }
            r2.<init>()     // Catch:{ all -> 0x00bb }
            java.lang.String r3 = "Error backing up "
            r2.append(r3)     // Catch:{ all -> 0x00bb }
            android.content.pm.PackageInfo r3 = r9.mPkg     // Catch:{ all -> 0x00bb }
            java.lang.String r3 = r3.packageName     // Catch:{ all -> 0x00bb }
            r2.append(r3)     // Catch:{ all -> 0x00bb }
            java.lang.String r3 = ": "
            r2.append(r3)     // Catch:{ all -> 0x00bb }
            java.lang.String r3 = r0.getMessage()     // Catch:{ all -> 0x00bb }
            r2.append(r3)     // Catch:{ all -> 0x00bb }
            java.lang.String r2 = r2.toString()     // Catch:{ all -> 0x00bb }
            android.util.Slog.e(r12, r2)     // Catch:{ all -> 0x00bb }
            r2 = -1003(0xfffffffffffffc15, float:NaN)
            java.io.OutputStream r0 = r9.mOutput     // Catch:{ IOException -> 0x0104 }
            r0.flush()     // Catch:{ IOException -> 0x0104 }
            if (r1 == 0) goto L_0x0100
            r0 = r1[r14]     // Catch:{ IOException -> 0x0104 }
            if (r0 == 0) goto L_0x00f7
            r0 = r1[r14]     // Catch:{ IOException -> 0x0104 }
            r0.close()     // Catch:{ IOException -> 0x0104 }
        L_0x00f7:
            r0 = r1[r15]     // Catch:{ IOException -> 0x0104 }
            if (r0 == 0) goto L_0x0100
            r0 = r1[r15]     // Catch:{ IOException -> 0x0104 }
            r0.close()     // Catch:{ IOException -> 0x0104 }
        L_0x0100:
            r16 = r1
            r11 = r2
            goto L_0x010d
        L_0x0104:
            r0 = move-exception
            android.util.Slog.w(r12, r10)
            r0 = -1000(0xfffffffffffffc18, float:NaN)
            r11 = r0
            r16 = r1
        L_0x010d:
            android.app.IBackupAgent r0 = r9.mAgent
            com.android.server.backup.BackupManagerServiceInjector.unlinkToDeath(r0)
            goto L_0x014c
        L_0x0113:
            java.io.OutputStream r0 = r9.mOutput     // Catch:{ IOException -> 0x012d }
            r0.flush()     // Catch:{ IOException -> 0x012d }
            if (r16 == 0) goto L_0x012c
            r0 = r16[r14]     // Catch:{ IOException -> 0x012d }
            if (r0 == 0) goto L_0x0123
            r0 = r16[r14]     // Catch:{ IOException -> 0x012d }
            r0.close()     // Catch:{ IOException -> 0x012d }
        L_0x0123:
            r0 = r16[r15]     // Catch:{ IOException -> 0x012d }
            if (r0 == 0) goto L_0x012c
            r0 = r16[r15]     // Catch:{ IOException -> 0x012d }
            r0.close()     // Catch:{ IOException -> 0x012d }
        L_0x012c:
            goto L_0x0133
        L_0x012d:
            r0 = move-exception
            android.util.Slog.w(r12, r10)
            r11 = -1000(0xfffffffffffffc18, float:NaN)
        L_0x0133:
            throw r1
        L_0x0134:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Unable to bind to full agent for "
            r0.append(r1)
            android.content.pm.PackageInfo r1 = r9.mPkg
            java.lang.String r1 = r1.packageName
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            android.util.Slog.w(r12, r0)
        L_0x014c:
            r17.tearDown()
            return r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.fullbackup.FullBackupEngine.backupOnePackage():int");
    }

    public void sendQuotaExceeded(long backupDataBytes, long quotaBytes) {
        if (initializeAgent()) {
            try {
                RemoteCall.execute(new RemoteCallable(backupDataBytes, quotaBytes) {
                    private final /* synthetic */ long f$1;
                    private final /* synthetic */ long f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r4;
                    }

                    public final void call(Object obj) {
                        FullBackupEngine.this.lambda$sendQuotaExceeded$0$FullBackupEngine(this.f$1, this.f$2, (IBackupCallback) obj);
                    }
                }, this.mAgentTimeoutParameters.getQuotaExceededTimeoutMillis());
            } catch (RemoteException e) {
                Slog.e(BackupManagerService.TAG, "Remote exception while telling agent about quota exceeded");
            }
        }
    }

    public /* synthetic */ void lambda$sendQuotaExceeded$0$FullBackupEngine(long backupDataBytes, long quotaBytes, IBackupCallback callback) throws RemoteException {
        this.mAgent.doQuotaExceeded(backupDataBytes, quotaBytes, callback);
    }

    private boolean initializeAgent() {
        if (this.mAgent == null) {
            Slog.d(BackupManagerService.TAG, "Binding to full backup agent : " + this.mPkg.packageName);
            this.mAgent = this.backupManagerService.bindToAgentSynchronous(this.mPkg.applicationInfo, 1);
        }
        if (this.mAgent != null) {
            return true;
        }
        return false;
    }

    private void tearDown() {
        PackageInfo packageInfo = this.mPkg;
        if (packageInfo != null) {
            this.backupManagerService.tearDownAgentAndKill(packageInfo.applicationInfo);
        }
    }
}
