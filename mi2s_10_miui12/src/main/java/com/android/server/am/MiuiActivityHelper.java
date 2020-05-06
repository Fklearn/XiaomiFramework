package com.android.server.am;

import android.app.ActivityManagerNative;
import android.os.Process;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class MiuiActivityHelper {
    static ActivityManagerService sAms = null;
    private static long sTotalMem = Process.getTotalMemory();

    private static native long getNativeCachedLostMemory();

    private static native long getNativeFreeMemory();

    public MiuiActivityHelper() {
        System.loadLibrary("miui_security");
    }

    private static long getCachePss() {
        ArrayList<ProcessRecord> procs;
        if (sAms == null) {
            ActivityManagerService activityManagerService = ActivityManagerNative.getDefault();
            if (activityManagerService instanceof ActivityManagerService) {
                sAms = activityManagerService;
            }
        }
        long cachePss = 0;
        ActivityManagerService activityManagerService2 = sAms;
        if (!(activityManagerService2 == null || (procs = activityManagerService2.collectProcesses((PrintWriter) null, 0, false, (String[]) null)) == null)) {
            Iterator<ProcessRecord> it = procs.iterator();
            while (it.hasNext()) {
                ProcessRecord proc = it.next();
                if (proc.setAdj >= 900) {
                    cachePss += proc.lastPss;
                }
            }
        }
        return 1024 * cachePss;
    }

    public static long getCachedLostRam() {
        return getNativeCachedLostMemory();
    }

    public static long getFreeMemory() {
        long nativeFree = getNativeFreeMemory() + getCachedLostRam();
        long free = getCachePss() + nativeFree;
        if (free >= sTotalMem) {
            return nativeFree;
        }
        return free;
    }
}
