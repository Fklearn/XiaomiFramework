package com.android.server.am;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.content.ContentResolver;
import android.content.IIntentReceiver;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.util.EventLog;
import android.util.Slog;
import android.util.SparseIntArray;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import com.android.server.display.color.DisplayTransformManager;
import com.android.server.pm.PackageManagerService;
import com.android.server.wm.WindowProcessController;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

public class BroadcastQueue {
    static final int BROADCAST_INTENT_MSG = 200;
    static final int BROADCAST_TIMEOUT_MSG = 201;
    static final int MAX_BROADCAST_HISTORY = (ActivityManager.isLowRamDeviceStatic() ? 10 : 50);
    static final int MAX_BROADCAST_SUMMARY_HISTORY = (ActivityManager.isLowRamDeviceStatic() ? 25 : DisplayTransformManager.LEVEL_COLOR_MATRIX_INVERT_COLOR);
    private static final String TAG = "BroadcastQueue";
    private static final String TAG_BROADCAST = "BroadcastQueue";
    private static final String TAG_MU = "BroadcastQueue_MU";
    final BroadcastRecord[] mBroadcastHistory = new BroadcastRecord[MAX_BROADCAST_HISTORY];
    final Intent[] mBroadcastSummaryHistory;
    boolean mBroadcastsScheduled;
    final BroadcastConstants mConstants;
    final boolean mDelayBehindServices;
    final BroadcastDispatcher mDispatcher;
    final BroadcastHandler mHandler;
    int mHistoryNext = 0;
    boolean mLogLatencyMetrics;
    private int mNextToken = 0;
    final ArrayList<BroadcastRecord> mParallelBroadcasts = new ArrayList<>();
    BroadcastRecord mPendingBroadcast;
    int mPendingBroadcastRecvIndex;
    boolean mPendingBroadcastTimeoutMessage;
    final String mQueueName;
    final ActivityManagerService mService;
    final SparseIntArray mSplitRefcounts = new SparseIntArray();
    final long[] mSummaryHistoryDispatchTime;
    final long[] mSummaryHistoryEnqueueTime;
    final long[] mSummaryHistoryFinishTime;
    int mSummaryHistoryNext;

    final class BroadcastHandler extends Handler {
        public BroadcastHandler(Looper looper) {
            super(looper, (Handler.Callback) null, true);
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 200) {
                BroadcastQueue.this.processNextBroadcast(true);
            } else if (i == 201) {
                synchronized (BroadcastQueue.this.mService) {
                    try {
                        ActivityManagerService.boostPriorityForLockedSection();
                        BroadcastQueue.this.broadcastTimeoutLocked(true);
                    } catch (Throwable th) {
                        while (true) {
                            ActivityManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
            }
        }
    }

    private final class AppNotResponding implements Runnable {
        private final String mAnnotation;
        private final ProcessRecord mApp;

        public AppNotResponding(ProcessRecord app, String annotation) {
            this.mApp = app;
            this.mAnnotation = annotation;
        }

        public void run() {
            this.mApp.appNotResponding((String) null, (ApplicationInfo) null, (String) null, (WindowProcessController) null, false, this.mAnnotation);
        }
    }

    BroadcastQueue(ActivityManagerService service, Handler handler, String name, BroadcastConstants constants, boolean allowDelayBehindServices) {
        int i = MAX_BROADCAST_SUMMARY_HISTORY;
        this.mBroadcastSummaryHistory = new Intent[i];
        this.mSummaryHistoryNext = 0;
        this.mSummaryHistoryEnqueueTime = new long[i];
        this.mSummaryHistoryDispatchTime = new long[i];
        this.mSummaryHistoryFinishTime = new long[i];
        this.mBroadcastsScheduled = false;
        this.mPendingBroadcast = null;
        this.mLogLatencyMetrics = true;
        this.mService = service;
        this.mHandler = new BroadcastHandler(handler.getLooper());
        this.mQueueName = name;
        this.mDelayBehindServices = allowDelayBehindServices;
        this.mConstants = constants;
        this.mDispatcher = new BroadcastDispatcher(this, this.mConstants, this.mHandler, this.mService);
    }

    /* access modifiers changed from: package-private */
    public void start(ContentResolver resolver) {
        this.mDispatcher.start();
        this.mConstants.startObserving(this.mHandler, resolver);
    }

    public String toString() {
        return this.mQueueName;
    }

    public boolean isPendingBroadcastProcessLocked(int pid) {
        BroadcastRecord broadcastRecord = this.mPendingBroadcast;
        return broadcastRecord != null && broadcastRecord.curApp.pid == pid;
    }

    public void enqueueParallelBroadcastLocked(BroadcastRecord r) {
        this.mParallelBroadcasts.add(r);
        enqueueBroadcastHelper(r);
    }

    public void enqueueOrderedBroadcastLocked(BroadcastRecord r) {
        this.mDispatcher.enqueueOrderedBroadcastLocked(r);
        enqueueBroadcastHelper(r);
    }

    private void enqueueBroadcastHelper(BroadcastRecord r) {
        r.enqueueClockTime = System.currentTimeMillis();
        if (Trace.isTagEnabled(64)) {
            Trace.asyncTraceBegin(64, createBroadcastTraceTitle(r, 0), System.identityHashCode(r));
        }
    }

    public final BroadcastRecord replaceParallelBroadcastLocked(BroadcastRecord r) {
        return replaceBroadcastLocked(this.mParallelBroadcasts, r, "PARALLEL");
    }

    public final BroadcastRecord replaceOrderedBroadcastLocked(BroadcastRecord r) {
        return this.mDispatcher.replaceBroadcastLocked(r, "ORDERED");
    }

    private BroadcastRecord replaceBroadcastLocked(ArrayList<BroadcastRecord> queue, BroadcastRecord r, String typeForLogging) {
        Intent intent = r.intent;
        int i = queue.size() - 1;
        while (i > 0) {
            BroadcastRecord old = queue.get(i);
            if (old.userId != r.userId || !intent.filterEquals(old.intent)) {
                i--;
            } else {
                queue.set(i, r);
                return old;
            }
        }
        return null;
    }

    private final void processCurBroadcastLocked(BroadcastRecord r, ProcessRecord app, boolean skipOomAdj) throws RemoteException {
        BroadcastRecord broadcastRecord = r;
        ProcessRecord processRecord = app;
        if (processRecord.thread == null) {
            throw new RemoteException();
        } else if (processRecord.inFullBackup) {
            skipReceiverLocked(r);
        } else {
            broadcastRecord.receiver = processRecord.thread.asBinder();
            broadcastRecord.curApp = processRecord;
            processRecord.curReceivers.add(broadcastRecord);
            if (BroadcastQueueInjector.checkReceiverAppDealBroadcast(this, this.mService, broadcastRecord, processRecord, true)) {
                processRecord.forceProcessStateUpTo(12);
                this.mService.mProcessList.updateLruProcessLocked(processRecord, false, (ProcessRecord) null);
                if (!skipOomAdj) {
                    this.mService.updateOomAdjLocked("updateOomAdj_meh");
                }
                broadcastRecord.intent.setComponent(broadcastRecord.curComponent);
                boolean started = false;
                try {
                    this.mService.notifyPackageUse(broadcastRecord.intent.getComponent().getPackageName(), 3);
                    processRecord.thread.scheduleReceiver(new Intent(broadcastRecord.intent), broadcastRecord.curReceiver, this.mService.compatibilityInfoForPackage(broadcastRecord.curReceiver.applicationInfo), broadcastRecord.resultCode, broadcastRecord.resultData, broadcastRecord.resultExtras, broadcastRecord.ordered, broadcastRecord.userId, app.getReportedProcState());
                    started = true;
                } finally {
                    if (!started) {
                        broadcastRecord.receiver = null;
                        broadcastRecord.curApp = null;
                        processRecord.curReceivers.remove(broadcastRecord);
                    }
                }
            }
        }
    }

    public boolean sendPendingBroadcastsLocked(ProcessRecord app) {
        BroadcastRecord br = this.mPendingBroadcast;
        if (br == null || br.curApp.pid <= 0 || br.curApp.pid != app.pid) {
            return false;
        }
        if (br.curApp != app) {
            Slog.e("BroadcastQueue", "App mismatch when sending pending broadcast to " + app.processName + ", intended target is " + br.curApp.processName);
            return false;
        }
        try {
            this.mPendingBroadcast = null;
            processCurBroadcastLocked(br, app, false);
            return true;
        } catch (Exception e) {
            Exception e2 = e;
            Slog.w("BroadcastQueue", "Exception in new application when starting receiver " + br.curComponent.flattenToShortString(), e2);
            logBroadcastReceiverDiscardLocked(br);
            finishReceiverLocked(br, br.resultCode, br.resultData, br.resultExtras, br.resultAbort, false);
            scheduleBroadcastsLocked();
            br.state = 0;
            throw new RuntimeException(e2.getMessage());
        }
    }

    public void skipPendingBroadcastLocked(int pid) {
        BroadcastRecord br = this.mPendingBroadcast;
        if (br != null && br.curApp.pid == pid) {
            br.state = 0;
            br.nextReceiver = this.mPendingBroadcastRecvIndex;
            this.mPendingBroadcast = null;
            scheduleBroadcastsLocked();
        }
    }

    public void skipCurrentReceiverLocked(ProcessRecord app) {
        BroadcastRecord broadcastRecord;
        BroadcastRecord r = null;
        BroadcastRecord curActive = this.mDispatcher.getActiveBroadcastLocked();
        if (curActive != null && curActive.curApp == app) {
            r = curActive;
        }
        if (r == null && (broadcastRecord = this.mPendingBroadcast) != null && broadcastRecord.curApp == app) {
            r = this.mPendingBroadcast;
        }
        if (r != null) {
            skipReceiverLocked(r);
        }
    }

    private void skipReceiverLocked(BroadcastRecord r) {
        logBroadcastReceiverDiscardLocked(r);
        finishReceiverLocked(r, r.resultCode, r.resultData, r.resultExtras, r.resultAbort, false);
        scheduleBroadcastsLocked();
    }

    public void scheduleBroadcastsLocked() {
        if (!this.mBroadcastsScheduled) {
            BroadcastHandler broadcastHandler = this.mHandler;
            broadcastHandler.sendMessage(broadcastHandler.obtainMessage(200, this));
            this.mBroadcastsScheduled = true;
        }
    }

    public BroadcastRecord getMatchingOrderedReceiver(IBinder receiver) {
        BroadcastRecord br = this.mDispatcher.getActiveBroadcastLocked();
        if (br == null || br.receiver != receiver) {
            return null;
        }
        return br;
    }

    private int nextSplitTokenLocked() {
        int next = this.mNextToken + 1;
        if (next <= 0) {
            next = 1;
        }
        this.mNextToken = next;
        return next;
    }

    private void postActivityStartTokenRemoval(ProcessRecord app, BroadcastRecord r) {
        String msgToken = (app.toShortString() + r.toString()).intern();
        this.mHandler.removeCallbacksAndMessages(msgToken);
        this.mHandler.postAtTime(new Runnable(app, r) {
            private final /* synthetic */ ProcessRecord f$1;
            private final /* synthetic */ BroadcastRecord f$2;

            {
                this.f$1 = r2;
                this.f$2 = r3;
            }

            public final void run() {
                BroadcastQueue.this.lambda$postActivityStartTokenRemoval$0$BroadcastQueue(this.f$1, this.f$2);
            }
        }, msgToken, r.receiverTime + this.mConstants.ALLOW_BG_ACTIVITY_START_TIMEOUT);
    }

    public /* synthetic */ void lambda$postActivityStartTokenRemoval$0$BroadcastQueue(ProcessRecord app, BroadcastRecord r) {
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                app.removeAllowBackgroundActivityStartsToken(r);
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r11v10, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v19, resolved type: android.content.pm.ActivityInfo} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean finishReceiverLocked(com.android.server.am.BroadcastRecord r19, int r20, java.lang.String r21, android.os.Bundle r22, boolean r23, boolean r24) {
        /*
            r18 = this;
            r0 = r18
            r1 = r19
            r2 = r23
            int r3 = r1.state
            android.content.pm.ActivityInfo r4 = r1.curReceiver
            long r5 = android.os.SystemClock.uptimeMillis()
            long r7 = r1.receiverTime
            long r7 = r5 - r7
            r9 = 0
            r1.state = r9
            java.lang.String r10 = "BroadcastQueue"
            if (r3 != 0) goto L_0x0034
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r12 = "finishReceiver ["
            r11.append(r12)
            java.lang.String r12 = r0.mQueueName
            r11.append(r12)
            java.lang.String r12 = "] called but state is IDLE"
            r11.append(r12)
            java.lang.String r11 = r11.toString()
            android.util.Slog.w(r10, r11)
        L_0x0034:
            boolean r11 = r1.allowBackgroundActivityStarts
            if (r11 == 0) goto L_0x004f
            com.android.server.am.ProcessRecord r11 = r1.curApp
            if (r11 == 0) goto L_0x004f
            com.android.server.am.BroadcastConstants r11 = r0.mConstants
            long r11 = r11.ALLOW_BG_ACTIVITY_START_TIMEOUT
            int r11 = (r7 > r11 ? 1 : (r7 == r11 ? 0 : -1))
            if (r11 <= 0) goto L_0x004a
            com.android.server.am.ProcessRecord r11 = r1.curApp
            r11.removeAllowBackgroundActivityStartsToken(r1)
            goto L_0x004f
        L_0x004a:
            com.android.server.am.ProcessRecord r11 = r1.curApp
            r0.postActivityStartTokenRemoval(r11, r1)
        L_0x004f:
            int r11 = r1.nextReceiver
            r12 = 1
            if (r11 <= 0) goto L_0x005b
            long[] r11 = r1.duration
            int r13 = r1.nextReceiver
            int r13 = r13 - r12
            r11[r13] = r7
        L_0x005b:
            boolean r11 = r1.timeoutExempt
            if (r11 != 0) goto L_0x009d
            com.android.server.am.BroadcastConstants r11 = r0.mConstants
            long r13 = r11.SLOW_TIME
            r15 = 0
            int r11 = (r13 > r15 ? 1 : (r13 == r15 ? 0 : -1))
            if (r11 <= 0) goto L_0x009d
            com.android.server.am.BroadcastConstants r11 = r0.mConstants
            long r13 = r11.SLOW_TIME
            int r11 = (r7 > r13 ? 1 : (r7 == r13 ? 0 : -1))
            if (r11 <= 0) goto L_0x009d
            com.android.server.am.ProcessRecord r11 = r1.curApp
            if (r11 == 0) goto L_0x0089
            com.android.server.am.ProcessRecord r11 = r1.curApp
            int r11 = r11.uid
            boolean r11 = android.os.UserHandle.isCore(r11)
            if (r11 != 0) goto L_0x009d
            com.android.server.am.BroadcastDispatcher r11 = r0.mDispatcher
            com.android.server.am.ProcessRecord r13 = r1.curApp
            int r13 = r13.uid
            r11.startDeferring(r13)
            goto L_0x009d
        L_0x0089:
            java.lang.StringBuilder r11 = new java.lang.StringBuilder
            r11.<init>()
            java.lang.String r13 = "finish receiver curApp is null? "
            r11.append(r13)
            r11.append(r1)
            java.lang.String r11 = r11.toString()
            android.util.Slog.d(r10, r11)
        L_0x009d:
            r11 = 0
            r1.receiver = r11
            android.content.Intent r13 = r1.intent
            r13.setComponent(r11)
            com.android.server.am.ProcessRecord r13 = r1.curApp
            if (r13 == 0) goto L_0x00ba
            com.android.server.am.ProcessRecord r13 = r1.curApp
            android.util.ArraySet<com.android.server.am.BroadcastRecord> r13 = r13.curReceivers
            boolean r13 = r13.contains(r1)
            if (r13 == 0) goto L_0x00ba
            com.android.server.am.ProcessRecord r13 = r1.curApp
            android.util.ArraySet<com.android.server.am.BroadcastRecord> r13 = r13.curReceivers
            r13.remove(r1)
        L_0x00ba:
            com.android.server.am.BroadcastFilter r13 = r1.curFilter
            if (r13 == 0) goto L_0x00c4
            com.android.server.am.BroadcastFilter r13 = r1.curFilter
            com.android.server.am.ReceiverList r13 = r13.receiverList
            r13.curBroadcast = r11
        L_0x00c4:
            r1.curFilter = r11
            r1.curReceiver = r11
            r1.curApp = r11
            r0.mPendingBroadcast = r11
            r13 = r20
            r1.resultCode = r13
            r14 = r21
            r1.resultData = r14
            r15 = r22
            r1.resultExtras = r15
            if (r2 == 0) goto L_0x00e9
            android.content.Intent r12 = r1.intent
            int r12 = r12.getFlags()
            r17 = 134217728(0x8000000, float:3.85186E-34)
            r12 = r12 & r17
            if (r12 != 0) goto L_0x00e9
            r1.resultAbort = r2
            goto L_0x00eb
        L_0x00e9:
            r1.resultAbort = r9
        L_0x00eb:
            if (r24 == 0) goto L_0x0166
            android.content.ComponentName r12 = r1.curComponent
            if (r12 == 0) goto L_0x0166
            com.android.server.am.BroadcastQueue r12 = r1.queue
            boolean r12 = r12.mDelayBehindServices
            if (r12 == 0) goto L_0x0166
            com.android.server.am.BroadcastQueue r12 = r1.queue
            com.android.server.am.BroadcastDispatcher r12 = r12.mDispatcher
            com.android.server.am.BroadcastRecord r12 = r12.getActiveBroadcastLocked()
            if (r12 != r1) goto L_0x0166
            int r12 = r1.nextReceiver
            java.util.List r11 = r1.receivers
            int r11 = r11.size()
            if (r12 >= r11) goto L_0x011e
            java.util.List r11 = r1.receivers
            int r12 = r1.nextReceiver
            java.lang.Object r11 = r11.get(r12)
            boolean r12 = r11 instanceof android.content.pm.ActivityInfo
            if (r12 == 0) goto L_0x011b
            r12 = r11
            android.content.pm.ActivityInfo r12 = (android.content.pm.ActivityInfo) r12
            goto L_0x011c
        L_0x011b:
            r12 = 0
        L_0x011c:
            r11 = r12
            goto L_0x011f
        L_0x011e:
            r11 = 0
        L_0x011f:
            if (r4 == 0) goto L_0x013a
            if (r11 == 0) goto L_0x013a
            android.content.pm.ApplicationInfo r12 = r4.applicationInfo
            int r12 = r12.uid
            android.content.pm.ApplicationInfo r9 = r11.applicationInfo
            int r9 = r9.uid
            if (r12 != r9) goto L_0x013a
            java.lang.String r9 = r4.processName
            java.lang.String r12 = r11.processName
            boolean r9 = r9.equals(r12)
            if (r9 != 0) goto L_0x0138
            goto L_0x013a
        L_0x0138:
            r9 = 0
            goto L_0x0166
        L_0x013a:
            com.android.server.am.ActivityManagerService r9 = r0.mService
            com.android.server.am.ActiveServices r9 = r9.mServices
            int r12 = r1.userId
            boolean r9 = r9.hasBackgroundServicesLocked(r12)
            if (r9 == 0) goto L_0x0165
            java.lang.StringBuilder r9 = new java.lang.StringBuilder
            r9.<init>()
            java.lang.String r12 = "Delay finish: "
            r9.append(r12)
            android.content.ComponentName r12 = r1.curComponent
            java.lang.String r12 = r12.flattenToShortString()
            r9.append(r12)
            java.lang.String r9 = r9.toString()
            android.util.Slog.i(r10, r9)
            r9 = 4
            r1.state = r9
            r9 = 0
            return r9
        L_0x0165:
            r9 = 0
        L_0x0166:
            r10 = 0
            r1.curComponent = r10
            r10 = 1
            if (r3 == r10) goto L_0x016f
            r11 = 3
            if (r3 != r11) goto L_0x0170
        L_0x016f:
            r9 = r10
        L_0x0170:
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.BroadcastQueue.finishReceiverLocked(com.android.server.am.BroadcastRecord, int, java.lang.String, android.os.Bundle, boolean, boolean):boolean");
    }

    public void backgroundServicesFinishedLocked(int userId) {
        BroadcastRecord br = this.mDispatcher.getActiveBroadcastLocked();
        if (br != null && br.userId == userId && br.state == 4) {
            Slog.i("BroadcastQueue", "Resuming delayed broadcast");
            br.curComponent = null;
            br.state = 0;
            processNextBroadcast(false);
        }
    }

    /* access modifiers changed from: package-private */
    public void performReceiveLocked(ProcessRecord app, IIntentReceiver receiver, Intent intent, int resultCode, String data, Bundle extras, boolean ordered, boolean sticky, int sendingUser) throws RemoteException {
        ProcessRecord processRecord = app;
        if (processRecord == null) {
            receiver.performReceive(intent, resultCode, data, extras, ordered, sticky, sendingUser);
        } else if (processRecord.thread != null) {
            try {
                processRecord.thread.scheduleRegisteredReceiver(receiver, intent, resultCode, data, extras, ordered, sticky, sendingUser, app.getReportedProcState());
            } catch (RemoteException e) {
                RemoteException ex = e;
                synchronized (this.mService) {
                    ActivityManagerService.boostPriorityForLockedSection();
                    Slog.w("BroadcastQueue", "Can't deliver broadcast to " + processRecord.processName + " (pid " + processRecord.pid + "). Crashing it.");
                    app.scheduleCrash("can't deliver broadcast");
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw ex;
                }
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        } else {
            throw new RemoteException("app.thread must not be null");
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:101:0x04c4  */
    /* JADX WARNING: Removed duplicated region for block: B:56:0x030d  */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x0383  */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x0391  */
    /* JADX WARNING: Removed duplicated region for block: B:67:0x03a0  */
    /* JADX WARNING: Removed duplicated region for block: B:78:0x03f4  */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x03f6  */
    /* JADX WARNING: Removed duplicated region for block: B:95:0x046b  */
    /* JADX WARNING: Removed duplicated region for block: B:96:0x04bb  */
    /* JADX WARNING: Removed duplicated region for block: B:99:0x04bf  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void deliverToRegisteredReceiverLocked(com.android.server.am.BroadcastRecord r22, com.android.server.am.BroadcastFilter r23, boolean r24, int r25) {
        /*
            r21 = this;
            r11 = r21
            r12 = r22
            r13 = r23
            r0 = 0
            com.android.server.am.ActivityManagerService r1 = r11.mService
            java.lang.String r2 = r12.callerPackage
            int r3 = r12.callingUid
            java.lang.String r4 = r13.packageName
            int r5 = r13.owningUid
            boolean r1 = r1.validateAssociationAllowedLocked(r2, r3, r4, r5)
            java.lang.String r2 = " through "
            java.lang.String r3 = ") to "
            java.lang.String r4 = " from "
            java.lang.String r7 = ", uid="
            java.lang.String r8 = " (pid="
            java.lang.String r14 = "BroadcastQueue"
            if (r1 != 0) goto L_0x0064
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r5 = "Association not allowed: broadcasting "
            r1.append(r5)
            android.content.Intent r5 = r12.intent
            java.lang.String r5 = r5.toString()
            r1.append(r5)
            r1.append(r4)
            java.lang.String r5 = r12.callerPackage
            r1.append(r5)
            r1.append(r8)
            int r5 = r12.callingPid
            r1.append(r5)
            r1.append(r7)
            int r5 = r12.callingUid
            r1.append(r5)
            r1.append(r3)
            java.lang.String r5 = r13.packageName
            r1.append(r5)
            r1.append(r2)
            r1.append(r13)
            java.lang.String r1 = r1.toString()
            android.util.Slog.w(r14, r1)
            r0 = 1
        L_0x0064:
            if (r0 != 0) goto L_0x00c7
            com.android.server.am.ActivityManagerService r1 = r11.mService
            com.android.server.firewall.IntentFirewall r15 = r1.mIntentFirewall
            android.content.Intent r1 = r12.intent
            int r5 = r12.callingUid
            int r6 = r12.callingPid
            java.lang.String r9 = r12.resolvedType
            com.android.server.am.ReceiverList r10 = r13.receiverList
            int r10 = r10.uid
            r16 = r1
            r17 = r5
            r18 = r6
            r19 = r9
            r20 = r10
            boolean r1 = r15.checkBroadcast(r16, r17, r18, r19, r20)
            if (r1 != 0) goto L_0x00c7
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r5 = "Firewall blocked: broadcasting "
            r1.append(r5)
            android.content.Intent r5 = r12.intent
            java.lang.String r5 = r5.toString()
            r1.append(r5)
            r1.append(r4)
            java.lang.String r5 = r12.callerPackage
            r1.append(r5)
            r1.append(r8)
            int r5 = r12.callingPid
            r1.append(r5)
            r1.append(r7)
            int r5 = r12.callingUid
            r1.append(r5)
            r1.append(r3)
            java.lang.String r3 = r13.packageName
            r1.append(r3)
            r1.append(r2)
            r1.append(r13)
            java.lang.String r1 = r1.toString()
            android.util.Slog.w(r14, r1)
            r0 = 1
        L_0x00c7:
            java.lang.String r1 = r13.requiredPermission
            java.lang.String r9 = ") requires appop "
            r10 = 1
            r15 = -1
            if (r1 == 0) goto L_0x0180
            com.android.server.am.ActivityManagerService r1 = r11.mService
            java.lang.String r1 = r13.requiredPermission
            int r2 = r12.callingPid
            int r3 = r12.callingUid
            int r1 = com.android.server.am.ActivityManagerService.checkComponentPermission(r1, r2, r3, r15, r10)
            if (r1 == 0) goto L_0x0123
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Permission Denial: broadcasting "
            r2.append(r3)
            android.content.Intent r3 = r12.intent
            java.lang.String r3 = r3.toString()
            r2.append(r3)
            r2.append(r4)
            java.lang.String r3 = r12.callerPackage
            r2.append(r3)
            r2.append(r8)
            int r3 = r12.callingPid
            r2.append(r3)
            r2.append(r7)
            int r3 = r12.callingUid
            r2.append(r3)
            java.lang.String r3 = ") requires "
            r2.append(r3)
            java.lang.String r3 = r13.requiredPermission
            r2.append(r3)
            java.lang.String r3 = " due to registered receiver "
            r2.append(r3)
            r2.append(r13)
            java.lang.String r2 = r2.toString()
            android.util.Slog.w(r14, r2)
            r0 = 1
            goto L_0x0180
        L_0x0123:
            java.lang.String r2 = r13.requiredPermission
            int r2 = android.app.AppOpsManager.permissionToOpCode(r2)
            if (r2 == r15) goto L_0x0180
            com.android.server.am.ActivityManagerService r3 = r11.mService
            com.android.server.appop.AppOpsService r3 = r3.mAppOpsService
            int r5 = r12.callingUid
            java.lang.String r6 = r12.callerPackage
            int r3 = r3.noteOperation(r2, r5, r6)
            if (r3 == 0) goto L_0x0180
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r5 = "Appop Denial: broadcasting "
            r3.append(r5)
            android.content.Intent r5 = r12.intent
            java.lang.String r5 = r5.toString()
            r3.append(r5)
            r3.append(r4)
            java.lang.String r4 = r12.callerPackage
            r3.append(r4)
            r3.append(r8)
            int r4 = r12.callingPid
            r3.append(r4)
            r3.append(r7)
            int r4 = r12.callingUid
            r3.append(r4)
            r3.append(r9)
            java.lang.String r4 = r13.requiredPermission
            java.lang.String r4 = android.app.AppOpsManager.permissionToOp(r4)
            r3.append(r4)
            java.lang.String r4 = " due to registered receiver "
            r3.append(r4)
            r3.append(r13)
            java.lang.String r3 = r3.toString()
            android.util.Slog.w(r14, r3)
            r0 = 1
        L_0x0180:
            java.lang.String r6 = " due to sender "
            java.lang.String r5 = ")"
            java.lang.String r4 = " (uid "
            java.lang.String r3 = " to "
            if (r0 != 0) goto L_0x028b
            java.lang.String[] r1 = r12.requiredPermissions
            if (r1 == 0) goto L_0x028b
            java.lang.String[] r1 = r12.requiredPermissions
            int r1 = r1.length
            if (r1 <= 0) goto L_0x028b
            r1 = 0
        L_0x0194:
            java.lang.String[] r2 = r12.requiredPermissions
            int r2 = r2.length
            if (r1 >= r2) goto L_0x0286
            java.lang.String[] r2 = r12.requiredPermissions
            r2 = r2[r1]
            com.android.server.am.ActivityManagerService r10 = r11.mService
            com.android.server.am.ReceiverList r10 = r13.receiverList
            int r10 = r10.pid
            r17 = r0
            com.android.server.am.ReceiverList r0 = r13.receiverList
            int r0 = r0.uid
            r18 = r1
            r1 = 1
            int r0 = com.android.server.am.ActivityManagerService.checkComponentPermission(r2, r10, r0, r15, r1)
            if (r0 == 0) goto L_0x0209
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r10 = "Permission Denial: receiving "
            r1.append(r10)
            android.content.Intent r10 = r12.intent
            java.lang.String r10 = r10.toString()
            r1.append(r10)
            r1.append(r3)
            com.android.server.am.ReceiverList r10 = r13.receiverList
            com.android.server.am.ProcessRecord r10 = r10.app
            r1.append(r10)
            r1.append(r8)
            com.android.server.am.ReceiverList r10 = r13.receiverList
            int r10 = r10.pid
            r1.append(r10)
            r1.append(r7)
            com.android.server.am.ReceiverList r10 = r13.receiverList
            int r10 = r10.uid
            r1.append(r10)
            java.lang.String r10 = ") requires "
            r1.append(r10)
            r1.append(r2)
            r1.append(r6)
            java.lang.String r10 = r12.callerPackage
            r1.append(r10)
            r1.append(r4)
            int r10 = r12.callingUid
            r1.append(r10)
            r1.append(r5)
            java.lang.String r1 = r1.toString()
            android.util.Slog.w(r14, r1)
            r1 = 1
            r0 = r1
            goto L_0x028f
        L_0x0209:
            int r1 = android.app.AppOpsManager.permissionToOpCode(r2)
            if (r1 == r15) goto L_0x027c
            int r10 = r12.appOp
            if (r1 == r10) goto L_0x027c
            com.android.server.am.ActivityManagerService r10 = r11.mService
            com.android.server.appop.AppOpsService r10 = r10.mAppOpsService
            com.android.server.am.ReceiverList r15 = r13.receiverList
            int r15 = r15.uid
            r20 = r0
            java.lang.String r0 = r13.packageName
            int r0 = r10.checkOperation(r1, r15, r0)
            if (r0 == 0) goto L_0x027e
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r10 = "Appop Denial: receiving "
            r0.append(r10)
            android.content.Intent r10 = r12.intent
            java.lang.String r10 = r10.toString()
            r0.append(r10)
            r0.append(r3)
            com.android.server.am.ReceiverList r10 = r13.receiverList
            com.android.server.am.ProcessRecord r10 = r10.app
            r0.append(r10)
            r0.append(r8)
            com.android.server.am.ReceiverList r10 = r13.receiverList
            int r10 = r10.pid
            r0.append(r10)
            r0.append(r7)
            com.android.server.am.ReceiverList r10 = r13.receiverList
            int r10 = r10.uid
            r0.append(r10)
            r0.append(r9)
            java.lang.String r10 = android.app.AppOpsManager.permissionToOp(r2)
            r0.append(r10)
            r0.append(r6)
            java.lang.String r10 = r12.callerPackage
            r0.append(r10)
            r0.append(r4)
            int r10 = r12.callingUid
            r0.append(r10)
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            android.util.Slog.w(r14, r0)
            r0 = 1
            goto L_0x028f
        L_0x027c:
            r20 = r0
        L_0x027e:
            int r1 = r18 + 1
            r0 = r17
            r10 = 1
            r15 = -1
            goto L_0x0194
        L_0x0286:
            r17 = r0
            r18 = r1
            goto L_0x028d
        L_0x028b:
            r17 = r0
        L_0x028d:
            r0 = r17
        L_0x028f:
            r15 = 0
            if (r0 != 0) goto L_0x0302
            java.lang.String[] r1 = r12.requiredPermissions
            if (r1 == 0) goto L_0x029f
            java.lang.String[] r1 = r12.requiredPermissions
            int r1 = r1.length
            if (r1 != 0) goto L_0x029c
            goto L_0x029f
        L_0x029c:
            r17 = r0
            goto L_0x0304
        L_0x029f:
            com.android.server.am.ActivityManagerService r1 = r11.mService
            com.android.server.am.ReceiverList r1 = r13.receiverList
            int r1 = r1.pid
            com.android.server.am.ReceiverList r2 = r13.receiverList
            int r2 = r2.uid
            r17 = r0
            r0 = -1
            r10 = 1
            int r1 = com.android.server.am.ActivityManagerService.checkComponentPermission(r15, r1, r2, r0, r10)
            if (r1 == 0) goto L_0x0304
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r2 = "Permission Denial: security check failed when receiving "
            r0.append(r2)
            android.content.Intent r2 = r12.intent
            java.lang.String r2 = r2.toString()
            r0.append(r2)
            r0.append(r3)
            com.android.server.am.ReceiverList r2 = r13.receiverList
            com.android.server.am.ProcessRecord r2 = r2.app
            r0.append(r2)
            r0.append(r8)
            com.android.server.am.ReceiverList r2 = r13.receiverList
            int r2 = r2.pid
            r0.append(r2)
            r0.append(r7)
            com.android.server.am.ReceiverList r2 = r13.receiverList
            int r2 = r2.uid
            r0.append(r2)
            java.lang.String r2 = ") due to sender "
            r0.append(r2)
            java.lang.String r2 = r12.callerPackage
            r0.append(r2)
            r0.append(r4)
            int r2 = r12.callingUid
            r0.append(r2)
            r0.append(r5)
            java.lang.String r0 = r0.toString()
            android.util.Slog.w(r14, r0)
            r0 = 1
            goto L_0x0306
        L_0x0302:
            r17 = r0
        L_0x0304:
            r0 = r17
        L_0x0306:
            if (r0 != 0) goto L_0x0383
            int r1 = r12.appOp
            r2 = -1
            if (r1 == r2) goto L_0x0383
            int r1 = r12.appOp
            com.android.server.am.ReceiverList r2 = r13.receiverList
            int r2 = r2.uid
            java.lang.String r10 = r13.packageName
            com.android.server.am.BroadcastQueue$BroadcastHandler r15 = r11.mHandler
            r18 = r0
            com.android.server.am.ActivityManagerService r0 = r11.mService
            r11 = r3
            r3 = r10
            r10 = r4
            r4 = r15
            r15 = r5
            r5 = r0
            r0 = r6
            r6 = r22
            int r1 = com.android.server.am.BroadcastQueueInjector.noteOperationLocked(r1, r2, r3, r4, r5, r6)
            if (r1 == 0) goto L_0x0388
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Appop Denial: receiving "
            r1.append(r2)
            android.content.Intent r2 = r12.intent
            java.lang.String r2 = r2.toString()
            r1.append(r2)
            r1.append(r11)
            com.android.server.am.ReceiverList r2 = r13.receiverList
            com.android.server.am.ProcessRecord r2 = r2.app
            r1.append(r2)
            r1.append(r8)
            com.android.server.am.ReceiverList r2 = r13.receiverList
            int r2 = r2.pid
            r1.append(r2)
            r1.append(r7)
            com.android.server.am.ReceiverList r2 = r13.receiverList
            int r2 = r2.uid
            r1.append(r2)
            r1.append(r9)
            int r2 = r12.appOp
            java.lang.String r2 = android.app.AppOpsManager.opToName(r2)
            r1.append(r2)
            r1.append(r0)
            java.lang.String r0 = r12.callerPackage
            r1.append(r0)
            r1.append(r10)
            int r0 = r12.callingUid
            r1.append(r0)
            r1.append(r15)
            java.lang.String r0 = r1.toString()
            android.util.Slog.w(r14, r0)
            r0 = 1
            goto L_0x038a
        L_0x0383:
            r18 = r0
            r11 = r3
            r10 = r4
            r15 = r5
        L_0x0388:
            r0 = r18
        L_0x038a:
            if (r0 != 0) goto L_0x03a0
            int r1 = r12.appOp
            r2 = -1
            if (r1 == r2) goto L_0x03a0
            r1 = r11
            r11 = r21
            com.android.server.am.ActivityManagerService r2 = r11.mService
            int r3 = r12.appOp
            boolean r2 = com.android.server.am.BroadcastQueueInjector.isSkip((com.android.server.am.ActivityManagerService) r2, (com.android.server.am.BroadcastRecord) r12, (com.android.server.am.BroadcastFilter) r13, (int) r3)
            if (r2 == 0) goto L_0x03a3
            r0 = 1
            goto L_0x03a3
        L_0x03a0:
            r1 = r11
            r11 = r21
        L_0x03a3:
            if (r0 != 0) goto L_0x03e9
            com.android.server.am.ReceiverList r2 = r13.receiverList
            com.android.server.am.ProcessRecord r2 = r2.app
            if (r2 == 0) goto L_0x03bd
            com.android.server.am.ReceiverList r2 = r13.receiverList
            com.android.server.am.ProcessRecord r2 = r2.app
            boolean r2 = r2.killed
            if (r2 != 0) goto L_0x03bd
            com.android.server.am.ReceiverList r2 = r13.receiverList
            com.android.server.am.ProcessRecord r2 = r2.app
            boolean r2 = r2.isCrashing()
            if (r2 == 0) goto L_0x03e9
        L_0x03bd:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Skipping deliver ["
            r2.append(r3)
            java.lang.String r3 = r11.mQueueName
            r2.append(r3)
            java.lang.String r3 = "] "
            r2.append(r3)
            r2.append(r12)
            r2.append(r1)
            com.android.server.am.ReceiverList r3 = r13.receiverList
            r2.append(r3)
            java.lang.String r3 = ": process gone or crashing"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            android.util.Slog.w(r14, r2)
            r0 = 1
        L_0x03e9:
            android.content.Intent r2 = r12.intent
            int r2 = r2.getFlags()
            r3 = 2097152(0x200000, float:2.938736E-39)
            r2 = r2 & r3
            if (r2 == 0) goto L_0x03f6
            r2 = 1
            goto L_0x03f7
        L_0x03f6:
            r2 = 0
        L_0x03f7:
            r18 = r2
            if (r0 != 0) goto L_0x0459
            if (r18 != 0) goto L_0x0459
            boolean r2 = r13.instantApp
            if (r2 == 0) goto L_0x0459
            com.android.server.am.ReceiverList r2 = r13.receiverList
            int r2 = r2.uid
            int r3 = r12.callingUid
            if (r2 == r3) goto L_0x0459
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Instant App Denial: receiving "
            r2.append(r3)
            android.content.Intent r3 = r12.intent
            java.lang.String r3 = r3.toString()
            r2.append(r3)
            r2.append(r1)
            com.android.server.am.ReceiverList r3 = r13.receiverList
            com.android.server.am.ProcessRecord r3 = r3.app
            r2.append(r3)
            r2.append(r8)
            com.android.server.am.ReceiverList r3 = r13.receiverList
            int r3 = r3.pid
            r2.append(r3)
            r2.append(r7)
            com.android.server.am.ReceiverList r3 = r13.receiverList
            int r3 = r3.uid
            r2.append(r3)
            java.lang.String r3 = ") due to sender "
            r2.append(r3)
            java.lang.String r3 = r12.callerPackage
            r2.append(r3)
            r2.append(r10)
            int r3 = r12.callingUid
            r2.append(r3)
            java.lang.String r3 = ") not specifying FLAG_RECEIVER_VISIBLE_TO_INSTANT_APPS"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            android.util.Slog.w(r14, r2)
            r0 = 1
        L_0x0459:
            if (r0 != 0) goto L_0x04bb
            boolean r2 = r13.visibleToInstantApp
            if (r2 != 0) goto L_0x04bb
            boolean r2 = r12.callerInstantApp
            if (r2 == 0) goto L_0x04bb
            com.android.server.am.ReceiverList r2 = r13.receiverList
            int r2 = r2.uid
            int r3 = r12.callingUid
            if (r2 == r3) goto L_0x04bb
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Instant App Denial: receiving "
            r2.append(r3)
            android.content.Intent r3 = r12.intent
            java.lang.String r3 = r3.toString()
            r2.append(r3)
            r2.append(r1)
            com.android.server.am.ReceiverList r1 = r13.receiverList
            com.android.server.am.ProcessRecord r1 = r1.app
            r2.append(r1)
            r2.append(r8)
            com.android.server.am.ReceiverList r1 = r13.receiverList
            int r1 = r1.pid
            r2.append(r1)
            r2.append(r7)
            com.android.server.am.ReceiverList r1 = r13.receiverList
            int r1 = r1.uid
            r2.append(r1)
            java.lang.String r1 = ") requires receiver be visible to instant apps due to sender "
            r2.append(r1)
            java.lang.String r1 = r12.callerPackage
            r2.append(r1)
            r2.append(r10)
            int r1 = r12.callingUid
            r2.append(r1)
            r2.append(r15)
            java.lang.String r1 = r2.toString()
            android.util.Slog.w(r14, r1)
            r0 = 1
            r15 = r0
            goto L_0x04bc
        L_0x04bb:
            r15 = r0
        L_0x04bc:
            r0 = 2
            if (r15 == 0) goto L_0x04c4
            int[] r1 = r12.delivery
            r1[r25] = r0
            return
        L_0x04c4:
            java.lang.String r1 = r13.packageName
            int r2 = r13.owningUserId
            boolean r1 = r11.requestStartTargetPermissionsReviewIfNeededLocked(r12, r1, r2)
            if (r1 != 0) goto L_0x04d3
            int[] r1 = r12.delivery
            r1[r25] = r0
            return
        L_0x04d3:
            int[] r1 = r12.delivery
            r2 = 1
            r1[r25] = r2
            if (r24 == 0) goto L_0x050c
            com.android.server.am.ReceiverList r1 = r13.receiverList
            android.content.IIntentReceiver r1 = r1.receiver
            android.os.IBinder r1 = r1.asBinder()
            r12.receiver = r1
            r12.curFilter = r13
            com.android.server.am.ReceiverList r1 = r13.receiverList
            r1.curBroadcast = r12
            r12.state = r0
            com.android.server.am.ReceiverList r0 = r13.receiverList
            com.android.server.am.ProcessRecord r0 = r0.app
            if (r0 == 0) goto L_0x050c
            com.android.server.am.ReceiverList r0 = r13.receiverList
            com.android.server.am.ProcessRecord r0 = r0.app
            r12.curApp = r0
            com.android.server.am.ReceiverList r0 = r13.receiverList
            com.android.server.am.ProcessRecord r0 = r0.app
            android.util.ArraySet<com.android.server.am.BroadcastRecord> r0 = r0.curReceivers
            r0.add(r12)
            com.android.server.am.ActivityManagerService r0 = r11.mService
            com.android.server.am.ProcessRecord r1 = r12.curApp
            java.lang.String r2 = "updateOomAdj_startReceiver"
            r3 = 1
            r0.updateOomAdjLocked(r1, r3, r2)
        L_0x050c:
            com.android.server.am.ActivityManagerService r0 = r11.mService
            com.android.server.am.ReceiverList r1 = r13.receiverList
            com.android.server.am.ProcessRecord r1 = r1.app
            r2 = 0
            boolean r0 = com.android.server.am.BroadcastQueueInjector.checkReceiverAppDealBroadcast(r11, r0, r12, r1, r2)
            if (r0 != 0) goto L_0x051a
            return
        L_0x051a:
            com.android.server.am.ReceiverList r0 = r13.receiverList     // Catch:{ RemoteException -> 0x0570 }
            com.android.server.am.ProcessRecord r0 = r0.app     // Catch:{ RemoteException -> 0x0570 }
            if (r0 == 0) goto L_0x052e
            com.android.server.am.ReceiverList r0 = r13.receiverList     // Catch:{ RemoteException -> 0x0570 }
            com.android.server.am.ProcessRecord r0 = r0.app     // Catch:{ RemoteException -> 0x0570 }
            boolean r0 = r0.inFullBackup     // Catch:{ RemoteException -> 0x0570 }
            if (r0 == 0) goto L_0x052e
            if (r24 == 0) goto L_0x056a
            r21.skipReceiverLocked(r22)     // Catch:{ RemoteException -> 0x0570 }
            goto L_0x056a
        L_0x052e:
            long r0 = android.os.SystemClock.uptimeMillis()     // Catch:{ RemoteException -> 0x0570 }
            r12.receiverTime = r0     // Catch:{ RemoteException -> 0x0570 }
            com.android.server.am.ReceiverList r0 = r13.receiverList     // Catch:{ RemoteException -> 0x0570 }
            com.android.server.am.ProcessRecord r0 = r0.app     // Catch:{ RemoteException -> 0x0570 }
            r11.maybeAddAllowBackgroundActivityStartsToken(r0, r12)     // Catch:{ RemoteException -> 0x0570 }
            com.android.server.am.ReceiverList r0 = r13.receiverList     // Catch:{ RemoteException -> 0x0570 }
            com.android.server.am.ProcessRecord r2 = r0.app     // Catch:{ RemoteException -> 0x0570 }
            com.android.server.am.ReceiverList r0 = r13.receiverList     // Catch:{ RemoteException -> 0x0570 }
            android.content.IIntentReceiver r3 = r0.receiver     // Catch:{ RemoteException -> 0x0570 }
            android.content.Intent r4 = new android.content.Intent     // Catch:{ RemoteException -> 0x0570 }
            android.content.Intent r0 = r12.intent     // Catch:{ RemoteException -> 0x0570 }
            r4.<init>(r0)     // Catch:{ RemoteException -> 0x0570 }
            int r5 = r12.resultCode     // Catch:{ RemoteException -> 0x0570 }
            java.lang.String r6 = r12.resultData     // Catch:{ RemoteException -> 0x0570 }
            android.os.Bundle r7 = r12.resultExtras     // Catch:{ RemoteException -> 0x0570 }
            boolean r8 = r12.ordered     // Catch:{ RemoteException -> 0x0570 }
            boolean r9 = r12.initialSticky     // Catch:{ RemoteException -> 0x0570 }
            int r10 = r12.userId     // Catch:{ RemoteException -> 0x0570 }
            r1 = r21
            r1.performReceiveLocked(r2, r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ RemoteException -> 0x0570 }
            boolean r0 = r12.allowBackgroundActivityStarts     // Catch:{ RemoteException -> 0x0570 }
            if (r0 == 0) goto L_0x056a
            boolean r0 = r12.ordered     // Catch:{ RemoteException -> 0x0570 }
            if (r0 != 0) goto L_0x056a
            com.android.server.am.ReceiverList r0 = r13.receiverList     // Catch:{ RemoteException -> 0x0570 }
            com.android.server.am.ProcessRecord r0 = r0.app     // Catch:{ RemoteException -> 0x0570 }
            r11.postActivityStartTokenRemoval(r0, r12)     // Catch:{ RemoteException -> 0x0570 }
        L_0x056a:
            if (r24 == 0) goto L_0x056f
            r0 = 3
            r12.state = r0     // Catch:{ RemoteException -> 0x0570 }
        L_0x056f:
            goto L_0x05aa
        L_0x0570:
            r0 = move-exception
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Failure sending broadcast "
            r1.append(r2)
            android.content.Intent r2 = r12.intent
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            android.util.Slog.w(r14, r1, r0)
            com.android.server.am.ReceiverList r1 = r13.receiverList
            com.android.server.am.ProcessRecord r1 = r1.app
            if (r1 == 0) goto L_0x059f
            com.android.server.am.ReceiverList r1 = r13.receiverList
            com.android.server.am.ProcessRecord r1 = r1.app
            r1.removeAllowBackgroundActivityStartsToken(r12)
            if (r24 == 0) goto L_0x059f
            com.android.server.am.ReceiverList r1 = r13.receiverList
            com.android.server.am.ProcessRecord r1 = r1.app
            android.util.ArraySet<com.android.server.am.BroadcastRecord> r1 = r1.curReceivers
            r1.remove(r12)
        L_0x059f:
            if (r24 == 0) goto L_0x05aa
            r1 = 0
            r12.receiver = r1
            r12.curFilter = r1
            com.android.server.am.ReceiverList r2 = r13.receiverList
            r2.curBroadcast = r1
        L_0x05aa:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.BroadcastQueue.deliverToRegisteredReceiverLocked(com.android.server.am.BroadcastRecord, com.android.server.am.BroadcastFilter, boolean, int):void");
    }

    private boolean requestStartTargetPermissionsReviewIfNeededLocked(BroadcastRecord receiverRecord, String receivingPackageName, int receivingUserId) {
        boolean callerForeground;
        BroadcastRecord broadcastRecord = receiverRecord;
        String str = receivingPackageName;
        final int i = receivingUserId;
        if (!this.mService.getPackageManagerInternalLocked().isPermissionsReviewRequired(str, i)) {
            return true;
        }
        if (broadcastRecord.callerApp != null) {
            callerForeground = broadcastRecord.callerApp.setSchedGroup != 0;
        } else {
            callerForeground = true;
        }
        if (!callerForeground || broadcastRecord.intent.getComponent() == null) {
            Slog.w("BroadcastQueue", "u" + i + " Receiving a broadcast in package" + str + " requires a permissions review");
        } else {
            IIntentSender target = this.mService.mPendingIntentController.getIntentSender(1, broadcastRecord.callerPackage, broadcastRecord.callingUid, broadcastRecord.userId, (IBinder) null, (String) null, 0, new Intent[]{broadcastRecord.intent}, new String[]{broadcastRecord.intent.resolveType(this.mService.mContext.getContentResolver())}, 1409286144, (Bundle) null);
            final Intent intent = new Intent("android.intent.action.REVIEW_PERMISSIONS");
            intent.addFlags(411041792);
            intent.putExtra("android.intent.extra.PACKAGE_NAME", str);
            intent.putExtra("android.intent.extra.INTENT", new IntentSender(target));
            this.mHandler.post(new Runnable() {
                public void run() {
                    BroadcastQueue.this.mService.mContext.startActivityAsUser(intent, new UserHandle(i));
                }
            });
        }
        return false;
    }

    /* access modifiers changed from: package-private */
    public final void scheduleTempWhitelistLocked(int uid, long duration, BroadcastRecord r) {
        if (duration > 2147483647L) {
            duration = 2147483647L;
        }
        StringBuilder b = new StringBuilder();
        b.append("broadcast:");
        UserHandle.formatUid(b, r.callingUid);
        b.append(":");
        if (r.intent.getAction() != null) {
            b.append(r.intent.getAction());
        } else if (r.intent.getComponent() != null) {
            r.intent.getComponent().appendShortString(b);
        } else if (r.intent.getData() != null) {
            b.append(r.intent.getData());
        }
        this.mService.tempWhitelistUidLocked(uid, duration, b.toString());
    }

    /* access modifiers changed from: package-private */
    public final boolean isSignaturePerm(String[] perms) {
        if (perms == null) {
            return false;
        }
        IPackageManager pm = AppGlobals.getPackageManager();
        int i = perms.length - 1;
        while (i >= 0) {
            try {
                PermissionInfo pi = pm.getPermissionInfo(perms[i], PackageManagerService.PLATFORM_PACKAGE_NAME, 0);
                if (pi == null || (pi.protectionLevel & 31) != 2) {
                    return false;
                }
                i--;
            } catch (RemoteException e) {
                return false;
            }
        }
        return true;
    }

    /* access modifiers changed from: package-private */
    public void processNextBroadcast(boolean fromMsg) {
        processNextBroadcast(fromMsg, false);
    }

    /* access modifiers changed from: package-private */
    public void processNextBroadcast(boolean fromMsg, boolean parallelOnce) {
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                processNextBroadcastLocked(fromMsg, false, parallelOnce);
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    /* access modifiers changed from: package-private */
    public final void processNextBroadcastLocked(boolean fromMsg, boolean skipOomAdj) {
        processNextBroadcastLocked(fromMsg, skipOomAdj, false);
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:338:0x0b02, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:339:0x0b03, code lost:
        r6 = r38;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:342:0x0b51, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:343:0x0b52, code lost:
        r21 = r1;
        r23 = r5;
        r22 = r10;
        r10 = r7;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:106:0x025e A[SYNTHETIC, Splitter:B:106:0x025e] */
    /* JADX WARNING: Removed duplicated region for block: B:120:0x02c2  */
    /* JADX WARNING: Removed duplicated region for block: B:305:0x0a26  */
    /* JADX WARNING: Removed duplicated region for block: B:306:0x0a57  */
    /* JADX WARNING: Removed duplicated region for block: B:308:0x0a5b  */
    /* JADX WARNING: Removed duplicated region for block: B:310:0x0a72  */
    /* JADX WARNING: Removed duplicated region for block: B:338:0x0b02 A[ExcHandler: RuntimeException (e java.lang.RuntimeException), Splitter:B:325:0x0ad9] */
    /* JADX WARNING: Removed duplicated region for block: B:348:0x0b81 A[RETURN] */
    /* JADX WARNING: Removed duplicated region for block: B:349:0x0b82  */
    /* JADX WARNING: Removed duplicated region for block: B:359:0x0c19 A[LOOP:2: B:46:0x00f8->B:359:0x0c19, LOOP_END] */
    /* JADX WARNING: Removed duplicated region for block: B:365:0x0311 A[SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void processNextBroadcastLocked(boolean r37, boolean r38, boolean r39) {
        /*
            r36 = this;
            r11 = r36
            com.android.server.am.ActivityManagerService r0 = r11.mService
            r0.updateCpuStats()
            r12 = 0
            if (r37 == 0) goto L_0x000c
            r11.mBroadcastsScheduled = r12
        L_0x000c:
            java.util.ArrayList<com.android.server.am.BroadcastRecord> r0 = r11.mParallelBroadcasts
            int r0 = r0.size()
            r13 = 64
            r15 = 1
            if (r0 <= 0) goto L_0x006b
            java.util.ArrayList<com.android.server.am.BroadcastRecord> r0 = r11.mParallelBroadcasts
            java.lang.Object r0 = r0.remove(r12)
            com.android.server.am.BroadcastRecord r0 = (com.android.server.am.BroadcastRecord) r0
            long r1 = android.os.SystemClock.uptimeMillis()
            r0.dispatchTime = r1
            long r1 = java.lang.System.currentTimeMillis()
            r0.dispatchClockTime = r1
            boolean r1 = android.os.Trace.isTagEnabled(r13)
            if (r1 == 0) goto L_0x0049
            java.lang.String r1 = r11.createBroadcastTraceTitle(r0, r12)
            int r2 = java.lang.System.identityHashCode(r0)
            android.os.Trace.asyncTraceEnd(r13, r1, r2)
            java.lang.String r1 = r11.createBroadcastTraceTitle(r0, r15)
            int r2 = java.lang.System.identityHashCode(r0)
            android.os.Trace.asyncTraceBegin(r13, r1, r2)
        L_0x0049:
            java.util.List r1 = r0.receivers
            int r1 = r1.size()
            r2 = 0
        L_0x0050:
            if (r2 >= r1) goto L_0x0061
            java.util.List r3 = r0.receivers
            java.lang.Object r3 = r3.get(r2)
            r4 = r3
            com.android.server.am.BroadcastFilter r4 = (com.android.server.am.BroadcastFilter) r4
            r11.deliverToRegisteredReceiverLocked(r0, r4, r12, r2)
            int r2 = r2 + 1
            goto L_0x0050
        L_0x0061:
            r11.addBroadcastToHistoryLocked(r0)
            if (r39 == 0) goto L_0x006a
            r36.scheduleBroadcastsLocked()
            return
        L_0x006a:
            goto L_0x000c
        L_0x006b:
            com.android.server.am.BroadcastRecord r0 = r11.mPendingBroadcast
            r10 = 0
            if (r0 == 0) goto L_0x00f5
            com.android.server.am.ProcessRecord r0 = r0.curApp
            int r0 = r0.pid
            if (r0 <= 0) goto L_0x009b
            com.android.server.am.ActivityManagerService r0 = r11.mService
            com.android.server.am.ActivityManagerService$PidMap r1 = r0.mPidsSelfLocked
            monitor-enter(r1)
            com.android.server.am.ActivityManagerService r0 = r11.mService     // Catch:{ all -> 0x0098 }
            com.android.server.am.ActivityManagerService$PidMap r0 = r0.mPidsSelfLocked     // Catch:{ all -> 0x0098 }
            com.android.server.am.BroadcastRecord r2 = r11.mPendingBroadcast     // Catch:{ all -> 0x0098 }
            com.android.server.am.ProcessRecord r2 = r2.curApp     // Catch:{ all -> 0x0098 }
            int r2 = r2.pid     // Catch:{ all -> 0x0098 }
            com.android.server.am.ProcessRecord r0 = r0.get(r2)     // Catch:{ all -> 0x0098 }
            if (r0 == 0) goto L_0x0094
            boolean r2 = r0.isCrashing()     // Catch:{ all -> 0x0098 }
            if (r2 == 0) goto L_0x0092
            goto L_0x0094
        L_0x0092:
            r2 = r12
            goto L_0x0095
        L_0x0094:
            r2 = r15
        L_0x0095:
            r0 = r2
            monitor-exit(r1)     // Catch:{ all -> 0x0098 }
            goto L_0x00be
        L_0x0098:
            r0 = move-exception
            monitor-exit(r1)     // Catch:{ all -> 0x0098 }
            throw r0
        L_0x009b:
            com.android.server.am.ActivityManagerService r0 = r11.mService
            com.android.server.am.ProcessList r0 = r0.mProcessList
            com.android.server.am.ProcessList$MyProcessMap r0 = r0.mProcessNames
            com.android.server.am.BroadcastRecord r1 = r11.mPendingBroadcast
            com.android.server.am.ProcessRecord r1 = r1.curApp
            java.lang.String r1 = r1.processName
            com.android.server.am.BroadcastRecord r2 = r11.mPendingBroadcast
            com.android.server.am.ProcessRecord r2 = r2.curApp
            int r2 = r2.uid
            java.lang.Object r0 = r0.get(r1, r2)
            com.android.server.am.ProcessRecord r0 = (com.android.server.am.ProcessRecord) r0
            if (r0 == 0) goto L_0x00bc
            boolean r1 = r0.pendingStart
            if (r1 != 0) goto L_0x00ba
            goto L_0x00bc
        L_0x00ba:
            r1 = r12
            goto L_0x00bd
        L_0x00bc:
            r1 = r15
        L_0x00bd:
            r0 = r1
        L_0x00be:
            if (r0 != 0) goto L_0x00c1
            return
        L_0x00c1:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "pending app  ["
            r1.append(r2)
            java.lang.String r2 = r11.mQueueName
            r1.append(r2)
            java.lang.String r2 = "]"
            r1.append(r2)
            com.android.server.am.BroadcastRecord r2 = r11.mPendingBroadcast
            com.android.server.am.ProcessRecord r2 = r2.curApp
            r1.append(r2)
            java.lang.String r2 = " died before responding to broadcast"
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "BroadcastQueue"
            android.util.Slog.w(r2, r1)
            com.android.server.am.BroadcastRecord r1 = r11.mPendingBroadcast
            r1.state = r12
            int r2 = r11.mPendingBroadcastRecvIndex
            r1.nextReceiver = r2
            r11.mPendingBroadcast = r10
        L_0x00f5:
            r0 = 0
            r16 = r0
        L_0x00f8:
            long r8 = android.os.SystemClock.uptimeMillis()
            com.android.server.am.BroadcastDispatcher r0 = r11.mDispatcher
            com.android.server.am.BroadcastRecord r7 = r0.getNextBroadcastLocked(r8)
            if (r7 != 0) goto L_0x0127
            com.android.server.am.BroadcastDispatcher r0 = r11.mDispatcher
            r0.scheduleDeferralCheckLocked(r12)
            com.android.server.am.ActivityManagerService r0 = r11.mService
            r0.scheduleAppGcsLocked()
            if (r16 == 0) goto L_0x0118
            com.android.server.am.ActivityManagerService r0 = r11.mService
            java.lang.String r1 = "updateOomAdj_startReceiver"
            r0.updateOomAdjLocked(r1)
        L_0x0118:
            com.android.server.am.ActivityManagerService r0 = r11.mService
            com.android.server.am.UserController r0 = r0.mUserController
            boolean r0 = r0.mBootCompleted
            if (r0 == 0) goto L_0x0126
            boolean r0 = r11.mLogLatencyMetrics
            if (r0 == 0) goto L_0x0126
            r11.mLogLatencyMetrics = r12
        L_0x0126:
            return
        L_0x0127:
            r0 = 0
            java.util.List r1 = r7.receivers
            if (r1 == 0) goto L_0x0133
            java.util.List r1 = r7.receivers
            int r1 = r1.size()
            goto L_0x0134
        L_0x0133:
            r1 = r12
        L_0x0134:
            r6 = r1
            com.android.server.am.ActivityManagerService r1 = r11.mService
            boolean r1 = r1.mProcessesReady
            r17 = 0
            if (r1 == 0) goto L_0x01bb
            boolean r1 = r7.timeoutExempt
            if (r1 != 0) goto L_0x01bb
            long r1 = r7.dispatchTime
            int r1 = (r1 > r17 ? 1 : (r1 == r17 ? 0 : -1))
            if (r1 <= 0) goto L_0x01bb
            if (r6 <= 0) goto L_0x01bb
            long r1 = r7.dispatchTime
            r3 = 2
            com.android.server.am.BroadcastConstants r5 = r11.mConstants
            long r13 = r5.TIMEOUT
            long r13 = r13 * r3
            long r3 = (long) r6
            long r13 = r13 * r3
            long r1 = r1 + r13
            int r1 = (r8 > r1 ? 1 : (r8 == r1 ? 0 : -1))
            if (r1 <= 0) goto L_0x01bb
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Hung broadcast ["
            r1.append(r2)
            java.lang.String r2 = r11.mQueueName
            r1.append(r2)
            java.lang.String r2 = "] discarded after timeout failure: now="
            r1.append(r2)
            r1.append(r8)
            java.lang.String r2 = " dispatchTime="
            r1.append(r2)
            long r2 = r7.dispatchTime
            r1.append(r2)
            java.lang.String r2 = " startTime="
            r1.append(r2)
            long r2 = r7.receiverTime
            r1.append(r2)
            java.lang.String r2 = " intent="
            r1.append(r2)
            android.content.Intent r2 = r7.intent
            r1.append(r2)
            java.lang.String r2 = " numReceivers="
            r1.append(r2)
            r1.append(r6)
            java.lang.String r2 = " nextReceiver="
            r1.append(r2)
            int r2 = r7.nextReceiver
            r1.append(r2)
            java.lang.String r2 = " state="
            r1.append(r2)
            int r2 = r7.state
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "BroadcastQueue"
            android.util.Slog.w(r2, r1)
            r11.broadcastTimeoutLocked(r12)
            r0 = 1
            r7.state = r12
            r13 = r0
            goto L_0x01bc
        L_0x01bb:
            r13 = r0
        L_0x01bc:
            int r0 = r7.state
            if (r0 == 0) goto L_0x01c1
            return
        L_0x01c1:
            java.util.List r0 = r7.receivers
            r14 = 2
            r20 = 1073741824(0x40000000, float:2.0)
            if (r0 == 0) goto L_0x0233
            int r0 = r7.nextReceiver
            if (r0 >= r6) goto L_0x0233
            boolean r0 = r7.resultAbort
            if (r0 != 0) goto L_0x0233
            if (r13 == 0) goto L_0x01d3
            goto L_0x0233
        L_0x01d3:
            boolean r0 = r7.deferred
            if (r0 != 0) goto L_0x022f
            java.util.List r0 = r7.receivers
            int r1 = r7.nextReceiver
            java.lang.Object r0 = r0.get(r1)
            int r0 = r7.getReceiverUid(r0)
            com.android.server.am.BroadcastDispatcher r1 = r11.mDispatcher
            boolean r1 = r1.isDeferringLocked(r0)
            if (r1 == 0) goto L_0x022f
            int r1 = r7.nextReceiver
            int r1 = r1 + r15
            if (r1 != r6) goto L_0x01f7
            r1 = r7
            com.android.server.am.BroadcastDispatcher r2 = r11.mDispatcher
            r2.retireBroadcastLocked(r7)
            goto L_0x0222
        L_0x01f7:
            int r1 = r7.nextReceiver
            com.android.server.am.BroadcastRecord r1 = r7.splitRecipientsLocked(r0, r1)
            android.content.IIntentReceiver r2 = r7.resultTo
            if (r2 == 0) goto L_0x0222
            int r2 = r7.splitToken
            if (r2 != 0) goto L_0x0215
            int r3 = r36.nextSplitTokenLocked()
            r1.splitToken = r3
            r7.splitToken = r3
            android.util.SparseIntArray r3 = r11.mSplitRefcounts
            int r4 = r7.splitToken
            r3.put(r4, r14)
            goto L_0x0222
        L_0x0215:
            android.util.SparseIntArray r3 = r11.mSplitRefcounts
            int r3 = r3.get(r2)
            android.util.SparseIntArray r4 = r11.mSplitRefcounts
            int r5 = r3 + 1
            r4.put(r2, r5)
        L_0x0222:
            com.android.server.am.BroadcastDispatcher r2 = r11.mDispatcher
            r2.addDeferredBroadcast(r0, r1)
            r2 = 0
            r3 = 1
            r8 = r2
            r16 = r3
            r15 = r10
            goto L_0x030f
        L_0x022f:
            r8 = r7
            r15 = r10
            goto L_0x030f
        L_0x0233:
            android.content.IIntentReceiver r0 = r7.resultTo
            if (r0 == 0) goto L_0x02c9
            r0 = 1
            int r1 = r7.splitToken
            if (r1 == 0) goto L_0x025a
            android.util.SparseIntArray r1 = r11.mSplitRefcounts
            int r2 = r7.splitToken
            int r1 = r1.get(r2)
            int r1 = r1 - r15
            if (r1 != 0) goto L_0x024f
            android.util.SparseIntArray r2 = r11.mSplitRefcounts
            int r3 = r7.splitToken
            r2.delete(r3)
            goto L_0x025a
        L_0x024f:
            r0 = 0
            android.util.SparseIntArray r2 = r11.mSplitRefcounts
            int r3 = r7.splitToken
            r2.put(r3, r1)
            r21 = r0
            goto L_0x025c
        L_0x025a:
            r21 = r0
        L_0x025c:
            if (r21 == 0) goto L_0x02c2
            com.android.server.am.ProcessRecord r2 = r7.callerApp     // Catch:{ RemoteException -> 0x0296 }
            android.content.IIntentReceiver r3 = r7.resultTo     // Catch:{ RemoteException -> 0x0296 }
            android.content.Intent r4 = new android.content.Intent     // Catch:{ RemoteException -> 0x0296 }
            android.content.Intent r0 = r7.intent     // Catch:{ RemoteException -> 0x0296 }
            r4.<init>(r0)     // Catch:{ RemoteException -> 0x0296 }
            int r5 = r7.resultCode     // Catch:{ RemoteException -> 0x0296 }
            java.lang.String r0 = r7.resultData     // Catch:{ RemoteException -> 0x0296 }
            android.os.Bundle r1 = r7.resultExtras     // Catch:{ RemoteException -> 0x0296 }
            r22 = 0
            r23 = 0
            int r10 = r7.userId     // Catch:{ RemoteException -> 0x028e }
            r24 = r1
            r1 = r36
            r25 = r6
            r6 = r0
            r14 = r7
            r7 = r24
            r26 = r8
            r8 = r22
            r9 = r23
            r15 = 0
            r1.performReceiveLocked(r2, r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ RemoteException -> 0x028c }
            r14.resultTo = r15     // Catch:{ RemoteException -> 0x028c }
            goto L_0x02cf
        L_0x028c:
            r0 = move-exception
            goto L_0x029d
        L_0x028e:
            r0 = move-exception
            r25 = r6
            r14 = r7
            r26 = r8
            r15 = 0
            goto L_0x029d
        L_0x0296:
            r0 = move-exception
            r25 = r6
            r14 = r7
            r26 = r8
            r15 = r10
        L_0x029d:
            r14.resultTo = r15
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Failure ["
            r1.append(r2)
            java.lang.String r2 = r11.mQueueName
            r1.append(r2)
            java.lang.String r2 = "] sending broadcast result of "
            r1.append(r2)
            android.content.Intent r2 = r14.intent
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "BroadcastQueue"
            android.util.Slog.w(r2, r1, r0)
            goto L_0x02cf
        L_0x02c2:
            r25 = r6
            r14 = r7
            r26 = r8
            r15 = r10
            goto L_0x02cf
        L_0x02c9:
            r25 = r6
            r14 = r7
            r26 = r8
            r15 = r10
        L_0x02cf:
            r36.cancelBroadcastTimeoutLocked()
            r11.addBroadcastToHistoryLocked(r14)
            android.content.Intent r0 = r14.intent
            android.content.ComponentName r0 = r0.getComponent()
            if (r0 != 0) goto L_0x0305
            android.content.Intent r0 = r14.intent
            java.lang.String r0 = r0.getPackage()
            if (r0 != 0) goto L_0x0305
            android.content.Intent r0 = r14.intent
            int r0 = r0.getFlags()
            r0 = r0 & r20
            if (r0 != 0) goto L_0x0305
            com.android.server.am.ActivityManagerService r1 = r11.mService
            android.content.Intent r0 = r14.intent
            java.lang.String r2 = r0.getAction()
            java.lang.String r3 = r14.callerPackage
            int r4 = r14.manifestCount
            int r5 = r14.manifestSkipCount
            long r6 = r14.finishTime
            long r8 = r14.dispatchTime
            long r6 = r6 - r8
            r1.addBroadcastStatLocked(r2, r3, r4, r5, r6)
        L_0x0305:
            com.android.server.am.BroadcastDispatcher r0 = r11.mDispatcher
            r0.retireBroadcastLocked(r14)
            r0 = 0
            r1 = 1
            r8 = r0
            r16 = r1
        L_0x030f:
            if (r8 == 0) goto L_0x0c19
            int r0 = r8.nextReceiver
            int r1 = r0 + 1
            r8.nextReceiver = r1
            r9 = r0
            long r0 = android.os.SystemClock.uptimeMillis()
            r8.receiverTime = r0
            if (r9 != 0) goto L_0x0359
            long r0 = r8.receiverTime
            r8.dispatchTime = r0
            long r0 = java.lang.System.currentTimeMillis()
            r8.dispatchClockTime = r0
            boolean r0 = r11.mLogLatencyMetrics
            if (r0 == 0) goto L_0x0338
            r0 = 142(0x8e, float:1.99E-43)
            long r1 = r8.dispatchClockTime
            long r3 = r8.enqueueClockTime
            long r1 = r1 - r3
            android.util.StatsLog.write(r0, r1)
        L_0x0338:
            r1 = 64
            boolean r0 = android.os.Trace.isTagEnabled(r1)
            if (r0 == 0) goto L_0x0359
            java.lang.String r0 = r11.createBroadcastTraceTitle(r8, r12)
            int r3 = java.lang.System.identityHashCode(r8)
            android.os.Trace.asyncTraceEnd(r1, r0, r3)
            r3 = 1
            java.lang.String r0 = r11.createBroadcastTraceTitle(r8, r3)
            int r3 = java.lang.System.identityHashCode(r8)
            android.os.Trace.asyncTraceBegin(r1, r0, r3)
        L_0x0359:
            boolean r0 = r11.mPendingBroadcastTimeoutMessage
            if (r0 != 0) goto L_0x0367
            long r0 = r8.receiverTime
            com.android.server.am.BroadcastConstants r2 = r11.mConstants
            long r2 = r2.TIMEOUT
            long r0 = r0 + r2
            r11.setBroadcastTimeoutLocked(r0)
        L_0x0367:
            android.app.BroadcastOptions r10 = r8.options
            java.util.List r0 = r8.receivers
            java.lang.Object r13 = r0.get(r9)
            boolean r0 = r13 instanceof com.android.server.am.BroadcastFilter
            if (r0 == 0) goto L_0x03a9
            r0 = r13
            com.android.server.am.BroadcastFilter r0 = (com.android.server.am.BroadcastFilter) r0
            boolean r1 = r8.ordered
            r11.deliverToRegisteredReceiverLocked(r8, r0, r1, r9)
            android.os.IBinder r1 = r8.receiver
            if (r1 == 0) goto L_0x03a3
            boolean r1 = r8.ordered
            if (r1 != 0) goto L_0x0384
            goto L_0x03a3
        L_0x0384:
            com.android.server.am.ReceiverList r1 = r0.receiverList
            if (r1 == 0) goto L_0x038f
            com.android.server.am.ReceiverList r1 = r0.receiverList
            com.android.server.am.ProcessRecord r1 = r1.app
            r11.maybeAddAllowBackgroundActivityStartsToken(r1, r8)
        L_0x038f:
            if (r10 == 0) goto L_0x03a8
            long r1 = r10.getTemporaryAppWhitelistDuration()
            int r1 = (r1 > r17 ? 1 : (r1 == r17 ? 0 : -1))
            if (r1 <= 0) goto L_0x03a8
            int r1 = r0.owningUid
            long r2 = r10.getTemporaryAppWhitelistDuration()
            r11.scheduleTempWhitelistLocked(r1, r2, r8)
            goto L_0x03a8
        L_0x03a3:
            r8.state = r12
            r36.scheduleBroadcastsLocked()
        L_0x03a8:
            return
        L_0x03a9:
            r14 = r13
            android.content.pm.ResolveInfo r14 = (android.content.pm.ResolveInfo) r14
            android.content.ComponentName r0 = new android.content.ComponentName
            android.content.pm.ActivityInfo r1 = r14.activityInfo
            android.content.pm.ApplicationInfo r1 = r1.applicationInfo
            java.lang.String r1 = r1.packageName
            android.content.pm.ActivityInfo r2 = r14.activityInfo
            java.lang.String r2 = r2.name
            r0.<init>(r1, r2)
            r1 = r0
            r0 = 0
            if (r10 == 0) goto L_0x03d8
            android.content.pm.ActivityInfo r2 = r14.activityInfo
            android.content.pm.ApplicationInfo r2 = r2.applicationInfo
            int r2 = r2.targetSdkVersion
            int r3 = r10.getMinManifestReceiverApiLevel()
            if (r2 < r3) goto L_0x03d7
            android.content.pm.ActivityInfo r2 = r14.activityInfo
            android.content.pm.ApplicationInfo r2 = r2.applicationInfo
            int r2 = r2.targetSdkVersion
            int r3 = r10.getMaxManifestReceiverApiLevel()
            if (r2 <= r3) goto L_0x03d8
        L_0x03d7:
            r0 = 1
        L_0x03d8:
            if (r0 != 0) goto L_0x0437
            com.android.server.am.ActivityManagerService r2 = r11.mService
            java.lang.String r3 = r8.callerPackage
            int r4 = r8.callingUid
            java.lang.String r5 = r1.getPackageName()
            android.content.pm.ActivityInfo r6 = r14.activityInfo
            android.content.pm.ApplicationInfo r6 = r6.applicationInfo
            int r6 = r6.uid
            boolean r2 = r2.validateAssociationAllowedLocked(r3, r4, r5, r6)
            if (r2 != 0) goto L_0x0437
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Association not allowed: broadcasting "
            r2.append(r3)
            android.content.Intent r3 = r8.intent
            java.lang.String r3 = r3.toString()
            r2.append(r3)
            java.lang.String r3 = " from "
            r2.append(r3)
            java.lang.String r3 = r8.callerPackage
            r2.append(r3)
            java.lang.String r3 = " (pid="
            r2.append(r3)
            int r3 = r8.callingPid
            r2.append(r3)
            java.lang.String r3 = ", uid="
            r2.append(r3)
            int r3 = r8.callingUid
            r2.append(r3)
            java.lang.String r3 = ") to "
            r2.append(r3)
            java.lang.String r3 = r1.flattenToShortString()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "BroadcastQueue"
            android.util.Slog.w(r3, r2)
            r0 = 1
        L_0x0437:
            if (r0 != 0) goto L_0x04a6
            com.android.server.am.ActivityManagerService r2 = r11.mService
            com.android.server.firewall.IntentFirewall r2 = r2.mIntentFirewall
            android.content.Intent r3 = r8.intent
            int r4 = r8.callingUid
            int r5 = r8.callingPid
            java.lang.String r6 = r8.resolvedType
            android.content.pm.ActivityInfo r7 = r14.activityInfo
            android.content.pm.ApplicationInfo r7 = r7.applicationInfo
            int r7 = r7.uid
            r26 = r2
            r27 = r3
            r28 = r4
            r29 = r5
            r30 = r6
            r31 = r7
            boolean r2 = r26.checkBroadcast(r27, r28, r29, r30, r31)
            r3 = 1
            r2 = r2 ^ r3
            r0 = r2
            if (r0 == 0) goto L_0x04a6
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Firewall blocked: broadcasting "
            r2.append(r3)
            android.content.Intent r3 = r8.intent
            java.lang.String r3 = r3.toString()
            r2.append(r3)
            java.lang.String r3 = " from "
            r2.append(r3)
            java.lang.String r3 = r8.callerPackage
            r2.append(r3)
            java.lang.String r3 = " (pid="
            r2.append(r3)
            int r3 = r8.callingPid
            r2.append(r3)
            java.lang.String r3 = ", uid="
            r2.append(r3)
            int r3 = r8.callingUid
            r2.append(r3)
            java.lang.String r3 = ") to "
            r2.append(r3)
            java.lang.String r3 = r1.flattenToShortString()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "BroadcastQueue"
            android.util.Slog.w(r3, r2)
        L_0x04a6:
            com.android.server.am.ActivityManagerService r2 = r11.mService
            android.content.pm.ActivityInfo r2 = r14.activityInfo
            java.lang.String r2 = r2.permission
            int r3 = r8.callingPid
            int r4 = r8.callingUid
            android.content.pm.ActivityInfo r5 = r14.activityInfo
            android.content.pm.ApplicationInfo r5 = r5.applicationInfo
            int r5 = r5.uid
            android.content.pm.ActivityInfo r6 = r14.activityInfo
            boolean r6 = r6.exported
            int r2 = com.android.server.am.ActivityManagerService.checkComponentPermission(r2, r3, r4, r5, r6)
            r7 = -1
            if (r0 != 0) goto L_0x0574
            if (r2 == 0) goto L_0x0574
            android.content.pm.ActivityInfo r3 = r14.activityInfo
            boolean r3 = r3.exported
            if (r3 != 0) goto L_0x051e
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Permission Denial: broadcasting "
            r3.append(r4)
            android.content.Intent r4 = r8.intent
            java.lang.String r4 = r4.toString()
            r3.append(r4)
            java.lang.String r4 = " from "
            r3.append(r4)
            java.lang.String r4 = r8.callerPackage
            r3.append(r4)
            java.lang.String r4 = " (pid="
            r3.append(r4)
            int r4 = r8.callingPid
            r3.append(r4)
            java.lang.String r4 = ", uid="
            r3.append(r4)
            int r4 = r8.callingUid
            r3.append(r4)
            java.lang.String r4 = ") is not exported from uid "
            r3.append(r4)
            android.content.pm.ActivityInfo r4 = r14.activityInfo
            android.content.pm.ApplicationInfo r4 = r4.applicationInfo
            int r4 = r4.uid
            r3.append(r4)
            java.lang.String r4 = " due to receiver "
            r3.append(r4)
            java.lang.String r4 = r1.flattenToShortString()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            java.lang.String r4 = "BroadcastQueue"
            android.util.Slog.w(r4, r3)
            goto L_0x0570
        L_0x051e:
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "Permission Denial: broadcasting "
            r3.append(r4)
            android.content.Intent r4 = r8.intent
            java.lang.String r4 = r4.toString()
            r3.append(r4)
            java.lang.String r4 = " from "
            r3.append(r4)
            java.lang.String r4 = r8.callerPackage
            r3.append(r4)
            java.lang.String r4 = " (pid="
            r3.append(r4)
            int r4 = r8.callingPid
            r3.append(r4)
            java.lang.String r4 = ", uid="
            r3.append(r4)
            int r4 = r8.callingUid
            r3.append(r4)
            java.lang.String r4 = ") requires "
            r3.append(r4)
            android.content.pm.ActivityInfo r4 = r14.activityInfo
            java.lang.String r4 = r4.permission
            r3.append(r4)
            java.lang.String r4 = " due to receiver "
            r3.append(r4)
            java.lang.String r4 = r1.flattenToShortString()
            r3.append(r4)
            java.lang.String r3 = r3.toString()
            java.lang.String r4 = "BroadcastQueue"
            android.util.Slog.w(r4, r3)
        L_0x0570:
            r0 = 1
            r3 = r0
            goto L_0x05ee
        L_0x0574:
            if (r0 != 0) goto L_0x05ed
            android.content.pm.ActivityInfo r3 = r14.activityInfo
            java.lang.String r3 = r3.permission
            if (r3 == 0) goto L_0x05ed
            android.content.pm.ActivityInfo r3 = r14.activityInfo
            java.lang.String r3 = r3.permission
            int r3 = android.app.AppOpsManager.permissionToOpCode(r3)
            if (r3 == r7) goto L_0x05ed
            com.android.server.am.ActivityManagerService r4 = r11.mService
            com.android.server.appop.AppOpsService r4 = r4.mAppOpsService
            int r5 = r8.callingUid
            java.lang.String r6 = r8.callerPackage
            int r4 = r4.noteOperation(r3, r5, r6)
            if (r4 == 0) goto L_0x05ed
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Appop Denial: broadcasting "
            r4.append(r5)
            android.content.Intent r5 = r8.intent
            java.lang.String r5 = r5.toString()
            r4.append(r5)
            java.lang.String r5 = " from "
            r4.append(r5)
            java.lang.String r5 = r8.callerPackage
            r4.append(r5)
            java.lang.String r5 = " (pid="
            r4.append(r5)
            int r5 = r8.callingPid
            r4.append(r5)
            java.lang.String r5 = ", uid="
            r4.append(r5)
            int r5 = r8.callingUid
            r4.append(r5)
            java.lang.String r5 = ") requires appop "
            r4.append(r5)
            android.content.pm.ActivityInfo r5 = r14.activityInfo
            java.lang.String r5 = r5.permission
            java.lang.String r5 = android.app.AppOpsManager.permissionToOp(r5)
            r4.append(r5)
            java.lang.String r5 = " due to registered receiver "
            r4.append(r5)
            java.lang.String r5 = r1.flattenToShortString()
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            java.lang.String r5 = "BroadcastQueue"
            android.util.Slog.w(r5, r4)
            r0 = 1
            r3 = r0
            goto L_0x05ee
        L_0x05ed:
            r3 = r0
        L_0x05ee:
            r6 = 1000(0x3e8, float:1.401E-42)
            if (r3 != 0) goto L_0x06ed
            android.content.pm.ActivityInfo r0 = r14.activityInfo
            android.content.pm.ApplicationInfo r0 = r0.applicationInfo
            int r0 = r0.uid
            if (r0 == r6) goto L_0x06ed
            java.lang.String[] r0 = r8.requiredPermissions
            if (r0 == 0) goto L_0x06ed
            java.lang.String[] r0 = r8.requiredPermissions
            int r0 = r0.length
            if (r0 <= 0) goto L_0x06ed
            r0 = 0
            r4 = r2
            r2 = r0
        L_0x0606:
            java.lang.String[] r0 = r8.requiredPermissions
            int r0 = r0.length
            if (r2 >= r0) goto L_0x06ea
            java.lang.String[] r0 = r8.requiredPermissions
            r5 = r0[r2]
            android.content.pm.IPackageManager r0 = android.app.AppGlobals.getPackageManager()     // Catch:{ RemoteException -> 0x0629 }
            android.content.pm.ActivityInfo r6 = r14.activityInfo     // Catch:{ RemoteException -> 0x0629 }
            android.content.pm.ApplicationInfo r6 = r6.applicationInfo     // Catch:{ RemoteException -> 0x0629 }
            java.lang.String r6 = r6.packageName     // Catch:{ RemoteException -> 0x0629 }
            android.content.pm.ActivityInfo r15 = r14.activityInfo     // Catch:{ RemoteException -> 0x0629 }
            android.content.pm.ApplicationInfo r15 = r15.applicationInfo     // Catch:{ RemoteException -> 0x0629 }
            int r15 = r15.uid     // Catch:{ RemoteException -> 0x0629 }
            int r15 = android.os.UserHandle.getUserId(r15)     // Catch:{ RemoteException -> 0x0629 }
            int r0 = r0.checkPermission(r5, r6, r15)     // Catch:{ RemoteException -> 0x0629 }
            r4 = r0
            goto L_0x062b
        L_0x0629:
            r0 = move-exception
            r4 = -1
        L_0x062b:
            if (r4 == 0) goto L_0x0677
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r6 = "Permission Denial: receiving "
            r0.append(r6)
            android.content.Intent r6 = r8.intent
            r0.append(r6)
            java.lang.String r6 = " to "
            r0.append(r6)
            java.lang.String r6 = r1.flattenToShortString()
            r0.append(r6)
            java.lang.String r6 = " requires "
            r0.append(r6)
            r0.append(r5)
            java.lang.String r6 = " due to sender "
            r0.append(r6)
            java.lang.String r6 = r8.callerPackage
            r0.append(r6)
            java.lang.String r6 = " (uid "
            r0.append(r6)
            int r6 = r8.callingUid
            r0.append(r6)
            java.lang.String r6 = ")"
            r0.append(r6)
            java.lang.String r0 = r0.toString()
            java.lang.String r6 = "BroadcastQueue"
            android.util.Slog.w(r6, r0)
            r3 = 1
            r0 = r3
            r12 = r4
            goto L_0x06ef
        L_0x0677:
            int r0 = android.app.AppOpsManager.permissionToOpCode(r5)
            if (r0 == r7) goto L_0x06e2
            int r6 = r8.appOp
            if (r0 == r6) goto L_0x06e2
            com.android.server.am.ActivityManagerService r6 = r11.mService
            com.android.server.appop.AppOpsService r6 = r6.mAppOpsService
            android.content.pm.ActivityInfo r15 = r14.activityInfo
            android.content.pm.ApplicationInfo r15 = r15.applicationInfo
            int r15 = r15.uid
            android.content.pm.ActivityInfo r12 = r14.activityInfo
            java.lang.String r12 = r12.packageName
            int r6 = r6.checkOperation(r0, r15, r12)
            if (r6 == 0) goto L_0x06e2
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r12 = "Appop Denial: receiving "
            r6.append(r12)
            android.content.Intent r12 = r8.intent
            r6.append(r12)
            java.lang.String r12 = " to "
            r6.append(r12)
            java.lang.String r12 = r1.flattenToShortString()
            r6.append(r12)
            java.lang.String r12 = " requires appop "
            r6.append(r12)
            java.lang.String r12 = android.app.AppOpsManager.permissionToOp(r5)
            r6.append(r12)
            java.lang.String r12 = " due to sender "
            r6.append(r12)
            java.lang.String r12 = r8.callerPackage
            r6.append(r12)
            java.lang.String r12 = " (uid "
            r6.append(r12)
            int r12 = r8.callingUid
            r6.append(r12)
            java.lang.String r12 = ")"
            r6.append(r12)
            java.lang.String r6 = r6.toString()
            java.lang.String r12 = "BroadcastQueue"
            android.util.Slog.w(r12, r6)
            r3 = 1
            r0 = r3
            r12 = r4
            goto L_0x06ef
        L_0x06e2:
            int r2 = r2 + 1
            r6 = 1000(0x3e8, float:1.401E-42)
            r12 = 0
            r15 = 0
            goto L_0x0606
        L_0x06ea:
            r0 = r3
            r12 = r4
            goto L_0x06ef
        L_0x06ed:
            r12 = r2
            r0 = r3
        L_0x06ef:
            if (r0 != 0) goto L_0x075c
            int r2 = r8.appOp
            if (r2 == r7) goto L_0x075c
            int r2 = r8.appOp
            android.content.pm.ActivityInfo r3 = r14.activityInfo
            android.content.pm.ApplicationInfo r3 = r3.applicationInfo
            int r3 = r3.uid
            android.content.pm.ActivityInfo r4 = r14.activityInfo
            java.lang.String r4 = r4.packageName
            com.android.server.am.BroadcastQueue$BroadcastHandler r5 = r11.mHandler
            com.android.server.am.ActivityManagerService r6 = r11.mService
            r15 = 1000(0x3e8, float:1.401E-42)
            r15 = r7
            r7 = r8
            int r2 = com.android.server.am.BroadcastQueueInjector.noteOperationLocked(r2, r3, r4, r5, r6, r7)
            if (r2 == 0) goto L_0x075d
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Appop Denial: receiving "
            r2.append(r3)
            android.content.Intent r3 = r8.intent
            r2.append(r3)
            java.lang.String r3 = " to "
            r2.append(r3)
            java.lang.String r3 = r1.flattenToShortString()
            r2.append(r3)
            java.lang.String r3 = " requires appop "
            r2.append(r3)
            int r3 = r8.appOp
            java.lang.String r3 = android.app.AppOpsManager.opToName(r3)
            r2.append(r3)
            java.lang.String r3 = " due to sender "
            r2.append(r3)
            java.lang.String r3 = r8.callerPackage
            r2.append(r3)
            java.lang.String r3 = " (uid "
            r2.append(r3)
            int r3 = r8.callingUid
            r2.append(r3)
            java.lang.String r3 = ")"
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "BroadcastQueue"
            android.util.Slog.w(r3, r2)
            r0 = 1
            goto L_0x075d
        L_0x075c:
            r15 = r7
        L_0x075d:
            if (r0 != 0) goto L_0x0770
            int r2 = r8.appOp
            if (r2 == r15) goto L_0x0770
            com.android.server.am.ActivityManagerService r2 = r11.mService
            int r3 = r8.appOp
            boolean r2 = com.android.server.am.BroadcastQueueInjector.isSkip((com.android.server.am.ActivityManagerService) r2, (com.android.server.am.BroadcastRecord) r8, (android.content.pm.ResolveInfo) r14, (int) r3)
            if (r2 == 0) goto L_0x0770
            r0 = 1
            r2 = r0
            goto L_0x0771
        L_0x0770:
            r2 = r0
        L_0x0771:
            r3 = 0
            com.android.server.am.ActivityManagerService r0 = r11.mService     // Catch:{ SecurityException -> 0x078b }
            android.content.pm.ActivityInfo r4 = r14.activityInfo     // Catch:{ SecurityException -> 0x078b }
            java.lang.String r4 = r4.processName     // Catch:{ SecurityException -> 0x078b }
            android.content.pm.ActivityInfo r5 = r14.activityInfo     // Catch:{ SecurityException -> 0x078b }
            android.content.pm.ApplicationInfo r5 = r5.applicationInfo     // Catch:{ SecurityException -> 0x078b }
            android.content.pm.ActivityInfo r6 = r14.activityInfo     // Catch:{ SecurityException -> 0x078b }
            java.lang.String r6 = r6.name     // Catch:{ SecurityException -> 0x078b }
            android.content.pm.ActivityInfo r7 = r14.activityInfo     // Catch:{ SecurityException -> 0x078b }
            int r7 = r7.flags     // Catch:{ SecurityException -> 0x078b }
            boolean r0 = r0.isSingleton(r4, r5, r6, r7)     // Catch:{ SecurityException -> 0x078b }
            r3 = r0
            r15 = r3
            goto L_0x0797
        L_0x078b:
            r0 = move-exception
            java.lang.String r4 = r0.getMessage()
            java.lang.String r5 = "BroadcastQueue"
            android.util.Slog.w(r5, r4)
            r2 = 1
            r15 = r3
        L_0x0797:
            android.content.pm.ActivityInfo r0 = r14.activityInfo
            int r0 = r0.flags
            r0 = r0 & r20
            if (r0 == 0) goto L_0x07d2
            android.content.pm.ActivityInfo r0 = r14.activityInfo
            android.content.pm.ApplicationInfo r0 = r0.applicationInfo
            int r0 = r0.uid
            java.lang.String r3 = "android.permission.INTERACT_ACROSS_USERS"
            int r0 = android.app.ActivityManager.checkUidPermission(r3, r0)
            if (r0 == 0) goto L_0x07d2
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "Permission Denial: Receiver "
            r0.append(r3)
            java.lang.String r3 = r1.flattenToShortString()
            r0.append(r3)
            java.lang.String r3 = " requests FLAG_SINGLE_USER, but app does not hold "
            r0.append(r3)
            java.lang.String r3 = "android.permission.INTERACT_ACROSS_USERS"
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.lang.String r3 = "BroadcastQueue"
            android.util.Slog.w(r3, r0)
            r2 = 1
        L_0x07d2:
            if (r2 != 0) goto L_0x0826
            android.content.pm.ActivityInfo r0 = r14.activityInfo
            android.content.pm.ApplicationInfo r0 = r0.applicationInfo
            boolean r0 = r0.isInstantApp()
            if (r0 == 0) goto L_0x0826
            int r0 = r8.callingUid
            android.content.pm.ActivityInfo r3 = r14.activityInfo
            android.content.pm.ApplicationInfo r3 = r3.applicationInfo
            int r3 = r3.uid
            if (r0 == r3) goto L_0x0826
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "Instant App Denial: receiving "
            r0.append(r3)
            android.content.Intent r3 = r8.intent
            r0.append(r3)
            java.lang.String r3 = " to "
            r0.append(r3)
            java.lang.String r3 = r1.flattenToShortString()
            r0.append(r3)
            java.lang.String r3 = " due to sender "
            r0.append(r3)
            java.lang.String r3 = r8.callerPackage
            r0.append(r3)
            java.lang.String r3 = " (uid "
            r0.append(r3)
            int r3 = r8.callingUid
            r0.append(r3)
            java.lang.String r3 = ") Instant Apps do not support manifest receivers"
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.lang.String r3 = "BroadcastQueue"
            android.util.Slog.w(r3, r0)
            r2 = 1
        L_0x0826:
            if (r2 != 0) goto L_0x087d
            boolean r0 = r8.callerInstantApp
            if (r0 == 0) goto L_0x087d
            android.content.pm.ActivityInfo r0 = r14.activityInfo
            int r0 = r0.flags
            r3 = 1048576(0x100000, float:1.469368E-39)
            r0 = r0 & r3
            if (r0 != 0) goto L_0x087d
            int r0 = r8.callingUid
            android.content.pm.ActivityInfo r3 = r14.activityInfo
            android.content.pm.ApplicationInfo r3 = r3.applicationInfo
            int r3 = r3.uid
            if (r0 == r3) goto L_0x087d
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "Instant App Denial: receiving "
            r0.append(r3)
            android.content.Intent r3 = r8.intent
            r0.append(r3)
            java.lang.String r3 = " to "
            r0.append(r3)
            java.lang.String r3 = r1.flattenToShortString()
            r0.append(r3)
            java.lang.String r3 = " requires receiver have visibleToInstantApps set due to sender "
            r0.append(r3)
            java.lang.String r3 = r8.callerPackage
            r0.append(r3)
            java.lang.String r3 = " (uid "
            r0.append(r3)
            int r3 = r8.callingUid
            r0.append(r3)
            java.lang.String r3 = ")"
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.lang.String r3 = "BroadcastQueue"
            android.util.Slog.w(r3, r0)
            r2 = 1
        L_0x087d:
            com.android.server.am.ProcessRecord r0 = r8.curApp
            if (r0 == 0) goto L_0x08b9
            com.android.server.am.ProcessRecord r0 = r8.curApp
            boolean r0 = r0.isCrashing()
            if (r0 == 0) goto L_0x08b9
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "Skipping deliver ordered ["
            r0.append(r3)
            java.lang.String r3 = r11.mQueueName
            r0.append(r3)
            java.lang.String r3 = "] "
            r0.append(r3)
            r0.append(r8)
            java.lang.String r3 = " to "
            r0.append(r3)
            com.android.server.am.ProcessRecord r3 = r8.curApp
            r0.append(r3)
            java.lang.String r3 = ": process crashing"
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.lang.String r3 = "BroadcastQueue"
            android.util.Slog.w(r3, r0)
            r2 = 1
        L_0x08b9:
            if (r2 != 0) goto L_0x08f2
            r3 = 0
            android.content.pm.IPackageManager r0 = android.app.AppGlobals.getPackageManager()     // Catch:{ Exception -> 0x08d4 }
            android.content.pm.ActivityInfo r4 = r14.activityInfo     // Catch:{ Exception -> 0x08d4 }
            java.lang.String r4 = r4.packageName     // Catch:{ Exception -> 0x08d4 }
            android.content.pm.ActivityInfo r5 = r14.activityInfo     // Catch:{ Exception -> 0x08d4 }
            android.content.pm.ApplicationInfo r5 = r5.applicationInfo     // Catch:{ Exception -> 0x08d4 }
            int r5 = r5.uid     // Catch:{ Exception -> 0x08d4 }
            int r5 = android.os.UserHandle.getUserId(r5)     // Catch:{ Exception -> 0x08d4 }
            boolean r0 = r0.isPackageAvailable(r4, r5)     // Catch:{ Exception -> 0x08d4 }
            r3 = r0
            goto L_0x08ef
        L_0x08d4:
            r0 = move-exception
            java.lang.StringBuilder r4 = new java.lang.StringBuilder
            r4.<init>()
            java.lang.String r5 = "Exception getting recipient info for "
            r4.append(r5)
            android.content.pm.ActivityInfo r5 = r14.activityInfo
            java.lang.String r5 = r5.packageName
            r4.append(r5)
            java.lang.String r4 = r4.toString()
            java.lang.String r5 = "BroadcastQueue"
            android.util.Slog.w(r5, r4, r0)
        L_0x08ef:
            if (r3 != 0) goto L_0x08f2
            r2 = 1
        L_0x08f2:
            if (r2 != 0) goto L_0x0909
            android.content.pm.ActivityInfo r0 = r14.activityInfo
            java.lang.String r0 = r0.packageName
            android.content.pm.ActivityInfo r3 = r14.activityInfo
            android.content.pm.ApplicationInfo r3 = r3.applicationInfo
            int r3 = r3.uid
            int r3 = android.os.UserHandle.getUserId(r3)
            boolean r0 = r11.requestStartTargetPermissionsReviewIfNeededLocked(r8, r0, r3)
            if (r0 != 0) goto L_0x0909
            r2 = 1
        L_0x0909:
            android.content.pm.ActivityInfo r0 = r14.activityInfo
            android.content.pm.ApplicationInfo r0 = r0.applicationInfo
            int r7 = r0.uid
            int r0 = r8.callingUid
            r3 = 1000(0x3e8, float:1.401E-42)
            if (r0 == r3) goto L_0x092c
            if (r15 == 0) goto L_0x092c
            com.android.server.am.ActivityManagerService r0 = r11.mService
            int r3 = r8.callingUid
            boolean r0 = r0.isValidSingletonCall(r3, r7)
            if (r0 == 0) goto L_0x092c
            com.android.server.am.ActivityManagerService r0 = r11.mService
            android.content.pm.ActivityInfo r3 = r14.activityInfo
            r4 = 0
            android.content.pm.ActivityInfo r0 = r0.getActivityInfoForUser(r3, r4)
            r14.activityInfo = r0
        L_0x092c:
            android.content.pm.ActivityInfo r0 = r14.activityInfo
            java.lang.String r6 = r0.processName
            com.android.server.am.ActivityManagerService r0 = r11.mService
            android.content.pm.ActivityInfo r3 = r14.activityInfo
            android.content.pm.ApplicationInfo r3 = r3.applicationInfo
            int r3 = r3.uid
            r4 = 0
            com.android.server.am.ProcessRecord r5 = r0.getProcessRecordLocked(r6, r3, r4)
            if (r2 != 0) goto L_0x09fb
            com.android.server.am.ActivityManagerService r0 = r11.mService
            android.content.pm.ActivityInfo r3 = r14.activityInfo
            android.content.pm.ApplicationInfo r3 = r3.applicationInfo
            int r3 = r3.uid
            android.content.pm.ActivityInfo r4 = r14.activityInfo
            java.lang.String r4 = r4.packageName
            r19 = r2
            android.content.pm.ActivityInfo r2 = r14.activityInfo
            android.content.pm.ApplicationInfo r2 = r2.applicationInfo
            int r2 = r2.targetSdkVersion
            r30 = -1
            r31 = 1
            r32 = 0
            r33 = 0
            r20 = r6
            java.lang.String r6 = r8.callerPackage
            r26 = r0
            r27 = r3
            r28 = r4
            r29 = r2
            r34 = r6
            int r0 = r26.getAppStartModeLocked(r27, r28, r29, r30, r31, r32, r33, r34)
            if (r0 == 0) goto L_0x09ff
            r2 = 3
            if (r0 != r2) goto L_0x0998
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Background execution disabled: receiving "
            r2.append(r3)
            android.content.Intent r3 = r8.intent
            r2.append(r3)
            java.lang.String r3 = " to "
            r2.append(r3)
            java.lang.String r3 = r1.flattenToShortString()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "BroadcastQueue"
            android.util.Slog.w(r3, r2)
            r2 = 1
            goto L_0x0a01
        L_0x0998:
            android.content.Intent r2 = r8.intent
            int r2 = r2.getFlags()
            r3 = 8388608(0x800000, float:1.17549435E-38)
            r2 = r2 & r3
            if (r2 != 0) goto L_0x09c6
            android.content.Intent r2 = r8.intent
            android.content.ComponentName r2 = r2.getComponent()
            if (r2 != 0) goto L_0x09ff
            android.content.Intent r2 = r8.intent
            java.lang.String r2 = r2.getPackage()
            if (r2 != 0) goto L_0x09ff
            android.content.Intent r2 = r8.intent
            int r2 = r2.getFlags()
            r3 = 16777216(0x1000000, float:2.3509887E-38)
            r2 = r2 & r3
            if (r2 != 0) goto L_0x09ff
            java.lang.String[] r2 = r8.requiredPermissions
            boolean r2 = r11.isSignaturePerm(r2)
            if (r2 != 0) goto L_0x09ff
        L_0x09c6:
            com.android.server.am.ActivityManagerService r2 = r11.mService
            android.content.Intent r3 = r8.intent
            java.lang.String r3 = r3.getAction()
            java.lang.String r4 = r1.getPackageName()
            r2.addBackgroundCheckViolationLocked(r3, r4)
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Background execution not allowed: receiving "
            r2.append(r3)
            android.content.Intent r3 = r8.intent
            r2.append(r3)
            java.lang.String r3 = " to "
            r2.append(r3)
            java.lang.String r3 = r1.flattenToShortString()
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "BroadcastQueue"
            android.util.Slog.w(r3, r2)
            r2 = 1
            goto L_0x0a01
        L_0x09fb:
            r19 = r2
            r20 = r6
        L_0x09ff:
            r2 = r19
        L_0x0a01:
            if (r2 != 0) goto L_0x0a57
            android.content.Intent r0 = r8.intent
            java.lang.String r0 = r0.getAction()
            java.lang.String r3 = "android.intent.action.ACTION_SHUTDOWN"
            boolean r0 = r3.equals(r0)
            if (r0 != 0) goto L_0x0a57
            com.android.server.am.ActivityManagerService r0 = r11.mService
            com.android.server.am.UserController r0 = r0.mUserController
            android.content.pm.ActivityInfo r3 = r14.activityInfo
            android.content.pm.ApplicationInfo r3 = r3.applicationInfo
            int r3 = r3.uid
            int r3 = android.os.UserHandle.getUserId(r3)
            r4 = 0
            boolean r0 = r0.isUserRunning(r3, r4)
            if (r0 != 0) goto L_0x0a57
            r2 = 1
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "Skipping delivery to "
            r0.append(r3)
            android.content.pm.ActivityInfo r3 = r14.activityInfo
            java.lang.String r3 = r3.packageName
            r0.append(r3)
            java.lang.String r3 = " / "
            r0.append(r3)
            android.content.pm.ActivityInfo r3 = r14.activityInfo
            android.content.pm.ApplicationInfo r3 = r3.applicationInfo
            int r3 = r3.uid
            r0.append(r3)
            java.lang.String r3 = " : user is not running"
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.lang.String r3 = "BroadcastQueue"
            android.util.Slog.w(r3, r0)
            r19 = r2
            goto L_0x0a59
        L_0x0a57:
            r19 = r2
        L_0x0a59:
            if (r19 == 0) goto L_0x0a72
            int[] r0 = r8.delivery
            r2 = 2
            r0[r9] = r2
            r3 = 0
            r8.receiver = r3
            r8.curFilter = r3
            r2 = 0
            r8.state = r2
            int r0 = r8.manifestSkipCount
            r4 = 1
            int r0 = r0 + r4
            r8.manifestSkipCount = r0
            r36.scheduleBroadcastsLocked()
            return
        L_0x0a72:
            r4 = 1
            int r0 = r8.manifestCount
            int r0 = r0 + r4
            r8.manifestCount = r0
            int[] r0 = r8.delivery
            r0[r9] = r4
            r8.state = r4
            r8.curComponent = r1
            android.content.pm.ActivityInfo r0 = r14.activityInfo
            r8.curReceiver = r0
            if (r10 == 0) goto L_0x0a96
            long r2 = r10.getTemporaryAppWhitelistDuration()
            int r0 = (r2 > r17 ? 1 : (r2 == r17 ? 0 : -1))
            if (r0 <= 0) goto L_0x0a96
            long r2 = r10.getTemporaryAppWhitelistDuration()
            r11.scheduleTempWhitelistLocked(r7, r2, r8)
        L_0x0a96:
            android.content.pm.IPackageManager r0 = android.app.AppGlobals.getPackageManager()     // Catch:{ RemoteException -> 0x0acd, IllegalArgumentException -> 0x0aa7 }
            android.content.ComponentName r2 = r8.curComponent     // Catch:{ RemoteException -> 0x0acd, IllegalArgumentException -> 0x0aa7 }
            java.lang.String r2 = r2.getPackageName()     // Catch:{ RemoteException -> 0x0acd, IllegalArgumentException -> 0x0aa7 }
            int r3 = r8.userId     // Catch:{ RemoteException -> 0x0acd, IllegalArgumentException -> 0x0aa7 }
            r6 = 0
            r0.setPackageStoppedState(r2, r6, r3)     // Catch:{ RemoteException -> 0x0acd, IllegalArgumentException -> 0x0aa7 }
            goto L_0x0ace
        L_0x0aa7:
            r0 = move-exception
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Failed trying to unstop package "
            r2.append(r3)
            android.content.ComponentName r3 = r8.curComponent
            java.lang.String r3 = r3.getPackageName()
            r2.append(r3)
            java.lang.String r3 = ": "
            r2.append(r3)
            r2.append(r0)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "BroadcastQueue"
            android.util.Slog.w(r3, r2)
            goto L_0x0acf
        L_0x0acd:
            r0 = move-exception
        L_0x0ace:
        L_0x0acf:
            if (r5 == 0) goto L_0x0b72
            android.app.IApplicationThread r0 = r5.thread
            if (r0 == 0) goto L_0x0b72
            boolean r0 = r5.killed
            if (r0 != 0) goto L_0x0b72
            android.content.pm.ActivityInfo r0 = r14.activityInfo     // Catch:{ RemoteException -> 0x0b51, RuntimeException -> 0x0b02 }
            java.lang.String r0 = r0.packageName     // Catch:{ RemoteException -> 0x0af7, RuntimeException -> 0x0b02 }
            android.content.pm.ActivityInfo r2 = r14.activityInfo     // Catch:{ RemoteException -> 0x0af7, RuntimeException -> 0x0b02 }
            android.content.pm.ApplicationInfo r2 = r2.applicationInfo     // Catch:{ RemoteException -> 0x0af7, RuntimeException -> 0x0b02 }
            long r2 = r2.longVersionCode     // Catch:{ RemoteException -> 0x0af7, RuntimeException -> 0x0b02 }
            com.android.server.am.ActivityManagerService r6 = r11.mService     // Catch:{ RemoteException -> 0x0af7, RuntimeException -> 0x0b02 }
            com.android.server.am.ProcessStatsService r6 = r6.mProcessStats     // Catch:{ RemoteException -> 0x0af7, RuntimeException -> 0x0b02 }
            r5.addPackage(r0, r2, r6)     // Catch:{ RemoteException -> 0x0af7, RuntimeException -> 0x0b02 }
            r11.maybeAddAllowBackgroundActivityStartsToken(r5, r8)     // Catch:{ RemoteException -> 0x0af7, RuntimeException -> 0x0b02 }
            r6 = r38
            r11.processCurBroadcastLocked(r8, r5, r6)     // Catch:{ RemoteException -> 0x0af5, RuntimeException -> 0x0af3 }
            return
        L_0x0af3:
            r0 = move-exception
            goto L_0x0b05
        L_0x0af5:
            r0 = move-exception
            goto L_0x0afa
        L_0x0af7:
            r0 = move-exception
            r6 = r38
        L_0x0afa:
            r21 = r1
            r23 = r5
            r22 = r10
            r10 = r7
            goto L_0x0b59
        L_0x0b02:
            r0 = move-exception
            r6 = r38
        L_0x0b05:
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r3 = "Failed sending broadcast to "
            r2.append(r3)
            android.content.ComponentName r3 = r8.curComponent
            r2.append(r3)
            java.lang.String r3 = " with "
            r2.append(r3)
            android.content.Intent r3 = r8.intent
            r2.append(r3)
            java.lang.String r2 = r2.toString()
            java.lang.String r3 = "BroadcastQueue"
            android.util.Slog.wtf(r3, r2, r0)
            r11.logBroadcastReceiverDiscardLocked(r8)
            int r3 = r8.resultCode
            java.lang.String r4 = r8.resultData
            android.os.Bundle r2 = r8.resultExtras
            r17 = r0
            boolean r0 = r8.resultAbort
            r18 = 0
            r21 = r1
            r1 = r36
            r22 = r2
            r2 = r8
            r23 = r5
            r5 = r22
            r6 = r0
            r22 = r10
            r10 = r7
            r7 = r18
            r1.finishReceiverLocked(r2, r3, r4, r5, r6, r7)
            r36.scheduleBroadcastsLocked()
            r1 = 0
            r8.state = r1
            return
        L_0x0b51:
            r0 = move-exception
            r21 = r1
            r23 = r5
            r22 = r10
            r10 = r7
        L_0x0b59:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Exception when sending broadcast to "
            r1.append(r2)
            android.content.ComponentName r2 = r8.curComponent
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            java.lang.String r2 = "BroadcastQueue"
            android.util.Slog.w(r2, r1, r0)
            goto L_0x0b79
        L_0x0b72:
            r21 = r1
            r23 = r5
            r22 = r10
            r10 = r7
        L_0x0b79:
            com.android.server.am.ActivityManagerService r0 = r11.mService
            boolean r0 = com.android.server.am.BroadcastQueueInjector.checkApplicationAutoStart(r11, r0, r8, r14)
            if (r0 != 0) goto L_0x0b82
            return
        L_0x0b82:
            com.android.server.am.ActivityManagerService r0 = r11.mService
            android.content.pm.ActivityInfo r1 = r14.activityInfo
            android.content.pm.ApplicationInfo r1 = r1.applicationInfo
            r29 = 1
            android.content.Intent r2 = r8.intent
            int r2 = r2.getFlags()
            r30 = r2 | 4
            com.android.server.am.HostingRecord r2 = new com.android.server.am.HostingRecord
            android.content.ComponentName r3 = r8.curComponent
            java.lang.String r5 = "broadcast"
            r2.<init>((java.lang.String) r5, (android.content.ComponentName) r3)
            android.content.Intent r3 = r8.intent
            int r3 = r3.getFlags()
            r5 = 33554432(0x2000000, float:9.403955E-38)
            r3 = r3 & r5
            if (r3 == 0) goto L_0x0ba9
            r32 = r4
            goto L_0x0bab
        L_0x0ba9:
            r32 = 0
        L_0x0bab:
            r33 = 0
            r34 = 0
            java.lang.String r3 = r8.callerPackage
            r26 = r0
            r27 = r20
            r28 = r1
            r31 = r2
            r35 = r3
            com.android.server.am.ProcessRecord r0 = r26.startProcessLocked(r27, r28, r29, r30, r31, r32, r33, r34, r35)
            r8.curApp = r0
            if (r0 != 0) goto L_0x0c0f
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Unable to launch app "
            r0.append(r1)
            android.content.pm.ActivityInfo r1 = r14.activityInfo
            android.content.pm.ApplicationInfo r1 = r1.applicationInfo
            java.lang.String r1 = r1.packageName
            r0.append(r1)
            java.lang.String r1 = "/"
            r0.append(r1)
            r0.append(r10)
            java.lang.String r1 = " for broadcast "
            r0.append(r1)
            android.content.Intent r1 = r8.intent
            r0.append(r1)
            java.lang.String r1 = ": process is bad"
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "BroadcastQueue"
            android.util.Slog.w(r1, r0)
            r11.logBroadcastReceiverDiscardLocked(r8)
            int r3 = r8.resultCode
            java.lang.String r4 = r8.resultData
            android.os.Bundle r5 = r8.resultExtras
            boolean r6 = r8.resultAbort
            r7 = 0
            r1 = r36
            r2 = r8
            r1.finishReceiverLocked(r2, r3, r4, r5, r6, r7)
            r36.scheduleBroadcastsLocked()
            r5 = 0
            r8.state = r5
            return
        L_0x0c0f:
            com.android.server.am.ProcessRecord r0 = r8.curApp
            r11.maybeAddAllowBackgroundActivityStartsToken(r0, r8)
            r11.mPendingBroadcast = r8
            r11.mPendingBroadcastRecvIndex = r9
            return
        L_0x0c19:
            r5 = r12
            r3 = r15
            r1 = 64
            r4 = 1
            r13 = r1
            r10 = r3
            r15 = r4
            goto L_0x00f8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.BroadcastQueue.processNextBroadcastLocked(boolean, boolean, boolean):void");
    }

    private void maybeAddAllowBackgroundActivityStartsToken(ProcessRecord proc, BroadcastRecord r) {
        if (r != null && proc != null && r.allowBackgroundActivityStarts) {
            this.mHandler.removeCallbacksAndMessages((proc.toShortString() + r.toString()).intern());
            proc.addAllowBackgroundActivityStartsToken(r);
        }
    }

    /* access modifiers changed from: package-private */
    public final void setBroadcastTimeoutLocked(long timeoutTime) {
        if (!this.mPendingBroadcastTimeoutMessage) {
            this.mHandler.sendMessageAtTime(this.mHandler.obtainMessage(201, this), timeoutTime);
            this.mPendingBroadcastTimeoutMessage = true;
        }
    }

    /* access modifiers changed from: package-private */
    public final void cancelBroadcastTimeoutLocked() {
        if (this.mPendingBroadcastTimeoutMessage) {
            this.mHandler.removeMessages(201, this);
            this.mPendingBroadcastTimeoutMessage = false;
        }
    }

    /* access modifiers changed from: package-private */
    public final void broadcastTimeoutLocked(boolean fromMsg) {
        Object curReceiver;
        ProcessRecord app;
        String anrMessage;
        boolean debugging = false;
        if (fromMsg) {
            this.mPendingBroadcastTimeoutMessage = false;
        }
        if (!this.mDispatcher.isEmpty() && this.mDispatcher.getActiveBroadcastLocked() != null) {
            long now = SystemClock.uptimeMillis();
            BroadcastRecord r = this.mDispatcher.getActiveBroadcastLocked();
            if (fromMsg) {
                if (this.mService.mProcessesReady && !r.timeoutExempt) {
                    long timeoutTime = r.receiverTime + this.mConstants.TIMEOUT;
                    if (timeoutTime > now) {
                        setBroadcastTimeoutLocked(timeoutTime);
                        return;
                    }
                } else {
                    return;
                }
            }
            if (r.state == 4) {
                StringBuilder sb = new StringBuilder();
                sb.append("Waited long enough for: ");
                sb.append(r.curComponent != null ? r.curComponent.flattenToShortString() : "(null)");
                Slog.i("BroadcastQueue", sb.toString());
                r.curComponent = null;
                r.state = 0;
                processNextBroadcast(false);
                return;
            }
            if (r.curApp != null && r.curApp.isDebugging()) {
                debugging = true;
            }
            Slog.w("BroadcastQueue", "Timeout of broadcast " + r + " - receiver=" + r.receiver + ", started " + (now - r.receiverTime) + "ms ago");
            r.receiverTime = now;
            if (!debugging) {
                r.anrCount++;
            }
            ProcessRecord app2 = null;
            if (r.nextReceiver > 0) {
                Object curReceiver2 = r.receivers.get(r.nextReceiver - 1);
                r.delivery[r.nextReceiver - 1] = 3;
                curReceiver = curReceiver2;
            } else {
                curReceiver = r.curReceiver;
            }
            Slog.w("BroadcastQueue", "Receiver during timeout of " + r + " : " + curReceiver);
            logBroadcastReceiverDiscardLocked(r);
            if (curReceiver == null || !(curReceiver instanceof BroadcastFilter)) {
                app = r.curApp;
            } else {
                BroadcastFilter bf = (BroadcastFilter) curReceiver;
                if (!(bf.receiverList.pid == 0 || bf.receiverList.pid == ActivityManagerService.MY_PID)) {
                    synchronized (this.mService.mPidsSelfLocked) {
                        app2 = this.mService.mPidsSelfLocked.get(bf.receiverList.pid);
                    }
                }
                app = app2;
            }
            if (app != null) {
                anrMessage = "Broadcast of " + r.intent.toString();
            } else {
                anrMessage = null;
            }
            if (this.mPendingBroadcast == r) {
                this.mPendingBroadcast = null;
            }
            finishReceiverLocked(r, r.resultCode, r.resultData, r.resultExtras, r.resultAbort, false);
            scheduleBroadcastsLocked();
            if (!debugging && anrMessage != null) {
                this.mHandler.post(new AppNotResponding(app, anrMessage));
            }
        }
    }

    private final int ringAdvance(int x, int increment, int ringSize) {
        int x2 = x + increment;
        if (x2 < 0) {
            return ringSize - 1;
        }
        if (x2 >= ringSize) {
            return 0;
        }
        return x2;
    }

    private final void addBroadcastToHistoryLocked(BroadcastRecord original) {
        if (original.callingUid >= 0) {
            original.finishTime = SystemClock.uptimeMillis();
            if (Trace.isTagEnabled(64)) {
                Trace.asyncTraceEnd(64, createBroadcastTraceTitle(original, 1), System.identityHashCode(original));
            }
            BroadcastRecord historyRecord = original.maybeStripForHistory();
            BroadcastRecord[] broadcastRecordArr = this.mBroadcastHistory;
            int i = this.mHistoryNext;
            broadcastRecordArr[i] = historyRecord;
            this.mHistoryNext = ringAdvance(i, 1, MAX_BROADCAST_HISTORY);
            this.mBroadcastSummaryHistory[this.mSummaryHistoryNext] = historyRecord.intent;
            this.mSummaryHistoryEnqueueTime[this.mSummaryHistoryNext] = historyRecord.enqueueClockTime;
            this.mSummaryHistoryDispatchTime[this.mSummaryHistoryNext] = historyRecord.dispatchClockTime;
            this.mSummaryHistoryFinishTime[this.mSummaryHistoryNext] = System.currentTimeMillis();
            this.mSummaryHistoryNext = ringAdvance(this.mSummaryHistoryNext, 1, MAX_BROADCAST_SUMMARY_HISTORY);
        }
    }

    /* access modifiers changed from: package-private */
    public boolean cleanupDisabledPackageReceiversLocked(String packageName, Set<String> filterByClasses, int userId, boolean doit) {
        boolean didSomething = false;
        for (int i = this.mParallelBroadcasts.size() - 1; i >= 0; i--) {
            didSomething |= this.mParallelBroadcasts.get(i).cleanupDisabledPackageReceiversLocked(packageName, filterByClasses, userId, doit);
            if (!doit && didSomething) {
                return true;
            }
        }
        return didSomething | this.mDispatcher.cleanupDisabledPackageReceiversLocked(packageName, filterByClasses, userId, doit);
    }

    /* access modifiers changed from: package-private */
    public final void logBroadcastReceiverDiscardLocked(BroadcastRecord r) {
        int logIndex = r.nextReceiver - 1;
        if (logIndex < 0 || logIndex >= r.receivers.size()) {
            if (logIndex < 0) {
                Slog.w("BroadcastQueue", "Discarding broadcast before first receiver is invoked: " + r);
            }
            EventLog.writeEvent(EventLogTags.AM_BROADCAST_DISCARD_APP, new Object[]{-1, Integer.valueOf(System.identityHashCode(r)), r.intent.getAction(), Integer.valueOf(r.nextReceiver), "NONE"});
            return;
        }
        Object curReceiver = r.receivers.get(logIndex);
        if (curReceiver instanceof BroadcastFilter) {
            BroadcastFilter bf = (BroadcastFilter) curReceiver;
            EventLog.writeEvent(EventLogTags.AM_BROADCAST_DISCARD_FILTER, new Object[]{Integer.valueOf(bf.owningUserId), Integer.valueOf(System.identityHashCode(r)), r.intent.getAction(), Integer.valueOf(logIndex), Integer.valueOf(System.identityHashCode(bf))});
            return;
        }
        ResolveInfo ri = (ResolveInfo) curReceiver;
        EventLog.writeEvent(EventLogTags.AM_BROADCAST_DISCARD_APP, new Object[]{Integer.valueOf(UserHandle.getUserId(ri.activityInfo.applicationInfo.uid)), Integer.valueOf(System.identityHashCode(r)), r.intent.getAction(), Integer.valueOf(logIndex), ri.toString()});
    }

    private String createBroadcastTraceTitle(BroadcastRecord record, int state) {
        Object[] objArr = new Object[4];
        objArr[0] = state == 0 ? "in queue" : "dispatched";
        String str = "";
        objArr[1] = record.callerPackage == null ? str : record.callerPackage;
        objArr[2] = record.callerApp == null ? "process unknown" : record.callerApp.toShortString();
        if (record.intent != null) {
            str = record.intent.getAction();
        }
        objArr[3] = str;
        return String.format("Broadcast %s from %s (%s) %s", objArr);
    }

    /* access modifiers changed from: package-private */
    public boolean isIdle() {
        return this.mParallelBroadcasts.isEmpty() && this.mDispatcher.isEmpty() && this.mPendingBroadcast == null;
    }

    /* access modifiers changed from: package-private */
    public void cancelDeferrals() {
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                this.mDispatcher.cancelDeferralsLocked();
                scheduleBroadcastsLocked();
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    /* access modifiers changed from: package-private */
    public String describeState() {
        String str;
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                str = this.mParallelBroadcasts.size() + " parallel; " + this.mDispatcher.describeStateLocked();
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        return str;
    }

    /* access modifiers changed from: package-private */
    public void writeToProto(ProtoOutputStream proto, long fieldId) {
        int i;
        int lastIndex;
        ProtoOutputStream protoOutputStream = proto;
        long token = proto.start(fieldId);
        protoOutputStream.write(1138166333441L, this.mQueueName);
        for (int i2 = this.mParallelBroadcasts.size() - 1; i2 >= 0; i2--) {
            this.mParallelBroadcasts.get(i2).writeToProto(protoOutputStream, 2246267895810L);
        }
        this.mDispatcher.writeToProto(protoOutputStream, 2246267895811L);
        BroadcastRecord broadcastRecord = this.mPendingBroadcast;
        if (broadcastRecord != null) {
            broadcastRecord.writeToProto(protoOutputStream, 1146756268036L);
        }
        int lastIndex2 = this.mHistoryNext;
        int ringIndex = lastIndex2;
        do {
            i = -1;
            ringIndex = ringAdvance(ringIndex, -1, MAX_BROADCAST_HISTORY);
            BroadcastRecord r = this.mBroadcastHistory[ringIndex];
            if (r != null) {
                r.writeToProto(protoOutputStream, 2246267895813L);
                continue;
            }
        } while (ringIndex != lastIndex2);
        int i3 = this.mSummaryHistoryNext;
        int ringIndex2 = i3;
        int lastIndex3 = i3;
        while (true) {
            int ringIndex3 = ringAdvance(ringIndex2, i, MAX_BROADCAST_SUMMARY_HISTORY);
            Intent intent = this.mBroadcastSummaryHistory[ringIndex3];
            if (intent == null) {
                lastIndex = lastIndex3;
            } else {
                lastIndex = lastIndex3;
                intent.writeToProto(proto, 1146756268033L, false, true, true, false);
                protoOutputStream.write(1112396529666L, this.mSummaryHistoryEnqueueTime[ringIndex3]);
                protoOutputStream.write(1112396529667L, this.mSummaryHistoryDispatchTime[ringIndex3]);
                protoOutputStream.write(1112396529668L, this.mSummaryHistoryFinishTime[ringIndex3]);
                protoOutputStream.end(protoOutputStream.start(2246267895814L));
            }
            int lastIndex4 = lastIndex;
            if (ringIndex3 == lastIndex4) {
                protoOutputStream.end(token);
                return;
            }
            lastIndex3 = lastIndex4;
            ringIndex2 = ringIndex3;
            i = -1;
        }
    }

    /* access modifiers changed from: package-private */
    public final boolean dumpLocked(FileDescriptor fd, PrintWriter pw, String[] args, int opti, boolean dumpAll, String dumpPackage, boolean needSep) {
        boolean needSep2;
        String str;
        int lastIndex;
        int lastIndex2;
        boolean printed;
        BroadcastRecord broadcastRecord;
        PrintWriter printWriter = pw;
        String str2 = dumpPackage;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String str3 = ":";
        if (!this.mParallelBroadcasts.isEmpty() || !this.mDispatcher.isEmpty() || this.mPendingBroadcast != null) {
            boolean printed2 = false;
            boolean needSep3 = needSep;
            for (int i = this.mParallelBroadcasts.size() - 1; i >= 0; i--) {
                BroadcastRecord br = this.mParallelBroadcasts.get(i);
                if (str2 == null || str2.equals(br.callerPackage)) {
                    if (!printed2) {
                        if (needSep3) {
                            pw.println();
                        }
                        needSep3 = true;
                        printed2 = true;
                        printWriter.println("  Active broadcasts [" + this.mQueueName + "]:");
                    }
                    printWriter.println("  Active Broadcast " + this.mQueueName + " #" + i + str3);
                    br.dump(printWriter, "    ", sdf);
                }
            }
            this.mDispatcher.dumpLocked(printWriter, str2, this.mQueueName, sdf);
            if (str2 == null || ((broadcastRecord = this.mPendingBroadcast) != null && str2.equals(broadcastRecord.callerPackage))) {
                pw.println();
                printWriter.println("  Pending broadcast [" + this.mQueueName + "]:");
                BroadcastRecord broadcastRecord2 = this.mPendingBroadcast;
                if (broadcastRecord2 != null) {
                    broadcastRecord2.dump(printWriter, "    ", sdf);
                } else {
                    printWriter.println("    (null)");
                }
                needSep2 = true;
            } else {
                needSep2 = needSep3;
            }
        } else {
            needSep2 = needSep;
        }
        this.mConstants.dump(printWriter);
        boolean printed3 = false;
        int i2 = -1;
        int lastIndex3 = this.mHistoryNext;
        int ringIndex = lastIndex3;
        while (true) {
            int ringIndex2 = ringAdvance(ringIndex, -1, MAX_BROADCAST_HISTORY);
            BroadcastRecord r = this.mBroadcastHistory[ringIndex2];
            int lastIndex4 = lastIndex3;
            int ringIndex3 = ringIndex2;
            if (r == null) {
                str = str3;
            } else {
                i2++;
                if (str2 == null || str2.equals(r.callerPackage)) {
                    if (!printed3) {
                        if (needSep2) {
                            pw.println();
                        }
                        needSep2 = true;
                        StringBuilder sb = new StringBuilder();
                        boolean z = printed3;
                        sb.append("  Historical broadcasts [");
                        sb.append(this.mQueueName);
                        sb.append("]:");
                        printWriter.println(sb.toString());
                        printed = true;
                    } else {
                        printed = printed3;
                    }
                    if (dumpAll) {
                        printWriter.print("  Historical Broadcast " + this.mQueueName + " #");
                        printWriter.print(i2);
                        printWriter.println(str3);
                        r.dump(printWriter, "    ", sdf);
                        str = str3;
                    } else {
                        printWriter.print("  #");
                        printWriter.print(i2);
                        printWriter.print(": ");
                        printWriter.println(r);
                        printWriter.print("    ");
                        str = str3;
                        printWriter.println(r.intent.toShortString(false, true, true, false));
                        if (!(r.targetComp == null || r.targetComp == r.intent.getComponent())) {
                            printWriter.print("    targetComp: ");
                            printWriter.println(r.targetComp.toShortString());
                        }
                        Bundle bundle = r.intent.getExtras();
                        if (bundle != null) {
                            printWriter.print("    extras: ");
                            printWriter.println(bundle.toString());
                        }
                    }
                    printed3 = printed;
                } else {
                    str = str3;
                }
            }
            lastIndex = lastIndex4;
            ringIndex = ringIndex3;
            if (ringIndex == lastIndex) {
                break;
            }
            str2 = dumpPackage;
            lastIndex3 = lastIndex;
            str3 = str;
        }
        if (str2 == null) {
            int lastIndex5 = this.mSummaryHistoryNext;
            int ringIndex4 = lastIndex5;
            if (dumpAll) {
                printed3 = false;
                i2 = -1;
            } else {
                int j = i2;
                while (j > 0 && ringIndex4 != lastIndex5) {
                    ringIndex4 = ringAdvance(ringIndex4, -1, MAX_BROADCAST_SUMMARY_HISTORY);
                    if (this.mBroadcastHistory[ringIndex4] == null) {
                        String str4 = dumpPackage;
                    } else {
                        j--;
                        String str5 = dumpPackage;
                    }
                }
            }
            while (true) {
                ringIndex4 = ringAdvance(ringIndex4, -1, MAX_BROADCAST_SUMMARY_HISTORY);
                Intent intent = this.mBroadcastSummaryHistory[ringIndex4];
                if (intent != null) {
                    if (!printed3) {
                        if (needSep2) {
                            pw.println();
                        }
                        printWriter.println("  Historical broadcasts summary [" + this.mQueueName + "]:");
                        printed3 = true;
                        needSep2 = true;
                    }
                    if (!dumpAll && i2 >= 50) {
                        printWriter.println("  ...");
                        int i3 = lastIndex5;
                        break;
                    }
                    i2++;
                    printWriter.print("  #");
                    printWriter.print(i2);
                    printWriter.print(": ");
                    boolean printed4 = printed3;
                    printWriter.println(intent.toShortString(false, true, true, false));
                    printWriter.print("    ");
                    lastIndex2 = lastIndex5;
                    TimeUtils.formatDuration(this.mSummaryHistoryDispatchTime[ringIndex4] - this.mSummaryHistoryEnqueueTime[ringIndex4], printWriter);
                    printWriter.print(" dispatch ");
                    TimeUtils.formatDuration(this.mSummaryHistoryFinishTime[ringIndex4] - this.mSummaryHistoryDispatchTime[ringIndex4], printWriter);
                    printWriter.println(" finish");
                    printWriter.print("    enq=");
                    printWriter.print(sdf.format(new Date(this.mSummaryHistoryEnqueueTime[ringIndex4])));
                    printWriter.print(" disp=");
                    printWriter.print(sdf.format(new Date(this.mSummaryHistoryDispatchTime[ringIndex4])));
                    printWriter.print(" fin=");
                    printWriter.println(sdf.format(new Date(this.mSummaryHistoryFinishTime[ringIndex4])));
                    Bundle bundle2 = intent.getExtras();
                    if (bundle2 != null) {
                        printWriter.print("    extras: ");
                        printWriter.println(bundle2.toString());
                    }
                    printed3 = printed4;
                } else {
                    lastIndex2 = lastIndex5;
                }
                int lastIndex6 = lastIndex2;
                if (ringIndex4 == lastIndex6) {
                    break;
                }
                lastIndex5 = lastIndex6;
            }
        }
        return needSep2;
    }
}
