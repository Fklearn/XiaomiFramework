package com.android.server.am;

import android.os.UEventObserver;
import android.util.Log;

public class LowMemoryKillerObserver extends UEventObserver {
    private static final boolean DEBUG = false;
    private static final String KEY_PID = "PID";
    private static final String LMK_KILL_REASON = "lowmemorykiller";
    private static final String LMK_UEVENT_MATCH = "DEVPATH=/devices/virtual/lowmemorykiller";
    private static final String TAG = "ProcessManager";
    private ProcessManagerService mProcessManagerService;

    public LowMemoryKillerObserver(ProcessManagerService pms) {
        this.mProcessManagerService = pms;
    }

    public void onUEvent(UEventObserver.UEvent event) {
        try {
            final int killedPid = Integer.valueOf(event.get(KEY_PID)).intValue();
            this.mProcessManagerService.mHandler.post(new Runnable() {
                public void run() {
                    LowMemoryKillerObserver.this.reportLmkKillEvent(killedPid);
                }
            });
        } catch (Exception e) {
            Log.d("ProcessManager", "exception on LowMemoryKillerObserver.onUEvent:" + e.toString());
        }
    }

    /* access modifiers changed from: private */
    public void reportLmkKillEvent(int killedPid) {
        ProcessRecord killedApp = this.mProcessManagerService.getProcessRecordByPid(killedPid);
        if (killedApp != null) {
            ProcessRecordInjector.reportKillProcessEvent(killedApp, LMK_KILL_REASON);
        } else {
            ProcessRecordInjector.checkNativeKillInList(killedPid, LMK_KILL_REASON);
        }
    }

    public void start() {
        startObserving(LMK_UEVENT_MATCH);
    }
}
