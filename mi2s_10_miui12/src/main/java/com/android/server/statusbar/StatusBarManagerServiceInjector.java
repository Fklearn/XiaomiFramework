package com.android.server.statusbar;

import android.os.Binder;
import android.os.MiuiProcess;
import android.os.Process;
import android.util.Log;
import android.util.Slog;
import com.android.server.am.ExtraActivityManagerService;

public class StatusBarManagerServiceInjector {
    private static final int BOOST_PRIORITY = -10;
    private static final String SYSTEMUI_BOOST_TAG = "systemui_boost";
    private static StatusBarManagerServiceInjector sInjector = new StatusBarManagerServiceInjector();
    private int mOldRenderPriority;
    private int mOldUIPriority;

    private StatusBarManagerServiceInjector() {
    }

    public static StatusBarManagerServiceInjector getInstance() {
        return sInjector;
    }

    public void boostSystemUI(boolean isVisible) {
        int pid = Binder.getCallingPid();
        Log.d(SYSTEMUI_BOOST_TAG, "notification panel visible=" + isVisible);
        int tid = ExtraActivityManagerService.getRenderThreadTidByPid(pid);
        Log.d(SYSTEMUI_BOOST_TAG, "ui thread tid=" + pid);
        Log.d(SYSTEMUI_BOOST_TAG, "render thread tid=" + tid);
        if (tid == 0) {
            Slog.e(SYSTEMUI_BOOST_TAG, "render-thread tid = 0, do not boost");
        } else if (isVisible) {
            try {
                this.mOldUIPriority = Process.getThreadPriority(pid);
                Log.d(SYSTEMUI_BOOST_TAG, "ui thread old priority=" + this.mOldUIPriority);
                try {
                    this.mOldRenderPriority = Process.getThreadPriority(tid);
                    Log.d(SYSTEMUI_BOOST_TAG, "render thread old priority=" + this.mOldRenderPriority);
                    MiuiProcess.setThreadPriority(pid, -10, SYSTEMUI_BOOST_TAG);
                    MiuiProcess.setThreadPriority(tid, -10, SYSTEMUI_BOOST_TAG);
                    Log.d(SYSTEMUI_BOOST_TAG, "ui thread and render thread are boosted");
                } catch (IllegalArgumentException e) {
                    Log.e(SYSTEMUI_BOOST_TAG, "render thread tid=" + tid + ", does not exist");
                }
            } catch (IllegalArgumentException e2) {
                Log.e(SYSTEMUI_BOOST_TAG, "ui thread tid=" + pid + ", does not exist");
            }
        } else {
            MiuiProcess.setThreadPriority(pid, this.mOldUIPriority, SYSTEMUI_BOOST_TAG);
            MiuiProcess.setThreadPriority(tid, this.mOldRenderPriority, SYSTEMUI_BOOST_TAG);
            Log.d(SYSTEMUI_BOOST_TAG, "ui thread and render thread are reset");
        }
    }
}
