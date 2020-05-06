package com.android.server.backup.fullbackup;

import android.app.backup.IFullBackupRestoreObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.ParcelFileDescriptor;
import android.util.Slog;
import com.android.server.backup.BackupManagerService;
import com.android.server.backup.BackupManagerServiceInjector;
import com.android.server.backup.BackupPasswordManager;
import com.android.server.backup.BackupRestoreTask;
import com.android.server.backup.UserBackupManagerService;
import com.android.server.backup.utils.PasswordUtils;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class PerformAdbBackupTask extends FullBackupTask implements BackupRestoreTask {
    private final boolean mAllApps;
    private final boolean mCompress;
    private final int mCurrentOpToken;
    private final String mCurrentPassword;
    private PackageInfo mCurrentTarget;
    private final boolean mDoWidgets;
    private final String mEncryptPassword;
    private final boolean mIncludeApks;
    private final boolean mIncludeObbs;
    private final boolean mIncludeShared;
    private final boolean mIncludeSystem;
    private final boolean mKeyValue;
    private final AtomicBoolean mLatch;
    public int mOutputFD;
    private final ParcelFileDescriptor mOutputFile;
    private final ArrayList<String> mPackages;
    private final UserBackupManagerService mUserBackupManagerService;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PerformAdbBackupTask(UserBackupManagerService backupManagerService, ParcelFileDescriptor fd, IFullBackupRestoreObserver observer, boolean includeApks, boolean includeObbs, boolean includeShared, boolean doWidgets, String curPassword, String encryptPassword, boolean doAllApps, boolean doSystem, boolean doCompress, boolean doKeyValue, String[] packages, AtomicBoolean latch) {
        super(observer);
        ArrayList<String> arrayList;
        String str = curPassword;
        String str2 = encryptPassword;
        this.mUserBackupManagerService = backupManagerService;
        this.mCurrentOpToken = backupManagerService.generateRandomIntegerToken();
        this.mLatch = latch;
        this.mOutputFile = fd;
        this.mIncludeApks = includeApks;
        this.mIncludeObbs = includeObbs;
        this.mIncludeShared = includeShared;
        this.mDoWidgets = doWidgets;
        this.mAllApps = doAllApps;
        this.mIncludeSystem = doSystem;
        if (packages == null) {
            arrayList = new ArrayList<>();
        } else {
            arrayList = new ArrayList<>(Arrays.asList(packages));
        }
        this.mPackages = arrayList;
        this.mCurrentPassword = str;
        if (str2 == null || "".equals(str2)) {
            this.mEncryptPassword = str;
        } else {
            this.mEncryptPassword = str2;
        }
        Slog.w(BackupManagerService.TAG, "Encrypting backup with passphrase=" + this.mEncryptPassword);
        this.mCompress = doCompress;
        this.mKeyValue = doKeyValue;
        BackupManagerServiceInjector.setOutputFileDescriptor(this, this.mOutputFile);
    }

    private void addPackagesToSet(TreeMap<String, PackageInfo> set, List<String> pkgNames) {
        for (String pkgName : pkgNames) {
            if (!set.containsKey(pkgName)) {
                try {
                    set.put(pkgName, BackupManagerServiceInjector.getPackageInfo(this.mUserBackupManagerService.getContext(), pkgName, this.mOutputFD));
                } catch (PackageManager.NameNotFoundException e) {
                    Slog.w(BackupManagerService.TAG, "Unknown package " + pkgName + ", skipping");
                }
            }
        }
    }

    private OutputStream emitAesBackupHeader(StringBuilder headerbuf, OutputStream ofstream) throws Exception {
        StringBuilder sb = headerbuf;
        byte[] newUserSalt = this.mUserBackupManagerService.randomBytes(512);
        SecretKey userKey = PasswordUtils.buildPasswordKey(BackupPasswordManager.PBKDF_CURRENT, this.mEncryptPassword, newUserSalt, 10000);
        byte[] masterPw = new byte[32];
        this.mUserBackupManagerService.getRng().nextBytes(masterPw);
        byte[] checksumSalt = this.mUserBackupManagerService.randomBytes(512);
        Cipher c = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec masterKeySpec = new SecretKeySpec(masterPw, "AES");
        c.init(1, masterKeySpec);
        OutputStream finalOutput = new CipherOutputStream(ofstream, c);
        sb.append(PasswordUtils.ENCRYPTION_ALGORITHM_NAME);
        sb.append(10);
        sb.append(PasswordUtils.byteArrayToHex(newUserSalt));
        sb.append(10);
        sb.append(PasswordUtils.byteArrayToHex(checksumSalt));
        sb.append(10);
        sb.append(10000);
        sb.append(10);
        Cipher mkC = Cipher.getInstance("AES/CBC/PKCS5Padding");
        mkC.init(1, userKey);
        sb.append(PasswordUtils.byteArrayToHex(mkC.getIV()));
        sb.append(10);
        byte[] IV = c.getIV();
        byte[] mk = masterKeySpec.getEncoded();
        byte[] checksum = PasswordUtils.makeKeyChecksum(BackupPasswordManager.PBKDF_CURRENT, masterKeySpec.getEncoded(), checksumSalt, 10000);
        ByteArrayOutputStream blob = new ByteArrayOutputStream(IV.length + mk.length + checksum.length + 3);
        DataOutputStream mkOut = new DataOutputStream(blob);
        mkOut.writeByte(IV.length);
        mkOut.write(IV);
        mkOut.writeByte(mk.length);
        mkOut.write(mk);
        mkOut.writeByte(checksum.length);
        mkOut.write(checksum);
        mkOut.flush();
        ByteArrayOutputStream byteArrayOutputStream = blob;
        sb.append(PasswordUtils.byteArrayToHex(mkC.doFinal(blob.toByteArray())));
        sb.append(10);
        return finalOutput;
    }

    private void finalizeBackup(OutputStream out) {
        try {
            out.write(new byte[1024]);
        } catch (IOException e) {
            Slog.w(BackupManagerService.TAG, "Error attempting to finalize backup stream");
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 35 */
    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:657)
        	at java.util.ArrayList.get(ArrayList.java:433)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processExcHandler(RegionMaker.java:1043)
        	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:975)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:52)
        */
    /* JADX WARNING: Removed duplicated region for block: B:111:0x027f A[SYNTHETIC, Splitter:B:111:0x027f] */
    /* JADX WARNING: Removed duplicated region for block: B:323:0x06d4 A[SYNTHETIC, Splitter:B:323:0x06d4] */
    /* JADX WARNING: Removed duplicated region for block: B:65:0x01b3 A[SYNTHETIC, Splitter:B:65:0x01b3] */
    /* JADX WARNING: Removed duplicated region for block: B:91:0x0227  */
    public void run() {
        /*
            r35 = this;
            r12 = r35
            boolean r0 = r12.mKeyValue
            if (r0 == 0) goto L_0x0009
            java.lang.String r0 = ", including key-value backups"
            goto L_0x000b
        L_0x0009:
            java.lang.String r0 = ""
        L_0x000b:
            r13 = r0
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "--- Performing adb backup"
            r0.append(r1)
            r0.append(r13)
            java.lang.String r1 = " ---"
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "BackupManagerService"
            android.util.Slog.i(r1, r0)
            int r0 = r12.mOutputFD
            r1 = -2
            if (r0 != r1) goto L_0x0034
            java.lang.String r0 = "BackupManagerService"
            java.lang.String r1 = "MIUI FD has been closed."
            android.util.Slog.e(r0, r1)
            return
        L_0x0034:
            java.util.TreeMap r0 = new java.util.TreeMap
            r0.<init>()
            r14 = r0
            com.android.server.backup.fullbackup.FullBackupObbConnection r0 = new com.android.server.backup.fullbackup.FullBackupObbConnection
            com.android.server.backup.UserBackupManagerService r1 = r12.mUserBackupManagerService
            r0.<init>(r1)
            r15 = r0
            r15.establish()
            r35.sendStartBackup()
            com.android.server.backup.UserBackupManagerService r0 = r12.mUserBackupManagerService
            android.content.pm.PackageManager r11 = r0.getPackageManager()
            boolean r0 = r12.mAllApps
            r10 = 1
            if (r0 == 0) goto L_0x0079
            r0 = 134217728(0x8000000, float:3.85186E-34)
            java.util.List r0 = r11.getInstalledPackages(r0)
            r1 = 0
        L_0x005a:
            int r2 = r0.size()
            if (r1 >= r2) goto L_0x0079
            java.lang.Object r2 = r0.get(r1)
            android.content.pm.PackageInfo r2 = (android.content.pm.PackageInfo) r2
            boolean r3 = r12.mIncludeSystem
            if (r3 != 0) goto L_0x0071
            android.content.pm.ApplicationInfo r3 = r2.applicationInfo
            int r3 = r3.flags
            r3 = r3 & r10
            if (r3 != 0) goto L_0x0076
        L_0x0071:
            java.lang.String r3 = r2.packageName
            r14.put(r3, r2)
        L_0x0076:
            int r1 = r1 + 1
            goto L_0x005a
        L_0x0079:
            boolean r0 = r12.mDoWidgets
            r1 = 0
            if (r0 == 0) goto L_0x00bd
            java.util.List r0 = com.android.server.AppWidgetBackupBridge.getWidgetParticipants(r1)
            if (r0 == 0) goto L_0x00bd
            java.lang.String r2 = "BackupManagerService"
            java.lang.String r3 = "Adding widget participants to backup set:"
            android.util.Slog.i(r2, r3)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r3 = 128(0x80, float:1.794E-43)
            r2.<init>(r3)
            java.lang.String r3 = "   "
            r2.append(r3)
            java.util.Iterator r3 = r0.iterator()
        L_0x009c:
            boolean r4 = r3.hasNext()
            if (r4 == 0) goto L_0x00b1
            java.lang.Object r4 = r3.next()
            java.lang.String r4 = (java.lang.String) r4
            r5 = 32
            r2.append(r5)
            r2.append(r4)
            goto L_0x009c
        L_0x00b1:
            java.lang.String r3 = r2.toString()
            java.lang.String r4 = "BackupManagerService"
            android.util.Slog.i(r4, r3)
            r12.addPackagesToSet(r14, r0)
        L_0x00bd:
            java.util.ArrayList<java.lang.String> r0 = r12.mPackages
            if (r0 == 0) goto L_0x00c4
            r12.addPackagesToSet(r14, r0)
        L_0x00c4:
            java.util.ArrayList r0 = new java.util.ArrayList
            r0.<init>()
            r8 = r0
            java.util.Set r0 = r14.entrySet()
            java.util.Iterator r16 = r0.iterator()
        L_0x00d2:
            boolean r0 = r16.hasNext()
            r2 = 9
            if (r0 == 0) goto L_0x015d
            java.lang.Object r0 = r16.next()
            java.util.Map$Entry r0 = (java.util.Map.Entry) r0
            java.lang.Object r0 = r0.getValue()
            android.content.pm.PackageInfo r0 = (android.content.pm.PackageInfo) r0
            android.content.pm.ApplicationInfo r3 = r0.applicationInfo
            com.android.server.backup.UserBackupManagerService r4 = r12.mUserBackupManagerService
            int r4 = r4.getUserId()
            boolean r3 = com.android.server.backup.utils.AppBackupUtils.appIsEligibleForBackup(r3, r4)
            if (r3 == 0) goto L_0x00fc
            android.content.pm.ApplicationInfo r3 = r0.applicationInfo
            boolean r3 = com.android.server.backup.utils.AppBackupUtils.appIsStopped(r3)
            if (r3 == 0) goto L_0x012a
        L_0x00fc:
            int r3 = r12.mOutputFD
            boolean r3 = com.android.server.backup.BackupManagerServiceInjector.isForceAllowBackup(r0, r3)
            if (r3 != 0) goto L_0x012a
            int r3 = r12.mOutputFD
            com.android.server.backup.BackupManagerServiceInjector.errorOccur((int) r2, (int) r3)
            r16.remove()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Package "
            r2.append(r3)
            java.lang.String r3 = r0.packageName
            r2.append(r3)
            java.lang.String r3 = " is not eligible for backup, removing."
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "BackupManagerService"
            android.util.Slog.i(r3, r2)
            goto L_0x015b
        L_0x012a:
            boolean r2 = com.android.server.backup.utils.AppBackupUtils.appIsKeyValueOnly(r0)
            if (r2 == 0) goto L_0x015b
            int r2 = r12.mOutputFD
            boolean r2 = com.android.server.backup.BackupManagerServiceInjector.isForceAllowBackup(r0, r2)
            if (r2 != 0) goto L_0x015b
            r16.remove()
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Package "
            r2.append(r3)
            java.lang.String r3 = r0.packageName
            r2.append(r3)
            java.lang.String r3 = " is key-value."
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "BackupManagerService"
            android.util.Slog.i(r3, r2)
            r8.add(r0)
        L_0x015b:
            goto L_0x00d2
        L_0x015d:
            java.util.ArrayList r0 = new java.util.ArrayList
            java.util.Collection r3 = r14.values()
            r0.<init>(r3)
            r9 = r0
            java.io.FileOutputStream r0 = new java.io.FileOutputStream
            android.os.ParcelFileDescriptor r3 = r12.mOutputFile
            java.io.FileDescriptor r3 = r3.getFileDescriptor()
            r0.<init>(r3)
            r7 = r0
            r3 = 0
            r4 = 0
            java.lang.String r0 = r12.mEncryptPassword     // Catch:{ RemoteException -> 0x06c1, Exception -> 0x0675, all -> 0x0668 }
            if (r0 == 0) goto L_0x01a8
            java.lang.String r0 = r12.mEncryptPassword     // Catch:{ RemoteException -> 0x019c, Exception -> 0x0190, all -> 0x0183 }
            int r0 = r0.length()     // Catch:{ RemoteException -> 0x019c, Exception -> 0x0190, all -> 0x0183 }
            if (r0 <= 0) goto L_0x01a8
            r0 = r10
            goto L_0x01a9
        L_0x0183:
            r0 = move-exception
            r1 = r0
            r30 = r7
            r31 = r8
            r32 = r9
            r6 = r10
            r18 = r11
            goto L_0x0726
        L_0x0190:
            r0 = move-exception
            r30 = r7
            r31 = r8
            r32 = r9
            r6 = r10
            r18 = r11
            goto L_0x067f
        L_0x019c:
            r0 = move-exception
            r30 = r7
            r31 = r8
            r32 = r9
            r6 = r10
            r18 = r11
            goto L_0x06cb
        L_0x01a8:
            r0 = r1
        L_0x01a9:
            r17 = r0
            int r0 = r12.mOutputFD     // Catch:{ RemoteException -> 0x06c1, Exception -> 0x0675, all -> 0x0668 }
            boolean r0 = com.android.server.backup.BackupManagerServiceInjector.isRunningFromMiui((int) r0)     // Catch:{ RemoteException -> 0x06c1, Exception -> 0x0675, all -> 0x0668 }
            if (r0 != 0) goto L_0x0215
            com.android.server.backup.UserBackupManagerService r0 = r12.mUserBackupManagerService     // Catch:{ RemoteException -> 0x019c, Exception -> 0x0190, all -> 0x0183 }
            boolean r0 = r0.deviceIsEncrypted()     // Catch:{ RemoteException -> 0x019c, Exception -> 0x0190, all -> 0x0183 }
            if (r0 == 0) goto L_0x0215
            if (r17 != 0) goto L_0x0215
            java.lang.String r0 = "BackupManagerService"
            java.lang.String r1 = "Unencrypted backup of encrypted device; aborting"
            android.util.Slog.e(r0, r1)     // Catch:{ RemoteException -> 0x019c, Exception -> 0x0190, all -> 0x0183 }
            if (r3 == 0) goto L_0x01cc
            r3.flush()     // Catch:{ IOException -> 0x01d2 }
            r3.close()     // Catch:{ IOException -> 0x01d2 }
        L_0x01cc:
            android.os.ParcelFileDescriptor r0 = r12.mOutputFile     // Catch:{ IOException -> 0x01d2 }
            r0.close()     // Catch:{ IOException -> 0x01d2 }
            goto L_0x01ed
        L_0x01d2:
            r0 = move-exception
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "IO error closing adb backup file: "
            r1.append(r2)
            java.lang.String r2 = r0.getMessage()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "BackupManagerService"
            android.util.Slog.e(r2, r1)
        L_0x01ed:
            java.util.concurrent.atomic.AtomicBoolean r1 = r12.mLatch
            monitor-enter(r1)
            java.util.concurrent.atomic.AtomicBoolean r0 = r12.mLatch     // Catch:{ all -> 0x0212 }
            r0.set(r10)     // Catch:{ all -> 0x0212 }
            java.util.concurrent.atomic.AtomicBoolean r0 = r12.mLatch     // Catch:{ all -> 0x0212 }
            r0.notifyAll()     // Catch:{ all -> 0x0212 }
            monitor-exit(r1)     // Catch:{ all -> 0x0212 }
            r35.sendEndBackup()
            r15.tearDown()
            java.lang.String r0 = "BackupManagerService"
            java.lang.String r1 = "Full backup pass complete."
            android.util.Slog.d(r0, r1)
            com.android.server.backup.UserBackupManagerService r0 = r12.mUserBackupManagerService
            android.os.PowerManager$WakeLock r0 = r0.getWakelock()
            r0.release()
            return
        L_0x0212:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0212 }
            throw r0
        L_0x0215:
            r5 = r7
            android.os.ParcelFileDescriptor r0 = r12.mOutputFile     // Catch:{ RemoteException -> 0x06c1, Exception -> 0x0675, all -> 0x0668 }
            int r6 = r12.mOutputFD     // Catch:{ RemoteException -> 0x06c1, Exception -> 0x0675, all -> 0x0668 }
            com.android.server.backup.BackupManagerServiceInjector.writeMiuiBackupHeader(r0, r6)     // Catch:{ RemoteException -> 0x06c1, Exception -> 0x0675, all -> 0x0668 }
            com.android.server.backup.UserBackupManagerService r0 = r12.mUserBackupManagerService     // Catch:{ RemoteException -> 0x06c1, Exception -> 0x0675, all -> 0x0668 }
            java.lang.String r6 = r12.mCurrentPassword     // Catch:{ RemoteException -> 0x06c1, Exception -> 0x0675, all -> 0x0668 }
            boolean r0 = r0.backupPasswordMatches(r6)     // Catch:{ RemoteException -> 0x06c1, Exception -> 0x0675, all -> 0x0668 }
            if (r0 != 0) goto L_0x027f
            java.lang.String r0 = "BackupManagerService"
            java.lang.String r1 = "Backup password mismatch; aborting"
            android.util.Slog.w(r0, r1)     // Catch:{ RemoteException -> 0x019c, Exception -> 0x0190, all -> 0x0183 }
            if (r3 == 0) goto L_0x0236
            r3.flush()     // Catch:{ IOException -> 0x023c }
            r3.close()     // Catch:{ IOException -> 0x023c }
        L_0x0236:
            android.os.ParcelFileDescriptor r0 = r12.mOutputFile     // Catch:{ IOException -> 0x023c }
            r0.close()     // Catch:{ IOException -> 0x023c }
            goto L_0x0257
        L_0x023c:
            r0 = move-exception
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "IO error closing adb backup file: "
            r1.append(r2)
            java.lang.String r2 = r0.getMessage()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "BackupManagerService"
            android.util.Slog.e(r2, r1)
        L_0x0257:
            java.util.concurrent.atomic.AtomicBoolean r1 = r12.mLatch
            monitor-enter(r1)
            java.util.concurrent.atomic.AtomicBoolean r0 = r12.mLatch     // Catch:{ all -> 0x027c }
            r0.set(r10)     // Catch:{ all -> 0x027c }
            java.util.concurrent.atomic.AtomicBoolean r0 = r12.mLatch     // Catch:{ all -> 0x027c }
            r0.notifyAll()     // Catch:{ all -> 0x027c }
            monitor-exit(r1)     // Catch:{ all -> 0x027c }
            r35.sendEndBackup()
            r15.tearDown()
            java.lang.String r0 = "BackupManagerService"
            java.lang.String r1 = "Full backup pass complete."
            android.util.Slog.d(r0, r1)
            com.android.server.backup.UserBackupManagerService r0 = r12.mUserBackupManagerService
            android.os.PowerManager$WakeLock r0 = r0.getWakelock()
            r0.release()
            return
        L_0x027c:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x027c }
            throw r0
        L_0x027f:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x06c1, Exception -> 0x0675, all -> 0x0668 }
            r6 = 1024(0x400, float:1.435E-42)
            r0.<init>(r6)     // Catch:{ RemoteException -> 0x06c1, Exception -> 0x0675, all -> 0x0668 }
            r6 = r0
            java.lang.String r0 = "ANDROID BACKUP\n"
            r6.append(r0)     // Catch:{ RemoteException -> 0x06c1, Exception -> 0x0675, all -> 0x0668 }
            r0 = 5
            r6.append(r0)     // Catch:{ RemoteException -> 0x06c1, Exception -> 0x0675, all -> 0x0668 }
            boolean r0 = r12.mCompress     // Catch:{ RemoteException -> 0x06c1, Exception -> 0x0675, all -> 0x0668 }
            if (r0 == 0) goto L_0x0297
            java.lang.String r0 = "\n1\n"
            goto L_0x0299
        L_0x0297:
            java.lang.String r0 = "\n0\n"
        L_0x0299:
            r6.append(r0)     // Catch:{ RemoteException -> 0x06c1, Exception -> 0x0675, all -> 0x0668 }
            if (r17 == 0) goto L_0x02b2
            java.io.OutputStream r0 = r12.emitAesBackupHeader(r6, r5)     // Catch:{ Exception -> 0x02a4, RemoteException -> 0x019c, all -> 0x0183 }
            r5 = r0
            goto L_0x02b8
        L_0x02a4:
            r0 = move-exception
            r29 = r6
            r30 = r7
            r31 = r8
            r32 = r9
            r6 = r10
            r18 = r11
            goto L_0x060a
        L_0x02b2:
            java.lang.String r0 = "none\n"
            r6.append(r0)     // Catch:{ Exception -> 0x05fe, RemoteException -> 0x06c1, all -> 0x0668 }
        L_0x02b8:
            java.lang.String r0 = r6.toString()     // Catch:{ Exception -> 0x05fe, RemoteException -> 0x06c1, all -> 0x0668 }
            java.lang.String r1 = "UTF-8"
            byte[] r0 = r0.getBytes(r1)     // Catch:{ Exception -> 0x05fe, RemoteException -> 0x06c1, all -> 0x0668 }
            r7.write(r0)     // Catch:{ Exception -> 0x05fe, RemoteException -> 0x06c1, all -> 0x0668 }
            boolean r1 = r12.mCompress     // Catch:{ Exception -> 0x05fe, RemoteException -> 0x06c1, all -> 0x0668 }
            if (r1 == 0) goto L_0x02d7
            java.util.zip.Deflater r1 = new java.util.zip.Deflater     // Catch:{ Exception -> 0x02a4, RemoteException -> 0x019c, all -> 0x0183 }
            r1.<init>(r2)     // Catch:{ Exception -> 0x02a4, RemoteException -> 0x019c, all -> 0x0183 }
            java.util.zip.DeflaterOutputStream r2 = new java.util.zip.DeflaterOutputStream     // Catch:{ Exception -> 0x02a4, RemoteException -> 0x019c, all -> 0x0183 }
            r2.<init>(r5, r1, r10)     // Catch:{ Exception -> 0x02a4, RemoteException -> 0x019c, all -> 0x0183 }
            r5 = r2
            r19 = r5
            goto L_0x02d9
        L_0x02d7:
            r19 = r5
        L_0x02d9:
            r5 = r19
            boolean r0 = r12.mIncludeShared     // Catch:{ RemoteException -> 0x05f1, Exception -> 0x05e4, all -> 0x05d6 }
            if (r0 == 0) goto L_0x0322
            com.android.server.backup.UserBackupManagerService r0 = r12.mUserBackupManagerService     // Catch:{ NameNotFoundException -> 0x031a }
            android.content.pm.PackageManager r0 = r0.getPackageManager()     // Catch:{ NameNotFoundException -> 0x031a }
            java.lang.String r1 = "com.android.sharedstoragebackup"
            r2 = 0
            android.content.pm.PackageInfo r0 = r0.getPackageInfo(r1, r2)     // Catch:{ NameNotFoundException -> 0x031a }
            r4 = r0
            r9.add(r4)     // Catch:{ NameNotFoundException -> 0x031a }
            goto L_0x0322
        L_0x02f2:
            r0 = move-exception
            r1 = r0
            r3 = r5
            r30 = r7
            r31 = r8
            r32 = r9
            r6 = r10
            r18 = r11
            goto L_0x0726
        L_0x0300:
            r0 = move-exception
            r3 = r5
            r30 = r7
            r31 = r8
            r32 = r9
            r6 = r10
            r18 = r11
            goto L_0x067f
        L_0x030d:
            r0 = move-exception
            r3 = r5
            r30 = r7
            r31 = r8
            r32 = r9
            r6 = r10
            r18 = r11
            goto L_0x06cb
        L_0x031a:
            r0 = move-exception
            java.lang.String r1 = "BackupManagerService"
            java.lang.String r2 = "Unable to find shared-storage backup handler"
            android.util.Slog.e(r1, r2)     // Catch:{ RemoteException -> 0x030d, Exception -> 0x0300, all -> 0x02f2 }
        L_0x0322:
            int r0 = r9.size()     // Catch:{ RemoteException -> 0x05f1, Exception -> 0x05e4, all -> 0x05d6 }
            r1 = 0
            r34 = r4
            r4 = r1
            r1 = r34
        L_0x032c:
            if (r4 >= r0) goto L_0x04f7
            java.lang.Object r2 = r9.get(r4)     // Catch:{ RemoteException -> 0x04e9, Exception -> 0x04db, all -> 0x04cc }
            android.content.pm.PackageInfo r2 = (android.content.pm.PackageInfo) r2     // Catch:{ RemoteException -> 0x04e9, Exception -> 0x04db, all -> 0x04cc }
            r3 = r2
            java.lang.String r1 = "BackupManagerService"
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x04bd, Exception -> 0x04ae, all -> 0x049e }
            r2.<init>()     // Catch:{ RemoteException -> 0x04bd, Exception -> 0x04ae, all -> 0x049e }
            java.lang.String r10 = "--- Performing full backup for package "
            r2.append(r10)     // Catch:{ RemoteException -> 0x0490, Exception -> 0x0482, all -> 0x0473 }
            java.lang.String r10 = r3.packageName     // Catch:{ RemoteException -> 0x0490, Exception -> 0x0482, all -> 0x0473 }
            r2.append(r10)     // Catch:{ RemoteException -> 0x0490, Exception -> 0x0482, all -> 0x0473 }
            java.lang.String r10 = " ---"
            r2.append(r10)     // Catch:{ RemoteException -> 0x0490, Exception -> 0x0482, all -> 0x0473 }
            java.lang.String r2 = r2.toString()     // Catch:{ RemoteException -> 0x0490, Exception -> 0x0482, all -> 0x0473 }
            android.util.Slog.i(r1, r2)     // Catch:{ RemoteException -> 0x0490, Exception -> 0x0482, all -> 0x0473 }
            java.lang.String r1 = r3.packageName     // Catch:{ RemoteException -> 0x0490, Exception -> 0x0482, all -> 0x0473 }
            java.lang.String r2 = "com.android.sharedstoragebackup"
            boolean r1 = r1.equals(r2)     // Catch:{ RemoteException -> 0x0490, Exception -> 0x0482, all -> 0x0473 }
            r20 = r1
            com.android.server.backup.fullbackup.FullBackupEngine r21 = new com.android.server.backup.fullbackup.FullBackupEngine     // Catch:{ RemoteException -> 0x0490, Exception -> 0x0482, all -> 0x0473 }
            com.android.server.backup.UserBackupManagerService r2 = r12.mUserBackupManagerService     // Catch:{ RemoteException -> 0x0490, Exception -> 0x0482, all -> 0x0473 }
            r10 = 0
            boolean r1 = r12.mIncludeApks     // Catch:{ RemoteException -> 0x0490, Exception -> 0x0482, all -> 0x0473 }
            r22 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            r24 = r8
            int r8 = r12.mCurrentOpToken     // Catch:{ RemoteException -> 0x0464, Exception -> 0x0455, all -> 0x0445 }
            r25 = 0
            r26 = r1
            r1 = r21
            r27 = r3
            r3 = r5
            r28 = r4
            r4 = r10
            r10 = r5
            r5 = r27
            r29 = r6
            r6 = r26
            r30 = r7
            r7 = r35
            r32 = r9
            r31 = r24
            r24 = r8
            r8 = r22
            r33 = r10
            r10 = r24
            r18 = r11
            r11 = r25
            r1.<init>(r2, r3, r4, r5, r6, r7, r8, r10, r11)     // Catch:{ RemoteException -> 0x043c, Exception -> 0x0433, all -> 0x0429 }
            r1 = r21
            int r2 = r12.mOutputFD     // Catch:{ RemoteException -> 0x043c, Exception -> 0x0433, all -> 0x0429 }
            com.android.server.backup.BackupManagerServiceInjector.setOutputFileDescriptor((com.android.server.backup.fullbackup.FullBackupEngine) r1, (int) r2)     // Catch:{ RemoteException -> 0x043c, Exception -> 0x0433, all -> 0x0429 }
            if (r20 == 0) goto L_0x03be
            java.lang.String r2 = "Shared storage"
            r3 = r2
            r2 = r27
            goto L_0x03c2
        L_0x03a5:
            r0 = move-exception
            r1 = r0
            r4 = r27
            r3 = r33
            r6 = 1
            goto L_0x0726
        L_0x03ae:
            r0 = move-exception
            r4 = r27
            r3 = r33
            r6 = 1
            goto L_0x067f
        L_0x03b6:
            r0 = move-exception
            r4 = r27
            r3 = r33
            r6 = 1
            goto L_0x06cb
        L_0x03be:
            r2 = r27
            java.lang.String r3 = r2.packageName     // Catch:{ RemoteException -> 0x0422, Exception -> 0x041b, all -> 0x0413 }
        L_0x03c2:
            r12.sendOnBackupPackage(r3)     // Catch:{ RemoteException -> 0x0422, Exception -> 0x041b, all -> 0x0413 }
            r12.mCurrentTarget = r2     // Catch:{ RemoteException -> 0x0422, Exception -> 0x041b, all -> 0x0413 }
            r1.backupOnePackage()     // Catch:{ RemoteException -> 0x0422, Exception -> 0x041b, all -> 0x0413 }
            boolean r3 = r12.mIncludeObbs     // Catch:{ RemoteException -> 0x0422, Exception -> 0x041b, all -> 0x0413 }
            if (r3 == 0) goto L_0x0400
            if (r20 != 0) goto L_0x0400
            r3 = r33
            boolean r4 = r15.backupObbs(r2, r3)     // Catch:{ RemoteException -> 0x03fb, Exception -> 0x03f6, all -> 0x03f0 }
            if (r4 == 0) goto L_0x03d9
            goto L_0x0402
        L_0x03d9:
            java.lang.RuntimeException r5 = new java.lang.RuntimeException     // Catch:{ RemoteException -> 0x03fb, Exception -> 0x03f6, all -> 0x03f0 }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x03fb, Exception -> 0x03f6, all -> 0x03f0 }
            r6.<init>()     // Catch:{ RemoteException -> 0x03fb, Exception -> 0x03f6, all -> 0x03f0 }
            java.lang.String r7 = "Failure writing OBB stack for "
            r6.append(r7)     // Catch:{ RemoteException -> 0x03fb, Exception -> 0x03f6, all -> 0x03f0 }
            r6.append(r2)     // Catch:{ RemoteException -> 0x03fb, Exception -> 0x03f6, all -> 0x03f0 }
            java.lang.String r6 = r6.toString()     // Catch:{ RemoteException -> 0x03fb, Exception -> 0x03f6, all -> 0x03f0 }
            r5.<init>(r6)     // Catch:{ RemoteException -> 0x03fb, Exception -> 0x03f6, all -> 0x03f0 }
            throw r5     // Catch:{ RemoteException -> 0x03fb, Exception -> 0x03f6, all -> 0x03f0 }
        L_0x03f0:
            r0 = move-exception
            r1 = r0
            r4 = r2
            r6 = 1
            goto L_0x0726
        L_0x03f6:
            r0 = move-exception
            r4 = r2
            r6 = 1
            goto L_0x067f
        L_0x03fb:
            r0 = move-exception
            r4 = r2
            r6 = 1
            goto L_0x06cb
        L_0x0400:
            r3 = r33
        L_0x0402:
            int r4 = r28 + 1
            r1 = r2
            r5 = r3
            r11 = r18
            r6 = r29
            r7 = r30
            r8 = r31
            r9 = r32
            r10 = 1
            goto L_0x032c
        L_0x0413:
            r0 = move-exception
            r3 = r33
            r1 = r0
            r4 = r2
            r6 = 1
            goto L_0x0726
        L_0x041b:
            r0 = move-exception
            r3 = r33
            r4 = r2
            r6 = 1
            goto L_0x067f
        L_0x0422:
            r0 = move-exception
            r3 = r33
            r4 = r2
            r6 = 1
            goto L_0x06cb
        L_0x0429:
            r0 = move-exception
            r2 = r27
            r3 = r33
            r1 = r0
            r4 = r2
            r6 = 1
            goto L_0x0726
        L_0x0433:
            r0 = move-exception
            r2 = r27
            r3 = r33
            r4 = r2
            r6 = 1
            goto L_0x067f
        L_0x043c:
            r0 = move-exception
            r2 = r27
            r3 = r33
            r4 = r2
            r6 = 1
            goto L_0x06cb
        L_0x0445:
            r0 = move-exception
            r2 = r3
            r3 = r5
            r30 = r7
            r32 = r9
            r18 = r11
            r31 = r24
            r1 = r0
            r4 = r2
            r6 = 1
            goto L_0x0726
        L_0x0455:
            r0 = move-exception
            r2 = r3
            r3 = r5
            r30 = r7
            r32 = r9
            r18 = r11
            r31 = r24
            r4 = r2
            r6 = 1
            goto L_0x067f
        L_0x0464:
            r0 = move-exception
            r2 = r3
            r3 = r5
            r30 = r7
            r32 = r9
            r18 = r11
            r31 = r24
            r4 = r2
            r6 = 1
            goto L_0x06cb
        L_0x0473:
            r0 = move-exception
            r2 = r3
            r3 = r5
            r30 = r7
            r31 = r8
            r32 = r9
            r18 = r11
            r1 = r0
            r4 = r2
            r6 = 1
            goto L_0x04ac
        L_0x0482:
            r0 = move-exception
            r2 = r3
            r3 = r5
            r30 = r7
            r31 = r8
            r32 = r9
            r18 = r11
            r4 = r2
            r6 = 1
            goto L_0x04bb
        L_0x0490:
            r0 = move-exception
            r2 = r3
            r3 = r5
            r30 = r7
            r31 = r8
            r32 = r9
            r18 = r11
            r4 = r2
            r6 = 1
            goto L_0x04ca
        L_0x049e:
            r0 = move-exception
            r2 = r3
            r3 = r5
            r30 = r7
            r31 = r8
            r32 = r9
            r18 = r11
            r1 = r0
            r4 = r2
            r6 = r10
        L_0x04ac:
            goto L_0x0726
        L_0x04ae:
            r0 = move-exception
            r2 = r3
            r3 = r5
            r30 = r7
            r31 = r8
            r32 = r9
            r18 = r11
            r4 = r2
            r6 = r10
        L_0x04bb:
            goto L_0x067f
        L_0x04bd:
            r0 = move-exception
            r2 = r3
            r3 = r5
            r30 = r7
            r31 = r8
            r32 = r9
            r18 = r11
            r4 = r2
            r6 = r10
        L_0x04ca:
            goto L_0x06cb
        L_0x04cc:
            r0 = move-exception
            r3 = r5
            r30 = r7
            r31 = r8
            r32 = r9
            r18 = r11
            r4 = r1
            r6 = r10
            r1 = r0
            goto L_0x0726
        L_0x04db:
            r0 = move-exception
            r3 = r5
            r30 = r7
            r31 = r8
            r32 = r9
            r18 = r11
            r4 = r1
            r6 = r10
            goto L_0x067f
        L_0x04e9:
            r0 = move-exception
            r3 = r5
            r30 = r7
            r31 = r8
            r32 = r9
            r18 = r11
            r4 = r1
            r6 = r10
            goto L_0x06cb
        L_0x04f7:
            r28 = r4
            r3 = r5
            r29 = r6
            r30 = r7
            r31 = r8
            r32 = r9
            r18 = r11
            boolean r2 = r12.mKeyValue     // Catch:{ RemoteException -> 0x05d1, Exception -> 0x05cc, all -> 0x05c6 }
            if (r2 == 0) goto L_0x056f
            java.util.Iterator r2 = r31.iterator()     // Catch:{ RemoteException -> 0x056a, Exception -> 0x0565, all -> 0x055f }
        L_0x050c:
            boolean r4 = r2.hasNext()     // Catch:{ RemoteException -> 0x056a, Exception -> 0x0565, all -> 0x055f }
            if (r4 == 0) goto L_0x056f
            java.lang.Object r4 = r2.next()     // Catch:{ RemoteException -> 0x056a, Exception -> 0x0565, all -> 0x055f }
            android.content.pm.PackageInfo r4 = (android.content.pm.PackageInfo) r4     // Catch:{ RemoteException -> 0x056a, Exception -> 0x0565, all -> 0x055f }
            java.lang.String r5 = "BackupManagerService"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ RemoteException -> 0x056a, Exception -> 0x0565, all -> 0x055f }
            r6.<init>()     // Catch:{ RemoteException -> 0x056a, Exception -> 0x0565, all -> 0x055f }
            java.lang.String r7 = "--- Performing key-value backup for package "
            r6.append(r7)     // Catch:{ RemoteException -> 0x056a, Exception -> 0x0565, all -> 0x055f }
            java.lang.String r7 = r4.packageName     // Catch:{ RemoteException -> 0x056a, Exception -> 0x0565, all -> 0x055f }
            r6.append(r7)     // Catch:{ RemoteException -> 0x056a, Exception -> 0x0565, all -> 0x055f }
            java.lang.String r7 = " ---"
            r6.append(r7)     // Catch:{ RemoteException -> 0x056a, Exception -> 0x0565, all -> 0x055f }
            java.lang.String r6 = r6.toString()     // Catch:{ RemoteException -> 0x056a, Exception -> 0x0565, all -> 0x055f }
            android.util.Slog.i(r5, r6)     // Catch:{ RemoteException -> 0x056a, Exception -> 0x0565, all -> 0x055f }
            com.android.server.backup.KeyValueAdbBackupEngine r5 = new com.android.server.backup.KeyValueAdbBackupEngine     // Catch:{ RemoteException -> 0x056a, Exception -> 0x0565, all -> 0x055f }
            com.android.server.backup.UserBackupManagerService r6 = r12.mUserBackupManagerService     // Catch:{ RemoteException -> 0x056a, Exception -> 0x0565, all -> 0x055f }
            com.android.server.backup.UserBackupManagerService r7 = r12.mUserBackupManagerService     // Catch:{ RemoteException -> 0x056a, Exception -> 0x0565, all -> 0x055f }
            android.content.pm.PackageManager r24 = r7.getPackageManager()     // Catch:{ RemoteException -> 0x056a, Exception -> 0x0565, all -> 0x055f }
            com.android.server.backup.UserBackupManagerService r7 = r12.mUserBackupManagerService     // Catch:{ RemoteException -> 0x056a, Exception -> 0x0565, all -> 0x055f }
            java.io.File r25 = r7.getBaseStateDir()     // Catch:{ RemoteException -> 0x056a, Exception -> 0x0565, all -> 0x055f }
            com.android.server.backup.UserBackupManagerService r7 = r12.mUserBackupManagerService     // Catch:{ RemoteException -> 0x056a, Exception -> 0x0565, all -> 0x055f }
            java.io.File r26 = r7.getDataDir()     // Catch:{ RemoteException -> 0x056a, Exception -> 0x0565, all -> 0x055f }
            r20 = r5
            r21 = r3
            r22 = r4
            r23 = r6
            r20.<init>(r21, r22, r23, r24, r25, r26)     // Catch:{ RemoteException -> 0x056a, Exception -> 0x0565, all -> 0x055f }
            java.lang.String r6 = r4.packageName     // Catch:{ RemoteException -> 0x056a, Exception -> 0x0565, all -> 0x055f }
            r12.sendOnBackupPackage(r6)     // Catch:{ RemoteException -> 0x056a, Exception -> 0x0565, all -> 0x055f }
            r5.backupOnePackage()     // Catch:{ RemoteException -> 0x056a, Exception -> 0x0565, all -> 0x055f }
            goto L_0x050c
        L_0x055f:
            r0 = move-exception
            r4 = r1
            r6 = 1
            r1 = r0
            goto L_0x0726
        L_0x0565:
            r0 = move-exception
            r4 = r1
            r6 = 1
            goto L_0x067f
        L_0x056a:
            r0 = move-exception
            r4 = r1
            r6 = 1
            goto L_0x06cb
        L_0x056f:
            r12.finalizeBackup(r3)     // Catch:{ RemoteException -> 0x05d1, Exception -> 0x05cc, all -> 0x05c6 }
            if (r3 == 0) goto L_0x057a
            r3.flush()     // Catch:{ IOException -> 0x0580 }
            r3.close()     // Catch:{ IOException -> 0x0580 }
        L_0x057a:
            android.os.ParcelFileDescriptor r0 = r12.mOutputFile     // Catch:{ IOException -> 0x0580 }
            r0.close()     // Catch:{ IOException -> 0x0580 }
            goto L_0x059b
        L_0x0580:
            r0 = move-exception
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r4 = "IO error closing adb backup file: "
            r2.append(r4)
            java.lang.String r4 = r0.getMessage()
            r2.append(r4)
            java.lang.String r2 = r2.toString()
            java.lang.String r4 = "BackupManagerService"
            android.util.Slog.e(r4, r2)
        L_0x059b:
            java.util.concurrent.atomic.AtomicBoolean r2 = r12.mLatch
            monitor-enter(r2)
            java.util.concurrent.atomic.AtomicBoolean r0 = r12.mLatch     // Catch:{ all -> 0x05c3 }
            r6 = 1
            r0.set(r6)     // Catch:{ all -> 0x05c3 }
            java.util.concurrent.atomic.AtomicBoolean r0 = r12.mLatch     // Catch:{ all -> 0x05c3 }
            r0.notifyAll()     // Catch:{ all -> 0x05c3 }
            monitor-exit(r2)     // Catch:{ all -> 0x05c3 }
            r35.sendEndBackup()
            r15.tearDown()
            java.lang.String r0 = "BackupManagerService"
            java.lang.String r2 = "Full backup pass complete."
            android.util.Slog.d(r0, r2)
            com.android.server.backup.UserBackupManagerService r0 = r12.mUserBackupManagerService
            android.os.PowerManager$WakeLock r0 = r0.getWakelock()
            r0.release()
            r4 = r1
            goto L_0x0720
        L_0x05c3:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x05c3 }
            throw r0
        L_0x05c6:
            r0 = move-exception
            r6 = 1
            r4 = r1
            r1 = r0
            goto L_0x0726
        L_0x05cc:
            r0 = move-exception
            r6 = 1
            r4 = r1
            goto L_0x067f
        L_0x05d1:
            r0 = move-exception
            r6 = 1
            r4 = r1
            goto L_0x06cb
        L_0x05d6:
            r0 = move-exception
            r3 = r5
            r30 = r7
            r31 = r8
            r32 = r9
            r6 = r10
            r18 = r11
            r1 = r0
            goto L_0x0726
        L_0x05e4:
            r0 = move-exception
            r3 = r5
            r30 = r7
            r31 = r8
            r32 = r9
            r6 = r10
            r18 = r11
            goto L_0x067f
        L_0x05f1:
            r0 = move-exception
            r3 = r5
            r30 = r7
            r31 = r8
            r32 = r9
            r6 = r10
            r18 = r11
            goto L_0x06cb
        L_0x05fe:
            r0 = move-exception
            r29 = r6
            r30 = r7
            r31 = r8
            r32 = r9
            r6 = r10
            r18 = r11
        L_0x060a:
            r1 = r0
            java.lang.String r0 = "BackupManagerService"
            java.lang.String r2 = "Unable to emit archive header"
            android.util.Slog.e(r0, r2, r1)     // Catch:{ RemoteException -> 0x0665, Exception -> 0x0663 }
            if (r3 == 0) goto L_0x061a
            r3.flush()     // Catch:{ IOException -> 0x0620 }
            r3.close()     // Catch:{ IOException -> 0x0620 }
        L_0x061a:
            android.os.ParcelFileDescriptor r0 = r12.mOutputFile     // Catch:{ IOException -> 0x0620 }
            r0.close()     // Catch:{ IOException -> 0x0620 }
            goto L_0x063b
        L_0x0620:
            r0 = move-exception
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r7 = "IO error closing adb backup file: "
            r2.append(r7)
            java.lang.String r7 = r0.getMessage()
            r2.append(r7)
            java.lang.String r2 = r2.toString()
            java.lang.String r7 = "BackupManagerService"
            android.util.Slog.e(r7, r2)
        L_0x063b:
            java.util.concurrent.atomic.AtomicBoolean r2 = r12.mLatch
            monitor-enter(r2)
            java.util.concurrent.atomic.AtomicBoolean r0 = r12.mLatch     // Catch:{ all -> 0x0660 }
            r0.set(r6)     // Catch:{ all -> 0x0660 }
            java.util.concurrent.atomic.AtomicBoolean r0 = r12.mLatch     // Catch:{ all -> 0x0660 }
            r0.notifyAll()     // Catch:{ all -> 0x0660 }
            monitor-exit(r2)     // Catch:{ all -> 0x0660 }
            r35.sendEndBackup()
            r15.tearDown()
            java.lang.String r0 = "BackupManagerService"
            java.lang.String r2 = "Full backup pass complete."
            android.util.Slog.d(r0, r2)
            com.android.server.backup.UserBackupManagerService r0 = r12.mUserBackupManagerService
            android.os.PowerManager$WakeLock r0 = r0.getWakelock()
            r0.release()
            return
        L_0x0660:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0660 }
            throw r0
        L_0x0663:
            r0 = move-exception
            goto L_0x067f
        L_0x0665:
            r0 = move-exception
            goto L_0x06cb
        L_0x0668:
            r0 = move-exception
            r30 = r7
            r31 = r8
            r32 = r9
            r6 = r10
            r18 = r11
            r1 = r0
            goto L_0x0726
        L_0x0675:
            r0 = move-exception
            r30 = r7
            r31 = r8
            r32 = r9
            r6 = r10
            r18 = r11
        L_0x067f:
            java.lang.String r1 = "BackupManagerService"
            java.lang.String r2 = "Internal exception during full backup"
            android.util.Slog.e(r1, r2, r0)     // Catch:{ all -> 0x0724 }
            if (r3 == 0) goto L_0x068e
            r3.flush()     // Catch:{ IOException -> 0x0694 }
            r3.close()     // Catch:{ IOException -> 0x0694 }
        L_0x068e:
            android.os.ParcelFileDescriptor r0 = r12.mOutputFile     // Catch:{ IOException -> 0x0694 }
            r0.close()     // Catch:{ IOException -> 0x0694 }
            goto L_0x06af
        L_0x0694:
            r0 = move-exception
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "IO error closing adb backup file: "
            r1.append(r2)
            java.lang.String r2 = r0.getMessage()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "BackupManagerService"
            android.util.Slog.e(r2, r1)
        L_0x06af:
            java.util.concurrent.atomic.AtomicBoolean r1 = r12.mLatch
            monitor-enter(r1)
            java.util.concurrent.atomic.AtomicBoolean r0 = r12.mLatch     // Catch:{ all -> 0x06be }
            r0.set(r6)     // Catch:{ all -> 0x06be }
            java.util.concurrent.atomic.AtomicBoolean r0 = r12.mLatch     // Catch:{ all -> 0x06be }
            r0.notifyAll()     // Catch:{ all -> 0x06be }
            monitor-exit(r1)     // Catch:{ all -> 0x06be }
            goto L_0x0709
        L_0x06be:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x06be }
            throw r0
        L_0x06c1:
            r0 = move-exception
            r30 = r7
            r31 = r8
            r32 = r9
            r6 = r10
            r18 = r11
        L_0x06cb:
            java.lang.String r1 = "BackupManagerService"
            java.lang.String r2 = "App died during full backup"
            android.util.Slog.e(r1, r2)     // Catch:{ all -> 0x0724 }
            if (r3 == 0) goto L_0x06da
            r3.flush()     // Catch:{ IOException -> 0x06e0 }
            r3.close()     // Catch:{ IOException -> 0x06e0 }
        L_0x06da:
            android.os.ParcelFileDescriptor r0 = r12.mOutputFile     // Catch:{ IOException -> 0x06e0 }
            r0.close()     // Catch:{ IOException -> 0x06e0 }
            goto L_0x06fb
        L_0x06e0:
            r0 = move-exception
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "IO error closing adb backup file: "
            r1.append(r2)
            java.lang.String r2 = r0.getMessage()
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "BackupManagerService"
            android.util.Slog.e(r2, r1)
        L_0x06fb:
            java.util.concurrent.atomic.AtomicBoolean r1 = r12.mLatch
            monitor-enter(r1)
            java.util.concurrent.atomic.AtomicBoolean r0 = r12.mLatch     // Catch:{ all -> 0x0721 }
            r0.set(r6)     // Catch:{ all -> 0x0721 }
            java.util.concurrent.atomic.AtomicBoolean r0 = r12.mLatch     // Catch:{ all -> 0x0721 }
            r0.notifyAll()     // Catch:{ all -> 0x0721 }
            monitor-exit(r1)     // Catch:{ all -> 0x0721 }
        L_0x0709:
            r35.sendEndBackup()
            r15.tearDown()
            java.lang.String r0 = "BackupManagerService"
            java.lang.String r1 = "Full backup pass complete."
            android.util.Slog.d(r0, r1)
            com.android.server.backup.UserBackupManagerService r0 = r12.mUserBackupManagerService
            android.os.PowerManager$WakeLock r0 = r0.getWakelock()
            r0.release()
        L_0x0720:
            return
        L_0x0721:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0721 }
            throw r0
        L_0x0724:
            r0 = move-exception
            r1 = r0
        L_0x0726:
            if (r3 == 0) goto L_0x072e
            r3.flush()     // Catch:{ IOException -> 0x0734 }
            r3.close()     // Catch:{ IOException -> 0x0734 }
        L_0x072e:
            android.os.ParcelFileDescriptor r0 = r12.mOutputFile     // Catch:{ IOException -> 0x0734 }
            r0.close()     // Catch:{ IOException -> 0x0734 }
            goto L_0x074f
        L_0x0734:
            r0 = move-exception
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r5 = "IO error closing adb backup file: "
            r2.append(r5)
            java.lang.String r5 = r0.getMessage()
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            java.lang.String r5 = "BackupManagerService"
            android.util.Slog.e(r5, r2)
        L_0x074f:
            java.util.concurrent.atomic.AtomicBoolean r2 = r12.mLatch
            monitor-enter(r2)
            java.util.concurrent.atomic.AtomicBoolean r0 = r12.mLatch     // Catch:{ all -> 0x0774 }
            r0.set(r6)     // Catch:{ all -> 0x0774 }
            java.util.concurrent.atomic.AtomicBoolean r0 = r12.mLatch     // Catch:{ all -> 0x0774 }
            r0.notifyAll()     // Catch:{ all -> 0x0774 }
            monitor-exit(r2)     // Catch:{ all -> 0x0774 }
            r35.sendEndBackup()
            r15.tearDown()
            java.lang.String r0 = "BackupManagerService"
            java.lang.String r2 = "Full backup pass complete."
            android.util.Slog.d(r0, r2)
            com.android.server.backup.UserBackupManagerService r0 = r12.mUserBackupManagerService
            android.os.PowerManager$WakeLock r0 = r0.getWakelock()
            r0.release()
            throw r1
        L_0x0774:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x0774 }
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.fullbackup.PerformAdbBackupTask.run():void");
    }

    public void execute() {
    }

    public void operationComplete(long result) {
    }

    public void handleCancel(boolean cancelAll) {
        PackageInfo target = this.mCurrentTarget;
        Slog.w(BackupManagerService.TAG, "adb backup cancel of " + target);
        if (target != null) {
            this.mUserBackupManagerService.tearDownAgentAndKill(this.mCurrentTarget.applicationInfo);
        }
        this.mUserBackupManagerService.removeOperation(this.mCurrentOpToken);
    }
}
