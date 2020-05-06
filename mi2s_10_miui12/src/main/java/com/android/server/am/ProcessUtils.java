package com.android.server.am;

import android.app.ActivityManagerNative;
import android.app.IWallpaperManager;
import android.app.WallpaperInfo;
import android.content.Context;
import android.os.Binder;
import android.os.Debug;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.speech.tts.TtsEngines;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;
import android.view.accessibility.AccessibilityManager;
import com.android.internal.telecom.ITelecomService;
import com.android.server.wm.WindowProcessController;
import com.android.server.wm.WindowProcessUtils;
import java.util.ArrayList;
import java.util.List;

public class ProcessUtils {
    public static final int FREEFORM_WORKSPACE_STACK_ID = 2;
    public static final int FULLSCREEN_WORKSPACE_STACK_ID = 1;
    private static final int LOW_MEMORY_RATE = 10;
    public static final Pair<Integer, Integer> PRIORITY_HEAVY = new Pair<>(400, 14);
    public static final Pair<Integer, Integer> PRIORITY_PERCEPTIBLE = new Pair<>(200, 5);
    public static final Pair<Integer, Integer> PRIORITY_UNKNOW = new Pair<>(1001, 21);
    public static final Pair<Integer, Integer> PRIORITY_VISIBLE = new Pair<>(100, 2);
    private static final String TAG = "ProcessManager";
    private static TtsEngines sTtsEngines;

    public static boolean isPhoneWorking() {
        boolean isWorking = true;
        if (SystemProperties.getBoolean("ro.radio.noril", false)) {
            return false;
        }
        long token = Binder.clearCallingIdentity();
        try {
            ITelecomService telecomService = ITelecomService.Stub.asInterface(ServiceManager.getService("telecom"));
            if (telecomService != null) {
                isWorking = telecomService.isInCall("system");
            }
        } catch (RemoteException e) {
            Log.w("ProcessManager", "RemoteException calling TelecomService isInCall().", e);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(token);
            throw th;
        }
        Binder.restoreCallingIdentity(token);
        return isWorking;
    }

    public static boolean isVoipWorking() {
        return true;
    }

    public static String getDefaultInputMethod(Context context) {
        int endIndex;
        String inputMethodId = Settings.Secure.getString(context.getContentResolver(), "default_input_method");
        if (TextUtils.isEmpty(inputMethodId) || (endIndex = inputMethodId.indexOf(47)) <= 0) {
            return null;
        }
        return inputMethodId.substring(0, endIndex);
    }

    public static String getActiveWallpaperPackage(Context context) {
        WallpaperInfo wInfo = null;
        try {
            wInfo = IWallpaperManager.Stub.asInterface(ServiceManager.getService("wallpaper")).getWallpaperInfo(-2);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        if (wInfo != null) {
            return wInfo.getPackageName();
        }
        return null;
    }

    public static String getActiveTtsEngine(Context context) {
        AccessibilityManager accessibilityManager = (AccessibilityManager) context.getSystemService("accessibility");
        if (accessibilityManager == null || !accessibilityManager.isEnabled() || !accessibilityManager.isTouchExplorationEnabled()) {
            return null;
        }
        if (sTtsEngines == null) {
            sTtsEngines = new TtsEngines(context);
        }
        return sTtsEngines.getDefaultEngine();
    }

    public static boolean isLowMemory() {
        return 10 * Process.getFreeMemory() < Process.getTotalMemory();
    }

    protected static boolean isHomeProcess(ProcessRecord pr) {
        WindowProcessController homeProcess = ActivityManagerNative.getDefault().mAtmInternal.getHomeProcess();
        return homeProcess != null && pr.getWindowProcessController() == homeProcess;
    }

    public static List<ProcessRecord> getProcessListByAdj(ActivityManagerService ams, int minOomAdj, List<String> whiteList) {
        ArrayList<ProcessRecord> procs = new ArrayList<>();
        synchronized (ams) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                int NP = ams.mProcessList.mProcessNames.getMap().size();
                for (int ip = 0; ip < NP; ip++) {
                    SparseArray<ProcessRecord> apps = (SparseArray) ams.mProcessList.mProcessNames.getMap().valueAt(ip);
                    int NA = apps.size();
                    for (int ia = 0; ia < NA; ia++) {
                        ProcessRecord app = apps.valueAt(ia);
                        if (!app.isPersistent()) {
                            if (whiteList == null || !whiteList.contains(app.processName)) {
                                if (app.removed) {
                                    procs.add(app);
                                } else if (app.setAdj >= minOomAdj) {
                                    procs.add(app);
                                }
                            }
                        }
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
        return procs;
    }

    public static int getProcTotalPss(int pid) {
        Debug.MemoryInfo info = new Debug.MemoryInfo();
        Debug.getMemoryInfo(pid, info);
        return info.getTotalPss();
    }

    public static int[] getPidsForProc(ActivityManagerService ams, List<ProcessRecord> procs) {
        int[] pids = null;
        synchronized (ams) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                if (procs != null && !procs.isEmpty()) {
                    int size = procs.size();
                    pids = new int[size];
                    for (int i = 0; i < size; i++) {
                        ProcessRecord app = procs.get(i);
                        if (!(app == null || app.pid == 0)) {
                            pids[i] = app.pid;
                        }
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
        return pids;
    }

    public static int getTotalPss(int[] pids) {
        if (pids == null || pids.length <= 0) {
            return 0;
        }
        int totalPss = 0;
        int i = 0;
        while (i < pids.length && pids[i] != 0) {
            totalPss += getProcTotalPss(pids[i]);
            i++;
        }
        return totalPss;
    }

    public static long getPackageLastPss(ActivityManagerService ams, ProcessManagerService pms, String packageName, int userId) {
        List<ProcessRecord> apps = pms.getProcessRecordList(packageName, userId);
        if (apps == null || apps.isEmpty()) {
            return 0;
        }
        long totalPss = 0;
        synchronized (ams) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                for (int i = 0; i < apps.size(); i++) {
                    ProcessRecord app = apps.get(i);
                    if (!(app == null || app.thread == null || app.killedByAm)) {
                        totalPss += apps.get(i).lastPss;
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
        return totalPss;
    }

    public static ProcessRecord getTaskTopApp(int taskId, ActivityManagerService ams) {
        return getProcessRecordByWPCtl(WindowProcessUtils.getTaskTopApp(taskId, ams.mActivityTaskManager), ams);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0018, code lost:
        return null;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static com.android.server.am.ProcessRecord getProcessRecordByWPCtl(com.android.server.wm.WindowProcessController r3, com.android.server.am.ActivityManagerService r4) {
        /*
            com.android.server.am.ActivityManagerService$PidMap r0 = r4.mPidsSelfLocked
            monitor-enter(r0)
            if (r3 == 0) goto L_0x0017
            com.android.server.am.ActivityManagerService$PidMap r1 = r4.mPidsSelfLocked     // Catch:{ all -> 0x001a }
            int r2 = r3.getPid()     // Catch:{ all -> 0x001a }
            com.android.server.am.ProcessRecord r1 = r1.get(r2)     // Catch:{ all -> 0x001a }
            com.android.server.wm.WindowProcessController r2 = r1.getWindowProcessController()     // Catch:{ all -> 0x001a }
            if (r2 != r3) goto L_0x0017
            monitor-exit(r0)     // Catch:{ all -> 0x001a }
            return r1
        L_0x0017:
            monitor-exit(r0)     // Catch:{ all -> 0x001a }
            r0 = 0
            return r0
        L_0x001a:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x001a }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ProcessUtils.getProcessRecordByWPCtl(com.android.server.wm.WindowProcessController, com.android.server.am.ActivityManagerService):com.android.server.am.ProcessRecord");
    }
}
