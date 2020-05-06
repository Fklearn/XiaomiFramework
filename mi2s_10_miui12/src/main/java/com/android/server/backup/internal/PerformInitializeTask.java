package com.android.server.backup.internal;

import android.app.backup.IBackupObserver;
import android.os.RemoteException;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.backup.TransportManager;
import com.android.server.backup.UserBackupManagerService;
import java.io.File;

public class PerformInitializeTask implements Runnable {
    private final UserBackupManagerService mBackupManagerService;
    private final File mBaseStateDir;
    private final OnTaskFinishedListener mListener;
    private IBackupObserver mObserver;
    private final String[] mQueue;
    private final TransportManager mTransportManager;

    public PerformInitializeTask(UserBackupManagerService backupManagerService, String[] transportNames, IBackupObserver observer, OnTaskFinishedListener listener) {
        this(backupManagerService, backupManagerService.getTransportManager(), transportNames, observer, listener, backupManagerService.getBaseStateDir());
    }

    @VisibleForTesting
    PerformInitializeTask(UserBackupManagerService backupManagerService, TransportManager transportManager, String[] transportNames, IBackupObserver observer, OnTaskFinishedListener listener, File baseStateDir) {
        this.mBackupManagerService = backupManagerService;
        this.mTransportManager = transportManager;
        this.mQueue = transportNames;
        this.mObserver = observer;
        this.mListener = listener;
        this.mBaseStateDir = baseStateDir;
    }

    private void notifyResult(String target, int status) {
        try {
            if (this.mObserver != null) {
                this.mObserver.onResult(target, status);
            }
        } catch (RemoteException e) {
            this.mObserver = null;
        }
    }

    private void notifyFinished(int status) {
        try {
            if (this.mObserver != null) {
                this.mObserver.backupFinished(status);
            }
        } catch (RemoteException e) {
            this.mObserver = null;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:52:0x017d A[LOOP:2: B:50:0x0177->B:52:0x017d, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:57:0x0194 A[LOOP:3: B:55:0x018e->B:57:0x0194, LOOP_END] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void run() {
        /*
            r24 = this;
            r1 = r24
            java.lang.String r2 = "BackupManagerService"
            java.lang.String r3 = "PerformInitializeTask.run()"
            java.util.ArrayList r0 = new java.util.ArrayList
            java.lang.String[] r4 = r1.mQueue
            int r4 = r4.length
            r0.<init>(r4)
            r4 = r0
            r5 = 0
            java.lang.String[] r0 = r1.mQueue     // Catch:{ Exception -> 0x016b }
            int r6 = r0.length     // Catch:{ Exception -> 0x016b }
            r8 = r5
            r5 = 0
        L_0x0015:
            if (r5 >= r6) goto L_0x0149
            r9 = r0[r5]     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            com.android.server.backup.TransportManager r10 = r1.mTransportManager     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            com.android.server.backup.transport.TransportClient r10 = r10.getTransportClient(r9, r3)     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            if (r10 != 0) goto L_0x0040
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            r11.<init>()     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            java.lang.String r12 = "Requested init for "
            r11.append(r12)     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            r11.append(r9)     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            java.lang.String r12 = " but not found"
            r11.append(r12)     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            android.util.Slog.e(r2, r11)     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            r19 = r0
            r20 = r6
            goto L_0x00d0
        L_0x0040:
            r4.add(r10)     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            java.lang.StringBuilder r11 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            r11.<init>()     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            java.lang.String r12 = "Initializing (wiping) backup transport storage: "
            r11.append(r12)     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            r11.append(r9)     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            java.lang.String r11 = r11.toString()     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            android.util.Slog.i(r2, r11)     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            com.android.server.backup.TransportManager r11 = r1.mTransportManager     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            android.content.ComponentName r12 = r10.getTransportComponent()     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            java.lang.String r11 = r11.getTransportDirName((android.content.ComponentName) r12)     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            r12 = 2821(0xb05, float:3.953E-42)
            android.util.EventLog.writeEvent(r12, r11)     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            long r12 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            com.android.internal.backup.IBackupTransport r14 = r10.connectOrThrow(r3)     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            int r15 = r14.initializeDevice()     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            if (r15 == 0) goto L_0x007a
            java.lang.String r7 = "Transport error in initializeDevice()"
            android.util.Slog.e(r2, r7)     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            goto L_0x0086
        L_0x007a:
            int r7 = r14.finishBackup()     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            r15 = r7
            if (r15 == 0) goto L_0x0086
            java.lang.String r7 = "Transport error in finishBackup()"
            android.util.Slog.e(r2, r7)     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
        L_0x0086:
            if (r15 != 0) goto L_0x00d2
            java.lang.String r7 = "Device init successful"
            android.util.Slog.i(r2, r7)     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            long r18 = android.os.SystemClock.elapsedRealtime()     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            r20 = r6
            long r6 = r18 - r12
            int r6 = (int) r6     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            r19 = r0
            r7 = 0
            java.lang.Object[] r0 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            r7 = 2827(0xb0b, float:3.961E-42)
            android.util.EventLog.writeEvent(r7, r0)     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            java.io.File r0 = new java.io.File     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            java.io.File r7 = r1.mBaseStateDir     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            r0.<init>(r7, r11)     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            com.android.server.backup.UserBackupManagerService r7 = r1.mBackupManagerService     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            r7.resetBackupState(r0)     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            r7 = 2
            java.lang.Object[] r7 = new java.lang.Object[r7]     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            r16 = 0
            java.lang.Integer r21 = java.lang.Integer.valueOf(r16)     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            r7[r16] = r21     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            java.lang.Integer r21 = java.lang.Integer.valueOf(r6)     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            r17 = 1
            r7[r17] = r21     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            r17 = r0
            r0 = 2825(0xb09, float:3.959E-42)
            android.util.EventLog.writeEvent(r0, r7)     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            com.android.server.backup.UserBackupManagerService r0 = r1.mBackupManagerService     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            r7 = 0
            r0.recordInitPending(r7, r9, r11)     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            r1.notifyResult(r9, r7)     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
        L_0x00d0:
            r7 = 0
            goto L_0x0128
        L_0x00d2:
            r19 = r0
            r20 = r6
            r0 = 2822(0xb06, float:3.954E-42)
            java.lang.String r6 = "(initialize)"
            android.util.EventLog.writeEvent(r0, r6)     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            com.android.server.backup.UserBackupManagerService r0 = r1.mBackupManagerService     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            r6 = 1
            r0.recordInitPending(r6, r9, r11)     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            r1.notifyResult(r9, r15)     // Catch:{ Exception -> 0x0146, all -> 0x0144 }
            r6 = r15
            long r7 = r14.requestBackupTime()     // Catch:{ Exception -> 0x013e, all -> 0x0138 }
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ Exception -> 0x013e, all -> 0x0138 }
            r0.<init>()     // Catch:{ Exception -> 0x013e, all -> 0x0138 }
            r17 = r6
            java.lang.String r6 = "Init failed on "
            r0.append(r6)     // Catch:{ Exception -> 0x0134, all -> 0x0130 }
            r0.append(r9)     // Catch:{ Exception -> 0x0134, all -> 0x0130 }
            java.lang.String r6 = " resched in "
            r0.append(r6)     // Catch:{ Exception -> 0x0134, all -> 0x0130 }
            r0.append(r7)     // Catch:{ Exception -> 0x0134, all -> 0x0130 }
            java.lang.String r0 = r0.toString()     // Catch:{ Exception -> 0x0134, all -> 0x0130 }
            android.util.Slog.w(r2, r0)     // Catch:{ Exception -> 0x0134, all -> 0x0130 }
            com.android.server.backup.UserBackupManagerService r0 = r1.mBackupManagerService     // Catch:{ Exception -> 0x0134, all -> 0x0130 }
            android.app.AlarmManager r0 = r0.getAlarmManager()     // Catch:{ Exception -> 0x0134, all -> 0x0130 }
            long r21 = java.lang.System.currentTimeMillis()     // Catch:{ Exception -> 0x0134, all -> 0x0130 }
            r6 = r9
            r18 = r10
            long r9 = r21 + r7
            r21 = r6
            com.android.server.backup.UserBackupManagerService r6 = r1.mBackupManagerService     // Catch:{ Exception -> 0x0134, all -> 0x0130 }
            android.app.PendingIntent r6 = r6.getRunInitIntent()     // Catch:{ Exception -> 0x0134, all -> 0x0130 }
            r22 = r7
            r7 = 0
            r0.set(r7, r9, r6)     // Catch:{ Exception -> 0x0134, all -> 0x0130 }
            r8 = r17
        L_0x0128:
            int r5 = r5 + 1
            r0 = r19
            r6 = r20
            goto L_0x0015
        L_0x0130:
            r0 = move-exception
            r8 = r17
            goto L_0x018a
        L_0x0134:
            r0 = move-exception
            r5 = r17
            goto L_0x016c
        L_0x0138:
            r0 = move-exception
            r17 = r6
            r8 = r17
            goto L_0x018a
        L_0x013e:
            r0 = move-exception
            r17 = r6
            r5 = r17
            goto L_0x016c
        L_0x0144:
            r0 = move-exception
            goto L_0x018a
        L_0x0146:
            r0 = move-exception
            r5 = r8
            goto L_0x016c
        L_0x0149:
            java.util.Iterator r0 = r4.iterator()
        L_0x014d:
            boolean r2 = r0.hasNext()
            if (r2 == 0) goto L_0x015f
            java.lang.Object r2 = r0.next()
            com.android.server.backup.transport.TransportClient r2 = (com.android.server.backup.transport.TransportClient) r2
            com.android.server.backup.TransportManager r5 = r1.mTransportManager
            r5.disposeOfTransportClient(r2, r3)
            goto L_0x014d
        L_0x015f:
            r1.notifyFinished(r8)
            com.android.server.backup.internal.OnTaskFinishedListener r0 = r1.mListener
            r0.onFinished(r3)
            goto L_0x0189
        L_0x0168:
            r0 = move-exception
            r8 = r5
            goto L_0x018a
        L_0x016b:
            r0 = move-exception
        L_0x016c:
            java.lang.String r6 = "Unexpected error performing init"
            android.util.Slog.e(r2, r6, r0)     // Catch:{ all -> 0x0168 }
            r8 = -1000(0xfffffffffffffc18, float:NaN)
            java.util.Iterator r0 = r4.iterator()
        L_0x0177:
            boolean r2 = r0.hasNext()
            if (r2 == 0) goto L_0x015f
            java.lang.Object r2 = r0.next()
            com.android.server.backup.transport.TransportClient r2 = (com.android.server.backup.transport.TransportClient) r2
            com.android.server.backup.TransportManager r5 = r1.mTransportManager
            r5.disposeOfTransportClient(r2, r3)
            goto L_0x0177
        L_0x0189:
            return
        L_0x018a:
            java.util.Iterator r2 = r4.iterator()
        L_0x018e:
            boolean r5 = r2.hasNext()
            if (r5 == 0) goto L_0x01a0
            java.lang.Object r5 = r2.next()
            com.android.server.backup.transport.TransportClient r5 = (com.android.server.backup.transport.TransportClient) r5
            com.android.server.backup.TransportManager r6 = r1.mTransportManager
            r6.disposeOfTransportClient(r5, r3)
            goto L_0x018e
        L_0x01a0:
            r1.notifyFinished(r8)
            com.android.server.backup.internal.OnTaskFinishedListener r2 = r1.mListener
            r2.onFinished(r3)
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.backup.internal.PerformInitializeTask.run():void");
    }
}
