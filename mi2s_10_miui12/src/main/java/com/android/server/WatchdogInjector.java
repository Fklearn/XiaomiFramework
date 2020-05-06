package com.android.server;

import android.os.Debug;
import com.android.server.Watchdog;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import miui.mqsas.sdk.MQSEventManagerDelegate;
import miui.mqsas.sdk.event.WatchdogEvent;

class WatchdogInjector {
    private static final String SYSTEM_SERVER = "system_server";
    private static final String TAG = "WatchdogInjector";
    private static ArrayList<String> mMonitorThreads = new ArrayList<>();

    WatchdogInjector() {
    }

    static {
        mMonitorThreads.add("ActivityManager");
        mMonitorThreads.add("PowerManagerService");
        mMonitorThreads.add("miui.fg");
    }

    public static void onWatchdog(int type, int pid, String subject, File trace) {
        onWatchdog(type, pid, subject, trace, (List<Watchdog.HandlerChecker>) null);
    }

    public static void onWatchdog(int type, int pid, String subject, File trace, List<Watchdog.HandlerChecker> handlerCheckers) {
        if (!Debug.isDebuggerConnected()) {
            WatchdogEvent event = new WatchdogEvent();
            event.setType(type);
            event.setPid(pid);
            event.setProcessName(SYSTEM_SERVER);
            event.setPackageName(SYSTEM_SERVER);
            event.setTimeStamp(System.currentTimeMillis());
            event.setSystem(true);
            event.setSummary(subject);
            event.setDetails(subject);
            if (trace != null) {
                event.setLogName(trace.getAbsolutePath());
            }
            if (handlerCheckers != null) {
                StringBuilder details = new StringBuilder();
                for (int i = 0; i < handlerCheckers.size(); i++) {
                    for (StackTraceElement element : handlerCheckers.get(i).getThread().getStackTrace()) {
                        details.append("    at ");
                        details.append(element);
                        details.append("\n");
                    }
                    details.append("\n\n");
                }
                event.setDetails(details.toString());
            }
            MQSEventManagerDelegate.getInstance().reportWatchdogEvent(event);
        }
    }

    public static boolean needMonitorThread(String name) {
        if (mMonitorThreads.contains(name)) {
            return true;
        }
        return false;
    }
}
