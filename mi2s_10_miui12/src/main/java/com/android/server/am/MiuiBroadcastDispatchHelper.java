package com.android.server.am;

import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.os.SystemProperties;
import com.android.server.ServiceThread;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;
import miui.util.ReflectionUtils;

public class MiuiBroadcastDispatchHelper {
    private static final int ANDROID_VERSION_N = 24;
    private static final boolean DEBUG_BROADCAST = false;
    private static final boolean DEBUG_BROADCAST_LIGHT = false;
    static final long DEFAULT_SLEEP_TIME = 5;
    static final long DISPATCH_SLEEP_TIME = 8;
    static final String HEAVY_CONSUMING_INTENT = "android.net.conn.CONNECTIVITY_CHANGE";
    static final long HEAVY_CONSUMING_INTENT_SLEEP_TIME = 50;
    static final long LIMIT_DISPATCH_SLEEP_TIME = 10;
    private static final int MAX_DISPATCH_INTENT_COUNT = 3;
    static final int MAX_RECEIVERS_SIZE = 30;
    static final int MIN_RECEIVERS_SIZE = 3;
    static final int MSG_PROCESS_NEXT_BROADCAST_FROM_AMS = 150001;
    static final long ONE_DISPATCH_MAX_TIME = 10;
    static final String PROP_MIUI_BROADCAST_DISPATCH = "persist.sys.m_b_dispatch";
    static final long SHORT_SLEEP_TIME = 3;
    private static MiuiBroadcastDispatchHelper mInstance;
    private Method mAddBroadcastToHistoryLocked;
    private ActivityManagerService mAms;
    private Method mDeliverToRegisteredReceiverLocked;
    private boolean mDispatchSleep = true;
    private final boolean mEnable;
    private ServiceThread mHandlerThread;
    private HashSet<String> timeConsumingIntents = new HashSet<>();

    private MiuiBroadcastDispatchHelper(ActivityManagerService ams) {
        this.mAms = ams;
        findDeliverToRegisteredReceiverLocked();
        findAddBroadcastToHistoryLocked();
        if (this.mDeliverToRegisteredReceiverLocked == null || this.mAddBroadcastToHistoryLocked == null) {
            this.mEnable = false;
            return;
        }
        this.mEnable = SystemProperties.getBoolean(PROP_MIUI_BROADCAST_DISPATCH, false);
        if (this.mEnable) {
            this.mHandlerThread = new ServiceThread("BroadcastQueueInjector", -2, false);
            this.mHandlerThread.start();
        }
    }

    public static void init(ActivityManagerService ams) {
        mInstance = new MiuiBroadcastDispatchHelper(ams);
    }

    public static void setMiuiBroadcastDispatchEnable(boolean enable) {
        if (getInstance() != null) {
            getInstance().setEnable(enable);
        }
    }

    public static void addTimeConsumingIntent(String[] actions) {
        if (getInstance() != null) {
            getInstance().addIntent(actions);
        }
    }

    public static void removeTimeConsumingIntent(String[] actions) {
        if (getInstance() != null) {
            getInstance().removeIntent(actions);
        }
    }

    public static void clearTimeConsumingIntent() {
        if (getInstance() != null) {
            getInstance().clearIntent();
        }
    }

    public static void onScreenOnBroadcastDone() {
        if (getInstance() != null) {
            getInstance().setDispatchSleep(true);
        }
    }

    public static void onScreenOffBroadcastSend() {
        if (getInstance() != null) {
            getInstance().setDispatchSleep(false);
        }
    }

    public static MiuiBroadcastDispatchHelper getInstance() {
        return mInstance;
    }

    private void findDeliverToRegisteredReceiverLocked() {
        if (Build.VERSION.SDK_INT >= 24) {
            Class<BroadcastQueue> cls = BroadcastQueue.class;
            try {
                this.mDeliverToRegisteredReceiverLocked = ReflectionUtils.findMethodExact(cls, "deliverToRegisteredReceiverLocked", new Class[]{BroadcastRecord.class, BroadcastFilter.class, Boolean.TYPE, Integer.TYPE});
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Class<BroadcastQueue> cls2 = BroadcastQueue.class;
            try {
                this.mDeliverToRegisteredReceiverLocked = ReflectionUtils.findMethodExact(cls2, "deliverToRegisteredReceiverLocked", new Class[]{BroadcastRecord.class, BroadcastFilter.class, Boolean.TYPE});
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private void findAddBroadcastToHistoryLocked() {
        try {
            this.mAddBroadcastToHistoryLocked = ReflectionUtils.findMethodExact(BroadcastQueue.class, "addBroadcastToHistoryLocked", new Class[]{BroadcastRecord.class});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isEnable() {
        return this.mEnable;
    }

    public void setEnable(boolean enable) {
        if (enable) {
            SystemProperties.set(PROP_MIUI_BROADCAST_DISPATCH, "true");
        } else {
            SystemProperties.set(PROP_MIUI_BROADCAST_DISPATCH, "false");
        }
    }

    public void setDispatchSleep(boolean dispatchSleep) {
        this.mDispatchSleep = dispatchSleep;
    }

    public boolean isTimeConsumingIntent(String action) {
        boolean contains;
        if (!this.mEnable) {
            return false;
        }
        synchronized (this.timeConsumingIntents) {
            contains = this.timeConsumingIntents.contains(action);
        }
        return contains;
    }

    public long getOrderedSleepTime(BroadcastRecord r) {
        if (!this.mDispatchSleep || !MiuiSysUserServiceHelper.isAllLimit()) {
            return DEFAULT_SLEEP_TIME;
        }
        if (HEAVY_CONSUMING_INTENT.equals(r.intent.getAction()) || isTimeConsumingIntent(r.intent.getAction())) {
            return HEAVY_CONSUMING_INTENT_SLEEP_TIME;
        }
        return 10;
    }

    public void addIntent(String[] actions) {
        if (this.mEnable) {
            synchronized (this.timeConsumingIntents) {
                for (String action : actions) {
                    this.timeConsumingIntents.add(action);
                }
            }
        }
    }

    public void removeIntent(String[] actions) {
        if (this.mEnable) {
            synchronized (this.timeConsumingIntents) {
                for (String action : actions) {
                    this.timeConsumingIntents.remove(action);
                }
            }
        }
    }

    public void clearIntent() {
        if (this.mEnable) {
            synchronized (this.timeConsumingIntents) {
                this.timeConsumingIntents.clear();
            }
        }
    }

    public Looper getLooper() {
        return this.mHandlerThread.getLooper();
    }

    private void deliverToRegisteredReceiverLocked(BroadcastQueue queue, BroadcastRecord r, BroadcastFilter filter, int index) {
        if (Build.VERSION.SDK_INT >= 24) {
            try {
                this.mDeliverToRegisteredReceiverLocked.invoke(queue, new Object[]{r, filter, false, Integer.valueOf(index)});
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                this.mDeliverToRegisteredReceiverLocked.invoke(queue, new Object[]{r, filter, false});
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    private void addBroadcastToHistoryLocked(BroadcastQueue queue, BroadcastRecord r) {
        try {
            this.mAddBroadcastToHistoryLocked.invoke(queue, new Object[]{r});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void shortBroadcastsDispatchLocked(BroadcastQueue queue, BroadcastRecord r) {
        r.dispatchTime = SystemClock.uptimeMillis();
        r.dispatchClockTime = System.currentTimeMillis();
        int N = r.receivers.size();
        for (int i = 0; i < N; i++) {
            deliverToRegisteredReceiverLocked(queue, r, (BroadcastFilter) r.receivers.get(i), i);
        }
        addBroadcastToHistoryLocked(queue, r);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0038, code lost:
        com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection();
        r7 = r11;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:17:0x003e, code lost:
        com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection();
        r8.dispatchTime = android.os.SystemClock.uptimeMillis();
        r8.dispatchClockTime = java.lang.System.currentTimeMillis();
        r7 = HEAVY_CONSUMING_INTENT.equals(r8.intent.getAction());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:18:0x0059, code lost:
        if (r7 == false) goto L_0x005d;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x005b, code lost:
        r0 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x005d, code lost:
        r0 = isTimeConsumingIntent(r8.intent.getAction());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x0067, code lost:
        r10 = r0;
        r14 = r8.receivers.size();
        r5 = 0;
        r15 = false;
        r16 = false;
        r17 = -1000;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:22:0x0078, code lost:
        if (r5 >= r14) goto L_0x011f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:23:0x007a, code lost:
        r3 = android.os.SystemClock.uptimeMillis();
        r6 = r1.mAms;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:24:0x0080, code lost:
        monitor-enter(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:?, code lost:
        com.android.server.am.ActivityManagerService.boostPriorityForLockedSection();
        r0 = r8.receivers.get(r5);
        r15 = false;
        r16 = false;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:27:0x008f, code lost:
        if (r5 >= (r14 - 1)) goto L_0x00c5;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:28:0x0091, code lost:
        r9 = (com.android.server.am.BroadcastFilter) r8.receivers.get(r5 + 1);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:29:0x009f, code lost:
        if (r9.receiverList.app == null) goto L_0x00b1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:31:0x00a9, code lost:
        r18 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x00ac, code lost:
        if (r9.receiverList.app.getCurProcState() > 2) goto L_0x00b3;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:33:0x00ae, code lost:
        r15 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:34:0x00b1, code lost:
        r18 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:37:0x00b9, code lost:
        if (r9.receiverList.pid != com.android.server.am.ActivityManagerService.MY_PID) goto L_0x00be;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:38:0x00bb, code lost:
        r16 = true;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:39:0x00be, code lost:
        r17 = r9.receiverList.uid;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:40:0x00c5, code lost:
        r18 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:41:0x00c7, code lost:
        deliverToRegisteredReceiverLocked(r2, r8, (com.android.server.am.BroadcastFilter) r0, r5);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:42:0x00cd, code lost:
        monitor-exit(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:43:0x00ce, code lost:
        com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:44:0x00d3, code lost:
        if (r1.mDispatchSleep == false) goto L_0x010f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:0x00d5, code lost:
        if (r15 != false) goto L_0x010f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x00d7, code lost:
        if (r16 == false) goto L_0x00da;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00e0, code lost:
        if (com.android.server.am.MiuiSysUserServiceHelper.isAllLimit() == false) goto L_0x00ee;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00e2, code lost:
        if (r10 == false) goto L_0x00ea;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00e4, code lost:
        sleep(HEAVY_CONSUMING_INTENT_SLEEP_TIME);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:51:0x00ea, code lost:
        sleep(10);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:53:0x00f6, code lost:
        if ((android.os.SystemClock.uptimeMillis() - r3) <= 10) goto L_0x00fe;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:54:0x00f8, code lost:
        sleep(8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:56:0x0102, code lost:
        if ((r14 - r5) <= 30) goto L_0x010a;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:57:0x0104, code lost:
        sleep(DEFAULT_SLEEP_TIME);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x010a, code lost:
        sleep(3);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x010f, code lost:
        r5 = r5 + 1;
        r7 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x0115, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:61:0x0116, code lost:
        r18 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:63:?, code lost:
        monitor-exit(r6);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:64:0x0119, code lost:
        com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:0x011c, code lost:
        throw r0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x011d, code lost:
        r0 = th;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x011f, code lost:
        r18 = r7;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:68:0x0121, code lost:
        if (r10 == false) goto L_0x0152;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:69:0x0123, code lost:
        android.util.Slog.i("BroadcastQueueInjector", "MBDH Done with parallel broadcast [" + r2.mQueueName + "] " + r8 + " total used : " + (android.os.SystemClock.uptimeMillis() - r8.dispatchTime));
     */
    /* JADX WARNING: Code restructure failed: missing block: B:70:0x0152, code lost:
        r7 = r1.mAms;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:71:0x0154, code lost:
        monitor-enter(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:?, code lost:
        com.android.server.am.ActivityManagerService.boostPriorityForLockedSection();
        addBroadcastToHistoryLocked(r2, r8);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x015b, code lost:
        monitor-exit(r7);
     */
    /* JADX WARNING: Code restructure failed: missing block: B:75:0x015c, code lost:
        com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection();
        r4 = r10;
        r7 = r11;
        r5 = r15;
        r6 = r16;
        r9 = r17;
        r3 = r18;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:76:0x016a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:79:0x016c, code lost:
        com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:0x016f, code lost:
        throw r0;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void dispatchParallelBroadcasts(com.android.server.am.BroadcastQueue r22, java.util.ArrayList<com.android.server.am.BroadcastRecord> r23) {
        /*
            r21 = this;
            r1 = r21
            r2 = r22
            r0 = 0
            r3 = 0
            r4 = 0
            r5 = 0
            r6 = -1000(0xfffffffffffffc18, float:NaN)
            r7 = 0
            r8 = 0
            r9 = r6
            r6 = r5
            r5 = r4
            r4 = r3
            r3 = r0
        L_0x0011:
            r0 = 0
            r10 = 3
            if (r7 >= r10) goto L_0x0181
            int r11 = r7 + 1
            com.android.server.am.ActivityManagerService r12 = r1.mAms
            monitor-enter(r12)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0177 }
            int r7 = r23.size()     // Catch:{ all -> 0x0177 }
            if (r7 <= 0) goto L_0x0170
            r13 = r23
            java.lang.Object r0 = r13.remove(r0)     // Catch:{ all -> 0x017f }
            com.android.server.am.BroadcastRecord r0 = (com.android.server.am.BroadcastRecord) r0     // Catch:{ all -> 0x017f }
            r8 = r0
            java.util.List r0 = r8.receivers     // Catch:{ all -> 0x017f }
            int r0 = r0.size()     // Catch:{ all -> 0x017f }
            if (r0 > r10) goto L_0x003d
            r1.shortBroadcastsDispatchLocked(r2, r8)     // Catch:{ all -> 0x017f }
            monitor-exit(r12)     // Catch:{ all -> 0x017f }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            r7 = r11
            goto L_0x0011
        L_0x003d:
            monitor-exit(r12)     // Catch:{ all -> 0x017f }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            long r14 = android.os.SystemClock.uptimeMillis()
            r8.dispatchTime = r14
            long r14 = java.lang.System.currentTimeMillis()
            r8.dispatchClockTime = r14
            android.content.Intent r0 = r8.intent
            java.lang.String r0 = r0.getAction()
            java.lang.String r7 = "android.net.conn.CONNECTIVITY_CHANGE"
            boolean r7 = r7.equals(r0)
            if (r7 == 0) goto L_0x005d
            r0 = 1
            goto L_0x0067
        L_0x005d:
            android.content.Intent r0 = r8.intent
            java.lang.String r0 = r0.getAction()
            boolean r0 = r1.isTimeConsumingIntent(r0)
        L_0x0067:
            r10 = r0
            r0 = 0
            r3 = 0
            r4 = -1000(0xfffffffffffffc18, float:NaN)
            java.util.List r5 = r8.receivers
            int r14 = r5.size()
            r5 = 0
            r15 = r0
            r16 = r3
            r17 = r4
        L_0x0078:
            if (r5 >= r14) goto L_0x011f
            long r3 = android.os.SystemClock.uptimeMillis()
            com.android.server.am.ActivityManagerService r6 = r1.mAms
            monitor-enter(r6)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0115 }
            java.util.List r0 = r8.receivers     // Catch:{ all -> 0x0115 }
            java.lang.Object r0 = r0.get(r5)     // Catch:{ all -> 0x0115 }
            r15 = 0
            r16 = 0
            int r9 = r14 + -1
            if (r5 >= r9) goto L_0x00c5
            java.util.List r9 = r8.receivers     // Catch:{ all -> 0x0115 }
            int r12 = r5 + 1
            java.lang.Object r9 = r9.get(r12)     // Catch:{ all -> 0x0115 }
            com.android.server.am.BroadcastFilter r9 = (com.android.server.am.BroadcastFilter) r9     // Catch:{ all -> 0x0115 }
            com.android.server.am.ReceiverList r12 = r9.receiverList     // Catch:{ all -> 0x0115 }
            com.android.server.am.ProcessRecord r12 = r12.app     // Catch:{ all -> 0x0115 }
            if (r12 == 0) goto L_0x00b1
            com.android.server.am.ReceiverList r12 = r9.receiverList     // Catch:{ all -> 0x0115 }
            com.android.server.am.ProcessRecord r12 = r12.app     // Catch:{ all -> 0x0115 }
            int r12 = r12.getCurProcState()     // Catch:{ all -> 0x0115 }
            r18 = r7
            r7 = 2
            if (r12 > r7) goto L_0x00b3
            r7 = 1
            r15 = r7
            goto L_0x00b3
        L_0x00b1:
            r18 = r7
        L_0x00b3:
            com.android.server.am.ReceiverList r7 = r9.receiverList     // Catch:{ all -> 0x011d }
            int r7 = r7.pid     // Catch:{ all -> 0x011d }
            int r12 = com.android.server.am.ActivityManagerService.MY_PID     // Catch:{ all -> 0x011d }
            if (r7 != r12) goto L_0x00be
            r7 = 1
            r16 = r7
        L_0x00be:
            com.android.server.am.ReceiverList r7 = r9.receiverList     // Catch:{ all -> 0x011d }
            int r7 = r7.uid     // Catch:{ all -> 0x011d }
            r17 = r7
            goto L_0x00c7
        L_0x00c5:
            r18 = r7
        L_0x00c7:
            r7 = r0
            com.android.server.am.BroadcastFilter r7 = (com.android.server.am.BroadcastFilter) r7     // Catch:{ all -> 0x011d }
            r1.deliverToRegisteredReceiverLocked(r2, r8, r7, r5)     // Catch:{ all -> 0x011d }
            monitor-exit(r6)     // Catch:{ all -> 0x011d }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            boolean r0 = r1.mDispatchSleep
            if (r0 == 0) goto L_0x010f
            if (r15 != 0) goto L_0x010f
            if (r16 == 0) goto L_0x00da
            goto L_0x010f
        L_0x00da:
            boolean r0 = com.android.server.am.MiuiSysUserServiceHelper.isAllLimit()
            r6 = 10
            if (r0 == 0) goto L_0x00ee
            if (r10 == 0) goto L_0x00ea
            r6 = 50
            r1.sleep(r6)
            goto L_0x010f
        L_0x00ea:
            r1.sleep(r6)
            goto L_0x010f
        L_0x00ee:
            long r19 = android.os.SystemClock.uptimeMillis()
            long r19 = r19 - r3
            int r0 = (r19 > r6 ? 1 : (r19 == r6 ? 0 : -1))
            if (r0 <= 0) goto L_0x00fe
            r6 = 8
            r1.sleep(r6)
            goto L_0x010f
        L_0x00fe:
            int r0 = r14 - r5
            r6 = 30
            if (r0 <= r6) goto L_0x010a
            r6 = 5
            r1.sleep(r6)
            goto L_0x010f
        L_0x010a:
            r6 = 3
            r1.sleep(r6)
        L_0x010f:
            int r5 = r5 + 1
            r7 = r18
            goto L_0x0078
        L_0x0115:
            r0 = move-exception
            r18 = r7
        L_0x0118:
            monitor-exit(r6)     // Catch:{ all -> 0x011d }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x011d:
            r0 = move-exception
            goto L_0x0118
        L_0x011f:
            r18 = r7
            if (r10 == 0) goto L_0x0152
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r3 = "MBDH Done with parallel broadcast ["
            r0.append(r3)
            java.lang.String r3 = r2.mQueueName
            r0.append(r3)
            java.lang.String r3 = "] "
            r0.append(r3)
            r0.append(r8)
            java.lang.String r3 = " total used : "
            r0.append(r3)
            long r3 = android.os.SystemClock.uptimeMillis()
            long r5 = r8.dispatchTime
            long r3 = r3 - r5
            r0.append(r3)
            java.lang.String r0 = r0.toString()
            java.lang.String r3 = "BroadcastQueueInjector"
            android.util.Slog.i(r3, r0)
        L_0x0152:
            com.android.server.am.ActivityManagerService r7 = r1.mAms
            monitor-enter(r7)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x016a }
            r1.addBroadcastToHistoryLocked(r2, r8)     // Catch:{ all -> 0x016a }
            monitor-exit(r7)     // Catch:{ all -> 0x016a }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            r4 = r10
            r7 = r11
            r5 = r15
            r6 = r16
            r9 = r17
            r3 = r18
            goto L_0x0011
        L_0x016a:
            r0 = move-exception
            monitor-exit(r7)     // Catch:{ all -> 0x016a }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x0170:
            r13 = r23
            monitor-exit(r12)     // Catch:{ all -> 0x017f }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0177:
            r0 = move-exception
            r13 = r23
        L_0x017a:
            monitor-exit(r12)     // Catch:{ all -> 0x017f }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x017f:
            r0 = move-exception
            goto L_0x017a
        L_0x0181:
            r13 = r23
            com.android.server.am.ActivityManagerService r10 = r1.mAms
            monitor-enter(r10)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0199 }
            int r11 = r23.size()     // Catch:{ all -> 0x0199 }
            if (r11 <= 0) goto L_0x0194
            r2.mBroadcastsScheduled = r0     // Catch:{ all -> 0x0199 }
            r22.scheduleBroadcastsLocked()     // Catch:{ all -> 0x0199 }
        L_0x0194:
            monitor-exit(r10)     // Catch:{ all -> 0x0199 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0199:
            r0 = move-exception
            monitor-exit(r10)     // Catch:{ all -> 0x0199 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.MiuiBroadcastDispatchHelper.dispatchParallelBroadcasts(com.android.server.am.BroadcastQueue, java.util.ArrayList):void");
    }

    public void dump(PrintWriter pw) {
        pw.println("  MIUI ADD :  MBDH dump start : ");
        pw.println("  Heavy Time Consuming Intents : ");
        pw.print(" action : ");
        pw.println(HEAVY_CONSUMING_INTENT);
        pw.println("  Time Consuming Intents : ");
        synchronized (this.timeConsumingIntents) {
            Iterator<String> iterator = this.timeConsumingIntents.iterator();
            while (iterator.hasNext()) {
                pw.print(" action : ");
                pw.println(iterator.next());
            }
        }
        pw.print(" enable : ");
        pw.println(this.mEnable);
        pw.print(" dispatchSleep : ");
        pw.println(this.mDispatchSleep);
        pw.println("  MBDH dump end !!!!");
    }
}
