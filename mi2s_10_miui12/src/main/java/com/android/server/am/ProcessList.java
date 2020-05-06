package com.android.server.am;

import android.app.ActivityManager;
import android.app.ActivityThread;
import android.app.AppGlobals;
import android.app.IApplicationThread;
import android.content.ComponentName;
import android.content.pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.AppZygote;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.ChildZygoteProcess;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Trace;
import android.os.UserHandle;
import android.util.ArrayMap;
import android.util.BoostFramework;
import android.util.EventLog;
import android.util.LongSparseArray;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.StatsLog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.ProcessMap;
import com.android.internal.app.procstats.ProcessStats;
import com.android.internal.util.MemInfoReader;
import com.android.server.MiuiNetworkManagementService;
import com.android.server.ServiceThread;
import com.android.server.Watchdog;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.job.controllers.JobStatus;
import com.android.server.slice.SliceClientPermissions;
import com.android.server.wm.ActivityServiceConnectionsHolder;
import com.android.server.wm.WindowManagerService;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import libcore.io.IoUtils;

public final class ProcessList {
    static final int BACKUP_APP_ADJ = 300;
    static final int CACHED_APP_IMPORTANCE_LEVELS = 5;
    static final int CACHED_APP_LMK_FIRST_ADJ = 950;
    static final int CACHED_APP_MAX_ADJ = 999;
    static final int CACHED_APP_MIN_ADJ = 900;
    static final int FOREGROUND_APP_ADJ = 0;
    static final int HEAVY_WEIGHT_APP_ADJ = 400;
    static final int HOME_APP_ADJ = 600;
    static final int INVALID_ADJ = -10000;
    static final byte LMK_GETKILLCNT = 4;
    static final byte LMK_PROCPRIO = 1;
    static final byte LMK_PROCPURGE = 3;
    static final byte LMK_PROCREMOVE = 2;
    static final byte LMK_TARGET = 0;
    static final long MAX_EMPTY_TIME = 1800000;
    static final int MIN_CACHED_APPS = 2;
    static final int MIN_CRASH_INTERVAL = 60000;
    static final int NATIVE_ADJ = -1000;
    static final int PAGE_SIZE = 4096;
    static final int PERCEPTIBLE_APP_ADJ = 200;
    static final int PERCEPTIBLE_LOW_APP_ADJ = 250;
    static final int PERCEPTIBLE_RECENT_FOREGROUND_APP_ADJ = 50;
    static final int PERSISTENT_PROC_ADJ = -800;
    static final int PERSISTENT_SERVICE_ADJ = -700;
    static final int PREVIOUS_APP_ADJ = 700;
    public static final int PROC_MEM_CACHED = 4;
    public static final int PROC_MEM_IMPORTANT = 2;
    public static final int PROC_MEM_NUM = 5;
    public static final int PROC_MEM_PERSISTENT = 0;
    public static final int PROC_MEM_SERVICE = 3;
    public static final int PROC_MEM_TOP = 1;
    private static final String PROPERTY_USE_APP_IMAGE_STARTUP_CACHE = "persist.device_config.runtime_native.use_app_image_startup_cache";
    public static final int PSS_ALL_INTERVAL = 1200000;
    private static final int PSS_FIRST_ASLEEP_BACKGROUND_INTERVAL = 30000;
    private static final int PSS_FIRST_ASLEEP_CACHED_INTERVAL = 60000;
    private static final int PSS_FIRST_ASLEEP_PERSISTENT_INTERVAL = 60000;
    private static final int PSS_FIRST_ASLEEP_TOP_INTERVAL = 20000;
    private static final int PSS_FIRST_BACKGROUND_INTERVAL = 20000;
    private static final int PSS_FIRST_CACHED_INTERVAL = 20000;
    private static final int PSS_FIRST_PERSISTENT_INTERVAL = 30000;
    private static final int PSS_FIRST_TOP_INTERVAL = 10000;
    public static final int PSS_MAX_INTERVAL = 3600000;
    public static final int PSS_MIN_TIME_FROM_STATE_CHANGE = 15000;
    public static final int PSS_SAFE_TIME_FROM_STATE_CHANGE = 1000;
    private static final int PSS_SAME_CACHED_INTERVAL = 600000;
    private static final int PSS_SAME_IMPORTANT_INTERVAL = 600000;
    private static final int PSS_SAME_PERSISTENT_INTERVAL = 600000;
    private static final int PSS_SAME_SERVICE_INTERVAL = 300000;
    private static final int PSS_SAME_TOP_INTERVAL = 60000;
    private static final int PSS_TEST_FIRST_BACKGROUND_INTERVAL = 5000;
    private static final int PSS_TEST_FIRST_TOP_INTERVAL = 3000;
    public static final int PSS_TEST_MIN_TIME_FROM_STATE_CHANGE = 10000;
    private static final int PSS_TEST_SAME_BACKGROUND_INTERVAL = 15000;
    private static final int PSS_TEST_SAME_IMPORTANT_INTERVAL = 10000;
    static final int SCHED_GROUP_BACKGROUND = 0;
    static final int SCHED_GROUP_DEFAULT = 2;
    static final int SCHED_GROUP_RESTRICTED = 1;
    public static final int SCHED_GROUP_TOP_APP = 3;
    static final int SCHED_GROUP_TOP_APP_BOUND = 4;
    static final int SERVICE_ADJ = 500;
    static final int SERVICE_B_ADJ = 800;
    static final int SYSTEM_ADJ = -900;
    static final String TAG = "ActivityManager";
    static final int TRIM_CRITICAL_THRESHOLD = 3;
    static final int TRIM_LOW_THRESHOLD = 5;
    static final int UNKNOWN_ADJ = 1001;
    static final int VISIBLE_APP_ADJ = 100;
    static final int VISIBLE_APP_LAYER_MAX = 99;
    public static BoostFramework mPerfServiceStartHint = new BoostFramework();
    private static final long[] sFirstAsleepPssTimes = {60000, ActivityManagerServiceInjector.KEEP_FOREGROUND_DURATION, 30000, 30000, 60000};
    private static final long[] sFirstAwakePssTimes = {30000, JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY, ActivityManagerServiceInjector.KEEP_FOREGROUND_DURATION, ActivityManagerServiceInjector.KEEP_FOREGROUND_DURATION, ActivityManagerServiceInjector.KEEP_FOREGROUND_DURATION};
    static KillHandler sKillHandler = null;
    static ServiceThread sKillThread = null;
    @GuardedBy({"sLmkdSocketLock"})
    private static InputStream sLmkdInputStream;
    @GuardedBy({"sLmkdSocketLock"})
    private static OutputStream sLmkdOutputStream;
    @GuardedBy({"sLmkdSocketLock"})
    private static LocalSocket sLmkdSocket;
    private static Object sLmkdSocketLock = new Object();
    private static final int[] sProcStateToProcMem = {0, 0, 1, 2, 2, 1, 2, 2, 2, 2, 2, 3, 4, 1, 2, 4, 4, 4, 4, 4, 4};
    private static final long[] sSameAsleepPssTimes = {600000, 60000, 600000, BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS, 600000};
    private static final long[] sSameAwakePssTimes = {600000, 60000, 600000, BackupAgentTimeoutParameters.DEFAULT_FULL_BACKUP_AGENT_TIMEOUT_MILLIS, 600000};
    private static final long[] sTestFirstPssTimes = {3000, 3000, 5000, 5000, 5000};
    private static final long[] sTestSamePssTimes = {15000, JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY, JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY, 15000, 15000};
    ActiveUids mActiveUids;
    @VisibleForTesting
    IsolatedUidRangeAllocator mAppIsolatedUidRangeAllocator = new IsolatedUidRangeAllocator(90000, 98999, 100);
    final ArrayMap<AppZygote, ArrayList<ProcessRecord>> mAppZygoteProcesses = new ArrayMap<>();
    final ProcessMap<AppZygote> mAppZygotes = new ProcessMap<>();
    private long mCachedRestoreLevel;
    @VisibleForTesting
    IsolatedUidRange mGlobalIsolatedUids = new IsolatedUidRange(99000, 99999);
    private boolean mHaveDisplaySize;
    final SparseArray<ProcessRecord> mIsolatedProcesses = new SparseArray<>();
    int mLruProcessActivityStart = 0;
    int mLruProcessServiceStart = 0;
    final ArrayList<ProcessRecord> mLruProcesses = new ArrayList<>();
    int mLruSeq = 0;
    private final int[] mOomAdj = {0, 100, 200, 250, CACHED_APP_MIN_ADJ, CACHED_APP_LMK_FIRST_ADJ};
    private final int[] mOomMinFree = new int[this.mOomAdj.length];
    private final int[] mOomMinFreeHigh = {73728, 92160, 110592, 129024, 147456, 184320};
    private final int[] mOomMinFreeLow = {12288, 18432, 24576, 36864, 43008, 49152};
    @GuardedBy({"mService"})
    final LongSparseArray<ProcessRecord> mPendingStarts = new LongSparseArray<>();
    @GuardedBy({"mService"})
    private long mProcStartSeqCounter = 0;
    @GuardedBy({"mService"})
    @VisibleForTesting
    long mProcStateSeqCounter = 0;
    final MyProcessMap mProcessNames = new MyProcessMap();
    final ArrayList<ProcessRecord> mRemovedProcesses = new ArrayList<>();
    ActivityManagerService mService = null;
    @GuardedBy({"mService"})
    final StringBuilder mStringBuilder = new StringBuilder(256);
    private final long mTotalMemMb;

    final class IsolatedUidRange {
        @VisibleForTesting
        public final int mFirstUid;
        @VisibleForTesting
        public final int mLastUid;
        @GuardedBy({"ProcessList.this.mService"})
        private int mNextUid;
        @GuardedBy({"ProcessList.this.mService"})
        private final SparseBooleanArray mUidUsed = new SparseBooleanArray();

        IsolatedUidRange(int firstUid, int lastUid) {
            this.mFirstUid = firstUid;
            this.mLastUid = lastUid;
            this.mNextUid = firstUid;
        }

        /* access modifiers changed from: package-private */
        @GuardedBy({"ProcessList.this.mService"})
        public int allocateIsolatedUidLocked(int userId) {
            int stepsLeft = (this.mLastUid - this.mFirstUid) + 1;
            for (int i = 0; i < stepsLeft; i++) {
                int i2 = this.mNextUid;
                if (i2 < this.mFirstUid || i2 > this.mLastUid) {
                    this.mNextUid = this.mFirstUid;
                }
                int uid = UserHandle.getUid(userId, this.mNextUid);
                this.mNextUid++;
                if (!this.mUidUsed.get(uid, false)) {
                    this.mUidUsed.put(uid, true);
                    return uid;
                }
            }
            return -1;
        }

        /* access modifiers changed from: package-private */
        @GuardedBy({"ProcessList.this.mService"})
        public void freeIsolatedUidLocked(int uid) {
            this.mUidUsed.delete(UserHandle.getAppId(uid));
        }
    }

    final class IsolatedUidRangeAllocator {
        @GuardedBy({"ProcessList.this.mService"})
        private final ProcessMap<IsolatedUidRange> mAppRanges = new ProcessMap<>();
        @GuardedBy({"ProcessList.this.mService"})
        private final BitSet mAvailableUidRanges;
        private final int mFirstUid;
        private final int mNumUidRanges;
        private final int mNumUidsPerRange;

        IsolatedUidRangeAllocator(int firstUid, int lastUid, int numUidsPerRange) {
            this.mFirstUid = firstUid;
            this.mNumUidsPerRange = numUidsPerRange;
            this.mNumUidRanges = ((lastUid - firstUid) + 1) / numUidsPerRange;
            this.mAvailableUidRanges = new BitSet(this.mNumUidRanges);
            this.mAvailableUidRanges.set(0, this.mNumUidRanges);
        }

        /* access modifiers changed from: package-private */
        @GuardedBy({"ProcessList.this.mService"})
        public IsolatedUidRange getIsolatedUidRangeLocked(String processName, int uid) {
            return (IsolatedUidRange) this.mAppRanges.get(processName, uid);
        }

        /* access modifiers changed from: package-private */
        @GuardedBy({"ProcessList.this.mService"})
        public IsolatedUidRange getOrCreateIsolatedUidRangeLocked(String processName, int uid) {
            IsolatedUidRange range = getIsolatedUidRangeLocked(processName, uid);
            if (range != null) {
                return range;
            }
            int uidRangeIndex = this.mAvailableUidRanges.nextSetBit(0);
            if (uidRangeIndex < 0) {
                return null;
            }
            this.mAvailableUidRanges.clear(uidRangeIndex);
            int i = this.mFirstUid;
            int i2 = this.mNumUidsPerRange;
            int actualUid = i + (uidRangeIndex * i2);
            IsolatedUidRange range2 = new IsolatedUidRange(actualUid, (i2 + actualUid) - 1);
            this.mAppRanges.put(processName, uid, range2);
            return range2;
        }

        /* access modifiers changed from: package-private */
        @GuardedBy({"ProcessList.this.mService"})
        public void freeUidRangeLocked(ApplicationInfo info) {
            IsolatedUidRange range = (IsolatedUidRange) this.mAppRanges.get(info.processName, info.uid);
            if (range != null) {
                this.mAvailableUidRanges.set((range.mFirstUid - this.mFirstUid) / this.mNumUidsPerRange);
                this.mAppRanges.remove(info.processName, info.uid);
            }
        }
    }

    final class MyProcessMap extends ProcessMap<ProcessRecord> {
        MyProcessMap() {
        }

        public ProcessRecord put(String name, int uid, ProcessRecord value) {
            ProcessRecord r = (ProcessRecord) ProcessList.super.put(name, uid, value);
            ProcessList.this.mService.mAtmInternal.onProcessAdded(r.getWindowProcessController());
            return r;
        }

        public ProcessRecord remove(String name, int uid) {
            ProcessRecord r = (ProcessRecord) ProcessList.super.remove(name, uid);
            ProcessList.this.mService.mAtmInternal.onProcessRemoved(name, uid);
            return r;
        }
    }

    final class KillHandler extends Handler {
        static final int KILL_PROCESS_GROUP_MSG = 4000;

        public KillHandler(Looper looper) {
            super(looper, (Handler.Callback) null, true);
        }

        public void handleMessage(Message msg) {
            if (msg.what != KILL_PROCESS_GROUP_MSG) {
                super.handleMessage(msg);
                return;
            }
            Trace.traceBegin(64, "killProcessGroup");
            Process.killProcessGroup(msg.arg1, msg.arg2);
            Trace.traceEnd(64);
        }
    }

    ProcessList() {
        MemInfoReader minfo = new MemInfoReader();
        minfo.readMemInfo();
        this.mTotalMemMb = minfo.getTotalSize() / 1048576;
        updateOomLevels(0, 0, false);
    }

    /* access modifiers changed from: package-private */
    public void init(ActivityManagerService service, ActiveUids activeUids) {
        this.mService = service;
        this.mActiveUids = activeUids;
        if (sKillHandler == null) {
            sKillThread = new ServiceThread("ActivityManager:kill", -2, true);
            sKillThread.start();
            sKillHandler = new KillHandler(sKillThread.getLooper());
        }
    }

    /* access modifiers changed from: package-private */
    public void applyDisplaySize(WindowManagerService wm) {
        if (!this.mHaveDisplaySize) {
            Point p = new Point();
            wm.getBaseDisplaySize(0, p);
            if (p.x != 0 && p.y != 0) {
                updateOomLevels(p.x, p.y, true);
                this.mHaveDisplaySize = true;
            }
        }
    }

    private void updateOomLevels(int displayWidth, int displayHeight, boolean write) {
        float scaleMem = ((float) (this.mTotalMemMb - 350)) / 350.0f;
        float scaleDisp = (((float) (displayWidth * displayHeight)) - ((float) 384000)) / ((float) (1024000 - 384000));
        float scale = scaleMem > scaleDisp ? scaleMem : scaleDisp;
        if (scale < 0.0f) {
            scale = 0.0f;
        } else if (scale > 1.0f) {
            scale = 1.0f;
        }
        int minfree_adj = Resources.getSystem().getInteger(17694830);
        int minfree_abs = Resources.getSystem().getInteger(17694829);
        boolean is64bit = Build.SUPPORTED_64_BIT_ABIS.length > 0;
        for (int i = 0; i < this.mOomAdj.length; i++) {
            int low = this.mOomMinFreeLow[i];
            int high = this.mOomMinFreeHigh[i];
            if (is64bit) {
                if (i == 4) {
                    high = (high * 3) / 2;
                } else if (i == 5) {
                    high = (high * 7) / 4;
                }
            }
            this.mOomMinFree[i] = (int) (((float) low) + (((float) (high - low)) * scale));
        }
        if (minfree_abs >= 0) {
            int i2 = 0;
            while (true) {
                int[] iArr = this.mOomAdj;
                if (i2 >= iArr.length) {
                    break;
                }
                int[] iArr2 = this.mOomMinFree;
                iArr2[i2] = (int) ((((float) minfree_abs) * ((float) iArr2[i2])) / ((float) iArr2[iArr.length - 1]));
                i2++;
            }
        }
        if (minfree_adj != 0) {
            int i3 = 0;
            while (true) {
                int[] iArr3 = this.mOomAdj;
                if (i3 >= iArr3.length) {
                    break;
                }
                int[] iArr4 = this.mOomMinFree;
                iArr4[i3] = iArr4[i3] + ((int) ((((float) minfree_adj) * ((float) iArr4[i3])) / ((float) iArr4[iArr3.length - 1])));
                if (iArr4[i3] < 0) {
                    iArr4[i3] = 0;
                }
                i3++;
            }
        }
        this.mCachedRestoreLevel = (getMemLevel(CACHED_APP_MAX_ADJ) / 1024) / 3;
        int reserve = (((displayWidth * displayHeight) * 4) * 3) / 1024;
        int reserve_adj = Resources.getSystem().getInteger(17694809);
        int reserve_abs = Resources.getSystem().getInteger(17694808);
        if (reserve_abs >= 0) {
            reserve = reserve_abs;
        }
        if (reserve_adj != 0 && (reserve = reserve + reserve_adj) < 0) {
            reserve = 0;
        }
        if (write) {
            ByteBuffer buf = ByteBuffer.allocate(((this.mOomAdj.length * 2) + 1) * 4);
            buf.putInt(0);
            for (int i4 = 0; i4 < this.mOomAdj.length; i4++) {
                buf.putInt((this.mOomMinFree[i4] * 1024) / 4096);
                buf.putInt(this.mOomAdj[i4]);
            }
            writeLmkd(buf, (ByteBuffer) null);
            SystemProperties.set("sys.sysctl.extra_free_kbytes", Integer.toString(reserve));
        }
    }

    public static int computeEmptyProcessLimit(int totalProcessLimit) {
        return totalProcessLimit / 2;
    }

    private static String buildOomTag(String prefix, String compactPrefix, String space, int val, int base, boolean compact) {
        int diff = val - base;
        if (diff != 0) {
            String str = "+";
            if (diff < 10) {
                StringBuilder sb = new StringBuilder();
                sb.append(prefix);
                if (!compact) {
                    str = "+ ";
                }
                sb.append(str);
                sb.append(Integer.toString(diff));
                return sb.toString();
            }
            return prefix + str + Integer.toString(diff);
        } else if (compact) {
            return compactPrefix;
        } else {
            if (space == null) {
                return prefix;
            }
            return prefix + space;
        }
    }

    public static String makeOomAdjString(int setAdj, boolean compact) {
        if (setAdj >= CACHED_APP_MIN_ADJ) {
            return buildOomTag("cch", "cch", "   ", setAdj, CACHED_APP_MIN_ADJ, compact);
        }
        if (setAdj >= 800) {
            return buildOomTag("svcb  ", "svcb", (String) null, setAdj, 800, compact);
        }
        if (setAdj >= 700) {
            return buildOomTag("prev  ", "prev", (String) null, setAdj, 700, compact);
        }
        if (setAdj >= 600) {
            return buildOomTag("home  ", "home", (String) null, setAdj, 600, compact);
        }
        if (setAdj >= 500) {
            return buildOomTag("svc   ", "svc", (String) null, setAdj, 500, compact);
        }
        if (setAdj >= HEAVY_WEIGHT_APP_ADJ) {
            return buildOomTag("hvy   ", "hvy", (String) null, setAdj, HEAVY_WEIGHT_APP_ADJ, compact);
        }
        if (setAdj >= 300) {
            return buildOomTag("bkup  ", "bkup", (String) null, setAdj, 300, compact);
        }
        if (setAdj >= 250) {
            return buildOomTag("prcl  ", "prcl", (String) null, setAdj, 250, compact);
        }
        if (setAdj >= 200) {
            return buildOomTag("prcp  ", "prcp", (String) null, setAdj, 200, compact);
        }
        if (setAdj >= 100) {
            return buildOomTag("vis", "vis", "   ", setAdj, 100, compact);
        }
        if (setAdj >= 0) {
            return buildOomTag("fore  ", "fore", (String) null, setAdj, 0, compact);
        }
        if (setAdj >= PERSISTENT_SERVICE_ADJ) {
            return buildOomTag("psvc  ", "psvc", (String) null, setAdj, PERSISTENT_SERVICE_ADJ, compact);
        }
        if (setAdj >= PERSISTENT_PROC_ADJ) {
            return buildOomTag("pers  ", "pers", (String) null, setAdj, PERSISTENT_PROC_ADJ, compact);
        }
        if (setAdj >= -900) {
            return buildOomTag("sys   ", "sys", (String) null, setAdj, -900, compact);
        }
        if (setAdj >= -1000) {
            return buildOomTag("ntv  ", "ntv", (String) null, setAdj, -1000, compact);
        }
        return Integer.toString(setAdj);
    }

    public static String makeProcStateString(int curProcState) {
        switch (curProcState) {
            case 0:
                return "PER ";
            case 1:
                return "PERU";
            case 2:
                return "TOP ";
            case 3:
                return "FGSL";
            case 4:
                return "BTOP";
            case 5:
                return "FGS ";
            case 6:
                return "BFGS";
            case 7:
                return "IMPF";
            case 8:
                return "IMPB";
            case 9:
                return "TRNB";
            case 10:
                return "BKUP";
            case 11:
                return "SVC ";
            case 12:
                return "RCVR";
            case 13:
                return "TPSL";
            case 14:
                return "HVY ";
            case 15:
                return IGestureStrategy.TYPE_HOME_ANIMATION;
            case 16:
                return "LAST";
            case 17:
                return "CAC ";
            case 18:
                return "CACC";
            case 19:
                return "CRE ";
            case 20:
                return "CEM ";
            case 21:
                return "NONE";
            default:
                return "??";
        }
    }

    public static int makeProcStateProtoEnum(int curProcState) {
        switch (curProcState) {
            case -1:
                return CACHED_APP_MAX_ADJ;
            case 0:
                return 1000;
            case 1:
                return 1001;
            case 2:
                return 1002;
            case 3:
                return 1003;
            case 4:
                return 1020;
            case 5:
                return 1003;
            case 6:
                return 1004;
            case 7:
                return 1005;
            case 8:
                return 1006;
            case 9:
                return 1007;
            case 10:
                return 1008;
            case 11:
                return 1009;
            case 12:
                return 1010;
            case 13:
                return 1011;
            case 14:
                return 1012;
            case 15:
                return 1013;
            case 16:
                return 1014;
            case 17:
                return 1015;
            case 18:
                return 1016;
            case 19:
                return 1017;
            case 20:
                return 1018;
            case 21:
                return 1019;
            default:
                return 998;
        }
    }

    public static void appendRamKb(StringBuilder sb, long ramKb) {
        int j = 0;
        int fact = 10;
        while (j < 6) {
            if (ramKb < ((long) fact)) {
                sb.append(' ');
            }
            j++;
            fact *= 10;
        }
        sb.append(ramKb);
    }

    public static final class ProcStateMemTracker {
        final int[] mHighestMem = new int[5];
        int mPendingHighestMemState;
        int mPendingMemState;
        float mPendingScalingFactor;
        final float[] mScalingFactor = new float[5];
        int mTotalHighestMem = 4;

        public ProcStateMemTracker() {
            for (int i = 0; i < 5; i++) {
                this.mHighestMem[i] = 5;
                this.mScalingFactor[i] = 1.0f;
            }
            this.mPendingMemState = -1;
        }

        public void dumpLine(PrintWriter pw) {
            pw.print("best=");
            pw.print(this.mTotalHighestMem);
            pw.print(" (");
            boolean needSep = false;
            for (int i = 0; i < 5; i++) {
                if (this.mHighestMem[i] < 5) {
                    if (needSep) {
                        pw.print(", ");
                    }
                    pw.print(i);
                    pw.print("=");
                    pw.print(this.mHighestMem[i]);
                    pw.print(" ");
                    pw.print(this.mScalingFactor[i]);
                    pw.print("x");
                    needSep = true;
                }
            }
            pw.print(")");
            if (this.mPendingMemState >= 0) {
                pw.print(" / pending state=");
                pw.print(this.mPendingMemState);
                pw.print(" highest=");
                pw.print(this.mPendingHighestMemState);
                pw.print(" ");
                pw.print(this.mPendingScalingFactor);
                pw.print("x");
            }
            pw.println();
        }
    }

    public static boolean procStatesDifferForMem(int procState1, int procState2) {
        int[] iArr = sProcStateToProcMem;
        return iArr[procState1] != iArr[procState2];
    }

    public static long minTimeFromStateChange(boolean test) {
        if (test) {
            return JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY;
        }
        return 15000;
    }

    public static void commitNextPssTime(ProcStateMemTracker tracker) {
        if (tracker.mPendingMemState >= 0) {
            tracker.mHighestMem[tracker.mPendingMemState] = tracker.mPendingHighestMemState;
            tracker.mScalingFactor[tracker.mPendingMemState] = tracker.mPendingScalingFactor;
            tracker.mTotalHighestMem = tracker.mPendingHighestMemState;
            tracker.mPendingMemState = -1;
        }
    }

    public static void abortNextPssTime(ProcStateMemTracker tracker) {
        tracker.mPendingMemState = -1;
    }

    public static long computeNextPssTime(int procState, ProcStateMemTracker tracker, boolean test, boolean sleeping, long now) {
        float scalingFactor;
        boolean first;
        long[] table;
        int memState = sProcStateToProcMem[procState];
        if (tracker != null) {
            int highestMemState = memState < tracker.mTotalHighestMem ? memState : tracker.mTotalHighestMem;
            first = highestMemState < tracker.mHighestMem[memState];
            tracker.mPendingMemState = memState;
            tracker.mPendingHighestMemState = highestMemState;
            if (first) {
                scalingFactor = 1.0f;
                tracker.mPendingScalingFactor = 1.0f;
            } else {
                scalingFactor = tracker.mScalingFactor[memState];
                tracker.mPendingScalingFactor = 1.5f * scalingFactor;
            }
        } else {
            first = true;
            scalingFactor = 1.0f;
        }
        if (test) {
            table = first ? sTestFirstPssTimes : sTestSamePssTimes;
        } else if (first) {
            table = sleeping ? sFirstAsleepPssTimes : sFirstAwakePssTimes;
        } else {
            table = sleeping ? sSameAsleepPssTimes : sSameAwakePssTimes;
        }
        long delay = (long) (((float) table[memState]) * scalingFactor);
        if (delay > 3600000) {
            delay = 3600000;
        }
        return now + delay;
    }

    /* access modifiers changed from: package-private */
    public long getMemLevel(int adjustment) {
        int i = 0;
        while (true) {
            int[] iArr = this.mOomAdj;
            if (i >= iArr.length) {
                return (long) (this.mOomMinFree[iArr.length - 1] * 1024);
            }
            if (adjustment <= iArr[i]) {
                return (long) (this.mOomMinFree[i] * 1024);
            }
            i++;
        }
    }

    /* access modifiers changed from: package-private */
    public long getCachedRestoreThresholdKb() {
        return this.mCachedRestoreLevel;
    }

    public static void setOomAdj(int pid, int uid, int amt) {
        if (pid > 0 && amt != 1001) {
            long start = SystemClock.elapsedRealtime();
            ByteBuffer buf = ByteBuffer.allocate(16);
            buf.putInt(1);
            buf.putInt(pid);
            buf.putInt(uid);
            buf.putInt(amt);
            writeLmkd(buf, (ByteBuffer) null);
            long now = SystemClock.elapsedRealtime();
            if (now - start > 250) {
                Slog.w(TAG, "SLOW OOM ADJ: " + (now - start) + "ms for pid " + pid + " = " + amt);
            }
        }
    }

    public static final void remove(int pid) {
        if (pid > 0) {
            ByteBuffer buf = ByteBuffer.allocate(8);
            buf.putInt(2);
            buf.putInt(pid);
            writeLmkd(buf, (ByteBuffer) null);
        }
    }

    public static final Integer getLmkdKillCount(int min_oom_adj, int max_oom_adj) {
        ByteBuffer buf = ByteBuffer.allocate(12);
        ByteBuffer repl = ByteBuffer.allocate(8);
        buf.putInt(4);
        buf.putInt(min_oom_adj);
        buf.putInt(max_oom_adj);
        if (!writeLmkd(buf, repl)) {
            return null;
        }
        if (repl.getInt() == 4) {
            return new Integer(repl.getInt());
        }
        Slog.e(TAG, "Failed to get kill count, code mismatch");
        return null;
    }

    @GuardedBy({"sLmkdSocketLock"})
    private static boolean openLmkdSocketLS() {
        try {
            sLmkdSocket = new LocalSocket(3);
            sLmkdSocket.connect(new LocalSocketAddress("lmkd", LocalSocketAddress.Namespace.RESERVED));
            sLmkdOutputStream = sLmkdSocket.getOutputStream();
            sLmkdInputStream = sLmkdSocket.getInputStream();
            return true;
        } catch (IOException e) {
            Slog.w(TAG, "lowmemorykiller daemon socket open failed");
            sLmkdSocket = null;
            return false;
        }
    }

    @GuardedBy({"sLmkdSocketLock"})
    private static boolean writeLmkdCommandLS(ByteBuffer buf) {
        try {
            sLmkdOutputStream.write(buf.array(), 0, buf.position());
            return true;
        } catch (IOException e) {
            Slog.w(TAG, "Error writing to lowmemorykiller socket");
            IoUtils.closeQuietly(sLmkdSocket);
            sLmkdSocket = null;
            return false;
        }
    }

    @GuardedBy({"sLmkdSocketLock"})
    private static boolean readLmkdReplyLS(ByteBuffer buf) {
        try {
            if (sLmkdInputStream.read(buf.array(), 0, buf.array().length) == buf.array().length) {
                return true;
            }
        } catch (IOException e) {
            Slog.w(TAG, "Error reading from lowmemorykiller socket");
        }
        IoUtils.closeQuietly(sLmkdSocket);
        sLmkdSocket = null;
        return false;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:25:0x0037, code lost:
        return true;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static boolean writeLmkd(java.nio.ByteBuffer r4, java.nio.ByteBuffer r5) {
        /*
            java.lang.Object r0 = sLmkdSocketLock
            monitor-enter(r0)
            r1 = 0
        L_0x0004:
            r2 = 3
            if (r1 >= r2) goto L_0x003c
            android.net.LocalSocket r3 = sLmkdSocket     // Catch:{ all -> 0x003f }
            if (r3 != 0) goto L_0x0028
            boolean r3 = openLmkdSocketLS()     // Catch:{ all -> 0x003f }
            if (r3 != 0) goto L_0x0019
            r2 = 1000(0x3e8, double:4.94E-321)
            java.lang.Thread.sleep(r2)     // Catch:{ InterruptedException -> 0x0017 }
            goto L_0x0039
        L_0x0017:
            r2 = move-exception
            goto L_0x0039
        L_0x0019:
            r3 = 4
            java.nio.ByteBuffer r3 = java.nio.ByteBuffer.allocate(r3)     // Catch:{ all -> 0x003f }
            r3.putInt(r2)     // Catch:{ all -> 0x003f }
            boolean r2 = writeLmkdCommandLS(r3)     // Catch:{ all -> 0x003f }
            if (r2 != 0) goto L_0x0028
            goto L_0x0039
        L_0x0028:
            boolean r2 = writeLmkdCommandLS(r4)     // Catch:{ all -> 0x003f }
            if (r2 == 0) goto L_0x0039
            if (r5 == 0) goto L_0x0036
            boolean r2 = readLmkdReplyLS(r5)     // Catch:{ all -> 0x003f }
            if (r2 == 0) goto L_0x0039
        L_0x0036:
            monitor-exit(r0)     // Catch:{ all -> 0x003f }
            r0 = 1
            return r0
        L_0x0039:
            int r1 = r1 + 1
            goto L_0x0004
        L_0x003c:
            monitor-exit(r0)     // Catch:{ all -> 0x003f }
            r0 = 0
            return r0
        L_0x003f:
            r1 = move-exception
            monitor-exit(r0)     // Catch:{ all -> 0x003f }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ProcessList.writeLmkd(java.nio.ByteBuffer, java.nio.ByteBuffer):boolean");
    }

    static void killProcessGroup(int uid, int pid) {
        KillHandler killHandler = sKillHandler;
        if (killHandler != null) {
            killHandler.sendMessage(killHandler.obtainMessage(4000, uid, pid));
            return;
        }
        Slog.w(TAG, "Asked to kill process group before system bringup!");
        Process.killProcessGroup(uid, pid);
    }

    /* access modifiers changed from: package-private */
    public final ProcessRecord getProcessRecordLocked(String processName, int uid, boolean keepIfLarge) {
        if (uid == 1000) {
            SparseArray<ProcessRecord> procs = (SparseArray) this.mProcessNames.getMap().get(processName);
            if (procs == null) {
                return null;
            }
            int procCount = procs.size();
            for (int i = 0; i < procCount; i++) {
                int procUid = procs.keyAt(i);
                if (UserHandle.isCore(procUid) && UserHandle.isSameUser(procUid, uid)) {
                    return procs.valueAt(i);
                }
            }
        }
        ProcessRecord proc = (ProcessRecord) this.mProcessNames.get(processName, uid);
        if (proc != null && !keepIfLarge && this.mService.mLastMemoryLevel > 0 && proc.setProcState >= 20 && proc.lastCachedPss >= getCachedRestoreThresholdKb()) {
            if (proc.baseProcessTracker != null) {
                proc.baseProcessTracker.reportCachedKill(proc.pkgList.mPkgList, proc.lastCachedPss);
                for (int ipkg = proc.pkgList.size() - 1; ipkg >= 0; ipkg--) {
                    ProcessStats.ProcessStateHolder holder = proc.pkgList.valueAt(ipkg);
                    StatsLog.write(17, proc.info.uid, holder.state.getName(), holder.state.getPackage(), proc.lastCachedPss, holder.appVersion);
                }
            }
            proc.kill(Long.toString(proc.lastCachedPss) + "k from cached", true);
        }
        return proc;
    }

    /* access modifiers changed from: package-private */
    public void getMemoryInfo(ActivityManager.MemoryInfo outInfo) {
        long homeAppMem = getMemLevel(600);
        long cachedAppMem = getMemLevel(CACHED_APP_MIN_ADJ);
        outInfo.availMem = Process.getFreeMemory();
        outInfo.totalMem = Process.getTotalMemory();
        outInfo.threshold = homeAppMem;
        outInfo.lowMemory = outInfo.availMem < ((cachedAppMem - homeAppMem) / 2) + homeAppMem;
        outInfo.hiddenAppThreshold = cachedAppMem;
        outInfo.secondaryServerThreshold = getMemLevel(500);
        outInfo.visibleAppThreshold = getMemLevel(100);
        outInfo.foregroundAppThreshold = getMemLevel(0);
    }

    /* access modifiers changed from: package-private */
    public ProcessRecord findAppProcessLocked(IBinder app, String reason) {
        int NP = this.mProcessNames.getMap().size();
        for (int ip = 0; ip < NP; ip++) {
            SparseArray<ProcessRecord> apps = (SparseArray) this.mProcessNames.getMap().valueAt(ip);
            int NA = apps.size();
            for (int ia = 0; ia < NA; ia++) {
                ProcessRecord p = apps.valueAt(ia);
                if (p.thread != null && p.thread.asBinder() == app) {
                    return p;
                }
            }
        }
        Slog.w(TAG, "Can't find mystery application for " + reason + " from pid=" + Binder.getCallingPid() + " uid=" + Binder.getCallingUid() + ": " + app);
        return null;
    }

    private void checkSlow(long startTime, String where) {
        long now = SystemClock.uptimeMillis();
        if (now - startTime > 100) {
            Slog.w(TAG, "Slow operation: " + (now - startTime) + "ms so far, now at " + where);
        }
    }

    /* Debug info: failed to restart local var, previous not found, register: 30 */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v0, resolved type: com.android.server.am.ProcessList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r13v0, resolved type: com.android.server.am.ProcessRecord} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v1, resolved type: com.android.server.am.ProcessRecord} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v2, resolved type: com.android.server.am.ProcessList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v6, resolved type: com.android.server.am.ProcessRecord} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v8, resolved type: com.android.server.am.ProcessList} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v10, resolved type: com.android.server.am.ProcessRecord} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r15v11, resolved type: com.android.server.am.ProcessRecord} */
    /* access modifiers changed from: package-private */
    /* JADX WARNING: Code restructure failed: missing block: B:105:0x020d, code lost:
        if (com.android.server.pm.dex.DexManager.isPackageSelectedToRunOob((java.util.Collection<java.lang.String>) r13.pkgList.mPkgList.keySet()) != false) goto L_0x020f;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:60:0x016c, code lost:
        if (r15.mService.mSafeMode == true) goto L_0x016e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:74:0x019f, code lost:
        if ("true".equals(r6) != false) goto L_0x01a1;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:82:0x01b4, code lost:
        if ("true".equals(r5) != false) goto L_0x01b6;
     */
    /* JADX WARNING: Multi-variable type inference failed */
    /* JADX WARNING: Removed duplicated region for block: B:112:0x0238 A[Catch:{ RemoteException -> 0x0106, RuntimeException -> 0x00fc }] */
    /* JADX WARNING: Removed duplicated region for block: B:113:0x023a A[Catch:{ RemoteException -> 0x0106, RuntimeException -> 0x00fc }] */
    /* JADX WARNING: Removed duplicated region for block: B:130:0x0278 A[SYNTHETIC, Splitter:B:130:0x0278] */
    /* JADX WARNING: Removed duplicated region for block: B:142:0x02b9  */
    /* JADX WARNING: Removed duplicated region for block: B:144:0x02bd  */
    /* JADX WARNING: Removed duplicated region for block: B:145:0x02c0 A[SYNTHETIC, Splitter:B:145:0x02c0] */
    /* JADX WARNING: Removed duplicated region for block: B:148:0x02c6 A[SYNTHETIC, Splitter:B:148:0x02c6] */
    /* JADX WARNING: Removed duplicated region for block: B:156:0x02d8  */
    /* JADX WARNING: Removed duplicated region for block: B:161:0x02e1 A[SYNTHETIC, Splitter:B:161:0x02e1] */
    /* JADX WARNING: Removed duplicated region for block: B:164:0x02eb  */
    /* JADX WARNING: Removed duplicated region for block: B:168:0x02fd  */
    /* JADX WARNING: Removed duplicated region for block: B:173:0x032e  */
    /* JADX WARNING: Removed duplicated region for block: B:177:0x0346 A[Catch:{ RuntimeException -> 0x0386 }] */
    /* JADX WARNING: Removed duplicated region for block: B:178:0x0349 A[Catch:{ RuntimeException -> 0x0386 }] */
    @com.android.internal.annotations.GuardedBy({"mService"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean startProcessLocked(com.android.server.am.ProcessRecord r31, com.android.server.am.HostingRecord r32, boolean r33, boolean r34, java.lang.String r35) {
        /*
            r30 = this;
            r15 = r30
            r13 = r31
            java.lang.String r0 = ""
            java.lang.String r14 = "ActivityManager"
            java.lang.String r1 = "1"
            boolean r2 = r13.pendingStart
            r3 = 1
            if (r2 == 0) goto L_0x0010
            return r3
        L_0x0010:
            long r11 = android.os.SystemClock.uptimeMillis()
            int r2 = r13.pid
            r10 = 0
            if (r2 <= 0) goto L_0x0042
            int r2 = r13.pid
            int r4 = com.android.server.am.ActivityManagerService.MY_PID
            if (r2 == r4) goto L_0x0042
            java.lang.String r2 = "startProcess: removing from pids map"
            r15.checkSlow(r11, r2)
            com.android.server.am.ActivityManagerService r2 = r15.mService
            com.android.server.am.ActivityManagerService$PidMap r2 = r2.mPidsSelfLocked
            r2.remove(r13)
            com.android.server.am.ActivityManagerService r2 = r15.mService
            com.android.server.am.ActivityManagerService$MainHandler r2 = r2.mHandler
            r4 = 20
            r2.removeMessages(r4, r13)
            java.lang.String r2 = "startProcess: done removing from pids map"
            r15.checkSlow(r11, r2)
            r13.setPid(r10)
            r4 = 0
            r13.startSeq = r4
        L_0x0042:
            com.android.server.am.ActivityManagerService r2 = r15.mService
            java.util.ArrayList<com.android.server.am.ProcessRecord> r2 = r2.mProcessesOnHold
            r2.remove(r13)
            java.lang.String r2 = "startProcess: starting to update cpu stats"
            r15.checkSlow(r11, r2)
            com.android.server.am.ActivityManagerService r2 = r15.mService
            r2.updateCpuStats()
            java.lang.String r2 = "startProcess: done updating cpu stats"
            r15.checkSlow(r11, r2)
            int r2 = r13.uid     // Catch:{ RemoteException -> 0x0398 }
            int r2 = android.os.UserHandle.getUserId(r2)     // Catch:{ RemoteException -> 0x0398 }
            android.content.pm.IPackageManager r4 = android.app.AppGlobals.getPackageManager()     // Catch:{ RemoteException -> 0x0398 }
            android.content.pm.ApplicationInfo r5 = r13.info     // Catch:{ RemoteException -> 0x0398 }
            java.lang.String r5 = r5.packageName     // Catch:{ RemoteException -> 0x0398 }
            r4.checkPackageStartable(r5, r2)     // Catch:{ RemoteException -> 0x0398 }
            int r2 = r13.uid     // Catch:{ RuntimeException -> 0x038f }
            r4 = 0
            r5 = 0
            boolean r6 = r13.isolated     // Catch:{ RuntimeException -> 0x038f }
            if (r6 != 0) goto L_0x010c
            r6 = 0
            java.lang.String r8 = "startProcess: getting gids from package manager"
            r15.checkSlow(r11, r8)     // Catch:{ RemoteException -> 0x0106 }
            android.content.pm.IPackageManager r8 = android.app.AppGlobals.getPackageManager()     // Catch:{ RemoteException -> 0x0106 }
            android.content.pm.ApplicationInfo r9 = r13.info     // Catch:{ RemoteException -> 0x0106 }
            java.lang.String r9 = r9.packageName     // Catch:{ RemoteException -> 0x0106 }
            r7 = 268435456(0x10000000, float:2.5243549E-29)
            int r3 = r13.userId     // Catch:{ RemoteException -> 0x0106 }
            int[] r3 = r8.getPackageGids(r9, r7, r3)     // Catch:{ RemoteException -> 0x0106 }
            r6 = r3
            boolean r3 = android.os.storage.StorageManager.hasIsolatedStorage()     // Catch:{ RemoteException -> 0x0106 }
            if (r3 == 0) goto L_0x0096
            if (r34 == 0) goto L_0x0096
            r3 = 6
            goto L_0x00a8
        L_0x0096:
            java.lang.Class<android.os.storage.StorageManagerInternal> r3 = android.os.storage.StorageManagerInternal.class
            java.lang.Object r3 = com.android.server.LocalServices.getService(r3)     // Catch:{ RemoteException -> 0x0106 }
            android.os.storage.StorageManagerInternal r3 = (android.os.storage.StorageManagerInternal) r3     // Catch:{ RemoteException -> 0x0106 }
            android.content.pm.ApplicationInfo r7 = r13.info     // Catch:{ RemoteException -> 0x0106 }
            java.lang.String r7 = r7.packageName     // Catch:{ RemoteException -> 0x0106 }
            int r7 = r3.getExternalStorageMountMode(r2, r7)     // Catch:{ RemoteException -> 0x0106 }
            r5 = r7
            r3 = r5
        L_0x00a8:
            boolean r5 = com.android.internal.util.ArrayUtils.isEmpty(r6)     // Catch:{ RuntimeException -> 0x00fc }
            r7 = 3
            if (r5 == 0) goto L_0x00b4
            int[] r5 = new int[r7]     // Catch:{ RuntimeException -> 0x00fc }
            r4 = r5
            goto L_0x00bd
        L_0x00b4:
            int r5 = r6.length     // Catch:{ RuntimeException -> 0x00fc }
            int r5 = r5 + r7
            int[] r5 = new int[r5]     // Catch:{ RuntimeException -> 0x00fc }
            r4 = r5
            int r5 = r6.length     // Catch:{ RuntimeException -> 0x00fc }
            java.lang.System.arraycopy(r6, r10, r4, r7, r5)     // Catch:{ RuntimeException -> 0x00fc }
        L_0x00bd:
            int r5 = android.os.UserHandle.getAppId(r2)     // Catch:{ RuntimeException -> 0x00fc }
            int r5 = android.os.UserHandle.getSharedAppGid(r5)     // Catch:{ RuntimeException -> 0x00fc }
            r4[r10] = r5     // Catch:{ RuntimeException -> 0x00fc }
            int r5 = android.os.UserHandle.getAppId(r2)     // Catch:{ RuntimeException -> 0x00fc }
            int r5 = android.os.UserHandle.getCacheAppGid(r5)     // Catch:{ RuntimeException -> 0x00fc }
            r7 = 1
            r4[r7] = r5     // Catch:{ RuntimeException -> 0x00fc }
            int r5 = android.os.UserHandle.getUserId(r2)     // Catch:{ RuntimeException -> 0x00fc }
            int r5 = android.os.UserHandle.getUserGid(r5)     // Catch:{ RuntimeException -> 0x00fc }
            r7 = 2
            r4[r7] = r5     // Catch:{ RuntimeException -> 0x00fc }
            r5 = r4[r10]     // Catch:{ RuntimeException -> 0x00fc }
            r8 = -1
            if (r5 != r8) goto L_0x00e6
            r5 = r4[r7]     // Catch:{ RuntimeException -> 0x00fc }
            r4[r10] = r5     // Catch:{ RuntimeException -> 0x00fc }
        L_0x00e6:
            r5 = 1
            r7 = r4[r5]     // Catch:{ RuntimeException -> 0x00fc }
            if (r7 != r8) goto L_0x00f0
            r7 = 2
            r8 = r4[r7]     // Catch:{ RuntimeException -> 0x00fc }
            r4[r5] = r8     // Catch:{ RuntimeException -> 0x00fc }
        L_0x00f0:
            int r5 = android.os.UserHandle.getUserId(r2)     // Catch:{ RuntimeException -> 0x00fc }
            int[] r5 = com.android.server.am.ActivityManagerServiceInjector.computeGids(r5, r4, r13)     // Catch:{ RuntimeException -> 0x00fc }
            r4 = r5
            r8 = r3
            r7 = r4
            goto L_0x010e
        L_0x00fc:
            r0 = move-exception
            r16 = r10
            r27 = r11
            r15 = r13
            r29 = r14
            goto L_0x03a6
        L_0x0106:
            r0 = move-exception
            java.lang.RuntimeException r1 = r0.rethrowAsRuntimeException()     // Catch:{ RuntimeException -> 0x00fc }
            throw r1     // Catch:{ RuntimeException -> 0x00fc }
        L_0x010c:
            r7 = r4
            r8 = r5
        L_0x010e:
            r13.mountMode = r8     // Catch:{ RuntimeException -> 0x038f }
            java.lang.String r3 = "startProcess: building args"
            r15.checkSlow(r11, r3)     // Catch:{ RuntimeException -> 0x038f }
            com.android.server.am.ActivityManagerService r3 = r15.mService     // Catch:{ RuntimeException -> 0x038f }
            com.android.server.wm.ActivityTaskManagerInternal r3 = r3.mAtmInternal     // Catch:{ RuntimeException -> 0x038f }
            com.android.server.wm.WindowProcessController r4 = r31.getWindowProcessController()     // Catch:{ RuntimeException -> 0x038f }
            boolean r3 = r3.isFactoryTestProcess(r4)     // Catch:{ RuntimeException -> 0x038f }
            if (r3 == 0) goto L_0x0128
            r2 = 0
            r18 = r2
            goto L_0x012a
        L_0x0128:
            r18 = r2
        L_0x012a:
            r2 = 0
            android.content.pm.ApplicationInfo r3 = r13.info     // Catch:{ RuntimeException -> 0x038f }
            int r3 = r3.flags     // Catch:{ RuntimeException -> 0x038f }
            r4 = 2
            r3 = r3 & r4
            if (r3 == 0) goto L_0x015f
            r2 = r2 | 1
            r2 = r2 | 256(0x100, float:3.59E-43)
            r2 = r2 | r4
            com.android.server.am.ActivityManagerService r3 = r15.mService     // Catch:{ RuntimeException -> 0x00fc }
            android.content.Context r3 = r3.mContext     // Catch:{ RuntimeException -> 0x00fc }
            android.content.ContentResolver r3 = r3.getContentResolver()     // Catch:{ RuntimeException -> 0x00fc }
            java.lang.String r4 = "art_verifier_verify_debuggable"
            r5 = 1
            int r3 = android.provider.Settings.Global.getInt(r3, r4, r5)     // Catch:{ RuntimeException -> 0x00fc }
            if (r3 != 0) goto L_0x015f
            r2 = r2 | 512(0x200, float:7.175E-43)
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x00fc }
            r3.<init>()     // Catch:{ RuntimeException -> 0x00fc }
            r3.append(r13)     // Catch:{ RuntimeException -> 0x00fc }
            java.lang.String r4 = ": ART verification disabled"
            r3.append(r4)     // Catch:{ RuntimeException -> 0x00fc }
            java.lang.String r3 = r3.toString()     // Catch:{ RuntimeException -> 0x00fc }
            android.util.Slog.w(r14, r3)     // Catch:{ RuntimeException -> 0x00fc }
        L_0x015f:
            android.content.pm.ApplicationInfo r3 = r13.info     // Catch:{ RuntimeException -> 0x038f }
            int r3 = r3.flags     // Catch:{ RuntimeException -> 0x038f }
            r3 = r3 & 16384(0x4000, float:2.2959E-41)
            if (r3 != 0) goto L_0x016e
            com.android.server.am.ActivityManagerService r3 = r15.mService     // Catch:{ RuntimeException -> 0x00fc }
            boolean r3 = r3.mSafeMode     // Catch:{ RuntimeException -> 0x00fc }
            r4 = 1
            if (r3 != r4) goto L_0x0170
        L_0x016e:
            r2 = r2 | 8
        L_0x0170:
            android.content.pm.ApplicationInfo r3 = r13.info     // Catch:{ RuntimeException -> 0x038f }
            int r3 = r3.privateFlags     // Catch:{ RuntimeException -> 0x038f }
            r4 = 8388608(0x800000, float:1.17549435E-38)
            r3 = r3 & r4
            if (r3 == 0) goto L_0x017d
            r3 = 32768(0x8000, float:4.5918E-41)
            r2 = r2 | r3
        L_0x017d:
            java.lang.String r3 = "debug.checkjni"
            java.lang.String r3 = android.os.SystemProperties.get(r3)     // Catch:{ RuntimeException -> 0x038f }
            boolean r3 = r1.equals(r3)     // Catch:{ RuntimeException -> 0x038f }
            if (r3 == 0) goto L_0x018b
            r2 = r2 | 2
        L_0x018b:
            java.lang.String r3 = "debug.generate-debug-info"
            java.lang.String r3 = android.os.SystemProperties.get(r3)     // Catch:{ RuntimeException -> 0x038f }
            r6 = r3
            boolean r3 = r1.equals(r6)     // Catch:{ RuntimeException -> 0x038f }
            java.lang.String r4 = "true"
            if (r3 != 0) goto L_0x01a1
            boolean r3 = r4.equals(r6)     // Catch:{ RuntimeException -> 0x00fc }
            if (r3 == 0) goto L_0x01a3
        L_0x01a1:
            r2 = r2 | 32
        L_0x01a3:
            java.lang.String r3 = "dalvik.vm.minidebuginfo"
            java.lang.String r3 = android.os.SystemProperties.get(r3)     // Catch:{ RuntimeException -> 0x038f }
            r5 = r3
            boolean r3 = r1.equals(r5)     // Catch:{ RuntimeException -> 0x038f }
            if (r3 != 0) goto L_0x01b6
            boolean r3 = r4.equals(r5)     // Catch:{ RuntimeException -> 0x00fc }
            if (r3 == 0) goto L_0x01b8
        L_0x01b6:
            r2 = r2 | 2048(0x800, float:2.87E-42)
        L_0x01b8:
            java.lang.String r3 = "debug.jni.logging"
            java.lang.String r3 = android.os.SystemProperties.get(r3)     // Catch:{ RuntimeException -> 0x038f }
            boolean r3 = r1.equals(r3)     // Catch:{ RuntimeException -> 0x038f }
            if (r3 == 0) goto L_0x01c6
            r2 = r2 | 16
        L_0x01c6:
            java.lang.String r3 = "debug.assert"
            java.lang.String r3 = android.os.SystemProperties.get(r3)     // Catch:{ RuntimeException -> 0x038f }
            boolean r1 = r1.equals(r3)     // Catch:{ RuntimeException -> 0x038f }
            if (r1 == 0) goto L_0x01d4
            r2 = r2 | 4
        L_0x01d4:
            com.android.server.am.ActivityManagerService r1 = r15.mService     // Catch:{ RuntimeException -> 0x038f }
            java.lang.String r1 = r1.mNativeDebuggingApp     // Catch:{ RuntimeException -> 0x038f }
            if (r1 == 0) goto L_0x01f1
            com.android.server.am.ActivityManagerService r1 = r15.mService     // Catch:{ RuntimeException -> 0x00fc }
            java.lang.String r1 = r1.mNativeDebuggingApp     // Catch:{ RuntimeException -> 0x00fc }
            java.lang.String r3 = r13.processName     // Catch:{ RuntimeException -> 0x00fc }
            boolean r1 = r1.equals(r3)     // Catch:{ RuntimeException -> 0x00fc }
            if (r1 == 0) goto L_0x01f1
            r1 = r2 | 64
            r1 = r1 | 32
            r2 = r1 | 128(0x80, float:1.794E-43)
            com.android.server.am.ActivityManagerService r1 = r15.mService     // Catch:{ RuntimeException -> 0x00fc }
            r3 = 0
            r1.mNativeDebuggingApp = r3     // Catch:{ RuntimeException -> 0x00fc }
        L_0x01f1:
            android.content.pm.ApplicationInfo r1 = r13.info     // Catch:{ RuntimeException -> 0x038f }
            boolean r1 = r1.isEmbeddedDexUsed()     // Catch:{ RuntimeException -> 0x038f }
            if (r1 != 0) goto L_0x020f
            android.content.pm.ApplicationInfo r1 = r13.info     // Catch:{ RuntimeException -> 0x00fc }
            boolean r1 = r1.isPrivilegedApp()     // Catch:{ RuntimeException -> 0x00fc }
            if (r1 == 0) goto L_0x0211
            com.android.server.am.ProcessRecord$PackageList r1 = r13.pkgList     // Catch:{ RuntimeException -> 0x00fc }
            android.util.ArrayMap<java.lang.String, com.android.internal.app.procstats.ProcessStats$ProcessStateHolder> r1 = r1.mPkgList     // Catch:{ RuntimeException -> 0x00fc }
            java.util.Set r1 = r1.keySet()     // Catch:{ RuntimeException -> 0x00fc }
            boolean r1 = com.android.server.pm.dex.DexManager.isPackageSelectedToRunOob((java.util.Collection<java.lang.String>) r1)     // Catch:{ RuntimeException -> 0x00fc }
            if (r1 == 0) goto L_0x0211
        L_0x020f:
            r2 = r2 | 1024(0x400, float:1.435E-42)
        L_0x0211:
            if (r33 != 0) goto L_0x0251
            com.android.server.am.ActivityManagerService r1 = r15.mService     // Catch:{ RuntimeException -> 0x00fc }
            com.android.server.am.ActivityManagerService$HiddenApiSettings r1 = r1.mHiddenApiBlacklist     // Catch:{ RuntimeException -> 0x00fc }
            boolean r1 = r1.isDisabled()     // Catch:{ RuntimeException -> 0x00fc }
            if (r1 != 0) goto L_0x0251
            android.content.pm.ApplicationInfo r1 = r13.info     // Catch:{ RuntimeException -> 0x00fc }
            com.android.server.am.ActivityManagerService r3 = r15.mService     // Catch:{ RuntimeException -> 0x00fc }
            com.android.server.am.ActivityManagerService$HiddenApiSettings r3 = r3.mHiddenApiBlacklist     // Catch:{ RuntimeException -> 0x00fc }
            int r3 = r3.getPolicy()     // Catch:{ RuntimeException -> 0x00fc }
            r1.maybeUpdateHiddenApiEnforcementPolicy(r3)     // Catch:{ RuntimeException -> 0x00fc }
            android.content.pm.ApplicationInfo r1 = r13.info     // Catch:{ RuntimeException -> 0x00fc }
            int r1 = r1.getHiddenApiEnforcementPolicy()     // Catch:{ RuntimeException -> 0x00fc }
            int r3 = com.android.internal.os.Zygote.API_ENFORCEMENT_POLICY_SHIFT     // Catch:{ RuntimeException -> 0x00fc }
            int r3 = r1 << r3
            r4 = r3 & 12288(0x3000, float:1.7219E-41)
            if (r4 != r3) goto L_0x023a
            r2 = r2 | r3
            goto L_0x0251
        L_0x023a:
            java.lang.IllegalStateException r0 = new java.lang.IllegalStateException     // Catch:{ RuntimeException -> 0x00fc }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x00fc }
            r4.<init>()     // Catch:{ RuntimeException -> 0x00fc }
            java.lang.String r9 = "Invalid API policy: "
            r4.append(r9)     // Catch:{ RuntimeException -> 0x00fc }
            r4.append(r1)     // Catch:{ RuntimeException -> 0x00fc }
            java.lang.String r4 = r4.toString()     // Catch:{ RuntimeException -> 0x00fc }
            r0.<init>(r4)     // Catch:{ RuntimeException -> 0x00fc }
            throw r0     // Catch:{ RuntimeException -> 0x00fc }
        L_0x0251:
            java.lang.String r1 = "persist.device_config.runtime_native.use_app_image_startup_cache"
            java.lang.String r1 = android.os.SystemProperties.get(r1, r0)     // Catch:{ RuntimeException -> 0x038f }
            r4 = r1
            boolean r1 = android.text.TextUtils.isEmpty(r4)     // Catch:{ RuntimeException -> 0x038f }
            if (r1 != 0) goto L_0x026d
            java.lang.String r1 = "false"
            boolean r1 = r4.equals(r1)     // Catch:{ RuntimeException -> 0x00fc }
            if (r1 != 0) goto L_0x026d
            r1 = 65536(0x10000, float:9.18355E-41)
            r1 = r1 | r2
            r17 = r1
            goto L_0x026f
        L_0x026d:
            r17 = r2
        L_0x026f:
            r1 = 0
            android.content.pm.ApplicationInfo r2 = r13.info     // Catch:{ RuntimeException -> 0x038f }
            int r2 = r2.flags     // Catch:{ RuntimeException -> 0x038f }
            r3 = 2
            r2 = r2 & r3
            if (r2 == 0) goto L_0x02b9
            java.lang.StringBuilder r2 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x00fc }
            r2.<init>()     // Catch:{ RuntimeException -> 0x00fc }
            android.content.pm.ApplicationInfo r3 = r13.info     // Catch:{ RuntimeException -> 0x00fc }
            java.lang.String r3 = r3.nativeLibraryDir     // Catch:{ RuntimeException -> 0x00fc }
            r2.append(r3)     // Catch:{ RuntimeException -> 0x00fc }
            java.lang.String r3 = "/wrap.sh"
            r2.append(r3)     // Catch:{ RuntimeException -> 0x00fc }
            java.lang.String r2 = r2.toString()     // Catch:{ RuntimeException -> 0x00fc }
            android.os.StrictMode$ThreadPolicy r3 = android.os.StrictMode.allowThreadDiskReads()     // Catch:{ RuntimeException -> 0x00fc }
            java.io.File r9 = new java.io.File     // Catch:{ all -> 0x02b4 }
            r9.<init>(r2)     // Catch:{ all -> 0x02b4 }
            boolean r9 = r9.exists()     // Catch:{ all -> 0x02b4 }
            if (r9 == 0) goto L_0x02ae
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ all -> 0x02b4 }
            r9.<init>()     // Catch:{ all -> 0x02b4 }
            java.lang.String r10 = "/system/bin/logwrapper "
            r9.append(r10)     // Catch:{ all -> 0x02b4 }
            r9.append(r2)     // Catch:{ all -> 0x02b4 }
            java.lang.String r9 = r9.toString()     // Catch:{ all -> 0x02b4 }
            r1 = r9
        L_0x02ae:
            android.os.StrictMode.setThreadPolicy(r3)     // Catch:{ RuntimeException -> 0x02ce }
            r19 = r1
            goto L_0x02bb
        L_0x02b4:
            r0 = move-exception
            android.os.StrictMode.setThreadPolicy(r3)     // Catch:{ RuntimeException -> 0x02ce }
            throw r0     // Catch:{ RuntimeException -> 0x02ce }
        L_0x02b9:
            r19 = r1
        L_0x02bb:
            if (r35 == 0) goto L_0x02c0
            r1 = r35
            goto L_0x02c4
        L_0x02c0:
            android.content.pm.ApplicationInfo r1 = r13.info     // Catch:{ RuntimeException -> 0x0386 }
            java.lang.String r1 = r1.primaryCpuAbi     // Catch:{ RuntimeException -> 0x0386 }
        L_0x02c4:
            if (r1 != 0) goto L_0x02d8
            java.lang.String[] r2 = android.os.Build.SUPPORTED_ABIS     // Catch:{ RuntimeException -> 0x02ce }
            r10 = 0
            r2 = r2[r10]     // Catch:{ RuntimeException -> 0x00fc }
            r1 = r2
            r3 = r1
            goto L_0x02da
        L_0x02ce:
            r0 = move-exception
            r27 = r11
            r15 = r13
            r29 = r14
            r16 = 0
            goto L_0x03a6
        L_0x02d8:
            r10 = 0
            r3 = r1
        L_0x02da:
            r1 = 0
            android.content.pm.ApplicationInfo r2 = r13.info     // Catch:{ RuntimeException -> 0x038f }
            java.lang.String r2 = r2.primaryCpuAbi     // Catch:{ RuntimeException -> 0x038f }
            if (r2 == 0) goto L_0x02eb
            android.content.pm.ApplicationInfo r2 = r13.info     // Catch:{ RuntimeException -> 0x00fc }
            java.lang.String r2 = r2.primaryCpuAbi     // Catch:{ RuntimeException -> 0x00fc }
            java.lang.String r2 = dalvik.system.VMRuntime.getInstructionSet(r2)     // Catch:{ RuntimeException -> 0x00fc }
            r1 = r2
            goto L_0x02ec
        L_0x02eb:
            r2 = r1
        L_0x02ec:
            r13.gids = r7     // Catch:{ RuntimeException -> 0x038f }
            r13.setRequiredAbi(r3)     // Catch:{ RuntimeException -> 0x038f }
            r13.instructionSet = r2     // Catch:{ RuntimeException -> 0x038f }
            android.content.pm.ApplicationInfo r1 = r13.info     // Catch:{ RuntimeException -> 0x038f }
            java.lang.String r1 = r1.seInfoUser     // Catch:{ RuntimeException -> 0x038f }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ RuntimeException -> 0x038f }
            if (r1 == 0) goto L_0x032e
            java.lang.String r1 = "SELinux tag not defined"
            java.lang.IllegalStateException r9 = new java.lang.IllegalStateException     // Catch:{ RuntimeException -> 0x00fc }
            java.lang.StringBuilder r10 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x02ce }
            r10.<init>()     // Catch:{ RuntimeException -> 0x02ce }
            r20 = r0
            java.lang.String r0 = "SELinux tag not defined for "
            r10.append(r0)     // Catch:{ RuntimeException -> 0x02ce }
            android.content.pm.ApplicationInfo r0 = r13.info     // Catch:{ RuntimeException -> 0x02ce }
            java.lang.String r0 = r0.packageName     // Catch:{ RuntimeException -> 0x02ce }
            r10.append(r0)     // Catch:{ RuntimeException -> 0x02ce }
            java.lang.String r0 = " (uid "
            r10.append(r0)     // Catch:{ RuntimeException -> 0x02ce }
            int r0 = r13.uid     // Catch:{ RuntimeException -> 0x02ce }
            r10.append(r0)     // Catch:{ RuntimeException -> 0x02ce }
            java.lang.String r0 = ")"
            r10.append(r0)     // Catch:{ RuntimeException -> 0x02ce }
            java.lang.String r0 = r10.toString()     // Catch:{ RuntimeException -> 0x02ce }
            r9.<init>(r0)     // Catch:{ RuntimeException -> 0x02ce }
            android.util.Slog.wtf(r14, r1, r9)     // Catch:{ RuntimeException -> 0x02ce }
            goto L_0x0330
        L_0x032e:
            r20 = r0
        L_0x0330:
            java.lang.StringBuilder r0 = new java.lang.StringBuilder     // Catch:{ RuntimeException -> 0x0386 }
            r0.<init>()     // Catch:{ RuntimeException -> 0x0386 }
            android.content.pm.ApplicationInfo r1 = r13.info     // Catch:{ RuntimeException -> 0x0386 }
            java.lang.String r1 = r1.seInfo     // Catch:{ RuntimeException -> 0x0386 }
            r0.append(r1)     // Catch:{ RuntimeException -> 0x0386 }
            android.content.pm.ApplicationInfo r1 = r13.info     // Catch:{ RuntimeException -> 0x0386 }
            java.lang.String r1 = r1.seInfoUser     // Catch:{ RuntimeException -> 0x0386 }
            boolean r1 = android.text.TextUtils.isEmpty(r1)     // Catch:{ RuntimeException -> 0x0386 }
            if (r1 == 0) goto L_0x0349
            r1 = r20
            goto L_0x034d
        L_0x0349:
            android.content.pm.ApplicationInfo r1 = r13.info     // Catch:{ RuntimeException -> 0x0386 }
            java.lang.String r1 = r1.seInfoUser     // Catch:{ RuntimeException -> 0x0386 }
        L_0x034d:
            r0.append(r1)     // Catch:{ RuntimeException -> 0x0386 }
            java.lang.String r9 = r0.toString()     // Catch:{ RuntimeException -> 0x0386 }
            java.lang.String r0 = "android.app.ActivityThread"
            java.lang.String r10 = "android.app.ActivityThread"
            r1 = r30
            r20 = r2
            r2 = r32
            r21 = r3
            r3 = r10
            r22 = r4
            r4 = r31
            r23 = r5
            r5 = r18
            r24 = r6
            r6 = r7
            r25 = r7
            r7 = r17
            r26 = r8
            r16 = 0
            r10 = r21
            r27 = r11
            r11 = r20
            r12 = r19
            r15 = r13
            r29 = r14
            r13 = r27
            boolean r1 = r1.startProcessLocked(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)     // Catch:{ RuntimeException -> 0x03a5 }
            return r1
        L_0x0386:
            r0 = move-exception
            r27 = r11
            r15 = r13
            r29 = r14
            r16 = 0
            goto L_0x0397
        L_0x038f:
            r0 = move-exception
            r16 = r10
            r27 = r11
            r15 = r13
            r29 = r14
        L_0x0397:
            goto L_0x03a6
        L_0x0398:
            r0 = move-exception
            r16 = r10
            r27 = r11
            r15 = r13
            r29 = r14
            java.lang.RuntimeException r1 = r0.rethrowAsRuntimeException()     // Catch:{ RuntimeException -> 0x03a5 }
            throw r1     // Catch:{ RuntimeException -> 0x03a5 }
        L_0x03a5:
            r0 = move-exception
        L_0x03a6:
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Failure starting process "
            r1.append(r2)
            java.lang.String r2 = r15.processName
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            r2 = r29
            android.util.Slog.e(r2, r1, r0)
            r1 = r30
            r2 = r15
            com.android.server.am.ActivityManagerService r3 = r1.mService
            android.content.pm.ApplicationInfo r4 = r2.info
            java.lang.String r4 = r4.packageName
            int r5 = r2.uid
            int r5 = android.os.UserHandle.getAppId(r5)
            r6 = 0
            r7 = 0
            r8 = 1
            r9 = 0
            r10 = 0
            int r11 = r2.userId
            java.lang.String r12 = "start failure"
            r3.forceStopPackageLocked(r4, r5, r6, r7, r8, r9, r10, r11, r12)
            return r16
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ProcessList.startProcessLocked(com.android.server.am.ProcessRecord, com.android.server.am.HostingRecord, boolean, boolean, java.lang.String):boolean");
    }

    /* access modifiers changed from: package-private */
    /* JADX WARNING: Removed duplicated region for block: B:14:0x00a2  */
    /* JADX WARNING: Removed duplicated region for block: B:16:0x00c6  */
    @com.android.internal.annotations.GuardedBy({"mService"})
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean startProcessLocked(com.android.server.am.HostingRecord r29, java.lang.String r30, com.android.server.am.ProcessRecord r31, int r32, int[] r33, int r34, int r35, java.lang.String r36, java.lang.String r37, java.lang.String r38, java.lang.String r39, long r40) {
        /*
            r28 = this;
            r13 = r28
            r14 = r31
            r15 = 1
            r14.pendingStart = r15
            r8 = 0
            r14.killedByAm = r8
            r14.removed = r8
            r14.killed = r8
            long r0 = r14.startSeq
            r2 = 0
            int r0 = (r0 > r2 ? 1 : (r0 == r2 ? 0 : -1))
            java.lang.String r1 = "startProcessLocked processName:"
            java.lang.String r9 = "ActivityManager"
            if (r0 == 0) goto L_0x0039
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r1)
            java.lang.String r2 = r14.processName
            r0.append(r2)
            java.lang.String r2 = " with non-zero startSeq:"
            r0.append(r2)
            long r2 = r14.startSeq
            r0.append(r2)
            java.lang.String r0 = r0.toString()
            android.util.Slog.wtf(r9, r0)
        L_0x0039:
            int r0 = r14.pid
            if (r0 == 0) goto L_0x005b
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r0.append(r1)
            java.lang.String r1 = r14.processName
            r0.append(r1)
            java.lang.String r1 = " with non-zero pid:"
            r0.append(r1)
            int r1 = r14.pid
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            android.util.Slog.wtf(r9, r0)
        L_0x005b:
            long r0 = r13.mProcStartSeqCounter
            r2 = 1
            long r0 = r0 + r2
            r13.mProcStartSeqCounter = r0
            r14.startSeq = r0
            r11 = r0
            r1 = r31
            r2 = r32
            r3 = r29
            r4 = r36
            r5 = r40
            r1.setStartParams(r2, r3, r4, r5)
            if (r39 != 0) goto L_0x0091
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            java.lang.String r1 = "wrap."
            r0.append(r1)
            java.lang.String r1 = r14.processName
            r0.append(r1)
            java.lang.String r0 = r0.toString()
            java.lang.String r0 = android.os.SystemProperties.get(r0)
            if (r0 == 0) goto L_0x008f
            goto L_0x0091
        L_0x008f:
            r0 = r8
            goto L_0x0092
        L_0x0091:
            r0 = r15
        L_0x0092:
            r14.setUsingWrapper(r0)
            android.util.LongSparseArray<com.android.server.am.ProcessRecord> r0 = r13.mPendingStarts
            r0.put(r11, r14)
            com.android.server.am.ActivityManagerService r0 = r13.mService
            com.android.server.am.ActivityManagerConstants r0 = r0.mConstants
            boolean r0 = r0.FLAG_PROCESS_START_ASYNC
            if (r0 == 0) goto L_0x00c6
            com.android.server.am.ActivityManagerService r0 = r13.mService
            android.os.Handler r0 = r0.mProcStartHandler
            com.android.server.am.-$$Lambda$ProcessList$vtq7LF5jIHO4t5NE03c8g7BT7Jc r10 = new com.android.server.am.-$$Lambda$ProcessList$vtq7LF5jIHO4t5NE03c8g7BT7Jc
            r1 = r10
            r2 = r28
            r3 = r31
            r4 = r30
            r5 = r33
            r6 = r34
            r7 = r35
            r8 = r37
            r9 = r38
            r15 = r10
            r10 = r39
            r16 = r11
            r1.<init>(r3, r4, r5, r6, r7, r8, r9, r10, r11)
            r0.post(r15)
            r10 = 1
            return r10
        L_0x00c6:
            r16 = r11
            r10 = r15
            android.os.Process$ProcessStartResult r0 = r28.startProcess(r29, r30, r31, r32, r33, r34, r35, r36, r37, r38, r39, r40)     // Catch:{ RuntimeException -> 0x00dd }
            int r3 = r0.pid     // Catch:{ RuntimeException -> 0x00dd }
            boolean r4 = r0.usingWrapper     // Catch:{ RuntimeException -> 0x00dd }
            r7 = 0
            r1 = r28
            r2 = r31
            r5 = r16
            r1.handleProcessStartedLocked(r2, r3, r4, r5, r7)     // Catch:{ RuntimeException -> 0x00dd }
            goto L_0x011a
        L_0x00dd:
            r0 = move-exception
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "Failure starting process "
            r1.append(r2)
            java.lang.String r2 = r14.processName
            r1.append(r2)
            java.lang.String r1 = r1.toString()
            android.util.Slog.e(r9, r1, r0)
            r14.pendingStart = r8
            com.android.server.am.ActivityManagerService r1 = r13.mService
            android.content.pm.ApplicationInfo r2 = r14.info
            java.lang.String r2 = r2.packageName
            int r3 = r14.uid
            int r20 = android.os.UserHandle.getAppId(r3)
            r21 = 0
            r22 = 0
            r23 = 1
            r24 = 0
            r25 = 0
            int r3 = r14.userId
            java.lang.String r27 = "start failure"
            r18 = r1
            r19 = r2
            r26 = r3
            r18.forceStopPackageLocked(r19, r20, r21, r22, r23, r24, r25, r26, r27)
        L_0x011a:
            int r0 = r14.pid
            if (r0 <= 0) goto L_0x011f
            r8 = r10
        L_0x011f:
            return r8
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ProcessList.startProcessLocked(com.android.server.am.HostingRecord, java.lang.String, com.android.server.am.ProcessRecord, int, int[], int, int, java.lang.String, java.lang.String, java.lang.String, java.lang.String, long):boolean");
    }

    /* Debug info: failed to restart local var, previous not found, register: 26 */
    /*  JADX ERROR: IndexOutOfBoundsException in pass: RegionMakerVisitor
        java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
        	at java.util.ArrayList.rangeCheck(ArrayList.java:657)
        	at java.util.ArrayList.get(ArrayList.java:433)
        	at jadx.core.dex.nodes.InsnNode.getArg(InsnNode.java:101)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:611)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverseMonitorExits(RegionMaker.java:619)
        	at jadx.core.dex.visitors.regions.RegionMaker.processMonitorEnter(RegionMaker.java:561)
        	at jadx.core.dex.visitors.regions.RegionMaker.traverse(RegionMaker.java:133)
        	at jadx.core.dex.visitors.regions.RegionMaker.makeRegion(RegionMaker.java:86)
        	at jadx.core.dex.visitors.regions.RegionMaker.processExcHandler(RegionMaker.java:1043)
        	at jadx.core.dex.visitors.regions.RegionMaker.processTryCatchBlocks(RegionMaker.java:975)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:52)
        */
    public /* synthetic */ void lambda$startProcessLocked$0$ProcessList(com.android.server.am.ProcessRecord r27, java.lang.String r28, int[] r29, int r30, int r31, java.lang.String r32, java.lang.String r33, java.lang.String r34, long r35) {
        /*
            r26 = this;
            r15 = r26
            r13 = r27
            r11 = r35
            com.android.server.am.HostingRecord r2 = r13.hostingRecord     // Catch:{ RuntimeException -> 0x004f }
            int r5 = r13.startUid     // Catch:{ RuntimeException -> 0x004f }
            java.lang.String r9 = r13.seInfo     // Catch:{ RuntimeException -> 0x004f }
            long r7 = r13.startTime     // Catch:{ RuntimeException -> 0x004f }
            r1 = r26
            r3 = r28
            r4 = r27
            r6 = r29
            r16 = r7
            r7 = r30
            r8 = r31
            r10 = r32
            r11 = r33
            r12 = r34
            r13 = r16
            android.os.Process$ProcessStartResult r0 = r1.startProcess(r2, r3, r4, r5, r6, r7, r8, r9, r10, r11, r12, r13)     // Catch:{ RuntimeException -> 0x0049 }
            r1 = r0
            com.android.server.am.ActivityManagerService r2 = r15.mService     // Catch:{ RuntimeException -> 0x0049 }
            monitor-enter(r2)     // Catch:{ RuntimeException -> 0x0049 }
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x003d }
            r3 = r27
            r4 = r35
            r15.handleProcessStartedLocked(r3, r1, r4)     // Catch:{ all -> 0x003b }
            monitor-exit(r2)     // Catch:{ all -> 0x003b }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()     // Catch:{ RuntimeException -> 0x0047 }
            goto L_0x00a1
        L_0x003b:
            r0 = move-exception
            goto L_0x0042
        L_0x003d:
            r0 = move-exception
            r3 = r27
            r4 = r35
        L_0x0042:
            monitor-exit(r2)     // Catch:{ all -> 0x003b }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()     // Catch:{ RuntimeException -> 0x0047 }
            throw r0     // Catch:{ RuntimeException -> 0x0047 }
        L_0x0047:
            r0 = move-exception
            goto L_0x0052
        L_0x0049:
            r0 = move-exception
            r3 = r27
            r4 = r35
            goto L_0x0052
        L_0x004f:
            r0 = move-exception
            r4 = r11
            r3 = r13
        L_0x0052:
            r1 = r0
            com.android.server.am.ActivityManagerService r2 = r15.mService
            monitor-enter(r2)
            com.android.server.am.ActivityManagerService.boostPriorityForLockedSection()     // Catch:{ all -> 0x00a2 }
            java.lang.String r0 = "ActivityManager"
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ all -> 0x00a2 }
            r6.<init>()     // Catch:{ all -> 0x00a2 }
            java.lang.String r7 = "Failure starting process "
            r6.append(r7)     // Catch:{ all -> 0x00a2 }
            java.lang.String r7 = r3.processName     // Catch:{ all -> 0x00a2 }
            r6.append(r7)     // Catch:{ all -> 0x00a2 }
            java.lang.String r6 = r6.toString()     // Catch:{ all -> 0x00a2 }
            android.util.Slog.e(r0, r6, r1)     // Catch:{ all -> 0x00a2 }
            android.util.LongSparseArray<com.android.server.am.ProcessRecord> r0 = r15.mPendingStarts     // Catch:{ all -> 0x00a2 }
            r0.remove(r4)     // Catch:{ all -> 0x00a2 }
            r0 = 0
            r3.pendingStart = r0     // Catch:{ all -> 0x00a2 }
            com.android.server.am.ActivityManagerService r0 = r15.mService     // Catch:{ all -> 0x00a2 }
            android.content.pm.ApplicationInfo r6 = r3.info     // Catch:{ all -> 0x00a2 }
            java.lang.String r6 = r6.packageName     // Catch:{ all -> 0x00a2 }
            int r7 = r3.uid     // Catch:{ all -> 0x00a2 }
            int r18 = android.os.UserHandle.getAppId(r7)     // Catch:{ all -> 0x00a2 }
            r19 = 0
            r20 = 0
            r21 = 1
            r22 = 0
            r23 = 0
            int r7 = r3.userId     // Catch:{ all -> 0x00a2 }
            java.lang.String r25 = "start failure"
            r16 = r0
            r17 = r6
            r24 = r7
            r16.forceStopPackageLocked(r17, r18, r19, r20, r21, r22, r23, r24, r25)     // Catch:{ all -> 0x00a2 }
            monitor-exit(r2)     // Catch:{ all -> 0x00a2 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
        L_0x00a1:
            return
        L_0x00a2:
            r0 = move-exception
            monitor-exit(r2)     // Catch:{ all -> 0x00a2 }
            com.android.server.am.ActivityManagerService.resetPriorityAfterLockedSection()
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ProcessList.lambda$startProcessLocked$0$ProcessList(com.android.server.am.ProcessRecord, java.lang.String, int[], int, int, java.lang.String, java.lang.String, java.lang.String, long):void");
    }

    @GuardedBy({"mService"})
    public void killAppZygoteIfNeededLocked(AppZygote appZygote) {
        ApplicationInfo appInfo = appZygote.getAppInfo();
        ArrayList<ProcessRecord> zygoteProcesses = this.mAppZygoteProcesses.get(appZygote);
        if (zygoteProcesses != null && zygoteProcesses.size() == 0) {
            this.mAppZygotes.remove(appInfo.processName, appInfo.uid);
            this.mAppZygoteProcesses.remove(appZygote);
            this.mAppIsolatedUidRangeAllocator.freeUidRangeLocked(appInfo);
            appZygote.stopZygote();
        }
    }

    @GuardedBy({"mService"})
    private void removeProcessFromAppZygoteLocked(ProcessRecord app) {
        IsolatedUidRange appUidRange = this.mAppIsolatedUidRangeAllocator.getIsolatedUidRangeLocked(app.info.processName, app.hostingRecord.getDefiningUid());
        if (appUidRange != null) {
            appUidRange.freeIsolatedUidLocked(app.uid);
        }
        AppZygote appZygote = (AppZygote) this.mAppZygotes.get(app.info.processName, app.hostingRecord.getDefiningUid());
        if (appZygote != null) {
            ArrayList<ProcessRecord> zygoteProcesses = this.mAppZygoteProcesses.get(appZygote);
            zygoteProcesses.remove(app);
            if (zygoteProcesses.size() == 0) {
                this.mService.mHandler.removeMessages(71);
                if (app.removed) {
                    killAppZygoteIfNeededLocked(appZygote);
                    return;
                }
                Message msg = this.mService.mHandler.obtainMessage(71);
                msg.obj = appZygote;
                this.mService.mHandler.sendMessageDelayed(msg, 5000);
            }
        }
    }

    private AppZygote createAppZygoteForProcessIfNeeded(ProcessRecord app) {
        AppZygote appZygote;
        ArrayList<ProcessRecord> zygoteProcessList;
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                int uid = app.hostingRecord.getDefiningUid();
                appZygote = (AppZygote) this.mAppZygotes.get(app.info.processName, uid);
                if (appZygote == null) {
                    IsolatedUidRange uidRange = this.mAppIsolatedUidRangeAllocator.getIsolatedUidRangeLocked(app.info.processName, app.hostingRecord.getDefiningUid());
                    int userId = UserHandle.getUserId(uid);
                    int firstUid = UserHandle.getUid(userId, uidRange.mFirstUid);
                    int lastUid = UserHandle.getUid(userId, uidRange.mLastUid);
                    ApplicationInfo appInfo = new ApplicationInfo(app.info);
                    appInfo.packageName = app.hostingRecord.getDefiningPackageName();
                    appInfo.uid = uid;
                    appZygote = new AppZygote(appInfo, uid, firstUid, lastUid);
                    this.mAppZygotes.put(app.info.processName, uid, appZygote);
                    zygoteProcessList = new ArrayList<>();
                    this.mAppZygoteProcesses.put(appZygote, zygoteProcessList);
                } else {
                    this.mService.mHandler.removeMessages(71, appZygote);
                    zygoteProcessList = this.mAppZygoteProcesses.get(appZygote);
                }
                zygoteProcessList.add(app);
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        return appZygote;
    }

    private Process.ProcessStartResult startProcess(HostingRecord hostingRecord, String entryPoint, ProcessRecord app, int uid, int[] gids, int runtimeFlags, int mountExternal, String seInfo, String requiredAbi, String instructionSet, String invokeWith, long startTime) {
        Process.ProcessStartResult startResult;
        ProcessRecord processRecord = app;
        long j = startTime;
        try {
            Trace.traceBegin(64, "Start proc: " + processRecord.processName);
            checkSlow(j, "startProcess: asking zygote to start proc");
            if (hostingRecord.usesWebviewZygote()) {
                String str = processRecord.processName;
                int i = processRecord.info.targetSdkVersion;
                String str2 = processRecord.info.dataDir;
                String str3 = processRecord.info.packageName;
                String str4 = str2;
                startResult = Process.startWebView(entryPoint, str, uid, uid, gids, runtimeFlags, mountExternal, i, seInfo, requiredAbi, instructionSet, str4, (String) null, str3, new String[]{"seq=" + processRecord.startSeq});
            } else if (hostingRecord.usesAppZygote()) {
                ChildZygoteProcess process = createAppZygoteForProcessIfNeeded(processRecord).getProcess();
                String str5 = processRecord.processName;
                int i2 = processRecord.info.targetSdkVersion;
                String str6 = processRecord.info.dataDir;
                String str7 = processRecord.info.packageName;
                String str8 = str6;
                int i3 = i2;
                startResult = process.start(entryPoint, str5, uid, uid, gids, runtimeFlags, mountExternal, i3, seInfo, requiredAbi, instructionSet, str8, (String) null, str7, false, new String[]{"seq=" + processRecord.startSeq});
            } else {
                String str9 = processRecord.processName;
                int i4 = processRecord.info.targetSdkVersion;
                String str10 = processRecord.info.dataDir;
                String str11 = processRecord.info.packageName;
                startResult = Process.start(entryPoint, str9, uid, uid, gids, runtimeFlags, mountExternal, i4, seInfo, requiredAbi, instructionSet, str10, invokeWith, str11, new String[]{"seq=" + processRecord.startSeq});
            }
            processRecord.enableBoost = ActivityManagerServiceInjector.isBoostNeeded(processRecord, hostingRecord.getType(), hostingRecord.getName());
            processRecord.boostBeginTime = processRecord.enableBoost ? SystemClock.uptimeMillis() : 0;
            if (!(mPerfServiceStartHint == null || hostingRecord.getType() == null || !hostingRecord.getType().equals("activity") || startResult == null)) {
                mPerfServiceStartHint.perfHint(4225, processRecord.processName, startResult.pid, 101);
            }
            checkSlow(j, "startProcess: returned from zygote!");
            return startResult;
        } finally {
            Trace.traceEnd(64);
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public final void startProcessLocked(ProcessRecord app, HostingRecord hostingRecord) {
        startProcessLocked(app, hostingRecord, (String) null);
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public final boolean startProcessLocked(ProcessRecord app, HostingRecord hostingRecord, String abiOverride) {
        return startProcessLocked(app, hostingRecord, false, false, abiOverride);
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public final ProcessRecord startProcessLocked(String processName, ApplicationInfo info, boolean knownToBeDead, int intentFlags, HostingRecord hostingRecord, boolean allowWhileBooting, boolean isolated, int isolatedUid, boolean keepIfLarge, String abiOverride, String entryPoint, String[] entryPointArgs, Runnable crashHandler, String callerPackage) {
        ProcessRecord app;
        String str = processName;
        ApplicationInfo applicationInfo = info;
        boolean z = isolated;
        long startTime = SystemClock.uptimeMillis();
        if (!z) {
            ProcessRecord app2 = getProcessRecordLocked(str, applicationInfo.uid, keepIfLarge);
            checkSlow(startTime, "startProcess: after getProcessRecord");
            if ((intentFlags & 4) == 0) {
                this.mService.mAppErrors.resetProcessCrashTimeLocked(applicationInfo);
                if (this.mService.mAppErrors.isBadProcessLocked(applicationInfo)) {
                    EventLog.writeEvent(EventLogTags.AM_PROC_GOOD, new Object[]{Integer.valueOf(UserHandle.getUserId(applicationInfo.uid)), Integer.valueOf(applicationInfo.uid), applicationInfo.processName});
                    this.mService.mAppErrors.clearBadProcessLocked(applicationInfo);
                    if (app2 != null) {
                        app2.bad = false;
                    }
                }
            } else if (this.mService.mAppErrors.isBadProcessLocked(applicationInfo)) {
                return null;
            }
            app = app2;
        } else {
            boolean z2 = keepIfLarge;
            app = null;
        }
        if (app != null && app.pid > 0) {
            if ((knownToBeDead || app.killed) && app.thread != null) {
                checkSlow(startTime, "startProcess: bad proc running, killing");
                killProcessGroup(app.uid, app.pid);
                this.mService.handleAppDiedLocked(app, true, true);
                checkSlow(startTime, "startProcess: done killing old proc");
            } else {
                app.addPackage(applicationInfo.packageName, applicationInfo.longVersionCode, this.mService.mProcessStats);
                checkSlow(startTime, "startProcess: done, added package to proc");
                return app;
            }
        }
        if (!ActivityManagerServiceInjector.processInitBefore(applicationInfo.processName)) {
            Slog.w(TAG, "Start process:" + applicationInfo.processName + " before normal start!");
            return null;
        }
        if (app == null) {
            checkSlow(startTime, "startProcess: creating new process record");
            ProcessRecord app3 = newProcessRecordLocked(info, processName, isolated, isolatedUid, hostingRecord);
            if (app3 == null) {
                Slog.w(TAG, "Failed making new process record for " + str + SliceClientPermissions.SliceAuthority.DELIMITER + applicationInfo.uid + " isolated=" + z);
                return null;
            }
            app3.crashHandler = crashHandler;
            app3.isolatedEntryPoint = entryPoint;
            app3.isolatedEntryPointArgs = entryPointArgs;
            checkSlow(startTime, "startProcess: done creating new process record");
            app = app3;
        } else {
            String str2 = entryPoint;
            String[] strArr = entryPointArgs;
            Runnable runnable = crashHandler;
            app.addPackage(applicationInfo.packageName, applicationInfo.longVersionCode, this.mService.mProcessStats);
            checkSlow(startTime, "startProcess: added package to existing proc");
        }
        app.callerPackage = callerPackage;
        if (this.mService.mProcessesReady || this.mService.isAllowedWhileBooting(applicationInfo) || allowWhileBooting) {
            checkSlow(startTime, "startProcess: stepping in to startProcess");
            boolean success = startProcessLocked(app, hostingRecord, abiOverride);
            checkSlow(startTime, "startProcess: done starting proc!");
            if (success) {
                return app;
            }
            return null;
        }
        if (!this.mService.mProcessesOnHold.contains(app)) {
            this.mService.mProcessesOnHold.add(app);
        }
        checkSlow(startTime, "startProcess: returning with proc on hold");
        return app;
    }

    @GuardedBy({"mService"})
    private String isProcStartValidLocked(ProcessRecord app, long expectedStartSeq) {
        StringBuilder sb = null;
        if (app.killedByAm) {
            if (0 == 0) {
                sb = new StringBuilder();
            }
            sb.append("killedByAm=true;");
        }
        if (this.mProcessNames.get(app.processName, app.uid) != app) {
            if (sb == null) {
                sb = new StringBuilder();
            }
            sb.append("No entry in mProcessNames;");
        }
        if (!app.pendingStart) {
            if (sb == null) {
                sb = new StringBuilder();
            }
            sb.append("pendingStart=false;");
        }
        if (app.startSeq > expectedStartSeq) {
            if (sb == null) {
                sb = new StringBuilder();
            }
            sb.append("seq=" + app.startSeq + ",expected=" + expectedStartSeq + ";");
        }
        if (sb == null) {
            return null;
        }
        return sb.toString();
    }

    @GuardedBy({"mService"})
    private boolean handleProcessStartedLocked(ProcessRecord pending, Process.ProcessStartResult startResult, long expectedStartSeq) {
        if (this.mPendingStarts.get(expectedStartSeq) != null) {
            return handleProcessStartedLocked(pending, startResult.pid, startResult.usingWrapper, expectedStartSeq, false);
        } else if (pending.pid != startResult.pid) {
            return false;
        } else {
            pending.setUsingWrapper(startResult.usingWrapper);
            return false;
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public boolean handleProcessStartedLocked(ProcessRecord app, int pid, boolean usingWrapper, long expectedStartSeq, boolean procAttached) {
        ProcessRecord oldApp;
        ProcessRecord processRecord = app;
        int i = pid;
        boolean z = usingWrapper;
        long j = expectedStartSeq;
        this.mPendingStarts.remove(j);
        String reason = isProcStartValidLocked(processRecord, j);
        if (reason != null) {
            Slog.w(TAG, processRecord + " start not valid, killing pid=" + i + ", " + reason);
            processRecord.pendingStart = false;
            ProcessRecordInjector.reportKillProcessEvent(processRecord, reason);
            Process.killProcessQuiet(pid);
            Process.killProcessGroup(processRecord.uid, processRecord.pid);
            return false;
        }
        this.mService.mBatteryStatsService.noteProcessStart(ActivityManagerServiceInjector.attachProcessStartReason(processRecord.processName, processRecord.hostingRecord.getType(), processRecord.hostingRecord.getName()), processRecord.info.uid);
        checkSlow(processRecord.startTime, "startProcess: done updating battery stats");
        Object[] objArr = new Object[6];
        objArr[0] = Integer.valueOf(UserHandle.getUserId(processRecord.startUid));
        objArr[1] = Integer.valueOf(pid);
        objArr[2] = Integer.valueOf(processRecord.startUid);
        objArr[3] = processRecord.processName;
        objArr[4] = processRecord.hostingRecord.getType();
        objArr[5] = processRecord.hostingRecord.getName() != null ? processRecord.hostingRecord.getName() : "";
        EventLog.writeEvent(EventLogTags.AM_PROC_START, objArr);
        try {
            AppGlobals.getPackageManager().logAppProcessStartIfNeeded(processRecord.processName, processRecord.uid, processRecord.seInfo, processRecord.info.sourceDir, pid);
        } catch (RemoteException e) {
        }
        MiuiNetworkManagementService.getInstance().setPidForPackage(processRecord.info.packageName, i, processRecord.uid);
        ActivityManagerServiceInjector.startProcessLocked(this.mService, processRecord, processRecord.hostingRecord.getType(), processRecord.hostingRecord.getName());
        ProcessPolicyManager.promoteImportantProcAdj(app);
        if (app.isPersistent()) {
            Watchdog.getInstance().processStarted(processRecord.processName, i);
        }
        checkSlow(processRecord.startTime, "startProcess: building log message");
        StringBuilder buf = this.mStringBuilder;
        buf.setLength(0);
        buf.append("Start proc ");
        buf.append(i);
        buf.append(':');
        buf.append(processRecord.processName);
        buf.append('/');
        UserHandle.formatUid(buf, processRecord.startUid);
        if (processRecord.isolatedEntryPoint != null) {
            buf.append(" [");
            buf.append(processRecord.isolatedEntryPoint);
            buf.append("]");
        }
        buf.append(" for ");
        buf.append(processRecord.hostingRecord.getType());
        if (processRecord.hostingRecord.getName() != null) {
            buf.append(" ");
            buf.append(processRecord.hostingRecord.getName());
        }
        buf.append(" caller=");
        buf.append(processRecord.callerPackage);
        if (processRecord.enableBoost) {
            buf.append(" enableBoost=true");
        }
        this.mService.reportUidInfoMessageLocked(TAG, buf.toString(), processRecord.startUid);
        app.setPid(pid);
        processRecord.setUsingWrapper(z);
        processRecord.pendingStart = false;
        checkSlow(processRecord.startTime, "startProcess: starting to update pids map");
        synchronized (this.mService.mPidsSelfLocked) {
            oldApp = this.mService.mPidsSelfLocked.get(i);
        }
        if (oldApp != null && !processRecord.isolated) {
            Slog.wtf(TAG, "handleProcessStartedLocked process:" + processRecord.processName + " startSeq:" + processRecord.startSeq + " pid:" + i + " belongs to another existing app:" + oldApp.processName + " startSeq:" + oldApp.startSeq);
            this.mService.cleanUpApplicationRecordLocked(oldApp, false, false, -1, true);
        }
        this.mService.mPidsSelfLocked.put(processRecord);
        synchronized (this.mService.mPidsSelfLocked) {
            if (!procAttached) {
                Message msg = this.mService.mHandler.obtainMessage(20);
                msg.obj = processRecord;
                this.mService.mHandler.sendMessageDelayed(msg, z ? 1200000 : JobStatus.DEFAULT_TRIGGER_UPDATE_DELAY);
            }
        }
        checkSlow(processRecord.startTime, "startProcess: done updating pids map");
        return true;
    }

    /* access modifiers changed from: package-private */
    public final void removeLruProcessLocked(ProcessRecord app) {
        int lrui = this.mLruProcesses.lastIndexOf(app);
        if (lrui >= 0) {
            if (!app.killed) {
                if (app.isPersistent()) {
                    Slog.w(TAG, "Removing persistent process that hasn't been killed: " + app);
                } else {
                    Slog.wtfStack(TAG, "Removing process that hasn't been killed: " + app);
                    if (app.pid > 0) {
                        ProcessRecordInjector.reportKillProcessEvent(app, "removeLruProcessLocked");
                        Process.killProcessQuiet(app.pid);
                        killProcessGroup(app.uid, app.pid);
                    } else {
                        app.pendingStart = false;
                    }
                }
            }
            int i = this.mLruProcessActivityStart;
            if (lrui <= i) {
                this.mLruProcessActivityStart = i - 1;
            }
            int i2 = this.mLruProcessServiceStart;
            if (lrui <= i2) {
                this.mLruProcessServiceStart = i2 - 1;
            }
            this.mLruProcesses.remove(lrui);
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public boolean killPackageProcessesLocked(String packageName, int appId, int userId, int minOomAdj, String reason) {
        return killPackageProcessesLocked(packageName, appId, userId, minOomAdj, false, true, true, false, false, reason);
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public final boolean killPackageProcessesLocked(String packageName, int appId, int userId, int minOomAdj, boolean callerWillRestart, boolean allowRestart, boolean doit, boolean evenPersistent, boolean setRemoved, String reason) {
        String str = packageName;
        int i = appId;
        int i2 = userId;
        boolean z = allowRestart;
        ArrayList<ProcessRecord> procs = new ArrayList<>();
        int NP = this.mProcessNames.getMap().size();
        for (int ip = 0; ip < NP; ip++) {
            SparseArray<ProcessRecord> apps = (SparseArray) this.mProcessNames.getMap().valueAt(ip);
            int NA = apps.size();
            for (int ia = 0; ia < NA; ia++) {
                ProcessRecord app = apps.valueAt(ia);
                if (app.isPersistent() && !evenPersistent) {
                    int i3 = minOomAdj;
                } else if (app.removed) {
                    if (doit) {
                        procs.add(app);
                        int i4 = minOomAdj;
                    } else {
                        int i5 = minOomAdj;
                    }
                } else if (app.setAdj >= minOomAdj) {
                    if (str != null) {
                        boolean isDep = app.pkgDeps != null && app.pkgDeps.contains(str);
                        if (isDep || UserHandle.getAppId(app.uid) == i) {
                            if (i2 == -1 || app.userId == i2) {
                                if (!app.pkgList.containsKey(str) && !isDep) {
                                }
                            }
                        }
                    } else if (i2 == -1 || app.userId == i2) {
                        if (i >= 0 && UserHandle.getAppId(app.uid) != i) {
                        }
                    }
                    if (!doit) {
                        return true;
                    }
                    if (setRemoved) {
                        app.removed = true;
                    }
                    procs.add(app);
                }
            }
            int i6 = minOomAdj;
        }
        int i7 = minOomAdj;
        AutoStartManagerService.signalStopProcessesLocked(procs, z, str, UserHandle.getUid(i2, i), this.mService);
        int N = procs.size();
        for (int i8 = 0; i8 < N; i8++) {
            removeProcessLocked(procs.get(i8), callerWillRestart, z, reason);
        }
        boolean z2 = callerWillRestart;
        String str2 = reason;
        ArrayList<AppZygote> zygotesToKill = new ArrayList<>();
        for (SparseArray<AppZygote> appZygotes : this.mAppZygotes.getMap().values()) {
            int i9 = 0;
            while (i9 < appZygotes.size()) {
                int appZygoteUid = appZygotes.keyAt(i9);
                if ((i2 == -1 || UserHandle.getUserId(appZygoteUid) == i2) && (i < 0 || UserHandle.getAppId(appZygoteUid) == i)) {
                    AppZygote appZygote = appZygotes.valueAt(i9);
                    if (str == null || str.equals(appZygote.getAppInfo().packageName)) {
                        zygotesToKill.add(appZygote);
                    }
                }
                i9++;
                i = appId;
                boolean z3 = allowRestart;
            }
            i = appId;
            boolean z4 = allowRestart;
        }
        Iterator<AppZygote> it = zygotesToKill.iterator();
        while (it.hasNext()) {
            killAppZygoteIfNeededLocked(it.next());
        }
        this.mService.updateOomAdjLocked("updateOomAdj_processEnd");
        return N > 0;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public boolean removeProcessLocked(ProcessRecord app, boolean callerWillRestart, boolean allowRestart, String reason) {
        String name = app.processName;
        int uid = app.uid;
        if (((ProcessRecord) this.mProcessNames.get(name, uid)) != app) {
            Slog.w(TAG, "Ignoring remove of inactive process: " + app);
            return false;
        }
        removeProcessNameLocked(name, uid);
        this.mService.mAtmInternal.clearHeavyWeightProcessIfEquals(app.getWindowProcessController());
        boolean needRestart = false;
        if ((app.pid <= 0 || app.pid == ActivityManagerService.MY_PID) && (app.pid != 0 || !app.pendingStart)) {
            this.mRemovedProcesses.add(app);
        } else {
            if (app.pid > 0) {
                this.mService.mPidsSelfLocked.remove(app);
                this.mService.mHandler.removeMessages(20, app);
                this.mService.mBatteryStatsService.noteProcessFinish(app.processName, app.info.uid);
                if (app.isolated) {
                    this.mService.mBatteryStatsService.removeIsolatedUid(app.uid, app.info.uid);
                    this.mService.getPackageManagerInternalLocked().removeIsolatedUid(app.uid);
                }
            }
            boolean willRestart = false;
            if (app.isPersistent() && !app.isolated) {
                if (!callerWillRestart) {
                    willRestart = true;
                } else {
                    needRestart = true;
                }
            }
            app.kill(reason, true);
            this.mService.handleAppDiedLocked(app, willRestart, allowRestart);
            if (willRestart) {
                removeLruProcessLocked(app);
                this.mService.addAppLocked(app.info, (String) null, false, (String) null);
            }
        }
        return needRestart;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public final void addProcessNameLocked(ProcessRecord proc) {
        ProcessRecord old = removeProcessNameLocked(proc.processName, proc.uid);
        if (old == proc && proc.isPersistent()) {
            Slog.w(TAG, "Re-adding persistent process " + proc);
        } else if (old != null) {
            Slog.wtf(TAG, "Already have existing proc " + old + " when adding " + proc);
        }
        UidRecord uidRec = this.mActiveUids.get(proc.uid);
        if (uidRec == null) {
            uidRec = new UidRecord(proc.uid);
            if (Arrays.binarySearch(this.mService.mDeviceIdleTempWhitelist, UserHandle.getAppId(proc.uid)) >= 0 || this.mService.mPendingTempWhitelist.indexOfKey(proc.uid) >= 0) {
                uidRec.curWhitelist = true;
                uidRec.setWhitelist = true;
            }
            uidRec.updateHasInternetPermission();
            this.mActiveUids.put(proc.uid, uidRec);
            EventLogTags.writeAmUidRunning(uidRec.uid);
            this.mService.noteUidProcessState(uidRec.uid, uidRec.getCurProcState());
        }
        proc.uidRecord = uidRec;
        proc.renderThreadTid = 0;
        uidRec.numProcs++;
        this.mProcessNames.put(proc.processName, proc.uid, proc);
        if (proc.isolated) {
            this.mIsolatedProcesses.put(proc.uid, proc);
        }
    }

    @GuardedBy({"mService"})
    private IsolatedUidRange getOrCreateIsolatedUidRangeLocked(ApplicationInfo info, HostingRecord hostingRecord) {
        if (hostingRecord == null || !hostingRecord.usesAppZygote()) {
            return this.mGlobalIsolatedUids;
        }
        return this.mAppIsolatedUidRangeAllocator.getOrCreateIsolatedUidRangeLocked(info.processName, hostingRecord.getDefiningUid());
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public final ProcessRecord newProcessRecordLocked(ApplicationInfo info, String customProcess, boolean isolated, int isolatedUid, HostingRecord hostingRecord) {
        String proc = customProcess != null ? customProcess : info.processName;
        int userId = UserHandle.getUserId(info.uid);
        int uid = info.uid;
        if (isolated) {
            if (isolatedUid == 0) {
                IsolatedUidRange uidRange = getOrCreateIsolatedUidRangeLocked(info, hostingRecord);
                if (uidRange == null || (uid = uidRange.allocateIsolatedUidLocked(userId)) == -1) {
                    return null;
                }
            } else {
                uid = isolatedUid;
            }
            this.mService.getPackageManagerInternalLocked().addIsolatedUid(uid, info.uid);
            this.mService.mBatteryStatsService.addIsolatedUid(uid, info.uid);
            StatsLog.write(43, info.uid, uid, 1);
        }
        ProcessRecord r = new ProcessRecord(this.mService, info, proc, uid);
        if (((!this.mService.mBooted && !this.mService.mBooting) || ProcessPolicyManager.isDelayBootPersistentApp(r.processName)) && userId == 0 && (info.flags & 9) == 9 && r.processName.equals(info.processName)) {
            r.setCurrentSchedulingGroup(2);
            r.setSchedGroup = 2;
            r.setPersistent(true);
            r.maxAdj = PERSISTENT_PROC_ADJ;
        }
        if (isolated && isolatedUid != 0) {
            r.maxAdj = PERSISTENT_SERVICE_ADJ;
        }
        addProcessNameLocked(r);
        return r;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public final ProcessRecord removeProcessNameLocked(String name, int uid) {
        return removeProcessNameLocked(name, uid, (ProcessRecord) null);
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public final ProcessRecord removeProcessNameLocked(String name, int uid, ProcessRecord expecting) {
        ProcessRecord old = (ProcessRecord) this.mProcessNames.get(name, uid);
        if (expecting == null || old == expecting) {
            this.mProcessNames.remove(name, uid);
        }
        if (!(old == null || old.uidRecord == null)) {
            old.uidRecord.numProcs--;
            if (old.uidRecord.numProcs == 0) {
                this.mService.enqueueUidChangeLocked(old.uidRecord, -1, 1);
                EventLogTags.writeAmUidStopped(uid);
                this.mActiveUids.remove(uid);
                this.mService.noteUidProcessState(uid, 21);
            }
            old.uidRecord = null;
        }
        this.mIsolatedProcesses.remove(uid);
        this.mGlobalIsolatedUids.freeIsolatedUidLocked(uid);
        ProcessRecord record = expecting != null ? expecting : old;
        if (record != null && record.appZygote) {
            removeProcessFromAppZygoteLocked(record);
        }
        return old;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public void updateCoreSettingsLocked(Bundle settings) {
        for (int i = this.mLruProcesses.size() - 1; i >= 0; i--) {
            ProcessRecord processRecord = this.mLruProcesses.get(i);
            try {
                if (processRecord.thread != null) {
                    processRecord.thread.setCoreSettings(settings);
                }
            } catch (RemoteException e) {
            }
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public void killAllBackgroundProcessesExceptLocked(int minTargetSdk, int maxProcState) {
        ArrayList<ProcessRecord> procs = new ArrayList<>();
        int NP = this.mProcessNames.getMap().size();
        for (int ip = 0; ip < NP; ip++) {
            SparseArray<ProcessRecord> apps = (SparseArray) this.mProcessNames.getMap().valueAt(ip);
            int NA = apps.size();
            for (int ia = 0; ia < NA; ia++) {
                ProcessRecord app = apps.valueAt(ia);
                if (app.removed || ((minTargetSdk < 0 || app.info.targetSdkVersion < minTargetSdk) && (maxProcState < 0 || app.setProcState > maxProcState))) {
                    procs.add(app);
                }
            }
        }
        int ip2 = procs.size();
        for (int i = 0; i < ip2; i++) {
            removeProcessLocked(procs.get(i), false, true, "kill all background except");
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public void updateAllTimePrefsLocked(int timePref) {
        for (int i = this.mLruProcesses.size() - 1; i >= 0; i--) {
            ProcessRecord r = this.mLruProcesses.get(i);
            if (r.thread != null) {
                try {
                    r.thread.updateTimePrefs(timePref);
                } catch (RemoteException e) {
                    Slog.w(TAG, "Failed to update preferences for: " + r.info.processName);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public void setAllHttpProxy() {
        ProcessRecord r;
        synchronized (this.mService) {
            try {
                ActivityManagerService.boostPriorityForLockedSection();
                for (int i = this.mLruProcesses.size() - 1; i >= 0; i--) {
                    r = this.mLruProcesses.get(i);
                    if (!(r.pid == ActivityManagerService.MY_PID || r.thread == null || r.isolated)) {
                        r.thread.updateHttpProxy();
                    }
                }
            } catch (RemoteException e) {
                Slog.w(TAG, "Failed to update http proxy for: " + r.info.processName);
            } catch (Throwable th) {
                while (true) {
                    ActivityManagerService.resetPriorityAfterLockedSection();
                    throw th;
                }
            }
        }
        ActivityManagerService.resetPriorityAfterLockedSection();
        ActivityThread.updateHttpProxy(this.mService.mContext);
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public void clearAllDnsCacheLocked() {
        for (int i = this.mLruProcesses.size() - 1; i >= 0; i--) {
            ProcessRecord r = this.mLruProcesses.get(i);
            if (r.thread != null) {
                try {
                    r.thread.clearDnsCache();
                } catch (RemoteException e) {
                    Slog.w(TAG, "Failed to clear dns cache for: " + r.info.processName);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public void handleAllTrustStorageUpdateLocked() {
        for (int i = this.mLruProcesses.size() - 1; i >= 0; i--) {
            ProcessRecord r = this.mLruProcesses.get(i);
            if (r.thread != null) {
                try {
                    r.thread.handleTrustStorageUpdate();
                } catch (RemoteException e) {
                    Slog.w(TAG, "Failed to handle trust storage update for: " + r.info.processName);
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public int updateLruProcessInternalLocked(ProcessRecord app, long now, int index, int lruSeq, String what, Object obj, ProcessRecord srcApp) {
        app.lastActivityTime = now;
        if (app.hasActivitiesOrRecentTasks()) {
            return index;
        }
        int lrui = this.mLruProcesses.lastIndexOf(app);
        if (lrui < 0) {
            Slog.wtf(TAG, "Adding dependent process " + app + " not on LRU list: " + what + " " + obj + " from " + srcApp);
            return index;
        } else if (lrui >= index) {
            return index;
        } else {
            int i = this.mLruProcessActivityStart;
            if (lrui >= i && index < i) {
                return index;
            }
            this.mLruProcesses.remove(lrui);
            if (index > 0) {
                index--;
            }
            this.mLruProcesses.add(index, app);
            app.lruSeq = lruSeq;
            return index;
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:58:0x00ee  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void updateClientActivitiesOrdering(com.android.server.am.ProcessRecord r10, int r11, int r12, int r13) {
        /*
            r9 = this;
            boolean r0 = r10.hasActivitiesOrRecentTasks()
            if (r0 != 0) goto L_0x0117
            boolean r0 = r10.treatLikeActivity
            if (r0 != 0) goto L_0x0117
            boolean r0 = r10.hasClientActivities()
            if (r0 != 0) goto L_0x0012
            goto L_0x0117
        L_0x0012:
            android.content.pm.ApplicationInfo r0 = r10.info
            int r0 = r0.uid
            int r1 = r10.connectionGroup
            if (r1 <= 0) goto L_0x0076
            int r1 = r10.connectionImportance
            r2 = r13
        L_0x001d:
            if (r2 < r12) goto L_0x0076
            java.util.ArrayList<com.android.server.am.ProcessRecord> r3 = r9.mLruProcesses
            java.lang.Object r3 = r3.get(r2)
            com.android.server.am.ProcessRecord r3 = (com.android.server.am.ProcessRecord) r3
            android.content.pm.ApplicationInfo r4 = r3.info
            int r4 = r4.uid
            if (r4 != r0) goto L_0x0073
            int r4 = r3.connectionGroup
            int r5 = r10.connectionGroup
            if (r4 != r5) goto L_0x0073
            if (r2 != r13) goto L_0x003e
            int r4 = r3.connectionImportance
            if (r4 < r1) goto L_0x003e
            int r13 = r13 + -1
            int r1 = r3.connectionImportance
            goto L_0x0073
        L_0x003e:
            r4 = 0
            r5 = r11
        L_0x0040:
            if (r5 <= r13) goto L_0x0061
            java.util.ArrayList<com.android.server.am.ProcessRecord> r6 = r9.mLruProcesses
            java.lang.Object r6 = r6.get(r5)
            com.android.server.am.ProcessRecord r6 = (com.android.server.am.ProcessRecord) r6
            int r7 = r3.connectionImportance
            int r8 = r6.connectionImportance
            if (r7 > r8) goto L_0x005e
            java.util.ArrayList<com.android.server.am.ProcessRecord> r7 = r9.mLruProcesses
            r7.remove(r2)
            java.util.ArrayList<com.android.server.am.ProcessRecord> r7 = r9.mLruProcesses
            r7.add(r5, r3)
            r4 = 1
            int r13 = r13 + -1
            goto L_0x0061
        L_0x005e:
            int r5 = r5 + -1
            goto L_0x0040
        L_0x0061:
            if (r4 != 0) goto L_0x0073
            java.util.ArrayList<com.android.server.am.ProcessRecord> r5 = r9.mLruProcesses
            r5.remove(r2)
            java.util.ArrayList<com.android.server.am.ProcessRecord> r5 = r9.mLruProcesses
            int r6 = r13 + -1
            r5.add(r6, r3)
            int r13 = r13 + -1
            int r1 = r3.connectionImportance
        L_0x0073:
            int r2 = r2 + -1
            goto L_0x001d
        L_0x0076:
            r1 = r13
        L_0x0077:
            if (r1 < r12) goto L_0x0116
            java.util.ArrayList<com.android.server.am.ProcessRecord> r2 = r9.mLruProcesses
            java.lang.Object r2 = r2.get(r1)
            com.android.server.am.ProcessRecord r2 = (com.android.server.am.ProcessRecord) r2
            android.content.pm.ApplicationInfo r3 = r2.info
            int r3 = r3.uid
            if (r3 == r0) goto L_0x0112
            if (r1 >= r13) goto L_0x00d8
            r3 = 0
            r4 = 0
            r5 = 0
        L_0x008c:
            if (r1 < r12) goto L_0x00d8
            java.util.ArrayList<com.android.server.am.ProcessRecord> r6 = r9.mLruProcesses
            r6.remove(r1)
            java.util.ArrayList<com.android.server.am.ProcessRecord> r6 = r9.mLruProcesses
            r6.add(r13, r2)
            int r1 = r1 + -1
            if (r1 >= r12) goto L_0x009d
            goto L_0x00d8
        L_0x009d:
            java.util.ArrayList<com.android.server.am.ProcessRecord> r6 = r9.mLruProcesses
            java.lang.Object r6 = r6.get(r1)
            r2 = r6
            com.android.server.am.ProcessRecord r2 = (com.android.server.am.ProcessRecord) r2
            boolean r6 = r2.hasActivitiesOrRecentTasks()
            if (r6 != 0) goto L_0x00d1
            boolean r6 = r2.treatLikeActivity
            if (r6 == 0) goto L_0x00b1
            goto L_0x00d1
        L_0x00b1:
            boolean r6 = r2.hasClientActivities()
            if (r6 == 0) goto L_0x00d5
            if (r3 == 0) goto L_0x00c9
            if (r4 == 0) goto L_0x00d8
            android.content.pm.ApplicationInfo r6 = r2.info
            int r6 = r6.uid
            if (r4 == r6) goto L_0x00c2
            goto L_0x00d8
        L_0x00c2:
            if (r5 == 0) goto L_0x00d8
            int r6 = r2.connectionGroup
            if (r5 == r6) goto L_0x00d5
            goto L_0x00d8
        L_0x00c9:
            r3 = 1
            android.content.pm.ApplicationInfo r6 = r2.info
            int r4 = r6.uid
            int r5 = r2.connectionGroup
            goto L_0x00d5
        L_0x00d1:
            if (r3 == 0) goto L_0x00d4
            goto L_0x00d8
        L_0x00d4:
            r3 = 1
        L_0x00d5:
            int r13 = r13 + -1
            goto L_0x008c
        L_0x00d8:
            int r13 = r13 + -1
            if (r13 < r12) goto L_0x00ec
            java.util.ArrayList<com.android.server.am.ProcessRecord> r3 = r9.mLruProcesses
            java.lang.Object r3 = r3.get(r13)
            com.android.server.am.ProcessRecord r3 = (com.android.server.am.ProcessRecord) r3
            android.content.pm.ApplicationInfo r4 = r3.info
            int r4 = r4.uid
            if (r4 != r0) goto L_0x00eb
            goto L_0x00ec
        L_0x00eb:
            goto L_0x00d8
        L_0x00ec:
            if (r13 < r12) goto L_0x0110
            java.util.ArrayList<com.android.server.am.ProcessRecord> r3 = r9.mLruProcesses
            java.lang.Object r3 = r3.get(r13)
            com.android.server.am.ProcessRecord r3 = (com.android.server.am.ProcessRecord) r3
        L_0x00f6:
            int r13 = r13 + -1
            if (r13 < r12) goto L_0x0110
            java.util.ArrayList<com.android.server.am.ProcessRecord> r4 = r9.mLruProcesses
            java.lang.Object r4 = r4.get(r13)
            com.android.server.am.ProcessRecord r4 = (com.android.server.am.ProcessRecord) r4
            android.content.pm.ApplicationInfo r5 = r4.info
            int r5 = r5.uid
            if (r5 != r0) goto L_0x0110
            int r5 = r4.connectionGroup
            int r6 = r3.connectionGroup
            if (r5 == r6) goto L_0x010f
            goto L_0x0110
        L_0x010f:
            goto L_0x00f6
        L_0x0110:
            r1 = r13
            goto L_0x0114
        L_0x0112:
            int r1 = r1 + -1
        L_0x0114:
            goto L_0x0077
        L_0x0116:
            return
        L_0x0117:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.am.ProcessList.updateClientActivitiesOrdering(com.android.server.am.ProcessRecord, int, int, int):void");
    }

    /* access modifiers changed from: package-private */
    public final void updateLruProcessLocked(ProcessRecord app, boolean activityChange, ProcessRecord client) {
        int nextIndex;
        long now;
        int j;
        ProcessRecord processRecord = app;
        ProcessRecord processRecord2 = client;
        boolean hasActivity = app.hasActivitiesOrRecentTasks() || app.hasClientActivities() || processRecord.treatLikeActivity;
        if (!activityChange && hasActivity) {
            return;
        }
        if (processRecord.pid != 0 || !processRecord.killedByAm) {
            this.mLruSeq++;
            long now2 = SystemClock.uptimeMillis();
            processRecord.lastActivityTime = now2;
            if (hasActivity) {
                int N = this.mLruProcesses.size();
                if (N > 0 && this.mLruProcesses.get(N - 1) == processRecord) {
                    return;
                }
            } else {
                int i = this.mLruProcessServiceStart;
                if (i > 0 && this.mLruProcesses.get(i - 1) == processRecord) {
                    return;
                }
            }
            int lrui = this.mLruProcesses.lastIndexOf(processRecord);
            if (!app.isPersistent() || lrui < 0) {
                if (lrui >= 0) {
                    int i2 = this.mLruProcessActivityStart;
                    if (lrui < i2) {
                        this.mLruProcessActivityStart = i2 - 1;
                    }
                    int i3 = this.mLruProcessServiceStart;
                    if (lrui < i3) {
                        this.mLruProcessServiceStart = i3 - 1;
                    }
                    this.mLruProcesses.remove(lrui);
                }
                int nextActivityIndex = -1;
                if (hasActivity) {
                    int N2 = this.mLruProcesses.size();
                    nextIndex = this.mLruProcessServiceStart;
                    if (app.hasActivitiesOrRecentTasks() || processRecord.treatLikeActivity || this.mLruProcessActivityStart >= N2 - 1) {
                        this.mLruProcesses.add(processRecord);
                        nextActivityIndex = this.mLruProcesses.size() - 1;
                    } else {
                        int pos = N2 - 1;
                        while (pos > this.mLruProcessActivityStart && this.mLruProcesses.get(pos).info.uid != processRecord.info.uid) {
                            pos--;
                        }
                        this.mLruProcesses.add(pos, processRecord);
                        int i4 = this.mLruProcessActivityStart;
                        if (pos == i4) {
                            this.mLruProcessActivityStart = i4 + 1;
                        }
                        int i5 = this.mLruProcessServiceStart;
                        if (pos == i5) {
                            this.mLruProcessServiceStart = i5 + 1;
                        }
                        int endIndex = pos - 1;
                        if (endIndex < this.mLruProcessActivityStart) {
                            endIndex = this.mLruProcessActivityStart;
                        }
                        nextActivityIndex = endIndex;
                        updateClientActivitiesOrdering(processRecord, pos, this.mLruProcessActivityStart, endIndex);
                    }
                } else {
                    int index = this.mLruProcessServiceStart;
                    if (processRecord2 != null) {
                        int clientIndex = this.mLruProcesses.lastIndexOf(processRecord2);
                        if (clientIndex <= lrui) {
                            clientIndex = lrui;
                        }
                        if (clientIndex >= 0 && index > clientIndex) {
                            index = clientIndex;
                        }
                    }
                    this.mLruProcesses.add(index, processRecord);
                    int nextIndex2 = index - 1;
                    this.mLruProcessActivityStart++;
                    this.mLruProcessServiceStart++;
                    if (index > 1) {
                        updateClientActivitiesOrdering(processRecord, this.mLruProcessServiceStart - 1, 0, index - 1);
                    }
                    nextIndex = nextIndex2;
                }
                processRecord.lruSeq = this.mLruSeq;
                int nextActivityIndex2 = nextActivityIndex;
                int j2 = processRecord.connections.size() - 1;
                int nextIndex3 = nextIndex;
                while (j2 >= 0) {
                    ConnectionRecord cr = processRecord.connections.valueAt(j2);
                    if (cr.binding == null || cr.serviceDead || cr.binding.service == null || cr.binding.service.app == null || cr.binding.service.app.lruSeq == this.mLruSeq || (cr.flags & 1073742128) != 0) {
                        j = j2;
                        now = now2;
                    } else if (cr.binding.service.app.isPersistent()) {
                        j = j2;
                        now = now2;
                    } else if (!cr.binding.service.app.hasClientActivities()) {
                        j = j2;
                        now = now2;
                        ConnectionRecord cr2 = cr;
                        ConnectionRecord connectionRecord = cr2;
                        nextIndex3 = updateLruProcessInternalLocked(cr2.binding.service.app, now, nextIndex3, this.mLruSeq, "service connection", cr2, app);
                    } else if (nextActivityIndex2 >= 0) {
                        j = j2;
                        now = now2;
                        nextActivityIndex2 = updateLruProcessInternalLocked(cr.binding.service.app, now2, nextActivityIndex2, this.mLruSeq, "service connection", cr, app);
                    } else {
                        j = j2;
                        now = now2;
                    }
                    j2 = j - 1;
                    now2 = now;
                }
                int i6 = j2;
                long now3 = now2;
                for (int j3 = processRecord.conProviders.size() - 1; j3 >= 0; j3--) {
                    ContentProviderRecord cpr = processRecord.conProviders.get(j3).provider;
                    if (cpr.proc == null || cpr.proc.lruSeq == this.mLruSeq || cpr.proc.isPersistent()) {
                    } else {
                        ContentProviderRecord contentProviderRecord = cpr;
                        nextIndex3 = updateLruProcessInternalLocked(cpr.proc, now3, nextIndex3, this.mLruSeq, "provider reference", cpr, app);
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    public final ProcessRecord getLRURecordForAppLocked(IApplicationThread thread) {
        IBinder threadBinder = thread.asBinder();
        for (int i = this.mLruProcesses.size() - 1; i >= 0; i--) {
            ProcessRecord rec = this.mLruProcesses.get(i);
            if (rec.thread != null && rec.thread.asBinder() == threadBinder) {
                return rec;
            }
        }
        return null;
    }

    /* access modifiers changed from: package-private */
    public boolean haveBackgroundProcessLocked() {
        for (int i = this.mLruProcesses.size() - 1; i >= 0; i--) {
            ProcessRecord rec = this.mLruProcesses.get(i);
            if (rec.thread != null && rec.setProcState >= 17) {
                return true;
            }
        }
        return false;
    }

    private static int procStateToImportance(int procState, int memAdj, ActivityManager.RunningAppProcessInfo currApp, int clientTargetSdk) {
        int imp = ActivityManager.RunningAppProcessInfo.procStateToImportanceForTargetSdk(procState, clientTargetSdk);
        if (imp == HEAVY_WEIGHT_APP_ADJ) {
            currApp.lru = memAdj;
        } else {
            currApp.lru = 0;
        }
        return imp;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public void fillInProcMemInfoLocked(ProcessRecord app, ActivityManager.RunningAppProcessInfo outInfo, int clientTargetSdk) {
        outInfo.pid = app.pid;
        outInfo.uid = app.info.uid;
        boolean z = true;
        if (this.mService.mAtmInternal.isHeavyWeightProcess(app.getWindowProcessController())) {
            outInfo.flags |= 1;
        }
        if (app.isPersistent()) {
            outInfo.flags |= 2;
        }
        if (app.hasActivities()) {
            outInfo.flags |= 4;
        }
        outInfo.lastTrimLevel = app.trimMemoryLevel;
        outInfo.importance = procStateToImportance(app.getCurProcState(), app.curAdj, outInfo, clientTargetSdk);
        outInfo.importanceReasonCode = app.adjTypeCode;
        outInfo.processState = app.getCurProcState();
        if (app != this.mService.getTopAppLocked()) {
            z = false;
        }
        outInfo.isFocused = z;
        outInfo.lastActivityTime = app.lastActivityTime;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public List<ActivityManager.RunningAppProcessInfo> getRunningAppProcessesLocked(boolean allUsers, int userId, boolean allUids, int callingUid, int clientTargetSdk) {
        int pid;
        List<ActivityManager.RunningAppProcessInfo> runList = null;
        for (int i = this.mLruProcesses.size() - 1; i >= 0; i--) {
            ProcessRecord app = this.mLruProcesses.get(i);
            if ((allUsers || app.userId == userId) && ((allUids || app.uid == callingUid) && app.thread != null && !app.isCrashing() && !app.isNotResponding())) {
                ActivityManager.RunningAppProcessInfo currApp = new ActivityManager.RunningAppProcessInfo(app.processName, app.pid, app.getPackageList());
                fillInProcMemInfoLocked(app, currApp, clientTargetSdk);
                if (app.adjSource instanceof ProcessRecord) {
                    currApp.importanceReasonPid = ((ProcessRecord) app.adjSource).pid;
                    currApp.importanceReasonImportance = ActivityManager.RunningAppProcessInfo.procStateToImportance(app.adjSourceProcState);
                } else if ((app.adjSource instanceof ActivityServiceConnectionsHolder) && (pid = ((ActivityServiceConnectionsHolder) app.adjSource).getActivityPid()) != -1) {
                    currApp.importanceReasonPid = pid;
                }
                if (app.adjTarget instanceof ComponentName) {
                    currApp.importanceReasonComponent = (ComponentName) app.adjTarget;
                }
                if (runList == null) {
                    runList = new ArrayList<>();
                }
                runList.add(currApp);
            }
        }
        return runList;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public int getLruSizeLocked() {
        return this.mLruProcesses.size();
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public void dumpLruListHeaderLocked(PrintWriter pw) {
        pw.print("  Process LRU list (sorted by oom_adj, ");
        pw.print(this.mLruProcesses.size());
        pw.print(" total, non-act at ");
        pw.print(this.mLruProcesses.size() - this.mLruProcessActivityStart);
        pw.print(", non-svc at ");
        pw.print(this.mLruProcesses.size() - this.mLruProcessServiceStart);
        pw.println("):");
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public ArrayList<ProcessRecord> collectProcessesLocked(int start, boolean allPkgs, String[] args) {
        if (args == null || args.length <= start || args[start].charAt(0) == '-') {
            return new ArrayList<>(this.mLruProcesses);
        }
        ArrayList<ProcessRecord> procs = new ArrayList<>();
        int pid = -1;
        try {
            pid = Integer.parseInt(args[start]);
        } catch (NumberFormatException e) {
        }
        for (int i = this.mLruProcesses.size() - 1; i >= 0; i--) {
            ProcessRecord proc = this.mLruProcesses.get(i);
            if (proc.pid > 0 && proc.pid == pid) {
                procs.add(proc);
            } else if (allPkgs && proc.pkgList != null && proc.pkgList.containsKey(args[start])) {
                procs.add(proc);
            } else if (proc.processName.equals(args[start])) {
                procs.add(proc);
            }
        }
        if (procs.size() <= 0) {
            return null;
        }
        return procs;
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public void updateApplicationInfoLocked(List<String> packagesToUpdate, int userId, boolean updateFrameworkRes) {
        for (int i = this.mLruProcesses.size() - 1; i >= 0; i--) {
            ProcessRecord app = this.mLruProcesses.get(i);
            if (app.thread != null && (userId == -1 || app.userId == userId)) {
                int packageCount = app.pkgList.size();
                for (int j = 0; j < packageCount; j++) {
                    String packageName = app.pkgList.keyAt(j);
                    if (updateFrameworkRes || packagesToUpdate.contains(packageName)) {
                        try {
                            ApplicationInfo ai = AppGlobals.getPackageManager().getApplicationInfo(packageName, 1024, app.userId);
                            if (ai != null) {
                                app.thread.scheduleApplicationInfoChanged(ai);
                            }
                        } catch (RemoteException e) {
                            Slog.w(TAG, String.format("Failed to update %s ApplicationInfo for %s", new Object[]{packageName, app}));
                        }
                    }
                }
            }
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public void sendPackageBroadcastLocked(int cmd, String[] packages, int userId) {
        boolean foundProcess = false;
        for (int i = this.mLruProcesses.size() - 1; i >= 0; i--) {
            ProcessRecord r = this.mLruProcesses.get(i);
            if (r.thread != null && (userId == -1 || r.userId == userId)) {
                try {
                    for (int index = packages.length - 1; index >= 0 && !foundProcess; index--) {
                        if (packages[index].equals(r.info.packageName)) {
                            foundProcess = true;
                        }
                    }
                    r.thread.dispatchPackageBroadcast(cmd, packages);
                } catch (RemoteException e) {
                }
            }
        }
        if (!foundProcess) {
            try {
                AppGlobals.getPackageManager().notifyPackagesReplacedReceived(packages);
            } catch (RemoteException e2) {
            }
        }
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public int getUidProcStateLocked(int uid) {
        UidRecord uidRec = this.mActiveUids.get(uid);
        if (uidRec == null) {
            return 21;
        }
        return uidRec.getCurProcState();
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public UidRecord getUidRecordLocked(int uid) {
        return this.mActiveUids.get(uid);
    }

    /* access modifiers changed from: package-private */
    @GuardedBy({"mService"})
    public void doStopUidForIdleUidsLocked() {
        int size = this.mActiveUids.size();
        for (int i = 0; i < size; i++) {
            if (!UserHandle.isCore(this.mActiveUids.keyAt(i))) {
                UidRecord uidRec = this.mActiveUids.valueAt(i);
                if (uidRec.idle) {
                    this.mService.doStopUidLocked(uidRec.uid, uidRec);
                }
            }
        }
    }
}
