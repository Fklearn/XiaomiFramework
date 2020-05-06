package com.android.server.backup.restore;

import android.app.IBackupAgent;
import android.app.backup.IBackupManagerMonitor;
import android.app.backup.IFullBackupRestoreObserver;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.Preconditions;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.backup.BackupManagerService;
import com.android.server.backup.BackupManagerServiceInjector;
import com.android.server.backup.BackupRestoreTask;
import com.android.server.backup.FileMetadata;
import com.android.server.backup.UserBackupManagerService;
import com.android.server.backup.fullbackup.FullBackupObbConnection;
import com.android.server.wm.ActivityTaskManagerService;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class FullRestoreEngine extends RestoreEngine {
    private IBackupAgent mAgent;
    private String mAgentPackage;
    private final BackupAgentTimeoutParameters mAgentTimeoutParameters;
    final boolean mAllowApks;
    private final boolean mAllowObbs;
    private long mAppVersion;
    private final UserBackupManagerService mBackupManagerService;
    final byte[] mBuffer;
    private long mBytes;
    private final HashSet<String> mClearedPackages = new HashSet<>();
    private final RestoreDeleteObserver mDeleteObserver = new RestoreDeleteObserver();
    final int mEphemeralOpToken;
    public int mInputFD;
    final boolean mIsAdbRestore;
    private final HashMap<String, Signature[]> mManifestSignatures = new HashMap<>();
    final IBackupManagerMonitor mMonitor;
    private final BackupRestoreTask mMonitorTask;
    private FullBackupObbConnection mObbConnection = null;
    private IFullBackupRestoreObserver mObserver;
    final PackageInfo mOnlyPackage;
    private final HashMap<String, String> mPackageInstallers = new HashMap<>();
    private final HashMap<String, RestorePolicy> mPackagePolicies = new HashMap<>();
    private ParcelFileDescriptor[] mPipes = null;
    @GuardedBy({"mPipesLock"})
    private boolean mPipesClosed;
    private final Object mPipesLock = new Object();
    private ApplicationInfo mTargetApp;
    private final int mUserId;
    private byte[] mWidgetData = null;

    static /* synthetic */ long access$014(FullRestoreEngine x0, long x1) {
        long j = x0.mBytes + x1;
        x0.mBytes = j;
        return j;
    }

    public FullRestoreEngine(UserBackupManagerService backupManagerService, BackupRestoreTask monitorTask, IFullBackupRestoreObserver observer, IBackupManagerMonitor monitor, PackageInfo onlyPackage, boolean allowApks, boolean allowObbs, int ephemeralOpToken, boolean isAdbRestore) {
        this.mBackupManagerService = backupManagerService;
        this.mEphemeralOpToken = ephemeralOpToken;
        this.mMonitorTask = monitorTask;
        this.mObserver = observer;
        this.mMonitor = monitor;
        this.mOnlyPackage = onlyPackage;
        this.mAllowApks = allowApks;
        this.mAllowObbs = allowObbs;
        this.mBuffer = new byte[32768];
        this.mBytes = 0;
        this.mAgentTimeoutParameters = (BackupAgentTimeoutParameters) Preconditions.checkNotNull(backupManagerService.getAgentTimeoutParameters(), "Timeout parameters cannot be null");
        this.mIsAdbRestore = isAdbRestore;
        this.mUserId = backupManagerService.getUserId();
    }

    public IBackupAgent getAgent() {
        return this.mAgent;
    }

    public byte[] getWidgetData() {
        return this.mWidgetData;
    }

    /* JADX WARNING: type inference failed for: r30v9 */
    /* JADX WARNING: type inference failed for: r30v10 */
    /* JADX WARNING: type inference failed for: r30v12 */
    /* JADX WARNING: type inference failed for: r30v14 */
    /* JADX WARNING: type inference failed for: r30v15 */
    /* JADX WARNING: type inference failed for: r30v17 */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:128:0x0330 A[SYNTHETIC, Splitter:B:128:0x0330] */
    /* JADX WARNING: Removed duplicated region for block: B:158:0x0425  */
    /* JADX WARNING: Removed duplicated region for block: B:250:0x063c A[Catch:{ IOException -> 0x072d }] */
    /* JADX WARNING: Removed duplicated region for block: B:292:0x06db A[Catch:{ IOException -> 0x0795 }] */
    /* JADX WARNING: Removed duplicated region for block: B:294:0x06e6 A[Catch:{ IOException -> 0x0795 }] */
    /* JADX WARNING: Removed duplicated region for block: B:298:0x0729 A[Catch:{ IOException -> 0x0795 }] */
    /* JADX WARNING: Removed duplicated region for block: B:307:0x0743 A[Catch:{ IOException -> 0x0795 }] */
    /* JADX WARNING: Removed duplicated region for block: B:309:0x074b A[Catch:{ IOException -> 0x0795 }] */
    /* JADX WARNING: Removed duplicated region for block: B:328:0x0797  */
    /* JADX WARNING: Removed duplicated region for block: B:345:0x0807  */
    /* JADX WARNING: Removed duplicated region for block: B:347:0x080d  */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x0828  */
    /* JADX WARNING: Removed duplicated region for block: B:352:0x083e  */
    /* JADX WARNING: Removed duplicated region for block: B:354:0x0842  */
    /* JADX WARNING: Removed duplicated region for block: B:363:? A[RETURN, SYNTHETIC] */
    /* JADX WARNING: Unknown variable types count: 1 */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean restoreOneFile(java.io.InputStream r43, boolean r44, byte[] r45, android.content.pm.PackageInfo r46, boolean r47, int r48, android.app.backup.IBackupManagerMonitor r49) {
        /*
            r42 = this;
            r1 = r42
            r11 = r43
            r12 = r45
            r13 = r46
            boolean r2 = r42.isRunning()
            r14 = 0
            java.lang.String r15 = "BackupManagerService"
            if (r2 != 0) goto L_0x0017
            java.lang.String r2 = "Restore engine used after halting"
            android.util.Slog.w(r15, r2)
            return r14
        L_0x0017:
            com.android.server.backup.restore.FullRestoreEngine$1 r2 = new com.android.server.backup.restore.FullRestoreEngine$1
            r2.<init>()
            r10 = r2
            com.android.server.backup.utils.TarBackupReader r2 = new com.android.server.backup.utils.TarBackupReader
            r9 = r49
            r2.<init>(r11, r10, r9)
            r7 = r2
            r6 = -3
            java.lang.String r2 = "Reading tar header for restoring file"
            android.util.Slog.v(r15, r2)     // Catch:{ IOException -> 0x07ce }
            com.android.server.backup.FileMetadata r2 = r7.readTarHeaders()     // Catch:{ IOException -> 0x07ce }
            r4 = r2
            if (r4 == 0) goto L_0x07bb
            r4.dump()     // Catch:{ IOException -> 0x07ce }
            int r2 = r1.mInputFD     // Catch:{ IOException -> 0x07ce }
            r4.rawFd = r2     // Catch:{ IOException -> 0x07ce }
            java.lang.String r2 = r4.packageName     // Catch:{ IOException -> 0x07ce }
            r3 = r2
            java.lang.String r2 = r1.mAgentPackage     // Catch:{ IOException -> 0x07ce }
            boolean r2 = r3.equals(r2)     // Catch:{ IOException -> 0x07ce }
            r8 = 0
            if (r2 != 0) goto L_0x00aa
            if (r13 == 0) goto L_0x0072
            java.lang.String r2 = r13.packageName     // Catch:{ IOException -> 0x0099 }
            boolean r2 = r3.equals(r2)     // Catch:{ IOException -> 0x0099 }
            if (r2 != 0) goto L_0x0072
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0099 }
            r2.<init>()     // Catch:{ IOException -> 0x0099 }
            java.lang.String r8 = "Expected data for "
            r2.append(r8)     // Catch:{ IOException -> 0x0099 }
            r2.append(r13)     // Catch:{ IOException -> 0x0099 }
            java.lang.String r8 = " but saw "
            r2.append(r8)     // Catch:{ IOException -> 0x0099 }
            r2.append(r3)     // Catch:{ IOException -> 0x0099 }
            java.lang.String r2 = r2.toString()     // Catch:{ IOException -> 0x0099 }
            android.util.Slog.w(r15, r2)     // Catch:{ IOException -> 0x0099 }
            r1.setResult(r6)     // Catch:{ IOException -> 0x0099 }
            r1.setRunning(r14)     // Catch:{ IOException -> 0x0099 }
            return r14
        L_0x0072:
            java.util.HashMap<java.lang.String, com.android.server.backup.restore.RestorePolicy> r2 = r1.mPackagePolicies     // Catch:{ IOException -> 0x0099 }
            boolean r2 = r2.containsKey(r3)     // Catch:{ IOException -> 0x0099 }
            if (r2 != 0) goto L_0x0081
            java.util.HashMap<java.lang.String, com.android.server.backup.restore.RestorePolicy> r2 = r1.mPackagePolicies     // Catch:{ IOException -> 0x0099 }
            com.android.server.backup.restore.RestorePolicy r6 = com.android.server.backup.restore.RestorePolicy.IGNORE     // Catch:{ IOException -> 0x0099 }
            r2.put(r3, r6)     // Catch:{ IOException -> 0x0099 }
        L_0x0081:
            android.app.IBackupAgent r2 = r1.mAgent     // Catch:{ IOException -> 0x0099 }
            if (r2 == 0) goto L_0x00aa
            java.lang.String r2 = "Saw new package; finalizing old one"
            android.util.Slog.d(r15, r2)     // Catch:{ IOException -> 0x0099 }
            r42.tearDownPipes()     // Catch:{ IOException -> 0x0099 }
            android.content.pm.ApplicationInfo r2 = r1.mTargetApp     // Catch:{ IOException -> 0x0099 }
            boolean r6 = r1.mIsAdbRestore     // Catch:{ IOException -> 0x0099 }
            r1.tearDownAgent(r2, r6)     // Catch:{ IOException -> 0x0099 }
            r1.mTargetApp = r8     // Catch:{ IOException -> 0x0099 }
            r1.mAgentPackage = r8     // Catch:{ IOException -> 0x0099 }
            goto L_0x00aa
        L_0x0099:
            r0 = move-exception
            r2 = r0
            r32 = r10
            r8 = r12
            r14 = r15
            r30 = 1
            r12 = r48
            r41 = r11
            r11 = r7
            r7 = r41
            goto L_0x07df
        L_0x00aa:
            java.lang.String r2 = r4.path     // Catch:{ IOException -> 0x07ce }
            java.lang.String r6 = "_manifest"
            boolean r2 = r2.equals(r6)     // Catch:{ IOException -> 0x07ce }
            if (r2 == 0) goto L_0x01b1
            android.content.pm.Signature[] r2 = r7.readAppManifestAndReturnSignatures(r4)     // Catch:{ IOException -> 0x0099 }
            r29 = r15
            long r14 = r4.version     // Catch:{ IOException -> 0x019f }
            r1.mAppVersion = r14     // Catch:{ IOException -> 0x019f }
            java.lang.Class<android.content.pm.PackageManagerInternal> r8 = android.content.pm.PackageManagerInternal.class
            java.lang.Object r8 = com.android.server.LocalServices.getService(r8)     // Catch:{ IOException -> 0x019f }
            r21 = r8
            android.content.pm.PackageManagerInternal r21 = (android.content.pm.PackageManagerInternal) r21     // Catch:{ IOException -> 0x019f }
            com.android.server.backup.UserBackupManagerService r8 = r1.mBackupManagerService     // Catch:{ IOException -> 0x019f }
            android.content.pm.PackageManager r17 = r8.getPackageManager()     // Catch:{ IOException -> 0x019f }
            int r8 = r1.mUserId     // Catch:{ IOException -> 0x019f }
            r16 = r7
            r18 = r47
            r19 = r4
            r20 = r2
            r22 = r8
            com.android.server.backup.restore.RestorePolicy r8 = r16.chooseRestorePolicy(r17, r18, r19, r20, r21, r22)     // Catch:{ IOException -> 0x019f }
            java.util.HashMap<java.lang.String, android.content.pm.Signature[]> r14 = r1.mManifestSignatures     // Catch:{ IOException -> 0x019f }
            java.lang.String r15 = r4.packageName     // Catch:{ IOException -> 0x019f }
            r14.put(r15, r2)     // Catch:{ IOException -> 0x019f }
            java.util.HashMap<java.lang.String, com.android.server.backup.restore.RestorePolicy> r14 = r1.mPackagePolicies     // Catch:{ IOException -> 0x019f }
            r14.put(r3, r8)     // Catch:{ IOException -> 0x019f }
            java.util.HashMap<java.lang.String, java.lang.String> r14 = r1.mPackageInstallers     // Catch:{ IOException -> 0x019f }
            java.lang.String r15 = r4.installerPackageName     // Catch:{ IOException -> 0x019f }
            r14.put(r3, r15)     // Catch:{ IOException -> 0x019f }
            long r14 = r4.size     // Catch:{ IOException -> 0x019f }
            r7.skipTarPadding(r14)     // Catch:{ IOException -> 0x019f }
            android.app.backup.IFullBackupRestoreObserver r14 = r1.mObserver     // Catch:{ IOException -> 0x019f }
            android.app.backup.IFullBackupRestoreObserver r14 = com.android.server.backup.utils.FullBackupRestoreObserverUtils.sendOnRestorePackage(r14, r3)     // Catch:{ IOException -> 0x019f }
            r1.mObserver = r14     // Catch:{ IOException -> 0x019f }
            int r14 = r1.mInputFD     // Catch:{ IOException -> 0x019f }
            int r15 = r1.mUserId     // Catch:{ IOException -> 0x019f }
            int r14 = com.android.server.backup.BackupManagerServiceInjector.getAppUserId(r14, r15)     // Catch:{ IOException -> 0x019f }
            boolean r15 = com.android.server.backup.BackupManagerServiceInjector.isXSpaceUser(r14)     // Catch:{ IOException -> 0x019f }
            if (r15 == 0) goto L_0x018a
            com.android.server.backup.restore.RestorePolicy r15 = com.android.server.backup.restore.RestorePolicy.ACCEPT_IF_APK     // Catch:{ IOException -> 0x019f }
            if (r8 != r15) goto L_0x018a
            boolean r15 = com.android.server.backup.BackupManagerServiceInjector.isXSpaceUserRunning()     // Catch:{ IOException -> 0x019f }
            r16 = 0
            if (r15 == 0) goto L_0x0121
            boolean r17 = com.android.server.backup.BackupManagerServiceInjector.installExistingPackageAsUser(r3, r14)     // Catch:{ IOException -> 0x019f }
            r16 = r17
            r5 = r16
            goto L_0x0123
        L_0x0121:
            r5 = r16
        L_0x0123:
            java.util.HashMap<java.lang.String, com.android.server.backup.restore.RestorePolicy> r6 = r1.mPackagePolicies     // Catch:{ IOException -> 0x019f }
            if (r5 == 0) goto L_0x012e
            com.android.server.backup.restore.RestorePolicy r18 = com.android.server.backup.restore.RestorePolicy.ACCEPT     // Catch:{ IOException -> 0x019f }
            r19 = r2
            r2 = r18
            goto L_0x0134
        L_0x012e:
            com.android.server.backup.restore.RestorePolicy r18 = com.android.server.backup.restore.RestorePolicy.IGNORE     // Catch:{ IOException -> 0x019f }
            r19 = r2
            r2 = r18
        L_0x0134:
            r6.put(r3, r2)     // Catch:{ IOException -> 0x019f }
            if (r5 == 0) goto L_0x013f
            int r2 = r1.mInputFD     // Catch:{ IOException -> 0x019f }
            com.android.server.backup.BackupManagerServiceInjector.onApkInstalled(r2)     // Catch:{ IOException -> 0x019f }
            goto L_0x0146
        L_0x013f:
            int r2 = r1.mInputFD     // Catch:{ IOException -> 0x019f }
            r6 = 11
            com.android.server.backup.BackupManagerServiceInjector.errorOccur((int) r6, (int) r2)     // Catch:{ IOException -> 0x019f }
        L_0x0146:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x019f }
            r2.<init>()     // Catch:{ IOException -> 0x019f }
            java.lang.String r6 = "installing, pkg="
            r2.append(r6)     // Catch:{ IOException -> 0x019f }
            r2.append(r3)     // Catch:{ IOException -> 0x019f }
            java.lang.String r6 = " appUserId="
            r2.append(r6)     // Catch:{ IOException -> 0x019f }
            r2.append(r14)     // Catch:{ IOException -> 0x019f }
            java.lang.String r6 = " xSpaceUserRunning="
            r2.append(r6)     // Catch:{ IOException -> 0x019f }
            r2.append(r15)     // Catch:{ IOException -> 0x019f }
            java.lang.String r6 = " isSuccessfullyInstalled="
            r2.append(r6)     // Catch:{ IOException -> 0x019f }
            r2.append(r5)     // Catch:{ IOException -> 0x019f }
            java.lang.String r2 = r2.toString()     // Catch:{ IOException -> 0x019f }
            r6 = r29
            android.util.Slog.d(r6, r2)     // Catch:{ IOException -> 0x0179 }
            r4.dump()     // Catch:{ IOException -> 0x0179 }
            goto L_0x018e
        L_0x0179:
            r0 = move-exception
            r2 = r0
            r14 = r6
            r32 = r10
            r8 = r12
            r30 = 1
            r12 = r48
            r41 = r11
            r11 = r7
            r7 = r41
            goto L_0x07df
        L_0x018a:
            r19 = r2
            r6 = r29
        L_0x018e:
            r31 = r4
            r14 = r6
            r32 = r10
            r8 = r12
            r30 = 1
            r12 = r48
            r41 = r11
            r11 = r7
            r7 = r41
            goto L_0x07ca
        L_0x019f:
            r0 = move-exception
            r2 = r0
            r32 = r10
            r8 = r12
            r14 = r29
            r30 = 1
            r12 = r48
            r41 = r11
            r11 = r7
            r7 = r41
            goto L_0x07df
        L_0x01b1:
            r14 = r15
            r6 = 11
            java.lang.String r2 = r4.path     // Catch:{ IOException -> 0x07b6 }
            java.lang.String r5 = "_meta"
            boolean r2 = r2.equals(r5)     // Catch:{ IOException -> 0x07b6 }
            if (r2 == 0) goto L_0x0201
            r7.readMetadata(r4)     // Catch:{ IOException -> 0x01f1 }
            byte[] r2 = r7.getWidgetData()     // Catch:{ IOException -> 0x01f1 }
            r1.mWidgetData = r2     // Catch:{ IOException -> 0x01f1 }
            android.app.backup.IBackupManagerMonitor r2 = r7.getMonitor()     // Catch:{ IOException -> 0x01f1 }
            long r5 = r4.size     // Catch:{ IOException -> 0x01e0 }
            r7.skipTarPadding(r5)     // Catch:{ IOException -> 0x01e0 }
            r31 = r4
            r32 = r10
            r8 = r12
            r30 = 1
            r12 = r48
            r41 = r11
            r11 = r7
            r7 = r41
            goto L_0x07cc
        L_0x01e0:
            r0 = move-exception
            r9 = r2
            r32 = r10
            r8 = r12
            r30 = 1
            r12 = r48
            r2 = r0
            r41 = r11
            r11 = r7
            r7 = r41
            goto L_0x07df
        L_0x01f1:
            r0 = move-exception
            r2 = r0
            r32 = r10
            r8 = r12
            r30 = 1
            r12 = r48
            r41 = r11
            r11 = r7
            r7 = r41
            goto L_0x07df
        L_0x0201:
            r15 = 1
            java.util.HashMap<java.lang.String, com.android.server.backup.restore.RestorePolicy> r2 = r1.mPackagePolicies     // Catch:{ IOException -> 0x07b6 }
            java.lang.Object r2 = r2.get(r3)     // Catch:{ IOException -> 0x07b6 }
            com.android.server.backup.restore.RestorePolicy r2 = (com.android.server.backup.restore.RestorePolicy) r2     // Catch:{ IOException -> 0x07b6 }
            r29 = r2
            r16 = r7
            long r6 = r4.size     // Catch:{ IOException -> 0x07a7 }
            int r2 = r1.mInputFD     // Catch:{ IOException -> 0x07a7 }
            com.android.server.backup.BackupManagerServiceInjector.addRestoredSize(r6, r2)     // Catch:{ IOException -> 0x07a7 }
            int[] r2 = com.android.server.backup.restore.FullRestoreEngine.AnonymousClass2.$SwitchMap$com$android$server$backup$restore$RestorePolicy     // Catch:{ IOException -> 0x07a7 }
            int r5 = r29.ordinal()     // Catch:{ IOException -> 0x07a7 }
            r2 = r2[r5]     // Catch:{ IOException -> 0x07a7 }
            r5 = 3
            r6 = 2
            r7 = 1
            if (r2 == r7) goto L_0x02f9
            java.lang.String r7 = "a"
            if (r2 == r6) goto L_0x026f
            if (r2 == r5) goto L_0x023f
            java.lang.String r2 = "Invalid policy from manifest"
            android.util.Slog.e(r14, r2)     // Catch:{ IOException -> 0x0261 }
            r15 = 0
            java.util.HashMap<java.lang.String, com.android.server.backup.restore.RestorePolicy> r2 = r1.mPackagePolicies     // Catch:{ IOException -> 0x0261 }
            com.android.server.backup.restore.RestorePolicy r7 = com.android.server.backup.restore.RestorePolicy.IGNORE     // Catch:{ IOException -> 0x0261 }
            r2.put(r3, r7)     // Catch:{ IOException -> 0x0261 }
            r13 = r3
            r3 = r4
            r32 = r10
            r11 = r16
            r30 = 1
            goto L_0x0305
        L_0x023f:
            java.lang.String r2 = r4.domain     // Catch:{ IOException -> 0x0261 }
            boolean r2 = r2.equals(r7)     // Catch:{ IOException -> 0x0261 }
            if (r2 == 0) goto L_0x0257
            java.lang.String r2 = "apk present but ACCEPT"
            android.util.Slog.d(r14, r2)     // Catch:{ IOException -> 0x0261 }
            r15 = 0
            r13 = r3
            r3 = r4
            r32 = r10
            r11 = r16
            r30 = 1
            goto L_0x0305
        L_0x0257:
            r13 = r3
            r3 = r4
            r32 = r10
            r11 = r16
            r30 = 1
            goto L_0x0305
        L_0x0261:
            r0 = move-exception
            r2 = r0
            r32 = r10
            r7 = r11
            r8 = r12
            r11 = r16
            r30 = 1
            r12 = r48
            goto L_0x07df
        L_0x026f:
            java.lang.String r2 = r4.domain     // Catch:{ IOException -> 0x02e8 }
            boolean r2 = r2.equals(r7)     // Catch:{ IOException -> 0x02e8 }
            if (r2 == 0) goto L_0x02d5
            java.lang.String r2 = "APK file; installing"
            android.util.Slog.d(r14, r2)     // Catch:{ IOException -> 0x02e8 }
            java.util.HashMap<java.lang.String, java.lang.String> r2 = r1.mPackageInstallers     // Catch:{ IOException -> 0x02e8 }
            java.lang.Object r2 = r2.get(r3)     // Catch:{ IOException -> 0x02e8 }
            r8 = r2
            java.lang.String r8 = (java.lang.String) r8     // Catch:{ IOException -> 0x02e8 }
            com.android.server.backup.UserBackupManagerService r2 = r1.mBackupManagerService     // Catch:{ IOException -> 0x02e8 }
            android.content.Context r5 = r2.getContext()     // Catch:{ IOException -> 0x02e8 }
            com.android.server.backup.restore.RestoreDeleteObserver r6 = r1.mDeleteObserver     // Catch:{ IOException -> 0x02e8 }
            java.util.HashMap<java.lang.String, android.content.pm.Signature[]> r7 = r1.mManifestSignatures     // Catch:{ IOException -> 0x02e8 }
            java.util.HashMap<java.lang.String, com.android.server.backup.restore.RestorePolicy> r2 = r1.mPackagePolicies     // Catch:{ IOException -> 0x02e8 }
            r19 = r15
            int r15 = r1.mUserId     // Catch:{ IOException -> 0x02e8 }
            r20 = r2
            r2 = r43
            r13 = r3
            r3 = r5
            r5 = r4
            r4 = r6
            r6 = r5
            r30 = 1
            r5 = r7
            r7 = r6
            r11 = 11
            r6 = r20
            r31 = r7
            r11 = r16
            r9 = r10
            r32 = r10
            r10 = r15
            boolean r2 = com.android.server.backup.utils.RestoreUtils.installApk(r2, r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ IOException -> 0x0314 }
            java.util.HashMap<java.lang.String, com.android.server.backup.restore.RestorePolicy> r3 = r1.mPackagePolicies     // Catch:{ IOException -> 0x0314 }
            if (r2 == 0) goto L_0x02b9
            com.android.server.backup.restore.RestorePolicy r4 = com.android.server.backup.restore.RestorePolicy.ACCEPT     // Catch:{ IOException -> 0x0314 }
            goto L_0x02bb
        L_0x02b9:
            com.android.server.backup.restore.RestorePolicy r4 = com.android.server.backup.restore.RestorePolicy.IGNORE     // Catch:{ IOException -> 0x0314 }
        L_0x02bb:
            r3.put(r13, r4)     // Catch:{ IOException -> 0x0314 }
            r3 = r31
            long r4 = r3.size     // Catch:{ IOException -> 0x0314 }
            r11.skipTarPadding(r4)     // Catch:{ IOException -> 0x0314 }
            if (r2 == 0) goto L_0x02cd
            int r4 = r1.mInputFD     // Catch:{ IOException -> 0x0314 }
            com.android.server.backup.BackupManagerServiceInjector.onApkInstalled(r4)     // Catch:{ IOException -> 0x0314 }
            goto L_0x02d4
        L_0x02cd:
            int r4 = r1.mInputFD     // Catch:{ IOException -> 0x0314 }
            r5 = 11
            com.android.server.backup.BackupManagerServiceInjector.errorOccur((int) r5, (int) r4)     // Catch:{ IOException -> 0x0314 }
        L_0x02d4:
            return r30
        L_0x02d5:
            r13 = r3
            r3 = r4
            r32 = r10
            r19 = r15
            r11 = r16
            r30 = 1
            java.util.HashMap<java.lang.String, com.android.server.backup.restore.RestorePolicy> r2 = r1.mPackagePolicies     // Catch:{ IOException -> 0x0314 }
            com.android.server.backup.restore.RestorePolicy r4 = com.android.server.backup.restore.RestorePolicy.IGNORE     // Catch:{ IOException -> 0x0314 }
            r2.put(r13, r4)     // Catch:{ IOException -> 0x0314 }
            r15 = 0
            goto L_0x0305
        L_0x02e8:
            r0 = move-exception
            r32 = r10
            r30 = 1
            r7 = r43
            r9 = r49
            r2 = r0
            r8 = r12
            r11 = r16
            r12 = r48
            goto L_0x07b5
        L_0x02f9:
            r13 = r3
            r3 = r4
            r30 = r7
            r32 = r10
            r19 = r15
            r11 = r16
            r15 = 0
        L_0x0305:
            boolean r2 = isRestorableFile(r3)     // Catch:{ IOException -> 0x079d }
            if (r2 == 0) goto L_0x031f
            java.lang.String r2 = r3.path     // Catch:{ IOException -> 0x0314 }
            boolean r2 = isCanonicalFilePath(r2)     // Catch:{ IOException -> 0x0314 }
            if (r2 != 0) goto L_0x0320
            goto L_0x031f
        L_0x0314:
            r0 = move-exception
            r7 = r43
            r9 = r49
            r2 = r0
            r8 = r12
            r12 = r48
            goto L_0x07df
        L_0x031f:
            r15 = 0
        L_0x0320:
            if (r15 == 0) goto L_0x032b
            android.app.IBackupAgent r2 = r1.mAgent     // Catch:{ IOException -> 0x0314 }
            if (r2 == 0) goto L_0x032b
            java.lang.String r2 = "Reusing existing agent instance"
            android.util.Slog.i(r14, r2)     // Catch:{ IOException -> 0x0314 }
        L_0x032b:
            java.lang.String r2 = "k"
            if (r15 == 0) goto L_0x03fa
            android.app.IBackupAgent r4 = r1.mAgent     // Catch:{ IOException -> 0x0314 }
            if (r4 != 0) goto L_0x03fa
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0314 }
            r4.<init>()     // Catch:{ IOException -> 0x0314 }
            java.lang.String r7 = "Need to launch agent for "
            r4.append(r7)     // Catch:{ IOException -> 0x0314 }
            r4.append(r13)     // Catch:{ IOException -> 0x0314 }
            java.lang.String r4 = r4.toString()     // Catch:{ IOException -> 0x0314 }
            android.util.Slog.d(r14, r4)     // Catch:{ IOException -> 0x0314 }
            com.android.server.backup.UserBackupManagerService r4 = r1.mBackupManagerService     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            android.content.Context r4 = r4.getContext()     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            int r7 = r1.mInputFD     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            int r9 = r1.mUserId     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            android.content.pm.ApplicationInfo r4 = com.android.server.backup.BackupManagerServiceInjector.getApplicationInfo(r4, r13, r7, r9)     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            r1.mTargetApp = r4     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            java.util.HashSet<java.lang.String> r4 = r1.mClearedPackages     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            boolean r4 = r4.contains(r13)     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            if (r4 != 0) goto L_0x03a0
            android.content.pm.ApplicationInfo r4 = r1.mTargetApp     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            java.lang.String r4 = r4.packageName     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            boolean r4 = r1.shouldForceClearAppDataOnFullRestore(r4)     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            android.content.pm.ApplicationInfo r7 = r1.mTargetApp     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            java.lang.String r7 = r7.backupAgentName     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            if (r7 == 0) goto L_0x038f
            if (r4 == 0) goto L_0x0371
            goto L_0x038f
        L_0x0371:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            r7.<init>()     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            java.lang.String r9 = "backup agent ("
            r7.append(r9)     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            android.content.pm.ApplicationInfo r9 = r1.mTargetApp     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            java.lang.String r9 = r9.backupAgentName     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            r7.append(r9)     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            java.lang.String r9 = ") => no clear"
            r7.append(r9)     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            java.lang.String r7 = r7.toString()     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            android.util.Slog.d(r14, r7)     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            goto L_0x0399
        L_0x038f:
            java.lang.String r7 = "Clearing app data preparatory to full restore"
            android.util.Slog.d(r14, r7)     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            com.android.server.backup.UserBackupManagerService r7 = r1.mBackupManagerService     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            r7.clearApplicationDataBeforeRestore(r13)     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
        L_0x0399:
            java.util.HashSet<java.lang.String> r7 = r1.mClearedPackages     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            r7.add(r13)     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            goto L_0x03a5
        L_0x03a0:
            java.lang.String r4 = "We've initialized this app already; no clear required"
            android.util.Slog.d(r14, r4)     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
        L_0x03a5:
            com.android.server.backup.BackupManagerServiceInjector.waitingBeforeGetAgent()     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            r42.setUpPipes()     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            com.android.server.backup.UserBackupManagerService r4 = r1.mBackupManagerService     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            android.content.pm.ApplicationInfo r7 = r1.mTargetApp     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            java.lang.String r9 = r3.domain     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            boolean r9 = r2.equals(r9)     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            if (r9 == 0) goto L_0x03b9
            r5 = 0
            goto L_0x03ba
        L_0x03b9:
        L_0x03ba:
            android.app.IBackupAgent r4 = r4.bindToAgentSynchronous(r7, r5)     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            r1.mAgent = r4     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            android.app.IBackupAgent r4 = r1.mAgent     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            int r5 = r1.mInputFD     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            android.os.ParcelFileDescriptor[] r7 = r1.mPipes     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            r7 = r7[r30]     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            com.android.server.backup.BackupManagerServiceInjector.linkToDeath(r4, r5, r7)     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            r1.mAgentPackage = r13     // Catch:{ IOException -> 0x03d0, NameNotFoundException -> 0x03ce }
            goto L_0x03d1
        L_0x03ce:
            r0 = move-exception
            goto L_0x03d2
        L_0x03d0:
            r0 = move-exception
        L_0x03d1:
        L_0x03d2:
            android.app.IBackupAgent r4 = r1.mAgent     // Catch:{ IOException -> 0x0314 }
            if (r4 != 0) goto L_0x03fa
            int r4 = r1.mInputFD     // Catch:{ IOException -> 0x0314 }
            com.android.server.backup.BackupManagerServiceInjector.errorOccur((int) r6, (int) r4)     // Catch:{ IOException -> 0x0314 }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0314 }
            r4.<init>()     // Catch:{ IOException -> 0x0314 }
            java.lang.String r5 = "Unable to create agent for "
            r4.append(r5)     // Catch:{ IOException -> 0x0314 }
            r4.append(r13)     // Catch:{ IOException -> 0x0314 }
            java.lang.String r4 = r4.toString()     // Catch:{ IOException -> 0x0314 }
            android.util.Slog.e(r14, r4)     // Catch:{ IOException -> 0x0314 }
            r15 = 0
            r42.tearDownPipes()     // Catch:{ IOException -> 0x0314 }
            java.util.HashMap<java.lang.String, com.android.server.backup.restore.RestorePolicy> r4 = r1.mPackagePolicies     // Catch:{ IOException -> 0x0314 }
            com.android.server.backup.restore.RestorePolicy r5 = com.android.server.backup.restore.RestorePolicy.IGNORE     // Catch:{ IOException -> 0x0314 }
            r4.put(r13, r5)     // Catch:{ IOException -> 0x0314 }
        L_0x03fa:
            if (r15 == 0) goto L_0x0423
            java.lang.String r4 = r1.mAgentPackage     // Catch:{ IOException -> 0x0314 }
            boolean r4 = r13.equals(r4)     // Catch:{ IOException -> 0x0314 }
            if (r4 != 0) goto L_0x0423
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0314 }
            r4.<init>()     // Catch:{ IOException -> 0x0314 }
            java.lang.String r5 = "Restoring data for "
            r4.append(r5)     // Catch:{ IOException -> 0x0314 }
            r4.append(r13)     // Catch:{ IOException -> 0x0314 }
            java.lang.String r5 = " but agent is for "
            r4.append(r5)     // Catch:{ IOException -> 0x0314 }
            java.lang.String r5 = r1.mAgentPackage     // Catch:{ IOException -> 0x0314 }
            r4.append(r5)     // Catch:{ IOException -> 0x0314 }
            java.lang.String r4 = r4.toString()     // Catch:{ IOException -> 0x0314 }
            android.util.Slog.e(r14, r4)     // Catch:{ IOException -> 0x0314 }
            r15 = 0
        L_0x0423:
            if (r15 == 0) goto L_0x0743
            r6 = 1
            long r9 = r3.size     // Catch:{ IOException -> 0x073b }
            java.lang.String r7 = "com.android.sharedstoragebackup"
            boolean r7 = r13.equals(r7)     // Catch:{ IOException -> 0x073b }
            if (r7 == 0) goto L_0x0439
            com.android.server.backup.BackupAgentTimeoutParameters r8 = r1.mAgentTimeoutParameters     // Catch:{ IOException -> 0x0314 }
            long r16 = r8.getSharedBackupAgentTimeoutMillis()     // Catch:{ IOException -> 0x0314 }
            r35 = r16
            goto L_0x0441
        L_0x0439:
            com.android.server.backup.BackupAgentTimeoutParameters r8 = r1.mAgentTimeoutParameters     // Catch:{ IOException -> 0x073b }
            long r16 = r8.getRestoreAgentTimeoutMillis()     // Catch:{ IOException -> 0x073b }
            r35 = r16
        L_0x0441:
            com.android.server.backup.UserBackupManagerService r8 = r1.mBackupManagerService     // Catch:{ IOException -> 0x0626, RemoteException -> 0x0603 }
            com.android.server.backup.BackupRestoreTask r4 = r1.mMonitorTask     // Catch:{ IOException -> 0x0626, RemoteException -> 0x0603 }
            r38 = 1
            r33 = r8
            r34 = r48
            r37 = r4
            r33.prepareOperationTimeout(r34, r35, r37, r38)     // Catch:{ IOException -> 0x0626, RemoteException -> 0x0603 }
            java.lang.String r4 = "obb"
            java.lang.String r5 = r3.domain     // Catch:{ IOException -> 0x0626, RemoteException -> 0x0603 }
            boolean r4 = r4.equals(r5)     // Catch:{ IOException -> 0x0626, RemoteException -> 0x0603 }
            java.lang.String r5 = " : "
            if (r4 == 0) goto L_0x0508
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x04fa, RemoteException -> 0x04ec }
            r2.<init>()     // Catch:{ IOException -> 0x04fa, RemoteException -> 0x04ec }
            java.lang.String r4 = "Restoring OBB file for "
            r2.append(r4)     // Catch:{ IOException -> 0x04fa, RemoteException -> 0x04ec }
            r2.append(r13)     // Catch:{ IOException -> 0x04fa, RemoteException -> 0x04ec }
            r2.append(r5)     // Catch:{ IOException -> 0x04fa, RemoteException -> 0x04ec }
            java.lang.String r4 = r3.path     // Catch:{ IOException -> 0x04fa, RemoteException -> 0x04ec }
            r2.append(r4)     // Catch:{ IOException -> 0x04fa, RemoteException -> 0x04ec }
            java.lang.String r2 = r2.toString()     // Catch:{ IOException -> 0x04fa, RemoteException -> 0x04ec }
            android.util.Slog.d(r14, r2)     // Catch:{ IOException -> 0x04fa, RemoteException -> 0x04ec }
            com.android.server.backup.fullbackup.FullBackupObbConnection r2 = r1.mObbConnection     // Catch:{ IOException -> 0x04fa, RemoteException -> 0x04ec }
            android.os.ParcelFileDescriptor[] r4 = r1.mPipes     // Catch:{ IOException -> 0x04fa, RemoteException -> 0x04ec }
            r5 = 0
            r18 = r4[r5]     // Catch:{ IOException -> 0x04fa, RemoteException -> 0x04ec }
            long r4 = r3.size     // Catch:{ IOException -> 0x04fa, RemoteException -> 0x04ec }
            int r8 = r3.type     // Catch:{ IOException -> 0x04fa, RemoteException -> 0x04ec }
            r33 = r6
            java.lang.String r6 = r3.path     // Catch:{ IOException -> 0x04e0, RemoteException -> 0x04d4 }
            r34 = r11
            long r11 = r3.mode     // Catch:{ IOException -> 0x04ca, RemoteException -> 0x04c0 }
            r37 = r9
            long r9 = r3.mtime     // Catch:{ IOException -> 0x04b8, RemoteException -> 0x04b0 }
            r39 = r7
            com.android.server.backup.UserBackupManagerService r7 = r1.mBackupManagerService     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            android.app.backup.IBackupManager r28 = r7.getBackupManagerBinder()     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            r16 = r2
            r17 = r13
            r19 = r4
            r21 = r8
            r22 = r6
            r23 = r11
            r25 = r9
            r27 = r48
            r16.restoreObbFile(r17, r18, r19, r21, r22, r23, r25, r27, r28)     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            r40 = r15
            goto L_0x05ee
        L_0x04b0:
            r0 = move-exception
            r39 = r7
            r2 = r0
            r40 = r15
            goto L_0x060f
        L_0x04b8:
            r0 = move-exception
            r39 = r7
            r2 = r0
            r40 = r15
            goto L_0x0632
        L_0x04c0:
            r0 = move-exception
            r39 = r7
            r37 = r9
            r2 = r0
            r40 = r15
            goto L_0x060f
        L_0x04ca:
            r0 = move-exception
            r39 = r7
            r37 = r9
            r2 = r0
            r40 = r15
            goto L_0x0632
        L_0x04d4:
            r0 = move-exception
            r39 = r7
            r37 = r9
            r34 = r11
            r2 = r0
            r40 = r15
            goto L_0x060f
        L_0x04e0:
            r0 = move-exception
            r39 = r7
            r37 = r9
            r34 = r11
            r2 = r0
            r40 = r15
            goto L_0x0632
        L_0x04ec:
            r0 = move-exception
            r33 = r6
            r39 = r7
            r37 = r9
            r34 = r11
            r2 = r0
            r40 = r15
            goto L_0x060f
        L_0x04fa:
            r0 = move-exception
            r33 = r6
            r39 = r7
            r37 = r9
            r34 = r11
            r2 = r0
            r40 = r15
            goto L_0x0632
        L_0x0508:
            r33 = r6
            r39 = r7
            r37 = r9
            r34 = r11
            java.lang.String r4 = r3.domain     // Catch:{ IOException -> 0x05fe, RemoteException -> 0x05f9 }
            boolean r2 = r2.equals(r4)     // Catch:{ IOException -> 0x05fe, RemoteException -> 0x05f9 }
            if (r2 == 0) goto L_0x0571
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            r2.<init>()     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            java.lang.String r4 = "Restoring key-value file for "
            r2.append(r4)     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            r2.append(r13)     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            r2.append(r5)     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            java.lang.String r4 = r3.path     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            r2.append(r4)     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            java.lang.String r2 = r2.toString()     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            android.util.Slog.d(r14, r2)     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            long r4 = r1.mAppVersion     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            r3.version = r4     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            com.android.server.backup.KeyValueAdbRestoreEngine r2 = new com.android.server.backup.KeyValueAdbRestoreEngine     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            com.android.server.backup.UserBackupManagerService r4 = r1.mBackupManagerService     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            com.android.server.backup.UserBackupManagerService r5 = r1.mBackupManagerService     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            java.io.File r18 = r5.getDataDir()     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            android.os.ParcelFileDescriptor[] r5 = r1.mPipes     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            r6 = 0
            r20 = r5[r6]     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            android.app.IBackupAgent r5 = r1.mAgent     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            r16 = r2
            r17 = r4
            r19 = r3
            r21 = r5
            r22 = r48
            r16.<init>(r17, r18, r19, r20, r21, r22)     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            java.lang.Thread r4 = new java.lang.Thread     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            java.lang.String r5 = "restore-key-value-runner"
            r4.<init>(r2, r5)     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            r4.start()     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            r40 = r15
            goto L_0x05ee
        L_0x0565:
            r0 = move-exception
            r2 = r0
            r40 = r15
            goto L_0x060f
        L_0x056b:
            r0 = move-exception
            r2 = r0
            r40 = r15
            goto L_0x0632
        L_0x0571:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x05fe, RemoteException -> 0x05f9 }
            r2.<init>()     // Catch:{ IOException -> 0x05fe, RemoteException -> 0x05f9 }
            java.lang.String r4 = "Invoking agent to restore file "
            r2.append(r4)     // Catch:{ IOException -> 0x05fe, RemoteException -> 0x05f9 }
            java.lang.String r4 = r3.path     // Catch:{ IOException -> 0x05fe, RemoteException -> 0x05f9 }
            r2.append(r4)     // Catch:{ IOException -> 0x05fe, RemoteException -> 0x05f9 }
            java.lang.String r2 = r2.toString()     // Catch:{ IOException -> 0x05fe, RemoteException -> 0x05f9 }
            android.util.Slog.d(r14, r2)     // Catch:{ IOException -> 0x05fe, RemoteException -> 0x05f9 }
            android.content.pm.ApplicationInfo r2 = r1.mTargetApp     // Catch:{ IOException -> 0x05fe, RemoteException -> 0x05f9 }
            java.lang.String r2 = r2.processName     // Catch:{ IOException -> 0x05fe, RemoteException -> 0x05f9 }
            java.lang.String r4 = "system"
            boolean r2 = r2.equals(r4)     // Catch:{ IOException -> 0x05fe, RemoteException -> 0x05f9 }
            if (r2 == 0) goto L_0x05c0
            java.lang.String r2 = "system process agent - spinning a thread"
            android.util.Slog.d(r14, r2)     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            com.android.server.backup.restore.RestoreFileRunnable r2 = new com.android.server.backup.restore.RestoreFileRunnable     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            com.android.server.backup.UserBackupManagerService r4 = r1.mBackupManagerService     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            android.app.IBackupAgent r5 = r1.mAgent     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            android.os.ParcelFileDescriptor[] r6 = r1.mPipes     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            r7 = 0
            r20 = r6[r7]     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            r16 = r2
            r17 = r4
            r18 = r5
            r19 = r3
            r21 = r48
            r16.<init>(r17, r18, r19, r20, r21)     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            java.lang.Thread r4 = new java.lang.Thread     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            java.lang.String r5 = "restore-sys-runner"
            r4.<init>(r2, r5)     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            r4.start()     // Catch:{ IOException -> 0x056b, RemoteException -> 0x0565 }
            r40 = r15
            goto L_0x05ee
        L_0x05c0:
            android.app.IBackupAgent r2 = r1.mAgent     // Catch:{ IOException -> 0x05fe, RemoteException -> 0x05f9 }
            android.os.ParcelFileDescriptor[] r4 = r1.mPipes     // Catch:{ IOException -> 0x05fe, RemoteException -> 0x05f9 }
            r5 = 0
            r17 = r4[r5]     // Catch:{ IOException -> 0x05fe, RemoteException -> 0x05f9 }
            long r4 = r3.size     // Catch:{ IOException -> 0x05fe, RemoteException -> 0x05f9 }
            int r6 = r3.type     // Catch:{ IOException -> 0x05fe, RemoteException -> 0x05f9 }
            java.lang.String r7 = r3.domain     // Catch:{ IOException -> 0x05fe, RemoteException -> 0x05f9 }
            java.lang.String r8 = r3.path     // Catch:{ IOException -> 0x05fe, RemoteException -> 0x05f9 }
            long r9 = r3.mode     // Catch:{ IOException -> 0x05fe, RemoteException -> 0x05f9 }
            long r11 = r3.mtime     // Catch:{ IOException -> 0x05fe, RemoteException -> 0x05f9 }
            r40 = r15
            com.android.server.backup.UserBackupManagerService r15 = r1.mBackupManagerService     // Catch:{ IOException -> 0x05f6, RemoteException -> 0x05f3 }
            android.app.backup.IBackupManager r28 = r15.getBackupManagerBinder()     // Catch:{ IOException -> 0x05f6, RemoteException -> 0x05f3 }
            r16 = r2
            r18 = r4
            r20 = r6
            r21 = r7
            r22 = r8
            r23 = r9
            r25 = r11
            r27 = r48
            r16.doRestoreFile(r17, r18, r20, r21, r22, r23, r25, r27, r28)     // Catch:{ IOException -> 0x05f6, RemoteException -> 0x05f3 }
        L_0x05ee:
            r6 = r33
            r15 = r40
            goto L_0x063a
        L_0x05f3:
            r0 = move-exception
            r2 = r0
            goto L_0x060f
        L_0x05f6:
            r0 = move-exception
            r2 = r0
            goto L_0x0632
        L_0x05f9:
            r0 = move-exception
            r40 = r15
            r2 = r0
            goto L_0x060f
        L_0x05fe:
            r0 = move-exception
            r40 = r15
            r2 = r0
            goto L_0x0632
        L_0x0603:
            r0 = move-exception
            r33 = r6
            r39 = r7
            r37 = r9
            r34 = r11
            r40 = r15
            r2 = r0
        L_0x060f:
            java.lang.String r4 = "Agent crashed during full restore"
            android.util.Slog.e(r14, r4)     // Catch:{ IOException -> 0x0618 }
            r6 = 0
            r4 = 0
            r15 = r4
            goto L_0x063a
        L_0x0618:
            r0 = move-exception
            r7 = r43
            r8 = r45
        L_0x061d:
            r12 = r48
            r9 = r49
            r2 = r0
            r11 = r34
            goto L_0x07df
        L_0x0626:
            r0 = move-exception
            r33 = r6
            r39 = r7
            r37 = r9
            r34 = r11
            r40 = r15
            r2 = r0
        L_0x0632:
            java.lang.String r4 = "Couldn't establish restore"
            android.util.Slog.d(r14, r4)     // Catch:{ IOException -> 0x072d }
            r6 = 0
            r2 = 0
            r15 = r2
        L_0x063a:
            if (r15 == 0) goto L_0x06db
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x072d }
            r2.<init>()     // Catch:{ IOException -> 0x072d }
            java.lang.String r4 = "  copying to restore agent: "
            r2.append(r4)     // Catch:{ IOException -> 0x072d }
            r4 = r37
            r2.append(r4)     // Catch:{ IOException -> 0x072d }
            java.lang.String r7 = " bytes"
            r2.append(r7)     // Catch:{ IOException -> 0x072d }
            java.lang.String r2 = r2.toString()     // Catch:{ IOException -> 0x072d }
            android.util.Slog.v(r14, r2)     // Catch:{ IOException -> 0x072d }
            r2 = 1
            java.io.FileOutputStream r7 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x072d }
            android.os.ParcelFileDescriptor[] r8 = r1.mPipes     // Catch:{ IOException -> 0x072d }
            r8 = r8[r30]     // Catch:{ IOException -> 0x072d }
            java.io.FileDescriptor r8 = r8.getFileDescriptor()     // Catch:{ IOException -> 0x072d }
            r7.<init>(r8)     // Catch:{ IOException -> 0x072d }
            r9 = r4
        L_0x0666:
            r4 = 0
            int r8 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r8 <= 0) goto L_0x06bf
            r8 = r45
            int r4 = r8.length     // Catch:{ IOException -> 0x06ba }
            long r4 = (long) r4     // Catch:{ IOException -> 0x06ba }
            int r4 = (r9 > r4 ? 1 : (r9 == r4 ? 0 : -1))
            if (r4 <= 0) goto L_0x0676
            int r4 = r8.length     // Catch:{ IOException -> 0x06ba }
            goto L_0x0677
        L_0x0676:
            int r4 = (int) r9     // Catch:{ IOException -> 0x06ba }
        L_0x0677:
            r11 = r43
            r5 = 0
            int r12 = r11.read(r8, r5, r4)     // Catch:{ IOException -> 0x06ba }
            r5 = r12
            if (r5 < 0) goto L_0x068c
            long r11 = r1.mBytes     // Catch:{ IOException -> 0x06ba }
            r16 = r3
            r17 = r4
            long r3 = (long) r5     // Catch:{ IOException -> 0x06ba }
            long r11 = r11 + r3
            r1.mBytes = r11     // Catch:{ IOException -> 0x06ba }
            goto L_0x0690
        L_0x068c:
            r16 = r3
            r17 = r4
        L_0x0690:
            if (r5 > 0) goto L_0x0693
            goto L_0x06c3
        L_0x0693:
            long r3 = (long) r5
            long r9 = r9 - r3
            if (r2 == 0) goto L_0x06b7
            r3 = 0
            r7.write(r8, r3, r5)     // Catch:{ IOException -> 0x069c }
            goto L_0x06b7
        L_0x069c:
            r0 = move-exception
            r3 = r0
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x06ba }
            r4.<init>()     // Catch:{ IOException -> 0x06ba }
            java.lang.String r11 = "Failed to write to restore pipe: "
            r4.append(r11)     // Catch:{ IOException -> 0x06ba }
            java.lang.String r11 = r3.getMessage()     // Catch:{ IOException -> 0x06ba }
            r4.append(r11)     // Catch:{ IOException -> 0x06ba }
            java.lang.String r4 = r4.toString()     // Catch:{ IOException -> 0x06ba }
            android.util.Slog.e(r14, r4)     // Catch:{ IOException -> 0x06ba }
            r2 = 0
        L_0x06b7:
            r3 = r16
            goto L_0x0666
        L_0x06ba:
            r0 = move-exception
            r7 = r43
            goto L_0x061d
        L_0x06bf:
            r8 = r45
            r16 = r3
        L_0x06c3:
            r3 = r16
            long r4 = r3.size     // Catch:{ IOException -> 0x06d9 }
            r11 = r34
            r11.skipTarPadding(r4)     // Catch:{ IOException -> 0x06d6 }
            com.android.server.backup.UserBackupManagerService r4 = r1.mBackupManagerService     // Catch:{ IOException -> 0x06d6 }
            r12 = r48
            boolean r4 = r4.waitUntilOperationComplete(r12)     // Catch:{ IOException -> 0x0795 }
            r6 = r4
            goto L_0x06e4
        L_0x06d6:
            r0 = move-exception
            goto L_0x073d
        L_0x06d9:
            r0 = move-exception
            goto L_0x0730
        L_0x06db:
            r8 = r45
            r12 = r48
            r11 = r34
            r4 = r37
            r9 = r4
        L_0x06e4:
            if (r6 != 0) goto L_0x0729
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0795 }
            r2.<init>()     // Catch:{ IOException -> 0x0795 }
            java.lang.String r4 = "Agent failure restoring "
            r2.append(r4)     // Catch:{ IOException -> 0x0795 }
            r2.append(r13)     // Catch:{ IOException -> 0x0795 }
            java.lang.String r4 = "; ending restore"
            r2.append(r4)     // Catch:{ IOException -> 0x0795 }
            java.lang.String r2 = r2.toString()     // Catch:{ IOException -> 0x0795 }
            android.util.Slog.w(r14, r2)     // Catch:{ IOException -> 0x0795 }
            com.android.server.backup.UserBackupManagerService r2 = r1.mBackupManagerService     // Catch:{ IOException -> 0x0795 }
            android.os.Handler r2 = r2.getBackupHandler()     // Catch:{ IOException -> 0x0795 }
            r4 = 18
            r2.removeMessages(r4)     // Catch:{ IOException -> 0x0795 }
            r42.tearDownPipes()     // Catch:{ IOException -> 0x0795 }
            android.content.pm.ApplicationInfo r2 = r1.mTargetApp     // Catch:{ IOException -> 0x0795 }
            r4 = 0
            r1.tearDownAgent(r2, r4)     // Catch:{ IOException -> 0x0795 }
            r2 = 0
            r1.mAgent = r2     // Catch:{ IOException -> 0x0795 }
            java.util.HashMap<java.lang.String, com.android.server.backup.restore.RestorePolicy> r2 = r1.mPackagePolicies     // Catch:{ IOException -> 0x0795 }
            com.android.server.backup.restore.RestorePolicy r4 = com.android.server.backup.restore.RestorePolicy.IGNORE     // Catch:{ IOException -> 0x0795 }
            r2.put(r13, r4)     // Catch:{ IOException -> 0x0795 }
            r2 = r13
            if (r46 == 0) goto L_0x072a
            r4 = -2
            r1.setResult(r4)     // Catch:{ IOException -> 0x0795 }
            r4 = 0
            r1.setRunning(r4)     // Catch:{ IOException -> 0x0795 }
            return r4
        L_0x0729:
            r2 = r13
        L_0x072a:
            r40 = r15
            goto L_0x0749
        L_0x072d:
            r0 = move-exception
            r8 = r45
        L_0x0730:
            r12 = r48
            r11 = r34
            r7 = r43
            r9 = r49
            r2 = r0
            goto L_0x07df
        L_0x073b:
            r0 = move-exception
            r8 = r12
        L_0x073d:
            r12 = r48
        L_0x073f:
            r7 = r43
            goto L_0x07a3
        L_0x0743:
            r8 = r12
            r2 = r13
            r40 = r15
            r12 = r48
        L_0x0749:
            if (r40 != 0) goto L_0x0797
            java.lang.String r4 = "[discarding file content]"
            android.util.Slog.d(r14, r4)     // Catch:{ IOException -> 0x0795 }
            long r4 = r3.size     // Catch:{ IOException -> 0x0795 }
            r6 = 511(0x1ff, double:2.525E-321)
            long r4 = r4 + r6
            r6 = -512(0xfffffffffffffe00, double:NaN)
            long r4 = r4 & r6
        L_0x0758:
            r6 = 0
            int r9 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r9 <= 0) goto L_0x078f
            int r6 = r8.length     // Catch:{ IOException -> 0x0795 }
            long r6 = (long) r6     // Catch:{ IOException -> 0x0795 }
            int r6 = (r4 > r6 ? 1 : (r4 == r6 ? 0 : -1))
            if (r6 <= 0) goto L_0x0766
            int r6 = r8.length     // Catch:{ IOException -> 0x0795 }
            goto L_0x0767
        L_0x0766:
            int r6 = (int) r4
        L_0x0767:
            r7 = r43
            r9 = 0
            int r10 = r7.read(r8, r9, r6)     // Catch:{ IOException -> 0x078d }
            long r9 = (long) r10     // Catch:{ IOException -> 0x078d }
            r15 = 0
            int r13 = (r9 > r15 ? 1 : (r9 == r15 ? 0 : -1))
            if (r13 < 0) goto L_0x077e
            r13 = r2
            r31 = r3
            long r2 = r1.mBytes     // Catch:{ IOException -> 0x078d }
            long r2 = r2 + r9
            r1.mBytes = r2     // Catch:{ IOException -> 0x078d }
            goto L_0x0781
        L_0x077e:
            r13 = r2
            r31 = r3
        L_0x0781:
            r2 = 0
            int r15 = (r9 > r2 ? 1 : (r9 == r2 ? 0 : -1))
            if (r15 > 0) goto L_0x0788
            goto L_0x07ca
        L_0x0788:
            long r4 = r4 - r9
            r2 = r13
            r3 = r31
            goto L_0x0758
        L_0x078d:
            r0 = move-exception
            goto L_0x07a3
        L_0x078f:
            r7 = r43
            r13 = r2
            r31 = r3
            goto L_0x07ca
        L_0x0795:
            r0 = move-exception
            goto L_0x073f
        L_0x0797:
            r7 = r43
            r13 = r2
            r31 = r3
            goto L_0x07ca
        L_0x079d:
            r0 = move-exception
            r7 = r43
            r8 = r12
            r12 = r48
        L_0x07a3:
            r9 = r49
            r2 = r0
            goto L_0x07df
        L_0x07a7:
            r0 = move-exception
            r32 = r10
            r7 = r11
            r8 = r12
            r11 = r16
            r30 = 1
            r12 = r48
            r9 = r49
            r2 = r0
        L_0x07b5:
            goto L_0x07df
        L_0x07b6:
            r0 = move-exception
            r32 = r10
            r8 = r12
            goto L_0x07d3
        L_0x07bb:
            r31 = r4
            r32 = r10
            r8 = r12
            r14 = r15
            r30 = 1
            r12 = r48
            r41 = r11
            r11 = r7
            r7 = r41
        L_0x07ca:
            r2 = r49
        L_0x07cc:
            r9 = r2
            goto L_0x07ff
        L_0x07ce:
            r0 = move-exception
            r32 = r10
            r8 = r12
            r14 = r15
        L_0x07d3:
            r30 = 1
            r12 = r48
            r41 = r11
            r11 = r7
            r7 = r41
            r9 = r49
            r2 = r0
        L_0x07df:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "io exception on restore socket read: "
            r3.append(r4)
            java.lang.String r4 = r2.getMessage()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            android.util.Slog.w(r14, r3)
            r3 = -3
            r1.setResult(r3)
            r4 = 0
            r31 = r4
        L_0x07ff:
            int r2 = r1.mInputFD
            boolean r2 = com.android.server.backup.BackupManagerServiceInjector.isCanceling(r2)
            if (r2 == 0) goto L_0x080b
            r2 = 0
            r1.setRunning(r2)
        L_0x080b:
            if (r31 != 0) goto L_0x0826
            com.android.server.backup.UserBackupManagerService r15 = r1.mBackupManagerService
            android.app.IBackupAgent r2 = r1.mAgent
            android.app.backup.IBackupManager r17 = r15.getBackupManagerBinder()
            int r3 = r1.mInputFD
            com.android.server.backup.UserBackupManagerService r4 = r1.mBackupManagerService
            android.os.Handler r19 = r4.getBackupHandler()
            r20 = 18
            r16 = r2
            r18 = r3
            com.android.server.backup.BackupManagerServiceInjector.restoreFileEnd(r15, r16, r17, r18, r19, r20)
        L_0x0826:
            if (r31 != 0) goto L_0x083e
            java.lang.String r2 = "No [more] data for this package; tearing down"
            android.util.Slog.i(r14, r2)
            r42.tearDownPipes()
            r2 = 0
            r1.setRunning(r2)
            if (r44 == 0) goto L_0x083f
            android.content.pm.ApplicationInfo r3 = r1.mTargetApp
            boolean r4 = r1.mIsAdbRestore
            r1.tearDownAgent(r3, r4)
            goto L_0x083f
        L_0x083e:
            r2 = 0
        L_0x083f:
            if (r31 == 0) goto L_0x0842
            goto L_0x0844
        L_0x0842:
            r30 = r2
        L_0x0844:
            return r30
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.restore.FullRestoreEngine.restoreOneFile(java.io.InputStream, boolean, byte[], android.content.pm.PackageInfo, boolean, int, android.app.backup.IBackupManagerMonitor):boolean");
    }

    /* renamed from: com.android.server.backup.restore.FullRestoreEngine$2  reason: invalid class name */
    static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$android$server$backup$restore$RestorePolicy = new int[RestorePolicy.values().length];

        static {
            try {
                $SwitchMap$com$android$server$backup$restore$RestorePolicy[RestorePolicy.IGNORE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$android$server$backup$restore$RestorePolicy[RestorePolicy.ACCEPT_IF_APK.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$android$server$backup$restore$RestorePolicy[RestorePolicy.ACCEPT.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }

    private void setUpPipes() throws IOException {
        synchronized (this.mPipesLock) {
            this.mPipes = ParcelFileDescriptor.createPipe();
            this.mPipesClosed = false;
        }
    }

    private void tearDownPipes() {
        synchronized (this.mPipesLock) {
            if (!this.mPipesClosed && this.mPipes != null) {
                try {
                    this.mPipes[0].close();
                    this.mPipes[1].close();
                    this.mPipesClosed = true;
                } catch (IOException e) {
                    Slog.w(BackupManagerService.TAG, "Couldn't close agent pipes", e);
                }
            }
        }
    }

    private void tearDownAgent(ApplicationInfo app, boolean doRestoreFinished) {
        IBackupAgent iBackupAgent = this.mAgent;
        if (iBackupAgent != null) {
            BackupManagerServiceInjector.unlinkToDeath(iBackupAgent);
            if (BackupManagerServiceInjector.isCanceling(this.mInputFD)) {
                Slog.e(BackupManagerService.TAG, "tearDownAgent - isCanceling!");
                doRestoreFinished = false;
            }
            if (doRestoreFinished) {
                try {
                    int token = this.mBackupManagerService.generateRandomIntegerToken();
                    long fullBackupAgentTimeoutMillis = this.mAgentTimeoutParameters.getFullBackupAgentTimeoutMillis();
                    AdbRestoreFinishedLatch latch = new AdbRestoreFinishedLatch(this.mBackupManagerService, token);
                    this.mBackupManagerService.prepareOperationTimeout(token, fullBackupAgentTimeoutMillis, latch, 1);
                    if (this.mTargetApp.processName.equals("system")) {
                        Slog.d(BackupManagerService.TAG, "system agent - restoreFinished on thread");
                        new Thread(new AdbRestoreFinishedRunnable(this.mAgent, token, this.mBackupManagerService), "restore-sys-finished-runner").start();
                    } else {
                        this.mAgent.doRestoreFinished(token, this.mBackupManagerService.getBackupManagerBinder());
                    }
                    latch.await();
                } catch (RemoteException e) {
                    Slog.d(BackupManagerService.TAG, "Lost app trying to shut down");
                }
            }
            this.mBackupManagerService.tearDownAgentAndKill(app);
            this.mAgent = null;
        }
    }

    /* access modifiers changed from: package-private */
    public void handleTimeout() {
        tearDownPipes();
        setResult(-2);
        setRunning(false);
    }

    private static boolean isRestorableFile(FileMetadata info) {
        if ("c".equals(info.domain)) {
            Slog.i(BackupManagerService.TAG, "Dropping cache file path " + info.path);
            return false;
        } else if (!ActivityTaskManagerService.DUMP_RECENTS_SHORT_CMD.equals(info.domain) || !info.path.startsWith("no_backup/")) {
            return true;
        } else {
            Slog.i(BackupManagerService.TAG, "Dropping no_backup file path " + info.path);
            return false;
        }
    }

    private static boolean isCanonicalFilePath(String path) {
        if (!path.contains("..") && !path.contains("//")) {
            return true;
        }
        Slog.w(BackupManagerService.TAG, "Dropping invalid path " + path);
        return false;
    }

    private boolean shouldForceClearAppDataOnFullRestore(String packageName) {
        String packageListString = Settings.Secure.getStringForUser(this.mBackupManagerService.getContext().getContentResolver(), "packages_to_clear_data_before_full_restore", this.mUserId);
        if (TextUtils.isEmpty(packageListString)) {
            return false;
        }
        return Arrays.asList(packageListString.split(";")).contains(packageName);
    }

    /* access modifiers changed from: package-private */
    public void sendOnRestorePackage(String name) {
        IFullBackupRestoreObserver iFullBackupRestoreObserver = this.mObserver;
        if (iFullBackupRestoreObserver != null) {
            try {
                iFullBackupRestoreObserver.onRestorePackage(name);
            } catch (RemoteException e) {
                Slog.w(BackupManagerService.TAG, "full restore observer went away: restorePackage");
                this.mObserver = null;
            }
        }
    }

    public void setObbConnection(FullBackupObbConnection obbConnection) {
        this.mObbConnection = obbConnection;
    }
}
