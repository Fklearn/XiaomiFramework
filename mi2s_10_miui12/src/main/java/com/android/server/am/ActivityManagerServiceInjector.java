package com.android.server.am;

import android.app.AppGlobals;
import android.app.ApplicationErrorReport;
import android.app.Notification;
import android.app.UserSwitchObserver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.UserInfo;
import android.os.AnrMonitor;
import android.os.Binder;
import android.os.Debug;
import android.os.Handler;
import android.os.IUserManager;
import android.os.Parcel;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.statistics.BinderServerMonitor;
import android.provider.MiuiSettings;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.os.UidAppBatteryStatsImpl;
import com.android.internal.util.ArrayUtils;
import com.android.server.LocalServices;
import com.android.server.am.AppErrorDialog;
import com.android.server.content.SyncStorageEngine;
import com.android.server.job.controllers.JobStatus;
import com.android.server.pm.PackageManagerService;
import com.android.server.pm.PackageManagerServiceInjector;
import com.android.server.pm.UserManagerService;
import com.android.server.wm.ActivityRecord;
import com.android.server.wm.ActivityTaskManagerInternal;
import com.android.server.wm.ActivityTaskManagerServiceInjector;
import com.android.server.wm.WindowManagerService;
import com.android.server.wm.WindowProcessController;
import com.miui.server.AccessController;
import com.miui.server.GreenGuardManagerService;
import com.miui.server.SecSpaceManagerService;
import com.miui.server.XSpaceManagerService;
import com.miui.server.greeze.GreezeManagerService;
import com.miui.whetstone.process.WtServiceControlEntry;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import miui.R;
import miui.app.backup.BackupManager;
import miui.mqsas.sdk.BootEventManager;
import miui.mqsas.sdk.MQSEventManagerDelegate;
import miui.mqsas.sdk.event.AnrEvent;
import miui.os.Build;
import miui.securityspace.XSpaceUserHandle;
import miui.util.ErrorReport;
import miui.util.ReflectionUtils;

public class ActivityManagerServiceInjector {
    public static final long BOOST_DURATION = 3000;
    private static final String BOOST_TAG = "Boost";
    private static final boolean DEBUG = true;
    private static final int FLAG_GRANT_SYSTEM_APP_URI_PERMISSION = Integer.MIN_VALUE;
    public static final long KEEP_FOREGROUND_DURATION = 20000;
    static final int PUSH_SERVICE_WHITELIST_TIMEOUT = 60000;
    public static final String SET_TOP_APP = "set_top_app";
    private static final String TAG = "ActivityManagerServiceInjector";
    private static boolean enableAdjDowngrade = SystemProperties.getBoolean("persist.sys.adj_downgrade", false);
    protected static boolean enableTaskIsolation = SystemProperties.getBoolean("persist.sys.task_isolation", false);
    private static Context mContext = null;
    private static long mLastCheckProcessCpuUsageUptime = 0;
    static ArrayList<String> mProtectedProcessList = new ArrayList<>();
    private static HashSet<String> mResizeBlackList = new HashSet<>();
    private static HashSet<String> mResizeWhiteList = new HashSet<>();
    private static UserManagerService mUserManager = null;
    private static UserSwitchObserver mUserSwitchObserver = new UserSwitchObserver() {
        public void onUserSwitchComplete(int newUserId) throws RemoteException {
            if (ActivityManagerServiceInjector.sDialog != null) {
                ActivityManagerServiceInjector.sDialog.dismiss();
            }
        }
    };
    private static final String mipushCaller = "com.xiaomi.xmsf";
    private static final String mipushServiceName = "com.xiaomi.mipush.sdk.PushMessageHandler";
    private static final String notificationCaller = "com.miui.notification";
    /* access modifiers changed from: private */
    public static BaseUserSwitchingDialog sDialog = null;
    private static Field sFieldBinderCpuTime = ReflectionUtils.tryFindField(ProcessRecord.class, "binderCpuTimeLastCheck");
    private static Field sFieldLastCheckedCpuTime = ReflectionUtils.tryFindField(ProcessRecord.class, "lastCheckedCpuTime");
    private static Field sFieldProcCpuTime = ReflectionUtils.tryFindField(ProcessRecord.class, "procCpuTimeLastCheck");
    private static int sSwitchUserCallingUid = 0;
    private static boolean sSystemBootCompleted = false;
    private static String[] skipVerifyList = {weixinProcessName, "com.tencent.mobileqq"};
    private static final String weixinProcessName = "com.tencent.mm";
    private static final String xiaomiVoiceServiceName = "com.miui.voiceassist/com.xiaomi.voiceassistant.VoiceService";

    static {
        mProtectedProcessList.add("com.android.phone");
    }

    static void addCalledBinderPids(int callerPid, List<Integer> firstPids) {
        if (!firstPids.contains(Integer.valueOf(callerPid))) {
            firstPids.add(Integer.valueOf(callerPid));
        }
        for (Integer intValue : collectCalledBinderPidsInChain(callerPid)) {
            int calledPid = intValue.intValue();
            if (!firstPids.contains(Integer.valueOf(calledPid))) {
                firstPids.add(Integer.valueOf(calledPid));
            }
        }
    }

    static List<Integer> collectCalledBinderPidsInChain(int callerPid) {
        return collectCalledBinderPidsInChain(callerPid, callerPid);
    }

    static List<Integer> collectCalledBinderPidsInChain(int callerPid, int callerTid) {
        List<Integer> pidsResult = new ArrayList<>();
        while (true) {
            int[] pidAndTid = getCalledBinderPidAndTid(callerPid, callerTid);
            if (pidAndTid == null || pidAndTid.length != 2 || pidAndTid[0] == 0 || pidAndTid[1] == 0) {
                return pidsResult;
            }
            pidsResult.add(Integer.valueOf(pidAndTid[0]));
            callerPid = pidAndTid[0];
            callerTid = pidAndTid[1];
        }
        return pidsResult;
    }

    static int[] getCalledBinderPidAndTid(int callerPid) {
        return getCalledBinderPidAndTid(callerPid, callerPid);
    }

    static int[] getCalledBinderPidAndTid(int callerPid, int callerTid) {
        int[] result = new int[2];
        File file = new File("/d/binder/proc/" + callerPid);
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            FileReader fileReader2 = new FileReader(file);
            BufferedReader bufferedReader2 = new BufferedReader(fileReader2);
            Pattern pattern = Pattern.compile(callerPid + ":" + callerTid + "\\sto\\s(\\d+):(\\d+)");
            while (true) {
                String readLine = bufferedReader2.readLine();
                String line = readLine;
                if (readLine != null) {
                    String line2 = line.trim();
                    if (line2.startsWith("outgoing") || line2.startsWith("pending") || line2.contains("to")) {
                        Matcher matcher = pattern.matcher(line2.trim());
                        if (matcher.find()) {
                            result[0] = Integer.valueOf(matcher.group(1)).intValue();
                            result[1] = Integer.valueOf(matcher.group(2)).intValue();
                            break;
                        }
                    }
                }
            }
            try {
                bufferedReader2.close();
                fileReader2.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (IOException e2) {
            e2.printStackTrace();
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (fileReader != null) {
                fileReader.close();
            }
        } catch (Throwable th) {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception e3) {
                    e3.printStackTrace();
                    throw th;
                }
            }
            if (fileReader != null) {
                fileReader.close();
            }
            throw th;
        }
        return result;
    }

    static final void init(Context context) {
        context.setTheme(R.style.Theme_DayNight);
        ExtraActivityManagerService.init();
        MiuiWarnings.getInstance().init(context);
        mContext = context;
    }

    static void finishBooting(ActivityManagerService ams) {
        ExtraActivityManagerService.finishBooting(ams);
        sendFinishBootingBroadcast(ams.mContext);
    }

    public static void sendFinishBootingBroadcast(Context context) {
        Intent intent = new Intent("miui.intent.action.FINISH_BOOTING");
        intent.setFlags(268435456);
        context.sendBroadcastAsUser(intent, UserHandle.SYSTEM);
    }

    static boolean isVRMode(Context context) {
        return Settings.System.getInt(context.getContentResolver(), "vr_mode", 0) == 1;
    }

    static void broadcastVRAppFC(Context context) {
        Intent intent = new Intent("miui.intent.vr.apperror");
        intent.setPackage("com.mi.dlabs.vr");
        context.sendBroadcast(intent);
    }

    static boolean isStartWithBackupRestriction(Context context, String backupPkgName, ProcessRecord app) {
        ApplicationInfo appInfo = app.getActiveInstrumentation() != null ? app.getActiveInstrumentation().mTargetInfo : app.info;
        return backupPkgName.equals(appInfo.packageName) && !BackupManager.isSysAppForBackup(context, appInfo.packageName);
    }

    public static boolean processInitBefore(String processName) {
        if (processName != null) {
            return !mProtectedProcessList.contains(processName);
        }
        return true;
    }

    public static boolean setProcessInitState(String processName) {
        if (processName == null || !mProtectedProcessList.contains(processName)) {
            return false;
        }
        mProtectedProcessList.remove(processName);
        Slog.d(TAG, "Remove:" + processName + " in mProtectedProcessList");
        return true;
    }

    public static int getAppStartMode(ActivityManagerService ams, int uid, String pkg, int defMode, String callingPackage) {
        if (!mipushCaller.equalsIgnoreCase(callingPackage) && !notificationCaller.equalsIgnoreCase(callingPackage)) {
            return defMode;
        }
        UidRecord record = ams.mProcessList.mActiveUids.get(uid);
        if (record == null || !record.idle || record.curWhitelist) {
            return 0;
        }
        ams.tempWhitelistUidLocked(record.uid, 60000, "push-service-launch");
        return 0;
    }

    public static void startProcessLocked(ActivityManagerService ams, ProcessRecord app, String hostingType, String hostingNameStr) {
        UidRecord record;
        if ((mipushCaller.equalsIgnoreCase(app.callerPackage) || notificationCaller.equalsIgnoreCase(app.callerPackage)) && (record = ams.mProcessList.mActiveUids.get(app.uid)) != null && record.idle && !record.curWhitelist) {
            ams.tempWhitelistUidLocked(record.uid, 60000, "push-service-launch");
        }
    }

    public static void startProcess(ActivityManagerService am, ApplicationInfo applicationInfo, String hostingType, ComponentName hostingName, String callerPackage) {
        if (applicationInfo != null) {
            synchronized (am) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    ProcessRecord app = am.startProcessLocked(applicationInfo.processName, applicationInfo, false, 0, new HostingRecord(hostingType, hostingName), false, false, false, callerPackage);
                    if (app == null) {
                        Slog.e(TAG, "startProcess " + applicationInfo.processName + " failed.");
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                    Slog.i(TAG, "startProcess " + applicationInfo.processName + " success.");
                    am.updateLruProcessLocked(app, false, (ProcessRecord) null);
                    ActivityManagerService.resetPriorityAfterLockedSection();
                } catch (Throwable th) {
                    while (true) {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
        }
    }

    public static void otaPreload() {
        new Thread("ota_preload_processes") {
            public void run() {
            }
        }.start();
    }

    private static void otaPreloadProcesses() {
        Process.setThreadGroupAndCpuset(Process.myTid(), 1);
        Process.setThreadPriority(Process.myTid(), -2);
        PackageManagerService pm = (PackageManagerService) ServiceManager.getService(com.android.server.pm.Settings.ATTR_PACKAGE);
        ActivityManagerService am = (ActivityManagerService) ServiceManager.getService("activity");
        for (String packageName : PackageManagerServiceInjector.getSecondaryDexOptPackages(pm)) {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 1024, UserHandle.myUserId());
            startProcess(am, applicationInfo, "ota-preload", new ComponentName(packageName, "ota-preload.stub"), PackageManagerService.PLATFORM_PACKAGE_NAME);
            Slog.i(TAG, "Process " + packageName + " is started by ota-preload, applicationInfo = " + applicationInfo);
        }
    }

    static void handleWindowManagerAndUserLru(Context context, int userId, int userIdOrig, int oldUserId, WindowManagerService mWindowManager, int[] mCurrentProfileIds) {
        ExtraActivityManagerService.handleWindowManagerAndUserLru(context, userId, userIdOrig, oldUserId, mWindowManager, mCurrentProfileIds, sSwitchUserCallingUid);
    }

    static int[] computeGids(int userId, int[] gids, ProcessRecord app) {
        int i = 0;
        if (userId != 0 && SecSpaceManagerService.isDataTransferProcess(userId, app.info.packageName)) {
            return ArrayUtils.appendInt(gids, UserHandle.getUserGid(0));
        }
        if (!XSpaceManagerService.sIsXSpaceCreated || gids == null) {
            return gids;
        }
        if (XSpaceUserHandle.isXSpaceUserId(userId)) {
            int length = gids.length;
            while (i < length) {
                if (gids[i] == XSpaceUserHandle.XSPACE_SHARED_USER_GID) {
                    return ArrayUtils.appendInt(gids, XSpaceUserHandle.OWNER_SHARED_USER_GID);
                }
                i++;
            }
            return gids;
        } else if (userId != 0) {
            return gids;
        } else {
            int length2 = gids.length;
            while (i < length2) {
                if (gids[i] == XSpaceUserHandle.OWNER_SHARED_USER_GID) {
                    return ArrayUtils.appendInt(gids, XSpaceUserHandle.XSPACE_SHARED_USER_GID);
                }
                i++;
            }
            return gids;
        }
    }

    static boolean showSwitchingDialog(final ActivityManagerService ams, final int mTargetUserId, final Handler handler) {
        setSwitchUserCallingUid(Binder.getCallingUid());
        String pkgName = ExtraActivityManagerService.getPackageNameByPid(Binder.getCallingPid());
        if (pkgName != null && (pkgName.equals(AccessController.PACKAGE_SYSTEMUI) || pkgName.equals("com.android.keyguard"))) {
            return false;
        }
        handler.post(new Runnable() {
            public void run() {
                int secondSpaceId = Settings.Secure.getIntForUser(ActivityManagerService.this.mContext.getContentResolver(), "second_user_id", ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION, 0);
                int mCurrentUserId = ActivityManagerService.this.mUserController.getCurrentUserId();
                if ((mTargetUserId == secondSpaceId && mCurrentUserId == 0) || (mCurrentUserId == secondSpaceId && mTargetUserId == 0)) {
                    ActivityManagerService activityManagerService = ActivityManagerService.this;
                    BaseUserSwitchingDialog unused = ActivityManagerServiceInjector.sDialog = new SecondSpaceSwitchingDialog(activityManagerService, activityManagerService.mContext, mTargetUserId);
                    ActivityManagerServiceInjector.sDialog.show();
                } else if (ActivityManagerServiceInjector.isKidUserSwitch(ActivityManagerService.this.mContext, mCurrentUserId, mTargetUserId)) {
                    ActivityManagerService activityManagerService2 = ActivityManagerService.this;
                    BaseUserSwitchingDialog unused2 = ActivityManagerServiceInjector.sDialog = new KidSpaceSwitchingDialog(activityManagerService2, activityManagerService2.mContext, mTargetUserId);
                    ActivityManagerServiceInjector.sDialog.show();
                } else {
                    Pair<UserInfo, UserInfo> userNames = new Pair<>(ActivityManagerServiceInjector.getUserManager().getUserInfo(mCurrentUserId), ActivityManagerServiceInjector.getUserManager().getUserInfo(mTargetUserId));
                    Handler handler = handler;
                    handler.sendMessage(handler.obtainMessage(1000, userNames));
                }
            }
        });
        ams.mUserController.registerUserSwitchObserver(mUserSwitchObserver, TAG);
        return true;
    }

    /* access modifiers changed from: private */
    public static boolean isKidUserSwitch(Context context, int currentUserId, int targetUserId) {
        int kidSpaceUserId = Settings.Secure.getIntForUser(context.getContentResolver(), "kid_user_id", ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION, 0);
        if ((targetUserId == kidSpaceUserId && currentUserId == 0) || (currentUserId == kidSpaceUserId && targetUserId == 0)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: private */
    public static UserManagerService getUserManager() {
        if (mUserManager == null) {
            mUserManager = IUserManager.Stub.asInterface(ServiceManager.getService("user"));
        }
        return mUserManager;
    }

    public static String getTopAppPackageName() {
        ActivityTaskManagerInternal aTm = (ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class);
        WindowProcessController wpc = aTm != null ? aTm.getTopApp() : null;
        ProcessRecord r = wpc != null ? (ProcessRecord) wpc.mOwner : null;
        ApplicationInfo info = r != null ? r.info : null;
        if (info != null) {
            return info.packageName;
        }
        return null;
    }

    public static boolean restartDiedAppOrNot(ProcessRecord app, boolean isHomeApp, boolean allowRestart, boolean fromBinderDied) {
        if (fromBinderDied) {
            return restartDiedAppOrNot(app, isHomeApp, allowRestart);
        }
        return true;
    }

    public static boolean restartDiedAppOrNot(ProcessRecord app, boolean isHomeApp, boolean allowRestart) {
        if (!WtServiceControlEntry.isServiceControlEnabled()) {
            return allowRestart;
        }
        long oldTime = SystemClock.uptimeMillis() - 1800000;
        if (isHomeApp || WtServiceControlEntry.isAppInServiceControlWhitelist(app.processName)) {
            return true;
        }
        if (!app.killedByAm && app.getCurProcState() > 11 && app.lastActivityTime < oldTime && app.curAdj > 500) {
            Slog.d("ActivityManagerInjector", "" + app.processName + " is a long inactive service(millionsecond) " + app.lastActivityTime + " with adj " + app.curAdj + ",do not allow restart");
            return false;
        } else if (!app.killedByAm || app.curAdj < 999 || app.getCurProcState() < 17) {
            return true;
        } else {
            Slog.d("ActivityManagerInjector", "" + app.processName + " is killed by AMS with adj " + app.curAdj + " ,do not allow restart");
            return false;
        }
    }

    static void setSwitchUserCallingUid(int uid) {
        sSwitchUserCallingUid = uid;
    }

    static boolean shouldAddPersistApp(ApplicationInfo info) {
        if (!TextUtils.equals(info.packageName, GreenGuardManagerService.GREEN_KID_AGENT_PKG_NAME) || info.enabled) {
            return true;
        }
        Slog.d(TAG, "persist app : " + info.packageName + "should not add to start");
        return false;
    }

    static boolean isSpecialBroadcast(int callingUid, String action) {
        if (UserHandle.getAppId(callingUid) != 2000) {
            return false;
        }
        if ("miui.intent.action.SWITCH_ON_MIUILOGS".equals(action) || "miui.intent.action.SWITCH_OFF_MIUILOGS".equals(action) || "miui.intent.action.REVERT_MIUILOG_SWITCHES".equals(action)) {
            return true;
        }
        return false;
    }

    public static void onANR(ActivityManagerService ams, ProcessRecord process, String activityShortComponentName, String parentShortCompontnName, String subject, String report, File logFile, ApplicationErrorReport.CrashInfo crashInfo, String headline) {
        ActivityManagerService activityManagerService = ams;
        reportANR(activityManagerService, ProcessPolicy.REASON_ANR, process, process.processName, activityShortComponentName, parentShortCompontnName, subject, report, logFile, crashInfo, headline);
        ANRManager.onANR(activityManagerService, process, (ActivityRecord) null, (ActivityRecord) null, subject, report, logFile, crashInfo, headline);
    }

    public static void reportANR(ActivityManagerService ams, String eventType, ProcessRecord process, String processName, String activityShortComponentName, String parentShortCompontnName, String subject, String report, File logFile, ApplicationErrorReport.CrashInfo crashInfo, String headline) {
        AnrEvent event = new AnrEvent();
        event.setPid(process.pid);
        event.setProcessName(process.pid == ActivityManagerService.MY_PID ? "system_server" : processName);
        event.setPackageName((!"system".equals(processName) || process.pid == ActivityManagerService.MY_PID) ? event.getProcessName() : process.info.packageName);
        event.setTimeStamp(System.currentTimeMillis());
        event.setReason(report);
        event.setCpuInfo(subject);
        event.setBgAnr(!process.isInterestingToUserLocked());
        if (logFile != null && logFile.exists()) {
            event.setLogName(logFile.getAbsolutePath());
        }
        if (activityShortComponentName != null) {
            event.setTargetActivity(activityShortComponentName);
        }
        if (parentShortCompontnName != null) {
            event.setParent(parentShortCompontnName);
        }
        MQSEventManagerDelegate.getInstance().reportAnrEvent(event);
    }

    public static String attachProcessStartReason(String processName, String hostingType, String hostingNameStr) {
        StringBuilder buf = new StringBuilder();
        buf.append(processName);
        buf.append(" #for# ");
        buf.append(hostingType);
        if (hostingNameStr != null) {
            buf.append(" ");
            buf.append(hostingNameStr);
        }
        return buf.toString();
    }

    public static void saveAnrInfoBeforeDumpTrace(ActivityManagerService ams, StringBuilder oriInfo, String extraInfo, String processName, int pid, boolean interestingToUser, ArrayList<Integer> firstPids, SparseArray<Boolean> lastPids, String[] nativeProcs) {
        boolean bgAnr = false;
        boolean showBackground = false;
        if (Settings.Secure.getInt(ams.mContext.getContentResolver(), "anr_show_background", 0) != 0) {
            showBackground = true;
        }
        if (showBackground || interestingToUser) {
            int i = pid;
        } else if (pid != ActivityManagerService.MY_PID) {
            bgAnr = true;
        }
        AnrMonitor.dumpAnrInfo(oriInfo, extraInfo, processName, pid, firstPids, lastPids, nativeProcs, ams.isUserAMonkey(), bgAnr);
    }

    static synchronized boolean parseDumpArgs(ActivityManagerService ams, String[] args, int startIndex) {
        String[] strArr = args;
        int i = startIndex;
        synchronized (ActivityManagerServiceInjector.class) {
            String cmd = strArr[i];
            if ("dump-app-trace".equals(cmd)) {
                try {
                    if (AnrMonitor.DBG) {
                        AnrMonitor.logDumpTrace("startIndex " + i + " cmd : " + cmd, (Throwable) null);
                        for (int i2 = 0; i2 < strArr.length; i2++) {
                            AnrMonitor.logDumpTrace("args[" + i2 + "] " + strArr[i2], (Throwable) null);
                        }
                    }
                    if (i < strArr.length) {
                        try {
                            String cmd2 = strArr[i + 1];
                            String[] arr = cmd2.split(",");
                            if (AnrMonitor.DBG) {
                                AnrMonitor.logDumpTrace("dump-app-trace cmd : " + cmd2, (Throwable) null);
                                for (int i3 = 0; i3 < arr.length; i3++) {
                                    AnrMonitor.logDumpTrace("arr[" + i3 + "] " + arr[i3], (Throwable) null);
                                }
                            }
                            if (arr == null || arr.length != 4) {
                                ActivityManagerService activityManagerService = ams;
                            } else {
                                int appPid = Integer.valueOf(arr[0]).intValue();
                                String processName = arr[1];
                                String appName = arr[2];
                                String msg = arr[3];
                                long start = SystemClock.uptimeMillis();
                                try {
                                    dumpAppTrace(ams, appPid, processName, appName, msg);
                                    AnrMonitor.logDumpTrace("dump " + processName + " trace took " + (SystemClock.uptimeMillis() - start) + "ms", (Throwable) null);
                                } catch (Exception e) {
                                    e = e;
                                }
                            }
                        } catch (Exception e2) {
                            e = e2;
                            ActivityManagerService activityManagerService2 = ams;
                            AnrMonitor.logDumpTrace("parseDumpArgs failed!", e);
                            return true;
                        }
                    } else {
                        ActivityManagerService activityManagerService3 = ams;
                    }
                } catch (Exception e3) {
                    e = e3;
                    ActivityManagerService activityManagerService4 = ams;
                    AnrMonitor.logDumpTrace("parseDumpArgs failed!", e);
                    return true;
                }
            } else {
                ActivityManagerService activityManagerService5 = ams;
                return false;
            }
        }
    }

    /* JADX WARNING: Unknown top exception splitter block from list: {B:45:0x0110=Splitter:B:45:0x0110, B:69:0x0169=Splitter:B:69:0x0169} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static void dumpAppTrace(com.android.server.am.ActivityManagerService r17, int r18, java.lang.String r19, java.lang.String r20, java.lang.String r21) {
        /*
            r1 = r18
            r2 = r20
            java.lang.String r0 = "dalvik.vm.stack-trace-file"
            r3 = 0
            java.lang.String r3 = android.os.SystemProperties.get(r0, r3)
            if (r3 == 0) goto L_0x018c
            int r0 = r3.length()
            if (r0 != 0) goto L_0x0019
            r10 = r19
            r8 = r21
            goto L_0x0190
        L_0x0019:
            android.os.StrictMode$ThreadPolicy r4 = android.os.StrictMode.allowThreadDiskReads()
            android.os.StrictMode.allowThreadDiskWrites()
            java.io.File r0 = new java.io.File     // Catch:{ all -> 0x0183 }
            r0.<init>(r3)     // Catch:{ all -> 0x0183 }
            r5 = r0
            java.io.File r0 = r5.getParentFile()     // Catch:{ all -> 0x0183 }
            r6 = r0
            java.io.File r0 = new java.io.File     // Catch:{ all -> 0x0183 }
            java.lang.String r7 = "_tmp_"
            r0.<init>(r6, r7)     // Catch:{ all -> 0x0183 }
            r7 = r0
            boolean r0 = r6.exists()     // Catch:{ IOException -> 0x0164 }
            if (r0 != 0) goto L_0x004a
            r6.mkdirs()     // Catch:{ IOException -> 0x0164 }
            java.lang.String r0 = r6.getPath()     // Catch:{ IOException -> 0x0164 }
            boolean r0 = android.os.SELinux.restorecon(r0)     // Catch:{ IOException -> 0x0164 }
            if (r0 != 0) goto L_0x004a
            android.os.StrictMode.setThreadPolicy(r4)
            return
        L_0x004a:
            java.lang.String r0 = r6.getPath()     // Catch:{ IOException -> 0x0164 }
            r8 = 509(0x1fd, float:7.13E-43)
            r9 = -1
            android.os.FileUtils.setPermissions(r0, r8, r9, r9)     // Catch:{ IOException -> 0x0164 }
            boolean r0 = r5.exists()     // Catch:{ IOException -> 0x0164 }
            if (r0 == 0) goto L_0x0060
            r7.delete()     // Catch:{ IOException -> 0x0164 }
            r5.renameTo(r7)     // Catch:{ IOException -> 0x0164 }
        L_0x0060:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0164 }
            r0.<init>()     // Catch:{ IOException -> 0x0164 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0164 }
            r8.<init>()     // Catch:{ IOException -> 0x0164 }
            long r10 = java.lang.System.currentTimeMillis()     // Catch:{ IOException -> 0x0164 }
            java.lang.String r10 = android.os.AnrMonitor.toCalendarTime(r10)     // Catch:{ IOException -> 0x0164 }
            r8.append(r10)     // Catch:{ IOException -> 0x0164 }
            java.lang.String r10 = "\n"
            r8.append(r10)     // Catch:{ IOException -> 0x0164 }
            java.lang.String r8 = r8.toString()     // Catch:{ IOException -> 0x0164 }
            r0.append(r8)     // Catch:{ IOException -> 0x0164 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0164 }
            r8.<init>()     // Catch:{ IOException -> 0x0164 }
            java.lang.String r10 = "pid : "
            r8.append(r10)     // Catch:{ IOException -> 0x0164 }
            r8.append(r1)     // Catch:{ IOException -> 0x0164 }
            java.lang.String r10 = "\n"
            r8.append(r10)     // Catch:{ IOException -> 0x0164 }
            java.lang.String r8 = r8.toString()     // Catch:{ IOException -> 0x0164 }
            r0.append(r8)     // Catch:{ IOException -> 0x0164 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0164 }
            r8.<init>()     // Catch:{ IOException -> 0x0164 }
            java.lang.String r10 = "pkgName : "
            r8.append(r10)     // Catch:{ IOException -> 0x0164 }
            r8.append(r2)     // Catch:{ IOException -> 0x0164 }
            java.lang.String r10 = "\n"
            r8.append(r10)     // Catch:{ IOException -> 0x0164 }
            java.lang.String r8 = r8.toString()     // Catch:{ IOException -> 0x0164 }
            r0.append(r8)     // Catch:{ IOException -> 0x0164 }
            java.lang.StringBuilder r8 = new java.lang.StringBuilder     // Catch:{ IOException -> 0x0164 }
            r8.<init>()     // Catch:{ IOException -> 0x0164 }
            java.lang.String r10 = "processName : "
            r8.append(r10)     // Catch:{ IOException -> 0x0164 }
            r10 = r19
            r8.append(r10)     // Catch:{ IOException -> 0x0162, all -> 0x0160 }
            java.lang.String r11 = "\n"
            r8.append(r11)     // Catch:{ IOException -> 0x0162, all -> 0x0160 }
            java.lang.String r8 = r8.toString()     // Catch:{ IOException -> 0x0162, all -> 0x0160 }
            r0.append(r8)     // Catch:{ IOException -> 0x0162, all -> 0x0160 }
            r8 = r21
            r0.append(r8)     // Catch:{ IOException -> 0x015e }
            java.io.FileOutputStream r11 = new java.io.FileOutputStream     // Catch:{ IOException -> 0x015e }
            r11.<init>(r5)     // Catch:{ IOException -> 0x015e }
            java.lang.String r12 = r0.toString()     // Catch:{ IOException -> 0x015e }
            byte[] r12 = r12.getBytes()     // Catch:{ IOException -> 0x015e }
            r11.write(r12)     // Catch:{ IOException -> 0x015e }
            r11.close()     // Catch:{ IOException -> 0x015e }
            java.lang.String r12 = r5.getPath()     // Catch:{ IOException -> 0x015e }
            r13 = 438(0x1b6, float:6.14E-43)
            android.os.FileUtils.setPermissions(r12, r13, r9, r9)     // Catch:{ IOException -> 0x015e }
            if (r1 == 0) goto L_0x0119
            com.android.server.am.ActivityManagerServiceInjector$4 r0 = new com.android.server.am.ActivityManagerServiceInjector$4     // Catch:{ all -> 0x0181 }
            r9 = 8
            r0.<init>(r3, r9)     // Catch:{ all -> 0x0181 }
            r9 = r0
            r9.startWatching()     // Catch:{ all -> 0x0114 }
            monitor-enter(r9)     // Catch:{ InterruptedException -> 0x010f }
            r0 = 3
            android.os.Process.sendSignal(r1, r0)     // Catch:{ all -> 0x010c }
            r11 = 500(0x1f4, double:2.47E-321)
            r9.wait(r11)     // Catch:{ all -> 0x010c }
            monitor-exit(r9)     // Catch:{ all -> 0x010c }
            goto L_0x0110
        L_0x010c:
            r0 = move-exception
            monitor-exit(r9)     // Catch:{ all -> 0x010c }
            throw r0     // Catch:{ InterruptedException -> 0x010f }
        L_0x010f:
            r0 = move-exception
        L_0x0110:
            r9.stopWatching()     // Catch:{ all -> 0x0181 }
            goto L_0x0119
        L_0x0114:
            r0 = move-exception
            r9.stopWatching()     // Catch:{ all -> 0x0181 }
            throw r0     // Catch:{ all -> 0x0181 }
        L_0x0119:
            r0 = 0
            r9 = 0
            r11 = 5
        L_0x011c:
            if (r11 < 0) goto L_0x014d
            java.util.Locale r12 = java.util.Locale.US     // Catch:{ all -> 0x0181 }
            java.lang.String r13 = "slow_app_%s_%02d.txt"
            r14 = 2
            java.lang.Object[] r14 = new java.lang.Object[r14]     // Catch:{ all -> 0x0181 }
            r15 = 0
            r14[r15] = r2     // Catch:{ all -> 0x0181 }
            r15 = 1
            java.lang.Integer r16 = java.lang.Integer.valueOf(r11)     // Catch:{ all -> 0x0181 }
            r14[r15] = r16     // Catch:{ all -> 0x0181 }
            java.lang.String r12 = java.lang.String.format(r12, r13, r14)     // Catch:{ all -> 0x0181 }
            java.io.File r13 = new java.io.File     // Catch:{ all -> 0x0181 }
            r13.<init>(r6, r12)     // Catch:{ all -> 0x0181 }
            r9 = r13
            boolean r13 = r9.exists()     // Catch:{ all -> 0x0181 }
            if (r13 == 0) goto L_0x0149
            if (r0 == 0) goto L_0x0146
            r9.renameTo(r0)     // Catch:{ all -> 0x0181 }
            goto L_0x0149
        L_0x0146:
            r9.delete()     // Catch:{ all -> 0x0181 }
        L_0x0149:
            r0 = r9
            int r11 = r11 + -1
            goto L_0x011c
        L_0x014d:
            r5.renameTo(r9)     // Catch:{ all -> 0x0181 }
            boolean r11 = r7.exists()     // Catch:{ all -> 0x0181 }
            if (r11 == 0) goto L_0x0159
            r7.renameTo(r5)     // Catch:{ all -> 0x0181 }
        L_0x0159:
            android.os.StrictMode.setThreadPolicy(r4)
            return
        L_0x015e:
            r0 = move-exception
            goto L_0x0169
        L_0x0160:
            r0 = move-exception
            goto L_0x0186
        L_0x0162:
            r0 = move-exception
            goto L_0x0167
        L_0x0164:
            r0 = move-exception
            r10 = r19
        L_0x0167:
            r8 = r21
        L_0x0169:
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x0181 }
            r9.<init>()     // Catch:{ all -> 0x0181 }
            java.lang.String r11 = "Unable to dump app traces file: "
            r9.append(r11)     // Catch:{ all -> 0x0181 }
            r9.append(r3)     // Catch:{ all -> 0x0181 }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x0181 }
            android.os.AnrMonitor.logDumpTrace(r9, r0)     // Catch:{ all -> 0x0181 }
            android.os.StrictMode.setThreadPolicy(r4)
            return
        L_0x0181:
            r0 = move-exception
            goto L_0x0188
        L_0x0183:
            r0 = move-exception
            r10 = r19
        L_0x0186:
            r8 = r21
        L_0x0188:
            android.os.StrictMode.setThreadPolicy(r4)
            throw r0
        L_0x018c:
            r10 = r19
            r8 = r21
        L_0x0190:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ActivityManagerServiceInjector.dumpAppTrace(com.android.server.am.ActivityManagerService, int, java.lang.String, java.lang.String, java.lang.String):void");
    }

    public static void adjustThreadGroup(ProcessRecord app, ProcessRecord top) {
    }

    public static int computeAdjForLowmem(ProcessRecord app, ActivityManagerService ams, long leastServiceIdle, int adj) {
        if (!enableAdjDowngrade || ams.mLastMemoryLevel != 3 || "fg-service".equals(app.adjType) || adj >= 500) {
            return adj;
        }
        if ((leastServiceIdle == JobStatus.NO_LATEST_RUNTIME || leastServiceIdle <= 600000) && !"provider".equals(app.adjType)) {
            return adj;
        }
        Slog.d(TAG, "downgrade: " + app + " from " + app.adjType + " to A service");
        return 500;
    }

    public static boolean checkMemForServiceRestart(ServiceRecord r, ActivityManagerService ams) {
        if (!enableAdjDowngrade || r.createdFromFg || ams.mLastMemoryLevel != 3) {
            return true;
        }
        Slog.d(TAG, "don't allow bg service restart under lowmem: " + r);
        return false;
    }

    public static void verifyForegroundService(ServiceRecord r, Notification notification) {
        if (enableAdjDowngrade) {
            boolean shouldVerify = true;
            int i = 0;
            while (true) {
                if (i < skipVerifyList.length) {
                    if (r.processName != null && r.processName.contains(skipVerifyList[i])) {
                        shouldVerify = false;
                        break;
                    }
                    i++;
                } else {
                    break;
                }
            }
            if (notification.contentView != null || !shouldVerify) {
                r.isForeground = true;
                return;
            }
            return;
        }
        r.isForeground = true;
    }

    public static boolean finishSilentAnr(ProcessRecord app) {
        if (!Build.IS_STABLE_VERSION || app.isInterestingToUserLocked() || app.pid == ActivityManagerService.MY_PID) {
            return false;
        }
        Slog.i(TAG, "finish silent ANR: " + app);
        app.setNotResponding(true);
        app.kill("bg anr", true);
        return true;
    }

    public static boolean finishSilentAnr(ActivityManagerService ams, ProcessRecord app, String activityName, String parentName, String subject, String report, File logFile, ApplicationErrorReport.CrashInfo crashInfo, String headline) {
        ProcessRecord processRecord = app;
        if (!Build.IS_STABLE_VERSION || app.isInterestingToUserLocked() || processRecord.pid == ActivityManagerService.MY_PID) {
            return false;
        }
        Slog.i(TAG, "finish silent ANR: " + app);
        app.setNotResponding(true);
        app.kill("bg anr", true);
        reportANR(ams, ProcessPolicy.REASON_ANR, app, processRecord.processName, activityName, parentName, subject, report, logFile, crashInfo, headline);
        return true;
    }

    public static boolean skipFrozenAppAnr(ApplicationInfo info, int uid) {
        GreezeManagerService greezer;
        if (((PowerManager) mContext.getSystemService("power")).isScreenOn() || (greezer = GreezeManagerService.getService()) == null) {
            return false;
        }
        int appid = UserHandle.getAppId(info.uid);
        if (uid != info.uid) {
            appid = UserHandle.getAppId(uid);
        }
        if (appid <= 1000) {
            return false;
        }
        int[] frozenUids = greezer.getFrozenUids(1);
        if (frozenUids.length > 0) {
            for (int i = 0; i < frozenUids.length; i++) {
                if (frozenUids[i] == appid) {
                    Slog.e(TAG, " matched frozen app is " + frozenUids[i] + " appid is " + appid);
                    return true;
                }
            }
        }
        return false;
    }

    static void markAmsReady() {
        BootEventManager.getInstance().setAmsReady(SystemClock.uptimeMillis());
    }

    static void markUIReady() {
        long bootCompleteTime = SystemClock.uptimeMillis();
        BootEventManager.getInstance().setUIReady(bootCompleteTime);
        BootEventManager.getInstance().setBootComplete(bootCompleteTime);
    }

    static void reportBootEvent() {
        BootEventManager.getInstance();
        BootEventManager.reportBootEvent();
    }

    static void markPrebootAppCount(int count) {
        BootEventManager.getInstance().setPrebootAppCount(count);
    }

    static boolean isExtraQueueEnabled() {
        return MiuiBroadcastManager.isExtraQueueEnabled();
    }

    static int getExtraQueueSize() {
        return MiuiBroadcastManager.getExtraQueueSize();
    }

    static MiuiBroadcastManager getMiuiBroadcastManager(ActivityManagerService ams) {
        return MiuiBroadcastManager.getInstance(ams);
    }

    static boolean initExtraQuqueIfNeed(ActivityManagerService ams, int startIndex) {
        return getMiuiBroadcastManager(ams).initExtraQuqueIfNeed(startIndex);
    }

    static boolean broadcastIntentLocked(ActivityManagerService ams, boolean parallel, boolean replacePending, BroadcastRecord record) {
        return getMiuiBroadcastManager(ams).broadcastIntentLocked(parallel, replacePending, record);
    }

    static BroadcastQueue broadcastQueueByFlag(ActivityManagerService ams, int flags) {
        return getMiuiBroadcastManager(ams).broadcastQueueByFlag(flags);
    }

    static boolean isFgBroadcastQueue(ActivityManagerService ams, BroadcastQueue queue) {
        return getMiuiBroadcastManager(ams).isFgBroadcastQueue(queue);
    }

    static boolean isFgBroadcastQueue(ActivityManagerService ams, ArraySet<BroadcastQueue> queues) {
        return getMiuiBroadcastManager(ams).isFgBroadcastQueue(queues);
    }

    static boolean shouldCrossXSpace(String packageName, int userId) {
        if (userId == 999) {
            long origId = Binder.clearCallingIdentity();
            try {
                if (AppGlobals.getPackageManager().getPackageInfo(packageName, 0, userId) == null) {
                    Binder.restoreCallingIdentity(origId);
                    return true;
                }
            } catch (RemoteException e) {
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(origId);
                throw th;
            }
            Binder.restoreCallingIdentity(origId);
        }
        return false;
    }

    static void setResizeWhiteList(List<String> whiteList) {
        if (whiteList != null) {
            synchronized (mResizeWhiteList) {
                mResizeWhiteList.clear();
                mResizeWhiteList.addAll(whiteList);
            }
        }
    }

    public static boolean inResizeWhiteList(String packageName) {
        boolean contains;
        synchronized (mResizeWhiteList) {
            contains = mResizeWhiteList.contains(packageName);
        }
        return contains;
    }

    public static boolean checkSystemUidHoldingPermissionsLocked(int modeFlags, int callingAppId) {
        return (callingAppId == 1000 || callingAppId == 0) && ignoreSystemUidAppCheck(modeFlags);
    }

    public static boolean ignoreSystemUidAppCheck(int modeFlags) {
        return (Integer.MIN_VALUE & modeFlags) != 0;
    }

    public static boolean isKillProvider(ContentProviderRecord cpr, ProcessRecord proc, ProcessRecord capp) {
        if (capp.curAdj > 200 && !ProcessUtils.isHomeProcess(capp)) {
            return true;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("visible app ");
        sb.append(capp.processName);
        sb.append(" depends on provider ");
        sb.append(cpr.name.flattenToShortString());
        sb.append(" in dying proc ");
        Object obj = "??";
        sb.append(proc != null ? proc.processName : obj);
        sb.append(" (adj ");
        Object obj2 = obj;
        if (proc != null) {
            obj2 = Integer.valueOf(proc.setAdj);
        }
        sb.append(obj2);
        sb.append(")");
        Slog.w(TAG, sb.toString());
        return false;
    }

    static void setResizeBlackList(List<String> blackList) {
        if (blackList != null) {
            synchronized (mResizeBlackList) {
                mResizeBlackList.clear();
                mResizeBlackList.addAll(blackList);
            }
        }
    }

    public static boolean inResizeBlackList(String packageName) {
        boolean contains;
        synchronized (mResizeBlackList) {
            contains = mResizeBlackList.contains(packageName);
        }
        return contains;
    }

    public static boolean isSystemPackage(String packageName, int userId) {
        try {
            ApplicationInfo applicationInfo = AppGlobals.getPackageManager().getApplicationInfo(packageName, 0, userId);
            if (applicationInfo == null) {
                return true;
            }
            int flags = applicationInfo.flags;
            if ((flags & 1) == 0 && (flags & 128) == 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    static boolean isBoostNeeded(ProcessRecord app, String hostingType, String hostingName) {
        String callerPackage = app.callerPackage;
        boolean isNeeded = false;
        boolean isSystem = isSystemPackage(callerPackage, 0);
        boolean isNeeded2 = xiaomiVoiceServiceName.equals(hostingName) || ("service".equals(hostingType) && hostingName.endsWith(mipushServiceName) && mipushCaller.equals(callerPackage) && isSystem);
        if (notificationCaller.equals(callerPackage) || isNeeded2) {
            isNeeded = true;
        }
        if (weixinProcessName.equals(app.processName)) {
            isNeeded = true;
        }
        Slog.d(BOOST_TAG, "hostingType=" + hostingType + ", hostingName=" + hostingName + ", callerPackage=" + callerPackage + ", isSystem=" + isSystem + ", isBoostNeeded=" + isNeeded + ".");
        return isNeeded;
    }

    static void doBoost(ProcessRecord app) {
        if (app.getCurrentSchedulingGroup() != 3) {
            app.setCurrentSchedulingGroup(3);
            Slog.d(BOOST_TAG, "Process is boosted to top app, processName=" + app.processName + ".");
        }
    }

    static boolean doBoostEx(ProcessRecord app, long beginTime) {
        boolean boostNeededNext = false | doTopAppBoost(app, beginTime);
        if (weixinProcessName.equals(app.processName)) {
            return boostNeededNext | doForegroundBoost(app, beginTime);
        }
        return boostNeededNext;
    }

    private static boolean doTopAppBoost(ProcessRecord app, long beginTime) {
        if (SystemClock.uptimeMillis() - beginTime > 3000 || app.getCurrentSchedulingGroup() == 3) {
            return false;
        }
        if (app.getCurrentSchedulingGroup() >= 3) {
            return true;
        }
        app.setCurrentSchedulingGroup(3);
        Slog.d(BOOST_TAG, "Process is boosted to top app, processName=" + app.processName + ".");
        return true;
    }

    private static boolean doForegroundBoost(ProcessRecord app, long beginTime) {
        if (SystemClock.uptimeMillis() - beginTime > KEEP_FOREGROUND_DURATION) {
            return false;
        }
        if (app.getCurrentSchedulingGroup() >= 2) {
            return true;
        }
        app.setCurrentSchedulingGroup(2);
        return true;
    }

    public static void setTopAppUIThread(int pid, char value) {
        writeToNode("/proc/" + pid + "/top_app", value);
    }

    public static void setTopAppUIThread(int pid, int tid, char value) {
        writeToNode("/proc/" + pid + "/task/" + tid + "/top_app", value);
    }

    public static void writeToNode(String node, char value) {
        FileWriter writer = null;
        String commMsg = " " + node + ":" + value;
        String errMsg = "error" + commMsg;
        String succMsg = SyncStorageEngine.MESG_SUCCESS + commMsg;
        try {
            writer = new FileWriter(node);
            writer.write(value);
            Slog.i(SET_TOP_APP, succMsg);
            try {
                writer.close();
            } catch (IOException e) {
                Slog.e(SET_TOP_APP, errMsg, e);
            }
        } catch (IOException e2) {
            Slog.e(SET_TOP_APP, errMsg, e2);
            if (writer != null) {
                writer.close();
            }
        } catch (Throwable th) {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e3) {
                    Slog.e(SET_TOP_APP, errMsg, e3);
                }
            }
            throw th;
        }
    }

    static void checkBoostPriorityForLockTime(long startBoostPriorityTime) {
        long endBoostPriorityTime = SystemClock.uptimeMillis();
        if (endBoostPriorityTime - startBoostPriorityTime > 3000) {
            Slog.w(TAG, "Slow operation: holding ams lock in " + Debug.getCallers(2) + " " + (endBoostPriorityTime - startBoostPriorityTime) + "ms");
        }
    }

    static boolean onTransact(ActivityManagerService service, int code, Parcel data, Parcel reply, int flags) {
        if (code == 16777214) {
            return setPackageHoldOn(service, data, reply);
        }
        return false;
    }

    /* JADX INFO: finally extract failed */
    static boolean setPackageHoldOn(ActivityManagerService service, Parcel data, Parcel reply) {
        data.enforceInterface("android.app.IActivityManager");
        int callingUid = Binder.getCallingUid();
        long ident = Binder.clearCallingIdentity();
        try {
            if (UserHandle.getAppId(callingUid) == 1000) {
                ActivityTaskManagerServiceInjector.setPackageHoldOn(service.mActivityTaskManager, data.readString());
            } else {
                Slog.e(TAG, "Permission Denial: setPackageHoldOn() not from system uid " + callingUid);
            }
            Binder.restoreCallingIdentity(ident);
            reply.writeNoException();
            return true;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(ident);
            throw th;
        }
    }

    static void updateProcessCpuTime(ProcessRecord app) {
        if (sFieldLastCheckedCpuTime != null && sFieldProcCpuTime != null && sFieldBinderCpuTime != null) {
            boolean curImportant = true;
            boolean setImportant = app.setProcState < 7;
            if (app.getCurProcState() >= 7) {
                curImportant = false;
            }
            if (setImportant && !curImportant) {
                try {
                    BinderServerMonitor.clearBinderClientCpuTimeUsed(app.pid);
                    sFieldLastCheckedCpuTime.setLong(app, app.curCpuTime);
                } catch (IllegalArgumentException e) {
                } catch (IllegalAccessException e2) {
                }
            } else if (!setImportant && curImportant) {
                long binderCpuTime = BinderServerMonitor.clearBinderClientCpuTimeUsed(app.pid);
                sFieldProcCpuTime.setLong(app, (sFieldProcCpuTime.getLong(app) + app.curCpuTime) - sFieldLastCheckedCpuTime.getLong(app));
                sFieldBinderCpuTime.setLong(app, sFieldBinderCpuTime.getLong(app) + binderCpuTime);
                sFieldLastCheckedCpuTime.setLong(app, app.curCpuTime);
            }
        }
    }

    static void checkProcessCpuUsage(ArrayList<ProcessRecord> lruProcesses) {
        long uptimeSince;
        long curUptime;
        int i;
        long j;
        ProcessRecord app;
        if (sFieldLastCheckedCpuTime != null && sFieldProcCpuTime != null && sFieldBinderCpuTime != null) {
            BinderServerMonitor.updateBinderClientCpuUsages();
            long curUptime2 = SystemClock.uptimeMillis();
            long uptimeSince2 = curUptime2 - mLastCheckProcessCpuUsageUptime;
            mLastCheckProcessCpuUsageUptime = curUptime2;
            long j2 = 0;
            if (uptimeSince2 > 0) {
                int i2 = lruProcesses.size();
                while (i2 > 0) {
                    int i3 = i2 - 1;
                    ProcessRecord app2 = lruProcesses.get(i3);
                    try {
                        if (app2.setProcState >= 7) {
                            long procCputime = (app2.curCpuTime - sFieldLastCheckedCpuTime.getLong(app2)) + sFieldProcCpuTime.getLong(app2);
                            Long binderCputime = (Long) BinderServerMonitor.getBinderClientCpuUsages().get(Integer.valueOf(app2.pid));
                            if (binderCputime == null) {
                                try {
                                    binderCputime = Long.valueOf(j2);
                                } catch (IllegalArgumentException e) {
                                    curUptime = curUptime2;
                                    uptimeSince = uptimeSince2;
                                    ProcessRecord processRecord = app2;
                                    j = j2;
                                    i = i3;
                                } catch (IllegalAccessException e2) {
                                    curUptime = curUptime2;
                                    uptimeSince = uptimeSince2;
                                    ProcessRecord processRecord2 = app2;
                                    j = j2;
                                    i = i3;
                                }
                            }
                            Long binderCputime2 = Long.valueOf(binderCputime.longValue() + sFieldBinderCpuTime.getLong(app2));
                            if (BinderServerMonitor.computeCpuUsage(uptimeSince2, procCputime + binderCputime2.longValue()) >= BinderServerMonitor.getExcessiveCpuUsageThreshold()) {
                                curUptime = curUptime2;
                                app = app2;
                                uptimeSince = uptimeSince2;
                                j = j2;
                                i = i3;
                                try {
                                    BinderServerMonitor.recordExcessiveCpuUsage(app2.processName, app2.setProcState, app2.pid, app2.uid, uptimeSince2, procCputime, binderCputime2.longValue(), app2.setProcState >= 17);
                                } catch (IllegalAccessException | IllegalArgumentException e3) {
                                }
                            } else {
                                curUptime = curUptime2;
                                uptimeSince = uptimeSince2;
                                app = app2;
                                j = j2;
                                i = i3;
                            }
                        } else {
                            curUptime = curUptime2;
                            uptimeSince = uptimeSince2;
                            app = app2;
                            j = j2;
                            i = i3;
                        }
                        sFieldProcCpuTime.setLong(app, j);
                        sFieldBinderCpuTime.setLong(app, j);
                        sFieldLastCheckedCpuTime.setLong(app, app.curCpuTime);
                    } catch (IllegalArgumentException e4) {
                        curUptime = curUptime2;
                        uptimeSince = uptimeSince2;
                        ProcessRecord processRecord3 = app2;
                        j = j2;
                        i = i3;
                    } catch (IllegalAccessException e5) {
                        curUptime = curUptime2;
                        uptimeSince = uptimeSince2;
                        ProcessRecord processRecord4 = app2;
                        j = j2;
                        i = i3;
                    }
                    j2 = j;
                    i2 = i;
                    curUptime2 = curUptime;
                    uptimeSince2 = uptimeSince;
                }
                long j3 = uptimeSince2;
                return;
            }
            long j4 = uptimeSince2;
        }
    }

    /* JADX WARNING: type inference failed for: r1v0, types: [android.app.Dialog, com.android.server.am.AppErrorDialog] */
    static void showMiuiAppCrashDialog(ActivityManagerService ams, AppErrorDialog.Data data, Context context, ProcessRecord proc, AppErrorResult res) {
        if (isVRMode(context)) {
            broadcastVRAppFC(context);
        } else if (!showAppCrashDialog(ams, data)) {
            AppErrorDialog.Data Dialogdata = new AppErrorDialog.Data();
            Dialogdata.result = res;
            Dialogdata.proc = proc;
            proc.crashDialog = new AppErrorDialog(context, ams, Dialogdata);
        }
    }

    /* JADX WARNING: type inference failed for: r3v4, types: [android.app.Dialog, com.android.server.am.AppErrorDialog] */
    static boolean showAppCrashDialog(ActivityManagerService ams, AppErrorDialog.Data data) {
        ProcessRecord proc = data.proc;
        AppErrorResult res = data.result;
        ErrorReport.sendExceptionReport(ams.mContext, proc.info.packageName, data.crash, 1);
        if (MiuiSettings.Secure.isForceCloseDialogEnabled(ams.mContext)) {
            proc.crashDialog = new AppErrorDialog(ams.mContext, ams, data);
        } else {
            res.set(0);
        }
        return true;
    }

    public static void addSysAppForegroundTime(int uid, UidAppBatteryStatsImpl.UidPackage.Proc sps, long diff) {
        if (uid == 1000 && sps != null) {
            sps.addForegroundTimeLocked(diff);
        }
    }
}
