package com.android.server.am;

import android.app.ActivityManager;
import android.app.ApplicationErrorReport;
import android.app.Dialog;
import android.app.IApplicationThread;
import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.content.pm.VersionedPackage;
import android.content.res.CompatibilityInfo;
import android.os.Binder;
import android.os.Debug;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.Trace;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.BoostFramework;
import android.util.DebugUtils;
import android.util.EventLog;
import android.util.SeempLog;
import android.util.Slog;
import android.util.StatsLog;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.procstats.ProcessState;
import com.android.internal.app.procstats.ProcessStats;
import com.android.internal.os.BatteryStatsImpl;
import com.android.internal.os.Zygote;
import com.android.server.am.ProcessList;
import com.android.server.wm.WindowProcessController;
import com.android.server.wm.WindowProcessListener;
import com.miui.server.AccessController;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class ProcessRecord implements WindowProcessListener {
    private static final String TAG = "ActivityManager";
    int adjSeq;
    Object adjSource;
    int adjSourceProcState;
    Object adjTarget;
    String adjType;
    int adjTypeCode;
    Dialog anrDialog;
    final boolean appZygote;
    boolean bad;
    ProcessState baseProcessTracker;
    long boostBeginTime;
    boolean cached;
    String callerPackage;
    CompatibilityInfo compat;
    int completedAdjSeq;
    final ArrayList<ContentProviderConnection> conProviders = new ArrayList<>();
    int connectionGroup;
    int connectionImportance;
    ServiceRecord connectionService;
    final ArraySet<ConnectionRecord> connections = new ArraySet<>();
    boolean containsCycle;
    Dialog crashDialog;
    Runnable crashHandler;
    ActivityManager.ProcessErrorStateInfo crashingReport;
    int curAdj;
    long curCpuTime;
    BatteryStatsImpl.Uid.Proc curProcBatteryStats;
    final ArraySet<BroadcastRecord> curReceivers = new ArraySet<>();
    IBinder.DeathRecipient deathRecipient;
    boolean empty;
    boolean enableBoost;
    ComponentName errorReportReceiver;
    boolean execServicesFg;
    final ArraySet<ServiceRecord> executingServices = new ArraySet<>();
    boolean forceCrashReport;
    Object forcingToImportant;
    int[] gids;
    boolean hasAboveClient;
    boolean hasShownUi;
    boolean hasStartedServices;
    HostingRecord hostingRecord;
    public boolean inFullBackup;
    final ApplicationInfo info;
    long initialIdlePss;
    String instructionSet;
    final boolean isolated;
    String isolatedEntryPoint;
    String[] isolatedEntryPointArgs;
    boolean killed;
    boolean killedByAm;
    long lastActivityTime;
    long lastCachedPss;
    long lastCachedSwapPss;
    int lastCompactAction;
    long lastCompactTime;
    long lastCpuTime;
    long lastLowMemory;
    Debug.MemoryInfo lastMemInfo;
    long lastMemInfoTime;
    long lastProviderTime;
    long lastPss;
    long lastPssTime;
    long lastRequestedGc;
    long lastStateTime;
    long lastSwapPss;
    long lastTopTime;
    int lruSeq;
    final ArraySet<Binder> mAllowBackgroundActivityStartsTokens = new ArraySet<>();
    private ArraySet<Integer> mBoundClientUids = new ArraySet<>();
    private boolean mCrashing;
    private int mCurProcState = 21;
    private int mCurRawAdj;
    private int mCurRawProcState = 21;
    private int mCurSchedGroup;
    private boolean mDebugging;
    private long mFgInteractionTime;
    private int mFgServiceTypes;
    private boolean mHasClientActivities;
    private boolean mHasForegroundActivities;
    private boolean mHasForegroundServices;
    private boolean mHasOverlayUi;
    private boolean mHasTopUi;
    private ActiveInstrumentation mInstr;
    private long mInteractionEventTime;
    private boolean mNotResponding;
    private boolean mPendingUiClean;
    private boolean mPersistent;
    private int mRepFgServiceTypes;
    private int mRepProcState = 21;
    private String mRequiredAbi;
    private final ActivityManagerService mService;
    private boolean mUsingWrapper;
    private long mWhenUnimportant;
    /* access modifiers changed from: private */
    public final WindowProcessController mWindowProcessController;
    int maxAdj;
    int maxProcState;
    int mountMode;
    long nextPssTime;
    boolean notCachedSinceIdle;
    ActivityManager.ProcessErrorStateInfo notRespondingReport;
    boolean pendingStart;
    int pid;
    ArraySet<String> pkgDeps;
    final PackageList pkgList = new PackageList();
    String procStatFile;
    boolean procStateChanged;
    final ProcessList.ProcStateMemTracker procStateMemTracker = new ProcessList.ProcStateMemTracker();
    final String processName;
    int pssProcState = 21;
    int pssStatType;
    final ArrayMap<String, ContentProviderRecord> pubProviders = new ArrayMap<>();
    final ArraySet<ReceiverList> receivers = new ArraySet<>();
    volatile boolean removed;
    int renderThreadTid;
    boolean repForegroundActivities;
    boolean reportLowMemory;
    boolean reportedInteraction;
    int reqCompactAction;
    boolean runningRemoteAnimation;
    int savedPriority;
    String seInfo;
    boolean serviceHighRam;
    boolean serviceb;

    /* renamed from: services  reason: collision with root package name */
    final ArraySet<ServiceRecord> f3services = new ArraySet<>();
    int setAdj;
    int setProcState = 21;
    int setRawAdj;
    int setSchedGroup;
    String shortStringName;
    long startSeq;
    long startTime;
    int startUid;
    boolean starting;
    String stringName;
    boolean systemNoUi;
    IApplicationThread thread;
    boolean treatLikeActivity;
    int trimMemoryLevel;
    final int uid;
    UidRecord uidRecord;
    boolean unlocked;
    final int userId;
    int verifiedAdj;
    Dialog waitDialog;
    boolean waitedForDebugger;
    String waitingToKill;
    boolean waitingToUse;
    boolean whitelistManager;

    final class PackageList {
        final ArrayMap<String, ProcessStats.ProcessStateHolder> mPkgList = new ArrayMap<>();

        PackageList() {
        }

        /* access modifiers changed from: package-private */
        public ProcessStats.ProcessStateHolder put(String key, ProcessStats.ProcessStateHolder value) {
            ProcessRecord.this.mWindowProcessController.addPackage(key);
            return this.mPkgList.put(key, value);
        }

        /* access modifiers changed from: package-private */
        public void clear() {
            this.mPkgList.clear();
            ProcessRecord.this.mWindowProcessController.clearPackageList();
        }

        /* access modifiers changed from: package-private */
        public int size() {
            return this.mPkgList.size();
        }

        /* access modifiers changed from: package-private */
        public String keyAt(int index) {
            return this.mPkgList.keyAt(index);
        }

        public ProcessStats.ProcessStateHolder valueAt(int index) {
            return this.mPkgList.valueAt(index);
        }

        /* access modifiers changed from: package-private */
        public ProcessStats.ProcessStateHolder get(String pkgName) {
            return this.mPkgList.get(pkgName);
        }

        /* access modifiers changed from: package-private */
        public boolean containsKey(Object key) {
            return this.mPkgList.containsKey(key);
        }
    }

    /* access modifiers changed from: package-private */
    public void setStartParams(int startUid2, HostingRecord hostingRecord2, String seInfo2, long startTime2) {
        this.startUid = startUid2;
        this.hostingRecord = hostingRecord2;
        this.seInfo = seInfo2;
        this.startTime = startTime2;
    }

    /* access modifiers changed from: package-private */
    public void dump(PrintWriter pw, String prefix) {
        long nowUptime = SystemClock.uptimeMillis();
        pw.print(prefix);
        pw.print("user #");
        pw.print(this.userId);
        pw.print(" uid=");
        pw.print(this.info.uid);
        if (this.uid != this.info.uid) {
            pw.print(" ISOLATED uid=");
            pw.print(this.uid);
        }
        pw.print(" gids={");
        if (this.gids != null) {
            for (int gi = 0; gi < this.gids.length; gi++) {
                if (gi != 0) {
                    pw.print(", ");
                }
                pw.print(this.gids[gi]);
            }
        }
        pw.println("}");
        pw.print(prefix);
        pw.print("mRequiredAbi=");
        pw.print(this.mRequiredAbi);
        pw.print(" instructionSet=");
        pw.println(this.instructionSet);
        if (this.info.className != null) {
            pw.print(prefix);
            pw.print("class=");
            pw.println(this.info.className);
        }
        if (this.info.manageSpaceActivityName != null) {
            pw.print(prefix);
            pw.print("manageSpaceActivityName=");
            pw.println(this.info.manageSpaceActivityName);
        }
        pw.print(prefix);
        pw.print("dir=");
        pw.print(this.info.sourceDir);
        pw.print(" publicDir=");
        pw.print(this.info.publicSourceDir);
        pw.print(" data=");
        pw.println(this.info.dataDir);
        pw.print(prefix);
        pw.print("packageList={");
        for (int i = 0; i < this.pkgList.size(); i++) {
            if (i > 0) {
                pw.print(", ");
            }
            pw.print(this.pkgList.keyAt(i));
        }
        pw.println("}");
        if (this.pkgDeps != null) {
            pw.print(prefix);
            pw.print("packageDependencies={");
            for (int i2 = 0; i2 < this.pkgDeps.size(); i2++) {
                if (i2 > 0) {
                    pw.print(", ");
                }
                pw.print(this.pkgDeps.valueAt(i2));
            }
            pw.println("}");
        }
        pw.print(prefix);
        pw.print("compat=");
        pw.println(this.compat);
        if (this.mInstr != null) {
            pw.print(prefix);
            pw.print("mInstr=");
            pw.println(this.mInstr);
        }
        pw.print(prefix);
        pw.print("thread=");
        pw.println(this.thread);
        pw.print(prefix);
        pw.print("pid=");
        pw.print(this.pid);
        pw.print(" starting=");
        pw.println(this.starting);
        pw.print(prefix);
        pw.print("lastActivityTime=");
        TimeUtils.formatDuration(this.lastActivityTime, nowUptime, pw);
        pw.print(" lastPssTime=");
        TimeUtils.formatDuration(this.lastPssTime, nowUptime, pw);
        pw.print(" pssStatType=");
        pw.print(this.pssStatType);
        pw.print(" nextPssTime=");
        TimeUtils.formatDuration(this.nextPssTime, nowUptime, pw);
        pw.println();
        pw.print(prefix);
        pw.print("adjSeq=");
        pw.print(this.adjSeq);
        pw.print(" lruSeq=");
        pw.print(this.lruSeq);
        pw.print(" lastPss=");
        DebugUtils.printSizeValue(pw, this.lastPss * 1024);
        pw.print(" lastSwapPss=");
        DebugUtils.printSizeValue(pw, this.lastSwapPss * 1024);
        pw.print(" lastCachedPss=");
        DebugUtils.printSizeValue(pw, this.lastCachedPss * 1024);
        pw.print(" lastCachedSwapPss=");
        DebugUtils.printSizeValue(pw, this.lastCachedSwapPss * 1024);
        pw.println();
        pw.print(prefix);
        pw.print("procStateMemTracker: ");
        this.procStateMemTracker.dumpLine(pw);
        pw.print(prefix);
        pw.print("cached=");
        pw.print(this.cached);
        pw.print(" empty=");
        pw.println(this.empty);
        if (this.serviceb) {
            pw.print(prefix);
            pw.print("serviceb=");
            pw.print(this.serviceb);
            pw.print(" serviceHighRam=");
            pw.println(this.serviceHighRam);
        }
        if (this.notCachedSinceIdle) {
            pw.print(prefix);
            pw.print("notCachedSinceIdle=");
            pw.print(this.notCachedSinceIdle);
            pw.print(" initialIdlePss=");
            pw.println(this.initialIdlePss);
        }
        pw.print(prefix);
        pw.print("oom: max=");
        pw.print(this.maxAdj);
        pw.print(" procState: max=");
        pw.print(this.maxProcState);
        pw.print(" curRaw=");
        pw.print(this.mCurRawAdj);
        pw.print(" setRaw=");
        pw.print(this.setRawAdj);
        pw.print(" cur=");
        pw.print(this.curAdj);
        pw.print(" set=");
        pw.println(this.setAdj);
        pw.print(prefix);
        pw.print("lastCompactTime=");
        pw.print(this.lastCompactTime);
        pw.print(" lastCompactAction=");
        pw.print(this.lastCompactAction);
        pw.print(prefix);
        pw.print("mCurSchedGroup=");
        pw.print(this.mCurSchedGroup);
        pw.print(" setSchedGroup=");
        pw.print(this.setSchedGroup);
        pw.print(" systemNoUi=");
        pw.print(this.systemNoUi);
        pw.print(" trimMemoryLevel=");
        pw.println(this.trimMemoryLevel);
        pw.print(prefix);
        pw.print("curProcState=");
        pw.print(getCurProcState());
        pw.print(" mRepProcState=");
        pw.print(this.mRepProcState);
        pw.print(" pssProcState=");
        pw.print(this.pssProcState);
        pw.print(" setProcState=");
        pw.print(this.setProcState);
        pw.print(" lastStateTime=");
        TimeUtils.formatDuration(this.lastStateTime, nowUptime, pw);
        pw.println();
        if (this.hasShownUi || this.mPendingUiClean || this.hasAboveClient || this.treatLikeActivity) {
            pw.print(prefix);
            pw.print("hasShownUi=");
            pw.print(this.hasShownUi);
            pw.print(" pendingUiClean=");
            pw.print(this.mPendingUiClean);
            pw.print(" hasAboveClient=");
            pw.print(this.hasAboveClient);
            pw.print(" treatLikeActivity=");
            pw.println(this.treatLikeActivity);
        }
        if (!(this.connectionService == null && this.connectionGroup == 0)) {
            pw.print(prefix);
            pw.print("connectionGroup=");
            pw.print(this.connectionGroup);
            pw.print(" Importance=");
            pw.print(this.connectionImportance);
            pw.print(" Service=");
            pw.println(this.connectionService);
        }
        if (hasTopUi() || hasOverlayUi() || this.runningRemoteAnimation) {
            pw.print(prefix);
            pw.print("hasTopUi=");
            pw.print(hasTopUi());
            pw.print(" hasOverlayUi=");
            pw.print(hasOverlayUi());
            pw.print(" runningRemoteAnimation=");
            pw.println(this.runningRemoteAnimation);
        }
        if (this.mHasForegroundServices || this.forcingToImportant != null) {
            pw.print(prefix);
            pw.print("mHasForegroundServices=");
            pw.print(this.mHasForegroundServices);
            pw.print(" forcingToImportant=");
            pw.println(this.forcingToImportant);
        }
        if (this.reportedInteraction || this.mFgInteractionTime != 0) {
            pw.print(prefix);
            pw.print("reportedInteraction=");
            pw.print(this.reportedInteraction);
            if (this.mInteractionEventTime != 0) {
                pw.print(" time=");
                TimeUtils.formatDuration(this.mInteractionEventTime, SystemClock.elapsedRealtime(), pw);
            }
            if (this.mFgInteractionTime != 0) {
                pw.print(" fgInteractionTime=");
                TimeUtils.formatDuration(this.mFgInteractionTime, SystemClock.elapsedRealtime(), pw);
            }
            pw.println();
        }
        if (this.mPersistent || this.removed) {
            pw.print(prefix);
            pw.print("persistent=");
            pw.print(this.mPersistent);
            pw.print(" removed=");
            pw.println(this.removed);
        }
        if (this.mHasClientActivities || this.mHasForegroundActivities || this.repForegroundActivities) {
            pw.print(prefix);
            pw.print("hasClientActivities=");
            pw.print(this.mHasClientActivities);
            pw.print(" foregroundActivities=");
            pw.print(this.mHasForegroundActivities);
            pw.print(" (rep=");
            pw.print(this.repForegroundActivities);
            pw.println(")");
        }
        if (this.lastProviderTime > 0) {
            pw.print(prefix);
            pw.print("lastProviderTime=");
            TimeUtils.formatDuration(this.lastProviderTime, nowUptime, pw);
            pw.println();
        }
        if (this.lastTopTime > 0) {
            pw.print(prefix);
            pw.print("lastTopTime=");
            TimeUtils.formatDuration(this.lastTopTime, nowUptime, pw);
            pw.println();
        }
        if (this.hasStartedServices) {
            pw.print(prefix);
            pw.print("hasStartedServices=");
            pw.println(this.hasStartedServices);
        }
        if (this.pendingStart) {
            pw.print(prefix);
            pw.print("pendingStart=");
            pw.println(this.pendingStart);
        }
        pw.print(prefix);
        pw.print("startSeq=");
        pw.println(this.startSeq);
        pw.print(prefix);
        pw.print("mountMode=");
        pw.println(DebugUtils.valueToString(Zygote.class, "MOUNT_EXTERNAL_", this.mountMode));
        if (this.setProcState > 11) {
            pw.print(prefix);
            pw.print("lastCpuTime=");
            pw.print(this.lastCpuTime);
            if (this.lastCpuTime > 0) {
                pw.print(" timeUsed=");
                TimeUtils.formatDuration(this.curCpuTime - this.lastCpuTime, pw);
            }
            pw.print(" whenUnimportant=");
            TimeUtils.formatDuration(this.mWhenUnimportant - nowUptime, pw);
            pw.println();
        }
        pw.print(prefix);
        pw.print("lastRequestedGc=");
        TimeUtils.formatDuration(this.lastRequestedGc, nowUptime, pw);
        pw.print(" lastLowMemory=");
        TimeUtils.formatDuration(this.lastLowMemory, nowUptime, pw);
        pw.print(" reportLowMemory=");
        pw.println(this.reportLowMemory);
        if (this.killed || this.killedByAm || this.waitingToKill != null) {
            pw.print(prefix);
            pw.print("killed=");
            pw.print(this.killed);
            pw.print(" killedByAm=");
            pw.print(this.killedByAm);
            pw.print(" waitingToKill=");
            pw.println(this.waitingToKill);
        }
        if (this.mDebugging || this.mCrashing || this.crashDialog != null || this.mNotResponding || this.anrDialog != null || this.bad) {
            pw.print(prefix);
            pw.print("mDebugging=");
            pw.print(this.mDebugging);
            pw.print(" mCrashing=");
            pw.print(this.mCrashing);
            pw.print(" ");
            pw.print(this.crashDialog);
            pw.print(" mNotResponding=");
            pw.print(this.mNotResponding);
            pw.print(" ");
            pw.print(this.anrDialog);
            pw.print(" bad=");
            pw.print(this.bad);
            if (this.errorReportReceiver != null) {
                pw.print(" errorReportReceiver=");
                pw.print(this.errorReportReceiver.flattenToShortString());
            }
            pw.println();
        }
        if (this.whitelistManager) {
            pw.print(prefix);
            pw.print("whitelistManager=");
            pw.println(this.whitelistManager);
        }
        if (!(this.isolatedEntryPoint == null && this.isolatedEntryPointArgs == null)) {
            pw.print(prefix);
            pw.print("isolatedEntryPoint=");
            pw.println(this.isolatedEntryPoint);
            pw.print(prefix);
            pw.print("isolatedEntryPointArgs=");
            pw.println(Arrays.toString(this.isolatedEntryPointArgs));
        }
        if (this.callerPackage != null) {
            pw.print(prefix);
            pw.print("callerPackage=");
            pw.println(this.callerPackage);
        }
        if (this.waitingToUse) {
            pw.print(prefix);
            pw.print("waitingToUse=");
            pw.println(this.waitingToUse);
        }
        this.mWindowProcessController.dump(pw, prefix);
        if (this.f3services.size() > 0) {
            pw.print(prefix);
            pw.println("Services:");
            for (int i3 = 0; i3 < this.f3services.size(); i3++) {
                pw.print(prefix);
                pw.print("  - ");
                pw.println(this.f3services.valueAt(i3));
            }
        }
        if (this.executingServices.size() > 0) {
            pw.print(prefix);
            pw.print("Executing Services (fg=");
            pw.print(this.execServicesFg);
            pw.println(")");
            for (int i4 = 0; i4 < this.executingServices.size(); i4++) {
                pw.print(prefix);
                pw.print("  - ");
                pw.println(this.executingServices.valueAt(i4));
            }
        }
        if (this.connections.size() > 0) {
            pw.print(prefix);
            pw.println("Connections:");
            for (int i5 = 0; i5 < this.connections.size(); i5++) {
                pw.print(prefix);
                pw.print("  - ");
                pw.println(this.connections.valueAt(i5));
            }
        }
        if (this.pubProviders.size() > 0) {
            pw.print(prefix);
            pw.println("Published Providers:");
            for (int i6 = 0; i6 < this.pubProviders.size(); i6++) {
                pw.print(prefix);
                pw.print("  - ");
                pw.println(this.pubProviders.keyAt(i6));
                pw.print(prefix);
                pw.print("    -> ");
                pw.println(this.pubProviders.valueAt(i6));
            }
        }
        if (this.conProviders.size() > 0) {
            pw.print(prefix);
            pw.println("Connected Providers:");
            for (int i7 = 0; i7 < this.conProviders.size(); i7++) {
                pw.print(prefix);
                pw.print("  - ");
                pw.println(this.conProviders.get(i7).toShortString());
            }
        }
        if (!this.curReceivers.isEmpty()) {
            pw.print(prefix);
            pw.println("Current Receivers:");
            for (int i8 = 0; i8 < this.curReceivers.size(); i8++) {
                pw.print(prefix);
                pw.print("  - ");
                pw.println(this.curReceivers.valueAt(i8));
            }
        }
        if (this.receivers.size() > 0) {
            pw.print(prefix);
            pw.println("Receivers:");
            for (int i9 = 0; i9 < this.receivers.size(); i9++) {
                pw.print(prefix);
                pw.print("  - ");
                pw.println(this.receivers.valueAt(i9));
            }
        }
        if (this.mAllowBackgroundActivityStartsTokens.size() > 0) {
            pw.print(prefix);
            pw.println("Background activity start whitelist tokens:");
            for (int i10 = 0; i10 < this.mAllowBackgroundActivityStartsTokens.size(); i10++) {
                pw.print(prefix);
                pw.print("  - ");
                pw.println(this.mAllowBackgroundActivityStartsTokens.valueAt(i10));
            }
        }
    }

    ProcessRecord(ActivityManagerService _service, ApplicationInfo _info, String _processName, int _uid) {
        this.mService = _service;
        this.info = _info;
        boolean z = true;
        this.isolated = _info.uid != _uid;
        this.appZygote = (UserHandle.getAppId(_uid) < 90000 || UserHandle.getAppId(_uid) > 98999) ? false : z;
        this.uid = _uid;
        this.userId = UserHandle.getUserId(_uid);
        this.processName = _processName;
        this.maxAdj = 1001;
        this.maxProcState = 21;
        this.setRawAdj = ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
        this.mCurRawAdj = ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
        this.verifiedAdj = ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
        this.setAdj = ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
        this.curAdj = ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION;
        this.mPersistent = false;
        this.removed = false;
        long uptimeMillis = SystemClock.uptimeMillis();
        this.nextPssTime = uptimeMillis;
        this.lastPssTime = uptimeMillis;
        this.lastStateTime = uptimeMillis;
        this.mWindowProcessController = new WindowProcessController(this.mService.mActivityTaskManager, this.info, this.processName, this.uid, this.userId, this, this);
        this.pkgList.put(_info.packageName, new ProcessStats.ProcessStateHolder(_info.longVersionCode));
    }

    public void setPid(int _pid) {
        this.pid = _pid;
        this.mWindowProcessController.setPid(this.pid);
        this.procStatFile = null;
        this.shortStringName = null;
        this.stringName = null;
    }

    public void makeActive(IApplicationThread _thread, ProcessStatsService tracker) {
        SeempLog.record_str(386, "app_uid=" + this.uid + ",app_pid=" + this.pid + ",oom_adj=" + this.curAdj + ",setAdj=" + this.setAdj + ",hasShownUi=" + (this.hasShownUi ? 1 : 0) + ",cached=" + (this.cached ? 1 : 0) + ",fA=" + (this.mHasForegroundActivities ? 1 : 0) + ",fS=" + (this.mHasForegroundServices ? 1 : 0) + ",systemNoUi=" + (this.systemNoUi ? 1 : 0) + ",curSchedGroup=" + this.mCurSchedGroup + ",curProcState=" + getCurProcState() + ",setProcState=" + this.setProcState + ",killed=" + (this.killed ? 1 : 0) + ",killedByAm=" + (this.killedByAm ? 1 : 0) + ",isDebugging=" + (isDebugging() ? 1 : 0));
        if (this.thread == null) {
            ProcessState origBase = this.baseProcessTracker;
            if (origBase != null) {
                origBase.setState(-1, tracker.getMemFactorLocked(), SystemClock.uptimeMillis(), this.pkgList.mPkgList);
                for (int ipkg = this.pkgList.size() - 1; ipkg >= 0; ipkg--) {
                    StatsLog.write(3, this.uid, this.processName, this.pkgList.keyAt(ipkg), ActivityManager.processStateAmToProto(-1), this.pkgList.valueAt(ipkg).appVersion);
                }
                origBase.makeInactive();
            }
            this.baseProcessTracker = tracker.getProcessStateLocked(this.info.packageName, this.info.uid, this.info.longVersionCode, this.processName);
            this.baseProcessTracker.makeActive();
            for (int i = 0; i < this.pkgList.size(); i++) {
                ProcessStats.ProcessStateHolder holder = this.pkgList.valueAt(i);
                if (!(holder.state == null || holder.state == origBase)) {
                    holder.state.makeInactive();
                }
                tracker.updateProcessStateHolderLocked(holder, this.pkgList.keyAt(i), this.info.uid, this.info.longVersionCode, this.processName);
                if (holder.state != this.baseProcessTracker) {
                    holder.state.makeActive();
                }
            }
        }
        this.thread = _thread;
        this.mWindowProcessController.setThread(this.thread);
    }

    public void makeInactive(ProcessStatsService tracker) {
        SeempLog.record_str(387, "app_uid=" + this.uid + ",app_pid=" + this.pid + ",oom_adj=" + this.curAdj + ",setAdj=" + this.setAdj + ",hasShownUi=" + (this.hasShownUi ? 1 : 0) + ",cached=" + (this.cached ? 1 : 0) + ",fA=" + (this.mHasForegroundActivities ? 1 : 0) + ",fS=" + (this.mHasForegroundServices ? 1 : 0) + ",systemNoUi=" + (this.systemNoUi ? 1 : 0) + ",curSchedGroup=" + this.mCurSchedGroup + ",curProcState=" + getCurProcState() + ",setProcState=" + this.setProcState + ",killed=" + (this.killed ? 1 : 0) + ",killedByAm=" + (this.killedByAm ? 1 : 0) + ",isDebugging=" + (isDebugging() ? 1 : 0));
        this.thread = null;
        this.mWindowProcessController.setThread((IApplicationThread) null);
        ProcessState origBase = this.baseProcessTracker;
        if (origBase != null) {
            origBase.setState(-1, tracker.getMemFactorLocked(), SystemClock.uptimeMillis(), this.pkgList.mPkgList);
            for (int ipkg = this.pkgList.size() - 1; ipkg >= 0; ipkg--) {
                StatsLog.write(3, this.uid, this.processName, this.pkgList.keyAt(ipkg), ActivityManager.processStateAmToProto(-1), this.pkgList.valueAt(ipkg).appVersion);
            }
            origBase.makeInactive();
            this.baseProcessTracker = null;
            for (int i = 0; i < this.pkgList.size(); i++) {
                ProcessStats.ProcessStateHolder holder = this.pkgList.valueAt(i);
                if (!(holder.state == null || holder.state == origBase)) {
                    holder.state.makeInactive();
                }
                holder.pkg = null;
                holder.state = null;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public boolean hasActivities() {
        return this.mWindowProcessController.hasActivities();
    }

    /* access modifiers changed from: package-private */
    public boolean hasActivitiesOrRecentTasks() {
        return this.mWindowProcessController.hasActivitiesOrRecentTasks();
    }

    /* access modifiers changed from: package-private */
    public boolean hasRecentTasks() {
        return this.mWindowProcessController.hasRecentTasks();
    }

    public boolean isInterestingToUserLocked() {
        if (this.mWindowProcessController.isInterestingToUser()) {
            return true;
        }
        int servicesSize = this.f3services.size();
        for (int i = 0; i < servicesSize; i++) {
            ServiceRecord r = this.f3services.valueAt(i);
            if (r != null && r.isForeground) {
                return true;
            }
        }
        return false;
    }

    public void unlinkDeathRecipient() {
        IApplicationThread iApplicationThread;
        if (!(this.deathRecipient == null || (iApplicationThread = this.thread) == null)) {
            iApplicationThread.asBinder().unlinkToDeath(this.deathRecipient, 0);
        }
        this.deathRecipient = null;
    }

    /* access modifiers changed from: package-private */
    public void updateHasAboveClientLocked() {
        this.hasAboveClient = false;
        for (int i = this.connections.size() - 1; i >= 0; i--) {
            if ((this.connections.valueAt(i).flags & 8) != 0) {
                this.hasAboveClient = true;
                return;
            }
        }
    }

    /* access modifiers changed from: package-private */
    public int modifyRawOomAdj(int adj) {
        if (!this.hasAboveClient || adj < 0) {
            return adj;
        }
        if (adj < 100) {
            return 100;
        }
        if (adj < 200) {
            return 200;
        }
        if (adj < 250) {
            return ScreenRotationAnimationInjector.COVER_OFFSET;
        }
        if (adj < 900) {
            return 900;
        }
        if (adj < 999) {
            return adj + 1;
        }
        return adj;
    }

    /* access modifiers changed from: package-private */
    public void scheduleCrash(String message) {
        if (!this.killedByAm && this.thread != null) {
            if (this.pid == Process.myPid()) {
                Slog.w(TAG, "scheduleCrash: trying to crash system process!");
                return;
            }
            long ident = Binder.clearCallingIdentity();
            try {
                this.thread.scheduleCrash(message);
            } catch (RemoteException e) {
                kill("scheduleCrash for '" + message + "' failed", true);
            } catch (Throwable th) {
                Binder.restoreCallingIdentity(ident);
                throw th;
            }
            Binder.restoreCallingIdentity(ident);
        }
    }

    /* access modifiers changed from: package-private */
    public void kill(String reason, boolean noisy) {
        if (!this.killedByAm) {
            Trace.traceBegin(64, "kill");
            BoostFramework ux_perf = new BoostFramework();
            if (this.mService != null && (noisy || this.info.uid == this.mService.mCurOomAdjUid)) {
                ActivityManagerService activityManagerService = this.mService;
                activityManagerService.reportUidInfoMessageLocked(TAG, "Killing " + toShortString() + " (adj " + this.setAdj + "): " + reason, this.info.uid);
            }
            if (this.pid > 0) {
                ProcessRecordInjector.reportKillProcessEvent(this, reason);
                EventLog.writeEvent(EventLogTags.AM_KILL, new Object[]{Integer.valueOf(this.userId), Integer.valueOf(this.pid), this.processName, Integer.valueOf(this.setAdj), reason});
                Process.killProcessQuiet(this.pid);
                ProcessList.killProcessGroup(this.uid, this.pid);
            } else {
                this.pendingStart = false;
            }
            if (!this.mPersistent) {
                this.killed = true;
                this.killedByAm = true;
            }
            ActivityManagerService activityManagerService2 = this.mService;
            if (!ActivityManagerService.mForceStopKill) {
                ux_perf.perfUXEngine_events(4, 0, this.processName, 0);
                ux_perf.perfHint(4243, this.processName, this.pid, 0);
            } else {
                ActivityManagerService activityManagerService3 = this.mService;
                ActivityManagerService.mForceStopKill = false;
            }
            Trace.traceEnd(64);
        }
    }

    public void writeToProto(ProtoOutputStream proto, long fieldId) {
        writeToProto(proto, fieldId, -1);
    }

    public void writeToProto(ProtoOutputStream proto, long fieldId, int lruIndex) {
        long token = proto.start(fieldId);
        proto.write(1120986464257L, this.pid);
        proto.write(1138166333442L, this.processName);
        proto.write(1120986464259L, this.info.uid);
        if (UserHandle.getAppId(this.info.uid) >= 10000) {
            proto.write(1120986464260L, this.userId);
            proto.write(1120986464261L, UserHandle.getAppId(this.info.uid));
        }
        if (this.uid != this.info.uid) {
            proto.write(1120986464262L, UserHandle.getAppId(this.uid));
        }
        proto.write(1133871366151L, this.mPersistent);
        if (lruIndex >= 0) {
            proto.write(1120986464264L, lruIndex);
        }
        proto.end(token);
    }

    public String toShortString() {
        String str = this.shortStringName;
        if (str != null) {
            return str;
        }
        StringBuilder sb = new StringBuilder(128);
        toShortString(sb);
        String sb2 = sb.toString();
        this.shortStringName = sb2;
        return sb2;
    }

    /* access modifiers changed from: package-private */
    public void toShortString(StringBuilder sb) {
        sb.append(this.pid);
        sb.append(':');
        sb.append(this.processName);
        sb.append('/');
        if (this.info.uid < 10000) {
            sb.append(this.uid);
            return;
        }
        sb.append('u');
        sb.append(this.userId);
        int appId = UserHandle.getAppId(this.info.uid);
        if (appId >= 10000) {
            sb.append('a');
            sb.append(appId + ScreenRotationAnimationInjector.BLACK_SURFACE_INVALID_POSITION);
        } else {
            sb.append('s');
            sb.append(appId);
        }
        if (this.uid != this.info.uid) {
            sb.append('i');
            sb.append(UserHandle.getAppId(this.uid) - 99000);
        }
    }

    public String toString() {
        String str = this.stringName;
        if (str != null) {
            return str;
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append("ProcessRecord{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(' ');
        toShortString(sb);
        sb.append('}');
        String sb2 = sb.toString();
        this.stringName = sb2;
        return sb2;
    }

    public String makeAdjReason() {
        if (this.adjSource == null && this.adjTarget == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(128);
        sb.append(' ');
        Object obj = this.adjTarget;
        if (obj instanceof ComponentName) {
            sb.append(((ComponentName) obj).flattenToShortString());
        } else if (obj != null) {
            sb.append(obj.toString());
        } else {
            sb.append("{null}");
        }
        sb.append("<=");
        Object obj2 = this.adjSource;
        if (obj2 instanceof ProcessRecord) {
            sb.append("Proc{");
            sb.append(((ProcessRecord) this.adjSource).toShortString());
            sb.append("}");
        } else if (obj2 != null) {
            sb.append(obj2.toString());
        } else {
            sb.append("{null}");
        }
        return sb.toString();
    }

    public boolean addPackage(String pkg, long versionCode, ProcessStatsService tracker) {
        if (this.pkgList.containsKey(pkg)) {
            return false;
        }
        ProcessStats.ProcessStateHolder holder = new ProcessStats.ProcessStateHolder(versionCode);
        if (this.baseProcessTracker != null) {
            tracker.updateProcessStateHolderLocked(holder, pkg, this.info.uid, versionCode, this.processName);
            this.pkgList.put(pkg, holder);
            if (holder.state == this.baseProcessTracker) {
                return true;
            }
            holder.state.makeActive();
            return true;
        }
        this.pkgList.put(pkg, holder);
        return true;
    }

    public int getSetAdjWithServices() {
        if (this.setAdj < 900 || !this.hasStartedServices) {
            return this.setAdj;
        }
        return ScreenRotationAnimationInjector.COVER_EGE;
    }

    public void forceProcessStateUpTo(int newState) {
        if (this.mRepProcState > newState) {
            this.mRepProcState = newState;
            setCurProcState(newState);
            setCurRawProcState(newState);
            for (int ipkg = this.pkgList.size() - 1; ipkg >= 0; ipkg--) {
                StatsLog.write(3, this.uid, this.processName, this.pkgList.keyAt(ipkg), ActivityManager.processStateAmToProto(this.mRepProcState), this.pkgList.valueAt(ipkg).appVersion);
            }
        }
    }

    public void resetPackageList(ProcessStatsService tracker) {
        int N = this.pkgList.size();
        if (this.baseProcessTracker != null) {
            this.baseProcessTracker.setState(-1, tracker.getMemFactorLocked(), SystemClock.uptimeMillis(), this.pkgList.mPkgList);
            for (int ipkg = this.pkgList.size() - 1; ipkg >= 0; ipkg--) {
                StatsLog.write(3, this.uid, this.processName, this.pkgList.keyAt(ipkg), ActivityManager.processStateAmToProto(-1), this.pkgList.valueAt(ipkg).appVersion);
            }
            if (N != 1) {
                for (int i = 0; i < N; i++) {
                    ProcessStats.ProcessStateHolder holder = this.pkgList.valueAt(i);
                    if (!(holder.state == null || holder.state == this.baseProcessTracker)) {
                        holder.state.makeInactive();
                    }
                }
                this.pkgList.clear();
                ProcessStats.ProcessStateHolder holder2 = new ProcessStats.ProcessStateHolder(this.info.longVersionCode);
                tracker.updateProcessStateHolderLocked(holder2, this.info.packageName, this.info.uid, this.info.longVersionCode, this.processName);
                this.pkgList.put(this.info.packageName, holder2);
                if (holder2.state != this.baseProcessTracker) {
                    holder2.state.makeActive();
                }
            }
        } else if (N != 1) {
            this.pkgList.clear();
            this.pkgList.put(this.info.packageName, new ProcessStats.ProcessStateHolder(this.info.longVersionCode));
        }
    }

    public String[] getPackageList() {
        int size = this.pkgList.size();
        if (size == 0) {
            return null;
        }
        String[] list = new String[size];
        for (int i = 0; i < this.pkgList.size(); i++) {
            list[i] = this.pkgList.keyAt(i);
        }
        return list;
    }

    public List<VersionedPackage> getPackageListWithVersionCode() {
        if (this.pkgList.size() == 0) {
            return null;
        }
        List<VersionedPackage> list = new ArrayList<>();
        for (int i = 0; i < this.pkgList.size(); i++) {
            list.add(new VersionedPackage(this.pkgList.keyAt(i), this.pkgList.valueAt(i).appVersion));
        }
        return list;
    }

    /* access modifiers changed from: package-private */
    public WindowProcessController getWindowProcessController() {
        return this.mWindowProcessController;
    }

    /* access modifiers changed from: package-private */
    public void setCurrentSchedulingGroup(int curSchedGroup) {
        this.mCurSchedGroup = curSchedGroup;
        this.mWindowProcessController.setCurrentSchedulingGroup(curSchedGroup);
    }

    /* access modifiers changed from: package-private */
    public int getCurrentSchedulingGroup() {
        return this.mCurSchedGroup;
    }

    /* access modifiers changed from: package-private */
    public void setCurProcState(int curProcState) {
        this.mCurProcState = curProcState;
        this.mWindowProcessController.setCurrentProcState(this.mCurProcState);
    }

    /* access modifiers changed from: package-private */
    public int getCurProcState() {
        return this.mCurProcState;
    }

    /* access modifiers changed from: package-private */
    public void setCurRawProcState(int curRawProcState) {
        this.mCurRawProcState = curRawProcState;
    }

    /* access modifiers changed from: package-private */
    public int getCurRawProcState() {
        return this.mCurRawProcState;
    }

    /* access modifiers changed from: package-private */
    public void setReportedProcState(int repProcState) {
        this.mRepProcState = repProcState;
        for (int ipkg = this.pkgList.size() - 1; ipkg >= 0; ipkg--) {
            StatsLog.write(3, this.uid, this.processName, this.pkgList.keyAt(ipkg), ActivityManager.processStateAmToProto(this.mRepProcState), this.pkgList.valueAt(ipkg).appVersion);
        }
        this.mWindowProcessController.setReportedProcState(repProcState);
    }

    /* access modifiers changed from: package-private */
    public int getReportedProcState() {
        return this.mRepProcState;
    }

    /* access modifiers changed from: package-private */
    public void setCrashing(boolean crashing) {
        this.mCrashing = crashing;
        this.mWindowProcessController.setCrashing(crashing);
    }

    /* access modifiers changed from: package-private */
    public boolean isCrashing() {
        return this.mCrashing;
    }

    /* access modifiers changed from: package-private */
    public void setNotResponding(boolean notResponding) {
        this.mNotResponding = notResponding;
        this.mWindowProcessController.setNotResponding(notResponding);
    }

    /* access modifiers changed from: package-private */
    public boolean isNotResponding() {
        return this.mNotResponding;
    }

    /* access modifiers changed from: package-private */
    public void setPersistent(boolean persistent) {
        this.mPersistent = persistent;
        this.mWindowProcessController.setPersistent(persistent);
    }

    /* access modifiers changed from: package-private */
    public boolean isPersistent() {
        return this.mPersistent;
    }

    public void setRequiredAbi(String requiredAbi) {
        this.mRequiredAbi = requiredAbi;
        this.mWindowProcessController.setRequiredAbi(requiredAbi);
    }

    /* access modifiers changed from: package-private */
    public String getRequiredAbi() {
        return this.mRequiredAbi;
    }

    /* access modifiers changed from: package-private */
    public void setHasForegroundServices(boolean hasForegroundServices, int fgServiceTypes) {
        this.mHasForegroundServices = hasForegroundServices;
        this.mFgServiceTypes = fgServiceTypes;
        this.mWindowProcessController.setHasForegroundServices(hasForegroundServices);
    }

    /* access modifiers changed from: package-private */
    public boolean hasForegroundServices() {
        return this.mHasForegroundServices;
    }

    /* access modifiers changed from: package-private */
    public boolean hasLocationForegroundServices() {
        return this.mHasForegroundServices && (this.mFgServiceTypes & 8) != 0;
    }

    /* access modifiers changed from: package-private */
    public int getForegroundServiceTypes() {
        if (this.mHasForegroundServices) {
            return this.mFgServiceTypes;
        }
        return 0;
    }

    /* access modifiers changed from: package-private */
    public int getReportedForegroundServiceTypes() {
        return this.mRepFgServiceTypes;
    }

    /* access modifiers changed from: package-private */
    public void setReportedForegroundServiceTypes(int foregroundServiceTypes) {
        this.mRepFgServiceTypes = foregroundServiceTypes;
    }

    /* access modifiers changed from: package-private */
    public void setHasForegroundActivities(boolean hasForegroundActivities) {
        this.mHasForegroundActivities = hasForegroundActivities;
        this.mWindowProcessController.setHasForegroundActivities(hasForegroundActivities);
    }

    /* access modifiers changed from: package-private */
    public boolean hasForegroundActivities() {
        return this.mHasForegroundActivities;
    }

    /* access modifiers changed from: package-private */
    public void setHasClientActivities(boolean hasClientActivities) {
        this.mHasClientActivities = hasClientActivities;
        this.mWindowProcessController.setHasClientActivities(hasClientActivities);
    }

    /* access modifiers changed from: package-private */
    public boolean hasClientActivities() {
        return this.mHasClientActivities;
    }

    /* access modifiers changed from: package-private */
    public void setHasTopUi(boolean hasTopUi) {
        this.mHasTopUi = hasTopUi;
        this.mWindowProcessController.setHasTopUi(hasTopUi);
    }

    /* access modifiers changed from: package-private */
    public boolean hasTopUi() {
        return this.mHasTopUi;
    }

    /* access modifiers changed from: package-private */
    public void setHasOverlayUi(boolean hasOverlayUi) {
        this.mHasOverlayUi = hasOverlayUi;
        this.mWindowProcessController.setHasOverlayUi(hasOverlayUi);
    }

    /* access modifiers changed from: package-private */
    public boolean hasOverlayUi() {
        return this.mHasOverlayUi;
    }

    /* access modifiers changed from: package-private */
    public void setInteractionEventTime(long interactionEventTime) {
        this.mInteractionEventTime = interactionEventTime;
        this.mWindowProcessController.setInteractionEventTime(interactionEventTime);
    }

    /* access modifiers changed from: package-private */
    public long getInteractionEventTime() {
        return this.mInteractionEventTime;
    }

    /* access modifiers changed from: package-private */
    public void setFgInteractionTime(long fgInteractionTime) {
        this.mFgInteractionTime = fgInteractionTime;
        this.mWindowProcessController.setFgInteractionTime(fgInteractionTime);
    }

    /* access modifiers changed from: package-private */
    public long getFgInteractionTime() {
        return this.mFgInteractionTime;
    }

    /* access modifiers changed from: package-private */
    public void setWhenUnimportant(long whenUnimportant) {
        this.mWhenUnimportant = whenUnimportant;
        this.mWindowProcessController.setWhenUnimportant(whenUnimportant);
    }

    /* access modifiers changed from: package-private */
    public long getWhenUnimportant() {
        return this.mWhenUnimportant;
    }

    /* access modifiers changed from: package-private */
    public void setDebugging(boolean debugging) {
        this.mDebugging = debugging;
        this.mWindowProcessController.setDebugging(debugging);
    }

    /* access modifiers changed from: package-private */
    public boolean isDebugging() {
        return this.mDebugging;
    }

    /* access modifiers changed from: package-private */
    public void setUsingWrapper(boolean usingWrapper) {
        this.mUsingWrapper = usingWrapper;
        this.mWindowProcessController.setUsingWrapper(usingWrapper);
    }

    /* access modifiers changed from: package-private */
    public boolean isUsingWrapper() {
        return this.mUsingWrapper;
    }

    /* access modifiers changed from: package-private */
    public void addAllowBackgroundActivityStartsToken(Binder entity) {
        if (entity != null) {
            this.mAllowBackgroundActivityStartsTokens.add(entity);
            this.mWindowProcessController.setAllowBackgroundActivityStarts(true);
        }
    }

    /* access modifiers changed from: package-private */
    public void removeAllowBackgroundActivityStartsToken(Binder entity) {
        if (entity != null) {
            this.mAllowBackgroundActivityStartsTokens.remove(entity);
            this.mWindowProcessController.setAllowBackgroundActivityStarts(!this.mAllowBackgroundActivityStartsTokens.isEmpty());
        }
    }

    /* access modifiers changed from: package-private */
    public void addBoundClientUid(int clientUid) {
        this.mBoundClientUids.add(Integer.valueOf(clientUid));
        this.mWindowProcessController.setBoundClientUids(this.mBoundClientUids);
    }

    /* access modifiers changed from: package-private */
    public void updateBoundClientUids() {
        if (this.f3services.isEmpty()) {
            clearBoundClientUids();
            return;
        }
        ArraySet<Integer> boundClientUids = new ArraySet<>();
        int K = this.f3services.size();
        for (int j = 0; j < K; j++) {
            ArrayMap<IBinder, ArrayList<ConnectionRecord>> conns = this.f3services.valueAt(j).getConnections();
            int N = conns.size();
            for (int conni = 0; conni < N; conni++) {
                ArrayList<ConnectionRecord> c = conns.valueAt(conni);
                for (int i = 0; i < c.size(); i++) {
                    boundClientUids.add(Integer.valueOf(c.get(i).clientUid));
                }
            }
        }
        this.mBoundClientUids = boundClientUids;
        this.mWindowProcessController.setBoundClientUids(this.mBoundClientUids);
    }

    /* access modifiers changed from: package-private */
    public void addBoundClientUidsOfNewService(ServiceRecord sr) {
        if (sr != null) {
            ArrayMap<IBinder, ArrayList<ConnectionRecord>> conns = sr.getConnections();
            for (int conni = conns.size() - 1; conni >= 0; conni--) {
                ArrayList<ConnectionRecord> c = conns.valueAt(conni);
                for (int i = 0; i < c.size(); i++) {
                    this.mBoundClientUids.add(Integer.valueOf(c.get(i).clientUid));
                }
            }
            this.mWindowProcessController.setBoundClientUids(this.mBoundClientUids);
        }
    }

    /* access modifiers changed from: package-private */
    public void clearBoundClientUids() {
        this.mBoundClientUids.clear();
        this.mWindowProcessController.setBoundClientUids(this.mBoundClientUids);
    }

    /* access modifiers changed from: package-private */
    public void setActiveInstrumentation(ActiveInstrumentation instr) {
        this.mInstr = instr;
        boolean z = true;
        boolean isInstrumenting = instr != null;
        WindowProcessController windowProcessController = this.mWindowProcessController;
        if (!isInstrumenting || !instr.mHasBackgroundActivityStartsPermission) {
            z = false;
        }
        windowProcessController.setInstrumenting(isInstrumenting, z);
    }

    /* access modifiers changed from: package-private */
    public ActiveInstrumentation getActiveInstrumentation() {
        return this.mInstr;
    }

    /* access modifiers changed from: package-private */
    public void setCurRawAdj(int curRawAdj) {
        this.mCurRawAdj = curRawAdj;
        this.mWindowProcessController.setPerceptible(curRawAdj <= 200);
    }

    /* access modifiers changed from: package-private */
    public int getCurRawAdj() {
        return this.mCurRawAdj;
    }

    public void clearProfilerIfNeeded() {
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                if (!(this.mService.mProfileData.getProfileProc() == null || this.mService.mProfileData.getProfilerInfo() == null)) {
                    if (this.mService.mProfileData.getProfileProc() == this) {
                        this.mService.clearProfilerLocked();
                        ActivityManagerService.resetPriorityAfterLockedSection();
                        return;
                    }
                }
                ActivityManagerService.resetPriorityAfterLockedSection();
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
    }

    public void updateServiceConnectionActivities() {
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                this.mService.mServices.updateServiceConnectionActivitiesLocked(this);
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    public void setPendingUiClean(boolean pendingUiClean) {
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                this.mPendingUiClean = pendingUiClean;
                this.mWindowProcessController.setPendingUiClean(pendingUiClean);
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    /* access modifiers changed from: package-private */
    public boolean hasPendingUiClean() {
        return this.mPendingUiClean;
    }

    public void setPendingUiCleanAndForceProcessStateUpTo(int newState) {
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                setPendingUiClean(true);
                forceProcessStateUpTo(newState);
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    public void updateProcessInfo(boolean updateServiceConnectionActivities, boolean activityChange, boolean updateOomAdj) {
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                if (updateServiceConnectionActivities) {
                    this.mService.mServices.updateServiceConnectionActivitiesLocked(this);
                }
                this.mService.mProcessList.updateLruProcessLocked(this, activityChange, (ProcessRecord) null);
                if (updateOomAdj) {
                    this.mService.updateOomAdjLocked("updateOomAdj_activityChange");
                }
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    public boolean isRemoved() {
        return this.removed;
    }

    public long getCpuTime() {
        return this.mService.mProcessCpuTracker.getCpuTimeForPid(this.pid);
    }

    public void onStartActivity(int topProcessState, boolean setProfileProc, String packageName, long versionCode) {
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                this.waitingToKill = null;
                if (setProfileProc) {
                    this.mService.mProfileData.setProfileProc(this);
                }
                if (packageName != null) {
                    addPackage(packageName, versionCode, this.mService.mProcessStats);
                }
                updateProcessInfo(false, true, true);
                this.hasShownUi = true;
                setPendingUiClean(true);
                forceProcessStateUpTo(topProcessState);
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    public void appDied() {
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                this.mService.appDiedLocked(this);
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    public long getInputDispatchingTimeout() {
        return this.mWindowProcessController.getInputDispatchingTimeout();
    }

    public int getProcessClassEnum() {
        if (this.pid == ActivityManagerService.MY_PID) {
            return 3;
        }
        ApplicationInfo applicationInfo = this.info;
        if (applicationInfo == null) {
            return 0;
        }
        return (applicationInfo.flags & 1) != 0 ? 2 : 1;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isSilentAnr() {
        return !getShowBackground() && !isInterestingForBackgroundTraces();
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public List<ProcessRecord> getLruProcessList() {
        return this.mService.mProcessList.mLruProcesses;
    }

    /* access modifiers changed from: package-private */
    @VisibleForTesting
    public boolean isMonitorCpuUsage() {
        ActivityManagerService activityManagerService = this.mService;
        return true;
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:192:0x0354, code lost:
        r7.append(r6.printCurrentLoad());
        r7.append(r1);
        r20 = r1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:256:0x049a, code lost:
        com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:257:0x049d, code lost:
        return;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void appNotResponding(java.lang.String r33, android.content.pm.ApplicationInfo r34, java.lang.String r35, com.android.server.wm.WindowProcessController r36, boolean r37, java.lang.String r38) {
        /*
            r32 = this;
            r15 = r32
            r14 = r33
            r13 = r35
            r12 = r36
            r11 = r38
            java.util.ArrayList r0 = new java.util.ArrayList
            r10 = 5
            r0.<init>(r10)
            r9 = r0
            android.util.SparseArray r0 = new android.util.SparseArray
            r1 = 20
            r0.<init>(r1)
            r8 = r0
            com.android.server.wm.WindowProcessController r0 = r15.mWindowProcessController
            com.android.server.am.-$$Lambda$ProcessRecord$1qn6-pj5yWgiSnKANZpVz3gwd30 r1 = new com.android.server.am.-$$Lambda$ProcessRecord$1qn6-pj5yWgiSnKANZpVz3gwd30
            r1.<init>()
            r0.appEarlyNotResponding(r11, r1)
            long r6 = android.os.SystemClock.uptimeMillis()
            boolean r0 = r32.isMonitorCpuUsage()
            if (r0 == 0) goto L_0x0032
            com.android.server.am.ActivityManagerService r0 = r15.mService
            r0.updateCpuStatsNow()
        L_0x0032:
            com.android.server.am.ActivityManagerService r5 = r15.mService
            monitor-enter(r5)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x04df }
            com.android.server.am.ActivityManagerService r0 = r15.mService     // Catch:{ all -> 0x04df }
            com.android.server.wm.ActivityTaskManagerInternal r0 = r0.mAtmInternal     // Catch:{ all -> 0x04df }
            boolean r0 = r0.isShuttingDown()     // Catch:{ all -> 0x04df }
            if (r0 == 0) goto L_0x0077
            java.lang.String r0 = "ActivityManager"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0065 }
            r1.<init>()     // Catch:{ all -> 0x0065 }
            java.lang.String r2 = "During shutdown skipping ANR: "
            r1.append(r2)     // Catch:{ all -> 0x0065 }
            r1.append(r15)     // Catch:{ all -> 0x0065 }
            java.lang.String r2 = " "
            r1.append(r2)     // Catch:{ all -> 0x0065 }
            r1.append(r11)     // Catch:{ all -> 0x0065 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0065 }
            android.util.Slog.i(r0, r1)     // Catch:{ all -> 0x0065 }
            monitor-exit(r5)     // Catch:{ all -> 0x0065 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0065:
            r0 = move-exception
            r19 = r5
            r30 = r6
            r28 = r8
            r29 = r9
            r4 = r11
            r2 = r14
            r3 = r15
            r6 = r34
            r8 = r37
            goto L_0x04ef
        L_0x0077:
            boolean r0 = r32.isNotResponding()     // Catch:{ all -> 0x04df }
            if (r0 == 0) goto L_0x00a0
            java.lang.String r0 = "ActivityManager"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0065 }
            r1.<init>()     // Catch:{ all -> 0x0065 }
            java.lang.String r2 = "Skipping duplicate ANR: "
            r1.append(r2)     // Catch:{ all -> 0x0065 }
            r1.append(r15)     // Catch:{ all -> 0x0065 }
            java.lang.String r2 = " "
            r1.append(r2)     // Catch:{ all -> 0x0065 }
            r1.append(r11)     // Catch:{ all -> 0x0065 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0065 }
            android.util.Slog.i(r0, r1)     // Catch:{ all -> 0x0065 }
            monitor-exit(r5)     // Catch:{ all -> 0x0065 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return
        L_0x00a0:
            boolean r0 = r32.isCrashing()     // Catch:{ all -> 0x04df }
            if (r0 == 0) goto L_0x00c9
            java.lang.String r0 = "ActivityManager"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0065 }
            r1.<init>()     // Catch:{ all -> 0x0065 }
            java.lang.String r2 = "Crashing app skipping ANR: "
            r1.append(r2)     // Catch:{ all -> 0x0065 }
            r1.append(r15)     // Catch:{ all -> 0x0065 }
            java.lang.String r2 = " "
            r1.append(r2)     // Catch:{ all -> 0x0065 }
            r1.append(r11)     // Catch:{ all -> 0x0065 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0065 }
            android.util.Slog.i(r0, r1)     // Catch:{ all -> 0x0065 }
            monitor-exit(r5)     // Catch:{ all -> 0x0065 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return
        L_0x00c9:
            boolean r0 = r15.killedByAm     // Catch:{ all -> 0x04df }
            if (r0 == 0) goto L_0x00f0
            java.lang.String r0 = "ActivityManager"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0065 }
            r1.<init>()     // Catch:{ all -> 0x0065 }
            java.lang.String r2 = "App already killed by AM skipping ANR: "
            r1.append(r2)     // Catch:{ all -> 0x0065 }
            r1.append(r15)     // Catch:{ all -> 0x0065 }
            java.lang.String r2 = " "
            r1.append(r2)     // Catch:{ all -> 0x0065 }
            r1.append(r11)     // Catch:{ all -> 0x0065 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0065 }
            android.util.Slog.i(r0, r1)     // Catch:{ all -> 0x0065 }
            monitor-exit(r5)     // Catch:{ all -> 0x0065 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return
        L_0x00f0:
            boolean r0 = r15.killed     // Catch:{ all -> 0x04df }
            if (r0 == 0) goto L_0x0117
            java.lang.String r0 = "ActivityManager"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ all -> 0x0065 }
            r1.<init>()     // Catch:{ all -> 0x0065 }
            java.lang.String r2 = "Skipping died app ANR: "
            r1.append(r2)     // Catch:{ all -> 0x0065 }
            r1.append(r15)     // Catch:{ all -> 0x0065 }
            java.lang.String r2 = " "
            r1.append(r2)     // Catch:{ all -> 0x0065 }
            r1.append(r11)     // Catch:{ all -> 0x0065 }
            java.lang.String r1 = r1.toString()     // Catch:{ all -> 0x0065 }
            android.util.Slog.i(r0, r1)     // Catch:{ all -> 0x0065 }
            monitor-exit(r5)     // Catch:{ all -> 0x0065 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0117:
            android.content.pm.ApplicationInfo r0 = r15.info     // Catch:{ all -> 0x04df }
            int r1 = r15.uid     // Catch:{ all -> 0x04df }
            boolean r0 = com.android.server.am.ActivityManagerServiceInjector.skipFrozenAppAnr(r0, r1)     // Catch:{ all -> 0x04df }
            if (r0 == 0) goto L_0x0126
            monitor-exit(r5)     // Catch:{ all -> 0x0065 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0126:
            com.android.server.am.ActivityManagerService r1 = r15.mService     // Catch:{ all -> 0x04df }
            java.lang.String r0 = ""
            r16 = 0
            r17 = 0
            r18 = 0
            r2 = r32
            r3 = r33
            r4 = r35
            r19 = r5
            r5 = r0
            r20 = r6
            r6 = r38
            r7 = r16
            r22 = r8
            r8 = r17
            r23 = r9
            r9 = r18
            boolean r0 = com.android.server.am.ActivityManagerServiceInjector.finishSilentAnr(r1, r2, r3, r4, r5, r6, r7, r8, r9)     // Catch:{ all -> 0x04d0 }
            if (r0 == 0) goto L_0x0162
            monitor-exit(r19)     // Catch:{ all -> 0x0152 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0152:
            r0 = move-exception
            r6 = r34
            r8 = r37
            r4 = r11
            r2 = r14
            r3 = r15
            r30 = r20
            r28 = r22
            r29 = r23
            goto L_0x04ef
        L_0x0162:
            r0 = 1
            r15.setNotResponding(r0)     // Catch:{ all -> 0x04d0 }
            r1 = 30008(0x7538, float:4.205E-41)
            java.lang.Object[] r2 = new java.lang.Object[r10]     // Catch:{ all -> 0x04d0 }
            int r3 = r15.userId     // Catch:{ all -> 0x04d0 }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ all -> 0x04d0 }
            r4 = 0
            r2[r4] = r3     // Catch:{ all -> 0x04d0 }
            int r3 = r15.pid     // Catch:{ all -> 0x04d0 }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ all -> 0x04d0 }
            r2[r0] = r3     // Catch:{ all -> 0x04d0 }
            java.lang.String r3 = r15.processName     // Catch:{ all -> 0x04d0 }
            r10 = 2
            r2[r10] = r3     // Catch:{ all -> 0x04d0 }
            android.content.pm.ApplicationInfo r3 = r15.info     // Catch:{ all -> 0x04d0 }
            int r3 = r3.flags     // Catch:{ all -> 0x04d0 }
            java.lang.Integer r3 = java.lang.Integer.valueOf(r3)     // Catch:{ all -> 0x04d0 }
            r5 = 3
            r2[r5] = r3     // Catch:{ all -> 0x04d0 }
            r3 = 4
            r2[r3] = r11     // Catch:{ all -> 0x04d0 }
            android.util.EventLog.writeEvent(r1, r2)     // Catch:{ all -> 0x04d0 }
            int r1 = r15.pid     // Catch:{ all -> 0x04d0 }
            java.lang.Integer r1 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x04d0 }
            r9 = r23
            r9.add(r1)     // Catch:{ all -> 0x04c1 }
            boolean r1 = r32.isSilentAnr()     // Catch:{ all -> 0x04c1 }
            if (r1 != 0) goto L_0x025c
            int r1 = r15.pid     // Catch:{ all -> 0x024c }
            if (r12 == 0) goto L_0x01c2
            int r2 = r36.getPid()     // Catch:{ all -> 0x01b2 }
            if (r2 <= 0) goto L_0x01c2
            int r2 = r36.getPid()     // Catch:{ all -> 0x01b2 }
            r1 = r2
            goto L_0x01c2
        L_0x01b2:
            r0 = move-exception
            r6 = r34
            r8 = r37
            r29 = r9
            r4 = r11
            r2 = r14
            r3 = r15
            r30 = r20
            r28 = r22
            goto L_0x04ef
        L_0x01c2:
            int r2 = r15.pid     // Catch:{ all -> 0x024c }
            if (r1 == r2) goto L_0x01cd
            java.lang.Integer r2 = java.lang.Integer.valueOf(r1)     // Catch:{ all -> 0x01b2 }
            r9.add(r2)     // Catch:{ all -> 0x01b2 }
        L_0x01cd:
            int r2 = com.android.server.am.ActivityManagerService.MY_PID     // Catch:{ all -> 0x024c }
            int r3 = r15.pid     // Catch:{ all -> 0x024c }
            if (r2 == r3) goto L_0x01e0
            int r2 = com.android.server.am.ActivityManagerService.MY_PID     // Catch:{ all -> 0x01b2 }
            if (r2 == r1) goto L_0x01e0
            int r2 = com.android.server.am.ActivityManagerService.MY_PID     // Catch:{ all -> 0x01b2 }
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)     // Catch:{ all -> 0x01b2 }
            r9.add(r2)     // Catch:{ all -> 0x01b2 }
        L_0x01e0:
            java.util.List r2 = r32.getLruProcessList()     // Catch:{ all -> 0x024c }
            int r2 = r2.size()     // Catch:{ all -> 0x024c }
            int r2 = r2 - r0
        L_0x01e9:
            if (r2 < 0) goto L_0x0249
            java.util.List r3 = r32.getLruProcessList()     // Catch:{ all -> 0x024c }
            java.lang.Object r3 = r3.get(r2)     // Catch:{ all -> 0x024c }
            com.android.server.am.ProcessRecord r3 = (com.android.server.am.ProcessRecord) r3     // Catch:{ all -> 0x024c }
            if (r3 == 0) goto L_0x0242
            android.app.IApplicationThread r6 = r3.thread     // Catch:{ all -> 0x024c }
            if (r6 == 0) goto L_0x0242
            int r6 = r3.pid     // Catch:{ all -> 0x024c }
            if (r6 <= 0) goto L_0x023f
            int r7 = r15.pid     // Catch:{ all -> 0x024c }
            if (r6 == r7) goto L_0x023f
            if (r6 == r1) goto L_0x023f
            int r7 = com.android.server.am.ActivityManagerService.MY_PID     // Catch:{ all -> 0x024c }
            if (r6 == r7) goto L_0x023f
            boolean r7 = com.android.server.am.ProcessPolicyManager.isNeedTraceProcess(r3)     // Catch:{ all -> 0x024c }
            if (r7 == 0) goto L_0x0219
            java.lang.Integer r7 = java.lang.Integer.valueOf(r6)     // Catch:{ all -> 0x01b2 }
            r9.add(r7)     // Catch:{ all -> 0x01b2 }
            r8 = r22
            goto L_0x0244
        L_0x0219:
            boolean r7 = r3.treatLikeActivity     // Catch:{ all -> 0x024c }
            if (r7 == 0) goto L_0x0227
            java.lang.Integer r7 = java.lang.Integer.valueOf(r6)     // Catch:{ all -> 0x01b2 }
            r9.add(r7)     // Catch:{ all -> 0x01b2 }
            r8 = r22
            goto L_0x0244
        L_0x0227:
            java.lang.Boolean r7 = java.lang.Boolean.TRUE     // Catch:{ all -> 0x024c }
            r8 = r22
            r8.put(r6, r7)     // Catch:{ all -> 0x022f }
            goto L_0x0244
        L_0x022f:
            r0 = move-exception
            r6 = r34
            r28 = r8
            r29 = r9
            r4 = r11
            r2 = r14
            r3 = r15
            r30 = r20
            r8 = r37
            goto L_0x04ef
        L_0x023f:
            r8 = r22
            goto L_0x0244
        L_0x0242:
            r8 = r22
        L_0x0244:
            int r2 = r2 + -1
            r22 = r8
            goto L_0x01e9
        L_0x0249:
            r8 = r22
            goto L_0x025e
        L_0x024c:
            r0 = move-exception
            r6 = r34
            r8 = r37
            r29 = r9
            r4 = r11
            r2 = r14
            r3 = r15
            r30 = r20
            r28 = r22
            goto L_0x04ef
        L_0x025c:
            r8 = r22
        L_0x025e:
            monitor-exit(r19)     // Catch:{ all -> 0x04b2 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r7 = r1
            r7.setLength(r4)
            java.lang.String r1 = "ANR in "
            r7.append(r1)
            java.lang.String r1 = r15.processName
            r7.append(r1)
            if (r14 == 0) goto L_0x0284
            java.lang.String r1 = " ("
            r7.append(r1)
            r7.append(r14)
            java.lang.String r1 = ")"
            r7.append(r1)
        L_0x0284:
            java.lang.String r1 = "\n"
            r7.append(r1)
            java.lang.String r1 = "PID: "
            r7.append(r1)
            int r1 = r15.pid
            r7.append(r1)
            java.lang.String r1 = "\n"
            r7.append(r1)
            if (r11 == 0) goto L_0x02a7
            java.lang.String r1 = "Reason: "
            r7.append(r1)
            r7.append(r11)
            java.lang.String r1 = "\n"
            r7.append(r1)
        L_0x02a7:
            if (r13 == 0) goto L_0x02bc
            boolean r1 = r13.equals(r14)
            if (r1 == 0) goto L_0x02bc
            java.lang.String r1 = "Parent: "
            r7.append(r1)
            r7.append(r13)
            java.lang.String r1 = "\n"
            r7.append(r1)
        L_0x02bc:
            com.android.internal.os.ProcessCpuTracker r1 = new com.android.internal.os.ProcessCpuTracker
            r1.<init>(r4)
            r6 = r1
            r1 = 0
            boolean r2 = r32.isSilentAnr()
            if (r2 == 0) goto L_0x02ea
            r2 = 0
        L_0x02ca:
            java.lang.String[] r3 = com.android.server.Watchdog.NATIVE_STACKS_OF_INTEREST
            int r3 = r3.length
            if (r2 >= r3) goto L_0x02e7
            java.lang.String[] r3 = com.android.server.Watchdog.NATIVE_STACKS_OF_INTEREST
            r3 = r3[r2]
            java.lang.String r10 = r15.processName
            boolean r3 = r3.equals(r10)
            if (r3 == 0) goto L_0x02e3
            java.lang.String[] r3 = new java.lang.String[r0]
            java.lang.String r10 = r15.processName
            r3[r4] = r10
            r1 = r3
            goto L_0x02e7
        L_0x02e3:
            int r2 = r2 + 1
            r10 = 2
            goto L_0x02ca
        L_0x02e7:
            r17 = r1
            goto L_0x02ee
        L_0x02ea:
            java.lang.String[] r1 = com.android.server.Watchdog.NATIVE_STACKS_OF_INTEREST
            r17 = r1
        L_0x02ee:
            if (r17 != 0) goto L_0x02f2
            r1 = 0
            goto L_0x02f6
        L_0x02f2:
            int[] r1 = android.os.Process.getPidsForCommands(r17)
        L_0x02f6:
            r3 = r1
            r1 = 0
            if (r3 == 0) goto L_0x0313
            java.util.ArrayList r2 = new java.util.ArrayList
            int r4 = r3.length
            r2.<init>(r4)
            r1 = r2
            int r2 = r3.length
            r4 = 0
        L_0x0303:
            if (r4 >= r2) goto L_0x0311
            r19 = r3[r4]
            java.lang.Integer r10 = java.lang.Integer.valueOf(r19)
            r1.add(r10)
            int r4 = r4 + 1
            goto L_0x0303
        L_0x0311:
            r10 = r1
            goto L_0x0314
        L_0x0313:
            r10 = r1
        L_0x0314:
            boolean r1 = r32.isSilentAnr()
            if (r1 != 0) goto L_0x0324
            com.android.server.Watchdog.getInstance()
            java.util.ArrayList r1 = com.android.server.Watchdog.getInterestingHalPids()
            r10.addAll(r1)
        L_0x0324:
            boolean r23 = r32.isSilentAnr()
            if (r23 == 0) goto L_0x032d
            r1 = 0
            goto L_0x032e
        L_0x032d:
            r1 = r6
        L_0x032e:
            if (r23 == 0) goto L_0x0332
            r2 = 0
            goto L_0x0333
        L_0x0332:
            r2 = r8
        L_0x0333:
            java.io.File r24 = com.android.server.am.ActivityManagerService.dumpStackTraces((java.util.ArrayList<java.lang.Integer>) r9, (com.android.internal.os.ProcessCpuTracker) r1, (android.util.SparseArray<java.lang.Boolean>) r2, (java.util.ArrayList<java.lang.Integer>) r10)
            r1 = 0
            boolean r2 = r32.isMonitorCpuUsage()
            if (r2 == 0) goto L_0x0368
            com.android.server.am.ActivityManagerService r2 = r15.mService
            r2.updateCpuStatsNow()
            com.android.server.am.ActivityManagerService r2 = r15.mService
            com.android.internal.os.ProcessCpuTracker r2 = r2.mProcessCpuTracker
            monitor-enter(r2)
            com.android.server.am.ActivityManagerService r4 = r15.mService     // Catch:{ all -> 0x0361 }
            com.android.internal.os.ProcessCpuTracker r4 = r4.mProcessCpuTracker     // Catch:{ all -> 0x0361 }
            r11 = r20
            java.lang.String r4 = r4.printCurrentState(r11)     // Catch:{ all -> 0x0366 }
            r1 = r4
            monitor-exit(r2)     // Catch:{ all -> 0x0366 }
            java.lang.String r2 = r6.printCurrentLoad()
            r7.append(r2)
            r7.append(r1)
            r20 = r1
            goto L_0x036c
        L_0x0361:
            r0 = move-exception
            r11 = r20
        L_0x0364:
            monitor-exit(r2)     // Catch:{ all -> 0x0366 }
            throw r0
        L_0x0366:
            r0 = move-exception
            goto L_0x0364
        L_0x0368:
            r11 = r20
            r20 = r1
        L_0x036c:
            java.lang.String r1 = r6.printCurrentState(r11)
            r7.append(r1)
            java.lang.String r1 = r7.toString()
            java.lang.String r2 = "ActivityManager"
            android.util.Slog.e(r2, r1)
            if (r24 != 0) goto L_0x0383
            int r1 = r15.pid
            android.os.Process.sendSignal(r1, r5)
        L_0x0383:
            int r2 = r15.uid
            java.lang.String r4 = r15.processName
            if (r14 != 0) goto L_0x038d
            java.lang.String r5 = "unknown"
            goto L_0x038e
        L_0x038d:
            r5 = r14
        L_0x038e:
            android.content.pm.ApplicationInfo r0 = r15.info
            if (r0 == 0) goto L_0x039e
            boolean r0 = r0.isInstantApp()
            if (r0 == 0) goto L_0x039b
            r18 = 2
            goto L_0x03a0
        L_0x039b:
            r18 = 1
            goto L_0x03a0
        L_0x039e:
            r18 = 0
        L_0x03a0:
            boolean r0 = r32.isInterestingToUserLocked()
            if (r0 == 0) goto L_0x03a8
            r0 = 2
            goto L_0x03a9
        L_0x03a8:
            r0 = 1
        L_0x03a9:
            int r19 = r32.getProcessClassEnum()
            android.content.pm.ApplicationInfo r1 = r15.info
            if (r1 == 0) goto L_0x03b4
            java.lang.String r1 = r1.packageName
            goto L_0x03b6
        L_0x03b4:
            java.lang.String r1 = ""
        L_0x03b6:
            r26 = r1
            r1 = 79
            r25 = r3
            r3 = r4
            r4 = r5
            r5 = r38
            r27 = r6
            r6 = r18
            r18 = r7
            r7 = r0
            r28 = r8
            r8 = r19
            r29 = r9
            r9 = r26
            android.util.StatsLog.write(r1, r2, r3, r4, r5, r6, r7, r8, r9)
            r30 = r11
            r12 = r36
            if (r12 == 0) goto L_0x03de
            java.lang.Object r0 = r12.mOwner
            com.android.server.am.ProcessRecord r0 = (com.android.server.am.ProcessRecord) r0
            r7 = r0
            goto L_0x03df
        L_0x03de:
            r7 = 0
        L_0x03df:
            com.android.server.am.ActivityManagerService r1 = r15.mService
            java.lang.String r4 = r15.processName
            r11 = 0
            java.lang.String r2 = "anr"
            r3 = r32
            r5 = r33
            r6 = r35
            r8 = r38
            r9 = r20
            r22 = r10
            r0 = 2
            r10 = r24
            r1.addErrorToDropBox(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11)
            com.android.server.am.ActivityManagerService r8 = r15.mService
            r1 = 0
            java.lang.String r16 = r18.toString()
            r9 = r32
            r10 = r33
            r11 = r35
            r12 = r20
            r13 = r38
            r2 = r14
            r14 = r24
            r3 = r15
            r15 = r1
            com.android.server.am.ActivityManagerServiceInjector.onANR(r8, r9, r10, r11, r12, r13, r14, r15, r16)
            com.android.server.wm.WindowProcessController r1 = r3.mWindowProcessController
            java.lang.String r4 = r18.toString()
            com.android.server.am.-$$Lambda$ProcessRecord$Cb3MKja7_iTlaFQrvQTzPvLyoT8 r5 = new com.android.server.am.-$$Lambda$ProcessRecord$Cb3MKja7_iTlaFQrvQTzPvLyoT8
            r5.<init>()
            com.android.server.am.-$$Lambda$ProcessRecord$2DImTokd0AWNTECl3WgBxJkOOqs r6 = new com.android.server.am.-$$Lambda$ProcessRecord$2DImTokd0AWNTECl3WgBxJkOOqs
            r6.<init>()
            boolean r1 = r1.appNotResponding(r4, r5, r6)
            if (r1 == 0) goto L_0x0428
            return
        L_0x0428:
            com.android.server.am.ActivityManagerService r1 = r3.mService
            monitor-enter(r1)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x04a4 }
            com.android.server.am.ActivityManagerService r4 = r3.mService     // Catch:{ all -> 0x04a4 }
            com.android.server.am.BatteryStatsService r4 = r4.mBatteryStatsService     // Catch:{ all -> 0x04a4 }
            if (r4 == 0) goto L_0x043f
            com.android.server.am.ActivityManagerService r4 = r3.mService     // Catch:{ all -> 0x04a4 }
            com.android.server.am.BatteryStatsService r4 = r4.mBatteryStatsService     // Catch:{ all -> 0x04a4 }
            java.lang.String r5 = r3.processName     // Catch:{ all -> 0x04a4 }
            int r6 = r3.uid     // Catch:{ all -> 0x04a4 }
            r4.noteProcessAnr(r5, r6)     // Catch:{ all -> 0x04a4 }
        L_0x043f:
            boolean r4 = r32.isSilentAnr()     // Catch:{ all -> 0x04a4 }
            if (r4 == 0) goto L_0x0456
            boolean r4 = r32.isDebugging()     // Catch:{ all -> 0x04a4 }
            if (r4 != 0) goto L_0x0456
            java.lang.String r0 = "bg anr"
            r4 = 1
            r3.kill(r0, r4)     // Catch:{ all -> 0x04a4 }
            monitor-exit(r1)     // Catch:{ all -> 0x04a4 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return
        L_0x0456:
            r4 = r38
            if (r4 == 0) goto L_0x046d
            java.lang.StringBuilder r5 = new java.lang.StringBuilder     // Catch:{ all -> 0x049e }
            r5.<init>()     // Catch:{ all -> 0x049e }
            java.lang.String r6 = "ANR "
            r5.append(r6)     // Catch:{ all -> 0x049e }
            r5.append(r4)     // Catch:{ all -> 0x049e }
            java.lang.String r5 = r5.toString()     // Catch:{ all -> 0x049e }
            goto L_0x046f
        L_0x046d:
            java.lang.String r5 = "ANR"
        L_0x046f:
            java.lang.String r6 = r18.toString()     // Catch:{ all -> 0x049e }
            r3.makeAppNotRespondingLocked(r2, r5, r6)     // Catch:{ all -> 0x049e }
            com.android.server.am.ActivityManagerService r5 = r3.mService     // Catch:{ all -> 0x049e }
            android.os.Handler r5 = r5.mUiHandler     // Catch:{ all -> 0x049e }
            if (r5 == 0) goto L_0x0495
            android.os.Message r5 = android.os.Message.obtain()     // Catch:{ all -> 0x049e }
            r5.what = r0     // Catch:{ all -> 0x049e }
            com.android.server.am.AppNotRespondingDialog$Data r0 = new com.android.server.am.AppNotRespondingDialog$Data     // Catch:{ all -> 0x049e }
            r6 = r34
            r8 = r37
            r0.<init>(r3, r6, r8)     // Catch:{ all -> 0x04b0 }
            r5.obj = r0     // Catch:{ all -> 0x04b0 }
            com.android.server.am.ActivityManagerService r0 = r3.mService     // Catch:{ all -> 0x04b0 }
            android.os.Handler r0 = r0.mUiHandler     // Catch:{ all -> 0x04b0 }
            r0.sendMessage(r5)     // Catch:{ all -> 0x04b0 }
            goto L_0x0499
        L_0x0495:
            r6 = r34
            r8 = r37
        L_0x0499:
            monitor-exit(r1)     // Catch:{ all -> 0x04b0 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            return
        L_0x049e:
            r0 = move-exception
            r6 = r34
            r8 = r37
            goto L_0x04ab
        L_0x04a4:
            r0 = move-exception
            r6 = r34
            r8 = r37
            r4 = r38
        L_0x04ab:
            monitor-exit(r1)     // Catch:{ all -> 0x04b0 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x04b0:
            r0 = move-exception
            goto L_0x04ab
        L_0x04b2:
            r0 = move-exception
            r6 = r34
            r28 = r8
            r29 = r9
            r4 = r11
            r2 = r14
            r3 = r15
            r30 = r20
            r8 = r37
            goto L_0x04ef
        L_0x04c1:
            r0 = move-exception
            r6 = r34
            r8 = r37
            r29 = r9
            r4 = r11
            r2 = r14
            r3 = r15
            r30 = r20
            r28 = r22
            goto L_0x04ef
        L_0x04d0:
            r0 = move-exception
            r6 = r34
            r8 = r37
            r4 = r11
            r2 = r14
            r3 = r15
            r30 = r20
            r28 = r22
            r29 = r23
            goto L_0x04ef
        L_0x04df:
            r0 = move-exception
            r19 = r5
            r30 = r6
            r28 = r8
            r29 = r9
            r4 = r11
            r2 = r14
            r3 = r15
            r6 = r34
            r8 = r37
        L_0x04ef:
            monitor-exit(r19)     // Catch:{ all -> 0x04f4 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r0
        L_0x04f4:
            r0 = move-exception
            goto L_0x04ef
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ProcessRecord.appNotResponding(java.lang.String, android.content.pm.ApplicationInfo, java.lang.String, com.android.server.wm.WindowProcessController, boolean, java.lang.String):void");
    }

    public /* synthetic */ void lambda$appNotResponding$0$ProcessRecord() {
        kill(ProcessPolicy.REASON_ANR, true);
    }

    public /* synthetic */ void lambda$appNotResponding$1$ProcessRecord() {
        kill(ProcessPolicy.REASON_ANR, true);
    }

    public /* synthetic */ void lambda$appNotResponding$2$ProcessRecord() {
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                this.mService.mServices.scheduleServiceTimeoutLocked(this);
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
    }

    private void makeAppNotRespondingLocked(String activity, String shortMsg, String longMsg) {
        setNotResponding(true);
        if (this.mService.mAppErrors != null) {
            this.notRespondingReport = this.mService.mAppErrors.generateProcessError(this, 2, activity, shortMsg, longMsg, (String) null);
        }
        startAppProblemLocked();
        getWindowProcessController().stopFreezingActivities();
    }

    /* access modifiers changed from: package-private */
    public void startAppProblemLocked() {
        this.errorReportReceiver = null;
        for (int userId2 : this.mService.mUserController.getCurrentProfileIds()) {
            if (this.userId == userId2) {
                this.errorReportReceiver = ApplicationErrorReport.getErrorReportReceiver(this.mService.mContext, this.info.packageName, this.info.flags);
            }
        }
        this.mService.skipCurrentReceiverLocked(this);
    }

    private boolean isInterestingForBackgroundTraces() {
        if (this.pid == ActivityManagerService.MY_PID || isInterestingToUserLocked()) {
            return true;
        }
        ApplicationInfo applicationInfo = this.info;
        if ((applicationInfo == null || !AccessController.PACKAGE_SYSTEMUI.equals(applicationInfo.packageName)) && !hasTopUi() && !hasOverlayUi()) {
            return false;
        }
        return true;
    }

    private boolean getShowBackground() {
        return Settings.Secure.getInt(this.mService.mContext.getContentResolver(), "anr_show_background", 0) != 0;
    }
}
