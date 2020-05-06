package com.android.server.am;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.AppGlobals;
import android.app.ExtraActivityManager;
import android.app.IApplicationThread;
import android.app.MiuiThemeHelper;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.MiuiResources;
import android.os.Binder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.widget.Toast;
import com.android.internal.app.IPerfShielder;
import com.android.internal.app.MiuiServicePriority;
import com.android.internal.os.BackgroundThread;
import com.android.internal.os.ProcessCpuTracker;
import com.android.server.job.JobSchedulerShellCommand;
import com.android.server.pm.PackageManagerService;
import com.android.server.pm.PackageManagerServiceInjector;
import com.android.server.wm.ActivityRecord;
import com.android.server.wm.WindowManagerService;
import com.android.server.wm.WindowProcessUtils;
import com.miui.server.AccessController;
import com.miui.server.PerfShielderService;
import com.miui.server.SecurityManagerService;
import com.miui.server.XSpaceManagerService;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import miui.content.res.IconCustomizer;
import miui.drm.DrmBroadcast;
import miui.mqsas.sdk.event.AnrEvent;
import miui.os.Build;
import miui.os.FileUtils;
import miui.security.ISecurityCallback;
import miui.security.WakePathChecker;
import miui.util.Network;
import miui.util.ReflectionUtils;

public class ExtraActivityManagerService {
    private static final String[] INCOMPATIBLE_ACTIVITIES = {"com.tencent.mobileqq.activity.QQLSActivity"};
    private static final String[] INCOMPATIBLE_PACKAGES = {"com.tencent.mobileqq"};
    private static final boolean IS_GMSCORE_INSTALLED = SystemProperties.getBoolean("ro.miui.has_gmscore", false);
    static final int SHOW_INCOMPATIBLE_ERROR = 1;
    private static String TAG = ExtraActivityManagerService.class.getName();
    private static IPerfShielder mPerfService;
    private static boolean mSystemReady;
    private static ActivityManagerService sAmInstance = null;
    private static HashSet<String> sGoogleAppsNeedBaseApp = new HashSet<>();
    static final Handler sHandler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                CompatibleMessage cMsg = (CompatibleMessage) msg.obj;
                Toast.makeText(cMsg.context, cMsg.message, 0).show();
            }
        }
    };
    private static Method sRemoveTaskByIdLocked;

    static {
        sGoogleAppsNeedBaseApp.add("com.google.android.gm");
        sGoogleAppsNeedBaseApp.add("com.google.android.apps.magazines");
        sGoogleAppsNeedBaseApp.add("com.google.android.apps.books");
        sGoogleAppsNeedBaseApp.add("com.google.android.apps.plus");
        sGoogleAppsNeedBaseApp.add("com.google.android.videos");
        sGoogleAppsNeedBaseApp.add("com.google.android.apps.docs");
        sGoogleAppsNeedBaseApp.add("com.android.vending");
        sGoogleAppsNeedBaseApp.add("com.google.android.youtube");
        sGoogleAppsNeedBaseApp.add("com.google.android.play.games");
        sGoogleAppsNeedBaseApp.add("com.google.android.apps.photos");
        sGoogleAppsNeedBaseApp.add("com.google.android.talk");
        sGoogleAppsNeedBaseApp.add("com.google.android.music");
        sGoogleAppsNeedBaseApp.add("com.google.android.apps.maps");
    }

    public static void adjustMediaButtonReceivers(Context context, List<Object> receivers, List<ActivityManager.RunningAppProcessInfo> procs, String action) {
        if ("android.intent.action.MEDIA_BUTTON".equals(action)) {
            adjustMediaButtonReceivers(context, receivers, procs);
        }
    }

    public static void adjustMediaButtonReceivers(Context context, List<Object> receivers, List<ActivityManager.RunningAppProcessInfo> list) {
        List<String> packageNames;
        if (receivers != null && receivers.size() > 1 && (packageNames = ExtraActivityManager.getPackageNameListForRecentTasks(context)) != null && !packageNames.isEmpty()) {
            int headIdx = 0;
            int receiverSize = receivers.size();
            for (String name : packageNames) {
                for (int i = headIdx; i < receiverSize; i++) {
                    Object r = receivers.get(i);
                    if (name.equals(getPackageNameForReceiver(r))) {
                        receivers.remove(i);
                        receivers.add(headIdx, r);
                        headIdx++;
                    }
                }
            }
        }
    }

    private static String getPackageNameForReceiver(Object receiver) {
        if (receiver instanceof BroadcastFilter) {
            return ((BroadcastFilter) receiver).packageName;
        }
        if (!(receiver instanceof ResolveInfo)) {
            return null;
        }
        ResolveInfo rinfo = (ResolveInfo) receiver;
        if (rinfo.activityInfo != null) {
            return rinfo.activityInfo.packageName;
        }
        return null;
    }

    public static void init() {
        FileUtils.mkdirs(new File(Environment.getDataDirectory(), "sdcard"), 511, -1, -1);
        IconCustomizer.checkModIconsTimestamp();
        mSystemReady = false;
    }

    public static void finishBooting(ActivityManagerService service) {
        XSpaceManagerService.init(service.mContext);
        DrmBroadcast.getInstance(service.mContext).broadcast();
    }

    static final class CompatibleMessage {
        Context context;
        String message;

        public CompatibleMessage(Context c, String msg) {
            this.context = c;
            this.message = msg;
        }
    }

    public static boolean checkRunningCompatibility(Context context, ActivityManagerService ams, IApplicationThread caller, Intent service, String resolvedType, int userId, String callingPackage) {
        if (!mSystemReady) {
            return true;
        }
        try {
            try {
                ResolveInfo rInfo = AppGlobals.getPackageManager().resolveService(service, resolvedType, 1024, userId);
                if (!checkWakePath(ams, caller, callingPackage, service, rInfo != null ? rInfo.serviceInfo : null, 8, userId)) {
                    return false;
                }
                return true;
            } catch (RemoteException e) {
            }
        } catch (RemoteException e2) {
            Intent intent = service;
            String str = resolvedType;
            int i = userId;
        }
    }

    public static boolean checkRunningCompatibility(Context context, ActivityManagerService ams, IApplicationThread caller, ActivityInfo info, Intent intent, int userId, String callingPackage) {
        if (ams == null) {
            ams = getActivityManagerService();
        }
        if (info == null) {
            return true;
        }
        int i = 0;
        while (true) {
            String[] strArr = INCOMPATIBLE_ACTIVITIES;
            if (i < strArr.length) {
                if (strArr[i].equals(info.name) && INCOMPATIBLE_PACKAGES[i].equals(info.packageName)) {
                    return false;
                }
                i++;
            } else if (!checkWakePath(ams, caller, callingPackage, intent, info, 1, userId)) {
                return false;
            } else {
                return true;
            }
        }
    }

    public static boolean checkRunningCompatibility(Context context, ActivityManagerService ams, IApplicationThread caller, ContentProviderRecord record, int userId) {
        return checkRunningCompatibility(context, ams, caller, -1, record, userId);
    }

    public static boolean checkRunningCompatibility(Context context, ActivityManagerService ams, IApplicationThread caller, int callingUid, ContentProviderRecord record, int userId) {
        if (!mSystemReady || record == null || record.name == null) {
            return true;
        }
        Intent intent = new Intent();
        intent.setClassName(record.name.getPackageName(), record.name.getClassName());
        intent.putExtra("android.intent.extra.UID", callingUid);
        if (!checkWakePath(ams, caller, (String) null, intent, record.info, 4, userId)) {
            return false;
        }
        return true;
    }

    static synchronized ActivityManagerService getActivityManagerService() {
        ActivityManagerService activityManagerService;
        synchronized (ExtraActivityManagerService.class) {
            if (sAmInstance == null) {
                sAmInstance = (ActivityManagerService) ServiceManager.getService("activity");
            }
            activityManagerService = sAmInstance;
        }
        return activityManagerService;
    }

    public static String getPackageNameByPid(int pid) {
        ActivityManagerService amService = getActivityManagerService();
        synchronized (amService.mPidsSelfLocked) {
            ProcessRecord processRecord = amService.mPidsSelfLocked.get(pid);
            if (processRecord == null) {
                return null;
            }
            String str = processRecord.info.packageName;
            return str;
        }
    }

    public static String getProcessNameByPid(int pid) {
        ProcessRecord app;
        ActivityManagerService amService = getActivityManagerService();
        synchronized (amService.mPidsSelfLocked) {
            app = amService.mPidsSelfLocked.get(pid);
        }
        return app == null ? String.valueOf(pid) : app.processName;
    }

    public static int getCurAdjByPid(int pid) {
        ActivityManagerService amService = getActivityManagerService();
        synchronized (amService.mPidsSelfLocked) {
            ProcessRecord processRecord = amService.mPidsSelfLocked.get(pid);
            if (processRecord == null) {
                return Integer.MAX_VALUE;
            }
            int i = processRecord.curAdj;
            return i;
        }
    }

    public static int getProcStateByPid(int pid) {
        ActivityManagerService amService = getActivityManagerService();
        synchronized (amService.mPidsSelfLocked) {
            ProcessRecord processRecord = amService.mPidsSelfLocked.get(pid);
            if (processRecord == null) {
                return -1;
            }
            int curProcState = processRecord.getCurProcState();
            return curProcState;
        }
    }

    public static int getCurSchedGroupByPid(int pid) {
        ActivityManagerService amService = getActivityManagerService();
        synchronized (amService.mPidsSelfLocked) {
            ProcessRecord processRecord = amService.mPidsSelfLocked.get(pid);
            if (processRecord == null) {
                return -1;
            }
            int currentSchedulingGroup = processRecord.getCurrentSchedulingGroup();
            return currentSchedulingGroup;
        }
    }

    public static int getRenderThreadTidByPid(int pid) {
        Field renderThreadTid = ReflectionUtils.tryFindField(ProcessRecord.class, "renderThreadTid");
        if (renderThreadTid == null) {
            return 0;
        }
        ActivityManagerService amService = getActivityManagerService();
        synchronized (amService.mPidsSelfLocked) {
            ProcessRecord proc = amService.mPidsSelfLocked.get(pid);
            if (proc == null) {
                return 0;
            }
            try {
                int tid = renderThreadTid.getInt(proc);
                return tid;
            } catch (Exception e) {
                return 0;
            }
        }
    }

    public static int getExtraInstallFlags(int pid) {
        if (getCurSchedGroupByPid(pid) == 0) {
            return 0 | Integer.MIN_VALUE;
        }
        return 0;
    }

    public static boolean hasForegroundActivities(int pid) {
        ActivityManagerService amService = getActivityManagerService();
        synchronized (amService.mPidsSelfLocked) {
            ProcessRecord processRecord = amService.mPidsSelfLocked.get(pid);
            if (processRecord == null) {
                return false;
            }
            boolean hasForegroundActivities = processRecord.hasForegroundActivities();
            return hasForegroundActivities;
        }
    }

    public static void handleExtraConfigurationChangesForSystem(int changes, Configuration newConfig) {
        MiuiResources.isPreloadedCacheEmpty();
        MiuiThemeHelper.handleExtraConfigurationChangesForSystem(changes, newConfig);
    }

    static void onSystemReady(Context context) {
        mSystemReady = true;
        mPerfService = IPerfShielder.Stub.asInterface(ServiceManager.getService(PerfShielderService.SERVICE_NAME));
        try {
            mPerfService.systemReady();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            ensureDeviceProvisioned(context);
        } catch (Exception e2) {
            Log.e(TAG, "ensureDeviceProvisioned occurs Exception.", e2);
        }
        BackgroundThread.getHandler().post(new Runnable() {
            public void run() {
                Process.setThreadPriority(5);
            }
        });
    }

    private static boolean isDeviceProvisioned(Context context) {
        return Settings.Global.getInt(context.getContentResolver(), "device_provisioned", 0) != 0;
    }

    private static void ensureDeviceProvisioned(Context context) {
        ComponentName checkEnableName;
        if (!isDeviceProvisioned(context)) {
            PackageManager pm = context.getPackageManager();
            if (!Build.IS_INTERNATIONAL_BUILD) {
                checkEnableName = new ComponentName("com.android.provision", "com.android.provision.activities.DefaultActivity");
            } else {
                checkEnableName = new ComponentName("com.google.android.setupwizard", "com.google.android.setupwizard.SetupWizardActivity");
            }
            if (pm != null && pm.getComponentEnabledSetting(checkEnableName) == 2) {
                Log.e(TAG, "The device provisioned state is inconsistent,try to restore.");
                Settings.Secure.putInt(context.getContentResolver(), "device_provisioned", 1);
                if (!Build.IS_INTERNATIONAL_BUILD) {
                    ComponentName name = new ComponentName("com.android.provision", "com.android.provision.activities.DefaultActivity");
                    pm.setComponentEnabledSetting(name, 1, 1);
                    Intent intent = new Intent("android.intent.action.MAIN");
                    intent.setComponent(name);
                    intent.addFlags(268435456);
                    intent.addCategory("android.intent.category.HOME");
                    context.startActivity(intent);
                    return;
                }
                Settings.Secure.putInt(context.getContentResolver(), "user_setup_complete", 1);
            }
        }
    }

    static boolean checkWakePath(ActivityManagerService ams, IApplicationThread caller, String callingPackage, Intent intent, ComponentInfo info, int wakeType, int userId) {
        int callerUid;
        String callerPkg;
        int calleeUid;
        ActivityManagerService activityManagerService = ams;
        IApplicationThread iApplicationThread = caller;
        Intent intent2 = intent;
        ComponentInfo componentInfo = info;
        if (activityManagerService == null || intent2 == null || componentInfo == null) {
            return true;
        }
        WakePathChecker checker = WakePathChecker.getInstance();
        checker.updatePath(intent2, componentInfo, wakeType, userId);
        long startTime = SystemClock.elapsedRealtime();
        String callerPkg2 = "";
        if (iApplicationThread != null) {
            String callerPkg3 = WindowProcessUtils.getCallerPackageName(activityManagerService.mActivityTaskManager, iApplicationThread);
            int callerUid2 = WindowProcessUtils.getCallerUid(activityManagerService.mActivityTaskManager, iApplicationThread);
            if (callerPkg3 == null) {
                return true;
            }
            callerUid = callerUid2;
            callerPkg = callerPkg3;
        } else if (!TextUtils.isEmpty(callingPackage)) {
            callerUid = -1;
            callerPkg = callingPackage;
        } else {
            int callerUid3 = intent2.getIntExtra("android.intent.extra.UID", -1);
            if (callerUid3 != -1) {
                try {
                    String[] pkgs = ams.getPackageManager().getPackagesForUid(callerUid3);
                    if (!(pkgs == null || pkgs.length == 0)) {
                        callerPkg2 = pkgs[0];
                    }
                    callerPkg = callerPkg2;
                    callerUid = callerUid3;
                } catch (Exception e) {
                    Log.e(TAG, "getPackagesFor uid exception!", e);
                    callerPkg = PackageManagerService.PLATFORM_PACKAGE_NAME;
                    callerUid = callerUid3;
                }
            } else {
                callerPkg = PackageManagerService.PLATFORM_PACKAGE_NAME;
                callerUid = callerUid3;
            }
        }
        String calleePkg = componentInfo.packageName;
        String className = componentInfo.name;
        String action = intent.getAction();
        if (componentInfo.applicationInfo != null) {
            calleeUid = componentInfo.applicationInfo.uid;
        } else {
            calleeUid = -1;
        }
        if (TextUtils.isEmpty(calleePkg) != 0 || TextUtils.equals(callerPkg, calleePkg)) {
            return true;
        }
        if (calleeUid >= 0 && WindowProcessUtils.isPackageRunning(activityManagerService.mActivityTaskManager, calleePkg, componentInfo.processName, calleeUid)) {
            return true;
        }
        String str = className;
        String str2 = calleePkg;
        boolean ret = true ^ checker.matchWakePathRule(action, className, callerPkg, calleePkg, callerUid, calleeUid, wakeType, userId);
        checkTime(startTime, "checkWakePath");
        return ret;
    }

    private static void checkTime(long startTime, String where) {
        long now = SystemClock.elapsedRealtime();
        if (now - startTime > 1000) {
            String str = TAG;
            Slog.w(str, "MIUILOG-checkTime:Slow operation: " + (now - startTime) + "ms so far, now at " + where);
        }
    }

    public static ArrayList<Intent> getTaskIntentForToken(IBinder token) {
        return WindowProcessUtils.getTaskIntentForToken(token, ActivityManagerNative.getDefault().mActivityTaskManager);
    }

    static void handleWindowManagerAndUserLru(Context context, int userId, int userIdOrig, int oldUserId, WindowManagerService mWindowManager, int[] mCurrentProfileIds, int switchUserCallingUid) {
        Integer valueOf = Integer.valueOf(userId);
        mWindowManager.setCurrentUser(userIdOrig, mCurrentProfileIds);
        int privacyUserId = Settings.Secure.getInt(context.getContentResolver(), "second_user_id", ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION);
        int kidSpaceUserId = Settings.Secure.getInt(context.getContentResolver(), "kid_user_id", ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION);
        Slog.d("ActivityManagerService", "privacyUserId :" + privacyUserId + " userId:" + userId + " userIdOrig:" + userIdOrig + " oldUserId:" + oldUserId + " kidSpaceUserId:" + kidSpaceUserId);
        if (UserHandle.getAppId(switchUserCallingUid) == 1000 && ((privacyUserId == oldUserId && userId == 0) || ((oldUserId == 0 && userId == privacyUserId) || ((kidSpaceUserId == oldUserId && userId == 0) || (oldUserId == 0 && userId == kidSpaceUserId))))) {
            Slog.d("ActivityManagerService", "switch without lock");
        } else {
            mWindowManager.lockNow((Bundle) null);
        }
    }

    public static int getCurrentUserId() {
        ActivityManagerService amService = getActivityManagerService();
        if (amService.checkCallingPermission("android.permission.INTERACT_ACROSS_USERS") == 0 || amService.checkCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL") == 0) {
            return amService.mUserController.getCurrentOrTargetUserId();
        }
        String msg = "Permission Denial: getCurrentUserId() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " requires " + "android.permission.INTERACT_ACROSS_USERS";
        Slog.w(TAG, msg);
        throw new SecurityException(msg);
    }

    public static HashMap<String, Object> getTopRunningActivityInfo() {
        return WindowProcessUtils.getTopRunningActivityInfo(getActivityManagerService().mActivityTaskManager);
    }

    static void reportAnr(ProcessRecord proc, ActivityRecord activity, AnrEvent event) {
        IPerfShielder iPerfShielder;
        String str;
        if (!event.getBgAnr() || AccessController.PACKAGE_SYSTEMUI.equals(proc.info.packageName)) {
            long duration = 0;
            if (event.isInputAnr()) {
                duration = 8000000000L;
            } else if (event.isBroadcastAnr()) {
                duration = 10000000000L;
            } else if (event.isServiceAnr()) {
                if (proc.execServicesFg) {
                    duration = 20000000000L;
                } else {
                    duration = 200000000000L;
                }
            } else if (event.isProviderAnr()) {
                duration = 20000000000L;
            }
            if (duration != 0 && (iPerfShielder = mPerfService) != null) {
                try {
                    int i = proc.pid;
                    if (activity == null) {
                        str = proc.processName;
                    } else {
                        str = WindowProcessUtils.getActivityComponentName(activity);
                    }
                    iPerfShielder.reportAnr(i, str, duration, System.currentTimeMillis(), event.getCpuInfo());
                } catch (RemoteException e) {
                }
            }
        }
    }

    public static void setSchedFgPid(int pid) {
        IPerfShielder iPerfShielder = mPerfService;
        if (iPerfShielder != null) {
            try {
                iPerfShielder.setSchedFgPid(pid);
            } catch (RemoteException e) {
            }
        }
    }

    static void reserveMemory() {
        IPerfShielder iPerfShielder = mPerfService;
        if (iPerfShielder != null) {
            try {
                iPerfShielder.reserveMemory();
            } catch (RemoteException e) {
            }
        }
    }

    public static void killUnusedApp(int uid, int pid) {
        ActivityManagerService service = getActivityManagerService();
        synchronized (service) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                int i = service.mProcessList.mLruProcesses.size() - 1;
                while (true) {
                    if (i < 0) {
                        break;
                    }
                    ProcessRecord app = service.mProcessList.mLruProcesses.get(i);
                    if (app != null) {
                        if (app.uid == uid && app.pid == pid && app.thread != null && !app.isCrashing() && !app.isNotResponding()) {
                            int tempAdj = app.setAdj;
                            Log.i(PerfShielderService.TAG, "check  package : " + app.info.packageName + "  uid : " + uid + " pid : " + pid + " tempAdj : " + tempAdj);
                            if (tempAdj > 200 && !app.killedByAm) {
                                Log.i(PerfShielderService.TAG, "kill app : " + app.info.packageName + "  uid : " + uid + " pid : " + pid);
                                app.kill("User unused app kill it !!", true);
                            }
                        }
                    }
                    i--;
                }
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    public static void setForkedProcessGroup(int puid, int ppid, int group, String processName) {
        IPerfShielder iPerfShielder = mPerfService;
        if (iPerfShielder != null) {
            try {
                iPerfShielder.setForkedProcessGroup(puid, ppid, group, processName);
            } catch (RemoteException e) {
            }
        }
    }

    public static List<Bundle> getRunningProcessInfos() {
        List<Bundle> result = new ArrayList<>();
        List<Integer> pids = new ArrayList<>();
        ActivityManagerService service = getActivityManagerService();
        synchronized (service) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                for (int i = service.mProcessList.mLruProcesses.size() - 1; i >= 0; i--) {
                    ProcessRecord proc = service.mProcessList.mLruProcesses.get(i);
                    int curAdj = proc.getSetAdjWithServices();
                    if (proc.thread != null && curAdj < 500) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("pid", proc.pid);
                        bundle.putInt("adj", curAdj);
                        bundle.putLong("lastPss", proc.lastPss);
                        bundle.putLong("lastPssTime", proc.lastPssTime);
                        bundle.putString("processName", proc.processName);
                        bundle.putInt("packageUid", proc.info.uid);
                        bundle.putString("packageName", proc.info.packageName);
                        result.add(bundle);
                    }
                    pids.add(Integer.valueOf(proc.pid));
                }
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        synchronized (service.mProcessCpuTracker) {
            int N = service.mProcessCpuTracker.countStats();
            for (int i2 = 0; i2 < N; i2++) {
                ProcessCpuTracker.Stats st = service.mProcessCpuTracker.getStats(i2);
                if (st.vsize > 0 && !pids.contains(Integer.valueOf(st.pid))) {
                    Bundle bundle2 = new Bundle();
                    bundle2.putInt("pid", st.pid);
                    bundle2.putInt("adj", JobSchedulerShellCommand.CMD_ERR_NO_PACKAGE);
                    bundle2.putLong("lastPss", -1);
                    bundle2.putLong("lastPssTime", 0);
                    bundle2.putString("processName", st.name);
                    result.add(bundle2);
                }
            }
        }
        return result;
    }

    public static void setServicePriority(List<MiuiServicePriority> servicePrioritys) {
        ActivityManagerService service = getActivityManagerService();
        synchronized (service) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                ActiveServicesInjector.setServicePriority(service, servicePrioritys);
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    public static void setServicePriority(List<MiuiServicePriority> servicePrioritys, long noProcDelayTime) {
        ActivityManagerService service = getActivityManagerService();
        synchronized (service) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                ActiveServicesInjector.setServicePriority(service, servicePrioritys, noProcDelayTime);
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    public static void removeServicePriority(MiuiServicePriority servicePriority, boolean inBlacklist) {
        ActivityManagerService service = getActivityManagerService();
        synchronized (service) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                ActiveServicesInjector.removeServicePriority(service, servicePriority, inBlacklist);
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    public static void closeCheckPriority() {
        ActivityManagerService service = getActivityManagerService();
        synchronized (service) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                ActiveServicesInjector.closeCheckPriority(service);
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    public static int getMemoryTrimLevel() {
        int i;
        ActivityManagerService service = getActivityManagerService();
        if (!UserHandle.isIsolated(Binder.getCallingUid())) {
            synchronized (service) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    i = service.mLastMemoryLevel;
                } catch (Throwable th) {
                    while (true) {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            return i;
        }
        throw new SecurityException("Isolated process not allowed to call getMemoryTrimLevel");
    }

    public static List<String> getConnProviderNamesLocked(String packageName, int userId) {
        ActivityManagerService service = getActivityManagerService();
        ArrayList<String> providerNames = new ArrayList<>();
        synchronized (service) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                if (service.mProviderMap == null) {
                    Slog.d(TAG, "mProviderMap is null !!!");
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    return providerNames;
                }
                ArrayList<ContentProviderRecord> providers = new ArrayList<>();
                service.mProviderMap.collectPackageProvidersLocked(packageName, (Set<String>) null, true, false, userId, providers);
                Iterator<ContentProviderRecord> it = providers.iterator();
                while (it.hasNext()) {
                    Iterator<ContentProviderConnection> it2 = it.next().connections.iterator();
                    while (it2.hasNext()) {
                        ContentProviderConnection conn = it2.next();
                        if (!(conn == null || conn.client == null || conn.client.processName == null)) {
                            providerNames.add(conn.client.processName);
                        }
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
                return providerNames;
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public static long getCachedLostRamKb() {
        return MiuiActivityHelper.getCachedLostRam() / 1024;
    }

    public static boolean isProcessRecordVisible(int pid, int callingUid) {
        ActivityManagerService service = getActivityManagerService();
        int NP = service.mProcessList.mProcessNames.getMap().size();
        for (int ip = 0; ip < NP; ip++) {
            SparseArray<ProcessRecord> apps = (SparseArray) service.mProcessList.mProcessNames.getMap().valueAt(ip);
            int NA = apps.size();
            for (int ia = 0; ia < NA; ia++) {
                ProcessRecord app = apps.valueAt(ia);
                if (app.pid == pid && app.uid == callingUid && app.curAdj == 100 && "visible".equals(app.adjType)) {
                    return true;
                }
            }
        }
        return false;
    }

    static boolean checkAndRunPreinstallation(Intent intent, Context context) {
        if (PackageManagerServiceInjector.getInstallFlag() || intent == null || intent.getComponent() == null || intent.getComponent().getPackageName() == null) {
            return false;
        }
        String packageName = intent.getComponent().getPackageName();
        if (!ifNeedCheck(packageName, context)) {
            return false;
        }
        try {
            ISecurityCallback ism = ((SecurityManagerService) ServiceManager.getService("security")).getGoogleBaseService();
            Slog.d(TAG, "check if need preinstall GMS base apps");
            if (ism != null) {
                if (ism.checkPreInstallNeeded(packageName)) {
                    String str = TAG;
                    Slog.d(str, "pending install GMS base app for: " + packageName);
                    ism.preInstallApps();
                    return true;
                }
            }
            PackageManagerServiceInjector.setInstallFlag(true);
            return false;
        } catch (Exception e) {
            Log.e(TAG, "googlebase remote error！", e);
        }
    }

    private static boolean ifNeedCheck(String pkgName, Context context) {
        if (Build.IS_INTERNATIONAL_BUILD || IS_GMSCORE_INSTALLED || !sGoogleAppsNeedBaseApp.contains(pkgName)) {
            return false;
        }
        if (UserHandle.getCallingUserId() == 0) {
            return Network.isNetworkConnected(context);
        }
        Slog.d(TAG, "not main space");
        return false;
    }
}
