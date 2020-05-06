package com.android.server.am;

import android.app.ActivityManagerNative;
import android.app.ApplicationErrorReport;
import android.os.DropBoxManager;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.os.ProcessCpuTracker;
import com.android.server.EventLogTags;
import com.android.server.wm.ActivityRecord;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import miui.util.Log;

public class ANRManager {
    private static final String ANR_TYPE_BROADCAST = "broadcast";
    private static final String ANR_TYPE_INPUT = "input";
    private static final String ANR_TYPE_PROVIDER = "provider";
    private static final String ANR_TYPE_SERVICE = "service";
    static final int BROADCAST_TIMEOUT_HALF_MSG = 1012;
    static final int DEFAULT_DROPBOX_MAX_SIZE = 196608;
    public static final int DEFAULT_MAX_FILES = (ENABLE_HALF_ANR_STACK ? 5000 : 1000);
    public static final int DEFAULT_QUOTA_KB = (ENABLE_HALF_ANR_STACK ? EventLogTags.LOCKDOWN_VPN_CONNECTING : 5120);
    static final int DROPBOX_MAX_SIZE = (ENABLE_HALF_ANR_STACK ? 393216 : DEFAULT_DROPBOX_MAX_SIZE);
    static final boolean ENABLE_HALF_ANR_STACK = SystemProperties.getBoolean("persist.enable_anr_half", false);
    static final int INPUT_TIMEOUT_HALF_MSG = 1014;
    static final int PROVIDER_TIMEOUT_HALF_MSG = 1013;
    private static final String REASON_BROADCAST_ANR = "Broadcast of Intent";
    private static final String REASON_INPUT_ANR = "Input dispatching timed out";
    private static final String REASON_SERVICE_ANR = "executing service";
    static final int SERVICE_TIMEOUT_HALF_MSG = 1011;
    private static final String TAG = "ANRManager";
    private static ActivityManagerService sActivityManagerService;
    private static SimpleDateFormat sAnrFileDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
    private static volatile WorkHandler sHandler;

    static class WorkHandler extends Handler {
        public WorkHandler(Looper looper) {
            super(looper, (Handler.Callback) null, true);
        }

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case ANRManager.SERVICE_TIMEOUT_HALF_MSG /*1011*/:
                    ANRManager.onServiceTimeoutHalf((ProcessRecord) msg.obj);
                    return;
                case ANRManager.BROADCAST_TIMEOUT_HALF_MSG /*1012*/:
                    ANRManager.onBroadcastTimeoutHalf((BroadcastQueue) msg.obj);
                    return;
                case ANRManager.PROVIDER_TIMEOUT_HALF_MSG /*1013*/:
                    ANRManager.onProviderTimeoutHalf((ProcessRecord) msg.obj);
                    return;
                case ANRManager.INPUT_TIMEOUT_HALF_MSG /*1014*/:
                    ANRManager.onInputTimeoutHalf(msg.arg1);
                    return;
                default:
                    return;
            }
        }
    }

    static void checkInit(ActivityManagerService ams) {
        if (sHandler == null) {
            synchronized (ANRManager.class) {
                if (sHandler == null) {
                    sHandler = new WorkHandler(ams.mHandler.getLooper());
                    sActivityManagerService = ams;
                }
            }
        }
    }

    static void scheduleServiceTimeoutHalf(ActivityManagerService ams, long endTime, ProcessRecord app) {
        if (ENABLE_HALF_ANR_STACK) {
            checkInit(ams);
            Message msg = sHandler.obtainMessage(SERVICE_TIMEOUT_HALF_MSG);
            msg.obj = app;
            sHandler.sendMessageAtTime(msg, endTime);
        }
    }

    static void cancelScheduleServiceTimeoutHalf(ActivityManagerService ams, ProcessRecord app) {
        if (ENABLE_HALF_ANR_STACK) {
            checkInit(ams);
            sHandler.removeMessages(SERVICE_TIMEOUT_HALF_MSG, app);
        }
    }

    static void scheduleBroadcastTimeoutHalf(ActivityManagerService ams, long endTime, BroadcastQueue bq) {
        if (ENABLE_HALF_ANR_STACK) {
            checkInit(ams);
            Message msg = sHandler.obtainMessage(BROADCAST_TIMEOUT_HALF_MSG);
            msg.obj = bq;
            sHandler.sendMessageAtTime(msg, endTime);
        }
    }

    static void cancelScheduleBroadcastTimeoutHalf(ActivityManagerService ams, BroadcastQueue bq) {
        if (ENABLE_HALF_ANR_STACK) {
            checkInit(ams);
            sHandler.removeMessages(BROADCAST_TIMEOUT_HALF_MSG, bq);
        }
    }

    static void scheduleProviderTimeoutHalf(ActivityManagerService ams, long delay, ProcessRecord app) {
        if (ENABLE_HALF_ANR_STACK) {
            checkInit(ams);
            Message msg = sHandler.obtainMessage(PROVIDER_TIMEOUT_HALF_MSG);
            msg.obj = app;
            sHandler.sendMessageDelayed(msg, delay);
        }
    }

    static void cancelScheduleProviderTimeoutHalf(ActivityManagerService ams, ProcessRecord app) {
        if (ENABLE_HALF_ANR_STACK) {
            checkInit(ams);
            sHandler.removeMessages(PROVIDER_TIMEOUT_HALF_MSG, app);
        }
    }

    public static void scheduleInputTimeoutHalf(int pid) {
        if (ENABLE_HALF_ANR_STACK) {
            checkInit(ActivityManagerNative.getDefault());
            Message msg = sHandler.obtainMessage(INPUT_TIMEOUT_HALF_MSG);
            msg.arg1 = pid;
            sHandler.sendMessage(msg);
        }
    }

    static void onServiceTimeoutHalf(ProcessRecord app) {
        dumpStackTracesLite(app, ANR_TYPE_SERVICE);
    }

    static void onBroadcastTimeoutHalf(BroadcastQueue bq) {
        ProcessRecord app = null;
        synchronized (sActivityManagerService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                BroadcastRecord r = bq.mDispatcher.mOrderedBroadcasts.get(0);
                Object curReceiver = r.receivers.get(r.nextReceiver - 1);
                if (curReceiver instanceof BroadcastFilter) {
                    BroadcastFilter bf = (BroadcastFilter) curReceiver;
                    if (!(bf.receiverList.pid == 0 || bf.receiverList.pid == ActivityManagerService.MY_PID)) {
                        synchronized (sActivityManagerService.mPidsSelfLocked) {
                            app = sActivityManagerService.mPidsSelfLocked.get(bf.receiverList.pid);
                        }
                    }
                } else {
                    app = r.curApp;
                }
                if (app != null) {
                    if (app.pid != 0) {
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        dumpStackTracesLite(app, "broadcast");
                        return;
                    }
                }
                Slog.e(TAG, "app not exist while broadcast timeout half");
                ActivityManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    static void onProviderTimeoutHalf(ProcessRecord app) {
        dumpStackTracesLite(app, ANR_TYPE_PROVIDER);
    }

    static void onInputTimeoutHalf(int pid) {
        dumpStackTracesLite(pid, ANR_TYPE_INPUT);
    }

    static void onANR(ActivityManagerService ams, ProcessRecord process, ActivityRecord activity, ActivityRecord parent, String subject, String report, File logFile, ApplicationErrorReport.CrashInfo crashInfo, String headline) {
        if (ENABLE_HALF_ANR_STACK && !TextUtils.isEmpty(report)) {
            String type = "";
            if (report.startsWith(REASON_INPUT_ANR)) {
                type = ANR_TYPE_INPUT;
            } else if (report.startsWith(REASON_SERVICE_ANR)) {
                type = ANR_TYPE_SERVICE;
            } else if (report.startsWith(REASON_BROADCAST_ANR)) {
                type = "broadcast";
            }
            renameTraceFile(process.info.packageName, type, false);
        }
    }

    static File dumpStackTracesLite(ProcessRecord app, String type) {
        if (app == null || app.pid == 0) {
            return null;
        }
        return dumpStackTracesLite(app.pid, type);
    }

    static File dumpStackTracesLite(int pid, String type) {
        String packageName = ExtraActivityManagerService.getPackageNameByPid(pid);
        ArrayList<Integer> pids = new ArrayList<>(2);
        pids.add(Integer.valueOf(pid));
        if (Process.myPid() != pid) {
            pids.add(Integer.valueOf(Process.myPid()));
        }
        Log.d(TAG, "start dumpStackTracesLite, pids:" + pids);
        ActivityManagerService.dumpStackTraces(pids, (ProcessCpuTracker) null, (SparseArray<Boolean>) null, (ArrayList<Integer>) null);
        return renameTraceFile(packageName, type, true);
    }

    static File renameTraceFile(String packageName, String type, boolean half) {
        String newTracesPath;
        String formattedDate = sAnrFileDateFormat.format(new Date());
        String tracesPath = SystemProperties.get("dalvik.vm.stack-trace-file", (String) null);
        StringBuilder sb = new StringBuilder();
        sb.append("_");
        sb.append(packageName);
        sb.append("_");
        sb.append(type);
        sb.append("@");
        sb.append(formattedDate);
        sb.append(half ? ".half.txt" : ".txt");
        String tail = sb.toString();
        if (tracesPath == null || tracesPath.length() == 0) {
            return null;
        }
        File traceRenameFile = new File(tracesPath);
        int lpos = tracesPath.lastIndexOf(".");
        if (-1 != lpos) {
            newTracesPath = tracesPath.substring(0, lpos) + tail;
        } else {
            newTracesPath = tracesPath + tail;
        }
        File newFile = new File(newTracesPath);
        traceRenameFile.renameTo(newFile);
        return newFile;
    }

    static void addTextToDropBox(File dataFile, String packageName, String eventType) {
        String dropboxTag = packageName + "_half_anr_" + eventType;
        DropBoxManager dbox = (DropBoxManager) sActivityManagerService.mContext.getSystemService("dropbox");
        if (dbox != null && dbox.isTagEnabled(dropboxTag)) {
            final StringBuilder sb = new StringBuilder(1024);
            Log.d(TAG, "add stack to dropbox, tag:" + dropboxTag + ", type: " + eventType);
            final File file = dataFile;
            final DropBoxManager dropBoxManager = dbox;
            final String str = dropboxTag;
            new Thread("Half anr time dump: " + dropboxTag) {
                public void run() {
                    int maxDataFileSize = ANRManager.DROPBOX_MAX_SIZE - sb.length();
                    File file = file;
                    if (file != null && maxDataFileSize > 0) {
                        try {
                            sb.append(FileUtils.readTextFile(file, maxDataFileSize, "\n\n[[TRUNCATED]]"));
                        } catch (IOException e) {
                            Slog.e(ANRManager.TAG, "Error reading " + file, e);
                        }
                    }
                    dropBoxManager.addText(str, sb.toString());
                }
            }.start();
        }
    }

    static String getDropBoxTag(ProcessRecord process, String processName, String eventType) {
        if (ProcessPolicy.REASON_ANR.equals(eventType)) {
            return process.info.packageName + "_" + eventType;
        }
        return processClass(process) + "_" + eventType;
    }

    private static String processClass(ProcessRecord process) {
        if (process == null || process.pid == Process.myPid()) {
            return "system_server";
        }
        if ((process.info.flags & 1) != 0) {
            return "system_app";
        }
        return "data_app";
    }
}
