package com.android.server.backup.internal;

import android.os.Handler;
import android.os.Looper;
import com.android.internal.util.Preconditions;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.backup.UserBackupManagerService;

public class BackupHandler extends Handler {
    public static final int MSG_BACKUP_OPERATION_TIMEOUT = 17;
    public static final int MSG_BACKUP_RESTORE_STEP = 20;
    public static final int MSG_FULL_CONFIRMATION_TIMEOUT = 9;
    public static final int MSG_OP_COMPLETE = 21;
    public static final int MSG_REQUEST_BACKUP = 15;
    public static final int MSG_RESTORE_OPERATION_TIMEOUT = 18;
    public static final int MSG_RESTORE_SESSION_TIMEOUT = 8;
    public static final int MSG_RETRY_CLEAR = 12;
    public static final int MSG_RETRY_INIT = 11;
    public static final int MSG_RUN_ADB_BACKUP = 2;
    public static final int MSG_RUN_ADB_RESTORE = 10;
    public static final int MSG_RUN_BACKUP = 1;
    public static final int MSG_RUN_CLEAR = 4;
    public static final int MSG_RUN_FULL_TRANSPORT_BACKUP = 14;
    public static final int MSG_RUN_GET_RESTORE_SETS = 6;
    public static final int MSG_RUN_RESTORE = 3;
    public static final int MSG_SCHEDULE_BACKUP_PACKAGE = 16;
    public static final int MSG_WIDGET_BROADCAST = 13;
    private final UserBackupManagerService backupManagerService;
    private final BackupAgentTimeoutParameters mAgentTimeoutParameters;

    public BackupHandler(UserBackupManagerService backupManagerService2, Looper looper) {
        super(looper);
        this.backupManagerService = backupManagerService2;
        this.mAgentTimeoutParameters = (BackupAgentTimeoutParameters) Preconditions.checkNotNull(backupManagerService2.getAgentTimeoutParameters(), "Timeout parameters cannot be null");
    }

    /* Debug info: failed to restart local var, previous not found, register: 22 */
    /* JADX WARNING: Code restructure failed: missing block: B:143:0x0453, code lost:
        r19 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:144:0x0459, code lost:
        if (r15.size() <= 0) goto L_0x048a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:147:0x046c, code lost:
        r20 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:149:?, code lost:
        com.android.server.backup.keyvalue.KeyValueBackupTask.start(r1.backupManagerService, r4, r17.transportDirName(), r15, r18, (android.app.backup.IBackupObserver) null, (android.app.backup.IBackupManagerMonitor) null, new com.android.server.backup.internal.$$Lambda$BackupHandler$TJcRazGYTaUxjeiX6mPLlipfZUI(r3, r4), java.util.Collections.emptyList(), false, false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:150:0x047b, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:151:0x047d, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:152:0x047e, code lost:
        r20 = r15;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:153:0x0480, code lost:
        android.util.Slog.e(com.android.server.backup.BackupManagerService.TAG, "Transport became unavailable attempting backup or error initializing backup task", r0);
        r19 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:154:0x048a, code lost:
        r20 = r15;
        android.util.Slog.v(com.android.server.backup.BackupManagerService.TAG, "Backup requested but nothing pending");
        r19 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:155:0x0495, code lost:
        if (r19 == false) goto L_0x0497;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:156:0x0497, code lost:
        r3.disposeOfTransportClient(r4, "BH/MSG_RUN_BACKUP");
     */
    /* JADX WARNING: Code restructure failed: missing block: B:157:0x04a0, code lost:
        monitor-enter(r1.backupManagerService.getQueueLock());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:159:?, code lost:
        r1.backupManagerService.setBackupRunning(false);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:161:0x04a7, code lost:
        r1.backupManagerService.getWakelock().release();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:199:?, code lost:
        return;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:200:?, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void handleMessage(android.os.Message r23) {
        /*
            r22 = this;
            r1 = r22
            r2 = r23
            com.android.server.backup.UserBackupManagerService r0 = r1.backupManagerService
            com.android.server.backup.TransportManager r3 = r0.getTransportManager()
            int r0 = r2.what
            r4 = 1
            r5 = 0
            switch(r0) {
                case 1: goto L_0x03ad;
                case 2: goto L_0x0368;
                case 3: goto L_0x02ee;
                case 4: goto L_0x02d8;
                case 5: goto L_0x0011;
                case 6: goto L_0x01f1;
                case 7: goto L_0x0011;
                case 8: goto L_0x01b7;
                case 9: goto L_0x015e;
                case 10: goto L_0x013c;
                case 11: goto L_0x0011;
                case 12: goto L_0x012d;
                case 13: goto L_0x011c;
                case 14: goto L_0x010b;
                case 15: goto L_0x00c8;
                case 16: goto L_0x00a7;
                case 17: goto L_0x0082;
                case 18: goto L_0x0082;
                case 19: goto L_0x0011;
                case 20: goto L_0x0043;
                case 21: goto L_0x0013;
                default: goto L_0x0011;
            }
        L_0x0011:
            goto L_0x04bb
        L_0x0013:
            java.lang.Object r0 = r2.obj     // Catch:{ ClassCastException -> 0x0028 }
            android.util.Pair r0 = (android.util.Pair) r0     // Catch:{ ClassCastException -> 0x0028 }
            java.lang.Object r4 = r0.first     // Catch:{ ClassCastException -> 0x0028 }
            com.android.server.backup.BackupRestoreTask r4 = (com.android.server.backup.BackupRestoreTask) r4     // Catch:{ ClassCastException -> 0x0028 }
            java.lang.Object r5 = r0.second     // Catch:{ ClassCastException -> 0x0028 }
            java.lang.Long r5 = (java.lang.Long) r5     // Catch:{ ClassCastException -> 0x0028 }
            long r5 = r5.longValue()     // Catch:{ ClassCastException -> 0x0028 }
            r4.operationComplete(r5)     // Catch:{ ClassCastException -> 0x0028 }
            goto L_0x04bb
        L_0x0028:
            r0 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Invalid completion in flight, obj="
            r4.append(r5)
            java.lang.Object r5 = r2.obj
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            java.lang.String r5 = "BackupManagerService"
            android.util.Slog.e(r5, r4)
            goto L_0x04bb
        L_0x0043:
            java.lang.Object r0 = r2.obj     // Catch:{ ClassCastException -> 0x0067 }
            com.android.server.backup.BackupRestoreTask r0 = (com.android.server.backup.BackupRestoreTask) r0     // Catch:{ ClassCastException -> 0x0067 }
            java.lang.String r4 = "BackupManagerService"
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ ClassCastException -> 0x0067 }
            r5.<init>()     // Catch:{ ClassCastException -> 0x0067 }
            java.lang.String r6 = "Got next step for "
            r5.append(r6)     // Catch:{ ClassCastException -> 0x0067 }
            r5.append(r0)     // Catch:{ ClassCastException -> 0x0067 }
            java.lang.String r6 = ", executing"
            r5.append(r6)     // Catch:{ ClassCastException -> 0x0067 }
            java.lang.String r5 = r5.toString()     // Catch:{ ClassCastException -> 0x0067 }
            android.util.Slog.v(r4, r5)     // Catch:{ ClassCastException -> 0x0067 }
            r0.execute()     // Catch:{ ClassCastException -> 0x0067 }
            goto L_0x04bb
        L_0x0067:
            r0 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Invalid backup/restore task in flight, obj="
            r4.append(r5)
            java.lang.Object r5 = r2.obj
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            java.lang.String r5 = "BackupManagerService"
            android.util.Slog.e(r5, r4)
            goto L_0x04bb
        L_0x0082:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r4 = "Timeout message received for token="
            r0.append(r4)
            int r4 = r2.arg1
            java.lang.String r4 = java.lang.Integer.toHexString(r4)
            r0.append(r4)
            java.lang.String r0 = r0.toString()
            java.lang.String r4 = "BackupManagerService"
            android.util.Slog.d(r4, r0)
            com.android.server.backup.UserBackupManagerService r0 = r1.backupManagerService
            int r4 = r2.arg1
            r0.handleCancel(r4, r5)
            goto L_0x04bb
        L_0x00a7:
            java.lang.Object r0 = r2.obj
            java.lang.String r0 = (java.lang.String) r0
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "MSG_SCHEDULE_BACKUP_PACKAGE "
            r4.append(r5)
            r4.append(r0)
            java.lang.String r4 = r4.toString()
            java.lang.String r5 = "BackupManagerService"
            android.util.Slog.d(r5, r4)
            com.android.server.backup.UserBackupManagerService r4 = r1.backupManagerService
            r4.dataChangedImpl(r0)
            goto L_0x04bb
        L_0x00c8:
            java.lang.Object r0 = r2.obj
            com.android.server.backup.params.BackupParams r0 = (com.android.server.backup.params.BackupParams) r0
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "MSG_REQUEST_BACKUP observer="
            r5.append(r6)
            android.app.backup.IBackupObserver r6 = r0.observer
            r5.append(r6)
            java.lang.String r5 = r5.toString()
            java.lang.String r6 = "BackupManagerService"
            android.util.Slog.d(r6, r5)
            com.android.server.backup.UserBackupManagerService r5 = r1.backupManagerService
            r5.setBackupRunning(r4)
            com.android.server.backup.UserBackupManagerService r4 = r1.backupManagerService
            android.os.PowerManager$WakeLock r4 = r4.getWakelock()
            r4.acquire()
            com.android.server.backup.UserBackupManagerService r5 = r1.backupManagerService
            com.android.server.backup.transport.TransportClient r6 = r0.transportClient
            java.lang.String r7 = r0.dirName
            java.util.ArrayList<java.lang.String> r8 = r0.kvPackages
            r9 = 0
            android.app.backup.IBackupObserver r10 = r0.observer
            android.app.backup.IBackupManagerMonitor r11 = r0.monitor
            com.android.server.backup.internal.OnTaskFinishedListener r12 = r0.listener
            java.util.ArrayList<java.lang.String> r13 = r0.fullPackages
            r14 = 1
            boolean r15 = r0.nonIncrementalBackup
            com.android.server.backup.keyvalue.KeyValueBackupTask.start(r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15)
            goto L_0x04bb
        L_0x010b:
            java.lang.Object r0 = r2.obj
            com.android.server.backup.fullbackup.PerformFullTransportBackupTask r0 = (com.android.server.backup.fullbackup.PerformFullTransportBackupTask) r0
            java.lang.Thread r4 = new java.lang.Thread
            java.lang.String r5 = "transport-backup"
            r4.<init>(r0, r5)
            r4.start()
            goto L_0x04bb
        L_0x011c:
            java.lang.Object r0 = r2.obj
            android.content.Intent r0 = (android.content.Intent) r0
            com.android.server.backup.UserBackupManagerService r4 = r1.backupManagerService
            android.content.Context r4 = r4.getContext()
            android.os.UserHandle r5 = android.os.UserHandle.SYSTEM
            r4.sendBroadcastAsUser(r0, r5)
            goto L_0x04bb
        L_0x012d:
            java.lang.Object r0 = r2.obj
            com.android.server.backup.params.ClearRetryParams r0 = (com.android.server.backup.params.ClearRetryParams) r0
            com.android.server.backup.UserBackupManagerService r4 = r1.backupManagerService
            java.lang.String r5 = r0.transportName
            java.lang.String r6 = r0.packageName
            r4.clearBackupData(r5, r6)
            goto L_0x04bb
        L_0x013c:
            java.lang.Object r0 = r2.obj
            com.android.server.backup.params.AdbRestoreParams r0 = (com.android.server.backup.params.AdbRestoreParams) r0
            com.android.server.backup.restore.PerformAdbRestoreTask r11 = new com.android.server.backup.restore.PerformAdbRestoreTask
            com.android.server.backup.UserBackupManagerService r5 = r1.backupManagerService
            android.os.ParcelFileDescriptor r6 = r0.fd
            java.lang.String r7 = r0.curPassword
            java.lang.String r8 = r0.encryptPassword
            android.app.backup.IFullBackupRestoreObserver r9 = r0.observer
            java.util.concurrent.atomic.AtomicBoolean r10 = r0.latch
            r4 = r11
            r4.<init>(r5, r6, r7, r8, r9, r10)
            java.lang.Thread r5 = new java.lang.Thread
            java.lang.String r6 = "adb-restore"
            r5.<init>(r4, r6)
            r5.start()
            goto L_0x04bb
        L_0x015e:
            com.android.server.backup.UserBackupManagerService r0 = r1.backupManagerService
            android.util.SparseArray r4 = r0.getAdbBackupRestoreConfirmations()
            monitor-enter(r4)
            com.android.server.backup.UserBackupManagerService r0 = r1.backupManagerService     // Catch:{ all -> 0x01b4 }
            android.util.SparseArray r0 = r0.getAdbBackupRestoreConfirmations()     // Catch:{ all -> 0x01b4 }
            int r5 = r2.arg1     // Catch:{ all -> 0x01b4 }
            java.lang.Object r0 = r0.get(r5)     // Catch:{ all -> 0x01b4 }
            com.android.server.backup.params.AdbParams r0 = (com.android.server.backup.params.AdbParams) r0     // Catch:{ all -> 0x01b4 }
            r5 = r0
            if (r5 == 0) goto L_0x0199
            java.lang.String r0 = "BackupManagerService"
            java.lang.String r6 = "Full backup/restore timed out waiting for user confirmation"
            android.util.Slog.i(r0, r6)     // Catch:{ all -> 0x01b4 }
            com.android.server.backup.UserBackupManagerService r0 = r1.backupManagerService     // Catch:{ all -> 0x01b4 }
            r0.signalAdbBackupRestoreCompletion(r5)     // Catch:{ all -> 0x01b4 }
            com.android.server.backup.UserBackupManagerService r0 = r1.backupManagerService     // Catch:{ all -> 0x01b4 }
            android.util.SparseArray r0 = r0.getAdbBackupRestoreConfirmations()     // Catch:{ all -> 0x01b4 }
            int r6 = r2.arg1     // Catch:{ all -> 0x01b4 }
            r0.delete(r6)     // Catch:{ all -> 0x01b4 }
            android.app.backup.IFullBackupRestoreObserver r0 = r5.observer     // Catch:{ all -> 0x01b4 }
            if (r0 == 0) goto L_0x01b1
            android.app.backup.IFullBackupRestoreObserver r0 = r5.observer     // Catch:{ RemoteException -> 0x0197 }
            r0.onTimeout()     // Catch:{ RemoteException -> 0x0197 }
            goto L_0x0198
        L_0x0197:
            r0 = move-exception
        L_0x0198:
            goto L_0x01b1
        L_0x0199:
            java.lang.String r0 = "BackupManagerService"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x01b4 }
            r6.<init>()     // Catch:{ all -> 0x01b4 }
            java.lang.String r7 = "couldn't find params for token "
            r6.append(r7)     // Catch:{ all -> 0x01b4 }
            int r7 = r2.arg1     // Catch:{ all -> 0x01b4 }
            r6.append(r7)     // Catch:{ all -> 0x01b4 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x01b4 }
            android.util.Slog.d(r0, r6)     // Catch:{ all -> 0x01b4 }
        L_0x01b1:
            monitor-exit(r4)     // Catch:{ all -> 0x01b4 }
            goto L_0x04bb
        L_0x01b4:
            r0 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x01b4 }
            throw r0
        L_0x01b7:
            com.android.server.backup.UserBackupManagerService r4 = r1.backupManagerService
            monitor-enter(r4)
            com.android.server.backup.UserBackupManagerService r0 = r1.backupManagerService     // Catch:{ all -> 0x01ee }
            com.android.server.backup.restore.ActiveRestoreSession r0 = r0.getActiveRestoreSession()     // Catch:{ all -> 0x01ee }
            if (r0 == 0) goto L_0x01eb
            java.lang.String r0 = "BackupManagerService"
            java.lang.String r5 = "Restore session timed out; aborting"
            android.util.Slog.w(r0, r5)     // Catch:{ all -> 0x01ee }
            com.android.server.backup.UserBackupManagerService r0 = r1.backupManagerService     // Catch:{ all -> 0x01ee }
            com.android.server.backup.restore.ActiveRestoreSession r0 = r0.getActiveRestoreSession()     // Catch:{ all -> 0x01ee }
            r0.markTimedOut()     // Catch:{ all -> 0x01ee }
            com.android.server.backup.restore.ActiveRestoreSession$EndRestoreRunnable r0 = new com.android.server.backup.restore.ActiveRestoreSession$EndRestoreRunnable     // Catch:{ all -> 0x01ee }
            com.android.server.backup.UserBackupManagerService r5 = r1.backupManagerService     // Catch:{ all -> 0x01ee }
            com.android.server.backup.restore.ActiveRestoreSession r5 = r5.getActiveRestoreSession()     // Catch:{ all -> 0x01ee }
            java.util.Objects.requireNonNull(r5)     // Catch:{ all -> 0x01ee }
            com.android.server.backup.UserBackupManagerService r6 = r1.backupManagerService     // Catch:{ all -> 0x01ee }
            com.android.server.backup.UserBackupManagerService r7 = r1.backupManagerService     // Catch:{ all -> 0x01ee }
            com.android.server.backup.restore.ActiveRestoreSession r7 = r7.getActiveRestoreSession()     // Catch:{ all -> 0x01ee }
            r0.<init>(r6, r7)     // Catch:{ all -> 0x01ee }
            r1.post(r0)     // Catch:{ all -> 0x01ee }
        L_0x01eb:
            monitor-exit(r4)     // Catch:{ all -> 0x01ee }
            goto L_0x04bb
        L_0x01ee:
            r0 = move-exception
            monitor-exit(r4)     // Catch:{ all -> 0x01ee }
            throw r0
        L_0x01f1:
            r4 = 0
            java.lang.Object r0 = r2.obj
            r6 = r0
            com.android.server.backup.params.RestoreGetSetsParams r6 = (com.android.server.backup.params.RestoreGetSetsParams) r6
            java.lang.String r7 = "BH/MSG_RUN_GET_RESTORE_SETS"
            r8 = 8
            com.android.server.backup.transport.TransportClient r0 = r6.transportClient     // Catch:{ Exception -> 0x0260 }
            com.android.internal.backup.IBackupTransport r0 = r0.connectOrThrow(r7)     // Catch:{ Exception -> 0x0260 }
            r9 = r0
            android.app.backup.RestoreSet[] r0 = r9.getAvailableRestoreSets()     // Catch:{ Exception -> 0x0260 }
            r4 = r0
            com.android.server.backup.restore.ActiveRestoreSession r10 = r6.session     // Catch:{ Exception -> 0x0260 }
            monitor-enter(r10)     // Catch:{ Exception -> 0x0260 }
            com.android.server.backup.restore.ActiveRestoreSession r0 = r6.session     // Catch:{ all -> 0x0259 }
            r0.setRestoreSets(r4)     // Catch:{ all -> 0x0259 }
            monitor-exit(r10)     // Catch:{ all -> 0x0259 }
            if (r4 != 0) goto L_0x0219
            r0 = 2831(0xb0f, float:3.967E-42)
            java.lang.Object[] r5 = new java.lang.Object[r5]     // Catch:{ Exception -> 0x0260 }
            android.util.EventLog.writeEvent(r0, r5)     // Catch:{ Exception -> 0x0260 }
        L_0x0219:
            android.app.backup.IRestoreObserver r0 = r6.observer
            if (r0 == 0) goto L_0x0247
            android.app.backup.IRestoreObserver r0 = r6.observer     // Catch:{ RemoteException -> 0x023f, Exception -> 0x0223 }
            r0.restoreSetsAvailable(r4)     // Catch:{ RemoteException -> 0x023f, Exception -> 0x0223 }
            goto L_0x0247
        L_0x0223:
            r0 = move-exception
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
        L_0x0229:
            java.lang.String r9 = "Restore observer threw: "
            r5.append(r9)
            java.lang.String r9 = r0.getMessage()
            r5.append(r9)
            java.lang.String r5 = r5.toString()
            java.lang.String r9 = "BackupManagerService"
            android.util.Slog.e(r9, r5)
            goto L_0x0247
        L_0x023f:
            r0 = move-exception
            java.lang.String r5 = "BackupManagerService"
            java.lang.String r9 = "Unable to report listing to observer"
            android.util.Slog.e(r5, r9)
        L_0x0247:
            r1.removeMessages(r8)
            com.android.server.backup.BackupAgentTimeoutParameters r0 = r1.mAgentTimeoutParameters
            long r9 = r0.getRestoreAgentTimeoutMillis()
            r1.sendEmptyMessageDelayed(r8, r9)
            com.android.server.backup.internal.OnTaskFinishedListener r0 = r6.listener
            r0.onFinished(r7)
            goto L_0x0296
        L_0x0259:
            r0 = move-exception
            monitor-exit(r10)     // Catch:{ all -> 0x0259 }
            throw r0     // Catch:{ Exception -> 0x0260 }
        L_0x025c:
            r0 = move-exception
            r5 = r4
            r4 = r0
            goto L_0x0298
        L_0x0260:
            r0 = move-exception
            java.lang.String r5 = "BackupManagerService"
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x025c }
            r9.<init>()     // Catch:{ all -> 0x025c }
            java.lang.String r10 = "Error from transport getting set list: "
            r9.append(r10)     // Catch:{ all -> 0x025c }
            java.lang.String r10 = r0.getMessage()     // Catch:{ all -> 0x025c }
            r9.append(r10)     // Catch:{ all -> 0x025c }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x025c }
            android.util.Slog.e(r5, r9)     // Catch:{ all -> 0x025c }
            android.app.backup.IRestoreObserver r0 = r6.observer
            if (r0 == 0) goto L_0x0247
            android.app.backup.IRestoreObserver r0 = r6.observer     // Catch:{ RemoteException -> 0x028d, Exception -> 0x0286 }
            r0.restoreSetsAvailable(r4)     // Catch:{ RemoteException -> 0x028d, Exception -> 0x0286 }
            goto L_0x0247
        L_0x0286:
            r0 = move-exception
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            goto L_0x0229
        L_0x028d:
            r0 = move-exception
            java.lang.String r5 = "BackupManagerService"
            java.lang.String r9 = "Unable to report listing to observer"
            android.util.Slog.e(r5, r9)
            goto L_0x0247
        L_0x0296:
            goto L_0x04bb
        L_0x0298:
            android.app.backup.IRestoreObserver r0 = r6.observer
            if (r0 == 0) goto L_0x02c6
            android.app.backup.IRestoreObserver r0 = r6.observer     // Catch:{ RemoteException -> 0x02be, Exception -> 0x02a2 }
            r0.restoreSetsAvailable(r5)     // Catch:{ RemoteException -> 0x02be, Exception -> 0x02a2 }
            goto L_0x02c6
        L_0x02a2:
            r0 = move-exception
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r10 = "Restore observer threw: "
            r9.append(r10)
            java.lang.String r10 = r0.getMessage()
            r9.append(r10)
            java.lang.String r9 = r9.toString()
            java.lang.String r10 = "BackupManagerService"
            android.util.Slog.e(r10, r9)
            goto L_0x02c6
        L_0x02be:
            r0 = move-exception
            java.lang.String r9 = "BackupManagerService"
            java.lang.String r10 = "Unable to report listing to observer"
            android.util.Slog.e(r9, r10)
        L_0x02c6:
            r1.removeMessages(r8)
            com.android.server.backup.BackupAgentTimeoutParameters r0 = r1.mAgentTimeoutParameters
            long r9 = r0.getRestoreAgentTimeoutMillis()
            r1.sendEmptyMessageDelayed(r8, r9)
            com.android.server.backup.internal.OnTaskFinishedListener r0 = r6.listener
            r0.onFinished(r7)
            throw r4
        L_0x02d8:
            java.lang.Object r0 = r2.obj
            com.android.server.backup.params.ClearParams r0 = (com.android.server.backup.params.ClearParams) r0
            com.android.server.backup.internal.PerformClearTask r4 = new com.android.server.backup.internal.PerformClearTask
            com.android.server.backup.UserBackupManagerService r5 = r1.backupManagerService
            com.android.server.backup.transport.TransportClient r6 = r0.transportClient
            android.content.pm.PackageInfo r7 = r0.packageInfo
            com.android.server.backup.internal.OnTaskFinishedListener r8 = r0.listener
            r4.<init>(r5, r6, r7, r8)
            r4.run()
            goto L_0x04bb
        L_0x02ee:
            java.lang.Object r0 = r2.obj
            r5 = r0
            com.android.server.backup.params.RestoreParams r5 = (com.android.server.backup.params.RestoreParams) r5
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r6 = "MSG_RUN_RESTORE observer="
            r0.append(r6)
            android.app.backup.IRestoreObserver r6 = r5.observer
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            java.lang.String r6 = "BackupManagerService"
            android.util.Slog.d(r6, r0)
            com.android.server.backup.restore.PerformUnifiedRestoreTask r0 = new com.android.server.backup.restore.PerformUnifiedRestoreTask
            com.android.server.backup.UserBackupManagerService r8 = r1.backupManagerService
            com.android.server.backup.transport.TransportClient r9 = r5.transportClient
            android.app.backup.IRestoreObserver r10 = r5.observer
            android.app.backup.IBackupManagerMonitor r11 = r5.monitor
            long r12 = r5.token
            android.content.pm.PackageInfo r14 = r5.packageInfo
            int r15 = r5.pmToken
            boolean r6 = r5.isSystemRestore
            java.lang.String[] r7 = r5.filterSet
            com.android.server.backup.internal.OnTaskFinishedListener r4 = r5.listener
            r17 = r7
            r7 = r0
            r16 = r6
            r18 = r4
            r7.<init>(r8, r9, r10, r11, r12, r14, r15, r16, r17, r18)
            r4 = r0
            com.android.server.backup.UserBackupManagerService r0 = r1.backupManagerService
            java.util.Queue r6 = r0.getPendingRestores()
            monitor-enter(r6)
            com.android.server.backup.UserBackupManagerService r0 = r1.backupManagerService     // Catch:{ all -> 0x0365 }
            boolean r0 = r0.isRestoreInProgress()     // Catch:{ all -> 0x0365 }
            if (r0 == 0) goto L_0x034c
            java.lang.String r0 = "BackupManagerService"
            java.lang.String r7 = "Restore in progress, queueing."
            android.util.Slog.d(r0, r7)     // Catch:{ all -> 0x0365 }
            com.android.server.backup.UserBackupManagerService r0 = r1.backupManagerService     // Catch:{ all -> 0x0365 }
            java.util.Queue r0 = r0.getPendingRestores()     // Catch:{ all -> 0x0365 }
            r0.add(r4)     // Catch:{ all -> 0x0365 }
            goto L_0x0362
        L_0x034c:
            java.lang.String r0 = "BackupManagerService"
            java.lang.String r7 = "Starting restore."
            android.util.Slog.d(r0, r7)     // Catch:{ all -> 0x0365 }
            com.android.server.backup.UserBackupManagerService r0 = r1.backupManagerService     // Catch:{ all -> 0x0365 }
            r7 = 1
            r0.setRestoreInProgress(r7)     // Catch:{ all -> 0x0365 }
            r0 = 20
            android.os.Message r0 = r1.obtainMessage(r0, r4)     // Catch:{ all -> 0x0365 }
            r1.sendMessage(r0)     // Catch:{ all -> 0x0365 }
        L_0x0362:
            monitor-exit(r6)     // Catch:{ all -> 0x0365 }
            goto L_0x04bb
        L_0x0365:
            r0 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x0365 }
            throw r0
        L_0x0368:
            java.lang.Object r0 = r2.obj
            com.android.server.backup.params.AdbBackupParams r0 = (com.android.server.backup.params.AdbBackupParams) r0
            com.android.server.backup.fullbackup.PerformAdbBackupTask r20 = new com.android.server.backup.fullbackup.PerformAdbBackupTask
            com.android.server.backup.UserBackupManagerService r5 = r1.backupManagerService
            android.os.ParcelFileDescriptor r6 = r0.fd
            android.app.backup.IFullBackupRestoreObserver r7 = r0.observer
            boolean r8 = r0.includeApks
            boolean r9 = r0.includeObbs
            boolean r10 = r0.includeShared
            boolean r11 = r0.doWidgets
            java.lang.String r12 = r0.curPassword
            java.lang.String r13 = r0.encryptPassword
            boolean r14 = r0.allApps
            boolean r15 = r0.includeSystem
            boolean r4 = r0.doCompress
            boolean r2 = r0.includeKeyValue
            r21 = r3
            java.lang.String[] r3 = r0.packages
            java.util.concurrent.atomic.AtomicBoolean r1 = r0.latch
            r16 = r4
            r4 = r20
            r17 = r2
            r18 = r3
            r19 = r1
            r4.<init>(r5, r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16, r17, r18, r19)
            r1 = r20
            java.lang.Thread r2 = new java.lang.Thread
            java.lang.String r3 = "adb-backup"
            r2.<init>(r1, r3)
            r2.start()
            r1 = r22
            r3 = r21
            goto L_0x04bb
        L_0x03ad:
            r21 = r3
            r1 = r22
            com.android.server.backup.UserBackupManagerService r0 = r1.backupManagerService
            long r2 = java.lang.System.currentTimeMillis()
            r0.setLastBackupPass(r2)
            java.lang.String r2 = "BH/MSG_RUN_BACKUP"
            r3 = r21
            com.android.server.backup.transport.TransportClient r4 = r3.getCurrentTransportClient(r2)
            r0 = 0
            if (r4 == 0) goto L_0x03cb
            com.android.internal.backup.IBackupTransport r6 = r4.connect(r2)
            goto L_0x03cc
        L_0x03cb:
            r6 = r0
        L_0x03cc:
            r17 = r6
            if (r17 != 0) goto L_0x03f8
            if (r4 == 0) goto L_0x03d6
            r3.disposeOfTransportClient(r4, r2)
        L_0x03d6:
            java.lang.String r0 = "BackupManagerService"
            java.lang.String r6 = "Backup requested but no transport available"
            android.util.Slog.v(r0, r6)
            com.android.server.backup.UserBackupManagerService r0 = r1.backupManagerService
            java.lang.Object r6 = r0.getQueueLock()
            monitor-enter(r6)
            com.android.server.backup.UserBackupManagerService r0 = r1.backupManagerService     // Catch:{ all -> 0x03f5 }
            r0.setBackupRunning(r5)     // Catch:{ all -> 0x03f5 }
            monitor-exit(r6)     // Catch:{ all -> 0x03f5 }
            com.android.server.backup.UserBackupManagerService r0 = r1.backupManagerService
            android.os.PowerManager$WakeLock r0 = r0.getWakelock()
            r0.release()
            goto L_0x04bb
        L_0x03f5:
            r0 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x03f5 }
            throw r0
        L_0x03f8:
            java.util.ArrayList r6 = new java.util.ArrayList
            r6.<init>()
            r15 = r6
            com.android.server.backup.UserBackupManagerService r6 = r1.backupManagerService
            com.android.server.backup.DataChangedJournal r18 = r6.getJournal()
            com.android.server.backup.UserBackupManagerService r6 = r1.backupManagerService
            java.lang.Object r6 = r6.getQueueLock()
            monitor-enter(r6)
            com.android.server.backup.UserBackupManagerService r7 = r1.backupManagerService     // Catch:{ all -> 0x04b4 }
            java.util.HashMap r7 = r7.getPendingBackups()     // Catch:{ all -> 0x04b4 }
            int r7 = r7.size()     // Catch:{ all -> 0x04b4 }
            if (r7 <= 0) goto L_0x0452
            com.android.server.backup.UserBackupManagerService r7 = r1.backupManagerService     // Catch:{ all -> 0x044e }
            java.util.HashMap r7 = r7.getPendingBackups()     // Catch:{ all -> 0x044e }
            java.util.Collection r7 = r7.values()     // Catch:{ all -> 0x044e }
            java.util.Iterator r7 = r7.iterator()     // Catch:{ all -> 0x044e }
        L_0x0425:
            boolean r8 = r7.hasNext()     // Catch:{ all -> 0x044e }
            if (r8 == 0) goto L_0x0438
            java.lang.Object r8 = r7.next()     // Catch:{ all -> 0x044e }
            com.android.server.backup.keyvalue.BackupRequest r8 = (com.android.server.backup.keyvalue.BackupRequest) r8     // Catch:{ all -> 0x044e }
            java.lang.String r9 = r8.packageName     // Catch:{ all -> 0x044e }
            r15.add(r9)     // Catch:{ all -> 0x044e }
            goto L_0x0425
        L_0x0438:
            java.lang.String r7 = "BackupManagerService"
            java.lang.String r8 = "clearing pending backups"
            android.util.Slog.v(r7, r8)     // Catch:{ all -> 0x044e }
            com.android.server.backup.UserBackupManagerService r7 = r1.backupManagerService     // Catch:{ all -> 0x044e }
            java.util.HashMap r7 = r7.getPendingBackups()     // Catch:{ all -> 0x044e }
            r7.clear()     // Catch:{ all -> 0x044e }
            com.android.server.backup.UserBackupManagerService r7 = r1.backupManagerService     // Catch:{ all -> 0x044e }
            r7.setJournal(r0)     // Catch:{ all -> 0x044e }
            goto L_0x0452
        L_0x044e:
            r0 = move-exception
            r20 = r15
            goto L_0x04b7
        L_0x0452:
            monitor-exit(r6)     // Catch:{ all -> 0x04b4 }
            r19 = 1
            int r0 = r15.size()
            if (r0 <= 0) goto L_0x048a
            com.android.server.backup.internal.-$$Lambda$BackupHandler$TJcRazGYTaUxjeiX6mPLlipfZUI r13 = new com.android.server.backup.internal.-$$Lambda$BackupHandler$TJcRazGYTaUxjeiX6mPLlipfZUI     // Catch:{ Exception -> 0x047d }
            r13.<init>(r4)     // Catch:{ Exception -> 0x047d }
            com.android.server.backup.UserBackupManagerService r6 = r1.backupManagerService     // Catch:{ Exception -> 0x047d }
            java.lang.String r8 = r17.transportDirName()     // Catch:{ Exception -> 0x047d }
            r11 = 0
            r12 = 0
            java.util.List r14 = java.util.Collections.emptyList()     // Catch:{ Exception -> 0x047d }
            r0 = 0
            r16 = 0
            r7 = r4
            r9 = r15
            r10 = r18
            r20 = r15
            r15 = r0
            com.android.server.backup.keyvalue.KeyValueBackupTask.start(r6, r7, r8, r9, r10, r11, r12, r13, r14, r15, r16)     // Catch:{ Exception -> 0x047b }
            goto L_0x0495
        L_0x047b:
            r0 = move-exception
            goto L_0x0480
        L_0x047d:
            r0 = move-exception
            r20 = r15
        L_0x0480:
            java.lang.String r6 = "BackupManagerService"
            java.lang.String r7 = "Transport became unavailable attempting backup or error initializing backup task"
            android.util.Slog.e(r6, r7, r0)
            r19 = 0
            goto L_0x0495
        L_0x048a:
            r20 = r15
            java.lang.String r0 = "BackupManagerService"
            java.lang.String r6 = "Backup requested but nothing pending"
            android.util.Slog.v(r0, r6)
            r19 = 0
        L_0x0495:
            if (r19 != 0) goto L_0x04bb
            r3.disposeOfTransportClient(r4, r2)
            com.android.server.backup.UserBackupManagerService r0 = r1.backupManagerService
            java.lang.Object r6 = r0.getQueueLock()
            monitor-enter(r6)
            com.android.server.backup.UserBackupManagerService r0 = r1.backupManagerService     // Catch:{ all -> 0x04b1 }
            r0.setBackupRunning(r5)     // Catch:{ all -> 0x04b1 }
            monitor-exit(r6)     // Catch:{ all -> 0x04b1 }
            com.android.server.backup.UserBackupManagerService r0 = r1.backupManagerService
            android.os.PowerManager$WakeLock r0 = r0.getWakelock()
            r0.release()
            goto L_0x04bb
        L_0x04b1:
            r0 = move-exception
            monitor-exit(r6)     // Catch:{ all -> 0x04b1 }
            throw r0
        L_0x04b4:
            r0 = move-exception
            r20 = r15
        L_0x04b7:
            monitor-exit(r6)     // Catch:{ all -> 0x04b9 }
            throw r0
        L_0x04b9:
            r0 = move-exception
            goto L_0x04b7
        L_0x04bb:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.internal.BackupHandler.handleMessage(android.os.Message):void");
    }
}
