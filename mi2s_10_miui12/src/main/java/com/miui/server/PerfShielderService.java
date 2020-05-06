package com.miui.server;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.RemoteException;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.statistics.E2EScenario;
import android.os.statistics.E2EScenarioPayload;
import android.os.statistics.E2EScenarioSettings;
import android.server.am.SplitScreenReporter;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.app.IMiuiSysUser;
import com.android.internal.app.IPerfShielder;
import com.android.internal.app.LaunchTimeRecord;
import com.android.internal.app.MiuiServicePriority;
import com.android.internal.app.QuickAppResolveInfo;
import com.android.server.MiuiBgThread;
import com.android.server.am.ExtraActivityManagerService;
import com.android.server.am.MiuiActivityHelper;
import com.android.server.am.MiuiBroadcastDispatchHelper;
import com.android.server.am.MiuiContentProviderControl;
import com.android.server.am.MiuiSysUserServiceHelper;
import com.android.server.am.ProcessPolicy;
import com.android.server.job.JobSchedulerShellCommand;
import com.android.server.job.controllers.JobStatus;
import com.android.server.slice.SliceClientPermissions;
import com.miui.daemon.performance.server.IMiuiPerfService;
import com.miui.hybrid.hook.CallingPkgHook;
import com.miui.hybrid.hook.FilterInfoInjector;
import com.miui.hybrid.hook.HapLinksInjector;
import com.miui.hybrid.hook.IntentHook;
import com.miui.hybrid.hook.PermissionChecker;
import com.miui.hybrid.hook.PkgInfoHook;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

public final class PerfShielderService extends IPerfShielder.Stub {
    private static final long ACTIVITY_BATCH_MAX_INTERVAL = 60000;
    private static final int ACTIVITY_BATCH_MAX_SIZE = 10;
    private static final long BIND_FAIL_RETRY_TIME = 60000;
    private static final long BIND_RETRY_TIME_BASE = 60000;
    private static final long BIND_RETRY_TIME_MAX = 3600000;
    private static final long BIND_SYSOPT_SERVICE_FIRST = 1500;
    private static final boolean DEBUG = true;
    private static final long DELAY_TIME = 300000;
    private static final int LAUNCH_TYPE_DEFAULT = 0;
    private static final int LAUNCH_TYPE_FROM_HOME = 1;
    private static final String MIUI_SYS_USER_CLASS = "com.miui.daemon.performance.SysoptService";
    private static final String MIUI_SYS_USER_PACKAHE = "com.miui.daemon";
    static final int MSG_BIND_MIUI_SYS_USER = 2;
    static final int MSG_REBIND = 1;
    private static final int NATIVE_ADJ;
    private static final String PERFORMANCE_CLASS = "com.miui.daemon.performance.MiuiPerfService";
    private static final String PERFORMANCE_PACKAGE = "com.miui.daemon";
    private static final String PROCESSGROUP_CGROUP_PATH = "/acct";
    private static final String PROCESSGROUP_CGROUP_PROCS_FILE = "/cgroup.procs";
    private static final String PROCESSGROUP_PID_PREFIX = "pid_";
    private static final String PROCESSGROUP_UID_PREFIX = "uid_";
    private static final int SELF_CAUSE_ANR = 7;
    private static final String[] SELF_CAUSE_NAMES = {"Slow main thread", "Slow handle input", "Slow handle animation", "Slow handle traversal", "Slow bitmap uploads", "Slow issue draw commands", "Slow swap buffers", "ANR"};
    public static final String SERVICE_NAME = "perfshielder";
    private static final String SYSTEM_SERVER = "system_server";
    public static final String TAG = "PerfShielderService";
    private static Pattern WINDOW_NAME_REX = Pattern.compile("(\\w+\\.)+(\\w+)\\/\\.?(\\w+\\.)*(\\w+)");
    private static ArrayList<String> WINDOW_NAME_WHITE_LIST = new ArrayList<>();
    /* access modifiers changed from: private */
    public static long mLastRetryTime = 60000;
    /* access modifiers changed from: private */
    public Context mContext;
    IBinder.DeathRecipient mDeathHandler = new IBinder.DeathRecipient() {
        public void binderDied() {
            Slog.v(PerfShielderService.TAG, "Miui performance service binderDied!");
            PerfShielderService.this.sendRebindServiceMsg(300000);
        }
    };
    /* access modifiers changed from: private */
    public BindServiceHandler mHandler;
    private List<LaunchTimeRecord> mLaunchTimes = new ArrayList();
    /* access modifiers changed from: private */
    public MiuiSysUserServiceConnection mMiuiSysUserConnection = new MiuiSysUserServiceConnection();
    IBinder.DeathRecipient mMiuiSysUserDeathHandler = new IBinder.DeathRecipient() {
        public void binderDied() {
            MiuiSysUserServiceHelper.setMiuiSysUser((IMiuiSysUser) null);
            Slog.v(PerfShielderService.TAG, "MiuiSysUser service binderDied!");
            PerfShielderService.this.mHandler.removeMessages(2);
            PerfShielderService.this.sendBindMiuiSysUserMsg(PerfShielderService.mLastRetryTime);
        }
    };
    private final AtomicReference<ParcelFileDescriptor> mPerfEventSocketFd = new AtomicReference<>();
    private final Object mPerfEventSocketFdLock = new Object();
    protected IMiuiPerfService mPerfService;
    /* access modifiers changed from: private */
    public final ServiceConnection mPerformanceConnection = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName arg0) {
            Slog.v(PerfShielderService.TAG, "Miui performance service disconnected!");
            PerfShielderService perfShielderService = PerfShielderService.this;
            perfShielderService.mPerfService = null;
            if (perfShielderService.mContext != null) {
                PerfShielderService.this.mContext.unbindService(PerfShielderService.this.mPerformanceConnection);
            }
        }

        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            PerfShielderService.this.mPerfService = IMiuiPerfService.Stub.asInterface(arg1);
            PerfShielderService.this.mHandler.removeMessages(1);
            try {
                Slog.v(PerfShielderService.TAG, "Miui performance service connected!");
                PerfShielderService.this.mPerfService.asBinder().linkToDeath(PerfShielderService.this.mDeathHandler, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private Method mReflectGetPssMethod;
    /* access modifiers changed from: private */
    public SystemPressureController mSystemPressureController;
    private WMServiceConnection mWMServiceConnection;

    static {
        WINDOW_NAME_WHITE_LIST.add("Keyguard");
        WINDOW_NAME_WHITE_LIST.add("StatusBar");
        WINDOW_NAME_WHITE_LIST.add("RecentsPanel");
        WINDOW_NAME_WHITE_LIST.add("InputMethod");
        WINDOW_NAME_WHITE_LIST.add("Volume Control");
        WINDOW_NAME_WHITE_LIST.add("GestureStubBottom");
        WINDOW_NAME_WHITE_LIST.add("GestureStub");
        WINDOW_NAME_WHITE_LIST.add("GestureAnywhereView");
        WINDOW_NAME_WHITE_LIST.add("NavigationBar");
        if (Build.VERSION.SDK_INT <= 23) {
            NATIVE_ADJ = -17;
        } else {
            NATIVE_ADJ = JobSchedulerShellCommand.CMD_ERR_NO_PACKAGE;
        }
    }

    public PerfShielderService(Context context) {
        this.mContext = context;
        this.mHandler = new BindServiceHandler(MiuiBgThread.get().getLooper());
        this.mReflectGetPssMethod = reflectDebugGetPssMethod();
        this.mWMServiceConnection = new WMServiceConnection(context);
        this.mSystemPressureController = new SystemPressureController(context);
    }

    public void systemReady() {
        this.mHandler.post(new Runnable() {
            public void run() {
                PerfShielderService.this.mSystemPressureController.start(PerfShielderService.this.mContext);
            }
        });
        this.mHandler.postDelayed(new Runnable() {
            public void run() {
                PerfShielderService.this.bindService();
            }
        }, JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY);
        sendBindMiuiSysUserMsg(BIND_SYSOPT_SERVICE_FIRST);
    }

    private String convertUidPidToPath(int uid, int pid) {
        return PROCESSGROUP_CGROUP_PATH + SliceClientPermissions.SliceAuthority.DELIMITER + PROCESSGROUP_UID_PREFIX + uid + SliceClientPermissions.SliceAuthority.DELIMITER + PROCESSGROUP_PID_PREFIX + pid + PROCESSGROUP_CGROUP_PROCS_FILE;
    }

    private boolean needToLimit(int pid, String processName) {
        boolean limit = false;
        BufferedReader reader = null;
        try {
            BufferedReader reader2 = new BufferedReader(new InputStreamReader(new FileInputStream("/proc/" + pid + "/cmdline")));
            String readLine = reader2.readLine();
            String line = readLine;
            if (readLine != null && line.contains(processName)) {
                limit = true;
            }
            try {
                reader2.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
            if (reader != null) {
                reader.close();
            }
        } catch (Throwable th) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
            throw th;
        }
        return limit;
    }

    public void setForkedProcessGroup(int puid, int ppid, int group, String processName) {
        if ((StrictMode.getThreadPolicyMask() & 3) == 0) {
            BufferedReader reader = null;
            try {
                BufferedReader reader2 = new BufferedReader(new InputStreamReader(new FileInputStream(convertUidPidToPath(puid, ppid))));
                while (true) {
                    String readLine = reader2.readLine();
                    String line = readLine;
                    if (readLine != null) {
                        int subPid = Integer.parseInt(line);
                        if (subPid != ppid) {
                            if (processName == null || needToLimit(subPid, processName)) {
                                Process.setProcessGroup(subPid, group);
                                Slog.i(TAG, "sFPG ppid:" + ppid + " grp:" + group + " forked:" + processName + " pid:" + subPid);
                            }
                        }
                    } else {
                        try {
                            reader2.close();
                            return;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                }
            } catch (Exception e2) {
                e2.printStackTrace();
                if (reader != null) {
                    reader.close();
                }
            } catch (Throwable th) {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
                throw th;
            }
        }
    }

    public int getMemoryTrimLevel() {
        return ExtraActivityManagerService.getMemoryTrimLevel();
    }

    public List<Bundle> getAllRunningProcessMemInfos() {
        List<Bundle> result = ExtraActivityManagerService.getRunningProcessInfos();
        if (result == null) {
            result = new ArrayList<>();
        }
        PidSwapGetter swapgetter = new PidSwapGetter();
        for (Bundle bundle : result) {
            long[] pidStatus = getProcessStatusValues(bundle.getInt("pid"));
            bundle.putLong("swap", pidStatus[0]);
            int ppid = (int) pidStatus[1];
            bundle.putInt("ppid", ppid);
            bundle.putLong("pswap", swapgetter.get(ppid));
            bundle.putLong("rss", pidStatus[2]);
            bundle.putLong("lastRssTime", SystemClock.uptimeMillis());
        }
        return result;
    }

    public List<Bundle> updateProcessFullMemInfoByPids(int[] pids) {
        int pidSize = pids.length;
        List<Bundle> result = new ArrayList<>(pidSize);
        PidSwapGetter swapgetter = new PidSwapGetter();
        for (int i = 0; i < pidSize; i++) {
            Bundle bundle = new Bundle();
            bundle.putInt("pid", pids[i]);
            bundle.putLong("lastPssTime", SystemClock.uptimeMillis());
            bundle.putLong("lastPss", getProcessPss(pids[i]));
            bundle.putLong("lastRssTime", SystemClock.uptimeMillis());
            long[] pidStatus = getProcessStatusValues(pids[i]);
            bundle.putLong("swap", pidStatus[0]);
            int ppid = (int) pidStatus[1];
            bundle.putInt("ppid", ppid);
            bundle.putLong("pswap", swapgetter.get(ppid));
            bundle.putLong("rss", pidStatus[2]);
            result.add(bundle);
        }
        return result;
    }

    public List<Bundle> updateProcessPartialMemInfoByPids(int[] pids) {
        int pidSize = pids.length;
        List<Bundle> result = new ArrayList<>(pidSize);
        PidSwapGetter swapgetter = new PidSwapGetter();
        for (int i = 0; i < pidSize; i++) {
            Bundle bundle = new Bundle();
            bundle.putInt("pid", pids[i]);
            bundle.putLong("lastRssTime", SystemClock.uptimeMillis());
            long[] pidStatus = getProcessStatusValues(pids[i]);
            bundle.putLong("swap", pidStatus[0]);
            int ppid = (int) pidStatus[1];
            bundle.putInt("ppid", ppid);
            bundle.putLong("pswap", swapgetter.get(ppid));
            bundle.putLong("rss", pidStatus[2]);
            result.add(bundle);
        }
        return result;
    }

    private class PidSwapGetter {
        Map<Integer, Long> pidSwapMap;

        private PidSwapGetter() {
            this.pidSwapMap = new HashMap();
        }

        public long get(int pid) {
            if (pid <= 0) {
                return 0;
            }
            if (!this.pidSwapMap.containsKey(Integer.valueOf(pid))) {
                this.pidSwapMap.put(Integer.valueOf(pid), Long.valueOf(PerfShielderService.this.getProcessStatusValues(pid)[0]));
            }
            return this.pidSwapMap.get(Integer.valueOf(pid)).longValue();
        }
    }

    private Method reflectDebugGetPssMethod() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                return Debug.class.getDeclaredMethod("getPss", new Class[]{Integer.TYPE, long[].class, long[].class});
            }
            return Debug.class.getDeclaredMethod("getPss", new Class[]{Integer.TYPE, long[].class});
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } catch (Throwable th) {
            return null;
        }
    }

    private long getProcessPss(int pid) {
        if (this.mReflectGetPssMethod == null) {
            return 0;
        }
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                return ((Long) this.mReflectGetPssMethod.invoke((Object) null, new Object[]{Integer.valueOf(pid), null, null})).longValue();
            }
            return ((Long) this.mReflectGetPssMethod.invoke((Object) null, new Object[]{Integer.valueOf(pid), null})).longValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } catch (Throwable th) {
            return 0;
        }
    }

    /* access modifiers changed from: private */
    public long[] getProcessStatusValues(int pid) {
        long[] procStatusValues = {-1, -1, -1};
        Process.readProcLines("/proc/" + pid + "/status", new String[]{"VmSwap:", "PPid:", "VmRSS:"}, procStatusValues);
        return procStatusValues;
    }

    public void reportPerceptibleJank(int callingPid, int renderThreadTid, String windowName, long totalDuration, long maxFrameDuration, long endTs, int appCause, long numFrames) {
        reportPerceptibleJank(callingPid, renderThreadTid, windowName, totalDuration, maxFrameDuration, endTs, appCause, numFrames, "");
    }

    public void reportAnr(int callingPid, String windowName, long totalDuration, long endTs, String cpuInfo) {
        reportPerceptibleJank(callingPid, -1, windowName, totalDuration, totalDuration, endTs, 7, 0, cpuInfo);
    }

    public void reportPerceptibleJank(int callingPid, int renderThreadTid, String windowName, long totalDuration, long maxFrameDuration, long endTs, int appCause, long numFrames, String cpuInfo) {
        String strAppCause;
        String windowName2 = windowName;
        long j = totalDuration;
        long j2 = maxFrameDuration;
        long j3 = endTs;
        int i = appCause;
        long j4 = numFrames;
        String callingPkg = ExtraActivityManagerService.getPackageNameByPid(callingPid);
        if (callingPkg != null) {
            if (windowName2 != null && !WINDOW_NAME_WHITE_LIST.contains(windowName2) && !WINDOW_NAME_REX.matcher(windowName2).matches()) {
                StringBuilder sb = new StringBuilder();
                sb.append(windowName.length() >= 3 ? windowName2.substring(0, 3) : windowName2);
                sb.append(windowName.hashCode());
                windowName2 = callingPkg + "-" + sb.toString();
            }
            String packageVersion = new PackageVersionNameGetter().get(callingPkg);
            if (i >= 0) {
                String[] strArr = SELF_CAUSE_NAMES;
                if (i < strArr.length) {
                    strAppCause = strArr[i];
                    Slog.d(TAG, callingPkg + "|" + windowName2 + "|" + (j / 1000000) + "|" + j3 + "|" + (j2 / 1000000) + "|" + i + "|" + j4);
                    Bundle bundle = new Bundle();
                    bundle.putInt("pid", callingPid);
                    bundle.putInt("tid", renderThreadTid);
                    bundle.putString(SplitScreenReporter.STR_PKG, callingPkg);
                    bundle.putString("pkgVersion", packageVersion);
                    bundle.putString("window", windowName2);
                    bundle.putLong("totalDuration", j);
                    bundle.putLong("maxFrameDuration", j2);
                    bundle.putLong("endTs", j3);
                    bundle.putString("appCause", strAppCause);
                    bundle.putString("cpuInfo", cpuInfo);
                    bundle.putLong("numFrames", j4);
                    markPerceptibleJank(bundle);
                }
            }
            strAppCause = ProcessPolicy.REASON_UNKNOWN;
            Slog.d(TAG, callingPkg + "|" + windowName2 + "|" + (j / 1000000) + "|" + j3 + "|" + (j2 / 1000000) + "|" + i + "|" + j4);
            Bundle bundle2 = new Bundle();
            bundle2.putInt("pid", callingPid);
            bundle2.putInt("tid", renderThreadTid);
            bundle2.putString(SplitScreenReporter.STR_PKG, callingPkg);
            bundle2.putString("pkgVersion", packageVersion);
            bundle2.putString("window", windowName2);
            bundle2.putLong("totalDuration", j);
            bundle2.putLong("maxFrameDuration", j2);
            bundle2.putLong("endTs", j3);
            bundle2.putString("appCause", strAppCause);
            bundle2.putString("cpuInfo", cpuInfo);
            bundle2.putLong("numFrames", j4);
            markPerceptibleJank(bundle2);
        }
    }

    public void markPerceptibleJank(Bundle bundle) {
        try {
            if (this.mPerfService != null) {
                this.mPerfService.markPerceptibleJank(bundle);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void addActivityLaunchTime(String packageName, String activityName, long launchStartTime, long launchEndTime, boolean fromHome, boolean isColdStart) {
        boolean z = fromHome;
        if (packageName != null) {
            LaunchTimeRecord record = new LaunchTimeRecord(packageName, activityName, launchStartTime, launchEndTime, isColdStart);
            record.setType(z ? 1 : 0);
            this.mLaunchTimes.add(record);
            long batchStartTime = this.mLaunchTimes.get(0).getLaunchStartTime();
            List<LaunchTimeRecord> list = this.mLaunchTimes;
            long batchEndTime = list.get(list.size() - 1).getLaunchEndTime();
            if (z || this.mLaunchTimes.size() >= 10 || batchEndTime < batchStartTime || batchEndTime - batchStartTime >= 60000) {
                reportActivityLaunchRecords();
                this.mLaunchTimes.clear();
            }
        }
    }

    private void reportActivityLaunchRecords() {
        try {
            if (this.mPerfService != null && this.mLaunchTimes.size() > 0) {
                PackageVersionNameGetter versionGetter = new PackageVersionNameGetter();
                List<Bundle> bundles = new ArrayList<>();
                for (int i = 0; i < this.mLaunchTimes.size(); i++) {
                    LaunchTimeRecord record = this.mLaunchTimes.get(i);
                    Bundle bundle = new Bundle();
                    bundle.putString("PackageName", record.getPackageName());
                    bundle.putString("PackageVersion", versionGetter.get(record.getPackageName()));
                    bundle.putString("Activity", record.getActivity());
                    bundle.putLong("LaunchStartTime", record.getLaunchStartTime());
                    bundle.putLong("LaunchEndTime", record.getLaunchEndTime());
                    bundle.putInt("Type", record.getType());
                    bundle.putBoolean("IsColdStart", record.isColdStart());
                    bundles.add(bundle);
                }
                this.mPerfService.reportActivityLaunchRecords(bundles);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void reportExcessiveCpuUsageRecords(List<Bundle> records) {
        try {
            if (this.mPerfService != null && records.size() > 0) {
                this.mPerfService.reportExcessiveCpuUsageRecords(records);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void reportProcessCleanEvent(Bundle bundle) {
        try {
            if (this.mPerfService != null) {
                this.mPerfService.reportProcessCleanEvent(bundle);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private class PackageVersionNameGetter {
        Map<String, String> packageVersionMap;

        private PackageVersionNameGetter() {
            this.packageVersionMap = new HashMap();
        }

        public String get(String packageName) {
            if (TextUtils.isEmpty(packageName)) {
                return "";
            }
            if (!this.packageVersionMap.containsKey(packageName)) {
                String packageVersion = "";
                try {
                    packageVersion = PerfShielderService.this.mContext.getPackageManager().getPackageInfo(packageName, 0).versionName;
                } catch (Exception e) {
                }
                this.packageVersionMap.put(packageName, packageVersion);
            }
            return this.packageVersionMap.get(packageName);
        }
    }

    public void setSchedFgPid(int pid) {
        if (pid > 0) {
            try {
                if (this.mPerfService != null) {
                    this.mPerfService.setSchedFgPid(pid);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void dumpFromFile(PrintWriter pw, String path) {
        File file = new File(path);
        BufferedReader reader = null;
        if (file.exists()) {
            try {
                BufferedReader reader2 = new BufferedReader(new FileReader(file));
                while (true) {
                    String readLine = reader2.readLine();
                    String line = readLine;
                    if (readLine != null) {
                        pw.println(line);
                    } else {
                        try {
                            reader2.close();
                            return;
                        } catch (IOException e) {
                            e.printStackTrace();
                            return;
                        }
                    }
                }
            } catch (Exception e2) {
                e2.printStackTrace(pw);
                if (reader != null) {
                    reader.close();
                }
            } catch (Throwable th) {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
                throw th;
            }
        }
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter pw, String[] args) {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.DUMP") != 0) {
            String errMsg = "Permission Denial: can't dump perfshielder from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " due to missing android.permission.DUMP permission";
            Slog.v(TAG, errMsg);
            pw.println(errMsg);
            return;
        }
        List<String> argsList = Arrays.asList(args);
        if (argsList.contains("SystemPressureControl")) {
            this.mSystemPressureController.dump(fd, pw, argsList);
            return;
        }
        pw.println("---- ION Memory Usage ----");
        dumpFromFile(pw, "/d/ion/heaps/system");
        dumpFromFile(pw, "/d/ion/heaps/ion_mm_heap");
        pw.println("---- End of ION Memory Usage ----\n");
        pw.println("---- minfree & adj ----");
        pw.print("minfree: ");
        dumpFromFile(pw, "/sys/module/lowmemorykiller/parameters/minfree");
        pw.print("    adj: ");
        dumpFromFile(pw, "/sys/module/lowmemorykiller/parameters/adj");
        pw.println("---- End of minfree & adj ----\n");
    }

    /* access modifiers changed from: private */
    public void sendRebindServiceMsg(long delayedTime) {
        this.mHandler.removeMessages(1);
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(1), delayedTime);
    }

    /* access modifiers changed from: private */
    public void bindService() {
        if (this.mContext != null && this.mPerfService == null) {
            Intent intent = new Intent();
            intent.setClassName("com.miui.daemon", PERFORMANCE_CLASS);
            if (!this.mContext.bindServiceAsUser(intent, this.mPerformanceConnection, 1, UserHandle.OWNER)) {
                Slog.v(TAG, "Miui performance: can't bind to com.miui.daemon.performance.MiuiPerfService");
                sendRebindServiceMsg(60000);
                return;
            }
            Slog.v(TAG, "Miui performance service started");
        }
    }

    private final class MiuiSysUserServiceConnection implements ServiceConnection {
        /* access modifiers changed from: private */
        public boolean isServiceDisconnected;

        private MiuiSysUserServiceConnection() {
            this.isServiceDisconnected = false;
        }

        public void onServiceDisconnected(ComponentName arg0) {
            MiuiSysUserServiceHelper.setMiuiSysUser((IMiuiSysUser) null);
            Slog.v(PerfShielderService.TAG, "MiuiSysUser service disconnected!");
            this.isServiceDisconnected = false;
            if (PerfShielderService.this.mContext != null) {
                PerfShielderService.this.mContext.unbindService(PerfShielderService.this.mMiuiSysUserConnection);
            }
        }

        public void onServiceConnected(ComponentName comp, IBinder iObj) {
            this.isServiceDisconnected = true;
            IMiuiSysUser sysOpt = IMiuiSysUser.Stub.asInterface(iObj);
            MiuiSysUserServiceHelper.setMiuiSysUser(sysOpt);
            PerfShielderService.this.mHandler.removeMessages(2);
            try {
                Slog.v(PerfShielderService.TAG, "MiuiSysUser service connected!");
                sysOpt.asBinder().linkToDeath(PerfShielderService.this.mMiuiSysUserDeathHandler, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* access modifiers changed from: private */
    public void sendBindMiuiSysUserMsg(long delayedTime) {
        this.mHandler.sendMessageDelayed(this.mHandler.obtainMessage(2), delayedTime);
    }

    /* access modifiers changed from: private */
    public void bindMiuiSysUser() {
        if (this.mContext != null && !this.mMiuiSysUserConnection.isServiceDisconnected) {
            Intent intent = new Intent();
            intent.setClassName("com.miui.daemon", MIUI_SYS_USER_CLASS);
            if (!this.mContext.bindServiceAsUser(intent, this.mMiuiSysUserConnection, 1, UserHandle.OWNER)) {
                sendBindMiuiSysUserMsg(mLastRetryTime);
                long j = mLastRetryTime;
                long j2 = 3600000;
                if (j < 3600000) {
                    j2 = j << 1;
                }
                mLastRetryTime = j2;
                Slog.v(TAG, "MiuiSysUser: can't bind to com.miui.daemon.performance.SysoptService, retry time == " + mLastRetryTime);
                return;
            }
            Slog.v(TAG, "MiuiSysUser service started");
        }
    }

    public void killUnusedApp(int uid, int pid) {
        checkSystemPermission("killUnusedApp");
        ExtraActivityManagerService.killUnusedApp(uid, pid);
    }

    public String getPackageNameByPid(int pid) {
        checkSystemPermission("getPackageNameByPid");
        return ExtraActivityManagerService.getPackageNameByPid(pid);
    }

    public void setServicePriority(List<MiuiServicePriority> servicePrioritys) {
        checkSystemPermission("setServicePriority");
        if (servicePrioritys != null && servicePrioritys.size() != 0) {
            MiuiSysUserServiceHelper.setEnable(true);
            ExtraActivityManagerService.setServicePriority(servicePrioritys);
        }
    }

    public void setServicePriorityWithNoProc(List<MiuiServicePriority> servicePrioritys, long noProcDelayTime) {
        checkSystemPermission("setServicePriorityWithNoProc");
        if (servicePrioritys != null && servicePrioritys.size() != 0) {
            MiuiSysUserServiceHelper.setEnable(true);
            ExtraActivityManagerService.setServicePriority(servicePrioritys, noProcDelayTime);
        }
    }

    public void removeServicePriority(MiuiServicePriority servicePriority, boolean inBlacklist) {
        checkSystemPermission("removeServicePriority");
        if (servicePriority != null && servicePriority.packageName != null) {
            ExtraActivityManagerService.removeServicePriority(servicePriority, inBlacklist);
        }
    }

    public void closeCheckPriority() {
        checkSystemPermission("closeCheckPriority");
        MiuiSysUserServiceHelper.setEnable(false);
        ExtraActivityManagerService.closeCheckPriority();
    }

    public void setMiuiContentProviderControl(boolean enable) {
        checkSystemPermission("setMiuiContentProviderControl");
        MiuiContentProviderControl mcpc = MiuiContentProviderControl.getInstance();
        if (mcpc == null) {
            return;
        }
        if (enable) {
            mcpc.openProviderControl();
        } else {
            mcpc.closeProviderControl();
        }
    }

    public void setMiuiBroadcastDispatchEnable(boolean enable) {
        checkSystemPermission("setMiuiBroadcastDispatchEnable");
        MiuiBroadcastDispatchHelper.setMiuiBroadcastDispatchEnable(enable);
    }

    public void addTimeConsumingIntent(String[] actions) {
        checkSystemPermission("addTimeConsumingIntent");
        if (actions != null && actions.length != 0) {
            MiuiBroadcastDispatchHelper.addTimeConsumingIntent(actions);
        }
    }

    public void removeTimeConsumingIntent(String[] actions) {
        checkSystemPermission("removeTimeConsumingIntent");
        if (actions != null && actions.length != 0) {
            MiuiBroadcastDispatchHelper.removeTimeConsumingIntent(actions);
        }
    }

    public void clearTimeConsumingIntent() {
        checkSystemPermission("clearTimeConsumingIntent");
        MiuiBroadcastDispatchHelper.clearTimeConsumingIntent();
    }

    private void checkSystemPermission(String name) {
        int callingUid = Binder.getCallingUid();
        if (UserHandle.getAppId(callingUid) != 1000) {
            throw new SecurityException("Caller " + callingUid + " does not match caller of " + name + " !!!");
        }
    }

    public boolean insertPackageInfo(PackageInfo pInfo) throws RemoteException {
        if (PermissionChecker.check(this.mContext)) {
            return PkgInfoHook.getInstance().insert(pInfo);
        }
        Slog.e("PkgInfoHook", "Check permission failed when insert PackageInfo.");
        return false;
    }

    public boolean deletePackageInfo(String pkgName) throws RemoteException {
        if (!PermissionChecker.check(this.mContext)) {
            Slog.e("PkgInfoHook", "Check permission failed when delete PackageInfo.");
            return false;
        } else if (PkgInfoHook.getInstance().delete(pkgName) != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean insertRedirectRule(String callingPkg, String destPkg, String redirectPkgname, Bundle clsNameMap) throws RemoteException {
        if (PermissionChecker.check(this.mContext)) {
            return IntentHook.getInstance().insert(callingPkg, destPkg, redirectPkgname, clsNameMap);
        }
        Slog.e("IntentHook", "Check permission failed when insert RedirectRule.");
        return false;
    }

    public boolean deleteRedirectRule(String callingPkg, String destPkg) throws RemoteException {
        if (!PermissionChecker.check(this.mContext)) {
            Slog.e("IntentHook", "Check permission failed when delete RedirectRule.");
            return false;
        } else if (IntentHook.getInstance().delete(callingPkg, destPkg) != null) {
            return true;
        } else {
            return false;
        }
    }

    public long getFreeMemory() throws RemoteException {
        return MiuiActivityHelper.getFreeMemory();
    }

    public ParcelFileDescriptor getPerfEventSocketFd() throws RemoteException {
        ParcelFileDescriptor fd = this.mPerfEventSocketFd.get();
        if (fd == null || fd.getFileDescriptor() == null || !fd.getFileDescriptor().valid() || Binder.getCallingPid() == Process.myPid()) {
            this.mPerfEventSocketFd.compareAndSet(fd, (Object) null);
        }
        obtainPerfEventSocketFd();
        ParcelFileDescriptor fd2 = this.mPerfEventSocketFd.get();
        if (fd2 == null || fd2.getFileDescriptor() == null || !fd2.getFileDescriptor().valid()) {
            return null;
        }
        try {
            return fd2.dup();
        } catch (IOException e) {
            try {
                fd2.close();
            } catch (IOException e2) {
            }
            this.mPerfEventSocketFd.compareAndSet(fd2, (Object) null);
            obtainPerfEventSocketFd();
            ParcelFileDescriptor fd3 = this.mPerfEventSocketFd.get();
            if (fd3 == null || fd3.getFileDescriptor() == null || !fd3.getFileDescriptor().valid()) {
                return null;
            }
            try {
                return fd3.dup();
            } catch (IOException e3) {
                return null;
            }
        }
    }

    private void obtainPerfEventSocketFd() {
        IMiuiPerfService perfService = this.mPerfService;
        if (this.mPerfEventSocketFd.get() == null && perfService != null) {
            synchronized (this.mPerfEventSocketFdLock) {
                if (this.mPerfEventSocketFd.get() == null) {
                    try {
                        this.mPerfEventSocketFd.set(perfService.getPerfEventSocketFd());
                    } catch (RemoteException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    private class BindServiceHandler extends Handler {
        public BindServiceHandler(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            int i = msg.what;
            if (i == 1) {
                PerfShielderService.this.bindService();
            } else if (i == 2) {
                PerfShielderService.this.bindMiuiSysUser();
            }
        }
    }

    public boolean addCallingPkgHookRule(String hostApp, String originCallingPkg, String hookCallingPkg) throws RemoteException {
        if (PermissionChecker.check(this.mContext)) {
            return CallingPkgHook.getInstance().add(hostApp, originCallingPkg, hookCallingPkg);
        }
        Slog.e("CallingPkgHook", "Check permission failed when addCallingPkgHookRule.");
        return false;
    }

    public boolean removeCallingPkgHookRule(String hostApp, String originCallingPkg) throws RemoteException {
        if (PermissionChecker.check(this.mContext)) {
            return CallingPkgHook.getInstance().remove(hostApp, originCallingPkg);
        }
        Slog.e("CallingPkgHook", "Check permission failed when removeCallingPkgHookRule.");
        return false;
    }

    public Bundle beginScenario(E2EScenario scenario, E2EScenarioSettings settings, String tag, E2EScenarioPayload payload, int tid, long uptimeMillis, boolean needResultBundle) throws RemoteException {
        long uptimeMillis2;
        if (this.mPerfService == null) {
            return null;
        }
        if (uptimeMillis == 0) {
            uptimeMillis2 = SystemClock.uptimeMillis();
        } else {
            uptimeMillis2 = uptimeMillis;
        }
        int pid = Binder.getCallingPid();
        int i = pid;
        return this.mPerfService.beginScenario(scenario, settings, tag, payload, uptimeMillis2, pid, tid, pid == Process.myPid() ? SYSTEM_SERVER : ExtraActivityManagerService.getProcessNameByPid(pid), ExtraActivityManagerService.getPackageNameByPid(pid), needResultBundle);
    }

    public void abortMatchingScenario(E2EScenario scenario, String tag, int tid, long uptimeMillis) throws RemoteException {
        long uptimeMillis2;
        if (this.mPerfService != null) {
            if (uptimeMillis == 0) {
                uptimeMillis2 = SystemClock.uptimeMillis();
            } else {
                uptimeMillis2 = uptimeMillis;
            }
            int pid = Binder.getCallingPid();
            this.mPerfService.abortMatchingScenario(scenario, tag, uptimeMillis2, pid, tid, pid == Process.myPid() ? SYSTEM_SERVER : ExtraActivityManagerService.getProcessNameByPid(pid), ExtraActivityManagerService.getPackageNameByPid(pid));
        }
    }

    public void abortSpecificScenario(Bundle scenarioBundle, int tid, long uptimeMillis) throws RemoteException {
        if (this.mPerfService != null) {
            if (uptimeMillis == 0) {
                uptimeMillis = SystemClock.uptimeMillis();
            }
            int pid = Binder.getCallingPid();
            this.mPerfService.abortSpecificScenario(scenarioBundle, uptimeMillis, pid, tid, pid == Process.myPid() ? SYSTEM_SERVER : ExtraActivityManagerService.getProcessNameByPid(pid), ExtraActivityManagerService.getPackageNameByPid(pid));
        }
    }

    public void finishMatchingScenario(E2EScenario scenario, String tag, E2EScenarioPayload payload, int tid, long uptimeMillis) throws RemoteException {
        long uptimeMillis2;
        if (this.mPerfService != null) {
            if (uptimeMillis == 0) {
                uptimeMillis2 = SystemClock.uptimeMillis();
            } else {
                uptimeMillis2 = uptimeMillis;
            }
            int pid = Binder.getCallingPid();
            this.mPerfService.finishMatchingScenario(scenario, tag, payload, uptimeMillis2, pid, tid, pid == Process.myPid() ? SYSTEM_SERVER : ExtraActivityManagerService.getProcessNameByPid(pid), ExtraActivityManagerService.getPackageNameByPid(pid));
        }
    }

    public void finishSpecificScenario(Bundle scenarioBundle, E2EScenarioPayload payload, int tid, long uptimeMillis) throws RemoteException {
        long uptimeMillis2;
        if (this.mPerfService != null) {
            if (uptimeMillis == 0) {
                uptimeMillis2 = SystemClock.uptimeMillis();
            } else {
                uptimeMillis2 = uptimeMillis;
            }
            int pid = Binder.getCallingPid();
            this.mPerfService.finishSpecificScenario(scenarioBundle, payload, uptimeMillis2, pid, tid, pid == Process.myPid() ? SYSTEM_SERVER : ExtraActivityManagerService.getProcessNameByPid(pid), ExtraActivityManagerService.getPackageNameByPid(pid));
        }
    }

    public void reportNotificationClick(String postPackage, Intent intent, long uptimeMillis) throws RemoteException {
        if (this.mPerfService != null) {
            if (uptimeMillis == 0) {
                uptimeMillis = SystemClock.uptimeMillis();
            }
            this.mPerfService.reportNotificationClick(postPackage, intent, uptimeMillis);
        }
    }

    public boolean insertFilterInfo(String packageName, String defaultLabel, Uri iconUri, List<Bundle> filterInfos) {
        if (PermissionChecker.check(this.mContext)) {
            return FilterInfoInjector.getInstance().insertFilterInfo(packageName, defaultLabel, iconUri, filterInfos);
        }
        Slog.e("CallingPkgHook", "Check permission failed when insertFilterInfo.");
        return false;
    }

    public boolean deleteFilterInfo(String packageName) {
        if (PermissionChecker.check(this.mContext)) {
            return FilterInfoInjector.getInstance().deleteFilterInfo(packageName);
        }
        Slog.e("CallingPkgHook", "Check permission failed when deleteFilterInfo.");
        return false;
    }

    public List<QuickAppResolveInfo> resolveQuickAppInfos(Intent targetIntent) {
        return FilterInfoInjector.getInstance().resolveAppInfos(this.mContext, targetIntent);
    }

    public void setHapLinks(Map data, ActivityInfo activityInfo) throws RemoteException {
        if (!PermissionChecker.check(this.mContext)) {
            Slog.e("CallingPkgHook", "Check permission failed when setHapLinks.");
        } else {
            HapLinksInjector.setData(data, activityInfo);
        }
    }

    public void reserveMemory() {
        this.mSystemPressureController.reserveMemory();
    }
}
