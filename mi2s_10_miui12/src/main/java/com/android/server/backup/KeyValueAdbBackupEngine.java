package com.android.server.backup;

import android.app.IBackupAgent;
import android.app.backup.FullBackup;
import android.app.backup.FullBackupDataOutput;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.SELinux;
import android.util.Slog;
import com.android.internal.util.Preconditions;
import com.android.server.backup.fullbackup.AppMetadataBackupWriter;
import com.android.server.backup.remote.ServiceBackupCallback;
import com.android.server.job.controllers.JobStatus;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import libcore.io.IoUtils;

public class KeyValueAdbBackupEngine {
    private static final String BACKUP_KEY_VALUE_BACKUP_DATA_FILENAME_SUFFIX = ".data";
    private static final String BACKUP_KEY_VALUE_BLANK_STATE_FILENAME = "blank_state";
    private static final String BACKUP_KEY_VALUE_DIRECTORY_NAME = "key_value_dir";
    private static final String BACKUP_KEY_VALUE_NEW_STATE_FILENAME_SUFFIX = ".new";
    private static final boolean DEBUG = false;
    private static final String TAG = "KeyValueAdbBackupEngine";
    private final BackupAgentTimeoutParameters mAgentTimeoutParameters;
    private ParcelFileDescriptor mBackupData;
    /* access modifiers changed from: private */
    public final File mBackupDataName;
    /* access modifiers changed from: private */
    public UserBackupManagerService mBackupManagerService;
    private final File mBlankStateName = new File(this.mStateDir, BACKUP_KEY_VALUE_BLANK_STATE_FILENAME);
    private final PackageInfo mCurrentPackage;
    /* access modifiers changed from: private */
    public final File mDataDir;
    /* access modifiers changed from: private */
    public final File mManifestFile;
    private ParcelFileDescriptor mNewState;
    private final File mNewStateName;
    private final OutputStream mOutput;
    /* access modifiers changed from: private */
    public final PackageManager mPackageManager;
    private ParcelFileDescriptor mSavedState;
    private final File mStateDir;

    public KeyValueAdbBackupEngine(OutputStream output, PackageInfo packageInfo, UserBackupManagerService backupManagerService, PackageManager packageManager, File baseStateDir, File dataDir) {
        this.mOutput = output;
        this.mCurrentPackage = packageInfo;
        this.mBackupManagerService = backupManagerService;
        this.mPackageManager = packageManager;
        this.mDataDir = dataDir;
        this.mStateDir = new File(baseStateDir, BACKUP_KEY_VALUE_DIRECTORY_NAME);
        this.mStateDir.mkdirs();
        String pkg = this.mCurrentPackage.packageName;
        File file = this.mDataDir;
        this.mBackupDataName = new File(file, pkg + ".data");
        File file2 = this.mStateDir;
        this.mNewStateName = new File(file2, pkg + ".new");
        this.mManifestFile = new File(this.mDataDir, UserBackupManagerService.BACKUP_MANIFEST_FILENAME);
        this.mAgentTimeoutParameters = (BackupAgentTimeoutParameters) Preconditions.checkNotNull(backupManagerService.getAgentTimeoutParameters(), "Timeout parameters cannot be null");
    }

    public void backupOnePackage() throws IOException {
        ApplicationInfo targetApp = this.mCurrentPackage.applicationInfo;
        try {
            prepareBackupFiles(this.mCurrentPackage.packageName);
            IBackupAgent agent = bindToAgent(targetApp);
            if (agent == null) {
                Slog.e(TAG, "Failed binding to BackupAgent for package " + this.mCurrentPackage.packageName);
                cleanup();
            } else if (!invokeAgentForAdbBackup(this.mCurrentPackage.packageName, agent)) {
                Slog.e(TAG, "Backup Failed for package " + this.mCurrentPackage.packageName);
                cleanup();
            } else {
                writeBackupData();
                cleanup();
            }
        } catch (FileNotFoundException e) {
            Slog.e(TAG, "Failed creating files for package " + this.mCurrentPackage.packageName + " will ignore package. " + e);
        } catch (Throwable th) {
            cleanup();
            throw th;
        }
    }

    private void prepareBackupFiles(String packageName) throws FileNotFoundException {
        this.mSavedState = ParcelFileDescriptor.open(this.mBlankStateName, 402653184);
        this.mBackupData = ParcelFileDescriptor.open(this.mBackupDataName, 1006632960);
        if (!SELinux.restorecon(this.mBackupDataName)) {
            Slog.e(TAG, "SELinux restorecon failed on " + this.mBackupDataName);
        }
        this.mNewState = ParcelFileDescriptor.open(this.mNewStateName, 1006632960);
    }

    private IBackupAgent bindToAgent(ApplicationInfo targetApp) {
        try {
            return this.mBackupManagerService.bindToAgentSynchronous(targetApp, 0);
        } catch (SecurityException e) {
            Slog.e(TAG, "error in binding to agent for package " + targetApp.packageName + ". " + e);
            return null;
        }
    }

    private boolean invokeAgentForAdbBackup(String packageName, IBackupAgent agent) {
        String str = packageName;
        int token = this.mBackupManagerService.generateRandomIntegerToken();
        try {
            this.mBackupManagerService.prepareOperationTimeout(token, this.mAgentTimeoutParameters.getKvBackupAgentTimeoutMillis(), (BackupRestoreTask) null, 0);
            ServiceBackupCallback serviceBackupCallback = new ServiceBackupCallback(this.mBackupManagerService.getBackupManagerBinder(), token);
            agent.doBackup(this.mSavedState, this.mBackupData, this.mNewState, JobStatus.NO_LATEST_RUNTIME, serviceBackupCallback, 0);
            if (this.mBackupManagerService.waitUntilOperationComplete(token)) {
                return true;
            }
            Slog.e(TAG, "Key-value backup failed on package " + str);
            return false;
        } catch (RemoteException e) {
            Slog.e(TAG, "Error invoking agent for backup on " + str + ". " + e);
            return false;
        }
    }

    class KeyValueAdbBackupDataCopier implements Runnable {
        private final PackageInfo mPackage;
        private final ParcelFileDescriptor mPipe;
        private final int mToken;

        KeyValueAdbBackupDataCopier(PackageInfo pack, ParcelFileDescriptor pipe, int token) throws IOException {
            this.mPackage = pack;
            this.mPipe = ParcelFileDescriptor.dup(pipe.getFileDescriptor());
            this.mToken = token;
        }

        public void run() {
            try {
                FullBackupDataOutput output = new FullBackupDataOutput(this.mPipe);
                new AppMetadataBackupWriter(output, KeyValueAdbBackupEngine.this.mPackageManager).backupManifest(this.mPackage, KeyValueAdbBackupEngine.this.mManifestFile, KeyValueAdbBackupEngine.this.mDataDir, "k", (String) null, false);
                KeyValueAdbBackupEngine.this.mManifestFile.delete();
                FullBackup.backupToTar(this.mPackage.packageName, "k", (String) null, KeyValueAdbBackupEngine.this.mDataDir.getAbsolutePath(), KeyValueAdbBackupEngine.this.mBackupDataName.getAbsolutePath(), output);
                try {
                    new FileOutputStream(this.mPipe.getFileDescriptor()).write(new byte[4]);
                } catch (IOException e) {
                    Slog.e(KeyValueAdbBackupEngine.TAG, "Unable to finalize backup stream!");
                }
                try {
                    KeyValueAdbBackupEngine.this.mBackupManagerService.getBackupManagerBinder().opComplete(this.mToken, 0);
                } catch (RemoteException e2) {
                }
            } catch (IOException e3) {
                Slog.e(KeyValueAdbBackupEngine.TAG, "Error running full backup for " + this.mPackage.packageName + ". " + e3);
            } catch (Throwable th) {
                IoUtils.closeQuietly(this.mPipe);
                throw th;
            }
            IoUtils.closeQuietly(this.mPipe);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:20:0x00a5  */
    /* JADX WARNING: Removed duplicated region for block: B:23:0x00b5  */
    /* JADX WARNING: Removed duplicated region for block: B:26:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void writeBackupData() throws java.io.IOException {
        /*
            r13 = this;
            java.lang.String r0 = "KeyValueAdbBackupEngine"
            com.android.server.backup.UserBackupManagerService r1 = r13.mBackupManagerService
            int r1 = r1.generateRandomIntegerToken()
            com.android.server.backup.BackupAgentTimeoutParameters r2 = r13.mAgentTimeoutParameters
            long r8 = r2.getKvBackupAgentTimeoutMillis()
            r2 = 0
            r10 = 0
            r11 = 1
            android.os.ParcelFileDescriptor[] r3 = android.os.ParcelFileDescriptor.createPipe()     // Catch:{ IOException -> 0x007a, all -> 0x0077 }
            r12 = r3
            com.android.server.backup.UserBackupManagerService r2 = r13.mBackupManagerService     // Catch:{ IOException -> 0x0075 }
            r6 = 0
            r7 = 0
            r3 = r1
            r4 = r8
            r2.prepareOperationTimeout(r3, r4, r6, r7)     // Catch:{ IOException -> 0x0075 }
            com.android.server.backup.KeyValueAdbBackupEngine$KeyValueAdbBackupDataCopier r2 = new com.android.server.backup.KeyValueAdbBackupEngine$KeyValueAdbBackupDataCopier     // Catch:{ IOException -> 0x0075 }
            android.content.pm.PackageInfo r3 = r13.mCurrentPackage     // Catch:{ IOException -> 0x0075 }
            r4 = r12[r11]     // Catch:{ IOException -> 0x0075 }
            r2.<init>(r3, r4, r1)     // Catch:{ IOException -> 0x0075 }
            r3 = r12[r11]     // Catch:{ IOException -> 0x0075 }
            r3.close()     // Catch:{ IOException -> 0x0075 }
            r3 = 0
            r12[r11] = r3     // Catch:{ IOException -> 0x0075 }
            java.lang.Thread r3 = new java.lang.Thread     // Catch:{ IOException -> 0x0075 }
            java.lang.String r4 = "key-value-app-data-runner"
            r3.<init>(r2, r4)     // Catch:{ IOException -> 0x0075 }
            r3.start()     // Catch:{ IOException -> 0x0075 }
            r4 = r12[r10]     // Catch:{ IOException -> 0x0075 }
            java.io.OutputStream r5 = r13.mOutput     // Catch:{ IOException -> 0x0075 }
            com.android.server.backup.utils.FullBackupUtils.routeSocketDataToOutput(r4, r5)     // Catch:{ IOException -> 0x0075 }
            com.android.server.backup.UserBackupManagerService r4 = r13.mBackupManagerService     // Catch:{ IOException -> 0x0075 }
            boolean r4 = r4.waitUntilOperationComplete(r1)     // Catch:{ IOException -> 0x0075 }
            if (r4 != 0) goto L_0x0062
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0075 }
            r4.<init>()     // Catch:{ IOException -> 0x0075 }
            java.lang.String r5 = "Full backup failed on package "
            r4.append(r5)     // Catch:{ IOException -> 0x0075 }
            android.content.pm.PackageInfo r5 = r13.mCurrentPackage     // Catch:{ IOException -> 0x0075 }
            java.lang.String r5 = r5.packageName     // Catch:{ IOException -> 0x0075 }
            r4.append(r5)     // Catch:{ IOException -> 0x0075 }
            java.lang.String r4 = r4.toString()     // Catch:{ IOException -> 0x0075 }
            android.util.Slog.e(r0, r4)     // Catch:{ IOException -> 0x0075 }
        L_0x0062:
            java.io.OutputStream r0 = r13.mOutput
            r0.flush()
            r0 = r12[r10]
            libcore.io.IoUtils.closeQuietly(r0)
            r0 = r12[r11]
        L_0x006f:
            libcore.io.IoUtils.closeQuietly(r0)
            goto L_0x00ad
        L_0x0073:
            r0 = move-exception
            goto L_0x00ae
        L_0x0075:
            r2 = move-exception
            goto L_0x007d
        L_0x0077:
            r0 = move-exception
            r12 = r2
            goto L_0x00ae
        L_0x007a:
            r3 = move-exception
            r12 = r2
            r2 = r3
        L_0x007d:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x0073 }
            r3.<init>()     // Catch:{ all -> 0x0073 }
            java.lang.String r4 = "Error backing up "
            r3.append(r4)     // Catch:{ all -> 0x0073 }
            android.content.pm.PackageInfo r4 = r13.mCurrentPackage     // Catch:{ all -> 0x0073 }
            java.lang.String r4 = r4.packageName     // Catch:{ all -> 0x0073 }
            r3.append(r4)     // Catch:{ all -> 0x0073 }
            java.lang.String r4 = ": "
            r3.append(r4)     // Catch:{ all -> 0x0073 }
            r3.append(r2)     // Catch:{ all -> 0x0073 }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x0073 }
            android.util.Slog.e(r0, r3)     // Catch:{ all -> 0x0073 }
            java.io.OutputStream r0 = r13.mOutput
            r0.flush()
            if (r12 == 0) goto L_0x00ad
            r0 = r12[r10]
            libcore.io.IoUtils.closeQuietly(r0)
            r0 = r12[r11]
            goto L_0x006f
        L_0x00ad:
            return
        L_0x00ae:
            java.io.OutputStream r2 = r13.mOutput
            r2.flush()
            if (r12 == 0) goto L_0x00bf
            r2 = r12[r10]
            libcore.io.IoUtils.closeQuietly(r2)
            r2 = r12[r11]
            libcore.io.IoUtils.closeQuietly(r2)
        L_0x00bf:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.KeyValueAdbBackupEngine.writeBackupData():void");
    }

    private void cleanup() {
        this.mBackupManagerService.tearDownAgentAndKill(this.mCurrentPackage.applicationInfo);
        this.mBlankStateName.delete();
        this.mNewStateName.delete();
        this.mBackupDataName.delete();
    }
}
