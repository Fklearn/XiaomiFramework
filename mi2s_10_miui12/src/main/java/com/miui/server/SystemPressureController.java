package com.miui.server;

import android.content.Context;
import android.os.Debug;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.spc.MemoryCleanInfo;
import android.os.spc.PSIEvent;
import android.os.spc.PSIEventSocketReader;
import android.os.spc.PSIMonitorSetting;
import android.os.spc.PressureState;
import android.os.spc.PressureStateSettings;
import android.util.EventLog;
import android.util.Log;
import android.util.Slog;
import com.android.internal.app.IPerfShielder;
import com.android.server.am.ActivityManagerService;
import com.android.server.am.AppUsageStatsManager;
import com.android.server.am.ProcessMemoryCleaner;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.List;
import miui.mqsas.IMQSNative;
import miui.util.ReflectionUtils;

public final class SystemPressureController {
    private static final String MQSASD_NAME = "miui.mqsas.IMQSNative";
    private static final int MSG_CLEAN_UP_MEMLVL_PROCESS = 4;
    private static final int MSG_CLEAR_MEMFULL_PSI_EVENTS = 3;
    private static final int MSG_MEMORY_RECLAIM_ALL = 1;
    private static final int MSG_MEMORY_RECLAIM_ONLY_FILE_CACHE = 2;
    private static final int MSG_RESERVE_MEMORY = 5;
    public static final String NAME = "SystemPressureControl";
    public static final int PROC_CLEAN_CODE = 30200;
    public static final int PROC_CLEAN_MEMINFO_CODE = 30199;
    public static final int PROC_RESERVE_MEMORY_CODE = 30201;
    public static final String TAG = "SystemPressureControl";
    private long lastProcessCleanTimeMillis;
    /* access modifiers changed from: private */
    public long lastReclaimAllTimeMillis;
    /* access modifiers changed from: private */
    public ActivityManagerService mAms = ((ActivityManagerService) ServiceManager.getService("activity"));
    /* access modifiers changed from: private */
    public H mHandler;
    private HandlerThread mHandlerTh = new HandlerThread("SystemPressureControlTh");
    private PressureState.PressureListener mMemFullPressureListener = new PressureState.PressureListener() {
        public void onStateChanged(PressureState.State curState, PressureState.State lastState) {
            if (PSIMonitorSetting.DEBUG) {
                Log.d("SystemPressureControl", String.format("current full memory pressure state:%s", new Object[]{curState}));
            }
            if (curState != PressureState.State.NON_PRESSURE) {
                SystemPressureController.this.mHandler.sendEmptyMessage(1);
            }
        }
    };
    private final IMQSNative mMqsNative = IMQSNative.Stub.asInterface(ServiceManager.getService(MQSASD_NAME));
    private PSIEventSocketReader.PSIEventListener mPSIEventListener = new PSIEventSocketReader.PSIEventListener() {
        public void onReceive(PSIEvent ev) {
            if (PSIMonitorSetting.DEBUG && (ev.type == 0 || ev.type == 1)) {
                Log.d("SystemPressureControl", ev.toString());
            }
            if (ev.type == 0) {
                if (ev.growthNs >= PressureStateSettings.MEM_SOME_PRESSURE_THRESHOLD1_MS * 1000000) {
                    SystemPressureController.this.mHandler.sendEmptyMessage(1);
                }
            } else if (ev.type == 1) {
                SystemPressureController.this.mHandler.removeMessages(3);
                PressureStateSettings.getPressureState(1).addPSIEvent(ev);
                SystemPressureController.this.mHandler.sendEmptyMessageDelayed(3, PressureStateSettings.MEM_PRESSURE_WINDOW_NS / 1000000);
                if (ev.growthNs >= PressureStateSettings.TRIGGER_PROC_CLEAN_PSI_MEM_FULL_MS) {
                    SystemPressureController.this.mHandler.sendEmptyMessage(4);
                }
            }
        }
    };
    private PSIEventSocketReader mPSIReader;
    private IPerfShielder mPerfShielder;
    private int mProcCleanIntervalScale = 1;
    ProcessMemoryCleaner mProcessCleaner;
    /* access modifiers changed from: private */
    public Method mReclaimMemMethod;

    private class H extends Handler {
        public H(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            boolean z = true;
            if (i != 1) {
                if (i != 2) {
                    if (i == 3) {
                        PressureStateSettings.getPressureState(1).clearPSIEvents();
                    } else if (i != 4) {
                        if (i == 5) {
                            if (PressureStateSettings.getProcessCleanerSetting(PressureStateSettings.ProcCleanerSettingFlags.ENABLED_MEMORY_RESERVE) == 0) {
                                z = false;
                            }
                            boolean isEnabledReserveMemory = z;
                            if (PressureStateSettings.PROCESS_CLEANER_ENABLED && isEnabledReserveMemory) {
                                SystemPressureController.this.handleReserveMemory(false);
                            }
                        }
                    } else if (PressureStateSettings.PROCESS_CLEANER_ENABLED) {
                        SystemPressureController.this.handleCleanUpMemory(false);
                    }
                } else if (SystemPressureController.this.mAms != null) {
                    boolean z2 = PressureStateSettings.MEM_PRESSURE_CONTROL_ENABLED;
                }
            } else if (SystemPressureController.this.mAms != null && PressureStateSettings.MEM_PRESSURE_CONTROL_ENABLED) {
                long currTimeMillis = SystemClock.elapsedRealtime();
                if (currTimeMillis - SystemPressureController.this.lastReclaimAllTimeMillis > PressureStateSettings.RECLAIM_MIN_INTERVAL) {
                    try {
                        if (SystemPressureController.this.mReclaimMemMethod != null) {
                            SystemPressureController.this.mReclaimMemMethod.invoke(SystemPressureController.this.mAms, new Object[]{2});
                        }
                    } catch (Exception e) {
                        Log.e("SystemPressureControl", "invoke mReclaimMemMethod failed");
                    }
                    long unused = SystemPressureController.this.lastReclaimAllTimeMillis = SystemClock.elapsedRealtime();
                    return;
                }
                Log.w("SystemPressureControl", String.format("Reclaimed all in %s ms, lass than %s ignore this action.", new Object[]{Long.valueOf(currTimeMillis - SystemPressureController.this.lastReclaimAllTimeMillis), Long.valueOf(PressureStateSettings.RECLAIM_MIN_INTERVAL)}));
            }
        }
    }

    /* access modifiers changed from: protected */
    public void dump(FileDescriptor fd, PrintWriter pw, List<String> args) {
        try {
            if (args.contains("action=reclaim_all")) {
                if (this.mReclaimMemMethod != null) {
                    this.mReclaimMemMethod.invoke(this.mAms, new Object[]{2});
                }
            } else if (args.contains("action=reclaim_file")) {
                if (this.mReclaimMemMethod != null) {
                    this.mReclaimMemMethod.invoke(this.mAms, new Object[]{1});
                }
            } else if (args.contains("clean")) {
                handleCleanUpMemory(true);
            } else if (args.contains("reserve")) {
                handleReserveMemory(true);
            } else if (args.contains("configs")) {
                dumpConfigs(fd, pw, args);
            } else if (args.contains("appusage")) {
                AppUsageStatsManager.getInstance().dumpAppUsage(pw);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* access modifiers changed from: protected */
    public void dumpConfigs(FileDescriptor fd, PrintWriter pw, List<String> list) {
        pw.println("DEBUG_ALL=" + PressureStateSettings.DEBUG_ALL);
        pw.println("-----------start of proc reclaim configs-----------");
        pw.println("MEM_PRESSURE_CONTROL_ENABLED = " + PressureStateSettings.MEM_PRESSURE_CONTROL_ENABLED);
        pw.println("MEM_PRESSURE_WINDOW_NS = " + PressureStateSettings.MEM_PRESSURE_WINDOW_NS);
        pw.println("MEM_SOME_PRESSURE_THRESHOLD1_MS = " + PressureStateSettings.MEM_SOME_PRESSURE_THRESHOLD1_MS);
        pw.println("MEM_FULL_PRESSURE_THRESHOLD1_MS = " + PressureStateSettings.MEM_FULL_PRESSURE_THRESHOLD1_MS);
        pw.println("RECLAIM_MIN_INTERVAL = " + PressureStateSettings.RECLAIM_MIN_INTERVAL);
        pw.println("SWAP_USAGE_RATE_LIMIT = " + PressureStateSettings.SWAP_USAGE_RATE_LIMIT);
        pw.println("-----------end of proc reclaim configs-----------\n");
        pw.println("-----------start of proc cleaner configs-----------");
        pw.println("PROCESS_CLEANER_ENABLED = " + PressureStateSettings.PROCESS_CLEANER_ENABLED);
        pw.println("TRIGGER_PROC_CLEAN_PSI_MEM_FULL_MS = " + PressureStateSettings.TRIGGER_PROC_CLEAN_PSI_MEM_FULL_MS);
        pw.println("MEMORY_AVAILABLE_THRESHOLD_KB = " + PressureStateSettings.MEMORY_AVAILABLE_THRESHOLD_KB);
        pw.println("PROC_MEM_LVL1_PSS_LIMIT_KB = " + PressureStateSettings.PROC_MEM_LVL1_PSS_LIMIT_KB);
        pw.println("PROC_MEM_LVL2_PSS_LIMIT_KB = " + PressureStateSettings.PROC_MEM_LVL2_PSS_LIMIT_KB);
        pw.println("PROC_CLEAN_MIN_INTERVAL_MS = " + PressureStateSettings.PROC_CLEAN_MIN_INTERVAL_MS);
        pw.println("INTERESTED_PROC_MIN_PSS_KB = " + PressureStateSettings.INTERESTED_PROC_MIN_PSS_KB);
        pw.println("RECENT_STARTED_APP_THRESHOLD_MS = 600000");
        pw.println("RECENT_FOREGROUND_APP_TIME_MILLIS = " + PressureStateSettings.RECENT_FOREGROUND_APP_TIME_MILLIS);
        pw.println("DISTANT_APP_TIME_TIME_MILLIS = " + PressureStateSettings.DISTANT_APP_TIME_TIME_MILLIS);
        pw.println("RESERVE_MEMORY_MIN_AVAILABLE_KB = " + PressureStateSettings.RESERVE_MEMORY_MIN_AVAILABLE_KB);
        pw.println("RESERVE_MEMORY_AMOUNT_KB = " + PressureStateSettings.RESERVE_MEMORY_AMOUNT_KB);
        pw.println("PROCESS_CLEANER_SETTINGS = " + PressureStateSettings.PROCESS_CLEANER_SETTINGS);
        pw.println("ENABLED_SPEED_TEST_PROTECT = " + PressureStateSettings.getProcessCleanerSetting(PressureStateSettings.ProcCleanerSettingFlags.ENABLED_SPEED_TEST_PROTECT));
        pw.println("ENABLED_MEMORY_RESERVE = " + PressureStateSettings.getProcessCleanerSetting(PressureStateSettings.ProcCleanerSettingFlags.ENABLED_MEMORY_RESERVE));
        pw.println("-----------end of proc cleaner configs-----------");
    }

    public SystemPressureController(Context context) {
        this.mHandlerTh.start();
        this.mHandler = new H(this.mHandlerTh.getLooper());
        PressureStateSettings.getPressureState(1).setListener(this.mMemFullPressureListener);
        ActivityManagerService activityManagerService = this.mAms;
        if (activityManagerService != null) {
            this.mProcessCleaner = new ProcessMemoryCleaner(activityManagerService);
        }
        Class<ActivityManagerService> cls = ActivityManagerService.class;
        try {
            this.mReclaimMemMethod = ReflectionUtils.findMethodExact(cls, "reclaimProcessMem", new Class[]{Integer.TYPE});
        } catch (NoSuchMethodException e) {
            Log.e("SystemPressureControl", "not find method: mReclaimMemMethod");
        }
        PressureStateSettings.registerCloudObserver(context, this.mHandler);
        PressureStateSettings.updatePressureStateSettings(context);
        PressureStateSettings.MEM_PRESSURE_CONTROL_ENABLED = false;
        Log.d("SystemPressureControl", "init SystemPressureController");
    }

    public void start(Context context) {
        if (this.mMqsNative == null) {
            Slog.e("SystemPressureControl", "mqsasd not available!");
            return;
        }
        for (PSIMonitorSetting setting : PSIMonitorSetting.PSI_MONITORS) {
            try {
                this.mMqsNative.addPSITriggerCommand(setting.getMonitorId(), setting.getPSINode(), setting.generateMonitorCommand());
                if (PSIMonitorSetting.DEBUG) {
                    Log.d("SystemPressureControl", String.format("addPSITriggerCommand(%s, %s, %s)", new Object[]{Integer.valueOf(setting.getMonitorId()), setting.getPSINode(), setting.generateMonitorCommand()}));
                }
            } catch (RemoteException ex) {
                Slog.e("SystemPressureControl", "Add PSI monitor failed!!!", ex);
            }
        }
        try {
            ParcelFileDescriptor psiSockFd = this.mMqsNative.connectPSIMonitor("SystemPressureController");
            if (psiSockFd != null) {
                this.mPSIReader = new PSIEventSocketReader(psiSockFd, this.mPSIEventListener);
                this.mPSIReader.startMonitor();
            }
        } catch (RemoteException e) {
            Slog.e("SystemPressureControl", "connect to SystemPressureController failed");
        }
        this.mPerfShielder = IPerfShielder.Stub.asInterface(ServiceManager.getService(PerfShielderService.SERVICE_NAME));
        ProcessMemoryCleaner processMemoryCleaner = this.mProcessCleaner;
        if (processMemoryCleaner != null) {
            processMemoryCleaner.start(context);
        }
    }

    /* access modifiers changed from: private */
    public void handleCleanUpMemory(boolean force) {
        if (this.mProcessCleaner != null) {
            if (force || SystemClock.elapsedRealtime() - this.lastProcessCleanTimeMillis > PressureStateSettings.PROC_CLEAN_MIN_INTERVAL_MS * ((long) this.mProcCleanIntervalScale)) {
                this.lastProcessCleanTimeMillis = SystemClock.elapsedRealtime();
                long[] meminfo = new long[15];
                Debug.getMemInfo(meminfo);
                long memAvailable = meminfo[3] + meminfo[1];
                MemoryCleanInfo cInfo = new MemoryCleanInfo();
                cInfo.time = this.lastProcessCleanTimeMillis;
                cInfo.beforeMemFree = meminfo[1];
                cInfo.beforeMemAvail = memAvailable;
                cInfo.memTotal = meminfo[0];
                cInfo.swapTotal = meminfo[8];
                cInfo.reason = "psi memory pressure";
                if (force || (memAvailable != 0 && memAvailable <= PressureStateSettings.MEMORY_AVAILABLE_THRESHOLD_KB)) {
                    long needMemSize = PressureStateSettings.MEMORY_AVAILABLE_THRESHOLD_KB - memAvailable;
                    cInfo.neededMemory = needMemSize;
                    if (needMemSize > 0) {
                        this.mProcessCleaner.scanProcessAndCleanUpMemory(needMemSize, cInfo, -1);
                        Debug.getMemInfo(meminfo);
                        cInfo.afterMemFree = meminfo[1];
                        cInfo.afterMemAvail = meminfo[3] + meminfo[1];
                        try {
                            if (ProcessMemoryCleaner.DEBUG) {
                                Slog.d(ProcessMemoryCleaner.TAG, "memory clean info: " + cInfo.toString());
                            }
                            EventLog.writeEvent(PROC_CLEAN_MEMINFO_CODE, new Object[]{Long.valueOf(cInfo.memTotal), Long.valueOf(cInfo.swapTotal), Long.valueOf(cInfo.beforeMemFree), Long.valueOf(cInfo.beforeMemAvail), Long.valueOf(cInfo.afterMemFree), Long.valueOf(cInfo.afterMemAvail), Long.valueOf(cInfo.neededMemory), cInfo.reason});
                            this.mPerfShielder.reportProcessCleanEvent(cInfo.toBundle());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    this.mProcCleanIntervalScale = 1;
                    return;
                }
                this.mProcCleanIntervalScale = Math.min(5, this.mProcCleanIntervalScale + 1);
                Slog.w("SystemPressureControl", String.format("Increase process clean interval: %s", new Object[]{Long.valueOf(PressureStateSettings.PROC_CLEAN_MIN_INTERVAL_MS * ((long) this.mProcCleanIntervalScale))}));
            }
        }
    }

    public void reserveMemory() {
        this.mHandler.sendEmptyMessage(5);
    }

    /* access modifiers changed from: private */
    public void handleReserveMemory(boolean force) {
        if (this.mProcessCleaner != null) {
            if (force || SystemClock.elapsedRealtime() - this.lastProcessCleanTimeMillis > PressureStateSettings.PROC_CLEAN_MIN_INTERVAL_MS) {
                this.lastProcessCleanTimeMillis = SystemClock.elapsedRealtime();
                long[] meminfo = new long[15];
                Debug.getMemInfo(meminfo);
                long memAvail = meminfo[3] + meminfo[1];
                if (force || (memAvail != 0 && memAvail < PressureStateSettings.RESERVE_MEMORY_MIN_AVAILABLE_KB)) {
                    long needMemory = PressureStateSettings.RESERVE_MEMORY_AMOUNT_KB;
                    MemoryCleanInfo cInfo = new MemoryCleanInfo();
                    cInfo.time = this.lastProcessCleanTimeMillis;
                    cInfo.beforeMemFree = meminfo[1];
                    cInfo.beforeMemAvail = meminfo[3] + meminfo[1];
                    cInfo.memTotal = meminfo[0];
                    cInfo.swapTotal = meminfo[8];
                    cInfo.reason = "reserve memory";
                    cInfo.neededMemory = needMemory;
                    this.mProcessCleaner.scanProcessAndCleanUpMemory(needMemory, cInfo, 1);
                    Debug.getMemInfo(meminfo);
                    cInfo.afterMemFree = meminfo[1];
                    cInfo.afterMemAvail = meminfo[3] + meminfo[1];
                    try {
                        if (ProcessMemoryCleaner.DEBUG) {
                            Slog.d(ProcessMemoryCleaner.TAG, "memory clean info: " + cInfo.toString());
                        }
                        EventLog.writeEvent(PROC_RESERVE_MEMORY_CODE, new Object[]{Long.valueOf(cInfo.memTotal), Long.valueOf(cInfo.swapTotal), Long.valueOf(cInfo.beforeMemFree), Long.valueOf(cInfo.beforeMemAvail), Long.valueOf(cInfo.afterMemFree), Long.valueOf(cInfo.afterMemAvail), Long.valueOf(cInfo.neededMemory), cInfo.reason});
                        this.mPerfShielder.reportProcessCleanEvent(cInfo.toBundle());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
