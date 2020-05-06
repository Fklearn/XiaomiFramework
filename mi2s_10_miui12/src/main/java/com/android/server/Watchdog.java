package com.android.server;

import android.app.ActivityThread;
import android.app.ApplicationErrorReport;
import android.app.IActivityController;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.biometrics.face.V1_0.IBiometricsFace;
import android.hardware.biometrics.fingerprint.V2_1.IBiometricsFingerprint;
import android.hardware.health.V2_0.IHealth;
import android.hardware.tetheroffload.control.V1_0.IOffloadControl;
import android.hidl.manager.V1_0.IServiceManager;
import android.os.Binder;
import android.os.Build;
import android.os.Debug;
import android.os.FileUtils;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.system.ErrnoException;
import android.system.Os;
import android.system.OsConstants;
import android.system.StructRlimit;
import android.util.EventLog;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import android.util.StatsLog;
import com.android.internal.os.ProcessCpuTracker;
import com.android.server.am.ActivityManagerService;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.wm.SurfaceAnimationThread;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import libcore.io.IoUtils;
import miui.mqsas.fdmonitor.FdInfoManager;

public class Watchdog extends Thread {
    public static final String[] APP_STACKS_OF_INTEREST = {"com.xiaomi.location.fused", "com.xiaomi.metoknlp", "com.android.commands.monkey"};
    static final long CHECK_INTERVAL = 30000;
    static final int COMPLETED = 0;
    static final boolean DB = false;
    public static final boolean DEBUG = true;
    static final long DEFAULT_TIMEOUT = 60000;
    public static final List<String> HAL_INTERFACES_OF_INTEREST = Arrays.asList(new String[]{"android.hardware.audio@2.0::IDevicesFactory", "android.hardware.audio@4.0::IDevicesFactory", "android.hardware.bluetooth@1.0::IBluetoothHci", "android.hardware.camera.provider@2.4::ICameraProvider", "android.hardware.graphics.allocator@2.0::IAllocator", "android.hardware.graphics.composer@2.1::IComposer", IHealth.kInterfaceName, "android.hardware.media.c2@1.0::IComponentStore", "android.hardware.media.omx@1.0::IOmx", "android.hardware.media.omx@1.0::IOmxStore", "android.hardware.sensors@1.0::ISensors", "android.hardware.vr@1.0::IVr", IBiometricsFingerprint.kInterfaceName, IOffloadControl.kInterfaceName, "android.hardware.keymaster@4.0::IKeymasterDevice", "android.hardware.keymaster@3.0::IKeymasterDevice", "android.system.suspend@1.0::ISystemSuspend", "vendor.xiaomi.hardware.displayfeature@1.0::IDisplayFeature", IBiometricsFace.kInterfaceName});
    public static final String[] NATIVE_STACKS_OF_INTEREST = {"/system/bin/audioserver", "/system/bin/cameraserver", "/system/bin/drmserver", "/system/bin/mediadrmserver", "/system/bin/mediaserver", "/system/bin/sdcard", "/system/bin/surfaceflinger", "/system/bin/vold", "/system/bin/netd", "media.extractor", "media.metrics", "media.codec", "media.swcodec", "com.android.bluetooth", "/system/bin/statsd", "zygote", "/system/bin/gatekeeperd", "/system/bin/keystore"};
    static final int OVERDUE = 3;
    static final String TAG = "Watchdog";
    static final int WAITED_HALF = 2;
    static final int WAITING = 1;
    static Watchdog sWatchdog;
    private static final boolean sWatchdogEnhanced = SystemProperties.getBoolean("persist.sys.watchdog_enhanced", false);
    ActivityManagerService mActivity;
    boolean mAllowRestart = true;
    IActivityController mController;
    /* access modifiers changed from: private */
    public final Object mDumpCompleteLock = new Object();
    /* access modifiers changed from: private */
    public Boolean mDumpCompleted = false;
    final ArrayList<HandlerChecker> mHandlerCheckers = new ArrayList<>();
    /* access modifiers changed from: private */
    public File mInitialStack = null;
    final HandlerChecker mMonitorChecker;
    final OpenFdMonitor mOpenFdMonitor;
    int mPhonePid;
    SimpleDateFormat mTraceDateFormat = new SimpleDateFormat("dd_MM_HH_mm_ss.SSS");
    private volatile WorkerHandler mWorkerHandler;
    private HandlerThread mWorkerThread;

    public interface Monitor {
        void monitor();
    }

    private final class WorkerHandler extends Handler {
        static final int WAITED_HALF_DUMP = 0;
        static final int WATCH_DOG_DUMP = 1;
        static final int WATCH_DOG_WITH_WAITED_HALF_DUMP = 2;

        public WorkerHandler(Looper looper) {
            super(looper, (Handler.Callback) null);
        }

        /* JADX WARNING: Code restructure failed: missing block: B:26:0x00b6, code lost:
            r0 = r4;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:35:?, code lost:
            return;
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void handleMessage(android.os.Message r20) {
            /*
                r19 = this;
                r1 = r19
                r2 = r20
                java.util.ArrayList r0 = new java.util.ArrayList
                r0.<init>()
                r3 = r0
                r0 = 0
                int r4 = r2.what
                if (r4 == 0) goto L_0x00bf
                r5 = 2
                r6 = 1
                if (r4 == r6) goto L_0x0021
                if (r4 == r5) goto L_0x001e
                java.lang.String r4 = "Watchdog"
                java.lang.String r5 = "    // wrong message received of WorkerHandler"
                android.util.Slog.w(r4, r5)
                goto L_0x00dc
            L_0x001e:
                r0 = 1
                r4 = r0
                goto L_0x0022
            L_0x0021:
                r4 = r0
            L_0x0022:
                java.lang.Object r0 = r2.obj
                r15 = r0
                java.lang.String r15 = (java.lang.String) r15
                int r0 = android.os.Process.myPid()
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                r3.add(r0)
                com.android.server.Watchdog.insertInterestedAppPids(r3)
                com.android.server.Watchdog r0 = com.android.server.Watchdog.this
                int r0 = r0.mPhonePid
                if (r0 <= 0) goto L_0x0046
                com.android.server.Watchdog r0 = com.android.server.Watchdog.this
                int r0 = r0.mPhonePid
                java.lang.Integer r0 = java.lang.Integer.valueOf(r0)
                r3.add(r0)
            L_0x0046:
                com.android.server.Watchdog r0 = com.android.server.Watchdog.this
                java.io.File r14 = r0.dumpTracesFile(r4, r3)
                com.android.server.Watchdog r0 = com.android.server.Watchdog.this
                com.android.server.am.ActivityManagerService r0 = r0.mActivity
                if (r0 == 0) goto L_0x006e
                com.android.server.Watchdog r0 = com.android.server.Watchdog.this
                com.android.server.am.ActivityManagerService r7 = r0.mActivity
                r9 = 0
                r11 = 0
                r12 = 0
                r13 = 0
                r0 = 0
                r17 = 0
                java.lang.String r8 = "watchdog"
                java.lang.String r10 = "system_server"
                r18 = r14
                r14 = r15
                r6 = r15
                r15 = r0
                r16 = r18
                r7.addErrorToDropBox(r8, r9, r10, r11, r12, r13, r14, r15, r16, r17)
                goto L_0x0071
            L_0x006e:
                r18 = r14
                r6 = r15
            L_0x0071:
                com.android.server.Watchdog r0 = com.android.server.Watchdog.this
                java.lang.Object r7 = r0.mDumpCompleteLock
                monitor-enter(r7)
                com.android.server.Watchdog r0 = com.android.server.Watchdog.this     // Catch:{ all -> 0x00b8 }
                java.lang.Boolean r0 = r0.mDumpCompleted     // Catch:{ all -> 0x00b8 }
                boolean r0 = r0.booleanValue()     // Catch:{ all -> 0x00b8 }
                if (r0 != 0) goto L_0x00b3
                java.lang.String r0 = "Watchdog"
                java.lang.String r8 = "Triggering SysRq for system_server watchdog"
                android.util.Slog.e(r0, r8)     // Catch:{ all -> 0x00b8 }
                com.android.server.Watchdog r0 = com.android.server.Watchdog.this     // Catch:{ all -> 0x00b8 }
                r8 = 119(0x77, float:1.67E-43)
                r0.doSysRq(r8)     // Catch:{ all -> 0x00b8 }
                com.android.server.Watchdog r0 = com.android.server.Watchdog.this     // Catch:{ all -> 0x00b8 }
                r8 = 108(0x6c, float:1.51E-43)
                r0.doSysRq(r8)     // Catch:{ all -> 0x00b8 }
                int r0 = android.os.Process.myPid()     // Catch:{ all -> 0x00b8 }
                com.android.server.Watchdog r8 = com.android.server.Watchdog.this     // Catch:{ all -> 0x00b8 }
                java.util.ArrayList r8 = r8.getBlockedCheckersLocked()     // Catch:{ all -> 0x00b8 }
                r9 = r18
                com.android.server.WatchdogInjector.onWatchdog(r5, r0, r6, r9, r8)     // Catch:{ all -> 0x00bd }
                com.android.server.Watchdog r0 = com.android.server.Watchdog.this     // Catch:{ all -> 0x00bd }
                r5 = 1
                java.lang.Boolean r5 = java.lang.Boolean.valueOf(r5)     // Catch:{ all -> 0x00bd }
                java.lang.Boolean unused = r0.mDumpCompleted = r5     // Catch:{ all -> 0x00bd }
                goto L_0x00b5
            L_0x00b3:
                r9 = r18
            L_0x00b5:
                monitor-exit(r7)     // Catch:{ all -> 0x00bd }
                r0 = r4
                goto L_0x00dc
            L_0x00b8:
                r0 = move-exception
                r9 = r18
            L_0x00bb:
                monitor-exit(r7)     // Catch:{ all -> 0x00bd }
                throw r0
            L_0x00bd:
                r0 = move-exception
                goto L_0x00bb
            L_0x00bf:
                int r4 = android.os.Process.myPid()
                java.lang.Integer r4 = java.lang.Integer.valueOf(r4)
                r3.add(r4)
                com.android.server.Watchdog.insertInterestedAppPids(r3)
                com.android.server.Watchdog r4 = com.android.server.Watchdog.this
                java.util.ArrayList r5 = com.android.server.Watchdog.getInterestingNativePids()
                r6 = 0
                java.io.File r5 = com.android.server.am.ActivityManagerService.dumpStackTraces((java.util.ArrayList<java.lang.Integer>) r3, (com.android.internal.os.ProcessCpuTracker) r6, (android.util.SparseArray<java.lang.Boolean>) r6, (java.util.ArrayList<java.lang.Integer>) r5)
                java.io.File unused = r4.mInitialStack = r5
            L_0x00dc:
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.android.server.Watchdog.WorkerHandler.handleMessage(android.os.Message):void");
        }
    }

    /* access modifiers changed from: private */
    public static void insertInterestedAppPids(ArrayList<Integer> outPids) {
        int[] interestedPids = Process.getPidsForCommands(APP_STACKS_OF_INTEREST);
        if (interestedPids != null && interestedPids.length > 0) {
            for (int pid : interestedPids) {
                if (!outPids.contains(Integer.valueOf(pid))) {
                    outPids.add(Integer.valueOf(pid));
                }
            }
        }
    }

    private void ensureWorkerHandlerReady() {
        if (this.mWorkerHandler == null) {
            this.mWorkerThread = new HandlerThread("watchdogWorkerThread");
            this.mWorkerThread.start();
            this.mWorkerHandler = new WorkerHandler(this.mWorkerThread.getLooper());
        }
    }

    private void sendMessage(int what, String subject) {
        this.mWorkerHandler.sendMessage(this.mWorkerHandler.obtainMessage(what, subject));
    }

    private void sendMessage(int what) {
        sendMessage(what, (String) null);
    }

    private boolean isDumpCompleted() {
        boolean booleanValue;
        synchronized (this.mDumpCompleteLock) {
            booleanValue = this.mDumpCompleted.booleanValue();
        }
        return booleanValue;
    }

    private void ensureDumpCompletedOrKernelReboot() {
        long timeout = 60000;
        long end = SystemClock.uptimeMillis() + 60000;
        while (!isDumpCompleted() && timeout > 0) {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                Log.wtf(TAG, e);
            }
            timeout = end - SystemClock.uptimeMillis();
        }
        if (timeout <= 0) {
            synchronized (this.mDumpCompleteLock) {
                if (!this.mDumpCompleted.booleanValue()) {
                    Slog.e(TAG, "Triggering SysRq for system_server watchdog");
                    doSysRq('w');
                    doSysRq('l');
                    WatchdogInjector.onWatchdog(385, Process.myPid(), describeCheckersLocked(getBlockedCheckersLocked()), (File) null);
                    this.mDumpCompleted = true;
                    doSysRq('c');
                }
            }
        }
    }

    public final class HandlerChecker implements Runnable {
        private boolean mCompleted;
        private Monitor mCurrentMonitor;
        private final Handler mHandler;
        private final ArrayList<Monitor> mMonitorQueue = new ArrayList<>();
        private final ArrayList<Monitor> mMonitors = new ArrayList<>();
        private final String mName;
        private int mPauseCount;
        private long mStartTime;
        private final long mWaitMax;

        HandlerChecker(Handler handler, String name, long waitMaxMillis) {
            this.mHandler = handler;
            this.mName = name;
            this.mWaitMax = waitMaxMillis;
            this.mCompleted = true;
        }

        /* access modifiers changed from: package-private */
        public void addMonitorLocked(Monitor monitor) {
            this.mMonitorQueue.add(monitor);
        }

        public void scheduleCheckLocked() {
            if (this.mCompleted) {
                this.mMonitors.addAll(this.mMonitorQueue);
                this.mMonitorQueue.clear();
            }
            if ((this.mMonitors.size() == 0 && this.mHandler.getLooper().getQueue().isPolling()) || this.mPauseCount > 0) {
                this.mCompleted = true;
            } else if (this.mCompleted) {
                this.mCompleted = false;
                this.mCurrentMonitor = null;
                this.mStartTime = SystemClock.uptimeMillis();
                this.mHandler.postAtFrontOfQueue(this);
            }
        }

        public boolean isOverdueLocked() {
            return !this.mCompleted && SystemClock.uptimeMillis() > this.mStartTime + this.mWaitMax;
        }

        public int getCompletionStateLocked() {
            if (this.mCompleted) {
                return 0;
            }
            long latency = SystemClock.uptimeMillis() - this.mStartTime;
            long j = this.mWaitMax;
            if (latency < j / 2) {
                return 1;
            }
            if (latency < j) {
                return 2;
            }
            return 3;
        }

        public Thread getThread() {
            return this.mHandler.getLooper().getThread();
        }

        public String getName() {
            return this.mName;
        }

        public String describeBlockedStateLocked() {
            if (this.mCurrentMonitor == null) {
                return "Blocked in handler on " + this.mName + " (" + getThread().getName() + ")";
            }
            return "Blocked in monitor " + this.mCurrentMonitor.getClass().getName() + " on " + this.mName + " (" + getThread().getName() + ")";
        }

        public void run() {
            int size = this.mMonitors.size();
            for (int i = 0; i < size; i++) {
                synchronized (Watchdog.this) {
                    this.mCurrentMonitor = this.mMonitors.get(i);
                }
                this.mCurrentMonitor.monitor();
            }
            synchronized (Watchdog.this) {
                this.mCompleted = true;
                this.mCurrentMonitor = null;
            }
        }

        public void pauseLocked(String reason) {
            this.mPauseCount++;
            this.mCompleted = true;
            Slog.i(Watchdog.TAG, "Pausing HandlerChecker: " + this.mName + " for reason: " + reason + ". Pause count: " + this.mPauseCount);
        }

        public void resumeLocked(String reason) {
            int i = this.mPauseCount;
            if (i > 0) {
                this.mPauseCount = i - 1;
                Slog.i(Watchdog.TAG, "Resuming HandlerChecker: " + this.mName + " for reason: " + reason + ". Pause count: " + this.mPauseCount);
                return;
            }
            Slog.wtf(Watchdog.TAG, "Already resumed HandlerChecker: " + this.mName);
        }
    }

    final class RebootRequestReceiver extends BroadcastReceiver {
        RebootRequestReceiver() {
        }

        public void onReceive(Context c, Intent intent) {
            if (intent.getIntExtra("nowait", 0) != 0) {
                Watchdog.this.rebootSystem("Received ACTION_REBOOT broadcast");
                return;
            }
            Slog.w(Watchdog.TAG, "Unsupported ACTION_REBOOT broadcast: " + intent);
        }
    }

    private static final class BinderThreadMonitor implements Monitor {
        private BinderThreadMonitor() {
        }

        public void monitor() {
            Binder.blockUntilThreadAvailable();
        }
    }

    public static Watchdog getInstance() {
        if (sWatchdog == null) {
            sWatchdog = new Watchdog();
        }
        return sWatchdog;
    }

    private Watchdog() {
        super("watchdog");
        this.mMonitorChecker = new HandlerChecker(FgThread.getHandler(), "foreground thread", 60000);
        this.mHandlerCheckers.add(this.mMonitorChecker);
        this.mHandlerCheckers.add(new HandlerChecker(new Handler(Looper.getMainLooper()), "main thread", 60000));
        this.mHandlerCheckers.add(new HandlerChecker(UiThread.getHandler(), "ui thread", 60000));
        this.mHandlerCheckers.add(new HandlerChecker(IoThread.getHandler(), "i/o thread", 60000));
        this.mHandlerCheckers.add(new HandlerChecker(DisplayThread.getHandler(), "display thread", 60000));
        this.mHandlerCheckers.add(new HandlerChecker(AnimationThread.getHandler(), "animation thread", 60000));
        this.mHandlerCheckers.add(new HandlerChecker(SurfaceAnimationThread.getHandler(), "surface animation thread", 60000));
        addMonitor(new BinderThreadMonitor());
        this.mOpenFdMonitor = OpenFdMonitor.create();
        if (sWatchdogEnhanced) {
            ensureWorkerHandlerReady();
        }
    }

    public void init(Context context, ActivityManagerService activity) {
        this.mActivity = activity;
        context.registerReceiver(new RebootRequestReceiver(), new IntentFilter("android.intent.action.REBOOT"), "android.permission.REBOOT", (Handler) null);
    }

    public void processStarted(String name, int pid) {
        synchronized (this) {
            if ("com.android.phone".equals(name)) {
                this.mPhonePid = pid;
            }
        }
    }

    public void setActivityController(IActivityController controller) {
        synchronized (this) {
            this.mController = controller;
        }
    }

    public void setAllowRestart(boolean allowRestart) {
        synchronized (this) {
            this.mAllowRestart = allowRestart;
        }
    }

    public void addMonitor(Monitor monitor) {
        synchronized (this) {
            this.mMonitorChecker.addMonitorLocked(monitor);
        }
    }

    public void addThread(Handler thread) {
        addThread(thread, 60000);
    }

    public void addThread(Handler thread, long timeoutMillis) {
        synchronized (this) {
            this.mHandlerCheckers.add(new HandlerChecker(thread, thread.getLooper().getThread().getName(), timeoutMillis));
        }
    }

    public void removeThread(Handler thread) {
        synchronized (this) {
            if (thread != null) {
                Iterator<HandlerChecker> it = this.mHandlerCheckers.iterator();
                while (it.hasNext()) {
                    HandlerChecker hc = it.next();
                    if (hc.getThread() == thread.getLooper().getThread()) {
                        this.mHandlerCheckers.remove(hc);
                    }
                }
            }
        }
    }

    public void pauseWatchingCurrentThread(String reason) {
        synchronized (this) {
            Iterator<HandlerChecker> it = this.mHandlerCheckers.iterator();
            while (it.hasNext()) {
                HandlerChecker hc = it.next();
                if (Thread.currentThread().equals(hc.getThread())) {
                    hc.pauseLocked(reason);
                }
            }
        }
    }

    public void resumeWatchingCurrentThread(String reason) {
        synchronized (this) {
            Iterator<HandlerChecker> it = this.mHandlerCheckers.iterator();
            while (it.hasNext()) {
                HandlerChecker hc = it.next();
                if (Thread.currentThread().equals(hc.getThread())) {
                    hc.resumeLocked(reason);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void rebootSystem(String reason) {
        Slog.i(TAG, "Rebooting system because: " + reason);
        try {
            ServiceManager.getService("power").reboot(false, reason, false);
        } catch (RemoteException e) {
        }
    }

    private int evaluateCheckerCompletionLocked() {
        int state = 0;
        for (int i = 0; i < this.mHandlerCheckers.size(); i++) {
            state = Math.max(state, this.mHandlerCheckers.get(i).getCompletionStateLocked());
        }
        return state;
    }

    /* access modifiers changed from: private */
    public ArrayList<HandlerChecker> getBlockedCheckersLocked() {
        ArrayList<HandlerChecker> checkers = new ArrayList<>();
        for (int i = 0; i < this.mHandlerCheckers.size(); i++) {
            HandlerChecker hc = this.mHandlerCheckers.get(i);
            if (hc.isOverdueLocked()) {
                checkers.add(hc);
            }
        }
        return checkers;
    }

    private String describeCheckersLocked(List<HandlerChecker> checkers) {
        StringBuilder builder = new StringBuilder(128);
        for (int i = 0; i < checkers.size(); i++) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(checkers.get(i).describeBlockedStateLocked());
        }
        return builder.toString();
    }

    public static ArrayList<Integer> getInterestingHalPids() {
        try {
            ArrayList<IServiceManager.InstanceDebugInfo> dump = IServiceManager.getService().debugDump();
            HashSet<Integer> pids = new HashSet<>();
            Iterator<IServiceManager.InstanceDebugInfo> it = dump.iterator();
            while (it.hasNext()) {
                IServiceManager.InstanceDebugInfo info = it.next();
                if (info.pid != -1) {
                    if (HAL_INTERFACES_OF_INTEREST.contains(info.interfaceName)) {
                        pids.add(Integer.valueOf(info.pid));
                    }
                }
            }
            return new ArrayList<>(pids);
        } catch (RemoteException e) {
            return new ArrayList<>();
        }
    }

    public static ArrayList<Integer> getInterestingNativePids() {
        ArrayList<Integer> pids = getInterestingHalPids();
        int[] nativePids = Process.getPidsForCommands(NATIVE_STACKS_OF_INTEREST);
        if (nativePids != null) {
            pids.ensureCapacity(pids.size() + nativePids.length);
            for (int i : nativePids) {
                pids.add(Integer.valueOf(i));
            }
        }
        return pids;
    }

    public void run() {
        List<HandlerChecker> blockedCheckers;
        final String subject;
        IActivityController controller;
        boolean waitedHalf = false;
        while (true) {
            int debuggerWasConnected = 0;
            synchronized (this) {
                for (int i = 0; i < this.mHandlerCheckers.size(); i++) {
                    this.mHandlerCheckers.get(i).scheduleCheckLocked();
                }
                if (0 > 0) {
                    debuggerWasConnected = 0 - 1;
                }
                long start = SystemClock.uptimeMillis();
                for (long timeout = 30000; timeout > 0; timeout = 30000 - (SystemClock.uptimeMillis() - start)) {
                    if (Debug.isDebuggerConnected()) {
                        debuggerWasConnected = 2;
                    }
                    try {
                        wait(timeout);
                    } catch (InterruptedException e) {
                        Log.wtf(TAG, e);
                    }
                    if (Debug.isDebuggerConnected()) {
                        debuggerWasConnected = 2;
                    }
                }
                boolean fdLimitTriggered = false;
                if (this.mOpenFdMonitor != null) {
                    fdLimitTriggered = this.mOpenFdMonitor.monitor();
                }
                int i2 = 1;
                if (!fdLimitTriggered) {
                    int waitState = evaluateCheckerCompletionLocked();
                    if (waitState == 0) {
                        waitedHalf = false;
                    } else if (waitState != 1) {
                        if (waitState != 2) {
                            blockedCheckers = getBlockedCheckersLocked();
                            subject = describeCheckersLocked(blockedCheckers);
                        } else if (!waitedHalf) {
                            if (sWatchdogEnhanced) {
                                WatchdogInjector.onWatchdog(384, Process.myPid(), describeCheckersLocked(getBlockedCheckersLocked()), (File) null);
                                sendMessage(0);
                                waitedHalf = true;
                            } else {
                                ArrayList<Integer> pids = new ArrayList<>();
                                pids.add(Integer.valueOf(Process.myPid()));
                                insertInterestedAppPids(pids);
                                this.mInitialStack = ActivityManagerService.dumpStackTraces(pids, (ProcessCpuTracker) null, (SparseArray<Boolean>) null, getInterestingNativePids());
                                waitedHalf = true;
                            }
                        }
                    }
                } else {
                    blockedCheckers = Collections.emptyList();
                    subject = "Open FD high water mark reached";
                }
                boolean allowRestart = this.mAllowRestart;
                EventLog.writeEvent(EventLogTags.WATCHDOG, subject);
                if (sWatchdogEnhanced) {
                    if (waitedHalf) {
                        i2 = 2;
                    }
                    sendMessage(i2, subject);
                } else {
                    ArrayList<Integer> pids2 = new ArrayList<>();
                    pids2.add(Integer.valueOf(Process.myPid()));
                    int i3 = this.mPhonePid;
                    if (i3 > 0) {
                        pids2.add(Integer.valueOf(i3));
                    }
                    insertInterestedAppPids(pids2);
                    final File newFd = dumpTracesFile(waitedHalf, pids2);
                    Thread dropboxThread = new Thread("watchdogWriteToDropbox") {
                        public void run() {
                            if (Watchdog.this.mActivity != null) {
                                Watchdog.this.mActivity.addErrorToDropBox("watchdog", (ProcessRecord) null, "system_server", (String) null, (String) null, (ProcessRecord) null, subject, (String) null, newFd, (ApplicationErrorReport.CrashInfo) null);
                            }
                            StatsLog.write(185, subject);
                        }
                    };
                    dropboxThread.start();
                    try {
                        dropboxThread.join(2000);
                    } catch (InterruptedException e2) {
                    }
                    Slog.e(TAG, "Triggering SysRq for system_server watchdog");
                    doSysRq('w');
                    doSysRq('l');
                    WatchdogInjector.onWatchdog(2, Process.myPid(), subject, newFd, getBlockedCheckersLocked());
                }
                if (SystemProperties.getBoolean("persist.sys.crashOnWatchdog", false)) {
                    doSysRq('c');
                }
                synchronized (this) {
                    controller = this.mController;
                }
                if (controller != null) {
                    Slog.i(TAG, "Reporting stuck state to activity controller");
                    try {
                        Binder.setDumpDisabled("Service dumps disabled due to hung system process.");
                        int res = controller.systemNotResponding(subject);
                        if (res >= 0) {
                            Slog.i(TAG, "Activity controller requested to coninue to wait");
                            waitedHalf = false;
                        } else {
                            Slog.i(TAG, "Activity controller.systemNotResponding(" + subject + ")  return value:" + res);
                        }
                    } catch (RemoteException e3) {
                        Slog.e(TAG, "Got RemoteException: ", e3);
                    }
                } else {
                    Slog.i(TAG, "Activity Controller is null.");
                }
                if (Debug.isDebuggerConnected()) {
                    debuggerWasConnected = 2;
                }
                if (debuggerWasConnected >= 2) {
                    Slog.w(TAG, "Debugger connected: Watchdog is *not* killing the system process");
                } else if (debuggerWasConnected > 0) {
                    Slog.w(TAG, "Debugger was connected: Watchdog is *not* killing the system process");
                } else if (!allowRestart) {
                    Slog.w(TAG, "Restart not allowed: Watchdog is *not* killing the system process");
                } else {
                    Slog.w(TAG, "*** WATCHDOG KILLING SYSTEM PROCESS: " + subject);
                    WatchdogDiagnostics.diagnoseCheckers(blockedCheckers);
                    Slog.w(TAG, "*** GOODBYE!");
                    if (sWatchdogEnhanced) {
                        synchronized (this) {
                            ensureDumpCompletedOrKernelReboot();
                        }
                    }
                    Process.killProcess(Process.myPid());
                    System.exit(10);
                }
                waitedHalf = false;
            }
        }
        while (true) {
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 4 */
    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
        r0.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x001c, code lost:
        throw r2;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0013, code lost:
        r2 = move-exception;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void doSysRq(char r5) {
        /*
            r4 = this;
            java.io.FileWriter r0 = new java.io.FileWriter     // Catch:{ IOException -> 0x001d }
            java.lang.String r1 = "/proc/sysrq-trigger"
            r0.<init>(r1)     // Catch:{ IOException -> 0x001d }
            r0.write(r5)     // Catch:{ all -> 0x0011 }
            r0.close()     // Catch:{ all -> 0x0011 }
            r0.close()     // Catch:{ IOException -> 0x001d }
            goto L_0x0025
        L_0x0011:
            r1 = move-exception
            throw r1     // Catch:{ all -> 0x0013 }
        L_0x0013:
            r2 = move-exception
            r0.close()     // Catch:{ all -> 0x0018 }
            goto L_0x001c
        L_0x0018:
            r3 = move-exception
            r1.addSuppressed(r3)     // Catch:{ IOException -> 0x001d }
        L_0x001c:
            throw r2     // Catch:{ IOException -> 0x001d }
        L_0x001d:
            r0 = move-exception
            java.lang.String r1 = "Watchdog"
            java.lang.String r2 = "Failed to write to /proc/sysrq-trigger"
            android.util.Slog.w(r1, r2, r0)
        L_0x0025:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.Watchdog.doSysRq(char):void");
    }

    /* access modifiers changed from: private */
    public File dumpTracesFile(boolean waitedHalf, ArrayList<Integer> pids) {
        File finalStack = ActivityManagerService.dumpStackTraces(pids, (ProcessCpuTracker) null, (SparseArray<Boolean>) null, getInterestingNativePids());
        if (Build.IS_DEBUGGABLE) {
            binderStateRead();
        }
        SystemClock.sleep(5000);
        File watchdogTraces = new File(new File(ActivityManagerService.ANR_TRACE_DIR), "traces_SystemServer_WDT" + this.mTraceDateFormat.format(new Date()) + "_pid" + String.valueOf(Process.myPid()));
        try {
            if (watchdogTraces.createNewFile()) {
                FileUtils.setPermissions(watchdogTraces.getAbsolutePath(), 384, -1, -1);
                if (this.mInitialStack == null) {
                    Slog.e(TAG, "First set of traces are empty!");
                } else if (System.currentTimeMillis() - this.mInitialStack.lastModified() < BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS) {
                    Slog.e(TAG, "First set of traces taken from " + this.mInitialStack.getAbsolutePath());
                    appendFile(watchdogTraces, this.mInitialStack);
                } else {
                    Slog.e(TAG, "First set of traces were collected more than 5 minutes ago, ignoring ...");
                }
                if (finalStack != null) {
                    Slog.e(TAG, "Second set of traces taken from " + finalStack.getAbsolutePath());
                    appendFile(watchdogTraces, finalStack);
                } else {
                    Slog.e(TAG, "Second set of traces are empty!");
                }
            } else {
                Slog.w(TAG, "Unable to create Watchdog dump file: createNewFile failed");
            }
        } catch (Exception e) {
            Slog.e(TAG, "Exception creating Watchdog dump file:", e);
        }
        this.mInitialStack = null;
        return watchdogTraces;
    }

    private String getTracesDirPath() {
        return SystemProperties.get("dalvik.vm.stack-trace-dir", (String) null);
    }

    private String getTracesFilePath() {
        return SystemProperties.get("dalvik.vm.stack-trace-file", (String) null);
    }

    private void appendFile(File writeTo, File copyFrom) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(copyFrom));
            FileWriter out = new FileWriter(writeTo, true);
            while (true) {
                String readLine = in.readLine();
                String line = readLine;
                if (readLine != null) {
                    out.write(line);
                    out.write(10);
                } else {
                    in.close();
                    out.close();
                    return;
                }
            }
        } catch (IOException e) {
            Slog.e(TAG, "Exception while writing watchdog traces to new file!");
            e.printStackTrace();
        }
    }

    private void binderStateRead() {
        BufferedReader in = null;
        FileWriter out = null;
        try {
            Slog.i(TAG, "Collecting Binder Transaction Status Information");
            in = new BufferedReader(new FileReader("/sys/kernel/debug/binder/state"));
            out = new FileWriter("/data/anr/BinderTraces_pid" + String.valueOf(Process.myPid()) + ".txt");
            while (true) {
                String readLine = in.readLine();
                String line = readLine;
                if (readLine == null) {
                    break;
                }
                out.write(line);
                out.write(10);
            }
        } catch (IOException e) {
            Slog.w(TAG, "Failed to collect state file", e);
        } catch (Throwable th) {
            IoUtils.closeQuietly((AutoCloseable) null);
            IoUtils.closeQuietly((AutoCloseable) null);
            throw th;
        }
        IoUtils.closeQuietly(in);
        IoUtils.closeQuietly(out);
    }

    public static final class OpenFdMonitor {
        private static final int BEGIN_TRACK_THRESHOLD = SystemProperties.getInt("persist.fdleak.begintrackthreshold", 1000);
        private static final int FD_HIGH_WATER_MARK = 12;
        private static boolean bugreportFiled = false;
        private static int openFdRlimit = 0;
        private static boolean sLoaded = false;
        private final File mDumpDir;
        private final File mFdHighWaterMark;

        public static OpenFdMonitor create() {
            if (!Build.IS_DEBUGGABLE) {
                return null;
            }
            try {
                StructRlimit rlimit = Os.getrlimit(OsConstants.RLIMIT_NOFILE);
                openFdRlimit = (int) rlimit.rlim_cur;
                return new OpenFdMonitor(new File(ActivityManagerService.ANR_TRACE_DIR), new File("/proc/self/fd/" + (rlimit.rlim_cur - 12)));
            } catch (ErrnoException errno) {
                Slog.w(Watchdog.TAG, "Error thrown from getrlimit(RLIMIT_NOFILE)", errno);
                return null;
            }
        }

        OpenFdMonitor(File dumpDir, File fdThreshold) {
            this.mDumpDir = dumpDir;
            this.mFdHighWaterMark = fdThreshold;
        }

        private void dumpOpenDescriptors() {
            String resolvedPath;
            List<String> dumpInfo = new ArrayList<>();
            String fdDirPath = String.format("/proc/%d/fd/", new Object[]{Integer.valueOf(Process.myPid())});
            File[] fds = new File(fdDirPath).listFiles();
            if (fds == null) {
                dumpInfo.add("Unable to list " + fdDirPath);
            } else {
                for (File f : fds) {
                    String fdSymLink = f.getAbsolutePath();
                    try {
                        resolvedPath = Os.readlink(fdSymLink);
                    } catch (ErrnoException ex) {
                        resolvedPath = ex.getMessage();
                    }
                    dumpInfo.add(fdSymLink + "\t" + resolvedPath + " (" + FdInfoManager.getFdOwner(f) + ")");
                }
            }
            try {
                Files.write(Paths.get(File.createTempFile("anr_fd_", "", this.mDumpDir).getAbsolutePath(), new String[0]), dumpInfo, StandardCharsets.UTF_8, new OpenOption[0]);
            } catch (IOException ex2) {
                Slog.w(Watchdog.TAG, "Unable to write open descriptors to file: " + ex2);
            }
        }

        public boolean monitor() {
            if (!sLoaded) {
                if (new File("/proc/self/fd/" + BEGIN_TRACK_THRESHOLD).exists()) {
                    sLoaded = true;
                    try {
                        System.loadLibrary("fdleak_track");
                    } catch (Throwable e) {
                        Slog.d(Watchdog.TAG, "Failed to load libfdleak_track.so: " + e.getMessage());
                    }
                }
            }
            if (bugreportFiled || !FdInfoManager.checkTooManyOpenFiles((openFdRlimit * 2) / 3) || !FdInfoManager.isMtbfTest()) {
                return false;
            }
            bugreportFiled = true;
            Slog.w(Watchdog.TAG, "Too many open fds, run bugreport now");
            Intent bugreport = new Intent("com.miui.bugreport.service.action.CONFIRM_DIALOG");
            bugreport.putExtra("extra_secret_code", "284");
            ActivityThread.currentActivityThread().getSystemContext().sendBroadcastAsUser(bugreport, UserHandle.SYSTEM);
            dumpOpenDescriptors();
            FdInfoManager.dumpHeapProfile("system_server", Process.myPid());
            return false;
        }
    }
}
