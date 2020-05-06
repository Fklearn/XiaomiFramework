package com.android.server.wm;

import android.app.ActivityManager;
import android.app.WindowConfiguration;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.dex.ArtManagerInternal;
import android.content.pm.dex.PackageOptimizationInfo;
import android.metrics.LogMaker;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.Trace;
import android.util.BoostFramework;
import android.util.EventLog;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.StatsLog;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.os.BackgroundThread;
import com.android.internal.os.SomeArgs;
import com.android.server.LocalServices;
import com.android.server.am.EventLogTags;
import com.android.server.usb.descriptors.UsbTerminalTypes;
import com.android.server.wm.ActivityMetricsLogger;

class ActivityMetricsLogger {
    private static final int INVALID_DELAY = -1;
    private static final long INVALID_START_TIME = -1;
    private static final int INVALID_TRANSITION_TYPE = -1;
    @VisibleForTesting
    static final int LAUNCH_OBSERVER_ACTIVITY_RECORD_PROTO_CHUNK_SIZE = 512;
    private static final int MSG_CHECK_VISIBILITY = 0;
    private static final String TAG = "ActivityTaskManager";
    private static final String[] TRON_WINDOW_STATE_VARZ_STRINGS = {"window_time_0", "window_time_1", "window_time_2", "window_time_3"};
    private static final int WINDOW_STATE_ASSISTANT = 3;
    private static final int WINDOW_STATE_FREEFORM = 2;
    private static final int WINDOW_STATE_INVALID = -1;
    private static final int WINDOW_STATE_SIDE_BY_SIDE = 1;
    private static final int WINDOW_STATE_STANDARD = 0;
    private static ActivityRecord mLaunchedActivity;
    public static BoostFramework mUxPerf = new BoostFramework();
    private ArtManagerInternal mArtManagerInternal;
    private final Context mContext;
    private int mCurrentTransitionDelayMs;
    private int mCurrentTransitionDeviceUptime;
    private long mCurrentTransitionStartTime = -1;
    private final H mHandler;
    private long mLastLogTimeSecs = (SystemClock.elapsedRealtime() / 1000);
    private long mLastTransitionStartTime = -1;
    private final SparseArray<WindowingModeTransitionInfo> mLastWindowingModeTransitionInfo = new SparseArray<>();
    private final LaunchObserverRegistryImpl mLaunchObserver;
    private boolean mLoggedTransitionStarting;
    private final MetricsLogger mMetricsLogger = new MetricsLogger();
    private final StringBuilder mStringBuilder = new StringBuilder();
    private final ActivityStackSupervisor mSupervisor;
    private int mWindowState = 0;
    private final SparseArray<WindowingModeTransitionInfo> mWindowingModeTransitionInfo = new SparseArray<>();

    private final class H extends Handler {
        public H(Looper looper) {
            super(looper);
        }

        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                SomeArgs args = (SomeArgs) msg.obj;
                ActivityMetricsLogger.this.checkVisibility((TaskRecord) args.arg1, (ActivityRecord) args.arg2);
            }
        }
    }

    private final class WindowingModeTransitionInfo {
        /* access modifiers changed from: private */
        public int bindApplicationDelayMs;
        /* access modifiers changed from: private */
        public boolean currentTransitionProcessRunning;
        /* access modifiers changed from: private */
        public boolean launchTraceActive;
        /* access modifiers changed from: private */
        public ActivityRecord launchedActivity;
        /* access modifiers changed from: private */
        public boolean loggedStartingWindowDrawn;
        /* access modifiers changed from: private */
        public boolean loggedWindowsDrawn;
        /* access modifiers changed from: private */
        public int reason;
        /* access modifiers changed from: private */
        public int startResult;
        /* access modifiers changed from: private */
        public int startingWindowDelayMs;
        /* access modifiers changed from: private */
        public int windowsDrawnDelayMs;

        private WindowingModeTransitionInfo() {
            this.startingWindowDelayMs = -1;
            this.bindApplicationDelayMs = -1;
            this.reason = 3;
        }
    }

    final class WindowingModeTransitionInfoSnapshot {
        final int activityRecordIdHashCode;
        /* access modifiers changed from: private */
        public final ApplicationInfo applicationInfo;
        /* access modifiers changed from: private */
        public final int bindApplicationDelayMs;
        /* access modifiers changed from: private */
        public final String launchedActivityAppRecordRequiredAbi;
        /* access modifiers changed from: private */
        public final String launchedActivityLaunchToken;
        /* access modifiers changed from: private */
        public final String launchedActivityLaunchedFromPackage;
        final String launchedActivityName;
        final String launchedActivityShortComponentName;
        final String packageName;
        /* access modifiers changed from: private */
        public final String processName;
        /* access modifiers changed from: private */
        public final WindowProcessController processRecord;
        /* access modifiers changed from: private */
        public final int reason;
        /* access modifiers changed from: private */
        public final int startingWindowDelayMs;
        final int type;
        final int userId;
        final int windowsDrawnDelayMs;
        final int windowsFullyDrawnDelayMs;

        private WindowingModeTransitionInfoSnapshot(ActivityMetricsLogger this$02, WindowingModeTransitionInfo info) {
            this(this$02, info, info.launchedActivity);
        }

        private WindowingModeTransitionInfoSnapshot(ActivityMetricsLogger this$02, WindowingModeTransitionInfo info, ActivityRecord launchedActivity) {
            this(info, launchedActivity, -1);
        }

        private WindowingModeTransitionInfoSnapshot(WindowingModeTransitionInfo info, ActivityRecord launchedActivity, int windowsFullyDrawnDelayMs2) {
            String str;
            this.applicationInfo = launchedActivity.appInfo;
            this.packageName = launchedActivity.packageName;
            this.launchedActivityName = launchedActivity.info.name;
            this.launchedActivityLaunchedFromPackage = launchedActivity.launchedFromPackage;
            this.launchedActivityLaunchToken = launchedActivity.info.launchToken;
            if (launchedActivity.app == null) {
                str = null;
            } else {
                str = launchedActivity.app.getRequiredAbi();
            }
            this.launchedActivityAppRecordRequiredAbi = str;
            this.reason = info.reason;
            this.startingWindowDelayMs = info.startingWindowDelayMs;
            this.bindApplicationDelayMs = info.bindApplicationDelayMs;
            this.windowsDrawnDelayMs = info.windowsDrawnDelayMs;
            this.type = ActivityMetricsLogger.this.getTransitionType(info);
            this.processRecord = ActivityMetricsLogger.this.findProcessForActivity(launchedActivity);
            this.processName = launchedActivity.processName;
            this.userId = launchedActivity.mUserId;
            this.launchedActivityShortComponentName = launchedActivity.shortComponentName;
            this.activityRecordIdHashCode = System.identityHashCode(launchedActivity);
            this.windowsFullyDrawnDelayMs = windowsFullyDrawnDelayMs2;
        }

        /* access modifiers changed from: package-private */
        public int getLaunchState() {
            int i = this.type;
            if (i == 7) {
                return 1;
            }
            if (i == 8) {
                return 2;
            }
            if (i != 9) {
                return -1;
            }
            return 3;
        }
    }

    ActivityMetricsLogger(ActivityStackSupervisor supervisor, Context context, Looper looper) {
        this.mSupervisor = supervisor;
        this.mContext = context;
        this.mHandler = new H(looper);
        this.mLaunchObserver = new LaunchObserverRegistryImpl(looper);
    }

    /* access modifiers changed from: package-private */
    public void logWindowState() {
        long now = SystemClock.elapsedRealtime() / 1000;
        int i = this.mWindowState;
        if (i != -1) {
            MetricsLogger.count(this.mContext, TRON_WINDOW_STATE_VARZ_STRINGS[i], (int) (now - this.mLastLogTimeSecs));
        }
        this.mLastLogTimeSecs = now;
        this.mWindowState = -1;
        ActivityStack stack = this.mSupervisor.mRootActivityContainer.getTopDisplayFocusedStack();
        if (stack != null) {
            if (stack.isActivityTypeAssistant()) {
                this.mWindowState = 3;
                return;
            }
            int windowingMode = stack.getWindowingMode();
            if (windowingMode == 2) {
                stack = this.mSupervisor.mRootActivityContainer.findStackBehind(stack);
                windowingMode = stack.getWindowingMode();
            }
            if (windowingMode == 1) {
                this.mWindowState = 0;
            } else if (windowingMode == 3 || windowingMode == 4) {
                this.mWindowState = 1;
            } else if (windowingMode == 5) {
                this.mWindowState = 2;
            } else if (windowingMode != 0) {
                throw new IllegalStateException("Unknown windowing mode for stack=" + stack + " windowingMode=" + windowingMode);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyActivityLaunching(Intent intent) {
        if (this.mCurrentTransitionStartTime == -1) {
            this.mCurrentTransitionStartTime = SystemClock.uptimeMillis();
            this.mLastTransitionStartTime = this.mCurrentTransitionStartTime;
            launchObserverNotifyIntentStarted(intent);
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyActivityLaunched(int resultCode, ActivityRecord launchedActivity) {
        WindowProcessController processRecord = findProcessForActivity(launchedActivity);
        boolean processSwitch = true;
        boolean processRunning = processRecord != null;
        if (processRecord != null && processRecord.hasStartedActivity(launchedActivity)) {
            processSwitch = false;
        }
        notifyActivityLaunched(resultCode, launchedActivity, processRunning, processSwitch);
    }

    private void notifyActivityLaunched(int resultCode, ActivityRecord launchedActivity, boolean processRunning, boolean processSwitch) {
        int windowingMode;
        boolean otherWindowModesLaunching = false;
        if (launchedActivity != null) {
            windowingMode = launchedActivity.getWindowingMode();
        } else {
            windowingMode = 0;
        }
        WindowingModeTransitionInfo info = this.mWindowingModeTransitionInfo.get(windowingMode);
        if (this.mCurrentTransitionStartTime != -1) {
            if (launchedActivity != null && launchedActivity.mDrawn) {
                reset(true, info, "launched activity already visible");
            } else if (launchedActivity == null || info == null) {
                if (this.mWindowingModeTransitionInfo.size() > 0 && info == null) {
                    otherWindowModesLaunching = true;
                }
                if ((!isLoggableResultCode(resultCode) || launchedActivity == null || windowingMode == 0) && !otherWindowModesLaunching) {
                    reset(true, info, "failed to launch or not a process switch");
                } else if (!otherWindowModesLaunching) {
                    WindowingModeTransitionInfo newInfo = new WindowingModeTransitionInfo();
                    ActivityRecord unused = newInfo.launchedActivity = launchedActivity;
                    boolean unused2 = newInfo.currentTransitionProcessRunning = processRunning;
                    int unused3 = newInfo.startResult = resultCode;
                    this.mWindowingModeTransitionInfo.put(windowingMode, newInfo);
                    this.mLastWindowingModeTransitionInfo.put(windowingMode, newInfo);
                    this.mCurrentTransitionDeviceUptime = (int) (SystemClock.uptimeMillis() / 1000);
                    startTraces(newInfo);
                    launchObserverNotifyActivityLaunched(newInfo);
                }
            } else {
                ActivityRecord unused4 = info.launchedActivity = launchedActivity;
            }
        }
    }

    private boolean isLoggableResultCode(int resultCode) {
        return resultCode == 0 || resultCode == 2;
    }

    /* access modifiers changed from: package-private */
    public WindowingModeTransitionInfoSnapshot notifyWindowsDrawn(@WindowConfiguration.WindowingMode int windowingMode, long timestamp) {
        WindowingModeTransitionInfo info = this.mWindowingModeTransitionInfo.get(windowingMode);
        if (info == null || info.loggedWindowsDrawn) {
            return null;
        }
        int unused = info.windowsDrawnDelayMs = calculateDelay(timestamp);
        boolean unused2 = info.loggedWindowsDrawn = true;
        WindowingModeTransitionInfoSnapshot infoSnapshot = new WindowingModeTransitionInfoSnapshot(info);
        if (allWindowsDrawn() && this.mLoggedTransitionStarting) {
            reset(false, info, "notifyWindowsDrawn - all windows drawn");
        }
        return infoSnapshot;
    }

    /* access modifiers changed from: package-private */
    public void notifyStartingWindowDrawn(@WindowConfiguration.WindowingMode int windowingMode, long timestamp) {
        WindowingModeTransitionInfo info = this.mWindowingModeTransitionInfo.get(windowingMode);
        if (info != null && !info.loggedStartingWindowDrawn) {
            boolean unused = info.loggedStartingWindowDrawn = true;
            int unused2 = info.startingWindowDelayMs = calculateDelay(timestamp);
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyTransitionStarting(SparseIntArray windowingModeToReason, long timestamp) {
        if (isAnyTransitionActive() && !this.mLoggedTransitionStarting) {
            this.mCurrentTransitionDelayMs = calculateDelay(timestamp);
            boolean abortMetrics = true;
            this.mLoggedTransitionStarting = true;
            WindowingModeTransitionInfo foundInfo = null;
            for (int index = windowingModeToReason.size() - 1; index >= 0; index--) {
                WindowingModeTransitionInfo info = this.mWindowingModeTransitionInfo.get(windowingModeToReason.keyAt(index));
                if (info != null) {
                    int unused = info.reason = windowingModeToReason.valueAt(index);
                    foundInfo = info;
                }
            }
            if (allWindowsDrawn() != 0) {
                if (foundInfo != null) {
                    abortMetrics = false;
                }
                reset(abortMetrics, foundInfo, "notifyTransitionStarting - all windows drawn");
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void notifyVisibilityChanged(ActivityRecord activityRecord) {
        WindowingModeTransitionInfo info = this.mWindowingModeTransitionInfo.get(activityRecord.getWindowingMode());
        if (info != null && info.launchedActivity == activityRecord) {
            TaskRecord t = activityRecord.getTaskRecord();
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = t;
            args.arg2 = activityRecord;
            this.mHandler.obtainMessage(0, args).sendToTarget();
        }
    }

    private boolean hasVisibleNonFinishingActivity(TaskRecord t) {
        for (int i = t.mActivities.size() - 1; i >= 0; i--) {
            ActivityRecord r = t.mActivities.get(i);
            if (r.visible && !r.finishing) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Code restructure failed: missing block: B:25:0x004e, code lost:
        com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:26:0x0051, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void checkVisibility(com.android.server.wm.TaskRecord r5, com.android.server.wm.ActivityRecord r6) {
        /*
            r4 = this;
            com.android.server.wm.ActivityStackSupervisor r0 = r4.mSupervisor
            com.android.server.wm.ActivityTaskManagerService r0 = r0.mService
            com.android.server.wm.WindowManagerGlobalLock r0 = r0.mGlobalLock
            monitor-enter(r0)
            com.android.server.wm.WindowManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x0052 }
            android.util.SparseArray<com.android.server.wm.ActivityMetricsLogger$WindowingModeTransitionInfo> r1 = r4.mWindowingModeTransitionInfo     // Catch:{ all -> 0x0052 }
            int r2 = r6.getWindowingMode()     // Catch:{ all -> 0x0052 }
            java.lang.Object r1 = r1.get(r2)     // Catch:{ all -> 0x0052 }
            com.android.server.wm.ActivityMetricsLogger$WindowingModeTransitionInfo r1 = (com.android.server.wm.ActivityMetricsLogger.WindowingModeTransitionInfo) r1     // Catch:{ all -> 0x0052 }
            if (r1 != 0) goto L_0x001d
            monitor-exit(r0)     // Catch:{ all -> 0x0052 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return
        L_0x001d:
            com.android.server.wm.ActivityRecord r2 = r1.launchedActivity     // Catch:{ all -> 0x0052 }
            if (r2 == r6) goto L_0x0028
            monitor-exit(r0)     // Catch:{ all -> 0x0052 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0028:
            boolean r2 = r4.hasVisibleNonFinishingActivity(r5)     // Catch:{ all -> 0x0052 }
            if (r2 == 0) goto L_0x0033
            monitor-exit(r0)     // Catch:{ all -> 0x0052 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0033:
            r4.logAppTransitionCancel(r1)     // Catch:{ all -> 0x0052 }
            android.util.SparseArray<com.android.server.wm.ActivityMetricsLogger$WindowingModeTransitionInfo> r2 = r4.mWindowingModeTransitionInfo     // Catch:{ all -> 0x0052 }
            int r3 = r6.getWindowingMode()     // Catch:{ all -> 0x0052 }
            r2.remove(r3)     // Catch:{ all -> 0x0052 }
            android.util.SparseArray<com.android.server.wm.ActivityMetricsLogger$WindowingModeTransitionInfo> r2 = r4.mWindowingModeTransitionInfo     // Catch:{ all -> 0x0052 }
            int r2 = r2.size()     // Catch:{ all -> 0x0052 }
            if (r2 != 0) goto L_0x004d
            r2 = 1
            java.lang.String r3 = "notifyVisibilityChanged to invisible"
            r4.reset(r2, r1, r3)     // Catch:{ all -> 0x0052 }
        L_0x004d:
            monitor-exit(r0)     // Catch:{ all -> 0x0052 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0052:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x0052 }
            com.android.server.wm.WindowManagerService.resetPriorityAfterLockedSection()
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityMetricsLogger.checkVisibility(com.android.server.wm.TaskRecord, com.android.server.wm.ActivityRecord):void");
    }

    /* access modifiers changed from: package-private */
    public void notifyBindApplication(ApplicationInfo appInfo) {
        for (int i = this.mWindowingModeTransitionInfo.size() - 1; i >= 0; i--) {
            WindowingModeTransitionInfo info = this.mWindowingModeTransitionInfo.valueAt(i);
            if (info.launchedActivity.appInfo == appInfo) {
                int unused = info.bindApplicationDelayMs = calculateCurrentDelay();
            }
        }
    }

    private boolean allWindowsDrawn() {
        for (int index = this.mWindowingModeTransitionInfo.size() - 1; index >= 0; index--) {
            if (!this.mWindowingModeTransitionInfo.valueAt(index).loggedWindowsDrawn) {
                return false;
            }
        }
        return true;
    }

    private boolean isAnyTransitionActive() {
        return this.mCurrentTransitionStartTime != -1 && this.mWindowingModeTransitionInfo.size() > 0;
    }

    private void reset(boolean abort, WindowingModeTransitionInfo info, String cause) {
        if (!abort && isAnyTransitionActive()) {
            logAppTransitionMultiEvents();
        }
        stopLaunchTrace(info);
        if (!isAnyTransitionActive()) {
            launchObserverNotifyIntentFailed();
        } else if (abort) {
            launchObserverNotifyActivityLaunchCancelled(info);
        } else {
            launchObserverNotifyActivityLaunchFinished(info);
        }
        this.mCurrentTransitionStartTime = -1;
        this.mCurrentTransitionDelayMs = -1;
        this.mLoggedTransitionStarting = false;
        this.mWindowingModeTransitionInfo.clear();
    }

    private int calculateCurrentDelay() {
        return (int) (SystemClock.uptimeMillis() - this.mCurrentTransitionStartTime);
    }

    private int calculateDelay(long timestamp) {
        return (int) (timestamp - this.mCurrentTransitionStartTime);
    }

    private void logAppTransitionCancel(WindowingModeTransitionInfo info) {
        int type = getTransitionType(info);
        if (type != -1) {
            LogMaker builder = new LogMaker(1144);
            builder.setPackageName(info.launchedActivity.packageName);
            builder.setType(type);
            builder.addTaggedData(871, info.launchedActivity.info.name);
            this.mMetricsLogger.write(builder);
            StatsLog.write(49, info.launchedActivity.appInfo.uid, info.launchedActivity.packageName, convertAppStartTransitionType(type), info.launchedActivity.info.name);
        }
    }

    private void logAppTransitionMultiEvents() {
        int index = this.mWindowingModeTransitionInfo.size() - 1;
        while (index >= 0) {
            WindowingModeTransitionInfo info = this.mWindowingModeTransitionInfo.valueAt(index);
            if (getTransitionType(info) != -1) {
                mLaunchedActivity = info.launchedActivity;
                WindowingModeTransitionInfoSnapshot infoSnapshot = new WindowingModeTransitionInfoSnapshot(info);
                BackgroundThread.getHandler().post(new Runnable(this.mCurrentTransitionDeviceUptime, this.mCurrentTransitionDelayMs, infoSnapshot) {
                    private final /* synthetic */ int f$1;
                    private final /* synthetic */ int f$2;
                    private final /* synthetic */ ActivityMetricsLogger.WindowingModeTransitionInfoSnapshot f$3;

                    {
                        this.f$1 = r2;
                        this.f$2 = r3;
                        this.f$3 = r4;
                    }

                    public final void run() {
                        ActivityMetricsLogger.this.lambda$logAppTransitionMultiEvents$0$ActivityMetricsLogger(this.f$1, this.f$2, this.f$3);
                    }
                });
                BackgroundThread.getHandler().post(new Runnable(infoSnapshot) {
                    private final /* synthetic */ ActivityMetricsLogger.WindowingModeTransitionInfoSnapshot f$1;

                    {
                        this.f$1 = r2;
                    }

                    public final void run() {
                        ActivityMetricsLogger.this.lambda$logAppTransitionMultiEvents$1$ActivityMetricsLogger(this.f$1);
                    }
                });
                info.launchedActivity.info.launchToken = null;
                index--;
            } else {
                return;
            }
        }
    }

    /* access modifiers changed from: private */
    /* renamed from: logAppTransition */
    public void lambda$logAppTransitionMultiEvents$0$ActivityMetricsLogger(int currentTransitionDeviceUptime, int currentTransitionDelayMs, WindowingModeTransitionInfoSnapshot info) {
        PackageOptimizationInfo packageOptimizationInfo;
        WindowingModeTransitionInfoSnapshot windowingModeTransitionInfoSnapshot = info;
        LogMaker builder = new LogMaker(761);
        builder.setPackageName(windowingModeTransitionInfoSnapshot.packageName);
        builder.setType(windowingModeTransitionInfoSnapshot.type);
        builder.addTaggedData(871, windowingModeTransitionInfoSnapshot.launchedActivityName);
        boolean isInstantApp = info.applicationInfo.isInstantApp();
        if (info.launchedActivityLaunchedFromPackage != null) {
            builder.addTaggedData(904, info.launchedActivityLaunchedFromPackage);
        }
        String launchToken = info.launchedActivityLaunchToken;
        if (launchToken != null) {
            builder.addTaggedData(903, launchToken);
        }
        builder.addTaggedData(905, Integer.valueOf(isInstantApp));
        builder.addTaggedData(325, Integer.valueOf(currentTransitionDeviceUptime));
        builder.addTaggedData(319, Integer.valueOf(currentTransitionDelayMs));
        builder.setSubtype(info.reason);
        if (info.startingWindowDelayMs != -1) {
            builder.addTaggedData(321, Integer.valueOf(info.startingWindowDelayMs));
        }
        if (info.bindApplicationDelayMs != -1) {
            builder.addTaggedData(945, Integer.valueOf(info.bindApplicationDelayMs));
        }
        builder.addTaggedData(322, Integer.valueOf(windowingModeTransitionInfoSnapshot.windowsDrawnDelayMs));
        ArtManagerInternal artManagerInternal = getArtManagerInternal();
        if (artManagerInternal == null || info.launchedActivityAppRecordRequiredAbi == null) {
            packageOptimizationInfo = PackageOptimizationInfo.createWithNoInfo();
        } else {
            packageOptimizationInfo = artManagerInternal.getPackageOptimizationInfo(info.applicationInfo, info.launchedActivityAppRecordRequiredAbi);
        }
        PackageOptimizationInfo packageOptimizationInfo2 = packageOptimizationInfo;
        builder.addTaggedData(1321, Integer.valueOf(packageOptimizationInfo2.getCompilationReason()));
        builder.addTaggedData(1320, Integer.valueOf(packageOptimizationInfo2.getCompilationFilter()));
        this.mMetricsLogger.write(builder);
        ArtManagerInternal artManagerInternal2 = artManagerInternal;
        String launchToken2 = launchToken;
        StatsLog.write(48, info.applicationInfo.uid, windowingModeTransitionInfoSnapshot.packageName, convertAppStartTransitionType(windowingModeTransitionInfoSnapshot.type), windowingModeTransitionInfoSnapshot.launchedActivityName, info.launchedActivityLaunchedFromPackage, isInstantApp, (long) (currentTransitionDeviceUptime * 1000), info.reason, currentTransitionDelayMs, info.startingWindowDelayMs, info.bindApplicationDelayMs, windowingModeTransitionInfoSnapshot.windowsDrawnDelayMs, launchToken2, packageOptimizationInfo2.getCompilationReason(), packageOptimizationInfo2.getCompilationFilter());
        logAppStartMemoryStateCapture(windowingModeTransitionInfoSnapshot);
    }

    /* access modifiers changed from: private */
    /* renamed from: logAppDisplayed */
    public void lambda$logAppTransitionMultiEvents$1$ActivityMetricsLogger(WindowingModeTransitionInfoSnapshot info) {
        if (info.type == 8 || info.type == 7) {
            EventLog.writeEvent(EventLogTags.AM_ACTIVITY_LAUNCH_TIME, new Object[]{Integer.valueOf(info.userId), Integer.valueOf(info.activityRecordIdHashCode), info.launchedActivityShortComponentName, Integer.valueOf(info.windowsDrawnDelayMs)});
            StringBuilder sb = this.mStringBuilder;
            sb.setLength(0);
            sb.append("Displayed ");
            sb.append(info.launchedActivityShortComponentName);
            sb.append(": ");
            TimeUtils.formatDuration((long) info.windowsDrawnDelayMs, sb);
            BoostFramework boostFramework = mUxPerf;
            if (boostFramework != null) {
                boostFramework.perfUXEngine_events(3, 0, info.packageName, info.windowsDrawnDelayMs);
            }
            Log.i(TAG, sb.toString());
            int isGame = mLaunchedActivity.isAppInfoGame();
            BoostFramework boostFramework2 = mUxPerf;
            if (boostFramework2 != null) {
                boostFramework2.perfUXEngine_events(5, 0, info.packageName, isGame);
            }
            if (mLaunchedActivity.mPerf != null && mLaunchedActivity.perfActivityBoostHandler > 0) {
                mLaunchedActivity.mPerf.perfLockReleaseHandler(mLaunchedActivity.perfActivityBoostHandler);
                mLaunchedActivity.perfActivityBoostHandler = -1;
            }
        }
    }

    private int convertAppStartTransitionType(int tronType) {
        if (tronType == 7) {
            return 3;
        }
        if (tronType == 8) {
            return 1;
        }
        if (tronType == 9) {
            return 2;
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public WindowingModeTransitionInfoSnapshot logAppTransitionReportedDrawn(ActivityRecord r, boolean restoredFromBundle) {
        int i;
        int i2;
        ActivityRecord activityRecord = r;
        WindowingModeTransitionInfo info = this.mLastWindowingModeTransitionInfo.get(r.getWindowingMode());
        if (info == null) {
            return null;
        }
        Trace.traceBegin(64, "ActivityManager:ReportingFullyDrawn " + info.launchedActivity.packageName);
        LogMaker builder = new LogMaker(1090);
        builder.setPackageName(activityRecord.packageName);
        builder.addTaggedData(871, activityRecord.info.name);
        long startupTimeMs = SystemClock.uptimeMillis() - this.mLastTransitionStartTime;
        builder.addTaggedData(1091, Long.valueOf(startupTimeMs));
        if (restoredFromBundle) {
            i = 13;
        } else {
            i = 12;
        }
        builder.setType(i);
        builder.addTaggedData(324, Integer.valueOf(info.currentTransitionProcessRunning ? 1 : 0));
        this.mMetricsLogger.write(builder);
        int i3 = info.launchedActivity.appInfo.uid;
        String str = info.launchedActivity.packageName;
        if (restoredFromBundle) {
            i2 = 1;
        } else {
            i2 = 2;
        }
        StatsLog.write(50, i3, str, i2, info.launchedActivity.info.name, info.currentTransitionProcessRunning, startupTimeMs);
        Trace.traceEnd(64);
        WindowingModeTransitionInfoSnapshot infoSnapshot = new WindowingModeTransitionInfoSnapshot(info, r, (int) startupTimeMs);
        BackgroundThread.getHandler().post(new Runnable(infoSnapshot) {
            private final /* synthetic */ ActivityMetricsLogger.WindowingModeTransitionInfoSnapshot f$1;

            {
                this.f$1 = r2;
            }

            public final void run() {
                ActivityMetricsLogger.this.lambda$logAppTransitionReportedDrawn$2$ActivityMetricsLogger(this.f$1);
            }
        });
        return infoSnapshot;
    }

    /* access modifiers changed from: private */
    /* renamed from: logAppFullyDrawn */
    public void lambda$logAppTransitionReportedDrawn$2$ActivityMetricsLogger(WindowingModeTransitionInfoSnapshot info) {
        if (info.type == 8 || info.type == 7) {
            StringBuilder sb = this.mStringBuilder;
            sb.setLength(0);
            sb.append("Fully drawn ");
            sb.append(info.launchedActivityShortComponentName);
            sb.append(": ");
            TimeUtils.formatDuration((long) info.windowsFullyDrawnDelayMs, sb);
            Log.i(TAG, sb.toString());
        }
    }

    /* access modifiers changed from: package-private */
    public void logAbortedBgActivityStart(Intent intent, WindowProcessController callerApp, int callingUid, String callingPackage, int callingUidProcState, boolean callingUidHasAnyVisibleWindow, int realCallingUid, int realCallingUidProcState, boolean realCallingUidHasAnyVisibleWindow, boolean comingFromPendingIntent) {
        WindowProcessController windowProcessController = callerApp;
        long nowElapsed = SystemClock.elapsedRealtime();
        long nowUptime = SystemClock.uptimeMillis();
        LogMaker builder = new LogMaker(1513);
        builder.setTimestamp(System.currentTimeMillis());
        builder.addTaggedData(1514, Integer.valueOf(callingUid));
        builder.addTaggedData(1515, callingPackage);
        builder.addTaggedData(1516, Integer.valueOf(ActivityManager.processStateAmToProto(callingUidProcState)));
        builder.addTaggedData(1517, Integer.valueOf(callingUidHasAnyVisibleWindow));
        builder.addTaggedData(1518, Integer.valueOf(realCallingUid));
        builder.addTaggedData(1519, Integer.valueOf(ActivityManager.processStateAmToProto(realCallingUidProcState)));
        builder.addTaggedData(1520, Integer.valueOf(realCallingUidHasAnyVisibleWindow));
        builder.addTaggedData(1527, Integer.valueOf(comingFromPendingIntent));
        if (intent != null) {
            builder.addTaggedData(1528, intent.getAction());
            ComponentName component = intent.getComponent();
            if (component != null) {
                builder.addTaggedData(1526, component.flattenToShortString());
            }
        }
        if (windowProcessController != null) {
            builder.addTaggedData(1529, windowProcessController.mName);
            builder.addTaggedData(1530, Integer.valueOf(ActivityManager.processStateAmToProto(callerApp.getCurrentProcState())));
            builder.addTaggedData(1531, Integer.valueOf(callerApp.hasClientActivities() ? 1 : 0));
            builder.addTaggedData(1532, Integer.valueOf(callerApp.hasForegroundServices() ? 1 : 0));
            builder.addTaggedData(1533, Integer.valueOf(callerApp.hasForegroundActivities() ? 1 : 0));
            builder.addTaggedData(1534, Integer.valueOf(callerApp.hasTopUi() ? 1 : 0));
            builder.addTaggedData(1535, Integer.valueOf(callerApp.hasOverlayUi() ? 1 : 0));
            builder.addTaggedData(1536, Integer.valueOf(callerApp.hasPendingUiClean() ? 1 : 0));
            if (callerApp.getInteractionEventTime() != 0) {
                builder.addTaggedData(UsbTerminalTypes.TERMINAL_EXTERN_ANALOG, Long.valueOf(nowElapsed - callerApp.getInteractionEventTime()));
            }
            if (callerApp.getFgInteractionTime() != 0) {
                builder.addTaggedData(UsbTerminalTypes.TERMINAL_EXTERN_DIGITAL, Long.valueOf(nowElapsed - callerApp.getFgInteractionTime()));
            }
            if (callerApp.getWhenUnimportant() != 0) {
                builder.addTaggedData(UsbTerminalTypes.TERMINAL_EXTERN_LINE, Long.valueOf(nowUptime - callerApp.getWhenUnimportant()));
            }
        }
        this.mMetricsLogger.write(builder);
    }

    /* access modifiers changed from: private */
    public int getTransitionType(WindowingModeTransitionInfo info) {
        if (info.currentTransitionProcessRunning) {
            if (info.startResult == 0) {
                return 8;
            }
            if (info.startResult == 2) {
                return 9;
            }
            return -1;
        } else if (info.startResult == 0 || info.startResult == 2) {
            return 7;
        } else {
            return -1;
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:3:0x0007, code lost:
        r0 = com.android.server.wm.ActivityMetricsLogger.WindowingModeTransitionInfoSnapshot.access$2200(r21).getPid();
        r1 = com.android.server.wm.ActivityMetricsLogger.WindowingModeTransitionInfoSnapshot.access$1400(r21).uid;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void logAppStartMemoryStateCapture(com.android.server.wm.ActivityMetricsLogger.WindowingModeTransitionInfoSnapshot r21) {
        /*
            r20 = this;
            com.android.server.wm.WindowProcessController r0 = r21.processRecord
            if (r0 != 0) goto L_0x0007
            return
        L_0x0007:
            com.android.server.wm.WindowProcessController r0 = r21.processRecord
            int r0 = r0.getPid()
            android.content.pm.ApplicationInfo r1 = r21.applicationInfo
            int r1 = r1.uid
            com.android.server.am.MemoryStatUtil$MemoryStat r14 = com.android.server.am.MemoryStatUtil.readMemoryStatFromFilesystem(r1, r0)
            if (r14 != 0) goto L_0x001c
            return
        L_0x001c:
            r2 = 55
            java.lang.String r4 = r21.processName
            r15 = r21
            java.lang.String r5 = r15.launchedActivityName
            long r6 = r14.pgfault
            long r8 = r14.pgmajfault
            long r10 = r14.rssInBytes
            long r12 = r14.cacheInBytes
            r16 = r12
            long r12 = r14.swapInBytes
            r3 = r1
            r18 = r12
            r12 = r16
            r16 = r14
            r14 = r18
            android.util.StatsLog.write(r2, r3, r4, r5, r6, r8, r10, r12, r14)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.ActivityMetricsLogger.logAppStartMemoryStateCapture(com.android.server.wm.ActivityMetricsLogger$WindowingModeTransitionInfoSnapshot):void");
    }

    /* access modifiers changed from: private */
    public WindowProcessController findProcessForActivity(ActivityRecord launchedActivity) {
        if (launchedActivity != null) {
            return (WindowProcessController) this.mSupervisor.mService.mProcessNames.get(launchedActivity.processName, launchedActivity.appInfo.uid);
        }
        return null;
    }

    private ArtManagerInternal getArtManagerInternal() {
        if (this.mArtManagerInternal == null) {
            this.mArtManagerInternal = (ArtManagerInternal) LocalServices.getService(ArtManagerInternal.class);
        }
        return this.mArtManagerInternal;
    }

    private void startTraces(WindowingModeTransitionInfo info) {
        if (Trace.isTagEnabled(64) && info != null && !info.launchTraceActive) {
            Trace.asyncTraceBegin(64, "launching: " + info.launchedActivity.packageName, 0);
            boolean unused = info.launchTraceActive = true;
        }
    }

    private void stopLaunchTrace(WindowingModeTransitionInfo info) {
        if (info != null && info.launchTraceActive) {
            Trace.asyncTraceEnd(64, "launching: " + info.launchedActivity.packageName, 0);
            boolean unused = info.launchTraceActive = false;
        }
    }

    public ActivityMetricsLaunchObserverRegistry getLaunchObserverRegistry() {
        return this.mLaunchObserver;
    }

    private void launchObserverNotifyIntentStarted(Intent intent) {
        Trace.traceBegin(64, "MetricsLogger:launchObserverNotifyIntentStarted");
        this.mLaunchObserver.onIntentStarted(intent);
        Trace.traceEnd(64);
    }

    private void launchObserverNotifyIntentFailed() {
        Trace.traceBegin(64, "MetricsLogger:launchObserverNotifyIntentFailed");
        this.mLaunchObserver.onIntentFailed();
        Trace.traceEnd(64);
    }

    private void launchObserverNotifyActivityLaunched(WindowingModeTransitionInfo info) {
        Trace.traceBegin(64, "MetricsLogger:launchObserverNotifyActivityLaunched");
        this.mLaunchObserver.onActivityLaunched(convertActivityRecordToProto(info.launchedActivity), convertTransitionTypeToLaunchObserverTemperature(getTransitionType(info)));
        Trace.traceEnd(64);
    }

    private void launchObserverNotifyActivityLaunchCancelled(WindowingModeTransitionInfo info) {
        Trace.traceBegin(64, "MetricsLogger:launchObserverNotifyActivityLaunchCancelled");
        this.mLaunchObserver.onActivityLaunchCancelled(info != null ? convertActivityRecordToProto(info.launchedActivity) : null);
        Trace.traceEnd(64);
    }

    private void launchObserverNotifyActivityLaunchFinished(WindowingModeTransitionInfo info) {
        Trace.traceBegin(64, "MetricsLogger:launchObserverNotifyActivityLaunchFinished");
        this.mLaunchObserver.onActivityLaunchFinished(convertActivityRecordToProto(info.launchedActivity));
        Trace.traceEnd(64);
    }

    @VisibleForTesting
    static byte[] convertActivityRecordToProto(ActivityRecord record) {
        Trace.traceBegin(64, "MetricsLogger:convertActivityRecordToProto");
        ProtoOutputStream protoOutputStream = new ProtoOutputStream(512);
        record.writeToProto(protoOutputStream);
        byte[] bytes = protoOutputStream.getBytes();
        Trace.traceEnd(64);
        return bytes;
    }

    private static int convertTransitionTypeToLaunchObserverTemperature(int transitionType) {
        if (transitionType == 7) {
            return 1;
        }
        if (transitionType == 8) {
            return 2;
        }
        if (transitionType != 9) {
            return -1;
        }
        return 3;
    }
}
