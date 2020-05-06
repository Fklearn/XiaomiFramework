package com.android.server.am;

import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.miui.AppOpsUtils;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.server.am.SplitScreenReporter;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.server.am.MiuiWarnings;
import com.android.server.notification.NotificationShellCmd;
import com.android.server.pm.PackageManagerService;
import com.android.server.slice.SliceClientPermissions;
import com.miui.whetstone.client.WhetstoneClientManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import miui.content.pm.PreloadedAppPolicy;
import miui.mqsas.sdk.MQSEventManagerDelegate;
import miui.mqsas.sdk.event.BroadcastEvent;
import miui.os.Build;
import miui.security.WakePathChecker;

public class BroadcastQueueInjector {
    private static final float ABNORMAL_BROADCAST_RATE = 0.6f;
    private static final String ACTION_C2DM = "com.google.android.c2dm.intent.RECEIVE";
    private static final int ACTIVE_ORDERED_BROADCAST_LIMIT = SystemProperties.getInt("persist.activebr.limit", 1000);
    private static ArrayList<BroadcastEvent> BR_LIST = new ArrayList<>();
    private static final boolean DEBUG = true;
    public static final String EXTRA_PACKAGE_NAME = "android.intent.extra.PACKAGE_NAME";
    public static final int FLAG_IMMUTABLE = 67108864;
    private static final boolean IS_STABLE_VERSION = Build.IS_STABLE_VERSION;
    private static final int MAX_QUANTITY = 30;
    public static final int OP_PROCESS_OUTGOING_CALLS = 54;
    static final String TAG = "BroadcastQueueInjector";
    private static volatile BRReportHandler mBRHandler;
    private static ArrayList<BroadcastMap> mBroadcastMap = new ArrayList<>();
    private static long mDispatchThreshold = SystemProperties.getLong("persist.broadcast.time", 3000);
    private static int mFinishDeno = SystemProperties.getInt("persist.broadcast.count", 5);
    private static int mIndex = 0;
    private static final Object mObject = new Object();
    /* access modifiers changed from: private */
    public static AtomicBoolean sAbnormalBroadcastWarning = new AtomicBoolean(false);
    private static int sActivityRequestId;
    private static ArrayMap<String, ArrayList<String>> sSystemAppSkipAction = new ArrayMap<>();
    private static boolean sSystemBootCompleted;
    private static ArrayList<String> sSystemSkipAction = new ArrayList<>();

    static {
        sSystemSkipAction.add("android.accounts.LOGIN_ACCOUNTS_PRE_CHANGED");
        sSystemSkipAction.add("android.accounts.LOGIN_ACCOUNTS_POST_CHANGED");
    }

    private static class BroadcastMap {
        private String action;
        private String packageName;

        public BroadcastMap(String action2, String packageName2) {
            this.action = action2;
            this.packageName = packageName2;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof BroadcastMap)) {
                return super.equals(obj);
            }
            BroadcastMap broadcastMapObj = (BroadcastMap) obj;
            return this.action.equals(broadcastMapObj.action) && this.packageName.equals(broadcastMapObj.packageName);
        }

        public String toString() {
            return "action: " + this.action + ", packageName: " + this.packageName;
        }
    }

    private static final class BRReportHandler extends Handler {
        static final int BROADCAST_RECORDS = 1;
        static final int BROADCAST_TIME_RECORDS = 0;

        public BRReportHandler(Looper looper) {
            super(looper, (Handler.Callback) null);
        }

        public void handleMessage(Message msg) {
            if (msg.what != 1) {
                Slog.w(BroadcastQueueInjector.TAG, "wrong message received of BRReportHandler");
                return;
            }
            try {
                ParceledListSlice<BroadcastEvent> reportEvents = (ParceledListSlice) msg.obj;
                Slog.d(BroadcastQueueInjector.TAG, "reporting BROADCAST_RECORDS : " + BroadcastQueueInjector.isSystemBootCompleted());
                if (reportEvents != null && BroadcastQueueInjector.isSystemBootCompleted()) {
                    MQSEventManagerDelegate.getInstance().reportBroadcastEvent(reportEvents);
                }
            } catch (Exception e) {
                Slog.e(BroadcastQueueInjector.TAG, "report message record error.", e);
            }
        }
    }

    static boolean checkReceiverAppDealBroadcast(BroadcastQueue bq, ActivityManagerService s, BroadcastRecord r, ProcessRecord app, boolean isStatic) {
        if (isStatic) {
            checkAbnormalBroadcastInQueueLocked(s, bq);
        }
        if (app == null || r == null || r.intent == null || WhetstoneClientManager.isBroadcastAllowedLocked(app.pid, app.uid, r.intent.getAction())) {
            return true;
        }
        Slog.v(TAG, "Skipping " + r + " to " + app);
        if (!r.ordered && !isStatic) {
            return false;
        }
        bq.skipCurrentReceiverLocked(app);
        return false;
    }

    static boolean checkApplicationAutoStart(BroadcastQueue bq, ActivityManagerService s, BroadcastRecord r, ResolveInfo info) {
        checkAbnormalBroadcastInQueueLocked(s, bq);
        if (AppOpsUtils.isXOptMode()) {
            return true;
        }
        String action = r.intent.getAction();
        if (Build.IS_INTERNATIONAL_BUILD && ACTION_C2DM.equals(action)) {
            return true;
        }
        String reason = null;
        if (WakePathChecker.getInstance().checkBroadcastWakePath(r.intent, r.callerPackage, r.callerApp != null ? r.callerApp.info : null, info, r.userId)) {
            boolean isSystem = (info.activityInfo.applicationInfo.flags & 1) != 0 || PreloadedAppPolicy.isProtectedDataApp(s.mContext, info.activityInfo.applicationInfo.packageName, 0);
            if (r.intent.getComponent() != null || (isSystem && r.intent.getPackage() != null && !"com.xiaomi.mipush.MESSAGE_ARRIVED".equals(action))) {
                return true;
            }
            boolean abort = false;
            if (isSystem) {
                if (sSystemSkipAction.contains(action)) {
                    return true;
                }
                ArrayList<String> skipActions = sSystemAppSkipAction.get(info.activityInfo.applicationInfo.packageName);
                if (skipActions != null && skipActions.contains(action)) {
                    return true;
                }
            }
            if (isSystem && "android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
                abort = true;
                reason = " system app CONNECTIVITY_CHANGE";
            }
            if (!abort) {
                if (AppOpsUtils.noteApplicationAutoStart(s.mContext, info.activityInfo.applicationInfo.packageName, info.activityInfo.applicationInfo.uid) == 0) {
                    return true;
                }
                reason = " auto start";
            }
        } else {
            reason = " weak path";
        }
        Slog.w(TAG, "Unable to launch app " + info.activityInfo.applicationInfo.packageName + SliceClientPermissions.SliceAuthority.DELIMITER + info.activityInfo.applicationInfo.uid + " for broadcast " + r.intent + ": process is not permitted to " + reason);
        bq.logBroadcastReceiverDiscardLocked(r);
        bq.finishReceiverLocked(r, r.resultCode, r.resultData, r.resultExtras, r.resultAbort, false);
        bq.scheduleBroadcastsLocked();
        r.state = 0;
        return false;
    }

    static boolean isSkip(ActivityManagerService service, BroadcastRecord r, ResolveInfo info, int appOp) {
        return isSKipNotifySms(service, r, info.activityInfo.applicationInfo.uid, info.activityInfo.packageName, appOp);
    }

    static boolean isSkip(ActivityManagerService service, BroadcastRecord r, BroadcastFilter filter, int appOp) {
        return isSKipNotifySms(service, r, filter.receiverList.uid, filter.packageName, appOp);
    }

    static boolean isSKipNotifySms(ActivityManagerService service, BroadcastRecord r, int uid, String packageName, int appOp) {
        if (appOp != 16) {
            return false;
        }
        Intent intent = r.intent;
        if (!"android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
            return false;
        }
        try {
            if (intent.getBooleanExtra("miui.intent.SERVICE_NUMBER", false) && service.mAppOpsService.checkOperation(10018, uid, packageName) != 0) {
                Slog.w(TAG, "MIUILOG- Sms Filter packageName : " + packageName + " uid " + uid);
                return true;
            }
        } catch (Exception e) {
            Slog.e(TAG, "isSKipNotifySms", e);
        }
        return false;
    }

    static boolean isSkipForUser(ActivityManagerService service, ResolveInfo info, boolean skip) {
        if (!ActivityManagerServiceCompat.isUserRunning(service, UserHandle.getUserId(info.activityInfo.applicationInfo.uid))) {
            return true;
        }
        return skip;
    }

    static Handler getBRReportHandler() {
        if (mBRHandler == null) {
            synchronized (mObject) {
                if (mBRHandler == null) {
                    HandlerThread mBRThread = new HandlerThread("brreport-thread");
                    mBRThread.start();
                    mBRHandler = new BRReportHandler(mBRThread.getLooper());
                }
            }
        }
        return mBRHandler;
    }

    /* access modifiers changed from: private */
    public static boolean isSystemBootCompleted() {
        if (!sSystemBootCompleted) {
            sSystemBootCompleted = SplitScreenReporter.ACTION_ENTER_SPLIT.equals(SystemProperties.get("sys.boot_completed"));
        }
        return sSystemBootCompleted;
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x009e  */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x00df  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void onBroadcastFinished(android.content.Intent r20, java.lang.String r21, int r22, long r23, long r25, long r27, long r29, int r31) {
        /*
            r0 = r23
            r2 = r25
            r4 = r27
            r6 = r31
            boolean r7 = IS_STABLE_VERSION
            if (r7 == 0) goto L_0x000d
            return
        L_0x000d:
            java.lang.String r7 = r20.getAction()
            if (r7 != 0) goto L_0x0016
            java.lang.String r7 = "null"
        L_0x0016:
            if (r21 != 0) goto L_0x001b
            java.lang.String r8 = "android"
            goto L_0x001d
        L_0x001b:
            r8 = r21
        L_0x001d:
            r9 = r22
            java.lang.String r10 = ""
            r11 = r8
            long r12 = java.lang.System.currentTimeMillis()
            long r14 = r2 - r0
            long r16 = mDispatchThreshold
            int r14 = (r14 > r16 ? 1 : (r14 == r16 ? 0 : -1))
            if (r14 >= 0) goto L_0x0040
            long r16 = r4 - r2
            r18 = r12
            long r12 = (long) r6
            long r12 = r12 * r29
            int r14 = mFinishDeno
            long r14 = (long) r14
            long r12 = r12 / r14
            int r12 = (r16 > r12 ? 1 : (r16 == r12 ? 0 : -1))
            if (r12 < 0) goto L_0x003e
            goto L_0x0042
        L_0x003e:
            r12 = 0
            goto L_0x0043
        L_0x0040:
            r18 = r12
        L_0x0042:
            r12 = 1
        L_0x0043:
            int r15 = mIndex
            r13 = 30
            if (r15 < 0) goto L_0x004b
            if (r15 <= r13) goto L_0x004c
        L_0x004b:
            r15 = 0
        L_0x004c:
            mIndex = r15
            com.android.server.am.BroadcastQueueInjector$BroadcastMap r14 = new com.android.server.am.BroadcastQueueInjector$BroadcastMap
            r14.<init>(r7, r8)
            int r15 = mIndex
            if (r15 == 0) goto L_0x0096
            if (r15 > r13) goto L_0x0096
            java.util.ArrayList<com.android.server.am.BroadcastQueueInjector$BroadcastMap> r15 = mBroadcastMap
            boolean r15 = r15.contains(r14)
            if (r15 == 0) goto L_0x0096
            java.util.ArrayList<com.android.server.am.BroadcastQueueInjector$BroadcastMap> r13 = mBroadcastMap
            int r13 = r13.indexOf(r14)
            java.util.ArrayList<miui.mqsas.sdk.event.BroadcastEvent> r15 = BR_LIST
            java.lang.Object r15 = r15.get(r13)
            miui.mqsas.sdk.event.BroadcastEvent r15 = (miui.mqsas.sdk.event.BroadcastEvent) r15
            r15.addCount()
            r16 = r8
            r17 = r9
            long r8 = r4 - r0
            r15.addTotalTime(r8)
            if (r12 == 0) goto L_0x008c
            r15.setReason(r10)
            r15.setEnTime(r0)
            r15.setDisTime(r2)
            r15.setFinTime(r4)
            r15.setNumReceivers(r6)
        L_0x008c:
            r21 = r14
            r9 = r16
            r13 = r17
            r14 = r18
            goto L_0x0117
        L_0x0096:
            r16 = r8
            r17 = r9
            int r8 = mIndex
            if (r8 < r13) goto L_0x00c8
            android.os.Message r8 = android.os.Message.obtain()
            r9 = 1
            r8.what = r9
            android.content.pm.ParceledListSlice r9 = new android.content.pm.ParceledListSlice
            java.util.ArrayList<miui.mqsas.sdk.event.BroadcastEvent> r13 = BR_LIST
            java.lang.Object r13 = r13.clone()
            java.util.ArrayList r13 = (java.util.ArrayList) r13
            r9.<init>(r13)
            r8.obj = r9
            android.os.Handler r9 = getBRReportHandler()
            r9.sendMessage(r8)
            java.util.ArrayList<miui.mqsas.sdk.event.BroadcastEvent> r9 = BR_LIST
            r9.clear()
            java.util.ArrayList<com.android.server.am.BroadcastQueueInjector$BroadcastMap> r9 = mBroadcastMap
            r9.clear()
            r9 = 0
            mIndex = r9
        L_0x00c8:
            java.util.ArrayList<com.android.server.am.BroadcastQueueInjector$BroadcastMap> r8 = mBroadcastMap
            r8.add(r14)
            int r8 = mIndex
            r9 = 1
            int r8 = r8 + r9
            mIndex = r8
            miui.mqsas.sdk.event.BroadcastEvent r8 = new miui.mqsas.sdk.event.BroadcastEvent
            r8.<init>()
            r9 = 64
            r8.setType(r9)
            if (r12 == 0) goto L_0x00ee
            r8.setReason(r10)
            r8.setEnTime(r0)
            r8.setDisTime(r2)
            r8.setFinTime(r4)
            r8.setNumReceivers(r6)
        L_0x00ee:
            r8.setAction(r7)
            r9 = r16
            r8.setCallerPackage(r9)
            r13 = 1
            r8.setCount(r13)
            r21 = r14
            long r13 = r4 - r0
            r8.setTotalTime(r13)
            r13 = r17
            r8.setPid(r13)
            r8.setPackageName(r11)
            r14 = r18
            r8.setTimeStamp(r14)
            r0 = 1
            r8.setSystem(r0)
            java.util.ArrayList<miui.mqsas.sdk.event.BroadcastEvent> r0 = BR_LIST
            r0.add(r8)
        L_0x0117:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.BroadcastQueueInjector.onBroadcastFinished(android.content.Intent, java.lang.String, int, long, long, long, long, int):void");
    }

    public static int noteOperationLocked(int appOp, int uid, String packageName, Handler handler, ActivityManagerService service, BroadcastRecord receiverRecord) {
        int i = appOp;
        final int i2 = uid;
        String str = packageName;
        final ActivityManagerService activityManagerService = service;
        BroadcastRecord broadcastRecord = receiverRecord;
        int mode = activityManagerService.mAppOpsService.checkOperation(i, i2, str);
        if (mode != 5 || UserHandle.getUserId(uid) == 999 || isSKipNotifySms(activityManagerService, broadcastRecord, i2, str, i)) {
            return mode;
        }
        activityManagerService.mAppOpsService.setMode(i, i2, str, 1);
        final Intent intent = new Intent("com.miui.intent.action.REQUEST_PERMISSIONS");
        intent.setPackage("com.lbe.security.miui");
        intent.addFlags(411041792);
        intent.putExtra("android.intent.extra.PACKAGE_NAME", str);
        intent.putExtra("android.intent.extra.UID", i2);
        intent.putExtra("op", i);
        if (!broadcastRecord.sticky) {
            String callerPackage = broadcastRecord.callerPackage;
            int callingUid = broadcastRecord.callingUid;
            if (callerPackage == null) {
                if (callingUid == 0) {
                    callerPackage = "root";
                } else if (callingUid == 2000) {
                    callerPackage = NotificationShellCmd.NOTIFICATION_PACKAGE;
                } else if (callingUid == 1000) {
                    callerPackage = PackageManagerService.PLATFORM_PACKAGE_NAME;
                }
            }
            if (callerPackage == null) {
                return mode;
            }
            int requestCode = getNextRequestIdLocked();
            Intent intentNew = new Intent(broadcastRecord.intent);
            intentNew.setPackage(str);
            PendingIntentController pendingIntentController = activityManagerService.mPendingIntentController;
            int i3 = broadcastRecord.userId;
            Intent[] intentArr = {intentNew};
            int i4 = i3;
            Intent intent2 = intentNew;
            int i5 = callingUid;
            intent.putExtra("android.intent.extra.INTENT", new IntentSender(pendingIntentController.getIntentSender(1, callerPackage, callingUid, i4, (IBinder) null, (String) null, requestCode, intentArr, new String[]{intentNew.resolveType(activityManagerService.mContext.getContentResolver())}, 1275068416, (Bundle) null)));
        }
        Slog.i(TAG, "MIUILOG - Launching Request permission [Broadcast] uid : " + i2 + "  pkg : " + str + " op : " + i);
        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    ActivityManagerService.this.mContext.startActivityAsUser(intent, new UserHandle(UserHandle.getUserId(i2)));
                } catch (Exception e) {
                }
            }
        }, i == 54 ? 1500 : 10);
        return 1;
    }

    private static int getNextRequestIdLocked() {
        if (sActivityRequestId >= Integer.MAX_VALUE) {
            sActivityRequestId = 0;
        }
        sActivityRequestId++;
        return sActivityRequestId;
    }

    private static void checkAbnormalBroadcastInQueueLocked(final ActivityManagerService ams, BroadcastQueue queue) {
        final AbnormalBroadcastRecord r;
        List<BroadcastRecord> broadcasts = queue.mDispatcher.mOrderedBroadcasts;
        if (SystemProperties.getBoolean("persist.sys.miui_optimization", true) && broadcasts != null && !broadcasts.isEmpty() && !sAbnormalBroadcastWarning.get() && broadcasts.size() >= ACTIVE_ORDERED_BROADCAST_LIMIT) {
            sAbnormalBroadcastWarning.set(true);
            final int broadcastCount = broadcasts.size();
            if (broadcastCount < ACTIVE_ORDERED_BROADCAST_LIMIT * 3) {
                r = getAbnormalBroadcastByRateIfExists(broadcasts);
            } else {
                r = getAbnormalBroadcastByCountIfExisted(broadcasts);
            }
            if (r != null) {
                final String packageLabel = getPackageLabelLocked(r, ams);
                queue.mHandler.postAtFrontOfQueue(new Runnable() {
                    public void run() {
                        BroadcastQueueInjector.processAbnormalBroadcast(ActivityManagerService.this, r, packageLabel, broadcastCount);
                    }
                });
                return;
            }
            sAbnormalBroadcastWarning.set(false);
        }
    }

    private static String getPackageLabelLocked(AbnormalBroadcastRecord r, ActivityManagerService ams) {
        CharSequence labelChar;
        String label = null;
        ProcessRecord app = getProcessRecordLocked(r.callerPackage, r.userId, ams);
        if (!(app == null || app.pkgList.size() != 1 || (labelChar = ams.mContext.getPackageManager().getApplicationLabel(app.info)) == null)) {
            label = labelChar.toString();
        }
        if (label == null) {
            return r.callerPackage;
        }
        return label;
    }

    private static ProcessRecord getProcessRecordLocked(String processName, int userId, ActivityManagerService ams) {
        for (int i = ams.mProcessList.mLruProcesses.size() - 1; i >= 0; i--) {
            ProcessRecord app = ams.mProcessList.mLruProcesses.get(i);
            if (app.thread != null && app.processName.equals(processName) && app.userId == userId) {
                return app;
            }
        }
        return null;
    }

    /* access modifiers changed from: private */
    public static void processAbnormalBroadcast(final ActivityManagerService ams, final AbnormalBroadcastRecord r, String packageLabel, int count) {
        boolean showDialogSuccess = false;
        if (count < ACTIVE_ORDERED_BROADCAST_LIMIT * 3) {
            Slog.d(TAG, "abnormal ordered broadcast, showWarningDialog");
            showDialogSuccess = MiuiWarnings.getInstance().showWarningDialog(packageLabel, new MiuiWarnings.WarningCallback() {
                public void onCallback(boolean positive) {
                    if (positive) {
                        BroadcastQueueInjector.forceStopAbnormalApp(ActivityManagerService.this, r);
                        BroadcastQueueInjector.sAbnormalBroadcastWarning.set(false);
                        return;
                    }
                    ActivityManagerService.this.mHandler.postDelayed(new Runnable() {
                        public void run() {
                            BroadcastQueueInjector.sAbnormalBroadcastWarning.set(false);
                        }
                    }, 60000);
                }
            });
        }
        if (!showDialogSuccess) {
            forceStopAbnormalApp(ams, r);
            sAbnormalBroadcastWarning.set(false);
        }
    }

    /* access modifiers changed from: private */
    public static void forceStopAbnormalApp(ActivityManagerService ams, AbnormalBroadcastRecord r) {
        synchronized (ams) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                Slog.d(TAG, "force-stop abnormal app:" + r.callerPackage + " userId:" + r.userId);
                ams.forceStopPackage(r.callerPackage, r.userId, "abnormal ordered broadcast");
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    private static AbnormalBroadcastRecord getAbnormalBroadcastByRateIfExists(List<BroadcastRecord> broadcasts) {
        long startTime = SystemClock.uptimeMillis();
        BroadcastRecord result = broadcasts.get(0);
        int count = 1;
        for (int i = 1; i < broadcasts.size(); i++) {
            if (count == 0) {
                result = broadcasts.get(i);
                count = 1;
            } else if (!TextUtils.equals(result.intent.getAction(), broadcasts.get(i).intent.getAction()) || !TextUtils.equals(result.callerPackage, broadcasts.get(i).callerPackage) || result.userId != broadcasts.get(i).userId) {
                count--;
            } else {
                count++;
            }
        }
        if (TextUtils.isEmpty(result.callerPackage) || TextUtils.equals(result.callerPackage, PackageManagerService.PLATFORM_PACKAGE_NAME) || ((float) count) < ((float) broadcasts.size()) * 0.20000005f) {
            Slog.d(TAG, "abnormal broadcast not found with first loop count:" + count + " with caller:" + result);
            return null;
        }
        int count2 = 0;
        for (BroadcastRecord r : broadcasts) {
            if (TextUtils.equals(result.intent.getAction(), r.intent.getAction()) && TextUtils.equals(result.callerPackage, r.callerPackage)) {
                count2++;
            }
        }
        if (((float) count2) < ((float) broadcasts.size()) * ABNORMAL_BROADCAST_RATE) {
            Slog.d(TAG, "abnormal broadcast not found with count:" + count2);
            return null;
        }
        Slog.d(TAG, "found abnormal broadcast in list by rate:" + result + " cost:" + (SystemClock.uptimeMillis() - startTime) + " ms");
        return new AbnormalBroadcastRecord(result);
    }

    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r7v10, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r3v4, resolved type: com.android.server.am.BroadcastQueueInjector$AbnormalBroadcastRecord} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static com.android.server.am.BroadcastQueueInjector.AbnormalBroadcastRecord getAbnormalBroadcastByCountIfExisted(java.util.List<com.android.server.am.BroadcastRecord> r9) {
        /*
            long r0 = android.os.SystemClock.uptimeMillis()
            java.util.HashMap r2 = new java.util.HashMap
            r2.<init>()
            java.util.Iterator r3 = r9.iterator()
        L_0x000d:
            boolean r4 = r3.hasNext()
            if (r4 == 0) goto L_0x0047
            java.lang.Object r4 = r3.next()
            com.android.server.am.BroadcastRecord r4 = (com.android.server.am.BroadcastRecord) r4
            java.lang.String r5 = r4.callerPackage
            java.lang.String r6 = "android"
            boolean r5 = android.text.TextUtils.equals(r6, r5)
            if (r5 == 0) goto L_0x0024
            goto L_0x000d
        L_0x0024:
            com.android.server.am.BroadcastQueueInjector$AbnormalBroadcastRecord r5 = new com.android.server.am.BroadcastQueueInjector$AbnormalBroadcastRecord
            r5.<init>(r4)
            java.lang.Object r6 = r2.get(r5)
            java.lang.Integer r6 = (java.lang.Integer) r6
            r7 = 1
            if (r6 != 0) goto L_0x003a
            java.lang.Integer r7 = java.lang.Integer.valueOf(r7)
            r2.put(r5, r7)
            goto L_0x0046
        L_0x003a:
            int r8 = r6.intValue()
            int r8 = r8 + r7
            java.lang.Integer r7 = java.lang.Integer.valueOf(r8)
            r2.put(r5, r7)
        L_0x0046:
            goto L_0x000d
        L_0x0047:
            r3 = 0
            r4 = 0
            java.util.Set r5 = r2.entrySet()
            java.util.Iterator r5 = r5.iterator()
        L_0x0051:
            boolean r6 = r5.hasNext()
            if (r6 == 0) goto L_0x007b
            java.lang.Object r6 = r5.next()
            java.util.Map$Entry r6 = (java.util.Map.Entry) r6
            java.lang.Object r7 = r6.getValue()
            java.lang.Integer r7 = (java.lang.Integer) r7
            int r7 = r7.intValue()
            if (r7 <= r4) goto L_0x007a
            java.lang.Object r7 = r6.getKey()
            r3 = r7
            com.android.server.am.BroadcastQueueInjector$AbnormalBroadcastRecord r3 = (com.android.server.am.BroadcastQueueInjector.AbnormalBroadcastRecord) r3
            java.lang.Object r7 = r6.getValue()
            java.lang.Integer r7 = (java.lang.Integer) r7
            int r4 = r7.intValue()
        L_0x007a:
            goto L_0x0051
        L_0x007b:
            java.lang.String r5 = "BroadcastQueueInjector"
            if (r3 == 0) goto L_0x00b3
            java.lang.String r6 = r3.callerPackage
            boolean r6 = android.text.TextUtils.isEmpty(r6)
            if (r6 != 0) goto L_0x00b3
            int r6 = ACTIVE_ORDERED_BROADCAST_LIMIT
            if (r4 >= r6) goto L_0x008c
            goto L_0x00b3
        L_0x008c:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "found abnormal broadcast in list by max count:"
            r6.append(r7)
            r6.append(r3)
            java.lang.String r7 = " cost:"
            r6.append(r7)
            long r7 = android.os.SystemClock.uptimeMillis()
            long r7 = r7 - r0
            r6.append(r7)
            java.lang.String r7 = " ms"
            r6.append(r7)
            java.lang.String r6 = r6.toString()
            android.util.Slog.d(r5, r6)
            return r3
        L_0x00b3:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            java.lang.String r7 = "the max number of same broadcasts in queue is not large enough:"
            r6.append(r7)
            r6.append(r3)
            java.lang.String r7 = " with count:"
            r6.append(r7)
            r6.append(r4)
            java.lang.String r6 = r6.toString()
            android.util.Slog.d(r5, r6)
            r5 = 0
            return r5
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.BroadcastQueueInjector.getAbnormalBroadcastByCountIfExisted(java.util.List):com.android.server.am.BroadcastQueueInjector$AbnormalBroadcastRecord");
    }

    static class AbnormalBroadcastRecord {
        String action;
        String callerPackage;
        int userId;

        AbnormalBroadcastRecord(BroadcastRecord r) {
            this.action = r.intent.getAction();
            this.callerPackage = r.callerPackage;
            this.userId = r.userId;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof AbnormalBroadcastRecord)) {
                return super.equals(obj);
            }
            AbnormalBroadcastRecord r = (AbnormalBroadcastRecord) obj;
            return TextUtils.equals(this.action, r.action) && TextUtils.equals(this.callerPackage, r.callerPackage) && this.userId == r.userId;
        }

        public int hashCode() {
            int i = 1 * 31;
            String str = this.action;
            int i2 = 0;
            int hashCode = (i + (str == null ? 0 : str.hashCode())) * 31;
            String str2 = this.callerPackage;
            if (str2 != null) {
                i2 = str2.hashCode();
            }
            return ((hashCode + i2) * 31) + Integer.valueOf(this.userId).hashCode();
        }

        public String toString() {
            return "AbnormalBroadcastRecord{action='" + this.action + '\'' + ", callerPackage='" + this.callerPackage + '\'' + ", userId=" + this.userId + '}';
        }
    }

    public static void checkTime(long startTime, String where) {
        long now = SystemClock.uptimeMillis();
        if (now - startTime > 3000) {
            Slog.w(TAG, "Slow operation: processNextBroadcast " + (now - startTime) + "ms so far, now at " + where);
        }
    }
}
