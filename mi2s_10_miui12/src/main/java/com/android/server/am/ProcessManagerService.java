package com.android.server.am;

import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.ActivityThread;
import android.app.AppOpsManager;
import android.app.INotificationManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.Signature;
import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.MiuiSettings;
import android.server.am.SplitScreenReporter;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.os.BackgroundThread;
import com.android.internal.os.BatteryStatsImpl;
import com.android.internal.os.BatteryStatsImplInjector;
import com.android.server.LocalServices;
import com.android.server.ServiceThread;
import com.android.server.am.ProcessPolicy;
import com.android.server.am.ProcessStarter;
import com.android.server.inputmethod.MiuiSecurityInputMethodHelper;
import com.android.server.pm.PackageManagerService;
import com.android.server.wm.ActivityRecord;
import com.android.server.wm.ActivityStack;
import com.android.server.wm.ForegroundInfoManager;
import com.android.server.wm.WindowProcessController;
import com.android.server.wm.WindowProcessUtils;
import com.google.android.collect.Sets;
import com.miui.enterprise.settings.EnterpriseSettings;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import miui.app.backup.BackupManager;
import miui.os.Build;
import miui.process.ActiveUidInfo;
import miui.process.ForegroundInfo;
import miui.process.IActivityChangeListener;
import miui.process.IForegroundInfoListener;
import miui.process.IForegroundWindowListener;
import miui.process.IMiuiApplicationThread;
import miui.process.PreloadProcessData;
import miui.process.ProcessCloudData;
import miui.process.ProcessConfig;
import miui.process.ProcessManager;
import miui.process.ProcessManagerInternal;
import miui.process.ProcessManagerNative;
import miui.process.RunningProcessInfo;
import org.json.JSONException;
import org.json.JSONObject;

public class ProcessManagerService extends ProcessManagerNative {
    static final int CAMERA_BOOST_KILL_NUM = 20;
    private static final boolean DEBUG = true;
    static final long KEEP_DEATH_RECORD_TIME_OUT = 60000;
    static final int MAX_PROCESS_CONFIG_HISTORY = 30;
    static final String PACKAGE_NAME_CAMERA = "com.android.camera";
    static final long PROCESS_RESTART_TIMEOUT = 2000;
    static final int RESTORE_AI_PROCESSES_INFO_MSG = 1;
    static final int RESTORE_FAST_RESTART_PROCESSES_INFO_MSG = 2;
    static final int SKIP_PRELOAD_COUNT_LIMIT = 2;
    static final long SKIP_PRELOAD_FC_TIME_OUT = 1200000;
    public static final boolean SKIP_PRELOAD_KILLED = SystemProperties.getBoolean("ro.sys.proc.skip_pre_killed", true);
    static final long SKIP_PRELOAD_KILLED_TIME_OUT = 300000;
    private static final String TAG = "ProcessManager";
    static final int USER_OWNER = 0;
    static final int USER_XSPACE = 999;
    private AccessibilityManager mAccessibilityManager;
    /* access modifiers changed from: private */
    public ActivityManagerService mActivityManagerService;
    /* access modifiers changed from: private */
    public SparseArray<Map<String, ProcessStarter.ProcessPriorityInfo>> mAdjBoostProcessMap;
    private AppOpsManager mAppOpsManager;
    /* access modifiers changed from: private */
    public Context mContext;
    private Map<Integer, ConcurrentHashMap<String, Integer>> mDiedProcessMap = new ConcurrentHashMap();
    /* access modifiers changed from: private */
    public ForegroundInfoManager mForegroundInfoManager;
    final MainHandler mHandler;
    int mHistoryNext = -1;
    /* access modifiers changed from: private */
    public boolean mIsLowMemory = false;
    /* access modifiers changed from: private */
    public Map<String, Integer> mKilledProcessRecordMap = new ConcurrentHashMap();
    private LowMemoryKillerObserver mLowMemoryKillerObserver;
    private ArrayList<ProcessRecord> mLruProcesses;
    /* access modifiers changed from: private */
    public MiuiApplicationThreadManager mMiuiApplicationThreadManager;
    private INotificationManager mNotificationManager;
    private PackageManagerService mPackageManagerService;
    final ProcessConfig[] mProcessConfigHistory = new ProcessConfig[30];
    /* access modifiers changed from: private */
    public ProcessKiller mProcessKiller;
    /* access modifiers changed from: private */
    public ProcessPolicy mProcessPolicy;
    /* access modifiers changed from: private */
    public ProcessStarter mProcessStarter;
    final ServiceThread mServiceThread;
    private Set<Signature> mSystemSignatures;

    public ProcessManagerService(Context context) {
        this.mContext = context;
        this.mAppOpsManager = (AppOpsManager) this.mContext.getSystemService("appops");
        this.mNotificationManager = NotificationManager.getService();
        this.mActivityManagerService = ActivityManagerNative.getDefault();
        this.mPackageManagerService = ActivityThread.getPackageManager();
        this.mAccessibilityManager = (AccessibilityManager) this.mContext.getSystemService("accessibility");
        this.mLruProcesses = this.mActivityManagerService.mProcessList.mLruProcesses;
        this.mServiceThread = new ServiceThread("ProcessManager", 0, false);
        this.mServiceThread.start();
        this.mHandler = new MainHandler(this.mServiceThread.getLooper());
        this.mProcessKiller = new ProcessKiller(this.mActivityManagerService);
        this.mProcessStarter = new ProcessStarter(this, this.mActivityManagerService);
        this.mProcessPolicy = new ProcessPolicy(this, this.mActivityManagerService, this.mAccessibilityManager, this.mServiceThread);
        BatteryStatsImpl batteryStats = this.mActivityManagerService.mBatteryStatsService.getActiveStatistics();
        try {
            batteryStats.getClass().getMethod("setActiveCallback", new Class[]{BatteryStatsImplInjector.ActiveCallback.class}).invoke(batteryStats, new Object[]{this.mProcessPolicy});
        } catch (Exception e) {
            Slog.w("ProcessManager", " No setActiveCallback Method Exception: ", e);
        }
        this.mMiuiApplicationThreadManager = new MiuiApplicationThreadManager(this.mActivityManagerService);
        this.mForegroundInfoManager = new ForegroundInfoManager(this);
        this.mLowMemoryKillerObserver = new LowMemoryKillerObserver(this);
        this.mLowMemoryKillerObserver.start();
        systemReady();
        LocalServices.addService(ProcessManagerInternal.class, new LocalService());
        this.mAdjBoostProcessMap = new SparseArray<>();
    }

    /* access modifiers changed from: protected */
    public void systemReady() {
        this.mProcessPolicy.systemReady(this.mContext);
    }

    class MainHandler extends Handler {
        public MainHandler(Looper looper) {
            super(looper, (Handler.Callback) null, true);
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 1 || i == 2) {
                synchronized (ProcessManagerService.this.mActivityManagerService) {
                    try {
                        ActivityManagerService.boostPriorityForLockedSection();
                        ProcessManagerService.this.mProcessStarter.restoreLastProcessesInfoLocked(msg.what);
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

    public void shutdown() {
    }

    public ProcessPolicy getProcessPolicy() {
        return this.mProcessPolicy;
    }

    public boolean kill(ProcessConfig config) throws RemoteException {
        if (checkPermission()) {
            int callingPid = Binder.getCallingPid();
            int policy = config.getPolicy();
            Slog.i("ProcessManager", "Kill reason " + getKillReason(config) + " from pid=" + callingPid);
            addConfigToHistory(config);
            this.mProcessPolicy.resetWhiteList(this.mContext, UserHandle.getCallingUserId());
            boolean success = false;
            switch (policy) {
                case 1:
                case 2:
                case 4:
                case 5:
                case 14:
                case 15:
                case 16:
                    success = killAll(config);
                    break;
                case 3:
                case 6:
                case 10:
                    success = killAny(config);
                    break;
                case 7:
                    success = swipeToKillApp(config);
                    break;
                case 11:
                case 12:
                case 13:
                    success = autoKillApp(config);
                    break;
                case 17:
                    success = killByPriority(config);
                    break;
                default:
                    Slog.w("ProcessManager", "unKnown policy");
                    break;
            }
            ProcessRecordInjector.reportAppPss();
            return success;
        }
        String msg = "Permission Denial: ProcessManager.kill() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid();
        Slog.w("ProcessManager", msg);
        throw new SecurityException(msg);
    }

    /* access modifiers changed from: protected */
    public boolean killAll(ProcessConfig config) {
        int policy = config.getPolicy();
        String reason = getKillReason(config);
        Map<Integer, String> fgTaskPackageMap = null;
        List<String> whiteList = null;
        if (policy != 2) {
            whiteList = config.getWhiteList();
            if (policy == 1 || policy == 15 || policy == 4 || policy == 14 || policy == 16) {
                if (whiteList == null) {
                    whiteList = new ArrayList<>();
                }
                if (policy == 15 || policy == 14 || policy == 16) {
                    whiteList.add("com.android.deskclock");
                }
                fgTaskPackageMap = WindowProcessUtils.getPerceptibleRecentAppList(this.mActivityManagerService.mActivityTaskManager);
            }
        }
        if (config.isRemoveTaskNeeded() && config.getRemovingTaskIdList() != null) {
            removeTasksIfNeeded(config.getRemovingTaskIdList(), fgTaskPackageMap != null ? fgTaskPackageMap.keySet() : null, whiteList);
        } else if (policy == 2) {
            removeAllTasks(UserHandle.getCallingUserId(), policy);
        }
        if (whiteList != null) {
            if (fgTaskPackageMap != null) {
                whiteList.addAll(fgTaskPackageMap.values());
            }
            this.mProcessPolicy.addWhiteList(8, whiteList, false);
        }
        killAll(policy, reason);
        return true;
    }

    /* access modifiers changed from: protected */
    public boolean swipeToKillApp(ProcessConfig config) throws RemoteException {
        boolean processHasOtherTask;
        if (config.isUserIdInvalid() || config.isTaskIdInvalid()) {
            String msg = "userId:" + config.getUserId() + " or taskId:" + config.getTaskId() + " is invalid";
            Slog.w("ProcessManager", msg);
            throw new IllegalArgumentException(msg);
        }
        String packageName = config.getKillingPackage();
        if (TextUtils.isEmpty(packageName)) {
            return false;
        }
        int taskId = config.getTaskId();
        List<ProcessRecord> appList = getProcessRecordList(packageName, config.getUserId());
        boolean appHasOtherTask = isAppHasActivityInOtherTask(appList, taskId);
        ProcessRecord taskTopApp = null;
        if (appHasOtherTask) {
            taskTopApp = ProcessUtils.getTaskTopApp(taskId, this.mActivityManagerService);
        }
        if (config.isRemoveTaskNeeded()) {
            if (appList.isEmpty()) {
                removeTaskIfNeeded(taskId, packageName, -1);
            } else {
                removeTaskIfNeeded(taskId, packageName, appList.get(0).info.uid);
            }
        }
        String killReason = getKillReason(config);
        if (!appHasOtherTask) {
            for (ProcessRecord app : appList) {
                if (!Build.IS_INTERNATIONAL_BUILD || !isAppHasForegroundServices(app)) {
                    killOnceByPolicy(app, killReason, config.getPolicy());
                }
            }
            return true;
        } else if (taskTopApp == null) {
            return true;
        } else {
            synchronized (this.mActivityManagerService) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    processHasOtherTask = WindowProcessUtils.isProcessHasActivityInOtherTaskLocked(taskTopApp.getWindowProcessController(), taskId);
                } catch (Throwable th) {
                    while (true) {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            if (processHasOtherTask) {
                return true;
            }
            if (Build.IS_INTERNATIONAL_BUILD && isAppHasForegroundServices(taskTopApp)) {
                return true;
            }
            killOnceByPolicy(taskTopApp, killReason, config.getPolicy(), false);
            return true;
        }
    }

    /* access modifiers changed from: protected */
    public boolean killAny(ProcessConfig config) {
        List<String> killedPackages;
        if (!config.isUserIdInvalid()) {
            String reason = getKillReason(config);
            ArrayMap<Integer, List<String>> killingPackageMaps = config.getKillingPackageMaps();
            if (killingPackageMaps == null) {
                return false;
            }
            if (config.isRemoveTaskNeeded()) {
                List<String> removedTasksInPackages = new ArrayList<>();
                for (int i = 0; i < killingPackageMaps.size(); i++) {
                    int killLevel = killingPackageMaps.keyAt(i).intValue();
                    if ((killLevel == 100 || killLevel == 103 || killLevel == 102 || killLevel == 104) && (killedPackages = killingPackageMaps.get(Integer.valueOf(killLevel))) != null && !killedPackages.isEmpty()) {
                        removedTasksInPackages.addAll(killedPackages);
                    }
                }
                Iterator pkgIterator = removedTasksInPackages.iterator();
                while (pkgIterator.hasNext()) {
                    String pkg = pkgIterator.next();
                    if (!TextUtils.isEmpty(pkg) && !isTrimMemoryEnable(pkg)) {
                        pkgIterator.remove();
                    }
                }
                removeTasksInPackages(removedTasksInPackages, config.getUserId());
            }
            for (int i2 = 0; i2 < killingPackageMaps.size(); i2++) {
                int killLevel2 = killingPackageMaps.keyAt(i2).intValue();
                List<String> killingPackages = killingPackageMaps.valueAt(i2);
                if (killingPackages != null && killingPackages.size() > 0) {
                    for (String pkg2 : killingPackages) {
                        if (!skipCurrentProcessInBackup((ProcessRecord) null, pkg2, config.getUserId())) {
                            for (ProcessRecord app : getProcessRecordList(pkg2, config.getUserId())) {
                                if (killLevel2 != 100) {
                                    killOnce(app, reason, killLevel2);
                                } else {
                                    killOnceByPolicy(app, reason, config.getPolicy());
                                }
                            }
                        }
                    }
                }
            }
            return true;
        }
        String msg = "userId:" + config.getUserId() + " is invalid";
        Slog.w("ProcessManager", msg);
        throw new IllegalArgumentException(msg);
    }

    /* access modifiers changed from: protected */
    public boolean killByPriority(ProcessConfig config) {
        if (!config.isPriorityInvalid()) {
            int minOomAdj = config.getPriority();
            if (minOomAdj <= 200) {
                Slog.w("ProcessManager", "minOomAdj:" + minOomAdj + ", may kill perceptible app!");
            }
            String killReason = TextUtils.isEmpty(config.getReason()) ? getKillReason(config.getPolicy()) : config.getReason();
            List<ProcessRecord> appList = ProcessUtils.getProcessListByAdj(this.mActivityManagerService, minOomAdj, (List<String>) null);
            if (appList == null || appList.isEmpty()) {
                Slog.w("ProcessManager", "no process found for adj:" + minOomAdj);
                return false;
            }
            List<String> whiteList = config.getWhiteList();
            if (whiteList != null && !whiteList.isEmpty()) {
                this.mProcessPolicy.addWhiteList(8, whiteList, false);
            }
            filterCurrentProcess(appList, config.getPolicy());
            int i = 0;
            while (true) {
                boolean canForceStop = true;
                if (i >= appList.size()) {
                    return true;
                }
                ProcessRecord process = appList.get(i);
                synchronized (this.mActivityManagerService) {
                    try {
                        ActivityManagerService.boostPriorityForLockedSection();
                        UidRecord uidRec = this.mActivityManagerService.mProcessList.getUidRecordLocked(process.uid);
                        if (uidRec == null || !ActivityManager.isProcStateBackground(uidRec.getCurProcState()) || uidRec.getCurProcState() == 13) {
                            canForceStop = false;
                        }
                    } catch (Throwable th) {
                        while (true) {
                            ActivityManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
                killOnceByPolicy(process, killReason, config.getPolicy(), canForceStop);
                i++;
            }
        } else {
            String msg = "priority:" + config.getPriority() + " is invalid";
            Slog.w("ProcessManager", msg);
            throw new IllegalArgumentException(msg);
        }
    }

    private void removeTaskIfNeeded(int taskId, String packageName, int uid) {
        removeTask(taskId);
    }

    private void removeTask(int taskId) {
        long token = Binder.clearCallingIdentity();
        try {
            WindowProcessUtils.removeTask(taskId, this.mActivityManagerService.mActivityTaskManager);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    private void removeTasksIfNeeded(List<Integer> taskIdList, Set<Integer> whiteTaskSet, List<String> whiteList) {
        long token = Binder.clearCallingIdentity();
        try {
            WindowProcessUtils.removeTasks(taskIdList, whiteTaskSet, this.mProcessPolicy, this.mActivityManagerService.mActivityTaskManager, whiteList);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    private void removeTasksInPackages(List<String> packages, int userId) {
        long token = Binder.clearCallingIdentity();
        try {
            WindowProcessUtils.removeTasksInPackages(packages, userId, this.mProcessPolicy, this.mActivityManagerService.mActivityTaskManager);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    private void removeAllTasks(int userId, int policy) {
        long token = Binder.clearCallingIdentity();
        try {
            WindowProcessUtils.removeAllTasks(this, userId, this.mActivityManagerService.mActivityTaskManager);
            if (userId == 0) {
                WindowProcessUtils.removeAllTasks(this, USER_XSPACE, this.mActivityManagerService.mActivityTaskManager);
            }
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    /* access modifiers changed from: protected */
    public boolean autoKillApp(ProcessConfig config) {
        if (!config.isUidInvalid()) {
            String packageName = config.getKillingPackage();
            int uid = config.getUid();
            if (TextUtils.isEmpty(packageName) || isUidSystem(uid)) {
                Log.d("ProcessManager", "cannot kill packageName:" + packageName + " uid:" + uid);
                return false;
            }
            Log.d("ProcessManager", "start autoKillApp: packageName=" + packageName + ", uid=" + uid);
            ForegroundInfo foregroundInfo = this.mForegroundInfoManager.getForegroundInfo();
            if ((!TextUtils.equals(foregroundInfo.mForegroundPackageName, packageName) || foregroundInfo.mForegroundUid != uid) && (!TextUtils.equals(foregroundInfo.mMultiWindowForegroundPackageName, packageName) || foregroundInfo.mMultiWindowForegroundUid != uid)) {
                for (ProcessRecord app : getProcessRecordListByPackageAndUid(packageName, uid)) {
                    killOnceByPolicy(app, getKillReason(config), config.getPolicy());
                }
                return true;
            }
            Log.d("ProcessManager", "autoKillApp:" + packageName + " failed, app with foreground activity");
            return false;
        }
        String msg = "uid:" + config.getUserId() + " is invalid";
        Slog.w("ProcessManager", msg);
        throw new IllegalArgumentException(msg);
    }

    public boolean isInWhiteList(WindowProcessController wpc, int userId, int policy) {
        return isInWhiteList(ProcessUtils.getProcessRecordByWPCtl(wpc, this.mActivityManagerService), userId, policy);
    }

    /* access modifiers changed from: package-private */
    public boolean isInWhiteList(ProcessRecord app, int userId, int policy) {
        if (app.uid == UserHandle.getAppId(1002) || app.info.packageName.contains("com.android.cts")) {
            return true;
        }
        switch (policy) {
            case 1:
            case 3:
            case 4:
            case 5:
            case 6:
                break;
            case 2:
                int flags = 5;
                if (EnterpriseSettings.ENTERPRISE_ACTIVATED) {
                    flags = 5 | 4096;
                }
                if (isPackageInList(app.info.packageName, flags) || this.mProcessPolicy.isProcessImportant(app)) {
                    return true;
                }
                return false;
            case 7:
                int flags2 = 13;
                if (EnterpriseSettings.ENTERPRISE_ACTIVATED) {
                    flags2 = 13 | 4096;
                }
                if (isPackageInList(app.info.packageName, flags2) || this.mProcessPolicy.isProcessImportant(app) || this.mProcessPolicy.isFastBootEnable(app.info.packageName, app.info.uid, true)) {
                    return true;
                }
                return false;
            default:
                switch (policy) {
                    case 14:
                    case 15:
                    case 16:
                    case 17:
                        break;
                    default:
                        return false;
                }
        }
        int flags3 = 13;
        if (EnterpriseSettings.ENTERPRISE_ACTIVATED) {
            flags3 = 13 | 4096;
        }
        if (isPackageInList(app.info.packageName, flags3) || this.mProcessPolicy.isLockedApplication(app.info.packageName, userId) || this.mProcessPolicy.isProcessImportant(app) || this.mProcessPolicy.isFastBootEnable(app.info.packageName, app.info.uid, true)) {
            return true;
        }
        return false;
    }

    /* access modifiers changed from: protected */
    public String getKillReason(ProcessConfig config) {
        int policy = config.getPolicy();
        if (policy != 10 || TextUtils.isEmpty(config.getReason())) {
            return getKillReason(policy);
        }
        return config.getReason();
    }

    private String getKillReason(int policy) {
        switch (policy) {
            case 1:
                return ProcessPolicy.REASON_ONE_KEY_CLEAN;
            case 2:
                return ProcessPolicy.REASON_FORCE_CLEAN;
            case 3:
                return ProcessPolicy.REASON_LOCK_SCREEN_CLEAN;
            case 4:
                return ProcessPolicy.REASON_GAME_CLEAN;
            case 5:
                return ProcessPolicy.REASON_OPTIMIZATION_CLEAN;
            case 6:
                return ProcessPolicy.REASON_GARBAGE_CLEAN;
            case 7:
                return ProcessPolicy.REASON_SWIPE_UP_CLEAN;
            case 10:
                return ProcessPolicy.REASON_USER_DEFINED;
            case 11:
                return ProcessPolicy.REASON_AUTO_POWER_KILL;
            case 12:
                return ProcessPolicy.REASON_AUTO_THERMAL_KILL;
            case 13:
                return ProcessPolicy.REASON_AUTO_IDLE_KILL;
            case 14:
                return ProcessPolicy.REASON_AUTO_SLEEP_CLEAN;
            case 15:
                return ProcessPolicy.REASON_AUTO_LOCK_OFF_CLEAN;
            case 16:
                return ProcessPolicy.REASON_AUTO_SYSTEM_ABNORMAL_CLEAN;
            case 17:
                return ProcessPolicy.REASON_AUTO_LOCK_OFF_CLEAN_BY_PRIORITY;
            default:
                return ProcessPolicy.REASON_UNKNOWN;
        }
    }

    /* access modifiers changed from: package-private */
    public boolean checkPermission() {
        int callingPid = Binder.getCallingPid();
        if (Binder.getCallingUid() < 10000) {
            return true;
        }
        ProcessRecord app = getProcessRecordByPid(callingPid);
        if (isSystemApp(app) || hasSystemSignature(app)) {
            return true;
        }
        return false;
    }

    public void updateApplicationLockedState(String packageName, int userId, boolean isLocked) {
        if (checkPermission()) {
            this.mProcessPolicy.updateApplicationLockedState(this.mContext, userId, packageName, isLocked);
            return;
        }
        String msg = "Permission Denial: ProcessManager.updateApplicationLockedState() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid();
        Slog.w("ProcessManager", msg);
        throw new SecurityException(msg);
    }

    public List<String> getLockedApplication(int userId) {
        return this.mProcessPolicy.getLockedApplication(userId);
    }

    public boolean isLockedApplication(String packageName, int userId) throws RemoteException {
        return this.mProcessPolicy.isLockedApplication(packageName, userId);
    }

    private void killAll(int policy, String reason) {
        ArrayList<ProcessRecord> processList;
        if (this.mLruProcesses != null) {
            synchronized (this.mActivityManagerService) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    processList = (ArrayList) this.mLruProcesses.clone();
                } catch (Throwable th) {
                    while (true) {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            if (processList != null && !processList.isEmpty()) {
                Slog.i("ProcessManager", "mLruProcesses size=" + this.mLruProcesses.size());
                filterCurrentProcess(processList, policy);
                for (int i = processList.size() + -1; i >= 0; i--) {
                    killOnceByPolicy(processList.get(i), reason, policy);
                }
            }
        }
    }

    private void killOnceByPolicy(ProcessRecord app, String reason, int policy) {
        killOnceByPolicy(app, reason, policy, true);
    }

    private void killOnceByPolicy(ProcessRecord app, String reason, int policy, boolean canForceStop) {
        if (app != null && app.thread != null && !app.killed && !skipCurrentProcessInBackup(app, app.info.packageName, UserHandle.getCallingUserId())) {
            int killLevel = 100;
            if (app.isPersistent() || app.setAdj < 0 || isInWhiteList(app, app.userId, policy)) {
                ProcessRecordInjector.addAppPssIfNeeded(this, app);
                if (isTrimMemoryEnable(app.info.packageName)) {
                    killLevel = 101;
                }
            } else if (isForceStopEnable(app, policy) && canForceStop) {
                killLevel = HdmiCecKeycode.CEC_KEYCODE_SELECT_MEDIA_FUNCTION;
            } else if (policy == 3) {
                killLevel = 102;
            } else {
                killLevel = 103;
            }
            killOnce(app, reason, killLevel);
        }
    }

    private void killOnce(ProcessRecord app, String reason, int killLevel) {
        killOnce(app, reason, killLevel, false);
    }

    private void killOnce(final ProcessRecord app, String reason, int killLevel, boolean evenForeground) {
        if (app != null && app.thread != null && !app.killed) {
            if (app.processName.equals("com.miui.fmservice:remote")) {
                final Intent intent = new Intent("miui.intent.action.TURN_OFF");
                intent.addFlags(268435456);
                this.mHandler.post(new Runnable() {
                    public void run() {
                        ProcessManagerService.this.mContext.sendBroadcastAsUser(intent, new UserHandle(app.userId));
                    }
                });
                Slog.i("ProcessManager", "don't kill fmservice just send turn off intent");
                return;
            }
            if (killLevel >= 102) {
                Slog.i("ProcessManager", reason + ": " + killLevelToString(killLevel) + " " + app.processName + " Adj=" + app.curAdj + " State=" + app.getCurProcState());
            }
            if (killLevel == 101) {
                this.mProcessKiller.trimMemory(app, evenForeground);
            } else if (killLevel == 102) {
                this.mProcessKiller.killBackgroundApplication(app, reason);
            } else if (killLevel == 103) {
                this.mProcessKiller.killApplication(app, reason, evenForeground);
            } else if (killLevel == 104) {
                this.mProcessKiller.forceStopPackage(app, reason, evenForeground);
            }
        }
    }

    private String killLevelToString(int level) {
        switch (level) {
            case 100:
                return "none";
            case 101:
                return "trim-memory";
            case 102:
                return "kill-background";
            case 103:
                return "kill";
            case HdmiCecKeycode.CEC_KEYCODE_SELECT_MEDIA_FUNCTION /*104*/:
                return "force-stop";
            default:
                return "";
        }
    }

    private void filterCurrentProcess(List<ProcessRecord> processList, int policy) {
        if (processList != null && !processList.isEmpty()) {
            switch (policy) {
                case 14:
                case 15:
                case 16:
                case 17:
                    Set<String> packageNames = new HashSet<>();
                    synchronized (this.mActivityManagerService) {
                        try {
                            ActivityManagerService.boostPriorityForLockedSection();
                            for (int i = this.mLruProcesses.size() - 1; i >= 0; i--) {
                                ProcessRecord app = this.mLruProcesses.get(i);
                                if (app.setAdj <= 0 || app.getWindowProcessController().isInterestingToUser()) {
                                    packageNames.add(app.info.packageName);
                                }
                            }
                        } catch (Throwable th) {
                            while (true) {
                                ActivityManagerService.resetPriorityAfterLockedSection();
                                throw th;
                                break;
                            }
                        }
                    }
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    ForegroundInfo foregroundInfo = this.mForegroundInfoManager.getForegroundInfo();
                    if (!TextUtils.isEmpty(foregroundInfo.mForegroundPackageName)) {
                        packageNames.add(foregroundInfo.mForegroundPackageName);
                    }
                    if (!TextUtils.isEmpty(foregroundInfo.mMultiWindowForegroundPackageName)) {
                        packageNames.add(foregroundInfo.mMultiWindowForegroundPackageName);
                    }
                    List<Integer> uids = this.mProcessPolicy.getActiveUidList(3);
                    if (packageNames.size() > 0 || uids.size() > 0) {
                        Iterator<ProcessRecord> iterator = processList.iterator();
                        while (iterator.hasNext()) {
                            ProcessRecord app2 = iterator.next();
                            if (packageNames.contains(app2.info.packageName) || uids.contains(Integer.valueOf(app2.info.uid))) {
                                iterator.remove();
                                Log.i("ProcessManager", "skip kill:" + app2.processName + " for foreground app, pkg = " + app2.info.packageName);
                            }
                        }
                        return;
                    }
                    return;
                default:
                    return;
            }
        }
    }

    private boolean skipCurrentProcessInBackup(ProcessRecord app, String packageName, int userId) {
        BackupManager backupManager = BackupManager.getBackupManager(this.mContext);
        if (backupManager.getState() == 0) {
            return false;
        }
        String curRunningPkg = backupManager.getCurrentRunningPackage();
        if ((TextUtils.isEmpty(packageName) || !packageName.equals(curRunningPkg)) && (app == null || app.thread == null || !app.pkgList.containsKey(curRunningPkg) || app.userId != userId)) {
            return false;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("skip kill:");
        sb.append(app != null ? app.processName : packageName);
        sb.append(" for Backup app");
        Log.i("ProcessManager", sb.toString());
        return true;
    }

    private boolean isForceStopEnable(ProcessRecord app, int policy) {
        if (policy == 13) {
            return true;
        }
        if (!Build.IS_INTERNATIONAL_BUILD && !isSystemApp(app) && !isAllowAutoStart(app.info.packageName, app.info.uid) && !isPackageInList(app.info.packageName, 42)) {
            return true;
        }
        return false;
    }

    public boolean isTrimMemoryEnable(String packageName) {
        return !isPackageInList(packageName, 16);
    }

    /* access modifiers changed from: package-private */
    public boolean isAllowAutoStart(String packageName, int uid) {
        return this.mAppOpsManager.checkOpNoThrow(10008, uid, packageName) == 0;
    }

    private boolean isPackageInList(String packageName, int flags) {
        if (packageName == null) {
            return false;
        }
        for (String item : this.mProcessPolicy.getWhiteList(flags)) {
            if (packageName.equals(item)) {
                return true;
            }
        }
        return false;
    }

    private boolean isSystemApp(int pid) {
        ProcessRecord processRecord = getProcessRecordByPid(pid);
        if (processRecord != null) {
            return isSystemApp(processRecord);
        }
        return false;
    }

    private boolean isSystemApp(ProcessRecord app) {
        if (app == null || app.info == null || (app.info.flags & MiuiSecurityInputMethodHelper.TEXT_PASSWORD) == 0) {
            return false;
        }
        return true;
    }

    private boolean hasSystemSignature(ProcessRecord app) {
        Set<Signature> set;
        PackageInfo systemPackage;
        if (app == null) {
            return false;
        }
        if (this.mSystemSignatures == null && (systemPackage = this.mPackageManagerService.getPackageInfo(PackageManagerService.PLATFORM_PACKAGE_NAME, 64, 0)) != null) {
            this.mSystemSignatures = Sets.newHashSet(systemPackage.signatures);
        }
        PackageInfo appPackage = this.mPackageManagerService.getPackageInfo(app.info.packageName, 64, app.userId);
        if (appPackage == null || (set = this.mSystemSignatures) == null || set.isEmpty()) {
            return false;
        }
        return Sets.newHashSet(appPackage.signatures).containsAll(this.mSystemSignatures);
    }

    private boolean isUidSystem(int uid) {
        return uid % 100000 < 10000;
    }

    private String getPackageNameByPid(int pid) {
        ProcessRecord processRecord = getProcessRecordByPid(pid);
        if (processRecord != null) {
            return processRecord.info.packageName;
        }
        return null;
    }

    public ProcessRecord getProcessRecordByPid(int pid) {
        ProcessRecord processRecord;
        synchronized (this.mActivityManagerService.mPidsSelfLocked) {
            processRecord = this.mActivityManagerService.mPidsSelfLocked.get(pid);
        }
        return processRecord;
    }

    public ProcessRecord getProcessRecord(String processName, int userId) {
        synchronized (this.mActivityManagerService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                int i = this.mLruProcesses.size() - 1;
                while (i >= 0) {
                    ProcessRecord app = this.mLruProcesses.get(i);
                    if (app.thread == null || !app.processName.equals(processName) || app.userId != userId) {
                        i--;
                    } else {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        return app;
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
                return null;
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public List<ProcessRecord> getProcessRecordList(String packageName, int userId) {
        List<ProcessRecord> appList = new ArrayList<>();
        synchronized (this.mActivityManagerService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                for (int i = this.mLruProcesses.size() - 1; i >= 0; i--) {
                    ProcessRecord app = this.mLruProcesses.get(i);
                    if (app.thread != null && app.pkgList.containsKey(packageName) && app.userId == userId) {
                        appList.add(app);
                    }
                }
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        return appList;
    }

    public List<ProcessRecord> getProcessRecordListByPackageAndUid(String packageName, int uid) {
        List<ProcessRecord> appList = new ArrayList<>();
        synchronized (this.mActivityManagerService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                for (int i = this.mLruProcesses.size() - 1; i >= 0; i--) {
                    ProcessRecord app = this.mLruProcesses.get(i);
                    if (app.thread != null && app.pkgList.containsKey(packageName) && app.info.uid == uid) {
                        appList.add(app);
                    }
                }
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        return appList;
    }

    public List<ProcessRecord> getProcessRecordByUid(int uid) {
        List<ProcessRecord> appList = new ArrayList<>();
        synchronized (this.mActivityManagerService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                for (int i = this.mLruProcesses.size() - 1; i >= 0; i--) {
                    ProcessRecord app = this.mLruProcesses.get(i);
                    if (app.thread != null && app.info.uid == uid) {
                        appList.add(app);
                    }
                }
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        return appList;
    }

    private boolean isAppHasActivityInOtherTask(List<ProcessRecord> appList, int curTaskId) {
        synchronized (this.mActivityManagerService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                for (ProcessRecord app : appList) {
                    if (WindowProcessUtils.isProcessHasActivityInOtherTaskLocked(app.getWindowProcessController(), curTaskId)) {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        return true;
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
                return false;
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    private boolean isAppHasForegroundServices(ProcessRecord processRecord) {
        boolean hasForegroundServices;
        synchronized (this.mActivityManagerService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                hasForegroundServices = processRecord.hasForegroundServices();
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        return hasForegroundServices;
    }

    public void recordKillProcessEventIfNeeded(String reason, String processName, int pid) {
        if (!TextUtils.isEmpty(reason) && !TextUtils.isEmpty(processName)) {
            List<String> reasons = new ArrayList<>();
            ProcessPolicy processPolicy = this.mProcessPolicy;
            reasons.addAll(ProcessPolicy.sExpKillProcReasons);
            for (String expReason : reasons) {
                if (reason.contains(expReason)) {
                    increaseRecordCount(processName, this.mKilledProcessRecordMap);
                    long delay = 300000;
                    ProcessPolicy processPolicy2 = this.mProcessPolicy;
                    if (ProcessPolicy.sExpKillProcReasons.contains(reason)) {
                        delay = SKIP_PRELOAD_FC_TIME_OUT;
                    }
                    reduceRecordCountDelay(processName, this.mKilledProcessRecordMap, delay);
                }
            }
        }
    }

    private void increaseRecordCount(String processName, Map<String, Integer> recordMap) {
        Integer expCount = recordMap.get(processName);
        if (expCount == null) {
            expCount = 0;
        }
        Integer valueOf = Integer.valueOf(expCount.intValue() + 1);
        Integer expCount2 = valueOf;
        recordMap.put(processName, valueOf);
    }

    private void reduceRecordCountDelay(final String processName, final Map<String, Integer> recordMap, long delay) {
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                Integer count = (Integer) recordMap.get(processName);
                if (count != null && count.intValue() > 0) {
                    Integer count2 = Integer.valueOf(count.intValue() - 1);
                    if (count2.intValue() <= 0) {
                        recordMap.remove(processName);
                    } else {
                        recordMap.put(processName, count2);
                    }
                }
            }
        }, delay);
    }

    public void updateConfig(ProcessConfig config) throws RemoteException {
        if (!checkPermission()) {
            String msg = "Permission Denial: ProcessManager.updateConfig() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid();
            Slog.w("ProcessManager", msg);
            throw new SecurityException(msg);
        }
    }

    public int startProcesses(List<PreloadProcessData> dataList, int startProcessCount, boolean ignoreMemory, int userId, int flag) throws RemoteException {
        if (!checkPermission()) {
            String msg = "Permission Denial: ProcessManager.startMutiProcesses() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid();
            Slog.w("ProcessManager", msg);
            throw new SecurityException(msg);
        } else if (dataList == null || dataList.size() == 0) {
            throw new IllegalArgumentException("packageNames cannot be null!");
        } else if (dataList.size() < startProcessCount) {
            throw new IllegalArgumentException("illegal start number!");
        } else if (!ignoreMemory && ProcessUtils.isLowMemory()) {
            Slog.w("ProcessManager", "low memory! skip start process!");
            return 0;
        } else if (startProcessCount <= 0) {
            Slog.w("ProcessManager", "startProcessCount <= 0, skip start process!");
            return 0;
        } else if (!allowPreload(dataList, flag)) {
            return 0;
        } else {
            return this.mProcessStarter.startProcesses(dataList, startProcessCount, ignoreMemory, userId, flag);
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:14:0x0031, code lost:
        r3 = r2.next();
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private boolean allowPreload(java.util.List<miui.process.PreloadProcessData> r8, int r9) {
        /*
            r7 = this;
            boolean r0 = SKIP_PRELOAD_KILLED
            if (r0 == 0) goto L_0x0089
            r0 = r9 & 1
            if (r0 == 0) goto L_0x0089
            r0 = 0
        L_0x0009:
            int r1 = r8.size()
            if (r0 >= r1) goto L_0x0081
            java.lang.Object r1 = r8.get(r0)
            miui.process.PreloadProcessData r1 = (miui.process.PreloadProcessData) r1
            if (r1 == 0) goto L_0x007e
            java.lang.String r2 = r1.getPackageName()
            boolean r2 = android.text.TextUtils.isEmpty(r2)
            if (r2 != 0) goto L_0x007e
            java.util.Map<java.lang.String, java.lang.Integer> r2 = r7.mKilledProcessRecordMap
            java.util.Set r2 = r2.keySet()
            java.util.Iterator r2 = r2.iterator()
        L_0x002b:
            boolean r3 = r2.hasNext()
            if (r3 == 0) goto L_0x007e
            java.lang.Object r3 = r2.next()
            java.lang.String r3 = (java.lang.String) r3
            java.util.Map<java.lang.String, java.lang.Integer> r4 = r7.mKilledProcessRecordMap
            java.lang.Object r4 = r4.get(r3)
            java.lang.Integer r4 = (java.lang.Integer) r4
            if (r4 == 0) goto L_0x007e
            int r5 = r4.intValue()
            if (r5 > 0) goto L_0x0048
            goto L_0x007e
        L_0x0048:
            int r5 = r4.intValue()
            r6 = 2
            if (r5 < r6) goto L_0x007d
            java.lang.String r5 = r1.getPackageName()
            boolean r5 = r3.startsWith(r5)
            if (r5 == 0) goto L_0x007d
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            java.lang.String r5 = "skip start "
            r2.append(r5)
            java.lang.String r5 = r1.getPackageName()
            r2.append(r5)
            java.lang.String r5 = ", because of errors or killed by user before"
            r2.append(r5)
            java.lang.String r2 = r2.toString()
            java.lang.String r5 = "ProcessManager"
            android.util.Slog.w(r5, r2)
            r8.remove(r0)
            goto L_0x007e
        L_0x007d:
            goto L_0x002b
        L_0x007e:
            int r0 = r0 + 1
            goto L_0x0009
        L_0x0081:
            int r0 = r8.size()
            if (r0 > 0) goto L_0x0089
            r0 = 0
            return r0
        L_0x0089:
            r0 = 1
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ProcessManagerService.allowPreload(java.util.List, int):boolean");
    }

    public void foregroundInfoChanged(String foregroundPackageName) {
        if (TextUtils.equals(foregroundPackageName, "com.android.camera")) {
            boostCameraIfNeeded(true);
        }
    }

    public void boostCameraIfNeeded() {
        boostCameraIfNeeded(false);
    }

    private void boostCameraIfNeeded(boolean isCallerSystem) {
        if (this.mProcessPolicy.isCameraBoostEnable() && isCallerSystem) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    long freeMemory = Process.getFreeMemory() / 1024;
                    if (freeMemory <= ProcessManagerService.this.mProcessPolicy.getCameraMemThreshold()) {
                        List<ProcessRecord> procs = ProcessUtils.getProcessListByAdj(ProcessManagerService.this.mActivityManagerService, 900, ProcessManagerService.this.mProcessPolicy.getWhiteList(8192));
                        Collections.sort(procs, new Comparator<ProcessRecord>() {
                            public int compare(ProcessRecord b1, ProcessRecord b2) {
                                return (int) (b2.lastPss - b1.lastPss);
                            }
                        });
                        int i = 20;
                        if (procs.size() < 20) {
                            i = procs.size();
                        }
                        int N = i;
                        int willFree = 0;
                        for (int i2 = 0; i2 < N; i2++) {
                            ProcessRecord app = procs.get(i2);
                            if (app != null) {
                                ProcessManagerService.this.mProcessKiller.killApplication(app, "camera boost", false);
                                willFree = (int) (((long) willFree) + app.lastPss);
                            }
                        }
                        Slog.i("ProcessManager", "boost camera with free mem:" + freeMemory + "KB, kill " + N + " processes, will free memory:" + willFree + "KB");
                        ProcessManagerService.this.reportCameraBoost(freeMemory, N, willFree);
                    }
                }
            });
        }
    }

    /* access modifiers changed from: private */
    public void reportCameraBoost(long nowFree, int killNum, int willFree) {
        final long j = nowFree;
        final int i = killNum;
        final int i2 = willFree;
        final long uptimeMillis = SystemClock.uptimeMillis();
        BackgroundThread.getHandler().post(new Runnable() {
            public void run() {
                JSONObject json = new JSONObject();
                try {
                    json.put("nowFree", j);
                    json.put("killNum", i);
                    json.put("willFree", i2);
                    json.put("totalMem", Process.getTotalMemory() / 1024);
                    json.put(SplitScreenReporter.STR_DEAL_TIME, uptimeMillis);
                    MiuiSettings.System.putString(ProcessManagerService.this.mContext.getContentResolver(), "camera_boost", json.toString());
                } catch (JSONException e) {
                    Log.w("ProcessManager", "error in reportCameraBoost to settings", e);
                }
            }
        });
    }

    public boolean protectCurrentProcess(boolean isProtected, int timeout) throws RemoteException {
        final ProcessRecord app = getProcessRecordByPid(Binder.getCallingPid());
        if (app == null || !this.mProcessPolicy.isInAppProtectList(app.info.packageName)) {
            String msg = "Permission Denial: ProcessManager.protectCurrentProcess() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid();
            Slog.w("ProcessManager", msg);
            throw new SecurityException(msg);
        }
        boolean success = this.mProcessPolicy.protectCurrentProcess(app, isProtected);
        if (isProtected && timeout > 0) {
            this.mHandler.postDelayed(new Runnable() {
                public void run() {
                    ProcessManagerService.this.mProcessPolicy.protectCurrentProcess(app, false);
                }
            }, (long) timeout);
        }
        return success;
    }

    public void adjBoost(String processName, int targetAdj, long timeout, int userId) {
        if (checkPermission()) {
            if (targetAdj < 0) {
                targetAdj = 0;
            }
            if (timeout > 300000 || timeout <= 0) {
                timeout = 300000;
            }
            doAdjBoost(processName, targetAdj, timeout, userId);
            return;
        }
        String msg = "Permission Denial: ProcessManager.adjBoost() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid();
        Slog.w("ProcessManager", msg);
        throw new SecurityException(msg);
    }

    private void doAdjBoost(String processName, int targetAdj, long timeout, int userId) {
        ProcessRecord app = getProcessRecord(processName, userId);
        if (app != null && !app.isPersistent()) {
            Map<String, ProcessStarter.ProcessPriorityInfo> dataMap = this.mAdjBoostProcessMap.get(userId);
            if (dataMap == null) {
                dataMap = new ConcurrentHashMap<>();
                this.mAdjBoostProcessMap.put(userId, dataMap);
            }
            if (dataMap.get(processName) == null) {
                ProcessStarter.ProcessPriorityInfo appInfo = new ProcessStarter.ProcessPriorityInfo();
                dataMap.put(processName, appInfo);
                synchronized (this.mActivityManagerService) {
                    try {
                        ActivityManagerService.boostPriorityForLockedSection();
                        appInfo.app = app;
                        appInfo.maxAdj = app.maxAdj;
                        appInfo.maxProcState = app.maxProcState;
                        app.maxAdj = targetAdj;
                        app.maxProcState = 14;
                        this.mActivityManagerService.updateOomAdjLocked("updateOomAdj_activityChange");
                    } catch (Throwable th) {
                        while (true) {
                            ActivityManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
                if (timeout > 0) {
                    doAdjDeboost(processName, timeout, userId);
                }
                Log.i("ProcessManager", "adj boost for:" + processName + ", timeout:" + timeout);
                return;
            }
            Log.i("ProcessManager", "process:" + processName + " is already boosted!");
        }
    }

    private void doAdjDeboost(final String processName, long timeout, final int userId) {
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                ProcessStarter.ProcessPriorityInfo priorityInfo;
                Map<String, ProcessStarter.ProcessPriorityInfo> data = (Map) ProcessManagerService.this.mAdjBoostProcessMap.get(userId);
                if (data != null && (priorityInfo = data.get(processName)) != null && priorityInfo.app != null && priorityInfo.app.info != null) {
                    synchronized (ProcessManagerService.this.mActivityManagerService) {
                        try {
                            ActivityManagerService.boostPriorityForLockedSection();
                            if (ProcessManagerService.this.mProcessPolicy.isLockedApplication(priorityInfo.app.info.packageName, userId)) {
                                priorityInfo.app.maxAdj = ProcessManager.LOCKED_MAX_ADJ;
                                priorityInfo.app.maxProcState = 14;
                            } else {
                                priorityInfo.app.maxAdj = priorityInfo.maxAdj;
                                priorityInfo.app.maxProcState = priorityInfo.maxProcState;
                            }
                            ProcessManagerService.this.mActivityManagerService.updateOomAdjLocked("updateOomAdj_activityChange");
                        } catch (Throwable th) {
                            while (true) {
                                ActivityManagerService.resetPriorityAfterLockedSection();
                                throw th;
                            }
                        }
                    }
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    data.remove(processName);
                }
            }
        }, timeout);
    }

    /* access modifiers changed from: private */
    public void checkUpdateOomAdjForHome(ActivityRecord foregroundRecord, ForegroundInfo lastInfo) {
        WindowProcessController home;
        ProcessRecord r;
        String foregroundPkgName = null;
        String lastForegroundPkgName = lastInfo != null ? lastInfo.mForegroundPackageName : null;
        if (foregroundRecord != null) {
            foregroundPkgName = foregroundRecord.packageName;
        }
        if (TextUtils.equals(lastForegroundPkgName, foregroundPkgName)) {
            return;
        }
        if (("com.android.camera".equals(foregroundPkgName) || "com.android.camera".equals(lastForegroundPkgName)) && (home = this.mActivityManagerService.mAtmInternal.getHomeProcess()) != null && (r = getProcessRecordByPid(home.getPid())) != null) {
            if ("com.android.camera".equals(foregroundPkgName)) {
                doAdjBoost(r.processName, 100, -1, r.userId);
            } else {
                doAdjDeboost(r.processName, 0, r.userId);
            }
        }
    }

    public void updateCloudData(ProcessCloudData cloudData) throws RemoteException {
        if (!checkPermission()) {
            String msg = "Permission Denial: ProcessManager.updateCloudWhiteList() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid();
            Slog.w("ProcessManager", msg);
            throw new SecurityException(msg);
        } else if (cloudData != null) {
            this.mProcessPolicy.updateCloudData(cloudData);
        } else {
            throw new IllegalArgumentException("cloudData cannot be null!");
        }
    }

    public void registerForegroundInfoListener(IForegroundInfoListener listener) throws RemoteException {
        if (checkPermission()) {
            Log.i("ProcessManager", "registerForegroundInfoListener, caller=" + Binder.getCallingPid() + ", listener=" + listener);
            this.mForegroundInfoManager.registerForegroundInfoListener(listener);
            return;
        }
        String msg = "Permission Denial: ProcessManager.registerForegroundInfoListener() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid();
        Slog.w("ProcessManager", msg);
        throw new SecurityException(msg);
    }

    public void unregisterForegroundInfoListener(IForegroundInfoListener listener) throws RemoteException {
        if (checkPermission()) {
            this.mForegroundInfoManager.unregisterForegroundInfoListener(listener);
            return;
        }
        String msg = "Permission Denial: ProcessManager.unregisterForegroundInfoListener() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid();
        Slog.w("ProcessManager", msg);
        throw new SecurityException(msg);
    }

    public void registerForegroundWindowListener(IForegroundWindowListener listener) throws RemoteException {
        if (checkPermission()) {
            Log.i("ProcessManager", "registerForegroundWindowListener, caller=" + Binder.getCallingPid() + ", listener=" + listener);
            this.mForegroundInfoManager.registerForegroundWindowListener(listener);
            return;
        }
        String msg = "Permission Denial: ProcessManager.registerForegroundWindowListener() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid();
        Slog.w("ProcessManager", msg);
        throw new SecurityException(msg);
    }

    public void unregisterForegroundWindowListener(IForegroundWindowListener listener) throws RemoteException {
        if (checkPermission()) {
            this.mForegroundInfoManager.unregisterForegroundWindowListener(listener);
            return;
        }
        String msg = "Permission Denial: ProcessManager.unregisterForegroundWindowListener() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid();
        Slog.w("ProcessManager", msg);
        throw new SecurityException(msg);
    }

    public void registerActivityChangeListener(List<String> targetPackages, List<String> targetActivities, IActivityChangeListener listener) throws RemoteException {
        if (checkPermission()) {
            this.mForegroundInfoManager.registerActivityChangeListener(targetPackages, targetActivities, listener);
            return;
        }
        String msg = "Permission Denial: ProcessManager.registerActivityChangeListener() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid();
        Slog.w("ProcessManager", msg);
        throw new SecurityException(msg);
    }

    public void unregisterActivityChangeListener(IActivityChangeListener listener) throws RemoteException {
        if (checkPermission()) {
            this.mForegroundInfoManager.unregisterActivityChangeListener(listener);
            return;
        }
        String msg = "Permission Denial: ProcessManager.unregisterActivityChangeListener() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid();
        Slog.w("ProcessManager", msg);
        throw new SecurityException(msg);
    }

    public ForegroundInfo getForegroundInfo() throws RemoteException {
        if (checkPermission()) {
            return this.mForegroundInfoManager.getForegroundInfo();
        }
        String msg = "Permission Denial: ProcessManager.unregisterForegroundInfoListener() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid();
        Slog.w("ProcessManager", msg);
        throw new SecurityException(msg);
    }

    public void addMiuiApplicationThread(IMiuiApplicationThread applicationThread, int pid) throws RemoteException {
        if (Binder.getCallingPid() == pid) {
            this.mMiuiApplicationThreadManager.addMiuiApplicationThread(applicationThread, pid);
            return;
        }
        String msg = "Permission Denial: ProcessManager.addMiuiApplicationThread() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid();
        Slog.w("ProcessManager", msg);
        throw new SecurityException(msg);
    }

    public IMiuiApplicationThread getForegroundApplicationThread() throws RemoteException {
        if (checkPermission()) {
            return this.mMiuiApplicationThreadManager.getMiuiApplicationThread(WindowProcessUtils.getTopRunningPidLocked(this.mActivityManagerService.mActivityTaskManager));
        }
        String msg = "Permission Denial: ProcessManager.getForegroundApplicationThread() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid();
        Slog.w("ProcessManager", msg);
        throw new SecurityException(msg);
    }

    public void notifyForegroundInfoChanged(ActivityRecord foregroundRecord, ActivityStack.ActivityState state, int pid, ApplicationInfo multiWindowAppInfo) {
        final ActivityRecord activityRecord = foregroundRecord;
        final ActivityStack.ActivityState activityState = state;
        final int i = pid;
        final ApplicationInfo applicationInfo = multiWindowAppInfo;
        this.mHandler.post(new Runnable() {
            public void run() {
                ProcessManagerService processManagerService = ProcessManagerService.this;
                processManagerService.checkUpdateOomAdjForHome(activityRecord, processManagerService.mForegroundInfoManager.getForegroundInfo());
                ProcessManagerService.this.mForegroundInfoManager.notifyForegroundInfoChanged(activityRecord, activityState, i, applicationInfo);
            }
        });
    }

    public void notifyForegroundWindowChanged(ActivityRecord foregroundRecord, ActivityStack.ActivityState state, int pid, ApplicationInfo multiWindowAppInfo) {
        final ActivityRecord activityRecord = foregroundRecord;
        final ActivityStack.ActivityState activityState = state;
        final int i = pid;
        final ApplicationInfo applicationInfo = multiWindowAppInfo;
        this.mHandler.post(new Runnable() {
            public void run() {
                ProcessManagerService.this.mForegroundInfoManager.notifyForegroundWindowChanged(activityRecord, activityState, i, applicationInfo);
            }
        });
    }

    public void notifyActivityChanged(final ComponentName curActivityComponent) {
        this.mHandler.post(new Runnable() {
            public void run() {
                ProcessManagerService.this.mForegroundInfoManager.notifyActivityChanged(curActivityComponent);
            }
        });
    }

    /* JADX WARNING: Code restructure failed: missing block: B:45:0x0099, code lost:
        com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:46:0x009c, code lost:
        return r1;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public java.util.List<miui.process.RunningProcessInfo> getRunningProcessInfo(int r6, int r7, java.lang.String r8, java.lang.String r9, int r10) {
        /*
            r5 = this;
            boolean r0 = r5.checkPermission()
            if (r0 == 0) goto L_0x00a3
            if (r10 > 0) goto L_0x000e
            int r10 = android.os.UserHandle.getCallingUserId()
            r0 = r10
            goto L_0x000f
        L_0x000e:
            r0 = r10
        L_0x000f:
            java.util.ArrayList r10 = new java.util.ArrayList
            r10.<init>()
            r1 = r10
            com.android.server.am.ActivityManagerService r2 = r5.mActivityManagerService
            monitor-enter(r2)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x009d }
            if (r6 <= 0) goto L_0x0029
            com.android.server.am.ProcessRecord r10 = r5.getProcessRecordByPid(r6)     // Catch:{ all -> 0x009d }
            r5.fillRunningProcessInfoList(r1, r10)     // Catch:{ all -> 0x009d }
            monitor-exit(r2)     // Catch:{ all -> 0x009d }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return r1
        L_0x0029:
            boolean r10 = android.text.TextUtils.isEmpty(r9)     // Catch:{ all -> 0x009d }
            if (r10 != 0) goto L_0x003b
            com.android.server.am.ProcessRecord r10 = r5.getProcessRecord(r9, r0)     // Catch:{ all -> 0x009d }
            r5.fillRunningProcessInfoList(r1, r10)     // Catch:{ all -> 0x009d }
            monitor-exit(r2)     // Catch:{ all -> 0x009d }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return r1
        L_0x003b:
            boolean r10 = android.text.TextUtils.isEmpty(r8)     // Catch:{ all -> 0x009d }
            if (r10 != 0) goto L_0x0060
            if (r7 <= 0) goto L_0x0060
            java.util.List r10 = r5.getProcessRecordListByPackageAndUid(r8, r7)     // Catch:{ all -> 0x009d }
            java.util.Iterator r3 = r10.iterator()     // Catch:{ all -> 0x009d }
        L_0x004b:
            boolean r4 = r3.hasNext()     // Catch:{ all -> 0x009d }
            if (r4 == 0) goto L_0x005b
            java.lang.Object r4 = r3.next()     // Catch:{ all -> 0x009d }
            com.android.server.am.ProcessRecord r4 = (com.android.server.am.ProcessRecord) r4     // Catch:{ all -> 0x009d }
            r5.fillRunningProcessInfoList(r1, r4)     // Catch:{ all -> 0x009d }
            goto L_0x004b
        L_0x005b:
            monitor-exit(r2)     // Catch:{ all -> 0x009d }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return r1
        L_0x0060:
            boolean r10 = android.text.TextUtils.isEmpty(r8)     // Catch:{ all -> 0x009d }
            if (r10 != 0) goto L_0x007e
            java.util.List r10 = r5.getProcessRecordList(r8, r0)     // Catch:{ all -> 0x009d }
            java.util.Iterator r3 = r10.iterator()     // Catch:{ all -> 0x009d }
        L_0x006e:
            boolean r4 = r3.hasNext()     // Catch:{ all -> 0x009d }
            if (r4 == 0) goto L_0x007e
            java.lang.Object r4 = r3.next()     // Catch:{ all -> 0x009d }
            com.android.server.am.ProcessRecord r4 = (com.android.server.am.ProcessRecord) r4     // Catch:{ all -> 0x009d }
            r5.fillRunningProcessInfoList(r1, r4)     // Catch:{ all -> 0x009d }
            goto L_0x006e
        L_0x007e:
            if (r7 <= 0) goto L_0x0098
            java.util.List r10 = r5.getProcessRecordByUid(r7)     // Catch:{ all -> 0x009d }
            java.util.Iterator r3 = r10.iterator()     // Catch:{ all -> 0x009d }
        L_0x0088:
            boolean r4 = r3.hasNext()     // Catch:{ all -> 0x009d }
            if (r4 == 0) goto L_0x0098
            java.lang.Object r4 = r3.next()     // Catch:{ all -> 0x009d }
            com.android.server.am.ProcessRecord r4 = (com.android.server.am.ProcessRecord) r4     // Catch:{ all -> 0x009d }
            r5.fillRunningProcessInfoList(r1, r4)     // Catch:{ all -> 0x009d }
            goto L_0x0088
        L_0x0098:
            monitor-exit(r2)     // Catch:{ all -> 0x009d }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return r1
        L_0x009d:
            r10 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x009d }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r10
        L_0x00a3:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "Permission Denial: ProcessManager.getRunningProcessInfo() from pid="
            r0.append(r1)
            int r1 = android.os.Binder.getCallingPid()
            r0.append(r1)
            java.lang.String r1 = ", uid="
            r0.append(r1)
            int r1 = android.os.Binder.getCallingUid()
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r1 = "ProcessManager"
            android.util.Slog.w(r1, r0)
            java.lang.SecurityException r1 = new java.lang.SecurityException
            r1.<init>(r0)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ProcessManagerService.getRunningProcessInfo(int, int, java.lang.String, java.lang.String, int):java.util.List");
    }

    private void fillRunningProcessInfoList(List<RunningProcessInfo> infoList, ProcessRecord app) {
        RunningProcessInfo info = generateRunningProcessInfo(app);
        if (info != null && !infoList.contains(info)) {
            infoList.add(info);
        }
    }

    /* access modifiers changed from: private */
    public RunningProcessInfo generateRunningProcessInfo(ProcessRecord app) {
        RunningProcessInfo info = null;
        if (app != null && app.thread != null && !app.isCrashing() && !app.isNotResponding()) {
            info = new RunningProcessInfo();
            info.mProcessName = app.processName;
            info.mPid = app.pid;
            info.mUid = app.uid;
            info.mAdj = app.curAdj;
            info.mProcState = app.getCurProcState();
            info.mHasForegroundActivities = app.hasForegroundActivities();
            info.mHasForegroundServices = app.hasForegroundServices();
            info.mPkgList = app.getPackageList();
            info.mLocationForeground = app.hasLocationForegroundServices() || info.mHasForegroundServices;
        }
        return info;
    }

    private List<StatusBarNotification> getAppNotificationWithFlag(String packageName, int uid, int flags) {
        List<StatusBarNotification> notificationList = new ArrayList<>();
        try {
            List<StatusBarNotification> notifications = this.mNotificationManager.getAppActiveNotifications(packageName, UserHandle.getUserId(uid)).getList();
            if (notifications != null) {
                if (!notifications.isEmpty()) {
                    for (StatusBarNotification statusBarNotification : notifications) {
                        if (!(statusBarNotification == null || statusBarNotification.getNotification() == null || (statusBarNotification.getNotification().flags & flags) == 0)) {
                            notificationList.add(statusBarNotification);
                        }
                    }
                    return notificationList;
                }
            }
            return notificationList;
        } catch (RemoteException e) {
        }
    }

    /* access modifiers changed from: private */
    public void removeNotificationIfNeeded(String packageName, int uid) {
        List<StatusBarNotification> notifications = getAppNotificationWithFlag(packageName, uid, 34);
        if (notifications != null && !notifications.isEmpty()) {
            try {
                for (StatusBarNotification notification : notifications) {
                    this.mNotificationManager.cancelNotificationWithTag(packageName, notification.getTag(), notification.getId(), UserHandle.getUserId(uid));
                    Slog.i("ProcessManager", "remove no clear notification:" + notification);
                }
            } catch (RemoteException e) {
            }
        }
    }

    /* access modifiers changed from: private */
    public void restartProcessIfNeeded(final String packageName, final String processName, final int uid) {
        boolean isLowMemory = ProcessUtils.isLowMemory() || this.mIsLowMemory;
        if (!TextUtils.equals(processName, "com.android.camera") || isLowMemory || Build.IS_LOW_MEMORY_DEVICE) {
            String reason = "";
            if (isLowMemory) {
                reason = " because of low mem!";
            } else if (Build.IS_LOW_MEMORY_DEVICE) {
                reason = " because this device is a lowmemory device!";
            }
            Slog.d("ProcessManager", "skip restart " + processName + reason);
            return;
        }
        int userId = UserHandle.getUserId(uid);
        if (!this.mActivityManagerService.mUserController.isUserRunning(userId, 4)) {
            Slog.i("ProcessManager", "user " + userId + " is still locked. Cannot restart process " + processName);
            return;
        }
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                if (ProcessManagerService.this.isAllowAutoStart(packageName, uid)) {
                    Integer crashCount = (Integer) ProcessManagerService.this.mKilledProcessRecordMap.get(processName);
                    if (crashCount == null || crashCount.intValue() <= 2) {
                        synchronized (ProcessManagerService.this.mActivityManagerService) {
                            try {
                                ActivityManagerService.boostPriorityForLockedSection();
                                ProcessRecord app = ProcessManagerService.this.mProcessStarter.startProcessLocked(processName, processName, UserHandle.getUserId(uid), ProcessStarter.makeHostingTypeFromFlag(2));
                                if (app != null) {
                                    ProcessManagerService.this.mProcessStarter.restoreLastProcessesInfoLocked(2);
                                    ProcessManagerService.this.mHandler.removeMessages(2);
                                    ProcessManagerService.this.mProcessStarter.saveProcessInfoLocked(app, 2);
                                    ProcessManagerService.this.mProcessStarter.addProtectionLocked(app, 2);
                                    ProcessManagerService.this.mHandler.sendEmptyMessageDelayed(2, 7200000);
                                }
                            } catch (Throwable th) {
                                while (true) {
                                    ActivityManagerService.resetPriorityAfterLockedSection();
                                    throw th;
                                }
                            }
                        }
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                    Log.w("ProcessManager", "skip restart " + processName + " due to too much crash!");
                }
            }
        }, PROCESS_RESTART_TIMEOUT);
    }

    public List<ActiveUidInfo> getActiveUidInfo(int flag) throws RemoteException {
        List<ActiveUidInfo> activeUidInfoList;
        if (checkPermission()) {
            List<ProcessPolicy.ActiveUidRecord> activeUidRecords = this.mProcessPolicy.getActiveUidRecordList(flag);
            SparseArray<ActiveUidInfo> activeUidInfos = new SparseArray<>();
            synchronized (this.mActivityManagerService) {
                try {
                    ActivityManagerService.boostPriorityForLockedSection();
                    for (ProcessPolicy.ActiveUidRecord r : activeUidRecords) {
                        for (ProcessRecord app : getProcessRecordByUid(r.uid)) {
                            ActiveUidInfo activeUidInfo = activeUidInfos.get(r.uid);
                            ActiveUidInfo activeUidInfo2 = activeUidInfo;
                            if (activeUidInfo != null) {
                                if (app.curAdj < activeUidInfo2.curAdj) {
                                    activeUidInfo2.curAdj = app.curAdj;
                                }
                                if (app.getCurProcState() < activeUidInfo2.curProcState) {
                                    activeUidInfo2.curProcState = app.getCurProcState();
                                }
                            } else {
                                ActiveUidInfo activeUidInfo3 = generateActiveUidInfo(app, r);
                                if (activeUidInfo3 != null) {
                                    activeUidInfos.put(r.uid, activeUidInfo3);
                                }
                            }
                        }
                    }
                    activeUidInfoList = new ArrayList<>();
                    for (int i = 0; i < activeUidInfos.size(); i++) {
                        activeUidInfoList.add(activeUidInfos.valueAt(i));
                    }
                } catch (Throwable th) {
                    while (true) {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        throw th;
                    }
                }
            }
            ActivityManagerService.resetPriorityAfterLockedSection();
            return activeUidInfoList;
        }
        String msg = "Permission Denial: ProcessManager.getActiveUidInfo() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid();
        Slog.w("ProcessManager", msg);
        throw new SecurityException(msg);
    }

    private ActiveUidInfo generateActiveUidInfo(ProcessRecord app, ProcessPolicy.ActiveUidRecord activeUidRecord) {
        if (app == null || app.thread == null || app.isCrashing() || app.isNotResponding()) {
            return null;
        }
        ActiveUidInfo info = new ActiveUidInfo();
        info.packageName = app.info.packageName;
        info.uid = activeUidRecord.uid;
        info.flag = activeUidRecord.flag;
        info.curAdj = app.curAdj;
        info.curProcState = app.getCurProcState();
        info.foregroundServices = app.hasForegroundServices();
        info.lastBackgroundTime = app.uidRecord.lastBackgroundTime;
        info.numProcs = app.uidRecord.numProcs;
        info.pkgList = app.getPackageList();
        return info;
    }

    public void recordDiedProcessIfNeeded(ProcessRecord app) {
        if (!app.killedByAm && !app.processName.equals(app.info.packageName) && this.mProcessPolicy.isInSecretlyProtectList(app.info.packageName)) {
            ConcurrentHashMap<String, Integer> diedProcess = this.mDiedProcessMap.get(Integer.valueOf(app.userId));
            if (diedProcess == null) {
                diedProcess = new ConcurrentHashMap<>();
                this.mDiedProcessMap.put(Integer.valueOf(app.userId), diedProcess);
            }
            increaseRecordCount(app.processName, diedProcess);
            reduceRecordCountDelay(app.processName, diedProcess, 60000);
        }
    }

    private final class LocalService extends ProcessManagerInternal {
        private LocalService() {
        }

        public void recordKillProcessEventIfNeeded(String reason, String processName, int pid) {
            ProcessManagerService.this.recordKillProcessEventIfNeeded(reason, processName, pid);
        }

        public void notifyForegroundInfoChanged(Object foregroundRecord, Object state, int pid, ApplicationInfo multiWindowAppInfo) {
            if (foregroundRecord != null && (foregroundRecord instanceof ActivityRecord) && state != null && (state instanceof ActivityStack.ActivityState)) {
                ProcessManagerService.this.notifyForegroundInfoChanged((ActivityRecord) foregroundRecord, (ActivityStack.ActivityState) state, pid, multiWindowAppInfo);
            }
        }

        public void notifyForegroundWindowChanged(Object foregroundRecord, Object state, int pid, ApplicationInfo multiWindowAppInfo) {
            if (foregroundRecord != null && (foregroundRecord instanceof ActivityRecord) && state != null && (state instanceof ActivityStack.ActivityState)) {
                ProcessManagerService.this.notifyForegroundWindowChanged((ActivityRecord) foregroundRecord, (ActivityStack.ActivityState) state, pid, multiWindowAppInfo);
            }
        }

        public void notifyActivityChanged(ComponentName curComponentActivity) {
            ProcessManagerService.this.notifyActivityChanged(curComponentActivity);
        }

        public void updateProcessForegroundLocked(int pid) {
            ProcessManagerService.this.mProcessPolicy.updateProcessForegroundLocked(ProcessManagerService.this.getProcessRecordByPid(pid));
        }

        public void forceStopPackage(String packageName, int userId, String reason) {
            ProcessManagerService.this.mActivityManagerService.forceStopPackage(packageName, userId, reason);
        }

        public ApplicationInfo getMultiWindowForegroundAppInfoLocked() {
            return WindowProcessUtils.getMultiWindowForegroundAppInfoLocked(ProcessManagerService.this.mActivityManagerService.mActivityTaskManager);
        }

        public IMiuiApplicationThread getMiuiApplicationThread(int pid) {
            return ProcessManagerService.this.mMiuiApplicationThreadManager.getMiuiApplicationThread(pid);
        }

        public void onCleanUpApplicationRecord(Object processRecord) {
            ProcessRecord app = (ProcessRecord) processRecord;
            if (app != null && app.info != null && !app.isolated) {
                final String packageName = app.info.packageName;
                final int uid = app.uid;
                String processName = app.processName;
                synchronized (ProcessManagerService.this.mActivityManagerService) {
                    try {
                        ActivityManagerService.boostPriorityForLockedSection();
                        UidRecord uidRecord = ProcessManagerService.this.mActivityManagerService.mProcessList.getUidRecordLocked(uid);
                        if (uidRecord == null || uidRecord.numProcs <= 1) {
                            ProcessManagerService.this.mHandler.post(new Runnable() {
                                public void run() {
                                    ProcessManagerService.this.removeNotificationIfNeeded(packageName, uid);
                                }
                            });
                        }
                    } catch (Throwable th) {
                        while (true) {
                            ActivityManagerService.resetPriorityAfterLockedSection();
                            throw th;
                        }
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
                ProcessManagerService.this.restartProcessIfNeeded(packageName, processName, uid);
            }
        }

        public boolean isPackageFastBootEnable(String packageName, int uid, boolean checkPass) {
            return ProcessManagerService.this.mProcessPolicy.isFastBootEnable(packageName, uid, checkPass);
        }

        public List<RunningProcessInfo> getAllRunningProcessInfo() {
            List<RunningProcessInfo> processInfoList = new ArrayList<>();
            synchronized (ProcessManagerService.this.mActivityManagerService.mPidsSelfLocked) {
                for (int i = ProcessManagerService.this.mActivityManagerService.mPidsSelfLocked.size() - 1; i >= 0; i--) {
                    ProcessRecord proc = ProcessManagerService.this.mActivityManagerService.mPidsSelfLocked.valueAt(i);
                    if (proc != null) {
                        processInfoList.add(ProcessManagerService.this.generateRunningProcessInfo(proc));
                    }
                }
            }
            return processInfoList;
        }

        public void onAppBinderDied(Object processRecord) {
            ProcessRecord app = (ProcessRecord) processRecord;
            if (app != null && app.info != null && !app.isolated) {
                ProcessManagerService.this.recordDiedProcessIfNeeded(app);
                if (!(app.getActiveInstrumentation() == null && !app.killedByAm) || ProcessManagerService.this.mActivityManagerService.mProcessList.haveBackgroundProcessLocked()) {
                    boolean unused = ProcessManagerService.this.mIsLowMemory = false;
                } else {
                    boolean unused2 = ProcessManagerService.this.mIsLowMemory = true;
                }
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

    private void addConfigToHistory(ProcessConfig config) {
        config.setKillingClockTime(System.currentTimeMillis());
        this.mHistoryNext = ringAdvance(this.mHistoryNext, 1, 30);
        this.mProcessConfigHistory[this.mHistoryNext] = config;
    }

    public boolean isLowMemory() {
        return this.mIsLowMemory;
    }

    public boolean isDeathCountExceedingLimit(String process, int userId, int limit) {
        ConcurrentHashMap<String, Integer> diedProcess = this.mDiedProcessMap.get(Integer.valueOf(userId));
        return diedProcess != null && diedProcess.containsKey(process) && diedProcess.get(process).intValue() > limit;
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        ProcessConfig config;
        if (!checkPermission()) {
            pw.println("Permission Denial: can't dump ProcessManager from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
            return;
        }
        pw.println("Process Config:");
        int lastIndex = this.mHistoryNext;
        int ringIndex = this.mHistoryNext;
        int i = 0;
        while (ringIndex != -1 && (config = this.mProcessConfigHistory[ringIndex]) != null) {
            pw.print("  #");
            pw.print(i);
            pw.print(": ");
            pw.println(config.toString());
            ringIndex = ringAdvance(ringIndex, -1, 30);
            i++;
            if (ringIndex == lastIndex) {
                break;
            }
        }
        this.mForegroundInfoManager.dump(pw, "    ");
        this.mProcessPolicy.dump(pw, "    ");
    }
}
