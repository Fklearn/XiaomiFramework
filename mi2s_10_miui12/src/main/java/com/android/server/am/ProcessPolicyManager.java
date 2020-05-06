package com.android.server.am;

import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.Slog;
import miui.process.ProcessManager;

public class ProcessPolicyManager {
    private static final int DEATH_COUNT_LIMIT = SystemProperties.getInt("persist.am.death_limit", 3);
    private static final boolean ENABLE = SystemProperties.getBoolean("persist.am.enable_ppm", true);
    private static final boolean ENABLE_PROMOTE_SUBPROCESS = SystemProperties.getBoolean("persist.am.enable_promote_sub", false);
    private static final String TAG = "ProcessPolicyManager";
    private static volatile ProcessManagerService sPmInstance;

    private static ProcessManagerService getProcessManagerService() {
        if (sPmInstance == null) {
            sPmInstance = (ProcessManagerService) ServiceManager.getService("ProcessManager");
        }
        return sPmInstance;
    }

    public static boolean isDelayBootPersistentApp(String packageName) {
        return false;
    }

    public static boolean isNeedTraceProcess(ProcessRecord app) {
        return ENABLE ? getProcessManagerService().getProcessPolicy().getWhiteList(128).contains(app.processName) : app.isPersistent();
    }

    public static boolean isSecureProtectedProcess(String packageName) {
        return false;
    }

    public static boolean isImportantProcess(String packageName, int userId) {
        return false;
    }

    public static void promoteImportantProcState(ProcessRecord app) {
    }

    public static void promoteImportantProcAdj(ProcessRecord app) {
        if (app.maxAdj > 0) {
            if (isLockedProcess(app.processName, app.userId) || isSecretlyProtectProcess(app.processName)) {
                if (app.maxAdj > ProcessManager.LOCKED_MAX_ADJ) {
                    app.maxAdj = ProcessManager.LOCKED_MAX_ADJ;
                }
                if (app.maxProcState > 14) {
                    app.maxProcState = 14;
                }
                Slog.d(TAG, "promote " + app.processName + " maxAdj to " + ProcessList.makeOomAdjString(app.maxAdj, false) + ", maxProcState to + " + ProcessList.makeProcStateString(app.maxProcState));
            } else if (ENABLE_PROMOTE_SUBPROCESS && isSecretlyProtectProcess(app.info.packageName) && sPmInstance.isDeathCountExceedingLimit(app.processName, app.userId, DEATH_COUNT_LIMIT)) {
                if (app.maxAdj > ProcessManager.SECRETLY_SUBPROCESS_MAX_ADJ) {
                    app.maxAdj = ProcessManager.SECRETLY_SUBPROCESS_MAX_ADJ;
                }
                if (app.maxProcState > 16) {
                    app.maxProcState = 16;
                }
                Slog.d(TAG, "promote " + app.processName + " maxAdj to " + ProcessList.makeOomAdjString(app.maxAdj, false) + ", maxProcState to + " + ProcessList.makeProcStateString(app.maxProcState));
            }
        }
    }

    private static boolean isLockedProcess(String processName, int userId) {
        try {
            return ENABLE && getProcessManagerService().isLockedApplication(processName, userId);
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean isSecretlyProtectProcess(String processName) {
        return ENABLE && getProcessManagerService().getProcessPolicy().isInSecretlyProtectList(processName);
    }

    public static boolean isIgnoreException(Thread t, Throwable e) {
        return false;
    }
}
