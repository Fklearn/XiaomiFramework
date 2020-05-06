package com.android.server.backup.restore;

import android.app.IBackupAgent;
import android.app.backup.BackupAgent;
import android.app.backup.IFullBackupRestoreObserver;
import android.content.pm.ApplicationInfo;
import android.content.pm.Signature;
import android.os.ParcelFileDescriptor;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.Preconditions;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.backup.BackupManagerService;
import com.android.server.backup.BackupManagerServiceInjector;
import com.android.server.backup.BackupPasswordManager;
import com.android.server.backup.UserBackupManagerService;
import com.android.server.backup.fullbackup.FullBackupObbConnection;
import com.android.server.backup.utils.PasswordUtils;
import com.android.server.pm.PackageManagerService;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.InflaterInputStream;

public class PerformAdbRestoreTask implements Runnable {
    private IBackupAgent mAgent;
    private String mAgentPackage;
    private final BackupAgentTimeoutParameters mAgentTimeoutParameters;
    private long mAppVersion;
    private final UserBackupManagerService mBackupManagerService;
    private long mBytes;
    private final HashSet<String> mClearedPackages = new HashSet<>();
    private final String mCurrentPassword;
    private final String mDecryptPassword;
    private final RestoreDeleteObserver mDeleteObserver = new RestoreDeleteObserver();
    private final ParcelFileDescriptor mInputFile;
    private final AtomicBoolean mLatchObject;
    private final HashMap<String, Signature[]> mManifestSignatures = new HashMap<>();
    private FullBackupObbConnection mObbConnection = null;
    private IFullBackupRestoreObserver mObserver;
    private final HashMap<String, String> mPackageInstallers = new HashMap<>();
    private final BackupAgent mPackageManagerBackupAgent;
    private final HashMap<String, RestorePolicy> mPackagePolicies = new HashMap<>();
    private ParcelFileDescriptor[] mPipes = null;
    private ApplicationInfo mTargetApp;
    private byte[] mWidgetData = null;

    public PerformAdbRestoreTask(UserBackupManagerService backupManagerService, ParcelFileDescriptor fd, String curPassword, String decryptPassword, IFullBackupRestoreObserver observer, AtomicBoolean latch) {
        this.mBackupManagerService = backupManagerService;
        this.mInputFile = fd;
        this.mCurrentPassword = curPassword;
        this.mDecryptPassword = decryptPassword;
        this.mObserver = observer;
        this.mLatchObject = latch;
        this.mAgent = null;
        this.mPackageManagerBackupAgent = backupManagerService.makeMetadataAgent();
        this.mAgentPackage = null;
        this.mTargetApp = null;
        this.mObbConnection = new FullBackupObbConnection(backupManagerService);
        this.mAgentTimeoutParameters = (BackupAgentTimeoutParameters) Preconditions.checkNotNull(backupManagerService.getAgentTimeoutParameters(), "Timeout parameters cannot be null");
        this.mClearedPackages.add(PackageManagerService.PLATFORM_PACKAGE_NAME);
        this.mClearedPackages.add(UserBackupManagerService.SETTINGS_PACKAGE);
    }

    /* JADX WARNING: Removed duplicated region for block: B:72:0x0153 A[SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0196 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
            r14 = this;
            java.lang.String r0 = "BackupManagerService"
            java.lang.String r1 = "--- Performing full-dataset restore ---"
            android.util.Slog.i(r0, r1)
            android.os.ParcelFileDescriptor r0 = r14.mInputFile
            int r1 = r0.getFd()
            com.android.server.backup.BackupManagerServiceInjector.readMiuiBackupHeader(r0, r1)
            com.android.server.backup.fullbackup.FullBackupObbConnection r0 = r14.mObbConnection
            r0.establish()
            android.app.backup.IFullBackupRestoreObserver r0 = r14.mObserver
            android.app.backup.IFullBackupRestoreObserver r0 = com.android.server.backup.utils.FullBackupRestoreObserverUtils.sendStartRestore(r0)
            r14.mObserver = r0
            java.lang.String r0 = android.os.Environment.getExternalStorageState()
            java.lang.String r1 = "mounted"
            boolean r0 = r0.equals(r1)
            if (r0 == 0) goto L_0x0033
            java.util.HashMap<java.lang.String, com.android.server.backup.restore.RestorePolicy> r0 = r14.mPackagePolicies
            com.android.server.backup.restore.RestorePolicy r1 = com.android.server.backup.restore.RestorePolicy.ACCEPT
            java.lang.String r2 = "com.android.sharedstoragebackup"
            r0.put(r2, r1)
        L_0x0033:
            r0 = 0
            r1 = 1
            com.android.server.backup.UserBackupManagerService r2 = r14.mBackupManagerService     // Catch:{ IOException -> 0x0135 }
            java.lang.String r3 = r14.mCurrentPassword     // Catch:{ IOException -> 0x0135 }
            boolean r2 = r2.backupPasswordMatches(r3)     // Catch:{ IOException -> 0x0135 }
            if (r2 != 0) goto L_0x0088
            java.lang.String r2 = "BackupManagerService"
            java.lang.String r3 = "Backup password mismatch; aborting"
            android.util.Slog.w(r2, r3)     // Catch:{ IOException -> 0x0135 }
            if (r0 == 0) goto L_0x004b
            r0.close()     // Catch:{ IOException -> 0x0051 }
        L_0x004b:
            android.os.ParcelFileDescriptor r2 = r14.mInputFile     // Catch:{ IOException -> 0x0051 }
            r2.close()     // Catch:{ IOException -> 0x0051 }
            goto L_0x0059
        L_0x0051:
            r2 = move-exception
            java.lang.String r3 = "BackupManagerService"
            java.lang.String r4 = "Close of restore data pipe threw"
            android.util.Slog.w(r3, r4, r2)
        L_0x0059:
            java.util.concurrent.atomic.AtomicBoolean r2 = r14.mLatchObject
            monitor-enter(r2)
            java.util.concurrent.atomic.AtomicBoolean r3 = r14.mLatchObject     // Catch:{ all -> 0x0085 }
            r3.set(r1)     // Catch:{ all -> 0x0085 }
            java.util.concurrent.atomic.AtomicBoolean r1 = r14.mLatchObject     // Catch:{ all -> 0x0085 }
            r1.notifyAll()     // Catch:{ all -> 0x0085 }
            monitor-exit(r2)     // Catch:{ all -> 0x0085 }
            com.android.server.backup.fullbackup.FullBackupObbConnection r1 = r14.mObbConnection
            r1.tearDown()
            android.app.backup.IFullBackupRestoreObserver r1 = r14.mObserver
            android.app.backup.IFullBackupRestoreObserver r1 = com.android.server.backup.utils.FullBackupRestoreObserverUtils.sendEndRestore(r1)
            r14.mObserver = r1
            java.lang.String r1 = "BackupManagerService"
            java.lang.String r2 = "Full restore pass complete."
            android.util.Slog.d(r1, r2)
            com.android.server.backup.UserBackupManagerService r1 = r14.mBackupManagerService
            android.os.PowerManager$WakeLock r1 = r1.getWakelock()
            r1.release()
            return
        L_0x0085:
            r1 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0085 }
            throw r1
        L_0x0088:
            r2 = 0
            r14.mBytes = r2     // Catch:{ IOException -> 0x0135 }
            java.io.FileInputStream r2 = new java.io.FileInputStream     // Catch:{ IOException -> 0x0135 }
            android.os.ParcelFileDescriptor r3 = r14.mInputFile     // Catch:{ IOException -> 0x0135 }
            java.io.FileDescriptor r3 = r3.getFileDescriptor()     // Catch:{ IOException -> 0x0135 }
            r2.<init>(r3)     // Catch:{ IOException -> 0x0135 }
            r0 = r2
            java.lang.String r2 = r14.mDecryptPassword     // Catch:{ IOException -> 0x0135 }
            java.io.InputStream r2 = parseBackupFileHeaderAndReturnTarStream(r0, r2)     // Catch:{ IOException -> 0x0135 }
            if (r2 != 0) goto L_0x00e1
            r0.close()     // Catch:{ IOException -> 0x00aa }
            android.os.ParcelFileDescriptor r3 = r14.mInputFile     // Catch:{ IOException -> 0x00aa }
            r3.close()     // Catch:{ IOException -> 0x00aa }
            goto L_0x00b2
        L_0x00aa:
            r3 = move-exception
            java.lang.String r4 = "BackupManagerService"
            java.lang.String r5 = "Close of restore data pipe threw"
            android.util.Slog.w(r4, r5, r3)
        L_0x00b2:
            java.util.concurrent.atomic.AtomicBoolean r3 = r14.mLatchObject
            monitor-enter(r3)
            java.util.concurrent.atomic.AtomicBoolean r4 = r14.mLatchObject     // Catch:{ all -> 0x00de }
            r4.set(r1)     // Catch:{ all -> 0x00de }
            java.util.concurrent.atomic.AtomicBoolean r1 = r14.mLatchObject     // Catch:{ all -> 0x00de }
            r1.notifyAll()     // Catch:{ all -> 0x00de }
            monitor-exit(r3)     // Catch:{ all -> 0x00de }
            com.android.server.backup.fullbackup.FullBackupObbConnection r1 = r14.mObbConnection
            r1.tearDown()
            android.app.backup.IFullBackupRestoreObserver r1 = r14.mObserver
            android.app.backup.IFullBackupRestoreObserver r1 = com.android.server.backup.utils.FullBackupRestoreObserverUtils.sendEndRestore(r1)
            r14.mObserver = r1
            java.lang.String r1 = "BackupManagerService"
            java.lang.String r3 = "Full restore pass complete."
            android.util.Slog.d(r1, r3)
            com.android.server.backup.UserBackupManagerService r1 = r14.mBackupManagerService
            android.os.PowerManager$WakeLock r1 = r1.getWakelock()
            r1.release()
            return
        L_0x00de:
            r1 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x00de }
            throw r1
        L_0x00e1:
            com.android.server.backup.restore.FullRestoreEngine r3 = new com.android.server.backup.restore.FullRestoreEngine     // Catch:{ IOException -> 0x0135 }
            com.android.server.backup.UserBackupManagerService r5 = r14.mBackupManagerService     // Catch:{ IOException -> 0x0135 }
            r6 = 0
            android.app.backup.IFullBackupRestoreObserver r7 = r14.mObserver     // Catch:{ IOException -> 0x0135 }
            r8 = 0
            r9 = 0
            r10 = 1
            r11 = 1
            r12 = 0
            r13 = 1
            r4 = r3
            r4.<init>(r5, r6, r7, r8, r9, r10, r11, r12, r13)     // Catch:{ IOException -> 0x0135 }
            com.android.server.backup.fullbackup.FullBackupObbConnection r4 = r14.mObbConnection     // Catch:{ IOException -> 0x0135 }
            r3.setObbConnection(r4)     // Catch:{ IOException -> 0x0135 }
            android.os.ParcelFileDescriptor r4 = r14.mInputFile     // Catch:{ IOException -> 0x0135 }
            int r4 = r4.getFd()     // Catch:{ IOException -> 0x0135 }
            com.android.server.backup.BackupManagerServiceInjector.setInputFileDescriptor(r3, r4)     // Catch:{ IOException -> 0x0135 }
            com.android.server.backup.restore.FullRestoreEngineThread r4 = new com.android.server.backup.restore.FullRestoreEngineThread     // Catch:{ IOException -> 0x0135 }
            r4.<init>((com.android.server.backup.restore.FullRestoreEngine) r3, (java.io.InputStream) r2)     // Catch:{ IOException -> 0x0135 }
            r4.run()     // Catch:{ IOException -> 0x0135 }
            java.lang.String r5 = "BackupManagerService"
            java.lang.String r6 = "Done consuming input tarfile."
            android.util.Slog.v(r5, r6)     // Catch:{ IOException -> 0x0135 }
            r0.close()     // Catch:{ IOException -> 0x0119 }
            android.os.ParcelFileDescriptor r2 = r14.mInputFile     // Catch:{ IOException -> 0x0119 }
            r2.close()     // Catch:{ IOException -> 0x0119 }
            goto L_0x0121
        L_0x0119:
            r2 = move-exception
            java.lang.String r3 = "BackupManagerService"
            java.lang.String r4 = "Close of restore data pipe threw"
            android.util.Slog.w(r3, r4, r2)
        L_0x0121:
            java.util.concurrent.atomic.AtomicBoolean r2 = r14.mLatchObject
            monitor-enter(r2)
            java.util.concurrent.atomic.AtomicBoolean r3 = r14.mLatchObject     // Catch:{ all -> 0x0130 }
            r3.set(r1)     // Catch:{ all -> 0x0130 }
            java.util.concurrent.atomic.AtomicBoolean r1 = r14.mLatchObject     // Catch:{ all -> 0x0130 }
            r1.notifyAll()     // Catch:{ all -> 0x0130 }
            monitor-exit(r2)     // Catch:{ all -> 0x0130 }
            goto L_0x015e
        L_0x0130:
            r1 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0130 }
            throw r1
        L_0x0133:
            r2 = move-exception
            goto L_0x0180
        L_0x0135:
            r2 = move-exception
            java.lang.String r3 = "BackupManagerService"
            java.lang.String r4 = "Unable to read restore input"
            android.util.Slog.e(r3, r4)     // Catch:{ all -> 0x0133 }
            if (r0 == 0) goto L_0x0142
            r0.close()     // Catch:{ IOException -> 0x0148 }
        L_0x0142:
            android.os.ParcelFileDescriptor r2 = r14.mInputFile     // Catch:{ IOException -> 0x0148 }
            r2.close()     // Catch:{ IOException -> 0x0148 }
            goto L_0x0150
        L_0x0148:
            r2 = move-exception
            java.lang.String r3 = "BackupManagerService"
            java.lang.String r4 = "Close of restore data pipe threw"
            android.util.Slog.w(r3, r4, r2)
        L_0x0150:
            java.util.concurrent.atomic.AtomicBoolean r2 = r14.mLatchObject
            monitor-enter(r2)
            java.util.concurrent.atomic.AtomicBoolean r3 = r14.mLatchObject     // Catch:{ all -> 0x017d }
            r3.set(r1)     // Catch:{ all -> 0x017d }
            java.util.concurrent.atomic.AtomicBoolean r1 = r14.mLatchObject     // Catch:{ all -> 0x017d }
            r1.notifyAll()     // Catch:{ all -> 0x017d }
            monitor-exit(r2)     // Catch:{ all -> 0x017d }
        L_0x015e:
            com.android.server.backup.fullbackup.FullBackupObbConnection r1 = r14.mObbConnection
            r1.tearDown()
            android.app.backup.IFullBackupRestoreObserver r1 = r14.mObserver
            android.app.backup.IFullBackupRestoreObserver r1 = com.android.server.backup.utils.FullBackupRestoreObserverUtils.sendEndRestore(r1)
            r14.mObserver = r1
            java.lang.String r1 = "BackupManagerService"
            java.lang.String r2 = "Full restore pass complete."
            android.util.Slog.d(r1, r2)
            com.android.server.backup.UserBackupManagerService r1 = r14.mBackupManagerService
            android.os.PowerManager$WakeLock r1 = r1.getWakelock()
            r1.release()
            return
        L_0x017d:
            r1 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x017d }
            throw r1
        L_0x0180:
            if (r0 == 0) goto L_0x0185
            r0.close()     // Catch:{ IOException -> 0x018b }
        L_0x0185:
            android.os.ParcelFileDescriptor r3 = r14.mInputFile     // Catch:{ IOException -> 0x018b }
            r3.close()     // Catch:{ IOException -> 0x018b }
            goto L_0x0193
        L_0x018b:
            r3 = move-exception
            java.lang.String r4 = "BackupManagerService"
            java.lang.String r5 = "Close of restore data pipe threw"
            android.util.Slog.w(r4, r5, r3)
        L_0x0193:
            java.util.concurrent.atomic.AtomicBoolean r3 = r14.mLatchObject
            monitor-enter(r3)
            java.util.concurrent.atomic.AtomicBoolean r4 = r14.mLatchObject     // Catch:{ all -> 0x01bf }
            r4.set(r1)     // Catch:{ all -> 0x01bf }
            java.util.concurrent.atomic.AtomicBoolean r1 = r14.mLatchObject     // Catch:{ all -> 0x01bf }
            r1.notifyAll()     // Catch:{ all -> 0x01bf }
            monitor-exit(r3)     // Catch:{ all -> 0x01bf }
            com.android.server.backup.fullbackup.FullBackupObbConnection r1 = r14.mObbConnection
            r1.tearDown()
            android.app.backup.IFullBackupRestoreObserver r1 = r14.mObserver
            android.app.backup.IFullBackupRestoreObserver r1 = com.android.server.backup.utils.FullBackupRestoreObserverUtils.sendEndRestore(r1)
            r14.mObserver = r1
            java.lang.String r1 = "BackupManagerService"
            java.lang.String r3 = "Full restore pass complete."
            android.util.Slog.d(r1, r3)
            com.android.server.backup.UserBackupManagerService r1 = r14.mBackupManagerService
            android.os.PowerManager$WakeLock r1 = r1.getWakelock()
            r1.release()
            throw r2
        L_0x01bf:
            r1 = move-exception
            monitor-exit(r3)     // Catch:{ all -> 0x01bf }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.restore.PerformAdbRestoreTask.run():void");
    }

    private static void readFullyOrThrow(InputStream in, byte[] buffer) throws IOException {
        int offset = 0;
        while (offset < buffer.length) {
            int bytesRead = in.read(buffer, offset, buffer.length - offset);
            if (bytesRead > 0) {
                offset += bytesRead;
            } else {
                throw new IOException("Couldn't fully read data");
            }
        }
    }

    @VisibleForTesting
    public static InputStream parseBackupFileHeaderAndReturnTarStream(InputStream rawInputStream, String decryptPassword) throws IOException {
        boolean compressed = false;
        InputStream preCompressStream = rawInputStream;
        boolean okay = false;
        byte[] streamHeader = new byte[UserBackupManagerService.BACKUP_FILE_HEADER_MAGIC.length()];
        readFullyOrThrow(rawInputStream, streamHeader);
        if (Arrays.equals(UserBackupManagerService.BACKUP_FILE_HEADER_MAGIC.getBytes("UTF-8"), streamHeader)) {
            String s = readHeaderLine(rawInputStream);
            int archiveVersion = Integer.parseInt(s);
            if (archiveVersion <= 5 || BackupManagerServiceInjector.isRunningFromMiui(rawInputStream)) {
                boolean z = false;
                boolean pbkdf2Fallback = archiveVersion == 1;
                if (Integer.parseInt(readHeaderLine(rawInputStream)) != 0) {
                    z = true;
                }
                compressed = z;
                String s2 = readHeaderLine(rawInputStream);
                if (s2.equals("none")) {
                    okay = true;
                } else if (decryptPassword == null || decryptPassword.length() <= 0) {
                    Slog.w(BackupManagerService.TAG, "Archive is encrypted but no password given");
                    BackupManagerServiceInjector.errorOccur(3, rawInputStream);
                } else {
                    preCompressStream = decodeAesHeaderAndInitialize(decryptPassword, s2, pbkdf2Fallback, rawInputStream);
                    if (preCompressStream != null) {
                        okay = true;
                    }
                    if (preCompressStream == null) {
                        BackupManagerServiceInjector.errorOccur(3, rawInputStream);
                    }
                }
            } else {
                Slog.w(BackupManagerService.TAG, "Wrong header version: " + s);
            }
        } else {
            Slog.w(BackupManagerService.TAG, "Didn't read the right header magic");
        }
        if (okay) {
            return compressed ? new InflaterInputStream(preCompressStream) : preCompressStream;
        }
        Slog.w(BackupManagerService.TAG, "Invalid restore data; aborting.");
        BackupManagerServiceInjector.errorOccur(1, rawInputStream);
        return null;
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

    /* JADX WARNING: Removed duplicated region for block: B:100:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:102:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x00f0  */
    /* JADX WARNING: Removed duplicated region for block: B:69:0x00ff  */
    /* JADX WARNING: Removed duplicated region for block: B:74:0x010e  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x011d  */
    /* JADX WARNING: Removed duplicated region for block: B:84:0x012c  */
    /* JADX WARNING: Removed duplicated region for block: B:89:0x0139  */
    /* JADX WARNING: Removed duplicated region for block: B:91:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:94:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:96:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:98:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static java.io.InputStream attemptMasterKeyDecryption(java.lang.String r20, java.lang.String r21, byte[] r22, byte[] r23, int r24, java.lang.String r25, java.lang.String r26, java.io.InputStream r27, boolean r28) {
        /*
            r1 = r21
            r2 = r24
            java.lang.String r0 = "AES"
            java.lang.String r3 = "Incorrect password"
            java.lang.String r4 = "BackupManagerService"
            r5 = 0
            java.lang.String r6 = "AES/CBC/PKCS5Padding"
            javax.crypto.Cipher r6 = javax.crypto.Cipher.getInstance(r6)     // Catch:{ InvalidAlgorithmParameterException -> 0x0130, BadPaddingException -> 0x0123, IllegalBlockSizeException -> 0x0114, NoSuchAlgorithmException -> 0x0105, NoSuchPaddingException -> 0x00f6, InvalidKeyException -> 0x00e7 }
            r7 = r20
            r8 = r22
            javax.crypto.SecretKey r9 = com.android.server.backup.utils.PasswordUtils.buildPasswordKey(r1, r7, r8, r2)     // Catch:{ InvalidAlgorithmParameterException -> 0x00e5, BadPaddingException -> 0x00e3, IllegalBlockSizeException -> 0x00e1, NoSuchAlgorithmException -> 0x00df, NoSuchPaddingException -> 0x00dd, InvalidKeyException -> 0x00db }
            byte[] r10 = com.android.server.backup.utils.PasswordUtils.hexToByteArray(r25)     // Catch:{ InvalidAlgorithmParameterException -> 0x00e5, BadPaddingException -> 0x00e3, IllegalBlockSizeException -> 0x00e1, NoSuchAlgorithmException -> 0x00df, NoSuchPaddingException -> 0x00dd, InvalidKeyException -> 0x00db }
            javax.crypto.spec.IvParameterSpec r11 = new javax.crypto.spec.IvParameterSpec     // Catch:{ InvalidAlgorithmParameterException -> 0x00e5, BadPaddingException -> 0x00e3, IllegalBlockSizeException -> 0x00e1, NoSuchAlgorithmException -> 0x00df, NoSuchPaddingException -> 0x00dd, InvalidKeyException -> 0x00db }
            r11.<init>(r10)     // Catch:{ InvalidAlgorithmParameterException -> 0x00e5, BadPaddingException -> 0x00e3, IllegalBlockSizeException -> 0x00e1, NoSuchAlgorithmException -> 0x00df, NoSuchPaddingException -> 0x00dd, InvalidKeyException -> 0x00db }
            javax.crypto.spec.SecretKeySpec r12 = new javax.crypto.spec.SecretKeySpec     // Catch:{ InvalidAlgorithmParameterException -> 0x00e5, BadPaddingException -> 0x00e3, IllegalBlockSizeException -> 0x00e1, NoSuchAlgorithmException -> 0x00df, NoSuchPaddingException -> 0x00dd, InvalidKeyException -> 0x00db }
            byte[] r13 = r9.getEncoded()     // Catch:{ InvalidAlgorithmParameterException -> 0x00e5, BadPaddingException -> 0x00e3, IllegalBlockSizeException -> 0x00e1, NoSuchAlgorithmException -> 0x00df, NoSuchPaddingException -> 0x00dd, InvalidKeyException -> 0x00db }
            r12.<init>(r13, r0)     // Catch:{ InvalidAlgorithmParameterException -> 0x00e5, BadPaddingException -> 0x00e3, IllegalBlockSizeException -> 0x00e1, NoSuchAlgorithmException -> 0x00df, NoSuchPaddingException -> 0x00dd, InvalidKeyException -> 0x00db }
            r13 = 2
            r6.init(r13, r12, r11)     // Catch:{ InvalidAlgorithmParameterException -> 0x00e5, BadPaddingException -> 0x00e3, IllegalBlockSizeException -> 0x00e1, NoSuchAlgorithmException -> 0x00df, NoSuchPaddingException -> 0x00dd, InvalidKeyException -> 0x00db }
            byte[] r12 = com.android.server.backup.utils.PasswordUtils.hexToByteArray(r26)     // Catch:{ InvalidAlgorithmParameterException -> 0x00e5, BadPaddingException -> 0x00e3, IllegalBlockSizeException -> 0x00e1, NoSuchAlgorithmException -> 0x00df, NoSuchPaddingException -> 0x00dd, InvalidKeyException -> 0x00db }
            byte[] r14 = r6.doFinal(r12)     // Catch:{ InvalidAlgorithmParameterException -> 0x00e5, BadPaddingException -> 0x00e3, IllegalBlockSizeException -> 0x00e1, NoSuchAlgorithmException -> 0x00df, NoSuchPaddingException -> 0x00dd, InvalidKeyException -> 0x00db }
            r15 = 0
            int r13 = r15 + 1
            byte r15 = r14[r15]     // Catch:{ InvalidAlgorithmParameterException -> 0x00e5, BadPaddingException -> 0x00e3, IllegalBlockSizeException -> 0x00e1, NoSuchAlgorithmException -> 0x00df, NoSuchPaddingException -> 0x00dd, InvalidKeyException -> 0x00db }
            r16 = r5
            int r5 = r13 + r15
            byte[] r5 = java.util.Arrays.copyOfRange(r14, r13, r5)     // Catch:{ InvalidAlgorithmParameterException -> 0x00d6, BadPaddingException -> 0x00d1, IllegalBlockSizeException -> 0x00cd, NoSuchAlgorithmException -> 0x00c9, NoSuchPaddingException -> 0x00c5, InvalidKeyException -> 0x00c1 }
            int r13 = r13 + r15
            int r10 = r13 + 1
            byte r13 = r14[r13]     // Catch:{ InvalidAlgorithmParameterException -> 0x00d6, BadPaddingException -> 0x00d1, IllegalBlockSizeException -> 0x00cd, NoSuchAlgorithmException -> 0x00c9, NoSuchPaddingException -> 0x00c5, InvalidKeyException -> 0x00c1 }
            int r15 = r10 + r13
            byte[] r15 = java.util.Arrays.copyOfRange(r14, r10, r15)     // Catch:{ InvalidAlgorithmParameterException -> 0x00d6, BadPaddingException -> 0x00d1, IllegalBlockSizeException -> 0x00cd, NoSuchAlgorithmException -> 0x00c9, NoSuchPaddingException -> 0x00c5, InvalidKeyException -> 0x00c1 }
            int r10 = r10 + r13
            int r7 = r10 + 1
            byte r10 = r14[r10]     // Catch:{ InvalidAlgorithmParameterException -> 0x00d6, BadPaddingException -> 0x00d1, IllegalBlockSizeException -> 0x00cd, NoSuchAlgorithmException -> 0x00c9, NoSuchPaddingException -> 0x00c5, InvalidKeyException -> 0x00c1 }
            int r13 = r7 + r10
            byte[] r13 = java.util.Arrays.copyOfRange(r14, r7, r13)     // Catch:{ InvalidAlgorithmParameterException -> 0x00d6, BadPaddingException -> 0x00d1, IllegalBlockSizeException -> 0x00cd, NoSuchAlgorithmException -> 0x00c9, NoSuchPaddingException -> 0x00c5, InvalidKeyException -> 0x00c1 }
            r17 = r7
            r7 = r23
            byte[] r18 = com.android.server.backup.utils.PasswordUtils.makeKeyChecksum(r1, r15, r7, r2)     // Catch:{ InvalidAlgorithmParameterException -> 0x00be, BadPaddingException -> 0x00bb, IllegalBlockSizeException -> 0x00b8, NoSuchAlgorithmException -> 0x00b5, NoSuchPaddingException -> 0x00b3, InvalidKeyException -> 0x00b1 }
            r19 = r18
            r1 = r19
            boolean r18 = java.util.Arrays.equals(r1, r13)     // Catch:{ InvalidAlgorithmParameterException -> 0x00be, BadPaddingException -> 0x00bb, IllegalBlockSizeException -> 0x00b8, NoSuchAlgorithmException -> 0x00b5, NoSuchPaddingException -> 0x00b3, InvalidKeyException -> 0x00b1 }
            if (r18 == 0) goto L_0x00a4
            r18 = r1
            javax.crypto.spec.IvParameterSpec r1 = new javax.crypto.spec.IvParameterSpec     // Catch:{ InvalidAlgorithmParameterException -> 0x009f, BadPaddingException -> 0x009a, IllegalBlockSizeException -> 0x0095, NoSuchAlgorithmException -> 0x0090, NoSuchPaddingException -> 0x008b, InvalidKeyException -> 0x0086 }
            r1.<init>(r5)     // Catch:{ InvalidAlgorithmParameterException -> 0x009f, BadPaddingException -> 0x009a, IllegalBlockSizeException -> 0x0095, NoSuchAlgorithmException -> 0x0090, NoSuchPaddingException -> 0x008b, InvalidKeyException -> 0x0086 }
            javax.crypto.spec.SecretKeySpec r11 = new javax.crypto.spec.SecretKeySpec     // Catch:{ InvalidAlgorithmParameterException -> 0x009f, BadPaddingException -> 0x009a, IllegalBlockSizeException -> 0x0095, NoSuchAlgorithmException -> 0x0090, NoSuchPaddingException -> 0x008b, InvalidKeyException -> 0x0086 }
            r11.<init>(r15, r0)     // Catch:{ InvalidAlgorithmParameterException -> 0x009f, BadPaddingException -> 0x009a, IllegalBlockSizeException -> 0x0095, NoSuchAlgorithmException -> 0x0090, NoSuchPaddingException -> 0x008b, InvalidKeyException -> 0x0086 }
            r0 = 2
            r6.init(r0, r11, r1)     // Catch:{ InvalidAlgorithmParameterException -> 0x009f, BadPaddingException -> 0x009a, IllegalBlockSizeException -> 0x0095, NoSuchAlgorithmException -> 0x0090, NoSuchPaddingException -> 0x008b, InvalidKeyException -> 0x0086 }
            javax.crypto.CipherInputStream r0 = new javax.crypto.CipherInputStream     // Catch:{ InvalidAlgorithmParameterException -> 0x009f, BadPaddingException -> 0x009a, IllegalBlockSizeException -> 0x0095, NoSuchAlgorithmException -> 0x0090, NoSuchPaddingException -> 0x008b, InvalidKeyException -> 0x0086 }
            r11 = r27
            r0.<init>(r11, r6)     // Catch:{ InvalidAlgorithmParameterException -> 0x00be, BadPaddingException -> 0x00bb, IllegalBlockSizeException -> 0x00b8, NoSuchAlgorithmException -> 0x00b5, NoSuchPaddingException -> 0x00b3, InvalidKeyException -> 0x00b1 }
            r5 = r0
            goto L_0x00ad
        L_0x0086:
            r0 = move-exception
            r11 = r27
            goto L_0x00ee
        L_0x008b:
            r0 = move-exception
            r11 = r27
            goto L_0x00fd
        L_0x0090:
            r0 = move-exception
            r11 = r27
            goto L_0x010c
        L_0x0095:
            r0 = move-exception
            r11 = r27
            goto L_0x011b
        L_0x009a:
            r0 = move-exception
            r11 = r27
            goto L_0x012a
        L_0x009f:
            r0 = move-exception
            r11 = r27
            goto L_0x0137
        L_0x00a4:
            r18 = r1
            if (r28 == 0) goto L_0x00ab
            android.util.Slog.w(r4, r3)     // Catch:{ InvalidAlgorithmParameterException -> 0x00be, BadPaddingException -> 0x00bb, IllegalBlockSizeException -> 0x00b8, NoSuchAlgorithmException -> 0x00b5, NoSuchPaddingException -> 0x00b3, InvalidKeyException -> 0x00b1 }
        L_0x00ab:
            r5 = r16
        L_0x00ad:
            r16 = r5
            goto L_0x013f
        L_0x00b1:
            r0 = move-exception
            goto L_0x00ee
        L_0x00b3:
            r0 = move-exception
            goto L_0x00fd
        L_0x00b5:
            r0 = move-exception
            goto L_0x010c
        L_0x00b8:
            r0 = move-exception
            goto L_0x011b
        L_0x00bb:
            r0 = move-exception
            goto L_0x012a
        L_0x00be:
            r0 = move-exception
            goto L_0x0137
        L_0x00c1:
            r0 = move-exception
            r7 = r23
            goto L_0x00ee
        L_0x00c5:
            r0 = move-exception
            r7 = r23
            goto L_0x00fd
        L_0x00c9:
            r0 = move-exception
            r7 = r23
            goto L_0x010c
        L_0x00cd:
            r0 = move-exception
            r7 = r23
            goto L_0x011b
        L_0x00d1:
            r0 = move-exception
            r7 = r23
            goto L_0x012a
        L_0x00d6:
            r0 = move-exception
            r7 = r23
            goto L_0x0137
        L_0x00db:
            r0 = move-exception
            goto L_0x00ea
        L_0x00dd:
            r0 = move-exception
            goto L_0x00f9
        L_0x00df:
            r0 = move-exception
            goto L_0x0108
        L_0x00e1:
            r0 = move-exception
            goto L_0x0117
        L_0x00e3:
            r0 = move-exception
            goto L_0x0126
        L_0x00e5:
            r0 = move-exception
            goto L_0x0133
        L_0x00e7:
            r0 = move-exception
            r8 = r22
        L_0x00ea:
            r7 = r23
            r16 = r5
        L_0x00ee:
            if (r28 == 0) goto L_0x013f
            java.lang.String r1 = "Illegal password; aborting"
            android.util.Slog.w(r4, r1)
            goto L_0x013f
        L_0x00f6:
            r0 = move-exception
            r8 = r22
        L_0x00f9:
            r7 = r23
            r16 = r5
        L_0x00fd:
            if (r28 == 0) goto L_0x013e
            java.lang.String r1 = "Needed padding mechanism unavailable!"
            android.util.Slog.e(r4, r1)
            goto L_0x013e
        L_0x0105:
            r0 = move-exception
            r8 = r22
        L_0x0108:
            r7 = r23
            r16 = r5
        L_0x010c:
            if (r28 == 0) goto L_0x013e
            java.lang.String r1 = "Needed decryption algorithm unavailable!"
            android.util.Slog.e(r4, r1)
            goto L_0x013e
        L_0x0114:
            r0 = move-exception
            r8 = r22
        L_0x0117:
            r7 = r23
            r16 = r5
        L_0x011b:
            if (r28 == 0) goto L_0x013e
            java.lang.String r1 = "Invalid block size in master key"
            android.util.Slog.w(r4, r1)
            goto L_0x013e
        L_0x0123:
            r0 = move-exception
            r8 = r22
        L_0x0126:
            r7 = r23
            r16 = r5
        L_0x012a:
            if (r28 == 0) goto L_0x013e
            android.util.Slog.w(r4, r3)
            goto L_0x013e
        L_0x0130:
            r0 = move-exception
            r8 = r22
        L_0x0133:
            r7 = r23
            r16 = r5
        L_0x0137:
            if (r28 == 0) goto L_0x013e
            java.lang.String r1 = "Needed parameter spec unavailable!"
            android.util.Slog.e(r4, r1, r0)
        L_0x013e:
        L_0x013f:
            return r16
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.restore.PerformAdbRestoreTask.attemptMasterKeyDecryption(java.lang.String, java.lang.String, byte[], byte[], int, java.lang.String, java.lang.String, java.io.InputStream, boolean):java.io.InputStream");
    }

    private static InputStream decodeAesHeaderAndInitialize(String decryptPassword, String encryptionName, boolean pbkdf2Fallback, InputStream rawInStream) {
        String str = encryptionName;
        InputStream result = null;
        try {
            if (str.equals(PasswordUtils.ENCRYPTION_ALGORITHM_NAME)) {
                byte[] userSalt = PasswordUtils.hexToByteArray(readHeaderLine(rawInStream));
                byte[] ckSalt = PasswordUtils.hexToByteArray(readHeaderLine(rawInStream));
                int rounds = Integer.parseInt(readHeaderLine(rawInStream));
                String userIvHex = readHeaderLine(rawInStream);
                String masterKeyBlobHex = readHeaderLine(rawInStream);
                result = attemptMasterKeyDecryption(decryptPassword, BackupPasswordManager.PBKDF_CURRENT, userSalt, ckSalt, rounds, userIvHex, masterKeyBlobHex, rawInStream, false);
                if (result == null && pbkdf2Fallback) {
                    result = attemptMasterKeyDecryption(decryptPassword, BackupPasswordManager.PBKDF_FALLBACK, userSalt, ckSalt, rounds, userIvHex, masterKeyBlobHex, rawInStream, true);
                }
            } else {
                Slog.w(BackupManagerService.TAG, "Unsupported encryption method: " + str);
            }
        } catch (NumberFormatException e) {
            Slog.w(BackupManagerService.TAG, "Can't parse restore data header");
        } catch (IOException e2) {
            Slog.w(BackupManagerService.TAG, "Can't read input header");
        }
        return result;
    }
}
