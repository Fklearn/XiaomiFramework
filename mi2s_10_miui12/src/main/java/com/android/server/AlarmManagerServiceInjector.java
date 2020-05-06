package com.android.server;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Binder;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.util.Log;
import android.util.Slog;
import android.util.SparseBooleanArray;
import com.android.server.AlarmManagerService;
import com.android.server.am.ExtraActivityManagerService;
import com.android.server.job.controllers.JobStatus;
import com.miui.whetstone.PowerKeeperPolicy;
import com.miui.whetstone.WhetstoneManager;
import com.miui.whetstone.client.WhetstoneClientManager;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

class AlarmManagerServiceInjector {
    private static final List<String> ADJUST_WHITE_LIST = new ArrayList<String>() {
        {
            add("com.android.mms");
            add("android.app.cts");
            add("android.app.stubs");
            add("com.google.android.gms");
        }
    };
    private static long APP_PUSH_PERIOD_TIME = 0;
    private static final int APP_PUSH_PERIOD_TIME_DELTA = 1000;
    private static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private static final int FOREGROUND_APP_ADJ = 0;
    private static final int MAX_APP_PUSH_PERIOD_TIME = 540000;
    private static final int MIN_APP_PUSH_PERIOD_TIME = 120000;
    public static String MM_BOOTER_TAG = "*walarm*:com.tencent.mm/.booter.MMReceivers$AlarmReceiver";
    public static String MM_HEART_BEAT_TAG = "*walarm*:ALARM_ACTION";
    public static String MM_PACKAGE = "com.tencent.mm";
    private static final long PENDING_DELAY_TIME = 259200000;
    private static final int PERCEPTIBLE_APP_ADJ = 200;
    static final List<String> PERSIST_PACKAGES = new ArrayList<String>() {
        {
            add("com.android.deskclock");
        }
    };
    public static String QQ_HEART_BEAT_TAG = "*walarm*:com.tencent.mobileqq:MSF_";
    public static String QQ_PACKAGE = "com.tencent.mobileqq";
    private static final String TAG = "AlarmManagerServiceInjector";
    private static final String THIRD_PARTY_WAKEUP_RESTRICT_PROP = "persist.sys.wakeup_restrict";
    private static final List<String> WAKEUP_WHITE_LIST = new ArrayList<String>() {
        {
            add("com.android.deskclock");
            add("com.android.providers.calendar");
            add("com.xiaomi.xmsf");
            add("com.mobiletools.systemhelper");
            add("com.chinaunicom.registerhelper");
        }
    };
    public static String XMSF_HEART_BEAT_TAG = "*walarm*:com.xiaomi.push.PING_TIMER";
    public static String XMSF_PACKAGE = "com.xiaomi.xmsf";
    private static long appPushLeaderLastTriggerElapsed;
    private static boolean isPushLeaderLive = false;
    private static final SparseBooleanArray mAlignedAlarmArray = new SparseBooleanArray();
    private static final ArrayList<AlarmManagerService.Alarm> mPushAlarmPendingList = new ArrayList<>();
    private static final SparseBooleanArray mUidPushHBAlignHistory = new SparseBooleanArray();
    private static final LinkedBlockingQueue<AlarmManagerService.Alarm> mdelAlarmHistory = new LinkedBlockingQueue<>(40);
    /* access modifiers changed from: private */
    public static final Object sLock = new Object();
    /* access modifiers changed from: private */
    public static boolean sNetAvailable = true;
    /* access modifiers changed from: private */
    public static boolean sScreenOn = true;

    AlarmManagerServiceInjector() {
    }

    static String[] filterPersistPackages(String[] pkgList) {
        List<String> filteredPkgList = new ArrayList<>();
        if (pkgList != null && pkgList.length > 0) {
            for (String pkg : pkgList) {
                if (!PERSIST_PACKAGES.contains(pkg)) {
                    filteredPkgList.add(pkg);
                }
            }
        }
        return (String[]) filteredPkgList.toArray(new String[0]);
    }

    public static int adjustWakeUpAlarmType(Context context, int originalType) {
        int uid;
        if ((originalType != 0 && originalType != 2) || (uid = Binder.getCallingUid()) < 10000) {
            return originalType;
        }
        String[] packages = context.getPackageManager().getPackagesForUid(uid);
        if ((packages != null && packages.length > 0 && inWakeUpAlarmWhiteList(packages[0])) || !SystemProperties.getBoolean(THIRD_PARTY_WAKEUP_RESTRICT_PROP, false)) {
            return originalType;
        }
        if (DEBUG) {
            Slog.d(TAG, "restrict all third party wakeup is set");
        }
        return nonWakeUpType(originalType);
    }

    public static void recordRTCWakeupInfo(Context context, int originalType, PendingIntent operation, boolean status) {
        int uid;
        if ((originalType == 0 || originalType == 2) && (uid = Binder.getCallingUid()) != 1000) {
            WhetstoneManager.recordRTCWakeupInfo(uid, operation, status);
        }
    }

    private static boolean inWakeUpAlarmWhiteList(String packageName) {
        for (String pkg : WAKEUP_WHITE_LIST) {
            if (packageName.equals(pkg)) {
                return true;
            }
        }
        return false;
    }

    private static int nonWakeUpType(int type) {
        if (type == 0) {
            return 1;
        }
        if (type != 2) {
            return type;
        }
        return 3;
    }

    public static boolean CheckIfAlarmGenralRistrictApply(int uid, int pid) {
        if (uid <= 10000) {
            return false;
        }
        int curAdj = ExtraActivityManagerService.getCurAdjByPid(pid);
        int procState = ExtraActivityManagerService.getProcStateByPid(pid);
        if (ExtraActivityManagerService.hasForegroundActivities(pid) || (curAdj >= 0 && curAdj <= 200 && procState != 12)) {
            return false;
        }
        return true;
    }

    public static boolean checkAlarmIsAllowedSend(Context context, AlarmManagerService.Alarm alarm) {
        if (alarm == null || alarm.operation == null) {
            return true;
        }
        return WhetstoneClientManager.isAlarmAllowedLocked(Binder.getCallingPid(), alarm.operation.getCreatorUid(), alarm.statsTag, CheckIfAlarmGenralRistrictApply(alarm.operation.getCreatorUid(), Binder.getCallingPid()));
    }

    private static boolean cmpCurPushAlarmPropertyWithHeartBeat(Intent cur, Intent heart) {
        String curAction = cur.getAction();
        String heartAction = heart.getAction();
        if (curAction == null || heartAction == null) {
            return false;
        }
        if (curAction.equals(heartAction) || curAction.contains(heartAction)) {
            return true;
        }
        return false;
    }

    public static long checkIsNeedAjustTriggerElapsed(PendingIntent operation, long triggerElapsed, long nowElapsed) {
        long j = nowElapsed;
        PowerKeeperPolicy powerKeeperPolicy = PowerKeeperPolicy.getInstance();
        int uid = operation.getCreatorUid();
        if (!powerKeeperPolicy.getAppPushAlarmFunc(uid) || !isPushLeaderLive) {
            return triggerElapsed;
        }
        if (uid == powerKeeperPolicy.getAppPushAlarmLeaderUid()) {
            long tmpDelta = triggerElapsed - j;
            if (tmpDelta > 540000 || tmpDelta < JobStatus.DEFAULT_TRIGGER_MAX_DELAY) {
                Slog.d(TAG, operation.getCreatorPackage() + " tmpDelta = " + tmpDelta + " is abnormal, ignore");
            } else {
                APP_PUSH_PERIOD_TIME = tmpDelta;
                appPushLeaderLastTriggerElapsed = triggerElapsed;
            }
            return triggerElapsed;
        }
        if (!cmpCurPushAlarmPropertyWithHeartBeat(operation.getIntent(), powerKeeperPolicy.getAppPushAlarmProperty(uid))) {
            return triggerElapsed;
        }
        mUidPushHBAlignHistory.put(uid, true);
        if (j > appPushLeaderLastTriggerElapsed) {
            Slog.d(TAG, operation.getCreatorPackage() + " nowElapsed = " + j + "　> LastTriggerElapsed " + appPushLeaderLastTriggerElapsed);
            return (appPushLeaderLastTriggerElapsed + APP_PUSH_PERIOD_TIME) - 1000;
        }
        Slog.d(TAG, operation.getCreatorPackage() + " nowElapsed = " + j + " < appPushLeaderLastTriggerElapsed " + appPushLeaderLastTriggerElapsed);
        return appPushLeaderLastTriggerElapsed - 1000;
    }

    private static boolean checkAlarmOperationIsHeartBeat(AlarmManagerService.Alarm alarm) {
        PowerKeeperPolicy powerKeeperPolicy = PowerKeeperPolicy.getInstance();
        int uid = alarm.operation.getCreatorUid();
        if (!powerKeeperPolicy.getAppPushAlarmFunc(uid)) {
            return false;
        }
        Intent heartIntent = powerKeeperPolicy.getAppPushAlarmProperty(uid);
        Intent curIntent = alarm.operation.getIntent();
        if (curIntent == null || heartIntent == null) {
            if (DEBUG) {
                Slog.d(TAG, "curIntent = " + curIntent + "heartIntent = " + heartIntent);
            }
            return false;
        } else if (!cmpCurPushAlarmPropertyWithHeartBeat(curIntent, heartIntent)) {
            return false;
        } else {
            return true;
        }
    }

    private static boolean checkAlarmOperationIsLeaderHeartBeat(AlarmManagerService.Alarm alarm) {
        if (alarm.operation.getCreatorUid() != PowerKeeperPolicy.getInstance().getAppPushAlarmLeaderUid()) {
            return false;
        }
        Slog.d(TAG, " alarm " + alarm + "is aligned leader, now sending all pending alarm");
        isPushLeaderLive = true;
        return true;
    }

    private static boolean checkAlarmIsAllowedAddToPushAlarmPendingList(AlarmManagerService.Alarm alarm) {
        boolean isFinded = false;
        for (int i = 0; i < mPushAlarmPendingList.size(); i++) {
            if (alarm.operation.getTargetPackage().equals(mPushAlarmPendingList.get(i).operation.getTargetPackage())) {
                isFinded = true;
            }
        }
        return !isFinded;
    }

    public static void doAppPushHeartBeatAlignment(Context context, ArrayList<AlarmManagerService.Alarm> triggerlist) {
        Iterator<AlarmManagerService.Alarm> sTriggerListIterator = triggerlist.iterator();
        boolean isContainLeader = false;
        while (sTriggerListIterator.hasNext()) {
            AlarmManagerService.Alarm currentAlarm = sTriggerListIterator.next();
            if (checkAlarmOperationIsHeartBeat(currentAlarm)) {
                if (true == checkAlarmOperationIsLeaderHeartBeat(currentAlarm)) {
                    isContainLeader = true;
                }
                if (!isPushLeaderLive) {
                    Slog.d(TAG, "isPushLeaderLive is false, continue");
                } else {
                    if (true == checkAlarmIsAllowedAddToPushAlarmPendingList(currentAlarm)) {
                        mPushAlarmPendingList.add(currentAlarm);
                    }
                    Slog.d(TAG, "alarm remove: " + currentAlarm);
                    if (mdelAlarmHistory.remainingCapacity() < 3) {
                        mdelAlarmHistory.poll();
                    }
                    mdelAlarmHistory.add(currentAlarm);
                    sTriggerListIterator.remove();
                }
            }
        }
        if (true == isContainLeader) {
            Iterator<AlarmManagerService.Alarm> sPendingListIterator = mPushAlarmPendingList.iterator();
            while (sPendingListIterator.hasNext()) {
                AlarmManagerService.Alarm tmpAlarm = sPendingListIterator.next();
                Slog.d(TAG, "alarm " + tmpAlarm + "is add to triggerlist ");
                triggerlist.add(tmpAlarm);
            }
            mPushAlarmPendingList.clear();
        }
    }

    public static void dumpAlarmAligStat(PrintWriter pw) {
        PowerKeeperPolicy powerKeeperPolicy = PowerKeeperPolicy.getInstance();
        int LeaderUid = powerKeeperPolicy.getAppPushAlarmLeaderUid();
        pw.println("");
        pw.println("Current Alarm Push HeartBeat Align state: ");
        pw.print(" leader uid = ");
        pw.println(LeaderUid);
        pw.print(" leader intent = ");
        pw.println(powerKeeperPolicy.getAppPushAlarmProperty(LeaderUid));
        pw.print(" leader live is = ");
        pw.println(isPushLeaderLive);
        pw.println("apps has ever aligned: ");
        for (int i = 0; i < mUidPushHBAlignHistory.size(); i++) {
            if (true == mUidPushHBAlignHistory.valueAt(i)) {
                pw.print(" uid = ");
                pw.println(mUidPushHBAlignHistory.keyAt(i));
            }
        }
        pw.println("recent align alarms stat: ");
        Iterator<AlarmManagerService.Alarm> alarmIterator = mdelAlarmHistory.iterator();
        while (alarmIterator.hasNext()) {
            pw.print(" alarm = ");
            pw.println(alarmIterator.next());
        }
        pw.print("pending list = ");
        pw.println(mPushAlarmPendingList);
        pw.print("APP_PUSH_PERIOD_TIME = ");
        pw.println(APP_PUSH_PERIOD_TIME);
        pw.print("APP_PUSH_PERIOD_TIME_DELTA = ");
        pw.println(1000);
    }

    public static boolean isAlarmAligned(AlarmManagerService.Alarm a) {
        return mAlignedAlarmArray.indexOfKey(a.hashCode()) >= 0;
    }

    public static void removeAlarm(AlarmManagerService.Alarm a) {
        mAlignedAlarmArray.delete(a.hashCode());
    }

    private static AlarmManagerService.Alarm findXmsfHeartBeatAlarm(AlarmManagerService.Alarm a, ArrayList<AlarmManagerService.Batch> batches, int distance) {
        AlarmManagerService.Alarm ret = null;
        if (batches != null) {
            int N = batches.size();
            for (int i = 0; i < N; i++) {
                ArrayList<AlarmManagerService.Alarm> alarms = batches.get(i).alarms;
                if (alarms != null) {
                    int M = alarms.size();
                    int j = 0;
                    while (true) {
                        if (j < M) {
                            AlarmManagerService.Alarm al = alarms.get(j);
                            if (al != null && al.statsTag.startsWith(XMSF_HEART_BEAT_TAG) && al.sourcePackage.equals(XMSF_PACKAGE)) {
                                ret = al;
                                break;
                            }
                            j++;
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        if (ret == null || Math.abs(a.whenElapsed - ret.whenElapsed) >= ((long) distance)) {
            return null;
        }
        return ret;
    }

    private static void adjustAlarm(AlarmManagerService.Alarm a, ArrayList<AlarmManagerService.Batch> batches, long min, int distance, boolean repeat) {
        AlarmManagerService.Alarm al;
        if (a.whenElapsed - SystemClock.elapsedRealtime() >= min && (al = findXmsfHeartBeatAlarm(a, batches, distance)) != null) {
            a.when = al.when;
            a.whenElapsed = al.whenElapsed;
            a.expectedWhenElapsed = al.expectedWhenElapsed;
            if (repeat) {
                long delta = al.whenElapsed - a.whenElapsed;
                a.maxWhenElapsed += delta;
                a.expectedMaxWhenElapsed += delta;
            } else {
                a.windowLength = al.windowLength;
                a.maxWhenElapsed = al.maxWhenElapsed;
                a.expectedMaxWhenElapsed = al.expectedMaxWhenElapsed;
            }
            mAlignedAlarmArray.put(a.hashCode(), true);
        }
    }

    public static void keepAlignedWithXMSF(AlarmManagerService.Alarm a, ArrayList<AlarmManagerService.Batch> batches) {
        if (!isAlarmAligned(a)) {
            if (a.sourcePackage.equals(MM_PACKAGE) && a.statsTag.startsWith(MM_HEART_BEAT_TAG)) {
                adjustAlarm(a, batches, 60000, 60000, false);
            } else if (a.sourcePackage.equals(MM_PACKAGE) && a.statsTag.startsWith(MM_BOOTER_TAG)) {
                adjustAlarm(a, batches, 60000, MIN_APP_PUSH_PERIOD_TIME, true);
            } else if (a.sourcePackage.equals(QQ_PACKAGE) && a.statsTag.startsWith(QQ_HEART_BEAT_TAG)) {
                adjustAlarm(a, batches, 30000, 60000, false);
            }
        }
    }

    public static void adjustAlarmLocked(AlarmManagerService.Alarm alarm) {
        long delay;
        if (alarm != null) {
            if ((alarm.flags & 15) == 0 && !ADJUST_WHITE_LIST.contains(alarm.sourcePackage)) {
                long now = SystemClock.elapsedRealtime();
                if (now >= alarm.whenElapsed || alarm.whenElapsed - now <= PENDING_DELAY_TIME) {
                    synchronized (sLock) {
                        if (!sScreenOn || !sNetAvailable) {
                            if (now < alarm.whenElapsed) {
                                delay = PENDING_DELAY_TIME;
                            } else {
                                delay = PENDING_DELAY_TIME + (now - alarm.whenElapsed);
                            }
                            alarm.when += delay;
                            alarm.whenElapsed += delay;
                            alarm.maxWhenElapsed += delay;
                            alarm.expectedWhenElapsed += delay;
                            alarm.expectedMaxWhenElapsed += delay;
                            if (DEBUG) {
                                Slog.d(TAG, "adjustAlarmLocked, is pending alarm: " + alarm.packageName + ", tag: " + alarm.statsTag + ", whenElapsed: " + alarm.whenElapsed + ", now: " + now);
                            }
                        }
                    }
                }
            } else if (DEBUG) {
                Slog.d(TAG, "alarm is not restricted!");
            }
        }
    }

    public static void createNetAndScreenReceiver(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.intent.action.SCREEN_ON");
        filter.addAction("android.intent.action.SCREEN_OFF");
        context.registerReceiver(new ScreenReceiver(), filter);
        NetworkCallbackImpl networkCallback = new NetworkCallbackImpl();
        ((ConnectivityManager) context.getSystemService("connectivity")).registerNetworkCallback(new NetworkRequest.Builder().build(), networkCallback);
    }

    private static class ScreenReceiver extends BroadcastReceiver {
        private ScreenReceiver() {
        }

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.intent.action.SCREEN_ON".equals(action)) {
                synchronized (AlarmManagerServiceInjector.sLock) {
                    boolean unused = AlarmManagerServiceInjector.sScreenOn = true;
                }
            } else if ("android.intent.action.SCREEN_OFF".equals(action)) {
                synchronized (AlarmManagerServiceInjector.sLock) {
                    boolean unused2 = AlarmManagerServiceInjector.sScreenOn = false;
                }
            }
        }
    }

    private static class NetworkCallbackImpl extends ConnectivityManager.NetworkCallback {
        private NetworkCallbackImpl() {
        }

        public void onAvailable(Network network) {
            synchronized (AlarmManagerServiceInjector.sLock) {
                boolean unused = AlarmManagerServiceInjector.sNetAvailable = true;
            }
        }

        public void onLost(Network network) {
            synchronized (AlarmManagerServiceInjector.sLock) {
                boolean unused = AlarmManagerServiceInjector.sNetAvailable = false;
            }
        }
    }
}
