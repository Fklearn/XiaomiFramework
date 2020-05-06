package com.android.server.am;

import android.os.RemoteException;
import com.android.server.pm.PackageManagerService;

public class ProcessKiller {
    private static final String TAG = "ProcessManager";
    private ActivityManagerService mActivityManagerService;

    public ProcessKiller(ActivityManagerService ams) {
        this.mActivityManagerService = ams;
    }

    private boolean isInterestingToUser(ProcessRecord app) {
        return app.getWindowProcessController().isInterestingToUser();
    }

    public void forceStopPackage(ProcessRecord app, String reason, boolean evenForeground) {
        if (evenForeground || !isInterestingToUser(app)) {
            forceStopPackage(app.info.packageName, app.userId, reason);
        }
    }

    public void killApplication(ProcessRecord app, String reason, boolean evenForeground) {
        if (evenForeground || !isInterestingToUser(app)) {
            kill(app, reason);
        }
    }

    public void killBackgroundApplication(ProcessRecord app, String reason) {
        this.mActivityManagerService.killBackgroundProcesses(app.info.packageName, app.userId, reason);
    }

    public void trimMemory(ProcessRecord app, boolean evenForeground) {
        if (!evenForeground && isInterestingToUser(app)) {
            return;
        }
        if (app.info.packageName.equals(PackageManagerService.PLATFORM_PACKAGE_NAME)) {
            scheduleTrimMemory(app, 60);
        } else {
            scheduleTrimMemory(app, 80);
        }
    }

    private void scheduleTrimMemory(ProcessRecord app, int level) {
        if (app != null && app.thread != null) {
            try {
                app.thread.scheduleTrimMemory(level);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    private void kill(ProcessRecord app, String reason) {
        if (app != null) {
            app.kill(reason, true);
        }
    }

    private void forceStopPackage(String packageName, int userId, String reason) {
        this.mActivityManagerService.forceStopPackage(packageName, userId, reason);
    }
}
