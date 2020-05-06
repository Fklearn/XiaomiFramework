package com.android.server.backup.fullbackup;

import android.app.IBackupAgent;
import android.app.backup.IBackupCallback;
import android.app.backup.IBackupManagerMonitor;
import android.app.backup.IBackupObserver;
import android.app.backup.IFullBackupRestoreObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.util.Slog;
import com.android.internal.util.Preconditions;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.backup.BackupRestoreTask;
import com.android.server.backup.FullBackupJob;
import com.android.server.backup.TransportManager;
import com.android.server.backup.UserBackupManagerService;
import com.android.server.backup.fullbackup.PerformFullTransportBackupTask;
import com.android.server.backup.internal.OnTaskFinishedListener;
import com.android.server.backup.internal.Operation;
import com.android.server.backup.remote.RemoteCall;
import com.android.server.backup.remote.RemoteCallable;
import com.android.server.backup.transport.TransportClient;
import com.android.server.backup.transport.TransportNotAvailableException;
import com.android.server.backup.utils.AppBackupUtils;
import com.android.server.backup.utils.BackupManagerMonitorUtils;
import com.android.server.backup.utils.BackupObserverUtils;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class PerformFullTransportBackupTask extends FullBackupTask implements BackupRestoreTask {
    private static final String TAG = "PFTBT";
    /* access modifiers changed from: private */
    public UserBackupManagerService backupManagerService;
    /* access modifiers changed from: private */
    public final BackupAgentTimeoutParameters mAgentTimeoutParameters;
    IBackupObserver mBackupObserver;
    SinglePackageBackupRunner mBackupRunner;
    private final int mBackupRunnerOpToken;
    private volatile boolean mCancelAll;
    private final Object mCancelLock = new Object();
    private final int mCurrentOpToken;
    PackageInfo mCurrentPackage;
    private volatile boolean mIsDoingBackup;
    FullBackupJob mJob;
    CountDownLatch mLatch;
    private final OnTaskFinishedListener mListener;
    /* access modifiers changed from: private */
    public IBackupManagerMonitor mMonitor;
    ArrayList<PackageInfo> mPackages;
    private final TransportClient mTransportClient;
    boolean mUpdateSchedule;
    private final int mUserId;
    boolean mUserInitiated;

    public static PerformFullTransportBackupTask newWithCurrentTransport(UserBackupManagerService backupManagerService2, IFullBackupRestoreObserver observer, String[] whichPackages, boolean updateSchedule, FullBackupJob runningJob, CountDownLatch latch, IBackupObserver backupObserver, IBackupManagerMonitor monitor, boolean userInitiated, String caller) {
        TransportManager transportManager = backupManagerService2.getTransportManager();
        TransportClient transportClient = transportManager.getCurrentTransportClient(caller);
        return new PerformFullTransportBackupTask(backupManagerService2, transportClient, observer, whichPackages, updateSchedule, runningJob, latch, backupObserver, monitor, new OnTaskFinishedListener(transportClient) {
            private final /* synthetic */ TransportClient f$1;

            {
                this.f$1 = r2;
            }

            public final void onFinished(String str) {
                TransportManager.this.disposeOfTransportClient(this.f$1, str);
            }
        }, userInitiated);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public PerformFullTransportBackupTask(UserBackupManagerService backupManagerService2, TransportClient transportClient, IFullBackupRestoreObserver observer, String[] whichPackages, boolean updateSchedule, FullBackupJob runningJob, CountDownLatch latch, IBackupObserver backupObserver, IBackupManagerMonitor monitor, OnTaskFinishedListener listener, boolean userInitiated) {
        super(observer);
        String[] strArr = whichPackages;
        this.backupManagerService = backupManagerService2;
        this.mTransportClient = transportClient;
        this.mUpdateSchedule = updateSchedule;
        this.mLatch = latch;
        this.mJob = runningJob;
        this.mPackages = new ArrayList<>(strArr.length);
        this.mBackupObserver = backupObserver;
        this.mMonitor = monitor;
        this.mListener = listener != null ? listener : OnTaskFinishedListener.NOP;
        this.mUserInitiated = userInitiated;
        this.mCurrentOpToken = backupManagerService2.generateRandomIntegerToken();
        this.mBackupRunnerOpToken = backupManagerService2.generateRandomIntegerToken();
        this.mAgentTimeoutParameters = (BackupAgentTimeoutParameters) Preconditions.checkNotNull(backupManagerService2.getAgentTimeoutParameters(), "Timeout parameters cannot be null");
        this.mUserId = backupManagerService2.getUserId();
        if (backupManagerService2.isBackupOperationInProgress()) {
            Slog.d(TAG, "Skipping full backup. A backup is already in progress.");
            this.mCancelAll = true;
            return;
        }
        registerTask();
        int length = strArr.length;
        int i = 0;
        while (i < length) {
            String pkg = strArr[i];
            try {
                PackageManager pm = backupManagerService2.getPackageManager();
                PackageInfo info = pm.getPackageInfoAsUser(pkg, 134217728, this.mUserId);
                this.mCurrentPackage = info;
                PackageManager packageManager = pm;
                if (!AppBackupUtils.appIsEligibleForBackup(info.applicationInfo, this.mUserId)) {
                    Slog.d(TAG, "Ignoring ineligible package " + pkg);
                    this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 9, this.mCurrentPackage, 3, (Bundle) null);
                    BackupObserverUtils.sendBackupOnPackageResult(this.mBackupObserver, pkg, -2001);
                } else if (!AppBackupUtils.appGetsFullBackup(info)) {
                    Slog.d(TAG, "Ignoring full-data backup of key/value participant " + pkg);
                    this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 10, this.mCurrentPackage, 3, (Bundle) null);
                    BackupObserverUtils.sendBackupOnPackageResult(this.mBackupObserver, pkg, -2001);
                } else if (AppBackupUtils.appIsStopped(info.applicationInfo)) {
                    Slog.d(TAG, "Ignoring stopped package " + pkg);
                    this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 11, this.mCurrentPackage, 3, (Bundle) null);
                    BackupObserverUtils.sendBackupOnPackageResult(this.mBackupObserver, pkg, -2001);
                } else {
                    this.mPackages.add(info);
                }
            } catch (PackageManager.NameNotFoundException e) {
                Slog.i(TAG, "Requested package " + pkg + " not found; ignoring");
                this.mMonitor = BackupManagerMonitorUtils.monitorEvent(this.mMonitor, 12, this.mCurrentPackage, 3, (Bundle) null);
            }
            i++;
            UserBackupManagerService userBackupManagerService = backupManagerService2;
            TransportClient transportClient2 = transportClient;
            IFullBackupRestoreObserver iFullBackupRestoreObserver = observer;
            strArr = whichPackages;
            boolean z = updateSchedule;
        }
    }

    private void registerTask() {
        synchronized (this.backupManagerService.getCurrentOpLock()) {
            Slog.d(TAG, "backupmanager pftbt token=" + Integer.toHexString(this.mCurrentOpToken));
            this.backupManagerService.getCurrentOperations().put(this.mCurrentOpToken, new Operation(0, this, 2));
        }
    }

    public void unregisterTask() {
        this.backupManagerService.removeOperation(this.mCurrentOpToken);
    }

    public void execute() {
    }

    public void handleCancel(boolean cancelAll) {
        synchronized (this.mCancelLock) {
            if (!cancelAll) {
                Slog.wtf(TAG, "Expected cancelAll to be true.");
            }
            if (this.mCancelAll) {
                Slog.d(TAG, "Ignoring duplicate cancel call.");
                return;
            }
            this.mCancelAll = true;
            if (this.mIsDoingBackup) {
                this.backupManagerService.handleCancel(this.mBackupRunnerOpToken, cancelAll);
                try {
                    this.mTransportClient.getConnectedTransport("PFTBT.handleCancel()").cancelFullBackup();
                } catch (RemoteException | TransportNotAvailableException e) {
                    Slog.w(TAG, "Error calling cancelFullBackup() on transport: " + e);
                }
            }
        }
    }

    public void operationComplete(long result) {
    }

    /* Debug info: failed to restart local var, previous not found, register: 34 */
    /* JADX INFO: finally extract failed */
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
    /* JADX WARNING: Removed duplicated region for block: B:365:0x0830  */
    /* JADX WARNING: Removed duplicated region for block: B:366:0x0833  */
    /* JADX WARNING: Removed duplicated region for block: B:369:0x085c  */
    public void run() {
        /*
            r34 = this;
            r10 = r34
            r1 = 0
            r2 = 0
            r3 = 0
            r11 = 0
            r13 = 0
            com.android.server.backup.UserBackupManagerService r5 = r10.backupManagerService     // Catch:{ Exception -> 0x0806, all -> 0x07fe }
            boolean r5 = r5.isEnabled()     // Catch:{ Exception -> 0x0806, all -> 0x07fe }
            if (r5 == 0) goto L_0x073c
            com.android.server.backup.UserBackupManagerService r5 = r10.backupManagerService     // Catch:{ Exception -> 0x0806, all -> 0x07fe }
            boolean r5 = r5.isSetupComplete()     // Catch:{ Exception -> 0x0806, all -> 0x07fe }
            if (r5 != 0) goto L_0x001c
            r27 = r11
            goto L_0x073e
        L_0x001c:
            com.android.server.backup.transport.TransportClient r5 = r10.mTransportClient     // Catch:{ Exception -> 0x0806, all -> 0x07fe }
            java.lang.String r6 = "PFTBT.run()"
            com.android.internal.backup.IBackupTransport r5 = r5.connect(r6)     // Catch:{ Exception -> 0x0806, all -> 0x07fe }
            r15 = r5
            r9 = 1
            if (r15 != 0) goto L_0x00b9
            java.lang.String r5 = "PFTBT"
            java.lang.String r6 = "Transport not present; full data backup not performed"
            android.util.Slog.w(r5, r6)     // Catch:{ Exception -> 0x00b2, all -> 0x00aa }
            r11 = -1000(0xfffffffffffffc18, float:NaN)
            android.app.backup.IBackupManagerMonitor r5 = r10.mMonitor     // Catch:{ Exception -> 0x00b2, all -> 0x00aa }
            r6 = 15
            android.content.pm.PackageInfo r7 = r10.mCurrentPackage     // Catch:{ Exception -> 0x00b2, all -> 0x00aa }
            android.app.backup.IBackupManagerMonitor r5 = com.android.server.backup.utils.BackupManagerMonitorUtils.monitorEvent(r5, r6, r7, r9, r13)     // Catch:{ Exception -> 0x00b2, all -> 0x00aa }
            r10.mMonitor = r5     // Catch:{ Exception -> 0x00b2, all -> 0x00aa }
            boolean r5 = r10.mCancelAll
            if (r5 == 0) goto L_0x0045
            r11 = -2003(0xfffffffffffff82d, float:NaN)
            r5 = r11
            goto L_0x0046
        L_0x0045:
            r5 = r11
        L_0x0046:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Full backup completed with status: "
            r6.append(r7)
            r6.append(r5)
            java.lang.String r6 = r6.toString()
            java.lang.String r7 = "PFTBT"
            android.util.Slog.i(r7, r6)
            android.app.backup.IBackupObserver r6 = r10.mBackupObserver
            com.android.server.backup.utils.BackupObserverUtils.sendBackupFinished(r6, r5)
            r10.cleanUpPipes(r2)
            r10.cleanUpPipes(r1)
            r34.unregisterTask()
            com.android.server.backup.FullBackupJob r6 = r10.mJob
            if (r6 == 0) goto L_0x0073
            int r7 = r10.mUserId
            r6.finishBackupPass(r7)
        L_0x0073:
            com.android.server.backup.UserBackupManagerService r6 = r10.backupManagerService
            java.lang.Object r6 = r6.getQueueLock()
            monitor-enter(r6)
            com.android.server.backup.UserBackupManagerService r7 = r10.backupManagerService     // Catch:{ all -> 0x00a6 }
            r7.setRunningFullBackupTask(r13)     // Catch:{ all -> 0x00a6 }
            monitor-exit(r6)     // Catch:{ all -> 0x00a6 }
            com.android.server.backup.internal.OnTaskFinishedListener r6 = r10.mListener
            java.lang.String r7 = "PFTBT.run()"
            r6.onFinished(r7)
            java.util.concurrent.CountDownLatch r6 = r10.mLatch
            r6.countDown()
            boolean r6 = r10.mUpdateSchedule
            if (r6 == 0) goto L_0x0095
            com.android.server.backup.UserBackupManagerService r6 = r10.backupManagerService
            r6.scheduleNextFullBackupJob(r3)
        L_0x0095:
            java.lang.String r6 = "PFTBT"
            java.lang.String r7 = "Full data backup pass finished."
            android.util.Slog.i(r6, r7)
            com.android.server.backup.UserBackupManagerService r6 = r10.backupManagerService
            android.os.PowerManager$WakeLock r6 = r6.getWakelock()
            r6.release()
            return
        L_0x00a6:
            r0 = move-exception
            r7 = r0
            monitor-exit(r6)     // Catch:{ all -> 0x00a6 }
            throw r7
        L_0x00aa:
            r0 = move-exception
            r6 = r1
            r13 = r2
            r27 = r11
            r1 = r0
            goto L_0x08a0
        L_0x00b2:
            r0 = move-exception
            r6 = r1
            r27 = r11
            r1 = r0
            goto L_0x080b
        L_0x00b9:
            java.util.ArrayList<android.content.pm.PackageInfo> r5 = r10.mPackages     // Catch:{ Exception -> 0x0806, all -> 0x07fe }
            int r5 = r5.size()     // Catch:{ Exception -> 0x0806, all -> 0x07fe }
            r8 = r5
            r5 = 8192(0x2000, float:1.14794E-41)
            byte[] r5 = new byte[r5]     // Catch:{ Exception -> 0x0806, all -> 0x07fe }
            r6 = 0
            r32 = r3
            r4 = r6
            r6 = r32
        L_0x00ca:
            if (r4 >= r8) goto L_0x06b6
            r10.mBackupRunner = r13     // Catch:{ Exception -> 0x06ab, all -> 0x069f }
            java.util.ArrayList<android.content.pm.PackageInfo> r3 = r10.mPackages     // Catch:{ Exception -> 0x06ab, all -> 0x069f }
            java.lang.Object r3 = r3.get(r4)     // Catch:{ Exception -> 0x06ab, all -> 0x069f }
            android.content.pm.PackageInfo r3 = (android.content.pm.PackageInfo) r3     // Catch:{ Exception -> 0x06ab, all -> 0x069f }
            java.lang.String r12 = r3.packageName     // Catch:{ Exception -> 0x06ab, all -> 0x069f }
            java.lang.String r13 = "PFTBT"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x06ab, all -> 0x069f }
            r9.<init>()     // Catch:{ Exception -> 0x06ab, all -> 0x069f }
            java.lang.String r14 = "Initiating full-data transport backup of "
            r9.append(r14)     // Catch:{ Exception -> 0x06ab, all -> 0x069f }
            r9.append(r12)     // Catch:{ Exception -> 0x06ab, all -> 0x069f }
            java.lang.String r14 = " token: "
            r9.append(r14)     // Catch:{ Exception -> 0x06ab, all -> 0x069f }
            int r14 = r10.mCurrentOpToken     // Catch:{ Exception -> 0x06ab, all -> 0x069f }
            r9.append(r14)     // Catch:{ Exception -> 0x06ab, all -> 0x069f }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x06ab, all -> 0x069f }
            android.util.Slog.i(r13, r9)     // Catch:{ Exception -> 0x06ab, all -> 0x069f }
            r9 = 2840(0xb18, float:3.98E-42)
            android.util.EventLog.writeEvent(r9, r12)     // Catch:{ Exception -> 0x06ab, all -> 0x069f }
            android.os.ParcelFileDescriptor[] r9 = android.os.ParcelFileDescriptor.createPipe()     // Catch:{ Exception -> 0x06ab, all -> 0x069f }
            r13 = r9
            boolean r2 = r10.mUserInitiated     // Catch:{ Exception -> 0x0693, all -> 0x0688 }
            if (r2 == 0) goto L_0x0108
            r2 = 1
            goto L_0x0109
        L_0x0108:
            r2 = 0
        L_0x0109:
            r14 = r2
            r17 = 9223372036854775807(0x7fffffffffffffff, double:NaN)
            java.lang.Object r9 = r10.mCancelLock     // Catch:{ Exception -> 0x0693, all -> 0x0688 }
            monitor-enter(r9)     // Catch:{ Exception -> 0x0693, all -> 0x0688 }
            boolean r2 = r10.mCancelAll     // Catch:{ all -> 0x0664 }
            if (r2 == 0) goto L_0x0131
            monitor-exit(r9)     // Catch:{ all -> 0x011d }
            r23 = r6
            r27 = r11
            goto L_0x06c1
        L_0x011d:
            r0 = move-exception
            r19 = r4
            r23 = r6
            r25 = r8
            r16 = r9
            r27 = r11
            r28 = r14
            r8 = r15
            r6 = r1
            r14 = r5
            r1 = r0
            r5 = r3
            goto L_0x0676
        L_0x0131:
            r19 = r4
            r2 = 0
            r4 = r13[r2]     // Catch:{ all -> 0x0653 }
            int r2 = r15.performFullBackup(r3, r4, r14)     // Catch:{ all -> 0x0653 }
            r20 = r2
            if (r20 != 0) goto L_0x01d8
            java.lang.String r2 = r3.packageName     // Catch:{ all -> 0x01c6 }
            r4 = 1
            long r21 = r15.getBackupQuota(r2, r4)     // Catch:{ all -> 0x01c6 }
            r23 = r6
            r6 = r21
            android.os.ParcelFileDescriptor[] r2 = android.os.ParcelFileDescriptor.createPipe()     // Catch:{ all -> 0x01b4 }
            r17 = r2
            com.android.server.backup.fullbackup.PerformFullTransportBackupTask$SinglePackageBackupRunner r4 = new com.android.server.backup.fullbackup.PerformFullTransportBackupTask$SinglePackageBackupRunner     // Catch:{ all -> 0x019f }
            r16 = 1
            r18 = r17[r16]     // Catch:{ all -> 0x019f }
            com.android.server.backup.transport.TransportClient r2 = r10.mTransportClient     // Catch:{ all -> 0x019f }
            int r1 = r10.mBackupRunnerOpToken     // Catch:{ all -> 0x019f }
            int r21 = r15.getTransportFlags()     // Catch:{ all -> 0x019f }
            r22 = r1
            r1 = r4
            r25 = r2
            r2 = r34
            r26 = r3
            r3 = r18
            r27 = r11
            r11 = r4
            r4 = r26
            r28 = r14
            r14 = r5
            r5 = r25
            r25 = r8
            r8 = r22
            r22 = r15
            r15 = r16
            r16 = r9
            r9 = r21
            r1.<init>(r2, r3, r4, r5, r6, r8, r9)     // Catch:{ all -> 0x0191 }
            r10.mBackupRunner = r11     // Catch:{ all -> 0x0191 }
            r1 = r17[r15]     // Catch:{ all -> 0x0191 }
            r1.close()     // Catch:{ all -> 0x0191 }
            r1 = 0
            r17[r15] = r1     // Catch:{ all -> 0x0191 }
            r10.mIsDoingBackup = r15     // Catch:{ all -> 0x0191 }
            r1 = r17
            goto L_0x01ea
        L_0x0191:
            r0 = move-exception
            r1 = r0
            r8 = r22
            r5 = r26
            r32 = r6
            r6 = r17
            r17 = r32
            goto L_0x0676
        L_0x019f:
            r0 = move-exception
            r25 = r8
            r16 = r9
            r27 = r11
            r28 = r14
            r14 = r5
            r1 = r0
            r5 = r3
            r8 = r15
            r32 = r6
            r6 = r17
            r17 = r32
            goto L_0x0676
        L_0x01b4:
            r0 = move-exception
            r25 = r8
            r16 = r9
            r27 = r11
            r28 = r14
            r14 = r5
            r5 = r3
            r17 = r6
            r8 = r15
            r6 = r1
            r1 = r0
            goto L_0x0676
        L_0x01c6:
            r0 = move-exception
            r23 = r6
            r25 = r8
            r16 = r9
            r27 = r11
            r28 = r14
            r14 = r5
            r6 = r1
            r5 = r3
            r8 = r15
            r1 = r0
            goto L_0x0676
        L_0x01d8:
            r26 = r3
            r23 = r6
            r25 = r8
            r16 = r9
            r27 = r11
            r28 = r14
            r22 = r15
            r15 = 1
            r14 = r5
            r6 = r17
        L_0x01ea:
            monitor-exit(r16)     // Catch:{ all -> 0x0649 }
            if (r20 != 0) goto L_0x041c
            r3 = 0
            r4 = r13[r3]     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            r4.close()     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            r4 = 0
            r13[r3] = r4     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            java.lang.Thread r3 = new java.lang.Thread     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            com.android.server.backup.fullbackup.PerformFullTransportBackupTask$SinglePackageBackupRunner r4 = r10.mBackupRunner     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            java.lang.String r5 = "package-backup-bridge"
            r3.<init>(r4, r5)     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            r3.start()     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            java.io.FileInputStream r3 = new java.io.FileInputStream     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            r4 = 0
            r5 = r1[r4]     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            java.io.FileDescriptor r4 = r5.getFileDescriptor()     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            r3.<init>(r4)     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            java.io.FileOutputStream r4 = new java.io.FileOutputStream     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            r5 = r13[r15]     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            java.io.FileDescriptor r5 = r5.getFileDescriptor()     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            r4.<init>(r5)     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            r8 = 0
            com.android.server.backup.fullbackup.PerformFullTransportBackupTask$SinglePackageBackupRunner r5 = r10.mBackupRunner     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            long r17 = r5.getPreflightResultBlocking()     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            r29 = r17
            r17 = 0
            r11 = r3
            r2 = r29
            int r16 = (r2 > r17 ? 1 : (r2 == r17 ? 0 : -1))
            if (r16 >= 0) goto L_0x0275
            java.lang.String r5 = "PFTBT"
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            r15.<init>()     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            r29 = r8
            java.lang.String r8 = "Backup error after preflight of package "
            r15.append(r8)     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            r15.append(r12)     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            java.lang.String r8 = ": "
            r15.append(r8)     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            r15.append(r2)     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            java.lang.String r8 = ", not running backup."
            r15.append(r8)     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            java.lang.String r8 = r15.toString()     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            android.util.Slog.d(r5, r8)     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            android.app.backup.IBackupManagerMonitor r5 = r10.mMonitor     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            android.content.pm.PackageInfo r9 = r10.mCurrentPackage     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            java.lang.String r15 = "android.app.backup.extra.LOG_PREFLIGHT_ERROR"
            r8 = 0
            android.os.Bundle r15 = com.android.server.backup.utils.BackupManagerMonitorUtils.putMonitoringExtra((android.os.Bundle) r8, (java.lang.String) r15, (long) r2)     // Catch:{ Exception -> 0x0411, all -> 0x0407 }
            r31 = r1
            r1 = 3
            r8 = 16
            android.app.backup.IBackupManagerMonitor r5 = com.android.server.backup.utils.BackupManagerMonitorUtils.monitorEvent(r5, r8, r9, r1, r15)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            r10.mMonitor = r5     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            int r1 = (int) r2     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            r8 = r22
            r22 = r4
            r32 = r2
            r2 = r29
            r29 = r32
            goto L_0x034d
        L_0x0275:
            r31 = r1
            r29 = r8
            r1 = 0
            r5 = r1
        L_0x027b:
            int r1 = r11.read(r14)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            java.lang.String r5 = "PFTBT"
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            r8.<init>()     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            java.lang.String r9 = "in.read(buffer) from app: "
            r8.append(r9)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            r8.append(r1)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            java.lang.String r8 = r8.toString()     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            android.util.Slog.v(r5, r8)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            if (r1 <= 0) goto L_0x02e5
            r5 = 0
            r4.write(r14, r5, r1)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            java.lang.Object r5 = r10.mCancelLock     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            monitor-enter(r5)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            boolean r8 = r10.mCancelAll     // Catch:{ all -> 0x02da }
            if (r8 != 0) goto L_0x02b1
            r8 = r22
            int r9 = r8.sendBackupData(r1)     // Catch:{ all -> 0x02ac }
            r20 = r9
            goto L_0x02b3
        L_0x02ac:
            r0 = move-exception
            r22 = r4
            r4 = r0
            goto L_0x02e0
        L_0x02b1:
            r8 = r22
        L_0x02b3:
            monitor-exit(r5)     // Catch:{ all -> 0x02d5 }
            r9 = r4
            long r4 = (long) r1
            long r4 = r29 + r4
            android.app.backup.IBackupObserver r15 = r10.mBackupObserver     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            if (r15 == 0) goto L_0x02cd
            int r15 = (r2 > r17 ? 1 : (r2 == r17 ? 0 : -1))
            if (r15 <= 0) goto L_0x02cd
            android.app.backup.IBackupObserver r15 = r10.mBackupObserver     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            r22 = r9
            android.app.backup.BackupProgress r9 = new android.app.backup.BackupProgress     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            r9.<init>(r2, r4)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            com.android.server.backup.utils.BackupObserverUtils.sendBackupOnUpdate(r15, r12, r9)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            goto L_0x02cf
        L_0x02cd:
            r22 = r9
        L_0x02cf:
            r29 = r2
            r2 = r4
            r4 = r20
            goto L_0x02f1
        L_0x02d5:
            r0 = move-exception
            r22 = r4
            r4 = r0
            goto L_0x02e0
        L_0x02da:
            r0 = move-exception
            r8 = r22
            r22 = r4
            r4 = r0
        L_0x02e0:
            monitor-exit(r5)     // Catch:{ all -> 0x02e2 }
            throw r4     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
        L_0x02e2:
            r0 = move-exception
            r4 = r0
            goto L_0x02e0
        L_0x02e5:
            r8 = r22
            r22 = r4
            r4 = r20
            r32 = r2
            r2 = r29
            r29 = r32
        L_0x02f1:
            if (r1 <= 0) goto L_0x0305
            if (r4 == 0) goto L_0x02f6
            goto L_0x0305
        L_0x02f6:
            r5 = r1
            r20 = r4
            r4 = r22
            r22 = r8
            r32 = r2
            r2 = r29
            r29 = r32
            goto L_0x027b
        L_0x0305:
            r5 = -1005(0xfffffffffffffc13, float:NaN)
            if (r4 != r5) goto L_0x0347
            java.lang.String r9 = "PFTBT"
            java.lang.StringBuilder r15 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            r15.<init>()     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            java.lang.String r5 = "Package hit quota limit in-flight "
            r15.append(r5)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            r15.append(r12)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            java.lang.String r5 = ": "
            r15.append(r5)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            r15.append(r2)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            java.lang.String r5 = " of "
            r15.append(r5)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            r15.append(r6)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            java.lang.String r5 = r15.toString()     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            android.util.Slog.w(r9, r5)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            android.app.backup.IBackupManagerMonitor r5 = r10.mMonitor     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            r9 = 18
            android.content.pm.PackageInfo r15 = r10.mCurrentPackage     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            r17 = r1
            r18 = r4
            r1 = 1
            r4 = 0
            android.app.backup.IBackupManagerMonitor r5 = com.android.server.backup.utils.BackupManagerMonitorUtils.monitorEvent(r5, r9, r15, r1, r4)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            r10.mMonitor = r5     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            com.android.server.backup.fullbackup.PerformFullTransportBackupTask$SinglePackageBackupRunner r1 = r10.mBackupRunner     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            r1.sendQuotaExceeded(r2, r6)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            goto L_0x034b
        L_0x0347:
            r17 = r1
            r18 = r4
        L_0x034b:
            r1 = r18
        L_0x034d:
            com.android.server.backup.fullbackup.PerformFullTransportBackupTask$SinglePackageBackupRunner r4 = r10.mBackupRunner     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            int r4 = r4.getBackupResultBlocking()     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            java.lang.Object r5 = r10.mCancelLock     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            monitor-enter(r5)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            r9 = 0
            r10.mIsDoingBackup = r9     // Catch:{ all -> 0x03ec }
            boolean r9 = r10.mCancelAll     // Catch:{ all -> 0x03ec }
            if (r9 != 0) goto L_0x0372
            if (r4 != 0) goto L_0x0367
            int r9 = r8.finishBackup()     // Catch:{ all -> 0x036b }
            if (r1 != 0) goto L_0x0366
            r1 = r9
        L_0x0366:
            goto L_0x0372
        L_0x0367:
            r8.cancelFullBackup()     // Catch:{ all -> 0x036b }
            goto L_0x0372
        L_0x036b:
            r0 = move-exception
            r17 = r2
            r2 = r1
            r1 = r0
            goto L_0x03f1
        L_0x0372:
            monitor-exit(r5)     // Catch:{ all -> 0x03ec }
            if (r1 != 0) goto L_0x0379
            if (r4 == 0) goto L_0x0380
            r1 = r4
            goto L_0x0380
        L_0x0379:
            java.lang.String r5 = "PFTBT"
            java.lang.String r9 = "Transport-level failure; cancelling agent work"
            android.util.Slog.i(r5, r9)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
        L_0x0380:
            java.lang.String r5 = "PFTBT"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            r9.<init>()     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            java.lang.String r15 = "Done delivering backup data: result="
            r9.append(r15)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            r9.append(r1)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            android.util.Slog.i(r5, r9)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            if (r1 == 0) goto L_0x03b6
            java.lang.String r5 = "PFTBT"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            r9.<init>()     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            java.lang.String r15 = "Error "
            r9.append(r15)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            r9.append(r1)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            java.lang.String r15 = " backing up "
            r9.append(r15)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            r9.append(r12)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            android.util.Slog.e(r5, r9)     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
        L_0x03b6:
            long r17 = r8.requestFullBackupTime()     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
            r23 = r17
            java.lang.String r5 = "PFTBT"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x03e2, all -> 0x03d9 }
            r9.<init>()     // Catch:{ Exception -> 0x03e2, all -> 0x03d9 }
            java.lang.String r15 = "Transport suggested backoff="
            r9.append(r15)     // Catch:{ Exception -> 0x03e2, all -> 0x03d9 }
            r17 = r2
            r3 = r1
            r1 = r23
            r9.append(r1)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            android.util.Slog.i(r5, r9)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            goto L_0x0424
        L_0x03d9:
            r0 = move-exception
            r1 = r23
            r3 = r1
            r6 = r31
            r1 = r0
            goto L_0x08a0
        L_0x03e2:
            r0 = move-exception
            r1 = r23
            r3 = r1
            r2 = r13
            r6 = r31
            r1 = r0
            goto L_0x080b
        L_0x03ec:
            r0 = move-exception
            r17 = r2
            r2 = r1
            r1 = r0
        L_0x03f1:
            monitor-exit(r5)     // Catch:{ all -> 0x03f3 }
            throw r1     // Catch:{ Exception -> 0x03fe, all -> 0x03f6 }
        L_0x03f3:
            r0 = move-exception
            r1 = r0
            goto L_0x03f1
        L_0x03f6:
            r0 = move-exception
            r1 = r0
            r3 = r23
            r6 = r31
            goto L_0x08a0
        L_0x03fe:
            r0 = move-exception
            r1 = r0
            r2 = r13
            r3 = r23
            r6 = r31
            goto L_0x080b
        L_0x0407:
            r0 = move-exception
            r31 = r1
            r1 = r0
            r3 = r23
            r6 = r31
            goto L_0x08a0
        L_0x0411:
            r0 = move-exception
            r31 = r1
            r1 = r0
            r2 = r13
            r3 = r23
            r6 = r31
            goto L_0x080b
        L_0x041c:
            r31 = r1
            r8 = r22
            r3 = r20
            r1 = r23
        L_0x0424:
            boolean r4 = r10.mUpdateSchedule     // Catch:{ Exception -> 0x0641, all -> 0x063a }
            if (r4 == 0) goto L_0x0443
            com.android.server.backup.UserBackupManagerService r4 = r10.backupManagerService     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            r17 = r6
            long r5 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            r4.enqueueFullBackup(r12, r5)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            goto L_0x0445
        L_0x0434:
            r0 = move-exception
            r3 = r1
            r6 = r31
            r1 = r0
            goto L_0x08a0
        L_0x043b:
            r0 = move-exception
            r3 = r1
            r2 = r13
            r6 = r31
            r1 = r0
            goto L_0x080b
        L_0x0443:
            r17 = r6
        L_0x0445:
            r4 = -1002(0xfffffffffffffc16, float:NaN)
            if (r3 != r4) goto L_0x0491
            android.app.backup.IBackupObserver r5 = r10.mBackupObserver     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            com.android.server.backup.utils.BackupObserverUtils.sendBackupOnPackageResult(r5, r12, r4)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            java.lang.String r4 = "PFTBT"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            r5.<init>()     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            java.lang.String r6 = "Transport rejected backup of "
            r5.append(r6)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            r5.append(r12)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            java.lang.String r6 = ", skipping"
            r5.append(r6)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            java.lang.String r5 = r5.toString()     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            android.util.Slog.i(r4, r5)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            r4 = 2841(0xb19, float:3.981E-42)
            r5 = 2
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            r6 = 0
            r5[r6] = r12     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            java.lang.String r6 = "transport rejected"
            r7 = 1
            r5[r7] = r6     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            android.util.EventLog.writeEvent(r4, r5)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            com.android.server.backup.fullbackup.PerformFullTransportBackupTask$SinglePackageBackupRunner r4 = r10.mBackupRunner     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            if (r4 == 0) goto L_0x048b
            com.android.server.backup.UserBackupManagerService r4 = r10.backupManagerService     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            r5 = r26
            android.content.pm.ApplicationInfo r6 = r5.applicationInfo     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            r4.tearDownAgentAndKill(r6)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            r6 = r31
            goto L_0x05f0
        L_0x048b:
            r5 = r26
            r6 = r31
            goto L_0x05f0
        L_0x0491:
            r5 = r26
            r7 = 1
            r4 = -1005(0xfffffffffffffc13, float:NaN)
            if (r3 != r4) goto L_0x04c3
            android.app.backup.IBackupObserver r6 = r10.mBackupObserver     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            com.android.server.backup.utils.BackupObserverUtils.sendBackupOnPackageResult(r6, r12, r4)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            java.lang.String r4 = "PFTBT"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            r6.<init>()     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            java.lang.String r9 = "Transport quota exceeded for package: "
            r6.append(r9)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            r6.append(r12)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            android.util.Slog.i(r4, r6)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            r4 = 2845(0xb1d, float:3.987E-42)
            android.util.EventLog.writeEvent(r4, r12)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            com.android.server.backup.UserBackupManagerService r4 = r10.backupManagerService     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            android.content.pm.ApplicationInfo r6 = r5.applicationInfo     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            r4.tearDownAgentAndKill(r6)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            r6 = r31
            goto L_0x05f0
        L_0x04c3:
            r4 = -1003(0xfffffffffffffc15, float:NaN)
            if (r3 != r4) goto L_0x04f2
            android.app.backup.IBackupObserver r6 = r10.mBackupObserver     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            com.android.server.backup.utils.BackupObserverUtils.sendBackupOnPackageResult(r6, r12, r4)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            java.lang.String r4 = "PFTBT"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            r6.<init>()     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            java.lang.String r9 = "Application failure for package: "
            r6.append(r9)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            r6.append(r12)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            android.util.Slog.w(r4, r6)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            r4 = 2823(0xb07, float:3.956E-42)
            android.util.EventLog.writeEvent(r4, r12)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            com.android.server.backup.UserBackupManagerService r4 = r10.backupManagerService     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            android.content.pm.ApplicationInfo r6 = r5.applicationInfo     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            r4.tearDownAgentAndKill(r6)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            r6 = r31
            goto L_0x05f0
        L_0x04f2:
            r4 = -2003(0xfffffffffffff82d, float:NaN)
            if (r3 != r4) goto L_0x052b
            android.app.backup.IBackupObserver r6 = r10.mBackupObserver     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            com.android.server.backup.utils.BackupObserverUtils.sendBackupOnPackageResult(r6, r12, r4)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            java.lang.String r4 = "PFTBT"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            r6.<init>()     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            java.lang.String r9 = "Backup cancelled. package="
            r6.append(r9)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            r6.append(r12)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            java.lang.String r9 = ", cancelAll="
            r6.append(r9)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            boolean r9 = r10.mCancelAll     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            r6.append(r9)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            android.util.Slog.w(r4, r6)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            r4 = 2846(0xb1e, float:3.988E-42)
            android.util.EventLog.writeEvent(r4, r12)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            com.android.server.backup.UserBackupManagerService r4 = r10.backupManagerService     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            android.content.pm.ApplicationInfo r6 = r5.applicationInfo     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            r4.tearDownAgentAndKill(r6)     // Catch:{ Exception -> 0x043b, all -> 0x0434 }
            r6 = r31
            goto L_0x05f0
        L_0x052b:
            if (r3 == 0) goto L_0x05de
            android.app.backup.IBackupObserver r4 = r10.mBackupObserver     // Catch:{ Exception -> 0x0641, all -> 0x063a }
            r6 = -1000(0xfffffffffffffc18, float:NaN)
            com.android.server.backup.utils.BackupObserverUtils.sendBackupOnPackageResult(r4, r12, r6)     // Catch:{ Exception -> 0x0641, all -> 0x063a }
            java.lang.String r4 = "PFTBT"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0641, all -> 0x063a }
            r6.<init>()     // Catch:{ Exception -> 0x0641, all -> 0x063a }
            java.lang.String r7 = "Transport failed; aborting backup: "
            r6.append(r7)     // Catch:{ Exception -> 0x0641, all -> 0x063a }
            r6.append(r3)     // Catch:{ Exception -> 0x0641, all -> 0x063a }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x0641, all -> 0x063a }
            android.util.Slog.w(r4, r6)     // Catch:{ Exception -> 0x0641, all -> 0x063a }
            r4 = 2842(0xb1a, float:3.982E-42)
            r6 = 0
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ Exception -> 0x0641, all -> 0x063a }
            android.util.EventLog.writeEvent(r4, r6)     // Catch:{ Exception -> 0x0641, all -> 0x063a }
            r11 = -1000(0xfffffffffffffc18, float:NaN)
            com.android.server.backup.UserBackupManagerService r4 = r10.backupManagerService     // Catch:{ Exception -> 0x05d4, all -> 0x05cb }
            android.content.pm.ApplicationInfo r6 = r5.applicationInfo     // Catch:{ Exception -> 0x05d4, all -> 0x05cb }
            r4.tearDownAgentAndKill(r6)     // Catch:{ Exception -> 0x05d4, all -> 0x05cb }
            boolean r4 = r10.mCancelAll
            if (r4 == 0) goto L_0x0563
            r11 = -2003(0xfffffffffffff82d, float:NaN)
            r4 = r11
            goto L_0x0564
        L_0x0563:
            r4 = r11
        L_0x0564:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "Full backup completed with status: "
            r6.append(r7)
            r6.append(r4)
            java.lang.String r6 = r6.toString()
            java.lang.String r7 = "PFTBT"
            android.util.Slog.i(r7, r6)
            android.app.backup.IBackupObserver r6 = r10.mBackupObserver
            com.android.server.backup.utils.BackupObserverUtils.sendBackupFinished(r6, r4)
            r10.cleanUpPipes(r13)
            r6 = r31
            r10.cleanUpPipes(r6)
            r34.unregisterTask()
            com.android.server.backup.FullBackupJob r7 = r10.mJob
            if (r7 == 0) goto L_0x0593
            int r9 = r10.mUserId
            r7.finishBackupPass(r9)
        L_0x0593:
            com.android.server.backup.UserBackupManagerService r7 = r10.backupManagerService
            java.lang.Object r7 = r7.getQueueLock()
            monitor-enter(r7)
            com.android.server.backup.UserBackupManagerService r9 = r10.backupManagerService     // Catch:{ all -> 0x05c7 }
            r11 = 0
            r9.setRunningFullBackupTask(r11)     // Catch:{ all -> 0x05c7 }
            monitor-exit(r7)     // Catch:{ all -> 0x05c7 }
            com.android.server.backup.internal.OnTaskFinishedListener r7 = r10.mListener
            java.lang.String r9 = "PFTBT.run()"
            r7.onFinished(r9)
            java.util.concurrent.CountDownLatch r7 = r10.mLatch
            r7.countDown()
            boolean r7 = r10.mUpdateSchedule
            if (r7 == 0) goto L_0x05b6
            com.android.server.backup.UserBackupManagerService r7 = r10.backupManagerService
            r7.scheduleNextFullBackupJob(r1)
        L_0x05b6:
            java.lang.String r7 = "PFTBT"
            java.lang.String r9 = "Full data backup pass finished."
            android.util.Slog.i(r7, r9)
            com.android.server.backup.UserBackupManagerService r7 = r10.backupManagerService
            android.os.PowerManager$WakeLock r7 = r7.getWakelock()
            r7.release()
            return
        L_0x05c7:
            r0 = move-exception
            r9 = r0
            monitor-exit(r7)     // Catch:{ all -> 0x05c7 }
            throw r9
        L_0x05cb:
            r0 = move-exception
            r6 = r31
            r3 = r1
            r27 = r11
            r1 = r0
            goto L_0x08a0
        L_0x05d4:
            r0 = move-exception
            r6 = r31
            r3 = r1
            r27 = r11
            r2 = r13
            r1 = r0
            goto L_0x080b
        L_0x05de:
            r6 = r31
            android.app.backup.IBackupObserver r4 = r10.mBackupObserver     // Catch:{ Exception -> 0x0634, all -> 0x062f }
            r9 = 0
            com.android.server.backup.utils.BackupObserverUtils.sendBackupOnPackageResult(r4, r12, r9)     // Catch:{ Exception -> 0x0634, all -> 0x062f }
            r4 = 2843(0xb1b, float:3.984E-42)
            android.util.EventLog.writeEvent(r4, r12)     // Catch:{ Exception -> 0x0634, all -> 0x062f }
            com.android.server.backup.UserBackupManagerService r4 = r10.backupManagerService     // Catch:{ Exception -> 0x0634, all -> 0x062f }
            r4.logBackupComplete(r12)     // Catch:{ Exception -> 0x0634, all -> 0x062f }
        L_0x05f0:
            r10.cleanUpPipes(r13)     // Catch:{ Exception -> 0x0634, all -> 0x062f }
            r10.cleanUpPipes(r6)     // Catch:{ Exception -> 0x0634, all -> 0x062f }
            android.content.pm.ApplicationInfo r4 = r5.applicationInfo     // Catch:{ Exception -> 0x0634, all -> 0x062f }
            if (r4 == 0) goto L_0x061d
            java.lang.String r4 = "PFTBT"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0634, all -> 0x062f }
            r9.<init>()     // Catch:{ Exception -> 0x0634, all -> 0x062f }
            java.lang.String r11 = "Unbinding agent in "
            r9.append(r11)     // Catch:{ Exception -> 0x0634, all -> 0x062f }
            r9.append(r12)     // Catch:{ Exception -> 0x0634, all -> 0x062f }
            java.lang.String r9 = r9.toString()     // Catch:{ Exception -> 0x0634, all -> 0x062f }
            android.util.Slog.i(r4, r9)     // Catch:{ Exception -> 0x0634, all -> 0x062f }
            com.android.server.backup.UserBackupManagerService r4 = r10.backupManagerService     // Catch:{ RemoteException -> 0x061c }
            android.app.IActivityManager r4 = r4.getActivityManager()     // Catch:{ RemoteException -> 0x061c }
            android.content.pm.ApplicationInfo r9 = r5.applicationInfo     // Catch:{ RemoteException -> 0x061c }
            r4.unbindBackupAgent(r9)     // Catch:{ RemoteException -> 0x061c }
            goto L_0x061d
        L_0x061c:
            r0 = move-exception
        L_0x061d:
            int r4 = r19 + 1
            r9 = r7
            r15 = r8
            r5 = r14
            r8 = r25
            r11 = r27
            r32 = r1
            r1 = r6
            r6 = r32
            r2 = r13
            r13 = 0
            goto L_0x00ca
        L_0x062f:
            r0 = move-exception
            r3 = r1
            r1 = r0
            goto L_0x08a0
        L_0x0634:
            r0 = move-exception
            r3 = r1
            r2 = r13
            r1 = r0
            goto L_0x080b
        L_0x063a:
            r0 = move-exception
            r6 = r31
            r3 = r1
            r1 = r0
            goto L_0x08a0
        L_0x0641:
            r0 = move-exception
            r6 = r31
            r3 = r1
            r2 = r13
            r1 = r0
            goto L_0x080b
        L_0x0649:
            r0 = move-exception
            r17 = r6
            r8 = r22
            r5 = r26
            r6 = r1
            r1 = r0
            goto L_0x0676
        L_0x0653:
            r0 = move-exception
            r23 = r6
            r25 = r8
            r16 = r9
            r27 = r11
            r28 = r14
            r8 = r15
            r14 = r5
            r5 = r3
            r6 = r1
            r1 = r0
            goto L_0x0676
        L_0x0664:
            r0 = move-exception
            r19 = r4
            r23 = r6
            r25 = r8
            r16 = r9
            r27 = r11
            r28 = r14
            r8 = r15
            r14 = r5
            r5 = r3
            r6 = r1
            r1 = r0
        L_0x0676:
            monitor-exit(r16)     // Catch:{ all -> 0x0685 }
            throw r1     // Catch:{ Exception -> 0x067e, all -> 0x0678 }
        L_0x0678:
            r0 = move-exception
            r1 = r0
            r3 = r23
            goto L_0x08a0
        L_0x067e:
            r0 = move-exception
            r1 = r0
            r2 = r13
            r3 = r23
            goto L_0x080b
        L_0x0685:
            r0 = move-exception
            r1 = r0
            goto L_0x0676
        L_0x0688:
            r0 = move-exception
            r23 = r6
            r27 = r11
            r6 = r1
            r3 = r23
            r1 = r0
            goto L_0x08a0
        L_0x0693:
            r0 = move-exception
            r23 = r6
            r27 = r11
            r6 = r1
            r2 = r13
            r3 = r23
            r1 = r0
            goto L_0x080b
        L_0x069f:
            r0 = move-exception
            r23 = r6
            r27 = r11
            r6 = r1
            r13 = r2
            r3 = r23
            r1 = r0
            goto L_0x08a0
        L_0x06ab:
            r0 = move-exception
            r23 = r6
            r27 = r11
            r6 = r1
            r3 = r23
            r1 = r0
            goto L_0x080b
        L_0x06b6:
            r19 = r4
            r14 = r5
            r23 = r6
            r25 = r8
            r27 = r11
            r8 = r15
            r13 = r2
        L_0x06c1:
            boolean r2 = r10.mCancelAll
            if (r2 == 0) goto L_0x06c8
            r11 = -2003(0xfffffffffffff82d, float:NaN)
            goto L_0x06ca
        L_0x06c8:
            r11 = r27
        L_0x06ca:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Full backup completed with status: "
            r2.append(r3)
            r2.append(r11)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "PFTBT"
            android.util.Slog.i(r3, r2)
            android.app.backup.IBackupObserver r2 = r10.mBackupObserver
            com.android.server.backup.utils.BackupObserverUtils.sendBackupFinished(r2, r11)
            r10.cleanUpPipes(r13)
            r10.cleanUpPipes(r1)
            r34.unregisterTask()
            com.android.server.backup.FullBackupJob r2 = r10.mJob
            if (r2 == 0) goto L_0x06f7
            int r3 = r10.mUserId
            r2.finishBackupPass(r3)
        L_0x06f7:
            com.android.server.backup.UserBackupManagerService r2 = r10.backupManagerService
            java.lang.Object r5 = r2.getQueueLock()
            monitor-enter(r5)
            com.android.server.backup.UserBackupManagerService r2 = r10.backupManagerService     // Catch:{ all -> 0x0733 }
            r3 = 0
            r2.setRunningFullBackupTask(r3)     // Catch:{ all -> 0x0733 }
            monitor-exit(r5)     // Catch:{ all -> 0x0733 }
            com.android.server.backup.internal.OnTaskFinishedListener r2 = r10.mListener
            java.lang.String r3 = "PFTBT.run()"
            r2.onFinished(r3)
            java.util.concurrent.CountDownLatch r2 = r10.mLatch
            r2.countDown()
            boolean r2 = r10.mUpdateSchedule
            if (r2 == 0) goto L_0x071d
            com.android.server.backup.UserBackupManagerService r2 = r10.backupManagerService
            r6 = r23
            r2.scheduleNextFullBackupJob(r6)
            goto L_0x071f
        L_0x071d:
            r6 = r23
        L_0x071f:
            java.lang.String r2 = "PFTBT"
            java.lang.String r3 = "Full data backup pass finished."
            android.util.Slog.i(r2, r3)
            com.android.server.backup.UserBackupManagerService r2 = r10.backupManagerService
            android.os.PowerManager$WakeLock r2 = r2.getWakelock()
            r2.release()
            r3 = r6
            r2 = r13
            goto L_0x0896
        L_0x0733:
            r0 = move-exception
            r6 = r23
            r2 = r0
        L_0x0737:
            monitor-exit(r5)     // Catch:{ all -> 0x0739 }
            throw r2
        L_0x0739:
            r0 = move-exception
            r2 = r0
            goto L_0x0737
        L_0x073c:
            r27 = r11
        L_0x073e:
            java.lang.String r5 = "PFTBT"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x07fa, all -> 0x07f4 }
            r6.<init>()     // Catch:{ Exception -> 0x07fa, all -> 0x07f4 }
            java.lang.String r7 = "full backup requested but enabled="
            r6.append(r7)     // Catch:{ Exception -> 0x07fa, all -> 0x07f4 }
            com.android.server.backup.UserBackupManagerService r7 = r10.backupManagerService     // Catch:{ Exception -> 0x07fa, all -> 0x07f4 }
            boolean r7 = r7.isEnabled()     // Catch:{ Exception -> 0x07fa, all -> 0x07f4 }
            r6.append(r7)     // Catch:{ Exception -> 0x07fa, all -> 0x07f4 }
            java.lang.String r7 = " setupComplete="
            r6.append(r7)     // Catch:{ Exception -> 0x07fa, all -> 0x07f4 }
            com.android.server.backup.UserBackupManagerService r7 = r10.backupManagerService     // Catch:{ Exception -> 0x07fa, all -> 0x07f4 }
            boolean r7 = r7.isSetupComplete()     // Catch:{ Exception -> 0x07fa, all -> 0x07f4 }
            r6.append(r7)     // Catch:{ Exception -> 0x07fa, all -> 0x07f4 }
            java.lang.String r7 = "; ignoring"
            r6.append(r7)     // Catch:{ Exception -> 0x07fa, all -> 0x07f4 }
            java.lang.String r6 = r6.toString()     // Catch:{ Exception -> 0x07fa, all -> 0x07f4 }
            android.util.Slog.i(r5, r6)     // Catch:{ Exception -> 0x07fa, all -> 0x07f4 }
            com.android.server.backup.UserBackupManagerService r5 = r10.backupManagerService     // Catch:{ Exception -> 0x07fa, all -> 0x07f4 }
            boolean r5 = r5.isSetupComplete()     // Catch:{ Exception -> 0x07fa, all -> 0x07f4 }
            if (r5 == 0) goto L_0x0778
            r5 = 13
            goto L_0x077a
        L_0x0778:
            r5 = 14
        L_0x077a:
            android.app.backup.IBackupManagerMonitor r6 = r10.mMonitor     // Catch:{ Exception -> 0x07fa, all -> 0x07f4 }
            r7 = 3
            r8 = 0
            android.app.backup.IBackupManagerMonitor r6 = com.android.server.backup.utils.BackupManagerMonitorUtils.monitorEvent(r6, r5, r8, r7, r8)     // Catch:{ Exception -> 0x07fa, all -> 0x07f4 }
            r10.mMonitor = r6     // Catch:{ Exception -> 0x07fa, all -> 0x07f4 }
            r6 = 0
            r10.mUpdateSchedule = r6     // Catch:{ Exception -> 0x07fa, all -> 0x07f4 }
            r6 = -2001(0xfffffffffffff82f, float:NaN)
            boolean r7 = r10.mCancelAll
            if (r7 == 0) goto L_0x078f
            r6 = -2003(0xfffffffffffff82d, float:NaN)
        L_0x078f:
            java.lang.StringBuilder r7 = new java.lang.StringBuilder
            r7.<init>()
            java.lang.String r8 = "Full backup completed with status: "
            r7.append(r8)
            r7.append(r6)
            java.lang.String r7 = r7.toString()
            java.lang.String r8 = "PFTBT"
            android.util.Slog.i(r8, r7)
            android.app.backup.IBackupObserver r7 = r10.mBackupObserver
            com.android.server.backup.utils.BackupObserverUtils.sendBackupFinished(r7, r6)
            r10.cleanUpPipes(r2)
            r10.cleanUpPipes(r1)
            r34.unregisterTask()
            com.android.server.backup.FullBackupJob r7 = r10.mJob
            if (r7 == 0) goto L_0x07bc
            int r8 = r10.mUserId
            r7.finishBackupPass(r8)
        L_0x07bc:
            com.android.server.backup.UserBackupManagerService r7 = r10.backupManagerService
            java.lang.Object r7 = r7.getQueueLock()
            monitor-enter(r7)
            com.android.server.backup.UserBackupManagerService r8 = r10.backupManagerService     // Catch:{ all -> 0x07f0 }
            r9 = 0
            r8.setRunningFullBackupTask(r9)     // Catch:{ all -> 0x07f0 }
            monitor-exit(r7)     // Catch:{ all -> 0x07f0 }
            com.android.server.backup.internal.OnTaskFinishedListener r7 = r10.mListener
            java.lang.String r8 = "PFTBT.run()"
            r7.onFinished(r8)
            java.util.concurrent.CountDownLatch r7 = r10.mLatch
            r7.countDown()
            boolean r7 = r10.mUpdateSchedule
            if (r7 == 0) goto L_0x07df
            com.android.server.backup.UserBackupManagerService r7 = r10.backupManagerService
            r7.scheduleNextFullBackupJob(r3)
        L_0x07df:
            java.lang.String r7 = "PFTBT"
            java.lang.String r8 = "Full data backup pass finished."
            android.util.Slog.i(r7, r8)
            com.android.server.backup.UserBackupManagerService r7 = r10.backupManagerService
            android.os.PowerManager$WakeLock r7 = r7.getWakelock()
            r7.release()
            return
        L_0x07f0:
            r0 = move-exception
            r8 = r0
            monitor-exit(r7)     // Catch:{ all -> 0x07f0 }
            throw r8
        L_0x07f4:
            r0 = move-exception
            r6 = r1
            r13 = r2
            r1 = r0
            goto L_0x08a0
        L_0x07fa:
            r0 = move-exception
            r6 = r1
            r1 = r0
            goto L_0x080b
        L_0x07fe:
            r0 = move-exception
            r27 = r11
            r6 = r1
            r13 = r2
            r1 = r0
            goto L_0x08a0
        L_0x0806:
            r0 = move-exception
            r27 = r11
            r6 = r1
            r1 = r0
        L_0x080b:
            r11 = -1000(0xfffffffffffffc18, float:NaN)
            java.lang.String r5 = "PFTBT"
            java.lang.String r7 = "Exception trying full transport backup"
            android.util.Slog.w(r5, r7, r1)     // Catch:{ all -> 0x089b }
            android.app.backup.IBackupManagerMonitor r5 = r10.mMonitor     // Catch:{ all -> 0x089b }
            r7 = 19
            android.content.pm.PackageInfo r8 = r10.mCurrentPackage     // Catch:{ all -> 0x089b }
            java.lang.String r9 = "android.app.backup.extra.LOG_EXCEPTION_FULL_BACKUP"
            java.lang.String r12 = android.util.Log.getStackTraceString(r1)     // Catch:{ all -> 0x089b }
            r13 = 0
            android.os.Bundle r9 = com.android.server.backup.utils.BackupManagerMonitorUtils.putMonitoringExtra((android.os.Bundle) r13, (java.lang.String) r9, (java.lang.String) r12)     // Catch:{ all -> 0x089b }
            r12 = 3
            android.app.backup.IBackupManagerMonitor r5 = com.android.server.backup.utils.BackupManagerMonitorUtils.monitorEvent(r5, r7, r8, r12, r9)     // Catch:{ all -> 0x089b }
            r10.mMonitor = r5     // Catch:{ all -> 0x089b }
            boolean r1 = r10.mCancelAll
            if (r1 == 0) goto L_0x0833
            r1 = -2003(0xfffffffffffff82d, float:NaN)
            goto L_0x0834
        L_0x0833:
            r1 = r11
        L_0x0834:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r7 = "Full backup completed with status: "
            r5.append(r7)
            r5.append(r1)
            java.lang.String r5 = r5.toString()
            java.lang.String r7 = "PFTBT"
            android.util.Slog.i(r7, r5)
            android.app.backup.IBackupObserver r5 = r10.mBackupObserver
            com.android.server.backup.utils.BackupObserverUtils.sendBackupFinished(r5, r1)
            r10.cleanUpPipes(r2)
            r10.cleanUpPipes(r6)
            r34.unregisterTask()
            com.android.server.backup.FullBackupJob r5 = r10.mJob
            if (r5 == 0) goto L_0x0861
            int r7 = r10.mUserId
            r5.finishBackupPass(r7)
        L_0x0861:
            com.android.server.backup.UserBackupManagerService r5 = r10.backupManagerService
            java.lang.Object r5 = r5.getQueueLock()
            monitor-enter(r5)
            com.android.server.backup.UserBackupManagerService r7 = r10.backupManagerService     // Catch:{ all -> 0x0897 }
            r8 = 0
            r7.setRunningFullBackupTask(r8)     // Catch:{ all -> 0x0897 }
            monitor-exit(r5)     // Catch:{ all -> 0x0897 }
            com.android.server.backup.internal.OnTaskFinishedListener r5 = r10.mListener
            java.lang.String r7 = "PFTBT.run()"
            r5.onFinished(r7)
            java.util.concurrent.CountDownLatch r5 = r10.mLatch
            r5.countDown()
            boolean r5 = r10.mUpdateSchedule
            if (r5 == 0) goto L_0x0884
            com.android.server.backup.UserBackupManagerService r5 = r10.backupManagerService
            r5.scheduleNextFullBackupJob(r3)
        L_0x0884:
            java.lang.String r5 = "PFTBT"
            java.lang.String r7 = "Full data backup pass finished."
            android.util.Slog.i(r5, r7)
            com.android.server.backup.UserBackupManagerService r5 = r10.backupManagerService
            android.os.PowerManager$WakeLock r5 = r5.getWakelock()
            r5.release()
            r11 = r1
            r1 = r6
        L_0x0896:
            return
        L_0x0897:
            r0 = move-exception
            r7 = r0
            monitor-exit(r5)     // Catch:{ all -> 0x0897 }
            throw r7
        L_0x089b:
            r0 = move-exception
            r1 = r0
            r13 = r2
            r27 = r11
        L_0x08a0:
            boolean r2 = r10.mCancelAll
            if (r2 == 0) goto L_0x08a9
            r27 = -2003(0xfffffffffffff82d, float:NaN)
            r2 = r27
            goto L_0x08ab
        L_0x08a9:
            r2 = r27
        L_0x08ab:
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r7 = "Full backup completed with status: "
            r5.append(r7)
            r5.append(r2)
            java.lang.String r5 = r5.toString()
            java.lang.String r7 = "PFTBT"
            android.util.Slog.i(r7, r5)
            android.app.backup.IBackupObserver r5 = r10.mBackupObserver
            com.android.server.backup.utils.BackupObserverUtils.sendBackupFinished(r5, r2)
            r10.cleanUpPipes(r13)
            r10.cleanUpPipes(r6)
            r34.unregisterTask()
            com.android.server.backup.FullBackupJob r5 = r10.mJob
            if (r5 == 0) goto L_0x08d8
            int r7 = r10.mUserId
            r5.finishBackupPass(r7)
        L_0x08d8:
            com.android.server.backup.UserBackupManagerService r5 = r10.backupManagerService
            java.lang.Object r5 = r5.getQueueLock()
            monitor-enter(r5)
            com.android.server.backup.UserBackupManagerService r7 = r10.backupManagerService     // Catch:{ all -> 0x090c }
            r8 = 0
            r7.setRunningFullBackupTask(r8)     // Catch:{ all -> 0x090c }
            monitor-exit(r5)     // Catch:{ all -> 0x090c }
            com.android.server.backup.internal.OnTaskFinishedListener r5 = r10.mListener
            java.lang.String r7 = "PFTBT.run()"
            r5.onFinished(r7)
            java.util.concurrent.CountDownLatch r5 = r10.mLatch
            r5.countDown()
            boolean r5 = r10.mUpdateSchedule
            if (r5 == 0) goto L_0x08fb
            com.android.server.backup.UserBackupManagerService r5 = r10.backupManagerService
            r5.scheduleNextFullBackupJob(r3)
        L_0x08fb:
            java.lang.String r5 = "PFTBT"
            java.lang.String r7 = "Full data backup pass finished."
            android.util.Slog.i(r5, r7)
            com.android.server.backup.UserBackupManagerService r5 = r10.backupManagerService
            android.os.PowerManager$WakeLock r5 = r5.getWakelock()
            r5.release()
            throw r1
        L_0x090c:
            r0 = move-exception
            r1 = r0
            monitor-exit(r5)     // Catch:{ all -> 0x090c }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.fullbackup.PerformFullTransportBackupTask.run():void");
    }

    /* access modifiers changed from: package-private */
    public void cleanUpPipes(ParcelFileDescriptor[] pipes) {
        if (pipes != null) {
            if (pipes[0] != null) {
                ParcelFileDescriptor fd = pipes[0];
                pipes[0] = null;
                try {
                    fd.close();
                } catch (IOException e) {
                    Slog.w(TAG, "Unable to close pipe!");
                }
            }
            if (pipes[1] != null) {
                ParcelFileDescriptor fd2 = pipes[1];
                pipes[1] = null;
                try {
                    fd2.close();
                } catch (IOException e2) {
                    Slog.w(TAG, "Unable to close pipe!");
                }
            }
        }
    }

    class SinglePackageBackupPreflight implements BackupRestoreTask, FullBackupPreflight {
        private final int mCurrentOpToken;
        final CountDownLatch mLatch = new CountDownLatch(1);
        final long mQuota;
        final AtomicLong mResult = new AtomicLong(-1003);
        final TransportClient mTransportClient;
        private final int mTransportFlags;

        SinglePackageBackupPreflight(TransportClient transportClient, long quota, int currentOpToken, int transportFlags) {
            this.mTransportClient = transportClient;
            this.mQuota = quota;
            this.mCurrentOpToken = currentOpToken;
            this.mTransportFlags = transportFlags;
        }

        public int preflightFullBackup(PackageInfo pkg, IBackupAgent agent) {
            long fullBackupAgentTimeoutMillis = PerformFullTransportBackupTask.this.mAgentTimeoutParameters.getFullBackupAgentTimeoutMillis();
            try {
                PerformFullTransportBackupTask.this.backupManagerService.prepareOperationTimeout(this.mCurrentOpToken, fullBackupAgentTimeoutMillis, this, 0);
                Slog.d(PerformFullTransportBackupTask.TAG, "Preflighting full payload of " + pkg.packageName);
                agent.doMeasureFullBackup(this.mQuota, this.mCurrentOpToken, PerformFullTransportBackupTask.this.backupManagerService.getBackupManagerBinder(), this.mTransportFlags);
                this.mLatch.await(fullBackupAgentTimeoutMillis, TimeUnit.MILLISECONDS);
                long totalSize = this.mResult.get();
                if (totalSize < 0) {
                    return (int) totalSize;
                }
                Slog.v(PerformFullTransportBackupTask.TAG, "Got preflight response; size=" + totalSize);
                int result = this.mTransportClient.connectOrThrow("PFTBT$SPBP.preflightFullBackup()").checkFullBackupSize(totalSize);
                if (result != -1005) {
                    return result;
                }
                Slog.d(PerformFullTransportBackupTask.TAG, "Package hit quota limit on preflight " + pkg.packageName + ": " + totalSize + " of " + this.mQuota);
                RemoteCall.execute(new RemoteCallable(agent, totalSize) {
                    private final /* synthetic */ IBackupAgent f$1;
                    private final /* synthetic */ long f$2;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                    }

                    public final void call(Object obj) {
                        PerformFullTransportBackupTask.SinglePackageBackupPreflight.this.lambda$preflightFullBackup$0$PerformFullTransportBackupTask$SinglePackageBackupPreflight(this.f$1, this.f$2, (IBackupCallback) obj);
                    }
                }, PerformFullTransportBackupTask.this.mAgentTimeoutParameters.getQuotaExceededTimeoutMillis());
                return result;
            } catch (Exception e) {
                Slog.w(PerformFullTransportBackupTask.TAG, "Exception preflighting " + pkg.packageName + ": " + e.getMessage());
                return -1003;
            }
        }

        public /* synthetic */ void lambda$preflightFullBackup$0$PerformFullTransportBackupTask$SinglePackageBackupPreflight(IBackupAgent agent, long totalSize, IBackupCallback callback) throws RemoteException {
            agent.doQuotaExceeded(totalSize, this.mQuota, callback);
        }

        public void execute() {
        }

        public void operationComplete(long result) {
            Slog.i(PerformFullTransportBackupTask.TAG, "Preflight op complete, result=" + result);
            this.mResult.set(result);
            this.mLatch.countDown();
            PerformFullTransportBackupTask.this.backupManagerService.removeOperation(this.mCurrentOpToken);
        }

        public void handleCancel(boolean cancelAll) {
            Slog.i(PerformFullTransportBackupTask.TAG, "Preflight cancelled; failing");
            this.mResult.set(-1003);
            this.mLatch.countDown();
            PerformFullTransportBackupTask.this.backupManagerService.removeOperation(this.mCurrentOpToken);
        }

        public long getExpectedSizeOrErrorCode() {
            try {
                this.mLatch.await(PerformFullTransportBackupTask.this.mAgentTimeoutParameters.getFullBackupAgentTimeoutMillis(), TimeUnit.MILLISECONDS);
                return this.mResult.get();
            } catch (InterruptedException e) {
                return -1;
            }
        }
    }

    class SinglePackageBackupRunner implements Runnable, BackupRestoreTask {
        final CountDownLatch mBackupLatch = new CountDownLatch(1);
        private volatile int mBackupResult = -1003;
        private final int mCurrentOpToken;
        private FullBackupEngine mEngine;
        private final int mEphemeralToken;
        private volatile boolean mIsCancelled;
        final ParcelFileDescriptor mOutput;
        final SinglePackageBackupPreflight mPreflight;
        final CountDownLatch mPreflightLatch = new CountDownLatch(1);
        private volatile int mPreflightResult = -1003;
        private final long mQuota;
        final PackageInfo mTarget;
        private final int mTransportFlags;
        final /* synthetic */ PerformFullTransportBackupTask this$0;

        SinglePackageBackupRunner(PerformFullTransportBackupTask this$02, ParcelFileDescriptor output, PackageInfo target, TransportClient transportClient, long quota, int currentOpToken, int transportFlags) throws IOException {
            this.this$0 = this$02;
            this.mOutput = ParcelFileDescriptor.dup(output.getFileDescriptor());
            this.mTarget = target;
            this.mCurrentOpToken = currentOpToken;
            this.mEphemeralToken = this$02.backupManagerService.generateRandomIntegerToken();
            this.mPreflight = new SinglePackageBackupPreflight(transportClient, quota, this.mEphemeralToken, transportFlags);
            this.mQuota = quota;
            this.mTransportFlags = transportFlags;
            registerTask();
        }

        /* access modifiers changed from: package-private */
        public void registerTask() {
            synchronized (this.this$0.backupManagerService.getCurrentOpLock()) {
                this.this$0.backupManagerService.getCurrentOperations().put(this.mCurrentOpToken, new Operation(0, this, 0));
            }
        }

        /* access modifiers changed from: package-private */
        public void unregisterTask() {
            synchronized (this.this$0.backupManagerService.getCurrentOpLock()) {
                this.this$0.backupManagerService.getCurrentOperations().remove(this.mCurrentOpToken);
            }
        }

        /* Debug info: failed to restart local var, previous not found, register: 14 */
        public void run() {
            this.mEngine = new FullBackupEngine(this.this$0.backupManagerService, new FileOutputStream(this.mOutput.getFileDescriptor()), this.mPreflight, this.mTarget, false, this, this.mQuota, this.mCurrentOpToken, this.mTransportFlags);
            try {
                if (!this.mIsCancelled) {
                    this.mPreflightResult = this.mEngine.preflightCheck();
                }
                this.mPreflightLatch.countDown();
                if (this.mPreflightResult == 0 && !this.mIsCancelled) {
                    this.mBackupResult = this.mEngine.backupOnePackage();
                }
                unregisterTask();
                this.mBackupLatch.countDown();
                try {
                    this.mOutput.close();
                } catch (IOException e) {
                }
            } catch (Exception e2) {
                try {
                    Slog.e(PerformFullTransportBackupTask.TAG, "Exception during full package backup of " + this.mTarget.packageName);
                } finally {
                    unregisterTask();
                    this.mBackupLatch.countDown();
                    try {
                        this.mOutput.close();
                    } catch (IOException e3) {
                        Slog.w(PerformFullTransportBackupTask.TAG, "Error closing transport pipe in runner");
                    }
                }
            } catch (Throwable th) {
                this.mPreflightLatch.countDown();
                throw th;
            }
        }

        public void sendQuotaExceeded(long backupDataBytes, long quotaBytes) {
            this.mEngine.sendQuotaExceeded(backupDataBytes, quotaBytes);
        }

        /* access modifiers changed from: package-private */
        public long getPreflightResultBlocking() {
            try {
                this.mPreflightLatch.await(this.this$0.mAgentTimeoutParameters.getFullBackupAgentTimeoutMillis(), TimeUnit.MILLISECONDS);
                if (this.mIsCancelled) {
                    return -2003;
                }
                if (this.mPreflightResult == 0) {
                    return this.mPreflight.getExpectedSizeOrErrorCode();
                }
                return (long) this.mPreflightResult;
            } catch (InterruptedException e) {
                return -1003;
            }
        }

        /* access modifiers changed from: package-private */
        public int getBackupResultBlocking() {
            try {
                this.mBackupLatch.await(this.this$0.mAgentTimeoutParameters.getFullBackupAgentTimeoutMillis(), TimeUnit.MILLISECONDS);
                if (this.mIsCancelled) {
                    return -2003;
                }
                return this.mBackupResult;
            } catch (InterruptedException e) {
                return -1003;
            }
        }

        public void execute() {
        }

        public void operationComplete(long result) {
        }

        public void handleCancel(boolean cancelAll) {
            Slog.w(PerformFullTransportBackupTask.TAG, "Full backup cancel of " + this.mTarget.packageName);
            PerformFullTransportBackupTask performFullTransportBackupTask = this.this$0;
            IBackupManagerMonitor unused = performFullTransportBackupTask.mMonitor = BackupManagerMonitorUtils.monitorEvent(performFullTransportBackupTask.mMonitor, 4, this.this$0.mCurrentPackage, 2, (Bundle) null);
            this.mIsCancelled = true;
            this.this$0.backupManagerService.handleCancel(this.mEphemeralToken, cancelAll);
            this.this$0.backupManagerService.tearDownAgentAndKill(this.mTarget.applicationInfo);
            this.mPreflightLatch.countDown();
            this.mBackupLatch.countDown();
            this.this$0.backupManagerService.removeOperation(this.mCurrentOpToken);
        }
    }
}
